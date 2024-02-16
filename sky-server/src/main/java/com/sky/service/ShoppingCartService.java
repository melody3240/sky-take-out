package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    List<ShoppingCart> shoppingList();

    void shoppingAdd(ShoppingCartDTO shoppingCartDTO);

    void deleteShoppingCart();

    void shoppingSub(ShoppingCartDTO shoppingCartDTO);
}
