package com.macacloud.fin.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User Role Enum
 * fixme(emmett): replace with UserRoleDomain.
 *
 * @author Emmett
 * @since 2025/01/09
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    ROOT(1L, UserRoleConstant.ROOT, 1),
    ADMIN(2L, UserRoleConstant.ADMIN, 10),
    USER(3L, UserRoleConstant.USER, 100),
    GUEST(4L, UserRoleConstant.GUEST, 1000),
    ;

    public final Long id;
    public final String code;
    // The smaller the number, the greater the authority.
    private final Integer level;
}
