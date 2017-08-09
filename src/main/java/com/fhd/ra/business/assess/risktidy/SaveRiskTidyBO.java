package com.fhd.ra.business.assess.risktidy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.kpiSet.ScoreObjectDeptEmpDAO;
import com.fhd.dao.assess.quaAssess.ScoreResultDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.assess.riskTidy.AdjustHistoryResult;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.StatisticsResultBO;
import com.fhd.ra.business.assess.quaassess.QuaAssessBO;

@Service
public class SaveRiskTidyBO {

	@Autowired
	private QuaAssessBO o_quaAssessBO;
	
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	@Autowired
	private ScoreResultDAO o_scoreResultDAO;
	
	@Autowired
	private ScoreObjectDeptEmpDAO o_ScoreObjectDeptEmpDAO;
	
	@Autowired
	private ScoreObjectBO o_scoreObiectBO;
	
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private AdjustHistoryResultBO o_adjustHistoryResultBO;
	
	/**
	 * 通过打分对象ID得到综合对象
	 * @param scoreObjectId 打分对象ID
	 * @return List<RangObjectDeptEmp>
	 * @author 金鹏祥
	 * */
	private List<RangObjectDeptEmp> findRangObjectDeptEmpByScoreObjectId(String scoreObjectId){
		Criteria criteria = o_ScoreObjectDeptEmpDAO.createCriteria();
		criteria.add(Restrictions.eq("scoreObject.id", scoreObjectId));		
		@SuppressWarnings("unchecked")
		List<RangObjectDeptEmp> list = criteria.list();
		
		return list;
	}
	
	/**
	 * 综合ID得到打分结果对象
	 * @param scoreObjectDeptEmpId 综合ID
	 * @return List<ScoreResult>
	 * @author 金鹏祥
	 * */
	private List<ScoreResult> findScoreResultByScoreObjectDeptEmpId(String scoreObjectDeptEmpId){
		Criteria criteria = o_scoreResultDAO.createCriteria();
		criteria.add(Restrictions.eq("rangObjectDeptEmpId.id", scoreObjectDeptEmpId));		
		@SuppressWarnings("unchecked")
		List<ScoreResult> list = criteria.list();
		
		return list;
	}
	
	
	
