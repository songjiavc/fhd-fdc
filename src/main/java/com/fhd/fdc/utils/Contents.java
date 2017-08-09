/**
 * Test.java
 * com.fhd.fdc.utils
 *
 * Function : ENDO
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2011-5-9 		胡迪新
 *
 * Copyright (c) 2011, Firsthuida All Rights Reserved.
*/

package com.fhd.fdc.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量类
 *
 * @author   胡迪新
 * @version  
 * @since    Ver 1.1
 * @Date	 2011-5-9		下午04:00:23
 *
 * @see 	 
 */
public final class Contents {
	/**
	 * 系统ID
	 */
    public static String SYSTEM = "SYSTEM";
    /**
     * 上传文件类型
     */
    public static Map<String, Boolean> FILE_TYPE_MAP=new HashMap<String, Boolean>();
    /**
     * 主要
     */
    public static String MAIN = "M";

    /**
     * 辅助
     */
    public static String ASSISTANT = "A";

    /*
     * 所属部门
     */
    public static String BELONGDEPARTMENT = "B";

    /*
     * 报告部门
     */
    public static String REPORTDEPARTMENT = "R";

    /*
     * 查看部门
     */
    public static String VIEWDEPARTMENT = "V";

    /*
     * 采集部门
     */
    public static String GATHERDEPARTMENT = "G";

    /*
     * 目标部门
     */
    public static String TARGETDEPARTMENT = "T";

    /**
     * 参与部门,配合部门
     */
    public static String ORG_PARTICIPATION = "OP";

    /**
     * 责任部门
     */
    public static String ORG_RESPONSIBILITY = "OR";

    /**
     * 处理状态：未开始
     */
    public static String DEAL_STATUS_NOTSTART = "N";

    /**
     * 处理状态: 已保存(待提交)
     */
    public static String DEAL_STATUS_SAVED = "S";
    
    /**
     * 处理状态: 已提交（待处理）
     */
    public static String DEAL_STATUS_SUBMITTED = "P";
    
    /**
     * 处理状态: 已复核
     */
    public static String DEAL_STATUS_REVIEW = "R";

    /**
     * 处理状态:	 处理中
     */
    public static String DEAL_STATUS_HANDLING = "H";
    
    /**
     * 处理状态: 已处理
     */
    public static String DEAL_STATUS_SOLVED = "D";
    
    /**
     * 处理状态:     待更新
     */
    public static String DEAL_STATUS_UPDATE = "U";

    /**
     * 处理状态:	 已纳入内控手册运转
     */
    public static String DEAL_STATUS_OPERATION = "O";
    
    /**
     * 处理状态: 已完成
     */
    public static String DEAL_STATUS_FINISHED = "F";

    /**
     * 处理状态: 逾期
     */
    public static String DEAL_STATUS_AFTER_DEADLINE = "A";

    /**
     * 处理状态: 已评价
     */
    public static String DEAL_STATUS_EVALUATION = "E";

    
    /**
     * 状态: 已保存(待提交)
     */
    @Deprecated
    public static String STATUS_SAVED = "S";

    /**
     * 状态: 已提交（待处理）
     */
    @Deprecated
    public static String STATUS_SUBMITTED = "P";
    /**
     * 状态: 已处理
     */
    @Deprecated
    public static String STATUS_SOLVED = "D";

    /**
     * 状态:正常
     */
    public static String STATUS_NORMAL = "1";

    /**
     * 状态:注销
     */
    public static String STATUS_DELETE = "0";
    

    /**
     * 删除状态: 已启用
     */
    public static String DELETE_STATUS_USEFUL = "1";

    /**
     * 删除状态: 已删除
     */
    public static String DELETE_STATUS_DELETED = "0";

    /**
     * 是否叶子节点: 是
     */
    public static String IS_LEAF_Y = "1";

    /**
     * 是否叶子节点: 否
     */
    public static String IS_LEAF_N = "0";

    /**
     * 是否系统数据: 是
     */
    public static String IS_SYSTEM_Y = "1";

    /**
     * 是否系统数据: 否
     */
    public static String IS_SYSTEM_N = "0";

    /**
     * 经办人,组员,评价人,参与人,执行人
     */
    public static String EMP_HANDLER = "EH";

    /**
     * 复核人
     */
    public static String EMP_REVIEW_PERSON = "ERP";

    /**
     * 审批人
     */
    public static String EMP_EXAMINER_AND_APPROVER = "EEAA";

    /**
     * 责任人,组长
     */
    public static String EMP_RESPONSIBILITY = "ER";

