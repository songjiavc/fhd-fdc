package com.fhd.entity.sys.auth.view;

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

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.group.SysGroup;
import com.fhd.entity.sys.orgstructure.SysPosition;

@Entity
@Subselect(
	"	SELECT" +
	"		T_SYS_AUTHORITY.*, CHILDREN.CHILDREN_COUNT CHILDREN_COUNT" +
	"	FROM T_SYS_AUTHORITY T_SYS_AUTHORITY" +
	"	LEFT JOIN (" +
	"		SELECT" +
	"			PARENT.ID,COUNT(CHILDREN.ID) CHILDREN_COUNT" +
	"		FROM T_SYS_AUTHORITY PARENT" +
	"		LEFT JOIN T_SYS_AUTHORITY CHILDREN ON PARENT.ID = CHILDREN.PARENT_ID" +
	"		GROUP BY" +
	"			PARENT.ID" +
	"	) CHILDREN ON CHILDREN.ID = T_SYS_AUTHORITY.ID"
)
@Synchronize("T_SYS_AUTHORITY")
public class VSysAuthority extends IdEntity implements java.io.Serializable {
	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	private static final long serialVersionUID = 3661606089254080694L;
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
	private int sn;
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
	 * childrenCount:子节点数量
	 */
	@Column(name = "CHILDREN_COUNT")
	private Integer childrenCount;
	
	
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
	
	public VSysAuthority() {
		super();
	}


	public VSysAuthority(SysAuthority parentAuthority, String authorityCode,
			String authorityName, String url, Boolean isLeaf, int sn,
			Integer level, String idSeq, String icon, String etype,
			Integer childrenCount, Set<SysAuthority> children,
			Set<SysRole> sysRoles, Set<SysUser> sysUsers,
			Set<SysPosition> sysPosition, Set<SysGroup> sysGroups) {
		super();
		this.parentAuthority = parentAuthority;
		this.authorityCode = authorityCode;
		this.authorityName = authorityName;
		this.url = url;
		this.isLeaf = isLeaf;
		this.sn = sn;
		this.level = level;
		this.idSeq = idSeq;
		this.icon = icon;
		this.etype = etype;
		this.childrenCount = childrenCount;
		this.children = children;
		this.sysRoles = sysRoles;
		this.sysUsers = sysUsers;
		this.sysPosition = sysPosition;
		this.sysGroups = sysGroups;
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


	public Boolean getIsLeaf() {
		return isLeaf;
	}


	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}


	public int getSn() {
		return sn;
	}


	public void setSn(int sn) {
		this.sn = sn;
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


	public Set<SysAuthority> getChildren() {
		return children;
	}


	public void setChildren(Set<SysAuthority> children) {
		this.children = children;
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


	public Set<SysGroup> getSysGroups() {
		return sysGroups;
	}


	public void setSysGroups(Set<SysGroup> sysGroups) {
		this.sysGroups = sysGroups;
	}


	public Integer getChildrenCount() {
		return childrenCount;
	}


	public void setChildrenCount(Integer childrenCount) {
		this.childrenCount = childrenCount;
	}

}