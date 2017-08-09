/**
 * RiskOrgBO.java
 * com.fhd.risk.business
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/
/**
 * RiskOrgBO.java
 * com.fhd.risk.business
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19        金鹏祥
 *
 * Copyright (c) 2012, FirstHuida All Rights Reserved.
*/


package com.fhd.ra.business.risk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.ra.interfaces.risk.IRiskOrgBO;


@Service
public class RiskOrgBO implements IRiskOrgBO{
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	
	@SuppressWarnings("unchecked")
	public List<RiskOrg> findRiskOrgBySome(String searchName, String companyId, String orgId, String deleteStatus, Boolean rbs) {
		Criteria criteria = o_riskOrgDAO.createCriteria();
		
		if (StringUtils.isNotBlank(searchName)) {
			criteria.createAlias("risk", "r")
				.add(Restrictions.like("r.name", searchName, MatchMode.ANYWHERE))
				.add(Restrictions.eq("r.company.id", companyId))
				.add(Restrictions.eq("r.deleteStatus", deleteStatus));
		} else {
			criteria.createAlias("risk", "r").add(Restrictions.eq("r.company.id", companyId));
		}
		criteria.createAlias("sysOrganization", "s").add(Restrictions.eq("s.id", orgId));

		if(rbs){
			criteria.add(Restrictions.eq("r.isRiskClass", "RBS"));
		}
		
		return criteria.list();
	}
	
	/**
	 * 该部门下是否有风险
	 * */
	@SuppressWarnings("unchecked")
	public List<RiskOrg> findRiskOrgByOrgId(String companyId, String orgId, String deleteStatus) {
		Criteria criteria = o_riskOrgDAO.createCriteria();
		criteria.createAlias("risk", "r").add(Restrictions.eq("r.company.id", companyId));
		criteria.createAlias("sysOrganization", "s").add(Restrictions.eq("s.id", orgId));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<RiskOrg> findRiskOrgBySome(String companyId, String query, String deleteStatus, Boolean rbs){
		List<RiskOrg> riskOrgList = null;
		Criteria criteria = o_riskOrgDAO.createCriteria()
		.createAlias("risk", "r")
		.add(Restrictions.eq("r.company.id", companyId))
		.add(Restrictions.eq("r.deleteStatus", deleteStatus));
		
		if (StringUtils.isNotBlank(query)) {
			criteria.add(Restrictions.like("r.name", query,
					MatchMode.ANYWHERE));
		}
		
		if(rbs){
			criteria.add(Restrictions.eq("r.isRiskClass", "RBS"));
		}
		
		riskOrgList = criteria.list();
		
		return riskOrgList;
	}
	
	public HashMap<String, Risk> findRiskOrgMap(){
		HashMap<String, Risk> map = new HashMap<String, Risk>();
		List<RiskOrg> riskOrgList = o_riskOrgDAO.createCriteria().list();
		for (RiskOrg riskOrg : riskOrgList) {
			try {
				if(null != riskOrg.getSysOrganization()){
					map.put(riskOrg.getSysOrganization().getId(), riskOrg.getRisk());
				}
			} catch (Exception e) {
			}
		}
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> findRiskOrg(){
		Set<String> otheridSet = new HashSet<String>();
		List<RiskOrg> riskOrgList = o_riskOrgDAO.createCriteria().list();
		for (RiskOrg riskOrg : riskOrgList) {
			otheridSet.add(riskOrg.getRisk().getId());
		}
		
		return otheridSet;
	}
	
	public void removeRiskOrgById(RiskOrg riskOrg){
		o_riskOrgDAO.delete(riskOrg);
	}
	public void removeRiskOrgByRiskId(String riskId){
		
		boolean isBool = false;
		try {
			SQLQuery sqlQuery = o_riskOrgDAO.createSQLQuery("delete from T_RM_RISK_ORG where risk_id = ?", riskId);
			sqlQuery.executeUpdate();
			isBool = true;
		} catch (Exception e) {
		}
	}
	public void saveRiskOrg(RiskOrg riskOrg){
		o_riskOrgDAO.merge(riskOrg);
	}
	
	/**  
	* @Title: findOrgsByRiskId  
	* @Description: 根据riskId获取部门
	* @param riskId
	* @return List<RiskOrg>
	* @throws  
	*/
	@SuppressWarnings("unchecked")
	public List<RiskOrg> findOrgsByRiskId(String riskId){
		Criteria criteria = this.o_riskOrgDAO.createCriteria();
		criteria.createAlias("risk", "risk");
		criteria.add(Restrictions.eq("risk.id", riskId));
		return criteria.list();
	}
	
}

