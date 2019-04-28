package com.etar.purifier.utils;

import entity.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户资料工具类
 *
 * @author hzh
 * @version 1.0v
 * @date 2019/1/11 9:47
 */
public class UserInfoUtil {
    /**
     * 通过用户列表获取手机号码
     *
     * @param users 用户列表
     * @return 手机号码
     */
    public static List<String> getPhoneList(List<User> users) {
        List<String> phoneList = new ArrayList<>();
        for (User user : users) {
            String phone = user.getPhone();
            if (phone != null) {
                phoneList.add(phone);
            }
        }
        return phoneList;
    }

}
