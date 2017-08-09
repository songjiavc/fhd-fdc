package com.fhd.sm.business;

/**
 * 
 * 监控预警错误代码
 *
 */
public class KpiErrorCode {
	
	/**
	 * 所属部门为空
	 */
	public static String  OWN_DEPT_NULL = "owndept_null";
	
	/**
	 * 所属部门/人员填写错误
	 */
	public static String  OWN_DEPT_NOT_FOUND = "owndept_notFound";
	
	/**
	 * 采集部门/人员填写错误
	 */
	public static String  GATHER_DEPT_NOT_FOUND = "gatherdept_notFound";
	
	/**
	 * 目标部门/人员填写错误
	 */
	public static String  TARGET_DEPT_NOT_FOUND  = "targetdept_notFound";
	
	/**
	 * 报告部门/人员填写错误
	 */
	public static String  REPORT_DEPT_NOT_FOUND  = "reportdept_notFound";
	
	/**
	 * 查看部门/人员填写错误
	 */
	public static String  VIEW_DEPT_NOT_FOUND  = "viewdept_notFound";
	
	/**
	 * 编号重复
	 */
	public static String  CODE_REPEAT = "code_repeat";
	
	/**
	 * 指标名称重复
	 */
	public static String  KPI_NAME_REPEAT = "kpi_repeat";
	
	/**
	 * 开始日期为空
	 */
	public static String  START_DATE_NULL = "startdate_null";
	
	/**
	 * 告警方案不存在
	 */
	public static String  ALARMPLAN_NOT_FOUND = "alarmPlan_notFound";
	
	/**
	 * 预警方案不存在
	 */
	public static String  WARNPLAN_NOT_FOUND = "warnPlan_notFound";
	
	/**
	 * 成功标志
	 */
	public final static String SUCCESS = "50000";
	/**
	 * 日期格式错误
	 */
	public final static String DATE_FORMAT_ERROR = "50001";
	/**
	 * 数据库错误
	 */
	public final static String DB_ERROR = "50002";
	

}
