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
 * 穿行测试导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-13 16:44:32
 */
@Entity
@Table(name = "TEMP_IMP_PRACTICE_TEST_ASSESS_POINT")
public class TempPracticeTestAssessPoint extends IdEntity implements Serializable{

	private static final long serialVersionUID = 4866513405460585196L;
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
	 * 流程名称--导入必填，不验证
	 */
	@Column(name="PROCESS_NAME")
	private String processName;
	/**
	 * 流程节点编号--必填
	 */
	@Column(name="PROCESS_POINT_CODE")
	private String processPointCode;
	/**
	 * 流程节点名称--导入必填，不验证
	 */
	@Column(name="PROCESS_POINT_NAME")
	private String processPointName;
	/**
	 * 评价点编号--必填
	 */
	@Column(name="ASSESS_POINT_CODE")
	private String assessPointCode;
	/**
	 * 评价点名称--导入必填，不验证
	 */
	@Column(name="ASSESS_POINT_NAME")
	private String assessPointName;
	/**
	 * 影响的财报科目
	 */
	@Column(name = "AFFECT_SUBJECTS")
	private String affectSubjects;
	/**
	 * 实施证据
	 */
	@Column(name = "IMPLEMENT_PROOF")
	private String implementProof;
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
	 * 流程节点id
	 */
	@Column(name = "PROCESS_POINT_ID")
	private String processPointId;
	
	public TempPracticeTestAssessPoint() {
		super();
	}

	public TempPracticeTestAssessPoint(String index, String processCode,
			String processName, String processPointCode,
			String processPointName, String assessPointCode,
			String assessPointName, String affectSubjects,
			String implementProof, String errorTip,
			FileUploadEntity fileUploadEntity, String processId,
			String processPointId) {
		super();
		this.index = index;
		this.processCode = processCode;
		this.processName = processName;
		this.processPointCode = processPointCode;
		this.processPointName = processPointName;
		this.assessPointCode = assessPointCode;
		this.assessPointName = assessPointName;
		this.affectSubjects = affectSubjects;
		this.implementProof = implementProof;
		this.errorTip = errorTip;
		this.fileUploadEntity = fileUploadEntity;
		this.processId = processId;
		this.processPointId = processPointId;
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

	public String getAssessPointCode() {
		return assessPointCode;
	}

	public void setAssessPointCode(String assessPointCode) {
		this.assessPointCode = assessPointCode;
	}

	public String getAssessPointName() {
		return assessPointName;
	}

	public void setAssessPointName(String assessPointName) {
		this.assessPointName = assessPointName;
	}

	public String getAffectSubjects() {
		return affectSubjects;
	}

	public void setAffectSubjects(String affectSubjects) {
		this.affectSubjects = affectSubjects;
	}

	public String getImplementProof() {
		return implementProof;
	}

	public void setImplementProof(String implementProof) {
		this.implementProof = implementProof;
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

	public String getProcessPointId() {
		return processPointId;
	}

	public void setProcessPointId(String processPointId) {
		this.processPointId = processPointId;
	}
}