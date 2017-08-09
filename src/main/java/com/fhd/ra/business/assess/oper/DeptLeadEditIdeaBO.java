package com.fhd.ra.business.assess.oper;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.quaAssess.DeptLeadEditIdeaDAO;
import com.fhd.dao.assess.quaAssess.DeptLeadResponseIdeaDAO;
import com.fhd.entity.assess.quaAssess.DeptLeadCircusee;
import com.fhd.entity.assess.quaAssess.DeptLeadEditIdea;
import com.fhd.entity.assess.quaAssess.DeptLeadResponseIdea;

@Service
public class DeptLeadEditIdeaBO {
	
	@Autowired
	private DeptLeadEditIdeaDAO o_deptLeadEditIdeaDAO;
	
	@Autowired
	private DeptLeadResponseIdeaDAO o_deptLeadResponseIdeaDAO;
	
	/**
	 * 保存风险评估结果
	 * @param depteLeadEditIdea 领导意见实体
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void mergeDepteLeadEditIdea(DeptLeadEditIdea depteLeadEditIdea) {
		o_deptLeadEditIdeaDAO.merge(depteLeadEditIdea);
	}
	
	public DeptLeadEditIdea findEntityById(String id){
		return o_deptLeadEditIdeaDAO.get(id);
	}
	
	public DeptLeadResponseIdea findEntityByHresponseId(String id){
		return o_deptLeadResponseIdeaDAO.get(id);
	}
	
	/**
	 * 保存风险应对结果
	 * @param depteLeadEditIdea 领导意见实体
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void mergeDepteLeadResponseIdea(DeptLeadResponseIdea depteLeadEditIdea) {
		o_deptLeadResponseIdeaDAO.merge(depteLeadEditIdea);
	}
	
	/**
	 * 通过领导人、打分对象ID、评估计划ID查询意见内容
	 * @param deptLeadEmpId 领导人员ID
	 * @param scoreOBjectId 打分对象ID
	 * @param assessPlanId 评估计划ID
	 * @return DeptLeadEditIdea
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public DeptLeadEditIdea findDeptLeadIdea(String deptLeadEmpId, String scoreOBjectId, String assessPlanId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id, b.edit_idea_content , br.edit_idea_content responseIdeaContent from t_rm_dept_lead_circusee a " +
				" LEFT JOIN t_rm_dept_lead_edit_idea b on a.id=b.dept_lead_circusee_id" + 
				" LEFT JOIN t_rm_dept_lead_edit_response br on a.id=br.dept_lead_circusee_id"+
				" where a.dept_lead_emp_id=:deptLeadEmpId " +
						" and a.score_object_id=:scoreOBjectId and assess_plan_id=:assessPlanId ");
        
        SQLQuery sqlQuery = o_deptLeadEditIdeaDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("deptLeadEmpId", deptLeadEmpId);
        sqlQuery.setParameter("scoreOBjectId", scoreOBjectId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
            
            String deptLeadCircuseeId = ""; //领导意见ID
            String editIdeaContent = ""; //领导意见内容
            String responseIdeaContent = ""; //领导意见内容
            
            if(null != objects[0]){
            	deptLeadCircuseeId = objects[0].toString();
            }if(null != objects[1]){
            	editIdeaContent = objects[1].toString();
            }if(null != objects[2]){
            	responseIdeaContent = objects[2].toString();
            }
            
            DeptLeadEditIdea deptLeadEditIdea = new DeptLeadEditIdea();
            DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
            deptLeadCircusee.setId(deptLeadCircuseeId);
            deptLeadEditIdea.setDeptLeadCircusee(deptLeadCircusee);
            deptLeadEditIdea.setEditIdeaContent(editIdeaContent);
            deptLeadEditIdea.setResponseIdeaContent(responseIdeaContent);
            return deptLeadEditIdea;
		}
		
		return null;
	}
	
	/**
	 * 通过deptLeadCircuseeId查询
	 * @param deptLeadCircuseeId 领导意见ID
	 * @return List<DeptLeadEditIdea>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public List<DeptLeadEditIdea> findDeptLeadEditIdeaByDeptLeadCircuseeIdList(String deptLeadCircuseeId){
		Criteria criteria = o_deptLeadEditIdeaDAO.createCriteria();
		criteria.add(Restrictions.eq("deptLeadCircusee.id", deptLeadCircuseeId));
		List<DeptLeadEditIdea> list = null;
		list = criteria.list();
		
		return list;
	}
	/**
	 * 通过deptLeadCircuseeId查询
	 * @param deptLeadCircuseeId 领导意见ID
	 * @return List<DeptLeadEditIdea>
	 * @author 郭鹏
	 * */
	@SuppressWarnings("unchecked")
	public List<DeptLeadResponseIdea> findDeptLeadResponseIdeaByDeptLeadCircuseeIdList(String deptLeadCircuseeId){
		Criteria criteria = o_deptLeadResponseIdeaDAO.createCriteria();
		criteria.add(Restrictions.eq("deptLeadCircusee.id", deptLeadCircuseeId));
		List<DeptLeadResponseIdea> list = null;
		list = criteria.list();
		
		return list;
	}
}
