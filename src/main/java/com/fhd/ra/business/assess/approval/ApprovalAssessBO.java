package com.fhd.ra.business.assess.approval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.assess.kpiSet.RangObjectDeptEmpDAO;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.NumberUtil;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.ScoreResultBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.assess.util.DynamicDim;
import com.fhd.ra.business.assess.util.MoreRoleDeptSummarizing;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.RiskStatusBO;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.assess.WeightSetBO;

import edu.emory.mathcs.backport.java.util.Collections;

@Service
public class ApprovalAssessBO {
	
	@Autowired
	private ScoreResultBO o_scoreResultBO;
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
	@Autowired
	private WeightSetBO o_weightSetBO;
	@Autowired
	private FormulaSetBO o_formulaSetBO;
	@Autowired
	private DimensionBO o_dimensionBO;
	@Autowired
	private TemplateBO o_templateBO;
	@Autowired
	private RiskOutsideBO o_risRiskService;
	@Autowired
	private RangObjectDeptEmpDAO o_rangObjectDeptEmpDAO;
	@Autowired
	private RiskStatusBO o_riskStatusBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	@Autowired
	private KpiBO o_kpiBO;
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	@Autowired
	private WeightSetBO o_weiWeightSetBO;
	@Autowired
	private RiskScoreObjectDAO o_riskScoreObjectDAO;
	
	/**
	 * 保存打分结果(领导审批通过)
	 * */
	@Transactional
	public boolean assessApprove(String params){
		boolean isBool = false;
		try {
			JSONArray jsonarr = JSONArray.fromObject(params);
			StringBuffer rangObjectDeptEmpId = new StringBuffer();
			for (Object objects : jsonarr) {
				JSONObject jsobjs = (JSONObject) objects;
				if(rangObjectDeptEmpId.indexOf(jsobjs.get("rangObjectDeptEmpId").toString()) == -1){
					rangObjectDeptEmpId =  rangObjectDeptEmpId.append(jsobjs.get("rangObjectDeptEmpId").toString() + ",");
				}
			}
			String rangObjectDeptEmpIdStr = rangObjectDeptEmpId.toString() + ",";
			String strs[] = rangObjectDeptEmpIdStr.split(",,");
			String rangObjectDeptEmpIds[] = strs[0].split(",");
			List<ScoreResult> scoreResultList = o_scoreResultBO.findScoreResultByRangObjectDeptEmpId(rangObjectDeptEmpIds);
			for (ScoreResult scoreResult : scoreResultList) {
				scoreResult.setApproval(true);
				o_scoreResultBO.mergeScoreResult(scoreResult);
			}
			
			isBool = true;
		} catch (Exception e) {
			return isBool;
		}
		
		return isBool;
	}
	
