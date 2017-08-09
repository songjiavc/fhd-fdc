package com.fhd.ra.business.assess.quaassess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreInstanceBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;

@Service
public class RiskQuaAssessBO {

	@Autowired
	private QuaAssessNextBO o_quaAssessNextBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private ScoreInstanceBO o_scoreInstanceBO;
	
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	/**
	 * 针对风险组织数据供应给前台使用
	 * */
	private HashMap<String, Object> getRiskScoreListHtml(String scoreDimId, ArrayList<String> templateDicDescAllMap, 
			String dimName, DictEntry dictEntry, Double weight, String parentDimId, String rangObjectDeptEmpId, 
			HashMap<String, ArrayList<String>> riskAdjusthistoryDimScoreAllMap, HashMap<String, String> maps, String riskId,
			String templateId){
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		ArrayList<String> dicNameArray = new ArrayList<String>();
		String divId = scoreDimId;
		boolean isParentDimId = false;
		String html = "";
		int count = 1;
		ArrayList<String> dimAndScoreList = riskAdjusthistoryDimScoreAllMap.get(riskId);
		
		String dimId = "";
		String scoreDicValue = "";
		String dimNames = "";
		String scoreDicName = "";
		String dimaNameTitle = "";
		
		for (String templateDicDescAll : templateDicDescAllMap) {
			String[] str = templateDicDescAll.split("--");
			dimId = str[0];
			scoreDicValue = str[2];
			dimNames = str[4];
			scoreDicName = str[5];
			
			if(dimId.equalsIgnoreCase(scoreDimId)){
				dimaNameTitle = dimNames;
				if(null != parentDimId){
					isParentDimId = true;
				}
				
				if(null != dimAndScoreList){
					int counts = 0;
					for (String string : dimAndScoreList) {
						String strs = dimId + "--" + scoreDicValue.toString();
						if(strs.equalsIgnoreCase(string)){
							dicNameArray.add("--" + scoreDicName + "--" + scoreDimId +  "--" + rangObjectDeptEmpId + "--" + 
									scoreDicValue.replace(".", "").replace("0", "")
									+ "--" + weight + "--" + parentDimId + "--" + "true," + count +"" + "--");
							counts = 1;
							break;
						}
					}if(counts == 0){
						dicNameArray.add("--" + scoreDicName + "--" + scoreDimId +  "--" + rangObjectDeptEmpId + "--" + 
								scoreDicValue.replace(".", "").replace("0", "")
								+ "--" + weight + "--" + parentDimId+ "--" + "false," + count +"" + "--");
					}
				}else{
					dicNameArray.add("--" + scoreDicName + "--" + scoreDimId +  "--" + rangObjectDeptEmpId + "--" + 
							scoreDicValue.replace(".", "").replace("0", "")
							+ "--" + weight + "--" + parentDimId+ "--" + "false," + count +"" + "--");
				}
				
				
				count++;
			}
		}
		
		if(isParentDimId){
			html = o_quaAssessNextBO.getHtml(divId + "--" + templateId, dimaNameTitle, dicNameArray.toArray(), dimName, maps, riskId);
		}else{
			html = o_quaAssessNextBO.getHtml(divId + "--" + templateId, dimaNameTitle, dicNameArray.toArray(), null, maps, riskId);
		}
		 
		map.put("divId", divId + "--" + templateId);
		map.put("html", html);
		
		return map;
	}
	
