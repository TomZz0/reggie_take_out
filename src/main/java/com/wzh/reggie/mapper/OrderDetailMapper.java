package com.wzh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzh.reggie.entity.OrderDetail;
import com.wzh.reggie.service.OrderDetailService;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
