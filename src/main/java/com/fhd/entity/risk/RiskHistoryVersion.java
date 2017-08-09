package com.fhd.entity.risk;

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
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 风险历史版本实体
 * 
 * @author zhengjunxiang
 * @version
 * @since Ver 1.0
 * @Date 2013-11-12
 * 
 * @see
 */
@Entity
@Table(name = "T_RM_HISTORYVERSION")
public class RiskHistoryVersion extends IdEntity implements Serializable {
	private static final long serialVersionUID = -4083772855355763270L;

	/**
	 * rbs:风险分类，re：风险事件
	 */
	@Column(name = "IS_RISK_CLASS")
	private String isRiskClass;

	/**
	 * 上级风险ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private Risk parent;
	
	/**
	 * 上级风险名称
	 */
	@Column(name = "PARENT_NAME")
	private String parentName;

	/**
	 * 层级
	 */
	@Column(name = "ELEVEL")
	private Integer level;

	/**
	 * 主键全路径
	 */
	@Column(name = "ID_SEQ")
	private String idSeq;

	/**
	 * 是否是叶子
	 */
	@Column(name = "IS_LEAF")
	private Boolean isLeaf;

	/**
	 * 删除状态 0：不可用；1：可用,2风险评估模块中添加
	 */
	@Column(name = "DELETE_ESTATUS")
	private String deleteStatus;

	/**
	 * 排列顺序
	 */
	@Column(name = "ESORT")
	private Integer sort;

	/**
	 * 所属公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;

	/**
	 * 风险编号
	 */
	@Column(name = "RISK_CODE")
	private String code;

	/**
	 * 风险名称
	 */
	@Column(name = "RISK_NAME", length = 4000)
	private String name;

	/**
	 * 风险描述
	 */
	@Column(name = "EDESC", length = 4000)
	private String desc;

	/**
	 * 创建时间.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME")
	private Date createTime;
	
	/**
	 * 创建人.
	 */
	@Column(name = "CREATE_BY")
	private String createBy;
	
	/**
	 * 责任部门
	 */
	@Column(name = "DUTY_DEPARTMENT")
	private String dutyDepartment;
	
	/**
	 * 相关部门
	 */
	@Column(name = "RELATIVE_DEPARTMENT")
	private String relativeDepartment;
	
	/**
	 * 责任部门ids
	 */
	@Column(name = "DUTY_DEPARTMENT_IDS")
	private String dutyDepartmentIds;
	
	/**
	 * 相关部门ids
	 */
	@Column(name = "RELATIVE_DEPARTMENT_IDS")
	private String relativeDepartmentIds;
	
	/**
	 * 责任人
	 */
	@Column(name = "DUTY_EMPLOYEE")
	private String dutyEmployee;
	
	/**
	 * 相关人
	 */
	@Column(name = "RELATIVE_EMPLOYEE")
	private String relativeEmployee;
	
	/**
	 * 影响指标
	 */
	@Column(name = "INFLUENCE_KPI")
	private String influenceKpi;
	
	/**
	 * 风险指标
	 */
	@Column(name = "RISK_KPI")
	private String riskKpi;
	
	/**
	 * 影响流程
	 */
	@Column(name = "INFLUENCE_PROCESS")
	private String influenceProcess;
	
	/**
	 * 控制流程
	 */
	@Column(name = "CONTROL_PROCESS")
	private String controlProcess;
	
	/**
	 * 发生可能性
	 */
	@Column(name = "PROBABILITY")
	private String probability;
	
	/**
	 * 影响程度
	 */
	@Column(name = "INFLUENCE_DEGREE")
	private String influenceDegree;
	
	/**
	 * 风险水平
	 */
	@Column(name = "RISK_SCORE")
	private Double riskScore;
	
	/**
	 * 风险状态
	 */
	@Column(name = "RISK_STATUS")
	private String riskStatus;
	
