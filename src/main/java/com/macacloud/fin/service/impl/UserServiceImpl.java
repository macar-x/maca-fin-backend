package com.macacloud.fin.service.impl;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;
import com.macacloud.fin.service.UserService;
import com.macacloud.fin.util.SnowFlakeUtil;
import io.quarkus.runtime.util.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;

/**
 * User Service Implement
 *
 * @author Emmett
 * @since 2025/01/09
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {

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

        // Existed entity check.
        UserInfoDomain existUser = UserInfoDomain.findByUsername(userRegistrationRequest.getUsername());
        if (existUser != null) {
            throw new ArgumentNotValidException(Collections.singletonList("username"), "has been taken");
        }

        // Persist and return.
        UserInfoDomain newUserInfo = new UserInfoDomain();
        newUserInfo.setId(SnowFlakeUtil.getNextId());
        newUserInfo.setUsername(userRegistrationRequest.getUsername());
        // newUserInfo.setPassword(PasswordHashingUtil.hashPassword(userRegistrationRequest.getPassword()));
        newUserInfo.setRoles(UserRoleConstant.DEFAULT);
        newUserInfo.setNickname(userRegistrationRequest.getUsername());
        newUserInfo.setMobilePhone(userRegistrationRequest.getMobilePhone());
        newUserInfo.setEmail(userRegistrationRequest.getEmail());
        newUserInfo.persist();
        return newUserInfo;
    }
}
