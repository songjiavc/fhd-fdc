package com.fhd.entity.sys.orgstructure;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 人员关联岗位临时表
 * */

@Entity
@Table(name = "temp_emppost_exceldata")
public class TempEmpPostExcelData extends IdEntity implements Serializable{

	/**
	 * serialVersionUID
	 *
	 * @author 金鹏祥
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * 岗位
	 * */
	@Column(name = "POST_ID")
	private String postId;
	
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
	 * 所属部门
	 * */
	@Column(name = "ORG_NAME")
	private String orgName;
	
	/**
	 * 集团
	 * */
	@Column(name = "COMPANY_ID")
	private String companyId;
	
	@Column(name = "COMPANY_NAME")
	private String companyName;
	
	/**
	 * 说明
	 * */
	@Column(name = "COMMENT")
	private String comment;

	/**
	 * 岗位名称
	 * */
	@Column(name = "POST_NAME")
	private String postName;
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
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
