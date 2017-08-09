/**
 * AssessPlanBO.java
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		刘中帅
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.business.response;

import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.dao.Page;
import com.fhd.dao.icm.assess.MeasureRelaOrgDAO;
import com.fhd.dao.response.SolutionDAO;
import com.fhd.dao.response.SolutionRelaRiskDAO;
import com.fhd.dao.sys.i18n.I18nDAO;
import com.fhd.entity.icm.control.MeasureRelaOrg;
import com.fhd.entity.response.Solution;
import com.fhd.entity.response.SolutionRelaRisk;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.response.base.ResponseBaseBO;
import com.fhd.ra.interfaces.response.IResponseOutSiteBO;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 宋佳
 * @version 尝试性基类
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see
 */
@Service
public class ResponseListBO extends ResponseBaseBO implements IResponseOutSiteBO{
	@Autowired
	private ResponseSolutionBO o_responseSolutionBO;
	@Autowired
	private ResponseMeasureBO o_responseMeasureBO;
	@Autowired
	private MeasureRelaOrgDAO o_measureRelaOrgDAO;
	@Autowired
	private SolutionRelaRiskDAO o_solutionRelaRiskDAO;
	@Autowired
	private SolutionDAO o_solutionDAO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	/**
	 * 查询控制措施关联的机构和人员
	 */
	@SuppressWarnings("unchecked")
	public List<MeasureRelaOrg> findOrgAndEmpByMeasureId(String measureId){
		Criteria cra = o_measureRelaOrgDAO.createCriteria();
		cra.createAlias("controlMeasure", "controlMeasure");
		cra.add(Restrictions.eq("controlMeasure.deleteStatus", Contents.DELETE_STATUS_USEFUL));
//		cra.add(Restrictions.isNotNull("org"));
		if(StringUtils.isNotBlank(measureId)){
			cra.add(Restrictions.eq("controlMeasure.id", measureId));
		}
		return cra.list();
	}
	/**
	 * outsite interface
	 * 查询所有风险的应对措施列表
	 * 
	 * return
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<SolutionRelaRisk> findSolutionsByRisk(List<String> riskIds) {
		Criteria cra = o_solutionRelaRiskDAO.createCriteria();
		cra.createAlias("solution", "solution");
		cra.createAlias("risk", "risk");
		cra.setFetchMode("risk", FetchMode.JOIN);
		cra.setFetchMode("solution", FetchMode.JOIN);
		if(riskIds != null && riskIds.size() > 0){
			cra.add(Restrictions.in("risk.id", riskIds));
		}
		cra.add(Restrictions.eq("risk.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		cra.add(Restrictions.eq("solution.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		cra.addOrder(Order.asc("risk.id"));
		return cra.list();
	}
	/**
	 * 在上个方法中增加两个过滤条件
	 * 在工作流的应对查询中要屏蔽掉自动任务和已处理过的应对措施
	 * params riskIds : 所有的风险id
	 * status : 应对的执行状态  
	 * type : 应对的类型（自动还是计划） 
	 * return
	 */
	
