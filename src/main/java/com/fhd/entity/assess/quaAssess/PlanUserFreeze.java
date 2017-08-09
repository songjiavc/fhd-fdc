package com.fhd.entity.assess.quaAssess;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.st.Plan;

/**
 * 流程中不可更改人员表
 * 20170615
 * 郭鹏
 */

@Entity
@Table(name = "T_PLAN_USER_FREEZE") 
public class PlanUserFreeze  extends IdEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	/**员工ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private SysUser userId;
	
	/**计划ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	private Plan planId;

	/**计划中角色*/
	@JoinColumn(name = "ROLE_NAME")
	private String roleName;

	public SysUser getUserId() {
		return userId;
	}

	public void setUserId(SysUser userId) {
		this.userId = userId;
	}

	public Plan getPlanId() {
		return planId;
	}

	public void setPlanId(Plan planId) {
		this.planId = planId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	

	
}