package com.etar.purifier.modules.users.controller;

import com.alibaba.fastjson.JSONObject;
import com.etar.purifier.modules.users.service.UserService;
import entity.user.User;
import entity.user.WxUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author hzh
 * @version 1.0
 * @date 2019/3/22 9:33
 */
@RestController
@RequestMapping(value = "/yida/miniProgram")
public class WxUserController {
    private final UserService userService;

    @Autowired
    public WxUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/saveNickName")
    public void saveNickName(@RequestBody WxUser wxUser) {
        userService.saveNickName(wxUser);
    }

    @GetMapping(value = "/findByOpenId")
    public User findByOpenId(@RequestParam(value = "openId") String openId) {
        return userService.findByOpenId(openId);
    }

    @GetMapping(value = "/saveByUserInfo")
    public User saveByUserInfo(@RequestParam(value = "openId") String openId) {
        return userService.saveByUserInfo(openId);
    }

    @GetMapping(value = "/findByPhone")
    public User findByPhone(@RequestParam(value = "phone") String phone) {
        return userService.findByPhone(phone);
    }


    @GetMapping(value = "/findById")
    public User findById(@RequestParam(value = "id") int id) {
        return userService.findById(id);
    }


    @GetMapping(value = "/save")
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

}
