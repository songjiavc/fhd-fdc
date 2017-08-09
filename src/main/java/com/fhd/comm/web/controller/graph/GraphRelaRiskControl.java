/**
 * GraphRelaRiskControl.java
 * com.fhd.comm.web.controller.graph
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-10-28 		张 雷
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.comm.web.controller.graph;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
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
import com.fhd.comm.business.graph.GraphRelaRiskBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.graph.Graph;
import com.fhd.entity.comm.graph.GraphRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.risk.RiskBO;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Controller
public class GraphRelaRiskControl {

	@Autowired
	private GraphRelaRiskBO o_graphRelaRiskBO;
	
	@Autowired
	private GraphBO o_graphBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	/**
	 * <pre>
	 * 新增图形，保存操作
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/saveGraphRelaRisk.f")
	public void saveGraphRelaRisk(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            Graph graph = new Graph();
            String riskId = request.getParameter("riskId");//针对不同对象传不同对象的ID
            String graphId = request.getParameter("graphId");//程序约定的
            String graphName = request.getParameter("graphName");//程序约定的
            String graphContext = request.getParameter("graphContext");//程序约定的
            if(StringUtils.isNotBlank(graphId)){
				graph = o_graphBO.findGraphById(graphId);
			}
            graph.setName(graphName);
            graph.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
            graph.setContent(java.net.URLDecoder.decode(graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"),"UTF-8").getBytes("UTF-8"));
            
            if(StringUtils.isNotBlank(graphId)){
            	o_graphBO.mergeGraph(graph);
			}else{
				graphId = o_graphBO.saveGraph(graph);
				GraphRelaRisk graphRelaRisk = new GraphRelaRisk();//针对不同对象保存不同对象的关联关系
				graphRelaRisk.setGraph(new Graph(graphId));
				graphRelaRisk.setRisk(new Risk(riskId));
				graphRelaRisk.setType(Contents.GRAPH_TYPE_CREATE);
				o_graphRelaRiskBO.saveGraphRelaRisk(graphRelaRisk);
			}
			flag = true;
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
	 * 新增引用关系，保存操作
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图形ID
	 * @param riskId 风险ID
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/mergeGraphRelaRisk.f")
	public void mergeGraphRelaRisk(String graphId,String riskId,HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
			List<GraphRelaRisk> graphRelaRiskList = null;//针对不同对象保存不同对象的关联关系
			if(StringUtils.isNotBlank(graphId) && StringUtils.isNotBlank(riskId)){
				graphRelaRiskList = o_graphRelaRiskBO.findGraphRelaRiskListBySome(graphId, riskId, Contents.GRAPH_TYPE_TRANSFER);
			}
			if(!(null != graphRelaRiskList && graphRelaRiskList.size()>0)){
				GraphRelaRisk graphRelaRisk = new GraphRelaRisk();
				graphRelaRisk.setGraph(new Graph(graphId));
				graphRelaRisk.setRisk(new Risk(riskId));
				graphRelaRisk.setType(Contents.GRAPH_TYPE_TRANSFER);
				o_graphRelaRiskBO.saveGraphRelaRisk(graphRelaRisk);
			}
			flag = true;
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
	 * 删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/removeGraphRelaRisk.f")
	public void removeGraphRelaRisk(String graphRelaRiskId, HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		boolean flag = false;
        try {
        	out = response.getWriter();
        	if(StringUtils.isNotBlank(graphRelaRiskId)){
        		o_graphRelaRiskBO.removeGraphRelaRiskById(graphRelaRiskId);
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
	 * 查询风险ID为riskId的风险关联的图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param riskId 风险ID
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "/comm/graph/findGraphRelaRiskList.f")
	public Map<String,Object> findGraphRelaRiskList(String riskId,HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(riskId!=null && riskId.length()>0){
			request.setAttribute("riskId", riskId);//针对不同对象传不同对象的ID
			List<GraphRelaRisk> graphRelaRiskList = o_graphRelaRiskBO.findGraphRelaRiskListBySome(null, riskId, null);//针对不同对象查询不同对象的关联关系
			for (GraphRelaRisk graphRelaRisk : graphRelaRiskList) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("graphRelaRiskId", graphRelaRisk.getId());//针对不同对象的关联关系传不同的关联关系的ID
				dataMap.put("graphId", graphRelaRisk.getGraph().getId());//程序约定的
				dataMap.put("name", graphRelaRisk.getGraph().getName());//程序约定的
				dataMap.put("type", graphRelaRisk.getType());//程序约定的
				String updateDate = "";
				if(null != graphRelaRisk.getGraph().getLastModifyTime()){
					updateDate = DateUtils.formatDate(
						DateUtils.parseTimeStamp(
							String.valueOf(graphRelaRisk.getGraph().getLastModifyTime()).substring(
								0,String.valueOf(graphRelaRisk.getGraph().getLastModifyTime()).lastIndexOf("."))), "yyyy-MM-dd");
				}else{
					if(null != graphRelaRisk.getGraph().getCreateTime()){
						updateDate = DateUtils.formatDate(DateUtils.parseTimeStamp(
							String.valueOf(graphRelaRisk.getGraph().getCreateTime()).substring(
								0,String.valueOf(graphRelaRisk.getGraph().getCreateTime()).lastIndexOf("."))), "yyyy-MM-dd");
					}
				}
				dataMap.put("updateDate", updateDate);
				datas.add(dataMap);
			}
		}
		map.put("datas", datas);
		map.put("totalCount", datas.size());
		return map;
	}
	
	/**
	 * <pre>
	 * 1.查询某个风险关联的所有图形中的一个图形，riskId必传
	 * 2.查询某个风险关联图形的关联关系下的一个图形，graphRelaRiskId必传
	 * 3.新建某个风险关联下的一个图形，isNew，riskId必传
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param panelId 
	 * @param graphRelaRiskId 风险关联图形的关联关系的ID
	 * @param riskId 风险ID
	 * @param viewType 查看状态
	 * @param isNew 是否新建新的图形
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/findGraphRelaRisk.f")
	public String findGraphRelaRisk(String panelId, String graphRelaRiskId,String riskId,String viewType,String isNew,
			HttpServletRequest request) throws Exception{
		request.setAttribute("panelId", panelId);//程序约定的
		GraphRelaRisk graphRelaRisk = null;
		if(StringUtils.isNotBlank(graphRelaRiskId)){
			request.setAttribute("graphRelaRiskId", graphRelaRiskId);
			graphRelaRisk = o_graphRelaRiskBO.findGraphRelaRiskById(graphRelaRiskId);
		}else if(StringUtils.isNotBlank(riskId)){
			request.setAttribute("riskId", riskId);
			//如果可修改则传viewType的值为Contents.GRAPH_TYPE_CREATE
			if(StringUtils.isNotBlank(isNew) && isNew.equals("true")){
				viewType = Contents.GRAPH_TYPE_CREATE;
				request.setAttribute("graphName", o_riskBO.findRiskById(riskId).getName()+"-");//程序约定的
			}else{
				List<GraphRelaRisk> graphRelaRiskList = o_graphRelaRiskBO.findGraphRelaRiskListBySome(null, riskId, null);
				if(graphRelaRiskList!=null && graphRelaRiskList.size()>0){
					graphRelaRisk = graphRelaRiskList.get(0);
				}
			}
			
		}
		if(graphRelaRisk!=null){
			String graphContext = IOUtils.toString(new ByteArrayInputStream(graphRelaRisk.getGraph().getContent()),"UTF-8");
			request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));//程序约定的
			request.setAttribute("graphId", graphRelaRisk.getGraph().getId());//程序约定的
			request.setAttribute("graphName", graphRelaRisk.getGraph().getName());//程序约定的
			request.setAttribute("riskId", graphRelaRisk.getRisk().getId());
			//如果viewType为可修改状态，要判断是否与图形允许操作相违背，如果违背，以图形允许的操作为准
			if(StringUtils.isNotBlank(viewType) && viewType.equals(Contents.GRAPH_TYPE_CREATE)){
				if(!graphRelaRisk.getType().equals(viewType)){
					viewType = graphRelaRisk.getType();
				}
				request.setAttribute("type", viewType);//程序约定的
			}else{
				request.setAttribute("type", graphRelaRisk.getType());//程序约定的
			}
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){//程序约定的
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphdrawview";//程序约定的
		}
	}
	
	
}

