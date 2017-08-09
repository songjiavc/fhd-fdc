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
 * 流程节点导入临时表实体.
 * @author 吴德福
 * @Date 2013-11-28 14:10:32
 */
@Entity
@Table(name = "TEMP_IMP_PROCESS_POINT")
public class TempProcessPoint extends IdEntity implements Serializable{

	private static final long serialVersionUID = -607676918452273805L;

	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX")
	private String index;
	/**
	 * 流程编号--必填
	 */
	@Column(name="PROCESS_CODE")
	private String processCode;
	/**
	 * 流程名称--必填
	 */
	@Column(name="PROCESS_NAME")
	private String processName;
	/**
	 * 流程节点编号--必填
	 */
	@Column(name="PROCESS_POINT_CODE")
	private String processPointCode;
	/**
	 * 流程节点名称--必填
	 */
	@Column(name="PROCESS_POINT_NAME")
	private String processPointName;
	/**
	 * 节点类型
	 */
	@Column(name="TYPE")
	private String type;
	/**
	 * 节点描述
	 */
	@Column(name="PROCESS_POINT_DESC")
	private String processPointDesc;
	/**
	 * 责任部门--末级流程必填
	 */
	@Column(name="RESPONSIBLE_ORG")
	private String responsibleOrg;
	/**
	 * 责任人--末级流程必填
	 */
	@Column(name="RESPONSIBLE_EMP")
	private String responsibleEmp;
	/**
	 * 输入信息
	 */
	@Column(name="INPUT_INFO")
	private String inputInfo;
	/**
	 * 输出信息
	 */
	@Column(name="OUTPUT_INFO")
	private String outputInfo;
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
	/**
	 * 流程id
	 */
	@Column(name = "PROCESS_ID")
	private String processId;
	/**
	 * 删除状态:默认值为--1
	 */
	@Column(name="DELETE_STATUS")
	private String deleteStatus;
	/**
	 * 状态:默认值为--0yn_y(数据字典0yn)
	 */
	@Column(name="STATUS")
	private String status;
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;
	
	public TempProcessPoint() {
		super();
	}
	
	public TempProcessPoint(String index, String processCode,
			String processName, String processPointCode,
			String processPointName, String type, String processPointDesc,
			String responsibleOrg, String responsibleEmp, String inputInfo,
			String outputInfo, String errorTip,
			FileUploadEntity fileUploadEntity, String processId,
			String deleteStatus, String status, Integer sort) {
		super();
		this.index = index;
		this.processCode = processCode;
		this.processName = processName;
		this.processPointCode = processPointCode;
		this.processPointName = processPointName;
		this.type = type;
		this.processPointDesc = processPointDesc;
		this.responsibleOrg = responsibleOrg;
		this.responsibleEmp = responsibleEmp;
		this.inputInfo = inputInfo;
		this.outputInfo = outputInfo;
		this.errorTip = errorTip;
		this.fileUploadEntity = fileUploadEntity;
		this.processId = processId;
		this.deleteStatus = deleteStatus;
		this.status = status;
		this.sort = sort;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProcessPointDesc() {
		return processPointDesc;
	}

	public void setProcessPointDesc(String processPointDesc) {
		this.processPointDesc = processPointDesc;
	}

	public String getResponsibleOrg() {
		return responsibleOrg;
	}

	public void setResponsibleOrg(String responsibleOrg) {
		this.responsibleOrg = responsibleOrg;
	}

	public String getResponsibleEmp() {
		return responsibleEmp;
	}

	public void setResponsibleEmp(String responsibleEmp) {
		this.responsibleEmp = responsibleEmp;
	}

	public String getInputInfo() {
		return inputInfo;
	}

	public void setInputInfo(String inputInfo) {
		this.inputInfo = inputInfo;
	}

	public String getOutputInfo() {
		return outputInfo;
	}

	public void setOutputInfo(String outputInfo) {
		this.outputInfo = outputInfo;
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

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
