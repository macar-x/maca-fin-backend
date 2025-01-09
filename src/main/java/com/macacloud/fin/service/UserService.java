package com.macacloud.fin.service;

import com.macacloud.fin.model.UserInfo;
import io.smallrye.mutiny.Uni;

public interface UserService {

    Uni<UserInfo> logicalDelete(Long id);
}
