package com.fhd.entity.sys.orgstructure;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;


/**
 * 人员临时表实体
 * */

@Entity
@Table(name = "temp_emp_exceldata")
public class TempEmpExcelData extends IdEntity implements Serializable{
	
	/**
	 * serialVersionUID
	 *
	 * @author 金鹏祥
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * 登录名
	 * */
	@Column(name = "USER_NAME")
	private String userName;
	
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
	 * 集团名称
	 * */
	@Column(name = "COMPANY_NAME")
	private String companyName;
	
	/**
	 * 集团ID
	 * */
	@Column(name = "COMPANY_ID")
	private String companyId;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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