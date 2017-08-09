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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.fhd.dao.response.SolutionExecutionDAO;
import com.fhd.dao.response.SolutionExecutionHistoryDAO;
import com.fhd.entity.response.SolutionExecutionHistory;


/**
 * @author 宋佳
 * @version 尝试性基类
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see
 */
@Service
public class ExecutionHistoryListBO{
	@Autowired
	private SolutionExecutionDAO o_solutionExecutionDAO;
	@Autowired
	private SolutionExecutionHistoryDAO o_solutionExecutionHistoryDAO;
	/**
	 *  通用分页查询
	 */
	/**
	 * 根据查询条件分页查询体系建设计划列表.
	 * @author 宋佳
	 * @param page
	 * @param sort
	 * @param dir
	 * @param query 查询条件
	 * @return Page<ConstructPlan>
	 * @since fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<SolutionExecutionHistory> findExecutionHistoryListByExecutionId(String executionId) {
		Criteria craHistory = o_solutionExecutionHistoryDAO.createCriteria();
		craHistory.createAlias("solutionExecution", "solutionExecution").setFetchMode("solutionExecution", FetchMode.JOIN);
		craHistory.add(Restrictions.eq("solutionExecution.id", executionId));
		return craHistory.list();
	}
}
