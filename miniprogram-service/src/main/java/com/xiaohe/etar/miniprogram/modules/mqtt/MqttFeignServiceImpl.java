package com.xiaohe.etar.miniprogram.modules.mqtt;

import com.xiaohe.etar.miniprogram.common.validation.XException;
import org.springframework.stereotype.Component;

/**
 * Mqtt服务降级
 *
 * @author hzh
 * @version 1.0
 * @date 2019/3/22 11:09
 */
@Component
public class MqttFeignServiceImpl implements MqttFeignService {
    @Override
    public int publish(int qos, String topic, String pushMessage) {
        throw new XException("网络异常，下发消息失败，请稍后重试！");
    }

    @Override
    public void subscribe(String topic, int qos) {
        throw new XException("网络异常，订阅主题失败，请稍后重试");
    }

    @Override
    public void reconnect() {
        throw new XException("网络异常，重连调用失败，请稍后重试");
    }
}
