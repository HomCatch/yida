package com.xiaohe.etar.miniprogram.modules.dev.service;

import com.xiaohe.etar.miniprogram.base.IBaseService;
import entity.dev.Device;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * Device接口
 * </p>
 *
 * @author hzh
 * @since 2018-10-08
 */

public interface DeviceService extends IBaseService<Device, Integer> {
    /**
     * 通过devCode查询设备是否在线
     *
     * @param devCode 设备编码
     * @return 存在结果
     */
    boolean existsByDevCode(String devCode);

    /**
     * 绑定设备
     *
     * @param devCode 设备编码
     * @param userId  用户id
     */
    @Transactional(rollbackFor = Exception.class)
    void bindDev(String devCode, Integer userId);

    /**
     * 解除某个用户下所有设备的绑定
     *
     * @param devId 设备id
     */
    @Transactional(rollbackFor = Exception.class)
    void unbindDevByDevId(Integer devId);

    /**
     * 批量解除绑定
     *
     * @param devIds devID集合
     */
    @Transactional(rollbackFor = Exception.class)
    void unbindDevBatch(List<Integer> devIds);

    /**
     * 通过devCode查询对象
     *
     * @param devCode 设备编码
     * @return 对象
     */
    Device findByDevCode(String devCode);

    /**
     * 通过devCode 分页查询
     *
     * @param page     第几页
     * @param pageSize 每页个数
     * @param devCode  设备编号
     * @param userId   用户id
     * @return page对象
     */
    Page<Device> findPage(int page, int pageSize, String devCode, Integer userId);

    /**
     * 查询用户下所有设备
     *
     * @param userId 用户id
     * @return 集合
     */
    List<Device> findByUserId(Integer userId);


    /**
     * 获取设备绑定的账号
     *
     * @param deviceList 要获取绑定账号的列表
     */
    void getDevBindAccount(List<Device> deviceList);


    /**
     * 批量删除
     *
     * @param devIds devI
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteBatch(List<Integer> devIds);

    /**
     * 更新设备绑定状态
     *
     * @param status  设备状态
     * @param devCode 设备码
     * @return 绑定的结果
     */
    @Transactional(rollbackFor = Exception.class)
    int updateBindStatus(Integer status, String devCode);

    /**
     * 更新滤芯寿命
     *
     * @param filterLife 滤芯寿命
     * @param devCode    设备码
     */
    void updateFilterLife(Integer filterLife, String devCode);

    /**
     * 通过用户id的到绑定设备个数
     *
     * @param userId 用户id
     * @return 绑定设备数
     */
    Integer countByUserId(Integer userId);

    /**
     * 获取用户绑定设备列表（小程序）
     *
     * @param status 绑定状态
     * @param userId 用户id
     * @return 查询结果
     */
    List<Device> findUserBindDevList(Integer status, Integer userId);

    /**
     * 更换滤芯
     *
     * @param status  滤芯状态
     * @param devCode 设备编码
     */
    void changeFilter(Integer status, String devCode);

}


