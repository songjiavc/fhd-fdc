package com.fhd.entity.kpi;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.dic.DictEntry;

/**指标采集对外接口实体
 * @author xiaozhe
 *
 */
@Entity
@Table(name = "t_kpi_tmp_gather_data")
public class GatherData extends IdEntity implements java.io.Serializable {

    private static final long serialVersionUID = -7372818842386951253L;

    /**
     * 指标名称
     */
    @Column(name = "name")
    private String name;

    /**
    * 指标
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KPI_ID")
    private RealTimeKpi realTimeKpi;

    /**
     * 采集说明
     */
    @Column(name = "edesc")
    private String desc;

    /**
     * 采集值类型
     */
    @Column(name = "etype")
    private String type;

    /**
     * 采集时间
     */
    @Column(name = "etimes")
    private Date times;

    /**
     * 采集值状态
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estatus")
    private DictEntry status;

    /**
     * 采集值
     */
    @Column(name = "value")
    private Double value;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimes() {
        return times;
    }

    public void setTimes(Date times) {
        this.times = times;
    }

    public RealTimeKpi getRealTimeKpi() {
        return realTimeKpi;
    }

    public void setRealTimeKpi(RealTimeKpi realTimeKpi) {
        this.realTimeKpi = realTimeKpi;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DictEntry getStatus() {
        return status;
    }

    public void setStatus(DictEntry status) {
        this.status = status;
    }

}
