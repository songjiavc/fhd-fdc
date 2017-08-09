package com.fhd.ra.business.assess.oper;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.risk.TempSyncRiskrbsScoreDAO;
import com.fhd.entity.risk.TempSyncRiskrbsScore;

@Service
public class TempSyncRiskRbsBO {

	@Autowired
	private TempSyncRiskrbsScoreDAO tempSyncRiskrbsScoreDAO;
	
	//save riskscore to riskku
	public TempSyncRiskrbsScore getRiskScoreByRiskIdAndAssessPlanId(String riskId,String assessPlanId){
		Criteria criteria = tempSyncRiskrbsScoreDAO.createCriteria();
		criteria.add(Restrictions.eq("pk.riskId", riskId));
		if(StringUtils.isNotBlank(assessPlanId)){
			criteria.add(Restrictions.eq("pk.assessPlanId", assessPlanId));
		}else{
			criteria.add(Restrictions.isNull("pk.assessPlanId"));
		}
		return (TempSyncRiskrbsScore)criteria.uniqueResult();
	}
	
	
}