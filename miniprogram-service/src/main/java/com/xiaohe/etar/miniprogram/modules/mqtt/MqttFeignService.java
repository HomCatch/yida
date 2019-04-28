package com.xiaohe.etar.miniprogram.modules.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hzh
 * @version 1.0
 * @date 2019/3/20 10:11
 */
@FeignClient(value = "mqtt-service", fallback = MqttFeignServiceImpl.class)
public interface MqttFeignService {
    /**
     * 发送mqtt消息
     *
     * @param qos         发送类型
     * @param topic       主题
     * @param pushMessage 消息体
     * @throws MqttException mqt异常
     */
    @GetMapping(value = "/publish")
    int publish(@RequestParam(value = "qos") int qos, @RequestParam(value = "topic") String topic, @RequestParam(value = "pushMessage") String pushMessage) throws MqttException;

    /**
     * 订阅
     *
     * @param topic 订阅的主题
     * @param qos   订阅方式
     * @throws MqttException mqt异常
     */
    @GetMapping(value = "/subscribe")
    void subscribe(@RequestParam(value = "topic") String topic, @RequestParam(value = "qos") int qos) throws MqttException;

    /**
     * 重连
     *
     * @throws MqttException mqt异常
     */
    @GetMapping(value = "/reconnect")
    void reconnect() throws MqttException;
}
