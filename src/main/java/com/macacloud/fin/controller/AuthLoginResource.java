package com.macacloud.fin.controller;

import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.UserLoginDTO;
import com.macacloud.fin.service.UserService;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

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
    UserService userService;
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
    @WithSession
    public Uni<CommonResponse<String>> doLogin(UserLoginDTO loginDTO) {
        return userService.doLogin(loginDTO).onItem().transform(token ->
                ResponseUtil.success("login succeed.", token));
    }
}
