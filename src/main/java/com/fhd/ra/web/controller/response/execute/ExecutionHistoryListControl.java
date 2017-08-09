/**
 *  
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.web.controller.response.execute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.response.SolutionExecutionHistory;
import com.fhd.ra.business.response.execute.ExecutionHistoryListBO;


/**
 * 
 */
@Controller
public class ExecutionHistoryListControl{
	@Autowired
	private ExecutionHistoryListBO o_executionHistoryListBO;
	/**
	 * 建设计划列表.
	 * @author 宋佳
	 * @param limit
	 * @param start
	 * @param sort
	 * @param dir
	 * @param query 查询条件
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/response/execute/executionhistorylist.f")
	public Map<String, Object> findExecutionHistoryListByExecutionId(String solutionExecutionId) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<SolutionExecutionHistory> solutionExecutionHistoryList = o_executionHistoryListBO.findExecutionHistoryListByExecutionId(solutionExecutionId);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (SolutionExecutionHistory solutionExecutionHistory : solutionExecutionHistoryList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", solutionExecutionHistory.getId());
			map.put("realStartTime", solutionExecutionHistory.getRealStartTime());
			map.put("realFinishTime", solutionExecutionHistory.getRealFinishTime());
			map.put("realCost", solutionExecutionHistory.getRealCost());
			map.put("realIncome", solutionExecutionHistory.getRealIncome());
			map.put("progress", solutionExecutionHistory.getProgress()+"%");
			map.put("desc", solutionExecutionHistory.getDesc());
			dataList.add(map);
		}
		result.put("datas", dataList);
		return result;
	}
	
}
