/**
 * 
 * 
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-10-6 		宋佳
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.business.response.execute;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.Identities;
import com.fhd.dao.response.SolutionDAO;
import com.fhd.dao.response.SolutionExecutionDAO;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.response.Solution;
import com.fhd.entity.response.SolutionExecution;
import com.fhd.entity.response.SolutionRelaOrg;
import com.fhd.entity.response.SolutionRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.response.ResponseListBO;
import com.fhd.ra.business.response.ResponseSolutionBO;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.web.controller.response.execute.SolutionExecuteBpmObject;
import com.fhd.sys.business.organization.OrgGridBO;


/**
 * @author 宋佳
 * @version 尝试性基类
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see
 */
@Service
public class ResponseSolutionExecuteBpmBO{
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private ResponseListBO o_responseListBO;
	@Autowired
	private SolutionDAO o_solutionDAO;
	@Autowired
	private ResponseSolutionBO o_responseSolutionBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private SolutionExecutionDAO o_solutionExecutionDAO;
	@Autowired
	private ResponseSolutionExecuteBO o_responseSolutionExecuteBO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	
	@Autowired
	private RiskOrgBO o_riskOrgBO;
	
	@Autowired
	private OrgGridBO o_orgGridBO;
	
	/**
	 * 第1步工作流提交--工作流提交
	 * @param executionId
	 * @param businessId
	 */
	@Transactional
	public boolean startResponseProcess(){
			//菜单触发提交开启工作流
			/**
			 * 测试用查询出所有的应对内容
			 */
			List<Solution> solutionList = o_responseSolutionBO.findSolutionsByAutoWorkFlowTime();
			List<SolutionExecuteBpmObject> returnList = new ArrayList<SolutionExecuteBpmObject>();
			SolutionExecuteBpmObject solutionExecuteBpmObject = null;
			for(Solution solution : solutionList){
				solutionExecuteBpmObject = new SolutionExecuteBpmObject();
				List<SolutionRelaOrg> orgList = o_responseSolutionBO.findOrgAndEmpFromSolutionById(solution.getId());
				Risk risk = o_responseSolutionBO.findRiskBySolutionId(solution.getId());
				for(SolutionRelaOrg solutionRelaOrg : orgList){
					if(Contents.EMP_RESPONSIBILITY.equals(solutionRelaOrg.getType())){
						//工作流开始前进行应对执行的初始化数据
						String executeId = this.initSolutionExecutionEntry(solution.getId(), solutionRelaOrg.getEmp().getId(),solutionRelaOrg.getEmp().getId());
						o_responseSolutionBO.updateStatusFromSolutionById(solution.getId(), Contents.DEAL_STATUS_HANDLING);
						solutionExecuteBpmObject.setSolutionExecuteEmpId(solutionRelaOrg.getEmp().getId());
						solutionExecuteBpmObject.setSolutionId(solution.getId());
						solutionExecuteBpmObject.setSolutionExecuteId(executeId);
						solutionExecuteBpmObject.setRiskId(risk==null?"":risk.getId());
						returnList.add(solutionExecuteBpmObject);
					}
				}
			}
			if(returnList!=null && returnList.size() > 0){
				Map<String, Object> variables = new HashMap<String, Object>();
				String entityType = "rmResponseSolution";
				variables.put("entityType", entityType);
				variables.put("id", "");
				variables.put("name","应对执行");
				variables.put("startExecuteEmpId", "");
				String executionId = o_jbpmBO.startProcessInstance(entityType,variables);
				//工作流提交
				Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
				variablesBpmTwo.put("items", returnList);
				variablesBpmTwo.put("joinCount", returnList.size());
				o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
			}else{
			}
		return true;
	}
	/**
	 * 初始化应对执行实体，用于工作流执行过程
	 */
	public String initSolutionExecutionEntry(String solutionId,String executeEmp,String createByEmp){
		SolutionExecution solutionExecution = new SolutionExecution();
 		solutionExecution.setCreateBy(new SysEmployee(executeEmp));
 		solutionExecution.setCreateTime(new Date());
 		solutionExecution.setRespnoseExecutor(new SysEmployee(createByEmp));
 		solutionExecution.setId(Identities.uuid());
 		solutionExecution.setSolution(new Solution(solutionId));
 		//初始化应对执行状态为   为开始
 		solutionExecution.setStatus(Contents.DEAL_STATUS_NOTSTART);
 		o_solutionExecutionDAO.merge(solutionExecution);
 		return solutionExecution.getId();
	}
	/**
	 * add by 宋佳 
	 * 获取应对执行的工作流参数传回前台
	 * 
	 */
	public Map<String,String> getBpmValuesByExecuteId(String executeId){
		Map<String,String> returnMap = new HashMap<String,String>();
		SolutionExecuteBpmObject solutionExecuteBpmObject = this.findBpmObjectByExecutionId(executeId,"item");
		returnMap.put("solutionExecuteId", solutionExecuteBpmObject.getSolutionExecuteId());
		returnMap.put("solutionId", solutionExecuteBpmObject.getSolutionId());
		returnMap.put("solutionExecuteEmpId", solutionExecuteBpmObject.getSolutionExecuteEmpId());
		returnMap.put("riskId", solutionExecuteBpmObject.getRiskId());
		return returnMap;
	}
	/**
	 * 根据流程实例ID获得类型为 object.java 的流程变量
	 * @author 吴德福
	 * @param executionId 流程实例ID
	 * @return AssessPlanBpmObject
	 * @since  fhd　Ver 1.1
	*/
	public SolutionExecuteBpmObject findBpmObjectByExecutionId(String executionId,String name){
		return (SolutionExecuteBpmObject) o_jbpmOperate.getVariableObj(executionId, name);
	}
	/**
	 * 应对执行工作流节点，当应对进度为100% 的时候执行
	 */
	@Transactional
	public Map<String,Object> responseExecutionBpm(String executionId){
		//获取流程中转变量
		Map<String, Object> returnMap = new HashMap<String, Object>();
		SolutionExecuteBpmObject solutionExecuteBpmObject = this.findBpmObjectByExecutionId(executionId,"item");
		String solutionExecuteId = solutionExecuteBpmObject.getSolutionExecuteId();
		List<String> list = new ArrayList<String>();
		list.add(solutionExecuteId);
		int count = o_responseSolutionExecuteBO.updateStatusFromSolutionExecutionBy(list,Contents.DEAL_STATUS_FINISHED);
		if(count > 0){
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
			returnMap.put("success", true);
		}else{
			returnMap.put("success", false);
		}
		return returnMap;
	}
	/**
	 * 应对方案制定  提交结果
	 */
	@Transactional
	@RecordLog("制定应对措施")
	public Map<String,Object> makeSolutionPlanBpm(String executionId,String businessId){
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		String orgIds = UserContext.getUser().getMajorDeptId();
		List<RiskOrg> riskOrgList = 
				o_riskOrgBO.findRiskOrgByOrgId(UserContext.getUser().getCompanyid(), orgIds, "1");
		
		if(riskOrgList.size() == 0){
			HashMap<String, Risk> orgRiskMap = o_riskOrgBO.findRiskOrgMap();
			String idSeq[] = o_orgGridBO.findOrganizationByOrgId(orgIds).getOrgseq().replace(".", ",").split(",");
			
			for (String orgId : idSeq) {
				if(null != orgRiskMap.get(orgId)){
					orgIds = orgId;
					break;
				}
			}
		}
		
		SysEmployee leader = o_riskScoreBO.findDeptLeaderOrCbr(businessId, orgIds);
		//应对方案状态改为"待审批"
		o_responseSolutionBO.updateSolutionArchiveStatusById(businessId, Contents.RISK_STATUS_EXAMINE);
		variables.put("approvePerson", leader.getId());
		o_jbpmBO.doProcessInstance(executionId, variables);
		returnMap.put("success", true);
		return returnMap;
	}
	/**
	 * 应对方案制定  提交结果
	 */
	@Transactional
	@RecordLog("应对计划审批")
	public Map<String,Object> solutionPlanBpmApprove(String executionId,String businessId,String isPass,String examineApproveIdea){
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		if("no".equals(isPass)){
			variables.put("makeSolutionPath", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
			//获取评估人
			RiskAssessPlanTakerObject riskAssessPlanTakerObject = (RiskAssessPlanTakerObject)o_jbpmOperate.getVariableObj(executionId,"riskTaskEvaluator");
			//评估人
			String empId = riskAssessPlanTakerObject.getRiskTaskEvaluatorId();
			List<String> riskIds = o_responseListBO.getRiskIdsByEmpIds(businessId,empId);
			List<SolutionRelaRisk> solutionRelaRiskList = o_responseListBO.findSolutionsByRisk(riskIds,Contents.DEAL_STATUS_NOTSTART,Contents.SOLUTION_PLAN,empId,businessId);
			List<SolutionExecuteBpmObject> returnList = new ArrayList<SolutionExecuteBpmObject>();
			SolutionExecuteBpmObject solutionExecuteBpmObject = null;
			for(SolutionRelaRisk solutionRelaRisk : solutionRelaRiskList){
				solutionExecuteBpmObject = new SolutionExecuteBpmObject();
				for(SolutionRelaOrg solutionRelaOrg : solutionRelaRisk.getSolution().getSolutionRelaOrg()){
					if(Contents.EMP_RESPONSIBILITY.equals(solutionRelaOrg.getType())){
						//工作流开始前进行应对执行的初始化数据
						String executeId = this.initSolutionExecutionEntry(solutionRelaRisk.getSolution().getId(), solutionRelaOrg.getEmp().getId(),solutionRelaOrg.getEmp().getId());
						//执行表插入完毕后，更新应对措施的状态为已处理
						o_responseSolutionBO.updateStatusFromSolutionById(solutionRelaRisk.getSolution().getId(), Contents.DEAL_STATUS_HANDLING);
						solutionExecuteBpmObject.setSolutionExecuteEmpId(solutionRelaOrg.getEmp().getId());
						solutionExecuteBpmObject.setSolutionId(solutionRelaRisk.getSolution().getId());
						solutionExecuteBpmObject.setSolutionExecuteId(executeId);
						solutionExecuteBpmObject.setRiskId(solutionRelaRisk.getRisk().getId());
						returnList.add(solutionExecuteBpmObject);
					}
				}
			}
			if(returnList!=null && returnList.size() > 0){
				/*向全局变量中保存 joinCount*/
				variables.put("joinCount", Integer.parseInt(o_jbpmOperate.getVariable(executionId, "joinCount")) + returnList.size()-1);
				o_jbpmOperate.saveVariables(o_jbpmBO.getExecutionIdBySubExecutionId(executionId),variables);
				Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
				variablesBpmTwo.put("items", returnList);
				variablesBpmTwo.put("makeSolutionPath", isPass);
				o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
				returnMap.put("success", true);
			}else{
				returnMap.put("reason", "无符合应对需要执行！");
			}
		}
		returnMap.put("success", true);
		return returnMap;
	}
	
	/**
	 * 计划主管审批
	 * add by 王再冉
	 * 2014-1-16  下午1:38:14
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId 	审批人id
	 * void
	 */
	@Transactional
	public void submitRiskResponseApprovalBySupervisor(String executionId, String businessId, String isPass, 
																	String examineApproveIdea, String approverId){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if(null != assessPlan){
			if ("no".equals(isPass)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				// 审批通过
				String planDeptLeaderId = "";
				Map<String, Object> variables = new HashMap<String, Object>();
				if(StringUtils.isNotBlank(approverId)){
					JSONArray jsonArray = JSONArray.fromObject(approverId);
					if (jsonArray.size() > 0){
						 JSONObject jsobj = jsonArray.getJSONObject(0);
						 planDeptLeaderId = jsobj.getString("id");//审批人
					}
				}
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				variables.put("PlanLeader", planDeptLeaderId);//部门领导
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	/**
	 * 计划领导审批
	 * add by 王再冉
	 * 2014-1-16  下午1:42:19
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * void
	 */
	public void submitRiskResponseApprovalByLeader(String executionId, String businessId, String isPass, 
			String examineApproveIdea){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if(null != assessPlan){
			if ("no".equals(isPass)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				// 审批通过
				String takers = o_jbpmOperate.getVariable(executionId,"empIds");
				List<RiskAssessPlanTakerObject> assessPlanTakerList = new ArrayList<RiskAssessPlanTakerObject>();
				if (StringUtils.isNotBlank(takers)) {
					String[] idArray = takers.split(",");
					for (String id : idArray) {
						RiskAssessPlanTakerObject assessPlanTaker = new RiskAssessPlanTakerObject();
						assessPlanTaker.setPlanTakerEmpId(id);
						assessPlanTakerList.add(assessPlanTaker);
					}
				}
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("assessPlanTakers", assessPlanTakerList);//承办人
				variables.put("assessPlanTakersJoinCount", assessPlanTakerList.size());
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	/**
	 * 任务分配提交
	 * add by 王再冉
	 * 2014-1-16  下午4:18:34
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param pingguEmpIds 	评估人id
	 * void
	 */
	@Transactional
	public void submitRiskTaskDistribute(String executionId, String businessId, String pingguEmpIds){
		Map<String, Object> variables = new HashMap<String, Object>();
		o_jbpmBO.doProcessInstance(executionId, variables);
	}
	/**
	 * 方案审批提交
	 * add by 王再冉
	 * 2014-1-18  下午1:42:29
	 * desc : 
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 * @param approverId 
	 * void
	 */
	@Transactional
	public Map<String,Object> solutionApproveLeader(String executionId, String businessId, String isPass, 
																String examineApproveIdea,String approverId){
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		if("no".equals(isPass)){
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
			// 审批通过
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			String approveLeaderId = "";
			JSONArray jsonArray = JSONArray.fromObject(approverId);
			if (jsonArray.size() > 0){
				 JSONObject jsobj = jsonArray.getJSONObject(0);
				 approveLeaderId = jsobj.getString("id");//审批人
			}
			variables.put("deptLeaderId", approveLeaderId);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}
		returnMap.put("success", true);
		return returnMap;
	}
	/**
	 * 单位主管审批
	 * add by 王再冉
	 * 2014-1-20  下午3:21:17
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	审批人id
	 * @return 
	 * Map<String,Object>
	 */
	@Transactional
	public Map<String,Object> solutionPlanApproveBySupervisor(String executionId, String businessId, String isPass, 
																String examineApproveIdea,String approverId){
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		if("no".equals(isPass)){
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
			// 审批通过
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			String approveLeaderId = "";
			if(StringUtils.isNotBlank(approverId)){
				JSONArray jsonArray = JSONArray.fromObject(approverId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 approveLeaderId = jsobj.getString("id");//审批人
				}
			}
			variables.put("approveLeader", approveLeaderId);//单位领导
			o_jbpmBO.doProcessInstance(executionId, variables);
			returnMap.put("success", true);
		}
		return returnMap;
	}
	/**
	 * 单位领导审批
	 * add by 王再冉
	 * 2014-1-18  下午2:26:45
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	审批人id
	 * @return 
	 * Map<String,Object>
	 */
	@Transactional
	public Map<String,Object> solutionPlanApproveByLeader(String executionId, String businessId, String isPass, 
																String examineApproveIdea,String approverId){
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		if("no".equals(isPass)){
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
			// 审批通过
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			String approveLeaderId = "";
			if(StringUtils.isNotBlank(approverId)){
				JSONArray jsonArray = JSONArray.fromObject(approverId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 approveLeaderId = jsobj.getString("id");//审批人
				}
			}
			variables.put("approveManager", approveLeaderId);//单位领导
			o_jbpmBO.doProcessInstance(executionId, variables);
			returnMap.put("success", true);
		}
		return returnMap;
	}
	/**
	 * 业务分管副总审批
	 * add by 王再冉
	 * 2014-1-18  下午3:06:55
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * void
	 */
	@Transactional
	public HashMap<String,Object> solutionPlanApproveToEnd(String executionId,String businessId,String isPass,String examineApproveIdea){
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		if("no".equals(isPass)){
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
			// 审批通过
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			//更新计划状态
			this.mergeResponsePlanStatusBybusinessId(businessId);
			o_responseSolutionBO.updateSolutionArchiveStatusById(businessId, Contents.RISK_STATUS_ARCHIVED);//已归档状态
			o_jbpmBO.doProcessInstance(executionId, variables);
		}
		returnMap.put("success", true);
		return returnMap;
	}
	/**
	 * 更新计划状态
	 * add by 王再冉
	 * 2014-1-18  下午3:14:06
	 * desc : 
	 * @param id 计划id
	 * void
	 */
	public void mergeResponsePlanStatusBybusinessId(String id) {
		//应对计划状态更改为已完成
		String hqlSql = "update RiskAssessPlan ra set ra.dealStatus = :dealStatus where ra.id = :id";
		Query query = o_riskAssessPlanDAO.createQuery(hqlSql);
		query.setParameter("dealStatus", Contents.DEAL_STATUS_FINISHED);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	/**
	 * 审批列表
	 * add by 王再冉
	 * 2014-1-21  上午9:30:11
	 * desc : 
	 * @param query			搜索框关键字
	 * @param businessId	计划id
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	public ArrayList<HashMap<String, String>> findApproveGridBybusinessId(String query,String businessId){
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
		sql.append(" SELECT s.ID,r.PARENT_ID,sr.RISK_ID,r.RISK_NAME,s.SOLUTION_NAME,s.RESPONSE_PLAN_ID ");
		sql.append(" FROM t_rm_solution s ");
		sql.append(" LEFT JOIN t_rm_solution_rela_risk sr on sr.SOLUTION_ID = s.ID ");
		sql.append(" left JOIN t_rm_risks r ON r.ID = sr.RISK_ID ");
		sql.append(" WHERE s.RESPONSE_PLAN_ID =:businessId order by r.risk_name ");
		SQLQuery sqlQuery = o_solutionDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("businessId", businessId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String solutionId = "";
			String riskParentId = "";
            String riskId = "";
            String riskName = "";
            String solutionName = "";
            if(null != o[0]){
            	solutionId = o[0].toString();
            }if(null != o[1]){
            	riskParentId = o[1].toString();
            }if(null != o[2]){
            	riskId = o[2].toString();
            }if(null != o[3]){
            	riskName = o[3].toString();
            }if(null != o[4]){
            	solutionName = o[4].toString();
            }
            List<SolutionRelaOrg> solutionRelaOrgList = o_responseSolutionBO.findOrgAndEmpFromSolutionById(solutionId);
            HashMap<String, String> map = new HashMap<String, String>();
			map.put("riskParentId", riskParentId);
			map.put("riskId", riskId);
			map.put("riskName", riskName);
			map.put("measureId", solutionId);
			map.put("measureName", solutionName);
			for(SolutionRelaOrg rela : solutionRelaOrgList){
				if(Contents.EMP_RESPONSIBILITY.equals(rela.getType())){
					map.put("empId", rela.getEmp().getId());
					map.put("empName", rela.getEmp().getEmpname());
				}else if(Contents.ORG_RESPONSIBILITY.equals(rela.getType())){
					map.put("orgId", rela.getOrg().getId());
					map.put("orgName", rela.getOrg().getOrgname());
				}
			}
			maps.add(map);
		}
		return maps;
	}
	
	/**
	 * 应对措施执行开启流程
	 * add by 王再冉
	 * 2014-1-21  上午11:41:05
	 * desc : 
	 * @param empId			应对执行人id
	 * @param businessId	应对措施id
	 * @param name 			应对计划名称
	 * void
	 */
	public void startExcuteResponse(String empId, String businessId, String name){
		String entityType = "riskResponseExcute";
		Solution solution = o_solutionDAO.get(businessId);
		SolutionExecuteBpmObject solutionExecuteBpmObject = new SolutionExecuteBpmObject();
		List<SolutionRelaOrg> orgList = o_responseSolutionBO.findOrgAndEmpFromSolutionById(solution.getId());
		Risk risk = o_responseSolutionBO.findRiskBySolutionId(solution.getId());
		for(SolutionRelaOrg solutionRelaOrg : orgList){
			if(Contents.EMP_RESPONSIBILITY.equals(solutionRelaOrg.getType())){
				//工作流开始前进行应对执行的初始化数据
				String executeId = this.initSolutionExecutionEntry(solution.getId(), solutionRelaOrg.getEmp().getId(),solutionRelaOrg.getEmp().getId());
				o_responseSolutionBO.updateStatusFromSolutionById(solution.getId(), Contents.DEAL_STATUS_HANDLING);
				solutionExecuteBpmObject.setSolutionExecuteEmpId(solutionRelaOrg.getEmp().getId());
				solutionExecuteBpmObject.setSolutionId(solution.getId());
				solutionExecuteBpmObject.setSolutionExecuteId(executeId);
				solutionExecuteBpmObject.setRiskId(risk.getId());
			}
		}
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("entityType", entityType);
		variables.put("id", businessId);
		variables.put("name", name);//应对措施名称
		variables.put("solutionExecuteEmpId", empId);
		variables.put("startExecuteEmpId", UserContext.getUser().getEmpid());
		variables.put("item", solutionExecuteBpmObject);
		String executionId = o_jbpmBO.startProcessInstance(entityType,variables);//开启工作流
		//工作流提交
		Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
		variablesBpmTwo.put("solutionExecuteEmpId", empId);
		o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
	}
	/**
	 * 方案执行--风险应对主管审批
	 * add by 王再冉
	 * 2014-1-21  下午4:58:28
	 * desc : 
	 * @param executionId		工作流程id
	 * @param businessId		计划id
	 * @param isPass			审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId		审批人id
	 * @return 
	 * Map<String,Object>
	 */
	@Transactional
	public void solutionExcuteToSupervisor(String executionId, String businessId, String isPass, 
																String examineApproveIdea,String approverId){
		Map<String, Object> variables = new HashMap<String, Object>();
		String approveLeaderId = "";
		if(StringUtils.isNotBlank(approverId)){
			JSONArray jsonArray = JSONArray.fromObject(approverId);
			if (jsonArray.size() > 0){
				 JSONObject jsobj = jsonArray.getJSONObject(0);
				 approveLeaderId = jsobj.getString("id");//审批人
			}
		}
		variables.put("approveSupervisor", approveLeaderId);//单位主管
		o_jbpmBO.doProcessInstance(executionId, variables);
	}
	/**
	 * 应对执行--个人工作台发起,更改应对措施执行状态
	 * add by 王再冉
	 * @param executionId	工作流程id
	 * @param businessId	执行id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @return 
	 * Map<String,Object>
	 */
	@Transactional
	public Map<String,Object> executeSolution(String executionId,String businessId,String isPass,String examineApproveIdea){
		//获取流程中转变量
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		if("no".equals(isPass)){
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
			returnMap.put("success", true);
		}else{
			int count = o_responseSolutionBO.updateStatusFromSolutionById(businessId, Contents.DEAL_STATUS_FINISHED);
			if(count > 0){
				o_jbpmBO.doProcessInstance(executionId, variables);
				returnMap.put("success", true);
			}else{
				returnMap.put("success", false);
			}
		}
		return returnMap;
	}
}
