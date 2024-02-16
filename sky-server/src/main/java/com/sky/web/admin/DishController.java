package com.sky.web.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("page")
    public Result page(DishPageDTO dishPageDTO){
        PageResult pageResult = dishService.page(dishPageDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result insert(@RequestBody DishDTO dishDTO){
        dishService.insert(dishDTO);
        return Result.success();
    }

    @GetMapping("{id}")
    public Result findById(@PathVariable Long id){
        DishVO dishVO = dishService.findById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        return Result.success();
    }

    @PostMapping("status/{status}")
    public Result checkStatus(@PathVariable Integer status,Long id){
        dishService.checkStatus(status,id);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        dishService.delete(ids);
        return Result.success();
    }

    @GetMapping("list")
    public Result findByCategoryId(Long categoryId,String name){
        List<Dish> dishes = dishService.findByCategory(categoryId,name);
        return Result.success(dishes);
    }
}
