package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        //菜品数据发生修改，删除对应的dish_categoryId缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();


    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
@ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}",dishPageQueryDTO);
        PageResult pageResult =  dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
@ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        //由于批量删除的多个菜品可能会属于多个categoryId,索性将缓存全部删除
        cleanCache("dish_*");


        log.info("要删除的菜品：{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();

    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> selectById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
         DishVO dishVO = dishService.getByIdWithFlavors(id);
         return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        //由于修改菜品的时候，可能会修改菜品的所属categoryId,涉及两个分类id，索性将缓存全部删除
        cleanCache("dish_*");


        log.info("修改菜品：{}",dishDTO);
        dishService.updateWithFlavors(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售、停售
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的起售停售")
    public Result dishStartOrStop(@PathVariable Integer status,Long id){
        //将所有的菜品缓存数据清理掉，redis中以dish_开头的key
        cleanCache("dish_*");

        log.info("菜品起售停售：{}",status,id);
        dishService.dishStratOrStop(status,id);
        return Result.success();

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> selectByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品:{}",categoryId);
         List<Dish> dishes = dishService.seleteByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 清楚缓存数据
     * @param pattern
     */
    public void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }








}
