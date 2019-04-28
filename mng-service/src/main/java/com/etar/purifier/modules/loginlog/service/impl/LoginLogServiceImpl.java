package com.etar.purifier.modules.loginlog.service.impl;

import com.etar.purifier.modules.loginlog.jpa.LoginLogRepository;
import com.etar.purifier.modules.loginlog.service.LoginLogService;
import entity.loginlog.LoginLog;
import entity.loginlog.QueryLoginLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * LoginLog服务类
 * </p>
 *
 * @author gmq
 * @since 2018-12-25
 */

@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Autowired
    private LoginLogRepository loginLogRepository;


    /**
     * 保存对象
     *
     * @param loginLog 对象
     *                 持久对象，或者对象集合
     */
    @Override
    public LoginLog save(LoginLog loginLog) {
        return loginLogRepository.save(loginLog);
    }

    /**
     * 通过id集合删除对象
     *
     * @param integers id集合
     */
    @Override
    public int deleteInBatch(List<Integer> integers) {
        List<LoginLog> allById = findAllById(integers);
        if(!CollectionUtils.isEmpty(allById)){
            return 0;
        }
        deleteAll(allById);
        return 1;
    }

    @Override
    public void deleteAll(List<LoginLog> list) {
        loginLogRepository.deleteAll(list);
    }

    @Override
    public void delete(LoginLog loginLog) {
        loginLogRepository.delete(loginLog);
    }

    @Override
    public void deleteById(Integer integer) {
        loginLogRepository.deleteById(integer);
    }

    @Override
    public boolean existsById(Integer integer) {
        return loginLogRepository.existsById(integer);
    }

    @Override
    public List<LoginLog> findList() {
        return loginLogRepository.findAll();
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return loginLogRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return LoginLog对象
     */
    @Override
    public LoginLog findById(Integer id) {
        Optional<LoginLog> optional = loginLogRepository.findById(id);
        return optional.orElse(null);
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<LoginLog>对象
     */
    @Override
    public Page<LoginLog> findAll(int page, int pageSize) {
        Pageable pageable =  PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return loginLogRepository.findAll(pageable);
    }

    @Override
    public Page<LoginLog> findAll(int page, int pageSize, QueryLoginLog queryLoginLog) {
        if (queryLoginLog == null) {
            return findAll(page, pageSize);
        }
        //过滤自己的广告
        Pageable pageable =  PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        //查询条件构造
        Specification<LoginLog> spec = new Specification<LoginLog>() {
            @Override
            public Predicate toPredicate(Root<LoginLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(queryLoginLog.getUserName())) {
                    predicate.getExpressions().add(cb.like(root.get("userName").as(String.class), "%" + queryLoginLog.getUserName() + "%"));
                }
                if (queryLoginLog.getOperateType() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("operateType").as(Integer.class), queryLoginLog.getOperateType()));
                }

                if (queryLoginLog.getStatus() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("status").as(Integer.class), queryLoginLog.getStatus()));
                }

                if (StringUtils.isNotBlank(queryLoginLog.getIp())) {
                    predicate.getExpressions().add(cb.like(root.get("ip").as(String.class), "%" + queryLoginLog.getIp() + "%"));
                }
                if (StringUtils.isNotBlank(queryLoginLog.getUserAgent())) {
                    predicate.getExpressions().add(cb.like(root.get("userAgent").as(String.class), "%" + queryLoginLog.getUserAgent() + "%"));
                }
                if (StringUtils.isNotBlank(queryLoginLog.getStartTime())) {
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createTime").as(String.class), queryLoginLog.getStartTime()));
                }
                if (StringUtils.isNotBlank(queryLoginLog.getEndTime())) {
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createTime").as(String.class), queryLoginLog.getEndTime()));
                }
                return predicate;
            }

        };
        return loginLogRepository.findAll(spec, pageable);
    }

    /**
     * 根据Id查询list
     *
     * @param ids id集合
     * @return list
     */
    @Override
    public List<LoginLog> findAllById(List<Integer> ids) {
        return loginLogRepository.findAllById(ids);
    }

}


