
package com.etar.purifier.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.etar.purifier.modules.sys.dao.SysRoleDao;
import com.etar.purifier.modules.sys.entity.SysRoleEntity;
import com.etar.purifier.modules.sys.service.SysRoleMenuService;
import com.etar.purifier.modules.sys.service.SysRoleService;
import com.etar.purifier.modules.sys.service.SysUserRoleService;
import com.etar.purifier.modules.sys.untils.Constant;
import com.etar.purifier.modules.sys.untils.PageUtils;
import com.etar.purifier.modules.sys.untils.Query;
import com.etar.purifier.modules.sys.untils.annotation.DataFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 角色
 * @Author wzq
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;


    @Override
    @DataFilter(subDept = true, user = false)
    public PageUtils queryPage(Map<String, Object> params) {
        String roleName = (String) params.get("roleName");

        Page<SysRoleEntity> page = this.selectPage(
                new Query<SysRoleEntity>(params).getPage(),
                new EntityWrapper<SysRoleEntity>()
                        .like(StringUtils.isNotBlank(roleName), "role_name", roleName)
                        .addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER))
        );
        int count = this.selectCount(new EntityWrapper<>());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setTotalCount(count);
        return pageUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysRoleEntity role) {
        role.setCreateTime(new Date());
        this.insert(role);

        //保存角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleEntity role) {
        this.updateAllColumnById(role);

        //更新角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] roleIds) {
        //删除角色
        this.deleteBatchIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        sysRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        sysUserRoleService.deleteBatch(roleIds);
    }

    @Override
    public List<SysRoleEntity> selectByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }


    @Override
    public SysRoleEntity selectByRoleName(String roleName) {
        return baseMapper.selectByRoleName(roleName);
    }
}
