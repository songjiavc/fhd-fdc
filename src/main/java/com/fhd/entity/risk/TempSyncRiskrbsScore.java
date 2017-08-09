package com.fhd.entity.risk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jia.song@pcitc.com
 */
@Entity
@Table(name = "TEMP_SYNC_RISKRBS_SCORE") 
public class TempSyncRiskrbsScore implements Serializable{

	private static final long serialVersionUID = -2132587611198273848L;

	private TempSyncRiskrbsScoreIdClass pk;
	
	public void setPk(TempSyncRiskrbsScoreIdClass pk) {
		this.pk = pk;
	}

	@Id
	public TempSyncRiskrbsScoreIdClass getPk(){
		return pk;
	}
	
	@Column(name = "RISK_SCORE")
	private double riskScore;
	
	public double getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(double riskScore) {
		this.riskScore = riskScore;
	}
}

