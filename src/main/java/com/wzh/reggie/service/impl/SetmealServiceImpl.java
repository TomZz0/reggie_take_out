package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.common.R;
import com.wzh.reggie.dto.DishDto;
import com.wzh.reggie.dto.SetmealDto;
import com.wzh.reggie.entity.Category;
import com.wzh.reggie.entity.Dish;
import com.wzh.reggie.entity.Setmeal;
import com.wzh.reggie.entity.SetmealDish;
import com.wzh.reggie.mapper.SetmealMapper;
import com.wzh.reggie.service.SetmealDishService;
import com.wzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author wzh
 * @date 2023年04月01日 19:53
 * Description:
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;





    @Override
    @Transactional

    public void saveWithDish(SetmealDto setmealDto) {
        //首先保存套餐
        this.save(setmealDto);
        //其次保存套餐和菜品关系
        //获取套餐id和SetmealDish集合 底层保存完之后就会生成id 填充id属性
        Long id = setmealDto.getId();
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        //保存套餐和菜品关系 要设置setmealid和sort
        for (SetmealDish setmealDish : list) {
            setmealDish.setSetmealId(id);
        }
        setmealDishService.saveBatch(list);
    }
}
