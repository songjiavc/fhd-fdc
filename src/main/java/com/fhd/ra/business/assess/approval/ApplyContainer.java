package com.fhd.ra.business.assess.approval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fhd.entity.assess.quaAssess.EditIdea;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;

/**
 * 动态实时计算动态纬度分值及高静灯需要的组织数据实体
 * */

public class ApplyContainer {

	/**
	 * 查询模版关联,主维度下有多少子维度,并已MAP方式存储(模板关联维度ParentID,模板关联维度实体)
	 * */
	private HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap;
	
	/**
	 * 查询模版关联维度全部信息,并已MAP方式存储(模板关联维度主键ID,模板关联维度实体)
	 * */
	private HashMap<String, TemplateRelaDimension> templateRelaDimensionMap;
	
	/**
	 * 查询全部打分综合ID对应维度ID、维度值结果集合 -- 通过评估人
	 * */
	private HashMap<String, String> scoreResultAllMap;
	
	/**
	 * 查询所有告警方案实体并已MAP方式存储
	 * */
	private HashMap<String, AlarmPlan> alarmPlanAllMap;
	
	/**
	 * 告警方案
	 * */
	private String alarmScenario;
	
	/**
	 * 查询该评估计划下的打分结果 -- 通过评估人
	 * */
	private HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap;
	
	/**
	 * 查询维度对应模板全部信息,并已MAP方式存储(维度ID--模板ID,模板关联维度实体)
	 * */
	private HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap;
	
	/**
	 * 风险水平计算公式
	 * */
	private String riskLevelFormula;
	
	/**
	 * 查询所有维度
	 * */
	private ArrayList<String> dimIdAllList;
	
	/**
	 * 查询模版全部信息,并已MAP方式存储(模板主键ID,模板实体)
	 * */
	private HashMap<String, Template> templateAllMap;
	
	/**
	 * 所有风险信息
	 * */
	private Map<String, Risk> riskAllMap;
	
	/**
	 * 系统模板ID
	 * */
	private String tempSys;
	
	/**
	 * 部门组织集合
	 * */
	private ArrayList<HashMap<String, Object>> mapsList;
	
	/**
	 * 维度ID集合
	 * */
	private ArrayList<String> dimIdArrayList;
	
	/**
	 * 查询角色编号权重Map
	 * */
	private HashMap<String, Double> riskAssessDynamicRoleLeaderMap;

	/**
	 * 查询所有保存的角色编号集合
	 * */
	private List<String> dynamicRoleList;
	
	/**
	 * 根据评估计划所有参加的评估人员ID
	 * */
	private ArrayList<String> rangObjectDeptEmpEmpIdList;
	
	/**
	 * 查询权重设置所有数据
	 * */
	private HashMap<String, Double> weightSetAllMap;
	
	/**
	 * 查询所有用户属于哪个角色
	 * */
	private HashMap<String, ArrayList<String>> userRoleAllMap;
	
	/**
	 * 查询所有风险关联部门表数据并已MAP方式存储
	 * */
	private HashMap<String, String> riskOrgAllMap;
	
	/**
	 * 查询打分结果维度分值所有数据过滤部门下人员
	 * */
	private ArrayList<ArrayList<String>> scoreResultAllList;
	
	/**
	 * 全部维度分值,并已MAP方式存储(模板ID,List)
	 * */
	private ArrayList<String> templateDicDescAllMap;
	
	/**
	 * 维度名称
	 * */
	private String dimName;
	
	/**
	 * 维度权重
	 * */
	private Double weight;
	
	/**
	 * 上级维度ID
	 * */
	private String parentDimId;
	
	/**
	 * 集合MAP
	 * */
	private HashMap<String, String> maps;
	
	/**
	 * 模板维度存储MAP
	 * */
	private HashMap<String, ScoreResult> scoreResultEnAllMap;
	
	/**
	 * 维度的ID
	 * */
	private String scoreDimId;
	
	/**部门从风险MAP集合
	 * 
	 * */
	private HashMap<String, ArrayList<String>> deptByRiskAllMap;
	
	/**
	 * 计算公式说明
	 * */
	private String riskLevelFormulaText;
	
	/**
	 * 意见MAP集合
	 * */
	private HashMap<String,EditIdea> editIdeaMapAll;
	
	/**
	 * 风险记录集合
	 * */
	private HashMap<String, ArrayList<String>> riskAdjusthistoryByRiskIdAndAssessPlanIdMapAll;
	
	/**
	 * 部门打分对象集合MAP
	 * */
	private HashMap<String, ArrayList<String>> orgByScoreObjectIdMapAll;
	
