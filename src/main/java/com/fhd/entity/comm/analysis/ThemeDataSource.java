package com.fhd.entity.comm.analysis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 主题分析数据源.
 * @author 吴德福
 * @since 2013-9-2
 */
@Entity
@Table(name = "T_COM_THEME_DATASOURCE")
public class ThemeDataSource extends IdEntity implements Serializable{

	private static final long serialVersionUID = -8223455292628664539L;
	
	//数据源名称
	@Column(name = "NAME")
	private String dataSourceName;
	//数据类型
	@Column(name = "TYPE")
	private String dataType;
	/**
	 * 主题分析面板(多对多关系维护).
	 */
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "T_COM_PANEL_DATASOURCE", joinColumns = { @JoinColumn(name = "DS_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "PANEL_ID", nullable = false, updatable = false) })
	private Set<ThemePanel> themePanels = new HashSet<ThemePanel>(0);
	
	public ThemeDataSource() {
		super();
	}

	public ThemeDataSource(String dataSourceName, String dataType,
			Set<ThemePanel> themePanels) {
		super();
		this.dataSourceName = dataSourceName;
		this.dataType = dataType;
		this.themePanels = themePanels;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Set<ThemePanel> getThemePanels() {
		return themePanels;
	}

	public void setThemePanels(Set<ThemePanel> themePanels) {
		this.themePanels = themePanels;
	}
}