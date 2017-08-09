package com.fhd.entity.assess.quaAssess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.risk.Dimension;

/**
 * 问卷统计结果
 */
@Entity
@Table(name = "T_RM_STATISTICS_RESULT")
public class StatisticsResult extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	/**维度*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCORE_DIM_ID")
	private Dimension dimension;
	
	/**评估计划*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESS_PLAN_ID")
	private RiskAssessPlan riskAssessPlan;
	
	/**打分对象*/
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "SCORE_OBJECT_ID")
	@Column(name = "SCORE_OBJECT_ID")
	private String riskScoreObject;
	
	/**分值*/
	@Column(name = "SCORE")
	private String score;
	
	/**状态*/
	@Column(name = "STATUS")
	private String status;

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public RiskAssessPlan getRiskAssessPlan() {
		return riskAssessPlan;
	}

	public String getRiskScoreObject() {
		return riskScoreObject;
	}

	public void setRiskScoreObject(String riskScoreObject) {
		this.riskScoreObject = riskScoreObject;
	}

	public void setRiskAssessPlan(RiskAssessPlan riskAssessPlan) {
		this.riskAssessPlan = riskAssessPlan;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}