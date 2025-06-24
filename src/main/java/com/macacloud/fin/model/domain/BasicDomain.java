package com.macacloud.fin.model.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;


@Getter
@Setter
@SoftDelete
@MappedSuperclass
public class BasicDomain extends PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    // PanacheEntityBase with deleted field already.
    // @Column(name = "is_deleted", nullable = false)
    // private Boolean is_deleted;
}