    /**
     * 批量执行的依据,按流程批量
     */
    public static String BATCHTYPE_PROCESS = "process";

    /**
     * 批量执行的依据,按部门批量
     */
    public static String BATCHTYPE_ORG = "org";

    /**
     * 风险等级：高
     */
    public static String RISK_LEVEL_HIGH = "icon-ibm-symbol-4-sm";

    /**
     * 风险等级：中
     */
    public static String RISK_LEVEL_MIDDLE = "icon-ibm-symbol-5-sm";

    /**
     * 风险等级：低
     */
    public static String RISK_LEVEL_LOW = "icon-ibm-symbol-6-sm";

    /**
     * 评价方式：穿行测试
     */
    public static String ASSESS_MEASURE_PRACTICE_TEST = "ca_assessment_measure_0";
    /**
     * 评价方式：抽样测试
     */
    public static String ASSESS_MEASURE_SAMPLE_TEST = "ca_assessment_measure_1";
    /**
     * 计划类型：合规诊断  ic_reconstruct_plan_type
     */
    public static String RECONSTRUCT_PLAN_TYPE_DIAGNOSES = "diagnoses";
    /**
     * 计划类型：流程梳理
     */
    public static String RECONSTRUCT_PLAN_TYPE_PROCESS = "process";
    /**
     * 评价方式：他评
     */
    public static String ASSESS_MEASURE_ETYPE_OTHER = "ca_assessment_etype_other";

    /**
     * 评价方式：自评
     */
    public static String ASSESS_MEASURE_ETYPE_SELF = "ca_assessment_etype_self";

    /**
     * 评价方式：穿行测试和抽样测试
     */
    public static String ASSESS_MEASURE_ALL_TEST = "ca_assessment_measure_2";

    /**
     * 流程选择：按部门选择
     */
    public static String ASSESS_MEASURE_PROCESSSELECTBYDEPT = "ca_scale_set_measure_dept";

    /**
     * 流程选择：按业务流程选择
     */
    public static String ASSESS_MEASURE_PROCESSSELECTBYBUSSESS = "ca_scale_set_measure_business";

    /**
     *  符合，合格，有效
     */
    public static String IS_OK_Y = "Y";

    /**
     * 部分符合，部分合格，部分有效
     */
    public static String IS_OK_YORN = "YORN";

    /**
     * 不符合，不合格，不有效
     */
    public static String IS_OK_N = "N";

    /**
     * 不适用
     */
    public static String IS_OK_NAN = "NAN";

    /**
     * 评价结果状态--完全符合
     */
    public static String AssessResult_STATUS_OK = "完全符合";

    /**
     * 评价结果状态--部分符合
     */
    public static String AssessResult_STATUS_YORN = "部分符合";

    /**
     * 评价结果状态--不符合
     */
    public static String AssessResult_STATUS_N = "不符合";

    /**
     * 评价结果状态--不适用
     */
    public static String AssessResult_STATUS_NAN = "不适用";

    /**
     * 样本测试状态--自动
     */
    public static String SAMPLE_STATUS_AUTO = "自动";

    /**
     * 样本测试状态--补充
     */
    public static String SAMPLE_STATUS_SUPPLEMENT = "补充";

    /**
     * 缺陷类型--双重缺陷
     */
    public static String DEFECT_TYPE_ALL = "ca_defect_type_all";

    /**
     * 缺陷类型--设计缺陷
     */
    public static String DEFECT_TYPE_DESIGN = "ca_defect_type_design";

    /**
     * 缺陷类型--执行缺陷
     */
    public static String DEFECT_TYPE_EXECUTE = "ca_defect_type_execute";

    /**
     * 缺陷级别--重大缺陷
     */
    public static String DEFECT_LEVEL_GREAT = "ca_defect_level_0";

    /**
     * 缺陷级别--重要缺陷
     */
    public static String DEFECT_LEVEL_IMPORTANT = "ca_defect_level_1";

    /**
     * 缺陷级别--一般缺陷
     */
    public static String DEFECT_LEVEL_GENERAL = "ca_defect_level_2";

    /**
     * 缺陷级别--例外事项
     */
    public static String DEFECT_LEVEL_EXCEPTION = "ca_defect_level_3";

    /**
     * 评价报告--测试报告
     */
    public static String ASSESSMENT_REPORT_TYPE_TEST = "test_report";
    /**
     *  建设计划报告--测试报告
     */
    public static String CONSTRUCT_REPORT_TYPE_TEST = "test_construct_report";
    /**
     * 体系报告--公司年度报告
     */
    public static String CONSTRUCT_REPORT_TYPE_COMPANY = "company_year_construct_report";
    
