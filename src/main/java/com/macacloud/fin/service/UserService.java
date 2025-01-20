package com.macacloud.fin.service;

import com.macacloud.fin.model.domain.UserInfoDomain;
import io.smallrye.mutiny.Uni;

public interface UserService {

    Uni<UserInfoDomain> logicalDelete(Long id);
}
