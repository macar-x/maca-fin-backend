package com.macacloud.fin.controller;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.service.UserService;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserInfoResource {

    @Inject
    UserService userService;
    @Inject
    SessionUtil sessionUtil;

    @GET
    @Path("")
    @RolesAllowed(UserRoleConstant.USER)
    public CommonResponse<UserInfoDomain> get() {
        return ResponseUtil.success(userService.getByUsername(sessionUtil.requireLoginUsername()));
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

    // fixme(emmett): Should also delete it on OIDC provider.
    // @DELETE
    // @Path("/{id}")
    // @RolesAllowed(UserRoleConstant.ADMIN)
    // public CommonResponse<Void> delete(@PathParam("id") Long id) {
    //     Boolean status = UserInfoDomain.deleteById(id);
    //     if (Boolean.TRUE.equals(status)) {
    //         return ResponseUtil.success();
    //     }
    //     return ResponseUtil.success(Response.Status.NOT_FOUND);
    // }
}
