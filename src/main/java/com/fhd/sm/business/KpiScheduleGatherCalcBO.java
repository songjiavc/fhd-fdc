package com.fhd.sm.business;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.impl.calendar.AnnualCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.formula.FormulaCalculateBO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;

@Service
public class KpiScheduleGatherCalcBO {
	
	@Autowired
	private KpiGatherResultBO o_kpiGatherResultBO;
	
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
    private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
    private FormulaCalculateBO o_formulaCalculateBO;
	
	@Autowired
    private ChartForEmailBO o_chartForEmailBO;
	
	
	private static Log logger = LogFactory.getLog(KpiScheduleGatherCalcBO.class);
	
	
	
  
	/**根据公式计算采集结果数据的评估值
	 * @param resultIds 采集结果id
	 * @param valueType 值类型 target:目标值采集     finish:实际值采集 
	 * @throws ParseException
	 */
	@Transactional
	public void mergeGatherResultSummarizing(String resultIds,String valueType) throws ParseException{
		String resultId[] = resultIds.split(",");
		List<String> resultIdList = Arrays.asList(resultId);
		Map<String,KpiGatherResult> resultMap = new HashMap<String, KpiGatherResult>();
    	if(resultIdList.size()>0){
    		List<KpiGatherResult> resultList = o_kpiGatherResultBO.findKpiGatherResultByIds(resultIdList);
    		for (KpiGatherResult kpiGatherResult : resultList) {
    			resultMap.put(kpiGatherResult.getId(), kpiGatherResult);
			}
    	}
		for(int i=0;i<resultId.length;i++){
			
			Double assessMentValue = null;
            String assessMentValueStr = "";
            
			KpiGatherResult kgr = resultMap.get(resultId[i]);
			if(null!=kgr){
					Kpi kpi = kgr.getKpi();
					//采集指标的目标值的情况
					if("target".equalsIgnoreCase(valueType)) {
		    			kgr.setTargetValue(kgr.getTempTargetValue());
		    			//目标值状态为提交
		    			kgr.setTargetSetStatus("1");
		    			//更新指标结果收集下次执行时间
					} else if("finish".equalsIgnoreCase(valueType)) {
		    			kgr.setFinishValue(kgr.getTempFinishValue());
		    			//完成值状态为提交
		    			kgr.setFinishSetStatus("1");
					}
					
					TimePeriod lastTimePeriod = kpi.getLastTimePeriod();
					String timePeriodId = "";
					if(null==lastTimePeriod){
						
		    			TimePeriod timePeriod = null;
		    			Date currentEndDate = kgr.getTimePeriod().getEndTime();
		    			if(null!=kgr.getTimePeriod()){
		    				timePeriod = kgr.getTimePeriod();
		    				timePeriodId = timePeriod.getId();
		    			}
		    			
		                if (null != timePeriod) { 
		                    kpi.setLastTimePeriod(timePeriod);
		                }
		                if("target".equalsIgnoreCase(valueType)) {
		                	 kpi.setTargetCalculatetime(findCalculateNextExecuteTime(kpi, "targetValue",currentEndDate));
		                }else if("finish".equalsIgnoreCase(valueType)){
		                	 kpi.setCalculatetime(findCalculateNextExecuteTime(kpi, "finishValue",currentEndDate));
		                }
					}else{
						Date lastDate = kpi.getLastTimePeriod().getStartTime();
		    			Date currentDate = kgr.getTimePeriod().getStartTime();
		    			Date currentEndDate = kgr.getTimePeriod().getEndTime();
		    			if(currentDate.compareTo(lastDate)>0){
		        			TimePeriod timePeriod = null;
		        			if(null!=kgr.getTimePeriod()){
		        				timePeriod = kgr.getTimePeriod();
		        				timePeriodId = timePeriod.getId();
		        			}
		        			
		                    if (null != timePeriod) { 
		                        kpi.setLastTimePeriod(timePeriod);
		                    }
		                    
		                   if("target".equalsIgnoreCase(valueType)) {
		                   	 kpi.setTargetCalculatetime(findCalculateNextExecuteTime(kpi, "targetValue",currentEndDate));
		                   }else if("finish".equalsIgnoreCase(valueType)){
		                   	 kpi.setCalculatetime(findCalculateNextExecuteTime(kpi, "finishValue",currentEndDate));
		                   }
		    			}
					}
		            
		            //首先计算目标值
		            if ("finish".equalsIgnoreCase(valueType)&&"0sys_use_formular_formula".equals(kpi.getIsTargetFormula())) {
	
		                if (StringUtils.isNotBlank(kpi.getTargetFormula())&&!"0".equals(kpi.getTargetFormula())) {
		                    //创建公式日志实体
		                    String ret = o_formulaCalculateBO.calculate(kpi.getId(),kpi.getName(), "kpi", kpi.getTargetFormula(), timePeriodId, kpi
		                            .getCompany().getId());
		                    if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
		                        //返回结果是double，计算正确
		                    	kgr.setTargetValue(NumberUtils.toDouble(ret));
		                    }
		                }
		                
		            }
		            
		            //计算评估值
		            if (StringUtils.isNotBlank(kpi.getAssessmentFormula())&&!"0".equals(kpi.getAssessmentFormula())&&
		    				"0sys_use_formular_formula".equals(kpi.getIsAssessmentFormula())) {
		    			assessMentValueStr = o_formulaCalculateBO.calculate(kpi.getId(),kpi.getName(), "kpi", kpi.getAssessmentFormula(), kgr
		                        .getTimePeriod().getId(),kpi.getCompany().getId());
		        		
		    		}
		            
		    		if(o_formulaCalculateBO.isDoubleOrInteger(assessMentValueStr)){
		    			assessMentValue = NumberUtils.toDouble(assessMentValueStr);
		    			kgr.setAssessmentValue(assessMentValue);
		    		}
		            
		    		
		            //获得亮灯依据默认值
		            String alarmMeasure = "kpi_alarm_measure_score";
		            if(null!=kpi.getAlarmMeasure()){
		            	alarmMeasure = kpi.getAlarmMeasure().getId();
		            }
		            
		          //计算告警状态 
		            AlarmPlan alarmPlan = o_kpiBO.findAlarmPlanByKpiId(kpi.getId(), Contents.ALARMPLAN_REPORT);
		            if (null == alarmPlan) {//指标没有关联告警方案,取常用告警方案
		                alarmPlan = o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast", kpi.getCompany().getId());
		            }
		            if (null != alarmPlan) {//告警方案不为空
		            	DictEntry assessmentStatus = null;
		            	if("kpi_alarm_measure_score".equals(alarmMeasure)){
		            		if(null!=assessMentValue){
		            			assessmentStatus= o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessMentValue);
		            		}
		            	}else {
		              		if(null!=kgr.getFinishValue()){
		              			assessmentStatus= o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, kgr.getFinishValue());
		              		}
		            	}
		            	kgr.setAssessmentStatus(assessmentStatus);
		            }
		            
