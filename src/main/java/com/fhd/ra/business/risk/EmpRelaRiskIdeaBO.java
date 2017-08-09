package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.risk.EmpRelaRiskIdeaDAO;
import com.fhd.entity.risk.EmpRelaRiskIdea;

/**
 * 风险关联人员BO
 * */

@Service
public class EmpRelaRiskIdeaBO {

	@Autowired
	private EmpRelaRiskIdeaDAO o_empRelaRiskIdeaDAO;
	
	/**
	 * 保存风险关联人员
	 * */
	@Transactional
	public void mergeEmpRelaRiskIdea(EmpRelaRiskIdea empRelaRiskIdea) {
		o_empRelaRiskIdeaDAO.merge(empRelaRiskIdea);
	}
	
	/**
	 * 删除风险关联人员
	 * */
	@Transactional
	public void delEmpRelaRiskIdea(EmpRelaRiskIdea empRelaRiskIdea) {
		o_empRelaRiskIdeaDAO.delete(empRelaRiskIdea);
	}
	
	/**
	 * 风险关联修改意见
	 * */
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> findDeptLeadIdea(String riskId, String empId){
		ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id,b.content from t_rm_emp_rela_risk_idea a  ");
		sql.append(" LEFT JOIN t_rm_risk_idea b on a.id = b.EMP_RELA_RISK_ID ");
		sql.append(" where a.RISK_ID = :riskId and a.emp_id=:empId ");
        
        SQLQuery sqlQuery = o_empRelaRiskIdeaDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("riskId", riskId);
        sqlQuery.setParameter("empId", empId);
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
            
			String empRelaRiskIdeaId = "";
            String editIdeaContent = "";
            HashMap<String, Object> map = new HashMap<String, Object>();
            
            if(null != objects[0]){
            	empRelaRiskIdeaId = objects[0].toString();
            }if(null != objects[1]){
            	editIdeaContent = objects[1].toString();
            }
            
            map.put("empRelaRiskIdeaId", empRelaRiskIdeaId);
            map.put("editIdeaContent", editIdeaContent);
            arrayList.add(map);
            
            return arrayList;
		}
		
		return null;
	}
	
	/**
	 * 分组部门
	 * */
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, String>> findEmpRelaRiskIdeaGroupOrgId(){
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.org_id,b.org_name from t_rm_emp_rela_risk_idea a  ");
		sql.append(" LEFT JOIN t_sys_organization b on a.org_id = b.id  ");
		sql.append(" GROUP BY a.org_id   ");
		
        SQLQuery sqlQuery = o_empRelaRiskIdeaDAO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
            
			String orgId = "";
			String orgName = "";
            
            if(null != objects[0]){
            	orgId = objects[0].toString();
            }if(null != objects[1]){
            	orgName = objects[1].toString();
            }
            
            
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("orgId", orgId);
            map.put("orgName", orgName);
            
            arrayList.add(map);
		}
		
		return arrayList;
	}
	
	/**
	 * 通过ID查询实体
	 * */
	@SuppressWarnings("unchecked")
	public EmpRelaRiskIdea findRiskIdeaByEmpRelaRiskIdeaId(String empRelaRiskIdeaId){
		Criteria criteria = o_empRelaRiskIdeaDAO.createCriteria();
		criteria.add(Restrictions.eq("id", empRelaRiskIdeaId));
		List<EmpRelaRiskIdea> list = null;
		list = criteria.list();
		
		return list.get(0);
	}
}