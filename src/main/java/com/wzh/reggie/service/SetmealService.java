package com.wzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.dto.SetmealDto;
import com.wzh.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 修改套餐需要进行原数据的回显 这个方法用于返回信息
     * @param id
     * @return
     */
    SetmealDto getByIdWithDishes(Long id);

    /**
     * 套餐修改操作
     * @param setmealDto
     */
    void updateWithDishes(SetmealDto setmealDto);

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    void deleteById(List<Long> ids);

    void updateStatus(Integer status, List<Long> ids);
}
