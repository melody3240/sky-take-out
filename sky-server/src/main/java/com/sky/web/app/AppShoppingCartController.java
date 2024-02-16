package com.sky.web.app;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
public class AppShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("list")
    public Result shoppingList(){
        List<ShoppingCart> shoppingCartList = shoppingCartService.shoppingList();
        return Result.success(shoppingCartList);
    }

    @PostMapping("add")
    public Result shoppingAdd(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.shoppingAdd(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("clean")
    public Result deleteShoppingCart(){
        shoppingCartService.deleteShoppingCart();
        return Result.success();
    }

    @PostMapping("sub")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.shoppingSub(shoppingCartDTO);
        return Result.success();
    }
}
