package com.xiaohe.etar.miniprogram.modules.errorlog.service;

import com.xiaohe.etar.miniprogram.base.IBaseService;
import entity.errorlog.ErrorLog;
import entity.errorlog.QueryErrorLog;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * <p>
 *  ErrorLog接口
 * </p>
 *
 * @author gmq
 * @since 2018-12-25
 */

public interface ErrorLogService  extends IBaseService<ErrorLog,Integer> {

    /**
     * 按条件查询
     * @param page 页数
     * @param pageSize 数量
     * @param queryErrorLog 参数
     * @return Page page
     */
    Page<ErrorLog> findAll(int page, int pageSize, QueryErrorLog queryErrorLog);

    /**
     * 根据Id查询list
     * @param ids id集合
     * @return list
     */
    List<ErrorLog> findAllById(List<Integer> ids);

    /**
     * 批量删除
     * @param integers id集合
     * @return int
     */
    int deleteInBatch(List<Integer> integers);
}


