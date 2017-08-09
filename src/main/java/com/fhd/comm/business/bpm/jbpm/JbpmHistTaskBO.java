package com.fhd.comm.business.bpm.jbpm;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.bpm.JbpmHistTaskDAO;
import com.fhd.entity.bpm.view.VJbpmHistTask;
@Service
@SuppressWarnings("unchecked")
public class JbpmHistTaskBO {

	@Autowired
	private JbpmHistTaskDAO o_jbpmHistTaskDAO;
	/**
	 * 
	 * findById:根据ID查询。没找到返回值为null。
	 * 
	 * @author 杨鹏
	 * @param taskId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public VJbpmHistTask findById(Long taskId){
		VJbpmHistTask vJbpmHistTask=null;
		Criteria createCriteria = o_jbpmHistTaskDAO.createCriteria();
		createCriteria.add(Restrictions.idEq(taskId));
		List<VJbpmHistTask> list = createCriteria.list();
		if(list.size()>0){
			vJbpmHistTask = list.get(0);
		}
		return vJbpmHistTask;
	}
	/**
	 * 
	 * findByExecutionId:
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public VJbpmHistTask findByExecutionId(String executionId){
		VJbpmHistTask vJbpmHistTask=null;
		Criteria createCriteria = o_jbpmHistTaskDAO.createCriteria();
		createCriteria.createAlias("jbpmExecution", "jbpmExecution");
		createCriteria.add(Restrictions.eq("jbpmExecution.id_", executionId));
		List<VJbpmHistTask> list = createCriteria.list();
		if(list.size()>0){
			vJbpmHistTask = list.get(0);
		}
		return vJbpmHistTask;
	}
	/**
	 * 
	 * findByDbversion:
	 * 
	 * @author 杨鹏
	 * @param dbversion:1：已执行 0：未执行
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<VJbpmHistTask> findByDbversion(Long dbversion){
		Criteria createCriteria = o_jbpmHistTaskDAO.createCriteria();
		createCriteria.add(Restrictions.eq("dbversion", dbversion));
		return createCriteria.list();
	}
	/**
	 * 
	 * findBySome:
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @param dbversion1：已执行 0：未执行
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<VJbpmHistTask> findBySome(Long[] onIds,Long dbversion){
		Criteria createCriteria = o_jbpmHistTaskDAO.createCriteria();
		createCriteria.add(Restrictions.not(Restrictions.in("id", onIds)));
		createCriteria.add(Restrictions.eq("dbversion", dbversion));
		return createCriteria.list();
	}
	/**
	 * 
	 * findCountBySome:
	 * 
	 * @author 杨鹏
	 * @param dbversion:1：已执行0：未执行
	 * @param empId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Long findCountBySome(Long dbversion,String empId){
		Criteria createCriteria = o_jbpmHistTaskDAO.createCriteria();
		createCriteria.add(Restrictions.eq("dbversion", dbversion));
		createCriteria.add(Restrictions.eq("assignee.id", empId));
		createCriteria.setProjection(Projections.rowCount());
		return (Long)createCriteria.uniqueResult();
	}
	/**
	 * 
	 * findDoneCountBySome:已执行的代办
	 * 
	 * @author 杨鹏
	 * @param empId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Long findDoneCountBySome(String empId){
		Criteria createCriteria = o_jbpmHistTaskDAO.createCriteria();
		createCriteria.add(Restrictions.eq("dbversion", 1L));
		createCriteria.add(Restrictions.eq("assignee.id", empId));
		createCriteria.setProjection(Projections.rowCount());
		return (Long)createCriteria.uniqueResult();
	}
	/**
	 * 
	 * findUnDoCountBySome:未执行的代办
	 * 
	 * @author 杨鹏
	 * @param empId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Long findUnDoCountBySome(String empId){
		Criteria createCriteria = o_jbpmHistTaskDAO.createCriteria();
		createCriteria.add(Restrictions.eq("dbversion", 0L));
		createCriteria.add(Restrictions.eq("assignee.id", empId));
		createCriteria.setProjection(Projections.rowCount());
		return (Long)createCriteria.uniqueResult();
	}
}