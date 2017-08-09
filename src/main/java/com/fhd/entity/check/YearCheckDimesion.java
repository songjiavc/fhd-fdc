package com.fhd.entity.check;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.check.checkdetail.CheckDetail;

@Entity
@Table(name="t_rm_check_rule_dimension")
public class YearCheckDimesion extends IdEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DETAIL_ID")
	private CheckDetail checkDetail;
	
	@Column(name="DALAY_DAYS")
	private int dalayDate;
	
	@Column(name="SUB_SCORE")
	private int subScore;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLAN_TYPE")
	private YearCheckPlanType playType;

	public CheckDetail getCheckDetail() {
		return checkDetail;
	}

	public void setCheckDetail(CheckDetail checkDetail) {
		this.checkDetail = checkDetail;
	}

	public int getDalayDate() {
		return dalayDate;
	}

	public void setDalayDate(int dalayDate) {
		this.dalayDate = dalayDate;
	}

	public int getSubScore() {
		return subScore;
	}

	public void setSubScore(int subScore) {
		this.subScore = subScore;
	}

	public YearCheckPlanType getPlayType() {
		return playType;
	}

	public void setPlayType(YearCheckPlanType playType) {
		this.playType = playType;
	}
	
	

}
