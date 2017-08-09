package com.fhd.ra.business.response.major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.response.major.RiskResponseItemCounterRelaDAO;
import com.fhd.entity.response.major.RiskResponseItemCounterRela;

@Service
public class RiskResponseItemCounterRelaService {

	private static final Logger log = LoggerFactory.getLogger(RiskResponseItemCounterRelaService.class);
	@Autowired
	private RiskResponseItemCounterRelaDAO RiskResponseItemCounterRelaDAO;
	@Transactional
	public void save(RiskResponseItemCounterRela entity)throws Exception{
		this.RiskResponseItemCounterRelaDAO.merge(entity);
	}
	@Transactional
	public void saveBatch(final List<RiskResponseItemCounterRela> list)throws Exception{
		 this.RiskResponseItemCounterRelaDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	            	connection.setAutoCommit(false);
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_item_counter_rela(id,item_id,counter_id,emp_id) values(?,?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponseItemCounterRela obj : list) {
	                	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getItem().getId());
	                      pst.setString(3, obj.getCounter().getId());
	                      pst.setString(4, obj.getExecutionEmp().getId());
	                      pst.addBatch();
					}
	                pst.executeBatch();
	                connection.commit();
	                connection.setAutoCommit(true);
	            }
	        });
	}
	@Transactional
	public void deleteBatchByList(final List<RiskResponseItemCounterRela> list)throws Exception{
		this.RiskResponseItemCounterRelaDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_item_counter_rela where id = ? ";
                pst = connection.prepareStatement(sql);
                for (RiskResponseItemCounterRela entity: list) {
                	pst.setString(1,entity.getId());
                	pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
	}
	@Transactional
	public void update(RiskResponseItemCounterRela entity)throws Exception{
		this.RiskResponseItemCounterRelaDAO.merge(entity);
	}
	@Transactional
	public void delete(RiskResponseItemCounterRela entity)throws Exception{
		this.RiskResponseItemCounterRelaDAO.delete(entity.getId());
	}
	
	public RiskResponseItemCounterRela findObj(RiskResponseItemCounterRela entity){
		return this.RiskResponseItemCounterRelaDAO.get(entity.getId());
	}
	@SuppressWarnings("unchecked")
	public List<RiskResponseItemCounterRela> findListByItemId(String itemId){
		Criteria criteria = this.RiskResponseItemCounterRelaDAO.createCriteria();
		criteria.createAlias("item", "item");
		criteria.add(Restrictions.eq("item.id", itemId));
		return criteria.list();
	}
}
