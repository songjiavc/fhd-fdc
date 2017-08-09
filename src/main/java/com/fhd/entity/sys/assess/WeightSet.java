package com.fhd.entity.sys.assess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Entity
@Table(name = "T_SYS_WEIGHT_SET")
public class WeightSet extends IdEntity implements Serializable {

	/**
	 * serialVersionUID:ENDO
	 *
	 * @author 金鹏祥
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * 责任部门权重
	 *
	 * @author 金鹏祥
	 * @since  fhd　Ver 1.1
	 */
	@Column(name = "DUTY_DEPT_WEIGHT", nullable = false)
	private String objectKey;
	
	/**
	 * 相关部门权重
	 *
	 * @author 金鹏祥
	 * @since  fhd　Ver 1.1
	 */
	@Column(name = "RELATED_DEPT_WEIGHT", nullable = false)
	private String relatedDeptWeight;
	
	/**
	 * 辅助部门权重
	 *
	 * @author 金鹏祥
	 * @since  fed　Ver 1.1
	 */
	@Column(name = "ASSIST_DEPT_WEIGHT", nullable = false)
	private String assistDeptWeight;
	
	/**
	 * 参与部门权重
	 *
	 * @author 金鹏祥
	 * @since  fhd　Ver 1.1
	 */
	/*@Column(name = "PARTAKE_DEPT_WEIGHT", nullable = false)
	private String partakeDeptWeight;*/
	
	/**
	 * 领导权重
	 *
	 * @author 金鹏祥
	 * @since  fhd　Ver 1.1
	 */
	@Column(name = "LEAD_WEIGHT", nullable = false)
	private String leadWeight;
	
	/**
	 * 员工权重
	 *
	 * @author 金鹏祥
	 * @since  fhd　Ver 1.1
	 */
	@Column(name = "STAFF_WEIGHT", nullable = false)
	private String staffWeight;
	/**
	 * 告警方案
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALARM_SCENARIO")
	private AlarmPlan alarmScenario;

	/**
	 * 角色权重
	 */
	@Column(name = "ROLE_WEIGHT")
	private String roleWeight;
	
	/**
	 * 所属公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	
	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getRelatedDeptWeight() {
		return relatedDeptWeight;
	}

	public void setRelatedDeptWeight(String relatedDeptWeight) {
		this.relatedDeptWeight = relatedDeptWeight;
	}

	public String getAssistDeptWeight() {
		return assistDeptWeight;
	}

	public void setAssistDeptWeight(String assistDeptWeight) {
		this.assistDeptWeight = assistDeptWeight;
	}

	public String getLeadWeight() {
		return leadWeight;
	}

	public void setLeadWeight(String leadWeight) {
		this.leadWeight = leadWeight;
	}

	public String getStaffWeight() {
		return staffWeight;
	}

	public void setStaffWeight(String staffWeight) {
		this.staffWeight = staffWeight;
	}

	public AlarmPlan getAlarmScenario() {
		return alarmScenario;
	}

	public void setAlarmScenario(AlarmPlan alarmScenario) {
		this.alarmScenario = alarmScenario;
	}

	public String getRoleWeight() {
		return roleWeight;
	}

	public void setRoleWeight(String roleWeight) {
		this.roleWeight = roleWeight;
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}
}