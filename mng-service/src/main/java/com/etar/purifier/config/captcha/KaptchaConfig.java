package com.etar.purifier.config.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author jack
 */
@Configuration
public class KaptchaConfig {
    /**
     * 注意事项:最好设置字体，否则在linux环境下在会因为缺少字体乱码
     * 参数介绍：
     * kaptcha.border  图片边框，合法值
     * kaptcha.border.color 边框颜色
     * kaptcha.border.thickness 边框厚度  合法值  大于0 的整数
     * kaptcha.image.width 图片宽度
     * kaptcha.image.height 图片高度
     * kaptcha.textproducer.char.length 验证码长度
     * kaptcha.textproducer.font.names 字体
     * kaptcha.textproducer.font.size 字体大小
     * kaptcha.textproducer.font.color 字体颜色
     * kaptcha.textproducer.char.space 文字间隔
     *
     * @return
     */
    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.font.color", "red");
        properties.put("kaptcha.textproducer.char.space", "5");
        properties.put("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", "宋体");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}