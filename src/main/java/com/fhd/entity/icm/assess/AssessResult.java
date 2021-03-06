package com.fhd.entity.icm.assess;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.sys.dic.DictEntry;

/**
 * 评价结果
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-12-26		下午3:46:39
 *
 * @see 	 
 */
@Entity
@Table(name="T_CA_ASSESSMENT_RESULT")
public class AssessResult extends IdEntity implements Serializable {

	private static final long serialVersionUID = -7439293033451628098L;
			
	/**
	 * 评价参与人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESSOR_ID")
	private Assessor assessor;
	
	/**
	 * 评价计划
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	private AssessPlan assessPlan;
	
	/**
	 * 流程
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROCESSURE_ID")
	private Process process;
	
	/**
	 * 流程节点
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTROL_POINT_ID")
	private ProcessPoint processPoint;
	
	/**
	 * 控制措施
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEASURE_ID")
	private Measure controlMeasure;
	
	/**
	 * 评价点
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POINT_ID")
	private AssessPoint assessPoint;
	
	/**
	 * 评价方式:穿行测试,抽样测试
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESSMENT_MEASURE")
	private DictEntry assessMeasure;		
	
	/**
	 * 要穿行的次数
	 */
	@Column(name="TO_EXTRACT_AMOUNT")
	private Integer toExtractAmount;
			
	/**
	 * 本次计划已抽取的样本数量
	 */
	@Column(name="EXTRACTED_AMOUNT")
	private Integer extractedAmount;
	
	/**
	 * 有效样本数
	 */
	@Column(name="EFFECTIVE_NUMBER")
	private Integer effectiveNumber;
	
	/**
	 * 无效样本数
	 */
	@Column(name="DEFECT_NUMBER")
	private Integer defectNumber;
		
	/**
	 * 自动机算：是否合格
	 */
	@Column(name="HAS_DEFECT")
	private Boolean hasDefect;
	
	/**
	 * 手动调整：是否合格
	 */
	@Column(name="HAS_DEFECT_ADJUST")
	private Boolean hasDefectAdjust;
	
	/**
	 * 调整说明
	 */
	@Column(name="ADJUST_DESC")
	private String adjustDesc;
	
	/**
	 * 缺陷描述
	 */
	@Column(name="ECOMMENT")
	private String comment;
	
	/**
	 * 影响程度
	 */
	@Column(name = "IMPACT")
	private Double impact;
	
	/**
	 * 发生可能性
	 */
	@Column(name = "PROBABILITY")
	private Double probability;
	
	/**
	 * 评价结果样本集合.
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "assessResult")
	Set<AssessSample> assessSamples = new HashSet<AssessSample>(0);
			
	public AssessResult(){
	}
	
	public AssessResult(String id){
		super.setId(id);
	}
	
	public AssessResult(Assessor assessor, AssessPlan assessPlan,
			Process process, ProcessPoint processPoint, Measure controlMeasure,
			AssessPoint assessPoint, DictEntry assessMeasure,
			Integer toExtractAmount, Integer extractedAmount,
			Integer effectiveNumber, Integer defectNumber, Boolean hasDefect,
			Boolean hasDefectAdjust, String adjustDesc, String comment,
			Double impact, Double probability, Set<AssessSample> assessSamples) {
		super();
		this.assessor = assessor;
		this.assessPlan = assessPlan;
		this.process = process;
		this.processPoint = processPoint;
		this.controlMeasure = controlMeasure;
		this.assessPoint = assessPoint;
		this.assessMeasure = assessMeasure;
		this.toExtractAmount = toExtractAmount;
		this.extractedAmount = extractedAmount;
		this.effectiveNumber = effectiveNumber;
		this.defectNumber = defectNumber;
		this.hasDefect = hasDefect;
		this.hasDefectAdjust = hasDefectAdjust;
		this.adjustDesc = adjustDesc;
		this.comment = comment;
		this.impact = impact;
		this.probability = probability;
		this.assessSamples = assessSamples;
	}

	public Assessor getAssessor() {
		return assessor;
	}

	public void setAssessor(Assessor assessor) {
		this.assessor = assessor;
	}

	public AssessPlan getAssessPlan() {
		return assessPlan;
	}

	public void setAssessPlan(AssessPlan assessPlan) {
		this.assessPlan = assessPlan;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public ProcessPoint getProcessPoint() {
		return processPoint;
	}

	public void setProcessPoint(ProcessPoint processPoint) {
		this.processPoint = processPoint;
	}

	public Measure getControlMeasure() {
		return controlMeasure;
	}

	public void setControlMeasure(Measure controlMeasure) {
		this.controlMeasure = controlMeasure;
	}

	public AssessPoint getAssessPoint() {
		return assessPoint;
	}

	public void setAssessPoint(AssessPoint assessPoint) {
		this.assessPoint = assessPoint;
	}

	public DictEntry getAssessMeasure() {
		return assessMeasure;
	}

	public void setAssessMeasure(DictEntry assessMeasure) {
		this.assessMeasure = assessMeasure;
	}

	public Integer getToExtractAmount() {
		return toExtractAmount;
	}

	public void setToExtractAmount(Integer toExtractAmount) {
		this.toExtractAmount = toExtractAmount;
	}

	public Integer getExtractedAmount() {
		return extractedAmount;
	}

	public void setExtractedAmount(Integer extractedAmount) {
		this.extractedAmount = extractedAmount;
	}

	public Integer getEffectiveNumber() {
		return effectiveNumber;
	}

	public void setEffectiveNumber(Integer effectiveNumber) {
		this.effectiveNumber = effectiveNumber;
	}

	public Integer getDefectNumber() {
		return defectNumber;
	}

	public void setDefectNumber(Integer defectNumber) {
		this.defectNumber = defectNumber;
	}

	public Boolean getHasDefect() {
		return hasDefect;
	}

	public void setHasDefect(Boolean hasDefect) {
		this.hasDefect = hasDefect;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getHasDefectAdjust() {
		return hasDefectAdjust;
	}

	public void setHasDefectAdjust(Boolean hasDefectAdjust) {
		this.hasDefectAdjust = hasDefectAdjust;
	}

	public String getAdjustDesc() {
		return adjustDesc;
	}

	public void setAdjustDesc(String adjustDesc) {
		this.adjustDesc = adjustDesc;
	}

	public Set<AssessSample> getAssessSamples() {
		return assessSamples;
	}

	public void setAssessSamples(Set<AssessSample> assessSamples) {
		this.assessSamples = assessSamples;
	}

	public Double getImpact() {
		return impact;
	}

	public void setImpact(Double impact) {
		this.impact = impact;
	}

	public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}
}