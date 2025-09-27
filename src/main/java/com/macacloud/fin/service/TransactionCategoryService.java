package com.macacloud.fin.service;

import com.macacloud.fin.model.category.CategoryCreationRequest;
import com.macacloud.fin.model.category.TransactionCategoryResponse;
import com.macacloud.fin.model.domain.TransactionCategoryDomain;

import java.util.List;

/**
 * Transaction Category Service Interface
 *
 * @author Emmett
 * @since 2025/08/10
 */
public interface TransactionCategoryService {

    TransactionCategoryResponse getByIdByUser(Long categoryId, Long userId);

    List<TransactionCategoryDomain> listRootCategoryByUser(Long userId);

    /**
     * create new transaction category for user.
     *
     * @param request new transaction category request;
     * @param userId  request initiator;
     * @return new transaction category info;
     */
    TransactionCategoryDomain create(CategoryCreationRequest request, Long userId);
}
