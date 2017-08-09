package com.fhd.icm.business.bpm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.defect.DefectDAO;
import com.fhd.dao.icm.icsystem.ConstructPlanDAO;
import com.fhd.dao.icm.icsystem.ConstructPlanRelaOrgDAO;
import com.fhd.dao.icm.icsystem.ConstructPlanRelaStandardEmpDAO;
import com.fhd.dao.icm.icsystem.ConstructRelaProcessDAO;
import com.fhd.dao.icm.icsystem.DiagnosesDAO;
import com.fhd.dao.icm.icsystem.DiagnosesRelaDefectDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.process.ProcessRelaOrgDAO;
import com.fhd.entity.icm.defect.Defect;
import com.fhd.entity.icm.icsystem.ConstructPlan;
import com.fhd.entity.icm.icsystem.ConstructPlanRelaOrg;
import com.fhd.entity.icm.icsystem.ConstructPlanRelaStandard;
import com.fhd.entity.icm.icsystem.ConstructPlanRelaStandardEmp;
import com.fhd.entity.icm.icsystem.ConstructRelaProcess;
import com.fhd.entity.icm.icsystem.Diagnoses;
import com.fhd.entity.icm.icsystem.DiagnosesRelaDefect;
import com.fhd.entity.icm.standard.StandardRelaOrg;
import com.fhd.entity.icm.standard.StandardRelaProcessure;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessRelaOrg;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.icsystem.ConstructPlanRelaStandardBO;
import com.fhd.icm.business.process.ProcessPointBO;
import com.fhd.icm.business.process.ProcessRiskBO;
import com.fhd.icm.web.controller.bpm.icsystem.ConstructPlanBpmObject;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.organization.EmployeeBO;

/**
 * 体系建设工作流BO.
 * @author 宋佳
 * @version
 * @since Ver 1.1
 * @Date 2013-4-15 下午3:22
 * @see
 */
@Service
@SuppressWarnings({"rawtypes","deprecation","unchecked"})
public class ConstructPlanBpmBO {

	private static final Log LOG = LogFactory.getLog(ConstructPlanBpmBO.class);
	
