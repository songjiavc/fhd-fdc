package com.fhd.ra.business.assess.formulateplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.response.RiskResponseDAO;
import com.fhd.entity.assess.quaAssess.ResponseIdea;
import com.fhd.entity.response.RiskResponse;

@Service
public class RiskResponseBO{
	
	@Autowired
	RiskResponseDAO o_riskResponseDAO;

	/** 
	  * @Description: 保存汇总辨识人添加的风险和应对信息
	  * @author jia.song@pcitc.com
	  * @date 2017年5月24日 下午4:00:52 
	  * @param riskId
	  * @param objectId
	  * @param responseText 
	  */
	@Transactional
	public void saveRiskResponse(String riskId,String responseText) {
		//保存完风险基本信息后，保存汇总人的应对意见
		RiskResponse riskResponse = getResponseByRiskId(riskId);
		if(riskResponse != null){
			riskResponse.setRiskId(riskId);
			riskResponse.setEditIdeaContent(responseText);
		}else{
			riskResponse = new RiskResponse();
			riskResponse.setId(UUID.randomUUID().toString());
			riskResponse.setRiskId(riskId);
			riskResponse.setEditIdeaContent(responseText);
		}
		o_riskResponseDAO.merge(riskResponse);
	}
	

	
	public RiskResponse getResponseByRiskId(String riskId){
		Criteria criteria = o_riskResponseDAO.createCriteria();
		criteria.add(Restrictions.eq("riskId", riskId));
		return (RiskResponse) criteria.uniqueResult();
	}	
	
	/** 
	  * @Description: 
	  * @author jia.song@pcitc.com
	  * @date 2017年5月25日 下午2:03:07 
	  * @param riskIds 
	  */
	@Transactional
	public void deleteRiskResponseByRiskIds(List<String> riskIds){
		o_riskResponseDAO.createQuery("delete RiskResponse where riskId in (:riskIds)").setParameterList("riskIds", riskIds).executeUpdate();
	}
	
	
	/**
	 * @param objectIds
	 * 辨识结束后批量讲应对措施导入风险库应对措施中
	 */
	@Transactional
	public void insertRiskResponseByObjectIds(List<String> objectIds){
		//找到临时表集合
		String querysql = "SELECT RISK_ID,EDIT_IDEA_CONTENT FROM t_rm_last_lead_edit_response WHERE score_object_id in (:objectIds)";
		final List<Object[]> list = o_riskResponseDAO.createSQLQuery(querysql).setParameterList("objectIds", objectIds).list();
		o_riskResponseDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				PreparedStatement pst = connection.prepareStatement("INSERT INTO t_rm_risk_response(ID,RISK_ID,EDIT_IDEA_CONTENT) VALUES(?,?,?)");
				for(Object[] object : list){
					if(object[0] != null && object[1] != null){
						pst.setObject(1, Identities.uuid());
						pst.setObject(2, object[0]);
	                    pst.setObject(3, object[1]);
	                    pst.addBatch();
					}
				}
				connection.commit();
				pst.executeBatch();
				connection.setAutoCommit(true);
			}
		});
	}
	
}