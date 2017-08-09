package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.risk.RiskIdeaDAO;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskIdea;
import com.fhd.ra.business.assess.util.TimeUtil;

/**
 * 风险修改意见BO
 * */

@Service
public class RiskIdeaBO {

	@Autowired
	private RiskIdeaDAO o_riskIdeaDAO;
	
	/**
	 * 保存风险关联人员
	 * */
	@Transactional
	public void mergeRiskIdea(RiskIdea riskIdea) {
		o_riskIdeaDAO.merge(riskIdea);
	}
	
	/**
	 * 删除风险关联人员
	 * */
	@Transactional
	public void delRiskIdea(RiskIdea riskIdea) {
		o_riskIdeaDAO.delete(riskIdea);
	}
	
	/**
	 * 风险关联人员下修改意见
	 * */
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> findRiskEmpIdeaList(String nowTime, String query, String orgId){
		ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
		Map<String,String> mapParams = new HashMap<String,String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.RISK_ID,b.id as empRelaRiskIdeaId," +
				"c.RISK_NAME,b.EMP_ID,d.EMP_NAME,a.CONTENT,g.org_name,a.id as riskIdeaId  from t_rm_risk_idea  a ");
		sql.append(" LEFT JOIN t_rm_emp_rela_risk_idea b on b.id = a.emp_rela_risk_id ");
		sql.append(" LEFT JOIN t_rm_risks c on c.id = b.RISK_ID ");
		sql.append(" LEFT JOIN t_sys_employee d on d.id = b.EMP_ID ");
		sql.append(" LEFT JOIN t_sys_emp_org e on e.EMP_ID = b.EMP_ID ");
		sql.append(" LEFT JOIN t_sys_organization g on g.id = e.ORG_ID ");
		sql.append(" where a.create_time < :nowTime and g.id=:orgId ");
		mapParams.put("nowTime", nowTime);
		mapParams.put("orgId", orgId);
		if(StringUtils.isNotBlank(query)){
			sql.append(" and c.RISK_NAME like :query ");
			mapParams.put("query", "%"+query+"%");
		}
		sql.append(" ORDER BY c.RISK_NAME ");
        SQLQuery sqlQuery = o_riskIdeaDAO.createSQLQuery(sql.toString(),mapParams);
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
            
			String riskId = "";
            String empRelaRiskIdeaId = "";
            String riskName = "";
            String empId = "";
            String empName = "";
            String content = "";
            String orgName = "";
            String riskIdeaId = "";
            HashMap<String, Object> map = new HashMap<String, Object>();
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	empRelaRiskIdeaId = objects[1].toString();
            } if(null != objects[2]){
            	riskName = objects[2].toString();
            }if(null != objects[3]){
            	empId = objects[3].toString();
            } if(null != objects[4]){
            	empName = objects[4].toString();
            }if(null != objects[5]){
            	content = objects[5].toString();
            }if(null != objects[6]){
            	orgName = objects[6].toString();
            }if(null != objects[7]){
            	riskIdeaId = objects[7].toString();
            }
            
            map.put("riskId", riskId);
            map.put("empRelaRiskIdeaId", empRelaRiskIdeaId);
            map.put("riskName", riskName);
            map.put("empId", empId);
            map.put("empName", empName);
            map.put("content", content);
            map.put("orgName", orgName);
            map.put("riskIdeaId", riskIdeaId);
            arrayList.add(map);
		}
		
		return arrayList;
	}
	
	/**
	 * 查询修改意见
	 * */
	@SuppressWarnings("unchecked")
	public RiskIdea findRiskIdeaByEmpRelaRiskIdeaId(String empRelaRiskIdeaId){
		Criteria criteria = o_riskIdeaDAO.createCriteria();
		criteria.add(Restrictions.eq("empRelaRiskIdea.id", empRelaRiskIdeaId));
		List<RiskIdea> list = null;
		list = criteria.list();
		
		return list.get(0);
	}
}