package com.macacloud.fin.model.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.macacloud.fin.model.domain.BasicDomain;
import com.macacloud.fin.model.domain.TransactionCategoryDomain;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.List;

/**
 * Transaction Category Response.
 *
 * @author Emmett
 * @since 2025/08/11
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class TransactionCategoryResponse extends TransactionCategoryDomain {

    @Serial
    private static final long serialVersionUID = -2408515537998575865L;

    @JsonProperty(value = "sub_category_list")
    private List<TransactionCategoryDomain> subCategoryList;
}
