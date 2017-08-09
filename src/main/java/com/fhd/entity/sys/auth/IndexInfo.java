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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
/**
 * 
 * ClassName:IndexInfo初始信息对象
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-8-23		下午1:58:04
 *
 * @see
 */
@Entity
@Table(name = "t_sys_index_info")
public class IndexInfo extends IdEntity implements java.io.Serializable {
	
	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = 2483021692165513126L;
	/**
	 * 主页url地址
	 */
	@Column(name = "HOME_URL")
	private String homeUrl;
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;
	/**
	 * 是否启用状态
	 */
	@Column(name = "ESTATUS")
	private String status;
	
	/**
	 * 用户(多对多关系维护).
	 */
	@OrderBy("username ASC")
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_USER_INDEX_INFO", joinColumns = { @JoinColumn(name = "INDEX_INFO_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) })
	private Set<SysUser> sysUsers = new HashSet<SysUser>(0);
	/**
	 * 角色(多对多关系维护).
	 */
	@OrderBy("roleCode ASC")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_SYS_ROLE_INDEX_INFO", joinColumns = { @JoinColumn(name = "INDEX_INFO_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) })
	private Set<SysRole> sysRoles = new HashSet<SysRole>(0);
	
	public IndexInfo() {
		super();
	}

	public IndexInfo(String homeUrl, Integer sort, String status,
			Set<SysUser> sysUsers, Set<SysRole> sysRoles) {
		super();
		this.homeUrl = homeUrl;
		this.sort = sort;
		this.status = status;
		this.sysUsers = sysUsers;
		this.sysRoles = sysRoles;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<SysUser> getSysUsers() {
		return sysUsers;
	}

	public void setSysUsers(Set<SysUser> sysUsers) {
		this.sysUsers = sysUsers;
	}

	public Set<SysRole> getSysRoles() {
		return sysRoles;
	}

	public void setSysRoles(Set<SysRole> sysRoles) {
		this.sysRoles = sysRoles;
	}
	
}