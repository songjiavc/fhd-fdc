package com.fhd.sm.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.CategoryReportBO;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.sm.business.KpiRiskRelaReportBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sm.business.StrategyMapReportBO;

@Controller
public class MonitorReportControl {
	
   @Autowired
   private StrategyMapBO o_strategyMapBO;
   
   @Autowired
   private CategoryReportBO o_categoryReportBO;
     
   @Autowired
   private StrategyMapReportBO o_strategyMapReportBO;
   
   @Autowired
   private KpiRiskRelaReportBO o_kpiRiskRelaReportBO;
   
   /**
    * 
    * @param node
    * @param query
    * @param request
    * @return
    */
   @ResponseBody
   @RequestMapping(value = "/kpi/monitorReport/monitorreporttreeloader.f")
   public List<Map<String, Object>> monitorreporttreeloader(String node, String query,HttpServletRequest request) {
	   List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();//返回前台的nodelist
       Map<String, Object> smKpiNode = new HashMap<String, Object>();
       smKpiNode.put("id", "smKpi");
       smKpiNode.put("text", "目标监控报表");
       smKpiNode.put("type", "smKpi");
       smKpiNode.put("leaf", true);
       smKpiNode.put("iconCls", "icon-strategy");
       nodes.add(smKpiNode);
   	   Map<String, Object> scKpiNode = new HashMap<String, Object>();
   	   scKpiNode.put("id", "scKpi");
   	   scKpiNode.put("text", "记分卡报表");
   	   scKpiNode.put("type", "scKpi");
   	   scKpiNode.put("leaf", true);
   	   scKpiNode.put("iconCls", "icon-ibm-icon-scorecards");
       nodes.add(scKpiNode);
   	   Map<String, Object> riskKpiNode = new HashMap<String, Object>();
	   	riskKpiNode.put("id", "riskKpi");
	   	riskKpiNode.put("text", "风险监控报表");
	   	riskKpiNode.put("type", "riskKpi");
	   	riskKpiNode.put("leaf", true);
	   	riskKpiNode.put("iconCls", "icon-server-connect");
       nodes.add(riskKpiNode);
	   return nodes;
   }
   
   /**
    * 
    * @param start
    * @param limit
    * @param query
    * @param sort
    * @param year
    * @param quarter
    * @param month
    * @param week
    * @param eType
    * @param isNewValue
    * @return
    */
   @ResponseBody
   @RequestMapping(value = "/kpi/report/findAllKpiSmDetail.f")
   public Map<String, Object> findAllSmKpiDetail(int start, int limit, String query, String sort, String year, String quarter,
           String month, String week, String eType, String isNewValue) {
       year = ",".equals(year) ? "" : year;
       quarter = ",".equals(quarter) ? "" : quarter;
       month = ",".equals(month) ? "" : month;
       week = ",".equals(week) ? "" : week;
       eType = ",".equals(eType) ? "" : eType;
       isNewValue = ",".equals(isNewValue) ? "" : isNewValue;
       if (null != year && year.contains(",")) {
           year = year.replace(",", "");
       }
       if (null != quarter && quarter.contains(",")) {
           quarter = quarter.replace(",", "");
       }
       if (null != month && month.contains(",")) {
           month = month.replace(",", "");
       }
       if (null != week && week.contains(",")) {
           week = week.replace(",", "");
       }
       if (null != eType && eType.contains(",")) {
           eType = eType.replace(",", "");
       }
       if (null != isNewValue && isNewValue.contains(",")) {
           isNewValue = isNewValue.replace(",", "");
       }
       String dir = "DESC";
       String sortColumn = "assessmentStatus";// 默认排序
       if (StringUtils.isNotBlank(sort)) {
           JSONArray jsonArray = JSONArray.fromObject(sort);
           if (jsonArray.size() > 0) {
               JSONObject jsobj = jsonArray.getJSONObject(0);
               sortColumn = jsobj.getString("property");// 按照哪个字段排序
               dir = jsobj.getString("direction");// 排序方向
           }
       }
       String lastflag = isNewValue;
       String frequence = eType;
       Map<String, Object> map = new HashMap<String, Object>();
       List<Map<String, Object>> datas = null;
       if ("true".equals(lastflag)) {// 最新值查询
    	   datas = o_strategyMapBO.findLastSmKpiResults(map, start, limit, query, sortColumn, dir);
       }else {
    	   datas = o_strategyMapBO.findAllSmKpiDetail(map, start, limit, query, sortColumn, dir, year, quarter, month, week,
                   frequence);
       }
       map.put("datas", datas);
       return map;
   }
   
