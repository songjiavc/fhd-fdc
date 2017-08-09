package com.fhd.entity.sys.orgstructure;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 人员关联角色临时表
 * */

@Entity
@Table(name = "temp_emprole_exceldata")
public class TempEmpRoleExcelData  extends IdEntity implements Serializable{

	/**
	 * serialVersionUID
	 *
	 * @author 金鹏祥
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * 角色
	 * */
	@Column(name = "ROLE_ID")
	private String roleId;
	
	/**
	 * 登录名
	 * */
	@Column(name = "USER_ID")
	private String userid;
	
	/**
	 * 行号
	 * */
	@Column(name = "EX_ROW")
	private String exRow;
	
	/**
	 * 说明
	 * */
	@Column(name = "COMMENT")
	private String comment;

	/**
	 * 角色名称
	 * */
	@Column(name = "ROLE_NAME")
	private String roleName;
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getExRow() {
		return exRow;
	}

	public void setExRow(String exRow) {
		this.exRow = exRow;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
