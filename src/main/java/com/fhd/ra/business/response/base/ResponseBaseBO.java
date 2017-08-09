/**
 * AssessPlanBO.java
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		刘中帅
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.business.response.base;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.fhd.comm.business.CategoryBO;
import com.fhd.dao.sys.i18n.I18nDAO;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.risk.RiskOutsideBO;


/**
 * @author 宋佳
 * @version 尝试性基类
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see
 */
@Service
public class ResponseBaseBO{
	@Autowired
	private CategoryBO o_categoryBO;
	@Autowired
	private RiskOutsideBO o_piskOutsideBO;
	/**
	 *  通用分页查询
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findResponseListByPage(int limit, int start, String sort, String dir, String selectId,String type,String queryJson) {
 		StringBuilder hql = new StringBuilder();
 		List<String> riskIds = null;
 		hql.append("SELECT rm.PARENT_ID,rm.id,rm.RISK_NAME,mea.id measureId,mea.MEASURE_NAME,mea.type, mea.status status FROM t_rm_risks rm "+ 
 				   "LEFT JOIN (SELECT c.RISK_ID,c.CONTROL_MEASURE_ID FROM T_CON_MEASURE_RELA_RISK c UNION SELECT d.RISK_ID,d.SOLUTION_ID FROM T_RM_SOLUTION_RELA_RISK d,T_RM_SOLUTION c WHERE d.SOLUTION_ID = c.id " +getHigherQueryFromQueryForm(queryJson).get("solutionQuery")+ ") mearm ON rm.id = mearm.RISK_ID " +
 				   "LEFT JOIN (SELECT a.id,a.MEASURE_NAME,'M' type,estatus status FROM T_CON_CONTROL_MEASURE a WHERE a.DELETE_ESTATUS = :deleteStatus " +
 				   "UNION SELECT c.id,c.SOLUTION_NAME,'S' type,estatus FROM T_RM_SOLUTION c WHERE c.DELETE_STATUS = '1' " + getHigherQueryFromQueryForm(queryJson).get("solutionQuery")+ ") " +
 				   "mea ON mearm.CONTROL_MEASURE_ID = mea.ID");
 		if("process".equals(type)){
 			riskIds = o_piskOutsideBO.findRiskEventByProcessId(selectId);
// 			hql.append("rm.id in :riskIds");
 		}else if("sm".equals(type)){
 			riskIds = o_piskOutsideBO.findRiskEventByStrategyMapId(selectId);
 		}else if("org".equals(type)){
 			riskIds = o_piskOutsideBO.findRiskEventByOrgId(selectId);
 		}else if("risk".equals(type)){
 			riskIds = o_piskOutsideBO.findRiskEventByRiskId(selectId);
 		}else if("kpi".equals(type)){
 			List<String> selectIdList = new ArrayList<String>();
 			selectIdList.add(selectId);
 			riskIds = o_piskOutsideBO.findRiskEventByKpiIds(selectIdList);
 		}else if("sc".equals(type)){
 			List<String> kpiIdList = new ArrayList<String>();
 			kpiIdList = o_categoryBO.findRelaKpiByScId(selectId);
 			riskIds = o_piskOutsideBO.findRiskEventByKpiIds(kpiIdList);
 		}
 		hql.append(" WHERE rm.id IN (:riskIds)");
 		hql.append( getHigherQueryFromQueryForm(queryJson).get("riskQuery"));
 		hql.append(" ORDER BY rm.id,mea.MEASURE_NAME DESC ");
 		WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();//SPRING-BEAN服务
 		I18nDAO o_I18nDAO = applicationContext.getBean(I18nDAO.class);
 		Session session = o_I18nDAO.getSessionFactory().openSession();//HIBERNATE-SESSION服务
 		Query hqlQuery = session.createSQLQuery(hql.toString());
		hqlQuery.setParameter("deleteStatus",Contents.DELETE_STATUS_USEFUL);
 		if(riskIds != null && riskIds.size() >0){
 			hqlQuery.setParameterList("riskIds", riskIds);
 		}else{
 			riskIds = new ArrayList<String>();
 			riskIds.add("12333333");
 			hqlQuery.setParameterList("riskIds",riskIds);
 		}
 		hqlQuery.setFirstResult(start);
 		hqlQuery.setMaxResults(limit);
 		return hqlQuery.list();
	}
	/**
	 * 根据 高级查询窗口返回查询条件
	 * @param queryJson
	 * @return
	 */
	public Map<String,String> getHigherQueryFromQueryForm(String queryJson){
		Map<String,String> queryMap = new HashMap<String,String>();
		String solutionQuery = "";
		String riskQuery = "";
		if(StringUtils.isNotBlank(queryJson)){
			JSONObject jo=JSONObject.fromObject(queryJson);
			StringBuffer solutionQuerySb = new StringBuffer();
			StringBuffer riskQuerySb = new StringBuffer();
			if(StringUtils.isNotBlank(jo.getString("solutionName"))){
				solutionQuerySb.append(" and c.solution_name like '%"+jo.getString("solutionName")+"%' ");
			}
			if(StringUtils.isNotBlank(jo.getString("solutionCode"))){
				solutionQuerySb.append(" and c.solution_code like '%"+jo.getString("solutionCode")+"%' ");
			}
			if(StringUtils.isNotBlank(jo.getString("isAddPresolution"))){
				solutionQuerySb.append(" and c.is_add_presolution = '"+jo.getString("isAddPresolution")+"' ");
			}
			if(StringUtils.isNotBlank(jo.getString("type"))&&!"请选择".equals(jo.getString("type"))){
				solutionQuerySb.append(" and c.ETYPE = '"+jo.getString("type")+"' ");
			}
			if(StringUtils.isNotBlank(jo.getString("status"))&&!"请选择".equals(jo.getString("status"))){
				String statusWhe = "";
				if(jo.getString("status").startsWith("[")){
					statusWhe = jo.getString("status").replace("[", "").replace("]", "");
				}else{
					statusWhe = jo.getString("status");
				}
				solutionQuerySb.append(" and c.ESTATUS in ("+statusWhe+") ");
			}
			if(StringUtils.isNotBlank(jo.getString("measureType"))&&!"请选择".equals(jo.getString("measureType"))){
				String statusWhe = "";
				if(jo.getString("measureType").startsWith("[")){
					statusWhe = jo.getString("measureType").replace("[", "").replace("]", "");
				}else{
					statusWhe = jo.getString("measureType");
				}
				riskQuerySb.append(" and mea.type in ("+statusWhe+") ");
			}
			
			solutionQuery = solutionQuerySb.toString();
			riskQuery = riskQuerySb.toString();
		}
		queryMap.put("solutionQuery", solutionQuery);
		queryMap.put("riskQuery", riskQuery);
		return queryMap;
	}
	/**
	 *  查询分页数据总条数
	 */
	@SuppressWarnings("unchecked")
	public Integer findResponseListCountByDir(String dir,String selectId,String type,String queryJson) {
		String companyId = UserContext.getUser().getCompanyid();
		StringBuilder hql = new StringBuilder();
		List<String> riskIds = null;
		hql.append("SELECT count(*) num FROM t_rm_risks rm "+ 
				"LEFT JOIN (SELECT c.RISK_ID,c.CONTROL_MEASURE_ID FROM T_CON_MEASURE_RELA_RISK c UNION SELECT d.RISK_ID,d.SOLUTION_ID FROM T_RM_SOLUTION_RELA_RISK d,T_RM_SOLUTION c WHERE d.SOLUTION_ID = c.id "+getHigherQueryFromQueryForm(queryJson).get("solutionQuery")+") mearm ON rm.id = mearm.RISK_ID " +
				"LEFT JOIN (SELECT a.id,a.MEASURE_NAME,'I' type FROM T_CON_CONTROL_MEASURE a WHERE a.DELETE_ESTATUS = :deleteStatus UNION" +
				" SELECT c.id,c.SOLUTION_NAME,'E' type FROM T_RM_SOLUTION c WHERE c.DELETE_STATUS = '1'  "+getHigherQueryFromQueryForm(queryJson).get("solutionQuery")+") mea " +
				"ON mearm.CONTROL_MEASURE_ID = mea.ID");
		if("process".equals(type)){
			riskIds = o_piskOutsideBO.findRiskEventByProcessId(selectId);
		}else if("sm".equals(type)){
			riskIds = o_piskOutsideBO.findRiskEventByStrategyMapId(selectId);
		}else if("org".equals(type)){
			riskIds = o_piskOutsideBO.findRiskEventByOrgId(selectId);
		}else if("risk".equals(type)){
			riskIds = o_piskOutsideBO.findRiskEventByRiskId(selectId);
		}else if("kpi".equals(type)){
			List<String> selectIdList = new ArrayList<String>();
 			selectIdList.add(selectId);
 			riskIds = o_piskOutsideBO.findRiskEventByKpiIds(selectIdList);
		}else if("sc".equals(type)){
 			List<String> kpiIdList = new ArrayList<String>();
 			kpiIdList = o_categoryBO.findRelaKpiByScId(selectId);
 			riskIds = o_piskOutsideBO.findRiskEventByKpiIds(kpiIdList);
 		}
		hql.append(" WHERE rm.id IN (:riskIds)");
		hql.append( getHigherQueryFromQueryForm(queryJson).get("riskQuery"));
 		
		WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();//SPRING-BEAN服务
 		I18nDAO o_I18nDAO = applicationContext.getBean(I18nDAO.class);
 		Session session = o_I18nDAO.getSessionFactory().openSession();//HIBERNATE-SESSION服务
 		Query hqlQuery = session.createSQLQuery(hql.toString());
 		hqlQuery.setParameter("deleteStatus",Contents.DELETE_STATUS_USEFUL);
 		if(riskIds != null && riskIds.size() >0 ){
 			hqlQuery.setParameterList("riskIds", riskIds);
 		}else{
 		    riskIds = new ArrayList<String>();
 			riskIds.add("12333333");
 			hqlQuery.setParameterList("riskIds",riskIds);
 		}
 		return Integer.valueOf(hqlQuery.uniqueResult().toString());
	}
}
