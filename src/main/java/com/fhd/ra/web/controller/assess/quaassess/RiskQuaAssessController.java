package com.fhd.ra.web.controller.assess.quaassess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreInstanceBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.assess.quaassess.QuaAssessNextBO;
import com.fhd.ra.business.assess.quaassess.RiskQuaAssessBO;
import com.fhd.ra.business.risk.RiskBO;

@Controller
public class RiskQuaAssessController {
	
	@Autowired
	private RiskQuaAssessBO o_riskQuaAssessBO;
	
	@Autowired
	private QuaAssessNextBO o_quaAssessNextBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private ScoreInstanceBO o_scoreInstanceBO;
	
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	/**
	 * 风险定性评估
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/assessRiskFindRiskInfo.f")
	public Map<String, Object> assessFindRiskInfo(String params) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> listMap = o_riskQuaAssessBO.findTemplateRelaDimByTemplateIdHtmlInit(params);
		HashMap<Integer, ArrayList<ArrayList<HashMap<String, Object>>>> countScoreMap = new HashMap<Integer, ArrayList<ArrayList<HashMap<String, Object>>>>();
		HashMap<Integer, String> riskNameMap = new HashMap<Integer, String>();
		HashMap<String, String> dimDescAllMap = o_scoreInstanceBO.findDimDescAllMap();
		HashMap<String, String> dicDescAllMap = o_scoreInstanceBO.findDicDescAllMap();
		int count = 0;
		
		JSONArray jsonarr = JSONArray.fromObject(params);
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			String rangObjectDeptEmpId = jsobjs.getString("rangObjectDeptEmpId");
			
			Set<Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>>> key2 = listMap.entrySet();
			for (Iterator<Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>>> it2 = 
																	key2.iterator(); it2.hasNext();) {
				Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>> mapKey2 = it2.next();
				if ("risk".equals(mapKey2.getKey())) {
 					countScoreMap.put(count, listMap.get(mapKey2.getKey()));
 					
 					riskNameMap.put(count, "" + "--" + "risk" + "--" + rangObjectDeptEmpId + "--" + "false");
					
					break;
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
	 * 风险定性评估查询
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/quaassess/assessRiskFindRiskInfoByIds.f")
	public Map<String, Object> assessFindRiskInfoByIds(String params) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> listMap = o_riskQuaAssessBO.findTemplateRelaDimByTemplateIdHtml(params);
		HashMap<Integer, ArrayList<ArrayList<HashMap<String, Object>>>> countScoreMap = new HashMap<Integer, ArrayList<ArrayList<HashMap<String, Object>>>>();
		HashMap<Integer, String> riskNameMap = new HashMap<Integer, String>();
		HashMap<String, ArrayList<String>> riskAdjusthistoryDimScoreAllMap = o_riskAdjustHistoryBO.findRiskAdjusthistoryDimScoreAllMap();
		ArrayList<String> dimArrayList = null;
		HashMap<String, String> dimDescAllMap = o_scoreInstanceBO.findDimDescAllMap();
		HashMap<String, String> dicDescAllMap = o_scoreInstanceBO.findDicDescAllMap();
		int count = 0;
		JSONArray jsonarr = JSONArray.fromObject(params);
		HashMap<String, HashMap<String, TemplateRelaDimension>> templateRelaDimensionParentIdIsNullMapAll = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionParentIdIsNull();
		
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			String riskId = jsobjs.getString("riskId");
			String riskName = jsobjs.getString("riskName");
			String rangObjectDeptEmpId = jsobjs.getString("rangObjectDeptEmpId");
			
			Set<Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>>> key2 = listMap.entrySet();
			for (Iterator<Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>>> it2 = 
																	key2.iterator(); it2.hasNext();) {
				Entry<String, ArrayList<ArrayList<HashMap<String, Object>>>> mapKey2 = it2.next();
				if (riskId.equals(mapKey2.getKey())) {
					if(dimArrayList == null){
						Template template = o_riskBO.findRiskById(riskId).getTemplate();
						String templateId = "";
						if(template != null){
							templateId = template.getId();
						}
						dimArrayList = o_quaAssessNextBO.getDimByTemplatesId(templateId, templateRelaDimensionParentIdIsNullMapAll);
					}
					
 					countScoreMap.put(count, listMap.get(mapKey2.getKey()));
 					
 					if(riskAdjusthistoryDimScoreAllMap.size() != 0){
 						/*对一个已知不是null的值重复判断    王再冉修改*/
 						//if(null != riskAdjusthistoryDimScoreAllMap){
 	 						if(riskAdjusthistoryDimScoreAllMap.get(riskId).size() == dimArrayList.size()){
 	 							riskNameMap.put(count, riskName + "--" + riskId + "--" + rangObjectDeptEmpId + "--" + "true");
 	 						}else{
 	 							riskNameMap.put(count, riskName + "--" + riskId + "--" + rangObjectDeptEmpId + "--" + "false");
 	 						}
 	 					//}else{
 	 					//	riskNameMap.put(count, riskName + "--" + riskId + "--" + rangObjectDeptEmpId + "--" + "false");
 	 					//}
 					}else{
 						riskNameMap.put(count, riskName + "--" + riskId + "--" + rangObjectDeptEmpId + "--" + "false");
 					}
 					
 					
					
					break;
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
}
