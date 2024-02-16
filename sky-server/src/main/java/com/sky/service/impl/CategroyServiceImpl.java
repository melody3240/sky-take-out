package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.BusinessException;
import com.sky.mapper.CategroyMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategroyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategroyServiceImpl implements CategroyService {
    @Autowired
    private CategroyMapper categroyMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        if(categoryPageQueryDTO.getPage() == 0){categoryPageQueryDTO.setPage(1);}
        if(categoryPageQueryDTO.getPageSize() == 0){categoryPageQueryDTO.setPageSize(10);}

        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        List<Category> list = categroyMapper.page(categoryPageQueryDTO);
        Page page = (Page) list;
        return new PageResult(page.getTotal(),list);
    }

    @Override
    public void insert(Category category) {
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(category.getCreateTime());
//        category.setUpdateUser(ThreadLocalUtil.getCurrentId());
//        category.setCreateUser(ThreadLocalUtil.getCurrentId());
        category.setStatus(StatusConstant.ENABLE);
        categroyMapper.insert(category);
    }

    @Override
    public void update(Category category) {
//        category.setUpdateUser(ThreadLocalUtil.getCurrentId());
//        category.setUpdateTime(LocalDateTime.now());
        categroyMapper.update(category);
    }

    @Override
    public Category findByName(String name) {
        return categroyMapper.findByName(name);
    }

    @Override
    public void delete(Long id) {
        //1.判断该类下有无菜品
        List<Dish> dishes = dishMapper.findByCategory(id, null);
        if(dishes != null && dishes.size() > 0){
            throw new BusinessException("该分类下有菜品无法删除");
        }
        //2.判断该类下有无套餐
        List<Setmeal> setmeals = setmealMapper.findByCategory(id);
        if(setmeals != null && setmeals.size() > 0){
            throw new BusinessException("该分类下有套餐无法删除");
        }
        categroyMapper.delete(id);
    }

    @Override
    public void checkStatus(Integer status,Long id) {
        Category category = Category.builder().id(id).status(status).updateTime(LocalDateTime.now()).updateUser(ThreadLocalUtil.getCurrentId()).build();
        categroyMapper.checkStatus(category);
    }

    @Override
    public List<Category> findList(Integer type) {
        return categroyMapper.findList(type);
    }

    @Override
    public List<Category> getCategoryList() {
        return categroyMapper.findAllByStatus(StatusConstant.ENABLE);
    }
}
