/*
 *北京第一会达风险管理有限公司 版权所有 2012
 *Copyright(C) 2012 Firsthuida Co.,Ltd. All rights reserved. 
 */


package com.fhd.sys.business.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.sys.auth.RoleDAO;
import com.fhd.entity.sys.auth.SysRole;

/**
 * ClassName:SysRoleTreeBO
 *
 * @author   胡迪新
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-1-8		上午11:32:00
 *
 * @see 	 
 */
@Service
public class SysRoleTreeBO {

	@Autowired
	private RoleDAO o_sysRoleDAO;
	@Autowired
	private RoleBO o_sysRoleBO;
	/**
	 * 
	 * treeLoader:查询树节点
	 * 
	 * @author 杨鹏
	 * @param query
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public  Map<String, Object> treeLoader(String query){
		Map<String, Object> root = new HashMap<String, Object>();
		List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
		List<Map<String, String>> sortList=new ArrayList<Map<String,String>>();
		Map<String, String> sort=new HashMap<String, String>();
		sort.put("property", "sort");
		sort.put("direction", "asc");
		sortList.add(sort);
		sort=new HashMap<String, String>();
		sort.put("property", "roleName");
		sort.put("direction", "asc");
		sortList.add(sort);
		List<SysRole> list = o_sysRoleBO.findBySome(null, query, sortList);
		for (SysRole sysRole : list) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", sysRole.getId());
			node.put("text", sysRole.getRoleName());
			node.put("leaf", true);
			node.put("cls", "role");
			children.add(node);
		}
		root.put("children", children);
		return root;
	}
	
	/**
	 * 
	 * <pre>
	 * 角色树
	 * 角色名称模糊匹配
	 * </pre>
	 * 
	 * @author 胡迪新，张雷
	 * @param id
	 * @param query 查询条件
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<Map<String, Object>> treeLoader(String id,String query){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		
		List<SysRole> sysRoles = o_sysRoleDAO.getAll();
		for (SysRole sysRole : sysRoles) {
			Map<String, Object> node = new HashMap<String, Object>();
			/**
			 * change by jia.song@pcitc.com 去掉id 后面跟的随机数
				node.put("id", sysRole.getId() + "_" + RandomUtils.nextInt(9999));
			 */
			node.put("id", sysRole.getId());
			node.put("dbid", sysRole.getId());
			node.put("text", sysRole.getRoleName());
			node.put("leaf", true);

			node.put("iconCls", "");
			node.put("cls", "role");
			if(StringUtils.isNotBlank(query)){
				if(sysRole.getRoleName().contains(query)){
					nodes.add(node);
				}
			}else{
				nodes.add(node);
			}
			
		}
		
		return nodes;
	}
	
}

