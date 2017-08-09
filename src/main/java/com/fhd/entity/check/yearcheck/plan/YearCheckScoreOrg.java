package com.fhd.entity.check.yearcheck.plan;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

@Table(name="t_rm_check_year_score_org")
@Entity
public class YearCheckScoreOrg extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="CHECK_PROJECT_NAME")
	private String checkProjectName;
	
	@Column(name="CHECK_PROJECT_SCORE")
	private int checkProjectScore;
	
	@Column(name="CHECK_COMMENT_NAME")
	private String checkCommenttName;
	
	@Column(name="CHECK_DETAIL_NAME")
	private String checkDetailName;
	
	@Column(name="CHECK_DETAIL_AUTO_SUB")
	private int checkDetailAutoSub;
	
	@Column(name="CHECK_DETAIL_SCORE")
	private int checkDetailScore;
	
	@Column(name="CHECK_DETAIL_DESCRIBE")
	private String checkDetailDescribe;
	
	@Column(name="RISK_SCORE")
	private int riskScore;
	
	@Column(name="AUDIT_SCORE")
	private int auditScore;
	
	@Column(name="OWEN_SCORE")
	private int owenScore;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PLAN_ORG_ID")
	private YearCheckPlanOrg planOrgId;

	public String getCheckProjectName() {
		return checkProjectName;
	}

	public void setCheckProjectName(String checkProjectName) {
		this.checkProjectName = checkProjectName;
	}

	public int getCheckProjectScore() {
		return checkProjectScore;
	}

	public void setCheckProjectScore(int checkProjectScore) {
		this.checkProjectScore = checkProjectScore;
	}

	public String getCheckCommenttName() {
		return checkCommenttName;
	}

	public void setCheckCommenttName(String checkCommenttName) {
		this.checkCommenttName = checkCommenttName;
	}

	public String getCheckDetailName() {
		return checkDetailName;
	}

	public void setCheckDetailName(String checkDetailName) {
		this.checkDetailName = checkDetailName;
	}

	public int getCheckDetailAutoSub() {
		return checkDetailAutoSub;
	}

	public void setCheckDetailAutoSub(int checkDetailAutoSub) {
		this.checkDetailAutoSub = checkDetailAutoSub;
	}

	public int getCheckDetailScore() {
		return checkDetailScore;
	}

	public void setCheckDetailScore(int checkDetailScore) {
		this.checkDetailScore = checkDetailScore;
	}

	public int getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(int riskScore) {
		this.riskScore = riskScore;
	}

	public int getAuditScore() {
		return auditScore;
	}

	public void setAuditScore(int auditScore) {
		this.auditScore = auditScore;
	}

	public int getOwenScore() {
		return owenScore;
	}

	public void setOwenScore(int owenScore) {
		this.owenScore = owenScore;
	}

	public YearCheckPlanOrg getPlanOrgId() {
		return planOrgId;
	}

	public void setPlanOrgId(YearCheckPlanOrg planOrgId) {
		this.planOrgId = planOrgId;
	}

	public String getCheckDetailDescribe() {
		return checkDetailDescribe;
	}

	public void setCheckDetailDescribe(String checkDetailDescribe) {
		this.checkDetailDescribe = checkDetailDescribe;
	}

	
	

	
	
	
}
