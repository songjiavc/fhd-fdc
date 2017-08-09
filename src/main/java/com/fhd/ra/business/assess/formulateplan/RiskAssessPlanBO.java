package com.fhd.ra.business.assess.formulateplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.dao.Page;
import com.fhd.dao.assess.formulatePlan.RiskAccessPlanDAO;
import com.fhd.dao.assess.formulatePlan.RiskCircuseeDAO;
import com.fhd.dao.assess.kpiSet.RangObjectDeptEmpDAO;
import com.fhd.dao.bpm.JbpmHistActinstDAO;
import com.fhd.dao.risk.TemplateDAO;
import com.fhd.dao.sys.autho.SysoUserDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskCircusee;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.CircuseeBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.interfaces.assess.formulateplan.IRiskAssessPlanBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;

/**
 * 计划任务BO
 * @author 王再冉
 *
 */
@Service
public class RiskAssessPlanBO implements IRiskAssessPlanBO{
	@Autowired
	private RiskAccessPlanDAO o_planDAO;
	@Autowired
	private TemplateDAO o_templateDAO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private RiskCircuseeDAO o_riskcircuseeDAO;
	@Autowired
	private SysoUserDAO o_sysoUserDAO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private CircuseeBO o_circuseeBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired
	private JbpmHistActinstDAO o_jbpmHistActinstDAO;
	@Autowired
	private RangObjectDeptEmpDAO o_rangObjectDeptEmpDAO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	
	@Autowired
	private OrgGridBO o_orgGridBO;
	
	/**
	 * 查询计划任务列表
	 * @param planName	计划任务名称
	 * @param page
	 * @param sort
	 * @param dir
	 * @return
	 */
	@RecordLog("查询计划列表")
	public Page<RiskAssessPlan> findPlansPageBySome(String query, Page<RiskAssessPlan> page,
								String companyId, String planType, String status,String schm) {
		DetachedCriteria dc = DetachedCriteria.forClass(RiskAssessPlan.class);
		if(null != companyId){
			dc.add(Restrictions.eq("company.id", companyId));
		}
		/*if(null != workType){
			dc.add(Restrictions.eq("workType", workType));
		}else{
			dc.add(Restrictions.or(Restrictions.isNull("workType"), Restrictions.ne("workType", "assess_work_type_project")));
		}*/
		if(StringUtils.isNotBlank(planType)){
			dc.add(Restrictions.eq("planType", planType));
		}
		if(StringUtils.isNotBlank(status)){
			dc.add(Restrictions.eq("dealStatus", status));
		}
		//schm分库标志
		if(StringUtils.isNotBlank(schm)){
			dc.add(Restrictions.eq("schm", schm));
			if("dept".equals(schm)){
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				dc.add(Restrictions.eq("createOrg", deptId));
			}
		}
		dc.addOrder(Order.desc("planCreatTime"));//按计划创建时间排序
		return o_planDAO.findPage(dc, page, false);
	}
	/**
	 * 保存计划
	 * @param riskPlan
	 */
	@Transactional
	@RecordLog("保存计划")
	public void saveRiskAssessPlan(RiskAssessPlan riskPlan) {
		 o_planDAO.merge(riskPlan);
		 
	}
	/**
	 * 更新计划
	 * @param riskPlan
	 */
	@Transactional
	@RecordLog("修改计划")
	public void mergeRiskAssessPlan(RiskAssessPlan riskPlan) {
		 o_planDAO.merge(riskPlan);
		 
	}
	/**
	 * 批量删除计划任务
	 * @param ids
	 */
	 @Transactional
	 public void removeRiskAssessPlansByIds(List<String> ids) {
		 for(String id : ids){
			 o_planDAO.delete(id);
		 }
	}
	/**
	 * 根据模板名称查询模板
	 * @param tempName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Template findTemplateById(String tempId){
		Criteria criteria = o_templateDAO.createCriteria();
		List<Template> tempList = null;
		if(StringUtils.isNotBlank(tempId)){
			criteria.add(Restrictions.eq("id", tempId));
		}
		tempList = criteria.list();
		if (tempList.size() > 0) {
			return tempList.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * 工作流完成后，更改评估计划状态
	 * @param planId
	 */
	@SuppressWarnings("deprecation")
	@Transactional
	public void mergeAssessPlanStatusByplanId(String planId){
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
		if(null != assessPlan){
			assessPlan.setStatus(Contents.STATUS_SOLVED);
			assessPlan.setDealStatus(Contents.DEAL_STATUS_FINISHED);
			saveRiskAssessPlan(assessPlan);
		}
	}
	/**
	 * 根据计划id删除计划及打分对象，打分部门，综合实体
	 * @param planIds	一个或多个计划id
	 * @return
	 * 
	 * add by 王再冉
	 */
	@Transactional
	@RecordLog("删除计划")
	public Boolean deleteAllByPlanIds(String planIds){
		List<String> idList = new ArrayList<String>();//计划id集合
		List<String> objIdList = new ArrayList<String>();//打分对象id集合
		String delObjIds = "";//删除的打分对象id字符串，用“，”连接
		String delScoreDeptIds = "";//删除的打分部门id字符串，用“，”连接
		String delrcIds = "";//删除的承办人审批人实体id字符串，用“，”连接
		if (StringUtils.isNotBlank(planIds)) {
			String[] idArray = planIds.split(",");
			for (String id : idArray) {
				idList.add(id);
			}
		}
		//查询评估计划所有相关的打分对象
		List<RiskScoreObject> scoreObjList = o_scoreObjectBO.findRiskScoreObjsByplanIdList(idList);
		for(RiskScoreObject obj : scoreObjList){
			delObjIds = obj.getId() + "," + delObjIds;
			objIdList.add(obj.getId());
		}
		//查询所有打分部门map
		List<Map<String,Object>> deptMapList = o_scoreDeptBO.findRiskDeptByObjIdList(objIdList);
		for(Map<String,Object> deptMap : deptMapList){
			RiskScoreDept dept = (RiskScoreDept)deptMap.get("scoreDept");
			delScoreDeptIds = dept.getId() + "," + delScoreDeptIds;
		}
		//删除打分部门
		o_riskScoreBO.removeScoreDeptsByDeptIds(delScoreDeptIds);
		//删除打分对象
		o_riskScoreBO.removeRiskScoreObjectsByIds(delObjIds);
		//批量删除评估计划
		this.removeAssessPlansByplanIds(planIds);
		//查询审批人承办人集合
		List<RiskCircusee> circuseeList = o_circuseeBO.findRiskCircuseesByplanIdList(idList);
		for(RiskCircusee rc : circuseeList){
			delrcIds = rc.getId()+ "," + delrcIds;
		}
		this.removeRiskCircuseesByIds(delrcIds);
		return true;
	}

