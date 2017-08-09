package com.fhd.entity.check.checkcomment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.check.checkproject.CheckProject;

@Entity
@Table(name = "t_rm_check_rule_comment")
public class CheckComment extends IdEntity implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1321988989551924919L;

	@Column(name = "NAME")
	private String name;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "PROJECT_ID")
	private CheckProject project;
	
	@Column(name = "COMMENT_ORDER")
	private String commentOrder;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CheckProject getProject() {
		return project;
	}
	@JsonManagedReference
	public void setProject(CheckProject project) {
		this.project = project;
	}
	public String getCommentOrder() {
		return commentOrder;
	}
	public void setCommentOrder(String commentOrder) {
		this.commentOrder = commentOrder;
	}





}
