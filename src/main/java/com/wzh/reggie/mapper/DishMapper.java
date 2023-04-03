package com.wzh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzh.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
