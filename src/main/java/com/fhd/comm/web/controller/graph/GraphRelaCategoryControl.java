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

import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.graph.GraphBO;
import com.fhd.comm.business.graph.GraphRelaCategoryBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.graph.Graph;
import com.fhd.entity.comm.graph.GraphRelaCategory;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Controller
public class GraphRelaCategoryControl {

	@Autowired
	private GraphRelaCategoryBO o_graphRelaCategoryBO;
	
	@Autowired
	private GraphBO o_graphBO;
	
	@Autowired
	private CategoryBO o_categoryBO;
	
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
	@RequestMapping(value = "/comm/graph/saveGraphRelaCategory.f")
	public void saveGraphRelaCategory(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            Graph graph = new Graph();
            String categoryId = request.getParameter("categoryId");//针对不同对象传不同对象的ID
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
				GraphRelaCategory graphRelaCategory = new GraphRelaCategory();//针对不同对象保存不同对象的关联关系
				graphRelaCategory.setGraph(new Graph(graphId));
				graphRelaCategory.setCategory(new Category(categoryId));
				graphRelaCategory.setType(Contents.GRAPH_TYPE_CREATE);
				o_graphRelaCategoryBO.saveGraphRelaCategory(graphRelaCategory);
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
	 * @param categoryId 记分卡ID
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/mergeGraphRelaCategory.f")
	public void mergeGraphRelaCategory(String graphId,String categoryId,HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        boolean flag = false;
        try {
        	response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
			List<GraphRelaCategory> graphRelaCategoryList = null;//针对不同对象保存不同对象的关联关系
			if(StringUtils.isNotBlank(graphId) && StringUtils.isNotBlank(categoryId)){
				graphRelaCategoryList = o_graphRelaCategoryBO.findGraphRelaCategoryListBySome(graphId, categoryId, Contents.GRAPH_TYPE_TRANSFER);
			}
			if(!(null != graphRelaCategoryList && graphRelaCategoryList.size()>0)){
				GraphRelaCategory graphRelaCategory = new GraphRelaCategory();
				graphRelaCategory.setGraph(new Graph(graphId));
				graphRelaCategory.setCategory(new Category(categoryId));
				graphRelaCategory.setType(Contents.GRAPH_TYPE_TRANSFER);
				o_graphRelaCategoryBO.saveGraphRelaCategory(graphRelaCategory);
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
	@RequestMapping(value = "/comm/graph/removeGraphRelaCategory.f")
	public void removeGraphRelaCategory(String graphRelaCategoryId, HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		boolean flag = false;
        try {
        	out = response.getWriter();
        	if(StringUtils.isNotBlank(graphRelaCategoryId)){
        		o_graphRelaCategoryBO.removeGraphRelaCategoryById(graphRelaCategoryId);
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
	 * 查询记分卡ID为categoryId的记分卡关联的图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param categoryId 记分卡ID
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "/comm/graph/findGraphRelaCategoryList.f")
	public Map<String,Object> findGraphRelaCategoryList(String categoryId,HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(categoryId!=null && categoryId.length()>0){
			request.setAttribute("categoryId", categoryId);//针对不同对象传不同对象的ID
			List<GraphRelaCategory> graphRelaCategoryList = o_graphRelaCategoryBO.findGraphRelaCategoryListBySome(null, categoryId, null);//针对不同对象查询不同对象的关联关系
			for (GraphRelaCategory graphRelaCategory : graphRelaCategoryList) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("graphRelaCategoryId", graphRelaCategory.getId());//针对不同对象的关联关系传不同的关联关系的ID
				dataMap.put("graphId", graphRelaCategory.getGraph().getId());//程序约定的
				dataMap.put("name", graphRelaCategory.getGraph().getName());//程序约定的
				dataMap.put("type", graphRelaCategory.getType());//程序约定的
				String updateDate = "";
				if(null != graphRelaCategory.getGraph().getLastModifyTime()){
					updateDate = DateUtils.formatDate(
						DateUtils.parseTimeStamp(
							String.valueOf(graphRelaCategory.getGraph().getLastModifyTime()).substring(
								0,String.valueOf(graphRelaCategory.getGraph().getLastModifyTime()).lastIndexOf("."))), "yyyy-MM-dd");
				}else{
					if(null != graphRelaCategory.getGraph().getCreateTime()){
						updateDate = DateUtils.formatDate(DateUtils.parseTimeStamp(
							String.valueOf(graphRelaCategory.getGraph().getCreateTime()).substring(
								0,String.valueOf(graphRelaCategory.getGraph().getCreateTime()).lastIndexOf("."))), "yyyy-MM-dd");
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
	 * 1.查询某个记分卡关联的所有图形中的一个图形，categoryId必传
	 * 2.查询某个记分卡关联图形的关联关系下的一个图形，graphRelaCategoryId必传
	 * 3.新建某个记分卡关联下的一个图形，isNew，categoryId必传
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param panelId 
	 * @param graphRelaCategoryId 记分卡关联图形的关联关系的ID
	 * @param categoryId 记分卡ID
	 * @param viewType 查看状态
	 * @param isNew 是否新建新的图形
	 * @param request
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/comm/graph/findGraphRelaCategory.f")
	public String findGraphRelaCategory(String panelId, String graphRelaCategoryId,String categoryId,String viewType,String isNew,
			HttpServletRequest request) throws Exception{
		request.setAttribute("panelId", panelId);//程序约定的
		GraphRelaCategory graphRelaCategory = null;
		if(StringUtils.isNotBlank(graphRelaCategoryId)){
			request.setAttribute("graphRelaCategoryId", graphRelaCategoryId);
			graphRelaCategory = o_graphRelaCategoryBO.findGraphRelaCategoryById(graphRelaCategoryId);
		}else if(StringUtils.isNotBlank(categoryId)){
			request.setAttribute("categoryId", categoryId);
			//如果可修改则传viewType的值为Contents.GRAPH_TYPE_CREATE
			if(StringUtils.isNotBlank(isNew) && isNew.equals("true")){
				viewType = Contents.GRAPH_TYPE_CREATE;
				request.setAttribute("graphName", o_categoryBO.findCategoryById(categoryId).getName()+"-");//程序约定的
			}else{
				List<GraphRelaCategory> graphRelaCategoryList = o_graphRelaCategoryBO.findGraphRelaCategoryListBySome(null, categoryId, null);
				if(graphRelaCategoryList!=null && graphRelaCategoryList.size()>0){
					graphRelaCategory = graphRelaCategoryList.get(0);
				}
			}
			
		}
		if(graphRelaCategory!=null){
			String graphContext = IOUtils.toString(new ByteArrayInputStream(graphRelaCategory.getGraph().getContent()),"UTF-8");
			request.setAttribute("graphContext", graphContext.replaceAll("\\n"," ").replaceAll("\"", "\'"));//程序约定的
			request.setAttribute("graphId", graphRelaCategory.getGraph().getId());//程序约定的
			request.setAttribute("graphName", graphRelaCategory.getGraph().getName());//程序约定的
			request.setAttribute("categoryId", graphRelaCategory.getCategory().getId());
			//如果viewType为可修改状态，要判断是否与图形允许操作相违背，如果违背，以图形允许的操作为准
			if(StringUtils.isNotBlank(viewType) && viewType.equals(Contents.GRAPH_TYPE_CREATE)){
				if(!graphRelaCategory.getType().equals(viewType)){
					viewType = graphRelaCategory.getType();
				}
				request.setAttribute("type", viewType);//程序约定的
			}else{
				request.setAttribute("type", graphRelaCategory.getType());//程序约定的
			}
		}
		if(StringUtils.isNotBlank(viewType) && viewType.startsWith("graph")){//程序约定的
			return "graph/"+StringUtils.trim(viewType);
		}else{
			return "graph/graphdrawview";//程序约定的
		}
	}
	
	
}

