package com.wzh.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author wzh
 * @date 2023年03月30日 21:22
 * Description:全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//要拦截的请求
@ResponseBody//要处理返回的JSON数据需要加的注解
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理的注解 后边跟要处理的异常类的class
     *
     * @return 返回处理结果
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] spit = ex.getMessage().split(" ");
            String msg = spit[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误 操作失败");
    }
}
