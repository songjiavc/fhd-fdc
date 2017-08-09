/**
 * GraphRelaStrategyMapControl.java
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
import com.fhd.comm.business.graph.GraphRelaStrategyMapBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.graph.Graph;
import com.fhd.entity.comm.graph.GraphRelaStrategyMap;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.StrategyMapBO;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Controller
public class GraphRelaStrategyMapControl {

	@Autowired
	private GraphRelaStrategyMapBO o_graphRelaStrategyMapBO;
	
	@Autowired
	private GraphBO o_graphBO;
	
	@Autowired
	private StrategyMapBO o_strategyMapBO;
	
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
	@RequestMapping(value = "/comm/graph/saveGraphRelaStrategyMap.f")
	public void saveGraphRelaStrategyMap(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            Graph graph = new Graph();
            String strategyMapId = request.getParameter("strategyMapId");//针对不同对象传不同对象的ID
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
				GraphRelaStrategyMap graphRelaStrategyMap = new GraphRelaStrategyMap();//针对不同对象保存不同对象的关联关系
				graphRelaStrategyMap.setGraph(new Graph(graphId));
				graphRelaStrategyMap.setStrategyMap(new StrategyMap(strategyMapId));
				graphRelaStrategyMap.setType(Contents.GRAPH_TYPE_CREATE);
				o_graphRelaStrategyMapBO.saveGraphRelaStrategyMap(graphRelaStrategyMap);
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
	 * @param strategyMapId 目标ID
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/mergeGraphRelaStrategyMap.f")
	public void mergeGraphRelaStrategyMap(String graphId,String strategyMapId,HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
			List<GraphRelaStrategyMap> graphRelaStrategyMapList = null;//针对不同对象保存不同对象的关联关系
			if(StringUtils.isNotBlank(graphId) && StringUtils.isNotBlank(strategyMapId)){
				graphRelaStrategyMapList = o_graphRelaStrategyMapBO.findGraphRelaStrategyMapListBySome(graphId, strategyMapId, Contents.GRAPH_TYPE_TRANSFER);
			}
			if(!(null != graphRelaStrategyMapList && graphRelaStrategyMapList.size()>0)){
				GraphRelaStrategyMap graphRelaStrategyMap = new GraphRelaStrategyMap();
				graphRelaStrategyMap.setGraph(new Graph(graphId));
				graphRelaStrategyMap.setStrategyMap(new StrategyMap(strategyMapId));
				graphRelaStrategyMap.setType(Contents.GRAPH_TYPE_TRANSFER);
				o_graphRelaStrategyMapBO.saveGraphRelaStrategyMap(graphRelaStrategyMap);
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
	@RequestMapping(value = "/comm/graph/removeGraphRelaStrategyMap.f")
	public void removeGraphRelaStrategyMap(String graphRelaStrategyMapId, HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		boolean flag = false;
        try {
        	out = response.getWriter();
        	if(StringUtils.isNotBlank(graphRelaStrategyMapId)){
        		o_graphRelaStrategyMapBO.removeGraphRelaStrategyMapById(graphRelaStrategyMapId);
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
	 * 查询目标ID为strategyMapId的目标关联的图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param strategyMapId 目标ID
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "/comm/graph/findGraphRelaStrategyMapList.f")
	public Map<String,Object> findGraphRelaStrategyMapList(String strategyMapId,HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(strategyMapId!=null && strategyMapId.length()>0){
			request.setAttribute("strategyMapId", strategyMapId);//针对不同对象传不同对象的ID
			List<GraphRelaStrategyMap> graphRelaStrategyMapList = o_graphRelaStrategyMapBO.findGraphRelaStrategyMapListBySome(null, strategyMapId, null);//针对不同对象查询不同对象的关联关系
			for (GraphRelaStrategyMap graphRelaStrategyMap : graphRelaStrategyMapList) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("graphRelaStrategyMapId", graphRelaStrategyMap.getId());//针对不同对象的关联关系传不同的关联关系的ID
				dataMap.put("graphId", graphRelaStrategyMap.getGraph().getId());//程序约定的
				dataMap.put("name", graphRelaStrategyMap.getGraph().getName());//程序约定的
				dataMap.put("type", graphRelaStrategyMap.getType());//程序约定的
				String updateDate = "";
				if(null != graphRelaStrategyMap.getGraph().getLastModifyTime()){
					updateDate = DateUtils.formatDate(
						DateUtils.parseTimeStamp(
							String.valueOf(graphRelaStrategyMap.getGraph().getLastModifyTime()).substring(
								0,String.valueOf(graphRelaStrategyMap.getGraph().getLastModifyTime()).lastIndexOf("."))), "yyyy-MM-dd");
				}else{
					if(null != graphRelaStrategyMap.getGraph().getCreateTime()){
						updateDate = DateUtils.formatDate(DateUtils.parseTimeStamp(
							String.valueOf(graphRelaStrategyMap.getGraph().getCreateTime()).substring(
								0,String.valueOf(graphRelaStrategyMap.getGraph().getCreateTime()).lastIndexOf("."))), "yyyy-MM-dd");
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
	 * 1.查询某个目标关联的所有图形中的一个图形，strategyMapId必传
	 * 2.查询某个目标关联图形的关联关系下的一个图形，graphRelaStrategyMapId必传
	 * 3.新建某个目标关联下的一个图形，isNew，strategyMapId必传
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param panelId 
	 * @param graphRelaStrategyMapId 目标关联图形的关联关系的ID
	 * @param strategyMapId 目标ID
	 * @param viewType 查看状态
	 * @param isNew 是否新建新的图形
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/findGraphRelaStrategyMap.f")
	public String findGraphRelaStrategyMap(String panelId, String graphRelaStrategyMapId,String strategyMapId,String viewType,String isNew,
			HttpServletRequest request) throws Exception{
		request.setAttribute("panelId", panelId);//程序约定的
		GraphRelaStrategyMap graphRelaStrategyMap = null;
		if(StringUtils.isNotBlank(graphRelaStrategyMapId)){
			request.setAttribute("graphRelaStrategyMapId", graphRelaStrategyMapId);
			graphRelaStrategyMap = o_graphRelaStrategyMapBO.findGraphRelaStrategyMapById(graphRelaStrategyMapId);
		}else if(StringUtils.isNotBlank(strategyMapId)){
			request.setAttribute("strategyMapId", strategyMapId);
			//如果可修改则传viewType的值为Contents.GRAPH_TYPE_CREATE
			if(StringUtils.isNotBlank(isNew) && isNew.equals("true")){
				viewType = Contents.GRAPH_TYPE_CREATE;
				request.setAttribute("graphName", o_strategyMapBO.findById(strategyMapId).getName()+"-");//程序约定的
			}else{
				List<GraphRelaStrategyMap> graphRelaStrategyMapList = o_graphRelaStrategyMapBO.findGraphRelaStrategyMapListBySome(null, strategyMapId, null);
				if(graphRelaStrategyMapList!=null && graphRelaStrategyMapList.size()>0){
					graphRelaStrategyMap = graphRelaStrategyMapList.get(0);
				}
			}
			
		}
		if(graphRelaStrategyMap!=null){
			String graphContext = IOUtils.toString(new ByteArrayInputStream(graphRelaStrategyMap.getGraph().getContent()),"UTF-8");
			request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));//程序约定的
			request.setAttribute("graphId", graphRelaStrategyMap.getGraph().getId());//程序约定的
			request.setAttribute("graphName", graphRelaStrategyMap.getGraph().getName());//程序约定的
			request.setAttribute("strategyMapId", graphRelaStrategyMap.getStrategyMap().getId());
			//如果viewType为可修改状态，要判断是否与图形允许操作相违背，如果违背，以图形允许的操作为准
			if(StringUtils.isNotBlank(viewType) && viewType.equals(Contents.GRAPH_TYPE_CREATE)){
				if(!graphRelaStrategyMap.getType().equals(viewType)){
					viewType = graphRelaStrategyMap.getType();
				}
				request.setAttribute("type", viewType);//程序约定的
			}else{
				request.setAttribute("type", graphRelaStrategyMap.getType());//程序约定的
			}
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){//程序约定的
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphdrawview";//程序约定的
		}
	}

}

