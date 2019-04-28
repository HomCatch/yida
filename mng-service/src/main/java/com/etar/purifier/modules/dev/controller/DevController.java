package com.etar.purifier.modules.dev.controller;

import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.dev.service.DeviceService;
import com.etar.purifier.modules.emqclient.service.EmqClientService;
import com.etar.purifier.modules.mqtt.MqttService;
import com.etar.purifier.utils.DateUtil;
import com.etar.purifier.utils.DevCodeUtil;
import com.etar.purifier.utils.MqttUtil;
import entity.common.entity.BatchReqVo;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import entity.dev.Device;
import entity.emqclient.EmqClient;
import entity.emqclient.QueryEmqClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.ResultCode;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hzh
 * @date 2018/10/8
 */
@RestController
@RequestMapping(value = "/yida/dev")
@Validated
public class DevController {


    @Value("${emq_server}")
    private String emqServer;
    @Value("${connect_url}")
    private String connectUrl;
    @Value("${appid}")
    private String appid;
    @Value("${appsecret}")
    private String appsecret;
    //    @Resource
//    private MqttConfiguration mqttConfiguration;
    private static Logger log = LoggerFactory.getLogger(DevController.class);

    private final DeviceService deviceService;
    private final MqttService mqttService;
    private final EmqClientService emqClientService;

    @Autowired
    public DevController(DeviceService deviceService, MqttService mqttService, EmqClientService emqClientService) {
        this.deviceService = deviceService;
        this.mqttService = mqttService;
        this.emqClientService = emqClientService;
    }

