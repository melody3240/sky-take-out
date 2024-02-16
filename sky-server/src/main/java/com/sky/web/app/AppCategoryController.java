package com.sky.web.app;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategroyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/category")
public class AppCategoryController {

    @Autowired
    private CategroyService categroyService;

    @GetMapping("list")
    public Result getCategoryList(){
        List<Category> categoryList = categroyService.getCategoryList();
        return Result.success(categoryList);
    }
}
