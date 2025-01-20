package com.macacloud.fin.service;

import com.macacloud.fin.model.domain.UserInfoDomain;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.Principal;
import java.time.LocalDateTime;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Override
    public Uni<UserInfoDomain> logicalDelete(Long id) {
        return UserInfoDomain.<UserInfoDomain>findById(id)
                .onItem().ifNotNull().transform(userInfo -> {
                    userInfo.setDeleted(Boolean.TRUE);
                    userInfo.setDeletedAt(LocalDateTime.now());
                    return userInfo;
                });
    }
}
