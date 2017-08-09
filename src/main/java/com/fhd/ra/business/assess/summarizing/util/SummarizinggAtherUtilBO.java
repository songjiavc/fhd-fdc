package com.fhd.ra.business.assess.summarizing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.dao.risk.KpiAdjustHistoryDAO;
import com.fhd.dao.risk.OrgAdjustHistoryDAO;
import com.fhd.dao.risk.ProcessAdjustHistoryDAO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.StrategyAdjustHistoryDAO;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.entity.risk.StrategyAdjustHistory;

/**
 * 计算状态分通用方法业务
 * */

@Service
public class SummarizinggAtherUtilBO {
	@Autowired
	private StatisticsResultDAO o_statisticsResultDAO;
	
	@Autowired
	private OrgAdjustHistoryDAO o_orgAdjustHistoryDAO;
	
	@Autowired
	private ProcessAdjustHistoryDAO o_processAdjustHistoryDAO;
	
	@Autowired
	private StrategyAdjustHistoryDAO o_strategyAdjustHistoryDAO;
	
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	
	@Autowired
	private KpiAdjustHistoryDAO o_kpiAdjustHistoryDAO;
	
	
	
	/**
	 * 查询该评估下的统计结果中所有风险对应风险水平并已MAP方式存储(发生可能性、影响程度)
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, ArrayList<String>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findStatisticsResultRiskElevelByAssessPlanId(String assessPlanId){
		Criteria criteria = o_statisticsResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		
		
		ArrayList<String> arrayList = null;
		
		
		List<StatisticsResult> list = null;
		list = criteria.list();
		for (StatisticsResult statisticsResult : list) {
			if(map.get(statisticsResult.getRiskScoreObject()) != null){
				map.get(statisticsResult.getRiskScoreObject()).add(statisticsResult.getDimension().getId() + "--" + statisticsResult.getScore());
			}else{
				arrayList = new ArrayList<String>();
				arrayList.add(statisticsResult.getDimension().getId() + "--" + statisticsResult.getScore());
				map.put(statisticsResult.getRiskScoreObject(), arrayList);
			}
		}
		
		return map;
	}
	
	/**
	 * 查询该公司下所有最新组织评估纪录
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findDeptByCompanyIdNewAllMap(String assessPlanId){
		Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		//criteria.add(Restrictions.eq("isLatest", "1"));
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		List<OrgAdjustHistory> list = null;
		list = criteria.list();
		for (OrgAdjustHistory orgAdjustHistory : list) {
			map.put(orgAdjustHistory.getOrganization().getId(), orgAdjustHistory.getAssessementStatus());
		}
		
		return map;
	}
	
	/**
	 * 查询该公司下所有最新流程评估纪录
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findProcessByCompanyIdNewAllMap(String assessPlanId){
		HashMap<String, String> map = new HashMap<String, String>();
		
		try {
			Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			//criteria.add(Restrictions.eq("isLatest", "1"));
			
			List<ProcessAdjustHistory> list = null;
			list = criteria.list();
			for (ProcessAdjustHistory processAdjustHistory : list) {
				map.put(processAdjustHistory.getProcess().getId(), processAdjustHistory.getAssessementStatus());
				
			}
		} catch (Exception e) {
			
		}
		
		
		return map;
	}
	
	/**
	 * 查询该公司下所有最新目标评估纪录
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findStrategyByCompanyIdNewAllMap(String assessPlanId){
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			//criteria.add(Restrictions.eq("isLatest", "1"));
			
			List<StrategyAdjustHistory> list = null;
			list = criteria.list();
			for (StrategyAdjustHistory strategyAdjustHistory : list) {
				map.put(strategyAdjustHistory.getStrategyMap().getId(), strategyAdjustHistory.getAssessementStatus());
			}
		} catch (Exception e) {
		}
		
		return map;
	}
	
	/**
	 * 查询该公司下所有最新风险评估纪录
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findRiskByCompanyIdNewAllMap(String assessPlanId){
		
		StringBuffer sql = new StringBuffer();
		HashMap<String, String> map = new HashMap<String, String>();
		
		sql.append(" select risk_id,ASSESSEMENT_STATUS from " +
				"t_rm_risk_adjust_history where ASSESS_PLAN_ID='" + assessPlanId + "' ");//and is_Latest = '1' ");
		
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String riskId = "";
            String assessEmentStatus = "";
            
            if(null != objects[0]){
            	riskId = objects[0].toString();

            }if(null != objects[1]){
            	assessEmentStatus = objects[1].toString();
            	
            }
            map.put(riskId, assessEmentStatus);
        }
        
        return map;
	}
	
	/**
	 * 查询该公司下所有最新指标评估纪录
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findKpiByCompanyIdNewAllMap(String assessPlanId){
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			//criteria.add(Restrictions.eq("isLatest", "1"));
			
			List<KpiAdjustHistory> list = null;
			list = criteria.list();
			for (KpiAdjustHistory kpiAdjustHistory : list) {
				map.put(kpiAdjustHistory.getKpiId().getId(), kpiAdjustHistory.getAssessementStatus());
			}
		} catch (Exception e) {
		}
		
		return map;
	}
}