package com.fhd.sm.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;



import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.web.controller.CategoryTrendComparator;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.AlarmRegion;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sm.business.dynamictable.AngularGauge;
import com.fhd.sm.business.dynamictable.Bar2D;
import com.fhd.sm.business.dynamictable.MSColumn2D;
import com.fhd.sm.business.dynamictable.MSLine;
import com.fhd.sm.web.form.KpiForm;

@Controller
public class ChartCmpControl {

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;

    @Autowired
    private CategoryBO o_categoryBO;
    
    @Autowired
    private StrategyMapBO o_strategyMapBO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;
    
    @Autowired
    private StrategyMapBO o_kpiStrategyMapBO;
    
    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
    
    /**记分卡和战略目标仪表盘图形
     * @param condItem 时间控件参数信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/findchartAngularGaugePanel.f")
    public Map<String, Object> findChartAngularGaugePanel(String condItem, HttpServletRequest request) {
    	String angularGaugeName = "";
    	Double angularGaugeValue = null;
    	Map<String,Object> angularMap = new HashMap<String, Object>();
    	Map<String,Object> histroyMap = new HashMap<String, Object>();
    	Map<String,Object> barMap = new HashMap<String, Object>();
    	Map<String,Object> alarmRegionMap = new HashMap<String, Object>();
    	Category category = null;
        StrategyMap strategyMap = null;
        AlarmPlan alarmPlan = null;
    	Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(condItem);
        if(!jsobj.containsKey("objectId")){
        	return result;
        }
        
        String monthId = "";
        if (jsobj.get("monthId") != null) {
            monthId = jsobj.getString("monthId");
        }
        String quarterId = "";
        if (jsobj.get("quarterId") != null) {
        	quarterId = jsobj.getString("quarterId");
        }
        String weekId = "";
        if (jsobj.get("weekId") != null) {
        	weekId = jsobj.getString("weekId");
        }
        boolean isNewValue = false;
        if (jsobj.get("isNewValue") != null) {
            isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
        }
        String dataType = "";
        if (jsobj.get("dataType") != null) {
            dataType = jsobj.getString("dataType");
        }
        String objectId = "";
        if (jsobj.get("objectId") != null) {
            objectId = jsobj.getString("objectId");
        }
        //记分卡或目标最后更新时间所在年
        String lastTimePeriodYearId = "";
        TimePeriod lastTimePeriod = null;
        //告警方案
        if ("str".equalsIgnoreCase(dataType)) {
            strategyMap = o_strategyMapBO.findById(objectId);
            if(null!=strategyMap){
            	lastTimePeriod = strategyMap.getTimePeriod();
            	angularGaugeName = strategyMap.getName();
                alarmPlan = o_strategyMapBO.findAlarmPlanBySmId(objectId, Contents.ALARMPLAN_REPORT);
                if(null==alarmPlan && null!=strategyMap.getCompany()){
                    alarmPlan =  o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast",strategyMap.getCompany().getId());
                }
            }
            
        }
        else if ("sc".equalsIgnoreCase(dataType)) {
            category = o_categoryBO.findCategoryById(objectId);
            if(null!=category){
            	lastTimePeriod = category.getTimePeriod();
            	angularGaugeName = category.getName();
                alarmPlan = o_categoryBO.findAlarmPlanByScId(objectId, Contents.ALARMPLAN_REPORT);
                if(null==alarmPlan && null!=category.getCompany()){
                    alarmPlan =  o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast",category.getCompany().getId());
                }
            }
            
        }
        if(null!=lastTimePeriod){
    		lastTimePeriodYearId = lastTimePeriod.getYear();
    	}
        
        String yearId = lastTimePeriodYearId;
        if (jsobj.get("yearId") != null&&!isNewValue) {
            yearId = jsobj.getString("yearId");
        }
        if(StringUtils.isBlank(yearId)){
        	yearId = DateUtils.getYear(new Date());
        }
        
        //仪表盘
        RelaAssessResult relaAssessResultByDataTypeEn = o_relaAssessResultBO.findLastAssessResultByObjectId(dataType, objectId);
        if (null != relaAssessResultByDataTypeEn) {
        	angularGaugeValue = relaAssessResultByDataTypeEn.getAssessmentValue();
        }
        
        alarmRegionMap.put("redMinValue", "");
    	alarmRegionMap.put("redMaxValue", "");
    	alarmRegionMap.put("yellowMinValue", "");
    	alarmRegionMap.put("yellowMaxValue", "");
    	alarmRegionMap.put("greenMinValue", "");
    	alarmRegionMap.put("greenMaxValue", "");
    	if(null!=alarmPlan){
    		List<AlarmRegion> alarmRegionList = o_categoryBO.findAlarmRegionByAlarmId(alarmPlan.getId());
            for (AlarmRegion alarmRegion : alarmRegionList) {
    		    if(StringUtils.isNotBlank(alarmRegion.getAlarmIcon().getId())){
    		        if("0alarm_startus_h".equals(alarmRegion.getAlarmIcon().getId())){
    		        	alarmRegionMap.put("redMinValue", alarmRegion.getMinValue());
    		        	alarmRegionMap.put("redMaxValue", alarmRegion.getMaxValue());
    		        }else if("0alarm_startus_m".equals(alarmRegion.getAlarmIcon().getId())){
    		        	alarmRegionMap.put("yellowMinValue", alarmRegion.getMinValue());
    		        	alarmRegionMap.put("yellowMaxValue", alarmRegion.getMaxValue());
                    }else if("0alarm_startus_l".equals(alarmRegion.getAlarmIcon().getId())){
                    	alarmRegionMap.put("greenMinValue", alarmRegion.getMinValue());
    		        	alarmRegionMap.put("greenMaxValue", alarmRegion.getMaxValue());
                    }
    		    }
    		}
    	}
        
        angularMap.put("name", angularGaugeName);
        angularMap.put("value", angularGaugeValue);
        angularMap.put("alarmRegion",alarmRegionMap);
        result.put("angularMap", angularMap);
        
        //历史数据
        JSONArray histroyArray = new JSONArray();
        JSONArray monthArray = new JSONArray();
        JSONArray statusArray = new JSONArray();
        
        List<RelaAssessResult> relaAssessResultByDataTypeList = o_relaAssessResultBO.findRelaAssessResultByDataType(dataType, objectId, isNewValue,
                yearId, monthId);
        if(null!=relaAssessResultByDataTypeList&&relaAssessResultByDataTypeList.size()>0){
        	for (RelaAssessResult relaAssessResult : relaAssessResultByDataTypeList) {
        	    if(null!=relaAssessResult){
        	        if(null!=relaAssessResult.getTimePeriod()){
                        monthArray.add(relaAssessResult.getTimePeriod().getMonth()+"月");
                    }
                    if(null!=relaAssessResult.getAssessmentValue()){
                        histroyArray.add(relaAssessResult.getAssessmentValue());
                    }else{
                        histroyArray.add("");
                    }
                    if(null!=relaAssessResult.getAssessmentStatus()){
                        statusArray.add(relaAssessResult.getAssessmentStatus().getId());
                    }else{
                        statusArray.add("");
                    }
        	    }
        		
    		}
        }
        histroyMap.put("datas", histroyArray);
        histroyMap.put("status", statusArray);
        histroyMap.put("months", monthArray);
        histroyMap.put("name", angularGaugeName);
        result.put("histroyMap", histroyMap);
        
        //指标条型图
        JSONArray categoryArray = new JSONArray();
        JSONArray valueArray = new JSONArray();
        List<KpiForm> datas = null;
        if ("str".equalsIgnoreCase(dataType)) {
        	 datas = this.findSmRelaKpiResult(objectId, yearId, quarterId, monthId, weekId, dataType, isNewValue);
        }
        else if ("sc".equalsIgnoreCase(dataType)) {
        	 datas = this.findScRelaKpiResult(objectId, yearId, quarterId, monthId, weekId, dataType, isNewValue,dataType);
        }
        if(null!=datas&&datas.size()>0){
        	for (KpiForm kpiForm : datas) {
    			if(StringUtils.isNotBlank(kpiForm.getName())){
    				categoryArray.add(kpiForm.getName());
    			}
    			valueArray.add(kpiForm.getAssessmentValue());
    		}
        }
        barMap.put("year", yearId);
        barMap.put("categorys", categoryArray);
        barMap.put("datas", valueArray);
        result.put("barMap", barMap);
        result.put("success", true);
    	return result;
    }
    
    
    /**
     * <pre>
     * 查询目标关联的图表信息
     * </pre>
     * 
     * @author 郝静
     */
    @ResponseBody
    @RequestMapping("/kpi/AngularGaugePanel.f")
    public Map<String, Object> findCategoryStrPanel(String condItem, HttpServletRequest request) {
        String xml = "";
        Category category = null;
        StrategyMap strategyMap = null;
        AlarmPlan alarmPlan = null;
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(condItem);
        if(!jsobj.containsKey("objectId")){
        	return result;
        }
        String yearId = "";
        if (jsobj.get("yearId") != null) {
            yearId = jsobj.getString("yearId");
        }
        String monthId = "";
        if (jsobj.get("monthId") != null) {
            monthId = jsobj.getString("monthId");
        }
        String quarterId = "";
        if (jsobj.get("quarterId") != null) {
        	quarterId = jsobj.getString("quarterId");
        }
        String weekId = "";
        if (jsobj.get("weekId") != null) {
        	weekId = jsobj.getString("weekId");
        }
        boolean isNewValue = false;
        if (jsobj.get("isNewValue") != null) {
            isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
        }
        String dataType = "";
        if (jsobj.get("dataType") != null) {
            dataType = jsobj.getString("dataType");
        }
        String objectId = "";
        if (jsobj.get("objectId") != null) {
            objectId = jsobj.getString("objectId");
        }
        if ("str".equalsIgnoreCase(dataType)) {
            strategyMap = o_strategyMapBO.findById(objectId);
            alarmPlan = o_strategyMapBO.findAlarmPlanBySmId(objectId, Contents.ALARMPLAN_REPORT);
            if(null==alarmPlan){
                alarmPlan =  o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast",strategyMap.getCompany().getId());
            }
        }
        else if ("sc".equalsIgnoreCase(dataType)) {
            category = o_categoryBO.findCategoryById(objectId);
            alarmPlan = o_categoryBO.findAlarmPlanByScId(objectId, Contents.ALARMPLAN_REPORT);
            if(null==alarmPlan){
                alarmPlan =  o_alarmPlanBO.findAlarmPlanByNameType("常用告警方案", "0alarm_type_kpi_forecast",category.getCompany().getId());
            }
        }

        RelaAssessResult relaAssessResultByDataTypeEn = o_relaAssessResultBO.findLastAssessResultByObjectId(dataType, objectId);
        if (null != relaAssessResultByDataTypeEn && null != alarmPlan) {
            List<AlarmRegion> alarmRegionList = o_categoryBO.findAlarmRegionByAlarmId(alarmPlan.getId());
            if ("sc".equalsIgnoreCase(dataType)) {
                if (null != relaAssessResultByDataTypeEn.getAssessmentValue()) {
                    xml = AngularGauge.getXml(alarmRegionList, relaAssessResultByDataTypeEn.getAssessmentValue(), category.getName());
                }
            }
            else if ("str".equalsIgnoreCase(dataType)) {
                if (null != relaAssessResultByDataTypeEn.getAssessmentValue()) {
                    xml = AngularGauge.getXml(alarmRegionList, relaAssessResultByDataTypeEn.getAssessmentValue(), strategyMap.getName());
                }
            }
        }

        List<RelaAssessResult> relaAssessResultByDataTypeList = o_relaAssessResultBO.findRelaAssessResultByDataType(dataType, objectId, isNewValue,
                yearId, monthId);
        String histXml = MSLine.getXml(relaAssessResultByDataTypeList);
        
        List<KpiForm> datas = new ArrayList<KpiForm>();
        if ("str".equalsIgnoreCase(dataType)) {
        	 datas = this.findSmRelaKpiResult(objectId, yearId, quarterId, monthId, weekId, dataType, isNewValue);
        }
        else if ("sc".equalsIgnoreCase(dataType)) {
        	 datas = this.findScRelaKpiResult(objectId, yearId, quarterId, monthId, weekId, dataType, isNewValue,dataType);
        }
       
        String relKpiXml = Bar2D.getXml(datas);
        result.put("success", true);
        result.put("xml", xml);
        result.put("histXml", histXml);
        result.put("relKpiXml", relKpiXml);
        return result;
    }
    
