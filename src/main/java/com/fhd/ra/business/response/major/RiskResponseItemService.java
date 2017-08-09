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

import com.fhd.core.utils.Identities;
import com.fhd.dao.response.major.RiskResponseItemDAO;
import com.fhd.entity.response.major.RiskResponseItem;
import com.fhd.entity.response.major.RiskResponseItemCounterRela;
import com.fhd.entity.response.major.RiskResponseScheme;
import com.fhd.entity.response.major.RiskResponseSchemeItemRela;
import com.fhd.ra.web.controller.response.major.utils.DateUtil;

@Service
public class RiskResponseItemService {

	private static final Logger log = LoggerFactory.getLogger(RiskResponseItemService.class);
	@Autowired
	private RiskResponseItemDAO RiskResponseItemDAO;
	@Autowired
	private RiskResponseSchemeService riskResponseSchemeService;
	@Autowired
	private RiskResponseSchemeItemRelaService riskResponseSchemeItemRelaService;
	
	@Transactional
	public void save(RiskResponseItem entity)throws Exception{
		this.RiskResponseItemDAO.merge(entity);
	}
	@Transactional
	public void saveBatch(final List<RiskResponseItem> list)throws Exception{
		 this.RiskResponseItemDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_item(id,description,flow,reason,type,create_user,create_org) values(?,?,?,?,?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponseItem obj : list) {
	                	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getDescription());
	                      pst.setString(3, obj.getFlow());
	                      pst.setString(4, obj.getReason()); 
	                      pst.setString(5, obj.getType());
	                      pst.setString(6, obj.getCreateUser().getId());
	                      pst.setString(7, obj.getCreateOrg().getId());
	                      pst.addBatch();
					}
	                pst.executeBatch();
	            }
	        });
	}
	@Transactional
	public void deleteBatchByList(final List<RiskResponseItem> list)throws Exception{
		this.RiskResponseItemDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_item where id = ? ";
                pst = connection.prepareStatement(sql);
                for (RiskResponseItem entity: list) {
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
	public void update(RiskResponseItem entity)throws Exception{
		this.RiskResponseItemDAO.merge(entity);
	}
	@Transactional
	public void delete(RiskResponseItem entity)throws Exception{
		this.RiskResponseItemDAO.delete(entity.getId());
	}
	
	public RiskResponseItem findObj(RiskResponseItem entity){
		return this.RiskResponseItemDAO.get(entity.getId());
	}
	/**  
	* @Title: saveItem  
	* @Description: 保存风险事项以及方案可风险事项的关系
	* @param item
	* @param schemeId
	* @return RiskResponseItem
	* @throws  
	*/
	@Transactional
	public RiskResponseItem saveItem(RiskResponseItem item,String schemeId){
		RiskResponseScheme schemeParam = new RiskResponseScheme();
		schemeParam.setId(schemeId);
		RiskResponseScheme scheme = this.riskResponseSchemeService.findObj(schemeParam);
		if(scheme !=null){
			RiskResponseSchemeItemRela riskResponseSchemeItemRela = new RiskResponseSchemeItemRela();
			riskResponseSchemeItemRela.setId(Identities.uuid());
			riskResponseSchemeItemRela.setItem(item);
			riskResponseSchemeItemRela.setScheme(scheme);
			try {
				save(item);
				this.riskResponseSchemeItemRelaService.save(riskResponseSchemeItemRela);
			} catch (Exception e) {
				item = null;
				e.printStackTrace();
			}
		}
		return item;
	}
}
