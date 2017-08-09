package com.fhd.entity.assess.quaAssess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 部门领导意见
 * */

@Entity
@Table(name = "t_rm_last_lead_edit_response")
public class LastLeadResponseIdea extends IdEntity implements java.io.Serializable {

	/**  
	  * @Description: ridy insert riskresponse text
	  * @author jia.song@pcitc.com
	  * @date 2017年5月24日 下午4:11:41 
	  */
	private static final long serialVersionUID = 1L;

	@Column(name = "RISK_ID")
	private String riskId;
	
	@Column(name = "SCORE_OBJECT_ID")
	private String scoreObjectId;
	
	/**意见内容*/
	@Column(name = "EDIT_IDEA_CONTENT")
	private String editIdeaContent;

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getScoreObjectId() {
		return scoreObjectId;
	}

	public void setScoreObjectId(String scoreObjectId) {
		this.scoreObjectId = scoreObjectId;
	}

	public String getEditIdeaContent() {
		return editIdeaContent;
	}

	public void setEditIdeaContent(String editIdeaContent) {
		this.editIdeaContent = editIdeaContent;
	}
	
}