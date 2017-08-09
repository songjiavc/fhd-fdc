package com.fhd.ra.web.controller.response.major.utils;

import java.io.Serializable;

public class Tasker implements Serializable{

	private static final long serialVersionUID = 8413716153714075403L;
	private String businessId;
	private String name;
	//部门普通员工
	private String deptCommonEmp;
	//部门风险管理员子流程专用
	private String deptRiskManagerForSub;
	//部门风险管理员主流程专用
	private String deptRiskManager;
	
	private String riskId;
	private String deptId;
	private String deptType;
	
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDeptCommonEmp() {
		return deptCommonEmp;
	}
	public void setDeptCommonEmp(String deptCommonEmp) {
		this.deptCommonEmp = deptCommonEmp;
	}
	public String getDeptRiskManagerForSub() {
		return deptRiskManagerForSub;
	}
	public void setDeptRiskManagerForSub(String deptRiskManagerForSub) {
		this.deptRiskManagerForSub = deptRiskManagerForSub;
	}
	public String getDeptRiskManager() {
		return deptRiskManager;
	}
	public void setDeptRiskManager(String deptRiskManager) {
		this.deptRiskManager = deptRiskManager;
	}
	public String getRiskId() {
		return riskId;
	}
	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptType() {
		return deptType;
	}
	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}
	
}
