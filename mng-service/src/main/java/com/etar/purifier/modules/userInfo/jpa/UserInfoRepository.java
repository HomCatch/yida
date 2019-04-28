package com.etar.purifier.modules.userInfo.jpa;

import entity.userinfo.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * jpa 接口
 *
 * @author gmq
 * @since 2019-01-09
 */

@Transactional(rollbackFor = Exception.class)
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    /**
     * 按条件查询方案
     *
     * @param spec     spec
     * @param pageable 分页
     * @return page
     */
    Page<UserInfo> findAll(Specification<UserInfo> spec, Pageable pageable);

    /**
     * 通过手机号码，判断是否存在
     *
     * @param phone 手机号码
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 通过手机获取
     *
     * @param phone 手机号
     * @return 查询结果
     */
    UserInfo findByPhone(String phone);

}