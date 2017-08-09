package com.fhd.entity.kpi;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;

public class KpiInfoDto implements Serializable {

	private static final long serialVersionUID = -7480225729051995484L;

	private String executeEmpId;

	private String id;

	/**
	 * KPI:指标，KC:指标类型
	 */
	private String isKpiCategory;

	/**
	 * 上级指标
	 */
	private Kpi parent;

	/**
	 * id全路径
	 */
	private String idSeq;

	/**
	 * 层次
	 */
	private Integer level;

	/**
	 * 是否叶子
	 */
	private Boolean isLeaf;

	/**
	 * 是否默认名称
	 */
	private Boolean isNameDefault;

	/**
	 * 是否继承
	 */
	private Boolean isInherit;

	/**
	 * 公司
	 */
	private SysOrganization company;

	/**
	 * 编号
	 */
	private String code;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 简称
	 */
	private String shortName;

	/**
	 * 说明
	 */
	private String desc;

	/**
	 * 采集频率 日、周、月、季、半年、每年
	 */
	private DictEntry gatherFrequence;

	/**
	 * 采集时间公式
	 */
	private String gatherDayFormulr;

	/**
	 * 结果采集扩充天数
	 */
	private Integer resultCollectInterval;

	/**
	 * 结果值累计计算
	 */
	private DictEntry resultSumMeasure;

	/**
	 * 目标值累计计算
	 */
	private DictEntry targetSumMeasure;

	/**
	 * 评估值累计计算
	 */
	private DictEntry assessmentSumMeasure;

	/**
	 * 目标收集频率
	 */
	private DictEntry targetSetFrequence;

	/**
	 * 目标设定时间公式
	 */
	private String targetSetDayFormular;

	/**
	 * 采集报告频率
	 */
	private DictEntry reportFrequence;

	/**
	 * 采集报告频率公式
	 */
	private String gatherReportDayFormulr;

	/**
	 * 目标收集报告频率
	 */
	private DictEntry targetSetReportFrequence;

	/**
	 * 目标收集报告频率公式
	 */
	private String targetSetReportDayFormulr;

	/**
	 * 起始日期
	 */
	private Date startDate;

	/**
	 * 目标公式定义
	 */
	private String targetFormula;

	/**
	 * 结果公式定义
	 */
	private String resultFormula;

	/**
	 * 关联关系公式
	 */
	private String relationFormula;

	/**
	 * 目标设定扩充天数
	 */
	private Integer targetSetInterval;

	/**
	 * 排列顺序
	 */
	private Integer sort;

	/**
	 * 删除状态
	 */
	private Boolean deleteStatus;

	/**
	 * 状态：
	 */
	private DictEntry status;

	/**
	 * 单位
	 */
	private DictEntry units;

	/**
	 * 是否计算
	 */
	private DictEntry calc;

	/**
	 * 目标值别名
	 */
	private String targetValueAlias;

	/**
	 * 结果值别名
	 */
	private String resultValueAlias;

	/**
	 * 是否监控
	 */
	private Boolean isMonitor;

	/**
	 * 监控状态
	 */
	private String monitorStatus;

	/**
	 * 预警依据
	 */
	private DictEntry alarmBasis;

	/**
	 * 数据类型
	 */
	private DictEntry dataType;

	/**
	 * 指标性质
	 */
	private DictEntry kpiType;

	/**
	 * 当前数据趋势相对于
	 */
	private DictEntry relativeTo;

	/**
	 * 评估公式
	 */
	private String assessmentFormula;

	/**
	 * 报表小数点位置
	 */
	private Integer scale;

	/**
	 * 预警值公式
	 */
	private String forecastFormula;

	/**
	 * 亮灯依据
	 */
	private DictEntry alarmMeasure;

	/**
	 * 指标所属类型
	 */
	private Kpi belongKpiCategory;

	/**
	 * 类型 正向指标、逆向指标。。。
	 */
	private DictEntry type;

	/**
	 * 目标值是否使用公式
	 */
	private String isTargetFormula;

	/**
	 * 结果值是否使用公式
	 */
	private String isResultFormula;

