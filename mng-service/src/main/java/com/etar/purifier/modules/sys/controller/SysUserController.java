
package com.etar.purifier.modules.sys.controller;


import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.config.ValidatorUtils;
import com.etar.purifier.modules.sys.entity.SysUserEntity;
import com.etar.purifier.modules.sys.service.SysUserRoleService;
import com.etar.purifier.modules.sys.service.SysUserService;
import com.etar.purifier.modules.sys.shiro.ShiroUtils;
import com.etar.purifier.modules.sys.untils.AbstractAssert;
import com.etar.purifier.modules.sys.untils.PageUtils;
import com.etar.purifier.modules.sys.untils.R;
import com.etar.purifier.modules.sys.untils.group.UpdateGroup;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 * @Author wzq
 */
@RestController
@RequestMapping("/sys/user")
@Validated
public class SysUserController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 所有用户列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:user:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysUserService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取登录的用户信息
     */
    @RequestMapping("/info")
    public R info() {
        SysUserEntity user = getUser();

        return R.ok().put("user", user);
    }

    /**
     * 修改登录用户密码
     */

    @RequestMapping("/password")
    @LogOperate(description = "修改登录用户密码")
    public R password(String password, @NotBlank(message = "新密码不能为空") String newPassword) {

        //原密码
        password = ShiroUtils.sha256(password, getUser().getSalt());
        //新密码
        newPassword = ShiroUtils.sha256(newPassword, getUser().getSalt());

        //更新密码
        boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
        if (!flag) {
            return R.error("原密码不正确");
        }

        return R.ok();
    }

    /**
     * 用户信息
     */
    @RequestMapping("/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public R info(@PathVariable("userId") Long userId) {
        SysUserEntity user = sysUserService.selectById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);
        //获取用户所属的设备列表
//        List<Long> deviceIdList = deviceService.findDeviceIdByUserId(userId);


        return R.ok().put("user", user).put("password", user.getPassword());
    }

    /**
     * 保存用户
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:user:save")
    @LogOperate(description = "保存用户")
    public R save(@Validated @RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user);
        if (CollectionUtils.isEmpty(user.getRoleIdList())) {
            return R.error(3001, "未分配角色，请分配");
        }

        int ret = sysUserService.save(user);
        if (ret == 1) {
            return R.error(2001, "账号重复，请更换");
        }
        return R.ok();
    }

    /**
     * 修改用户
     */

    @RequestMapping("/update")
    @RequiresPermissions("sys:user:update")
    @LogOperate(description = "修改用户")
    public R update(@Validated @RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);
        if (CollectionUtils.isEmpty(user.getRoleIdList())) {
            return R.error(3001, "为分配角色，请分配");
        }
        int ret = sysUserService.update(user);
        if (ret == 1) {
            return R.error(2001, "账号重复，请更换");
        }
        return R.ok();
    }

    /**
     * 删除用户
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:user:delete")
    @LogOperate(description = "删除用户")
    public R delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            return R.error("系统管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, getUserId())) {
            return R.error("当前用户不能删除");
        }

        sysUserService.deleteBatchIds(Arrays.asList(userIds));
        return R.ok();
    }

}
