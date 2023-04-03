package com.wzh.reggie.controller;

import com.wzh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.FileInfo;
import org.apache.tomcat.jni.Multicast;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author wzh
 * @date 2023年04月01日 21:26
 * Description:进行文件的上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    //从配置文件中读取路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传操作
     * 在yml配置文件中设置转存路径 即上文中的basePath
     *
     * @param file 通过前端控件选择本地文件上传
     * @return 返回上传信息
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //处理文件重名问题
        String hzName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String finalFilename = UUID.randomUUID().toString() + hzName;
        //如果目录不存在 要创建
        File file1 = new File(basePath);
        if (!file1.exists()) file1.mkdir();

        try {
            file.transferTo(new File(basePath + finalFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //返回文件绝对路径  以便后续文件下载找到文件路径 显示到浏览器中
        return R.success(finalFilename);
    }

    /**
     * 将上传的文件读取 并传给浏览器展示
     *
     * @param name 上传文件方法中 最后经过UUID处理和追加文件后缀得到的fileName
     * @param response 通过response获得输出流 向浏览器输出
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //设置类型
        response.setContentType("image/jpeg");
        //获取输入流 创建byte数组用于读写 创建len存储每次读取长度
        FileInputStream inputStream = new FileInputStream(basePath + name);
        byte[] b = new byte[1024];
        int len;
        //获取输出流 边读边写
        ServletOutputStream outputStream = response.getOutputStream();
        while ((len = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, len);
            //刷新
            outputStream.flush();
        }
        //关流
        inputStream.close();
        if (outputStream != null) outputStream.close();
    }
}
