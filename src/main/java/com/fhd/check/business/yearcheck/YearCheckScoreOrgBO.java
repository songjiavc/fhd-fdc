package com.fhd.check.business.yearcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.dao.check.yearcheck.YearCheckScoreOrgDAO;
import com.fhd.entity.check.checkproject.CheckProject;
import com.fhd.entity.check.yearcheck.plan.YearCheckScoreOrg;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;

import net.sf.json.JSONArray;

@Service
public class YearCheckScoreOrgBO {
	
	@Autowired
	YearCheckScoreOrgDAO checkScoreOrgDAO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;

	@SuppressWarnings("unchecked")
	public List<YearCheckScoreOrg> findCheckRuleByEmp(String businessId, String orgId) {
		Criteria c=checkScoreOrgDAO.createCriteria();
		c.createAlias("planOrgId", "planOrg",c.LEFT_JOIN);
		c.createAlias("planOrg.yearCheckPlan", "plan",c.LEFT_JOIN);
		c.createAlias("planOrg.orgId", "org",c.LEFT_JOIN);
		if (StringUtils.isNotBlank(businessId)) {
			c.add(Restrictions.eq("plan.id", businessId));
		}
		if (StringUtils.isNotBlank(orgId)) {
			c.add(Restrictions.eq("org.id", orgId));
		}
		c.addOrder(Order.desc("checkProjectName")).addOrder(Order.desc("checkCommenttName")).addOrder(Order.desc("checkDetailName"));
		List<YearCheckScoreOrg> list=c.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<YearCheckScoreOrg> findCheckRuleByOrgId(String businessId,String executionId) {
		Criteria c=checkScoreOrgDAO.createCriteria();
		c.createAlias("planOrgId", "planOrg",c.LEFT_JOIN);
		c.createAlias("planOrg.yearCheckPlan", "plan",c.LEFT_JOIN);
		c.createAlias("planOrg.orgId", "org",c.LEFT_JOIN);
		if (StringUtils.isNotBlank(businessId)) {
			c.add(Restrictions.eq("plan.id", businessId));
		}
		String orgId=o_jbpmBO.getVariable("orgId", executionId);
			if (StringUtils.isNotBlank(orgId)) {
				c.add(Restrictions.eq("org.id", orgId));
			}
		
		c.addOrder(Order.desc("checkProjectName")).addOrder(Order.desc("checkCommenttName")).addOrder(Order.desc("checkDetailName"));
		List<YearCheckScoreOrg> list=c.list();
		return list;
	}
	
	@Transactional
	public void savaCheckScoreOrg(List<YearCheckScoreOrg> list,String data){
		if (null!=list&&null==data) {
			for (YearCheckScoreOrg yearCheckScoreOrg:list) {
				if (!StringUtils.isNotBlank(yearCheckScoreOrg.getId())) {
					yearCheckScoreOrg.setId(UUID.randomUUID().toString());
				}
				checkScoreOrgDAO.merge(yearCheckScoreOrg);
			}
		}else if(data!=null&&list==null)
		{
			JSONArray str = JSONArray.fromObject(data);
			@SuppressWarnings("unchecked")
			List<YearCheckScoreOrg> scoreOrgList = (List<YearCheckScoreOrg>) JSONArray.toCollection(str, YearCheckScoreOrg.class);
			for(YearCheckScoreOrg score:scoreOrgList)
			{
				YearCheckScoreOrg oldScore=checkScoreOrgDAO.get(score.getId());
				oldScore.setOwenScore(score.getOwenScore());
				oldScore.setAuditScore(score.getAuditScore());
				oldScore.setRiskScore(score.getRiskScore());
				checkScoreOrgDAO.merge(oldScore);
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findCheckGroupByOrg(String executionId, String businessId,boolean isAll) {
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT org.ORG_NAME,org.ID,SUM(yso.OWEN_SCORE),SUM(yso.RISK_SCORE),SUM(yso.AUDIT_SCORE),yso.id FROM t_rm_check_year_score_org yso");
		sql.append(" LEFT JOIN t_rm_check_year_plan_org plan ON plan.ID = yso.PLAN_ORG_ID");
		sql.append(" LEFT JOIN t_sys_organization org ON plan.ORG_ID = org.ID");
		String orgID = o_jbpmBO.getVariable("orgId", executionId);
		String empID = o_jbpmBO.getVariable("empId", executionId);
		String empName = o_jbpmBO.getVariable("empName", executionId);
		sql.append(" WHERE plan.plan_id = :planId");
		if (!isAll) {
		sql.append(" AND org.id = :orgId");
		}
		sql.append(" GROUP BY org.id");
		SQLQuery sqlquery=checkScoreOrgDAO.createSQLQuery(sql.toString());
		if (!isAll) {
		sqlquery.setParameter("orgId", orgID);
		}
		sqlquery.setParameter("planId", businessId);
		List<Object[]> list=sqlquery.list();
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		  for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			  Object[] objects = (Object[]) iterator.next();
			  Map<String,Object> map=new HashMap<String, Object>();
			  if(null!=objects[0])
			  {
				  map.put("orgName", objects[0]) ;
			  }
			  if(null!=objects[1])
			  {
				  map.put("orgId", objects[1]) ;
			  }
			  if(null!=objects[2])
			  {
				  map.put("owenScore", objects[2]) ;
			  }
			  if(null!=objects[3])
			  {
				  map.put("riskScore", objects[3]) ;
			  }
			  if(null!=objects[4])
			  {
				  map.put("auditScore", objects[4]) ;
			  }
			  if(null!=objects[5])
			  {
				  map.put("id", objects[5]) ;
			  }
			  if(!isAll){
				  map.put("empId", empID);
				  map.put("empName", empName);
			  }
			  data.add(map);
		  }
		return data;		
	}

}
