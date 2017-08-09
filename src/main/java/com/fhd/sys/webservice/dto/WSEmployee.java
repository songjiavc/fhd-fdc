package com.fhd.sys.webservice.dto;
public class WSEmployee {
	private String code;
	private String name;
	private String username;
	private String password;
	private String companyCode;
	private String[] orgCodes;
	private String[] positionCodes;
	private String[] roleCodes;
	private String editType;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String[] getOrgCodes() {
		return orgCodes;
	}
	public void setOrgCodes(String[] orgCodes) {
		this.orgCodes = orgCodes;
	}
	public String[] getPositionCodes() {
		return positionCodes;
	}
	public void setPositionCodes(String[] positionCodes) {
		this.positionCodes = positionCodes;
	}
	public String[] getRoleCodes() {
		return roleCodes;
	}
	public void setRoleCodes(String[] roleCodes) {
		this.roleCodes = roleCodes;
	}
	public String getEditType() {
		return editType;
	}
	public void setEditType(String editType) {
		this.editType = editType;
	}
	
}
