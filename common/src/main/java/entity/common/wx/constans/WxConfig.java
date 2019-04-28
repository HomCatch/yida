package entity.common.wx.constans;

/**
 * 微信相关常量
 *
 * @author hzh
 * @date 2018/10/8
 */
public interface WxConfig {

    /**
     * 微信小程序appId
     */
    //    String APP_ID = "wx7a27365cced50f99";
    //宜达
    String APP_ID = "wx4a4fbe2d9832cbb0";

    /**
     * 微信小程序密钥
     */
//    String APP_SECRET = "d9f3746a1f2f6388ec65a3f36617ff21";
    //宜达
    String APP_SECRET = "e47c7688553a5ade42bcb23f25d140e9";

    /**
     * 获取用户openId的url
     */
    String USER_OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APP_ID + "&secret=" + APP_SECRET + "&js_code=JSCODE&grant_type=authorization_code";
    /**
     * 获取token的url
     */
    String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID + "&secret=" + APP_SECRET;
    /**
     * 获取用户信息的url
     */
    String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
}
