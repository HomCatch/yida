package com.etar.purifier.common.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.dev.service.DeviceService;
import com.etar.purifier.modules.loginlog.service.LoginLogService;
import com.etar.purifier.modules.operatelog.service.OperateLogService;
import com.etar.purifier.modules.users.service.UserService;
import com.etar.purifier.utils.ConstantUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.dev.Device;
import entity.loginlog.LoginLog;
import entity.operatelog.OperateLog;
import entity.user.User;
import entity.user.WxUser;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @program: purifier
 * @description: 异步任务
 * @author: Gmq
 * @date: 2019-03-13 10:39
 **/
@Component
public class AsyncTask {
    private final static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    private final RestTemplate restTemplate;
    private final LoginLogService loginLogService;
    private final OperateLogService operateLogService;
    private final DeviceService deviceService;
    private final UserService userService;
    private Gson gson = new GsonBuilder().create();
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    public AsyncTask(RestTemplate restTemplate, LoginLogService loginLogService, OperateLogService operateLogService, DeviceService deviceService, UserService userService) {
        this.restTemplate = restTemplate;
        this.loginLogService = loginLogService;
        this.operateLogService = operateLogService;
        this.deviceService = deviceService;
        this.userService = userService;
    }

    /**
     * 记录登陆日志异步任务
     *
     * @param loginLog 登录对象
     */
    @Async
    public void saveLoginLog(LoginLog loginLog) {

        //调用淘宝位置接口存入城市
        String jsonStr = "";
        String city = "";
        try {
            jsonStr = restTemplate.getForObject("http://ip.taobao.com/service/getIpInfo.php?ip=" + loginLog.getIp(), String.class);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            JSONObject data = jsonObject != null ? jsonObject.getJSONObject("data") : null;
            city = data == null ? city : data.getString("region") + "-" + data.getString("city");
            loginLog.setCity(city);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取淘宝地理位置API出错，连接超时！");
        }
        //存入日志表
        loginLogService.save(loginLog);
        //kafka收集
//        kafkaTemplate.send("loginLog",gson.toJson(loginLog));
        logger.info("+++++++++++++++++++++  message = {}", gson.toJson(loginLog));
        logger.info("记录登陆日志......");
    }

    /**
     * 保存操作日志
     *
     * @param joinPoint joinPoint
     * @param time      请求时间
     * @param status    状态
     */
    @Async
    public void saveOperateLog(JoinPoint joinPoint, long time, int status, OperateLog operateLog) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogOperate logOperate = method.getAnnotation(LogOperate.class);
            //请求的参数
            String params = "";
            Object[] args = joinPoint.getArgs();
            try {
                params = new Gson().toJson(args[0]);
                operateLog.setReqParam(params);
            } catch (Exception e) {

            }
            String userName2 = "";
            if (StringUtils.isBlank(operateLog.getUserName())) {
                //先根据devCode查询绑定的userId  再根据请求的userId  查出对应微信用户名
                String devCode = "";
                String userId = "";
                String[] parameterNames = signature.getParameterNames();
                for (int i = 0; i < parameterNames.length; i++) {
                    if (ConstantUtil.DEV_CODE.equals(parameterNames[i])) {
                        devCode = new Gson().toJson(args[i]);
                    }
                    if (ConstantUtil.USERID.equals(parameterNames[i])) {
                        userId = new Gson().toJson(args[i]);
                    }
                }
                if (StringUtils.isNotBlank(devCode)) {
                    Device byDevCode = deviceService.findByDevCode(devCode.replace("\"", "").replace("\"", ""));
                    if (byDevCode != null && byDevCode.getUserId() != null) {
                        userId = byDevCode.getUserId().toString();
                    }
                }
                if (StringUtils.isNotBlank(userId)) {
                    User byId = userService.findById(Integer.parseInt(userId.replace("\"", "").replace("\"", "")));
                    userName2 = byId != null ? byId.getNickName() : null;
                }
            }
            //状态
            operateLog.setStatus(0 == status ? 1 : 0);
            //请求时间
            operateLog.setReqTime((int) time);
            int adStatus;
            String adlog = "";
            String userName = operateLog.getUserName();
            String requestURI = operateLog.getReqUrl();
            if (StringUtils.isNotBlank(requestURI)) {
                if (requestURI.contains("/yida/ad/ads") && requestURI.contains("state")) {
                    adStatus = Integer.parseInt(requestURI.substring(requestURI.length() - 1));
                    adlog = adStatus == 0 ? "待机广告下架" : "待机广告上架";
                } else if (requestURI.contains("/yida/ad/ads") && requestURI.contains("audit")) {
                    adStatus = Integer.parseInt(requestURI.substring(requestURI.length() - 1));
                    adlog = adStatus == 2 ? "待机广告审核通过" : "待机广告审核失败";
                } else if (requestURI.contains("/yida/miniProgram/nick")) {
                    WxUser wxUser = JSONObject.parseObject(params, WxUser.class);
                    if (wxUser != null) {
                        userName = wxUser.getNickName();
                    }
                }
            }
            //用户名
            operateLog.setUserName(StringUtils.isNotBlank(userName) ? userName : userName2);
            //操作信息
            operateLog.setOperateInfo(StringUtils.isNotBlank(adlog) ? adlog : logOperate.description());
            operateLog.setReqTime((int) time);
            operateLog.setCreateTime(new Date());
            //保存系统日志
            operateLogService.save(operateLog);
            logger.info("+++++++++++++++++++++  message = {}", gson.toJson(operateLog));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //kafka收集
//        kafkaTemplate.send("operateLog", gson.toJson(operateLog));
        logger.info("保存操作日志......");
    }
}