	/**
	 * 评估值是否使用公式
	 */
	private String isAssessmentFormula;

	/**
	 * 标杆值
	 */
	private Double modelValue;

	/**
	 * 最大值
	 */
	private Double maxValue;

	/**
	 * 最小值
	 */
	private Double minValue;

	/**
	 * 是否关注
	 */
	private String isFocus;

	/**
	 * 指标关联采集信息
	 */
	private Set<KpiGatherResult> kpiGatherResult = new HashSet<KpiGatherResult>(
			0);

	/**
	 * 最后采集时间频率
	 */
	private TimePeriod lastTimePeriod;

	/**
	 * 结果收集频率显示
	 */
	private String gatherDayFormulrShow;

	/**
	 * 目标收集频率显示
	 */
	private String targetSetDayFormularShow;

	/**
	 * 结果收集报告频率显示
	 */
	private String gatherReportDayFormulrShow;

	/**
	 * 目标收集报告频率显示
	 */
	private String targetReportDayFormulrShow;

	/**
	 * 指标采集频率时间换算
	 */
	private Date calculatetime;

	/**
	 * 指标目标频率时间换算
	 */
	private Date targetCalculatetime;

	/**
	 * 实际值采集外部数据源类型(sql语句或存储过程)
	 */
	private String isResultDsType;

	/**
	 * 目标值采集外部数据源类型(sql语句或存储过程)
	 */
	private String isTargetDsType;

	/**
	 * 评估值采集外部数据源类型(sql语句或存储过程)
	 */
	private String isAssessmentDsType;

	/**
	 * 实际值外部数据源
	 */
	private KpiDataSource resultDs;

	/**
	 * 目标值外部数据源
	 */
	private KpiDataSource targetDs;

	/**
	 * 评估值外部数据源
	 */
	private KpiDataSource asseessmentDs;

	/**
	 * 指标采集频率时间换算临时字段
	 */
	private Date tmpCalculatetime;

	/**
	 * 最后采集时间频率临时字段
	 */
	private TimePeriod tmpLastTimePeriod;

	/**
	 * 采集说明
	 */
	private String gatherDesc;

	public String getGatherDesc() {
		return gatherDesc;
	}

	public void setGatherDesc(String gatherDesc) {
		this.gatherDesc = gatherDesc;
	}

	public Date getTmpCalculatetime() {
		return tmpCalculatetime;
	}

	public void setTmpCalculatetime(Date tmpCalculatetime) {
		this.tmpCalculatetime = tmpCalculatetime;
	}

	public TimePeriod getTmpLastTimePeriod() {
		return tmpLastTimePeriod;
	}

	public void setTmpLastTimePeriod(TimePeriod tmpLastTimePeriod) {
		this.tmpLastTimePeriod = tmpLastTimePeriod;
	}

	public String getIsFocus() {
		return isFocus;
	}

	public void setIsFocus(String isFocus) {
		this.isFocus = isFocus;
	}

	public String getIsKpiCategory() {
		return isKpiCategory;
	}

	public void setIsKpiCategory(String isKpiCategory) {
		this.isKpiCategory = isKpiCategory;
	}

	public Kpi getParent() {
		return parent;
	}

	public void setParent(Kpi parent) {
		this.parent = parent;
	}

	public String getIdSeq() {
		return idSeq;
	}

	public String getTargetReportDayFormulrShow() {
		return targetReportDayFormulrShow;
	}

