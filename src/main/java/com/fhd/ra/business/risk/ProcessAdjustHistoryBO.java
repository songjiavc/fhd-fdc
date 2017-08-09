package com.fhd.ra.business.risk;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.risk.ProcessAdjustHistoryDAO;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.process.Process;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.fdc.utils.NumberUtil;
import com.fhd.ra.business.assess.oper.StatisticsResultBO;

@Service
public class ProcessAdjustHistoryBO {

	@Autowired
	private ProcessAdjustHistoryDAO o_processAdjustHistoryDAO;
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	private Log log = LogFactory.getLog(ProcessAdjustHistoryBO.class);
	
	/**
	 * 批量保存流程历史记录
	 * */
	@Transactional
    public void saveProcessAdjustHistorySql(final List<Object> processAdjustHistoryArrayList) {
        this.o_processAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_processure_adjust_history " +
                		"(ID, PROCESSURE_ID, ADJUST_TYPE, ADJUST_TIME, IS_LATEST, ETREND, TIME_PERIOD_ID," +
                		" ASSESSEMENT_STATUS, LOCK_STATUS, COMPANY_ID, RISK_STATUS, " +
                		"ASSESS_PLAN_ID, CALCULATE_FORMULA) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (Object object : processAdjustHistoryArrayList) {
                	ProcessAdjustHistory processAdjustHistory = (ProcessAdjustHistory)object;
                	
                	pst.setString(1, processAdjustHistory.getId());
                	pst.setString(2, processAdjustHistory.getProcess().getId());
                	pst.setString(3, processAdjustHistory.getAdjustType());
                	pst.setTimestamp(4, new Timestamp(processAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(5, processAdjustHistory.getIsLatest());
                	pst.setString(6, processAdjustHistory.getEtrend());
                	pst.setString(7, processAdjustHistory.getTimePeriod().getId());
                	pst.setString(8, processAdjustHistory.getAssessementStatus());
                	pst.setString(9, null);
                	pst.setString(10, processAdjustHistory.getCompany().getId());
                	pst.setDouble(11, processAdjustHistory.getRiskStatus());
                	pst.setString(12, processAdjustHistory.getRiskAssessPlan().getId());
                	pst.setString(13, processAdjustHistory.getCalculateFormula());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 保存流程风险评估纪录表
	 * */
	@Transactional
	public void mergeProcessAdjustHistory(ProcessAdjustHistory processAdjustHistory) {
		o_processAdjustHistoryDAO.merge(processAdjustHistory);
	}
	
	/**
	 * 删除流程风险评估纪录表
	 * */
	@Transactional
	public void deleteProcessAdjustHistory(ProcessAdjustHistory processAdjustHistory) {
		o_processAdjustHistoryDAO.delete(processAdjustHistory);
	}
	
	/**
	 * 删除流程风险评估纪录表
	 * */
	@Transactional
	public void deleteProcessAdjustHistory(ArrayList<String> idsArrayList) {
		SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery("delete from t_rm_processure_adjust_history where id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 删除流程评估结果
	 * */
	@Transactional
	public void deleteProcessAdjustHistory(String assessPlanId) {
		SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery("delete from t_rm_processure_adjust_history where assess_plan_id = :assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 批量更新流程历史记录
	 * */
	@Transactional
    public void updateProcessAdjustHistorySql(final List<Object> processAdjustHistoryArrayList) {
        this.o_processAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " update t_rm_processure_adjust_history set " +
                		" PROCESSURE_ID=?, ADJUST_TYPE=?, ADJUST_TIME=?, IS_LATEST=?, ETREND=?, TIME_PERIOD_ID=?, " +
                		" ASSESSEMENT_STATUS=?, LOCK_STATUS=?, RISK_STATUS=?, " +
                		" CALCULATE_FORMULA=? where ID=? ";
                pst = connection.prepareStatement(sql);
                
                for (Object object : processAdjustHistoryArrayList) {
                	ProcessAdjustHistory processAdjustHistory = (ProcessAdjustHistory)object;
                	
                	pst.setString(1, processAdjustHistory.getProcess().getId());
                	pst.setString(2, processAdjustHistory.getAdjustType());
                	pst.setTimestamp(3, new Timestamp(processAdjustHistory.getAdjustTime().getTime()));
                	pst.setString(4, processAdjustHistory.getIsLatest());
                	pst.setString(5, processAdjustHistory.getEtrend());
                	pst.setString(6, processAdjustHistory.getTimePeriod().getId());
                	pst.setString(7, processAdjustHistory.getAssessementStatus());
                	pst.setString(8, null);
                	pst.setDouble(9, processAdjustHistory.getRiskStatus());
                	pst.setString(10, processAdjustHistory.getCalculateFormula());
                	pst.setString(11, processAdjustHistory.getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 更新流程评估纪录表所有is_latest(最新)为0
	 * */
	@Transactional
	public void updateProcessAdjustHistory(String processureIds){
		o_processAdjustHistoryDAO.createSQLQuery(" update t_rm_processure_adjust_history set is_latest='0' " +
				"where processure_id in (" + processureIds + ") ").executeUpdate();
	}
	
	/**
	 * 更新该计划流程评估纪录表所有is_latest(最新)为1
	 * */
	@Transactional
	public void updateProcessAdjustHistoryByAssessPlanId(String assessPlanId){
		o_processAdjustHistoryDAO.createSQLQuery(" update t_rm_processure_adjust_history set is_latest='1' " +
				"where assess_plan_id='" + assessPlanId + "'").executeUpdate();
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	public List<ProcessAdjustHistory> findProcessAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<ProcessAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProcessAdjustHistory> findTransactionalProcessAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<ProcessAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询评估计划下的流程评估记录,已MAP方式存储(processure_id, ProcessAdjustHistory)
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ProcessAdjustHistory> findProcessAdjustHistoryByAssessPlanIdMapAll(String assessPlanId){
		HashMap<String, ProcessAdjustHistory> map = new HashMap<String, ProcessAdjustHistory>();
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<ProcessAdjustHistory> list = null;
		list = criteria.list();
		for (ProcessAdjustHistory processAdjustHistory : list) {
			map.put(processAdjustHistory.getProcess().getId(), processAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public List<ProcessAdjustHistory> findProcessAdjustHistoryByAssessAllList(){
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		List<ProcessAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ProcessAdjustHistory> findProcessAdjustHistoryByAssessAllMap(){
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		List<ProcessAdjustHistory> list = null;
		list = criteria.list();
		HashMap<String, ProcessAdjustHistory> map = new HashMap<String, ProcessAdjustHistory>();
		for (ProcessAdjustHistory processAdjustHistory : list) {
			map.put(processAdjustHistory.getId(), processAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询该评估计划下,流程下风险事件,已MAP方式存储(processure_id, List<Map<String,Object>>)
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Object>> findProcessAdjustHistoryProcessureRisksMapAll(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		HashMap<String, ArrayList<Object>> maps = new HashMap<String, ArrayList<Object>>();
		
		sql.append(" select a.processure_id,d.id,d.risk_name,c.risk_status,c.assessement_status ");
		sql.append(" from t_processure_risk_processure a, t_rm_risk_score_object b, t_rm_risk_adjust_history c , t_rm_risks d ");
		sql.append(" where b.ASSESS_PLAN_ID=:assessPlanId and c.ASSESS_PLAN_ID=:assessPlanId ");
		sql.append(" and a.risk_id=b.risk_id and a.etype='i' and c.risk_id=b.risk_id and b.risk_id=d.id   ");
		sql.append(" ORDER BY c.risk_status desc ");
		
		SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		
		List<Object[]> list = sqlQuery.list();
		for(Object[] o:list){
			if(maps.get(o[0].toString()) != null){
				maps.get(o[0].toString()).add(
						o[0].toString() + "--" + o[1].toString() + "--" + o[2].toString() + "--" + 
				NumberUtil.meg(new Double(o[3].toString()), 2) + "--" + o[4].toString());
			}else{
				ArrayList<Object> arrayList = new ArrayList<Object>();
				arrayList.add(
						o[0].toString() + "--" + o[1].toString() + "--" + o[2].toString() + "--" + 
				NumberUtil.meg(new Double(o[3].toString()), 2) + "--" + o[4].toString());
				maps.put(o[0].toString(), arrayList);
			}
		}
		
		return maps;
	}
	
	/**
	 * 查询该公司下所有流程对应的风险
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Object>> findProcessureToRiskByCompanyId(String companyId, ArrayList<String> riskIds){
		StringBuffer sql = new StringBuffer();
		HashMap<String, ArrayList<Object>> maps = new HashMap<String, ArrayList<Object>>();
		sql.append(" select processure_id,risk_id from t_processure_risk_processure where etype = 'I' and risk_id in (:riskIds) ");
        SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("riskIds", riskIds);
        
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String processureId = "";
            String riskId = "";
            
            ArrayList<Object> arrayList = new ArrayList<Object>();
            
            if(null != objects[0]){
            	processureId = objects[0].toString();
            }
            if(null != objects[1]){
            	riskId = objects[1].toString();
            }
            
            arrayList.add(riskId);
            
            if(maps.get(processureId) != null){
            	maps.get(processureId).add(riskId);
            }else{
            	maps.put(processureId, arrayList);
            }
        }
        
        return maps;
	}
	
	/**
	 * 查询该流程评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ProcessAdjustHistory> findGroupAdjustTime(String companyId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select this_.id, this_.processure_id as y0_, max(this_.adjust_time) "
				+ "as y1_, this_.risk_status from t_rm_processure_adjust_history this_ where " +
				"this_.COMPANY_ID = :companyId group by this_.processure_id ");

		SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql
				.toString());
		sqlQuery.setParameter("companyId", companyId);
		List<Object[]> list = sqlQuery.list();

		HashMap<String, ProcessAdjustHistory> map = new HashMap<String, ProcessAdjustHistory>();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
			ProcessAdjustHistory processAdjustHistory = new ProcessAdjustHistory();
			Process process = new Process();
			TimePeriod timePeriod = new TimePeriod();
			processAdjustHistory.setId(objects[0].toString());
			process.setId(objects[1].toString());
			if (null != objects[2]) {
				timePeriod.setId(objects[2].toString());
				processAdjustHistory.setTimePeriod(timePeriod);
			} else {
				processAdjustHistory.setTimePeriod(null);
			}

			if (null != objects[3]) {
				processAdjustHistory.setRiskStatus(Double
						.parseDouble(objects[3].toString()));
			} else {
				processAdjustHistory.setRiskStatus(null);
			}

			processAdjustHistory.setProcess(process);

			map.put(processAdjustHistory.getProcess().getId(),
					processAdjustHistory);
		}

		return map;
	}

	/**
	 * 查询该流程评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<ProcessAdjustHistory>> findProcessAdjustHistoryById() {
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("isLatest", "1"));
		criteria.addOrder(Order.desc("adjustTime"));
		HashMap<String, ArrayList<ProcessAdjustHistory>> map = new HashMap<String, ArrayList<ProcessAdjustHistory>>();

		List<ProcessAdjustHistory> list = criteria.list();
		for (ProcessAdjustHistory processAdjustHistory : list) {
			try {
				ArrayList<ProcessAdjustHistory> arrayList = new ArrayList<ProcessAdjustHistory>();
				arrayList.add(processAdjustHistory);

				if (map.get(processAdjustHistory.getProcess().getId()) != null) {
					map.get(processAdjustHistory.getProcess().getId() + "--" + processAdjustHistory.getIsLatest()).add(processAdjustHistory);
				} else {
					map.put(processAdjustHistory.getProcess().getId() + "--" + processAdjustHistory.getIsLatest(), arrayList);
				}
			} catch (Exception e) {
				log.error(processAdjustHistory + "无对象");
			}
		}

		return map;
	}

	/**
	 * 查询评估记录该评估计划与次ID对应的数据
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ProcessAdjustHistory> findProcessureAdjustHistoryByIdAndAssessPlanId(String assessPlanId){
		HashMap<String, ProcessAdjustHistory> map = new HashMap<String, ProcessAdjustHistory>();
		try {
			Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			List<ProcessAdjustHistory> list = criteria.list();
			for (ProcessAdjustHistory processAdjustHistory : list) {
				map.put(processAdjustHistory.getProcess().getId(), processAdjustHistory);
			}
		} catch (Exception e) {
			
		}
		
		return map;
	}
	
	
	/**
	 * 查询根级风险集合
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findParentProcess(String assessPlanId, String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id,a.processure_name,a.company_id, a.parent_id " +
				" from t_ic_processure a, t_rm_processure_adjust_history b " +
				" where a.id = b.processure_id and a.company_id=:companyId and b.assess_plan_id = :assessPlanId" + 
				" and a.parent_id is not null ");
        
        SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String processId = "";
            String parentId = "";
            
            if(null != objects[0]){
            	processId = objects[0].toString();
            }if(null != objects[3]){
            	parentId = objects[3].toString();
            }
            
            if(map.get(parentId) != null){
            	map.get(parentId).add(processId);
            }else{
            	ArrayList<String> arrayList = new ArrayList<String>();
            	arrayList.add(processId);
            	map.put(parentId, arrayList);
            }
        }
		
		return map;
	}
	
	
	
	/**
	 * 通过风险事件、分类寻找根级风险
	 * */
	@SuppressWarnings({ "unchecked", "unused" })
	public ArrayList<String> findRootProcess(String assessPlanId, String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id,a.id_seq " +
				" from t_ic_processure a, t_rm_processure_adjust_history b " +
				" where a.id = b.processure_id and a.company_id=:companyId and b.assess_plan_id = :assessPlanId");
        
        SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        ArrayList<String> arrayList = new ArrayList<String>();
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String processId = "";
            String idSeq = "";
            
            if(null != objects[0]){
            	processId = objects[0].toString();
            }if(null != objects[1]){
            	idSeq = objects[1].toString();
            }
            
            arrayList.add(idSeq);
        }
		
		return arrayList;
	}
	
	/**
	 * 根据计划id查询流程信息
	 * @param assessPlanId
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findParentProcessRisks(String assessPlanId,String query){
		List<Map<String, Object>> item = new ArrayList<Map<String,Object>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskMap = this.findChildrenProcessRisks(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.id,b.processure_name,a.risk_status,a.assessement_status " +
				" from t_rm_processure_adjust_history a LEFT JOIN t_ic_processure b on a.processure_id = b.id " +
				" where  a.assess_plan_id=:assessPlanId");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (b.processure_name like :query or a.risk_status like :query) ");
		}
        
        SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        if(StringUtils.isNotBlank(query)){
            sqlQuery.setParameter("query", "%"+query+"%");
        }
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String processId = "";
            String processName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            
            if(null != objects[0]){
            	processId = objects[0].toString();
            }if(null != objects[1]){
            	processName = objects[1].toString();
            }if(null != objects[2]){
            	//riskStatus = objects[2].toString();
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[2].toString()),2).toString();
            }if(null != objects[3]){
            	assessmentStatus = objects[3].toString();
            }
            
            Map<String, Object> orgMap = new HashMap<String, Object>();
            orgMap.put("processId", processId);
            orgMap.put("name", processName);
            orgMap.put("score", riskStatus);
            orgMap.put("iconCls", assessmentStatus);
            if(null != childrenRiskMap.get(processId)){
            	orgMap.put("children", childrenRiskMap.get(processId));//部门下的风险
            	orgMap.put("leaf", false);
            }else{
            	orgMap.put("leaf", true);
            }
            orgMap.put("linked", true);
            item.add(orgMap);
        }
		
		return item;
	}
	
	/**
	 * 根据计划id查询流程下所有风险信息
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenProcessRisks(String assessPlanId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		List<StatisticsResult> statisticResultList = new ArrayList<StatisticsResult>();
		HashMap<String, List<StatisticsResult>> statisticsResultMap = 
				o_statisticsResultBO.findStatisticsResultListByAssessPlanIdAllMap(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.PROCESSURE_ID,c.id,c.risk_name,b.risk_status,b.assessement_status " +
				" from T_PROCESSURE_RISK_PROCESSURE a LEFT JOIN t_rm_risk_adjust_history b on a.risk_id=b.risk_id LEFT JOIN t_rm_risks c on c.id = a.risk_id " +
				" where a.ETYPE='I' and b.assess_plan_id=:assessPlanId");
        
        SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String processId = "";
            String riskId = "";
            String riskName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            String dimId = "";
            String score = "";
            
            if(null != objects[0]){
            	processId = objects[0].toString();
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
            statisticResultList = statisticsResultMap.get(riskId);
            for(StatisticsResult statis : statisticResultList){
            	dimId = statis.getDimension().getId();
				score = statis.getScore();
				riskMap.put(dimId, NumberUtil.meg(Double.parseDouble(score), 2));
            }
            if(childrenMap.get(processId) != null){
            	childrenMap.get(processId).add(riskMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(riskMap);
            	childrenMap.put(processId, arrayList);
            }
        }
		return childrenMap;
	}
	
	/**
	 * 导出流程统计列表
	 * add by 王再冉
	 * 2013-12-27  下午5:15:21
	 * desc : 
	 * @param processList
	 * @param indexList
	 * @param j
	 * @return 
	 * List<Object[]>
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> exportProcessCountGrid(List<Map<String, Object>> processList, List<String> indexList,int j){
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		Object[] childObjects = null;
		for(Map<String, Object> riskMap : processList){
			objects = new Object[j];
			List<Object[]> childObjList = new ArrayList<Object[]>();
			List<Map<String, Object>> childrenlist = new ArrayList<Map<String,Object>>();
			if(null != riskMap.get("children")){
				childrenlist = (List<Map<String, Object>>)riskMap.get("children");
				for(Map<String, Object> children :childrenlist){
					childObjects = new Object[j];
					for(int m=0;m<indexList.size();m++){
						childObjects[m] = children.get(indexList.get(m)).toString();
					}
					childObjList.add(childObjects);
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