package com.fhd.sm.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.CategoryBO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.AlarmRegion;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sm.business.chart.FAngularGaugeChart;
import com.fhd.sm.business.chart.FAngularGaugeCircleChart;
import com.fhd.sm.business.chart.FHLinearGaugeChart;
import com.fhd.sm.business.dynamictable.AngularGauge;
import com.fhd.sm.business.dynamictable.Bar2D;
import com.fhd.sm.business.dynamictable.MSLine;
import com.fhd.sm.web.controller.util.Utils;
import com.fhd.sm.web.form.KpiForm;

@Controller
public class RelaAssessResultControl {

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;
    
    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private CategoryBO o_categoryBO;
    
    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private StrategyMapBO o_strategyMapBO;

    @Autowired
    private AlarmPlanBO o_alarmPlanBO;
    
    @Autowired
    private StrategyMapBO o_kpiStrategyMapBO;
    
    
    
    /**首页指标图
     * @param items 参数信息
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/kpi/createkpichartList.f")
    public Map<String,Object> findCreateKpiChartList(String itmes, HttpServletRequest request){
        AlarmPlan alarmPlan = null;
        String xml = "";
        String ctx = request.getContextPath();
        Map<String,Object> map = new HashMap<String, Object>();
        JSONArray xmlArray = new JSONArray();
        JSONObject resultObj = new JSONObject();
        JSONObject jsobj = JSONObject.fromObject(itmes);
        Integer limit = jsobj.getInt("limit");
        Integer start = jsobj.getInt("start");
        String query = jsobj.getString("query");
        Map<String,Object> resultMap = o_kpiGatherResultBO.findLastKpiGatherResult(UserContext.getUser().getCompanyid(),true,true,start,limit,query);
        int totalCount = Integer.valueOf(String.valueOf(resultMap.get("totalCount")));
        if (limit * start < totalCount) {
            map.put("isNext", true);
        }
        else {
            map.put("isNext", false);
        }
        map.put("start", start);
        map.put("totalCount", totalCount);
        List<KpiGatherResult> resultList = (List<KpiGatherResult>)resultMap.get("resultlist");
        
        Map<String,AlarmPlan> alarmPlanMap = new HashMap<String, AlarmPlan>();
        List<String> kpiIdList = new ArrayList<String>();
        for (KpiGatherResult kpiGatherResult : resultList) {
        	String kpiId = kpiGatherResult.getKpi().getId();
        	kpiIdList.add(kpiId);
        }
        if(kpiIdList.size()>0){
        	alarmPlanMap = o_alarmPlanBO.findAlarmPlanByKpiId(kpiIdList, Contents.ALARMPLAN_REPORT);
        }
        Map<String,List<AlarmRegion>> regionMap = new HashMap<String, List<AlarmRegion>>();
        if(alarmPlanMap.size()>0){
        	List<String> alarmIdList = new ArrayList<String>();
        	Set<String> alarmIdSet = new HashSet<String>(); 
        	Collection<AlarmPlan> alarmCollection = alarmPlanMap.values();
        	for (AlarmPlan alarm : alarmCollection) {
        		alarmIdSet.add(alarm.getId());
			}
        	alarmIdList.addAll(alarmIdSet);
        	regionMap = o_alarmPlanBO.findAlarmRegionByAlarmId(alarmIdList);
        }
        for (KpiGatherResult kpiGatherResult : resultList) {
        	if(null!=kpiGatherResult){
        		Kpi kpi = kpiGatherResult.getKpi();
        		if(null!=kpi){
        			String kpiId = kpi.getId();
                    String kpiName = kpi.getName();
                    resultObj.put("objectId", kpiId);
                    alarmPlan = alarmPlanMap.get(kpiId);
                    if (null != alarmPlan&&null!= kpiGatherResult.getAssessmentValue()) {
                        List<AlarmRegion> alarmRegionList = regionMap.get(alarmPlan.getId());
                        xml = FAngularGaugeCircleChart.getXml(alarmRegionList, kpiGatherResult.getAssessmentValue(), kpiName, kpiId, true, ctx, "");
                        resultObj.put("xml", xml);
                        xmlArray.add(resultObj);
                    }
        		}
                
        	}
            
        }
        map.put("datas", xmlArray);
        return map;
    }
    /**首页记分卡和目标图
     * @param items 参数信息
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/kpi/createchartList.f")
    public Map<String,Object> findCreateChartList(String itmes, HttpServletRequest request){
        AlarmPlan alarmPlan = null;
        String xml = "";
        String ctx = request.getContextPath();
        Map<String,Object> map = new HashMap<String, Object>();
        JSONArray xmlArray = new JSONArray();
        JSONObject resultObj = new JSONObject();
        JSONObject jsobj = JSONObject.fromObject(itmes);
        String type = jsobj.getString("type");
        Integer limit = jsobj.getInt("limit");
        Integer start = jsobj.getInt("start");
        String query = jsobj.getString("query");
        Map<String,Object> resultMap = o_relaAssessResultBO.findLastAssessResultList(type, true, true, start, limit,query,UserContext.getUser().getCompanyid());
        int totalCount = Integer.valueOf(String.valueOf(resultMap.get("totalCount")));
        if (limit * start < totalCount) {
            map.put("isNext", true);
        }
        else {
            map.put("isNext", false);
        }
        map.put("start", start);
        map.put("totalCount", totalCount);
        
        List<String> idList = new ArrayList<String>();
        Map<String,AlarmPlan> alarmMap = new HashMap<String, AlarmPlan>();
        List<RelaAssessResult> resultList = (List<RelaAssessResult>)resultMap.get("resultlist");
        for (RelaAssessResult relaAssessResult : resultList) {
        	idList.add(relaAssessResult.getObjectId());
        }
        if(idList.size()>0){
        	if("sc".equals(type)){
            	alarmMap = o_alarmPlanBO.findScAlarmPlanByKpiId(idList, Contents.ALARMPLAN_REPORT);
	        }else{
	        	alarmMap = o_alarmPlanBO.findSmAlarmPlanByKpiId(idList, Contents.ALARMPLAN_REPORT);
	        }
        }
        Map<String,List<AlarmRegion>> regionMap = new HashMap<String, List<AlarmRegion>>();
        if(alarmMap.size()>0){
        	List<String> alarmIdList = new ArrayList<String>();
        	Set<String> alarmIdSet = new HashSet<String>(); 
        	Collection<AlarmPlan> alarmCollection = alarmMap.values();
        	for (AlarmPlan alarm : alarmCollection) {
        		alarmIdSet.add(alarm.getId());
			}
        	alarmIdList.addAll(alarmIdSet);
        	regionMap = o_alarmPlanBO.findAlarmRegionByAlarmId(alarmIdList);
        	 
        }
        for (RelaAssessResult relaAssessResult : resultList) {
        	if(null!=relaAssessResult){
        		resultObj.put("objectId", relaAssessResult.getObjectId());
                alarmPlan = alarmMap.get(relaAssessResult.getObjectId());
                if (null != alarmPlan) {
                    List<AlarmRegion> alarmRegionList = regionMap.get(alarmPlan.getId());
                    if (null != relaAssessResult.getAssessmentValue()&&null!=alarmRegionList) {
                        if("str".equals(type)){
                            xml= FAngularGaugeChart.getXml(alarmRegionList, relaAssessResult.getAssessmentValue(), relaAssessResult.getObjectName(),
                                    relaAssessResult.getObjectId(), true, ctx,"");
                        }else if("sc".equals(type)){
                            xml= FHLinearGaugeChart.getXml(alarmRegionList, relaAssessResult.getAssessmentValue(), relaAssessResult.getObjectName(),
                                    relaAssessResult.getObjectId(), true, ctx,"");
                        }
                        resultObj.put("xml", xml);
                        xmlArray.add(resultObj);
                    }
                }
        	}
            
            
        }
        map.put("datas", xmlArray);
        return map;
    }
    
    /**指标折线图
     * @param items 参数信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/findkpidiffhistoryresults.f")
    public Map<String, Object>  findKpiDiffHistoryResults(String items,String yearId){
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(items);
        String kpiName = "";
        Boolean isNewValue = false;
        String year = yearId;
        String frequence = "";
        String legendTargetAlias = "目标值";
        String legendFinishAlias = "实际值";
        String unit = "";
        Integer scale = Contents.DEFAULT_KPI_DOT_POSITION;
        JSONArray categorys = new JSONArray();
        JSONArray targetValues = new JSONArray();
        JSONArray finishValues = new JSONArray();
        JSONArray diffValues = new JSONArray();
        if (null!=jsobj.get("kpiname")) {
            kpiName = jsobj.getString("kpiname");
        }
        if (null!=jsobj.get("isNewValue")){
            isNewValue = jsobj.getBoolean("isNewValue");
        }
        Kpi kpi = o_kpiBO.findKpiByName(kpiName);
        if(null!=kpi){
            if(isNewValue){
                TimePeriod lastTimePeriod = kpi.getLastTimePeriod();
                if(null!=lastTimePeriod){
                    year = lastTimePeriod.getYear();
                }
            }else{
                if (StringUtils.isBlank(yearId)) {
                    year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                }
            }
            
            if(null!=kpi.getGatherFrequence()){
                frequence = kpi.getGatherFrequence().getId();//指标频率
            }
            
            if(null!=kpi.getTargetValueAlias()&&!"0".equals(kpi.getTargetValueAlias())){
                legendTargetAlias = kpi.getTargetValueAlias();
            }
            if(null!=kpi.getResultValueAlias()&&!"0".equals(kpi.getResultValueAlias())){
                legendFinishAlias = kpi.getResultValueAlias();
            }
            if(null!=kpi.getUnits()){
                unit = kpi.getUnits().getName();
            }
            if(null!=kpi.getScale()){
                scale = kpi.getScale();
            }
        }
        
        
        result.put("unit", unit);
        result.put("legendTargetAlias", legendTargetAlias);
        result.put("legendFinishAlias", legendFinishAlias);
        Map<String, Date> timeMap = new HashMap<String, Date>();
        if("0frequecy_quarter".equals(frequence)){
            timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence, 8, 4);
        }else if("0frequecy_year".equals(frequence)){
            timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence, 2, 0);
        }else if("0frequecy_month".equals(frequence)){
            timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence, 0, 12);
        }else if("0frequecy_week".equals(frequence)){
        	timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence,0,0);
        }else if("0frequecy_halfyear".equals(frequence)){
        	timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence,0,1);
        }
        //需要添加周的timemap
        Date startTime = timeMap.get("startDate");
        Date endTime = timeMap.get("endDate");
        if(null!=startTime && null!=endTime){
            List<KpiGatherResult> list = o_kpiGatherResultBO.findKpiGatherResultByTimeRange(kpi.getId(), startTime, endTime);
            for (KpiGatherResult kpiGatherResult : list) {
                String category = "";
                Double targetValue = kpiGatherResult.getTargetValue();
                Double finishValue = kpiGatherResult.getFinishValue();
                TimePeriod timePeriod = kpiGatherResult.getTimePeriod();
                if("0frequecy_month".equals(frequence)){
                    category = timePeriod.getYear()+"."+timePeriod.getMonth()+"月";
                }else if("0frequecy_quarter".equals(frequence)){
                    category = timePeriod.getYear()+"."+timePeriod.getQuarter()+"季";
                }else if("0frequecy_year".equals(frequence)){
                    category = timePeriod.getYear()+"年";
                }else if("0frequecy_week".equals(frequence)){
                    category = timePeriod.getWeekNofYear()+"周";
                }else if("0frequecy_halfyear".equals(frequence)){
                    category = timePeriod.getTimePeriodFullName();
                }
                Double convertTargetValue = null;
                Double convertFinishValue = null;
                if(null!=targetValue&&null!=finishValue){
                    convertTargetValue = Double.parseDouble(Utils.getValue(targetValue, scale));
                    targetValues.add(convertTargetValue);
                    if(!categorys.contains(category)){
                        categorys.add(category);
                    }
                }
                if(null!=finishValue&&null!=targetValue){
                    convertFinishValue = Double.parseDouble(Utils.getValue(finishValue, scale));
                    finishValues.add(convertFinishValue);
                    if(!categorys.contains(category)){
                        categorys.add(category);
                    }
                }
                if(null!=targetValue&&null!=finishValue){
                    diffValues.add(Math.abs(convertTargetValue-convertFinishValue));
                }
            }
        }
        
        result.put("targetValues", targetValues);
        result.put("finishValues", finishValues);
        result.put("diffValues", diffValues);
        result.put("categorys", categorys);
        result.put("success", true);
        return result;
    }
    
    
    /**查询指标历史数据(供图表使用)
     * @param items　指标名称
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/findkpihistoryresults.f")
    public Map<String, Object>  findKpiHistoryResults(String items,String yearId){
    	String kpiName = "";
    	String frequence = "";
    	Boolean isNewValue = false;
    	String year = yearId;
    	Integer scale = Contents.DEFAULT_KPI_DOT_POSITION;
    	Kpi kpi = null;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsobj = JSONObject.fromObject(items);
        Map<String, Object> result = new HashMap<String, Object>();
        if (null!=jsobj.get("kpiname")) {
            kpiName = jsobj.getString("kpiname");
        }
        if (null!=jsobj.get("isNewValue")){
            isNewValue = jsobj.getBoolean("isNewValue");
        }
        if(StringUtils.isNotBlank(kpiName)){
        	kpi = o_kpiBO.findKpiByName(kpiName);
        	if(kpi!=null){
        		scale = kpi.getScale();
        		if(isNewValue){
                    TimePeriod lastTimePeriod = kpi.getLastTimePeriod();
                    if(null!=lastTimePeriod){
                        year = lastTimePeriod.getYear();
                    }
                }else{
                    if (StringUtils.isBlank(yearId)) {
                        year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                    }
                }
            	if(null!=kpi.getGatherFrequence()){
                    frequence = kpi.getGatherFrequence().getId();//指标频率
                }
        	}
        	
        }
        
        Map<String, Date> timeMap = new HashMap<String, Date>();
        if("0frequecy_quarter".equals(frequence)){
            timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence, 8, 4);
        }else if("0frequecy_year".equals(frequence)){
            timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence, 2, 0);
        }else if("0frequecy_month".equals(frequence)){
            timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence, 0, 12);
        }else if("0frequecy_week".equals(frequence)){
        	timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence,0,0);
        }else if("0frequecy_halfyear".equals(frequence)){
        	timeMap = this.o_kpiGatherResultBO.findTimeRange(year, frequence,0,1);
        }
        Date startTime = timeMap.get("startDate");
        Date endTime = timeMap.get("endDate");
        List<KpiGatherResult> list = o_kpiGatherResultBO.findKpiGatherResultByKpiName(kpiName,UserContext.getUser().getCompanyid(),startTime,endTime);
        for (KpiGatherResult kpiGatherResult : list) {
            JSONArray item = new JSONArray();
            Date beginTime = kpiGatherResult.getBeginTime();
            if(kpiGatherResult.getFinishValue()!=null){
            	Double finishValue = Double.parseDouble(Utils.getValue(kpiGatherResult.getFinishValue(), scale));
                long time = DateUtils.toCalendar(beginTime).getTimeInMillis()+86400000;
                if(null!=finishValue){
                    item.add(time);
                    item.add(finishValue);
                    jsonArray.add(item);
                }
            }
        }
        result.put("chartDatas", jsonArray);
        result.put("success", true);
        return result;
    }
    
    /** 查询目标关联的图表信息
     * @param condItem  查询条件
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/categoryPanel.f")
    public Map<String, Object> findCategoryPanel(String condItem, HttpServletRequest request) {
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

        RelaAssessResult relaAssessResultByDataTypeEn = o_relaAssessResultBO.findRelaAssessResultByDataTypeEn(dataType, objectId, isNewValue, yearId,
                monthId);
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

        result.put("success", true);
        result.put("xml", xml);
        result.put("histXml", histXml);
        return result;
    }
    /**
     * <pre>
     * 查询记分卡关联的图表信息
     * </pre>
     * condItem 查询条件
     * @author 郝静
     */
    @ResponseBody
    @RequestMapping("/kpi/categoryStrPanel.f")
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

        RelaAssessResult relaAssessResultByDataTypeEn = o_relaAssessResultBO.findRelaAssessResultByDataTypeEn(dataType, objectId, isNewValue, yearId,
                monthId);
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
        List<KpiForm> datas = this.findSmRelaKpiResult(objectId, yearId, quarterId, monthId, weekId, dataType, isNewValue);
        String relKpiXml = Bar2D.getXml(datas);
        result.put("success", true);
        result.put("xml", xml);
        result.put("histXml", histXml);
        result.put("relKpiXml", relKpiXml);
        return result;
    }
    
    /**查询目标关联的指标信息
     * @param id 目标id
     * @param year 年
     * @param quarter 季
     * @param month 月
     * @param week 周
     * @param eType 频率
     * @param isNewValue 是否最新值
     * @return
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
            gatherDatas = o_kpiStrategyMapBO.findLastSmRelaKpiResults(map, 0, 20, null, sortColumn, dir, id);
        }
        else {// 具体频率查询
            gatherDatas = o_kpiStrategyMapBO.findSpecificSmRelaKpiResults(map, 0, 20, null, sortColumn, dir, id, year, quarter, month, week,
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
}
