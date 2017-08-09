package com.fhd.ra.web.controller.response.major.utils;

import java.io.Serializable;

public class ApproveGridEntity implements Serializable{

	private static final long serialVersionUID = 7250907399459523246L;

	private String id;
	private String planId;
	private String riskId;
	private String riskName;
	private String majorDept;
	private String relaDept;
	private int deptCount;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public String getRiskId() {
		return riskId;
	}
	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}
	public String getRiskName() {
		return riskName;
	}
	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}
	public String getMajorDept() {
		return majorDept;
	}
	public void setMajorDept(String majorDept) {
		this.majorDept = majorDept;
	}
	public String getRelaDept() {
		return relaDept;
	}
	public void setRelaDept(String relaDept) {
		this.relaDept = relaDept;
	}
	public int getDeptCount() {
		return deptCount;
	}
	public void setDeptCount(int deptCount) {
		this.deptCount = deptCount;
	}
}
