package com.fhd.ra.business.assess.quaassess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.approval.ApplyContainer;
import com.fhd.ra.business.assess.oper.ScoreInstanceBO;
import com.fhd.ra.business.assess.oper.ScoreResultBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.ra.business.risk.TemplateBO;

@Service
public class QuaAssessNextBO {

	@Autowired
	private ScoreResultBO o_scoreResultBO;
	
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	@Autowired
	private ScoreInstanceBO o_scoreInstanceBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	@Autowired
	private TemplateBO o_templateBO;
	
	/**
	 * 得到此模板有多少维度
	 * */
	public ArrayList<String> getDimByTemplatesId(String templateId, 
			HashMap<String, HashMap<String, TemplateRelaDimension>> templateRelaDimensionParentIdIsNullMapAll){
		HashMap<String, TemplateRelaDimension> templateRelaDimensionParentIdIsNullList = 
				templateRelaDimensionParentIdIsNullMapAll.get(templateId);
		ArrayList<String> dimArrayList = new ArrayList<String>();
		
		Set<Entry<String, TemplateRelaDimension>> key2 = templateRelaDimensionParentIdIsNullList.entrySet();
		for (Iterator<Entry<String, TemplateRelaDimension>> it2 = key2.iterator(); it2.hasNext();) {
			String id = it2.next().getKey();
			String dimId = templateRelaDimensionParentIdIsNullList.get(id).getDimension().getId();
			dimArrayList.add(dimId);
		}
		return dimArrayList;
	}
	
