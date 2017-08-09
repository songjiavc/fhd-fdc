package com.fhd.entity.assess.formulatePlan;

import java.io.Serializable;
/**
 * 评估计划承办人
 * @author 王再冉
 *
 */
public class RiskAssessPlanTakerObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 评估计划承办人
	 */
	private String planTakerEmpId;
	/**
	 *  评估人
	 */
	private String riskTaskEvaluatorId;

	public String getPlanTakerEmpId() {
		return planTakerEmpId;
	}

	public void setPlanTakerEmpId(String planTakerEmpId) {
		this.planTakerEmpId = planTakerEmpId;
	}

	public String getRiskTaskEvaluatorId() {
		return riskTaskEvaluatorId;
	}

	public void setRiskTaskEvaluatorId(String riskTaskEvaluatorId) {
		this.riskTaskEvaluatorId = riskTaskEvaluatorId;
	}

	
	

}
