/**
 * GraphRelaKpiControl.java
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
import com.fhd.comm.business.graph.GraphRelaKpiBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.graph.Graph;
import com.fhd.entity.comm.graph.GraphRelaKpi;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.KpiBO;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Controller
public class GraphRelaKpiControl {

	@Autowired
	private GraphRelaKpiBO o_graphRelaKpiBO;
	
	@Autowired
	private GraphBO o_graphBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
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
	@RequestMapping(value = "/comm/graph/saveGraphRelaKpi.f")
	public void saveGraphRelaKpi(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            Graph graph = new Graph();
            String kpiId = request.getParameter("kpiId");//针对不同对象传不同对象的ID
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
				GraphRelaKpi graphRelaKpi = new GraphRelaKpi();//针对不同对象保存不同对象的关联关系
				graphRelaKpi.setGraph(new Graph(graphId));
				graphRelaKpi.setKpi(new Kpi(kpiId));
				graphRelaKpi.setType(Contents.GRAPH_TYPE_CREATE);
				o_graphRelaKpiBO.saveGraphRelaKpi(graphRelaKpi);
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
	 * @param kpiId 指标ID
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/mergeGraphRelaKpi.f")
	public void mergeGraphRelaKpi(String graphId,String kpiId,HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
			List<GraphRelaKpi> graphRelaKpiList = null;//针对不同对象保存不同对象的关联关系
			if(StringUtils.isNotBlank(graphId) && StringUtils.isNotBlank(kpiId)){
				graphRelaKpiList = o_graphRelaKpiBO.findGraphRelaKpiListBySome(graphId, kpiId, Contents.GRAPH_TYPE_TRANSFER);
			}
			if(!(null != graphRelaKpiList && graphRelaKpiList.size()>0)){
				GraphRelaKpi graphRelaKpi = new GraphRelaKpi();
				graphRelaKpi.setGraph(new Graph(graphId));
				graphRelaKpi.setKpi(new Kpi(kpiId));
				graphRelaKpi.setType(Contents.GRAPH_TYPE_TRANSFER);
				o_graphRelaKpiBO.saveGraphRelaKpi(graphRelaKpi);
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
	@RequestMapping(value = "/comm/graph/removeGraphRelaKpi.f")
	public void removeGraphRelaKpi(String graphRelaKpiId, HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		boolean flag = false;
        try {
        	out = response.getWriter();
        	if(StringUtils.isNotBlank(graphRelaKpiId)){
        		o_graphRelaKpiBO.removeGraphRelaKpiById(graphRelaKpiId);
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
	 * 查询指标ID为kpiId的指标关联的图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param kpiId 指标ID
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "/comm/graph/findGraphRelaKpiList.f")
	public Map<String,Object> findGraphRelaKpiList(String kpiId,HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(kpiId!=null && kpiId.length()>0){
			request.setAttribute("kpiId", kpiId);//针对不同对象传不同对象的ID
			List<GraphRelaKpi> graphRelaKpiList = o_graphRelaKpiBO.findGraphRelaKpiListBySome(null, kpiId, null);//针对不同对象查询不同对象的关联关系
			for (GraphRelaKpi graphRelaKpi : graphRelaKpiList) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("graphRelaKpiId", graphRelaKpi.getId());//针对不同对象的关联关系传不同的关联关系的ID
				dataMap.put("graphId", graphRelaKpi.getGraph().getId());//程序约定的
				dataMap.put("name", graphRelaKpi.getGraph().getName());//程序约定的
				dataMap.put("type", graphRelaKpi.getType());//程序约定的
				String updateDate = "";
				if(null != graphRelaKpi.getGraph().getLastModifyTime()){
					updateDate = DateUtils.formatDate(
						DateUtils.parseTimeStamp(
							String.valueOf(graphRelaKpi.getGraph().getLastModifyTime()).substring(
								0,String.valueOf(graphRelaKpi.getGraph().getLastModifyTime()).lastIndexOf("."))), "yyyy-MM-dd");
				}else{
					if(null != graphRelaKpi.getGraph().getCreateTime()){
						updateDate = DateUtils.formatDate(DateUtils.parseTimeStamp(
							String.valueOf(graphRelaKpi.getGraph().getCreateTime()).substring(
								0,String.valueOf(graphRelaKpi.getGraph().getCreateTime()).lastIndexOf("."))), "yyyy-MM-dd");
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
	 * 1.查询某个指标关联的所有图形中的一个图形，kpiId必传
	 * 2.查询某个指标关联图形的关联关系下的一个图形，graphRelaKpiId必传
	 * 3.新建某个指标关联下的一个图形，isNew，kpiId必传
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param panelId 
	 * @param graphRelaKpiId 指标关联图形的关联关系的ID
	 * @param kpiId 指标ID
	 * @param viewType 查看状态
	 * @param isNew 是否新建新的图形
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/findGraphRelaKpi.f")
	public String findGraphRelaKpi(String panelId, String graphRelaKpiId,String kpiId,String viewType,String isNew,
			HttpServletRequest request) throws Exception{
		request.setAttribute("panelId", panelId);//程序约定的
		GraphRelaKpi graphRelaKpi = null;
		if(StringUtils.isNotBlank(graphRelaKpiId)){
			request.setAttribute("graphRelaKpiId", graphRelaKpiId);
			graphRelaKpi = o_graphRelaKpiBO.findGraphRelaKpiById(graphRelaKpiId);
		}else if(StringUtils.isNotBlank(kpiId)){
			request.setAttribute("kpiId", kpiId);
			//如果可修改则传viewType的值为Contents.GRAPH_TYPE_CREATE
			if(StringUtils.isNotBlank(isNew) && isNew.equals("true")){
				viewType = Contents.GRAPH_TYPE_CREATE;
				request.setAttribute("graphName", o_kpiBO.findKpiById(kpiId).getName()+"-");//程序约定的
			}else{
				List<GraphRelaKpi> graphRelaKpiList = o_graphRelaKpiBO.findGraphRelaKpiListBySome(null, kpiId, null);
				if(graphRelaKpiList!=null && graphRelaKpiList.size()>0){
					graphRelaKpi = graphRelaKpiList.get(0);
				}
			}
			
		}
		if(graphRelaKpi!=null){
			String graphContext = IOUtils.toString(new ByteArrayInputStream(graphRelaKpi.getGraph().getContent()),"UTF-8");
			request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));//程序约定的
			request.setAttribute("graphId", graphRelaKpi.getGraph().getId());//程序约定的
			request.setAttribute("graphName", graphRelaKpi.getGraph().getName());//程序约定的
			request.setAttribute("kpiId", graphRelaKpi.getKpi().getId());
			//如果viewType为可修改状态，要判断是否与图形允许操作相违背，如果违背，以图形允许的操作为准
			if(StringUtils.isNotBlank(viewType) && viewType.equals(Contents.GRAPH_TYPE_CREATE)){
				if(!graphRelaKpi.getType().equals(viewType)){
					viewType = graphRelaKpi.getType();
				}
				request.setAttribute("type", viewType);//程序约定的
			}else{
				request.setAttribute("type", graphRelaKpi.getType());//程序约定的
			}
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){//程序约定的
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphdrawview";//程序约定的
		}
	}
	
	
}

