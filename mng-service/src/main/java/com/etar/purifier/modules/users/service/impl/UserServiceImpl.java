package com.etar.purifier.modules.users.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.etar.purifier.common.validation.XException;
import com.etar.purifier.modules.dev.service.DeviceService;
import com.etar.purifier.modules.users.jpa.UserRepository;
import com.etar.purifier.modules.users.service.UserService;
import entity.dev.Device;
import entity.user.User;
import entity.user.WxUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * User服务类
 *
 * @author hzh
 * @since 2018-10-08
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private DeviceService deviceService;

    /**
     * 保存对象
     *
     * @param user 对象
     *             持久对象，或者对象集合
     */
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除对象
     *
     * @param user 对象
     */
    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteAll(List<User> list) {
        userRepository.deleteAll(list);
    }

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Integer id) {
        return userRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return userRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return User对象
     */
    @Override
    public User findById(Integer id) {
        Optional<User> optional = userRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<User>对象
     */
    @Override
    public Page<User> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> findList() {
        return userRepository.findAll();
    }

    @Override
    public User findByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    @Override
    public User saveByUserInfo(String openId) {
        User user = findByOpenId(openId);
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setRegTime(new Date());
            user = save(user);
        }
        return user;
    }

    @Override
    public void saveNickName(WxUser wxUser) {
        User user = new User();
        Integer userId = wxUser.getUserId();
        User byId = findById(userId);
        //用户id不为空，判断是否被删除
        if (byId != null) {
            user.setId(userId);
            user.setNickName(wxUser.getNickName());
            System.out.println(("进入更新微信昵称：" + wxUser.getNickName()));
            userRepository.updateNickName(user.getId(), user.getNickName());
        } else {
            //用户id为空，通过openId判断是否被删除
            User byOpenId = findByOpenId(wxUser.getOpenId());
            if (byOpenId != null) {
                System.out.println(("进入保存微信昵称：" + wxUser.getNickName()));
                byOpenId.setNickName(wxUser.getNickName());
                save(byOpenId);
            } else {
                if (wxUser.getOpenId() == null) {
                    throw new XException("微信信息为空，请重试");
                }
                //都不存在就新增
                User user1 = new User();
                user1.setOpenId(wxUser.getOpenId());
                user1.setNickName(wxUser.getNickName());
                user1.setRegTime(new Date());
                save(user1);

            }
        }
    }

    @Override
    public void delBatchAndUnboundDev(List<Integer> userIds) {
        List<User> userList = userRepository.findAllById(userIds);
        if (!userList.isEmpty()) {
            //解除绑定
            for (User user : userList) {
                deviceService.unbindDevByDevId(user.getId());
            }
            userRepository.deleteInBatch(userList);
        }
    }

    @Override
    public void countBindDevByUserId(List<User> users) {
        for (User user : users) {
            Integer bindDevNum = deviceService.countByUserId(user.getId());
            user.setBindDevNum(bindDevNum);
        }
    }

    @Override
    public Page<User> findPage(Integer page, Integer pageSize, String nickName) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        Specification specification = (Specification) (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (StringUtils.isNotBlank(nickName)) {
                predicate.getExpressions().add(cb.like(root.get("nickName").as(String.class), "%" + nickName + "%"));
            }
            return predicate;
        };
        return userRepository.findAll(specification, pageable);
    }

    @Override
    public void delUserAndUnboundDev(Integer userId) {
        List<Device> devices = deviceService.findByUserId(userId);
        if (!CollectionUtils.isEmpty(devices)) {
            //解除设备绑定
            deviceService.unbindDevByDevId(userId);
        }
        //删除用户
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> findByNickNameLike(String nickName) {
        return userRepository.findByNickNameLike("%" + nickName + "%");
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
}


