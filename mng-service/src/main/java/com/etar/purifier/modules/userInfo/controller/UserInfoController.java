package com.etar.purifier.modules.userInfo.controller;

import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.dev.service.DeviceService;
import com.etar.purifier.modules.userInfo.service.UserInfoService;
import com.etar.purifier.modules.users.service.UserService;
import com.etar.purifier.utils.UserInfoUtil;
import entity.common.entity.BatchReqVo;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import entity.user.User;
import entity.userinfo.QueryUserInfo;
import entity.userinfo.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.ResultCode;

import java.util.List;

/**
 * UserInfoController层
 *
 * @author gmq
 * @since 2019-01-09
 */
@RestController
@RequestMapping(value = "yida/userInfos")
public class UserInfoController {
    private static Logger log = LoggerFactory.getLogger(UserInfoController.class);
    private final UserInfoService userInfoService;
    private final UserService userService;
    private final DeviceService deviceService;

    @Autowired
    public UserInfoController(UserInfoService userInfoService, UserService userService, DeviceService deviceService) {
        this.userInfoService = userInfoService;
        this.userService = userService;
        this.deviceService = deviceService;
    }

    /**
     * 保存对象
     *
     * @param userInfo 对象
     */
    @PostMapping
    @LogOperate(description = "新增用户信息")
    public Result save(@Validated @RequestBody UserInfo userInfo) {
        Result result = new Result();
        try {
            boolean existsByPhone = userInfoService.existsByPhone(userInfo.getPhone());
            if (existsByPhone) {
                return result.error(ResultCode.USER_INFO_PHONE_EXISTS);
            }
            userInfoService.save(userInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2001, "新增失败");
        }
        return result.ok();
    }

    /**
     * 更新
     *
     * @param userInfo 用户资料
     */
    @PutMapping(value = "/{id}")
    @LogOperate(description = "更新用户信息")
    public Result updateUserInfo(@Validated @RequestBody UserInfo userInfo, @PathVariable("id") Integer id) {
        Result result = new Result();
        try {
            boolean exists = userInfoService.existsById(id);
            if (!exists) {
                return result.error(2002, "修改失败");
            }
            UserInfo byPhone = userInfoService.findByPhone(userInfo.getPhone());
            if (byPhone != null) {
                if (!byPhone.getId().equals(id)) {
                    return result.error(ResultCode.PHONE_IS_REPEAT);
                }
            }
            userInfoService.save(userInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2002, "修改失败");
        }
        return result.ok();
    }


    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @DeleteMapping(value = "/{id}")
    @LogOperate(description = "删除用户信息")
    public Result deleteById(@PathVariable("id") Integer id) {
        Result result = new Result();
        try {
            userInfoService.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2003, "删除失败");
        }
        return result.ok();
    }

    /**
     * 批量删除
     *
     * @param batchReqVo id集合
     * @return 操作结果
     */
    @PostMapping(value = "/batch")
    @LogOperate(description = "批量删除用户信息")
    public Result delBatch(@Validated @RequestBody BatchReqVo batchReqVo) {
        log.info("进入批量删除广告接口");
        Result result = new Result();

        if (CollectionUtils.isEmpty(batchReqVo.getIdList())) {
            return result.error(ResultCode.USER_INFO_ID_IS_NULL);
        }
        try {
            int ret = userInfoService.delBatch(batchReqVo);
            switch (ret) {
                case 1:
                    result.ok();
                    break;
                case 2:
                    result.error(ResultCode.USER_INFO_IS_NULL);
                    break;
                default:
                    result.error(ResultCode.ERROR);
                    break;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 通过id查找对象
     *
     * @param id id
     * @return UserInfo 对象
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id") Integer id) {
        DataResult result = new DataResult();
        try {
            result.setDatas(userInfoService.findById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2004, "不存在");
        }
        return result.ok();
    }

    /**
     * 分页查询
     *
     * @return Page<UserInfo> 对象
     */
    @PostMapping(value = "/pages")
    public Result findByPage(@RequestBody QueryUserInfo queryUserInfo) {

        DataResult result = new DataResult();
        try {
            int page = queryUserInfo.getPage();
            int pageSize = queryUserInfo.getPageSize();
            String nickName = queryUserInfo.getNickName();
            List<User> users;
            //通过微信账号获取用户手机
            if (nickName != null) {
                users = userService.findByNickNameLike(nickName);
                if (!CollectionUtils.isEmpty(users)) {
                    queryUserInfo.setPhoneList(UserInfoUtil.getPhoneList(users));
                }
            }
            Page<UserInfo> all = userInfoService.findAll(page - 1, pageSize, queryUserInfo);
            List<UserInfo> content = all.getContent();
            //得到昵称、注册时间、绑定设备数
            for (UserInfo userInfo : content) {
                String phone = userInfo.getPhone();
                if (phone != null) {
                    User user = userService.findByPhone(phone);
                    if (user != null) {
                        Integer count = deviceService.countByUserId(user.getId());
                        userInfo.setBindNum(count);
                        userInfo.setNickName(user.getNickName());
                        userInfo.setRegTime(user.getRegTime());
                    }
                }
            }
            PageBean<UserInfo> pageBean = new PageBean<>();
            pageBean.setCurPage(page);
            pageBean.setItemCounts(all.getTotalElements());
            pageBean.setPageSize(pageSize);
            pageBean.setList(content);
            result.setDatas(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return result.error(2005, "查询出错");
        }
        return result.ok();
    }
}