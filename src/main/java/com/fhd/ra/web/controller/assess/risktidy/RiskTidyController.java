package com.fhd.ra.web.controller.assess.risktidy;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.approval.RiskTidyBO;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.DeptLeadCircuseeBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.quaassess.CountAssessBO;
import com.fhd.ra.business.assess.quaassess.QuaAssessBO;
import com.fhd.ra.business.assess.risktidy.RiskTidyCountBO;
import com.fhd.ra.business.assess.risktidy.SaveRiskTidyBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.assess.summarizing.RiskFormulaSummarizingBO;
import com.fhd.ra.business.assess.summarizing.util.KpiOperBO;
import com.fhd.ra.business.assess.summarizing.util.OrgOperBO;
import com.fhd.ra.business.assess.summarizing.util.ProcessureOperBO;
import com.fhd.ra.business.assess.summarizing.util.RiskRbsOperBO;
import com.fhd.ra.business.assess.summarizing.util.StrategyMapOperBO;
import com.fhd.ra.business.riskidentify.RiskIdentifyBO;
import com.fhd.sys.business.assess.WeightSetBO;
import com.fhd.sys.business.dic.DictBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 风险整理、统计执行操作
 * */

@Controller
public class RiskTidyController {
	
	@Autowired
	private ShowRiskTidyBO o_showRiskTidyBO;
	
	@Autowired
	private SaveRiskTidyBO o_saveRiskTidyBO;
	
	@Autowired
	private RiskTidyCountBO o_riskTidyCountBO;
	
	@Autowired
	private CountAssessBO o_countAssessBO;
	
	@Autowired
	private RiskFormulaSummarizingBO o_riskFormulaSummarizingBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private RiskRbsOperBO o_riskRbsOperBO;
	
	@Autowired
	private KpiOperBO o_kpiOperBO;
	
	@Autowired
	private OrgOperBO o_orgOperBO;
	
	@Autowired
	private StrategyMapOperBO o_strateMapOperBO;
	
	@Autowired
	private ProcessureOperBO o_processureOperBO;
	
	@Autowired
	private JBPMBO o_jbpmBO;
	
	@Autowired
	private RiskAssessPlanBO o_planBO;
	
	@Autowired
	private DictBO o_dictBO;
	
	@Autowired
	private DeptLeadCircuseeBO o_deptLeadCircuseeBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private AdjustHistoryResultBO o_adjustHistoryResultBO;
	
	@Autowired
	private RiskTidyBO o_riskTidyBO;
	@Autowired
	private ScoreObjectBO o_scoreObiectBO;
	@Autowired
	private QuaAssessBO o_quaAssessBO;
	
	@Autowired
	private RiskIdentifyBO o_riskIdentifyBO;
	
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
	 * 风险分类、部门、目标、流程下的风险事件
	 * @param query 条件查询
	 * @param assessPlanId 评估计划ID
	 * @param typeId 操作类型ID
	 * @param 操作类型
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/findRiskReByRisk.f")
	public List<Map<String, String>> findRiskReByRisk(String query, String assessPlanId, String typeId, String type) {
		//风险事件GRID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		String companyId = UserContext.getUser().getCompanyid();
		return o_showRiskTidyBO.findRiskReListSf2(query, assessPlanId, typeId, type,companyId);
	}
	
	/** 
	  * @Description: 风险辨识汇总分数针对于保密风险的查询
	  * @author jia.song@pcitc.com
	  * @date 2017年5月18日 下午2:45:38 
	  * @param query
	  * @param assessPlanId
	  * @param typeId
	  * @param type
	  * @return 
	  */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/findRiskReByRiskForSecurity.f")
	public List<Map<String,String>> findRiskReByRiskForSecurity(String query, String assessPlanId, String typeId, String type){
		//风险事件GRID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		String companyId = UserContext.getUser().getCompanyid();
		return o_showRiskTidyBO.findRiskReListSf2ForSecurity(query, assessPlanId, typeId, type,companyId);
	}
	

