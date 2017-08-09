package com.fhd.entity.risk;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 人员与风险修改意见关联
 * */

@Entity
@Table(name = "t_rm_emp_rela_risk_idea ") 
public class EmpRelaRiskIdea extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
     * 风险
     */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RISK_ID")
    private Risk risk;
    
    /**
     * 人员
     */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private SysEmployee emp;

	/**
     * 部门
     */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    private SysOrganization org;
	
	public Risk getRisk() {
		return risk;
	}
	
	public void setRisk(Risk risk) {
		this.risk = risk;
	}

	public SysEmployee getEmp() {
		return emp;
	}

	public void setEmp(SysEmployee emp) {
		this.emp = emp;
	}

	public SysOrganization getOrg() {
		return org;
	}

	public void setOrg(SysOrganization org) {
		this.org = org;
	}
}