	@Autowired
	private RoleBO o_sysRoleBO;
	@Autowired
	private ConstructPlanRelaStandardBO o_constructPlanRelaStandardBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	@Autowired
	private ProcessRiskBO o_processRiskBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private DiagnosesDAO o_diagnosesDAO;
	@Autowired
	private ConstructRelaProcessDAO o_constructRelaProcessDAO;
	@Autowired
	private ConstructPlanDAO o_constructPlanDAO;
	@Autowired
	private ConstructPlanRelaOrgDAO o_constructPlanRelaOrgDAO;
	@Autowired
	private ConstructPlanRelaStandardEmpDAO o_constructPlanRelaStandardEmpDAO;
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private DefectDAO o_defectDAO;
	@Autowired
	private DiagnosesRelaDefectDAO o_diagnosesRelaDefectDAO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private ProcessRelaOrgDAO o_processRelaOrgDAO;
	
	
	/**
	 * 根据评价计划id查询评价计划制定审批人.
	 * @param roleKye :配置文件中角色的属性名称
	 * @return String
	 */
	public String findConstructPlanEmpIdByRole(String roleKey){
		String empId = "";
		//配置文件读取文件路径
		String roleName = ResourceBundle.getBundle("application").getString(roleKey);
		List<SysEmployee> employeeList = o_sysRoleBO.getEmpByCorpAndRole(roleName);
		if(null != employeeList && employeeList.size()>0){
			SysEmployee sysEmployee = employeeList.get(0);
			if(null != sysEmployee){
				empId = sysEmployee.getId();
			}
		}
		return empId;
	}
	/**
	 * 第1步工作流提交--评价计划制定.
	 * @param executionId
	 * @param businessId
	 */
	@Transactional
	public void mergeConstructPlanStatusBpmPlanDraft(String executionId, String businessId){
		ConstructPlan constructPlan = o_constructPlanDAO.get(businessId);
		if(null != constructPlan){
			String empId = this.findConstructPlanEmpIdByRole("ICDepartmentMinister");
			constructPlan.setStatus(Contents.STATUS_SUBMITTED);
			constructPlan.setDealStatus(Contents.DEAL_STATUS_HANDLING);
			if (!"".equals(empId)) {
				if(StringUtils.isBlank(executionId)){
					//菜单触发提交开启工作流
					Map<String, Object> variables = new HashMap<String, Object>();
					String entityType = "icmICSystemPlan";
					variables.put("entityType", entityType);
					variables.put("id", businessId);
					variables.put("name",constructPlan.getName());
					variables.put("ICDepartmentStaffEmpId", UserContext.getUser().getEmpid());
					executionId = o_jbpmBO.startProcessInstance(entityType,variables);
					
					//工作流提交
					Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
					variablesBpmTwo.put("ICDepartmentMinisterEmpId", empId);
					o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
				}else{
					//工作流提交
					Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
					variablesBpmTwo.put("ICDepartmentMinisterEmpId", empId);
					o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
				}
			}
		}
	}
	/**
	 * 第2步工作流提交--评价计划制定审批.
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 */
	@Transactional
	public void mergeConstructPlanApproval(String executionId, String businessId, String isPass, String examineApproveIdea){
		ConstructPlan constructPlan = o_constructPlanDAO.get(businessId);
		if(null != constructPlan){
			
			if ("no".equals(isPass)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				// 审批通过进入待发布状态，由计划创建人员进行计划发布？ 是否需要创建将角色
				SysEmployee empId = constructPlan.getCreateBy();
				if (!"".equals(empId.getId())) {
					//工作流提交
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put("ICDepartmentMinisterEmpId", empId.getId());
					variables.put("path", isPass);
					variables.put("examineApproveIdea", examineApproveIdea);
					o_jbpmBO.doProcessInstance(executionId, variables);
				}
			}
		}
	}
	/**
	 * @author 宋佳
	 * @param executionId,constructPlanId
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeConstructPlanPublish(String executionId,String constructPlanId){
		
		List<ConstructPlanBpmObject> list = new ArrayList<ConstructPlanBpmObject>();
		List<ConstructPlanRelaStandard> constructPlanRelaStandardList = o_constructPlanRelaStandardBO.findConstructPlanRelaStandardListByConstructPlanId(constructPlanId);
		
		if(null != constructPlanRelaStandardList && constructPlanRelaStandardList.size()>0){
			//业务操作部分1：更新计划状态；2：初始化流程和诊断表信息
			//获取小组长id
			Criteria cri = o_constructPlanRelaOrgDAO.createCriteria();
			cri.add(Restrictions.eq("constructPlan.id",constructPlanId));
			cri.add(Restrictions.eq("type", Contents.EMP_RESPONSIBILITY));
			ConstructPlanRelaOrg constructPlanRelaOrg = (ConstructPlanRelaOrg) cri.uniqueResult();
			String constructPlanLeader = constructPlanRelaOrg.getEmp().getId();
			//  标准对应的人员列表
			ConstructPlanBpmObject constructPlanBpmObjectDiagnose = null;
			ConstructPlanBpmObject constructPlanBpmObjectEdit = null;
			Set<String> constructPlanRelaStandardEmpSet = this.getConstructPlanRealStandardEmp(constructPlanId);
			String testingDepartmentMinister = this.findConstructPlanEmpIdByRole("ICByTestingDepartmentMinister");
			for(String temp : constructPlanRelaStandardEmpSet){  //循环人员
				constructPlanBpmObjectDiagnose = new ConstructPlanBpmObject();
				constructPlanBpmObjectEdit = new ConstructPlanBpmObject();
				constructPlanBpmObjectDiagnose.setPath("path2");
				constructPlanBpmObjectEdit.setPath("path1");
				for(ConstructPlanRelaStandard constructPlanRelaStandard : constructPlanRelaStandardList){ //循环标准
					//标准所对应的人员
					ConstructPlanRelaStandardEmp constructPlanRelaStandardEmp = o_constructPlanRelaStandardBO.getPlanStandardEmp(constructPlanRelaStandard.getId());
					if(!constructPlanRelaStandardEmp.getConstructPlanRelaOrg().getEmp().getId().equals(temp)){
						continue;
					}else{
						if(constructPlanRelaStandard.getIsNormallyDiagnosis()){  //判断是合规诊断
							//新插入一条合规诊断
							Diagnoses diagnoses = new Diagnoses();
							diagnoses.setConstructPlanRelaStandard(constructPlanRelaStandard);
							diagnoses.setId(Identities.uuid());
							diagnoses.setConstructPlan(o_constructPlanDAO.get(constructPlanId));
							o_diagnosesDAO.merge(diagnoses);
							
							// 分发的执行id合并为
							if(StringUtils.isBlank(constructPlanBpmObjectDiagnose.getForeachExecutionId())){
								constructPlanBpmObjectDiagnose.setForeachExecutionId(diagnoses.getId());
							}else{
								constructPlanBpmObjectDiagnose.setForeachExecutionId(constructPlanBpmObjectDiagnose.getForeachExecutionId()+","+diagnoses.getId());
							}
							constructPlanBpmObjectDiagnose.setExecuteEmpId(constructPlanRelaStandardEmp.getConstructPlanRelaOrg().getEmp().getId());//合规诊断人员
						}
						if(constructPlanRelaStandard.getIsProcessEdit()){
							//新插入一条流程流程矩阵编写
							ConstructRelaProcess constructRelaProcess = new ConstructRelaProcess();
							constructRelaProcess.setId(Identities.uuid());
							constructRelaProcess.setConstructPlan(o_constructPlanDAO.get(constructPlanId));
							constructRelaProcess.setConstructPlanRelaStandard(constructPlanRelaStandard);
							constructRelaProcess.setActualStartDate(new Date());
							o_constructRelaProcessDAO.merge(constructRelaProcess);
							
							Set<StandardRelaProcessure> standardRelaProcessures = constructPlanRelaStandard.getStandard().getStandardRelaProcessure();
							Process process = null;
							for (StandardRelaProcessure standardRelaProcessure : standardRelaProcessures) {
								process =  standardRelaProcessure.getProcessure();
							}
							process.setDealStatus(Contents.DEAL_STATUS_HANDLING);
							o_processDAO.merge(process);
							if(StringUtils.isBlank(constructPlanBpmObjectEdit.getForeachExecutionId())){
								constructPlanBpmObjectEdit.setForeachExecutionId(constructRelaProcess.getId());
							}else{
								constructPlanBpmObjectEdit.setForeachExecutionId(constructPlanBpmObjectEdit.getForeachExecutionId()+","+constructRelaProcess.getId());
							}
							constructPlanBpmObjectEdit.setProcessEditEmpId(constructPlanRelaStandardEmp.getConstructPlanRelaOrg().getEmp().getId());//流程矩阵编写的人
							constructPlanBpmObjectEdit.setApproveEmpId(testingDepartmentMinister);//成果审批人员
							constructPlanBpmObjectEdit.setResultsPublish(constructPlanLeader);    //成果发布人员
							
						}
					}
				}
				if(!StringUtils.isBlank(constructPlanBpmObjectEdit.getProcessEditEmpId())){
					list.add(constructPlanBpmObjectEdit);
				}if(!StringUtils.isBlank(constructPlanBpmObjectDiagnose.getExecuteEmpId())){
					list.add(constructPlanBpmObjectDiagnose);
				}
			}
		}
		
		//将分发的两条线用path区分，里面放入相同的对象，但是含义确实不一样的。之后放在流程变量中
		if(!list.isEmpty()){
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("items", list);
			variables.put("joinCount", list.size());
			o_jbpmOperate.saveVariables(o_jbpmBO.getExecutionIdBySubExecutionId(executionId),variables);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}// 如果是缺陷反馈这样多级分发的情况，应该首先获取ConstructPlanBpmObject 对象
	}	
	
	/**
	 *    获取计划所对应的所有人员列表
	 */
	private Set<String> getConstructPlanRealStandardEmp(String constructPlanId){
		Set<String> empId = new HashSet<String>();
		Criteria cri = o_constructPlanRelaStandardEmpDAO.createCriteria();
		cri.createAlias("constructPlanRelaStandard", "constructPlanRelaStandard");
		cri.createAlias("constructPlanRelaStandard.constructPlan", "constructPlan");
		cri.add(Restrictions.eq("constructPlan.id", constructPlanId));
		List<ConstructPlanRelaStandardEmp>  constructPlanRelaStandardEmpList =  cri.list();
		for(ConstructPlanRelaStandardEmp constructPlanRelaStandardEmp : constructPlanRelaStandardEmpList){
			String temp = constructPlanRelaStandardEmp.getConstructPlanRelaOrg().getEmp().getId();
			empId.add(temp);
		}
		return empId;
	}
	/**
	 * 第2步工作流提交--评价计划制定审批.
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 */
	public Map<String,Object> constructPlanRelaProcessSubmit(String executionId, String businessId){
		//获取所梳理流程id
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("success", true);
		ConstructPlanBpmObject constructPlanBpmObject = this.findBpmObjectByExecutionId(executionId,"item");
		String constructPlanRelaProcess = constructPlanBpmObject.getForeachExecutionId();
		String[] constructPlanRelaProcessArray = constructPlanRelaProcess.split(",");
		Process process = null;
		Map<String,String> workFlowMap = new HashMap<String,String>();//key：部门id value：流程id
		for(String constructPlanRelaProcessId : constructPlanRelaProcessArray){
			ConstructRelaProcess constructRelaProcess = o_constructRelaProcessDAO.get(constructPlanRelaProcessId);
			ConstructPlanRelaStandard constructPlanRelaStandard = constructRelaProcess.getConstructPlanRelaStandard();
			Set<StandardRelaProcessure> standardRelaProcessures = constructPlanRelaStandard.getStandard().getStandardRelaProcessure();
			for (StandardRelaProcessure standardRelaProcessure : standardRelaProcessures) {
				process =  standardRelaProcessure.getProcessure();
				//如果流程下无节点直接跳出程序
				List<ProcessPoint> processPointList = o_processPointBO.findProcessPointListByProcessId(process.getId());
				if(processPointList == null || processPointList.size()==0){
					resultMap.put("pointInfo", "流程：["+process.getName()+"]下没有对应的流程节点,不能提交！");
					resultMap.put("success", false);
					break;
				}
				List<Risk> riskList = o_processRiskBO.findRiskListByProcessId(process.getId());
				if(riskList == null || riskList.size()==0){
					resultMap.put("pointInfo", "流程：["+process.getName()+"]下没有对应的风险,不能提交！");
					resultMap.put("success", false);
					break;
				}
			}
			if(!(Boolean)resultMap.get("success")){
				break;
			}
			String[] sb = new String[1];
			sb[0]=process.getId();
			List<SysOrganization> sysOrganizationList =  this.findSysOrganizationByProcessId(sb,Contents.ORG_RESPONSIBILITY);//责任部门
			if(sysOrganizationList.size()==0){
				//有角色没有设置的则跳过此代办
				LOG.error("执行流程节点时没有得到指定人",new Exception("执行流程节点时没有得到指定人"));
			}
			String processRelaOrgId = sysOrganizationList.get(0).getId();
			if(StringUtils.isBlank(workFlowMap.get(processRelaOrgId))){
				String constructPlanRelaProcessIds = constructPlanRelaProcessId;
				workFlowMap.put(processRelaOrgId,constructPlanRelaProcessIds);
			}else{
				String constructPlanRelaProcessIds = workFlowMap.get(processRelaOrgId)+","+constructPlanRelaProcessId;
				workFlowMap.put(processRelaOrgId,constructPlanRelaProcessIds);
			}
		}
		if((Boolean)resultMap.get("success")){
			Iterator<Entry<String, String>> iter = workFlowMap.entrySet().iterator();
			List<ConstructPlanBpmObject> constructPlanBpmFeckBackObjectList =  new ArrayList<ConstructPlanBpmObject>();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				String val = (String) entry.getValue();
				Set<String> orgSet = new HashSet<String>();
				orgSet.add(key);
				String roleName = ResourceBundle.getBundle("application").getString("ICByTestingDepartmentMinister");//被测试部门部长
				List<Object[]> testingDepartmentMinister = o_employeeBO.findEmployeeListByOrgIdsAndRoleName(orgSet,roleName);
				ConstructPlanBpmObject constructPlanBpmObject1 = new ConstructPlanBpmObject();
				constructPlanBpmObject1.setConstructPlanRelaProcessIds(val);
				if(testingDepartmentMinister.size()==0){
					//有角色没有设置的则跳过此代办
					LOG.error("执行流程节点时没有得到指定人",new Exception("执行流程节点时没有得到指定人"));
					break;
				}
				constructPlanBpmObject1.setProcessApprovalEmpId(String.valueOf(testingDepartmentMinister.get(0)[0]));//流程成果审批
				constructPlanBpmObject1.setProcessRepairEmpId(UserContext.getUserid());	//流程修复
				constructPlanBpmFeckBackObjectList.add(constructPlanBpmObject1);
			}
			if(!constructPlanBpmFeckBackObjectList.isEmpty()){
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("approveitems", constructPlanBpmFeckBackObjectList);
				variables.put("joinCount", Integer.parseInt(o_jbpmOperate.getVariable(executionId, "joinCount")) + constructPlanBpmFeckBackObjectList.size()-1);
				o_jbpmOperate.saveVariables(o_jbpmBO.getExecutionIdBySubExecutionId(executionId),variables);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
			resultMap.put("success", true);
		}
		return resultMap;
		}
	/**
	 * <pre>
	 * 通过流程Id查找相关联的实体集合
	 * </pre>
	 * @author 刘中帅
	 * @param processId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysOrganization> findSysOrganizationByProcessId(String[] processId,String type){
		List<ProcessRelaOrg> processRelaOrgList=null;
		List<SysOrganization> sysOrganizationList=new ArrayList<SysOrganization>();
		Criteria criteria=o_processRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.in("process.id", processId));
		criteria.add(Restrictions.eq("type", type));
		processRelaOrgList=criteria.list();
		for(ProcessRelaOrg processRelaOrg:processRelaOrgList){
			if(!sysOrganizationList.contains(processRelaOrg.getOrg())){
				sysOrganizationList.add(processRelaOrg.getOrg());
			}
		}
		return sysOrganizationList;
	}
	/**
	 * 第2步工作流提交--评价计划制定审批.
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 */
	@Transactional
	public void constructPlanResultsPublish(String executionId, String businessId){
		ConstructPlan constructPlan = o_constructPlanDAO.get(businessId);
		if(null != constructPlan){
			//工作流提交
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
		}
	}
	/**
	 * 第2步工作流提交--评价计划制定审批.
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 */
	public Map<String,Object> constructPlanResultsRepair(String executionId, String businessId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("success", true);
		ConstructPlanBpmObject constructPlanBpmObject = this.findBpmObjectByExecutionId(executionId,"approveitem");
		String constructPlanRelaProcess = constructPlanBpmObject.getConstructPlanRelaProcessIds();
		String[] constructPlanRelaProcessArray = constructPlanRelaProcess.split(",");
		Process process = null;
		for(String constructPlanRelaProcessId : constructPlanRelaProcessArray){
			ConstructRelaProcess constructRelaProcess = o_constructRelaProcessDAO.get(constructPlanRelaProcessId);
			ConstructPlanRelaStandard constructPlanRelaStandard = constructRelaProcess.getConstructPlanRelaStandard();
			Set<StandardRelaProcessure> standardRelaProcessures = constructPlanRelaStandard.getStandard().getStandardRelaProcessure();
			for (StandardRelaProcessure standardRelaProcessure : standardRelaProcessures) {
				process =  standardRelaProcessure.getProcessure();
				//如果流程下无节点直接跳出程序
				List<ProcessPoint> processPointList = o_processPointBO.findProcessPointListByProcessId(process.getId());
				if(processPointList == null || processPointList.size()==0){
					resultMap.put("pointInfo", process.getName()+":下的节点为空,不能提交！");
					resultMap.put("success", false);
					break;
				}
				List<Risk> riskList = o_processRiskBO.findRiskListByProcessId(process.getId());
				if(riskList == null || riskList.size()==0){
					resultMap.put("pointInfo", process.getName()+":下的风险为空,不能提交！");
					resultMap.put("success", false);
					break;
				}
			}
			if(!(Boolean)resultMap.get("success")){
				break;
			}
		}
		if((Boolean)resultMap.get("success")){
		//工作流提交
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
			resultMap.put("success", true);
		}
		return resultMap;
	}
	
