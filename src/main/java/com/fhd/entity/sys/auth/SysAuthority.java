package com.fhd.entity.sys.auth;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.group.SysGroup;
import com.fhd.entity.sys.orgstructure.SysPosition;

/**
 * SysAuthority entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "T_SYS_AUTHORITY")
public class SysAuthority extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 4983441176451017690L;
	/**
	 * 父id.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private SysAuthority parentAuthority;
	/**
	 * 权限编号.
	 */
	@Column(name = "AUTHORITY_CODE")
	private String authorityCode;
	/**
	 * 权限名称.
	 */
	@Column(name = "AUTHORITY_NAME")
	private String authorityName;
	/**
	 * url.
	 */
	@Column(name = "URL")
	private String url;
	/**
	 * 是否是页子结点.
	 */
	@Column(name = "IS_LEAF")
	private Boolean isLeaf;
	/**
	 * 排列顺序.
	 */
	@Column(name = "ESORT")
	private Integer sn;

	/**
	 * 级别.
	 */
	@Column(name = "ELEVEL")
	private Integer level;
	/**
	 * 查询序列.
	 */
	@Column(name = "ID_SEQ")
	private String idSeq;
	/**
	 * icon:图标
	 */
	@Column(name = "ICON")
	private String icon;
	/**
	 * ETYPE
	 */
	@Column(name = "ETYPE")
	private String etype;
	/**
	 * 权限集合（权限维护）.
	 */
	@OrderBy("sn ASC")
	@OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY, mappedBy = "parentAuthority")
	private Set<SysAuthority> children = new HashSet<SysAuthority>(0);
	/**
	 * 角色(多对多关系维护).
	 */
	@OrderBy("roleCode ASC")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_ROLE_AUTHORITY", joinColumns = { @JoinColumn(name = "AUTH_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) })
	private Set<SysRole> sysRoles = new HashSet<SysRole>(0);

	/**
	 * 用户(多对多关系维护).
	 */
	@OrderBy("realname ASC")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_USER_AUTHORITY", joinColumns = { @JoinColumn(name = "AUTH_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) })
	private Set<SysUser> sysUsers = new HashSet<SysUser>(0);
	/**
	 * 岗位(多对多关系维护).
	 */
	@OrderBy("posicode ASC")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_POSI_AUTHORITY", joinColumns = { @JoinColumn(name = "AUTH_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "POSI_ID", nullable = false, updatable = false) })
	private Set<SysPosition> sysPosition = new HashSet<SysPosition>(0);
	/**
	 * 工作组(多对多关系维护).
	 */
	@OrderBy("groupCode ASC")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_GROUP_AUTHORITY", joinColumns = { @JoinColumn(name = "AUTH_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) })
	private Set<SysGroup> sysGroups = new HashSet<SysGroup>(0);

	public SysAuthority() {
	}

	public SysAuthority(String id, String authorityName) {
		setId(id);
		this.authorityName = authorityName;
	}

	public Set<SysGroup> getSysGroups() {
		return sysGroups;
	}

	public void setSysGroups(Set<SysGroup> sysGroups) {
		this.sysGroups = sysGroups;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Set<SysAuthority> getChildren() {
		return children;
	}

	public void setChildren(Set<SysAuthority> children) {
		this.children = children;
	}
	public Integer getSn() {
		return sn;
	}

	public void setSn(Integer sn) {
		this.sn = sn;
	}
	public SysAuthority getParentAuthority() {
		return parentAuthority;
	}

	public void setParentAuthority(SysAuthority parentAuthority) {
		this.parentAuthority = parentAuthority;
	}

	public String getAuthorityCode() {
		return authorityCode;
	}

	public void setAuthorityCode(String authorityCode) {
		this.authorityCode = authorityCode;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getIdSeq() {
		return idSeq;
	}

	public void setIdSeq(String idSeq) {
		this.idSeq = idSeq;
	}

	public Set<SysRole> getSysRoles() {
		return sysRoles;
	}

	public void setSysRoles(Set<SysRole> sysRoles) {
		this.sysRoles = sysRoles;
	}

	public Set<SysUser> getSysUsers() {
		return sysUsers;
	}

	public void setSysUsers(Set<SysUser> sysUsers) {
		this.sysUsers = sysUsers;
	}

	public Set<SysPosition> getSysPosition() {
		return sysPosition;
	}

	public void setSysPosition(Set<SysPosition> sysPosition) {
		this.sysPosition = sysPosition;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getEtype() {
		return etype;
	}

	public void setEtype(String etype) {
		this.etype = etype;
	}

	

}