    /**
     * <pre>
     * 查询目标关联的指标信息
     * </pre>
     * 
     * @author 郝静
     */
    public List<KpiForm> findSmRelaKpiResult(String id, String year, String quarter,
            String month, String week, String eType, boolean isNewValue) {
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
        if (null != id && id.contains(",")) {
            id = id.replace(",", "");
        }
        if (null != eType && eType.contains(",")) {
            eType = eType.replace(",", "");
        }
        boolean lastflag = isNewValue;
        String frequence = eType;
        String dir = "DESC";
        String sortColumn = "assessmentStatus";// 默认排序
        KpiForm kpi = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        List<Object[]> gatherDatas = null;
        if (lastflag) {// 最新值查询
            gatherDatas = o_kpiStrategyMapBO.findLastSmRelaKpiResults(map, 0, 1000, null, sortColumn, dir, id);
        }
        else {// 具体频率查询
            gatherDatas = o_kpiStrategyMapBO.findSpecificSmRelaKpiResults(map, 0, 1000, null, sortColumn, dir, id, year, quarter, month, week,
                    frequence);
        }
        if (null != gatherDatas) {
            for (Object[] objects : gatherDatas) {
            	kpi = new KpiForm(objects, "sm");
                datas.add(kpi);
            }
        }
        return datas;
    }
    
