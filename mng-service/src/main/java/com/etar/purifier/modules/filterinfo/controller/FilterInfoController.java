package com.etar.purifier.modules.filterinfo.controller;

import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.filterinfo.service.FilterInfoService;
import entity.common.entity.BatchLongReqVo;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import entity.filterinfo.FilterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utils.ResultCode;

import java.util.ArrayList;
import java.util.Date;

/**
 * FilterInfoController层
 *
 * @author gmq
 * @since 2018-12-03
 */
@RestController
@RequestMapping(value = "yida/filterInfos")
public class FilterInfoController {
    private static Logger log = LoggerFactory.getLogger(FilterInfoController.class);
    private final FilterInfoService filterInfoService;

    @Autowired
    public FilterInfoController(FilterInfoService filterInfoService) {
        this.filterInfoService = filterInfoService;
    }


    /**
     * 保存对象<br/>
     *
     * @param filterInfo 对象
     */
    @PostMapping
    @LogOperate(description = "新增滤芯")
    public Result save(@Validated @RequestBody FilterInfo filterInfo) {
        Result result = new Result();
        boolean exists = filterInfoService.existsByFilterCode(filterInfo.getFilterCode());
        try {
            if (!exists) {
                filterInfo.setInventoryTime(new Date());
                filterInfoService.save(filterInfo);
            } else {
                return result.error(ResultCode.FILTER_INFO_CODE_EXIST);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2001, "新增失败");
        }
        return result.ok();
    }

    /**
     * 更新
     */
    @PutMapping(value = "/{id}")
    @LogOperate(description = "更新滤芯")
    public Result updateBanner(@PathVariable("id") Long id, @Validated @RequestBody FilterInfo filterInfo) {
        Result result = new Result();
        try {
            boolean exists = filterInfoService.existsById(id);
            if (!exists) {
                return result.error(ResultCode.FILTER_INFO_NOT_EXIST);
            }
            FilterInfo filterInfo1 = filterInfoService.findByFilterCode(filterInfo.getFilterCode());
            if (filterInfo1.getId().equals(id)) {
                filterInfo.setInventoryTime(filterInfo1.getInventoryTime());
                filterInfoService.save(filterInfo);
            } else {
                return result.error(ResultCode.FILTER_INFO_CODE_EXIST);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2002, "修改失败");
        }
        return result.ok();
    }

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @DeleteMapping(value = "/{id}")
    @LogOperate(description = "删除滤芯")
    public Result deleteById(@PathVariable("id") Long id) {
        Result result = new Result();
        try {
            FilterInfo byId = filterInfoService.findById(id);
            if (byId.getStatus().equals(1)) {
                return result.error(2005, "滤芯使用中，不能删除");
            }
            filterInfoService.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2003, "删除失败");
        }
        return result.ok();
    }


    /**
     * 通过id查找对象
     *
     * @param id id
     * @return FilterInfo 对象
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id") Long id) {
        DataResult result = new DataResult();
        try {
            result.setDatas(filterInfoService.findById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(ResultCode.FILTER_INFO_NOT_EXIST);
        }
        return result.ok();
    }

    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 页面大小
     * @return Page<FilterInfo> 对象
     */
    @GetMapping
    public Result findByPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "filterCode", required = false) String filterCode) {

        DataResult result = new DataResult();
        try {
            Page<FilterInfo> all = filterInfoService.findPage(page - 1, pageSize, filterCode);
            PageBean<FilterInfo> pageBean = new PageBean<>();
            if (all == null) {
                pageBean.setList(new ArrayList<>());
                result.setDatas(pageBean);
                return result.ok();
            }
            pageBean.setCurPage(page);
            pageBean.setItemCounts(all.getTotalElements());
            pageBean.setPageSize(pageSize);
            pageBean.setList(all.getContent());
            result.setDatas(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return result.error(2005, "查询出错");
        }
        return result.ok();
    }

    /**
     * 导入滤芯
     *
     * @param file ex文件
     * @return Result
     */
    @PostMapping("/import")
    @LogOperate(description = "导入滤芯")
    public Result addFilterInfo(@RequestParam("file") MultipartFile file) {
        Result result = new Result();
        try {
            result= filterInfoService.batchImport(file);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new Result().error(ResultCode.EXCEL_IMPORT_FAIL);
        }
        return result;
    }

    /**
     * 批量导出
     *
     * @param batchReqVo 要导出的id
     * @return result
     */
    @PostMapping("/export")
    @LogOperate(description = "批量导出滤芯")
    public Result export(@Validated @RequestBody BatchLongReqVo batchReqVo) {
        try {
            filterInfoService.batchExport(batchReqVo.getIds());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Result().error(ResultCode.EXCEL_EXPORT_FAIL);
        }
        return new Result().ok();
    }


    /**
     * 下载模板
     */
    @GetMapping("/download")
    @LogOperate(description = "下载滤芯模板")
    public Result downloadTemplate() {
        try {
            filterInfoService.downloadTemplate();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Result().error(ResultCode.EXCEL_EXPORT_FAIL);
        }
        return new Result().ok();
    }

    /**
     * 更新滤芯使用状态
     *
     * @param status ex文件
     * @param filterCode ex文件
     */
    @GetMapping("/updateFilterCode")
    public int updateFilterCode(@RequestParam("status") int status,@RequestParam("filterCode")  String filterCode) {
        int result=0;
        try {
            result= filterInfoService.updateFilterCode(status, filterCode);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }


}


