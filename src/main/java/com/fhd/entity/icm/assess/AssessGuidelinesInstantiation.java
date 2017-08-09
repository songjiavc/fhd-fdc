package com.fhd.entity.icm.assess;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;


/**
 * 评价计划与评价模板关联表.
 * @author   吴德福
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-7-8		上午11:21:12
 * @see 	 
 */
@Entity
@Table(name="T_CA_GUIDELINES_INSTANTIATION")
public class AssessGuidelinesInstantiation extends IdEntity implements Serializable {

	private static final long serialVersionUID = -1782443889589722258L;

	/**
	 * 评价计划
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	private AssessPlan assessPlan;
	/**
	 * 评价标准模板
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GUIDELINES_ID")
	private AssessGuidelines assessGuidelines;
	
	public AssessGuidelinesInstantiation() {
	}

	public AssessGuidelinesInstantiation(AssessPlan assessPlan,
			AssessGuidelines assessGuidelines) {
		super();
		this.assessPlan = assessPlan;
		this.assessGuidelines = assessGuidelines;
	}

	public AssessPlan getAssessPlan() {
		return assessPlan;
	}

	public void setAssessPlan(AssessPlan assessPlan) {
		this.assessPlan = assessPlan;
	}

	public AssessGuidelines getAssessGuidelines() {
		return assessGuidelines;
	}

	public void setAssessGuidelines(AssessGuidelines assessGuidelines) {
		this.assessGuidelines = assessGuidelines;
	}
}