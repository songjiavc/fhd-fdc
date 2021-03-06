package com.fhd.sm.business;

import static org.hibernate.FetchMode.JOIN;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.quartz.CronTrigger;
import org.quartz.impl.calendar.AnnualCalendar;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.comm.business.formula.FunctionCalculateBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.kpi.KpiGatherResultDAO;
import com.fhd.dao.kpi.KpiRelaAlarmDAO;
import com.fhd.dao.kpi.KpiRelaCategoryDAO;
import com.fhd.dao.kpi.KpiRelaDimDAO;
import com.fhd.dao.kpi.KpiRelaOrgEmpDAO;
import com.fhd.dao.kpi.SmRelaKpiDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.AlarmRegion;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiDataSource;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.KpiRelaAlarm;
import com.fhd.entity.kpi.KpiRelaCategory;
import com.fhd.entity.kpi.KpiRelaDim;
import com.fhd.entity.kpi.KpiRelaOrgEmp;
import com.fhd.entity.kpi.SmRelaKpi;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.commons.security.OperatorDetails;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.interfaces.IKpiBO;
import com.fhd.sm.web.form.KpiForm;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

/**
 * KPI_指标BO ClassName:KpiBO
 * 
 * @author 张帅
 * @version
 * @since Ver 1.1
 * @Date 2012 2012-9-17 上午13:56:52
 * 
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class KpiBO implements IKpiBO {

    @Autowired
    private KpiTableBO o_kpiTableBO;
    

    @Autowired
    private KpiDAO o_kpiDAO;

    @Autowired
    private KpiTreeBO o_kpiTreeBO;

    @Autowired
    private OrganizationBO o_organizationBO;

    @Autowired
    private DictBO o_dictEntryBO;

    @Autowired
    private KpiRelaOrgEmpDAO o_kpiRelaOrgEmpDAO;

    @Autowired
    private KpiRelaDimDAO o_kpiRelaDimDAO;

    @Autowired
    private KpiRelaAlarmDAO o_kpiRelaAlarmDAO;

    @Autowired
    private CategoryBO o_categoryBO;

    @Autowired
    private KpiRelaCategoryDAO o_kpiRelaCategoryDAO;

    @Autowired
    private SmRelaKpiDAO o_smRelaKpiDAO;

    @Autowired
    private KpiGatherResultDAO o_kpiGatherResultDAO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private FunctionCalculateBO o_functionCalculateBO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;

    
    @Autowired
    private KpiDataSourceBO o_kpiDataSourceBO;
    
    @Autowired
    private TimePeriodBO o_timePeriodBO;
    
    private static final Log logger = LogFactory.getLog(KpiBO.class);

    
    /** 查询所在公司所有可用(未删除状态)的指标.
     * @param companyId 公司id
     * @return
     */
    public List<Kpi> findAllKpiListByCompanyId(String companyId){
    	Criteria criteria = this.o_kpiDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("company.id",companyId));
        criteria.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
        criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
        criteria.setFetchMode("createBy", FetchMode.SELECT);
        criteria.addOrder(Order.desc("level"));
        //criteria.setCacheable(true);
        return criteria.list();
    }
    
    /**
     * <pre>
     * 查询所有可用(未删除状态)的指标.
     * </pre>
     * @return List<Kpi>
     */
    public List<Kpi> findAllKpiList() {
        Criteria criteria = this.o_kpiDAO.createCriteria();
        criteria.setFetchMode("alarmMeasure", FetchMode.JOIN);
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
        criteria.setFetchMode("company", FetchMode.SELECT);
        criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
        criteria.setFetchMode("createBy", FetchMode.SELECT);
        criteria.addOrder(Order.desc("level"));
        criteria.add(Restrictions.eq("calc.id", Contents.DICT_Y));
        return criteria.list();
    }
    
    /**查找所有在计算时间范围内的指标
     * @return
     */
    public List<Kpi> findAllCalcKpiList(){
    	Criteria criteria = this.o_kpiDAO.createCriteria();
        criteria.setFetchMode("alarmMeasure", FetchMode.JOIN);
        criteria.add(Restrictions.sqlRestriction("now()>=calculate_time and ADDDATE(calculate_time,RESULT_COLLECT_INTERVAL)>=now()"));
        criteria.add(Restrictions.sqlRestriction(" (LENGTH(target_formula)<>0 or LENGTH(result_formula)<>0 or LENGTH(assessment_formula)<>0)"));
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
        criteria.setFetchMode("company", FetchMode.SELECT);
        criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
        criteria.setFetchMode("createBy", FetchMode.SELECT);
        criteria.addOrder(Order.desc("level"));
        criteria.add(Restrictions.eq("calc.id", Contents.DICT_Y));
        return criteria.list();
    }
    
    /**
     * <pre>
     * 查询所有可用(未删除状态)的指标,定时任务调用.
     * </pre>
     * @return List<Kpi>
     */
    public List<Kpi> findAllKpiListByTask() {
    	Criteria criteria = this.o_kpiDAO.createCriteria();
    	criteria.createAlias("gatherFrequence", "gatherFrequence");
    	criteria.setFetchMode("gatherFrequence", FetchMode.JOIN);
    	criteria.createAlias("company", "company");
    	criteria.setFetchMode("company", FetchMode.JOIN);
    	criteria.add(Restrictions.eq("deleteStatus", true));
    	criteria.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
    	criteria.add(Restrictions.isNotNull("gatherFrequence"));
    	criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
    	criteria.setFetchMode("createBy", FetchMode.SELECT);
    	criteria.addOrder(Order.desc("level"));
    	return criteria.list();
    }

    /**
     * 根据指标ID集合查询指标
     * 
     * @param kpilist
     *            指标ID集合
     * @return 指标实体集合
     */
    public List<Kpi> findKpiListByIds(List<String> kpilist) {

        Criteria criteria = this.o_kpiDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
        criteria.setFetchMode("company", FetchMode.SELECT);
        criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
        criteria.setFetchMode("createBy", FetchMode.SELECT);
        criteria.addOrder(Order.desc("level"));
        criteria.add(Restrictions.in("id", kpilist));
        return criteria.list();
    }

    /**
     * <pre>
     * 查询所有可用(未删除状态)的指标.
     * </pre>
     * 
     * @param page
     *            分页对象
     * @param query
     *            查询条件
     * @param sort
     *            排序字段
     * @param dir
     *            排序顺序
     * @return
     */
    public Page<Kpi> findAllKpiList(Page<Kpi> page, String query, String sort, String dir,String gType) {
        String sortstr = "id";
        DetachedCriteria dc = DetachedCriteria.forClass(Kpi.class);
        dc.add(Restrictions.eq("deleteStatus", true));
        dc.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
        dc.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
        dc.add(Restrictions.eq("status.id", Contents.DICT_Y));
        if (StringUtils.isNotBlank(query)) {
            dc.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.equals("name", sort)) {
            sortstr = "name";
        }
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        if(StringUtils.isNotBlank(gType)) {
       	 dc.add(Restrictions.eq("gatherFrequence.id", gType));
       }
        else {
            dc.addOrder(Order.desc(sortstr));
        }
        return o_kpiDAO.findPage(dc, page, false);

    }

    /**
     * 根据指标id和预警类型type获取预警方案.
     * 
     * @author 陈晓哲
     * @param kpiId
     * @param type
     *            forecast：预警方案;report：告警方案;
     * @return AlarmPlan
     */
    public AlarmPlan findAlarmPlanByKpiId(String kpiId, String type) {

        String alarmType = "fcAlarmPlan";
        if ("forecast".equals(type)) {
            alarmType = "fcAlarmPlan";
        }
        else if ("report".equals(type)) {
            alarmType = "rAlarmPlan";
        }
        AlarmPlan alarmPlan = null;
        KpiRelaAlarm currentAlarm = null;
        Date currentDate = new Date();

        if (StringUtils.isNotBlank(kpiId)) {
            KpiRelaAlarm kpiRelaAlarmTmp = null;
            Criteria criteria = o_kpiRelaAlarmDAO.createCriteria();
            criteria.add(Property.forName("kpi.id").eq(kpiId));
            criteria.setFetchMode(alarmType, JOIN);
            criteria.setFetchMode("rAlarmPlan.alarmRegions", FetchMode.JOIN);
            criteria.addOrder(Order.asc("startDate"));
            boolean flag = false;
            List<KpiRelaAlarm> kpiRelaAlarms = criteria.list();
            if (null != kpiRelaAlarms && kpiRelaAlarms.size() > 0) {
                for (int i = 0; i < kpiRelaAlarms.size(); i++) {
                    KpiRelaAlarm kpiRelaAlarm = kpiRelaAlarms.get(i);
                    Date startDate = kpiRelaAlarm.getStartDate();
                    if (currentDate.before(startDate)) {
                        return null;
                    }
                    if (currentDate.equals(startDate)) {
                        currentAlarm = kpiRelaAlarm;
                        flag = true;
                        break;
                    }
                    else {
                        if (i == kpiRelaAlarms.size() - 1) {
                            kpiRelaAlarmTmp = kpiRelaAlarms.get(i);
                        }
                        else {
                            kpiRelaAlarmTmp = kpiRelaAlarms.get(i + 1);
                        }
                        if (currentDate.after(startDate) && currentDate.before(kpiRelaAlarmTmp.getStartDate())) {
                            currentAlarm = kpiRelaAlarmTmp;
                            flag = true;
                            break;
                        }
                    }

                }
                if (!flag) {
                    currentAlarm = kpiRelaAlarms.get(kpiRelaAlarms.size() - 1);
                }
                if ("forecast".equals(type)) {
                    alarmPlan = currentAlarm.getFcAlarmPlan();
                }
                else if ("report".equals(type)) {
                    alarmPlan = currentAlarm.getrAlarmPlan();
                }
            }

        }

        return alarmPlan;
    }

    /**
     * 根据评估值获取评估值预警状态.
     * 
     * @author 陈晓哲
     * @param alarmPlan
     * @param assessmentValue
     *            评估值
     * @return String 预警状态值(数据字典) 高：icon-ibm-symbol-6-sm 中：icon-ibm-symbol-5-sm
     *         低：icon-ibm-symbol-4-sm 无：""
     */
    public DictEntry findKpiAssessmentStatusByAssessmentValue(AlarmPlan alarmPlan, Double assessmentValue) {
        if (null != alarmPlan && null != assessmentValue) {
            Set<AlarmRegion> alarmRegions = alarmPlan.getAlarmRegions();
            for (AlarmRegion alarmRegion : alarmRegions) {
                // 目前公式不支持0<=0&&0<=80计算，所以，换算成if函数计算
                String minFormula = "if(" + alarmRegion.getMinValue() + alarmRegion.getIsContainMin().getName() + assessmentValue + ",1,0)";
                String maxFormula = "if(" + assessmentValue + alarmRegion.getIsContainMax().getName() + alarmRegion.getMaxValue() + ",1,0)";
                String minFormulaResult = o_functionCalculateBO.calculate(o_functionCalculateBO.strCast(minFormula));
                String maxFormulaResult = o_functionCalculateBO.calculate(o_functionCalculateBO.strCast(maxFormula));
                if ("1".equals(minFormulaResult) && "1".equals(maxFormulaResult)) {
                    return alarmRegion.getAlarmIcon();
                }
            }
        }
        return null;
    }

    /**
     * <pre>
     * 根据cron表达式算出下次执行时间
     * </pre>
     * 
     * @author 陈晓哲
     * @param formulr
     *            cron表达式
     * @return
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    private String findKpiFrequenceByFormulr(String formulr){
        CronTrigger trigger = new CronTrigger();
        try {
			trigger.setCronExpression(formulr);
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("trigger设置表达式异常:["+e.toString()+"]");
		}
        AnnualCalendar cal = new AnnualCalendar();
        trigger.computeFirstFireTime(cal);
        return DateUtils.formatDate(trigger.getNextFireTime(), "yyyy-MM-dd");
    }

    /**
     * <pre>
     * 根据指标标识获取所有下级指标
     * </pre>
     * 
     * @author 陈晓哲
     * @param id指标标识
     * @param self是否包含自己
     * @return 指标列表
     * @since fhd　Ver 1.1
     */
    public List<Kpi> findChildsKpiById(String id, boolean self) {
        List<Kpi> kpiList = new ArrayList<Kpi>();
        if (StringUtils.isNotBlank(id)) {
            Criteria criteria = this.o_kpiDAO.createCriteria();
            if (self) {// 包含自己
                criteria.add(Restrictions.or(Property.forName("parent.id").eq(id), Property.forName("id").eq(id)));
            }
            else {
                criteria.add(Property.forName("parent.id").eq(id));
            }
            kpiList = criteria.list();
        }
        return kpiList;
    }

    /**
     * 根据指标ID或类型ID查询孩子指标
     * 
     * @param page
     *            分页对象
     * @param query
     *            查询条件 名称
     * @param type
     *            查询条件 类型 "KPI":指标 ,"KC":指标类型
     * @param id
     *            指标ID或指标类型ID
     * @param sort
     *            排序字段
     * @param dir
     *            排序顺序
     * @return
     */
    public Page<Kpi> findKpiListById(Page<Kpi> page, String query, String type, String id, String sort, String dir,String gType) {
        String sortstr = "id";
        DetachedCriteria dc = DetachedCriteria.forClass(Kpi.class);
        dc.add(Restrictions.eq("deleteStatus", true));
        dc.add(Restrictions.eq("isKpiCategory", type));
        dc.add(Restrictions.eq("status.id", Contents.DICT_Y));
        if (StringUtils.isNotBlank(query)) {
            dc.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(id)) {
            dc.add(Property.forName("belongKpiCategory.id").eq(id));
        }
        if (StringUtils.equals("name", sort)) {
            sortstr = "name";
        }
        if(StringUtils.isNotBlank(gType)) {
        	 dc.add(Restrictions.eq("gatherFrequence.id", gType));
        }
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }
        return o_kpiDAO.findPage(dc, page, false);
    }

    /**
     * <pre>
     * 根据指标ID查询和指标关联的预警方案
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标标识
     * @return 预警方案列表
     * @since fhd　Ver 1.1
     */
    public List<AlarmPlan> findKpiRelaFcAlarmPlanListById(String id) {
        List<AlarmPlan> alarmPlanList = new ArrayList<AlarmPlan>();
        if (StringUtils.isNotBlank(id)) {
            Criteria criteria = o_kpiRelaAlarmDAO.createCriteria();
            criteria.add(Property.forName("kpi.id").eq(id));
            criteria.setFetchMode("fcAlarmPlan", JOIN);
            List<KpiRelaAlarm> kpiRelaAlarms = criteria.list();
            for (KpiRelaAlarm kpiRelaAlarm : kpiRelaAlarms) {
                alarmPlanList.add(kpiRelaAlarm.getFcAlarmPlan());
            }
        }
        return alarmPlanList;
    }

    /**
     * <pre>
     * 根据指标ID查询和指标关联的告警方案
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标标识
     * @return 告警方案列表
     * @since fhd　Ver 1.1
     */
    public List<AlarmPlan> findKpiRelaRAlarmPlanPlanListById(String id) {
        List<AlarmPlan> alarmPlanList = new ArrayList<AlarmPlan>();
        if (StringUtils.isNotBlank(id)) {
            Criteria criteria = o_kpiRelaAlarmDAO.createCriteria();
            criteria.add(Property.forName("kpi.id").eq(id));
            criteria.setFetchMode("rAlarmPlan", JOIN);
            List<KpiRelaAlarm> kpiRelaAlarms = criteria.list();
            for (KpiRelaAlarm kpiRelaAlarm : kpiRelaAlarms) {
                alarmPlanList.add(kpiRelaAlarm.getrAlarmPlan());
            }
        }
        return alarmPlanList;
    }

    /**
     * <pre>
     * 根据指标标识查询关联的目标集合
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标标识
     * @return 目标列表
     * @since fhd　Ver 1.1
     */
    public List<StrategyMap> findStrategyMapListById(String id) {
        List<StrategyMap> smList = new ArrayList<StrategyMap>();
        if (StringUtils.isNotBlank(id)) {
            Criteria criteria = o_smRelaKpiDAO.createCriteria();
            criteria.add(Property.forName("kpi.id").eq(id));
            criteria.setFetchMode("strategyMap", JOIN);
            List<SmRelaKpi> smRelaKpis = criteria.list();
            for (SmRelaKpi smRelaKpi : smRelaKpis) {
                smList.add(smRelaKpi.getStrategyMap());
            }
        }
        return smList;
    }

    /**
     * <pre>
     * 根据员工ID查询所关联的指标集合
     * </pre>
     * 
     * @author 陈晓哲
     * @param empId
     *            员工ID
     * @return 指标列表
     * @since fhd　Ver 1.1
     */
    public List<Kpi> findKpiListByEmpId(String empId, String type, Date date) {
        List<Kpi> kpiList = new ArrayList<Kpi>();
        Kpi kpi = null;
        if (StringUtils.isNotBlank(empId)) {
            Criteria criteria = o_kpiRelaOrgEmpDAO.createCriteria();
            criteria.createAlias("kpi", "kpi");
            criteria.add(Property.forName("emp.id").eq(empId));
            criteria.setFetchMode("kpi", JOIN);
            if (StringUtils.isNotBlank(type)) {
                criteria.add(Restrictions.eq("type", type));
            }
            if (null != date) {
                // date参数，可能与指标calculate_time和target_calculate_time比较相等不正确
                criteria.add(Restrictions.or(Restrictions.and(Restrictions.eq("type", "G"), Restrictions.le("kpi.calculatetime", date)),
                        Restrictions.and(Restrictions.eq("type", "T"), Restrictions.le("kpi.targetCalculatetime", date))));
            }
            criteria.add(Restrictions.eq("kpi.deleteStatus", true));
            List<KpiRelaOrgEmp> kpiRelaOrgEmps = criteria.list();
            for (KpiRelaOrgEmp kpiRelaOrgEmp : kpiRelaOrgEmps) {
                kpi = kpiRelaOrgEmp.getKpi();
                if ("0sys_use_formular_formula".equals(kpi.getIsTargetFormula()) && "0sys_use_formular_formula".equals(kpi.getIsResultFormula())) {
                    //如果目标值和实际值都是公式计算，不添加
                    continue;
                }
                if (!kpiList.contains(kpi)) {
                    kpiList.add(kpi);
                }
            }
        }
        return kpiList;
    }


    

    /**计算指标告警状态(供金融街使用)
     * @param kpi 指标对象
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


    /**
     * 根据id查询指标
     * 
     * @author 张帅
     * @param id
     *            指标id集合
     * @return 指标树节点列表
     * @since fhd　Ver 1.1
     */
    public List<Map<String, Object>> findKpiByid(String[] ids) {
        Map<String, Object> data = null;
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        List<Kpi> list = this.findBySome(ids, null, null, null, null, null, null, null, null, true);
        for (Kpi kpi : list) {
            data = o_kpiTreeBO.wrapKPINode(kpi, false, true, false);
            data.put("id", kpi.getId());
            datas.add(data);
        }
        return datas;
    }

    /**
     * <pre>
     * 根据指标Id查找指标对象
     * </pre>
     * 
     * @author 陈晓哲
     * @param id指标ID
     * @return 指标实体
     * @since fhd　Ver 1.1
     */
    public Kpi findKpiById(String id) {
        Kpi kpi = null;
        Criteria criteria = o_kpiDAO.createCriteria();
        //criteria.setCacheable(true);
        criteria.add(Restrictions.eq("id", id)).add(Restrictions.eq("deleteStatus", true));
        // 避免关联查询不需要的对象
        this.removeKpiRelaObject(criteria);
        List<Kpi> kpilist = criteria.list();
        if (kpilist.size() > 0) {
            kpi = kpilist.get(0);
        }
        return kpi;
    }

    /**
     * 根据公司id查询指标
     * 
     * @author 张帅
     * @param id
     *            公司ID
     * @return 指标对象
     * @since fhd　Ver 1.1
     */
    public Kpi findKpiByCompanyId(String id) {
        Kpi kpi = null;
        Criteria criteria = o_kpiDAO.createCriteria();
        //criteria.setCacheable(true);
        criteria.add(Restrictions.eq("company.id", id))
                .add(Restrictions.eq("deleteStatus", true)).add(Restrictions.isNull("parent"))
                .add(Restrictions.ne("isKpiCategory", Contents.KC_TYPE));
        // 避免关联查询不需要的对象//add by chenxiaozhe
        this.removeKpiRelaObject(criteria);
        List<Kpi> kpilist = criteria.list();
        if (kpilist.size() > 0) {
            kpi = kpilist.get(0);
        }
        return kpi;
    }

    /**
     * 根据条件查询
     * 
     * @author 张帅
     * @param id指标id
     * @param companyId
     *            公司id
     * @param parentId
     *            父节点id
     * @param isRoot
     *            是否是跟目录
     * @param statuss
     *            状态
     * @param name
     *            名称
     * @param empId
     *            所属人id
     * @param kpiRelaOrgEmpsId
     *            部门id
     * @param smRelaKpiId
     *            目标id
     * @param deleteStatus
     *            删除状态
     * @return 指标列表
     * @since fhd　Ver 1.1
     */

    public List<Kpi> findBySome(String[] ids, String companyId, String parentId, Boolean isRoot, String[] statuss, String name, String empId,
            String kpiRelaOrgEmpsId, String smRelaKpiId, Boolean deleteStatus) {
        Criteria criteria = o_kpiDAO.createCriteria();

        if (null != ids) {
            if (ids.length > 0) {
                criteria.add(Restrictions.in("id", ids));
            }
            else {
                criteria.add(Restrictions.isNull("id"));
            }
        }
        if (StringUtils.isNotBlank(companyId)) {
            criteria.add(Restrictions.eq("company.id", companyId));
        }
        else {
            criteria.setFetchMode("company", FetchMode.SELECT);
        }
        if (StringUtils.isNotBlank(parentId)) {
            criteria.add(Restrictions.eq("parent.id", parentId));
        }
        else {
            criteria.setFetchMode("parent", FetchMode.SELECT);
        }
        if (null != isRoot && isRoot) {
            criteria.add(Restrictions.isNull("parent"));
        }
        if (StringUtils.isNotBlank(name)) {
            criteria.add(Restrictions.ilike("name", name));
        }
        // 根据员工id查询所有所属指标
        if (StringUtils.isNotBlank(empId)) {
            criteria.createAlias("kpiRelaOrgEmps", "kroe");
            criteria.add(Restrictions.eq("kroe.emp.id", empId));
        }
        else {
            criteria.setFetchMode("kpiRelaOrgEmps", FetchMode.SELECT);
        }
        // 根据部门查询所有指标
        if (StringUtils.isNotBlank(kpiRelaOrgEmpsId)) {
            if (StringUtils.isNotBlank(empId)) {
                criteria.add(Restrictions.eq("kroe.org.id", kpiRelaOrgEmpsId));
            }
            else {
                criteria.createAlias("kpiRelaOrgEmps", "kroe");
                criteria.add(Restrictions.eq("kroe.org.id", kpiRelaOrgEmpsId));
            }
        }
        // 根据目标查询所有指标
        if (StringUtils.isNotBlank(smRelaKpiId)) {
            criteria.createAlias("dmRelaKpis", "drk");
            criteria.add(Restrictions.eq("drk.strategyMap.id", smRelaKpiId));
        }
        else {
            criteria.setFetchMode("dmRelaKpis", FetchMode.SELECT);
        }
        criteria.add(Restrictions.eq("deleteStatus", deleteStatus));

        // 不加载无关的关联对象//add by chenxiaozhe
        this.removeKpiRelaObject(criteria);

        return criteria.list();
    }

    /**
     * <pre>
     * 根据指标实体查找部门信息并封装成json格式
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpi
     *            指标对象
     * @return 查找部门信息并封装成json格式
     * @since fhd　Ver 1.1
     */
    public JSONObject findKpiRelaOrgEmpBySmToJson(Kpi kpi) {
        JSONObject jsobj = new JSONObject();
        if (null != kpi) {
            String type = "";
            JSONArray blongArr = new JSONArray();
            JSONArray reportArr = new JSONArray();
            JSONArray viewArr = new JSONArray();
            JSONArray gatherArr = new JSONArray();
            JSONArray targetArr = new JSONArray();
            SysEmployee sysEmp = null;
            SysOrganization sysOrg = null;

            Criteria criteria = this.o_kpiRelaOrgEmpDAO.createCriteria();
            //criteria.setCacheable(true);
            criteria.add(Restrictions.eq("kpi.id", kpi.getId())).setFetchMode("org", JOIN).setFetchMode("emp", JOIN);
            // 不加载无关的关联对象
            criteria.setFetchMode("kpi.units", FetchMode.SELECT).setFetchMode("kpi.createBy", FetchMode.SELECT)
                    .setFetchMode("kpi.lastModifyBy", FetchMode.SELECT).setFetchMode("kpi.relativeTo", FetchMode.SELECT)
                    .setFetchMode("kpi.kpiType", FetchMode.SELECT).setFetchMode("kpi.alarmBasis", FetchMode.SELECT)
                    .setFetchMode("kpi.parent", FetchMode.SELECT).setFetchMode("kpi.alarmMeasure", FetchMode.SELECT)
                    .setFetchMode("kpi.belongKpiCategory", FetchMode.SELECT).setFetchMode("kpi.dataType", FetchMode.SELECT)
                    .setFetchMode("kpi.status", FetchMode.SELECT).setFetchMode("kpi.type", FetchMode.SELECT)
                    .setFetchMode("kpi.gatherFrequence", FetchMode.SELECT).setFetchMode("kpi.reportFrequence", FetchMode.SELECT);
            List<KpiRelaOrgEmp> kpiRelaOrgEmpSet = criteria.list();
            for (KpiRelaOrgEmp kpiRelaOrgEmp : kpiRelaOrgEmpSet) {
                type = kpiRelaOrgEmp.getType();
                JSONObject blongobj = new JSONObject();
                sysOrg = kpiRelaOrgEmp.getOrg();
                sysEmp = kpiRelaOrgEmp.getEmp();
                try {
                    if (null != sysOrg) {
                        blongobj.put("deptid", sysOrg.getId());
                        blongobj.put("deptno", sysOrg.getOrgcode());
                        blongobj.put("deptname", sysOrg.getOrgname());
                    }
                    else {
                        blongobj.put("deptid", "");
                    }
                }
                catch (ObjectNotFoundException e) {
                    blongobj.put("deptid", "");
                }
                try {
                    if (null != sysEmp) {
                        blongobj.put("empid", sysEmp.getId());
                        blongobj.put("empno",sysEmp.getEmpcode());
                        blongobj.put("empname",sysEmp.getEmpname());
                    }
                    else {
                        blongobj.put("empid", "");
                    }
                }
                catch (ObjectNotFoundException e) {
                    blongobj.put("empid", "");
                }
                if (Contents.BELONGDEPARTMENT.equals(type)) {
                    blongArr.add(blongobj);
                }
                if (Contents.REPORTDEPARTMENT.equals(type)) {
                    reportArr.add(blongobj);
                }
                if (Contents.VIEWDEPARTMENT.equals(type)) {
                    viewArr.add(blongobj);
                }
                if (Contents.GATHERDEPARTMENT.equals(type)) {
                    gatherArr.add(blongobj);
                }
                if (Contents.TARGETDEPARTMENT.equals(type)) {
                    targetArr.add(blongobj);
                }
            }
            jsobj.put("ownDept", blongArr);
            jsobj.put("reportDept", reportArr);
            jsobj.put("viewDept", viewArr);
            jsobj.put("gatherDept", gatherArr);
            jsobj.put("targetDept", targetArr);
        }
        return jsobj;
    }

    /**
     * <pre>
     * 查询指标关联的预警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            预警方案名称
     * @param id
     *            指标ID
     * @param sort
     *            排序字段
     * @param dir
     *            排序方向
     * @return 指标关联的预警信息
     * @since fhd　Ver 1.1
     */
    public List<KpiRelaAlarm> findKpiRelaAlarmBySome(String name, String id, String editflag, String sort, String dir) {
        String sortstr = "id";
        Criteria dc = this.o_kpiRelaAlarmDAO.createCriteria();
        dc.createAlias("kpi", "kpi");
        dc.add(Restrictions.eq("kpi.company.id", UserContext.getUser().getCompanyid()));
        if (StringUtils.isNotBlank(name)) {
            dc.createAlias("fcAlarmPlan", "fcAlarmPlan")
                    .createAlias("rAlarmPlan", "rAlarmPlan")
                    .add(Restrictions.or(Property.forName("fcAlarmPlan.name").like(name, MatchMode.ANYWHERE), Property.forName("rAlarmPlan.name")
                            .like(name, MatchMode.ANYWHERE)));
        }
        if (StringUtils.isNotBlank(sort) && !StringUtils.equals("id", sort)) {
            dc.createAlias("fcAlarmPlan", "fcAlarmPlan").createAlias("rAlarmPlan", "rAlarmPlan");
            if (StringUtils.equals("alarm", sort)) {
                sortstr = "rAlarmPlan.name";
            }
            else if (StringUtils.equals("warning", sort)) {
                sortstr = "fcAlarmPlan.name";
            }
            else if (StringUtils.equals("date", sort)) {
                sortstr = "startDate";
            }
        }
        dc.add(Property.forName("kpi.id").eq(id));
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }

        List<KpiRelaAlarm> list = dc.list();
        if (!"true".equals(editflag) && null != list && list.size() == 0) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(Integer.valueOf(DateUtils.getYear(new Date())), 0, 1);
            // 判断,如果没有结果集,则默认添加全局告警信息
            AlarmPlan forecast = o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast");
            AlarmPlan alarm = o_alarmPlanBO.findAlarmPlanByNameType("常用预警方案", "0alarm_type_kpi_alarm");
            if (null != forecast || null != alarm) {
                list = new ArrayList<KpiRelaAlarm>();
                KpiRelaAlarm kpiRelaAlarm = new KpiRelaAlarm();
                kpiRelaAlarm.setId("");
                kpiRelaAlarm.setrAlarmPlan(forecast);
                kpiRelaAlarm.setFcAlarmPlan(alarm);
                kpiRelaAlarm.setStartDate(calendar.getTime());
                list.add(kpiRelaAlarm);
            }
        }
        return list;
    }

    /**
     * <pre>
     * 生成指标类型代码
     * </pre>
     * 
     * @author 陈晓哲
     * @return 指标类型编码值
     * @since fhd　Ver 1.1
     */
    public String findKpiTypeCode(String id) {
        Kpi kpi = null;
        StringBuffer code = new StringBuffer();
        if (StringUtils.isNotBlank(id) && !Contents.ID_UNDEFINED.equals(id) && !"null".equals(id)) {
            kpi = this.findKpiById(id);
            if (null != kpi.getCode()) {
                return kpi.getCode();
            }
        }
        Long count = (Long) this.o_kpiDAO.createCriteria().add(Restrictions.eq("isKpiCategory", Contents.KC_TYPE))
                .add(Restrictions.eq("deleteStatus", true)).add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()))
                .setProjection(Projections.rowCount()).uniqueResult();
        code.append("ZBLX000").append(count + 1);
        return code.toString();
    }

    /**
     * 根据idseq查询指标对象
     * 
     * @param idSeq
     * @return map <idseq,kpi>
     */
    public Map<String, Kpi> findKpiByIdSeq(String idSeq) {
        Map<String, Kpi> map = new HashMap<String, Kpi>();
        if (StringUtils.isNotBlank(idSeq)) {
            String[] idarr = StringUtils.split(idSeq, ".");
            Criteria criteria = o_kpiDAO.createCriteria();
            criteria.add(Restrictions.in("id", idarr));
            List<Kpi> kpilist = criteria.list();
            for (Kpi kpi : kpilist) {
                map.put(kpi.getId(), kpi);
            }
        }
        return map;
    }

    /**
     * <pre>
     * 生成指标编码
     * </pre>
     * 
     * @author 陈晓哲
     * @param parentId
     *            父节点ID
     * @param id
     *            当前节点ID
     * @return 指标编码值
     * @since fhd　Ver 1.1
     */
    public String findKpiCode(String parentId, String id) {
        long sort = 0;
        long count = 0;
        Kpi kpi = null;
        Kpi pkpi = null;
        StringBuffer code = new StringBuffer();
        if (StringUtils.isNotBlank(id) && !Contents.ID_UNDEFINED.equals(id)) {
            kpi = findKpiById(id);
            if(null!=kpi){
            	if(null!=kpi.getSort()){
                	sort = kpi.getSort();
                }else{
                	sort = 0;
                }
            	if (null != kpi.getCode()) {
                    return kpi.getCode();
                }
            }
            
        }
        if (Contents.KPI_ROOT.equals(parentId)) {
            code.append("ZB");
        }
        else {
            if (StringUtils.isNotBlank(parentId)) {
                pkpi = findKpiById(parentId);
                String idSeq = pkpi.getIdSeq();
                if (StringUtils.isNotBlank(idSeq)) {
                    Map<String, Kpi> kpiMap = findKpiByIdSeq(idSeq);
                    String[] idarr = StringUtils.split(idSeq, ".");
                    for (int i = 0; i < idarr.length; i++) {
                        if (StringUtils.isNotBlank(idarr[i])) {
                            kpi = kpiMap.get(idarr[i]);
                            String codeno = kpi.getCode();
                            if (null != codeno && StringUtils.isNotBlank(codeno)) {
                                if (!codeno.contains(code)) {
                                    code.append(codeno).append("-");
                                }
                                else {
                                    code.append(codeno.substring(code.length())).append("-");
                                }
                            }
                            else {
                                if (null == kpi.getParent() && null != kpi.getLevel() && 1 != kpi.getLevel()) {
                                    code.append("ZB");
                                }
                                else {
                                    if (code.indexOf("ZB") == -1) {
                                        code.append("ZB");
                                    }
                                    code.append("000").append(kpi.getSort()).append("-");
                                }
                            }
                        }
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(id) && !Contents.ID_UNDEFINED.equals(id)) {
            count = sort;
            code.append("000").append(count);
        }
        else {
            if (Contents.KPI_ROOT.equals(parentId)) {
                count = (Long) this.o_kpiDAO.createCriteria().add(Restrictions.isNull("parent")).add(Restrictions.eq("deleteStatus", true))
                        .setProjection(Projections.rowCount()).uniqueResult();
            }
            else {
                count = (Long) this.o_kpiDAO.createCriteria().add(Restrictions.eq("parent", pkpi)).add(Restrictions.eq("deleteStatus", true))
                        .setProjection(Projections.rowCount()).uniqueResult();
            }
            code.append("000").append(count + 1);
        }

        return code.toString();
    }

    /**
     * <pre>
     * 根据查询条件查询指标对象的个数
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            指标名称
     * @param id
     *            指标对象
     * @return
     * @since fhd　Ver 1.1
     */
    public long findKpiCountByName(String name, String id, String type) {
        long count = 0;
        Criteria criteria = this.o_kpiDAO.createCriteria().add(Restrictions.eq("deleteStatus", true));
        // 添加了根据公司过滤 -- 胡迪新
        criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
        criteria.add(Restrictions.eq("isKpiCategory", type));
        if (StringUtils.isNotBlank(id)) {// update
            count = (Long) criteria.add(Restrictions.eq("name", name)).add(Restrictions.ne("id", id)).setProjection(Projections.rowCount())
                    .uniqueResult();
        }
        else {// add
            count = (Long) criteria.add(Restrictions.eq("name", name)).setProjection(Projections.rowCount()).uniqueResult();
        }
        return count;
    }

    /**
     * <pre>
     * 根据code查找指标对象数量
     * </pre>
     * 
     * @author 陈晓哲
     * @param code
     *            指标编码
     * @param id
     *            指标ID
     * @return
     * @since fhd　Ver 1.1
     */
    public long findKpiCountByCode(String code, String id, String type) {
        long count = 0;
        Criteria criteria = this.o_kpiDAO.createCriteria().add(Restrictions.eq("deleteStatus", true));
        // 添加了根据公司过滤 -- 胡迪新
        criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
        criteria.add(Restrictions.eq("isKpiCategory", type));
        if (StringUtils.isNotBlank(id) && !Contents.ID_UNDEFINED.equals(id)) {// update
            if (StringUtils.isNotBlank(code)) {
                count = (Long) criteria.add(Restrictions.eq("code", code)).add(Restrictions.ne("id", id)).setProjection(Projections.rowCount())
                        .uniqueResult();
            }
        }
        else {// add
            if (StringUtils.isNotBlank(code)) {
                count = (Long) criteria.add(Restrictions.eq("code", code)).setProjection(Projections.rowCount()).uniqueResult();
            }

        }
        return count;
    }

    /**
     * <pre>
     * 根据指标类型Id查找该类型下的指标数量
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标类型ID
     * @return
     * @since fhd　Ver 1.1
     */
    public long findKpiCountByType(String id) {
        long count = (Long) this.o_kpiDAO.createCriteria().add(Restrictions.eq("belongKpiCategory.id", id))
                .add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid())).add(Restrictions.eq("deleteStatus", true))
                .setProjection(Projections.rowCount()).uniqueResult();
        return count;
    }

    /**
     * <pre>
     * 查找所有的指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param page
     *            分页对象
     * @param query
     *            查询条件
     * @param sort
     *            排序字段
     * @param dir
     *            排序方向
     * @return
     * @since fhd　Ver 1.1
     */
    public List<Kpi> findKpiTypeAll(String query, String sort, String dir) {
        String sortstr = "id";
        Criteria criteria = o_kpiDAO.createCriteria().add(Restrictions.eq("isKpiCategory", Contents.KC_TYPE));
        criteria.add(Restrictions.eq("deleteStatus", true));
        // 添加按公司过滤 -- 胡迪新
        criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
        removeKpiRelaObject(criteria);
        if (StringUtils.isNotBlank(query)) {
            criteria.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.equals("name", sort)) {
            sortstr = "name";
        }
        if ("ASC".equalsIgnoreCase(dir)) {
            criteria.addOrder(Order.asc(sortstr));
        }
        else {
            criteria.addOrder(Order.desc(sortstr));
        }
        return criteria.list();
    }

    /**
     * <pre>
     * 根据指标ID查找所属的指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId
     *            指标ID
     * @return
     * @since fhd　Ver 1.1
     */
    public JSONObject findKpiTypeByKid(String kpiId) {
        JSONObject jsobj = new JSONObject();
        if (StringUtils.isNotBlank(kpiId)) {
            Kpi kpi = findKpiById(kpiId);
            if (null != kpi) {
                Kpi belongKpi = kpi.getBelongKpiCategory();
                if (null != belongKpi) {
                    jsobj.put("id", belongKpi.getId());
                    jsobj.put("name", belongKpi.getName());
                    jsobj.put("type", Contents.KC_TYPE);
                }
            }
        }
        return jsobj;
    }

    /**
     * <pre>
     * 根据指标ID查询该指标的所有采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标ID
     * @return 指标列表
     * @since fhd　Ver 1.1
     */
    public List<Kpi> findKpiGatherResultByKpiId(String id) {
        Criteria criteria = o_kpiDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true)).add(Restrictions.ne("isKpiCategory", Contents.KC_TYPE)).add(Restrictions.eq("id", id));
        // 去掉无关对象
        removeKpiRelaObject(criteria);
        criteria.createAlias("kpiGatherResult", "kpiGatherResult", CriteriaSpecification.LEFT_JOIN);
        criteria.createAlias("kpiGatherResult.timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN).addOrder(Order.asc("timePeriod.startTime"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.createAlias("kpiGatherResult.assessmentStatus", "assessmentStatus", CriteriaSpecification.LEFT_JOIN);
        criteria.createAlias("kpiGatherResult.direction", "direction", CriteriaSpecification.LEFT_JOIN);

        return criteria.list();
    }

    /**
     * 查询所有指标最新的关联指标的采集结果
     * 
     * @param map
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param queryName
     *            指标类型名称
     * @param sortColumn
     *            排序字段
     * @param dir
     *            排序方向
     * @param    myflag 我的指标标示
     * @return
     */
    public List<Object[]> findLastGatherResults(Map<String, Object> map, int start, int limit, String queryName, String sortColumn, String dir,
            boolean myflag) {
        String companyid = UserContext.getUser().getCompanyid();
        String empid = UserContext.getUser().getEmpid();
        List<Object[]> list = null;
        Map<String, Object> paraMap = new HashMap<String, Object>();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromLeftJoinBuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
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
        selectBuf.append(" ,dirdict.dict_entry_value  direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid,kpi.is_focus  ");
        selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence , kpi.escale  escale");
        countBuf.append(" select count(*) ");

        fromLeftJoinBuf.append(" from t_kpi_kpi  kpi");
        if (myflag) {
            fromLeftJoinBuf.append(" inner join t_kpi_kpi_rela_org_emp emp on emp.kpi_id=kpi.id ");
        }
        fromLeftJoinBuf.append(" left outer join t_kpi_kpi_gather_result  result on kpi.id=result.kpi_id and kpi.latest_time_period_id=result.time_period_id ");
        fromLeftJoinBuf.append(" left outer join t_com_time_period  time on result.time_period_id=time.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
        fromLeftJoinBuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI'  and 1=1 ");

        if (StringUtils.isNotBlank(companyid)) {
            fromLeftJoinBuf.append(" and kpi.company_id=:COMPANY_ID ");
            paraMap.put("COMPANY_ID",companyid);
        }

        if (StringUtils.isNotBlank(queryName)) {
            fromLeftJoinBuf.append(" and kpi.kpi_name like :QUERY");
            paraMap.put("QUERY","%"+queryName+"%");
        }

        if (myflag && StringUtils.isNotBlank(empid)) {
            fromLeftJoinBuf.append(" and emp.emp_id=:EMP_ID ");
            paraMap.put("EMP_ID",empid);
            fromLeftJoinBuf.append(" and emp.etype='B' ");
        }
        SQLQuery countQuery = o_kpiDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).toString(), paraMap);
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

        SQLQuery sqlquery = o_kpiDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(orderbuf).toString(), paraMap);
        sqlquery.setFirstResult(start);
        sqlquery.setMaxResults(limit);
        list = sqlquery.list();

        return list;
    }

    /**
     * 查询所有指标具体的查询条件的关联指标的采集结果
     * 
     * @param map
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param queryName
     *            指标类型名称
     * @param sortColumn
     *            排序字段
     * @param dir
     *            排序方向
     * @param frequence
     *            指标采集频率
     * @return
     */
    public List<Object[]> findSpecificGatherResults(Map<String, Object> map, int start, int limit, String queryName, String sortColumn, String dir,
            String year, String quarter, String month, String week, String frequence, boolean myflag) {
        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        String empid = UserContext.getUser().getEmpid();
        Map<String, Object> paraMap = new HashMap<String, Object>();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromLeftJoinBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
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
        selectBuf.append(",dirdict.dict_entry_value  direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid ,kpi.is_focus");
        selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence ,kpi.escale  escale ");
        countBuf.append(" select count(*) ");
        fromLeftJoinBuf.append(" from t_kpi_kpi   kpi ");
        if (myflag) {
            fromLeftJoinBuf.append(" inner join t_kpi_kpi_rela_org_emp emp on emp.kpi_id=kpi.id ");
        }
        fromLeftJoinBuf.append(" left outer join ");
        fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result   result  ");
        fromLeftJoinBuf.append(" inner join ");
        fromLeftJoinBuf.append(" t_com_time_period   time on result.time_period_id=time.id  ");
        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) && StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
            fromLeftJoinBuf.append("  and time.id=:TIME_ID");
            paraMap.put("TIME_ID", year);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(month)) {
            fromLeftJoinBuf.append("  and time.id=:TIME_ID");
            paraMap.put("TIME_ID", quarter);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
            fromLeftJoinBuf.append("  and time.id=:TIME_ID");
            paraMap.put("TIME_ID", month);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isNotBlank(week)) {
            fromLeftJoinBuf.append("  and time.id=:TIME_ID");
            paraMap.put("TIME_ID", week);
        }
        fromLeftJoinBuf.append(" ) on kpi.id=result.kpi_id and kpi.gather_frequence=time.etype ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  frequencedict on kpi.gather_frequence = frequencedict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
        wherebuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI' and 1=1 ");
        if (StringUtils.isNotBlank(companyid)) {
            wherebuf.append(" and kpi.company_id=:COMPANY_ID ");
            paraMap.put("COMPANY_ID", companyid);
        }
        if (StringUtils.isNotBlank(queryName)) {
            wherebuf.append(" and kpi.kpi_name like :QUERY ");
            paraMap.put("QUERY", "%"+queryName+"%");
        }
        if (myflag && StringUtils.isNotBlank(empid)) {
            wherebuf.append(" and emp.emp_id=:EMP_ID ");
            paraMap.put("EMP_ID",empid);
            wherebuf.append(" and emp.etype='B' ");
        }
        SQLQuery countQuery = o_kpiDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).append(wherebuf).toString(), paraMap);
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

        SQLQuery sqlquery = o_kpiDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).append(orderbuf).toString(), paraMap);
        sqlquery.setFirstResult(start);
        sqlquery.setMaxResults(limit);
        list = sqlquery.list();

        return list;
    }

    /**
     * 根据指标类型ID查询具体的关联指标的采集结果
     * 
     * @param map
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param queryName
     *            指标类型名称
     * @param sortColumn
     *            排序字段
     * @param dir
     *            排序方向
     * @param kpitypeid
     *            指标类型ID
     * @param frequence
     *            指标采集频率
     * @return
     */
    public List<Object[]> findSpecificGatherResultByKpiTypeid(Map<String, Object> map, int start, int limit, String queryName, String sortColumn,
            String dir, String kpitypeid, String year, String quarter, String month, String week, String frequence) {

        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        if (StringUtils.isNotBlank(kpitypeid) && !Contents.ID_UNDEFINED.equals(kpitypeid)) {
        	Map<String, Object> paraMap = new HashMap<String, Object>();
            StringBuffer selectBuf = new StringBuffer();
            StringBuffer fromLeftJoinBuf = new StringBuffer();
            StringBuffer wherebuf = new StringBuffer();
            StringBuffer countBuf = new StringBuffer();
            StringBuffer orderbuf = new StringBuffer();
            StringBuffer statusColumn = new StringBuffer();
            statusColumn.append(" case when statusdict.dict_entry_value is null then ");
            if(dir.equals("ASC")){
            	statusColumn.append(" 'nothing' "); 
            }else{
            	statusColumn.append(" 'anothing' ");
            }
            statusColumn.append(" else statusdict.dict_entry_value end status ");
            selectBuf.append(" select kpi.id   id, kpi.kpi_name   name , time.time_period_full_name   timerange , result.assessment_value   assessmentValue ,result.target_value   targetValue , result.finish_value   finishValue ,");
            selectBuf.append(statusColumn);
            selectBuf.append("  ,dirdict.dict_entry_value   direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid,kpi.is_focus  ");
            
            selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence ,kpi.escale  escale ");
            countBuf.append(" select count(*) ");
            fromLeftJoinBuf.append(" from t_kpi_kpi   kpi ");
            fromLeftJoinBuf.append(" left outer join ");
            fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result   result  ");
            fromLeftJoinBuf.append(" inner join ");
            fromLeftJoinBuf.append(" t_com_time_period   time on result.time_period_id=time.id  ");
            if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) && StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
                fromLeftJoinBuf.append("  and time.id=:TIME_ID ");
                paraMap.put("TIME_ID", year);
            }
            if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(month)) {
                fromLeftJoinBuf.append("  and time.id=:TIME_ID ");
                paraMap.put("TIME_ID", quarter);
            }
            if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
                fromLeftJoinBuf.append("  and time.id=:TIME_ID ");
                paraMap.put("TIME_ID", month);
            }
            if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isNotBlank(week)) {
                fromLeftJoinBuf.append("  and time.id=:TIME_ID ");
                paraMap.put("TIME_ID", week);
            }
            fromLeftJoinBuf.append(" ) on kpi.id=result.kpi_id and kpi.gather_frequence=time.etype ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry   statusdict on result.assessment_status = statusdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry   dirdict on result.direction = dirdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
            wherebuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI'  and 1=1 ");
            if (StringUtils.isNotBlank(kpitypeid)) {
                wherebuf.append(" and kpi.belong_kpi_category=:KPI_CATEGORY ");
                paraMap.put("KPI_CATEGORY", kpitypeid);
            }
            if (StringUtils.isNotBlank(queryName)) {
                wherebuf.append(" and kpi.kpi_name like :QUERY ");
                paraMap.put("QUERY", "%"+queryName+"%");
            }
            if (StringUtils.isNotBlank(companyid)) {
                wherebuf.append(" and kpi.company_id=:COMPANY_ID ");
                paraMap.put("COMPANY_ID", companyid);
            }

            SQLQuery countQuery = o_kpiDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).append(wherebuf).toString(), paraMap);
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

            SQLQuery sqlquery = o_kpiDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).append(orderbuf).toString(), paraMap);
            sqlquery.setFirstResult(start);
            sqlquery.setMaxResults(limit);
            list = sqlquery.list();
        }
        return list;
    }

    /**
     * 根据指标类型ID查询最新的关联指标的采集结果
     * 
     * @param map
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param queryName
     *            指标类型名称
     * @param sortColumn
     *            排序字段
     * @param dir
     *            排序方向
     * @param kpitypeid
     *            指标类型ID
     * @return
     */
    public List<Object[]> findLastGatherResultByKpiTypeid(Map<String, Object> map, int start, int limit, String queryName, String sortColumn,
            String dir, String kpitypeid) {
        List<Object[]> list = null;
        if (StringUtils.isNotBlank(kpitypeid) && !Contents.ID_UNDEFINED.equals(kpitypeid)) {
            String companyid = UserContext.getUser().getCompanyid();
            StringBuffer selectBuf = new StringBuffer();
            Map<String, Object> paraMap = new HashMap<String, Object>();
            StringBuffer fromLeftJoinBuf = new StringBuffer();
            StringBuffer countBuf = new StringBuffer();
            StringBuffer orderbuf = new StringBuffer();
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
            selectBuf.append("  ,dirdict.dict_entry_value  direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid,kpi.is_focus  ");
            selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence ,kpi.escale  escale ");
            countBuf.append(" select count(*) ");
            fromLeftJoinBuf.append(" from t_kpi_kpi  kpi left outer join t_kpi_kpi_gather_result  result on kpi.id=result.kpi_id and kpi.latest_time_period_id=result.time_period_id ");
            fromLeftJoinBuf.append(" left outer join t_com_time_period  time on result.time_period_id=time.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
            fromLeftJoinBuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category=:KPI_CATEGORY  and 1=1 ");
            paraMap.put("KPI_CATEGORY", "KPI");

            if (StringUtils.isNotBlank(kpitypeid)) {
                fromLeftJoinBuf.append(" and kpi.belong_kpi_category=:BELONG_KPI_CATEGORY ");
                paraMap.put("BELONG_KPI_CATEGORY",kpitypeid);
            }
            if (StringUtils.isNotBlank(companyid)) {
                fromLeftJoinBuf.append(" and kpi.company_id=:COMPANY_ID ");
                paraMap.put("COMPANY_ID",companyid);
            }
            if (StringUtils.isNotBlank(queryName)) {
                fromLeftJoinBuf.append(" and kpi.kpi_name like :QUERY ");
                paraMap.put("QUERY", "%"+queryName+"%");
            }
            SQLQuery countQuery = o_kpiDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).toString(), paraMap);

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
            SQLQuery sqlquery = o_kpiDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(orderbuf).toString(), paraMap);
            sqlquery.setFirstResult(start);
            sqlquery.setMaxResults(limit);
            list = sqlquery.list();
        }
        return list;
    }

    /**
     * <pre>
     * 根据指标名称查询指标信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            指标名称
     * @return 指标对象
     * @since fhd　Ver 1.1
     */
    public Kpi findKpiByName(String name) {
        String companyId = "";
        OperatorDetails userDetails = UserContext.getUser();// 所在公司id
        if(null!=userDetails){
            companyId = userDetails.getCompanyid();
        }
        Kpi kpi = null;
        if (StringUtils.isNotBlank(name)) {
            Criteria criteria = this.o_kpiDAO.createCriteria();
            criteria.add(Property.forName("name").eq(name));
            criteria.add(Restrictions.eq("deleteStatus", true));
            if(StringUtils.isNotBlank(companyId)){
                criteria.add(Restrictions.eq("company.id", companyId));
            }
            criteria.add(Restrictions.eq("isKpiCategory", "KPI"));
            removeKpiRelaObject(criteria);
            List<Kpi> listKpi = criteria.list();
            if (listKpi.size() > 0) {
                return listKpi.get(0);
            }
        }
        return kpi;
    }
    
    /**根据指标名称获得采集频率
     * @param name 指标名称
     * @return
     */
    public String findKpiFrequenceByName(String name){
        String frequence = "";
        Kpi kpi = findKpiByName(name);
        DictEntry gatherFrequence = kpi.getGatherFrequence();
        if (null != gatherFrequence) {
            frequence = gatherFrequence.getId();
        }
        return frequence;
    }

    /**
     * 通过指标ID数组查询
     * 
     * @author 金鹏祥
     * @param id
     * @return
     */
    public List<Kpi> findKpiById(String[] id) {
        Criteria criteria = this.o_kpiDAO.createCriteria();
        criteria.add(Property.forName("id").in(id));
        removeKpiRelaObject(criteria);
        return criteria.list();
    }

    /**
     * 
     * 
     * @author 金鹏祥
     * @param kpiIds
     * @return
     */
    public List<KpiGatherResult> findKpiAndGatherResult(String ids, boolean isNewValue, String year) {
        StringBuffer sql = new StringBuffer();
        List<KpiGatherResult> kpiList = new ArrayList<KpiGatherResult>();
        if(StringUtils.isNotBlank(ids)){
        	KpiGatherResult result = null;
            DictEntry dictEntry = null;
            Kpi kpi = null;
            TimePeriod timePeriod = null;
            TimePeriod latestTimePeriodId = null;
            if (!isNewValue) {
                sql.append(" select kpi.id,kpi.gather_frequence,result.time_period_id,result.TARGET_VALUE,result.FINISH_VALUE,kpi.LATEST_TIME_PERIOD_ID,time.EMONTH ");
                sql.append(" from t_kpi_kpi kpi , t_kpi_kpi_gather_result result  ,t_com_time_period time ");
                sql.append(" where result.kpi_id=kpi.id and result.kpi_id in (:KPI_IDS) ");
                sql.append(" and result.time_period_id = time.id and kpi.gather_frequence = time.ETYPE ");
                sql.append(" and time.eyear = (select t1.eyear from t_com_time_period t1 where kpi.LATEST_TIME_PERIOD_ID =t1.id)  order by result.time_period_id");
            }
            else {
                sql.append(" select kpi.id,kpi.gather_frequence,result.time_period_id,result.TARGET_VALUE,result.FINISH_VALUE,kpi.LATEST_TIME_PERIOD_ID,time.EMONTH ");
                sql.append(" from t_kpi_kpi  kpi , t_kpi_kpi_gather_result  result  ,t_com_time_period  time ");
                sql.append(" where result.kpi_id=kpi.id and result.kpi_id in (:KPI_IDS) ");
                sql.append(" and result.time_period_id = time.id and kpi.gather_frequence = time.ETYPE and result.time_period_id like :QUERY ");
                sql.append(" order by result.time_period_id ");

            }

            SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(sql.toString());
            sqlQuery.setParameterList("KPI_IDS", StringUtils.split(ids,","));
            if (isNewValue){
            	sqlQuery.setParameter("QUERY", "%"+year+"%");
            }
            List<Object[]> list = sqlQuery.list();
            for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
                Object[] objects = (Object[]) iterator.next();
                result = new KpiGatherResult();
                dictEntry = new DictEntry();
                kpi = new Kpi();
                timePeriod = new TimePeriod();
                latestTimePeriodId = new TimePeriod();

                String kpiId = "";
                String frequence = "";
                String timePeriodId = "";
                Double targetValue = null;
                Double finishValue = null;
                String latestTime = "";
                String month = "";

                if (objects[0] != null) {
                    kpiId = objects[0].toString();
                }
                if (objects[1] != null) {
                    frequence = objects[1].toString();
                }
                if (objects[2] != null) {
                    timePeriodId = objects[2].toString();
                }
                if (objects[3] != null) {
                    targetValue = Double.valueOf(objects[3].toString());
                }
                if (objects[4] != null) {
                    finishValue = Double.valueOf(objects[4].toString());
                }
                if (objects[5] != null) {
                    latestTime = objects[5].toString();
                }
                if (objects[6] != null) {
                    month = objects[6].toString();
                }

                latestTimePeriodId.setId(latestTime);
                dictEntry.setId(frequence);
                kpi.setId(kpiId);
                kpi.setGatherFrequence(dictEntry);
                kpi.setLastTimePeriod(latestTimePeriodId);
                result.setKpi(kpi);
                timePeriod.setId(timePeriodId);
                timePeriod.setMonth(month);
                result.setTimePeriod(timePeriod);
                result.setTargetValue(targetValue);
                result.setFinishValue(finishValue);
                kpiList.add(result);
            }
        }
        

        return kpiList;
    }

    /**
     * 得到年采集结果集合最新值
     * 
     * @author 金鹏祥
     * @param kpiIds
     * @return
     */
    public Map<String, String> findKpiAndGatherResultYearNewValue(String ids) {
        StringBuffer sql = new StringBuffer();
        HashMap<String, String> map = new HashMap<String, String>();
        if(StringUtils.isNotBlank(ids)){
        	sql.append(" select kpi.id,kpi.gather_frequence,result.time_period_id,result.TARGET_VALUE,result.FINISH_VALUE,"
                    + " kpi.LATEST_TIME_PERIOD_ID,time.EMONTH ");
            sql.append(" from t_kpi_kpi kpi , t_kpi_kpi_gather_result result  ,t_com_time_period time ");
            sql.append(" where result.kpi_id=kpi.id and KPI.gather_frequence = '0frequecy_year'  and result.kpi_id in (:KPI_IDS) ");
            sql.append(" and result.time_period_id = time.id and kpi.gather_frequence = time.ETYPE ");
            sql.append(" and time.eyear = (select t1.eyear from t_com_time_period t1 where kpi.LATEST_TIME_PERIOD_ID =t1.id)  "
                    + " order by result.time_period_id");
            SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(sql.toString());
            sqlQuery.setParameterList("KPI_IDS", StringUtils.split(ids,","));
            List<Object[]> list = sqlQuery.list();
            for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
                Object[] objects = (Object[]) iterator.next();

                String kpiId = "";
                String timePeriodId = "";

                if (objects[0] != null) {
                    kpiId = objects[0].toString();
                }
                if (objects[2] != null) {
                    timePeriodId = objects[2].toString();
                }

                map.put(kpiId, timePeriodId);

            }
        }
        

        return map;
    }

    /**
     * 得到年份的前5年数据
     * 
     * @author 金鹏祥
     * @param kpiIds
     * @return
     */
    public List<KpiGatherResult> findKpiAndGatherResultYear(String id, String year) {
        StringBuffer sql = new StringBuffer();
        List<KpiGatherResult> kpiList = new ArrayList<KpiGatherResult>();
        KpiGatherResult result = null;
        DictEntry dictEntry = null;
        Kpi kpi = null;
        TimePeriod timePeriod = null;
        TimePeriod latestTimePeriodId = null;

        sql.append(" select  kpi.id, ");
        sql.append(" kpi.gather_frequence, ");
        sql.append(" result.time_period_id, ");
        sql.append(" result.TARGET_VALUE, ");
        sql.append(" result.FINISH_VALUE, ");
        sql.append(" kpi.LATEST_TIME_PERIOD_ID ");
        sql.append(" from t_kpi_kpi_gather_result result , t_kpi_kpi kpi  ");
        sql.append(" where result.kpi_id=kpi.id ");
        sql.append(" and kpi_id = :KPI_ID  and KPI.gather_frequence = '0frequecy_year' ");
        sql.append(" and result.time_period_id in (:YEAR_IDS)  ORDER BY result.time_period_id");

        SQLQuery sqlQuery = o_kpiGatherResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("KPI_ID", id);
        sqlQuery.setParameterList("YEAR_IDS", StringUtils.split(year,","));
        
        List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            result = new KpiGatherResult();
            dictEntry = new DictEntry();
            kpi = new Kpi();
            timePeriod = new TimePeriod();
            latestTimePeriodId = new TimePeriod();

            String kpiId = "";
            String frequence = "";
            String timePeriodId = "";
            Double targetValue = null;
            Double finishValue = null;
            String latestTime = "";

            if (objects[0] != null) {
                kpiId = objects[0].toString();
            }
            if (objects[1] != null) {
                frequence = objects[1].toString();
            }
            if (objects[2] != null) {
                timePeriodId = objects[2].toString();
            }
            if (objects[3] != null) {
                targetValue = Double.valueOf(objects[3].toString());
            }
            if (objects[4] != null) {
                finishValue = Double.valueOf(objects[4].toString());
            }
            if (objects[5] != null) {
                latestTime = objects[5].toString();
            }

            latestTimePeriodId.setId(latestTime);
            dictEntry.setId(frequence);
            kpi.setId(kpiId);
            kpi.setGatherFrequence(dictEntry);
            kpi.setLastTimePeriod(latestTimePeriodId);
            result.setKpi(kpi);
            timePeriod.setId(timePeriodId);
            result.setTimePeriod(timePeriod);
            result.setTargetValue(targetValue);
            result.setFinishValue(finishValue);
            kpiList.add(result);
        }

        return kpiList;
    }
    
    /**查询关注的指标
     * @param query 查询条件 
     * @return
     */
    public List<Kpi> findFocusKpi(String query,String isFocus){
    	Criteria criteria = o_kpiDAO.createCriteria();
        criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
        if(StringUtils.isNotBlank(query)){
        	criteria.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("isFocus", isFocus));
    	return criteria.list();
    }
    /**根据指标id查询最后更新的采集结果
     * @param id 指标id
     * @return
     */
    public Map<String, Object > findLastestGatherReusltById(String id) {
    	Map<String, Object> map = new HashMap<String,Object>();
    	Kpi kpi = this.findKpiById(id);
    	TimePeriod tp;
    	if(kpi.getLastTimePeriod() != null) {
    		tp = kpi.getLastTimePeriod();
    		
    	} else {
    	    tp = o_timePeriodBO.findTimePeriodBySome(kpi.getGatherFrequence().getId());
    	}
    	map.put("dataRange", tp.getTimePeriodFullName());
    	KpiGatherResult kgr = o_kpiGatherResultBO.findKpiGatherResultByIdAndTimeperiod(id,tp);
        map.put("kgrid", kgr.getId());
        String memoTile = "注释——" + kpi.getName() + "(" + map.get("dataRange") + ")";
        map.put("memoTitle",memoTile);
        map.put("kpiname", kpi.getName());
    	return map;
    }

    /**
     * 得到一个指标维护最新值
     * 
     * @author 金鹏祥
     * @param kpiId
     * @return String
     */
    public String findKpiNewValue(String kpiId) {
        String timeperiod = null;
        Date currentDate = new Date();
        Criteria criteria = o_kpiGatherResultDAO.createCriteria();
        criteria.createAlias("kpi", "kpi", CriteriaSpecification.LEFT_JOIN);
        criteria.setFetchMode("kpi.createBy", FetchMode.SELECT);
        criteria.setFetchMode("kpi.lastModifyBy", FetchMode.SELECT);
        criteria.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
        criteria.add(Restrictions.eqProperty("kpi.gatherFrequence.id", "timePeriod.type"));
        criteria.add(Restrictions.eq("kpi.id", kpiId));
        criteria.add(Restrictions.ge("timePeriod.endTime", currentDate));
        criteria.add(Restrictions.le("timePeriod.startTime", currentDate));
        List<KpiGatherResult> list = criteria.list();
        if (null != list && list.size() > 0) {
            timeperiod = list.get(0).getTimePeriod().getId();
        }
        return timeperiod;
    }
    
    /**
     * 指标录入保存,得到一个指标维护最新值
     * 
     * @author 金鹏祥
     * @param kpiId
     * @return String
     */
    public String findKpiNewValueNes(String params) {
        String newValue = null;
        JSONArray jsonarr = JSONArray.fromObject(params);
        if (jsonarr.size() == 1) {
            //单一保存
            for (Object object : jsonarr) {
                JSONObject jsobj = (JSONObject) object;
                String str[] = jsobj.toString().split(":");
                newValue = str[0].replace("{\"", "").replace("\"", "");
            }
        }
        else {
            //全部保存
            if (params.indexOf('Q') != -1) {
                if (params.indexOf("mm") != -1) {
                    //月
                    List<String> monthList = new ArrayList<String>();
                    for (Object object : jsonarr) {
                        JSONObject jsobj = (JSONObject) object;
                        String str[] = jsobj.toString().split(":");
                        String key = str[0].replace("{\"", "").replace("\"", "");
                        String value = str[1].split(",")[1];

                        if (key.indexOf("mm") != -1) {
                            if (StringUtils.isNotBlank(value)) {
                                monthList.add(key);
                            }
                        }
                    }

                    if (monthList.size() != 0) {
                        newValue = monthList.get(monthList.size() - 1);
                    }
                }
                else {
                    //季
                    List<String> quarterList = new ArrayList<String>();
                    for (Object object : jsonarr) {
                        JSONObject jsobj = (JSONObject) object;
                        String str[] = jsobj.toString().split(":");
                        String key = str[0].replace("{\"", "").replace("\"", "");
                        String value = str[1].split(",")[1];

                        if (key.indexOf('Q') != -1) {
                            if (StringUtils.isNotBlank(value)) {
                                quarterList.add(key);
                            }
                        }
                    }

                    if (quarterList.size() != 0) {
                        newValue = quarterList.get(quarterList.size() - 1);
                    }
                }
            }
        }

        return newValue;
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
    public Date findCalculateNextExecuteTime(Kpi kpi, String type) throws ParseException {
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

    /**
     * <pre>
     * 根据指标名称查询指标信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            指标名称
     * @return 指标对象
     * @since fhd　Ver 1.1
     */
    public Kpi findkpiTypeByName(String name) {
        String companyId = "";
        OperatorDetails userDetails = UserContext.getUser();// 所在公司id
        if(null!=userDetails){
            companyId = userDetails.getCompanyid();
        }
        Kpi kpi = null;
        if (StringUtils.isNotBlank(name)) {
            Criteria criteria = this.o_kpiDAO.createCriteria();
            criteria.add(Property.forName("name").eq(name));
            criteria.add(Restrictions.eq("deleteStatus", true));
            if(StringUtils.isNotBlank(companyId)){
                criteria.add(Restrictions.eq("company.id", companyId));
            }
            criteria.add(Restrictions.eq("isKpiCategory", "KC"));
            removeKpiRelaObject(criteria);
            List<Kpi> listKpi = criteria.list();
            if (listKpi.size() > 0) {
                return listKpi.get(0);
            }
        }
        return kpi;
    }
	/**
	 * 查询指标类型或指标编号集合map格式 key:code value:指标名称
	 * @param type KC,KPI
	 * @return  当前指标的编号集合
	 */
	public Map<String, String> findAllKpiCodeList(String type){
		Map<String, String> map = new HashMap<String, String>();
		Criteria criteria = o_kpiDAO.createCriteria();		
		//criteria.setCacheable(true);
        String companyId = "";
        OperatorDetails userDetails = UserContext.getUser();// 所在公司id
        if(null!=userDetails){
            companyId = userDetails.getCompanyid();
        }
        criteria.add(Restrictions.eq("deleteStatus", true));
        if(StringUtils.isNotBlank(companyId)){
        	
            criteria.add(Restrictions.eq("company.id", companyId));
        }
        if(StringUtils.isNotBlank(type)) {
            criteria.add(Restrictions.eq("isKpiCategory", type));
        }
        List<Kpi> kpiList = criteria.list();
        for(Kpi kpi:kpiList) {
        	if(StringUtils.isNotBlank(kpi.getCode()))
        	{
        		map.put(kpi.getCode(), kpi.getName());	
        	}
        	
        }
		return map;
	}
	/**
	 * 查询指标类型或指标编码所对应的指标idmap key:指标编码 value:指标id
	 * @param type KC,KPI
	 * @return  当前指标的编号集合
	 */
	public Map<String, String> findAllKpiTypeIdMap(String type){
		Map<String, String> map = new HashMap<String, String>();
		Criteria criteria = o_kpiDAO.createCriteria();		
		//criteria.setCacheable(true);
        String companyId = "";
        OperatorDetails userDetails = UserContext.getUser();// 所在公司id
        if(null!=userDetails){
            companyId = userDetails.getCompanyid();
        }
        criteria.add(Restrictions.eq("deleteStatus", true));
        if(StringUtils.isNotBlank(companyId)){
        	
            criteria.add(Restrictions.eq("company.id", companyId));
        }
        if(StringUtils.isNotBlank(type)) {
            criteria.add(Restrictions.eq("isKpiCategory", type));
        }
        List<Kpi> kpiList = criteria.list();
        for(Kpi kpi:kpiList) {
        	if(StringUtils.isNotBlank(kpi.getCode()))
        	{ 
        		map.put(kpi.getCode(), kpi.getId());	
        	}        	
        }
		return map;
	}
	/**
	 * 
	 * @param type KC,KPI
	 * @return  当前指标的报表位数集合
	 */
	public Map<String, Object> findAllKpiReportScaleMap(String type){
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = o_kpiDAO.createCriteria();		
		//criteria.setCacheable(true);
        String companyId = "";
        OperatorDetails userDetails = UserContext.getUser();// 所在公司id
        if(null!=userDetails){
            companyId = userDetails.getCompanyid();
        }
        criteria.add(Restrictions.eq("deleteStatus", true));
        if(StringUtils.isNotBlank(companyId)){
        	
            criteria.add(Restrictions.eq("company.id", companyId));
        }
        if(StringUtils.isNotBlank(type)) {
            criteria.add(Restrictions.eq("isKpiCategory", type));
        }
        List<Kpi> kpiList = criteria.list();
        for(Kpi kpi:kpiList) {       	
          map.put(kpi.getName(), kpi.getScale());	       	
        }
		return map;
	}
	
	 /**
     * <pre>
     * 查找指标标关联的部门和人员信息
     * </pre>
     * 
     * @author 王鑫
     * @param kpi
     *            指标对象
     * @return
     * @since fhd　Ver 1.1
     */
    public Map<String, Object> findKpiRelaOrgEmpByKpi(Kpi kpi) {
    	Map<String, Object> map = new HashMap<String,Object>();
    	List<String> ownDept = new ArrayList<String>();
    	List<String> reportDept = new ArrayList<String>();
    	List<String> viewDept = new ArrayList<String>();
    	List<String> gatherDept = new ArrayList<String>();
    	List<String> targetDept = new ArrayList<String>();
        if (null != kpi) {
        	 String type = "";
             SysEmployee sysEmp = null;
             SysOrganization sysOrg = null;
             Set<KpiRelaOrgEmp> kpiRelaOrgEmpSet = kpi.getKpiRelaOrgEmps();
             for (KpiRelaOrgEmp kpiRelaOrgEmp : kpiRelaOrgEmpSet) {
                 type = kpiRelaOrgEmp.getType();
                 sysOrg = kpiRelaOrgEmp.getOrg();
                 sysEmp = kpiRelaOrgEmp.getEmp();
                 StringBuffer sb = new StringBuffer();
                 try {
                     if (null != sysOrg) {
                         sb.append(sysOrg.getOrgname());
                     }
                 }
                 catch (Exception e) {
                	 logger.error("获得机构名称异常:信息["+e.toString()+"]");
                 }
                 try {
                     if (null != sysEmp.getId()) {
                         sb.append("：（").append(sysEmp.getEmpname()).append("）");
                     }
                 }
                 catch (Exception e) {
                	 logger.error("获得emp名称异常:信息["+e.toString()+"]");
                 }
                 if (Contents.BELONGDEPARTMENT.equals(type)) {
                	 ownDept.add(sb.toString());
                 }
                 if (Contents.REPORTDEPARTMENT.equals(type)) {
                	 reportDept.add(sb.toString());
                 }
                 if (Contents.VIEWDEPARTMENT.equals(type)) {
                	 viewDept.add(sb.toString());
                 }
                 if (Contents.GATHERDEPARTMENT.equals(type)) {
                	 gatherDept.add(sb.toString());
                 }
                 if (Contents.TARGETDEPARTMENT.equals(type)) {
                	 targetDept.add(sb.toString());
                 }
             }
        }
    	map.put("ownDept", ownDept);
    	map.put("reportDept", reportDept);
    	map.put("viewDept", viewDept);
    	map.put("gatherDept", gatherDept);
    	map.put("targetDept", targetDept);
    	return map;
    }
    /**
     * <pre>
     * 查询所有可用(未删除状态)的指标.
     * </pre>
     * 
     * @param page
     *            分页对象
     * @param query
     *            查询条件
     * @param sort
     *            排序字段
     * @param dir
     *            排序顺序
     * @return
     */
    public Page<Kpi> findKpiByDeptId(Page<Kpi> page, String query, String sort, String dir,String gType,String deptId) {
        String sortstr = "id";
        DetachedCriteria dc = DetachedCriteria.forClass(Kpi.class);
        dc.createAlias("kpiRelaOrgEmps", "kpirelaorg");
        dc.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
        dc.add(Restrictions.eq("deleteStatus", true));
        dc.add(Restrictions.eq("status.id", Contents.DICT_Y));
        if (StringUtils.isNotBlank(query)) {
            dc.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(deptId)) {
            dc.add(Restrictions.eq("kpirelaorg.org.id", deptId));
            dc.add(Restrictions.eq("kpirelaorg.type", Contents.BELONGDEPARTMENT));
        }
        if (StringUtils.equals("name", sort)) {
            sortstr = "name";
        }
        if(StringUtils.isNotBlank(gType)) {
       	 dc.add(Restrictions.eq("gatherFrequence.id", gType));
       }
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }
        return o_kpiDAO.findPage(dc, page, false);

    }
    
    /**
     * <pre>
     * 更新指标类型采集计算报告
     * </pre>
     * 
     * @author 陈晓哲
     * @param form
     *            指标Form
     * @return
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @Transactional
    @RecordLog(value="保存指标采集计算报告信息")
    public void mergeKpiTypeCalculate(KpiForm form) {
        Kpi kpi = null;
        String currentId = StringUtils.defaultIfEmpty(form.getId(), "");
        String relativeToStr = form.getRelativeToStr();// 趋势相对于
        String gatherfrequenceDict = form.getGatherFrequenceDict();// 结果收集频率
        String targetfrequenceDict = form.getTargetSetFrequenceDict();// 目标设置频率
        String reportfrequenceDict = form.getReportFrequenceDict();// 结果收集报告频率
        String targetSetReportFrequenceDict = form.getTargetSetReportFrequenceDict();// 目标收集报告频率
        String resultSumMeasureStr = form.getResultSumMeasureStr();// 结果值累计计算
        String targetSumMeasureStr = form.getTargetSumMeasureStr();// 目标值累计计算
        String assessmentSumMeasureStr = form.getAssessmentSumMeasureStr();// 评估值累计计算
        String resultgatherfrequence = form.getResultgatherfrequence();
        String gatherfrequenceRule = form.getGatherfrequenceRule();
        String reportFrequenceStr = form.getReportFrequenceStr();
        String reportFrequenceRule = form.getReportFrequenceRule();
        String targetSetFrequencestr = form.getTargetSetFrequenceStr();
        String targetSetFrequenceRule = form.getTargetSetFrequenceRule();
        String targetSetReportFrequenceStr = form.getTargetSetReportFrequenceStr();
        String calcValue = form.getCalcStr();
        String gatherDesc = form.getGatherDesc();
        

        if (!"".equals(currentId) && !Contents.ID_UNDEFINED.equals(currentId)) {

            kpi = o_kpiDAO.get(currentId);
            /* 保存公式关联关系 */
            String isResultFormula = form.getIsResultFormula();
            String resultformula = form.getResultFormula();
            String resultDsName = form.getResultDsStr();
            
            //mainSoft接口
            if(StringUtils.isNotBlank(resultDsName)) {
            	kpi.setResultDs(o_kpiDataSourceBO.findDataSourcebyName(resultDsName));	
            } else {
            	kpi.setResultDs(null);
            }
            kpi.setIsResultDsType(form.getIsResultDsType());
            kpi.setIsResultFormula(isResultFormula);
            if (!Contents.ID_UNDEFINED.equals(resultformula)) {
                kpi.setResultFormula(resultformula);
            }
            if (Contents.FORMULAR_FORMULA.equals(isResultFormula)) {
                if (StringUtils.isNotBlank(resultformula)) {
                   // o_formulaObjectRelationBO.saveFormulaObjectRelation(kpi.getId(), Contents.OBJECT_KPI, "resultValueFormula", resultformula);//jrj注释
                }
            }
            String isTargetFormula = form.getIsTargetFormula();
            String targetformula = form.getTargetFormula();
            String targetDsName = form.getTargetDsStr();
            // mainSoft接口
            if(StringUtils.isNotBlank(targetDsName)) {
            	kpi.setTargetDs(o_kpiDataSourceBO.findDataSourcebyName(targetDsName));           	
            } else {
            	kpi.setTargetDs(null);
            }
            kpi.setIsTargetDsType(form.getIsTargetDsType());
            
            kpi.setIsTargetFormula(isTargetFormula);
            if (!Contents.ID_UNDEFINED.equals(targetformula)) {
                kpi.setTargetFormula(targetformula);
            }
            if (Contents.FORMULAR_FORMULA.equals(isTargetFormula)) {
                if (StringUtils.isNotBlank(targetformula)) {
                  //  o_formulaObjectRelationBO.saveFormulaObjectRelation(kpi.getId(), Contents.OBJECT_KPI, "targetValueFormula", targetformula);//jrj注释
                }
            }
            String isAssessmentFormula = form.getIsAssessmentFormula();
            String assessmentformula = form.getAssessmentFormula();
            kpi.setIsAssessmentFormula(isAssessmentFormula);
            // mainSoft接口
            String assessmentDsName = form.getAsseessmentDsStr();
            if(StringUtils.isNotBlank(assessmentDsName)) {
            	kpi.setAsseessmentDs(o_kpiDataSourceBO.findDataSourcebyName(assessmentDsName));          	
            } else {
            	kpi.setAsseessmentDs(null);
            }
            kpi.setIsAssessmentDsType(form.getIsAssessmentDsType());
            if (!Contents.ID_UNDEFINED.equals(assessmentformula)) {
                kpi.setAssessmentFormula(assessmentformula);
            }
            if (Contents.FORMULAR_FORMULA.equals(isAssessmentFormula)) {
                if (StringUtils.isNotBlank(assessmentformula)) {
                   // o_formulaObjectRelationBO
                   //         .saveFormulaObjectRelation(kpi.getId(), Contents.OBJECT_KPI, "assessmentValueFormula", assessmentformula);//jrj注释
                }
            }
            /*String alarmformula = form.getAlarmFormula();
            if (!Contents.ID_UNDEFINED.equals(alarmformula)) {
                kpi.setForecastFormula(alarmformula);
            }*/
            if (null != form.getTargetSetIntervalStr()) {
                kpi.setTargetSetInterval(Integer.valueOf(form.getTargetSetIntervalStr()));
            }
            if (null != form.getResultCollectIntervalStr()) {
                kpi.setResultCollectInterval(Integer.valueOf(form.getResultCollectIntervalStr()));
            }
            if (StringUtils.isNotBlank(resultgatherfrequence)) {
                if (StringUtils.isNotBlank(form.getGatherValueCron())) {
                    kpi.setGatherDayFormulr(form.getGatherValueCron());
                }
                kpi.setGatherDayFormulrShow(resultgatherfrequence + "@" + gatherfrequenceRule);
            }
            if (StringUtils.isNotBlank(targetSetFrequencestr)) {
                if (StringUtils.isNotBlank(form.getTargetValueCron())) {
                    kpi.setTargetSetDayFormular(form.getTargetValueCron());
                }
                kpi.setTargetSetDayFormularShow(targetSetFrequencestr + "@" + targetSetFrequenceRule);
            }
            if (StringUtils.isNotBlank(reportFrequenceStr)) {
                if (StringUtils.isNotBlank(form.getGatherReportValueCron())) {
                    kpi.setGatherReportDayFormulr(form.getGatherReportValueCron());
                }
                kpi.setGatherReportDayFormulrShow(reportFrequenceStr + "@" + reportFrequenceRule);
            }
            if (StringUtils.isNotBlank(targetSetReportFrequenceStr)) {
                if (StringUtils.isNotBlank(form.getTargetReportCron())) {
                    kpi.setTargetSetReportDayFormulr(form.getTargetReportCron());
                }
                kpi.setTargetReportDayFormulrShow(targetSetReportFrequenceStr + "@" + form.getTargetSetReportFrequenceRule());
            }

            if (StringUtils.isNotBlank(form.getTargetValueCron())) {
                kpi.setTargetCalculatetime(DateUtils.stringToDateToDay(findKpiFrequenceByFormulr(form.getTargetValueCron())));
            }
            if (StringUtils.isNotBlank(form.getGatherValueCron())) {
                kpi.setCalculatetime(DateUtils.stringToDateToDay(findKpiFrequenceByFormulr(form.getGatherValueCron())));
            }
            // 保存标杆值
            kpi.setModelValue(form.getModelValue());
            // 最大值
            kpi.setMaxValue(form.getMaxValue());
            // 最小值
            kpi.setMinValue(form.getMinValue());
            if(StringUtils.isNotBlank(calcValue)){
              //是否计算
                kpi.setCalc(o_dictEntryBO.findDictEntryById(calcValue));
            }
            if (StringUtils.isNotBlank(relativeToStr)) {
                kpi.setRelativeTo(o_dictEntryBO.findDictEntryById(relativeToStr));
            }
            if (StringUtils.isNotBlank(gatherfrequenceDict)) {
                kpi.setGatherFrequence(o_dictEntryBO.findDictEntryById(gatherfrequenceDict));
            }
            if (StringUtils.isNotBlank(targetfrequenceDict)) {
                kpi.setTargetSetFrequence(o_dictEntryBO.findDictEntryById(targetfrequenceDict));
            }
            if (StringUtils.isNotBlank(reportfrequenceDict)) {
                kpi.setReportFrequence(o_dictEntryBO.findDictEntryById(reportfrequenceDict));
            }
            if (StringUtils.isNotBlank(targetSetReportFrequenceDict)) {
                kpi.setTargetSetReportFrequence(o_dictEntryBO.findDictEntryById(targetSetReportFrequenceDict));
            }

            if (StringUtils.isNotBlank(resultSumMeasureStr)) {
                kpi.setResultSumMeasure(o_dictEntryBO.findDictEntryById(resultSumMeasureStr));
            }
            if (StringUtils.isNotBlank(targetSumMeasureStr)) {
                kpi.setTargetSumMeasure(o_dictEntryBO.findDictEntryById(targetSumMeasureStr));
            }
            if (StringUtils.isNotBlank(assessmentSumMeasureStr)) {
                kpi.setAssessmentSumMeasure(o_dictEntryBO.findDictEntryById(assessmentSumMeasureStr));
            }

            kpi.setScale(form.getScale());
            kpi.setGatherDesc(gatherDesc);
            o_kpiDAO.merge(kpi);
            // 根据指标类型,向结果采集表中插入采集结果数据
            if (null != kpi.getGatherFrequence() && StringUtils.isNotBlank(kpi.getGatherFrequence().getId())
                    &&!Contents.FREQUECY_RELATIME.equals(kpi.getGatherFrequence().getId())
                    && Contents.KPI_TYPE.equals(kpi.getIsKpiCategory())) {
            	//生成当年采集数据
            	Date current = new Date();
            	String currentYear = DateUtils.getYear(current);
                
                List<KpiGatherResult> kpiGatherResultList = o_kpiGatherResultBO.findCurrentYearKpiGatherResultByKpiId(kpi.getId(),
                        String.valueOf(currentYear));
                
                if (null != kpiGatherResultList && kpiGatherResultList.size() == 0) {
                    this.o_kpiGatherResultBO.saveBatchKpiGatherResultByFrequenceType(kpi.getId(), kpi.getGatherFrequence().getId(), Integer.parseInt(currentYear));
                }
            }
        }
    }

    

    /**
     * <pre>
     * 保存指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param form
     *            指标form
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @Transactional
    public String mergeKpiType(KpiForm form) throws ParseException {
        Kpi kpi = null;
        // 获得机构对象
        boolean addflag = false;
        String kpiId = Identities.uuid();
        String ynid = form.getStatusStr();// 状态
        String unitsStr = form.getUnitsStr();// 单位
        String startDateStr = form.getStartDateStr();
        boolean isMonitor = false;
        if (Contents.DICT_Y.equals(form.getMonitorStr())) {
            isMonitor = true;
        }
        String typeStr = form.getTypeStr();// 指标类型
        String dataTypeStr = form.getDataTypeStr();// 数据类型
        String kpiTypeStr = form.getKpiTypeStr();// 指标性质
        String alarmMeasureStr = form.getAlarmMeasureStr();// 亮灯依据
        String relativeToStr = form.getRelativeToStr();// 趋势相对于
        String alarmBasisStr = form.getAlarmBasisStr();// 预警依据
        String otherDim = form.getOtherDim();// 辅助维度
        String mainDim = form.getMainDim();// 主维度
        String ownDept = form.getOwnDept();// 所属部门
        String viewDept = form.getViewDept();// 查看部门
        String reportDept = form.getReportDept();// 报告部门
        String gatherDept = form.getGatherDept();// 采集部门
        String targetDept = form.getTargetDept();// 采集部门
        String gatherFrequenceDict = form.getGatherFrequenceDict();// 采集频率
        SysOrganization company = o_organizationBO.get(UserContext.getUser().getCompanyid());
        String currentId = StringUtils.defaultIfEmpty(form.getId(), "");

        if (!"".equals(currentId) && !Contents.ID_UNDEFINED.equals(currentId)) {
            kpi = o_kpiDAO.get(currentId);
            kpiId = kpi.getId();
            // 删除部门和人员信息
            this.removeKpiRelaOrgEmp(kpi, Contents.ORG_ALL);
            // 删除主,辅助维度信息
            this.removeKpiRelaDim(kpi);
            String gatherDayFormulr = kpi.getGatherDayFormulr();
            String targetSetDayFormular = kpi.getTargetSetDayFormular();
            String gatherReportDayFormulr = kpi.getGatherReportDayFormulr();
            String targetSetReportDayFormulr = kpi.getTargetSetReportDayFormulr();
            DictEntry gatherDict = kpi.getGatherFrequence();
            DictEntry targetSetDict = kpi.getTargetSetFrequence();
            DictEntry targetSetReportDict = kpi.getTargetSetReportFrequence();
            DictEntry gatherSetReportDict = kpi.getReportFrequence();
            Integer resultCollectInterval = kpi.getResultCollectInterval();
            Integer targetSetInterval = kpi.getTargetSetInterval();
            Date calculatetime = kpi.getCalculatetime();
            Date targetCalculatetime = kpi.getTargetCalculatetime();
            Integer scale = kpi.getScale();
            DictEntry relativeToDict = kpi.getRelativeTo();
            String gatherDayFormulrShow = kpi.getGatherDayFormulrShow();
            String gatherReportDayFormulrShow = kpi.getGatherReportDayFormulrShow();
            String targetSetDayFormularShow = kpi.getTargetSetDayFormularShow();
            String targetReportDayFormulrShow = kpi.getTargetReportDayFormulrShow();
            String gatherCron = kpi.getGatherDayFormulr();
            String isResultFormula = kpi.getIsResultFormula();
            String resultFormula = kpi.getResultFormula();
            String isTargetFormula = kpi.getIsTargetFormula();
            String targetFormula = kpi.getTargetFormula();
            String isAssessmentFormula = kpi.getIsAssessmentFormula();
            String assessmentFormula = kpi.getAssessmentFormula();
            DictEntry resultSum = kpi.getResultSumMeasure();
            DictEntry targetSum = kpi.getTargetSumMeasure();
            DictEntry assessmentSum = kpi.getAssessmentSumMeasure();
            Double modelValue = kpi.getModelValue();
            Double maxValue = kpi.getMaxValue();
            Double minValue = kpi.getMinValue();
            DictEntry calcDict =kpi.getCalc();
            // String alarmFormula = kpi.getForecastFormula();
            BeanUtils.copyProperties(form, kpi);
            kpi.setTargetReportDayFormulrShow(targetReportDayFormulrShow);
            kpi.setGatherDayFormulr(gatherDayFormulr);
            kpi.setTargetSetDayFormular(targetSetDayFormular);
            kpi.setTargetSetReportDayFormulr(targetSetReportDayFormulr);
            kpi.setGatherReportDayFormulr(gatherReportDayFormulr);
            kpi.setGatherFrequence(gatherDict);
            kpi.setTargetSetFrequence(targetSetDict);
            kpi.setTargetSetReportFrequence(targetSetReportDict);
            kpi.setReportFrequence(gatherSetReportDict);
            kpi.setResultCollectInterval(resultCollectInterval);
            kpi.setTargetSetInterval(targetSetInterval);
            kpi.setScale(scale);
            kpi.setRelativeTo(relativeToDict);
            kpi.setCalculatetime(calculatetime);
            kpi.setTargetCalculatetime(targetCalculatetime);
            kpi.setGatherDayFormulrShow(gatherDayFormulrShow);
            kpi.setGatherReportDayFormulrShow(gatherReportDayFormulrShow);
            kpi.setTargetSetDayFormularShow(targetSetDayFormularShow);
            kpi.setGatherDayFormulr(gatherCron);
            kpi.setIsResultFormula(isResultFormula);
            kpi.setResultFormula(resultFormula);
            kpi.setIsTargetFormula(isTargetFormula);
            kpi.setTargetFormula(targetFormula);
            kpi.setIsAssessmentFormula(isAssessmentFormula);
            kpi.setAssessmentFormula(assessmentFormula);
            kpi.setResultSumMeasure(resultSum);
            kpi.setTargetSumMeasure(targetSum);
            kpi.setAssessmentSumMeasure(assessmentSum);
            kpi.setModelValue(modelValue);
            kpi.setMaxValue(maxValue);
            kpi.setMinValue(minValue);
            kpi.setCalc(calcDict);
            /* //告警公式
               kpi.setForecastFormula(alarmFormula);
            */
        }
        else {
            addflag = true;
            kpi = new Kpi();
            BeanUtils.copyProperties(form, kpi);
            kpi.setId(kpiId);
            saveKpiDefaultValue(kpi);
        }
        kpi.setIsKpiCategory(Contents.KC_TYPE);
        kpi.setCompany(company);
        kpi.setDeleteStatus(true);
        if (StringUtils.isNotBlank(startDateStr)) {
            Date startDate = org.apache.commons.lang3.time.DateUtils.parseDate(form.getStartDateStr(), "yyyy-MM-dd");
            kpi.setStartDate(startDate);
        }
        if (StringUtils.isNotBlank(unitsStr)) {
            kpi.setUnits(o_dictEntryBO.findDictEntryById(unitsStr));
        }
        if (StringUtils.isNotBlank(ynid)) {
            kpi.setStatus(o_dictEntryBO.findDictEntryById(ynid));
        }
        if (StringUtils.isNotBlank(typeStr)) {
            kpi.setType(o_dictEntryBO.findDictEntryById(typeStr));
        }
        if (StringUtils.isNotBlank(dataTypeStr)) {
            kpi.setDataType(o_dictEntryBO.findDictEntryById(dataTypeStr));
        }
        if (StringUtils.isNotBlank(kpiTypeStr)) {
            kpi.setKpiType(o_dictEntryBO.findDictEntryById(kpiTypeStr));
        }
        if (StringUtils.isNotBlank(alarmMeasureStr)) {
            kpi.setAlarmMeasure(o_dictEntryBO.findDictEntryById(alarmMeasureStr));
        }
        if (StringUtils.isNotBlank(alarmBasisStr)) {
            kpi.setAlarmBasis(o_dictEntryBO.findDictEntryById(alarmBasisStr));
        }
        if (StringUtils.isNotBlank(relativeToStr)) {
            kpi.setRelativeTo(o_dictEntryBO.findDictEntryById(relativeToStr));
        }
        if (StringUtils.isNotBlank(gatherFrequenceDict)) {
            kpi.setGatherFrequence(o_dictEntryBO.findDictEntryById(gatherFrequenceDict));
        }
        kpi.setIsMonitor(isMonitor);

        // 保存指标对象
        o_kpiDAO.merge(kpi);
        // 保存所属部门信息
        this.saveKpiRelaOrgEmp(kpi, ownDept, Contents.BELONGDEPARTMENT);
        // 保存采集部门信息
        this.saveKpiRelaOrgEmp(kpi, gatherDept, Contents.GATHERDEPARTMENT);
        // 保存目标部门信息
        this.saveKpiRelaOrgEmp(kpi, targetDept, Contents.TARGETDEPARTMENT);
        // 保存查看部门信息
        this.saveKpiRelaOrgEmp(kpi, viewDept, Contents.VIEWDEPARTMENT);
        // 保存报告部门信息
        this.saveKpiRelaOrgEmp(kpi, reportDept, Contents.REPORTDEPARTMENT);
        // 保存主维度信息
        this.saveKpiRelaDim(kpi, mainDim, Contents.MAIN);
        // 保存辅助维度信息
        if (StringUtils.isNotBlank(otherDim)) {
            String[] otherDims = otherDim.split(",");
            for (String tmpstr : otherDims) {
                this.saveKpiRelaDim(kpi, tmpstr, Contents.ASSISTANT);
            }
        }
        if (addflag) {
            //告警或预警默认值保存
            AlarmPlan forecast = o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast");
            AlarmPlan alarm = o_alarmPlanBO.findAlarmPlanByNameType("常用预警方案", "0alarm_type_kpi_alarm");
            if (null != forecast || null != alarm) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                KpiRelaAlarm kpiRelaAlarm = new KpiRelaAlarm();
                kpiRelaAlarm.setId(Identities.uuid());
                kpiRelaAlarm.setKpi(kpi);
                kpiRelaAlarm.setrAlarmPlan(forecast);
                kpiRelaAlarm.setFcAlarmPlan(alarm);
                kpiRelaAlarm.setStartDate(sdf.parse(DateUtils.formatDate(new Date(), "yyyy-MM-dd")));
                o_kpiRelaAlarmDAO.merge(kpiRelaAlarm);
            }
        }
        return kpiId;
    }

    /**
     * <pre>
     * 保存指标对象
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpi
     *            指标对象
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void mergeKpi(Kpi kpi) {
        this.o_kpiDAO.merge(kpi);
    }

    /**
     * <pre>
     * 更新指标和预警关联信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param jsonString
     * @param id
     *            指标ID
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @Transactional
    @RecordLog(value="保存指标关联告警和预警信息")
    public void mergeKpiRelaAlarm(String jsonString, String id) throws ParseException {
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(jsonString)) {
            JSONArray jsonArray = JSONArray.fromObject(jsonString);
            AlarmPlan alarmPlan = null;
            AlarmPlan warningPlan = null;
            KpiRelaAlarm kpiRelaAlarm = null;
            Kpi kpi = this.findKpiById(id);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            /*
             * 删除指标关联的预警告警信息
             */
            Set<KpiRelaAlarm> smRelaAlarms = kpi.getKpiRelaAlarms();
            for (KpiRelaAlarm kpiRelaAlarmItem : smRelaAlarms) {
                this.o_kpiRelaAlarmDAO.delete(kpiRelaAlarmItem);
            }
            List<String> alarmIdList = new ArrayList<String>();
            List<String> warningIdList = new ArrayList<String>();
            for(int i=0;i<jsonArray.size();i++){
            	JSONObject jsonObject = jsonArray.getJSONObject(i);
                String alarmId = jsonObject.getString("alarm");
                String warningId = jsonObject.getString("warning");
                if (StringUtils.isNotBlank(alarmId)) {
                	alarmIdList.add(alarmId);
                }
                if (StringUtils.isNotBlank(warningId)) {
                	warningIdList.add(warningId);
                }
            }
            Map<String,AlarmPlan> alarmMap = o_alarmPlanBO.findAlarmPlanMapByPlanIdList(alarmIdList);
            
            Map<String,AlarmPlan> warningMap = o_alarmPlanBO.findAlarmPlanMapByPlanIdList(warningIdList);
            
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String alarmId = jsonObject.getString("alarm");
                String warningId = jsonObject.getString("warning");
                String date = jsonObject.getString("date");
                /*
                 * 保存告警,预警信息
                 */
                if (StringUtils.isNotBlank(alarmId)) {
                    alarmPlan = alarmMap.get(alarmId);
                }
                if (StringUtils.isNotBlank(warningId)) {
                    warningPlan = warningMap.get(warningId);
                }
                kpiRelaAlarm = new KpiRelaAlarm();
                kpiRelaAlarm.setId(Identities.uuid());
                kpiRelaAlarm.setKpi(kpi);
                kpiRelaAlarm.setrAlarmPlan(alarmPlan);
                kpiRelaAlarm.setFcAlarmPlan(warningPlan);
                kpiRelaAlarm.setStartDate(sdf.parse(date));
                o_kpiRelaAlarmDAO.merge(kpiRelaAlarm);
            }
        }
    }

    
    /**指标对象赋值默认值
     * @param kpi指标对象
     * @throws ParseException
     */
    private void saveKpiDefaultValue(Kpi kpi) throws ParseException {
        //实际值采集频率
        DictEntry monthFrequence = o_dictEntryBO.findDictEntryById("0frequecy_month");
        DictEntry relativeTo = o_dictEntryBO.findDictEntryById("kpi_relative_to_previs");
        DictEntry assessmentSumMeasure = o_dictEntryBO.findDictEntryById("kpi_sum_measure_avg");
        DictEntry resultSumMeasure = o_dictEntryBO.findDictEntryById("kpi_sum_measure_sum");
        DictEntry targetSumMeasure = o_dictEntryBO.findDictEntryById("kpi_sum_measure_sum");
        kpi.setGatherFrequence(monthFrequence);
        kpi.setGatherDayFormulr("0 0 0 L * ?");
        kpi.setGatherDayFormulrShow("每月,期间末日@2,");
        kpi.setCalculatetime(DateUtils.stringToDateToDay(findKpiFrequenceByFormulr("0 0 0 L * ?")));

        //目标收集频率
        kpi.setTargetSetFrequence(monthFrequence);
        kpi.setTargetSetDayFormular("0 0 0 1 * ?");
        kpi.setTargetSetDayFormularShow("每月,期间首日@1,");
        kpi.setTargetCalculatetime(DateUtils.stringToDateToDay(findKpiFrequenceByFormulr("0 0 0 1 * ?")));
        //评估值是否使用公式
        kpi.setIsAssessmentFormula("0sys_use_formular_formula");
        //评估值公式
        kpi.setAssessmentFormula("IF(#[myself~实际值]>=#[myself~目标值],100,#[myself~实际值]/#[myself~目标值]*100)");
        //实际值是否使用公式
        kpi.setIsResultFormula("0sys_use_formular_manual");
        //目标值是否使用公式
        kpi.setIsTargetFormula("0sys_use_formular_manual");
        //实际值采集延期天数
        kpi.setResultCollectInterval(3);
        //目标收集延期天数
        kpi.setTargetSetInterval(3);
        //报表小数点位置
        kpi.setScale(2);
        //趋势相对于
        kpi.setRelativeTo(relativeTo);
        //实际值汇总
        kpi.setResultSumMeasure(resultSumMeasure);
        //目标值汇总
        kpi.setTargetSumMeasure(targetSumMeasure);
        //评估值汇总
        kpi.setAssessmentSumMeasure(assessmentSumMeasure);

    }

    /**指标对象赋值默认值根据指标类型
     * @param kpi指标对象
     * @throws ParseException
     */
    private void saveKpiDefaultValueByKpiType(Kpi kpi, Kpi kpiType) throws ParseException {
        //实际值采集频率
        kpi.setGatherFrequence(kpiType.getGatherFrequence());
        kpi.setGatherDayFormulr(kpiType.getGatherDayFormulr());
        kpi.setGatherDayFormulrShow(kpiType.getGatherDayFormulrShow());
        if (StringUtils.isNotBlank(kpiType.getGatherDayFormulr())) {
            kpi.setCalculatetime(DateUtils.stringToDateToDay(findKpiFrequenceByFormulr(kpiType.getGatherDayFormulr())));
        }
        //目标收集频率
        kpi.setTargetSetFrequence(kpiType.getTargetSetFrequence());
        kpi.setTargetSetDayFormular(kpiType.getTargetSetDayFormular());
        kpi.setTargetSetDayFormularShow(kpiType.getTargetSetDayFormularShow());
        if (StringUtils.isNotBlank(kpiType.getTargetSetDayFormular())) {
            kpi.setTargetCalculatetime(DateUtils.stringToDateToDay(findKpiFrequenceByFormulr(kpiType.getTargetSetDayFormular())));
        }
        //评估值是否使用公式
        kpi.setIsAssessmentFormula(kpiType.getIsAssessmentFormula());
        //评估值公式
        kpi.setAssessmentFormula(kpiType.getAssessmentFormula());
        //实际值是否使用公式
        kpi.setIsResultFormula(kpiType.getIsResultFormula());
        //实际值公式
        kpi.setResultFormula(kpiType.getResultFormula());
        //目标值是否使用公式
        kpi.setIsTargetFormula(kpiType.getIsTargetFormula());
        //目标值公式
        kpi.setTargetFormula(kpiType.getTargetFormula());
        //实际值采集延期天数
        kpi.setResultCollectInterval(3);
        //目标收集延期天数
        kpi.setTargetSetInterval(3);
        //报表小数点位置
        kpi.setScale(2);
        //趋势相对于
        kpi.setRelativeTo(kpiType.getRelativeTo());
        //实际值汇总
        kpi.setResultSumMeasure(kpiType.getResultSumMeasure());
        //目标值汇总
        kpi.setTargetSumMeasure(kpiType.getTargetSumMeasure());
        //评估值汇总
        kpi.setAssessmentSumMeasure(kpiType.getAssessmentSumMeasure());
        //标杆值
        kpi.setModelValue(kpiType.getModelValue());
        //最大值
        kpi.setMaxValue(kpiType.getMaxValue());
        //最小值
        kpi.setMinValue(kpiType.getMinValue());
        //实际收集报告频率
        kpi.setReportFrequence(kpiType.getReportFrequence());
        //目标收集报告频率
        kpi.setTargetSetReportFrequence(kpiType.getTargetSetFrequence());

    }
    
    /**
     * <pre>
     * 批量保存指标对象
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiSet
     *            指标ID集合
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveKpiBatch(Set<Kpi> kpiSet) {
        int year = Integer.valueOf(DateUtils.getYear(new Date()));
        Map<String, String> map = new HashMap<String, String>();
        if (null != kpiSet && kpiSet.size() > 0) {
            for (Kpi kpi : kpiSet) {
                // 不继承的属性
                removeKpiAttribute(kpi);
                // 设置自动生成kpi的sort字段值
                kpi.setSort(0);
                o_kpiDAO.merge(kpi);
                if (null != kpi.getGatherFrequence()) {
                    map.put(kpi.getId(), kpi.getGatherFrequence().getId());
                }
            }
            this.o_kpiGatherResultBO.saveBatchKpiGatherResultByFrequenceType(map, year);
        }
    }

    /**
     * <pre>
     * 保存指标和部门和人员的关系
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpi
     *            指标实体
     * @param orgEmp
     *            部门人员对象
     * @param type
     *            部门类型
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveKpiRelaOrgEmp(Kpi kpi, String orgEmp, String type) {
        if (StringUtils.isNotBlank(orgEmp)) {
            SysEmployee emp = null;
            SysOrganization org = null;
            JSONArray jsonarr = JSONArray.fromObject(orgEmp);
            for (int i = 0; i < jsonarr.size(); i++) {
                KpiRelaOrgEmp kpiRelaOrgEmp = new KpiRelaOrgEmp();
                JSONObject jsobj = jsonarr.getJSONObject(i);
                if (StringUtils.isNotBlank(jsobj.getString("deptid"))) {
                	org = new SysOrganization();
                	org.setId(jsobj.getString("deptid"));
                }
                if (StringUtils.isNotBlank(jsobj.getString("empid"))) {
                	emp = new SysEmployee();
                	emp.setId(jsobj.getString("empid"));
                }
                kpiRelaOrgEmp.setId(Identities.uuid());
                kpiRelaOrgEmp.setType(type);
                kpiRelaOrgEmp.setKpi(kpi);
                if (null != org) {
                    kpiRelaOrgEmp.setOrg(org);
                }
                if (null != emp) {
                    kpiRelaOrgEmp.setEmp(emp);
                }
                this.o_kpiRelaOrgEmpDAO.merge(kpiRelaOrgEmp);
            }
        }
    }

    /**
     * <pre>
     * 保存指标和维度关联关系
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpi
     *            指标实体
     * @param dimStr
     *            纬度信息
     * @param type
     *            纬度类型 ,主纬度或辅助纬度
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveKpiRelaDim(Kpi kpi, String dimStr, String type) {
        if (StringUtils.isNotBlank(dimStr)) {
            KpiRelaDim kpiRelaDim = new KpiRelaDim();
            DictEntry entryTemp = o_dictEntryBO.findDictEntryById(dimStr);
            kpiRelaDim.setId(Identities.uuid());
            kpiRelaDim.setSmDim(entryTemp);
            kpiRelaDim.setKpi(kpi);
            kpiRelaDim.setType(type);
            o_kpiRelaDimDAO.merge(kpiRelaDim);
        }
    }

    /**保存指标最后更新时间
     * @param params 时间区间参数信息
     * @param kpiId 指标id
     * @return
     */
    @Transactional
    public boolean saveKpiLastTimePeriod(String params, String kpiId) {
        boolean isSave = false;

        // 维护最新值
        if (StringUtils.isNotBlank(this.findKpiNewValueNes(params))) {
            TimePeriod timePeriodEn = o_kpiGatherResultBO.findMaxKpiTimePeriod(kpiId);//取指标最晚的指标采集结果时间区间维度
            Kpi kpiEn = this.findKpiById(kpiId);
            kpiEn.setLastTimePeriod(timePeriodEn);
            this.mergeKpi(kpiEn);
            isSave = true;
        }
        isSave = true;

        return isSave;
    }

    /**
     * <pre>
     * 保存指标采集结果
     * </pre>
     * 
     * @author 金鹏翔
     * @param params
     * @param kpiid
     *            指标ID
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveKpiGatherResultQuarter(String params, String kpiid) {
        Kpi kpi = this.findKpiById(kpiid);
        if (StringUtils.isNotBlank(params)) {
            JSONArray jsonarr = JSONArray.fromObject(params);
            JSONObject jsobj = null;
            KpiGatherResult result = null;
            List<TimePeriod> weekList = o_kpiTableBO.findTimePeriodByEType("0frequecy_week");
            List<TimePeriod> monthList = o_kpiTableBO.findTimePeriodByEType("0frequecy_month");
            List<TimePeriod> yearList = o_kpiTableBO.findTimePeriodByEType("0frequecy_year");
            List<TimePeriod> quarterList = o_kpiTableBO.findTimePeriodByEType("0frequecy_quarter");
            List<TimePeriod> halfyearList = o_kpiTableBO.findTimePeriodByEType("0frequecy_halfyear");
            List<KpiGatherResult> kpiGatherResultList = o_kpiTableBO.findKpiGatherResultsByKpiId(kpi.getId());
            boolean isKpiGatherResultByTimePeriodId = false;
            TimePeriod timePeriod = null;
            String timeperiodId = "";
            Double targetValue = null;
            Double finishValue = null;
            Double assessmentValue = null;
            // 得到预警方案实体
            AlarmPlan alarmPlan = this.findAlarmPlanByKpiId(kpiid, Contents.ALARMPLAN_REPORT);
            if (null == alarmPlan) {//指标没有关联告警方案,取常用告警方案
                alarmPlan = o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast", kpi.getCompany()
                        .getId());
            }
            for (Object object : jsonarr) {
                targetValue = null;
                finishValue = null;
                assessmentValue = null;
                jsobj = (JSONObject) object;
                timeperiodId = jsobj.toString().split(":")[0].replace("\"", "").replace("{", "");
                String values[] = jsobj.toString().split(":")[1].replace("\"", "").replace("}", "").split(",");

                if (values.length != 0) {
                    if (values.length == 1) {
                        if (values[0] != null) {
                            if (!values[0].equalsIgnoreCase("")) {
                                finishValue = Double.parseDouble(values[0]);
                            }
                        }
                    }
                    else if (values.length == 2) {
                        if (StringUtils.isNotBlank(values[0])) {
                            if (values[0] != null) {
                                if (!values[0].equalsIgnoreCase("")) {
                                    finishValue = Double.parseDouble(values[0]);
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(values[1])) {
                            if (values[1] != null) {
                                if (!values[1].equalsIgnoreCase("")) {
                                    targetValue = Double.parseDouble(values[1]);
                                }
                            }
                        }
                    }
                    else if (values.length == 3) {
                        if (StringUtils.isNotBlank(values[0])) {
                            if (values[0] != null) {
                                if (!values[0].equalsIgnoreCase("")) {
                                    finishValue = Double.parseDouble(values[0]);
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(values[1])) {
                            if (values[1] != null) {
                                if (!values[1].equalsIgnoreCase("")&&!"null".equals(values[1])) {
                                    targetValue = Double.parseDouble(values[1]);
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(values[2])) {
                            if (values[2] != null) {
                                if (!values[2].equalsIgnoreCase("")&&!"null".equals(values[2])) {
                                    assessmentValue = Double.parseDouble(values[2]);
                                }
                            }
                        }
                    }
                }

                isKpiGatherResultByTimePeriodId = o_kpiTableBO.isKpiGatherResultByTimePeriodId(kpiGatherResultList, timeperiodId);

                if (timeperiodId.indexOf('w') != -1) {// 周添加、编辑
                                                      // 周
                    timePeriod = o_kpiTableBO.getTimePeriod(weekList, timeperiodId);
                }
                else if (timeperiodId.indexOf("mm") != -1) {// 周添加、编辑
                                                            // 月
                    timePeriod = o_kpiTableBO.getTimePeriod(monthList, timeperiodId);
                }
                else if (timeperiodId.indexOf('Q') != -1) {// 季度添加、编辑
                                                           // 季度
                    timePeriod = o_kpiTableBO.getTimePeriod(quarterList, timeperiodId);
                }
                else if (timeperiodId.indexOf("hf") != -1) {// 季度添加、编辑
                                                            // 半年
                    timePeriod = o_kpiTableBO.getTimePeriod(halfyearList, timeperiodId);
                }
                else {
                    timePeriod = o_kpiTableBO.getTimePeriod(yearList, timeperiodId);
                }

                if (isKpiGatherResultByTimePeriodId) {// 年添加、编辑
                    result = o_kpiTableBO.getKpiGatherResult(kpiGatherResultList, timeperiodId);
                    result.setAssessmentValue(assessmentValue);
                    result.setKpi(kpi);
                    result.setTimePeriod(timePeriod);
                    result.setTargetValue(targetValue);
                    result.setFinishValue(finishValue);
                    result.setBeginTime(timePeriod.getStartTime());
                    result.setEndTime(timePeriod.getEndTime());
                    this.o_kpiGatherResultDAO.merge(result);
                    DictEntry assessmentStatus = null;
                   
                    //预警依据
                    if("kpi_alarm_measure_score".equalsIgnoreCase(kpi.getAlarmMeasure().getId())){//状态分数 评估值
                        if(null != assessmentValue){
                            assessmentStatus = findKpiAlarmStatusByValues(alarmPlan, Double.valueOf(assessmentValue));
                        }
                    }else if("kpi_alarm_measure_result".equalsIgnoreCase(kpi.getAlarmMeasure().getId())){//状态结果 实际值
                        if(null != assessmentValue){
                            assessmentStatus = findKpiAlarmStatusByValues(alarmPlan, Double.valueOf(finishValue));
                        }
                    }
                    result.setAssessmentStatus(assessmentStatus);
                    DictEntry trend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(result);
                    result.setDirection(trend);
                    this.o_kpiGatherResultDAO.merge(result);
                    //更新下期趋势
                    KpiGatherResult nextGatherResult = o_kpiGatherResultBO.findNextKpiGatherResultByKpiId(kpi.getId(), timeperiodId);
                    if(null!=nextGatherResult){
                    	DictEntry nextTrend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(nextGatherResult);
                        nextGatherResult.setDirection(nextTrend);
                        this.o_kpiGatherResultDAO.merge(nextGatherResult);
                    }
                    
                }
                else {
                    // 添加
                    result = new KpiGatherResult();
                    result.setId(Identities.uuid());
                    result.setKpi(kpi);
                    result.setTimePeriod(timePeriod);
                    result.setTargetValue(targetValue);
                    result.setFinishValue(finishValue);
                    result.setAssessmentValue(assessmentValue);
                    result.setBeginTime(timePeriod.getStartTime());
                    result.setEndTime(timePeriod.getEndTime());
                    result.setCompany(o_organizationBO.get(UserContext.getUser().getCompanyid()));
                    this.o_kpiGatherResultDAO.merge(result);
                    DictEntry assessmentStatus = null;
                    //预警依据
                    if("kpi_alarm_measure_score".equalsIgnoreCase(kpi.getAlarmMeasure().getId())){//状态分数 评估值
                    	if(null!=assessmentValue){
                    		assessmentStatus = findKpiAlarmStatusByValues(alarmPlan, Double.valueOf(assessmentValue));
                    	}
                    }else if("kpi_alarm_measure_result".equalsIgnoreCase(kpi.getAlarmMeasure().getId())){//状态结果 实际值
                    	if(null!=finishValue){
                    		assessmentStatus = findKpiAlarmStatusByValues(alarmPlan, Double.valueOf(finishValue));
                    	}
                    }
                    result.setAssessmentStatus(assessmentStatus);
                    
                    // 得到得到趋势对应数据字典实体
                    DictEntry trend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(result);
                    result.setDirection(trend);
                    this.o_kpiGatherResultDAO.merge(result);
                    
                    //更新下期趋势
                    KpiGatherResult nextGatherResult = o_kpiGatherResultBO.findNextKpiGatherResultByKpiId(kpi.getId(), timeperiodId);
                    if(null!=nextGatherResult){
                    	DictEntry nextTrend = o_kpiGatherResultBO.calculateTrendByAssessmentValue(nextGatherResult);
                        nextGatherResult.setDirection(nextTrend);
                        this.o_kpiGatherResultDAO.merge(nextGatherResult);
                    }
                    
                }
                
                /*
                 * 累积值公式计算--包括:季度累积值计算和年度累积值计算
                 */
                //季度累积值计算--只有月指标使用
                //根据指标id查询当期所在季度的指标采集结果
                KpiGatherResult quarterKpiGatherResult = o_kpiGatherResultBO.findQuarterKpiGatherResultByKpiId(kpi.getId(), timeperiodId);
                if (null != quarterKpiGatherResult) {
                    if ("0frequecy_month".equals(kpi.getGatherFrequence().getId())) {
                        if (!"kpi_sum_measure_manual".equals(kpi.getTargetSumMeasure().getId())) {
                            Double targetValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(result,
                                    "targetValueSum", kpi.getTargetSumMeasure().getId());
                            quarterKpiGatherResult.setTargetValue(targetValueSum);
                        }
                        if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
                            Double resultValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(result,
                                    "resultValueSum", kpi.getResultSumMeasure().getId());
                            quarterKpiGatherResult.setFinishValue(resultValueSum);
                        }
                        if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
                            Double assessmentValueSum = o_kpiGatherResultBO.calculateQuarterAccumulatedValueByCalculateItem(
                                    result, "assessmentValueSum", kpi.getAssessmentSumMeasure().getId());
                            quarterKpiGatherResult.setAssessmentValue(assessmentValueSum);
                            //计算预警状态
                            if(null!=assessmentValueSum){
                            	DictEntry assessmentStatus = findKpiAlarmStatusByValues(alarmPlan, assessmentValueSum);
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
                        //只有月指标需要进行季度累积值计算
                    }
                }

                //年度累积值计算--只有年度指标不使用
                //根据指标id查询当期所在年度的指标采集结果
                KpiGatherResult yearKpiGatherResult = o_kpiGatherResultBO.findYearKpiGatherResultByKpiId(kpi.getId(), timeperiodId);
                if (null != yearKpiGatherResult) {
                    if ("0frequecy_year".equals(kpi.getGatherFrequence().getId())) {
                        //年度指标不需要进行年度累积值计算
                    }
                    else {
                        if (!"kpi_sum_measure_manual".equals(kpi.getTargetSumMeasure().getId())) {
                            Double targetValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(result,
                                    "targetValueSum", kpi.getTargetSumMeasure().getId());
                            yearKpiGatherResult.setTargetValue(targetValueSum);
                        }
                        if (!"kpi_sum_measure_manual".equals(kpi.getResultSumMeasure().getId())) {
                            Double resultValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(result,
                                    "resultValueSum", kpi.getResultSumMeasure().getId());
                            yearKpiGatherResult.setFinishValue(resultValueSum);
                        }
                        if (!"kpi_sum_measure_manual".equals(kpi.getAssessmentSumMeasure().getId())) {
                            Double assessmentValueSum = o_kpiGatherResultBO.calculateYearAccumulatedValueByCalculateItem(result,
                                    "assessmentValueSum", kpi.getAssessmentSumMeasure().getId());
                            yearKpiGatherResult.setAssessmentValue(assessmentValueSum);
                            //计算预警状态  
                            if (null != assessmentValueSum) {
                                DictEntry assessmentStatus = findKpiAlarmStatusByValues(alarmPlan, assessmentValueSum);
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

    }

    /**
     * <pre>
     * 保存指标信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param form
     *            指标Form
     * @return
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @Transactional
    @RecordLog(value="保存指标信息")
    public String saveKpi(KpiForm form) throws ParseException {
        Kpi kpi = null;
        Kpi parentKpi = null;
        Kpi kpiType = null;
        Category category = null;
        int sort = 0;
        // 获得机构对象
        String namedefault = form.getNamedefault();
        String opflag = form.getOpflag();
        String categoryid = form.getCategoryId();
        String isInheritStr = form.getIsInheritStr();
        boolean isInherit = false;
        if (Contents.DICT_Y.equals(isInheritStr)) {
            isInherit = true;
        }
        boolean isNameDefault = false;
        if (Contents.DICT_Y.equals(namedefault)) {
            isNameDefault = true;
        }
        String kpitypeid = form.getKpitypeid();
        String parentKpiId = form.getParentKpiId();
        String kpiId = Identities.uuid();
        String ynid = form.getStatusStr();// 状态
        String unitsStr = form.getUnitsStr();// 单位
        String startDateStr = form.getStartDateStr();
        boolean isMonitor = false;
        if (Contents.DICT_Y.equals(form.getMonitorStr())) {
            isMonitor = true;
        }
        String typeStr = form.getTypeStr();// 指标类型
        String dataTypeStr = form.getDataTypeStr();// 数据类型
        String kpiTypeStr = form.getKpiTypeStr();// 指标性质
        String alarmMeasureStr = form.getAlarmMeasureStr();// 亮灯依据
        String relativeToStr = form.getRelativeToStr();// 趋势相对于
        String alarmBasisStr = form.getAlarmBasisStr();// 预警依据
        String otherDim = form.getOtherDim();// 辅助维度
        String mainDim = form.getMainDim();// 主维度
        String ownDept = form.getOwnDept();// 所属部门
        String viewDept = form.getViewDept();// 查看部门
        String reportDept = form.getReportDept();// 报告部门
        String gatherDept = form.getGatherDept();// 采集部门
        String targetDept = form.getTargetDept();// 采集部门
        String gatherFrequenceDict = form.getGatherFrequenceDict();// 采集频率
        SysOrganization company = o_organizationBO.get(UserContext.getUser().getCompanyid());

        boolean updateFlag = false;
        String currentId = StringUtils.defaultIfEmpty(form.getId(), "");

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isNotBlank(parentKpiId)) {
            parentKpi = findKpiById(parentKpiId);
        }

        if (null != parentKpi) {
            parentKpi.setIsLeaf(false);
        }
        if (StringUtils.isNotBlank(kpitypeid)) {
            kpiType = findKpiById(kpitypeid);
        }
        if (StringUtils.isNotBlank(categoryid)) {
            category = o_categoryBO.findCategoryById(categoryid);
        }
        if (Contents.DICT_Y.equals(namedefault) && !"edit".equals(opflag)) {// 默认名称的判断,当选中默认名称时,指标名称为'分类名称'+'指标类型名称'
            if (null != kpiType) {
                form.setName(category.getName() + " " + kpiType.getName());
            }
            else {
                form.setName(form.getName());
            }
        }

        if (StringUtils.isNotBlank(currentId)) {// update
            updateFlag = true;
            kpi = findKpiById(currentId);
            sort = null == kpi.getSort() ? 0 : kpi.getSort();
            kpiId = kpi.getId();
            // 删除部门和人员信息
            this.removeKpiRelaOrgEmp(kpi, Contents.ORG_ALL);
            // 删除主,辅助维度信息
            this.removeKpiRelaDim(kpi);
            String gatherDayFormulr = kpi.getGatherDayFormulr();
            String targetSetDayFormular = kpi.getTargetSetDayFormular();
            String gatherReportDayFormulr = kpi.getGatherReportDayFormulr();
            String targetSetReportDayFormulr = kpi.getTargetSetReportDayFormulr();
            DictEntry gatherDict = kpi.getGatherFrequence();
            DictEntry targetSetDict = kpi.getTargetSetFrequence();
            DictEntry targetSetReportDict = kpi.getTargetSetReportFrequence();
            DictEntry gatherSetReportDict = kpi.getReportFrequence();
            Integer resultCollectInterval = kpi.getResultCollectInterval();
            Integer targetSetInterval = kpi.getTargetSetInterval();
            Date calculatetime = kpi.getCalculatetime();
            Date targetCalculatetime = kpi.getTargetCalculatetime();
            Integer scale = kpi.getScale();
            DictEntry relativeToDict = kpi.getRelativeTo();
            String gatherDayFormulrShow = kpi.getGatherDayFormulrShow();
            String gatherReportDayFormulrShow = kpi.getGatherReportDayFormulrShow();
            String targetSetDayFormularShow = kpi.getTargetSetDayFormularShow();
            String targetReportDayFormulrShow = kpi.getTargetReportDayFormulrShow();
            String gatherCron = kpi.getGatherDayFormulr();
            boolean isleaf = kpi.getIsLeaf();
            String isResultFormula = kpi.getIsResultFormula();
            String resultFormula = kpi.getResultFormula();
            String isTargetFormula = kpi.getIsTargetFormula();
            String targetFormula = kpi.getTargetFormula();
            String isAssessmentFormula = kpi.getIsAssessmentFormula();
            String assessmentFormula = kpi.getAssessmentFormula();
            DictEntry resultSum = kpi.getResultSumMeasure();
            DictEntry targetSum = kpi.getTargetSumMeasure();
            DictEntry assessmentSum = kpi.getAssessmentSumMeasure();
            Double modelValue = kpi.getModelValue();
            Double maxValue = kpi.getMaxValue();
            Double minValue = kpi.getMinValue();
            TimePeriod lastTime = kpi.getLastTimePeriod();
            String isAssessmentDsType = kpi.getIsAssessmentDsType();
            String isResultDsType = kpi.getIsResultDsType();
            String isTargetDsType = kpi.getIsTargetDsType();
            KpiDataSource asseessmentDs = kpi.getAsseessmentDs();
            KpiDataSource resultDs = kpi.getResultDs();
            KpiDataSource targetDs = kpi.getTargetDs();
            DictEntry isCalc = kpi.getCalc();
            String isFocus = kpi.getIsFocus();
            String gatherMemo = kpi.getGatherDesc();
            BeanUtils.copyProperties(form, kpi);
            kpi.setTargetReportDayFormulrShow(targetReportDayFormulrShow);
            kpi.setGatherDayFormulr(gatherDayFormulr);
            kpi.setTargetSetDayFormular(targetSetDayFormular);
            kpi.setTargetSetReportDayFormulr(targetSetReportDayFormulr);
            kpi.setGatherReportDayFormulr(gatherReportDayFormulr);
            kpi.setGatherFrequence(gatherDict);
            kpi.setTargetSetFrequence(targetSetDict);
            kpi.setTargetSetReportFrequence(targetSetReportDict);
            kpi.setReportFrequence(gatherSetReportDict);
            kpi.setResultCollectInterval(resultCollectInterval);
            kpi.setTargetSetInterval(targetSetInterval);
            kpi.setScale(scale);
            kpi.setRelativeTo(relativeToDict);
            kpi.setCalculatetime(calculatetime);
            kpi.setTargetCalculatetime(targetCalculatetime);
            kpi.setIsLeaf(isleaf);
            kpi.setGatherDayFormulrShow(gatherDayFormulrShow);
            kpi.setGatherReportDayFormulrShow(gatherReportDayFormulrShow);
            kpi.setTargetSetDayFormularShow(targetSetDayFormularShow);
            kpi.setGatherDayFormulr(gatherCron);
            kpi.setIsResultFormula(isResultFormula);
            kpi.setResultFormula(resultFormula);
            kpi.setIsTargetFormula(isTargetFormula);
            kpi.setTargetFormula(targetFormula);
            kpi.setIsAssessmentFormula(isAssessmentFormula);
            kpi.setAssessmentFormula(assessmentFormula);
            kpi.setResultSumMeasure(resultSum);
            kpi.setTargetSumMeasure(targetSum);
            kpi.setAssessmentSumMeasure(assessmentSum);
            kpi.setModelValue(modelValue);
            kpi.setMaxValue(maxValue);
            kpi.setMinValue(minValue);
            kpi.setLastTimePeriod(lastTime);
            kpi.setIsAssessmentDsType(isAssessmentDsType);
            kpi.setIsResultDsType(isResultDsType);
            kpi.setIsTargetDsType(isTargetDsType);
            kpi.setAsseessmentDs(asseessmentDs);
            kpi.setResultDs(resultDs);
            kpi.setTargetDs(targetDs);
            kpi.setCalc(isCalc);
            kpi.setIsFocus(isFocus);
            kpi.setGatherDesc(gatherMemo);
        }
        else {
            Long count = 0L;
            if (Contents.KPI_ROOT.equals(parentKpiId)) {
                count = (Long) o_kpiDAO.createCriteria().add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE))
                        .add(Restrictions.and(Restrictions.isNull("parent"), Restrictions.ne("deleteStatus", false)))
                        .setProjection(Projections.rowCount()).uniqueResult();
            }
            else {
                count = (Long) o_kpiDAO.createCriteria().add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE))
                        .add(Restrictions.and(Restrictions.eq("parent.id", parentKpiId), Restrictions.ne("deleteStatus", false)))
                        .setProjection(Projections.rowCount()).uniqueResult();
            }

            sort = count.intValue() + 1;
            kpi = new Kpi();
            BeanUtils.copyProperties(form, kpi);
            //拷贝指标类型默认值;
            if (null != kpiType) {
                saveKpiDefaultValueByKpiType(kpi, kpiType);
            }
            kpi.setIsLeaf(true);
        }
        Integer level = 0;
        String idseq = ".";
        if (null != parentKpi) {
            idseq = parentKpi.getIdSeq();
            level = parentKpi.getLevel();
        }
        kpi.setId(kpiId);
        kpi.setIsInherit(isInherit);
        kpi.setIsNameDefault(isNameDefault);
        kpi.setIsKpiCategory(Contents.KPI_TYPE);
        kpi.setCompany(company);
        kpi.setDeleteStatus(true);
        kpi.setIdSeq(idseq + kpiId + ".");
        kpi.setSort(sort);
        kpi.setParent(parentKpi);
        kpi.setLevel((null == level ? 0 : level) + 1);
        kpi.setBelongKpiCategory(kpiType);
        if (StringUtils.isNotBlank(startDateStr)) {
            kpi.setStartDate(sf.parse(form.getStartDateStr()));
        }
        if (StringUtils.isNotBlank(unitsStr)) {
            kpi.setUnits(o_dictEntryBO.findDictEntryById(unitsStr));
        }
        if (StringUtils.isNotBlank(ynid)) {
            kpi.setStatus(o_dictEntryBO.findDictEntryById(ynid));
        }
        if (StringUtils.isNotBlank(typeStr)) {
            kpi.setType(o_dictEntryBO.findDictEntryById(typeStr));
        }
        if (StringUtils.isNotBlank(dataTypeStr)) {
            kpi.setDataType(o_dictEntryBO.findDictEntryById(dataTypeStr));
        }
        if (StringUtils.isNotBlank(kpiTypeStr)) {
            kpi.setKpiType(o_dictEntryBO.findDictEntryById(kpiTypeStr));
        }
        if (StringUtils.isNotBlank(alarmMeasureStr)) {
            kpi.setAlarmMeasure(o_dictEntryBO.findDictEntryById(alarmMeasureStr));
        }
        if (StringUtils.isNotBlank(alarmBasisStr)) {
            kpi.setAlarmBasis(o_dictEntryBO.findDictEntryById(alarmBasisStr));
        }
        if (StringUtils.isNotBlank(relativeToStr)) {
            kpi.setRelativeTo(o_dictEntryBO.findDictEntryById(relativeToStr));
        }
        if (StringUtils.isNotBlank(gatherFrequenceDict)) {
            kpi.setGatherFrequence(o_dictEntryBO.findDictEntryById(gatherFrequenceDict));
        }
        kpi.setIsMonitor(isMonitor);

        // 保存指标对象
        o_kpiDAO.merge(kpi);
        // 保存所属部门信息
        this.saveKpiRelaOrgEmp(kpi, ownDept, Contents.BELONGDEPARTMENT);
        // 保存采集部门信息
        this.saveKpiRelaOrgEmp(kpi, gatherDept, Contents.GATHERDEPARTMENT);
        // 保存目标部门信息
        this.saveKpiRelaOrgEmp(kpi, targetDept, Contents.TARGETDEPARTMENT);
        // 保存查看部门信息
        this.saveKpiRelaOrgEmp(kpi, viewDept, Contents.VIEWDEPARTMENT);
        // 保存报告部门信息
        this.saveKpiRelaOrgEmp(kpi, reportDept, Contents.REPORTDEPARTMENT);
        // 保存主维度信息
        this.saveKpiRelaDim(kpi, mainDim, Contents.MAIN);
        // 保存辅助维度信息
        if (StringUtils.isNotBlank(otherDim)) {
            String[] otherDims = otherDim.split(",");
            for (String tmpstr : otherDims) {
                this.saveKpiRelaDim(kpi, tmpstr, Contents.ASSISTANT);
            }
        }
        if (!updateFlag) {
            // 保存维度和指标的关系
            KpiRelaCategory kpiRelaCategory = new KpiRelaCategory();
            kpiRelaCategory.setId(Identities.uuid());
            kpiRelaCategory.setCategory(category);
            kpiRelaCategory.setKpi(kpi);
            kpiRelaCategory.setIsCreator(true);
            o_kpiRelaCategoryDAO.merge(kpiRelaCategory);
            //复制指标类型关联的告警信息
            if (null != kpiType) {
                Set<KpiRelaAlarm> kpiRelaAlarms = kpiType.getKpiRelaAlarms();
                for (KpiRelaAlarm kpiRelaAlarm : kpiRelaAlarms) {
                    KpiRelaAlarm newKpiRelaAlarm = new KpiRelaAlarm();
                    newKpiRelaAlarm.setKpi(kpi);
                    newKpiRelaAlarm.setId(Identities.uuid());
                    newKpiRelaAlarm.setStartDate(kpiRelaAlarm.getStartDate());
                    newKpiRelaAlarm.setrAlarmPlan(kpiRelaAlarm.getrAlarmPlan());
                    newKpiRelaAlarm.setFcAlarmPlan(kpiRelaAlarm.getFcAlarmPlan());
                    o_kpiRelaAlarmDAO.merge(newKpiRelaAlarm);
                }
            }
        }

        return kpiId;

    }

    /**
     * <pre>
     * 逻辑删除指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标类型Id
     * @since fhd　Ver 1.1
     */
    @Transactional
    public boolean removeKpiType(String id) {
        Kpi kpi = this.findKpiById(id);
        kpi.setDeleteStatus(false);
        this.o_kpiDAO.merge(kpi);
        return true;
    }

    /**
     * <pre>
     * 删除指标和维度关联关系
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpi
     *            指标对象
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void removeKpiRelaDim(Kpi kpi) {
        if (null != kpi) {
            Set<KpiRelaDim> kpiRelaDimSet = kpi.getKpiRelaDims();
            for (KpiRelaDim kpiRelaDim : kpiRelaDimSet) {
                this.o_kpiRelaDimDAO.delete(kpiRelaDim);
            }
        }
    }

    /**
     * <pre>
     * 删除和指标所关联的部门和人员信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpi
     * @param type
     *            type 部门类型 "B":所属部门 , "R":报告部门 , V:查看部门 , G:采集部门 , T:目标部门,
     *            "ALL":所有部门
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void removeKpiRelaOrgEmp(Kpi kpi, String type) {
        if (null != kpi) {
            Set<KpiRelaOrgEmp> kpiRelaOrgEmpSet = kpi.getKpiRelaOrgEmps();
            for (KpiRelaOrgEmp kpiRelaOrgEmp : kpiRelaOrgEmpSet) {
                if (Contents.ORG_ALL.equals(type)) {
                    o_kpiRelaOrgEmpDAO.delete(kpiRelaOrgEmp);
                }
                else {
                    if (type.equals(kpiRelaOrgEmp.getType())) {
                        o_kpiRelaOrgEmpDAO.delete(kpiRelaOrgEmp);
                    }
                }
            }
        }
    }

    /**
     * <pre>
     * 批量删除指标信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiItems
     *            kpiId集合
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void removeKpiBatch(String kpiItems) {
        Kpi kpi = null;
        JSONObject jsobj = JSONObject.fromObject(kpiItems);
        String categoryid = jsobj.getString("categoryid");
        JSONArray kpiidArr = jsobj.getJSONArray("kpiids");
        String[] kpiids = new String[] {};
        kpiids = (String[]) kpiidArr.toArray(kpiids);
        Criteria criteria = this.o_kpiRelaCategoryDAO.createCriteria().add(Restrictions.in("kpi.id", kpiids))
                .add(Restrictions.eq("category.id", categoryid));
        List<KpiRelaCategory> kpiRelaCategorys = criteria.list();
        for (KpiRelaCategory kpiRelaCategory : kpiRelaCategorys) {
            if (kpiRelaCategory.getIsCreator()) {
                // 所属指标逻辑删除
                kpi = kpiRelaCategory.getKpi();
                kpi.setDeleteStatus(false);
                this.o_kpiDAO.merge(kpi);
            }
            
            // 关联指标删除关联关系
            this.o_kpiRelaCategoryDAO.delete(kpiRelaCategory);
            
        }
    }

    /**
     * <pre>
     *  批量删除指标信息(我的度量指标,所有度量指标,战略目标关联的度量指标,指标类型关联的度量指标列表中的删除事件使用)
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiItems
     *            kpiId集合
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void removeCommonKpiBatch(String kpiItems) {
        JSONArray kpiidArr = JSONArray.fromObject(kpiItems);
        Criteria criteria = this.o_kpiDAO.createCriteria();
        criteria.add(Restrictions.in("id", kpiidArr));
        List<Kpi> kpiList = criteria.list();
        for (Kpi kpi : kpiList) {
            kpi.setDeleteStatus(false);
            o_kpiDAO.merge(kpi);
        }
    }
    
    /**删除目标和指标的关联关系,更新指标的删除状态
     * @param kpiItems kpiId集合
     * @param smId 目标id
     */
    @Transactional
    public void removeSmRelaKpiBatch(String kpiItems,String smId) {
    	Kpi kpi = null;
        JSONArray kpiidArr = JSONArray.fromObject(kpiItems);
        Criteria criteria = this.o_smRelaKpiDAO.createCriteria().add(Restrictions.in("kpi.id", kpiidArr))
                .add(Restrictions.eq("strategyMap.id", smId));
        
        List<SmRelaKpi> smRelaKpiList = criteria.list();
        for (SmRelaKpi smRelaKpi : smRelaKpiList) {
        	kpi = smRelaKpi.getKpi();
            kpi.setDeleteStatus(false);
            o_kpiDAO.merge(kpi);
            this.o_smRelaKpiDAO.delete(smRelaKpi);
        }
    }

    /**
     * <pre>
     * 去掉指标属性,不继承这些属性
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpi
     * @since fhd　Ver 1.1
     */
    private void removeKpiAttribute(Kpi kpi) {
        kpi.setCode(null);
        kpi.setSort(0);
    }

    /**
     * <pre>
     * 去掉指标对象无关的对象
     * </pre>
     * 
     * @author 陈晓哲
     * @param criteria
     * @since fhd　Ver 1.1
     */
    public void removeKpiRelaObject(Criteria criteria) {
        criteria.setFetchMode("units", FetchMode.SELECT).setFetchMode("createBy", FetchMode.SELECT).setFetchMode("lastModifyBy", FetchMode.SELECT)
                .setFetchMode("relativeTo", FetchMode.SELECT).setFetchMode("kpiType", FetchMode.SELECT).setFetchMode("alarmBasis", FetchMode.SELECT)
                .setFetchMode("parent", FetchMode.SELECT).setFetchMode("alarmMeasure", FetchMode.SELECT)
                .setFetchMode("belongKpiCategory", FetchMode.SELECT).setFetchMode("dataType", FetchMode.SELECT)
                .setFetchMode("status", FetchMode.SELECT).setFetchMode("type", FetchMode.SELECT).setFetchMode("gatherFrequence", FetchMode.SELECT)
                .setFetchMode("reportFrequence", FetchMode.SELECT).setFetchMode("kpiGatherResult", FetchMode.SELECT)
                .setFetchMode("resultSumMeasure", FetchMode.SELECT).setFetchMode("targetSumMeasure", FetchMode.SELECT)
                .setFetchMode("assessmentSumMeasure", FetchMode.SELECT);

    }
    
    /**
     * 根据idseq查询指标对象
     * 
     * @param idSeq
     * @return map <idseq,kpi>
     */
    public Map<String, Kpi> findKpiAllMap() {
        Map<String, Kpi> map = new HashMap<String, Kpi>();
        Criteria criteria = o_kpiDAO.createCriteria();
        List<Kpi> kpilist = criteria.list();
        for (Kpi kpi : kpilist) {
            map.put(kpi.getName(), kpi);
        }
        return map;
    }
}
