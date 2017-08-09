/**
 * GraphRelaProcessControl.java
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
import com.fhd.comm.business.graph.GraphRelaProcessBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.graph.Graph;
import com.fhd.entity.comm.graph.GraphRelaProcess;
import com.fhd.entity.process.Process;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessBO;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Controller
public class GraphRelaProcessControl {

	@Autowired
	private GraphRelaProcessBO o_graphRelaProcessBO;
	
	@Autowired
	private GraphBO o_graphBO;
	
	@Autowired
	private ProcessBO o_processBO;
	
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
	@RequestMapping(value = "/comm/graph/saveGraphRelaProcess.f")
	public void saveGraphRelaProcess(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            Graph graph = new Graph();
            String processId = request.getParameter("processId");//针对不同对象传不同对象的ID
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
				GraphRelaProcess graphRelaProcess = new GraphRelaProcess();//针对不同对象保存不同对象的关联关系
				graphRelaProcess.setGraph(new Graph(graphId));
				graphRelaProcess.setProcess(new Process(processId));
				graphRelaProcess.setType(Contents.GRAPH_TYPE_CREATE);
				o_graphRelaProcessBO.saveGraphRelaProcess(graphRelaProcess);
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
	 * @param processId 流程ID
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/mergeGraphRelaProcess.f")
	public void mergeGraphRelaProcess(String graphId,String processId,HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
			List<GraphRelaProcess> graphRelaProcessList = null;//针对不同对象保存不同对象的关联关系
			if(StringUtils.isNotBlank(graphId) && StringUtils.isNotBlank(processId)){
				graphRelaProcessList = o_graphRelaProcessBO.findGraphRelaProcessListBySome(graphId, processId, Contents.GRAPH_TYPE_TRANSFER);
			}
			if(!(null != graphRelaProcessList && graphRelaProcessList.size()>0)){
				GraphRelaProcess graphRelaProcess = new GraphRelaProcess();
				graphRelaProcess.setGraph(new Graph(graphId));
				graphRelaProcess.setProcess(new Process(processId));
				graphRelaProcess.setType(Contents.GRAPH_TYPE_TRANSFER);
				o_graphRelaProcessBO.saveGraphRelaProcess(graphRelaProcess);
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
	@RequestMapping(value = "/comm/graph/removeGraphRelaProcess.f")
	public void removeGraphRelaProcess(String graphRelaProcessId, HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		boolean flag = false;
        try {
        	out = response.getWriter();
        	if(StringUtils.isNotBlank(graphRelaProcessId)){
        		o_graphRelaProcessBO.removeGraphRelaProcessById(graphRelaProcessId);
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
	 * 查询流程ID为processId的流程关联的图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param processId 流程ID
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "/comm/graph/findGraphRelaProcessList.f")
	public Map<String,Object> findGraphRelaProcessList(String processId,HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(processId!=null && processId.length()>0){
			request.setAttribute("processId", processId);//针对不同对象传不同对象的ID
			List<GraphRelaProcess> graphRelaProcessList = o_graphRelaProcessBO.findGraphRelaProcessListBySome(null, processId, null);//针对不同对象查询不同对象的关联关系
			for (GraphRelaProcess graphRelaProcess : graphRelaProcessList) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("graphRelaProcessId", graphRelaProcess.getId());//针对不同对象的关联关系传不同的关联关系的ID
				dataMap.put("graphId", graphRelaProcess.getGraph().getId());//程序约定的
				dataMap.put("name", graphRelaProcess.getGraph().getName());//程序约定的
				dataMap.put("type", graphRelaProcess.getType());//程序约定的
				String updateDate = "";
				if(null != graphRelaProcess.getGraph().getLastModifyTime()){
					updateDate = DateUtils.formatDate(
						DateUtils.parseTimeStamp(
							String.valueOf(graphRelaProcess.getGraph().getLastModifyTime()).substring(
								0,String.valueOf(graphRelaProcess.getGraph().getLastModifyTime()).lastIndexOf("."))), "yyyy-MM-dd");
				}else{
					if(null != graphRelaProcess.getGraph().getCreateTime()){
						updateDate = DateUtils.formatDate(DateUtils.parseTimeStamp(
							String.valueOf(graphRelaProcess.getGraph().getCreateTime()).substring(
								0,String.valueOf(graphRelaProcess.getGraph().getCreateTime()).lastIndexOf("."))), "yyyy-MM-dd");
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
	 * 1.查询某个流程关联的所有图形中的一个图形，processId必传
	 * 2.查询某个流程关联图形的关联关系下的一个图形，graphRelaProcessId必传
	 * 3.新建某个流程关联下的一个图形，isNew，processId必传
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param panelId 
	 * @param graphRelaProcessId 流程关联图形的关联关系的ID
	 * @param processId 流程ID
	 * @param viewType 查看状态
	 * @param isNew 是否新建新的图形
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/findGraphRelaProcess.f")
	public String findGraphRelaProcess(String panelId, String graphRelaProcessId,String processId,String viewType,String isNew,
			HttpServletRequest request) throws Exception{
		request.setAttribute("panelId", panelId);//程序约定的
		GraphRelaProcess graphRelaProcess = null;
		if(StringUtils.isNotBlank(graphRelaProcessId)){
			request.setAttribute("graphRelaProcessId", graphRelaProcessId);
			graphRelaProcess = o_graphRelaProcessBO.findGraphRelaProcessById(graphRelaProcessId);
		}else if(StringUtils.isNotBlank(processId)){
			request.setAttribute("processId", processId);
			//如果可修改则传viewType的值为Contents.GRAPH_TYPE_CREATE
			if(StringUtils.isNotBlank(isNew) && isNew.equals("true")){
				viewType = Contents.GRAPH_TYPE_CREATE;
				request.setAttribute("graphName", o_processBO.findProcessById(processId).getName()+"-");//程序约定的
			}else{
				List<GraphRelaProcess> graphRelaProcessList = o_graphRelaProcessBO.findGraphRelaProcessListBySome(null, processId, null);
				if(graphRelaProcessList!=null && graphRelaProcessList.size()>0){
					graphRelaProcess = graphRelaProcessList.get(0);
				}
			}
			
		}
		if(graphRelaProcess!=null){
			String graphContext = IOUtils.toString(new ByteArrayInputStream(graphRelaProcess.getGraph().getContent()),"UTF-8");
			request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));//程序约定的
			request.setAttribute("graphId", graphRelaProcess.getGraph().getId());//程序约定的
			request.setAttribute("graphName", graphRelaProcess.getGraph().getName());//程序约定的
			request.setAttribute("processId", graphRelaProcess.getProcess().getId());
			//如果viewType为可修改状态，要判断是否与图形允许操作相违背，如果违背，以图形允许的操作为准
			if(StringUtils.isNotBlank(viewType) && viewType.equals(Contents.GRAPH_TYPE_CREATE)){
				if(!graphRelaProcess.getType().equals(viewType)){
					viewType = graphRelaProcess.getType();
				}
				request.setAttribute("type", viewType);//程序约定的
			}else{
				request.setAttribute("type", graphRelaProcess.getType());//程序约定的
			}
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){//程序约定的
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphdrawview";//程序约定的
		}
	}
	
	
}

