package com.fhd.entity.risk;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 *  管理报告关联风险
 * 
 * @author 张健
 * @date 2013-12-31
 * @since Ver 1.1
 */
@Entity
@Table(name = "T_MANAGE_REPORT_RELA_RISK") 
public class ManageReportRelaRisk extends IdEntity implements Serializable{
    private static final long serialVersionUID = -560056895428600775L;
    
    /**
     * 管理模板
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private ManageReport manageReport;
    
    /**
     * 风险
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RISK_ID")
    private Risk risk;
    
    public Risk getRisk() {
        return risk;
    }

    public void setRisk(Risk risk) {
        this.risk = risk;
    }

    public ManageReport getManageReport() {
        return manageReport;
    }

    public void setManageReport(ManageReport manageReport) {
        this.manageReport = manageReport;
    }

}

