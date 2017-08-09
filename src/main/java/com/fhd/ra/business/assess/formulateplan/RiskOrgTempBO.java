package com.fhd.ra.business.assess.formulateplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.formulatePlan.RiskOrgRelationTempDAO;
import com.fhd.dao.assess.formulatePlan.RiskOrgTempDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.entity.assess.formulatePlan.RiskOrgRelationTemp;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 主要为打版本的更新风险库service
 * @author Jzq
 *
 */
@Service
public class RiskOrgTempBO {
	
	private static final Logger log = LoggerFactory.getLogger(RiskOrgTempBO.class);
	
	@Autowired
	private RiskOrgTempDAO riskOrgTempDAO;
	@Autowired
	private RiskOrgRelationTempDAO riskOrgRelationTempDAO;
	@Autowired
	private RiskDAO riskDAO;
	@Autowired
	private RiskOrgDAO riskOrgDAO;
	
	/**辨识计划完成更新状态为finished
	 * @author Jzq
	 * finished:该风险计划完成，可以作为备选  running：计划进行中，该风险不可以作为备选
	 * @param planId
	 */
	@Transactional
	public void updateRiskOrgTempStatusByPlanId(final String planId){
		 this.riskOrgTempDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	            	connection.setAutoCommit(false);
	                PreparedStatement pst = null;
	                StringBuffer sql = new StringBuffer();
	                sql.append("update t_rm_risk_org_temp set `status` = 'finished' where plan_id=?");
	                pst = connection.prepareStatement(sql.toString());
	                pst.setString(1, planId);
	                pst.execute();
	                connection.commit();
	                connection.setAutoCommit(true);
	            }
	        });
	}
	
	/**
	 * 清空风险库 打版本使用 t_rm_risks
	 */
	public void deleteRisk(final String schm,final String planId){
		 //删除风险实体，删除风险实体关联部门实体 
		 StringBuffer sql = new StringBuffer();
         /*宋佳修改联合查询语句，保证左关联的也可以删除*/
		 sql.append("delete a,b from t_rm_risks a left join  t_rm_risk_org b  on a.id=b.risk_id where a.schm =:schm and a.id in (select risk_id from t_rm_risk_score_object c where c.assess_plan_id =:planId)");
         Session session = this.riskDAO.getSessionFactory().getCurrentSession();
         SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
         sqlQuery.setParameter("schm", schm);
         sqlQuery.setParameter("planId", planId);
         Transaction tx = session.beginTransaction();
         sqlQuery.executeUpdate();
         tx.commit();
	}
	
	/**根据计划id将打分对象表备份到风险库
	 * @param planId
	 */
	@Transactional
	public void saveRiskBatch(final String planId){
		this.riskDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_risks( ID, COMPANY_ID, TEMPLATE_ID, RISK_CODE, RISK_NAME, EDESC, PARENT_ID, PARENT_NAME, ELEVEL, ID_SEQ, DELETE_ESTATUS, ARCHIVE_STATUS, ESORT, CREATE_TIME, CREATE_BY, CREATE_ORG_ID, LAST_MODIFY_TIME, LAST_MODIFY_BY, ASSESSMENT_MEASURE, RESPONSE_STRATEGY, IS_RISK_CLASS, IS_LEAF, CHART_TYPE, RELATIVE_TO, IS_INHERIT, FORMULA_DEFINE, ALARM_SCENARIO, GATHER_FREQUENCE, GATHER_DAY_FORMULR_SHOW, GATHER_DAY_FORMULR, RESULT_COLLECT_INTERVAL, ALARM_MEASURE, IMPACT_TIME, MONITOR_FREQUENCE, MONITOR_DAY_FORMULR_SHOW, MONITOR_DAY_FORMULR, MONITOR_COLLECT_INTERVAL, EXPAND_DAYS, IS_FIX, IS_USE, IS_ANSWER, IS_CALC, calc_digit, SCHM ) SELECT RISK_ID, COMPANY_ID, TEMPLATE_ID, RISK_CODE, RISK_NAME, EDESC, PARENT_ID, PARENT_NAME, ELEVEL, ID_SEQ, DELETE_ESTATUS, 'archived', ESORT, CREATE_TIME, CREATE_BY, CREATE_ORG_ID, LAST_MODIFY_TIME, LAST_MODIFY_BY, ASSESSMENT_MEASURE, RESPONSE_STRATEGY, IS_RISK_CLASS, IS_LEAF, CHART_TYPE, RELATIVE_TO, IS_INHERIT, FORMULA_DEFINE, ALARM_SCENARIO, GATHER_FREQUENCE, GATHER_DAY_FORMULR_SHOW, GATHER_DAY_FORMULR, RESULT_COLLECT_INTERVAL, ALARM_MEASURE, IMPACT_TIME, MONITOR_FREQUENCE, MONITOR_DAY_FORMULR_SHOW, MONITOR_DAY_FORMULR, MONITOR_COLLECT_INTERVAL, EXPAND_DAYS, IS_FIX, IS_USE, IS_ANSWER, IS_CALC, calc_digit, SCHM FROM t_rm_risk_score_object obj WHERE obj.ASSESS_PLAN_ID = ? and obj.DELETE_ESTATUS in ('1','2') ";
                pst = connection.prepareStatement(sql);
                pst.setString(1, planId);
                pst.execute();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
	}
	

	@Transactional
    public void saveRiskOrgBatchByRiskOrgRelationList(final List<RiskOrgRelationTemp> list) {
        this.riskOrgDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_risk_org (id,risk_id,org_id,etype,emp_id) values(?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                for (RiskOrgRelationTemp riskOrgRelationTemp : list) {
                	pst.setString(1, riskOrgRelationTemp.getId());
                	pst.setString(2, riskOrgRelationTemp.getRisk().getId());
                	pst.setString(3, riskOrgRelationTemp.getSysOrganization().getId());
                	pst.setString(4, riskOrgRelationTemp.getType());
                	if(riskOrgRelationTemp.getEmp() == null){
                		pst.setString(5, null);
                	}else{
                		pst.setString(5, riskOrgRelationTemp.getEmp().getId());
                	}
                    pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	
	@SuppressWarnings("unchecked")
	public List<RiskOrgTemp> getRiskOrgTempListByPlanId(String planId){
		Criteria criteria = this.riskOrgTempDAO.createCriteria();
		criteria.add(Restrictions.eq("planId", planId));
		return criteria.list();
	}
	
	/**
	 * 通过计划id获取风险与部门之间的实际关联关系
	 * @param planId
	 * @return
	 */
	public List<RiskOrgRelationTemp> getRiskOrgRelationTempListByPlanId(final String planId){
		List<RiskOrgRelationTemp> retList = new ArrayList<RiskOrgRelationTemp>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from t_rm_risk_org_relation_temp where OBJECT_ID in (select ID from t_rm_risk_score_object a where a.ASSESS_PLAN_ID =:planId)");
		 SQLQuery sqlQuery = riskOrgRelationTempDAO.createSQLQuery(sql.toString());
		 sqlQuery.setParameter("planId", planId);
			List<Object[]> list = sqlQuery.list();
	        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
	            Object[] objects = (Object[]) iterator.next();
	            String id = "";
	            String objectId = "";
	            String eType = "";
	            String orgId = "";
	            String riskId = "";
	            if(null != objects[0]){
	            	id = objects[0].toString();
	            }if(null != objects[1]){
	            	objectId = objects[1].toString();
	            }if(null != objects[2]){
	            	eType = objects[2].toString();
	            }if(null != objects[3]){
	            	orgId = objects[3].toString();
	            }if(null != objects[4]){
	            	riskId = objects[4].toString();
	            }
	            RiskOrgRelationTemp obj = new RiskOrgRelationTemp();
	            obj.setId(id);
	            obj.setRiskScoreObject(new RiskScoreObject(objectId));
	            obj.setType(eType);
	            obj.setSysOrganization(new SysOrganization(orgId));
	            obj.setRisk(new Risk(riskId));
	            retList.add(obj);
	        }
	        return retList;
	}
	
	
	/**
	 * 保存风险与部门之间的关系
	 * @param list
	 */
	@Transactional
    public void saveRiskOrgRelationBatch(final List<RiskOrgRelationTemp> list) {
        this.riskOrgRelationTempDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_risk_org_relation_temp (id,risk_id,org_id,etype,object_id,emp_id) values(?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (RiskOrgRelationTemp riskOrgRelationTemp : list) {
                	pst.setString(1, riskOrgRelationTemp.getId());
                	pst.setString(2, riskOrgRelationTemp.getRiskScoreObject().getRiskId());
                	pst.setString(3, riskOrgRelationTemp.getSysOrganization().getId());
                	pst.setString(4, riskOrgRelationTemp.getType());
                	pst.setString(5, riskOrgRelationTemp.getRiskScoreObject().getId());
                	if(riskOrgRelationTemp.getEmp() != null){
                		pst.setString(6, riskOrgRelationTemp.getEmp().getId());
                	}else{
                		pst.setString(6, null);
                	}
                	
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	@Transactional
	public void removeRiskOrgRelationTempByDeptIds(String deptids){
		if(deptids != null && !"".equals(deptids)){
			this.riskOrgRelationTempDAO.createQuery("delete RiskOrgRelationTemp where id in (:ids)")
			.setParameterList("ids", StringUtils.split(deptids,",")).executeUpdate();
		}
	}
	@Transactional
    public void deleteRiskOrgRelationTempByObjectId(final String objectId) {
        this.riskOrgRelationTempDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_risk_org_relation_temp where object_id=?";
                pst = connection.prepareStatement(sql);
            	pst.setString(1, objectId);
                pst.execute();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**根据打分对象id获取该风险的部门列表
	 * @param objectId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskOrgRelationTemp> getRiskOrgRelationTempListByObjectId(String objectId){
		Criteria criteria = this.riskOrgRelationTempDAO.createCriteria();
		criteria.add(Restrictions.eq("riskScoreObject.id", objectId));
		return criteria.list();
	}
}
