package com.fhd.entity.risk;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 部分评估记录
 */
@Entity
@Table(name = "T_RM_ORG_ADJUST_HISTORY") 
public class OrgAdjustHistory extends IdEntity implements Serializable{
	private static final long serialVersionUID = -560056895428600775L;
	
	/**
	 * 组织机构
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID")
	private SysOrganization organization;
	
	/**
	 * 调整方式
	 */
	@Column(name = "ADJUST_TYPE")
	private String adjustType;
	
	/**
	 * 更改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ADJUST_TIME")
	private Date adjustTime;
	
	/**
	 * 是否最新记录趋势
	 */
	@Column(name = "IS_LATEST")
	private String isLatest;
	
	/**
	 * 状态
	 */
	@Column(name = "ASSESSEMENT_STATUS")
	private String assessementStatus;
	
	/**
	 * 趋势
	 */
	@Column(name = "ETREND")
	private String etrend;
	
	/**
	 * 时间区间维
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TIME_PERIOD_ID")
	private TimePeriod timePeriod;
	
	/**
	 * 风险水平
	 */
	@Column(name = "RISK_STATUS")
	private Double riskStatus;
	
	/**公司ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization companyId;
	
	/**
	 * 评估计划ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESS_PLAN_ID")
	private RiskAssessPlan riskAssessPlan;
	
	/**
	 * 计算公式
	 */
	@Column(name = "calculate_formula")
	private String calculateFormula;
	
	public RiskAssessPlan getRiskAssessPlan() {
		return riskAssessPlan;
	}

	public void setRiskAssessPlan(RiskAssessPlan riskAssessPlan) {
		this.riskAssessPlan = riskAssessPlan;
	}
	
	public SysOrganization getCompanyId() {
		return companyId;
	}

	public void setCompanyId(SysOrganization companyId) {
		this.companyId = companyId;
	}

	public Double getRiskStatus() {
		return riskStatus;
	}

	public void setRiskStatus(Double riskStatus) {
		this.riskStatus = riskStatus;
	}
	
	public OrgAdjustHistory(){
		
	}
	
	public OrgAdjustHistory(String id){
		super.setId(id);
	}

	public SysOrganization getOrganization() {
		return organization;
	}

	public void setOrganization(SysOrganization organization) {
		this.organization = organization;
	}

	public String getAdjustType() {
		return adjustType;
	}

	public void setAdjustType(String adjustType) {
		this.adjustType = adjustType;
	}

	public Date getAdjustTime() {
		return adjustTime;
	}

	public void setAdjustTime(Date adjustTime) {
		this.adjustTime = adjustTime;
	}

	public String getIsLatest() {
		return isLatest;
	}

	public void setIsLatest(String isLatest) {
		this.isLatest = isLatest;
	}

	public String getAssessementStatus() {
		return assessementStatus;
	}

	public void setAssessementStatus(String assessementStatus) {
		this.assessementStatus = assessementStatus;
	}

	public String getEtrend() {
		return etrend;
	}

	public void setEtrend(String etrend) {
		this.etrend = etrend;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public String getCalculateFormula() {
		return calculateFormula;
	}

	public void setCalculateFormula(String calculateFormula) {
		this.calculateFormula = calculateFormula;
	}
}