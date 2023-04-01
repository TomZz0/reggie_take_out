package com.wzh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.entity.Category;
import com.wzh.reggie.mapper.CategoryMapper;
import com.wzh.reggie.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @author wzh
 * @date 2023年04月01日 16:27
 * Description:
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}

