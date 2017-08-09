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
 * 流程节点关系导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-06 17:40:32
 */
@Entity
@Table(name = "TEMP_IMP_PROCESS_POINT_RELATION")
public class TempProcessPointRelation extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = -1689322484167446915L;

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
	 * 流程编号--导入必填，不验证
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
	 * 流程节点名称
	 */
	@Column(name="PROCESS_POINT_NAME")
	private String processPointName;
	/**
	 * 上一步流程节点id
	 */
	@Column(name="PARENT_PROCESS_POINT_ID")
	private String parentProcessPointId;
	/**
	 * 上一步流程节点编号--必填
	 */
	@Column(name="PARENT_PROCESS_POINT_CODE")
	private String parentProcessPointCode;
	/**
	 * 上一步流程节点名称
	 */
	@Column(name="PARENT_PROCESS_POINT_NAME")
	private String parentProcessPointName;
	/**
	 * 上一步流程节点进入条件
	 */
	@Column(name="ENTER_CONDITION")
	private String enterCondition;
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
	
	public TempProcessPointRelation() {
		super();
	}

	public TempProcessPointRelation(String index, String processId,
			String processCode, String processName, String processPointId,
			String processPointCode, String processPointName,
			String parentProcessPointId, String parentProcessPointCode,
			String parentProcessPointName, String enterCondition,
			String errorTip, FileUploadEntity fileUploadEntity) {
		super();
		this.index = index;
		this.processId = processId;
		this.processCode = processCode;
		this.processName = processName;
		this.processPointId = processPointId;
		this.processPointCode = processPointCode;
		this.processPointName = processPointName;
		this.parentProcessPointId = parentProcessPointId;
		this.parentProcessPointCode = parentProcessPointCode;
		this.parentProcessPointName = parentProcessPointName;
		this.enterCondition = enterCondition;
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

	public String getParentProcessPointId() {
		return parentProcessPointId;
	}

	public void setParentProcessPointId(String parentProcessPointId) {
		this.parentProcessPointId = parentProcessPointId;
	}

	public String getParentProcessPointCode() {
		return parentProcessPointCode;
	}

	public void setParentProcessPointCode(String parentProcessPointCode) {
		this.parentProcessPointCode = parentProcessPointCode;
	}

	public String getParentProcessPointName() {
		return parentProcessPointName;
	}

	public void setParentProcessPointName(String parentProcessPointName) {
		this.parentProcessPointName = parentProcessPointName;
	}

	public String getEnterCondition() {
		return enterCondition;
	}

	public void setEnterCondition(String enterCondition) {
		this.enterCondition = enterCondition;
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