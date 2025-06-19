package com.macacloud.fin.service;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.exception.GlobalRuntimeException;
import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.util.PasswordHashingUtil;
import com.macacloud.fin.util.SnowFlakeUtil;
import io.quarkus.runtime.util.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Override
    public UserInfoDomain getByUsername(String username) {

        UserInfoDomain userInfoDomain = UserInfoDomain.findByUsername(username);
        if (userInfoDomain == null) {
            throw new GlobalRuntimeException("user " + username + " not exist.");
        }
        return userInfoDomain;
    }

    @Override
    public UserInfoDomain create(UserRegistrationRequest userRegistrationRequest) {

        // Parameter validations.
        if (StringUtil.isNullOrEmpty(userRegistrationRequest.getMobilePhone())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("mobile_phone"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(userRegistrationRequest.getEmail())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("email"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        UserInfoDomain existUser = UserInfoDomain.findByUsername(userRegistrationRequest.getUsername());
        if (existUser != null) {
            throw new ArgumentNotValidException(Collections.singletonList("username"), "has been taken");
        }

        UserInfoDomain userInfo = new UserInfoDomain();
        userInfo.setId(SnowFlakeUtil.getNextId());
        userInfo.setUsername(userRegistrationRequest.getUsername());
        userInfo.setPassword(PasswordHashingUtil.hashPassword(userRegistrationRequest.getPassword()));
        userInfo.setRoles(UserRoleConstant.DEFAULT);
        userInfo.setMobilePhone(userRegistrationRequest.getMobilePhone());
        userInfo.setEmail(userRegistrationRequest.getEmail());
        userInfo.persist();
        return userInfo;
    }
}
