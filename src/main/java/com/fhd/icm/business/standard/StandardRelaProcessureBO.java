/**
 * StandardRelaOrgBO.java
 * com.fhd.icm.business.standard
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-12-25 		刘中帅
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.icm.business.standard;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.icm.standard.StandardRelaProcessureDAO;
import com.fhd.entity.icm.standard.StandardRelaProcessure;
import com.fhd.icm.interfaces.standard.IStandardRelaOrg;

/**
 * @author   元杰
 * @version  
 * @see 	 
 */
@Service
@SuppressWarnings("unchecked")
public class StandardRelaProcessureBO implements IStandardRelaOrg{
	
	@Autowired
	private StandardRelaProcessureDAO o_standardRelaProcessureDAO;

	/**
	 * <pre>
	 *通过Id集合删除内控标准与流程的关联实体
	 * </pre>
	 * 
	 * @author 元杰
	 * @param standardId
	 */
	@Transactional
	public void delStandardRelaProcessureByStandardId(String standardId) {
		if(StringUtils.isNotBlank(standardId)){
			Criteria criteria=o_standardRelaProcessureDAO.createCriteria();
			criteria.add(Restrictions.eq("standard.id", standardId));
			List<StandardRelaProcessure> srpList = criteria.list();
			for(StandardRelaProcessure srp : srpList){
				o_standardRelaProcessureDAO.delete(srp);
			}
		}
	}


	/**
	 * <pre>
	 * 保存标准关联流程
	 * </pre>
	 * 
	 * @author 元杰
	 */
	@Transactional
	public void saveStandardRelaProcessure(StandardRelaProcessure standardRelaProcessure) {
		if(null != standardRelaProcessure){
			o_standardRelaProcessureDAO.merge(standardRelaProcessure);
		}
	}
	
	/**
	 * 根据公司id查询流程相关控制标准(要求)集合.
	 * @author 吴德福
	 * @param companyId
	 * @return List<StandardRelaProcessure>
	 */
	public List<StandardRelaProcessure> findStandardRelaRiskByCompanyId(String companyId){
		Criteria criteria = o_standardRelaProcessureDAO.createCriteria();
		criteria.createAlias("processure", "p");
		criteria.createAlias("p.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
}

