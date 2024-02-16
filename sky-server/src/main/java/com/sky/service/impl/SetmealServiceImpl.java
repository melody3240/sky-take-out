package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BusinessException;
import com.sky.mapper.CategroyMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private CategroyMapper categroyMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public PageResult page(SetmealPageDTO setmealPageDTO) {
        PageHelper.startPage(setmealPageDTO.getPage(),setmealPageDTO.getPageSize());
        List<Setmeal> setmeals = setmealMapper.page(setmealPageDTO);
        Page page = (Page) setmeals;
        ArrayList<SetmealVO> list = new ArrayList<>();

        setmeals.forEach(item->{
            SetmealVO setmealVO = BeanUtil.copyProperties(item, SetmealVO.class);
            String name = categroyMapper.getCategoryName(item.getCategoryId());
            setmealVO.setCategoryName(name);
            list.add(setmealVO);
        });

        return new PageResult(page.getTotal(),list);
    }

    @Override
    public SetmealVO findById(Long id) {
        Setmeal setmeal = setmealMapper.findById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.findBysetmealId(id);
        SetmealVO setmealVO = BeanUtil.copyProperties(setmeal, SetmealVO.class);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Transactional
    @Override
    public void insert(SetmealDTO setmealDTO) {
        Setmeal findByNameSetmeal = setmealMapper.findByName(setmealDTO.getName());
        if(findByNameSetmeal != null){
            throw new BusinessException("菜品名称重复请重新输入");
        }
        Setmeal setmeal = BeanUtil.copyProperties(setmealDTO, Setmeal.class);
        setmealMapper.insert(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(item->{
            item.setSetmealId(setmealDTO.getId());
            setmealDishMapper.insert(item);
        });
    }

    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal findByNameSetmeal = setmealMapper.findByName(setmealDTO.getName());
        if(findByNameSetmeal != null && !findByNameSetmeal.getId().equals(setmealDTO.getId())){
            throw new BusinessException("菜品名称重复请重新输入");
        }

        Setmeal setmeal = BeanUtil.copyProperties(setmealDTO, Setmeal.class);
        setmealMapper.update(setmeal);

        // 先删再加
        setmealDishMapper.delete(setmealDTO.getId());

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(item->{
            item.setSetmealId(setmealDTO.getId());
            setmealDishMapper.insert(item);
        });
    }

    @Override
    public void checkStatus(Integer status, Long id) {
        if(status.equals(StatusConstant.ENABLE)){
            Integer count = setmealDishMapper.findDishCountBySetmealIdsDisable(id);
            if(count > 0) {
                throw new BusinessException("套餐无法启售，套餐中含有禁售商品");
            }
        }
        setmealMapper.checkStatus(status,id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if(ids == null || ids.size() <= 0){
            throw new BusinessException("请选择需要删除的套餐");
        }

        // 禁用可删除
        Integer count = setmealMapper.findCountShow(ids);
        if(count > 0){
            throw new BusinessException("当前要删除的套餐中有启用项");
        }

        ids.forEach(id->{
            setmealDishMapper.delete(id);
            setmealMapper.deleteById(id);
        });
    }

    @Override
    public List<Setmeal> setmealList(Long categoryId) {
        List<Setmeal> list = setmealMapper.findByCategory(categoryId);
        List<Setmeal> setmeals = new ArrayList<>();

        if(list != null && list.size() > 0){
            list.forEach(e->{
                if (e.getStatus() == 1) {
                    setmeals.add(e);
                }
            });
        }
        return setmeals;
    }

    @Override
    public List<DishItemVO> setmealDish(Long id) {
        return setmealDishMapper.setmealDish(id);
    }
}
