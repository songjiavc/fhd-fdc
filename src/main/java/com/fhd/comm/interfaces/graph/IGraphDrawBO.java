/**
 * IGraphDrawBO.java
 * com.fhd.comm.interfaces
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-10-28 		张 雷
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.comm.interfaces.graph;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 获取graph图形的亮灯状态
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-10-28		上午9:45:22
 *
 * @see 	 
 */
@Service
public interface IGraphDrawBO {
	
	/**
	 * 红灯
	 */
	static final String RISK_LEVEL_RED = "red";
	/**
	 * 黄灯
	 */
	static final String RISK_LEVEL_YELLOW = "yellow";
	/**
	 * 绿灯
	 */
	static final String RISK_LEVEL_GREEN = "green";
	
	public final static String ORG_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/organization.png";
	public final static String ORG_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/org_red.png";
	public final static String ORG_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/org_yellow.png";
	public final static String ORG_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/org_green.png";
	
	public final static String USER_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/user.png";
	public final static String ICON_USER_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_user.png";
	
	
	public final static String SM_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/strategy.png";
	public final static String SM_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/sm_red.png";
	public final static String SM_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/sm_yellow.png";
	public final static String SM_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/sm_green.png";
	
	public final static String KPI_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/kpi.png";
	public final static String KPI_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/kpi_red.png";
	public final static String KPI_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/kpi_yellow.png";
	public final static String KPI_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/kpi_green.png";
	public final static String ICON_KPI_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_kpi.png";
	public final static String ICON_KPI_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_kpi_red.png";
	public final static String ICON_KPI_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_kpi_yellow.png";
	public final static String ICON_KPI_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_kpi_green.png";
	
	
	public final static String RISK_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/risk.png";
	public final static String RISK_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/risk_red.png";
	public final static String RISK_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/risk_yellow.png";
	public final static String RISK_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/risk_green.png";
	public final static String ICON_RISK_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_risk.png";
	public final static String ICON_RISK_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_risk_red.png";
	public final static String ICON_RISK_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_risk_yellow.png";
	public final static String ICON_RISK_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_risk_green.png";
	
	public final static String PROCESS_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/flow.png";
	public final static String PROCESS_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/flow_red.png";
	public final static String PROCESS_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/flow_yellow.png";
	public final static String PROCESS_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/flow_green.png";
	
	public final static String SC_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/scorecard.png";
	public final static String SC_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/sc_red.png";
	public final static String SC_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/sc_yellow.png";
	public final static String SC_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/sc_green.png";
	
	public final static String CONTROL_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/control.png";
	public final static String ICON_CONTROL_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_control.png";
	
	public final static String DOCUMENT_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/document.png";
	public final static String ICON_DOCUMENT_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_document.png";
	
//	public final static String ICON_ORG_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_org_red.png";
//	public final static String ICON_ORG_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_org_yellow.png";
//	public final static String ICON_ORG_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_org_green.png";

//	public final static String ICON_SC_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_sc_red.png";
//	public final static String ICON_SC_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_sc_yellow.png";
//	public final static String ICON_SC_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_sc_green.png";

//	public final static String ICON_SM_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_sm_red.png";
//	public final static String ICON_SM_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_sm_yellow.png";
//	public final static String ICON_SM_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_sm_green.png";
	
//	public final static String ICON_PROCESS_RED_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_flow_red.png";
//	public final static String ICON_PROCESS_YELLOW_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_flow_yellow.png";
//	public final static String ICON_PROCESS_GREEN_PNG = "/scripts/mxgraph-1.10/grapheditor/stencils/images/icon_flow_green.png";
	
	
	
	/**
	 * <pre>
	 *	通过对象的ID获得其的最新的风险状态
	 *	map格式：{
	 *				id:'',//主对象的ID
	 *				riskLevel:RISK_LEVEL_YELLOW,//主对象的风险状态
	 *				value:3.41,//主对象的风险水平
	 *				name:''//主对象的名称
	 *			}
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 对象的ID
	 * @return Map<String,Object> 
	 * @since  fhd　Ver 1.1
	*/
	public Map<String,Object> showStatus(String id);
	
	/**
	 * <pre>
	 * 批量通过对象的ID获得其的最新的风险状态
	 *	map格式：{
	 *				id:'',//主对象的ID
	 *				riskLevel:RISK_LEVEL_YELLOW,//主对象的风险状态
	 *				value:3.41,//主对象的风险水平
	 *				name:''//主对象的名称
	 *			}
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 对象的idList
	 * @return List<Map<String,Object>> 
	 * @since  fhd　Ver 1.1
	*/
	public List<Map<String,Object>> showStatus(List<String> idList);
}

