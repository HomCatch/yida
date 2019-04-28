package com.etar.purifier.modules.inletfilter.controller;


import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.inletfilter.service.InletFilterService;
import entity.common.entity.BatchReqVo;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import entity.inletfilter.InletFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.ResultCode;

import java.util.Date;
import java.util.List;

/**
 * 滤芯 InletFilterController层
 *
 * @author hzh
 * @since 2018-10-15
 */
@RestController
@RequestMapping(value = "yida/filter")
public class InletFilterController {
    private static Logger log = LoggerFactory.getLogger(InletFilterController.class);
    private final InletFilterService inletFilterService;

    @Autowired
    public InletFilterController(InletFilterService inletFilterService) {
        this.inletFilterService = inletFilterService;
    }


    /**
     * 新增InletFilter
     *
     * @param inletFilter InletFilter
     */
    @PostMapping(value = "/filters")
    @LogOperate(description = "新增设备广告")
    public Result addAd(@Validated @RequestBody InletFilter inletFilter) {
        log.info("进入设备广告");
        Result result = new Result();
        boolean exists = inletFilterService.existsByName(inletFilter.getName());
        if (exists) {
            return result.error(ResultCode.FILTER_NAME_EXISTS);
        }
        inletFilter.setCreateTime(new Date());
        inletFilter.setUpdateTime(new Date());
        try {
            inletFilterService.save(inletFilter);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * 更新
     *
     * @param adId adId
     */
    @PutMapping(value = "/filters/{adId}")
    @LogOperate(description = "更新设备广告")
    public Result updateBanner(@PathVariable("adId") Integer adId, @Validated @RequestBody InletFilter inletFilter) {
        log.info("进入更新滤芯接口");
        Result result = new Result();
        boolean exists = inletFilterService.existsById(adId);
        if (!exists) {
            return result.error(ResultCode.FILTER_NOT_EXISTS);
        }
        try {
            int ret = inletFilterService.updateAdvertising(inletFilter);
            switch (ret) {
                case 0:
                    result.error(ResultCode.FILTER_NAME_EXISTS);
                    break;
                case 1:
                    result.ok();
                    break;
                case 0xff:
                    result.error(ResultCode.DB_ERROR);
                    break;
                default:
                    result.error(ResultCode.ERROR);
                    break;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 删除
     *
     * @param adId adId
     */
    @DeleteMapping(value = "/filters/{adId}")
    @LogOperate(description = "删除设备广告")
    public Result delAd(@PathVariable("adId") Integer adId) {
        log.info("进入删除滤芯接口");
        Result result = new Result();
        boolean exists = inletFilterService.existsById(adId);
        if (!exists) {
            return result.error(ResultCode.FILTER_NOT_EXISTS);
        }
        try {
            inletFilterService.deleteById(adId);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * 批量删除
     *
     * @param batchReqVo id集合
     */
    @PostMapping(value = "/batch")
    @LogOperate(description = "批量删除设备广告")
    public Result delBatch(@Validated @RequestBody BatchReqVo batchReqVo) {
        log.info("进入批量删除inletFilterService");
        Result result = new Result();
        if (CollectionUtils.isEmpty(batchReqVo.getIdList())) {
            return result.error(ResultCode.AD_IDS_IS_NULL);
        }
        try {
            int ret = inletFilterService.delBatch(batchReqVo);
            switch (ret) {
                case 1:
                    result.ok();
                    break;
                case 2:
                    result.error(ResultCode.ADS_IS_NULL);
                    break;
                default:
                    result.error(ResultCode.ERROR);
                    break;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result;
    }

    /**
     * 滤芯上架或下架
     *
     * @param filterId 滤芯id
     */
    @PutMapping(value = "/filters/{filterId}/state/{state}")
    @LogOperate(description = "滤芯上架或下架")
    public Result shelves(@PathVariable("filterId") Integer filterId, @PathVariable("state") Integer state) {
        log.info("进入banner上架或下架接口");
        Result result = new Result();
        boolean exists = inletFilterService.existsById(filterId);
        if (!exists) {
            return result.error(ResultCode.FILTER_NOT_EXISTS);
        }
        try {
            inletFilterService.shelves(filterId, state);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.error(ResultCode.SUCCESS);
    }

    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 页面大小
     * @return Page<Advertising> 对象
     */
    @GetMapping(value = "/pages")
    public Result findByPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "name", required = false, defaultValue = "") String name) {

        DataResult result = new DataResult();
        Page<InletFilter> all = inletFilterService.findByPage(page - 1, pageSize, name);
        List<InletFilter> content = all.getContent();
        PageBean<InletFilter> pageBean = new PageBean<>();
        pageBean.setCurPage(page);
        pageBean.setItemCounts(all.getTotalElements());
        pageBean.setPageSize(pageSize);
        pageBean.setList(content);
        result.ok();
        result.setDatas(pageBean);
        return result;
    }

    /**
     * 获取上架的广告
     *
     * @return 广告 对象
     */
    @GetMapping(value = "/filters")
    public Result findState() {
        DataResult result = new DataResult();
        InletFilter inletFilter = inletFilterService.findByState(1);
        result.ok();
        result.setDatas(inletFilter);
        return result;
    }
}