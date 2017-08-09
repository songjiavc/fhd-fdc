package com.fhd.ra.web.form.risk;

/**
 * 风险综合查询表单类
 * @author wzr
 *
 */
public class RiskCompreQueryForm {

	private String riskCode;
	private String riskName;
	private String riskDesc;
	private String parentId;
	private String parentName;
	private String mainDeptId;
	private String mainDeptName;
	private String relaDeptId;
	private String relaDeptName;
	private String schm;
	private String schmDept;
	private String riskVersion;
	
	public RiskCompreQueryForm(){
		
	}

	public String getRiskCode() {
		return riskCode;
	}

	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}

	public String getRiskName() {
		return riskName;
	}

	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}

	public String getRiskDesc() {
		return riskDesc;
	}

	public void setRiskDesc(String riskDesc) {
		this.riskDesc = riskDesc;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getMainDeptId() {
		return mainDeptId;
	}

	public void setMainDeptId(String mainDeptId) {
		this.mainDeptId = mainDeptId;
	}

	public String getMainDeptName() {
		return mainDeptName;
	}

	public void setMainDeptName(String mainDeptName) {
		this.mainDeptName = mainDeptName;
	}

	public String getRelaDeptId() {
		return relaDeptId;
	}

	public void setRelaDeptId(String relaDeptId) {
		this.relaDeptId = relaDeptId;
	}

	public String getRelaDeptName() {
		return relaDeptName;
	}

	public void setRelaDeptName(String relaDeptName) {
		this.relaDeptName = relaDeptName;
	}

	public String getSchm() {
		return schm;
	}

	public void setSchm(String schm) {
		this.schm = schm;
	}

	public String getSchmDept() {
		return schmDept;
	}

	public void setSchmDept(String schmDept) {
		this.schmDept = schmDept;
	}

	public String getRiskVersion() {
		return riskVersion;
	}

	public void setRiskVersion(String riskVersion) {
		this.riskVersion = riskVersion;
	}
	
	
}
