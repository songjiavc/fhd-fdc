package com.fhd.entity.kpi;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 指标备注
 *
 * @author   郝静
 * @since    fhd Ver 4.5
 * @Date	 2013-7-18  上午10:54:47
 *
 * @see 	 
 */
@Entity
@Table(name = "T_KPI_RELA_MEMO")
public class KpiMemo extends IdEntity implements Serializable {

    private static final long serialVersionUID = -7499607588729661779L;

	/**
	 * 指标
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KPI_GATHERRESLUT_ID")
	private KpiGatherResult kpiGatherResult;
	
	/**
	 * 备注主题
	 */
	@Column(name = "THEME")
	private String theme;
	
	/**
	 * 重要性
	 */
	@Column(name = "IMPORTANT")
	private String important;
	
	/**
	 * 备注信息
	 */
	@Column(name = "MEMO")
	private String memo;
	
    /**
     * 操作时间
     */
    @Column(name = "OPERATION_TIME")
    private Date operTime;
    
	/**
	 * 操作人
	 */
	@Column(name = "OPERATION_BY")
	private String operBy;
	
    public KpiMemo() {
    }

	public KpiGatherResult getKpiGatherResult() {
		return kpiGatherResult;
	}

	public void setKpiGatherResult(KpiGatherResult kpiGatherResult) {
		this.kpiGatherResult = kpiGatherResult;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getImportant() {
		return important;
	}

	public void setImportant(String important) {
		this.important = important;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getOperTime() {
		return operTime;
	}

	public void setOperTime(Date operTime) {
		this.operTime = operTime;
	}

	public String getOperBy() {
		return operBy;
	}

	public void setOperBy(String operBy) {
		this.operBy = operBy;
	}


}
