package com.macacloud.fin.controller;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.exception.GlobalRuntimeException;
import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * User Info API
 *
 * @author Emmett
 * @since 2025/01/08
 */
@Slf4j
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@ApplicationScoped
public class UserInfoResource {

    @Inject
    SessionUtil sessionUtil;

    @GET
    @Path("")
    @RolesAllowed(UserRoleConstant.USER)
    public CommonResponse<UserInfoDomain> get() {

        String username = sessionUtil.requireLoginUsername();
        UserInfoDomain userInfoDomain = UserInfoDomain.findByUsername(username);
        if (userInfoDomain == null) {
            throw new GlobalRuntimeException("user " + username + " not exist.");
        }

        return ResponseUtil.success(userInfoDomain);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(UserRoleConstant.ADMIN)
    public CommonResponse<UserInfoDomain> get(@PathParam("id") Long userId) {
        return ResponseUtil.success(UserInfoDomain.findById(userId));
    }

    @GET
    @Path("/list")
    @RolesAllowed(UserRoleConstant.ADMIN)
    public CommonResponse<List<UserInfoDomain>> list() {
        List<UserInfoDomain> list = UserInfoDomain.listAll(Sort.by("id")).stream()
                .map(entity -> (UserInfoDomain) entity).toList();
        return ResponseUtil.success(list);
    }

    // Will not provided user delete operation, should keep OIDC user for other application if existed.
}
