package com.macacloud.fin.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User Role Enum
 *
 * @author Emmett
 * @since 2025/01/09
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    ROOT(UserRoleConstant.ROOT, 1),
    ADMIN(UserRoleConstant.ADMIN, 10),
    USER(UserRoleConstant.USER, 100),
    GUEST(UserRoleConstant.GUEST, 1000),
    ;

    public final String code;
    // The smaller the number, the greater the authority.
    private final Integer level;
}
