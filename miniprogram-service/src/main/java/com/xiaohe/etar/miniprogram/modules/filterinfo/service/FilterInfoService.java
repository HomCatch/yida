package com.xiaohe.etar.miniprogram.modules.filterinfo.service;

import com.xiaohe.etar.miniprogram.base.IBaseService;
import entity.filterinfo.FilterInfo;
import org.springframework.data.domain.Page;

/**
 * FilterInfo接口
 *
 * @author gmq
 * @since 2018-12-03
 */

public interface FilterInfoService extends IBaseService<FilterInfo, Long> {


    /**
     * 通过滤芯编号查询是否存在
     *
     * @param filterCode 滤芯编码
     * @return 存在结果
     */
    boolean existsByFilterCode(String filterCode);

    /**
     * 通过滤芯编号查询
     *
     * @param filterCode 滤芯编号
     * @return FilterInfo
     */
    FilterInfo findByFilterCode(String filterCode);

    /**
     * 更新滤芯使用情况
     *
     * @param status     使用状态
     * @param filterCode 滤芯编码
     * @return 更新结果 int
     */
    int updateFilterCode(int status, String filterCode);

    /**
     * 检验滤芯编号
     *
     * @param filterCode 滤芯编号
     * @return 0、可以使用1、编码不存在2、已使用
     */
    int verifyFilterCode(String filterCode);

    /**
     * 查询
     *
     * @param page       第几页
     * @param pageSize   每页个数
     * @param filterCode 滤芯编码
     * @return 查询结果
     */
    Page<FilterInfo> findPage(Integer page, Integer pageSize, String filterCode);
}


