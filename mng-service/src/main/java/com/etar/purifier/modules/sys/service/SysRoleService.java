
package com.etar.purifier.modules.sys.service;


import com.baomidou.mybatisplus.service.IService;
import com.etar.purifier.modules.sys.entity.SysRoleEntity;
import com.etar.purifier.modules.sys.untils.PageUtils;

import java.util.List;
import java.util.Map;


/**
 * 角色
 * @Author wzq
 */
public interface SysRoleService extends IService<SysRoleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(SysRoleEntity role);

    void update(SysRoleEntity role);

    void deleteBatch(Long[] roleIds);

    List<SysRoleEntity> selectByUserId(Long userId);

    SysRoleEntity selectByRoleName(String roleName);

}
