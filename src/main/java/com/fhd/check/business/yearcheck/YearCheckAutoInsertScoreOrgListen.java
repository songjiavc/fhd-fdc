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



	public class YearCheckAutoInsertScoreOrgListen implements EventListener{

		private static final long serialVersionUID = 1L;
		
		@Override
		public void notify(EventListenerExecution eventListener) throws Exception {
			
			YearCheckPlanOrgBO yearCheckPlanOrgBO = ContextLoader.getCurrentWebApplicationContext().getBean(YearCheckPlanOrgBO.class);
			CheckDetailBO checkDetailBO = ContextLoader.getCurrentWebApplicationContext().getBean(CheckDetailBO.class);
			YearCheckScoreOrgBO yearCheckScoreOrgBO = ContextLoader.getCurrentWebApplicationContext().getBean(YearCheckScoreOrgBO.class);
			Object businessId=eventListener.getVariable("id");
			List<YearCheckPlanOrg> checkPlans=yearCheckPlanOrgBO.getPlanOrgByPlanID(businessId.toString(),null,null);
		
			//将打分表及评价准则表数据复制
			List<CheckDetail> detailList=checkDetailBO.findCheckDetail(null);
			List<YearCheckScoreOrg> yearCheckScoreOrgs=new ArrayList<YearCheckScoreOrg>();
			for (int i = 0; i < checkPlans.size(); i++) {
				for (CheckDetail checkDetail:detailList) {
					YearCheckScoreOrg yearCheckScoreOrg=new YearCheckScoreOrg();
					yearCheckScoreOrg.setCheckDetailName(checkDetail.getName());
					yearCheckScoreOrg.setCheckDetailDescribe(checkDetail.getDetailStandard());
					yearCheckScoreOrg.setCheckDetailScore(checkDetail.getDetailScore());
					yearCheckScoreOrg.setCheckCommenttName(checkDetail.getCheckComment().getName());
					yearCheckScoreOrg.setCheckProjectName(checkDetail.getCheckComment().getProject().getName());
					yearCheckScoreOrg.setCheckProjectScore(Integer.parseInt(checkDetail.getCheckComment().getProject().getTotalScore()));
					yearCheckScoreOrg.setPlanOrgId(checkPlans.get(i));
					yearCheckScoreOrgs.add(yearCheckScoreOrg);
				}
			}
			yearCheckScoreOrgBO.savaCheckScoreOrg(yearCheckScoreOrgs,null);
		}
	}