	/**
	 * 批量删除评估计划id by 计划ids
	 * @param planids	计划id字符串
	 */
	@Transactional
	@RecordLog("批量删除计划")
	public void removeAssessPlansByplanIds(String planids){
		o_planDAO.createQuery("delete RiskAssessPlan where id in (:ids)")
		.setParameterList("ids", StringUtils.split(planids,",")).executeUpdate();
	}
	
	/**
	 * 删除审批人评估人记录表 by ids
	 * @param rcIds
	 */
	@Transactional
	public void removeRiskCircuseesByIds(String rcIds){
		if(!"".equals(rcIds)){
			o_riskcircuseeDAO.createQuery("delete RiskCircusee where id in (:ids)")
			.setParameterList("ids", StringUtils.split(rcIds,",")).executeUpdate();
		}
	}
	/**
	 * 通过用户id集合查用户
	 * @param userIdList
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<SysUser> findUsersByuserIdList(List<String> userIdList) {
		Criteria c = o_sysoUserDAO.createCriteria();
		List<SysUser> list = null;
		if(userIdList.size()>0){
			c.add(Restrictions.in("id", userIdList));
			list = c.list();
		}
		return list;
	}
	
	
	/**
	 * 查询评估计划状态
	 * */
	@SuppressWarnings("unchecked")
	public List<RiskAssessPlan> findByDealStatus(String dealStatus){
		Criteria criteria = o_planDAO.createCriteria();
		if(StringUtils.isNotBlank(dealStatus)){
			criteria.add(Restrictions.eq("dealStatus", dealStatus));
			criteria.add(Restrictions.ne("workType", "assess_work_type_project"));
		}
		List<RiskAssessPlan> riskAssessPlanList = criteria.list();
		return riskAssessPlanList;
	}
	
	/**
	 * 查询评估计划状态
	 * */
	@SuppressWarnings("unchecked")
	public List<RiskAssessPlan> findByDealStatusOrderTime(String dealStatus){
		Criteria criteria = o_planDAO.createCriteria();
		if(StringUtils.isNotBlank(dealStatus)){
			criteria.add(Restrictions.eq("dealStatus", dealStatus));
			criteria.add(Restrictions.eq("planType", "riskAssess"));
			criteria.addOrder(Order.desc("planCreatTime"));
		}
		List<RiskAssessPlan> riskAssessPlanList = criteria.list();
		return riskAssessPlanList;
	}
	
