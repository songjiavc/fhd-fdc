package com.fhd.entity.comm.analysis;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 柱状图.
 * @author 吴德福
 * @since 2013-9-2
 */
@Entity
@Table(name = "T_COM_CHART_COLUMN")
public class ColumnChart extends IdEntity implements Serializable{

	private static final long serialVersionUID = -8135136116406317868L;
	
	//面板
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PANEL_ID")
    private ThemePanel themePanel;
    
	public ColumnChart() {
		super();
	}

	public ColumnChart(ThemePanel themePanel) {
		super();
		this.themePanel = themePanel;
	}

	public ThemePanel getThemePanel() {
		return themePanel;
	}

	public void setThemePanel(ThemePanel themePanel) {
		this.themePanel = themePanel;
	}
}