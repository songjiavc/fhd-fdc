package com.fhd.ra.business.riskidentify;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.assess.kpiSet.RangObjectDeptEmpDAO;
import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.assess.formulatePlan.RiskOrgRelationTemp;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.quaAssess.DeptLeadCircusee;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.approval.RiskTidyBO;
import com.fhd.ra.business.assess.email.AssessEmailBO;
import com.fhd.ra.business.assess.formulateplan.LastLeaderBO;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.formulateplan.RiskOrgTempBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.DeptLeadCircuseeBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.assess.summarizing.AssessRiskTaskBO;
import com.fhd.ra.business.assess.summarizing.util.SummarizinggAtherUtilBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.business.risk.history.RiskVersionBO;
import com.fhd.ra.web.form.risk.RiskVersionForm;
import com.fhd.ra.web.form.riskidentify.RiskIdentifyForm;
import com.fhd.sys.business.assess.SendEmailBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 风险辨识BO
 * @功能 : 
 * @author 王再冉
 * @date 2014-1-7
 * @since Ver
 * @copyRight FHD
 */
@Service
public class RiskIdentifyBO {
	private static final Logger log = LoggerFactory.getLogger(RiskIdentifyBO.class);
	@Autowired
	private OrgGridBO o_orgGridBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private RiskBO o_riskBO;
	//部门
	@Autowired
	private RiskOrgBO o_riskOrgBO;
	@Autowired
	private DeptLeadCircuseeBO o_deptLeadCircuseeBO;
	@Autowired
	private SummarizinggAtherUtilBO o_summarizinggAtherUtilBO;
	@Autowired
	private ShowRiskTidyBO o_showRiskTidyBO;
	@Autowired
	private StatisticsResultDAO o_statisticsResultDAO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private RangObjectDeptEmpDAO o_rangObjectDeptEmpDAO;
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	@Autowired
	private SendEmailBO o_sendEmailBO;
	
	@Autowired
	private AssessEmailBO o_assessEmailBO;
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	@Autowired
	private RiskScoreObjectDAO o_riskScoreObjectDAO;
	
