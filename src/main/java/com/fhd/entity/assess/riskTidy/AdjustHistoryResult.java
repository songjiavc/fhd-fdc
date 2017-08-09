package com.fhd.entity.assess.riskTidy;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.risk.Dimension;

/**
 * 定性评估结果
 */
@Entity
@Table(name = "T_RM_ADJUST_HISTORY_RESULT")
public class AdjustHistoryResult extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	
	/**维度*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCORE_DIM_ID")
	private Dimension dimension;
	
	/**历史调整记录ID*/
	@Column(name = "ADJUST_HISTORY_ID")
	private String adjustHistoryId;
	
	/**分值*/
	@Column(name = "SCORE")
	private String score;
	
	/**评估计划*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESS_PLAN_ID")
	private RiskAssessPlan riskAssessPlan;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "UPDATER")
	private String updater;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCORE_OBJECT_ID")
	private RiskScoreObject riskScoreObject;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "UPDATE_TIME", length = 7)
	private Date updateTime;
	
	/**  
	  * @Description: 增加评估人数属性，该属性不存在表中
	  * @author jia.song@pcitc.com
	  * @date 2017年4月20日 上午9:38:06 
	  */
	@Transient
	private String count;
	
	
	public Dimension getDimension() {
		return dimension;
	}


	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}


	public String getAdjustHistoryId() {
		return adjustHistoryId;
	}


	public void setAdjustHistoryId(String adjustHistoryId) {
		this.adjustHistoryId = adjustHistoryId;
	}


	public String getScore() {
		return score;
	}


	public void setScore(String score) {
		this.score = score;
	}


	public RiskAssessPlan getRiskAssessPlan() {
		return riskAssessPlan;
	}


	public void setRiskAssessPlan(RiskAssessPlan riskAssessPlan) {
		this.riskAssessPlan = riskAssessPlan;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getUpdater() {
		return updater;
	}


	public void setUpdater(String updater) {
		this.updater = updater;
	}
	
	public RiskScoreObject getRiskScoreObject() {
		return riskScoreObject;
	}


	public void setRiskScoreObject(RiskScoreObject riskScoreObject) {
		this.riskScoreObject = riskScoreObject;
	}


	public Date getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	public String getCount() {
		return count;
	}


	public void setCount(String count) {
		this.count = count;
	}


	
	
}