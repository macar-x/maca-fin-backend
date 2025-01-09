package com.macacloud.fin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;

@FilterDef(
        name = "deletedFilter",
        parameters = @ParamDef(name = "isDeleted", type = Boolean.class)
)
@Filter(
        name = "deletedFilter",
        condition = "deleted = :isDeleted"
)
@Getter
@Setter
@MappedSuperclass
public class BasicDomain extends PanacheEntity {

    @JsonIgnore
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
