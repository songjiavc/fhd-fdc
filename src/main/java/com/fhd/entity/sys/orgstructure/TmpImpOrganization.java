package com.fhd.entity.sys.orgstructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.file.FileUploadEntity;

/**
 * 
 * ClassName:TmpImpOrganization组织机构导入临时表
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-30		上午10:19:37
 *
 * @see
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "TMP_IMP_ORGANIZATION")
public class TmpImpOrganization extends IdEntity implements java.io.Serializable {

	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = 7453429705386207748L;
	
	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX", length = 255)
	private String index;
	/**
	 * 公司编号
	 */
	@Column(name = "COMPANY_CODE", length = 255)
	private String companyCode;
	/**
	 * 公司名称
	 */
	@Column(name = "COMPANY_NAME", length = 255)
	private String companyName;
	/**
	 * 部门编号
	 */
	@Column(name = "ORGANIZATION_CODE", length = 255)
	private String organizationCode;
	/**
	 * 部门名称
	 */
	@Column(name = "ORGANIZATION_NAME", length = 255)
	private String organizationName;
	/**
	 * 岗位编号
	 */
	@Column(name = "POSITION_CODE", length = 255)
	private String positionCode;
	/**
	 * 岗位名称
	 */
	@Column(name = "POSITION_NAME", length = 255)
	private String positionName;
	/**
	 * 用户名
	 */
	@Column(name = "USER_NAME", length = 255)
	private String userName;
	/**
	 * 员工姓名
	 */
	@Column(name = "EMP_NAME", length = 255)
	private String empName;
	/**
	 * 拥有角色编号
	 */
	@Column(name = "HAVE_ROLE_CODES")
	private String haveRoleCodes;
	/**
	 * 拥有角色名称
	 */
	@Column(name = "HAVE_ROLE_NAMES")
	private String haveRoleNames;
	/**
	 * 错误内容
	 */
	@Column(name = "ERROR")
	private String error;
	/**
	 * 上传文档
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILE_ID")
	private FileUploadEntity fileUploadEntity;
	
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getOrganizationCode() {
		return organizationCode;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getPositionCode() {
		return positionCode;
	}
	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getHaveRoleCodes() {
		return haveRoleCodes;
	}
	public void setHaveRoleCodes(String haveRoleCodes) {
		this.haveRoleCodes = haveRoleCodes;
	}
	public String getHaveRoleNames() {
		return haveRoleNames;
	}
	public void setHaveRoleNames(String haveRoleNames) {
		this.haveRoleNames = haveRoleNames;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public FileUploadEntity getFileUploadEntity() {
		return fileUploadEntity;
	}
	public void setFileUploadEntity(FileUploadEntity fileUploadEntity) {
		this.fileUploadEntity = fileUploadEntity;
	}
}