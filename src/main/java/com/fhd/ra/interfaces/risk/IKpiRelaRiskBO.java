/**
 * IRiskOrgBO.java
 * com.fhd.risk.interfaces
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/
/**
 * IRiskOrgBO.java
 * com.fhd.risk.interfaces
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19        金鹏祥
 *
 * Copyright (c) 2012, FirstHuida All Rights Reserved.
*/


package com.fhd.ra.interfaces.risk;

import java.util.List;
import java.util.Set;

import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;


/**
 * 风险、部门关联接口
 *
 * @author   zhengjunxiang
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-19		上午09:34:53
 *
 * @see 	 
 */
public interface IKpiRelaRiskBO {

	/**
	 * 保存风险指标和影响指标
	 * @author zhengjunxiang
	 */
	public void saveKpiRelaRisk(KpiRelaRisk kpiRelaRisk);
	
	/**
	 * 删除风险指标和影响指标
	 * @author zhengjunxiang
	 */
	public void removeKpiRelaRiskById(String id);
}

