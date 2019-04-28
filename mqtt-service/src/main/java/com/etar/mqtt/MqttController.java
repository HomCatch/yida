package com.etar.mqtt;


import entity.advstatic.AdvStatic;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * MqttPushClientController控制层
 *
 * @author gmq
 * @version 1.0
 * @date 2019/3/18 13:41
 */
@RestController
public class MqttController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttController.class);
    private final MqttPushClient mqttPushClient;
    @Resource
    private PushCallbackService pushCallbackService;

    @Autowired
    public MqttController(MqttPushClient mqttPushClient) {
        this.mqttPushClient = mqttPushClient;
    }

    /**
     * 发布，默认qos为0，非持久化
     *
     * @param topic       订阅主题
     * @param pushMessage 发送的消息
     */
    @GetMapping("/publish")
    public int publish(@RequestParam(value = "qos") int qos, @RequestParam(value = "topic") String topic,@RequestParam(value = "pushMessage") String pushMessage) throws MqttException {
        return mqttPushClient.publish(qos, true, topic, pushMessage);
    }

    /**
     * 订阅某个主题
     *
     * @param topic 主题
     * @param qos   订阅方式
     */
    @GetMapping("/subscribe")
    public void subscribe(@RequestParam(value = "topic")String topic, @RequestParam(value = "qos",required = false) int qos) throws MqttException {
        mqttPushClient.subscribe(topic, qos);
    }

    /**
     * 重连
     */
    @GetMapping("/reconnect")
    public void reconnect() {
        mqttPushClient.reconnect();
    }

    /**
     * 保存广告应答对象<br/>
     *
     * @param advstatic
     * @throws Exception
     */
    @RequestMapping(value = "/save")
    public void save(AdvStatic advstatic) {
        pushCallbackService.save(advstatic);
    }

}
