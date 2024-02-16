package com.sky.test;

import com.sky.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testValueOperations() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("hello","world");

        System.out.println(valueOperations.get("hello"));

        Category category = Category.builder().id(1L).status(1).name("aaaa").build();
        valueOperations.set("category",category);
        System.out.println(valueOperations.get("category"));

        // 设置有时长的key
        valueOperations.set("sms",111111,5, TimeUnit.SECONDS);
        valueOperations.set("sms1",222222, Duration.ofSeconds(5));

        // 没有再写
        Boolean result = valueOperations.setIfAbsent("a", 1111);
        System.out.println(result);
        result = valueOperations.setIfAbsent("a", 1111);
        System.out.println(result);
    }

    @Test
    public void testHashOperations() {
        HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("uid:1","name","tom");
        hashOperations.put("uid:1","age",20);

        System.out.println(hashOperations.get("uid:1", "name"));

        // 获取字段集合
        System.out.println(hashOperations.keys("uid:1"));
        // 获取字段值集合
        System.out.println(hashOperations.values("uid:1"));
        // 获取所有数据
        System.out.println(hashOperations.entries("uid:1"));
        // 删除指定字段
        hashOperations.delete("uid:1","name");

        System.out.println(hashOperations.entries("uid:1"));
    }

    @Test
    public void testCommon() {
        Boolean aBoolean = redisTemplate.hasKey("uid:1");
        System.out.println(aBoolean);

        redisTemplate.delete("hellow");
        Boolean hellow = redisTemplate.hasKey("hellow");
        System.out.println(hellow);

        Set keys = redisTemplate.keys("a*");
        System.out.println(keys);
    }
}
