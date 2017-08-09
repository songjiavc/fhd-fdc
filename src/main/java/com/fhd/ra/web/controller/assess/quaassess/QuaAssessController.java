package com.fhd.ra.web.controller.assess.quaassess;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.quaAssess.DeptLeadCircuseeDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.assess.quaAssess.DeptLeadCircusee;
import com.fhd.entity.assess.quaAssess.DeptLeadEditIdea;
import com.fhd.entity.assess.quaAssess.DeptLeadResponseIdea;
import com.fhd.entity.assess.quaAssess.EditIdea;
import com.fhd.entity.assess.quaAssess.ResponseIdea;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.approval.ApprovalAssessBO;
import com.fhd.ra.business.assess.email.QuaAssessEmailBO;
import com.fhd.ra.business.assess.email.RiskTidyEmailBO;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.kpiset.AssessTaskBO;
import com.fhd.ra.business.assess.oper.DeptLeadCircuseeBO;
import com.fhd.ra.business.assess.oper.DeptLeadEditIdeaBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreInstanceBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.ScoreResultBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.assess.quaassess.CountAssessBO;
import com.fhd.ra.business.assess.quaassess.QuaAssessNextBO;
import com.fhd.ra.business.assess.quaassess.SaveAssessBO;
import com.fhd.ra.business.assess.quaassess.ShowAssessBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.sys.business.assess.SendEmailBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.file.FileUploadBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.EmpOrgBO;
import com.fhd.sys.business.orgstructure.SysEmpOrgBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 风险评估、领导审批执行操作
 * */

@Controller
public class QuaAssessController {

	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	@Autowired
	private ShowAssessBO o_showAssessBO;

	@Autowired
	private CountAssessBO o_countAssessBO;

	@Autowired
	private SaveAssessBO o_saveAssessBO;

	@Autowired
	private QuaAssessNextBO o_quaAssessNextBO;

	@Autowired
	private JBPMBO o_jbpmBO;

	@Autowired
	private SysEmpOrgBO o_sysEmpOrgBO;
	
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	
	@Autowired
	private AssessTaskBO o_assessTaskBO;
	
	@Autowired
	private EmpGridBO o_empBO;
	
	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
	
	@Autowired
	private ScoreResultBO o_scoreResultBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private ScoreInstanceBO o_scoreInstanceBO;
	
	@Autowired
	private DictBO o_dictBO;
	
	@Autowired
	private DeptLeadEditIdeaBO o_deptLeadEditIdeaBO;
	
	@Autowired
	private SendEmailBO o_sendEmailBO;
	
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	
	@Autowired
	private QuaAssessEmailBO o_quaAssessEmailBO;
	
	@Autowired
	private RiskTidyEmailBO o_riskTidyEmailBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	@Autowired
	private TemplateBO o_templateBO;
	
	@Autowired
	private DeptLeadCircuseeBO o_deptLeadCircuseeBO;
	
	@Autowired
	private EmpOrgBO o_empOgrBO;
	
	@Autowired
	private FileUploadBO o_fileUploadBO;
	
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	@Autowired
	private ApprovalAssessBO o_approvalAssessBO;
	
	@Autowired
	private DeptLeadCircuseeDAO deptLeadCircuseeDAO;
	
	/**
	 * 过滤评估计划ID
	 * @param str 过滤字符串
	 * @return String
	 * @author 金鹏祥
	 * */
	private String getFilterStr(String str){
		if(str.indexOf(",") != -1){
			str = str.split(",")[0];
		}
		
		return str;
	}
	
	/**
	 * 得到业务ID
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return String
	 * @author 金鹏祥
	 * */
	private String getAssessPlanId(String assessPlanId, String executionId){
		if(null == assessPlanId){
			return o_jbpmOperate.getVariable(executionId,"id");
		}
		return assessPlanId;
	}
	
