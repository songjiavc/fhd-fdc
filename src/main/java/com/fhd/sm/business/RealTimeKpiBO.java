package com.fhd.sm.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.kpi.GatherDataDAO;
import com.fhd.dao.kpi.RealTimeKpiDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.kpi.GatherData;
import com.fhd.entity.kpi.RealTimeKpi;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.ra.web.form.risk.RiskEventForm;
import com.fhd.sm.web.form.RealTimeKpiForm;
import com.fhd.sys.business.dic.DictBO;

/**
 * 实时指标BO
 *
 */
@Service
@SuppressWarnings("unchecked")
public class RealTimeKpiBO {

    @Autowired
    private GatherDataDAO o_gatherDataDAO;

    @Autowired
    private RealTimeKpiDAO o_realTimeKpiDAO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;

    @Autowired
    private DictBO o_DictBO;

    /**根据编号查询指标
     * @param code 实时指标编号
     * @param companyId 公司ID
     * @param kpiId 实时指标id
     * @return
     */
    public RealTimeKpi findRealTimeKpiByCode(String code, String companyId, String kpiId) {
        Criteria criteria = o_realTimeKpiDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("company.id", companyId));
        criteria.add(Restrictions.eq("code", code));
        if (StringUtils.isNotBlank(kpiId)) {
            criteria.add(Restrictions.ne("id", kpiId));
        }
        List<RealTimeKpi> list = criteria.list();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**根据名称查询指标
     * @param name 实时指标名称
     * @param companyId 公司ID
     * @param kpiId 实时指标id
     * @return
     */
    public RealTimeKpi findRealTimeKpiByName(String name, String companyId, String kpiId) {
        Criteria criteria = o_realTimeKpiDAO.createCriteria();
        criteria.add(Restrictions.eq("deleteStatus", true));
        if (StringUtils.isNotBlank(companyId)) {
            criteria.add(Restrictions.eq("company.id", companyId));
        }
        criteria.add(Restrictions.eq("name", name));
        if (StringUtils.isNotBlank(kpiId)) {
            criteria.add(Restrictions.ne("id", kpiId));
        }
        List<RealTimeKpi> list = criteria.list();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**查询实时指标图形
     * @param kpiId 实时指标id
     * @param year 年信息
     * @return
     */
    public List<GatherData> findRealTimeKpiHistoryChart(String kpiId, String year) {
        Criteria criteria = o_gatherDataDAO.createCriteria();
        criteria.add(Restrictions.eq("realTimeKpi.id", kpiId));
        criteria.addOrder(Order.asc("times"));
        return criteria.list();
    }

    /**
     * @param kpiId 实时指标id
     * @return
     */
    public DictEntry findMaxTimeGatherStatusByKpiId(String kpiId) {
        String selectHql = " from  GatherData g where  g.times=(select max(ig.times) from GatherData ig where ig.realTimeKpi.id=:kpiId )  and  g.realTimeKpi.id=:kpiId";
        Query query = o_gatherDataDAO.createQuery(selectHql);
        query.setString("kpiId", kpiId);
        List<GatherData> gatherList = query.list();
        if (gatherList.size() > 0) {
            if (null != gatherList.get(0).getStatus()) {
                return gatherList.get(0).getStatus();
            }
        }
        return null;
    }

    /**根据指标id查询指标对象
     * @param kpiId 实时指标id
     * @return
     */
    public RealTimeKpi findRealTimeKpiById(String kpiId) {
        return o_realTimeKpiDAO.get(kpiId);
    }

    /**查询实时指标列表
     * @param page 分页对象
     * @param paraMap 参数map
     * @return
     */
    public Page<RealTimeKpi> findRealTimeBySome(Page<RealTimeKpi> page, Map<String, Object> paraMap) {
        String sortstr = "name";
        String query = String.valueOf(paraMap.get("query"));
        String sort = String.valueOf(paraMap.get("sort"));
        String dir = String.valueOf(paraMap.get("dir"));
        String companyId = String.valueOf(paraMap.get("companyId"));
        DetachedCriteria dc = DetachedCriteria.forClass(RealTimeKpi.class).add(Restrictions.eq("deleteStatus", true));
        //dc.add(Restrictions.eq("company.id", companyId));
        if (StringUtils.isNotBlank(query)) {
            dc.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.equals("name", sort)) {
            sortstr = "name";
        }
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }

        return o_realTimeKpiDAO.findPage(dc, page, false);
    }

