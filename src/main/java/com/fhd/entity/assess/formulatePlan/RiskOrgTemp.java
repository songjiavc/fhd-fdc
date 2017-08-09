/**
 * RiskOrg.java
 * com.fhd.risk.entity
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-2 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/
/**
 * RiskOrg.java
 * com.fhd.risk.entity
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-2        金鹏祥
 *
 * Copyright (c) 2012, FirstHuida All Rights Reserved.
*/


package com.fhd.entity.assess.formulatePlan;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.SysPosition;

/**
 * 风险辨识流程中针对主责部门和相关部门的修改不影响风险库中的风险与部门之间的关系
 * @author Jzq
 * @time 2017年5月5日10:57:43
 */
@Entity

@Table(name = "T_RM_RISK_ORG_TEMP")
public class RiskOrgTemp extends IdEntity implements Serializable{
	private static final long serialVersionUID = 2365875573835805410L;

//	/**
//	 * 风险事件
//	 */
//	@Transient
//	private Risk risk;
//	
//	
//	@JoinColumn(name = "RISK_ID")
//	private String riskId;
	
	
	/**
	 * 类型 M：主要风险类别；A：相关风险类别
	 */
	@Column(name = "ETYPE")
	private String type;
	
	/**
	 * 机构
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID")
	private SysOrganization sysOrganization;
	
	/**
	 * 岗位
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POSITION_ID")
	private SysPosition sysPosition;
	
	/**
	 * 人员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	private SysEmployee emp;
	
	/**
	 * 计划id
	 */
	@Column(name = "PLAN_ID")
	private String planId;
	
	/**
	 * 辨识过程中的风险事件状态
	 */
	@Column(name = "STATUS")
	private String status;
	

	/**
	 * 风险事件
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBJECT_ID")
	private RiskScoreObject riskScoreObject;

	public RiskOrgTemp(String scoreDeptId) {
		this.setId(scoreDeptId);
	}

	public RiskOrgTemp(){}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SysOrganization getSysOrganization() {
		return sysOrganization;
	}

	public void setSysOrganization(SysOrganization sysOrganization) {
		this.sysOrganization = sysOrganization;
	}

	public SysPosition getSysPosition() {
		return sysPosition;
	}

	public void setSysPosition(SysPosition sysPosition) {
		this.sysPosition = sysPosition;
	}

	public SysEmployee getEmp() {
		return emp;
	}

	public void setEmp(SysEmployee emp) {
		this.emp = emp;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public RiskScoreObject getRiskScoreObject() {
		return riskScoreObject;
	}

	public void setRiskScoreObject(RiskScoreObject riskScoreObject) {
		this.riskScoreObject = riskScoreObject;
	}
	
	
}