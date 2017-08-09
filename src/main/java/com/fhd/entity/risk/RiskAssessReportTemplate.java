package com.fhd.entity.risk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
/**
 * 报告模板实体
 * @author 邓广义
 *
 */
@Entity
@Table(name = "T_REPORT_TEMPLATE")
public class RiskAssessReportTemplate extends IdEntity implements Serializable{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 名称
	 */
	@Column(name = "TEMPLATE_NAME")                         
	private String name;
	/**
	 * 编号
	 */
	@Column(name = "TEMPLATE_CODE")
	private String code;
	/**
     * 公司
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private SysOrganization company;
	/**
	 * 模板内容
	 */
	@Lob
	@Column(name = "TEMPLATE_DATA")
	private byte[] templateData;
	/**
	 * 报告类型:风险模板-内控模板-监控预警模板
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_TYPE")
    private DictEntry templateType;
	/**
	 * 状态:？
	 */
	@Column(name = "ESTATUS")
    private String status;
	
	/**
	 * 排序
	 */
	@Column(name = "ESORT")
    private Integer sort;

	/**
	 * 是否为默认模板
	 */
	@Column(name = "IS_DEFAULT")
    private boolean isDefault;

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}

	public byte[] getTemplateData() {
		return templateData;
	}

	public void setTemplateData(byte[] templateData) {
		this.templateData = templateData;
	}

	public DictEntry getTemplateType() {
		return templateType;
	}

	public void setTemplateType(DictEntry templateType) {
		this.templateType = templateType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}


	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
}
