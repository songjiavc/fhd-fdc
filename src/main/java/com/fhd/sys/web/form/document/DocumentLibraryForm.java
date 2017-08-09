package com.fhd.sys.web.form.document;

import com.fhd.entity.sys.document.DocumentLibrary;
import com.fhd.entity.sys.document.DocumentRelaFile;

public class DocumentLibraryForm extends DocumentLibrary{
	private static final long serialVersionUID = 1L;
	
	private String orgId;
	private String fileIds;
	private String dictEntryId;
	private String dictEntryNameStr;
	private String createTimeStr;
	private String lastModifyTimeStr;
	
	public DocumentLibraryForm(){
		
	}
	
	public DocumentLibraryForm(DocumentLibrary document,DocumentRelaFile docRelaFile){
		this.setId(document.getId());
		this.setDocumentCode(document.getDocumentCode());
		this.setDocumentName(document.getDocumentName());
		this.setLastModifyTimeStr(null!=document.getLastModifyTime()?document.getLastModifyTime().toString().split(" ")[0]:"");
		this.setDictEntryNameStr(null!=document.getDictEntry()?document.getDictEntry().getName():"");//所属分类
		this.setCreateTimeStr(null!=document.getCreatTime()?document.getCreatTime().toString().split(" ")[0]:"");
		if(null != docRelaFile){
			this.setFileIds(docRelaFile.getFile().getId());
		}else{
			this.setFileIds("");
		}
	}
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getFileIds() {
		return fileIds;
	}
	public void setFileIds(String fileIds) {
		this.fileIds = fileIds;
	}
	public String getDictEntryId() {
		return dictEntryId;
	}
	public void setDictEntryId(String dictEntryId) {
		this.dictEntryId = dictEntryId;
	}

	public String getDictEntryNameStr() {
		return dictEntryNameStr;
	}

	public void setDictEntryNameStr(String dictEntryNameStr) {
		this.dictEntryNameStr = dictEntryNameStr;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getLastModifyTimeStr() {
		return lastModifyTimeStr;
	}

	public void setLastModifyTimeStr(String lastModifyTimeStr) {
		this.lastModifyTimeStr = lastModifyTimeStr;
	}
	
	

}
