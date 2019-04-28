package com.etar.purifier.utils;

/**
 * 常量池
 *
 * @author hzh
 * @date 2018/10/16
 */
public interface ConstantUtil {

    int DEV_CODE_LENGTH = 15;
    String DEV_START_STR = "20001";

    int STRING_LENGTH = 10;

    /**
     * 0待上架，审核通过
     */
    int AD_NO_STAY = 0;
    /**
     * 1上架
     */
    int AD_IS_STAY = 1;
    /**
     * 2待审核
     */
    int AD_TO_CHECK = 2;
    /**
     * 3审核失败
     */
    int AD_CHECK_FAIL = 3;
    /**
     * 点击下架，或者通过该审核
     */
    int CLICK_AD_UNSTAY = 0;
    /**
     * 点击上架
     */
    int CLICK_AD_STAY = 1;
    /**
     * 点击通过审核
     */
    int CLICK_AD_AUDIT = 2;
    /**
     * 点击审核失败
     */
    int CLICK_AD_UNAUDIT = 3;

    /**
     * 激活状态
     */
    int DEV_ACTIVE = 1;
    int DEV_UN_ACTIVE = 0;
    /**
     * mqtt 指令前缀
     */
    String MQTT_ADV_PREFIX_BROADCAST = "1,";
    String MQTT_ADV_PREFIX_ONLINE = "7,";
    String MQTT_ACTIVE_PREFIX = "1,1,";
    String MQTT_TIME_PREFIX = "2,";
    String MQTT_COMMA = ",";
    /**
     * MQTT连接异常
     */
    int MQTT_CONNECT_ERROR = 128;
    /**
     * GB2312编码
     */
    String GB2312 = "GB2312";
    /**
     * 腾讯短信返回ok
     */
    String MSG_OK = "OK";
    int BLUE_FILTER_CODE = 2;
    Integer ACTIVE = 1;
    Integer UN_ACTIVE = 0;
    Integer ONLINE = 1;
    Integer ACTIVE_FILTER = 100;
    /**
     * 硬件同步时间成功
     */
    String HARD_GET_TIME_SUCCESS = "1";
    /**
     * 硬件同步时间成功
     */
    String HARD_GET_TIME_FAIL = "2";
    /**
     * 激活成功
     */
    String MQTT_ACTIVE_SUCCESS = "1";
    /**
     * 解除绑定成功
     */
    String MQTT_UNBIND_SUCCESS = "2";
    /**
     * 开机返回
     */
    String MQTT_OPEN_SUCCESS = "3";
    /**
     * 滤芯寿命
     */
    String MQTT_FILTER_LIFE = "4,";
    /**
     * 硬件手动解绑
     */
    String MQTT_HARDWARE_UNBIND = "5";
    /**
     * 跟换滤芯
     */
    String MQTT_HARDWARE_CHANGE_FILTER = "6,";
    /**
     * 广告应答
     */
    String MQTT_HARDWARE_ADVRET = "broadcast_ret";
    /**
     * 订阅主题
     */
    String MQTT_RET_TOPIC = "activate_ret";
    /**
     * 手机号正则
     */
    String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";

    /**
     * null 这个字符串
     */
    String NULL_STR = "null";
    /**
     * devCode
     */
    String DEV_CODE = "devCode";
    /**
     * userId
     */
    String USERID = "userId";
    /**
     * 客户端上线action标志
     */
    String CLIENT_CONN = "client_connected";
    /**
     * 客户端下线action标志
     */
    String CLIENT_DISCONN = "client_disconnected";
}