		            //计算趋势
		            DictEntry trend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(kgr);
		            kgr.setDirection(trend);
		            
		            o_kpiGatherResultBO.mergeKpiGatherResult(kgr);
		            
		            o_kpiBO.mergeKpi(kpi);
		            
		            /*
		             * 累积值公式计算--包括:季度累积值计算和年度累积值计算
		             */
		            //季度累积值计算--只有月指标使用
		            //根据指标id查询当期所在季度的指标采集结果
		            KpiGatherResult quarterKpiGatherResult = o_kpiGatherResultBO.findQuarterKpiGatherResultByKpiId(kpi.getId(),timePeriodId);
		            if (null != quarterKpiGatherResult) {
		                if ("0frequecy_month".equals(kpi.getGatherFrequence().getId())) {
		                    if (!"kpi_sum_measure_manual".equals(kpi.getTargetSumMeasure().getId())) {
		                        Double targetValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(kgr,
		                                "targetValueSum", kpi.getTargetSumMeasure().getId());
		                        quarterKpiGatherResult.setTargetValue(targetValueSum);
		                    }
		                    if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
		                        Double resultValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(kgr,
		                                "resultValueSum", kpi.getResultSumMeasure().getId());
		                        quarterKpiGatherResult.setFinishValue(resultValueSum);
		                    }
		                    if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
		                        Double assessmentValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(kgr,
		                                "assessmentValueSum", kpi.getAssessmentSumMeasure().getId());
		                        quarterKpiGatherResult.setAssessmentValue(assessmentValueSum);
		                        //计算预警状态
		                        if(assessmentValueSum!=null) {
		                        	 DictEntry assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessmentValueSum);
		                             quarterKpiGatherResult.setAssessmentStatus(assessmentStatus);
		                        }
		                        //计算趋势
		                        DictEntry quarterTrend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(quarterKpiGatherResult);
		                        quarterKpiGatherResult.setDirection(quarterTrend);
		                    }
		                    //保存指标采集结果季度累积值
		                    o_kpiGatherResultBO.saveKpiGatherResult(quarterKpiGatherResult);
		                    
		                }
		                else {
		                	logger.info("只有月指标需要进行季度累积值计算");
		                }
		            }
	
		            //年度累积值计算--只有年度指标不使用
		            //根据指标id查询当期所在年度的指标采集结果
		            KpiGatherResult yearKpiGatherResult = o_kpiGatherResultBO.findYearKpiGatherResultByKpiId(kpi.getId(), timePeriodId);
		            if (null != yearKpiGatherResult) {
		                if ("0frequecy_year".equals(kpi.getGatherFrequence().getId())) {
		                	logger.info("年度指标不需要进行年度累积值计算");
		                }
		                else {
		                    if (!"kpi_sum_measure_manual".equals(kpi.getTargetSumMeasure().getId())) {
		                        Double targetValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(kgr,
		                                "targetValueSum", kpi.getTargetSumMeasure().getId());
		                        yearKpiGatherResult.setTargetValue(targetValueSum);
		                    }
		                    if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
		                        Double resultValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(kgr,
		                                "resultValueSum", kpi.getResultSumMeasure().getId());
		                        yearKpiGatherResult.setFinishValue(resultValueSum);
		                    }
		                    if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
		                        Double assessmentValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(kgr,
		                                "assessmentValueSum", kpi.getAssessmentSumMeasure().getId());
		                        yearKpiGatherResult.setAssessmentValue(assessmentValueSum);
		                        //计算预警状态  
		                        if (null != alarmPlan && null!=assessmentValueSum) {
		                            DictEntry assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessmentValueSum);
		                            yearKpiGatherResult.setAssessmentStatus(assessmentStatus);
		                        }
		                        //计算趋势
		                        DictEntry yearTrend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(yearKpiGatherResult);
		                        yearKpiGatherResult.setDirection(yearTrend);
		                    }
		                    //保存指标采集结果年度累积值
		                    o_kpiGatherResultBO.saveKpiGatherResult(yearKpiGatherResult);
		                }
		            }
		            if(o_chartForEmailBO.findIsValueChangeSend()){
		            	o_chartForEmailBO.sendFormulaCalculateEmail(kpi.getId(),kgr.getTimePeriod().getId());
		           }
				}
			}
			
	}
	
	
	/**
     * 根据指标采集频率cron表达式计算下次执行时间.
     * 
     * @param kpi
     * @param type值类型
     * @param endDate 采集数据endDate
     * @return Date
     * @throws ParseException
     */
    public Date findCalculateNextExecuteTime(Kpi kpi, String type,Date endDate) throws ParseException {
        CronTrigger trigger = new CronTrigger();
        trigger.setStartTime(endDate);
        if ("targetValue".equals(type)) {
            if (StringUtils.isNotBlank(kpi.getTargetSetDayFormular())) {
                trigger.setCronExpression(kpi.getTargetSetDayFormular());
            }
            else {
                return null;
            }
        }
        else if ("finishValue".equals(type)) {
            if (StringUtils.isNotBlank(kpi.getGatherDayFormulr())) {
                trigger.setCronExpression(kpi.getGatherDayFormulr());
            }
            else {
                return null;
            }
        }
        AnnualCalendar cal = new AnnualCalendar();
        trigger.computeFirstFireTime(cal);
        return trigger.getNextFireTime();
    }
	
    
}
