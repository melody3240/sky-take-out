package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.BusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategroyMapper categroyMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult page(DishPageDTO dishPageDTO) {
        PageHelper.startPage(dishPageDTO.getPage(),dishPageDTO.getPageSize());
        List<Dish> list = dishMapper.page(dishPageDTO);
        Page page = (Page) list;
        List<DishVO> dishVOS = new ArrayList<>();
        list.forEach(item->{
            String name = categroyMapper.getCategoryName(item.getCategoryId());
            DishVO dishVO = BeanUtil.copyProperties(item, DishVO.class);
            dishVO.setCategoryName(name);
            dishVOS.add(dishVO);
        });
        return new PageResult(page.getTotal(),dishVOS);
    }

    @Transactional
    @Override
    public void insert(DishDTO dishDTO) {
        if (dishDTO.getCategoryId() == null) {
            throw new BusinessException("菜品分类Id不能为空");
        }

        if (StrUtil.isBlank(dishDTO.getImage())) {
            throw new BusinessException("图片不能为空");
        }
        if(StrUtil.isBlank(dishDTO.getName())){
            throw new BusinessException("名称不能为空");
        }

        if(dishDTO.getPrice() == null){
            throw new BusinessException("价格不能为空");
        }

        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        dish.setCreateTime(LocalDateTime.now());
        dish.setCreateUser(ThreadLocalUtil.getCurrentId());
        dish.setStatus(StatusConstant.ENABLE);
        dish.setUpdateTime(dish.getCreateTime());
        dish.setUpdateUser(ThreadLocalUtil.getCurrentId());
        dishMapper.insert(dish);
        List<DishFlavor> list = dishDTO.getFlavors();
        if(list != null && list.size() > 0){
            for (DishFlavor dishFlavor : list) {
                dishFlavor.setDishId(dish.getId());
                dishFlavorMapper.insertFlavor(dishFlavor);
            }
        }

        redisTemplate.delete(redisTemplate.keys("dishList:*"));
    }

    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        if (dishDTO.getCategoryId() == null) {
            throw new BusinessException("菜品分类Id不能为空");
        }

        if (StrUtil.isBlank(dishDTO.getImage())) {
            throw new BusinessException("图片不能为空");
        }
        if(StrUtil.isBlank(dishDTO.getName())){
            throw new BusinessException("名称不能为空");
        }

        if(dishDTO.getPrice() == null){
            throw new BusinessException("价格不能为空");
        }

        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(ThreadLocalUtil.getCurrentId());
        dishMapper.update(dish);

        // 口味列表先删再加
        dishFlavorMapper.delete(dish.getId());

        List<DishFlavor> list = dishDTO.getFlavors();
        if(list != null && list.size() > 0){
            for (DishFlavor dishFlavor : list) {
                dishFlavor.setDishId(dish.getId());
                dishFlavorMapper.insertFlavor(dishFlavor);
            }
        }
        redisTemplate.delete(redisTemplate.keys("dishList:*"));
    }

    @Override
    public DishVO findById(Long id) {
        Dish dish = dishMapper.findById(id);
        List<DishFlavor> list = dishFlavorMapper.findDishFlavors(id);

        DishVO dishVO = BeanUtil.copyProperties(dish,DishVO.class);
        if(list != null && list.size() > 0){
            dishVO.setFlavors(list);
        }
        return dishVO;
    }

    @Override
    public void checkStatus(Integer status, Long id) {
        dishMapper.checkStatus(status,id);
        if(status.equals(StatusConstant.DISABLE)){
            setmealMapper.checkStatusDishable(id);
        }
        redisTemplate.delete(redisTemplate.keys("dishList:*"));
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        // ids 非空判断
        if( ids == null || ids.size() == 0){
            throw new BusinessException("请选择要删除的id");
        }
        // 启用不可删除
        Integer byIds = dishMapper.findCountEnableByIds(ids);
        if(byIds > 0){
            throw new BusinessException("启用项不可删除");
        }

        // 与套餐关联不可删
        Integer byDishIds = setmealDishMapper.findCountByDishIds(ids);
        if(byDishIds > 0){
            throw new BusinessException("当前菜品与套餐关联不可删除");
        }

        dishMapper.delete(ids);
        ids.forEach(id->{
            dishFlavorMapper.delete(id);
        });
        redisTemplate.delete(redisTemplate.keys("dishList:*"));
    }

    @Override
    public List<Dish> findByCategory(Long id,String name) {
        return dishMapper.findByCategory(id,name);
    }

    @Override
    public List<DishVO> findDishByCategoryId(Long categoryId) {

        Boolean hasKey = redisTemplate.hasKey("dishList:" + categoryId);
        if(hasKey){
            return (List<DishVO>) redisTemplate.opsForValue().get("dishList:" + categoryId);
        }

        List<DishVO> dishVOS = dishMapper.findDishByCategoryId(categoryId);
        if(dishVOS != null && dishVOS.size() > 0){
            for (DishVO dishVO : dishVOS) {
                dishVO.setFlavors(dishFlavorMapper.findDishFlavors(dishVO.getId()));
            }
        }

        redisTemplate.opsForValue().set("dishList:" + categoryId,dishVOS);

        return dishVOS;
    }
}
