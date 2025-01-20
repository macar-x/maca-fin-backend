package com.macacloud.fin.service;

import com.macacloud.fin.model.UserLoginDTO;
import com.macacloud.fin.model.UserSaveDTO;
import com.macacloud.fin.model.domain.UserInfoDomain;
import io.smallrye.mutiny.Uni;

public interface UserService {

    Uni<UserInfoDomain> getById(Long userId);

    Uni<UserInfoDomain> create(UserSaveDTO userSaveDTO);

    Uni<String> doLogin(UserLoginDTO loginDTO);
}
