package com.etar.purifier;

import com.etar.purifier.config.HttpServletRequestReplacedFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.servlet.MultipartConfigElement;

/**
 * @author hzh
 * @date
 */
@SpringBootApplication
@EnableAsync
@MapperScan(basePackages = {"com.etar.purifier.modules.*.dao"})
@Configuration
@EnableCaching
@EnableFeignClients
@EntityScan(value = {"entity.*"})
public class MngApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(MngApplication.class, args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            System.out.println("activeProfile = " + activeProfile);
        }
    }

    /**
     * 配置上传文件大小的配置
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize("2MB");
        /// 总上传数据大小
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }

    /**
     * 配置body数据在controller可以继续使用
     *
     * @return FilterRegistrationBean
     */
//    @Bean
    public FilterRegistrationBean httpServletRequestReplacedRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestReplacedFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("httpServletRequestReplacedFilter");
        registration.setOrder(1);
        return registration;
    }
}