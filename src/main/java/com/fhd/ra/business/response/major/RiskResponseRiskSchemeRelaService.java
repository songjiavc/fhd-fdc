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

import com.fhd.dao.response.major.RiskResponseRiskSchemeRelaDAO;
import com.fhd.entity.response.major.RiskResponseRiskSchemeRela;
import com.fhd.entity.response.major.RiskResponseTaskExecution;

@Service
public class RiskResponseRiskSchemeRelaService {

	private static final Logger log = LoggerFactory.getLogger(RiskResponseRiskSchemeRelaService.class);
	@Autowired
	private RiskResponseRiskSchemeRelaDAO RiskResponseRiskSchemeRelaDAO;
	@Transactional
	public void save(RiskResponseRiskSchemeRela entity)throws Exception{
		this.RiskResponseRiskSchemeRelaDAO.merge(entity);
	}
	@Transactional
	public void saveBatch(final List<RiskResponseRiskSchemeRela> list)throws Exception{
		 this.RiskResponseRiskSchemeRelaDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_risk_scheme_rela(id,task_execution_id,scheme_id) values(?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponseRiskSchemeRela obj : list) {
	                	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getTaskExecutionObj().getId());
	                      pst.setString(3, obj.getScheme().getId());
	                      pst.addBatch();
					}
	                pst.executeBatch();
	            }
	        });
	}
	@Transactional
	public void deleteBatchByList(final List<RiskResponseRiskSchemeRela> list)throws Exception{
		this.RiskResponseRiskSchemeRelaDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_risk_scheme_rela where id = ? ";
                pst = connection.prepareStatement(sql);
                for (RiskResponseRiskSchemeRela entity: list) {
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
	public void update(RiskResponseRiskSchemeRela entity)throws Exception{
		this.RiskResponseRiskSchemeRelaDAO.merge(entity);
	}
	@Transactional
	public void delete(RiskResponseRiskSchemeRela entity)throws Exception{
		this.RiskResponseRiskSchemeRelaDAO.delete(entity.getId());
	}
	
	public RiskResponseRiskSchemeRela findObj(RiskResponseRiskSchemeRela entity){
		return this.RiskResponseRiskSchemeRelaDAO.get(entity.getId());
	}
	
	public RiskResponseRiskSchemeRela findByTaskExecution(RiskResponseTaskExecution taskExecutionObj){
		Criteria criteria = this.RiskResponseRiskSchemeRelaDAO.createCriteria();
		criteria.createAlias("taskExecutionObj", "taskExecutionObj");
		criteria.add(Restrictions.eq("taskExecutionObj.id", taskExecutionObj.getId()));
		return (RiskResponseRiskSchemeRela) criteria.uniqueResult();
	}
}
