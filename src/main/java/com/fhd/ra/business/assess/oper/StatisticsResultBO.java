package com.fhd.ra.business.assess.oper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.risk.Dimension;

@Service
public class StatisticsResultBO {

	@Autowired
	private StatisticsResultDAO o_statisticsResultDAO;
	
	/**
	 * 保存综合打分结果
	 * @param statisticsResult 问卷统计结果
	 * @author 金鹏祥
	 * */
	@Transactional
	public void mergeStatisticsResult(StatisticsResult statisticsResult) {
		o_statisticsResultDAO.merge(statisticsResult);
	}
	
	/**
	 * 批量保存统计打分结果
	 * @param statisticsResultList 总体打分实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveStatisticsResultSql(final List<StatisticsResult> statisticsResultList) {
        this.o_statisticsResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_statistics_result (id,score_dim_id,assess_plan_id,score_object_id,score,status) values(?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (StatisticsResult statisticsResult : statisticsResultList) {
                	pst.setString(1, Identities.uuid());
                    pst.setString(2, statisticsResult.getDimension().getId());
                    pst.setString(3, statisticsResult.getRiskAssessPlan().getId());
                    pst.setString(4, statisticsResult.getRiskScoreObject());
                    pst.setString(5, statisticsResult.getScore());
                    pst.setString(6, statisticsResult.getStatus());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 更新风险级别从评估计划
	 */
	@Transactional
	public void updateRiskEventByAssessPlanId(String assessPlanId){
		if(StringUtils.isNotBlank(assessPlanId)){
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT ob.RISK_ID,r.DELETE_ESTATUS FROM t_rm_risk_score_object ob")
			.append(" LEFT JOIN t_rm_risks r on ob.RISK_ID = r.ID")
			.append(" WHERE ob.assess_plan_id = :assessPlanId AND r.DELETE_ESTATUS = '2'");
			SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
			sqlQuery.setParameter("assessPlanId", assessPlanId);
	        @SuppressWarnings("unchecked")
			List<Object[]> list = sqlQuery.list();
			ArrayList<String> idsArrayList = new ArrayList<String>();
			for(Object[] o :list){
				if(o[0]!=null&&o[1]!=null){
					idsArrayList.add(o[0].toString());
				}
			}
			if(idsArrayList.size()>0){
				String exeSql = " UPDATE t_rm_risks  SET DELETE_ESTATUS='1' where ID in(:ids)";
				SQLQuery sqlQuery2 = this.o_statisticsResultDAO.createSQLQuery(exeSql);
				sqlQuery2.setParameterList("ids", idsArrayList);
				sqlQuery2.executeUpdate();
			}
		}
	}
	
