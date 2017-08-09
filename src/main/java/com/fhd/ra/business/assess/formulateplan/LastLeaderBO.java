package com.fhd.ra.business.assess.formulateplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.quaAssess.LastLeadResponseIdeaDAO;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.quaAssess.LastLeadResponseIdea;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
/**
 * 打分对象，打分部门BO类
 * @author 王再冉
 *
 */
@Service
public class LastLeaderBO{
	@Autowired
	LastLeadResponseIdeaDAO o_lastLeadResponseIdeaDAO;
    @Autowired
    ScoreObjectBO scoreObjectBO;
    @Autowired
    RiskResponseBO riskResponseBO;
	/** 
	  * @Description: 保存汇总辨识人添加的风险和应对信息
	  * @author jia.song@pcitc.com
	  * @date 2017年5月24日 下午4:00:52 
	  * @param riskId
	  * @param objectId
	  * @param responseText 
	  */
	@Transactional
	public void saveLastLeader(String riskId, String objectId,String responseText) {
		//保存完风险基本信息后，保存汇总人的应对意见
        LastLeadResponseIdea lastLeadResponseIdea = new LastLeadResponseIdea();
        lastLeadResponseIdea.setId(UUID.randomUUID().toString());
        lastLeadResponseIdea.setRiskId(riskId);
        lastLeadResponseIdea.setScoreObjectId(objectId);
        lastLeadResponseIdea.setEditIdeaContent(responseText);
        o_lastLeadResponseIdeaDAO.merge(lastLeadResponseIdea);
	}
	
	
	/** 
	  * @Description: 根据打分对象id获取应对说明
	  * @author jia.song@pcitc.com
	  * @date 2017年5月24日 下午7:30:50 
	  * @param objectId
	  * @return 
	  */
	public Map<String,Object> findResponseTextByObjectId(String objectId){
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		String sql = "SELECT edit_idea_content FROM t_rm_last_lead_edit_response res WHERE res.score_object_id = :scoreObjectId";
		SQLQuery query = o_lastLeadResponseIdeaDAO.createSQLQuery(sql);
		query.setParameter("scoreObjectId", objectId);
		List<Object> list = query.list();
		for(Object o :list){
			if(o != null){
				rtnMap.put("responseText", o);
				break;
			}
		}
		return rtnMap;
	}
	
	/** 
	  * @Description: 根据打分对象id更新应对说明
	  * @author jia.song@pcitc.com
	  * @date 2017年5月24日 下午8:18:02 
	  * @param objectId
	  * @param responseText 
	  */
	@Transactional
	public void updateResponseTextByObjectId(String objectId,String riskId,String responseText){
		Map<String,Object> paramMap = this.findResponseTextByObjectId(objectId);
		if(paramMap.isEmpty()){
			this.saveLastLeader(riskId, objectId, responseText);
		}else{
			String sql = "UPDATE t_rm_last_lead_edit_response res SET res.edit_idea_content = :responseText WHERE res.score_object_id = :scoreObjectId";
			SQLQuery query = o_lastLeadResponseIdeaDAO.createSQLQuery(sql);
			query.setParameter("scoreObjectId", objectId);
			query.setParameter("responseText", responseText);
			query.executeUpdate();
		}
	}
	
	/**
	 * @author songjia
	 * @desc    判断整理是否已经存储过应对结果
	 * @param scoreObjectId
	 * @date     2017-7-27
	 */
	public Long getCountFromLastResponseByScoreObjectId(String scoreObjectId){
		String hqlQuery = "SELECT  count(*) FROM LastLeadResponseIdea lastIdea WHERE lastIdea.scoreObjectId = :scoreObjectId";
		Query query = o_lastLeadResponseIdeaDAO.createQuery(hqlQuery).setParameter("scoreObjectId", scoreObjectId);
		return  (Long)query.uniqueResult();
	}
	
	
	@Transactional
	public void syncRiskResponseByAssessPlanId(String assessPlanId){
		//根据计划id查找所有打分对象id
		List<RiskScoreObject> objectList = scoreObjectBO.findRiskScoreObjByplanId(assessPlanId);
		List<String> riskIds = new ArrayList<String>();
		List<String> objectIds = new ArrayList<String>();
		for(RiskScoreObject riskScoreObject : objectList){
			riskIds.add(riskScoreObject.getRiskId());
			objectIds.add(riskScoreObject.getId());
		}
		riskResponseBO.deleteRiskResponseByRiskIds(riskIds);
		riskResponseBO.insertRiskResponseByObjectIds(objectIds);
	}
}