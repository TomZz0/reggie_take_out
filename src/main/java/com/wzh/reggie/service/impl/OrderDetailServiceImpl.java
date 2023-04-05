package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.entity.OrderDetail;
import com.wzh.reggie.mapper.OrderDetailMapper;
import com.wzh.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author wzh
 * @date 2023年04月05日 21:01
 * Description:
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
