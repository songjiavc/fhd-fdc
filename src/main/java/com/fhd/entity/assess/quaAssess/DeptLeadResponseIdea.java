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
@Table(name = "t_rm_dept_lead_edit_response") 
public class DeptLeadResponseIdea extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/**领导意见打分对象关联ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPT_LEAD_CIRCUSEE_ID")
	private DeptLeadCircusee deptLeadCircusee;
	
	/**意见内容*/
	@Column(name = "EDIT_IDEA_CONTENT")
	private String editIdeaContent;

	public DeptLeadCircusee getDeptLeadCircusee() {
		return deptLeadCircusee;
	}

	public void setDeptLeadCircusee(DeptLeadCircusee deptLeadCircusee) {
		this.deptLeadCircusee = deptLeadCircusee;
	}

	public String getEditIdeaContent() {
		return editIdeaContent;
	}

	public void setEditIdeaContent(String editIdeaContent) {
		this.editIdeaContent = editIdeaContent;
	}	
}