	/** 
	  * @Description: 根据计划查询出所有的辨识风险汇总意见列表
	  * @author jia.song@pcitc.com
	  * @date 2017年5月25日 下午4:30:50 
	  * @param query
	  * @param assessPlanId
	  * @param typeId
	  * @param type
	  * @return 
	  */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/findTidyRiskReByRiskForSecurity.f")
	public List<Map<String,String>> findTidyRiskReByRiskForSecurity(String query, String assessPlanId, String typeId, String type){
		//风险事件GRID
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		return o_showRiskTidyBO.findTidyRiskReListSf2ForSecurity(query, assessPlanId, typeId, type);
	}
 
	
	/**
	 * 保存评估
	 * @param params 风险信息参数
	 * @param assessPlanId 评估计划ID
	 * @return Map<String, Object>
	 * @author 金鹏祥
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/riskTidySaveAssess.f")
	public Map<String, Object> riskTidySaveAssess(String params, String assessPlanId) {
		assessPlanId = this.getFilterStr(assessPlanId); //过滤评估计划ID
		Map<String, Object> result = new HashMap<String, Object>(); //数据信息键值
		if(o_saveRiskTidyBO.saveAssessSf2(params, assessPlanId)){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		return result;
	}
	
	/**
	 * 汇总
	 * @param assessPlanId 评估计划ID
	 * @param executionId 流程ID
	 * @param type 启用流程ID类型
	 * @return Map<String, Object>
	 * @author 金鹏祥 
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/riskTidySummarizing.f")
	public Map<String, Object> riskTidySummarizing(String assessPlanId, String executionId, String type) {
		assessPlanId = this.getFilterStr(assessPlanId);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//通过待办任务进入到汇总整理中后，首先判断是否已经有计算结果，如果有则不进行重新计算
			Integer count = o_adjustHistoryResultBO.getHistoryResultCountByAssessPlanId(assessPlanId);
			if(count == 0){
				o_riskFormulaSummarizingBO.summarizing(assessPlanId);
			}
			if(StringUtils.isNotBlank(type)){
				result.put("type", o_jbpmBO.findPDDByExecutionId(executionId));
			}
			result.put("success", true);
		} catch (Exception e) {
			result.put("success", false);
			System.out.println("汇总出错==================" + e.getMessage() + "============================");
		}
		return result;
	}
	

	/**
	 * 初始化统计
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/delRiskTidySummarizing.f")
	public Map<String, String> delRiskTidySummarizing(String assessPlanId, HttpServletResponse response) {
		Map<String, String> returnMap = new HashMap<String, String>();
		assessPlanId = this.getFilterStr(assessPlanId);
		//o_riskFormulaSummarizingBO.deleteHistoryResult(assessPlanId);
		o_riskFormulaSummarizingBO.summarizing(assessPlanId);
	//	String ids =  o_showRiskTidyBO.findRiskReList(assessPlanId);
		
		//returnMap.put("ids", ids);
		returnMap.put("success", "true");
		
		return returnMap;
	}
	
	/** 
	  * @Description: 宋佳增加重新计算功能，根据评估公式重新计算综合分数
	  * @author jia.song@pcitc.com
	  * @date 2017年5月5日 下午3:12:50 
	  * @param assessPlanId
	  * @return 
	  */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/afreshCalu.f")
	public Map<String,String> afreshCalu(String assessPlanId){
		Map<String, String> returnMap = new HashMap<String, String>();
		assessPlanId = this.getFilterStr(assessPlanId);
		o_riskFormulaSummarizingBO.afreshCalu(assessPlanId);
		returnMap.put("success", "true");
		return returnMap;
	}
	
