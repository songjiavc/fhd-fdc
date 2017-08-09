
package com.fhd.entity.assess.formulatePlan;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Entity
@Table(name = "T_RM_RISK_ORG_RELATION_TEMP")
public class RiskOrgRelationTemp extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 2365875573835805410L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RISK_ID")
	private Risk risk;
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
	 * 人员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	private SysEmployee emp;
	
	/**
	 * 打分对象
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBJECT_ID")
	private RiskScoreObject riskScoreObject;
	public Risk getRisk() {
		return risk;
	}
	public void setRisk(Risk risk) {
		this.risk = risk;
	}
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
	public RiskScoreObject getRiskScoreObject() {
		return riskScoreObject;
	}
	public void setRiskScoreObject(RiskScoreObject riskScoreObject) {
		this.riskScoreObject = riskScoreObject;
	}
	public SysEmployee getEmp() {
		return emp;
	}
	public void setEmp(SysEmployee emp) {
		this.emp = emp;
	}
	
	
	
}