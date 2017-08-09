package com.fhd.sm.business;

import static org.hibernate.FetchMode.JOIN;
import static org.hibernate.FetchMode.SELECT;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.comm.AlarmPlanDAO;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.kpi.SmRelaAlarmDAO;
import com.fhd.dao.kpi.SmRelaKpiDAO;
import com.fhd.dao.kpi.SmRelaOrgEmpDAO;
import com.fhd.dao.kpi.StrategyMapDAO;
import com.fhd.dao.kpi.StrategyMapRelaDimDAO;
import com.fhd.dao.kpi.StrategyMapRelaThemeDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiRelaOrgEmp;
import com.fhd.entity.kpi.SmRelaAlarm;
import com.fhd.entity.kpi.SmRelaDim;
import com.fhd.entity.kpi.SmRelaKpi;
import com.fhd.entity.kpi.SmRelaOrgEmp;
import com.fhd.entity.kpi.SmRelaTheme;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.web.form.StrategyMapForm;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

/**
 * KPI_战略目标BO ClassName:KpiStrategyMapBO
 * 
 * @author 杨鹏
 * @version
 * @since Ver 1.1
 * @Date 2012 2012-8-15 上午11:56:52
 * 
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class StrategyMapBO {

    @Autowired
    private StrategyMapDAO o_kpiStrategyMapDAO;

    @Autowired
    private StrategyMapTreeBO o_strategyMapTreeBO;

    @Autowired
    private StrategyMapRelaDimDAO o_strategyMapRelaDimDAO;

    @Autowired
    private StrategyMapRelaThemeDAO o_strategyMapRelaThemeDAO;

    @Autowired
    private OrganizationBO o_organizationBO;

    @Autowired
    private DictBO o_dictEntryBO;

    @Autowired
    private SmRelaKpiDAO o_smRelaKpiDAO;

    @Autowired
    private KpiDAO o_kpiDAO;

    @Autowired
    private SmRelaAlarmDAO o_smRelaAlarmDAO;

    @Autowired
    private AlarmPlanDAO o_alarmPlanDAO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;

    @Autowired
    private SmRelaOrgEmpDAO o_smRelaOrgEmpDAO;


    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;
    
    @Autowired
    private KpiBO o_kpiBO;

    private static final Log logger = LogFactory.getLog(StrategyMapBO.class);
    /**
     * 根据ID查询 findById:
     * 
     * @author 杨鹏
     * @param id
     * @return
     * @since fhd　Ver 1.1
     */
    public StrategyMap findById(String id) {
        return (StrategyMap) this.o_kpiStrategyMapDAO.createCriteria().add(Property.forName("id").eq(id))
                .add(Property.forName("deleteStatus").eq(true)).setFetchMode("status", SELECT).setFetchMode("createBy", FetchMode.SELECT)
                .setFetchMode("lastModifyBy", FetchMode.SELECT).setFetchMode("relativeTo", FetchMode.SELECT)
                .setFetchMode("company", FetchMode.SELECT).setFetchMode("parent", FetchMode.SELECT).uniqueResult();
               
    }
    
    /**根据目标id集合查询目标对象集合
     * @param idList目标id集合
     * @return
     */
    public List<StrategyMap> findByIdList(String[] idList) {
    	return this.o_kpiStrategyMapDAO.createCriteria().add(Property.forName("id").in(idList))
    			.add(Property.forName("deleteStatus").eq(true)).setFetchMode("status", SELECT).list();
    }

    /**
     * 根据公司Id查询根目录 findById:
     * 
     * @author 张帅
     * @param id
     * @return
     * @since fhd　Ver 1.1
     */
    public StrategyMap findSmByCompanyId(String id) {
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
        //criteria.setCacheable(true);
        if (StringUtils.isNotBlank(id)) {
            criteria.add(Restrictions.eq("company.id", id));
        }
        criteria.add(Restrictions.isNull("parent"));
        criteria.add(Restrictions.eq("deleteStatus", true));
        //避免查询过多的关联对象 //add by chenxiaozhe
        criteria.setFetchMode("parent", FetchMode.SELECT).setFetchMode("status", FetchMode.SELECT).setFetchMode("createBy", FetchMode.SELECT)
                .setFetchMode("lastModifyBy", FetchMode.SELECT).setFetchMode("relativeTo", FetchMode.SELECT)
                .setFetchMode("company", FetchMode.SELECT);
        return (StrategyMap) criteria.uniqueResult();
    }

    /**
     * 根据父节点Id查询 findById:
     * 
     * @author 张帅
     * @param id
     * @return
     * @since fhd　Ver 1.1
     */
    public List<StrategyMap> findSmByParentId(String id) {
        return this.findBySome(null, null, id, false, null, true);
    }

    /**
     * 多条件查询查询 findBySome:
     * 
     * @author 张帅
     * @param ids
     *            目标id
     * @param companyId
     *            公司id
     * @param parentId
     *            父节点id
     * @param isRoot
     *            是否是根节点
     * @param statuss
     *            状态
     * @param deleteStatus
     *            删除状态
     * @return
     * @since fhd　Ver 1.1
     */

    public List<StrategyMap> findBySome(String[] ids, String companyId, String parentId, Boolean isRoot, String[] statuss, Boolean deleteStatus) {
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria().setFetchMode("createBy", FetchMode.SELECT)
                .setFetchMode("lastModifyBy", FetchMode.SELECT).setFetchMode("relativeTo", FetchMode.SELECT)
                .setFetchMode("company", FetchMode.SELECT);
        //criteria.setCacheable(true);
        if (null != ids && ids.length > 0) {
            criteria.add(Restrictions.in("id", ids));
        }
        if (StringUtils.isNotBlank(companyId)) {
            criteria.add(Restrictions.eq("company.id", companyId));
        }
        else {
            criteria.setFetchMode("company", FetchMode.SELECT);
        }
        if (StringUtils.isNotBlank(parentId)) {
            if ("sm_root".equals(parentId)) {
                criteria.add(Restrictions.isNull("parent"));
            }
            else {
                criteria.add(Restrictions.eq("parent.id", parentId));
            }

            //去掉parent属性的不需要加载的eager对象 //add by chenxiaozhe
            criteria.setFetchMode("parent.createBy", FetchMode.SELECT);
            criteria.setFetchMode("parent.lastModifyBy", FetchMode.SELECT);
            criteria.setFetchMode("parent.status", FetchMode.SELECT);
            criteria.setFetchMode("parent.company", FetchMode.SELECT);
            criteria.setFetchMode("parent.relativeTo", FetchMode.SELECT);
        }
        else {
            criteria.setFetchMode("parent", FetchMode.SELECT);
        }
        if (null != isRoot && isRoot) {
            criteria.add(Restrictions.isNull("parent"));
        }
        if (null != statuss && statuss.length > 0) {
            criteria.add(Restrictions.in("status", statuss));
        }
        else {
            criteria.setFetchMode("status", FetchMode.SELECT);
        }
        criteria.addOrder(Order.asc("name"));
        criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
        return criteria.list();
    }

    /**
     * <pre>
     * listMap:根据id查找目标
     * </pre>
     * 
     * @author 张 帅
     * @return ids串
     * @since fhd　Ver 1.1
     */
    public List<Map<String, Object>> findStrategyById(String[] ids) {
        Map<String, Object> data = null;
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        List<StrategyMap> list = this.findBySome(ids, null, null, null, null, true);
        for (StrategyMap kpiStrategyMap : list) {
            data = this.o_strategyMapTreeBO.wrapStrategyMapNode(kpiStrategyMap, false, false, false);
            datas.add(data);
        }
        return datas;
    }

    /**
     * <pre>
     * 显示指标衡量列表
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            查询参数
     * @param currentSmId
     *            当前目标ID
     * @param page
     *            分页对象
     * @param sort
     *            排序字段
     * @param dir
     *            排序顺序
     * @return 分页对象
     * @since fhd　Ver 1.1
     */
    public List<SmRelaKpi> findSmRelaKpiBySome(String name, String currentSmId, String sort, String dir) {
        String sortstr = "id";
        Criteria dc = this.o_smRelaKpiDAO.createCriteria();
        // 添加按照公司过滤
        dc.createAlias("kpi", "kpi");
        dc.add(Restrictions.eq("kpi.deleteStatus", true));
        dc.add(Restrictions.eq("kpi.company.id", UserContext.getUser().getCompanyid()));
        if (StringUtils.isNotBlank(name)) {
            dc.add(Property.forName("kpi.name").like(name, MatchMode.ANYWHERE));
        }
        if (StringUtils.equals("weight", sort)) {
            sortstr = "weight";
        }
        else if (StringUtils.equals("name", sort)) {
            sortstr = "kpi.name";
        }
        if (StringUtils.isNotBlank(currentSmId)) {
            dc.add(Property.forName("strategyMap").eq(this.findById(currentSmId)));
        }
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }
        return dc.list();
    }

    /**
     * <pre>
     * 根据查询条件查询策略目标对象的个数
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            目标名称
     * @param smId
     *            目标id
     * @since fhd　Ver 1.1
     */
    public long findStrategyMapCountByName(String name, String smId,String companyId) {
        long count = 0;
        Criteria criteria = this.o_kpiStrategyMapDAO.createCriteria().add(Restrictions.eq("deleteStatus", true));
        // 添加按照公司过滤
        criteria.add(Restrictions.eq("company.id", companyId));
        if (!"".equals(smId) && !Contents.ID_UNDEFINED.equals(smId)) {// update
            count = (Long) criteria.add(Restrictions.eq("name", name)).add(Restrictions.ne("id", smId)).setProjection(Projections.rowCount())
                    .uniqueResult();
        }
        else {// add
            count = (Long) criteria.add(Restrictions.eq("name", name)).setProjection(Projections.rowCount()).uniqueResult();
        }
        return count;
    }

    /**
     * <pre>
     * 根据code查找目标对象数量
     * </pre>
     * 
     * @author 陈晓哲
     * @param code
     * @param smId
     * @return
     * @since fhd　Ver 1.1
     */
    public long findStrategyMapCountByCode(String code, String smId,String companyId) {
        long count = 0;
        Criteria criteria = this.o_kpiStrategyMapDAO.createCriteria().add(Restrictions.eq("deleteStatus", true));
        // 添加按照公司过滤
        criteria.add(Restrictions.eq("company.id",companyId));
        if (!"".equals(smId) && !Contents.ID_UNDEFINED.equals(smId)) {// update
            count = (Long) criteria.add(Restrictions.eq("code", code)).add(Restrictions.ne("id", smId)).setProjection(Projections.rowCount())
                    .uniqueResult();
        }
        else {// add
            count = (Long) criteria.add(Restrictions.eq("code", code)).setProjection(Projections.rowCount()).uniqueResult();
        }
        return count;
    }

    /**
     * <pre>
     * 查询目标关联的预警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            查询参数
     * @param currentSmId
     *            当前目标ID
     * @param page
     *            分页对象
     * @param sort
     *            排序字段
     * @param dir
     *            排序方向
     * @return 分页对象
     * @since fhd　Ver 1.1
     */
    public List<SmRelaAlarm> findSmRelaAlarmBySome(String name, String currentSmId, String editflag, String sort, String dir) {
        String sortstr = "id";
        Criteria dc = this.o_smRelaAlarmDAO.createCriteria();
        dc.createAlias("strategyMap", "sm");
        dc.add(Restrictions.eq("sm.company.id", UserContext.getUser().getCompanyid()));
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
        if (StringUtils.isNotBlank(currentSmId)) {
            dc.add(Property.forName("strategyMap").eq(this.findById(currentSmId)));
        }
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }
        List<SmRelaAlarm> list = dc.list();
        if (!"true".equals(editflag) && null != list && list.size() == 0) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(Integer.valueOf(DateUtils.getYear(new Date())), 0, 1);
            // 判断,如果没有结果集,则默认添加全局告警信息
            AlarmPlan forecast = o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast");
            AlarmPlan alarm = o_alarmPlanBO.findAlarmPlanByNameType("常用预警方案", "0alarm_type_kpi_alarm");
            if (null != forecast || null != alarm) {
                list = new ArrayList<SmRelaAlarm>();
                SmRelaAlarm kpiRelaAlarm = new SmRelaAlarm();
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
     * 根据类型查询告警或预警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param type
     *            告警或预警类型
     * @return 告警或预警列表
     * @since fhd　Ver 1.1
     */
    public List<AlarmPlan> findAlarmPlanByType(String type) {
        String types = type.equals("alarmtype") ? "0alarm_type_kpi_forecast" : "0alarm_type_kpi_alarm";
        DictEntry entry = this.o_dictEntryBO.findDictEntryById(types);
        Criteria criteria = this.o_alarmPlanDAO.createCriteria();
        criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
        criteria.add(Restrictions.eq("type", entry));
        criteria.add(Restrictions.eq("deleteStatus", true));
        return criteria.list();
    }

    /**
     * <pre>
     * queryStrategyMapBySearchName:模糊匹配目标名称
     * </pre>
     * 
     * @author 张 帅
     * @param searchName
     *            查询条件
     * @param type
     *            根据当前员工emp、机构org过滤
     * @param idseq
     *            是否是idseq
     * @param deleteStatus
     *            删除状态
     * @return id串
     * @since fhd　Ver 1.1
     */
    protected Set<String> findStrategyMapBySearchName(String searchName, String type, Boolean idseq, Boolean deleteStatus) {
        List<StrategyMap> list = null;
        Set<String> idSet = new HashSet<String>();
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria()
                //去掉一些不需要的关联对象//add by chenxiaozhe
                .setFetchMode("createBy", FetchMode.SELECT).setFetchMode("lastModifyBy", FetchMode.SELECT).setFetchMode("status", FetchMode.SELECT)
                .setFetchMode("parent", FetchMode.SELECT).setFetchMode("relativeTo", FetchMode.SELECT).setFetchMode("company", FetchMode.SELECT);
        //criteria.setCacheable(true);
        if (StringUtils.isNotBlank(searchName)) {
            criteria.add(Restrictions.like("name", searchName, MatchMode.ANYWHERE));
        }

        String companyid = null;
        String empId = null;
        String orgId = null;

        if (null != UserContext.getUser()) {
            companyid = UserContext.getUser().getCompanyid();
        }
        criteria.add(Restrictions.eq("company.id", companyid));
        if (StringUtils.isNotBlank(type)) {

            empId = UserContext.getUser().getEmpid();
            orgId = UserContext.getUser().getMajorDeptId();

            criteria.createAlias("smRelaOrgEmps", "sroe");
            if ("emp".equals(type)) {
                criteria.add(Restrictions.eq("sroe.emp.id", empId));
            }
            else if ("org".equals(type)) {
                criteria.add(Restrictions.eq("sroe.org.id", orgId));
            }
        }

        list = criteria.list();
        if (idseq) {
        	if(null != list && list.size() > 0) {
                for (StrategyMap entity : list) {
                    String[] idsTemp = entity.getIdSeq().split("\\.");
                    idSet.addAll(Arrays.asList(idsTemp));
                }
        	}

        }
        else {
        	if(null != list && list.size() > 0) {
                for (StrategyMap entity : list) {
                    idSet.add(entity.getId());
                }
        	}
        }
        return idSet;
    }

    /**
     * <pre>
     * 查询所有的目标和组织单元的关联对象
     * </pre>
     * 
     * @author 陈晓哲
     * @return
     * @since fhd　Ver 1.1
     */
    public List<SmRelaOrgEmp> findSmRelaOrgEmpAll() {
        return this.o_smRelaOrgEmpDAO.createCriteria().list();
    }

    /**
     * <pre>
     * 根据父节点Id生成目标编码
     * </pre>
     * 
     * @author 陈晓哲
     * @param parentId
     *            父目标ID
     * @return
     * @since fhd　Ver 1.1
     */
    public String findCodeBySmParentId(String parentId, String smId) {
        long sort = 0;
        long count = 0;
        StrategyMap sm = null;
        StrategyMap psm = null;
        StringBuffer code = new StringBuffer();
        if (StringUtils.isNotBlank(smId) && !Contents.ID_UNDEFINED.equals(smId)) {
            sm = this.findById(smId);
            if (null != sm) {
            	if(StringUtils.isNotBlank(sm.getCode())) {
            		return sm.getCode();
            	}  else {  
                    if(null == sm.getSort()){
                    	sort = 0;
                    }else{
                    	sort = sm.getSort();
                    }
            	}
            }

        }
        if ("sm_root".equals(parentId)) {
            code.append("SM");
        }
        else {
            if (StringUtils.isNotBlank(parentId)) {
                psm = findById(parentId);
                String idSeq = psm.getIdSeq();
                if (StringUtils.isNotBlank(idSeq)) {
                    String[] idarr = StringUtils.split(idSeq, ".");
                    for (int i = 0; i < idarr.length; i++) {
                        if (StringUtils.isNotBlank(idarr[i])) {
                            sm = findById(idarr[i]);
                            String codeno = sm.getCode();
                            if (null != codeno && StringUtils.isNotBlank(codeno)) {
                                if (!codeno.contains(code)) {
                                    code.append(codeno).append("");
                                }
                                else {
                                    code.append(codeno.substring(code.length())).append("");
                                }
                            }
                            else {
                                if (null == sm.getParent()) {
                                    code.append("SM");
                                }
                                else {
                                    code.append("000").append(sm.getSort()).append("");
                                }
                            }
                        }
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(smId) && !Contents.ID_UNDEFINED.equals(smId)) {
            count = sort;
            code.append("000").append(count);
        }
        else {
            Criteria criteria = this.o_kpiStrategyMapDAO.createCriteria();
            if ("sm_root".equals(parentId)) {
                criteria.add(Restrictions.isNull("parent"));
            }
            else {
                criteria.add(Restrictions.eq("parent", psm));
            }
            criteria.add(Restrictions.eq("deleteStatus", true));
            criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
            count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
            code.append("000").append(count + 1);
        }

        return code.toString();
    }

    /**
     * <pre>
     * 根据kpiid查询所属部门
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiID
     * @since fhd　Ver 1.1
     */
    public JSONObject findKpiRelaOrgEmpById(String kpiID) {
        JSONObject kpiOrgObject = new JSONObject();
        if (StringUtils.isNotBlank(kpiID)) {
            String kpiid = "";
            JSONArray kpiIdArr = JSONArray.fromObject(kpiID);
            for (int i = 0; i < kpiIdArr.size(); i++) {
            	String orgid = "";
                StringBuffer orgBuf = new StringBuffer();
                kpiid = (String) kpiIdArr.get(i);
                Kpi kpi = this.o_kpiDAO.get(kpiid);
                Set<KpiRelaOrgEmp> orgSet = kpi.getKpiRelaOrgEmps();
                for (KpiRelaOrgEmp kpiRelaOrgEmp : orgSet) {
                    if (null != kpiRelaOrgEmp && null != kpiRelaOrgEmp.getOrg() && Contents.BELONGDEPARTMENT.equals(kpiRelaOrgEmp.getType())) {

                        orgBuf.append(kpiRelaOrgEmp.getOrg().getOrgname()).append(",");
                    }

                }
                if (orgBuf.length() > 0){
                	orgid = orgBuf.toString().substring(0, orgBuf.length() - 1);
                }
                JSONObject kpiObj = new JSONObject();
                kpiObj.put("orgName", orgid);
                String frequence = kpi.getGatherFrequence().getName();
	       		if(frequence.equals(Contents.FREQUECY_MONTH)){
   	    			frequence = "每月";
   	    		}else if(frequence.equals(Contents.FREQUECY_WEEK)){
   	    			frequence = "每周";
   	    		}else if(frequence.equals(Contents.FREQUECY_QUARTER)){
   	    			frequence = "每季";
   	    		}else if(frequence.equals(Contents.FREQUECY_YEAR)){
   	    			frequence = "每年";
   	    		}else if(frequence.equals(Contents.FREQUECY_HALFYEAR)){
   	    			frequence = "每半年";
   	    		}
                kpiObj.put("frequence", frequence);
                kpiObj.put("unit", kpi.getUnits().getName());
                kpiOrgObject.put(kpiid, kpiObj);
            }
        }
        return kpiOrgObject;
    }

    /**
     * <pre>
     * 查找目标关联的部门和人员信息,并格式化为json格式,目标编辑和查看时使用
     * </pre>
     * 
     * @author 陈晓哲
     * @param sm
     *            目标对象
     * @return
     * @since fhd　Ver 1.1
     */
    public JSONObject findSmRelaOrgEmpBySmToJson(StrategyMap sm) {
        JSONObject jsobj = new JSONObject();
        if (null != sm) {
            String type = "";
            JSONArray blongArr = new JSONArray();
            JSONArray reportArr = new JSONArray();
            JSONArray viewArr = new JSONArray();
            SysEmployee sysEmp = null;
            SysOrganization sysOrg = null;
            Set<SmRelaOrgEmp> smRelaOrgEmpSet = sm.getSmRelaOrgEmps();
            for (SmRelaOrgEmp smRelaOrgEmp : smRelaOrgEmpSet) {
                type = smRelaOrgEmp.getType();
                JSONObject blongobj = new JSONObject();
                sysOrg = smRelaOrgEmp.getOrg();
                sysEmp = smRelaOrgEmp.getEmp();
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
                    logger.error("获得orgid异常:信息["+e.toString()+"]");
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
                    logger.error("获得Empid异常:信息["+e.toString()+"]");
                }
                if ("B".equals(type)) {
                    blongArr.add(blongobj);
                }
                if ("R".equals(type)) {
                    reportArr.add(blongobj);
                }
                if ("V".equals(type)) {
                    viewArr.add(blongobj);
                }
            }
            jsobj.put("ownDept", blongArr);
            jsobj.put("reportDept", reportArr);
            jsobj.put("viewDept", viewArr);
        }
        return jsobj;
    }
    /**
     * <pre>
     * 查找目标关联的部门和人员信息,并格式化为json格式,目标编辑和查看时使用
     * </pre>
     * 
     * @author 陈晓哲
     * @param sm
     *            目标对象
     * @return
     * @since fhd　Ver 1.1
     */
    public Map<String, Object> findSmRelaOrgEmpBySm(StrategyMap sm) {
    	Map<String, Object> map = new HashMap<String,Object>();
    	List<String> ownDept = new ArrayList<String>();
    	List<String> reportDept = new ArrayList<String>();
    	List<String> viewDept = new ArrayList<String>();
        if (null != sm) {
        	 String type = "";
             SysEmployee sysEmp = null;
             SysOrganization sysOrg = null;
             Set<SmRelaOrgEmp> smRelaOrgEmpSet = sm.getSmRelaOrgEmps();
             for (SmRelaOrgEmp smRelaOrgEmp : smRelaOrgEmpSet) {
                 type = smRelaOrgEmp.getType();
                 sysOrg = smRelaOrgEmp.getOrg();
                 sysEmp = smRelaOrgEmp.getEmp();
                 StringBuffer sb = new StringBuffer();
                 try {
                     if (null != sysOrg) {
                         sb.append(sysOrg.getOrgname());
                     }
                 }
                 catch (Exception e) {
                	 logger.error("获得orgname异常:信息["+e.toString()+"]");
                 }
                 try {
                     if (null != sysEmp.getId()) {
                         sb.append("：（").append(sysEmp.getEmpname()).append("）");
                     }
                 }
                 catch (Exception e) {
                	 logger.error("获得Empname异常:信息["+e.toString()+"]");
                 }
                 if ("B".equals(type)) {
                	 ownDept.add(sb.toString());
                 }
                 if ("R".equals(type)) {
                	 reportDept.add(sb.toString());
                 }
                 if ("V".equals(type)) {
                	 viewDept.add(sb.toString());
                 }
             }
        }
    	map.put("ownDept", ownDept);
    	map.put("reportDept", reportDept);
    	map.put("viewDept", viewDept);
    	return map;
    }

    /**
     * <pre>
     * 根据目标ID查询指标集合
     * </pre>
     * 
     * @param page
     *            分页对象
     * @param query
     *            查询条件
     * @param id
     *            目标ID
     * @param sort
     *            排序字段
     * @param dir
     *            排序顺序
     * @return
     */
    public Page<Kpi> findKpiBySmId(Page<Kpi> page, String query, String id, String sort, String dir,String gType) {

        String sortstr = "id";
        DetachedCriteria dc = DetachedCriteria.forClass(Kpi.class);
        dc.createAlias("kpiRelaSm", "kpirelasm");
        dc.add(Restrictions.eq("isKpiCategory", "KPI"));
        dc.add(Restrictions.eq("deleteStatus", true));
        dc.add(Restrictions.eq("status.id", Contents.DICT_Y));
        if (StringUtils.isNotBlank(query)) {
            dc.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(id)) {
            dc.add(Restrictions.eq("kpirelasm.strategyMap.id", id));
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
     * 根据目标ID查询最新的关联指标的采集结果
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
     * @param smid
     *            目标ID
     * @return
     */
    @RecordLog(value="根据目标ID查询最新的关联指标的采集结果")
    public List<Object[]> findLastSmRelaKpiResults(Map<String, Object> map, int start, int limit, String queryName, String sortColumn, String dir,
            String smid) {

        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        if (StringUtils.isNotBlank(smid) && !Contents.ID_UNDEFINED.equals(smid)) {
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
            selectBuf.append(" ,dirdict.dict_entry_value  direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid,kpi.is_focus ");
            selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence,smrelakpi.eweight eweight,kpi.escale  escale ");
            countBuf.append(" select count(*) ");

            fromLeftJoinBuf.append(" from t_kpi_kpi  kpi left outer join t_kpi_kpi_gather_result  result on kpi.id=result.kpi_id and kpi.latest_time_period_id=result.time_period_id ");
            fromLeftJoinBuf.append(" left outer join t_com_time_period  time on result.time_period_id=time.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.direction = dirdict.id ");
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
            fromLeftJoinBuf.append(" left outer join t_kpi_sm_rela_kpi  smrelakpi on smrelakpi.kpi_id=kpi.id ");
            fromLeftJoinBuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI' and smrelakpi.strategy_map_id=:smid and 1=1 ");
            if (StringUtils.isNotBlank(queryName)) {
                fromLeftJoinBuf.append(" and kpi.kpi_name like '%").append(queryName).append("%'");
            }
            if (StringUtils.isNotBlank(companyid)) {
                fromLeftJoinBuf.append(" and kpi.company_id=:companyid ");
            }
            SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).toString());
            countQuery.setParameter("smid", smid);
            countQuery.setParameter("companyid", companyid);
            
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
            SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(orderbuf).toString());
            sqlquery.setParameter("smid", smid);
            sqlquery.setParameter("companyid", companyid);
            sqlquery.setFirstResult(start);
            sqlquery.setMaxResults(limit);
            list = sqlquery.list();
        }
        return list;
    }

    /**
     * 根据记分卡ID查询具体的关联指标的采集结果
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
     * @param id
     *            记分卡ID
     * @param frequence
     *            指标采集频率
     * @return
     */
    public List<Object[]> findSpecificSmRelaKpiResults(Map<String, Object> map, int start, int limit, String queryName, String sortColumn,
            String dir, String smid, String year, String quarter, String month, String week, String frequence) {
        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        if (StringUtils.isNotBlank(smid) && !Contents.ID_UNDEFINED.equals(smid)) {
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
            selectBuf.append(" select kpi.id  id, kpi.kpi_name  name , time.time_period_full_name  timerange , result.assessment_value  assessmentValue ,result.target_value  targetValue , result.finish_value  finishValue ,");
            selectBuf.append(statusColumn);
            selectBuf.append(" ,dirdict.dict_entry_value  direction ,kpi.is_enabled is_enabled,result.time_period_id,result.is_memo,result.id kgrid,kpi.is_focus  ");
            selectBuf.append(",unitdict.dict_entry_name units,kpi.gather_frequence frequence,smrelakpi.eweight eweight ,kpi.escale  escale ");
            countBuf.append(" select count(*) ");
            fromLeftJoinBuf.append(" from t_kpi_kpi  kpi ");
            fromLeftJoinBuf.append(" left outer join ");
            fromLeftJoinBuf.append(" (t_kpi_kpi_gather_result  result  ");
            fromLeftJoinBuf.append(" inner join ");
            fromLeftJoinBuf.append(" t_com_time_period  time on result.time_period_id=time.id  ");
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
            fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ");
            fromLeftJoinBuf.append(" left outer join t_kpi_sm_rela_kpi  smrelakpi on smrelakpi.kpi_id=kpi.id ");
            fromLeftJoinBuf.append(" where kpi.delete_status=1 and kpi.is_kpi_category='KPI' and 1=1 ");
            if (StringUtils.isNotBlank(smid)) {
                wherebuf.append(" and smrelakpi.strategy_map_id=? ");
                paralist.add(smid);
            }
            if (StringUtils.isNotBlank(queryName)) {
                wherebuf.append(" and kpi.kpi_name like '%").append(queryName).append("%'");
            }
            if (StringUtils.isNotBlank(companyid)) {
                wherebuf.append(" and kpi.company_id=?");
                paralist.add(companyid);
            }
            Object[] paraobjects = new Object[paralist.size()];
            paraobjects = paralist.toArray(paraobjects);
            SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).append(wherebuf).toString(), paraobjects);
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
            SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(
                    selectBuf.append(fromLeftJoinBuf).append(wherebuf).append(orderbuf).toString(), paraobjects);
            sqlquery.setFirstResult(start);
            sqlquery.setMaxResults(limit);
            list = sqlquery.list();
        }
        return list;
    }

   

    /**
     * 查询所有战略目标
     * 
     * @author 金鹏祥
     * @return List<StrategyMap>
     */
    public List<StrategyMap> findStrategyMapAll() {
        List<StrategyMap> list = null;
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.addOrder(Order.desc("level"));
        //criteria.setCacheable(true);
        list = criteria.list();
        return list;
    }
    
    /**查询所有要计算的战略目标
     * @return
     */
    public List<StrategyMap> findCalcStrategyMapAll() {
    	List<StrategyMap> list = null;
    	Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
    	criteria.add(Restrictions.eq("deleteStatus", true));
    	criteria.add(Restrictions.sqlRestriction("(LENGTH(this_.forecast_formula)<>0 or LENGTH(this_.assessment_formula)<>0)"));
    	criteria.addOrder(Order.desc("level"));
    	//criteria.setCacheable(true);
    	list = criteria.list();
    	return list;
    }
    /**查询所在公司下所有的战略母豹
     * @param companyId公司ID
     * @return
     */
    public List<StrategyMap> findStrategyMapAllByCompanyId(String companyId) {
    	List<StrategyMap> list = null;
    	Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
    	criteria.add(Restrictions.eq("deleteStatus", true));
    	criteria.add(Restrictions.eq("company.id", companyId));
    	criteria.addOrder(Order.desc("level"));
    	//criteria.setCacheable(true);
    	list = criteria.list();
    	return list;
    }

    /**
     * 查询所有记分卡与告警方案
     * 
     * @author 金鹏祥
     * @return HashMap<String, CategoryRelaAlarm>
     */
    public Map<String, SmRelaAlarm> findSmRelaAlarmAll() {
        HashMap<String, SmRelaAlarm> map = new HashMap<String, SmRelaAlarm>();
        List<SmRelaAlarm> list = null;
        Criteria c = this.o_smRelaAlarmDAO.createCriteria();
        c.createAlias("strategyMap", "strategymap");
        c.setFetchMode("strategymap", FetchMode.JOIN);
        c.add(Restrictions.isNotNull("rAlarmPlan"));
        list = c.list();
        for (SmRelaAlarm smRelaAlarm : list) {
            if (smRelaAlarm.getStrategyMap() != null) {
                map.put(smRelaAlarm.getStrategyMap().getId(), smRelaAlarm);
            }
        }
        return map;
    }

    /**
     * 根据战略目标查询最新的告警方案
     * 
     * @param smId 目标ID
     * @param type 告警或是预警 forecast:预警,report:告警
     * @return  AlarmPlan 告警方案
     */
    public AlarmPlan findAlarmPlanBySmId(String smId, String type) {
        String alarmType = "fcAlarmPlan";
        if ("forecast".equals(type)) {
            alarmType = "fcAlarmPlan";
        }
        else if ("report".equals(type)) {
            alarmType = "rAlarmPlan";
        }
        Date currentDate = new Date();
        SmRelaAlarm currentAlarm = null;
        AlarmPlan alarmPlan = null;
        if (StringUtils.isNotBlank(smId)) {
            boolean flag = false;
            SmRelaAlarm smRelaAlarmTemp = null;
            Criteria criteria = o_smRelaAlarmDAO.createCriteria();
            criteria.add(Property.forName("strategyMap.id").eq(smId));
            criteria.setFetchMode(alarmType, JOIN);
            criteria.addOrder(Order.asc("startDate"));
            List<SmRelaAlarm> smRelaAlarms = criteria.list();
            if (null != smRelaAlarms && smRelaAlarms.size() > 0) {
                for (int i = 0; i < smRelaAlarms.size(); i++) {
                    SmRelaAlarm smRelaAlarm = smRelaAlarms.get(i);
                    Date startDate = smRelaAlarm.getStartDate();
                    if (currentDate.before(startDate)) {
                        return null;
                    }
                    if (currentDate.equals(startDate)) {
                        currentAlarm = smRelaAlarm;
                        flag = true;
                        break;
                    }
                    else {
                        if (i == smRelaAlarms.size() - 1) {
                            smRelaAlarmTemp = smRelaAlarms.get(i);
                        }
                        else {
                            smRelaAlarmTemp = smRelaAlarms.get(i + 1);
                        }
                        if (currentDate.after(startDate) && currentDate.before(smRelaAlarmTemp.getStartDate())) {
                            currentAlarm = smRelaAlarmTemp;
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag) {
                    currentAlarm = smRelaAlarms.get(smRelaAlarms.size() - 1);
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
     * <pre>
     * 查找目标关联的部门和人员信息,并格式化为json格式,目标编辑和查看时使用
     * </pre>
     * 
     * @author zhengjunxiang
     * @param sm 目标对象
     * @return
     */
    public JSONObject findSmRelaOrgEmpBySmToJsonStr(StrategyMap sm) {
        JSONObject jsobj = new JSONObject();
        if (null != sm) {
            String type = "";
            StringBuffer blongArr = new StringBuffer();
            StringBuffer reportArr = new StringBuffer();
            StringBuffer viewArr = new StringBuffer();
            SysEmployee sysEmp = null;
            SysOrganization sysOrg = null;
            Set<SmRelaOrgEmp> smRelaOrgEmpSet = sm.getSmRelaOrgEmps();
            for (SmRelaOrgEmp smRelaOrgEmp : smRelaOrgEmpSet) {
                type = smRelaOrgEmp.getType();
                sysOrg = smRelaOrgEmp.getOrg();
                sysEmp = smRelaOrgEmp.getEmp();
                String deptName = "";
                String empName = "";
                try {
                    if (null != sysOrg) {
                        deptName = sysOrg.getOrgname();
                    }
                }
                catch (Exception ex) {
                    deptName = "";
                    logger.error("获得orgname异常:信息["+ex.toString()+"]");
                }
                try {
                    if (null != sysEmp) {
                        empName = sysEmp.getEmpname();
                    }
                }
                catch (Exception ex) {
                    empName = "";
                    logger.error("获得Empname异常:信息["+ex.toString()+"]");
                }
                if ("B".equals(type)) {
                    blongArr.append(deptName).append(empName).append(",");
                }
                if ("R".equals(type)) {
                    reportArr.append(deptName).append(empName).append(",");
                }
                if ("V".equals(type)) {
                    viewArr.append(deptName).append(empName).append(",");
                }
            }

            //去掉，
            if (blongArr.length() > 0) {
                jsobj.put("ownDept", blongArr.toString().substring(0, blongArr.length() - 1));
            }
            else {
                jsobj.put("ownDept", blongArr.toString());
            }
            if (reportArr.length() > 0) {
                jsobj.put("reportDept", reportArr.toString().substring(0, reportArr.length() - 1));
            }
            else {
                jsobj.put("reportDept", reportArr.toString());
            }
            if (viewArr.length() > 0) {
                jsobj.put("viewDept", viewArr.toString().substring(0, viewArr.length() - 1));
            }
            else {
                jsobj.put("viewDept", viewArr.toString());
            }

        }
        return jsobj;
    }
    
    /**查询关注的战略目标
     * @param query 查询条件 
     * @return
     */
    public List<StrategyMap> findFocusStrategymap(String query,String isFocus){
    	Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
        criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
        if(StringUtils.isNotBlank(query)){
        	criteria.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("isFocus", isFocus));
    	return criteria.list();
    }
    
    /**查询最近的我所属的部门的目标
     * @param deptId
     * @param deptType
     * @param start
     * @param limit
     * @param queryName
     * @param sortColumn
     * @param dir
     * @return
     */
    public List<Object[]> findLastStrategyMapBelongDeptId(Map<String, Object> map,String deptId,String deptType,int start, int limit, String queryName, String sortColumn, String dir){
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
        
        selectBuf.append(" select sm.id  id, sm.strategy_map_name  name , time.time_period_full_name  timerange , result.assessment_value  assessmentValue , ");
        selectBuf.append(statusColumn);
        selectBuf.append(" ,dirdict.dict_entry_value  direction ,sm.estatus,sm.is_focus,oe.EMP_ID,e.EMP_NAME,p.strategy_map_name,sm.PARENT_ID,time.id timeid,result.id rid  ");
        countBuf.append(" select count(*) ");

        fromLeftJoinBuf.append(" from t_kpi_strategy_map  sm");
        fromLeftJoinBuf.append(" left outer join t_kpi_strategy_map p on sm.PARENT_ID=p.id");
        fromLeftJoinBuf.append(" left outer join t_kpi_sm_assess_result  result on sm.id=result.object_id and result.data_type='str' and sm.latest_time_period_id=result.time_period_id ");
        fromLeftJoinBuf.append(" left outer join t_com_time_period  time on result.time_period_id=time.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.trend = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_kpi_sm_rela_org_emp oe on oe.strategy_map_id=sm.ID and oe.etype='B' ");
        fromLeftJoinBuf.append(" left outer join T_SYS_EMPLOYEE e on oe.EMP_ID = e.id");
        fromLeftJoinBuf.append(" where  sm.id in (select strategy_map_id from t_kpi_sm_rela_org_emp where 1=1");
        fromLeftJoinBuf.append(" and org_id=? ");
        paralist.add(deptId);
        fromLeftJoinBuf.append(" and etype=? ");
        paralist.add(deptType);
        fromLeftJoinBuf.append(" )and  sm.delete_status=1 ");

        if (StringUtils.isNotBlank(queryName)) {
            fromLeftJoinBuf.append(" and sm.strategy_map_name like '%").append(queryName).append("%'");
        }


        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);
        SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).toString(), paraobjects);
        map.put("totalCount", countQuery.uniqueResult());

        if (StringUtils.isNotBlank(sortColumn)) {
            orderbuf.append(" order by ");
            if (StringUtils.equals("name", sortColumn)) {
                orderbuf.append("sm.strategy_map_name");
            }
            else if (StringUtils.equals("id", sortColumn)) {
                orderbuf.append("sm.id");
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
            else if (StringUtils.equals("parentName", sortColumn)) {
                orderbuf.append("p.strategy_map_name");
            }
            else if (StringUtils.equals("owerName", sortColumn)) {
            	orderbuf.append("e.emp_name");
            }
           
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(" asc ");
            }
            else {
                orderbuf.append(" desc ");
            }
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(",sm.strategy_map_name asc ");
            }else{
                orderbuf.append(",sm.strategy_map_name desc ");
            }
        }

        SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(orderbuf).toString(), paraobjects);
        sqlquery.setFirstResult(start);
        sqlquery.setMaxResults(limit);
        list = sqlquery.list();
        return list;
    }
    
    /**查询具体时间段我所属的部门的目标
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
    public List<Object[]> findSpecificStrategyMapBelongDeptId(Map<String, Object> map,String deptId,String deptType,int start, int limit, String queryName, String sortColumn, String dir,String year, String quarter, String month, String week, String frequence){
    	
    	List<Object[]> list = null;
        //deptId = UserContext.getUser().getMajorDeptId();
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
        selectBuf.append(" select sm.id  id, sm.strategy_map_name  name , time.time_period_full_name  timerange , result.assessment_value  assessmentValue , ");
        selectBuf.append(statusColumn);
        selectBuf.append(",dirdict.dict_entry_value  direction ,sm.estatus,sm.is_focus,oe.EMP_ID,e.EMP_NAME,p.strategy_map_name,sm.PARENT_ID ,time.id timeid ,result.id rid ");
        countBuf.append(" select count(*) ");
        fromLeftJoinBuf.append(" from t_kpi_strategy_map  sm ");
        fromLeftJoinBuf.append(" left outer join t_kpi_strategy_map p on sm.PARENT_ID=p.id");
        fromLeftJoinBuf.append(" left outer join ");
        fromLeftJoinBuf.append(" (t_kpi_sm_assess_result result  ");
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
        fromLeftJoinBuf.append(" ) on sm.id=result.object_id and result.data_type='str'");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ");
        fromLeftJoinBuf.append(" left outer join t_sys_dict_entry  dirdict on result.trend = dirdict.id ");
        fromLeftJoinBuf.append(" left outer join t_kpi_sm_rela_org_emp oe on oe.strategy_map_id=sm.ID and oe.etype='B' ");
        fromLeftJoinBuf.append(" left outer join T_SYS_EMPLOYEE e on oe.EMP_ID = e.id");
        wherebuf.append(" where  sm.id in (select strategy_map_id from t_kpi_sm_rela_org_emp where 1=1 ");
        wherebuf.append(" and org_id=? ");
        paralist.add(UserContext.getUser().getMajorDeptId());
        wherebuf.append(" and etype=? ");
        paralist.add(deptType);
        wherebuf.append(")and sm.delete_status=1 ");
        if (StringUtils.isNotBlank(queryName)) {
            wherebuf.append(" and sm.strategy_map_name like '%").append(queryName).append("%'");
        }
        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);
        SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromLeftJoinBuf).append(wherebuf).toString(), paraobjects);
        map.put("totalCount", countQuery.uniqueResult());

        if (StringUtils.isNotBlank(sortColumn)) {
            orderbuf.append(" order by ");
            if (StringUtils.equals("name", sortColumn)) {
                orderbuf.append("sm.strategy_map_name");
            }
            else if (StringUtils.equals("id", sortColumn)) {
                orderbuf.append("sm.id");
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
            else if (StringUtils.equals("parentName", sortColumn)) {
                orderbuf.append("p.strategy_map_name");
            }
            else if (StringUtils.equals("owerName", sortColumn)) {
            	orderbuf.append("e.emp_name");
            }
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(" asc ");
            }
            else {
                orderbuf.append(" desc ");
            }
            if ("ASC".equalsIgnoreCase(dir)) {
                orderbuf.append(",sm.strategy_map_name asc ");
            }else{
                orderbuf.append(",sm.strategy_map_name desc ");
            }
        }

        SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).append(wherebuf).append(orderbuf).toString(), paraobjects);
        sqlquery.setFirstResult(start);
        sqlquery.setMaxResults(limit);
        list = sqlquery.list();
        return list;
    }
    
    
    /**
     * 查询目标关联指标
     * @param map
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
     * @return 查询结果
     */
    
    public List<Map<String, Object>> findAllSmKpiDetail(Map<String, Object> map, int start, int limit, String queryName, String sortColumn,
            String dir, String year, String quarter, String month, String week, String frequence) {
    	Map<String,Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap("KPI");
        List<Map<String,Object>> detailList = new ArrayList<Map<String,Object>>();
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        List<Object> paralist = new ArrayList<Object>();
        StringBuffer cond = new StringBuffer();
        StringBuffer kpicond = new StringBuffer();
        countBuf.append("select count(*) ");
        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) && StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
        	cond.append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(year);
            paralist.add(year);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(month)) {
        	cond.append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(quarter);
            paralist.add(quarter);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
        	cond .append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(month);
            paralist.add(month);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isNotBlank(week)) {
        	cond.append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(week);
            paralist.add(week);
        }

        selectBuf.append("SELECT")
                 .append("    SM.ID,") //战略目标ID
                 .append("    SM.STRATEGY_MAP_NAME,") //战略目标名称
                 .append("    SMRESULT.ASSESSMENT_VALUE smassess,")//战略目标评估值
                 .append("    SMSTATUS.DICT_ENTRY_VALUE smstaus,")//战略目标亮灯状态
                 .append("    KPI.KPI_NAME,")//关联指标名称
                 .append("    TIME.TIME_PERIOD_FULL_NAME,")//时间维度
                 .append("    SK.EWEIGHT,")//权重
                 .append("    UNITDICT.DICT_ENTRY_NAME unit,")//单位
                 .append("    FREDICT.DICT_ENTRY_NAME fre,")//指标采集频率
                 .append("    RESULT.ASSESSMENT_VALUE kpiassess,")//指标评估值
                 .append("    RESULT.TARGET_VALUE,")// 指标结果值
                 .append("    RESULT.FINISH_VALUE,")// 指标完成值
                 .append("    STATUSDICT.DICT_ENTRY_VALUE kpistatus,")// 指标亮灯状态
                 .append("    ORG.ORG_NAME ");// 指标所属部门名称
        fromBuf.append(" FROM T_KPI_STRATEGY_MAP SM ")
               .append("      LEFT OUTER JOIN T_KPI_SM_ASSESS_RESULT SMRESULT ")
               .append("      ON SM.ID = SMRESULT.OBJECT_ID  ")
               .append(cond)
               .append("      left outer join t_sys_dict_entry  smstatus")
               .append("      on smresult.assessment_status = smstatus.id ")
               .append("      LEFT outer JOIN (t_kpi_sm_rela_kpi sk ")
               .append("                      inner join t_kpi_kpi kpi")
               .append("                      on sk.KPI_ID = kpi.ID AND ")
               .append("                         kpi.DELETE_STATUS = '1' AND")
               .append("                         kpi.IS_ENABLED = '0yn_y' and")
               .append("                         kpi.is_kpi_category='KPI'")
               .append("                      left outer join (t_kpi_kpi_gather_result result")
               .append("                                       INNER JOIN t_com_time_period time")
               .append("                                       on time.id = result.TIME_PERIOD_ID")
               .append(kpicond)
               .append("                                       )  on kpi.id=result.kpi_id ")
               .append("                                          and  kpi.gather_frequence=time.etype")
               .append("                 )  on sm.id = sk.STRATEGY_MAP_ID ")
               .append("      left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id")
               .append("      left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id")
               .append("      left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
               .append("      left outer join (t_kpi_kpi_rela_org_emp ko ")
               .append("      inner join t_sys_organization  org on ko.org_id  = org.id )")
               .append("      on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
          wherebuf.append(" where sm.delete_status = 1");
          // 战略目标模糊查询
          if(StringUtils.isNotBlank(queryName)) {
        	  wherebuf.append(" and sm.STRATEGY_MAP_NAME like '%").append(queryName).append("%'   "); 
          }
          if (StringUtils.isNotBlank(companyid)) {
              wherebuf.append(" and sm.company_id=?");
              paralist.add(companyid);
          }
          Object[] paraobjects = new Object[paralist.size()];
          paraobjects = paralist.toArray(paraobjects);
          orderbuf.append("order by sm.ELEVEL,sm.id,sk.EWEIGHT DESC");
          if (StringUtils.isNotBlank(sortColumn)) {
        	  // 增加额外的排序
          }
          SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromBuf).append(wherebuf).toString(), paraobjects);
          map.put("totalCount", countQuery.uniqueResult());
          SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(
                  selectBuf.append(fromBuf).append(wherebuf).append(orderbuf).toString(), paraobjects);
          if(start >= 0) {
        	  sqlquery.setFirstResult(start);
              sqlquery.setMaxResults(limit); 
          }
         
          List<Object[]> list  = sqlquery.list();
          if(null != list && list.size() > 0) {
              for(Object[] o: list) {
            	  Map<String, Object> dataMap = new HashMap<String,Object>();
    			  dataMap.put("id", String.valueOf(o[0]));
    			  dataMap.put("smName", String.valueOf(o[1]));
    			  if(null != o[2]) {
    				  dataMap.put("smAssessValue",convertValue(Double.parseDouble(String.valueOf(o[2])),2));
    			  } else {
    				  dataMap.put("smAssessValue", o[2]);
    			  }
    			
    			  dataMap.put("smStatus", o[3]);
    			  dataMap.put("kpiName", o[4]);
    			  dataMap.put("timePeriod",o[5]);
    			  if(null != o[6]) {
    				  dataMap.put("kpiWeight",convertValue(Double.parseDouble(String.valueOf(o[6])),2));
    			  } else {
    				  dataMap.put("kpiWeight", o[6]);
    			  }
    			  dataMap.put("units", o[7]);
    			  dataMap.put("kpiFrequency", o[8]);
    			  if(null != o[9]) {
    				   dataMap.put("assessMentValue",convertValue(Double.parseDouble(String.valueOf(o[9])),2));
    			  } else {
    				  dataMap.put("assessMentValue", o[9]);
    			  }			 
    			  dataMap.put("targetValue", o[10]);
    			  dataMap.put("finishValue", o[11]);
    			  dataMap.put("status", o[12]);
    			  dataMap.put("deptName", o[13]);

            	  // 根据指标的报表小数位置和单位生成目标值和完成值
    			  // 根据指标的报表小数位置和单位生成目标值和完成值
    			  if(null != o[4]) {
    				  Integer i = null;
    				  if(null != scaleReportMap.get(o[4])) {
    					 i = (Integer) scaleReportMap.get(o[4]);
    				  } else {
    					 i = 2; 
    				  }
    				  if(null != o[10]) {
    					  dataMap.put("targetValue" , convertValue(Double.parseDouble(String.valueOf(o[10])),i) + dataMap.get("units"));
    				  }
    				  if(null != o[11]) {
    					  dataMap.put("finishValue" , convertValue(Double.parseDouble(String.valueOf(o[11])),i) + dataMap.get("units"));
    				  }
    			  }
            	  detailList.add(dataMap);
              }
          }
        return detailList;
    }
    /**
     * 查询目标关联指标(时间控件最新值)
     * @param map
     * @param start
     * @param limit
     * @param queryName
     * @param sortColumn
     * @param dir
     * @return
     */
    public  List<Map<String, Object>> findLastSmKpiResults(Map<String, Object> map, int start, int limit, String queryName, String sortColumn, String dir) {
    	Map<String,Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap("KPI");
        List<Map<String,Object>> detailList = new ArrayList<Map<String,Object>>();
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        countBuf.append("select count(*) ");
        selectBuf.append("SELECT")
		        .append("    SM.ID,") //战略目标ID
		        .append("    SM.STRATEGY_MAP_NAME,") //战略目标名称
		        .append("    SMRESULT.ASSESSMENT_VALUE smassess,")//战略目标评估值
		        .append("    SMSTATUS.DICT_ENTRY_VALUE smstaus,")//战略目标亮灯状态
		        .append("    KPI.KPI_NAME,")//关联指标名称
		        .append("    TIME.TIME_PERIOD_FULL_NAME,")//时间维度
		        .append("    SK.EWEIGHT,")//权重
		        .append("    UNITDICT.DICT_ENTRY_NAME unit,")//单位
		        .append("    FREDICT.DICT_ENTRY_NAME fre,")//指标采集频率
		        .append("    RESULT.ASSESSMENT_VALUE kpiassess,")//指标评估值
		        .append("    RESULT.TARGET_VALUE,")// 指标结果值
		        .append("    RESULT.FINISH_VALUE,")// 指标完成值
		        .append("    STATUSDICT.DICT_ENTRY_VALUE kpistatus,")// 指标亮灯状态
		        .append("    ORG.ORG_NAME ");// 指标所属部门名称
		fromBuf.append(" FROM T_KPI_STRATEGY_MAP SM ")
		      .append("      LEFT OUTER JOIN T_KPI_SM_ASSESS_RESULT SMRESULT ")
		      .append("      ON SM.ID = SMRESULT.OBJECT_ID  ")
		      .append("      AND SM.latest_time_period_id = SMRESULT.time_period_id")
		      .append("      left outer join t_sys_dict_entry  smstatus")
		      .append("      on smresult.assessment_status = smstatus.id ")
		      .append("      LEFT outer JOIN (t_kpi_sm_rela_kpi sk ")
		      .append("                      inner join t_kpi_kpi kpi")
		      .append("                      on sk.KPI_ID = kpi.ID AND ")
		      .append("                         kpi.DELETE_STATUS = '1' AND")
		      .append("                         kpi.IS_ENABLED = '0yn_y' and")
		      .append("                         kpi.is_kpi_category='KPI'")
		      .append("                      left outer join (t_kpi_kpi_gather_result result")
		      .append("                                       INNER JOIN t_com_time_period time")
		      .append("                                       on time.id = result.TIME_PERIOD_ID")
		      .append("                                       )  on  kpi.id=result.kpi_id ")
		      .append("                                          and  kpi.latest_time_period_id=result.time_period_id ")
		      .append("                                          and  kpi.gather_frequence=time.etype")
		      .append("                 )  on sm.id = sk.STRATEGY_MAP_ID ")
		      .append("      left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id")
		      .append("      left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id")
		      .append("      left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
		      .append("      left outer join (t_kpi_kpi_rela_org_emp ko ")
		      .append("      inner join t_sys_organization  org on ko.org_id  = org.id )")
		      .append("      on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
		 wherebuf.append(" where 1 = 1 and sm.delete_status = 1 ");
		 // 战略目标模糊查询
		 if(StringUtils.isNotBlank(queryName)) {
			  wherebuf.append(" and sm.STRATEGY_MAP_NAME like '%").append(queryName).append("%'   "); 
		 }
		 if (StringUtils.isNotBlank(companyid)) {
		     wherebuf.append(" and sm.company_id=:companyid ");
		 }
         orderbuf.append("order by sm.ELEVEL,sm.id,sk.EWEIGHT DESC");
		 SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromBuf).append(wherebuf).toString());
		 if (StringUtils.isNotBlank(companyid)) {
		     countQuery.setParameter("companyid",companyid);
		 }
		 map.put("totalCount", countQuery.uniqueResult());
		 SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(selectBuf.append(fromBuf).append(wherebuf).append(orderbuf).toString());
		 if (StringUtils.isNotBlank(companyid)) {
		     sqlquery.setParameter("companyid",companyid);
		 }
         if(start >= 0) {
       	  sqlquery.setFirstResult(start);
             sqlquery.setMaxResults(limit); 
         }
         List<Object[]> list = sqlquery.list();
		 for(Object[] o: list) {
			  Map<String, Object> dataMap = new HashMap<String,Object>();
			  dataMap.put("id", String.valueOf(o[0]));
			  dataMap.put("smName", String.valueOf(o[1]));
			  if(null != o[2]) {
				  dataMap.put("smAssessValue",convertValue(Double.parseDouble(String.valueOf(o[2])),2));
			  } else {
				  dataMap.put("smAssessValue", o[2]);
			  }
			
			  dataMap.put("smStatus", o[3]);
			  dataMap.put("kpiName", o[4]);
			  dataMap.put("timePeriod",o[5]);
			  if(null != o[6]) {
				  dataMap.put("kpiWeight",convertValue(Double.parseDouble(String.valueOf(o[6])),2));
			  } else {
				  dataMap.put("kpiWeight", o[6]);
			  }
			  dataMap.put("units", o[7]);
			  dataMap.put("kpiFrequency", o[8]);
			  if(null != o[9]) {
				   dataMap.put("assessMentValue",convertValue(Double.parseDouble(String.valueOf(o[9])),2));
			  } else {
				  dataMap.put("assessMentValue", o[9]);
			  }			 
			  dataMap.put("targetValue", o[10]);
			  dataMap.put("finishValue", o[11]);
			  dataMap.put("status", o[12]);
			  dataMap.put("deptName", o[13]);
			  // 根据指标的报表小数位置和单位生成目标值和完成值
			  if(null != o[4]) {
				  Integer i = null;
				  if(null != scaleReportMap.get(o[4])) {
					 i = (Integer) scaleReportMap.get(o[4]);
				  } else {
					 i = Contents.DEFAULT_KPI_DOT_POSITION; 
				  }
				  if(null != o[10]) {
					  dataMap.put("targetValue" , convertValue(Double.parseDouble(String.valueOf(o[10])),i) + dataMap.get("units"));
				  }
				  if(null != o[11]) {
					  dataMap.put("finishValue" , convertValue(Double.parseDouble(String.valueOf(o[11])) ,i) + dataMap.get("units"));
				  }
			  }
			  detailList.add(dataMap);
		 }
		return detailList;
    }
    
    /** 查询目标ID和编码Map key:目标编码 ,value:目标ID
     *  @param companyId:公司ID
     *  @return 查询结果
     */
    public Map<String,String> findSmIdAndCodeMap(String companyId){
    	Map<String, String> codeMap = new HashMap<String, String>();
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.addOrder(Order.desc("level"));
        criteria.add(Restrictions.eq("company.id", companyId));
        List<StrategyMap> list = criteria.list();
        for (StrategyMap strategyMap : list) {
        	String code = strategyMap.getCode();
			if(null!=code&&StringUtils.isNotBlank(code)){
				codeMap.put(code, strategyMap.getId());
			}
		}
    	return codeMap;
    }
    /**查询目标名称和编码Map key:目标名称 ,value:目标编码
     * @param companyId 公司id
     * @return
     */
    public Map<String,String> findSmNameAndCodeMap(String companyId){
        Map<String, String> codeMap = new HashMap<String, String>();
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.addOrder(Order.desc("level"));
        criteria.add(Restrictions.eq("company.id", companyId));
        List<StrategyMap> list = criteria.list();
        for (StrategyMap strategyMap : list) {
            String name = strategyMap.getName();
            if(null!=name&&StringUtils.isNotBlank(name)){
                codeMap.put(name, strategyMap.getCode());
            }
        }
        return codeMap;
    }
    
    /**  查询目标ID和编码Map key:目标编码 ,value:目标ID
     *   companyId:公司ID
     *   @return 查询结果
     */
    public Map<String,String> findSmIdAndNameMap(String companyId){
    	Map<String, String> codeMap = new HashMap<String, String>();
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.setFetchMode("status", SELECT).setFetchMode("createBy", FetchMode.SELECT)
        .setFetchMode("lastModifyBy", FetchMode.SELECT).setFetchMode("relativeTo", FetchMode.SELECT)
        .setFetchMode("company", FetchMode.SELECT).setFetchMode("parent", FetchMode.SELECT);
        criteria.addOrder(Order.desc("level"));
        //criteria.setCacheable(true);
        criteria.add(Restrictions.eq("company.id", companyId));
        List<StrategyMap> list = criteria.list();
        for (StrategyMap strategyMap : list) {		
			codeMap.put(strategyMap.getId(), strategyMap.getName());
		}
    	return codeMap;
    }
    
    /** 查询目标所关联的告警方案ID map
     * @param companyId 公司ID
     * @return 查询结果
     */
    public Map<String,Object> findSmRelaAlarmMap(String companyId){
    	Map<String, Object> alarmMap = new HashMap<String, Object>();
    	Criteria dc = this.o_smRelaAlarmDAO.createCriteria();
        dc.createAlias("strategyMap", "sm");
        dc.add(Restrictions.eq("sm.company.id", companyId));
        dc.createAlias("rAlarmPlan", "rAlarmPlan");
        List<SmRelaAlarm> smRelaAlarms = dc.list();
        for (SmRelaAlarm smRelaAlarm : smRelaAlarms) {
        	StrategyMap strategyMap = smRelaAlarm.getStrategyMap();
        	if(!alarmMap.containsKey(strategyMap.getCode()))
        	{
        		alarmMap.put(strategyMap.getCode(), smRelaAlarm.getrAlarmPlan().getId());
        	}
		}
    	return alarmMap;
    }
    
    /**查询所有托管状态的目标对象
     * @return
     */
    public List<StrategyMap> findDetachedAllStrategyList(){
        List<StrategyMap> list = null;
        DetachedCriteria criteria = DetachedCriteria.forClass(StrategyMap.class);
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.addOrder(Order.desc("level"));
        list = criteria.getExecutableCriteria(o_kpiStrategyMapDAO.getSession()).list();
        return list;
    }
    
    /**
     * <pre>
     * 保存目标关联的部门和人员信息
     * </pre>
     * 
     * param sm 目标对象 param orgEmp 部门和人员信息 param type 部门类型 "B":所属部门 , "R":报告部门 ,
     * V:查看部门
     * 
     * @author 陈晓哲
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveSmRelaOrgEmp(StrategyMap sm, String orgEmp, String type) {
        if (StringUtils.isNotBlank(orgEmp)) {
            
            JSONArray jsonarr = JSONArray.fromObject(orgEmp);
            for (int i = 0; i < jsonarr.size(); i++) {
                SysEmployee emp = null;
                SysOrganization org = null;
                SmRelaOrgEmp smRelaOrgEmp = new SmRelaOrgEmp();
                JSONObject jsobj = jsonarr.getJSONObject(i);
                if(jsobj.containsKey("deptid")&&StringUtils.isNotBlank(jsobj.getString("deptid"))){
                    org = new SysOrganization(jsobj.getString("deptid"));
                }
                if(jsobj.containsKey("empid")&&StringUtils.isNotBlank(jsobj.getString("empid"))){
                    emp = new SysEmployee(jsobj.getString("empid"));
                }
                smRelaOrgEmp.setId(Identities.uuid());
                smRelaOrgEmp.setType(type);
                smRelaOrgEmp.setStrategyMap(sm);
                if (null != org) {
                    smRelaOrgEmp.setOrg(org);
                }
                if (null != emp) {
                    smRelaOrgEmp.setEmp(emp);
                }
                this.o_smRelaOrgEmpDAO.merge(smRelaOrgEmp);
            }
        }
    }
    
    /**
     * <pre>
     * 添加单个目标和指标关联关系
     * </pre>
     * 
     * 
     * @param kpiParam
     *            kpi指标参数
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void saveSingleKpiRela(String kpiParam,String smid){
    	StrategyMap sm = findById(smid);
    	SmRelaKpi smRelaKpi = new SmRelaKpi();
        smRelaKpi.setStrategyMap(sm);
        Set<SmRelaKpi> smRelaKpis = sm.getSmRelaKpi();
        for(SmRelaKpi sr: smRelaKpis) {
        	if(sr.getKpi().getId().equals(kpiParam)) {
        		o_smRelaKpiDAO.delete(sr);
        		break;
        	}        	
        }
        Kpi kpi = o_kpiDAO.get(kpiParam);
        smRelaKpi.setId(Identities.uuid());
        smRelaKpi.setKpi(kpi);
        smRelaKpi.setWeight(Double.parseDouble("1"));
        o_smRelaKpiDAO.merge(smRelaKpi);
    }
    /**
     * 更新目标关联的指标
     * @param jsobj 目标/指标
     */
    @Transactional
    public void mergeSmRelaKpi(JSONObject jsobj) {
    	 if (null != jsobj) {
             boolean flag = false;
             String smId = jsobj.getString("smId");
             StrategyMap sm =  o_kpiStrategyMapDAO.get(smId);
             Set<SmRelaKpi> smRelaKpi = null;

             JSONArray kpiids = jsobj.getJSONArray("kpiIds");
             if (null != kpiids && kpiids.size() > 0) {
                 smRelaKpi = sm.getSmRelaKpi();
                 for (Object kpiId : kpiids) {
                     if (null != smRelaKpi) {
                         for (SmRelaKpi k : smRelaKpi) {
                             try {
                                 if (kpiId.equals(k.getKpi().getId())) {
                                     flag = true;
                                 }
                             }
                             catch (ObjectNotFoundException e) {
                            	 logger.error("获得指标id异常,异常信息:["+e.toString()+"]");
                             }
                         }
                     }
                     if (!flag) {
                    	 SmRelaKpi sk = new SmRelaKpi();
                    	 sk.setId(Identities.uuid());
                    	 sk.setStrategyMap(sm);
                    	 sk.setKpi(o_kpiDAO.get((String) kpiId));
                         this.o_smRelaKpiDAO.merge(sk);
                     }

                 }
             }
             
         }
    }
    
    
    
    /**
     * <pre>
     * 保存目标和预警,告警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param alarmParam
     *            预警参数
     * @param currentSmId
     *            当前目标ID
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @Transactional
    @RecordLog(value="保存战略目标关联的预警,告警信息")
    public void mergeStrategyRelaAlarm(String jsonString, String currentSmId) throws ParseException {
        if (StringUtils.isNotBlank(currentSmId) && StringUtils.isNotBlank(jsonString)) {
            JSONArray jsonArray = JSONArray.fromObject(jsonString);
            StrategyMap sm = findById(currentSmId);// 根据目标id查询目标对象
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            /*
             * 删除目标关联的预警告警信息
             */
            Set<SmRelaAlarm> smRelaAlarms = sm.getSmRelaAlarms();
            for (SmRelaAlarm smAlarm : smRelaAlarms) {
                o_smRelaAlarmDAO.delete(smAlarm);
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
            	AlarmPlan alarmPlan = null;
                AlarmPlan warningPlan = null;
                SmRelaAlarm smRelaAlarm = null;
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String alarmId = jsonObject.getString("alarm");
                String warningId = jsonObject.getString("warning");
                String date = jsonObject.getString("date");
                /*
                 * 保存告警\预警信息
                 */
                if (StringUtils.isNotBlank(alarmId)) {
                    alarmPlan = alarmMap.get(alarmId);
                }
                if (StringUtils.isNotBlank(warningId)) {
                    warningPlan = warningMap.get(warningId);
                }
                smRelaAlarm = new SmRelaAlarm();
                smRelaAlarm.setId(Identities.uuid());
                smRelaAlarm.setStrategyMap(sm);
                smRelaAlarm.setrAlarmPlan(alarmPlan);
                smRelaAlarm.setFcAlarmPlan(warningPlan);
                smRelaAlarm.setStartDate(sdf.parse(date));
                o_smRelaAlarmDAO.merge(smRelaAlarm);
            }
        }
    }

    /**
     * <pre>
     * 保存目标和指标关联关系
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiParam
     *            kpi指标参数
     * @since fhd　Ver 1.1
     */
    @Transactional
    @RecordLog(value="保存战略目标和指标关联关系")
    public void mergeStrategyRelaKpi(String kpiParam, String currentSmId) {
        if (StringUtils.isNotBlank(currentSmId)) {
            String kpiId = "";
            String[] kpiStr = null;
            Kpi kpi = null;// 指标临时对象
            SmRelaKpi smRelaKpi = null;// 目标与指标临时关联对象
            StrategyMap sm = findById(currentSmId);// 根据目标id查询目标对象
            /*
             * 删除目标关联的指标
             */
            Set<SmRelaKpi> smRelaKpis = sm.getSmRelaKpi();
            for (SmRelaKpi tmp : smRelaKpis) {
                o_smRelaKpiDAO.delete(tmp);
            }

            String[] params = StringUtils.split(kpiParam, ";");
            if(null!=params){
            	List<String> kpiIdList = new ArrayList<String>();
            	Map<String,Kpi> kpiMap = new HashMap<String, Kpi>();
            	for (String para : params) {
            		kpiStr = StringUtils.split(para, ",");
                    kpiId = kpiStr[0];
                    kpiIdList.add(kpiId);
                   
            	}
            	List<Kpi> kpiList = o_kpiBO.findKpiListByIds(kpiIdList);
            	for (Kpi kpiObj : kpiList) {
            		kpiMap.put(kpiObj.getId(),kpiObj);
				}
            	for (String para : params) {
                	String kpiWeight = "";
                    if (StringUtils.isNotBlank(para)) {
                        kpiStr = StringUtils.split(para, ",");
                        if(null!=kpiStr){
                        	kpiId = kpiStr[0];
                        	if(kpiStr.length>1){
                            	kpiWeight = kpiStr[1];
                            }
                        }
                        
                        /*
                         * 创建目标和指标关联对象
                         */
                        smRelaKpi = new SmRelaKpi();
                        smRelaKpi.setStrategyMap(sm);
                        kpi = kpiMap.get(kpiId);
                        smRelaKpi.setId(Identities.uuid());
                        smRelaKpi.setKpi(kpi);
                        if(StringUtils.isNotBlank(kpiWeight)){
                        	smRelaKpi.setWeight(Double.parseDouble(kpiWeight));
                        }
                        o_smRelaKpiDAO.merge(smRelaKpi);

                    }
                }
            }
            
        }
    }

    /**修改战略目标对象
     * @param strategyMap 战略目标实体
     */
    @Transactional
    public void mergeStrategyMap(StrategyMap strategyMap) {
        o_kpiStrategyMapDAO.merge(strategyMap);
    }

    /**
     * <pre>
     * 添加策略目标对象
     * </pre>
     * 
     * @author 陈晓哲
     * @param strategyMapForm
     *            目标form
     * @since fhd　Ver 1.1
     */
    @Transactional
    @RecordLog(value="保存战略目标对象")
    public String mergeStrategyMap(StrategyMapForm strategyMapForm) {
        int esort = 0;
        boolean addFlag = false;
        // 维度,主题临时对象
        DictEntry entryTemp = null;
        StrategyMap strategyMap = null;
        StrategyMap parentStrategyMap = null;
        // 获得父目标对象
        String parentId = strategyMapForm.getParentId();
        if (!"sm_root".equals(parentId)) {
            parentStrategyMap = this.findById(parentId);
        }
        // 获得公司机构ID
        String companyid = UserContext.getUser().getCompanyid();
        // 获得机构对象
        SysOrganization company = o_organizationBO.get(companyid);
        String strategyMapId = Identities.uuid();
        String ynid = strategyMapForm.getEstatus();
        String isfocus = strategyMapForm.getIsfocustr();
        String currentSmid = StringUtils.defaultIfEmpty(strategyMapForm.getCurrentSmId(), "");
        // 主维度信息
        String mainDim = StringUtils.defaultIfEmpty(strategyMapForm.getMainDim(), "");
        // 战略主题
        String mainTheme = StringUtils.defaultIfEmpty(strategyMapForm.getMainTheme(), "");
        // 辅助维度
        String otherDim = StringUtils.defaultIfEmpty(strategyMapForm.getOtherDim(), "");
        // 辅助战略主题
        String otherTheme = StringUtils.defaultIfEmpty(strategyMapForm.getOtherTheme(), "");
        // 图表类型
        String chartTypeStr = strategyMapForm.getChartTypeStr();

        Integer level = 0;
        String idseq = ".";
        if (null != parentStrategyMap) {
            idseq = parentStrategyMap.getIdSeq();
            level = parentStrategyMap.getLevel();
        }

        if (!currentSmid.equals("") && !currentSmid.equals(Contents.ID_UNDEFINED)) {// update
            strategyMap = this.findById(currentSmid);
            if (null != strategyMap) {
                /* 设置esort字段 */
                esort = strategyMap.getSort();
                // 删除目标关联的部门和人员信息
                this.removeSmRelaOrgEmp(strategyMap, "ALL");
                // 删除维度信息
                Set<SmRelaDim> smRelaDims = strategyMap.getSmRelaDims();
                for (SmRelaDim smRelaDim : smRelaDims) {
                    o_strategyMapRelaDimDAO.delete(smRelaDim);
                }
                // 删除主题信息
                Set<SmRelaTheme> smRelaThemes = strategyMap.getSmRelaThemes();
                for (SmRelaTheme theme : smRelaThemes) {
                    o_strategyMapRelaThemeDAO.delete(theme);
                }
                strategyMapId = strategyMap.getId();
                boolean isleaf = strategyMap.getIsLeaf();
                TimePeriod lastTimePeriod = strategyMap.getTimePeriod();
                BeanUtils.copyProperties(strategyMapForm, strategyMap);
                strategyMap.setId(strategyMapId);
                strategyMap.setIsLeaf(isleaf);
                strategyMap.setIdSeq(idseq + strategyMapId + ".");
                strategyMap.setTimePeriod(lastTimePeriod);
            }

        }
        else {// add
              // 构建目标对象
              // 查询父节点下有多少个子节点
            addFlag = true;
            Long count = (Long) this.o_kpiStrategyMapDAO.createCriteria()
                    .add(Restrictions.and(Restrictions.eq("parent", parentStrategyMap), Restrictions.ne("deleteStatus", false)))
                    .setProjection(Projections.rowCount()).uniqueResult();
            esort = count.intValue() + 1;
            strategyMap = new StrategyMap();
            BeanUtils.copyProperties(strategyMapForm, strategyMap);
            strategyMap.setId(strategyMapId);
            strategyMap.setIdSeq(idseq + strategyMapId + ".");
            strategyMap.setIsLeaf(true);
        }
        if (null != strategyMap) {
            strategyMap.setSort(esort);
            strategyMap.setParent(parentStrategyMap);
            strategyMap.setCompany(company);
            strategyMap.setDeleteStatus(true);
            strategyMap.setLevel((null == level ? 0 : level) + 1);
            strategyMap.setStatus(o_dictEntryBO.findDictEntryById(ynid));
            strategyMap.setIsFocus(isfocus);

            // 图表类型
            if (StringUtils.isNotBlank(chartTypeStr)) {
                strategyMap.setChartType(chartTypeStr);
            }

            if (null != parentStrategyMap) {
                parentStrategyMap.setIsLeaf(false);
            }

            // 保存目标对象
            o_kpiStrategyMapDAO.merge(strategyMap);

            // 保存所属部门信息
            String ownDept = strategyMapForm.getOwnDept();
            this.saveSmRelaOrgEmp(strategyMap, ownDept, "B");

            // 保存报告部门信息
            String reportDept = strategyMapForm.getReportDept();
            this.saveSmRelaOrgEmp(strategyMap, reportDept, "R");
            // 保存查看部门信息
            String viewDept = strategyMapForm.getViewDept();
            this.saveSmRelaOrgEmp(strategyMap, viewDept, "V");

            // 保存主维度信息
            if (!mainDim.equals("")) {
                SmRelaDim mainSmRelaDim = new SmRelaDim();
                entryTemp = o_dictEntryBO.findDictEntryById(mainDim);
                mainSmRelaDim.setId(Identities.uuid());
                mainSmRelaDim.setSmDim(entryTemp);
                mainSmRelaDim.setStrategyMap(strategyMap);
                mainSmRelaDim.setType("M");
                o_strategyMapRelaDimDAO.merge(mainSmRelaDim);
            }
            // 保存战略主题信息
            if (!mainTheme.equals("")) {
                SmRelaTheme mainTheamSmRela = new SmRelaTheme();
                entryTemp = o_dictEntryBO.findDictEntryById(mainTheme);
                mainTheamSmRela.setId(Identities.uuid());
                mainTheamSmRela.setTheme(entryTemp);
                mainTheamSmRela.setStrategyMap(strategyMap);
                mainTheamSmRela.setType("M");
                o_strategyMapRelaThemeDAO.merge(mainTheamSmRela);
            }

            // 保存辅助维度信息
            if (!otherDim.equals("")) {
                String[] otherDims = otherDim.split(",");
                for (String tmpstr : otherDims) {
                    entryTemp = o_dictEntryBO.findDictEntryById(tmpstr);
                    SmRelaDim otherSmRelaDim = new SmRelaDim();
                    otherSmRelaDim.setId(Identities.uuid());
                    otherSmRelaDim.setSmDim(entryTemp);
                    otherSmRelaDim.setStrategyMap(strategyMap);
                    otherSmRelaDim.setType("A");
                    o_strategyMapRelaDimDAO.merge(otherSmRelaDim);
                }
            }
            // 保存辅助主题信息
            if (!otherTheme.equals("")) {
                String[] otherThemes = otherTheme.split(",");
                for (String tmpstr : otherThemes) {
                    entryTemp = o_dictEntryBO.findDictEntryById(tmpstr);
                    SmRelaTheme otherSmRelaTheme = new SmRelaTheme();
                    otherSmRelaTheme.setId(Identities.uuid());
                    otherSmRelaTheme.setTheme(entryTemp);
                    otherSmRelaTheme.setStrategyMap(strategyMap);
                    otherSmRelaTheme.setType("A");
                    o_strategyMapRelaThemeDAO.merge(otherSmRelaTheme);
                }
            }
        }
        if (addFlag) {
            String year = DateUtils.getYear(new Date());
            List<TimePeriod> timeList = o_relaAssessResultBO.findTimePeriodByMonthType(year);
            //向评分结果表中插入默认数据;
            JSONArray smJsonArray = new JSONArray();
            JSONObject smJsonObject = new JSONObject();
            smJsonObject.put("objectId", strategyMap.getId());
            smJsonObject.put("objectName", strategyMap.getName());
            smJsonObject.put("companyid", strategyMap.getCompany().getId());
            smJsonArray.add(smJsonObject);
            o_relaAssessResultBO.saveBathResultData(smJsonArray, timeList, "str",year);
        }
        return strategyMapId;
    }

    /**
     * 删除目标下的关联指标关系
     * @param kpiItems kpiId集合
     * @param smId 战略目标Id
     */
    @Transactional
    public void removeSmRelaKpiByIds(String kpiItems,String smId){
    	JSONArray jsonArray = JSONArray.fromObject(kpiItems);
    	String sql = "delete from t_kpi_sm_rela_kpi where kpi_id in (:kpiIdList) and strategy_map_id = :smId";
    	o_smRelaKpiDAO.createSQLQuery(sql).setParameterList("kpiIdList", jsonArray, new StringType()).setParameter("smId", smId, new StringType()).executeUpdate();
    }

    /**
     * <pre>
     * 删除和目标所关联的部门和人员信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param sm
     *            目标对象
     * @param type
     *            部门类型 "B":所属部门 , "R":报告部门 , V:查看部门 , "ALL":所有部门
     * @since fhd　Ver 1.1
     */
    @Transactional
    public void removeSmRelaOrgEmp(StrategyMap sm, String type) {
        if (null != sm) {
            Set<SmRelaOrgEmp> smRelaOrgEmpSet = sm.getSmRelaOrgEmps();
            for (SmRelaOrgEmp smRelaOrgEmp : smRelaOrgEmpSet) {
                if ("ALL".equals(type)) {
                    o_smRelaOrgEmpDAO.delete(smRelaOrgEmp);
                }
                else {
                    if (type.equals(smRelaOrgEmp.getType())) {
                        o_smRelaOrgEmpDAO.delete(smRelaOrgEmp);
                    }
                }
            }
        }
    }

    /**
     * <pre>
     * 根据当前目标节点判断它下面是否有子目标,首否可以删除
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            目标ID
     * @return 删除成功标志
     * @since fhd　Ver 1.1
     */
    @Transactional
    public boolean removeStrategyMap(String id) {
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria();
        criteria.add(Restrictions.like("idSeq", "%." + id + ".%"));
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.setProjection(Projections.count("id"));
        // 判断当前节点下是否存在子节点
        long count = (Long) criteria.uniqueResult();
        if (count >= 2) {
            return false;
        }

        // 逻辑删除
        StrategyMap strategyMap = o_kpiStrategyMapDAO.get(id);
        StrategyMap psm = strategyMap.getParent();
        /* 当父节点没有子节点时,将父节点变为叶子节点 */
        if (null != psm) {
            count = (Long) o_kpiStrategyMapDAO
                    .createCriteria()
                    .add(Restrictions.and(Restrictions.and(Restrictions.eq("parent", psm), Restrictions.ne("id", id)),
                            Restrictions.ne("deleteStatus", false))).setProjection(Projections.rowCount()).uniqueResult();
            if (count == 0) {
                psm.setIsLeaf(true);
            }
        }
        strategyMap.setDeleteStatus(false);
        o_kpiStrategyMapDAO.merge(strategyMap);
        return true;
    }

    /**
     * <pre>
     * 根据目标id删除关联信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            目标ID
     * @return 删除是否成功标志
     * @since fhd　Ver 1.1
     */
    @Transactional
    public boolean removeStrategyMapBatch(String id) {
        Criteria criteria = o_kpiStrategyMapDAO.createCriteria().add(Restrictions.like("idSeq", "%." + id + ".%"));
        // 查询出当前目标和子目标节点
        List<StrategyMap> list = criteria.list();
        // 遍历每一个目标对象
        for (StrategyMap sm : list) {
            // 设置每一个目标对象删除状态
            sm.setDeleteStatus(false);
            o_kpiStrategyMapDAO.merge(sm);
        }
        return true;
    }
    
	/**
     * <pre>
     * 添加策略目标对象
     * </pre>
     * 
     * @author 王鑫
     * @param strategyMapForm
     *            目标form
     * @since fhd　Ver 1.1
     */
    @Transactional
    @RecordLog(value="保存战略目标对象")
    public String mergeStrategyMapAndUpdateNode(StrategyMapForm strategyMapForm) {
        int esort = 0;
        boolean addFlag = false;
        // 维度,主题临时对象
        DictEntry entryTemp = null;
        StrategyMap strategyMap = null;
        StrategyMap parentStrategyMap = null;
        // 获得父目标对象
        String parentId = strategyMapForm.getParentId();
        if (!"sm_root".equals(parentId)) {
            parentStrategyMap = this.findById(parentId);
        }
        // 获得公司机构ID
        String companyid = UserContext.getUser().getCompanyid();
        // 获得机构对象
        SysOrganization company = o_organizationBO.get(companyid);
        String strategyMapId = Identities.uuid();
        String ynid = strategyMapForm.getEstatus();
        String isfocus = strategyMapForm.getIsfocustr();
        String currentSmid = StringUtils.defaultIfEmpty(strategyMapForm.getCurrentSmId(), "");
        // 主维度信息
        String mainDim = StringUtils.defaultIfEmpty(strategyMapForm.getMainDim(), "");
        // 战略主题
        String mainTheme = StringUtils.defaultIfEmpty(strategyMapForm.getMainTheme(), "");
        // 辅助维度
        String otherDim = StringUtils.defaultIfEmpty(strategyMapForm.getOtherDim(), "");
        // 辅助战略主题
        String otherTheme = StringUtils.defaultIfEmpty(strategyMapForm.getOtherTheme(), "");
        // 图表类型
        String chartTypeStr = strategyMapForm.getChartTypeStr();

        Integer level = 0;
        String idseq = ".";
        if (null != parentStrategyMap) {
            idseq = parentStrategyMap.getIdSeq();
            level = parentStrategyMap.getLevel();
        }

        if (!currentSmid.equals("") && !currentSmid.equals(Contents.ID_UNDEFINED)) {// update
            strategyMap = this.findById(currentSmid);
            // 如果上级目标有修改 则更新下级目标的isSeq以及level
            if((null != parentStrategyMap && null!=strategyMap && null!= strategyMap.getParent() && !strategyMap.getParent().getId().equalsIgnoreCase(parentId)) 
            		|| (null != strategyMap && null!= strategyMap.getParent() && parentStrategyMap == null)
            		||  (null != strategyMap && null == strategyMap.getParent() && parentStrategyMap != null))  {
               	if(!strategyMap.getIsLeaf()) {
                	String parentIdSeq = ".";
                	if(!"sm_root".equals(parentId)) {
                		parentIdSeq = this.findById(parentId).getIdSeq();
                	}
                	String oldSeq = ".";
                	if(null != strategyMap.getParent()) {
                		oldSeq = strategyMap.getParent().getIdSeq();
                	}
               		Integer i = 0;
               		if(null != strategyMap.getParent()) {
               			i = strategyMap.getParent().getLevel();
               		}                			               		
               		this.mergeIdseqAndLevel(strategyMap,parentIdSeq,oldSeq,level - i);

               	}
           	    if(null!=strategyMap.getParent()) {
        		 List<StrategyMap> list = this.findSmByParentId(strategyMap.getParent().getId());
        		 if(null == list || list.size() <= 1) {
        			 strategyMap.getParent().setIsLeaf(true);
        			 this.mergeStrategyMap(strategyMap.getParent());
        		 }
        	    }
            }
            if (null != strategyMap) {
                /* 设置esort字段 */
                esort = strategyMap.getSort();
                // 删除目标关联的部门和人员信息
                this.removeSmRelaOrgEmp(strategyMap, "ALL");
                // 删除维度信息
                Set<SmRelaDim> smRelaDims = strategyMap.getSmRelaDims();
                for (SmRelaDim smRelaDim : smRelaDims) {
                    o_strategyMapRelaDimDAO.delete(smRelaDim);
                }
                // 删除主题信息
                Set<SmRelaTheme> smRelaThemes = strategyMap.getSmRelaThemes();
                for (SmRelaTheme theme : smRelaThemes) {
                    o_strategyMapRelaThemeDAO.delete(theme);
                }
                strategyMapId = strategyMap.getId();
                boolean isleaf = strategyMap.getIsLeaf();
                TimePeriod lastTimePeriod = strategyMap.getTimePeriod();
                BeanUtils.copyProperties(strategyMapForm, strategyMap);
                strategyMap.setId(strategyMapId);
                strategyMap.setIsLeaf(isleaf);
                strategyMap.setIdSeq(idseq + strategyMapId + ".");
                strategyMap.setTimePeriod(lastTimePeriod);
            }

        }
        else {// add
              // 构建目标对象
              // 查询父节点下有多少个子节点
            addFlag = true;
            Long count = (Long) this.o_kpiStrategyMapDAO.createCriteria()
                    .add(Restrictions.and(Restrictions.eq("parent", parentStrategyMap), Restrictions.ne("deleteStatus", false)))
                    .setProjection(Projections.rowCount()).uniqueResult();
            esort = count.intValue() + 1;
            strategyMap = new StrategyMap();
            BeanUtils.copyProperties(strategyMapForm, strategyMap);
            strategyMap.setId(strategyMapId);
            strategyMap.setIdSeq(idseq + strategyMapId + ".");
            strategyMap.setIsLeaf(true);
        }
        if (null != strategyMap) {
            strategyMap.setSort(esort);
            strategyMap.setParent(parentStrategyMap);
            strategyMap.setCompany(company);
            strategyMap.setDeleteStatus(true);
            strategyMap.setLevel((null == level ? 0 : level) + 1);
            strategyMap.setStatus(o_dictEntryBO.findDictEntryById(ynid));
            strategyMap.setIsFocus(isfocus);

            // 图表类型
            if (StringUtils.isNotBlank(chartTypeStr)) {
                strategyMap.setChartType(chartTypeStr);
            }

            if (null != parentStrategyMap) {
                parentStrategyMap.setIsLeaf(false);
            }

            // 保存目标对象
            o_kpiStrategyMapDAO.merge(strategyMap);

            // 保存所属部门信息
            String ownDept = strategyMapForm.getOwnDept();
            this.saveSmRelaOrgEmp(strategyMap, ownDept, "B");

            // 保存报告部门信息
            String reportDept = strategyMapForm.getReportDept();
            this.saveSmRelaOrgEmp(strategyMap, reportDept, "R");
            // 保存查看部门信息
            String viewDept = strategyMapForm.getViewDept();
            this.saveSmRelaOrgEmp(strategyMap, viewDept, "V");

            // 保存主维度信息
            if (!mainDim.equals("")) {
                SmRelaDim mainSmRelaDim = new SmRelaDim();
                entryTemp = o_dictEntryBO.findDictEntryById(mainDim);
                mainSmRelaDim.setId(Identities.uuid());
                mainSmRelaDim.setSmDim(entryTemp);
                mainSmRelaDim.setStrategyMap(strategyMap);
                mainSmRelaDim.setType("M");
                o_strategyMapRelaDimDAO.merge(mainSmRelaDim);
            }
            // 保存战略主题信息
            if (!mainTheme.equals("")) {
                SmRelaTheme mainTheamSmRela = new SmRelaTheme();
                entryTemp = o_dictEntryBO.findDictEntryById(mainTheme);
                mainTheamSmRela.setId(Identities.uuid());
                mainTheamSmRela.setTheme(entryTemp);
                mainTheamSmRela.setStrategyMap(strategyMap);
                mainTheamSmRela.setType("M");
                o_strategyMapRelaThemeDAO.merge(mainTheamSmRela);
            }

            // 保存辅助维度信息
            if (!otherDim.equals("")) {
                String[] otherDims = otherDim.split(",");
                for (String tmpstr : otherDims) {
                    entryTemp = o_dictEntryBO.findDictEntryById(tmpstr);
                    SmRelaDim otherSmRelaDim = new SmRelaDim();
                    otherSmRelaDim.setId(Identities.uuid());
                    otherSmRelaDim.setSmDim(entryTemp);
                    otherSmRelaDim.setStrategyMap(strategyMap);
                    otherSmRelaDim.setType("A");
                    o_strategyMapRelaDimDAO.merge(otherSmRelaDim);
                }
            }
            // 保存辅助主题信息
            if (!otherTheme.equals("")) {
                String[] otherThemes = otherTheme.split(",");
                for (String tmpstr : otherThemes) {
                    entryTemp = o_dictEntryBO.findDictEntryById(tmpstr);
                    SmRelaTheme otherSmRelaTheme = new SmRelaTheme();
                    otherSmRelaTheme.setId(Identities.uuid());
                    otherSmRelaTheme.setTheme(entryTemp);
                    otherSmRelaTheme.setStrategyMap(strategyMap);
                    otherSmRelaTheme.setType("A");
                    o_strategyMapRelaThemeDAO.merge(otherSmRelaTheme);
                }
            }
        }
        if (addFlag) {
            String year = DateUtils.getYear(new Date());
            List<TimePeriod> timeList = o_relaAssessResultBO.findTimePeriodByMonthType(year);
            //向评分结果表中插入默认数据;
            JSONArray smJsonArray = new JSONArray();
            JSONObject smJsonObject = new JSONObject();
            smJsonObject.put("objectId", strategyMap.getId());
            smJsonObject.put("objectName", strategyMap.getName());
            smJsonObject.put("companyid", strategyMap.getCompany().getId());
            smJsonArray.add(smJsonObject);
            o_relaAssessResultBO.saveBathResultData(smJsonArray, timeList, "str",year);
        } 
        return strategyMapId;
    }
    
    /**递归修改id目标对象的idseq
     * @param sm 目标对象
     * @param newIdSeq 新的idseq
     * @param oldIdSeq 原来的idseq
     * @param i level层级
     */
    @Transactional
    public void mergeIdseqAndLevel(StrategyMap sm,String newIdSeq,String oldIdSeq,Integer i) {
           List<StrategyMap> list = this.findSmByParentId(sm.getId());
           for(StrategyMap m : list) {
        	   if(".".equals(oldIdSeq) && !".".equals(newIdSeq)) {
        		   m.setIdSeq(newIdSeq.substring(0,newIdSeq.length()-1) + m.getIdSeq());
        	   }
        	   else {
        		   m.setIdSeq(m.getIdSeq().replace(oldIdSeq, newIdSeq));
        	   }
        	   m.setLevel(m.getLevel() + i);
        	   this.mergeStrategyMap(m);
        	   if(!m.getIsLeaf()) {
        		   this.mergeIdseqAndLevel(m,newIdSeq,oldIdSeq,i);
        	   }
           }
    }
    
    /**
     * 根据传入的位数格式化数字
     * @param valueObj
     * @param m 截取的字符串位数
     * @return 转换成的字符串
     */
	private String convertValue(Double valueObj,Integer m){
		String valueStr = "";
		if(null == valueObj){
			valueStr = "";
    	}else{
    		BigDecimal value = new BigDecimal(valueObj);
        	int valueInt = value.intValue();
        	float valueFloat = value.floatValue();
        	if(valueFloat>valueInt){
        		StringBuffer sb = new StringBuffer("0.");
        		for(int i = 1;i<m;i++){
        			sb.append("0");
        		}
        		valueStr = new DecimalFormat(sb.toString()).format(value);
        	}else{
        		valueStr = new DecimalFormat("0").format(value);
        	}
    	}
		return valueStr;
	}

}