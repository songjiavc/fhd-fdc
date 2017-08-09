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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;

@Service
public class ScoreObjectBO {
	private static final Logger log = LoggerFactory.getLogger(ScoreObjectBO.class);
	@Autowired
	private RiskScoreObjectDAO o_riskScoreObjDAO;
	
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	
	
	@Transactional
	public void updateScoreObject(RiskScoreObject entity){
		try {
			this.o_riskScoreObjDAO.merge(entity);
			log.info("{} 更新成功! schm:={}",entity.getName(),entity.getSchm());
		} catch (Exception e) {
			log.info("{} 更新失败! schm:={}",entity.getName(),entity.getSchm());
		}
		
	}
	
	public RiskScoreObject get(String id){
		return this.o_riskScoreObjDAO.get(id);
	}
	
	
	
	/**
	 * 通过风险ID得到打分对象
	 * */
	public RiskScoreObject findRiskScoreObjectByRiskId(String riskId, String assessPlanId){
		Criteria criteria =  o_riskScoreObjDAO.createCriteria();
		criteria.add(Restrictions.eq("riskId", riskId));
		criteria.add(Restrictions.eq("assessPlan.id", assessPlanId));
		return (RiskScoreObject) criteria.list().get(0);
	}
	
	/**
	 * 通过计划和风险查询打分对象是否存在
	 * @param planId	计划id
	 * @param riskId	风险id
	 * @return
	 */
	public RiskScoreObject findRiskScoreObjsByPlanAndRisk(String planId, String riskId) {
		List<RiskScoreObject> scoreObjList = this.findRiskScoreObjByplanId(planId);
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for(RiskScoreObject obj : scoreObjList){
			Map<String, Object> exitObjMap = new HashMap<String, Object>();
			exitObjMap.put("scoreObj", obj);
			exitObjMap.put("planId", obj.getAssessPlan().getId());
			if(null != obj.getRisk()){
				exitObjMap.put("riskId", obj.getRisk().getId());
			}else{
				exitObjMap.put("riskId", "");
			}
			listMap.add(exitObjMap);
		}
		for(Map<String, Object> onemap : listMap){
			if(planId.equals(onemap.get("planId"))&&riskId.equals(onemap.get("riskId"))){
				return (RiskScoreObject)onemap.get("scoreObj");
			}
		}
		return null;
	}
	
	/**
	 * 根据计划id和风险id集合查打分对象集合map
	 * @param planId	计划id
	 * @param riskIds	风险id集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findRiskScoreObjsByPlanIdAndRiskIds(String planId, List<String> riskIds) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Criteria c = o_riskScoreObjDAO.createCriteria();
		List<RiskScoreObject> list = new ArrayList<RiskScoreObject>();
		if (StringUtils.isNotBlank(planId)&&riskIds.size()>0) {
			c.add(Restrictions.and(Restrictions.eq("assessPlan.id", planId), Restrictions.in("risk.id", riskIds)));
			list = c.list();
		}
		
		for(RiskScoreObject obj : list){
			Map<String, Object> exitObjMap = new HashMap<String, Object>();
			exitObjMap.put("scoreObj", obj);
			exitObjMap.put("planId", obj.getAssessPlan().getId());
			exitObjMap.put("riskId", obj.getRisk().getId());
			listMap.add(exitObjMap);
		}
		return listMap;
	}
	/**
	 * 根据id查询打分对象
	 * @param objId	打分对象id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RiskScoreObject findRiskScoreObjById(String objId) {
		Criteria c = o_riskScoreObjDAO.createCriteria();
		List<RiskScoreObject> list = null;
		if (StringUtils.isNotBlank(objId)) {
			c.add(Restrictions.eq("id", objId));
		}
		list = c.list();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 通过评估计划id查询打分对象
	 * @param planId 计划id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreObject> findRiskScoreObjByplanId(String planId) {
		Criteria c = o_riskScoreObjDAO.createCriteria();
		List<RiskScoreObject> list = new ArrayList<RiskScoreObject>();
		if (StringUtils.isNotBlank(planId)) {
			c.add(Restrictions.eq("assessPlan.id", planId));
			list = c.list();
		}

		return list;
	}
	
	/**
	 * 根据计划id集合查打分对象集合
	 * @param planIds	计划id集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreObject> findRiskScoreObjsByplanIdList(List<String> planIds) {
		Criteria c = o_riskScoreObjDAO.createCriteria();
		List<RiskScoreObject> list = null;
		c.add(Restrictions.in("assessPlan.id", planIds));
		list = c.list();
		return list;
	}
	
	
	/**
	 * 查询此评估计划下风险评估中所有新添加的风险事件
	 * */
	@SuppressWarnings({ "unused", "unchecked" })
	public ArrayList<String> findScoreObjectNewsRiskId(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = new ArrayList<String>();
        sql.append(" select o.risk_id,b.delete_estatus from t_rm_risk_score_object o ");
        sql.append(" LEFT JOIN t_rm_risks b on o.risk_id = b.id ");
        sql.append(" where  b.delete_estatus = '2' and assess_plan_id=:assessPlanId ");
        
        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String riskId = "";
            String deleteEstatus = "";
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	deleteEstatus = objects[1].toString();
            }
            