    /**
     * <pre>
     * 查询记分卡关联的指标信息
     * </pre>
     * 
     * @author 郝静
     */
    public List<KpiForm> findScRelaKpiResult(String id, String year, String quarter,
            String month, String week, String eType, boolean isNewValue, String tableType) {
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
        if (null != id && id.contains(",")) {
            id = id.replace(",", "");
        }
        if (null != eType && eType.contains(",")) {
            eType = eType.replace(",", "");
        }
        boolean lastflag = isNewValue;
        String frequence = eType;
        String dir = "DESC";
        String sortColumn = "assessmentStatus";// 默认排序
        KpiForm form = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        List<Object[]> gatherDatas = null;
        if (lastflag) {
            gatherDatas = o_categoryBO.findLastCategoryRelaKpiResultsByChartSome(map, 0, 1000, null, id, sortColumn, dir, tableType);

        }
        else {
            gatherDatas = o_categoryBO.findSpecificCategoryRelaKpiResultsChartBySome(map, 0, 1000, null, sortColumn, dir, id, year, quarter,
                    month, week, frequence, tableType);
        }
        if (null != gatherDatas) {
            for (Object[] objects : gatherDatas) {
                form = new KpiForm(objects, "chart");
                datas.add(form);
            }
        }
        return datas;
    }
    
