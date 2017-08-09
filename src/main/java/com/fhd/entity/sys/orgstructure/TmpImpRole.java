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
 * ClassName:TmpImpRole 角色导入临时表
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-30		上午11:14:06
 *
 * @see
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "TMP_IMP_ROLE")
public class TmpImpRole extends IdEntity implements java.io.Serializable {

	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	
	private static final long serialVersionUID = -4947887921869495528L;
	
	/**
	 * 数据序号
	 */
	@Column(name = "E_INDEX", length = 255)
	private String index;
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
	 * 权限编号
	 */
	@Column(name = "AUTHORITY_CODE", length = 255)
	private String authorityCode;
	/**
	 * 权限名称
	 */
	@Column(name = "AUTHORITY_NAME", length = 255)
	private String authorityName;
	
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

	public String getAuthorityCode() {
		return authorityCode;
	}

	public void setAuthorityCode(String authorityCode) {
		this.authorityCode = authorityCode;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
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