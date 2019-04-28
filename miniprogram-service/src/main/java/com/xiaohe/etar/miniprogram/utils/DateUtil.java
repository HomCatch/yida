package com.xiaohe.etar.miniprogram.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 日期转换工具类
 *
 * @author hzh
 * @date 2018/8/9
 */
public class DateUtil {

    private static String DEFAULT_SIMPLE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date DEFAULT_TIME = Objects.requireNonNull(DateUtil.parse("2019-01-01 00:00:00"));

    /**
     * 字符串转日期
     *
     * @param dateStr 日期字符串
     * @return 日期
     */
    private static Date strToDate(String dateStr) {
        DateFormat format = new SimpleDateFormat(DEFAULT_SIMPLE_FORMAT);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化日期
     *
     * @param date         日期
     * @param formatString 格式化
     */
    private static String format(Date date, String formatString) {
        SimpleDateFormat df = new SimpleDateFormat(formatString);
        return df.format(date);
    }

    /**
     * 格式化日期(使用默认格式)
     *
     * @param date 日期
     */
    public static String format(Date date) {
        return format(date, DEFAULT_SIMPLE_FORMAT);
    }


    /**
     * 转换成日期
     *
     * @param dateString 要转换的时间
     * @return 使劲
     */
    public static Date parse(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_SIMPLE_FORMAT);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 与当前时间比较，是否超过5分钟
     *
     * @param dbDate  要比较的时间
     * @param nowDate 当前时间
     * @return 表结果 在5分钟内返回真
     */
    public static boolean isInFiveMin(Date dbDate, Date nowDate) {
        if (dbDate == null || nowDate == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_SIMPLE_FORMAT);
        Date start = null;
        Date end = null;
        try {
            start = sdf.parse(format(dbDate));
            end = sdf.parse(format(nowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long cha = Objects.requireNonNull(end).getTime() - start.getTime();
        //说明小于5分钟
        return cha <= 300000 / 5;
    }


    public static void main(String[] args) {
        Date date = parse("2018-10-15 16:02:40");
        System.out.println(date);
        Date date1 = strToDate("2018-10-15 16:02:40");
        System.out.println(date1);
    }


}
