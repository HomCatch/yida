package com.etar.purifier.utils;

/**
 * @program: fangjia-sjdbc-read-write-springboot
 * @description:
 * @author: Gmq
 * @date: 2019-03-14 15:40
 **/
public class SwitchDbUtil {
    public static String userName = "admin";
    public static String passWord = "123456";
    public static int dbSource = 0;

    public static synchronized void  switchDb(int dbSource2){
        dbSource=dbSource2;
    }
}
