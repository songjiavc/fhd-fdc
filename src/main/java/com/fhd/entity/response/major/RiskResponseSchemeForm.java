package com.fhd.entity.response.major;

public class RiskResponseSchemeForm extends RiskResponseScheme{

	private static final long serialVersionUID = 8193994198100015719L;

	private String startTimeStr;
	private String finishTimeStr;
	public String getStartTimeStr() {
		return startTimeStr;
	}
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}
	public String getFinishTimeStr() {
		return finishTimeStr;
	}
	public void setFinishTimeStr(String finishTimeStr) {
		this.finishTimeStr = finishTimeStr;
	}
}
