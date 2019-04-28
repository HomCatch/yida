
package com.etar.purifier.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.etar.purifier.modules.sys.entity.SysRoleEntity;

import java.util.List;

/**
 * 角色管理
 */
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {
    /**
     * 通过userID查询角色
     *
     * @param userId 用戶id
     * @return 集合
     */
    List<SysRoleEntity> selectByUserId(Long userId);

    /**
     * 通過角色名查詢
     * @param roleName 角色名
     * @return 查询结果
     */
    SysRoleEntity selectByRoleName(String roleName);
}
