package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.common.CustomException;
import com.wzh.reggie.dto.DishDto;
import com.wzh.reggie.entity.Dish;
import com.wzh.reggie.entity.DishFlavor;
import com.wzh.reggie.entity.SetmealDish;
import com.wzh.reggie.mapper.DishMapper;
import com.wzh.reggie.service.DishFlavorService;
import com.wzh.reggie.service.DishService;
import com.wzh.reggie.service.SetmealDishService;
import com.wzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private SetmealDishService setmealDishService;

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
    public void deleteByIdWithFlavor(List<Long> ids) {
        //先查询是否有套餐菜品关系含有某个菜品 若含有则无法删除菜品
        LambdaUpdateWrapper<SetmealDish> smdQueryWrapper = new LambdaUpdateWrapper<>();
        smdQueryWrapper.in(SetmealDish::getDishId, ids);
        int count = setmealDishService.count(smdQueryWrapper);
        if (count != 0) throw new CustomException("菜品在套餐中,无法删除");
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

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        //sql操作器
        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper<>();
        //创建操作条件
        queryWrapper.set(Dish::getStatus, status);
        queryWrapper.in(Dish::getId, ids);
        //执行修改
        this.update(queryWrapper);
    }

    @Override
    public List<DishDto> getListWithFlavor(Dish dish) {
        //设置查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //只查询处于销售中的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //查询
        List<Dish> list = this.list(queryWrapper);
        List<DishDto> dishDtos = new ArrayList<>();
        //设置DishDto 赋值口味
        for (Dish dish1 : list) {
            //查询口味并设置
            Long id = dish1.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            //根据菜品id查询口味
            queryWrapper1.eq(DishFlavor::getDishId, id);
            List<DishFlavor> list1 = dishFlavorService.list(queryWrapper1);
            //创建dto对象
            DishDto dishDto = new DishDto();
            //将dish中的数据拷贝到dishdto 因为dishdto继承了dish 可以拷贝
            BeanUtils.copyProperties(dish1, dishDto);
            //设置口味属性
            dishDto.setFlavors(list1);
            //添加到返回集合中
            dishDtos.add(dishDto);
        }
        return dishDtos;
    }

}
