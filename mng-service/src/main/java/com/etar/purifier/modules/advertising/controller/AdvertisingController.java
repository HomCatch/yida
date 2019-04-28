package com.etar.purifier.modules.advertising.controller;


import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.common.validation.XException;
import com.etar.purifier.modules.advertising.service.AdvertisingService;
import com.etar.purifier.modules.mqtt.MqttService;
import com.etar.purifier.utils.AdUtil;
import com.etar.purifier.utils.ConstantUtil;
import com.etar.purifier.utils.MqttUtil;
import entity.adverstising.Advertising;
import entity.common.entity.BatchReqVo;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.ResultCode;

import java.util.Date;
import java.util.List;

/**
 * 待机广告 AdvertisingController层
 *
 * @author hzh
 * @since 2018-10-15
 */
@RestController
@RequestMapping(value = "yida/ad")
public class AdvertisingController {
    private static Logger log = LoggerFactory.getLogger(AdvertisingController.class);
    private final AdvertisingService advertisingService;
    private final MqttService mqttService;

    @Autowired
    public AdvertisingController(AdvertisingService advertisingService, MqttService mqttService) {
        this.advertisingService = advertisingService;
        this.mqttService = mqttService;
    }

    /**
     * 新增banner
     *
     * @param advertising advertising
     */
    @PostMapping(value = "/ads")
    @LogOperate(description = "新增待机广告")
    @RequiresPermissions("sys:ads:save")
    public Result addAd(@Validated @RequestBody Advertising advertising) {
        log.info("进入设备广告");
        Result result = new Result();
        boolean exists = advertisingService.existsByName(advertising.getName());
        if (exists) {
            return result.error(ResultCode.AD_NAME_EXISTS);
        }
        //判断广告是否会乱码
        boolean readableSolgan = AdUtil.isReadableSolgan(advertising.getSolgan());
        if (!readableSolgan) {
            return result.error(ResultCode.AD_UNREADABLE_SOLGAN);
        }
        advertising.setCreateTime(new Date());
        advertising.setUpdateTime(new Date());
        //待审查状态
        advertising.setState(ConstantUtil.AD_TO_CHECK);
        try {
            advertisingService.save(advertising);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * 更新
     *
     * @param adId adId
     */
    @PutMapping(value = "/ads/{adId}")
    @LogOperate(description = "更新设备待机广告")
    @RequiresPermissions("sys:ads:update")
    public Result updateBanner(@PathVariable("adId") Integer adId, @Validated @RequestBody Advertising advertising) {
        log.info("进入更新广告接口");
        Result result = new Result();
        boolean exists = advertisingService.existsById(adId);
        if (!exists) {
            return result.error(ResultCode.BANNER_NOT_EXISTS);
        }
        //判断广告是否会乱码
        boolean readableSolgan = AdUtil.isReadableSolgan(advertising.getSolgan());
        if (!readableSolgan) {
            return result.error(ResultCode.AD_UNREADABLE_SOLGAN);
        }
        try {
            int ret = advertisingService.updateAdvertising(advertising);
            switch (ret) {
                case 0:
                    result.error(ResultCode.AD_NAME_EXISTS);
                    break;
                case 1:
                    result.ok();
                    break;
                case 0xff:
                    result.error(ResultCode.DB_ERROR);
                    break;
                default:
                    result.error(ResultCode.ERROR);
                    break;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 删除banner
     *
     * @param adId adId
     */
    @DeleteMapping(value = "/ads/{adId}")
    @LogOperate(description = "删除待机广告")
    @RequiresPermissions("sys:ads:delete")
    public Result delAd(@PathVariable("adId") Integer adId) {
        log.info("进入删除广告接口");
        Result result = new Result();
        boolean exists = advertisingService.existsById(adId);
        if (!exists) {
            return result.error(ResultCode.AD_NOT_EXISTS);
        }
        try {
            advertisingService.deleteById(adId);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * 批量删除banner
     *
     * @param batchReqVo id集合
     */
    @PostMapping(value = "/batch")
    @LogOperate(description = "批量删除待机banner")
    public Result delBatch(@Validated @RequestBody BatchReqVo batchReqVo) {
        log.info("进入批量删除广告接口");
        Result result = new Result();

        if (CollectionUtils.isEmpty(batchReqVo.getIdList())) {
            return result.error(ResultCode.AD_IDS_IS_NULL);
        }
        try {
            int ret = advertisingService.delBatch(batchReqVo);
            switch (ret) {
                case 2:
                    result.error(ResultCode.ADS_IS_NULL);
                    break;
                case 1:
                    result.ok();
                    break;
                default:
                    result.error(ResultCode.ERROR);
                    break;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result;
    }


    /**
     * banner上架或下架
     *
     * @param adId 广告
     */
    @PutMapping(value = "/ads/{adId}/state/{state}")
    public Result shelves(@PathVariable("adId") Integer adId, @PathVariable("state") Integer state) {
        log.info("进入广告上架或下架接口");

        Result result = new Result();
        Advertising advertising = advertisingService.findById(adId);
        int adState = 2;
        if (advertising == null) {
            return result.error(ResultCode.AD_NOT_EXISTS);
        }
        try {
            //上架
            if (state == ConstantUtil.CLICK_AD_STAY) {
                String advMsg = MqttUtil.getAdvMsg(advertising, ConstantUtil.MQTT_ADV_PREFIX_BROADCAST);
                try {

                    mqttService.publish(1, "broadcast", advMsg);
                    log.info("广告上架指令发送:" + advMsg + ",订阅硬件通道" + "broadcast_ret");
                    //  订阅设备广告应答
                    mqttService.subscribe("broadcast_ret", 0);
                    adState = 1;
                } catch (MqttException e) {
                    log.info(e.getMessage());
                    if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECTING || e.getReasonCode() == MqttException.REASON_CODE_SERVER_CONNECT_ERROR
                            || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED || e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST
                            || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECT_PROHIBITED || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED) {
                        try {
                            mqttService.reconnect();
                        } catch (MqttException e1) {
                            e1.printStackTrace();
                            log.info("重连MQTT服务器失败！");
                        }
                    } else {
                        log.info("Mqtt异常");
                    }
                    throw new XException(e.getMessage());
                }
            }
            //更改广告状态
            adState = getAdState(state, adState);
            //TODO 如果已上架广告，下架，下发默认广告
            advertisingService.shelves(adId, adState);
        } catch (Exception e) {
            e.printStackTrace();
            return result.error(ResultCode.MQTT_AD_SEND_FAIL);
        }
        return result.error(ResultCode.SUCCESS);
    }

    /**
     * 广告审核
     *
     * @param adId 广告
     */
    @PutMapping(value = "/ads/{adId}/audit/{state}")
    @LogOperate(description = "待机广告审核")
    @RequiresPermissions("sys:ads:audit")
    public Result audit(@PathVariable("adId") Integer adId, @PathVariable("state") Integer state) {
        log.info("广告审核");
        Result result = new Result();
        Advertising advertising = advertisingService.findById(adId);
        int adState = 2;
        if (advertising == null) {
            return result.error(ResultCode.AD_NOT_EXISTS);
        }
        try {
            //更改广告状态
            adState = getAdState(state, adState);
            advertisingService.shelves(adId, adState);
        } catch (Exception e) {
            return result.error(ResultCode.MQTT_CANT_SEND_CATIVE);
        }
        return result.error(ResultCode.SUCCESS);
    }

    /**
     * 更改广告状态
     *
     * @param state   点击状态
     * @param adState 广告状态
     * @return 要保存的广告状态
     */
    private int getAdState(Integer state, int adState) {
        if (state == ConstantUtil.CLICK_AD_AUDIT) {
            //点击审核通过
            adState = ConstantUtil.AD_NO_STAY;
        } else if (state == ConstantUtil.CLICK_AD_UNAUDIT) {
            //点击审核不通过
            adState = ConstantUtil.AD_CHECK_FAIL;
        } else if (state == ConstantUtil.CLICK_AD_UNSTAY) {
            //点击下架
            adState = ConstantUtil.AD_NO_STAY;
        }
        return adState;
    }


    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 页面大小
     * @return Page<Advertising> 对象
     */
    @GetMapping(value = "/pages")
    @RequiresPermissions("sys:ads:list")
    public Result findByPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, @RequestParam(value = "name", required = false, defaultValue = "") String name) {

        DataResult result = new DataResult();
        Page<Advertising> all = advertisingService.findByPage(page - 1, pageSize, name);
        List<Advertising> content = all.getContent();
        PageBean<Advertising> pageBean = new PageBean<>();
        pageBean.setCurPage(page);
        pageBean.setItemCounts(all.getTotalElements());
        pageBean.setPageSize(pageSize);
        pageBean.setList(content);
        result.ok();
        result.setDatas(pageBean);
        return result;
    }

    /**
     * 获取上架的广告
     *
     * @return 广告 对象
     */
    @GetMapping(value = "/ads")
    public Result findState() {
        DataResult result = new DataResult();
        Advertising banner = advertisingService.findByState(ConstantUtil.AD_IS_STAY);
        result.ok();
        result.setDatas(banner);
        return result;
    }

    /**
     * 获取广告对象
     *
     * @return 广告 对象
     */
    @GetMapping(value = "/findById")
    public Advertising findById(@RequestParam(value = "id") int id) {
        return advertisingService.findById(id);
    }

}