    /**
     * <pre>
     * 多维对比图组件下部图表
     * </pre>
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/MultiDimComparePanel.f")
    public Map<String, Object> findCategoryRelaKpiByChart(String condItem, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(condItem);
        if(!jsobj.containsKey("objectId")){
        	return result;
        }
        String yearId = "";
        if (jsobj.get("yearId") != null) {
            yearId = jsobj.getString("yearId");
        }
        String monthId = "";
        if (jsobj.get("monthId") != null) {
            monthId = jsobj.getString("monthId");
        }
        String quarterId = "";
        if (jsobj.get("quarterId") != null) {
        	quarterId = jsobj.getString("quarterId");
        }
        String weekId = "";
        if (jsobj.get("weekId") != null) {
        	weekId = jsobj.getString("weekId");
        }
        boolean isNewValue = false;
        if (jsobj.get("isNewValue") != null) {
            isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
        }
        String dataType = "";
        if (jsobj.get("dataType") != null) {
            dataType = jsobj.getString("dataType");
        }
        String objectId = "";
        if (jsobj.get("objectId") != null) {
            objectId = jsobj.getString("objectId");
        }
        String eType = "";
        if (null != eType && eType.contains(",")) {
            eType = eType.replace(",", "");
        }
        boolean lastflag = isNewValue;
        String frequence = eType;
        String dir = "DESC";
        String sortColumn = "assessmentStatus";// 默认排序
        KpiForm form = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        List<Object[]> gatherDatas = null;
        if (lastflag){
            gatherDatas = o_categoryBO.findLastCategoryRelaKpiResultsByChartSome(map, 0, 1000, null, objectId, sortColumn, dir, dataType);

        }
        else {
            gatherDatas = o_categoryBO.findSpecificCategoryRelaKpiResultsChartBySome(map, 0, 1000, null, sortColumn, dir, objectId, yearId, quarterId,
                    monthId, weekId, frequence, dataType);
        }
        if (null != gatherDatas) {
            for (Object[] objects : gatherDatas) {
                form = new KpiForm(objects, "chart");
                datas.add(form);
            }
        }
        String xml = MSColumn2D.getXml(datas);
        result.put("success", true);
        result.put("xml", xml);
        return result;
    }
    
    /**
     * <pre>
     * 多维对比图上部表格
     * </pre>
     * 
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/cmp/findcategoryrelakpi.f")
    public Map<String, Object> findCategoryRelaKpiByChart(int start, int limit, String query, String sort, String objectId, String year, String quarter,
            String month, String week, String eType, String isNewValue, String dataType) {
    	objectId = ",".equals(objectId) ? "" : objectId;
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
        if (null != objectId && objectId.contains(",")) {
        	objectId = objectId.replace(",", "");
        }
        if (null != eType && eType.contains(",")) {
            eType = eType.replace(",", "");
        }
        if (null != isNewValue && isNewValue.contains(",")) {
            isNewValue = isNewValue.replace(",", "");
        }
        String lastflag = isNewValue;
        String frequence = eType;
        String dir = "DESC";
        String sortColumn = "assessmentStatus";// 默认排序
        KpiForm form = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        List<Object[]> gatherDatas = null;
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");// 按照哪个字段排序
                dir = jsobj.getString("direction");// 排序方向
            }
        }
        if ("true".equals(lastflag)) {
            gatherDatas = o_categoryBO.findLastCategoryRelaKpiResultsByChartSome(map, start, limit, query, objectId, sortColumn, dir, dataType);

        }
        else {
            gatherDatas = o_categoryBO.findSpecificCategoryRelaKpiResultsChartBySome(map, start, limit, query, sortColumn, dir, objectId, year, quarter,
                    month, week, frequence, dataType);
        }
        if (null != gatherDatas) {
            for (Object[] objects : gatherDatas) {
                form = new KpiForm(objects, "chart");
                datas.add(form);
            }
        }
        map.put("datas", datas);
        return map;
    }
    /**记分卡和战略目标多维对比分析图形
     * @param condItem 时间控件参数信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/cmp/findkpimultidimchart.f")
    public Map<String, Object> findKpiMultiDimChart(String condItem, HttpServletRequest request) {
    	JSONObject jsobj = JSONObject.fromObject(condItem);
    	Map<String, Object> map = new HashMap<String, Object>();
        if(!jsobj.containsKey("objectId")){
        	return map;
        }
        String yearId = "";
        if (jsobj.get("yearId") != null) {
            yearId = jsobj.getString("yearId");
        }
        String monthId = "";
        if (jsobj.get("monthId") != null) {
            monthId = jsobj.getString("monthId");
        }
        String quarterId = "";
        if (jsobj.get("quarterId") != null) {
        	quarterId = jsobj.getString("quarterId");
        }
        String weekId = "";
        if (jsobj.get("weekId") != null) {
        	weekId = jsobj.getString("weekId");
        }
        boolean isNewValue = false;
        if (jsobj.get("isNewValue") != null) {
            isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
        }
        String dataType = "";
        if (jsobj.get("dataType") != null) {
            dataType = jsobj.getString("dataType");
        }
        String objectId = "";
        if (jsobj.get("objectId") != null) {
            objectId = jsobj.getString("objectId");
        }
    	String dir = "DESC";
    	String sortColumn = "assessmentStatus";// 默认排序
    	List<Object[]> gatherDatas = null;
    	if (isNewValue) {
    		gatherDatas = o_categoryBO.findLastCategoryRelaKpiResultsByChartSome(map, 0, 1000, "", objectId, sortColumn, dir, dataType);
    		
    	}
    	else {
    		gatherDatas = o_categoryBO.findSpecificCategoryRelaKpiResultsChartBySome(map, 0, 1000, "", sortColumn, dir, objectId, yearId, quarterId,
    				monthId, weekId, "", dataType);
    	}
    	JSONArray valueArray = new JSONArray();
    	 //['目标值', '实际值', '上期实际值', '去年同期值']
    	if (null != gatherDatas) {
    		for (Object[] objects : gatherDatas) {
    			JSONArray values = new JSONArray();
    			JSONObject dataObj = new JSONObject();
    			if(null!=objects[4]){
    				values.add(objects[4]);//目标值
    			}
    			if(null!=objects[5]){
    				values.add(objects[5]);//实际值
    			}
    			if(null!=objects[8]){
    				values.add(objects[8]);//上期实际值
    			}
    			if(null!=objects[9]){
    				values.add(objects[9]);//去年同期值
    			}
    			dataObj.put("name", (String) objects[1]);
    			dataObj.put("data", values);
    			valueArray.add(dataObj);
    		}
    	}
    	map.put("datas", valueArray);
    	map.put("success", true);
    	return map;
    }
    
    /**
     * 趋势图上部分历史数据（记分卡）.
     * 
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/cmp/findcategoryrelakpihistorydatas.f")
    public Map<String, Object> findcategoryrelakpihistorydatas(String objectId, String query, String year, String quarter, String month, String week,
            String eType, String isNewValue, String sort,String dataType) {
        // 返回结果集
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(objectId) && !"undefined".equals(objectId)) {
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

            String lastflag = isNewValue;
            if ("true".equals(lastflag)) {
                year = "";
            }
            Map<String, List<Object[]>> kpiGatherResultMap  = new HashMap<String, List<Object[]>>();
            if("str".equalsIgnoreCase(dataType)){
            	
            	kpiGatherResultMap = o_kpiGatherResultBO.findKpiGatherResultsBySmId(objectId, query, year);
            	
            }else if("sc".equalsIgnoreCase(dataType)){
            	
            	kpiGatherResultMap = o_kpiGatherResultBO.findKpiGatherResultsByCategoryId(objectId, query, year);
            }
            
            // 定义封装参数
            List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
            Map<String, Object> row = null;
            
            for (Entry<String, List<Object[]>> entry : kpiGatherResultMap.entrySet()) {
            	String kpiName = entry.getKey();
                row = new HashMap<String, Object>();

                //row.put("id", (int) (Math.random() * 10000));
                row.put("id", new Random().nextInt(10001));
                List<Object[]> objects = entry.getValue();
                if (null != objects && objects.size() > 0) {
                    if (null != objects.get(0)[1]) {
                        String assessmentStatus = "";
                        String directionstr = "";
                        for (Object[] object : objects) {
                            row.put("kid", object[0]);
                            row.put("name", object[1]);
                            if ("true".equals(lastflag)) {
                                if (null != object[3] && null != object[4]) {
                                    if (String.valueOf(object[3]).equals(String.valueOf(object[4]))) {
                                        assessmentStatus = String.valueOf(object[8]);
                                        directionstr = String.valueOf(object[9]);
                                        break;
                                    }
                                }
                            }
                            else {
                                if (StringUtils.isNotBlank(year) && year.equals(object[4])) {
                                    assessmentStatus = String.valueOf(object[8]);
                                    directionstr = String.valueOf(object[9]);
                                    break;
                                }
                                else if (StringUtils.isNotBlank(quarter) && quarter.equals(object[4])) {
                                    assessmentStatus = String.valueOf(object[8]);
                                    directionstr = String.valueOf(object[9]);
                                    break;
                                }
                                else if (StringUtils.isNotBlank(month) && month.equals(object[4])) {
                                    assessmentStatus = String.valueOf(object[8]);
                                    directionstr = String.valueOf(object[9]);
                                    break;
                                }
                            }
                        }
                        row.put("assessmentStatus", assessmentStatus);
                        row.put("directionstr", directionstr);
                        if ("0frequecy_month".equals(objects.get(0)[2])) {
                            // 月
                            row.put("januaryValue", null != objects.get(0)[7] ? objects.get(0)[7] : "");
                            row.put("februaryValue", null != objects.get(1)[7] ? objects.get(1)[7] : "");
                            row.put("marchValue", null != objects.get(2)[7] ? objects.get(2)[7] : "");
                            row.put("aprilValue", null != objects.get(3)[7] ? objects.get(3)[7] : "");
                            row.put("mayValue", null != objects.get(4)[7] ? objects.get(4)[7] : "");
                            row.put("juneValue", null != objects.get(5)[7] ? objects.get(5)[7] : "");
                            row.put("julyValue", null != objects.get(6)[7] ? objects.get(6)[7] : "");
                            row.put("aguestValue", null != objects.get(7)[7] ? objects.get(7)[7] : "");
                            row.put("septemberValue", null != objects.get(8)[7] ? objects.get(8)[7] : "");
                            row.put("octoberValue", null != objects.get(9)[7] ? objects.get(9)[7] : "");
                            row.put("novemberValue", null != objects.get(10)[7] ? objects.get(10)[7] : "");
                            row.put("decemberValue", null != objects.get(11)[7] ? objects.get(11)[7] : "");
                        }
                        else if ("0frequecy_quarter".equals(objects.get(0)[2])) {
                            // 季
                            row.put("januaryValue", "");
                            row.put("februaryValue", "");
                            row.put("marchValue", null != objects.get(0)[7] ? objects.get(0)[7] : "");
                            row.put("aprilValue", "");
                            row.put("mayValue", "");
                            row.put("juneValue", null != objects.get(1)[7] ? objects.get(1)[7] : "");
                            row.put("julyValue", "");
                            row.put("aguestValue", "");
                            row.put("septemberValue", null != objects.get(2)[7] ? objects.get(2)[7] : "");
                            row.put("octoberValue", "");
                            row.put("novemberValue", "");
                            row.put("decemberValue", null != objects.get(3)[7] ? objects.get(3)[7] : "");
                        }
                        else if ("0frequecy_halfyear".equals(objects.get(0)[2])) {
                            // 半年
                            row.put("januaryValue", "");
                            row.put("februaryValue", "");
                            row.put("marchValue", "");
                            row.put("aprilValue", "");
                            row.put("mayValue", "");
                            row.put("juneValue", null != objects.get(0)[7] ? objects.get(0)[7] : "");
                            row.put("julyValue", "");
                            row.put("aguestValue", "");
                            row.put("septemberValue", "");
                            row.put("octoberValue", "");
                            row.put("novemberValue", "");
                            row.put("decemberValue", null != objects.get(1)[7] ? objects.get(1)[7] : "");
                        }
                        else if ("0frequecy_year".equals(objects.get(0)[2])) {
                            // 年
                            row.put("januaryValue", "");
                            row.put("februaryValue", "");
                            row.put("marchValue", "");
                            row.put("aprilValue", "");
                            row.put("mayValue", "");
                            row.put("juneValue", "");
                            row.put("julyValue", "");
                            row.put("aguestValue", "");
                            row.put("septemberValue", "");
                            row.put("octoberValue", "");
                            row.put("novemberValue", "");
                            row.put("decemberValue", null != objects.get(0)[7] ? objects.get(0)[7] : "");
                        }
                        else {
                            row.put("assessmentStatus", "");
                            row.put("directionstr", "");
                            row.put("januaryValue", "");
                            row.put("februaryValue", "");
                            row.put("marchValue", "");
                            row.put("aprilValue", "");
                            row.put("mayValue", "");
                            row.put("juneValue", "");
                            row.put("julyValue", "");
                            row.put("aguestValue", "");
                            row.put("septemberValue", "");
                            row.put("octoberValue", "");
                            row.put("novemberValue", "");
                            row.put("decemberValue", "");
                        }
                    }
                    else {
                        row.put("assessmentStatus", "");
                        row.put("directionstr", "");
                        row.put("januaryValue", "");
                        row.put("februaryValue", "");
                        row.put("marchValue", "");
                        row.put("aprilValue", "");
                        row.put("mayValue", "");
                        row.put("juneValue", "");
                        row.put("julyValue", "");
                        row.put("aguestValue", "");
                        row.put("septemberValue", "");
                        row.put("octoberValue", "");
                        row.put("novemberValue", "");
                        row.put("decemberValue", "");
                    }
                }
                else {
                    row.put("kid", "");
                    row.put("name", kpiName);
                    row.put("assessmentStatus", "");
                    row.put("directionstr", "");
                    row.put("januaryValue", "");
                    row.put("februaryValue", "");
                    row.put("marchValue", "");
                    row.put("aprilValue", "");
                    row.put("mayValue", "");
                    row.put("juneValue", "");
                    row.put("julyValue", "");
                    row.put("aguestValue", "");
                    row.put("septemberValue", "");
                    row.put("octoberValue", "");
                    row.put("novemberValue", "");
                    row.put("decemberValue", "");
                }
                datas.add(row);
            }
            Collections.sort(datas, new CategoryTrendComparator(sortColumn, dir));
            map.put("datas", datas);
            map.put("totalCount", kpiGatherResultMap.keySet().size());
        }
        else {
            map.put("totalCount", "0");
            map.put("datas", new Object[0]);
        }

        return map;
    }
    /**记分卡和战略目标所关联的指标趋势分析图形
     * @param condItem 时间控件参数信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/cmp/findobjectrelakpitrend.f")
    public Map<String, Object> findObjectRelaKpiTrend(String condItem, HttpServletRequest request) {
    	// 返回结果集
    	Map<String, Object> map = new HashMap<String, Object>();
    	JSONObject jsobj = JSONObject.fromObject(condItem);
    	if(!jsobj.containsKey("objectId")){
        	return map;
        }
    	String objectId = "";
        if (jsobj.get("objectId") != null) {
            objectId = jsobj.getString("objectId");
        }
    	if (StringUtils.isNotBlank(objectId) && !"undefined".equals(objectId)) {
            
            String yearId = "";
            if (jsobj.get("yearId") != null) {
                yearId = jsobj.getString("yearId");
            }
            String monthId = "";
            if (jsobj.get("monthId") != null) {
                monthId = jsobj.getString("monthId");
            }
            String quarterId = "";
            if (jsobj.get("quarterId") != null) {
            	quarterId = jsobj.getString("quarterId");
            }
            /*String weekId = "";
            if (jsobj.get("weekId") != null) {
            	weekId = jsobj.getString("weekId");
            }*/
            boolean isNewValue = false;
            if (jsobj.get("isNewValue") != null) {
                isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
            }
            String dataType = "";
            if (jsobj.get("dataType") != null) {
                dataType = jsobj.getString("dataType");
            }
            if(isNewValue){
            	yearId = "";//如果时间控件选择了最新值,那么将yearId赋为空字符串,按照指标的最后更新字段查询
            }
        	String dir = "DESC";
        	String sortColumn = "assessmentStatus";// 默认排序
    		Map<String, List<Object[]>> kpiGatherResultMap  = new HashMap<String, List<Object[]>>();
    		if("str".equalsIgnoreCase(dataType)){
    			
    			kpiGatherResultMap = o_kpiGatherResultBO.findKpiGatherResultsBySmId(objectId, "", yearId);
    			
    		}else if("sc".equalsIgnoreCase(dataType)){
    			
    			kpiGatherResultMap = o_kpiGatherResultBO.findKpiGatherResultsByCategoryId(objectId, "", yearId);
    		}
    		
