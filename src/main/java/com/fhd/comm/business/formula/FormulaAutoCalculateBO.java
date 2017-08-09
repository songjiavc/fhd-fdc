package com.fhd.comm.business.formula;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.ChartForEmailBO;
import com.fhd.sm.business.DataSourceCollectBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;

/**
 * 公式每天定时计算.
 * 触发时间表达式为：0 0 0 5 * ? --每月5号凌晨00:00:00触发
 * @author   吴德福
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-12-05		下午03:36:35
 */
@Service
public class FormulaAutoCalculateBO {

    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private FormulaCalculateBO o_formulaCalculateBO;

    @Autowired
    private FormulaLogBO o_formulaLogBO;

    @Autowired
    private TimePeriodBO o_timePeriodBO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;

    @Autowired
    private DataSourceCollectBO o_dataSourceCollectBO;

    @Autowired
    private ChartForEmailBO o_chartForEmailBO;

    private static Log logger = LogFactory.getLog(FormulaAutoCalculateBO.class);

    /**
     * 定时触发计算公式.
     * @throws ParseException 
     */
    @Transactional
    public boolean formulaAutoCalculate() throws ParseException {
        List<String> kpiIdList = new ArrayList<String>();
        List<Kpi> kpiList = o_kpiBO.findAllCalcKpiList();
        for (Kpi kpi : kpiList) {
            kpiIdList.add(kpi.getId());
        }
        //指标对应的告警方案map
        Map<String, AlarmPlan> alarmPlanMap = null;
        if (kpiIdList.size() > 0) {
            alarmPlanMap = o_alarmPlanBO.findAlarmPlanByKpiId(kpiIdList, Contents.ALARMPLAN_REPORT);
        }
        Map<String, Boolean> calcMap = new HashMap<String, Boolean>();
        Map<String, Boolean> logInfoMap = new HashMap<String, Boolean>();
        List<FormulaLog> formulaLogList = new ArrayList<FormulaLog>();
        for (int k = 0; k < new ArrayList<Kpi>(kpiList).size(); k++) {
            for (Kpi kpi : new ArrayList<Kpi>(kpiList)) {
                try {
                    AlarmPlan alarmPlan = alarmPlanMap.get(kpi.getId());
                    //定义公式计算日志
                    FormulaLog formulaLog = null;
                    SysEmployee emp = null;
                    //获得亮灯依据默认值
                    String alarmMeasure = "kpi_alarm_measure_score";
                    if (null != kpi.getAlarmMeasure()) {
                        alarmMeasure = kpi.getAlarmMeasure().getId();
                    }

                    //更新指标timeperiod标识位
                    boolean computeFlag = false;
                    //更新指标下次执行时间标识位
                    boolean calculateTimeFlag = true;

                    //根据指标id查询所有指标采集结果
                    //获得指标当期采集结果
                    KpiGatherResult currentKpiGatherResult = o_kpiGatherResultBO.findCurrentKpiGatherResultByKpiId(kpi.getId(), "");
                    if (null != currentKpiGatherResult) {
                        Double finishValue = null;
                        Double assessMentValue = null;
                        String timePeriodId = "";
                        if (null != currentKpiGatherResult.getTimePeriod()) {
                            timePeriodId = currentKpiGatherResult.getTimePeriod().getId();
                        }
                        //目标值公式计算
                        if ("0sys_use_formular_formula".equals(kpi.getIsTargetFormula())) {

                            if (StringUtils.isNotBlank(kpi.getTargetFormula()) && !"0".equals(kpi.getTargetFormula())) {
                                //创建公式日志实体
                                formulaLog = new FormulaLog();
                                String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getTargetFormula(), timePeriodId, kpi.getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                    //返回结果是double，计算正确
                                    computeFlag = true;
                                    currentKpiGatherResult.setTargetValue(NumberUtils.toDouble(ret));
                                    //保存公式日志实体
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("success").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA, kpi.getTargetFormula(),
                                                "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                                else {
                                    computeFlag = false;
                                    //返回结果是字符串描述，计算错误
                                    calculateTimeFlag = false;
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("failure").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        //保存公式日志实体
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA, kpi.getTargetFormula(),
                                                "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                            }

                        }
                        else if ("0sys_use_formular_export".equals(kpi.getIsTargetFormula())) {
                            // 外部数据采集
                            if (StringUtils.isNotBlank(kpi.getTargetFormula())) {
                                //创建公式日志实体
                                formulaLog = new FormulaLog();
                                String dsName = "";
                                if (null != kpi.getTargetDs()) {
                                    dsName = kpi.getTargetDs().getId();
                                }
                                String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(), kpi.getName(), kpi.getIsTargetDsType(), dsName, kpi.getTargetFormula(), timePeriodId, "target");
                                if (o_dataSourceCollectBO.isDoubleOrInteger(ret)) {
                                    computeFlag = true;
                                    currentKpiGatherResult.setTargetValue(NumberUtils.toDouble(ret));
                                    //保存公式日志实体
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("success").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA, kpi.getTargetFormula(),
                                                "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                                else {
                                    computeFlag = false;
                                    //返回结果是字符串描述，计算错误
                                    calculateTimeFlag = false;
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("failure").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA, kpi.getTargetFormula(),
                                                "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                            }
                        }
                        //结果值公式计算
                        if ("0sys_use_formular_formula".equals(kpi.getIsResultFormula())) {

                            if (StringUtils.isNotBlank(kpi.getResultFormula()) && !"0".equals(kpi.getResultFormula())) {
                                //创建公式日志实体
                                formulaLog = new FormulaLog();

                                String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getResultFormula(), timePeriodId, kpi.getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                    //返回结果是double，计算正确
                                    computeFlag = true;
                                    finishValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setFinishValue(finishValue);
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("success").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        //保存公式日志实体
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA, kpi.getResultFormula(),
                                                "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }

                                }
                                else {
                                    computeFlag = false;
                                    //返回结果是字符串描述，计算错误
                                    calculateTimeFlag = false;
                                    //保存公式日志实体
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("failure").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA, kpi.getResultFormula(),
                                                "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                            }
                        }
                        else if ("0sys_use_formular_export".equals(kpi.getIsResultFormula())) {
                            // 外部数据采集
                            if (StringUtils.isNotBlank(kpi.getResultFormula())) {
                                //创建公式日志实体
                                formulaLog = new FormulaLog();
                                String dsName = "";
                                if (null != kpi.getResultDs()) {
                                    dsName = kpi.getResultDs().getId();
                                }
                                String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(), kpi.getName(), kpi.getIsResultDsType(), dsName, kpi.getResultFormula(), timePeriodId, "result");
                                if (o_dataSourceCollectBO.isDoubleOrInteger(ret)) {
                                    computeFlag = true;
                                    finishValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setFinishValue(finishValue);
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("success").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        //保存公式日志实体
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA, kpi.getResultFormula(),
                                                "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                                else {
                                    computeFlag = false;
                                    //返回结果是字符串描述，计算错误
                                    calculateTimeFlag = false;
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("failure").toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA, kpi.getResultFormula(),
                                                "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                            }

                        }
                        //评估值公式计算
                        if ("0sys_use_formular_formula".equals(kpi.getIsAssessmentFormula())) {//如果是公式计算

                            if (StringUtils.isNotBlank(kpi.getAssessmentFormula())) {//如果评估值公式不为空,用公式计算评估值,然后与告警区间比较
                                formulaLog = new FormulaLog();
                                String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getAssessmentFormula(), timePeriodId, kpi.getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                    //返回结果是double，计算正确
                                    computeFlag = true;
                                    assessMentValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setAssessmentValue(assessMentValue);
                                    //保存公式日志实体
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.ASSESSMENT_VALUE_FORMULA).append("-----").append("success")
                                            .toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.ASSESSMENT_VALUE_FORMULA,
                                                kpi.getAssessmentFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                                else {
                                    computeFlag = false;
                                    //返回结果是字符串描述，计算错误
                                    calculateTimeFlag = false;
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.ASSESSMENT_VALUE_FORMULA).append("-----").append("failure")
                                            .toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        //保存公式日志实体
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.ASSESSMENT_VALUE_FORMULA,
                                                kpi.getAssessmentFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                            }
                        }
                        else if ("0sys_use_formular_export".equals(kpi.getIsAssessmentFormula())) {
                            if (StringUtils.isNotBlank(kpi.getAssessmentFormula())) {//如果评估值公式不为空,用公式计算评估值,然后与告警区间比较
                                formulaLog = new FormulaLog();
                                String dsName = "";
                                if (null != kpi.getAsseessmentDs()) {
                                    dsName = kpi.getAsseessmentDs().getId();
                                }
                                String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(), kpi.getName(), kpi.getIsAssessmentDsType(), dsName, kpi.getAssessmentFormula(), timePeriodId,
                                        "assess");
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                    //返回结果是double，计算正确
                                    computeFlag = true;
                                    assessMentValue = NumberUtils.toDouble(ret);
                                    currentKpiGatherResult.setAssessmentValue(assessMentValue);
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.ASSESSMENT_VALUE_FORMULA).append("-----").append("success")
                                            .toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        //保存公式日志实体
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.ASSESSMENT_VALUE_FORMULA,
                                                kpi.getAssessmentFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                                else {
                                    computeFlag = false;
                                    //返回结果是字符串描述，计算错误
                                    calculateTimeFlag = false;
                                    //保存公式日志实体
                                    String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.ASSESSMENT_VALUE_FORMULA).append("-----").append("failure")
                                            .toString();
                                    if (!logInfoMap.containsKey(kpiFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.ASSESSMENT_VALUE_FORMULA,
                                                kpi.getAssessmentFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), emp));
                                        logInfoMap.put(kpiFormulaType, true);
                                    }
                                }
                            }
                        }

                        if (computeFlag) {
                            //计算告警状态 

                            if (null != alarmPlan) {//告警方案不为空
                                DictEntry assessmentStatus = null;
                                if ("kpi_alarm_measure_score".equals(alarmMeasure)) {
                                    if (null != assessMentValue) {
                                        assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessMentValue);
                                    }
                                }
                                else {
                                    if (null != finishValue) {
                                        assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, finishValue);
                                    }
                                }
                                currentKpiGatherResult.setAssessmentStatus(assessmentStatus);
                            }

                            //计算趋势
                            DictEntry trend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(currentKpiGatherResult);
                            currentKpiGatherResult.setDirection(trend);

                            //保存指标采集结果
                            o_kpiGatherResultBO.mergeKpiGatherResult(currentKpiGatherResult);

                            calcMap.put(kpi.getId(), true);

                            //更新指标timeperiod字段
                            String lastTimePeriod = o_kpiBO.findKpiNewValue(kpi.getId());
                            if (null != lastTimePeriod) {
                                kpi.setLastTimePeriod(o_timePeriodBO.findTimePeriodById(lastTimePeriod));
                                o_kpiBO.mergeKpi(kpi);
                            }
                            //更新指标下次执行时间
                            if (calculateTimeFlag) {
                                kpi.setCalculatetime(o_kpiBO.findCalculateNextExecuteTime(kpi, "finishValue"));
                                kpi.setTargetCalculatetime(o_kpiBO.findCalculateNextExecuteTime(kpi, "targetValue"));
                                o_kpiBO.mergeKpi(kpi);
                            }

                            kpiList.remove(kpi);
                        }
                        else {
                            calcMap.put(kpi.getId(), false);

                            continue;
                        }

                    }

