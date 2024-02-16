package com.sky.web.app;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
public class AppSetmealController {
    @Autowired
    private SetmealService setmealService;

    // 根据套餐id查询启售套餐
    @Cacheable(value = "SETMEAL",key = "#categoryId",unless = "#result.data.size() == 0")
    @GetMapping("/list")
    public Result setmealList(Long categoryId){
        List<Setmeal> setmeals = setmealService.setmealList(categoryId);
        return Result.success(setmeals);
    }

    // 根据套餐id查询包含的菜品
    @GetMapping("/dish/{id}")
    public Result setmealDish(@PathVariable Long id){
        List<DishItemVO> dishItemVOS = setmealService.setmealDish(id);
        return Result.success(dishItemVOS);
    }
}
