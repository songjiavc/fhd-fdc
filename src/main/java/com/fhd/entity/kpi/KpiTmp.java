package com.fhd.entity.kpi;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.base.IdEntity;
@Entity
@Table(name = "TMP_IMP_KPI_KPI")
public class KpiTmp extends IdEntity  implements Serializable{
	
    private static final long serialVersionUID = 2756564806248230798L;
    


	/**
     * KPI:指标，KC:指标类型
     */
    @Column(name = "IS_KPI_CATEGORY", length = 2000)
    private String isKpiCategory;

    /**
     * 上级指标
     */
    @Column(name = "PARENT_ID")
    private String parent;

    /**
     * id全路径
     */
    @Column(name = "ID_SEQ", length = 2000)
    private String idSeq;
    
    /**
     * 层次
     */
    @Column(name = "ELEVEL")
    private Integer level;

    /**
     * 是否叶子
     */
    @Column(name = "IS_LEAF")
    private Boolean isLeaf;

    /**
     * 是否默认名称
     */
    @Column(name = "IS_USE_DEFAULT_NAME")
    private Boolean isNameDefault;
    
    /**
     * 是否继承
     */
    @Column(name = "IS_INHERIT")
    private Boolean isInherit;

    /**
     * 公司
     */
    @Column(name = "COMPANY_ID")
    private String company;
    /**
     * 编号
     */
    @Column(name = "KPI_CODE")
    private String code;

    /**
     * 名称
     */
    @Column(name = "KPI_NAME")
    private String name;

    /**
     * 简称
     */
    @Column(name = "SHORT_NAME")
    private String shortName;

    /**
     * 说明
     */
    @Column(name = "EDESC", length = 2000)
    private String desc;
    
    /**
     * 采集频率   日、周、月、季、半年、每年
     */
    @Column(name = "GATHER_FREQUENCE")
    private String gatherFrequence;
    
    /**
     * 采集时间公式
     */
    @Column(name = "GATHER_DAY_FORMULR", length = 2000)
    private String gatherDayFormulr;

    /**
     * 结果采集扩充天数
     */
    @Column(name = "RESULT_COLLECT_INTERVAL")
    private Integer resultCollectInterval;

    /**
     * 结果值累计计算
     */
    @Column(name = "FINISH_VALUE_SUM_MEASURE")
    private String resultSumMeasure;
    
    /**
     * 目标值累计计算
     */
    @Column(name = "TARGET_VALUE_SUM_MEASURE")
    private String targetSumMeasure;
    
    /**
     * 评估值累计计算
     */
    @Column(name = "ASSESSMENT_VALUE_SUM_MEASURE")
    private String assessmentSumMeasure;
    
    /**
     * 目标收集频率
     */
    @Column(name = "TARGET_SET_FREQUENCE")
    private String targetSetFrequence;
    
    /**
     * 目标设定时间公式
     */
    @Column(name = "TARGET_SET_DAY_FORMULAR", length = 2000)
    private String targetSetDayFormular;

    /**
     * 采集报告频率
     */
    @Column(name = "GATHER_REPORT_FREQUENCE")
    private String reportFrequence;
    
    /**
     * 采集报告频率公式
     */
    @Column(name = "GATHER_REPORT_DAY_FORMULR")
    private String gatherReportDayFormulr;
    
    /**
     * 目标收集报告频率
     */
    @Column(name = "TARGET_SET_REPORT_FREQUENCE")
    private String targetSetReportFrequence;

    /**
     * 目标收集报告频率公式
     */
    @Column(name = "TARGET_SET_REPORT_DAY_FORMULR")
    private String targetSetReportDayFormulr;

    /**
     * 起始日期
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", length = 0)
    private Date startDate;
    
    /**
     * 目标公式定义
     */
    @Column(name = "TARGET_FORMULA", length = 2000)
    private String targetFormula;

    /**
     * 结果公式定义
     */
    @Column(name = "RESULT_FORMULA", length = 2000)
    private String resultFormula;

    /**
     * 关联关系公式
     */
    @Column(name = "RELATION_FORMULA")
    private String relationFormula;
    
    /**
     * 目标设定扩充天数
     */
    @Column(name = "TARGET_SET_INTERVAL")
    private Integer targetSetInterval;