	/**
	 * 浏览评估GRID
	 * @param query 查询条件
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return ArrayList<HashMap<String, Object>>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/findAssessShowGrid.f")
	public ArrayList<HashMap<String, Object>> findAssessShowGrid(String query, String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID得到评估计划ID
		String scoreEmpId = UserContext.getUser().getEmpid(); //人员ID
		String companyid = UserContext.getUser().getCompanyid(); //公司ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		
		//风险事件GRID
		return o_showAssessBO.findRiskByScoreEmpId(query, scoreEmpId, o_showAssessBO.getTemplateId(assessPlanId), true, assessPlanId, companyid);
	}

	/**
	 * 定性评估GRID
	 * @param query 查询条件
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return ArrayList<HashMap<String, Object>>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/findAssessGrid.f")
	public ArrayList<HashMap<String, Object>> findAssessGrid(String query, String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId);//通过流程ID得到评估计划ID
		String scoreEmpId = UserContext.getUser().getEmpid();//人员ID
		String companyid = UserContext.getUser().getCompanyid();//公司ID
		assessPlanId = this.getFilterStr(assessPlanId);//过滤评估计划ID
		
		//风险事件GRID
		return o_showAssessBO.findRiskByScoreEmpId(query, scoreEmpId, o_showAssessBO.getTemplateId(assessPlanId), false, assessPlanId, companyid);
	}
	
	
	/**
	 * 保密定性评估GRID
	 * @param query 查询条件
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return ArrayList<HashMap<String, Object>>
	 * @author 郭鹏
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/findAssessGridSecrecy.f")
	public ArrayList<HashMap<String, Object>> findAssessGridSecrecy(String query, String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId);//通过流程ID得到评估计划ID
		String scoreEmpId = UserContext.getUser().getEmpid();//人员ID
		String companyid = UserContext.getUser().getCompanyid();//公司ID
		assessPlanId = this.getFilterStr(assessPlanId);//过滤评估计划ID
		
		//风险事件GRID
		return o_showAssessBO.findRiskByScoreEmpIdSecrecy(query, scoreEmpId, o_showAssessBO.getTemplateId(assessPlanId), true, assessPlanId, companyid);
	}

	/**
	 * 动态维度
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @param type 工作流ID启用类型
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/findDimCols.f")
	public Map<String, Object> findDimCols(String assessPlanId, String executionId, String type) {
		assessPlanId = this.getAssessPlanId(assessPlanId, executionId); //通过流程ID得到评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤计划ID
		Map<String, Object> map = new HashMap<String, Object>(); //组织信息键值
		List<Map<String, Object>> templateRelaDimensionMapList = 
				o_showAssessBO.findTemplateRelaDimensionIsParentIdIsNullAllMapObj(o_showAssessBO.getTemplateId(assessPlanId));
		
		if(StringUtils.isNotBlank(type)){
			map.put("type",  o_jbpmBO.findPDDByExecutionId(executionId));
		}
		
		if(o_sendEmailBO.findIsSendValue("send_email_assess_riskplan_all")){
			map.put("assessEamil", true);
		}else{
			map.put("assessEamil", false);
		}
		
		map.put("templateRelaDimensionMapList", templateRelaDimensionMapList);
		return map;
	}
	
	/**
	 * 动态维度
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/findDimColsInfos.f")
	public Map<String, Object> findDimColsInfo(String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(assessPlanId, executionId); //通过流程ID获得评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> map = new HashMap<String, Object>(); //组织信息键值
		List<Map<String, Object>> templateRelaDimensionMapList = 
				o_showAssessBO.findTemplateRelaDimensionIsParentIdIsNullAllMapObj(o_showAssessBO.getTemplateId(assessPlanId));
		/*对一个已知不是null的值重复判断    王再冉修改*/
		/*if(templateRelaDimensionMapList == null){
			return null;
		}*/
		map.put("templateRelaDimensionMapList", templateRelaDimensionMapList);

		
		//得到评估人动态维度及分值
		List<Map<String, Object>> templateRelaDimensionMapList2 = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map2 : templateRelaDimensionMapList) {
			Map<String, Object> maps = new HashMap<String, Object>();
			maps.put("dimId", map2.get("dimId") + "emp");
			maps.put("dimName", map2.get("dimName") + "(评估)");
			templateRelaDimensionMapList2.add(maps);
		}
		
		//得到汇总动态维度及分值
		List<Map<String, Object>> templateRelaDimensionMapList3 = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map2 : templateRelaDimensionMapList) {
			Map<String, Object> maps = new HashMap<String, Object>();
			maps.put("dimId", map2.get("dimId"));
			maps.put("dimName", map2.get("dimName") + "(汇总)");
			templateRelaDimensionMapList3.add(maps);
		}
		
		int size = templateRelaDimensionMapList.size();
		for (int i = 0; i < size; i++) {
			templateRelaDimensionMapList.remove(0);
		}
		
		for (Map<String, Object> map2 : templateRelaDimensionMapList3) {
			templateRelaDimensionMapList.add(map2);
		}
		
		for (Map<String, Object> map2 : templateRelaDimensionMapList2) {
			templateRelaDimensionMapList.add(map2);
		}
		
		return map;
	}

	/**
	 * 定性评估列表下一步
	 * @param params 风险参数
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/assessFindRiskInfoByIds.f")
	public Map<String, Object> assessFindRiskInfoByIds(String params, String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID获得评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		String scoreEmpId = UserContext.getUser().getEmpid(); //人员ID
		Map<String, Object> returnMap = new HashMap<String, Object>(); //组织信息键值
		String templateType = o_showAssessBO.getTemplateId(assessPlanId); //评估模板类型
		HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> listMap = 
				o_quaAssessNextBO.findTemplateRelaDimByTemplateIdHtml(params, assessPlanId, templateType);
		HashMap<Integer, ArrayList<ArrayList<HashMap<String, Object>>>> countScoreMap = 
				new HashMap<Integer, ArrayList<ArrayList<HashMap<String, Object>>>>(); //组织信息键值
		HashMap<Integer, String> riskNameMap = new HashMap<Integer, String>(); //组织信息键值
		HashMap<String, String> dicByAssessPlanIdMapAll = o_scoreResultBO.findDicByAssessPlanIdMapAll(assessPlanId, scoreEmpId);
		ArrayList<String> dimArrayList = null;
		HashMap<String, String> dimDescAllMap = o_scoreInstanceBO.findDimDescAllMap();
		HashMap<String, String> dicDescAllMap = o_scoreInstanceBO.findDicDescAllMap();
		HashMap<String, EditIdea> editIdeaByObjectDeptEmpIdMapAll = o_saveAssessBO.findEditIdeaByObjectDeptEmpIdMapAll(scoreEmpId);
		String editIdea = ""; //人员意见
		int count = 0; //统计记数
		Map<String, Risk> riskAllMap = null; //风险实体键值
		String companyId = UserContext.getUser().getCompanyid(); //公司ID
		String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId(); //系统模板ID
		String templateId = ""; //模板ID
		HashMap<String, HashMap<String, TemplateRelaDimension>> templateRelaDimensionParentIdIsNullMapAll = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionParentIdIsNull();
		HashMap<String, String> dicByAssessPlanIdMapAllNew = null; //打分结果
		
		if("dim_template_type_custom".equalsIgnoreCase(templateType)){
			//自定义模板
			riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
		}else{
			//其他模板
			templateId = o_assessPlanBO.findRiskAssessPlanById(assessPlanId).getTemplate().getId();
			if(null == dimArrayList){
				dimArrayList = o_quaAssessNextBO.getDimByTemplatesId(templateId, templateRelaDimensionParentIdIsNullMapAll);
			}
			
			dicByAssessPlanIdMapAllNew = o_scoreResultBO.getDicByAssessPlanIdMapAll(dicByAssessPlanIdMapAll, templateId);
		}
		
		JSONArray jsonarr = JSONArray.fromObject(params);
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			String riskId = jsobjs.getString("riskId");
			String riskName = jsobjs.getString("riskName");
			String objectId = jsobjs.getString("objectId");
			
			String rangObjectDeptEmpId = jsobjs.getString("rangObjectDeptEmpId");
			boolean isScore = true;
			if("dim_template_type_custom".equalsIgnoreCase(templateType)){
				//自定义模板
				if(null == riskAllMap.get(riskId).getTemplate()){
					templateId = tempSys;
            	}else{
            		templateId = riskAllMap.get(riskId).getTemplate().getId();
            	}
				
				dimArrayList = o_quaAssessNextBO.getDimByTemplatesId(templateId, templateRelaDimensionParentIdIsNullMapAll);
				dicByAssessPlanIdMapAllNew = o_scoreResultBO.getDicByAssessPlanIdMapAll(dicByAssessPlanIdMapAll, templateId);
			}
			
			//此模板没有维度
			if(dimArrayList.size() == 0){
				isScore = false;
			}
			
			//遍历此模板下维度是否打分
			for (String dim : dimArrayList) {
				if(dicByAssessPlanIdMapAllNew.get(rangObjectDeptEmpId + "--" + riskId + "--" + dim) == null){
					riskNameMap.put(count, riskName + "--" + riskId + "--" + rangObjectDeptEmpId + "--" + "false"+"--"+objectId);
					isScore = false;
				}
			}
			
			//此模板已全部打分
			if(isScore){
				if(editIdeaByObjectDeptEmpIdMapAll.get(rangObjectDeptEmpId) != null){
					editIdea = editIdeaByObjectDeptEmpIdMapAll.get(rangObjectDeptEmpId).getEditIdeaContent();
				}
				if(editIdea.equals("") ||editIdea == null){
					riskNameMap.put(count, riskName + "--" + riskId + "--" + rangObjectDeptEmpId + "--" + "true" + "--" +objectId);
				}else{
					riskNameMap.put(count, riskName + "--" + riskId + "--" + rangObjectDeptEmpId + "--" + "true" + "--" + editIdea+"--"+objectId);
				}
				
			}
			
			//此模板下没有维度
			if(dimArrayList.size() == 0){
				riskNameMap.put(count, riskName + "--" + riskId + "notDim" + "--" + rangObjectDeptEmpId + "--" + "false"+"--"+objectId);
			}
			
			Set<Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>>> key2 = listMap.entrySet();
			for (Iterator<Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>>> it2 = key2.iterator(); it2.hasNext();) {
				Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>> mapKey2 = it2.next();
				if(mapKey2.getKey().equalsIgnoreCase(riskId)){
					countScoreMap.put(count, listMap.get(mapKey2.getKey()));
				}
			}
			
			count++;
		}
		
		returnMap.put("data", riskNameMap);
		returnMap.put("success", true);
		returnMap.put("result", countScoreMap);
		returnMap.put("dimDescAllMap", dimDescAllMap);
		returnMap.put("dicDescAllMap", dicDescAllMap);
		returnMap.put("totalCount", countScoreMap.size());

		return returnMap;
	}
	
	/**
	 * 评价加载
	 * @param rangObjectDeptEmpId 综合打分ID
	 * @param assessPlanId 评估计划ID
	 * @param riskId 风险ID
	 * @param executionId 流程ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/assessEvaluate.f")
	public Map<String, Object> assessEvaluate(String rangObjectDeptEmpId, String assessPlanId, String riskId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID获得评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		String scoreEmpId = UserContext.getUser().getEmpid();// 人员ID
		Map<String, Object> returnMap = new HashMap<String, Object>(); //组织数据键值
		HashMap<String, String> dicByAssessPlanIdMapAll = o_scoreResultBO.findDicByAssessPlanIdMapAll(assessPlanId, scoreEmpId);
		ArrayList<String> dimArrayList = null; //维度列表
		String templateId = ""; //模板ID
		HashMap<String, HashMap<String, TemplateRelaDimension>> templateRelaDimensionParentIdIsNullMapAll = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionParentIdIsNull();
		HashMap<String, String> dicByAssessPlanIdMapAllNew = null; //打分结果
		
		if("dim_template_type_custom".equalsIgnoreCase(o_showAssessBO.getTemplateId(assessPlanId))){
			//自定义模板
			Template template = o_riskBO.findRiskById(riskId).getTemplate();
			if(template != null){
				templateId = template.getId();
			}else{
				templateId = o_templateBO.findTemplateByType("dim_template_type_sys").getId();
			}
		}else{
			//其他模板
			templateId = o_showAssessBO.getTemplateId(assessPlanId);
		}
		
		dimArrayList = o_quaAssessNextBO.getDimByTemplatesId(templateId, templateRelaDimensionParentIdIsNullMapAll);
		boolean isScore = true;
		//此模板没有维度
		if(dimArrayList.size() == 0){
			isScore = false;
		}
		
		dicByAssessPlanIdMapAllNew = o_scoreResultBO.getDicByAssessPlanIdMapAll(dicByAssessPlanIdMapAll, templateId);
		
		//遍历此模板下维度是否打分
		for (String dim : dimArrayList) {
			if(dicByAssessPlanIdMapAllNew.get(rangObjectDeptEmpId + "--" + riskId + "--" + dim) == null){
				returnMap.put("evaluate", false);
				isScore = false;
			}
		}
		
		if(isScore){
			returnMap.put("evaluate", true);
		}
		
		returnMap.put("success", true);

		return returnMap;
	}
	
	/**
	 * 保存评估处理
	 * @param params 风险信息参数
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaAssess/saveDicValue.f")
	public Map<String, Object> saveDicValue(String params) {
		Map<String, Object> result = new HashMap<String, Object>(); //组织信息键值
		if (o_saveAssessBO.assessSaveOper(params)) {
			result.put("success", true);
		} else {
			result.put("success", false);
		}

		return result;
	}

	/**
	 * 通过风险ID集合查询已评估风险总数、未评估总数、已评估总数
	 * @param assessPlanId 评估计划ID
	 * @param params 风险信息参数
	 * @param executionId 流程ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/findAssessCount.f")
	public Map<String, Object> findAssessCount(String assessPlanId, String params, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID获得评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> result = new HashMap<String, Object>(); //组织数据键值
		String scoreEmpId = UserContext.getUser().getEmpid(); //人员ID
		HashMap<String, Integer> map = o_countAssessBO.getAssessCountMap(scoreEmpId, params, assessPlanId);
		
		result.put("isAssessCount", map.get("isAssessCount"));// 已评估数
		result.put("isNotAssessCount", map.get("isNotAssessCount"));// 未评估数
		result.put("totalCount", map.get("totalCount"));// 评估总数
		result.put("success", true);

		return result;
	}
	
	/**
	 * 得到评估计划名称
	 * @param assessPlanId 评估计划ID
	 * @param 流程ID
	 * @param type 流程开启类型
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/findAssessName.f")
	public Map<String, Object> findAssessName(String assessPlanId, String executionId, String type) {
		assessPlanId = this.getAssessPlanId(assessPlanId, executionId); //通过流程ID得到评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> result = new HashMap<String, Object>(); //组织数据信息键值
		RiskAssessPlan riskssessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
		
		if(StringUtils.isNotBlank(type)){
			result.put("type",  o_jbpmBO.findPDDByExecutionId(executionId));
		}
		
		result.put("assessPlanName", riskssessPlan.getPlanName());// 计划名称
		result.put("success", true);

		return result;
	}
	
	/**
	 * 得到评估计划对应模板的FILEID
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/findAssessFileId.f")
	public Map<String, Object> findAssessFileId(String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID得到评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> result = new HashMap<String, Object>(); //组织数据键值
		RiskAssessPlan riskssessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
		if(riskssessPlan.getTemplate() != null){
			//模块不为空
			Template template = o_templateBO.findTemplateById(riskssessPlan.getTemplate().getId()); //模板实体
			if(null != template.getFile()){
				FileUploadEntity fileUpload = o_fileUploadBO.queryFileById(template.getFile().getId()); //附件下载
				try {
					fileUpload.setCountNum(fileUpload.getCountNum() + 1);
					result.put("success", true);
					result.put("assessFileId", template.getFile().getId());
					return result;
				} catch (Exception e) {
					result.put("assessFileId", "null");
					return result;
				}
			}else{
				result.put("assessFileId", "null");
			}
		}else{
			result.put("assessFileId", "null");
		}
		
		return result;
	}

	/**
	 * 提交评估
	 * @param executionId 流程ID
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥 
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/submitAssess.f")
	public void submitAssess(String executionId, String assessPlanId, HttpServletResponse response, HttpServletRequest request) {
		PrintWriter out = null;
		HashMap<String, Long> empIdTaskIdMap = new HashMap<String, Long>(); //人员ID与TASKID键值
		try {
			out = response.getWriter();
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
			
			//部门领导审批添加--针对新加的风险事件
			/*
			String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
			HashMap<String, DeptLeadCircusee> map = o_deptLeadCircuseeBO.findDeptLeadCircuseeByAssessPlanId(assessPlanId);
			List<RiskScoreObject> scoreObjsByAssessPlanIdList = o_scoreObjectBO.findRiskScoreObjByplanId(assessPlanId);
			for (RiskScoreObject riskScoreObject : scoreObjsByAssessPlanIdList) {
				if(null == map.get(riskScoreObject.getId())){
					DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
					RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
					riskAssessPlan.setId(assessPlanId);
					SysEmployee sysEmployee = new SysEmployee();
					sysEmployee.setId(scoreEmpId);
					deptLeadCircusee.setId(Identities.uuid());
					deptLeadCircusee.setScoreObjectId(riskScoreObject);
					deptLeadCircusee.setRiskAssessPlan(riskAssessPlan);
					deptLeadCircusee.setDeptLeadEmpId(sysEmployee);
					//o_deptLeadCircuseeBO.mergeDeptLeadCircusee(deptLeadCircusee);·
				}
			}
			*/
			
			String deptId = UserContext.getUser().getMajorDeptId();//登录用户所在部门
			List<SysEmpOrg> empOrgList = o_riskScoreBO.findEmpDeptBydeptId(deptId);
			List<SysEmployee> empList = new ArrayList<SysEmployee>();
			if(null != empOrgList){
				for(SysEmpOrg empOrg : empOrgList){
					empList.add(empOrg.getSysEmployee());
				}
			}
			
			//判断是否邮件发送
			if(o_sendEmailBO.findIsSendValue("send_email_assess_riskplan_all")){
				JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(assessPlanId);
				List<JbpmHistActinst> jbpmHistActinstList = o_riskAssessPlanBO.findJbpmHistActinstsBySome(jbpmhp.getId(),0L);
				for(JbpmHistActinst jbpmHistActinst:jbpmHistActinstList){
					VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
					SysEmployee sysEmployee = jbpmHistTask.getAssignee();
					if(empList.contains(sysEmployee)){
						if(jbpmHistTask!=null){
							if("评估任务审批".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
								Long taskId = jbpmHistTask.getId();
								empIdTaskIdMap.put(sysEmployee.getId(), taskId);
							}
						}
					}
				}
				if(empIdTaskIdMap.size()>0){
					o_quaAssessEmailBO.sendQuaAssessEmail(assessPlanId, empIdTaskIdMap);
				}
			}
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}

	/**
	 * 部门领导浏览
	 * @param query 查询条件
	 * @param executionId 流程ID
	 * @param assessPlanId 评估计划ID
	 * @param loginType 登录类型
	 * @param username 登录用户名
	 * @return ArrayList<HashMap<String, Object>>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/findLeaderDept.f")
	public ArrayList<HashMap<String, Object>> findLeaderDept(String query, String executionId, String assessPlanId,
			String loginType, String username) {
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID得到评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		String orgEmpId = UserContext.getUser().getEmpid(); //承办人ID(部门风险管理员)
		String companyId = UserContext.getUser().getCompanyid(); //公司ID
		String orgId = ""; //部门ID
		try {
			orgId = o_sysEmpOrgBO.queryOrgByEmpid(orgEmpId).getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<String> orgList = o_riskScoreBO.findAllDeptIdsBydeptId(orgId);
		orgList.add(orgId);
		ArrayList<String> scoreEmpIdList = new ArrayList<String>(); //人员ID集合
		List<SysEmployee> empOrgBOList = o_riskScoreBO.findAllEmpsBydeptId(orgId); //查询此部门下人员集合
		
		for (SysEmployee sysEmployee : empOrgBOList) {
			if(null != sysEmployee){
				scoreEmpIdList.add(sysEmployee.getId());
			}
		}
		
		return o_rangObjectDeptEmpBO.findAssessByDept(query, assessPlanId, orgList, companyId, scoreEmpIdList);
	}

	/**
	 * 查询TASKID
	 * @param assessPlanId 评估计划ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/findRiskTidyTaskId.f")
	public Map<String, Object> findRiskTidyTaskId(String assessPlanId) {
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> result = new HashMap<String, Object>(); //数据信息键值
		JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(assessPlanId);
		List<JbpmHistActinst> jbpmHistActinstList = o_riskAssessPlanBO.findJbpmHistActinstsBySome(jbpmhp.getId(),0L);
		for(JbpmHistActinst jbpmHistActinst:jbpmHistActinstList){
			VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
			if(jbpmHistTask!=null){
				if("评估结果整理".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
					Long taskId = jbpmHistTask.getId();
					result.put("taskId", taskId);
					result.put("success", true);
				}
			}
		}

		return result;
	}
	
	/**
	 * 领导审批
	 * @param executionId 流程ID
	 * @param params 风险参数信息
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/approvalColl.f")
	public void approvalColl(String executionId,String isPass, String params, String assessPlanId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, Object> variables = new HashMap<String, Object>();
			//2017年4月1日16:21:17 jzq
			variables.put("path", isPass);
			o_jbpmBO.doProcessInstance(executionId, variables);
			
			if(o_sendEmailBO.findIsSendValue("send_email_assess_riskplan_all")){//风险整理邮件提醒
				JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(assessPlanId);
				List<JbpmHistActinst> jbpmHistActinstList = o_riskAssessPlanBO.findJbpmHistActinstsBySome(jbpmhp.getId(),0L);
				for(JbpmHistActinst jbpmHistActinst:jbpmHistActinstList){
					VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
					SysEmployee sysEmployee = jbpmHistTask.getAssignee();
					if(jbpmHistTask!=null){
						if("评估结果整理".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
							Long taskId = jbpmHistTask.getId();
							o_riskTidyEmailBO.sendRiskTidyEmail(assessPlanId, sysEmployee.getId(), taskId.toString());
						}
					}
				}
			}
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 查询修改意见
	 * @param objectDeptEmpId 打分综合ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/findEditIdeaByObjectDeptEmpId.f")
	public Map<String, Object> findAssessCount(String objectDeptEmpId) {
		Map<String, Object> result = new HashMap<String, Object>();//数据信息键值
		String editIdeaContent = "",responseText = "";
		EditIdea editIdea = o_saveAssessBO.findEditIdeaByObjectDeptEmpId(objectDeptEmpId);
		if(null != editIdea){
			editIdeaContent = editIdea.getEditIdeaContent();
		}
		/**
		 * add by 	songjia
		 * desc   	加载应对措施
		 */
		ResponseIdea responseIdea = o_saveAssessBO.findResponseIdeaByObjectDeptEmpId(objectDeptEmpId);
		if(null != responseIdea){
			responseText = responseIdea.getEditIdeaContent();
		}
		result.put("objectDeptEmpId", objectDeptEmpId);//综合ID
		result.put("editIdeaContent", editIdeaContent);//意见内容
		result.put("responseText",responseText);
		result.put("success", true);

		return result;
	}
	
	/**
	 * 部门领导查询修改意见
	 * @param scoreObjectId 打分对象ID
	 * @param assessPlanId 评估计划ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/findDeptLeadIdea.f")
	public Map<String, Object> findDeptLeadIdea(String scoreObjectId, String assessPlanId) {
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> result = new HashMap<String, Object>(); //数据信息键值
		String scoreEmpId = UserContext.getUser().getEmpid(); //人员ID
		String editIdeaContent = null; //领导意见内容
		String deptLeadCircuseeId = null; //领导意见ID
		String responseContent=null;
		DeptLeadEditIdea deptLeadEditIdea = o_deptLeadEditIdeaBO.findDeptLeadIdea(scoreEmpId, scoreObjectId, assessPlanId);
		if(null != deptLeadEditIdea){
			//领导信息不为空,编辑状态
			editIdeaContent = deptLeadEditIdea.getEditIdeaContent();
			responseContent=deptLeadEditIdea.getResponseIdeaContent();
			deptLeadCircuseeId = deptLeadEditIdea.getDeptLeadCircusee().getId();
		}else{
			//添加领导信息状态
				DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
				SysEmployee sysEmpoyee = new SysEmployee();
				RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
				RiskScoreObject riskScoreObject = new RiskScoreObject();
				
				riskScoreObject.setId(scoreObjectId);
				riskAssessPlan.setId(assessPlanId);
				
				sysEmpoyee.setId(scoreEmpId);
				deptLeadCircusee.setId(Identities.uuid());
				deptLeadCircusee.setDeptLeadEmpId(sysEmpoyee);
				deptLeadCircusee.setRiskAssessPlan(riskAssessPlan);
				deptLeadCircusee.setScoreObjectId(riskScoreObject);
				deptLeadCircuseeId = deptLeadCircusee.getId();
				o_deptLeadCircuseeBO.mergeDeptLeadCircusee(deptLeadCircusee);
		}
		
		result.put("deptLeadCircuseeId", deptLeadCircuseeId);
		result.put("editIdeaContent", editIdeaContent);
		result.put("responseIdea", responseContent);
		result.put("success", true);

		return result;
	}
	
	
	/**
	 * 保存修改意见
	 * @param deptLeadCircuseeId 领导意见ID
	 * @param editIdeaContent 领导意见内容
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/saveDeptEditIdea.f")
	public void saveDeptEditIdea(String deptLeadCircuseeId, String editIdeaContent,String responseIdeaContent, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			if(null!=editIdeaContent){
				//领导意见内容不为空,编辑状态
				List<DeptLeadEditIdea> list = o_deptLeadEditIdeaBO.findDeptLeadEditIdeaByDeptLeadCircuseeIdList(deptLeadCircuseeId);
				DeptLeadEditIdea edit = null;
				if(list.size() == 0){
					edit = new DeptLeadEditIdea();
					edit.setId(Identities.uuid());
				}else{
					edit = list.get(0);
				}
				
				DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
				deptLeadCircusee.setId(deptLeadCircuseeId);
				edit.setDeptLeadCircusee(deptLeadCircusee);
				edit.setEditIdeaContent(editIdeaContent);
				o_deptLeadEditIdeaBO.mergeDepteLeadEditIdea(edit);
			}		
			if(null!=responseIdeaContent){
				//领导意见内容不为空,编辑状态
				List<DeptLeadResponseIdea> list = o_deptLeadEditIdeaBO.findDeptLeadResponseIdeaByDeptLeadCircuseeIdList(deptLeadCircuseeId);
				DeptLeadResponseIdea edit = null;
				if(list.size() == 0){
					edit = new DeptLeadResponseIdea();
					edit.setId(Identities.uuid());
				}else{
					edit = list.get(0);
				}
				DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
				deptLeadCircusee.setId(deptLeadCircuseeId);
				edit.setDeptLeadCircusee(deptLeadCircusee);
				edit.setEditIdeaContent(responseIdeaContent);
				o_deptLeadEditIdeaBO.mergeDepteLeadResponseIdea(edit);
			}
			out.write("true");
		} catch (Exception e) {
			
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	
	/**
	 * 保存修改意见
	 * @param objectDeptEmpId 综合打分ID
	 * @param editIdeaContent 意见内容
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/saveEditIdea.f")
	public void saveEditIdea(String objectDeptEmpId,String editIdeaContent,String responseText , HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			if(null!= editIdeaContent){
				//评估人员意见内容不为空,添加、编辑状态
				EditIdea editIdea = o_saveAssessBO.findEditIdeaByObjectDeptEmpId(objectDeptEmpId);
				if(null == editIdea){
					RangObjectDeptEmp rangObjectDeptEmp = o_rangObjectDeptEmpBO.findRangObjectDeptEmpById(objectDeptEmpId);
					editIdea = new EditIdea();
					editIdea.setId(Identities.uuid());
					editIdea.setObjectDeptEmpId(rangObjectDeptEmp);
					editIdea.setEditIdeaContent(editIdeaContent);
					/**
						rangObjectDeptEmp.setEditIdea(editIdea);
						o_assessTaskBO.saveObjectDeptEmp(rangObjectDeptEmp);
					*/
					o_saveAssessBO.mergeEditIdea(editIdea);
				}else{
					editIdea.setEditIdeaContent(editIdeaContent);
					o_saveAssessBO.mergeEditIdea(editIdea);
				}
			}
			/**
			 * add by songjia
			 * 保存应对措施
			 */
			if(StringUtils.isNotEmpty(responseText)){
				//根据id获取打分对象表
				ResponseIdea responseIdea = o_saveAssessBO.findResponseIdeaByObjectDeptEmpId(objectDeptEmpId);
				if(null == responseIdea){
					RangObjectDeptEmp rangObjectDeptEmp = o_rangObjectDeptEmpBO.findRangObjectDeptEmpById(objectDeptEmpId);
					responseIdea = new ResponseIdea();
					responseIdea.setId(Identities.uuid());
					responseIdea.setObjectDeptEmpId(rangObjectDeptEmp);
					responseIdea.setEditIdeaContent(responseText);
					o_saveAssessBO.mergeEditIdea(responseIdea);
				}else{
					responseIdea.setEditIdeaContent(responseText);
					o_saveAssessBO.mergeEditIdea(responseIdea);
				}
			}
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 评估计划新增严正
	 * @param parentId 上级风险ID
	 * @return VOID
	 * @author 金鹏祥
	 */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/saveTaskByEmpIdValidate.f")
	public void saveTaskByEmpIdValidate(String parentId, HttpServletResponse response, String executionId) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String assessPlanId = this.getAssessPlanId(null, executionId); //过滤评估计划ID
			String templateType = o_assessPlanBO.findRiskAssessPlanById(assessPlanId).getTemplateType(); //模板类型
			if(!"dim_template_type_custom".equalsIgnoreCase(templateType)){
				//其他模板
				out.write("true");
			}else{
				//自定义模板
				Risk risk =  o_riskBO.findRiskById(parentId);
				if(null != risk.getTemplate()){
					out.write("true");
				}else{
					out.write("false");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 评估计划新增
	 * @param parentId 上级风险ID
	 * @param riskId 风险ID
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/saveTaskByEmpId.f")
	public void saveTaskByEmpId(String parentId, String riskId, String assessPlanId, String executionId, HttpServletResponse response) {
		PrintWriter out = null;
		String templateId = ""; //模板ID
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID返回评估计划ID
		String scoreEmpId = UserContext.getUser().getEmpid(); //人员ID
		try {
			out = response.getWriter();
			RiskAssessPlan riskAssessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
			String templateType = riskAssessPlan.getTemplateType(); //模板类型
			if(!"dim_template_type_custom".equalsIgnoreCase(templateType)){
				//其他模板
				templateId = riskAssessPlan.getTemplate().getId();
				o_riskScoreBO.addRiskAndsaveAllBySome(assessPlanId, riskId, scoreEmpId, templateId);
				out.write("true");
			}else{
				Risk risk =  o_riskBO.findRiskById(parentId);
				if(null != risk.getTemplate()){
					templateId = risk.getTemplate().getId();
					o_riskScoreBO.addRiskAndsaveAllBySome(assessPlanId, riskId, scoreEmpId, templateId);
					out.write("true");
				}else{
					o_riskBO.delRisk(riskId);
					out.write("false");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 删除新增评估计划
	 * @param riskId 风险ID
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/deletetaskbysome.f")
	public void deleteTaskBySome(String riskId, String assessPlanId, HttpServletResponse response) {
		PrintWriter out = null;
		String scoreEmpId = UserContext.getUser().getEmpid(); //人员ID
		try {
			out = response.getWriter();
			o_riskScoreBO.deleteRiskAndDeleteAllBySome(assessPlanId, riskId, scoreEmpId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 导出评估预览Excel
	 * @param businessId	计划id
	 * @param exportFileName	excel文件名
	 * @param sheetName		sheet页名称
	 * @param headerData	列表数据
	 * @return VOID
	 * @author 王再冉
	 */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/exportAssessShowGrid.f")
	public void exportAssessShowGrid(String businessId, String exportFileName, String sheetName, String headerData, 
									String empId, HttpServletRequest request, HttpServletResponse response,
									String executionId) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		String scoreEmpId = "";
		String companyid = "";
		if(StringUtils.isBlank(businessId)){
			businessId = this.getAssessPlanId(businessId, executionId);
		}
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0alarm_startus");//风险水平
		List<Map<String,String>> dictRiskLevelMapList = new ArrayList<Map<String,String>>();
		for(DictEntry dict : dictEntryList){
			Map<String,String> dictRiskLevelMap = new HashMap<String, String>();
			dictRiskLevelMap.put(dict.getValue(), dict.getName());
			dictRiskLevelMapList.add(dictRiskLevelMap);
		}
		if(StringUtils.isBlank(empId)){
			scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
			companyid = UserContext.getUser().getCompanyid();
		}else{//问卷监控页面的导出，按人员查找
			scoreEmpId = empId;
			SysEmployee emp = o_empBO.findEmpEntryByEmpId(empId);
			companyid = emp.getSysOrganization().getCompany().getId();
		}
		
		ArrayList<HashMap<String, Object>> mapList = 
				o_showAssessBO.findRiskByScoreEmpId(null,scoreEmpId, o_showAssessBO.getTemplateId(businessId), true, businessId, companyid);
		
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		
		
		for(HashMap<String, Object> map : mapList){
			objects = new Object[j];
			for(int m=0;m<indexList.size();m++){
				if(null!=map.get(indexList.get(m))){
					if("riskIcon".equals(indexList.get(m))){//风险水平
						for(Map<String,String> dictMap : dictRiskLevelMapList){
							String mkey = map.get(indexList.get(m)).toString();
							if(null != dictMap.get(mkey)){
								objects[m] = dictMap.get(mkey);
							}
						}
					}else{
						objects[m] = map.get(indexList.get(m)).toString();
					}
				}
			}
			list.add(objects);
		}
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "评估结果预览数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 导出评估审阅Excel
	 * @param businessId	计划id
	 * @param exportFileName	excel文件名
	 * @param sheetName		sheet页名称
	 * @param executionId	工作流执行id
	 * @return VOID
	 * @author 王再冉
	 */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/exportfindleaderdeptgrid.f")
	public void exportfindLeaderDeptGrid(String businessId, String exportFileName, 
			String sheetName, String executionId, String headerData, String isLeader,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		businessId = this.getAssessPlanId(businessId, executionId);
		String orgEmpId = UserContext.getUser().getEmpid();// 承办人ID
		String companyId = UserContext.getUser().getCompanyid();
		String orgId = "";
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0alarm_startus");//风险水平
		List<Map<String,String>> dictRiskLevelMapList = new ArrayList<Map<String,String>>();
		for(DictEntry dict : dictEntryList){
			Map<String,String> dictRiskLevelMap = new HashMap<String, String>();
			dictRiskLevelMap.put(dict.getValue(), dict.getName());
			dictRiskLevelMapList.add(dictRiskLevelMap);
		}
		
		try {
			orgId = o_sysEmpOrgBO.queryOrgByEmpid(orgEmpId).getId();// 部门领导ID
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ArrayList<String> scoreEmpIdList = new ArrayList<String>();
		List<SysEmployee> empOrgBOList = o_riskScoreBO.findAllEmpsBydeptId(orgId); //查询此部门下人员集合
		for (SysEmployee sysEmployee : empOrgBOList) {
			if(null != sysEmployee){
				scoreEmpIdList.add(sysEmployee.getId());
			}
		}
		
		List<String> orgList = o_riskScoreBO.findAllDeptIdsBydeptId(orgId);
		orgList.add(orgId);
		
		ArrayList<HashMap<String, Object>> mapList = null;
		if(StringUtils.isNotBlank(isLeader)){//多级审批（风险部门主管、领导审批），需要导出计划下所有评估风险
			mapList = o_approvalAssessBO.findLeaderGridAssessPlan(null, businessId, companyId);
		}else{//子流程审批导出，由评估人条件查找
			mapList = o_rangObjectDeptEmpBO.findAssessByDept(null, businessId, orgList, companyId, scoreEmpIdList);
		}
		
		
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		
		for(HashMap<String, Object> map : mapList){
			objects = new Object[j];
			for(int m=0;m<indexList.size();m++){
				if(null!=map.get(indexList.get(m))){
					if("riskIcon".equals(indexList.get(m))||"isSummarizingRiskIcon".equals(indexList.get(m))){//风险水平
						for(Map<String,String> dictMap : dictRiskLevelMapList){
							String mkey = map.get(indexList.get(m)).toString();
							if(null != dictMap.get(mkey)){
								objects[m] = dictMap.get(mkey);
							}
						}
					}else{
						objects[m] = map.get(indexList.get(m)).toString();
					}
				}
			}
			list.add(objects);
		}
		
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "评估任务审批数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 判断是否有文件准则下载
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/isFlie.f")
	public Map<String, Object> isFlie(String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId); //通过流程ID返回评估计划ID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> result = new HashMap<String, Object>();
		RiskAssessPlan riskssessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
		if(riskssessPlan.getTemplate() != null){
			result.put("assessFileId", riskssessPlan.getTemplate().getId()); //计划名称
		}else{
			result.put("assessFileId", "null"); //计划名称
		}
		result.put("success", true);

		return result;
	}
}