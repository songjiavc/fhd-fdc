package com.fhd.comm.business.bpm.jbpm;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.jbpm.pvm.internal.history.model.HistoryActivityInstanceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.bpm.HistoryActivityInstanceDAO;

@Service
@SuppressWarnings({"unchecked"})
public class HistoryActivityInstanceBO {
	@Autowired
	private HistoryActivityInstanceDAO o_historyActivityInstanceDAO;
	
	public HistoryActivityInstanceImpl findById(Long dbid){
		return o_historyActivityInstanceDAO.get(dbid);
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
	public List<HistoryActivityInstanceImpl> findByExecutionId(String executionId){
		Criteria criteria = o_historyActivityInstanceDAO.createCriteria();
		criteria.add(Restrictions.eq("executionId", executionId));
		return criteria.list();
	}
	/**
	 * 
	 * findUntreadToActinstsById:
	 * 
	 * @author 杨鹏
	 * @param dbid
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<HistoryActivityInstanceImpl> findUntreadToActinstsById(Long dbid){
		DetachedCriteria subselect=DetachedCriteria.forClass(HistoryActivityInstanceImpl.class);
		subselect.add(Restrictions.idEq(dbid));
		subselect.setProjection(Property.forName("executionId"));
		Criteria criteria = o_historyActivityInstanceDAO.createCriteria();
		criteria.add(Restrictions.not(Restrictions.idEq(dbid)));
		criteria.add(Property.forName("executionId").in(subselect));
		criteria.addOrder(Order.desc("startTime"));
		return criteria.list();
	}
	
}