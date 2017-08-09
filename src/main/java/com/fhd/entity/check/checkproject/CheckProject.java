package com.fhd.entity.check.checkproject;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

@Entity
@Table(name = "t_rm_check_rule_project")
public class CheckProject extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5514817024874549201L;
	/**
	 * 
	 */
	@Column(name = "NAME")
	private String name;
	@Column(name = "TOTAL_SCORE")
	private String totalScore;
	@Column(name = "PROJECT_ORDER")
	private String projectOrder;
	@Column(name = "IS_USERD")
	private String isUserd;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(String totalScore) {
		this.totalScore = totalScore;
	}

	public String getProjectOrder() {
		return projectOrder;
	}

	public void setProjectOrder(String projectOrder) {
		this.projectOrder = projectOrder;
	}

	public String getIsUserd() {
		return isUserd;
	}

	public void setIsUserd(String isUserd) {
		this.isUserd = isUserd;
	}



}
