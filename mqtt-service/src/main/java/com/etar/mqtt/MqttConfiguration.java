package com.etar.mqtt;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * MqttConfiguration配置
 *
 * @author hzh
 * @version 1.0
 * @date 2019/1/17 13:41
 */
@Component
@Configuration
public class MqttConfiguration {
    @Value("${mqtt.host}")
    private String host;
    @Value("${mqtt.clientid}")
    private String clientid;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.timeout}")
    private int timeout;
    @Value("${mqtt.keepalive}")
    private int keepalive;

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(int keepalive) {
        this.keepalive = keepalive;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Bean
    public MqttPushClient getMqttPushClient() {
        MqttPushClient mqttPushClient = new MqttPushClient();
        mqttPushClient.connect(host, clientid, username, password, timeout, keepalive);
        try {
            //订阅通配主题
            mqttPushClient.subscribe("activate_ret/+");
        } catch (MqttException e) {
            System.out.println("订阅默认通配主题activate_ret/+失败");
        }
        return mqttPushClient;
    }
}