                    /*
                     * 累积值公式计算--包括:季度累积值计算和年度累积值计算
                     */
                    //季度累积值计算--只有月指标使用
                    //根据指标id查询当期所在季度的指标采集结果
                    KpiGatherResult quarterKpiGatherResult = o_kpiGatherResultBO.findQuarterKpiGatherResultByKpiId(kpi.getId(), "");
                    if (null != quarterKpiGatherResult) {
                        if ("0frequecy_month".equals(kpi.getGatherFrequence().getId())) {
                            if (!"kpi_sum_measure_manual".equals(kpi.getTargetSumMeasure().getId())) {
                                Double targetValueSum = o_kpiGatherResultBO
                                        .calculateQuarterAccumulatedValueByCalculateItem(currentKpiGatherResult, "targetValueSum", kpi.getTargetSumMeasure().getId());
                                quarterKpiGatherResult.setTargetValue(targetValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
                                Double resultValueSum = o_kpiGatherResultBO
                                        .calculateQuarterAccumulatedValueByCalculateItem(currentKpiGatherResult, "resultValueSum", kpi.getResultSumMeasure().getId());
                                quarterKpiGatherResult.setFinishValue(resultValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
                                Double assessmentValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(currentKpiGatherResult, "assessmentValueSum", kpi
                                        .getAssessmentSumMeasure().getId());
                                quarterKpiGatherResult.setAssessmentValue(assessmentValueSum);
                                //计算预警状态
                                if (null != assessmentValueSum && null != alarmPlan) {
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
                    KpiGatherResult yearKpiGatherResult = o_kpiGatherResultBO.findYearKpiGatherResultByKpiId(kpi.getId(), "");
                    if (null != yearKpiGatherResult) {
                        if ("0frequecy_year".equals(kpi.getGatherFrequence().getId())) {
                            logger.info("年度指标不需要进行年度累积值计算");
                        }
                        else {
                            if (!"kpi_sum_measure_manual".equals(kpi.getTargetSumMeasure().getId())) {
                                Double targetValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(currentKpiGatherResult, "targetValueSum", kpi.getTargetSumMeasure().getId());
                                yearKpiGatherResult.setTargetValue(targetValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
                                Double resultValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(currentKpiGatherResult, "resultValueSum", kpi.getResultSumMeasure().getId());
                                yearKpiGatherResult.setFinishValue(resultValueSum);
                            }
                            if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
                                Double assessmentValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(currentKpiGatherResult, "assessmentValueSum", kpi
                                        .getAssessmentSumMeasure().getId());
                                yearKpiGatherResult.setAssessmentValue(assessmentValueSum);
                                //计算预警状态  
                                if (null != alarmPlan && null != assessmentValueSum) {
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
                    //add by  haojing 2013-8-21  计算后发送邮件
                    if (computeFlag && o_chartForEmailBO.findIsValueChangeSend() && null != currentKpiGatherResult) {
                        o_chartForEmailBO.sendFormulaCalculateEmail(kpi.getId(), currentKpiGatherResult.getTimePeriod().getId());
                    }

                }
                catch (Exception e) {
                    logger.error("公式计算异常信息:" + e.toString());
                    continue;
                }

            }
        }
        //批量插入公式计算日志
        o_formulaLogBO.batchSaveFormulaLog(formulaLogList);

        return true;
    }

}
