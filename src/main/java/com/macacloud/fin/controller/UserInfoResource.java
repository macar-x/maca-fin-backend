package com.macacloud.fin.controller;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.model.UserInfo;
import com.macacloud.fin.model.UserSaveDTO;
import com.macacloud.fin.service.UserService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.util.StringUtil;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

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
@ApplicationScoped
public class UserInfoResource {

    @Inject
    UserService userService;

    @GET
    @Path("")
    @RolesAllowed({UserRoleConstant.ROOT, UserRoleConstant.ADMIN})
    // Uni is an asynchronous type. It’s a bit like a future.
    public Uni<List<UserInfo>> list() {
        return UserInfo.listAll(Sort.by("id"));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({UserRoleConstant.ROOT, UserRoleConstant.ADMIN})
    public Uni<UserInfo> get(@PathParam("id") Long id) {
        return UserInfo.findById(id);
    }

    @POST
    @Path("")
    @PermitAll
    public Uni<RestResponse<UserInfo>> create(UserSaveDTO userSaveDTO) {

        if (StringUtil.isNullOrEmpty(userSaveDTO.getUsername())) {
            // todo(emmett): use exception and handler instead.
            return null;
        }
        if (StringUtil.isNullOrEmpty(userSaveDTO.getPassword())) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userSaveDTO.getUsername());
        userInfo.setPassword(userSaveDTO.getPassword());
        userInfo.setEmail(userSaveDTO.getEmail());
        userInfo.setMobilePhone(userSaveDTO.getMobilePhone());
        userInfo.setRole(UserRoleConstant.USER);
        userInfo.setDeleted(Boolean.FALSE);
        userInfo.setDeletedAt(null);
        return Panache.withTransaction(userInfo::persist)
                .replaceWith(RestResponse.status(Response.Status.CREATED, userInfo));
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({UserRoleConstant.ROOT, UserRoleConstant.ADMIN})
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Panache.withTransaction(() -> userService.logicalDelete(id)
                .onItem().transform(user -> Response.ok(user).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build()));
    }
}
