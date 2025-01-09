package com.macacloud.fin.controller;

import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.UserLoginDTO;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.WebTokenUtil;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * System Login API
 *
 * @author Emmett
 * @since 2025/01/08
 */
@Slf4j
@RequestScoped
@Path("/system")
public class SystemLoginResource {

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.birthdate)
    String birthdate;

    @GET
    @Path("/hello")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResponse<String> hello(@Context SecurityContext ctx) {
        return ResponseUtil.success(this.checkJWT(ctx));
    }

    @GET
    @Path("/login")
    @RolesAllowed({ "User", "Admin" })
    @Produces(MediaType.TEXT_PLAIN)
    public CommonResponse<String> isLogin(@Context SecurityContext ctx) {
        String response = this.checkJWT(ctx);
        return ResponseUtil.success(response + ", birthdate: " + birthdate);
    }

    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResponse<String> doLogin(UserLoginDTO loginDTO) {

        String user = StringUtil.EMPTY_STRING;
        if (loginDTO == null) {
            return ResponseUtil.success(user);
        }

        if ("admin".equals(loginDTO.getUsername()) && "admin".equals(loginDTO.getPassword())) {
            user = "admin";
        }
        if ("hello".equals(loginDTO.getUsername()) && "world".equals(loginDTO.getPassword())) {
            user = "user";
        }

        return ResponseUtil.success(WebTokenUtil.generateToken(user));
    }


    private String checkJWT(SecurityContext ctx) {

        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }
}
