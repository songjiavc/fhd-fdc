/**
 * KpiForm.java
 * com.fhd.kpi.web.form
 *   ver     date           author
 * ──────────────────────────────────
 *           2012-11-12         陈晓哲
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
 */

package com.fhd.sm.web.form;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;

/**
 * 指标,指标类型Form
 * 
 * @author 陈晓哲
 * @version
 * @since Ver 1.1
 * @Date 2012-11-12 下午02:25:31
 * 
 * @see
 */
public class KpiForm extends Kpi {

    private static final long serialVersionUID = -5667854013775850988L;
    
    /**
     * 告警公式
     */
    private String alarmFormula;

    /*
     * 时间段
     */
    private String dateRange = "";

    /*
     * 采集部门/人员
     */
    private String gatherDept;

    /*
     * 所属部门/人员
     */
    private String ownDept;

    /*
     * 目标部门/人员
     */
    private String targetDept;

    /*
     * 查看部门/人员
     */
    private String viewDept;

    /*
     * 报告部门/人员
     */
    private String reportDept;

    /*
     * 是否监控
     */
    private String monitorStr;

    /*
     * 单位
     */
    private String unitsStr;

    /*
     * 状态
     */
    private String statusStr;

    /*
     * 主维度
     */
    private String mainDim;

    /*
     * 辅助维度
     */
    private String otherDim;

    /*
     * 指标类型
     */
    private String typeStr;

    /*
     * 数据类型
     */
    private String dataTypeStr;

    /*
     * 指标性质
     */
    private String kpiTypeStr;

    /*
     * 亮灯依据
     */
    private String alarmMeasureStr;

    /*
     * 预警依据
     */
    private String alarmBasisStr;

    /*
     * 趋势相对于
     */
    private String relativeToStr;

    /*
     * 开始时间字符串
     */
    private String startDateStr;

    /*
     * 结果采集频率数据字典
     */
    private String gatherFrequenceDict;

    /*
     * 趋势字符串
     */
    private String directionstr;

    /*
     * 状态字符串
     */
    private String estatus;

    /*
     * 实际值 double
     */
    private Double doubleFinishValue;

    /*
     * 目标值 double
     */
    private Double doubleTargetValue;

    /*
     * 评估值 double
     */
    private Double doubleAssessmentValue;

    /*
     * 实际值
     */
    private String finishValue;

    /*
     * 目标值
     */
    private String targetValue;

    /*
     * 评估值
     */
    private String assessmentValue;

    /*
     * 预警状态
     */
    private String forecastStatus;

    /*
     * 告警状态字符串
     */
    private String assessmentStatus;

    /*
     * 告警状态数据字典
     */

    private DictEntry assessmentStatusDict;

    /*
     * 趋势数据字典
     */
    private DictEntry directionDict;

    /*
     * 指标id
     */
    private String id;

    /*
     * 报表小数点位置
     */
    private Integer scale;

    /*
     * 名称
     */
    private String name;

    /*
     * 指标类型id
     */
    private String kpitypeid;

    /*
     * 是否继承字符串
     */
    private String isInheritStr;

    /*
     * 父指标id
     */
    private String parentKpiId;

    /*
     * 指标所属分类id
     */
    private String categoryId;

    /*
     * 添加或删除标识
     */
    private String opflag;

    /*
     * 是否使用默认名称
     */
    private String namedefault;

    /*
     * 结果值累计计算
     */
    private String resultSumMeasureStr;

    /*
     * 目标值累计计算
     */
    private String targetSumMeasureStr;

    /*
     * 结果收集频率
     */
    private String resultgatherfrequence;

    /*
     * 评估值累计计算
     */
    private String assessmentSumMeasureStr;

    /*
     * 目标收集报告频率
     */
    private String targetSetReportFrequenceStr;

    /*
     * 目标收集报告频率规则
     */
    private String targetSetReportFrequenceRule;

    /*
     * 目标收集报告频率数据字典
     */
    private String targetSetReportFrequenceDict;