	@SuppressWarnings("unchecked")
	public List<SolutionRelaRisk> findSolutionsByRisk(List<String> riskIds,String status,String type,String empId,String businessId) {
		Criteria cra = o_solutionRelaRiskDAO.createCriteria();
		cra.createAlias("solution", "solution");
		cra.createAlias("risk", "risk");
		cra.setFetchMode("risk", FetchMode.JOIN);
		cra.setFetchMode("solution", FetchMode.JOIN);
		if(riskIds != null && riskIds.size() > 0){
			cra.add(Restrictions.in("risk.id", riskIds));
		}
		cra.add(Restrictions.eq("solution.type", type));
		cra.add(Restrictions.eq("solution.status", status));
		cra.add(Restrictions.eq("risk.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		cra.add(Restrictions.eq("solution.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		cra.add(Restrictions.eq("solution.riskAssessPlan.id", businessId));
		if(StringUtils.isNotBlank(empId)){
			cra.add(Restrictions.eq("solution.createBy.id", empId));
		}
		cra.addOrder(Order.asc("risk.id"));
		return cra.list();
	}
	/**
	 * 根据id 删除控制措施
	 * add by 宋佳
	 * 
	 */
	public int deleteAllRelaFromSolutionBySolutionId(String ids){
		// 删除关联应对和文件关联
		String[] arr = ids.split(",");
		List<String> idList = Arrays.asList(arr);
		o_responseSolutionBO.deleteFilesFromSolutionBySolutionId(idList);
		// 删除应对和风险关联
		o_responseSolutionBO.deleteRisksFromSolutionBySolutionId(idList);
		// 删除应对和机构关联
		o_responseSolutionBO.deleteOrgAndEmpFromSolutionBySolutionId(idList);
		// 删除应对
		return o_responseSolutionBO.deleteSolutionsBySolutionId(idList);
	}
	/**
	 * 根据id 删除控制措施
	 * add by 宋佳
	 * 
	 */
	public int deleteAllRelaFromMeasureByMeasureId(String ids){
		// 删除关联应对和文件关联
		String[] arr = ids.split(",");
		List<String> idList = Arrays.asList(arr);
		// 删除应对和风险关联
		o_responseMeasureBO.removeRisksFromMeasureByMeasureId(idList);
		// 删除应对和机构关联
		o_responseMeasureBO.removeOrgAndEmpFromMeasureByMeasureId(idList);
		// 删除应对
		return o_responseMeasureBO.removeMeasuresByMeasureId(idList);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findResponseListPageByType(int limit, int start, String sort, String dir, String selectId,String type,String queryJson) {
 		StringBuilder hql = new StringBuilder();
 		hql.append("SELECT rm.PARENT_ID,rm.id,rm.RISK_NAME,mea.id measureId,mea.MEASURE_NAME,mea.type type,mea.status FROM t_rm_risks rm "+ 
 				 "INNER JOIN (SELECT c.RISK_ID,c.CONTROL_MEASURE_ID FROM T_CON_MEASURE_RELA_RISK c UNION SELECT d.RISK_ID,d.SOLUTION_ID FROM T_RM_SOLUTION_RELA_RISK d,T_RM_SOLUTION c WHERE d.SOLUTION_ID = c.ID "+ this.getHigherQueryFromQueryForm(queryJson).get("solutionQuery") +" ) mearm ON rm.id = mearm.RISK_ID " +
				 "INNER JOIN (SELECT a.id,a.MEASURE_NAME,meaOrg.ORG_ID orgId,'M' type,estatus status FROM T_CON_CONTROL_MEASURE a  INNER JOIN T_CON_MEASURE_RELA_ORG meaOrg ON meaOrg.CONTROL_MEASURE_ID = a.ID WHERE a.DELETE_ESTATUS = :meaDelStatus AND meaOrg.ETYPE = :meaOrgType   UNION SELECT c.id,c.SOLUTION_NAME,solOrg.ORG_ID,'S' type,estatus FROM T_RM_SOLUTION c INNER JOIN T_RM_SOLUTION_RELA_ORG solOrg ON c.id =  solOrg.SOLUTION_ID WHERE c.DELETE_STATUS = :solDelStatus AND solOrg.etype = :solOrgType "+ this.getHigherQueryFromQueryForm(queryJson).get("solutionQuery") +") mea  ON mearm.CONTROL_MEASURE_ID = mea.ID");
 		hql.append(" WHERE orgId = :orgId ");
 		hql.append( getHigherQueryFromQueryForm(queryJson).get("riskQuery"));
 		hql.append(" ORDER BY rm.id,mea.MEASURE_NAME DESC ");
 		WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();//SPRING-BEAN服务
 		I18nDAO o_I18nDAO = applicationContext.getBean(I18nDAO.class);
 		Session session = o_I18nDAO.getSessionFactory().openSession();//HIBERNATE-SESSION服务
 		Query hqlQuery = session.createSQLQuery(hql.toString());
 		hqlQuery.setParameter("meaDelStatus",Contents.DELETE_STATUS_USEFUL);
 		hqlQuery.setParameter("meaOrgType",Contents.ORG_RESPONSIBILITY);
 		hqlQuery.setParameter("solDelStatus",Contents.DELETE_STATUS_USEFUL);
 		hqlQuery.setParameter("solOrgType",Contents.ORG_RESPONSIBILITY);
 		hqlQuery.setParameter("orgId",selectId);
 		hqlQuery.setFirstResult(start);
 		hqlQuery.setMaxResults(limit);
 		return hqlQuery.list();
	}
	
	/**
	 * 查询应对措施和控制措施列表
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public Map<String,Object> findResponseListByType(int limit, int start, String sort, String dir, String selectId,String type,String businessType,String queryJson,String query) {
	    Map<String,Object> map = new HashMap<String,Object>();
	    String companyId = UserContext.getUser().getCompanyid();
	    String sql = "";
	    sql = sql + " select list.RPARENT,list.RID,list.RNAME,list.SID,list.SNAME,list.STYPE,list.SSTATUS,list.ARCHIVESTATUS from (";
	    //风险应对
	    sql = sql + " SELECT solu.ID SID,solu.SOLUTION_NAME SNAME,solu.ESTATUS SSTATUS,'solution' STYPE,solu.ARCHIVE_STATUS ARCHIVESTATUS,sorisk.RISK_ID RID,risk.RISK_NAME RNAME,risk.PARENT_ID RPARENT ";
	    sql = sql + " FROM t_rm_solution solu ";
	    sql = sql + " LEFT JOIN t_rm_solution_rela_risk sorisk ON solu.ID = sorisk.SOLUTION_ID ";
	    sql = sql + " LEFT JOIN t_rm_risks risk on sorisk.RISK_ID = risk.ID ";
	    if("dept".equals(type)){
	        sql = sql + " LEFT JOIN t_rm_solution_rela_org soorg on solu.ID = soorg.SOLUTION_ID ";
	        sql = sql + " LEFT JOIN t_sys_organization org on soorg.ORG_ID = org.ID ";
	        sql = sql + " where solu.DELETE_STATUS = :deleteStatus and soorg.ORG_ID = :selectId ";
	    }else if("risk".equals(type)){
	        sql = sql + " where solu.DELETE_STATUS = :deleteStatus and sorisk.RISK_ID = :selectId ";
	    }else if("all".equals(type)){
	        if(!"all".equals(selectId) && !"other".equals(selectId)){
	            sql = sql + " where solu.DELETE_STATUS = :deleteStatus and solu.ARCHIVE_STATUS=:selectId ";
	        }else if("other".equals(selectId)){
                sql = sql + " where solu.DELETE_STATUS = :deleteStatus and solu.ARCHIVE_STATUS is null ";
            }else{
	            sql = sql + " where solu.DELETE_STATUS = :deleteStatus and 1=1 ";
	        }
	    }else{
	        sql = sql + " where solu.DELETE_STATUS = :deleteStatus and 1!=1 ";
	    }
	    sql = sql + " and solu.COMPANY_ID = :companyId";
	    sql = sql + " UNION ";
	    //控制措施
	    sql = sql + " select mea.ID SID,mea.MEASURE_NAME SNAME,mea.ESTATUS SSTATUS,'measure' STYPE,mea.ARCHIVE_STATUS ARCHIVESTATUS ,mearisk.RISK_ID RID,risk.RISK_NAME RNAME,risk.PARENT_ID RPARENT  ";
	    sql = sql + " from T_CON_CONTROL_MEASURE mea ";
	    sql = sql + " LEFT JOIN t_con_measure_rela_risk mearisk ON mea.ID = mearisk.CONTROL_MEASURE_ID  ";
	    sql = sql + " LEFT JOIN t_rm_risks risk ON mearisk.RISK_ID = risk.ID ";
	    if("dept".equals(type)){
	        sql = sql + " LEFT JOIN t_con_measure_rela_org meaorg ON mea.ID = meaorg.CONTROL_MEASURE_ID ";
            sql = sql + " LEFT JOIN t_sys_organization org ON meaorg.ORG_ID = org.ID ";
	        sql = sql + " where mea.DELETE_ESTATUS = :deleteStatus and meaorg.ORG_ID = :selectId ";
	    }else if("risk".equals(type)){
	        sql = sql + " where mea.DELETE_ESTATUS = :deleteStatus and mearisk.RISK_ID = :selectId ";
	    }else if("all".equals(type)){
	        if(!"all".equals(selectId) && !"other".equals(selectId)){
	            sql = sql + " where mea.DELETE_ESTATUS = :deleteStatus  and mea.ARCHIVE_STATUS=:selectId ";
	        }else if("other".equals(selectId)){
                sql = sql + " where mea.DELETE_ESTATUS = :deleteStatus  and mea.ARCHIVE_STATUS is null ";
            }else{
	            sql = sql + " where mea.DELETE_ESTATUS = :deleteStatus  and 1=1 ";
	        }
	    }else{
	        sql = sql + " where mea.DELETE_ESTATUS = :deleteStatus  and 1!=1 ";
	    }
	    sql = sql + " and mea.COMPANY_ID = :companyId";
	    sql = sql + " ) list where 1=1 ";
	    if(StringUtils.isNotBlank(businessType) && !"analysis".equals(businessType)){
	        sql = sql + " and list.STYPE ='"+businessType+"'";
	    }
	    if(StringUtils.isNotBlank(query)){
	        sql = sql + " and (list.RNAME like '%"+query+"%' or list.SNAME like '%"+query+"%')";
	    }
	    sql = sql + " ORDER BY list.SSTATUS desc,list.SNAME asc ";
	    
	    SQLQuery sqlQueryCount = o_solutionDAO.createSQLQuery("select count(1) from ("+sql+") coun");
	    sqlQueryCount.setParameter("deleteStatus",Contents.DELETE_STATUS_USEFUL);
	    sqlQueryCount.setParameter("companyId",companyId);
	    if(!"all".equals(selectId) && !"other".equals(selectId)){
	        sqlQueryCount.setParameter("selectId",selectId);
	    }
	    map.put("count", sqlQueryCount.list().get(0).toString());
	    SQLQuery sqlQuery = o_solutionDAO.createSQLQuery(sql);
	    sqlQuery.setParameter("deleteStatus",Contents.DELETE_STATUS_USEFUL);
	    if(!"all".equals(selectId) && !"other".equals(selectId)){
	        sqlQuery.setParameter("selectId",selectId);
	    }
	    sqlQuery.setParameter("companyId",companyId);
	    sqlQuery.setFirstResult(start);
	    sqlQuery.setMaxResults(limit);
	    map.put("list", sqlQuery.list());
	    return map;
	}
	
	/**
	 * 通过qbc 查找所有应对措施 （不包括控制措施）
	 * @author 宋佳
	 * @param page
	 * @param sort
	 * @param dir
	 * @param query 查询条件
	 * @return Page<Solution>
	 * @since fhd　Ver 1.1
	 */
	public Page<Solution> findSolutionsByPage(Page<Solution> page, String sort, String dir, String query,String status) {
		DetachedCriteria dc = DetachedCriteria.forClass(Solution.class);
		dc.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		dc.add(Restrictions.eq("isAddPresolution.id", Contents.DICT_Y));
		dc.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		if(StringUtils.isNotBlank(query)){
			dc.add(Restrictions.or(Property.forName("code").like(query, MatchMode.ANYWHERE),Property.forName("name").like(query, MatchMode.ANYWHERE)));
		}
		dc.addOrder(Order.desc("createTime"));
		return o_solutionDAO.findPage(dc, page, false);
	}
	
	/**
	 * 通过sql 查询风险关联的应对措施内容（不包括）
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findSolutionListByRiskIds(String businessId,List<String> riskIds,String empId) {
		StringBuilder hql = new StringBuilder();
 		hql.append("SELECT rm.PARENT_ID,rm.id,rm.RISK_NAME,mea.id measureId,mea.SOLUTION_NAME,mea.type type FROM t_rm_risks rm "+ 
 				   "LEFT JOIN (SELECT d.RISK_ID,d.SOLUTION_ID FROM T_RM_SOLUTION_RELA_RISK d,T_RM_SOLUTION c WHERE d.SOLUTION_ID = c.ID AND c.etype = :type AND c.estatus = :status AND c.CREATE_BY = :empId and c.RESPONSE_PLAN_ID = :planId ) mearm ON rm.id = mearm.RISK_ID " +
 				   "LEFT JOIN (SELECT b.id,b.SOLUTION_NAME,'E' type FROM T_RM_SOLUTION b WHERE b.DELETE_STATUS = :deleteStatus AND b.etype = :type AND b.estatus = :status AND b.CREATE_BY = :empId and b.RESPONSE_PLAN_ID = :planId ) mea ON mearm.SOLUTION_ID = mea.ID");
 		if(riskIds != null && riskIds.size() >0){
 		    hql.append(" WHERE rm.id IN (:riskIds)");
        }else{
            hql.append(" WHERE 1!=1");
        }
 		hql.append(" ORDER BY rm.id,mea.SOLUTION_NAME DESC ");
 		Query hqlQuery = o_solutionDAO.createSQLQuery(hql.toString());
		hqlQuery.setParameter("deleteStatus",Contents.DELETE_STATUS_USEFUL);
		hqlQuery.setParameter("type",Contents.SOLUTION_PLAN);
		hqlQuery.setParameter("status",Contents.DEAL_STATUS_NOTSTART);
		hqlQuery.setParameter("planId",businessId);
		
		hqlQuery.setParameter("empId",empId);
 		if(riskIds != null && riskIds.size() >0){
 			hqlQuery.setParameterList("riskIds", riskIds);
 		}
 		return hqlQuery.list();
	}
	/**
	 * 通过sql 查询风险关联的应对措施内容（不包括）
	 * add by 王再冉
	 * 2014-2-11  下午2:18:51
	 * desc : 
	 * @param businessId
	 * @param riskIds
	 * @param empIds
	 * @return 
	 * List<Object[]>
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findSolutionListByRiskIdsAndEmpIds(String businessId,List<String> riskIds,List<String> empIds) {
		StringBuilder hql = new StringBuilder();
 		hql.append("SELECT rm.PARENT_ID,rm.id,rm.RISK_NAME,mea.id measureId,mea.SOLUTION_NAME,mea.type type FROM t_rm_risks rm "+ 
 				   "LEFT JOIN (SELECT d.RISK_ID,d.SOLUTION_ID FROM T_RM_SOLUTION_RELA_RISK d,T_RM_SOLUTION c WHERE d.SOLUTION_ID = c.ID AND c.etype = :type AND c.estatus = :status AND c.CREATE_BY IN (:empId) and c.RESPONSE_PLAN_ID = :planId ) mearm ON rm.id = mearm.RISK_ID " +
 				   "LEFT JOIN (SELECT b.id,b.SOLUTION_NAME,'E' type FROM T_RM_SOLUTION b WHERE b.DELETE_STATUS = :deleteStatus AND b.etype = :type AND b.estatus = :status AND b.CREATE_BY IN (:empId) and b.RESPONSE_PLAN_ID = :planId ) mea ON mearm.SOLUTION_ID = mea.ID");
 		hql.append(" WHERE rm.id IN (:riskIds)");
 		hql.append(" ORDER BY rm.id,mea.SOLUTION_NAME DESC ");
 		Query hqlQuery = o_solutionDAO.createSQLQuery(hql.toString());
		hqlQuery.setParameter("deleteStatus",Contents.DELETE_STATUS_USEFUL);
		hqlQuery.setParameter("type",Contents.SOLUTION_PLAN);
		hqlQuery.setParameter("status",Contents.DEAL_STATUS_NOTSTART);
		hqlQuery.setParameter("planId",businessId);
		
		hqlQuery.setParameterList("empId",empIds);
 		if(riskIds != null && riskIds.size() >0){
 			hqlQuery.setParameterList("riskIds", riskIds);
 			return hqlQuery.list();
 		}else{
 			return null;
 		}
 		
	}
	
	/**
	 * 通过bussinessId 和 executionId 来获取风险ID
	 * 
	 * @author 张健
	 * @param businessId 计划ID
	 * @param empId 人员ID
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRiskIdsByEmpIds(String businessId,String empId){
		//获取计划制定人
		String hqlSQL = "SELECT b.risk_id FROM t_rm_rang_object_dept_emp a INNER JOIN t_rm_risk_score_object b  " +
				"ON a.SCORE_OBJECT_ID = b.ID WHERE SCORE_EMP_ID = :empId " +
				"AND b.assess_plan_id = :businessId";
		Query hqlQuery = o_solutionDAO.createSQLQuery(hqlSQL);
		hqlQuery.setParameter("businessId", businessId);
		hqlQuery.setParameter("empId", empId);
		return hqlQuery.list();
	}
	
	/**
	 * 
	 * add by 王再冉
	 * 2014-2-11  下午2:13:50
	 * desc : 
	 * @param businessId
	 * @param empId
	 * @return 
	 * List<String>
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRiskIdsByEmpIdListAndBusinessId(String businessId,List<String> empIds){
		//获取计划制定人
		String hqlSQL = "SELECT b.risk_id FROM t_rm_rang_object_dept_emp a INNER JOIN t_rm_risk_score_object b  " +
				"ON a.SCORE_OBJECT_ID = b.ID WHERE SCORE_EMP_ID IN (:empIds) " +
				"AND b.assess_plan_id = :businessId";
		Query hqlQuery = o_solutionDAO.createSQLQuery(hqlSQL);
		hqlQuery.setParameter("businessId", businessId);
		hqlQuery.setParameterList("empIds", empIds);
		return hqlQuery.list();
	}
	
	/**
	 * 查询该计划下所有的风险id
	 * 
	 * @author 张健
	 * @param businessId 计划ID
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRiskIdsByBusinessId(String businessId){
		//获取当前登录人
		String hqlSQL = "SELECT b.risk_id FROM t_rm_rang_object_dept_emp a INNER JOIN t_rm_risk_score_object b  " +
				"ON a.SCORE_OBJECT_ID = b.ID WHERE b.assess_plan_id = :businessId";
		Query hqlQuery = o_solutionDAO.createSQLQuery(hqlSQL);
		hqlQuery.setParameter("businessId", businessId);
		return hqlQuery.list();
	}
}
