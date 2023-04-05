package com.wzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.entity.ShoppingCart;

import javax.servlet.http.HttpSession;

public interface ShoppingCartService extends IService<ShoppingCart> {
    /**
     * 将菜品或套餐保存到购物车
     *
     * @param shoppingCart
     * @param session
     */
    ShoppingCart addDishOrSetmeal(ShoppingCart shoppingCart, HttpSession session);

    /**
     * 减少或者删除购物车中的菜品或套餐
     * @param shoppingCart
     */
    ShoppingCart subDishOrSetmeal(ShoppingCart shoppingCart);
}