	/**
	 * 针对风险组织数据供应给前台使用
	 * */
	private HashMap<String, Object> getRiskScoreListHtmlInit(String scoreDimId, ArrayList<String> templateDicDescAllMap, 
			String dimName, DictEntry dictEntry, Double weight, String parentDimId, String rangObjectDeptEmpId, 
			ArrayList<HashMap<String, Object>> arrays, String templateId){
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		ArrayList<String> dicNameArray = new ArrayList<String>();
		String divId = scoreDimId;
		boolean isParentDimId = false;
		String html = "";
		int count = 1;
		
		
		String dimId = "";
		String scoreDicValue = "";
		String dimNames = "";
		String scoreDicName = "";
		String dimaNameTitle = "";
		
		for (String templateDicDescAll : templateDicDescAllMap) {
			String[] str = templateDicDescAll.split("--");
			dimId = str[0];
			scoreDicValue = str[2];
			dimNames = str[4];
			scoreDicName = str[5];
			
			if(dimId.equalsIgnoreCase(scoreDimId)){
				dimaNameTitle = dimNames;
				if(null != parentDimId){
					isParentDimId = true;
				}
			
				dicNameArray.add("--" + scoreDicName + "--" + scoreDimId +  "--" + rangObjectDeptEmpId + "--" + 
						scoreDicValue.replace(".", "").replace("0", "") + "--" + weight + "--" + parentDimId+ "--" + "false," + count +"" + "--");
				
				count++;
			}
		}
		
		if(isParentDimId){
			html = o_quaAssessNextBO.getHtmlInit(divId + "--" + templateId, dimaNameTitle, dicNameArray.toArray(), dimName, arrays);
		}else{
			html = o_quaAssessNextBO.getHtmlInit(divId + "--" + templateId, dimaNameTitle, dicNameArray.toArray(), null, arrays);
		}
		 
		map.put("divId", divId + "--" + templateId);
		map.put("html", html);
		
		return map;
	}
	
