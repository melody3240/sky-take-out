package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> shoppingList(Long userId);

    ShoppingCart isExist(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void setNumber(ShoppingCart shoppingCart);

    void save(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId};")
    void deleteShoppingCart(Long userId);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteShoppingCartById(Long id);
}
