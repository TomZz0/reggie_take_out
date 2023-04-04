package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.entity.User;
import com.wzh.reggie.mapper.UserMapper;
import com.wzh.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author wzh
 * @date 2023年04月04日 22:00
 * Description:
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
