package com.fhd.entity.comm.analysis;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 主题分析动态列.
 * @author 吴德福
 * @since 2013-9-2
 */
@Entity
@Table(name = "T_COM_THEME_DYNAMIC_COLUMN")
public class ThemeDynamicColumn extends IdEntity implements Serializable{

	private static final long serialVersionUID = -3856087632375586404L;
	
	//数据源id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DS_ID")
    private ThemeDataSource themeDataSource;
	//对象id
    @Column(name = "OBJECT_ID")
    private String objectId;
	//表名称
    @Column(name = "TABLENAME")
    private String tableName;
	//列名称
    @Column(name = "COLUMN_NAME")
    private String columnName;
	//显示名称
    @Column(name = "DISPLAY_NAME")
    private String displayName;
	//时间类型
    @Column(name = "TIME_TYPE")
    private String timeType;
    
	public ThemeDynamicColumn() {
		super();
	}

	public ThemeDynamicColumn(ThemeDataSource themeDataSource, String objectId,
			String tableName, String columnName, String displayName,
			String timeType) {
		super();
		this.themeDataSource = themeDataSource;
		this.objectId = objectId;
		this.tableName = tableName;
		this.columnName = columnName;
		this.displayName = displayName;
		this.timeType = timeType;
	}

	public ThemeDataSource getThemeDataSource() {
		return themeDataSource;
	}

	public void setThemeDataSource(ThemeDataSource themeDataSource) {
		this.themeDataSource = themeDataSource;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}
}