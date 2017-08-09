package com.fhd.entity.response;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 控制措施
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-9-17		下午2:23:09
 *
 * @see 	 
 */
@Entity
@Table(name="T_RM_SOLUTION_RELA_ORG")
public class SolutionRelaOrg extends IdEntity implements Serializable {
	
	private static final long serialVersionUID = -7714989398654347108L;

	/**
	 * 应对措施
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOLUTION_ID")
	private Solution solution;
	
	/**
	 * 部门
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ORG_ID")
	private SysOrganization org;
	/**
	 * 员工
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EMP_ID")
	private SysEmployee emp;
	/**
	 * 类型：责任部门，配合部门，责任人
	 */
	@Column(name="ETYPE")
	private String type;

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	public SysOrganization getOrg() {
		return org;
	}

	public void setOrg(SysOrganization org) {
		this.org = org;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SysEmployee getEmp() {
		return emp;
	}

	public void setEmp(SysEmployee emp) {
		this.emp = emp;
	}
	
	
}

