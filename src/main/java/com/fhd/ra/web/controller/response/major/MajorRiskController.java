package com.fhd.ra.web.controller.response.major;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.response.major.RiskResponseCounter;
import com.fhd.entity.response.major.RiskResponseCounterForm;
import com.fhd.entity.response.major.RiskResponseItem;
import com.fhd.entity.response.major.RiskResponseItemCounterRela;
import com.fhd.entity.response.major.RiskResponsePlanRiskRela;
import com.fhd.entity.response.major.RiskResponseRiskSchemeRela;
import com.fhd.entity.response.major.RiskResponseScheme;
import com.fhd.entity.response.major.RiskResponseSchemeForm;
import com.fhd.entity.response.major.RiskResponseSchemeItemRela;
import com.fhd.entity.response.major.RiskResponseTaskExecution;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.planconform.PlanConformBO;
import com.fhd.ra.business.response.major.RiskResponseCounterService;
import com.fhd.ra.business.response.major.RiskResponseItemCounterRelaService;
import com.fhd.ra.business.response.major.RiskResponseItemService;
import com.fhd.ra.business.response.major.RiskResponsePlanRiskRelaService;
import com.fhd.ra.business.response.major.RiskResponseRiskSchemeRelaService;
import com.fhd.ra.business.response.major.RiskResponseSchemeItemRelaService;
import com.fhd.ra.business.response.major.RiskResponseSchemeService;
import com.fhd.ra.business.response.major.RiskResponseTaskExecutionService;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.business.risk.RiskEventRelatedService;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.web.controller.response.major.utils.ApproveGridEntity;
import com.fhd.ra.web.controller.response.major.utils.DateUtil;
import com.fhd.ra.web.controller.response.major.utils.JacksonUtilofNullToBlank;
import com.fhd.ra.web.controller.response.major.utils.Tasker;
import com.fhd.ra.web.form.planconform.PlanConformForm;

import net.sf.json.JSONArray;

/**
 * 重大风险制定应对计划Controller
 * @author Jzq
 *
 */
@Controller
public class MajorRiskController {

	private static final Logger log = LoggerFactory.getLogger(MajorRiskController.class);
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private PlanConformBO o_planConformBO;
	@Autowired
	private RiskResponsePlanRiskRelaService riskResponsePlanRiskRelaService;
	@Autowired
	private RiskCmpBO o_riskCmpBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired
	private RiskOrgBO riskOrgService;
	@Autowired
	private RiskResponseTaskExecutionService riskResponseTaskExecutionService;
	@Autowired
	private RiskResponseSchemeService riskResponseSchemeService;
	@Autowired
	private RiskResponseItemService riskResponseItemService;
	@Autowired
	private RiskResponseCounterService riskResponseCounterService;
	@Autowired
	private RiskResponseItemCounterRelaService riskResponseItemCounterRelaService;
	@Autowired
	private RiskResponseSchemeItemRelaService riskResponseSchemeItemRelaService;
	@ResponseBody
	@RequestMapping("/majorResponse/formSubmit")
	public Map<String,Object> formSubmit(String businessId,String executionId,String riskName,String people){
		Map<String,Object> ret = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		String empName = UserContext.getUsername();
		map.put("id", businessId);
		map.put("riskName", riskName);
		map.put("prePeople", empName);
		map.put("people", people);
		ret.put("data", map);
		ret.put("success", true);
		Map<String,Object> variables = new HashMap<String,Object>();
		//工作流提交
		variables.put("riskName",riskName);
		variables.put("planDirectorApproval", people);
		variables.put("planLeaderApproval", people);
		variables.put("deptRiskManager", people);
//		variables.put("deptCommonEmp", people);
		variables.put("deptRiskManagerForSub", people);
		variables.put("directorApproval", people);
		variables.put("leaderApproval", people);
		variables.put("managerApproval", people);
		variables.put("officeApproval", people);
//		variables.put("officeSummary", people);
		String os = o_jbpmBO.getVariable("officeSummary", executionId);
		log.debug("jzq:="+os);
		variables.put("businessId", businessId);
		variables.put("name", "吉志强");
		List<Map<String,Object>> taskers = new ArrayList<Map<String,Object>>();
		Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("deptRiskManager", "c351732e84fb490ba3b2124feadba202");
		Map<String,Object> map2 = new HashMap<String,Object>();
		map2.put("deptRiskManager", "851784b955fe428ea9553ee0848adedf");
		taskers.add(map1);
		taskers.add(map2);
		variables.put("taskers", taskers);
		variables.put("path", "yes");
		variables.put("joinCount", 2);
		o_jbpmBO.doProcessInstance(executionId, variables);
		
		return ret;
	}
	
