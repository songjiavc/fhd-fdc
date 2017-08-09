package com.fhd.entity.assess.quaAssess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.base.IdEntity;

/**
 * 综合打分意见
 */

@Entity
@Table(name = "t_rm_edit_response") 
public class ResponseIdea  extends IdEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	/**综合ID*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBJECT_DEPT_EMP_ID")
	private RangObjectDeptEmp objectDeptEmpId;
	
	/**意见内容*/
	@Column(name = "EDIT_IDEA_CONTENT")
	private String editIdeaContent;

	public RangObjectDeptEmp getObjectDeptEmpId() {
		return objectDeptEmpId;
	}

	public void setObjectDeptEmpId(RangObjectDeptEmp objectDeptEmpId) {
		this.objectDeptEmpId = objectDeptEmpId;
	}

	public String getEditIdeaContent() {
		return editIdeaContent;
	}

	public void setEditIdeaContent(String editIdeaContent) {
		this.editIdeaContent = editIdeaContent;
	}


}