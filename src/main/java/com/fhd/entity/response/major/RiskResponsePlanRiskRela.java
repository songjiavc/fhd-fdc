package com.fhd.entity.response.major;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 应对计划与重大风险、部门关系表
 * @author Jzq
 *
 */
@Entity
@Table(name="t_rm_response_plan_risk_rela")
public class RiskResponsePlanRiskRela extends IdEntity implements Serializable{


	private static final long serialVersionUID = -5728159237078929579L;
	//应对计划
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	@NotFound(action=NotFoundAction.IGNORE)
	private RiskAssessPlan plan;
	//重大风险
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "RISK_ID")
	private Risk majorRisk;
	//部门
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "DEPT_ID")
	@NotFound(action=NotFoundAction.IGNORE)
	private SysOrganization dept;
	//责任类型
	@Column(name = "DEPT_TYPE")
	private String deptType;
	//更新时间
	
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	//更新人
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "UPDATE_USER")
	@NotFound(action=NotFoundAction.IGNORE)
	private SysEmployee updateUser;
	
	public RiskAssessPlan getPlan() {
		return plan;
	}
	public void setPlan(RiskAssessPlan plan) {
		this.plan = plan;
	}
	public Risk getMajorRisk() {
		return majorRisk;
	}
	public void setMajorRisk(Risk majorRisk) {
		this.majorRisk = majorRisk;
	}
	public SysOrganization getDept() {
		return dept;
	}
	public void setDept(SysOrganization dept) {
		this.dept = dept;
	}
	public String getDeptType() {
		return deptType;
	}
	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public SysEmployee getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(SysEmployee updateUser) {
		this.updateUser = updateUser;
	}

	
	
}
