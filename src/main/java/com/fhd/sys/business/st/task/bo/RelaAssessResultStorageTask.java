package com.fhd.sys.business.st.task.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.comm.business.formula.FormulaCalculateBO;
import com.fhd.comm.business.formula.FormulaLogBO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;

@Service
public class RelaAssessResultStorageTask {

    @Autowired
    private CategoryBO o_categoryBO;

    @Autowired
    private StrategyMapBO o_strategyMapBO;

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;

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
    private KpiBO o_kpiBO;

    private Map<String, TimePeriod> timePeriodMap;//时间MapAll

    private static final String categoryType = "sc";

    private static final String strategyMapType = "str";

    private static final Log logger = LogFactory.getLog(RelaAssessResultStorageTask.class);

    /**
     * 计算记分卡评估值存储服务
     * 
     * @author 金鹏祥
     * @return
    */
    @Transactional
    public boolean categoryAssessedService() {
        this.assessedService(new Category());
        return true;
    }

    /**
     * 计算战略目标评估值存储服务
     * 
     * @author 金鹏祥
     * @return
    */
    @Transactional
    public boolean strategyMapAssessedService() {
        this.assessedService(new StrategyMap());
        return true;
    }

    /**
     * 评估值处理服务
     * 
     * @author 金鹏祥
     * @param obj
     * @return
    */
    private void assessedService(Object obj) {
        String ret = "";
        Double assessValue = 0.0d;
        AlarmPlan alarmPlan = null;
        timePeriodMap = o_kpiGatherResultBO.findTimePeriodAllMap();
        FormulaLog formulaLog = new FormulaLog();
        TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome("");
        Date currentDate = new Date();
        List<FormulaLog> formulaLogList = new ArrayList<FormulaLog>();
        Map<String, Boolean> logInfoMap = new HashMap<String, Boolean>();
        if (obj instanceof Category) {
            List<String> categoryIdList = new ArrayList<String>();
            Map<String, Boolean> calcMap = new HashMap<String, Boolean>();
            List<Category> categoryListAll = o_categoryBO.findCalcCategoryAll();
            for (Category category : categoryListAll) {
                categoryIdList.add(category.getId());
            }
            Map<String, AlarmPlan> alarmMap = null;
            if (categoryIdList.size() > 0) {
                alarmMap = o_alarmPlanBO.findScAlarmPlanByKpiId(categoryIdList, Contents.ALARMPLAN_REPORT);
            }
            for (int j = 0; j < new ArrayList<Category>(categoryListAll).size(); j++) {
                for (Category category : new ArrayList<Category>(categoryListAll)) {
                    boolean computeFlag = false;
                    //告警方案
                    alarmPlan = alarmMap.get(category.getId());
                    try {
                        RelaAssessResult currentRelaAssessResult = o_relaAssessResultBO.findCurrentRelaAssessResultByType(category.getId(), "sc", currentDate);
                        if (null != currentRelaAssessResult) {

                            logger.info(category.getAssessmentFormula() + "======================================");
                            logger.info("=================name======" + category.getName());
                            //评估值计算公式
                            if (StringUtils.isNotBlank(category.getAssessmentFormula())) {//评估值公式不为空
                                ret = o_formulaCalculateBO.calculate(category.getId(), category.getName(), "category", category.getAssessmentFormula(),
                                        currentRelaAssessResult.getTimePeriod().getId(), category.getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                    //返回结果是double，计算正确
                                    computeFlag = true;
                                    assessValue = NumberUtils.toDouble(ret);
                                    this.saveRelaAssessResult(currentRelaAssessResult, category.getId(), category.getName(), assessValue, category.getId(), obj, category.getCompany().getId(),
                                            alarmPlan);
                                    //保存最后更新时间
                                    TimePeriod tPeriod = currentRelaAssessResult.getTimePeriod();
                                    category.setTimePeriod(tPeriod);
                                    o_categoryBO.mergeCategory(category);

                                    formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, category.getId(), category.getName(), "category", Contents.SC_ASSESSMENT_VALUE_FORMULA,
                                            category.getAssessmentFormula(), "success", ret, timePeriod, null));
                                }
                                else {
                                    computeFlag = false;
                                    //返回结果是字符串描述，计算错误
                                    String scFormulaType = new StringBuffer().append(category.getId()).append("-----").append(Contents.SC_ASSESSMENT_VALUE_FORMULA).append("-----").append("failure")
                                            .toString();
                                    if (!logInfoMap.containsKey(scFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, category.getId(), category.getName(), "category", Contents.SC_ASSESSMENT_VALUE_FORMULA,
                                                category.getAssessmentFormula(), "failure", ret, timePeriod, null));
                                        logInfoMap.put(scFormulaType, true);
                                    }
                                }
                            }
                            else {//评估值公式为空
                                if (null != currentRelaAssessResult.getAssessmentValue()) {//评估值不为空
                                    ret = currentRelaAssessResult.getAssessmentValue().toString();
                                    if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                        //返回结果是double，计算正确
                                        assessValue = NumberUtils.toDouble(ret);
                                        this.saveRelaAssessResult(currentRelaAssessResult, category.getId(), category.getName(), assessValue, category.getId(), obj, category.getCompany().getId(),
                                                alarmPlan);
                                    }
                                }

                            }
                        }
                    }
                    catch (Exception e) {
                        logger.info("记分卡计算异常信息:" + e.toString());
                        continue;
                    }

