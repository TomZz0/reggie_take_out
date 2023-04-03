package com.wzh.reggie.dto;

import com.wzh.reggie.entity.Setmeal;
import com.wzh.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;
    //套餐名
    private String categoryName;
}
