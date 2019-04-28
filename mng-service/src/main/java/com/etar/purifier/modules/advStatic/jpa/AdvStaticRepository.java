package com.etar.purifier.modules.advStatic.jpa;

import entity.advstatic.AdvStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 *  jpa 接口
 *
 * @author wzq
 * @since 2019-01-21
 */

@Transactional(rollbackFor = Exception.class)
public interface AdvStaticRepository extends JpaRepository<AdvStatic, Integer> {
    /**
     * 通过条件查询
     *
     * @param specification 过滤条件
     * @param pageable      分页对象
     * @return 插件page对象
     */
    Page<AdvStatic> findAll(Specification specification, Pageable pageable);
}