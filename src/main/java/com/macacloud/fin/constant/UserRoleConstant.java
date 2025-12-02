package com.macacloud.fin.constant;

/**
 * User Role Constant
 * Realm roles on OIDC.
 *
 * @author Emmett
 * @since 2025/01/09
 */
public abstract class UserRoleConstant {

    // Below were roles for realm only.
    public static final String ADMIN = "admin";
    public static final String USER = "user";

    // Below were roles for OIDC service.
    public static final String DEFAULT = "default-roles-maca-fin";
}
