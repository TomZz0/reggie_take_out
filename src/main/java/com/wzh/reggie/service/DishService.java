package com.wzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.dto.DishDto;
import com.wzh.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {
    /**
     * 新增菜品 同时插入数据到dish和dish_flavor表中
     *
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 修改菜品时的信息回显
     * @param id 需要参数id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品 保存后对表进行更新
     * @param dishDto
     */
    void updateByIdWithFlavor(DishDto dishDto);

    /**
     * 菜品删除功能 同时删除菜品对应的口味
     * @param ids
     */
    void deleteByIdWithFlavor(List<Long> ids);

    void updateStatus(Integer status, List<Long> ids);
}
