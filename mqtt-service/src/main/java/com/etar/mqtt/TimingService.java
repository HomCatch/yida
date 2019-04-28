package com.etar.mqtt;

import com.etar.utils.MqttUtil;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author hzh
 * @date 2018/10/25
 */
@Component
@EnableScheduling
public class TimingService {

    private static Logger log = LoggerFactory.getLogger(TimingService.class);
    @Resource
    private MqttPushClient mqttPushClient;


    @Scheduled(cron = "0 0/50  * * * ?")
    public void mqttSendDate() {
        try {
            String timeMsg = MqttUtil.getTimeMsg();
            mqttPushClient.publish(1, "broadcast", timeMsg);
            log.info("下发时间broadcast：" + timeMsg);
        } catch (MqttException e) {
            if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECTING || e.getReasonCode() == MqttException.REASON_CODE_SERVER_CONNECT_ERROR
                    || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED || e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST
                    || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_DISCONNECT_PROHIBITED || e.getReasonCode() == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED) {
                    mqttPushClient.reconnect();
                log.info("下发时间时客户机未连接");
            } else {
                log.info("Mqtt异常");
            }

        }

    }

}
