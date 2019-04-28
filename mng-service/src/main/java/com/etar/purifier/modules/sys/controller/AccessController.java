package com.etar.purifier.modules.sys.controller;


import com.etar.purifier.common.annotation.LogLogin;
import com.etar.purifier.modules.cache.CaptchaInfo;
import com.etar.purifier.modules.cache.CaptchaService;
import entity.common.entity.DataResult;
import entity.common.entity.Result;
import com.etar.purifier.modules.sys.service.SysUserService;
import com.etar.purifier.modules.sys.shiro.ShiroUtils;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * @Author wzq
 */
@RequestMapping(value = "/access")
@RestController
public class AccessController {


    @Autowired
    private Producer producer;

    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private SysUserService sysUserService;

    @RequestMapping(value = "/unauth", method = RequestMethod.GET)
    public Result unauth() {
        Result result = new Result();
        result.error(2000, "尚未登录");
        return result;
    }

    /**
     * 登出接口
     *
     * @return
     */
    @RequestMapping("/logout")
    public Result logout() {
        Result result = new Result();
        result.ok();

        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        return result;
    }

    /**
     * 生成验证码
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping("/captcha")
    public void captcha(@RequestParam(value = "key") String key, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        System.out.println("生成验证码成功！");
        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);

        captchaService.setCaptcha(new CaptchaInfo(key, text));

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }

    /**
     * 登录接口
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    @LogLogin
    public Result login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "key") String key,
                        @RequestParam(value = "captcha") String captcha, HttpServletRequest request) {

        String kaptcha = captchaService.getCaptcha(key);

        DataResult result = new DataResult();

        if (StringUtils.isBlank(kaptcha) || !kaptcha.equals(captcha)) {
            result.error(2004, "验证码错误");
            return result;
        }

        Map<String, String> map = new HashMap<String, String>();
        result.error(0, "success");

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        try {
            subject.login(usernamePasswordToken);

            //过期时间为5个小时
            subject.getSession().setTimeout(18000000);
            String sessionId = (String) subject.getSession().getId();
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            map.put("token", sessionId);
            map.put("userId", ShiroUtils.getUserId().toString());
            result.setDatas(map);
        } catch (AuthenticationException e) {

            result.error(2001, "用户名，密码不匹配");

        } catch (Exception e) {

            result.error(2002, "未知异常");
        }
        return result;
    }

    @RequiresPermissions("test")
    @RequestMapping("/dotest")
    public Result secondApi() {

        Result result = new Result();
        result.error(1, "test success");
        return result;
    }

    /**
     * 生成验证码2
     *
     * @throws IOException
     */
    @RequestMapping("/getCaptcha")
    public String getCaptcha(@RequestParam(value = "key") String key) throws IOException {

        //生成文字验证码
        String text = producer.createText();
        captchaService.setCaptcha(new CaptchaInfo(key, text));
        return text;
    }

}
