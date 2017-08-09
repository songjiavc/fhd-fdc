/**
 * AssessPlanBO.java
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-9-22 		宋佳
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserventd.
 */

package com.fhd.ra.interfaces.response;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fhd.entity.response.SolutionRelaRisk;


/**
 * @author 宋佳
 * @ param riskId 
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see 如果riskId ="" 查询全部的应对措施
 */
@Service
public interface IResponseOutSiteBO{
	/**
	 * 通过风险idlist 查找所有的风险应对关联关系
	 * add by 宋佳
	 * @param riskIds
	 * @return
	 */
	public List<SolutionRelaRisk> findSolutionsByRisk(List<String> riskIds);
}
