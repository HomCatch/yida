package com.etar.purifier.common.aspect;

import com.etar.purifier.common.async.AsyncTask;
import entity.common.entity.Result;
import com.etar.purifier.modules.sys.untils.R;
import com.etar.purifier.utils.IPUtils;
import entity.operatelog.OperateLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

//import org.springframework.kafka.core.KafkaTemplate;

/**
 * @program: xplatform
 * @description: 操作日志切面
 * @author: Gmq
 * @date: 2018-12-24 17:38
 **/
@Aspect
@Component
public class OperateLogAspect {


    private final AsyncTask asyncTask;

    @Autowired
    public OperateLogAspect(AsyncTask asyncTask) {
        this.asyncTask = asyncTask;

    }

    @Around(value = "@annotation(com.etar.purifier.common.annotation.LogOperate)")
    public Object doAfter(ProceedingJoinPoint joinPoint) throws Throwable {
        Object o = null;
        //调用执行目标方法(result为目标方法执行结果)
        long beginTime = System.currentTimeMillis();
        Object res = joinPoint.proceed();
        long time = System.currentTimeMillis() - beginTime;
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取到请求头对象
        HttpServletRequest request = attributes.getRequest();
        OperateLog operateLog = new OperateLog();
        //设置IP地址
        operateLog.setIp(IPUtils.getIpAddr(request));
        operateLog.setUserAgent(request.getHeader("User-Agent"));
        //请求方式
        operateLog.setReqType(request.getMethod());
        //用户名
        Object userName = request.getSession().getAttribute("username");
        operateLog.setUserName(userName == null ? "" : userName.toString());
        operateLog.setCreateTime(new Date());
        String requestURI = request.getRequestURI();
        //请求路径
        operateLog.setReqUrl(requestURI);
        if (res instanceof R) {
            R result = (R) res;
            //执行时长(毫秒)
            int status = (int) result.get("code");
            asyncTask.saveOperateLog(joinPoint, time, status, operateLog);
            o = result;
        } else if (res instanceof Result) {
            Result result = (Result) res;
            //执行时长(毫秒)
            int status = result.getRet();
            asyncTask.saveOperateLog(joinPoint, time, status, operateLog);
            o = result;
        }
        return o;
    }
}