   /**
    * 
    * @param start
    * @param limit
    * @param query
    * @param sort
    * @param year
    * @param quarter
    * @param month
    * @param week
    * @param eType
    * @param isNewValue
    * @return
    */
   @ResponseBody
   @RequestMapping(value = "/kpi/report/findAllKpiScDetail.f")
   public Map<String, Object> findAllScKpiDetail(int start, int limit, String query, String sort, String year, String quarter,
           String month, String week, String eType, String isNewValue){
           year = ",".equals(year) ? "" : year;
           quarter = ",".equals(quarter) ? "" : quarter;
           month = ",".equals(month) ? "" : month;
           week = ",".equals(week) ? "" : week;
           eType = ",".equals(eType) ? "" : eType;
           isNewValue = ",".equals(isNewValue) ? "" : isNewValue;
           if (null != year && year.contains(",")) {
               year = year.replace(",", "");
           }
           if (null != quarter && quarter.contains(",")) {
               quarter = quarter.replace(",", "");
           }
           if (null != month && month.contains(",")) {
               month = month.replace(",", "");
           }
           if (null != week && week.contains(",")) {
               week = week.replace(",", "");
           }
           if (null != eType && eType.contains(",")) {
               eType = eType.replace(",", "");
           }
           if (null != isNewValue && isNewValue.contains(",")) {
               isNewValue = isNewValue.replace(",", "");
           }
           String dir = "DESC";
           String sortColumn = "assessmentStatus";// 默认排序
           if (StringUtils.isNotBlank(sort)) {
               JSONArray jsonArray = JSONArray.fromObject(sort);
               if (jsonArray.size() > 0) {
                   JSONObject jsobj = jsonArray.getJSONObject(0);
                   sortColumn = jsobj.getString("property");// 按照哪个字段排序
                   dir = jsobj.getString("direction");// 排序方向
               }
           }
           String lastflag = isNewValue;
           String frequence = eType;
           Map<String, Object> map = new HashMap<String, Object>();
           List<Map<String, Object>> datas = null;
           if ("true".equals(lastflag)) {// 最新值查询
        	   datas = o_categoryReportBO.findLastScKpiDetailResults(map, start, limit, query, sortColumn, dir);
           }else {
        	   datas = o_categoryReportBO.findSpecificScKpiDetailResults(map, start, limit, query, sortColumn, dir, year, quarter, month, week,
                       frequence);
           }
           map.put("datas", datas);
           return map;
   }
   
