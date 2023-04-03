package com.wzh.reggie.dto;

import com.wzh.reggie.entity.Dish;
import com.wzh.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 在Java开发中，DTO（Data Transfer Object）类是一种Java对象，用于在不同层之间传递数据。
 * DTO 类通常包含用于描述应用程序数据的属性，并且可能包括用于访问和操作这些属性的方法。
 * 因为传来的数据中不光有一个类的属性 由于表的外键 还可能有其他属性 这时用一个类无法接收
 * 全部的数据 就要使用dto类 继承原类的基础上加上额外的属性
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;

}
