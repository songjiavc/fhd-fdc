package com.fhd.entity.check.yearcheck.plan;

public class YearCheckPlanOrgForm extends YearCheckPlanOrg {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String empName;
private String orgName;
private String id;
public String getEmpName() {
	return empName;
}
public void setEmpName(String empName) {
	this.empName = empName;
}
public String getOrgName() {
	return orgName;
}
public void setOrgName(String orgName) {
	this.orgName = orgName;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public YearCheckPlanOrgForm() {
	super();
}
public YearCheckPlanOrgForm(YearCheckPlanOrg checkPlanOrg) {
	super();
	this.empName = checkPlanOrg.getEmpId().getEmpname();
	this.orgName = checkPlanOrg.getOrgId().getOrgname();
	this.id = checkPlanOrg.getId();
}


}
