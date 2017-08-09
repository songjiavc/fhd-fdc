package com.fhd.entity.sys.document;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.file.FileUploadEntity;

/**
 * 文档库附件
 * @author 王再冉
 *
 */
@Entity
@Table(name = "T_SYS_DOCUMENT_RELA_FILE")
public class DocumentRelaFile extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 文档库
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOCUMENT_ID")
	private DocumentLibrary document;
	
	/**
	 * 附件
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILE_ID")
	private FileUploadEntity file;
	
	public DocumentRelaFile(){
		
	}
	
	public DocumentRelaFile(String id){
		this.setId(id);
	}

	public DocumentLibrary getDocument() {
		return document;
	}

	public void setDocument(DocumentLibrary document) {
		this.document = document;
	}

	public FileUploadEntity getFile() {
		return file;
	}

	public void setFile(FileUploadEntity file) {
		this.file = file;
	}
	
}
