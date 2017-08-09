package com.fhd.sm.web.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.kpi.KpiTmp;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sm.business.KpiTypeDataImportBO;
import com.fhd.sm.web.form.KpiTmpForm;
import com.fhd.sys.web.form.file.FileForm;

/**
 * 
 * @author 王鑫
 *
 */
@Controller
public class DataKpiTypeImportControl {
	
	@Autowired
    private KpiTypeDataImportBO o_kpiDataImportBO; 
	
	/**
	 * 
	 * @param form
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping("/kpi/kpi/uploadKpitype.f")
	public void uploadKpiDataToTmp(FileForm form, HttpServletResponse response,Boolean isCoverage) throws IOException{
	    Boolean result = true;
		Boolean flag = false;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
				.readEspecialExcel(form.getFile().getInputStream(), 2);// 读取文件(第五页)
			
			//将数据写入数据库临时表
			flag = o_kpiDataImportBO.saveKpiDataFromExcel(excelDatas, "KC",isCoverage);
			resultMap.put("uploadResult", flag);
		} catch (Exception e) {
		    result = false;
			e.printStackTrace();
		}
		resultMap.put("success", result);
		response.getWriter().print(JSONSerializer.toJSON(resultMap));
		response.getWriter().close();
	}
	
	/**
	 * 
	 * @return 临时表数据
	 */
	@ResponseBody
	@RequestMapping("/kpi/kpi/QuerykpiTypeImportData.f")
	public Map<String, Object> queryKpiTypeImportData(String type){
		List<Map<String, Object>> datas = null;
		Map<String, Object> map = new HashMap<String, Object>();
		datas = o_kpiDataImportBO.findAllKpiTmp(type);
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 
	 * @param id 临时表指标ID
	 * @return 对应的结果
	 */
	@ResponseBody
	@RequestMapping("/kpiTmp/Kpi/findKpiTmpById.f")
	public Map<String, Object> findKpiTmpById(String id) {
		    JSONArray otherdimArr = new JSONArray();
	        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	        Map<String, Object> dataMap = new HashMap<String, Object>();
	        Map<String, Object> jsonMap = new HashMap<String, Object>();
	        if (StringUtils.isNotBlank(id) && !"undefined".equals(id)) {
	            KpiTmp kpi = o_kpiDataImportBO.findKpiTmpById(id);
	            // 基本信息赋值
	            if (null != kpi.getStartDate()) {
	                jsonMap.put("startDateStr", sf.format(kpi.getStartDate()));
	            }
	            jsonMap.put("id", kpi.getId());
	            jsonMap.put("code", kpi.getCode());
	            jsonMap.put("name", kpi.getName());
	            jsonMap.put("desc", kpi.getDesc());
	            jsonMap.put("targetValueAlias", kpi.getTargetValueAlias());
	            jsonMap.put("resultValueAlias", kpi.getResultValueAlias());
	            // 默认名称
	            if (null != kpi.getIsNameDefault()) {
	                jsonMap.put("namedefault", kpi.getIsNameDefault());
	            }
	            if (null != kpi.getIsInherit()) {
	                jsonMap.put("isInheritStr", kpi.getIsInherit());
	            }

	            if (null != kpi.getIsMonitor()) {
	                jsonMap.put("monitorStr", kpi.getIsMonitor());
	            }
	            if (null != kpi.getUnits()) {
	                jsonMap.put("unitsStr", kpi.getUnits());
	            }
	            if (null != kpi.getStatus()) {
	                jsonMap.put("statusStr", kpi.getStatus());
	            }
	            if (null != kpi.getType()) {
	                jsonMap.put("typeStr", kpi.getType());
	            }
	            if (null != kpi.getDataType()) {
	                jsonMap.put("dataTypeStr", kpi.getDataType());
	            }
	            if (null != kpi.getKpiType()) {
	                jsonMap.put("kpiTypeStr", kpi.getKpiType());
	            }
	            if (null != kpi.getAlarmMeasure()) {
	                jsonMap.put("alarmMeasureStr", kpi.getAlarmMeasure());
	            }
	            if (null != kpi.getRelativeTo()) {
	                jsonMap.put("relativeToStr", kpi.getRelativeTo());
	            }
	            if (null != kpi.getAlarmBasis()) {
	                jsonMap.put("alarmBasisStr", kpi.getAlarmBasis());
	            }
	            if (null != kpi.getGatherFrequence()) {
	                jsonMap.put("gatherFrequenceStr", kpi.getGatherFrequence());
	            }
	            jsonMap.put("targetSetInterval", kpi.getTargetSetInterval());
	            jsonMap.put("resultCollectInterval", kpi.getResultCollectInterval());
	            jsonMap.put("targetFormula", kpi.getTargetFormula());
	            jsonMap.put("resultFormula", kpi.getResultFormula());
	            jsonMap.put("assessmentFormula", kpi.getAssessmentFormula());
	            jsonMap.put("relationFormula", kpi.getRelationFormula());
	            jsonMap.put("forecastFormula", kpi.getForecastFormula());
	            jsonMap.put("shortName", kpi.getShortName());
	            jsonMap.put("sort", kpi.getSort());
	           
	            // 维度信息赋值
	            jsonMap.put("mainDim", kpi.getMainDim());
	            if(StringUtils.isNotBlank(kpi.getSupportDim())) {
	            	String[] otherDims = kpi.getSupportDim().split(",");
	            	for(String otherDim : otherDims) {
	            		 otherdimArr.add(otherDim);
	            	}
	            	jsonMap.put("otherDimArray", otherdimArr.toString());
	            }
	              
	            // 部门信息赋值
	            jsonMap.put("ownDept",kpi.getOwenrDept());
	            jsonMap.put("reportDept", kpi.getReportDept());
	            jsonMap.put("viewDept", kpi.getCheckDept());
	            jsonMap.put("gatherDept",kpi.getCollectDept());
	            jsonMap.put("targetDept", kpi.getTargetDept());
	            //jsonMap.put("parentKpiId", "none");
	            // 查询指标所属的类型信息
	            Map<String, Object> othervalue = new HashMap<String, Object>();
	            jsonMap.put("resultSumMeasureStr", kpi.getResultSumMeasure());
	            jsonMap.put("targetSumMeasureStr",kpi.getTargetSumMeasure());
	            jsonMap.put("assessmentSumMeasureStr",kpi.getAssessmentSumMeasure());
	            String isResultFormula = kpi.getIsResultFormula();
	            String resultFormula = kpi.getResultFormula();
	            if (StringUtils.isNotBlank(isResultFormula)) {
	                jsonMap.put("isResultFormula", isResultFormula);
	            }
	            if (null != resultFormula && StringUtils.isNotBlank(resultFormula)) {
	                jsonMap.put("resultFormula", resultFormula);
	            }
	            String isTargetFormula = kpi.getIsTargetFormula();
	            if (StringUtils.isNotBlank(isTargetFormula)) {
	                jsonMap.put("isTargetFormula", isTargetFormula);
	            }
	            String targetFormula = kpi.getTargetFormula();
	            if (null != targetFormula && StringUtils.isNotBlank(targetFormula)) {
	                jsonMap.put("targetFormula", targetFormula);
	            }
	            String isAssessmentFormula = kpi.getIsAssessmentFormula();
	            if (StringUtils.isNotBlank(isAssessmentFormula)) {
	                jsonMap.put("isAssessmentFormula", isAssessmentFormula);
	            }
	            String assessmentFormula = kpi.getAssessmentFormula();
	            if (null != assessmentFormula && StringUtils.isNotBlank(assessmentFormula)) {
	                jsonMap.put("assessmentFormula", assessmentFormula);
	            }
	            if (null != kpi.getScale()) {
	                jsonMap.put("scale", kpi.getScale());
	            }
	            if (null != kpi.getResultCollectInterval()) {
	                jsonMap.put("resultCollectIntervalStr", kpi.getResultCollectInterval());
	            }
	            if (null != kpi.getTargetSetInterval()) {
	                jsonMap.put("targetSetIntervalStr", kpi.getTargetSetInterval());
	            }
	            if (null != kpi.getRelativeTo()) {
	                jsonMap.put("relativeToStr", kpi.getRelativeTo());
	            }
	            if(null!=kpi.getCalc()){
	                jsonMap.put("calcStr", kpi.getCalc());
	            }else{
	            	jsonMap.put("calcStr", "");
	            }
	            jsonMap.put("modelValue", kpi.getModelValue());
	            jsonMap.put("maxValue", kpi.getMaxValue());
	            jsonMap.put("minValue", kpi.getMinValue());
	            
	            String[] values = null;

	            String gatherDayFormulr = kpi.getGatherDayFormulrShow();
	            String targetSetDayFormular = kpi.getTargetSetDayFormularShow();
	            String gatherReportDayFormulr = kpi.getGatherReportDayFormulrShow();
	            String targetSetReportDayFormulr = kpi.getTargetReportDayFormulrShow();

	            String gatherDayCron = StringUtils.defaultIfEmpty(kpi.getGatherDayFormulr(), "");
	            String targetDayCron = StringUtils.defaultIfEmpty(kpi.getTargetSetDayFormular(), "");
	            othervalue.put("gatherDayCron", gatherDayCron);
	            othervalue.put("targetDayCron", targetDayCron);
	   

	            if (StringUtils.isNotBlank(gatherDayFormulr)) {
	                values = StringUtils.split(gatherDayFormulr, "@");
	                if (null != values && values.length > 0) {
	                    jsonMap.put("resultgatherfrequence", values[0]);
	                }
	                    othervalue.put("resultgatherfrequenceDictType", kpi.getGatherFrequence());

	                if (null != values && values.length > 1) {
	                    othervalue.put("resultgatherfrequenceRule", values[1]);
	                }
	            }
	            if (StringUtils.isNotBlank(targetSetDayFormular)) {
	                values = StringUtils.split(targetSetDayFormular, "@");
	                if (null != values && values.length > 0) {
	                    jsonMap.put("targetSetFrequence", values[0]);
	                    othervalue.put("targetSetFrequence", values[0]);
	                }

	                othervalue.put("targetSetFrequenceDictType", kpi.getTargetSetFrequence());
	                if (null != values && values.length > 1) {
	                    othervalue.put("targetSetFrequenceRule", values[1]);
	                }
	            }
	            if (StringUtils.isNotBlank(gatherReportDayFormulr)) {
	                values = StringUtils.split(gatherReportDayFormulr, "@");
	                if (null != values && values.length > 0) {
	                    jsonMap.put("reportFrequence", values[0]);
	                    othervalue.put("reportFrequence", values[0]);
	                } 
	                    othervalue.put("reportFrequenceDictType", kpi.getReportFrequence());
	                if (null != values && values.length > 1) {
	                    othervalue.put("reportFrequenceRule", values[1]);
	                }
	            }
	            if (StringUtils.isNotBlank(targetSetReportDayFormulr)) {
	                values = StringUtils.split(targetSetReportDayFormulr, "@");
	                if (null != values && values.length > 0) {
	                    jsonMap.put("targetSetReportFrequence", values[0]);
	                    othervalue.put("targetSetReportFrequence", values[0]);
	                }
	                    othervalue.put("targetSetReportFrequenceDictType", kpi.getTargetSetReportFrequence());
	                if (null != values && values.length > 1) {
	                    othervalue.put("targetSetReportFrequenceRule", values[1]);
	                }

	            }
	            othervalue.put("validateInfo", kpi.getValidateInfo());
	            // 告警方案
	            jsonMap.put("warningPlan",kpi.getForeWarningSet());
	            jsonMap.put("alarmPlan",kpi.getWarningSet());
	            jsonMap.put("effDate", kpi.getWarningEffDate());
	            // 公司名称
	            jsonMap.put("companyName",kpi.getCompany());
	            dataMap.put("data", jsonMap);
	            dataMap.put("othervalue", othervalue);
	            dataMap.put("success", true);
	        }

	        return dataMap;
	}
	
	/**
	 * 
	 * @param  param
	 * @return 插入结果
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping("/kpi/kpi/mergeKpiTmp.f")
	public Map<String, Object> mergeKpiTmp(String param) throws IllegalAccessException, InvocationTargetException, ParseException{
    	String otherDimStr = "";
    	StringBuffer otherDimBf = new StringBuffer();
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(param);
        KpiTmpForm form = new KpiTmpForm();
        BeanUtils.populate(form, jsobj);
        form.setOwenrDept(jsobj.getString("ownDept"));
        form.setCollectDept(jsobj.getString("gatherDept"));
        form.setTargetDept(jsobj.getString("targetDept"));
        form.setReportDept(jsobj.getString("reportDept"));
        form.setCheckDept(jsobj.getString("viewDept"));
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");       
        form.setStartDate(fmt.parse(jsobj.getString("startDateStr")));
        form.setUnits(jsobj.getString("unitsStr"));
        form.setType(jsobj.getString("typeStr"));
        form.setKpiType(jsobj.getString("kpiTypeStr"));
        form.setAlarmMeasure(jsobj.getString("alarmMeasureStr"));
        form.setAlarmBasis(jsobj.getString("alarmBasisStr"));
        if(jsobj.containsKey("otherDim")&&!"".equals(jsobj.get("otherDim"))){
        	JSONArray otherDimArr = jsobj.getJSONArray("otherDim");
        	for (Object otherDim : otherDimArr) {
        		otherDimBf.append(otherDim).append(",");
			}
        }
          if(otherDimBf.length()>0){
        	otherDimStr = otherDimBf.substring(0, otherDimBf.length()-1);
          }
          form.setSupportDim(otherDimStr);
          form.setGatherReportDayFormulr(jsobj.getString("gatherreportCron"));
          form.setTargetSetReportDayFormulr(jsobj.getString("targetReportCron"));
          form.setGatherDayFormulr(jsobj.getString("gatherValueCron"));
          form.setTargetSetDayFormular(jsobj.getString("targetValueCron"));
          form.setGatherFrequence(jsobj.getString("gatherfrequence"));
          form.setTargetSetFrequence(jsobj.getString("targetSetFrequenceDictType"));
          form.setReportFrequence(jsobj.getString("reportFrequenceDictType"));
          form.setTargetSetReportFrequence(jsobj.getString("targetSetReportFrequenceDictType"));
        form.setIsResultFormula(jsobj.getString("resultformulaDict"));
        form.setIsTargetFormula(jsobj.getString("targetformulaDict"));
        form.setIsAssessmentFormula(jsobj.getString("assessmentformulaDict"));
        if(jsobj.containsKey("calcValue")&&StringUtils.isNotBlank(jsobj.getString("calcValue"))){
            form.setCalc(jsobj.getString("calcValue"));
        }
        if (jsobj.containsKey("resultformula")&&!"null".equals(jsobj.getString("resultformula"))) {
            form.setResultFormula(jsobj.getString("resultformula"));
        }
        if (jsobj.containsKey("targetformula")&&!"null".equals(jsobj.getString("targetformula"))) {
            form.setTargetFormula(jsobj.getString("targetformula"));
        }
        if (jsobj.containsKey("assessmentformula")&&!"null".equals(jsobj.getString("assessmentformula"))) {
            form.setAssessmentFormula(jsobj.getString("assessmentformula"));
        } 
        form.setResultSumMeasure(jsobj.getString("resultSumMeasureStr"));
        form.setTargetSumMeasure(jsobj.getString("targetSumMeasureStr"));
        form.setAssessmentSumMeasure(jsobj.getString("assessmentSumMeasureStr"));
        if (StringUtils.isNotBlank(jsobj.getString("scale"))) {
            form.setScale(Integer.parseInt(jsobj.getString("scale")));
        }
        form.setRelativeTo(jsobj.getString("relativeTo"));
        form.setResultCollectInterval(Integer.parseInt(jsobj.getString("resultCollectInterval")));
        form.setTargetSetInterval(Integer.parseInt(jsobj.getString("targetSetInterval")));
        if (!"null".equals(jsobj.getString("modelValue")) && StringUtils.isNotBlank(jsobj.getString("modelValue"))) {
            form.setModelValue(Double.parseDouble(jsobj.getString("modelValue")));
        }
        if (!"null".equals(jsobj.getString("maxValue")) && StringUtils.isNotBlank(jsobj.getString("maxValue"))) {
            form.setMaxValue(Double.parseDouble(jsobj.getString("maxValue")));
        }
        if (!"null".equals(jsobj.getString("minValue")) && StringUtils.isNotBlank(jsobj.getString("minValue"))) {
            form.setMinValue(Double.parseDouble(jsobj.getString("minValue")));
        }
        if (!"null".equals(jsobj.getString("targetSetFrequenceStr")) && StringUtils.isNotBlank(jsobj.getString("targetSetFrequenceStr"))) {
        form.setTargetSetDayFormularShow(jsobj.getString("targetSetFrequenceStr") + "@" + jsobj.getString("targetSetFrequenceRadioType"));
        }
        if (!"null".equals(jsobj.getString("reportFrequenceStr")) && StringUtils.isNotBlank(jsobj.getString("reportFrequenceStr"))) {
        form.setGatherReportDayFormulrShow(jsobj.getString("reportFrequenceStr") + "@" + jsobj.getString("reportFrequenceRadioType"));
        }
        if (!"null".equals(jsobj.getString("resultgatherfrequence")) && StringUtils.isNotBlank(jsobj.getString("resultgatherfrequence"))){
        	form.setGatherDayFormulrShow(jsobj.getString("resultgatherfrequence") + "@" + jsobj.getString("gatherfrequenceRule"));
        }
        if (!"null".equals(jsobj.getString("targetSetReportFrequenceStr")) && StringUtils.isNotBlank(jsobj.getString("targetSetReportFrequenceStr"))) {
    	   form.setTargetReportDayFormulrShow(jsobj.getString("targetSetReportFrequenceStr") + "@" + jsobj.getString("targetSetReportFrequenceRadioType") ); 
        }
        if(!"null".equals(jsobj.getString("warnPlanId")) && StringUtils.isNotBlank(jsobj.getString("warnPlanId"))) {
           form.setForeWarningSet(jsobj.getString("warnPlanId"));
        }
        if(!"null".equals(jsobj.getString("alarmPlanId")) && StringUtils.isNotBlank(jsobj.getString("alarmPlanId"))) {
            form.setWarningSet(jsobj.getString("alarmPlanId"));
         }
        
        if(!"null".equals(jsobj.getString("warnstartDate")) && StringUtils.isNotBlank(jsobj.getString("warnstartDate"))) {
            form.setWarningEffDate(fmt.parse(jsobj.getString("warnstartDate")));
         }
        if(!"null".equals(jsobj.getString("company")) && StringUtils.isNotBlank(jsobj.getString("company"))) {
            form.setCompany(jsobj.getString("company"));
         }
        form.setValidateInfoString(o_kpiDataImportBO.validateEditForm(form));
        o_kpiDataImportBO.mergeKpiForm(form);
        result.put("success", true);
        return result;
	}
	
	/**
	 * 
	 * @return 导入数据结果
	 * @throws ParseException 
	 */
	@ResponseBody
	@RequestMapping("/kpi/kpiTmp/mergeKpiTmpKpiType.f")
	public Map<String, Object> mergeKpiTmpKpiType(Boolean isCoverage) throws ParseException {
		Map<String, Object> data = new HashMap<String,Object>();
		o_kpiDataImportBO.mergeKpiTmpKpiType(isCoverage);
		data.put("success", true);
		return data;
	   }
	}
	 
