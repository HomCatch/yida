package com.etar.purifier.modules.filterinfo.service.impl;

import com.etar.purifier.modules.filterinfo.jpa.FilterInfoRepository;
import com.etar.purifier.modules.filterinfo.service.FilterInfoService;
import entity.common.entity.Result;
import entity.filterinfo.FilterInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import utils.ResultCode;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>
 * FilterInfo服务类
 * </p>
 *
 * @author gmq
 * @since 2018-12-03
 */

@Service
public class FilterInfoServiceImpl implements FilterInfoService {
    private static Logger log = LoggerFactory.getLogger(FilterInfoServiceImpl.class);
    private final FilterInfoRepository filterInfoRepository;
    @Resource
    private HttpServletResponse response;
    @Resource
    private ResourceLoader resourceLoader;

    @Autowired
    public FilterInfoServiceImpl(FilterInfoRepository filterInfoRepository) {
        this.filterInfoRepository = filterInfoRepository;
    }

    /**
     * 保存对象
     *
     * @param filterInfo 对象
     *                   持久对象，或者对象集合
     */
    @Override
    public FilterInfo save(FilterInfo filterInfo) {
        return filterInfoRepository.save(filterInfo);
    }

    /**
     * 通过id删除对象
     *
     * @param id id
     */
    @Override
    public void deleteById(Long id) {
        filterInfoRepository.deleteById(id);
    }

    /**
     * 通过id判断是否存在
     *
     * @param id id
     */
    @Override
    public boolean existsById(Long id) {
        return filterInfoRepository.existsById(id);
    }


    @Override
    public FilterInfo findByFilterCode(String filterCode) {
        return filterInfoRepository.findByFilterCode(filterCode);
    }

    @Override
    public int updateFilterCode(int status, String filterCode) {
        return filterInfoRepository.updateFilterCode(status, filterCode);
    }

    @Override
    public int verifyFilterCode(String filterCode) {
        boolean exists = existsByFilterCode(filterCode);
        if (!exists) {
            return 1;
        }
        FilterInfo byFilterCode = findByFilterCode(filterCode);
        if (byFilterCode.getStatus() == 1) {
            return 2;
        }
        return 0;


    }

    @Override
    public Page<FilterInfo> findPage(Integer page, Integer pageSize, String filterCode) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (StringUtils.isNotBlank(filterCode)) {
                    predicate.getExpressions().add(cb.like(root.get("filterCode").as(String.class), "%" + filterCode + "%"));
                }
                return predicate;
            }
        };
        return filterInfoRepository.findAll(specification, pageable);
    }

    /**
     * 通过id判断是否存在
     *
     * @param filterCode 滤芯编码
     */
    @Override
    public boolean existsByFilterCode(String filterCode) {
        return filterInfoRepository.existsByFilterCode(filterCode);
    }

    @Override
    public void deleteAll(List<FilterInfo> list) {
        filterInfoRepository.deleteInBatch(list);
    }

    @Override
    public void delete(FilterInfo filterInfo) {
        filterInfoRepository.delete(filterInfo);
    }

    @Override
    public List<FilterInfo> findList() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return filterInfoRepository.findAll(sort);
    }

    private List<FilterInfo> findByIdIn(List<Long> ids) {
        return filterInfoRepository.findByIdInOrderByIdDesc(ids);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return filterInfoRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return FilterInfo对象
     */
    @Override
    public FilterInfo findById(Long id) {
        Optional<FilterInfo> byId = filterInfoRepository.findById(id);
        FilterInfo filterInfo = null;
        if (byId.isPresent()) {
            filterInfo = byId.get();
        }
        return filterInfo;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<FilterInfo>对象
     */
    @Override
    public Page<FilterInfo> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return filterInfoRepository.findAll(pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchImport(MultipartFile file) throws Exception {
        Result result = new Result();
        List<FilterInfo> filterInfos = new ArrayList<>();

        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return result.error(ResultCode.EXCEL_INVALID_FORMAT);
        }
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        InputStream is;
        Workbook wb;

        is = file.getInputStream();
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);
        int rowNum = sheet.getPhysicalNumberOfRows();
        int coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();
        if (rowNum == 1) {
            //只有标题行
            return result.error(ResultCode.EXCEL_NULL_VALUE);
        }
        if (coloumNum != 1) {
            //不是一列
            return result.error(ResultCode.EXCEL_INVALID_FORMAT);
        }
        FilterInfo filterInfo;
        Date date = new Date();
        //导入失败的记录
        List<String> erroList = new LinkedList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
            String filterCode = row.getCell(0).getStringCellValue();
            if (filterCode == null || filterCode.isEmpty()) {
                log.info("导入失败(第" + (r + 1) + "行,滤芯编码未填写)");
                continue;
            }

            filterInfo = new FilterInfo();
            filterInfo.setFilterCode(filterCode);
            filterInfo.setInventoryTime(date);
            filterInfo.setStatus(0);
            filterInfos.add(filterInfo);
        }

        for (FilterInfo info : filterInfos) {
            String filterCode = info.getFilterCode();
            boolean exists = existsByFilterCode(filterCode);
            if (exists) {
                log.info(filterCode + "已经存在，不能导入");
                erroList.add(filterCode);
                continue;
            }
            save(info);
        }
        result.setRet(erroList.size() > 0 ? -1 : 0);
        result.setMsg("成功导入" + (filterInfos.size() - erroList.size()) + "条,导入失败" + erroList.size() + "条");
        return result;
    }


    @Override
    public void batchExport(List<Long> ids) throws Exception {

        //创建HSSFWorkbook对象(excel的文档对象)
        XSSFWorkbook wb = new XSSFWorkbook();
        //建立新的sheet对象（excel的表单）
        XSSFSheet sheet = wb.createSheet("滤芯信息表");
        setTitle(wb, sheet);

        List<FilterInfo> filterInfos;
        if (CollectionUtils.isEmpty(ids)) {
            filterInfos = findList();
        } else {
            filterInfos = findByIdIn(ids);
        }
        //新增数据行，并且设置单元格数据
        int rowNum = 1;
        for (FilterInfo filterInfo : filterInfos) {
            XSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(filterInfo.getFilterCode());
            rowNum++;
        }
        //输出Excel文件
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=filterInfo.xlsx");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();

    }

    @Override
    public void downloadTemplate() {
        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            String fileName = "滤芯编码导入模板.xlsx";
            String path = "template/filterInfoModel.xlsx";
            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:" + path);

            response.setContentType("application/msexcel");
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("charset", "utf-8");
            response.addHeader("Pragma", "no-cache");
            String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + encodeName);

            inputStream = resource.getInputStream();
            servletOutputStream = response.getOutputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                // 召唤jvm的垃圾回收器
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 设置表头
     * @param workbook workbook
     * @param sheet  XSSFSheet
     */
    private void setTitle(XSSFWorkbook workbook, XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        sheet.setColumnWidth(0, 20 * 256);

        //设置为居中加粗
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //sheet页对象
        //设置一下上下左右的格式
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);

        style.setFont(font);
        XSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("滤芯编号");
        cell.setCellStyle(style);

    }
}


