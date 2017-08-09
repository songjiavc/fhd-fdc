/**
 * AssessPlanBO.java
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		刘中帅
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.business.response.execute;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.response.SolutionExecutionDAO;
import com.fhd.dao.response.SolutionExecutionHistoryDAO;
import com.fhd.entity.response.SolutionExecution;
import com.fhd.entity.response.SolutionExecutionHistory;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.web.form.response.solution.SolutionExecutionHistoryForm;


/**
 * @author 宋佳
 * @version 尝试性基类
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see
 */
@Service
public class ResponseSolutionExecuteBO{
	@Autowired
	private SolutionExecutionDAO o_solutionExecutionDAO;
	@Autowired
	private SolutionExecutionHistoryDAO o_solutionExecutionHistoryDAO;
	/**
	 * 更改执行应对的状态
	 * add by 宋佳
	 * 2013-9-27
	 */
	@Transactional
	public int updateStatusFromSolutionExecutionBy(List<String> items,String status){
		String hqlSql = "update SolutionExecution ex set ex.status=:status where ex.id = :executionId";
		Map<String,Object> paramMap = o_solutionExecutionDAO.getParameterMap();
		paramMap.put("status", status);
		paramMap.put("executionId", items);
		int upCount = o_solutionExecutionDAO.createQuery(hqlSql, paramMap).executeUpdate();
		return upCount;
	}
	
	/**
	 *  保存执行记录
	 */
	@Transactional
	@RecordLog("保存应对计划执行记录")
	public void saveExecutionRecordByForm(SolutionExecutionHistoryForm solutionExecutionHistoryForm){
		//插入执行历史表
		SolutionExecutionHistory solutionExecutionHistory = new SolutionExecutionHistory();
		solutionExecutionHistory.setId(Identities.uuid());
		solutionExecutionHistory.setSolutionExecution(new SolutionExecution(solutionExecutionHistoryForm.getSolutionExecutionId()));
		solutionExecutionHistory.setProgress(solutionExecutionHistoryForm.getProgress());
		solutionExecutionHistory.setDesc(solutionExecutionHistoryForm.getDesc());
		solutionExecutionHistory.setRealCost(solutionExecutionHistoryForm.getRealCost());
		solutionExecutionHistory.setRealIncome(solutionExecutionHistoryForm.getRealIncome());
		solutionExecutionHistory.setRealStartTime(solutionExecutionHistoryForm.getRealStartTime());
		solutionExecutionHistory.setRealFinishTime(solutionExecutionHistoryForm.getRealFinishTime());
		o_solutionExecutionHistoryDAO.merge(solutionExecutionHistory);
		//修改执行表记录
		SolutionExecution solutionExecution = this.getSolutionExecutionById(solutionExecutionHistoryForm.getSolutionExecutionId());
		solutionExecution.setLastModifyBy(new SysEmployee(UserContext.getUser().getEmpid()));
		solutionExecution.setLastModifyTime(new Date());
		// 应对保存历史记录后，马上更改应对执行状态为 已处理
		solutionExecution.setStatus(Contents.DEAL_STATUS_HANDLING);
		o_solutionExecutionDAO.merge(solutionExecution);
	}
	/**
	 * 获取执行实体通过执行id
	 * add by 宋佳
	 * 
	 */
	
	public SolutionExecution getSolutionExecutionById(String id){
		return o_solutionExecutionDAO.get(id);
	}
	

}
