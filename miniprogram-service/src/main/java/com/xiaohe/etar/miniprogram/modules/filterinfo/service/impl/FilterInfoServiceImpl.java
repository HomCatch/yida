package com.xiaohe.etar.miniprogram.modules.filterinfo.service.impl;

import com.xiaohe.etar.miniprogram.modules.filterinfo.jpa.FilterInfoRepository;
import com.xiaohe.etar.miniprogram.modules.filterinfo.service.FilterInfoService;
import entity.filterinfo.FilterInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * FilterInfo服务类
 * </p>
 *
 * @author gmq
 * @since 2018-12-03
 */

@Service
public class FilterInfoServiceImpl implements FilterInfoService {
    private static Logger log = LoggerFactory.getLogger(FilterInfoServiceImpl.class);
    private final FilterInfoRepository filterInfoRepository;
    @Resource
    private HttpServletResponse response;
    @Resource
    private ResourceLoader resourceLoader;

    @Autowired
    public FilterInfoServiceImpl(FilterInfoRepository filterInfoRepository) {
        this.filterInfoRepository = filterInfoRepository;
    }

    /**
     * 保存对象
     *
     * @param filterInfo 对象
     *                   持久对象，或者对象集合
     */
    @Override
    public FilterInfo save(FilterInfo filterInfo) {
        return filterInfoRepository.save(filterInfo);
    }

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @Override
    public void deleteById(Long id) {
        filterInfoRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Long id) {
        return filterInfoRepository.existsById(id);
    }


    @Override
    public FilterInfo findByFilterCode(String filterCode) {
        return filterInfoRepository.findByFilterCode(filterCode);
    }

    @Override
    public int updateFilterCode(int status, String filterCode) {
        return filterInfoRepository.updateFilterCode(status, filterCode);
    }

    @Override
    public int verifyFilterCode(String filterCode) {
        boolean exists = existsByFilterCode(filterCode);
        if (!exists) {
            return 1;
        }
        FilterInfo byFilterCode = findByFilterCode(filterCode);
        if (byFilterCode.getStatus() == 1) {
            return 2;
        }
        return 0;


    }

    @Override
    public Page<FilterInfo> findPage(Integer page, Integer pageSize, String filterCode) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(filterCode)) {
                    predicate.getExpressions().add(cb.like(root.get("filterCode").as(String.class), "%" + filterCode + "%"));
                }
                return predicate;
            }
        };
        return filterInfoRepository.findAll(specification, pageable);
    }

    /**
     * 通过id判断是否存在
     *
     * @param filterCode 滤芯编码
     */
    @Override
    public boolean existsByFilterCode(String filterCode) {
        return filterInfoRepository.existsByFilterCode(filterCode);
    }

    @Override
    public void deleteAll(List<FilterInfo> list) {
        filterInfoRepository.deleteInBatch(list);
    }

    @Override
    public void delete(FilterInfo filterInfo) {
        filterInfoRepository.delete(filterInfo);
    }

    @Override
    public List<FilterInfo> findList() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return filterInfoRepository.findAll(sort);
    }

    private List<FilterInfo> findByIdIn(List<Long> ids) {
        return filterInfoRepository.findByIdInOrderByIdDesc(ids);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return filterInfoRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return FilterInfo对象
     */
    @Override
    public FilterInfo findById(Long id) {
        Optional<FilterInfo> byId = filterInfoRepository.findById(id);
        FilterInfo filterInfo = null;
        if (byId.isPresent()) {
            filterInfo = byId.get();
        }
        return filterInfo;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<FilterInfo>对象
     */
    @Override
    public Page<FilterInfo> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return filterInfoRepository.findAll(pageable);
    }


}


