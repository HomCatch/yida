package com.xiaohe.etar.miniprogram.modules.filterinfo.jpa;

import entity.filterinfo.FilterInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * jpa 接口
 *
 * @author gmq
 * @since 2018-12-03
 */

@Transactional(rollbackFor = Exception.class)
public interface FilterInfoRepository extends JpaRepository<FilterInfo, Long> {
    /**
     * 通过滤芯编号查询是否存在
     *
     * @param filterCode 滤芯编号
     * @return 存在情况
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
     * 分页查询
     *
     * @param specification 查询条件对象
     * @param pageable      分页对象
     * @return page 分页对象
     */
    Page<FilterInfo> findAll(Specification specification, Pageable pageable);

    /**
     * 更新滤芯使用情况
     *
     * @param status     使用状态
     * @param filterCode 滤芯编码
     * @return 更新结果
     */
    @Modifying
    @Query(value = "update FilterInfo  set status=?1 where filterCode=?2")
    int updateFilterCode(int status, String filterCode);

    /**
     * id集合查询列表
     *
     * @param ids id集合
     * @return 结果
     */
    List<FilterInfo> findByIdInOrderByIdDesc(List<Long> ids);
}