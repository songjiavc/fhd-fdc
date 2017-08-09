package com.fhd.comm.business.bpm.jbpm;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.jbpm.api.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.dao.bpm.JbpmExecutionDAO;
import com.fhd.dao.bpm.JbpmHistProcinstDAO;
import com.fhd.entity.bpm.JbpmExecution;
import com.fhd.entity.bpm.JbpmHistProcinst;
@Service
@SuppressWarnings("unchecked")
public class JbpmHistProcinstBO{
	@Autowired
	private JbpmHistProcinstDAO o_jbpmHistProcinstDAO;
	@Autowired
	private JBPMOperate o_jBPMOperate;
	@Autowired
	private JbpmExecutionDAO o_jbpmExecutionDAO;
	public void editSubJbpmHistProcinst(String executionId){
		/**
		 * 判断是否产生子流程，如产生则赋予子关系
		 */
		List<JbpmExecution> subExecutionList = this.findAllSubProcinstExecutionByExecutionId(executionId);
		for (JbpmExecution jbpmExecution : subExecutionList) {
			String id_ = jbpmExecution.getId_();
			Execution executionTemp = o_jBPMOperate.getExecutionService().findExecutionById(id_);
			List<JbpmHistProcinst> subJbpmHistProcinstList = this.findJbpmHistProcinstByProcessInstanceId(executionTemp.getSubProcessInstance().getId());
			JbpmHistProcinst subJbpmHistProcinst = subJbpmHistProcinstList.get(0);
			JbpmHistProcinst jbpmHistProcinst = jbpmExecution.getJbpmHistProcinst();
			subJbpmHistProcinst.setKey(jbpmHistProcinst.getKey()+subJbpmHistProcinst.getId()+".");
			o_jbpmHistProcinstDAO.merge(subJbpmHistProcinst);
		}
	}
	/**
	 * 根据流程实例ID 查询JbpmHistProcinst
	 * @param processInstanceId
	 * @return
	 */
	public List<JbpmHistProcinst> findJbpmHistProcinstByProcessInstanceId(String processInstanceId){
		Criteria criteria = o_jbpmHistProcinstDAO.createCriteria();
		criteria.add(Restrictions.eq("id_", processInstanceId));
		return criteria.list();
	}
	
	/**
	 * 
	 * findAllSubProcinstExecutionByExecutionId:根据父执行ID查询所有有子流程的流程执行
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<JbpmExecution> findAllSubProcinstExecutionByExecutionId(String executionId) {
		Criteria criteria = o_jbpmExecutionDAO.createCriteria();
		criteria.add(Restrictions.isNotNull("subprocinst"));
		criteria.add(Restrictions.like("id_", executionId,MatchMode.ANYWHERE));
		List<JbpmExecution> list = criteria.list();
		return list;
	}
	

	/**
	 * 
	 * findJbpmHistProcinstPageBySome:分页条件查询历史定义
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param businessName
	 * @param jbpmDeploymentName
	 * @param dbversion 执行状态：0-未执行，1-已执行
	 * @param assigneeId 執行人
	 * @param sort
	 * @param dir
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<JbpmHistProcinst> findJbpmHistProcinstPageBySome(Page<JbpmHistProcinst> page,String query,String businessName,String jbpmDeploymentName,String startEmpName,Long dbversion,String assigneeId,List<Map<String, String>> sortList){
		DetachedCriteria dc=DetachedCriteria.forClass(JbpmHistProcinst.class);
		dc.createAlias("businessWorkFlow", "businessWorkFlow");
		dc.createAlias("vJbpmDeployment", "vJbpmDeployment");
		dc.createAlias("vJbpmDeployment.processDefinitionDeploy", "processDefinitionDeploy");
		dc.setFetchMode("businessWorkFlow.createByEmp", FetchMode.JOIN);
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("businessWorkFlow.businessName", query,MatchMode.ANYWHERE));
			disjunction.add(Restrictions.like("processDefinitionDeploy.disName", query,MatchMode.ANYWHERE));
			if("已删除".equals(query)){
				disjunction.add(Restrictions.eq("endactivity", "remove1"));
			}else if("已完成".equals(query)){
				disjunction.add(Restrictions.eq("endactivity", "end1"));
			}else if("执行中".equals(query)){
				disjunction.add(Restrictions.isNull("endactivity"));
			}
			dc.add(disjunction);
		}
		if(StringUtils.isNotBlank(businessName)){
			dc.add(Restrictions.like("businessWorkFlow.businessName", businessName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(jbpmDeploymentName)){
			dc.add(Restrictions.like("processDefinitionDeploy.disName", jbpmDeploymentName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(startEmpName)){
			dc.add(Restrictions.like("businessWorkFlow.createBy", startEmpName, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(assigneeId)||dbversion!=null){
			Criteria criteria = o_jbpmHistProcinstDAO.createCriteria();
			if(StringUtils.isNotBlank(assigneeId)){
				criteria.createAlias("jbpmHistActinsts", "jbpmHistActinsts");
				criteria.createAlias("jbpmHistActinsts.jbpmHistTask", "jbpmHistTask");
				criteria.add(Restrictions.or(Restrictions.eq("jbpmHistTask.taskAssignee.id", assigneeId), Restrictions.eq("jbpmHistTask.assignee.id", assigneeId)));
			}
			if(dbversion!=null){
				if(StringUtils.isBlank(assigneeId)){
					criteria.createAlias("jbpmHistActinsts", "jbpmHistActinsts");
				}
				criteria.add(Restrictions.eq("jbpmHistActinsts.dbversion", dbversion));
			}
			criteria.setProjection(Property.forName("id"));
			List<String> list = criteria.list();
			if(null!=list&&list.size()!=0){
				dc.add(Property.forName("id").in(list));
			}else{
				dc.add(Property.forName("id").isNull());
			}
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("businessName".equalsIgnoreCase(property)){
						property="businessWorkFlow.businessName";
					}else if("createTime".equalsIgnoreCase(property)){
						property="businessWorkFlow.createTime";
					}else if("jbpmDeploymentName".equalsIgnoreCase(property)){
						property="processDefinitionDeploy.disName";
					}else if("endactivityShow".equalsIgnoreCase(property)){
						property="endactivity";
					}
					if("desc".equalsIgnoreCase(direction)){
						dc.addOrder(Order.desc(property));
					}else{
						dc.addOrder(Order.asc(property));
					}
				}
			}
		}
		return o_jbpmHistProcinstDAO.findPage(dc, page,false);
	}
	
	/**
	 * 
	 * findJbpmHistProcinstBySome:条件查询历史定义
	 * 
	 * @author 杨鹏
	 * @param id_:流程实例标示
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<JbpmHistProcinst> findJbpmHistProcinstBySome(String id_){
		Criteria criteria = o_jbpmHistProcinstDAO.createCriteria();
		if(id_!=null){
			criteria.add(Restrictions.eq("id_", id_));
		}
		return criteria.list();
	}
	
	/**
	 * 
	 * findJbpmHistProcinstById:根据ID查询历史流程定义
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public JbpmHistProcinst findJbpmHistProcinstById(Long id){
		return o_jbpmHistProcinstDAO.get(id);
	}
	/**
	 * 
	 * merge:保存方法
	 * 
	 * @author 杨鹏
	 * @param jbpmHistProcinst
	 * @since  fhd　Ver 1.1
	 */
	public void merge(JbpmHistProcinst jbpmHistProcinst) {
		o_jbpmHistProcinstDAO.merge(jbpmHistProcinst);
	}
}