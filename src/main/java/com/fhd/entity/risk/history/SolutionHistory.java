package com.fhd.entity.risk.history;

import com.fhd.entity.base.IdEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 风险应对版本实体
 * Created by wzr on 2017/4/1.
 */
@Entity
@Table(name = "T_SOLUTION_HISTORY")
public class SolutionHistory extends IdEntity implements Serializable {

    /**
     * 风险应对所属版本
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERSION_ID")
    private NewRiskVersion versionId;

    /**
     * 风险id
     */
    @Column(name = "RISK_ID")
    private String riskId;

    /**
     * 风险应对id
     */
    @Column(name = "SOLUTION_ID")
    private String solutionId;

    /**
     * 风险应对名称
     */
    @Column(name = "SOLUTION_NAME")
    private String solutionName;

    /**
     * 风险应对编号
     */
    @Column(name = "SOLUTION_CODE")
    private String solutionCode;

    /**
     * 应对策略
     */
    @Column(name = "STATEGY_NAME")
    private String stategyName;

    /**
     * 责任部门/人名称
     */
    @Column(name = "MAIN_DEPT_NAME")
    private String mainDeptName;

    /**
     * 应对预计开始时间
     */
    @Column(name = "BEGIN_DATE")
    private Date beginDate;

    /**
     * 应对预计结束时间
     */
    @Column(name = "END_DATE")
    private Date endDate;

    /**
     * 应对方案描述
     */
    @Column(name = "EDESC")
    private String desc;

    /**
     * 完成标识
     */
    @Column(name = "COMPLETE_INDICATOR")
    private String completeIndicator;

    /**
     * 预计成本
     */
    @Column(name = "COST")
    private String cost;

    /**
     * 预计收效
     */
    @Column(name = "INCOME")
    private String income;

    /**
     * 附件
     */
    @Column(name = "FILE_ID")
    private String fileId;

    public NewRiskVersion getVersionId() {
        return versionId;
    }

    public void setVersionId(NewRiskVersion versionId) {
        this.versionId = versionId;
    }

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(String solutionId) {
        this.solutionId = solutionId;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getSolutionCode() {
        return solutionCode;
    }

    public void setSolutionCode(String solutionCode) {
        this.solutionCode = solutionCode;
    }

    public String getStategyName() {
        return stategyName;
    }

    public void setStategyName(String stategyName) {
        this.stategyName = stategyName;
    }

    public String getMainDeptName() {
        return mainDeptName;
    }

    public void setMainDeptName(String mainDeptName) {
        this.mainDeptName = mainDeptName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCompleteIndicator() {
        return completeIndicator;
    }

    public void setCompleteIndicator(String completeIndicator) {
        this.completeIndicator = completeIndicator;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
