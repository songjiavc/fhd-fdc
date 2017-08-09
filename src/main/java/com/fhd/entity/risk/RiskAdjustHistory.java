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
 * 风险评估记录
 */
@Entity
@Table(name = "T_RM_RISK_ADJUST_HISTORY") 
public class RiskAdjustHistory extends IdEntity implements Serializable{
	private static final long serialVersionUID = -560056895428600775L;
	
	/**
	 * 风险
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RISK_ID")
	private Risk risk;
	
	/**
	 * 风险水平
	 */
	@Column(name = "RISK_STATUS")
	private Double status;
	
	/**
	 * 影响程度
	 */
	@Column(name = "IMPACTS")
	private Double impacts;
	
	/**
	 * 发生可能性
	 */
	@Column(name = "PROBABILITY")
	private Double probability;
	
	/**
	 * 风险水平风险管理迫切性
	 */
	@Column(name = "MANAGEMENT_URGENCY")
	private Double managementUrgency;
	
	/**
	 * 来源
	 * 0:评估计划
	 * 1:手动输入
	 * 2:评估计划的历史记录修改后会改为此状态，现在数据库中此状态的数据都为垃圾数据
	 * 3:公式定义
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
	 * 评估状态
	 */
	@Column(name = "ASSESSEMENT_STATUS")
	private String assessementStatus;
	
	/**
	 * 锁定状态
	 */
	@Column(name = "LOCK_STATUS")
	private String lockStatus;

	/**
	 * 所属公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	
	/**
	 * 评估计划ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESS_PLAN_ID")
	private RiskAssessPlan riskAssessPlan;
	
	/**
	 * 评估模板
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEMPLATE_ID")
	private Template template;
	
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

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}

	public RiskAdjustHistory(){
		
	}
	
	public RiskAdjustHistory(String id){
		super.setId(id);
	}

	public Risk getRisk() {
		return risk;
	}

	public void setRisk(Risk risk) {
		this.risk = risk;
	}

	public Double getStatus() {
		return status;
	}

	public void setStatus(Double status) {
		this.status = status;
	}

	public Double getImpacts() {
		return impacts;
	}

	public void setImpacts(Double impacts) {
		this.impacts = impacts;
	}

	public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}

	public Double getManagementUrgency() {
		return managementUrgency;
	}

	public void setManagementUrgency(Double managementUrgency) {
		this.managementUrgency = managementUrgency;
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

	public String getAssessementStatus() {
		return assessementStatus;
	}

	public void setAssessementStatus(String assessementStatus) {
		this.assessementStatus = assessementStatus;
	}

	public String getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(String lockStatus) {
		this.lockStatus = lockStatus;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public String getCalculateFormula() {
		return calculateFormula;
	}

	public void setCalculateFormula(String calculateFormula) {
		this.calculateFormula = calculateFormula;
	}
}