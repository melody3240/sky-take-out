package com.sky.web.admin;

import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("{status}")
    public Result setStatus(@PathVariable Integer status){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("shop_status",status);
        return Result.success();
    }

    @GetMapping("status")
    public Result getStatus(){
        return Result.success(redisTemplate.opsForValue().get("shop_status"));
    }

}
