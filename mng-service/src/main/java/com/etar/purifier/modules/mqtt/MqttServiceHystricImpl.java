package com.etar.purifier.modules.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @program: yida2
 * @description: MqttServiceHystric
 * @author: Gmq
 * @date: 2019-03-20 16:15
 **/
@Component
public class MqttServiceHystricImpl implements MqttService {
    private static Logger log = LoggerFactory.getLogger(MqttServiceHystricImpl.class);

    @Override
    public int publish(int qos, String topic, String pushMessage) throws MqttException {
        MqttException mqttException = new MqttException(500);
        if (212 == mqttException.getReasonCode()) {
            log.error("MQTT服务器异常,发布主题失败");
            throw mqttException;
        }
        return 0;
    }

    @Override
    public void subscribe(String topic, int qos) throws MqttException {
        MqttException mqttException = new MqttException(500);
        if (212 == mqttException.getReasonCode()) {
            log.error("MQTT服务器异常,订阅主题失败");
            throw mqttException;
        }
    }

    @Override
    public void reconnect() throws MqttException {
        MqttException mqttException = new MqttException(500);
        if (212 == mqttException.getReasonCode()) {
            log.error("MQTT服务器异常,重连失败");
            throw mqttException;
        }
    }
}
