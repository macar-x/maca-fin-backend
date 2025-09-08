package com.macacloud.fin.controller;

import com.macacloud.fin.constant.UserRoleConstant;
import com.macacloud.fin.exception.DataNotFoundException;
import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.model.category.CategoryCreationRequest;
import com.macacloud.fin.model.category.TransactionCategoryResponse;
import com.macacloud.fin.model.domain.TransactionCategoryDomain;
import com.macacloud.fin.service.TransactionCategoryService;
import com.macacloud.fin.util.ResponseUtil;
import com.macacloud.fin.util.SessionUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Transaction Category API
 * <a href="https://quarkus.io/guides/hibernate-orm-panache">ORM Reference</a>
 *
 * @author Emmett
 * @since 2025/08/10
 */
@Slf4j
@Path("/category")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@ApplicationScoped
public class TransactionCategoryResource {

    @Inject
    SessionUtil sessionUtil;

    @Inject
    TransactionCategoryService transactionCategoryService;

    @GET
    @Path("/{id}")
    @RolesAllowed(UserRoleConstant.USER)
    public CommonResponse<TransactionCategoryResponse> get(@PathParam("id") Long categoryId) {
        Long userId = sessionUtil.getLoginUserIdOrThrow();
        return ResponseUtil.success(transactionCategoryService.getByIdByUser(categoryId, userId));
    }

    @GET
    @Path("")
    @RolesAllowed(UserRoleConstant.USER)
    public CommonResponse<List<TransactionCategoryDomain>> listRootCategory() {
        Long userId = sessionUtil.getLoginUserIdOrThrow();
        return ResponseUtil.success(transactionCategoryService.listRootCategoryByUser(userId));
    }

    @POST
    @Path("")
    @RolesAllowed(UserRoleConstant.USER)
    public CommonResponse<TransactionCategoryDomain> save(CategoryCreationRequest request) {
        Long userId = sessionUtil.getLoginUserIdOrThrow();
        return ResponseUtil.success(transactionCategoryService.create(request, userId));
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed(UserRoleConstant.USER)
    public CommonResponse<TransactionCategoryResponse> delete(@PathParam("id") Long categoryId) {

        Long userId = sessionUtil.getLoginUserIdOrThrow();

        // todo(emmett): move biz-logic into service layer, abort deletion when category has tx, or move tx to unknown category.
        TransactionCategoryResponse targetCategory = transactionCategoryService.getByIdByUser(categoryId, userId);
        if (targetCategory == null || targetCategory.getId() == 0) {
            // Could not delete a category that not existed or internal built-in (id 0);
            throw new DataNotFoundException();
        }
        TransactionCategoryDomain.deleteById(targetCategory.getId());

        if (CollectionUtils.isNotEmpty(targetCategory.getSubCategoryList())) {
            targetCategory.getSubCategoryList().forEach(subCategoryItem ->
                    TransactionCategoryDomain.deleteById(subCategoryItem.getId()));
        }
        return ResponseUtil.success(targetCategory);
    }
}
