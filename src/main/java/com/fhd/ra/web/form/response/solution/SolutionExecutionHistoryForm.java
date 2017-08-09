package com.fhd.ra.web.form.response.solution;

import com.fhd.entity.response.SolutionExecutionHistory;

public class SolutionExecutionHistoryForm extends SolutionExecutionHistory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    /**
     * solutionExecutionId  应对计划执行id
     */
	private String solutionExecutionId = "";
	
	public String getSolutionExecutionId() {
		return solutionExecutionId;
	}
	public void setSolutionExecutionId(String solutionExecutionId) {
		this.solutionExecutionId = solutionExecutionId;
	}
	
	
}
