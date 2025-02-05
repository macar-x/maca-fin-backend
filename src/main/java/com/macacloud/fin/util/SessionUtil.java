package com.macacloud.fin.util;

import com.macacloud.fin.exception.LoginRequiredException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;

/**
 * System Session Utility.
 *
 * @author Emmett
 * @since 2025/01/17
 */
@Slf4j
@RequestScoped
public class SessionUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = -4537689537273972999L;

    /**
     * Injection point for the Access Token issued by the OpenID Connect Provider
     * @Inject
     * JsonWebToken accessToken;
     */

    @Inject
    SecurityIdentity identity;


    public Boolean isLogin() {

        // log.warn("identity: {}", identity.getPrincipal().getName());
        // log.warn("accessToken: {}", accessToken.getRawToken());

        return !this.isNotLogin();
    }

    public Boolean isNotLogin() {
        if (identity == null) {
            return Boolean.TRUE;
        }
        return Boolean.TRUE.equals(identity.isAnonymous());
    }

    public String requireLoginUsername() {

        String username = this.getLoginUsername();
        if (username == null) {
            throw new LoginRequiredException();
        }
        return username;
    }

    public String getLoginUsername() {

        if (identity == null) {
            return null;
        }
        Principal principal = identity.getPrincipal();
        if (principal == null) {
            return null;
        }

        return principal.getName();
    }
}
