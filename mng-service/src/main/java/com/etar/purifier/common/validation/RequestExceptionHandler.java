package com.etar.purifier.common.validation;

import entity.common.entity.Result;
import com.etar.purifier.modules.errorlog.service.ErrorLogService;
import com.etar.purifier.utils.HttpHelper;
import com.etar.purifier.utils.IPUtils;
import com.etar.purifier.utils.StringUtil;
import entity.errorlog.ErrorLog;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utils.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleBodyException(MethodArgumentNotValidException exception) throws UnsupportedEncodingException {
        BindingResult result = exception.getBindingResult();
        List<ObjectError> errors = result.getAllErrors();
        StringBuffer buffer = new StringBuffer(500);
        for (ObjectError tmp : errors) {
            buffer.append(tmp.getDefaultMessage()).append(",");
        }
        String errorString = buffer.toString();
        logger.error("实体属性校验异常:" + errorString);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "text/html; charset=utf-8");
        return new ResponseEntity<Object>(errorString.substring(0, errorString.length() - 1), headers, HttpStatus.BAD_REQUEST);
    }


    /**
     * 声明要捕获的异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result defaultExceptionHandler(UnauthorizedException e) {
        saveSysLog(StringUtil.errorInfo(e));
        logger.error(e.getMessage());
        Result result = new Result();
        return result.error(ResultCode.NO_AUTH);
    }

    /**
     * 声明要捕获的异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result defaultExceptionHandler(AuthenticationException e) {
        saveSysLog(StringUtil.errorInfo(e));
        logger.error(e.getMessage());
        Result result = new Result();
        return result.error(ResultCode.LOGIN_NOT);
    }

    /**
     * 参数错误异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> ConstraintViolationException(ConstraintViolationException e) {
        saveSysLog(StringUtil.errorInfo(e));
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuffer buffer = new StringBuffer(500);
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            System.out.println(constraintViolation.getMessage());
            buffer.append(constraintViolation.getMessage()).append(",");
        }
        String errorString = buffer.toString();
        logger.error("实体属性校验异常:" + errorString);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "text/html; charset=utf-8");
        return new ResponseEntity<Object>(errorString.substring(0, errorString.length() - 1), headers, HttpStatus.BAD_REQUEST);
    }

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
