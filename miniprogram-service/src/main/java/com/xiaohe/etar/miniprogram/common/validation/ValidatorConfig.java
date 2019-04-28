package com.xiaohe.etar.miniprogram.common.validation;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * 需要我们声明一个Bean，注入校验器到Spring Boot的运行环境，负责不能生效对应的@Range、@Min、@Max等validator包里面提供的注解
 *
 * @author jack
 */
@Configuration
@EnableAutoConfiguration
public class ValidatorConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}