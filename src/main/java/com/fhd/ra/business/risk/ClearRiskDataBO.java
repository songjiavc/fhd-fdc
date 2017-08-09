package com.fhd.ra.business.risk;

import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fhd.dao.base.SqlBuilder;
import com.fhd.dao.risk.RiskDAO;

@Service
public class ClearRiskDataBO {
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Transactional
	public boolean removeRiskData(String companyId){
		String sql = "";
		SQLQuery sqlQuery;

    	//0.删除风险评估数据
    	this.removeRiskAssessData(companyId);
    	
		//1.删除风险应对措施
		sql = SqlBuilder.getSql("removeRiskSolutionByCompanyId", null);
		sqlQuery = o_riskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//2.删除风险控制措施
		sql = SqlBuilder.getSql("removeRiskMeasureByCompanyId", null);
		sqlQuery = o_riskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//3.删除风险关联的部门
		sql = SqlBuilder.getSql("removeRiskOrgByCompanyId", null);
		sqlQuery = o_riskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//4.删除风险库
		sql = SqlBuilder.getSql("removeRiskByCompanyId", null);
		sqlQuery = o_riskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
		return true;
	}
	
	@Transactional
	public boolean removeRiskAssessData(String companyId){
	    String sql = "";
        SQLQuery sqlQuery;
	    
	    //删除风险记录表
        sql = SqlBuilder.getSql("removeRiskAdjustByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除评估结果表
        sql = SqlBuilder.getSql("removeAjustHistoryByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除组织评估表
        sql = SqlBuilder.getSql("removeOrgAdjustByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除流程评估表
        sql = SqlBuilder.getSql("removeProcessAdjustByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除指标评估表
        sql = SqlBuilder.getSql("removeKpiAdjustByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除目标评估表
        sql = SqlBuilder.getSql("removeSmAdjustByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除结果表
        sql = SqlBuilder.getSql("removeScoreByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除统计结果表
        sql = SqlBuilder.getSql("removeStatisticByComapnyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除意见表
        sql = SqlBuilder.getSql("removeEditIdeaByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除综合表
        sql = SqlBuilder.getSql("removeRangByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除打分部门表
        sql = SqlBuilder.getSql("removeScoreDeptByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除打分对表
        sql = SqlBuilder.getSql("removeRiskScoreByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除审批人,评估人
        sql = SqlBuilder.getSql("removeCircuseeByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
	    //删除计划表
        sql = SqlBuilder.getSql("removeAssessPlanByCompanyId", null);
        sqlQuery = o_riskDAO.createSQLQuery(sql);
        sqlQuery.setString("companyId", companyId);
        sqlQuery.executeUpdate();
        
		return true;
	}

}