	/**
	 * 第2步工作流提交--评价计划制定审批.
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 */
	@Transactional
	public void mergePlanProcessEditApproval(String executionId, String businessId, String isPass, String examineApproveIdea){
		ConstructPlan constructPlan = o_constructPlanDAO.get(businessId);
		if(null != constructPlan){
			
			if ("no".equals(isPass)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				//工作流提交
				// 更新计划关联流程表，将流程状态改为已完成。
				ConstructPlanBpmObject constructPlanBpmObject = findBpmObjectByExecutionId(executionId,"approveitem");
				String constructPlanRelaProcess = constructPlanBpmObject.getConstructPlanRelaProcessIds();
				String[] constructPlanRelaProcessArray = constructPlanRelaProcess.split(",");
				for(String constructPlanRelaProcessId : constructPlanRelaProcessArray){
					ConstructRelaProcess constructRelaProcess = o_constructRelaProcessDAO.get(constructPlanRelaProcessId);
					constructRelaProcess.setEstatus(Contents.DEAL_STATUS_FINISHED);
					constructRelaProcess.setActualEndDate(new Date());
					o_constructRelaProcessDAO.merge(constructRelaProcess);
				}
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	/**
	 * 根据流程实例ID获得类型为AssessPlanBpmObject的流程变量
	 * @author 吴德福
	 * @param executionId 流程实例ID
	 * @return AssessPlanBpmObject
	 * @since  fhd　Ver 1.1
	*/
	public ConstructPlanBpmObject findBpmObjectByExecutionId(String executionId,String name){
		return (ConstructPlanBpmObject) o_jbpmOperate.getVariableObj(executionId, name);
	}
	
	
	/**
	 * 合规诊断任务提交
	 * @author 宋佳
	 * @param executionId,
	 * @param constructPlanId
	 * @param jsonString
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void diagnosesSubmit(String executionId,String constructPlanId,String jsonString){
		
		JSONArray jsonArray=JSONArray.fromObject(jsonString);
		if(jsonArray.size()==0){
			return;
		}
		Criteria cri = o_constructPlanRelaOrgDAO.createCriteria();
		cri.add(Restrictions.eq("constructPlan.id",constructPlanId));
		cri.add(Restrictions.eq("type", Contents.EMP_RESPONSIBILITY));
		ConstructPlanRelaOrg constructPlanRelaOrg = (ConstructPlanRelaOrg) cri.uniqueResult();
		String constructPlanLeader = constructPlanRelaOrg.getEmp().getId();//组长
		Map<String,String> workFlowMap = new HashMap<String,String>();//key:部门id value：缺陷整改id
		for(int i=0;i<jsonArray.size();i++){
			//处理业务信息
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			Diagnoses diagnoses = o_diagnosesDAO.get(jsonObject.getString("id"));
			if(jsonObject.getBoolean("diagnosis")){
				diagnoses.setEstatus(Contents.DEAL_STATUS_FINISHED);
				o_diagnosesDAO.merge(diagnoses);
			}else{
				Defect defect = new Defect();
				defect.setId(Identities.uuid());
				defect.setDesc(jsonObject.getString("description"));
				o_defectDAO.merge(defect);
				// 保存 缺陷关联合规诊断信息
				DiagnosesRelaDefect diagnosesRelaDefect = new DiagnosesRelaDefect();
				diagnosesRelaDefect.setId(Identities.uuid());
				diagnosesRelaDefect.setDefect(defect);
				diagnosesRelaDefect.setDiagnoses(o_diagnosesDAO.get(jsonObject.getString("id")));
				o_diagnosesRelaDefectDAO.merge(diagnosesRelaDefect);
				//处理工作流信息
				String standardRelaOrgId = this.findStandardRelaOrg(o_diagnosesDAO.get(jsonObject.getString("id")));
				if(StringUtils.isBlank(workFlowMap.get(standardRelaOrgId))){
					String diagnosesRelaDefectIds = diagnosesRelaDefect.getId();
					workFlowMap.put(standardRelaOrgId,diagnosesRelaDefectIds);
				}else{
					String diagnosesRelaDefectIds = workFlowMap.get(standardRelaOrgId)+","+diagnosesRelaDefect.getId();
					workFlowMap.put(standardRelaOrgId,diagnosesRelaDefectIds);
				}
				}
		}
				Iterator<Entry<String, String>> iter = workFlowMap.entrySet().iterator();
				List<ConstructPlanBpmObject> constructPlanBpmFeckBackObjectList =  new ArrayList<ConstructPlanBpmObject>();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String val = (String) entry.getValue();
					Set<String> orgSet = new HashSet<String>();
					orgSet.add(key);
					String roleName = ResourceBundle.getBundle("application").getString("ICByTestingDepartmentMinister");//被测试部门部长
					List<Object[]> testingDepartmentMinister = o_employeeBO.findEmployeeListByOrgIdsAndRoleName(orgSet,roleName);
					ConstructPlanBpmObject constructPlanBpmFeckBackObject = new ConstructPlanBpmObject();
					constructPlanBpmFeckBackObject.setDianosesRelaDefectId(val);
					if(testingDepartmentMinister.size()==0){
						//有角色没有设置的则跳过此代办
						LOG.error("执行流程节点时没有得到指定人",new Exception("执行流程节点时没有得到指定人"));
					}
					constructPlanBpmFeckBackObject.setDefectFeedbackEmpId(String.valueOf(testingDepartmentMinister.get(0)[0]));
					constructPlanBpmFeckBackObject.setDefectCleanUpEmpId(constructPlanLeader);
					constructPlanBpmFeckBackObjectList.add(constructPlanBpmFeckBackObject);
				}
				if(constructPlanBpmFeckBackObjectList.isEmpty()){
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put("path", "no");
					o_jbpmBO.doProcessInstance(executionId, variables);
				}
				else{
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put("feedbackitems", constructPlanBpmFeckBackObjectList);
					variables.put("joinCount", Integer.parseInt(o_jbpmOperate.getVariable(executionId, "joinCount")) + constructPlanBpmFeckBackObjectList.size()-1);
					o_jbpmOperate.saveVariables(o_jbpmBO.getExecutionIdBySubExecutionId(executionId),variables);
					variables.put("path", "yes");
					o_jbpmBO.doProcessInstance(executionId, variables);
				}
		}
	
	/**
	 * @author 宋佳
	 * @param executionId,constructPlanId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void submitDiagnosesDefect(String executionId,String constructPlanId){
		Map<String, Object> variables = new HashMap<String, Object>();
		o_jbpmBO.doProcessInstance(executionId, variables);
	}	
	/**
	 * @author 宋佳
	 * @param executionId,constructPlanId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void submitDefectClearUp(String executionId,String constructPlanId){
		// 更改合规诊断表状态为已完成
		// 更新计划关联流程表，将流程状态改为已完成。
		ConstructPlanBpmObject constructPlanBpmObject = findBpmObjectByExecutionId(executionId,"backItem");
		String dianosesRelaDefectIds = constructPlanBpmObject.getDianosesRelaDefectId();
		String[] dianosesRelaDefectIdArray = dianosesRelaDefectIds.split(",");
		for(String dianosesRelaDefectId : dianosesRelaDefectIdArray){
			DiagnosesRelaDefect diagnosesRelaDefect = o_diagnosesRelaDefectDAO.get(dianosesRelaDefectId);
			Diagnoses diagnoses = o_diagnosesDAO.get(diagnosesRelaDefect.getDiagnoses().getId());
			diagnoses.setEstatus(Contents.DEAL_STATUS_FINISHED);
			o_diagnosesDAO.merge(diagnoses);
		}
		Map<String, Object> variables = new HashMap<String, Object>();
		o_jbpmBO.doProcessInstance(executionId, variables);
	}	
	
	//   找到标准所对应的责任机关
	private String findStandardRelaOrg(Diagnoses diagnoses){
		Set<StandardRelaOrg> orgList = diagnoses.getConstructPlanRelaStandard().getStandard().getStandardRelaOrg();
		for(Iterator<StandardRelaOrg> it = orgList.iterator();it.hasNext();){
			StandardRelaOrg org = (StandardRelaOrg) it.next();
			if(Contents.ORG_RESPONSIBILITY.equals(org.getType())){
				return org.getOrg().getId();
			}
		}
		return "";
	}
}
