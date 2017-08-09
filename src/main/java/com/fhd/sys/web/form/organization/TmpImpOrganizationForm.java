/**
 * OrganizationForm.java
 * com.fhd.sys.web.form.organization
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-14 		黄晨曦
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.web.form.organization;

import com.fhd.entity.sys.orgstructure.TmpImpOrganization;

/**
 * 
 * ClassName:TmpImpOrganizationForm 组织机构导入表单实体
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-31		上午11:18:23
 *
 * @see
 */
public class TmpImpOrganizationForm extends TmpImpOrganization{
	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = -2524558126442367210L;
	private String fileId;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
}

