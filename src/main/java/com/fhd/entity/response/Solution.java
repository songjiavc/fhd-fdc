package com.fhd.entity.response;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.base.AuditableEntity;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 控制措施
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-9-17		下午2:23:09
 *
 * @see 	 
 */

@Entity
@Table(name="T_RM_SOLUTION")
public class Solution extends AuditableEntity implements Serializable {
	
	private static final long serialVersionUID = -7714989398654347108L;

	public Solution() {
	}
	
	public Solution(String id) {
		 this.setId(id);
	}
	
	/**
	 * 所属公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	/**
	 * 所属应对计划
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESPONSE_PLAN_ID")
	private RiskAssessPlan riskAssessPlan;
	
	/**
	 * 应对措施编号
	 */
	@Column(name="SOLUTION_CODE")
	private String solutionCode;
	/**
	 * 应对措施名称
	 */
	@Column(name="SOLUTION_NAME")
	private String solutionName;
	/**
	 * 预计成本
	 */
	@Column(name="EXPECT_COST")
	private String cost;
	/**
	 * 预计收效
	 */
	@Column(name="EXPECT_INCOME")
	private String income;
	/**
	 * 预案开始时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="EXPECT_START_TIME", length = 7)
	private Date expectStartTime;
	/**
	 * 预案结束时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="EXPECT_FINISH_TIME", length = 7)
	private Date expectEndTime;
	/**
	 * 描述
	 */
	@Column(name="EDESC")
	private String solutionDesc;
	/**
	 * 完成标志
	 */
	@Column(name="COMPLETE_INDICATOR")
	private String completeIndicator;
	/**
	 * 完成标志
	 */
	@Column(name="ESTATUS")
	private String status;
	/**
	 * 完成标志
	 */
	@Column(name="DELETE_STATUS")
	private String deleteStatus;
	/**
	 * 应对策略
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="STATEGY")
	private DictEntry stategy;
	/**
	 * 类型 (0:自动应对任务;1:应对计划)
	 */
	@Column(name="ETYPE")
	private String type;
	
	/**
     * 审批状态
     */
    @Column(name="ARCHIVE_STATUS")
    private String archiveStatus;
	
	public String getSolutionCode() {
		return solutionCode;
	}

	public void setSolutionCode(String solutionCode) {
		this.solutionCode = solutionCode;
	}

	public String getSolutionName() {
		return solutionName;
	}

	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}

	/**
	 *  应对措施对应的部门和人员
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "solution")
	private Set<SolutionRelaOrg> solutionRelaOrg = new HashSet<SolutionRelaOrg>(0);
	/**
	 * 是否增加为预案
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="IS_ADD_PRESOLUTION")
	private DictEntry isAddPresolution;
	
	public SysOrganization getCompany() {
		return company;
	}
	public void setCompany(SysOrganization company) {
		this.company = company;
	}
	
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public Date getExpectStartTime() {
		return expectStartTime;
	}
	public void setExpectStartTime(Date expectStartTime) {
		this.expectStartTime = expectStartTime;
	}
	public Date getExpectEndTime() {
		return expectEndTime;
	}
	public void setExpectEndTime(Date expectEndTime) {
		this.expectEndTime = expectEndTime;
	}

    public String getCompleteIndicator() {
		return completeIndicator;
	}
	public void setCompleteIndicator(String completeIndicator) {
		this.completeIndicator = completeIndicator;
	}
	public DictEntry getStategy() {
		return stategy;
	}
	public void setStategy(DictEntry stategy) {
		this.stategy = stategy;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public Set<SolutionRelaOrg> getSolutionRelaOrg() {
		return solutionRelaOrg;
	}
	public void setSolutionRelaOrg(Set<SolutionRelaOrg> solutionRelaOrg) {
		this.solutionRelaOrg = solutionRelaOrg;
	}

	public DictEntry getIsAddPresolution() {
		return isAddPresolution;
	}

	public void setIsAddPresolution(DictEntry isAddPresolution) {
		this.isAddPresolution = isAddPresolution;
	}

	public RiskAssessPlan getRiskAssessPlan() {
		return riskAssessPlan;
	}

	public void setRiskAssessPlan(RiskAssessPlan riskAssessPlan) {
		this.riskAssessPlan = riskAssessPlan;
	}

    public String getArchiveStatus() {
        return archiveStatus;
    }

    public void setArchiveStatus(String archiveStatus) {
        this.archiveStatus = archiveStatus;
    }

    public String getSolutionDesc() {
        return solutionDesc;
    }

    public void setSolutionDesc(String solutionDesc) {
        this.solutionDesc = solutionDesc;
    }
	
}

