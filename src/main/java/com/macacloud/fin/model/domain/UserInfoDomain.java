package com.macacloud.fin.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * User Info Domain Object
 * fixme(emmett): query could still fetched deleted line.
 *
 * @author Emmett
 * @since 2025/01/09
 */
@Getter
@Setter
@Entity
@Cacheable
@Table(name = "user_info", schema = "backend")
public class UserInfoDomain extends BasicDomain {

    @Column(name = "username", length = 64, unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 256, nullable = false)
    private String password;

    @Column(name = "roles", length = 128, nullable = false)
    private String roles;

    @Column(name = "mobile_phone", length = 32, nullable = false)
    private String mobilePhone;

    @Column(name = "email", length = 128, nullable = false)
    private String email;


    public static Uni<UserInfoDomain> findByUsername(String username) {
        return UserInfoDomain.find("username = ?1", username).firstResult();
    }
}
