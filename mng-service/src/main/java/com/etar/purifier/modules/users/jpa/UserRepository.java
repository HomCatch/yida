package com.etar.purifier.modules.users.jpa;

import entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * jpa 接口
 *
 * @author hzh
 * @since 2018-10-08
 */

@Transactional(rollbackFor = Exception.class)
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * 通过条件查询
     *
     * @param specification 过滤条件
     * @param pageable      分页对象
     * @return 插件page对象
     */
    Page<User> findAll(Specification specification, Pageable pageable);

    /**
     * 通过openId查询对象
     *
     * @param openId 微信openId
     * @return 对象
     */
    User findByOpenId(String openId);

    /**
     * 更新用户的昵称
     *
     * @param id       id
     * @param nickName 昵称
     */
    @Modifying
    @Query(value = "update wx_user set nick_name=?2 where id=?1 ", nativeQuery = true)
    void updateNickName(int id, String nickName);


    /**
     * 手机号模糊查询
     *
     * @param nickName 手机号
     * @return 结果
     */
    List<User> findByNickNameLike(String nickName);


    /**
     * 通过手机号查询
     *
     * @param phone 手机号
     * @return 用户
     */
    User findByPhone(String phone);
}