                    if (computeFlag) {
                        calcMap.put(category.getId(), true);
                        categoryListAll.remove(category);
                    }
                    else {
                        calcMap.put(category.getId(), false);
                    }

                }
            }

        }
        else if (obj instanceof StrategyMap) {
            List<String> strategyIdList = new ArrayList<String>();
            Map<String, Boolean> calcMap = new HashMap<String, Boolean>();
            List<StrategyMap> strategyMapListAll = o_strategyMapBO.findCalcStrategyMapAll();
            for (StrategyMap strategyMap : strategyMapListAll) {
                strategyIdList.add(strategyMap.getId());
            }
            Map<String, AlarmPlan> alarmMap = null;
            if (strategyIdList.size() > 0) {
                alarmMap = o_alarmPlanBO.findSmAlarmPlanByKpiId(strategyIdList, Contents.ALARMPLAN_REPORT);
            }
            for (int k = 0; k < new ArrayList<StrategyMap>(strategyMapListAll).size(); k++) {
                for (StrategyMap strategyMap : new ArrayList<StrategyMap>(strategyMapListAll)) {
                    boolean computeFlag = false;
                    //告警方案
                    alarmPlan = alarmMap.get(strategyMap.getId());
                    try {
                        //评估值计算公式
                        RelaAssessResult currentRelaAssessResult = o_relaAssessResultBO.findCurrentRelaAssessResultByType(strategyMap.getId(), "str", currentDate);
                        if (null != currentRelaAssessResult) {
                            if (StringUtils.isNotBlank(strategyMap.getAssessmentFormula())) {//评估值公式不为空
                                logger.info(strategyMap.getAssessmentFormula() + "======================================");
                                ret = o_formulaCalculateBO.calculate(strategyMap.getId(), strategyMap.getName(), "strategy", strategyMap.getAssessmentFormula(), currentRelaAssessResult
                                        .getTimePeriod().getId(), strategyMap.getCompany().getId());
                                if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                    computeFlag = true;
                                    //返回结果是double，计算正确
                                    assessValue = Double.valueOf(ret);

                                    this.saveRelaAssessResult(currentRelaAssessResult, strategyMap.getId(), strategyMap.getName(), assessValue, strategyMap.getId(), obj, strategyMap.getCompany()
                                            .getId(), alarmPlan);
                                    //保存最后更新时间
                                    TimePeriod tPeriod = currentRelaAssessResult.getTimePeriod();
                                    strategyMap.setTimePeriod(tPeriod);
                                    o_strategyMapBO.mergeStrategyMap(strategyMap);

                                    formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, strategyMap.getId(), strategyMap.getName(), "strategy", Contents.SM_ASSESSMENT_VALUE_FORMULA,
                                            strategyMap.getAssessmentFormula(), "success", ret, timePeriod, null));
                                }
                                else {
                                    //返回结果是字符串描述，计算错误
                                    computeFlag = false;
                                    String smFormulaType = new StringBuffer().append(strategyMap.getId()).append("-----").append(Contents.SM_ASSESSMENT_VALUE_FORMULA).append("-----")
                                            .append("failure").toString();
                                    if (!logInfoMap.containsKey(smFormulaType)) {
                                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, strategyMap.getId(), strategyMap.getName(), "strategy", Contents.SM_ASSESSMENT_VALUE_FORMULA,
                                                strategyMap.getAssessmentFormula(), "failure", ret, timePeriod, null));
                                        logInfoMap.put(smFormulaType, true);
                                    }
                                }
                            }
                            else {//评估值公式为空
                                if (null != currentRelaAssessResult.getAssessmentValue()) {//评估值不为空
                                    ret = currentRelaAssessResult.getAssessmentValue().toString();
                                    if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                                        assessValue = Double.valueOf(ret);
                                        this.saveRelaAssessResult(currentRelaAssessResult, strategyMap.getId(), strategyMap.getName(), assessValue, strategyMap.getId(), obj, strategyMap.getCompany()
                                                .getId(), alarmPlan);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        logger.error("目标计算异常信息:" + e.toString());
                        continue;
                    }

                    if (computeFlag) {
                        calcMap.put(strategyMap.getId(), true);
                        strategyMapListAll.remove(strategyMap);
                    }
                    else {
                        calcMap.put(strategyMap.getId(), false);
                    }

                }
            }

        }
        //批量插入公式计算日志
        o_formulaLogBO.batchSaveFormulaLog(formulaLogList);
    }

    /**
     * 存储评估值及对应类型
     * 
     * @author 金鹏祥
     * @param code
     * @param name
     * @param assessValue
    */
    private void saveRelaAssessResult(RelaAssessResult assessResult, String code, String name, Double assessValue, String id, Object obj, String companyId, AlarmPlan alarmPlan) {
        DictEntry trendDict = null;
        RelaAssessResult relaAssessResult = new RelaAssessResult();
        DictEntry alarmIcon = null;
        TimePeriod timePeriod = assessResult.getTimePeriod();

        relaAssessResult.setId(assessResult.getId());
        relaAssessResult.setObjectId(code);
        relaAssessResult.setObjectName(name);
        relaAssessResult.setTimePeriod(timePeriod);
        relaAssessResult.setBeginTime(timePeriodMap.get(relaAssessResult.getTimePeriod().getId()).getStartTime());
        relaAssessResult.setEndTime(timePeriodMap.get(relaAssessResult.getTimePeriod().getId()).getEndTime());
        relaAssessResult.setAssessmentValue(assessValue);
        trendDict = this.o_relaAssessResultBO.calculateTrendByAssessmentValue(assessResult);
        relaAssessResult.setTrend(trendDict);
        if (obj instanceof Category) {
            relaAssessResult.setDataType(categoryType);
            if (alarmPlan != null) {
                relaAssessResult.setAssessmentAlarmPlan(alarmPlan);
                alarmIcon = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessValue);
                relaAssessResult.setAssessmentStatus(alarmIcon);
            }
        }
        else if (obj instanceof StrategyMap) {
            relaAssessResult.setDataType(strategyMapType);
            if (alarmPlan != null) {
                relaAssessResult.setAssessmentAlarmPlan(alarmPlan);
                alarmIcon = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessValue);
                relaAssessResult.setAssessmentStatus(alarmIcon);
            }
        }
        o_relaAssessResultBO.saveRelaAssessResult(relaAssessResult);
    }

}