   /**
    * 战略目标报表导出
    * @param id
    * @param exportFileName
    * @param sheetName
    * @param headerData
    * @param timePeriod
    * @param request
    * @param response
    * @throws Exception
    */
   @ResponseBody
   @RequestMapping(value = "kpi/report/exportsmkpireport.f")
   public void exportsmkpireport(String id,String exportFileName, 
           String sheetName,  String headerData, String timePeriod,
           HttpServletRequest request, HttpServletResponse response) throws Exception  {
       List<Object[]> list = new ArrayList<Object[]>();
       List<String> indexList = new ArrayList<String>();//列ID
       List<Map<String,Object>> listMap = null;//列表数据
       JSONObject jsonObject = JSONObject.fromObject(timePeriod);
       String year = jsonObject.getString("year");
       String quarter = jsonObject.getString("quarter");
       String month = jsonObject.getString("month");
       String week = jsonObject.getString("week");
       String eType = jsonObject.getString("eType");
       String isNewValue = jsonObject.getString("isNewValue");
       String[] fieldTitle = null;
       JSONArray headerArray=JSONArray.fromObject(headerData);
       int j = headerArray.size();
       fieldTitle = new String[j];
       for(int i=0;i<j;i++){
           JSONObject jsonObj = headerArray.getJSONObject(i);
           fieldTitle[i] = jsonObj.get("text").toString();
           indexList.add(jsonObj.get("dataIndex").toString());
       }
       String lastflag = isNewValue;
       String frequence = eType;
       Map<String, Object> map = new HashMap<String, Object>();
       if ("true".equals(lastflag)) {// 最新值查询
    	   listMap = o_strategyMapBO.findLastSmKpiResults(map, -1, 0, null, null, null);
       }else {
    	   listMap = o_strategyMapBO.findAllSmKpiDetail(map, -1, 0, null, null, null, year, quarter, month, week,
                   frequence);
       }
       for(Map<String,Object> maps : listMap){
    	   Object[] object = new Object[j];
           if("icon-ibm-symbol-4-sm".equals(maps.get("smStatus"))) {
               object[2] = "color|red";
           }else if("icon-ibm-symbol-6-sm".equals(maps.get("smStatus"))){
               object[2] = "color|green";
           }else if("icon-ibm-symbol-5-sm".equals(maps.get("smStatus"))){
               object[2] = "color|yellow";
           }else if("icon-ibm-symbol-0-sm".equals(maps.get("smStatus"))){
               object[2] = "color|";
           }else{
               object[2] = null;
           }
          
           object[0] = maps.get("smName");
           if(null !=  maps.get("smAssessValue")) {
        	   object[1] = maps.get("smAssessValue").toString();
           } else {
        	   object[1] = null;
           }           
          // object[2] = maps.get("smStatus");
           if(null !=  maps.get("kpiWeight")){
        	   object[3] = maps.get("kpiWeight").toString();   
           } else {
        	   object[3] = null;
           }           
           object[4] = maps.get("kpiName");
           object[5] = maps.get("deptName");
           object[6] = maps.get("kpiFrequency");
           object[7] = maps.get("targetValue");
           object[8] = maps.get("finishValue");
           if(null != maps.get("assessMentValue")) {
        	   object[9] = maps.get("assessMentValue").toString();  
           } else {
        	   object[9] = null;
           }
           if("icon-ibm-symbol-4-sm".equals(maps.get("status"))) {
               object[10] = "color|red";
           }else if("icon-ibm-symbol-6-sm".equals(maps.get("status"))){
               object[10] = "color|green";
           }else if("icon-ibm-symbol-5-sm".equals(maps.get("status"))){
               object[10] = "color|yellow";
           }else if("icon-ibm-symbol-0-sm".equals(maps.get("status"))){
               object[10] = "color|";
           }else{
               object[10] = null;
           }
           //object[10] = maps.get("status");           
           object[11] = maps.get("timePeriod");
           list.add(object);
       }
       
       if(StringUtils.isBlank(exportFileName)){
           exportFileName = "目标监控报表.xls";
       }
       if(StringUtils.isBlank(sheetName)){
           sheetName = "目标监控";
       }
       //数据，列名称，文件名称，sheet名称，sheet位置
       ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
   }
  
