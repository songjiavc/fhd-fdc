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

import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author 宋佳
 * @ param riskId 
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see 如果riskId ="" 查询全部的应对措施
 */
@Service
public interface ISolutionOutSiteBO{
	/**
	 * add by 宋佳
	 * 2013-9-24
	 * 删除应对和文件的关联关系
	 * @param idList 应对id列表  solutionIds
	 * @return 删除成功或者失败的返回值
	 */
	public int deleteFilesFromSolutionBySolutionId(List<String> idList);
	/**
	 * add by 宋佳
	 * 2013-9-24
	 * 删除风险和应对的关联关系
	 * @param idList 应对id列表  solutionIds
	 * @return 删除成功或者失败的返回值
	 */
	public int deleteRisksFromSolutionBySolutionId(List<String> idList);
	/**
	 * add by 宋佳
	 * 2013-9-24
	 * 删除应对和机构关联
	 * @param idList 应对id列表  solutionIds
	 * @return 删除成功或者失败的返回值
	 */
	public int deleteOrgAndEmpFromSolutionBySolutionId(List<String> idList);
	/**
	 * add by 宋佳
	 * 2013-9-24
	 * 删除应对
	 * @param idList 应对id列表  solutionIds
	 * @return 删除成功或者失败的返回值
	 */
	public int deleteSolutionsBySolutionId(List<String> idList);
}