	public void setTargetReportDayFormulrShow(String targetReportDayFormulrShow) {
		this.targetReportDayFormulrShow = targetReportDayFormulrShow;
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

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
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

	public DictEntry getGatherFrequence() {
		return gatherFrequence;
	}

	public void setGatherFrequence(DictEntry gatherFrequence) {
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

	public DictEntry getResultSumMeasure() {
		return resultSumMeasure;
	}

	public void setResultSumMeasure(DictEntry resultSumMeasure) {
		this.resultSumMeasure = resultSumMeasure;
	}

	public DictEntry getTargetSumMeasure() {
		return targetSumMeasure;
	}

	public void setTargetSumMeasure(DictEntry targetSumMeasure) {
		this.targetSumMeasure = targetSumMeasure;
	}

	public DictEntry getAssessmentSumMeasure() {
		return assessmentSumMeasure;
	}

	public void setAssessmentSumMeasure(DictEntry assessmentSumMeasure) {
		this.assessmentSumMeasure = assessmentSumMeasure;
	}

	public DictEntry getTargetSetFrequence() {
		return targetSetFrequence;
	}

	public void setTargetSetFrequence(DictEntry targetSetFrequence) {
		this.targetSetFrequence = targetSetFrequence;
	}

	public String getTargetSetDayFormular() {
		return targetSetDayFormular;
	}

	public void setTargetSetDayFormular(String targetSetDayFormular) {
		this.targetSetDayFormular = targetSetDayFormular;
	}

	public DictEntry getReportFrequence() {
		return reportFrequence;
	}

	public void setReportFrequence(DictEntry reportFrequence) {
		this.reportFrequence = reportFrequence;
	}

	public String getGatherReportDayFormulr() {
		return gatherReportDayFormulr;
	}

	public void setGatherReportDayFormulr(String gatherReportDayFormulr) {
		this.gatherReportDayFormulr = gatherReportDayFormulr;
	}

	public DictEntry getTargetSetReportFrequence() {
		return targetSetReportFrequence;
	}

	public void setTargetSetReportFrequence(DictEntry targetSetReportFrequence) {
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

	public DictEntry getStatus() {
		return status;
	}

	public void setStatus(DictEntry status) {
		this.status = status;
	}

	public DictEntry getUnits() {
		return units;
	}

	public void setUnits(DictEntry units) {
		this.units = units;
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

	public DictEntry getAlarmBasis() {
		return alarmBasis;
	}

	public void setAlarmBasis(DictEntry alarmBasis) {
		this.alarmBasis = alarmBasis;
	}

	public DictEntry getDataType() {
		return dataType;
	}

	public void setDataType(DictEntry dataType) {
		this.dataType = dataType;
	}

	public DictEntry getKpiType() {
		return kpiType;
	}

	public void setKpiType(DictEntry kpiType) {
		this.kpiType = kpiType;
	}

	public DictEntry getRelativeTo() {
		return relativeTo;
	}

	public void setRelativeTo(DictEntry relativeTo) {
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

	public DictEntry getAlarmMeasure() {
		return alarmMeasure;
	}

	public void setAlarmMeasure(DictEntry alarmMeasure) {
		this.alarmMeasure = alarmMeasure;
	}

	public Kpi getBelongKpiCategory() {
		return belongKpiCategory;
	}

	public void setBelongKpiCategory(Kpi belongKpiCategory) {
		this.belongKpiCategory = belongKpiCategory;
	}

	public DictEntry getType() {
		return type;
	}

	public void setType(DictEntry type) {
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

	public Set<KpiGatherResult> getKpiGatherResult() {
		return kpiGatherResult;
	}

	public void setKpiGatherResult(Set<KpiGatherResult> kpiGatherResult) {
		this.kpiGatherResult = kpiGatherResult;
	}

	public TimePeriod getLastTimePeriod() {
		return lastTimePeriod;
	}

	public void setLastTimePeriod(TimePeriod lastTimePeriod) {
		this.lastTimePeriod = lastTimePeriod;
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

	public DictEntry getCalc() {
		return calc;
	}

	public void setCalc(DictEntry calc) {
		this.calc = calc;
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

	public KpiDataSource getResultDs() {
		return resultDs;
	}

	public void setResultDs(KpiDataSource resultDs) {
		this.resultDs = resultDs;
	}

	public KpiDataSource getTargetDs() {
		return targetDs;
	}

	public void setTargetDs(KpiDataSource targetDs) {
		this.targetDs = targetDs;
	}

	public KpiDataSource getAsseessmentDs() {
		return asseessmentDs;
	}

	public void setAsseessmentDs(KpiDataSource asseessmentDs) {
		this.asseessmentDs = asseessmentDs;
	}

	public String getExecuteEmpId() {
		return executeEmpId;
	}

	public void setExecuteEmpId(String executeEmpId) {
		this.executeEmpId = executeEmpId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