   /**
    * 记分卡报表导出
    * @param id
    * @param exportFileName
    * @param sheetName
    * @param headerData
    * @param timePeriod
    * @param request
    * @param response
    * @throws Exception
    */
   @ResponseBody
   @RequestMapping(value = "kpi/report/exportsckpireport.f")
   public void exportsckpireport(String id,String exportFileName, 
           String sheetName,  String headerData, String timePeriod,
           HttpServletRequest request, HttpServletResponse response) throws Exception {
       List<Object[]> list = new ArrayList<Object[]>();
       List<String> indexList = new ArrayList<String>();//列ID
       List<Map<String,Object>> listMap = null;//列表数据
       JSONObject jsonObject = JSONObject.fromObject(timePeriod);
       String year = jsonObject.getString("year");
       String quarter = jsonObject.getString("quarter");
       String month = jsonObject.getString("month");
       String week = jsonObject.getString("week");
       String eType = jsonObject.getString("eType");
       String isNewValue = jsonObject.getString("isNewValue");
       String[] fieldTitle = null;
       JSONArray headerArray=JSONArray.fromObject(headerData);
       int j = headerArray.size();
       fieldTitle = new String[j];
       for(int i=0;i<j;i++){
           JSONObject jsonObj = headerArray.getJSONObject(i);
           fieldTitle[i] = jsonObj.get("text").toString();
           indexList.add(jsonObj.get("dataIndex").toString());
       }
       String lastflag = isNewValue;
       String frequence = eType;
       Map<String, Object> map = new HashMap<String, Object>();
       if ("true".equals(lastflag)) {// 最新值查询
    	   listMap = o_categoryReportBO.findLastScKpiDetailResults(map, -1, 0, null, null, null);
       }else {
    	   listMap = o_categoryReportBO.findSpecificScKpiDetailResults(map, -1, 0, null, null, null, year, quarter, month, week,
                   frequence);
       }
       for(Map<String,Object> maps : listMap){
    	   Object[] object = new Object[j];
           if("icon-ibm-symbol-4-sm".equals(maps.get("scStatus"))) {
               object[2] = "color|red";
           }else if("icon-ibm-symbol-6-sm".equals(maps.get("scStatus"))){
               object[2] = "color|green";
           }else if("icon-ibm-symbol-5-sm".equals(maps.get("scStatus"))){
               object[2] = "color|yellow";
           }else if("icon-ibm-symbol-0-sm".equals(maps.get("scStatus"))){
               object[2] = "color|";
           }else{
               object[2] = null;
           }
          
           object[0] = maps.get("scName");
           if(null !=  maps.get("scAssessValue")) {
        	   object[1] = maps.get("scAssessValue").toString();
           } else {
        	   object[1] = null;
           }           
          // object[2] = maps.get("smStatus");
           if(null !=  maps.get("kpiWeight")){
        	   object[3] = maps.get("kpiWeight").toString();   
           } else {
        	   object[3] = null;
           }           
           object[4] = maps.get("kpiName");
           object[5] = maps.get("deptName");
           object[6] = maps.get("kpiFrequency");
           object[7] = maps.get("targetValue");
           object[8] = maps.get("finishValue");
           if(null != maps.get("assessMentValue")) {
        	   object[9] = maps.get("assessMentValue").toString();  
           } else {
        	   object[9] = null;
           }
           if("icon-ibm-symbol-4-sm".equals(maps.get("status"))) {
               object[10] = "color|red";
           }else if("icon-ibm-symbol-6-sm".equals(maps.get("status"))){
               object[10] = "color|green";
           }else if("icon-ibm-symbol-5-sm".equals(maps.get("status"))){
               object[10] = "color|yellow";
           }else if("icon-ibm-symbol-0-sm".equals(maps.get("status"))){
               object[10] = "color|";
           }else{
               object[10] = null;
           }
           //object[10] = maps.get("status");           
           object[11] = maps.get("timePeriod");
           list.add(object);
       }
       
       if(StringUtils.isBlank(exportFileName)){
           exportFileName = "公司记分卡.xls";
       }
       if(StringUtils.isBlank(sheetName)){
           sheetName = "记分卡";
       }
       //数据，列名称，文件名称，sheet名称，sheet位置
       ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
   }
   /**
    * 
    * @param node
    * @param year
    * @param quarter
    * @param month
    * @param week
    * @param eType
    * @param isNewValue
    * @param query
    * @return
    */
   @ResponseBody
   @RequestMapping(value ="/kpi/report/scKpiReportTreeLoader.f")
   public Map<String, Object> scKpiReportTreeLoader(String node, String year, String quarter,
           String month, String week, String eType, String isNewValue,String query) {
	   Map<String, Object> map = new HashMap<String, Object>();
	   List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	   year = ",".equals(year) ? "" : year;
       quarter = ",".equals(quarter) ? "" : quarter;
       month = ",".equals(month) ? "" : month;
       week = ",".equals(week) ? "" : week;
       eType = ",".equals(eType) ? "" : eType;
       isNewValue = ",".equals(isNewValue) ? "" : isNewValue;
       if (null != year && year.contains(",")) {
           year = year.replace(",", "");
       }
       if (null != quarter && quarter.contains(",")) {
           quarter = quarter.replace(",", "");
       }
       if (null != month && month.contains(",")) {
           month = month.replace(",", "");
       }
       if (null != week && week.contains(",")) {
           week = week.replace(",", "");
       }
       if (null != eType && eType.contains(",")) {
           eType = eType.replace(",", "");
       }
       if (null != isNewValue && isNewValue.contains(",")) {
           isNewValue = isNewValue.replace(",", "");
       }
       if ("true".equals(isNewValue)) {
    	  list  =  o_categoryReportBO.scKpiReportTreeLoader(node, query);
       } else if("false".equals(isNewValue)) {
    	  list =   o_categoryReportBO.scKpiReportTreeLoader(node, query, year, quarter, month, week,
    			  eType);
       }
       map.put("children", list);
	   return map;
   }
   
