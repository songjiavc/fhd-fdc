package com.fhd.ra.business.riskidentify;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.springframework.web.context.ContextLoader;

import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.quaAssess.DeptLeadCircusee;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.DeptLeadCircuseeBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;

public class RiskIdentifyTaskListener implements EventListener{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void notify(EventListenerExecution EventListener) throws Exception {
		
		ContextLoader.getCurrentWebApplicationContext().getBean(ScoreObjectBO.class);
		ScoreObjectBO o_scoreObjectBO = ContextLoader.getCurrentWebApplicationContext().getBean(ScoreObjectBO.class);
		RiskScoreBO o_riskScoreBO = ContextLoader.getCurrentWebApplicationContext().getBean(RiskScoreBO.class);
		DeptLeadCircuseeBO o_deptLeadCircuseeBO = ContextLoader.getCurrentWebApplicationContext().getBean(DeptLeadCircuseeBO.class);
		ContextLoader.getCurrentWebApplicationContext().getBean(RangObjectDeptEmpBO.class);
		
		String deptId = UserContext.getUser().getMajorDeptId();//登录用户所在部门
		String assessPlanId = EventListener.getVariable("id").toString();//计划id
		
		ArrayList<String> empList = o_scoreObjectBO.findRangObjectDeptEmpEmpIdListByPlanIdAndDeptId(assessPlanId, deptId);
		
		List<RiskAssessPlanTakerObject> assessPlanTakerList =  new ArrayList<RiskAssessPlanTakerObject>();
		for (String empId : empList) {
			RiskAssessPlanTakerObject assessPlanTaker = new RiskAssessPlanTakerObject();
			assessPlanTaker.setRiskTaskEvaluatorId(empId);//评估人
			assessPlanTakerList.add(assessPlanTaker);
		}
		
		String rodeCode = "DeptRiskManager";//部门风险管理员角色编号
		SysEmployee deptLeader = o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId, rodeCode);//部门风险管理人员或 当前登录人
		if(null == deptLeader){
		    deptLeader = UserContext.getUser().getEmp();
		}
		
		//存储部门领导对应打分对象
		List<RiskScoreObject> scoreObjectsBydeptId = 
				o_scoreObjectBO.findScoreObjectsBydeptId(deptId, EventListener.getVariable("id").toString());
		List<DeptLeadCircusee> saveDeptLeads = new ArrayList<DeptLeadCircusee>();
		for (RiskScoreObject riskScoreObject : scoreObjectsBydeptId) {
			DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
			deptLeadCircusee.setId(Identities.uuid());
			deptLeadCircusee.setDeptLeadEmpId(deptLeader);
			RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
			riskAssessPlan.setId(EventListener.getVariable("id").toString());
			deptLeadCircusee.setRiskAssessPlan(riskAssessPlan);
			deptLeadCircusee.setScoreObjectId(riskScoreObject);
			saveDeptLeads.add(deptLeadCircusee);
		}
		o_deptLeadCircuseeBO.saveDeptLeadCircuseeList(saveDeptLeads);
		
		EventListener.setVariable("riskTaskEvaluators", assessPlanTakerList);
		EventListener.setVariable("businessId", EventListener.getVariable("id"));
		EventListener.setVariable("name", EventListener.getVariable("name"));
		EventListener.setVariable("approvePerson", deptLeader.getId());
		EventListener.setVariable("joinCount", assessPlanTakerList.size());
		EventListener.setVariable("deptIdBS", deptId);
	}

}
