package com.fhd.ra.business.assess.oper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.riskTidy.AdjustHistoryResultDAO;
import com.fhd.entity.assess.riskTidy.AdjustHistoryResult;
import com.fhd.sm.web.controller.util.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Service
public class AdjustHistoryResultBO {

	@Autowired
	private AdjustHistoryResultDAO o_adjustHistoryResultDAO;
	
	/**
	 * 批量保存定性评估结果
	 * @param adjustHistoryResultArrayList 风险记录打分实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveAdjustHistorySql(final List<Object> adjustHistoryResultArrayList) {
        this.o_adjustHistoryResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_adjust_history_result (ID, ADJUST_HISTORY_ID, SCORE, ASSESS_PLAN_ID, SCORE_DIM_ID) " +
                		"VALUES (?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (Object object : adjustHistoryResultArrayList) {
                	AdjustHistoryResult  adjustHistoryResult = (AdjustHistoryResult)object;
                	pst.setString(1, adjustHistoryResult.getId());
                	pst.setString(2, adjustHistoryResult.getAdjustHistoryId());
                	pst.setString(3, adjustHistoryResult.getScore());
                	pst.setString(4, adjustHistoryResult.getRiskAssessPlan().getId());
                	pst.setString(5, adjustHistoryResult.getDimension().getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	
	@Transactional
	public void updateScoreObjectRiskScore(String assessPlanId,final List<Object> adjustHistoryResultArrayList){
		//u
	}
	
	
	/** 
	  * @Description: 保存评估结果记录表
	  * @author jia.song@pcitc.com
	  * @date 2017年4月18日 下午2:37:06 
	  * @param adjustHistoryResultArrayList 
	  */
	@Transactional
    public void saveAdjustHistorySqlSf2(final List<AdjustHistoryResult> adjustHistoryResultArrayList) {
        this.o_adjustHistoryResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_adjust_history_result (ID, SCORE_OBJECT_ID, SCORE_DIM_ID,SCORE_DIM_VALUE,ASSESS_EMP_COUNT,SCORE,ASSESS_PLAN_ID,STATUS,UPDATER) " +
                		"VALUES (?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                for (AdjustHistoryResult adjustHistoryResult : adjustHistoryResultArrayList) {
                	pst.setString(1, Identities.uuid());
                	pst.setString(2, adjustHistoryResult.getRiskScoreObject().getId());
                	pst.setString(3, adjustHistoryResult.getDimension().getId());
                	pst.setString(4, adjustHistoryResult.getDimension().getName());
                	pst.setString(5, adjustHistoryResult.getCount());
                	pst.setString(6, adjustHistoryResult.getScore());
                	pst.setString(7, adjustHistoryResult.getRiskAssessPlan().getId());
                	pst.setString(8, adjustHistoryResult.getStatus());
                	pst.setString(9, adjustHistoryResult.getUpdater());
                    pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	
	/** 
	  * @Description: 保存整理提交的风险保存到打分表中
	  * @author jia.song@pcitc.com
	  * @date 2017年5月24日 下午5:08:31 
	  * @param dimList
	  * @param objectId
	  * @param assessPlanId 
	  */
	public void saveTidyAdjustHistory(final String dimList,final String objectId,final String assessPlanId){
		this.o_adjustHistoryResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_adjust_history_result (ID, SCORE_OBJECT_ID, SCORE_DIM_ID,SCORE_DIM_VALUE,ASSESS_EMP_COUNT,SCORE,ASSESS_PLAN_ID,STATUS,UPDATER) " +
                		"VALUES (?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                JSONArray dimArray = JSONArray.fromObject(dimList);
        		for(int i =0;i < dimArray.size();i++){
        			JSONObject object = (JSONObject) dimArray.get(i);
        			pst.setString(1, Identities.uuid());
                	pst.setString(2, objectId);
                	pst.setString(3, object.get("dimId").toString());
                	pst.setString(4, object.get("dimName").toString());
                	pst.setString(5, "1");
                	pst.setString(6, "1");
                	pst.setString(7, assessPlanId);
                	pst.setString(8, "1");
                	pst.setString(9, "system");
                    pst.addBatch();
        		}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
		
	}
	
	/**
	 * 保存定性评估结果
	 * */
	@Transactional
	public void mergeAdjustHistoryResult(AdjustHistoryResult adjustHistoryResult) {
		o_adjustHistoryResultDAO.merge(adjustHistoryResult);
	}
	
	/**
	 * 删除评估结果记录 
	 * */
	@Transactional
	public void delAdjustHistoryResult(AdjustHistoryResult adjustHistoryResult) {
		o_adjustHistoryResultDAO.delete(adjustHistoryResult);
	}
	
	/**
	 * 删除指标评估结果
	 * */
	@Transactional
	public void deleteAdjustHistory(ArrayList<String> idsArrayList) {
		SQLQuery sqlQuery = o_adjustHistoryResultDAO.createSQLQuery("delete from t_rm_adjust_history_result where id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	@Transactional
	public void deleteAdjustHistoryByAssessPlanId(String assessPlanId){
		SQLQuery sqlQuery = o_adjustHistoryResultDAO.createSQLQuery("delete from t_rm_adjust_history_result where  assess_plan_id = :assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 删除指标评估结果
	 * */
	@Transactional
	public void deleteAdjustHistoryId(ArrayList<String> idsArrayList) {
		SQLQuery sqlQuery = o_adjustHistoryResultDAO.createSQLQuery("delete from t_rm_adjust_history_result where adjust_history_id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 批量更新定性评估结果
	 * @param adjustHistoryResultArrayList 风险记录打分实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
    @Transactional
    public void updateAdjustHistorySql(final List<Object> adjustHistoryResultArrayList) {
        this.o_adjustHistoryResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "update t_rm_adjust_history_result set ADJUST_HISTORY_ID=?, SCORE=?, ASSESS_PLAN_ID=?, SCORE_DIM_ID=? where ID=? ";
                pst = connection.prepareStatement(sql);
                
                for (Object object : adjustHistoryResultArrayList) {
                	AdjustHistoryResult  adjustHistoryResult = (AdjustHistoryResult)object;
                	pst.setString(1, adjustHistoryResult.getAdjustHistoryId());
                	pst.setString(2, adjustHistoryResult.getScore());
                	pst.setString(3, adjustHistoryResult.getRiskAssessPlan().getId());
                	pst.setString(4, adjustHistoryResult.getDimension().getId());
                	pst.setString(5, adjustHistoryResult.getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
    
    /**
     * @author jia.song@pcitc.com
     * @param assessPlanId
     * @date    2017-06-12
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Map<String,String>> findAdjustHistoryResultListByAssessPlanId(String riskId,String assessPlanId){
    	List<Map<String,String>> rtnMap = new ArrayList<Map<String,String>>();
      	Map<String,String> tempMap = null;
    	Criteria criteria = o_adjustHistoryResultDAO.createCriteria();
    	criteria.createAlias("riskScoreObject", "scoreObject");
    	if(StringUtils.isBlank(assessPlanId)){
    		criteria.add(Restrictions.isNull("riskAssessPlan.id"));
    	}else{
    		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
    	}
    	criteria.add(Restrictions.eq("scoreObject.riskId", riskId));
    	List<AdjustHistoryResult> rtnList = criteria.list();
    	//TODO jia.song 0613 ready to set riskstatus
    	for(AdjustHistoryResult result : rtnList ){
    		tempMap = new HashMap<String,String>();
    		tempMap.put("scoreId", result.getDimension().getId());
    		tempMap.put("scoreName", result.getDimension().getName());
    		tempMap.put("score", Utils.retain2Decimals(Double.parseDouble(result.getScore())));
    		rtnMap.add(tempMap);
    	}
    	return rtnMap;
    }
    
	/**
	 * 查询全部评估统计,并已MAP方式存储(风险评估ID--维度ID--评估计划ID,评估统计实体)
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, AdjustHistoryResult> findAdjustHistoryResultAllMap(String assessPlanId){
		HashMap<String, AdjustHistoryResult> map = new HashMap<String, AdjustHistoryResult>();
		Criteria criteria = o_adjustHistoryResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<AdjustHistoryResult> list = null;
		list = criteria.list();
		
		for (AdjustHistoryResult adjustHistoryResult : list) {
			map.put(adjustHistoryResult.getAdjustHistoryId() + "--" + adjustHistoryResult.getDimension().getId() + "--" + 
					adjustHistoryResult.getRiskAssessPlan().getId(), adjustHistoryResult);
		}
		
		return map;
	}
	
	/**
	 * 查询全部评估统计,并已MAP方式存储(风险评估ID,评估统计实体)
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<AdjustHistoryResult>> findAdjustHistoryResultByRiskHistoryIdAllMap(String assessPlanId){
		HashMap<String, List<AdjustHistoryResult>> map = new HashMap<String, List<AdjustHistoryResult>>();
		Criteria criteria = o_adjustHistoryResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<AdjustHistoryResult> list = null;
		list = criteria.list();
		List<AdjustHistoryResult> lists = null;
		for (AdjustHistoryResult adjustHistoryResult : list) {
			if(map.get(adjustHistoryResult.getAdjustHistoryId()) != null){
				map.get(adjustHistoryResult.getAdjustHistoryId()).add(adjustHistoryResult);
			}else{
				lists = new ArrayList<AdjustHistoryResult>();
				lists.add(adjustHistoryResult);
				map.put(adjustHistoryResult.getAdjustHistoryId(), lists);
			}
		}
		
		return map;
	}
	
	/**
	 * 通过历史事件ID查询分值
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	@SuppressWarnings("unchecked")
	public List<AdjustHistoryResult> findRiskAdjustHistoryId(String adjustHistoryId){
		Criteria criteria = o_adjustHistoryResultDAO.createCriteria();
		criteria.add(Restrictions.eq("adjustHistoryId", adjustHistoryId));
		List<AdjustHistoryResult> list = null;
		list = criteria.list();
		
		return list;
	}
	
	
	/** 
	  * @Description: 通过计划id 获取historyresult表中内容
	  * @author jia.song@pcitc.com
	  * @date 2017年5月5日 下午3:05:29 
	  * @param assessPlanId
	  * @return 
	  */
	public List<AdjustHistoryResult> findHistoryResultByAssessPlanId(String assessPlanId){
		Criteria criteria = o_adjustHistoryResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		criteria.addOrder(Order.asc("riskScoreObject.id")).addOrder(Order.asc("dimension.id"));
		return criteria.list();
	}
	
	
	/** 
	  * @Description: 获取计划对应的统计结果数量
	  * @author jia.song@pcitc.com
	  * @date 2017年5月8日 上午9:21:24 
	  * @param assessPlanId
	  * @return 
	  */
	public Integer getHistoryResultCountByAssessPlanId(String assessPlanId){
		Criteria c = o_adjustHistoryResultDAO.createCriteria();
		c.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		c.setProjection(Projections.rowCount());
		return Integer.parseInt(c.uniqueResult().toString());  
	}
	
	/**
	 * @Description delete t_rm_risks_dimension_value by riskid
	 * @author jia.song@pcitc.com
	 * @date     2017-06-15
	 */
	@Transactional
	public void deleteRiskDimensionValuesByRiskId(String riskId){
		SQLQuery sqlQuery = o_adjustHistoryResultDAO.createSQLQuery("delete from t_rm_risks_dimension_value where risk_id =:riskId ");
		sqlQuery.setParameter("riskId", riskId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * @Desciption insert into t_rm_risks_dimension_value by paramMap
	 * @author jia.song@pcitc.com
	 * @Date    2017-6-15
	 */
	//TODO 1
	@Transactional
    public void insertIntoRiskDimensionValues(final JSONArray ja,final String riskId) {
        this.o_adjustHistoryResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
            	JSONObject jsonObj = null;
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_risks_dimension_value (ID, RISK_ID, SCORE_DIM_ID, SCORE_DIM_VALUE, SCORE) " +
                		"VALUES (?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                for (int i = 0;i < ja.size(); i++ ) {
                	jsonObj = ja.getJSONObject(i);
                	pst.setString(1, Identities.uuid());
                	pst.setString(2, riskId);
                	pst.setString(3, jsonObj.getString("scoreId"));
                	pst.setString(4, jsonObj.getString("scoreName"));
                	pst.setString(5,jsonObj.getString("score"));
                    pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	/**
	 * @author jia.song@pcitc.com
	 * @param riskId
	 * @desc    get dimensionvalue by riskid
	 * @return
	 */
	public List<Map<String,String>> findRiskDimensionValuesByRiskId(String riskId){
		List<Map<String,String>> rtnList = new ArrayList<Map<String,String>>();
		Map<String,String> tempMap = null;
		String selectSql = "SELECT id,risk_id,score_dim_id,score_dim_value,score FROM t_rm_risks_dimension_value WHERE RISK_ID = :riskId"; 
		SQLQuery query = o_adjustHistoryResultDAO.createSQLQuery(selectSql);
		query.setString("riskId", riskId); 
		List<Object[]> list = query.list();
		if(list != null && list.size() > 0){
			for(Object[] object : list){
				tempMap = new HashMap<String,String>();
				tempMap.put("scoreId", object[2].toString());
	    		tempMap.put("scoreName", object[3].toString());
	    		tempMap.put("score", object[4].toString());
	    		rtnList.add(tempMap);
			}
		}
		return rtnList;
	}
	
}
