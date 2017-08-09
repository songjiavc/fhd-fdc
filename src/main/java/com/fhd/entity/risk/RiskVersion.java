package com.fhd.entity.risk;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.fhd.entity.base.IdEntity;


/**
 * 风险版本类
 * @author  zhengjunxiang
 * @version  
 * @since    Ver 1.0
 * @Date	 2013-11-12	
 *
 * @see 	 
 */
@Entity
@Table(name = "T_RM_VERSION") 
public class RiskVersion extends IdEntity implements Serializable{
	private static final long serialVersionUID = -8267861072609143434L;
	
	/**
	 * 编号
	 */
	@Column(name = "VERSION_CODE")
	private String code;
	
	/**
	 * 名称
	 */
	@Column(name = "VERSION_NAME")
	private String name; 
	
	/**
	 * 描述
	 */
	@Column(name = "EDESC")
	private String desc;
	
	/**
	 * 创建时间
	 */
	@Column(name = "CREATE_TIME")
	private Date createTime;
	
	/**
	 * 创建人
	 */
	@Column(name = "CREATE_BY")
	private String createBy;

	/**
	 * 公司id
	 */
	@Column(name = "COMPANY_ID")
	private String companyId;
	
	/**
	 * 分库标志
	 */
	@Column(name = "SCHM")
	private String schm;
	/**
	 * 创建的组织分库用
	 */
	@Column(name = "CREATE_ORG")
	private String createOrg;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getSchm() {
		return schm;
	}

	public void setSchm(String schm) {
		this.schm = schm;
	}

	public String getCreateOrg() {
		return createOrg;
	}

	public void setCreateOrg(String createOrg) {
		this.createOrg = createOrg;
	}
	
}

