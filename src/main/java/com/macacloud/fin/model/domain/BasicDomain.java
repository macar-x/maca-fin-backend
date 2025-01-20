package com.macacloud.fin.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
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
public class BasicDomain extends PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = Boolean.FALSE;
}
