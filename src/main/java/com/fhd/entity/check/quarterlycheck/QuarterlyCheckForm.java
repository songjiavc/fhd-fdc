package com.fhd.entity.check.quarterlycheck;

import com.fhd.entity.check.yearcheck.plan.YearCheckPlan;

public class QuarterlyCheckForm extends QuarterlyCheck {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String beginDataStr;//开始时间
	private String endDataStr;//结束时间
	private String contactName;//联系人姓名
	private String responsName;//负责人姓名
	
	
	
	
	public QuarterlyCheckForm() {
		super();
	}
	
	public QuarterlyCheckForm(QuarterlyCheck quarterlyCheck) {
		this.setId(quarterlyCheck.getId());
		this.setName(quarterlyCheck.getName());
		this.setPlanCode(quarterlyCheck.getPlanCode());
		if(null != quarterlyCheck.getCheckContent()){
			this.setCheckContent(quarterlyCheck.getCheckContent());
		}else{
			this.setCheckContent("");
		}
		if(null != quarterlyCheck.getBeginDate()){
			this.setBeginDataStr(quarterlyCheck.getBeginDate().toString().split(" ")[0]);//开始时间
		}else{
			this.setBeginDataStr("");
		}
		if(null != quarterlyCheck.getEndDate()){
			this.setEndDataStr(quarterlyCheck.getEndDate().toString().split(" ")[0]);//结束时间
		}else{
			this.setEndDataStr("");
		}
		this.setRangeRequire(quarterlyCheck.getRangeRequire());
		this.contactName = null == quarterlyCheck.getContactPerson()?"":quarterlyCheck.getContactPerson().getEmpname();
		this.responsName = null == quarterlyCheck.getResponsiblePerson()?"":quarterlyCheck.getResponsiblePerson().getEmpname();
		this.setStatus(quarterlyCheck.getStatus());//状态
		this.setDealStatus(quarterlyCheck.getDealStatus());//处理状态

	}

	public String getBeginDataStr() {
		return beginDataStr;
	}

	public void setBeginDataStr(String beginDataStr) {
		this.beginDataStr = beginDataStr;
	}

	public String getEndDataStr() {
		return endDataStr;
	}

	public void setEndDataStr(String endDataStr) {
		this.endDataStr = endDataStr;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getResponsName() {
		return responsName;
	}

	public void setResponsName(String responsName) {
		this.responsName = responsName;
	}
	
	
}
