package com.fhd.entity.sys.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.group.SysGroup;
import com.fhd.entity.sys.orgstructure.SysPosition;

/**
 * SysRole entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "T_SYS_ROLE")
public class SysRole extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 9080021866738613391L;
	/**
	 * 角色编号.
	 */
	@Column(name = "ROLE_CODE", length = 20)
	private String roleCode;
	/**
	 * 角色名称.
	 */
	@Column(name = "ROLE_NAME", length = 30)
	private String roleName;
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;
	/**
	 * 用户(多对多关系维护).
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_USER_ROLE", joinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) })
	private Set<SysUser> sysUsers = new HashSet<SysUser>(0);
	/**
	 * 岗位(多对多关系维护).
	 */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "sysRoles")
	
	private Set<SysPosition> sysPositions = new HashSet<SysPosition>(0);
	/**
	 * 权限(多对多关系维护).
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_ROLE_AUTHORITY", joinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "AUTH_ID", nullable = false, updatable = false) })
	private Set<SysAuthority> sysAuthorities = new HashSet<SysAuthority>(0);
	/**
	 * 工作组(多对多关系维护).
	 */
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sysRoles")
	private Set<SysGroup> sysGroups = new HashSet<SysGroup>(0);
	
	/**
	 * 登录信息(多对多关系维护).
	 */
	@OrderBy("sort ASC")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_ROLE_INDEX_INFO", joinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "INDEX_INFO_ID", nullable = false, updatable = false) })
	private List<IndexInfo> indexInfoList = new ArrayList<IndexInfo>(0);

	// Constructors

	/** default constructor */
	public SysRole() {
	}

	/** full constructor */
	public SysRole(String roleCode, String roleName) {
		this.roleCode = roleCode;
		this.roleName = roleName;
	}
	
	public SysRole(String roleCode, String roleName, Integer sort,
			Set<SysUser> sysUsers, Set<SysPosition> sysPositions,
			Set<SysAuthority> sysAuthorities, Set<SysGroup> sysGroups,
			List<IndexInfo> indexInfoList) {
		super();
		this.roleCode = roleCode;
		this.roleName = roleName;
		this.sort = sort;
		this.sysUsers = sysUsers;
		this.sysPositions = sysPositions;
		this.sysAuthorities = sysAuthorities;
		this.sysGroups = sysGroups;
		this.indexInfoList = indexInfoList;
	}

	// Property accessors

	public String getRoleCode() {
		return this.roleCode;
	}

	public Set<SysGroup> getSysGroups() {
		return sysGroups;
	}

	public void setSysGroups(Set<SysGroup> sysGroups) {
		this.sysGroups = sysGroups;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<SysUser> getSysUsers() {
		return this.sysUsers;
	}

	public void setSysUsers(Set<SysUser> sysUsers) {
		this.sysUsers = sysUsers;
	}

	public Set<SysAuthority> getSysAuthorities() {
		return this.sysAuthorities;
	}

	public void setSysAuthorities(Set<SysAuthority> sysAuthorities) {
		this.sysAuthorities = sysAuthorities;
	}

	public Set<SysPosition> getSysPositions() {
		return sysPositions;
	}

	public void setSysPositions(Set<SysPosition> sysPositions) {
		this.sysPositions = sysPositions;
	}

	public List<IndexInfo> getIndexInfoList() {
		return indexInfoList;
	}

	public void setIndexInfoList(List<IndexInfo> indexInfoList) {
		this.indexInfoList = indexInfoList;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

}