package com.fhd.ra.web.form.response.solution;

import com.fhd.entity.response.Solution;

public class SolutionForm extends Solution{
    /**
	 *  扩展了riskId
	 */
	private static final long serialVersionUID = 1L;
	private String riskId = "";
	/**
	 * 评估计划id
	 */
	private String riskAssessPlanId = "";
	/**
	 * 部门ID
	 */
	private String orgId;
	
	/**
	 * 员工ID
	 * 
	 */
	private String empId;
	/**
	 * 文件id
	 */
	private String fileId = "";
	/**
	 * 描述
	 */
	private String descHtml = "";
	
	public String getRiskId() {
		return riskId;
	}
	
	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getRiskAssessPlanId() {
		return riskAssessPlanId;
	}

	public void setRiskAssessPlanId(String riskAssessPlanId) {
		this.riskAssessPlanId = riskAssessPlanId;
	}

    public String getDescHtml() {
        return descHtml;
    }

    public void setDescHtml(String descHtml) {
        this.descHtml = descHtml;
    }
   
}
