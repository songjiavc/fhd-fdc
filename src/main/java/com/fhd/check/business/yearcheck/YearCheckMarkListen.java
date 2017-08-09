package com.fhd.check.business.yearcheck;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.springframework.web.context.ContextLoader;

import com.fhd.check.business.checkDetail.CheckDetailBO;
import com.fhd.entity.check.checkdetail.CheckDetail;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlanOrg;
import com.fhd.entity.check.yearcheck.plan.YearCheckScoreOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;



	public class YearCheckMarkListen implements EventListener{

		private static final long serialVersionUID = 1L;
		
		@Override
		public void notify(EventListenerExecution eventListener) throws Exception {
			
			RiskScoreBO riskScoreBO = ContextLoader.getCurrentWebApplicationContext().getBean(RiskScoreBO.class);
			YearCheckPlanOrgBO yearCheckPlanOrgBO = ContextLoader.getCurrentWebApplicationContext().getBean(YearCheckPlanOrgBO.class);
			Object businessId=eventListener.getVariable("id");
			List<SysEmployee> raters=new ArrayList<SysEmployee>();
			List<YearCheckPlanOrg> checkPlans=yearCheckPlanOrgBO.getPlanOrgByPlanID(businessId.toString(),null,null);
			for (int i = 0; i < checkPlans.size(); i++) {
				SysEmployee emp=riskScoreBO.findEmpsByRoleIdAnddeptId(checkPlans.get(i).getOrgId().getId(),"DeptRiskManager");
				raters.add(emp);
			}
			eventListener.setVariable("raters", raters);
			eventListener.setVariable("joinCountForSon", checkPlans.size());
			eventListener.setVariable("businessId", businessId);
			eventListener.setVariable("name", eventListener.getVariable("name"));
	
		}
	}


