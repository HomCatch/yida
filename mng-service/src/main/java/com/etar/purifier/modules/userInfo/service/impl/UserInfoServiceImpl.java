package com.etar.purifier.modules.userInfo.service.impl;

import entity.common.entity.BatchReqVo;
import com.etar.purifier.modules.userInfo.jpa.UserInfoRepository;
import com.etar.purifier.modules.userInfo.service.UserInfoService;
import entity.userinfo.QueryUserInfo;
import entity.userinfo.UserInfo;
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
 * UserInfo服务类
 *
 * @author gmq
 * @since 2019-01-09
 */

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;

    @Autowired
    public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * 保存对象
     *
     * @param userInfo 对象
     *                 持久对象，或者对象集合
     */
    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    /**
     * 删除对象
     *
     * @param userInfo 对象
     */
    @Override
    public void delete(UserInfo userInfo) {
        userInfoRepository.delete(userInfo);
    }

    @Override
    public void deleteAll(List<UserInfo> list) {
        userInfoRepository.deleteAll(list);
    }

    @Override
    public void deleteById(Integer id) {
        userInfoRepository.deleteById(id);
    }

    @Override
    public List<UserInfo> findList() {
        return userInfoRepository.findAll();
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Integer id) {
        return userInfoRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return userInfoRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return UserInfo对象
     */
    @Override
    public UserInfo findById(Integer id) {
        Optional<UserInfo> optional = userInfoRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<UserInfo>对象
     */
    @Override
    public Page<UserInfo> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return userInfoRepository.findAll(pageable);
    }

    @Override
    public Page<UserInfo> findAll(int page, int pageSize, QueryUserInfo queryUserInfo) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        //查询条件构造
        Specification<UserInfo> spec = new Specification<UserInfo>() {
            @Override
            public Predicate toPredicate(Root<UserInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(queryUserInfo.getRealName())) {
                    predicate.getExpressions().add(cb.like(root.get("realName").as(String.class), "%" + queryUserInfo.getRealName() + "%"));
                }
                if (StringUtils.isNotBlank(queryUserInfo.getPhone())) {
                    predicate.getExpressions().add(cb.like(root.get("phone").as(String.class), "%" + queryUserInfo.getPhone() + "%"));
                }
                //微信账号查询的手机结果列表
                List<String> phoneList = queryUserInfo.getPhoneList();
                if (!CollectionUtils.isEmpty(phoneList)) {
                    CriteriaBuilder.In<Object> in = cb.in(root.get("phone").as(String.class));
                    for (String phone : phoneList) {
                        in.value(phone);
                    }
                    predicate.getExpressions().add(cb.and(in));
                }
                return predicate;
            }

        };
        return userInfoRepository.findAll(spec, pageable);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userInfoRepository.existsByPhone(phone);
    }

    @Override
    public int delBatch(BatchReqVo batchReqVo) {
        List<UserInfo> bannerList = userInfoRepository.findAllById(batchReqVo.getIdList());
        if (bannerList.isEmpty()) {
            return 2;
        }
        userInfoRepository.deleteInBatch(bannerList);
        return 1;
    }

    @Override
    public UserInfo findByPhone(String phone) {
        return userInfoRepository.findByPhone(phone);
    }
}


