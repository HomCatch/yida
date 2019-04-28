package com.etar.mqtt;

import entity.adverstising.Advertising;
import entity.advstatic.AdvStatic;
import entity.dev.Device;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mng-service")
public interface PushCallbackService {


    /**
     * 通过devCode查询对象
     *
     * @param devCode 设备编码
     * @return 对象
     */
    @GetMapping(value = "/yida/dev/findByDevCode")
    Device findByDevCode(@RequestParam(value = "devCode") String devCode);

    /**
     * 更新设备绑定状态
     *
     * @param status  设备状态
     * @param devCode 设备码
     * @return 绑定的结果
     */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping(value = "/yida/dev/updateBindStatus")
    int updateBindStatus(@RequestParam(value = "status") Integer status, @RequestParam(value = "devCode") String devCode);

    /**
     * 硬件手动解除绑定
     *
     * @param status  设备状态
     * @param devCode 设备码
     * @return 解除结果
     * @throws MqttException MqttException
     */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping(value = "/yida/dev/hardwareUnbind")
    int hardwareUnbind(@RequestParam(value = "status") Integer status, @RequestParam(value = "devCode") String devCode) throws MqttException;

    /**
     * 开机修改上线状态
     *
     * @param online  上线状态
     * @param devCode 设备码
     * @return int int
     * @throws MqttException MqttException
     */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping(value = "/yida/dev/updateOnline")
    int updateOnline(@RequestParam(value = "online") Integer online, @RequestParam(value = "devCode") String devCode) throws MqttException;

    /**
     * 更新滤芯寿命
     *
     * @param filterLife 滤芯寿命
     * @param devCode    设备码
     */
    @GetMapping(value = "/yida/dev/updateFilterLife")
    void updateFilterLife(@RequestParam(value = "filterLife") Integer filterLife, @RequestParam(value = "devCode") String devCode);

    /**
     * 更新滤芯使用情况
     *
     * @param status     使用状态
     * @param filterCode 滤芯编码
     * @return 更新结果 int
     */
    @GetMapping(value = "/yida/filterInfos/updateFilterCode")
    int updateFilterCode(@RequestParam(value = "status") int status, @RequestParam(value = "filterCode") String filterCode);

    /**
     * 查询广告
     *
     * @param id 使用状态
     * @return 更新结果 int
     */
    @GetMapping(value = "/yida/ad/findById")
    Advertising findById(@RequestParam(value = "id") int id);

    /**
     * 保存advstatic对象
     *
     * @param advstatic 持久对象，或者对象集合
     */
    @PostMapping(value = "/advstatic/save")
    void save(@RequestBody AdvStatic advstatic);
}
