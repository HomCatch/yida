package com.xiaohe.etar.miniprogram.modules.dev.service.impl;

import com.xiaohe.etar.miniprogram.modules.advertising.service.AdvertisingService;
import com.xiaohe.etar.miniprogram.modules.dev.jpa.DeviceRepository;
import com.xiaohe.etar.miniprogram.modules.dev.service.DeviceService;
import com.xiaohe.etar.miniprogram.modules.users.UserFeignService;
import com.xiaohe.etar.miniprogram.utils.ConstantUtil;
import entity.dev.Device;
import entity.user.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Device服务类
 * </p>
 *
 * @author hzh
 * @since 2018-10-08
 */

@Service
public class DeviceServiceImpl implements DeviceService {
    private static Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);
    private final DeviceRepository deviceRepository;
    private final UserFeignService userService;

    @Autowired
    public DeviceServiceImpl(DeviceRepository deviceRepository, UserFeignService userService) {
        this.deviceRepository = deviceRepository;
        this.userService = userService;
    }

    /**
     * 保存对象
     *
     * @param device 对象
     *               持久对象，或者对象集合
     */
    @Override
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    /**
     * 删除对象
     *
     * @param device 对象
     */
    @Override
    public void delete(Device device) {
        deviceRepository.delete(device);
    }

    @Override
    public void deleteAll(List<Device> list) {
        deviceRepository.deleteAll(list);
    }

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @Override
    public void deleteById(Integer id) {
        deviceRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Integer id) {
        return deviceRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return deviceRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return Device对象
     */
    @Override
    public Device findById(Integer id) {
        Optional<Device> optional = deviceRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<Device>对象
     */
    @Override
    public Page<Device> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return deviceRepository.findAll(pageable);
    }

    @Override
    public List<Device> findByUserId(Integer userId) {
        return deviceRepository.findByUserId(userId);
    }

    @Override
    public List<Device> findList() {
        return deviceRepository.findAll();
    }

    @Override
    public boolean existsByDevCode(String devCode) {
        return deviceRepository.existsByDevCode(devCode);
    }

    @Override
    public void bindDev(String devCode, Integer userId) {
        //先通过devCode查询对象
        Device device = findByDevCode(devCode);
        //设置要绑定的值
        device.setBindTime(new Date());
        device.setUserId(userId);
        //第一次扫码，滤芯寿命设为0并标记为激活
        if (ConstantUtil.DEV_UN_ACTIVE == device.getActive()) {
            //绑定时，滤芯寿命更为0
            updateFilterLife(0, devCode);
            //设置激活
            device.setActive(ConstantUtil.DEV_ACTIVE);
        }
        deviceRepository.save(device);
    }

    @Override
    public void unbindDevBatch(List<Integer> devIds) {
        if (!devIds.isEmpty()) {
            for (Integer id : devIds) {
                unbindDevByDevId(id);
            }
        }

    }

    @Override
    public Device findByDevCode(String devCode) {
        return deviceRepository.findByDevCode(devCode);
    }


    @Override
    public Page<Device> findPage(int page, int pageSize, String devCode, Integer userId) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        Specification specification = (Specification) (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (StringUtils.isNotBlank(devCode)) {
                predicate.getExpressions().add(cb.like(root.get("devCode").as(String.class), "%" + devCode + "%"));
            }
            if (userId != null) {
                predicate.getExpressions().add(cb.equal(root.get("userId").as(Integer.class), userId));
                //获取绑定的设备
                predicate.getExpressions().add(cb.equal(root.get("status").as(Integer.class), 1));
            }
            return predicate;
        };
        return deviceRepository.findAll(specification, pageable);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Integer> devIds) {
        //查找所有设备
        List<Device> deviceList = deviceRepository.findAllById(devIds);
        if (!deviceList.isEmpty()) {
            deviceRepository.deleteInBatch(deviceList);
        }
    }


    @Override
    public int updateBindStatus(Integer status, String devCode) {
        return deviceRepository.updateBindStatus(status, devCode);
    }


    @Override
    public void updateFilterLife(Integer filterLife, String devCode) {
        deviceRepository.updateFilterLife(filterLife, devCode);
    }

    @Override
    public Integer countByUserId(Integer userId) {
        return deviceRepository.countDevNumByUserId(userId);
    }

    @Override
    public void unbindDevByDevId(Integer devId) {
        Device device = findById(devId);
        if (device != null) {
            //更改设备为未绑定状态
            device.setStatus(0);
            //关闭重复绑定功能
            device.setUserId(null);
            deviceRepository.save(device);
        }
    }

    @Override
    public void changeFilter(Integer filterLifeTime, String devCode) {
        deviceRepository.changeFilter(filterLifeTime, devCode);
    }

    @Override
    public List<Device> findUserBindDevList(Integer status, Integer userId) {
        return deviceRepository.findByStatusAndUserId(status, userId);
    }

    @Override
    public void getDevBindAccount(List<Device> deviceList) {
        for (Device device : deviceList) {
            Integer userId = device.getUserId();
            if (userId != null) {
                User user = userService.findById(userId);
                if (user != null) {
                    device.setBindAccount(user.getNickName());
                }
            }

        }
    }
}


