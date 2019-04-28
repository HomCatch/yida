package com.etar.purifier.modules.advStatic.controller;

import com.etar.purifier.modules.advStatic.service.AdvStaticService;
import entity.advstatic.AdvStatic;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AdvStaticController层
 *
 * @author wzq
 * @since 2019-01-21
 */
@RestController
@RequestMapping(value = "/advstatic")
public class AdvStaticController {
    private static Logger log = LoggerFactory.getLogger(AdvStaticController.class);
    @Autowired
    private AdvStaticService advstaticService;

    /**
     * 保存对象<br/>
     *
     * @param advstatic
     * @throws Exception
     */
    //TODO 转移到mqtt服务
    @RequestMapping(value = "/save")
    public void save(AdvStatic advstatic) {
        advstaticService.save(advstatic);
    }

    /**
     * 删除对象
     *
     * @param advstatic
     * @throws Exception
     */
    @RequestMapping(value = "/delete")
    public void delete(AdvStatic advstatic) {
        advstaticService.delete(advstatic);
    }

    /**
     * 通过id删除对象
     *
     * @param id
     * @throws Exception
     */
    @RequestMapping(value = "/deleteById")
    public void deleteById(@RequestParam(value = "id") Integer id) {
        advstaticService.deleteById(id);
    }

    /**
     * 通过id查找对象
     *
     * @param id
     * @return AdvStatic
     * @throws Exception
     */
    @RequestMapping(value = "/findById")
    public AdvStatic findById(@RequestParam(value = "id") Integer id) {
        return advstaticService.findById(id);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return Page<AdvStatic>
     * @throws Exception
     */
    @GetMapping(value = "/findAll")
    public DataResult findAll(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, @RequestParam(value = "advName", required = false, defaultValue = "") String advName) {
        DataResult result = new DataResult();

        Page<AdvStatic> advStatics = advstaticService.findAll(page, pageSize, advName);
        List<AdvStatic> advList = advStatics.getContent();

        PageBean<AdvStatic> pageBean = new PageBean<>();
        pageBean.setCurPage(page);
        pageBean.setPageSize(pageSize);
        pageBean.setItemCounts(advStatics.getTotalElements());
        pageBean.setList(advList);
        result.ok();
        result.setDatas(pageBean);
        return result;

    }
}