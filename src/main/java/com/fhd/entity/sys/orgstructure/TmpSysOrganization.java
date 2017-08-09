package com.fhd.entity.sys.orgstructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
/**
 * 导入机构临时表
 * @功能 : 
 * @author 王再冉
 * @date 2014-4-22
 * @since Ver
 * @copyRight FHD
 */
@Entity
@Table(name = "TMP_IMP_SYS_ORGANIZATION")
public class TmpSysOrganization extends IdEntity implements java.io.Serializable{

	/**
	 * 2014-4-22
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 机构编号
	 */
	@Column(name = "ORG_CODE", length = 255)
	private String orgCode;
	/**
	 * 机构名称
	 */
	@Column(name = "ORG_NAME", length = 255)
	private String orgName;
	/**
	 * id序列
	 */
	@Column(name = "ID_SEQ", length = 255)
	private String idSeq;
	/**
	 * 机构类型
	 */
	@Column(name = "ORG_TYPE", length = 255)
	private String orgType;
	/**
	 * 机构层级
	 */
	@Column(name = "ORG_LEVEL")
	private Integer orgLevel;
	/**
	 * 机构排序
	 */
	@Column(name = "ESORT", length = 255)
	private int sn;
	/**
	 * 上级机构
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ORG_CODE")
	private TmpSysOrganization parentOrg;
	/**
	 * 公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private TmpSysOrganization company;
	/**
	 * 名称序列
	 */
	@Column(name = "NAME_SEQ", length = 255)
	private String nameSeq;
	/**
	 * 是否叶子节点
	 */
	@Column(name = "IS_LEAF", length = 255)
	private Boolean isLeaf;
	/**
	 * 错误信息
	 */
	@Column(name = "ERROR", length = 255)
	private String error;
	/**
	 * 上级机构名称
	 */
	@Column(name = "PARENT_ORG_NAME", length = 255)
	private String parentName;
	/**
	 * 公司名称
	 */
	@Column(name = "COMPANY_NAME", length = 255)
	private String companyName;
	/**
	 * 行号
	 */
	@Column(name = "ROW_LINE")
	private int rowLine;
	
	public TmpSysOrganization(){
		
	}

	public int getRowLine() {
		return rowLine;
	}

	public void setRowLine(int rowLine) {
		this.rowLine = rowLine;
	}

	public TmpSysOrganization(String id){
		this.setId(id);
	}
	
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getIdSeq() {
		return idSeq;
	}

	public void setIdSeq(String idSeq) {
		this.idSeq = idSeq;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public TmpSysOrganization getParentOrg() {
		return parentOrg;
	}

	public void setParentOrg(TmpSysOrganization parentOrg) {
		this.parentOrg = parentOrg;
	}

	public TmpSysOrganization getCompany() {
		return company;
	}

	public void setCompany(TmpSysOrganization company) {
		this.company = company;
	}

	public String getNameSeq() {
		return nameSeq;
	}

	public void setNameSeq(String nameSeq) {
		this.nameSeq = nameSeq;
	}

	public Integer getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(Integer orgLevel) {
		this.orgLevel = orgLevel;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	
	

}
