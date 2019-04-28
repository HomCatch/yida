package com.xiaohe.etar.miniprogram.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * API统一返回结果状态码
 *
 * @author hzh
 * @date 2018/10/10
 */
public enum ResultCode {

    /*成功状态码 */
    SUCCESS(0, "SUCCESS"),
    /*一般异常*/
    ERROR(1, "未知异常，错误码：1"),
    NULL_POINTER_ERROR(2, "空指针异常"),
    EXCEL_IMPORT_FAIL(3, "EXCEL导入失败"),
    EXCEL_EXPORT_FAIL(4, "EXCEL导出失败"),
    EXCEL_INVALID_FORMAT(5, "EXCEL导入格式错误"),
    EXCEL_NULL_VALUE(6, "EXCEL导入内容为空"),

    BLUETOOTH_BINDIND_FAIL(7, "蓝牙绑定失败"),
    PHONE_CODE_FAIL(8, "验证码错误，请重输"),
    PHONE_CODE_IS_NULL(9, "验证码为空"),
    PHONE_IS_NULL(10, "手机号码为空"),
    PHONE_CODE_OUT_TIME(11, "验证码已过期，请重试"),
    PHONE_IS_REPEAT(12, "手机号码重复"),
    PHONE_SEND_MASSAGE_FAIL(13, "短信下发失败，请稍后重试"),
    /*登录相关*/
    LOGIN_NOT(100, "未登录"),
    NO_AUTH(101, "没有权限"),
    /*文件相关*/
    IMG_IS_EMPTY(1001, "所传图片文件为空"),
    IMG_FORMAT_ERROR(1002, "文件格式错误"),
    IMG_SAVE_ERROR(1003, "文件保存异常"),
    IMG_SIZE_ERROR(1004, "文件太大"),
    /*设备相关：10001-19999*/
    FILTER_INFO_NOT_EXIST(10001, "滤芯编号不存在"),
    FILTER_INFO_CODE_EXIST(10002, "滤芯编号已存在"),
    FILTER_INFO_ID_NULL(10003, "滤芯编号为空"),
    FILTER_INFO_FORMAT_ERROR(10004, "滤芯编号格式错误"),
    FILTER_INFO_IS_USER(10005, "滤芯编号已使用"),
    FILTER_INFO_USER_BY_SELF(10006, "该设备正在使用此滤芯"),
    /*设备相关：10001-19999*/
    DEV_NOT_EXIST(10001, "设备不存在"),
    DEV_IS_BINDING(10002, "设备已被绑定"),
    DEV_CID_EXIST(10003, "设备CID已存在,不能新增"),
    DEV_ID_NULL(10004, "设备ID为空"),
    DEV_LENGTH_ERROR(10006, "请输入15位设备CID！"),
    DEV_FORMAT_ERROR(10007, "设备CID格式错误"),
    DEV_CID_ERROR(10011, "CID无效"),
    DEV_NOT_NUM(10008, "设备CID不是纯数字"),
    DEV_UNBIND_FAIL(10009, "设备解绑失败"),
    DEV_CID_CANNOT_NULL(10010, "设备CID不能为空"),
    DEV_NOT_ONLINE(10011, "设备不在线"),
    /* 用户错误：20001-29999*/
    USER_NOT_LOGGED_IN(20001, "用户未登录"),
    USER_LOGIN_ERROR(20002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_HAS_EXISTED(20005, "用户已存在"),
    USER_CODE_ERROR(20006, "用户CODE不合法"),
    USER_ID_NULL(20007, "用户ID为空"),
    USER_BIND_PHONE(20008, "手机号已被绑定，请重试"),
    USER_UNBIND_PHONE(20009, "未绑定手机号"),
    USER_OPENID_NULL(20010, "微信用户OPENID为空"),

    /*数据库异常，30001-29999*/
    DB_ERROR(30001, "数据库操作异常"),
    /*mqtt相关，40001-49999*/
    MQTT_CANT_SEND_CATIVE(40000, "激活失败"),
    MQTT_AD_SEND_FAIL(40001, "广告下发失败,请稍后重试！"),
    /*banner相关，50001-59999*/
    BANNER_NAME_EXISTS(50001, "Banner名称已存在"),
    BANNER_NOT_EXISTS(50002, "Banner名称不存在"),
    BANNER_IDS_IS_NULL(50003, "批量操作的ID为空"),
    BANNER_ISSUE_FAIL(50004, "Banner下发失败"),
    /*滤芯相关，60001-69999*/
    FILTER_NAME_EXISTS(60001, "滤芯广告名称已存在"),
    FILTER_NOT_EXISTS(60002, "滤芯广告名称不存在"),
    FILTER_IDS_IS_NULL(60003, "批量操作的ID为空"),
    /*待机广告相关，70001-79999*/
    AD_NAME_EXISTS(70001, "广告名称已存在"),
    AD_NOT_EXISTS(70002, "广告名称不存在"),
    AD_IDS_IS_NULL(70003, "批量操作的ID为空"),
    ADS_IS_NULL(70004, "查询结果集为空"),
    /*用户资料相关，80001-89999*/
    USER_INFO_NAME_EXISTS(70001, "用户名称已存在"),
    USER_INFO_PHONE_EXISTS(70002, "手机号码已存在"),
    USER_INFO_ID_IS_NULL(70003, "用户资料的ID为空"),
    USER_INFO_IS_NULL(70004, "查询结果集为空");

    private Integer code;

    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * 校验重复的code值
     */
    public static void main(String[] args) {
        ResultCode[] apiResultCodes = ResultCode.values();
        List<Integer> codeList = new ArrayList<>();
        for (ResultCode apiResultCode : apiResultCodes) {
            if (codeList.contains(apiResultCode.code)) {
                System.out.println(apiResultCode.code);
            } else {
                codeList.add(apiResultCode.code());
            }
        }
    }

}
