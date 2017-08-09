package com.fhd.entity.sys.orgstructure;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 人员关联机构临时表
 * */

@Entity
@Table(name = "temp_emporg_exceldata")
public class TempEmpOrgExcelData  extends IdEntity implements Serializable{

	/**
	 * serialVersionUID
	 *
	 * @author 金鹏祥
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * 机构
	 * */
	@Column(name = "ORG_ID")
	private String orgId;
	
	/**
	 * 员工编号
	 * */
	@Column(name = "EMP_CODE")
	private String empCode;
	
	/**
	 * 员工名称
	 * */
	@Column(name = "EMP_NAME")
	private String empName;
	
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
	 * 所属部门名称
	 * */
	@Column(name = "ORG_NAME")
	private String orgName;
	
	/**
	 * 集团/公司
	 * */
	@Column(name = "COMPANY_ID")
	private String companyId;
	
	/**
	 * 集团/公司(名称)
	 * */
	@Column(name = "COMPANY_NAME")
	private String companyName;

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
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
