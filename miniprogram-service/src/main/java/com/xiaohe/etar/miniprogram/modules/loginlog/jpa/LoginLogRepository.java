package com.xiaohe.etar.miniprogram.modules.loginlog.jpa;

import entity.loginlog.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 *  jpa 接口
 *
 * @author gmq
 * @since 2018-12-25
 */

@Transactional(rollbackFor = Exception.class)
public interface LoginLogRepository extends JpaRepository<LoginLog, Integer> {

    /**
     * 按条件查询方案
     * @param spec spec
     * @param pageable 分页
     * @return page
     */
    Page<LoginLog> findAll(Specification<LoginLog> spec, Pageable pageable);

}