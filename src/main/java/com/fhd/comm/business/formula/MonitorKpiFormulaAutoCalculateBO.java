package com.fhd.comm.business.formula;

import java.text.ParseException;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.kpi.KpiDAO;
import com.fhd.entity.kpi.Kpi;
import com.fhd.fdc.utils.Contents;

/**
 * 监控指标公式自动计算
 *
 */
@Service
public class MonitorKpiFormulaAutoCalculateBO {

    @Autowired
    private KpiDAO o_kpiDAO;

    @Autowired
    private CalculateBatchBO o_formulaAutoCalculateBO;

    /**
     * 定时触发计算公式.
     * @throws ParseException 
     */
    @Transactional
    public boolean formulaAutoCalculate() throws ParseException {
        List<Kpi> kpiList = findAllMonitorKpiList();
        //根据公式计算指标
        o_formulaAutoCalculateBO.calculateByFormula(kpiList);
        return true;
    }

    /**
     * <pre>
     * 查询所有可用(未删除状态)的监控类型的指标.
     * </pre>
     * @return List<Kpi>
     */
    @SuppressWarnings("unchecked")
    public List<Kpi> findAllMonitorKpiList() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Kpi.class);
        criteria.setFetchMode("alarmMeasure", FetchMode.JOIN);
        criteria.add(Restrictions.sqlRestriction("now()>=calculate_time and ADDDATE(calculate_time,RESULT_COLLECT_INTERVAL)>=now()"));
        criteria.add(Restrictions.sqlRestriction(" (LENGTH(target_formula)<>0 or LENGTH(result_formula)<>0 or LENGTH(assessment_formula)<>0)"));
        criteria.add(Restrictions.like("kpiType.id", "%kpi_kpi_type_alarm%"));
        criteria.add(Restrictions.eq("deleteStatus", true));
        criteria.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
        criteria.setFetchMode("company", FetchMode.SELECT);
        criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
        criteria.setFetchMode("createBy", FetchMode.SELECT);
        criteria.addOrder(Order.desc("level"));
        criteria.add(Restrictions.eq("calc.id", Contents.DICT_Y));
        return criteria.getExecutableCriteria(o_kpiDAO.getSession()).list();
    }
}