	/**
	 * 得到匹配当前模板下的所有维度
	 * @param id 模版关联维度ID
	 * @param list 模版关联维度全部信息
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	public List<TemplateRelaDimension> getTemplateRelaDimensionListByTemplateId(String id, List<TemplateRelaDimension> list){
		List<TemplateRelaDimension> arrayList = new ArrayList<TemplateRelaDimension>();
		for (TemplateRelaDimension templateRelaDimension : list) {
			if(templateRelaDimension.getTemplate().getId().equalsIgnoreCase(id)){
				arrayList.add(templateRelaDimension);
			}
		}
		
		return arrayList;
	}
	
	/**
	 * 得到匹配当前维度的下级维度
	 * @param id 模版关联维度ID
	 * @param list 模版关联维度全部信息
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	public List<TemplateRelaDimension> getTemplateRelaDimensionListByParentId(String id, List<TemplateRelaDimension> list){
		List<TemplateRelaDimension> arrayList = new ArrayList<TemplateRelaDimension>();
		for (TemplateRelaDimension templateRelaDimension : list) {
			if(templateRelaDimension.getParent() != null){
				if(templateRelaDimension.getParent().getId().equalsIgnoreCase(id)){
					arrayList.add(templateRelaDimension);
				}
			}
		}
		
		return arrayList;
	}
	
	public String getHtml(String divId, String dimName, Object countStr[], String parentDimName, HashMap<String, String> maps, String riskId){
		String startStr = "";
		String parentDimNameTitle = "";
		if(parentDimName != null){
			if(maps.get(riskId) == null){
				
				parentDimNameTitle = "<div class='star'><span class='dimWidth'>"+ parentDimName	 +"</span></div>";
				maps.put(riskId, "");
			}
			if(dimName.length() > 4){
				startStr = "<div class='star yxcd' id=\"" + divId +"\">" + 
						"<span class='dimWidth'>"+ dimName +"</span>" + 
						"<ul>" ;
			}else{
				startStr = "<div class='star yxcd' id=\"" + divId +"\">" + 
						"<span class='dimWidth'>"+ dimName +"</span>" + 
						"<ul>" ;
			}
			
		}else{
			startStr = "<div class='star' id=\"" + divId +"\">" + 
					"<span class='dimWidth'>"+ dimName +"</span>" + 
					"<ul class='ulfsgl'>" ;
		}			
		
		StringBuffer liStr = new StringBuffer();
		for (int i = 0; i < countStr.length; i++) {
			//liStr += "<li><a href=\"javascript:;\">" + countStr[i] +"</a></li>";
			liStr = liStr.append("<li><a href=\"javascript:;\">" + countStr[i] +"</a></li>");
		}
		
		
		String endStr = "<div id='starp'></ul>" + 
				"<span></span>" + 
				"<p id='" + divId + "pid--count:'></p>" + 
			"</div>";
		return parentDimNameTitle + startStr + liStr.toString() + endStr;
	}
	
	public String getHtmlInit(String divId, String dimName, Object countStr[], String parentDimName, ArrayList<HashMap<String, Object>> arrays){
		String startStr = "";
		String parentDimNameTitle = "";
		if(parentDimName != null){
			if(arrays.size() == 0){
				parentDimNameTitle = "<div class='star'><span class='dimWidth'>"+ parentDimName	 +"</span></div>";
			}
			for (HashMap<String, Object> hashMap : arrays) {
				if(hashMap.get("html").toString().indexOf(parentDimName) != -1){
					
				}else{
					parentDimNameTitle = "<div class='star'><span class='dimWidth'>"+ parentDimName	 +"</span></div>";
					break;
				}
				
				break;
			}
			
			if(dimName.length() > 4){
				startStr = "<div class='star yxcd' id=\"" + divId +"\">" + 
						"<span class='dimWidth'>"+ dimName +"</span>" + 
						"<ul>" ;
			}else{
				startStr = "<div class='star yxcd' id=\"" + divId +"\">" + 
						"<span class='dimWidth'>"+ dimName +"</span>" + 
						"<ul>" ;
			}
			
		}else{
			startStr = "<div class='star' id=\"" + divId +"\">" + 
					"<span class='dimWidth'>"+ dimName +"</span>" + 
					"<ul class='ulfsgl'>" ;
		}			
		
		StringBuffer liStr = new StringBuffer();
		for (int i = 0; i < countStr.length; i++) {
			//liStr += "<li><a href=\"javascript:;\">" + countStr[i] +"</a></li>";
			liStr = liStr.append("<li><a href=\"javascript:;\">" + countStr[i] +"</a></li>");
		}
		
		
		String endStr = "</ul>" + 
				"<span></span>" + 
				"<p></p>" + 
			"</div>";
		return parentDimNameTitle + startStr + liStr.toString() + endStr;
	}
	
	/**
	 * 组织数据供应给前台使用
	 * @param scoreDimId 维度ID
	 * @param list 维度分值关联全部信息
	 * @param dictEntry 求值类型
	 * @param weight 权重
	 * @param rangObjectDeptEmpId 综合ID
	 * @param scoreResultAllMap 打分结果全部信息
	 * @author 金鹏祥
	 * @return ArrayList<ArrayList<String>>
	 * */
	private HashMap<String, Object> getScoreListHtml(String rangObjectDeptEmpId, 
			String riskId, String templateId, ApplyContainer applyContainer){
	
		String scoreDimId = applyContainer.getScoreDimId();
		ArrayList<String> templateDicDescAllMap = applyContainer.getTemplateDicDescAllMap();
		String dimName = applyContainer.getDimName();
		Double weight = applyContainer.getWeight();
		String parentDimId = applyContainer.getParentDimId();
		HashMap<String, ScoreResult> scoreResultAllMap = applyContainer.getScoreResultEnAllMap();
		HashMap<String, String> maps = applyContainer.getMaps();
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<String> dicNameArray = new ArrayList<String>();
		String divId = scoreDimId;
		boolean isParentDimId = false;
		String html = "";
		int count = 1;
		String dimId = "";
		String scoreDicId = "";
		String scoreDicValue = "";
		String dimaNameTitle = "";
		String dimNames = "";
		String scoreDicName = "";
		
		for (String templateDicDescAll : templateDicDescAllMap) {
			String[] str = templateDicDescAll.split("--");
			dimId = str[0];
			scoreDicId = str[1];
			scoreDicValue = str[2];
			dimNames = str[4];
			scoreDicName = str[5];
			
			if(dimId.equalsIgnoreCase(scoreDimId)){
				dimaNameTitle = dimNames;
				dimNames = str[4];
				if(null != parentDimId){
					isParentDimId = true;
				}
				
				if(scoreResultAllMap.get(scoreDicId + "--" + rangObjectDeptEmpId) != null){
					dicNameArray.add("--" + scoreDicName + "--" + scoreDimId +  "--" + rangObjectDeptEmpId + "--" + 
							scoreDicValue.replace(".", "").replace("0", "") + "--" + weight + "--" + parentDimId + "--" + "true," + count +"" + "--");
				}else{
					dicNameArray.add("--" + scoreDicName + "--" + scoreDimId +  "--" + rangObjectDeptEmpId + "--" + 
							scoreDicValue.replace(".", "").replace("0", "") + "--" + weight + "--" + parentDimId+ "--" + "false," + count +"" + "--");
				}
				
				count++;
			}
		}
		
		if(isParentDimId){
			html = this.getHtml(divId + "--" + templateId, dimaNameTitle, dicNameArray.toArray(), dimName, maps, riskId);
		}else{
			html = this.getHtml(divId + "--" + templateId, dimaNameTitle, dicNameArray.toArray(), null, maps, riskId);
		}
		 
		map.put("divId", divId + "--" + templateId);
		map.put("html", html);
		
		return map;
	}
	
