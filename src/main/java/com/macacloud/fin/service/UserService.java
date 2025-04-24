package com.macacloud.fin.service;

import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;
import io.smallrye.mutiny.Uni;

public interface UserService {

    UserInfoDomain getByUsername(String username);

    UserInfoDomain create(UserRegistrationRequest userRegistrationRequest);
}
