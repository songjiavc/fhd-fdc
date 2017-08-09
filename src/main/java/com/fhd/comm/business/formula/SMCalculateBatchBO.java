package com.fhd.comm.business.formula;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.dao.kpi.RelaAssessResultDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.RelaAssessResultDTO;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sys.business.dic.DictBO;

@Service
@SuppressWarnings("unchecked")
public class SMCalculateBatchBO {

    @Autowired
    private StrategyMapBO o_strategyMapBO;

    @Autowired
    private RelaAssessResultDAO o_relaAssessResultDAO;

    @Autowired
    private CalculateFormulaBO o_calculateFormulaBO;

    @Autowired
    private FormulaCalculateBO o_formulaCalculateBO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;

    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private DictBO o_DictBO;

    @Autowired
    private FormulaLogBO o_formulaLogBO;

    @Autowired
    private TimePeriodBO o_timePeriodBO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;

    private static final Log logger = LogFactory.getLog(SMCalculateBatchBO.class);

    /**根据公式计算目标分值
     * @return
     */
    @Transactional
    public Boolean calculateByFormula() {

        List<StrategyMap> strategyMapList = findAllStrategy();
        Long l1 = System.currentTimeMillis();
        autoCalc(strategyMapList);
        Long l2 = System.currentTimeMillis();
        System.out.println("SMCalculateBatchBO 战略目标计算时间[" + (l2 - l1) + "]");

        return true;

    }

