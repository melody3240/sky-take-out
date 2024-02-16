package com.sky.mapper;

import com.sky.anno.AutoFill;
import com.sky.dto.SetmealPageDTO;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("select * from setmeal where category_id = #{id}")
    List<Setmeal> findByCategory(Long id);

    List<Setmeal> page(SetmealPageDTO setmealPageDTO);

    @Select("select * from setmeal where id = #{id}")
    Setmeal findById(Long id);

    @AutoFill("insert")
    void insert(Setmeal setmeal);

    @Select("select * from setmeal where name = #{name}")
    Setmeal findByName(String name);

    @AutoFill("update")
    @Update("update setmeal set status = #{status} where id = #{id}")
    void checkStatus(Integer status, Long id);

    @AutoFill("update")
    void update(Setmeal setmeal);

    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);

    void checkStatusDishable(Long id);

    Integer findCountShow(List<Long> ids);
}
