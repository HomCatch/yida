package com.xiaohe.etar.miniprogram.modules.inletfilter.service;

import com.xiaohe.etar.miniprogram.base.IBaseService;
import entity.common.entity.BatchReqVo;
import entity.inletfilter.InletFilter;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

/**
 * 滤芯 InletFilter接口
 *
 * @author hzh
 * @since 2018-10-15
 */

public interface InletFilterService extends IBaseService<InletFilter, Integer> {
    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 每页个数
     * @param name     要查询的banner名字
     * @return 查询的页面对象
     */
    Page<InletFilter> findByPage(int page, int pageSize, String name);

    /**
     * 通过banner名字判断是否存在
     *
     * @param name banner名字
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * banner 上架或下架
     *
     * @param filerId filerId
     * @param state   上下架状态
     */
    @Transactional(rollbackFor = Exception.class)
    void shelves(Integer filerId, Integer state);

    /**
     * 修改对象
     *
     * @param inletFilter 修改的对象
     * @return 0为失败 0xff为其他异常 1位成功
     * @throws ConstraintViolationException 方法名字不能重复异常
     */
    int updateAdvertising(InletFilter inletFilter);

    /**
     * 批量删除
     *
     * @param batchReqVo 要删除的id集合
     */
    int delBatch(BatchReqVo batchReqVo);

    /**
     * 得到上架的Advertising
     *
     * @param state 上架
     * @return 查询结果
     */
    InletFilter findByState(Integer state);
}