    /**计算目标分值
     * @param strategyMapList 目标对象集合
     */
    @Transactional
    public void autoCalc(List<StrategyMap> strategyMapList) {
        Date currentDate = new Date();
        List<String> stragegyIdList = new ArrayList<String>();
        List<String> stragegyCodeList = new ArrayList<String>();
        FormulaLog formulaLog = null;
        List<FormulaLog> formulaLogList = new ArrayList<FormulaLog>();

        for (StrategyMap strategyMap : strategyMapList) {
            stragegyIdList.add(strategyMap.getId());
            if (StringUtils.isNotBlank(strategyMap.getCode())) {
                stragegyCodeList.add(strategyMap.getCode());
            }
        }

        Map<String, Boolean> logInfoMap = new HashMap<String, Boolean>();

        //告警方案map
        Map<String, AlarmPlan> alarmMap = null;
        if (stragegyIdList.size() > 0) {
            alarmMap = o_alarmPlanBO.findSmAlarmPlanByKpiId(stragegyIdList, Contents.ALARMPLAN_REPORT);
        }

        DictEntry trendDict = null;
        DictEntry upDict = o_DictBO.findDictEntryById("up");
        DictEntry flatDict = o_DictBO.findDictEntryById("flat");
        DictEntry downDict = o_DictBO.findDictEntryById("down");

        Map<String, RelaAssessResult> resultMap = findCurrentRelaAssessResultListByType(stragegyIdList, "str", currentDate);

        Map<String, List<Object[]>> strategyMapRelaKpiGatherResultMap = findStrategyRelaKpiGatherResultMap();

        Map<String, List<Object[]>> strategyMapRelaSubCategoryResultMap = findStrategyRelaSubCategoryResultMap();

        Map<String, Object> valueMapObj = new HashMap<String, Object>();

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
        //记分卡是否是通过其它记分卡配置
        String isSmSmFormulaNameConfig = "false";
        if (resourceBundle.containsKey("isSmSmFormulaNameConfig")) {
            isSmSmFormulaNameConfig = resourceBundle.getString("isSmSmFormulaNameConfig");
        }
        String isSmKpiFormulaNameConfig = "false";
        if (resourceBundle.containsKey("isSmKpiFormulaNameConfig")) {
            isSmKpiFormulaNameConfig = resourceBundle.getString("isSmKpiFormulaNameConfig");
        }

        Map<String, List<RelaAssessResultDTO>> allResultMap = new HashMap<String, List<RelaAssessResultDTO>>();
        if (Boolean.valueOf(isSmSmFormulaNameConfig)) {
            TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome(Contents.FREQUECY_MONTH);
            if (null != timePeriod) {
                allResultMap = o_relaAssessResultBO.findAssessResultListByCodes(stragegyCodeList, "str", timePeriod.getId());
            }
        }

        Map<String, KpiGatherResult> allKpiResultMap = new HashMap<String, KpiGatherResult>();
        if (Boolean.valueOf(isSmKpiFormulaNameConfig)) {
            allKpiResultMap = o_kpiGatherResultBO.findCurrentKpiGatherResultMapByKpiIds(null, "");
            valueMapObj.put("allKpiResultList", new ArrayList<KpiGatherResult>(allKpiResultMap.values()));
        }

        List<StrategyMap> calcStrategyMapList = new ArrayList<StrategyMap>();

        List<RelaAssessResult> calcGatherResultList = new ArrayList<RelaAssessResult>();

        for (int k = 0; k < new ArrayList<StrategyMap>(strategyMapList).size(); k++) {

            for (StrategyMap strategyMap : new ArrayList<StrategyMap>(strategyMapList)) {
                try {
                    formulaLog = new FormulaLog();
                    String strategyMapId = strategyMap.getId();
                    String strategyMapName = strategyMap.getName();
                    String companyId = strategyMap.getCompany().getId();
                    RelaAssessResult currentResult = resultMap.get(strategyMapId);
                    if (currentResult == null) {
                        continue;
                    }
                    List<Object[]> strategyMapRelaKpiResultList = strategyMapRelaKpiGatherResultMap.get(strategyMapId);
                    List<Object[]> strategyMapRelaSubCategoryResultList = strategyMapRelaSubCategoryResultMap.get(strategyMapId);
                    valueMapObj.put("strategyMapRelaKpiResultList", strategyMapRelaKpiResultList);
                    valueMapObj.put("strategyMapRelaSubCategoryResultList", strategyMapRelaSubCategoryResultList);

                    List<RelaAssessResultDTO> allStrategyGatherResult = null;
                    if (Boolean.valueOf(isSmSmFormulaNameConfig)) {
                        allStrategyGatherResult = allResultMap.get(companyId);
                        valueMapObj.put("allStrategyGatherResult", allStrategyGatherResult);
                    }

                    TimePeriod timePeriod = currentResult.getTimePeriod();
                    String timePeriodId = timePeriod.getId();
                    //记分卡评估值公式
                    String formula = strategyMap.getAssessmentFormula();
                    String ret = o_calculateFormulaBO.calculate(strategyMapId, strategyMapName, "strategy", formula, timePeriodId, companyId, valueMapObj);
                    if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                        Double assessValue = NumberUtils.toDouble(ret);
                        currentResult.setAssessmentValue(assessValue);
                        AlarmPlan alarmPlan = alarmMap.get(strategyMapId);
                        if (null != alarmPlan && null != assessValue) {//告警方案不为空
                            DictEntry assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessValue);
                            currentResult.setAssessmentStatus(assessmentStatus);
                        }

                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, strategyMap.getId(), strategyMap.getName(), "strategy", Contents.SM_ASSESSMENT_VALUE_FORMULA,
                                strategyMap.getAssessmentFormula(), "success", ret, timePeriod, null));

                        calcGatherResultList.add(currentResult);

                        strategyMapList.remove(strategyMap);

                        strategyMap.setTimePeriod(currentResult.getTimePeriod());

                        calcStrategyMapList.add(strategyMap);

                        String parentId = "";
                        if (null != strategyMap.getParent()) {
                            parentId = strategyMap.getParent().getId();
                        }

                        if (StringUtils.isNotBlank(parentId)) {
                            List<Object[]> subCategoryResultList = strategyMapRelaSubCategoryResultMap.get(parentId);
                            for (Object[] objects : subCategoryResultList) {
                                String smId = String.valueOf(objects[2]);
                                if (smId.equals(strategyMapId)) {
                                    objects[1] = assessValue;
                                }
                            }
                        }

                        if (null != allStrategyGatherResult) {
                            for (RelaAssessResultDTO relaAssessResultDTO : allStrategyGatherResult) {
                                if (strategyMapId.equals(relaAssessResultDTO.getObjectId())) {
                                    relaAssessResultDTO.setAssessmentValue(assessValue);
                                }
                            }
                        }

                    }
                    else {
                        String smFormulaType = new StringBuffer().append(strategyMap.getId()).append("-----").append(Contents.SM_ASSESSMENT_VALUE_FORMULA).append("-----").append("failure").toString();
                        if (!logInfoMap.containsKey(smFormulaType)) {
                            formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, strategyMap.getId(), strategyMap.getName(), "strategy", Contents.SM_ASSESSMENT_VALUE_FORMULA,
                                    strategyMap.getAssessmentFormula(), "failure", ret, timePeriod, null));
                            logInfoMap.put(smFormulaType, true);
                        }
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                    logger.error("计算战略目标异常:" + e.toString());
                    continue;
                }

            }

        }

        Map<String, Double> trendMap = findTrend(calcGatherResultList);
        for (RelaAssessResult gatherResult : calcGatherResultList) {
            String objectId = gatherResult.getObjectId();
            Double currValue = gatherResult.getAssessmentValue();
            Double preValue = trendMap.get(objectId);
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
            gatherResult.setTrend(trendDict);
        }

        //批量更新采集数据
        batchUpdateGatherResultList(calcGatherResultList);
        //批量更新记分卡lasttimeperiod字段
        batchUpdateStrategyMapList(calcStrategyMapList);
        //批量插入日志
        o_formulaLogBO.batchSaveFormulaLog(formulaLogList);
    }

    /**查询目标采集结果趋势
     * @param calcGatherResultList 目标采集结果对象集合
     * @return
     */
    public Map<String, Double> findTrend(List<RelaAssessResult> calcGatherResultList) {
        Map<String, Double> trendMap = new HashMap<String, Double>();
        if (null != calcGatherResultList && calcGatherResultList.size() > 0) {
            List<String> idList = new ArrayList<String>();
            for (RelaAssessResult gatherResult : calcGatherResultList) {
                StringBuffer idBuf = new StringBuffer();
                idBuf.append("'").append(gatherResult.getObjectId()).append("'");
                idList.add(idBuf.toString());
            }

            StringBuffer sqlBuf = new StringBuffer();
            sqlBuf.append(" select tab.object_id,tab.current_value,r.assessment_value pre_value,tab.object_name  from t_kpi_sm_assess_result r  ");
            sqlBuf.append(" INNER JOIN ( select object_id,assessment_value current_value ,object_name,time_period_id ,time.PRE_PERIOD_ID from t_kpi_sm_assess_result result ");
            sqlBuf.append(" INNER JOIN t_com_time_period time ON result.time_period_id = time.id where object_id in (").append(StringUtils.join(idList, ",")).append(") ");
            sqlBuf.append(" and result.begin_time <=now() and result.end_time >=now() ").append(" ) tab on tab.object_id = r.object_id  where r.TIME_PERIOD_ID=tab.PRE_PERIOD_ID ");
            SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(sqlBuf.toString());
            List<Object[]> list = sqlQuery.list();
            for (Object[] objects : list) {
                String objectId = String.valueOf(objects[0]);
                Double preValue = objects[2] == null ? null : Double.valueOf(String.valueOf(objects[2]));
                trendMap.put(objectId, preValue);
            }
        }
        return trendMap;
    }

    /**批量更新目标最后更新时间字段
     * @param calcStrategyMapList 目标对象集合
     */
    @Transactional
    public void batchUpdateStrategyMapList(final List<StrategyMap> calcStrategyMapList) {
        o_relaAssessResultDAO.getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                connection.setAutoCommit(false);
                String sql = "update t_com_category set LATEST_TIME_PERIOD_ID=? where id=?";
                PreparedStatement pst = connection.prepareStatement(sql);
                for (StrategyMap strategyMap : calcStrategyMapList) {
                    pst.setObject(1, strategyMap.getTimePeriod() == null ? null : strategyMap.getTimePeriod().getId());
                    pst.setObject(2, strategyMap.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }

    /**批量更新目标采集结果数据
     * @param calcGatherResultList 目标采集结果对象集合
     */
    @Transactional
    public void batchUpdateGatherResultList(final List<RelaAssessResult> calcGatherResultList) {
        o_relaAssessResultDAO.getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                connection.setAutoCommit(false);
                String sql = "update t_kpi_sm_assess_result set assessment_value=?,assessment_status=?,trend=? where id=?";
                PreparedStatement pst = connection.prepareStatement(sql);
                for (RelaAssessResult gatherResultDTO : calcGatherResultList) {
                    pst.setObject(1, gatherResultDTO.getAssessmentValue());
                    pst.setObject(2, gatherResultDTO.getAssessmentStatus() == null ? null : gatherResultDTO.getAssessmentStatus().getId());
                    pst.setObject(3, gatherResultDTO.getTrend() == null ? null : gatherResultDTO.getTrend().getId());
                    pst.setObject(4, gatherResultDTO.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }

    /**查询所有目标所对应的采集数据集合 key:目标id value:采集结果数据
     * @return
     */
    public Map<String, List<Object[]>> findStrategyRelaKpiGatherResultMap() {
        StringBuffer sqlBuff = new StringBuffer();
        Map<String, List<Object[]>> resultMap = new HashMap<String, List<Object[]>>();
        sqlBuff.append(" SELECT k.kpi_name, r.assessment_value,r.finish_value, c.STRATEGY_MAP_NAME,k.gather_frequence,k.id kid,c.id cid ,r.id krid,p.id tid,p.start_time,p.end_time,kc.eweight ");
        sqlBuff.append(" FROM t_kpi_kpi_gather_result r, t_kpi_kpi k,t_kpi_sm_rela_kpi kc,t_kpi_strategy_map c,t_com_time_period p ");
        sqlBuff.append(" WHERE r.kpi_id = k.id AND k.id = kc.kpi_id AND kc.STRATEGY_MAP_ID = c.id AND r.time_period_id = p.id AND k.gather_frequence = p.etype ");
        sqlBuff.append(" AND k.delete_status = 1 AND k.is_kpi_category = 'KPI' AND r.begin_time <= DATE_FORMAT(now(), '%Y-%m-%d') ");
        sqlBuff.append(" AND r.end_time >= DATE_FORMAT(now(), '%Y-%m-%d') ");

        SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(sqlBuff.toString());
        List<Object[]> list = sqlQuery.list();
        for (Object[] objs : list) {
            String categoryId = String.valueOf(objs[6]);
            if (!resultMap.containsKey(categoryId)) {
                List<Object[]> objList = new ArrayList<Object[]>();
                resultMap.put(categoryId, objList);
            }
            List<Object[]> resultList = resultMap.get(categoryId);
            resultList.add(objs);
        }

        return resultMap;
    }

    /**查询所有目标所对应下级目标的采集数据集合 key:目标id value:目标所对应采集结果数据集合
     * @return
     */
    public Map<String, List<Object[]>> findStrategyRelaSubCategoryResultMap() {
        StringBuffer sqlBuff = new StringBuffer();
        Map<String, List<Object[]>> resultMap = new HashMap<String, List<Object[]>>();
        sqlBuff.append(" SELECT c.STRATEGY_MAP_NAME, r.assessment_value, c.id cid,pc.id pcid,r.id rid,p.id tid,");
        sqlBuff.append(" p.START_TIME, p.END_TIME FROM t_kpi_sm_assess_result r, t_kpi_strategy_map c, t_kpi_strategy_map pc,t_com_time_period p");
        sqlBuff.append(" WHERE r.OBJECT_ID = c.id AND r.TIME_PERIOD_ID = p.ID AND r.DATA_TYPE = 'str' and c.PARENT_ID=pc.id ");
        sqlBuff.append(" AND c.delete_status = 1 AND r.begin_time <= DATE_FORMAT(now(), '%Y-%m-%d') AND r.end_time >= DATE_FORMAT(now(), '%Y-%m-%d') ");
        SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(sqlBuff.toString());
        List<Object[]> list = sqlQuery.list();
        for (Object[] objs : list) {
            String pcategoryId = String.valueOf(objs[3]);
            if (!resultMap.containsKey(pcategoryId)) {
                List<Object[]> objList = new ArrayList<Object[]>();
                resultMap.put(pcategoryId, objList);
            }
            List<Object[]> resultList = resultMap.get(pcategoryId);
            resultList.add(objs);
        }

        return resultMap;

    }

    /**查询所有目标对象集合
     * @return
     */
    public List<StrategyMap> findAllStrategy() {
        List<StrategyMap> stragegyListAll = o_strategyMapBO.findDetachedAllStrategyList();
        return stragegyListAll;
    }

    /***查询目标采集结果对象集合
     * @param objectIdList 目标id集合
     * @param type
     * @param date 当前时间
     * @return
     */
    public Map<String, RelaAssessResult> findCurrentRelaAssessResultListByType(List<String> objectIdList, String type, Date date) {
        Map<String, RelaAssessResult> dataMap = new HashMap<String, RelaAssessResult>();
        DetachedCriteria criteria = DetachedCriteria.forClass(RelaAssessResult.class);
        criteria.add(Restrictions.eq("dataType", type));
        criteria.add(Restrictions.le("beginTime", date));
        criteria.add(Restrictions.ge("endTime", date));
        criteria.add(Restrictions.in("objectId", objectIdList));
        List<RelaAssessResult> list = criteria.getExecutableCriteria(o_relaAssessResultDAO.getSession()).list();
        for (RelaAssessResult relaAssessResult : list) {
            dataMap.put(relaAssessResult.getObjectId(), relaAssessResult);
        }
        return dataMap;
    }

}
