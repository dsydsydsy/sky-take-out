package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品id查询套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 添加套餐的菜品信息
     * @param setmealDishes
     */
    @AutoFill(value = OperationType.INSERT)
    void insertSeatmealDishes(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询套餐菜品关系
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);

    /**
     * 删除setmeal_dish表里的套餐下菜品信息
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据setmealId删除套餐的全部菜品信息
     * @param setmealId
     */
    void deleteBysetmealId(Long setmealId);
}
