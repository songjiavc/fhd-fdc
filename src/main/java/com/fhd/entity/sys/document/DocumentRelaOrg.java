package com.fhd.entity.sys.document;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 文档关联部门表
 * @author 王再冉
 *
 */
@Entity
@Table(name="T_SYS_DOCUMENT_RELA_ORG")
public class DocumentRelaOrg extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 文档库
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOCUMENT_ID")
	private DocumentLibrary document;
	
	/**
	 * 部门
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID")
	private SysOrganization org;
	
	/**
	 * 部门类型
	 */
	@Column(name="ETYPE")
	private String type;
	
	public DocumentRelaOrg(){
		
	}
	
	public DocumentRelaOrg(String id){
		this.setId(id);
	}

	public DocumentLibrary getDocument() {
		return document;
	}

	public void setDocument(DocumentLibrary document) {
		this.document = document;
	}

	public SysOrganization getOrg() {
		return org;
	}

	public void setOrg(SysOrganization org) {
		this.org = org;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
