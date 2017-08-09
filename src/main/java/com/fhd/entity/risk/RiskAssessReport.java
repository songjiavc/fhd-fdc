package com.fhd.entity.risk;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.base.IdEntity;

/**
 * 风险评估报告实体
 * @author 邓广义
 *
 */
@Entity
@Table(name = "T_RM_ASSESS_REPORT")
public class RiskAssessReport extends IdEntity implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 报告内容
	 */
	@Lob
	@Column(name = "REPORT_DATA")
	private byte[] reportData;
	
	/**
	 * 	
	 * 状态:保存/提交 onSave/onSubmit
	 */
	@Column(name = "ESTATUS")
    private String status;
	
	
	/**
	 * 创建日期
	 */
	@Column(name = "CREATE_DATE")
    private Date create_date;
	
	/**
	 * 评估计划实体
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSESS_PLAN_ID")
    private RiskAssessPlan riskAssessPlan;
	
	
	/**
	 * 报告word文档
	 */
	@Lob
	@Column(name = "REPORT_DOC")
	private byte[] reportDoc;

	/**
	 * getter and setter
	 * @return
	 */
	public byte[] getReportData() {
		return reportData;
	}

	public void setReportData(byte[] reportData) {
		this.reportData = reportData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public RiskAssessPlan getRiskAssessPlan() {
		return riskAssessPlan;
	}

	public void setRiskAssessPlan(RiskAssessPlan riskAssessPlan) {
		this.riskAssessPlan = riskAssessPlan;
	}

	public byte[] getReportDoc() {
		return reportDoc;
	}

	public void setReportDoc(byte[] reportDoc) {
		this.reportDoc = reportDoc;
	}

	
	
}
