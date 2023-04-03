package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.dto.DishDto;
import com.wzh.reggie.entity.Dish;
import com.wzh.reggie.entity.DishFlavor;
import com.wzh.reggie.mapper.DishMapper;
import com.wzh.reggie.service.DishFlavorService;
import com.wzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wzh
 * @date 2023年04月01日 19:53
 * Description:
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品 保存菜品和口味
     * 需要事务操作
     *
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //因为继承了dish 所以可直接保存到dish表中
        this.save(dishDto);
        //获取保存的菜品的id 给口味赋值 先由底层生成 在添加到数据库 所以可以获取到
        Long id = dishDto.getId();
        //设置口味属于的菜品的id
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(id);
        }
        //保存口味
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品
        Dish byId = this.getById(id);
        DishDto dishDto = new DishDto();
        //内容拷贝
        BeanUtils.copyProperties(byId, dishDto);
        //查询口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        queryWrapper.eq(DishFlavor::getDishId, byId.getId());
        //查询
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //将口味赋给dishdto对象
        dishDto.setFlavors(list);
        //返回
        return dishDto;
    }

    @Override
    @Transactional
    public void updateByIdWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);
        //获取口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(dishDto.getId());
        }

        //先删除原有口味 再添加
        //查询条件
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        //执行删除操作
        dishFlavorService.remove(queryWrapper);
        //将口味存到表中
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public void deleteByIdWithFlavor(Long ids) {
        //先设置查询器删除口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件 菜品名为ids就删除
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, ids);
        //执行口味的删除
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //创建菜品查询器删除菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        dishLambdaQueryWrapper.eq(Dish::getId, ids);
        //执行删除
        this.remove(dishLambdaQueryWrapper);
    }

}
