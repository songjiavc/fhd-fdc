package com.fhd.entity.comm.analysis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 主题分析实体表.
 * @author 吴德福
 * @since 2013-9-2
 */
@Entity
@Table(name = "T_COM_THEME")
public class ThemeAnalysis extends IdEntity implements Serializable{

	private static final long serialVersionUID = -6086748789833953451L;
	
	//公司
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_ID")
    private SysOrganization company;
	//名称
	@Column(name = "NAME")
	private String name;
	//布局类型
	@Column(name = "LAYOUT_TYPE")
	private String layoutType;
	//描述
	@Column(name = "DESCS")
	private String desc;
	//删除状态
	@Column(name = "DELETE_STATUS")
	private String deleteStatus;
	//布局属性：相对或绝对
	@Column(name = "ATTRIBUTE")
	private String attribute;
	/**
	 * 布局方式.
	 */
	@OrderBy("position ASC")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "themeAnalysis")
	private Set<ThemePanel> themePanels = new HashSet<ThemePanel>(0);
	
	public ThemeAnalysis() {
		super();
	}

	public ThemeAnalysis(SysOrganization company, String name,
			String layoutType, String desc, String deleteStatus,
			String attribute, Set<ThemePanel> themePanels) {
		super();
		this.company = company;
		this.name = name;
		this.layoutType = layoutType;
		this.desc = desc;
		this.deleteStatus = deleteStatus;
		this.attribute = attribute;
		this.themePanels = themePanels;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(String layoutType) {
		this.layoutType = layoutType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Set<ThemePanel> getThemePanels() {
		return themePanels;
	}

	public void setThemePanels(Set<ThemePanel> themePanels) {
		this.themePanels = themePanels;
	}
}