    /**
     * 解除绑定
     *
     * @param devId 设备id
     */
    @PutMapping(value = "/unbind/{devId}")
    @LogOperate(description = "解除绑定")
//    @RequiresPermissions("dev:unbindDev")
    public Result unbindDev(@NotNull(message = "设备ID不能为空") @PathVariable("devId") Integer devId) {
        Result result = new Result();
        try {
            if (devId == null) {
                result.error(ResultCode.DEV_ID_NULL);
            }
            Device device = deviceService.findById(devId);
            if (device == null) {
                return result.error(ResultCode.DEV_NOT_EXIST);
            }
            //todo 解除绑定
            int active = unbindDev(devId, device.getDevCode());
            if (1 == active) {
                result.ok();
            } else if (2 == active) {
                result.error(ResultCode.DEV_UNBIND_FAIL);
            } else {
                result.error(ResultCode.ERROR);
            }
        } catch (Exception e) {
            result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 进入解绑
     *
     * @param devId 设备id
     * @param code  code
     */
    private int unbindDev(Integer devId, String code) {
        try {

            mqttService.publish(1, "activate/" + code, "3,1");
            log.info("进入后台解绑：" + "activate/" + code);
            //更改数据库状态
            deviceService.unbindDevByDevId(devId);
            return 1;
        } catch (MqttException e) {
            if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECTING || e.getReasonCode() == MqttException.REASON_CODE_SERVER_CONNECT_ERROR
                    || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED || e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST
                    || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECT_PROHIBITED || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED) {
                try {
                    mqttService.reconnect();
                } catch (MqttException e1) {
                    e1.printStackTrace();
                    log.info("重连MQTT服务器失败！");
                }
                log.info("解绑失败,请稍后重试");
            } else {
                log.info("Mqtt异常");
            }
            return 2;
        }
    }

    /**
     * 批量解除绑定
     *
     * @param batchReqVo 批量绑定id集合
     */
    @PostMapping(value = "/unbind")
    @LogOperate(description = "批量解除绑定")
//    @RequiresPermissions("dev:unbindDevBatch")
    public Result unbindDevBatch(@Validated @RequestBody BatchReqVo batchReqVo) {
        Result result = new Result();
        try {
            List<Integer> idList = batchReqVo.getIdList();
            if (idList.isEmpty()) {
                result.error(ResultCode.DEV_ID_NULL);
            }
            //解绑
            for (Integer devId : idList) {
                unbindDev(devId);
            }
            deviceService.unbindDevBatch(idList);

            result.ok();
        } catch (NullPointerException e) {
            log.info(e.getMessage());
            return result.error(ResultCode.NULL_POINTER_ERROR);
        } catch (Exception e) {
            log.info(e.getMessage());
            result.error(ResultCode.DB_ERROR);
        }
        return result;
    }


    /**
     * 删除设备
     *
     * @param devId 要删除的id
     * @return 结果
     */
    @DeleteMapping(value = "/devices/{devId}")
    @LogOperate(description = "删除设备")
//    @RequiresPermissions("dev:delete")
    public Result delDevice(@PathVariable("devId") Integer devId) {
        Result result = new Result();
        try {
            boolean exists = deviceService.existsById(devId);
            if (!exists) {
                return result.error(ResultCode.DEV_NOT_EXIST);
            }
            deviceService.deleteById(devId);
            result.ok();
        } catch (Exception e) {
            log.info(e.getMessage());
            result.error(ResultCode.DB_ERROR);
        }
        return result;
    }

    /**
     * 批量删除设备
     *
     * @param batchReqVo 要删除的id集合
     * @return 结果
     */
    @PostMapping(value = "/batch")
    @LogOperate(description = "批量删除设备")
//    @RequiresPermissions("dev:delBatch")
    public Result delBatch(@Validated @RequestBody BatchReqVo batchReqVo) {
        Result result = new Result();
        if (batchReqVo.getIdList().isEmpty()) {
            return result.error(ResultCode.DEV_ID_NULL);
        }
        try {
            deviceService.deleteBatch(batchReqVo.getIdList());
            result.ok();
        } catch (Exception e) {
            log.info(e.getMessage());
            result.error(ResultCode.ERROR);
        }
        return result;
    }


    /**
     * 是否开启重复绑定功能
     *
     * @param devId 设备id
     * @return 状态
     */
    @PutMapping(value = "/repeat/{devId}")
    @LogOperate(description = "是否开启重复绑定功能")
//    @RequiresPermissions("dev:repeat")
    public Result repeat(@PathVariable("devId") Integer devId, @RequestParam(value = "enableMultipleBind") Integer enableMultipleBind) {
        Result result = new Result();
        try {
            if (devId == null) {
                return result.error(ResultCode.DEV_ID_NULL);
            }
            //更新数据
            result = unbindDev(devId);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * 新增设备
     *
     * @param device 新增对象属性
     * @return 结果
     */
    @PostMapping(value = "/devices")
    @LogOperate(description = "新增设备")
//    @RequiresPermissions("dev:add")
    public Result addDevice(@RequestBody Device device) {
        Result result;
        int code = DevCodeUtil.verifyDevCode(device.getDevCode());
        result = DevCodeUtil.parseCode(code);
        if (!Result.isOk(result)) {
            return result;
        }
        try {
            boolean exists = deviceService.existsByDevCode(device.getDevCode());
            if (exists) {
                return result.error(ResultCode.DEV_CID_EXIST);
            }
            device.setInventoryTime(new Date());
            //默认绑定时间为2019-01-01 00:00:00，方便判断重绑
            device.setBindTime(DateUtil.DEFAULT_TIME);
            deviceService.save(device);
            result.ok();
        } catch (Exception e) {
            log.info(e.getMessage());
            result.error(ResultCode.DB_ERROR);
        }
        return result;
    }

    /**
     * 更新设备
     *
     * @param devId 设备id
     * @return 状态
     */
    @PutMapping(value = "/devices/{devId}")
    @LogOperate(description = "更新设备")
//    @RequiresPermissions("dev:update")
    public Result updateDevice(@PathVariable("devId") Integer devId, @RequestBody Device device) {
        Result result;
        int code = DevCodeUtil.verifyDevCode(device.getDevCode());
        result = DevCodeUtil.parseCode(code);
        if (!Result.isOk(result)) {
            return result;
        }
        try {
            if (devId == null) {
                return result.error(ResultCode.DEV_ID_NULL);
            }
            //更新数据
            Device dbDevice = deviceService.findById(devId);
            if (dbDevice == null) {
                return result.error(ResultCode.DEV_NOT_EXIST);
            }
            //todo 设备暂时不更新数据
            // deviceService.updatedDevice(device);
            result.ok();
        } catch (Exception e) {
            log.info(e.getMessage());
            result.error(ResultCode.DB_ERROR);
        }
        return result;
    }

    /**
     * 获取设备列表
     *
     * @param devCode 设备code
     * @return 查询结果
     */
    @GetMapping(value = "/list")
//    @RequiresPermissions("dev:list")
    public DataResult deviceList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "devCode", required = false, defaultValue = "") String devCode,
                                 @RequestParam(value = "userId", required = false) Integer userId) {
        DataResult result = new DataResult();
        Page<Device> devicePage = deviceService.findPage(page - 1, pageSize, devCode, userId);
        List<Device> deviceList = devicePage.getContent();
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
        pageBean.setItemCounts(devicePage.getTotalElements());
        //按在线状态排序  在线在前
        List<Device> collect = deviceList.stream().sorted(Comparator.comparingInt(Device::getOnline).reversed()).collect(Collectors.toList());
        pageBean.setList(collect);
        result.ok();
        result.setDatas(pageBean);
        return result;
    }


    @GetMapping("/auth")
    public void authDev(@RequestParam(value = "clientid", defaultValue = "") String clientId, HttpServletResponse response) throws IOException {
        log.info("开始认证......");
        boolean b = deviceService.existsByDevCode(clientId);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (b || clientId.contains("backend")) {
            response.setStatus(200);
            log.info("认证成功 ！");
        } else {
            response.setStatus(401);
            log.info("认证失败 ！");
        }
    }

    /**
     * 获取设备连接历史
     *
     * @param queryEmqClient 设备连接
     * @return 查询结果
     */
    @PostMapping(value = "/clientConnRecord")
//    @RequiresPermissions("dev:connect:records")
    public DataResult getClientConn(@RequestBody QueryEmqClient queryEmqClient) {
        DataResult result = new DataResult();
        try {
            //获取最近100条数据
            Page<EmqClient> devicePage = emqClientService.findAll(queryEmqClient.getPage() - 1, queryEmqClient.getPageSize()
                    , queryEmqClient);
            List<EmqClient> deviceList = devicePage.getContent();
            PageBean<EmqClient> pageBean = new PageBean<>();
            pageBean.setCurPage(queryEmqClient.getPage());
            pageBean.setPageSize(queryEmqClient.getPageSize());
            pageBean.setItemCounts(devicePage.getTotalElements() > 100 ? 100 : devicePage.getTotalElements());
            pageBean.setList(deviceList);
            result.ok();
            result.setDatas(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            result.error(2005, "查询出错");
            return result;
        }
        return result;
    }
}
