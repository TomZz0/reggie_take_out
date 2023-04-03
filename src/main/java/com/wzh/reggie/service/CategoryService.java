package com.wzh.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 移除菜品
     * @param id
     */
    public void remove(Long id);
}
