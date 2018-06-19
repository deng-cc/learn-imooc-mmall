package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.List;

/**
 * User: Dulk
 * Date: 2018/6/13
 * Time: 15:27
 */
public interface ICategoryService {
    ServerResponse addCategory(Integer parentId, String categoryName);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
