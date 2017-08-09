package com.fhd.ra.web.controller.risk;

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

import com.fhd.core.dao.Page;
import com.fhd.entity.risk.Risk;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.assess.summarizing.util.SummarizinggAtherUtilBO;
import com.fhd.ra.business.risk.PotentialRiskEventBO;
import com.fhd.ra.business.risk.RiskBO;

@Controller
public class PotentialRiskEventControl {
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private PotentialRiskEventBO o_potentialRiskEventBO;
	
	@Autowired
	private ShowRiskTidyBO o_showRiskTidyBO;
	
	@Autowired
	private SummarizinggAtherUtilBO o_summarizinggAtherUtilBO;
	
   /**
	 * 风险树组件初始化
	 * @author 郑军祥
	*/
	@ResponseBody
    @RequestMapping(value = "/risk/initPotentialRiskEventByIds")
    public Map<String,Object> initPotentialRiskEventByIds(String ids) throws Exception{

		Set<String> idSet = new HashSet<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if(StringUtils.isNotBlank(ids)){
			JSONArray jsonArray=JSONArray.fromObject(ids);
			if(jsonArray.size()==0){
				return null;
			}
			
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject=jsonArray.getJSONObject(i);
				if(StringUtils.isNotBlank(jsonObject.getString("id"))){
					idSet.add(jsonObject.getString("id"));
				}
			}
			list = o_riskBO.findRiskByIdSet(idSet);
			
			map.put("success", true);
			map.put("data", list);
		}
		return map;
	}
		
	/**
	 * 查询组织下的潜在风险事件
	 * 
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/potentialRiskEvent/findPotentialRiskEventByOrgId")
	public Map<String, Object> findPotentialRiskEventByOrgId(String id,
			String query) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Risk> page = new Page<Risk>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		page = o_riskBO.findRiskEventByOrgId(id, page, null, null, query);
		List<Risk> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Risk risk : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", risk.getId());
			item.put("code", risk.getCode());
			item.put("name", risk.getName());
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}

	/**
	 * 查询指标下的潜在风险事件
	 * 
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/potentialRiskEvent/findPotentialRiskEventByKpiId")
	public Map<String, Object> findPotentialRiskEventByKpiId(String id,
			String query) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Risk> page = new Page<Risk>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		page = o_riskBO.findRiskEventByKpiId(id, page, null, null, query);
		List<Risk> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Risk risk : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", risk.getId());
			item.put("code", risk.getCode());
			item.put("name", risk.getName());
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}

	/**
	 * 查询流程下的潜在风险事件
	 * 
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/potentialRiskEvent/findPotentialRiskEventByProcessId")
	public Map<String, Object> findPotentialRiskEventByProcessId(String id,
			String query) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Risk> page = new Page<Risk>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		page = o_riskBO.findRiskEventByProcessId(id, page, null, null, query);
		List<Risk> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Risk risk : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", risk.getId());
			item.put("code", risk.getCode());
			item.put("name", risk.getName());
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}

	/**
	 * 查询风险下的潜在风险事件
	 * 
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/potentialRiskEvent/findPotentialRiskEventByRiskId")
	public Map<String, Object> findPotentialRiskEventByRiskId(String id,
			String query) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Risk> page = new Page<Risk>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		page = o_riskBO.findRiskEventBySome(id, page, null, null, query);
		List<Risk> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Risk risk : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", risk.getId());
			item.put("code", risk.getCode());
			item.put("name", risk.getName());
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}
	
	

    /** 
      * @Description:  通过计划中的风险事件反射出对应的上级风险分类
      * @author jia.song@pcitc.com
      * @date 2017年4月25日 下午2:00:39 
      * @param node
      * @param query
      * @param ids
      * @param assessPlanId
      * @return 
      */
    @ResponseBody
	@RequestMapping(value = "/potentialRiskEvent/getRiskTreeRecordByEventIds")
    public List<Map<String, Object>> getRiskTreeRecordByEventIdsSf2(String node, String query,String ids, String assessPlanId){
    	//String companyId = UserContext.getUser().getCompanyid();
    	/*
    	ids = o_showRiskTidyBO.findRiskRbsOrReList(assessPlanId, companyId).toString();
    	JSONArray array = JSONArray.fromObject(ids);
    	Map<String,String> riskMap = new HashMap<String,String>();
    	String[] eventIds = new String[array.size()];	//风险事件ID数组
    	for(int i=0;i<array.size();i++){
    		JSONObject obj = (JSONObject)array.get(i);
			String id = obj.getString("id");
    		if(obj.getString("type").equalsIgnoreCase("re")){
        		eventIds[i] = id;
    		}
    		riskMap.put(id, obj.getString("icon"));
    	}
    	*/
    	
    	return o_showRiskTidyBO.getRiskTreeRecordByEventIds(node, query,assessPlanId);
    }
	
    /** 
     * @Description:  通过计划中的风险事件反射出对应的上级风险分类
     * @author jia.song@pcitc.com
     * @date 2017年4月25日 下午2:00:39 
     * @param node
     * @param query
     * @param ids
     * @param assessPlanId
     * @return 
     */
   @ResponseBody
	@RequestMapping(value = "/potentialRiskEvent/getRiskTreeRecordByEventIdsForSecurity")
   public List<Map<String, Object>> getRiskTreeRecordByEventIdsForSecurity(String node, String query,String ids, String assessPlanId){
   	String companyId = UserContext.getUser().getCompanyid();
   	return o_showRiskTidyBO.getRiskTreeRecordByEventIdsForSecurity(node, query,assessPlanId,companyId);
   }
    
	/**
	 * 风险树,按风险事件id数组过滤
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param ids			风险id串 “2,3”
	 * @return
	 */
    /*
    @ResponseBody
	@RequestMapping(value = "/potentialRiskEvent/getRiskTreeRecordByEventIds")
    public List<Map<String, Object>> getRiskTreeRecordByEventIds(String node, String query,String ids, String assessPlanId){
    	String companyId = UserContext.getUser().getCompanyid();
    	ids = o_showRiskTidyBO.findRiskRbsOrReList(assessPlanId, companyId).toString();
    	
    	JSONArray array = JSONArray.fromObject(ids);
    	Map<String,String> riskMap = new HashMap<String,String>();
    	String[] eventIds = new String[array.size()];	//风险事件ID数组
    	for(int i=0;i<array.size();i++){
    		JSONObject obj = (JSONObject)array.get(i);
			String id = obj.getString("id");
    		if(obj.getString("type").equalsIgnoreCase("re")){
        		eventIds[i] = id;
    		}
    		riskMap.put(id, obj.getString("icon"));
    	}
    	return o_potentialRiskEventBO.getRiskTreeRecordByEventIds(node, query, eventIds, riskMap);
    }
    */
    /**
	 * 组织树,按风险事件id数组过滤
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param ids			风险id串 “2,3”
	 * @return
	 */
    @ResponseBody
    @RequestMapping("/potentialRiskEvent/getOrgTreeRecordByEventIds")
    public List<Map<String, Object>> getOrgTreeRecordByEventIds(String node, String query,String ids, String assessPlanId) {
    	if(StringUtils.isBlank(ids)){
    		return null;
    	}
    	
    	String[] eventIds = ids.split(",");
    	HashMap<String, String> nodeMap = o_summarizinggAtherUtilBO.findDeptByCompanyIdNewAllMap(assessPlanId);
    	return o_potentialRiskEventBO.getOrgTreeRecordByEventIds(node, query, eventIds,nodeMap);
    }
    
	/**
	 * 目标树,按风险事件id数组过滤
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param ids			风险id串 “2,3”
	 * @return
	 */
    @ResponseBody
    @RequestMapping("/potentialRiskEvent/getStrategyMapTreeRecordByEventIds")
    public List<Map<String, Object>> getStrategyMapTreeRecordByEventIds(String node, String query,String ids, String assessPlanId) {
    	if(StringUtils.isBlank(ids)){
    		return null;
    	}
    	String[] eventIds = ids.split(",");
    	HashMap<String, String> nodeMap = o_summarizinggAtherUtilBO.findStrategyByCompanyIdNewAllMap(assessPlanId);
    	HashMap<String, String> kpiMap = o_summarizinggAtherUtilBO.findKpiByCompanyIdNewAllMap(assessPlanId);
    	return o_potentialRiskEventBO.getStrategyMapTreeRecordByEventIds(node, query, eventIds,nodeMap,kpiMap);
    }
    
    /**
	 * 流程树,按风险事件id数组过滤
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param ids			风险id串 “2,3”
	 * @return
	 */
    @ResponseBody
    @RequestMapping("/potentialRiskEvent/getProcessTreeRecordByEventIds")
    public List<Map<String, Object>> getProcessTreeRecordByEventIds(String node, String query,String ids, String assessPlanId) {
    	if(StringUtils.isBlank(ids)){
    		return null;
    	}
    	String[] eventIds = ids.split(",");
    	HashMap<String, String> nodeMap = o_summarizinggAtherUtilBO.findProcessByCompanyIdNewAllMap(assessPlanId);
    	return o_potentialRiskEventBO.getProcessTreeRecordByEventIds(node, query, eventIds,nodeMap);
    }
}
