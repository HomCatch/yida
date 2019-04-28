package com.xiaohe.etar.miniprogram.modules.miniprogram;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.xiaohe.etar.miniprogram.common.LogOperate;
import com.xiaohe.etar.miniprogram.common.validation.XException;
import com.xiaohe.etar.miniprogram.modules.banner.service.BannerService;
import com.xiaohe.etar.miniprogram.modules.dev.service.DeviceService;
import com.xiaohe.etar.miniprogram.modules.filterinfo.service.FilterInfoService;
import com.xiaohe.etar.miniprogram.modules.inletfilter.service.InletFilterService;
import com.xiaohe.etar.miniprogram.modules.mqtt.MqttFeignService;
import com.xiaohe.etar.miniprogram.modules.users.UserFeignService;
import com.xiaohe.etar.miniprogram.utils.*;
import entity.banner.Banner;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import entity.common.wx.constans.WxConfig;
import entity.dev.Device;
import entity.filterinfo.FilterInfo;
import entity.inletfilter.InletFilter;
import entity.user.User;
import entity.user.WxUser;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.ResultCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 小程序接口类
 *
 * @author hzh
 * @date 2018/11/27
 */
@RestController
@RequestMapping(value = "yida/miniProgram")
@Validated
public class MiniProgramController {

    private static Logger log = LoggerFactory.getLogger(MiniProgramController.class);

    @Value("${emq_server}")
    private String emqServer;
    @Value("${connect_url}")
    private String connectUrl;
    @Value("${appid}")
    private String appid;
    @Value("${appsecret}")
    private String appsecret;

    @Autowired
    private MqttFeignService mqttFeignService;
    private final UserFeignService userService;
    private final DeviceService deviceService;
    private final BannerService bannerService;
    private final InletFilterService inletFilterService;
    private final FilterInfoService filterInfoService;

    @Autowired
    public MiniProgramController(FilterInfoService filterInfoService, UserFeignService userService, DeviceService deviceService, BannerService bannerService, InletFilterService inletFilterService) {
        this.filterInfoService = filterInfoService;
        this.userService = userService;
        this.deviceService = deviceService;
        this.bannerService = bannerService;
        this.inletFilterService = inletFilterService;
    }

    /**
     * 通过code 获取微信的opedId
     *
     * @param code 微信code
     */

