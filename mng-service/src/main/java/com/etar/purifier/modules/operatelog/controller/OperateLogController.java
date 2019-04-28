package com.etar.purifier.modules.operatelog.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import com.etar.purifier.modules.operatelog.service.OperateLogService;
import com.etar.purifier.utils.StringUtil;
import entity.operatelog.OperateLog;
import entity.operatelog.QueryOperateLog;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * OperateLogController层
 *
 * @author gmq
 * @since 2018-12-25
 */
@RestController
@RequestMapping(value = "/operateLogs")
public class OperateLogController {
    private static Logger log = LoggerFactory.getLogger(OperateLogController.class);
    @Autowired
    private OperateLogService operateLogService;


    /**
     * 保存对象<br/>
     *
     * @param operateLog 对象
     */
    @PostMapping(value = "/")
    public Result save(@Validated @RequestBody OperateLog operateLog) {
        Result result = new Result();
        try {
            operateLogService.save(operateLog);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2001, "新增失败");
        }
        return result.ok();
    }

    /**
     * 更新
     *
     * @param operateLog
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result updateBanner(@Validated @RequestBody OperateLog operateLog) {
        Result result = new Result();
        try {
            boolean exists = operateLogService.existsById(operateLog.getId());
            if (!exists) {
                return result.error(2002, "修改失败，未找到");
            }
            operateLogService.save(operateLog);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2002, "修改失败");
        }
        return result.ok();
    }

    /**
     * 通过id集合删除对象
     *
     * @param ids id集合
     */
    @DeleteMapping(value = "/{id}")
    public Result deleteById(@PathVariable("id") String ids) {
        Result result = new Result();
        try {
            operateLogService.deleteInBatch(StringUtil.stringToIntegerList(ids));
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
     * @return LogOperate 对象
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id") Integer id) {
        DataResult result = new DataResult();
        try {
            result.setDatas(operateLogService.findById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2004, "不存在");
        }
        return result.ok();
    }

    /**
     * 分页查询
     *
     * @return Page<LogOperate> 对象
     */
    @PostMapping(value = "")
    public Result findByPage(@RequestBody QueryOperateLog queryOperateLog) {

        DataResult result = new DataResult();
        try {
            int page = queryOperateLog.getPage();
            int pageSize = queryOperateLog.getPageSize();
            Page<OperateLog> all = operateLogService.findAll(page - 1, pageSize, queryOperateLog);
            PageBean<OperateLog> pageBean = new PageBean<>();
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
     * EXCEL导出
     * @param response re
     * @param ids  id逗号隔开
     * @throws IOException IO异常
     */
    @GetMapping(value = "/export/{id}")
    public void prjExport(HttpServletResponse response, @PathVariable("id") String ids) throws IOException {
        OutputStream os = null;
        try {
            //获取数据库数据
            Workbook workbook;
            List<OperateLog> list = operateLogService.findAllById(StringUtil.stringToIntegerList(ids));
            workbook = ExcelExportUtil.exportExcel(new ExportParams("操作日志表", "operate_log"),
                    OperateLog.class, list);
            //保存在本地
            String filename = "ter_daily_report" + System.currentTimeMillis();
            os = response.getOutputStream();
            // 清空输出流
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xls");
            response.setContentType("application/msexcel");
            if (workbook != null) {
                workbook.write(os);
                log.info("导出成功.....");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            if(os!=null){
                os.flush();
                os.close();
            }
        }
    }
}