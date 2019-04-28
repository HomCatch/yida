package com.xiaohe.etar.miniprogram.modules.verificationCode.service.impl;

import com.xiaohe.etar.miniprogram.modules.verificationCode.jpa.VerificationCodeRepository;
import com.xiaohe.etar.miniprogram.modules.verificationCode.service.VerificationCodeService;
import entity.verificationcode.QueryVerificationCode;
import entity.verificationcode.VerificationCode;
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
 * 手机验证码 VerificationCode服务类
 * </p>
 *
 * @author gmq
 * @since 2019-01-11
 */

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    /**
     * 保存对象
     *
     * @param verificationCode 对象
     *                         持久对象，或者对象集合
     */
    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        return verificationCodeRepository.save(verificationCode);
    }

    /**
     * 删除对象
     *
     * @param verificationCode 对象
     */
    @Override
    public void delete(VerificationCode verificationCode) {
        verificationCodeRepository.delete(verificationCode);
    }

    @Override
    public void deleteAll(List<VerificationCode> list) {
        verificationCodeRepository.deleteAll(list);
    }

    @Override
    public void deleteById(Integer id) {
        verificationCodeRepository.deleteById(id);
    }

    @Override
    public List<VerificationCode> findList() {
        return verificationCodeRepository.findAll();
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Integer id) {
        return verificationCodeRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return verificationCodeRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return VerificationCode对象
     */
    @Override
    public VerificationCode findById(Integer id) {
        Optional<VerificationCode> optional = verificationCodeRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<VerificationCode>对象
     */
    @Override
    public Page<VerificationCode> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return verificationCodeRepository.findAll(pageable);
    }

    @Override
    public Page<VerificationCode> findAll(int page, int pageSize, QueryVerificationCode queryVerificationCode) {
        //过滤自己的广告
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        //查询条件构造
        Specification<VerificationCode> spec = new Specification<VerificationCode>() {
            @Override
            public Predicate toPredicate(Root<VerificationCode> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(queryVerificationCode.getPhone())) {
                    predicate.getExpressions().add(cb.like(root.get("phone").as(String.class), "%" + queryVerificationCode.getPhone() + "%"));
                }
                if (StringUtils.isNotBlank(queryVerificationCode.getCode())) {
                    predicate.getExpressions().add(cb.like(root.get("code").as(String.class), "%" + queryVerificationCode.getCode() + "%"));
                }
                if (StringUtils.isNotBlank(queryVerificationCode.getStartTime())) {
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createTime").as(String.class), queryVerificationCode.getStartTime()));
                }
                if (StringUtils.isNotBlank(queryVerificationCode.getEndTime())) {
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createTime").as(String.class), queryVerificationCode.getEndTime()));
                }
                return predicate;
            }

        };
        return verificationCodeRepository.findAll(spec, pageable);
    }

    @Override
    public VerificationCode findByPhone(String phone) {
        return verificationCodeRepository.findByPhone(phone);
    }
}


