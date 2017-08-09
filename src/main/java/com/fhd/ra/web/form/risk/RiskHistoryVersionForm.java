package com.fhd.ra.web.form.risk;

import com.fhd.entity.risk.RiskHistoryVersion;


public class RiskHistoryVersionForm {
	
	/**
	 * 排名序号
	 */
	private int num;
	
	/**
	 * 风险id
	 */
	private String riskId;
	private String parentId;
	private String parentName;
	private String name;
	private String adjustHistoryId;
	private String templateId;
	private String dutyDepartment;
	private String relativeDepartment;
	private String probability;
	private String influenceDegree;
	private Double riskScore;
	private String riskStatus;
	private String adjustType;
	private int level;
	
	/**
	 * 构造函数，用于直接通过RiskHistoryVersion赋值
	 */
	public RiskHistoryVersionForm(RiskHistoryVersion riskHistoryVersion){
		this.riskId = riskHistoryVersion.getRisk().getId();
		this.parentName = riskHistoryVersion.getParentName();
		this.name = riskHistoryVersion.getName();
		this.adjustHistoryId = riskHistoryVersion.getAdjustHistoryId();
		this.templateId = riskHistoryVersion.getTemplateId();
		this.dutyDepartment = riskHistoryVersion.getDutyDepartment();
		this.relativeDepartment = riskHistoryVersion.getRelativeDepartment();
		this.probability = riskHistoryVersion.getProbability();
		this.influenceDegree = riskHistoryVersion.getInfluenceDegree();
		this.riskScore = riskHistoryVersion.getRiskScore();
		this.riskStatus = riskHistoryVersion.getRiskStatus();
		this.adjustType = "";
		this.level = 1;   //riskHistoryVersion.getLevel()==null?null:riskHistoryVersion.getLevel(); 数据库为空，有问题，先不解决
	}
	
	/**
	 * 默认构造函数
	 */
	public RiskHistoryVersionForm(){

	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

    public String getAdjustType() {
        return adjustType;
    }

    public void setAdjustType(String adjustType) {
        this.adjustType = adjustType;
    }
    
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}
