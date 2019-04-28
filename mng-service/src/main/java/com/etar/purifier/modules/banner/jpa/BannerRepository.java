package com.etar.purifier.modules.banner.jpa;

import entity.banner.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 小程序banner jpa 接口
 *
 * @author hzh
 * @since 2018-10-15
 */

@Transactional(rollbackFor = Exception.class)
public interface BannerRepository extends JpaRepository<Banner, Integer> {
    /**
     * 通过条件查询
     *
     * @param specification 过滤条件
     * @param pageable      分页对象
     * @return 插件page对象
     */
    Page<Banner> findAll(Specification specification, Pageable pageable);


    /**
     * 通过banner名字判断是否存在
     *
     * @param name banner名字
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 通过name获取对象
     *
     * @param name banner名称
     * @return 查询结果
     */
    Banner findByName(String name);

    /**
     * banner 上架或下架
     *
     * @param bannerId bannerId
     * @param state    上下架状态
     */
    @Modifying
    @Query(value = "update  banner set state=?2 where id=?1", nativeQuery = true)
    void shelves(Integer bannerId, Integer state);

    /**
     * 复杂JPA操作  使用@Query()自定义sql语句  根据业务id UId去更新整个实体
     * 删除和更新操作，需要@Modifying和@Transactional注解的支持
     * <p>
     * 更新操作中 如果某个字段为null则不更新，否则更新【注意符号和空格位置】
     *
     * @param banner 传入实体，分别取实体字段进行set
     * @return 更新操作返回sql作用条数
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("update Banner b set " +
            "b.name = CASE WHEN :#{#banner.name} IS NULL THEN b.name ELSE :#{#banner.name} END ," +
            "b.imageUrl = CASE WHEN :#{#banner.imageUrl} IS NULL THEN b.imageUrl ELSE :#{#banner.imageUrl} END ," +
            "b.adUrl = CASE WHEN :#{#banner.adUrl} IS NULL THEN b.adUrl ELSE :#{#banner.adUrl} END ," +
            "b.state = CASE WHEN :#{#banner.state} IS NULL THEN b.state ELSE :#{#banner.state} END ," +
            "b.createTime = CASE WHEN :#{#banner.createTime} IS NULL THEN b.createTime ELSE :#{#banner.createTime} END ," +
            "b.updateTime =  CASE WHEN :#{#banner.updateTime} IS NULL THEN b.updateTime ELSE :#{#banner.updateTime} END " +
            "where b.id = :#{#banner.id}")
    int update(@Param("banner") Banner banner);

    /**
     * 得到上架的banner
     *
     * @param state 上架状态
     * @return 查询结果
     */
    Banner findByState(Integer state);
}