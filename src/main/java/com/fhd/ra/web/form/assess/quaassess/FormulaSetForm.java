package com.fhd.ra.web.form.assess.quaassess;

import com.fhd.entity.sys.assess.FormulaSet;

public class FormulaSetForm extends FormulaSet{
	
	private static final long serialVersionUID = 1L;
	
	private String orgSummarizing;
	private String kpiSummarizing;
	private String strategySummarizing;
	private String processSummarizing;
	
	public FormulaSetForm(){
		
	}
	
	public FormulaSetForm(FormulaSet formula){
		this.setDeptRiskFormula(formula.getDeptRiskFormula());
		this.setId(formula.getId());
		this.setKpiRiskFormula(formula.getKpiRiskFormula());
		this.setProcessRiskFormula(formula.getProcessRiskFormula());
		this.setRiskTypeDimFormula(formula.getRiskTypeDimFormula());
		this.setStrategyRiskFormula(formula.getStrategyRiskFormula());
		this.setRiskLevelFormula(formula.getRiskLevelFormula());
	}

	public String getOrgSummarizing() {
		return orgSummarizing;
	}

	public void setOrgSummarizing(String orgSummarizing) {
		this.orgSummarizing = orgSummarizing;
	}

	public String getKpiSummarizing() {
		return kpiSummarizing;
	}

	public void setKpiSummarizing(String kpiSummarizing) {
		this.kpiSummarizing = kpiSummarizing;
	}

	public String getStrategySummarizing() {
		return strategySummarizing;
	}

	public void setStrategySummarizing(String strategySummarizing) {
		this.strategySummarizing = strategySummarizing;
	}

	public String getProcessSummarizing() {
		return processSummarizing;
	}

	public void setProcessSummarizing(String processSummarizing) {
		this.processSummarizing = processSummarizing;
	}
}