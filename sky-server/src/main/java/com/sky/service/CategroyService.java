package com.sky.service;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategroyService {
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void insert(Category category);

    void update(Category category);

    Category findByName(String name);

    void delete(Long id);

    void checkStatus(Integer status,Long id);

    List<Category> findList(Integer type);

    List<Category> getCategoryList();
}
