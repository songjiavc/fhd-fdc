package com.fhd.sys.web.form.auth;

import com.fhd.entity.sys.auth.SysAuthority;
/**
 * 
 * ClassName:SysAuthorityForm
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-9-27		上午1:50:05
 *
 * @see
 */
public class SysAuthorityForm extends SysAuthority {
	private static final long serialVersionUID = 1603087104140883560L;
	private String parentId;

	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}

