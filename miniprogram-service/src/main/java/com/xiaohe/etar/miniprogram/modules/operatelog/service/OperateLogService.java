package com.xiaohe.etar.miniprogram.modules.operatelog.service;

import com.xiaohe.etar.miniprogram.base.IBaseService;
import entity.operatelog.OperateLog;
import entity.operatelog.QueryOperateLog;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * <p>
 *  OperateLog接口
 * </p>
 *
 * @author gmq
 * @since 2018-12-25
 */

public interface OperateLogService  extends IBaseService<OperateLog,Integer> {

    /**
     * 按条件查询
     * @param page 页数
     * @param pageSize 数量
     * @param queryOperateLog 参数
     * @return Page
     */
    Page<OperateLog> findAll(int page, int pageSize, QueryOperateLog queryOperateLog);

    /**
     * 根据Id查询list
     * @param ids ID集合
     * @return List
     */
    List<OperateLog> findAllById(List<Integer> ids);

    /**
     * 批量删除
     * @param integers id集合
     * @return int
     */
    int deleteInBatch(List<Integer> integers);

}


