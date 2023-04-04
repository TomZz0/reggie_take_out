package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.common.CustomException;
import com.wzh.reggie.dto.SetmealDto;
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

import java.util.List;

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

    @Override
    public SetmealDto getByIdWithDishes(Long id) {
        //获取套餐对象
        Setmeal byId = this.getById(id);
        //获取套餐菜品关系
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        queryWrapper.eq(SetmealDish::getSetmealId, byId.getId());
        //执行查询
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        //创建seatmealdto对象
        SetmealDto setmealDto = new SetmealDto();
        //拷贝数据
        BeanUtils.copyProperties(byId, setmealDto);
        //设置套餐菜品集合
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        //更新套餐信息
        this.updateById(setmealDto);
        //更新套餐菜品关系表 先删除原有的
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //设置条件
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(queryWrapper);
        //重新添加
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        //如果正在出售 无法删除 返回false
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //未停售的
        queryWrapper.eq(Setmeal::getStatus, 1);
        //id在ids内的
        queryWrapper.in(Setmeal::getId, ids);
        int count = this.count(queryWrapper);
        //如果有仍在售卖的套餐 则删除失败
        if (count != 0) throw new CustomException("套餐正在售卖中,无法删除");
        //删除套餐
        this.removeByIds(ids);
        //删除套餐菜品关系
        LambdaQueryWrapper<SetmealDish> dishQueryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件 套餐id在ids之中的 都要删除
        dishQueryWrapper.in(SetmealDish::getSetmealId, ids);
        //执行删除
        setmealDishService.remove(dishQueryWrapper);
    }

    /**
     * 更新状态 可执行单个或批量
     *
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        //sql操作器
        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        //创建操作条件
        queryWrapper.set(Setmeal::getStatus, status);
        queryWrapper.in(Setmeal::getId, ids);
        //执行修改
        this.update(queryWrapper);
    }
}
