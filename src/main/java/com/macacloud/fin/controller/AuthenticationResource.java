package com.macacloud.fin.controller;

import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.auth.UserLoginRequest;
import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.service.UserService;
import com.macacloud.fin.util.ConfigurationUtil;
import com.macacloud.fin.util.KeyCloakUtil;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import io.quarkus.runtime.util.StringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.Date;

/**
 * User Login API
 * Will not remove the old token when login unless it expired.
 *
 * @author Emmett
 * @since 2025/01/08
 */
@Slf4j
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AuthenticationResource {

    @Inject
    @RestClient
    KeyCloakUtil keyCloakUtil;

    @Inject
    SessionUtil sessionUtil;

    @Inject
    UserService userService;

    private final static String realm = ConfigurationUtil.getConfigValue("quarkus.oidc.realm");
    private final static String clientId = ConfigurationUtil.getConfigValue("quarkus.oidc.client-id");
    private final static String clientSecret = ConfigurationUtil.getConfigValue("quarkus.oidc.credentials.secret");
    private final static String adminUsername = ConfigurationUtil.getConfigValue("quarkus.oidc.realm.admin.username");
    private final static String adminPassword = ConfigurationUtil.getConfigValue("quarkus.oidc.realm.admin.password");

    private AccessTokenResponse adminToken;
    private Long adminLoginTimestamp;

    @PostConstruct
    public void initialize() {
        this.doAdminLogin();
    }

    @GET
    @Path("/login")
    @PermitAll
    public CommonResponse<Boolean> isLogin() {
        return ResponseUtil.success(sessionUtil.isLogin());
    }

    @POST
    @Path("/login")
    @PermitAll
    public CommonResponse<AccessTokenResponse> doLogin(UserLoginRequest userLoginRequest) {

        // Parameter validations.
        if (userLoginRequest == null) {
            throw new ArgumentNotValidException();
        }

        // if refresh token not empty, refresh instead of login.
        CommonResponse<AccessTokenResponse> loginAttemptResponse;
        if (!StringUtil.isNullOrEmpty(userLoginRequest.getRefreshToken())) {
            loginAttemptResponse = this.refreshToken(userLoginRequest);
            // todo(emmett): Consider to verify response before return, divide refresh/login into two methods.
            log.debug("user access-token refreshed by refresh-token {}", userLoginRequest.getRefreshToken());
            return loginAttemptResponse;
        }

        loginAttemptResponse = this.getToken(userLoginRequest);
        // Login succeed, user existed, check if there's a local account.
        if (loginAttemptResponse != null && Response.Status.OK.getStatusCode() == loginAttemptResponse.getCode()) {
            // todo(emmett): 后续 用户模块 - 完善用户信息 功能上线后可通过返回标记引导填写。
            String username = userLoginRequest.getUsername();
            UserInfoDomain localUserInfo = UserInfoDomain.findByUsername(username);
            if (localUserInfo == null) {
                log.warn("user {} login succeed, but no local profile found.", username);
            } else {
                log.debug("user {} login succeed", username);
            }
        }
        return loginAttemptResponse;
    }

    @POST
    @Path("/register")
    @PermitAll
    @Transactional
    public CommonResponse<UserInfoDomain> create(UserRegistrationRequest userRegistrationRequest) {

        // Parameter validations.
        if (userRegistrationRequest == null) {
            throw new ArgumentNotValidException();
        }

        // Check if user existed on OIDC service.
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(userRegistrationRequest.getUsername());
        userLoginRequest.setPassword(userRegistrationRequest.getPassword());
        CommonResponse<AccessTokenResponse> loginAttemptResponse = this.getToken(userLoginRequest);
        // Login succeed, user existed, check if there's a local account.
        if (loginAttemptResponse != null &&
                Response.Status.OK.getStatusCode() == loginAttemptResponse.getCode()) {
            UserInfoDomain userInfo = UserInfoDomain.findByUsername(userRegistrationRequest.getUsername());
            if (userInfo == null) {
                log.warn("user existed on OIDC, create local account only.");
                userInfo = userService.create(userRegistrationRequest);
            }
            return ResponseUtil.compose(Response.Status.OK, null, userInfo);
        }

        // user existed on OIDC service, create a new one.
        log.info("user not existed on OIDC, will create first, then insert a local account.");
        try (Response response = this.register(userRegistrationRequest.getEmail(),
                userRegistrationRequest.getUsername(), userRegistrationRequest.getPassword())) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new ArgumentNotValidException(response.getStatusInfo().toString());
            }
        } catch (WebApplicationException webApplicationException) {
            Response response = webApplicationException.getResponse();
            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new ArgumentNotValidException(Collections.singletonList("username"), "has been taken");
            }
            throw webApplicationException;
        }

        return ResponseUtil.compose(Response.Status.CREATED, null, userService.create(userRegistrationRequest));
    }


    // OIDC Login Handler
    private CommonResponse<AccessTokenResponse> getToken(UserLoginRequest userLoginRequest) {

        // Parameter validations.
        if (StringUtil.isNullOrEmpty(userLoginRequest.getUsername())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("username"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(userLoginRequest.getPassword())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("password"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        try {
            AccessTokenResponse tokenResponse = keyCloakUtil.login(realm, "password",
                    userLoginRequest.getUsername(), userLoginRequest.getPassword(), clientId, clientSecret);
            return ResponseUtil.success("login succeed.", tokenResponse);
        } catch (WebApplicationException webApplicationException) {
            Response response = webApplicationException.getResponse();
            if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                log.error("user {} try to login but failed.", userLoginRequest.getUsername());
                return ResponseUtil.success(Response.Status.UNAUTHORIZED, "username and password not match.");
            }
            throw webApplicationException;
        }
    }

    private CommonResponse<AccessTokenResponse> refreshToken(UserLoginRequest userLoginRequest) {

        // Parameter validations.
        if (StringUtil.isNullOrEmpty(userLoginRequest.getRefreshToken())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("refresh_token"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        try {
            AccessTokenResponse tokenResponse = keyCloakUtil.refreshToken(realm, "refresh_token",
                    userLoginRequest.getRefreshToken(), clientId, clientSecret);
            return ResponseUtil.success("refresh succeed.", tokenResponse);
        } catch (WebApplicationException webApplicationException) {
            Response response = webApplicationException.getResponse();
            if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                return ResponseUtil.success(Response.Status.UNAUTHORIZED, "refresh_token invalid.");
            }
            throw webApplicationException;
        }
    }

    // OIDC Register Handler
    private Response register(String email, String username, String password) {
        UserRepresentation userRepresentation = keyCloakUtil.composeUserRepresentation(email, username, password);
        return keyCloakUtil.createUser(this.getAdminToken(), realm, userRepresentation);
    }

    // OIDC Admin Handler
    private String getAdminToken() {

        if (adminToken == null || this.isAdminExpired()) {
            this.doAdminLogin();
        }

        return "Bearer " + adminToken.getToken();
    }

    private boolean isAdminExpired() {
        return System.currentTimeMillis() > (adminToken.getExpiresIn() * 1000L + adminLoginTimestamp);
    }

    private void doAdminLogin() {

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUsername(adminUsername);
        userLoginRequest.setPassword(adminPassword);

        adminToken = this.doLogin(userLoginRequest).getBody();
        adminLoginTimestamp = new Date().getTime();
    }
}