    /**
     * 评价报告--集团年度报告
     */
    public static String CONSTRUCT_REPORT_TYPE_GROUP = "group_year_construct_report";
    /**
     * 评价报告--公司年度报告
     */
    public static String ASSESSMENT_REPORT_TYPE_COMPANY = "company_year_assessment_report";
    
    /**
     * 评价报告--集团年度报告
     */
    public static String ASSESSMENT_REPORT_TYPE_GROUP = "group_year_assessment_report";

    /**
     * 风险管理报告
     */
    public static String REPORT_RISK_MANAGE = "risk_manage_report_template";
    
    /**
     * 风险评估报告
     */
    public static String REPORT_RISK_ASSESS = "risk_assess_report_template";
    
    /**
     * 战略目标评估值公式
     */
    public static String SM_ASSESSMENT_VALUE_FORMULA = "sm_assessmentValueFormula";

    /**
     * 风险评估公式
     * */
    public static String ASSESS_VALUE_FORMULA = "assessValueFormula";
    
    /**
     * 记分卡评估值公式
     */
    public static String SC_ASSESSMENT_VALUE_FORMULA = "sc_assessmentValueFormula";

    /**
     * 目标值公式
     */
    public static String TARGET_VALUE_FORMULA = "targetValueFormula";

    /**
     * 实际值公式
     */
    public static String RESULT_VALUE_FORMULA = "resultValueFormula";

    /**
     * 评估值公式
     */
    public static String ASSESSMENT_VALUE_FORMULA = "assessmentValueFormula";

    /**
     * 预期值公式
     */
    public static String EXPECT_VALUE_FORMULA = "expectValueFormula";

    /**
     * 影响程度公式
     */
    public static String IMPACTS_FORMULA = "impactsFormula";

    /**
     * 发生可能性公式
     */
    public static String PROBABILITY_FORMULA = "probabilityFormula";

    /**
     * 频率--每天
     */
    public static String FREQUECY_DAY = "0frequecy_day";
    /**
     * 实时频率
     */
    public static String FREQUECY_RELATIME = "0frequecy_relatime";

    /**
     * 频率--每天(工作日)
     */
    public static String FREQUECY_WEEKDAY = "0frequecy_weekday";

    /**
     * 频率--周
     */
    public static String FREQUECY_WEEK = "0frequecy_week";

    /**
     * 频率--半月
     */
    public static String FREQUECY_HALFMONTH = "0frequecy_halfmonth";

    /**
     * 频率--月
     */
    public static String FREQUECY_MONTH = "0frequecy_month";

    /**
     * 频率--季
     */
    public static String FREQUECY_QUARTER = "0frequecy_quarter";

    /**
     * 频率--半年
     */
    public static String FREQUECY_HALFYEAR = "0frequecy_halfyear";

    /**
     * 频率--年
     */
    public static String FREQUECY_YEAR = "0frequecy_year";

    /**
     * 公式类型--手动输入
     */
    public static String FORMULAR_MANUAL = "0sys_use_formular_manual";

    /**
     * 公式类型--公式计算
     */
    public static String FORMULAR_FORMULA = "0sys_use_formular_formula";

    /**
     * 累积值类型--手动输入
     */
    public static String SUM_MEASURE_MANUAL = "kpi_sum_measure_manual";

    /**
     * 累积值类型--累计取第一项
     */
    public static String SUM_MEASURE_FIRST = "kpi_sum_measure_first";

    /**
     * 累积值类型--累计取最后一项
     */
    public static String SUM_MEASURE_LAST = "kpi_sum_measure_last";

    /**
     * 累积值类型--累计取最大值
     */
    public static String SUM_MEASURE_MAX = "kpi_sum_measure_max";

    /**
     * 累积值类型--累计去最小值
     */
    public static String SUM_MEASURE_MIN = "kpi_sum_measure_min";

    /**
     * 累积值类型--累计求平均值
     */
    public static String SUM_MEASURE_AVG = "kpi_sum_measure_avg";

    /**
     * 累积值类型--累计求和
     */
    public static String SUM_MEASURE_SUM = "kpi_sum_measure_sum";

    /**
     * 累积目标值
     */
    public static String TARGET_VALUE_SUM = "targetValueSum";

    /**
     * 累积实际值
     */
    public static String RESULT_VALUE_SUM = "resultValueSum";

    /**
     * 累积评估值
     */
    public static String ASSESSMENT_VALUE_SUM = "assessmentValueSum";

