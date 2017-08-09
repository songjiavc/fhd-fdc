package com.fhd.comm.business.formula;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.impl.calendar.AnnualCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.ChartForEmailBO;
import com.fhd.sm.business.DataSourceCollectBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;

/**
 * 公式每天定时计算.
 * 触发时间表达式为：0 0 0 5 * ? --每月5号凌晨00:00:00触发
 * @author   吴德福
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-12-05		下午03:36:35
 */
@Service
public class FormulaReCalculateBO {

    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;

    @Autowired
    private FormulaCalculateBO o_formulaCalculateBO;

    @Autowired
    private FormulaLogBO o_formulaLogBO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;

    @Autowired
    private CategoryBO o_categoryBO;

    @Autowired
    private StrategyMapBO o_strategyMapBO;
    
    @Autowired
    private DataSourceCollectBO o_dataSourceCollectBO;
    
    @Autowired
    private ChartForEmailBO o_chartForEmailBO;
    
    @Autowired
    private TimePeriodBO o_timePeriodBO;
    
    private Logger logger = Logger.getLogger(FormulaReCalculateBO.class);

    /**记分卡和目标任意期数据计算
     * @param objectId 记分卡或目标ID
     * @param type 记分卡或目标类型
     * @param items 目标ID和时间区间纬度ID
     * @return
     */
    @Transactional
    public boolean categoryformulaCalculate(String objectId, String type, Map<String, String> items,String lastTimePeriodId) {
        AlarmPlan alarmPlan = null;
        Category category = null;
        StrategyMap strategyMap = null;
        String formula = "";
        String name = "";
        String companyid = "";
        String formulaType = "";
        SysEmployee emp = null;
        FormulaLog formulaLog = new FormulaLog();
        Double assessValue = null;
        TimePeriod lastTimePeriod = null;
        if(StringUtils.isNotBlank(lastTimePeriodId)){
        	lastTimePeriod = o_timePeriodBO.findTimePeriodById(lastTimePeriodId);
    	}
        if ("category".equals(type)) {//记分卡
            alarmPlan = o_categoryBO.findAlarmPlanByScId(objectId, Contents.ALARMPLAN_REPORT);
            category = o_categoryBO.findCategoryById(objectId);
            formula = category.getAssessmentFormula();
            name = category.getName();
            companyid = category.getCompany().getId();
            formulaType = Contents.SC_ASSESSMENT_VALUE_FORMULA;
            //更新最后时间纬
            if(StringUtils.isNotBlank(lastTimePeriodId)){
            	category.setTimePeriod(lastTimePeriod);
            	o_categoryBO.mergeCategory(category);
            }
        }
        else {//战略目标
            alarmPlan = o_strategyMapBO.findAlarmPlanBySmId(objectId, Contents.ALARMPLAN_REPORT);
            strategyMap = o_strategyMapBO.findById(objectId);
            formula = strategyMap.getAssessmentFormula();
            name = strategyMap.getName();
            companyid = strategyMap.getCompany().getId();
            formulaType = Contents.SM_ASSESSMENT_VALUE_FORMULA;
            //更新最后时间纬
            if(StringUtils.isNotBlank(lastTimePeriodId)){
            	strategyMap.setTimePeriod(lastTimePeriod);
            	o_strategyMapBO.mergeStrategyMap(strategyMap);
            }
            
        }
        if (null == alarmPlan) {
            alarmPlan = o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast", UserContext.getUser().getCompanyid());
        }
        for (Map.Entry<String, String> item : items.entrySet()) {
        	String ret = "";
            String gatherResultId = item.getKey();
            RelaAssessResult relaAssessResult = o_relaAssessResultBO.findRelaAssessResultById(gatherResultId);
            if(null!=relaAssessResult){
            	if(StringUtils.isBlank(formula)&&null!=relaAssessResult.getAssessmentValue()){
                    ret = String.valueOf(relaAssessResult.getAssessmentValue());
                }else{
                	ret = o_formulaCalculateBO.calculate(objectId, name, type, formula, relaAssessResult.getTimePeriod().getId(), companyid);
                }
                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                    assessValue = NumberUtils.toDouble(ret);
                    DictEntry status = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessValue);
                    if(null != status){
                    	relaAssessResult.setAssessmentStatus(status);
                    }
                    relaAssessResult.setAssessmentValue(assessValue);
                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, objectId, name,type, formulaType,
                			formula, "success", ret, relaAssessResult.getTimePeriod(), emp));
                }else{
                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, objectId, name,type, formulaType,
                			formula, "failure", ret, relaAssessResult.getTimePeriod(), emp));
                }
                logger.info("公式为:"+formula+"    计算值ret=" + ret);
            }
            
        }
        o_relaAssessResultBO.mergedRelaAssessResultsTrend(objectId, type);
        return true;
    }

    /**指标任意一期数据计算
     * @param items 指标ID和时间区间纬度ID
     * @return
     * @throws ParseException
     */
    @Transactional
    public boolean formulaCalculate(Map<String, String> items) throws ParseException {
    	try {
    		if (null != items) {
    			List<String> kpiIdList = new ArrayList<String>();
    			for (Map.Entry<String, String> item : items.entrySet()) {
                    String kpiId = item.getKey();
                    kpiIdList.add(kpiId);
                }
    			//指标对应的告警方案map
    	        Map<String,AlarmPlan> alarmPlanMap = new HashMap<String, AlarmPlan>();
    	        if(kpiIdList.size()>0){
    	        	alarmPlanMap = o_alarmPlanBO.findAlarmPlanByKpiId(kpiIdList, Contents.ALARMPLAN_REPORT);
    	        }
    	        //指标map
    	        Map<String,Kpi> kpiMap = new HashMap<String, Kpi>();
    	        List<Kpi> kpiList = o_kpiBO.findKpiListByIds(kpiIdList);
    	        for (Kpi kpi : kpiList) {
    	        	kpiMap.put(kpi.getId(), kpi);
				}
    			Date currentDate = new Date();
                for (Map.Entry<String, String> item : items.entrySet()) {
                	//更新指标timeperiod标识位
                    boolean computeFlag = true;
                    //更新指标下次执行时间标识位
                    boolean calculateTimeFlag = false;
                    String kpiId = item.getKey();
                    String timePeriodId = item.getValue();
                    Kpi kpi = kpiMap.get(kpiId);
                    AlarmPlan alarmPlan = alarmPlanMap.get(kpi.getId());
                    DictEntry frequanceDict = kpi.getGatherFrequence();
                    if(null==frequanceDict){
                    	//如果指标没有频率,忽略该指标
                    	continue;
                    }
                    String lastTimePeriodId = "";
                    String frequance = frequanceDict.getId();
                    TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome(frequance);
                    if(null!=timePeriod){
                    	lastTimePeriodId = timePeriod.getId();
                    }
                    if(lastTimePeriodId.equals(timePeriodId)||"".equals(timePeriodId)){
                		calculateTimeFlag = true;
                	}
                    //更新指标timeperiod标识位
                    FormulaLog formulaLog = null;

                    SysEmployee emp = null;
                    
                    //获得亮灯依据默认值
                    String alarmMeasure = "kpi_alarm_measure_score";
                    if(null!=kpi.getAlarmMeasure()){
                    	alarmMeasure = kpi.getAlarmMeasure().getId();
                    }
                    KpiGatherResult currentKpiGatherResult = o_kpiGatherResultBO.findCurrentKpiGatherResultByKpiId(kpiId, timePeriodId);
                    if (null != currentKpiGatherResult) {
                    	Double finishValue = null;
                    	Double assessMentValue = null;
                        //目标值公式计算
                        if ("0sys_use_formular_formula".equals(kpi.getIsTargetFormula())) {

                            if (StringUtils.isNotBlank(kpi.getTargetFormula()) && !"0".equals(kpi.getTargetFormula())) {
                                //创建公式日志实体
                                formulaLog = new FormulaLog();

                                String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getTargetFormula(), timePeriodId, kpi
                                        .getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                	computeFlag = true;
                                    currentKpiGatherResult.setTargetValue(NumberUtils.toDouble(ret));
                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "targetValueFormula", kpi.getTargetFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                }
                                else {
                                	computeFlag = false;
                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "targetValueFormula", kpi.getTargetFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                }
                                
                                logger.info("目标值ret=" + ret);
                            }
                        } else if("0sys_use_formular_export".equals(kpi.getIsTargetFormula())) {
                        	if (StringUtils.isNotBlank(kpi.getTargetFormula()) && !"0".equals(kpi.getTargetFormula())) {
                        		//创建公式日志实体
                                formulaLog = new FormulaLog();
                                String dsName = "";
                                if(null != kpi.getTargetDs()) {
                                	dsName = kpi.getTargetDs().getId();
                                }
                                String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(),kpi.getName(),kpi.getIsTargetDsType(),dsName,kpi.getTargetFormula(),timePeriodId,"target");
                                if(o_dataSourceCollectBO.isDoubleOrInteger(ret)) {
                                	computeFlag = true;
                                    currentKpiGatherResult.setTargetValue(NumberUtils.toDouble(ret));
                                    //保存公式日志实体
                                    o_formulaLogBO
                                            .saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                                    "targetValueFormula", kpi.getTargetFormula(), "success", ret,
                                                    currentKpiGatherResult.getTimePeriod(), emp));
                                } else {
                                	 computeFlag = false;
                                	 o_formulaLogBO
                                     .saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                             "targetValueFormula", kpi.getTargetFormula(), "failure", ret,
                                             currentKpiGatherResult.getTimePeriod(), emp));
                                }
                                logger.info("目标值ret=" + ret);
                        	
                           }
                        }
                        //结果值公式计算
                        if ("0sys_use_formular_formula".equals(kpi.getIsResultFormula())) {

                            if (StringUtils.isNotBlank(kpi.getResultFormula()) && !"0".equals(kpi.getResultFormula())) {
                                //创建公式日志实体
                                formulaLog = new FormulaLog();

                                String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getResultFormula(), timePeriodId, kpi
                                        .getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                	computeFlag = true;
                                	finishValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setFinishValue(finishValue);
                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "resultValueFormula", kpi.getResultFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                }
                                else {
                                	computeFlag = false;
                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "resultValueFormula", kpi.getResultFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                }
                                logger.info("结果值ret=" + ret);
                            }
                        } else if ("0sys_use_formular_export".equals(kpi.getIsResultFormula())) {
                        	if (StringUtils.isNotBlank(kpi.getResultFormula()) && !"0".equals(kpi.getResultFormula())) {
                        		//创建公式日志实体
                                formulaLog = new FormulaLog();
                                String dsName = "";
                                if(null != kpi.getResultDs()) {
                                	dsName = kpi.getResultDs().getId();
                                }
                                String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(),kpi.getName(),kpi.getIsResultDsType(),dsName,kpi.getResultFormula(),timePeriodId,"result");
                                if(o_dataSourceCollectBO.isDoubleOrInteger(ret)) {
                                	computeFlag = true;
                                	finishValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setFinishValue(finishValue);
                                    //保存公式日志实体
                                    o_formulaLogBO
                                    .saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "resultValueFormula", kpi.getResultFormula(), "success", ret,
                                            currentKpiGatherResult.getTimePeriod(), emp));
                                } else {
                                	computeFlag = false;
                                	 //返回结果是字符串描述，计算错误
                                    o_formulaLogBO
                                    .saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "resultValueFormula", kpi.getResultFormula(), "failure", ret,
                                            currentKpiGatherResult.getTimePeriod(), emp));
                                }
                                logger.info("结果值ret=" + ret);
                        	}
                        	
                        }
                        //评估值公式计算
                        if ("0sys_use_formular_formula".equals(kpi.getIsAssessmentFormula())) {//如果是公式计算

                            if (StringUtils.isNotBlank(kpi.getAssessmentFormula())&&!"0".equals(kpi.getAssessmentFormula())) {//如果评估值公式不为空,用公式计算评估值,然后与告警区间比较
                                formulaLog = new FormulaLog();
                                String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getAssessmentFormula(), timePeriodId,
                                        kpi.getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                	computeFlag = true;
                                	assessMentValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setAssessmentValue(assessMentValue);
                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "assessmentValueFormula", kpi.getAssessmentFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(),
                                            emp));
                                }
                                else {
                                	computeFlag = false;
                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "assessmentValueFormula", kpi.getAssessmentFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(),
                                            emp));
                                }
                                logger.info("评估值ret=" + ret);
                            }

                        } else if("0sys_use_formular_export".equals(kpi.getIsAssessmentFormula())) {
                            if (StringUtils.isNotBlank(kpi.getAssessmentFormula())&&!"0".equals(kpi.getAssessmentFormula())) {//如果评估值公式不为空,用公式计算评估值,然后与告警区间比较
                                formulaLog = new FormulaLog();
                                String dsName = "";
                                if(null != kpi.getAsseessmentDs()) {
                                	dsName = kpi.getAsseessmentDs().getId();
                                }
                                String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(),kpi.getName(),kpi.getIsAssessmentDsType(),dsName,kpi.getAssessmentFormula(),timePeriodId,"assess");
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                	computeFlag = true;
                                    //返回结果是double，计算正确
                                	assessMentValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setAssessmentValue(assessMentValue);

                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "assessmentValueFormula", kpi.getAssessmentFormula(), "success", ret,
                                            currentKpiGatherResult.getTimePeriod(), emp));
                                }
                                else {
                                	computeFlag = false;
                                    //保存公式日志实体
                                    o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi",
                                            "assessmentValueFormula", kpi.getAssessmentFormula(), "failure", ret,
                                            currentKpiGatherResult.getTimePeriod(), emp));
                                }
                            }
                        	
                        }
                        if(computeFlag){
                        	//计算告警状态 
                            if (null != alarmPlan) {//告警方案不为空
                            	DictEntry assessmentStatus = null;
                            	if("kpi_alarm_measure_score".equals(alarmMeasure)){
                            		if(null!=assessMentValue){
                            			assessmentStatus= o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessMentValue);
                            		}
                            	}else {
                            		if(null!=finishValue){
                            			assessmentStatus= o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, finishValue);
                            		}
                            	}
                                currentKpiGatherResult.setAssessmentStatus(assessmentStatus);
                            }
                            
                            //计算趋势
                            DictEntry trend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(currentKpiGatherResult);
                            currentKpiGatherResult.setDirection(trend);

                            //保存指标采集结果
                            o_kpiGatherResultBO.mergeKpiGatherResult(currentKpiGatherResult);
                        }else{
                        	continue;
                        }
                        
                        
                        if (StringUtils.isNotBlank(lastTimePeriodId)
                        		&&currentKpiGatherResult.getTimePeriod().getStartTime().before(currentDate)
                        		&&currentKpiGatherResult.getTimePeriod().getEndTime().after(currentDate)) {
                        	//更新指标timeperiod字段
    	                    if (computeFlag) {
    		                    	kpi.setLastTimePeriod(timePeriod);
    	                            o_kpiBO.mergeKpi(kpi);
    	                            //更新指标下次执行时间
    	                            if (calculateTimeFlag) {
    	                            	 kpi.setCalculatetime(o_kpiBO.findCalculateNextExecuteTime(kpi, "finishValue"));
    	                                 kpi.setTargetCalculatetime(o_kpiBO.findCalculateNextExecuteTime(kpi, "targetValue"));
    	                        		o_kpiBO.mergeKpi(kpi);
    	                            }
    	                    }
    	                    
                        }
                        
                        if(computeFlag&&o_chartForEmailBO.findIsValueChangeSend()){
                        	//add by  haojing 2013-8-21  计算后发送邮件
                            if(StringUtils.isNotBlank(timePeriodId)){
                            	o_chartForEmailBO.sendFormulaCalculateEmail(kpiId,timePeriodId);
                            }else{
                            	o_chartForEmailBO.sendFormulaCalculateEmail(kpiId,lastTimePeriodId);
                            }
                        }

                    }

                    /*
                     * 累积值公式计算--包括:季度累积值计算和年度累积值计算
                     */
                    //季度累积值计算--只有月指标使用
                    //根据指标id查询当期所在季度的指标采集结果
                    KpiGatherResult quarterKpiGatherResult = o_kpiGatherResultBO.findQuarterKpiGatherResultByKpiId(kpi.getId(),timePeriodId);
                    if (null != quarterKpiGatherResult) {
                        if ("0frequecy_month".equals(kpi.getGatherFrequence().getId())) {
                            if (!"kpi_sum_measure_manual".equals(kpi.getTargetSumMeasure().getId())) {
                                Double targetValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(currentKpiGatherResult,
                                        "targetValueSum", kpi.getTargetSumMeasure().getId());
                                quarterKpiGatherResult.setTargetValue(targetValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
                                Double resultValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(currentKpiGatherResult,
                                        "resultValueSum", kpi.getResultSumMeasure().getId());
                                quarterKpiGatherResult.setFinishValue(resultValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
                                Double assessmentValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(currentKpiGatherResult,
                                        "assessmentValueSum", kpi.getAssessmentSumMeasure().getId());
                                quarterKpiGatherResult.setAssessmentValue(assessmentValueSum);
                                //计算预警状态
                                if(assessmentValueSum!=null) {
                                	 DictEntry assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessmentValueSum);
                                     quarterKpiGatherResult.setAssessmentStatus(assessmentStatus);
                                }
                                //计算趋势
                                DictEntry trend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(quarterKpiGatherResult);
                                quarterKpiGatherResult.setDirection(trend);
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
                                Double targetValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(currentKpiGatherResult,
                                        "targetValueSum", kpi.getTargetSumMeasure().getId());
                                yearKpiGatherResult.setTargetValue(targetValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
                                Double resultValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(currentKpiGatherResult,
                                        "resultValueSum", kpi.getResultSumMeasure().getId());
                                yearKpiGatherResult.setFinishValue(resultValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
                                Double assessmentValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(currentKpiGatherResult,
                                        "assessmentValueSum", kpi.getAssessmentSumMeasure().getId());
                                yearKpiGatherResult.setAssessmentValue(assessmentValueSum);
                                //计算预警状态  
                                if (null != alarmPlan && null!=assessmentValueSum) {
                                    DictEntry assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessmentValueSum);
                                    yearKpiGatherResult.setAssessmentStatus(assessmentStatus);
                                }
                                //计算趋势
                                DictEntry trend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(yearKpiGatherResult);
                                yearKpiGatherResult.setDirection(trend);
                            }
                            //保存指标采集结果年度累积值
                            o_kpiGatherResultBO.saveKpiGatherResult(yearKpiGatherResult);
                            
                        }
                    }

                }

            }
		} catch (Exception e) {
			logger.error("重新计算异常信息:"+e.toString());
		}

        return true;
    }
    /**
     * 根据指标采集频率cron表达式计算下次执行时间.
     * @param kpi
     * @return Date
     * @throws ParseException
     */
    public Date calculateNextExecuteTime(Kpi kpi) throws ParseException {
        CronTrigger trigger = new CronTrigger();
        if (StringUtils.isNotBlank(kpi.getGatherDayFormulr())) {
            trigger.setCronExpression(kpi.getGatherDayFormulr());
        }
        else {
            return null;
        }
        AnnualCalendar cal = new AnnualCalendar();
        trigger.computeFirstFireTime(cal);
        return trigger.getNextFireTime();
    }
}
