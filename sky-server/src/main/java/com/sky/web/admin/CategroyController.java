package com.sky.web.admin;

import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.util.StrUtil;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.BusinessException;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategroyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategroyController {

    @Autowired
    private CategroyService categroyService;

    @GetMapping("page")
    public Result page(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult pageResult = categroyService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result insert(@RequestBody Category category){
        if(StrUtil.isBlank(category.getName())){
            throw new BusinessException("请输入分类名");
        }

        Category findByNameCategory = categroyService.findByName(category.getName());

        if(findByNameCategory != null){
            throw new BusinessException("分类名称重复");
        }
        categroyService.insert(category);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody Category category){
        if(StrUtil.isBlank(category.getName())){
            throw new BusinessException("请输入分类名");
        }

        Category findByNameCategory = categroyService.findByName(category.getName());
        if(findByNameCategory != null && category.getId() != findByNameCategory.getId()){
            throw new BusinessException("分类名称重复");
        }

        categroyService.update(category);
        return Result.success();
    }

    @DeleteMapping()
    public Result delete(Long id){
        categroyService.delete(id);
        return Result.success();
    }

    @PostMapping("status/{status}")
    public Result checkStatus(@PathVariable Integer status,Long id){
        categroyService.checkStatus(status,id);
        return Result.success();
    }

    @GetMapping("list")
    public Result findList(Integer type){
        List<Category> list = categroyService.findList(type);
        return Result.success(list);
    }
}
