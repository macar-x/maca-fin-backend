package com.macacloud.fin.model.domain;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * Transaction Category Domain Object
 *
 * @author Emmett
 * @since 2025/08/11
 */
@Getter
@Setter
@Entity
@Cacheable
@ToString(callSuper = true)
@Table(name = "transaction_category", schema = "backend")
public class TransactionCategoryDomain extends BasicDomain {

    @Serial
    private static final long serialVersionUID = -114420335521689114L;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", length = 64, unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "logo_path", length = 256, nullable = false)
    private String logoPath;


    public static TransactionCategoryDomain findByNameWithUser(String categoryName, Long userId) {
        return TransactionCategoryDomain.find("name = ?1 and (userId = ?2 or userId is null)", categoryName, userId).firstResult();
    }

    public static TransactionCategoryDomain findByIdWithUser(Long categoryId, Long userId) {
        return TransactionCategoryDomain.find("id = ?1 and (userId = ?2 or userId is null)", categoryId, userId).firstResult();
    }
}
