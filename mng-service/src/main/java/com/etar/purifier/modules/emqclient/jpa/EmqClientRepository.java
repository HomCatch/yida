package com.etar.purifier.modules.emqclient.jpa;

import entity.emqclient.EmqClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 *  jpa 接口
 *
 * @author gmq
 * @since 2019-02-13
 */

@Transactional(rollbackFor = Exception.class)
public interface EmqClientRepository extends JpaRepository<EmqClient, Integer> {

    /**
     * 按条件查询方案
     * @param spec spec
     * @param pageable 分页
     * @return page
     */
    Page<EmqClient> findAll(Specification<EmqClient> spec, Pageable pageable);

}