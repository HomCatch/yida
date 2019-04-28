package com.etar.mqtt;


import com.etar.utils.ConstantUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * MqttPushClient连接配置
 *
 * @author hzh
 * @version 1.0
 * @date 2019/1/17 13:41
 */
@Component
public class MqttPushClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttPushClient.class);
    private static MqttClient client;
    @Resource
    MqttConfiguration mqttConfiguration;

    public static MqttClient getClient() {
        return client;
    }


    public static void setClient(MqttClient client) {
        MqttPushClient.client = client;
    }

    public void connect(String host, String clientID, String username, String password, int timeout, int keepalive) {
        MqttClient client;
        try {
            client = new MqttClient(host, clientID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(timeout);
            options.setKeepAliveInterval(keepalive);
            MqttPushClient.setClient(client);
            try {
                client.setCallback(new PushCallback());
                client.connect(options);
            } catch (MqttException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        } catch (MqttException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 发布，默认qos为0，非持久化
     *
     * @param topic       订阅主题
     * @param pushMessage 发送的消息
     */
    public int publish(int qos, String topic, String pushMessage) throws MqttException {
        return publish(qos, true, topic, pushMessage);
    }

    /**
     * 发布
     *
     * @param qos         订阅方式
     * @param retained    retained
     * @param topic       主题
     * @param pushMessage 消息
     */
    public int publish(int qos, boolean retained, String topic, String pushMessage) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        try {
            byte[] bytes = pushMessage.getBytes(ConstantUtil.GB2312);
            message.setPayload(bytes);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
        MqttTopic mTopic = MqttPushClient.getClient().getTopic(topic);
        if (null == mTopic) {
            LOGGER.error("topic not exist");
        }
        MqttDeliveryToken token;

        token = mTopic.publish(message);
        token.waitForCompletion();
        return token.getMessageId();

    }

    /**
     * 订阅某个主题，qos默认为0
     *
     * @param topic 主题
     */
    public void subscribe(String topic) throws MqttException {
        subscribe(topic, 0);
    }

    /**
     * 订阅某个主题
     *
     * @param topic 主题
     * @param qos   订阅方式
     */

    public void subscribe(String topic, int qos) throws MqttException {
        MqttPushClient.getClient().subscribe(topic, qos);
    }

    /**
     * 重连
     */
    public void reconnect() {
        connect(mqttConfiguration.getHost(), mqttConfiguration.getClientid(), mqttConfiguration.getUsername(), mqttConfiguration.getPassword()
                , mqttConfiguration.getTimeout(), mqttConfiguration.getKeepalive());

    }
}
