package com.fhd.entity.icm.tempimport;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.file.FileUploadEntity;

/**
 * 风险--流程--流程节点--控制措施关系导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-12 16:27:32
 */
@Entity
@Table(name = "TEMP_IMP_RISK_PROCESS_POINT_MEASURE_RELATION")
public class TempRiskProcessPointMeasureRelation extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = -3555929254002363551L;
	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX")
	private String index;
	/**
	 * 风险id
	 */
	@Column(name="RISK_ID")
	private String riskId;
	/**
	 * 风险编号--必填
	 */
	@Column(name="RISK_CODE")
	private String riskCode;
	/**
	 * 风险名称--导入必填，不验证
	 */
	@Column(name="RISK_NAME")
	private String riskName;
	/**
	 * 流程id
	 */
	@Column(name="PROCESS_ID")
	private String processId;
	/**
	 * 流程编号--必填
	 */
	@Column(name="PROCESS_CODE")
	private String processCode;
	/**
	 * 流程名称--导入必填，不验证
	 */
	@Column(name="PROCESS_NAME")
	private String processName;
	/**
	 * 流程节点id
	 */
	@Column(name="PROCESS_POINT_ID")
	private String processPointId;
	/**
	 * 流程节点编号--必填
	 */
	@Column(name="PROCESS_POINT_CODE")
	private String processPointCode;
	/**
	 * 流程名称--导入必填，不验证
	 */
	@Column(name="PROCESS_POINT_NAME")
	private String processPointName;
	/**
	 * 控制措施id
	 */
	@Column(name="CONTROL_MEASURE_ID")
	private String controlMeasureId;
	/**
	 * 控制措施编号--必填
	 */
	@Column(name="CONTROL_MEASURE_CODE")
	private String controlMeasureCode;
	/**
	 * 控制措施名称--导入必填，不验证
	 */
	@Column(name="CONTROL_MEASURE_NAME")
	private String controlMeasureName;
	/**
	 * 错误内容提示
	 */
	@Column(name = "ERROR_TIP")
	private String errorTip;
	/**
	 * 上传文档
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILE_ID")
	private FileUploadEntity fileUploadEntity;
	
	public TempRiskProcessPointMeasureRelation() {
		super();
	}

	public TempRiskProcessPointMeasureRelation(String index, String riskId,
			String riskCode, String riskName, String processId,
			String processCode, String processName, String processPointId,
			String processPointCode, String processPointName,
			String controlMeasureId, String controlMeasureCode,
			String controlMeasureName, String errorTip,
			FileUploadEntity fileUploadEntity) {
		super();
		this.index = index;
		this.riskId = riskId;
		this.riskCode = riskCode;
		this.riskName = riskName;
		this.processId = processId;
		this.processCode = processCode;
		this.processName = processName;
		this.processPointId = processPointId;
		this.processPointCode = processPointCode;
		this.processPointName = processPointName;
		this.controlMeasureId = controlMeasureId;
		this.controlMeasureCode = controlMeasureCode;
		this.controlMeasureName = controlMeasureName;
		this.errorTip = errorTip;
		this.fileUploadEntity = fileUploadEntity;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getRiskCode() {
		return riskCode;
	}

	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}

	public String getRiskName() {
		return riskName;
	}

	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessPointId() {
		return processPointId;
	}

	public void setProcessPointId(String processPointId) {
		this.processPointId = processPointId;
	}

	public String getProcessPointCode() {
		return processPointCode;
	}

	public void setProcessPointCode(String processPointCode) {
		this.processPointCode = processPointCode;
	}

	public String getProcessPointName() {
		return processPointName;
	}

	public void setProcessPointName(String processPointName) {
		this.processPointName = processPointName;
	}

	public String getControlMeasureId() {
		return controlMeasureId;
	}

	public void setControlMeasureId(String controlMeasureId) {
		this.controlMeasureId = controlMeasureId;
	}

	public String getControlMeasureCode() {
		return controlMeasureCode;
	}

	public void setControlMeasureCode(String controlMeasureCode) {
		this.controlMeasureCode = controlMeasureCode;
	}

	public String getControlMeasureName() {
		return controlMeasureName;
	}

	public void setControlMeasureName(String controlMeasureName) {
		this.controlMeasureName = controlMeasureName;
	}

	public String getErrorTip() {
		return errorTip;
	}

	public void setErrorTip(String errorTip) {
		this.errorTip = errorTip;
	}

	public FileUploadEntity getFileUploadEntity() {
		return fileUploadEntity;
	}

	public void setFileUploadEntity(FileUploadEntity fileUploadEntity) {
		this.fileUploadEntity = fileUploadEntity;
	}
}