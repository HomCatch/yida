package com.xiaohe.etar.miniprogram.modules.verificationCode.jpa;

import entity.verificationcode.VerificationCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 手机验证码 jpa 接口
 *
 * @author gmq
 * @since 2019-01-11
 */

@Transactional(rollbackFor = Exception.class)
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {

    /**
     * 按条件查询方案
     *
     * @param spec     spec
     * @param pageable 分页
     * @return page
     */
    Page<VerificationCode> findAll(Specification<VerificationCode> spec, Pageable pageable);

    /**
     * 通过手机号查询
     *
     * @param phone 手机
     * @return 结果
     */
    VerificationCode findByPhone(String phone);
}