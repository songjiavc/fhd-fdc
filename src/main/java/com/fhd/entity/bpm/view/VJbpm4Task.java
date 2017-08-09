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
	"		JBPM4_TASK.DBID_,JBPM4_HIST_PROCINST.DBID_ JBPM4HISTPROCINSTID,JBPM4_HIST_PROCINST.ENDACTIVITY_,	T_JBPM_PROCESS_DEFINE_DEPLOY.DIS_NAME,	T_JBPM_BUSINESS_WORKFLOW.PROCESS_INSTANCE_ID,	JBPM4_TASK.ACTIVITY_NAME_,	T_JBPM_BUSINESS_WORKFLOW.BUSINESS_ID,	T_JBPM_BUSINESS_WORKFLOW.BUSINESS_NAME,	JBPM4_TASK.CREATE_,	JBPM4_TASK.FORM_,	T_JBPM_BUSINESS_WORKFLOW.RATE,JBPM4_TASK.EXECUTION_ID_,JBPM4_TASK.ASSIGNEE_ ASSIGNEEID"+
	"	FROM"+
	"		JBPM4_TASK"+
	"	LEFT JOIN JBPM4_EXECUTION ON JBPM4_EXECUTION.DBID_ = JBPM4_TASK.EXECUTION_"+
	"	LEFT JOIN JBPM4_EXECUTION JBPM4_EXECUTION1 ON JBPM4_EXECUTION1.SUBPROCINST_ = JBPM4_EXECUTION.INSTANCE_"+
	"	LEFT JOIN JBPM4_HIST_PROCINST ON JBPM4_HIST_PROCINST.DBID_=JBPM4_EXECUTION1.INSTANCE_ OR JBPM4_EXECUTION1.INSTANCE_ IS NULL AND JBPM4_HIST_PROCINST.DBID_ = JBPM4_TASK.PROCINST_"+
	"	LEFT JOIN T_JBPM_BUSINESS_WORKFLOW ON T_JBPM_BUSINESS_WORKFLOW.PROCESS_INSTANCE_ID=JBPM4_HIST_PROCINST.ID_"+
	"	LEFT JOIN JBPM4_DEPLOYPROP ON JBPM4_DEPLOYPROP.STRINGVAL_=JBPM4_HIST_PROCINST.PROCDEFID_ AND JBPM4_DEPLOYPROP.KEY_='PDID'"+
	"	LEFT JOIN JBPM4_DEPLOYMENT ON JBPM4_DEPLOYMENT.DBID_=JBPM4_DEPLOYPROP.DEPLOYMENT_"+
	"	LEFT JOIN T_JBPM_PROCESS_DEFINE_DEPLOY ON T_JBPM_PROCESS_DEFINE_DEPLOY.ID=JBPM4_DEPLOYMENT.NAME_"
)
@Synchronize("JBPM4_TASK")
public class VJbpm4Task implements Serializable {

	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = 229451137261298919L;

	@Id
	@Column(name = "DBID_")
	private Long id;
	
	@Column(name = "JBPM4HISTPROCINSTID")
	private Long jbpm4HistProcinstId;
	
	@Column(name = "ENDACTIVITY_")
	private String endactivity;
	
	@Column(name = "DIS_NAME")
	private String disName;
	
	@Column(name = "ACTIVITY_NAME_")
	private String activityName;
	
	@Column(name = "BUSINESS_ID")
	private String businessId;
	
	@Column(name = "BUSINESS_NAME")
	private String businessName;
	
	@Column(name = "ASSIGNEEID")
	private String assigneeId;
	
	@Column(name = "CREATE_")
	private Date create;

	@Column(name = "FORM_")
	private String form;
	
	@Column(name = "RATE")
	private String rate;
	
	@Column(name = "EXECUTION_ID_")
	private String executionId;

	//待办执行过程中返回每个待办任务对应的流程实例所对应的计划的schm
	@Column(name = "schm")
	private String schm;
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getJbpm4HistProcinstId() {
		return jbpm4HistProcinstId;
	}

	public void setJbpm4HistProcinstId(Long jbpm4HistProcinstId) {
		this.jbpm4HistProcinstId = jbpm4HistProcinstId;
	}

	public String getEndactivity() {
		return endactivity;
	}

	public void setEndactivity(String endactivity) {
		this.endactivity = endactivity;
	}

	public String getDisName() {
		return disName;
	}

	public void setDisName(String disName) {
		this.disName = disName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}

	public Date getCreate() {
		return create;
	}

	public void setCreate(Date create) {
		this.create = create;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getSchm() {
		return schm;
	}

	public void setSchm(String schm) {
		this.schm = schm;
	}

}
