/**
 * GraphControl.java
 * com.fhd.comm.web.controller.graph
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-10-29 		张 雷
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.comm.web.controller.graph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.graph.GraphBO;
import com.fhd.comm.interfaces.graph.IGraphDrawBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.graph.Graph;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.utils.mxgraph.GraphCell;
import com.fhd.icm.utils.mxgraph.GraphCellXmlUtils;
import com.fhd.icm.utils.mxgraph.GraphUserObject;
import com.fhd.ra.business.risk.graph.OrganizationGraphDrawBO;
import com.fhd.ra.business.risk.graph.ProcessGraphDrawBO;
import com.fhd.ra.business.risk.graph.RiskGraphDrawBO;
import com.fhd.sm.business.graph.KpiGraphDrawBO;
import com.fhd.sm.business.graph.ScGraphDrawBO;
import com.fhd.sm.business.graph.SmGraphDrawBO;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Controller
public class GraphControl {
	@Autowired
	private GraphBO o_graphBO;
	
	@Autowired
	private RiskGraphDrawBO o_riskGraphDrawBO;
	
	@Autowired
	private OrganizationGraphDrawBO o_organizationGraphDrawBO;
	
	@Autowired
	private ProcessGraphDrawBO o_processGraphDrawBO;
	
	@Autowired
	private KpiGraphDrawBO o_kpiGraphDrawBO;
	
	@Autowired
	private ScGraphDrawBO o_scGraphDrawBO;
	
	@Autowired
	private SmGraphDrawBO o_smGraphDrawBO;
	
	@ResponseBody
	@RequestMapping(value = "/comm/graph/mergeGraph.f")
	public Map<String,Object> mergeGraph(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		Map<String, Object> map = new HashMap<String, Object>();
    	response.setContentType("text/html;charset=utf-8");
        Graph graph = new Graph();
        String graphId = request.getParameter("graphId");//程序约定的
        String graphName = request.getParameter("graphName");//程序约定的
        String graphContext = request.getParameter("graphContext");//程序约定的
        if(StringUtils.isNotBlank(graphId)){
			graph = o_graphBO.findGraphById(graphId);
		}else{
			 graph.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
		}
        graph.setName(graphName);
        graph.setContent(java.net.URLDecoder.decode(graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"),"UTF-8").getBytes("UTF-8"));
        if(StringUtils.isNotBlank(graphId)){
        	o_graphBO.mergeGraph(graph);
		}else{
			graphId = o_graphBO.saveGraph(graph);
		}
        map.put("success", true);
        map.put("id", graphId);
        return map;
	}
	
	/**
	 * <pre>
	 * 删除图形及关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/removeGraph.f")
	public void removeGraph(String graphId,HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		boolean flag = false;
        try {
        	out = response.getWriter();
        	if(StringUtils.isNotBlank(graphId)){
        		o_graphBO.removeGraphById(graphId);
        		flag = true;
        	}
        	out.print(flag);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(null != out){
        		out.close();
        	}
        }
	}
	
	
	/**
	 * <pre>
	 * 分页查询图形列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param limit 页面记录数
	 * @param start 起始值
	 * @param query 查询条件
	 * @param companyId 公司ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/comm/graph/findGraphPageBySome.f")
	public Map<String,Object> findGraphPageBySome(int limit, int start, String query, String companyId){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Page<Graph> page = new Page<Graph>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_graphBO.findGraphPageBySome(page, query, companyId);
		List<Graph> graphList = page.getResult();
		for (Graph graph : graphList) {
			Map<String,Object> dataMap = new HashMap<String,Object>();
			dataMap.put("id", graph.getId());
			dataMap.put("name", graph.getName());
			String updateDate = "";
			if(null != graph.getLastModifyTime()){
				updateDate = DateUtils.formatDate(
					DateUtils.parseTimeStamp(
						String.valueOf(graph.getLastModifyTime()).substring(
							0,String.valueOf(graph.getLastModifyTime()).lastIndexOf("."))), "yyyy-MM-dd");
			}else{
				if(null != graph.getCreateTime()){
					updateDate = DateUtils.formatDate(DateUtils.parseTimeStamp(
						String.valueOf(graph.getCreateTime()).substring(
							0,String.valueOf(graph.getCreateTime()).lastIndexOf("."))), "yyyy-MM-dd");
				}
			}
			dataMap.put("updateDate", updateDate);
			datas.add(dataMap);
			
		}
		map.put("datas", datas);
		map.put("totalCount", page.getTotalItems());
		return map;
	}
	
	/**
	 * <pre>
	 * 查询单个图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形ID
	 * @param viewType 查看状态
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/findGraph.f")
	public String findGraph(String panelId, String id, String viewType, HttpServletRequest request) throws Exception{
		Graph graph = null;
		request.setAttribute("panelId", panelId);//程序约定的
		if(StringUtils.isNotBlank(id)){
			graph = o_graphBO.findGraphById(id);
		}
		if(graph!=null){
			String graphContext = IOUtils.toString(new ByteArrayInputStream(graph.getContent()),"UTF-8");
			request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));
			request.setAttribute("graphId", graph.getId());
			request.setAttribute("graphName", graph.getName());
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){//程序约定的
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphdrawview";//程序约定的
		}
	}
	
	/**
	 * <pre>
	 * 刷新图的最新状态
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图的ID
	 * @param request 
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value="/comm/graph/refreshgraph.f")
	public void refreshGraph(String graphId,HttpServletRequest request,HttpServletResponse response) throws IOException{
		PrintWriter out=response.getWriter();
		boolean result=false;
		try {
			String ctx = request.getContextPath();
			Graph graph = o_graphBO.findGraphById(graphId);
			if(null != graph){
				String graphContext = new String(graph.getContent());
				//将图的xml格式转换成图形元素对象
				List<GraphCell> graphCellList = GraphCellXmlUtils.renderGraphCell(graphContext);
				List<GraphUserObject> graphUserObjectList = GraphCellXmlUtils.renderGraphUserObject(graphContext);
				//存放图中需要更新的对象的ID集合
				List<String> riskIdList = new ArrayList<String>();
				List<String> orgIdList = new ArrayList<String>();
				List<String> processIdList = new ArrayList<String>();
				List<String> kpiIdList = new ArrayList<String>();
				List<String> smIdList = new ArrayList<String>();
				List<String> scIdList = new ArrayList<String>();
				
				for (GraphCell graphCell : graphCellList) {
					String graphCellId = graphCell.getId();
					if(graphCellId.indexOf('|')!=-1){
						String[] graphCellIdArray = graphCellId.split("\\|");
						for (int i = 0; i < graphCellIdArray.length; i++) {
							if(graphCellIdArray[i].indexOf('@')!=-1){
								String objectId = graphCellIdArray[i].substring(0, graphCellIdArray[i].indexOf('@'));
								if(StringUtils.endsWith(graphCellIdArray[i], "risk")){
									riskIdList.add(objectId);
								}
							}
						}
					}else if(graphCellId.indexOf('@')!=-1){
						String objectId = graphCellId.substring(0, graphCellId.indexOf('@'));
						if(StringUtils.endsWith(graphCellId, "risk")){
							riskIdList.add(objectId);
						}else if(StringUtils.endsWith(graphCellId, "org")){
							orgIdList.add(objectId);
						}else if(StringUtils.endsWith(graphCellId, "process")){
							processIdList.add(objectId);
						}else if(StringUtils.endsWith(graphCellId, "kpi")){
							kpiIdList.add(objectId);
						}else if(StringUtils.endsWith(graphCellId, "sm")){
							smIdList.add(objectId);
						}else if(StringUtils.endsWith(graphCellId, "sc")){
							scIdList.add(objectId);
						}/*else if(StringUtils.endsWith(graphCellId, "controlMeasure")){
							// 待实现，暂无需求
						}else if(StringUtils.endsWith(graphCellId, "document")){
							// 待实现，暂无需求
						}*/
					}
				}
				//获取对象的最新状态
				List<Map<String, Object>>  riskStatusList = null;
				if(riskIdList.size()>0){
					riskStatusList = o_riskGraphDrawBO.showStatus(riskIdList);
				}
				List<Map<String, Object>>  kpiStatusList = null;
				if(kpiIdList.size()>0){	
					kpiStatusList = o_kpiGraphDrawBO.showStatus(kpiIdList);
				}
				List<Map<String, Object>>  orgStatusList = null;
				if(orgIdList.size()>0){	
					orgStatusList = o_organizationGraphDrawBO.showStatus(orgIdList);
				}
				List<Map<String, Object>>  processStatusList = null;
				if(processIdList.size()>0){	
					processStatusList = o_processGraphDrawBO.showStatus(processIdList);
				}
				List<Map<String, Object>>  smStatusList = null;
				if(smIdList.size()>0){	
					smStatusList = o_smGraphDrawBO.showStatus(smIdList);
				}
				List<Map<String, Object>>  scStatusList = null;
				if(scIdList.size()>0){	
					scStatusList = o_scGraphDrawBO.showStatus(scIdList);
				}
				//更新对象的最新状态
				for (GraphCell graphCell : graphCellList) {
					String graphCellId = graphCell.getId();
					if(graphCellId.indexOf('|')!=-1){//如果是系统元素批量
						String[] graphCellIdArray = graphCellId.split("\\|");
						if(graphCellId.indexOf("@risk")!=-1){
							StringBuffer sb = new StringBuffer();
							sb.append("&lt;table cellpadding=&quot;5&quot; style=&quot;font-size:9pt;border:none;border-collapse:collapse;width:100%;&quot;&gt;&lt;tr&gt;&lt;td colspan=&quot;2&quot; style=&quot;border:1px solid gray;background:#e4e4e4;&quot;&gt;风险&lt;/td&gt;&lt;/tr&gt;");
							for (int i = 0; i < graphCellIdArray.length; i++) {
								if(graphCellIdArray[i].indexOf("@")!=-1){
									String objectId = graphCellIdArray[i].substring(0, graphCellIdArray[i].indexOf('@'));
									if(StringUtils.endsWith(graphCellIdArray[i], "risk")){
										riskIdList.add(objectId);
										for (Map<String, Object> map : riskStatusList) {
											if(objectId.equals(map.get("id"))){
												sb.append("&lt;tr&gt;&lt;td style=&quot;border:1px solid gray;width:15px;&quot;&gt;");
												sb.append("&lt;div style=&quot;display:inline-block;font-size:20pt;text-align:center;color:");
												sb.append(map.get("riskLevel"));
												sb.append(";text-decoration:none&quot;&gt;●&lt;/div&gt;");
												sb.append("&lt;/td&gt;&lt;td style=&quot;border:1px solid gray;&quot;&gt;&lt;a href=&quot;#&quot; onclick=&quot;viewObjectDetail(&#39;");
												sb.append(map.get("id"));
												sb.append("&#39;,&#39;risk&#39;)&quot;&gt;");
												sb.append(map.get("name"));
												sb.append("&lt;/a&gt;&lt;/td&gt;&lt;/tr&gt;");
												break;
											}
										}
									}
								}
							}
							sb.append("&lt;/table&gt;");
							graphCell.setValue(sb.toString());
						}
						
					}else if(StringUtils.endsWith(graphCellId, "risk")){
						this.updatePngAndIconPngStatus(ctx, graphCell, riskStatusList, 
							IGraphDrawBO.RISK_PNG, IGraphDrawBO.RISK_RED_PNG, IGraphDrawBO.RISK_YELLOW_PNG, IGraphDrawBO.RISK_GREEN_PNG, 
							IGraphDrawBO.ICON_RISK_PNG, IGraphDrawBO.ICON_RISK_RED_PNG, IGraphDrawBO.ICON_RISK_YELLOW_PNG, IGraphDrawBO.ICON_RISK_GREEN_PNG);
					}else if(StringUtils.endsWith(graphCellId, "org")){
						this.updatePngStatus(ctx, graphCell, orgStatusList, 
							IGraphDrawBO.ORG_PNG, IGraphDrawBO.ORG_RED_PNG, IGraphDrawBO.ORG_YELLOW_PNG, IGraphDrawBO.ORG_GREEN_PNG);
					}else if(StringUtils.endsWith(graphCellId, "process")){
						this.updatePngStatus(ctx, graphCell, processStatusList, 
							IGraphDrawBO.PROCESS_PNG, IGraphDrawBO.PROCESS_RED_PNG, IGraphDrawBO.PROCESS_YELLOW_PNG, IGraphDrawBO.PROCESS_GREEN_PNG);
					}else if(StringUtils.endsWith(graphCellId, "kpi")){
						this.updatePngAndIconPngStatus(ctx, graphCell, kpiStatusList, 
							IGraphDrawBO.KPI_PNG, IGraphDrawBO.KPI_RED_PNG, IGraphDrawBO.KPI_YELLOW_PNG, IGraphDrawBO.KPI_GREEN_PNG, 
							IGraphDrawBO.ICON_KPI_PNG, IGraphDrawBO.ICON_KPI_RED_PNG, IGraphDrawBO.ICON_KPI_YELLOW_PNG, IGraphDrawBO.ICON_KPI_GREEN_PNG);
					}else if(StringUtils.endsWith(graphCellId, "sm")){
						this.updatePngStatus(ctx, graphCell, smStatusList, 
							IGraphDrawBO.SM_PNG, IGraphDrawBO.SM_RED_PNG, IGraphDrawBO.SM_YELLOW_PNG, IGraphDrawBO.SM_GREEN_PNG);
					}else if(StringUtils.endsWith(graphCellId, "sc")){
						this.updatePngStatus(ctx, graphCell, scStatusList, 
							IGraphDrawBO.SC_PNG, IGraphDrawBO.SC_RED_PNG, IGraphDrawBO.SC_YELLOW_PNG, IGraphDrawBO.SC_GREEN_PNG);
					}/*else if(StringUtils.endsWith(graphCellId, "controlMeasure")){
						// 待实现，暂无需求
					}else if(StringUtils.endsWith(graphCellId, "document")){
						// 待实现，暂无需求
					}*/
				}
				
			    graph.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
			    String graphContent = GraphCellXmlUtils.render(graphCellList, graphUserObjectList);
	            graph.setContent(graphContent.replaceAll("\\n"," ").replaceAll("\"", "\'").getBytes("UTF-8"));
				//保存更新后的图的xml
	            o_graphBO.mergeGraph(graph);
			}
			result = true;
			out.print(result);
		} catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(null != out){
        		out.close();
        	}
        }
	}
	
	/**
	 * <pre>
	 * 更新系统元素和主对象图标的最新状态：亮灯状态和名称
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param ctx request.getContextPath()
	 * @param graphCell 图形元素
	 * @param statusList 对象的最新的状态
	 * @param png 更新后的系统元素的图片路径
	 * @param redPng 更新后的系统元素的红灯的图片路径
	 * @param yellowPng 更新后的系统元素的黄灯的图片路径
	 * @param greenPng 更新后的系统元素的绿灯的图片路径
	 * @param iconPng 更新后的主对象图标的图片路径
	 * @param iconRedPng 更新后的主对象图标的红灯的图片路径
	 * @param iconYellowPng 更新后的主对象图标的黄灯的图片路径
	 * @param iconGreenPng 更新后的主对象图标的绿灯的图片路径
	 * @since  fhd　Ver 1.1
	*/
	private void updatePngAndIconPngStatus(String ctx, GraphCell graphCell, List<Map<String, Object>>  statusList, 
			String png, String redPng, String yellowPng, String greenPng,
			String iconPng, String iconRedPng, String iconYellowPng, String iconGreenPng){
		String graphCellId = graphCell.getId();
		String graphCellValue = graphCell.getValue();
		String objectId = graphCellId.substring(0, graphCellId.indexOf('@'));
		String objectValue = null;
		int i = -1;
		int styleIndex = -1;
		StringBuffer style =new StringBuffer(";");
		if(StringUtils.isNotBlank(graphCellValue)){//
			objectValue = graphCellValue.replaceAll("\\n", "");
			i = graphCellValue.indexOf('\n');//获取值中的换行符的位置
			styleIndex = graphCell.getStyle().indexOf("png;");//获取需要截取的style的样式的开始位置
			if(styleIndex == -1){
				styleIndex = graphCell.getStyle().length();
			}else{
				styleIndex += 4;
			}
			style.append(graphCell.getStyle().substring(styleIndex,graphCell.getStyle().length()));//获取需要截取的样式
		}
		for (Map<String, Object> map : statusList) {
			if(objectId.equals(map.get("id"))){
				StringBuffer pngUrl = new StringBuffer();
				StringBuffer iconPngUrl = new StringBuffer();
				String riskLevel = map.get("riskLevel")!=null ? map.get("riskLevel").toString() : null;
				if(IGraphDrawBO.RISK_LEVEL_GREEN.equals(riskLevel)){
					pngUrl.append("label;image=").append(ctx).append(greenPng).append(style);
					iconPngUrl.append("image;image=").append(ctx).append(iconGreenPng).append(style);
				}else if(IGraphDrawBO.RISK_LEVEL_YELLOW.equals(riskLevel)){
					pngUrl.append("label;image=").append(ctx).append(yellowPng).append(style);
					iconPngUrl.append("image;image=").append(ctx).append(iconYellowPng).append(style);
				}else if(IGraphDrawBO.RISK_LEVEL_RED.equals(riskLevel)){
					pngUrl.append("label;image=").append(ctx).append(redPng).append(style);
					iconPngUrl.append("image;image=").append(ctx).append(iconRedPng).append(style);
				}else{
					pngUrl.append("label;image=").append(ctx).append(png).append(style);
					iconPngUrl.append("image;image=").append(ctx).append(iconPng).append(style);
				}
				if(StringUtils.isNotBlank(objectValue)){
					String name = map.get("name")!=null ? map.get("name").toString() : null;
					if(!objectValue.equals(name)){
						if(StringUtils.isNotBlank(name)){
							StringBuffer tmpName = new StringBuffer();
							if(i!=-1 && i<=name.length()){
								for(int j=0; j<name.length(); j+=i){
									if((j+i)<name.length()){
										tmpName.append(name.substring(j, j+i)).append("&#xa;");
									}else{
										tmpName.append(name.substring(j, name.length()));
									}
								}
							}else{
								tmpName.append(name);
							}
							graphCell.setValue(tmpName.toString());
						}
					}else{
						graphCell.setValue(graphCellValue.replaceAll("\\n", "&#xa;"));
					}
					graphCell.setStyle(pngUrl.toString());
				}else{
					graphCell.setStyle(iconPngUrl.toString());
				}
			}
		}
	}
	
	/**
	 * <pre>
	 * 更新系统元素的最新状态：亮灯状态和名称
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param ctx request.getContextPath()
	 * @param graphCell 图形元素
	 * @param statusList 对象的最新的状态
	 * @param png 更新后的系统元素的图片路径
	 * @param redPng 更新后的系统元素的红灯的图片路径
	 * @param yellowPng 更新后的系统元素的黄灯的图片路径
	 * @param greenPng 更新后的系统元素的绿灯的图片路径
	 * @since  fhd　Ver 1.1
	*/
	private void updatePngStatus(String ctx, GraphCell graphCell, List<Map<String, Object>>  statusList,
			String png, String redPng, String yellowPng, String greenPng){
		String graphCellId = graphCell.getId();
		String graphCellValue = graphCell.getValue();
		String objectId = graphCellId.substring(0, graphCellId.indexOf('@'));
		String objectValue = null;
		int i = -1;
		int styleIndex = -1;
		StringBuffer style =new StringBuffer(";");
		if(StringUtils.isNotBlank(graphCellValue)){//
			objectValue = graphCellValue.replaceAll("\\n", "");
			i = graphCellValue.indexOf('\n');//获取值中的换行符的位置
			styleIndex = graphCell.getStyle().indexOf("png;");//获取需要截取的style的样式的开始位置
			if(styleIndex == -1){
				styleIndex = graphCell.getStyle().length();
			}else{
				styleIndex += 4;
			}
			style.append(graphCell.getStyle().substring(styleIndex,graphCell.getStyle().length()));//获取需要截取的样式
		}
		for (Map<String, Object> map : statusList) {
			if(objectId.equals(map.get("id"))){
				StringBuffer pngUrl = new StringBuffer();
				String riskLevel = map.get("riskLevel")!=null ? map.get("riskLevel").toString() : null;
				if(IGraphDrawBO.RISK_LEVEL_GREEN.equals(riskLevel)){
					pngUrl.append("label;image=").append(ctx).append(greenPng).append(style);
				}else if(IGraphDrawBO.RISK_LEVEL_YELLOW.equals(riskLevel)){
					pngUrl.append("label;image=").append(ctx).append(yellowPng).append(style);
				}else if(IGraphDrawBO.RISK_LEVEL_RED.equals(riskLevel)){
					pngUrl.append("label;image=").append(ctx).append(redPng).append(style);
				}else{
					pngUrl.append("label;image=").append(ctx).append(png).append(style);
				}
				if(StringUtils.isNotBlank(objectValue)){
					String name = map.get("name")!=null ? map.get("name").toString() : null;
					if(!objectValue.equals(name)){
						if(StringUtils.isNotBlank(name)){
							StringBuffer tmpName = new StringBuffer();
							if(i!=-1 && i<=name.length()){
								for(int j=0; j<name.length(); j+=i){
									if((j+i)<name.length()){
										tmpName.append(name.substring(j, j+i)).append("&#xa;");
									}else{
										tmpName.append(name.substring(j, name.length()));
									}
								}
							}else{
								tmpName.append(name);
							}
							graphCell.setValue(tmpName.toString());
						}
					}else{
						graphCell.setValue(graphCellValue.replaceAll("\\n", "&#xa;"));
					}
					graphCell.setStyle(pngUrl.toString());
				}
			}
		}
	}
	
}

