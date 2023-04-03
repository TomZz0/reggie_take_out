package com.wzh.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wzh
 * @date 2023年03月29日 23:30
 * Description:启动类
 */
@Slf4j//输出日志
@SpringBootApplication
@ServletComponentScan //扫描过滤器注解
@EnableTransactionManagement//开启事务
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功 A successful starting");
    }
}