    /**
     * 预警或告警方案类型--预警方案
     */
    public static String ALARMPLAN_FORECAST = "forecast";

    /**
     * 预警或告警方案类型--告警方案
     */
    public static String ALARMPLAN_REPORT = "report";

    /**
     * 对象类型--指标
     */
    public static String OBJECT_KPI = "kpi";

    /**
     * 对象类型--战略目标
     */
    public static String OBJECT_STRATEGY = "strategy";

    /**
     * 对象类型--记分卡
     */
    public static String OBJECT_CATEGORY = "category";

    /**
     * 对象类型--风险
     */
    public static String OBJECT_RISK = "risk";

    /**
     * 操作类型--成功
     */
    public static String OPERATE_SUCCESS = "success";

    /**
     * 操作类型--失败
     */
    public static String OPERATE_FAILURE = "failure";

    /**
     * 执行中
     */
    public static String EXECUTING = "ca_work_plan_executing";

    /**
     * 已完成
     */
    public static String FINISH = "ca_work_plan_finish";

    /**
     * 已提交 ca_work_plan_submit
     */
    public static String SUBMIT = "ca_work_plan_submit";

    /**
     * 已保存
     */
    public static String SAVE = "ca_work_plan_save";

    /**
     * 评价点类型：设计有效性评价点
     */
    public static String ASSESS_POINT_TYPE_DESIGN = "D";

    /**
     * 评价点类型：执行有效性评价点
     */
    public static String ASSESS_POINT_TYPE_EXECUTE = "E";

    /**
     * ID undifined
     */
    public static String ID_UNDEFINED = "undefined";

    /**
     * 标示指标
     */
    public static String KPI_TYPE = "KPI";

    /**
     * 标示指标类型
     */
    public static String KC_TYPE = "KC";

    /**
     * 数据字典 0yn_y
     */
    public static String DICT_Y = "0yn_y";

    /**
     * 数据字典 0yn_n
     */
    public static String DICT_N = "0yn_n";

    /**
     * 所有部门
     */
    public static String ORG_ALL = "ALL";

    /**
     * 指标树根
     */
    public static String KPI_ROOT = "kpi_root";

    /**
     * 目标值
     */
    public static String TARGET_VALUE = "目标值";

    /**
     * 实际值
     */
    public static String RESULT_VALUE = "实际值";

    /**
     * 评估值
     */
    public static String ASSEMENT_VALUE = "评估值";

    /**
     * 标杆值
     */
    public static String MODEL_VALUE = "标杆值";
    
    /**
     * 同比值
     */
    public static String SAME_VALUE = "同比值";
    
    /**
     * 环比值
     */
    public static String RATIO_VALUE = "环比值";

    /**
     * 无
     */
    public static String NONE = "无";

    /**
     * 内控标准：标准分类
     */
    public static String STANDARD_TYPE_CLASS = "1";

    /**
     * 内控标准：标准下的要求
     */
    public static String STANDARD_TYPE_REQUIREMENT = "0";
    /**
     * 角色编码：内控部门分管领导
     */
    public static String IC_DEPARTMENT_LEADER = "ICDepartmentLeader";
    /**
     * 角色编码：内控部门部长
     */
    public static String IC_DEPARTMENT_MINISTER = "ICDepartmentMinister";
    /**
     * 角色编码：内控部门员工
     */
    public static String IC_DEPARTMENT_STAFF = "ICDepartmentStaff";
    /**
     * 流程发生频率：无规律
     */
    public static String IC_CONTROL_FREQUENCY_0="ic_control_frequency_0";
    /**
     * 流程发生频率：随时
     */
    public static String IC_CONTROL_FREQUENCY_1="ic_control_frequency_1";
    /**
     * 流程发生频率：每周
     */
    public static String IC_CONTROL_FREQUENCY_WEEK="ic_control_frequency_week";
    /**
     * 流程发生频率：月度
     */
    public static String IC_CONTROL_FREQUENCY_MONTH="ic_control_frequency_month";
    /**
     * 流程发生频率：季度
     */
    public static String IC_CONTROL_FREQUENCY_QUARTER="ic_control_frequency_quarter";
    /**
     * 流程发生频率：半年
     */
    public static String IC_CONTROL_FREQUENCY_HALFYEAR="ic_control_frequency_halfyear";
    
    /**
     * 流程发生频率：年度
     */
    public static String IC_CONTROL_FREQUENCY_YEAR="ic_control_frequency_year";
    
    /**
     * 负无穷
     */
    public static String NEGATIVE_INFINITY="-∞";
    
