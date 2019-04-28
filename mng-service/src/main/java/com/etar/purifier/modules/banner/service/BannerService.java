package com.etar.purifier.modules.banner.service;

import com.etar.purifier.base.IBaseService;
import entity.common.entity.BatchReqVo;
import entity.banner.Banner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

/**
 * <p>
 * 小程序banner Banner接口
 * </p>
 *
 * @author hzh
 * @since 2018-10-15
 */

public interface BannerService extends IBaseService<Banner, Integer> {
    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 每页个数
     * @param name     要查询的banner名字
     * @return 查询的页面对象
     */
    Page<Banner> findByPage(int page, int pageSize, String name);

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
     * @param bannerId bannerId
     * @param state    上下架状态
     */
    @Transactional(rollbackFor = Exception.class)
    void shelves(Integer bannerId, Integer state);

    /**
     * 修改对象
     *
     * @param banner 修改的对象
     * @return 0为失败 0xff为其他异常 1位成功
     * @throws ConstraintViolationException 方法名字不能重复异常
     */
    int updateBanner(Banner banner);

    /**
     * 批量删除
     *
     * @param batchReqVo 要删除的id集合
     */
    void delBatch(BatchReqVo batchReqVo);

    /**
     * 得到上架的banner
     *
     * @param state 上架状态
     * @return 查询结果
     */
    Banner findByState(Integer state);
}


