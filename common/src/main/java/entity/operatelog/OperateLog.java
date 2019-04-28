package entity.operatelog;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gmq
 * @since 2018-12-25
 */

@Entity
@Table(name = "tb_operate_log")
@DynamicInsert
@DynamicUpdate
public class OperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(name = "编号")
    private Integer id;
    /**
     * 用户名
     */
    @Excel(name = "用户名")
    private String userName;
    /**
     * 操作信息
     */
    @Excel(name = "操作信息")
    private String operateInfo;
    /**
     * 请求url
     */
    @Excel(name = "请求url")
    private String reqUrl;
    /**
     * 请求方式
     */
    @Excel(name = "请求方式")
    private String reqType;
    /**
     * 请求参数
     */
    @Excel(name = "请求参数")
    private String reqParam;
    /**
     * 请求时间
     */
    @Excel(name = "请求时间")
    private Integer reqTime;
    /**
     * 状态（0失败1成功）
     */
    @Excel(name = "状态",replace = { "失败_0", "成功_1" })
    private Integer status;
    /**
     * 操作IP
     */
    @Excel(name = "操作IP")
    private String ip;
    /**
     * 用户代理
     */
    @Excel(name = "用户代理")
    private String userAgent;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss")
    @Temporal(value = TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Transient
    private String startTime;
    @Transient
    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOperateInfo() {
        return operateInfo;
    }

    public void setOperateInfo(String operateInfo) {
        this.operateInfo = operateInfo;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getReqParam() {
        return reqParam;
    }

    public void setReqParam(String reqParam) {
        this.reqParam = reqParam;
    }

    public Integer getReqTime() {
        return reqTime;
    }

    public void setReqTime(Integer reqTime) {
        this.reqTime = reqTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


}