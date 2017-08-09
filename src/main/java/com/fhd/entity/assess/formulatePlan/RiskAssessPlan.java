package com.fhd.entity.assess.formulatePlan;

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

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 评估计划实体
 * @author 王再冉
 */
@Entity
@Table(name = "T_RM_RISK_ASSESS_PLAN") 
public class RiskAssessPlan extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 计划名称
	 */
	@Column(name = "PLAN_NAME")
	private String planName;
	/**
	 * 计划编号
	 */
	@Column(name = "PLAN_CODE")
	private String planCode;
	/**
	 * 模板类型
	 */
	@Column(name = "TEMPLATE_TYPE")
	private String templateType;
	/**
	 * 有效性验证
	 */
	@Column(name = "EFFECTIVE_VALID")
	private String effective;
	/**
	 * 开始日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BEGIN_DATE")
	private Date beginDate;
	/**
	 * 结束日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;
	/**
	 * 联系人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTACT_PERSON")
	private SysEmployee contactPerson;
	/**
	 * 负责人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESPONSIBLE_PERSON")
	private SysEmployee responsPerson;
	/**
	 * 采集频率
	 */
	@Column(name = "COLLECT_RATE")
	private String collectRate;
	/**
	 * 工作类型 
	 */
	@Column(name = "WORK_TYPE")
	private String workType;
	/**
	 * 评估范围类型
	 *//*
	@Column(name = "RANGE_TYPE")
	private String rangeType;*/
	/**
	 * 范围要求
	 */
	@Column(name = "RANGE_REQUIRE")
	private String rangeReq;
	/**
	 * 状态
	 */
	@Column(name = "STATUS")
	private String status;
	/**
	 * 处理状态
	 */
	@Column(name = "DEAL_STATUS")
	private String dealStatus;
	/**
	 * 工作目标
	 */
	@Column(name = "WORK_TARG")
	private String workTage;
	/**
	 * 公司ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	/**
	 * 模板ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEMP_ID")
	private Template template;
	/**
	 * 评估计划创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATTIME")
	private Date planCreatTime;
	
	/**
     * 应急方案类型
     */
    @Column(name = "CONTINGENCY_TYPE")
    private String contingencyType;
	
	
	/**
	 * 模板类型：
	 * riskWorkFlow：风险评估计划
	 * responsePlan：风险应对计划
	 * riskIdentify：风险辨识计划
	 * riskContingencyPlanTotal:应急预案
	 */
	@Column(name = "PLAN_TYPE")
	private String planType;
	/**
	 * 分库标志
	 */
	@Column(name = "SCHM")
	private String schm;

	/**
	 * 创建部门 部门分库时使用
	 */
	@Column(name = "CREATE_ORG")
	private String createOrg;
	
	public Date getCheckBeginDate() {
		return checkBeginDate;
	}

	public void setCheckBeginDate(Date checkBeginDate) {
		this.checkBeginDate = checkBeginDate;
	}


	/**
	 * 考核开始日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHECK_BEGIN_DATE")
	private Date checkBeginDate;

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}


	/**
	 * 考核周期
	 */
	@Column(name = "CHECK_TYPE")
	private String checkType;
	
	public RiskAssessPlan() {
	}
	
	public RiskAssessPlan(String id) {
		 this.setId(id);
	}
	
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	public String getEffective() {
		return effective;
	}
	public void setEffective(String effective) {
		this.effective = effective;
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
	
	public SysEmployee getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(SysEmployee contactPerson) {
		this.contactPerson = contactPerson;
	}
	public SysEmployee getResponsPerson() {
		return responsPerson;
	}
	public void setResponsPerson(SysEmployee responsPerson) {
		this.responsPerson = responsPerson;
	}
	public SysOrganization getCompany() {
		return company;
	}
	public void setCompany(SysOrganization company) {
		this.company = company;
	}
	public String getCollectRate() {
		return collectRate;
	}
	public void setCollectRate(String collectRate) {
		this.collectRate = collectRate;
	}
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
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
	public String getWorkTage() {
		return workTage;
	}
	public void setWorkTage(String workTage) {
		this.workTage = workTage;
	}
	public SysOrganization getSysOrganization() {
		return company;
	}
	public void setSysOrganization(SysOrganization sysOrganization) {
		this.company = sysOrganization;
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public String getPlanCode() {
		return planCode;
	}
	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}
	public String getRangeReq() {
		return rangeReq;
	}
	public void setRangeReq(String rangeReq) {
		this.rangeReq = rangeReq;
	}
	public Date getPlanCreatTime() {
		return planCreatTime;
	}
	public void setPlanCreatTime(Date planCreatTime) {
		this.planCreatTime = planCreatTime;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

    public String getContingencyType() {
        return contingencyType;
    }

    public void setContingencyType(String contingencyType) {
        this.contingencyType = contingencyType;
    }

	public String getSchm() {
		return schm;
	}

	public void setSchm(String schm) {
		this.schm = schm;
	}

	public String getCreateOrg() {
		return createOrg;
	}

	public void setCreateOrg(String createOrg) {
		this.createOrg = createOrg;
	}
	
	
}
