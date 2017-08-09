/*
 * 北京第一会达风险管理有限公司 版权所有 2013
 * Copyright(C) 2013 Firsthuida Co.,Ltd. All rights reserved. 
 */
package com.fhd.comm.web.controller.wp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.wp.MilestionBO;
import com.fhd.comm.business.wp.WorkPlanBO;
import com.fhd.entity.wp.EnforcementOrgs;
import com.fhd.entity.wp.WorkPlan;

/**
 * 工作计划驾驶舱
 *
 * @author   胡迪新
 * @since    fhd Ver 4.5
 * @Date	 2013-3-6  上午9:31:06
 *
 * @see 	 
 */
@Controller
public class WorkPlanDashboardControl {

	@Autowired
	private WorkPlanBO o_workPlanBO;
	@Autowired
	private MilestionBO o_milestionBO;
	
	/*
	 * 查询
	 */
	
	/**
	 * 
	 * <pre>
	 * 驾驶舱显示当年工作计划的总体进度
	 * </pre>
	 * 
	 * @author 胡迪新
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/wp/dashboard/findworkplanexecutionstatus.f")
	public List<Map<String, Object>> findWorkPlanExecutionStatus() {
		Calendar calendar = Calendar.getInstance();
		Integer year = calendar.get(Calendar.YEAR);
		
		List<Map<String, Object>> jsonWorkPlans = new ArrayList<Map<String,Object>>();
		
		List<WorkPlan> workPlans = o_workPlanBO.findWorkPlanByYear(year);
		for (WorkPlan workPlan : workPlans) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("workPlan", workPlan.getName());
			
			// 公司的执行情况
			List<Map<String, Object>> jsonEnforcementOrgs = new ArrayList<Map<String,Object>>();
			for (EnforcementOrgs enforcementOrgs : workPlan.getEnforcementOrgses()) {
				Map<String, Object> enforcementOrgsData = new HashMap<String, Object>();
				enforcementOrgsData.put("id", enforcementOrgs.getId());
				enforcementOrgsData.put("orgName", enforcementOrgs.getOrg().getOrgname());
				enforcementOrgsData.put("status", enforcementOrgs.getStatus());
				enforcementOrgsData.put("attainmentRate", enforcementOrgs.getAttainmentRate());
				enforcementOrgsData.put("contributeAmount", enforcementOrgs.getContributeAmount());
				enforcementOrgsData.put("contributeLevel", enforcementOrgs.getContributeLevel());
				jsonEnforcementOrgs.add(enforcementOrgsData);
			}
			data.put("enforcementOrgs", jsonEnforcementOrgs);
			
			// 状态图表
			List<Object[]> milestoneExecuteStatus = o_milestionBO.findMilestoneForDashboardByWorkPlanId(workPlan.getId());
			Map<Object, Object> status = new HashMap<Object, Object>();
			for (Object[] objects : milestoneExecuteStatus) {
				if(null != objects[0]){
					status.put(objects[0], objects[1]);
				}
				
			}
			if(status.size() > 0){
				data.put("executeStatus", status);
			}
			
			jsonWorkPlans.add(data);
		}
		
		return jsonWorkPlans;
		
	}
	
	
}

