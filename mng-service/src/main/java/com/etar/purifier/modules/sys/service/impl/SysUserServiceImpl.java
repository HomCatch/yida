
package com.etar.purifier.modules.sys.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.etar.purifier.modules.sys.dao.SysUserDao;
import com.etar.purifier.modules.sys.entity.SysUserEntity;
import com.etar.purifier.modules.sys.service.SysUserRoleService;
import com.etar.purifier.modules.sys.service.SysUserService;
import com.etar.purifier.modules.sys.shiro.ShiroUtils;
import com.etar.purifier.modules.sys.untils.Constant;
import com.etar.purifier.modules.sys.untils.PageUtils;
import com.etar.purifier.modules.sys.untils.Query;
import com.etar.purifier.modules.sys.untils.annotation.DataFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 系统用户
 * @Author wzq
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    @DataFilter(subDept = true, user = false)
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("userName");

        Wrapper<SysUserEntity> wrapper = new EntityWrapper<SysUserEntity>()
                .like(StringUtils.isNotBlank(username), "user_name", username)
                .notIn("user_id", 1)
                .addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER));
        if (ShiroUtils.getUserId() != 1) {
            wrapper.in("user_id", ShiroUtils.getUserId().toString());
        }
        Page<SysUserEntity> page = this.selectPage(
                new Query<SysUserEntity>(params).getPage(),
                wrapper
        );
//        for (SysUserEntity sysUserEntity : page.getRecords()) {
//            List<Long> deviceIdByUserId = deviceService.findDeviceIdByUserId(sysUserEntity.getUserId());
//            //获取绑定设备数
//            sysUserEntity.setDevNum(deviceIdByUserId.size());
//        }

        Wrapper<SysUserEntity> entityWrapper = new EntityWrapper<SysUserEntity>()
                .notIn("user_id", 1);
        if (ShiroUtils.getUserId() != 1) {
            entityWrapper.in("user_id", ShiroUtils.getUserId().toString());
        }
        Integer total = baseMapper.selectCount(entityWrapper);
        page.setTotal(total);
        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(SysUserEntity user) {
        user.setCreateTime(new Date());
        //TODO 暂时去掉加盐
        //sha256加密
        /*
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setSalt(salt);
		user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
		*/

        //检查账户名是否存在
        SysUserEntity byUsername = this.findByUserName(user.getUserName());
        if (byUsername != null) {
            return 1;
        }
        this.insert(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SysUserEntity user) {
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(null);
        } else {
            //TODO 暂时去掉加盐
            //user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
        }
        SysUserEntity byUsername = this.findByUserName(user.getUserName());
        if (byUsername != null) {
            int i = byUsername.getUserId().compareTo(user.getUserId());
            if (i != 0) {
                return 1;
            }
        }
        this.updateById(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
        return 0;
    }

    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new EntityWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
    }

    @Override
    public SysUserEntity findByUserName(String username) {
        return baseMapper.findByUserName(username);
    }

    /**
     * 查询所有经销商（用户）
     *
     * @return
     */
    @Override
    public List<SysUserEntity> findAll() {
        return baseMapper.selectList(new EntityWrapper<>());
    }

}
