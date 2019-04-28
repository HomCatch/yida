package com.etar.purifier.utils;

import entity.common.entity.Result;
import org.apache.commons.lang3.StringUtils;
import utils.ResultCode;

import java.util.regex.Pattern;

/**
 * @author hzh
 * @date 2018/10/16
 */
public class DevCodeUtil {
    /**
     * 是否是纯数字的正则
     */
    private static String REX = "^[-\\+]?[\\d]*$";

    /**
     * 校验devCode的合法性
     *
     * @param devCode 设备编码
     * @return 0、合法 1、不是纯数字 2、长度不是15 3、格式不是20001开头
     */
    public static int verifyDevCode(String devCode) {
        Pattern pattern = Pattern.compile(REX);
        if (StringUtils.isBlank(devCode)) {
            return 3;
        }
        if (!pattern.matcher(devCode).matches()) {
            return 1;
        }
        if (ConstantUtil.DEV_CODE_LENGTH != devCode.length()) {
            return 2;
        }

        return 0;
    }

    public static Result parseCode(int code) {
        Result result = new Result();
        switch (code) {
            case 0:
                result.ok();
                break;
            case 1:
                result.error(ResultCode.DEV_CID_ERROR);
                break;
            case 2:
                result.error(ResultCode.DEV_CID_ERROR);
                break;
            case 3:
                result.error(ResultCode.DEV_CID_ERROR);
                break;
            default:
                result.error(ResultCode.ERROR);
                break;
        }
        return result;
    }
}
