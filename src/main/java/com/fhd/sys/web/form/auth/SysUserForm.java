package com.fhd.sys.web.form.auth;

import com.fhd.entity.sys.auth.SysUser;

/**
 * 用户Form类.
 * @author  wudefu
 * @version V1.0  创建时间：2010-8-30
 * Company FirstHuiDa.
 */
public class SysUserForm extends SysUser {
	
	private static final long serialVersionUID = 1487551196355242520L;
	/**
	 * 所有的用户id集合.
	 */
	private String[] userIds={};
	/**
	 * 所有的角色id字符串.
	 */
	private String[] roleIds={};
	
	private String homeUrl;
	
	public SysUserForm() {
		super();
	}

	public SysUserForm(String[] userIds, String[] roleIds, String homeUrl) {
		super();
		this.userIds = userIds;
		this.roleIds = roleIds;
		this.homeUrl = homeUrl;
	}

	public String[] getUserIds() {
		return userIds;
	}

	public void setUserIds(String[] userIds) {
		this.userIds = userIds;
	}

	public String[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}
}

