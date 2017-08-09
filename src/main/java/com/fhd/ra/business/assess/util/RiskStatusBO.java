package com.fhd.ra.business.assess.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.ra.business.assess.approval.ApplyContainer;
import com.fhd.sm.business.KpiBO;

@Service
public class RiskStatusBO {

	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	/**
	 * 得到风险评估灯
	 * @param riskId 风险ID
	 * @param templateId 模板ID
	 * @param rangObjectDeptEmpId 综合ID
	 * @param groupList 分组集合
	 * @param infoList 信息集合
	 * @param map 模板维度关联所有信息
	 * @param AllList 过滤集合
	 * @param alarmPlanAllMap 告警集合
	 * @param riskAllMap 风险集合
	 * @return String
	 * @author 金鹏祥
	 * */
	public String getRiskIcon(String riskId, 
			String templateId,
			String rangObjectDeptEmpId,
			ApplyContainer applyContainer){
		
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				applyContainer.getTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = applyContainer.getTemplateRelaDimensionMap();
		HashMap<String, String> scoreResultAllMap = applyContainer.getScoreResultAllMap();
		HashMap<String, AlarmPlan> alarmPlanAllMap = applyContainer.getAlarmPlanAllMap();
		String alarmScenario = applyContainer.getAlarmScenario();
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = applyContainer.getScoreResultByrangObjectDeptEmpIdAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				applyContainer.getTemplateRelaDimensionByIdAndTemplateIdAllMap();
		String riskLevelFormula = applyContainer.getRiskLevelFormula();
		ArrayList<String> dimIdAllList = applyContainer.getDimIdAllList();
		Map<String, Risk> riskAllMap = applyContainer.getRiskAllMap();
		String tempSys = applyContainer.getTempSys();
		ArrayList<String> scoreDicValuelist = new ArrayList<String>();
		double riskScoreValue = 1l;
		String riskIcon = "";
		ArrayList<ScoreResult> scoreResultByRangObjectDeptEmpIdAllList = scoreResultByrangObjectDeptEmpIdAllMap.get(rangObjectDeptEmpId);
		String temp = "";
		if(scoreResultByRangObjectDeptEmpIdAllList == null){
			return "";
		}
		
		for (ScoreResult scoreResult : scoreResultByRangObjectDeptEmpIdAllList) {
            if(null != scoreResult.getDimension()){
            	String scoreDimId = scoreResult.getDimension().getId();
	            String tempLateRelaDimensionId = "";
	            ArrayList<TemplateRelaDimension> templateRelaDimensionList = null;
	            
	            if(StringUtils.isBlank(templateId)){
	            	if(null == riskAllMap.get(riskId).getTemplate()){
	            		templateId = tempSys;
	            	}else{
	            		templateId = riskAllMap.get(riskId).getTemplate().getId();
	            	}
	            }
	            
	            if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId) != null){
	            	
	            	TemplateRelaDimension parent = templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId).getParent();
	            	//parent.getDimension().getName();
		            if(parent != null){
		            	//子维度
		            	templateRelaDimensionList = templateRelaDimensionIsParentIdIsNullInfoAllMap.get(parent.getId());
		            	tempLateRelaDimensionId = parent.getId();
		            }else{
		            	tempLateRelaDimensionId = scoreDimId;
		            }
		            
		            if(temp.split("--").length > 1){
			            if(temp.indexOf(tempLateRelaDimensionId) != -1){
		        			continue;
		        		}
		            }
		            
		            if(null != templateRelaDimensionList){
		            	String calculateMethod = "";
		        		double scoreDimWeight = 0l;
		            	if(templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod() != null){
		            		calculateMethod = templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod().getId();//计算方式
		            	}
		            	
		            	if(templateRelaDimensionMap.get(tempLateRelaDimensionId) != null){
		            		scoreDimWeight = templateRelaDimensionMap.get(tempLateRelaDimensionId).getWeight();//权重
		            	}
		            	
		        		double[] scoreDicValue = null;
						String[] tempScoreDicValue = null;
						String scoreDicValues = "";
						double scoreDimValue = 0l;
		            	
		            	//多维度
		            	for (TemplateRelaDimension templateRelaDimensions : templateRelaDimensionList) {
		            		scoreDicValues += scoreResultAllMap.get(rangObjectDeptEmpId + "--" + templateRelaDimensions.getDimension().getId()) + "--";
		    			}
		            	
		            	tempScoreDicValue = scoreDicValues.split("--");
						scoreDicValue = new double[tempScoreDicValue.length];
//						int count = 0;
						for (int i = 0; i < scoreDicValue.length; i++) {
							if(!tempScoreDicValue[i].toString().equalsIgnoreCase("null")){
								scoreDicValue[i] = Double.parseDouble(tempScoreDicValue[i]);
//								count++;
							}
						}
//						if(count == scoreDicValue.length){
							scoreDimValue = DynamicDim.getValue(calculateMethod, scoreDimWeight, scoreDicValue);//维度分值
							scoreDicValuelist.add(templateRelaDimensionMap.get(tempLateRelaDimensionId).getDimension().getId() + "--" + scoreDimValue);
//						}
		            }else{
		            	//单维度
		            	if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId) != null){
		            		if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId).getParent() == null){
		            			if(null != scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId)){
		    	            		scoreDicValuelist.add(scoreDimId + "--" + Double.parseDouble(scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId)));
		    	            	}
		            		}
		            	}
		            }
		            if(null != parent){
		            	temp += parent.getId() + "--";
		            }
	            }
            }
		}
		
		if(scoreDicValuelist.size() != 0){
			//得到风险水平
			String riskScoreValueStr = o_riskLevelBO.getRisklevel(scoreDicValuelist, riskLevelFormula, dimIdAllList);
			if(riskScoreValueStr.indexOf("--") == -1){
				riskScoreValue = Double.valueOf(riskScoreValueStr);
			}
			
			if(riskScoreValue != 0){
				//得到风险灯
				DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), riskScoreValue);
				if(dict != null){
					riskIcon = dict.getValue();
				}
			}
		}
		
		return riskIcon;
	}
	
	/**
	 * 得到风险评估灯
	 * @param riskId 风险ID
	 * @param templateId 模板ID
	 * @param rangObjectDeptEmpId 综合ID
	 * @param groupList 分组集合
	 * @param infoList 信息集合
	 * @param map 模板维度关联所有信息
	 * @param AllList 过滤集合
	 * @param alarmPlanAllMap 告警集合
	 * @param riskAllMap 风险集合
	 * @return String
	 * @author 金鹏祥
	 * */
	public String getRiskIcon(String riskId, 
			String templateId,
			String rangObjectDeptEmpId, 
			String assessPlanId,
			ApplyContainer applyContainer){
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				applyContainer.getTemplateRelaDimensionIsParentIdIsNullInfoAllMap();	
		HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = 
				applyContainer.getTemplateRelaDimensionMap();
		HashMap<String, String> scoreResultAllMap = applyContainer.getScoreResultAllMap();
		HashMap<String, AlarmPlan> alarmPlanAllMap = applyContainer.getAlarmPlanAllMap();
		String alarmScenario = applyContainer.getAlarmScenario();
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = applyContainer.getScoreResultByrangObjectDeptEmpIdAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				applyContainer.getTemplateRelaDimensionByIdAndTemplateIdAllMap();
		String riskLevelFormula = applyContainer.getRiskLevelFormula();
		ArrayList<String> dimIdAllList = applyContainer.getDimIdAllList();
		HashMap<String, Template> templateMapAll = applyContainer.getTemplateAllMap();
		Map<String, Risk> riskAllMap = applyContainer.getRiskAllMap();
		String tempSys = applyContainer.getTempSys();
		ArrayList<String> scoreDicValuelist = new ArrayList<String>();
		double riskScoreValue = 1l;
		String riskIcon = "";
		ArrayList<ScoreResult> scoreResultByRangObjectDeptEmpIdAllList = 
				scoreResultByrangObjectDeptEmpIdAllMap.get(rangObjectDeptEmpId + "--" + assessPlanId);
		String temp = "";
		if(scoreResultByRangObjectDeptEmpIdAllList == null){
			return "";
		}
		for (ScoreResult scoreResult : scoreResultByRangObjectDeptEmpIdAllList) {
            if(null != scoreResult.getDimension()){
            	String scoreDimId = scoreResult.getDimension().getId();
	            String tempLateRelaDimensionId = "";
	            ArrayList<TemplateRelaDimension> templateRelaDimensionList = null;
	            
	            if("dim_template_type_custom".equalsIgnoreCase(templateMapAll.get(templateId).getType().getId())){
	            	if(null == riskAllMap.get(riskId).getTemplate()){
	            		templateId = tempSys;
	            	}else{
	            		templateId = riskAllMap.get(riskId).getTemplate().getId();
	            	}
	            }
	            
	            TemplateRelaDimension parent = templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId).getParent();
	            if(parent != null){
	            	//子维度
	            	templateRelaDimensionList = templateRelaDimensionIsParentIdIsNullInfoAllMap.get(parent.getId());
	            	temp += parent.getId() + "--";
	            	tempLateRelaDimensionId = parent.getId();
	            }else{
	            	tempLateRelaDimensionId = scoreDimId;
	            }
	            
	            if(temp.split("--").length > 1){
		            if(temp.indexOf(tempLateRelaDimensionId) != -1){
	        			continue;
	        		}
	            }
	            
	            if(null != templateRelaDimensionList){
	            	String calculateMethod = "";
	        		double scoreDimWeight = 0l;
	            	if(templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod() != null){
	            		calculateMethod = templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod().getId();//计算方式
	            	}
	            	
	            	if(templateRelaDimensionMap.get(tempLateRelaDimensionId) != null){
	            		scoreDimWeight = templateRelaDimensionMap.get(tempLateRelaDimensionId).getWeight();//权重
	            	}
	            	
	        		double[] scoreDicValue = null;
					String[] tempScoreDicValue = null;
					String scoreDicValues = "";
					double scoreDimValue = 0l;
	            	
	            	//多维度
	            	for (TemplateRelaDimension templateRelaDimensions : templateRelaDimensionList) {
	            		scoreDicValues += scoreResultAllMap.get(rangObjectDeptEmpId + "--" + templateRelaDimensions.getDimension().getId()) + "--";
	    			}
	            	
	            	tempScoreDicValue = scoreDicValues.split("--");
					scoreDicValue = new double[tempScoreDicValue.length];
//					int count = 0;
					for (int i = 0; i < scoreDicValue.length; i++) {
						if(!tempScoreDicValue[i].toString().equalsIgnoreCase("null")){
							scoreDicValue[i] = Double.parseDouble(tempScoreDicValue[i]);
//							count++;
						}
					}
//					if(count == scoreDicValue.length){
						scoreDimValue = DynamicDim.getValue(calculateMethod, scoreDimWeight, scoreDicValue);//维度分值
						scoreDicValuelist.add(templateRelaDimensionMap.get(tempLateRelaDimensionId).getDimension().getId() + "--" + scoreDimValue);
//					}
	            }else{
	            	//单维度
	            	if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId) != null){
	            		if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId).getParent() == null){
	            			if(null != scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId)){
	    	            		scoreDicValuelist.add(scoreDimId + "--" + Double.parseDouble(scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId)));
	    	            	}
	            		}
	            	}
	            }
            }
		}
		
		//得到风险水平
		String riskScoreValueStr = o_riskLevelBO.getRisklevel(scoreDicValuelist, riskLevelFormula, dimIdAllList);
		if(riskScoreValueStr.indexOf("--") == -1){
			riskScoreValue = Double.parseDouble(riskScoreValueStr);
		}
		
		//得到风险灯
		DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), riskScoreValue);
		if(dict != null){
			riskIcon = dict.getValue();
		}
		return riskIcon;
	}
}
