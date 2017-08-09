	package com.fhd.sys.web.controller.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.dao.utils.HibernateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.view.VSysAuthority;
import com.fhd.sys.business.auth.AuthorityBO;
import com.fhd.sys.business.auth.SysAuthorityTreeBO;
import com.fhd.sys.web.form.auth.SysAuthorityForm;

@Controller
public class SysAuthorityControl {
	
	@Autowired
	private AuthorityBO o_sysAuthorityBO;
	@Autowired
	private SysAuthorityTreeBO o_sysAuthorityTreeBO;
	
	/**
	 * 
	 * getTreeRoot:得到根节点
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping("/sys/auth/auth/getTreeRoot.f")
    public Map<String, Object> getTreeRoot(){
        SysAuthority sysAuthority = o_sysAuthorityTreeBO.getTreeRoot();
        Map<String, Object> nodeMap = sysAuthorityToNodeMap(sysAuthority);
        return nodeMap;
    }
    
    /**
     * 
     * getTreeCheckRoot:
     * 
     * @author 杨鹏
     * @return
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/sys/auth/auth/getTreeCheckRoot.f")
    public Map<String, Object> getTreeCheckRoot(){
    	SysAuthority sysAuthority = o_sysAuthorityTreeBO.getTreeRoot();
    	Map<String, Object> nodeMap = sysAuthorityToCheckNodeMap(sysAuthority);
    	return nodeMap;
    }
	
	/**
	 * 
	 * findById:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/auth/findById.f")
	public Map<String, Object> findById(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> data = new HashMap<String, String>();
		SysAuthority sysAuthority = o_sysAuthorityBO.findById(id);
		if(sysAuthority!=null){
			data.put("id", sysAuthority.getId());
			data.put("authorityCode", sysAuthority.getAuthorityCode());
			data.put("authorityName", sysAuthority.getAuthorityName());
			SysAuthority parentAuthority = sysAuthority.getParentAuthority();
			String parentAuthorityId = "";
			String parentAuthorityName = "";
			if(null!=parentAuthority){
				parentAuthorityId = parentAuthority.getId();
				parentAuthorityName = parentAuthority.getAuthorityName();
			}
			data.put("parentAuthorityId", parentAuthorityId);
			data.put("parentAuthorityName", parentAuthorityName);
			Integer sn = sysAuthority.getSn();
			String snStr="";
			if(sn!=null){
				snStr=sn+"";
			}
			data.put("sn", snStr);
			String icon = sysAuthority.getIcon();
			data.put("icon", icon);
			String url = sysAuthority.getUrl();
			data.put("url", url);
		}
		map.put("data", data);
		map.put("success", true);
		return map;
    }
    /**
     * 
     * findPageBySome:分页查询
     * 
     * @author 杨鹏
     * @param query
     * @param parentId
     * @param etype
     * @param limit
     * @param start
     * @param sort
     * @return
     * @throws Exception
     * @since  fhd　Ver 1.1
     */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/sys/auth/auth/findPageBySome.f")
	public Map<String,Object> findPageBySome(String query,String parentId,String etype,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<SysAuthority> page=new Page<SysAuthority>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysAuthorityBO.findPageBySome(page, query,parentId,etype,sortList);
		List<SysAuthority> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (SysAuthority sysAuthority : list) {
			Map<String, Object> sysAuthorityToMap = sysAuthorityToMap(sysAuthority);
			datas.add(sysAuthorityToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	/**
	 * 
	 * sysAuthorityToMap:将权限转成map格式
	 * 
	 * @author 杨鹏
	 * @param sysAuthority
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> sysAuthorityToMap(SysAuthority sysAuthority){
		Map<String,Object> map = new HashMap<String, Object>();
		String id = sysAuthority.getId();
		String parentId="";
		SysAuthority parentAuthority = sysAuthority.getParentAuthority();
		if(parentAuthority!=null){
			parentId = parentAuthority.getId();
		}
		String authorityCode = sysAuthority.getAuthorityCode();
		String authorityName = sysAuthority.getAuthorityName();
		String type = sysAuthority.getEtype();
		String icon = sysAuthority.getIcon();
		String url = sysAuthority.getUrl();
		int sn = sysAuthority.getSn();
		map.put("id", id);
		map.put("parentId", parentId);
		map.put("authorityCode", authorityCode);
		map.put("authorityName", authorityName);
		map.put("etype", type);
		map.put("icon", icon);
		map.put("url", url);
		map.put("sn", sn);
		return map;
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "/sys/menu/authorityTreeLoader.f")
	public void authorityTreeLoader(HttpServletResponse response,HttpServletRequest request) throws Exception {
		PrintWriter out = null;
		out = response.getWriter();
		try{
			out.write(o_sysAuthorityTreeBO.authorityTreeLoader(request));
		} finally {
			out.close();
		}
	}
	
	/**
	 * 宋佳改变菜单加载方式
	 * @param node
	 * @param canChecked
	 * @param leafCheck
	 * @param query
	 * @param showLight
	 * @param dealStatus
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/menu/getAuthorityTreeLoader.f")
	public List<Map<String, Object>> getAuthorityTreeLoader(HttpServletRequest request,String node,String params,boolean canChecked) {
		if("root".equals(node)){
			node = params;
		}
		return o_sysAuthorityTreeBO.getAuthorityTree(request,node);
	}
	
	/**
	 * 
	 * isAuthorityCode:验证code是否存在
	 * 
	 * @author 杨鹏
	 * @param authorityCode
	 * @param authorityId
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/auth/isAuthorityCode.f")
	public Boolean isAuthorityCode(String authorityCode,String authorityId)throws Exception {
		Boolean flag=false;
		List<SysAuthority> list = o_sysAuthorityBO.findBySome(authorityId, authorityCode);
		if(list!=null&&list.size()==0){
			flag=true;
		}
		return flag;
	}
	
    /**
     * 
     * findNodeIdByNoUserId:查询不包括指定ID的权限
     * 
     * @author 杨鹏
     * @param noUserId
     * @return
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/sys/auth/auth/findNodeIdByNoUserId.f")
    public String[] findNodeIdByNoUserId(String noUserId){
    	Set<String> nodeIds = o_sysAuthorityTreeBO.findNodeIdByNoUserId(noUserId);
    	return nodeIds.toArray(new String[nodeIds.size()]);
    }
    
    /**
     * 
     * findNodeIdByUserId:查询包括指定ID的权限
     * 
     * @author 杨鹏
     * @param userId
     * @return
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/sys/auth/auth/findNodeIdByUserId.f")
    public String[] findNodeIdByUserId(String userId){
    	Set<String> nodeIds =o_sysAuthorityTreeBO.findNodeIdByUserId(userId);
    	return nodeIds.toArray(new String[nodeIds.size()]);
    }
    /**
     * 
     * findNodeIdByNoRoleId:查询不包括指定ID的权限
     * 
     * @author 杨鹏
     * @param noRoleId
     * @return
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/sys/auth/auth/findNodeIdByNoRoleId.f")
    public String[] findNodeIdByNoRoleId(String noRoleId){
        Set<String> nodeIds = o_sysAuthorityTreeBO.findNodeIdByNoRoleId(noRoleId);
        return nodeIds.toArray(new String[nodeIds.size()]);
    }
    
    /**
     * 
     * findNodeIdByRoleId:查询包括指定ID的权限
     * 
     * @author 杨鹏
     * @param roleId
     * @return
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/sys/auth/auth/findNodeIdByRoleId.f")
    public String[] findNodeIdByRoleId(String roleId){
    	Set<String> nodeIds =o_sysAuthorityTreeBO.findNodeIdByRoleId(roleId);
    	return nodeIds.toArray(new String[nodeIds.size()]);
    }
    
	/**
	 * 
	 * checkTreeLoader:查询下级节点带复选框的
	 * 
	 * @author 杨鹏
	 * @param nodeId
	 * @param query
	 * @param roleId：包含的角色ID
	 * @param noRoleId：不包含的角色ID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/auth/auth/checkTreeLoader.f")
	public List<Map<String, Object>> checkTreeLoader(String query,String node,String[] nodeIds){
		List<SysAuthority> sysAuthoritys = o_sysAuthorityTreeBO.treeLoader(query, node, nodeIds, "sn","asc");
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		for (SysAuthority sysAuthority : sysAuthoritys) {
			Map<String, Object> map=sysAuthorityToCheckNodeMap(sysAuthority);
			nodes.add(map);
		}
	    return nodes;
	}
    
	/**
	 * 
	 * sysAuthorityToCheckNodeMap:权限映射为带复选框的nodeMap格式
	 * 
	 * @author 杨鹏
	 * @param sysAuthority
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String, Object> sysAuthorityToCheckNodeMap(SysAuthority sysAuthority){
		Map<String, Object> node=new HashMap<String, Object>();
		String id = sysAuthority.getId();
		String authorityName = sysAuthority.getAuthorityName();
		Boolean isLeaf = sysAuthority.getIsLeaf();
        node.put("id",id);
        node.put("text", authorityName);
        node.put("leaf", isLeaf);
        String iconCls="";
        String etype = sysAuthority.getEtype();
        if("M".equals(etype)){
        	iconCls="icon-menu";
        }else if("T".equals(etype)){
        	iconCls="icon-tab";
        }else if("B".equals(etype)){
        	iconCls="icon-button";
        }else if("F".equals(etype)){
        	iconCls="icon-textfield-key";
        }else if("G".equals(etype)){
        	iconCls="icon-ibm-tree-scorecard-group-null";
        }
        node.put("iconCls", iconCls);
        node.put("checked", false);
        return node;
	}
	/**
	 * 
	 * checkTreeLoaderAllByRole:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param node
	 * @param roleId
	 * @param noRoleId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/auth/auth/checkTreeLoaderAllByRole.f")
	public List<Map<String, Object>> checkTreeLoaderAllByRole(String query,String node,String roleId,String noRoleId,String[] etypes){
		Set<String> nodeIdSet=null;
		if(StringUtils.isNotBlank(roleId)){
			nodeIdSet =o_sysAuthorityTreeBO.findNodeIdByRoleId(roleId);
		}else if(StringUtils.isNotBlank(noRoleId)){
			nodeIdSet =o_sysAuthorityTreeBO.findNodeIdByNoRoleId(noRoleId);
		}
		String[] nodeIds = null;
		if(nodeIdSet!=null){
			nodeIds = nodeIdSet.toArray(new String[nodeIdSet.size()]);
		}
		List<VSysAuthority> vSysAuthoritys = o_sysAuthorityTreeBO.treeLoaderAll(query, node, nodeIds,etypes, "sn","asc");
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		for (VSysAuthority vSysAuthority : vSysAuthoritys) {
			Map<String, Object> map=vSysAuthorityToCheckNodeMap(vSysAuthority);
			nodes.add(map);
		}
	    return nodes;
	}
	
	/**
	 * 
	 * checkTreeLoaderAllByUser:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param node
	 * @param userId
	 * @param noUserId
	 * @param etypes
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/auth/auth/checkTreeLoaderAllByUser.f")
	public List<Map<String, Object>> checkTreeLoaderAllByUser(String query,String node,String userId,String noUserId,String[] etypes){
		Set<String> nodeIdSet=null;
		if(StringUtils.isNotBlank(userId)){
			nodeIdSet =o_sysAuthorityTreeBO.findNodeIdByUserId(userId);
		}else if(StringUtils.isNotBlank(noUserId)){
			nodeIdSet =o_sysAuthorityTreeBO.findNodeIdByNoUserId(noUserId);
		}
		String[] nodeIds = null;
		if(nodeIdSet!=null){
			nodeIds = nodeIdSet.toArray(new String[nodeIdSet.size()]);
		}
		List<VSysAuthority> vSysAuthoritys = o_sysAuthorityTreeBO.treeLoaderAll(query, node, nodeIds,etypes, "sn","asc");
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		for (VSysAuthority vSysAuthority : vSysAuthoritys) {
			Map<String, Object> map=vSysAuthorityToCheckNodeMap(vSysAuthority);
			nodes.add(map);
		}
	    return nodes;
	}
	
	/**
	 * 
	 * vSysAuthorityToCheckNodeMap:权限映射为带复选框的nodeMap格式
	 * 
	 * @author 杨鹏
	 * @param sysAuthority
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String, Object> vSysAuthorityToCheckNodeMap(VSysAuthority vSysAuthority){
		Map<String, Object> node=new HashMap<String, Object>();
		String id = vSysAuthority.getId();
		String authorityName = vSysAuthority.getAuthorityName();
		Boolean isLeaf = vSysAuthority.getIsLeaf();
		if(vSysAuthority.getChildrenCount()==0){
			isLeaf=true;
		}else{
			isLeaf=false;
		}
        node.put("id",id);
        node.put("text", authorityName);
        node.put("leaf", isLeaf);
        String iconCls="";
        String etype = vSysAuthority.getEtype();
        if("M".equals(etype)){
        	iconCls="icon-menu";
        }else if("T".equals(etype)){
        	iconCls="icon-tab";
        }else if("B".equals(etype)){
        	iconCls="icon-button";
        }else if("F".equals(etype)){
        	iconCls="icon-textfield-key";
        }else if("G".equals(etype)){
        	iconCls="icon-ibm-tree-scorecard-group-null";
        }
        node.put("iconCls", iconCls);
        node.put("checked", false);
        return node;
	}
	/**
	 * 
	 * treecheckLoader:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param etype
	 * @param node
	 * @param nodeIds
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/auth/auth/treeCheckLoader.f")
	public List<Map<String, Object>> treeCheckLoader(String query,String etype,String node,String[] nodeIds){
		List<SysAuthority> sysAuthoritys = o_sysAuthorityTreeBO.treeLoader(query,etype, node, nodeIds, "sn","asc");
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		for (SysAuthority sysAuthority : sysAuthoritys) {
			Map<String, Object> map=sysAuthorityToCheckNodeMap(sysAuthority);
			nodes.add(map);
		}
	    return nodes;
	}
	/**
	 * 
	 * treeLoader:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param etype
	 * @param roleId：包含的角色ID
	 * @param noRoleId：不包含的角色ID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/auth/auth/treeLoader.f")
	public List<Map<String, Object>> treeLoader(String query,String etype,String node,String[] nodeIds){
		List<SysAuthority> sysAuthoritys = o_sysAuthorityTreeBO.treeLoader(query,"M", node, nodeIds, "sn","asc");
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		for (SysAuthority sysAuthority : sysAuthoritys) {
			Map<String, Object> map=sysAuthorityToNodeMap(sysAuthority);
			nodes.add(map);
		}
	    return nodes;
	}
	/**
	 * 
	 * sysAuthorityToNodeMap:权限映射为nodeMap格式
	 * 
	 * @author 杨鹏
	 * @param sysAuthority
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String, Object> sysAuthorityToNodeMap(SysAuthority sysAuthority){
		Map<String, Object> node=new HashMap<String, Object>();
		String id = sysAuthority.getId();
		String authorityName = sysAuthority.getAuthorityName();
		Boolean isLeaf = sysAuthority.getIsLeaf();
        node.put("id",id);
        node.put("text", authorityName);
        node.put("leaf", isLeaf);
        String iconCls="";
        String etype = sysAuthority.getEtype();
        if("M".equals(etype)){
        	iconCls="icon-menu";
        }else if("T".equals(etype)){
        	iconCls="icon-tab";
        }else if("B".equals(etype)){
        	iconCls="icon-button";
        }else if("F".equals(etype)){
        	iconCls="icon-textfield-key";
        }else if("G".equals(etype)){
        	iconCls="icon-ibm-tree-scorecard-group-null";
        }
        node.put("iconCls", iconCls);
        return node;
	}
	/**
	 * 
	 * merge:保存修改方法
	 * 
	 * @author 杨鹏
	 * @param form
	 * @param result
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/auth/merge.f")
	public Map<String,Object> merge(SysAuthorityForm form,BindingResult result) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			SysAuthority sysAuthority=new SysAuthority();
			String id = form.getId();
			SysAuthority parentAuthority=new SysAuthority();
			if(StringUtils.isBlank(id)){
				id=Identities.uuid();
			}else{
				sysAuthority=o_sysAuthorityBO.findById(id);
			}
			sysAuthority.setId(id);
			sysAuthority.setAuthorityCode(form.getAuthorityCode());
			sysAuthority.setAuthorityName(form.getAuthorityName());
			sysAuthority.setEtype(form.getEtype());
			parentAuthority.setId(form.getParentId());
			sysAuthority.setParentAuthority(parentAuthority);
			sysAuthority.setSn(form.getSn());
			sysAuthority.setIcon(form.getIcon());
			sysAuthority.setUrl(form.getUrl());
			o_sysAuthorityBO.merge(sysAuthority);
			map.put("id", id);
			map.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 
	 * mergeByDatasStr:根据data字符串保存
	 * 
	 * @author 杨鹏
	 * @param datasStr
	 * @param etype
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/auth/mergeByDatasStr.f")
	public void mergeByDatasStr(String datasStr) {
		JSONArray jsonArray=JSONArray.fromObject(datasStr);
		Integer n = jsonArray.size();
		List<SysAuthority> list = new ArrayList<SysAuthority>();
		for(int i=0;i<n;i++){
			SysAuthority sysAuthority=new SysAuthority();
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			String id = jsonObj.getString("id");
			if(StringUtils.isBlank(id)){
				id=Identities.uuid();
			}
			String parentId = jsonObj.getString("parentId");
			String authorityCode = jsonObj.getString("authorityCode");
			String authorityName = jsonObj.getString("authorityName");
			String etype = jsonObj.getString("etype");
			String url = jsonObj.getString("url");
			String icon = jsonObj.getString("icon");
			Integer sn = jsonObj.getInt("sn");
			sysAuthority.setId(id);
			SysAuthority parentAuthority=new SysAuthority();
			parentAuthority.setId(parentId);
			sysAuthority.setParentAuthority(parentAuthority);
			sysAuthority.setAuthorityCode(authorityCode);
			sysAuthority.setAuthorityName(authorityName);
			sysAuthority.setUrl(url);
			sysAuthority.setIcon(icon);
			sysAuthority.setSn(sn);
			sysAuthority.setEtype(etype);
			list.add(sysAuthority);
		}
		o_sysAuthorityBO.merge(list.toArray(new SysAuthority[list.size()]));
	}
	
	/**
	 * 
	 * removeById:根据ID删除权限
	 * 
	 * @author 杨鹏
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
	@RequestMapping(value = "/sys/auth/auth/removeById.f")
	public void removeById(String id){
		o_sysAuthorityBO.removeById(id);
	}
    /**
     * 
     * removeByIds:根据ID删除权限
     * 
     * @author 杨鹏
     * @param ids
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
	@RequestMapping(value = "/sys/auth/auth/removeByIds.f")
	public void removeByIds(String idsStr){
    	String[] ids = StringUtils.split(idsStr,",");
		o_sysAuthorityBO.removeByIds(ids);
	}
/*****************************************新旧代码分割线*******************************************************/	
	
	/**
	 * 加载权限树.
	 * @author 吴德福
	 * @param request
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/authorityStructure.do")
	public void authorityStructure(HttpServletRequest request) {
		request.setAttribute("orgRoot", o_sysAuthorityBO.getRootOrg());
	}
	/**
	 * 构造根结点的全部树结点.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @return List<Map<String, Object>> 根结点的树结点集合.
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/loadAuthorityTree.do")
	public List<Map<String, Object>> loadAuthorityTree(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String id = request.getParameter("node");
		return o_sysAuthorityBO.loadAuthorityTree(id,request.getContextPath());
	}
	/**
	 * 构造根结点的静态树json数据.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/loadStaticAuthorityTree.do")
	public void loadStaticAuthorityTree(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		try {
			response.setContentType("text/json; charset=utf-8");
			out = response.getWriter();
			
			String authorityString = "";
			String id = request.getParameter("node");
			if(StringUtils.isNotBlank(id)){
				authorityString = o_sysAuthorityBO.loadStaticAuthorityTree(id);
				request.getSession().setAttribute("authorityString", authorityString);
			}
			out.write(authorityString);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	
	/**
	 * 点击菜单，加载tab页面.
	 * @author 吴德福
	 * @param id 权限id.
	 * @param request
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/tabs.do")
	public void showTabs(String id, HttpServletRequest request) {
		request.setAttribute("id", id);
	}


	/**
	 * 进入下级功能页面
	 * @author 万业
	 * @param id
	 * @param success
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/sys/auth/authority/query.do")
	public void query(String id,String success,HttpServletRequest request,Model model){
		model.addAttribute("id",id);
		model.addAttribute("success", success);
		//model.addAttribute("authorityList", o_sysAuthorityBO.query(id,HibernateUtils.buildPropertyFilters(request, "filter_")));
	}
	//不知是否有用
/*	*//**
	 * 获取功能列表 分页 查询
	 * @author 万业
	 * @param limit
	 * @param start
	 * @param authForm
	 * @return
	 *//*
	@ResponseBody
	@RequestMapping(value="/sys/auth/authority/queryList.do")
	public Map<String, Object> authorList(int limit, int start,HttpServletRequest request, String authorityCode, String authorityName, String parentId){
		Page<SysAuthority> page = new Page<SysAuthority>();
		String id=request.getParameter("id");
		page.setPageNumber((limit == 0 ? 0 : start / limit)+1);
		page.setObjectsPerPage(limit);
		
		if(StringUtils.isBlank(parentId)){
			
			page=o_sysAuthorityBO.queryAuthPage(page,authorityCode, authorityName, id);
		}else{
			
			page=o_sysAuthorityBO.queryAuthPage(page,authorityCode, authorityName, parentId);
		}
		return this.convertsysAuthValues(page);
	}
	*//**
	 * 转换数据
	 * @author 万业
	 * @param page
	 * @return
	 *//*
	public Map<String, Object> convertsysAuthValues(Page<SysAuthority> page){
		List<SysAuthority> sysAuths=page.getList();
		Iterator<SysAuthority> iterator=sysAuths.iterator();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		while(iterator.hasNext()){
			Map<String, Object> row = new HashMap<String, Object>();
			SysAuthority sysAuth=iterator.next();
			row.put("id", sysAuth.getId());
			row.put("authCode",sysAuth.getAuthorityCode());
			row.put("authName", sysAuth.getAuthorityName());
			row.put("authSn", sysAuth.getSn());
			row.put("authpname",o_sysAuthorityBO.getGuidName(sysAuth.getIdSeq().split("\\.")));
			
			datas.add(row);
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getFullListSize());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}*/
	/**
	 * get方式修改权限.
	 * @author 吴德福
	 * @param id 菜单id.
	 * @param model
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/edit.do", method = RequestMethod.GET)
	public void g_editAuthority(String id,Model model) throws IllegalAccessException, InvocationTargetException {
		SysAuthorityForm sysAuthorityForm = new SysAuthorityForm();
		SysAuthority sysAuthority = o_sysAuthorityBO.queryAuthorityById(id);
		BeanUtils.copyProperties(sysAuthority, sysAuthorityForm);
		model.addAttribute("sysAuthorityForm",sysAuthorityForm);
	}
	/**
	 * post方式修改权限.
	 * @author 吴德福
	 * @param sysAuthorityForm 权限Form.
	 * @param result
	 * @return String
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/edit.do", method = RequestMethod.POST)
	public String p_editAuthority(SysAuthorityForm sysAuthorityForm,BindingResult result) throws IllegalAccessException, InvocationTargetException {
		SysAuthority sysAuthority = o_sysAuthorityBO.queryAuthorityById(sysAuthorityForm.getId());
		BeanUtils.copyProperties(sysAuthorityForm, sysAuthority, new String[]{"parentAuthority"});
		sysAuthority.setAuthorityCode(sysAuthorityForm.getAuthorityCode().toUpperCase());
		sysAuthority.setUrl(sysAuthorityForm.getUrl());
		o_sysAuthorityBO.updateAuthority(sysAuthority);
		return "redirect:/sys/auth/authority/edit.do?id=" + sysAuthorityForm.getId();
	}
	/**
	 * get方式添加权限.
	 * @author 吴德福
	 * @param id 权限id.
	 * @param model
	 * @return String 
	 * @since  fhd　Ver 1.1
	 */
	//@ResponseBody
	@RequestMapping(value = "/sys/auth/authority/add.do", method = RequestMethod.GET)
	public String g_add(String id,Model model, HttpServletRequest request) {
		SysAuthorityForm sysAuthorityForm = new SysAuthorityForm();
		sysAuthorityForm.setParentAuthority(o_sysAuthorityBO.queryAuthorityById(id));
		model.addAttribute(sysAuthorityForm);
		model.addAttribute("operation", request.getParameter("operation"));
		return "sys/auth/authority/add-model";
	}
	/**
	 * post方式添加权限.
	 * @author 吴德福
	 * @param sysAuthorityForm 权限Form.
	 * @param result
	 * @return String 
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/add.do", method = RequestMethod.POST)
	public void p_add(SysAuthorityForm sysAuthorityForm,HttpServletResponse response, HttpServletRequest request)throws Exception{
		PrintWriter out = null;
		String flag = "false";
		out = response.getWriter();
		
		try{
			SysAuthority sysAuthority = new SysAuthority();
			BeanUtils.copyProperties(sysAuthorityForm, sysAuthority);
			sysAuthority.setId(Identities.uuid());
			sysAuthority.setIsLeaf(true);
			sysAuthority.setEtype(sysAuthorityForm.getEtype().toUpperCase());
			sysAuthority.setIcon(sysAuthorityForm.getIcon());
			sysAuthority.setAuthorityCode(sysAuthorityForm.getAuthorityCode().toUpperCase());
			sysAuthority.setParentAuthority(o_sysAuthorityBO.queryAuthorityById(sysAuthorityForm.getParentId()));
		
			sysAuthority.setIsLeaf(true);
			sysAuthority.setUrl(sysAuthorityForm.getUrl());
			if (sysAuthority.getParentAuthority().getIsLeaf()) {
				sysAuthority.getParentAuthority().setIsLeaf(false);
			}
			o_sysAuthorityBO.saveAuthority(sysAuthority);
			flag = "true";
			out.write(flag);
		}catch (Exception e) {
			e.printStackTrace();
			out.write(flag);
		} finally {
			out.close();
		}
		
		
		//return "redirect:/sys/auth/authority/query.do?id=" + sysAuthorityForm.getParentAuthority().getId() + "&success=1";
		
		
		
		
	}
	/**
	 * get方式更新权限.
	 * @author 吴德福
	 * @param id
	 * @param model
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/update.do", method = RequestMethod.GET)
	public String g_update(String id,Model model) {
		SysAuthorityForm sysAuthorityForm = new SysAuthorityForm();
		SysAuthority sysAuthority = o_sysAuthorityBO.queryAuthorityById(id);
		BeanUtils.copyProperties(sysAuthority, sysAuthorityForm);
		sysAuthorityForm.setParentId(sysAuthority.getParentAuthority().getId());
		model.addAttribute(sysAuthorityForm);
		return "sys/auth/authority/update-model";
	}
	/**
	 * post方式更新权限.
	 * @author 吴德福
	 * @param menuForm
	 * @param result
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/update.do", method = RequestMethod.POST)
	public void p_update(SysAuthorityForm sysAuthorityForm, HttpServletResponse response, HttpServletRequest request)throws Exception{
		PrintWriter out = null;
		String flag = "false";
		
		try {
			out = response.getWriter();
			SysAuthority sysAuthority = new SysAuthority();
			BeanUtils.copyProperties(sysAuthorityForm, sysAuthority);
			sysAuthority.setAuthorityCode(sysAuthorityForm.getAuthorityCode().toUpperCase());
			sysAuthority.setParentAuthority(o_sysAuthorityBO.queryAuthorityById(sysAuthorityForm.getParentId()));
			sysAuthority.setUrl(sysAuthorityForm.getUrl());
			//获得当前未修改的权限的父权限
			SysAuthority parAuthority = o_sysAuthorityBO.queryAuthorityById(sysAuthorityForm.getParentAuthority().getId());
			//查询该父权限下是否存在权限
			List<SysAuthority> authorityList = o_sysAuthorityBO.query(parAuthority.getId(),HibernateUtils.buildPropertyFilters(request, "filter_"));
			//不存在则将该节点置为叶子结点
			if(authorityList.isEmpty() || authorityList.size()<2){
				parAuthority.setIsLeaf(true);
			}
			SysAuthority a = sysAuthority.getParentAuthority();
			if (a.getIsLeaf()) {
				sysAuthority.getParentAuthority().setIsLeaf(false);
			}
			if (!sysAuthority.getParentAuthority().getId().equals(sysAuthority.getId())) {
				o_sysAuthorityBO.updateAuthority(sysAuthority);
				//o_sysAuthorityBO.updateAuthority(parAuthority);
				flag="true";
			}else{
				flag="上级权限设置错误";
			}
			out.write(flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != out){
				out.close();
			}
		}
	}
	/**
	 * post方式删除权限.
	 * @author 吴德福
	 * @param id 权限id集合.
	 * @param parentid 父id.
	 * @return String 
	 * @since  fhd　Ver 1.1
	 */
/*	@RequestMapping(value = "/sys/auth/authority/delete.do", method = RequestMethod.POST)
	public String p_delete(String[] id,String parentid,HttpServletRequest request){
		parentid = id[0];
		for(int i=1;i<id.length;i++){
			//权限下有子权限存在，不允许删除
			List<SysAuthority> authorityList = new ArrayList<SysAuthority>();
			authorityList = o_sysAuthorityBO.query(id[i],HibernateUtils.buildPropertyFilters(request, "filter_"));
			if(null != authorityList && authorityList.size()>0){
				return "redirect:/sys/auth/authority/query.do?id=" + parentid + "&success=0";
			}
			
			o_sysAuthorityBO.removeAuthority(id[i]);
		}
		return "redirect:/sys/auth/authority/query.do?id=" + parentid + "&success=1";
	}*/
	
	/**
	 * post方式删除权限.
	 * @author 吴德福
	 * @param ids 权限id集合.
	 * @param parentid 父id.
	 * @return boolean 
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/authority/delete.do")
	public boolean p_delete(String ids,HttpServletRequest request){
		//parentid = id[0];
		String[] authids=ids.split(",");
		SysAuthority parAuthority = null;
		for(int i=0;i<authids.length;i++){
			if(StringUtils.isNotBlank(authids[i])){
				
				//权限下有子权限存在，不允许删除
				parAuthority = o_sysAuthorityBO.queryAuthorityById(authids[i]).getParentAuthority();
				List<SysAuthority> authorityList = o_sysAuthorityBO.query(authids[i],HibernateUtils.buildPropertyFilters(request, "filter_"));
				if(!authorityList.isEmpty()){
					return false;
				}
				o_sysAuthorityBO.removeAuthority(authids[i]);
				List<SysAuthority> authorities = o_sysAuthorityBO.query(parAuthority.getId(), HibernateUtils.buildPropertyFilters(request, "filter_"));
				if(authorities.isEmpty()){
					parAuthority.setIsLeaf(true);
				}
				o_sysAuthorityBO.updateAuthority(parAuthority);
			}
			
		}
		return true;
	}
	
	/**
	 * get方式删除权限.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/delete.do", method = RequestMethod.GET)
	public void g_delete(HttpServletRequest request,HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag = "false";
		String aid = request.getParameter("id");
		try {
			out = response.getWriter();
			//权限下有子权限存在，不允许删除
			List<SysAuthority> sysAuthoritys = o_sysAuthorityBO.query(aid,HibernateUtils.buildPropertyFilters(request, "filter_"));
			if(!sysAuthoritys.isEmpty()){
				out.write("权限下有子权限存在,不允许删除");
				return;
			}
			SysAuthority parAuthority = o_sysAuthorityBO.queryAuthorityById(aid).getParentAuthority();
			o_sysAuthorityBO.removeAuthority(aid);	
			List<SysAuthority> authorities = o_sysAuthorityBO.query(parAuthority.getId(), HibernateUtils.buildPropertyFilters(request, "filter_"));
			
			if (authorities.isEmpty()) {
				parAuthority.setIsLeaf(true);
			}
			o_sysAuthorityBO.updateAuthority(parAuthority);
//			SysAuthority parSysAuthority = o_sysAuthorityBO.queryAuthorityById(aid).getParentAuthority();
//			String id = parSysAuthority.getId();
//			List<SysAuthority> pa = o_sysAuthorityBO.query(id,HibernateUtils.buildPropertyFilters(request, "filter_"));

//			//判断被删除的节点的父节点下是否存在节点，不存在则设置为叶子节点
//			sysAuthoritys = o_sysAuthorityBO.query(id,HibernateUtils.buildPropertyFilters(request, "filter_"));
//			if (sysAuthoritys.isEmpty()) {
//				pa.get(0).getParentAuthority().setIsLeaf(true);
//			}
//			o_sysAuthorityBO.updateAuthority(pa.get(0).getParentAuthority());
			
			flag="true";
			out.write(flag);
		}catch(Exception e){
			e.printStackTrace();
			out.write(flag);
		}finally{
			if(null != out){
				out.close();
			}
		}
	}
	/**
	 * 移动node弹出页面.
	 * @author 吴德福
	 * @param model
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/choose.do")
	public String choose(Model model){
		return "sys/auth/authority/choose";
	}
	/**
	 * Ext拖拽移动node.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/move.do")
	public void moveNode(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		
		String flag = "false";
        try {
            String currentId = request.getParameter("currentId");
            String pid = request.getParameter("pid");
            String targetId = request.getParameter("targetId");
            response.setContentType("text/html;charset=utf-8");
            out = response.getWriter();
            
			if (o_sysAuthorityBO.moveNode(currentId,pid,targetId)) {
				flag = "true";
				out.write(flag);
			}else{
				out.write(flag);
			}
        } catch (Exception e) {
        	e.printStackTrace();
        	out.write(flag);
        } finally {
        	if(null != out){
        		out.close();
        	}
        }
	}
	/**
	 * 根据查询条件查询权限.
	 * @author 吴德福
	 * @param model
	 * @param request
	 * @return String 跳转到authorityList.jsp页面.
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/authority/authorityByCondition.do")
	public String queryAuthorityByCondition(Model model,HttpServletRequest request,SysAuthorityForm sysAuthorityForm,BindingResult result){
		model.addAttribute("authorityList", o_sysAuthorityBO.query(sysAuthorityForm));
		return "sys/auth/authority/query";
	}
	/**
	 * 查询所有的功能.
	 * @author 吴德福
	 * @param model
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value="/sys/auth/functionList.do",method=RequestMethod.GET)
	public String queryAllfunction(Model model){
		model.addAttribute("sysAuthorityForm", new SysAuthorityForm());
		return "sys/menu/queryAuthoritySelect";
	}
	// 不知是否有用
/*	*//**
	 * 根据查询条件查询所有的作用于菜单的权限.
	 * @author 吴德福
	 * @param limit
	 * @param start
	 * @param authorityCode
	 * @param authorityName
	 * @return Map<String, Object>
	 * @since  fhd　Ver 1.1
	 *//*
	@ResponseBody
	@RequestMapping(value = "/sys/auth/functionList.do",method=RequestMethod.POST)
	public Map<String, Object> queryAuthorityList(int limit, int start, String authorityCode,String authorityName) {
		Page<SysAuthority> page = new Page<SysAuthority>();
		page.setPageNumber((limit == 0 ? 0 : start / limit) + 1);
		page.setObjectsPerPage(limit);
		page = o_sysAuthorityBO.queryAuthorityList(page,authorityCode,authorityName);
		List<SysAuthority> sysAuthorityList = page.getList();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (SysAuthority sysAuthority : sysAuthorityList) {
			Map<String, Object> item = new HashMap<String, Object>();
			// 组装JSON对象
			item.put("id", sysAuthority.getId());
			item.put("authorityCode", sysAuthority.getAuthorityCode());
			item.put("authorityName", sysAuthority.getAuthorityName());
			item.put("parentAuthority", sysAuthority.getParentAuthority().getAuthorityName());
				
			datas.add(item);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("datas", datas);
		result.put("totalCount", page.getFullListSize());
		return result;
	}*/
	/**
	 * 根据查询条件查询功能.
	 * @author 吴德福
	 * @param model
	 * @param request
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/functionByCondition.do")
	public String queryFunctionByCondition(Model model,HttpServletRequest request,SysAuthorityForm sysAuthorityForm,BindingResult result){
		model.addAttribute("authorityList", o_sysAuthorityBO.query(sysAuthorityForm));
		return "sys/menu/queryAuthority";
	}
}