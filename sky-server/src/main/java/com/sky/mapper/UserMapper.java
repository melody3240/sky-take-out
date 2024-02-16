package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openid}")
    User findByOpenid(String openid);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into user (id,openid,create_time) values (null,#{openid},#{createTime})")
    void save(User user);

    // 根据用户id查询
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    @Select("select COUNT(1) from user where DATE(create_time) = #{date} ")
    Integer findNewUserCount(LocalDate date);
}
