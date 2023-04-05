package com.wzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wzh.reggie.common.R;
import com.wzh.reggie.entity.User;
import com.wzh.reggie.service.UserService;
import com.wzh.reggie.utils.SMSUtils;
import com.wzh.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;

/**
 * @author wzh
 * @date 2023年04月04日 22:00
 * Description:
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码 要先在aliyun上创建用户得到accessKeyId和secret 填入到SMSUtils中
     * 然后页面输入要接受验证码的手机号 点击获取验证码 就会来到这个方法 生成code
     * 接收到验证码后通过设置的签名和模板Code调用SMSUtils中的方法 就会发送验证码
     * 阿里的测试签名和模板Code
     * SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code);
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (phone == null) return R.error("手机号为空 发送失败");
        //生成4位随机验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("======验证码是{}", code);
        //发送短信 注意要设置签名和模板Code 前端响应请求返回手机号
        SMSUtils.sendMessage("外卖项目开发学习", "SMS_275345788", phone, code);
        //保存到session中用于后续判断
        session.setAttribute(phone, code);
        return R.success("手机验证码短信发送成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取输入的验证码
        String code = map.get("code").toString();
        //获取发送的验证码 存的时候用的是phone
        String code1 = session.getAttribute(phone).toString();
        if (code1 == null || !code1.equals(code)) return R.error("验证码错误");

        //查询是否存在此用户 不存在就注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //设置查询条件
        queryWrapper.eq(User::getPhone, phone);
        User one = userService.getOne(queryWrapper);
        //判断是否为空 为空说明是新用户 要注册
        if (one != null) {
            //保存用户id 因为拦截器会判断否已经登录并决定是否需要跳到登陆页面
            session.setAttribute("user", one.getId());
            return R.success(one);
        }
        //注册
        User user = new User();
        user.setPhone(phone);
        userService.save(user);
        //保存用户id 因为拦截器会判断否已经登录并决定是否需要跳到登陆页面
        session.setAttribute("user", user.getId());
        return R.success(user);
    }
}
