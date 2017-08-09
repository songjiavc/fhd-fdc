/*
 *北京第一会达风险管理有限公司 版权所有 2012
 *Copyright(C) 2012 Firsthuida Co.,Ltd. All rights reserved. 
 */


package com.fhd.sys.web.controller.orgstructure;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.orgstructure.OrgCmpBO;
import com.fhd.sys.business.orgstructure.OrgTreeBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 机构控件控制器
 *
 * @author   胡迪新
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-8-20		上午11:40:33
 *
 * @see 	 
 */

@Controller
public class OrganizationCmpControl {

	@Autowired
	private OrgTreeBO o_orgTreeBO;
	@Autowired
	private OrgCmpBO o_orgCmpBO;
	
	/*
	 * 添加
	 */
	
	/*
	 * 修改
	 */
	
	/*
	 * 删除
	 */
	
	/*
	 * 查询
	 */
	
	/**
	 * 
	 * 部门树
	 * 
	 * @author 胡迪新, 张雷
	 * @param companyid 公司id
	 * @param showLight 部门树节点是否显示风险状态灯
	 * @throws UnsupportedEncodingException 
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/org/cmp/depttreeloader.f")
	public List<Map<String, Object>> deptTreeLoader(String node,Boolean checkable,Boolean subCompany,String chooseId,String query, Boolean companyOnly,Boolean showLight, String schm) throws UnsupportedEncodingException{
		if(showLight==null){
    		showLight = false;
    	}
		if(StringUtils.isBlank(node)){
			node = UserContext.getUser().getCompanyid();
		}
		return o_orgTreeBO.deptTreeLoader(checkable, node, subCompany, chooseId, query, companyOnly,showLight,schm);
		
	}
	
	@ResponseBody
	@RequestMapping("/sys/org/cmp/companytreeloader.f")
	public List<Map<String, Object>> companyTreeLoader(String node){
		return o_orgTreeBO.companyTreeLoader(node);
	}
	
	/**
	 * 根据公司id查询公司的所有部门列表.
	 * @author 吴德福
	 * @param companyId
	 * @return List<Map<String, String>>
	 */
	@ResponseBody
	@RequestMapping("/sys/org/cmp/deptListByCompanyId.f")
	public List<Map<String, String>> findDeptListByCompanyId(String companyId){
		if(StringUtils.isBlank(companyId)){
			companyId = UserContext.getUser().getCompanyid();
		}
		
		List<Map<String, String>> datas=new ArrayList<Map<String, String>>();
		
		List<SysOrganization> orgList = o_orgCmpBO.findDeptListByCompanyId(companyId);
		if(null != orgList && orgList.size()>0){
			Map<String,String> row=null;
			for (SysOrganization org : orgList) {
				row = new HashMap<String,String>();
				row.put("id", org.getId());
				row.put("name", org.getOrgname());
				datas.add(row);
			}
		}
		return datas;
	}
	
