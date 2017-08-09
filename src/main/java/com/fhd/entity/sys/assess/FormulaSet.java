package com.fhd.entity.sys.assess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;
/**
 * 公式计算
 * @author 王再冉
 *
 */
@Entity
@Table(name = "T_SYS_FOMULASET") 
public class FormulaSet extends IdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 组织风险公式
	 */
	@Column(name = "DEPT_RISK_FORMULA")
	private String deptRiskFormula;
	/**
	 * 目标风险公式
	 */
	@Column(name = "STRATEGY_RISK_FORMULA")
	private String strategyRiskFormula;
	/**
	 * 指标风险公式
	 */
	@Column(name = "KPI_RISK_FORMULA")
	private String kpiRiskFormula;
	/**
	 * 流程风险公式
	 */
	@Column(name = "PROC2ESS_RISK_FORMULA")
	private String processRiskFormula;
	/**
	 * 风险分类维度定义公式
	 */
	@Column(name = "RISK_TYPE_DIM_FORMULA")
	private String riskTypeDimFormula;

	/**
	 * 风险水平计算公式
	 */
	@Column(name = "RISK_LEVEL_FORMULA")
	private String riskLevelFormula;
	
	/**
	 * 汇总(是否汇总风险、目标、流程、组织)
	 * */
	@Column(name = "SUMMARIZING")
	private String summarizing;
	
	/**
	 * 风险水平计算公式说明TEXT
	 * */
	@Column(name = "RISK_LEVEL_FORMULA_TEXT")
	private String riskLevelFormulaText;
	
	/**
	 * 所属公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	
	public String getRiskLevelFormulaText() {
		return riskLevelFormulaText;
	}

	public void setRiskLevelFormulaText(String riskLevelFormulaText) {
		this.riskLevelFormulaText = riskLevelFormulaText;
	}

	public String getRiskLevelFormula() {
		return riskLevelFormula;
	}

	public void setRiskLevelFormula(String riskLevelFormula) {
		this.riskLevelFormula = riskLevelFormula;
	}

	public FormulaSet(){
		
	}
	
	public String getDeptRiskFormula() {
		return deptRiskFormula;
	}
	public void setDeptRiskFormula(String deptRiskFormula) {
		this.deptRiskFormula = deptRiskFormula;
	}
	public String getStrategyRiskFormula() {
		return strategyRiskFormula;
	}
	public void setStrategyRiskFormula(String strategyRiskFormula) {
		this.strategyRiskFormula = strategyRiskFormula;
	}
	public String getKpiRiskFormula() {
		return kpiRiskFormula;
	}
	public void setKpiRiskFormula(String kpiRiskFormula) {
		this.kpiRiskFormula = kpiRiskFormula;
	}
	public String getProcessRiskFormula() {
		return processRiskFormula;
	}
	public void setProcessRiskFormula(String processRiskFormula) {
		this.processRiskFormula = processRiskFormula;
	}

	public String getRiskTypeDimFormula() {
		return riskTypeDimFormula;
	}

	public void setRiskTypeDimFormula(String riskTypeDimFormula) {
		this.riskTypeDimFormula = riskTypeDimFormula;
	}

	public String getSummarizing() {
		return summarizing;
	}

	public void setSummarizing(String summarizing) {
		this.summarizing = summarizing;
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}
}