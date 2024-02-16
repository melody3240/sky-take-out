package com.sky.mapper;

import com.sky.dto.DishPageDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    List<Dish> page(DishPageDTO dishPageDTO);

    void insert(Dish dish);

    @Update("update dish set category_id = #{categoryId},description = #{description},image = #{image},name = #{name},price = #{price},status = #{status} where id = #{id}")
    void update(Dish dish);

    @Select("select * from dish where id = #{id}")
    Dish findById(Long id);

    @Update("update dish set status = #{status} where id = #{id}")
    void checkStatus(Integer status, Long id);

    void delete(List<Long> ids);

    List<Dish> findByCategory(Long id, String name);

    Integer findCountEnableByIds(List<Long> ids);

    @Select("select * from dish where category_id = #{categoryId} and status = 1")
    List<DishVO> findDishByCategoryId(Long categoryId);
}
