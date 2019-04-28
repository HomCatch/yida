package com.etar.purifier.modules.banner.controller;


import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.banner.service.BannerService;
import entity.banner.Banner;
import entity.common.entity.BatchReqVo;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utils.ResultCode;

import java.util.Date;
import java.util.List;

/**
 * 小程序banner BannerController层
 *
 * @author hzh
 * @since 2018-10-15
 */
@RestController
@RequestMapping(value = "yida/banner")
public class BannerController {
    private static Logger log = LoggerFactory.getLogger(BannerController.class);
    private final BannerService bannerService;

    @Autowired
    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    /**
     * 新增banner
     *
     * @param banner banner对象
     */
    @PostMapping(value = "/banners")
    @LogOperate(description = "新增banner")
    public Result addBanner(@Validated @RequestBody Banner banner) {
        log.info("进入设备添加");
        Result result = new Result();
        boolean exists = bannerService.existsByName(banner.getName());
        if (exists) {
            return result.error(ResultCode.BANNER_NAME_EXISTS);
        }
        Date time = new Date();
        banner.setUpdateTime(time);
        banner.setCreateTime(time);
        try {

            bannerService.save(banner);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * 更新banner
     *
     * @param bannerId bannerId
     */
    @PutMapping(value = "/banners/{bannerId}")
    @LogOperate(description = "更新banner")
    public Result updateBanner(@PathVariable("bannerId") Integer bannerId, @Validated @RequestBody Banner banner) {
        log.info("进入更新设备接口");
        Result result = new Result();
        boolean exists = bannerService.existsById(bannerId);
        if (!exists) {
            return result.error(ResultCode.BANNER_NOT_EXISTS);
        }
        try {
            int ret = bannerService.updateBanner(banner);
            switch (ret) {
                case 0:
                    result.error(ResultCode.BANNER_NAME_EXISTS);
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
     * 删除banner
     *
     * @param bannerId bannerId
     */
    @DeleteMapping(value = "/banners/{bannerId}")
    @LogOperate(description = "删除banner")
    public Result delBanner(@PathVariable("bannerId") Integer bannerId) {
        log.info("进入删除设备接口");
        Result result = new Result();
        boolean exists = bannerService.existsById(bannerId);
        if (!exists) {
            return result.error(ResultCode.BANNER_NOT_EXISTS);
        }
        try {
            bannerService.deleteById(bannerId);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * 批量删除banner
     *
     * @param batchReqVo id集合
     */
    @PostMapping(value = "/batch")
    @LogOperate(description = "批量删除banner")
    public Result delBatch(@Validated @RequestBody BatchReqVo batchReqVo) {
        log.info("进入删除设备接口");
        Result result = new Result();
        if (batchReqVo.getIdList().isEmpty()) {
            return result.error(ResultCode.BANNER_IDS_IS_NULL);
        }
        try {
            bannerService.delBatch(batchReqVo);
        } catch (Exception e) {
            log.info(e.getMessage());
            return result.error(ResultCode.DB_ERROR);
        }
        return result.ok();
    }

    /**
     * banner上架或下架
     *
     * @param bannerId bannerId
     */
    @PutMapping(value = "/banners/{bannerId}/state/{state}")
    @LogOperate(description = "banner上架或下架")
    public Result shelves(@PathVariable("bannerId") Integer bannerId, @PathVariable("state") Integer state) {
        log.info("进入banner上架或下架接口");
        Result result = new Result();
        boolean exists = bannerService.existsById(bannerId);
        if (!exists) {
            return result.error(ResultCode.BANNER_NOT_EXISTS);
        }
        try {
            bannerService.shelves(bannerId, state);
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
     * @return Page<Banner> 对象
     */
    @GetMapping(value = "/pages")
    public Result findByPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "name", required = false, defaultValue = "") String name) {
        DataResult result = new DataResult();
        Page<Banner> all = bannerService.findByPage(page - 1, pageSize, name);
        List<Banner> content = all.getContent();
        PageBean<Banner> pageBean = new PageBean<>();
        pageBean.setCurPage(page);
        pageBean.setItemCounts(all.getTotalElements());
        pageBean.setPageSize(pageSize);
        pageBean.setList(content);
        result.ok();
        result.setDatas(pageBean);
        return result;
    }

    /**
     * 获取上架的banner
     *
     * @return banner 对象
     */
    @GetMapping(value = "/banners")
    public Result findState() {
        DataResult result = new DataResult();
        Banner banner = bannerService.findByState(1);
        result.ok();
        result.setDatas(banner);
        return result;
    }
}