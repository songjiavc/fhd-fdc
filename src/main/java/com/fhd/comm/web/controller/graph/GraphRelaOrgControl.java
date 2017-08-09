/**
 * GraphRelaOrgControl.java
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
import com.fhd.comm.business.graph.GraphRelaOrgBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.graph.Graph;
import com.fhd.entity.comm.graph.GraphRelaOrg;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.orgstructure.OrganizationBO;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Controller
public class GraphRelaOrgControl {

	@Autowired
	private GraphRelaOrgBO o_graphRelaOrgBO;
	
	@Autowired
	private GraphBO o_graphBO;
	
	@Autowired
	private OrganizationBO o_organizationBO;
	
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
	@RequestMapping(value = "/comm/graph/saveGraphRelaOrg.f")
	public void saveGraphRelaOrg(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            Graph graph = new Graph();
            String orgId = request.getParameter("orgId");//针对不同对象传不同对象的ID
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
				GraphRelaOrg graphRelaOrg = new GraphRelaOrg();//针对不同对象保存不同对象的关联关系
				graphRelaOrg.setGraph(new Graph(graphId));
				graphRelaOrg.setOrg(new SysOrganization(orgId));
				graphRelaOrg.setType(Contents.GRAPH_TYPE_CREATE);
				o_graphRelaOrgBO.saveGraphRelaOrg(graphRelaOrg);
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
	 * @param orgId 组织ID
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/mergeGraphRelaOrg.f")
	public void mergeGraphRelaOrg(String graphId,String orgId,HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
			List<GraphRelaOrg> graphRelaOrgList = null;//针对不同对象保存不同对象的关联关系
			if(StringUtils.isNotBlank(graphId) && StringUtils.isNotBlank(orgId)){
				graphRelaOrgList = o_graphRelaOrgBO.findGraphRelaOrgListBySome(graphId, orgId, Contents.GRAPH_TYPE_TRANSFER);
			}
			if(!(null != graphRelaOrgList && graphRelaOrgList.size()>0)){
				GraphRelaOrg graphRelaOrg = new GraphRelaOrg();
				graphRelaOrg.setGraph(new Graph(graphId));
				graphRelaOrg.setOrg(new SysOrganization(orgId));
				graphRelaOrg.setType(Contents.GRAPH_TYPE_TRANSFER);
				o_graphRelaOrgBO.saveGraphRelaOrg(graphRelaOrg);
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
	@RequestMapping(value = "/comm/graph/removeGraphRelaOrg.f")
	public void removeGraphRelaOrg(String graphRelaOrgId, HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		boolean flag = false;
        try {
        	out = response.getWriter();
        	if(StringUtils.isNotBlank(graphRelaOrgId)){
        		o_graphRelaOrgBO.removeGraphRelaOrgById(graphRelaOrgId);
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
	 * 查询组织ID为orgId的组织关联的图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 组织ID
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "/comm/graph/findGraphRelaOrgList.f")
	public Map<String,Object> findGraphRelaOrgList(String orgId,HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(orgId!=null && orgId.length()>0){
			request.setAttribute("orgId", orgId);//针对不同对象传不同对象的ID
			List<GraphRelaOrg> graphRelaOrgList = o_graphRelaOrgBO.findGraphRelaOrgListBySome(null, orgId, null);//针对不同对象查询不同对象的关联关系
			for (GraphRelaOrg graphRelaOrg : graphRelaOrgList) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("graphRelaOrgId", graphRelaOrg.getId());//针对不同对象的关联关系传不同的关联关系的ID
				dataMap.put("graphId", graphRelaOrg.getGraph().getId());//程序约定的
				dataMap.put("name", graphRelaOrg.getGraph().getName());//程序约定的
				dataMap.put("type", graphRelaOrg.getType());//程序约定的
				String updateDate = "";
				if(null != graphRelaOrg.getGraph().getLastModifyTime()){
					updateDate = DateUtils.formatDate(
						DateUtils.parseTimeStamp(
							String.valueOf(graphRelaOrg.getGraph().getLastModifyTime()).substring(
								0,String.valueOf(graphRelaOrg.getGraph().getLastModifyTime()).lastIndexOf("."))), "yyyy-MM-dd");
				}else{
					if(null != graphRelaOrg.getGraph().getCreateTime()){
						updateDate = DateUtils.formatDate(DateUtils.parseTimeStamp(
							String.valueOf(graphRelaOrg.getGraph().getCreateTime()).substring(
								0,String.valueOf(graphRelaOrg.getGraph().getCreateTime()).lastIndexOf("."))), "yyyy-MM-dd");
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
	 * 1.查询某个组织关联的所有图形中的一个图形，orgId必传
	 * 2.查询某个组织关联图形的关联关系下的一个图形，graphRelaOrgId必传
	 * 3.新建某个组织关联下的一个图形，isNew，orgId必传
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param panelId 
	 * @param graphRelaOrgId 组织关联图形的关联关系的ID
	 * @param orgId 组织ID
	 * @param viewType 查看状态
	 * @param isNew 是否新建新的图形
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/findGraphRelaOrg.f")
	public String findGraphRelaOrg(String panelId, String graphRelaOrgId,String orgId,String viewType,String isNew,
			HttpServletRequest request) throws Exception{
		request.setAttribute("panelId", panelId);//程序约定的
		GraphRelaOrg graphRelaOrg = null;
		if(StringUtils.isNotBlank(graphRelaOrgId)){
			request.setAttribute("graphRelaOrgId", graphRelaOrgId);
			graphRelaOrg = o_graphRelaOrgBO.findGraphRelaOrgById(graphRelaOrgId);
		}else if(StringUtils.isNotBlank(orgId)){
			request.setAttribute("orgId", orgId);
			//如果可修改则传viewType的值为Contents.GRAPH_TYPE_CREATE
			if(StringUtils.isNotBlank(isNew) && isNew.equals("true")){
				viewType = Contents.GRAPH_TYPE_CREATE;
				request.setAttribute("graphName", o_organizationBO.get(orgId).getOrgname()+"-");//程序约定的
			}else{
				List<GraphRelaOrg> graphRelaOrgList = o_graphRelaOrgBO.findGraphRelaOrgListBySome(null, orgId, null);
				if(graphRelaOrgList!=null && graphRelaOrgList.size()>0){
					graphRelaOrg = graphRelaOrgList.get(0);
				}
			}
			
		}
		if(graphRelaOrg!=null){
			String graphContext = IOUtils.toString(new ByteArrayInputStream(graphRelaOrg.getGraph().getContent()),"UTF-8");
			request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));//程序约定的
			request.setAttribute("graphId", graphRelaOrg.getGraph().getId());//程序约定的
			request.setAttribute("graphName", graphRelaOrg.getGraph().getName());//程序约定的
			request.setAttribute("orgId", graphRelaOrg.getOrg().getId());
			//如果viewType为可修改状态，要判断是否与图形允许操作相违背，如果违背，以图形允许的操作为准
			if(StringUtils.isNotBlank(viewType) && viewType.equals(Contents.GRAPH_TYPE_CREATE)){
				if(!graphRelaOrg.getType().equals(viewType)){
					viewType = graphRelaOrg.getType();
				}
				request.setAttribute("type", viewType);//程序约定的
			}else{
				request.setAttribute("type", graphRelaOrg.getType());//程序约定的
			}
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){//程序约定的
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphdrawview";//程序约定的
		}
	}
	
	
}

