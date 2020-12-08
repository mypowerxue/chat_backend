package com.example.demo.config;

import com.example.demo.ConfigClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMVcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有访问img/**的请求映射到文件上传的路径下 C:\Users\wanghao/upload/img（图片的保存路径）
        registry.addResourceHandler(ConfigClass.IMG_PATH + "**").addResourceLocations("file:" + ConfigClass.IMG_ABSOLUTE_PATH);
        super.addResourceHandlers(registry);
    }
}