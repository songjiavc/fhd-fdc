/**
 * 
 * 
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-9-27 		宋佳
 * 
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.web.controller.response.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.response.ResponseSolutionBO;
import com.fhd.ra.business.response.execute.ResponseSolutionExecuteBO;
import com.fhd.ra.web.form.response.solution.SolutionExecutionHistoryForm;


/**
 * 
 */
@Controller
public class ResponseSolutionExecuteControl{
	@Autowired
	private ResponseSolutionExecuteBO o_responseSolutionExecuteBO;
	/**
	 * 保存执行历史记录
	 * add by 宋佳
	 * 
	 */
	@ResponseBody
	@RequestMapping("/response/responsesolutionexecutecontrol/saveExecutionHistory.f")
	public Map<String,Object> saveExecutionHistory(SolutionExecutionHistoryForm solutionExecutionHistoryForm){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		o_responseSolutionExecuteBO.saveExecutionRecordByForm(solutionExecutionHistoryForm);
		returnMap.put("success", true);
		return returnMap;
	}
	
	/**
	 * 工作流汇总，应对措施状态更改为已完成
	 * @param SolutionExecuteBpmObjectList	应对措施封装对象
	 */	
	public void updateSolutionStatus(List<SolutionExecuteBpmObject> SolutionExecuteBpmObjectList){
		ResponseSolutionBO o_responseSolutionBO = ContextLoader.getCurrentWebApplicationContext().getBean(ResponseSolutionBO.class);
		for(SolutionExecuteBpmObject temp : SolutionExecuteBpmObjectList){
			o_responseSolutionBO.updateStatusFromSolutionById(temp.getSolutionId(), Contents.DEAL_STATUS_FINISHED);
		}
	}
}
