package com.sky.web.app;

import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
public class AppShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    // 店铺是否营业
    @GetMapping("status")
    public Result shopStatus(){
        return Result.success(redisTemplate.opsForValue().get("shop_status"));
    }
}
