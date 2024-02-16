package com.sky.mapper;

import com.sky.anno.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategroyMapper {

    List<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill("insert")
    @Insert("insert into category values (null,#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    @AutoFill("update")
    void update(Category category);

    @Select("select * from category where name = #{name}")
    Category findByName(String name);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    @Update("update category set status = #{status},update_time = #{updateTime},update_user = #{updateUser} where id = #{id}")
    void checkStatus(Category category);

    @Select("select * from category where type = #{type}")
    List<Category> findList(Integer type);

    @Select("select name from category where id = #{categoryId}")
    String getCategoryName(Long categoryId);

    @Select("select * from category where status = #{enable}")
    List<Category> findAllByStatus(Integer enable);
}
