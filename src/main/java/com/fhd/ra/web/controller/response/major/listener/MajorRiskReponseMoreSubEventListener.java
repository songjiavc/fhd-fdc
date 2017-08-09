package com.fhd.ra.web.controller.response.major.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.springframework.web.context.ContextLoader;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.response.major.RiskResponsePlanRiskRela;
import com.fhd.entity.response.major.RiskResponseTaskExecution;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.response.major.RiskResponsePlanRiskRelaService;
import com.fhd.ra.business.response.major.RiskResponseTaskExecutionService;
import com.fhd.ra.web.controller.response.major.utils.Tasker;

/**
 * @author Jzq
 * 子流程监听类
 */
public class MajorRiskReponseMoreSubEventListener implements EventListener{

	private static final long serialVersionUID = 7932226739943170242L;

	@Override
	public void notify(EventListenerExecution eventListener) throws Exception {
		String planId = eventListener.getVariable("id").toString();
		Tasker mainTasker =  (Tasker) eventListener.getVariable("mainTasker");
		String riskId = mainTasker.getRiskId();
		String deptType = mainTasker.getDeptType();
		String _deptId = mainTasker.getDeptId();
		RiskResponsePlanRiskRelaService riskResponsePlanRiskRelaService =  ContextLoader.getCurrentWebApplicationContext().getBean(RiskResponsePlanRiskRelaService.class);
		RiskResponseTaskExecutionService riskResponseTaskExecutionService = ContextLoader.getCurrentWebApplicationContext().getBean(RiskResponseTaskExecutionService.class);
		RiskScoreBO o_riskScoreBO = ContextLoader.getCurrentWebApplicationContext().getBean(RiskScoreBO.class);
		
		//获取当前部门
		String deptId = UserContext.getUser().getMajorDeptId();
		//计划id与部门id确定唯一的应对对象 
		RiskResponsePlanRiskRela paramObj = new RiskResponsePlanRiskRela();
		paramObj.setPlan(new RiskAssessPlan(planId));
		paramObj.setDept(new SysOrganization(deptId));
		paramObj.setMajorRisk(new Risk(riskId));
		paramObj.setDeptType(deptType);
		RiskResponsePlanRiskRela riskResponsePlanRiskRela = riskResponsePlanRiskRelaService.findOne(paramObj);
		RiskResponseTaskExecution paramEntity = new RiskResponseTaskExecution();
		paramEntity.setPlanRiskRelaObj(riskResponsePlanRiskRela);
		paramEntity.setEmpType("1");
		List<RiskResponseTaskExecution> list = riskResponseTaskExecutionService.findList(paramEntity);
		
		List<Tasker> taskers = new ArrayList<Tasker>();
		if(list != null && list.size()>0)
		for(RiskResponseTaskExecution rrte :list){
			Tasker tasker = new Tasker();
			tasker.setDeptId(rrte.getPlanRiskRelaObj().getDept().getId());
			tasker.setDeptType(rrte.getPlanRiskRelaObj().getDeptType());
			tasker.setRiskId(rrte.getPlanRiskRelaObj().getMajorRisk().getId());
			tasker.setDeptCommonEmp(rrte.getExecuteEmp().getId());
			taskers.add(tasker);
		}
		eventListener.setVariable("riskId", riskId);
		eventListener.setVariable("deptType", deptType);
		eventListener.setVariable("deptId", _deptId);
		
		eventListener.setVariable("taskers", taskers);
		eventListener.setVariable("businessId",planId );
		eventListener.setVariable("joinCount",taskers.size());
		String rodeCode = "DeptRiskManager";//部门风险管理员角色编号
		SysEmployee deptRiskManager = o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId, rodeCode);//部门风险管理人员
		//部门主管审批节点也就是子流程中join后第一个节点要在这定义好，未知原因，否则获取不到流程实例，即获取不到流程变量
		eventListener.setVariable("deptRiskManagerForSub",deptRiskManager.getId());
	}

}