    /*
     * 结果收集报告频率
     */
    private String reportFrequenceStr;

    /*
     * 结果收集报告频率规则
     */
    private String reportFrequenceRule;

    /*
     * 结果收集报告频率数据字典
     */
    private String reportFrequenceDict;

    /*
     * 结果采集频率规则
     */
    private String gatherfrequenceRule;

    /*
     * 结果收集延期天
     */
    private String resultCollectIntervalStr;

    /*
     * 目标收集延期天
     */
    private String targetSetIntervalStr;

    /*
     * 目标收集频率
     */
    private String targetSetFrequenceStr;

    /*
     * 目标收集频率规则
     */
    private String targetSetFrequenceRule;

    /*
     * 目标收集频率数据字典
     */
    private String targetSetFrequenceDict;

    /*
     * 结果收集频率时间换算
     */
    private String gatherValueCron;

    /*
     * 目标收集频率时间换算
     */
    private String targetValueCron;

    /*
     * 目标收集报告频率换算
     */
    private String gatherReportValueCron;

    /*
     * 前期实际值 double
     */
    private Double doublePreFinishValue;

    /*
     * 前期实际值 string
     */
    private String preFinishValue;

    /*
     * 去年同期实际值 double
     */
    private Double doublePreYearFinishValue;

    /*
     * 去年同期实际值 string
     */
    private String preYearFinishValue;

    /*
     * 最小值
     */
    private Double minValue;

    /*
     * 最大值
     */
    private Double maxValue;

    /*
     * 所属指标
     */
    private String belongKpi;
    
    /*
     * 关注状态
     */
    
    private String isfocustr;


	/*
     *目标收集报告频率换算 
     */
    private String targetReportCron;

    /*
     * 指标状态
     */
    private String kpistatus;
    /**
     * 是否计算
     */
    private String calcStr;
    /**
     * 采集结果时间纬度
     */
    private String timeperiod;
    /*
     * 备注
     */
    private String isMemo;
    /*
     * 备注--采集结果id
     */
    private String kgrId;
    /*
     * 备注信息字符串
     */
    private String memoStr;
    
	/**
     * 实际值采集数据源
     */
    private String resultDsStr;
    
    /**
     * 目标值采集数据源
     */
    private String targetDsStr;
    
    /**
     * 评估值采集数据源
     */
    private String asseessmentDsStr;
    
    
    /**
     * 是否关注
     */
    private String kpifocus;
    
    /**
     * 上级名称
     */
    private String parentName;
    
    /**
     * 所属人
     */
    private String owerName;
    /**
     * 权重
     */
    private String eweight;
    
    
    /**
     * 采集说明
     */
    private String gatherMemo;
    
    
    public String getGatherMemo() {
		return gatherMemo;
	}

	public void setGatherMemo(String gatherMemo) {
		this.gatherMemo = gatherMemo;
	}

	public String getEweight() {
		return eweight;
	}

	public void setEweight(String eweight) {
		this.eweight = eweight;
	}

	public String getKpifocus() {
        return kpifocus;
    }

    public void setKpifocus(String kpifocus) {
        this.kpifocus = kpifocus;
    }

    public String getTimeperiod() {
        return timeperiod;
    }

    public void setTimeperiod(String timeperiod) {
        this.timeperiod = timeperiod;
    }

    public String getCalcStr() {
        return calcStr;
    }

    public void setCalcStr(String calcStr) {
        this.calcStr = calcStr;
    }

    public String getKpistatus() {
        return kpistatus;
    }

    public void setKpistatus(String kpistatus) {
        this.kpistatus = kpistatus;
    }

    public String getTargetReportCron() {
        return targetReportCron;
    }

    public void setTargetReportCron(String targetReportCron) {
        this.targetReportCron = targetReportCron;
    }

    public String getGatherReportValueCron() {
        return gatherReportValueCron;
    }

    public void setGatherReportValueCron(String gatherReportValueCron) {
        this.gatherReportValueCron = gatherReportValueCron;
    }

