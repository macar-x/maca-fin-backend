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
    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "created_by")
    private Long createdBy;

    @JsonIgnore
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "updated_by")
    private Long updatedBy;

    @JsonIgnore
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    @Column(name = "deleted_by")
    private Long deletedBy;

    // PanacheEntityBase with deleted field already.
    // @Column(name = "is_deleted", nullable = false)
    // private Boolean is_deleted;
}
