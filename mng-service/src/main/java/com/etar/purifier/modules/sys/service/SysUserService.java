package com.etar.purifier.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.etar.purifier.modules.sys.entity.SysUserEntity;
import com.etar.purifier.modules.sys.untils.PageUtils;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 */
public interface SysUserService extends IService<SysUserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(Long userId);

    /**
     * 保存用户
     */
    int save(SysUserEntity user);

    /**
     * 修改用户
     */
    int update(SysUserEntity user);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param password    原密码
     * @param newPassword 新密码
     */
    boolean updatePassword(Long userId, String password, String newPassword);


    SysUserEntity findByUserName(String username);

    /**
     * 查询所有用户
     *
     * @return
     */
    List<SysUserEntity> findAll();

}
