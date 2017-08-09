package com.fhd.sys.web.form.tamplatemanage;

import com.fhd.entity.sys.templatemanage.TemplateManage;

public class TemplateManageForm extends TemplateManage{
	private static final long serialVersionUID = 1L;
	
	private String dictEntryId;
	
	public TemplateManageForm(){
		
	}

	public String getDictEntryId() {
		return dictEntryId;
	}

	public void setDictEntryId(String dictEntryId) {
		this.dictEntryId = dictEntryId;
	}

	
}
