package com.xiaohe.etar.miniprogram.modules.advertising.service.impl;

import com.xiaohe.etar.miniprogram.modules.advertising.jpa.AdvertisingRepository;
import com.xiaohe.etar.miniprogram.modules.advertising.service.AdvertisingService;
import com.xiaohe.etar.miniprogram.utils.ConstantUtil;
import entity.adverstising.Advertising;
import entity.common.entity.BatchReqVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 待机广告 Advertising服务类
 *
 * @author hzh
 * @since 2018-10-15
 */

@Service
public class AdvertisingServiceImpl implements AdvertisingService {
    private final AdvertisingRepository advertisingRepository;

    @Autowired
    public AdvertisingServiceImpl(AdvertisingRepository advertisingRepository) {
        this.advertisingRepository = advertisingRepository;
    }

    /**
     * 保存对象
     *
     * @param advertising 对象
     *                    持久对象，或者对象集合
     */
    @Override
    public Advertising save(Advertising advertising) {
        return advertisingRepository.save(advertising);
    }

    /**
     * 删除对象
     *
     * @param advertising 对象
     */
    @Override
    public void delete(Advertising advertising) {
        advertisingRepository.delete(advertising);
    }

    @Override
    public void deleteAll(List<Advertising> list) {
        advertisingRepository.deleteAll(list);
    }

    /**
     * 通过id删除对象
     *
     * @param id 广告id
     */
    @Override
    public void deleteById(Integer id) {
        advertisingRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id 广告id
     */
    @Override
    public boolean existsById(Integer id) {
        return advertisingRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return advertisingRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return Advertising对象
     */
    @Override
    public Advertising findById(Integer id) {
        Optional<Advertising> optional = advertisingRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    @Override
    public List<Advertising> findList() {
        return advertisingRepository.findAll();
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<Advertising>对象
     */
    @Override
    public Page<Advertising> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return advertisingRepository.findAll(pageable);
    }

    @Override
    public Page<Advertising> findByPage(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        Specification specification = getSpecification(name);
        return advertisingRepository.findAll(specification, pageable);
    }

    private Specification getSpecification(String name) {
        return (Specification) (root, criteriaQuery, cb) -> {
            Predicate predicate = cb.conjunction();
            if (StringUtils.isNotBlank(name)) {
                predicate.getExpressions().add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
            }
            return predicate;
        };
    }

    @Override
    public boolean existsByName(String name) {
        return advertisingRepository.existsByName(name);
    }

    @Override
    public void shelves(Integer adId, Integer state) {
        //1代表要上架，则其余全部下架
        if (ConstantUtil.AD_IS_STAY == state) {
            List<Advertising> advertisingList = findList();
            for (Advertising advertising : advertisingList) {
                if (ConstantUtil.AD_IS_STAY == advertising.getState()) {
                    advertisingRepository.shelves(advertising.getId(), ConstantUtil.AD_NO_STAY);
                }
            }
        }
        advertisingRepository.shelves(adId, state);
    }

    @Override
    public int updateAdvertising(Advertising advertising) {
        //查找源数据
        Advertising byName = advertisingRepository.findByName(advertising.getName());
        if (byName != null) {
            //如果两个id相等，则表示名称已存在数据库
            if (advertising.getId().intValue() != byName.getId().intValue()) {
                return 0;
            }
        }
        advertising.setUpdateTime(new Date());
        try {
            //修改时，要重新审核
            advertising.setState(ConstantUtil.AD_TO_CHECK);
            save(advertising);
        } catch (Exception e) {
            return 0xff;
        }
        return 1;
    }

    @Override
    public int delBatch(BatchReqVo batchReqVo) {
        List<Advertising> bannerList = advertisingRepository.findAllById(batchReqVo.getIdList());
        if (bannerList.isEmpty()) {
            return 2;
        }
        advertisingRepository.deleteInBatch(bannerList);
        return 1;
    }

    @Override
    public Advertising findByState(Integer state) {
        return advertisingRepository.findByState(state);
    }
}


