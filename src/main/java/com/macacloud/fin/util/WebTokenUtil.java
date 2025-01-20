package com.macacloud.fin.util;

import com.macacloud.fin.constant.UserRoleEnum;
import io.netty.util.internal.StringUtil;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * System Token Utility.
 *
 * @author Emmett
 * @since 2025/01/09
 */
public class WebTokenUtil {

    private final static String issuer = ConfigurationUtil.getConfigValue("mp.jwt.verify.issuer");

    private final static String DEFAULT_AUTHORIZATION_METHOD = "Bearer ";

    public static String generateToken(Long userId, String username) {

        if (username == null) {
            return StringUtil.EMPTY_STRING;
        }
        return WebTokenUtil.generateToken(userId, username, new HashSet<>(Collections.singletonList(UserRoleEnum.ROOT.getCode())));
    }

    public static String generateToken(Long userId, String username, Set<String> roles) {

        if (username == null) {
            return StringUtil.EMPTY_STRING;
        }
        if (roles == null || roles.isEmpty()) {
            return StringUtil.EMPTY_STRING;
        }

        return DEFAULT_AUTHORIZATION_METHOD +
                Jwt.issuer(issuer).upn(userId.toString()).groups(roles).preferredUserName(username)
                        .claim(Claims.birthdate.name(), new Date()).sign();
    }
}
