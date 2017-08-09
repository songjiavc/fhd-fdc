package com.fhd.entity.response.major;

public class RiskResponseCounterForm  extends RiskResponseCounter {
	private static final long serialVersionUID = 1130362976850551262L;
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