	/**
	 * @ResponseBody
	@RequestMapping(value = "/sys/auth/user/findSysUserPageByRoleIdAndDeptId.f")
	public Map<String,Object> findSysUserPageByRoleIdAndDeptId(String query,String orgId,String roleId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		//当传入参数都为空查询结果集过大，无意义  直接返回空
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		Map<String,Object> tempMap = null;
		Page<SysEmployee> page=new Page<SysEmployee>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysEmployeeBO.findSysUserPageByDeptIdAndRoleId(page, orgId, roleId, query);
		List<SysEmployee> tempList = page.getResult();
		for(Object temp : tempList){
			Object[] obj = (Object[]) temp;
			tempMap = getMapByIdFromList(obj[0].toString(),dataList);
			if(tempMap.isEmpty()){
				dataList.add(tempMap);
				tempMap.put("roleName", obj[4]);
			}else{
				tempMap.put("roleName", tempMap.get("roleName")+"|"+obj[4]);
			}
			tempMap.put("id", obj[0]);
			tempMap.put("empcode",obj[2]);
			tempMap.put("empname", obj[1]);
			tempMap.put("orgName", obj[3]);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", dataList);
		map.put("success", true);
		return map;
	}
	 * @return
	 */
	
	
	@ResponseBody
	@RequestMapping("/sys/org/cmp/findDeptListByQuery.f")
	public Map<String,Object> findDeptListByQuery(String query, Integer limit, Integer start, String sort) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		String companyId = UserContext.getUser().getCompanyid();
		//当传入参数都为空查询结果集过大，无意义  直接返回空
		List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
		Map<String,String> tempMap = null;
		Page<SysOrganization> page=new Page<SysOrganization>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_orgCmpBO.findOrgByQuery(page, query,companyId);
		List<SysOrganization> tempList = page.getResult();
		for(SysOrganization temp : tempList){
			tempMap = new HashMap<String,String>();
			tempMap.put("id", temp.getId());
			tempMap.put("orgCode",temp.getOrgcode());
			tempMap.put("orgName",temp.getOrgname());
			dataList.add(tempMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", dataList);
		map.put("success", true);
		return map;
	}
	
	/**
	 * <pre>
	 * 根据ParentId查询Company集合
	 * </pre>
	 * d
	 * @author 张 雷
	 * @param parentId 父ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
    @RequestMapping(value = "/sys/org/findCompanyByParentId.f")
    public Map<String,Object> findCompanyByParentId(String parentId){
		return o_orgCmpBO.findCompanyByParentId(parentId);
	}
	
	/**
	 * <pre>
	 * 根据ID串，得到名称，编号，id对应的map
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param ids ID串
	 * @param type 调用类型
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
    @RequestMapping(value = "/sys/org/findDeptAndEmpByIds.f")
    public Map<String,Object> findDeptAndEmpByIds(String ids,String type){
		Set<String> orgIdSet = new HashSet<String>();
		Set<String> empIdSet = new HashSet<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(StringUtils.isNotBlank(ids)){
			JSONArray jsonArray=JSONArray.fromObject(ids);
			if(jsonArray.size()==0){
				return null;
			}
			if("dept".equals(type)){
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					if(StringUtils.isNotBlank(jsonObject.getString("id"))){
						orgIdSet.add(jsonObject.getString("id"));
					}
				}
				List<SysOrganization> orgList = o_orgCmpBO.findOrgByIdSet(orgIdSet);
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					if(StringUtils.isNotBlank(jsonObject.getString("id"))){
						for (SysOrganization org : orgList) {
							if(jsonObject.getString("id").equals(org.getId())){
								Map<String, Object> item = new HashMap<String, Object>();
								item.put("id", org.getId());
								item.put("deptno", org.getOrgcode());
								item.put("deptname", org.getOrgname());
								list.add(item);
							}
						}
					}
				}
			}else if("emp".equals(type)){
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					if(StringUtils.isNotBlank(jsonObject.getString("id"))){
						empIdSet.add(jsonObject.getString("id"));
					}
				}
				List<SysEmployee> empList = o_orgCmpBO.findEmpByIdSet(empIdSet);
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					if(StringUtils.isNotBlank(jsonObject.getString("id"))){
						for (SysEmployee emp : empList) {
							if(jsonObject.getString("id").equals(emp.getId())){
								Map<String, Object> item = new HashMap<String, Object>();
								item.put("id", emp.getId());
								item.put("empno", emp.getEmpcode());
								item.put("empname", emp.getEmpname());
								list.add(item);
							}
						}
					}
				}
			}else if("dept_emp".equals(type)){
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					if(StringUtils.isNotBlank(jsonObject.getString("deptid"))){
						orgIdSet.add(jsonObject.getString("deptid"));
						if(StringUtils.isNotBlank(jsonObject.getString("empid"))){
							empIdSet.add(jsonObject.getString("empid"));
						}
					}
				}
				List<SysOrganization> orgList = o_orgCmpBO.findOrgByIdSet(orgIdSet);
				List<SysEmployee> empList = o_orgCmpBO.findEmpByIdSet(empIdSet);
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					if(StringUtils.isNotBlank(jsonObject.getString("deptid"))){
						for (SysOrganization org : orgList) {
							if(jsonObject.getString("deptid").equals(org.getId())){
								Map<String, Object> item = new HashMap<String, Object>();
								item.put("deptid", org.getId());
								item.put("deptno", org.getOrgcode());
								item.put("deptname", org.getOrgname());
								if(StringUtils.isNotBlank(jsonObject.getString("empid"))){
									for (SysEmployee emp : empList) {
										if(jsonObject.getString("empid").equals(emp.getId())){
											item.put("empid", emp.getId());
											item.put("empno", emp.getEmpcode());
											item.put("empname", emp.getEmpname());
										}
									}
								}else{
									item.put("empid", "");
									item.put("empno", "");
									item.put("empname", "");
								}
								list.add(item);
							}
						}
					}
				}
			}
			map.put("success", true);
			map.put("data", list);
		}
		return map;
	}
	
	/**
	 * <pre>
	 * 根据员工的ID串得到员工的编号，姓名信息
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param ids 员工的ID串，多个以“,”分隔
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
    @RequestMapping(value = "/sys/org/findEmpByIds.f")
    public Map<String,Object> findEmpByIds(String ids){
		Set<String> empIdSet = new HashSet<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] idArray = ids.split(",");
		for(int i=0;i<idArray.length;i++){
			empIdSet.add(idArray[i]);
		}
		List<SysEmployee> empList = o_orgCmpBO.findEmpByIdSet(empIdSet);
		for (SysEmployee emp : empList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", emp.getId());
			item.put("empno", emp.getEmpcode());
			item.put("empname", emp.getEmpname());
			list.add(item);
		}
		map.put("success", true);
		map.put("data", list);
		return map;
	}
}