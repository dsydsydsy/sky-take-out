package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
//import jdk.vm.ci.meta.Value;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品的数量
     * @param id
     * @return
     */
    @Select("select count(*) from dish where category_id = #{id} ")
    Integer countByCategoryId(Long id);

    /**
     * 插入菜品数据
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据ids批量删除菜品信息
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据菜品id动态修改菜品信息
     * @param dish
     */
    void update(Dish dish);

    /**
     * 菜品的停售起售
     * @param status
     * @param id
     */
    @Update("update dish set status = #{status} where id = #{id}")
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectByCategoryId(Long categoryId);

    /**
     * 动态查询查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);
}
