package com.etar.purifier.modules.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 访问自定义静态文件配置方法
 *
 * @author hzh
 * @date 2018/10/17
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Value("${img.uploadPath}")
    private String uploadPath;

    private static final String PATTERN = "/static/**";
    private static final String IMG_RESOURCE = "/static/imgs/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        if (!registry.hasMappingForPattern(PATTERN)) {
            //uploadPath为文件保存路径，imgResource 为url访问路径资源，即将保存路径转换为访问路径，
            System.out.println("uploadPath路径：" + uploadPath);
            registry.addResourceHandler(IMG_RESOURCE).addResourceLocations("file:" + uploadPath);
        }
        super.addResourceHandlers(registry);
    }

}