    public String getBelongKpi() {
        return belongKpi;
    }

    public void setBelongKpi(String belongKpi) {
        this.belongKpi = belongKpi;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Double getDoublePreFinishValue() {
        return doublePreFinishValue;
    }

    public void setDoublePreFinishValue(Double doublePreFinishValue) {
        this.doublePreFinishValue = doublePreFinishValue;
    }

    public String getPreFinishValue() {
        return preFinishValue;
    }

    public void setPreFinishValue(String preFinishValue) {
        this.preFinishValue = preFinishValue;
    }

    public Double getDoublePreYearFinishValue() {
        return doublePreYearFinishValue;
    }

    public String getTargetValueCron() {
        return targetValueCron;
    }

    public void setTargetValueCron(String targetValueCron) {
        this.targetValueCron = targetValueCron;
    }

    public void setDoublePreYearFinishValue(Double doublePreYearFinishValue) {
        this.doublePreYearFinishValue = doublePreYearFinishValue;
    }

    public String getPreYearFinishValue() {
        return preYearFinishValue;
    }

    public void setPreYearFinishValue(String preYearFinishValue) {
        this.preYearFinishValue = preYearFinishValue;
    }

    public Double getDoubleFinishValue() {
        return doubleFinishValue;
    }

    public void setDoubleFinishValue(Double doubleFinishValue) {
        this.doubleFinishValue = doubleFinishValue;
    }

    public Double getDoubleTargetValue() {
        return doubleTargetValue;
    }

    public void setDoubleTargetValue(Double doubleTargetValue) {
        this.doubleTargetValue = doubleTargetValue;
    }

    public Double getDoubleAssessmentValue() {
        return doubleAssessmentValue;
    }

    public void setDoubleAssessmentValue(Double doubleAssessmentValue) {
        this.doubleAssessmentValue = doubleAssessmentValue;
    }

    @JsonIgnore
    public DictEntry getAssessmentStatusDict() {
        return assessmentStatusDict;
    }

    public void setAssessmentStatusDict(DictEntry assessmentStatusDict) {
        this.assessmentStatusDict = assessmentStatusDict;
    }

    @JsonIgnore
    public DictEntry getDirectionDict() {
        return directionDict;
    }

    public void setDirectionDict(DictEntry directionDict) {
        this.directionDict = directionDict;
    }

    public String getGatherValueCron() {
        return gatherValueCron;
    }

    public void setGatherValueCron(String gatherValueCron) {
        this.gatherValueCron = gatherValueCron;
    }

    public String getTargetSetReportFrequenceDict() {
        return targetSetReportFrequenceDict;
    }

    public void setTargetSetReportFrequenceDict(String targetSetReportFrequenceDict) {
        this.targetSetReportFrequenceDict = targetSetReportFrequenceDict;
    }

    public String getReportFrequenceDict() {
        return reportFrequenceDict;
    }

    public void setReportFrequenceDict(String reportFrequenceDict) {
        this.reportFrequenceDict = reportFrequenceDict;
    }

    public String getTargetSetFrequenceDict() {
        return targetSetFrequenceDict;
    }

    public void setTargetSetFrequenceDict(String targetSetFrequenceDict) {
        this.targetSetFrequenceDict = targetSetFrequenceDict;
    }

    public String getTargetSetReportFrequenceRule() {
        return targetSetReportFrequenceRule;
    }

    public void setTargetSetReportFrequenceRule(String targetSetReportFrequenceRule) {
        this.targetSetReportFrequenceRule = targetSetReportFrequenceRule;
    }

    public String getReportFrequenceRule() {
        return reportFrequenceRule;
    }

    public void setReportFrequenceRule(String reportFrequenceRule) {
        this.reportFrequenceRule = reportFrequenceRule;
    }

    public String getTargetSetFrequenceStr() {
        return targetSetFrequenceStr;
    }

    public void setTargetSetFrequenceStr(String targetSetFrequenceStr) {
        this.targetSetFrequenceStr = targetSetFrequenceStr;
    }

    public String getTargetSetFrequenceRule() {
        return targetSetFrequenceRule;
    }

    public void setTargetSetFrequenceRule(String targetSetFrequenceRule) {
        this.targetSetFrequenceRule = targetSetFrequenceRule;
    }

    public String getResultCollectIntervalStr() {
        return resultCollectIntervalStr;
    }

    public void setResultCollectIntervalStr(String resultCollectIntervalStr) {
        this.resultCollectIntervalStr = resultCollectIntervalStr;
    }

    public String getTargetSetIntervalStr() {
        return targetSetIntervalStr;
    }

    public void setTargetSetIntervalStr(String targetSetIntervalStr) {
        this.targetSetIntervalStr = targetSetIntervalStr;
    }

    public String getTargetSetReportFrequenceStr() {
        return targetSetReportFrequenceStr;
    }

    public void setTargetSetReportFrequenceStr(String targetSetReportFrequenceStr) {
        this.targetSetReportFrequenceStr = targetSetReportFrequenceStr;
    }

    public String getReportFrequenceStr() {
        return reportFrequenceStr;
    }

    public void setReportFrequenceStr(String reportFrequenceStr) {
        this.reportFrequenceStr = reportFrequenceStr;
    }

    public String getGatherfrequenceRule() {
        return gatherfrequenceRule;
    }

    public void setGatherfrequenceRule(String gatherfrequenceRule) {
        this.gatherfrequenceRule = gatherfrequenceRule;
    }

    public String getGatherFrequenceDict() {
        return gatherFrequenceDict;
    }

    public void setGatherFrequenceDict(String gatherFrequenceDict) {
        this.gatherFrequenceDict = gatherFrequenceDict;
    }

    public String getResultSumMeasureStr() {
        return resultSumMeasureStr;
    }

    public void setResultSumMeasureStr(String resultSumMeasureStr) {
        this.resultSumMeasureStr = resultSumMeasureStr;
    }

    public String getTargetSumMeasureStr() {
        return targetSumMeasureStr;
    }

    public void setTargetSumMeasureStr(String targetSumMeasureStr) {
        this.targetSumMeasureStr = targetSumMeasureStr;
    }

    public String getAssessmentSumMeasureStr() {
        return assessmentSumMeasureStr;
    }

    public void setAssessmentSumMeasureStr(String assessmentSumMeasureStr) {
        this.assessmentSumMeasureStr = assessmentSumMeasureStr;
    }

    public String getNamedefault() {
        return namedefault;
    }

    public void setNamedefault(String namedefault) {
        this.namedefault = namedefault;
    }

    public String getOpflag() {
        return opflag;
    }

    public void setOpflag(String opflag) {
        this.opflag = opflag;
    }

    public String getForecastStatus() {
        return forecastStatus;
    }

    public void setForecastStatus(String forecastStatus) {
        this.forecastStatus = forecastStatus;
    }

    public String getFinishValue() {
        return finishValue;
    }

    public void setFinishValue(String finishValue) {
        this.finishValue = finishValue;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public String getAssessmentValue() {
        return assessmentValue;
    }

    public void setAssessmentValue(String assessmentValue) {
        this.assessmentValue = assessmentValue;
    }

    public String getDirectionstr() {
        return directionstr;
    }

    public void setDirectionstr(String directionstr) {
        this.directionstr = directionstr;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getIsInheritStr() {
        return isInheritStr;
    }

    public void setIsInheritStr(String isInheritStr) {
        this.isInheritStr = isInheritStr;
    }

    public String getKpitypeid() {
        return kpitypeid;
    }

    public void setKpitypeid(String kpitypeid) {
        this.kpitypeid = kpitypeid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public String getMonitorStr() {
        return monitorStr;
    }

    public void setMonitorStr(String monitorStr) {
        this.monitorStr = monitorStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getGatherDept() {
        return gatherDept;
    }

    public void setGatherDept(String gatherDept) {
        this.gatherDept = gatherDept;
    }

    public String getOwnDept() {
        return ownDept;
    }

    public void setOwnDept(String ownDept) {
        this.ownDept = ownDept;
    }

    public String getTargetDept() {
        return targetDept;
    }

    public String getAlarmBasisStr() {
        return alarmBasisStr;
    }

    public void setAlarmBasisStr(String alarmBasisStr) {
        this.alarmBasisStr = alarmBasisStr;
    }

    public void setTargetDept(String targetDept) {
        this.targetDept = targetDept;
    }

    public String getViewDept() {
        return viewDept;
    }

    public void setViewDept(String viewDept) {
        this.viewDept = viewDept;
    }

    public String getReportDept() {
        return reportDept;
    }

    public void setReportDept(String reportDept) {
        this.reportDept = reportDept;
    }

    public String getUnitsStr() {
        return unitsStr;
    }

    public void setUnitsStr(String unitsStr) {
        this.unitsStr = unitsStr;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getMainDim() {
        return mainDim;
    }

    public void setMainDim(String mainDim) {
        this.mainDim = mainDim;
    }

    public String getOtherDim() {
        return otherDim;
    }

    public void setOtherDim(String otherDim) {
        this.otherDim = otherDim;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getDataTypeStr() {
        return dataTypeStr;
    }

    public void setDataTypeStr(String dataTypeStr) {
        this.dataTypeStr = dataTypeStr;
    }

    public String getKpiTypeStr() {
        return kpiTypeStr;
    }

    public void setKpiTypeStr(String kpiTypeStr) {
        this.kpiTypeStr = kpiTypeStr;
    }

    public String getAlarmMeasureStr() {
        return alarmMeasureStr;
    }

    public void setAlarmMeasureStr(String alarmMeasureStr) {
        this.alarmMeasureStr = alarmMeasureStr;
    }

    public String getRelativeToStr() {
        return relativeToStr;
    }

    public void setRelativeToStr(String relativeToStr) {
        this.relativeToStr = relativeToStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentKpiId() {
        return parentKpiId;
    }

    public void setParentKpiId(String parentKpiId) {
        this.parentKpiId = parentKpiId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getAssessmentStatus() {
        return assessmentStatus;
    }

    public void setAssessmentStatus(String assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }

    public String getResultgatherfrequence() {
        return resultgatherfrequence;
    }

    public void setResultgatherfrequence(String resultgatherfrequence) {
        this.resultgatherfrequence = resultgatherfrequence;
    }

    public String getAlarmFormula() {
        return alarmFormula;
    }

    public void setAlarmFormula(String alarmFormula) {
        this.alarmFormula = alarmFormula;
    }
    
    public String getResultDsStr() {
		return resultDsStr;
	}

	public void setResultDsStr(String resultDsStr) {
		this.resultDsStr = resultDsStr;
	}

	public String getTargetDsStr() {
		return targetDsStr;
	}

	public void setTargetDsStr(String targetDsStr) {
		this.targetDsStr = targetDsStr;
	}

	public String getAsseessmentDsStr() {
		return asseessmentDsStr;
	}

	public void setAsseessmentDsStr(String asseessmentDsStr) {
		this.asseessmentDsStr = asseessmentDsStr;
	}

    public String getIsMemo() {
		return isMemo;
	}

	public void setIsMemo(String isMemo) {
		this.isMemo = isMemo;
	}

	public String getKgrId() {
		return kgrId;
	}

	public void setKgrId(String kgrId) {
		this.kgrId = kgrId;
	}

	public String getMemoStr() {
		return memoStr;
	}

	public void setMemoStr(String memoStr) {
		this.memoStr = memoStr;
	}

	public String getIsfocustr() {
			return isfocustr;
	}

	public void setIsfocustr(String isfocustr) {
		this.isfocustr = isfocustr;
	}
		
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getOwerName() {
		return owerName;
	}

	public void setOwerName(String owerName) {
		this.owerName = owerName;
	}

	public KpiForm() {

    }
	
	private void convertFrequence(String frequence){
		if(null==frequence){
    		this.gatherFrequenceDict = "";
    	}else{
    		if(frequence.equals("0frequecy_month")){
    			this.gatherFrequenceDict = "每月";
    		}else if(frequence.equals("0frequecy_week")){
    			this.gatherFrequenceDict = "每周";
    		}else if(frequence.equals("0frequecy_quarter")){
    			this.gatherFrequenceDict = "每季";
    		}else if(frequence.equals("0frequecy_year")){
    			this.gatherFrequenceDict = "每年";
    		}else if(frequence.equals("0frequecy_halfyear")){
    			this.gatherFrequenceDict = "每半年";
    		}
    	}
	}
	
	private void convertWeight(Object weightObj){
		if(null == weightObj){
    		this.eweight = "";
    	}else{
    		BigDecimal weight = (BigDecimal) weightObj;
        	int weightInt = weight.intValue();
        	float weightFloat = weight.floatValue();
        	if(weightFloat>weightInt){
        		this.eweight = new DecimalFormat("0.0").format((BigDecimal) weightObj);//权重
        	}else{
        		this.eweight = new DecimalFormat("0").format((BigDecimal) weightObj);//权重
        	}
    	}
	}
	
	
	private String findMaximumFractionDigits(int position,Object value){
    	DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();
		df.setMaximumFractionDigits(position);
		return df.format(value);
    }
	
	private String convertValue(int position,Object valueObj){
		String valueStr = "";
		if(null == valueObj){
			valueStr = "";
    	}else{
    		BigDecimal value = (BigDecimal) valueObj;
        	int valueInt = value.intValue();
        	float valueFloat = value.floatValue();
        	if(valueFloat>valueInt){
        		valueStr = this.findMaximumFractionDigits(position, valueObj);//权重
        	}else{
        		valueStr = new DecimalFormat("0").format((BigDecimal) valueObj);//权重
        	}
    	}
		return valueStr;
	}
	

    public KpiForm(Object[] objs, String type) {
        if (null != objs) {
            this.id = (String) objs[0];//指标ID
            this.name = (String) objs[1];//指标名称
            this.dateRange = null == objs[2] ? "" : (String) objs[2];//时间段
            this.assessmentStatus = null == objs[6] ? "" : (String) objs[6];//告警状态
            this.directionstr = null == objs[7] ? "" : (String) objs[7];//趋势
            if ("chart".equals(type)) {
                if (objs.length > 8) {
                    this.preFinishValue = null == objs[8] ? "" : new DecimalFormat("0.00").format((BigDecimal) objs[8]);//前时间区间完成值
                }
                if (objs.length > 9) {
                    this.preYearFinishValue = null == objs[9] ? "" : new DecimalFormat("0.00").format((BigDecimal) objs[9]);//前年完成值
                }
                if(objs.length>10){
                    this.kpistatus = null == objs[10] ? "" : String.valueOf(objs[10]);//指标状态
                }
                if(objs.length>11){
                	this.kpifocus = null == objs[11] ? "" : String.valueOf(objs[11]);//是否关注
                }
                if(objs.length>12){
                	this.unitsStr = null == objs[12] ? "" : String.valueOf(objs[12]);//单位
                }
                if(objs.length>13){
                	convertFrequence(String.valueOf(objs[13]));
                }
                if(objs.length>14){
                	convertWeight(objs[14]);
                }
                if(objs.length>15){
                	this.scale = null == objs[15] ? Contents.DEFAULT_KPI_DOT_POSITION : Integer.parseInt(objs[15].toString());
                }
            }
            else if ("category".equals(type)) {

                if (objs.length > 8) {
                    this.belongKpi = null == objs[8] ? "" : String.valueOf(objs[8]);//所属指标
                }
                if (objs.length > 9) {
                    this.kpistatus = null == objs[9] ? "" : String.valueOf(objs[9]);//指标状态
                }
                if(objs.length>10){
                    this.timeperiod = null == objs[10] ? "" : String.valueOf(objs[10]);//时间区间纬度
                }
                if (objs.length >= 13) {
                	this.isMemo = null == objs[11] ? "" : String.valueOf(objs[11]);//备注
                    this.kgrId = null == objs[12] ? "" : String.valueOf(objs[12]);//备注--采集结果id
                }
                if(objs.length>13){
                    this.kpifocus = null == objs[13] ? "" : String.valueOf(objs[13]);//是否关注
                }
                if(objs.length>14){
                	this.unitsStr = null == objs[14] ? "" : String.valueOf(objs[14]);//单位
                }
                if(objs.length>15){
                	convertFrequence(String.valueOf(objs[15]));
                }
                if(objs.length>16){
                	convertWeight(objs[16]);
                }
                if(objs.length>17){
                	this.scale = null == objs[17] ? Contents.DEFAULT_KPI_DOT_POSITION : Integer.parseInt(objs[17].toString());
                }
            }
            else if ("sm".equals(type) ) {
                if (objs.length > 8) {
                    this.kpistatus = null == objs[8] ? "" : String.valueOf(objs[8]);//指标状态
                }
                if(objs.length>9){
                    this.timeperiod = null == objs[9] ? "" : String.valueOf(objs[9]);//时间区间纬度
                }
                if (objs.length >= 12) {
                	this.isMemo = null == objs[10] ? "" : String.valueOf(objs[10]);//备注
                    this.kgrId = null == objs[11] ? "" : String.valueOf(objs[11]);//备注--采集结果id
                }
                if(objs.length>12){
                    this.kpifocus = null == objs[12] ? "" : String.valueOf(objs[12]);//是否关注
                }
                if(objs.length>13){
                	this.unitsStr = null == objs[13] ? "" : String.valueOf(objs[13]);//单位
                }
                if(objs.length>14){
                	convertFrequence(String.valueOf(objs[14]));
                }
                if(objs.length>15){
                	convertWeight(objs[15]);
                }
                if(objs.length>16){
                	this.scale = null == objs[16] ? Contents.DEFAULT_KPI_DOT_POSITION : Integer.parseInt(objs[16].toString());
                }
            }else if("kpitype".equals(type) || "mykpi".equals(type) || "allkpi".equals(type)){
            	if (objs.length > 8) {
                    this.kpistatus = null == objs[8] ? "" : String.valueOf(objs[8]);//指标状态
                }
                if(objs.length>9){
                    this.timeperiod = null == objs[9] ? "" : String.valueOf(objs[9]);//时间区间纬度
                }
                if (objs.length >= 12) {
                	this.isMemo = null == objs[10] ? "" : String.valueOf(objs[10]);//备注
                    this.kgrId = null == objs[11] ? "" : String.valueOf(objs[11]);//备注--采集结果id
                }
                if(objs.length>12){
                    this.kpifocus = null == objs[12] ? "" : String.valueOf(objs[12]);//是否关注
                }
                if(objs.length>13){
                	this.unitsStr = null == objs[13] ? "" : String.valueOf(objs[13]);//单位
                }
                if(objs.length>14){
                	convertFrequence(String.valueOf(objs[14]));
                }
                if(objs.length>15){
                	this.scale = null == objs[15] ? Contents.DEFAULT_KPI_DOT_POSITION : Integer.parseInt(objs[15].toString());
                }
            }
            
            this.assessmentValue = null == objs[3] ? "" : convertValue(this.scale,objs[3]) ;//评估值
            this.targetValue = null == objs[4] ? "" :  convertValue(this.scale,objs[4]) + this.unitsStr;//目标值
            this.finishValue = null == objs[5] ? "" :  convertValue(this.scale,objs[5]) + this.unitsStr;//完成值
        }
    }

    public KpiForm(Kpi kpi) {
        this.id = kpi.getId();
        this.name = kpi.getName();
    }
}