	/**
	 * 通过ID查询打分结果表
	 * @param assessPlanID 评估计划ID
	 * @return ScoreResult
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unused")
	private ScoreResult findScoreResultById(String assessPlanID){
		Criteria criteria = o_scoreResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanID));
		
		return (ScoreResult) criteria.list().get(0);
	}
	
	@Transactional
	public boolean saveAssessSf2(final String params, final String assessPlanId){
		o_scoreResultDAO.getSession().doWork(new Work() {
	        public void execute(Connection connection) throws SQLException {
	            connection.setAutoCommit(false);
	            PreparedStatement pst = null;
				JSONObject jo = JSONObject.fromObject(params);
				String scoreDimId = jo.get("scoreDimId").toString(); //打分维度ID
				String score = jo.get("score").toString(); //分值
				String objectId = jo.get("objectId").toString(); //风险
				String sql = "UPDATE T_RM_ADJUST_HISTORY_RESULT SET SCORE=? WHERE ASSESS_PLAN_ID = ? AND SCORE_DIM_ID = ? AND SCORE_OBJECT_ID = ?";
				pst = connection.prepareStatement(sql);
				pst.setString(1, score);
				pst.setString(2, assessPlanId);
				pst.setString(3, scoreDimId);
				pst.setString(4, objectId);
				pst.executeUpdate();
				connection.setAutoCommit(true);
	        }
		});
		return true;
	}
	
	/**
	 * 保存评估
	 * @param params 风险信息参数
	 * @param assessPlanId 评估计划ID
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean saveAssess(String params, String assessPlanId){
		boolean isBool = false;
		
		JSONArray jsonarr = JSONArray.fromObject(params);
		try {
			for (Object objects : jsonarr) {
				JSONObject jsobjs = (JSONObject) objects;
				String scoreDimId = jsobjs.get("scoreDimId").toString(); //打分维度ID
				String score = jsobjs.get("score").toString(); //分值
				String riskId = jsobjs.get("riskId").toString(); //风险
				
				StatisticsResult statisticsResult = o_statisticsResultBO.findStatisticsResultBySome(assessPlanId, scoreDimId, riskId);
				if(null == statisticsResult){
					//新建立综合打分实体
					statisticsResult = new StatisticsResult();
					statisticsResult.setId(Identities.uuid());
					RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
					riskAssessPlan.setId(assessPlanId);
					
					Dimension dimension = new Dimension();
					dimension.setId(scoreDimId);
					
					statisticsResult.setRiskAssessPlan(riskAssessPlan);
					statisticsResult.setDimension(dimension);
					statisticsResult.setScore(score);
					statisticsResult.setRiskScoreObject(riskId);
				}else{
					//编辑打分实体
					RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
					riskAssessPlan.setId(assessPlanId);
					
					Dimension dimension = new Dimension();
					dimension.setId(scoreDimId);
					
					statisticsResult.setRiskAssessPlan(riskAssessPlan);
					statisticsResult.setDimension(dimension);
					statisticsResult.setRiskScoreObject(riskId);
					statisticsResult.setScore(score);
				}
				//更新打分实体
				o_statisticsResultBO.mergeStatisticsResult(statisticsResult);
			}
			
			isBool = true;
		}catch (Exception e) {
			return isBool;
		}
		
		return isBool;
	}
	
	/**
	 * 删除风险事件
	 * @param params 风险参数
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	@RecordLog("删除评价风险数据")
	public void delAssessRiskRe(String params, String assessPlanId){
		HashMap<String, RiskAdjustHistory> riskAdjustHistoryByRiskIdAllMap = 
				o_riskAdjustHistoryBO.findRiskAdjustHistoryByRiskIdAllMap(assessPlanId);
		HashMap<String, List<AdjustHistoryResult>> adjustHistoryResultByRiskHistoryIdAllMap = 
				o_adjustHistoryResultBO.findAdjustHistoryResultByRiskHistoryIdAllMap(assessPlanId);
		String riskIds[] = params.split(",");
		
		for (String riskId : riskIds) {
			//删除风险评估记录表
			RiskAdjustHistory riskAdjustHistory = riskAdjustHistoryByRiskIdAllMap.get(riskId);
			if(riskAdjustHistory != null){
				o_riskAdjustHistoryBO.delRiskAdjustHistory(riskAdjustHistory);
				
				//删除评估结果表
				List<AdjustHistoryResult> adjustHistoryResultList = adjustHistoryResultByRiskHistoryIdAllMap.get(riskAdjustHistory.getId());
				for (AdjustHistoryResult adjustHistoryResult2 : adjustHistoryResultList) {
					o_adjustHistoryResultBO.delAdjustHistoryResult(adjustHistoryResult2);
				}
			}
			
			//删除统计打分结果表
			List<StatisticsResult> statisticsResultList = o_statisticsResultBO.findStatisticsByRiskScoreObject(riskId, assessPlanId);
			for (StatisticsResult statisticsResult : statisticsResultList) {
				o_quaAssessBO.delStatisticsResult(statisticsResult);
			}
			
			//查询打分对象
			RiskScoreObject riskScoreObject = o_scoreObiectBO.findRiskScoreObjectByRiskId(riskId, assessPlanId);
			
			//删除打分对象表
			List<ScoreResult> scoreResultList = this.findScoreResultByScoreObjectDeptEmpId(riskScoreObject.getId());
			for (ScoreResult scoreResult : scoreResultList) {
				o_quaAssessBO.delScoreResult(scoreResult);
			}
			
			//删除综合表
			List<RangObjectDeptEmp> rangObjectDeptEmpList = this.findRangObjectDeptEmpByScoreObjectId(riskScoreObject.getId());
			for (RangObjectDeptEmp rangObjectDeptEmp : rangObjectDeptEmpList) {
				o_quaAssessBO.delRangObjectDeptEmp(rangObjectDeptEmp);
			}
			
			//删除打分部门
			List<RiskScoreDept> riskScoreDeptList = o_scoreDeptBO.findRiskScoreDeptByScoreObjectId(riskScoreObject.getId());
			for (RiskScoreDept riskScoreDept : riskScoreDeptList) {
				o_quaAssessBO.delRiskScoreDept(riskScoreDept);
			}
			
			//删除打分对象
			//o_quaAssessBO.delRiskScoreObject(riskScoreObject);
			//逻辑删除打分对象 
			//吉志强 2017年4月24日11:28:21修改
			riskScoreObject.setDeleteStatus("0");
			o_quaAssessBO.updateRiskScoreObject(riskScoreObject);
		}
	}
	
	
	
	/** 
	  * @Description: 辨识汇总中删除风险评估记录
	  * @author jia.song@pcitc.com
	  * @date 2017年5月10日 下午5:10:06 
	  * @param params
	  * @param assessPlanId 
	  */
	@Transactional
	public void delAssessRiskReSf2(List<String> objectIdList, String assessPlanId){
		// 宋佳 辨识汇总删除风险
		String sql = "DELETE FROM t_rm_adjust_history_result WHERE score_object_id IN (:objectIdList)";
		o_scoreResultDAO.createSQLQuery(sql).setParameterList("objectIdList", objectIdList).executeUpdate();
	}
	
	@Transactional
	public void delAssessRiskReSf2ForSecurity(List<String> objectIdList, String assessPlanId){
		// 宋佳 辨识汇总删除风险
		String sql = "DELETE FROM t_rm_adjust_history_result WHERE score_object_id IN (:objectIdList)";
		o_scoreResultDAO.createSQLQuery(sql).setParameterList("objectIdList", objectIdList).executeUpdate();
		String deleteObject = "DELETE FROM t_rm_risk_score_object WHERE id IN (:objectIdList)";
		o_scoreResultDAO.createSQLQuery(deleteObject).setParameterList("objectIdList", objectIdList).executeUpdate();
	}
	
}