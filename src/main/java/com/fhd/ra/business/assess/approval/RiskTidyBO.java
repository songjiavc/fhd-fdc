package com.fhd.ra.business.assess.approval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.formula.FormulaLogBO;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.kpiSet.RangObjectDeptEmpDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.icm.tempimport.TempControlStandard;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.util.MoreRoleDeptSummarizing;
import com.fhd.ra.business.assess.util.RiskLevelBO;

@Service
public class RiskTidyBO {

	@Autowired
	private RangObjectDeptEmpDAO o_rangObjectDeptEmpDAO;
	
	@Autowired
	private FormulaLogBO o_formulaLogBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private AssessDateInitBO o_assessDateInitBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private RiskAssessPlanBO o_planBO;
	
	/**
	 * 该评估计划下需要评估的部门ID
	 * @param assessPlanId 评估计划ID
	 * @return ArrayList<String>
	 * @author 金鹏祥
	 * */
	public ArrayList<String> findNeedAssessDepts(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		
		ArrayList<String> arrayList = new ArrayList<String>();
		
        sql.append(" select ORG_ID,ORG_ID from T_RM_RISK_SCORE_DEPT where SCORE_OBJECT_ID in ");
        sql.append(" (select id from t_rm_risk_score_object where ASSESS_PLAN_ID = :assessPlanId) GROUP BY org_id ");
      
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String deptId = "";//部门ID
           
            if(null != objects[0]){
            	deptId = objects[0].toString();
            }
            arrayList.add(deptId);
        }
		
