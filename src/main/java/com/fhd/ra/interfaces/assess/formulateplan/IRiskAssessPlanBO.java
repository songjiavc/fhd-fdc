package com.fhd.ra.interfaces.assess.formulateplan;

import java.util.List;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.core.dao.Page;

public interface IRiskAssessPlanBO {

	/**
	 * 查询计划任务列表
	 * @param planName	计划名称
	 * @param page
	 * @param sort
	 * @param dir
	 * @param companyId
	 * @return
	 */
	public Page<RiskAssessPlan> findPlansPageBySome(String planName, Page<RiskAssessPlan> page, 
			String companyId, String workType, String status);
	/**
	 * 查询计划任务列表
	 * @param planName	计划名称
	 * @param page
	 * @param sort
	 * @param dir
	 * @param companyId
	 * @param schm 分库标志
	 * @return
	 */
	public Page<RiskAssessPlan> findPlansPageBySome(String planName, Page<RiskAssessPlan> page, 
			String companyId, String workType, String status,String schm);
	
	/**
	 * 保存
	 * @param riskPlan
	 */
	public void saveRiskAssessPlan(RiskAssessPlan riskPlan);
	
	/**
	 * (批量)删除计划任务
	 * @param ids
	 */
	 public void removeRiskAssessPlansByIds(List<String> ids);
	 /**
	  * 更新计划
	  * @param riskPlan
	  */
	 public void mergeRiskAssessPlan(RiskAssessPlan riskPlan);
	
}