   /**
    * 
    * @param node
    * @param year
    * @param quarter
    * @param month
    * @param week
    * @param eType
    * @param isNewValue
    * @param query
    * @return
    */
   @ResponseBody
   @RequestMapping(value = "/kpi/report/smKpiReportTreeLoader.f")
   public Map<String, Object> smKpiReportTreeLoader(String node, String year, String quarter,
           String month, String week, String eType, String isNewValue,String query) {
	   Map<String, Object> map = new HashMap<String, Object>();
	   List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	   year = ",".equals(year) ? "" : year;
       quarter = ",".equals(quarter) ? "" : quarter;
       month = ",".equals(month) ? "" : month;
       week = ",".equals(week) ? "" : week;
       eType = ",".equals(eType) ? "" : eType;
       isNewValue = ",".equals(isNewValue) ? "" : isNewValue;
       if (null != year && year.contains(",")) {
           year = year.replace(",", "");
       }
       if (null != quarter && quarter.contains(",")) {
           quarter = quarter.replace(",", "");
       }
       if (null != month && month.contains(",")) {
           month = month.replace(",", "");
       }
       if (null != week && week.contains(",")) {
           week = week.replace(",", "");
       }
       if (null != eType && eType.contains(",")) {
           eType = eType.replace(",", "");
       }
       if (null != isNewValue && isNewValue.contains(",")) {
           isNewValue = isNewValue.replace(",", "");
       }
       
       if ("true".equals(isNewValue)) {
    	   list = o_strategyMapReportBO.treeLoader(node, query);
       } else if("false".equals(isNewValue)) {
           list = 	o_strategyMapReportBO.treeLoader(node, query, year, quarter, month, week,
     			  eType);
        }
        map.put("children", list);
 	   return map;
   }
   
   /**
    * 
    * @param start
    * @param limit
    * @param query
    * @param sort
    * @param year
    * @param quarter
    * @param month
    * @param week
    * @param eType
    * @param isNewValue
    * @return
    */
   @ResponseBody
   @RequestMapping(value = "/kpi/report/findAllRiskKpiDetail.f")
   public Map<String, Object> findAllRiskKpiDetail(int start, int limit, String query, String sort, String year, String quarter,
           String month, String week, String eType, String isNewValue,Boolean isGroup){
       if (null != isNewValue && isNewValue.contains(",")) {
           isNewValue = isNewValue.replace(",", "");
       }
       String dir = "DESC";
       String sortColumn = "assessmentStatus";// 默认排序
       if (StringUtils.isNotBlank(sort)) {
           JSONArray jsonArray = JSONArray.fromObject(sort);
           if (jsonArray.size() > 0) {
               JSONObject jsobj = jsonArray.getJSONObject(0);
               sortColumn = jsobj.getString("property");// 按照哪个字段排序
               dir = jsobj.getString("direction");// 排序方向
           }
       }
       String lastflag = isNewValue;
       //String frequence = eType;
       Map<String, Object> map = new HashMap<String, Object>();
       List<Map<String, Object>> datas = null;
       if("true".equalsIgnoreCase(lastflag)) {
    	   datas = o_kpiRiskRelaReportBO.findLastKpiRiskResults(map, start, limit, query, sortColumn, dir,isGroup);
       }else{
    	   year = ",".equals(year) ? "" : year;
           quarter = ",".equals(quarter) ? "" : quarter;
           month = ",".equals(month) ? "" : month;
           week = ",".equals(week) ? "" : week;
           eType = ",".equals(eType) ? "" : eType;
           isNewValue = ",".equals(isNewValue) ? "" : isNewValue;
           if (null != year && year.contains(",")) {
               year = year.replace(",", "");
           }
           if (null != quarter && quarter.contains(",")) {
               quarter = quarter.replace(",", "");
               
           }
           if (null != month && month.contains(",")) {
               month = month.replace(",", "");
             
           }
           if (null != week && week.contains(",")) {
               week = week.replace(",", "");               
           }
           if (null != eType && eType.contains(",")) {
               eType = eType.replace(",", "");
               map.put("eType", eType);
           }
           map.put("year", year);
           map.put("quarter", quarter);
           map.put("month", month);
           map.put("week", week);
           map.put("start", start);
           map.put("limit", limit);
    	   datas = o_kpiRiskRelaReportBO.findKpiRiskResultsByTimeInfo(map,query, sortColumn, dir, isGroup);
       }
       map.put("datas", datas);
       return map;
   }
   
