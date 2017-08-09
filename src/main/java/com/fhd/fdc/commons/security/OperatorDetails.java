/**
 * OperatorDetails.java
 * com.fhd.fdc.commons.security
 *
 * Function : ENDO
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2010-11-30 		胡迪新
 *
 * Copyright (c) 2010, Firsthuida All Rights Reserved.
*/

package com.fhd.fdc.commons.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.orgstructure.SysEmployee;

/**
 * ClassName:OperatorDetails
 * Function: ENDO ADD FUNCTION
 * Reason:	 ENDO ADD REASON
 *
 * @author   胡迪新
 * @version  
 * @since    Ver 1.1
 * @Date	 2010-11-30		上午10:11:01
 *
 * @see 	 
 */
public class OperatorDetails extends User implements Serializable {

	/**
	 * serialVersionUID:ENDO
	 *
	 * @author 胡迪新
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = 2859758677160232193L;
	
	public OperatorDetails(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, Collection<GrantedAuthority> authorities) {
		
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
	}
	
	// 用户ID
	private String userid;
	// 用户真实名称
	private String realname;
	// 所在公司ID
	private String companyid;
	// 所在公司名称
	private String companyName;
	// 员工
	private SysEmployee emp;
	// 员工ID
	private String empid;
	//员工编号
	private String empNo;
	//员工姓名
	private String empName;
	// 主责部门名称
	private String majorDeptName;
	// 主责部门id
	private String majorDeptId;
	//主责部门编号
	private String majorDeptNo;
	// 用户所有角色
	private Set<SysRole> sysRoles;
	// 部门经理
	private String divisionManagerId;
	// ip地址
	private String remoteIp;
	
	public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getMajorDeptNo() {
        return majorDeptNo;
    }

    public void setMajorDeptNo(String majorDeptNo) {
        this.majorDeptNo = majorDeptNo;
    }

    public Set<SysRole> getSysRoles() {
		return sysRoles;
	}

	public void setSysRoles(Set<SysRole> sysRoles) {
		this.sysRoles = sysRoles;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCompanyid() {
		return companyid;
	}

	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getMajorDeptName() {
		return majorDeptName;
	}

	public void setMajorDeptName(String majorDeptName) {
		this.majorDeptName = majorDeptName;
	}

	public String getMajorDeptId() {
		return majorDeptId;
	}

	public void setMajorDeptId(String majorDeptId) {
		this.majorDeptId = majorDeptId;
	}

	public SysEmployee getEmp() {
		return emp;
	}

	public void setEmp(SysEmployee emp) {
		this.emp = emp;
	}

	public String getDivisionManagerId() {
		return divisionManagerId;
	}

	public void setDivisionManagerId(String divisionManagerId) {
		this.divisionManagerId = divisionManagerId;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}
}