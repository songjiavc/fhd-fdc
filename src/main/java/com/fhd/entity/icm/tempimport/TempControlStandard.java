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
 * 控制标准(要求)导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-11 15:10:32
 */
@Entity
@Table(name = "TEMP_IMP_CONTROL_STANDARD")
public class TempControlStandard extends IdEntity implements Serializable{

	private static final long serialVersionUID = 8273006076356478411L;
	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX")
	private String index;
	/**
	 * 上级控制标准(要求)编号
	 */
	@Column(name="PARENT_CONTROL_STANDARD_CODE")
	private String parentControlStandardCode;
	/**
	 * 上级控制标准(要求)名称
	 */
	@Column(name="PARENT_CONTROL_STANDARD_NAME")
	private String parentControlStandardName;
	/**
	 * 控制标准(要求)编号--必填
	 */
	@Column(name="CONTROL_STANDARD_CODE")
	private String controlStandardCode;
	/**
	 * 控制标准(要求)名称--必填
	 */
	@Column(name="CONTROL_STANDARD_NAME")
	private String controlStandardName;
	/**
	 * 责任部门--要求必填
	 */
	@Column(name="RESPONSIBLE_ORG")
	private String responsibleOrg;
	/**
	 * 控制层级--要求必填
	 */
	@Column(name="CONTROL_LEVEL")
	private String controlLevel;
	/**
	 * 内控要素--要求必填
	 */
	@Column(name = "CONTROL_ELEMENTS")
	private String controlElements ;
	/**
	 * 是否分类
	 */
	@Column(name = "IS_CLASS")
	private String isClass;
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
	 * 主键全路径
	 */
	@Column(name="ID_SEQ")
	private String idSeq;
	/**
	 * 所属公司
	 */
	@Column(name = "COMPANY_ID")
	private String companyId;
	/**
	 * 上级流程
	 */
	@Column(name="PARENT_ID")
	private String parentId;
	/**
	 * 层级
	 */
	@Column(name = "ELEVEL")
	private Integer level;
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;
	/**
	 * 是否是页子节点.
	 */
	@Column(name = "IS_LEAF")
	private Boolean isLeaf;
	/**
	 * 删除状态:默认值为1--启用
	 */
	@Column(name="DELETE_STATUS")
	private String deleteStatus;
	/**
	 * 处理状态: 默认值为F--已完成
	 */
	@Column(name = "DEAL_STATUS")
	private String dealStatus;
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
	
	public TempControlStandard() {
		super();
	}

	public TempControlStandard(String index, String parentControlStandardCode,
			String parentControlStandardName, String controlStandardCode,
			String controlStandardName, String responsibleOrg,
			String controlLevel, String controlElements, String isClass,
			String errorTip, FileUploadEntity fileUploadEntity, String idSeq,
			String companyId, String parentId, Integer level, Integer sort,
			Boolean isLeaf, String deleteStatus, String dealStatus,
			String createTime, String createBy) {
		super();
		this.index = index;
		this.parentControlStandardCode = parentControlStandardCode;
		this.parentControlStandardName = parentControlStandardName;
		this.controlStandardCode = controlStandardCode;
		this.controlStandardName = controlStandardName;
		this.responsibleOrg = responsibleOrg;
		this.controlLevel = controlLevel;
		this.controlElements = controlElements;
		this.isClass = isClass;
		this.errorTip = errorTip;
		this.fileUploadEntity = fileUploadEntity;
		this.idSeq = idSeq;
		this.companyId = companyId;
		this.parentId = parentId;
		this.level = level;
		this.sort = sort;
		this.isLeaf = isLeaf;
		this.deleteStatus = deleteStatus;
		this.dealStatus = dealStatus;
		this.createTime = createTime;
		this.createBy = createBy;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getParentControlStandardCode() {
		return parentControlStandardCode;
	}

	public void setParentControlStandardCode(String parentControlStandardCode) {
		this.parentControlStandardCode = parentControlStandardCode;
	}

	public String getParentControlStandardName() {
		return parentControlStandardName;
	}

	public void setParentControlStandardName(String parentControlStandardName) {
		this.parentControlStandardName = parentControlStandardName;
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

	public String getResponsibleOrg() {
		return responsibleOrg;
	}

	public void setResponsibleOrg(String responsibleOrg) {
		this.responsibleOrg = responsibleOrg;
	}

	public String getControlLevel() {
		return controlLevel;
	}

	public void setControlLevel(String controlLevel) {
		this.controlLevel = controlLevel;
	}

	public String getControlElements() {
		return controlElements;
	}

	public void setControlElements(String controlElements) {
		this.controlElements = controlElements;
	}

	public String getIsClass() {
		return isClass;
	}

	public void setIsClass(String isClass) {
		this.isClass = isClass;
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

	public String getIdSeq() {
		return idSeq;
	}

	public void setIdSeq(String idSeq) {
		this.idSeq = idSeq;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
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