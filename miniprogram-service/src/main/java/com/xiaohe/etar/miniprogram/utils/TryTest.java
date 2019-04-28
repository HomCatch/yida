package com.xiaohe.etar.miniprogram.utils;

/**
 * @author hzh
 * @version 1.0
 * @date 2019/3/25 9:27
 */
public class TryTest {

    public static void main(String[] args) {
        System.out.println(outStr(2));
//        System.out.println(outStr(2));
    }

    public static String outStr(int temp) {
        if (temp == 1) {
            System.out.println("直接返回");
            return null;
        } else if (temp == 2) {
            try {
                System.out.println("计算中");
                temp += 2;
                return temp + "";
            } catch (Exception e) {
                System.out.println("异常");
                e.printStackTrace();
            } finally {
                System.out.println("finally语句");
            }
        }
        return "this is test";
    }
}
