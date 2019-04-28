package com.xiaohe.etar.miniprogram.modules.users;

import com.xiaohe.etar.miniprogram.common.validation.XException;
import entity.user.User;
import entity.user.WxUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hzh
 * @version 1.0
 * @date 2019/3/21 18:20
 */
@FeignClient(value = "mng-service", fallback = UserFeignServiceImpl.class)
public interface UserFeignService {

    @PostMapping(value = "/yida/miniProgram/saveNickName")
    void saveNickName(@RequestBody WxUser wxUser) throws XException;

    @GetMapping(value = "/yida/miniProgram/findByOpenId")
    User findByOpenId(@RequestParam(value = "openId") String openId) throws XException;

    @GetMapping(value = "/yida/miniProgram/saveByUserInfo")
    User saveByUserInfo(@RequestParam(value = "openId") String openId) throws XException;

    @GetMapping(value = "/yida/miniProgram/findByPhone")
    User findByPhone(@RequestParam(value = "phone") String phone) throws XException;

    @GetMapping(value = "/yida/miniProgram/findById")
    User findById(@RequestParam(value = "id") int id) throws XException;

    @GetMapping(value = "/yida/miniProgram/save")
    User save(@RequestBody User user) throws XException;
}
