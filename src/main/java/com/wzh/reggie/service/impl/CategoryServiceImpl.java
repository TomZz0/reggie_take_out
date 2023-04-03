package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.common.CustomException;
import com.wzh.reggie.entity.Category;
import com.wzh.reggie.entity.Dish;
import com.wzh.reggie.entity.Setmeal;
import com.wzh.reggie.mapper.CategoryMapper;
import com.wzh.reggie.service.CategoryService;
import com.wzh.reggie.service.DishService;
import com.wzh.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author wzh
 * @date 2023年04月01日 16:27
 * Description:
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据id删除分类 删除前判断是否关联了套餐
     */
    @Override
    public void remove(Long id) {
        //添加查询菜品表的条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        //查询该菜系具有的菜品的数量
        int countOfDish = dishService.count(dishLambdaQueryWrapper);
        //根据count判断
        if (countOfDish > 0) {
            //如果菜品数目大于0 说明菜系关联了菜品 无法删除 要抛出异常
            throw new CustomException("菜系已关联菜品,无法删除");
        }
        //添加查询套餐表的条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        //查询属于该菜系的套餐的数目
        int countOfSetmeal = setmealService.count(setmealLambdaQueryWrapper);
        if (countOfSetmeal > 0) {
            //如果套餐数目大于0 说明菜系关联了菜品 无法删除 抛出异常
            throw new CustomException("菜系已关联套餐,无法删除");
        }
        //都未关联 正常删除
        super.removeById(id);
    }
}

