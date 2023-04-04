package com.wzh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzh.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author wzh
 * @date 2023年04月04日 21:58
 * Description:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