	public HashMap<String, ArrayList<TemplateRelaDimension>> getTemplateRelaDimensionIsParentIdIsNullInfoAllMap() {
		return templateRelaDimensionIsParentIdIsNullInfoAllMap;
	}
	public void setTemplateRelaDimensionIsParentIdIsNullInfoAllMap(
			HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap) {
		this.templateRelaDimensionIsParentIdIsNullInfoAllMap = templateRelaDimensionIsParentIdIsNullInfoAllMap;
	}
	public HashMap<String, TemplateRelaDimension> getTemplateRelaDimensionMap() {
		return templateRelaDimensionMap;
	}
	public void setTemplateRelaDimensionMap(
			HashMap<String, TemplateRelaDimension> templateRelaDimensionMap) {
		this.templateRelaDimensionMap = templateRelaDimensionMap;
	}
	public HashMap<String, String> getScoreResultAllMap() {
		return scoreResultAllMap;
	}
	public void setScoreResultAllMap(HashMap<String, String> scoreResultAllMap) {
		this.scoreResultAllMap = scoreResultAllMap;
	}
	public HashMap<String, AlarmPlan> getAlarmPlanAllMap() {
		return alarmPlanAllMap;
	}
	public void setAlarmPlanAllMap(HashMap<String, AlarmPlan> alarmPlanAllMap) {
		this.alarmPlanAllMap = alarmPlanAllMap;
	}
	public String getAlarmScenario() {
		return alarmScenario;
	}
	public void setAlarmScenario(String alarmScenario) {
		this.alarmScenario = alarmScenario;
	}
	public HashMap<String, ArrayList<ScoreResult>> getScoreResultByrangObjectDeptEmpIdAllMap() {
		return scoreResultByrangObjectDeptEmpIdAllMap;
	}
	public void setScoreResultByrangObjectDeptEmpIdAllMap(
			HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap) {
		this.scoreResultByrangObjectDeptEmpIdAllMap = scoreResultByrangObjectDeptEmpIdAllMap;
	}
	public HashMap<String, TemplateRelaDimension> getTemplateRelaDimensionByIdAndTemplateIdAllMap() {
		return templateRelaDimensionByIdAndTemplateIdAllMap;
	}
	public void setTemplateRelaDimensionByIdAndTemplateIdAllMap(
			HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap) {
		this.templateRelaDimensionByIdAndTemplateIdAllMap = templateRelaDimensionByIdAndTemplateIdAllMap;
	}
	public String getRiskLevelFormula() {
		return riskLevelFormula;
	}
	public void setRiskLevelFormula(String riskLevelFormula) {
		this.riskLevelFormula = riskLevelFormula;
	}
	public HashMap<String, Template> getTemplateAllMap() {
		return templateAllMap;
	}
	public void setTemplateAllMap(HashMap<String, Template> templateAllMap) {
		this.templateAllMap = templateAllMap;
	}
	public Map<String, Risk> getRiskAllMap() {
		return riskAllMap;
	}
	public void setRiskAllMap(Map<String, Risk> riskAllMap) {
		this.riskAllMap = riskAllMap;
	}
	public String getTempSys() {
		return tempSys;
	}
	public void setTempSys(String tempSys) {
		this.tempSys = tempSys;
	}
	public ArrayList<HashMap<String, Object>> getMapsList() {
		return mapsList;
	}
	public void setMapsList(ArrayList<HashMap<String, Object>> mapsList) {
		this.mapsList = mapsList;
	}
	public ArrayList<String> getDimIdArrayList() {
		return dimIdArrayList;
	}
	public void setDimIdArrayList(ArrayList<String> dimIdArrayList) {
		this.dimIdArrayList = dimIdArrayList;
	}
	public HashMap<String, Double> getRiskAssessDynamicRoleLeaderMap() {
		return riskAssessDynamicRoleLeaderMap;
	}
	public void setRiskAssessDynamicRoleLeaderMap(
			HashMap<String, Double> riskAssessDynamicRoleLeaderMap) {
		this.riskAssessDynamicRoleLeaderMap = riskAssessDynamicRoleLeaderMap;
	}
	public List<String> getDynamicRoleList() {
		return dynamicRoleList;
	}
	public void setDynamicRoleList(List<String> dynamicRoleList) {
		this.dynamicRoleList = dynamicRoleList;
	}
	public ArrayList<String> getRangObjectDeptEmpEmpIdList() {
		return rangObjectDeptEmpEmpIdList;
	}
	public void setRangObjectDeptEmpEmpIdList(
			ArrayList<String> rangObjectDeptEmpEmpIdList) {
		this.rangObjectDeptEmpEmpIdList = rangObjectDeptEmpEmpIdList;
	}
	public HashMap<String, Double> getWeightSetAllMap() {
		return weightSetAllMap;
	}
	public void setWeightSetAllMap(HashMap<String, Double> weightSetAllMap) {
		this.weightSetAllMap = weightSetAllMap;
	}
	public HashMap<String, ArrayList<String>> getUserRoleAllMap() {
		return userRoleAllMap;
	}
	public void setUserRoleAllMap(HashMap<String, ArrayList<String>> userRoleAllMap) {
		this.userRoleAllMap = userRoleAllMap;
	}
	public HashMap<String, String> getRiskOrgAllMap() {
		return riskOrgAllMap;
	}
	public void setRiskOrgAllMap(HashMap<String, String> riskOrgAllMap) {
		this.riskOrgAllMap = riskOrgAllMap;
	}
	public ArrayList<ArrayList<String>> getScoreResultAllList() {
		return scoreResultAllList;
	}
	public void setScoreResultAllList(
			ArrayList<ArrayList<String>> scoreResultAllList) {
		this.scoreResultAllList = scoreResultAllList;
	}
	public ArrayList<String> getTemplateDicDescAllMap() {
		return templateDicDescAllMap;
	}
	public void setTemplateDicDescAllMap(ArrayList<String> templateDicDescAllMap) {
		this.templateDicDescAllMap = templateDicDescAllMap;
	}
	public String getDimName() {
		return dimName;
	}
	public void setDimName(String dimName) {
		this.dimName = dimName;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getParentDimId() {
		return parentDimId;
	}
	public void setParentDimId(String parentDimId) {
		this.parentDimId = parentDimId;
	}
	public HashMap<String, String> getMaps() {
		return maps;
	}
	public void setMaps(HashMap<String, String> maps) {
		this.maps = maps;
	}
	public HashMap<String, ScoreResult> getScoreResultEnAllMap() {
		return scoreResultEnAllMap;
	}
	public void setScoreResultEnAllMap(
			HashMap<String, ScoreResult> scoreResultEnAllMap) {
		this.scoreResultEnAllMap = scoreResultEnAllMap;
	}
	public String getScoreDimId() {
		return scoreDimId;
	}
	public void setScoreDimId(String scoreDimId) {
		this.scoreDimId = scoreDimId;
	}
	public ArrayList<String> getDimIdAllList() {
		return dimIdAllList;
	}
	public void setDimIdAllList(ArrayList<String> dimIdAllList) {
		this.dimIdAllList = dimIdAllList;
	}
	public HashMap<String, ArrayList<String>> getDeptByRiskAllMap() {
		return deptByRiskAllMap;
	}
	public void setDeptByRiskAllMap(
			HashMap<String, ArrayList<String>> deptByRiskAllMap) {
		this.deptByRiskAllMap = deptByRiskAllMap;
	}
	public String getRiskLevelFormulaText() {
		return riskLevelFormulaText;
	}
	public void setRiskLevelFormulaText(String riskLevelFormulaText) {
		this.riskLevelFormulaText = riskLevelFormulaText;
	}
	public HashMap<String, EditIdea> getEditIdeaMapAll() {
		return editIdeaMapAll;
	}
	public void setEditIdeaMapAll(HashMap<String, EditIdea> editIdeaMapAll) {
		this.editIdeaMapAll = editIdeaMapAll;
	}
	public HashMap<String, ArrayList<String>> getRiskAdjusthistoryByRiskIdAndAssessPlanIdMapAll() {
		return riskAdjusthistoryByRiskIdAndAssessPlanIdMapAll;
	}
	public void setRiskAdjusthistoryByRiskIdAndAssessPlanIdMapAll(
			HashMap<String, ArrayList<String>> riskAdjusthistoryByRiskIdAndAssessPlanIdMapAll) {
		this.riskAdjusthistoryByRiskIdAndAssessPlanIdMapAll = riskAdjusthistoryByRiskIdAndAssessPlanIdMapAll;
	}
	public HashMap<String, ArrayList<String>> getOrgByScoreObjectIdMapAll() {
		return orgByScoreObjectIdMapAll;
	}
	public void setOrgByScoreObjectIdMapAll(
			HashMap<String, ArrayList<String>> orgByScoreObjectIdMapAll) {
		this.orgByScoreObjectIdMapAll = orgByScoreObjectIdMapAll;
	}
}