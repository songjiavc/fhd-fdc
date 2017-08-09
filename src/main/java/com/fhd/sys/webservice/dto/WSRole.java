package com.fhd.sys.webservice.dto;
public class WSRole {
	private String code;
	private String name;
	private String[] authorityCodes;
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
	public String[] getAuthorityCodes() {
		return authorityCodes;
	}
	public void setAuthorityCodes(String[] authorityCodes) {
		this.authorityCodes = authorityCodes;
	}
	public String getEditType() {
		return editType;
	}
	public void setEditType(String editType) {
		this.editType = editType;
	}
}