	/**
	 * 针对风险、得到模板对应维度及分数
	 * @param templateId 模板ID
	 * @return ArrayList<Score>
	 * @author 金鹏祥
	 * */
	public HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> findTemplateRelaDimByTemplateIdHtmlInit(String params) {
		HashMap<String, ArrayList<String>> templateDicDescAllMap = o_scoreInstanceBO.findTemplateDicDescAllMap();
		List<TemplateRelaDimension> templateRelaDimensionAllList = o_templateRelaDimensionBO.findTemplateRelaDimensionAllList();
		List<TemplateRelaDimension> templateRelaDimensionList = null;
		List<TemplateRelaDimension> templateRelaDimensionList2 = null;
		
		HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> arrayListCount = 
				new HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>>();
		
		JSONArray jsonarr = JSONArray.fromObject(params);

		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			ArrayList<ArrayList<HashMap<String, Object>>> arrayList = new ArrayList<ArrayList<HashMap<String, Object>>>();
			templateRelaDimensionList = o_quaAssessNextBO.getTemplateRelaDimensionListByTemplateId(jsobjs.getString("templateId"), 
					templateRelaDimensionAllList);
			for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionList) {
				
				if(templateRelaDimension.getParent() == null){
					templateRelaDimensionList2 = o_quaAssessNextBO.getTemplateRelaDimensionListByParentId(templateRelaDimension.getId(), 
							templateRelaDimensionAllList);
					ArrayList<HashMap<String, Object>> arrays = new ArrayList<HashMap<String, Object>>();
					
					if(templateRelaDimensionList2.size() != 0){
						//存在下级
						for (TemplateRelaDimension template : templateRelaDimensionList2) {
							HashMap<String, Object> mapObj = 
									this.getRiskScoreListHtmlInit(
											template.getDimension().getId(), 
											templateDicDescAllMap.get(jsobjs.getString("templateId")), 
											templateRelaDimension.getDimension().getName(), 
											templateRelaDimension.getCalculateMethod(), 
											templateRelaDimension.getWeight(), 
											templateRelaDimension.getDimension().getId(),
											jsobjs.getString("rangObjectDeptEmpId"),
											arrays,
											jsobjs.getString("templateId"));
							arrays.add(mapObj);
						}
						arrayList.add(arrays);
					}else{
						//不存在下级
						HashMap<String, Object> mapObj = 
								this.getRiskScoreListHtmlInit(
										templateRelaDimension.getDimension().getId(), 
										templateDicDescAllMap.get(jsobjs.getString("templateId")), 
										templateRelaDimension.getDimension().getName(), 
										null, 
										null, 
										null,
										jsobjs.getString("rangObjectDeptEmpId"),
										null,
										jsobjs.getString("templateId"));
						arrays.add(mapObj);
						
						arrayList.add(arrays);
					}
				}
			}
			arrayListCount.put("risk", arrayList);
		}
		
		return arrayListCount;
	}
	
	/**
	 * 针对风险、得到模板对应维度及分数
	 * @param templateId 模板ID
	 * @return ArrayList<Score>
	 * @author 金鹏祥
	 * */
	public HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> findTemplateRelaDimByTemplateIdHtml(String params) {
		HashMap<String, ArrayList<String>> templateDicDescAllMap = o_scoreInstanceBO.findTemplateDicDescAllMap();
		List<TemplateRelaDimension> templateRelaDimensionAllList = o_templateRelaDimensionBO.findTemplateRelaDimensionAllList();
		HashMap<String, ArrayList<String>> riskAdjusthistoryDimScoreAllMap = o_riskAdjustHistoryBO.findRiskAdjusthistoryDimScoreAllMap();
		List<TemplateRelaDimension> templateRelaDimensionList = null;
		List<TemplateRelaDimension> templateRelaDimensionList2 = null;
		
		HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> arrayListCount = 
				new HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>>();
		HashMap<String, String> maps = new HashMap<String, String>();
		
		JSONArray jsonarr = JSONArray.fromObject(params);

		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			ArrayList<ArrayList<HashMap<String, Object>>> arrayList = new ArrayList<ArrayList<HashMap<String, Object>>>();
			templateRelaDimensionList = o_quaAssessNextBO.getTemplateRelaDimensionListByTemplateId(jsobjs.getString("templateId"), 
					templateRelaDimensionAllList);
			for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionList) {
				
				if(templateRelaDimension.getParent() == null){
					templateRelaDimensionList2 = o_quaAssessNextBO.getTemplateRelaDimensionListByParentId(templateRelaDimension.getId(), 
							templateRelaDimensionAllList);
					ArrayList<HashMap<String, Object>> arrays = new ArrayList<HashMap<String, Object>>();
					
					if(templateRelaDimensionList2.size() != 0){
						//存在下级
						for (TemplateRelaDimension template : templateRelaDimensionList2) {
							HashMap<String, Object> mapObj = 
									this.getRiskScoreListHtml(
											template.getDimension().getId(), 
											templateDicDescAllMap.get(jsobjs.getString("templateId")), 
											templateRelaDimension.getDimension().getName(), 
											templateRelaDimension.getCalculateMethod(), 
											templateRelaDimension.getWeight(), 
											templateRelaDimension.getDimension().getId(),
											jsobjs.getString("rangObjectDeptEmpId"),
											riskAdjusthistoryDimScoreAllMap, maps, jsobjs.getString("riskId"),
											jsobjs.getString("templateId"));
							arrays.add(mapObj);
						}
						arrayList.add(arrays);
					}else{
						//不存在下级
						HashMap<String, Object> mapObj = 
								this.getRiskScoreListHtml(
										templateRelaDimension.getDimension().getId(), 
										templateDicDescAllMap.get(jsobjs.getString("templateId")), 
										templateRelaDimension.getDimension().getName(), 
										null, 
										null, 
										null,
										jsobjs.getString("rangObjectDeptEmpId"),
										riskAdjusthistoryDimScoreAllMap, maps, jsobjs.getString("riskId"),
										jsobjs.getString("templateId"));
						arrays.add(mapObj);
						
						arrayList.add(arrays);
					}
				}
			}
			arrayListCount.put(jsobjs.getString("riskId"), arrayList);
		}
		
		return arrayListCount;
	}
}
