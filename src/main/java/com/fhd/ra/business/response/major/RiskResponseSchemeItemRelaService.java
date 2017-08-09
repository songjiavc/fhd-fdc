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

import com.fhd.dao.response.major.RiskResponseSchemeItemRelaDAO;
import com.fhd.entity.response.major.RiskResponseRiskSchemeRela;
import com.fhd.entity.response.major.RiskResponseSchemeItemRela;

@Service
public class RiskResponseSchemeItemRelaService {

	private static final Logger log = LoggerFactory.getLogger(RiskResponseSchemeItemRelaService.class);
	@Autowired
	private RiskResponseSchemeItemRelaDAO RiskResponseSchemeItemRelaDAO;
	
	@Transactional
	public void save(RiskResponseSchemeItemRela entity)throws Exception{
		this.RiskResponseSchemeItemRelaDAO.merge(entity);
	}
	
	@Transactional
	public void saveBatch(final List<RiskResponseSchemeItemRela> list)throws Exception{
		 this.RiskResponseSchemeItemRelaDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_scheme_item_rela(id,scheme_id,item_id) values(?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponseSchemeItemRela obj : list) {
	                	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getScheme().getId());
	                      pst.setString(3, obj.getItem().getId());
	                      pst.addBatch();
					}
	                pst.executeBatch();
	            }
	        });
	}
	@Transactional
	public void deleteBatchByList(final List<RiskResponseSchemeItemRela> list)throws Exception{
		this.RiskResponseSchemeItemRelaDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_scheme_item_rela where id = ? ";
                pst = connection.prepareStatement(sql);
                for (RiskResponseSchemeItemRela entity: list) {
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
	public void update(RiskResponseSchemeItemRela entity)throws Exception{
		this.RiskResponseSchemeItemRelaDAO.merge(entity);
	}
	@Transactional
	public void delete(RiskResponseSchemeItemRela entity)throws Exception{
		this.RiskResponseSchemeItemRelaDAO.delete(entity.getId());
	}
	
	public RiskResponseSchemeItemRela findObj(RiskResponseSchemeItemRela entity)throws Exception{
		return this.RiskResponseSchemeItemRelaDAO.get(entity.getId());
	}
	@SuppressWarnings("unchecked")
	public List<RiskResponseSchemeItemRela> findBySchemeId(String  schemeObjectId){
		Criteria criteria = this.RiskResponseSchemeItemRelaDAO.createCriteria();
		criteria.createAlias("scheme", "scheme");
		criteria.add(Restrictions.eq("scheme.id", schemeObjectId));
		return criteria.list();
	}
	
}
