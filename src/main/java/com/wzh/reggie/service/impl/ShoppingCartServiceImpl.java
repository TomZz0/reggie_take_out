package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.entity.Setmeal;
import com.wzh.reggie.entity.ShoppingCart;
import com.wzh.reggie.mapper.ShoppingCartMapper;
import com.wzh.reggie.service.DishFlavorService;
import com.wzh.reggie.service.SetmealService;
import com.wzh.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author wzh
 * @date 2023年04月05日 15:26
 * Description:
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 将选中的菜品或套餐加入到购物车
     * @param shoppingCart
     * @param session
     * @return
     */
    @Override
    @Transactional
    public ShoppingCart addDishOrSetmeal(ShoppingCart shoppingCart, HttpSession session) {
        String name = shoppingCart.getName();
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);
        //通过id和name确定购物车项
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        queryWrapper.eq(ShoppingCart::getName, name);
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        ShoppingCart one = this.getOne(queryWrapper);
        //如果不为空 数目加一 否则数目为1
        if (one != null) {
            //传来的shoppingcart没有id属性 要使用查询到的
            one.setNumber(one.getNumber() + 1);
            this.updateById(one);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            //更新one 统一返回one
            one = shoppingCart;
        }
        return one;
    }

    @Override
    @Transactional
    //传来的只有菜品id或套餐id
    public ShoppingCart subDishOrSetmeal(ShoppingCart shoppingCart) {
        Long id = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //通过id直接查询购物项
        if (id == null) {
            id = shoppingCart.getSetmealId();
            queryWrapper.eq(ShoppingCart::getSetmealId, id);
        } else {
            queryWrapper.eq(ShoppingCart::getDishId, id);
        }
        ShoppingCart one = this.getOne(queryWrapper);
        //如果不为空 数目加一 否则数目为1
        if (one.getNumber() != 1) {
            one.setNumber(one.getNumber() - 1);
            this.updateById(one);
        } else {
            this.removeById(one.getId());
            one.setNumber(0);
        }
        return one;
    }
}
