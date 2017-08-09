package com.fhd.entity.check.yearcheck.plan;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;


public class YearCheckPlanForm extends YearCheckPlan {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String beginDataStr;//开始时间
	private String endDataStr;//结束时间
	private String contactName;//联系人姓名
	private String responsName;//负责人姓名
	
	
	
	
	public YearCheckPlanForm() {
		super();
	}
	
	public YearCheckPlanForm(YearCheckPlan yearCheckPlan) {
		this.setId(yearCheckPlan.getId());
		this.setName(yearCheckPlan.getName());
		this.setPlanCode(yearCheckPlan.getPlanCode());
		if(null != yearCheckPlan.getWorkTarg()){
			this.setWorkTarg(yearCheckPlan.getWorkTarg());
		}else{
			this.setWorkTarg("");
		}
		if(null != yearCheckPlan.getBeginDate()){
			this.setBeginDataStr(yearCheckPlan.getBeginDate().toString().split(" ")[0]);//开始时间
		}else{
			this.setBeginDataStr("");
		}
		if(null != yearCheckPlan.getEndDate()){
			this.setEndDataStr(yearCheckPlan.getEndDate().toString().split(" ")[0]);//结束时间
		}else{
			this.setEndDataStr("");
		}
		this.setRangeRequire(yearCheckPlan.getRangeRequire());
		this.contactName = null == yearCheckPlan.getContactPerson()?"":yearCheckPlan.getContactPerson().getEmpname();
		this.responsName = null == yearCheckPlan.getResponsiblePerson()?"":yearCheckPlan.getResponsiblePerson().getEmpname();
		this.setStatus(yearCheckPlan.getStatus());//状态
		this.setDealStatus(yearCheckPlan.getDealStatus());//处理状态

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
