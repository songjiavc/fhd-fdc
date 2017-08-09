package com.fhd.ra.business.assess.risktidy;

import org.apache.log4j.Logger;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.task.Assignable;
import org.jbpm.api.task.AssignmentHandler;
import org.springframework.web.context.ContextLoader;

import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.ra.business.assess.email.RiskTidyEmailBO;

/***
 * 风险整理发送邮件
 * */
public class RiskTidyHandler implements AssignmentHandler{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(RiskTidyHandler.class);
	
	/**
	 * 风险整理发送邮件
	 * @param arg0 工作流参数
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Override
	public void assign(Assignable arg0, OpenExecution arg1) throws Exception {
		try {
			String assessPlanId = arg1.getVariable("id").toString();
			RiskTidyEmailBO o_riskTidyEmailBO = ContextLoader.getCurrentWebApplicationContext().getBean(RiskTidyEmailBO.class);
			JBPMOperate o_jbpmOperate = ContextLoader.getCurrentWebApplicationContext().getBean(JBPMOperate.class);
			String taskId = o_jbpmOperate.getTaskService().createTaskQuery().executionId(arg1.getId()).uniqueResult().getId();
			String taskName = o_jbpmOperate.getTaskService().createTaskQuery().executionId(arg1.getId()).uniqueResult().getActivityName();
			String empId = o_jbpmOperate.getTaskService().createTaskQuery().executionId(arg1.getId()).uniqueResult().getAssignee();
			if("评估结果整理".equalsIgnoreCase(taskName)){
				o_riskTidyEmailBO.sendRiskTidyEmail(assessPlanId, empId, taskId);
			}
		} catch (Exception e) {
			log.error("============================风险整理发送邮件异常:" + e.getMessage() + "============================");
		}
	}
}
