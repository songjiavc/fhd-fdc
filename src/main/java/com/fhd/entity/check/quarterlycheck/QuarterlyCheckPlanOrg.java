package com.fhd.entity.check.quarterlycheck;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlan;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Entity
@Table(name="t_rm_check_quarterly_plan_org")
public class QuarterlyCheckPlanOrg extends IdEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PLAN_ID")
	private YearCheckPlan yearCheckPlan;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ORG_ID")
	private SysOrganization orgId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="EMP_ID")
	private SysEmployee empId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CHECKED_EMP_ID")
	private SysEmployee checkEmpId;
	
	@Column(name="PROMLEM")
	private String problem;

	public YearCheckPlan getYearCheckPlan() {
		return yearCheckPlan;
	}

	public void setYearCheckPlan(YearCheckPlan yearCheckPlan) {
		this.yearCheckPlan = yearCheckPlan;
	}

	public SysOrganization getOrgId() {
		return orgId;
	}

	public void setOrgId(SysOrganization orgId) {
		this.orgId = orgId;
	}

	public SysEmployee getEmpId() {
		return empId;
	}

	public void setEmpId(SysEmployee empId) {
		this.empId = empId;
	}

	public SysEmployee getCheckEmpId() {
		return checkEmpId;
	}

	public void setCheckEmpId(SysEmployee checkEmpId) {
		this.checkEmpId = checkEmpId;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}
	
	

}
