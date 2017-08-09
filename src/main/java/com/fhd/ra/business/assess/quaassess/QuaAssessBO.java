package com.fhd.ra.business.assess.quaassess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.formulatePlan.RiskScoreDeptDAO;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.assess.kpiSet.ScoreObjectDeptEmpDAO;
import com.fhd.dao.assess.quaAssess.ScoreResultDAO;
import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.assess.quaAssess.StatisticsResult;

@Service
public class QuaAssessBO{
	
	@Autowired
	private ScoreResultDAO o_scoreResultDAO;
	
	@Autowired
	private StatisticsResultDAO o_statisticsResultDAO;
	
	@Autowired 
	private RiskScoreObjectDAO o_riskScoreObjectDAO;
	
	@Autowired
	private RiskScoreDeptDAO o_riskScoreDeptDAO;
	
	@Autowired
	private ScoreObjectDeptEmpDAO o_scoreObjectDeptEmpDAO;
	
	/**
	 * 删除打分结果表除
	 * @param scoreResult 打分实体
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void delScoreResult(ScoreResult scoreResult) {
		o_scoreResultDAO.merge(scoreResult);
	}
	
	/**
	 * 删除综合表
	 * @param rangObjectDeptEmp 综合实体
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void delRangObjectDeptEmp(RangObjectDeptEmp rangObjectDeptEmp) {
		o_scoreObjectDeptEmpDAO.merge(rangObjectDeptEmp);
	}
	
	
	/**
	 * 删除打分部门表
	 * @param 打分部门实体
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void delRiskScoreDept(RiskScoreDept riskScoreDept) {
		o_riskScoreDeptDAO.merge(riskScoreDept);
	}
	
	
	/**
	 * 删除打分对象表
	 * @param riskScoreObject 风险打分对象
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void delRiskScoreObject(RiskScoreObject riskScoreObject) {
		o_riskScoreObjectDAO.delete(riskScoreObject);
	}
	
	/**
	 * 逻辑删除打分对象
	 * @author Jzq
	 * @date ：2017年4月24日11:26:27
	 * @param riskScoreObject
	 */
	@Transactional
	public void updateRiskScoreObject(RiskScoreObject riskScoreObject) {
		o_riskScoreObjectDAO.merge(riskScoreObject);
	}
	
	/**
	 * 删除统计打分结果表
	 * @param statisticsResult 总体打分实体
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void delStatisticsResult(StatisticsResult statisticsResult) {
		o_statisticsResultDAO.delete(statisticsResult);
	}
}