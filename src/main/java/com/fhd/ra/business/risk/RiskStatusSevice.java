package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;

/**
 * 计算风险水平
 * @author   zhengjunxiang
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-11-25
 *
 * @see 	 
 */
@Service
@SuppressWarnings("unchecked")
public class RiskStatusSevice {

	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	
    /**
     * 根据指标名称，查询相关的风险状态/评估值等
     * @param kpiName 指标名称
     * @param timePeriod 时间纬度字符串
     * @return
     */
	@Transactional
    public List<RiskAdjustHistory> findRiskStatusBySome(String kpiName, String timePeriod) {
    	List<Risk> riskList = findRiskByKpiName(kpiName);
    	
    	if(riskList==null || riskList.size() == 0){
    		return new ArrayList<RiskAdjustHistory>();
    	}
        Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
        criteria.createAlias("risk", "risk",CriteriaSpecification.LEFT_JOIN,Restrictions.eq("risk.deleteStatus", "1"));
//        criteria.createAlias("risk.kpiRelaRisks", "kpiRelaRisks");
//        criteria.createAlias("kpiRelaRisks.kpi", "kpi");
        criteria.createAlias("timePeriod", "timePeriod");
        //自动查询历史记录
        //criteria.createAlias("adjustHistory", "adjustHistory", CriteriaSpecification.LEFT_JOIN);
        
        //criteria.add(Restrictions.eq("kpi.name", kpiName));
        if(StringUtils.isNotBlank(timePeriod)){
        	criteria.add(Restrictions.eq("timePeriod.id", timePeriod));
        }else {//最新记录
        	criteria.add(Restrictions.eq("isLatest", "1"));
        }
        criteria.add(Restrictions.in("risk", riskList));
        
        return criteria.list();
    }
    
    /**
     * 查找指标名称下的风险
     * @param kpiName
     * @return
     */
    public List<Risk> findRiskByKpiName(String kpiName){
    	Criteria criteria = o_riskDAO.createCriteria();
        criteria.createAlias("kpiRelaRisks", "kpiRelaRisks",CriteriaSpecification.INNER_JOIN,Restrictions.eq("type", "I"));
        criteria.createAlias("kpiRelaRisks.kpi", "kpi");
        criteria.add(Restrictions.eq("kpi.isKpiCategory", "KPI"));
        criteria.add(Restrictions.eq("kpi.name", kpiName));
    	return criteria.list();
    }
}

