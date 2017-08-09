package com.fhd.entity.sys.document;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 文档库实体
 * @author 王再冉
 *
 */
@Entity
@Table(name="T_SYS_DOCUMENT_LIBRARY")
public class DocumentLibrary extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 所属公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	/**
	 * 数据字典类型
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DICT_ENTRY_ID")
	private DictEntry dictEntry;
	/**
	 * 文档编号
	 */
	@Column(name="DOCUMENT_CODE")
	private String documentCode;
	/**
	 * 文档名称
	 */
	@Column(name="DOCUMENT_NAME")
	private String documentName;
	/**
	 * 描述
	 */
	@Column(name="EDESC")
	private String desc;
	/**
	 * 删除状态
	 */
	@Column(name = "DELETE_STATUS")
	private Boolean deleteStatus;
	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME")
	private Date creatTime;
	/**
	 * 创建人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATE_BY")
	private SysEmployee createBy;
	/**
	 * 最后修改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFY_TIME")
	private Date lastModifyTime;
	/**
	 * 最后修改人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LAST_MODIFY_BY")
	private SysEmployee lastModifyBy;
	/**
	 * 分类
	 */
	@Column(name="ETYPE")
	private String type;
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;

	public DocumentLibrary(){
		
	}
	
	public DocumentLibrary(String id){
		this.setId(id);
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}

	public DictEntry getDictEntry() {
		return dictEntry;
	}

	public void setDictEntry(DictEntry dictEntry) {
		this.dictEntry = dictEntry;
	}

	public String getDocumentCode() {
		return documentCode;
	}

	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public SysEmployee getCreateBy() {
		return createBy;
	}

	public void setCreateBy(SysEmployee createBy) {
		this.createBy = createBy;
	}

	public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public SysEmployee getLastModifyBy() {
		return lastModifyBy;
	}

	public void setLastModifyBy(SysEmployee lastModifyBy) {
		this.lastModifyBy = lastModifyBy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	
}
