package com.fhd.entity.check;

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
 * 针对于业务的人员机关管理关系表
 * @author 宋佳
 */
@Entity
@Table(name = "T_RM_CHECK_BUSSINESS_ORG_RELA_EMP") 
public class BussinessOrgRelaEmp extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 所属业务id
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSSINESS_ID")
	private Bussiness bussinessId;
	
	/**
	 * 管理部门
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MANAGE_ORG_ID")
	private SysOrganization manageOrgId;
	
	/**
	 * 	被管理部门
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MANAGED_ORG_ID")
	private SysOrganization managedOrgId;
	
	/**
	 * 员工
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	private SysEmployee emp;

	public Bussiness getBussinessId() {
		return bussinessId;
	}

	public void setBussinessId(Bussiness bussinessId) {
		this.bussinessId = bussinessId;
	}

	public SysOrganization getManageOrgId() {
		return manageOrgId;
	}

	public void setManageOrgId(SysOrganization manageOrgId) {
		this.manageOrgId = manageOrgId;
	}

	public SysOrganization getManagedOrgId() {
		return managedOrgId;
	}

	public void setManagedOrgId(SysOrganization managedOrgId) {
		this.managedOrgId = managedOrgId;
	}

	public SysEmployee getEmp() {
		return emp;
	}

	public void setEmp(SysEmployee emp) {
		this.emp = emp;
	}
	
}
