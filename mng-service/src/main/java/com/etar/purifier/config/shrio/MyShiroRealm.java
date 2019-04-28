package com.etar.purifier.config.shrio;

import com.etar.purifier.modules.sys.dao.SysMenuDao;
import com.etar.purifier.modules.sys.dao.SysUserDao;
import com.etar.purifier.modules.sys.entity.SysMenuEntity;
import com.etar.purifier.modules.sys.entity.SysUserEntity;
import com.etar.purifier.modules.sys.service.SysUserService;
import com.etar.purifier.modules.sys.untils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author jack
 */
public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private SysUserService userInfoService;


    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysMenuDao sysMenuDao;


    /**
     * 获取用于权限列表
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SysUserEntity user = (SysUserEntity) principals.getPrimaryPrincipal();

        Long userId = user.getUserId();

        List<String> permsList;

        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN) {
            List<SysMenuEntity> menuList = sysMenuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for (SysMenuEntity menu : menuList) {
                permsList.add(menu.getPerms());
            }
        } else {
            permsList = sysUserDao.queryAllPerms(userId);
        }

        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for (String perms : permsList) {
            if (StringUtils.isBlank(perms)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);

        return info;
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        // 获取账号
        String username = (String) token.getPrincipal();

        // 根据账号 获取用户信息
        SysUserEntity userInfo = userInfoService.findByUserName(username);

        if (userInfo == null) {
            return null;
        }

        // 将userinfo, credentials 传递下去，以便后续框架调用 CredentialsMatcher 来进行比对
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userInfo, userInfo.getPassword(), getName());

        return authenticationInfo;
    }

}