   @ResponseBody
   @RequestMapping(value = "/kpi/report/exportriskkpireport.f")
   public void exportriskkpireport(String id,String exportFileName, 
           String sheetName,  String headerData, String timePeriod,
           HttpServletRequest request, HttpServletResponse response) throws Exception  {
       List<Object[]> list = new ArrayList<Object[]>();
       List<String> indexList = new ArrayList<String>();//列ID
       List<Map<String,Object>> listMap = null;//列表数据
       JSONObject jsonObject = JSONObject.fromObject(timePeriod);
       String year = jsonObject.getString("year");
       String quarter = jsonObject.getString("quarter");
       String month = jsonObject.getString("month");
       String week = jsonObject.getString("week");
      // String eType = jsonObject.getString("eType");
       String isNewValue = jsonObject.getString("isNewValue");
       String[] fieldTitle = null;
       JSONArray headerArray=JSONArray.fromObject(headerData);
       int j = headerArray.size();
       fieldTitle = new String[j];
       for(int i=0;i<j;i++){
           JSONObject jsonObj = headerArray.getJSONObject(i);
           fieldTitle[i] = jsonObj.get("text").toString();
           indexList.add(jsonObj.get("dataIndex").toString());
       }
       String lastflag = isNewValue;
       //String frequence = eType;
       Map<String, Object> map = new HashMap<String, Object>();
       if ("true".equals(lastflag)) {// 最新值查询
    	   listMap = o_kpiRiskRelaReportBO.findLastKpiRiskResults(map, -1, -1, null, null, null,true);
       }else {
           map.put("year", year);
           map.put("quarter", quarter);
           map.put("month", month);
           map.put("week", week);
    	   listMap = o_kpiRiskRelaReportBO.findKpiRiskResultsByTimeInfo(map,null, null, null, false);
       }
       for(Map<String,Object> maps : listMap){
    	   Object[] object = new Object[j];
          
           object[0] = maps.get("riskName");
           if(null != maps.get("riskScore")) {
        	   object[1] = maps.get("riskScore").toString();  
           } else {
        	   object[1] = null;
           }
           if("icon-ibm-symbol-4-sm".equals(maps.get("riskStatus"))) {
               object[2] = "color|red";
           }else if("icon-ibm-symbol-6-sm".equals(maps.get("riskStatus"))){
               object[2] = "color|green";
           }else if("icon-ibm-symbol-5-sm".equals(maps.get("riskStatus"))){
               object[2] = "color|yellow";
           }else if("icon-ibm-symbol-0-sm".equals(maps.get("riskStatus"))){
               object[2] = "color|";
           }else{
               object[2] = null;
           }
           object[3] = maps.get("kpiName");
           if(null !=  maps.get("kpiWeight")){
        	   object[4] = maps.get("kpiWeight").toString();   
           } else {
        	   object[4] = null;
           }           
           
           object[5] = maps.get("deptName");
           object[6] = maps.get("kpiFrequency");
           object[7] = maps.get("targetValue");
           object[8] = maps.get("finishValue");
           if(null != maps.get("assessMentValue")) {
        	   object[9] = maps.get("assessMentValue").toString();  
           } else {
        	   object[9] = null;
           }
           if("icon-ibm-symbol-4-sm".equals(maps.get("status"))) {
               object[10] = "color|red";
           }else if("icon-ibm-symbol-6-sm".equals(maps.get("status"))){
               object[10] = "color|green";
           }else if("icon-ibm-symbol-5-sm".equals(maps.get("status"))){
               object[10] = "color|yellow";
           }else if("icon-ibm-symbol-0-sm".equals(maps.get("status"))){
               object[10] = "color|";
           }else{
               object[10] = null;
           }
           //object[10] = maps.get("status");           
           object[11] = maps.get("timePeriod");
           list.add(object);
       }
       
       if(StringUtils.isBlank(exportFileName)){
           exportFileName = "公司风险监控报表.xls";
       }
       if(StringUtils.isBlank(sheetName)){
           sheetName = "风险监控";
       }
       //数据，列名称，文件名称，sheet名称，sheet位置
       ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
   }
   
}
