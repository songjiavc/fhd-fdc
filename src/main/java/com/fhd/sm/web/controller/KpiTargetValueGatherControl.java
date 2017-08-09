package com.fhd.sm.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.sm.business.KpiTargetValueGatherPlanBO;
import com.fhd.sys.business.orgstructure.OrgTreeBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

@Controller
public class KpiTargetValueGatherControl {
	
	@Autowired
	private KpiTargetValueGatherPlanBO o_kpiTargetValueGatherPlanBO;
	
	@Autowired
	private OrgTreeBO o_orgTreeBO;
	
	@Autowired
	private OrganizationBO o_organizationBO;
    
	/**
	 * 统计考核计划中各个部门的考核指标
	 * @param id 考核计划id
	 * @return 部门考察目标统计
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/plan/findstatisticdatabyplanid.f")
	public Map<String, Object> findPlanStatisticDataById(String planId,String query) {
		   Map<String, Object> data = new HashMap<String, Object>();
		   List<Map<String,Object>> datas = null;
		   datas =  o_kpiTargetValueGatherPlanBO.findPlanStatisticDataById(planId,query);
		   data.put("datas", datas);
		   return data;
	}
	
	/**
	 * 更新考核结果
	 * @param param 考核计划id与指标Id Json对象
	 * @return 更新结果
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/kpi/plan/mergeplanrelakpi.f")
	public Map<String, Object> mergePlanRelaKpi(String param) {
		   Map<String, Object> map  = new HashMap<String,Object>();

	        if (StringUtils.isNotBlank(param)) {
	            JSONObject jsobj = JSONObject.fromObject(param);
	            String planId = jsobj.getString("planId");
	            JSONArray kpiids = jsobj.getJSONArray("kpiIds");
	            
	            // 存在新增的考核指标时 
	            if(null != kpiids && jsobj.size() > 0) {
	            	List<String> kpiIdList = new ArrayList<String>();
	            	for(Object kpiId : kpiids) {
	            		kpiIdList.add((String) kpiId);
	            	}
	               o_kpiTargetValueGatherPlanBO.mergePlanRelaKpi(planId,kpiids);
	            }	           
	        }
			   map.put("success", true);
		   return map;
	}
	/**
	 * 查询考核计划下制定部门的考核明细
	 * @param deptId 部门Id
	 * @param planId 考核计划Id
	 * @return 部门考核指标
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/plan/findDeptPlanKpiById.f")
	public Map<String, Object> findDeptPlanKpiById(String deptId,String planId,String gatherType) {
		   Map<String, Object> data = new HashMap<String, Object>();
		   List<Map<String,Object>> datas = null;
		   datas =  o_kpiTargetValueGatherPlanBO.findDeptPlanKpiById(planId,deptId,gatherType);
		   data.put("datas", datas);
		   return data;
	}
	
	/**
	 * 删除考核指标
	 * @param planId 计划Id
	 * @param ids 指标Id
	 * @return 删除结果
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/plan/removeplanrelakpiByIds.f")
	public Map<String, Object> removePlanRelaKpiByIds(String planId,String ids) {
		 Map<String, Object> map = new HashMap<String, Object>();
		 o_kpiTargetValueGatherPlanBO.removePlanRelaKpiByIds(planId,ids);
		 map.put("success", true);
	     return map;
	}
	
	/**
	 * 查询考核计划的考核时间
	 * @param planId 计划ID
	 * @return 考核时间
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/plan/findplanchecktimebyid.f")
	public Map<String, Object> findplanChecktimeById(String planId) {
		 Map<String, Object> map = null;
		 map = o_kpiTargetValueGatherPlanBO.findplanChecktimeById(planId);
		 map.put("success", true);
	     return map;
	}
	
	/**
	 * 保存考核时间
	 * @param planId 考核计划id
	 * @param startTime 考核时间
	 * @return 保存是否成功
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value ="/access/planconform/saveplanbycheckTime.f") 
	public Map<String, Object> saveplanbycheckTime(String planId,String startTime,String checkType) throws ParseException  {
		   Map<String, Object> map = new HashMap<String,Object>();
		   o_kpiTargetValueGatherPlanBO.saveplanbycheckTime(planId,startTime,checkType);
		   map.put("success",true);
		   return map;
	}
	
    /**
     * 
     * @param planId 考核计划id
     * @param startTime
     * @param checkType 考核
     * @param gatherType
     * @throws ParseException 
     */
	@ResponseBody
	@RequestMapping(value = "/kpi/plan/startKpiGahterProcess.f")
	public Map<String, Object> startKpiGahterProcess(String planId,String startTime,String checkType,String gatherType) throws ParseException{
		  Map<String, Object> map = new HashMap<String,Object>();
		o_kpiTargetValueGatherPlanBO.startKpiGahterProcess(planId,startTime,checkType,gatherType);
		   map.put("success",true);
		   return map;
	}
	/**
	 * 部门树加载
	 * @param node 加载部门指标树
	 * @param checkable 是否有复选框
	 * @param subCompany 是否检查出子公司
	 * @param chooseId 选中部门id
	 * @param query 部门模糊查询
	 * @param companyOnly 是否只查出公司
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/plan/depttreeloader.f")
	public List<Map<String, Object>> depttreeloader(String node,Boolean checkable,Boolean subCompany,String chooseId,String query, Boolean companyOnly,String deptId) {
		if(StringUtils.isBlank(deptId)){
			return o_orgTreeBO.kpiDeptTreeLoader(checkable, node, subCompany, chooseId, query, companyOnly);
		}
		else {
			List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
			Map<String, Object> dn = new HashMap<String, Object>();
			dn.put("id", deptId);
			dn.put("dbid", deptId);
			dn.put("text", o_organizationBO.findById(deptId).getOrgname());
			dn.put("leaf", true);
			dn.put("cls", "org");
			dn.put("type","kpi_dept");
			nodes.add(dn);
			return nodes;
		}
		
	}
}