            arrayList.add(riskId);
           
        }
        
        return arrayList;
	}
	
	/**
	 * 查询一级流程
	 * */
	@SuppressWarnings("unchecked")
	public ArrayList<String> findProcessOneId(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = new ArrayList<String>();
		sql.append(" select d.id,d.processure_name,d.id_seq from t_rm_risk_score_object a  ");
		sql.append(" LEFT JOIN t_processure_risk_processure b on b.risk_id = a.risk_id ");
		sql.append(" LEFT JOIN t_rm_risks c on c.id=a.risk_id ");
		sql.append(" LEFT JOIN t_ic_processure d on d.id = b.processure_id ");
		sql.append(" where b.etype='i' and a.assess_plan_id=:assessPlanId GROUP BY d.id ");
        
        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String id = "";
            
            if(null != objects[2]){
            	id = objects[2].toString().replace(".", ",").split(",")[1];
            }
            
            arrayList.add(id);
           
        }
        
        return arrayList;
	}
	
	/**
	 * 该流程下的风险事件
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findNextRiskRe(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = null;
		sql.append(" select processure_id,risk_id from t_processure_risk_processure where etype = 'i' ");
        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String processureId = "";
            String riskId = "";
            
            if(null != objects[0]){
            	processureId = objects[0].toString();
            }if(null != objects[1]){
            	riskId = objects[1].toString();
            }
            
            if(map.get(processureId) != null){
            	map.get(processureId).add(riskId);
            }else{
            	arrayList = new ArrayList<String>();
            	arrayList.add(riskId);
            	map.put(processureId, arrayList);
            }
        }
        
        return map;
	}
	
	/**
	 * 通过打分对象id集合查打分对象实体集合
	 * @param objIds 打分对象id集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreObject> findRiskScoreObjsByobjIdList(List<String> objIds) {
		Criteria c = o_riskScoreObjDAO.createCriteria();
		List<RiskScoreObject> list = null;
		c.add(Restrictions.in("id", objIds));
		list = c.list();
		return list;
	}
	
	/**
	 * 根据部门id查询打分对象的id集合
	 * add by 王再冉
	 * @param deptId		部门id
	 * @param bussinessId	计划id
	 * @return 
	 * List<RiskScoreObject>
	 */
	public List<RiskScoreObject> findScoreObjectsBydeptId(String deptId, String bussinessId){
		List<RiskScoreObject> objList = new ArrayList<RiskScoreObject>();
		List<String> objIds = this.findScoreObjectIdsBydeptIdAndPlanId(deptId, bussinessId);
		if(objIds.size()>0){
			objList = this.findRiskScoreObjsByidList(objIds);
		}
		return objList;
	}
	
	/**
	 * 通过部门id和评估计划id查询打分对象id集合
	 * @param deptId		部门id
	 * @param bussinessId	计划id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> findScoreObjectIdsBydeptIdAndPlanId(String deptId, String bussinessId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT b.id " +
				" FROM t_rm_risk_org_temp a LEFT JOIN t_rm_risk_score_object b ON a.object_id=b.id " +
				" WHERE a.org_id = :deptId " + " AND b.assess_plan_id = :bussinessId ");
        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("deptId", deptId);
        sqlQuery.setParameter("bussinessId", bussinessId);
        List<String> list = sqlQuery.list();
		
		return list;
	}
	
	/**
	 * 通过打分对象id集合查询打分对象集合
	 * @param objIds	打分对象id集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreObject> findRiskScoreObjsByidList(List<String> objIds) {
		Criteria c = o_riskScoreObjDAO.createCriteria();
		List<RiskScoreObject> list = null;
		c.add(Restrictions.in("id", objIds));
		list = c.list();
		return list;
	}
	
	/**
	 * 根据计划id查询风险字符串
	 * @param assessPlanId 计划id
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public ArrayList<String> findRisksNameAndTemplateIdByPlanId(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = new ArrayList<String>();
        sql.append(" SELECT r.id,r.risk_name,r.template_id FROM t_rm_risks r WHERE ID IN (");
        sql.append(" SELECT RISK_ID FROM t_rm_risk_score_object ");
        sql.append(" WHERE assess_plan_id =:assessPlanId )");
        
        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String riskId = "";
            String riskName = "";
            String templateId = "";
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	riskName = objects[1].toString();
            }if(null != objects[2]){
            	templateId = objects[2].toString();
            }
            arrayList.add(riskId+"--"+riskName+"--"+templateId);
        }
        return arrayList;
	}
	
	/**
	 * 查询所有风险关联部门表数据并已MAP方式存储
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findRiskOrgAllMap(String assessPlanId){
		StringBuffer sql = new StringBuffer();
        
		ArrayList<RiskOrg> riskOrgList = new ArrayList<RiskOrg>();
        RiskOrg riskOrg = null;
        sql.append(" select a.risk_id, b.org_id, b.org_type from t_rm_risk_score_object a, t_rm_risk_score_dept b " +
        		"where ASSESS_PLAN_ID=:assessPlanId and b.SCORE_OBJECT_ID=a.id ");
        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String riskId = ""; //风险ID
            String orgIds = ""; //部门ID
            String type = ""; //部门类型 责任/相关/参与
            
            if(null != objects[0]){
            	riskId = objects[0].toString();
            }if(null != objects[1]){
            	orgIds = objects[1].toString();
            }if(null != objects[2]){
            	type = objects[2].toString();
            }
            
            riskOrg = new RiskOrg();
            Risk risk = new Risk();
            risk.setId(riskId);
            riskOrg.setRisk(risk);
            riskOrg.setType(type);
            SysOrganization org = new SysOrganization();
            org.setId(orgIds);
            riskOrg.setSysOrganization(org);
            
            riskOrgList.add(riskOrg);
        }
		
		HashMap<String, String> map = new HashMap<String, String>();
		for (RiskOrg riskOrgs : riskOrgList) {
			map.put(riskOrgs.getRisk().getId() + "--" + riskOrgs.getSysOrganization().getId(), riskOrgs.getType());
		}
		
		return map;
	}
	
	/**
	 * 查询计划下该部门下所有打分人员id集合
	 * add by 王再冉
	 * 2014-3-3  上午10:22:55
	 * desc : 
	 * @param assessPlanId	计划id
	 * @param deptId		部门id
	 * @return 
	 * ArrayList<String>
	 */
	public ArrayList<String> findRangObjectDeptEmpEmpIdListByPlanIdAndDeptId(String assessPlanId,String deptId) {
		StringBuffer sql = new StringBuffer();
		List<SysEmployee> empList = o_riskScoreBO.findAllEmpsBydeptId(deptId);
		for (SysEmployee sysEmployee : empList) {
			System.out.println(sysEmployee.getEmpname());
		}
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> arrayList = new ArrayList<String>();
		sql.append(" SELECT b.score_emp_id,c.org_id FROM t_rm_risk_score_object a ");
		sql.append(" LEFT JOIN  t_rm_rang_object_dept_emp b ON b.score_object_id = a.id ");
		sql.append(" LEFT JOIN t_sys_emp_org c on c.emp_id = b.score_emp_id ");
		sql.append(" WHERE assess_plan_id = :assessPlanId ");
        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String empId = "";//范围-对象-部门-人员综合表人员ID
            String orgId = "";

            if(null != objects[0]){
            	empId = objects[0].toString();
            }if(null != objects[1]){
            	orgId = objects[1].toString();
            }
            if(!empId.equalsIgnoreCase("")){
            	map.put(empId, orgId);
            }
//            if(orgId.equals(deptId) && !(arrayList.contains(empId))){
//            	arrayList.add(empId);
//            }
        }
        for (SysEmployee sysEmployee : empList) {
        	if(null != sysEmployee){
				if(null != map.get(sysEmployee.getId())){
					arrayList.add(sysEmployee.getId());
				}
        	}
		}
        
        
        return arrayList;
	}
	
	
