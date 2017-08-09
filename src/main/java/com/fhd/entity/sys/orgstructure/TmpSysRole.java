package com.fhd.entity.sys.orgstructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
/**
 * 角色导入临时表
 * @功能 : 
 * @author 王再冉
 * @date 2014-4-24
 * @since Ver
 * @copyRight FHD
 */
@Entity
@Table(name = "TMP_IMP_SYS_ROLE")
public class TmpSysRole extends IdEntity implements java.io.Serializable{

	/**
	 * 2014-4-24
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 角色编号
	 */
	@Column(name = "ROLE_CODE", length = 255)
	private String roleCode;
	
	/**
	 * 角色名称
	 */
	@Column(name = "ROLE_NAME", length = 255)
	private String roleName;
	/**
	 * 校验信息
	 */
	@Column(name = "VALIDATE_INFO", length = 255)
	private String validateInfo;
	/**
	 * 行号
	 */
	@Column(name = "ROW_LINE")
	private int rowLine;

	public TmpSysRole(){
		
	}


	public int getRowLine() {
		return rowLine;
	}

	public void setRowLine(int rowLine) {
		this.rowLine = rowLine;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getValidateInfo() {
		return validateInfo;
	}

	public void setValidateInfo(String validateInfo) {
		this.validateInfo = validateInfo;
	}
	
	
}
