package com.fhd.ra.web.form.risk;

import com.fhd.entity.risk.Risk;

public class RiskForm extends Risk {

	private static final long serialVersionUID = -2209315597081220145L;
	
	public RiskForm(){
		
	}
	
	/**
	 * 上级风险ID
	 */
	private String parentId = null;
	
	/**
	 * 责任部门和责任人
	 */
	private String respDeptName = "";
	
	/**
	 * 相关部门和相关人
	 */
	private String relaDeptName = "";
	
	/**
	 * 责任岗位
	 */
	private String respPositionName = "";
	
	/**
	 * 相关岗位
	 */
	private String relaPositionName = "";
	

	/**
	 * 风险指标
	 */
	private String riskKpiName = "";
	
	/**
	 * 影响指标
	 */
	private String influKpiName = "";

	/**
	 * 控制流程
	 */
	private String controlProcessureName = "";
	
	/**
	 * 影响流程
	 */
	private String influProcessureName = "";
	
	/**
	 * 模板ID
	 */
	private String templateId = null;
	
	/**
	 * 告警方案ID
	 */
	private String alarmPlanId = null;
	
	/**
	 * 风险类别
	 */
	String riskKind = null;
	
	/**
	 * 涉及版块
	 */
	String relePlate = null;
	
	/**
	 * 风险类型
	 */
	String riskType = null;
	
	/**
	 * 内部动因
	 */
	String innerReason = null;
	
	/**
	 * 外部动因
	 */
	String outterReason = null;
	
	/**
	 * 风险动因
	 */
	String riskReason = null;
	
	/**
	 * 风险影响
	 */
	String riskInfluence = null;
	
	/**
	 * 风险价值链
	 */
	String valueChain = null;
	
	/**
	 * 影响期间
	 */
	String impactTime = null;
	
	/**
	 * 应对策略
	 */
	String responseStrategy = null;
	
	/**
	 * 风险分类
	 */
	String riskStructure = null;
	
	/**
     * 是否计算
     */
	String calcStr = null;
	
	/**
	 * 序号
	 */
	String num = "";
	
	/**
	 * 上级风险
	 */
	String parentName = "";
	
	/**
	 * 最新历史记录id
	 */
	String historyId = "";
	
	/**
	 * 发生可能性
	 */
	String probability = "";
	
	/**
	 * 影响程度
	 */
	String influenceDegree = "";
	
	/**
	 * 风险值
	 */
	String riskScore = "";
	
	/**
	 * 风险水平
	 */
	String riskStatus = "";
	
	String scoreObjectId = "";
	
	/**
	 * 存储应对措施内容
	 * songjia
	 */
	String responseText = null;
	
	/**
	 * 存储操作类型
	 * songjia
	 */
	String operationType = null;
	
	public String getImpactTime() {
		return impactTime;
	}

	public void setImpactTime(String impactTime) {
		this.impactTime = impactTime;
	}

	public String getResponseStrategy() {
		return responseStrategy;
	}

	public void setResponseStrategy(String responseStrategy) {
		this.responseStrategy = responseStrategy;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getRespDeptName() {
		return respDeptName;
	}

	public void setRespDeptName(String respDeptName) {
		this.respDeptName = respDeptName;
	}

	public String getRelaDeptName() {
		return relaDeptName;
	}

	public void setRelaDeptName(String relaDeptName) {
		this.relaDeptName = relaDeptName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getRespPositionName() {
		return respPositionName;
	}

	public void setRespPositionName(String respPositionName) {
		this.respPositionName = respPositionName;
	}

	public String getRelaPositionName() {
		return relaPositionName;
	}

	public void setRelaPositionName(String relaPositionName) {
		this.relaPositionName = relaPositionName;
	}

	public String getRiskKpiName() {
		return riskKpiName;
	}

	public void setRiskKpiName(String riskKpiName) {
		this.riskKpiName = riskKpiName;
	}

	public String getInfluKpiName() {
		return influKpiName;
	}

	public void setInfluKpiName(String influKpiName) {
		this.influKpiName = influKpiName;
	}

	public String getControlProcessureName() {
		return controlProcessureName;
	}

	public void setControlProcessureName(String controlProcessureName) {
		this.controlProcessureName = controlProcessureName;
	}

	public String getInfluProcessureName() {
		return influProcessureName;
	}

	public void setInfluProcessureName(String influProcessureName) {
		this.influProcessureName = influProcessureName;
	}

	public String getAlarmPlanId() {
		return alarmPlanId;
	}

	public void setAlarmPlanId(String alarmPlanId) {
		this.alarmPlanId = alarmPlanId;
	}

	public String getRiskKind() {
		return riskKind;
	}

	public void setRiskKind(String riskKind) {
		this.riskKind = riskKind;
	}

	public String getRelePlate() {
		return relePlate;
	}

	public void setRelePlate(String relePlate) {
		this.relePlate = relePlate;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public String getInnerReason() {
		return innerReason;
	}

	public void setInnerReason(String innerReason) {
		this.innerReason = innerReason;
	}

	public String getOutterReason() {
		return outterReason;
	}

	public void setOutterReason(String outterReason) {
		this.outterReason = outterReason;
	}

	public String getRiskReason() {
		return riskReason;
	}

	public void setRiskReason(String riskReason) {
		this.riskReason = riskReason;
	}

	public String getRiskInfluence() {
		return riskInfluence;
	}

	public void setRiskInfluence(String riskInfluence) {
		this.riskInfluence = riskInfluence;
	}

	public String getValueChain() {
		return valueChain;
	}

	public void setValueChain(String valueChain) {
		this.valueChain = valueChain;
	}

	public String getRiskStructure() {
		return riskStructure;
	}

	public void setRiskStructure(String riskStructure) {
		this.riskStructure = riskStructure;
	}

    public String getCalcStr() {
        return calcStr;
    }

    public void setCalcStr(String calcStr) {
        this.calcStr = calcStr;
    }

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getHistoryId() {
		return historyId;
	}

	public void setHistoryId(String historyId) {
		this.historyId = historyId;
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

	public String getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(String riskScore) {
		this.riskScore = riskScore;
	}

	public String getRiskStatus() {
		return riskStatus;
	}

	public void setRiskStatus(String riskStatus) {
		this.riskStatus = riskStatus;
	}

	public String getScoreObjectId() {
		return scoreObjectId;
	}

	public void setScoreObjectId(String scoreObjectId) {
		this.scoreObjectId = scoreObjectId;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	
}
