package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> findBysetmealId(Long id);

    void insert(SetmealDish item);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void delete(Long id);

    Integer findCountByDishIds(List<Long> ids);

    @Select("select COUNT(1) from setmeal_dish sd left join setmeal s on sd.setmeal_id = s.id" +
            " left join dish d on d.id = sd.dish_id where d.status = 0 and s.id = #{id}")
    Integer findDishCountBySetmealIdsDisable(Long id);

    @Select("select copies,description,image,d.name name from setmeal_dish sd left join dish d on sd.dish_id = d.id where d.id = #{id} and d.status = 1")
    List<DishItemVO> setmealDish(Long id);
}
