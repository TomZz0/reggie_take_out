package com.wzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzh.reggie.common.R;
import com.wzh.reggie.dto.SetmealDto;
import com.wzh.reggie.entity.Setmeal;
import com.wzh.reggie.entity.SetmealDish;
import com.wzh.reggie.service.SetmealDishService;
import com.wzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author wzh
 * @date 2023年04月03日 20:26
 * Description:套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        //获取页面 因为Dish中并没有菜系名 所以要使用dto
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //添加查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加like查询 判断name是否为空 非空则加入查询条件
        queryWrapper.like(StringUtils.hasText(name), Setmeal::getName, name);
        //按照更新时间查询
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询 将结果更新到pageInfo中
        setmealService.page(pageInfo, queryWrapper);
        //对象拷贝 因为要处理记录中的菜系名 所以无需拷贝records
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        //获取records
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = new LinkedList<>();
        for (Setmeal setmeal : records) {
            //获取套餐的id 以便查询套餐名称
            Long setmealId = setmeal.getId();
            //通过套餐id查询对象
            Setmeal byId = setmealService.getById(setmealId);
            //如果查询不到 就执行下一次循环
            if (byId == null) {
                log.info("id:{}的套餐对象为空,查询不到", setmealId);
                continue;
            }
            //将获取到的大类套餐名赋给dto对象
            SetmealDto setmealDto = new SetmealDto();
            setmealDto.setCategoryName(byId.getName());
            //将其他Setmeal信息拷贝到dto对象
            BeanUtils.copyProperties(setmeal, setmealDto);
            //添加到page中
            list.add(setmealDto);
        }

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }
}
