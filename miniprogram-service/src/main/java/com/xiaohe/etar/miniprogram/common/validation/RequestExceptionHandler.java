package com.xiaohe.etar.miniprogram.common.validation;

import com.xiaohe.etar.miniprogram.modules.errorlog.service.ErrorLogService;
import com.xiaohe.etar.miniprogram.utils.HttpHelper;
import com.xiaohe.etar.miniprogram.utils.IPUtils;
import com.xiaohe.etar.miniprogram.utils.StringUtil;
import entity.common.entity.Result;
import entity.errorlog.ErrorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 自定义异常捕获类
 *
 * @author hzh
 */
@RestControllerAdvice
public class RequestExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);

    @Autowired
    private ErrorLogService errorLogService;


    /**
     * 声明要捕获的异常
     */
    @ExceptionHandler(XException.class)
    public Result defaultExceptionHandler(XException e) {
        saveSysLog(StringUtil.errorInfo(e));
        logger.error(e.getMessage());
        Result result = new Result();
        return result.error(e.getRet(), e.getMsg());
    }

    /**
     * 记录异常日志
     *
     * @param errorMsg
     */
    private void saveSysLog(String errorMsg) {//request对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map parameterMap = request.getParameterMap();
        ErrorLog errorLog = new ErrorLog();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        errorLog.setReqType(method);
        String bodyString = null;
        String reqParam = null;
        try {
            if (RequestMethod.GET.name().equals(method) || RequestMethod.DELETE.name().equals(method)) {
                reqParam = requestURI.substring(requestURI.lastIndexOf("/") + 1, requestURI.length());
            } else {
                bodyString = HttpHelper.getBodyString(request);
                reqParam = CollectionUtils.isEmpty(parameterMap) ? bodyString : parameterMap.toString();
            }
        } catch (IOException e) {
            errorMsg += StringUtil.errorInfo(e);
        }
        errorLog.setReqParam(reqParam);
        errorLog.setReqUrl(requestURI);
        errorLog.setIp(IPUtils.getIpAddr(request));
        errorLog.setUserAgent(request.getHeader("User-Agent"));
        errorLog.setCreateTime(new Date());
        errorLog.setErrorInfo(errorMsg);

        //保存异常日志
        errorLogService.save(errorLog);
        logger.info("记录异常日志......");
    }

}
