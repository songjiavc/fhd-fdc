package com.fhd.ra.business.assess.oper;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.assess.formulatePlan.RiskAccessPlanDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;

@Service
public class RAssessPlanBO {

	@Autowired
	private RiskAccessPlanDAO o_riskAssessPlanDAO;
	
	/**
	 * 根据计划任务id查询计划任务
	 * @param id 评估ID
	 * @return RiskAssessPlan
	 * @author 金鹏祥
	 */
	@SuppressWarnings("unchecked")
	public RiskAssessPlan findRiskAssessPlanById(String id) {
		Criteria c = o_riskAssessPlanDAO.createCriteria();
		List<RiskAssessPlan> list = null;
		
		if (StringUtils.isNotBlank(id)) {
			c.add(Restrictions.eq("id", id));
		} else {
			return null;
		}
		
		list = c.list();
		if(null!=list&&list.size()>0){
			
			return list.get(0);
		}
		return new RiskAssessPlan();
	}
	
	/**
	 * 查询未开始和执行中的计划
	 * @param status
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskAssessPlan> findRiskAssessPlansBydealStatus(){
		Criteria c = o_riskAssessPlanDAO.createCriteria();
		c.add(Restrictions.or(Restrictions.eq("dealStatus", "H"), Restrictions.eq("dealStatus", "N")));
		return c.list();
	}
}
