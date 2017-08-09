package com.fhd.entity.risk;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 管理报告实体
 * 
 * @author 张健
 * @date 2013-12-31
 * @since Ver 1.1
 */
@Entity
@Table(name = "T_MANAGE_REPORT") 
public class ManageReport extends IdEntity implements Serializable{
    private static final long serialVersionUID = 1831605195472471583L;
    
    /**
     * 名称
     */
    @Column(name = "ENAME")
    private String reportName;
    
    /**
     * 编号
     */
    @Column(name = "CODE")
    private String reportCode;
    
    /**
     * 责任部门
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_OCCURED_ORG")
    private SysOrganization occuredOrg;
    
    /**
     * 责任人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_OCCURED_EMP")
    private SysEmployee employee;
    
    /**
     * 内容
     */
    @Column(name = "ECONTENT")
    private String content;
    
    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATE_BY")
    private SysEmployee createBy;
    
    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;
    
    /**
     * 最后修改时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFY_TIME")
    private Date lastModifyTime;
    
    /**
     * 状态
     */
    @Column(name = "ESTATUS")
    private String status;
    
    /**
     * 删除状态
     */
    @Column(name = "DELETE_STATUS")
    private String deleteStatus;
    
    /**
     * 开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;
    
    /**
     * 结束时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    private Date endDate;
    
    /**
     * 类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ETYPE")
    private DictEntry reportType;
    
    /**
     * 评估计划ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSESS_PLAN_ID")
    private RiskAssessPlan riskAssessPlan;
    
    /**
     * 所属公司
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private SysOrganization company;
    
    /**
     * 历史记录关联表
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "manageReport")
    private Set<ManageReportRelaRisk> manageReportRelaRisk = new HashSet<ManageReportRelaRisk>(0);
    
    
    /**
     * 排序
     */
    @Column(name = "ESORT")
    private Integer sort;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public SysOrganization getOccuredOrg() {
        return occuredOrg;
    }

    public void setOccuredOrg(SysOrganization occuredOrg) {
        this.occuredOrg = occuredOrg;
    }

    public SysEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(SysEmployee employee) {
        this.employee = employee;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SysEmployee getCreateBy() {
        return createBy;
    }

    public void setCreateBy(SysEmployee createBy) {
        this.createBy = createBy;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public DictEntry getReportType() {
        return reportType;
    }

    public void setReportType(DictEntry reportType) {
        this.reportType = reportType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Set<ManageReportRelaRisk> getManageReportRelaRisk() {
        return manageReportRelaRisk;
    }

    public void setManageReportRelaRisk(Set<ManageReportRelaRisk> manageReportRelaRisk) {
        this.manageReportRelaRisk = manageReportRelaRisk;
    }

    public RiskAssessPlan getRiskAssessPlan() {
        return riskAssessPlan;
    }

    public void setRiskAssessPlan(RiskAssessPlan riskAssessPlan) {
        this.riskAssessPlan = riskAssessPlan;
    }

    public SysOrganization getCompany() {
        return company;
    }

    public void setCompany(SysOrganization company) {
        this.company = company;
    }
    
}

