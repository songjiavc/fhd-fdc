package com.fhd.comm.business.bpm.jbpm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.dao.bpm.VJbpmHistActinstDAO;
import com.fhd.entity.bpm.view.VJbpmHistActinst;
@Service
@SuppressWarnings("unchecked")
public class VJbpmHistActinstBO{

	@Autowired
	private VJbpmHistActinstDAO o_vJbpmHistActinstDAO;
	/**
	 * 
	 * findBySome:
	 * 
	 * @author 杨鹏
	 * @param mainJbpmHistProcinstId
	 * @param type
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<VJbpmHistActinst> findBySome(Long mainJbpmHistProcinstId,String type){
		Criteria criteria = o_vJbpmHistActinstDAO.createCriteria();
		criteria.add(Restrictions.like("jbpm4HistProcinstKey", "."+mainJbpmHistProcinstId+".", MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("type", type));
		return criteria.list();
	}
	/**
	 * 
	 * findPageBySome:
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param mainJbpmHistProcinstId
	 * @param type
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<VJbpmHistActinst> findPageBySome(Page<VJbpmHistActinst> page,Long mainJbpmHistProcinstId,String type, List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(VJbpmHistActinst.class);
		if(mainJbpmHistProcinstId!=null){
			criteria.add(Restrictions.or(Restrictions.like("jbpmHistProcinstKey", "."+mainJbpmHistProcinstId+".", MatchMode.ANYWHERE), Restrictions.eq("jbpmHistProcinstId", mainJbpmHistProcinstId)));
		}
		if(StringUtils.isNotBlank(type)){
			criteria.add(Restrictions.eq("type", type));
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if("dbversionStr".equals(property)){
					property="dbversion";
				}else if("startStr".equals(property)){
					property="start";
				}else if("endStr".equals(property)){
					property="end";
				}
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}
		return o_vJbpmHistActinstDAO.findPage(criteria, page,false);
	}
	public Page<VJbpmHistActinst> findPageBySome(String query,Long mainJbpmHistProcinstId,String type, Integer limit, Integer start,List<Map<String, String>> sortList) {
		Page<VJbpmHistActinst> page=new Page<VJbpmHistActinst>();
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer count = new StringBuffer("");
		StringBuffer select = new StringBuffer("");
		StringBuffer sql = new StringBuffer("");
		count.append("	SELECT");
		count.append("		COUNT(*)");
		select.append("	SELECT");
		select.append("		JBPM4_HIST_ACTINST.DBID_,");
		select.append("		JBPM4_HIST_ACTINST.ACTIVITY_NAME_,");
		select.append("		JBPM4_HIST_ACTINST.EXECUTION_,");
		select.append("		JBPM4_HIST_ACTINST.DBVERSION_,");
		select.append("		JBPM4_HIST_ACTINST.START_,");
		select.append("		JBPM4_HIST_ACTINST.END_,");
		select.append("		JBPM4_HIST_ACTINST.HPROCI_,");
		select.append("		JBPM4_HIST_ACTINST.HTASK_,");
		select.append("		JBPM4_HIST_ACTINST.TYPE_,");
		select.append("		JBPM4_HIST_ACTINST.TRANSITION_,");
		select.append("		JBPM4_HIST_PROCINST.KEY_ JBPM4_HIST_PROCINST_KEY_,");
		select.append("		JBPM4_HIST_TASK.ASSIGNEE_ JBPM4_HIST_TASK_ASSIGNEE_,");
		select.append("		T_JBPM_EXAMINE_APPROVE_IDEA.EA_CONTENT EA_CONTENT,");
		select.append("		T_SYS_EMPLOYEE.EMP_CODE T_SYS_EMPLOYEE_EMP_CODE,");
		select.append("		T_SYS_EMPLOYEE.REAL_NAME T_SYS_EMPLOYEE_REAL_NAME,");
		select.append("		COMPANY.ID COMPANY_ID,");
		select.append("		COMPANY.ORG_NAME COMPANY_NAME,");
		select.append("		ORG.ID ORG_ID,");
		select.append("		ORG.ORG_NAME ORG_NAME");
		sql.append("	FROM JBPM4_HIST_ACTINST");
		sql.append("	LEFT JOIN JBPM4_HIST_PROCINST ON JBPM4_HIST_PROCINST.DBID_=JBPM4_HIST_ACTINST.HPROCI_");
		sql.append("	LEFT JOIN JBPM4_HIST_TASK ON JBPM4_HIST_TASK.DBID_ = JBPM4_HIST_ACTINST.HTASK_");
		sql.append("	LEFT JOIN T_JBPM_EXAMINE_APPROVE_IDEA ON T_JBPM_EXAMINE_APPROVE_IDEA.TASK_ID=JBPM4_HIST_TASK.DBID_");
		sql.append("	LEFT JOIN T_SYS_EMPLOYEE ON T_SYS_EMPLOYEE.ID = JBPM4_HIST_TASK.ASSIGNEE_");
		sql.append("	LEFT JOIN T_SYS_EMP_ORG ON T_SYS_EMP_ORG.EMP_ID=T_SYS_EMPLOYEE.ID AND T_SYS_EMP_ORG.ISMAIN=1");
		sql.append("	LEFT JOIN T_SYS_ORGANIZATION ORG ON ORG.ID=T_SYS_EMP_ORG.ORG_ID");
		sql.append("	LEFT JOIN T_SYS_ORGANIZATION COMPANY ON COMPANY.ID = T_SYS_EMPLOYEE.ORG_ID");
		sql.append("	WHERE");
		sql.append("		1=1");
		if(StringUtils.isNotBlank(query)){
			sql.append("	AND JBPM4_HIST_ACTINST.ACTIVITY_NAME_ LIKE CONCAT('%',CONCAT(:query,'%'))");
			values.put("query",query);
		}
		if(mainJbpmHistProcinstId!=null){
			sql.append("	AND (JBPM4_HIST_PROCINST.KEY_ LIKE CONCAT('%',CONCAT(:mainJbpmHistProcinstId,'%')) OR JBPM4_HIST_PROCINST.DBID_=:mainJbpmHistProcinstId)");
			values.put("mainJbpmHistProcinstId",mainJbpmHistProcinstId);
		}
		if(StringUtils.isNotBlank(type)){
			sql.append("	AND JBPM4_HIST_ACTINST.TYPE_=:type");
			values.put("type",type);
		}
		if(sortList!=null&&sortList.size()>0){
			sql.append("	ORDER BY");
			List<String> sortSqlList = new ArrayList<String>();
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if("dbversionStr".equals(property)){
					property="JBPM4_HIST_ACTINST.DBVERSION_";
				}else if("startStr".equals(property)){
					property="JBPM4_HIST_ACTINST.START_";
				}else if("endStr".equals(property)){
					property="JBPM4_HIST_ACTINST.END_";
				}else if("activityName".equals(property)){
					property="JBPM4_HIST_ACTINST.ACTIVITY_NAME_";
				}
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						sortSqlList.add("	"+property+" DESC");
					}else{
						sortSqlList.add("	"+property+" ASC");
					}
				}
			}
			sql.append(StringUtils.join(sortSqlList.toArray(new String[sortSqlList.size()]),","));
		}
		count.append(sql);
		select.append(sql);
		SQLQuery countSqlQuery = o_vJbpmHistActinstDAO.createSQLQuery(count.toString(), values);
		List<Object> countList = countSqlQuery.list();
		page.setTotalItems(Long.valueOf(countList.get(0).toString()));
		SQLQuery selectSqlQuery = o_vJbpmHistActinstDAO.createSQLQuery(select.toString(), values);
		selectSqlQuery.setFirstResult(start);
		selectSqlQuery.setMaxResults(limit);
		List<Object[]> list = selectSqlQuery.list();
		List<VJbpmHistActinst> vJbpmHistActinstList = new ArrayList<VJbpmHistActinst>();
		for (Object[] objects : list) {
			VJbpmHistActinst vJbpmHistActinst = new VJbpmHistActinst();
			int i=0;
			Object idObj = objects[i++];
			if(idObj!=null){
				vJbpmHistActinst.setId(Long.valueOf(idObj.toString()));
			}
			Object activityNameObj = objects[i++];
			if(activityNameObj!=null){
				vJbpmHistActinst.setActivityName(activityNameObj.toString());
			}
			Object executionIdObj = objects[i++];
			if(executionIdObj!=null){
				vJbpmHistActinst.setExecutionId(executionIdObj.toString());
			}
			Object dbversionObj = objects[i++];
			if(dbversionObj!=null){
				vJbpmHistActinst.setDbversion(Long.valueOf(dbversionObj.toString()));
			}
			Object startObj = objects[i++];
			if(startObj!=null){
				vJbpmHistActinst.setStart((Date)startObj);
			}
			Object endObj = objects[i++];
			if(endObj!=null){
				vJbpmHistActinst.setEnd((Date)endObj);
			}
			Object jbpmHistProcinstIdObj = objects[i++];
			if(jbpmHistProcinstIdObj!=null){
				vJbpmHistActinst.setJbpmHistProcinstId(Long.valueOf(jbpmHistProcinstIdObj.toString()));
			}
			Object jbpmHistTaskIdObj = objects[i++];
			if(jbpmHistTaskIdObj!=null){
				vJbpmHistActinst.setJbpmHistTaskId(Long.valueOf(jbpmHistTaskIdObj.toString()));
			}
			Object typeObj = objects[i++];
			if(typeObj!=null){
				vJbpmHistActinst.setType(typeObj.toString());
			}
			Object transitionObj = objects[i++];
			if(transitionObj!=null){
				vJbpmHistActinst.setTransition(transitionObj.toString());
			}
			Object jbpmHistProcinstKeyObj = objects[i++];
			if(jbpmHistProcinstKeyObj!=null){
				vJbpmHistActinst.setJbpmHistProcinstKey(jbpmHistProcinstKeyObj.toString());
			}
			Object eaContentObj = objects[i++];
			if(eaContentObj!=null){
				vJbpmHistActinst.setEaContent(eaContentObj.toString());
			}
			Object assigneeIdObj = objects[i++];
			if(assigneeIdObj!=null){
				vJbpmHistActinst.setAssigneeId(assigneeIdObj.toString());
			}
			Object assigneeCodeObj = objects[i++];
			if(assigneeCodeObj!=null){
				vJbpmHistActinst.setAssigneeCode(assigneeCodeObj.toString());
			}
			Object assigneeRealNameObj = objects[i++];
			if(assigneeRealNameObj!=null){
				vJbpmHistActinst.setAssigneeRealName(assigneeRealNameObj.toString());
			}
			Object assigneeCompanyIdObj = objects[i++];
			if(assigneeCompanyIdObj!=null){
				vJbpmHistActinst.setAssigneeCompanyId(assigneeCompanyIdObj.toString());
			}
			Object assigneeCompanyNameObj = objects[i++];
			if(assigneeCompanyNameObj!=null){
				vJbpmHistActinst.setAssigneeCompanyName(assigneeCompanyNameObj.toString());
			}
			Object assigneeOrgIdObj = objects[i++];
			if(assigneeOrgIdObj!=null){
				vJbpmHistActinst.setAssigneeOrgId(assigneeOrgIdObj.toString());
			}
			Object assigneeOrgNameObj = objects[i++];
			if(assigneeOrgNameObj!=null){
				vJbpmHistActinst.setAssigneeOrgName(assigneeOrgNameObj.toString());
			}
			
			vJbpmHistActinstList.add(vJbpmHistActinst);
		}
		page.setResult(vJbpmHistActinstList);
		return page;
	}
}