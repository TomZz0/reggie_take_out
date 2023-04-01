package com.wzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzh.reggie.common.R;
import com.wzh.reggie.entity.Category;
import com.wzh.reggie.entity.Employee;
import com.wzh.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author wzh
 * @date 2023年04月01日 16:30
 * Description:分类的管理
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired //自动装配所需要的service层的属性
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 菜品分类分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize) {
        //创建分页构造器
        Page<Category> pageInfo = new Page(page, pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<Category>();
        //添加排序条件
        queryWrapper.orderByDesc(Category::getSort);
        //执行查询操作
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping("/{ids}")
    public R<String> delete(@PathVariable("ids") Long id) {
        categoryService.removeById(id);
        return R.success("删除成功");
    }
}
