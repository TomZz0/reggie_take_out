package com.wzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzh.reggie.common.R;
import com.wzh.reggie.dto.DishDto;
import com.wzh.reggie.entity.Category;
import com.wzh.reggie.entity.Dish;
import com.wzh.reggie.entity.DishFlavor;
import com.wzh.reggie.entity.Employee;
import com.wzh.reggie.service.CategoryService;
import com.wzh.reggie.service.DishFlavorService;
import com.wzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * @author wzh
 * @date 2023年04月01日 22:44
 * Description:菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @return 返回成功信息
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 修改菜品操作
     *
     * @param dishDto 封装Dish对象以及菜品口味list
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateByIdWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 进行修改菜品操作时需要回显数据 本方法就是用来接收前端页面发送的id 获取目标菜品数据
     * 以及口味信息回显给页面
     *
     * @param id 要修改菜品的id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable("id") Long id) {
        //获取dishDto对象
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 分页操作
     *
     * @param page     开始页面
     * @param pageSize 每页数目
     * @param name     模糊查询关键字
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {
        //获取页面 因为Dish中并没有菜系名 所以要使用dto
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //添加查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加like查询 判断name是否为空 非空则加入查询条件
        queryWrapper.like(StringUtils.hasText(name), Dish::getName, name);
        //按照更新时间查询
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询 将结果更新到pageInfo中
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝 因为要处理记录中的菜系名 所以无需拷贝records
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        //获取records
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new LinkedList<>();
        for (Dish dish : records) {
            //获取菜系的id 以便查询菜系名称
            Long categoryId = dish.getCategoryId();
            //通过菜系id查询对象
            Category byId = categoryService.getById(categoryId);
            //如果查询不到 就执行下一次循环
            if (byId == null) continue;
            //将获取到的菜系名赋给dto对象
            DishDto dishDto = new DishDto();
            dishDto.setCategoryName(byId.getName());
            //将其他Dish信息拷贝到dto对象
            BeanUtils.copyProperties(dish, dishDto);
            //添加到page中
            list.add(dishDto);
        }

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 菜品停售功能
     */
    @PostMapping("/status/{status}")
    public R<String> stopSelling(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        dishService.updateStatus(status, ids);
        return R.success("修改状态成功");
    }

    /**
     * 菜品删除功能
     * 删除对应口味
     * 删除对应菜品
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.deleteByIdWithFlavor(ids);
        return R.error("删除菜品成功");
    }

    /**
     * 根据菜系查询菜品 显示到添加套餐页面中
     *
     * @param dish 使用Dish接收 提高通用性
     * @return 返回查询到的数据 显示到页面
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        List<DishDto> list = dishService.getListWithFlavor(dish);

        return R.success(list);
    }
}
