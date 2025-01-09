package com.macacloud.fin.service;

import com.macacloud.fin.model.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Override
    public Uni<UserInfo> logicalDelete(Long id) {
        return UserInfo.<UserInfo>findById(id)
                .onItem().ifNotNull().transform(userInfo -> {
                    userInfo.setDeleted(Boolean.TRUE);
                    userInfo.setDeletedAt(LocalDateTime.now());
                    return userInfo;
                });
    }
}
