package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    PageResult page(DishPageDTO dishPageDTO);

    void insert(DishDTO dishDTO);

    void update(DishDTO dishDTO);

    DishVO findById(Long id);

    void checkStatus(Integer status, Long id);

    void delete(List<Long> ids);

    List<Dish> findByCategory(Long id,String name);

    List<DishVO> findDishByCategoryId(Long categoryId);
}
