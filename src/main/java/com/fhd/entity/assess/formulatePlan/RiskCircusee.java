package com.fhd.entity.assess.formulatePlan;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
/**
 * 
 * @author 王再冉
 *
 */
@Entity
@Table(name = "T_RM_RISK_CIRCUSEE")
public class RiskCircusee extends IdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 评估计划ID 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESS_PLAN_ID")
	private RiskAssessPlan assessPlan;
	
	/**
	 * 部门ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID")
	private SysOrganization organization;
	/**
	 * 承办人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNDERTAKER")
	private SysEmployee underTaker;
	/**
	 * 审批人
	 */
	@OneToOne
	@JoinColumn(name = "APPROVER")
	private SysEmployee approver;
	public RiskAssessPlan getAssessPlan() {
		return assessPlan;
	}
	public void setAssessPlan(RiskAssessPlan assessPlan) {
		this.assessPlan = assessPlan;
	}
	public SysOrganization getOrganization() {
		return organization;
	}
	public void setOrganization(SysOrganization organization) {
		this.organization = organization;
	}
	public SysEmployee getUnderTaker() {
		return underTaker;
	}
	public void setUnderTaker(SysEmployee underTaker) {
		this.underTaker = underTaker;
	}
	public SysEmployee getApprover() {
		return approver;
	}
	public void setApprover(SysEmployee approver) {
		this.approver = approver;
	}
	
	

}
