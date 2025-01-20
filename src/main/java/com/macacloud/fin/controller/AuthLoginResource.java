package com.macacloud.fin.controller;

import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.exception.GlobalRuntimeException;
import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.UserLoginDTO;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.util.PasswordHashingUtil;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import com.macacloud.fin.util.WebTokenUtil;
import io.quarkus.runtime.util.StringUtil;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/**
 * User Login API
 * Due to request-specific state, use @RequestScoped instead of @ApplicationScoped.
 *
 * @author Emmett
 * @since 2025/01/08
 */
@Slf4j
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AuthLoginResource {

    @Inject
    SessionUtil sessionUtil;

    @GET
    @Path("/login")
    @PermitAll
    public CommonResponse<Boolean> isLogin() {
        return ResponseUtil.success(sessionUtil.isLogin());
    }

    @POST
    @Path("/login")
    @PermitAll
    public Uni<CommonResponse<String>> doLogin(UserLoginDTO loginDTO) {

        // Parameter validations.
        if (loginDTO == null) {
            throw new ArgumentNotValidException();
        }
        if (StringUtil.isNullOrEmpty(loginDTO.getUsername())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("username"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(loginDTO.getPassword())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("password"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        return UserInfoDomain.findByUsername(loginDTO.getUsername())
                .onItem().ifNull().failWith(() -> new GlobalRuntimeException(
                        "USER_NOT_FOUND", "user " + loginDTO.getUsername() + " not exist."))
                .flatMap(userInfo -> {
                    // Verify password
                    if (!PasswordHashingUtil.verifyPassword(loginDTO.getPassword(), userInfo.getPassword())) {
                        return Uni.createFrom().failure(new GlobalRuntimeException(
                                "PASSWORD_INCORRECT", "user " + loginDTO.getUsername() + " password incorrect."));
                    }

                    // Generate token and create response
                    String token = WebTokenUtil.generateToken(userInfo.getId(), userInfo.getUsername());
                    return Uni.createFrom().item(ResponseUtil.success(token));
                });
    }
}
