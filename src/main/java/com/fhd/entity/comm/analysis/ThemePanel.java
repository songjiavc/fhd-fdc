package com.fhd.entity.comm.analysis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 主题分析面板.
 * @author 吴德福
 * @since 2013-9-2
 */
@Entity
@Table(name = "T_COM_PANEL")
public class ThemePanel extends IdEntity implements Serializable{

	private static final long serialVersionUID = 8775773919796536055L;
	
	//主题分析
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THEME_ID")
    private ThemeAnalysis themeAnalysis;
    //面板名称
    @Column(name = "NAME")
    private String panelName;
    //图表类型
    @Column(name = "CHART_TYPE")
    private String chartType;
 	//高度比
    @Column(name = "HEIGHT_RATIO")
	private String heightRatio;
	//宽度比
    @Column(name = "WIDTH_RATIO")
	private String widthRatio;
	//高度
    @Column(name = "HEIGHT")
	private String height;
    //宽度
    @Column(name = "WIDTH")
	private String width;
    //面板位置--按字符顺序排列A,B,C,D...
    @Column(name = "POSITION")
	private String position;
    //对象类型--kpi/risk/category/strategy
    @Column(name = "OBJECT_TYPE")
	private String objectType;
    /*
     * 值类型
     * risk--发生可能性和影响程度
     * kpi--目标值、实际值、评估值等
     * category--评估值
     * strategy--评价值
     */
    @Column(name = "VALUE_TYPE")
	private String valueType;
    //时间区间
    @Column(name = "TIME_TYPE")
	private String timeType;
    /**
	 * 主题分析数据源(多对多关系维护).
	 */
	@OrderBy("dataSourceName ASC")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_COM_PANEL_DATASOURCE", joinColumns = { @JoinColumn(name = "PANEL_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "DS_ID", nullable = false, updatable = false) })
	private Set<ThemeDataSource> themeDataSources = new HashSet<ThemeDataSource>(0);
    
	public ThemePanel() {
		super();
	}

	public ThemePanel(ThemeAnalysis themeAnalysis, String panelName,
			String chartType, String heightRatio, String widthRatio,
			String height, String width, String position, String objectType,
			String valueType, String timeType,
			Set<ThemeDataSource> themeDataSources) {
		super();
		this.themeAnalysis = themeAnalysis;
		this.panelName = panelName;
		this.chartType = chartType;
		this.heightRatio = heightRatio;
		this.widthRatio = widthRatio;
		this.height = height;
		this.width = width;
		this.position = position;
		this.objectType = objectType;
		this.valueType = valueType;
		this.timeType = timeType;
		this.themeDataSources = themeDataSources;
	}

	public ThemeAnalysis getThemeAnalysis() {
		return themeAnalysis;
	}

	public void setThemeAnalysis(ThemeAnalysis themeAnalysis) {
		this.themeAnalysis = themeAnalysis;
	}

	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Set<ThemeDataSource> getThemeDataSources() {
		return themeDataSources;
	}

	public void setThemeDataSources(Set<ThemeDataSource> themeDataSources) {
		this.themeDataSources = themeDataSources;
	}

	public String getHeightRatio() {
		return heightRatio;
	}

	public void setHeightRatio(String heightRatio) {
		this.heightRatio = heightRatio;
	}

	public String getWidthRatio() {
		return widthRatio;
	}

	public void setWidthRatio(String widthRatio) {
		this.widthRatio = widthRatio;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}
}