    @GetMapping(value = "/info/{code}")
    public DataResult userInfo(@NotBlank(message = "微信CODE不能为空") @PathVariable("code") String code) {

        DataResult result = new DataResult();
        // 拼接请求地址
        String requestUrl = WxConfig.USER_OPENID_URL.replace("JSCODE", code);
        log.info("请求的requestUrl:{}", requestUrl);
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        log.info("返回的jsonObject:{}", jsonObject.toJSONString());
        if (jsonObject.containsKey("errcode") && jsonObject.containsKey("errmsg")) {
            log.info("获取微信openid失败，微信返回错误：{}", jsonObject.toJSONString());
            result.error(ResultCode.USER_CODE_ERROR);
            return result;
        }

        //保存用户注册信息
        String openid = (String) jsonObject.get("openid");
        User user = null;
        try {
            user = userService.saveByUserInfo(openid);
        } catch (Exception e) {
            throw new XException("网络异常，保存用户失败，请稍后重试");
        }
        if (user != null) {
            jsonObject.put("userId", user.getId());
        }
        result.ok();
        result.setDatas(jsonObject);
        log.info("返回用户id:{}", JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 保存对象昵称
     *
     * @param wxUser 微信对象
     */

    @PostMapping(value = "/nick")
    @LogOperate(description = "保存对象昵称")
    public Result nickName(@Validated @RequestBody WxUser wxUser) {
        Result result = new Result();
        log.info("进入保存微信昵称：" + JSONObject.toJSONString(wxUser));
        try {
            //通过openId查询对象，比较昵称是否相等
            User byOpenId = null;
            try {
                byOpenId = userService.findByOpenId(wxUser.getOpenId());
            } catch (Exception e) {
                throw new XException("网络异常，查找用户失败，请稍后重试");
            }
            if (byOpenId != null) {
                String nickName = byOpenId.getNickName();
                if (nickName != null && nickName.equals(wxUser.getNickName())) {
                    return result.ok();
                }
                try {
                    userService.saveNickName(wxUser);
                } catch (Exception e) {
                    throw new XException("网络异常，保存用户昵称失败，请稍后重试");
                }
                result.ok();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result.error(ResultCode.ERROR);
        }
        return result;
    }


    /**
     * 绑定设备
     *
     * @param devCode 设备code
     */
    @GetMapping(value = "/bind/{devCode}/{userId}")
    @LogOperate(description = "绑定设备")
    public Result bind(@NotBlank(message = "设备CID不能为空") @PathVariable String devCode, @NotNull(message = "用户id不能为空") @PathVariable Integer userId) {
        log.info("获取到的devCode:" + devCode);
        Result result;
        int code = DevCodeUtil.verifyDevCode(devCode);
        result = DevCodeUtil.parseCode(code);
        if (!Result.isOk(result)) {
            return result;
        }
        try {
            Device device = deviceService.findByDevCode(devCode);
            if (device == null) {
                result.error(ResultCode.DEV_NOT_EXIST);
                return result;
            }
            //检查设备是否还能绑定
            int bindResult = device.getStatus();
            switch (bindResult) {
                //可以绑定
                case 0:
                    result.ok();
                    break;
                //不可绑定状态
                case 1:
                    //是否被绑定
                    return result.error(ResultCode.DEV_IS_BINDING);
                default:
                    //未知异常
                    return result.error(ResultCode.ERROR);
            }
            try {
                mqttFeignService.publish(1, "activate/" + devCode, MqttUtil.getActiveTime());
                deviceService.bindDev(devCode, userId);
                log.info("activate/" + devCode + "激活下发成功,订阅硬件通道主题：" + "activate_ret/" + devCode);
                result.ok();
            } catch (HystrixRuntimeException e) {
                log.info(e.getMessage());
                return new Result().error(ResultCode.MQTT_SEND_FAIL);
            } catch (MqttException e) {
                if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECTING || e.getReasonCode() == MqttException.REASON_CODE_SERVER_CONNECT_ERROR
                        || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED || e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST
                        || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECT_PROHIBITED || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED) {
                    mqttFeignService.reconnect();
                } else {
                    log.info("Mqtt异常");
                }
                return new Result().error(ResultCode.MQTT_CANT_SEND_CATIVE);
            }

        } catch (Exception e) {
            log.info(e.getMessage());
            result.error(ResultCode.MQTT_CANT_SEND_CATIVE);
        }
        return result;
    }


    /**
     * 获取设备绑定状态
     *
     * @param devCode 设备编码
     */
    @GetMapping(value = "/status/{devCode}")
    public DataResult status(@NotBlank(message = "设备CID不能为空") @PathVariable("devCode") String devCode) {
        DataResult result = new DataResult();
        try {
            Device device = deviceService.findByDevCode(devCode);
            //返回能否被绑定的状态
            result.setDatas(device.getStatus());
            result.ok();
            log.info("获取绑定设备的状态：{}", JSONObject.toJSONString(result));
        } catch (Exception e) {
            log.error(e.getMessage());
            result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 获取设备列表
     *
     * @param userId 用户id
     * @return 查询结果
     */
    @GetMapping(value = "/list")
    public Result deviceList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                             @NotNull(message = "用户ID不能为空") @RequestParam(value = "userId") Integer userId) {
        DataResult result = new DataResult();

        List<Device> deviceList = deviceService.findUserBindDevList(1, userId);
        if (!deviceList.isEmpty()) {
            //一般是分页后的数据集合，不会很多
            deviceService.getDevBindAccount(deviceList);
        }
        //设备连接信息
        for (Device device : deviceList) {
            MqttUtil.getDevOnlineStatus(device, emqServer + connectUrl, appid, appsecret);
        }
        PageBean<Device> pageBean = new PageBean<>();
        pageBean.setCurPage(page);
        pageBean.setPageSize(pageSize);
        pageBean.setItemCounts((long) deviceList.size());
        pageBean.setList(deviceList);
        result.ok();
        result.setDatas(pageBean);
        log.info("设备列表：{}", JSONObject.toJSONString(result));
        return result;
    }


    /**
     * 解除绑定
     *
     * @param devCode 设备id
     */
    @GetMapping(value = "/unbind/{devCode}")
    @LogOperate(description = "解除绑定")
    public Result unbindDev(@NotBlank(message = "设备ID不能为空") @PathVariable("devCode") String devCode) {
        Result result = new Result();
        try {
            if (StringUtils.isBlank(devCode)) {
                result.error(ResultCode.DEV_ID_NULL);
            }
            Device device = deviceService.findByDevCode(devCode);
            try {
                deviceService.unbindDevByDevId(device.getId());
                mqttFeignService.publish(1, "activate/" + devCode, "3,1");
                mqttFeignService.subscribe("activate_ret/" + devCode, 0);
                result.ok();
            } catch (HystrixRuntimeException e) {
                log.info(e.getMessage());
                return new Result().error(ResultCode.MQTT_SEND_FAIL);
            } catch (MqttException e) {
                if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECTING || e.getReasonCode() == MqttException.REASON_CODE_SERVER_CONNECT_ERROR
                        || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED || e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST
                        || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECT_PROHIBITED || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED) {
                    mqttFeignService.reconnect();
                } else {
                    log.info("Mqtt异常");
                }
                throw new XException(e.getMessage());
            } catch (Exception e) {
                result.error(ResultCode.DEV_UNBIND_FAIL);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 获取上架的banner
     *
     * @return banner 对象
     */
    @GetMapping(value = "/banners")
    public Result findStateBanner() {
        DataResult result = new DataResult();
        Banner banner = bannerService.findByState(1);
        result.setDatas(banner);
        result.ok();
        return result;
    }

    /**
     * 获取上架的广告
     *
     * @return 广告 对象
     */
    @GetMapping(value = "/filters")
    public Result findStateFilter() {
        DataResult result = new DataResult();
        InletFilter inletFilter = inletFilterService.findByState(1);
        result.setDatas(inletFilter);
        result.ok();
        return result;
    }

    /**
     * 获取滤芯状态
     *
     * @param filterCode 滤芯编码
     * @return 绑定结果
     */
    @GetMapping(value = "/validFilterCode")
    public Result validFilterCode(@NotBlank(message = "滤芯编码不能为空") @RequestParam(value = "filterCode") String
                                          filterCode,
                                  @NotNull(message = "用户id不能为空") @RequestParam(value = "userId") Integer userId,
                                  @NotBlank(message = "设备CID不能为空") @RequestParam(value = "devCode") String devCode) {
        Result result = new Result();
        Device byDevCode = deviceService.findByDevCode(devCode);
        if (byDevCode.getUserId().equals(userId)) {
            if (filterCode.equals(byDevCode.getFilterCode())) {
                FilterInfo byFilterCode = filterInfoService.findByFilterCode(filterCode);
                //查看滤芯是否被自己绑定
                if (byFilterCode.getStatus().equals(1)) {
                    return result.error(ResultCode.FILTER_INFO_USER_BY_SELF);
                }
            }
        }
        //检查filterCode
        int verifyFilterCode = filterInfoService.verifyFilterCode(filterCode);
        bindResult(verifyFilterCode, result);
        return result;
    }


    /**
     * 获取滤芯激活状态
     *
     * @param devCode 设备编码
     * @return 绑定结果
     */
    @GetMapping(value = "/filterActive/{devCode}")
    public Result filterActive(@NotBlank(message = "设备CID不能为空") @PathVariable("devCode") String devCode) {
        DataResult result = new DataResult();

        Device byDevCode = deviceService.findByDevCode(devCode);
        if (byDevCode.getFilterLifeTime().equals(ConstantUtil.ACTIVE_FILTER)) {
            result.setDatas(1);
        } else {
            result.setDatas(0);
        }
        result.ok();
        return result;
    }

    /**
     * 扫码激活滤芯
     *
     * @param devCode    设备编码
     * @param filterCode 滤芯编码
     * @return 绑定结果
     */
    @GetMapping(value = "/activeFilterLife")
    @LogOperate(description = "扫码激活滤芯")
    public Result activeFilterLife(@NotBlank(message = "设备CID不能为空") @RequestParam("devCode") String
                                           devCode, @NotBlank(message = "滤芯编码不能为空") @RequestParam("filterCode") String filterCode) {

        Result result;
        //检查devCode
        int code = DevCodeUtil.verifyDevCode(devCode);
        result = DevCodeUtil.parseCode(code);
        if (!Result.isOk(result)) {
            return result;
        }
        //检查filterCode
        int verifyFilterCode = filterInfoService.verifyFilterCode(filterCode);
        result = bindResult(verifyFilterCode, result);
        if (!Result.isOk(result)) {
            return result;
        }
        try {
            Device byDevCode = deviceService.findByDevCode(devCode);
            //下发激活滤芯指令
            String activeTime = DateUtil.format(new Date());
            mqttFeignService.publish(1, "activate/" + devCode, "4,1," + activeTime);
            log.info("下发激活滤芯指令:" + "4,1," + activeTime);
            if (byDevCode != null) {
                mqttFeignService.subscribe("activate_ret/" + devCode, 0);
                log.info("订阅设备:" + "activate_ret/" + devCode + "，保存滤芯编号");
                //激活设备滤芯，保存滤芯编码
                byDevCode.setFilterCode(filterCode);
                deviceService.save(byDevCode);
                //更新滤芯寿命
                deviceService.changeFilter(100, devCode);
                //更新为已使用
                filterInfoService.updateFilterCode(1, filterCode);
            }
        } catch (HystrixRuntimeException e) {
            log.info(e.getMessage());
            return new Result().error(ResultCode.MQTT_SEND_FAIL);
        } catch (MqttException e) {
            if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECTING || e.getReasonCode() == MqttException.REASON_CODE_SERVER_CONNECT_ERROR
                    || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED || e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST
                    || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECT_PROHIBITED || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED) {
                try {
                    mqttFeignService.reconnect();
                } catch (MqttException e1) {
                    e1.printStackTrace();
                }
                return result.error(301, "客户机未连接，请稍后重试！");
            } else {
                log.info("Mqtt异常");
            }
        } catch (Exception e) {
            return result.error(301, "滤芯修改失败");
        }

        return result;
    }


    /**
     * 蓝牙绑定滤芯
     *
     * @param devCode    设备号
     * @param filterCode 滤芯编码
     * @return 绑定结果
     */
    @GetMapping(value = "/btBindFilter")
    @LogOperate(description = "蓝牙绑定滤芯")
    public Result bluetoothBindFilter(@NotBlank(message = "设备编码不能为空") @RequestParam(value = "devCode") String
                                              devCode,
                                      @NotBlank(message = "滤芯编码不能为空") @RequestParam(value = "filterCode") String filterCode) {
        log.info("进入蓝牙绑定滤芯后更新数据后台:" + devCode);
        Result result;
        //检查devCode
        int code = DevCodeUtil.verifyDevCode(devCode);
        result = DevCodeUtil.parseCode(code);
        if (!Result.isOk(result)) {
            return result;
        }
        //检查filterCode
        int verifyFilterCode = filterInfoService.verifyFilterCode(filterCode);
        result = bindResult(verifyFilterCode, result);
        if (!Result.isOk(result)) {
            return result;
        }
        Device byDevCode = deviceService.findByDevCode(devCode);
        if (byDevCode != null) {
            //更新滤芯寿命
            deviceService.changeFilter(100, devCode);
            //更新为已使用
            filterInfoService.updateFilterCode(1, filterCode);
        }
        return result;
    }

    /**
     * 蓝牙绑定设备
     *
     * @param devCode 设备编号
     * @param userId  用户ID
     * @return 绑定结果
     */
    @GetMapping(value = "/btBindDev")
    @LogOperate(description = "蓝牙绑定设备")
    public Result bluetoothBindDev(@NotBlank(message = "设备编码不能为空") @RequestParam(value = "devCode") String devCode,
                                   @NotNull(message = "用户id不能为空") @RequestParam(value = "userId") Integer userId) {
        log.info("进入蓝牙绑定设备后更新数据后台:" + devCode);
        Result result;
        int code = DevCodeUtil.verifyDevCode(devCode);
        result = DevCodeUtil.parseCode(code);
        if (!Result.isOk(result)) {
            return result;
        }
        Device device = deviceService.findByDevCode(devCode);
        if (device == null) {
            return result.error(ResultCode.DEV_NOT_EXIST);
        }
        try {
            device.setStatus(1);
            device.setUserId(userId);
            deviceService.save(device);
        } catch (Exception e) {
            log.error("蓝牙绑定失败");
            return new Result().error(ResultCode.BLUETOOTH_BINDIND_FAIL);
        }
        return result.ok();
    }

    /**
     * 蓝牙传输指令
     *
     * @param devCode 设备号
     * @return 组合好的指令
     */
    @GetMapping(value = "/btCommand")
    @LogOperate(description = "蓝牙传输指令")
    public Result getBluetoothKey(@NotBlank(message = "设备号不能为空") @RequestParam(value = "devCode") String devCode) {
        DataResult result = new DataResult();
        String btStr = "1,1," + devCode;
        log.info("获取蓝牙指令:" + btStr);
        result.setDatas(btStr);
        return result.ok();
    }

    /**
     * 组合result
     *
     * @param ver    判断标识
     * @param result 结果
     */
    private static Result bindResult(int ver, Result result) {
        switch (ver) {
            case 0:
                result.ok();
                break;
            case 1:
                result.error(ResultCode.FILTER_INFO_NOT_EXIST);
                break;
            case 2:
                result.error(ResultCode.FILTER_INFO_IS_USER);
                break;
            default:
                result.error(ResultCode.ERROR);
                break;
        }
        return result;
    }
}