    /**
     * 排列顺序
     */
    @Column(name = "ESORT")
    private Integer sort;

    /**
     * 删除状态
     */
    @Column(name = "DELETE_STATUS", length = 100)
    private Boolean deleteStatus;
    
    /**
     * 状态：
     */
    @Column(name = "IS_ENABLED")
    private String status;

    /**
     * 单位
     */
    @Column(name = "UNITS")
    private String units;
    
    /**
     * 是否计算
     */
    @Column(name = "IS_CALC")
    private String calc;

    /**
     * 目标值别名
     */
    @Column(name = "TARGET_VALUE_ALIAS")
    private String targetValueAlias;

    /**
     * 结果值别名
     */
    @Column(name = "RESULT_VALUE_ALIAS")
    private String resultValueAlias;

    /**
     * 是否监控
     */
    @Column(name = "IS_MONITOR", length = 100)
    private Boolean isMonitor;

    /**
     * 监控状态
     */
    @Column(name = "MONITOR_STATUS")
    private String monitorStatus;

    /**
     * 预警依据
     */
    @Column(name = "ALARM_BASIS")
    private String alarmBasis;

    /**
     * 数据类型
     */
    @Column(name = "DATA_TYPE")
    private String dataType;

    /**
     * 指标性质
     */
    @Column(name = "KPI_TYPE")
    private String kpiType;

    /**
     * 当前数据趋势相对于
     */
    @Column(name = "RELATIVE_TO")
    private String relativeTo;
    
    /**
     * 评估公式
     */
    @Column(name = "ASSESSMENT_FORMULA")
    private String assessmentFormula;

    /**
     * 报表小数点位置
     */
    @Column(name = "ESCALE")
    private Integer scale;

    /**
     * 预警值公式
     */
    @Column(name = "FORECAST_FORMULA")
    private String forecastFormula;

    /**
     * 亮灯依据
     */
    @Column(name = "ALARM_MEASURE")
    private String alarmMeasure;
    
    /**
     * 指标所属类型
     */
    @Column(name = "BELONG_KPI_CATEGORY")
    private String belongKpiCategory;

    /**
     * 类型   正向指标、逆向指标。。。
     */
    @Column(name = "ETYPE")
    private String type;

    /**
     * 目标值是否使用公式
     */
    @Column(name = "IS_TARGET_FORMULA")
    private String isTargetFormula;

    /**
     * 结果值是否使用公式
     */
    @Column(name = "IS_RESULT_FORMULA")
    private String isResultFormula;

    /**
     * 评估值是否使用公式
     */
    @Column(name = "IS_ASSESSMENT_FORMULA")
    private String isAssessmentFormula;
    
    /**
     * 标杆值
     */
    @Column(name = "MODEL_VALUE")
    private Double modelValue;

    /**
     * 最大值
     */
    @Column(name = "MAX_VALUE")
    private Double maxValue;

    /**
     * 最小值
     */
    @Column(name = "MIN_VALUE")
    private Double minValue;
    
    /**
     * 结果收集频率显示
     */
    @Column(name = "GATHER_DAY_FORMULR_SHOW")
    private String gatherDayFormulrShow;

    /**
     * 目标收集频率显示
     */
    @Column(name = "TARGET_SET_DAY_FORMULAR_SHOW")
    private String targetSetDayFormularShow;

    /**
     * 结果收集报告频率显示
     */
    @Column(name = "GATHER_REPORT_DAY_FORMULR_SHOW")
    private String gatherReportDayFormulrShow;

    /**
     * 目标收集报告频率显示
     */
    @Column(name = "TARGET_REPORT_DAY_FORMULR_SHOW")
    private String targetReportDayFormulrShow;

    /**
     * 指标采集频率时间换算
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "CALCULATE_TIME")
    private Date calculatetime;

    /**
     * 指标目标频率时间换算
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "TARGET_CALCULATE_TIME")
    private Date targetCalculatetime;
    
    /**
     *  实际值采集外部数据源类型(sql语句或存储过程)
     */
    @Column(name = "IS_RESULT_DS_TYPE")
    private String isResultDsType;
    
	/**
     *  目标值采集外部数据源类型(sql语句或存储过程)
     */
    @Column(name = "IS_TARGET_DS_TYPE")
    private String isTargetDsType;
    
