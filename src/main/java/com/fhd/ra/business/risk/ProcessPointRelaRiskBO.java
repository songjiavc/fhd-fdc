package com.fhd.ra.business.risk;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.process.ProcessPointRelaRiskDAO;
import com.fhd.entity.process.ProcessPointRelaRisk;

/**
 * 流程节点相关风险BO.
 * @author 吴德福
 * @Date 2013-12-13 14:58:02
 */
@Service
@SuppressWarnings("unchecked")
public class ProcessPointRelaRiskBO {

	@Autowired
	private ProcessPointRelaRiskDAO o_processPointRelaRiskDAO;
	
	/**
	 * 根据公司id查询流程相关风险列表.
	 * @author 吴德福
	 * @param companyId
	 * @return List<ProcessPointRelaRisk>
	 */
	public List<ProcessPointRelaRisk> findProcessPointRelaRiskListByCompanyId(String companyId){
		Criteria criteria = o_processPointRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "r");
		criteria.createAlias("r.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
}
