package com.fhd.ra.business.assess.oper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.TimePeriodBO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.NumberUtil;
import com.fhd.ra.business.assess.approval.ApplyContainer;
import com.fhd.ra.business.assess.approval.AssessDateInitBO;
import com.fhd.ra.business.assess.summarizing.util.RiskRbsOperBO;
import com.fhd.ra.business.assess.util.DynamicDim;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.RiskStatusBO;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.sm.business.KpiBO;

@Service
public class RiskAdjustHistoryBO {

	@Autowired
	private RiskRbsOperBO o_riskRbsOperBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	
	@Autowired
	private RiskStatusBO o_riskStatusBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
	@Autowired
	private AssessDateInitBO o_assessDateInitBO;
	
	/**
	 * 批量添加风险记录
	 * */
	@Transactional
    public void saveRiskAdjustHistory(final ArrayList<RiskAdjustHistory> riskAdjustHistoryList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_risk_adjust_history " +
                		"(id,risk_id,risk_status,adjust_type,adjust_time,is_latest," +
                		"etrend,time_period_id,assessement_status,company_id,calculate_formula) value(?,?,?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (RiskAdjustHistory riskAdjustHistory : riskAdjustHistoryList) {
                	  pst.setString(1, riskAdjustHistory.getId());
                      pst.setString(2, riskAdjustHistory.getRisk().getId());
                      pst.setDouble(3, riskAdjustHistory.getStatus());
                      pst.setString(4, riskAdjustHistory.getAdjustType());
                      pst.setTimestamp(5, new java.sql.Timestamp(riskAdjustHistory.getAdjustTime().getTime()));
                      pst.setString(6, riskAdjustHistory.getIsLatest());
                      pst.setString(7, riskAdjustHistory.getEtrend());
                      pst.setString(8, riskAdjustHistory.getTimePeriod().getId());
                      pst.setString(9, riskAdjustHistory.getAssessementStatus());
                      pst.setString(10, riskAdjustHistory.getCompany().getId());
                      pst.setString(11, riskAdjustHistory.getCalculateFormula());
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量保存风险历史记录
	 * @param RiskAdjustHistoryArrayList 风险记录实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveRiskAdjustHistorySql(final List<Object> RiskAdjustHistoryArrayList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " INSERT INTO t_rm_risk_adjust_history (ID, RISK_ID, RISK_STATUS, MANAGEMENT_URGENCY, " + 
                		" ADJUST_TYPE, ADJUST_TIME, IS_LATEST, ETREND, TIME_PERIOD_ID, " + 
                		" ASSESSEMENT_STATUS, LOCK_STATUS, COMPANY_ID, ASSESS_PLAN_ID, " + 
                		" IMPACTS, PROBABILITY, TEMPLATE_ID, CALCULATE_FORMULA) VALUES " + 
                		" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (Object object : RiskAdjustHistoryArrayList) {
                	RiskAdjustHistory riskAdjustHistory = (RiskAdjustHistory)object;
                	pst.setString(1, riskAdjustHistory.getId());
                	pst.setString(2, riskAdjustHistory.getRisk().getId());
                	pst.setDouble(3, riskAdjustHistory.getStatus());
                	pst.setString(4, null);
                	pst.setString(5, riskAdjustHistory.getAdjustType());
                	pst.setTimestamp(6, new Timestamp(riskAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(7, riskAdjustHistory.getIsLatest());
                	pst.setString(8, riskAdjustHistory.getEtrend());
                	pst.setString(9, riskAdjustHistory.getTimePeriod().getId());
                	pst.setString(10, riskAdjustHistory.getAssessementStatus());
                	pst.setString(11, riskAdjustHistory.getLockStatus());
                	pst.setString(12, riskAdjustHistory.getCompany().getId());
                	pst.setString(13, riskAdjustHistory.getRiskAssessPlan().getId());
                	pst.setString(14, null);
                	pst.setString(15, null);
                	pst.setString(16, riskAdjustHistory.getTemplate().getId());
                	pst.setString(17, riskAdjustHistory.getCalculateFormula());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 保存风险评估结果
	 * */
	@Transactional
	public void mergeRiskAdjustHistory(RiskAdjustHistory riskAdjustHistory) {
		o_riskAdjustHistoryDAO.merge(riskAdjustHistory);
	}
	
	/**
	 * 删除风险评估记录
	 * */
	@Transactional
	public void delRiskAdjustHistory(RiskAdjustHistory riskAdjustHistory) {
		o_riskAdjustHistoryDAO.delete(riskAdjustHistory);
	}
	
	/**
	 * 删除风险评估结果
	 * */
	@Transactional
	public void deleteRiskAdjustHistory(ArrayList<String> idsArrayList) {
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery("delete from t_rm_risk_adjust_history where id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 删除风险评估结果
	 * */
	@Transactional
	public void deleteRiskAdjustHistory(String assessPlanId) {
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery("delete from t_rm_risk_adjust_history where assess_plan_id = :assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 批量更新风险历史记录
	 * @param RiskAdjustHistoryArrayList 风险记录实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void updateRiskAdjustHistorySql(final List<Object> RiskAdjustHistoryArrayList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "update t_rm_risk_adjust_history set RISK_ID=?, RISK_STATUS=?, MANAGEMENT_URGENCY=?, " + 
                		" ADJUST_TYPE=?, ADJUST_TIME=?, IS_LATEST=?, ETREND=?, TIME_PERIOD_ID=?, " + 
                		" ASSESSEMENT_STATUS=?, LOCK_STATUS=?, COMPANY_ID=?, " + 
                		" IMPACTS=?, PROBABILITY=?, TEMPLATE_ID=?, CALCULATE_FORMULA=? where id=?";
                pst = connection.prepareStatement(sql);
                
                for (Object object : RiskAdjustHistoryArrayList) {
                	RiskAdjustHistory riskAdjustHistory = (RiskAdjustHistory)object;
                	pst.setString(1, riskAdjustHistory.getRisk().getId());
                	pst.setDouble(2, riskAdjustHistory.getStatus());
                	pst.setString(3, null);
                	pst.setString(4, riskAdjustHistory.getAdjustType());
                	pst.setTimestamp(5, new Timestamp(riskAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(6, riskAdjustHistory.getIsLatest());
                	pst.setString(7, riskAdjustHistory.getEtrend());
                	pst.setString(8, riskAdjustHistory.getTimePeriod().getId());
                	pst.setString(9, riskAdjustHistory.getAssessementStatus());
                	pst.setString(10, riskAdjustHistory.getLockStatus());
                	pst.setString(11, riskAdjustHistory.getCompany().getId());
                	pst.setString(12, null);
                	pst.setString(13, null);
                	pst.setString(14, riskAdjustHistory.getTemplate().getId());
                	pst.setString(15, riskAdjustHistory.getCalculateFormula());
                	pst.setString(16, riskAdjustHistory.getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 更新风险评估纪录表所有is_latest(最新)为0
	 * */
	@Transactional
	public void updateRiskAdjustHistory(ArrayList<String> idsArrayList){
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(" update t_rm_risk_adjust_history set is_latest='0' where risk_id in (:ids) ");
		if(!idsArrayList.contains(null))
		{
			sqlQuery.setParameterList("ids", idsArrayList);
			sqlQuery.executeUpdate();
		}
		
	}
	
	/**
	 * 更新该计划下风险评估纪录表所有is_latest(最新)为1
	 * */
	@Transactional
	public void updateRiskAdjustHistoryByAssessPlanId(String assessPlanId){
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(" update t_rm_risk_adjust_history set is_latest='1' where assess_plan_id=:assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 查询全部评估统计,并已MAP方式存储(风险ID,评估统计实体)
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, RiskAdjustHistory> findRiskAdjustHistoryByRiskIdAllMap(String assessPlanId){
		HashMap<String, RiskAdjustHistory> map = new HashMap<String, RiskAdjustHistory>();
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<RiskAdjustHistory> list = null;
		list = criteria.list();
		
		for (RiskAdjustHistory riskAdjustHistory : list) {
			map.put(riskAdjustHistory.getRisk().getId(), riskAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public List<RiskAdjustHistory> findRiskAdjustHistoryByAssessAllList(String riskId){
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		if(StringUtils.isNotBlank(riskId)){
		    criteria.add(Restrictions.eq("risk.id", riskId));
		}
		List<RiskAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, RiskAdjustHistory> findRiskAdjustHistoryByAssessAllMap(){
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		List<RiskAdjustHistory> list = null;
		list = criteria.list();
		HashMap<String, RiskAdjustHistory> map = new HashMap<String, RiskAdjustHistory>();
		for (RiskAdjustHistory riskAdjustHistory : list) {
			map.put(riskAdjustHistory.getId(), riskAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 通过风险ID查询
	 * */
	@SuppressWarnings("unchecked")
	public boolean findRiskAdjustHistoryByRiskId(String riskId){
		boolean isBool = false;
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("risk.id", riskId));
		List<RiskAdjustHistory> list = null;
		list = criteria.list();
		if(list.size() != 0){
			isBool = true;
		}
		return isBool;
	}
	
	/**
     * 通过风险ID查询最新的历史记录
     * isCal :是否是公式计算，公式计算没有模板的
     * */
    @SuppressWarnings("unchecked")
    public RiskAdjustHistory findNowRiskAdjustHistoryByRiskId(String riskId,Boolean haveCal){
        Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
        criteria.add(Restrictions.eq("risk.id", riskId));
        criteria.add(Restrictions.eq("isLatest", "1"));
        if(!haveCal){
            criteria.add(Restrictions.ne("adjustType", "3"));
        }
        List<RiskAdjustHistory> list = criteria.list();
        if(list.size() > 0){
        	RiskAdjustHistory riskAdjustHistory = list.get(0);
            return riskAdjustHistory;
        }else{
            return null;
        }
    }
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	public List<RiskAdjustHistory> findRiskAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<RiskAdjustHistory> list = null;
		list = criteria.list();
		return list;
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<RiskAdjustHistory> findTransactionalRiskAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<RiskAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 该公司下的一条
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, RiskAdjustHistory> findGroupAdjustTime(String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select this_.id, this_.risk_id  y0_, max(this_.adjust_time) " +
				" y1_, this_.risk_status from t_rm_risk_adjust_history this_ where this_.COMPANY_ID = :companyId group by this_.risk_id ");
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
		List<Object[]> list = sqlQuery.list();
		
		HashMap<String, RiskAdjustHistory> map = new HashMap<String, RiskAdjustHistory>();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
			RiskAdjustHistory riskAdjustHistory = new RiskAdjustHistory();
			Risk risk = new Risk();
			TimePeriod timePeriod = new TimePeriod();
			
			riskAdjustHistory.setId(objects[0].toString());
			risk.setId(objects[1].toString());
			
			if(null != objects[2]){
				timePeriod.setId(objects[2].toString());
				riskAdjustHistory.setTimePeriod(timePeriod);
			}else{
				riskAdjustHistory.setTimePeriod(null);
			}
			
			
			if(null != objects[3]){
				riskAdjustHistory.setStatus(Double.parseDouble(objects[3].toString()));
			}else{
				riskAdjustHistory.setStatus(null);
			}
			
			
			riskAdjustHistory.setRisk(risk);
			
			map.put(riskAdjustHistory.getRisk().getId(), riskAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询根级风险集合
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findParentRisk(String assessPlanId, String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id,a.risk_name,a.is_risk_class,a.company_id, a.parent_id " +
				" from t_rm_risks a, t_rm_risk_adjust_history b " +
				" where a.id = b.risk_id and a.company_id=:companyId and b.assess_plan_id = :assessPlanId " +
						" and a.parent_id is not null ");
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String riskId = "";
            String isRiskClass = "";
            String parentId = "";
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[2]){
            	isRiskClass = objects[2].toString();
            }if(null != objects[4]){
            	parentId = objects[4].toString();
            }
            
            if(map.get(parentId) != null){
            	map.get(parentId).add(riskId + "--" + isRiskClass);
            }else{
            	ArrayList<String> arrayList = new ArrayList<String>();
            	arrayList.add(riskId + "--" + isRiskClass);
            	map.put(parentId, arrayList);
            }
        }
		
		return map;
	}
	
	/**
	 * 通过风险事件、分类寻找根级风险
	 * */
	@SuppressWarnings({ "unchecked", "unused" })
	public ArrayList<String> findRootRiskByRiskReAndRiskRbs(String assessPlanId, String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id,a.id_seq " +
				" from t_rm_risks a, t_rm_risk_adjust_history b " +
				" where a.id = b.risk_id and a.company_id=:companyId and b.assess_plan_id = :assessPlanId ");
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        ArrayList<String> arrayList = new ArrayList<String>();
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String riskId = "";
            String idSeq = "";
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	idSeq = objects[1].toString();
            }
            
            arrayList.add(idSeq);
        }
		
		return arrayList;
	}
	
	/**
	 * 查询该风险评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<RiskAdjustHistory>> findRiskAdjustHistoryById(){
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("isLatest", "1"));
		criteria.addOrder(Order.desc("adjustTime"));
		HashMap<String, ArrayList<RiskAdjustHistory>> map = new HashMap<String, ArrayList<RiskAdjustHistory>>();
		
		List<RiskAdjustHistory> list = criteria.list();
		for (RiskAdjustHistory riskAdjustHistory : list) {
			ArrayList<RiskAdjustHistory> arrayList = new ArrayList<RiskAdjustHistory>();
			arrayList.add(riskAdjustHistory);
			
			if(map.get(riskAdjustHistory.getRisk().getId()) != null){
				map.get(riskAdjustHistory.getRisk().getId() + "--" + riskAdjustHistory.getIsLatest()).add(riskAdjustHistory);
			}else{
				map.put(riskAdjustHistory.getRisk().getId() + "--" + riskAdjustHistory.getIsLatest(), arrayList);
			}
		}
		
		return map;
	}

	/**
	 * 查询评估记录该评估计划与次ID对应的数据
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, RiskAdjustHistory> findRiskAdjustHistoryByIdAndAssessPlanId(String assessPlanId){
		HashMap<String, RiskAdjustHistory> map = new HashMap<String, RiskAdjustHistory>();
		try {
			Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			List<RiskAdjustHistory> list = criteria.list();
			for (RiskAdjustHistory riskAdjustHistory : list) {
				map.put(riskAdjustHistory.getRisk().getId(), riskAdjustHistory);
			}
		} catch (Exception e) {
			
		}
		
		return map;
	}
	
	/**
	 * 查询风险评估记录对应维度及分值,并已MAP方式存储(dimId--score,score)
	 * @author 金鹏祥
	 * @return String
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findRiskAdjusthistoryDimScoreAllMap(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = null;
		sql.append(" select a.risk_id,b.score_dim_id, b.SCORE from   ");
		sql.append(" t_rm_risk_adjust_history a  ");
		sql.append(" LEFT JOIN t_rm_adjust_history_result b on a.id=b.adjust_history_id where a.adjust_type='1' ORDER BY RISK_ID ");
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String riskId = "";
        	String scoreDimId = "";
        	String score = "";
        	
        	if(null != objects[0]){
        		riskId = objects[0].toString();
            }if(null != objects[1]){
            	scoreDimId = objects[1].toString();
            }if(null != objects[2]){
            	score = objects[2].toString();
            }
            
        	if(map.get(riskId) != null){
        		map.get(riskId).add(scoreDimId + "--" + score);
        	}else{
        		arrayList = new ArrayList<String>();
        		arrayList.add(scoreDimId + "--" + score);
        		map.put(riskId, arrayList);
        	}
        }
        
		return map;
	}
	
	/**
	 * 批量修改将所有指定风险is_latest更新为0
	 * */
	@Transactional
    public void updateRiskAdjustHistoryZeroByInRiskId(final ArrayList<String> riskIdList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement pst = null;
                String sql = "update t_rm_risk_adjust_history set is_latest=? where risk_id=?";
                pst = connection.prepareStatement(sql);
                
                for (String riskId : riskIdList) {
                	  pst.setString(1, "0");
                      pst.setString(2, riskId);
                      pst.addBatch();
				}
                pst.executeBatch();
            }
        });
    }
	
	/**
	 * 批量修改将所有指定ID的is_latest更新为1
	 * */
	@Transactional
    public void updateRiskAdjustHistoryOneByInRiskId(final ArrayList<String> riskAdjustHistoryId) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement pst = null;
                String sql = "update t_rm_risk_adjust_history set is_latest=? where id=?";
                pst = connection.prepareStatement(sql);
                
                for (String id : riskAdjustHistoryId) {
                	  pst.setString(1, "1");
                      pst.setString(2, id);
                      pst.addBatch();
				}
                pst.executeBatch();
            }
        });
    }
	
	/**
	 * 保存风险评估记录
	 * */
	@Transactional
	public RiskAdjustHistory getRiskAdjustHistory(String riskAdjustHistoryId, HashMap<String, TimePeriod>  timePeriodMapAll, 
			HashMap<String, AlarmPlan> alarmPlanAllMap,
			List<RiskAdjustHistory> riskAdjustHistoryListAll, Risk risk, String alarmScenario, double riskScore){
		RiskAdjustHistory riskAdjustHistory = new RiskAdjustHistory();
		try {
			String adjustType = "3";//自动计算
			Date adjustTime = TimeUtil.getCurrentTime();
			String assessementStatus = "";
			TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome(risk.getGatherFrequence().getId());
			String calculateFormula = risk.getFormulaDefine();
			DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), riskScore);
			if(dict != null){
				assessementStatus = dict.getValue();
			}
			double parentRiskLevel = o_riskRbsOperBO.getRiskParentRiskLevel(
					riskAdjustHistoryListAll, risk.getId(), adjustTime.toString());
			String etrend = "";
			if(parentRiskLevel != -1){
				if(riskScore > parentRiskLevel){
					//up：向上；
					etrend = "up";
				}else if(riskScore < parentRiskLevel){
					//down：向下；
					etrend = "down";
				}else if(riskScore == parentRiskLevel){
					//flat：水平
					etrend = "flat";
				}
			}
			
			SysOrganization company = new SysOrganization();
			
			company.setId(risk.getCompany().getId());
	        riskAdjustHistory.setId(riskAdjustHistoryId);
	        riskAdjustHistory.setRisk(risk);
	        riskAdjustHistory.setStatus(riskScore);
	        riskAdjustHistory.setAdjustType(adjustType);
	        riskAdjustHistory.setAdjustTime(adjustTime);
	        riskAdjustHistory.setIsLatest("0");
	        riskAdjustHistory.setEtrend(etrend);
	        riskAdjustHistory.setTimePeriod(timePeriod);
	        riskAdjustHistory.setAssessementStatus(assessementStatus);
	        riskAdjustHistory.setCompany(company);
	        riskAdjustHistory.setCalculateFormula(calculateFormula);
		} catch (Exception e) {
			return null;
		}
		
		return riskAdjustHistory; 
	}
	
	/**
	 * 通过打分对象查询风险维度值(riskId--assessPlanId,scoreDimId--score)
	 * @author 金鹏祥
	 * @return String
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findRiskAdjusthistoryByRiskIdAndAssessPlanIdMapAll(String planIdQuery){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = null;
		
		sql.append(" select a.assess_plan_id, a.risk_id,b.score_dim_id,b.score from t_rm_risk_adjust_history a  ");
		sql.append(" LEFT JOIN t_rm_adjust_history_result b on b.adjust_history_id = a.id ");
		sql.append(" where a.ASSESS_PLAN_ID = :planIdQuery ");
		
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("planIdQuery", planIdQuery);
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String riskId = "";
        	String assessPlanId = "";
        	String scoreDimId = "";
        	String score = "";
        	
        	if(null != objects[0]){
        		riskId = objects[0].toString();
            }if(null != objects[1]){
            	assessPlanId = objects[1].toString();
            }if(null != objects[2]){
        		scoreDimId = objects[2].toString();
            }if(null != objects[3]){
            	score = objects[3].toString();
            }
            
        	if(map.get(riskId + "--" + assessPlanId) != null){
        		map.get(riskId + "--" + assessPlanId).add(scoreDimId + "--" + score);
        	}else{
        		arrayList = new ArrayList<String>();
        		arrayList.add(scoreDimId + "--" + score);
        		map.put(riskId + "--" + assessPlanId, arrayList);
        	}
        }
        
		return map;
	}
	
	/**
	 * 大综合查询
	 * @author 金鹏祥
	 * @return ArrayList<HashMap<String, Object>>
	 * */
	@SuppressWarnings("unchecked")
	@RecordLog("综合查询")
	public ArrayList<HashMap<String, Object>> findRiskAdjustHistoryInfos(int start, int limit, String companyId,
			String planIdQuery, String assementStatusQuery, String riskStatusQuery, 
			String orgMQuery, String orgAQuery, String assessEmpQuery, String riskNameQuery) {
		StringBuffer sql = new StringBuffer();
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
        ApplyContainer applyContainer = o_assessDateInitBO.getPreviewAssessPlanEmpAllDateInit(companyId, planIdQuery);
        
        sql.append(" select h.plan_name,g.risk_name  parent_name,e.risk_name,a.risk_status, ");
        sql.append(" a.assessement_status,f.template_name,z.emp_name,d.id,d.SCORE_OBJECT_ID," +
        		"f.template_type,a.assess_plan_id,e.id  riskId,d.score_emp_id,f.id  template_id ");
        sql.append(" from t_rm_risk_adjust_history a  ");
        sql.append(" LEFT JOIN t_rm_risk_score_object c on c.risk_id= a.risk_id and c.assess_plan_id= a.assess_plan_id ");
        sql.append(" LEFT JOIN t_rm_rang_object_dept_emp d on d.SCORE_OBJECT_ID = c.id ");
        sql.append(" LEFT JOIN t_rm_risks e on e.id = a.risk_id ");
        sql.append(" LEFT JOIN t_rm_risks g on e.PARENT_ID = g.id ");
        sql.append(" LEFT JOIN t_rm_risk_assess_plan h on h.id = a.assess_plan_id ");
        sql.append(" LEFT JOIN t_dim_template f on f.id = a.template_id ");
        sql.append(" LEFT JOIN t_sys_employee z on z.id = d.score_emp_id ");
        sql.append(" where  ");
        sql.append(" a.assess_plan_id=:planIdQuery ");
        
        if(StringUtils.isNotBlank(assementStatusQuery)){
        	sql.append(" and a.assessement_status = :assementStatusQuery ");
        }if(StringUtils.isNotBlank(riskStatusQuery)){
        	sql.append(" and a.risk_status = :riskStatusQuery ");
        }if(StringUtils.isNotBlank(assessEmpQuery) && !assessEmpQuery.equalsIgnoreCase("undefined")){
        	sql.append(" and ");
        	int count = 0;
        	JSONArray jsonarr = JSONArray.fromObject(assessEmpQuery);
    		for (Object objects : jsonarr) {
    			JSONObject jsobjs = (JSONObject) objects;
    			String empId = jsobjs.getString("id");
    			count++;
    			if(jsonarr.size() > 1){
    				if(count == jsonarr.size()){
        				sql.append(" d.score_emp_id = '" + empId + "'  ");
        			}else{
        				sql.append(" d.score_emp_id = '" + empId + "' or ");
        			}
    			}else{
    				sql.append(" d.score_emp_id = '" + empId + "'  ");
    			}
    		}
        }if(StringUtils.isNotBlank(riskNameQuery)){
        	sql.append(" and e.risk_name like :riskNameQuery ");
        }
        
        sql.append(" and e.IS_RISK_CLASS = 're' ORDER BY h.ID,g.risk_name ");
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planIdQuery", planIdQuery);
        if(StringUtils.isNotBlank(assementStatusQuery)){
        	sqlQuery.setParameter("assementStatusQuery", assementStatusQuery);
        }if(StringUtils.isNotBlank(riskStatusQuery)){
        	sqlQuery.setParameter("riskStatusQuery", riskStatusQuery);
        }if(StringUtils.isNotBlank(riskNameQuery)){
        	sqlQuery.setParameter("riskNameQuery", "%" + riskNameQuery + "%");
        }
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String planName = "";
            String riskName= "";
            String parentRiskName = "";
            double riskStatus = 0l;
            String assessementStatus = "";
            String templateName = "";
            String empName = "";
            String rangObjectDeptEmpId = "";
            String scoreObjectId = "";
            String templateType = "";
            String riskId = "";
            String assessPlanId = "";
            String dimId = "";
            String score = "";
            String orgType = "";
            String orgName = "";
            String riskIcon = "";
            String templateId = "";
            String orgId = "";
            double riskScoreValue = 0l;
            ArrayList<String> scoreDicValuelist = new ArrayList<String>();
            HashMap<String, Object> map = new HashMap<String, Object>();
            ArrayList<String> dimList = null;
            
            if(null != objects[0]){
            	planName = objects[0].toString();
            }if(null != objects[1]){
            	parentRiskName = objects[1].toString();
            }if(null != objects[2]){
            	riskName = objects[2].toString();
            }if(null != objects[3]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[3].toString()), 2);
            }if(null != objects[4]){
            	assessementStatus = objects[4].toString();
            }if(null != objects[5]){
            	templateName = objects[5].toString();
            }if(null != objects[6]){
            	empName = objects[6].toString();
            }if(null != objects[7]){
            	rangObjectDeptEmpId = objects[7].toString();
            }if(null != objects[8]){
            	scoreObjectId = objects[8].toString();
            }if(null != objects[9]){
            	templateType = objects[9].toString();
            }if(null != objects[10]){
            	assessPlanId = objects[10].toString();
            }if(null != objects[11]){
            	riskId = objects[11].toString();
            }if(null != objects[13]){
            	templateId = objects[13].toString();
            }
            
            map.put("planName", planName);
            map.put("parentRiskName", parentRiskName);
            map.put("riskName", riskName);
            map.put("riskStatus", riskStatus);
            map.put("assessementStatus", assessementStatus);
            if("icon-ibm-symbol-4-sm".equalsIgnoreCase(assessementStatus)){
				//高
				map.put("icon", "4");
			}else if("icon-ibm-symbol-5-sm".equalsIgnoreCase(assessementStatus)){
				//中
				map.put("icon", "3");
			}else if("icon-ibm-symbol-6-sm".equalsIgnoreCase(assessementStatus)){
				//低
				map.put("icon", "2");
			}else if("icon-ibm-symbol-0-sm".equalsIgnoreCase(assessementStatus)){
				//无
				map.put("icon", "1");
			}else{
				map.put("icon", "0");
			}
            
            map.put("templateName", templateName);
            map.put("empName", empName);
            map.put("assessPlanId", assessPlanId);
            map.put("riskId", riskId);
            
            if(templateType.equalsIgnoreCase("dim_template_type_custom")){
            	map.put("templateType", "自有模板");
            }else if(templateType.equalsIgnoreCase("dim_template_type_sys")){
            	map.put("templateType", "系统模板");
            }else if(templateType.equalsIgnoreCase("dim_template_type_self")){
            	map.put("templateType", "自定义模板");
            }
            
            ArrayList<String> dimIdAndScoreArrayList = applyContainer.getRiskAdjusthistoryByRiskIdAndAssessPlanIdMapAll().get(assessPlanId + "--" + riskId);
			for (String dimIdAndScore : dimIdAndScoreArrayList) {
				dimId = dimIdAndScore.split("--")[0].toString();
				score = dimIdAndScore.split("--")[1].toString();
				map.put(dimId, NumberUtil.meg(Double.parseDouble(score), 2));
			}
			
			ArrayList<String> orgByScoreObjectIdList = applyContainer.getOrgByScoreObjectIdMapAll().get(scoreObjectId);
            StringBuffer dept = new StringBuffer();
            String deptA = "";
            StringBuffer deptM = new StringBuffer();
            
			for (String orgByScoreObjectId : orgByScoreObjectIdList) {
				orgType = orgByScoreObjectId.split("--")[0].toString();
				orgName = orgByScoreObjectId.split("--")[1].toString();
				orgId = orgByScoreObjectId.split("--")[2].toString();
				
				if(orgType.equalsIgnoreCase("M")){
					//M:责任部门
					//map.put("orgM", orgName);
					//dept += "M--" + orgId + ",";
					dept = dept.append("M--" + orgId + ",");
				}else{
					//A:相关部门
					//map.put("orgA", orgName);

					//dept += "A--" + orgId + ",";
					dept = dept.append("A--" + orgId + ",");
				}
			}
            
			if(StringUtils.isNotBlank(orgMQuery)){
				if(!orgMQuery.equalsIgnoreCase("undefined")){
					JSONArray jsonarr = JSONArray.fromObject(orgMQuery);
		    		for (Object objects2 : jsonarr) {
		    			JSONObject jsobjs = (JSONObject) objects2;
		    			String id = jsobjs.getString("id");
		    			//deptM += "M--" + id + ",";
		    			deptM = deptM.append("M--" + id + ",");
		    		}
				}
			}
            
			if(StringUtils.isNotBlank(orgAQuery)){
				if(!orgAQuery.equalsIgnoreCase("undefined")){
					JSONArray jsonarr = JSONArray.fromObject(orgAQuery);
		    		for (Object objects2 : jsonarr) {
		    			JSONObject jsobjs = (JSONObject) objects2;
		    			String id = jsobjs.getString("id");
		    			//deptM += "A--" + id + ",";
		    			deptM = deptM.append("A--" + id + ",");
		    		}
				}
			}
            
            
			if(StringUtils.isNotBlank(orgMQuery) && StringUtils.isNotBlank(orgAQuery)){
				if(!orgAQuery.equalsIgnoreCase("undefined") && !orgMQuery.equalsIgnoreCase("undefined")){
					//两总情况都有
					int countM = 0;
					String [] mEmp = deptM.toString().split(",");
					for (String m : mEmp) {
						if(dept.toString().indexOf(m) == -1){
							//没有
							countM = 1;
							break;
						}
					}
					
					int countA = 0;
					String [] aEmp = deptA.split(",");
					for (String a : aEmp) {
						if(dept.toString().indexOf(a) == -1){
							//没有
							countA = 1;
							break;
						}
					}
					
					if((countM + countA) != 0){
						continue;
					}
				}
			}else{
				if(StringUtils.isNotBlank(deptM.toString())){
					int countM = 0;
					String [] mEmp = deptM.toString().split(",");
					for (String m : mEmp) {
						if(dept.toString().indexOf(m) == -1){
							//没有
							countM = 1;
							break;
						}
					}
					
					if(countM == 1){
						continue;
					}
				}if(StringUtils.isNotBlank(deptA)){
					int countA = 0;
					String [] aEmp = deptA.split(",");
					for (String a : aEmp) {
						if(dept.toString().indexOf(a) == -1){
							//没有
							countA = 1;
							break;
						}
					}
					
					if(countA == 1){
						continue;
					}
				}
			}
			
			
			for (String orgByScoreObjectId : orgByScoreObjectIdList) {
				orgType = orgByScoreObjectId.split("--")[0].toString();
				orgName = orgByScoreObjectId.split("--")[1].toString();
				orgId = orgByScoreObjectId.split("--")[2].toString();
				
				if(orgType.equalsIgnoreCase("M")){
					//M:责任部门
					map.put("orgM", orgName);
				}else{
					//A:相关部门
					map.put("orgA", orgName);
				}
			}
			
            if(templateType.equalsIgnoreCase("dim_template_type_custom")){
            	if(applyContainer.getRiskAllMap().get(riskId).getTemplate() != null){
            		templateId = applyContainer.getRiskAllMap().get(riskId).getTemplate().getId();
            	}
            }
            
            dimList = DynamicDim.getDimValue(riskId, templateId, rangObjectDeptEmpId, assessPlanId, applyContainer);
        	
        	if(dimList != null){
        		for (String obj : dimList) {
            		map.put(obj.split("--")[0] + "emp", NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()), 2));
            		scoreDicValuelist.add(obj.split("--")[0] + "--" + obj.split("--")[1].toString());
				}
        	}
        	
        	riskIcon = o_riskStatusBO.getRiskIcon(riskId, templateId, rangObjectDeptEmpId, assessPlanId, applyContainer);
        	
        	map.put("riskIcon", riskIcon);
            
        	String riskScoreValueStr = o_riskLevelBO.getRisklevel(
        			scoreDicValuelist, applyContainer.getRiskLevelFormula(), applyContainer.getDimIdAllList());
        	if(riskScoreValueStr.indexOf("--") == -1){
        		riskScoreValue = Double.parseDouble(riskScoreValueStr);
        	}
        	map.put("riskScoreValue", riskScoreValue);
        	
        	arrayList.add(map);
        }
        
        Collections.sort(arrayList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0,
					Map<String, Object> arg1) {
				return -arg0.get("icon").toString().compareTo(arg1.get("icon").toString());
			}
		});
        
        return arrayList;
	}
	
