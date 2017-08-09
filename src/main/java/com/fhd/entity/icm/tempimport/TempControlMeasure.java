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
 * 控制措施导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-12 13:40:32
 */
@Entity
@Table(name = "TEMP_IMP_CONTROL_MEASURE")
public class TempControlMeasure extends IdEntity implements Serializable{

	private static final long serialVersionUID = 8273006076356478411L;
	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX")
	private String index;
	/**
	 * 控制措施编号--必填
	 */
	@Column(name="CONTROL_MEASURE_CODE")
	private String controlMeasureCode;
	/**
	 * 控制措施名称--必填
	 */
	@Column(name="CONTROL_MEASURE_NAME")
	private String controlMeasureName;
	/**
	 * 责任部门--必填
	 */
	@Column(name="RESPONSIBLE_ORG")
	private String responsibleOrg;
	/**
	 * 责任人--必填
	 */
	@Column(name="RESPONSIBLE_EMP")
	private String responsibleEmp;
	/**
	 * 是否关键控制点--必填
	 */
	@Column(name = "IS_KEY_CONTROL_POINT")
	private String isKeyControlPoint;
	/**
	 * 控制目标
	 */
	@Column(name = "CONTROL_TARGET")
	private String controlTarget;
	/**
	 * 实施证据
	 */
	@Column(name = "IMPLEMENT_PROOF")
	private String implementProof;
	/**
	 * 控制方式
	 */
	@Column(name = "CONTROL_MODE")
	private String controlMode;
	/**
	 * 控制频率--必填
	 */
	@Column(name = "CONTROL_FREQUENCY")
	private String controlFrequency;
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
	 * 所属公司
	 */
	@Column(name = "COMPANY_ID")
	private String companyId;
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;
	/**
	 * 删除状态:默认值为1--启用
	 */
	@Column(name="DELETE_STATUS")
	private String deleteStatus;
	/**
	 * 状态:默认值为--0yn_y(数据字典0yn)
	 */
	@Column(name="ESTATUS")
	private String status;
	/**
	 * 创建时间.
	 */
	@Column(name = "CREATE_TIME")
	private String createTime;
	/**
	 * 创建的操作员的登录名.
	 */
	@Column(name = "CREATE_BY")
	private String createBy;
	
	public TempControlMeasure() {
		super();
	}

	public TempControlMeasure(String index, String controlMeasureCode,
			String controlMeasureName, String responsibleOrg,
			String responsibleEmp, String isKeyControlPoint,
			String controlTarget, String implementProof, String controlMode,
			String controlFrequency, String errorTip,
			FileUploadEntity fileUploadEntity, String companyId, Integer sort,
			String deleteStatus, String status, String createTime,
			String createBy) {
		super();
		this.index = index;
		this.controlMeasureCode = controlMeasureCode;
		this.controlMeasureName = controlMeasureName;
		this.responsibleOrg = responsibleOrg;
		this.responsibleEmp = responsibleEmp;
		this.isKeyControlPoint = isKeyControlPoint;
		this.controlTarget = controlTarget;
		this.implementProof = implementProof;
		this.controlMode = controlMode;
		this.controlFrequency = controlFrequency;
		this.errorTip = errorTip;
		this.fileUploadEntity = fileUploadEntity;
		this.companyId = companyId;
		this.sort = sort;
		this.deleteStatus = deleteStatus;
		this.status = status;
		this.createTime = createTime;
		this.createBy = createBy;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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

	public String getIsKeyControlPoint() {
		return isKeyControlPoint;
	}

	public void setIsKeyControlPoint(String isKeyControlPoint) {
		this.isKeyControlPoint = isKeyControlPoint;
	}

	public String getControlTarget() {
		return controlTarget;
	}

	public void setControlTarget(String controlTarget) {
		this.controlTarget = controlTarget;
	}

	public String getImplementProof() {
		return implementProof;
	}

	public void setImplementProof(String implementProof) {
		this.implementProof = implementProof;
	}

	public String getControlMode() {
		return controlMode;
	}

	public void setControlMode(String controlMode) {
		this.controlMode = controlMode;
	}

	public String getControlFrequency() {
		return controlFrequency;
	}

	public void setControlFrequency(String controlFrequency) {
		this.controlFrequency = controlFrequency;
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

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
}