    /**
     *  评估值采集外部数据源类型(sql语句或存储过程)
     */
    @Column(name = "IS_ASSESSMENT_DS_TYPE")
    private String isAssessmentDsType;
    
    /**
     *   实际值外部数据源
     */
    @Column(name = "RESULT_DS_ID")
    private String resultDs;
    
    /**
     *   目标值外部数据源
     */
    @Column(name = "TARGET_DS_ID")
    private String targetDs;
    
    /**
     *   评估值外部数据源
     */
    @Column(name = "ASSESSMENT_DS_ID")
    private String asseessmentDs;
   

	/**
     * 指标所属部门/人员
     */
    @Column(name = "OWNER_DEPT")
    private String owenrDept;
    /**
     * 指标采集部门/人员
     */
    @Column(name="COLLECT_DEPT")
    private String collectDept;
    
    /**
     * 目标部门/人员
     */
    @Column(name = "TARGET_DEPT")
    private String targetDept;
    
    /**
     * 报告部门/人员
     */
    @Column(name ="REPORT_DEPT")
    private String reportDept;
    
    /**
     * 查看部门/人员
     */
    @Column(name ="CHECK_DEPT")
    private String checkDept;
    
    /**
     * 方案生效日期
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "WARNING_EFF_DATE")
    private Date warningEffDate;
    
    /**
     * 告警方案
     */
    @Column(name = "WARNING_SET")
    private String warningSet;
    
    /**
     * 预警方案
     */
    @Column(name = "FORE_WARNING_SET")
    private String foreWarningSet;
    


	/**
     * 主维度
     */
    @Column(name = "MAIN_DIM")
    private String mainDim;
    
    /**
     * 辅助维度
     */
    @Column(name = "SUPPORT_DIM")
    private String supportDim;
    
	/**
     * 校验信息
     */
    @Column(name ="VALIDATE_INFO")
    private String validateInfo;
    
