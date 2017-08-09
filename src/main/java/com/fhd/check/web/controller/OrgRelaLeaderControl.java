package com.fhd.check.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.check.business.BussinessBO;
import com.fhd.check.business.OrgRelaLeaderBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.check.BussinessOrgRelaEmp;

@Controller
public class OrgRelaLeaderControl {

	@Autowired
	private OrgRelaLeaderBO o_OrgRelaLeaderBO;
	
	@Autowired
	private BussinessBO o_BussinessBO;
	/**
	 * @author songjia
	 * @desc    保存部门和人员的对应关系
	 * @param bussinessId
	 * @param empIds
	 * @param orgIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/check/saveOrgRelaEmpInfo.f")
	public Map<String, Object> saveOrgRelaEmpInfo(String bussinessId,String empIds,String[] orgIds) {
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		o_OrgRelaLeaderBO.saveOrgRelaEmpInfo(bussinessId, empIds, orgIds);
		rtnMap.put("success", true);
		return rtnMap;
	}
	
	/**
	 * @author songjia 
	 * @desc    分页查看被管理机关和管理机关对应表
	 * @param query
	 * @param bussinessId
	 * @param manageId
	 * @param managedId
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/check/findOrgRelaEmpInfoByBussIdOrOrgId.f")
	public Map<String,Object> findOrgRelaEmpInfoByBussIdOrOrgId(String query,String bussinessId,String managePeopleId,String managedId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		//当传入参数都为空查询结果集过大，无意义  直接返回空
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		Map<String,Object> tempMap = null;
		Page<BussinessOrgRelaEmp> page=new Page<BussinessOrgRelaEmp>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_OrgRelaLeaderBO.findOrgRelaEmpInfoByBussIdOrOrgId(page, bussinessId,managePeopleId, managedId, query);
		List<BussinessOrgRelaEmp> tempList = page.getResult();
		for(BussinessOrgRelaEmp temp : tempList){
			tempMap = new HashMap<String,Object>();
			tempMap.put("id", temp.getId());
			tempMap.put("bussinessName",temp.getBussinessId().getName());
			tempMap.put("empName", temp.getEmp().getEmpname());
			tempMap.put("managedName", temp.getManagedOrgId().getOrgname());
			tempMap.put("manageName", temp.getManageOrgId().getOrgname());
			dataList.add(tempMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", dataList);
		map.put("success", true);
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/check/deleteRelaOrgByIds.f")
	public Map<String,Object> deleteRelaOrgByIds(String[] ids){
		o_OrgRelaLeaderBO.deleteOrgRelaEmp(ids);
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		rtnMap.put("success", true);
		return rtnMap;
	}
	
	/**
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/check/findEmpFromLeaderRelaOrgByAll.f")
	public List<Map<String,String>> findEmpFromLeaderRelaOrgByAll(){
		return o_OrgRelaLeaderBO.findEmpFromLeaderRelaOrgByAll();
	}
	
	@ResponseBody
	@RequestMapping(value = "/check/findBussinessByAll.f")
	public List<Map<String,String>> findBussinessByAll(){
		return o_BussinessBO.findBussinessByAll();
	}
}