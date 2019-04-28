package com.etar.purifier.modules.inletfilter.jpa;

import entity.inletfilter.InletFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 滤芯 jpa 接口
 *
 * @author hzh
 * @since 2018-10-15
 */

@Transactional(rollbackFor = Exception.class)
public interface InletFilterRepository extends JpaRepository<InletFilter, Integer> {

    /**
     * 通过条件查询
     *
     * @param specification 过滤条件
     * @param pageable      分页对象
     * @return 插件page对象
     */
    Page<InletFilter> findAll(Specification specification, Pageable pageable);


    /**
     * 通过名字判断是否存在
     *
     * @param name Advertising名字
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 通过name获取对象
     *
     * @param name Advertising名字
     * @return 查询结果
     */
    InletFilter findByName(String name);

    /**
     * Advertising 上架或下架
     *
     * @param adId  adId
     * @param state 上下架状态
     */
    @Modifying
    @Query(value = "update inlet_filter set state=?2 where id=?1", nativeQuery = true)
    void shelves(Integer adId, Integer state);

    /**
     * 复杂JPA操作  使用@Query()自定义sql语句  根据业务id UId去更新整个实体
     * 删除和更新操作，需要@Modifying和@Transactional注解的支持
     * <p>
     * 更新操作中 如果某个字段为null则不更新，否则更新【注意符号和空格位置】
     *
     * @param inletFilter 传入实体，分别取实体字段进行set
     * @return 更新操作返回sql作用条数
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("update InletFilter b set " +
            "b.name = CASE WHEN :#{#inletFilter.name} IS NULL THEN b.name ELSE :#{#inletFilter.name} END ," +
            "b.url = CASE WHEN :#{#inletFilter.url} IS NULL THEN b.url ELSE :#{#inletFilter.url} END ," +
            "b.state = CASE WHEN :#{#inletFilter.state} IS NULL THEN b.state ELSE :#{#inletFilter.state} END ," +
            "b.createTime = CASE WHEN :#{#inletFilter.createTime} IS NULL THEN b.createTime ELSE :#{#inletFilter.createTime} END ," +
            "b.updateTime =  CASE WHEN :#{#inletFilter.updateTime} IS NULL THEN b.updateTime ELSE :#{#inletFilter.updateTime} END " +
            "where b.id = :#{#inletFilter.id}")
    int update(@Param("inletFilter") InletFilter inletFilter);

    /**
     * 得到上架的Advertising
     *
     * @param state 上架状态
     * @return 查询结果
     */
    InletFilter findByState(Integer state);
}