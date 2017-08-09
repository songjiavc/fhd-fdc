package com.fhd.ra.web.controller.response.execute;

import java.io.Serializable;

/**
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-4-17		下午 14:18
 * @see 	 
 */
public class SolutionExecuteBpmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 *    应对执行人
	 */
	private String solutionExecuteEmpId = "";
	/**
	 *  应对执行id
	 * @return
	 */
	private String solutionExecuteId = "";
	
	/**
	 * 应对 id
	 * @return
	 */
	private String solutionId = "";
	
	/**
	 * 风险id
	 * @return
	 */
	private String riskId = "";
	
	public String getSolutionExecuteEmpId() {
		return solutionExecuteEmpId;
	}

	public void setSolutionExecuteEmpId(String solutionExecuteEmpId) {
		this.solutionExecuteEmpId = solutionExecuteEmpId;
	}

	public String getSolutionExecuteId() {
		return solutionExecuteId;
	}

	public void setSolutionExecuteId(String solutionExecuteId) {
		this.solutionExecuteId = solutionExecuteId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}
	
}