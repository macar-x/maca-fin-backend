package com.macacloud.fin.service;

import com.macacloud.fin.constant.UserRoleEnum;
import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.exception.DataNotFoundException;
import com.macacloud.fin.model.UserLoginDTO;
import com.macacloud.fin.model.UserSaveDTO;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.util.PasswordHashingUtil;
import com.macacloud.fin.util.SnowFlakeUtil;
import com.macacloud.fin.util.WebTokenUtil;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.runtime.util.StringUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Override
    public Uni<UserInfoDomain> getById(Long userId) {
        return UserInfoDomain.findById(userId)
                .onItem().ifNull().failWith(DataNotFoundException::new)
                .onItem().transform(entity -> (UserInfoDomain) entity);
    }

    @Override
    public Uni<UserInfoDomain> create(UserSaveDTO userSaveDTO) {

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

        return UserInfoDomain.findByUsername(userSaveDTO.getUsername()).onItem().ifNotNull()
                // Username duplicate check.
                .failWith(new ArgumentNotValidException(Collections.singletonList("username"), "has been taken"))
                // Persist new user.
                .onItem().call(() -> {
                    UserInfoDomain userInfo = new UserInfoDomain();
                    userInfo.setId(SnowFlakeUtil.getNextId());
                    userInfo.setRoleId(UserRoleEnum.USER.getId());
                    userInfo.setUsername(userSaveDTO.getUsername());
                    userInfo.setPassword(PasswordHashingUtil.hashPassword(userSaveDTO.getPassword()));
                    userInfo.setMobilePhone(userSaveDTO.getMobilePhone());
                    userInfo.setEmail(userSaveDTO.getEmail());
                    return Panache.withTransaction(userInfo::persist).replaceWith(userInfo);
                });
    }

    @Override
    public Uni<String> doLogin(UserLoginDTO loginDTO) {

        // Parameter validations.
        if (loginDTO == null) {
            throw new ArgumentNotValidException();
        }
        if (StringUtil.isNullOrEmpty(loginDTO.getUsername())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("username"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(loginDTO.getPassword())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("password"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        return UserInfoDomain.findByUsername(loginDTO.getUsername())
                .onItem().ifNull().failWith(() -> new DataNotFoundException("user"))
                // Verify password
                .onItem().ifNotNull().call(userInfo -> {
                    if (!PasswordHashingUtil.verifyPassword(loginDTO.getPassword(), userInfo.getPassword())) {
                        // Don't report password error, but user not found, to avoid some user registration detection.
                        // return Uni.createFrom().failure(new GlobalRuntimeException(
                        //         "user " + loginDTO.getUsername() + " password incorrect."));
                        return Uni.createFrom().failure(new DataNotFoundException("user"));
                    }
                    return Uni.createFrom().item(userInfo);
                })
                .onItem().transformToUni(WebTokenUtil::generateToken)
                .onItem().transform(token -> token);
    }
}
