package com.sky.web.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("page")
    public Result page(SetmealPageDTO setmealPageDTO){
        PageResult pageResult = setmealService.page(setmealPageDTO);
        return Result.success(pageResult);
    }

    @GetMapping("{id}")
    public Result findById(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.findById(id);
        return Result.success(setmealVO);
    }

    @PostMapping()
//    @CacheEvict(value = "SETMEAL",key = "setmealDTO.categoryId")
    @CacheEvict(value = "SETMEAL",allEntries = true)
    public Result insert(@RequestBody SetmealDTO setmealDTO){
        setmealService.insert(setmealDTO);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result checkStatus(@PathVariable Integer status,Long id){
        setmealService.checkStatus(status,id);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Long> ids){
        setmealService.deleteByIds(ids);
        return Result.success();
    }
}