	/**
	 * 得到模板对应维度及分数
	 * @param templateId 模板ID
	 * @param assessPlanId 评估计划ID
	 * @param templateType 评估模板类型
	 * @return HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>>
	 * @author 金鹏祥
	 * */
	public HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> findTemplateRelaDimByTemplateIdHtml(String params, 
			String assessPlanId, String templateType) {
		HashMap<String, ArrayList<String>> templateDicDescAllMap = o_scoreInstanceBO.findTemplateDicDescAllMap();
		List<TemplateRelaDimension> templateRelaDimensionAllList = o_templateRelaDimensionBO.findTemplateRelaDimensionAllList();
		HashMap<String, ScoreResult> scoreResultAllMap = o_scoreResultBO.findScoreResultAllMap(assessPlanId);
		List<TemplateRelaDimension> templateRelaDimensionList = null; //匹配当前模板下的所有维度
		List<TemplateRelaDimension> templateRelaDimensionList2 = null; //匹配当前维度的下级维度
		String companyId = UserContext.getUser().getCompanyid(); //公司ID
		Map<String, Risk> riskMapAll = o_riskOutsideBO.getAllRiskInfo(companyId);
		HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> arrayListCount = 
				new HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>>(); //组织信息键值
		
		String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId(); //系统模板ID
		JSONArray jsonarr = JSONArray.fromObject(params); //拆解风险参数为数组
		ApplyContainer applyContainer = null; //组织数据实体
		String riskId = ""; //风险ID
		String jsobTemplateId = ""; //模板ID
		String rangObjectDeptEmpId = ""; //综合打分ID
		
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			ArrayList<ArrayList<HashMap<String, Object>>> arrayList = new ArrayList<ArrayList<HashMap<String, Object>>>(); //组织信息键值
			riskId = jsobjs.getString("riskId");
			jsobTemplateId = jsobjs.getString("templateId");
			rangObjectDeptEmpId = jsobjs.getString("rangObjectDeptEmpId");
			
			if(!"dim_template_type_custom".equalsIgnoreCase(templateType)){
				//其他模板类型
				templateRelaDimensionList = 
						this.getTemplateRelaDimensionListByTemplateId(jsobTemplateId, templateRelaDimensionAllList);
			}else{
				//自定义模板类型
				Template template = riskMapAll.get(riskId).getTemplate(); //风险自身模板ID
				if(null != template){
					//自身模板ID不为空
					templateRelaDimensionList = this.getTemplateRelaDimensionListByTemplateId(template.getId(), templateRelaDimensionAllList);
				}else{
					//系统模板ID
					templateRelaDimensionList = this.getTemplateRelaDimensionListByTemplateId(tempSys, templateRelaDimensionAllList);
				}
			}
			
			for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionList) {
				String templateId = ""; //模板ID
				if(!"dim_template_type_custom".equalsIgnoreCase(templateType)){
					//其他模板
					templateId = jsobTemplateId;
				}else{
					//自定义模板
					Template template = riskMapAll.get(riskId).getTemplate(); //风险自身模板ID
					if(null != template){
						templateId = template.getId(); //得到风险自身模板ID
					}else{
						templateId = tempSys; //得到系统模板ID
					}
				}
				if(templateRelaDimension.getParent() == null){
					templateRelaDimensionList2 = this.getTemplateRelaDimensionListByParentId(templateRelaDimension.getId(), 
							templateRelaDimensionAllList);
					ArrayList<HashMap<String, Object>> arrays = new ArrayList<HashMap<String, Object>>();
					HashMap<String, String> maps = new HashMap<String, String>(); //组织信息键值
					if(templateRelaDimensionList2.size() != 0){
						//维度存在下级
					
						for (TemplateRelaDimension template : templateRelaDimensionList2) {
							applyContainer = new ApplyContainer();
							applyContainer.setScoreDimId(template.getDimension().getId()); //维度ID
							applyContainer.setTemplateDicDescAllMap(templateDicDescAllMap.get(templateId)); //模板维度关联实体
							applyContainer.setDimName(templateRelaDimension.getDimension().getName()); //维度名称
							applyContainer.setWeight(templateRelaDimension.getWeight()); //维度权重
							applyContainer.setParentDimId(templateRelaDimension.getDimension().getId()); //上级维度ID
							applyContainer.setScoreResultEnAllMap(scoreResultAllMap); //打分结果
							applyContainer.setMaps(maps);
							
							HashMap<String, Object> mapObj = 
									this.getScoreListHtml(rangObjectDeptEmpId, riskId, templateId, applyContainer);
							arrays.add(mapObj);
						}
						arrayList.add(arrays);
					}else{
						applyContainer = new ApplyContainer();
						applyContainer.setScoreDimId(templateRelaDimension.getDimension().getId());
						applyContainer.setTemplateDicDescAllMap(templateDicDescAllMap.get(templateId));
						applyContainer.setDimName(templateRelaDimension.getDimension().getName());
						applyContainer.setScoreResultEnAllMap(scoreResultAllMap);
						applyContainer.setMaps(maps);
						//维度不存在下级
						HashMap<String, Object> mapObj = 
								this.getScoreListHtml(rangObjectDeptEmpId, riskId, templateId, applyContainer);
						arrays.add(mapObj);
						
						arrayList.add(arrays);
					}
				}
			}
			arrayListCount.put(riskId, arrayList);
		}
		
		return arrayListCount;
	}
}