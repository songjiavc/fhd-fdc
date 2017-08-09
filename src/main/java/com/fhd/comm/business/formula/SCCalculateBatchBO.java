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
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.dao.kpi.RelaAssessResultDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.RelaAssessResultDTO;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sys.business.dic.DictBO;

@Service
@SuppressWarnings("unchecked")
public class SCCalculateBatchBO {

    @Autowired
    private CategoryBO o_categoryBO;

    @Autowired
    private TimePeriodBO o_timePeriodBO;

    @Autowired
    private RelaAssessResultDAO o_relaAssessResultDAO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;

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

    private static final Log logger = LogFactory.getLog(SCCalculateBatchBO.class);

    /**根据公式计算记分卡分值
     * @return
     */
    @Transactional
    public Boolean calculateByFormula() {

        List<Category> categoryList = findAllCategory();
        Long l1 = System.currentTimeMillis();
        autoCalc(categoryList);
        Long l2 = System.currentTimeMillis();
        System.out.println("SCCalculateBatchBO 记分卡公式计算时间[" + (l2 - l1) + "]");
        return true;
    }

    /**计算记分卡分值
     * @param categoryList记分卡对象集合
     */
    @Transactional
    public void autoCalc(List<Category> categoryList) {
        Date currentDate = new Date();
        List<String> categoryIdList = new ArrayList<String>();
        List<String> categoryCodeList = new ArrayList<String>();
        FormulaLog formulaLog = null;
        List<FormulaLog> formulaLogList = new ArrayList<FormulaLog>();

        for (Category category : categoryList) {
            categoryIdList.add(category.getId());
            if (StringUtils.isNotBlank(category.getCode())) {
                categoryCodeList.add(category.getCode());
            }
        }

        Map<String, Boolean> logInfoMap = new HashMap<String, Boolean>();

        //告警方案map
        Map<String, AlarmPlan> alarmMap = null;
        if (categoryIdList.size() > 0) {
            alarmMap = o_alarmPlanBO.findScAlarmPlanByKpiId(categoryIdList, Contents.ALARMPLAN_REPORT);
        }

        DictEntry trendDict = null;
        DictEntry upDict = o_DictBO.findDictEntryById("up");
        DictEntry flatDict = o_DictBO.findDictEntryById("flat");
        DictEntry downDict = o_DictBO.findDictEntryById("down");

        Map<String, RelaAssessResult> resultMap = findCurrentRelaAssessResultListByType(categoryIdList, "sc", currentDate);

        Map<String, List<Object[]>> categoryRelaKpiGatherResultMap = findCategoryRelaKpiGatherResultMap();

        Map<String, List<Object[]>> categoryRelaSubCategoryResultMap = findCategoryRelaSubCategoryResultMap();

        Map<String, Object> valueMapObj = new HashMap<String, Object>();

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
        //记分卡是否是通过其它记分卡配置
        String isScScFormulaNameConfig = "false";
        if (resourceBundle.containsKey("isScScFormulaNameConfig")) {
            isScScFormulaNameConfig = resourceBundle.getString("isScScFormulaNameConfig");
        }
        //记分卡是否是通过其它指标配置
        String isScKpiFormulaNameConfig = "false";
        if (resourceBundle.containsKey("isScKpiFormulaNameConfig")) {
            isScKpiFormulaNameConfig = resourceBundle.getString("isScKpiFormulaNameConfig");
        }

        Map<String, List<RelaAssessResultDTO>> allResultMap = new HashMap<String, List<RelaAssessResultDTO>>();
        if (Boolean.valueOf(isScScFormulaNameConfig)) {
            TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome(Contents.FREQUECY_MONTH);
            if (null != timePeriod) {
                allResultMap = o_relaAssessResultBO.findAssessResultListByCodes(categoryCodeList, "sc", timePeriod.getId());
            }
        }

        Map<String, KpiGatherResult> allKpiResultMap = new HashMap<String, KpiGatherResult>();
        if (Boolean.valueOf(isScKpiFormulaNameConfig)) {
            allKpiResultMap = o_kpiGatherResultBO.findCurrentKpiGatherResultMapByKpiIds(null, "");
            valueMapObj.put("allKpiResultList", new ArrayList<KpiGatherResult>(allKpiResultMap.values()));
        }

        List<Category> calcCategoryList = new ArrayList<Category>();

        List<RelaAssessResult> calcGatherResultList = new ArrayList<RelaAssessResult>();

        //查询所有记分卡当期的采集数据
        for (int k = 0; k < new ArrayList<Category>(categoryList).size(); k++) {

            for (Category category : new ArrayList<Category>(categoryList)) {
                try {
                    formulaLog = new FormulaLog();

                    String categoryId = category.getId();
                    String categoryName = category.getName();
                    String companyId = category.getCompany().getId();
                    RelaAssessResult currentResult = resultMap.get(categoryId);
                    if (currentResult == null) {
                        continue;
                    }

                    List<Object[]> categoryRelaKpiResultList = categoryRelaKpiGatherResultMap.get(categoryId);
                    List<Object[]> categoryRelaSubCategoryResultList = categoryRelaSubCategoryResultMap.get(categoryId);
                    valueMapObj.put("categoryRelaKpiResultList", categoryRelaKpiResultList);
                    valueMapObj.put("categoryRelaSubCategoryResultList", categoryRelaSubCategoryResultList);

                    List<RelaAssessResultDTO> allCategoryGatherResult = null;
                    if (Boolean.valueOf(isScScFormulaNameConfig)) {
                        allCategoryGatherResult = allResultMap.get(companyId);
                        valueMapObj.put("allCategoryGatherResult", allCategoryGatherResult);
                    }
                    TimePeriod timePeriod = currentResult.getTimePeriod();
                    String timePeriodId = timePeriod.getId();
                    //记分卡评估值公式
                    String formula = category.getAssessmentFormula();
                    String ret = o_calculateFormulaBO.calculate(categoryId, categoryName, "category", formula, timePeriodId, companyId, valueMapObj);
                    if (o_formulaCalculateBO.isDoubleOrInteger(ret)) {
                        Double assessValue = NumberUtils.toDouble(ret);
                        currentResult.setAssessmentValue(assessValue);
                        AlarmPlan alarmPlan = alarmMap.get(categoryId);
                        if (null != alarmPlan && null != assessValue) {//告警方案不为空
                            DictEntry assessmentStatus = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessValue);
                            currentResult.setAssessmentStatus(assessmentStatus);
                        }

                        formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, category.getId(), category.getName(), "category", Contents.SC_ASSESSMENT_VALUE_FORMULA,
                                category.getAssessmentFormula(), "success", ret, timePeriod, null));

                        calcGatherResultList.add(currentResult);

                        categoryList.remove(category);

                        category.setTimePeriod(currentResult.getTimePeriod());

                        calcCategoryList.add(category);

                        String parentId = null == category.getParent() ? "" : category.getParent().getId();
                        if (StringUtils.isNotBlank(parentId)) {
                            List<Object[]> subCategoryResultList = categoryRelaSubCategoryResultMap.get(parentId);
                            for (Object[] objects : subCategoryResultList) {
                                String scId = String.valueOf(objects[2]);
                                if (scId.equals(categoryId)) {
                                    objects[1] = assessValue;
                                }
                            }
                        }

                        if (null != allCategoryGatherResult) {
                            for (RelaAssessResultDTO relaAssessResultDTO : allCategoryGatherResult) {
                                if (categoryId.equals(relaAssessResultDTO.getObjectId())) {
                                    relaAssessResultDTO.setAssessmentValue(assessValue);
                                }
                            }
                        }

                    }
                    else {
                        String scFormulaType = new StringBuffer().append(category.getId()).append("-----").append(Contents.SC_ASSESSMENT_VALUE_FORMULA).append("-----").append("failure").toString();
                        if (!logInfoMap.containsKey(scFormulaType)) {
                            formulaLogList.add(o_formulaLogBO.packageFormulaLog(formulaLog, category.getId(), category.getName(), "category", Contents.SC_ASSESSMENT_VALUE_FORMULA,
                                    category.getAssessmentFormula(), "failure", ret, timePeriod, null));
                            logInfoMap.put(scFormulaType, true);
                        }
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                    logger.error("计算记分卡异常:" + e.toString());
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
        batchUpdateCategoryList(calcCategoryList);
        //批量插入日志
        o_formulaLogBO.batchSaveFormulaLog(formulaLogList);

    }

    /**查询记分卡采集结果趋势
     * @param calcGatherResultList 记分卡采集结果对象集合
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

    /**批量更新记分卡最后更新时间字段
     * @param calcCategoryList 记分卡对象集合
     */
    @Transactional
    public void batchUpdateCategoryList(final List<Category> calcCategoryList) {
        o_relaAssessResultDAO.getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                connection.setAutoCommit(false);
                String sql = "update t_com_category set LATEST_TIME_PERIOD_ID=? where id=?";
                PreparedStatement pst = connection.prepareStatement(sql);
                for (Category category : calcCategoryList) {
                    pst.setObject(1, category.getTimePeriod() == null ? null : category.getTimePeriod().getId());
                    pst.setObject(2, category.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }

    /**批量更新记分卡采集结果数据
     * @param calcGatherResultList 记分卡采集结果对象集合
     */
    @Transactional
    public void batchUpdateGatherResultList(final List<RelaAssessResult> calcGatherResultList) {
        o_relaAssessResultDAO.getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                connection.setAutoCommit(false);
                String sql = "update t_kpi_sm_assess_result set assessment_value=?,assessment_status=?,trend=? where id=?";
                PreparedStatement pst = connection.prepareStatement(sql);
                for (RelaAssessResult categoryGatherResult : calcGatherResultList) {
                    pst.setObject(1, categoryGatherResult.getAssessmentValue());
                    pst.setObject(2, categoryGatherResult.getAssessmentStatus() == null ? null : categoryGatherResult.getAssessmentStatus().getId());
                    pst.setObject(3, categoryGatherResult.getTrend() == null ? null : categoryGatherResult.getTrend().getId());
                    pst.setObject(4, categoryGatherResult.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }

    /**查询所有记分卡所对应的采集数据集合 key:记分卡id value:采集结果数据
     * @return
     */
    public Map<String, List<Object[]>> findCategoryRelaKpiGatherResultMap() {
        StringBuffer sqlBuff = new StringBuffer();
        Map<String, List<Object[]>> resultMap = new HashMap<String, List<Object[]>>();
        sqlBuff.append(" SELECT k.kpi_name, r.assessment_value,r.finish_value, c.category_name,k.gather_frequence,k.id kid,c.id cid ,r.id krid,p.id tid,p.start_time,p.end_time,kc.eweight ");
        sqlBuff.append(" FROM t_kpi_kpi_gather_result r, t_kpi_kpi k,t_kpi_kpi_rela_category kc,t_com_category c,t_com_time_period p ");
        sqlBuff.append(" WHERE r.kpi_id = k.id AND k.id = kc.kpi_id AND kc.category_id = c.id AND r.time_period_id = p.id AND k.gather_frequence = p.etype ");
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

    /**查询所有记分卡所对应下级记分卡的采集数据集合 key:父记分卡id value:子记分卡所对应采集结果数据集合
     * @return
     */
    public Map<String, List<Object[]>> findCategoryRelaSubCategoryResultMap() {
        StringBuffer sqlBuff = new StringBuffer();
        Map<String, List<Object[]>> resultMap = new HashMap<String, List<Object[]>>();
        sqlBuff.append(" SELECT c.CATEGORY_NAME, r.assessment_value, c.id cid,pc.id pcid,r.id rid,p.id tid,");
        sqlBuff.append(" p.START_TIME, p.END_TIME FROM t_kpi_sm_assess_result r, t_com_category c, t_com_category pc,t_com_time_period p");
        sqlBuff.append(" WHERE r.OBJECT_ID = c.id AND r.TIME_PERIOD_ID = p.ID AND r.DATA_TYPE = 'sc' and c.PARENT_ID=pc.id ");
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

    /**查询所有记分卡对象集合
     * @return
     */
    public List<Category> findAllCategory() {
        List<Category> categoryListAll = o_categoryBO.findDetachedCalcCategoryAll();
        return categoryListAll;
    }

    /**查询记分卡采集结果对象集合
     * @param objectIdList 记分卡id集合
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