    /**
     * 正无穷
     */
    public static String POSITIVE_INFINITY="+∞";
    
    /**
     * 案件类型，预案-1
     */
    public static String PLAN_TYPE_PRE="0gender_m";
    
    /**
     * 案件类型，应对-0
     */
    public static String PLAN_TYPE_RESPONSE="0gender_f";
    
    /**
     * 锁定类型，锁定-true
     */
    public static Boolean LOCK_STATE_LOCK=true;
    
    /**
     * 锁定类型，正常-false
     */
    public static Boolean LOCK_STATE_NORMAL=false;
    
    /**
     * 启用类型，启用-true
     */
    public static Boolean ENABLE_T=true;
    
    /**
     * 启用类型，放弃-false
     */
    public static Boolean ENABLE_F=false;
    /**
     * 应对措施类型(0:自动应对任务)
     */
    public static String SOLUTION_AUTO = "0";
    /**
     * 应对措施类型(1:应对计划)
     */
    public static String SOLUTION_PLAN = "1";
    /**
     * 应对措施
     */
    public static String RESPONSE_MEASURE = "M";
    /**
     * 控制措施
     */
    public static String RESPONSE_SOLUTION = "S";
    
    /**
     * 创建
     */
    public static final String GRAPH_TYPE_CREATE = "graphdraw";
    
    /**
     * 调用
     */
    public static final String GRAPH_TYPE_TRANSFER = "graphdrawview";
    /**
     * 指标采集数据完成值类型
     */
    public static final String KPI_GATHER_VALUE_FINISH_TYPE = "finish";
    /**
     * 指标采集数据目标值类型
     */
    public static final String KPI_GATHER_VALUE_TARGET_TYPE = "target";
    /**
     * 指标采集数据评估值类型
     */
    public static final String KPI_GATHER_VALUE_ASSESSMENT_TYPE = "assessment";
    /**
     * 指标风险等级-高
     */
    public static final String KPI_STATUS_HIGHT_LEVEL = "0alarm_startus_h";
    /**
     * 指标风险等级-低
     */
    public static final String KPI_STATUS_LOW_LEVEL = "0alarm_startus_l";
    /**
     * 指标风险等级-中
     */
    public static final String KPI_STATUS_MID_LEVEL = "0alarm_startus_m";
    
    /**
     * 风险责任部门
     */
    public static final String DUTY_DEPARTMENT = "M";
    
    /**
     * 风险相关部门
     */
    public static final String RELATIVE_DEPARTMENT = "A";
    
    /**
     * 风险责任人
     */
    public static final String DUTY_EMPLOYEE = "M";
    
    /**
     * 风险相关人
     */
    public static final String RELATIVE_EMPLOYEE = "A";
    
    /**
     * 风险影响指标
     */
    public static final String INFLUENCE_KPI = "I";
    
    /**
     * 风险风险指标
     */
    public static final String RISK_KPI = "RM";
    
    /**
     * 风险影响流程
     */
    public static final String INFLUENCE_PROCESS = "I";
    
    /**
     * 风险控制流程
     */
    public static final String CONTROL_PROCESS = "C";
    
    /**
     * 风险状态: 待审核
     */
    public static String RISK_STATUS_SAVED = "saved";
    
    /**
     * 风险状态: 已提交
     */
    public static String RISK_STATUS_SUBMITED = "submited";
    
    /**
     * 风险状态: 待审批
     */
    public static String RISK_STATUS_WAITINGAPPROVE = "waitingApprove";
    
    /**
     * 风险状态: 审批中
     */
    public static String RISK_STATUS_EXAMINE = "examine";
    
    /**
     * 风险状态: 待归档
     */
    public static String RISK_STATUS_WAITINGARCHIVE = "waitingArchive";
    
    /**
     * 风险状态: 已归档
     */
    public static String RISK_STATUS_ARCHIVED = "archived";
    
    /**
     * 风险状态: 已删除(审批时的删除状态)
     */
    public static String RISK_STATUS_DELETED = "deleted";
    
    /**
     * 风险状态: 退回(审批时的退回状态)
     */
    public static String RISK_STATUS_RETURNBACK = "returnBack";
    /**
     * 默认报表小数点位数
     */
    public static Integer DEFAULT_KPI_DOT_POSITION = 2;
    /**
     * 执行退回
     */
    public static String JBPM_ACTINST_UNTREAD = "untread";
    /**
     * 执行删除
     */
    public static String JBPM_ACTINST_REMOVE = "remove";
    
}
