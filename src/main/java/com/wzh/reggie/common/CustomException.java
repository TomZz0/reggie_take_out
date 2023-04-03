package com.wzh.reggie.common;

/**
 * @author wzh
 * @date 2023年04月01日 20:17
 * Description:
 */
public class CustomException extends RuntimeException {
    public CustomException(String msg) {
        super(msg);
    }
}
