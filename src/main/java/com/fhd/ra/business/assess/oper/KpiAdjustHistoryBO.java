package com.fhd.ra.business.assess.oper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.risk.KpiAdjustHistoryDAO;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.fdc.utils.NumberUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Service
public class KpiAdjustHistoryBO {

	@Autowired
	private KpiAdjustHistoryDAO o_kpiAdjustHistoryDAO;
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	private Log log = LogFactory.getLog(KpiAdjustHistoryBO.class);
	
	/**
	 * 批量保存指标历史记录
	 * */
	@Transactional
    public void savekpiAdjustHistorySql(final List<Object> kpiAdjustHistoryArrayList) {
        this.o_kpiAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_kpi_adjust_history " +
                		"(ID, KPI_ID, ADJUST_TYPE, ADJUST_TIME, IS_LATEST, ETREND, TIME_PERIOD_ID, " +
                		"ASSESSEMENT_STATUS, LOCK_STATUS, COMPANY_ID, RISK_STATUS, ASSESS_PLAN_ID, CALCULATE_FORMULA) " +
                		"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (Object object : kpiAdjustHistoryArrayList) {
                	KpiAdjustHistory kpiAdjustHistory = (KpiAdjustHistory)object;
                	
                	pst.setString(1, kpiAdjustHistory.getId());
                	pst.setString(2, kpiAdjustHistory.getKpiId().getId());
                	pst.setString(3, kpiAdjustHistory.getAdjustType());
                	pst.setTimestamp(4, new Timestamp(kpiAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(5, kpiAdjustHistory.getIsLatest());
                	pst.setString(6, kpiAdjustHistory.getEtrend());
                	pst.setString(7, kpiAdjustHistory.getTimePeriod().getId());
                	pst.setString(8, kpiAdjustHistory.getAssessementStatus());
                	pst.setString(9, kpiAdjustHistory.getLockStatus());
                	pst.setString(10, kpiAdjustHistory.getCompanyId().getId());
                	pst.setDouble(11, kpiAdjustHistory.getRiskStatus());
                	pst.setString(12, kpiAdjustHistory.getRiskAssessPlan().getId());
                	pst.setString(13, kpiAdjustHistory.getCalculateFormula());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 保存指标评估结果
	 * */
	@Transactional
	public void mergeKpiAdjustHistory(KpiAdjustHistory kpiAdjustHistory) {
		o_kpiAdjustHistoryDAO.merge(kpiAdjustHistory);
	}
	
	/**
	 * 删除指标评估结果
	 * */
	@Transactional
	public void deleteKpiAdjustHistory(KpiAdjustHistory kpiAdjustHistory) {
		o_kpiAdjustHistoryDAO.delete(kpiAdjustHistory);
	}
	
	/**
	 * 删除指标评估结果
	 * */
	@Transactional
	public void deleteKpiAdjustHistory(ArrayList<String> idsArrayList) {
		SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery("delete from t_rm_kpi_adjust_history where id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 删除指标评估结果
	 * */
	@Transactional
	public void deleteKpiAdjustHistory(String assessPlanId) {
		SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery("delete from t_rm_kpi_adjust_history where assess_plan_id = :assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 批量更新指标历史记录
	 * */
	@Transactional
    public void updatekpiAdjustHistorySql(final List<Object> kpiAdjustHistoryArrayList) {
        this.o_kpiAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " update t_rm_kpi_adjust_history set " +
                		" KPI_ID=?, ADJUST_TYPE=?, ADJUST_TIME=?, IS_LATEST=?, ETREND=?, TIME_PERIOD_ID=?, " +
                		" ASSESSEMENT_STATUS=?, LOCK_STATUS=?, RISK_STATUS=?, CALCULATE_FORMULA=? where ID=? ";
                pst = connection.prepareStatement(sql);
                
                for (Object object : kpiAdjustHistoryArrayList) {
                	KpiAdjustHistory kpiAdjustHistory = (KpiAdjustHistory)object;
                	
                	pst.setString(1, kpiAdjustHistory.getKpiId().getId());
                	pst.setString(2, kpiAdjustHistory.getAdjustType());
                	pst.setTimestamp(3, new Timestamp(kpiAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(4, kpiAdjustHistory.getIsLatest());
                	pst.setString(5, kpiAdjustHistory.getEtrend());
                	pst.setString(6, kpiAdjustHistory.getTimePeriod().getId());
                	pst.setString(7, kpiAdjustHistory.getAssessementStatus());
                	pst.setString(8, kpiAdjustHistory.getLockStatus());
                	pst.setDouble(9, kpiAdjustHistory.getRiskStatus());
                	pst.setString(10, kpiAdjustHistory.getCalculateFormula());
                	pst.setString(11, kpiAdjustHistory.getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 更新指标评估纪录表所有is_latest(最新)为0
	 * */
	@Transactional
	public void updateKpiAdjustHistory(ArrayList<String> idsArrayList){
		SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery(" update t_rm_kpi_adjust_history set is_latest='0' where kpi_id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 更新该计划指标评估纪录表所有is_latest(最新)为1
	 * */
	@Transactional
	public void updateKpiAdjustHistoryByAssessPlanId(String assessPlanId){
		SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery(" update t_rm_kpi_adjust_history set is_latest='1' where assess_plan_id=:assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	public List<KpiAdjustHistory> findKpiAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<KpiAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<KpiAdjustHistory> findTransactionalKpiAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<KpiAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public List<KpiAdjustHistory> findKpiAdjustHistoryByAssessAllList(){
		Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
		List<KpiAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, KpiAdjustHistory> findKpiAdjustHistoryByAssessAllMap(){
		Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
		List<KpiAdjustHistory> list = null;
		list = criteria.list();
		HashMap<String, KpiAdjustHistory> map = new HashMap<String, KpiAdjustHistory>();
		for (KpiAdjustHistory kpiAdjustHistory : list) {
			map.put(kpiAdjustHistory.getId(), kpiAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询该公司下所有指标对应的风险
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Object>> findKpiToRiskByCompanyId(String companyId, ArrayList<String> idsArrayList){
		StringBuffer sql = new StringBuffer();
		HashMap<String, ArrayList<Object>> maps = new HashMap<String, ArrayList<Object>>();
		
		sql.append(" select b.id,b.kpi_name,a.risk_id from t_kpi_kpi_rela_risk a,t_kpi_kpi b where a.etype = 'i' and a.RISK_ID in ");
		sql.append(" (:ids) ");
		sql.append(" and a.kpi_id=b.id ");
        
        SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("ids", idsArrayList);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String kpiId = "";
            String riskId = "";
            
            ArrayList<Object> arrayList = new ArrayList<Object>();
            
            if(null != objects[0]){
            	kpiId = objects[0].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }
            
            arrayList.add(riskId);
            
            if(maps.get(kpiId) != null){
            	maps.get(kpiId).add(riskId);
            }else{
            	maps.put(kpiId, arrayList);
            }
        }
        
        return maps;
	}
	
	/**
	 * 查询该指标评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, KpiAdjustHistory> findGroupAdjustTime(String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select this_.id, this_.kpi_id  y0_, max(this_.adjust_time) " +
				" y1_, this_.risk_status from t_rm_kpi_adjust_history this_ where this_.COMPANY_ID = :companyId group by this_.kpi_id ");
        
        SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
		List<Object[]> list = sqlQuery.list();
		
		HashMap<String, KpiAdjustHistory> map = new HashMap<String, KpiAdjustHistory>();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            KpiAdjustHistory kpiAdjustHistory = new KpiAdjustHistory();
			Kpi kpi = new Kpi();
			TimePeriod timePeriod = new TimePeriod();
			kpiAdjustHistory.setId(objects[0].toString());
			kpi.setId(objects[1].toString());
			if(null != objects[2]){
				timePeriod.setId(objects[2].toString());
				kpiAdjustHistory.setTimePeriod(timePeriod);
			}else{
				kpiAdjustHistory.setTimePeriod(null);
			}
			
			if(null != objects[3]){
				kpiAdjustHistory.setRiskStatus(Double.parseDouble(objects[3].toString()));
			}else{
				kpiAdjustHistory.setRiskStatus(null);
			}
			
			
			kpiAdjustHistory.setKpiId(kpi);
			
			map.put(kpiAdjustHistory.getKpiId().getId(), kpiAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询该指标评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<KpiAdjustHistory>> findKpiAdjustHistoryById(){
		Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("isLatest", "1"));
		criteria.addOrder(Order.desc("adjustTime"));
		HashMap<String, ArrayList<KpiAdjustHistory>> map = new HashMap<String, ArrayList<KpiAdjustHistory>>();
		
		List<KpiAdjustHistory> list = criteria.list();
		for (KpiAdjustHistory kpiAdjustHistory : list) {
			try {
				ArrayList<KpiAdjustHistory> arrayList = new ArrayList<KpiAdjustHistory>();
				arrayList.add(kpiAdjustHistory);
				
				if(map.get(kpiAdjustHistory.getKpiId().getId()) != null){
					map.get(kpiAdjustHistory.getKpiId().getId() + "--" + kpiAdjustHistory.getIsLatest()).add(kpiAdjustHistory);
				}else{
					map.put(kpiAdjustHistory.getKpiId().getId() + "--" + kpiAdjustHistory.getIsLatest(), arrayList);
				}
			} catch (Exception e) {
				log.error(kpiAdjustHistory + "无对象");
			}
		}
		
		return map;
	}
	
	/**
	 * 查询评估记录该评估计划与次ID对应的数据
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, KpiAdjustHistory>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, KpiAdjustHistory> findKpiAdjustHistoryByIdAndAssessPlanId(String assessPlanId){
		HashMap<String, KpiAdjustHistory> map = new HashMap<String, KpiAdjustHistory>();
		try {
			Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			List<KpiAdjustHistory> list = criteria.list();
			for (KpiAdjustHistory kpiAdjustHistory : list) {
				map.put(kpiAdjustHistory.getKpiId().getId(), kpiAdjustHistory);
			}
		} catch (Exception e) {
			
		}
		
		return map;
	}
	
	/**
	 * 查询该计划下指标下的风险
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenstrategyRisks(String assessPlanId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		HashMap<String, List<StatisticsResult>> statisticsResultMap = 
				o_statisticsResultBO.findStatisticsResultListByAssessPlanIdAllMap(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.kpi_id,a.risk_id,c.risk_name,a.risk_status,a.assessement_status " +
				" from t_rm_risk_adjust_history a LEFT JOIN t_kpi_kpi_rela_risk b on b.risk_id = a.risk_id LEFT JOIN t_rm_risks c on c.id=a.risk_id " +
				" where b.etype='i' and a.assess_plan_id=:assessPlanId ");
        
        SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String kpiId = "";
            String riskId = "";
            String riskName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            String dimId = "";
            String score = "";
            
            if(null != objects[0]){
            	kpiId = objects[0].toString();
            }if(null != objects[1]){
            	riskId = objects[1].toString();
            }if(null != objects[2]){
            	riskName = objects[2].toString();
            }if(null != objects[3]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[3].toString()),2).toString();
            }if(null != objects[4]){
            	assessmentStatus = objects[4].toString();
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
            if(childrenMap.get(kpiId) != null){
            	childrenMap.get(kpiId).add(riskMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(riskMap);
            	childrenMap.put(kpiId, arrayList);
            }
        }
		return childrenMap;
	}
}
