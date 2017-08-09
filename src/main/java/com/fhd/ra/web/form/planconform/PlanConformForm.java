package com.fhd.ra.web.form.planconform;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;

public class PlanConformForm extends RiskAssessPlan{
	private static final long serialVersionUID = 1L;
	
	private String beginDataStr;//开始时间
	private String endDataStr;//结束时间
	private String contactName;//联系人姓名
	private String responsName;//负责人姓名
	private String templateId;//模板
	
	public PlanConformForm(){
		
	}
	
	public PlanConformForm(RiskAssessPlan riskPlan){
		this.setId(riskPlan.getId());
		this.setPlanName(riskPlan.getPlanName());
		this.setPlanCode(riskPlan.getPlanCode());
		this.setCollectRate(riskPlan.getCollectRate());
		this.setRangeReq(riskPlan.getRangeReq());
		if(null != riskPlan.getWorkTage()){
			this.setWorkTage(riskPlan.getWorkTage());
		}else{
			this.setWorkTage("");
		}
		if(null != riskPlan.getBeginDate()){
			this.setBeginDataStr(riskPlan.getBeginDate().toString().split(" ")[0]);//开始时间
		}else{
			this.setBeginDataStr("");
		}
		if(null != riskPlan.getEndDate()){
			this.setEndDataStr(riskPlan.getEndDate().toString().split(" ")[0]);//结束时间
		}else{
			this.setEndDataStr("");
		}
		this.setWorkType(riskPlan.getWorkType());
		this.contactName = null == riskPlan.getContactPerson()?"":riskPlan.getContactPerson().getEmpname();
		this.responsName = null == riskPlan.getResponsPerson()?"":riskPlan.getResponsPerson().getEmpname();
		this.setStatus(riskPlan.getStatus());//状态
		this.setDealStatus(riskPlan.getDealStatus());//处理状态
		//计划类型
		this.setPlanType(riskPlan.getPlanType());
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

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}
