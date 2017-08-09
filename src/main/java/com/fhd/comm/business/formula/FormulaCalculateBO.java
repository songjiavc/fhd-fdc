package com.fhd.comm.business.formula;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.interfaces.IFormulaCalculateBO;
import com.fhd.entity.comm.Category;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.RelaAssessResultDTO;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.risk.RiskStatusSevice;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;

@Service
public class FormulaCalculateBO implements IFormulaCalculateBO {

    @Autowired
    private FunctionCalculateBO o_functionCalculateBO;
    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;
    @Autowired
    private CategoryBO o_categroyBO;
    @Autowired
    private StrategyMapBO o_strategyMapBO;
    @Autowired
    private KpiBO o_kpiBO;
    @Autowired
    private RiskStatusSevice o_riskStatusSevice;
    
    private Logger logger = Logger.getLogger(FormulaCalculateBO.class);

    /**
     * 计算公式.
     * @param targetName 对象名称
     * @param type 类型:kpi/risk
     * @param formula 公式内容
     * @param timePeriodId 时间区间维id
     * @return String
     */
    public String calculate(String targetId,String targetName, String type, String formula, String timePeriodId,String companyId) {
        if(StringUtils.isBlank(formula)){
            return "公式为空";
        }
        if (StringUtils.isNotBlank(formula)) {
            formula = formula.replaceAll("\\$", "");
        }
        DecimalFormat df = new DecimalFormat("0.00");
        if (Contents.OBJECT_KPI.equals(type)) {
            /*
             * 指标公式的解析计算
             */
        	if(formula.indexOf("prior") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("prior");
            	while(temp!=-1){
            		formulaBefore = tempFormula.substring(0, temp);
            		//确定函数的右括号位置
            		String otherFormula = tempFormula.substring(temp,tempFormula.length());
            		formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
            		midTemp = calculatePriorFun(targetName, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
            		tempFormula = formulaBefore + midTemp + formulaAfter;
            		temp = tempFormula.indexOf("prior");
            	}
            	formula = tempFormula;
        	}
        	if(formula.indexOf("kpiStatusLevel") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("kpiStatusLevel");
        		while(temp!=-1){
        			formulaBefore = tempFormula.substring(0, temp);
        			//确定函数的右括号位置
        			String otherFormula = tempFormula.substring(temp,tempFormula.length());
        			formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
        			midTemp = calculateKpiStatusLevelFun(targetName, formulaMid, timePeriodId);
        			if(!isDoubleOrInteger(midTemp)){
        				return midTemp;
        			}
        			formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
        			tempFormula = formulaBefore + midTemp + formulaAfter;
        			temp = tempFormula.indexOf("kpiStatusLevel");
        		}
        		formula = tempFormula;
        	}
        	if(formula.indexOf("isKpiStatus") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("isKpiStatus");
            	while(temp!=-1){
            		formulaBefore = tempFormula.substring(0, temp);
            		//确定函数的右括号位置
            		String otherFormula = tempFormula.substring(temp,tempFormula.length());
            		formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
            		midTemp = calculateIsKpiStatusFun(targetName, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
            		tempFormula = formulaBefore + midTemp + formulaAfter;
            		temp = tempFormula.indexOf("isKpiStatus");
            	}
            	formula = tempFormula;
        	}
        	if(formula.indexOf("subSumRiskStatus") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("subSumRiskStatus");
            	while(temp!=-1){
            		formulaBefore = tempFormula.substring(0, temp);
            		//确定函数的右括号位置
            		String otherFormula = tempFormula.substring(temp,tempFormula.length());
            		formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
            		midTemp = calculateSubSumRiskStatusFun(targetName, type, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
            		tempFormula = formulaBefore + midTemp + formulaAfter;
            		temp = tempFormula.indexOf("subSumRiskStatus");
            	}
            	formula = tempFormula;
        	}
        	/*
        	 * formula去除函数后的指标分解计算
        	 */
        	List<String> nameList = new ArrayList<String>();
            List<Map<String, String>> decompositionFormulaResult = decompositionFormulaString(targetName, formula);
            for (Map<String, String> formulaMap : decompositionFormulaResult) {
                String name = formulaMap.get("name");
                if (!nameList.contains(name)) {
                    nameList.add(name);
                }
            }
            if (null != nameList && nameList.size() > 0) {
                /*
                 * 根据指标名称批量查询对应的指标采集结果list
                 * @param nameList
                 */
                List<KpiGatherResult> kpiGatherResultList = o_kpiGatherResultBO.findKpiGatherResultListByKpiNames(nameList, timePeriodId, companyId);
                if (null != kpiGatherResultList && kpiGatherResultList.size() > 0) {
                    for (KpiGatherResult kpiGatherResult : kpiGatherResultList) {
                        for (Map<String, String> formulaMap : decompositionFormulaResult) {
                            String name = formulaMap.get("name");
                            String vType = formulaMap.get("type");
                            String originalName = formulaMap.get("originalName");
                            if (kpiGatherResult.getKpi().getName().equals(name)) {
                                Double value = 0.0;
                                if (Contents.TARGET_VALUE.equals(vType)) {
                                    //目标值
                                    if (null != kpiGatherResult.getTargetValue()) {
                                        value = kpiGatherResult.getTargetValue();
                                    }else {
                                        return "指标[" + name + "]的目标值不存在!";
                                    }
                                }else if (Contents.RESULT_VALUE.equals(vType)) {
                                    //实际值
                                    if (null != kpiGatherResult.getFinishValue()) {
                                        value = kpiGatherResult.getFinishValue();
                                    }else {
                                        return "指标[" + name + "]的结果值不存在!";
                                    }
                                }else if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                    //评估值
                                    if (null != kpiGatherResult.getAssessmentValue()) {
                                        value = kpiGatherResult.getAssessmentValue();
                                    }else {
                                        return "指标[" + name + "]的评估值不存在!";
                                    }
                                }else if (Contents.MODEL_VALUE.equals(vType)) {
                                    //标杆值
                                    if (null != kpiGatherResult.getKpi().getModelValue()) {
                                        value = kpiGatherResult.getKpi().getModelValue();
                                    }else {
                                        return "指标[" + name + "]的标杆值不存在!";
                                    }
                                }else if (Contents.SAME_VALUE.equals(vType)) {
                                    //同比值
                                    if (null != kpiGatherResult.getSameValue()) {
                                        value = kpiGatherResult.getSameValue();
                                    }else {
                                        return "指标[" + name + "]的同比值不存在!";
                                    }
                                }else if (Contents.RATIO_VALUE.equals(vType)) {
                                    //环比值
                                    if (null != kpiGatherResult.getRatioValue()) {
                                        value = kpiGatherResult.getRatioValue();
                                    }else {
                                        return "指标[" + name + "]的环比值不存在!";
                                    }
                                }

                                if (value >= 0) {
                                    //如果采集结果是正数
                                    formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                }else if (value < 0) {
                                    //如果采集结果是负数,要把数字括起来
                                    formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                }
                            }
                        }
                    }
                }
            }
        }else if(Contents.OBJECT_CATEGORY.equals(type)){
        	/*
             * 记分卡公式的解析计算
             */
        	if(formula.indexOf("subSumKpiStatus") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("subSumKpiStatus");
            	while(temp!=-1){
            		formulaBefore = tempFormula.substring(0, temp);
            		//确定函数的右括号位置
            		String otherFormula = tempFormula.substring(temp,tempFormula.length());
            		formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
            		midTemp = calculateSubSumKpiStatusFun(targetName, type, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
            		tempFormula = formulaBefore + midTemp + formulaAfter;
            		temp = tempFormula.indexOf("subSumKpiStatus");
            	}
            	formula = tempFormula;
        	}
        	if(formula.indexOf("subSumScStatus") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("subSumScStatus");
            	while(temp!=-1){
            		formulaBefore = tempFormula.substring(0, temp);
            		//确定函数的右括号位置
            		String otherFormula = tempFormula.substring(temp,tempFormula.length());
            		formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
            		midTemp = calculateSubSumScStatusFun(targetName, type, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
            		tempFormula = formulaBefore + midTemp + formulaAfter;
            		temp = tempFormula.indexOf("subSumScStatus");
            	}
            	formula = tempFormula;
        	}
        	if(formula.indexOf("isKpiStatus") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("isKpiStatus");
            	while(temp!=-1){
            		formulaBefore = tempFormula.substring(0, temp);
            		//确定函数的右括号位置
            		String otherFormula = tempFormula.substring(temp,tempFormula.length());
            		formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
            		midTemp = calculateIsKpiStatusFun(targetName, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
            		tempFormula = formulaBefore + midTemp + formulaAfter;
            		temp = tempFormula.indexOf("isKpiStatus");
            	}
            	formula = tempFormula;
        	}
        	if(formula.indexOf("isScStatus") != -1){
        		String tempFormula = formula;
        		String formulaBefore = "";
        		String formulaMid = "";
        		String midTemp = "";
        		String formulaAfter ="";
        		int temp=tempFormula.indexOf("isScStatus");
            	while(temp!=-1){
            		formulaBefore = tempFormula.substring(0, temp);
            		//确定函数的右括号位置
            		String otherFormula = tempFormula.substring(temp,tempFormula.length());
            		formulaMid = tempFormula.substring(temp, temp+otherFormula.indexOf(')')+1);
            		midTemp = calculateIsScStatusFun(targetName, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = tempFormula.substring(temp+otherFormula.indexOf(')')+1, tempFormula.length());
            		tempFormula = formulaBefore + midTemp + formulaAfter;
            		temp = tempFormula.indexOf("isScStatus");
            	}
            	formula = tempFormula;
        	}
        	if (formula.indexOf("SUB") != -1) {
                return calculateSubFun(targetId, targetName, type, formula, timePeriodId);
            }
        	if(formula.indexOf("SumWeightAverage")!=-1){
        		return calculateSumWeightAverage(targetId,targetName,type,formula,timePeriodId);
        	}
        	if(formula.indexOf("@[")!=-1){//记分卡公式支持通过其它记分卡配置
        		List<String> nameList = new ArrayList<String>();
                List<Map<String, String>> decompositionFormulaResult = decompositionSCFormulaString(targetName, formula);
                for (Map<String, String> formulaMap : decompositionFormulaResult) {
                    String name = formulaMap.get("name");
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                }
                
                if (null != nameList && nameList.size() > 0) {
                	List<String> codeList = findCodeListByNameList(nameList);
                	List<RelaAssessResultDTO> assessResultList = null;
                	if(codeList.size()>0){
                		assessResultList = o_relaAssessResultBO.findAssessResultListByCodes(codeList, "sc", timePeriodId, companyId);
                	}else{
                		assessResultList = o_relaAssessResultBO.findAssessResultListByNames(nameList, "sc", timePeriodId, companyId);
                	}
                	
                	if(null!=assessResultList&&assessResultList.size()>0){
                		for (RelaAssessResultDTO relaAssessResult : assessResultList) {
                			 for (Map<String, String> formulaMap : decompositionFormulaResult) {
                				 String name = formulaMap.get("name");
                                 String vType = formulaMap.get("type");
                                 String originalName = formulaMap.get("originalName");
                                 if(codeList.size()>0){
                                	 if(name.contains(relaAssessResult.getObjectCode())){
                                    	 Double value = 0.0;
                                    	 if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                             //评估值
                                             if (null != relaAssessResult.getAssessmentValue()) {
                                                 value = relaAssessResult.getAssessmentValue();
                                             }else {
                                                 return "记分卡[" + name + "]的评估值不存在!";
                                             }
                                         }
                                    	 if (value >= 0) {
                                             //如果采集结果是正数
                                             formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                         }else if (value < 0) {
                                             //如果采集结果是负数,要把数字括起来
                                             formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                         }
                                     }
                                 }else{
                                	 if(name.equals(relaAssessResult.getObjectName())){
                                    	 Double value = 0.0;
                                    	 if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                             //评估值
                                             if (null != relaAssessResult.getAssessmentValue()) {
                                                 value = relaAssessResult.getAssessmentValue();
                                             }else {
                                                 return "记分卡[" + name + "]的评估值不存在!";
                                             }
                                         }
                                    	 if (value >= 0) {
                                             //如果采集结果是正数
                                             formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                         }else if (value < 0) {
                                             //如果采集结果是负数,要把数字括起来
                                             formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                         }
                                     }
                                 }
                                 
                			 }
						}
                	}
                }
        	}
        	else{
            	List<String> nameList = new ArrayList<String>();
                List<Map<String, String>> decompositionFormulaResult = decompositionFormulaString(targetName, formula);
                for (Map<String, String> formulaMap : decompositionFormulaResult) {
                    String name = formulaMap.get("name");
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                }
                if (null != nameList && nameList.size() > 0) {
                    /*
                     * 根据指标名称批量查询对应的指标采集结果list
                     * @param nameList
                     */
                    List<KpiGatherResult> kpiGatherResultList = o_kpiGatherResultBO.findKpiGatherResultListByKpiNames(nameList, timePeriodId, companyId);
                    if (null != kpiGatherResultList && kpiGatherResultList.size() > 0) {
                        for (KpiGatherResult kpiGatherResult : kpiGatherResultList) {
                            for (Map<String, String> formulaMap : decompositionFormulaResult) {
                                String name = formulaMap.get("name");
                                String vType = formulaMap.get("type");
                                String originalName = formulaMap.get("originalName");
                                if (kpiGatherResult.getKpi().getName().equals(name)) {
                                    Double value = 0.0;
                                    if (Contents.TARGET_VALUE.equals(vType)) {
                                        //目标值
                                        if (null != kpiGatherResult.getTargetValue()) {
                                            value = kpiGatherResult.getTargetValue();
                                        }else {
                                            return "指标[" + name + "]的目标值不存在!";
                                        }
                                    }else if (Contents.RESULT_VALUE.equals(vType)) {
                                        //实际值
                                        if (null != kpiGatherResult.getFinishValue()) {
                                            value = kpiGatherResult.getFinishValue();
                                        }else {
                                            return "指标[" + name + "]的结果值不存在!";
                                        }
                                    }else if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                        //评估值
                                        if (null != kpiGatherResult.getAssessmentValue()) {
                                            value = kpiGatherResult.getAssessmentValue();
                                        }else {
                                            return "指标[" + name + "]的评估值不存在!";
                                        }
                                    }else if (Contents.MODEL_VALUE.equals(vType)) {
                                        //标杆值
                                        if (null != kpiGatherResult.getKpi().getModelValue()) {
                                            value = kpiGatherResult.getKpi().getModelValue();
                                        }else {
                                            return "指标[" + name + "]的标杆值不存在!";
                                        }
                                    }else if (Contents.SAME_VALUE.equals(vType)) {
                                        //同比值
                                        if (null != kpiGatherResult.getSameValue()) {
                                            value = kpiGatherResult.getSameValue();
                                        }else {
                                            return "指标[" + name + "]的同比值不存在!";
                                        }
                                    }else if (Contents.RATIO_VALUE.equals(vType)) {
                                        //环比值
                                        if (null != kpiGatherResult.getRatioValue()) {
                                            value = kpiGatherResult.getRatioValue();
                                        }else {
                                            return "指标[" + name + "]的环比值不存在!";
                                        }
                                    }

                                    if (value >= 0) {
                                        //如果采集结果是正数
                                        formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                    }else if (value < 0) {
                                        //如果采集结果是负数,要把数字括起来
                                        formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else if(Contents.OBJECT_STRATEGY.equals(type)){
        	/*
             * 战略目标公式的解析计算
             */
            if (formula.indexOf("SUB") != -1) {
                return calculateSubFun(targetId, targetName, type, formula, timePeriodId);
            }
            if(formula.indexOf("SumWeightAverage")!=-1){
        		return calculateSumWeightAverage(targetId,targetName,type,formula,timePeriodId);
        	}
            if(formula.indexOf("@[")!=-1){//记分卡公式支持通过其它记分卡配置
        		List<String> nameList = new ArrayList<String>();
                List<Map<String, String>> decompositionFormulaResult = decompositionSCFormulaString(targetName, formula);
                for (Map<String, String> formulaMap : decompositionFormulaResult) {
                    String name = formulaMap.get("name");
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                }
                if (null != nameList && nameList.size() > 0) {
                	List<String> codeList = findCodeListByNameList(nameList);
                	List<RelaAssessResultDTO> assessResultList = null;
                	if(codeList.size()>0){
                		assessResultList = o_relaAssessResultBO.findAssessResultListByCodes(codeList, "str", timePeriodId, companyId);
                	}else{
                		assessResultList = o_relaAssessResultBO.findAssessResultListByNames(nameList, "str", timePeriodId, companyId);
                	}
                	if(null!=assessResultList&&assessResultList.size()>0){
                		for (RelaAssessResultDTO relaAssessResult : assessResultList) {
                			 for (Map<String, String> formulaMap : decompositionFormulaResult) {
                				 String name = formulaMap.get("name");
                                 String vType = formulaMap.get("type");
                                 String originalName = formulaMap.get("originalName");
                                 if(codeList.size()>0){
                                	 if(name.contains(relaAssessResult.getObjectCode())){
                                    	 Double value = 0.0;
                                    	 if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                             //评估值
                                             if (null != relaAssessResult.getAssessmentValue()) {
                                                 value = relaAssessResult.getAssessmentValue();
                                             }else {
                                                 return "战略目标[" + name + "]的评估值不存在!";
                                             }
                                         }
                                    	 if (value >= 0) {
                                             //如果采集结果是正数
                                             formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                         }else if (value < 0) {
                                             //如果采集结果是负数,要把数字括起来
                                             formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                         }
                                     }
                                 }else{
                                	 if(relaAssessResult.getObjectName().equals(name)){
                                    	 Double value = 0.0;
                                    	 if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                             //评估值
                                             if (null != relaAssessResult.getAssessmentValue()) {
                                                 value = relaAssessResult.getAssessmentValue();
                                             }else {
                                                 return "目标[" + name + "]的评估值不存在!";
                                             }
                                         }
                                    	 if (value >= 0) {
                                             //如果采集结果是正数
                                             formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                         }else if (value < 0) {
                                             //如果采集结果是负数,要把数字括起来
                                             formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                         }
                                     }
                                 }
                                 
                			 }
						}
                	}
                }
        	}
        	else{
            	List<String> nameList = new ArrayList<String>();
                List<Map<String, String>> decompositionFormulaResult = decompositionFormulaString(targetName, formula);
                for (Map<String, String> formulaMap : decompositionFormulaResult) {
                    String name = formulaMap.get("name");
                    if (!nameList.contains(name)) {
                        nameList.add(name);
                    }
                }
                if (null != nameList && nameList.size() > 0) {
                    /*
                     * 根据指标名称批量查询对应的指标采集结果list
                     * @param nameList
                     */
                    List<KpiGatherResult> kpiGatherResultList = o_kpiGatherResultBO.findKpiGatherResultListByKpiNames(nameList, timePeriodId, companyId);
                    if (null != kpiGatherResultList && kpiGatherResultList.size() > 0) {
                        for (KpiGatherResult kpiGatherResult : kpiGatherResultList) {
                            for (Map<String, String> formulaMap : decompositionFormulaResult) {
                                String name = formulaMap.get("name");
                                String vType = formulaMap.get("type");
                                String originalName = formulaMap.get("originalName");
                                if (kpiGatherResult.getKpi().getName().equals(name)) {
                                    Double value = 0.0;
                                    if (Contents.TARGET_VALUE.equals(vType)) {
                                        //目标值
                                        if (null != kpiGatherResult.getTargetValue()) {
                                            value = kpiGatherResult.getTargetValue();
                                        }else {
                                            return "指标[" + name + "]的目标值不存在!";
                                        }
                                    }else if (Contents.RESULT_VALUE.equals(vType)) {
                                        //实际值
                                        if (null != kpiGatherResult.getFinishValue()) {
                                            value = kpiGatherResult.getFinishValue();
                                        }else {
                                            return "指标[" + name + "]的结果值不存在!";
                                        }
                                    }else if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                        //评估值
                                        if (null != kpiGatherResult.getAssessmentValue()) {
                                            value = kpiGatherResult.getAssessmentValue();
                                        }else {
                                            return "指标[" + name + "]的评估值不存在!";
                                        }
                                    }else if (Contents.MODEL_VALUE.equals(vType)) {
                                        //标杆值
                                        if (null != kpiGatherResult.getKpi().getModelValue()) {
                                            value = kpiGatherResult.getKpi().getModelValue();
                                        }else {
                                            return "指标[" + name + "]的标杆值不存在!";
                                        }
                                    }else if (Contents.SAME_VALUE.equals(vType)) {
                                        //同比值
                                        if (null != kpiGatherResult.getSameValue()) {
                                            value = kpiGatherResult.getSameValue();
                                        }else {
                                            return "指标[" + name + "]的同比值不存在!";
                                        }
                                    }else if (Contents.RATIO_VALUE.equals(vType)) {
                                        //环比值
                                        if (null != kpiGatherResult.getRatioValue()) {
                                            value = kpiGatherResult.getRatioValue();
                                        }else {
                                            return "指标[" + name + "]的环比值不存在!";
                                        }
                                    }

                                    if (value >= 0) {
                                        //如果采集结果是正数
                                        formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                    }else if (value < 0) {
                                        //如果采集结果是负数,要把数字括起来
                                        formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        	
        }
        else if (Contents.OBJECT_RISK.equals(type)) {
        	List<String> nameList = new ArrayList<String>();
            List<Map<String, String>> decompositionFormulaResult = decompositionFormulaString(targetName, formula);
            for (Map<String, String> formulaMap : decompositionFormulaResult) {
                String name = formulaMap.get("name");
                if (!nameList.contains(name)) {
                    nameList.add(name);
                }
            }
            if (null != nameList && nameList.size() > 0) {
                /*
                 * 根据指标名称批量查询对应的指标采集结果list
                 * @param nameList
                 */
                List<KpiGatherResult> kpiGatherResultList = o_kpiGatherResultBO.findKpiGatherResultListByKpiNames(nameList, timePeriodId, companyId);
                if (null != kpiGatherResultList && kpiGatherResultList.size() > 0) {
                    for (KpiGatherResult kpiGatherResult : kpiGatherResultList) {
                        for (Map<String, String> formulaMap : decompositionFormulaResult) {
                            String name = formulaMap.get("name");
                            String vType = formulaMap.get("type");
                            String originalName = formulaMap.get("originalName");
                            if (kpiGatherResult.getKpi().getName().equals(name)) {
                                Double value = 0.0;
                                if (Contents.TARGET_VALUE.equals(vType)) {
                                    //目标值
                                    if (null != kpiGatherResult.getTargetValue()) {
                                        value = kpiGatherResult.getTargetValue();
                                    }else {
                                        return "指标[" + name + "]的目标值不存在!";
                                    }
                                }else if (Contents.RESULT_VALUE.equals(vType)) {
                                    //实际值
                                    if (null != kpiGatherResult.getFinishValue()) {
                                        value = kpiGatherResult.getFinishValue();
                                    }else {
                                        return "指标[" + name + "]的结果值不存在!";
                                    }
                                }else if (Contents.ASSEMENT_VALUE.equals(vType)) {
                                    //评估值
                                    if (null != kpiGatherResult.getAssessmentValue()) {
                                        value = kpiGatherResult.getAssessmentValue();
                                    }else {
                                        return "指标[" + name + "]的评估值不存在!";
                                    }
                                }else if (Contents.MODEL_VALUE.equals(vType)) {
                                    //标杆值
                                    if (null != kpiGatherResult.getKpi().getModelValue()) {
                                        value = kpiGatherResult.getKpi().getModelValue();
                                    }else {
                                        return "指标[" + name + "]的标杆值不存在!";
                                    }
                                }else if (Contents.SAME_VALUE.equals(vType)) {
                                    //同比值
                                    if (null != kpiGatherResult.getSameValue()) {
                                        value = kpiGatherResult.getSameValue();
                                    }else {
                                        return "指标[" + name + "]的同比值不存在!";
                                    }
                                }else if (Contents.RATIO_VALUE.equals(vType)) {
                                    //环比值
                                    if (null != kpiGatherResult.getRatioValue()) {
                                        value = kpiGatherResult.getRatioValue();
                                    }else {
                                        return "指标[" + name + "]的环比值不存在!";
                                    }
                                }

                                if (value >= 0) {
                                    //如果采集结果是正数
                                    formula = formula.replace(originalName, df.format(value).replace(",", ""));
                                }else if (value < 0) {
                                    //如果采集结果是负数,要把数字括起来
                                    formula = formula.replace(originalName, "(" + df.format(value).replace(",", "") + ")");
                                }
                            }
                        }
                    }
                }
            }

        }
        logger.info("替换后的公式: "+formula);
        return o_functionCalculateBO.calculate(o_functionCalculateBO.strCast(formula));
    }

    /**
     * subSumKpiStatus函数解析.
     * @param targetName 对象名称
     * @param type 计算对象类型: category-记分卡/strategy-战略目标
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String subSumKpiStatus函数计算结果
     */
    public String calculateSubSumKpiStatusFun(String targetName, String type, String subFormula, String timePeriod){
    	if(Contents.OBJECT_CATEGORY.equals(type)){
            String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
            String[] contentArray = funContent.split(",");
            /*
             * contentArray[0]存的是记分卡/战略目标名称，暂时未用，以后可扩展
             * 目前支持场景：记分卡A使用记分卡A下的度量指标进行计算
             * 可扩展场景：记分卡A使用记分卡B下的度量指标进行计算
             */
            int statusConut = 0;
            if (null != contentArray && contentArray.length == 2) {
                List<KpiGatherResult> kpiGatherResultList = o_kpiGatherResultBO.findKpiGatherResultListBySome(targetName, timePeriod);
                if (null != kpiGatherResultList && kpiGatherResultList.size() > 0) {
                    for (KpiGatherResult kpiGatherResult : kpiGatherResultList) {
                        if (null != kpiGatherResult.getAssessmentStatus()) {
                            if("green".equals(contentArray[1]) && "0alarm_startus_safe".equals(kpiGatherResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }else if("red".equals(contentArray[1]) && "0alarm_startus_h".equals(kpiGatherResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }else if("orange".equals(contentArray[1]) && "0alarm_startus_m".equals(kpiGatherResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }else if("yellow".equals(contentArray[1]) && "0alarm_startus_l".equals(kpiGatherResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }
                        }else{
                        	if(null!=kpiGatherResult.getTimePeriod()){
            					return "指标["+kpiGatherResult.getKpi().getName()+"] 时间["+kpiGatherResult.getTimePeriod().getId()+"] 状态灯不存在";
            				}
                        }
                    }
                }
            }
            return String.valueOf(statusConut);
    	}else if(Contents.OBJECT_STRATEGY.equals(type)){
    		//ENDO战略目标暂不使用
    	}
    	return "";
    }
    /**
     * subSumRiskStatus函数解析.
     * @param targetName 对象名称
     * @param type 计算对象类型: kpi-指标
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String subSumRiskStatus函数计算结果
     */
    public String calculateSubSumRiskStatusFun(String targetName, String type, String subFormula, String timePeriod){
    	if(Contents.OBJECT_KPI.equals(type)){
            String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
            String[] contentArray = funContent.split(",");
            /*
             * contentArray[0]存的是指标名称，暂时未用，以后可扩展
             * 目前支持场景：指标A使用指标A的相关风险进行计算
             * 可扩展场景：指标A使用指标B的相关风险进行计算
             */
            int statusConut = 0;
            if (null != contentArray && contentArray.length == 2) {
                List<RiskAdjustHistory> riskAdjustHistoryList = o_riskStatusSevice.findRiskStatusBySome(targetName, timePeriod);
                if (null != riskAdjustHistoryList && riskAdjustHistoryList.size() > 0) {
                    for (RiskAdjustHistory riskAdjustHistory : riskAdjustHistoryList) {
                        if (StringUtils.isNotBlank(riskAdjustHistory.getAssessementStatus())) {
                            if("green".equals(contentArray[1]) && Contents.RISK_LEVEL_LOW.equals(riskAdjustHistory.getAssessementStatus())){
                            	statusConut++;
                            }else if("red".equals(contentArray[1]) && Contents.RISK_LEVEL_HIGH.equals(riskAdjustHistory.getAssessementStatus())){
                            	statusConut++;
                            }else if("yellow".equals(contentArray[1]) && Contents.RISK_LEVEL_MIDDLE.equals(riskAdjustHistory.getAssessementStatus())){
                            	statusConut++;
                            }
                        }else{
                        	if(null!=riskAdjustHistory.getTimePeriod()){
            					return "风险["+riskAdjustHistory.getRisk().getName()+"] 时间["+riskAdjustHistory.getTimePeriod().getId()+"] 状态灯不存在";
            				}
                        }
                    }
                }
            }
            return String.valueOf(statusConut);
    	}
    	return "";
    }
    /**
     * subSumScStatus函数解析.
     * @param targetName 对象名称
     * @param type 计算对象类型: category-记分卡/strategy-战略目标
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String subSumScStatus函数计算结果
     */
    public String calculateSubSumScStatusFun(String targetName, String type, String subFormula, String timePeriod){
    	int statusConut = 0;
    	if(Contents.OBJECT_CATEGORY.equals(type)){
            String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
            String[] contentArray = funContent.split(",");
            
            if (null != contentArray && contentArray.length == 2) {
                List<RelaAssessResult> relaAssessResultList = o_relaAssessResultBO.findCategoryAssessResultListBySome(targetName, timePeriod);
                if (null != relaAssessResultList && relaAssessResultList.size() > 0) {
                    for (RelaAssessResult relaAssessResult : relaAssessResultList) {
                        if (null != relaAssessResult.getAssessmentStatus()) {
                            if("green".equals(contentArray[1]) && "0alarm_startus_safe".equals(relaAssessResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }else if("red".equals(contentArray[1]) && "0alarm_startus_h".equals(relaAssessResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }else if("orange".equals(contentArray[1]) && "0alarm_startus_m".equals(relaAssessResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }else if("yellow".equals(contentArray[1]) && "0alarm_startus_l".equals(relaAssessResult.getAssessmentStatus().getId())){
                            	statusConut++;
                            }
                        }else{
            				return "["+relaAssessResult.getObjectName()+"] 时间["+relaAssessResult.getTimePeriod().getId()+"] 状态灯不存在";
                        }
                    }
                }
            }
    	}else if(Contents.OBJECT_STRATEGY.equals(type)){
    		//ENDO战略目标暂不使用
    	}
    	return String.valueOf(statusConut);
    }
    
    /**
     * isKpiStatus函数解析.
     * @param targetName 对象名称
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String isKpiStatus函数计算结果
     */
    public String calculateIsKpiStatusFun(String targetName, String subFormula, String timePeriod){
    	int statusConut = 0;
    	
    	String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
        String[] contentArray = funContent.split(",");
        
        if (null != contentArray && contentArray.length == 2) {
            KpiGatherResult kpiGatherResult = null;
        	String kpiName = contentArray[0].substring(contentArray[0].indexOf("#[")+2,contentArray[0].indexOf(']'));
        	String frequence = this.o_kpiBO.findKpiFrequenceByName(kpiName);
        	if("0frequecy_relatime".equals(frequence)){//实时指标查询最晚的采集结果
        	    kpiGatherResult = o_kpiGatherResultBO.findRelaTimeKpiGatherResultBySome(kpiName);
        	}else{
        	    kpiGatherResult = o_kpiGatherResultBO.findKpiGatherResultBySome(kpiName,timePeriod);
        	}
        	if (null != kpiGatherResult) {
                if (null != kpiGatherResult.getAssessmentStatus()) {
                    if("green".equals(contentArray[1]) && "0alarm_startus_safe".equals(kpiGatherResult.getAssessmentStatus().getId())){
                        statusConut++;
                    }else if("red".equals(contentArray[1]) && "0alarm_startus_h".equals(kpiGatherResult.getAssessmentStatus().getId())){
                        statusConut++;
                    }else if("orange".equals(contentArray[1]) && "0alarm_startus_m".equals(kpiGatherResult.getAssessmentStatus().getId())){
                        statusConut++;
                    }else if("yellow".equals(contentArray[1]) && "0alarm_startus_l".equals(kpiGatherResult.getAssessmentStatus().getId())){
                        statusConut++;
                    }
                }else{
                	if(null!=kpiGatherResult.getTimePeriod()){
    					return "指标["+kpiName+"] 时间["+kpiGatherResult.getTimePeriod().getId()+"] 状态灯不存在";
    				}
                }
            }
        }
        return String.valueOf(statusConut);
    }
    
    /**
     * isScStatus函数解析.
     * @param targetName 对象名称
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String isScStatus函数计算结果
     */
    public String calculateIsScStatusFun(String targetName, String subFormula, String timePeriod){
    	int statusConut = 0;
    	
    	String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
        String[] contentArray = funContent.split(",");
        
        if (null != contentArray && contentArray.length == 2) {
        	
        	String categoryName = contentArray[0].substring(contentArray[0].indexOf("@[")+2,contentArray[0].indexOf(']'));
        	
        	RelaAssessResult relaAssessResult = o_relaAssessResultBO.findRelaAssessResultBySome(categoryName,timePeriod);
            if (null != relaAssessResult) {
                if (null != relaAssessResult.getAssessmentStatus()) {
                    if("green".equals(contentArray[1]) && "0alarm_startus_safe".equals(relaAssessResult.getAssessmentStatus().getId())){
                    	statusConut++;
                    }else if("red".equals(contentArray[1]) && "0alarm_startus_h".equals(relaAssessResult.getAssessmentStatus().getId())){
                    	statusConut++;
                    }else if("orange".equals(contentArray[1]) && "0alarm_startus_m".equals(relaAssessResult.getAssessmentStatus().getId())){
                    	statusConut++;
                    }else if("yellow".equals(contentArray[1]) && "0alarm_startus_l".equals(relaAssessResult.getAssessmentStatus().getId())){
                    	statusConut++;
                    }
                }else{
                	return "["+relaAssessResult.getObjectName()+"] 时间["+relaAssessResult.getTimePeriod().getId()+"] 状态灯不存在";
                }
            }
        }
        return String.valueOf(statusConut);
    }
    
    /**
     * prior函数解析.
     * @param targetName 对象名称
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String prior函数计算结果
     */
    public String calculatePriorFun(String targetName, String subFormula, String timePeriod){
    	Double value = null;
    	
    	String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
        String[] contentArray = funContent.split(",");
        
        if (null != contentArray && contentArray.length == 2) {
        	
        	String[] paramArray = apartTarget(contentArray[0], contentArray[0].indexOf("#[")).split("~");
        	
        	String kpiName = paramArray[0];
        	String vType = paramArray[1];
        	String periods = contentArray[1];
        	
            KpiGatherResult kpiGatherResult = o_kpiGatherResultBO.findPreKpiGatherResultBySome(kpiName, Integer.valueOf(periods), timePeriod);
            if (null != kpiGatherResult) {
                	if (Contents.TARGET_VALUE.equals(vType)) {
                        //目标值
                        if (null != kpiGatherResult.getTargetValue()) {
                            value = kpiGatherResult.getTargetValue();
                        }else {
                            return "指标[" + kpiName + "]的目标值不存在!";
                        }
                    }else if (Contents.RESULT_VALUE.equals(vType)) {
                        //实际值
                        if (null != kpiGatherResult.getFinishValue()) {
                            value = kpiGatherResult.getFinishValue();
                        }else {
                            return "指标[" + kpiName + "]的结果值不存在!";
                        }
                    }else if (Contents.ASSEMENT_VALUE.equals(vType)) {
                        //评估值
                        if (null != kpiGatherResult.getAssessmentValue()) {
                            value = kpiGatherResult.getAssessmentValue();
                        }else {
                            return "指标[" + kpiName + "]的评估值不存在!";
                        }
                    }else if (Contents.MODEL_VALUE.equals(vType)) {
                        //标杆值
                        if (null != kpiGatherResult.getKpi().getModelValue()) {
                            value = kpiGatherResult.getKpi().getModelValue();
                        }else {
                            return "指标[" + kpiName + "]的标杆值不存在!";
                        }
                    }else if (Contents.SAME_VALUE.equals(vType)) {
                        //同比值
                        if (null != kpiGatherResult.getSameValue()) {
                            value = kpiGatherResult.getSameValue();
                        }else {
                            return "指标[" + kpiName + "]的同比值不存在!";
                        }
                    }else if (Contents.RATIO_VALUE.equals(vType)) {
                        //环比值
                        if (null != kpiGatherResult.getRatioValue()) {
                            value = kpiGatherResult.getRatioValue();
                        }else {
                            return "指标[" + kpiName + "]的环比值不存在!";
                        }
                    }
            }
        }
        if(null!=value){
            return String.valueOf(value);
        }
        else{
            return null;
        }
    }
    
    /**
     * kpiStatusLevel函数解析.
     * @param targetName 对象名称
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String kpiStatusLevel函数计算结果
     */
    public String calculateKpiStatusLevelFun(String targetName, String subFormula, String timePeriod){
    	Double value = null;
    	
    	String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
    	String[] contentArray = funContent.split(",");
    	
    	if (null != contentArray && contentArray.length == 2) {
    		
    		String kpiName = apartTarget(contentArray[0], contentArray[0].indexOf("#["));
    		String periods = contentArray[1];
    		KpiGatherResult kpiGatherResult = o_kpiGatherResultBO.findPreKpiGatherResultBySome(kpiName, Integer.valueOf(periods), timePeriod);
    		if (null != kpiGatherResult) {
    			if(null != kpiGatherResult.getAssessmentStatus()){
    				if("0alarm_startus_safe".equals(kpiGatherResult.getAssessmentStatus().getId())){
                    	value = 1.0;
                    }else if("0alarm_startus_h".equals(kpiGatherResult.getAssessmentStatus().getId())){
                    	value = 4.0;
                    }else if("0alarm_startus_m".equals(kpiGatherResult.getAssessmentStatus().getId())){
                    	value = 3.0;
                    }else if("0alarm_startus_l".equals(kpiGatherResult.getAssessmentStatus().getId())){
                    	value = 2.0;
                    }
    			}else{
    				if(null!=kpiGatherResult.getTimePeriod()){
    					return "指标["+kpiName+"] 时间["+kpiGatherResult.getTimePeriod().getId()+"] 状态灯不存在";
    				}
    			}
    		}
    	}
    	if(null!=value){
    	    return String.valueOf(value);
    	}
    	else{
    	    return null;
    	}
    }
    
    /**
     * 分解公式字符串.
     * @param targetName 对象名称
     * @param formula
     * @return List<Map<String, String>> map格式：{name:'',type:'',originalName:''}
     */
    public List<Map<String, String>> decompositionFormulaString(String targetName, String formula) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;

        int temp = formula.indexOf("#[");
        while (temp != -1) {
            map = new HashMap<String, String>();

            //关联指标与值类型的字符串
            String targetNameAndValueTypeTemp = apartTarget(formula, temp);
            map.put("originalName", "#[" + targetNameAndValueTypeTemp + "]");
            //指标名称与类型分解数组
            String[] targetNameAndValueType = targetNameAndValueTypeTemp.split("~");
            //指标的名称；
            String tempTargetName = targetNameAndValueType[0];
            if ("myself".equals(tempTargetName)) {
                tempTargetName = targetName;
            }
            map.put("name", tempTargetName);
            //指标的值类型;
            String dataTypeName = targetNameAndValueType[1];
            map.put("type", dataTypeName);

            formula = formula.substring(formula.indexOf(']') + 1, formula.length());
            temp = formula.indexOf("#[");
            result.add(map);
        }
        return result;
    }
    /**
     * 记分卡分解公式字符串.
     * @param targetName 对象名称
     * @param formula
     * @return List<Map<String, String>> map格式：{name:'',type:'',originalName:''}
     */
    public List<Map<String, String>> decompositionSCFormulaString(String targetName, String formula) {
    	List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    	Map<String, String> map = null;
    	
    	int temp = formula.indexOf("@[");
    	while (temp != -1) {
    		map = new HashMap<String, String>();
    		
    		//关联指标与值类型的字符串
    		String targetNameAndValueTypeTemp = apartTarget(formula, temp);
    		map.put("originalName", "@[" + targetNameAndValueTypeTemp + "]");
    		//指标名称与类型分解数组
    		String[] targetNameAndValueType = targetNameAndValueTypeTemp.split("~");
    		//指标的名称；
    		String tempTargetName = targetNameAndValueType[0];
    		if ("myself".equals(tempTargetName)) {
    			tempTargetName = targetName;
    		}
    		map.put("name", tempTargetName);
    		//指标的值类型;
    		String dataTypeName = targetNameAndValueType[1];
    		map.put("type", dataTypeName);
    		
    		formula = formula.substring(formula.indexOf(']') + 1, formula.length());
    		temp = formula.indexOf("@[");
    		result.add(map);
    	}
    	return result;
    }

    /**
     * 从公式字符串中分离出指标名称和指标类型
     * 分离的结果为长度为2的字符串数组，第1个元素为指标名称，第2个为值类型
     * @author 陈燕杰
     * @param sourceStr：公式字符串；
     * @param beginIndex：#[在公式字符串中的index;
     * @return String
     * @since  fhd　Ver 1.1
     */
    public String apartTarget(String sourceStr, int beginIndex) {
        StringBuilder result = new StringBuilder();
        int len = sourceStr.length();
        for (int i = beginIndex + 2; i < len; i++) {
            String temp = sourceStr.substring(i, i + 1);
            if (!temp.equals("]")) {
                result.append(temp);
            }
            else {
                break;
            }
        }
        return result.toString();
    }

    /**
     * java正则表达式判断字符串是否是double类型.
     * @param str
     * @return boolean
     */
    public boolean isDoubleOrInteger(String str) {
        return NumberUtils.isNumber(str);
    }

    /**
     * 删除formula字符串中的html格式.
     * @param formula
     * @param length
     * @return String
     */
    public static String splitAndFilterString(String formula, int length) {
        if (StringUtils.isBlank(formula)) {
            return "";
        }
        // 去掉所有html元素,
        String str = formula.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
        str = str.replaceAll("[(/>)<]", "");
        int len = str.length();
        if (len <= length) {
            return str;
        }else {
            str = str.substring(0, length);
            str += "......";
        }
        return str;
    }

    /**
     * 解析公式字符串存在多少变量值--暂时未用.
     * @param str
     * @return int
     */
    public static int queryParamsCountByFormulaString(String str) {
        int count = 0;
        //一共有str的长度的循环次数   
        for (int i = 0; i < str.length();) {
            int c = -1;
            c = str.indexOf('@');
            //如果有@这样的子字符串。则c的值不是-1.   
            if (c != -1) {
                //这里的c+1 而不是 c+ s.length();这是因为。如果str的字符串是"aaaa"， s = "aa"，则结果是2个。但是实际上是3个子字符串   
                //将剩下的字符冲洗取出放到str中   
                str = str.substring(c + 1);
                count++;
            }
            else {
                break;
            }
        }
        return count;
    }

    /**
     * 记分卡、战略目标计算公式.
     * 目前不支持混搭，有3种计算方式，即：由下级记分卡计算，由度量指标计算，由选择的指标计算.
     * @param targetId 对象id
     * @param type 类型:category/strategy
     * @param formula 公式内容
     * @timePeriodId 时间区间维id
     * 公式内容定义：sub(assessmentValue,kpi,A)
     * sub--函数名称
     * &记分卡、战略目标名称或者myself
     * &评估值--值类型
     * &kpi--值对象类型
     * &A--函数名称average的简称;S--函数名称sum的简称
     * @return String
     */
    @Transactional
    public String calculateCategory(String targetId, String type, String formula, String timePeriodId) {
        String ret = "";

        String targetName = "";

        if (StringUtils.isNotBlank(targetId)) {
            if (Contents.OBJECT_CATEGORY.equals(type)) {
                Category category = o_categroyBO.findCategoryById(targetId);
                if (null != category) {
                    targetName = category.getName();
                }
            }
            else if (Contents.OBJECT_STRATEGY.equals(type)) {
                StrategyMap strategyMap = o_strategyMapBO.findById(targetId);
                if (null != strategyMap) {
                    targetName = strategyMap.getName();
                }
            }
            /*
            //去除常量的特殊字符
            formula = formula.replaceAll("\\$", "");
            
            String formulaBefore = "";
            String formulaMid = "";
            String formulaAfter = "";
            String midTemp = "";
            if(formula.indexOf("SUB")!=-1){
            	int temp=formula.indexOf("SUB");
            	while(temp!=-1){
            		formulaBefore = formula.substring(0, temp);
            		formulaMid = formula.substring(temp, formula.indexOf(")")+1);
            		midTemp = calculateSubFun(targetId, targetName, type, formulaMid, timePeriodId);
            		if(!isDoubleOrInteger(midTemp)){
            			return midTemp;
            		}
            		formulaAfter = formula.substring(formula.indexOf(")")+1, formula.length());
            		formula=formulaBefore + midTemp + formulaAfter;
            		temp = formula.indexOf("SUB");
            	}
            }
            
            ret = o_functionCalculateBO.calculate(o_functionCalculateBO.strCast(formula));
            */
            if (StringUtils.isNotBlank(formula)) {
                formula = formula.replaceAll("\\$", "");
            }
            if (formula.indexOf("SUB") != -1) {
                ret = calculateSubFun(targetId, targetName, type, formula, timePeriodId);
            }
            else {
                ret = "公式定义不正确!";
            }
        }

        return ret;
    }

    /**计算记分卡或战略目标关联指标的加权平均值
     * @param targetId 记分卡或目标ID
     * @param targetName 记分卡或目标名称
     * @param type category:表示记分卡 ; strategy表示战略目标
     * @param subFormula 公式字符串
     * @param timePeriod 时间区间纬度id
     * @return
     */
    public String calculateSumWeightAverage(String targetId, String targetName, String type, String subFormula, String timePeriod){
    	String ret = "";
    	Double sum = 0.0d;
    	Double sumWeight = 0.0d;
    	List<Object[]> objectList = null;
    	String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
        String[] contentArray = funContent.split(",");
    	if(Contents.OBJECT_CATEGORY.equals(type)){//记分卡
    		 objectList =  o_kpiGatherResultBO.findKpiGatherResultsListByCategoryId(targetId, contentArray[1], timePeriod);
    		
    	}else if(Contents.OBJECT_STRATEGY.equals(type)){
    		 objectList = o_kpiGatherResultBO.findKpiGatherResultsListByStrategyId(targetId, contentArray[1], timePeriod);
    	}
    	if(null!=objectList&&objectList.size()>0){
			 for (Object[] object : objectList) {
				 if(object[1]!=null&&object[10]!=null){
					 Double value = Double.valueOf(String.valueOf(object[1]));
					 Double weight = Double.valueOf(String.valueOf(object[10]));
					 sumWeight+=weight;
					 sum+=value*weight;
				 }else{
					 StringBuffer info = new StringBuffer();
					 info.append("指标[").append(object[0]).append("]").append("没有设置权重!");
					 logger.info(info.toString());
				 }
			 }
		}
    	if(sumWeight!=0){
    		Double result = sum/sumWeight;
    		ret = result.toString();
    	}
    	return ret;
    }
    /**
     * sub函数解析.
     * @param targetId 对象id
     * @param targetName 对象名称
     * @param type 计算对象类型
     * @param subFormula sub函数内容
     * @param timePeriod 时间区间维id
     * @return String sub函数计算结果
     */
    public String calculateSubFun(String targetId, String targetName, String type, String subFormula, String timePeriod) {
        DecimalFormat df = new DecimalFormat("0.00");
        String funContent = subFormula.substring(subFormula.indexOf('(') + 1, subFormula.indexOf(')'));
        String[] contentArray = funContent.split(",");
        StringBuilder sb = new StringBuilder();
        if (null != contentArray && contentArray.length > 0) {
            if ("myself".equals(String.valueOf(contentArray[0]))) {
                contentArray[0] = targetName;
            }
            List<Object[]> objectList = null;
            if (Contents.OBJECT_CATEGORY.equals(type)) {
                if (Contents.OBJECT_CATEGORY.equals(contentArray[2])) {
                    //通过下级记分卡计算
                    objectList = o_relaAssessResultBO.findRelaAssessResultBySubCategory(targetId, contentArray[1], timePeriod);
                }
                else if (Contents.OBJECT_KPI.equals(contentArray[2])) {
                    //通过度量指标计算
                    objectList = o_kpiGatherResultBO.findKpiGatherResultsListByCategoryId(targetId, contentArray[1], timePeriod);
                }
            }
            else if (Contents.OBJECT_STRATEGY.equals(type)) {
                if (Contents.OBJECT_STRATEGY.equals(contentArray[2])) {
                    //通过下级战略目标计算
                    objectList = o_relaAssessResultBO.findRelaAssessResultBySubStrategy(targetId, contentArray[1], timePeriod);
                }
                else if (Contents.OBJECT_KPI.equals(contentArray[2])) {
                    //通过度量指标计算
                    objectList = o_kpiGatherResultBO.findKpiGatherResultsListByStrategyId(targetId, contentArray[1], timePeriod);
                }
            }
            if (null != objectList && objectList.size() > 0) {
                if ("A".equals(contentArray[3])) {
                    sb.append("average").append("(");
                }
                else if ("S".equals(contentArray[3])) {
                    sb.append("sum").append("(");
                }
                Boolean flag = false;
                for (Object[] object : objectList) {
                    Double v = 0.0;
                    if (null != object[1]) {
                    	flag = true;
                        v = Double.valueOf(String.valueOf(object[1]));
                        if (v >= 0) {
                            //如果评估值是正数
                            sb.append(df.format(v));
                        }
                        else if (v < 0) {
                            //如果评估值是负数,要把数字括起来
                            sb.append("(" + df.format(v) + ")");
                        }

                       
                        sb.append(",");
                        
                    }
                    else {
                        if (Contents.OBJECT_CATEGORY.equals(contentArray[2])) {
                            logger.info("记分卡[" + object[0] + "]的'" + contentArray[1] + "'不存在!") ;
                        }
                        else if (Contents.OBJECT_STRATEGY.equals(contentArray[2])) {
                        	logger.info("战略目标[" + object[0] + "]的'" + contentArray[1] + "'不存在!");
                        }
                        else if (Contents.OBJECT_KPI.equals(contentArray[2])) {
                        	logger.info( "指标[" + object[0] + "]的'" + contentArray[1] + "'不存在!");
                        }
                    }
                }
                
                if(flag){
                	String tmpSb = sb.substring(0, sb.length()-1);
                	sb = new StringBuilder(tmpSb);
                }else{
                	return "";//如果没有结果数据直接返回空字符串
                }
                sb.append(")");
                return o_functionCalculateBO.calculate(o_functionCalculateBO.strCast(sb.toString()));
            }
            else {
                String str = "";
                if (Contents.OBJECT_CATEGORY.equals(type)) {
                    if (Contents.OBJECT_CATEGORY.equals(contentArray[2])) {
                        //通过下级记分卡计算
                        str = "记分卡[" + targetName + "]" + "需要计算的下级记分卡值不存在!";
                    }
                    else if (Contents.OBJECT_KPI.equals(contentArray[2])) {
                        //通过度量指标计算
                        str = "记分卡[" + targetName + "]" + "需要计算的度量指标值不存在!";
                    }
                }
                else if (Contents.OBJECT_STRATEGY.equals(type)) {
                    if (Contents.OBJECT_STRATEGY.equals(contentArray[2])) {
                        //通过下级战略目标计算
                        str = "战略目标[" + targetName + "]" + "需要计算的下级战略目标值不存在!";
                    }
                    else if (Contents.OBJECT_KPI.equals(contentArray[2])) {
                        //通过度量指标计算
                        str = "战略目标[" + targetName + "]" + "需要计算的度量指标值不存在!";
                    }
                }
                return str;
            }
        }
        return subFormula;
    }
    
    /**根据名称找到编码的集合
     * @param nameList名称和编码的集合
     * @return
     */
    private List<String> findCodeListByNameList(List<String> nameList){
    	List<String> codeList = new ArrayList<String>();
    	for(int j=0;j<nameList.size();j++){
    		String name = nameList.get(j);
    		if(name.contains(" ")){
    			String[] nameArray = StringUtils.split(name," ");
    			int length = nameArray.length;
    			codeList.add(nameArray[length-1]);
    		}
    	}
    	return codeList;
    }
}
