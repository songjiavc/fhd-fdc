package com.fhd.ra.web.controller.assess.risktidy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.oper.OrgAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.StrategyAdjustHistoryBO;
import com.fhd.ra.business.risk.ProcessAdjustHistoryBO;
/**
 * 根据评价计划ID 获取此ID下的风险统计信息
 * @author 邓广义
 *
 */
@Controller
public class RiskAssessCountController {
	@Autowired
	private OrgAdjustHistoryBO o_orgAdjustHistoryBO;
	@Autowired
	private ProcessAdjustHistoryBO o_processAdjustHistoryBO;
	@Autowired
	private StrategyAdjustHistoryBO o_strategyAdjustHistoryBO;
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	/**
	 * 组织统计
	 * @param assessPlanId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/organizationcountgrid.f")
	public Map<String,Object> organizationCountGrid(String assessPlanId,String query){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> orgList = o_orgAdjustHistoryBO.findParentOrgRisks(assessPlanId,query);
		if(orgList.size()>0){
			map.put("children", orgList);
			map.put("linked", true);
		}
		return map;
	}
	
	/**
	 * 流程统计
	 * @param assessPlanId
	 * @param query
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/processcountgrid.f")
	public Map<String,Object> processCountGrid(String assessPlanId,String query){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> processList = o_processAdjustHistoryBO.findParentProcessRisks(assessPlanId,query);
		if(processList.size()>0){
			map.put("children", processList);
			map.put("linked", true);
		}
		return map;
	}
	
	/**
	 * 目标统计
	 * @param assessPlanId
	 * @param query
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/kpicountgrid.f")
	public Map<String,Object> kpiCountGrid(String assessPlanId,String query){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> kpiList = o_strategyAdjustHistoryBO.findParentStrategyRisks(assessPlanId,query);
		if(kpiList.size()>0){
			map.put("children", kpiList);
			map.put("linked", true);
		}
		return map;
	}
	
	/**
	 * 分类统计
	 * @param assessPlanId
	 * @param query
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/riskcountgrid.f")
	public Map<String,Object> riskCountGrid(String assessPlanId,String query){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> riskList = o_riskAdjustHistoryBO.findParentRisksRbs(assessPlanId,query);
		if(riskList.size()>0){
			map.put("children", riskList);
			map.put("linked", true);
		}
		return map;
	}
	
	/**
	 * 分类统计导出
	 * add by 王再冉
	 * 2013-12-27  下午3:00:17
	 * desc : 
	 * @param businessId
	 * @param exportFileName
	 * @param sheetName
	 * @param headerData
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/exportriskcountgrid.f")
	public void exportRiskCountGrid(String businessId, String exportFileName, String sheetName,
			String headerData, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		List<Map<String, Object>> riskList = o_riskAdjustHistoryBO.findParentRisksRbs(businessId,null);
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		
		List<Object[]> list = o_riskAdjustHistoryBO.exportRiskCountGrid(riskList, indexList, j);
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "分类统计数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 导出组织统计列表
	 * add by 王再冉
	 * 2013-12-27  下午5:01:57
	 * desc : 
	 * @param businessId
	 * @param exportFileName
	 * @param sheetName
	 * @param headerData
	 * @param request
	 * @param response
	 * @throws Exception 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/exportorgcountgrid.f")
	public void exportOrgCountGrid(String businessId, String exportFileName, String sheetName,
			String headerData, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		List<Map<String, Object>> orgList = o_orgAdjustHistoryBO.findParentOrgRisks(businessId,null);
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		List<Object[]> list = o_orgAdjustHistoryBO.exportOrgCountGrid(orgList, indexList, j);
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "组织统计数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 流程列表导出
	 * add by 王再冉
	 * 2013-12-27  下午5:13:16
	 * desc : 
	 * @param businessId
	 * @param exportFileName
	 * @param sheetName
	 * @param headerData
	 * @param request
	 * @param response
	 * @throws Exception 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/exportprocesscountgrid.f")
	public void exportProcessCountGrid(String businessId, String exportFileName, String sheetName,
			String headerData, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		List<Map<String, Object>> processList = o_processAdjustHistoryBO.findParentProcessRisks(businessId,null);
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		List<Object[]> list = o_processAdjustHistoryBO.exportProcessCountGrid(processList, indexList, j);
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "流程统计数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 导出目标统计列表
	 * add by 王再冉
	 * 2013-12-27  下午5:19:18
	 * desc : 
	 * @param businessId
	 * @param exportFileName
	 * @param sheetName
	 * @param headerData
	 * @param request
	 * @param response
	 * @throws Exception 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value="/assess/riskTidy/exportkpicountgrid.f")
	public void exportKpiCountGrid(String businessId, String exportFileName, String sheetName,
			String headerData, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		List<Map<String, Object>> kpiList = o_strategyAdjustHistoryBO.findParentStrategyRisks(businessId,null);
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		List<Object[]> list = o_strategyAdjustHistoryBO.exportKpiCountGrid(kpiList, indexList, j);
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "目标统计数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
}
