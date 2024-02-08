package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;




    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShopping(ShoppingCartDTO shoppingCartDTO) {
        //首先根据user_id和商品id判断加入购物车的商品是否已经存在于，如果存在，那么number+1
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

         List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
         if(list != null && list.size() >0){
             ShoppingCart shoppingCart1 = list.get(0);
             shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
             shoppingCartMapper.updateNumberById(shoppingCart1);
             return;
         }

        //如果不存在，那么向数据库里插入一条数据，注意在这之前需要先查询dish或setmeal表补充其他信息
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        //如果此次添加的是菜品
        if(dishId != null){
            Dish dish = dishMapper.getById(dishId);

            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setName(dish.getName());
        }else {
            //此次添加的是套餐
            Setmeal setmeal = setmealMapper.selectById(setmealId);

            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setName(setmeal.getName());
        }
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setNumber(1);
        //将shoppingCart对象插入数据库
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }

    /**
     * 删除购物车中的商品
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询该条购物车数据，判断number的值
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        ShoppingCart listData = list.get(0);
        //如果number > 1，那么我们修改其number--
        if(listData.getNumber() > 1) {
           Integer newNumber  = listData.getNumber()-1;
           listData.setNumber(newNumber);
            shoppingCartMapper.updateNumberById(listData);
            return;
        }
        //否则，删除该条数据记录
        shoppingCartMapper.subShoppingCart(shoppingCart);
    }


}
