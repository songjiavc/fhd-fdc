package com.fhd.entity.check.yearcheck.plan;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;


@Entity
@Table(name="t_rm_check_year_plan")
public class YearCheckPlan extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 计划名称
	 */
	@Column(name="NAME")
	private String name;

	/**
	 * 计划编号
	 */
	@Column(name="PLAN_CODE")
	private String planCode;

	/**
	 * 计划开始时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BEGIN_DATE")
	private Date beginDate;

	/**
	 * 计划结束时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="END_DATE")
	private Date endDate;

	/**
	 * 联系人
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="CONTACT_PERSON")
	private SysEmployee contactPerson;

	/**
	 * 负责人
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="RESPONSIBLE_PERSON")
	private SysEmployee responsiblePerson;

	/**
	 * 计划状态
	 */
	@Column(name="STATUS")
	private String status;

	/**
	 * 计划处理状态
	 */
	@Column(name="DEAL_STATUS")
	private String dealStatus;

	/**
	 * 工作目标
	 */
	@Column(name="WORK_TARG")
	private String workTarg;

	/**
	 * 范围要求
	 */
	@Column(name="RANGE_REQUIRE")
	private String rangeRequire;

	/**
	 * 计划创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATTIME")
	private Date createTime;

	/**
	 * 计划创建部门
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="CREATE_ORG")
	private SysOrganization createOrg;

	/**
	 * 计划创建员工
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="CREATE_EMP")
	private SysEmployee createEmp;
	
	
	//form 字段
	@Transient
	private String beginDateStr;
	@Transient
	private String endDateStr;
	@Transient
	private String contactName;
	@Transient
	private String responsName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}


	public SysEmployee getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(SysEmployee contactPerson) {
		this.contactPerson = contactPerson;
	}

	public SysEmployee getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(SysEmployee responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}



	public String getRangeRequire() {
		return rangeRequire;
	}

	public void setRangeRequire(String rangeRequire) {
		this.rangeRequire = rangeRequire;
	}

	
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public SysOrganization getCreateOrg() {
		return createOrg;
	}

	public void setCreateOrg(SysOrganization createOrg) {
		this.createOrg = createOrg;
	}

	public SysEmployee getCreateEmp() {
		return createEmp;
	}

	public void setCreateEmp(SysEmployee createEmp) {
		this.createEmp = createEmp;
	}

	public String getWorkTarg() {
		return workTarg;
	}

	public void setWorkTarg(String workTarg) {
		this.workTarg = workTarg;
	}

	public String getBeginDateStr() {
		return beginDateStr;
	}

	public void setBeginDateStr(String beginDateStr) {
		this.beginDateStr = beginDateStr;
	}

	public String getEndDateStr() {
		return endDateStr;
	}

	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getResponsName() {
		return responsName;
	}

	public void setResponsName(String responsName) {
		this.responsName = responsName;
	}
}
