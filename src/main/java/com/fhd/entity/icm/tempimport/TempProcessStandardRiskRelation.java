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
 * 流程--控制标准(要求)--风险关系导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-12 10:17:32
 */
@Entity
@Table(name = "TEMP_IMP_PROCESS_STANDARD_RISK_RELATION")
public class TempProcessStandardRiskRelation extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 6580785900401474890L;
	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX")
	private String index;
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
	 * 控制标准(要求)id
	 */
	@Column(name="CONTROL_STANDARD_ID")
	private String controlStandardId;
	/**
	 * 控制标准(要求)编号--必填
	 */
	@Column(name="CONTROL_STANDARD_CODE")
	private String controlStandardCode;
	/**
	 * 控制标准(要求)名称--导入必填，不验证
	 */
	@Column(name="CONTROL_STANDARD_NAME")
	private String controlStandardName;
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
	
	public TempProcessStandardRiskRelation() {
		super();
	}

	public TempProcessStandardRiskRelation(String index, String processId,
			String processCode, String processName, String controlStandardId,
			String controlStandardCode, String controlStandardName,
			String riskId, String riskCode, String riskName, String errorTip,
			FileUploadEntity fileUploadEntity) {
		super();
		this.index = index;
		this.processId = processId;
		this.processCode = processCode;
		this.processName = processName;
		this.controlStandardId = controlStandardId;
		this.controlStandardCode = controlStandardCode;
		this.controlStandardName = controlStandardName;
		this.riskId = riskId;
		this.riskCode = riskCode;
		this.riskName = riskName;
		this.errorTip = errorTip;
		this.fileUploadEntity = fileUploadEntity;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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

	public String getControlStandardId() {
		return controlStandardId;
	}

	public void setControlStandardId(String controlStandardId) {
		this.controlStandardId = controlStandardId;
	}

	public String getControlStandardCode() {
		return controlStandardCode;
	}

	public void setControlStandardCode(String controlStandardCode) {
		this.controlStandardCode = controlStandardCode;
	}

	public String getControlStandardName() {
		return controlStandardName;
	}

	public void setControlStandardName(String controlStandardName) {
		this.controlStandardName = controlStandardName;
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