package com.wzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.dto.SetmealDto;
import com.wzh.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);
}
