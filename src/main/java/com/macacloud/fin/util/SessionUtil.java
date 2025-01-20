package com.macacloud.fin.util;

import com.macacloud.fin.exception.LoginRequiredException;
import io.quarkus.runtime.util.StringUtil;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;

/**
 * System Session Utility.
 *
 * @author Emmett
 * @since 2025/01/17
 */
@RequestScoped
public class SessionUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = -4537689537273972999L;

    @Inject
    CurrentIdentityAssociation identityAssociation;


    public Boolean isLogin() {
        return !this.isNotLogin();
    }

    public Boolean isNotLogin() {

        SecurityIdentity identity = this.getIdentity();
        if (identity == null) {
            return Boolean.TRUE;
        }
        return Boolean.TRUE.equals(identity.isAnonymous());
    }

    public Long requireLoginUserId() {

        Long userId = this.getLoginUserId();
        if (userId == null) {
            throw new LoginRequiredException();
        }
        return userId;
    }

    public Long getLoginUserId() {

        Principal principal = this.getLoginUser();
        if (principal == null) {
            return null;
        }
        String userIdInString = principal.getName();
        if (StringUtil.isNullOrEmpty(userIdInString)) {
            return null;
        }
        return Long.parseLong(userIdInString);
    }

    private Principal getLoginUser() {

        SecurityIdentity securityIdentity = this.getIdentity();
        if (securityIdentity == null) {
            return null;
        }
        return securityIdentity.getPrincipal();
    }

    private SecurityIdentity getIdentity() {

        if (identityAssociation == null) {
            return null;
        }
        return identityAssociation.getIdentity();
    }
}
