package com.fhd.entity.sys.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Entity
@Table(name = "TMP_RISKS_EXCELDATA")
public class RiskFromExcel extends IdEntity implements Serializable{
	/**
	 *
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = 1279547061443491710L;
	
	/**
	 * 所属公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	/**
	 * 父级风险CODE
	 */
	@Column(name = "PARENT_CODE")
	private String parentCode;
	/**
	 * 父级风险Name
	 */
	@Column(name = "PARENT_NAME")
	private String parentName;
	/**
	 * 风险CODE
	 */
	@Column(name = "RISK_CODE")
	private String code;
	/**
	 * 风险Name
	 */
	@Column(name = "RISK_NAME")
	private String name;
	/**
	 * 风险描述
	 */
	@Column(name = "EDESC")
	private String desc;
	/**
	 * 主责部门
	 */
	@Column(name = "RESP_ORGS")
	private String respOrgs;
	/**
	 * 相关部门
	 */
	@Column(name = "RELA_ORGS_NAME")
	private String relaOrgsName;
	/**
	 * 主责部门Name
	 */
	@Column(name = "RESP_ORGS_NAME")
	private String respOrgsName;
	/**
	 * 相关部门Name
	 */
	@Column(name = "RELA_ORGS")
	private String relaOrgs;
	/**
	 * 影响指标
	 */
	@Column(name = "IMPACT_TARGET")
	private String impactTarget;
	/**
	 * 影响流程
	 */
	@Column(name = "IMPACT_PROCESS")
	private String impactProcess;	
	/**
	 * 影响指标Name
	 */
	@Column(name = "IMPACT_TARGET_NAME")
	private String impactTargetName;
	/**
	 * 影响流程Name
	 */
	@Column(name = "IMPACT_PROCESS_NAME")
	private String impactProcessName;
	/**
	 * 风险排序
	 */
	@Column(name = "ESORT")
	private String esort;
	/**
	 * 是否继承评估模板
	 */
	@Column(name = "IS_INHERIT")
	private String isInherit;
	/**
	 * rbs:风险分类，re：风险事件
	 */
	@Column(name = "IS_RISK_CLASS")
	private String isRiskClass;
	/**
	 * 评估模板Name
	 */
	@Column(name = "ASSESSMENT_TEMPLATE_NAME")
	private String assessmentTemplateName;
	/**
	 * 评估模板
	 */
	@Column(name = "ASSESSMENT_TEMPLATE")
	private String assessmentTemplate;
	/**
	 * 发生可能性
	 */
	@Column(name = "PROBABILITY")
	private String probability;
	/**
	 * 影响程度
	 */
	@Column(name = "IMPACT")
	private String impact;
	/**
	 * 管理紧迫性
	 */
	@Column(name = "URGENCY")
	private String urgency;
	/**
	 * 风险水平
	 */
	@Column(name = "RISK_LEVEL")
	private String riskLevel;
	/**
	 * 风险状态（红、黄、绿）
	 */
	@Column(name = "RISK_STATUS")
	private String riskStatus;


	/**
	 * 分库标识
	 */
	@Column(name = "SCHM")
	private String SCHM;


	/**
	 * 所属部门
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "create_org")
	private SysOrganization createOrg;
	/**
	 * 评论
	 */
	@Column(name = "COMMENT")
	private String comment;
	/**
     * 层级
     */
    @Column(name = "ELEVEL")
    private String level;
    
    /**
     * 层级
     */
    @Column(name = "EX_ROW")
    private String exRow;
	
    /**
     * 风险应对
     */
    @Column(name = "RISK_RESPONSE")
    private String responseStr;
    
	public RiskFromExcel() {
		super();
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRespOrgs() {
		return respOrgs;
	}

	public void setRespOrgs(String respOrgs) {
		this.respOrgs = respOrgs;
	}

	public String getRelaOrgs() {
		return relaOrgs;
	}

	public void setRelaOrgs(String relaOrgs) {
		this.relaOrgs = relaOrgs;
	}

	public String getImpactTarget() {
		return impactTarget;
	}

	public void setImpactTarget(String impactTarget) {
		this.impactTarget = impactTarget;
	}

	public String getImpactProcess() {
		return impactProcess;
	}

	public void setImpactProcess(String impactProcess) {
		this.impactProcess = impactProcess;
	}

	public String getEsort() {
		return esort;
	}

	public void setEsort(String esort) {
		this.esort = esort;
	}

	public String getIsInherit() {
		return isInherit;
	}

	public void setIsInherit(String isInherit) {
		this.isInherit = isInherit;
	}

	public String getIsRiskClass() {
		return isRiskClass;
	}

	public void setIsRiskClass(String isRiskClass) {
		this.isRiskClass = isRiskClass;
	}

	public String getAssessmentTemplate() {
		return assessmentTemplate;
	}

	public void setAssessmentTemplate(String assessmentTemplate) {
		this.assessmentTemplate = assessmentTemplate;
	}

	public String getProbability() {
		return probability;
	}

	public void setProbability(String probability) {
		this.probability = probability;
	}

	public String getImpact() {
		return impact;
	}

	public void setImpact(String impact) {
		this.impact = impact;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getRiskStatus() {
		return riskStatus;
	}

	public void setRiskStatus(String riskStatus) {
		this.riskStatus = riskStatus;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getImpactTargetName() {
		return impactTargetName;
	}

	public void setImpactTargetName(String impactTargetName) {
		this.impactTargetName = impactTargetName;
	}

	public String getImpactProcessName() {
		return impactProcessName;
	}

	public void setImpactProcessName(String impactProcessName) {
		this.impactProcessName = impactProcessName;
	}

	public String getRelaOrgsName() {
		return relaOrgsName;
	}

	public void setRelaOrgsName(String relaOrgsName) {
		this.relaOrgsName = relaOrgsName;
	}

	public String getRespOrgsName() {
		return respOrgsName;
	}

	public void setRespOrgsName(String respOrgsName) {
		this.respOrgsName = respOrgsName;
	}

	public String getAssessmentTemplateName() {
		return assessmentTemplateName;
	}

	public void setAssessmentTemplateName(String assessmentTemplateName) {
		this.assessmentTemplateName = assessmentTemplateName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getExRow() {
		return exRow;
	}

	public void setExRow(String exRow) {
		this.exRow = exRow;
	}
	public String getSCHM() { return SCHM; }
	public void setSCHM(String SCHM) { this.SCHM = SCHM; }
	public SysOrganization getCreateOrg() { return createOrg; }
	public void setCreateOrg(SysOrganization createOrg) { this.createOrg = createOrg; }

	public String getResponseStr() {
		return responseStr;
	}

	public void setResponseStr(String responseStr) {
		this.responseStr = responseStr;
	}
	

}