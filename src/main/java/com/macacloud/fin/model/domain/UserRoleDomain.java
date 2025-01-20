package com.macacloud.fin.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * User Role Domain Object
 *
 * @author Emmett
 * @since 2025/01/16
 */
@Getter
@Setter
@Entity
@Cacheable
@Table(name = "user_role", schema = "backend")
public class UserRoleDomain extends BasicDomain {

    @Column(name = "name", length = 128, unique = true, nullable = false)
    private String name;

    @JsonIgnore
    @Column(name = "level", nullable = false)
    private Integer level;
}
