package com.xiaohe.etar.miniprogram.modules.users;

import com.xiaohe.etar.miniprogram.common.validation.XException;
import entity.user.User;
import entity.user.WxUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级
 *
 * @author hzh
 * @version 1.0
 * @date 2019/3/22 11:14
 */
@Component
public class UserFeignServiceImpl implements UserFeignService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserFeignServiceImpl.class);

    @Override
    public void saveNickName(WxUser wxUser) throws XException {
        LOGGER.info("网络异常，保存用户昵称失败，请稍后重试");
    }

    @Override
    public User findByOpenId(String openId) throws XException {
        LOGGER.info("网络异常，查找用户，请稍后重试");
        throw new XException("网络异常，查找用户，请稍后重试");
    }

    @Override
    public User saveByUserInfo(String openId) throws XException {
        LOGGER.info("网络异常，保存用户失败，请稍后重试");
        throw new XException("网络异常，保存用户失败，请稍后重试");
    }

    @Override
    public User findByPhone(String phone) throws XException {
        throw new XException("网络异常，查找用户失败，请稍后重试");
    }

    @Override
    public User findById(int id) throws XException {
        throw new XException("网络异常，查找用户失败，请稍后重试");
    }

    @Override
    public User save(User user) throws XException {
        throw new XException("网络异常，保存用户失败，请稍后重试");
    }
}
