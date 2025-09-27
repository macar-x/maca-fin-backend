package com.macacloud.fin.service;

import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;

/**
 * User Service Interface
 *
 * @author Emmett
 * @since 2025/01/09
 */
public interface UserService {

    /**
     * create new user.
     *
     * @param userRegistrationRequest new user request;
     * @return new user info;
     */
    UserInfoDomain create(UserRegistrationRequest userRegistrationRequest);
}
