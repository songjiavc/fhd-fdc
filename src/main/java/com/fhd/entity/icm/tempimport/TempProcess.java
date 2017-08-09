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
 * 流程导入临时表实体.
 * @author 吴德福
 * @Date 2013-11-26 14:10:32
 */
@Entity
@Table(name = "TEMP_IMP_PROCESS")
public class TempProcess extends IdEntity implements Serializable{

	private static final long serialVersionUID = -607676918452273805L;

	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX")
	private String index;
	/**
	 * 上级流程编号
	 */
	@Column(name="PARENT_PROCESS_CODE")
	private String parentProcessureCode;
	/**
	 * 上级流程名称
	 */
	@Column(name="PARENT_PROCESS_NAME")
	private String parentProcessureName;
	/**
	 * 流程编号--必填
	 */
	@Column(name="PROCESS_CODE")
	private String processureCode;
	/**
	 * 流程名称--必填
	 */
	@Column(name="PROCESS_NAME")
	private String processureName;
	/**
	 * 责任部门--末级流程必填
	 */
	@Column(name="RESPONSIBLE_ORG")
	private String responsibleOrg;
	/**
	 * 相关部门
	 */
	@Column(name="RELATE_ORG")
	private String relateOrg;
	/**
	 * 流程发生频率--末级流程必填
	 */
	@Column(name = "FREQUENCY")
	private String frequency;
	/**
	 * 影响的财报科目
	 */
	@Column(name = "AFFECT_SUBJECTS")
	private String affectSubjects;
	/**
	 * 责任人--末级流程必填
	 */
	@Column(name="RESPONSIBLE_EMP")
	private String responsibleEmp;
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
	
	public TempProcess() {
		super();
	}
	
	public TempProcess(String index, String parentProcessureCode,
			String parentProcessureName, String processureCode,
			String processureName, String responsibleOrg, String relateOrg,
			String frequency, String affectSubjects, String responsibleEmp,
			String errorTip, FileUploadEntity fileUploadEntity, String idSeq,
			String companyId, String parentId, Integer level, Integer sort,
			Boolean isLeaf, String deleteStatus, String dealStatus,
			String createTime, String createBy) {
		super();
		this.index = index;
		this.parentProcessureCode = parentProcessureCode;
		this.parentProcessureName = parentProcessureName;
		this.processureCode = processureCode;
		this.processureName = processureName;
		this.responsibleOrg = responsibleOrg;
		this.relateOrg = relateOrg;
		this.frequency = frequency;
		this.affectSubjects = affectSubjects;
		this.responsibleEmp = responsibleEmp;
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

	public String getParentProcessureCode() {
		return parentProcessureCode;
	}

	public void setParentProcessureCode(String parentProcessureCode) {
		this.parentProcessureCode = parentProcessureCode;
	}

	public String getParentProcessureName() {
		return parentProcessureName;
	}

	public void setParentProcessureName(String parentProcessureName) {
		this.parentProcessureName = parentProcessureName;
	}

	public String getProcessureCode() {
		return processureCode;
	}

	public void setProcessureCode(String processureCode) {
		this.processureCode = processureCode;
	}

	public String getProcessureName() {
		return processureName;
	}

	public void setProcessureName(String processureName) {
		this.processureName = processureName;
	}

	public String getResponsibleOrg() {
		return responsibleOrg;
	}

	public void setResponsibleOrg(String responsibleOrg) {
		this.responsibleOrg = responsibleOrg;
	}

	public String getRelateOrg() {
		return relateOrg;
	}

	public void setRelateOrg(String relateOrg) {
		this.relateOrg = relateOrg;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getAffectSubjects() {
		return affectSubjects;
	}

	public void setAffectSubjects(String affectSubjects) {
		this.affectSubjects = affectSubjects;
	}

	public String getResponsibleEmp() {
		return responsibleEmp;
	}

	public void setResponsibleEmp(String responsibleEmp) {
		this.responsibleEmp = responsibleEmp;
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
