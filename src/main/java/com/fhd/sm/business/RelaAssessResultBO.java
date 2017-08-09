package com.fhd.sm.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.jdbc.Work;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.base.SqlBuilder;
import com.fhd.dao.comm.TimePeriodDAO;
import com.fhd.dao.kpi.RelaAssessResultDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.RelaAssessResultDTO;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.web.controller.util.Utils;
import com.fhd.sys.business.dic.DictBO;

/**
 * 记分卡，指标类型，战略目标相关评估结果分值表BO.
 * @author   吴德福
 * @version  
 * @since    Ver 1.1
 * @Date     2013-1-7  下午5:25:44
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class RelaAssessResultBO {

    @Autowired
    private RelaAssessResultDAO o_relaAssessResultDAO;

    @Autowired
    private StrategyMapBO o_strategyMapBO;

    @Autowired
    private CategoryBO o_categoryBO;

    @Autowired
    private TimePeriodDAO o_timePeriodDAO;

    @Autowired
    private DictBO o_dictBO;
    
    @Autowired
    private TimePeriodBO o_timePeriodBO;
    
    @Autowired
    private KpiBO o_kpiBO;

    
    private static final Log log= LogFactory.getLog(RelaAssessResultBO.class);

    
    /**根据id集合查询所对应的采集数据 
     * @param objectIdList 记分卡或目标id集合
     * @return key:记分卡或目标id集合 value:所对应的采集结果对象集合
     */
    public Map<String,List<RelaAssessResult>> findMapGatherResultListByObjectIdList(List<String> objectIdList,String year,String dataType){
        Map<String,List<RelaAssessResult>> resultMap = new HashMap<String, List<RelaAssessResult>>();
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timePeriod");
        criteria.add(Restrictions.in("objectId", objectIdList));
        criteria.add(Restrictions.eq("timePeriod.year", year));
        criteria.add(Restrictions.eq("dataType", dataType));
        criteria.addOrder(Order.asc("objectId"));
        List<RelaAssessResult> resultList = criteria.list();
        for (RelaAssessResult gatherResult : resultList)
        {
            String objectId = gatherResult.getObjectId();
            if(!resultMap.containsKey(objectId)){
                List<RelaAssessResult> childResultList = new ArrayList<RelaAssessResult>();
                resultMap.put(objectId, childResultList);
            }
            resultMap.get(objectId).add(gatherResult);
        }
        return resultMap;
    }
    
    /**
     * 查询时间纬度表,类型为月.
     * @param year 年份信息
     */
    public List<TimePeriod> findTimePeriodByMonthType(String year) {
        Criteria timeCriteria = this.o_timePeriodDAO.createCriteria();
        timeCriteria.setCacheable(true);// 缓存时间纬度提高效率
        timeCriteria.add(Restrictions.eq("year", String.valueOf(year)));
        timeCriteria.add(Restrictions.eq("type", "0frequecy_month"));
        timeCriteria.addOrder(Order.asc("endTime"));
        timeCriteria.addOrder(Order.desc("startTime"));
        return timeCriteria.list();
    }

    
    /**
     * 根据id查询相关评估结果分值表.
     * @param id
     * @return RelaAssessResult
     */
    public RelaAssessResult findRelaAssessResultById(String id) {
    	RelaAssessResult result  = null;
    	Criteria criteria = o_relaAssessResultDAO.createCriteria();
    	criteria.add(Restrictions.eq("id", id));
    	List<RelaAssessResult> list = criteria.list();
    	if(list.size()>0){
    		result = list.get(0);
    	}
        return result;
    }

    /**
     * 根据对象id查询最新相关评估结果分值表.
     * @param objectId 对象id
     * @return RelaAssessResult
     */
    public RelaAssessResult findLastestRelaAssessResultListByObjectId(String objectId) {
        RelaAssessResult relaAssessResult = null;
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        if (StringUtils.isNotBlank(objectId)) {
            criteria.add(Restrictions.eq("objectId", objectId));
        }
        criteria.add(Restrictions.isNotNull("assessmentValue"));
        criteria.addOrder(Order.desc("beginTime"));
        List<RelaAssessResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            relaAssessResult = list.get(0);
        }
        return relaAssessResult;
    }

    /**
     * 根据查询条件查询相关评估结果分值表.
     * @param objectId 对象id
     * @param objectName 对象名称
     * @param dataType 对象类型
     * @param nameList 对象名称集合
     * @return List<RelaAssessResult>
     */
    public List<RelaAssessResult> findRelaAssessResultListBySome(String objectId, String objectName, String dataType, List<String> nameList) {
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        if (StringUtils.isNotBlank(objectId)) {
            criteria.add(Restrictions.eq("objectId", objectId));
        }
        if (StringUtils.isNotBlank(objectName)) {
            criteria.add(Restrictions.like("objectName", objectName, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(dataType)) {
            if ("category".equals(dataType)) {
                dataType = "sc";
            }
            else if ("strategy".equals(dataType)) {
                dataType = "str";
            }
            criteria.add(Restrictions.eq("dataType", dataType));
        }
        if (null != nameList && nameList.size() > 0) {
            criteria.add(Restrictions.in("objectName", nameList));
        }
        criteria.addOrder(Order.asc("beginTime"));
        return criteria.list();
    }

    /**
     * 根据参数查询记分卡的值.
     * @param targetId 记分卡id
     * @param valueType 值类型
     * @param timePeriodId 时间区间维id
     * @return List<Object[]>
     */
    public List<Object[]> findRelaAssessResultBySubCategory(String targetId, String valueType, String timePeriodId) {
    	String dateStr = "";
    	if(StringUtils.isNotBlank(timePeriodId)){
    		TimePeriod timePeriod = o_timePeriodBO.findTimePeriodById(timePeriodId);
        	Date startTime = timePeriod.getStartTime();
        	dateStr = DateUtils.formatDate(startTime, "yyyy-MM-dd");
    	}else{
    		Date date = new Date();
            dateStr = DateUtils.formatDate(date, "yyyy-MM-dd");
    	}
    	Map<String,String> model = new HashMap<String, String>();
    	String querySql = SqlBuilder.getSql("findRelaAssessResultBySubCategory", model);
    	SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(querySql);
    	sqlQuery.setString("targetId", targetId);
    	sqlQuery.setString("date", dateStr);
    	return sqlQuery.list();
    }

    /**
     * 根据参数查询战略目标的值.
     * @param targetId 战略目标id
     * @param valueType 值类型
     * @param timePeriodId 时间区间维id
     * @return List<Object[]>
     */
    public List<Object[]> findRelaAssessResultBySubStrategy(String targetId, String valueType, String timePeriodId) {
    	String dateStr = "";
    	if(StringUtils.isNotBlank(timePeriodId)){
    		TimePeriod timePeriod = o_timePeriodBO.findTimePeriodById(timePeriodId);
        	Date startTime = timePeriod.getStartTime();
        	dateStr = DateUtils.formatDate(startTime, "yyyy-MM-dd");
    	}else{
    		Date date = new Date();
            dateStr = DateUtils.formatDate(date, "yyyy-MM-dd");
    	}
    	Map<String,String> model = new HashMap<String, String>();
    	String querySql = SqlBuilder.getSql("findRelaAssessResultBySubStrategy", model);
    	SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(querySql);
    	sqlQuery.setString("targetId", targetId);
    	sqlQuery.setString("date", dateStr);
    	return sqlQuery.list();
    }

    /**根据对象id和类型和日期查询评分结果
     * @param objectId记分卡或目标id
     * @param type 对象类型  'sc':记分卡 'str':目标
     * @param date 日期
     * @return
     */
    public RelaAssessResult findCurrentRelaAssessResultByType(String objectId, String type, Date date) {
        RelaAssessResult relaassessresult = null;
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.add(Restrictions.eq("dataType", type));
        criteria.add(Restrictions.le("beginTime", date));
        criteria.add(Restrictions.ge("endTime", date));
        criteria.add(Restrictions.eq("objectId", objectId));
        List<RelaAssessResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            relaassessresult = list.get(0);
        }
        return relaassessresult;
    }
    

    /**
     * 根据目标的名称查询它下级的采集结果
     * @param name 目标的名称
     * @return 下级目标的采集结果
     */
    public List<Object[]> findChildRelaAssessResultByStrategyName(String name) {
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer joinBuf = new StringBuffer();
        StringBuffer whereBuf = new StringBuffer();
        selectBuf.append(" select sm.strategy_map_name,result.id,result.object_id,result.object_name,result.time_period_id,result.begin_time,result.end_time,result.assessment_value,result.assessment_status, ");
        selectBuf.append(" result.assessment_alarm_id,result.forecast_value,result.forecast_status,result.forecast_alarm_id,result.trend,result.data_type ");
        joinBuf.append(" from t_kpi_strategy_map sm ");
        joinBuf.append(" left outer join t_kpi_strategy_map parentsm on sm.parent_id=parentsm.id ");
        joinBuf.append(" left outer join t_kpi_sm_assess_result result on sm.id=result.object_id and to_days(now())>=to_days(result.begin_time) and to_days(now())<= to_days(result.end_time) ");
        whereBuf.append(" where sm.delete_status=1 and result.data_type ='str' and 1=1 ");
        if (StringUtils.isNotBlank(name)) {
            whereBuf.append(" and parentsm.strategy_map_name=:strategyName ");
        }
        else {
            whereBuf.append(" and sm.parent_id is null ");
        }
        String sql = selectBuf.append(joinBuf).append(whereBuf).toString();
        SQLQuery sqlquery = o_relaAssessResultDAO.createSQLQuery(sql);
        sqlquery.setString("strategyName", name);
        return sqlquery.list();
    }

    /**
     * 根据目标名称集合查询对应的采集结果
     * @param names 战略目标名称
     * @return List<RelaAssessResult> 战略目标对象的结果
     */
    public List<RelaAssessResult> findSmAssessResultByNames(List<String> names) {
        Date currentDate = new Date();
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.add(Restrictions.eq("dataType", "str"));
        criteria.add(Restrictions.in("objectName", names));
        criteria.add(Restrictions.and(Restrictions.ge("beginTime", currentDate), Restrictions.le("endTime", currentDate)));
        return criteria.list();
    }

    /**
     * 根据记分卡的名称查询它下级的采集结果
     * @param name 记分卡的名称
     * @return 下级记分卡的采集结果
     */
    public List<Object[]> findChildRelaRelaAssessResultByCategoryName(String name) {
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer joinBuf = new StringBuffer();
        StringBuffer whereBuf = new StringBuffer();
        selectBuf
                .append(" select category.category_name,result.id,result.object_id,result.object_name,result.time_period_id,result.begin_time,result.end_time,result.assessment_value,result.assessment_status, ");
        selectBuf
                .append(" result.assessment_alarm_id,result.forecast_value,result.forecast_status,result.forecast_alarm_id,result.trend,result.data_type ");
        joinBuf.append(" from t_com_category category ");
        joinBuf.append(" left outer join t_com_category parentcategory on category.parent_id=parentcategory.id ");
        joinBuf.append(" left outer join t_kpi_sm_assess_result result on category.id=result.object_id and to_days(now())>=to_days(result.begin_time) and to_days(now())<= to_days(result.end_time) ");
        whereBuf.append(" where category.delete_status=1 and result.data_type ='sc' and 1=1 ");
        if (StringUtils.isNotBlank(name)) {
            whereBuf.append(" and parentcategory.category_name=:categoryName ");
        }
        else {
            whereBuf.append(" and category.parent_id is null ");
        }
        String sql = selectBuf.append(joinBuf).append(whereBuf).toString();
        SQLQuery sqlquery = o_relaAssessResultDAO.createSQLQuery(sql);
        sqlquery.setString("categoryName", name);
        return sqlquery.list();
    }

    /**
     * 根据记分卡名称集合查询对应的采集结果
     * @param names 记分卡名称
     * @return List<RelaAssessResult> 记分卡集合对象对应的结果
     */
    public List<RelaAssessResult> findCategoryAssessResultByNames(List<String> names) {
        Date currentDate = new Date();
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.add(Restrictions.eq("dataType", "sc"));
        criteria.add(Restrictions.in("objectName", names));
        criteria.add(Restrictions.and(Restrictions.ge("beginTime", currentDate), Restrictions.le("endTime", currentDate)));
        return criteria.list();
    }


    /**
     * 通过dataType查询评估结果
     * @author 金鹏祥
     * @param dataType
     * @return List<RelaAssessResult>
    */
    public List<RelaAssessResult> findRelaAssessResultByDataType(String dataType, String objectId, boolean isNewValue, String yearId, String monthId) {
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        if (StringUtils.isNotBlank(dataType)) {
            criteria.add(Restrictions.eq("dataType", dataType));
        }

        criteria.add(Restrictions.eq("objectId", objectId));

        if (isNewValue) {
            criteria.add(Property.forName("timePeriod.id").like(yearId, MatchMode.ANYWHERE));
        }
        else {
            if (StringUtils.isNotBlank(monthId)) {
                //月份
                criteria.add(Restrictions.eq("timePeriod.id", monthId));
            }
            else {
                //年份
                criteria.add(Property.forName("timePeriod.id").like(yearId, MatchMode.ANYWHERE));
            }
        }
        criteria.addOrder(Order.asc("timePeriod.id"));
        return criteria.list();
    }
    
    
    /**根据对象id和类型查询最后跟新时间区间纬度
     * @param dataType数据类型 sc:记分卡   str:目标
     * @param objectId 记分卡或目标id
     * @return
     */
    public RelaAssessResult findLastAssessResultByObjectId(String dataType,String objectId){
    	TimePeriod lastTimePeriod = null;
    	Criteria criteria = o_relaAssessResultDAO.createCriteria();
        if (StringUtils.isNotBlank(dataType)) {
            criteria.add(Restrictions.eq("dataType", dataType));
        }
        if("sc".equals(dataType)){
        	Category category = this.o_categoryBO.findCategoryById(objectId);
        	if(null!=category){
        		lastTimePeriod = category.getTimePeriod();
        	}
        }else if("str".equals(dataType)){
        	StrategyMap strategyMap = this.o_strategyMapBO.findById(objectId);
        	if(null!=strategyMap){
        		lastTimePeriod = strategyMap.getTimePeriod();
        	}
        }
        criteria.add(Restrictions.eq("objectId", objectId));
        criteria.add(Restrictions.eq("timePeriod", lastTimePeriod));
        List<RelaAssessResult> list = criteria.list();
        if(list.size()>0){
        	return (RelaAssessResult) criteria.list().get(0);
        }
    	return null;
    }

    /**
     * 通过dataType查询评估结果
     * @author 金鹏祥
     * @param dataType
     * @return List<RelaAssessResult>
    */
    public RelaAssessResult findRelaAssessResultByDataTypeEn(String dataType, String objectId, boolean isNewValue, String yearId, String monthId) {
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        if (StringUtils.isNotBlank(dataType)) {
            criteria.add(Restrictions.eq("dataType", dataType));
        }

        criteria.add(Restrictions.eq("objectId", objectId));

        if (isNewValue) {
            criteria.add(Property.forName("timePeriod.id").like(yearId, MatchMode.ANYWHERE));
        }
        else {
            if (StringUtils.isNotBlank(monthId)) {
                //月份
                criteria.add(Restrictions.eq("timePeriod.id", monthId));
            }
            else {
                //年份
                criteria.add(Property.forName("timePeriod.id").like(yearId, MatchMode.ANYWHERE));
            }
        }
        criteria.add(Restrictions.isNotNull("assessmentValue"));
        criteria.addOrder(Order.desc("timePeriod.id"));
        if (criteria.list().size() != 0) {
            return (RelaAssessResult) criteria.list().get(0);
        }
        else {
            return null;
        }

    }

    /**
     * 通过dataType查询评估结果
     * @author 金鹏祥
     * @param dataType
     * @return List<RelaAssessResult>
    */
    public RelaAssessResult findRelaAssessResultByObjectId(String objectId) {
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.add(Restrictions.eq("objectId", objectId));
        criteria.addOrder(Order.desc("timePeriod.id"));
        if (criteria.list().size() != 0) {
            return (RelaAssessResult) criteria.list().get(0);
        }
        else {
            return null;
        }

    }
    /**
     * 查询记分卡和目标最后更新的采集结果
     * @param type sc:记分卡 sm:战略目标
     * @param isPage 是否分页
     * @param start 起始位置
     * @param limit 分页大小
     * @return HashMap<String, String>
     */
    public Map<String,Object> findLastAssessResultList(String type ,boolean isFocus,boolean isPage,int start,int limit,String query,String companyid){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        List<RelaAssessResult> resultList = new ArrayList<RelaAssessResult>();
        StringBuffer countSql = new StringBuffer();
        countSql.append(" select count(*) ");
        StringBuffer sql = new StringBuffer();
        StringBuffer fromsql = new StringBuffer();
        StringBuffer wheresql = new StringBuffer();
        Map<String,Object> paraMap = new HashMap<String, Object>();
        sql.append(" select object_id ,");
        if("sc".equals(type)){
            sql.append(" sm.category_name ,");
        }else if("str".equals(type)){
            sql.append(" sm.strategy_map_name ,");
        }
        sql.append(" assessment_value,assessment_status ");
        if("sc".equals(type)){
            fromsql.append(" from  t_com_category sm LEFT OUTER JOIN t_kpi_sm_assess_result smresult  on sm.ID=smresult.OBJECT_ID  ");
        }else if("str".equals(type)){
            fromsql.append(" from  t_kpi_strategy_map sm LEFT OUTER JOIN t_kpi_sm_assess_result smresult  on sm.ID=smresult.OBJECT_ID  ");
        }
        //wheresql.append(" where  end_time=(select max(end_time) from t_kpi_sm_assess_result r where smresult.OBJECT_ID=r.object_id and r.assessment_value is not null)");
        wheresql.append(" where  smresult.TIME_PERIOD_ID=sm.latest_time_period_id ");
        wheresql.append(" and smresult.data_type=:dataType ");
        if(StringUtils.isNotBlank(query)){
            if("sc".equals(type)){
                wheresql.append(" and sm.category_name like :QUERY ");
            }else if("str".equals(type)){
                wheresql.append(" and sm.strategy_map_name like :QUERY ");
            }
            paraMap.put("QUERY", "%"+query+"%");
        }
        if(StringUtils.isNotBlank(companyid)){
            wheresql.append(" and sm.company_id=:companyId ");
        }
        wheresql.append(" and sm.delete_status=1 ");
        if(isFocus){
            wheresql.append(" and sm.is_focus='0yn_y'   ");
        }
        wheresql.append(" and smresult.ASSESSMENT_VALUE is not null ");
        wheresql.append(" order by smresult.ASSESSMENT_VALUE ");
        if(StringUtils.isNotBlank(companyid)){
            paraMap.put("companyId", companyid);
        }
        paraMap.put("dataType", type);
        SQLQuery sqlcountquery = o_relaAssessResultDAO.createSQLQuery(countSql.append(fromsql).append(wheresql).toString(),paraMap);
        
        resultMap.put("totalCount", sqlcountquery.uniqueResult());
        SQLQuery sqlquery = o_relaAssessResultDAO.createSQLQuery(sql.append(fromsql).append(wheresql).toString(),paraMap);
        if(isPage){
            sqlquery.setFirstResult((start - 1) * limit);// 设置分页
            sqlquery.setMaxResults(limit);
        }
       
        List<Object[]> list = sqlquery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            RelaAssessResult result = new RelaAssessResult();
            Object[] objects = (Object[]) iterator.next();
            
            if(null!=objects[0]){
                result.setObjectId(String.valueOf(objects[0]));
            }
            if(null!=objects[1]){
                result.setObjectName(String.valueOf(objects[1]));
            }
            if(null!=objects[2]){
                result.setAssessmentValue(NumberUtils.createDouble(String.valueOf(objects[2])));
            }
            resultList.add(result);
        }
        resultMap.put("resultlist", resultList);
        return resultMap;
    }

    /**
    * 查询所有类型最新值
    * @author 金鹏祥
    * @return HashMap<String, String>
    */
    public Map<String, String> findAssessmentMaxEntTimeAll(String type, String categoryIds) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuffer sql = new StringBuffer();
        sql.append(" select object_id,assessment_status from  t_kpi_sm_assess_result smresult  ");
        sql.append(" where  end_time=(select max(end_time) from t_kpi_sm_assess_result r where smresult.OBJECT_ID=r.object_id and r.assessment_value is not null)");
        sql.append(" and data_type=:DATA_TYPE ");
        if (StringUtils.isNotBlank(categoryIds)) {
            sql.append(" and object_id in(:IDS) ");
        }
        SQLQuery sqlquery = o_relaAssessResultDAO.createSQLQuery(sql.toString());
        sqlquery.setParameter("DATA_TYPE", type);
        sqlquery.setParameterList("IDS", StringUtils.split(categoryIds, ","));
        List<Object[]> list = sqlquery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            if (objects[1] != null) {
                map.put(objects[0].toString(), objects[1].toString());
            }
            else {
                map.put(objects[0].toString(), null);
            }
        }
        return map;
    }
    
    /**查找记分卡或目标最新的采集数据时间区间纬度
     * @param objectId 记分卡或目标ID
     * @param type 'sc'表示记分卡  'str'表示目标
     * @return
     */
    public TimePeriod findMaxAssessResultTimePeriod(String objectId,String type){
    	DetachedCriteria dc= DetachedCriteria.forEntityName("com.fhd.entity.kpi.RelaAssessResult", "r");
    	dc.setProjection(Projections.max("endTime"));
    	dc.add(Restrictions.or(Restrictions.isNotNull("assessmentValue"),Restrictions.isNotNull("assessmentStatus")));
    	dc.add(Restrictions.eqProperty("re.objectId", "r.objectId"));
    	
    	Criteria criteria = this.o_relaAssessResultDAO.getSession().createCriteria(RelaAssessResult.class, "re");
    	criteria.add(Restrictions.eq("objectId", objectId));
    	criteria.add(Restrictions.eq("dataType", type));
        criteria.add(Subqueries.propertyEq("re.endTime", dc));
        List<RelaAssessResult> list = criteria.list();
        if(null!=list&&list.size()>0){
            return list.get(0).getTimePeriod();
        }
        return null;
    }
    
    /**
    * 查询所有类型最新值
    * @author 郝静
    * @return HashMap<String, String>
    */
    public Map<String, String> findAssessmentMaxEntTimeAllNew(String type, String ids) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuffer sql = new StringBuffer();
        sql.append(" select object_id,assessment_status from  t_kpi_sm_assess_result result  ");
        sql.append(" where  TIME_PERIOD_ID=(select LATEST_TIME_PERIOD_ID from ");
        if("str".equalsIgnoreCase(type)){
        	sql.append("t_kpi_strategy_map r ");
        }else if("sc".equalsIgnoreCase(type)){
        	sql.append("t_com_category r ");
        }
        sql.append(" where result.OBJECT_ID=r.id )");
        sql.append(" and data_type=:type");
        sql.append(" and result.assessment_value is not null");
        if (StringUtils.isNotBlank(ids)) {
            sql.append(" and object_id in(:IDS) ");
        }
        SQLQuery sqlquery = o_relaAssessResultDAO.createSQLQuery(sql.toString());
        sqlquery.setParameter("type", type);
        sqlquery.setParameterList("IDS", StringUtils.split(ids, ","));
        List<Object[]> list = sqlquery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            if (objects[1] != null) {
                map.put(objects[0].toString(), objects[1].toString());
            }
            else {
                map.put(objects[0].toString(), null);
            }
        }
        return map;
    }

    /**
     * 查询记分卡或目标的分值集合
     * @param objectId 记分卡或战略目标ID
     * @param type sc:记分卡 sm:战略目标
     * @author 陈晓哲
     * @return List<RelaAssessResult> 记分卡或目标的分值集合
     */
    public Page<RelaAssessResult> findRelaAssessResultsBySome(Page<RelaAssessResult> page, String objectId, String type, String sort, String dir,String query) {
        String sortstr = sort;
        Date currentDate = new Date();
        DetachedCriteria dc = DetachedCriteria.forClass(RelaAssessResult.class);
        dc.add(Restrictions.eq("objectId", objectId));
        dc.add(Restrictions.eq("dataType", type));
        dc.add(Restrictions.le("beginTime", currentDate));
        if(StringUtils.isNotBlank(query)){
            dc.add(Restrictions.like("timePeriod.id", "%"+query+"%"));
        }
        if ("assessmentStatusStr".equals(sort)) {
            dc.createAlias("assessmentStatus", "assessmentStatus", CriteriaSpecification.LEFT_JOIN);
            sortstr = "assessmentStatus.value";
        }
        else if ("directionstr".equals(sort)) {
            dc.createAlias("trend", "trend", CriteriaSpecification.LEFT_JOIN);
            sortstr = "trend.value";
        }
        else if ("dateRange".equals(sort)) {
            sortstr = "beginTime";
        }

        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }
        return o_relaAssessResultDAO.findPage(dc, page, false);
    }

    /**根据记分卡名称和时间区间维度id查询下级记分卡评估结果
     * @param categoryName 记分卡名称
     * @param timePeriod 时间区间字符串
     * @return
     */
    public List<RelaAssessResult> findCategoryAssessResultListBySome(String categoryName, String timePeriod){
        List<String> categoryIdList = new ArrayList<String>();
        Category category =  o_categoryBO.findCategoryByName(categoryName);
        String categoryId = category.getId();
        List<Category> childCategorys = o_categoryBO.findChildCategorysByCategoryId(categoryId);
        for (Category childCategory : childCategorys) {
            categoryIdList.add(childCategory.getId());
        }
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.add(Restrictions.in("objectId", categoryIdList));
        if(StringUtils.isNotBlank(timePeriod)){
            criteria.add(Restrictions.eq("timePeriod.id", timePeriod));
        }
        else{
            //当前时间
                Date currentDate = new Date();
                criteria.add(Restrictions.ge("endTime", currentDate));
                criteria.add(Restrictions.le("beginTime", currentDate));
        }
        return criteria.list();
    }
    /**根据记分卡名称和时间区间纬度id查询记分卡采集结果
     * @param targetName 记分卡名称
     * @param timePeriod 时间区间纬度ID
     * @return
     */
    public RelaAssessResult  findRelaAssessResultBySome(String targetName,String timePeriod){
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        if(StringUtils.isNotBlank(timePeriod)){
            criteria.add(Restrictions.eq("timePeriod.id", timePeriod));
        }else{
          //当前时间
            Date currentDate = new Date();
            criteria.add(Restrictions.ge("endTime", currentDate));
            criteria.add(Restrictions.le("beginTime", currentDate));
        }
        criteria.add(Restrictions.eq("objectName", targetName));
        List<RelaAssessResult> list = criteria.list();
        if(null!=list&&list.size()>0){
            return list.get(0);
        }
        return null;
    }
    /**根据对象id,包括记分卡和战略目标和时间区间纬度id查询记分卡采集结果
     * @param objectId 对象id
     * @param timePeriod 时间区间纬度ID
     * @return
     */
    public RelaAssessResult  findRelaAssessResultByIdAndTimePeriod(String objectId,String timePeriod){
    	Criteria criteria = o_relaAssessResultDAO.createCriteria();
    	if(StringUtils.isNotBlank(timePeriod)){
    		criteria.add(Restrictions.eq("timePeriod.id", timePeriod));
    	}else{
    		//当前时间
    		Date currentDate = new Date();
    		criteria.add(Restrictions.ge("endTime", currentDate));
    		criteria.add(Restrictions.le("beginTime", currentDate));
    	}
    	criteria.add(Restrictions.eq("objectId", objectId));
    	List<RelaAssessResult> list = criteria.list();
    	if(null!=list&&list.size()>0){
    		return list.get(0);
    	}
    	return null;
    }
    
    /**根据对象名称集合查询某一期的采集数据
     * @param nameList 记分卡或目标名称集合
     * @param objectType 'sc':记分卡,'str':目标
     * @param timePeriodId 时间区间ID
     * @param companyId 公司ID
     * @return
     */
    public List<RelaAssessResultDTO> findAssessResultListByNames(List<String> nameList, String objectType,String timePeriodId, String companyId){
    	List<RelaAssessResultDTO> results = new ArrayList<RelaAssessResultDTO>();
    	StringBuffer selectSqlbuf = new StringBuffer();
    	StringBuffer fromSqlbuf = new StringBuffer();
    	StringBuffer whereSqlbuf = new StringBuffer(" where 1=1 ");
    	selectSqlbuf.append("select r.id,r.object_id  ");
    	fromSqlbuf.append(" from t_kpi_sm_assess_result r ");
    	if("str".equals(objectType)){
    		fromSqlbuf.append(" , t_kpi_strategy_map c ");
    		selectSqlbuf.append(" ,c.strategy_map_name ,r.time_period_id,r.assessment_value ");
    		whereSqlbuf.append(" and c.id=r.object_id ");
    		whereSqlbuf.append(" and r.time_period_id=:timePeriodId ");
    		whereSqlbuf.append(" and c.company_id=:companyId ");
    		whereSqlbuf.append(" and c.strategy_map_name in (:namelist) ");
    	}else if("sc".equals(objectType)){
    		fromSqlbuf.append(" , t_com_category c ");
    		selectSqlbuf.append(" ,c.category_name ,r.time_period_id,r.assessment_value ");
    		whereSqlbuf.append(" and c.id=r.object_id ");
    		whereSqlbuf.append(" and r.time_period_id=:timePeriodId ");
    		whereSqlbuf.append(" and c.company_id=:companyId ");
    		whereSqlbuf.append(" and c.category_name in (:namelist) ");
    	}
    	
    	String sql = selectSqlbuf.append(fromSqlbuf).append(whereSqlbuf).toString();
    	SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(sql);
    	sqlQuery.setParameter("timePeriodId", timePeriodId);
    	sqlQuery.setParameter("companyId", companyId);
    	sqlQuery.setParameterList("namelist", nameList, new StringType());
    	List<Object[]> listData = sqlQuery.list();
    	if(null!=listData&&listData.size()>0){
    		for(int i=0;i<listData.size();i++){
    			Object[] data = listData.get(i);
    			RelaAssessResultDTO result = new RelaAssessResultDTO();
    			result.setId(String.valueOf(data[0]));
    			result.setObjectId(String.valueOf(data[1]));
    			result.setObjectName(String.valueOf(data[2]));
    			if(null!=data[4]&&NumberUtils.isNumber(String.valueOf(data[4]))){
    				result.setAssessmentValue(Double.valueOf(String.valueOf(data[4])));
    			}else{
    				result.setAssessmentValue(null);
    			}
    			results.add(result);
    		}
    	}
    	return results;
    }
    /**根据对象名称集合查询某一期的采集数据
     * @param nameList 记分卡编码集合
     * @param objectType 'sc':记分卡,'str':目标
     * @param timePeriodId 时间区间ID
     * @param companyId 公司ID
     * @return
     */
    public List<RelaAssessResultDTO> findAssessResultListByCodes(List<String> codeList, String objectType,String timePeriodId, String companyId){
    	List<RelaAssessResultDTO> results = new ArrayList<RelaAssessResultDTO>();
    	StringBuffer selectSqlbuf = new StringBuffer();
    	StringBuffer fromSqlbuf = new StringBuffer();
    	StringBuffer whereSqlbuf = new StringBuffer(" where 1=1 ");
    	selectSqlbuf.append("select r.id,r.object_id,r.object_name,r.time_period_id,r.assessment_value  ");
    	fromSqlbuf.append(" from t_kpi_sm_assess_result r ");
    	if("str".equals(objectType)){
    		fromSqlbuf.append(" , t_kpi_strategy_map c ");
    		selectSqlbuf.append(" ,c.strategy_map_code ");
    		whereSqlbuf.append(" and c.id=r.object_id ");
    		whereSqlbuf.append(" and r.time_period_id=:timePeriodId ");
    		whereSqlbuf.append(" and c.company_id=:companyId ");
    		whereSqlbuf.append(" and c.strategy_map_code in (:codelist) ");
    	}else if("sc".equals(objectType)){
    		fromSqlbuf.append(" , t_com_category c ");
    		selectSqlbuf.append(" ,c.category_code ");
    		whereSqlbuf.append(" and c.id=r.object_id ");
    		whereSqlbuf.append(" and r.time_period_id=:timePeriodId ");
    		whereSqlbuf.append(" and c.company_id=:companyId ");
    		whereSqlbuf.append(" and c.category_code in (:codelist) ");
    	}
    	
    	String sql = selectSqlbuf.append(fromSqlbuf).append(whereSqlbuf).toString();
    	SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(sql);
    	sqlQuery.setParameter("timePeriodId", timePeriodId);
    	sqlQuery.setParameter("companyId", companyId);
    	sqlQuery.setParameterList("codelist", codeList, new StringType());
    	List<Object[]> listData = sqlQuery.list();
    	if(null!=listData&&listData.size()>0){
    		for(int i=0;i<listData.size();i++){
    			Object[] data = listData.get(i);
    			RelaAssessResultDTO result = new RelaAssessResultDTO();
    			result.setId(String.valueOf(data[0]));
    			result.setObjectId(String.valueOf(data[1]));
    			result.setObjectName(String.valueOf(data[2]));
    			if(null!=data[4]&&NumberUtils.isNumber(String.valueOf(data[4]))){
    				result.setAssessmentValue(Double.valueOf(String.valueOf(data[4])));
    			}else{
    				result.setAssessmentValue(null);
    			}
    			if(null!=data[5]){
    				result.setObjectCode(String.valueOf(data[5]));
    			}else{
    				result.setObjectCode("");
    			}
    			
    			results.add(result);
    		}
    	}
    	return results;
    }
    
    /**根据对象名称集合查询某一期的采集数据
     * @param nameList 记分卡编码集合
     * @param objectType 'sc':记分卡,'str':目标
     * @param timePeriodId 时间区间ID
     * @return Map<String,List<RelaAssessResultDTO>> key:公司id value:该公司下,当期采集数据
     */
    public Map<String,List<RelaAssessResultDTO>> findAssessResultListByCodes(List<String> codeList, String objectType,String timePeriodId){
        Map<String,List<RelaAssessResultDTO>> resultMap = new HashMap<String, List<RelaAssessResultDTO>>();
        StringBuffer selectSqlbuf = new StringBuffer();
        StringBuffer fromSqlbuf = new StringBuffer();
        StringBuffer whereSqlbuf = new StringBuffer(" where 1=1 ");
        selectSqlbuf.append("select r.id,r.object_id,r.object_name,r.time_period_id,r.assessment_value  ");
        fromSqlbuf.append(" from t_kpi_sm_assess_result r ");
        if("str".equals(objectType)){
            fromSqlbuf.append(" , t_kpi_strategy_map c ");
            selectSqlbuf.append(" ,c.strategy_map_code ");
            whereSqlbuf.append(" and c.id=r.object_id ");
            whereSqlbuf.append(" and r.time_period_id=:timePeriodId ");
            whereSqlbuf.append(" and c.strategy_map_code in (:codelist) ");
        }else if("sc".equals(objectType)){
            fromSqlbuf.append(" , t_com_category c ");
            selectSqlbuf.append(" ,c.category_code ");
            whereSqlbuf.append(" and c.id=r.object_id ");
            whereSqlbuf.append(" and r.time_period_id=:timePeriodId ");
            whereSqlbuf.append(" and c.category_code in (:codelist) ");
        }
        selectSqlbuf.append(" ,r.company_id ");
        String sql = selectSqlbuf.append(fromSqlbuf).append(whereSqlbuf).toString();
        SQLQuery sqlQuery = o_relaAssessResultDAO.createSQLQuery(sql);
        sqlQuery.setParameter("timePeriodId", timePeriodId);
        sqlQuery.setParameterList("codelist", codeList, new StringType());
        List<Object[]> listData = sqlQuery.list();
        if(null!=listData&&listData.size()>0){
            for(int i=0;i<listData.size();i++){
                Object[] data = listData.get(i);
                RelaAssessResultDTO result = new RelaAssessResultDTO();
                String companyId = null==data[6]?"":String.valueOf(data[6]);
                if(!resultMap.containsKey(companyId)){
                    List<RelaAssessResultDTO> results = new ArrayList<RelaAssessResultDTO>();
                    resultMap.put(companyId, results);
                }
                List<RelaAssessResultDTO> results = resultMap.get(companyId);
                result.setId(String.valueOf(data[0]));
                result.setObjectId(String.valueOf(data[1]));
                result.setObjectName(String.valueOf(data[2]));
                if(null!=data[4]&&NumberUtils.isNumber(String.valueOf(data[4]))){
                    result.setAssessmentValue(Double.valueOf(String.valueOf(data[4])));
                }else{
                    result.setAssessmentValue(null);
                }
                if(null!=data[5]){
                    result.setObjectCode(String.valueOf(data[5]));
                }else{
                    result.setObjectCode("");
                }
                
                results.add(result);
            }
        }
        return resultMap;
    }
    
    
    
    /**
     * 根据评估值计算趋势.
     * 
     * @param relaAssessResult当期记分卡或战略目标评分结果
     *            
     */
    public DictEntry calculateTrendByAssessmentValue(RelaAssessResult relaAssessResult) {
        DictEntry trend = null;
        Double preAssvalue = null;
        String objectId = relaAssessResult.getObjectId();
        TimePeriod prePeriodTime = relaAssessResult.getTimePeriod().getPrePeriod();
        Double assvalue = relaAssessResult.getAssessmentValue();
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.add(Restrictions.eq("timePeriod", prePeriodTime));
        criteria.add(Restrictions.eq("objectId", objectId));
        criteria.add(Restrictions.eq("dataType", relaAssessResult.getDataType()));
        List<RelaAssessResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            if (null != list.get(0).getAssessmentValue()) {
                preAssvalue = list.get(0).getAssessmentValue();
                if (null != preAssvalue && null != assvalue) {
                    DictEntry upDict = o_dictBO.findDictEntryById("up");
                    DictEntry flatDict = o_dictBO.findDictEntryById("flat");
                    DictEntry downDict = o_dictBO.findDictEntryById("down");
                    if (preAssvalue.compareTo(assvalue) < 0) {
                        trend = upDict;
                    }
                    else if (preAssvalue.compareTo(assvalue) == 0) {
                        trend = flatDict;
                    }
                    else {
                        trend = downDict;
                    }
                }
            }
        }
        return trend;
    }

    /**更新记分卡或战略目标的趋势信息
     * @param objectId 记分卡或战略目标ID
     * @param type sc:记分卡 str:战略目标
     */
    @Transactional
    public void mergedRelaAssessResultsTrend(String objectId, String type) {
        DictEntry trend = null;
        Criteria criteria = o_relaAssessResultDAO.createCriteria();
        criteria.add(Restrictions.eq("objectId", objectId));
        criteria.add(Restrictions.eq("dataType", type));
        criteria.addOrder(Order.asc("beginTime"));
        List<RelaAssessResult> assessResults = criteria.list();
        for (RelaAssessResult result : assessResults) {
            trend = calculateTrendByAssessmentValue(result);
            result.setTrend(trend);
            o_relaAssessResultDAO.merge(result);
        }
    }
    
    

    /**修改记分卡或战略目标的评分结果
     * @param modifiedRecores 需要修改的评分结果记录
     */
    @Transactional
    public void mergedRelaAssessResults(String modifiedRecores) {
        JSONObject jsobj = JSONObject.fromObject(modifiedRecores);
        String type = (String) jsobj.get("type");
        String objectId = (String) jsobj.get("objectId");
        JSONArray jsonArray = jsobj.getJSONArray("datas");
        if (jsonArray.size() > 0) {
            AlarmPlan alarmPlan = null;
            if ("sc".equals(type)) {
                alarmPlan = o_categoryBO.findAlarmPlanByScId(objectId, Contents.ALARMPLAN_REPORT);
            }
            else if ("str".equals(type)) {
                alarmPlan = o_strategyMapBO.findAlarmPlanBySmId(objectId, Contents.ALARMPLAN_REPORT);
            }
            DictEntry cssicon = null;
            Double assessmentValue = null;
            RelaAssessResult relaAssessResult = null;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject assessResult = jsonArray.getJSONObject(i);
                String id = assessResult.getString("id");
                String assessmentValuestr = assessResult.getString("assessmentValue");
                if (Utils.isNumeric(assessmentValuestr)) {
                    assessmentValue = Double.valueOf(assessmentValuestr);
                }
                relaAssessResult = this.findRelaAssessResultById(id);
                if (null != alarmPlan) {
                	cssicon = o_kpiBO.findKpiAlarmStatusByValues(alarmPlan, assessmentValue);
                    relaAssessResult.setAssessmentStatus(cssicon);
                }
                relaAssessResult.setAssessmentValue(assessmentValue);
                o_relaAssessResultDAO.merge(relaAssessResult);
            }
            //更新记分卡或战略目标的趋势信息
            mergedRelaAssessResultsTrend(objectId, type);

        }
    }
    

    /**
     * 去掉已经生成的战略目标或是计分卡的采集结果数据的时间纬度.
     * @param timePeriodList 时间纬度列表
     * @param objectID 计分卡或是战略目标ID
     * @param dataType 'str':战略目标,'sc':计分卡
     */
    public List<TimePeriod> mergeTimePeriodList(List<TimePeriod> timePeriodList, List<RelaAssessResult> originalTimePeriodList , String dataType) {
        List<TimePeriod> mergeTimePeriodList = new ArrayList<TimePeriod>();
        for (TimePeriod timePeriod : timePeriodList) {
            boolean existFlag = false;
            for (RelaAssessResult okpigatherresult : originalTimePeriodList) {
                if (timePeriod.getId().equals(okpigatherresult.getTimePeriod().getId())) {
                    existFlag = true;
                }
            }
            if (!existFlag) {
                mergeTimePeriodList.add(timePeriod);
            }
        }
        return mergeTimePeriodList;
    }

    /**
     * 修改相关评估结果分值表.
     * @param relaAssessResult
     */
    @Transactional
    public void mergeRelaAssessResult(RelaAssessResult relaAssessResult) {
        o_relaAssessResultDAO.merge(relaAssessResult);
    }

    /**
     * 根据id删除相关评估结果分值表.
     * @param id
     */
    @Transactional
    public void removeRelaAssessResultById(String id) {
        o_relaAssessResultDAO.delete(id);
    }

    /**
     * 批量删除相关评估结果分值表.
     * @param ids 相关评估结果分值表id集合
     */
    @Transactional
    public void removeRelaAssessResultByIds(String ids) {
        StringBuilder relaAssessResultIds = new StringBuilder();
        String[] idArray = ids.split(",");
        for (int i = 0; i < idArray.length; i++) {
            relaAssessResultIds.append("'").append(idArray[i]).append("'");
            if (i != idArray.length - 1) {
                relaAssessResultIds.append(",");
            }
        }
        o_relaAssessResultDAO.createQuery("delete RelaAssessResult where id in (" + relaAssessResultIds + ")").executeUpdate();
    }

    
    /**
     * 新增相关评估结果分值表.
     * @param relaAssessResult
     */
    @Transactional
    public void saveRelaAssessResult(RelaAssessResult relaAssessResult) {
        o_relaAssessResultDAO.merge(relaAssessResult);
    }
    
    /**
     * 生成计分卡和战略目标采集数据.
     * @param list [{objectId:'计分卡或战略目标id',objectName:'计分卡或战略目标名称'}]
     * @param timeList 时间纬度
     * @param dataType 'str':战略目标,'sc':计分卡
     */
    @Transactional
    public void saveBathResultData(final JSONArray list, final List<TimePeriod> timeList, final String dataType,final String year) {

        o_relaAssessResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                List<String> objectIdList = new ArrayList<String>();
                for (Object jsobj : list) {
                    JSONObject jobj = ((JSONObject) jsobj);
                    String objectId = jobj.getString("objectId");
                    objectIdList.add(objectId);
                }
                Map<String,List<RelaAssessResult>> resultMap = findMapGatherResultListByObjectIdList(objectIdList,year,dataType);
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                final String bathSql = "insert into t_kpi_sm_assess_result (id,object_id,object_name,time_period_id,begin_time,end_time,data_type,company_id) values(?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(bathSql);
                for (Object jsobj : list) {
                    JSONObject jobj = ((JSONObject) jsobj);
                    String objectId = jobj.getString("objectId");
                    String objectName = jobj.getString("objectName");
                    List<RelaAssessResult> originalTimePeriodList = resultMap.get(objectId);
                    List<TimePeriod> mergeTimeList = null;
                    if(null==originalTimePeriodList){
                        mergeTimeList = timeList;
                    }else{
                        mergeTimeList = mergeTimePeriodList(timeList, originalTimePeriodList, dataType);
                    }
                    for (TimePeriod timePeriod : mergeTimeList) {
                        pst.setString(1, Identities.uuid());
                        pst.setString(2, objectId);
                        pst.setString(3, objectName);
                        pst.setString(4, timePeriod.getId());
                        pst.setTimestamp(5, new java.sql.Timestamp(timePeriod.getStartTime().getTime()));
                        pst.setTimestamp(6, new java.sql.Timestamp(timePeriod.getEndTime().getTime()));
                        pst.setString(7, dataType);
                        pst.setString(8, jobj.getString("companyid"));
                        pst.addBatch();
                    }
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });

    }
    
    /**
     * 生成计分卡和战略目标采集数据.
     */
    @Transactional
    public boolean saveBathSMAndSCResultData() {
        boolean result = true;
        String year = DateUtils.getYear(new Date());
        JSONArray smJsonArray = new JSONArray();
        JSONArray scJsonArray = new JSONArray();
        try {
            final List<TimePeriod> timeList = findTimePeriodByMonthType(year);
            final List<StrategyMap> smList = o_strategyMapBO.findStrategyMapAll();
            final List<Category> scList = o_categoryBO.findCategoryAllByTask();

            for (StrategyMap strategyMap : smList) {
                JSONObject smJsObj = new JSONObject();
                smJsObj.put("objectId", strategyMap.getId());
                smJsObj.put("objectName", strategyMap.getName());
                smJsObj.put("companyid", strategyMap.getCompany().getId());
                smJsonArray.add(smJsObj);
            }
            for (Category category : scList) {
                JSONObject scJsObj = new JSONObject();
                scJsObj.put("objectId", category.getId());
                scJsObj.put("objectName", category.getName());
                scJsObj.put("companyid", category.getCompany().getId());
                scJsonArray.add(scJsObj);
            }
            if(smJsonArray.size()>0){
                saveBathResultData(smJsonArray, timeList, "str",year);
            }
            if(scJsonArray.size()>0){
                saveBathResultData(scJsonArray, timeList, "sc",year);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("生成采集数据异常:["+e.toString()+"]");
            result = false;
        }
        
        return result;
    }
    
    /**生成战略目标采集数据
     * @param year年
     */
    @Transactional
    public void generationSmGatherData(String year){
        JSONArray smJsonArray = new JSONArray();
        final List<TimePeriod> timeList = findTimePeriodByMonthType(year);
        final List<StrategyMap> smList = o_strategyMapBO.findStrategyMapAll();

        for (StrategyMap strategyMap : smList) {
            JSONObject smJsObj = new JSONObject();
            smJsObj.put("objectId", strategyMap.getId());
            smJsObj.put("objectName", strategyMap.getName());
            smJsObj.put("companyid", strategyMap.getCompany().getId());
            smJsonArray.add(smJsObj);
        }
        saveBathResultData(smJsonArray, timeList, "str",year);
    }
    
    /**生成记分卡标采集数据
     * @param year年
     */
    @Transactional
    public void generationScGatherData(String year){
        JSONArray scJsonArray = new JSONArray();
        final List<TimePeriod> timeList = findTimePeriodByMonthType(year);
        final List<Category> scList = o_categoryBO.findCategoryAllByTask();

        for (Category category : scList) {
            JSONObject scJsObj = new JSONObject();
            scJsObj.put("objectId", category.getId());
            scJsObj.put("objectName", category.getName());
            scJsObj.put("companyid", category.getCompany().getId());
            scJsonArray.add(scJsObj);
        }
        saveBathResultData(scJsonArray, timeList, "sc",year);
    }
    
    
}
