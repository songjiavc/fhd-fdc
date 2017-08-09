package com.fhd.comm.business.formula;

import static org.hibernate.FetchMode.JOIN;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.quartz.CronTrigger;
import org.quartz.impl.calendar.AnnualCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.TimePeriodBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.dao.kpi.KpiGatherResultDAO;
import com.fhd.dao.kpi.KpiRelaAlarmDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.KpiRelaAlarm;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.DataSourceCollectBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sys.business.dic.DictBO;

@Service
public class CalculateBatchBO {

    @Autowired
    private CalculateFormulaBO o_formulaCalculateBO;

    @Autowired
    private DataSourceCollectBO o_dataSourceCollectBO;

    @Autowired
    private KpiGatherResultDAO o_kpiGatherResultDAO;

    @Autowired
    private KpiRelaAlarmDAO o_KpiRelaAlarmDAO;

    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private FormulaLogBO o_formulaLogBO;

    @Autowired
    private DictBO o_DictBO;

    @Autowired
    private TimePeriodBO o_timePeriodBO;

    private static Log logger = LogFactory.getLog(CalculateBatchBO.class);

    /**根据公式计算指标值
     * @param kpiList 指标对象集合
     */
    @Transactional
    public void calculateByFormula(List<Kpi> kpiList) {
        //指标id集合
        List<String> kpiIdList = new ArrayList<String>();

        List<String> calcKpiIdList = new ArrayList<String>();

        FormulaLog formulaLog = null;

        List<FormulaLog> formulaLogList = new ArrayList<FormulaLog>();

        List<KpiGatherResult> calcGatherResultList = new ArrayList<KpiGatherResult>();
        //告警依据map
        Map<String, String> alarmMeasureMap = new HashMap<String, String>();

        //过滤kpiList
        List<Kpi> validKpiList = new ArrayList<Kpi>();
        for (Kpi kpi : kpiList) {
            String kpiId = kpi.getId();
            alarmMeasureMap.put(kpiId, kpi.getAlarmMeasure() == null ? "kpi_alarm_measure_score" : kpi.getAlarmMeasure().getId());
            validKpiList.add(kpi);
            kpiIdList.add(kpiId);
        }
        if (validKpiList.size() > 0) {
            DictEntry trendDict = null;
            DictEntry upDict = o_DictBO.findDictEntryById("up");
            DictEntry flatDict = o_DictBO.findDictEntryById("flat");
            DictEntry downDict = o_DictBO.findDictEntryById("down");

            List<Kpi> calcKpiList = new ArrayList<Kpi>();

            Map<String, Boolean> calcMap = new HashMap<String, Boolean>();
            Map<String, Boolean> logInfoMap = new HashMap<String, Boolean>();

            //告警方案map
            Map<String, AlarmPlan> alarmPlanMap = findAlarmPlanByKpiId(kpiIdList, Contents.ALARMPLAN_REPORT);
            //最后更新时间map
            Map<String, TimePeriod> lastTimePeriodMap = getKpiLastTimePeriodMap(kpiIdList);

            Map<String, KpiGatherResult> resultMap = o_kpiGatherResultBO.findCurrentKpiGatherResultMapByKpiIds(null, "");

            if (null != resultMap) {
                for (int k = 0; k < new ArrayList<Kpi>(validKpiList).size(); k++) {
                    for (Kpi kpi : new ArrayList<Kpi>(validKpiList)) {
                        List<KpiGatherResult> kpiResultList = new ArrayList<KpiGatherResult>(resultMap.values());
                        Map<String, Object> valueMapObj = new HashMap<String, Object>();
                        valueMapObj.put("allKpiResultList", kpiResultList);
                        try {
                            //更新指标timeperiod标识位
                            boolean computeFlag = false;
                            //更新指标下次执行时间标识位
                            boolean calculateTimeFlag = true;
                            //取出当期采集结果对象
                            String kpiId = kpi.getId();
                            KpiGatherResult currentKpiGatherResult = resultMap.get(kpiId);
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
                                        String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getTargetFormula(), timePeriodId, kpi.getCompany().getId(), valueMapObj);
                                        if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                            //返回结果是double，计算正确
                                            computeFlag = true;
                                            currentKpiGatherResult.setTargetValue(NumberUtils.toDouble(ret));
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("success")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                //保存公式日志实体
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA,
                                                        kpi.getTargetFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), null));
                                                logInfoMap.put(kpiFormulaType, true);
                                            }
                                        }
                                        else {
                                            computeFlag = false;
                                            //返回结果是字符串描述，计算错误
                                            calculateTimeFlag = false;
                                            //保存公式日志实体
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("failure")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA,
                                                        kpi.getTargetFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), null));
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
                                        String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(), kpi.getName(), kpi.getIsTargetDsType(), dsName, kpi.getTargetFormula(), timePeriodId,
                                                "target");
                                        if (o_dataSourceCollectBO.isDoubleOrInteger(ret)) {
                                            computeFlag = true;
                                            currentKpiGatherResult.setTargetValue(NumberUtils.toDouble(ret));
                                            //保存公式日志实体
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("success")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA,
                                                        kpi.getTargetFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), null));
                                                logInfoMap.put(kpiFormulaType, true);
                                            }

                                        }
                                        else {
                                            computeFlag = false;
                                            //返回结果是字符串描述，计算错误
                                            calculateTimeFlag = false;
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.TARGET_VALUE_FORMULA).append("-----").append("failure")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.TARGET_VALUE_FORMULA,
                                                        kpi.getTargetFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), null));
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

                                        String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getResultFormula(), timePeriodId, kpi.getCompany().getId(), valueMapObj);
                                        if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                            //返回结果是double，计算正确
                                            computeFlag = true;
                                            finishValue = NumberUtils.toDouble(ret);
                                            currentKpiGatherResult.setFinishValue(finishValue);
                                            //保存公式日志实体
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("success")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA,
                                                        kpi.getResultFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), null));
                                                logInfoMap.put(kpiFormulaType, true);
                                            }
                                        }
                                        else {
                                            computeFlag = false;
                                            //返回结果是字符串描述，计算错误
                                            calculateTimeFlag = false;
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("failure")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                //保存公式日志实体
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA,
                                                        kpi.getResultFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), null));
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
                                        String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(), kpi.getName(), kpi.getIsResultDsType(), dsName, kpi.getResultFormula(), timePeriodId,
                                                "result");
                                        if (o_dataSourceCollectBO.isDoubleOrInteger(ret)) {
                                            computeFlag = true;
                                            finishValue = NumberUtils.toDouble(ret);
                                            currentKpiGatherResult.setFinishValue(finishValue);
                                            //保存公式日志实体
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("success")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA,
                                                        kpi.getResultFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), null));
                                                logInfoMap.put(kpiFormulaType, true);
                                            }
                                        }
                                        else {
                                            computeFlag = false;
                                            //返回结果是字符串描述，计算错误
                                            calculateTimeFlag = false;
                                            String kpiFormulaType = new StringBuffer().append(kpi.getId()).append("-----").append(Contents.RESULT_VALUE_FORMULA).append("-----").append("failure")
                                                    .toString();
                                            if (!logInfoMap.containsKey(kpiFormulaType)) {
                                                formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, kpi.getId(), kpi.getName(), "kpi", Contents.RESULT_VALUE_FORMULA,
                                                        kpi.getResultFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), null));
                                                logInfoMap.put(kpiFormulaType, true);
                                            }
                                        }
                                    }

                                }

                                //评估值公式计算
                                if ("0sys_use_formular_formula".equals(kpi.getIsAssessmentFormula())) {//如果是公式计算

                                    if (StringUtils.isNotBlank(kpi.getAssessmentFormula())) {//如果评估值公式不为空,用公式计算评估值,然后与告警区间比较
                                        formulaLog = new FormulaLog();
                                        String ret = o_formulaCalculateBO.calculate(kpi.getId(), kpi.getName(), "kpi", kpi.getAssessmentFormula(), timePeriodId, kpi.getCompany().getId(), valueMapObj);
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
                                                        kpi.getAssessmentFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), null));
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
                                                        kpi.getAssessmentFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), null));
                                                logInfoMap.put(kpiFormulaType, true);
                                            }
                                        }
                                    }
                                }
                                else if ("0sys_use_formular_export".equals(kpi.getIsAssessmentFormula())) {
                                    if (StringUtils.isNotBlank(kpi.getAssessmentFormula())) {//如果评估值公式不为空,用公式计算评估值,然后与告警区间比较
                                        String dsName = "";
                                        formulaLog = new FormulaLog();
                                        if (null != kpi.getAsseessmentDs()) {
                                            dsName = kpi.getAsseessmentDs().getId();
                                        }
                                        String ret = o_dataSourceCollectBO.executeConfigDetails(kpi.getId(), kpi.getName(), kpi.getIsAssessmentDsType(), dsName, kpi.getAssessmentFormula(),
                                                timePeriodId, "assess");
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
                                                        kpi.getAssessmentFormula(), "success", ret, currentKpiGatherResult.getTimePeriod(), null));
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
                                                        kpi.getAssessmentFormula(), "failure", ret, currentKpiGatherResult.getTimePeriod(), null));
                                                logInfoMap.put(kpiFormulaType, true);
                                            }
                                        }
                                    }
                                }

                                if (computeFlag) {

                                    //计算告警状态
                                    String alarmMeasure = alarmMeasureMap.get(kpiId);
                                    AlarmPlan alarmPlan = alarmPlanMap.get(kpiId);
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

                                    calcKpiIdList.add(kpi.getId());

                                    calcMap.put(kpi.getId(), true);

                                    calcGatherResultList.add(currentKpiGatherResult);

                                    validKpiList.remove(kpi);
                                }
                                else {
                                    calcMap.put(kpi.getId(), false);
                                    continue;
                                }

                                //更新指标timeperiod字段
                                if (computeFlag) {
                                    TimePeriod lastTimePeriod = lastTimePeriodMap.get(kpiId);
                                    if (null != lastTimePeriod) {
                                        kpi.setLastTimePeriod(lastTimePeriod);
                                    }
                                    //更新指标下次执行时间
                                    if (calculateTimeFlag) {
                                        kpi.setCalculatetime(calculateNextExecuteTime(kpi, "finishValue"));
                                        kpi.setTargetCalculatetime(calculateNextExecuteTime(kpi, "targetValue"));
                                    }
                                    calcKpiList.add(kpi);
                                }
                            }

                        }
                        catch (Exception e) {
                            logger.error("公式计算异常信息:" + e.toString());
                            continue;
                        }

                    }
                }
                if (calcGatherResultList.size() > 0) {
                    //更新趋势
                    Map<String, Double> trendMap = findTrend(calcGatherResultList);
                    for (KpiGatherResult kpiGatherResult : calcGatherResultList) {
                        String resultId = kpiGatherResult.getId();
                        Double currValue = kpiGatherResult.getAssessmentValue();
                        Double preValue = trendMap.get(resultId);
                        if (null == preValue) {
                            trendDict = upDict;
                        }
                        else if (currValue.compareTo(preValue) > 0) {
                            trendDict = upDict;
                        }
                        else if (currValue.compareTo(preValue) == 0) {
                            trendDict = flatDict;
                        }
                        else if (currValue.compareTo(preValue) < 0) {
                            trendDict = downDict;
                        }
                        kpiGatherResult.setDirection(trendDict);
                    }
                    //批量更新指标
                    batchUpdateKpi(calcKpiList);
                    //批量更新指标采集结果
                    batchUpdateKpiResults(calcGatherResultList);
                    //批量季度汇总
                    String currentYear = DateUtils.getYear(new Date());

                    batchUpdateQuarterDataSum(calcKpiIdList, currentYear);
                    //批量年汇总
                    batchUpdateYearDataSum(calcKpiIdList, currentYear);
                    //批量发送邮件
                }

                //批量插入日志
                o_formulaLogBO.batchSaveFormulaLog(formulaLogList);

                //清除session缓存数据
                clearSession();
            }
        }
    }

    /**批量更新年汇总
     * @param kpiIdList 指标id集合
     * @param year 年信息
     */
    @Transactional
    public void batchUpdateYearDataSum(final List<String> kpiIdList, final String year) {
        final Map<String, Map<String, List<Double>>> sumMap = findKpiYearData(kpiIdList, year);
        o_kpiGatherResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                String sql = "update t_kpi_kpi_gather_result set target_value=?,finish_value=?,assessment_value=? where kpi_id=? and time_period_id=? ";
                connection.setAutoCommit(false);
                PreparedStatement pst = connection.prepareStatement(sql);
                for (Entry<String, Map<String, List<Double>>> valueEntry : sumMap.entrySet()) {
                    String kpiId = valueEntry.getKey();
                    Map<String, List<Double>> resultValues = valueEntry.getValue();
                    Set<String> valueTypeSet = resultValues.keySet();
                    Map<String, Double> typeValueMap = new HashMap<String, Double>();
                    for (String valueType : valueTypeSet) {
                        String[] valueTypeArray = StringUtils.split(valueType, ";");
                        String type = valueTypeArray[0];
                        String sumMethod = valueTypeArray[1];
                        Double valueSum = o_kpiGatherResultBO.calculateAccumulateValue(resultValues.get(valueType), sumMethod);
                        typeValueMap.put(type, valueSum);

                    }
                    pst.setObject(1, typeValueMap.get("targetValueSum"));
                    pst.setObject(2, typeValueMap.get("resultValueSum"));
                    pst.setObject(3, typeValueMap.get("assessmentValueSum"));
                    pst.setObject(4, kpiId);
                    pst.setObject(5, year);
                    pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);

            }
        });
    }

    /**批量更新季度汇总
     * @param kpiIdList 指标id集合
     * @param year 年
     */
    @Transactional
    public void batchUpdateQuarterDataSum(List<String> kpiIdList, String year) {

        final Map<String, Map<String, Map<String, List<Double>>>> sumMap = findKpiQuarterData(kpiIdList, DateUtils.getYear(new Date()));
        o_kpiGatherResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                String sql = "update t_kpi_kpi_gather_result set target_value=?,finish_value=?,assessment_value=? where kpi_id=? and time_period_id=? ";
                connection.setAutoCommit(false);
                PreparedStatement pst = connection.prepareStatement(sql);
                for (Entry<String, Map<String, Map<String, List<Double>>>> timeEntry : sumMap.entrySet()) {
                    String kpiId = timeEntry.getKey();
                    Map<String, Map<String, List<Double>>> timeMap = timeEntry.getValue();
                    for (Entry<String, Map<String, List<Double>>> valueEntry : timeMap.entrySet()) {
                        String timeId = valueEntry.getKey();
                        Map<String, List<Double>> resultValues = valueEntry.getValue();
                        Set<String> valueTypeSet = resultValues.keySet();
                        Map<String, Double> typeValueMap = new HashMap<String, Double>();
                        for (String valueType : valueTypeSet) {
                            String[] valueTypeArray = StringUtils.split(valueType, ";");
                            String type = valueTypeArray[0];
                            String sumMethod = valueTypeArray[1];
                            Double valueSum = o_kpiGatherResultBO.calculateAccumulateValue(resultValues.get(valueType), sumMethod);
                            typeValueMap.put(type, valueSum);

                        }
                        pst.setObject(1, typeValueMap.get("targetValueSum"));
                        pst.setObject(2, typeValueMap.get("resultValueSum"));
                        pst.setObject(3, typeValueMap.get("assessmentValueSum"));
                        pst.setObject(4, kpiId);
                        pst.setObject(5, timeId);
                        pst.addBatch();
                    }

                }

                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });

    }

    /**根据指标id集合查询指标所在年采集数据
     * @param kpiIdList 指标id集合
     * @param year 年
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, List<Double>>> findKpiYearData(List<String> kpiIdList, String year) {
        List<String> IdList = new ArrayList<String>();
        StringBuffer querySqlBuf = new StringBuffer();
        for (String kpiId : kpiIdList) {
            StringBuffer kpiIdBuf = new StringBuffer();
            kpiIdBuf.append("'").append(kpiId).append("'");
            IdList.add(kpiIdBuf.toString());
        }
        Map<String, Map<String, List<Double>>> sumMap = new HashMap<String, Map<String, List<Double>>>();
        querySqlBuf.append(" select kpi.id,kpi.kpi_name,timeperiod.id timeid,assementmeasure.id assementmeasure,finishmeasure.id finishmeasure,");
        querySqlBuf.append(" targetmeasure.id targetmeasure,result.target_value,result.finish_value,result.assessment_value  ");
        querySqlBuf.append(" FROM t_kpi_kpi_gather_result result INNER JOIN t_kpi_kpi kpi ON result.kpi_id = kpi.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_sys_dict_entry assementmeasure ON kpi.assessment_value_sum_measure = assementmeasure.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_sys_dict_entry finishmeasure ON kpi.finish_value_sum_measure = finishmeasure.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_sys_dict_entry targetmeasure ON kpi.target_value_sum_measure = targetmeasure.id ");
        querySqlBuf.append(" INNER JOIN t_com_time_period timeperiod ON result.time_period_id = timeperiod.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_com_time_period ptimeperiod ON timeperiod.parent_id = ptimeperiod.id ");
        querySqlBuf.append(" WHERE ").append(" kpi.id IN (").append(StringUtils.join(IdList, ",")).append(") ");
        querySqlBuf.append(" AND timeperiod.id LIKE '%").append(year).append("%'").append(" and timeperiod.etype=kpi.GATHER_FREQUENCE ");
        querySqlBuf.append(" and !(target_value is null and FINISH_VALUE is null and ASSESSMENT_VALUE is null) ");
        querySqlBuf.append(" and kpi.GATHER_FREQUENCE<>'0frequecy_year' ");
        querySqlBuf.append(" ORDER BY kpi.kpi_name ,timeperiod.id ");
        SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(querySqlBuf.toString());
        List<Object[]> list = sqlQuery.list();
        for (Object[] objects : list) {
            String kpiId = String.valueOf(objects[0]);
            Double finishValue = objects[7] == null ? null : Double.valueOf(String.valueOf(objects[7]));
            Double targetValue = objects[6] == null ? null : Double.valueOf(String.valueOf(objects[6]));
            Double assessmentValue = objects[8] == null ? null : Double.valueOf(String.valueOf(objects[8]));
            String finishSumMethod = objects[4] == null ? null : String.valueOf(objects[4]);
            String targetSumMethod = objects[5] == null ? null : String.valueOf(objects[5]);
            String assessSumMethod = objects[3] == null ? null : String.valueOf(objects[3]);
            if (!sumMap.containsKey(kpiId)) {
                Map<String, List<Double>> valueMap = new HashMap<String, List<Double>>();

                List<Double> finishValueList = new ArrayList<Double>();
                valueMap.put("resultValueSum;" + finishSumMethod, finishValueList);

                List<Double> targetValueList = new ArrayList<Double>();
                valueMap.put("targetValueSum;" + targetSumMethod, targetValueList);

                List<Double> assessValueList = new ArrayList<Double>();
                valueMap.put("assessmentValueSum;" + assessSumMethod, assessValueList);

                sumMap.put(kpiId, valueMap);
            }

            Map<String, List<Double>> valueMap = sumMap.get(kpiId);
            if (finishValue != null && !"kpi_sum_measure_manual".equals(finishSumMethod)) {
                List<Double> finishValueList = valueMap.get("resultValueSum;" + finishSumMethod);
                finishValueList.add(finishValue);
            }
            if (targetValue != null && !"kpi_sum_measure_manual".equals(targetSumMethod)) {
                List<Double> targetValueList = valueMap.get("targetValueSum;" + targetSumMethod);
                targetValueList.add(targetValue);
            }
            if (assessmentValue != null && !"kpi_sum_measure_manual".equals(assessSumMethod)) {
                List<Double> assessValueList = valueMap.get("assessmentValueSum;" + assessSumMethod);
                assessValueList.add(assessmentValue);
            }
        }
        return sumMap;
    }

    /**根据指标id集合查询所在年的季度数据
     * @param kpiIdList 指标id集合
     * @param year 年
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Map<String, List<Double>>>> findKpiQuarterData(List<String> kpiIdList, String year) {
        List<String> IdList = new ArrayList<String>();
        for (String kpiId : kpiIdList) {
            StringBuffer kpiIdBuf = new StringBuffer();
            kpiIdBuf.append("'").append(kpiId).append("'");
            IdList.add(kpiIdBuf.toString());
        }
        TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome(Contents.FREQUECY_QUARTER);
        Map<String, Map<String, Map<String, List<Double>>>> sumMap = new HashMap<String, Map<String, Map<String, List<Double>>>>();
        StringBuffer querySqlBuf = new StringBuffer();
        querySqlBuf
                .append("SELECT kpi.id,kpi.kpi_name,ptimeperiod.id ptid,assementmeasure.id assementmeasure ,finishmeasure.id finishmeasure,targetmeasure.id targetmeasure,result.target_value,result.finish_value,result.assessment_value ");
        querySqlBuf.append(" FROM t_kpi_kpi_gather_result result INNER JOIN t_kpi_kpi kpi ON result.kpi_id = kpi.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_sys_dict_entry assementmeasure ON kpi.assessment_value_sum_measure = assementmeasure.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_sys_dict_entry finishmeasure ON kpi.finish_value_sum_measure = finishmeasure.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_sys_dict_entry targetmeasure ON kpi.target_value_sum_measure = targetmeasure.id ");
        querySqlBuf.append(" INNER JOIN t_com_time_period timeperiod ON result.time_period_id = timeperiod.id ");
        querySqlBuf.append(" LEFT OUTER JOIN t_com_time_period ptimeperiod ON timeperiod.parent_id = ptimeperiod.id ");
        querySqlBuf.append(" WHERE kpi.gather_frequence ='0frequecy_month' AND kpi.id IN (").append(StringUtils.join(IdList, ",")).append(") ");
        querySqlBuf.append(" AND timeperiod.id LIKE '%").append(year).append("%' ").append(" AND NOT (timeperiod.id LIKE '%Q%') ");
        querySqlBuf.append(" AND timeperiod.id <> '").append(year).append("' ").append(" AND result.begin_time <=now()  ");
        //querySqlBuf.append(" AND timeperiod.id <> '").append(year).append("' ").append(" ");
        querySqlBuf.append(" and !(target_value is null and FINISH_VALUE is null and ASSESSMENT_VALUE is null) ");
        if (null != timePeriod) {
            querySqlBuf.append(" and  ptimeperiod.id='").append(timePeriod.getId()).append("' ");
        }
        querySqlBuf.append(" ORDER BY result.time_period_id ASC ");
        SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(querySqlBuf.toString());
        List<Object[]> list = sqlQuery.list();

        for (Object[] kpiGatherResult : list) {
            String kpiId = String.valueOf(kpiGatherResult[0]);
            String parentTimePeriodId = String.valueOf(kpiGatherResult[2]);
            Double finishValue = kpiGatherResult[7] == null ? null : Double.valueOf(String.valueOf(kpiGatherResult[7]));
            Double targetValue = kpiGatherResult[6] == null ? null : Double.valueOf(String.valueOf(kpiGatherResult[6]));
            Double assessmentValue = kpiGatherResult[8] == null ? null : Double.valueOf(String.valueOf(kpiGatherResult[8]));
            String finishSumMethod = kpiGatherResult[4] == null ? null : String.valueOf(kpiGatherResult[4]);
            String targetSumMethod = kpiGatherResult[5] == null ? null : String.valueOf(kpiGatherResult[5]);
            String assessSumMethod = kpiGatherResult[3] == null ? null : String.valueOf(kpiGatherResult[3]);

            if (!sumMap.containsKey(kpiId)) {
                Map<String, Map<String, List<Double>>> timeMap = new HashMap<String, Map<String, List<Double>>>();

                Map<String, List<Double>> valueMap = new HashMap<String, List<Double>>();

                List<Double> finishValueList = new ArrayList<Double>();
                valueMap.put("resultValueSum;" + finishSumMethod, finishValueList);

                List<Double> targetValueList = new ArrayList<Double>();
                valueMap.put("targetValueSum;" + targetSumMethod, targetValueList);

                List<Double> assessValueList = new ArrayList<Double>();
                valueMap.put("assessmentValueSum;" + assessSumMethod, assessValueList);

                timeMap.put(parentTimePeriodId, valueMap);

                sumMap.put(kpiId, timeMap);
            }
            Map<String, Map<String, List<Double>>> timeMap = sumMap.get(kpiId);
            Map<String, List<Double>> valueMap = timeMap.get(parentTimePeriodId);
            if (null != valueMap) {
                if (finishValue != null && !"kpi_sum_measure_manual".equals(finishSumMethod)) {
                    List<Double> finishValueList = valueMap.get("resultValueSum;" + finishSumMethod);
                    finishValueList.add(finishValue);
                }
                if (targetValue != null && !"kpi_sum_measure_manual".equals(targetSumMethod)) {
                    List<Double> targetValueList = valueMap.get("targetValueSum;" + targetSumMethod);
                    targetValueList.add(targetValue);
                }
                if (assessmentValue != null && !"kpi_sum_measure_manual".equals(assessSumMethod)) {
                    List<Double> assessValueList = valueMap.get("assessmentValueSum;" + assessSumMethod);
                    assessValueList.add(assessmentValue);
                }
            }

        }

        return sumMap;
    }

    /**根据采集结果集合查询出趋势信息
     * @param calcGatherResultList 采集结果集合
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Double> findTrend(List<KpiGatherResult> calcGatherResultList) {
        Map<String, Double> trendMap = new HashMap<String, Double>();
        if (null != calcGatherResultList && calcGatherResultList.size() > 0) {
            List<String> kpiIdList = new ArrayList<String>();
            for (KpiGatherResult kpiGatherResult : calcGatherResultList) {
                StringBuffer kpiIdBuf = new StringBuffer();
                kpiIdBuf.append("'").append(kpiGatherResult.getKpi().getId()).append("'");
                kpiIdList.add(kpiIdBuf.toString());
            }

            StringBuffer sqlBuf = new StringBuffer();
            sqlBuf.append("select CASE WHEN result.ASSESSMENT_VALUE < t.ASSESSMENT_VALUE THEN 'up'  ");
            sqlBuf.append("WHEN result.ASSESSMENT_VALUE = t.ASSESSMENT_VALUE THEN 'flat' ");
            sqlBuf.append("WHEN result.ASSESSMENT_VALUE > t.ASSESSMENT_VALUE THEN 'down' ");
            sqlBuf.append("ELSE NULL END trend ,t.rid ,result.KPI_ID, result.ASSESSMENT_VALUE prevalue, result.TIME_PERIOD_ID,t.ASSESSMENT_VALUE currentvalue ");
            sqlBuf.append("FROM t_kpi_kpi kpi ");
            sqlBuf.append("INNER JOIN ( SELECT result.id rid,time.PRE_PERIOD_ID,kpi.id id,result.ASSESSMENT_VALUE FROM ");
            sqlBuf.append("t_kpi_kpi kpi LEFT OUTER JOIN (t_kpi_kpi_gather_result result ");
            sqlBuf.append(" INNER JOIN t_com_time_period time ON result.time_period_id = time.id ");
            sqlBuf.append(" ) ON kpi.id = result.kpi_id AND kpi.gather_frequence = time.etype ");
            sqlBuf.append(" WHERE kpi_id IN  (").append(StringUtils.join(kpiIdList, ",")).append(") ");
            sqlBuf.append("AND result.begin_time <= now() AND result.end_time >= now() ) t ON t.id = KPI.id ");
            sqlBuf.append(" LEFT OUTER JOIN ( t_kpi_kpi_gather_result result INNER JOIN t_com_time_period time ON result.time_period_id = time.id ");
            sqlBuf.append(" ) ON kpi.id = result.kpi_id AND kpi.gather_frequence = time.etype WHERE result.TIME_PERIOD_ID = t.PRE_PERIOD_ID ");
            SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(sqlBuf.toString());
            List<Object[]> list = sqlQuery.list();
            for (Object[] objects : list) {
                String resultId = String.valueOf(objects[1]);
                Double preValue = objects[3] == null ? null : Double.valueOf(String.valueOf(objects[3]));
                trendMap.put(resultId, preValue);
            }
        }
        return trendMap;
    }

    /**批量更新指标latest_time_period_id,calculate_time字段
     * @param kpiList 指标对象集合
     */
    @Transactional
    public void batchUpdateKpi(final List<Kpi> kpiList) {
        o_kpiGatherResultDAO.getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "update t_kpi_kpi set latest_time_period_id=?,calculate_time=? where id=?";
                pst = connection.prepareStatement(sql);
                for (Kpi kpi : kpiList) {
                    pst.setObject(1, kpi.getLastTimePeriod() != null ? kpi.getLastTimePeriod().getId() : null);
                    pst.setObject(2, kpi.getCalculatetime());
                    pst.setObject(3, kpi.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);

            }
        });
    }

    /**批量更新采集结果数据
     * @param resultList 采集结果对象集合
     */
    @Transactional
    public void batchUpdateKpiResults(final List<KpiGatherResult> resultList) {
        o_kpiGatherResultDAO.getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "update t_kpi_kpi_gather_result set target_value=?,finish_value=?,assessment_value=?,assessment_status=?,direction=? where id=?";
                pst = connection.prepareStatement(sql);
                for (KpiGatherResult kpiGatherResult : resultList) {
                    pst.setObject(1, kpiGatherResult.getTargetValue());
                    pst.setObject(2, kpiGatherResult.getFinishValue());
                    pst.setObject(3, kpiGatherResult.getAssessmentValue());
                    pst.setObject(4, kpiGatherResult.getAssessmentStatus() != null ? kpiGatherResult.getAssessmentStatus().getId() : null);
                    pst.setObject(5, kpiGatherResult.getDirection() != null ? kpiGatherResult.getDirection().getId() : null);
                    pst.setObject(6, kpiGatherResult.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }

    /**
     * 根据指标采集频率cron表达式计算下次执行时间.
     * 
     * @param kpi
     * @param type
     *            值类型
     * @return Date
     * @throws ParseException
     */
    private Date calculateNextExecuteTime(Kpi kpi, String type) throws ParseException {
        CronTrigger trigger = new CronTrigger();
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

    /**查找指标最后更新时间map集合
     * @param kpiIdList指标id集合
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, TimePeriod> getKpiLastTimePeriodMap(List<String> kpiIdList) {
        Map<String, TimePeriod> lastTimePeriodMap = new HashMap<String, TimePeriod>();
        Date currentDate = new Date();
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi", CriteriaSpecification.LEFT_JOIN);
        criteria.setFetchMode("kpi.createBy", FetchMode.SELECT);
        criteria.setFetchMode("kpi.lastModifyBy", FetchMode.SELECT);
        criteria.setFetchMode("kpi", FetchMode.JOIN);
        criteria.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
        criteria.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        criteria.add(Restrictions.in("kpi.id", kpiIdList));
        criteria.add(Restrictions.ge("timePeriod.endTime", currentDate));
        criteria.add(Restrictions.le("timePeriod.startTime", currentDate));
        List<KpiGatherResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            for (KpiGatherResult kpiGatherResult : list) {
                String kpiId = kpiGatherResult.getKpi().getId();
                lastTimePeriodMap.put(kpiId, kpiGatherResult.getTimePeriod());
            }
        }
        return lastTimePeriodMap;
    }

    /**
     * @param kpiIdList 指标id集合
     * @param type 告警或是预警 fcAlarmPlan:预警方案 , rAlarmPlan:告警方案
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, AlarmPlan> findAlarmPlanByKpiId(List<String> kpiIdList, String type) {
        Map<String, AlarmPlan> alarmPlanMap = new HashMap<String, AlarmPlan>();
        String alarmType = "fcAlarmPlan";
        if ("forecast".equals(type)) {
            alarmType = "fcAlarmPlan";
        }
        else if ("report".equals(type)) {
            alarmType = "rAlarmPlan";
        }
        Date currentDate = new Date();
        Criteria criteria = o_KpiRelaAlarmDAO.createCriteria();
        criteria.createAlias("rAlarmPlan", "rAlarmPlan");
        criteria.setFetchMode("kpi", FetchMode.JOIN);
        criteria.setFetchMode("rAlarmPlan.alarmRegions", FetchMode.JOIN);
        criteria.add(Restrictions.in("kpi.id", kpiIdList));
        criteria.add(Restrictions.le("startDate", currentDate));
        criteria.setFetchMode(alarmType, JOIN);
        criteria.addOrder(Order.asc("startDate"));
        List<KpiRelaAlarm> kpiRelaAlarms = criteria.list();
        if (null != kpiRelaAlarms) {
            for (int i = 0; i < kpiRelaAlarms.size(); i++) {
                KpiRelaAlarm kpiRelaAlarm = kpiRelaAlarms.get(i);
                String kpiId = kpiRelaAlarm.getKpi().getId();
                if ("forecast".equals(type)) {
                    alarmPlanMap.put(kpiId, kpiRelaAlarm.getFcAlarmPlan());
                }
                else if ("report".equals(type)) {
                    alarmPlanMap.put(kpiId, kpiRelaAlarm.getrAlarmPlan());
                }
            }

        }
        return alarmPlanMap;
    }

    /**
     * 清理缓存
     */
    private void clearSession() {
        o_kpiGatherResultDAO.getSession().clear();
    }

}
