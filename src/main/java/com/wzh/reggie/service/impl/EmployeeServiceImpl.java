package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.entity.Employee;
import com.wzh.reggie.mapper.EmployeeMapper;
import com.wzh.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author wzh
 * @date 2023年03月30日 12:50
 * Description:
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
