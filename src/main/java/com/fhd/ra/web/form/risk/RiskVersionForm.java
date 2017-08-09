package com.fhd.ra.web.form.risk;

import com.fhd.entity.risk.history.NewRiskVersion;

public class RiskVersionForm extends NewRiskVersion{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String versionName;
	private String desc;
	
	public RiskVersionForm(){
		
	}
	
	public RiskVersionForm(NewRiskVersion riskVersion){
		this.setId(riskVersion.getId());
		this.setVersionName(riskVersion.getVersionName());
		this.setDesc(riskVersion.getDesc());
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
