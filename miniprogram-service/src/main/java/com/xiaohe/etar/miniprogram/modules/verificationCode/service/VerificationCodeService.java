package com.xiaohe.etar.miniprogram.modules.verificationCode.service;

import com.xiaohe.etar.miniprogram.base.IBaseService;
import entity.verificationcode.QueryVerificationCode;
import entity.verificationcode.VerificationCode;
import org.springframework.data.domain.Page;

/**
 * 手机验证码 VerificationCode接口
 *
 * @author gmq
 * @since 2019-01-11
 */

public interface VerificationCodeService extends IBaseService<VerificationCode, Integer> {

    /**
     * 按条件查询
     *
     * @param page                  页数
     * @param pageSize              数量
     * @param queryVerificationCode q
     * @return Page 分页对象
     */
    Page<VerificationCode> findAll(int page, int pageSize, QueryVerificationCode queryVerificationCode);

    /**
     * 通过手机号查询
     *
     * @param phone 手机
     * @return 结果
     */
    VerificationCode findByPhone(String phone);
}

