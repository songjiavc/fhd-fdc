package com.fhd.entity.check.checkdetail;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.check.checkcomment.CheckComment;

@Entity
@Table(name="t_rm_check_rule_detail")
public class CheckDetail extends IdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DETAIL_STANDARD")
	private String detailStandard;
	
	@Column(name="DETAIL_SCORE")
	private int detailScore;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="COMMENT_ID")
	private CheckComment checkComment;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetailStandard() {
		return detailStandard;
	}

	public void setDetailStandard(String detailStandard) {
		this.detailStandard = detailStandard;
	}

	public int getDetailScore() {
		return detailScore;
	}

	public void setDetailScore(int detailScore) {
		this.detailScore = detailScore;
	}

	public CheckComment getCheckComment() {
		return checkComment;
	}

	public void setCheckComment(CheckComment checkComment) {
		this.checkComment = checkComment;
	}

	
}