	/**
	 * 来源
	 */
	@Column(name = "SOURCE")
	private String source;

	/**
     * 风险
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RISK_ID")
    private Risk risk;
    
	/**
     * 版本
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERSION_ID")
    private RiskVersion version;
    
    /**
	 * 最新历史记录id
	 */
	@Column(name = "HISTORY_ID")
	private String adjustHistoryId;
	
	/**
	 * 模板id
	 */
	@Column(name = "TEMPLATE_ID")
	private String templateId;

	public RiskHistoryVersion() {

	}

	public RiskHistoryVersion(String id) {
		super.setId(id);
	}

	public String getIsRiskClass() {
		return isRiskClass;
	}

	public void setIsRiskClass(String isRiskClass) {
		this.isRiskClass = isRiskClass;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getIdSeq() {
		return idSeq;
	}

	public void setIdSeq(String idSeq) {
		this.idSeq = idSeq;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getDutyDepartment() {
		return dutyDepartment;
	}

	public void setDutyDepartment(String dutyDepartment) {
		this.dutyDepartment = dutyDepartment;
	}

	public String getRelativeDepartment() {
		return relativeDepartment;
	}

	public void setRelativeDepartment(String relativeDepartment) {
		this.relativeDepartment = relativeDepartment;
	}

	public String getDutyEmployee() {
		return dutyEmployee;
	}

	public void setDutyEmployee(String dutyEmployee) {
		this.dutyEmployee = dutyEmployee;
	}

	public String getRelativeEmployee() {
		return relativeEmployee;
	}

	public void setRelativeEmployee(String relativeEmployee) {
		this.relativeEmployee = relativeEmployee;
	}

	public String getInfluenceKpi() {
		return influenceKpi;
	}

	public void setInfluenceKpi(String influenceKpi) {
		this.influenceKpi = influenceKpi;
	}

	public String getRiskKpi() {
		return riskKpi;
	}

	public void setRiskKpi(String riskKpi) {
		this.riskKpi = riskKpi;
	}

	public String getInfluenceProcess() {
		return influenceProcess;
	}

	public void setInfluenceProcess(String influenceProcess) {
		this.influenceProcess = influenceProcess;
	}

	public String getControlProcess() {
		return controlProcess;
	}

	public void setControlProcess(String controlProcess) {
		this.controlProcess = controlProcess;
	}

	public String getProbability() {
		return probability;
	}

	public void setProbability(String probability) {
		this.probability = probability;
	}

	public String getInfluenceDegree() {
		return influenceDegree;
	}

	public void setInfluenceDegree(String influenceDegree) {
		this.influenceDegree = influenceDegree;
	}

	public Double getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(Double riskScore) {
		this.riskScore = riskScore;
	}

	public String getRiskStatus() {
		return riskStatus;
	}

	public void setRiskStatus(String riskStatus) {
		this.riskStatus = riskStatus;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Risk getRisk() {
		return risk;
	}

	public void setRisk(Risk risk) {
		this.risk = risk;
	}

	public RiskVersion getVersion() {
		return version;
	}

	public void setVersion(RiskVersion version) {
		this.version = version;
	}

	public Risk getParent() {
		return parent;
	}

	public void setParent(Risk parent) {
		this.parent = parent;
	}

	public String getDutyDepartmentIds() {
		return dutyDepartmentIds;
	}

	public void setDutyDepartmentIds(String dutyDepartmentIds) {
		this.dutyDepartmentIds = dutyDepartmentIds;
	}

	public String getRelativeDepartmentIds() {
		return relativeDepartmentIds;
	}

	public void setRelativeDepartmentIds(String relativeDepartmentIds) {
		this.relativeDepartmentIds = relativeDepartmentIds;
	}

	public String getAdjustHistoryId() {
		return adjustHistoryId;
	}

	public void setAdjustHistoryId(String adjustHistoryId) {
		this.adjustHistoryId = adjustHistoryId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	
}
