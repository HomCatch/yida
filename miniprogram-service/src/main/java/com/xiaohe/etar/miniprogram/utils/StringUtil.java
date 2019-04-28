package com.xiaohe.etar.miniprogram.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String 工具类
 * @author  shuzheng on 2016/12/07.
 */
public class StringUtil {

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final String FORMAT = "0";
    private static final String FORMAT2 = ".00";
    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);

        str = sb.toString();
        str = str.substring(0, 1).toUpperCase() + str.substring(1);

        return str;
    }

    /**
     * 驼峰转下划线,效率比上面高
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线(简单写法，效率低于{@link #humpToLine(String)})
     * @param str
     * @return
     */
    public static String humpToLine2(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    /**
     * 首字母转小写
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 首字母转大写
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuffer()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * object转String
     * @param object
     * @return
     */
    public static String getString(Object object) {
        return getString(object, "");
    }

    public static String getString(Object object, String defaultValue) {
        if (null == object) {
            return defaultValue;
        }
        try {
            return object.toString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * object转Integer
     * @param object
     * @return
     */
    public static int getInt(Object object) {
        return getInt(object, -1);
    }

    public static int getInt(Object object, Integer defaultValue) {
        if (null == object) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * object转Boolean
     * @param object
     * @return
     */
    public static boolean getBoolean(Object object) {
        return getBoolean(object, false);
    }

    public static boolean getBoolean(Object object, Boolean defaultValue) {
        if (null == object) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 把浮点型正整数型字符串格式为两位小数
     * @param s 价格字符串
     * @return String
     */
    public static String formatPrice(String  s){
        int i1 = s.indexOf(".");
        int i2 = s.length() - 1;
        StringBuffer sb=new StringBuffer();
        if(i1!=-1){
            if((i2-i1)>TWO){
                sb=sb.append(s.substring(0,i1+THREE));
            }else if((i2-i1)==TWO){
                sb.append(s);
            }else if((i2-i1)==ONE){
                sb.append(s+FORMAT);
            }
        }else{
            sb.append(s+FORMAT2);
        }
        return sb.toString();
    }
    /**
     * String字符串转成List<Long>数据格式
     * String str = "1,2,3,4,5,6" -> List<Long> listLong [1,2,3,4,5,6];
     *
     * @param strArr
     * @return
     */
    public static List<Long> stringToLongList(String strArr) {
        return Arrays.stream(strArr.split(","))
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toList());
    }
    /**
     * String字符串转成List<Integer>数据格式
     *
     * @param strArr
     * @return
     */
    public static List<Integer> stringToIntegerList(String strArr) {
        return Arrays.stream(strArr.split(","))
                .map(s -> Integer.parseInt(s.trim()))
                .collect(Collectors.toList());
    }

    /**
     * 错误详情
     * @param e
     * @return
     */
    public static String errorInfo(Exception e){
        e.printStackTrace();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
