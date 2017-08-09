package com.fhd.entity.sys.orgstructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
/**
 * 岗位临时表
 * @功能 : 
 * @author 王再冉
 * @date 2014-4-24
 * @since Ver
 * @copyRight FHD
 */
@Entity
@Table(name = "TMP_IMP_SYS_POSITION")
public class TmpSysPosition extends IdEntity implements java.io.Serializable{

	/**
	 * 2014-4-24
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 岗位编号
	 */
	@Column(name = "POSI_CODE", length = 255)
	private String posiCode;
	/**
	 * 岗位名称
	 */
	@Column(name = "POSI_NAME", length = 255)
	private String posiName;
	/**
	 * 所属机构
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID")
	private SysOrganization sysOrganization;
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
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private int sn;
	/**
	 * 机构名称
	 */
	@Column(name = "ORG_NAME", length = 255)
	private String orgName;
	
	public TmpSysPosition(){
		
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getPosiCode() {
		return posiCode;
	}

	public void setPosiCode(String posiCode) {
		this.posiCode = posiCode;
	}

	public String getPosiName() {
		return posiName;
	}

	public void setPosiName(String posiName) {
		this.posiName = posiName;
	}

	public SysOrganization getSysOrganization() {
		return sysOrganization;
	}

	public void setSysOrganization(SysOrganization sysOrganization) {
		this.sysOrganization = sysOrganization;
	}

	public String getValidateInfo() {
		return validateInfo;
	}

	public void setValidateInfo(String validateInfo) {
		this.validateInfo = validateInfo;
	}

	public int getRowLine() {
		return rowLine;
	}

	public void setRowLine(int rowLine) {
		this.rowLine = rowLine;
	}

	
	
}