	/**
	 * 删除综合评估结果
	 * */
	@Transactional
	public void deleteStatisticsResultAdjustHistory(String assessPlanId) {
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery("delete from t_rm_statistics_result where assess_plan_id = :assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 通过对象字符串ID查询统计打分结果表
	 * */
	public List<StatisticsResult> findStatisticsByRiskScoreObject(String riskScoreObject, String assessPlanId){
		Criteria criteria = o_statisticsResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskScoreObject", riskScoreObject));
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		@SuppressWarnings("unchecked")
		List<StatisticsResult> list = criteria.list();
		
		return list;
	}
	
	/**
	 * 通过维度ID,评估ID,风险ID查询统计打分结果数据
	 * @param assessPlanId 评估计划ID
	 * @param scoreDimId 打分维度
	 * @param riskId 风险ID
	 * @return StatisticsResult
	 * @author 金鹏祥
	 * */
	public StatisticsResult findStatisticsResultBySome(String assessPlanId, String scoreDimId, String riskId){
		Criteria criteria = o_statisticsResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		criteria.add(Restrictions.eq("dimension.id", scoreDimId));
		criteria.add(Restrictions.eq("riskScoreObject", riskId));
		@SuppressWarnings("unchecked")
		List<StatisticsResult> lists = criteria.list();
		
		if(lists.size() != 0){
			return (StatisticsResult) lists.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 删除打分综合结果表
	 * */
	@Transactional
	public boolean delStatisticsResult(String assessPlanId){
		boolean isBool = false;
		try {
			SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery("delete from t_rm_statistics_result where assess_plan_id = ?", assessPlanId);
			sqlQuery.executeUpdate();
			isBool = true;
		} catch (Exception e) {
			System.out.println("==========删除统计打分表t_rm_statistics_result出错:" + e.getMessage());
			return isBool;
		}
		
		return isBool;
	}
	
	/**
	 * 批量修改统计打分结果
	 * @param statisticsResultList 总体打分实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void updateStatisticsResultSql(final List<StatisticsResult> statisticsResultList) {
        this.o_statisticsResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "update t_rm_statistics_result set score_dim_id=?,assess_plan_id=?,score_object_id=?,score=?,status=? where id=?";
                pst = connection.prepareStatement(sql);
                
                for (StatisticsResult statisticsResult : statisticsResultList) {
                    pst.setString(1, statisticsResult.getDimension().getId());
                    pst.setString(2, statisticsResult.getRiskAssessPlan().getId());
                    pst.setString(3, statisticsResult.getRiskScoreObject());
                    pst.setString(4, statisticsResult.getScore());
                    pst.setString(5, statisticsResult.getStatus());
                    pst.setString(6, statisticsResult.getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量删除统计打分结果
	 * */
	@Transactional
    public void deleteStatisticsResultSql(final List<String> assessPlanIdList) {
        this.o_statisticsResultDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_statistics_result where assess_plan_id=?";
                pst = connection.prepareStatement(sql);
                
                for (String str : assessPlanIdList) {
                    pst.setString(1, str);
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 删除指标评估结果
	 * */
	@Transactional
	public void deleteStatisticsResult(String assessPlanId) {
		SQLQuery sqlQuery =  o_statisticsResultDAO.createSQLQuery("delete from t_rm_statistics_result where assess_plan_id =:assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 查询该评估记录下的打分统计结果
	 * @param assessPlanId 评估计划
	 * @return HashMap<String, StatisticsResult>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, StatisticsResult> findStatisticsResultByAssessPlanIdAllMap(String assessPlanId){
		Criteria criteria = o_statisticsResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<StatisticsResult> statisticsResultList = criteria.list();
		HashMap<String, StatisticsResult> map = new HashMap<String, StatisticsResult>();
		for (StatisticsResult statisticsResult : statisticsResultList) {
			map.put(statisticsResult.getRiskScoreObject(), statisticsResult);
		}
		
		return map;
	}
	
	/**
	 * 查询该评估计划下打分对象有多少个
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findStatisticsResultGroupByAssessPlanIdListAll(){
		StringBuffer sql = new StringBuffer();
		sql.append(" select id,score_object_id from t_rm_statistics_result GROUP BY score_object_id ");
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        HashMap<String, String> map = new HashMap<String, String>();
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            //String id = "";
            String scoreObjectId = "";
            
            if(null != objects[1]){
            	scoreObjectId = objects[1].toString();
            }
            
            map.put(scoreObjectId, scoreObjectId);
        }
		
		return map;
	}
	
	/**
	 * 查询评估记录下的打分结果，返回所有的维度
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<StatisticsResult>> findStatisticsResultListByAssessPlanIdAllMap(String assessPlanId){
		Criteria criteria = o_statisticsResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<StatisticsResult> statisticsResultList = criteria.list();
		HashMap<String, List<StatisticsResult>> map = new HashMap<String, List<StatisticsResult>>();
		for (StatisticsResult statisticsResult : statisticsResultList) {
			if(map.get(statisticsResult.getRiskScoreObject()) != null){
				map.get(statisticsResult.getRiskScoreObject()).add(statisticsResult);
            }else{
            	ArrayList<StatisticsResult> arrayList = new ArrayList<StatisticsResult>();
            	arrayList.add(statisticsResult);
            	map.put(statisticsResult.getRiskScoreObject(), arrayList);
            }
		}
		return map;
	}
	
	/**
	 * 查询该评估记录下的打分统计结果
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, StatisticsResult>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, StatisticsResult> findStatisticsResultByAssessPlanIdByDimIdAllMap(String assessPlanId){
		Criteria criteria = o_statisticsResultDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<StatisticsResult> statisticsResultList = criteria.list();
		HashMap<String, StatisticsResult> map = new HashMap<String, StatisticsResult>();
		for (StatisticsResult statisticsResult : statisticsResultList) {
			map.put(statisticsResult.getRiskScoreObject() + "--" + statisticsResult.getDimension().getId(), statisticsResult);
		}
		
		return map;
	}
	
	/**
	 * 查询该评估计划下的所有统计对象风险事件
	 * @param assessPlanId 评估计划ID
	 * @return List<StatisticsResult>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public List<StatisticsResult> findStatisticsResultByAssessPlanIdAllList(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		
		List<StatisticsResult> arrayList = new ArrayList<StatisticsResult>();
		StatisticsResult statisticsResult = null;
		Dimension dimension = null;
		RiskAssessPlan riskAssessPlan = null;
		
        sql.append(" SELECT ");
        sql.append(" t.id,t.SCORE_DIM_ID,t.ASSESS_PLAN_ID,t.SCORE_OBJECT_ID,t.SCORE,t.STATUS  statu ");
        sql.append(" FROM ");
        sql.append(" T_RM_STATISTICS_RESULT t, t_rm_risks r ");
        sql.append(" WHERE ");
        sql.append(" t.SCORE_OBJECT_ID = r.ID ");
        sql.append(" AND t.assess_plan_id = :assessPlanId ");
        sql.append(" AND r.IS_RISK_CLASS = 're' ");
       
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String id = "";	//总体打分ID
            String dimId = ""; //维度ID
            String assessPlanIds = ""; //评估计划ID
            String scoreObjectId = ""; //打分对象ID
            String score = ""; //分值
            String status = null; //状态
            
            statisticsResult = new StatisticsResult();
            dimension = new Dimension();
            riskAssessPlan = new RiskAssessPlan();
            
            if(null != objects[0]){
            	id = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	assessPlanIds = objects[2].toString();
            }if(null != objects[3]){
            	scoreObjectId = objects[3].toString();
            }if(null != objects[4]){
            	score = objects[4].toString();
            }if(null != objects[5]){
            	status = objects[5].toString();
            }
            
            statisticsResult.setId(id);
            dimension.setId(dimId);
            statisticsResult.setDimension(dimension);
            riskAssessPlan.setId(assessPlanIds);
            statisticsResult.setRiskAssessPlan(riskAssessPlan);
            statisticsResult.setRiskScoreObject(scoreObjectId);
            statisticsResult.setScore(score);
            statisticsResult.setStatus(status);
            
            arrayList.add(statisticsResult);
        }
		
        return arrayList;
	}
	
	/**
	 * 统计风险事件,风险分类
	 * */
	public HashMap<String, String> findStatisticsResultRiskReOrRbsByAssessPlanId(String assessPlanId) {
		StringBuffer sql = new StringBuffer();
		HashMap<String, String> maps = new HashMap<String, String>();		
		sql.append(" select id,SCORE_OBJECT_ID from t_rm_statistics_result " +
				"where assess_plan_id=:assessPlanId GROUP BY SCORE_OBJECT_ID ");
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String scoreObjectId = "";
            
            if(null != objects[1]){
            	scoreObjectId = objects[1].toString();
            }
            maps.put(scoreObjectId, scoreObjectId);
        }
        
        return maps;
	}
	
	
	
	/** 
	  * @Description: 获取评估计划对应对象打分详细信息
	  * @author jia.song@pcitc.com
	  * @date 2017年4月17日 下午4:11:05 
	  * @param assessPlanId
	  * @return 
	  */
	public List<Map<String,String>> findAccessPlayRelaScoreObject(String assessPlanId){
		List<Map<String,String>> rtnList = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" object.RISK_ID,scoreDept.ORG_ID,scoreDept.ETYPE,emp.SCORE_EMP_ID,result.SCORE_DIC_ID,result.SCORE_DIM_ID,dim.SCORE_DIM_NAME,dic.SCORE_DIC_VALUE,roles.ROLE_CODE,object.ID ");
		sql.append(" FROM ");
		sql.append("T_RM_RISK_ASSESS_PLAN plan,T_RM_RISK_SCORE_OBJECT object,T_RM_RISK_ORG_TEMP scoreDept,T_RM_RANG_OBJECT_DEPT_EMP emp,T_RM_SCORE_RESULT result,T_DIM_SCORE_DIC dic,T_DIM_SCORE_DIM dim,t_sys_user users,t_sys_employee employee,t_sys_user_role userRelaRole,t_sys_role roles ");
		sql.append(" WHERE ");
		sql.append(" plan.ID=:assessPlanId ");
		sql.append(" AND roles.ROLE_CODE IN ('Employee','DeptLeader') ");
		sql.append(" AND plan.ID = object.assess_plan_id ");
		sql.append(" AND emp.SCORE_DEPT_ID = scoreDept.ID ");
		sql.append(" AND emp.SCORE_OBJECT_ID = object.ID ");
		sql.append(" AND emp.ID = result.RANG_OBJECT_DEPT_EMP_ID ");
		sql.append(" AND result.SCORE_DIC_ID = dic.ID ");
		sql.append(" AND users.ID = employee.USER_ID ");
		sql.append(" AND employee.ID = emp.SCORE_EMP_ID ");
		sql.append(" AND userRelaRole.USER_ID = users.ID ");
		sql.append(" AND roles.ID = userRelaRole.ROLE_ID ");
		sql.append(" AND dim.ID = result.SCORE_DIM_ID ");
		sql.append(" ORDER BY object.ID,dic.SCORE_DIM_ID ");
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
	    sqlQuery.setParameter("assessPlanId", assessPlanId);
	    List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            Map<String,String> map = new HashMap<String,String>();
            if(null != objects[0]){
            	map.put("riskId", objects[0].toString());
            }
            if(null != objects[1]){
            	map.put("orgId", objects[1].toString());
            }
            if(null != objects[2]){
            	map.put("orgType", objects[2].toString());
            }
            if(null != objects[3]){
            	map.put("scoreEmpId", objects[3].toString());
            }
            if(null != objects[4]){
            	map.put("scoreDicId", objects[4].toString());
            }
            if(null != objects[5]){
            	map.put("scoreDimId", objects[5].toString());
            }
            if(null != objects[6]){
            	map.put("scoreDimName", objects[6].toString());
            }
            if(null != objects[7]){
            	map.put("scoreDicValue", objects[7].toString());
            }
            if(null != objects[8]){
            	map.put("roleCode", objects[8].toString());
            }
            if(null != objects[9]){
            	map.put("objectId", objects[9].toString());
            }
            rtnList.add(map);
        }
        
		return rtnList;
	}
	
	
	/** 
	  * @Description: 通过查询获取到模板所对应的计算公式
	  * @author jia.song@pcitc.com
	  * @date 2017年4月24日 上午9:23:19 
	  * @param assessPlanId
	  * @return 
	  */
	public String queryCaluFormulaByAssessPlanId(String assessPlanId){
	    String caluFormula = null;
	    StringBuffer sql = new StringBuffer();
	    sql.append(" SELECT ");
	    sql.append(" calu_formula ");
	    sql.append(" FROM ");
	    sql.append(" t_dim_template template,t_rm_risk_assess_plan plan ");
	    sql.append(" WHERE ");
	    sql.append(" plan.ID=:assessPlanId ");
	    sql.append(" AND template.ID = plan.TEMP_ID ");
	    SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
	    sqlQuery.setParameter("assessPlanId", assessPlanId);
	    caluFormula  = (String) sqlQuery.uniqueResult();
	    return caluFormula;
	}
	
	
}