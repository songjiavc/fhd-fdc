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

import com.fhd.dao.risk.OrgAdjustHistoryDAO;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.NumberUtil;

@Service
public class OrgAdjustHistoryBO {

	@Autowired
	private OrgAdjustHistoryDAO o_orgAdjustHistoryDAO;
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	private Log log = LogFactory.getLog(OrgAdjustHistoryBO.class);
	
	/**
	 * 批量添加组织记录
	 * @param 组织历史实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveOrgAdjustHistorySql(final ArrayList<Object> orgAdjustHistoryList) {
        this.o_orgAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_org_adjust_history " +
                		"(ID, ORG_ID, ADJUST_TYPE, ADJUST_TIME, IS_LATEST, ETREND, TIME_PERIOD_ID, " +
                		"ASSESSEMENT_STATUS, LOCK_STATUS, COMPANY_ID, RISK_STATUS, ASSESS_PLAN_ID, CALCULATE_FORMULA) " +
                		"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (Object object : orgAdjustHistoryList) {
                	OrgAdjustHistory orgAdjustHistory = (OrgAdjustHistory)object;
					pst.setString(1, orgAdjustHistory.getId());
					pst.setString(2, orgAdjustHistory.getOrganization().getId());
					pst.setString(3, orgAdjustHistory.getAdjustType());
					pst.setTimestamp(4, new java.sql.Timestamp(orgAdjustHistory.getAdjustTime().getTime()));
					pst.setString(5, orgAdjustHistory.getIsLatest());
					pst.setString(6, orgAdjustHistory.getEtrend());
					pst.setString(7, orgAdjustHistory.getTimePeriod().getId());
					pst.setString(8, orgAdjustHistory.getAssessementStatus());
					pst.setString(9, null);
					pst.setString(10, orgAdjustHistory.getCompanyId().getId());
					pst.setDouble(11, orgAdjustHistory.getRiskStatus());
					pst.setString(12, orgAdjustHistory.getRiskAssessPlan().getId());
					pst.setString(13, orgAdjustHistory.getCalculateFormula());
					pst.addBatch();
				}
                pst.executeBatch();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 保存组织评估结果
	 * */
	@Transactional
	public void mergeOrgAdjustHistory(OrgAdjustHistory orgAdjustHistory) {
		o_orgAdjustHistoryDAO.merge(orgAdjustHistory);
	}
	
