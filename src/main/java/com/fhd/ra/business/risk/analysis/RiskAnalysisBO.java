
package com.fhd.ra.business.risk.analysis;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.kpi.KpiRelaCategoryDAO;
import com.fhd.dao.kpi.SmRelaKpiDAO;
import com.fhd.dao.risk.KpiRelaRiskDAO;
import com.fhd.entity.kpi.KpiRelaCategory;
import com.fhd.entity.kpi.SmRelaKpi;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.sm.business.KpiBO;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("unchecked")
public class RiskAnalysisBO {

    @Autowired
    private KpiRelaRiskDAO o_kpiRelaRiskDAO;
    
    @Autowired
    private SmRelaKpiDAO o_smRelaKpiDAO;

    @Autowired
    private KpiRelaCategoryDAO o_kpiRelaCategoryDAO;

    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private RiskBO o_riskBO;

    /**
     * 分页查询风险分析
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-23
     * @since Ver 1.1
     */
    public Page<SmRelaKpi> findRiskAnalysis(String query, Page<SmRelaKpi> page, String sort, String dir, String smid) {
        String companyId = UserContext.getUser().getCompanyid();
        DetachedCriteria dc = DetachedCriteria.forClass(SmRelaKpi.class);
        dc.add(Restrictions.eq("strategyMap.id", smid));
        // 战略目标筛选
        dc.createAlias("kpi", "kpi", CriteriaSpecification.LEFT_JOIN);
        dc.createAlias("kpi.kpiRelaRisks", "kkrr", CriteriaSpecification.LEFT_JOIN,Restrictions.eq("kkrr.type", "I"));
        dc.createAlias("kkrr.risk", "r", CriteriaSpecification.LEFT_JOIN,
                Restrictions.and(Restrictions.and(Restrictions.eq("r.company.id", companyId), Restrictions.eq("r.isRiskClass", "re")), 
                        Restrictions.eq("r.deleteStatus", "1")));
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sort));
        } else {
            dc.addOrder(Order.desc(sort));
        }
        return o_smRelaKpiDAO.findPage(dc, page, false);
    }

    /**
     * 根据影响指标查询风险
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-27
     * @since Ver 1.1
     */
    public List<KpiRelaRisk> findInfluKpi(String kpiid, String smid) {
        String companyId = UserContext.getUser().getCompanyid();
        Criteria criteria = o_kpiRelaRiskDAO.createCriteria();
        criteria.add(Restrictions.eq("type", "I"));// 影响指标
        // 风险筛选条件
        criteria.createCriteria("risk", "r");
        criteria.add(Restrictions.eq("r.company.id", companyId));
        criteria.add(Restrictions.eq("r.isRiskClass", "re"));
        criteria.add(Restrictions.eq("r.deleteStatus", "1"));
        criteria.createCriteria("kpi", "kpi");
        criteria.add(Restrictions.eq("kpi.id", kpiid));
        criteria.createCriteria("kpi.dmRelaKpis", "dk");
        criteria.createCriteria("dk.strategyMap", "dsm");
        criteria.add(Restrictions.eq("dsm.id", smid));
        return criteria.list();
    }

    /**
     * 风险关联保存
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-28
     * @since Ver 1.1
     */
    @Transactional
    public void saveKpiRelaRisk(String kpiid, String riskids, String smid) {
        List<KpiRelaRisk> krrList = findInfluKpi(kpiid, smid);
        for (KpiRelaRisk kkr : krrList) {
            o_kpiRelaRiskDAO.delete(kkr);
        }
        if (StringUtils.isNotBlank(riskids)) {
            String[] riskidArray = riskids.split(",");
            for (String riskid : riskidArray) {
                KpiRelaRisk krr = new KpiRelaRisk();
                krr.setId(Identities.uuid());
                krr.setKpi(o_kpiBO.findKpiById(kpiid));
                krr.setRisk(o_riskBO.findRiskById(riskid));
                krr.setType("I");
                o_kpiRelaRiskDAO.merge(krr);
            }
        }
    }

    /**
     * 积分卡风险分析
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-23
     * @since Ver 1.1
     */
    public Page<KpiRelaCategory> findScRiskAnalysis(String query, Page<KpiRelaCategory> page, String sort, String dir, String categoryid) {
        String companyId = UserContext.getUser().getCompanyid();
        DetachedCriteria dc = DetachedCriteria.forClass(KpiRelaCategory.class);
        dc.add(Restrictions.eq("category.id", categoryid));
        // 战略目标筛选
        dc.createAlias("kpi", "kpi", CriteriaSpecification.LEFT_JOIN);
        dc.createAlias("kpi.kpiRelaRisks", "kkrr", CriteriaSpecification.LEFT_JOIN,Restrictions.eq("kkrr.type", "I"));
        dc.createAlias("kkrr.risk", "r", CriteriaSpecification.LEFT_JOIN,
                Restrictions.and(Restrictions.and(Restrictions.eq("r.company.id", companyId), Restrictions.eq("r.isRiskClass", "re")), 
                        Restrictions.eq("r.deleteStatus", "1")));
        if ("ASC".equalsIgnoreCase(dir)) {
            dc.addOrder(Order.asc(sort));
        } else {
            dc.addOrder(Order.desc(sort));
        }
        return o_kpiRelaCategoryDAO.findPage(dc, page, false);
    }
    
    /**
     * 记分卡根据影响指标查询风险
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-27
     * @since Ver 1.1
     */
    public List<KpiRelaRisk> findScInfluKpi(String kpiid, String categoryid) {
        String companyId = UserContext.getUser().getCompanyid();
        Criteria criteria = o_kpiRelaRiskDAO.createCriteria();
        criteria.add(Restrictions.eq("type", "I"));// 影响指标
        // 风险筛选条件
        criteria.createCriteria("risk", "r");
        criteria.add(Restrictions.eq("r.company.id", companyId));
        criteria.add(Restrictions.eq("r.isRiskClass", "re"));
        criteria.add(Restrictions.eq("r.deleteStatus", "1"));
        criteria.createCriteria("kpi", "kpi");
        criteria.add(Restrictions.eq("kpi.id", kpiid));
        criteria.createCriteria("kpi.KpiRelaCategorys", "kk");
        criteria.createCriteria("kk.category", "kc");
        criteria.add(Restrictions.eq("kc.id", categoryid));
        return criteria.list();
    }
    /**
     * 记分卡风险关联保存
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-28
     * @since Ver 1.1
     */
    @Transactional
    public void saveScKpiRelaRisk(String kpiid, String riskids, String categoryid) {
        List<KpiRelaRisk> krrList = findScInfluKpi(kpiid, categoryid);
        for (KpiRelaRisk kkr : krrList) {
            o_kpiRelaRiskDAO.delete(kkr);
        }
        if (StringUtils.isNotBlank(riskids)) {
            String[] riskidArray = riskids.split(",");
            for (String riskid : riskidArray) {
                KpiRelaRisk krr = new KpiRelaRisk();
                krr.setId(Identities.uuid());
                krr.setKpi(o_kpiBO.findKpiById(kpiid));
                krr.setRisk(o_riskBO.findRiskById(riskid));
                krr.setType("I");
                o_kpiRelaRiskDAO.merge(krr);
            }
        }
    }

}