        return arrayList;
	}
	
	/**
	 * 该评估计划下需要评估的风险数量
	 * */
	public Integer findNeedAssessRisk(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		
        sql.append(" select * from T_RM_RANG_OBJECT_DEPT_EMP a, ");
        sql.append(" (select * from T_RM_RISK_SCORE_DEPT  ");
        sql.append(" where org_id in ");
        sql.append(" (select ORG_ID from T_RM_RISK_SCORE_DEPT where SCORE_OBJECT_ID  ");
        sql.append(" in (select id from t_rm_risk_score_object where ASSESS_PLAN_ID = :assessPlanId) GROUP BY org_id)) b ");
        sql.append(" where a.score_dept_id = b.id ");
      
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        return list.size();
	}
	
	/**
	 * 该评估计划下已被评估的风险数量
	 * */
	public Integer findAlreadyAssessRisk(String assessPlanId){
		StringBuffer sql = new StringBuffer();
        
        sql.append(" select * from t_rm_score_result where rang_object_dept_emp_id in( ");
        sql.append(" select a.id from T_RM_RANG_OBJECT_DEPT_EMP a, ");
        sql.append(" (select * from T_RM_RISK_SCORE_DEPT  ");
        sql.append(" where org_id in  ");
        sql.append(" (select ORG_ID from T_RM_RISK_SCORE_DEPT where SCORE_OBJECT_ID  ");
        sql.append(" in (select id from t_rm_risk_score_object where ASSESS_PLAN_ID = :assessPlanId) GROUP BY org_id)) b ");
        sql.append(" where a.score_dept_id = b.id ");
        sql.append(" ) AND is_approval = '1' GROUP BY rang_object_dept_emp_id ");
        
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        return list.size();
	}
	
	/**
	 * 通过风险ID,得到同一部门打分的风险
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<String> getDeptRisks(ArrayList<HashMap<String, Object>> mapsList, String riskId){
		ArrayList<String> arrayList = new ArrayList<String>();
		
		for (HashMap<String, Object> hashMap : mapsList) {
			if(riskId.equalsIgnoreCase(hashMap.get("riskId").toString())){
				arrayList.add(hashMap.get("rangObjectDeptEmpId") + "--" + hashMap.get("deptId")); 
			}
		}
		return arrayList;
	}
	
	/**
	 * 风险评估全部部门风险存储到统计结果表中
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @param statisticsResultAllMap 该评估记录下的打分统计结果
	 * @param formulaSet 公式计算实体
	 * @param templateRelaDimensionMapList 查询模版关联维度(主维度)
	 * @return List<StatisticsResult>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings({"unused" })
	@Transactional
	public List<StatisticsResult> getRiskReSummarizingList(String assessPlanId, String companyId, 
			HashMap<String, StatisticsResult> statisticsResultAllMap, FormulaSet formulaSet, 
			List<Map<String, Object>> templateRelaDimensionMapList) {
		HashMap<String, String> filtrateMap = new HashMap<String, String>(); //记录是否已执行该风险键值
		HashMap<String, HashMap<String, Double>> riskDimValueMap = new HashMap<String, HashMap<String, Double>>(); //该风险打分维度及分值键值
		int count = 1; //计数
		ArrayList<String> deptRisksList = null; //记录该风险被评分人与部门
		FormulaLog formulaLog = null; //公式日志
		List<StatisticsResult> statisticsResultList = new ArrayList<StatisticsResult>(); //数据信息返回总分实体集合
		ApplyContainer applyContainer = o_assessDateInitBO.getCollectDateInit(companyId, assessPlanId, formulaSet);
		
		for (HashMap<String, Object> hashMap : applyContainer.getMapsList()) {
			String riskId = hashMap.get("riskId").toString();
			String rangObjectDeptEmpId = hashMap.get("rangObjectDeptEmpId").toString();
			String tempId = hashMap.get("templateId").toString();
			HashMap<String, Double> map = null;
			ArrayList<String> scoreValueTempList = null;
			double riskLevel = 0;
			String riskLevelStr = "";
			
			try {
				if(filtrateMap.get(riskId) != null){
					continue;
				}
				
	            deptRisksList = this.getDeptRisks(applyContainer.getMapsList(), riskId);
	            
	            if(deptRisksList.size() > 1){
	            	//多部门打分
	            	HashMap<String, Double> dimValueMap = 
	            			MoreRoleDeptSummarizing.getDynamicDimValueMap("dept", riskId, tempId, deptRisksList, applyContainer);
	            	
	            	map = new HashMap<String, Double>();
	            	
	            	for (String dimId : applyContainer.getDimIdArrayList()) {
	            		if(hashMap.get(dimId) != null){
	            			map.put(dimId, Double.parseDouble(hashMap.get(dimId).toString()));
	            		}
					}
	            	
	            	if(map.size() == 0){
	            		double scoreValue = 0L;
	                 	for (Map<String, Object> keys : templateRelaDimensionMapList) {
	                 		map.put(keys.get("dimId").toString(), scoreValue);
	     				}
	            	}
	            	
	            	riskDimValueMap.put(riskId, dimValueMap);
	            	filtrateMap.put(riskId, riskId);
	            }else{
	            	//单部门打分
	            	map = new HashMap<String, Double>();
	            	
	            	for (String dimId : applyContainer.getDimIdArrayList()) {
	            		if(hashMap.get(dimId) != null){
	            			map.put(dimId, Double.parseDouble(hashMap.get(dimId).toString()));
	            		}
					}
	            	
	            	if(map.size() == 0){
	            		double scoreValue = 0L;
	                 	for (Map<String, Object> keys : templateRelaDimensionMapList) {
	                 		map.put(keys.get("dimId").toString(), scoreValue);
	     				}
	            	}
	            	
	            	riskDimValueMap.put(riskId, map);
	            }
	            
	            count++;
	            
	            scoreValueTempList = new ArrayList<String>();
	            Set<Entry<String, Double>> key = map.entrySet();
	            for (Iterator<Entry<String, Double>> it = key.iterator(); it.hasNext();) {
	                String keys = it.next().getKey();
	                scoreValueTempList.add(keys + "--" + map.get(keys));
	            }
	            
	            riskLevelStr = o_riskLevelBO.getRisklevel(scoreValueTempList, applyContainer.getRiskLevelFormula(), applyContainer.getDimIdAllList());
	            if(riskLevelStr.indexOf("--") != -1){
	            	//失败
                	String formulaContent = riskLevelStr;
                	formulaLog = new FormulaLog();
                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(
                			formulaLog, riskId, applyContainer.getRiskAllMap().get(riskId).getName(), "risk",
                            "assessValueFormula", formulaContent, "failure", null, null, null));
	            }else{
	            	
	            	if(Double.parseDouble(riskLevelStr) == -1){
	            		//失败
	                	String formulaContent = riskLevelStr;
	                	formulaLog = new FormulaLog();
	                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(
	                			formulaLog, riskId, applyContainer.getRiskAllMap().get(riskId).getName(), "risk",
	                            "assessValueFormula", formulaContent, "failure", null, null, null));
	            	}else{
	            		//成功
	                	String formulaContent = applyContainer.getRiskLevelFormulaText();
	                	formulaLog = new FormulaLog();
	                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(
	                			formulaLog, riskId, applyContainer.getRiskAllMap().get(riskId).getName(), "risk",
	                            "assessValueFormula", formulaContent, "success", String.valueOf(riskLevelStr), null, null));
	            	}
	            }
			} catch (Exception e) {
				//失败
            	String formulaContent = e.getMessage();
            	formulaLog = new FormulaLog();
            	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, riskId, applyContainer.getRiskAllMap().get(riskId).getName(), "risk",
                        "assessValueFormula", formulaContent, "failure", null, null, null));
			}
		}
		
		Set<Entry<String, HashMap<String, Double>>> key = riskDimValueMap.entrySet();
        for (Iterator<Entry<String, HashMap<String, Double>>> it = key.iterator(); it.hasNext();) {
        	String riskId = it.next().getKey();
            HashMap<String, Double> dimValueMap = riskDimValueMap.get(riskId);
            
            Set<Entry<String, Double>> key2 = dimValueMap.entrySet();
	        for (Iterator<Entry<String, Double>> it2 = key2.iterator(); it2.hasNext();) {
	            String dimId = it2.next().getKey();
	            double dimValue = dimValueMap.get(dimId);
	            
	            StatisticsResult statisticsResult = new StatisticsResult();
				Dimension dimension = new Dimension();
				RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
				
				riskAssessPlan.setId(assessPlanId);
				dimension.setId(dimId);
				statisticsResult.setDimension(dimension);
				if(statisticsResultAllMap.get(riskId) != null){
					statisticsResult.setId(statisticsResultAllMap.get(riskId).getId());
				}else{
					statisticsResult.setId(Identities.uuid());
				}
				
				if(dimValue != 0){
					statisticsResult.setScore(String.valueOf(dimValue));	
				}
				
				statisticsResult.setRiskAssessPlan(riskAssessPlan);
				statisticsResult.setRiskScoreObject(riskId);
				statisticsResult.setStatus(null);
				statisticsResultList.add(statisticsResult);
	        }
        }
		
		return statisticsResultList;
	}
	
	/** 
	  * @Description: 当风险评估流程结束后更新相关业务数据
	  * @author jia.song@pcitc.com
	  * @date 2017年5月11日 下午2:28:26 
	  * @param assessPlanId
	  * @return 
	  */
	@Transactional
	public boolean finishAccessPlan(String assessPlanId){
		boolean flag = false;
		//更新评估计划状态
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
		assessPlan.setDealStatus(Contents.DEAL_STATUS_FINISHED);//点结束工作流按钮，处理状态为"已完成"
		o_planBO.mergeRiskAssessPlan(assessPlan);
		updateRiskScoreFromRiskList(assessPlanId);
		return flag;
	}
	
 	/**
 	 * @author jia.song@pcitc.com
 	 * @desc update riskscorebyplan
 	 * @param assessPlanId
 	 */
 	@Transactional
    public void updateRiskScoreFromRiskList(String assessPlanId) {
 		String updateSql = " UPDATE t_rm_risks risk INNER JOIN temp_sync_riskrbs_score score ON risk.ID = score.RISK_ID set risk.RISK_SCORE = score.RISK_SCORE,risk.ASSESS_PLAN_ID=:assessPlanId  WHERE score.ASSESS_PLAN_ID = :assessPlanId ";
 		o_rangObjectDeptEmpDAO.getSession().createSQLQuery(updateSql).setParameter("assessPlanId", assessPlanId).executeUpdate();  //
    }
 	
 	/**
 	 * @author jia.song@pcitc.com
 	 * @desc     sync riskscore by list
 	 * @param paramMap
 	 */
 	@Transactional
 	public void syncRiskRbsScoreFromList(final List<Map<String,Object>> paramMap,final String assessPlanId){
 		o_rangObjectDeptEmpDAO.getSession().doWork(new Work(){
 			public void execute(Connection connection) throws SQLException {
 				connection.setAutoCommit(false);
 				String sql = "insert into temp_sync_riskrbs_score(RISK_ID,RISK_SCORE,ASSESS_PLAN_ID) values(?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				if(paramMap != null && paramMap.size() > 0){
		 			for(Map<String,Object> o : paramMap){
		 				pst.setObject(1, o.get("id"));
						pst.setDouble(2, Double.parseDouble(o.get("riskScore").toString()));
						pst.setString(3, assessPlanId);
						pst.addBatch();
		 			}
		 			pst.executeBatch();
					connection.commit();
					connection.setAutoCommit(true);
		 		}
 			}
 		}); 
 	}
 	
 	/**
 	 * @author jia.song
 	 * @desc    delete syncriskscore by assessplanid
 	 * @param assessPlanId
 	 */
 	@Transactional
 	public void deleteSyncRIskScoreByAssessPlanId(String assessPlanId){
 		String deleteSql = "DELETE FROM temp_sync_riskrbs_score WHERE assess_plan_id = :assessPlanId";
 	    o_rangObjectDeptEmpDAO.getSession().createSQLQuery(deleteSql).setParameter("assessPlanId", assessPlanId).executeUpdate();
 	}
 	
	/** 
	  * @Description: 给风险分类赋值
	  * @author jia.song@pcitc.com
	  * @date 2017年5月15日 上午9:31:25
	  * @param assessPlanId
	  * @return 
	  */
	public List<Map<String,Object>> updateReAndRbsByObject(String assessPlanId){
		List<Map<String,Object>> rtnList = this.findRbsAndReFromObject(assessPlanId);
		String parentId = null;
		recursionSum(parentId,rtnList);
		return rtnList;
	}
	
	/**
	 * @author jia.song@pcitc.com
	 * @date     2017-6-7
	 * @param ridy 
	 */
	public void execRiskTidy(String assessPlanId){
		List<Map<String,Object>> rtnList = updateReAndRbsByObject(assessPlanId);
		this.deleteSyncRIskScoreByAssessPlanId(assessPlanId);
		this.syncRiskRbsScoreFromList(rtnList,assessPlanId);
	}
	
	
	/** 
	  * @Description: 查找所有打分的风险事件和关联的风险分类
	  * @author jia.song@pcitc.com
	  * @date 2017年5月12日 下午3:28:18 
	  * @param assessPlanId
	  * @return 
	  */
	private List<Map<String,Object>> findRbsAndReFromObject(String assessPlanId){
		List<Map<String,Object>> rtnList = new ArrayList<Map<String,Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" objects.RISK_ID,objects.PARENT_ID,objects.RISK_SCORE,objects.is_risk_class ");
		sql.append(" FROM ");
		sql.append(" t_rm_risk_score_object objects " );
		sql.append(" WHERE ");
		sql.append(" objects.ASSESS_PLAN_ID = :assessPlanId ");
		sql.append(" UNION ALL ");
		sql.append(" SELECT  ");
		sql.append(" DISTINCT risks1.ID,risks1.PARENT_ID,risks1.RISK_SCORE,risks1.is_risk_class ");
		sql.append(" FROM  t_rm_risks risks1,t_rm_risk_score_object objects  ");
		sql.append(" WHERE objects.id_seq like concat(risks1.id_seq,'%')  AND objects.ASSESS_PLAN_ID = :assessPlanId  AND  risks1.IS_RISK_CLASS = 'rbs' ");
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		Map<String,Object> map = null;
		if(list != null && list.size() > 0){
			for(Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				map = new HashMap<String, Object>();
				if(objects[0] != null){
					map.put("id", objects[0]);
				}
				map.put("parentId", objects[1]);
				map.put("riskScore", objects[2]);
				if(objects[3] != null){
					map.put("isRiskClass", objects[3]);
				}
				rtnList.add(map);
			}
		}
		return rtnList;
	}
	
	
	/** 
	  * @Description: 根据parentId 获取子集合
	  * @author jia.song@pcitc.com
	  * @date 2017年5月12日 下午3:38:16 
	  * @param parentId
	  * @param paramList
	  * @return 
	  */
	private Map<String,Object> findRbsAndReFromObjectByParentId(String parentId,List<Map<String,Object>> paramList){
		int i = 0;
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		List<Map<String,Object>> rtnList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> paramMap : paramList){
			if(paramMap.get("parentId") == null || parentId == null){
				if(parentId == paramMap.get("parentId")){
					i++;
					rtnList.add(paramMap);
				}
			}else{
				if(parentId.equals(paramMap.get("parentId").toString())){
					i++;
					rtnList.add(paramMap);
				}
			}
		}
		rtnMap.put("count", i);
		rtnMap.put("rtnList", rtnList);
		return rtnMap;
	}
	
	/** 
	  * @Description: 获取根节点
	  * @author jia.song@pcitc.com
	  * @date 2017年5月12日 下午3:40:41 
	  * @param paramList
	  * @return 
	  */
	private Map<String,Object> getRootRiskRbs(List<Map<String,Object>> paramList){
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		for(Map<String,Object> paramMap : paramList){
			if(paramMap.get("parentId") == null){
				rtnMap = paramMap;
				break;
			}
		}
		return rtnMap;
	}
	
	/** 
	  * @Description: 对对应的风险分类赋值
	  * @author jia.song@pcitc.com
	  * @date 2017年5月15日 上午9:20:41 
	  * @param riskScore
	  * @param parentId
	  * @param totalList 
	  */
	private void setRiskScoreToList(double riskScore,String parentId,List<Map<String,Object>> totalList){
		for(Map<String,Object> tempMap : totalList){
			if(tempMap.get("id").equals(parentId)){
				tempMap.put("riskScore", Double.toString(riskScore));
				break;
			}
		}
	}
	
	private double getRiskScoreByRiskId(String riskId,List<Map<String,Object>> totalList){
		double rtn = 0.0;
		for(Map<String,Object> map : totalList){
			if(map.get("id").toString().equals(riskId)){
				if(map.get("riskScore") != null){
					rtn = Double.parseDouble(map.get("riskScore").toString());
				}
				break;
			}
		}
		return rtn;
	}
	
	/** 
	  * @Description: 递归方式在内存中计算风险分类的风险分值
	  * @author jia.song@pcitc.com
	  * @date 2017年5月15日 上午9:23:58 
	  * @param parentId
	  * @param paramList
	  * @param totalList
	  */
	private double recursionSum(String parentId,List<Map<String,Object>> totalList){
		Map<String,Object> map = this.findRbsAndReFromObjectByParentId(parentId, totalList);
		int count = Integer.parseInt(map.get("count").toString());
		double tempScore = 0.0;
		List<Map<String,String>> tempList = (List<Map<String,String>>)map.get("rtnList");
		if(count > 0){
			for(Map<String,String> tempMap : tempList){
				String tempParentId = tempMap.get("id");
				tempScore += this.recursionSum(tempParentId,totalList);
			}
			setRiskScoreToList(tempScore/count,parentId,totalList);
		}
		return getRiskScoreByRiskId(parentId,totalList);
	}
}