	/**
	 * 汇总JS验证
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/isRiskTidySummarizing.f")
	public Map<String, Object> isRiskTidySummarizing(String assessPlanId) {
		assessPlanId = this.getFilterStr(assessPlanId);
		Map<String, Object> result = new HashMap<String, Object>();
		List<RiskAdjustHistory> riskAdjustHistoryByAssessPlanIdAllList = 
				o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		if(riskAdjustHistoryByAssessPlanIdAllList.size() == 0){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		
		return result;
	}
	
	/**
	 * 通过风险ID集合查询已评估风险总数、未评估总数、已评估总数
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/findRiskTidyAssessCount.f")
	public Map<String,Object> findRiskTidyAssessCount(String assessPlanId, String params) {
		assessPlanId = this.getFilterStr(assessPlanId);
		Map<String, Object> result = new HashMap<String, Object>();
		RiskAssessPlan riskssessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
		JSONArray riskReAll = o_riskTidyCountBO.findRiskReAll(assessPlanId);
		HashMap<String, Integer> map = o_countAssessBO.getAssessCountMap(null, riskReAll.toString(), assessPlanId);
		
		result.put("assessPlanName", riskssessPlan.getPlanName());//计划名称
		result.put("isAssessCount", map.get("isAssessCount"));//已评估数
		result.put("isNotAssessCount", map.get("isNotAssessCount"));//未评估数
		result.put("totalCount", map.get("totalCount"));//评估总数
		result.put("success", true);
		
		return result;
	}
	
	/**
	 * 风险分类、事件树
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/riskRbsOrRe.f")
	public Map<String, String> riskRbsOrRe(String assessPlanId, String executionId, String type) {
		assessPlanId = this.getFilterStr(assessPlanId);
		Map<String, String> returnMap = new HashMap<String, String>();
		String ids =  o_showRiskTidyBO.findRiskReList(assessPlanId);
		
		if(StringUtils.isNotBlank(type)){
			returnMap.put("type", o_jbpmBO.findPDDByExecutionId(executionId));
		}
		
		returnMap.put("success", "true");
		returnMap.put("ids", ids);
		
		return returnMap;
	}
	
	/**
	 * 删除风险事件
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/delRiskRbs.f")
	public Map<String, Object> delRiskRbs(String ids, String assessPlanId) {
		assessPlanId = this.getFilterStr(assessPlanId);
		Map<String, Object> result = new HashMap<String, Object>();
		
		//o_saveRiskTidyBO.delAssessRiskRe(ids, assessPlanId);
		String riskIds[] = ids.split(",");
		for (String riskId : riskIds) {
			//查询打分对象
			RiskScoreObject riskScoreObject = o_scoreObiectBO.findRiskScoreObjectByRiskId(riskId, assessPlanId);
			riskScoreObject.setDeleteStatus("100");
			o_quaAssessBO.updateRiskScoreObject(riskScoreObject);
		}
		String treeIds =  o_showRiskTidyBO.findRiskReList(assessPlanId);
		
		result.put("success", true);
		result.put("treeIds", treeIds);
		return result;
	}
	
	
	/** 
	  * @Description: 评估结果整理时删除风险事件
	  * @author jia.song@pcitc.com
	  * @date 2017年5月10日 下午3:48:42
	  * @param ids
	  * @param assessPlanId
	  * @return 
	  */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/delRiskRbsSf2.f")
	public Map<String, Object> delRiskRbsSf2(String[] ids, String assessPlanId) {
		assessPlanId = this.getFilterStr(assessPlanId);
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> objectIdList = new ArrayList<String>();
		for(String id : ids){
			objectIdList.add(id);
		}
		o_saveRiskTidyBO.delAssessRiskReSf2(objectIdList, assessPlanId);
		result.put("success", true);
		return result;
	}
	