    		// 定义封装参数
    		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
    		Map<String, Object> row = null;
    		Set<Map.Entry<String, List<Object[]>>> entrys =  kpiGatherResultMap.entrySet();
    		for (Map.Entry<String, List<Object[]>> entry : entrys) {    			
    			row = new HashMap<String, Object>();    			
    			row.put("id", new Random().nextInt(10001));
    			List<Object[]> objects = entry.getValue();
    			if (null != objects && objects.size() > 0) {
    				if (null != objects.get(0)[1]) {
    					String assessmentStatus = "";
    					String directionstr = "";
    					for (Object[] object : objects) {
    						row.put("kid", object[0]);
    						row.put("name", object[1]);
    						if (isNewValue) {
    							if (null != object[3] && null != object[4]) {
    								if (String.valueOf(object[3]).equals(String.valueOf(object[4]))) {
    									assessmentStatus = String.valueOf(object[8]);
    									directionstr = String.valueOf(object[9]);
    									break;
    								}
    							}
    						}
    						else {
    							if (StringUtils.isNotBlank(yearId) && yearId.equals(object[4])) {
    								assessmentStatus = String.valueOf(object[8]);
    								directionstr = String.valueOf(object[9]);
    								break;
    							}
    							else if (StringUtils.isNotBlank(quarterId) && quarterId.equals(object[4])) {
    								assessmentStatus = String.valueOf(object[8]);
    								directionstr = String.valueOf(object[9]);
    								break;
    							}
    							else if (StringUtils.isNotBlank(monthId) && monthId.equals(object[4])) {
    								assessmentStatus = String.valueOf(object[8]);
    								directionstr = String.valueOf(object[9]);
    								break;
    							}
    						}
    					}
    					row.put("assessmentStatus", assessmentStatus);
    					row.put("directionstr", directionstr);
    					if ("0frequecy_month".equals(objects.get(0)[2])) {
    						// 月
    						if(objects.size()>=1){
    							row.put("januaryValue", null != objects.get(0)[7] ? objects.get(0)[7] : "");
        						row.put("januaryStatus", null != objects.get(0)[8] ? objects.get(0)[8] : "");
    						}else{
    							row.put("januaryValue", "");
        						row.put("januaryStatus", "");
    						}
    						if(objects.size()>=2){
    							row.put("februaryValue", null != objects.get(1)[7] ? objects.get(1)[7] : "");
    							row.put("februaryStatus", null != objects.get(1)[8] ? objects.get(1)[8] : "");
    						}else{
    							row.put("februaryValue", "");
    							row.put("februaryStatus", "");
    						}
    						if(objects.size()>=3){
    							row.put("marchValue", null != objects.get(2)[7] ? objects.get(2)[7] : "");
    							row.put("marchStatus", null != objects.get(2)[8] ? objects.get(2)[8] : "");
    						}else{
    							row.put("marchValue",  "");
    							row.put("marchStatus", "");
    						}
    						if(objects.size()>=4){
    							row.put("aprilValue", null != objects.get(3)[7] ? objects.get(3)[7] : "");
    							row.put("aprilStatus", null != objects.get(3)[8] ? objects.get(3)[8] : "");
    						}else{
    							row.put("aprilValue",  "");
    							row.put("aprilStatus", "");
    						}
    						if(objects.size()>=5){
    							row.put("mayValue", null != objects.get(4)[7] ? objects.get(4)[7] : "");
    							row.put("mayStatus", null != objects.get(4)[8] ? objects.get(4)[8] : "");
    						}else{
    							row.put("mayValue",  "");
    							row.put("mayStatus",  "");
    						}
    						if(objects.size()>=6){
    							row.put("juneValue", null != objects.get(5)[7] ? objects.get(5)[7] : "");
    							row.put("juneStatus", null != objects.get(5)[8] ? objects.get(5)[8] : "");
    						}else{
    							row.put("juneValue",  "");
    							row.put("juneStatus", "");
    						}
    						if(objects.size()>=7){
    							row.put("julyValue", null != objects.get(6)[7] ? objects.get(6)[7] : "");
        						row.put("julyStatus", null != objects.get(6)[8] ? objects.get(6)[8] : "");
    						}else{
    							row.put("julyValue",  "");
        						row.put("julyStatus", "");
    						}
    						if(objects.size()>=8){
    							row.put("aguestValue", null != objects.get(7)[7] ? objects.get(7)[7] : "");
    							row.put("aguestStatus", null != objects.get(7)[8] ? objects.get(7)[8] : "");
    						}else{
    							row.put("aguestValue",  "");
    							row.put("aguestStatus", "");
    						}
    						if(objects.size()>=9){
    							row.put("septemberValue", null != objects.get(8)[7] ? objects.get(8)[7] : "");
    							row.put("septemberStatus", null != objects.get(8)[8] ? objects.get(8)[8] : "");
    						}else{
    							row.put("septemberValue",  "");
    							row.put("septemberStatus", "");
    						}
    						if(objects.size()>=10){
    							row.put("octoberValue", null != objects.get(9)[7] ? objects.get(9)[7] : "");
    							row.put("octoberStatus", null != objects.get(9)[8] ? objects.get(9)[8] : "");
    						}else{
    							row.put("octoberValue",  "");
    							row.put("octoberStatus", "");
    						}
    						if(objects.size()>=11){
    							row.put("novemberValue", null != objects.get(10)[7] ? objects.get(10)[7] : "");
    							row.put("novemberStatus", null != objects.get(10)[8] ? objects.get(10)[8] : "");
    						}else{
    							row.put("novemberValue",  "");
    							row.put("novemberStatus", "");
    						}
    						if(objects.size()>=12){
    							row.put("decemberValue", null != objects.get(11)[7] ? objects.get(11)[7] : "");
    							row.put("decemberStatus", null != objects.get(11)[8] ? objects.get(11)[8] : "");
    						}else{
    							row.put("decemberValue", "");
    							row.put("decemberStatus", "");
    						}
    					}
    					else if ("0frequecy_quarter".equals(objects.get(0)[2])) {
    						// 季
    						row.put("januaryValue", "");
    						row.put("januaryStatus", "");
    						row.put("februaryValue", "");
    						row.put("februaryStatus", "");
    						if(objects.size()>=1){
    							row.put("marchValue", null != objects.get(0)[7] ? objects.get(0)[7] : ""); 
        						row.put("marchStatus", null != objects.get(0)[8] ? objects.get(0)[8] : ""); 
    						}else{
    							row.put("marchValue",  ""); 
        						row.put("marchStatus", "");
    						}
    						
    						row.put("aprilValue", "");
    						row.put("aprilStatus", "");
    						row.put("mayValue", "");
    						row.put("mayStatus", "");
    						if(objects.size()>=2){
    							row.put("juneValue", null != objects.get(1)[7] ? objects.get(1)[7] : "");
    							row.put("juneStatus", null != objects.get(1)[8] ? objects.get(1)[8] : "");
    						}else{
    							row.put("juneValue",  "");
    							row.put("juneStatus", "");
    						}
    						row.put("julyValue", "");
    						row.put("julyStatus", "");
    						row.put("aguestValue", "");
    						row.put("aguestStatus", "");
    						if(objects.size()>=3){
    							row.put("septemberValue", null != objects.get(2)[7] ? objects.get(2)[7] : "");
    							row.put("septemberStatus", null != objects.get(2)[8] ? objects.get(2)[8] : "");
    						}else{
    							row.put("septemberValue",  "");
    							row.put("septemberStatus", "");
    						}
    						row.put("octoberValue", "");
    						row.put("octoberStatus", "");
    						row.put("novemberValue", "");
    						row.put("novemberStatus", "");
    						if(objects.size()>=4){
    							row.put("decemberValue", null != objects.get(3)[7] ? objects.get(3)[7] : "");
    							row.put("decemberStatus", null != objects.get(3)[8] ? objects.get(3)[8] : "");
    						}else{
    							row.put("decemberValue",  "");
    							row.put("decemberStatus", "");
    						}
    					}
    					else if ("0frequecy_halfyear".equals(objects.get(0)[2])) {
    						// 半年
    						row.put("januaryValue", "");
    						row.put("januaryStatus", "");
    						row.put("februaryValue", "");
    						row.put("februaryStatus", "");
    						row.put("marchValue", "");
    						row.put("marchStatus", "");
    						row.put("aprilValue", "");
    						row.put("aprilStatus", "");
    						row.put("mayValue", "");
    						row.put("mayStatus", "");
    						if(objects.size()>=1){
    							row.put("juneValue", null != objects.get(0)[7] ? objects.get(0)[7] : "");
    							row.put("juneStatus", null != objects.get(0)[8] ? objects.get(0)[8] : "");
    						}else{
    							row.put("juneValue",  "");
    							row.put("juneStatus", "");
    						}
    						row.put("julyValue", "");
    						row.put("julyStatus", "");
    						row.put("aguestValue", "");
    						row.put("aguestStatus", "");
    						row.put("septemberValue", "");
    						row.put("septemberStatus", "");
    						row.put("octoberValue", "");
    						row.put("octoberStatus", "");
    						row.put("novemberValue", "");
    						row.put("novemberStatus", "");
    						if(objects.size()>=2){
    							row.put("decemberValue", null != objects.get(1)[7] ? objects.get(1)[7] : "");
    							row.put("decemberStatus", null != objects.get(1)[8] ? objects.get(1)[8] : "");
    						}else{
    							row.put("decemberValue",  "");
    							row.put("decemberStatus", "");
    						}
    					}
    					else if ("0frequecy_year".equals(objects.get(0)[2])) {
    						// 年
    						row.put("januaryValue", "");
    						row.put("januaryStatus", "");
    						row.put("februaryValue", "");
    						row.put("februaryStatus", "");
    						row.put("marchValue", "");
    						row.put("marchStatus", "");
    						row.put("aprilValue", "");
    						row.put("aprilStatus", "");
    						row.put("mayValue", "");
    						row.put("mayStatus", "");
    						row.put("juneValue", "");
    						row.put("juneStatus", "");
    						row.put("julyValue", "");
    						row.put("julyStatus", "");
    						row.put("aguestValue", "");
    						row.put("aguestStatus", "");
    						row.put("septemberValue", "");
    						row.put("septemberStatus", "");
    						row.put("octoberValue", "");
    						row.put("octoberStatus", "");
    						row.put("novemberValue", "");
    						row.put("novemberStatus", "");
    						if(objects.size()>=1){
    							row.put("decemberValue", null != objects.get(0)[7] ? objects.get(0)[7] : "");
    							row.put("decemberStatus", null != objects.get(0)[8] ? objects.get(0)[8] : "");
    						}else{
    							row.put("decemberValue",  "");
    							row.put("decemberStatus", "");
    						}
    					}
    					else {
    						row.put("assessmentStatus", "");
    						row.put("directionstr", "");
    						row.put("januaryValue", "");
    						row.put("januaryStatus", "");
    						row.put("februaryValue", "");
    						row.put("februaryStatus", "");
    						row.put("marchValue", "");
    						row.put("marchStatus", "");
    						row.put("aprilValue", "");
    						row.put("aprilStatus", "");
    						row.put("mayValue", "");
    						row.put("mayStatus", "");
    						row.put("juneValue", "");
    						row.put("juneStatus", "");
    						row.put("julyValue", "");
    						row.put("julyStatus", "");
    						row.put("aguestValue", "");
    						row.put("aguestStatus", "");
    						row.put("septemberValue", "");
    						row.put("septemberStatus", "");
    						row.put("octoberValue", "");
    						row.put("octoberStatus", "");
    						row.put("novemberValue", "");
    						row.put("novemberStatus", "");
    						row.put("decemberValue", "");
    						row.put("decemberStatus", "");
    					}
    				}
    				else {
    					row.put("assessmentStatus", "");
    					row.put("directionstr", "");
    					row.put("januaryValue", "");
    					row.put("januaryStatus", "");
    					row.put("februaryValue", "");
    					row.put("februaryStatus", "");
    					row.put("marchValue", "");
    					row.put("marchStatus", "");
    					row.put("aprilValue", "");
    					row.put("aprilStatus", "");
    					row.put("mayValue", "");
    					row.put("mayStatus", "");
    					row.put("juneValue", "");
    					row.put("juneStatus", "");
    					row.put("julyValue", "");
    					row.put("julyStatus", "");
    					row.put("aguestValue", "");
    					row.put("aguestStatus", "");
    					row.put("septemberValue", "");
    					row.put("septemberStatus", "");
    					row.put("octoberValue", "");
    					row.put("octoberStatus", "");
    					row.put("novemberValue", "");
    					row.put("novemberStatus", "");
    					row.put("decemberValue", "");
    					row.put("decemberStatus", "");
    				}
    			}
    			else {
    				row.put("kid", "");
    				row.put("name", entry.getKey());
    				row.put("assessmentStatus", "");
    				row.put("directionstr", "");
    				row.put("januaryValue", "");
    				row.put("februaryValue", "");
    				row.put("marchValue", "");
    				row.put("aprilValue", "");
    				row.put("mayValue", "");
    				row.put("juneValue", "");
    				row.put("julyValue", "");
    				row.put("aguestValue", "");
    				row.put("septemberValue", "");
    				row.put("octoberValue", "");
    				row.put("novemberValue", "");
    				row.put("decemberValue", "");
    				row.put("januaryStatus", "");
    				row.put("februaryStatus", "");
    				row.put("marchStatus", "");
    				row.put("aprilStatus", "");
    				row.put("mayStatus", "");
    				row.put("juneStatus", "");
    				row.put("julyStatus", "");
    				row.put("aguestStatus", "");
    				row.put("septemberStatus", "");
    				row.put("octoberStatus", "");
    				row.put("novemberStatus", "");
    				row.put("decemberStatus", "");
    			}
    			datas.add(row);
    		}
    		Collections.sort(datas, new CategoryTrendComparator(sortColumn, dir));
    		map.put("datas", datas);
    		map.put("totalCount", kpiGatherResultMap.keySet().size());
    	}
    	else {
    		map.put("totalCount", "0");
    		map.put("datas", new Object[0]);
    	}
    	map.put("success", true);
    	return map;
    }
    
    /**
     * 结构图上部分图表数据.
     * 
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/cmp/findcategoryrelakpidata.f")
    public Map<String, Object> findcategoryrelakpidata(int start, int limit, String query, String sort, String objectId, String year, String quarter,
            String month, String week, String eType, String isNewValue) {
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
        // 返回结果集
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(objectId) && !"undefined".equals(objectId)) {
            year = ",".equals(year) ? "" : year;
            quarter = ",".equals(quarter) ? "" : quarter;
            month = ",".equals(month) ? "" : month;
            week = ",".equals(week) ? "" : week;
            objectId = ",".equals(objectId) ? "" : objectId;
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

            String lastflag = isNewValue;
            String frequence = eType;

            // 定义封装参数
            List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

            List<Object[]> results = null;
            if ("true".equals(lastflag)) {
                results = o_categoryBO.findLastCategoryRelaKpiResultsBySome(map, start, limit, query, objectId, sortColumn, dir);

            }
            else {
                results = o_categoryBO.findSpecificCategoryRelaKpiResultsBySome(map, start, limit, query, sortColumn, dir, objectId, year, quarter, month,
                        week, frequence);
            }
            if (null != results && results.size() > 0) {
                for (Object[] objects : results) {
                	Map<String, Object> row = new HashMap<String, Object>();

                    row.put("id", new Random().nextInt(10001));
                    row.put("kid", objects[0]);
                    row.put("name", objects[1]);
                    row.put("assessmentStatus", objects[6]);
                    row.put("directionstr", objects[7]);
                    row.put("finishValue", objects[5]);

                    datas.add(row);
                }
            }

            map.put("datas", datas);
            if(null!=results){
            	map.put("totalCount", results.size());
            }else{
            	map.put("totalCount", "0");
            }
            
        }
        else {
            map.put("totalCount", "0");
            map.put("datas", new Object[0]);
        }

        return map;
    }
    /**记分卡所关联的指标结构分析图形
     * @param condItem 时间控件参数信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/cmp/findcategoryrelakpistruct.f")
    public Map<String, Object> findCategoryRelaKpiStruct(String condItem, HttpServletRequest request) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	JSONObject jsobj = JSONObject.fromObject(condItem);
    	if(!jsobj.containsKey("objectId")){
        	return map;
        }
    	String objectId = "";
        if (jsobj.get("objectId") != null) {
            objectId = jsobj.getString("objectId");
        }
    	// 返回结果集
    	if (StringUtils.isNotBlank(objectId) && !"undefined".equals(objectId)) {
    		String yearId = "";
            if (jsobj.get("yearId") != null) {
                yearId = jsobj.getString("yearId");
            }
            String monthId = "";
            if (jsobj.get("monthId") != null) {
                monthId = jsobj.getString("monthId");
            }
            String quarterId = "";
            if (jsobj.get("quarterId") != null) {
            	quarterId = jsobj.getString("quarterId");
            }
            String weekId = "";
            if (jsobj.get("weekId") != null) {
            	weekId = jsobj.getString("weekId");
            }
            boolean isNewValue = false;
            if (jsobj.get("isNewValue") != null) {
                isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
            }
            /*String dataType = "";
            if (jsobj.get("dataType") != null) {
                dataType = jsobj.getString("dataType");
            }*/
            
        	String dir = "DESC";
        	String sortColumn = "assessmentStatus";// 默认排序
    		String frequence = "";
    		
    		// 定义封装参数
    		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
    		
    		List<Object[]> results = null;
    		if (isNewValue) {
    			results = o_categoryBO.findLastCategoryRelaKpiResultsBySome(map, 0, 1000, "", objectId, sortColumn, dir);
    			
    		}
    		else {
    			results = o_categoryBO.findSpecificCategoryRelaKpiResultsBySome(map, 0, 1000, "", sortColumn, dir, objectId, yearId, quarterId, monthId,
    					weekId, frequence);
    		}
    		Map<String, Object> row = null;
    		if (null != results && results.size() > 0) {
    			for (Object[] objects : results) {
    				row = new HashMap<String, Object>();
    				
    				row.put("id", new Random().nextInt(10001));
    				row.put("kid", objects[0]);
    				row.put("name", objects[1]);
    				row.put("assessmentStatus", objects[6]);
    				row.put("directionstr", objects[7]);
    				row.put("finishValue", objects[5]==null?"":objects[5]);
    				
    				datas.add(row);
    			}
    		}
    		
    		map.put("datas", datas);
    		if(null != results && results.size() > 0) {
    			map.put("totalCount", results.size());
    		}else {
    			map.put("totalCount", "0");
    		}   		
    	}
    	else {    		
    		map.put("datas", new Object[0]);
    	}
    	map.put("success", true);
    	return map;
    }

}
