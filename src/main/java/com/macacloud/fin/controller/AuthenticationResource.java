package com.macacloud.fin.controller;

import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.auth.UserLoginRequest;
import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.util.ConfigurationUtil;
import com.macacloud.fin.util.KeyCloakUtil;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import io.quarkus.runtime.util.StringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    private final static String realm = ConfigurationUtil.getConfigValue("quarkus.oidc.realm");
    private final static String clientId = ConfigurationUtil.getConfigValue("quarkus.oidc.client-id");
    private final static String clientSecret = ConfigurationUtil.getConfigValue("quarkus.oidc.credentials.secret");

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
        if (!StringUtil.isNullOrEmpty(userLoginRequest.getRefreshToken())) {
            return this.refreshToken(userLoginRequest);
        } else {
            return this.getToken(userLoginRequest);
        }
    }

    @POST
    @Path("/register")
    @PermitAll
    public CommonResponse<UserInfoDomain> create(UserRegistrationRequest userRegistrationRequest) {

        // Parameter validations.
        if (userRegistrationRequest == null) {
            throw new ArgumentNotValidException();
        }

        try (Response response = this.register(userRegistrationRequest)) {

            // create user to OIDC provider, check status.
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                ResponseUtil.failed(response.getStatusInfo().toString());
            }
            return ResponseUtil.success("register succeed.");

            // Create user to local service.
            // Uni<UserInfoDomain> userInfoFuture = userService.create(userRegistrationRequest);
            // return userInfoFuture.onItem().transform(item -> ResponseUtil.success("register succeed", item));
        } catch (WebApplicationException webApplicationException) {
            Response response = webApplicationException.getResponse();
            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                return ResponseUtil.success(Response.Status.CONFLICT, "user existed.");
            }
            throw webApplicationException;
        }
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
    private Response register(UserRegistrationRequest userRegistrationRequest) {

        if (StringUtil.isNullOrEmpty(userRegistrationRequest.getUsername())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("username"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(userRegistrationRequest.getPassword())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("password"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        UserRepresentation userRepresentation = keyCloakUtil.composeUserRepresentation(userRegistrationRequest);
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
        userLoginRequest.setUsername("admin");
        userLoginRequest.setPassword("admin");

        adminToken = this.doLogin(userLoginRequest).getBody();
        adminLoginTimestamp = new Date().getTime();
    }
}
