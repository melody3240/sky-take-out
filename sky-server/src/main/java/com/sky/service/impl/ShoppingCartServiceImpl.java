package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public List<ShoppingCart> shoppingList() {
        return shoppingCartMapper.shoppingList(ThreadLocalUtil.getCurrentId());
    }

    @Override
    public void shoppingAdd(ShoppingCartDTO shoppingCartDTO) {
        // 校验
        if(shoppingCartDTO.getDishId() == null && shoppingCartDTO.getSetmealId() == null && shoppingCartDTO.getDishFlavor() == null){
            throw new BusinessException("请求参数错误");
        }

        // 将数据封装成shoppingCart
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        // 设置userId
        shoppingCart.setUserId(ThreadLocalUtil.getCurrentId());

        // 判断是否有当前菜品 或 套餐
        ShoppingCart isExistShoppingCart = shoppingCartMapper.isExist(shoppingCart);

        // 如果包含 数量加一
        if(isExistShoppingCart != null){
            isExistShoppingCart.setNumber(isExistShoppingCart.getNumber() + 1);
            shoppingCartMapper.setNumber(isExistShoppingCart);
        }else{
            // 不包含 完善数据添加
            // 判断是菜品还是套餐
            if(shoppingCartDTO.getDishId() != null){
                Dish dish = dishMapper.findById(shoppingCart.getDishId());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());// 单价
            }

            if(shoppingCartDTO.getSetmealId() != null){
                Setmeal setmeal = setmealMapper.findById(shoppingCart.getSetmealId());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());// 单价
            }

            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);

            shoppingCartMapper.save(shoppingCart);
        }

    }

    @Override
    public void shoppingSub(ShoppingCartDTO shoppingCartDTO) {
        // 校验
        if(shoppingCartDTO.getDishId() == null && (shoppingCartDTO.getSetmealId() == null || shoppingCartDTO.getDishFlavor() == null || shoppingCartDTO.getDishFlavor().equals(""))){
            throw new BusinessException("请求参数错误");
        }

        // 将数据封装成shoppingCart
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        // 设置userId
        shoppingCart.setUserId(ThreadLocalUtil.getCurrentId());

        // 判断是否有当前菜品 或 套餐
        ShoppingCart isExistShoppingCart = shoppingCartMapper.isExist(shoppingCart);

        // 如果包含 数量加一
        if(isExistShoppingCart != null){
            if(isExistShoppingCart.getNumber() > 1){
                isExistShoppingCart.setNumber(isExistShoppingCart.getNumber() - 1);
                shoppingCartMapper.setNumber(isExistShoppingCart);
            }else {
                // 删除某一项
                shoppingCartMapper.deleteShoppingCartById(isExistShoppingCart.getId());
            }
        }else{
            throw new BusinessException("参数错误没有当前套餐或菜品");
        }
    }

    @Override
    public void deleteShoppingCart() {
        shoppingCartMapper.deleteShoppingCart(ThreadLocalUtil.getCurrentId());
    }


}
