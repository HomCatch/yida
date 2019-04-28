package com.etar.purifier.modules.emqclient.service;

import com.etar.purifier.base.IBaseService;
import entity.emqclient.EmqClient;
import entity.emqclient.QueryEmqClient;
import org.springframework.data.domain.Page;

import java.util.List;
/**
 * <p>
 *  EmqClient接口
 * </p>
 *
 * @author gmq
 * @since 2019-02-13
 */

public interface EmqClientService  extends IBaseService<EmqClient,Integer> {

    /**
     * 按条件查询
     * @param page 页数
     * @param pageSize 数量
     * @param
     * @return Page
     */
    Page<EmqClient> findAll(int page, int pageSize, QueryEmqClient queryEmqClient);

    /**
     * 根据Id查询list
     * @return
     */
    List<EmqClient> findAllById(List<Integer> ids);


}