	/**
	 * findJbpmHistActinstsBySome: 问卷监控查询状态
	 * @param jbpmHistProcinstId	流程id
	 * @param dbversion				状态 0：未开始
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<JbpmHistActinst> findJbpmHistActinstsBySome(Long jbpmHistProcinstId,Long dbversion){
		Criteria dc = o_jbpmHistActinstDAO.createCriteria();
		dc.createAlias("jbpmHistTask", "jbpmHistTask");
		dc.createAlias("jbpmHistProcinst", "jbpmHistProcinst");
		dc.createAlias("jbpmHistProcinst.vJbpmDeployment", "vJbpmDeployment");
		dc.createAlias("jbpmHistProcinst.vJbpmDeployment.processDefinitionDeploy", "processDefinitionDeploy");
		dc.createAlias("jbpmExecution", "jbpmExecution");
		dc.setFetchMode("jbpmHistTask.assignee", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistTask.assignee.sysOrganization", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistTask.examineApproveIdea", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistProcinst.businessWorkFlow", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistProcinst.businessWorkFlow.createByEmp", FetchMode.JOIN);
		
		
		if(jbpmHistProcinstId!=null){
			DetachedCriteria subProcinstCriteria=DetachedCriteria.forClass(JbpmHistActinst.class);
			subProcinstCriteria.createAlias("jbpmHistProcinst", "jbpmHistProcinst");
			subProcinstCriteria.createAlias("jbpmExecution", "jbpmExecution");
			subProcinstCriteria.createAlias("jbpmExecution.jbpmHistSubProcinst", "jbpmHistSubProcinst");
			subProcinstCriteria.add(Restrictions.eq("jbpmHistProcinst.id", jbpmHistProcinstId));
			subProcinstCriteria.setProjection(Property.forName("jbpmHistSubProcinst.id"));
			dc.add(Restrictions.or(Restrictions.eq("jbpmHistProcinst.id", jbpmHistProcinstId),Property.forName("jbpmHistProcinst.id").in(subProcinstCriteria)));
		}
		if(dbversion!=null){
			dc.add(Restrictions.eq("dbversion", dbversion));
		}
		return dc.list();
	}
	
	/**
	 * 
	 * findJbpmHistActinstsBySome:
	 * 
	 * @author 杨鹏
	 * @param jbpmHistProcinstId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findJbpmHistActinstsBySomeSQl(Long jbpmHistProcinstId){
		StringBuffer sql=new StringBuffer("");
		sql.append("	SELECT");
		sql.append("			T_SYS_EMPLOYEE.ID ASSIGNEEID,T_SYS_EMPLOYEE.REAL_NAME,JBPM4_EXECUTION.ID_,JBPM4_TASK.ACTIVITY_NAME_");
		sql.append("	FROM");
		sql.append("		JBPM4_TASK");
		sql.append("	LEFT JOIN T_SYS_EMPLOYEE ON T_SYS_EMPLOYEE.ID=JBPM4_TASK.ASSIGNEE_");
		sql.append("	LEFT JOIN JBPM4_EXECUTION ON JBPM4_EXECUTION.DBID_ = JBPM4_TASK.EXECUTION_");
		sql.append("	LEFT JOIN JBPM4_EXECUTION JBPM4_EXECUTION1 ON JBPM4_EXECUTION1.SUBPROCINST_ = JBPM4_EXECUTION.INSTANCE_");
		sql.append("	LEFT JOIN JBPM4_HIST_PROCINST ON JBPM4_HIST_PROCINST.DBID_=JBPM4_EXECUTION1.INSTANCE_ OR JBPM4_EXECUTION1.INSTANCE_ IS NULL AND JBPM4_HIST_PROCINST.DBID_ = JBPM4_TASK.PROCINST_");
		sql.append("	where JBPM4_HIST_PROCINST.DBID_=?");
		SQLQuery query = o_jbpmHistActinstDAO.createSQLQuery(sql.toString(), jbpmHistProcinstId);
		return query.list();
	}
	
	/**
	 * 查询已经进行任务分配后的部门，人员，打分对象和打分部门map
	 * @param businessId	计划id
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,ArrayList<Map<String,Object>>> findPlanFollowMapByBusinessId(String businessId, String query){
		Map<String,ArrayList<Map<String,Object>>> mapAll = 
							new HashMap<String,ArrayList<Map<String,Object>>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT e.org_id,f.org_name,c.id,c.emp_name,d.score_object_id,d.score_dept_id " +
				" FROM t_sys_employee c INNER JOIN " +
				" (SELECT SCORE_DEPT_ID,SCORE_OBJECT_ID,SCORE_EMP_ID FROM t_rm_rang_object_dept_emp " + 
				" WHERE SCORE_OBJECT_ID IN (SELECT ID FROM t_rm_risk_score_object WHERE assess_plan_id = ?)) d " + 
				" INNER JOIN t_rm_risk_score_dept e INNER JOIN t_sys_organization f " + 
				" WHERE d.score_emp_id = c.id AND d.score_dept_id = e.id AND e.org_id = f.id ");
		
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
		sqlQuery.setString(0, businessId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
			String deptId = "";
			String deptName = "";
			String empId = "";
			String empName = "";
			String scoreObjId = "";
			String scoreDeptId = "";
			
			if(null != objects[0]){
				deptId = objects[0].toString();
			}if(null != objects[1]){
				deptName = objects[1].toString();
			}if(null != objects[2]){
				empId = objects[2].toString();
			}if(null != objects[3]){
				empName = objects[3].toString();
			}if(null != objects[4]){
				scoreObjId = objects[4].toString();
			}if(null != objects[5]){
				scoreDeptId = objects[5].toString();
			}
			
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("deptId", deptId);
			tempMap.put("deptName", deptName);
			tempMap.put("empId", empId);
			tempMap.put("empName", empName);
			tempMap.put("scoreDeptId", scoreDeptId);
			tempMap.put("scoreObjId", scoreObjId);
			
			if(mapAll.get(deptId) != null){
				if(!mapAll.get(deptId).contains(tempMap)){
					mapAll.get(deptId).add(tempMap);
				}
            }else{
            	ArrayList<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
            	arrayList.add(tempMap);
            	mapAll.put(deptId, arrayList);
            }
		}
		return mapAll;
	}
	
	/**
	 * 查询评估详细跟踪列表
	 * @param businessId	计划id
	 * @param query			搜索关键字
	 * @return
	 */
	public List<Map<String,Object>> findPlanFollowGridByBusinessId(String businessId, String query){
		List<Map<String,Object>> gridMap = new ArrayList<Map<String,Object>>();
		Map<String,Object> rtnMap = o_scoreDeptBO.findScoreDeptsByPlanId(businessId);//该计划的所有打分对象
		List<Map<String,Object>> scoreDeptMapList = (List<Map<String, Object>>) rtnMap.get("scoreDept");
		Map<String,ArrayList<Map<String,Object>>> mapAll = this.findPlanFollowMapByBusinessId(businessId,query);
		JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
		List<Object[]> jbpmHistActinstList = this.findJbpmHistActinstsBySomeSQl(jbpmhp.getId());//工作流未执行的任务节点
		
		Set<String> deptIdSet = mapAll.keySet();
		if(deptIdSet.size()>0){//已经进行任务分配
			ArrayList<String> comDeptIds = new ArrayList<String>();
			for(Map<String,Object> scoreDeptMap : scoreDeptMapList){
				RiskScoreDept scoreDept = (RiskScoreDept)scoreDeptMap.get("scoreDept");
				if(!comDeptIds.contains(scoreDept.getOrganization().getId())){
					ArrayList<String> compEmpIds = new ArrayList<String>();
					if(null != mapAll.get(scoreDept.getOrganization().getId())){//该部门完成任务分配
						ArrayList<Map<String,Object>> arraylist = mapAll.get(scoreDept.getOrganization().getId());
						for(Map<String,Object> tmap : arraylist){
							String empId = tmap.get("empId").toString();
							if(!compEmpIds.contains(empId)){
								String count = this.findPlanEmpRiskCountBySome(empId,arraylist);
								Map<String,String> statusMap = this.findPlanEmpStatusBySome(empId, jbpmHistActinstList,scoreDept.getOrganization().getId());
								Map<String,Object> tempMap = new HashMap<String, Object>();
								tempMap.put("deptId", tmap.get("deptId"));
								tempMap.put("deptName", tmap.get("deptName"));
								tempMap.put("empId", empId);
								tempMap.put("empName", tmap.get("empName"));
								tempMap.put("scoreObjCount", count);//风险数量
								tempMap.put("taskStatus", statusMap.get("status"));//执行状态
								tempMap.put("executionId", statusMap.get("executionId"));
								tempMap.put("queryStatus", statusMap.get("queryStatus"));
								compEmpIds.add(empId);//标记已添加的人员
								comDeptIds.add(tmap.get("deptId").toString());//标记已添加的部门
								gridMap.add(tempMap);
							}
						}
						
					}else{//该部门未完成分配
						Map<String,Object> deptMap = new HashMap<String, Object>();
						deptMap.put("deptName", scoreDept.getOrganization().getOrgname());
						comDeptIds.add(scoreDept.getOrganization().getId());
						gridMap.add(deptMap);
					}
				}
			}
		}else{//未进行任务分配
			ArrayList<String> comDeptIds = new ArrayList<String>();
			for(Map<String,Object> scoreDeptMap : scoreDeptMapList){
				RiskScoreDept scoreDept = (RiskScoreDept)scoreDeptMap.get("scoreDept");
				if(!comDeptIds.contains(scoreDept.getOrganization().getId())){
					Map<String,Object> deptMap = new HashMap<String, Object>();
					deptMap.put("deptName", scoreDept.getOrganization().getOrgname());
					comDeptIds.add(scoreDept.getOrganization().getId());
					gridMap.add(deptMap);
				}
			}
		}
		if(StringUtils.isNotBlank(query)){//模糊查询
			List<Map<String,Object>> queryMapList = new ArrayList<Map<String,Object>>();
			for(Map<String,Object> querymap : gridMap){
				String deptName = querymap.get("deptName").toString();
				String empName = null!=querymap.get("empName")?querymap.get("empName").toString():"";
				String counts = null!=querymap.get("scoreObjCount")?querymap.get("scoreObjCount").toString():"";
				String queryStatus = null!=querymap.get("queryStatus")?querymap.get("queryStatus").toString():"";
				if(deptName.contains(query)||empName.contains(query)||counts.contains(query)||queryStatus.contains(query)){
					queryMapList.add(querymap);
				}
				
			}
			gridMap = queryMapList;
		}
		
		return gridMap;
	}
	
