package com.fhd.ra.business.assess.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fhd.comm.business.formula.StatisticFunctionCalculateBO;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.ra.business.assess.approval.ApplyContainer;

public class DynamicDim {

	/**
	 * 判断类型并计算取值
	 * @param calculateMethod 取值类型
	 * @param scoreDimWeight 维度权重
	 * @param scoreDicValue 纬度值
	 * @return double
	 * @author 金鹏祥
	 * */
	public static double getValue(String calculateMethod, double scoreDimWeight, double[] scoreDicValue){
		double value = 0l;
		
		if("dim_calculate_method_avg".equalsIgnoreCase(calculateMethod)){
			value = StatisticFunctionCalculateBO.ma(scoreDicValue);
		}else if("dim_calculate_method_max".equalsIgnoreCase(calculateMethod)){
			value = StatisticFunctionCalculateBO.max(scoreDicValue);
		}else if("dim_calculate_method_min".equalsIgnoreCase(calculateMethod)){
			value = StatisticFunctionCalculateBO.min(scoreDicValue);
		}else if("dim_calculate_method_weight_avg".equalsIgnoreCase(calculateMethod)){
			
		}
		
		return value;
	}
	
	/**
	 * 得到动态维度及分值
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
	public static ArrayList<String> getDimValue(
			String riskId, 
			String templateId,
			String rangObjectDeptEmpId, 
			String assessPlanId,
			ApplyContainer applyContainer){
		
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap =
				applyContainer.getTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = applyContainer.getTemplateRelaDimensionMap();
		HashMap<String, String> scoreResultAllMap = applyContainer.getScoreResultAllMap();
		Map<String, Risk> riskAllMap = applyContainer.getRiskAllMap();
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = applyContainer.getScoreResultByrangObjectDeptEmpIdAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				applyContainer.getTemplateRelaDimensionByIdAndTemplateIdAllMap();
		HashMap<String, Template> templateMapAll = applyContainer.getTemplateAllMap();
		String tempSys = applyContainer.getTempSys();
		
		ArrayList<String> lists = new ArrayList<String>();
		ArrayList<ScoreResult> scoreResultByRangObjectDeptEmpIdAllList = 
				scoreResultByrangObjectDeptEmpIdAllMap.get(rangObjectDeptEmpId + "--" + assessPlanId);
		String temp = "";
		
		if(scoreResultByRangObjectDeptEmpIdAllList == null){
			return null;
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
	            	if(templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod() != null){
	            		calculateMethod = templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod().getId();//计算方式
	            	}
	            	
	            	double scoreDimWeight = 0L;
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
						scoreDimValue = getValue(calculateMethod, scoreDimWeight, scoreDicValue);//维度分值
						lists.add(templateRelaDimensionMap.get(tempLateRelaDimensionId).getDimension().getId() + "--" + scoreDimValue);
//					}
	            }else{
	            	//单维度
	            	if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId) != null){
	            		if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId).getParent() == null){
	            			if(null != scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId)){
	    	            		lists.add(scoreDimId + "--" + scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId));
	    	            	}
	            		}
	            	}
	            }
            }
		}
		
		return lists;
	}
	
	/**
	 * 得到动态维度及分值
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
	public static ArrayList<String> getDimValue(
			String riskId, 
			String templateId,
			String rangObjectDeptEmpId, 
			ApplyContainer applyContainer){
		
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
				applyContainer.getTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = applyContainer.getTemplateRelaDimensionMap();
		HashMap<String, String> scoreResultAllMap = applyContainer.getScoreResultAllMap();
		Map<String, Risk> riskAllMap = applyContainer.getRiskAllMap();
		HashMap<String, ArrayList<ScoreResult>> scoreResultByrangObjectDeptEmpIdAllMap = applyContainer.getScoreResultByrangObjectDeptEmpIdAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
				applyContainer.getTemplateRelaDimensionByIdAndTemplateIdAllMap();
		String tempSys = applyContainer.getTempSys();
		
		ArrayList<String> lists = new ArrayList<String>();
		ArrayList<ScoreResult> scoreResultByRangObjectDeptEmpIdAllList = scoreResultByrangObjectDeptEmpIdAllMap.get(rangObjectDeptEmpId);
		String temp = "";
		
		if(scoreResultByRangObjectDeptEmpIdAllList == null){
			return null;
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
	            if(null != templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId)){
	            	TemplateRelaDimension parent = templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId).getParent();
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
		            	if(templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod() != null){
		            		calculateMethod = templateRelaDimensionMap.get(tempLateRelaDimensionId).getCalculateMethod().getId();//计算方式
		            	}
		            	
		            	double scoreDimWeight = 0L;
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
							scoreDimValue = getValue(calculateMethod, scoreDimWeight, scoreDicValue);//维度分值
							lists.add(templateRelaDimensionMap.get(tempLateRelaDimensionId).getDimension().getId() + "--" + scoreDimValue);
//						}
		            }else{
		            	//单维度
		            	if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId) != null){
		            		if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateId).getParent() == null){
		            			if(null != scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId)){
		    	            		lists.add(scoreDimId + "--" + scoreResultAllMap.get(rangObjectDeptEmpId + "--" + scoreDimId));
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
		
		return lists;
	}
}
