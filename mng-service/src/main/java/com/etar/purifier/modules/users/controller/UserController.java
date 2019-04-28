package com.etar.purifier.modules.users.controller;


import com.alibaba.fastjson.JSONObject;
import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.users.service.UserService;
import entity.common.entity.BatchReqVo;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import entity.user.User;
import entity.user.WxUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.ResultCode;

import java.util.List;

/**
 * UserController层
 *
 * @author hzh
 * @since 2018-10-09
 */
@RestController
@RequestMapping(value = "/yida/user")
public class UserController {
    private static Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 删除对象
     *
     * @param userId 用户id
     */
    @DeleteMapping(value = "/users/{userId}")
    @LogOperate(description = "删除用户")
    public Result delUser(@PathVariable("userId") Integer userId) {
        Result result = new Result();
        try {
            if (userId == null) {
                return result.error(ResultCode.USER_ID_NULL);
            }
            userService.delUserAndUnboundDev(userId);
            result.ok();
        } catch (Exception e) {
            result.error(ResultCode.DB_ERROR);
        }
        return result;
    }

    /**
     * 批量删除用户
     *
     * @param batchReqVo 用户id集合
     * @return 状态
     */
    @PostMapping(value = "/batch")
    @LogOperate(description = "批量删除用户")
    public Result deleteBatch(@Validated @RequestBody BatchReqVo batchReqVo) {
        Result result = new Result();
        try {
            List<Integer> idList = batchReqVo.getIdList();
            if (idList.isEmpty()) {
                result.error(ResultCode.USER_ID_NULL);
            }
            userService.delBatchAndUnboundDev(idList);
            result.ok();
        } catch (NullPointerException e) {
            log.info(e.getMessage());
            return result.error(ResultCode.NULL_POINTER_ERROR);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result;
    }

    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 页面大小
     * @return Page<User> 对象
     */
    @GetMapping(value = "/list")
    public Result findByPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "nickName", required = false, defaultValue = "") String nickName) {
        DataResult result = new DataResult();
        Page<User> all = userService.findPage(page - 1, pageSize, nickName);
        List<User> users = all.getContent();
        if (!users.isEmpty()) {
            userService.countBindDevByUserId(users);
        }
        PageBean<User> pageBean = new PageBean<>();
        pageBean.setCurPage(page);
        pageBean.setItemCounts(all.getTotalElements());
        pageBean.setPageSize(pageSize);
        pageBean.setList(users);
        result.ok();
        result.setDatas(pageBean);
        return result;
    }

}