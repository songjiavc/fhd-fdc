package com.fhd.entity.bpm.view;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Subselect(
"	SELECT"+
"		JBPM4_HIST_ACTINST.DBID_,"+
"		JBPM4_HIST_ACTINST.ACTIVITY_NAME_,"+
"		JBPM4_HIST_ACTINST.EXECUTION_,"+
"		JBPM4_HIST_ACTINST.DBVERSION_,"+
"		JBPM4_HIST_ACTINST.START_,"+
"		JBPM4_HIST_ACTINST.END_,"+
"		JBPM4_HIST_ACTINST.HPROCI_,"+
"		JBPM4_HIST_ACTINST.HTASK_,"+
"		JBPM4_HIST_ACTINST.TYPE_,"+
"		JBPM4_HIST_ACTINST.TRANSITION_,"+
"		JBPM4_HIST_PROCINST.KEY_ JBPM4_HIST_PROCINST_KEY_,"+
"		JBPM4_HIST_TASK.ASSIGNEE_ JBPM4_HIST_TASK_ASSIGNEE_,"+
"		T_JBPM_EXAMINE_APPROVE_IDEA.EA_CONTENT EA_CONTENT,"+
"		T_SYS_EMPLOYEE.EMP_CODE T_SYS_EMPLOYEE_EMP_CODE,"+
"		T_SYS_EMPLOYEE.REAL_NAME T_SYS_EMPLOYEE_REAL_NAME,"+
"		COMPANY.ID COMPANY_ID,"+
"		COMPANY.ORG_NAME COMPANY_NAME,"+
"		ORG.ID ORG_ID,"+
"		ORG.ORG_NAME ORG_NAME"+
"	FROM JBPM4_HIST_ACTINST"+
"	LEFT JOIN JBPM4_HIST_PROCINST ON JBPM4_HIST_PROCINST.DBID_=JBPM4_HIST_ACTINST.HPROCI_"+
"	LEFT JOIN JBPM4_HIST_TASK ON JBPM4_HIST_TASK.DBID_ = JBPM4_HIST_ACTINST.HTASK_"+
"	LEFT JOIN T_JBPM_EXAMINE_APPROVE_IDEA ON T_JBPM_EXAMINE_APPROVE_IDEA.TASK_ID=JBPM4_HIST_TASK.DBID_"+
"	LEFT JOIN T_SYS_EMPLOYEE ON T_SYS_EMPLOYEE.ID = JBPM4_HIST_TASK.ASSIGNEE_"+
"	LEFT JOIN T_SYS_EMP_ORG ON T_SYS_EMP_ORG.EMP_ID=T_SYS_EMPLOYEE.ID AND T_SYS_EMP_ORG.ISMAIN=1"+
"	LEFT JOIN T_SYS_ORGANIZATION ORG ON ORG.ID=T_SYS_EMP_ORG.ORG_ID"+
"	LEFT JOIN T_SYS_ORGANIZATION COMPANY ON COMPANY.ID = T_SYS_EMPLOYEE.ORG_ID"
)
@Synchronize({"jbpm4_hist_procinst","JBPM4_HIST_TASK"})
public class VJbpmHistActinst implements Serializable{
	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = 4065561587132470579L;

	@Id
	@Column(name = "DBID_")
	private Long id;
	
	/**
	 * activityName:节点名称
	 */
	@Column(name = "ACTIVITY_NAME_")
	private String activityName;
	
	/**
	 * executionId:执行ID
	 */
	@Column(name = "EXECUTION_")
	private String executionId;
	
	/**
	 * 执行状态：0-未执行，1-已执行
	 */
	@Column(name = "DBVERSION_")
	private Long dbversion;
	
	
	@Column(name = "START_")
	private Date start;

	@Column(name = "END_")
	private Date end;
	
	@Column(name = "HPROCI_")
	private Long jbpmHistProcinstId;
	
	@Column(name = "HTASK_")
	private Long jbpmHistTaskId;
	
	@Column(name = "TYPE_")
	private String type;
	
	@Column(name = "TRANSITION_")
	private String transition;
	
	@Column(name = "JBPM4_HIST_PROCINST_KEY_")
	private String jbpmHistProcinstKey;
	
	@Column(name = "EA_CONTENT")
	private String eaContent;
	
	@Column(name = "JBPM4_HIST_TASK_ASSIGNEE_")
	private String assigneeId;
	
	@Column(name = "T_SYS_EMPLOYEE_EMP_CODE")
	private String assigneeCode;
	
	@Column(name = "T_SYS_EMPLOYEE_REAL_NAME")
	private String assigneeRealName;
	
	@Column(name = "COMPANY_ID")
	private String assigneeCompanyId;
	
	@Column(name = "COMPANY_NAME")
	private String assigneeCompanyName;
	
	@Column(name = "ORG_ID")
	private String assigneeOrgId;
	
	@Column(name = "ORG_NAME")
	private String assigneeOrgName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public Long getDbversion() {
		return dbversion;
	}

	public void setDbversion(Long dbversion) {
		this.dbversion = dbversion;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Long getJbpmHistProcinstId() {
		return jbpmHistProcinstId;
	}

	public void setJbpmHistProcinstId(Long jbpmHistProcinstId) {
		this.jbpmHistProcinstId = jbpmHistProcinstId;
	}

	public Long getJbpmHistTaskId() {
		return jbpmHistTaskId;
	}

	public void setJbpmHistTaskId(Long jbpmHistTaskId) {
		this.jbpmHistTaskId = jbpmHistTaskId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getJbpmHistProcinstKey() {
		return jbpmHistProcinstKey;
	}

	public void setJbpmHistProcinstKey(String jbpmHistProcinstKey) {
		this.jbpmHistProcinstKey = jbpmHistProcinstKey;
	}

	public String getEaContent() {
		return eaContent;
	}

	public void setEaContent(String eaContent) {
		this.eaContent = eaContent;
	}

	public String getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}

	public String getAssigneeCode() {
		return assigneeCode;
	}

	public void setAssigneeCode(String assigneeCode) {
		this.assigneeCode = assigneeCode;
	}

	public String getAssigneeRealName() {
		return assigneeRealName;
	}

	public void setAssigneeRealName(String assigneeRealName) {
		this.assigneeRealName = assigneeRealName;
	}

	public String getAssigneeCompanyId() {
		return assigneeCompanyId;
	}

	public void setAssigneeCompanyId(String assigneeCompanyId) {
		this.assigneeCompanyId = assigneeCompanyId;
	}

	public String getAssigneeCompanyName() {
		return assigneeCompanyName;
	}

	public void setAssigneeCompanyName(String assigneeCompanyName) {
		this.assigneeCompanyName = assigneeCompanyName;
	}

	public String getAssigneeOrgId() {
		return assigneeOrgId;
	}

	public void setAssigneeOrgId(String assigneeOrgId) {
		this.assigneeOrgId = assigneeOrgId;
	}

	public String getAssigneeOrgName() {
		return assigneeOrgName;
	}

	public void setAssigneeOrgName(String assigneeOrgName) {
		this.assigneeOrgName = assigneeOrgName;
	}

	public String getTransition() {
		return transition;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}
}
