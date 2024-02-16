package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult page(SetmealPageDTO setmealPageDTO);

    SetmealVO findById(Long id);

    void insert(SetmealDTO setmealDTO);

    void checkStatus(Integer status, Long id);

    void update(SetmealDTO setmealDTO);

    void deleteByIds(List<Long> ids);

    List<Setmeal> setmealList(Long categoryId);

    List<DishItemVO> setmealDish(Long id);
}
