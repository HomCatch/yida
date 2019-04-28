package com.etar.purifier.modules.userInfo.service;

import com.etar.purifier.base.IBaseService;
import entity.common.entity.BatchReqVo;
import entity.userinfo.QueryUserInfo;
import entity.userinfo.UserInfo;
import org.springframework.data.domain.Page;

/**
 * <p>
 * UserInfo接口
 * </p>
 *
 * @author gmq
 * @since 2019-01-09
 */

public interface UserInfoService extends IBaseService<UserInfo, Integer> {

    /**
     * 按条件查询
     *
     * @param page          页数
     * @param pageSize      数量
     * @param queryUserInfo 查询结果集
     * @return Page
     */
    Page<UserInfo> findAll(int page, int pageSize, QueryUserInfo queryUserInfo);

    /**
     * 通过手机号码，判断是否存在
     *
     * @param phone 手机号码
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 批量删除
     *
     * @param batchReqVo 要删除的id集合
     * @return 删除结果
     */
    int delBatch(BatchReqVo batchReqVo);

    /**
     * 通过手机获取
     *
     * @param phone 手机号
     * @return 查询结果
     */
    UserInfo findByPhone(String phone);

}


