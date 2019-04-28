package com.xiaohe.etar.miniprogram.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 作用：用于发送短信验证码 <br>
 * 使用场景：找回密码
 *
 * @author hzh
 * @date 2019-01-11
 */
public class RandomCode {
    /**
     * 数字
     */
    private static final String SYMBOLS = "0123456789";


    private static final Random RANDOM = new SecureRandom();

    public static void main(String[] args) {

        System.out.println(getNonceStr());
    }

    /**
     * 获取长度为 6 的随机数字
     *
     * @return 随机数字
     */
    public static String getNonceStr() {
        // 如果需要4位，那 new char[4] 即可，其他位数同理可得
        char[] nonceChars = new char[6];

        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }
}