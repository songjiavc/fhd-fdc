package com.fhd.icm.web.form.assess;


public class TempAssessTemplate {
	/**
	 * 计划ID
	 */
	private String planId;
	/**
	 * 评价结果ID
	 */
	private String assessResultId;
	/**
	 * 样本编号
	 */
	private String sampleCode;
	/**
	 * 样本名称
	 */
	private String sampleName;
	/**
	 * 是否合格
	 */
	private String isQualified;
	/**
	 * 说明
	 */
	private String comment;
	
	/**
	 * 上传文档
	 */
	private String fileId;
	
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public String getAssessResultId() {
		return assessResultId;
	}
	public void setAssessResultId(String assessResultId) {
		this.assessResultId = assessResultId;
	}
	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	public String getSampleName() {
		return sampleName;
	}
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	public String getIsQualified() {
		return isQualified;
	}
	public void setIsQualified(String isQualified) {
		this.isQualified = isQualified;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
}
