/**
 * AssessPlanBO.java
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		刘中帅
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.icm.business.assess;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.icm.assess.MeasureDAO;
import com.fhd.entity.icm.control.Measure;
import com.fhd.fdc.utils.Contents;

/**
 * 控制措施BO.
 * @author 吴德福
 * @since Ver 1.1
 * @Date 2013-12-12 13:40:32
 * @see
 */
@Service
@SuppressWarnings({"unchecked"})
public class MeasureBO {

	@Autowired
	private MeasureDAO o_measureDAO;

	/**
	 * 根据公司id查询应对措施.
	 * @author 吴德福
	 * @param companyId
	 * @return List<Measure>
	 */
	public List<Measure> findMeasureListByCompanyId(String companyId){
		Criteria criteria = o_measureDAO.createCriteria();
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return criteria.list();
	}
}