	/**
	 * 查询计划下的风险分类
	 * @param assessPlanId
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findParentRisksRbs(String assessPlanId,String query){
		List<Map<String, Object>> item = new ArrayList<Map<String,Object>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskMap = this.findChildrenRisksRbs(assessPlanId);
		//风险事件
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskResMap = this.findChildrenRisksRe(assessPlanId);
		HashMap<String, List<StatisticsResult>> statisticsResultMap = 
							o_statisticsResultBO.findStatisticsResultListByAssessPlanIdAllMap(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.id,b.risk_name,b.elevel,a.risk_status,a.assessement_status " +
				" from t_rm_risk_adjust_history a LEFT JOIN t_rm_risks b on a.RISK_ID = b.ID " +
				" where b.IS_RISK_CLASS ='rbs' and b.PARENT_ID is null and a.assess_plan_id=:assessPlanId ");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (b.risk_name like :query or a.risk_status like :query) ");
		}
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        if(StringUtils.isNotBlank(query)){
        	sqlQuery.setParameter("query", "%"+query+"%");
        }
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String riskId = "";
            String riskName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            String dimId = "";
            String score = "";
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	riskName = objects[1].toString();
            }if(null != objects[3]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[3].toString()),2).toString();
            }if(null != objects[4]){
            	assessmentStatus = objects[4].toString();
            }
            Map<String, Object> riskMap = new HashMap<String, Object>();
            riskMap.put("riskId", riskId);
            riskMap.put("name", riskName);
            riskMap.put("score", riskStatus);
            riskMap.put("iconCls", assessmentStatus);
            if(null != childrenRiskMap.get(riskId)){
            	riskMap.put("children", childrenRiskMap.get(riskId));//部门下的风险
            	riskMap.put("leaf", false);
            }else{
            	if(null != childrenRiskResMap.get(riskId)){
            		riskMap.put("children", childrenRiskResMap.get(riskId));//部门下的风险
                	riskMap.put("leaf", false);
            	}else{
            		riskMap.put("leaf", true);
            	}
            }
            List<StatisticsResult> statisticResultList = statisticsResultMap.get(riskId);
            for(StatisticsResult statis : statisticResultList){
            	dimId = statis.getDimension().getId();
				score = statis.getScore();
				riskMap.put(dimId, NumberUtil.meg(Double.parseDouble(score), 2));
            }
			riskMap.put("linked", true);
            item.add(riskMap);
        }
		return item;
	}
	
	
	/**
	 * 查询风险子分类
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenRisksRbs(String assessPlanId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskMap = this.findChildrenRisksRe(assessPlanId);
		HashMap<String, List<StatisticsResult>> statisticsResultMap = 
							o_statisticsResultBO.findStatisticsResultListByAssessPlanIdAllMap(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.id,b.parent_id,b.risk_name,b.elevel,a.risk_status,a.assessement_status " +
				" from t_rm_risk_adjust_history a LEFT JOIN t_rm_risks b on a.RISK_ID = b.ID " +
				" where b.IS_RISK_CLASS ='rbs' and b.PARENT_ID is not null and a.assess_plan_id=:assessPlanId ");
        
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String riskId = "";
            String parentId = "";
            String riskName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            String dimId = "";
            String score = "";
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	parentId = objects[1].toString();
            }if(null != objects[2]){
            	riskName = objects[2].toString();
            }if(null != objects[4]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[4].toString()),2).toString();
            }if(null != objects[5]){
            	assessmentStatus = objects[5].toString();
            }
            
            Map<String, Object> riskMap = new HashMap<String, Object>();
            riskMap.put("riskId", riskId);
            riskMap.put("name", riskName);
            riskMap.put("score", riskStatus);//风险分值
            riskMap.put("iconCls", assessmentStatus);//风险水平
            riskMap.put("linked", true);
            List<StatisticsResult> statisticResultList = statisticsResultMap.get(riskId);
            for(StatisticsResult statis : statisticResultList){
            	dimId = statis.getDimension().getId();
				score = statis.getScore();
				riskMap.put(dimId, NumberUtil.meg(Double.parseDouble(score), 2));
            }
            if(null != childrenRiskMap.get(riskId)){
            	riskMap.put("children", childrenRiskMap.get(riskId));//部门下的风险
            	riskMap.put("leaf", false);
            }/*else{
            	riskMap.put("leaf", true);
            }*/
            if(childrenMap.get(parentId) != null){
            	childrenMap.get(parentId).add(riskMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(riskMap);
            	childrenMap.put(parentId, arrayList);
            }
        }
		//return childrenMap;
		return findChildrenRisksRbsSelf(childrenMap);
	}
	
	/**
	 * 递归查询风险子分类中是否含有下级子分类
	 * @param childrenMap
	 * @return
	 */
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenRisksRbsSelf(
												HashMap<String, ArrayList<Map<String, Object>>> childrenMap){
		Set<Entry<String, ArrayList<Map<String, Object>>>> parentRiskIds = childrenMap.entrySet();
		for(Entry<String, ArrayList<Map<String, Object>>> parentRiskIdEntry : parentRiskIds){	//parentRiskId 父级风险id
			ArrayList<Map<String, Object>> parentRiskMaps = childrenMap.get(parentRiskIdEntry.getKey());
			for(Map<String, Object> parentRiskMap : parentRiskMaps){
				String riskId = (String)parentRiskMap.get("riskId");
				if(null!=childrenMap.get(riskId)){
					parentRiskMap.put("children", childrenMap.get(riskId));
					//parentRiskMap.put("leaf", false);
				}else{
					//parentRiskMap.put("leaf", true);
				}
			}
		}
		return childrenMap;
	}
	
	/**
	 * 查询风险分类下风险事件
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenRisksRe(String assessPlanId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		HashMap<String, List<StatisticsResult>> statisticsResultMap = 
				o_statisticsResultBO.findStatisticsResultListByAssessPlanIdAllMap(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.id,b.parent_id,b.risk_name,b.elevel,a.risk_status,a.assessement_status " +
				" from t_rm_risk_adjust_history a LEFT JOIN t_rm_risks b on a.RISK_ID = b.ID " +
				" where b.IS_RISK_CLASS ='re' and a.assess_plan_id=:assessPlanId ");
        SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String riskId = "";
            String parentId = "";
            String riskName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            String dimId = "";
            String score = "";
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	parentId = objects[1].toString();
            }if(null != objects[2]){
            	riskName = objects[2].toString();
            }if(null != objects[4]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[4].toString()),2).toString();
            }if(null != objects[5]){
            	assessmentStatus = objects[5].toString();
            }
            Map<String, Object> riskMap = new HashMap<String, Object>();
            riskMap.put("riskId", riskId);
            riskMap.put("name", riskName);
            riskMap.put("score", riskStatus);//风险分值
            riskMap.put("iconCls", assessmentStatus);//风险水平
            riskMap.put("linked", true);
            riskMap.put("leaf", true);
            List<StatisticsResult> statisticResultList = statisticsResultMap.get(riskId);
            for(StatisticsResult statis : statisticResultList){
            	dimId = statis.getDimension().getId();
				score = statis.getScore();
				riskMap.put(dimId, NumberUtil.meg(Double.parseDouble(score), 2));
            }
            if(childrenMap.get(parentId) != null){
            	childrenMap.get(parentId).add(riskMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(riskMap);
            	childrenMap.put(parentId, arrayList);
            }
        }
		return childrenMap;
	}
	
	/**
	 * 导出分类统计列表时，递归查询风险
	 * add by 王再冉
	 * 2013-12-27  下午6:33:47
	 * desc : 
	 * @param riskList
	 * @return
	 * @throws Exception 
	 * List<Map<String,Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getAllReportRisk(List<Map<String, Object>> riskList) throws Exception{
	    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	    for(Map<String, Object> map : riskList){
	    	list.add(map);
	    	List<Map<String,Object>> listItem = (List<Map<String, Object>>) map.get("children");
	    	if(null != listItem){
 	            List<Map<String,Object>> listItemC = this.getAllReportRisk(listItem);
 	            list.addAll(listItemC);
 	        }
	    }
	    return list;
	}
	
	/**
	 * 导出分类统计列表
	 * add by 王再冉
	 * 2013-12-27  下午6:33:35
	 * desc : 
	 * @param riskList
	 * @param indexList
	 * @param j
	 * @return
	 * @throws Exception 
	 * List<Object[]>
	 */
	public List<Object[]> exportRiskCountGrid(List<Map<String, Object>> riskList, List<String> indexList,int j) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		List<Map<String,Object>> riskListAll = this.getAllReportRisk(riskList);
		for(Map<String, Object> riskMap : riskListAll){
			objects = new Object[j];
			for(int m=0;m<indexList.size();m++){
				if(null!=riskMap.get(indexList.get(m))){
					objects[m] = riskMap.get(indexList.get(m)).toString();
				}
			}
			list.add(objects);
		}
		return list;
	}
}