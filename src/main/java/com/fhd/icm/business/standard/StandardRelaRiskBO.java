package com.fhd.icm.business.standard;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.icm.standard.StandardRelaRiskDAO;
import com.fhd.entity.icm.standard.StandardRelaRisk;

/**
 * 控制标准(要求)关联风险BO.
 * @author 吴德福
 * @Date 2013-12-16 13:06:56
 */
@Service
@SuppressWarnings("unchecked")
public class StandardRelaRiskBO {

	@Autowired
	private StandardRelaRiskDAO o_standardRelaRiskDAO;
	
	/**
	 * 根据公司id查询风险相关控制标准(要求)集合.
	 * @author 吴德福
	 * @param companyId
	 * @return List<StandardRelaRisk>
	 */
	public List<StandardRelaRisk> findStandardRelaRiskByCompanyId(String companyId){
		Criteria criteria = o_standardRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "r");
		criteria.createAlias("r.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
}