	/**
	 * 保存应对计划基本信息即第一步的信息
	 * @param planForm
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/majorResponse/savePlan")
	public Map<String, Object> savePlan(PlanConformForm planForm) throws Exception{
		return o_planConformBO.savePlanByPlanTypeNew(planForm);
	}	
	
	/**  
	* @Title: getTreeRecord  
	* @Description: 获取重大风险tree，根据planId查询是否有重大风险checked = true
	* @param planId
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/getTreeRecord")
	public List<Map<String,Object>> getTreeRecord(
			String node, String query, Boolean subCompany, 
    		Boolean showLight, String chooseId,Boolean onlyLeaf,Boolean showAllUsed,
    		String schm,
    		Boolean canChecked,
    		String dbid,
    		Boolean leaf){
		onlyLeaf = true;
		if(node.equals("root")){
			leaf = false;
		}else{
			leaf = true;
		}
		if(subCompany==null){
    		subCompany = false;
    	}
    	if(showLight==null){
    		showLight = false;
    	}
    	if(canChecked==null){
    		canChecked = false;
    	}
    	if(showAllUsed==null){
    		showAllUsed = false;
    	}
    	List<Map<String,Object>> list = null;
    	list = riskResponsePlanRiskRelaService.getRiskTreeRecord(node, query, subCompany, showLight, canChecked, chooseId,onlyLeaf,showAllUsed,schm,leaf);
    	return this.riskResponsePlanRiskRelaService.compareTreeAndGridData(list);
	}
	
	/**
	 * 制定计划下一步时点击重大风险，获取该重大风险所涉及的部门
	 * @param majorRisk {majorRiskId："",majorRiskName:"",planId:""}
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/majorResponse/getDeptForMajorRisk")
	public List<Map<String,Object>> getDeptForMajorRisk(String majorRisk){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = JacksonUtilofNullToBlank.toCollection(majorRisk, new TypeReference<Map<String,Object>>() {});
		if(majorRisk == null ||majorRisk.equals("")){
			return list;
		}else{
			String majorRiskId = map.get("majorRiskId").toString();
			String majorRiskName = map.get("majorRiskName").toString();
			String planId = map.get("planId").toString();
			List<RiskOrg> riskOrgList = null;
			riskOrgList = this.riskOrgService.findOrgsByRiskId(majorRiskId);
			for(RiskOrg riskOrg :riskOrgList){
				Map<String,Object> mapTemp = new HashMap<String,Object>();
				mapTemp.put("id", riskOrg.getId());
				mapTemp.put("riskName", majorRiskName);
				mapTemp.put("dept",riskOrg.getSysOrganization().getOrgname());
				mapTemp.put("deptType", riskOrg.getType());
				mapTemp.put("riskId", riskOrg.getRisk().getId());
				mapTemp.put("planId", planId);
				mapTemp.put("deptId", riskOrg.getSysOrganization().getId());
				list.add(mapTemp);
			}
		}
		return list;
	}
	
	/**  
	* @Title: saveRiskAndPlanRela  
	* @Description: 保存风险计划部门表，并且启动流程
	* @param param [{planId:"",riskId:"",detp:"",deptType:""}]
	* @param approverId [{id:""}]  审批人
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/saveRiskAndPlanRela")
	public Map<String,Object> saveRiskAndPlanRela(String param,String approverId){
		return this.riskResponsePlanRiskRelaService.saveRiskAndPlanRela(param, approverId);
	}

	/**  
	* @Title: loadPlanSelectedDataByPlanId  
	* @Description: 加载应对计划下已经选择的重大风险对应部门列表
	* @param planId
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadPlanSelectedDataByPlanId")
	public List<Map<String,Object>> loadPlanSelectedDataByPlanId(String planId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<RiskResponsePlanRiskRela> retList = null;
		retList = this.riskResponsePlanRiskRelaService.findListByPlanId(planId);
		if(retList !=null && retList.size()>0){
			for(RiskResponsePlanRiskRela obj :retList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", obj.getId());
				map.put("planId", obj.getPlan().getId());
				map.put("riskId", obj.getMajorRisk().getId());
				map.put("riskName", obj.getMajorRisk().getName());
				map.put("dept", obj.getDept().getOrgname());
				map.put("deptType", obj.getDeptType());
				map.put("deptId", obj.getDept().getId());
				list.add(map);
			}
		}
		return list;
	}
	/**  
	* @Title: loadPlanApproveDataByPlanId  
	* @Description: 计划审批grid
	* @param planId
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadPlanApproveDataByPlanId")
	public List<ApproveGridEntity> loadPlanApproveDataByPlanId(String planId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<RiskResponsePlanRiskRela> retList = null;
		retList = this.riskResponsePlanRiskRelaService.findListByPlanId(planId);
		List<ApproveGridEntity> listTemp = new ArrayList<ApproveGridEntity>();
		Map<String,ApproveGridEntity> riskMap = new HashMap<String,ApproveGridEntity>();
		if(retList !=null && retList.size()>0){
			for(RiskResponsePlanRiskRela obj :retList){
				String riskId = obj.getMajorRisk().getId();
				String riskName = obj.getMajorRisk().getName();
				String deptName = obj.getDept().getOrgname();
				String deptType = obj.getDeptType();
				if(!riskMap.containsKey(riskId)){
					ApproveGridEntity entity = new ApproveGridEntity();
					if(deptType.equals("M")){
						entity.setPlanId(obj.getPlan().getId());
						entity.setMajorDept(deptName);
						entity.setRiskName(riskName);
						entity.setRiskId(riskId);
					}else if(deptType.equals("A")){
						String temp = entity.getRelaDept();
						entity.setRelaDept(temp==null || "".equals(temp)?deptName:temp+","+deptName);
						entity.setPlanId(obj.getPlan().getId());
						entity.setRiskName(riskName);
						entity.setRiskId(riskId);
					}
					riskMap.put(riskId, entity);
				}else{
					ApproveGridEntity entity = riskMap.get(riskId);
					if(deptType.equals("M")){
						entity.setPlanId(obj.getPlan().getId());
						entity.setMajorDept(deptName);
						entity.setRiskName(riskName);
						entity.setRiskId(riskId);
					}else if(deptType.equals("A")){
						String temp = entity.getRelaDept();
						entity.setRelaDept(temp==null || "".equals(temp)?deptName:temp+","+deptName);
						entity.setPlanId(obj.getPlan().getId());
						entity.setRiskName(riskName);
						entity.setRiskId(riskId);
					}
				}
			}
			for (ApproveGridEntity entity : riskMap.values()){
				String relaDept = entity.getRelaDept();
				if(relaDept != null && !"".equals(relaDept)){
					String[] relaArr = relaDept.split(",");
					entity.setDeptCount((relaArr.length));
				}
				String majorDept = entity.getMajorDept();
				if(majorDept!= null && !"".equals(majorDept)){
					entity.setDeptCount(entity.getDeptCount()+1);
				}
				
				listTemp.add(entity);
			}
		}
		return listTemp;
	}
	
	/**  
	* @Title: majorRiskPlanApprove  
	* @Description: 重大风险应对计划 主管、领导审批
	* @param businessId 计划id
	* @param executionId 流程执行id
	* @param isPass  yes or no
	* @param examineApproveIdea   审批处理意见
	* @param approverId 审批人
	* @param approverKey 审批流程变量key 区别主管与领导
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/majorRiskPlanApprove")
	public Map<String,Object> majorRiskPlanApprove(String businessId,String executionId,String isPass,String examineApproveIdea,String approverId,String approverKey){
		List<Map<String,Object>> approverIdList = JacksonUtilofNullToBlank.toCollection(approverId, new TypeReference<List<Map<String,Object>>>() {});
		//审批人
		String _appriverId = approverIdList.get(0).get("id").toString();
		return this.riskResponsePlanRiskRelaService.majorRiskPlanApprove(businessId, executionId, isPass, examineApproveIdea, _appriverId, approverKey);
	}
	
	/**
	 * 
	* @Title: getMajorRiskTaskSelectEmp  
	* @Description:获取任务分配页面初始化信息，部门风险管理员所要分配的风险信息,当前登录人肯定为部门的风险管理员
	* @param businessId
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/getMajorRiskTaskSelectEmp")
	public Map<String,Object> getMajorRiskTaskSelectEmp(String businessId,String executionId) throws Exception{
		Map<String,Object> retMap = new HashMap<String,Object>();
		RiskResponsePlanRiskRela entity = new RiskResponsePlanRiskRela();
		entity.setPlan(new RiskAssessPlan(businessId));
		String currentUserDeptId = UserContext.getUser().getMajorDeptId();
		Tasker tasker = (Tasker) this.o_jbpmBO.getVariableObj("tasker", executionId);
		if(tasker == null){
			tasker = (Tasker) this.o_jbpmBO.getVariableObj("mainTasker", executionId);
		}
		String deptId = tasker.getDeptId();
		if(deptId == null ||"".equals(deptId)){
			deptId = currentUserDeptId;
		}
		String _deptType = tasker.getDeptType();
		String riskId = tasker.getRiskId();
		entity.setDept(new SysOrganization(deptId));
		entity.setDeptType(_deptType);
		entity.setMajorRisk(new Risk(riskId));
		RiskResponsePlanRiskRela obj = this.riskResponsePlanRiskRelaService.findOne(entity);
		boolean flag = false;
		Map<String,Object> result = new HashMap<String,Object>();
		if(obj != null){
			result.put("riskId", obj.getMajorRisk().getId());
			result.put("majorRiskName", obj.getMajorRisk().getName());
			result.put("deptId", obj.getDept().getId());
			result.put("deptName", obj.getDept().getOrgname());
			result.put("planId", businessId);
			String deptType = obj.getDeptType();
			if(deptType.equals("M")){
				result.put("deptType", "主责部门");
			}else{
				result.put("deptType", "相关部门");
			}
			
			String planRiskRelaId = obj.getId();
			RiskResponseTaskExecution taskExecutionParam = new RiskResponseTaskExecution();
			taskExecutionParam.setPlanRiskRelaObj(obj);
			taskExecutionParam.setEmpType("1");//应对人员为普通员工，即由部门风险管理员分配的人员
			List<RiskResponseTaskExecution> taskExecutionObjList = this.riskResponseTaskExecutionService.findList(taskExecutionParam);
			//整理应对人
			List<Map<String,Object>> empList = new ArrayList<Map<String,Object>>();
			if(taskExecutionObjList!= null){
				for(RiskResponseTaskExecution taskExecutionObj : taskExecutionObjList){
					Map<String,Object> empMap = new HashMap<String,Object>();
					empMap.put("id", taskExecutionObj.getExecuteEmp().getId());
					empMap.put("name", taskExecutionObj.getExecuteEmp().getEmpname());
					empList.add(empMap);
				}
				result.put("empList", empList);
			}
			flag = true;
		}
		
		retMap.put("data", result);
		retMap.put("success", flag);
		return retMap;
	}
	
	/**
	* @Title: saveTaskExecutionEmp  
	* @Description: 任务分配时保存执行人并执行任务节点
	* @param executionId
	* @param businessId
	* @param majorRiskId
	* @param deptId
	* @param executionEmps ["",""]
	* @return Map<String,Object>
	* @throws  Exception 
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/saveTaskExecutionEmp")
	public Map<String,Object> saveTaskExecutionEmp(String executionId,String businessId,String majorRiskId,String deptId,String executionEmps) throws Exception{
		Map<String,Object> retMap = new HashMap<String,Object>();
		boolean flag = false;
		RiskResponsePlanRiskRela entity = new RiskResponsePlanRiskRela();
		entity.setPlan(new RiskAssessPlan(businessId));
		entity.setDept(new SysOrganization(deptId));
		entity.setMajorRisk(new Risk(majorRiskId));
		
		List<RiskResponseTaskExecution> riskResponseTaskExecutionList = new ArrayList<RiskResponseTaskExecution>();
		RiskResponsePlanRiskRela retObj = this.riskResponsePlanRiskRelaService.findOne(entity);
		if(retObj != null){
			String empType = "1";//在分配的执行人类型都是 1
			String [] arrEmps = executionEmps.split(",");
			if(arrEmps != null && arrEmps.length>0){
				for(String empId :arrEmps){
					RiskResponseTaskExecution obj = new RiskResponseTaskExecution();
					obj.setEmpType(empType);
					obj.setExecuteEmp(new SysEmployee(empId));
					obj.setPlanRiskRelaObj(retObj);
					obj.setId(Identities.uuid());
					riskResponseTaskExecutionList.add(obj);
					//循环查询数据库了，待改
					List<RiskResponseTaskExecution> isHasList = this.riskResponseTaskExecutionService.findList(obj);
					if(isHasList!= null && isHasList.size()>0){
						this.riskResponseTaskExecutionService.deleteBatchByList(isHasList);
					}
				}
			}
		}
		try {
			//TODO为部门汇总也就是部门风险管理员也做作为执行人
			String managerType ="2";
			RiskResponseTaskExecution managerExecution = new RiskResponseTaskExecution();
			managerExecution.setEmpType(managerType);
			managerExecution.setExecuteEmp(new SysEmployee(UserContext.getUser().getEmpid()));
			managerExecution.setPlanRiskRelaObj(retObj);
			managerExecution.setId(Identities.uuid());
			riskResponseTaskExecutionList.add(managerExecution);
			//保存执行人信息
			this.riskResponseTaskExecutionService.saveBatch(riskResponseTaskExecutionList);
			//执行流程
			Map<String,Object> variables = new HashMap<String,Object>();
			variables.put("id", businessId);
			this.o_jbpmBO.doProcessInstance(executionId, variables);
		} catch (Exception e) {
			flag = true;
		}
		retMap.put("success", flag);
		return retMap;
	}
	
	/**  
	* @Title: saveScheme  
	* @Description: 保存方案信息和风险和方案的相关关系
	* @param riskResponseSchemeForm
	* @param planId
	* @param riskId
	* @param deptId
	* @param empType   汇总 or普通员工制定 1：汇总 0：非汇总
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/saveScheme")
	public Map<String,Object> saveScheme(RiskResponseSchemeForm riskResponseSchemeForm,String planId,String riskId,String deptId,String empType ,HttpServletResponse response){
		boolean flag = false;
		Map<String,Object> map = new HashMap<String,Object>();
		String currentUserId = UserContext.getUser().getEmpid();
		riskResponseSchemeForm.setCreateOrg(new SysOrganization(deptId));
		riskResponseSchemeForm.setCreateUser(new SysEmployee(currentUserId));
		RiskResponseTaskExecution executionObj = this.riskResponseTaskExecutionService.getExecutionObj(planId,riskId,deptId,currentUserId,empType);
		//保存方案信息和风险和方案的关联关系
		Map<String,Object> resultMap = new HashMap<String,Object>(); 
		if(executionObj !=null){
			RiskResponseScheme retObj = null;
			try {
				retObj = this.riskResponseSchemeService.saveScheme(riskResponseSchemeForm,planId,riskId,deptId,currentUserId,empType,executionObj);
				flag = true;
				resultMap.put("executionObjectId", executionObj.getId());
				resultMap.put("schemeObjectId", retObj.getId());
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			}
			
		}
		map.put("data", resultMap);
		map.put("success", flag);
		return map;
		
	}
	/**  
	* @Title: loadScheme  
	* @Description: 增加方案表单页面初始化
	* @param planId
	* @param empType
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadScheme")
	public Map<String,Object> loadScheme(String planId,String executionId,String empType){
		Map<String,Object> map = new HashMap<String,Object>();
		RiskResponsePlanRiskRela entity = new RiskResponsePlanRiskRela();
		Tasker tasker = (Tasker) this.o_jbpmBO.getVariableObj("tasker", executionId);
		if(tasker == null){
			tasker = (Tasker) this.o_jbpmBO.getVariableObj("mainTasker", executionId);
		}
		String deptId = tasker.getDeptId();
		String _deptType = tasker.getDeptType();
		String riskId = tasker.getRiskId();
		entity.setDept(new SysOrganization(deptId));
		entity.setMajorRisk(new Risk(riskId));
		entity.setDeptType(_deptType);
		entity.setPlan(new RiskAssessPlan(planId));
		String currentUserDeptId = UserContext.getUser().getMajorDeptId();
		entity.setDept(new SysOrganization(currentUserDeptId));
		RiskResponsePlanRiskRela obj = this.riskResponsePlanRiskRelaService.findOne(entity);
		boolean flag = false;
		Map<String,Object> schemeMap = this.riskResponseSchemeService.loadScheme(planId, obj.getMajorRisk().getId(), currentUserDeptId, empType);
		flag = true;
		map.put("data", schemeMap);
		map.put("success",flag);
		return map;
	}
	@ResponseBody
	@RequestMapping("/majorResponse/InfoItemByPlanAndDeptInfo")
	public List<Map<String,Object>> InfoItemByPlanAndDeptInfo(String executionId,String planId,String empType){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		RiskResponsePlanRiskRela entity = new RiskResponsePlanRiskRela();
		entity.setPlan(new RiskAssessPlan(planId));
		String currentUserDeptId = UserContext.getUser().getMajorDeptId();
		//entity.setDept(new SysOrganization(currentUserDeptId));
		Tasker tasker = (Tasker) this.o_jbpmBO.getVariableObj("tasker", executionId);
		if(tasker == null){
			tasker = (Tasker) this.o_jbpmBO.getVariableObj("mainTasker", executionId);
		}
		String deptId = tasker.getDeptId();
		String _deptType = tasker.getDeptType();
		String riskId = tasker.getRiskId();
		entity.setDept(new SysOrganization(deptId));
		entity.setMajorRisk(new Risk(riskId));
		entity.setDeptType(_deptType);
		entity.setPlan(new RiskAssessPlan(planId));
		
		RiskResponsePlanRiskRela objPara = this.riskResponsePlanRiskRelaService.findOne(entity);
		Map<String,Object> schemeMap = this.riskResponseSchemeService.loadScheme(planId, objPara.getMajorRisk().getId(), currentUserDeptId, empType);
		String schemeId = schemeMap.get("id").toString();
		if(schemeId == null ||"".equals(schemeId)){
			return list;
		}else{
			List<RiskResponseSchemeItemRela> schemeItemRelaList = this.riskResponseSchemeItemRelaService.findBySchemeId(schemeId);
			for(RiskResponseSchemeItemRela obj :schemeItemRelaList){
				Map<String,Object> map = new HashMap<String,Object>();
				RiskResponseItem item = obj.getItem();
				map.put("id", item.getId());
				map.put("description", item.getDescription());
				map.put("flow", item.getFlow());
				map.put("reason", item.getReason());
				map.put("userName",item.getCreateUser().getUsername());
				map.put("userId", item.getCreateUser().getId());
				list.add(map);
			}
			return list;
		}
	}
	
	/**  
	* @Title: loadScheme  
	* @Description: 根据方案id获取方案信息
	* @param schemeObjectId
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadSchemefById")
	public Map<String,Object> loadScheme(String schemeObjectId){
		Map<String,Object> retMap = new HashMap<String,Object>();
		RiskResponseScheme param =new RiskResponseScheme();
		param.setId(schemeObjectId);
		RiskResponseScheme scheme = this.riskResponseSchemeService.findObj(param);
		String id = "";
		String description="";
		String analysis = "";
		String strategy = "";
		Date startTime = null;
		Date finishTime = null;
		String type = "";
		String target = "";
		if(scheme !=null){
			id = scheme.getId();
			description = scheme.getDescription();
			analysis = scheme.getAnalysis();
			strategy = scheme.getStrategy();
			startTime = scheme.getStartTime();
			finishTime = scheme.getFinishTime();
			target = scheme.getTarget();
			type = scheme.getType();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", id);
			map.put("description", description);
			map.put("analysis", analysis);
			map.put("strategy", strategy);
			map.put("startTime", startTime ==null ? "":DateUtil.format(startTime, "yyyy-mm-dd"));
			map.put("finishTime", finishTime ==null ? "":DateUtil.format(finishTime, "yyyy-mm-dd"));
			map.put("type", type);
			map.put("target", target);
			retMap.put("data", map);
		}
		retMap.put("success",true);
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping("/majorResponse/saveItem")
	public Map<String,Object> saveItem(RiskResponseItem riskResponseItem,String planId,String riskId,String deptId,String empType,String schemeObjectId,String executionObjectId){
		Map<String,Object> map = new HashMap<String,Object>();
		String currentUserId = UserContext.getUser().getEmpid();
		String id = riskResponseItem.getId();
		if(id == null || "".equals(id)){
			id = Identities.uuid();
		}
		riskResponseItem.setId(id);
		riskResponseItem.setCreateOrg(new SysOrganization(deptId));
		riskResponseItem.setCreateUser(new SysEmployee(currentUserId));
		Map<String,Object> resultMap = new HashMap<String,Object>(); 
		RiskResponseItem retItem = null;
		retItem = this.riskResponseItemService.saveItem(riskResponseItem,schemeObjectId);
		if(retItem != null){
			resultMap.put("itemId", retItem.getId());
		}
		map.put("data", resultMap);
		map.put("success", true);
		return map;
		
	}
	@ResponseBody
	@RequestMapping("/majorResponse/loadItemById")
	public Map<String,Object> loadItemById(String itemId){
		Map<String,Object> retMap = new HashMap<String,Object>();
		RiskResponseItem param =new RiskResponseItem();
		param.setId(itemId);
		RiskResponseItem item = this.riskResponseItemService.findObj(param);
		String id = "";
		String description="";
		String flow = "";
		String reason = "";
		String type = "";
		if(item !=null){
			id = item.getId();
			description = item.getDescription();
			flow = item.getFlow();
			reason = item.getReason();
			type = item.getType();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", id);
			map.put("description", description);
			map.put("flow", flow);
			map.put("reason", reason);
			map.put("type", type);
			retMap.put("data", map);
		}
		retMap.put("success",true);
		return retMap;
	}
	
	/**  
	* @Title: saveCounter  
	* @Description: 保存应对措施已经措施和风险事项关系
	* @param counter
	* @param executionEmpId
	* @param itemId
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/saveCounter")
	public Map<String,Object> saveCounter(RiskResponseCounterForm counterForm,String executionEmpId,String itemId){
		String currentUserId = UserContext.getUser().getEmpid();
		String currentDept = UserContext.getUser().getMajorDeptId();
		String[] executionEmpIdArr = {};
		if(executionEmpId != null && !"".equals(executionEmpId)){
			executionEmpIdArr = executionEmpId.split(",");
		}
		String id = counterForm.getId();
		if(id == null || "".equals(id)){
			id = Identities.uuid();
		}
		counterForm.setId(id);
		RiskResponseCounter counter = new RiskResponseCounter();
		String _startTime = counterForm.getStartTimeStr();
		String _finishTime = counterForm.getFinishTimeStr();
		Date startTime = null;
		Date finishTime = null;
		try {
			startTime = _startTime ==null || "".equals(_startTime)?null: DateUtil.parse(_startTime, "yyyy-dd-mm");
			finishTime = _finishTime ==null || "".equals(_finishTime)?null: DateUtil.parse(_finishTime, "yyyy-dd-mm");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		counter.setId(counterForm.getId());
		counter.setDescription(counterForm.getDescription());
		counter.setStartTime(startTime);
		counter.setFinishTime(finishTime);
		counter.setTarget(counterForm.getTarget());
		counter.setCompleteSign(counterForm.getCompleteSign());
		counter.setType(counterForm.getType());
		counter.setCreateUser(new SysEmployee(currentUserId));
		counter.setCreateOrg(new SysOrganization(currentDept));
		return this.riskResponseCounterService.saveCounter(counter,executionEmpIdArr,itemId);
		
	}
	
	
	/**  
	* @Title: loadCounterByItemId  
	* @Description: 根据风险事项id获取措施列表
	* @param itemId
	* @param schemeObjectId
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadCounterByItemId")
	public List<Map<String,Object>> loadCounterByItemId(String itemId,String schemeObjectId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(itemId ==null || "".equals(itemId)){
			return list;
		}
		List<RiskResponseItemCounterRela> itemCounterRelaList = this.riskResponseItemCounterRelaService.findListByItemId(itemId);
		for(RiskResponseItemCounterRela obj :itemCounterRelaList){
			Map<String,Object> map = new HashMap<String,Object>();
			RiskResponseCounter counter = obj.getCounter();
			map.put("id", counter.getId());
			map.put("description", counter.getDescription());
			map.put("target", counter.getTarget());
			map.put("userName", obj.getExecutionEmp().getUsername());
			map.put("userId", obj.getExecutionEmp().getId());
			map.put("completeSign", counter.getCompleteSign());
			list.add(map);
		}
		return list;
		
	}
	
	/**  
	* @Title: loadItemBySchemeId  
	* @Description: 根据方案id获取风险事项列表
	* @param schemeObjectId
	* @param planId
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadItemBySchemeId")
	public List<Map<String,Object>> loadItemBySchemeId(String schemeObjectId,String planId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(schemeObjectId ==null || "".equals(schemeObjectId)){
			return list;
		}
		List<RiskResponseSchemeItemRela> schemeItemRelaList = this.riskResponseSchemeItemRelaService.findBySchemeId(schemeObjectId);
		for(RiskResponseSchemeItemRela obj :schemeItemRelaList){
			Map<String,Object> map = new HashMap<String,Object>();
			RiskResponseItem item = obj.getItem();
			map.put("id", item.getId());
			map.put("description", item.getDescription());
			map.put("flow", item.getFlow());
			map.put("reason", item.getReason());
			map.put("userName",item.getCreateUser().getUsername());
			map.put("userId", item.getCreateUser().getId());
			list.add(map);
		}
		return list;
	}
	
	/**  
	* @Title: doProcessForMakeSchemeOfCommonEmp  
	* @Description: 普通员工方案指定完提交流程
	* @param executionId
	* @param businessId
	* @param deptId
	* @param empType
	* @param majorRiskId void
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/doProcessForMakeSchemeOfCommonEmp")
	public Map<String,Object> doProcessForMakeSchemeOfCommonEmp(String executionId,String businessId,String deptId,String empType,String majorRiskId){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> variables = new HashMap<String,Object>();
		String currentEmpId = UserContext.getUser().getEmpid();
		this.o_jbpmBO.doProcessInstance(executionId, variables);
		map.put("success", true);
		return map;
	}
	/**  
	* @Title: doProcessForMakeSchemeOfManagerEmp  
	* @Description: 部门汇总方案制定提交流程
	* @param executionId
	* @param businessId
	* @param deptId
	* @param empType
	* @param majorRiskId
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/doProcessForMakeSchemeOfManagerEmp")
	public Map<String,Object> doProcessForMakeSchemeOfManagerEmp(String approverId,String executionId,String businessId,String deptId,String empType,String majorRiskId){
		List<Map<String,Object>> approverIdList = JacksonUtilofNullToBlank.toCollection(approverId, new TypeReference<List<Map<String,Object>>>() {});
		//审批人
		String _approverId = approverIdList.get(0).get("id").toString();
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> variables = new HashMap<String,Object>();
		String currentEmpId = UserContext.getUser().getEmpid();
		String collectDept = UserContext.getUser().getMajorDeptId();
		String assigeeKey = "directorApproval";
		variables.put(assigeeKey, _approverId);
		//为主管，领导，副总审批知道审批的是哪个部门
		variables.put("collectDept", collectDept);
		//汇总人肯定为部门风险管理员
		variables.put("collectEmp", currentEmpId);
		this.o_jbpmBO.doProcessInstance(executionId, variables);
		map.put("success", true);
		return map;
	}
	
	/**  
	* @Title: loadSchemeListOfDept  
	* @Description: 部门风险管理员获取本部门普通员工制定的方案列表
	* @param planId
	* @param empType
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadSchemeListOfDept")
	public List<Map<String,Object>> loadSchemeListOfDept(String planId,String empType,String executionId){
		String currentDeptId = UserContext.getUser().getMajorDeptId();
		Tasker tasker = (Tasker) this.o_jbpmBO.getVariableObj("tasker", executionId);
		if(tasker == null){
			tasker = (Tasker) this.o_jbpmBO.getVariableObj("mainTasker", executionId);
		}
		String riskId = tasker.getRiskId();
		List<Map<String,Object>> list = this.riskResponsePlanRiskRelaService.findPlanRiskRelaListByPlanIdAndDeptId(planId, currentDeptId, empType,riskId);
		return list;
	}
	
	/**  
	* @Title: loadCollectSchemeOfDept  
	* @Description: 流程变量中存储了汇总部门id和汇总人员，获取重大风险与计划的关系需要部门id，该id从流程变量中获取
	* @param executionId
	* @param planId
	* @param empType
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadCollectSchemeOfDept")
	public List<Map<String,Object>> loadCollectSchemeOfDept(String executionId,String planId,String empType){
		String deptId = this.o_jbpmBO.getVariable("collectDept", executionId);
		Tasker tasker = (Tasker) this.o_jbpmBO.getVariableObj("tasker", executionId);
		if(tasker == null){
			tasker = (Tasker) this.o_jbpmBO.getVariableObj("mainTasker", executionId);
		}
		String riskId = tasker.getRiskId();
		List<Map<String,Object>> list = this.riskResponsePlanRiskRelaService.findPlanRiskRelaListByPlanIdAndDeptId(planId, deptId, empType,riskId);
		return list;
	}
	
	/**  
	* @Title: getMajorRiskTaskSelectEmpOfDeptIdInVarible  
	* @Description: 流程变量中存储了汇总部门id和汇总人员，获取重大风险与计划的关系需要部门id，该id从流程变量中获取
	* @param businessId
	* @param executionId
	* @return
	* @throws Exception Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/getMajorRiskTaskSelectEmpOfDeptIdInVarible")
	public Map<String,Object> getMajorRiskTaskSelectEmpOfDeptIdInVarible(String businessId,String executionId) throws Exception{
		Map<String,Object> retMap = new HashMap<String,Object>();
		RiskResponsePlanRiskRela entity = new RiskResponsePlanRiskRela();
		entity.setPlan(new RiskAssessPlan(businessId));
		String deptId = this.o_jbpmBO.getVariable("collectDept", executionId);
		entity.setDept(new SysOrganization(deptId));
		Tasker tasker = (Tasker) this.o_jbpmBO.getVariableObj("tasker", executionId);
		if(tasker == null){
			tasker = (Tasker) this.o_jbpmBO.getVariableObj("mainTasker", executionId);
		}
		String _deptType = tasker.getDeptType();
		String riskId = tasker.getRiskId();
		entity.setDeptType(_deptType);
		entity.setMajorRisk(new Risk(riskId));
		RiskResponsePlanRiskRela obj = this.riskResponsePlanRiskRelaService.findOne(entity);
		boolean flag = false;
		Map<String,Object> result = new HashMap<String,Object>();
		if(obj != null){
			result.put("riskId", obj.getMajorRisk().getId());
			result.put("majorRiskName", obj.getMajorRisk().getName());
			result.put("deptId", obj.getDept().getId());
			result.put("deptName", obj.getDept().getOrgname());
			result.put("planId", businessId);
			String deptType = obj.getDeptType();
			if(deptType.equals("M")){
				result.put("deptType", "主责部门");
			}else{
				result.put("deptType", "相关部门");
			}
			String planRiskRelaId = obj.getId();
			RiskResponseTaskExecution taskExecutionParam = new RiskResponseTaskExecution();
			taskExecutionParam.setPlanRiskRelaObj(obj);
			taskExecutionParam.setEmpType("2");//部门风险管理员即汇总人员
			List<RiskResponseTaskExecution> taskExecutionObjList = this.riskResponseTaskExecutionService.findList(taskExecutionParam);
			//整理应对人
			List<Map<String,Object>> empList = new ArrayList<Map<String,Object>>();
			if(taskExecutionObjList!= null){
				for(RiskResponseTaskExecution taskExecutionObj : taskExecutionObjList){
					Map<String,Object> empMap = new HashMap<String,Object>();
					empMap.put("id", taskExecutionObj.getExecuteEmp().getId());
					empMap.put("name", taskExecutionObj.getExecuteEmp().getEmpname());
					empList.add(empMap);
				}
				result.put("empList", empList);
			}
			flag = true;
		}
		
		retMap.put("data", result);
		retMap.put("success", flag);
		return retMap;
	}
	
	/**  
	* @Title: schemeApprove  
	* @Description: 部门主管，领导，副总审批流程
	* @param businessId
	* @param executionId
	* @param isPass
	* @param examineApproveIdea
	* @param approverId
	* @param approverKey
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/schemeApprove")
	public Map<String,Object> schemeApprove(String businessId,String executionId,String isPass,String examineApproveIdea,String approverId,String approverKey){
		
		String _appriverId = "";
		if(approverId!=null && !"".equals(approverId)){
			List<Map<String,Object>> approverIdList = JacksonUtilofNullToBlank.toCollection(approverId, new TypeReference<List<Map<String,Object>>>() {});
			//审批人
			_appriverId = approverIdList.get(0).get("id").toString();
		}
		return this.riskResponseSchemeService.schemeApprove(businessId, executionId, isPass, examineApproveIdea, _appriverId, approverKey);
	}
	
	/**  
	* @Title: loadAllSchemeListOfDept  
	* @Description: 获取该计划下所有部门汇总的方案
	* @param planId
	* @param empType
	* @return List<Map<String,Object>>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/loadAllSchemeListOfDept")
	public List<Map<String,Object>> loadAllSchemeListOfDept(String planId,String empType){
		List<Map<String,Object>> list = this.riskResponsePlanRiskRelaService.findPlanRiskRelaListByPlanIdAndDeptId(planId, empType);
		return list;
	}
	/**  
	* @Title: finishProcess  
	* @Description: 方案整理流程结束
	* @param businessId
	* @param executionId
	* @param empType
	* @return Map<String,Object>
	* @throws  
	*/
	@ResponseBody
	@RequestMapping("/majorResponse/finishProcess")
	public Map<String,Object> finishProcess(String businessId,String executionId,String empType){
		Map<String,Object> map = new HashMap<String,Object>();
		//TODO 方案制定流程结束发起方案实施流程
		//流程中设计各个部门制定的方案信息
		List<Map<String,Object>> list = this.riskResponsePlanRiskRelaService.findPlanRiskRelaListByPlanIdAndDeptId(businessId, empType);
		Map<String,Object> variables = new HashMap<String,Object>();
		this.o_jbpmBO.doProcessInstance(executionId, variables);
		map.put("success", true);
		return map;
	}
}
