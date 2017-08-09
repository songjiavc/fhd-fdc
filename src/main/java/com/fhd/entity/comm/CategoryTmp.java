package com.fhd.entity.comm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

@Entity
@Table(name = "tmp_imp_category")
public class CategoryTmp extends IdEntity implements Serializable {

	private static final long serialVersionUID = -8279404858381319479L;

	/**
	 * 名称
	 */
	@Column(name = "CATEGORY_NAME")
	private String name;

	/**
	 * 代码
	 */
	@Column(name = "CATEGORY_CODE")
	private String code;

	/**
	 * 上级指标
	 */
	@Column(name = "PARENT_ID")
	private String parent;

	/**
	 * 预警公式
	 */
	@Column(name = "FORECAST_FORMULA")
	private String forecastFormula;

	/**
	 * 状态：
	 */
	@Column(name = "IS_ENABLED")
	private String status;

	/**
	 * 是否叶子节点
	 */
	@Column(name = "IS_LEAF")
	private String isLeaf;

	/**
	 * 说明
	 */
	@Column(name = "EDESC", length = 2000)
	private String desc;

	/**
	 * 层次
	 */
	@Column(name = "ELEVEL")
	private Integer level;

	/**
	 * 排序
	 */
	@Column(name = "ESORT")
	private Integer sort;

	/**
	 * id全路径
	 */
	@Column(name = "ID_SEQ", length = 2000)
	private String idSeq;

	/**
	 * 删除状态
	 */
	@Column(name = "DELETE_STATUS", length = 100)
	private Boolean deleteStatus;

	/**
	 * 是否关注
	 */
	@Column(name = "IS_FOCUS")
	private String isFocus;
	/**
	 * 所属部门
	 */
	@Column(name = "OWNER_DEPT")
	private String ownDept;

	/**
	 * 所属人员
	 */
	@Column(name = "OWNER_EMP")
	private String ownEmp;

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
	 * 关联的指标
	 */
	@Column(name = "rela_kpi")
	private String relaKpi;

	/**
	 * 所属公司
	 */
	@Column(name = "company_id")
	private String company;

	/**
	 * 图表类型
	 */
	@Column(name = "CHART_TYPE")
	private String chartType;

	/**
	 * 评估值公式
	 */
	@Column(name = "ASSESSMENT_FORMULA", length = 100)
	private String assessmentFormula;

	/**
	 * 是否生成度量指标：
	 */
	@Column(name = "IS_GENERATE_KPI")
	private String createKpi;

	/**
	 * 是否计算
	 */
	@Column(name = "IS_CALC")
	private String calc;

	/**
	 * 数据类型
	 */
	@Column(name = "DATA_TYPE")
	private String dateType;
	/**
	 * 上级编号
	 */
	@Column(name = "parent_code")
	private String parentCode;
	
	/**
	 * 行号
	 */
	@Column(name = "ROW_NO")
	private Integer rowNo;
	
	/**
	 * 验证信息
	 */
	@Column(name = "VALIDATE_INFO")
	private String validateInfo;
	
	
	
	

	public String getValidateInfo() {
		return validateInfo;
	}

	public void setValidateInfo(String validateInfo) {
		this.validateInfo = validateInfo;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getForecastFormula() {
		return forecastFormula;
	}

	public void setForecastFormula(String forecastFormula) {
		this.forecastFormula = forecastFormula;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(String isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public String getIdSeq() {
		return idSeq;
	}

	public void setIdSeq(String idSeq) {
		this.idSeq = idSeq;
	}

	public Boolean getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public String getIsFocus() {
		return isFocus;
	}

	public void setIsFocus(String isFocus) {
		this.isFocus = isFocus;
	}

	public String getOwnDept() {
		return ownDept;
	}

	public void setOwnDept(String ownDept) {
		this.ownDept = ownDept;
	}

	public String getOwnEmp() {
		return ownEmp;
	}

	public void setOwnEmp(String ownEmp) {
		this.ownEmp = ownEmp;
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

	public String getRelaKpi() {
		return relaKpi;
	}

	public void setRelaKpi(String relaKpi) {
		this.relaKpi = relaKpi;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getAssessmentFormula() {
		return assessmentFormula;
	}

	public void setAssessmentFormula(String assessmentFormula) {
		this.assessmentFormula = assessmentFormula;
	}

	public String getCreateKpi() {
		return createKpi;
	}

	public void setCreateKpi(String createKpi) {
		this.createKpi = createKpi;
	}

	public String getCalc() {
		return calc;
	}

	public void setCalc(String calc) {
		this.calc = calc;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

}
