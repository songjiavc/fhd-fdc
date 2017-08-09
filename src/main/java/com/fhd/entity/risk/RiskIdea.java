package com.fhd.entity.risk;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.base.IdEntity;

/**
 * 风险修改意见
 * */

@Entity
@Table(name = "t_rm_risk_idea ") 
public class RiskIdea extends IdEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 风险人员关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_RELA_RISK_ID")
    private EmpRelaRiskIdea empRelaRiskIdea;

    /**
     * 意见内容
     */
    @Column(name = "CONTENT")
    private String content;
    
    /**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME")
	private Date createTime;

	public EmpRelaRiskIdea getEmpRelaRiskIdea() {
		return empRelaRiskIdea;
	}

	public void setEmpRelaRiskIdea(EmpRelaRiskIdea empRelaRiskIdea) {
		this.empRelaRiskIdea = empRelaRiskIdea;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}