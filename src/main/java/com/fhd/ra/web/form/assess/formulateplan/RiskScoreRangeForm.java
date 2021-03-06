package com.fhd.ra.web.form.assess.formulateplan;

import com.fhd.entity.assess.formulatePlan.RiskScoreRange;
import com.fhd.entity.sys.orgstructure.SysOrganization;

public class RiskScoreRangeForm extends RiskScoreRange{
	private static final long serialVersionUID = 1L;
	
	private String deptName;//部门名称

	public RiskScoreRangeForm(){
		
	}
	
	public RiskScoreRangeForm(RiskScoreRange scoreRange, SysOrganization org){
		this.setId(scoreRange.getId());
		this.setRangeType(scoreRange.getRangeType());
		this.setDeptName(org.getOrgname());
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	
	
}
