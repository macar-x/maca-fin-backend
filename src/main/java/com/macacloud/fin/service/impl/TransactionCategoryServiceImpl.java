package com.macacloud.fin.service.impl;

import com.macacloud.fin.exception.ArgumentNotValidException;
import com.macacloud.fin.exception.DataNotFoundException;
import com.macacloud.fin.model.category.CategoryCreationRequest;
import com.macacloud.fin.model.category.TransactionCategoryResponse;
import com.macacloud.fin.model.domain.TransactionCategoryDomain;
import com.macacloud.fin.service.TransactionCategoryService;
import com.macacloud.fin.util.SnowFlakeUtil;
import io.quarkus.runtime.util.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils2.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

/**
 * Transaction Category Service Implement
 *
 * @author Emmett
 * @since 2025/08/10
 */
@Slf4j
@ApplicationScoped
public class TransactionCategoryServiceImpl implements TransactionCategoryService {

    @Override
    public TransactionCategoryResponse getByIdByUser(Long categoryId, Long userId) {

        TransactionCategoryDomain targetCategoryDomain = TransactionCategoryDomain.findByIdWithUser(categoryId, userId);
        if (targetCategoryDomain == null || targetCategoryDomain.getId() == null) {
            throw new DataNotFoundException();
        }
        TransactionCategoryResponse response = new TransactionCategoryResponse();
        try {
            BeanUtils.copyProperties(response, targetCategoryDomain);
        } catch (InvocationTargetException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        response.setSubCategoryList(this.listSubCategoryByIdByUser(categoryId, userId));
        return response;
    }

    @Override
    public List<TransactionCategoryDomain> listRootCategoryByUser(Long userId) {
        return TransactionCategoryDomain.list("parentId is null and (userId is null or userId = ?1)", userId);
    }

    @Override
    @Transactional
    public TransactionCategoryDomain create(CategoryCreationRequest request, Long userId) {

        // Parameter validations.
        if (userId == null) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("user_id"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(request.getName())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("name"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }
        if (StringUtil.isNullOrEmpty(request.getLogoPath())) {
            throw new ArgumentNotValidException(
                    Collections.singletonList("logo_path"), ArgumentNotValidException.MESSAGE_NOT_EMPTY);
        }

        // Existed entity check.
        TransactionCategoryDomain existCategory = TransactionCategoryDomain.findByNameWithUser(request.getName(), userId);
        if (existCategory != null) {
            throw new ArgumentNotValidException(Collections.singletonList("name"), "has been taken");
        }

        // Parent entity check.
        if (request.getParentId() != null) {
            TransactionCategoryDomain parentCategory = TransactionCategoryDomain.findByIdWithUser(
                    request.getParentId(), userId);
            if (parentCategory == null) {
                throw new DataNotFoundException("parent_category");
            }
            if (parentCategory.getParentId() != null) {
                // note(emmett): Only two level category supported, parent-child is valid but child-child is forbidden.
                throw new ArgumentNotValidException(Collections.singletonList("parent_category"), "not root category");
            }
        }

        // Persist and return.
        TransactionCategoryDomain newTransactionCategory = new TransactionCategoryDomain();
        newTransactionCategory.setId(SnowFlakeUtil.getNextId());
        newTransactionCategory.setParentId(request.getParentId());
        newTransactionCategory.setUserId(userId);
        newTransactionCategory.setName(request.getName());
        newTransactionCategory.setDescription(request.getDescription());
        newTransactionCategory.setLogoPath(request.getLogoPath());
        newTransactionCategory.setCreatedBy(userId);
        newTransactionCategory.setUpdatedBy(userId);
        newTransactionCategory.persist();
        return TransactionCategoryDomain.findById(newTransactionCategory.getId());
    }

    private List<TransactionCategoryDomain> listSubCategoryByIdByUser(Long parentCategoryId, Long userId) {
        return TransactionCategoryDomain.list("parentId = ?1 and (userId is null or userId = ?2)",
                parentCategoryId, userId);
    }
}
