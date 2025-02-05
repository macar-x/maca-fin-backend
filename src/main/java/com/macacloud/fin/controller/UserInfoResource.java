package com.macacloud.fin.controller;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.service.UserService;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
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
    public Uni<CommonResponse<UserInfoDomain>> get() {
        return userService.getByUsername(sessionUtil.requireLoginUsername()).onItem().transform(ResponseUtil::success);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(UserRoleConstant.ADMIN)
    public Uni<CommonResponse<UserInfoDomain>> get(@PathParam("id") Long userId) {
        return userService.getById(userId).onItem().transform(ResponseUtil::success);
    }

    @GET
    @Path("/list")
    @RolesAllowed(UserRoleConstant.ADMIN)
    public Uni<CommonResponse<List<UserInfoDomain>>> list() {
        return UserInfoDomain.listAll(Sort.by("id"))
                .onItem().transform(list -> {
                    List<UserInfoDomain> userInfoList = list.stream()
                            .map(entity -> (UserInfoDomain) entity)
                            .toList();
                    return ResponseUtil.success(userInfoList);
                });
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed(UserRoleConstant.ADMIN)
    public Uni<CommonResponse<Boolean>> delete(@PathParam("id") Long id) {
        // fixme(emmett): Should also delete it on OIDC too.
        return Panache.withTransaction(() -> UserInfoDomain.deleteById(id)
                .onItem().transform(ResponseUtil::success)
                .onItem().ifNull().continueWith(ResponseUtil.success(Response.Status.NOT_FOUND)));
    }
}
