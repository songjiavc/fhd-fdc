package com.fhd.ra.business.assess.approval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.entity.assess.quaAssess.EditIdea;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.assess.WeightSet;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.ScoreResultBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.sys.business.assess.WeightSetBO;

/**
 * 组织数据业务
 * */

@Service
public class AssessDateInitBO {

	@Autowired
	private RiskOutsideBO o_risRiskService;
	
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
	
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	@Autowired
	private ScoreResultBO o_scoreResultBO;
	
	@Autowired
	private FormulaSetBO o_formulaSetBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private TemplateBO o_templateBO;
	
	@Autowired
	private AlarmPlanBO o_AssessAlarmPlanBO;
	
	@Autowired
	private RiskTidyBO o_riskTidyBO;
	
	@Autowired
	private ShowRiskTidyBO o_showRiskTidyBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private ScoreDeptBO o_riskScoreDeptBO;
	
	/**
	 * 浏览此风险所有评估人组织数据
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param riskId 风险ID
	 * @return ApplyContainer
	 * @author 金鹏祥
	 * */
	public ApplyContainer getAssessPreviewEmpAllDateInit(String companyId, String assessPlanId, String riskId){
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = 
				o_scoreResultBO.findScoreResultByAssessPlanIdAndRiskIdAllMap(assessPlanId, riskId);
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
    	HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
    	HashMap<String, String> scoreResultAllMap = o_rangObjectDeptEmpBO.findRangObjectDeptEmpDimDicList(assessPlanId);
    	String alarmScenario = "";
    	if(null != o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid())){
    		alarmScenario = o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid()).getAlarmScenario().getId();
    	}
    	ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
    	HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
    	String companyid = UserContext.getUser().getCompanyid();
    	Map<String, Risk> riskAllMap = o_risRiskService.getAllRiskInfo(companyid);
    	HashMap<String,EditIdea> editIdeaMapAll = o_showRiskTidyBO.findEditIdeaMapAll();
		
		String riskLevelFormula = "";
		if(null != o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid())){
    		riskLevelFormula = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid()).getRiskLevelFormula();
    	}
		HashMap<String, Template> templateAllMap = o_templateBO.findTemplateAllMap();
		String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId();
		
		ApplyContainer applyContainer = new ApplyContainer();
		applyContainer.setTemplateRelaDimensionIsParentIdIsNullInfoAllMap(templateRelaDimensionIsParentIdIsNullInfoAllMap);
        applyContainer.setTemplateRelaDimensionMap(templateRelaDimensionMap);
        applyContainer.setScoreResultAllMap(scoreResultAllMap);
        applyContainer.setAlarmPlanAllMap(alarmPlanAllMap);
        applyContainer.setAlarmScenario(alarmScenario);
        applyContainer.setScoreResultByrangObjectDeptEmpIdAllMap(scoreResultByrangObjectDeptEmpIdAllMap);
        applyContainer.setTemplateRelaDimensionByIdAndTemplateIdAllMap(templateRelaDimensionByIdAndTemplateIdAllMap);
        applyContainer.setRiskLevelFormula(riskLevelFormula);
        applyContainer.setDimIdAllList(dimIdAllList);
        applyContainer.setTemplateAllMap(templateAllMap);
        applyContainer.setRiskAllMap(riskAllMap);
        applyContainer.setTempSys(tempSys);
        applyContainer.setEditIdeaMapAll(editIdeaMapAll);
        
        return applyContainer;
	}
	
	/**
	 * 评估浏览初始化组织数据
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param scoreEmpId 人员ID
	 * @param scoreEmpIdList 人员ID集合列表
	 * @return ApplyContainer
	 * @author 金鹏祥
	 * */
	public ApplyContainer getAssessPreviewDateInit(String companyId, String assessPlanId, String scoreEmpId, ArrayList<String> scoreEmpIdList){
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = 
    			o_scoreResultBO.findScoreResultByAssessPlanIdAllMap(assessPlanId, scoreEmpId);
    	HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
    	HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
    	HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
    	HashMap<String, String> scoreResultAllMap = o_rangObjectDeptEmpBO.findRangObjectDeptEmpDimDicList(assessPlanId, scoreEmpIdList);
    	HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
    	HashMap<String, Template> templateAllMap = o_templateBO.findTemplateAllMap();
    	String alarmScenario = ""; //告警方案
    	WeightSet weightSet = o_weightSetBO.findWeightSetAll(companyId);
    	if(null != weightSet){
    		if(null != weightSet.getAlarmScenario()){
        		alarmScenario = weightSet.getAlarmScenario().getId();
        	}
    	}
    	String riskLevelFormula = ""; //风险水平计算公式
    	FormulaSet formulaSet = o_formulaSetBO.findFormulaSet(companyId);
    	if(null != formulaSet){
    		if(null != formulaSet.getRiskLevelFormula()){
        		riskLevelFormula = formulaSet.getRiskLevelFormula();
        	}
    	}
    	ArrayList<String> dimIdAlllist = o_dimensionBO.findDimensionDimIdAllList();
    	String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId(); //系统模板ID
		Map<String, Risk> riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
    	
		ApplyContainer applyContainer = new ApplyContainer();
    	applyContainer.setScoreResultByrangObjectDeptEmpIdAllMap(scoreResultByrangObjectDeptEmpIdAllMap);
    	applyContainer.setTemplateRelaDimensionIsParentIdIsNullInfoAllMap(templateRelaDimensionIsParentIdIsNullInfoAllMap);
    	applyContainer.setTemplateRelaDimensionByIdAndTemplateIdAllMap(templateRelaDimensionByIdAndTemplateIdAllMap);
    	applyContainer.setTemplateRelaDimensionMap(templateRelaDimensionMap);
    	applyContainer.setScoreResultAllMap(scoreResultAllMap);
    	applyContainer.setAlarmPlanAllMap(alarmPlanAllMap);
    	applyContainer.setTemplateAllMap(templateAllMap);
    	applyContainer.setAlarmScenario(alarmScenario);
    	applyContainer.setRiskLevelFormula(riskLevelFormula);
    	applyContainer.setDimIdAllList(dimIdAlllist);
    	applyContainer.setTempSys(tempSys);
    	applyContainer.setRiskAllMap(riskAllMap);
    	
    	return applyContainer;
	}
	
	
	/**
	 * 得到风险灯及动态维度及多人评估初始化数据
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param 人员集合
	 * @return ApplyContainer
	 * @author 金鹏祥
	 * */
	public ApplyContainer getLeadPreviewDateInit(
			String companyId, String assessPlanId, ArrayList<String> scoreEmpIdList){
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = 
				o_scoreResultBO.findScoreResultByAssessPlanIdAllMap(assessPlanId, scoreEmpIdList);
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
    	HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
    	HashMap<String, String> scoreResultAllMap = o_rangObjectDeptEmpBO.findRangObjectDeptEmpDimDicList(assessPlanId, scoreEmpIdList);
    	String alarmScenario = ""; //告警方案
    	if(null != o_weightSetBO.findWeightSetAll(companyId)){
    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
    	}
    	String riskLevelFormula = ""; //风险水平计算公式
    	if(null != o_formulaSetBO.findFormulaSet(companyId)){
    		riskLevelFormula = o_formulaSetBO.findFormulaSet(companyId).getRiskLevelFormula();
    	}
    	ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
    	HashMap<String, Template> templateAllMap = o_templateBO.findTemplateAllMap();
    	HashMap<String, Double> riskAssessDynamicRoleLeaderMap =  o_weightSetBO.findRoleWeight();
    	List<String> dynamicRoleList = o_weightSetBO.findRoleCodes();
    	ArrayList<String> rangObjectDeptEmpEmpIdList = o_rangObjectDeptEmpBO.findRangObjectDeptEmpEmpIdList(assessPlanId, scoreEmpIdList);
    	String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId();
    	
    	HashMap<String, String> riskOrgAllMap = o_scoreObjectBO.findRiskOrgAllMap(assessPlanId);
		ArrayList<ArrayList<String>> scoreResultAllList = o_scoreResultBO.findScoreResultAllList(assessPlanId, scoreEmpIdList);
		HashMap<String, ArrayList<String>> userRoleAllMap = o_rangObjectDeptEmpBO.findUserRoleAllMap();
		HashMap<String, Double> weightSetAllMap = o_weightSetBO.findWeightSetAllMap(companyId);
		HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
		Map<String, Risk> riskAllMap = o_risRiskService.getAllRiskInfo(companyId);
    	
		ApplyContainer applyContainer = new ApplyContainer();
		applyContainer.setTemplateRelaDimensionIsParentIdIsNullInfoAllMap(templateRelaDimensionIsParentIdIsNullInfoAllMap);
        applyContainer.setTemplateRelaDimensionMap(templateRelaDimensionMap);
        applyContainer.setScoreResultAllMap(scoreResultAllMap);
        applyContainer.setAlarmPlanAllMap(alarmPlanAllMap);
        applyContainer.setAlarmScenario(alarmScenario);
        applyContainer.setScoreResultByrangObjectDeptEmpIdAllMap(scoreResultByrangObjectDeptEmpIdAllMap);
        applyContainer.setTemplateRelaDimensionByIdAndTemplateIdAllMap(templateRelaDimensionByIdAndTemplateIdAllMap);
        applyContainer.setRiskLevelFormula(riskLevelFormula);
        applyContainer.setDimIdAllList(dimIdAllList);
        applyContainer.setTemplateAllMap(templateAllMap);
        applyContainer.setRiskAllMap(riskAllMap);
        applyContainer.setTempSys(tempSys);
		
        applyContainer.setWeightSetAllMap(weightSetAllMap);
        applyContainer.setUserRoleAllMap(userRoleAllMap);
        applyContainer.setRiskOrgAllMap(riskOrgAllMap);
        applyContainer.setScoreResultAllList(scoreResultAllList);
        applyContainer.setRiskAssessDynamicRoleLeaderMap(riskAssessDynamicRoleLeaderMap);
        applyContainer.setDynamicRoleList(dynamicRoleList);
        applyContainer.setRangObjectDeptEmpEmpIdList(rangObjectDeptEmpEmpIdList);
    	
    	return applyContainer;
	}
	
	/**
	 * 得到风险灯及动态维度及多人评估初始化数据
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 公式计算实体
	 * @return ApplyContainer
	 * @author 金鹏祥
	 * */
	public ApplyContainer getCollectDateInit(String companyId, String assessPlanId, FormulaSet formulaSet){
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>(); //部门人员评价的风险集合键值
		ArrayList<String> needAssessDeptsList = o_riskTidyBO.findNeedAssessDepts(assessPlanId);
		HashMap<String, String> riskOrgAllMap = o_scoreObjectBO.findRiskOrgAllMap(assessPlanId);
		ArrayList<ArrayList<String>> scoreResultAllList = o_scoreResultBO.findScoreResultAllList(assessPlanId);
		HashMap<String, ArrayList<String>> userRoleAllMap = o_rangObjectDeptEmpBO.findUserRoleAllMap();
		HashMap<String, Double> weightSetAllMap = o_weightSetBO.findWeightSetAllMap(companyId);
		HashMap<String, AlarmPlan> alarmPlanAllMap = o_AssessAlarmPlanBO.findAlarmPlanAllMap(companyId);
		Map<String, Risk> riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = 
				o_scoreResultBO.findScoreResultByAssessPlanIdAllMap(assessPlanId);
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
    	HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
    	HashMap<String, String> scoreResultAllMap = 
    			o_rangObjectDeptEmpBO.findRangObjectDeptEmpDimDicList(assessPlanId);
    	HashMap<String, Template> templateAllMap = o_templateBO.findTemplateAllMap();
    	ArrayList<String> dimIdArrayList = o_dimensionBO.findDimensionDimIdAllList();
    	HashMap<String, Double> riskAssessDynamicRoleLeaderMap =  o_weightSetBO.findRoleWeight();
    	List<String> dynamicRoleList = o_weightSetBO.findRoleCodes();
    	ArrayList<String> rangObjectDeptEmpEmpIdList = o_rangObjectDeptEmpBO.findRangObjectDeptEmpEmpIdGroupList(assessPlanId);	
    	String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId();
    	String riskLevelFormula = formulaSet.getRiskLevelFormula();
    	String riskLevelFormulaText = formulaSet.getRiskLevelFormulaText();
		HashMap<String, ArrayList<String>> deptByRiskAllMap = o_rangObjectDeptEmpBO.finAssessByInDeptRiskAllMap(assessPlanId, needAssessDeptsList);
		
    	ApplyContainer applyContainer = new ApplyContainer();
		applyContainer.setWeightSetAllMap(weightSetAllMap);
		applyContainer.setUserRoleAllMap(userRoleAllMap);
		applyContainer.setRiskOrgAllMap(riskOrgAllMap);
		applyContainer.setScoreResultAllList(scoreResultAllList);
		applyContainer.setTemplateRelaDimensionIsParentIdIsNullInfoAllMap(templateRelaDimensionIsParentIdIsNullInfoAllMap);
		applyContainer.setTemplateRelaDimensionMap(templateRelaDimensionMap);
		applyContainer.setScoreResultAllMap(scoreResultAllMap);
		applyContainer.setAlarmPlanAllMap(alarmPlanAllMap);
		applyContainer.setRiskAllMap(riskAllMap);
		applyContainer.setScoreResultByrangObjectDeptEmpIdAllMap(scoreResultByrangObjectDeptEmpIdAllMap);
		applyContainer.setTemplateRelaDimensionByIdAndTemplateIdAllMap(templateRelaDimensionByIdAndTemplateIdAllMap);
		applyContainer.setTemplateAllMap(templateAllMap);
		applyContainer.setDimIdArrayList(dimIdArrayList);
		applyContainer.setRiskAssessDynamicRoleLeaderMap(riskAssessDynamicRoleLeaderMap);
		applyContainer.setDynamicRoleList(dynamicRoleList);
		applyContainer.setRangObjectDeptEmpEmpIdList(rangObjectDeptEmpEmpIdList);
		applyContainer.setTempSys(tempSys);
    	applyContainer.setDeptByRiskAllMap(deptByRiskAllMap);
    	applyContainer.setRiskLevelFormulaText(riskLevelFormulaText);
    	applyContainer.setRiskLevelFormula(riskLevelFormula);
    	
    	HashMap<String, ArrayList<HashMap<String, Object>>> mapLists = o_rangObjectDeptEmpBO.findAssessByDeptFiltrate(assessPlanId, applyContainer);
		for (String orgId : needAssessDeptsList) {
			ArrayList<HashMap<String, Object>> mapList = mapLists.get(orgId);
			
			for (HashMap<String, Object> hashMap : mapList) {
				mapsList.add(hashMap);
			}
		}
		
		applyContainer.setMapsList(mapsList);
    	
    	return applyContainer;
	}
	
	/**
	 * 查询此计划下所有评估人组织数据
	 * */
	public ApplyContainer getPreviewAssessPlanEmpAllDateInit(String companyId, String assessPlanId){
		HashMap<String, ArrayList<String>> riskAdjusthistoryByRiskIdAndAssessPlanIdMapAll = 
				o_riskAdjustHistoryBO.findRiskAdjusthistoryByRiskIdAndAssessPlanIdMapAll(assessPlanId);
        HashMap<String, ArrayList<String>> orgByScoreObjectIdMapAll = o_riskScoreDeptBO.findOrgByScoreObjectIdMapAll(assessPlanId);
        HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
        HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
        HashMap<String, String> scoreResultAllMap = o_rangObjectDeptEmpBO.findRangObjectDeptEmpDimDicList(assessPlanId);
        HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
        Map<String, Risk> riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
        HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = 
        		o_scoreResultBO.findScoreResultByAssessPlanIdQueryAllMap(assessPlanId);
        HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
        HashMap<String, Template> templateAllMap = o_templateBO.findTemplateAllMap();
        
        String alarmScenario = "";
        WeightSet weightSet = o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid());
    	if(null != weightSet){
    		if(null != weightSet.getAlarmScenario()){
        		alarmScenario = weightSet.getAlarmScenario().getId();
        	}
    	}
    	
        String riskLevelFormula = "";
        FormulaSet formulaSet = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid());
    	if(null != formulaSet){
    		if(null != formulaSet.getRiskLevelFormula()){
        		riskLevelFormula = formulaSet.getRiskLevelFormula();
        	}
    	}
        
        ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
        String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId();
        
        ApplyContainer applyContainer = new ApplyContainer();
        applyContainer.setRiskAdjusthistoryByRiskIdAndAssessPlanIdMapAll(riskAdjusthistoryByRiskIdAndAssessPlanIdMapAll);
        applyContainer.setOrgByScoreObjectIdMapAll(orgByScoreObjectIdMapAll);
		applyContainer.setTemplateRelaDimensionIsParentIdIsNullInfoAllMap(templateRelaDimensionIsParentIdIsNullInfoAllMap);
        applyContainer.setTemplateRelaDimensionMap(templateRelaDimensionMap);
        applyContainer.setScoreResultAllMap(scoreResultAllMap);
        applyContainer.setAlarmPlanAllMap(alarmPlanAllMap);
        applyContainer.setAlarmScenario(alarmScenario);
        applyContainer.setScoreResultByrangObjectDeptEmpIdAllMap(scoreResultByrangObjectDeptEmpIdAllMap);
        applyContainer.setTemplateRelaDimensionByIdAndTemplateIdAllMap(templateRelaDimensionByIdAndTemplateIdAllMap);
        applyContainer.setRiskLevelFormula(riskLevelFormula);
        applyContainer.setTemplateAllMap(templateAllMap);
        applyContainer.setRiskAllMap(riskAllMap);
        applyContainer.setTempSys(tempSys);
        applyContainer.setDimIdAllList(dimIdAllList);
        
        return applyContainer;
	}
}