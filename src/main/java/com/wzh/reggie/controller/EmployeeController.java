package com.wzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzh.reggie.common.R;
import com.wzh.reggie.entity.Employee;
import com.wzh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.awt.image.ImageWatched;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * @author wzh
 * @date 2023年03月30日 12:52
 * Description:
 */
@Slf4j
@RequestMapping("/employee")
@RestController
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

    /**
     * 新增员工
     *
     * @param employee 通过json形式接收从页面传来的employee的信息
     * @return
     */
    @PostMapping //路径就是/employee所以不用再添加
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //设置统一密码 需要进行MD5加密
        String pwd = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(pwd);
        //设置创建时间和更新时间 抽取到方法中进行设置
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //设置创建人和更新人 为当前登录用户的id
        //employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        //保存到数据库
        employeeService.save(employee);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name) {
        //创建分页构造器
        Page<Employee> pageInfo = new Page(page, pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        //添加过滤条件
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询操作
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据员工id修改员工信息
     * 修改员工信和和禁用启用都使用这一方法
     *
     * @return
     */
    @PutMapping()
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //获取当前用户id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //更新修改用户
        //employee.setUpdateUser(empId);
        //更新修改时间
        // employee.setUpdateTime(LocalDateTime.now());
        //更新用户
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 通过id查询用户 服务器接收传入的id 查询数据 以json形式响应给页面
     * 只有value值没有key 不是id=111而是直接在路径中 employee/111
     * 所以要使用占位符加@PathVariable注解
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id) {
        Employee byId = employeeService.getById(id);
        if (byId != null)
            return R.success(byId);
        else return R.error("未查询到目标用户 查询失败");
    }
}
