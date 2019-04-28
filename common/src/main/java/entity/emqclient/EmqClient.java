package entity.emqclient;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * @since 2019-02-13
 */

@Entity
@Table(name = "tb_emq_client")
@DynamicInsert
@DynamicUpdate
public class EmqClient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(name = "编号")
    private Integer id;
    /**
     * 用户名
     */
    @Excel(name = "用户名")
    @JsonProperty("username")
    private String userName;
    /**
     * 客户端ID
     */
    @Excel(name = "客户端ID")
    @JsonProperty("client_id")
    private String clientId;
    /**
     * 0上线1下线
     */
    @Excel(name = "0下线1上线")
    @JsonProperty("action")
    private String action;
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
    @Transient
    @JsonProperty("conn_ack")
    private String connAck;

    public String getConnAck() {
        return connAck;
    }

    public void setConnAck(String connAck) {
        this.connAck = connAck;
    }

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "{" +
                "userName='" + userName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", action=" + action +
                ", createTime=" + createTime +
                ", connAck='" + connAck + '\'' +
                '}';
    }
}