package com.sky.web.app;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/dish")
public class AppDishController {

    @Autowired
    private DishService dishService;

    @GetMapping("list")
    public Result findDishByCategoryId(Long categoryId){
        List<DishVO> dishVOList = dishService.findDishByCategoryId(categoryId);
        return Result.success(dishVOList);
    }
}
