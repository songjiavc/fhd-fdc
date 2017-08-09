package com.fhd.comm.business.bpm.jbpm;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.bpm.JbpmHistActinstDAO;
import com.fhd.entity.bpm.JbpmHistActinst;
@Service
public class JbpmHistActinstBO{
	@Autowired
	private JbpmHistActinstDAO o_jbpmHistActinstDAO;
	/**
	 * 
	 * findById:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public JbpmHistActinst findById(Long id) {
		return o_jbpmHistActinstDAO.get(id);
	}
	/**
	 * 
	 * findByTaskId:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public JbpmHistActinst findByTaskId(Long id) {
		Criteria criteria = o_jbpmHistActinstDAO.createCriteria();
		criteria.add(Restrictions.eq("jbpmHistTask.id", id));
		return (JbpmHistActinst)criteria.uniqueResult();
	}
	
	/**
	 * 
	 * merge:保存方法
	 * 
	 * @author 杨鹏
	 * @param jbpmHistActinst
	 * @since  fhd　Ver 1.1
	 */
	public void merge(JbpmHistActinst jbpmHistActinst) {
		o_jbpmHistActinstDAO.merge(jbpmHistActinst);
	}
}