	/** 
	  * @Description: 评估结果整理时删除风险事件
	  * @author jia.song@pcitc.com
	  * @date 2017年5月10日 下午3:48:42
	  * @param ids
	  * @param assessPlanId
	  * @return 
	  */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/delRiskRbsSf2ForSecurity.f")
	public Map<String, Object> delRiskRbsSf2ForSecurity(String[] ids, String assessPlanId) {
		assessPlanId = this.getFilterStr(assessPlanId);
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> objectIdList = new ArrayList<String>();
		for(String id : ids){
			objectIdList.add(id);
		}
		o_saveRiskTidyBO.delAssessRiskReSf2ForSecurity(objectIdList, assessPlanId);
		result.put("success", true);
		return result;
	}
	
	
	/**
	 * 提交
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/submitRiskTidyForSecurity.f")
	public void submitRiskTidyForSecurity(String executionId, String businessId, String approverId, String description, HttpServletResponse response) {
//		String assessPlanId = this.getFilterStr(businessId);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			//bussiness exec
			//o_riskTidyBO.execRiskTidy(businessId);
			//jbpm exec
			Map<String, Object> variables = new HashMap<String, Object>();
			String approveForeId = "";
			if(StringUtils.isNotBlank(approverId)){
				JSONArray jsonArray = JSONArray.fromObject(approverId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 approveForeId = jsobj.getString("id");//审批人
				}
				variables.put("approveFore", approveForeId);//单位主管
			}
			o_jbpmBO.doProcessInstance(executionId, variables);
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
	 * 提交
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/submitRiskTidy.f")
	public void submitRiskTidy(String executionId, String assessPlanId, HttpServletResponse response) {
		assessPlanId = this.getFilterStr(assessPlanId);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, Object> variables = new HashMap<String, Object>();
			o_riskRbsOperBO.updateRiskAdjustHistoryIsLatestIsZero(assessPlanId);
			o_riskRbsOperBO.updateRiskAdjustHistoryIsLatestIsOne(assessPlanId, true);
			o_orgOperBO.updateOrgAdjustHistoryIsLatestIsOne(assessPlanId, true);
			o_kpiOperBO.updateProcessAdjustHistoryIsLatestIsOne(assessPlanId, true);
			o_strateMapOperBO.updateStrategyAdjustHistoryIsLatestIsOne(assessPlanId, true);
			o_processureOperBO.updateProcessAdjustHistoryIsLatestIsOne(assessPlanId, true);
			o_jbpmBO.doProcessInstance(executionId, variables);
			RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
			assessPlan.setDealStatus(Contents.DEAL_STATUS_FINISHED);//点结束工作流按钮，处理状态为"已完成"
			o_planBO.mergeRiskAssessPlan(assessPlan);
			//TODO 宋佳 评估计划执行完毕后，更新风险分类汇总分数，一直汇总到最上级
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
	  * @Description: 风险评估流程完毕，更新风险库，并统计风险分类综合分数
	  * @author jia.song@pcitc.com
	  * @date 2017年5月11日 上午8:53:38 
	  * @param executionId
	  * @param assessPlanId
	  * @param response 
	  */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/submitRiskTidySf2.f")
	public void submitRiskTidySf2(String executionId, String assessPlanId, HttpServletResponse response) {
		assessPlanId = this.getFilterStr(assessPlanId);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			//更新工作流状态
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
			//TODO 宋佳 评估计划执行完毕后，更新风险分类汇总分数，一直汇总到最上级
			o_riskTidyBO.finishAccessPlan(assessPlanId);
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
	 * 更新风险库
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/updateRiskTidy.f")
	public void updateRiskTidy(String executionId, String assessPlanId, HttpServletResponse response) {
		assessPlanId = this.getFilterStr(assessPlanId);
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskRbsOperBO.updateRiskAdjustHistoryIsLatestIsZero(assessPlanId);
			o_riskRbsOperBO.updateRiskAdjustHistoryIsLatestIsOne(assessPlanId, false);
			o_orgOperBO.updateOrgAdjustHistoryIsLatestIsOne(assessPlanId, false);
			o_kpiOperBO.updateProcessAdjustHistoryIsLatestIsOne(assessPlanId, false);
			o_strateMapOperBO.updateStrategyAdjustHistoryIsLatestIsOne(assessPlanId, false);
			o_processureOperBO.updateProcessAdjustHistoryIsLatestIsOne(assessPlanId, false);
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
	 * 重新统计
	 * */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/afreshSummarizing.f")
	public Map<String, String> submitRiskTidy(String assessPlanId, HttpServletResponse response) {
		Map<String, String> returnMap = new HashMap<String, String>();
		assessPlanId = this.getFilterStr(assessPlanId);
		o_riskFormulaSummarizingBO.afreshSummarizing(assessPlanId);
		returnMap.put("success", "true");
		return returnMap;
	}
	
	/**
	 * 所有评估人相关信息
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/riskTidy/findRiskByAssessPlanIdAndRiskId.f")
	public List<Map<String, Object>> findRiskByAssessPlanIdAndRiskId(String scoreObjectId){
		String companyId = UserContext.getUser().getCompanyid();
		return o_showRiskTidyBO.findRiskByAssessPlanIdAndRiskId(scoreObjectId,companyId);
	}
	
	/**
	 * 部门风险管理员反馈信息
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/riskTidy/findRiskByDeptRiskIdea.f")
	public List<Map<String, Object>> findRiskByDeptRiskIdea(String scoreObjectId){
		return o_deptLeadCircuseeBO.findRiskByDeptRiskIdea(scoreObjectId);
	}
	
	/**
	 * 导出评估结果整理Excel
	 * @param businessId		计划id
	 * @param exportFileName	excel文件名	
	 * @param sheetName			sheet页名称
	 * @param headerData		列表数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/assess/riskTidy/exportrisktidygrid.f")
	public void exportRiskTidyGrid(String businessId, String exportFileName, String sheetName, String headerData,
			String type, String typeId, HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0alarm_startus");//风险水平
		List<Map<String,String>> dictRiskLevelMapList = new ArrayList<Map<String,String>>();
		for(DictEntry dict : dictEntryList){
			Map<String,String> dictRiskLevelMap = new HashMap<String, String>();
			dictRiskLevelMap.put(dict.getValue(), dict.getName());
			dictRiskLevelMapList.add(dictRiskLevelMap);
		}
		
		if("undefined".equals(type)){
			type = null;
		}
		if("undefined".equals(typeId)){
			typeId = null;
		}
		
		ArrayList<HashMap<String, String>> mapList = o_showRiskTidyBO.findRiskReList(null, businessId, typeId, type);
		
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		
		
		for(HashMap<String, String> map : mapList){
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
					}else if("riskStatus".equals(indexList.get(m))){//状态
						if("icon-status-assess_new".equals(map.get(indexList.get(m)).toString())){
							objects[m] = "新增";
						}else if("icon-status-assess_edit".equals(map.get(indexList.get(m)).toString())){
							objects[m] = "修改";
						}else{
							objects[m] = "";
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
	 * 查看tab签显示的权限
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/assess/risktidy/findsummarizingtab.f")
	public Map<String,Boolean> findSummarizingTab(){
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		HashMap<String, String> summarizingMap = o_weightSetBO.findInitValue("orgSummarizing,kpiSummarizing,strategySummarizing,processSummarizing");
		boolean orgSummarizing = false;
		boolean kpiSummarizing = false;
		boolean strategySummarizing = false;
		boolean processSummarizing = false;
		
		if(summarizingMap.size() != 0){
			if(summarizingMap.get("orgSummarizing").indexOf("True") != -1){
				orgSummarizing = true;
			}if(summarizingMap.get("kpiSummarizing").indexOf("True") != -1){
				kpiSummarizing = true;
			}if(summarizingMap.get("strategySummarizing").indexOf("True") != -1){
				strategySummarizing = true;
			}if(summarizingMap.get("processSummarizing").indexOf("True") != -1){
				processSummarizing = true;
			}
		}else{
			orgSummarizing = true;
			kpiSummarizing = true;
			strategySummarizing = true;
			processSummarizing = true;
		}
		map.put("orgSummarizing", orgSummarizing);
		map.put("kpiSummarizing", kpiSummarizing);
		map.put("strategySummarizing", strategySummarizing);
		map.put("processSummarizing", processSummarizing);
		return map;
	}
	
}