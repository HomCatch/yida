package com.etar.purifier.modules.emqclient.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.etar.purifier.common.annotation.LogOperate;
import com.etar.purifier.modules.emqclient.service.EmqClientService;
import com.etar.purifier.utils.StringUtil;
import entity.common.entity.DataResult;
import entity.common.entity.PageBean;
import entity.common.entity.Result;
import entity.emqclient.EmqClient;
import entity.emqclient.QueryEmqClient;
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
 * EmqClientController层
 *
 * @author gmq
 * @since 2019-02-13
 */
@RestController
@RequestMapping(value = "/emqClients")
public class EmqClientController {
    private static Logger log = LoggerFactory.getLogger(EmqClientController.class);
    @Autowired
    private EmqClientService emqClientService;


    /**
     * 保存对象<br/>
     *
     * @param emqClient 对象
     */
    @PostMapping
    @LogOperate(description = "新增EmqClient")
    public Result save(@Validated @RequestBody EmqClient emqClient) {
        Result result = new Result();
        try {
            emqClientService.save(emqClient);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2001, "新增失败");
        }
        return result.ok();
    }

    /**
     * 更新
     *
     * @param emqClient
     * @return
     */
    @PutMapping(value = "/{id}")
    @LogOperate(description = "更新EmqClient")
    public Result updateBanner(@Validated @RequestBody EmqClient emqClient) {
        Result result = new Result();
        try {
            boolean exists = emqClientService.existsById(emqClient.getId());
            if (!exists) {
                return result.error(2002, "修改失败，未找到");
            }
            emqClientService.save(emqClient);
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2002, "修改失败");
        }
        return result.ok();
    }

    /**
     * 通过id集合删除对象
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @LogOperate(description = "删除EmqClient")
    public Result deleteById(@PathVariable("id") Integer id) {
        Result result = new Result();
        try {
            emqClientService.deleteById(id);
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
     * @return EmqClient 对象
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id") Integer id) {
        DataResult result = new DataResult();
        try {
            result.setDatas(emqClientService.findById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(2004, "不存在");
        }
        return result.ok();
    }

    /**
     * 分页查询
     *
     * @return Page<EmqClient> 对象
     */
    @PostMapping(value = "/pages")
    public Result findByPage(@RequestBody QueryEmqClient queryEmqClient) {

        DataResult result = new DataResult();
        try {
            int page = queryEmqClient.getPage();
            int pageSize = queryEmqClient.getPageSize();
            Page<EmqClient> all = emqClientService.findAll(page - 1, pageSize, queryEmqClient);
            PageBean<EmqClient> pageBean = new PageBean<>();
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
     *
     * @param response re
     * @param ids      id逗号隔开
     * @throws IOException IO异常
     */
    @GetMapping(value = "/export/{id}")
    public void prjExport(HttpServletResponse response, @PathVariable("id") String ids) throws IOException {
        OutputStream os = null;
        try {
            //获取数据库数据
            Workbook workbook;
            List<EmqClient> list = emqClientService.findAllById(StringUtil.stringToIntegerList(ids));
            workbook = ExcelExportUtil.exportExcel(new ExportParams("表", "tb_emq_client"),
                    EmqClient.class, list);
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
            if (os != null) {
                os.flush();
                os.close();
            }
        }
    }
}
