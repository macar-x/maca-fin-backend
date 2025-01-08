package com.macacloud.fin.controller;

import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.util.ResponseUtil;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * User Login OpenAPI
 * todo(emmett): refer to <a href="security-oidc-bearer-token-authentication-tutorial">...</a>.
 *
 * @author Emmett
 * @since 2025/01/08
 */
@Slf4j
@Path("/api/open")
public class DemoResource {

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResponse<Boolean> isLoginStatus() {

        log.debug("this is a demo.");
        log.info("this is a demo.");
        log.warn("this is a demo.");
        log.error("this is a demo.");

        return ResponseUtil.success(Boolean.FALSE);
    }
}
