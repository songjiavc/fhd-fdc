package com.fhd.entity.risk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author jia.song@pcitc.com
 */
@Embeddable
public class TempSyncRiskrbsScoreIdClass implements Serializable{

	private static final long serialVersionUID = -2132587611198273848L;

	@Column(name = "	RISK_ID",length=25)
	private String riskId;

	
	@Column(name = "ASSESS_PLAN_ID")
	private String assessPlanId;

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getAssessPlanId() {
		return assessPlanId;
	}

	public void setAssessPlanId(String assessPlanId) {
		this.assessPlanId = assessPlanId;
	}
}

