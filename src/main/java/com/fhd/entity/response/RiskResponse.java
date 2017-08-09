package com.fhd.entity.response;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

@Entity
@Table(name="T_RM_RISK_RESPONSE")
public class RiskResponse extends IdEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 风险id
	 */
	@Column(name = "RISK_ID")
	private String riskId;

	/**
	 * 应对措施
	 */
	@Column(name = "EDIT_IDEA_CONTENT")
	private String editIdeaContent;

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getEditIdeaContent() {
		return editIdeaContent;
	}

	public void setEditIdeaContent(String editIdeaContent) {
		this.editIdeaContent = editIdeaContent;
	}
	
	
}
