package com.fhd.entity.kpi;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

@Entity
@Table(name = "tmp_imp_strategy_map")
public class StrategyMapTmp extends IdEntity implements java.io.Serializable {
	private static final long serialVersionUID = 4405628221874997598L;

	/**
	 * 行号
	 */
	@Column(name = "ROW_NO")
	private Integer rowNo;

	/**
	 * 上级目标
	 */
	@Column(name = "PARENT_ID")
	private String parent;

	/**
	 * 名称
	 */
	@Column(name = "STRATEGY_MAP_NAME")
	private String name;

	/**
	 * 预警公式
	 */
	@Column(name = "FORECAST_FORMULA")
	private String warningFormula;

	/**
	 * 评估值公式
	 */
	@Column(name = "ASSESSMENT_FORMULA")
	private String assessmentFormula;

	/**
	 * 编号：
	 */
	@Column(name = "STRATEGY_MAP_CODE")
	private String code = "";

	/**
	 * 简称
	 */
	@Column(name = "SHORT_NAME")
	private String shortName;

	/**
	 * ID层级序列：例.parentId.id.
	 */
	@Column(name = "ID_SEQ")
	private String idSeq;

	/**
	 * 层级：最高层为1
	 */
	@Column(name = "ELEVEL")
	private Integer level;

	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;

	/**
	 * 是否叶子节点
	 */
	@Column(name = "IS_LEAF")
	private Boolean isLeaf;

	/**
	 * 说明
	 */
	@Column(name = "EDESC")
	private String desc;

	/**
	 * 图表类型
	 */
	@Column(name = "CHART_TYPE")
	private String chartType;
	/**
	 * 公司id
	 */
	@Column(name = "COMPANY_ID")
	private String companyId;

	/**
	 * 状态
	 */
	@Column(name = "estatus")
	private String status;

	/**
	 * 当前数据趋势相对于
	 */
	@Column(name = "RELATIVE_TO")
	private String relativeTo;

	/**
	 * 是否关注
	 */
	@Column(name = "IS_FOCUS")
	private String isFocus;

	/**
	 * 查看部门
	 */
	@Column(name = "CHECK_DEPT")
	private String checkDept;
	/**
	 * 报告部门
	 */
	@Column(name = "REPORT_DEPT")
	private String reportDept;
	/**
	 * 所属部门
	 */
	@Column(name = "OWNER_DEPT")
	private String ownerDept;
	/**
	 * 查看人员
	 */
	@Column(name = "CHECK_EMP")
	private String checkEmp;
	/**
	 * 报告人员
	 */
	@Column(name = "REPORT_EMP")
	private String reportEmp;
	/**
	 * 所属人员
	 */
	@Column(name = "OWNER_EMP")
	private String ownerEmp;
	

	/**
	 * 方案生效日期
	 */
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
	 * 战略主题
	 */
	@Column(name = "main_theme")
	private String mainTheme;

	/**
	 * 辅助战略主题
	 */
	@Column(name = "other_theme")
	private String otherTheme;

	/**
	 * 校验信息
	 */
	@Column(name = "VALIDATE_INFO")
	private String validateInfo;

	/**
	 * 目标关联的指标
	 */
	@Column(name = "rela_kpi")
	private String relaKpi;
	
	public String getCheckEmp() {
		return checkEmp;
	}

	public void setCheckEmp(String checkEmp) {
		this.checkEmp = checkEmp;
	}

	public String getReportEmp() {
		return reportEmp;
	}

	public void setReportEmp(String reportEmp) {
		this.reportEmp = reportEmp;
	}

	public String getOwnerEmp() {
		return ownerEmp;
	}

	public void setOwnerEmp(String ownerEmp) {
		this.ownerEmp = ownerEmp;
	}
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWarningFormula() {
		return warningFormula;
	}

	public void setWarningFormula(String warningFormula) {
		this.warningFormula = warningFormula;
	}

	public String getAssessmentFormula() {
		return assessmentFormula;
	}

	public void setAssessmentFormula(String assessmentFormula) {
		this.assessmentFormula = assessmentFormula;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRelativeTo() {
		return relativeTo;
	}

	public void setRelativeTo(String relativeTo) {
		this.relativeTo = relativeTo;
	}

	public String getIsFocus() {
		return isFocus;
	}

	public void setIsFocus(String isFocus) {
		this.isFocus = isFocus;
	}

	public String getCheckDept() {
		return checkDept;
	}

	public void setCheckDept(String checkDept) {
		this.checkDept = checkDept;
	}

	public String getReportDept() {
		return reportDept;
	}

	public void setReportDept(String reportDept) {
		this.reportDept = reportDept;
	}

	public String getOwnerDept() {
		return ownerDept;
	}

	public void setOwnerDept(String ownerDept) {
		this.ownerDept = ownerDept;
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

	public String getMainTheme() {
		return mainTheme;
	}

	public void setMainTheme(String mainTheme) {
		this.mainTheme = mainTheme;
	}

	public String getOtherTheme() {
		return otherTheme;
	}

	public void setOtherTheme(String otherTheme) {
		this.otherTheme = otherTheme;
	}

	public String getValidateInfo() {
		return validateInfo;
	}

	public void setValidateInfo(String validateInfo) {
		this.validateInfo = validateInfo;
	}

	public String getRelaKpi() {
		return relaKpi;
	}

	public void setRelaKpi(String relaKpi) {
		this.relaKpi = relaKpi;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}

}
