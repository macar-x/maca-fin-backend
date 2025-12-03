package com.macacloud.fin.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SoftDelete;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@SoftDelete
@MappedSuperclass
public class BasicDomain extends PanacheEntityBase implements Serializable {

    @Serial
    private static final long serialVersionUID = -485968750974305779L;

    @Id
    @JsonProperty("id")
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;

    @JsonIgnore
    @Column(name = "created_user_id", nullable = false)
    private Long createdUserId;

    @JsonIgnore
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_by", length = 64, nullable = false)
    private String updatedBy;

    @JsonIgnore
    @Column(name = "updated_user_id", nullable = false)
    private Long updatedUserId;

    @JsonIgnore
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "deleted_by", length = 64)
    private String deletedBy;

    @JsonIgnore
    @Column(name = "deleted_user_id")
    private Long deletedUserId;

    @JsonIgnore
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // PanacheEntityBase with deleted field already.
    // @Column(name = "is_deleted", nullable = false)
    // private Boolean is_deleted;
}
