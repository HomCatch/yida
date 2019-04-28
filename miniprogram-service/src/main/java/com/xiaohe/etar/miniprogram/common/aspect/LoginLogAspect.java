package com.xiaohe.etar.miniprogram.common.aspect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaohe.etar.miniprogram.common.async.AsyncTask;
import com.xiaohe.etar.miniprogram.modules.loginlog.service.LoginLogService;
import com.xiaohe.etar.miniprogram.utils.IPUtils;
import entity.common.entity.Result;
import entity.loginlog.LoginLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @program: xplatform
 * @description: 登陆切面类
 * @author: Gmq
 * @date: 2018-12-24 16:48
 **/
@Component
@Aspect
public class LoginLogAspect {

    private final LoginLogService loginLogService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;
    private final AsyncTask asyncTask;

    private Gson gson = new GsonBuilder().create();

    /*使用org.slf4j.Logger,这是spring实现日志的方法*/
    private final static Logger logger = LoggerFactory.getLogger(LoginLogAspect.class);

    @Autowired
    public LoginLogAspect(LoginLogService loginLogService, KafkaTemplate<String, String> kafkaTemplate, RestTemplate restTemplate, AsyncTask asyncTask) {
        this.loginLogService = loginLogService;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
        this.asyncTask = asyncTask;
    }


    @Around(value = "@annotation(com.xiaohe.etar.miniprogram.common.LogLogin)")
    public Result around(ProceedingJoinPoint joinPoint) throws Throwable {
        //调用执行目标方法(result为目标方法执行结果)
        Result result = (Result) joinPoint.proceed();
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取到请求头对象
        HttpServletRequest request = attributes.getRequest();
        LoginLog loginLog = new LoginLog();
        loginLog.setUserName(request.getParameter("username"));
        loginLog.setOperateType(1);
        String ipAddr = IPUtils.getIpAddr(request);
        loginLog.setIp(ipAddr);
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setStatus(0 == result.getRet() ? 1 : 0);
        loginLog.setCreateTime(new Date());
        //异步写登录日志
        asyncTask.saveLoginLog(loginLog);
        logger.info(result.getRet() == 0 ? "登录成功" : "登录失败");
        return result;
    }
}

