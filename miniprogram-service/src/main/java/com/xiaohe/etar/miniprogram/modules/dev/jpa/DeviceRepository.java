package com.xiaohe.etar.miniprogram.modules.dev.jpa;

import entity.dev.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * jpa 接口
 *
 * @author hzh
 * @since 2018-10-08
 */

@Transactional(rollbackFor = Exception.class)
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    /**
     * 复杂JPA操作  使用@Query()自定义sql语句  根据业务id UId去更新整个实体
     * 删除和更新操作，需要@Modifying和@Transactional注解的支持
     * <p>
     * 更新操作中 如果某个字段为null则不更新，否则更新【注意符号和空格位置】
     *
     * @param device 传入实体，分别取实体字段进行set
     * @return 更新操作返回sql作用条数
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("update Device b set " +
            "b.active = CASE WHEN :#{#device.active} IS NULL THEN b.active ELSE :#{#device.active} END ," +
            "b.bindTime = CASE WHEN :#{#device.bindTime} IS NULL THEN b.bindTime ELSE :#{#device.bindTime} END ," +
            "b.devCode = CASE WHEN :#{#device.devCode} IS NULL THEN b.devCode ELSE :#{#device.devCode} END ," +
            "b.inventoryTime = CASE WHEN :#{#device.inventoryTime} IS NULL THEN b.inventoryTime ELSE :#{#device.inventoryTime} END ," +
            "b.online = CASE WHEN :#{#device.online} IS NULL THEN b.online ELSE :#{#device.online} END ," +
            "b.status = CASE WHEN :#{#device.status} IS NULL THEN b.status ELSE :#{#device.status} END ," +
            "b.userId = CASE WHEN :#{#device.userId} IS NULL THEN b.userId ELSE :#{#device.userId} END ," +
            "b.filterLifeTime =  CASE WHEN :#{#device.filterLifeTime} IS NULL THEN b.filterLifeTime ELSE :#{#device.filterLifeTime} END " +
            "where b.id = :#{#device.id}")
    int update(@Param("device") Device device);

    /**
     * 通过devCode查询设备是否在线
     *
     * @param devCode 设备编码
     * @return 存在结果
     */
    boolean existsByDevCode(String devCode);

    /**
     * 通过设备编码查询对象
     *
     * @param devCode 设备编码
     * @return 查询对象
     */
    Device findByDevCode(String devCode);

    /**
     * 通过用户id获取设备绑定的列表
     *
     * @param userId 用户id
     * @return 集合
     */
    List<Device> findByUserId(Integer userId);

    /**
     * 通过条件查询
     *
     * @param specification 过滤条件
     * @param pageable      分页对象
     * @return 插件page对象
     */
    Page<Device> findAll(Specification specification, Pageable pageable);


    /**
     * 更新绑定（激活）状态
     *
     * @param status  激活状态
     * @param devCode 设备编码
     */
    @Modifying
    @Query("update Device u set u.status = ?1 where u.devCode= ?2")
    int updateBindStatus(Integer status, String devCode);


    /**
     * 更上线状态状态
     *
     * @param online  在线状态
     * @param devCode 设备编码
     */
    @Modifying
    @Query("update Device u set u.online = ?1 where u.devCode= ?2")
    int updateOnline(Integer online, String devCode);

    /**
     * 更改滤芯寿命
     *
     * @param filterLife 滤芯寿命
     * @param devCode    设备码
     */
    @Modifying
    @Query("update Device u set u.filterLifeTime = ?1 where u.devCode= ?2")
    void updateFilterLife(Integer filterLife, String devCode);

    /***
     *修改滤芯
     * @param filterLifeTime 滤芯
     * @param devCode  设备编码
     */
    @Modifying
    @Query("update Device u set u.filterLifeTime = ?1 where u.devCode= ?2")
    void changeFilter(Integer filterLifeTime, String devCode);

    /**
     * 统计用户设备数
     *
     * @param userId 用户id
     * @return 设备数
     */
    @Query(value = "select count(id) from device where user_id=?1 and status=1 ", nativeQuery = true)
    Integer countDevNumByUserId(Integer userId);

    /**
     * 获取用户绑定的设备列表（小程序）
     *
     * @param status 状态
     * @param userId 用户id
     */
    List<Device> findByStatusAndUserId(Integer status, Integer userId);
}