	@Autowired
	private RiskTidyBO o_riskTidyBO;
	@Autowired
	private LastLeaderBO lastLeaderBO;
	/**
	 * 保存风险辨识计划
	 * add by 王再冉
	 * 2014-1-7  下午3:19:49
	 * desc : 
	 * @param planForm	计划表单实体
	 * @param id	计划id
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	@Transactional
	public Map<String, Object> saveRiskIdentifyPlan(RiskIdentifyForm planForm,String id) throws Exception{
		String companyId = UserContext.getUser().getCompanyid();//登录人公司id
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = new RiskAssessPlan();
		assessPlan.setId(Identities.uuid());
		assessPlan.setPlanName(planForm.getPlanName());
		assessPlan.setPlanCode(planForm.getPlanCode());
		assessPlan.setCompany(o_orgGridBO.findOrganizationByOrgId(companyId));//保存当前机构
		assessPlan.setWorkType(planForm.getWorkType());//工作类型
		assessPlan.setRangeReq(planForm.getRangeReq());//范围要求
		assessPlan.setWorkTage(planForm.getWorkTage());//工作目标
		assessPlan.setPlanType("riskIdentify");//风险辨识计划
		assessPlan.setPlanCreatTime(new Date());//创建时间
		assessPlan.setTemplate(null);
		assessPlan.setTemplateType(null);
		if(StringUtils.isNotBlank(planForm.getBeginDataStr())){//开始时间
			assessPlan.setBeginDate(DateUtils.parseDate(planForm.getBeginDataStr(), "yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(planForm.getEndDataStr())){//开始时间
			assessPlan.setEndDate(DateUtils.parseDate(planForm.getEndDataStr(), "yyyy-MM-dd"));
		}
		
		if(null != planForm.getContactName()){
			String conEmpId = "";
			JSONArray conArray = JSONArray.fromObject(planForm.getContactName());
			if(null != conArray && conArray.size()>0){
				conEmpId = (String)JSONObject.fromObject(conArray.get(0)).get("id");
			}
			SysEmployee conEmp = o_empGridBO.findEmpEntryByEmpId(conEmpId);
			if(null != conEmp){
				assessPlan.setContactPerson(conEmp);//保存联系人
			}
		}
		if(null != planForm.getResponsName()){
			String respEmpId = "";
			JSONArray respArray = JSONArray.fromObject(planForm.getResponsName());
			if(null != respArray && respArray.size()>0){
				respEmpId = (String)JSONObject.fromObject(respArray.get(0)).get("id");
			}
			SysEmployee respEmp = o_empGridBO.findEmpEntryByEmpId(respEmpId);
			if(null != respEmp){
				assessPlan.setResponsPerson(respEmp);//保存负责人
			}
		}
		assessPlan.setDealStatus(Contents.DEAL_STATUS_NOTSTART);//点保存按钮，处理状态为"未开始"
		assessPlan.setStatus(Contents.DEAL_STATUS_SAVED);//保存：状态为"已保存"
		if(StringUtils.isNotBlank(id)){//修改
			if(id.contains(",")){
				id = id.split(",")[0];
			}
			assessPlan.setId(id);
			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
		}else{
			o_riskAssessPlanBO.saveRiskAssessPlan(assessPlan);//保存
		}
		inmap.put("planId", assessPlan.getId());
		DictEntry entry = o_dictBO.findDictEntryById("riskIdentify");
		if(null != entry){
			inmap.put("dictValue", entry.getValue().split(";")[0]);//下一步js路径 
			inmap.put("valueAll", entry.getValue());
		}else{
			inmap.put("dictValue", "");
			inmap.put("valueAll", "");
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 风险辨识工作流--提交
	 * add by 王再冉
	 * 2014-1-7  下午4:52:52
	 * desc : 
	 * @param empIds		承办人id
	 * @param approverId	审批人id
	 * @param businessId	计划id
	 * @param executionId	工作流执行id
	 * @param entityType	工作流类型
	 * @param deptEmpId 	部门人员对应id
	 * void
	 */
	@Transactional
	@SuppressWarnings("deprecation")
	@RecordLog("提交风险辨识计划")
	public void submitRiskIdentifyPlanBySome(String empIds, String approverId, String businessId, 
										String executionId, String entityType,String deptEmpId){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		String makePlanEmpId = UserContext.getUser().getEmpid();//制定计划人员id
		String riskManagemerStr = o_riskScoreBO.findRiskManagerIdByCompanyId(UserContext.getUser().getCompanyid());
		if(StringUtils.isBlank(riskManagemerStr)){//如果公司管理员为空，则选用计划制定人
			riskManagemerStr = makePlanEmpId;
		}
		if(null != assessPlan){
			assessPlan.setStatus(Contents.STATUS_SUBMITTED);
			assessPlan.setDealStatus(Contents.DEAL_STATUS_HANDLING);
			String planSupervisorId = "";
			if(StringUtils.isNotBlank(approverId)){
				JSONArray jsonArray = JSONArray.fromObject(approverId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 planSupervisorId = jsobj.getString("id");//审批人
				}
			}else{
				String deptId = UserContext.getUser().getMajorDeptId();
				SysEmployee planSupervisor = o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId,"PlanSupervisor");
				planSupervisorId = planSupervisor.getId();
			}
			//审批人为计划主管
			if(StringUtils.isBlank(executionId)){
				//菜单触发提交开启工作流
				Map<String, Object> variables = new HashMap<String, Object>();
				if(!StringUtils.isNotBlank(entityType)){
					entityType = "riskIdentifyTotal";//流程名称
				}
				variables.put("entityType", entityType);
				variables.put("AssessPlanApproverEmpId", planSupervisorId);//审批人
				variables.put("id", businessId);
				variables.put("name", assessPlan.getPlanName());//评估计划名称
				variables.put("empIds", empIds);
				variables.put("companyId", UserContext.getUser().getCompanyid());
				variables.put("riskManagemer", riskManagemerStr);//该公司的风险管理员
				variables.put("makePlanEmpId", makePlanEmpId);
				executionId = o_jbpmBO.startProcessInstance(entityType,variables);//开启工作流
				//工作流提交
				Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
				variablesBpmTwo.put("AssessPlanApproverEmpId", planSupervisorId);
				o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
			}else{
				//工作流提交
				Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
				variablesBpmTwo.put("AssessPlanApproverEmpId", planSupervisorId);
				variablesBpmTwo.put("empIds", empIds);//可能重新分配承办人，更新
				o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
			}
			//保存审批人承办人
			o_riskScoreBO.saveRiskCircuseeBysome(deptEmpId, planSupervisorId, assessPlan, businessId);
		}
	}
	/**
	 * 计划主管审批
	 * add by 王再冉
	 * 2014-1-8  下午1:36:29
	 * desc : 
	 * @param executionId	流程id
	 * @param businessId	辨识计划id
	 * @param isPass		是否通过
	 * @param examineApproveIdea 审批意见
	 * void
	 */
	@Transactional
	@RecordLog("风险辨识计划，计划主管审批")
	public void submitRiskIdentifyApprovalBySupervisor(String executionId, String businessId, String isPass, 
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
				variables.put("PlanDeptLeader", planDeptLeaderId);//部门领导
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	
	/**
	 * 风险辨识计划主管审批
	 * add by 王再冉
	 * 2014-1-8  下午1:36:29
	 * desc : 
	 * @param executionId	工作流执行id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * void
	 */
	@Transactional
	@RecordLog("风险辨识计划，计划主管审批")
	public void submitRiskIdentifyApprovalBySupervisor(String executionId, String businessId, String isPass, 
																	String examineApproveIdea, String approverId, String approverKey){
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
				variables.put(approverKey, planDeptLeaderId);//部门领导
				o_jbpmBO.doProcessInstance(executionId, variables);
				
				if(o_sendEmailBO.findIsSendValue("send_email_assess_riskplan_all")){//查询计划审批节点是否发送email
					JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
					List<JbpmHistActinst> jbpmHistActinstList = o_riskAssessPlanBO.findJbpmHistActinstsBySome(jbpmhp.getId(),0L);
					for(JbpmHistActinst jbpmHistActinst:jbpmHistActinstList){
						VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
						if(jbpmHistTask!=null){
							//评估计划领导审批
							if(approverKey.equalsIgnoreCase("PlanDeptLeader")){
								if("评估计划领导审批".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
									Long taskId = jbpmHistTask.getId();
									o_assessEmailBO.sendAssessEmail(businessId, planDeptLeaderId, taskId.toString(), 
											"FHD.view.risk.assess.planLeadApprove.PlanLeadApproveMain", "评估计划领导审批");
								}
							}
							//评估单位主管审批 approveOne
							else if (approverKey.equalsIgnoreCase("approveOne")){
								if("评估单位主管审批".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
									Long taskId = jbpmHistTask.getId();
									o_assessEmailBO.sendAssessEmailByEmpIdsAndSome(businessId, planDeptLeaderId, taskId.toString(), 
											"FHD.view.risk.assess.companyManagerApprove.CompanyManagerApproveMain", "评估单位主管审批",executionId);
								}
							}
							//评估单位领导审批 approveTwo
							else if (approverKey.equalsIgnoreCase("approveTwo")){
								if("评估单位领导审批".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
									Long taskId = jbpmHistTask.getId();
									o_assessEmailBO.sendAssessEmailByEmpIdsAndSome(businessId, planDeptLeaderId, taskId.toString(), 
											"FHD.view.risk.assess.companyLeadApprove.CompanyLeadApproveMain", "评估单位领导审批",executionId);
								}
							}
							//评估业务分管副总审批 approveThree
							else if (approverKey.equalsIgnoreCase("approveThree")){
								if("评估业务分管副总审批".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
									Long taskId = jbpmHistTask.getId();
									o_assessEmailBO.sendAssessEmailByEmpIdsAndSome(businessId, planDeptLeaderId, taskId.toString(), 
											"FHD.view.risk.assess.presidentApprove.PresidentApproveMain", "评估业务分管副总审批",executionId);
								}
							}
							//评估风险部门主管审批 approveFore
							else if (approverKey.equalsIgnoreCase("approveFore")){
								if("评估风险部门主管审批".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
									Long taskId = jbpmHistTask.getId();
									o_assessEmailBO.sendAssessEmail(businessId, planDeptLeaderId, taskId.toString(), 
											"FHD.view.risk.assess.deptManagerApprove.DeptManagerApproveMain", "评估风险部门主管审批");
								}
							}
							//评估风险部门领导审批 approveFive
							else if (approverKey.equalsIgnoreCase("approveFive")){
								if("评估风险部门领导审批".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
									Long taskId = jbpmHistTask.getId();
									o_assessEmailBO.sendAssessEmail(businessId, planDeptLeaderId, taskId.toString(), 
											"FHD.view.risk.assess.deptLeadApprove.DeptLeadApproveMain", "评估风险部门领导审批");
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	
	/** 
	  * @Description: 保密风险最后一级审批
	  * @author jia.song@pcitc.com
	  * @date 2017年5月25日 上午9:08:32 
	  * @param executionId
	  * @param businessId
	  * @param isPass
	  * @param examineApproveIdea 
	  */
	public void submitTidyRiskAssessRiskForSecurity(String executionId,String businessId,String isPass,String examineApproveIdea){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if ("no".equals(isPass)) {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
			// 审批通过UNDO
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
//			assessPlan.setDealStatus(Contents.DEAL_STATUS_FINISHED);
//			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
			syncRisks(businessId,"security");
			o_riskTidyBO.updateRiskScoreFromRiskList(businessId);
			//将应对意见同步到风险应对表中
			lastLeaderBO.syncRiskResponseByAssessPlanId(businessId);
		}
	}
	/**
	 * 风险辨识计划领导审批
	 * add by 王再冉
	 * 2014-1-8  下午2:03:08
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * void
	 */
	@Transactional
	@RecordLog("风险辨识计划，计划领导审批")
	public void submitRiskIdentifyApprovalByLeader(String executionId, String businessId, String isPass, 
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
				variables.put("assessPlanTakers", assessPlanTakerList);
				variables.put("assessPlanTakersJoinCount", assessPlanTakerList.size());
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	
	/**
	 * 风险辨识任务分配
	 * add by 王再冉
	 * 2014-1-9  上午9:20:41
	 * desc : 
	 * @param executionId	流程id
	 * @param businessId	计划id
	 * @param pingguEmpIds 	评估人id
	 * void
	 */
	@Transactional
	@RecordLog("风险辨识任务分配")
	public void submitRiskIdentifyTask(String executionId, String businessId, String pingguEmpIds){
		Map<String, Object> variables = new HashMap<String, Object>();
		o_jbpmBO.doProcessInstance(executionId, variables);
	}
	
	/**
	 * 风险辨识新增风险
	 * add by 王再冉
	 * 2014-1-9  下午1:43:42
	 * desc : 
	 * @param parentId		上级风险id
	 * @param riskId		风险id
	 * @param assessPlanId	计划id
	 * @param executionId 	流程id
	 * void
	 */
	public void saveTaskBySome(String parentId, String riskId, String assessPlanId, String executionId,String type){
		String templateId = "";
		String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
		Risk risk =  o_riskBO.findRiskById(parentId);
		if(null != risk.getTemplate()){
			templateId = risk.getTemplate().getId();
		}
		if("task".equals(type)){//任务分配添加风险，不保存打分综合表
			o_riskScoreBO.addRiskAndsaveAllBySome(assessPlanId, riskId, null, templateId);
		}else{
			o_riskScoreBO.addRiskAndsaveAllBySome(assessPlanId, riskId, scoreEmpId, templateId);
		}
		
	}
	
	public String saveTaskBySome(String parentId, Risk risk, String assessPlanId, String executionId,String type,List<RiskOrgTemp> zhuzeOrgList,List<RiskOrgTemp>relaOrgList){
		String templateId = "";
		String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
//		Risk risk =  o_riskBO.findRiskById(parentId);
		if(null != risk.getTemplate()){
			templateId = risk.getTemplate().getId();
		}
		if("task".equals(type)){//任务分配添加风险，不保存打分综合表
			return o_riskScoreBO.addRiskAndsaveAllBySome(assessPlanId, risk, null, templateId,zhuzeOrgList,relaOrgList);
		}else{
			return o_riskScoreBO.addRiskAndsaveAllBySome(assessPlanId, risk, scoreEmpId, templateId,zhuzeOrgList,relaOrgList);
		}
		
	}
	
	/**
	 * 风险辨识提交
	 * add by 王再冉
	 * 2014-1-9  下午2:43:31
	 * desc : 
	 * @param executionId	流程id
	 * @param assessPlanId 	计划id
	 * void
	 */
	public void submitIdentitiAssess(String executionId, String assessPlanId){
		Map<String, Object> variables = new HashMap<String, Object>();
		o_jbpmBO.doProcessInstance(executionId, variables);
		//部门领导审批添加--针对新加的风险事件
		String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
		HashMap<String, DeptLeadCircusee> map = o_deptLeadCircuseeBO.findDeptLeadCircuseeByAssessPlanId(assessPlanId);
		List<RiskScoreObject> scoreObjsByAssessPlanIdList = o_scoreObjectBO.findRiskScoreObjByplanId(assessPlanId);
		for (RiskScoreObject riskScoreObject : scoreObjsByAssessPlanIdList) {
			if(null == map.get(riskScoreObject.getId())){
				DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
				RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
				riskAssessPlan.setId(assessPlanId);
				SysEmployee sysEmployee = new SysEmployee();
				sysEmployee.setId(scoreEmpId);
				deptLeadCircusee.setId(Identities.uuid());
				deptLeadCircusee.setScoreObjectId(riskScoreObject);
				deptLeadCircusee.setRiskAssessPlan(riskAssessPlan);
				deptLeadCircusee.setDeptLeadEmpId(sysEmployee);
				o_deptLeadCircuseeBO.mergeDeptLeadCircusee(deptLeadCircusee);
			}
		}
	}
	/**
	 * 单位主管审批,业务副总审批
	 * add by 王再冉
	 * 2014-1-10  下午5:01:13
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	单位领导审批人id
	 * void
	 */
	@Transactional
	public void submitIdentifyApproveOne(String executionId, String businessId, String isPass, 
																String examineApproveIdea,String approverId){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if(null != assessPlan){
			if ("no".equals(isPass)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				// 审批通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				String approveTwoId = "";
				if(StringUtils.isNotBlank(approverId)){
					JSONArray jsonArray = JSONArray.fromObject(approverId);
					if (jsonArray.size() > 0){
						 JSONObject jsobj = jsonArray.getJSONObject(0);
						 approveTwoId = jsobj.getString("id");//审批人
					}
					variables.put("approveTwo", approveTwoId);//单位领导
				}
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	/**
	 * 单位领导审批
	 * add by 王再冉
	 * 2014-1-13  下午4:13:30
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId 	业务分管副总审批人id
	 * void
	 */
	@Transactional
	public void submitIdentifyApproveTwo(String executionId, String businessId, String isPass, 
																String examineApproveIdea,String approverId){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if(null != assessPlan){
			if ("no".equals(isPass)) {//审批未通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				// 审批通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				String approveTwoId = "";
				if(StringUtils.isNotBlank(approverId)){
					JSONArray jsonArray = JSONArray.fromObject(approverId);
					if (jsonArray.size() > 0){
						 JSONObject jsobj = jsonArray.getJSONObject(0);
						 approveTwoId = jsobj.getString("id");//审批人
					}
					variables.put("approveThree", approveTwoId);//单位领导
				}
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	/**
	 * 风险部门主管审批
	 * add by 王再冉
	 * 2014-1-13  下午4:52:43
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId 	风险部门领导审批人id
	 * void
	 */
	@Transactional
	public void submitIdentifyApproveFour(String executionId, String businessId, String isPass, 
																String examineApproveIdea,String approverId){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if(null != assessPlan){
			if ("no".equals(isPass)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				// 审批通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				String approveFiveId = "";
				if(StringUtils.isNotBlank(approverId)){
					JSONArray jsonArray = JSONArray.fromObject(approverId);
					if (jsonArray.size() > 0){
						 JSONObject jsobj = jsonArray.getJSONObject(0);
						 approveFiveId = jsobj.getString("id");//审批人
					}
					variables.put("approveFive", approveFiveId);//风险部门领导
				}
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	
	
	
	@Autowired
	private RiskVersionBO riskVersionBO;
	@Autowired
	private RiskOrgTempBO riskOrgTempBO;
	/**
	 * 风险部门领导审批，结束工作流
	 * add by 王再冉
	 * 2014-1-10  下午5:02:01
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * void
	 */
	@Transactional
	public void submitAndComplete(String executionId, String businessId, String isPass, String examineApproveIdea){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if(null != assessPlan){
			if ("no".equals(isPass)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				// 审批通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				syncRisks(businessId,assessPlan.getSchm());
				lastLeaderBO.syncRiskResponseByAssessPlanId(businessId);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	
	@Transactional
	public void syncRisks(String businessId,String schm){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		//TODO 吉志强调用打版本接口后 一系列操作
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		String nowStr = sf.format(now);
		String versionName = nowStr + "_计划名称:[" + assessPlan.getPlanName()+"]";
		String desc = assessPlan.getPlanName()+"_该计划完成时间["+now.toString()+"]";
		RiskVersionForm versionForm = new RiskVersionForm();
		versionForm.setVersionName(versionName);
		versionForm.setDesc(desc);
		boolean isSuccess = this.riskVersionBO.saveRiskVersion(versionForm, "", assessPlan.getSchm());
		//TODO 吉志强调用打版本接口后 一系列操作
		
		if(isSuccess){
			assessPlan.setDealStatus(Contents.DEAL_STATUS_FINISHED);//处理状态为"已完成"
			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
			//更新t_rm_risk_org_temp 的status为finished
			this.riskOrgTempBO.updateRiskOrgTempStatusByPlanId(businessId);
			//删除风险库以及与部门的关系表
			this.riskOrgTempBO.deleteRisk(assessPlan.getSchm(),businessId);
			//批量将打分对象插入风险库
			this.riskOrgTempBO.saveRiskBatch(businessId);
			//批量将打分部门t_risk_org_temp插入风险库与部门关系表 t_risk_org
			this.riskOrgTempBO.saveRiskOrgBatchByRiskOrgRelationList(this.riskOrgTempBO.getRiskOrgRelationTempListByPlanId(businessId));
			// add by songjia 应对措施保存
			
			
			//将新增风险状态改为‘1’
			this.updateNewRisksStatusByids(businessId);
		}else{
			log.info("计划名称：[{}],打版本失败!",assessPlan.getPlanName());
		}
	}
	
	
	
	
	/**
	 * 结果整理列表查询
	 * add by 王再冉
	 * 2014-1-11  上午10:19:40
	 * desc : 
	 * @param query			搜索框关键字
	 * @param assessPlanId	计划id
	 * @param typeId		树节点类型
	 * @param type			折叠数类型
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	@SuppressWarnings("unchecked")
	@RecordLog("查询风险整理风险、组织、目标、流程信息")
	public ArrayList<HashMap<String, String>> findRiskReList(String query, String assessPlanId, String typeId, String type){
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
		sql.append(" SELECT obj.id,obj.RISK_NAME,obj.PARENT_NAME,count(1) as COUNT,obj.DELETE_ESTATUS FROM t_rm_risk_score_object obj ");
		sql.append(" LEFT JOIN t_rm_rang_object_dept_emp r ON r.SCORE_OBJECT_ID = obj.ID ");
		sql.append(" WHERE ");
		if(!"root".equalsIgnoreCase(typeId)){
			//如果节点不为root 则是树节点下的过滤查询
			sql.append(" obj.ID_SEQ like :likeTypeId and ");
		}
		sql.append(" obj.ASSESS_PLAN_ID =  :assessPlanId ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){
				sql.append(" and (obj.RISK_CODE like :likeQuery or obj.RISK_NAME like :likeQuery) ");
			}
		}
		sql.append(" GROUP BY obj.RISK_ID ");
		sql.append(" ORDER BY obj.RISK_ID ");
		if(StringUtils.isNotBlank(sql)){
			SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
			sqlQuery.setParameter("assessPlanId", assessPlanId);
			if(StringUtils.isNotBlank(query)){
				if(!query.matches("\\d*")){//判断查询条件是否为数字
					sqlQuery.setParameter("likeQuery", "%"+query+"%");
				}
			}
			
			if(StringUtils.isNotBlank(typeId)){
				if(!"root".equalsIgnoreCase(typeId)){
					sqlQuery.setParameter("likeTypeId", "%." + typeId + ".%");
				}
			}
			List<Object[]> list = sqlQuery.list();
			for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				/* obj.id,obj.RISK_NAME,obj.PARENT_NAME,count(1) as COUNT,obj.DELETE_ESTATUS */
				String id = null;
				String riskName = null;
				String riskParentName = null;
				String empCount = null;
				String riskStatusIcon = null;
				if(null != objects[0]){
					id = objects[0].toString();
				}if(null != objects[1]){
					riskName = objects[1].toString();
				}if(null != objects[2]){
					riskParentName = objects[2].toString();
				}if(null != objects[3]){
					empCount = objects[3].toString();
				}if(null != objects[4]){
					riskStatusIcon = objects[4].toString();
				}
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", id);
				map.put("riskName", riskName);
				map.put("riskParentName", riskParentName);
				map.put("empCount", empCount);
				map.put("deleteStatus", riskStatusIcon);
				if(riskStatusIcon.equalsIgnoreCase("1")){
					map.put("riskStatus", "icon-status-assess_mr");
				}else if(riskStatusIcon.equalsIgnoreCase("2")){
					map.put("riskStatus", "icon-status-assess_new");
				}
				maps.add(map);
			}
		}
        
        return maps;
	}
	/**
	 * 风险部门主管和领导审批列表查询
	 * add by 王再冉
	 * 2014-1-11  下午3:01:50
	 * desc : 
	 * @param query			搜索框关键字
	 * @param businessId	计划id
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	public ArrayList<HashMap<String, String>> findApproveTidyGrid(String query,String businessId){
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
		sql.append(" SELECT ob.risk_id,ob.risk_name ,parent.risk_name   parent_name,org.id   ORG_ID,org.org_name,rode.score_emp_id,e.emp_name,ob.DELETE_ESTATUS ,ob.id as objectId ");
		sql.append(" FROM t_rm_rang_object_dept_emp rode LEFT JOIN t_rm_risk_score_object ob on rode.score_object_id = ob.id ");
//		sql.append(" LEFT JOIN t_rm_risks risk on risk.id = ob.risk_id ");
		sql.append(" LEFT JOIN t_rm_risks parent ON parent.id = ob.parent_Id ");
		sql.append(" LEFT JOIN t_sys_employee e ON e.id = rode.score_emp_id ");
		sql.append(" LEFT JOIN t_sys_emp_org eo ON eo.emp_id = e.id ");
		sql.append(" LEFT JOIN t_sys_organization org ON org.id = eo.org_id ");
		sql.append(" WHERE ob.assess_plan_id =:businessId and ob.risk_id is not null ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){
				sql.append(" and (ob.risk_name like :likeQuery or parent.risk_name like :likeQuery or org.org_name like :likeQuery or e.emp_name like :likeQuery ) ");
			}
		}
		sql.append(" order by ob.risk_name ");
		System.out.println("!!!!!!!!!!风险部门主管和领导审批列表查询SQL:="+sql.toString());
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("businessId", businessId);
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
            String riskId = "";
            String riskName = "";
            String parentName = "";
            String orgId = "";
            String orgName = "";
            String empId = "";
            String empName = "";
            String deleteStatus = "";
            String objectId = "";
            if(null != o[0]){
            	riskId = o[0].toString();
            }if(null != o[1]){
            	riskName = o[1].toString();
            }if(null != o[2]){
            	parentName = o[2].toString();//上级风险名称
            }if(null != o[3]){
            	orgId = o[3].toString();
            }if(null != o[4]){
            	orgName = o[4].toString();
            }if(null != o[5]){
            	empId = o[5].toString();
            }if(null != o[6]){
            	empName = o[6].toString();
            }
            if(null != o[7]){
            	deleteStatus = o[7].toString();
            }
            if(null != o[8]){
            	objectId = o[8].toString();
            }
            HashMap<String, String> map = new HashMap<String, String>();
			map.put("riskId", riskId);
			map.put("riskName", riskName);
			map.put("parentRiskName", parentName);
			map.put("deptId", orgId);
			map.put("deptName", orgName);
			map.put("empId", empId);
			map.put("assessEmpId", empName);
			map.put("deleteStatus", deleteStatus);
			map.put("scoreObjectId", objectId);
			maps.add(map);
		}
		return maps;
	}
	/**
	 * 查找计划下新增的风险
	 * add by 王再冉
	 * 2014-1-14  上午11:58:41
	 * desc : 
	 * @param businessId 计划id
	 * @return 
	 * List<String>
	 */
	public List<String> findNewRisksByplanId(String businessId){
		StringBuffer sql = new StringBuffer();
		List<String> riskIds = new ArrayList<String>();
		sql.append(" SELECT ob.risk_id,ob.risk_name ");
		sql.append(" FROM t_rm_rang_object_dept_emp rode LEFT JOIN t_rm_risk_score_object ob on rode.score_object_id = ob.id ");
		//sql.append(" LEFT JOIN t_rm_risks r ON r.id = ob.risk_id ");
		sql.append(" WHERE ob.delete_estatus = '2' and ob.assess_plan_id =:businessId");
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("businessId", businessId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
            String riskId = "";
            if(null != o[0]){
            	riskId = o[0].toString();
            	riskIds.add(riskId);
            }
		}
		return riskIds;
	}
	/**
	 * 更新新增风险的状态
	 * add by 王再冉
	 * 2014-1-14  下午1:00:07
	 * desc : 
	 * @param businessId 计划id
	 * void
	 */
	@Transactional
	public void updateNewRisksStatusByids(String businessId){
		List<String> riskIdList = this.findNewRisksByplanId(businessId);
		if(riskIdList.size()>0){
			StringBuffer riskIds = new StringBuffer();
			for(String id : riskIdList){
				//riskIds = riskIds + "," + id;
				riskIds = riskIds.append("," + id);
			}
			if(!"".equals(riskIds.toString())){
				o_riskDAO.createQuery("UPDATE Risk SET DELETE_ESTATUS='1' where id in (:ids)")
				.setParameterList("ids", StringUtils.split(riskIds.toString(),",")).executeUpdate();
			}
		}
	}

	/**
	 * 结束simple风险辨识
	 * add by 王再冉
	 * 2014-1-15  下午1:43:19
	 * desc : 
	 * @param executionId 工作流程id
	 * @param businessId 计划id
	 * void
	 */
	public void finishRiskIdentify(String executionId, String businessId){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		Map<String, Object> variables = new HashMap<String, Object>();
//		//将新增风险状态改为‘1’
//		this.updateNewRisksStatusByids(businessId);
//		assessPlan.setDealStatus(Contents.DEAL_STATUS_FINISHED);//处理状态为"已完成"
//		o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
		
		syncRisks(businessId,assessPlan.getSchm());
		o_jbpmBO.doProcessInstance(executionId, variables);
	}
	/**
	 * 查询风险辨识列表
	 * add by 王再冉
	 * 2014-2-17  下午5:00:30
	 * desc : 
	 * @param businessId	计划id
	 * @param empIds		辨识人id集合
	 * @param query			搜索框关键字
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	public ArrayList<HashMap<String, Object>> findIdentifyListByBusinessId(String businessId,String scoreEmpId,String scoreOrgId ,String query) {
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
		//吉志强修改，返回汇总意见
		sql.append(" SELECT distinct b.risk_id,b.PARENT_ID,b.RISK_NAME,d.RISK_NAME   parentName,f.EMP_NAME,z.EDIT_IDEA_CONTENT,leadEditIdea.EDIT_IDEA_CONTENT as leadContent,b.id as objectId,leadEditIdea.id as hIdeaId ,r.id as hResponseId ,r.EDIT_IDEA_CONTENT Hresponse, q.EDIT_IDEA_CONTENT response ");
		sql.append(" FROM t_rm_rang_object_dept_emp a ");
		sql.append(" LEFT JOIN t_rm_risk_score_object b ON a.SCORE_OBJECT_ID = b.ID ");
		sql.append(" LEFT JOIN  t_rm_risk_org_temp e ON a.score_object_id = e.object_id");
		sql.append(" LEFT JOIN t_rm_risks d on b.parent_id=d.id ");
		sql.append(" LEFT JOIN t_sys_employee f on f.id = a.score_emp_id ");
		sql.append(" LEFT JOIN t_rm_edit_idea z on z.OBJECT_DEPT_EMP_ID = a.id ");
		//吉志强修改，返回汇总意见
		sql.append(" LEFT JOIN t_rm_dept_lead_circusee leadCircusee on leadCircusee.SCORE_OBJECT_ID = a.SCORE_OBJECT_ID ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_idea leadEditIdea on leadEditIdea.DEPT_LEAD_CIRCUSEE_ID = leadCircusee.ID ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_response r ON r.dept_lead_circusee_id = leadCircusee.id");
		sql.append(" LEFT JOIN t_rm_edit_response q ON q.OBJECT_DEPT_EMP_ID = a.id");
		sql.append(" WHERE b.assess_plan_id = :businessId AND e.id = a.SCORE_DEPT_ID and leadCircusee.dept_lead_emp_id = :scoreEmpId and e.org_id =:scoreOrgId and b.risk_id is not null ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){
				sql.append(" and (r.risk_name like :likeQuery or d.risk_name like :likeQuery or f.EMP_NAME like :likeQuery ) ");
			}
		}
		sql.append(" order by d.RISK_NAME ");
		//吉志强 2017年4月18日11:00:03测试 获取辨识列表的各个反馈意见的sql
		System.out.println("@@@@@@ 获取辨识列表的各个反馈意见的sql:=  "+sql.toString());
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("businessId", businessId);
        sqlQuery.setParameter("scoreEmpId", scoreEmpId);
        sqlQuery.setParameter("scoreOrgId", scoreOrgId);
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
            String riskId = "";//风险id
            String riskName = "";//风险名称
            String parentName = "";//上级风险名称
            String empName = "";//辨识人名称
            String empEditIdea = "";//辨识人意见
            String leadContent = "";
            String objectId = "";
            String hResponse = null;
            String response = null;
            String hIdeaId = null;
            String hResponseId = null;
            if(null != o[0]){
            	riskId = o[0].toString();
            }if(null != o[2]){
            	riskName = o[2].toString();
            }if(null != o[3]){
            	parentName = o[3].toString();
            }if(null != o[4]){
            	empName = o[4].toString();
            }if(null != o[5]){
            	empEditIdea = o[5].toString();
            }if(null != o[6]){
            	leadContent = o[6].toString();
            }if(null != o[7]){
            	objectId = o[7].toString();
            }if(null != o[8]){
            	hIdeaId = o[8].toString();
            }if(null != o[9]){
            	hResponseId = o[9].toString();
            }if(null != o[10]){
            	hResponse = o[10].toString();
            }if(null != o[11]){
            	response = o[11].toString();
            }
            
            HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("riskId", riskId);
			map.put("riskName", riskName);
			map.put("parentRiskName", parentName);
			map.put("empEditIdea", empEditIdea);
			map.put("assessEmpId", empName);
			map.put("leadContent", leadContent);
			map.put("objectScoreId", objectId);
			map.put("hIdeaId", hIdeaId);
			map.put("hResponseId", hResponseId);
			map.put("response", response);
			map.put("hResponse", hResponse);
			mapsList.add(map);
		}
        return mapsList;
	}
	/**
	 * 风险辨识按部门添加风险
	 * add by 王再冉
	 * 2014-2-18  上午10:07:16
	 * desc : 
	 * @param orgIds	部门id
	 * @param planId	计划id
	 * @return 
	 * Boolean
	 */
	public Boolean saveScoreObjectsAndScoreDeptsByOrgIds(String orgIds, String planId,String typeId){
		List<RiskScoreObject> saveObjList = new ArrayList<RiskScoreObject>();
		List<RiskScoreDept> saveDeptList = new ArrayList<RiskScoreDept>();
		
		List<RiskOrgTemp> riskOrgTempList = new ArrayList<RiskOrgTemp>();
		List<RiskOrgRelationTemp> riskOrgRelationTempList = new ArrayList<RiskOrgRelationTemp>();
		List<String> saveOrgIdList = new ArrayList<String>();
		List<String> orgIdList = new ArrayList<String>();
		Map<String,String> savedRiskIdsMap = new HashMap<String, String>();//已经保存的风险（打分对象只保存一次）
		if (StringUtils.isNotBlank(orgIds)) {
			String[] idArray = orgIds.split(",");
			for (String id : idArray) {
				orgIdList.add(id);//部门id集合
			}
		}
		//查询计划下保存的打分对象，放到map
		List<RiskScoreObject> findObjList = o_scoreObjectBO.findRiskScoreObjByplanId(planId);
		Map<String, String> findObjMap = new HashMap<String, String>();
		for(RiskScoreObject findObj : findObjList){
			if(null != findObj.getRisk()){
				findObjMap.put(findObj.getRisk().getId(), findObj.getAssessPlan().getId());
			}
		}
		RiskAssessPlan assessP = this.o_riskAssessPlanDAO.get(planId);
		ArrayList<HashMap<String, String>> riskOrgMapList = this.findRisksByOrgIds(orgIdList,false,typeId,assessP.getPlanType());
		for(HashMap<String, String> hashmap : riskOrgMapList){
			String riskId = hashmap.get("riskId");
			//不存在已保存的打分对象
			if(!findObjMap.containsKey(riskId)){
				RiskScoreObject scoreObj = new RiskScoreObject();
				scoreObj.setId(Identities.uuid());
				scoreObj.setRisk(new Risk(riskId));
				scoreObj.setAssessPlan(new RiskAssessPlan(planId));
				//判断之前保存的打分对象中是否有重复的数据
				if(null == savedRiskIdsMap.get(riskId)){
					//批量保存的打分对象list
					saveObjList.add(scoreObj);
					savedRiskIdsMap.put(riskId, scoreObj.getId());
				}else{
					scoreObj.setId(savedRiskIdsMap.get(riskId));
				}
				
				String orgId = hashmap.get("orgId");
				String eType = hashmap.get("eType");
				String empId = hashmap.get("empId");
				RiskScoreDept scoreDept = new RiskScoreDept();
				scoreDept.setId(Identities.uuid());
				scoreDept.setScoreObject(scoreObj);
				scoreDept.setOrganization(new SysOrganization(orgId));
				//部门类型
				scoreDept.setOrgType(eType);
				saveDeptList.add(scoreDept);
				
				///////  增加riskOrgTemp表为该表赋值    begin
				RiskOrgTemp riskOrgTemp = new RiskOrgTemp();
				riskOrgTemp.setId(scoreDept.getId());//为了不更改原逻辑，为新增的表 t_rm_risk_org_temp的主键与t_rm_risk_score_dept一致
				riskOrgTemp.setRiskScoreObject(scoreObj);
				riskOrgTemp.setPlanId(planId);
				riskOrgTemp.setEmp(new SysEmployee(empId));
				//running正在进行辨识的风险
				riskOrgTemp.setStatus("running");
				riskOrgTemp.setSysOrganization(new SysOrganization(orgId));
				riskOrgTemp.setType(eType);
				riskOrgTempList.add(riskOrgTemp);
				///////  增加riskOrgTemp表为该表赋值    end
				
			///////  增加riskOrgTemp表为该表赋值    begin
			RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
			riskOrgRelationTemp.setId(scoreDept.getId());
			riskOrgRelationTemp.setRiskScoreObject(scoreObj);
			riskOrgRelationTemp.setRisk(new Risk(riskId));
			riskOrgRelationTemp.setSysOrganization(new SysOrganization(orgId));
			riskOrgRelationTemp.setType(eType);
			riskOrgRelationTempList.add(riskOrgRelationTemp);
			///////  增加riskOrgTemp表为该表赋值    end
				
				
				if(!saveOrgIdList.contains(orgId)){
					saveOrgIdList.add(orgId);
				}
			}
		}
		if(orgIdList.size() > saveOrgIdList.size()){
			for(String orgid : orgIdList){
				if(!saveOrgIdList.contains(orgid)){
					RiskScoreObject scoreobj = new RiskScoreObject();
					scoreobj.setId(Identities.uuid());
					scoreobj.setAssessPlan(new RiskAssessPlan(planId));
					saveObjList.add(scoreobj);//批量保存的打分对象list
					
					RiskScoreDept scoredept = new RiskScoreDept();
					scoredept.setId(Identities.uuid());
					scoredept.setScoreObject(scoreobj);
					scoredept.setOrganization(new SysOrganization(orgid));
					saveDeptList.add(scoredept);
					
				///////  增加riskOrgTemp表为该表赋值    begin
					RiskOrgTemp riskOrgTemp = new RiskOrgTemp();
					riskOrgTemp.setId(scoredept.getId());//为了不更改原逻辑，为新增的表 t_rm_risk_org_temp的主键与t_rm_risk_score_dept一致
					riskOrgTemp.setRiskScoreObject(scoreobj);
					riskOrgTemp.setPlanId(planId);
					//running正在进行辨识的风险
					riskOrgTemp.setStatus("running");
					riskOrgTemp.setSysOrganization(new SysOrganization(orgid));
//					riskOrgTemp.setType(eType);
					riskOrgTempList.add(riskOrgTemp);
					///////  增加riskOrgTemp表为该表赋值    end
					
				///////  增加riskOrgTemp表为该表赋值    begin
					RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
					riskOrgRelationTemp.setId(scoredept.getId());
					riskOrgRelationTemp.setRiskScoreObject(scoreobj);
					riskOrgRelationTemp.setSysOrganization(new SysOrganization(orgid));
					riskOrgRelationTempList.add(riskOrgRelationTemp);
				///////  增加riskOrgTemp表为该表赋值    end
					
				}
			}
		}
		o_riskScoreBO.saveRiskScoreObjectsSql(saveObjList);//批量保存打分对象
		//o_riskScoreBO.saveRiskScoreDeptsSql(saveDeptList);//批量保存打分部门
		//增加riskOrgTemp表为该表赋值    
		o_riskScoreBO.saveRiskOrgTemp(riskOrgTempList);
		riskOrgTempBO.saveRiskOrgRelationBatch(riskOrgRelationTempList);
		return true;
	}
	
	
	
	
	/**saveScoreObjectsAndScoreDeptsByOrgIds函数备份，保留原逻辑，该函数只是备份，没有被调用 --->吉志强
	 * @param orgIds
	 * @param planId
	 * @return
	 */
	public Boolean saveScoreObjectsAndScoreDeptsByOrgIdsBAKBYJZQ(String orgIds, String planId){
		List<RiskScoreObject> saveObjList = new ArrayList<RiskScoreObject>();
		List<RiskScoreDept> saveDeptList = new ArrayList<RiskScoreDept>();
		List<String> saveOrgIdList = new ArrayList<String>();
		List<String> orgIdList = new ArrayList<String>();
		Map<String,String> savedRiskIdsMap = new HashMap<String, String>();//已经保存的风险（打分对象只保存一次）
		if (StringUtils.isNotBlank(orgIds)) {
			String[] idArray = orgIds.split(",");
			for (String id : idArray) {
				orgIdList.add(id);//部门id集合
			}
		}
		//查询计划下保存的打分对象，放到map
		List<RiskScoreObject> findObjList = o_scoreObjectBO.findRiskScoreObjByplanId(planId);
		Map<String, String> findObjMap = new HashMap<String, String>();
		for(RiskScoreObject findObj : findObjList){
			if(null != findObj.getRisk()){
				findObjMap.put(findObj.getRisk().getId(), findObj.getAssessPlan().getId());
			}
		}
		ArrayList<HashMap<String, String>> riskOrgMapList = this.findRisksByOrgIds(orgIdList,false,null);
		for(HashMap<String, String> hashmap : riskOrgMapList){
			String riskId = hashmap.get("riskId");
			//不存在已保存的打分对象
			if(!findObjMap.containsKey(riskId)){
				RiskScoreObject scoreObj = new RiskScoreObject();
				scoreObj.setId(Identities.uuid());
				scoreObj.setRisk(new Risk(riskId));
				scoreObj.setAssessPlan(new RiskAssessPlan(planId));
				//判断之前保存的打分对象中是否有重复的数据
				if(null == savedRiskIdsMap.get(riskId)){
					//批量保存的打分对象list
					saveObjList.add(scoreObj);
					savedRiskIdsMap.put(riskId, scoreObj.getId());
				}else{
					scoreObj.setId(savedRiskIdsMap.get(riskId));
				}
				
				String orgId = hashmap.get("orgId");
				String eType = hashmap.get("eType");
				String empId = hashmap.get("empId");
				RiskScoreDept scoreDept = new RiskScoreDept();
				scoreDept.setId(Identities.uuid());
				scoreDept.setScoreObject(scoreObj);
				scoreDept.setOrganization(new SysOrganization(orgId));
				//部门类型
				scoreDept.setOrgType(eType);
				saveDeptList.add(scoreDept);
				
				
				
				
				if(!saveOrgIdList.contains(orgId)){
					saveOrgIdList.add(orgId);
				}
			}
		}
		if(orgIdList.size() > saveOrgIdList.size()){
			for(String orgid : orgIdList){
				if(!saveOrgIdList.contains(orgid)){
					RiskScoreObject scoreobj = new RiskScoreObject();
					scoreobj.setId(Identities.uuid());
					scoreobj.setAssessPlan(new RiskAssessPlan(planId));
					saveObjList.add(scoreobj);//批量保存的打分对象list
					
					RiskScoreDept scoredept = new RiskScoreDept();
					scoredept.setId(Identities.uuid());
					scoredept.setScoreObject(scoreobj);
					scoredept.setOrganization(new SysOrganization(orgid));
					saveDeptList.add(scoredept);
				}
			}
		}
		o_riskScoreBO.saveRiskScoreObjectsSql(saveObjList);//批量保存打分对象
		o_riskScoreBO.saveRiskScoreDeptsSql(saveDeptList);//批量保存打分部门
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 根据部门id集合查询相关风险事件
	 * add by 王再冉
	 * 2014-2-17  下午5:03:57
	 * desc : 
	 * @param orgIds	部门id集合
	 * @param isMain	是否责任部门
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	public ArrayList<HashMap<String, String>> findRisksByOrgIds(List<String> orgIds,Boolean isMain,String typeId,String planType) {
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, String>> mapsList = new ArrayList<HashMap<String, String>>();
		sql.append(" SELECT distinct ro.ETYPE,ro.ORG_ID,r.ID   RISK_ID ,ro.EMP_ID ");
		sql.append(" FROM t_rm_risk_org ro ");
		sql.append(" LEFT JOIN t_rm_risks r ON r.ID = ro.RISK_ID ");
		sql.append(" WHERE ORG_ID IN (:orgIds) AND r.DELETE_ESTATUS = '1' AND r.IS_RISK_CLASS = 're'");
		//过滤掉还在计划中没有执行完的
		sql.append(" and r.id not in ( SELECT a.RISK_ID FROM t_rm_risk_score_object a, t_rm_risk_org_temp b, t_rm_risk_assess_plan c WHERE a.ID = b.OBJECT_ID AND a.ASSESS_PLAN_ID = c.ID AND b.`STATUS` = 'running' AND c.PLAN_TYPE =:planType )");
		
		if(isMain){
			sql.append(" AND ro.ETYPE = 'M' ");
		}
		if (StringUtils.isNotBlank(typeId)) {
			sql.append(" AND r.SCHM = :typeId");
		}
		SQLQuery sqlQuery = o_riskOrgDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("orgIds", orgIds);
        if (StringUtils.isNotBlank(typeId)) {
        	sqlQuery.setParameter("typeId", typeId);
		}
        sqlQuery.setParameter("planType", planType);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String eType = "";
			String orgId = "";
            String riskId = "";
            String empId = "";
            if(null != o[0]){
            	eType = o[0].toString();
            }if(null != o[1]){
            	orgId = o[1].toString();
            }if(null != o[2]){
            	riskId = o[2].toString();
            }
            if(null != o[3]){
            	empId = o[3].toString();
            }
            HashMap<String, String> map = new HashMap<String, String>();
			map.put("riskId", riskId);
			map.put("eType", eType);
			map.put("orgId", orgId);
			map.put("empId", empId);
			mapsList.add(map);
		}
        return mapsList;
	}
	
	public ArrayList<HashMap<String, String>> findRisksByOrgIds(List<String> orgIds,Boolean isMain,String typeId) {
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, String>> mapsList = new ArrayList<HashMap<String, String>>();
		sql.append(" SELECT distinct ro.ETYPE,ro.ORG_ID,r.ID   RISK_ID ,ro.EMP_ID ");
		sql.append(" FROM t_rm_risk_org ro ");
		sql.append(" LEFT JOIN t_rm_risks r ON r.ID = ro.RISK_ID ");
		sql.append(" WHERE ORG_ID IN (:orgIds) AND r.DELETE_ESTATUS = '1' AND r.IS_RISK_CLASS = 're'");
		if(isMain){
			sql.append(" AND ro.ETYPE = 'M' ");
		}
		if (StringUtils.isNotBlank(typeId)) {
			sql.append(" AND r.SCHM = :typeId");
		}
		SQLQuery sqlQuery = o_riskOrgDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("orgIds", orgIds);
        if (StringUtils.isNotBlank(typeId)) {
        	sqlQuery.setParameter("typeId", typeId);
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String eType = "";
			String orgId = "";
            String riskId = "";
            String empId = "";
            if(null != o[0]){
            	eType = o[0].toString();
            }if(null != o[1]){
            	orgId = o[1].toString();
            }if(null != o[2]){
            	riskId = o[2].toString();
            }
            if(null != o[3]){
            	empId = o[3].toString();
            }
            HashMap<String, String> map = new HashMap<String, String>();
			map.put("riskId", riskId);
			map.put("eType", eType);
			map.put("orgId", orgId);
			map.put("empId", empId);
			mapsList.add(map);
		}
        return mapsList;
	}
	/**
	 * 风险辨识任务分配列表查询
	 * add by 王再冉
	 * 2014-2-18  下午3:43:48
	 * desc : 
	 * @param businessId	计划id
	 * @param query			搜索框关键字
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	public ArrayList<HashMap<String, Object>> findIdentifyRisksByBusinessId(String businessId,String query) {
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
		List<String> orgIds = new ArrayList<String>();
		String empId = UserContext.getUser().getEmpid();
		List<SysEmpOrg> empOrgs = o_riskScoreBO.findEmpDeptsByEmpId(empId);
		for(SysEmpOrg emporg : empOrgs){
			orgIds.add(emporg.getSysOrganization().getId());
		}
		sql.append(" SELECT so.RISK_ID as id,so.parent_id,so.risk_name,pr.risk_name   parent_risk_name, so.id as objectId ");
		sql.append(" FROM t_rm_risk_score_object so ");
		sql.append(" LEFT JOIN t_rm_risks pr ON pr.ID = so.parent_id  ");
		sql.append(" LEFT JOIN t_rm_risk_org_temp sd ON sd.object_id = so.id ");
		sql.append(" WHERE SO.ASSESS_PLAN_ID = :businessId AND sd.org_id IN (:orgIds) ");
		sql.append(" AND (sd.ETYPE = 'M' OR (sd.ETYPE = 'A' AND so.DELETE_ESTATUS != 2)) ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){
				sql.append(" and (so.risk_name like :likeQuery or pr.risk_name like :likeQuery) ");
			}
		}
		sql.append(" AND so.risk_id is NOT NULL ORDER BY so.DELETE_ESTATUS desc , so.risk_name  ");
		System.out.println("@@@@@@@@@@@@@ 风险辨识任务分配风险列表查询sql：="+sql.toString());
		SQLQuery sqlQuery = o_riskScoreObjectDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("businessId", businessId);
        sqlQuery.setParameterList("orgIds", orgIds);
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
            String riskId = null;
            String parentRiskId = null;
            String riskName = null;
            String parentName = null;
            String objectId = null;
            if(null != o[0]){
            	riskId = o[0].toString();
            }if(null != o[1]){
            	parentRiskId = o[1].toString();
            }if(null != o[2]){
            	riskName = o[2].toString();
            }if(null != o[3]){
            	parentName = o[3].toString();
            }
            if(null != o[4]){
            	objectId = o[4].toString();
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("riskId", riskId);
			map.put("riskName", riskName);
			map.put("parentRiskName", parentName);
			map.put("parentRiskId", parentRiskId);
			map.put("objectId", objectId);
			mapsList.add(map);
		}
        return mapsList;
	}
	
	
	public ArrayList<HashMap<String, Object>> findIdentifyRisksByBusinessId_bak(String businessId,String query) {
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
		List<String> orgIds = new ArrayList<String>();
		String empId = UserContext.getUser().getEmpid();
		List<SysEmpOrg> empOrgs = o_riskScoreBO.findEmpDeptsByEmpId(empId);
		for(SysEmpOrg emporg : empOrgs){
			orgIds.add(emporg.getSysOrganization().getId());
		}
		sql.append(" SELECT r.id,r.parent_id,so.risk_name,pr.risk_name   parent_risk_name ");
		sql.append(" FROM t_rm_risk_score_object so ");
		sql.append(" LEFT JOIN t_rm_risks r ON r.id = so.RISK_ID ");
		sql.append(" LEFT JOIN t_rm_risks pr ON pr.ID = r.parent_id  ");
		sql.append(" LEFT JOIN t_rm_risk_score_dept sd ON sd.score_object_id = so.id ");
		sql.append(" WHERE SO.ASSESS_PLAN_ID = :businessId AND sd.org_id IN (:orgIds) ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){
				sql.append(" and (r.risk_name like :likeQuery or pr.risk_name like :likeQuery) ");
			}
		}
		sql.append(" AND so.risk_id is NOT NULL ORDER BY pr.risk_name  ");
		System.out.println("@@@@@@@@@@@@@ 风险辨识任务分配风险列表查询sql：="+sql.toString());
		SQLQuery sqlQuery = o_riskScoreObjectDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("businessId", businessId);
        sqlQuery.setParameterList("orgIds", orgIds);
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
            String riskId = "";
            String parentRiskId = "";
            String riskName = "";
            String parentName = "";
            
            if(null != o[0]){
            	riskId = o[0].toString();
            }if(null != o[1]){
            	parentRiskId = o[1].toString();
            }if(null != o[2]){
            	riskName = o[2].toString();
            }if(null != o[3]){
            	parentName = o[3].toString();
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("riskId", riskId);
			map.put("riskName", riskName);
			map.put("parentRiskName", parentName);
			map.put("parentRiskId", parentRiskId);
			mapsList.add(map);
		}
        return mapsList;
	}
	
	
	/**
	 * 查询风险为空的辨识打分对象
	 * add by 王再冉
	 * 2014-4-18  下午4:03:19
	 * desc : 
	 * @param planId
	 * @return 
	 * RiskScoreObject
	 */
	@SuppressWarnings({ "unchecked" })
	public RiskScoreObject findnullRiskObjByPlanId(String planId) {
		Criteria c = o_riskScoreObjectDAO.createCriteria();
		List<RiskScoreObject> list = null;
		if(StringUtils.isNotBlank(planId)){
			//c.add(Restrictions.and(Restrictions.eq("assessPlan.id", planId), Restrictions.isNull("risk.id")));
			c.add(Restrictions.and(Restrictions.eq("assessPlan.id", planId), Restrictions.isNull("riskId")));
		}
		list = c.list();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
}
