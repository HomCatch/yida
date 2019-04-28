package com.etar.purifier.base;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author hzh
 * @date 2018/9/17
 */
public interface IBaseService<T, ID> {
    /**
     * 保存对象
     *
     * @param t 持久对象，或者对象集合
     * @return 保存后的对象
     */
    T save(T t);


    /**
     * 通过list删除对象
     *
     * @param list 集合
     */
    void deleteAll(List<T> list);

    /**
     * 删除对象
     *
     * @param t 对象
     */
    void delete(T t);

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    void deleteById(ID id);

    /**
     * 通过id判断是否存在
     *
     * @param id id
     * @return 存在与否
     */
    boolean existsById(ID id);

    /**
     * 返回可用实体的数量
     *
     * @return long 统计个数
     */
    long count();

    /**
     * 通过id查询
     *
     * @param id id
     * @return T
     */
    T findById(ID id);

    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 每页个数
     * @return Page<T>
     */
    Page<T> findAll(int page, int pageSize);

    /**
     * 查询集合
     *
     * @return Page<T>
     */
    List<T> findList();

}
