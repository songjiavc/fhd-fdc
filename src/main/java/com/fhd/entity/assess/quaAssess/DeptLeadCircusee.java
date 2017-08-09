package com.fhd.entity.assess.quaAssess;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;


/**
 * 部门领导意见打分对象关联
 * */

@Entity
@Table(name = "t_rm_dept_lead_circusee") 
public class DeptLeadCircusee extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/**打分对象ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCORE_OBJECT_ID")
	private RiskScoreObject scoreObjectId;
	
	/**用户ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPT_LEAD_EMP_ID")
	private SysEmployee deptLeadEmpId;
	
	/**评估ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESS_PLAN_ID")
	private RiskAssessPlan riskAssessPlan;

	public SysEmployee getDeptLeadEmpId() {
		return deptLeadEmpId;
	}

	public void setDeptLeadEmpId(SysEmployee deptLeadEmpId) {
		this.deptLeadEmpId = deptLeadEmpId;
	}

	public RiskAssessPlan getRiskAssessPlan() {
		return riskAssessPlan;
	}

	public void setRiskAssessPlan(RiskAssessPlan riskAssessPlan) {
		this.riskAssessPlan = riskAssessPlan;
	}

	public RiskScoreObject getScoreObjectId() {
		return scoreObjectId;
	}

	public void setScoreObjectId(RiskScoreObject scoreObjectId) {
		this.scoreObjectId = scoreObjectId;
	}
}