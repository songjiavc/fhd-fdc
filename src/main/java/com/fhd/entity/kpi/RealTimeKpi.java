package com.fhd.entity.kpi;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 实时指标实体
 */
@Entity
@Table(name = "T_KPI_RTIME")
public class RealTimeKpi extends IdEntity implements Serializable {

    private static final long serialVersionUID = 4593457370522105038L;

    /**
     * 名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 编号
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 说明
     */
    @Column(name = "EDESC", length = 2000)
    private String desc;

    /**
     * 告警方案
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALARM_ID")
    private AlarmPlan alarmPlan;

    /**
     * 公司
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private SysOrganization company;

    /**
     * 删除状态
     */
    @Column(name = "DELETE_STATUS", length = 100)
    private Boolean deleteStatus;
    
    /**
     * 灯状态
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ESTATUS")
    private DictEntry status;
    
    /**
     * 单位
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNITS")
    private DictEntry units;
    

    public DictEntry getUnits() {
        return units;
    }

    public void setUnits(DictEntry units) {
        this.units = units;
    }

    public DictEntry getStatus() {
        return status;
    }

    public void setStatus(DictEntry status) {
        this.status = status;
    }

    public Boolean getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Boolean deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public SysOrganization getCompany() {
        return company;
    }

    public void setCompany(SysOrganization company) {
        this.company = company;
    }

    public AlarmPlan getAlarmPlan() {
        return alarmPlan;
    }

    public void setAlarmPlan(AlarmPlan alarmPlan) {
        this.alarmPlan = alarmPlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
