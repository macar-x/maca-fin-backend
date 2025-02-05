package com.macacloud.fin.service;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.exception.DataNotFoundException;
import com.macacloud.fin.exception.GlobalRuntimeException;
import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.util.PasswordHashingUtil;
import com.macacloud.fin.util.SnowFlakeUtil;
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
    public Uni<UserInfoDomain> getByUsername(String username) {
        return UserInfoDomain.findByUsername(username)
                .onItem().ifNull().failWith(() -> new GlobalRuntimeException("user " + username + " not exist."));
    }

    @Override
    public Uni<UserInfoDomain> create(UserRegistrationRequest userRegistrationRequest) {

        // Parameter validations.
        if (StringUtil.isNullOrEmpty(userRegistrationRequest.getMobilePhone())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("mobile_phone"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(userRegistrationRequest.getEmail())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("email"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        return UserInfoDomain.findByUsername(userRegistrationRequest.getUsername()).onItem().ifNotNull()
                // Username duplicate check.
                .failWith(new ArgumentNotValidException(Collections.singletonList("username"), "has been taken"))
                // Persist new user.
                .onItem().call(() -> {
                    UserInfoDomain userInfo = new UserInfoDomain();
                    userInfo.setId(SnowFlakeUtil.getNextId());
                    userInfo.setUsername(userRegistrationRequest.getUsername());
                    userInfo.setPassword(PasswordHashingUtil.hashPassword(userRegistrationRequest.getPassword()));
                    userInfo.setRoles(UserRoleConstant.USER);
                    userInfo.setMobilePhone(userRegistrationRequest.getMobilePhone());
                    userInfo.setEmail(userRegistrationRequest.getEmail());
                    return Panache.withTransaction(userInfo::persist).replaceWith(userInfo);
                });
    }
}
