package com.fhd.sm.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.comm.business.formula.StatisticFunctionCalculateBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.base.SqlBuilder;
import com.fhd.dao.comm.TimePeriodDAO;
import com.fhd.dao.kpi.KpiGatherResultDAO;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.interfaces.IKpiGatherResultBO;
import com.fhd.sys.business.dic.DictBO;

/**
 * 
 * 指标采集结果业务逻辑BO
 * 
 * @author 陈晓哲
 * @version
 * @since Ver 1.1
 * @Date 2012 2012-9-17 上午13:56:52
 * 
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class KpiGatherResultBO implements IKpiGatherResultBO {

    @Autowired
    private KpiGatherResultDAO o_kpiGatherResultDAO;

    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private DictBO o_dictBO;

    @Autowired
    private TimePeriodDAO o_timePeriodDAO;

    @Autowired
    private CategoryBO o_categoryBO;
    
    @Autowired
    private TimePeriodBO o_timePeriodBO;
    
    private static Log logger = LogFactory.getLog(KpiGatherResultBO.class);
    
    /**
     * 根据指标采集结果id查询指标采集结果.
     * 
     * @param id
     *            指标采集结果id
     * @return KpiGatherResult
     */
    public KpiGatherResult findKpiGatherResultById(String id) {
        return o_kpiGatherResultDAO.get(id);
    }
    
    
    /**
     * 根据记分卡ID查询所关联的指标所在年的采集结果
     * 
     * @param categoryId
     *            记分卡ID
     * @param year
     *            年分
     * @return List<KpiGatherResult>
     */
    public Map<Kpi, List<KpiGatherResult>> findKpiGatherResultByCategoryId(String categoryId, String name, String year) {
        Map<Kpi, List<KpiGatherResult>> maps = new TreeMap<Kpi, List<KpiGatherResult>>();
        StringBuffer hqlbuf = new StringBuffer();
        hqlbuf.append(" select  distinct kpi from Category  category left join category.kpiRelaCategorys  kpireal left join  kpireal.kpi  kpi left join fetch kpi.kpiGatherResult  result left join result.timePeriod  time ");
        hqlbuf.append(" where kpi.deleteStatus=true and kpi.gatherFrequence.id=time.type and time.year=:year ");
        if (StringUtils.isNotBlank(categoryId)) {
            hqlbuf.append(" and category.id=:id ");
        }
        if (StringUtils.isNotBlank(name)) {
            hqlbuf.append(" and category.name=:name ");
        }
        hqlbuf.append(" order by category.name asc");
        Query query = o_kpiGatherResultDAO.createQuery(hqlbuf.toString());
        query.setParameter("year", year);
        if (StringUtils.isNotBlank(name)) {
            query.setParameter("name", name);
        }
        if (StringUtils.isNotBlank(categoryId)) {
            query.setParameter("id", categoryId);
        }
        List<Kpi> listkpi = query.list();
        for (Kpi kpi : listkpi) {
            maps.put(kpi, new ArrayList<KpiGatherResult>(kpi.getKpiGatherResult()));
        }
        return maps;
    }

    /**
     * 根据记分卡ID查询所关联的指标所在年的采集结果
     * 
     * @param categoryId
     *            记分卡ID
     * @param year
     *            年分
     * @return Map<Kpi, List<Object[]>>
     *         object[]数据中的数据顺序{name指标名称,frequence频率,lasttimeperiod最后更新时间区间维
     *         ,timeperiod时间区间维
     *         ,starttime开始时间,endtime结束时间,finishvalue实际值,status告警状态,direction趋势}
     */
    public Map<String, List<Object[]>> findKpiGatherResultsByCategoryId(String categoryId, String name, String year) {
        Map<String, List<Object[]>> map = new HashMap<String, List<Object[]>>();
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromLeftJoinBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        List<Object> paralist = new ArrayList<Object>();
        selectBuf.append(" select kpi.id  id, kpi.kpi_name  name , kpi.gather_frequence  frequence ,kpi.latest_time_period_id  latesttimeperiod, time.id  timeperiod,time.start_time  starttime,time.end_time  endtime ,result.finish_value  finishValue ,statusdict.dict_entry_value  status ,dirdict.dict_entry_value  direction ");
        fromLeftJoinBuf.append(" from t_kpi_kpi  kpi ");
        fromLeftJoinBuf.append(" left outer join ");
        fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result  result  ");
        fromLeftJoinBuf.append(" inner join ");
        fromLeftJoinBuf.append(" t_com_time_period  time on result.time_period_id=time.id  ");
        if (StringUtils.isNotBlank(year)) {
            fromLeftJoinBuf.append("  and time.eyear=?");
            paralist.add(year); 
        }
        else {
        	//String currentYear = DateUtils.getYear(new Date());
            //fromLeftJoinBuf.append(" and time.eyear='").append(currentYear).append("' ");
            fromLeftJoinBuf.append(" and time.eyear=(select t.EYEAR from t_com_time_period t where t.id=kpi.latest_time_period_id ) ");
        }
        fromLeftJoinBuf.append(" ) on kpi.id=result.kpi_id and kpi.gather_frequence=time.etype ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_kpi_kpi_rela_category  category on category.kpi_id=kpi.id ");
        wherebuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI' ");

        if (StringUtils.isNotBlank(categoryId)) {
            wherebuf.append(" and category.category_id=? ");
            paralist.add(categoryId);
        }
        if (StringUtils.isNotBlank(companyid)) {
            wherebuf.append(" and kpi.company_id=? ");
            paralist.add(companyid);
        }

        if (StringUtils.isNotBlank(name)) {
            wherebuf.append(" and kpi.kpi_name like '%").append(name).append("%'");
        }
        orderbuf.append(" order by name,starttime asc ");

        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);

        SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).append(orderbuf).toString(),
                paraobjects);
        List<Object[]> list = sqlquery.list();
        if (null != list) {
            for (Object[] objects : list) {
                String kpiId = (String) objects[0];
                String kpiName = (String) objects[1];
                String frequence = (String) objects[2];
                Object lasttimeperiod = objects[3];// 最后更新时间区间维ID
                String timeperiod = (String) objects[4];// 时间区间维ID
                Object starttime = objects[5];// 开始时间
                Object endtime = objects[6];// 结束时间
                Object finishvalue = objects[7];// 完成值
                String status = (String) objects[8];// 告警状态
                String direction = (String) objects[9];// 趋势
                String key = kpiName + "@" + frequence;
                Object[] valueObjects = new Object[] { kpiId, kpiName, frequence, lasttimeperiod, timeperiod, starttime, endtime, finishvalue,
                        status, direction };
                if (!map.containsKey(key)) {
                    List<Object[]> valueList = new ArrayList<Object[]>();
                    valueList.add(valueObjects);
                    map.put(key, valueList);
                }
                else {
                    map.get(key).add(valueObjects);
                }
            }
        }
        Map<String, List<Object[]>> resultmap = new HashMap<String, List<Object[]>>();
        for (Map.Entry<String, List<Object[]>> m : map.entrySet()) {
            String key = m.getKey();
            List<Object[]> values = m.getValue();
            String[] arr = key.split("@");
            String kpiName = arr[0];
            resultmap.put(kpiName, values);
        }
        return resultmap;
    }

    /**
     * 根据指标ID、时间ID查询KpiFatherResult
     * 
     * @author 金鹏祥
     * @param kpiId
     *            指标ID
     * @param timePeriodId
     *            时间ID
     * @return List<KpiGatherResult>
     */
    public List<KpiGatherResult> findKpiGatherResultByKpiIdAndTimePeriodId(String kpiId, boolean like, String timePeriodId) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        if (!like) {
            criteria.add(Restrictions.eq("timePeriod.id", timePeriodId));
        }
        else {
            criteria.add(Property.forName("timePeriod.id").like(timePeriodId, MatchMode.ANYWHERE));
        }
        criteria.addOrder(Order.asc("timePeriod.id"));
        return criteria.list();
    }

    /**
     * 根据指标ID查询指标结果
     * 
     * @author 金鹏祥
     * @param kpiId 指标ID
     * @param time 年度
     * @return List<KpiGatherResult>
     */
    public List<KpiGatherResult> findKpiGatherResultByKpiId(String kpiId, String time[]) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.in("timePeriod.id", time));
        return criteria.list();
    }

    /**
     * 根据指标ID、时间ID查询KpiFatherResult
     * 
     * @author 金鹏祥
     * @param kpiId
     *            指标ID
     * @param timePeriodId
     *            时间ID
     * @return List<KpiGatherResult>
     */
    public List<KpiGatherResult> findKpiGatherResultByKpiIdAndTimePeriodId(List<String> kpiId, boolean like, String timePeriodId) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.in("kpi.id", kpiId));
        if (!like) {
            criteria.add(Restrictions.eq("timePeriod.id", timePeriodId));
        }
        else {
            criteria.add(Property.forName("timePeriod.id").like(timePeriodId, MatchMode.ANYWHERE));
        }
        criteria.addOrder(Order.asc("timePeriod.id"));
        return criteria.list();
    }

    /**
     * 时间存放到MAP
     * 
     * @author 金鹏祥
     * @return HashMap<String, TimePeriod>
     */
    public Map<String, TimePeriod> findTimePeriodAllMap() {
        HashMap<String, TimePeriod> map = new HashMap<String, TimePeriod>();
        Criteria criteria = this.o_timePeriodDAO.createCriteria();
        criteria.setCacheable(true);
        List<TimePeriod> list = criteria.list();
        for (TimePeriod timePeriod : list) {
            map.put(timePeriod.getId(), timePeriod);
        }
        return map;
    }

   


    /**
     * 根据指标采集结果ID集合查询出采集结果集合.
     * 
     * @param idList
     *            指标采集结果id集合
     * @return List<KpiGatherResult>指标采集结果列表
     */
    public List<KpiGatherResult> findKpiGatherResultByIds(List<String> idList) {
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.in("id", idList));
        return criteria.list();
    }




    /**
     * 根据目标名称查询当期的指标采集结果
     * 
     * @param name
     *            目标名称
     * @return List<Objectp[]> 当期指标采集结果
     */
    public List<Object[]> findSmRelaCurrentResultBySmId(String name) {
        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        if (StringUtils.isNotBlank(name) && !"undefined".equals(name)) {
            StringBuffer selectBuf = new StringBuffer();
            StringBuffer fromLeftJoinBuf = new StringBuffer();
            StringBuffer wherebuf = new StringBuffer();
            List<Object> paralist = new ArrayList<Object>();
            selectBuf
                    .append(" select kpi.id  id, kpi.kpi_name  name , time.time_period_full_name  timerange , result.assessment_value  assessmentValue ,result.target_value  targetValue , result.finish_value  finishValue ,statusdict.dict_entry_value  status ,dirdict.dict_entry_value  direction ");
            fromLeftJoinBuf.append(" from t_kpi_kpi  kpi ");
            fromLeftJoinBuf.append(" left outer join ");
            fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result  result  ");
            fromLeftJoinBuf.append(" inner join ");
            fromLeftJoinBuf.append(" t_com_time_period  time on result.time_period_id=time.id  ");
            fromLeftJoinBuf.append(" ) on kpi.id=result.kpi_id and kpi.gather_frequence=time.etype ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
            fromLeftJoinBuf.append(" left outer join t_kpi_sm_rela_kpi  smrelakpi on smrelakpi.kpi_id=kpi.id ");
            fromLeftJoinBuf.append(" left outer join t_kpi_strategy_map  sm on sm.id=smrelakpi.strategy_map_id ");
            wherebuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI' and to_days(now())>=to_days(result.begin_time) and to_days(now())<= to_days(result.end_time) and 1=1 ");
            if (StringUtils.isNotBlank(name)) {
                wherebuf.append(" and sm.strategy_map_name=? ");
                paralist.add(name);
            }
            if (StringUtils.isNotBlank(companyid)) {
                wherebuf.append(" and kpi.company_id= ?");
                paralist.add(companyid);
            }
            Object[] paraobjects = new Object[paralist.size()];
            paraobjects = paralist.toArray(paraobjects);
            SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).toString(), paraobjects);
            list = sqlquery.list();
        }
        return list;
    }

    /**
     * 根据记分卡名称查询当期的指标采集结果
     * 
     * @param name
     *            记分卡名称
     * @return List<Objectp[]> 当期指标采集结果
     */
    public List<Object[]> findCategoryRelaCurrentResultBycategoryName(String name) {
        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        if (StringUtils.isNotBlank(name) && !"undefined".equals(name)) {
            StringBuffer selectBuf = new StringBuffer();
            StringBuffer fromLeftJoinBuf = new StringBuffer();
            StringBuffer wherebuf = new StringBuffer();
            List<Object> paralist = new ArrayList<Object>();
            selectBuf
                    .append(" select kpi.id  id, kpi.kpi_name  name , time.time_period_full_name  timerange , result.assessment_value  assessmentValue ,result.target_value  targetValue , result.finish_value  finishValue ,statusdict.dict_entry_value  status ,dirdict.dict_entry_value  direction ");
            fromLeftJoinBuf.append(" from t_kpi_kpi  kpi ");
            fromLeftJoinBuf.append(" left outer join ");
            fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result  result  ");
            fromLeftJoinBuf.append(" inner join ");
            fromLeftJoinBuf.append(" t_com_time_period  time on result.time_period_id=time.id  ");
            fromLeftJoinBuf.append(" ) on kpi.id=result.kpi_id and kpi.gather_frequence=time.etype ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
            fromLeftJoinBuf.append(" left outer t_kpi_kpi_rela_category  categoryrela on categoryrela.kpi_id=kpi.id ");
            fromLeftJoinBuf.append(" left outer join t_com_category  category on category.id=categoryrela.category_id ");
            wherebuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI' and to_days(now())>=to_days(result.begin_time) and to_days(now())<= to_days(result.end_time) and 1=1 ");
            if (StringUtils.isNotBlank(name)) {
                wherebuf.append(" and category.category_name=? ");
                paralist.add(name);
            }
            if (StringUtils.isNotBlank(companyid)) {
                wherebuf.append(" and kpi.company_id=? ");
                paralist.add(companyid);
            }
            Object[] paraobjects = new Object[paralist.size()];
            paraobjects = paralist.toArray(paraobjects);
            SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).toString(), paraobjects);
            list = sqlquery.list();
        }
        return list;
    }

    /**
     * 查询所有的指标相关信息.
     * 
     * @author 吴德福
     * @return List<Object[]>
     */
    public List<Object[]> findAllKpiRelaInfo() {
    	Map<String,String> model = new HashMap<String, String>();
    	String querySql = SqlBuilder.getSql("findAllKpiRelaInfo", model);
    	SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(querySql);
    	return sqlQuery.list();
    }

    
    /**根据记分卡id查询所关联的指标采集结果数据
     * @param categoryId 记分卡id
     * @param valueType 值类型
     * @param timePeriodId 时间区间纬度id
     * @return
     */
    public List<Object[]> findKpiGatherResultsListByCategoryId(String categoryId, String valueType, String timePeriodId) {
    	String dateStr = "";
    	String querySql = "";
    	if(StringUtils.isNotBlank(timePeriodId)){
    		TimePeriod timePeriod = o_timePeriodBO.findTimePeriodById(timePeriodId);
        	Date startTime = timePeriod.getStartTime();
        	dateStr = DateUtils.formatDate(startTime, "yyyy-MM-dd");
    	}else{
    		Date date = new Date();
            dateStr = DateUtils.formatDate(date, "yyyy-MM-dd");
    	}
    	Map<String,String> model = new HashMap<String, String>();
    	if("评估值".equals(valueType)){
    		querySql = SqlBuilder.getSql("findKpiGatherResultsAssessmentListByCategoryId", model);
    	}else if("实际值".equals(valueType)){
    		querySql = SqlBuilder.getSql("findKpiGatherResultsFinishListByCategoryId", model);
    	}
    	SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(querySql);
    	sqlQuery.setString("categoryId", categoryId);
    	sqlQuery.setString("date", dateStr);
    	return sqlQuery.list();
    }
    
    /**根据战略目标id查询所关联的指标采集结果数据
     * @param strategyId 战略目标id
     * @param valueType 值类型
     * @param timePeriodId 时间区间纬度id
     * @return
     */
    public List<Object[]> findKpiGatherResultsListByStrategyId(String strategyId, String valueType, String timePeriodId) {
    	String dateStr = "";
    	String querySql = "";
    	if(StringUtils.isNotBlank(timePeriodId)){
    		TimePeriod timePeriod = o_timePeriodBO.findTimePeriodById(timePeriodId);
        	Date startTime = timePeriod.getStartTime();
        	dateStr = DateUtils.formatDate(startTime, "yyyy-MM-dd");
    	}else{
    		Date date = new Date();
            dateStr = DateUtils.formatDate(date, "yyyy-MM-dd");
    	}
    	Map<String,String> model = new HashMap<String, String>();
    	if("评估值".equals(valueType)){
    		querySql = SqlBuilder.getSql("findKpiGatherResultsAssessmentListByStrategyId", model);
    	}else if("实际值".equals(valueType)){
    		querySql = SqlBuilder.getSql("findKpiGatherResultsFinishListByStrategyId", model);
    	}
    	SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(querySql);
    	sqlQuery.setString("strategyId", strategyId);
    	sqlQuery.setString("date", dateStr);
    	return sqlQuery.list();
    }
    
    /**根据实时指标名称查询最晚的采集结果
     * @param kpiName 指标名称
     * @return
     */
    public KpiGatherResult findRelaTimeKpiGatherResultBySome(String kpiName){
        DetachedCriteria dc= DetachedCriteria.forEntityName("com.fhd.entity.kpi.KpiGatherResult", "r");
        dc.createAlias("kpi", "kpi");
        dc.setProjection(Projections.max("endTime"));
        dc.add(Restrictions.isNotNull("assessmentStatus"));
        dc.add(Restrictions.eqProperty("re.kpi", "r.kpi"));
        
        Criteria criteria = this.o_kpiGatherResultDAO.getSession().createCriteria(KpiGatherResult.class, "re");
        criteria.createAlias("kpi", "kpi");
        criteria.add(Restrictions.eq("kpi.name", kpiName));
        criteria.add(Subqueries.propertyEq("re.endTime", dc));
        List<KpiGatherResult> list = criteria.list();
        if(null!=list&&list.size()>0){
            return list.get(0);
        }
        return null;
    }
    
    
    /**根据指标名称和时间区间维度id查询指标采集结果
     * @param kpiName 指标名称
     * @param timePeriod 时间区间纬度字符串
     * @return
     */
    public KpiGatherResult findKpiGatherResultBySome(String kpiName, String timePeriod) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "timePeriod");
        criteria.add(Restrictions.eq("kpi.name", kpiName));
        criteria.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        if (StringUtils.isNotBlank(timePeriod)) {
            TimePeriod timeperiod = o_timePeriodBO.findTimePeriodById(timePeriod);
            Date startTime = timeperiod.getStartTime();
            Date endTime = timeperiod.getEndTime();
            String timestr = sdf.format(endTime);
            try {
                criteria.add(Restrictions.ge("endTime",sdf.parse(timestr)));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            criteria.add(Restrictions.le("beginTime", startTime));
        }
        else {
            criteria.add(Restrictions.eqProperty("timePeriod", "kpi.lastTimePeriod"));
            //当前时间
            /*Date currentDate = new Date();
            criteria.add(Restrictions.ge("endTime", currentDate));
            criteria.add(Restrictions.le("beginTime", currentDate));*/
        }
        List<KpiGatherResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    
    /**根据指标名称和时间区间维度id查询指标采集结果
     * @param kpiName 指标名称
     * @param timePeriod 时间区间纬度字符串
     * @return
     */
    public KpiGatherResult findKpiGatherResultById(String kpiId, String timePeriod) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "timePeriod");
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        if (StringUtils.isNotBlank(timePeriod)) {
            
            criteria.add(Restrictions.eq("timePeriod.id", timePeriod));
        }
        else {//当前时间
            Date currentDate = new Date();
            criteria.add(Restrictions.ge("endTime", currentDate));
            criteria.add(Restrictions.le("beginTime", currentDate));
        }
        List<KpiGatherResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**根据记分卡名称和时间区间维度id查询度量指标采集结果
     * @param categoryName 记分卡名称
     * @param timePeriod 时间纬度字符串
     * @return
     */
    public List<KpiGatherResult> findKpiGatherResultListBySome(String categoryName, String timePeriod) {
        List<Kpi> kpiList = o_categoryBO.findChildKpiByCategoryName(categoryName);
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "timePeriod");
        criteria.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        if (StringUtils.isNotBlank(timePeriod)) {
            criteria.add(Restrictions.eq("timePeriod.id", timePeriod));
        }
        else {//当前时间
            criteria.add(Restrictions.eqProperty("timePeriod", "kpi.lastTimePeriod"));
            /*Date currentDate = new Date();
            criteria.add(Restrictions.ge("endTime", currentDate));
            criteria.add(Restrictions.le("beginTime", currentDate));*/
        }
        criteria.add(Restrictions.in("kpi", kpiList));
        return criteria.list();
    }
    
    /**根据时间范围查找采集结果
     * @param kpiId 指标ID
     * @param startTime 开始时间
     * @param endTime　结束时间
     * @return
     */
    public List<KpiGatherResult> findKpiGatherResultByTimeRange(String kpiId,Date startTime,Date endTime){
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "timePeriod");
        criteria.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.ge("endTime", startTime));
        criteria.add(Restrictions.le("endTime", endTime));
        criteria.addOrder(Order.asc("timePeriod"));
        return  criteria.list();
    }
    
    /**根据指标ID查询最大的采集频率时间区间维
     * @param kpiId 指标ID
     * @return
     */
    public TimePeriod findMaxKpiTimePeriod(String kpiId){
        DetachedCriteria dc= DetachedCriteria.forEntityName("com.fhd.entity.kpi.KpiGatherResult", "r");
        dc.createAlias("kpi", "kpi");
        dc.createAlias("timePeriod", "timePeriod");
        
        dc.setProjection(Projections.max("endTime"));
        dc.add(Restrictions.or(Restrictions.isNotNull("assessmentStatus"), 
        		Restrictions.or(Restrictions.isNotNull("assessmentValue"), 
        	    Restrictions.or(Restrictions.isNotNull("finishValue"), Restrictions.isNotNull("targetValue")))));
        dc.add(Restrictions.eqProperty("re.kpi", "r.kpi"));
        dc.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        
        Criteria criteria = this.o_kpiGatherResultDAO.getSession().createCriteria(KpiGatherResult.class, "re");
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "timePeriod");
        criteria.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Subqueries.propertyEq("re.endTime", dc));
        List<KpiGatherResult> list = criteria.list();
        if(null!=list&&list.size()>0){
            return list.get(0).getTimePeriod();
        }
        return null;
    }
    

    /**根据指标名称和前几期数值得到采集结果;
     * @param kpiName指标名称
     * @param num 前几期数值
     * @return
     */
    public KpiGatherResult findPreKpiGatherResultBySome(String kpiName, int num,String timePeriod) {
        KpiGatherResult result = findKpiGatherResultBySome(kpiName, timePeriod);
        if(null!=result){
            Date date = result.getEndTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String timestr = sdf.format(date);
            String[] timearr = timestr.split("/");
            String timePeriods = new StringBuffer().append(timearr[0]).append("mm").append(timearr[1]).toString();
            String frequence = result.getKpi().getGatherFrequence().getId();
            String convertTimePeriod = convertTimePeriodBySome(frequence, timePeriods, num);
            return findKpiGatherResultBySome(kpiName, convertTimePeriod);
        }else{
            return null;
        }
        
    }
    
    /**查询时间纬度
     * @param year 年度信息
     * @param frequence　频率
     * @param preNum　向过去推几期
     * @param afterNum　向未来推几期
     * @return
     */
    public  Map<String,Date> findTimeRange(String year,String frequence,int preNum,int afterNum){
        String stime = "";
        String etime = "";
        Map<String,Date> resultMap = new HashMap<String, Date>();
        TimePeriod timeperiod = null;
        if(StringUtils.isBlank(year)){
            Date date = new Date();
            year = DateUtils.getYear(date);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date startTime = null;
        Date endTime = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.valueOf(year));
        cal.set(Calendar.MONTH, Integer.valueOf(1) - 1);
        if ("0frequecy_month".equals(frequence)) {
            cal.add(Calendar.MONTH, -preNum);
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            stime = new StringBuffer().append(timearr[0]).append("mm").append(timearr[1]).toString();
            timeperiod = o_timePeriodBO.findTimePeriodById(stime);
            startTime = timeperiod.getStartTime();
            etime = year+"mm12";
            timeperiod = o_timePeriodBO.findTimePeriodById(etime);
            endTime = timeperiod.getEndTime();
        }else if("0frequecy_year".equals(frequence)){
            cal.add(Calendar.YEAR, -preNum);//preNum=1
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            stime = timearr[0];
            timeperiod = o_timePeriodBO.findTimePeriodById(stime);
            startTime = timeperiod.getStartTime();
            cal.add(Calendar.YEAR, preNum+afterNum);//afterNum=2
            timestr = sdf.format(cal.getTime());
            timestr = sdf.format(cal.getTime());
            timearr = timestr.split("/");
            timeperiod = o_timePeriodBO.findTimePeriodById(timearr[0]);
            endTime = timeperiod.getEndTime();
        }else if("0frequecy_quarter".equals(frequence)){
            cal.add(Calendar.MONTH, -preNum * 3);
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            Integer quarter = ((Integer.valueOf(timearr[1]) * 3) / 10) + 1;
            stime = new StringBuffer().append(timearr[0]).append("Q").append(quarter).toString();
            timeperiod = o_timePeriodBO.findTimePeriodById(stime);
            startTime = timeperiod.getStartTime();
            cal.add(Calendar.MONTH, preNum * 3+ afterNum*3);
            timestr = sdf.format(cal.getTime());
            timearr = timestr.split("/");
            quarter = ((Integer.valueOf(timearr[1]) * 3) / 10) + 1;
            etime = new StringBuffer().append(timearr[0]).append("Q").append(quarter).toString();
            timeperiod = o_timePeriodBO.findTimePeriodById(etime);
            endTime = timeperiod.getEndTime();
        }else if("0frequecy_halfyear".equals(frequence)){
            cal.add(Calendar.MONTH, -preNum * 6);
            String halfStr = "";
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            if (Integer.valueOf(timearr[1]) > 6) {
                halfStr = "1";
            }
            else {
                halfStr = "0";
            }
            stime = new StringBuffer().append(timearr[0]).append("hf").append(halfStr).toString();
            timeperiod = o_timePeriodBO.findTimePeriodById(stime);
            startTime = timeperiod.getStartTime();
            cal.add(Calendar.MONTH, preNum * 6+ afterNum*6);
            halfStr = "";
            timestr = sdf.format(cal.getTime());
            timearr = timestr.split("/");
            if (Integer.valueOf(timearr[1]) > 6) {
                halfStr = "1";
            }
            else {
                halfStr = "0";
            }
            etime = new StringBuffer().append(timearr[0]).append("hf").append(halfStr).toString();
            timeperiod = o_timePeriodBO.findTimePeriodById(etime);
            endTime = timeperiod.getEndTime();
            
        }else if("0frequecy_week".equals(frequence)){
        	String timePeriodId = new StringBuffer().append(year).toString();
        	timeperiod = o_timePeriodBO.findTimePeriodById(timePeriodId);
        	startTime = timeperiod.getStartTime();
        	endTime = timeperiod.getEndTime();
        }
        resultMap.put("startDate", startTime);
        resultMap.put("endDate", endTime);
        return resultMap;
    }
    
    /**查询最近的我所属的部门的指标采集结果
     * @param deptId
     * @param deptType
     * @param start
     * @param limit
     * @param queryName
     * @param sortColumn
     * @param dir
     * @return
     */
    public List<Object[]> findLastKpiGatherByBelongDeptId(Map<String, Object> map,String deptId,String deptType,int start, int limit, String queryName, String sortColumn, String dir){
        List<Object[]> list = null;
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromLeftJoinBuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        List<Object> paralist = new ArrayList<Object>();
        StringBuffer statusColumn = new StringBuffer();
        statusColumn.append(" case when statusdict.dict_entry_value is null then ");
        if(dir.equals("ASC")){
        	statusColumn.append(" 'nothing' "); 
        }else{
        	statusColumn.append(" 'anothing' ");
        }
        statusColumn.append(" else statusdict.dict_entry_value end status ");
        selectBuf.append(" select kpi.id  id, kpi.kpi_name  name , time.time_period_full_name  timerange , result.assessment_value  assessmentValue ,result.target_value  targetValue , result.finish_value  finishValue ,");
        selectBuf.append(statusColumn);
        selectBuf.append(",dirdict.dict_entry_value  direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid,kpi.is_focus  ");
        selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence ,kpi.escale  escale ");
        countBuf.append(" select count(*) ");

        fromLeftJoinBuf.append(" from t_kpi_kpi  kpi");
        fromLeftJoinBuf.append(" inner join t_kpi_kpi_rela_org_emp emp on emp.kpi_id=kpi.id ");
        fromLeftJoinBuf.append(" left outer join t_kpi_kpi_gather_result  result on kpi.id=result.kpi_id and kpi.latest_time_period_id=result.time_period_id ");
        fromLeftJoinBuf.append(" left outer join t_com_time_period  time on result.time_period_id=time.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
        fromLeftJoinBuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI'  and 1=1 ");


        if (StringUtils.isNotBlank(queryName)) {
            fromLeftJoinBuf.append(" and kpi.kpi_name like '%").append(queryName).append("%'");
        }

        fromLeftJoinBuf.append(" and emp.org_id=? ");
        paralist.add(deptId);
        fromLeftJoinBuf.append(" and emp.etype=? ");
        paralist.add(deptType);
        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);
        SQLQuery countQuery = o_kpiGatherResultDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).toString(), paraobjects);
        map.put("totalCount", countQuery.uniqueResult());

        if (StringUtils.isNotBlank(sortColumn)) {
            orderbuf.append(" order by ");
            if (StringUtils.equals("name", sortColumn)) {
                orderbuf.append("kpi.kpi_name");
            }
            else if (StringUtils.equals("id", sortColumn)) {
                orderbuf.append("kpi.id");
            }
            else if (StringUtils.equals("finishValue", sortColumn)) {
                orderbuf.append("finishValue");
            }
            else if (StringUtils.equals("targetValue", sortColumn)) {
                orderbuf.append("targetValue");
            }
            else if (StringUtils.equals("assessmentValue", sortColumn)) {
                orderbuf.append("assessmentValue");
            }
            else if (StringUtils.equals("assessmentStatus", sortColumn)) {
                orderbuf.append("status");
            }
            else if (StringUtils.equals("directionstr", sortColumn)) {
                orderbuf.append("direction");
            }
            else if (StringUtils.equals("dateRange", sortColumn)) {
                orderbuf.append("timerange");
            }
           
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(" asc ");
            }
            else {
                orderbuf.append(" desc ");
            }
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(",kpi.kpi_name asc ");
            }else{
                orderbuf.append(",kpi.kpi_name desc ");
            }
        }

        SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(orderbuf).toString(), paraobjects);
        sqlquery.setFirstResult(start);
        sqlquery.setMaxResults(limit);
        list = sqlquery.list();
        return list;
    }
    
    /**查询具体时间段我所属的部门的指标采集结果
     * @param map
     * @param deptId
     * @param deptType
     * @param start
     * @param limit
     * @param queryName
     * @param sortColumn
     * @param dir
     * @param year
     * @param quarter
     * @param month
     * @param week
     * @param frequence
     * @return
     */
    public List<Object[]> findSpecificKpiGatherByBelongDeptId(Map<String, Object> map,String deptId,String deptType,int start, int limit, String queryName, String sortColumn, String dir,String year, String quarter, String month, String week, String frequence){
    	
    	List<Object[]> list = null;
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromLeftJoinBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        List<Object> paralist = new ArrayList<Object>();
        StringBuffer statusColumn = new StringBuffer();
        statusColumn.append(" case when statusdict.dict_entry_value is null then ");
        if(dir.equals("ASC")){
        	statusColumn.append(" 'nothing' "); 
        }else{
        	statusColumn.append(" 'anothing' ");
        }
        statusColumn.append(" else statusdict.dict_entry_value end status ");
        selectBuf.append(" select kpi.id  id, kpi.kpi_name  name , time.time_period_full_name  timerange , result.assessment_value  assessmentValue ,result.target_value  targetValue , result.finish_value  finishValue , ");
        selectBuf.append(statusColumn);
        selectBuf.append(" ,dirdict.dict_entry_value  direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid ,kpi.is_focus ");
        selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence ,kpi.escale  escale ");
        countBuf.append(" select count(*) ");
        fromLeftJoinBuf.append(" from t_kpi_kpi   kpi ");
        fromLeftJoinBuf.append(" inner join t_kpi_kpi_rela_org_emp emp on emp.kpi_id=kpi.id ");
        fromLeftJoinBuf.append(" left outer join ");
        fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result result  ");
        fromLeftJoinBuf.append(" inner join ");
        fromLeftJoinBuf.append(" t_com_time_period   time on result.time_period_id=time.id  ");
        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) && StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
            fromLeftJoinBuf.append("  and time.id=?");
            paralist.add(year);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(month)) {
            fromLeftJoinBuf.append("  and time.id=?");
            paralist.add(quarter);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
            fromLeftJoinBuf.append("  and time.id=?");
            paralist.add(month);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isNotBlank(week)) {
            fromLeftJoinBuf.append("  and time.id=?");
            paralist.add(week);
        }
        fromLeftJoinBuf.append(" ) on kpi.id=result.kpi_id and kpi.gather_frequence=time.etype ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  frequencedict on kpi.gather_frequence = frequencedict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
        wherebuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI' and 1=1 ");
        if (StringUtils.isNotBlank(queryName)) {
            wherebuf.append(" and kpi.kpi_name like '%").append(queryName).append("%'");
        }
        wherebuf.append(" and emp.org_id=? ");
        paralist.add(deptId);
        wherebuf.append(" and emp.etype=? ");
        paralist.add(deptType);
        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);
        SQLQuery countQuery = o_kpiGatherResultDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).append(wherebuf).toString(), paraobjects);
        map.put("totalCount", countQuery.uniqueResult());

        if (StringUtils.isNotBlank(sortColumn)) {
            orderbuf.append(" order by ");
            if (StringUtils.equals("name", sortColumn)) {
                orderbuf.append("kpi.kpi_name");
            }
            else if (StringUtils.equals("id", sortColumn)) {
                orderbuf.append("kpi.id");
            }
            else if (StringUtils.equals("finishValue", sortColumn)) {
                orderbuf.append("finishValue");
            }
            else if (StringUtils.equals("targetValue", sortColumn)) {
                orderbuf.append("targetValue");
            }
            else if (StringUtils.equals("assessmentValue", sortColumn)) {
                orderbuf.append("assessmentValue");
            }
            else if (StringUtils.equals("assessmentStatus", sortColumn)) {
                orderbuf.append("status");
            }
            else if (StringUtils.equals("directionstr", sortColumn)) {
                orderbuf.append("direction");
            }
            else if (StringUtils.equals("dateRange", sortColumn)) {
                orderbuf.append("timerange");
            }
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(" asc ");
            }
            else {
                orderbuf.append(" desc ");
            }
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(",kpi.kpi_name asc ");
            }else{
                orderbuf.append(",kpi.kpi_name desc ");
            }
        }

        SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).append(orderbuf).toString(), paraobjects);
        sqlquery.setFirstResult(start);
        sqlquery.setMaxResults(limit);
        list = sqlquery.list();
        return list;
    }
    
    /**
     * 根据目标ID查询所关联的指标所在年的采集结果
     * 
     * @param smId
     *            目标ID
     * @param year
     *            年分
     * @return Map<Kpi, List<Object[]>>
     *         object[]数据中的数据顺序{name指标名称,frequence频率,lasttimeperiod最后更新时间区间维
     *         ,timeperiod时间区间维
     *         ,starttime开始时间,endtime结束时间,finishvalue实际值,status告警状态,direction趋势}
     */
    public Map<String, List<Object[]>> findKpiGatherResultsBySmId(String smId, String name, String year) {
        Map<String, List<Object[]>> map = new HashMap<String, List<Object[]>>();
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromLeftJoinBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        List<Object> paralist = new ArrayList<Object>();
        selectBuf
                .append(" select kpi.id  id, kpi.kpi_name  name , kpi.gather_frequence  frequence ,kpi.latest_time_period_id  latesttimeperiod, time.id  timeperiod,time.start_time  starttime,time.end_time  endtime ,result.finish_value  finishValue ,statusdict.dict_entry_value  status ,dirdict.dict_entry_value  direction ");
        fromLeftJoinBuf.append(" from t_kpi_kpi  kpi ");
        fromLeftJoinBuf.append(" left outer join ");
        fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result  result  ");
        fromLeftJoinBuf.append(" inner join ");
        fromLeftJoinBuf.append(" t_com_time_period  time on result.time_period_id=time.id  ");
        if (StringUtils.isNotBlank(year)) {
            fromLeftJoinBuf.append("  and time.eyear=?");
            paralist.add(year);
        }
        else {
        	//String currentYear = DateUtils.getYear(new Date());
            //fromLeftJoinBuf.append(" and time.eyear='").append(currentYear).append("' ");
            fromLeftJoinBuf.append(" and time.eyear=(select t.EYEAR from t_com_time_period t where t.id=kpi.latest_time_period_id ) ");
        }
        fromLeftJoinBuf.append(" ) on kpi.id=result.kpi_id and kpi.gather_frequence=time.etype ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_kpi_sm_rela_kpi  sm on sm.kpi_id=kpi.id ");
        wherebuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI'  and 1=1 ");

        if (StringUtils.isNotBlank(smId)) {
            wherebuf.append(" and sm.strategy_map_id=? ");
            paralist.add(smId);
        }
        if (StringUtils.isNotBlank(companyid)) {
            wherebuf.append(" and kpi.company_id=? ");
            paralist.add(companyid);
        }

        if (StringUtils.isNotBlank(name)) {
            wherebuf.append(" and kpi.kpi_name like '%").append(name).append("%'");
        }
        orderbuf.append(" order by name,starttime asc ");

        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);

        SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).append(orderbuf).toString(),
                paraobjects);
        List<Object[]> list = sqlquery.list();
        if (null != list) {
            for (Object[] objects : list) {
                String kpiId = (String) objects[0];
                String kpiName = (String) objects[1];
                String frequence = (String) objects[2];
                Object lasttimeperiod = objects[3];// 最后更新时间区间维ID
                String timeperiod = (String) objects[4];// 时间区间维ID
                Object starttime = objects[5];// 开始时间
                Object endtime = objects[6];// 结束时间
                Object finishvalue = objects[7];// 完成值
                String status = (String) objects[8];// 告警状态
                String direction = (String) objects[9];// 趋势
                String key = kpiName + "@" + frequence;
                Object[] valueObjects = new Object[] { kpiId, kpiName, frequence, lasttimeperiod, timeperiod, starttime, endtime, finishvalue,
                        status, direction };
                if (!map.containsKey(key)) {
                    List<Object[]> valueList = new ArrayList<Object[]>();
                    valueList.add(valueObjects);
                    map.put(key, valueList);
                }
                else {
                    map.get(key).add(valueObjects);
                }
            }
        }
        Map<String, List<Object[]>> resultmap = new HashMap<String, List<Object[]>>();
        for (Map.Entry<String, List<Object[]>> m : map.entrySet()) {
            String key = m.getKey();
            List<Object[]> values = m.getValue();
            String[] arr = key.split("@");
            String kpiName = arr[0];
            resultmap.put(kpiName, values);
        }
        return resultmap;
    }
    
    
    /**
     * <pre>
     * 根据指标ID查询采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId
     *            指标ID
     * @return
     * @since fhd　Ver 1.1
     */
    public Map<String, String> findMapKpiGatherResultListByKpiId(String kpiId, String year) {
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.like("timePeriod.id", "%" + year + "%"));
        List<KpiGatherResult> list = criteria.list();
        HashMap<String, String> map = new HashMap<String, String>();
        for (KpiGatherResult kpiGatherResult : list) {
            map.put(kpiGatherResult.getTimePeriod().getId(), kpiGatherResult.getTimePeriod().getId() + "," + kpiGatherResult.getFinishValue() + ","
                    + kpiGatherResult.getTargetValue() + "," + kpiGatherResult.getAssessmentValue());
        }
        return map;
    }
    
    /**
     * 根据查询结果ID和指标名称查询采集结果
     * @param id
     * @param query
     * @return 查询
     */
    
    public KpiGatherResult findKpiGatherResultByKpiNameAndId(String id,String query) {
		Criteria criteria = o_kpiGatherResultDAO.createCriteria();
		criteria.createAlias("kpi", "kpi");
		criteria.add(Restrictions.eq("id", id));
		criteria.add(Restrictions.like("kpi.name", query,MatchMode.ANYWHERE));
		List<KpiGatherResult> list = criteria.list();
		if(list.size() > 0) {
			return (KpiGatherResult) list.get(0);
		}
		return null;
    	
    } 

    /**根据指标名称和公司ID查询指标采集结果
     * @param kpiName 指标名称
     * @param companyId 公司ID
     * @return
     */
    public List<KpiGatherResult> findKpiGatherResultByKpiName(String kpiName,String companyId,Date startTime,Date endTime){
    	Criteria criteria = o_kpiGatherResultDAO.createCriteria();
    	criteria.createAlias("kpi", "kpi");
    	criteria.createAlias("timePeriod", "time");
    	criteria.setFetchMode("time", FetchMode.JOIN);
    	criteria.add(Restrictions.eqProperty("time.type", "kpi.gatherFrequence.id"));
    	criteria.add(Restrictions.eq("kpi.name", kpiName));
    	criteria.add(Restrictions.ge("endTime", startTime));
        criteria.add(Restrictions.le("endTime", endTime));
    	criteria.add(Restrictions.eq("kpi.deleteStatus", true));
    	criteria.add(Restrictions.eq("company.id", companyId));
    	criteria.addOrder(Order.asc("beginTime"));
    	return criteria.list();
    }
    
    /**根据指标名称和公司ID查询指标采集结果
     * @param kpiName 指标名称
     * @param companyId 公司ID
     * @return
     */
    public List<KpiGatherResult> findKpiGatherResultByKpiName(String kpiName,String companyId,String yearId){
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "time");
        criteria.setFetchMode("time", FetchMode.JOIN);
        criteria.add(Restrictions.eqProperty("time.type", "kpi.gatherFrequence.id"));
        criteria.add(Restrictions.eq("kpi.name", kpiName));
        if(StringUtils.isNotBlank(yearId)){
        	criteria.add(Restrictions.eq("time.year", yearId));
        }else{
        	Kpi kpi = o_kpiBO.findKpiByName(kpiName);
        	TimePeriod lastTimePeriod = kpi.getLastTimePeriod();
        	if(null!=lastTimePeriod){
        		String year = lastTimePeriod.getYear();
        		criteria.add(Restrictions.eq("time.year",year));
        	}else{
        		criteria.add(Restrictions.eq("time.year", DateUtils.getYear(new Date())));
        	}
        }
        criteria.add(Restrictions.eq("kpi.deleteStatus", true));
        criteria.add(Restrictions.eq("company.id", companyId));
        criteria.addOrder(Order.asc("beginTime"));
        return criteria.list();
    }
    
    
    /**根据指标名称和公司ID查询指标最后更新的采集结果
     * @param isFocus 是否关注
     * @param companyId 公司ID
     * @param isPage 是否分页
     * @param start 起始位置
     * @param limit 分页大小
     * @return
     */
    public Map<String,Object> findLastKpiGatherResult(String companyId,boolean isFocus,boolean isPage, int start,int limit,String query){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        List<KpiGatherResult> resultList = new ArrayList<KpiGatherResult>();
        StringBuffer countSql = new StringBuffer();
        // SQL检索用参数名称/参数值 Map
        Map<String, Object> param = new HashMap<String, Object>();
        countSql.append(" select count(*) ");
        StringBuffer sql = new StringBuffer();
        StringBuffer fromsql = new StringBuffer();
        StringBuffer wheresql = new StringBuffer();
        sql.append(" select k.id,kresult.ASSESSMENT_STATUS,kresult.ASSESSMENT_VALUE ");
        fromsql.append(" from t_kpi_kpi k LEFT OUTER join t_kpi_kpi_gather_result kresult on k.id=kresult.KPI_ID   ");
        wheresql.append(" where k.LATEST_TIME_PERIOD_ID=kresult.TIME_PERIOD_ID and k.delete_status= :DELETE_STATUS ");
        param.put("DELETE_STATUS", 1);
        if(isFocus){
            wheresql.append(" and k.is_focus= :IS_FOCUS  ");
            param.put("IS_FOCUS", Contents.DICT_Y);
        }
        if(StringUtils.isNotBlank(query)){
            wheresql.append(" and k.kpi_name like :QUERY");
            param.put("QUERY", "%" + query + "%");
        }
        if(StringUtils.isNotBlank(companyId)){
        	 wheresql.append(" and k.company_id= :COMPANY_ID");
        	 param.put("COMPANY_ID", companyId);
        }
        wheresql.append(" and kresult.ASSESSMENT_VALUE is not null ");
        wheresql.append("  order by kresult.ASSESSMENT_VALUE ");
        SQLQuery sqlcountquery = o_kpiGatherResultDAO.createSQLQuery(countSql.append(fromsql).append(wheresql).toString(),param);
        resultMap.put("totalCount", sqlcountquery.uniqueResult());
        SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(sql.append(fromsql).append(wheresql).toString(),param);
        if(isPage){
            sqlquery.setFirstResult((start - 1) * limit);// 设置分页
            sqlquery.setMaxResults(limit);
        }
       
        List<Object[]> list = sqlquery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            KpiGatherResult result = new KpiGatherResult();
            Object[] objects = (Object[]) iterator.next();
            
            if(null!=objects[0]){
                result.setKpi(o_kpiBO.findKpiById(String.valueOf(objects[0])));
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
     * 根据指标id查询指标当期采集结果.
     * 
     * @param kpiId
     *            指标id
     * @param timePeriodId
     *            时间区间维id
     * @return KpiGatherResult
     */
    public KpiGatherResult findCurrentKpiGatherResultByKpiId(String kpiId, String timePeriodId) {

        KpiGatherResult gatherResult = null;

        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "time");

        criteria.add(Restrictions.eqProperty("time.type", "kpi.gatherFrequence.id"));
        criteria.add(Restrictions.eq("kpi.id", kpiId));

        if (StringUtils.isNotBlank(timePeriodId)) {
            criteria.add(Restrictions.eq("time.id", timePeriodId));
        }
        else {
            // 取当前时间
            Date currentDate = new Date();
            criteria.add(Restrictions.and(Restrictions.le("time.startTime", currentDate), Restrictions.gt("time.endTime", currentDate)));
        }
        List<KpiGatherResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            gatherResult = list.get(0);
        }
        return gatherResult;
    }
    /**
     * 根据指标id查询指标当期采集结果.
     * 
     * @param kpiId
     *            指标id
     * @param timePeriodId
     *            时间区间维id
     * @return KpiGatherResult
     */
    public List<KpiGatherResult> findCurrentKpiGatherResultByKpiIds(String kpiId, String timePeriodId) {
    	
    	Criteria criteria = o_kpiGatherResultDAO.createCriteria();
    	criteria.createAlias("kpi", "kpi");
    	criteria.createAlias("timePeriod", "time");
    	criteria.add(Restrictions.eqProperty("time.type", "kpi.gatherFrequence.id"));
    	criteria.add(Restrictions.eq("kpi.id", kpiId));
    	
    	if (StringUtils.isNotBlank(timePeriodId)) {
    		criteria.add(Restrictions.eq("time.id", timePeriodId));
    	}
    	else {
    		// 取当前时间
    		Date currentDate = new Date();
    		criteria.add(Restrictions.and(Restrictions.le("time.startTime", currentDate), Restrictions.gt("time.endTime", currentDate)));
    	}
    	criteria.addOrder(Order.asc("time.id"));
    	return criteria.list();
    }

    /**
     * 根据指标id查询指标上期采集结果.
     * 
     * @param kpiId
     *            指标id
     * @param timePeriodId
     *            时间区间维id
     * @return KpiGatherResult
     */
    public KpiGatherResult findPreviousKpiGatherResultByKpiId(String kpiId, String timePeriodId) {

        KpiGatherResult gatherResult = null;

        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.createAlias("timePeriod", "time");

        criteria.add(Restrictions.eqProperty("time.type", "kpi.gatherFrequence.id"));
        criteria.add(Restrictions.eq("kpi.id", kpiId));

        if (StringUtils.isNotBlank(timePeriodId)) {
            criteria.add(Restrictions.eq("time.nextPeriod.id", timePeriodId));
        }
        else {
            // 取当前时间
            Date currentDate = new Date();
            criteria.add(Restrictions.and(Restrictions.le("time.startTime", currentDate), Restrictions.gt("time.endTime", currentDate)));
        }
        List<KpiGatherResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            gatherResult = list.get(0);
        }
        return gatherResult;
    }
    /**
     * 根据指标id查询指标下期采集结果.
     * 
     * @param kpiId
     *            指标id
     * @param timePeriodId
     *            时间区间维id
     * @return KpiGatherResult
     */
    public KpiGatherResult findNextKpiGatherResultByKpiId(String kpiId, String timePeriodId) {
    	
    	KpiGatherResult gatherResult = null;
    	
    	Criteria criteria = o_kpiGatherResultDAO.createCriteria();
    	criteria.createAlias("kpi", "kpi");
    	criteria.createAlias("timePeriod", "time");
    	
    	criteria.add(Restrictions.eqProperty("time.type", "kpi.gatherFrequence.id"));
    	criteria.add(Restrictions.eq("kpi.id", kpiId));
    	
    	if (StringUtils.isNotBlank(timePeriodId)) {
    		criteria.add(Restrictions.eq("time.prePeriod.id", timePeriodId));
    	}
    	else {
    		// 取当前时间
    		Date currentDate = new Date();
    		criteria.add(Restrictions.and(Restrictions.le("time.startTime", currentDate), Restrictions.gt("time.endTime", currentDate)));
    	}
    	List<KpiGatherResult> list = criteria.list();
    	if (null != list && list.size() > 0) {
    		gatherResult = list.get(0);
    	}
    	return gatherResult;
    }

    /**
     * 根据指标id查询指标当期采集结果所在的季度指标采集结果(采集频率为月份).
     * 
     * @param kpiId
     *            指标id
     * @param timePeriod
     *            时间区间维id
     * @return KpiGatherResult
     */
    public KpiGatherResult findQuarterKpiGatherResultByKpiId(String kpiId, String timePeriod) {
        KpiGatherResult gatherResult = null;
        KpiGatherResult currentGatherResult = findCurrentKpiGatherResultByKpiId(kpiId, timePeriod);
        if (null != currentGatherResult) {
            TimePeriod parentTimePeriod = currentGatherResult.getTimePeriod().getParent();
            Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
            criteria.add(Restrictions.eq("kpi.id", kpiId));
            criteria.add(Restrictions.eq("timePeriod", parentTimePeriod));
            List<KpiGatherResult> list = criteria.list();
            if (null != list && list.size() > 0) {
                gatherResult = list.get(0);
            }

        }
        return gatherResult;
    }

    /**
     * 根据指标id查询指标当期采集结果所在的年度指标采集结果(采集频率不包括年).
     * 
     * @param kpiId
     *            指标id
     * @param timePeriod
     *            时间区间维id
     * @return KpiGatherResult
     */
    public KpiGatherResult findYearKpiGatherResultByKpiId(String kpiId, String timePeriod) {
        KpiGatherResult gatherResult = null;
        KpiGatherResult currentGatherResult = findCurrentKpiGatherResultByKpiId(kpiId, timePeriod);
        if (null != currentGatherResult) {
            String year = currentGatherResult.getTimePeriod().getYear();
            Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
            criteria.add(Restrictions.eq("kpi.id", kpiId));
            criteria.add(Restrictions.eq("timePeriod.id", year));
            List<KpiGatherResult> list = criteria.list();
            if (null != list && list.size() > 0) {
                gatherResult = list.get(0);
            }
        }
        return gatherResult;
    }

    /**
     * 根据指标id查询指标所有采集结果.
     * 
     * @param kpiId
     *            指标id
     * @return List<KpiGatherResult>
     */
    public List<KpiGatherResult> findKpiGatherResultListByKpiId(String kpiId) {
        Kpi kpi = o_kpiBO.findKpiById(kpiId);
        Set<KpiGatherResult> list = kpi.getKpiGatherResult();
        return new ArrayList<KpiGatherResult>(list);
    }

    /**
     * 根据指标名称集合查询对应的当期指标采集结果.
     * 
     * @param nameList
     *            名称集合
     * @param timePeriodId
     *            时间区间维id
     * @return List<KpiGatherResult>
     */
    public List<KpiGatherResult> findKpiGatherResultListByKpiNames(List<String> nameList, String timePeriodId) {
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "k");
        criteria.createAlias("k.gatherFrequence", "g");
        criteria.createAlias("timePeriod", "t");
        criteria.add(Restrictions.in("k.name", nameList));
        criteria.add(Restrictions.eqProperty("t.type", "g.id"));
        if (StringUtils.isNotBlank(timePeriodId)) {
            criteria.add(Restrictions.eq("t.id", timePeriodId));
        }
        else {
            Date currentDate = new Date();
            criteria.add(Restrictions.le("t.startTime", currentDate));
            criteria.add(Restrictions.ge("t.endTime", currentDate));
        }

        return criteria.list();
    }
    
    /**根据指标名称集合查询对应的当期指标采集结果.
     * @param nameList 指标名称集合
     * @param timePeriodId 时间区间id
     * @param companyId  公司id
     * @return
     */
    public List<KpiGatherResult> findKpiGatherResultListByKpiNames(List<String> nameList, String timePeriodId,String companyId) {
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "k");
        criteria.createAlias("k.company", "kcompany");
        criteria.createAlias("k.gatherFrequence", "g");
        criteria.createAlias("timePeriod", "t");
        
        criteria.add(Restrictions.in("k.name", nameList));
        criteria.add(Restrictions.eq("kcompany.id", companyId));
        criteria.add(Restrictions.eq("company.id", companyId));
        criteria.add(Restrictions.eqProperty("t.type", "g.id"));
        criteria.add(Restrictions.eq("k.deleteStatus",true));
        if (StringUtils.isNotBlank(timePeriodId)) {
            criteria.add(Restrictions.eq("t.id", timePeriodId));
        }
        else {
            Date currentDate = new Date();
            criteria.add(Restrictions.le("t.startTime", currentDate));
            criteria.add(Restrictions.ge("t.endTime", currentDate));
        }
        
        return criteria.list();
    }

    /**
     * <pre>
     * 根据指标ID查询当前年的采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId
     *            指标ID
     * @return
     * @since fhd　Ver 1.1
     */
    public List<KpiGatherResult> findCurrentYearKpiGatherResultByKpiId(String kpiId, String year) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timeperiod");
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.eq("timeperiod.year", year));
        return criteria.list();
    }

    /**根据指标ID和时间区间纬度查询
     * @param kpiid 指标ID
     * @param timeperiod 时间区间纬度
     * @return
     */
    public KpiGatherResult findKpiGatherResultByIdAndTimeperiod(String kpiid, TimePeriod timeperiod) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timeperiod");
        criteria.add(Restrictions.eq("kpi.id", kpiid));
        criteria.add(Restrictions.eq("timePeriod", timeperiod));
        List<KpiGatherResult> results = criteria.list();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    /**
     * <pre>
     * 根据指标ID查询当前年的采集结果,根据频率
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId指标ID
     * @param frequence 采集频率
     * @return
     * @since fhd　Ver 1.1
     */
    public List<KpiGatherResult> findCurrentYearKpiGatherResultByKpiIdAndFrequence(String kpiId, String year, String frequence) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timeperiod");
        criteria.setFetchMode("timeperiod", FetchMode.JOIN);
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.eq("timeperiod.year", year));
        criteria.add(Restrictions.eq("timeperiod.type", frequence));
        criteria.addOrder(Order.asc("timePeriod"));
        return criteria.list();
    }
    /**
     * <pre>
     * 根据指标ID集合查询当前年的采集结果,根据频率
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId指标ID
     * @param frequence 采集频率
     * @return
     * @since fhd　Ver 1.1
     */
    public List<KpiGatherResult> findCurrentYearKpiGatherResultByKpiIdsAndFrequence(List<String> kpiIds, String year, String frequence) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timeperiod");
        criteria.createAlias("kpi", "kpi");
        criteria.setFetchMode("timeperiod", FetchMode.JOIN);
        criteria.setFetchMode("kpi", FetchMode.JOIN);
        criteria.add(Restrictions.in("kpi.id", kpiIds));
        criteria.add(Restrictions.eq("timeperiod.year", year));
        criteria.add(Restrictions.eq("timeperiod.type", frequence));
        criteria.addOrder(Order.asc("kpi.id"));
        criteria.addOrder(Order.asc("timePeriod"));
        return criteria.list();
    }

    /**
     * <pre>
     * 根据指标ID查询年的采集结果,根据频率
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId指标ID
     * @param frequence 采集频率
     * @param years 采集结果所在年
     * @return
     * @since fhd　Ver 1.1
     */
    public List<KpiGatherResult> findYearsKpiGatherResultByKpiIdAndFrequence(String kpiId, List<String> years, String frequence) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timeperiod");
        criteria.setFetchMode("timeperiod", FetchMode.JOIN);
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.in("timeperiod.year", years));
        criteria.add(Restrictions.eq("timeperiod.type", frequence));
        criteria.addOrder(Order.asc("timePeriod"));
        return criteria.list();
    }
    /**
     * <pre>
     * 根据指标ID查询年的采集结果,根据频率
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId指标ID
     * @param frequence 采集频率
     * @param years 采集结果所在年
     * @return
     * @since fhd　Ver 1.1
     */
    public List<KpiGatherResult> findYearsKpiGatherResultByKpiNameAndFrequence(String name, List<String> years, String frequence) {
        Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timeperiod");
        criteria.createAlias("kpi", "kpi");
        criteria.setFetchMode("timeperiod", FetchMode.JOIN);
        criteria.add(Restrictions.eq("kpi.name", name));
        criteria.add(Restrictions.in("timeperiod.year", years));
        criteria.add(Restrictions.eq("timeperiod.type", frequence));
        criteria.addOrder(Order.asc("timePeriod"));
        return criteria.list();
    }

    /**
     * 根据指标名称集合查询对应的当前月份之前的采集结果.
     * 
     * @param nameList
     *            指标名称集合
     * @return Map<String, List<KpiGatherResult>>
     */
    public Map<String, List<KpiGatherResult>> findKpiGatherResultPreMonthListByKpiNames(List<String> nameList, String month) {
        String year = DateUtils.getYear(new Date());
        Map<String, List<KpiGatherResult>> result = new HashMap<String, List<KpiGatherResult>>();
        if (null != nameList && nameList.size() > 0) {
            Criteria criteria = this.o_kpiGatherResultDAO.createCriteria();
            criteria.createAlias("kpi", "kpi");
            criteria.createAlias("timePeriod", "timeperiod");
            criteria.add(Restrictions.in("kpi.name", nameList));
            criteria.add(Restrictions.eq("timeperiod.type", "0frequecy_month"));
            criteria.add(Restrictions.eq("timeperiod.year", year));
            criteria.add(Restrictions.le("timeperiod.month", month));
            List<KpiGatherResult> resultList = criteria.list();
            for (KpiGatherResult kpiGatherResult : resultList) {
                String kpiName = kpiGatherResult.getKpi().getName();
                if (!result.containsKey(kpiName)) {
                    List<KpiGatherResult> rlist = new ArrayList<KpiGatherResult>();
                    rlist.add(kpiGatherResult);
                    result.put(kpiName, rlist);
                }
                else {
                    result.get(kpiName).add(kpiGatherResult);
                }
            }
        }
        return result;
    }

    /**
     * <pre>
     * 根据指标ID查询采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId
     *            指标ID
     * @return
     * @since fhd　Ver 1.1
     */
    public Map<String, KpiGatherResult> findMapKpiGatherResultListByKpiId(String kpiId) {
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        List<KpiGatherResult> list = criteria.list();
        HashMap<String, KpiGatherResult> map = new HashMap<String, KpiGatherResult>();
        for (KpiGatherResult kpiGatherResult : list) {
            if(null!=kpiGatherResult.getTimePeriod()){
                map.put(kpiGatherResult.getTimePeriod().getId(), kpiGatherResult);
            }
        }
        return map;
    }
    
    
    /**根据指标id集合查询所对应的采集数据 
     * @param kpiIdList指标id集合
     * @return key:指标id value:指标所对应的采集结果对象集合
     */
    public Map<String,List<KpiGatherResult>> findMapKpiGatherResultListByKpiIdList(List<String> kpiIdList,String year){
        Map<String,List<KpiGatherResult>> resultMap = new HashMap<String, List<KpiGatherResult>>();
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "timePeriod");
        criteria.add(Restrictions.in("kpi.id", kpiIdList));
        criteria.add(Restrictions.eq("timePeriod.year", year));
        criteria.addOrder(Order.asc("kpi.id"));
        List<KpiGatherResult> resultList = criteria.list();
        for (KpiGatherResult kpiGatherResult : resultList)
        {
            String kpiId = kpiGatherResult.getKpi().getId();
            if(!resultMap.containsKey(kpiId)){
                List<KpiGatherResult> childResultList = new ArrayList<KpiGatherResult>();
                resultMap.put(kpiId, childResultList);
            }
            resultMap.get(kpiId).add(kpiGatherResult);
        }
        return resultMap;
    }

    /**
     * 根据时间查询对应VALUE
     * 
     * @author 金鹏祥
     * @param whereIn 时间集合   
     * @return List<KpiGatherResult>
     */
    public List<KpiGatherResult> findKpiGatherResultListByTimePeriodId(String kpiId, String[] whereIn) {
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.in("timePeriod.id", whereIn));
        criteria.add(Restrictions.isNotNull("targetValue"));
        return  criteria.list();
    }

    /**
     * 根据时间查询对应VALUE
     * 
     * @author 金鹏祥
     * @param whereIn 时间集合   
     * @return List<KpiGatherResult>
     */
    public List<KpiGatherResult> findKpiGatherResultListByTimePeriodId(String kpiId, Object[] whereIn) {
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.in("timePeriod.id", whereIn));
        criteria.add(Restrictions.isNotNull("targetValue"));
        return criteria.list();
    }

    /**
     * <pre>
     * 根据采集频率查询时间维度
     * </pre>
     * 
     * @author 陈晓哲
     * @param frequenceType
     *            采集频率
     * @return
     * @since fhd　Ver 1.1
     */
    public List<TimePeriod> findTimePeriodListByFrequenceType(String frequenceType, int year) {
        List<String> condList = new ArrayList<String>();
        if ("0frequecy_year".equals(frequenceType)) {
            condList.add("0frequecy_year");
        }
        else if ("0frequecy_quarter".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_quarter");
        }
        else if ("0frequecy_month".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_quarter");
            condList.add("0frequecy_month");
        }
        else if ("0frequecy_halfyear".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_halfyear");
        }
        else if ("0frequecy_week".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_week");
            condList.add("0frequecy_month");
        }

        if (condList.size() > 0) {
            Criteria criteria = this.o_timePeriodDAO.createCriteria();
            criteria.setCacheable(true);// 缓存时间纬度提高效率
            criteria.add(Restrictions.eq("year", String.valueOf(year)));
            criteria.add(Restrictions.in("type", condList));
            criteria.addOrder(Order.asc("endTime"));
            criteria.addOrder(Order.desc("startTime"));
            return criteria.list();
        }
        else {
            return new ArrayList<TimePeriod>();
        }

    }

    /**
     * <pre>
     * 根据当期的采集结果查询上期的采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param currentGatherResult
     *            当期采集结果
     * @return KpiGatherResult 前期采集结果
     * @since fhd　Ver 1.1
     */
    public KpiGatherResult findPreGatherResultByCurrent(KpiGatherResult currentGatherResult) {
        KpiGatherResult preKpiGatherResult = null;
        if (null != currentGatherResult) {
            TimePeriod preTimePeriod = currentGatherResult.getTimePeriod().getPrePeriod();
            Kpi kpi = currentGatherResult.getKpi();
            Criteria criteria = o_kpiGatherResultDAO.createCriteria();
            criteria.add(Restrictions.eq("kpi", kpi));
            criteria.add(Restrictions.eq("timePeriod", preTimePeriod));
            List<KpiGatherResult> list = criteria.list();
            if (null != list && list.size() > 0) {
                preKpiGatherResult = list.get(0);
            }
        }
        return preKpiGatherResult;
    }

    /**
     * <pre>
     * 根据当期的采集结果查询去年同期的采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param currentGatherResult
     *            当期采集结果
     * @return KpiGatherResult 去年同期采集结果
     * @since fhd　Ver 1.1
     */
    public KpiGatherResult findPreYearGatherResultByCurrent(KpiGatherResult currentGatherResult) {
        KpiGatherResult preKpiGatherResult = null;
        if (null != currentGatherResult) {
            TimePeriod preYearTimePeriod = currentGatherResult.getTimePeriod().getPreYearPeriod();
            Kpi kpi = currentGatherResult.getKpi();
            Criteria criteria = o_kpiGatherResultDAO.createCriteria();
            criteria.add(Restrictions.eq("kpi", kpi));
            criteria.add(Restrictions.eq("timePeriod", preYearTimePeriod));
            List<KpiGatherResult> list = criteria.list();
            if (null != list && list.size() > 0) {
                preKpiGatherResult = list.get(0);
            }
        }
        return preKpiGatherResult;
    }

    
    /**根据指标id和查询出当期采集数据
     * @param kpiIdList指标id集合
     * @param timePeriodId 时间区间id
     * @return Map<String, KpiGatherResult> key:指标id,  value:对应的采集结果数据
     */
    public Map<String, KpiGatherResult> findCurrentKpiGatherResultMapByKpiIds(List<String> kpiIdList, String timePeriodId)
    {
        Map<String, KpiGatherResult> resultMap = new HashMap<String, KpiGatherResult>();
        DetachedCriteria criteria = DetachedCriteria.forClass(KpiGatherResult.class);
        criteria.createAlias("kpi", "kpi");
        criteria.add(Restrictions.eq("kpi.deleteStatus", true));
        criteria.createAlias("timePeriod", "time");
        criteria.add(Restrictions.eqProperty("time.type", "kpi.gatherFrequence.id"));
        if (null != kpiIdList && kpiIdList.size() > 0)
        {
            criteria.add(Restrictions.in("kpi.id", kpiIdList));
        }
        if (StringUtils.isNotBlank(timePeriodId))
        {
            criteria.add(Restrictions.eq("time.id", timePeriodId));
        }
        else
        {
            // 取当前时间
            Date currentDate = new Date();
            criteria.add(Restrictions.and(Restrictions.le("time.startTime", currentDate), Restrictions.gt("time.endTime", currentDate)));
        }
        List<KpiGatherResult> list = criteria.getExecutableCriteria(o_kpiGatherResultDAO.getSession()).list();
        if (null != list && list.size() > 0)
        {
            for (KpiGatherResult kpiGatherResult : list)
            {
                String kpiId = kpiGatherResult.getKpi().getId();
                resultMap.put(kpiId, kpiGatherResult);
            }
        }
        return resultMap;
    }
    
    /**生成指定年份指标采集数据
     * @param year 年信息
     */
    @Transactional
    public void generationKpiGatherData(String year){
        String currentYear = year;
        List<Kpi> kpiList = o_kpiBO.findAllKpiListByTask();
        JSONArray jsonarray = new JSONArray();
        for (Kpi kpi : kpiList) {
            JSONObject jsobj = new JSONObject();
            if (null != kpi.getGatherFrequence() && null != kpi.getCompany()) {
                try {
                    jsobj.put("companyid", kpi.getCompany().getId());
                }
                catch (ObjectNotFoundException e) {
                    logger.error("异常信息:" + e.toString());
                    continue;
                }
                try {
                    jsobj.put("frequence", kpi.getGatherFrequence().getId());
                }
                catch (ObjectNotFoundException e) {
                    logger.error("异常信息:" + e.toString());
                    continue;
                }
                jsobj.put("kpiid", kpi.getId());
                jsonarray.add(jsobj);
            }
        }
        
        if (jsonarray.size() > 0) {
            saveBatchKpiGatherResultByFrequenceType(jsonarray, Integer.valueOf(currentYear));
        }
    }
    
    /**
     * <pre>
     * 批量插入所有有效指标的采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @since fhd　Ver 1.1
     */
    @Transactional
    public boolean saveBatherKpiGather() {
        boolean result = true;
    	Date current = new Date();
        String currentYear = DateUtils.getYear(current);
        try {
            List<Kpi> kpiList = o_kpiBO.findAllKpiListByTask();
            JSONArray jsonarray = new JSONArray();
            for (Kpi kpi : kpiList) {
                JSONObject jsobj = new JSONObject();
                if (null != kpi.getGatherFrequence() && null != kpi.getCompany()) {
                    try {
                        jsobj.put("companyid", kpi.getCompany().getId());
                    }
                    catch (ObjectNotFoundException e) {
                        logger.error("异常信息:" + e.toString());
                        continue;
                    }
                    try {
                        jsobj.put("frequence", kpi.getGatherFrequence().getId());
                    }
                    catch (ObjectNotFoundException e) {
                        logger.error("异常信息:" + e.toString());
                        continue;
                    }
                    jsobj.put("kpiid", kpi.getId());
                    jsonarray.add(jsobj);
                }
            }
            
            if (jsonarray.size() > 0) {
                saveBatchKpiGatherResultByFrequenceType(jsonarray, Integer.valueOf(currentYear));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("生成指标采集数据异常:["+e.toString()+"]");
            result = false;
        }
        return result;
        
    }

    /**
     * <pre>
     * 批量插入指标采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param jsonarray
     *            指标ID,公司id,采集频率
     * @param year
     *            年信息
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveBatchKpiGatherResultByFrequenceType(final JSONArray jsonarray, final int year) {
        this.o_kpiGatherResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	List<TimePeriod> yearTimeList =  findTimePeriodListByFrequenceType("0frequecy_year", year);
                List<TimePeriod> halfYearTimeList =  findTimePeriodListByFrequenceType("0frequecy_halfyear", year);
                List<TimePeriod> monthTimeList =  findTimePeriodListByFrequenceType("0frequecy_month", year);
                List<TimePeriod> quarterTimeList =  findTimePeriodListByFrequenceType("0frequecy_quarter", year);
                List<TimePeriod> weekTimeList =  findTimePeriodListByFrequenceType("0frequecy_week", year);
                Map<String,List<TimePeriod>> timeMap = new HashMap<String, List<TimePeriod>>();
                timeMap.put("0frequecy_year", yearTimeList);
                timeMap.put("0frequecy_halfyear", halfYearTimeList);
                timeMap.put("0frequecy_month", monthTimeList);
                timeMap.put("0frequecy_quarter", quarterTimeList);
                timeMap.put("0frequecy_week", weekTimeList);
                List<String> kpiIdList = new ArrayList<String>();
                for (Object jsobj : jsonarray) {
                    String kpiid = ((JSONObject) jsobj).getString("kpiid");
                    kpiIdList.add(kpiid);
                }
                Map<String,List<KpiGatherResult>> resultMap = findMapKpiGatherResultListByKpiIdList(kpiIdList,String.valueOf(year));
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_kpi_kpi_gather_result (id,company_id,kpi_id,time_period_id,begin_time,end_time) values(?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                for (Object jsobj : jsonarray) {
                    String kpiid = ((JSONObject) jsobj).getString("kpiid");
                    String frequenceType = ((JSONObject) jsobj).getString("frequence");
                    String companyid = ((JSONObject) jsobj).getString("companyid");
                    List<TimePeriod> timelist = timeMap.get(frequenceType);
                    List<KpiGatherResult> originalTimePeriodList =resultMap.get(kpiid); 
                    List<TimePeriod> mergeTimeList = null;
                    if(null==originalTimePeriodList){
                        mergeTimeList = timelist;
                    }else{
                        mergeTimeList = mergeTimePeriodList(timelist, originalTimePeriodList, companyid);
                    }
                    for (TimePeriod timePeriod : mergeTimeList) {
                        pst.setString(1, Identities.uuid());
                        pst.setString(2, companyid);
                        pst.setString(3, kpiid);
                        pst.setString(4, timePeriod.getId());
                        pst.setTimestamp(5, new java.sql.Timestamp(timePeriod.getStartTime().getTime()));
                        pst.setTimestamp(6, new java.sql.Timestamp(timePeriod.getEndTime().getTime()));
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
     * <pre>
     * 批量插入指标采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param 指标ID和指标采集频率Map
     * @param year
     *            年信息
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveBatchKpiGatherResultByFrequenceType(final Map<String, String> map, final int year) {
        final String companyid = UserContext.getUser().getCompanyid();// 定时任务会报空指针异常
        this.o_kpiGatherResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_kpi_kpi_gather_result (id,company_id,kpi_id,time_period_id,begin_time,end_time) values(?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                for (Map.Entry<String, String> m : map.entrySet()) {

                    String kpiid = m.getKey();
                    String frequenceType = m.getValue();
                    List<TimePeriod> timelist = findTimePeriodListByFrequenceType(frequenceType, year);
                    for (TimePeriod timePeriod : timelist) {
                        pst.setString(1, Identities.uuid());
                        pst.setString(2, companyid);
                        pst.setString(3, kpiid);
                        pst.setString(4, timePeriod.getId());
                        pst.setTimestamp(5, new java.sql.Timestamp(timePeriod.getStartTime().getTime()));
                        pst.setTimestamp(6, new java.sql.Timestamp(timePeriod.getEndTime().getTime()));
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
     * <pre>
     * 批量插入指标采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiid
     *            指标ID
     * @param frequenceType
     *            采集频率
     * @param year
     *            年
     * @throws SQLException
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveBatchKpiGatherResultByFrequenceType(final String kpiid, String frequenceType, int year){
        List<String> condList = new ArrayList<String>();
        final String companyid = UserContext.getUser().getCompanyid();
        if ("0frequecy_year".equals(frequenceType)) {
            condList.add("0frequecy_year");
        }
        else if ("0frequecy_quarter".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_quarter");
        }
        else if ("0frequecy_month".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_quarter");
            condList.add("0frequecy_month");
        }
        else if ("0frequecy_halfyear".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_halfyear");
        }
        else if ("0frequecy_week".equals(frequenceType)) {
            condList.add("0frequecy_year");
            condList.add("0frequecy_week");
            condList.add("0frequecy_month");
        }
        Criteria criteria = this.o_timePeriodDAO.createCriteria();
        criteria.add(Restrictions.eq("year", String.valueOf(year)));
        criteria.add(Restrictions.in("type", condList));
        criteria.addOrder(Order.asc("endTime"));
        criteria.addOrder(Order.desc("startTime"));
        final List<TimePeriod> times = criteria.list();
        this.o_kpiGatherResultDAO.getSession().doWork(new Work() {
            public void execute(Connection conn) throws SQLException {
            	conn.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_kpi_kpi_gather_result (id,company_id,kpi_id,time_period_id,begin_time,end_time) values(?,?,?,?,?,?)";
                pst = conn.prepareStatement(sql);
                for (TimePeriod timePeriod : times) {
                    pst.setString(1, Identities.uuid());
                    pst.setString(2, companyid);
                    pst.setString(3, kpiid);
                    pst.setString(4, timePeriod.getId());
                    pst.setTimestamp(5, new java.sql.Timestamp(timePeriod.getStartTime().getTime()));
                    pst.setTimestamp(6, new java.sql.Timestamp(timePeriod.getEndTime().getTime()));
                    pst.addBatch();
                }
                pst.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);
            }
        });
    }

   
    
    
    /**
     * 新增指标采集结果.
     * 
     * @param kpiGatherResult
     *            指标采集结果实体
     */
    @Transactional
    public void saveKpiGatherResult(KpiGatherResult kpiGatherResult) {
        o_kpiGatherResultDAO.save(kpiGatherResult);
    }

    
    /**去调指标采集结果表中已经存在的时间区间纬度
     * @param timePeriodList 时间区间纬度列表
     * @param kpiId 指标id
     * @param companyId 公司ID
     * @return
     */
    public List<TimePeriod> mergeTimePeriodList(List<TimePeriod> timePeriodList, List<KpiGatherResult> originalTimePeriodList , String companyId) {
        List<TimePeriod> mergeTimePeriodList = new ArrayList<TimePeriod>();
        for (TimePeriod timePeriod : timePeriodList) {
            boolean existFlag = false;
            for (KpiGatherResult okpigatherresult : originalTimePeriodList) {
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
     * 保存指标采集结果.
     * 
     * @param kpiGatherResult
     *            指标采集结果实体
     */
    @Transactional
    public void mergeKpiGatherResult(KpiGatherResult kpiGatherResult) {
        o_kpiGatherResultDAO.merge(kpiGatherResult);
    }
    
    /**删除指标采集结果
     * @param id采集结果id
     */
    @Transactional
    public void deleteKpiGatherResult(String id){
        String sql = " delete from t_kpi_kpi_gather_result where id=?";
        SQLQuery sqlquery = o_kpiGatherResultDAO.createSQLQuery(sql, id);
        sqlquery.executeUpdate();
    }
    
    /**
     * 根据累积值计算项计算季度累积值.
     * 
     * @param kpiGatherResult
     *            指标当期采集结果
     * @param typeSum
     *            指标采集结果累积类型值
     * @param calculateItem
     *            累积值计算项
     */
    public Double calculateQuarterAccumulatedValueByCalculateItem(KpiGatherResult kpiGatherResult, String typeSum, String calculateItem) {
        // 当前期间采集结果的父时间维度
        TimePeriod parentTimePeriod = kpiGatherResult.getTimePeriod().getParent();
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "time");
        criteria.add(Property.forName("kpi").eq(kpiGatherResult.getKpi()));
        criteria.add(Property.forName("time.parent").eq(parentTimePeriod));
        criteria.addOrder(Order.asc("time.startTime"));
        List<KpiGatherResult> gatherResults = criteria.list();
        List<Double> gatherResultValues = new ArrayList<Double>();
        for (KpiGatherResult result : gatherResults) {
            if ("targetValueSum".equals(typeSum))// 目标值累计
            {
                if(null!=result.getTargetValue()){
                    gatherResultValues.add(result.getTargetValue());
                }
            }
            else if ("resultValueSum".equals(typeSum))// 结果值累计
            {
                if(null!=result.getFinishValue()){
                    gatherResultValues.add(result.getFinishValue());
                }
            }
            else if ("assessmentValueSum".equals(typeSum))// 评估值累计
            {
                if(null!=result.getAssessmentValue()){
                    gatherResultValues.add(result.getAssessmentValue());
                }
            }
        }
        return calculateAccumulateValue(gatherResultValues, calculateItem);
    }

    /**
     * 根据累积值计算项计算年度累积值.
     * 
     * @param kpiGatherResult
     *            指标当期采集结果
     * @param typeSum
     *            指标采集结果累积类型值
     * @param calculateItem
     *            累积值计算项
     */
    public Double calculateYearAccumulatedValueByCalculateItem(KpiGatherResult kpiGatherResult, String typeSum, String calculateItem) {
        Kpi kpi = kpiGatherResult.getKpi();
        String frequence = kpi.getGatherFrequence().getId();
        TimePeriod timePeriod = kpiGatherResult.getTimePeriod();
        String year = timePeriod.getYear();
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("timePeriod", "time");
        criteria.add(Restrictions.eq("kpi", kpi));
        criteria.add(Property.forName("time.year").eq(year));
        criteria.add(Property.forName("time.type").eq(frequence));
        criteria.addOrder(Order.asc("time.startTime"));
        List<KpiGatherResult> gatherResults = criteria.list();
        List<Double> gatherResultValues = new ArrayList<Double>();
        for (KpiGatherResult result : gatherResults) {
            if ("targetValueSum".equals(typeSum))// 目标值累计
            {
                if(null!=result.getTargetValue()){
                    gatherResultValues.add(result.getTargetValue());
                }
            }
            else if ("resultValueSum".equals(typeSum))// 结果值累计
            {
                if(null!=result.getFinishValue()){
                    gatherResultValues.add(result.getFinishValue());
                }
            }
            else if ("assessmentValueSum".equals(typeSum))// 评估值累计
            {
                if(null!=result.getAssessmentValue()){
                    gatherResultValues.add(result.getAssessmentValue());
                }
            }
        }
        //根据累计类型计算累计值
        return calculateAccumulateValue(gatherResultValues, calculateItem);
    }

    /**
     * 根据评估值计算趋势.
     * 
     * @param kpiGatherResult
     *            当期指标采集结果
     */
    public DictEntry calculateTrendByAssessmentValue(KpiGatherResult gatherResult) {
        DictEntry trend = null;
        Kpi kpi = gatherResult.getKpi();
        TimePeriod prePeriodTime = gatherResult.getTimePeriod().getPrePeriod();
        Double assvalue = gatherResult.getAssessmentValue();
        Double preAssvalue = null;
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.add(Restrictions.eq("timePeriod", prePeriodTime));
        criteria.add(Restrictions.eq("kpi", kpi));
        List<KpiGatherResult> list = criteria.list();
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

    /**
     * <pre>
     * 根据累计类型计算累计值
     * </pre>
     * 
     * @author 陈晓哲
     * @param gatherResultValues
     *            待求累计值数组
     * @param calculateItem
     *            累计类型
     * @return 累计值
     * @since fhd　Ver 1.1
     */
    public Double calculateAccumulateValue(List<Double> gatherResultValues, String calculateItem) {
        Double result = null;
        if (gatherResultValues.size() > 0) {
            double[] results = null;
            Double[] gatherResult = gatherResultValues.toArray(new Double[gatherResultValues.size()]);
            if (null != gatherResult && gatherResult.length > 0) {
                results = new double[gatherResult.length];
                for (int i = 0; i < gatherResult.length; i++) {
                    if (null != gatherResult[i]) {
                        results[i] = gatherResult[i];
                    }
                    else {
                        results[i] = 0.0;
                    }
                }
                if ("kpi_sum_measure_avg".equals(calculateItem)) {
                    result = StatisticFunctionCalculateBO.ma(results);
                }
                else if ("kpi_sum_measure_max".equals(calculateItem)) {
                    result = StatisticFunctionCalculateBO.max(results);
                }
                else if ("kpi_sum_measure_min".equals(calculateItem)) {
                    result = StatisticFunctionCalculateBO.min(results);
                }
                else if ("kpi_sum_measure_sum".equals(calculateItem)) {
                    Sum sum = new Sum();
                    result = sum.evaluate(results);
                }
                else if ("kpi_sum_measure_first".equals(calculateItem)) {
                    if (null != results && results.length > 0) {
                        result = results[0];
                    }
                }
                else if ("kpi_sum_measure_last".equals(calculateItem)) {
                    int size = results.length;
                    if (null != results && size > 0) {
                        result = results[size - 1];
                    }
                }
            }
        }
        return result;
    }
    
    /**根据月度时间区间维和采集频率得到时间区间维
     * @param frequence 指标采集频率
     * @param timePeriod 月的时间区间纬度ID 都转化为月份
     * @param num 前几期数值
     * @return
     */
    public String convertTimePeriodBySome(String frequence, String timePeriod, int num) {
        String time = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String year = timePeriod.substring(0, 4);
        String month = timePeriod.substring(6);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.valueOf(year));
        cal.set(Calendar.MONTH, Integer.valueOf(month) - 1);
        if ("0frequecy_month".equals(frequence)) {
            cal.add(Calendar.MONTH, -num);
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            time = new StringBuffer().append(timearr[0]).append("mm").append(timearr[1]).toString();
        }
        else if ("0frequecy_year".equals(frequence)) {
            cal.add(Calendar.YEAR, -num);
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            time = timearr[0];
        }
        else if ("0frequecy_halfyear".equals(frequence)) {
            cal.add(Calendar.MONTH, -num * 6);
            String halfStr = "";
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            if (Integer.valueOf(timearr[1]) > 6) {
                halfStr = "1";
            }
            else {
                halfStr = "0";
            }
            time = new StringBuffer().append(timearr[0]).append("hf").append(halfStr).toString();

        }
        else if ("0frequecy_quarter".equals(frequence)) {
            cal.add(Calendar.MONTH, -num * 3);
            String timestr = sdf.format(cal.getTime());
            String[] timearr = timestr.split("/");
            Integer quarter = ((Integer.valueOf(timearr[1]) * 3) / 10) + 1;
            time = new StringBuffer().append(timearr[0]).append("Q").append(quarter).toString();
        }
        return time;
    }
     
}
