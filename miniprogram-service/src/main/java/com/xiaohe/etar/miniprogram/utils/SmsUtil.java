package com.xiaohe.etar.miniprogram.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import entity.common.entity.Result;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 腾讯平台短信
 *
 * @author hzh
 * @version 1.0
 * @date 2019/1/10 18:15
 */
public class SmsUtil {
    private static Logger log = LoggerFactory.getLogger(SmsUtil.class);
    /**
     * 短信应用SDK AppID
     */
    private static int APP_ID = 1400179427;

    /**
     * 短信应用SDK AppKey
     */
    private static String APP_KEY = "d9191bfbb1cad521c465cc7621984f9e";

    /**
     * 短信模板ID，需要在短信应用中申请
     * templateId7839对应的内容是"您的验证码是: {1}"
     * NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
     */
    private static int TEMPLATE_ID = 264315;

    /**
     * 签名
     * NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`
     */
    private static String SMS_SIGN = "宜达商城";

    public static Result sendMsg(String phone, String nonceStr) {
        Result result = new Result();
        try {
            String[] params = {nonceStr, "30"};
            SmsSingleSender singleSender = new SmsSingleSender(APP_ID, APP_KEY);
            // 签名参数未提供或者为空时，会使用默认签名发送短信
            SmsSingleSenderResult sendWithParam = singleSender.sendWithParam("86", phone,
                    TEMPLATE_ID, params, null, "", "");
            log.info("腾讯短信下发结果:{}", sendWithParam.errMsg);
            result.error(sendWithParam.result, sendWithParam.errMsg);
        } catch (HTTPException | JSONException | IOException e) {
            // HTTP响应码错误
            log.info("腾讯短信下发失败");
        }
        return result;
    }


    public static void main(String[] args) {
        String nonceStr = RandomCode.getNonceStr();
        System.out.println(nonceStr);
        sendMsg("13686498585", nonceStr);
    }


}