	/**
     * 行号
     */
    @Column(name = "EXCEL_ROWNO")
    private String rowNum;
    
    
    public String getRowNum() {
		return rowNum;
	}

	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}
    
    public String getIsKpiCategory() {
		return isKpiCategory;
	}

	public void setIsKpiCategory(String isKpiCategory) {
		this.isKpiCategory = isKpiCategory;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getIdSeq() {
		return idSeq;
	}

	public void setIdSeq(String idSeq) {
		this.idSeq = idSeq;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Boolean getIsNameDefault() {
		return isNameDefault;
	}

	public void setIsNameDefault(Boolean isNameDefault) {
		this.isNameDefault = isNameDefault;
	}

	public Boolean getIsInherit() {
		return isInherit;
	}

	public void setIsInherit(Boolean isInherit) {
		this.isInherit = isInherit;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGatherFrequence() {
		return gatherFrequence;
	}

	public void setGatherFrequence(String gatherFrequence) {
		this.gatherFrequence = gatherFrequence;
	}

	public String getGatherDayFormulr() {
		return gatherDayFormulr;
	}

	public void setGatherDayFormulr(String gatherDayFormulr) {
		this.gatherDayFormulr = gatherDayFormulr;
	}

	public Integer getResultCollectInterval() {
		return resultCollectInterval;
	}

	public void setResultCollectInterval(Integer resultCollectInterval) {
		this.resultCollectInterval = resultCollectInterval;
	}

	public String getResultSumMeasure() {
		return resultSumMeasure;
	}

	public void setResultSumMeasure(String resultSumMeasure) {
		this.resultSumMeasure = resultSumMeasure;
	}

	public String getTargetSumMeasure() {
		return targetSumMeasure;
	}

	public void setTargetSumMeasure(String targetSumMeasure) {
		this.targetSumMeasure = targetSumMeasure;
	}

	public String getAssessmentSumMeasure() {
		return assessmentSumMeasure;
	}

	public void setAssessmentSumMeasure(String assessmentSumMeasure) {
		this.assessmentSumMeasure = assessmentSumMeasure;
	}

	public String getTargetSetFrequence() {
		return targetSetFrequence;
	}

	public void setTargetSetFrequence(String targetSetFrequence) {
		this.targetSetFrequence = targetSetFrequence;
	}

	public String getTargetSetDayFormular() {
		return targetSetDayFormular;
	}

	public void setTargetSetDayFormular(String targetSetDayFormular) {
		this.targetSetDayFormular = targetSetDayFormular;
	}

	public String getReportFrequence() {
		return reportFrequence;
	}

	public void setReportFrequence(String reportFrequence) {
		this.reportFrequence = reportFrequence;
	}

	public String getGatherReportDayFormulr() {
		return gatherReportDayFormulr;
	}

	public void setGatherReportDayFormulr(String gatherReportDayFormulr) {
		this.gatherReportDayFormulr = gatherReportDayFormulr;
	}

	public String getTargetSetReportFrequence() {
		return targetSetReportFrequence;
	}

	public void setTargetSetReportFrequence(String targetSetReportFrequence) {
		this.targetSetReportFrequence = targetSetReportFrequence;
	}

	public String getTargetSetReportDayFormulr() {
		return targetSetReportDayFormulr;
	}

	public void setTargetSetReportDayFormulr(String targetSetReportDayFormulr) {
		this.targetSetReportDayFormulr = targetSetReportDayFormulr;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getTargetFormula() {
		return targetFormula;
	}

	public void setTargetFormula(String targetFormula) {
		this.targetFormula = targetFormula;
	}

	public String getResultFormula() {
		return resultFormula;
	}

	public void setResultFormula(String resultFormula) {
		this.resultFormula = resultFormula;
	}

	public String getRelationFormula() {
		return relationFormula;
	}

	public void setRelationFormula(String relationFormula) {
		this.relationFormula = relationFormula;
	}

	public Integer getTargetSetInterval() {
		return targetSetInterval;
	}

	public void setTargetSetInterval(Integer targetSetInterval) {
		this.targetSetInterval = targetSetInterval;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Boolean getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getCalc() {
		return calc;
	}

	public void setCalc(String calc) {
		this.calc = calc;
	}

	public String getTargetValueAlias() {
		return targetValueAlias;
	}

	public void setTargetValueAlias(String targetValueAlias) {
		this.targetValueAlias = targetValueAlias;
	}

	public String getResultValueAlias() {
		return resultValueAlias;
	}

	public void setResultValueAlias(String resultValueAlias) {
		this.resultValueAlias = resultValueAlias;
	}

	public Boolean getIsMonitor() {
		return isMonitor;
	}

	public void setIsMonitor(Boolean isMonitor) {
		this.isMonitor = isMonitor;
	}

	public String getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(String monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public String getAlarmBasis() {
		return alarmBasis;
	}

	public void setAlarmBasis(String alarmBasis) {
		this.alarmBasis = alarmBasis;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getKpiType() {
		return kpiType;
	}

	public void setKpiType(String kpiType) {
		this.kpiType = kpiType;
	}

	public String getRelativeTo() {
		return relativeTo;
	}

	public void setRelativeTo(String relativeTo) {
		this.relativeTo = relativeTo;
	}

	public String getAssessmentFormula() {
		return assessmentFormula;
	}

	public void setAssessmentFormula(String assessmentFormula) {
		this.assessmentFormula = assessmentFormula;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getForecastFormula() {
		return forecastFormula;
	}

	public void setForecastFormula(String forecastFormula) {
		this.forecastFormula = forecastFormula;
	}

	public String getAlarmMeasure() {
		return alarmMeasure;
	}

	public void setAlarmMeasure(String alarmMeasure) {
		this.alarmMeasure = alarmMeasure;
	}

	public String getBelongKpiCategory() {
		return belongKpiCategory;
	}

	public void setBelongKpiCategory(String belongKpiCategory) {
		this.belongKpiCategory = belongKpiCategory;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsTargetFormula() {
		return isTargetFormula;
	}

	public void setIsTargetFormula(String isTargetFormula) {
		this.isTargetFormula = isTargetFormula;
	}

	public String getIsResultFormula() {
		return isResultFormula;
	}

	public void setIsResultFormula(String isResultFormula) {
		this.isResultFormula = isResultFormula;
	}

	public String getIsAssessmentFormula() {
		return isAssessmentFormula;
	}

	public void setIsAssessmentFormula(String isAssessmentFormula) {
		this.isAssessmentFormula = isAssessmentFormula;
	}

	public Double getModelValue() {
		return modelValue;
	}

	public void setModelValue(Double modelValue) {
		this.modelValue = modelValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public String getGatherDayFormulrShow() {
		return gatherDayFormulrShow;
	}

	public void setGatherDayFormulrShow(String gatherDayFormulrShow) {
		this.gatherDayFormulrShow = gatherDayFormulrShow;
	}

	public String getTargetSetDayFormularShow() {
		return targetSetDayFormularShow;
	}

	public void setTargetSetDayFormularShow(String targetSetDayFormularShow) {
		this.targetSetDayFormularShow = targetSetDayFormularShow;
	}

	public String getGatherReportDayFormulrShow() {
		return gatherReportDayFormulrShow;
	}

	public void setGatherReportDayFormulrShow(String gatherReportDayFormulrShow) {
		this.gatherReportDayFormulrShow = gatherReportDayFormulrShow;
	}

	public String getTargetReportDayFormulrShow() {
		return targetReportDayFormulrShow;
	}

	public void setTargetReportDayFormulrShow(String targetReportDayFormulrShow) {
		this.targetReportDayFormulrShow = targetReportDayFormulrShow;
	}

	public Date getCalculatetime() {
		return calculatetime;
	}

	public void setCalculatetime(Date calculatetime) {
		this.calculatetime = calculatetime;
	}

	public Date getTargetCalculatetime() {
		return targetCalculatetime;
	}

	public void setTargetCalculatetime(Date targetCalculatetime) {
		this.targetCalculatetime = targetCalculatetime;
	}

	public String getIsResultDsType() {
		return isResultDsType;
	}

	public void setIsResultDsType(String isResultDsType) {
		this.isResultDsType = isResultDsType;
	}

	public String getIsTargetDsType() {
		return isTargetDsType;
	}

	public void setIsTargetDsType(String isTargetDsType) {
		this.isTargetDsType = isTargetDsType;
	}

	public String getIsAssessmentDsType() {
		return isAssessmentDsType;
	}

	public void setIsAssessmentDsType(String isAssessmentDsType) {
		this.isAssessmentDsType = isAssessmentDsType;
	}

	public String getResultDs() {
		return resultDs;
	}

	public void setResultDs(String resultDs) {
		this.resultDs = resultDs;
	}

	public String getTargetDs() {
		return targetDs;
	}

	public void setTargetDs(String targetDs) {
		this.targetDs = targetDs;
	}

	public String getAsseessmentDs() {
		return asseessmentDs;
	}

	public void setAsseessmentDs(String asseessmentDs) {
		this.asseessmentDs = asseessmentDs;
	}
	
	 public String getOwenrDept() {
			return owenrDept;
    }

	public void setOwenrDept(String owenrDept) {
		this.owenrDept = owenrDept;
	}

	public String getCollectDept() {
		return collectDept;
	}

	public void setCollectDept(String collectDept) {
		this.collectDept = collectDept;
	}

	public String getTargetDept() {
		return targetDept;
	}

	public void setTargetDept(String targetDept) {
		this.targetDept = targetDept;
	}

	public String getReportDept() {
		return reportDept;
	}

	public void setReportDept(String reportDept) {
		this.reportDept = reportDept;
	}

	public String getCheckDept() {
		return checkDept;
	}

	public void setCheckDept(String checkDept) {
		this.checkDept = checkDept;
	}

	public Date getWarningEffDate() {
		return warningEffDate;
	}

	public void setWarningEffDate(Date warningEffDate) {
		this.warningEffDate = warningEffDate;
	}

	public String getWarningSet() {
		return warningSet;
	}

	public void setWarningSet(String warningSet) {
		this.warningSet = warningSet;
	}

	public String getForeWarningSet() {
		return foreWarningSet;
	}

	public void setForeWarningSet(String foreWarningSet) {
		this.foreWarningSet = foreWarningSet;
	}
    public String getValidateInfo() {
		return validateInfo;
	}

	public void setValidateInfoString(String validateInfo) {
		this.validateInfo = validateInfo;
	}
    public String getMainDim() {
		return mainDim;
	}

	public void setMainDim(String mainDim) {
		this.mainDim = mainDim;
	}

	public String getSupportDim() {
		return supportDim;
	}

	public void setSupportDim(String supportDim) {
		this.supportDim = supportDim;
	}


}
