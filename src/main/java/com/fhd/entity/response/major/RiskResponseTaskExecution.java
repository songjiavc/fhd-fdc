package com.fhd.entity.response.major;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;

/**
 * 应对计划与重大风险、部门、应对人关系表
 * @author Jzq
 *
 */
@Entity
@Table(name="t_rm_response_task_execution")
public class RiskResponseTaskExecution extends IdEntity implements Serializable{

	private static final long serialVersionUID = -873770911671881889L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PLAN_RISK_RELA_ID")
	@NotFound(action=NotFoundAction.IGNORE)
	private RiskResponsePlanRiskRela planRiskRelaObj;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	@NotFound(action=NotFoundAction.IGNORE)
	private SysEmployee executeEmp;
	//应对人类别 普通员工or汇总人员
	@Column(name ="EMP_TYPE")
	@NotFound(action=NotFoundAction.IGNORE)
	private String empType;
	public RiskResponsePlanRiskRela getPlanRiskRelaObj() {
		return planRiskRelaObj;
	}
	public void setPlanRiskRelaObj(RiskResponsePlanRiskRela planRiskRelaObj) {
		this.planRiskRelaObj = planRiskRelaObj;
	}
	public SysEmployee getExecuteEmp() {
		return executeEmp;
	}
	public void setExecuteEmp(SysEmployee executeEmp) {
		this.executeEmp = executeEmp;
	}
	public String getEmpType() {
		return empType;
	}
	public void setEmpType(String empType) {
		this.empType = empType;
	}


	
	
	
}