	/**
	 * 风险评估列表审批
	 * add by 王再冉
	 * 2014-2-11  下午4:44:43
	 * desc : 
	 * @param query
	 * @param assessPlanId
	 * @param orgId
	 * @param deptByRiskAllList
	 * @param riskOrgAllMap
	 * @param scoreResultAllList
	 * @param userRoleAllMap
	 * @param weightSetAllMap
	 * @param types
	 * @param alarmPlanAllMap
	 * @param riskAllMap
	 * @param isSummarizing
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	public ArrayList<HashMap<String, Object>> findAssessByDept(String query,
			String assessPlanId, 
			List<String> orgIdList,
			ArrayList<String> scoreEmpIdList) {
		
		StringBuffer sql = new StringBuffer();
		ArrayList<String> deptRisksList = null;
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
		ArrayList<String> dimList = null;
		String companyId = UserContext.getUser().getCompanyid();
		
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = 
				o_scoreResultBO.findScoreResultByAssessPlanIdAllMap(assessPlanId, scoreEmpIdList);
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
    	HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
    	HashMap<String, String> scoreResultAllMap = o_rangObjectDeptEmpBO.findRangObjectDeptEmpDimDicList(assessPlanId, scoreEmpIdList);
    	HashMap<String, String> isSummarizingRiskIconMap = new HashMap<String, String>();
    	String alarmScenario = "";
    	if(null != o_weightSetBO.findWeightSetAll(companyId)){
    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
    	}
    	String riskLevelFormula = "";
    	if(null != o_formulaSetBO.findFormulaSet(companyId)){
    		riskLevelFormula = o_formulaSetBO.findFormulaSet(companyId).getRiskLevelFormula();
    	}
    	ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
    	HashMap<String, Template> templateAllMap = o_templateBO.findTemplateAllMap();
    	ArrayList<String> dimIdAlllist = o_dimensionBO.findDimensionDimIdAllList();
    	HashMap<String, Double> riskAssessDynamicRoleLeaderMap =  o_weightSetBO.findRoleWeight();
    	List<String> dynamicRoleList = o_weightSetBO.findRoleCodes();
    	ArrayList<String> rangObjectDeptEmpEmpIdList = o_rangObjectDeptEmpBO.findRangObjectDeptEmpEmpIdList(assessPlanId, scoreEmpIdList);
    	String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId();
    	HashMap<String, String> riskOrgAllMap = o_scoreObjectBO.findRiskOrgAllMap(assessPlanId);
		ArrayList<ArrayList<String>> scoreResultAllList = o_scoreResultBO.findScoreResultAllList(assessPlanId, scoreEmpIdList);
		ArrayList<ArrayList<String>> deptByRiskAllList = o_rangObjectDeptEmpBO.finAssessByDeptRiskAllList(assessPlanId, orgIdList);
		HashMap<String, ArrayList<String>> userRoleAllMap = o_rangObjectDeptEmpBO.findUserRoleAllMap();
		HashMap<String, Double> weightSetAllMap = o_weiWeightSetBO.findWeightSetAllMap(companyId);//权重设置
		HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);//告警方案设置
		Map<String, Risk> riskAllMap = o_risRiskService.getAllRiskInfo(companyId);
    	
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
    	applyContainer.setWeightSetAllMap(weightSetAllMap);
    	applyContainer.setUserRoleAllMap(userRoleAllMap);
    	applyContainer.setRiskOrgAllMap(riskOrgAllMap);
    	applyContainer.setScoreResultAllList(scoreResultAllList);
    	applyContainer.setRangObjectDeptEmpEmpIdList(rangObjectDeptEmpEmpIdList);
    	applyContainer.setDynamicRoleList(dynamicRoleList);
    	applyContainer.setRiskAssessDynamicRoleLeaderMap(riskAssessDynamicRoleLeaderMap);
    	
    	
		sql.append(" select a.id,f.emp_name,b.id  risk_id,e.org_id,b.risk_name,d.risk_name  parent_name, ");
		sql.append(" (select id from t_dim_template where id = (select temp_id from t_rm_risk_assess_plan where id=:assessPlanId))  templateId, ");
		sql.append(" a.score_object_id,h.id  hId, z.EDIT_IDEA_CONTENT, h.EDIT_IDEA_CONTENT  Hedit  from  ");
		sql.append(" t_rm_rang_object_dept_emp  a  ");
		sql.append(" LEFT JOIN t_rm_risk_score_object b on a.score_object_id = b.id ");
		sql.append(" LEFT JOIN t_rm_risks d on b.parent_id=d.id  ");
		sql.append(" LEFT JOIN t_rm_risk_org_temp e on a.score_dept_id = e.id ");
		sql.append(" LEFT JOIN t_sys_employee f on f.id = a.score_emp_id  ");
		sql.append(" LEFT JOIN t_rm_dept_lead_circusee g on g.score_object_id = a.score_object_id ");//and g.dept_lead_emp_id IN (:orgDeptEmpId) ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_idea h on h.dept_lead_circusee_id = g.id ");
		sql.append(" LEFT JOIN t_rm_edit_idea z on z.OBJECT_DEPT_EMP_ID = a.id ");
		sql.append(" where b.assess_plan_id=:assessPlanId ");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (b.risk_name like :query or d.risk_name like :query or f.emp_name like :query) ");
		}
		//吉志强 2017年4月6日10:43:17 风险辨识和风险评估grid拆分开    begin
		//sql.append(" and e.org_id in (:orgId) and f.id = a.score_emp_id or h.id is not null ORDER BY c.risk_name ");
		sql.append(" and e.org_id in (:orgId) and f.id = a.score_emp_id ORDER BY b.risk_name ");
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
		if(StringUtils.isNotBlank(query)){
			sqlQuery.setParameter("query", "%"+query+"%");
		}
//		sqlQuery.setParameterList("orgDeptEmpId", scoreEmpIdList);
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.setParameterList("orgId", orgIdList);
		
		System.out.println("风险评估列表审批:(辨识和评估是否在一起的sql)：="+sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		String tempStr = "";
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
	        String assessEmpId = "";//打分人员ID
	        String riskId = "";//风险ID
	        String riskName = "";//部门ID
	        String parentRiskName = "";//上级风险名称
	        String templateId = "";
	        String tempId = "";
	        String riskIcon = "";
	        String isSummarizingRiskIcon = "";
	        String objectScoreId = "";
	        String hId = "";
	        String empEditIdea = "";
	        String hEditIdeaContent = "";
	        
	        if(null != objects[0]){
	        	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	assessEmpId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[4]){
            	riskName = objects[4].toString();
            }if(null != objects[5]){
            	parentRiskName = objects[5].toString();
            }if(null != objects[6]){
            	templateId = objects[6].toString();
            }if(null != objects[7]){
            	objectScoreId = objects[7].toString();
            }if(null != objects[8]){
            	hId = objects[8].toString();
            }if(null != objects[9]){
            	empEditIdea = objects[9].toString();
            }if(null != objects[10]){
            	hEditIdeaContent = objects[10].toString();
            }
            
            if(StringUtils.isBlank(templateId)){
            	if(riskAllMap.get(riskId).getTemplate() != null){
            		tempId = riskAllMap.get(riskId).getTemplate().getId();
            	}else{
            		tempId = tempSys;
            	}
            }else{
            	tempId = templateId;
            }
            
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
            map.put("assessEmpId", assessEmpId);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("parentRiskName", parentRiskName);
            map.put("templateId", tempId);
            map.put("empEditIdea", empEditIdea);
            
            if("".equalsIgnoreCase(hEditIdeaContent)){
            	tempStr += "\t";
            	map.put("hEditIdeaContent", tempStr);
            }else{
            	 map.put("hEditIdeaContent", hEditIdeaContent);
            }
           
            riskIcon = o_riskStatusBO.getRiskIcon(
        			riskId, 
        			tempId, 
        			rangObjectDeptEmpId, 
        			applyContainer);
            
            map.put("riskIcon", riskIcon);
            
            dimList = DynamicDim.getDimValue(
        			riskId, 
        			tempId, 
        			rangObjectDeptEmpId, 
        			applyContainer);
        	
            if(dimList != null){
	        	for (String obj : dimList) {
	        		map.put(obj.split("--")[0], NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()),2));
				}
            }
        	
        	deptRisksList = o_scoreDeptBO.getDeptRisks(deptByRiskAllList, riskId);
        	if(deptRisksList.size() > 1){
        		if(isSummarizingRiskIconMap.get(riskId) == null){
	        		//多人评估
        			HashMap<String, Double> dimValueMap = 
        					MoreRoleDeptSummarizing.getDynamicDimValueMap("role", riskId, tempId, deptRisksList, applyContainer);
        			
        			
        			String valueStr = "";
	        		double value = 1L;
	        		Set<String> key = dimValueMap.keySet();
	        		ArrayList<String> scoreDicValuelist = new ArrayList<String>();
	                for (Iterator<String> it = key.iterator(); it.hasNext();) {
	                    String keys = (String) it.next();
	                    scoreDicValuelist.add(keys + "--" + dimValueMap.get(keys).toString());
	                }
	        		
	                valueStr = o_riskLevelBO.getRisklevel(scoreDicValuelist, riskLevelFormula, dimIdAllList);
	                if(valueStr.indexOf("--") == -1){
	                	value = Double.parseDouble(valueStr);
	                }
	                
	        		//得到风险灯
	        		DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), value);
	        		if(dict != null){
	        			isSummarizingRiskIcon = dict.getValue();
	        		}
	        		
	        		isSummarizingRiskIconMap.put(riskId, isSummarizingRiskIcon);
        		}else{
        			isSummarizingRiskIcon = isSummarizingRiskIconMap.get(riskId);
        		}
        	}else{
        		//一人评估
        		isSummarizingRiskIcon = riskIcon;
        	}
        	
        	map.put("isSummarizingRiskIcon", isSummarizingRiskIcon);
        	
        	if("icon-ibm-symbol-4-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//高
 				map.put("icon", "4");
 			}else if("icon-ibm-symbol-5-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//中
 				map.put("icon", "3");
 			}else if("icon-ibm-symbol-6-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//低
 				map.put("icon", "2");
 			}else if("icon-ibm-symbol-0-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//无
 				map.put("icon", "1");
 			}else{
 				map.put("icon", "0");
 			}
        	
        	map.put("objectScoreId", objectScoreId);
        	map.put("hId", hId);
        	mapsList.add(map);
        }
        
        Collections.sort(mapsList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0,
					Map<String, Object> arg1) {
				return -arg0.get("icon").toString().compareTo(arg1.get("icon").toString());
			}
		});
        
        return mapsList;
	}
	
	/**
	 * 查询领导部门下的同部门所有评估人
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<HashMap<String, Object>> findLeaderGridAssessPlan(String query, String assessPlanId, String companyId) {
		StringBuffer sql = new StringBuffer();
		ArrayList<String> deptRisksList = null;
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
		ArrayList<String> dimList = null;
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = 
				o_scoreResultBO.findScoreResultByAssessPlanIdAllMap(assessPlanId);
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
    	HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
    			o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
    	HashMap<String, String> scoreResultAllMap = o_rangObjectDeptEmpBO.findRangObjectDeptEmpDimDicList(assessPlanId);
    	HashMap<String, String> isSummarizingRiskIconMap = new HashMap<String, String>();
    	String alarmScenario = "";
    	if(null != o_weightSetBO.findWeightSetAll(companyId)){
    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
    	}
    	String riskLevelFormula = "";
    	if(null != o_formulaSetBO.findFormulaSet(companyId)){
    		riskLevelFormula = o_formulaSetBO.findFormulaSet(companyId).getRiskLevelFormula();
    	}
    	ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
    	HashMap<String, Template> templateAllMap = o_templateBO.findTemplateAllMap();
    	ArrayList<String> dimIdAlllist = o_dimensionBO.findDimensionDimIdAllList();
    	HashMap<String, Double> riskAssessDynamicRoleLeaderMap =  o_weightSetBO.findRoleWeight();
    	List<String> dynamicRoleList = o_weightSetBO.findRoleCodes();
    	ArrayList<String> rangObjectDeptEmpEmpIdList = o_rangObjectDeptEmpBO.findRangObjectDeptEmpEmpIdList(assessPlanId);
    	String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId();
    	
    	HashMap<String, String> riskOrgAllMap = o_scoreObjectBO.findRiskOrgAllMap(assessPlanId);
		ArrayList<ArrayList<String>> scoreResultAllList = o_scoreResultBO.findScoreResultAllList(assessPlanId);
		ArrayList<ArrayList<String>> deptByRiskAllList = o_rangObjectDeptEmpBO.finAssessByDeptRiskAllList(assessPlanId);
		HashMap<String, ArrayList<String>> userRoleAllMap = o_rangObjectDeptEmpBO.findUserRoleAllMap();
		HashMap<String, Double> weightSetAllMap = o_weiWeightSetBO.findWeightSetAllMap(companyId);//权重设置
		HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);//告警方案设置
		Map<String, Risk> riskAllMap = o_risRiskService.getAllRiskInfo(companyId);
    	
    	
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
    	applyContainer.setRiskOrgAllMap(riskOrgAllMap);
    	applyContainer.setScoreResultAllList(scoreResultAllList);
    	applyContainer.setUserRoleAllMap(userRoleAllMap);
    	applyContainer.setWeightSetAllMap(weightSetAllMap);
    	applyContainer.setRangObjectDeptEmpEmpIdList(rangObjectDeptEmpEmpIdList);
    	applyContainer.setRiskAssessDynamicRoleLeaderMap(riskAssessDynamicRoleLeaderMap);
    	applyContainer.setDynamicRoleList(dynamicRoleList);
    	
		sql.append(" select a.id,f.emp_name,c.id  risk_id,e.org_id,c.risk_name,d.risk_name  parent_name, ");
		sql.append(" (select id from t_dim_template where id = (select temp_id from t_rm_risk_assess_plan where id=:assessPlanId))  templateId, ");
		sql.append(" a.score_object_id,h.id  hId, z.EDIT_IDEA_CONTENT, h.EDIT_IDEA_CONTENT  Hedit  from  ");
		sql.append(" t_rm_rang_object_dept_emp  a  ");
		sql.append(" LEFT JOIN t_rm_risk_score_object b on a.score_object_id = b.id ");
		sql.append(" LEFT JOIN t_rm_risks c on b.risk_id = c.id ");
		sql.append(" LEFT JOIN t_rm_risks d on c.parent_id=d.id  ");
		sql.append(" LEFT JOIN t_rm_risk_score_dept e on a.score_dept_id = e.id ");
		sql.append(" LEFT JOIN t_sys_employee f on f.id = a.score_emp_id  ");
		sql.append(" LEFT JOIN t_rm_dept_lead_circusee g on g.score_object_id = a.score_object_id and g.ASSESS_PLAN_ID = :assessPlanId ");
//				" and g.DEPT_LEAD_EMP_ID = a.SCORE_EMP_ID ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_idea h on h.dept_lead_circusee_id = g.id ");
		sql.append(" LEFT JOIN t_rm_edit_idea z on z.OBJECT_DEPT_EMP_ID = a.id ");
		sql.append(" where b.assess_plan_id=:assessPlanId or h.id is not null ");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (c.risk_name like :query or d.risk_name like :query or f.emp_name like :query) ");
		}
		sql.append(" and f.id = a.score_emp_id ORDER BY c.risk_name ");
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
		if(StringUtils.isNotBlank(query)){
			sqlQuery.setParameter("query", "%"+query+"%");
		}
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		String tempStr = "";
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
	        String assessEmpId = "";//打分人员ID
	        String riskId = "";//风险ID
	        String riskName = "";//部门ID
	        String parentRiskName = "";//上级风险名称
	        String templateId = "";
	        String tempId = "";
	        String riskIcon = "";
	        String isSummarizingRiskIcon = "";
	        String objectScoreId = "";
	        String hId = "";
	        String empEditIdea = "";
	        String hEditIdeaContent = "";
	        
	        if(null != objects[0]){
	        	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	assessEmpId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[4]){
            	riskName = objects[4].toString();
            }if(null != objects[5]){
            	parentRiskName = objects[5].toString();
            }if(null != objects[6]){
            	templateId = objects[6].toString();
            }if(null != objects[7]){
            	objectScoreId = objects[7].toString();
            }if(null != objects[8]){
            	hId = objects[8].toString();
            }if(null != objects[9]){
            	empEditIdea = objects[9].toString();
            }if(null != objects[10]){
            	hEditIdeaContent = objects[10].toString();
            }
            
            if(StringUtils.isBlank(templateId)){
            	if(riskAllMap.get(riskId).getTemplate() != null){
            		tempId = riskAllMap.get(riskId).getTemplate().getId();
            	}else{
            		tempId = tempSys;
            	}
            }else{
            	tempId = templateId;
            }
            
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
            map.put("assessEmpId", assessEmpId);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("parentRiskName", parentRiskName);
            map.put("templateId", tempId);
            map.put("empEditIdea", empEditIdea);
            
            if("".equalsIgnoreCase(hEditIdeaContent)){
            	tempStr += "\t";
            	map.put("hEditIdeaContent", tempStr);
            }else{
            	 map.put("hEditIdeaContent", hEditIdeaContent);
            }
            
            riskIcon = o_riskStatusBO.getRiskIcon(
        			riskId, 
        			tempId, 
        			rangObjectDeptEmpId, 
        			applyContainer);
            
            map.put("riskIcon", riskIcon);
            
            dimList = DynamicDim.getDimValue(
        			riskId, 
        			tempId, 
        			rangObjectDeptEmpId, 
        			applyContainer);
        	
            if(dimList != null){
	        	for (String obj : dimList) {
	        		map.put(obj.split("--")[0], NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()),2));
				}
            }
        	
        	deptRisksList = o_scoreDeptBO.getDeptRisks(deptByRiskAllList, riskId);
        	if(deptRisksList.size() > 1){
        		if(isSummarizingRiskIconMap.get(riskId) == null){
	        		//多人评估
        			HashMap<String, Double> dimValueMap = 
        					MoreRoleDeptSummarizing.getDynamicDimValueMap("role", riskId, tempId, deptRisksList, applyContainer);
        			
        			String valueStr = "";
	        		double value = 1L;
	        		Set<String> key = dimValueMap.keySet();
	        		ArrayList<String> scoreDicValuelist = new ArrayList<String>();
	                for (Iterator it = key.iterator(); it.hasNext();) {
	                    String keys = (String) it.next();
	                    scoreDicValuelist.add(keys + "--" + dimValueMap.get(keys).toString());
	                }
	        		
	                valueStr = o_riskLevelBO.getRisklevel(scoreDicValuelist, riskLevelFormula, dimIdAllList);
	                if(valueStr.indexOf("--") == -1){
	                	value = Double.parseDouble(valueStr);
	                }
	                
	        		//得到风险灯
	        		DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), value);
	        		if(dict != null){
	        			isSummarizingRiskIcon = dict.getValue();
	        		}
	        		
	        		isSummarizingRiskIconMap.put(riskId, isSummarizingRiskIcon);
        		}else{
        			isSummarizingRiskIcon = isSummarizingRiskIconMap.get(riskId);
        		}
        	}else{
        		//一人评估
        		isSummarizingRiskIcon = riskIcon;
        	}
        	
        	map.put("isSummarizingRiskIcon", isSummarizingRiskIcon);
        	
        	if("icon-ibm-symbol-4-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//高
 				map.put("icon", "4");
 			}else if("icon-ibm-symbol-5-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//中
 				map.put("icon", "3");
 			}else if("icon-ibm-symbol-6-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//低
 				map.put("icon", "2");
 			}else if("icon-ibm-symbol-0-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//无
 				map.put("icon", "1");
 			}else{
 				map.put("icon", "0");
 			}
        	
        	map.put("objectScoreId", objectScoreId);
        	map.put("hId", hId);
        	mapsList.add(map);
        }
        
        Collections.sort(mapsList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0,
					Map<String, Object> arg1) {
				return -arg0.get("icon").toString().compareTo(arg1.get("icon").toString());
			}
		});
        
        return mapsList;
	}
	/**
	 * 根据评估人查找风险列表（邮件）
	 * add by 王再冉
	 * 2014-3-10  下午6:06:40
	 * desc : 
	 * @param businessId	计划id
	 * @param empIds		人员id
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	public ArrayList<HashMap<String, Object>> findIdentifyApproveGridByPlanIdAndEmpIds(String businessId,List<String> empIds) {
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
		sql.append(" select o.org_name,r.risk_name from t_rm_risk_score_object  a ");
		sql.append(" LEFT JOIN t_rm_risk_score_dept e ON e.SCORE_OBJECT_ID = a.id  ");
		sql.append(" LEFT JOIN t_sys_organization o on o.id = e.ORG_ID ");
		sql.append(" LEFT JOIN t_rm_risks r ON r.id = a.RISK_ID ");
		sql.append(" LEFT JOIN t_rm_rang_object_dept_emp c on  c.score_object_id = a.id ");
		sql.append(" where a. ASSESS_PLAN_ID = :businessId and c.score_emp_id in (:empIds) GROUP BY r.risk_name ");
		SQLQuery sqlQuery = o_riskScoreObjectDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("businessId", businessId);
        sqlQuery.setParameterList("empIds", empIds);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
            String orgName = "";
            String riskName = "";
            
            if(null != o[0]){
            	orgName = o[0].toString();
            }if(null != o[1]){
            	riskName = o[1].toString();
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("orgName", orgName);
			map.put("riskName", riskName);
			mapsList.add(map);
		}
        return mapsList;
	}
}