package entity.advstatic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wzq
 * @since 2019-01-21
 */

@Entity
@Table(name="adv_static")
public class AdvStatic implements Serializable {

    private static final long serialVersionUID = 1L;

  	@Id
    @JsonProperty(value = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonProperty(value = "adv_id")
    private Integer advId;
    @JsonProperty(value = "adv_name")
    private String advName;
    @JsonProperty(value = "dev_id")
    private String devId;
    @JsonProperty(value = "report_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportTime;

    public String getAdvName() {
        return advName;
    }

    public void setAdvName(String advName) {
        this.advName = advName;
    }

    public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getAdvId() {
      return advId;
   }

   public void setAdvId(Integer advId) {
      this.advId = advId;
   }

   public String getDevId() {
      return devId;
   }

   public void setDevId(String devId) {
      this.devId = devId;
   }

   public Date getReportTime() {
      return reportTime;
   }

   public void setReportTime(Date reportTime) {
      this.reportTime = reportTime;
   }


}