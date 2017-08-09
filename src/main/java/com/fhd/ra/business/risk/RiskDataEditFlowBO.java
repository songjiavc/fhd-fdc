package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.entity.risk.EmpRelaRiskIdea;
import com.fhd.entity.risk.RiskIdea;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;

@Service
public class RiskDataEditFlowBO {

	@Autowired
	private EmpRelaRiskIdeaBO o_empRelaRiskIdeaBO;
	
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	
	@Autowired
	private RiskIdeaBO o_riskIdeaBO;

	@Autowired
	private JBPMBO o_jbpmBO;
	
	/**
	 * 由定时任务开启
	 */
	@SuppressWarnings("unused")
	@Transactional
	public boolean submitSavedRiskToApprover(){
		String entityType = "riskDataEdit";	//流程名称
		ArrayList<HashMap<String, String>> orgIdList = o_empRelaRiskIdeaBO.findEmpRelaRiskIdeaGroupOrgId();
		//3.开启工作流
		for(HashMap<String, String> map : orgIdList){
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("entityType", entityType);
			variables.put("name", map.get("orgName") + "编辑风险");
			variables.put("id", map.get("orgId") + "Work");
			//根据部门id找到部门领导
			String rodeCode = "DeptRiskManager";	//部门风险管理员角色编号
			SysEmployee emp = o_riskScoreBO.findEmpsByRoleIdAnddeptId(map.get("orgId"), rodeCode);
			String deptLeaderId = emp.getId();
			variables.put("AssessPlanApproverEmpId", deptLeaderId);//审批部门领导
			String executionId = o_jbpmBO.startProcessInstance(entityType,variables);//开启工作流
		}
		
		return true;
	}
	
	/**
	 * 工作流第二步
	 */
	@Transactional
	public void approveRisk(String executionId, String isPass, String examineApproveIdea){
		if ("no".equals(isPass)) {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
			//审批通过
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("path", isPass);
			//更加部门id找到风险管理员
			variables.put("path", "yes");
			SysEmployee sysEmployee = o_riskScoreBO.findRiskManagerByCompanyId();
			variables.put("makePlanEmpId", null!=sysEmployee?sysEmployee.getId():"");//该公司的风险管理员
			o_jbpmBO.doProcessInstance(executionId, variables);
		}
	}
	
	/**
	 * 工作流第三步--结束工作流
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 */
	@Transactional
	public void archiveRisk(String executionId, String empRelaRiskIdeaIdDatas){
		JSONArray jsonarr = JSONArray.fromObject(empRelaRiskIdeaIdDatas);
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			String empRelaRiskIdeaId = jsobjs.getString("empRelaRiskIdeaId");
			EmpRelaRiskIdea empRelaRiskIdea = new EmpRelaRiskIdea();
			empRelaRiskIdea.setId(empRelaRiskIdeaId);
			o_empRelaRiskIdeaBO.delEmpRelaRiskIdea(empRelaRiskIdea);
		}
		
		
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			String empRelaRiskIdeaId = jsobjs.getString("empRelaRiskIdeaId");
			
			RiskIdea riskIdea = o_riskIdeaBO.findRiskIdeaByEmpRelaRiskIdeaId(empRelaRiskIdeaId);
			o_riskIdeaBO.delRiskIdea(riskIdea);
		}
		
		
		Map<String, Object> variables = new HashMap<String, Object>();
		o_jbpmBO.doProcessInstance(executionId, variables);
	}
}