package com.fhd.icm.business.assess;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.icm.assess.MeasureRelaRiskDAO;
import com.fhd.entity.icm.control.MeasureRelaRisk;

/**
 * 控制措施相关风险BO.
 * @author 吴德福
 * @Date 2013-12-13 14:58:02
 */
@Service
@SuppressWarnings("unchecked")
public class MeasureRelaRiskBO {

	@Autowired
	private MeasureRelaRiskDAO o_measureRelaRiskDAO;
	
	/**
	 * 根据公司id查询控制措施相关风险列表.
	 * @author 吴德福
	 * @param companyId
	 * @return List<MeasureRelaRisk>
	 */
	public List<MeasureRelaRisk> findMeasureRelaRiskListByCompanyId(String companyId){
		Criteria criteria = o_measureRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "r");
		criteria.createAlias("r.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
}