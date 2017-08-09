package com.fhd.ra.business.contingencyplan;

import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.springframework.web.context.ContextLoader;

public class ContingencyPlanListener implements EventListener{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void notify(EventListenerExecution EventListener) throws Exception {
		
		ContextLoader.getCurrentWebApplicationContext().getBean(ScoreObjectBO.class);
		RiskScoreBO o_riskScoreBO = ContextLoader.getCurrentWebApplicationContext().getBean(RiskScoreBO.class);
        SysEmployee deptLeader = o_riskScoreBO.findRiskManagerByCompanyId();//查询该公司的风险管理员
        if(null == deptLeader){
            deptLeader = UserContext.getUser().getEmp();//如果没有风险管理员则把任务分配给当前登录人
        }

        //从数据库里取出来
		EventListener.setVariable("approveFore", deptLeader.getId());
		EventListener.setVariable("businessId", EventListener.getVariable("id"));
		EventListener.setVariable("name", EventListener.getVariable("name"));
		
	}

}
