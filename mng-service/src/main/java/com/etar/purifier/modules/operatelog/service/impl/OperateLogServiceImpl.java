package com.etar.purifier.modules.operatelog.service.impl;

import com.etar.purifier.modules.loginlog.service.LoginLogService;
import com.etar.purifier.modules.operatelog.jpa.OperateLogRepository;
import com.etar.purifier.modules.operatelog.service.OperateLogService;
import entity.operatelog.OperateLog;
import entity.operatelog.QueryOperateLog;
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
 * OperateLog服务类
 * </p>
 *
 * @author gmq
 * @since 2018-12-25
 */

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private OperateLogRepository operateLogRepository;
    @Autowired
    private LoginLogService loginLogService;


    /**
     * 保存对象
     *
     * @param operateLog 对象
     *                   持久对象，或者对象集合
     */
    @Override
    public OperateLog save(OperateLog operateLog) {
        return operateLogRepository.save(operateLog);
    }


    @Override
    public void deleteAll(List<OperateLog> list) {
        operateLogRepository.deleteAll(list);

    }

    @Override
    public void delete(OperateLog operateLog) {
        operateLogRepository.delete(operateLog);

    }

    @Override
    public void deleteById(Integer integer) {
        operateLogRepository.deleteById(integer);
    }

    @Override
    public List<OperateLog> findList() {
        return operateLogRepository.findAll();
    }
    /**
     * 通过id集合删除对象
     *
     * @param integers id集合
     */
    @Override
    public int deleteInBatch(List<Integer> integers) {
        List<OperateLog> allById = findAllById(integers);
        if(!CollectionUtils.isEmpty(allById)){
            return 0;
        }
        deleteAll(allById);
        return 1;
    }

    /**
     * 通过id判断是否存在
     *
     * @param id
     */
    @Override
    public boolean existsById(Integer id) {
        return operateLogRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return operateLogRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return OperateLog对象
     */
    @Override
    public OperateLog findById(Integer id) {
        Optional<OperateLog> optional = operateLogRepository.findById(id);
        return optional.orElse(null);
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<LogOperate>对象
     */
    @Override
    public Page<OperateLog> findAll(int page, int pageSize) {
        Pageable pageable =  PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return operateLogRepository.findAll(pageable);
    }

    @Override
    public Page<OperateLog> findAll(int page, int pageSize, QueryOperateLog queryOperateLog) {
        if (queryOperateLog == null) {
            return findAll(page, pageSize);
        }
        //过滤自己的广告
        Pageable pageable =  PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        //查询条件构造
        Specification<OperateLog> spec = new Specification<OperateLog>() {
            @Override
            public Predicate toPredicate(Root<OperateLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(queryOperateLog.getUserName())) {
                    predicate.getExpressions().add(cb.like(root.get("userName").as(String.class), "%" + queryOperateLog.getUserName() + "%"));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getUserName())) {
                    predicate.getExpressions().add(cb.like(root.get("userName").as(String.class), "%" + queryOperateLog.getUserName() + "%"));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getOperateInfo())) {
                    predicate.getExpressions().add(cb.like(root.get("operateInfo").as(String.class), "%" + queryOperateLog.getOperateInfo() + "%"));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getReqUrl())) {
                    predicate.getExpressions().add(cb.like(root.get("reqUrl").as(String.class), "%" + queryOperateLog.getReqUrl() + "%"));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getReqType())) {
                    predicate.getExpressions().add(cb.like(root.get("reqType").as(String.class), "%" + queryOperateLog.getReqType() + "%"));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getReqParam())) {
                    predicate.getExpressions().add(cb.like(root.get("reqParam").as(String.class), "%" + queryOperateLog.getReqParam() + "%"));
                }
                if (queryOperateLog.getReqTime() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("reqTime").as(Integer.class), queryOperateLog.getReqTime()));
                }

                if (queryOperateLog.getStatus() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("status").as(Integer.class), queryOperateLog.getStatus()));
                }

                if (StringUtils.isNotBlank(queryOperateLog.getIp())) {
                    predicate.getExpressions().add(cb.like(root.get("ip").as(String.class), "%" + queryOperateLog.getIp() + "%"));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getUserAgent())) {
                    predicate.getExpressions().add(cb.like(root.get("userAgent").as(String.class), "%" + queryOperateLog.getUserAgent() + "%"));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getStartTime())) {
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createTime").as(String.class), queryOperateLog.getStartTime()));
                }
                if (StringUtils.isNotBlank(queryOperateLog.getEndTime())) {
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createTime").as(String.class), queryOperateLog.getEndTime()));
                }
                return predicate;
            }

        };
        return operateLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<OperateLog> findAllById(List<Integer> ids) {
        return operateLogRepository.findAllById(ids);
    }
}


