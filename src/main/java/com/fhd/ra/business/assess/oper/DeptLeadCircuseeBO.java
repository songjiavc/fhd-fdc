package com.fhd.ra.business.assess.oper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.quaAssess.DeptLeadCircuseeDAO;
import com.fhd.entity.assess.quaAssess.DeptLeadCircusee;
import com.fhd.fdc.commons.interceptor.RecordLog;

@Service
public class DeptLeadCircuseeBO {

	@Autowired
	private DeptLeadCircuseeDAO o_deptLeadCircuseeDAO;
	
	/**
	 * 保存风险评估结果
	 * */
	@Transactional
	public void mergeDeptLeadCircusee(DeptLeadCircusee deptLeadCircusee) {
		o_deptLeadCircuseeDAO.merge(deptLeadCircusee);
	}
	
	/**
	 * 删除
	 * */
	@Transactional
	public void removeDeptLeadCircusee(DeptLeadCircusee deptLeadCircusee) {
		o_deptLeadCircuseeDAO.delete(deptLeadCircusee);
	}
	
	/**
	 * 查询领导审批记录
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, DeptLeadCircusee> findDeptLeadCircuseeByAssessPlanId(String assessPlanId){
		HashMap<String, DeptLeadCircusee> map = new HashMap<String, DeptLeadCircusee>();
		Criteria criteria = o_deptLeadCircuseeDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		List<DeptLeadCircusee> list = criteria.list();
		for (DeptLeadCircusee deptLeadCircusee : list) {
			if(null != deptLeadCircusee.getScoreObjectId()){
				map.put(deptLeadCircusee.getScoreObjectId().getId(), deptLeadCircusee);
			}
		}
		
		return map;
	}
	
	/**
	 * 通过评估计划ID，打分对象查询领导审批记录
	 * */
	@SuppressWarnings("unchecked")
	public List<DeptLeadCircusee> findDeptLeadCircuseeByAssessPlanIdAndScoreObjectId(String assessPlanId, String scoreObjectId){
		Criteria criteria = o_deptLeadCircuseeDAO.createCriteria();
		criteria.add(Restrictions.eq("riskAssessPlan.id", assessPlanId));
		criteria.add(Restrictions.eq("scoreObjectId.id", scoreObjectId));
		List<DeptLeadCircusee> list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询各部门领导针对风险反馈意见
	 * */
	@RecordLog("查询评估人评价风险信息")
	public List<Map<String, Object>> findRiskByDeptRiskIdea(String scoreObjectId){
		List<Map<String, Object>> arrayListMap = new ArrayList<Map<String,Object>>();
		StringBuffer sql = new StringBuffer();
		// 0516修改
		sql.append(" SELECT a.id,b.edit_idea_content as hIdea,res.edit_idea_content as hResponse,e.emp_name,org.org_name FROM t_rm_dept_lead_circusee a ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_idea b on b.dept_lead_circusee_id=a.id ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_response res ON res.dept_lead_circusee_id = a.id ");
		sql.append(" LEFT JOIN t_rm_risk_score_object c on c.id = a.SCORE_OBJECT_ID  ");
		sql.append(" LEFT JOIN t_sys_employee e on e.id = a.DEPT_LEAD_EMP_ID ");
		sql.append(" LEFT JOIN t_sys_emp_org empOrg ON empOrg.emp_id = e.id ");
		sql.append(" LEFT JOIN t_sys_organization org ON org.id = empOrg.org_id ");
		sql.append(" where a.score_object_id=:scoreObjectId ");
		SQLQuery sqlQuery = o_deptLeadCircuseeDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("scoreObjectId", scoreObjectId);
		List<Object[]> list = sqlQuery.list();
		Map<String, Object> map = null;
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			map = new HashMap<String, Object>();
            Object[] objects = (Object[]) iterator.next();
            String circuseeId = null;
            String hIdea = null;
            String hResponse = null;
            String empName = null;
            String orgName = null;
            if(null != objects[0]){
            	circuseeId = objects[0].toString();
            }if(null != objects[1]){
            	hIdea = objects[1].toString();
            }if(null != objects[2]){
            	hResponse = objects[2].toString();
            }if(null != objects[3]){
            	empName = objects[3].toString();
            }if(null != objects[4]){
            	orgName = objects[4].toString();
            }
            map.put("circuseeId", circuseeId);
            map.put("hIdea", hIdea);
            map.put("hResponse", hResponse);
            map.put("empName", empName);
            map.put("orgName", orgName);
            arrayListMap.add(map);
        }
        
        return arrayListMap;
	}
	/**
	 * 批量保存风险评估结果
	 * add by 王再冉
	 * 2014-3-3  上午11:00:23
	 * desc : 
	 * @param objList 
	 * void
	 */
	@Transactional
    public void saveDeptLeadCircuseeList(final List<DeptLeadCircusee> objList) {
        this.o_deptLeadCircuseeDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_dept_lead_circusee (id,DEPT_LEAD_EMP_ID,ASSESS_PLAN_ID,SCORE_OBJECT_ID) values(?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (DeptLeadCircusee obj : objList) {
                	pst.setString(1, obj.getId());
                	pst.setString(2, obj.getDeptLeadEmpId().getId());
                    pst.setString(3, obj.getRiskAssessPlan().getId());
                    pst.setString(4, obj.getScoreObjectId().getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
}
