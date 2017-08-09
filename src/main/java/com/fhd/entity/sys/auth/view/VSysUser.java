package com.fhd.entity.sys.auth.view;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Entity
@Subselect(
	"SELECT" +
	"	T_SYS_USER.*," +
	"	T_SYS_EMPLOYEE.ID EMP_ID," +
	"	T_SYS_EMPLOYEE.ORG_ID COMPANY_ID,T_SYS_EMPLOYEE.DELETE_STATUS	" +
	"FROM" +
	"	T_SYS_USER T_SYS_USER" +
	"	LEFT JOIN T_SYS_EMPLOYEE T_SYS_EMPLOYEE ON T_SYS_EMPLOYEE.USER_ID=T_SYS_USER.ID"
)
@Synchronize({"T_SYS_USER","T_SYS_EMPLOYEE"})
public class VSysUser extends IdEntity implements Serializable{

	private static final long serialVersionUID = 804474197076595952L;
	/**
	 * 用户名.
	 */
	@SearchableProperty(name = "code")
	@Column(name = "USER_NAME", length = 30)
	private String username;
	/**
	 * 密码.
	 */
	@SearchableProperty
	@Column(name = "PASSWORD")
	private String password;
	/**
	 * 真实姓名.
	 */
	@SearchableProperty(name = "name")
	@Column(name = "REAL_NAME", length = 30)
	private String realname;
	/**
	 * 用户状态.
	 */
	@Column(name = "ESTATUS")
	private String userStatus;
	/**
	 * 最后登录时间.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_LOGIN_TIME")
	private Date lastLoginTime;
	/**
	 * 错误统计.
	 */
	@Column(name = "ERR_COUNT")
	private Integer errCount;
	/**
	 * 锁定状态.
	 */
	@Column(name = "LOCK_STATE")
	private Boolean lockstate;
	/**
	 * 是否启用.
	 */
	@Column(name = "IS_ENABLE")
	private Boolean enable;
	/**
	 * 失效日期.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "EXPIRY_DATE", length = 7)
	private Date expiryDate;
	/**
	 * 密码过期日期.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "CREDENTIALS_EXPIRY_DATE", length = 7)
	private Date credentialsexpiryDate;
	/**
	 * 注册日期.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "REG_DATE", length = 7)
	private Date regdate;
	/**
	 * MAC地址
	 */
	@Column(name = "MAC")
	private String mac;
	
	/**
	 * 对应员工ID
	 */
	@Column(name = "EMP_ID")
	private String empId;
	
	/**
	 * 对应员工
	 */
	@JoinColumn(name = "EMP_ID",insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.LAZY)
	private SysEmployee sysEmployee;
	
	/**
	 * 对应员工ID
	 */
	@Column(name = "COMPANY_ID")
	private String companyId;
	
	/**
	 * 逻辑删除状态
	 */
	@Column(name = "DELETE_STATUS")
	private String deleteStatus;
	
	public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	/**
	 * 所属公司.
	 */
	@JoinColumn(name = "COMPANY_ID",insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private SysOrganization company;
	
	/**
	 * 角色(多对多关系维护).
	 */
	@OrderBy("sort ASC")
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_USER_ROLE", joinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) })
	private Set<SysRole> sysRoles = new HashSet<SysRole>(0);
	/**
	 * 权限(多对多关系维护).
	 */
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_USER_AUTHORITY", joinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "AUTH_ID", nullable = false, updatable = false) })
	private Set<SysAuthority> sysAuthorities = new HashSet<SysAuthority>(0);
	
	public VSysUser() {
		super();
	}

	public VSysUser(String username, String password, String realname,
			String userStatus, Date lastLoginTime, Integer errCount,
			Boolean lockstate, Boolean enable, Date expiryDate,
			Date credentialsexpiryDate, Date regdate, String mac, String empId,
			SysEmployee sysEmployee, String companyId, SysOrganization company,
			Set<SysRole> sysRoles, Set<SysAuthority> sysAuthorities) {
		super();
		this.username = username;
		this.password = password;
		this.realname = realname;
		this.userStatus = userStatus;
		this.lastLoginTime = lastLoginTime;
		this.errCount = errCount;
		this.lockstate = lockstate;
		this.enable = enable;
		this.expiryDate = expiryDate;
		this.credentialsexpiryDate = credentialsexpiryDate;
		this.regdate = regdate;
		this.mac = mac;
		this.empId = empId;
		this.sysEmployee = sysEmployee;
		this.companyId = companyId;
		this.company = company;
		this.sysRoles = sysRoles;
		this.sysAuthorities = sysAuthorities;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Integer getErrCount() {
		return errCount;
	}
	public void setErrCount(Integer errCount) {
		this.errCount = errCount;
	}
	public Boolean getLockstate() {
		return lockstate;
	}
	public void setLockstate(Boolean lockstate) {
		this.lockstate = lockstate;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public Date getCredentialsexpiryDate() {
		return credentialsexpiryDate;
	}
	public void setCredentialsexpiryDate(Date credentialsexpiryDate) {
		this.credentialsexpiryDate = credentialsexpiryDate;
	}
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public SysEmployee getSysEmployee() {
		return sysEmployee;
	}
	public void setSysEmployee(SysEmployee sysEmployee) {
		this.sysEmployee = sysEmployee;
	}
	public Set<SysRole> getSysRoles() {
		return sysRoles;
	}
	public void setSysRoles(Set<SysRole> sysRoles) {
		this.sysRoles = sysRoles;
	}
	public Set<SysAuthority> getSysAuthorities() {
		return sysAuthorities;
	}
	public void setSysAuthorities(Set<SysAuthority> sysAuthorities) {
		this.sysAuthorities = sysAuthorities;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}
}