//	public ArrayList<String> findRangObjectDeptEmpEmpIdListByPlanIdAndDeptId(String assessPlanId,String deptId) {
//		List<String> orgIdList = new ArrayList<String>();
//		orgIdList.add(deptId);
//		List<SysOrganization> childOrgList = o_orgGridBO.findChildOrgByparentId(deptId);
//		for(SysOrganization o : childOrgList){
//			orgIdList.add(o.getId());
//		}
//		StringBuffer sql = new StringBuffer();
//		ArrayList<String> arrayList = new ArrayList<String>();
//		sql.append(" SELECT b.score_emp_id,c.org_id FROM t_rm_risk_score_object a ");
//		sql.append(" LEFT JOIN  t_rm_rang_object_dept_emp b ON b.score_object_id = a.id ");
//		sql.append(" LEFT JOIN t_sys_emp_org c on c.emp_id = b.score_emp_id ");
//		sql.append(" WHERE assess_plan_id = :assessPlanId ");
//        SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
//        sqlQuery.setParameter("assessPlanId", assessPlanId);
//        @SuppressWarnings("unchecked")
//		List<Object[]> list = sqlQuery.list();
//        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
//            Object[] objects = (Object[]) iterator.next();
//           
//            String empId = "";//范围-对象-部门-人员综合表人员ID
//            String orgId = "";
//
//            if(null != objects[0]){
//            	empId = objects[0].toString();
//            }if(null != objects[1]){
//            	orgId = objects[1].toString();
//            }
//            if(orgIdList.contains(orgId) && !(arrayList.contains(empId))){
//            	arrayList.add(empId);
//            }
//        }
//        return arrayList;
//	}
	
	
	/** 
	  * @Description:  通过list 更新本次评估计划中所涉及到的风险综合得分
	  * @author jia.song@pcitc.com
	  * @date 2017年4月24日 下午1:46:15 
	  * @param paramMap(风险 和分数的映射list),assessPlanId(评估计划id)
	  */
	
	@Transactional
	public void updateRiskScoreByRiskId(final List<Map<String,Double>> paramList) {
	    this.o_riskScoreObjDAO.getSession().doWork(new Work() {
		public void execute(Connection connection) throws SQLException {
		    connection.setAutoCommit(false);
		    PreparedStatement pst = null;
		    String sql = "UPDATE t_rm_risk_score_object SET RISK_SCORE=? WHERE id = ? ";
		    pst = connection.prepareStatement(sql);
		    if(paramList.size() > 0 && paramList != null){
				for(Map<String,Double> map : paramList){
				    String key = map.keySet().iterator().next();
				    pst.setString(2, key);
				    pst.setDouble(1, map.get(key));
				    pst.addBatch();
				}
		    }
		    pst.executeBatch();
		    connection.commit();
		    connection.setAutoCommit(true);
		}
	    });
	}
}