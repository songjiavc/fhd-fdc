package com.fhd.ra.business.assess.risktidy;

import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;

@Service
public class RiskTidyCountBO {

	@Autowired
	private StatisticsResultDAO o_statisticsResultDAO;
	
	/**
	 * 所有风险事件ID
	 * @param assessPlanId 评估计划ID
	 * @return JSONArray JSON参数
	 * @author 金鹏祥
	 * */
	public JSONArray findRiskReAll(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		JSONArray jsonArray = new JSONArray();
		sql.append(" select a.id,risk.parent_id,risk.parent_name,risk.id as risk_id,risk.risk_name,risk.template_id,d.temp_id,risk.alarm_scenario ");
		sql.append(" from t_rm_statistics_result  a, t_rm_risks risk , t_rm_risks c ,t_rm_risk_assess_plan d ");
		sql.append(" where a.score_object_id=risk.id and risk.is_risk_class = 're'   and a.assess_plan_id=:assessPlanId ");
		sql.append(" and risk.parent_id=c.id  GROUP BY risk.id ");
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String riskId = ""; //风险ID
            
            if(null != objects[3]){
            	riskId = objects[3].toString();
            }
            
            JSONObject resultJSON = new JSONObject(); 
            
            resultJSON.put("riskId", riskId);

            jsonArray.add(resultJSON);
        }
        
        return jsonArray;
	}
}
