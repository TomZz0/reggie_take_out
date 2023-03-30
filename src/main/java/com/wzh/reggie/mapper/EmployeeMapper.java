package com.wzh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzh.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * EmployeeMapper 用来处理涉及employee表的操作
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
