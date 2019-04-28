package com.etar.purifier.modules.emqclient.service.impl;


import com.etar.purifier.modules.emqclient.jpa.EmqClientRepository;
import com.etar.purifier.modules.emqclient.service.EmqClientService;
import entity.emqclient.EmqClient;
import entity.emqclient.QueryEmqClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * EmqClient服务类
 * </p>
 *
 * @author gmq
 * @since 2019-02-13
 */

@Service
public class EmqClientServiceImpl implements EmqClientService {

    @Autowired
    private EmqClientRepository emqClientRepository;

    /**
     * 保存对象
     *
     * @param emqClient 对象
     *                  持久对象，或者对象集合
     */
    @Override
    public EmqClient save(EmqClient emqClient) {
        return emqClientRepository.save(emqClient);
    }

    /**
     * 删除对象
     *
     * @param emqClient 对象
     */
    @Override
    public void delete(EmqClient emqClient) {
        emqClientRepository.delete(emqClient);
    }

    @Override
    public void deleteAll(List<EmqClient> list) {
        emqClientRepository.deleteAll(list);
    }

    @Override
    public void deleteById(Integer integer) {
        emqClientRepository.deleteById(integer);
    }

    @Override
    public List<EmqClient> findList() {
        return emqClientRepository.findAll();
    }

    /**
     * 通过id判断是否存在
     *
     * @param id
     */
    @Override
    public boolean existsById(Integer id) {
        return emqClientRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return emqClientRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return EmqClient对象
     */
    @Override
    public EmqClient findById(Integer id) {
        Optional<EmqClient> optional = emqClientRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<EmqClient>对象
     */
    @Override
    public Page<EmqClient> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return emqClientRepository.findAll(pageable);
    }

    @Override
    public Page<EmqClient> findAll(int page, int pageSize, QueryEmqClient queryEmqClient) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");

        //查询条件构造
        Specification<EmqClient> spec = new Specification<EmqClient>() {
            @Override
            public Predicate toPredicate(Root<EmqClient> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(queryEmqClient.getUserName())) {
                    predicate.getExpressions().add(cb.like(root.get("userName").as(String.class), "%" + queryEmqClient.getUserName() + "%"));
                }
                if (StringUtils.isNotBlank(queryEmqClient.getDevCode())) {
                    predicate.getExpressions().add(cb.equal(root.get("clientId").as(String.class), queryEmqClient.getDevCode()));
                }
                if (queryEmqClient.getAction() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("action").as(Integer.class), queryEmqClient.getAction()));
                }

                if (StringUtils.isNotBlank(queryEmqClient.getStartTime())) {
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createTime").as(String.class), queryEmqClient.getStartTime()));
                }
                if (StringUtils.isNotBlank(queryEmqClient.getEndTime())) {
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createTime").as(String.class), queryEmqClient.getEndTime()));
                }
                return predicate;
            }

        };
        return emqClientRepository.findAll(spec, pageable);
    }

    /**
     * 根据Id查询list
     *
     * @param ids id集合
     * @return list
     */
    @Override
    public List<EmqClient> findAllById(List<Integer> ids) {
        return emqClientRepository.findAllById(ids);
    }

}


