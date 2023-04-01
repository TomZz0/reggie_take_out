package com.wzh.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author wzh
 * @date 2023年04月01日 15:37
 * Description:自定义元数据对象处理器 将插入和更新操作中的set属性的操作封装起来放到方法中
 * 减少代码量
 */
@Component //交给spring框架管理
@Slf4j //日志记录
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作 自动填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作 自动填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId() );
    }
}
