package com.etar.purifier.modules.inletfilter.service.impl;

import entity.common.entity.BatchReqVo;
import com.etar.purifier.modules.inletfilter.jpa.InletFilterRepository;
import com.etar.purifier.modules.inletfilter.service.InletFilterService;
import entity.inletfilter.InletFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 滤芯 InletFilter服务类
 *
 * @author hzh
 * @since 2018-10-15
 */

@Service
public class InletFilterServiceImpl implements InletFilterService {

    private static Logger log = LoggerFactory.getLogger(InletFilterServiceImpl.class);

    private final InletFilterRepository inletFilterRepository;

    @Autowired
    public InletFilterServiceImpl(InletFilterRepository inletFilterRepository) {
        this.inletFilterRepository = inletFilterRepository;
    }

    /**
     * 保存对象
     *
     * @param inletFilter 对象
     *                    持久对象，或者对象集合
     */
    @Override
    public InletFilter save(InletFilter inletFilter) {
        return inletFilterRepository.save(inletFilter);
    }

    /**
     * 删除对象
     *
     * @param inletFilter 对象
     */
    @Override
    public void delete(InletFilter inletFilter) {
        inletFilterRepository.delete(inletFilter);
    }

    @Override
    public void deleteAll(List<InletFilter> list) {
        inletFilterRepository.deleteAll(list);
    }

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @Override
    public void deleteById(Integer id) {
        inletFilterRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Integer id) {
        return inletFilterRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return inletFilterRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return InletFilter对象
     */
    @Override
    public InletFilter findById(Integer id) {
        Optional<InletFilter> optional = inletFilterRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    @Override
    public List<InletFilter> findList() {
        return inletFilterRepository.findAll();
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<InletFilter>对象
     */
    @Override
    public Page<InletFilter> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return inletFilterRepository.findAll(pageable);
    }

    @Override
    public Page<InletFilter> findByPage(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(name)) {
                    predicate.getExpressions().add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
                }
                return predicate;
            }
        };
        return inletFilterRepository.findAll(specification, pageable);
    }

    @Override
    public boolean existsByName(String name) {
        return inletFilterRepository.existsByName(name);
    }

    @Override
    public void shelves(Integer adId, Integer state) {
        //1代表要上架，则其余全部下架
        if (1 == state) {
            List<InletFilter> inletFilterList = findList();
            for (InletFilter inletFilter : inletFilterList) {
                if (1 == inletFilter.getState()) {
                    inletFilterRepository.shelves(inletFilter.getId(), 0);
                }
            }
        }
        inletFilterRepository.shelves(adId, state);

    }

    @Override
    public int updateAdvertising(InletFilter inletFilter) {
        //查找源数据
        InletFilter byIdAd = findById(inletFilter.getId());
        InletFilter byName = inletFilterRepository.findByName(inletFilter.getName());
        if (byName != null) {
            //如果两个id相等，则表示名称已存在数据库
            if (byIdAd.getId().intValue() != byName.getId().intValue()) {
                return 0;
            }
        }
        inletFilter.setUpdateTime(new Date());
        try {
            save(inletFilter);
        } catch (Exception e) {
            return 0xff;
        }
        return 1;
    }

    @Override
    public int delBatch(BatchReqVo batchReqVo) {
        List<InletFilter> bannerList = inletFilterRepository.findAllById(batchReqVo.getIdList());
        if (bannerList.isEmpty()) {
            return 2;
        }
        inletFilterRepository.deleteInBatch(bannerList);
        return 1;
    }

    @Override
    public InletFilter findByState(Integer state) {
        return inletFilterRepository.findByState(state);
    }
}


