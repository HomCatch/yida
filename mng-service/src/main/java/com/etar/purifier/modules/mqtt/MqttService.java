package com.etar.purifier.modules.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * mqtt-service
 *
 * @author gmq
 * @version 1.0
 * @date 2019/3/19 13:42
 */
@FeignClient(value = "mqtt-service", fallback = MqttServiceHystricImpl.class)
public interface MqttService {

    /**
     * 发布，默认qos为0，非持久化
     *
     * @param topic       订阅主题
     * @param pushMessage 发送的消息
     */
    @GetMapping("/publish")
    int publish(@RequestParam(value = "qos") int qos, @RequestParam(value = "topic") String topic, @RequestParam(value = "pushMessage") String pushMessage) throws MqttException;

    /**
     * 订阅某个主题
     *
     * @param topic 主题
     * @param qos   订阅方式
     */
    @GetMapping("/subscribe")
    void subscribe(@RequestParam(value = "topic") String topic, @RequestParam(value = "qos", required = false) int qos) throws MqttException;

    /**
     * 重连
     */
    @GetMapping("/reconnect")
    void reconnect() throws MqttException;
}
