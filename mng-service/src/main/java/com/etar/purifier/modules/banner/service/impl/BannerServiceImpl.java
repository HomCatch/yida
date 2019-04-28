package com.etar.purifier.modules.banner.service.impl;

import com.etar.purifier.modules.banner.jpa.BannerRepository;
import com.etar.purifier.modules.banner.service.BannerService;
import entity.common.entity.BatchReqVo;
import entity.banner.Banner;
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
 * 小程序banner Banner服务类
 *
 * @author hzh
 * @since 2018-10-15
 */

@Service
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;

    @Autowired
    public BannerServiceImpl(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    /**
     * 保存对象
     *
     * @param banner 对象
     *               持久对象，或者对象集合
     */
    @Override
    public Banner save(Banner banner) {
        return bannerRepository.save(banner);
    }

    /**
     * 删除对象
     *
     * @param banner 对象
     */
    @Override
    public void delete(Banner banner) {
        bannerRepository.delete(banner);
    }

    @Override
    public void deleteAll(List<Banner> list) {
        bannerRepository.deleteAll(list);
    }

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @Override
    public void deleteById(Integer id) {
        bannerRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Integer id) {
        return bannerRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return bannerRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return Banner对象
     */
    @Override
    public Banner findById(Integer id) {
        Optional<Banner> optional = bannerRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    @Override
    public List<Banner> findList() {
        return bannerRepository.findAll();
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<Banner>对象
     */
    @Override
    public Page<Banner> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return bannerRepository.findAll(pageable);
    }

    @Override
    public Page<Banner> findByPage(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        Specification specification = (Specification) (root, criteriaQuery, cb) -> {
            Predicate predicate = cb.conjunction();
            if (StringUtils.isNotBlank(name)) {
                predicate.getExpressions().add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
            }
            return predicate;
        };
        return bannerRepository.findAll(specification, pageable);
    }

    @Override
    public boolean existsByName(String name) {
        return bannerRepository.existsByName(name);
    }

    @Override
    public void shelves(Integer bannerId, Integer state) {
        //1代表要上架banner，则其余banner全部下架
        if (1 == state) {
            List<Banner> bannerList = findList();
            for (Banner banner : bannerList) {
                if (1 == banner.getState()) {
                    bannerRepository.shelves(banner.getId(), 0);
                }
            }
        }
        bannerRepository.shelves(bannerId, state);
    }

    @Override
    public int updateBanner(Banner banner) {
        //查找源数据
        Banner byNameBanner = bannerRepository.findByName(banner.getName());
        if (byNameBanner != null) {
            //如果两个id相等，则表示banner名称已存在数据库
            if (banner.getId().intValue() != byNameBanner.getId().intValue()) {
                return 0;
            }
        }
        banner.setUpdateTime(new Date());
        try {
            save(banner);
        } catch (Exception e) {
            return 0xff;
        }
        return 1;
    }


    @Override
    public void delBatch(BatchReqVo batchReqVo) {
        List<Banner> bannerList = bannerRepository.findAllById(batchReqVo.getIdList());
        if (!bannerList.isEmpty()) {
            bannerRepository.deleteInBatch(bannerList);
        }
    }

    @Override
    public Banner findByState(Integer state) {
        return bannerRepository.findByState(state);
    }
}


