package com.macacloud.fin.service;

import com.macacloud.fin.model.auth.UserRegistrationRequest;
import com.macacloud.fin.model.domain.UserInfoDomain;
import io.smallrye.mutiny.Uni;

public interface UserService {

    Uni<UserInfoDomain> getById(Long userId);

    Uni<UserInfoDomain> getByUsername(String username);

    Uni<UserInfoDomain> create(UserRegistrationRequest userRegistrationRequest);
}
