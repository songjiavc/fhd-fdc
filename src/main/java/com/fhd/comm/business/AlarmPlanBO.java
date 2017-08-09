/**
 * AlarmPlanBO.java
 * com.fhd.kpi.business
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-10-25 		陈晓哲
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.comm.business;

import static org.hibernate.FetchMode.JOIN;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.comm.AlarmPlanDAO;
import com.fhd.dao.comm.AlarmRegionDAO;
import com.fhd.dao.comm.CategoryRelaAlarmDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.AlarmRegion;
import com.fhd.entity.comm.CategoryRelaAlarm;
import com.fhd.entity.kpi.KpiRelaAlarm;
import com.fhd.entity.kpi.SmRelaAlarm;
import com.fhd.comm.interfaces.IAlarmPlanBO;
import com.fhd.comm.web.form.AlarmPlanForm;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.dao.kpi.KpiRelaAlarmDAO;
import com.fhd.dao.kpi.SmRelaAlarmDAO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 告警预警BO
 * ClassName:KpiStrategyMapBO
 *
 * @author   陈晓哲
 * @version
 * @since    Ver 1.1
 * @Date     2012   2012-10-25       上午13:46:52
 *
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class AlarmPlanBO implements IAlarmPlanBO
{

    @Autowired
    private AlarmPlanDAO o_alarmPlanDAO;

    @Autowired
    private SmRelaAlarmDAO o_smRelaAlarmDAO;
    
    @Autowired
    private CategoryRelaAlarmDAO o_categoryRelaAlarmDAO;
    
    @Autowired
    private  KpiRelaAlarmDAO o_kpiRelaAlarmDAO;

    @Autowired
    private DictBO o_dictEntryBO;

    @Autowired
    private AlarmRegionDAO o_alarmRegionDAO;

    @Autowired
    private OrganizationBO o_organizationBO;

    
    /**
     * 计算指标告警状态
     * @return
     */
    public DictEntry findKpiAlarmStatusByValues(AlarmPlan alarmPlan, Double finishValue) {
        //获得采集结果关联的指标对象
        DictEntry alarmLevel = null;
        //获得指标关联的告警方案
        if(null!=alarmPlan){
            Set<AlarmRegion> alarmRegions = alarmPlan.getAlarmRegions();
            for (AlarmRegion alarmRegion : alarmRegions) {
                String maxValueStr = alarmRegion.getMaxValue();
                String minValueStr = alarmRegion.getMinValue();
                String maxSign = alarmRegion.getIsContainMax().getName();
                String minSign = alarmRegion.getIsContainMin().getName();
                
                if (!Contents.POSITIVE_INFINITY.equals(maxValueStr) && !Contents.NEGATIVE_INFINITY.equals(minValueStr)) {
                    Double maxValue = Double.valueOf(maxValueStr);
                    Double minValue = Double.valueOf(minValueStr);
                    if("<=".equals(minSign)&&"<".equals(maxSign)){
                        if (finishValue >= minValue && finishValue < maxValue) {
                            alarmLevel = alarmRegion.getAlarmIcon();
                            break;
                        }
                    }else if("<=".equals(minSign)&&"<=".equals(maxSign)){
                        if (finishValue >= minValue && finishValue <= maxValue) {
                            alarmLevel = alarmRegion.getAlarmIcon();
                            break;
                        }
                    }else if("<".equals(minSign)&&"<=".equals(maxSign)){
                        if (finishValue > minValue && finishValue <= maxValue) {
                            alarmLevel = alarmRegion.getAlarmIcon();
                            break;
                        }
                    }else if("<".equals(minSign)&&"<".equals(maxSign)){
                        if (finishValue > minValue && finishValue < maxValue) {
                            alarmLevel = alarmRegion.getAlarmIcon();
                            break;
                        }
                    }
                    
                }
                else {
                    if (Contents.POSITIVE_INFINITY.equals(maxValueStr)) {
                        Double minValue = Double.valueOf(minValueStr);
                        if("<".equals(minSign)){
                            if (finishValue > Double.valueOf(minValue)) {
                                alarmLevel = alarmRegion.getAlarmIcon();
                                break;
                            }
                        }else if("<=".equals(minSign)){
                            if (finishValue >= Double.valueOf(minValue)) {
                                alarmLevel = alarmRegion.getAlarmIcon();
                                break;
                            }
                        }
                        
                    }
                    else if (Contents.NEGATIVE_INFINITY.equals(minValueStr)) {
                        Double maxValue = Double.valueOf(maxValueStr);
                        if("<=".equals(maxSign)){
                            if (finishValue <= maxValue) {
                                alarmLevel = alarmRegion.getAlarmIcon();
                                break;
                            }
                        }else if("<".equals(maxSign)){
                            if (finishValue < maxValue) {
                                alarmLevel = alarmRegion.getAlarmIcon();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return alarmLevel;
    }
    
    
    /**根据告警方案id集合查询出对应的区间;key:告警方案id value:对应的区间 
     * @param alarmIdList 告警方案id
     * @return
     */
    public Map<String,List<AlarmRegion>> findAlarmRegionByAlarmId(List<String> alarmIdList) {
        List<AlarmRegion> list = null;
        Map<String,List<AlarmRegion>> regionMap = new HashMap<String, List<AlarmRegion>>();
        Criteria c = o_alarmRegionDAO.createCriteria();
        c.add(Restrictions.in("alarmPlan.id", alarmIdList));
        c.addOrder(Order.asc("minValue"));
        list = c.list();
        for (AlarmRegion alarmRegion : list) {
        	String planId = alarmRegion.getAlarmPlan().getId();
			if(!regionMap.containsKey(planId)){
				regionMap.put(planId, new ArrayList<AlarmRegion>());
			}
			regionMap.get(planId).add(alarmRegion);
		}
        return regionMap;
    }
    
    /** 
      * @Description: 根据告警方案id获取对应的区间 
      * @author jia.song@pcitc.com
      * @date 2017年5月8日 上午9:55:27 
      * @param alarmId
      * @return 
      */
    public List<AlarmRegion> findAlarmRegionByAlarmId(String alarmId){
    	Criteria c = o_alarmRegionDAO.createCriteria();
        c.add(Restrictions.eq("alarmPlan.id", alarmId));
        c.add(Restrictions.eq("deleteStatus", true));
        c.addOrder(Order.asc("minValue"));
        return c.list();
    }
    
	/**
	 * 根据预警方案id查询预警区间的最小值和最大值.
	 * @param alarmPlanId 预警方案id
	 * @return List<Object[]>
	 * @author 吴德福
	 * @since  fhd　Ver 1.1
	 */
	public List<Object[]> findExtremeValueByAlarmPlanId(String alarmPlanId){
		Criteria criteria = o_alarmRegionDAO.createCriteria();
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.max("maxValue"));
		projectionList.add(Projections.min("minValue"));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.eq("alarmPlan.id", alarmPlanId));
		criteria.add(Restrictions.eq("deleteStatus", true));
		return criteria.list();
	}
	
	/**
	 * 根据名称和类型查询方案 
	 * @param name 名称
	 * @param type 预警 :0alarm_type_kpi_alarm , 告警:0alarm_type_kpi_forecast
	 * @return 告警或预警方案
	 */
	public AlarmPlan findAlarmPlanByNameType(String name,String type){
		Criteria criteria = o_alarmPlanDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		criteria.add(Restrictions.eq("name", name));
		if("0alarm_type_kpi_alarm".equals(type)||"0alarm_type_kpi_forecast".equals(type)){
			criteria.add(Restrictions.eq("type.id", type));
		}
		List<AlarmPlan> list = criteria.list();
		if(null!=list&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根据名称和类型查询方案 
	 * @param type 预警 :0alarm_type_kpi_alarm , 告警:0alarm_type_kpi_forecast
	 * @return 告警或预警方案集合
	 */
	public Map<String, String> findAlarmPlanMap(String type) {
		Map<String, String> map = new HashMap<String,String>();
		Criteria criteria = o_alarmPlanDAO.createCriteria();
		//criteria.setCacheable(true);
		criteria.add(Restrictions.eq("deleteStatus", true));
		criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		if("0alarm_type_kpi_alarm".equals(type)||"0alarm_type_kpi_forecast".equals(type)){
			criteria.add(Restrictions.eq("type.id", type));
		}
		List<AlarmPlan> list = criteria.list();
		for(AlarmPlan alarmplan: list) {
			map.put(alarmplan.getName(), alarmplan.getId());
		}
		return map;
	}
	
	/**
     * 根据名称和类型查询方案 
     * @param name 名称
     * @param companyId 公司ID
     * @param type 预警 :0alarm_type_kpi_alarm , 告警:0alarm_type_kpi_forecast
     * @return 告警或预警方案
     */
	public AlarmPlan findAlarmPlanByNameType(String name,String type,String companyId){
	    Criteria criteria = o_alarmPlanDAO.createCriteria();
	    criteria.add(Restrictions.eq("company.id",companyId));
	    criteria.add(Restrictions.eq("name", name));
	    if("0alarm_type_kpi_alarm".equals(type)||"0alarm_type_kpi_forecast".equals(type)){
	        criteria.add(Restrictions.eq("type.id", type));
	    }
	    List<AlarmPlan> list = criteria.list();
	    if(null!=list&&list.size()>0){
	        return list.get(0);
	    }
	    return null;
	}
	
	/**根据指标id集合查询出所对应的告警方案 返回格式为map
	 * @param kpiIdList 指标id集合
	 * @param type 告警类型
	 * @return
	 */
	public Map<String,AlarmPlan> findAlarmPlanByKpiId(List<String> kpiIdList, String type) {
    	Map<String,AlarmPlan> alarmPlanMap = new HashMap<String, AlarmPlan>();
        String alarmType = "fcAlarmPlan";
        if ("forecast".equals(type)) {
            alarmType = "fcAlarmPlan";
        }
        else if ("report".equals(type)) {
            alarmType = "rAlarmPlan";
        }
        Date currentDate = new Date();
        Criteria criteria = o_kpiRelaAlarmDAO.createCriteria();
        criteria.createAlias("rAlarmPlan", "rAlarmPlan");
        criteria.setFetchMode("kpi", FetchMode.JOIN);
        criteria.setFetchMode("rAlarmPlan.alarmRegions", FetchMode.JOIN);
        criteria.add(Restrictions.in("kpi.id",kpiIdList));
        criteria.add(Restrictions.le("startDate", currentDate));
        criteria.setFetchMode(alarmType, JOIN);
        criteria.addOrder(Order.asc("startDate"));
        List<KpiRelaAlarm> kpiRelaAlarms = criteria.list();
        if (null != kpiRelaAlarms ) {
            for (int i = 0; i < kpiRelaAlarms.size(); i++) {
                KpiRelaAlarm kpiRelaAlarm = kpiRelaAlarms.get(i);
                String kpiId = kpiRelaAlarm.getKpi().getId();
                if ("forecast".equals(type)) {
                    alarmPlanMap.put(kpiId, kpiRelaAlarm.getFcAlarmPlan());
                } else if ("report".equals(type)) {
                    alarmPlanMap.put(kpiId, kpiRelaAlarm.getrAlarmPlan());
                }
            }
        }
        return alarmPlanMap;
    }
	
	/**根据记分卡id集合查询出所对应的告警方案 返回格式为map
	 * @param categoryIdList 记分卡id集合
	 * @param type 告警类型
	 * @return
	 */
	public Map<String,AlarmPlan> findScAlarmPlanByKpiId(List<String> categoryIdList, String type) {
		Map<String,AlarmPlan> alarmPlanMap = new HashMap<String, AlarmPlan>();
		String alarmType = "fcAlarmPlan";
		if ("forecast".equals(type)) {
			alarmType = "fcAlarmPlan";
		}
		else if ("report".equals(type)) {
			alarmType = "rAlarmPlan";
		}
		Date currentDate = new Date();
		Criteria criteria = o_categoryRelaAlarmDAO.createCriteria();
		criteria.createAlias("rAlarmPlan", "rAlarmPlan");
		criteria.setFetchMode("category", FetchMode.JOIN);
		criteria.setFetchMode("rAlarmPlan.alarmRegions", FetchMode.JOIN);
		criteria.add(Restrictions.in("category.id",categoryIdList));
		criteria.add(Restrictions.le("startDate", currentDate));
		criteria.setFetchMode(alarmType, JOIN);
		criteria.addOrder(Order.asc("startDate"));
		List<CategoryRelaAlarm> categoryRelaAlarms = criteria.list();
		if (null != categoryRelaAlarms ) {
			for (int i = 0; i < categoryRelaAlarms.size(); i++) {
				CategoryRelaAlarm categoryRelaAlarm = categoryRelaAlarms.get(i);
				String kpiId = categoryRelaAlarm.getCategory().getId();
				if ("forecast".equals(type)) {
					alarmPlanMap.put(kpiId, categoryRelaAlarm.getFcAlarmPlan());
				}
				else if ("report".equals(type)) {
					alarmPlanMap.put(kpiId, categoryRelaAlarm.getrAlarmPlan());
				}
			}
		}
		return alarmPlanMap;
	}
	
	/**根据战略目标id集合查询出所对应的告警方案 返回格式为map
	 * @param strategyIdList 战略目标id集合
	 * @param type 告警类型
	 * @return
	 */
	public Map<String,AlarmPlan> findSmAlarmPlanByKpiId(List<String> strategyIdList, String type) {
		Map<String,AlarmPlan> alarmPlanMap = new HashMap<String, AlarmPlan>();
		String alarmType = "fcAlarmPlan";
		if ("forecast".equals(type)) {
			alarmType = "fcAlarmPlan";
		}
		else if ("report".equals(type)) {
			alarmType = "rAlarmPlan";
		}
		Date currentDate = new Date();
		Criteria criteria = o_smRelaAlarmDAO.createCriteria();
		criteria.createAlias("rAlarmPlan", "rAlarmPlan");
		criteria.setFetchMode("strategyMap", FetchMode.JOIN);
		criteria.setFetchMode("rAlarmPlan.alarmRegions", FetchMode.JOIN);
		criteria.add(Restrictions.in("strategyMap.id",strategyIdList));
		criteria.add(Restrictions.le("startDate", currentDate));
		criteria.setFetchMode(alarmType, JOIN);
		criteria.addOrder(Order.asc("startDate"));
		List<SmRelaAlarm> smRelaAlarms = criteria.list();
		if (null != smRelaAlarms ) {
			for (int i = 0; i < smRelaAlarms.size(); i++) {
				SmRelaAlarm smRelaAlarm = smRelaAlarms.get(i);
				String kpiId = smRelaAlarm.getStrategyMap().getId();
				if ("forecast".equals(type)) {
					alarmPlanMap.put(kpiId, smRelaAlarm.getFcAlarmPlan());
				}
				else if ("report".equals(type)) {
					alarmPlanMap.put(kpiId, smRelaAlarm.getrAlarmPlan());
				}
			}
		}
		return alarmPlanMap;
	}
	
	/**
	 * 查询所有告警方案实体并已MAP方式存储
	 * @param companyId 公司ID
	 * @return HashMap<String, AlarmPlan>
	 * @author 金鹏祥
	 * */
	public HashMap<String, AlarmPlan> findAlarmPlanAllMap(String companyId){
		HashMap<String, AlarmPlan> map = new HashMap<String, AlarmPlan>();
		Criteria criteria = o_alarmPlanDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.addOrder(Order.asc("sort"));
		List<AlarmPlan> list = null;
		list = criteria.list();
		
		for (AlarmPlan alarmPlan : list) {
			map.put(alarmPlan.getId(), alarmPlan);
		}
		
		return map;
	}
	
	/**
	 * 查询所有告警方案实体并已MAP方式存储
	 * @author 金鹏祥
	 * @return HashMap<String, AlarmPlan>
	 * */
	public HashMap<String, AlarmPlan> findAlarmPlanAllMap(){
		HashMap<String, AlarmPlan> map = new HashMap<String, AlarmPlan>();
		Criteria criteria = o_alarmPlanDAO.createCriteria();
		criteria.addOrder(Order.asc("sort"));
		List<AlarmPlan> list = null;
		list = criteria.list();
		
		for (AlarmPlan alarmPlan : list) {
			map.put(alarmPlan.getId(), alarmPlan);
		}
		
		return map;
	}
	
	/**根据告警或预警id集合查询告警map key为方案id,value为方案对象
	 * @param planIdList 告警方案或预警方案id集合
	 * @return 告警map key为方案id,value为方案对象
	 */
	public Map<String,AlarmPlan> findAlarmPlanMapByPlanIdList(List<String> planIdList){
		Map<String,AlarmPlan> planMap = new HashMap<String, AlarmPlan>();
		if(null!=planIdList&&planIdList.size()>0){
		    Criteria criteria = o_alarmPlanDAO.createCriteria();
	        criteria.add(Restrictions.in("id", planIdList));
	        criteria.add(Restrictions.eq("deleteStatus", true));
	        List<AlarmPlan> alarmList = criteria.list();
	        for (AlarmPlan alarmPlan : alarmList) {
	            planMap.put(alarmPlan.getId(), alarmPlan);
	        }
		}
		
		return planMap;
	}
	
    /**
     * <pre>
     * 查找告警关联的目标实体的数量
     * </pre>
     * 
     * @author 陈晓哲
     * @param alarmId 告警ID
     * @return
     * @since  fhd　Ver 1.1
    */
    public long findSmRelaAlarmCount(String alarmId)
    {
        long count = (Long) this.o_smRelaAlarmDAO
                .createCriteria()
                .add(Restrictions.or(Restrictions.eq("fcAlarmPlan.id", alarmId),
                        Restrictions.eq("rAlarmPlan.id", alarmId))).setProjection(Projections.rowCount())
                .uniqueResult();
        if(count==0){
            count =  (Long) this.o_categoryRelaAlarmDAO
                    .createCriteria()
                    .add(Restrictions.or(Restrictions.eq("fcAlarmPlan.id", alarmId),
                            Restrictions.eq("rAlarmPlan.id", alarmId))).setProjection(Projections.rowCount())
                            .uniqueResult();
            if(count==0){
                count = (Long) this.o_kpiRelaAlarmDAO
                        .createCriteria()
                        .add(Restrictions.or(Restrictions.eq("fcAlarmPlan.id", alarmId),
                                Restrictions.eq("rAlarmPlan.id", alarmId))).setProjection(Projections.rowCount())
                                .uniqueResult();
            }
        }
        return count;
    }
    /**
     * <pre>
     * 查找告警关联的记分卡实体的数量
     * </pre>
     * 
     * @author 陈晓哲
     * @param alarmId 告警ID
     * @return
     * @since  fhd　Ver 1.1
     */
    public Long findCategoryRelaAlarmCount(String alarmId)
    {
        Long count = (Long) this.o_categoryRelaAlarmDAO
                .createCriteria()
                .add(Restrictions.or(Restrictions.eq("fcAlarmPlan.id", alarmId),
                        Restrictions.eq("rAlarmPlan.id", alarmId))).setProjection(Projections.rowCount())
                        .uniqueResult();
        return count;
    }

    
    /**
     * <pre>
     * 根据类型查询告警方案
     * </pre>
     * 
     * @author 陈晓哲
     * @param type 告警类型
     * @return
    */
    public List<AlarmPlan> findAlarmPlanByType(String type){
    	String companyid = UserContext.getUser().getCompanyid();
    	Criteria criteria = o_alarmPlanDAO.createCriteria();
    	criteria.add(Restrictions.eq("company.id", companyid));
    	criteria.add(Restrictions.eq("type.id", type));
    	return criteria.list();
    }
    
    /**
     * <pre>
     * 根据查询条件查询告警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param page
     * @param query 查询条件
     * @param sort
     * @param dir
     * @return
     * @since  fhd　Ver 1.1
    */
    public Page<AlarmPlan> findAlarmPlanBySome(Page<AlarmPlan> page, String query, String sort, String dir)
    {
        String sortstr = "id";
        String companyid = UserContext.getUser().getCompanyid();
        DetachedCriteria dc = DetachedCriteria.forClass(AlarmPlan.class).add(Restrictions.eq("deleteStatus", true));
        dc.add(Restrictions.eq("company.id", companyid));
        if (StringUtils.isNotBlank(query))
        {
            dc.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.equals("name", sort))
        {
            sortstr = "name";
        }
        else if (StringUtils.equals("descs", sort))
        {
            sortstr = "desc";
        }
        else if (StringUtils.equals("types", sort))
        {
            dc.createAlias("type", "type");
            sortstr = "type.name";
        }
        if ("ASC".equalsIgnoreCase(dir))
        {
            dc.addOrder(Order.asc(sortstr));
        }
        else
        {
            dc.addOrder(Order.desc(sortstr));
        }
        return o_alarmPlanDAO.findPage(dc, page, false);
    }

    /**
     * <pre>
     * 根据告警ID查询告警信息,并转换为map,为form赋值
     * </pre>
     * 
     * @author 陈晓哲
     * @param id 告警方案ID
     * @return
     * @since  fhd　Ver 1.1
    */
    public Map<String, Object> findAlarmPlanByIdToJson(String id)
    {
        String range;//区间值json串
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        AlarmPlan alarmPlan = this.findAlarmPlanById(id);
        jsonMap.put("name", alarmPlan.getName());
        jsonMap.put("desc", alarmPlan.getDesc());
        jsonMap.put("types", alarmPlan.getType());
        /*区间值集合*/
        DictEntry levelEntry = null;
        DictEntry maxSignEntry = null;
        DictEntry minSignEntry = null;
        JSONArray jsonArray = new JSONArray();
        Set<AlarmRegion> alarmRegions = alarmPlan.getAlarmRegions();
        for (AlarmRegion alarmRegion : alarmRegions)
        {
            JSONObject jsobj = new JSONObject();
            //最大公式符号
            maxSignEntry = alarmRegion.getIsContainMax();
            minSignEntry = alarmRegion.getIsContainMin();
            levelEntry = alarmRegion.getAlarmIcon();
            if (null!=maxSignEntry)
            {
                jsobj.put("maxSign", maxSignEntry.getName());
            }
            if (null!=minSignEntry)
            {
                jsobj.put("minSign", minSignEntry.getName());
            }
            jsobj.put("level", levelEntry.getName());
            jsobj.put("formulaMax", alarmRegion.getMaxValue());
            jsobj.put("formulaMin", alarmRegion.getMinValue());
            jsonArray.add(jsobj);

        }
        range = jsonArray.toString();
        jsonMap.put("range", range);
        map.put("data", jsonMap);
        map.put("success", true);
        return map;
    }

    /**
     * <pre>
     * 根据告警ID查询告警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     * @return
     * @since  fhd　Ver 1.1
    */
    public AlarmPlan findAlarmPlanById(String id)
    {
        return this.o_alarmPlanDAO.get(id);
    }

    /**
     * <pre>
     * 校验预警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param validateItem 校验项
     * @param mode 添加或修改标志
     * @param id 告警方案ID
     * @return
     * @since  fhd　Ver 1.1
    */
    public long findAlarmPlanCountBySome(String validateItem, String id, String mode,String name)
    {
        String companyid = UserContext.getUser().getCompanyid();
        JSONObject jsobj = JSONObject.fromObject(validateItem);
        Criteria criteria = this.o_alarmPlanDAO.createCriteria().add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("company.id", companyid));
        if (jsobj.containsKey("name"))
        {
            criteria.add(Restrictions.eq("name", name));
            if (!Boolean.valueOf(mode))
            {
                criteria.add(Restrictions.ne("id", id));
            }
        }
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    /**
     * <pre>
     * 保存告警及告警区间值信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param form
     * @since  fhd　Ver 1.1
    */
    @Transactional
    public void saveAlarmPlan(AlarmPlanForm form)
    {
        //获得机构对象
        SysOrganization company = o_organizationBO.get(UserContext.getUser().getCompanyid());
        /*保存基本信息*/
        AlarmPlan alarmPlan = new AlarmPlan();
        alarmPlan.setName(form.getName());
        alarmPlan.setId(Identities.uuid());
        alarmPlan.setDesc(form.getDescs());
        alarmPlan.setType(o_dictEntryBO.findDictEntryById(form.getTypes()));
        alarmPlan.setDeleteStatus(true);
        alarmPlan.setCompany(company);
        this.o_alarmPlanDAO.merge(alarmPlan);

        /*保存告警关联的告警区间值信息*/
        String range = form.getRange();
        if (StringUtils.isNotBlank(range))
        {
            JSONArray rangeArr = JSONArray.fromObject(range);
            for (int i = 0; i < rangeArr.size(); i++)
            {
                JSONObject jsobj = rangeArr.getJSONObject(i);
                this.saveAlarmRegion(alarmPlan, jsobj);
            }
        }
    }

    /**
     * <pre>
     *保存告警方案的区间值信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param plan 告警方案对象
     * @param jsobj 前台传递过来的区间值信息
     * @since  fhd　Ver 1.1
    */
    @Transactional
    private void saveAlarmRegion(AlarmPlan plan, JSONObject jsobj)
    {
        DictEntry levelEntry = null;
        AlarmRegion alarmRegion = new AlarmRegion();
        String level = jsobj.getString("level");
        String formulaMax = jsobj.getString("formulaMax");
        String formulaMin = jsobj.getString("formulaMin");
        String maxSign = jsobj.getString("maxSign");
        String minSign = jsobj.getString("minSign");
        /*获取等级所关联的数据字典*/
        if (StringUtils.isNotBlank(level))
        {
            levelEntry = this.o_dictEntryBO.findDictEntryById(level);
        }
        alarmRegion.setId(Identities.uuid());
        alarmRegion.setAlarmIcon(levelEntry);
        alarmRegion.setAlarmPlan(plan);
        alarmRegion.setDeleteStatus(true);
        alarmRegion.setMaxValue(formulaMax);
        alarmRegion.setMinValue(formulaMin);
        //关联符号数据字典
        if (StringUtils.isNotBlank(maxSign))
        {
            DictEntry maxSignEntry = this.o_dictEntryBO.findDictEntryById(maxSign);
            alarmRegion.setIsContainMax(maxSignEntry);
        }
        if (StringUtils.isNotBlank(minSign))
        {
            DictEntry minSignEntry = this.o_dictEntryBO.findDictEntryById(minSign);
            alarmRegion.setIsContainMin(minSignEntry);
        }
        o_alarmRegionDAO.merge(alarmRegion);
    }

    /**
     * <pre>
     * 修改告警及告警区间值信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param form 告警表单对象
     * @since  fhd　Ver 1.1
    */
    @Transactional
    public void mergeAlarmPlan(AlarmPlanForm form)
    {

        String alarmPlanId = form.getId();
        if (StringUtils.isNotBlank(alarmPlanId))
        {
            /*首先删除关联区间值信息*/
            AlarmPlan alarmPlan = this.o_alarmPlanDAO.get(alarmPlanId);
            Set<AlarmRegion> alarmRegions = alarmPlan.getAlarmRegions();
            for (AlarmRegion alarmRegion : alarmRegions)
            {
                o_alarmRegionDAO.delete(alarmRegion);
            }
            /*更新基本信息*/
            alarmPlan.setDesc(form.getDescs());
            alarmPlan.setType(o_dictEntryBO.findDictEntryById(form.getTypes()));
            alarmPlan.setName(form.getName());
            this.o_alarmPlanDAO.merge(alarmPlan);

            /*添加区间值信息*/
            String range = form.getRange();
            if (StringUtils.isNotBlank(range))
            {
                JSONArray rangeArr = JSONArray.fromObject(range);
                for (int i = 0; i < rangeArr.size(); i++)
                {
                    JSONObject jsobj = rangeArr.getJSONObject(i);
                    this.saveAlarmRegion(alarmPlan, jsobj);
                }
            }
        }
    }

    /**
     * <pre>
     * 根据告警id逻辑删除告警方案
     * </pre>
     * 
     * @author 陈晓哲
     * @param id 告警ID
     * @since  fhd　Ver 1.1
    */
    @Transactional
    public void removeAlarmPlanById(String id)
    {
        AlarmPlan alarmPlan = this.o_alarmPlanDAO.get(id);
        alarmPlan.setDeleteStatus(false);
        o_alarmPlanDAO.merge(alarmPlan);
    }

    
   
	
}