    /**根据实时指标id查询所有历史数据
     * @param page  分页对象
     * @param paraMap 参数map
     * @return
     */
    public Page<GatherData> findRealTimeHistoryBySome(Page<GatherData> page, Map<String, Object> paraMap) {
        String sortstr = "times";
        String query = String.valueOf(paraMap.get("query"));
        String sort = String.valueOf(paraMap.get("sort"));
        String dir = String.valueOf(paraMap.get("dir"));
        String kpiId = String.valueOf(paraMap.get("kpiId"));
        DetachedCriteria dc = DetachedCriteria.forClass(GatherData.class);
        dc.add(Restrictions.eq("realTimeKpi.id", kpiId));
        if (StringUtils.isNotBlank(query)) {
            dc.add(Property.forName("desc").like(query, MatchMode.ANYWHERE));
        }
        if (StringUtils.equals("value", sort)) {
            sortstr = "value";
        }
        else if (StringUtils.equals("date", sort)) {
            sortstr = "times";
        }
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sortstr));
        }
        else {
            dc.addOrder(Order.desc(sortstr));
        }

        return o_gatherDataDAO.findPage(dc, page, false);
    }

    /**查询告警方案下拉框值
     * @param type 告警类型
     * @return
     */
    public List<AlarmPlan> findAlarmComboxValue(String type) {
        return o_alarmPlanBO.findAlarmPlanByType(type);
    }

    /**添加和修改实时指标历史数据
     * @param form 历史数据form
     * @throws ParseException 
     */
    @Transactional
    public void mergeRealTimeKpiHistoryData(List<RiskEventForm> formList, String kpiId) throws ParseException {
        for (RiskEventForm form : formList) {
            String gatherId = form.getId();
            String desc = form.getDesc();
            Double value = Double.valueOf(form.getValue());
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date times = sf.parse(form.getDate());
            GatherData gatherData = null;
            RealTimeKpi realTimeKpi = null;
            DictEntry statusDict = null;
            if (StringUtils.isNotBlank(kpiId)) {
                realTimeKpi = o_realTimeKpiDAO.get(kpiId);
            }
            if (StringUtils.isNotBlank(gatherId)) {
                gatherData = o_gatherDataDAO.get(gatherId);
            }
            else {
                gatherData = new GatherData();
                gatherData.setId(Identities.uuid());
            }
            gatherData.setRealTimeKpi(realTimeKpi);
            if (null != realTimeKpi) {
                AlarmPlan alarmPlan = realTimeKpi.getAlarmPlan();

                if (null != alarmPlan) {
                    statusDict = o_alarmPlanBO.findKpiAlarmStatusByValues(alarmPlan, value);
                }
            }
            gatherData.setStatus(statusDict);
            gatherData.setValue(value);
            gatherData.setDesc(desc);
            gatherData.setTimes(times);
            o_gatherDataDAO.merge(gatherData);
            //更新实时指标最后采集状态
            mergeRealKpiStatus(kpiId);
        }
    }

    /**更新实时指标对象
     * @param realTimeKpi 实时指标对象
     */
    @Transactional
    public void mergeRealTimeKpi(RealTimeKpi realTimeKpi) {
        o_realTimeKpiDAO.merge(realTimeKpi);
    }

    /**添加和修改实时指标信息
     * @param realTimeKpiForm 实时指标信息form
     */
    @Transactional
    public void mergeRealTimeKpi(RealTimeKpiForm realTimeKpiForm, String companyId) {
        String kpiId = realTimeKpiForm.getId();
        String code = realTimeKpiForm.getCode();
        String name = realTimeKpiForm.getName();
        String desc = realTimeKpiForm.getDesc();
        String unit = realTimeKpiForm.getUnit();
        String alarmId = realTimeKpiForm.getAlarmId();
        AlarmPlan alarmPlan = null;
        RealTimeKpi kpi = null;
        if (StringUtils.isNotBlank(alarmId)) {
            alarmPlan = o_alarmPlanBO.findAlarmPlanById(alarmId);
        }
        if (StringUtils.isNotBlank(kpiId)) {
            //修改指标信息
            kpi = o_realTimeKpiDAO.get(kpiId);
        }
        else {
            //添加
            String uuid = Identities.uuid();
            kpi = new RealTimeKpi();
            kpi.setId(uuid);
            SysOrganization org = new SysOrganization(companyId);
            kpi.setCompany(org);
            kpi.setDeleteStatus(true);
        }
        kpi.setName(name);
        kpi.setAlarmPlan(alarmPlan);
        kpi.setDesc(desc);
        kpi.setCode(code);
        if (StringUtils.isNotBlank(unit)) {
            kpi.setUnits(o_DictBO.findDictEntryById(unit));
        }
        o_realTimeKpiDAO.merge(kpi);
    }

    /**批量更新实时指标删除状态
     * @param idList 实时指标id集合
     */
    @Transactional
    public void removeRealTimeKpi(List<String> idList) {
        if (null != idList) {
            for (String kpiId : idList) {
                RealTimeKpi kpi = o_realTimeKpiDAO.get(kpiId);
                kpi.setDeleteStatus(false);
                o_realTimeKpiDAO.merge(kpi);
            }
        }
    }

    /**批量删除实时指标采集数据
     * @param gatherIdList 实时指标采集id集合
     */
    @Transactional
    public void removeRealTimeKpiHistoryData(List<String> gatherIdList) {
        String delHql = "delete GatherData g where g.id in (:idlist)";
        Query query = o_gatherDataDAO.createQuery(delHql);
        query.setParameterList("idlist", gatherIdList);
        query.executeUpdate();
    }

    /**更新实时指标最后采集状态
     * @param kpiId 实时指标id
     */
    @Transactional
    public void mergeRealKpiStatus(String kpiId) {
        if (StringUtils.isNotBlank(kpiId)) {
            DictEntry status = findMaxTimeGatherStatusByKpiId(kpiId);
            RealTimeKpi realTimeKpi = o_realTimeKpiDAO.get(kpiId);
            realTimeKpi.setStatus(status);
            o_realTimeKpiDAO.merge(realTimeKpi);
        }
    }

}
