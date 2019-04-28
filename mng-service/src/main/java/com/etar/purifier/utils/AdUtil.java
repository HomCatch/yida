package com.etar.purifier.utils;

import com.etar.purifier.common.validation.XException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hzh
 * @version 1.0
 * @date 2019/3/18 16:58
 */
public class AdUtil {
    /**
     * 设备广告每行显示16个中文
     */
    private final static int STR_LENGTH = 16;


    public static void main(String[] args) {
        System.out.println(isReadableSolgan("——发——"));
    }

    public static boolean isReadableSolgan(String solgan) {
        //获取字符个数
        int length = solgan.length();
        //判断有几行满格的
        int line = length / STR_LENGTH;
        //将字符串按16个分行
        List<String> solganStrList = getStrList(solgan, STR_LENGTH);
        switch (line) {
            case 0:
                return true;
            case 1:
                return !isEvenEngChar(getEngCharNum(solganStrList.get(0)));
            case 2:
            case 3:
                if (isEvenEngChar(getEngCharNum(solganStrList.get(0)))) {
                    return false;
                }
                return !isEvenEngChar(getEngCharNum(solganStrList.get(1)));
            default:
                throw new XException("广告语字数错误");
        }
    }

    /**
     * 是否是奇数
     *
     * @param num 计算的数目
     * @return 奇数结果
     */
    private static boolean isEvenEngChar(int num) {
        return (num & 1) == 1;
    }

    /**
     * 获取英文字符传的个数
     *
     * @param str 要计算的字符串
     * @return 个数
     */
    private static int getEngCharNum(String str) {
        int m = 0;
        char arr[] = str.toCharArray();
        for (char c : arr) {
            // 英文字符
            if (c <= 0x00FF) {
                m = m + 1;
            }
        }
        return m;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @return 字符串列表
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return 字符串列表
     */
    private static List<String> getStrList(String inputString, int length,
                                           int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str 原始字符串
     * @param f   开始位置
     * @param t   结束位置
     * @return String String
     */
    private static String substring(String str, int f, int t) {
        if (f > str.length()) {
            return null;
        }
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

}
