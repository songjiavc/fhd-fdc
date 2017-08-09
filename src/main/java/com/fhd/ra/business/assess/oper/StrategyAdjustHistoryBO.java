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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.risk.StrategyAdjustHistoryDAO;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.risk.StrategyAdjustHistory;
import com.fhd.fdc.utils.NumberUtil;

@Service
public class StrategyAdjustHistoryBO {

	@Autowired
	private StrategyAdjustHistoryDAO o_strategyAdjustHistoryDAO;
	@Autowired
	private KpiAdjustHistoryBO o_kpiAdjustHistoryBO;
	
	/**
	 * 批量保存目标历史记录
	 * */
	@Transactional
    public void saveStrategyAdjustHistorySql(final List<Object> strategyAdjustHistoryArrayList) {
        this.o_strategyAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_strategy_adjust_history " +
                		"(ID, STRATEGY_ID, ADJUST_TYPE, ADJUST_TIME, IS_LATEST, ETREND, TIME_PERIOD_ID, " +
                		"ASSESSEMENT_STATUS, LOCK_STATUS, COMPANY_ID, RISK_STATUS, ASSESS_PLAN_ID, " +
                		"CALCULATE_FORMULA) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (Object object : strategyAdjustHistoryArrayList) {
                	StrategyAdjustHistory strategyAdjustHistory = (StrategyAdjustHistory)object;
                	
                	pst.setString(1, strategyAdjustHistory.getId());
                	pst.setString(2, strategyAdjustHistory.getStrategyMap().getId());
                	pst.setString(3, strategyAdjustHistory.getAdjustType());
                	pst.setTimestamp(4, new Timestamp(strategyAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(5, strategyAdjustHistory.getIsLatest());
                	pst.setString(6, strategyAdjustHistory.getEtrend());
                	pst.setString(7, strategyAdjustHistory.getTimePeriod().getId());
                	pst.setString(8, strategyAdjustHistory.getAssessementStatus());
                	pst.setString(9, null);
                	pst.setString(10, strategyAdjustHistory.getCompanyId().getId());
                	pst.setDouble(11, strategyAdjustHistory.getRiskStatus());
                	pst.setString(12, strategyAdjustHistory.getRiskAssessPlan().getId());
                	pst.setString(13, strategyAdjustHistory.getCalculateFormula());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 保存目标评估结果
	 * */
	@Transactional
	public void mergeStrategyAdjustHistory(StrategyAdjustHistory strategyAdjustHistory) {
		o_strategyAdjustHistoryDAO.merge(strategyAdjustHistory);
	}
	
	/**
	 * 删除目标评估结果
	 * */
	@Transactional
	public void deleteStrategyAdjustHistory(StrategyAdjustHistory strategyAdjustHistory) {
		o_strategyAdjustHistoryDAO.delete(strategyAdjustHistory);
	}
	
	/**
	 * 删除目标评估结果
	 * */
	@Transactional
	public void deleteStrategyAdjustHistory(ArrayList<String> idsArrayList) {
		SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery("delete from t_rm_strategy_adjust_history where id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 删除目标评估结果
	 * */
	@Transactional
	public void deleteStrategyAdjustHistory(String assessPlanId) {
		SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery("delete from t_rm_strategy_adjust_history where assess_plan_id = :assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 批量更新目标历史记录
	 * */
	@Transactional
    public void updateStrategyAdjustHistorySql(final List<Object> strategyAdjustHistoryArrayList) {
        this.o_strategyAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " update t_rm_strategy_adjust_history set " +
                		" STRATEGY_ID=?, ADJUST_TYPE=?, ADJUST_TIME=?, IS_LATEST=?, ETREND=?, TIME_PERIOD_ID=?, " +
                		" ASSESSEMENT_STATUS=?, LOCK_STATUS=?, RISK_STATUS=?, " +
                		" CALCULATE_FORMULA=? where ID=? ";
                pst = connection.prepareStatement(sql);
                
                for (Object object : strategyAdjustHistoryArrayList) {
                	StrategyAdjustHistory strategyAdjustHistory = (StrategyAdjustHistory)object;
                	
                	pst.setString(1, strategyAdjustHistory.getStrategyMap().getId());
                	pst.setString(2, strategyAdjustHistory.getAdjustType());
                	pst.setTimestamp(3, new Timestamp(strategyAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(4, strategyAdjustHistory.getIsLatest());
                	pst.setString(5, strategyAdjustHistory.getEtrend());
                	pst.setString(6, strategyAdjustHistory.getTimePeriod().getId());
                	pst.setString(7, strategyAdjustHistory.getAssessementStatus());
                	pst.setString(8, null);
                	pst.setDouble(9, strategyAdjustHistory.getRiskStatus());
                	pst.setString(10, strategyAdjustHistory.getCalculateFormula());
                	pst.setString(11, strategyAdjustHistory.getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 更新目标评估纪录表所有is_latest(最新)为0
	 * */
	@Transactional
	public void updateStrategyAdjustHistory(ArrayList<String> idsArrayList){
		SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery(" update t_rm_strategy_adjust_history set is_latest='0' where strategy_id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 更新该计划目标评估纪录表所有is_latest(最新)为1
	 * */
	@Transactional
	public void updateStrategyAdjustHistoryByAssessPlanId(String assessPlanId){
		SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery(
				" update t_rm_strategy_adjust_history set is_latest='1' where assess_plan_id=:assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	public List<StrategyAdjustHistory> findStrategyAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<StrategyAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<StrategyAdjustHistory> findTransactionalStrategyAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<StrategyAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询该公司下所有目标下指标(内涵风险)和权重
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Object>> findStrategyToRiskByCompanyId(String companyId, ArrayList<String> ids, String assessPlanId){
		StringBuffer sql = new StringBuffer();
		HashMap<String, ArrayList<Object>> maps = new HashMap<String, ArrayList<Object>>();
        sql.append(" select a.strategy_map_id, a.kpi_id, a.eweight ");
        sql.append(" from t_kpi_sm_rela_kpi a, t_kpi_strategy_map b , ");
        sql.append(" (select KPI_ID  from t_kpi_kpi_rela_risk where RISK_ID in (:ids) and etype = 'i' GROUP BY KPI_ID) c ");
        sql.append("where a.strategy_map_id = b.id and b.company_id=:companyId and a.KPI_ID = c.KPI_ID ORDER BY a.strategy_map_id");
        SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("ids", ids);
        sqlQuery.setParameter("companyId", companyId);
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String categoryId = "";
            String kpiId = "";
            double eweight = 0l;
            
            ArrayList<Object> arrayList = new ArrayList<Object>();
            
            if(null != objects[0]){
            	categoryId = objects[0].toString();
            }if(null != objects[1]){
            	kpiId = objects[1].toString();
            }if(null != objects[2]){
            	eweight = Double.parseDouble(objects[2].toString());
            }
            
            arrayList.add(kpiId + "--" + eweight);
            
            if(maps.get(categoryId) != null){
            	maps.get(categoryId).add(kpiId + "--" + eweight);
            }else{
            	maps.put(categoryId, arrayList);
            }
        }
        
        return maps;
	}
	
	/**
	 * 查询该指标评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, StrategyAdjustHistory> findGroupAdjustTime(String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select this_.id, this_.strategy_id  y0_, max(this_.adjust_time) " +
				" y1_, this_.risk_status from t_rm_strategy_adjust_history this_ where " +
				"this_.COMPANY_ID = :companyId group by this_.strategy_id ");
        SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
		List<Object[]> list = sqlQuery.list();
		
		HashMap<String, StrategyAdjustHistory> map = new HashMap<String, StrategyAdjustHistory>();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            StrategyAdjustHistory strategyAdjustHistory = new StrategyAdjustHistory();
			StrategyMap strategyMap = new StrategyMap();
			TimePeriod timePeriod = new TimePeriod();
			strategyAdjustHistory.setId(objects[0].toString());
			strategyMap.setId(objects[1].toString());
			if(null != objects[2]){
				timePeriod.setId(objects[2].toString());
				strategyAdjustHistory.setTimePeriod(timePeriod);
			}else{
				strategyAdjustHistory.setTimePeriod(null);
			}
			
			if(null != objects[3]){
				strategyAdjustHistory.setRiskStatus(Double.parseDouble(objects[3].toString()));
			}else{
				strategyAdjustHistory.setRiskStatus(null);
			}
			
			
			strategyAdjustHistory.setStrategyMap(strategyMap);
			
			map.put(strategyAdjustHistory.getStrategyMap().getId(), strategyAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询该指标评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<StrategyAdjustHistory>> findStrategyAdjustHistoryById(){
		Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("isLatest", "1"));
		criteria.addOrder(Order.desc("adjustTime"));
		HashMap<String, ArrayList<StrategyAdjustHistory>> map = new HashMap<String, ArrayList<StrategyAdjustHistory>>();
		
		List<StrategyAdjustHistory> list = criteria.list();
		for (StrategyAdjustHistory strategyAdjustHistory : list) {
			ArrayList<StrategyAdjustHistory> arrayList = new ArrayList<StrategyAdjustHistory>();
			arrayList.add(strategyAdjustHistory);
			
			if(map.get(strategyAdjustHistory.getStrategyMap().getId()) != null){
				map.get(strategyAdjustHistory.getStrategyMap().getId() + "--" + strategyAdjustHistory.getIsLatest()).add(strategyAdjustHistory);
			}else{
				map.put(strategyAdjustHistory.getStrategyMap().getId() + "--" + strategyAdjustHistory.getIsLatest(), arrayList);
			}
		}
		
		return map;
	}
	
	/**
	 * 查询评估记录该评估计划与次ID对应的数据
	 * */
	public HashMap<String, StrategyAdjustHistory> findStrategyMapAdjustHistoryByIdAndAssessPlanId(String assessPlanId){
		HashMap<String, StrategyAdjustHistory> map = new HashMap<String, StrategyAdjustHistory>();
		try {
			Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			@SuppressWarnings("unchecked")
			List<StrategyAdjustHistory> list = criteria.list();
			for (StrategyAdjustHistory strategyAdjustHistory : list) {
				map.put(strategyAdjustHistory.getStrategyMap().getId(), strategyAdjustHistory);
			}
		} catch (Exception e) {
			
		}
		
		return map;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public List<StrategyAdjustHistory> findStrategyAdjustHistoryByAssessAllList(){
		Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
		List<StrategyAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, StrategyAdjustHistory> findStrategyAdjustHistoryByAssessAllMap(){
		Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
		List<StrategyAdjustHistory> list = null;
		list = criteria.list();
		HashMap<String, StrategyAdjustHistory> map = new HashMap<String, StrategyAdjustHistory>();
		for (StrategyAdjustHistory strategyAdjustHistory : list) {
			map.put(strategyAdjustHistory.getId(), strategyAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 根据计划id查询目标
	 * @param assessPlanId
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findParentStrategyRisks(String assessPlanId,String query){
		List<Map<String, Object>> item = new ArrayList<Map<String,Object>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenKpiMap = this.findChildrenKpis(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.id,b.strategy_map_name,a.risk_status,a.assessement_status " +
				" from t_rm_strategy_adjust_history a LEFT JOIN t_kpi_strategy_map b on a.strategy_id = b.id " +
				" where  a.assess_plan_id=:assessPlanId ");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (b.strategy_map_name like :query or a.risk_status like :query) ");
		}
        SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        if(StringUtils.isNotBlank(query)){
        	sqlQuery.setParameter("query", "%"+query+"%");
        }
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String strategyId = "";
            String strategyName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            
            if(null != objects[0]){
            	strategyId = objects[0].toString();
            }if(null != objects[1]){
            	strategyName = objects[1].toString();
            }if(null != objects[2]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[2].toString()),2).toString();
            }if(null != objects[3]){
            	assessmentStatus = objects[3].toString();
            }
            Map<String, Object> strategyMap = new HashMap<String, Object>();
            strategyMap.put("strategyId", strategyId);
            strategyMap.put("name", strategyName);
            strategyMap.put("score", riskStatus);
            strategyMap.put("iconCls", assessmentStatus);
            if(null != childrenKpiMap.get(strategyId)){
            	strategyMap.put("children", childrenKpiMap.get(strategyId));//部门下的风险
            	strategyMap.put("leaf", false);
            }else{
            	strategyMap.put("leaf", true);
            }
            strategyMap.put("linked", true);
            item.add(strategyMap);
        }
		
		return item;
	}
	
	/**
	 * 查询计划下目标的所有指标
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenKpis(String assessPlanId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenRisksMap = o_kpiAdjustHistoryBO.findChildrenstrategyRisks(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select strategy_map_id,a.kpi_id,c.kpi_name,a.risk_status,a.assessement_status " +
				" from t_rm_kpi_adjust_history a LEFT JOIN t_kpi_sm_rela_kpi b on a.kpi_id=b.kpi_id LEFT JOIN t_kpi_kpi c on c.id=a.kpi_id " +
				" where a.assess_plan_id=:assessPlanId");
        SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String strategyId = "";
            String kpiId = "";
            String kpiName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            
            if(null != objects[0]){
            	strategyId = objects[0].toString();
            }if(null != objects[1]){
            	kpiId = objects[1].toString();
            }if(null != objects[2]){
            	kpiName = objects[2].toString();
            }if(null != objects[3]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[3].toString()),2).toString();
            }if(null != objects[4]){
            	assessmentStatus = objects[4].toString();
            }
            
            Map<String, Object> kpiMap = new HashMap<String, Object>();
            kpiMap.put("kpiId", kpiId);
            kpiMap.put("name", kpiName);
            kpiMap.put("score", riskStatus);//风险分值
            kpiMap.put("iconCls", assessmentStatus);//风险水平
            kpiMap.put("linked", true);
            if(null != childrenRisksMap.get(kpiId)){
            	kpiMap.put("children", childrenRisksMap.get(kpiId));//部门下的风险
            	kpiMap.put("leaf", false);
            }else{
            	kpiMap.put("leaf", true);
            }
            if(childrenMap.get(strategyId) != null){
            	childrenMap.get(strategyId).add(kpiMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(kpiMap);
            	childrenMap.put(strategyId, arrayList);
            }
        }
		return childrenMap;
	}
	
	/**
	 * 导出目标统计列表
	 * add by 王再冉
	 * 2013-12-27  下午5:18:51
	 * desc : 
	 * @param kpiList
	 * @param indexList
	 * @param j
	 * @return 
	 * List<Object[]>
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> exportKpiCountGrid(List<Map<String, Object>> kpiList, List<String> indexList,int j){
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		Object[] childObjects = null;
		Object[] riskChildObjects = null;
		for(Map<String, Object> riskMap : kpiList){
			objects = new Object[j];
			List<Object[]> childObjList = new ArrayList<Object[]>();
			if(null != riskMap.get("children")){
				List<Map<String, Object>> childrenlist = (List<Map<String, Object>>)riskMap.get("children");
				for(Map<String, Object> children :childrenlist){//目标下取指标
					childObjects = new Object[j];
					List<Object[]> riskChildObjList = new ArrayList<Object[]>();
					if(null != children.get("children")){//指标下取风险
						riskChildObjects = new Object[j];
						List<Map<String, Object>> riskChildrenList = (List<Map<String, Object>>)children.get("children");
						for(Map<String, Object> riskChildren : riskChildrenList){
							for(int m=0;m<indexList.size();m++){
								riskChildObjects[m] = riskChildren.get(indexList.get(m)).toString();
							}
							riskChildObjList.add(riskChildObjects);
						}
					}
					for(int m=0;m<indexList.size();m++){
						if(null != children.get(indexList.get(m))){
							childObjects[m] = children.get(indexList.get(m)).toString();
						}
					}
					childObjList.add(childObjects);
					childObjList.addAll(riskChildObjList);
				}
			}
			for(int m=0;m<indexList.size();m++){
				if(null!=riskMap.get(indexList.get(m))){
					objects[m] = riskMap.get(indexList.get(m)).toString();
				}
			}
			list.add(objects);
			list.addAll(childObjList);
		}
		return list;
	}
}