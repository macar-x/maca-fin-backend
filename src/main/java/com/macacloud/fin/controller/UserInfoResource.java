package com.macacloud.fin.controller;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.constant.UserRoleEnum;
import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.model.UserSaveDTO;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.service.UserService;
import com.macacloud.fin.util.PasswordHashingUtil;
import com.macacloud.fin.util.SessionUtil;
import com.macacloud.fin.util.SnowFlakeUtil;
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

import java.util.Collections;
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
    @Inject
    SessionUtil sessionUtil;

    @GET
    @Path("")
    @PermitAll
    public Uni<UserInfoDomain> get() {
        Long userId = sessionUtil.requireLoginUserId();
        return UserInfoDomain.findById(userId);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({UserRoleConstant.ROOT, UserRoleConstant.ADMIN})
    public Uni<UserInfoDomain> get(@PathParam("id") Long id) {
        return UserInfoDomain.findById(id);
    }

    @GET
    @Path("/list")
    @RolesAllowed({UserRoleConstant.ROOT, UserRoleConstant.ADMIN})
    public Uni<List<UserInfoDomain>> list() {
        return UserInfoDomain.listAll(Sort.by("id"));
    }

    @POST
    @Path("")
    @PermitAll
    public Uni<RestResponse<UserInfoDomain>> create(UserSaveDTO userSaveDTO) {

        // Parameter validations.
        if (StringUtil.isNullOrEmpty(userSaveDTO.getUsername())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("username"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(userSaveDTO.getPassword())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("password"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(userSaveDTO.getMobilePhone())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("mobile_phone"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(userSaveDTO.getEmail())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("email"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        UserInfoDomain userInfo = new UserInfoDomain();
        userInfo.setId(SnowFlakeUtil.getNextId());
        userInfo.setRoleId(UserRoleEnum.USER.getId());
        userInfo.setUsername(userSaveDTO.getUsername());
        userInfo.setPassword(PasswordHashingUtil.hashPassword(userSaveDTO.getPassword()));
        userInfo.setMobilePhone(userSaveDTO.getMobilePhone());
        userInfo.setEmail(userSaveDTO.getEmail());

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