	/**
	 * 判断人员执行任务情况业务处理
	 * @param empOrgScoreDeptMapList
	 * @param jbpmHistActinstList
	 * @return
	 */
	public Map<String,String> findPlanEmpStatusBySome(String empId, List<Object[]> jbpmHistActinstList, String orgId){
		Map<String,String> map = new HashMap<String, String>();
		List<String> assignessList = new ArrayList<String>();
		List<String> orgIdList = new ArrayList<String>();
		String status = "1";
		String queryStatus = "已处理";
		String executionId = "";
		//查工作流中人员的状态
		for (Iterator<Object[]> iterator = jbpmHistActinstList.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
			String assigneeId = "";
			//String assigneeName ="";
			String activityName = "";
			
			if(null != objects[0]){
				assigneeId = objects[0].toString();
			}if(null != objects[3]){
				activityName = objects[3].toString();
			}
			if("评估任务分配".equals(activityName)){
				SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(assigneeId);
				orgIdList.add(empOrg.getSysOrganization().getId());
			}
			
			assignessList.add(assigneeId);
			if(empId.equals(assigneeId)){
				if("风险评估".equals(activityName)){
					status = "0";
					queryStatus = "未开始";
					if(null != objects[2]){
						executionId = objects[2].toString();
					}
				}
				if("评估任务分配".equals(activityName)){
					status = "0";
					queryStatus = "未开始";
				}
			}
		}
		if(!assignessList.contains(empId) && orgIdList.contains(orgId)){//不包含这个员工 ，任务分配节点包含这个部门，则员工未完成评估
			status = "0";
			queryStatus = "未开始";
		}
		map.put("status", status);
		map.put("queryStatus", queryStatus);
		map.put("executionId", executionId);
		return map;
	}
	
	/**
	 * 查询人员的风险评估数量
	 * @param empId
	 * @param mapList
	 * @return
	 */
	public String findPlanEmpRiskCountBySome(String empId,ArrayList<Map<String,Object>> mapList){
		int count = 0;
		for(Map<String,Object> map : mapList){
			if(empId.equals(map.get("empId"))){
				count++;
			}
		}
		return count+"";
	}
	@Override
	public Page<RiskAssessPlan> findPlansPageBySome(String planName, Page<RiskAssessPlan> page, String companyId,
			String workType, String status) {
		// ENDO Auto-generated method stub
		return null;
	}
	
	
}
