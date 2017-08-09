package com.fhd.entity.kpi;

public class RelaAssessResultDTO {
	/**
	 * ID
	 */
	private String id;
	/**
	 * 对象id
	 */
	private String objectId;
	/**
	 * 对象名称
	 */
	private String objectName;
	/**
	 * 对象名称
	 */
	private String objectCode;

	/**
	 * 评估值
	 */
	private Double assessmentValue;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectCode() {
		return objectCode;
	}

	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}

	public Double getAssessmentValue() {
		return assessmentValue;
	}

	public void setAssessmentValue(Double assessmentValue) {
		this.assessmentValue = assessmentValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RelaAssessResultDTO() {
		super();
	}

}