	/**
	 * 删除组织评估纪录表
	 * */
	@Transactional
	public void deleteOrgAdjustHistory(ArrayList<String> idsArrayList) {
		SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery("delete from t_rm_org_adjust_history where id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 删除组织评估结果
	 * */
	@Transactional
	public void deleteOrgAdjustHistory(String assessPlanId) {
		SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery("delete from t_rm_org_adjust_history where assess_plan_id = :assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 批量更新组织记录
	 * @param orgAdjustHistoryList 组织历史集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void updateOrgAdjustHistorySql(final ArrayList<Object> orgAdjustHistoryList) {
        this.o_orgAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " update t_rm_org_adjust_history set" +
                		" ORG_ID=?, ADJUST_TYPE=?, ADJUST_TIME=?, IS_LATEST=?, ETREND=?, TIME_PERIOD_ID=?, " +
                		" ASSESSEMENT_STATUS=?, LOCK_STATUS=?, RISK_STATUS=?, CALCULATE_FORMULA=? " +
                		" where ID=? ";
                pst = connection.prepareStatement(sql);
                
                for (Object object : orgAdjustHistoryList) {
                	OrgAdjustHistory orgAdjustHistory = (OrgAdjustHistory)object;
					pst.setString(1, orgAdjustHistory.getOrganization().getId());
					pst.setString(2, orgAdjustHistory.getAdjustType());
					pst.setTimestamp(3, new java.sql.Timestamp(orgAdjustHistory.getAdjustTime().getTime()));
					pst.setString(4, orgAdjustHistory.getIsLatest());
					pst.setString(5, orgAdjustHistory.getEtrend());
					pst.setString(6, orgAdjustHistory.getTimePeriod().getId());
					pst.setString(7, orgAdjustHistory.getAssessementStatus());
					pst.setString(8, null);
					pst.setDouble(9, orgAdjustHistory.getRiskStatus());
					pst.setString(10, orgAdjustHistory.getCalculateFormula());
					pst.setString(11, orgAdjustHistory.getId());
					pst.addBatch();
				}
                pst.executeBatch();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 更新组织评估纪录表所有is_latest(最新)为0
	 * */
	@Transactional
	public void updateOrgAdjustHistory(ArrayList<String> idsArrayList){
		SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery(" update t_rm_org_adjust_history set is_latest='0' where org_id in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 更新该计划下组织评估纪录表所有is_latest(最新)为1
	 * */
	@Transactional
	public void updateOrgAdjustHistoryByAssessPlanId(String assessPlanId){
		SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery(" update t_rm_org_adjust_history set is_latest='1' where assess_plan_id=:assessPlanId ");
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.executeUpdate();
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	public List<OrgAdjustHistory> findOrgAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<OrgAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部评估统计
	 * */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<OrgAdjustHistory> findTransactionalOrgAdjustHistoryByAssessPlanIdAllList(String assessPlanId){
		Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<OrgAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}

	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public List<OrgAdjustHistory> findOrgAdjustHistoryByAssessAllList(){
		Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
		List<OrgAdjustHistory> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询全部
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, OrgAdjustHistory> findOrgAdjustHistoryByAssessAllMap(){
		Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
		List<OrgAdjustHistory> list = null;
		list = criteria.list();
		HashMap<String, OrgAdjustHistory> map = new HashMap<String, OrgAdjustHistory>();
		for (OrgAdjustHistory orgAdjustHistory : list) {
			map.put(orgAdjustHistory.getId(), orgAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询该公司下所有组织对应的风险
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Object>> findOrgToRiskByCompanyId(String companyId, ArrayList<String> riskIds){
		StringBuffer sql = new StringBuffer();
		HashMap<String, ArrayList<Object>> maps = new HashMap<String, ArrayList<Object>>();
		sql.append(" select ORG_ID,RISK_ID from t_rm_risk_org where RISK_ID in(:riskIds) ");
        SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("riskIds", riskIds);
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String orgId = "";
            String riskId = "";
            
            ArrayList<Object> arrayList = new ArrayList<Object>();
            
            if(null != objects[0]){
            	orgId = objects[0].toString();
            }
            if(null != objects[1]){
            	riskId = objects[1].toString();
            }
            
            arrayList.add(riskId);
            
            if(maps.get(orgId) != null){
            	maps.get(orgId).add(riskId);
            }else{
            	maps.put(orgId, arrayList);
            }
        }
        
        return maps;
	}
	
	/**
	 * 查询该指标评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, OrgAdjustHistory> findGroupAdjustTime(String companyId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select this_.id, this_.org_id  y0_, max(this_.adjust_time) " +
				" y1_, this_.risk_status from t_rm_org_adjust_history this_ where " +
				"this_.COMPANY_ID = :companyId group by this_.org_id ");
        
        SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
		List<Object[]> list = sqlQuery.list();
		
		HashMap<String, OrgAdjustHistory> map = new HashMap<String, OrgAdjustHistory>();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            OrgAdjustHistory orgAdjustHistory = new OrgAdjustHistory();
            SysOrganization sysOrganization = new SysOrganization();
            TimePeriod timePeriod = new TimePeriod();
            orgAdjustHistory.setId(objects[0].toString());
            sysOrganization.setId(objects[1].toString());
            if(null != objects[2]){
				timePeriod.setId(objects[2].toString());
				orgAdjustHistory.setTimePeriod(timePeriod);
			}else{
				orgAdjustHistory.setTimePeriod(null);
			}
            
            if(null != objects[3]){
				orgAdjustHistory.setRiskStatus(Double.parseDouble(objects[3].toString()));
			}else{
				orgAdjustHistory.setRiskStatus(null);
			}
			
			
			orgAdjustHistory.setOrganization(sysOrganization);
			map.put(orgAdjustHistory.getOrganization().getId(), orgAdjustHistory);
		}
		
		return map;
	}
	
	/**
	 * 查询该指标评估记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<OrgAdjustHistory>> findOrgAdjustHistoryById(){
		Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("isLatest", "1"));
		criteria.addOrder(Order.desc("adjustTime"));
		HashMap<String, ArrayList<OrgAdjustHistory>> map = new HashMap<String, ArrayList<OrgAdjustHistory>>();
		
		List<OrgAdjustHistory> list = criteria.list();
		for (OrgAdjustHistory orgAdjustHistory : list) {
			try {
				ArrayList<OrgAdjustHistory> arrayList = new ArrayList<OrgAdjustHistory>();
				arrayList.add(orgAdjustHistory);
				
				if(map.get(orgAdjustHistory.getOrganization().getId()) != null){
					map.get(orgAdjustHistory.getOrganization().getId() + "--" + orgAdjustHistory.getIsLatest()).add(orgAdjustHistory);
				}else{
					map.put(orgAdjustHistory.getOrganization().getId() + "--" + orgAdjustHistory.getIsLatest(), arrayList);
				}
			} catch (Exception e) {
				log.error(orgAdjustHistory + "无对象");
			}
		}
		
		return map;
	}
	
	/**
	 * 查询评估记录该评估计划与次ID对应的数据
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, OrgAdjustHistory> findOrgAdjustHistoryByIdAndAssessPlanId(String assessPlanId){
		HashMap<String, OrgAdjustHistory> map = new HashMap<String, OrgAdjustHistory>();
		try {
			Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
			criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
			List<OrgAdjustHistory> list = criteria.list();
			for (OrgAdjustHistory orgAdjustHistory : list) {
				map.put(orgAdjustHistory.getOrganization().getId(), orgAdjustHistory);
			}
		} catch (Exception e) {
			
		}
		
		return map;
	}
	
	/**
	 * 根据评估计划id查询部门风险分类
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findParentOrgRisks(String assessPlanId,String query){
		List<Map<String, Object>> item = new ArrayList<Map<String,Object>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskMap = this.findChildrenOrgRisks(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.id,b.org_name,a.risk_status,a.assessement_status " +
				" from t_rm_org_adjust_history a LEFT JOIN t_sys_organization b on a.org_id = b.id " +
				" where  a.assess_plan_id=:assessPlanId ");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (b.org_name like :query or a.risk_status like :query) ");
		}
        
        SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        if(StringUtils.isNotBlank(query)){
        	sqlQuery.setParameter("query", "%"+query+"%");
        }
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String orgId = "";
            String orgName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            
            if(null != objects[0]){
            	orgId = objects[0].toString();
            }if(null != objects[1]){
            	orgName = objects[1].toString();
            }if(null != objects[2]){
            	riskStatus = NumberUtil.meg(Double.parseDouble(objects[2].toString()),2).toString();
            }if(null != objects[3]){
            	assessmentStatus = objects[3].toString();
            }
            
            Map<String, Object> orgMap = new HashMap<String, Object>();
            orgMap.put("orgId", orgId);
            orgMap.put("name", orgName);
            orgMap.put("score", riskStatus);
            orgMap.put("iconCls", assessmentStatus);
            if(null != childrenRiskMap.get(orgId)){
            	orgMap.put("children", childrenRiskMap.get(orgId));//部门下的风险
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
	 * 查询组织下风险
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenOrgRisks(String assessPlanId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		HashMap<String, List<StatisticsResult>> statisticsResultMap = 
				o_statisticsResultBO.findStatisticsResultListByAssessPlanIdAllMap(assessPlanId);
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.ORG_ID,c.id,c.risk_name,b.risk_status,b.assessement_status " +
				" from t_rm_risk_org a LEFT JOIN t_rm_risk_adjust_history b on a.risk_id=b.risk_id LEFT JOIN t_rm_risks c on c.id = a.risk_id " +
				" where b.assess_plan_id=:assessPlanId and c.IS_RISK_CLASS = 're' ");
        
        SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String orgId = "";
            String riskId = "";
            String riskName = "";
            String riskStatus = "";
            String assessmentStatus = "";
            String dimId = "";
            String score = "";
            
            if(null != objects[0]){
            	orgId = objects[0].toString();
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
            if(childrenMap.get(orgId) != null){
            	childrenMap.get(orgId).add(riskMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(riskMap);
            	childrenMap.put(orgId, arrayList);
            }
        }
		return childrenMap;
	}
	
	/**|
	 * 导出组织统计列表BO
	 * add by 王再冉
	 * 2013-12-27  下午5:11:39
	 * desc : 
	 * @param orgList
	 * @param indexList
	 * @param j
	 * @return 
	 * List<Object[]>
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> exportOrgCountGrid(List<Map<String, Object>> orgList, List<String> indexList,int j){
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		Object[] childObjects = null;
		for(Map<String, Object> riskMap : orgList){
			objects = new Object[j];
			List<Object[]> childObjList = new ArrayList<Object[]>();
			if(null != riskMap.get("children")){
				List<Map<String, Object>> childrenlist = (List<Map<String, Object>>)riskMap.get("children");
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
