package com.etar.purifier.modules.advStatic.service;

import com.etar.purifier.modules.advStatic.jpa.AdvStaticRepository;
import entity.advstatic.AdvStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.Optional;


/**
 * <p>
 * AdvStatic服务类
 * </p>
 *
 * @author wzq
 * @since 2019-01-21
 */

@Service
public class AdvStaticService {

    @Autowired
    private AdvStaticRepository advstaticRepository;

    /**
     * 保存对象
     *
     * @param advstatic 持久对象，或者对象集合
     * @throws Exception
     */
    public void save(AdvStatic advstatic) {
        advstaticRepository.save(advstatic);
    }

    /**
     * 删除对象
     *
     * @param advstatic
     * @throws Exception
     */

    public void delete(AdvStatic advstatic) {
        advstaticRepository.delete(advstatic);
    }

    /**
     * 通过id删除对象
     *
     * @param id
     * @throws Exception
     */
    public void deleteById(Integer id) {
        advstaticRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id
     * @throws Exception
     */
    public boolean existsById(Integer id) {
        return advstaticRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     *
     * @throws Exception
     */
    public long count() {
        return advstaticRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return AdvStatic
     * @throws Exception
     */
    public AdvStatic findById(Integer id) {
        Optional<AdvStatic> optional = advstaticRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return Page<AdvStatic>
     * @throws Exception properties 字符串为需要排序的字段，可以传多个
     */
    public Page<AdvStatic> findAll(int page, int pageSize, String advName) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id");
        //查询条件构造
        Specification<AdvStatic> spec = new Specification<AdvStatic>() {
            @Override
            public Predicate toPredicate(Root<AdvStatic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path<String> name = root.get("advName");
                Predicate p1 = cb.like(name, "%" + advName + "%");

                return cb.and(p1);
            }
        };

        return advstaticRepository.findAll(spec, pageable);
    }
}


