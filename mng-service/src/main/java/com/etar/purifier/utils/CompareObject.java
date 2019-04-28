package com.etar.purifier.utils;

import com.alibaba.fastjson.JSONObject;
import entity.banner.Banner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 比较两个对象不同值，并修改不同的对应值
 *
 * @author hzh
 * @date 2018/10/16
 */
public class CompareObject {
    private static Logger log = LoggerFactory.getLogger(CompareObject.class);

    /**
     * 修改对象变化的字段值
     */
    public static String modifyBeanContent(Object source, Object target) {
        StringBuilder modifyContent = new StringBuilder();
        if (null == source || null == target) {
            return "";
        }
        //取出source类
        Class<?> sourceClass = source.getClass();
        Field[] sourceFields = sourceClass.getDeclaredFields();
        for (Field srcField : sourceFields) {
            String fieldName = srcField.getName();
            if (fieldName.contains("id") || fieldName.contains("serialVersionUID")) {
                continue;
            }
            //获取srcField值
            String srcValue = getFieldValue(source, fieldName) == null ? "" : getFieldValue(source, fieldName).toString();
            //获取对应的targetField值
            String targetValue = getFieldValue(target, fieldName) == null ? "" : getFieldValue(target, fieldName).toString();
            //值为空则返回
            if (StringUtils.isEmpty(srcValue) && StringUtils.isEmpty(targetValue)) {
                continue;
            }
            //修改有改变的值
            if (!srcValue.equals(targetValue)) {
                try {
                    //开启能改变私有属性的值功能
                    srcField.setAccessible(true);
                    String typeName = srcField.getType().getName();
                    Method fieldSetMet = sourceClass.getMethod(parSetName(fieldName), srcField
                            .getType());
                    //改变值
                    if (typeName.contains("Integer") || typeName.contains("int")) {
                        fieldSetMet.invoke(target, Integer.valueOf(srcValue));
                    } else if (typeName.contains("Date")) {
                        //1 有值
                        Date srcDate = (Date) getFieldValue(source, fieldName);
                        //2 不一定有值
                        Date tarDate = null;
                        if (targetValue != null) {
                            tarDate = DateUtil.parse(targetValue);
                        }
                        if (tarDate != null) {
                            if (!tarDate.equals(srcDate)) {
                                fieldSetMet.invoke(target, srcDate);
                            }
                        } else {
                            fieldSetMet.invoke(target, srcDate);
                        }
                    } else if (typeName.contains("Float") || typeName.contains("float")) {
                        fieldSetMet.invoke(target, Float.valueOf(srcValue));
                    } else if (typeName.contains("String")) {
                        fieldSetMet.invoke(target, srcValue);
                    } else if (typeName.toLowerCase().contains("boolean")) {
                        fieldSetMet.invoke(target, Boolean.valueOf(srcValue));
                    } else {
                        log.info("没有这个对象属性");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    log.info("参数不合法");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    log.info("没有这个set方法");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    log.info("调用目标异常");
                }
                modifyContent.append(fieldName).append("由‘").append(targetValue).append("’修改为‘").append(srcValue).append("’;");
            }
        }
        return modifyContent.toString();
    }


    /**
     * 获取Obj对象的fieldName属性的值
     *
     * @param obj       对象
     * @param fieldName 字段名
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        Object fieldValue = null;
        if (null == obj) {
            return null;
        }
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (!methodName.startsWith("get")) {
                continue;
            }
            if (methodName.startsWith("get") && methodName.substring(3).toUpperCase().equals(fieldName.toUpperCase())) {
                try {
                    fieldValue = method.invoke(obj);
                } catch (Exception e) {
                    System.out.println("取值出错，方法名 " + methodName);
                }
            }
        }
        return fieldValue;
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName 字段名
     * @return String
     */
    private static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return "set" + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
    }

    public static void main(String[] args) {
        Banner banner = new Banner();
        banner.setName("sadj");
        banner.setImageUrl("sdddd.com");
        banner.setAdUrl("hhgg.com");
        banner.setState(1);
        Banner banner1 = new Banner();
        banner1.setName("aaaa");
        banner1.setImageUrl("sdddd.com");
        banner1.setAdUrl("hhhh.com");
        banner1.setState(1);
        System.out.println(modifyBeanContent(banner, banner1));
        System.out.println("banner:" + JSONObject.toJSONString(banner));
        System.out.println("banner1:" + JSONObject.toJSONString(banner1));
    }
}
