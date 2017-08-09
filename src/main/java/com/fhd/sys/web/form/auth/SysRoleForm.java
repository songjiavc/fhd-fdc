package com.fhd.sys.web.form.auth;

import com.fhd.entity.sys.auth.SysRole;

/**
 * 
 * ClassName:SysRoleForm
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-8-7		上午10:33:33
 *
 * @see
 */
public class SysRoleForm extends SysRole {

	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = -7018459346045832138L;
	
	private String homeUrl;
	public SysRoleForm() {
		super();
	}

	/** full constructor */
	public SysRoleForm(String roleCode, String roleName) {
		super(roleCode, roleName);
	}

	public SysRoleForm(String homeUrl) {
		super();
		this.homeUrl = homeUrl;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}
	
}

