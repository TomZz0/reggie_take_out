package com.wzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzh.reggie.common.R;
import com.wzh.reggie.entity.Employee;
import com.wzh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.image.ImageWatched;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;

/**
 * @author wzh
 * @date 2023年03月30日 12:52
 * Description:
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录方法
     *
     * @param request  获取request 登陆成功后在请求域中保存id
     * @param employee 数据通过JSON封装 要获取需要加上@RequestBody注解
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1 将页面提交的密码进行md5加密处理
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());

        //2 根据页面提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3 如果没有查询到或者密码不正确就返回登录失败结果
        if (emp == null || !emp.getPassword().equals(pwd)) {
            return R.error("用户名或密码错误,登陆失败");
        }

        //4 查看员工状态 如果为禁用状态 则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("用户已被封禁,请联系客服查询");
        }

        //5 登陆成功 将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
