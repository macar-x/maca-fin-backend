package com.macacloud.fin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * User Domain Object
 *
 * @author Emmett
 * @since 2025/01/09
 */
@Getter
@Setter
@Entity
@Cacheable
@Table(name = "user_info")
public class UserInfo extends BasicDomain {

    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "role", length = 32)
    private String role;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "mobilePhone", length = 32)
    private String mobilePhone;
}
