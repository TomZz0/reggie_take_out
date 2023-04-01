package com.wzh.reggie.common;

/**
 * @author wzh
 * @date 2023年04月01日 16:01
 * Description:基于ThreadLocal封装工具类 用于保存和获取当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
