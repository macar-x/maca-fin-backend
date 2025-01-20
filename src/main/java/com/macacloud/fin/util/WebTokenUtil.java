package com.macacloud.fin.util;

import com.macacloud.fin.exception.DataNotFoundException;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.model.domain.UserRoleDomain;
import io.netty.util.internal.StringUtil;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WebTokenUtil {

    private final static String issuer = ConfigurationUtil.getConfigValue("mp.jwt.verify.issuer");
    private final static Long expirationTime = Long.parseLong(ConfigurationUtil.getConfigValue("com.macacloud.fin.session.exprition"));

    private final static String DEFAULT_AUTHORIZATION_METHOD = "Bearer ";

    public static Uni<String> generateToken(UserInfoDomain userInfo) {

        if (userInfo == null) {
            throw new NotFoundException("user");
        }
        return UserRoleDomain.findById(userInfo.getRoleId())
                .onItem().ifNull().failWith(new DataNotFoundException("role"))
                .onItem().transform(entity -> {
                    UserRoleDomain userRole = (UserRoleDomain) entity;
                    log.debug("{} {} login succeed.", userRole.getName(), userInfo.getUsername());
                    return WebTokenUtil.generateToken(userInfo.getId(), userInfo.getUsername(),
                            new HashSet<>(Collections.singletonList(userRole.getName())));
                });
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
                        .claim(Claims.birthdate.name(), new Date()).expiresIn(expirationTime).sign();
    }
}
