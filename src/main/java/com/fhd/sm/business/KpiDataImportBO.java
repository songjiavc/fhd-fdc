package com.fhd.sm.business;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.kpi.KpiGatherResultDAO;
import com.fhd.dao.kpi.KpiRelaAlarmDAO;
import com.fhd.dao.kpi.KpiTmpDAO;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiRelaAlarm;
import com.fhd.entity.kpi.KpiTmp;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpOrgBO;

@SuppressWarnings("deprecation")
@Service
public class KpiDataImportBO {
	@Autowired
	private KpiTmpDAO  o_kpiTmpDAO;
	@Autowired
	private KpiTypeDataImportBO o_kpiTypeDataImportBO;
	@Autowired
	private KpiBO o_kpiBO;
	@Autowired
	private KpiDAO o_kpiDao;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	@Autowired
	private DataImportCommBO o_dataImportCommBO;
    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
    @Autowired
    private KpiGatherResultDAO o_kpiGatherResultDAO;
    @Autowired
    private EmpOrgBO o_empOrgBO;
    @Autowired
    private KpiRelaAlarmDAO o_kpiRelaAlarmDAO;
     
    private static final String STATUS_MEASURE = "状态分数";
    
    private static final String RESULT_MEASURE = "状态结果";
    
	/**设置parent和idseq
	 * @param strategyMapTmpList
	 */
	private void findParent(List<KpiTmp> kpiTmpList ){
		for (KpiTmp kpiTmp : kpiTmpList) {
			String parent = kpiTmp.getParent();
			if(StringUtils.isBlank(parent)){
				kpiTmp.setIdSeq("."+kpiTmp.getId()+".");
			}else{
				for (int j=0;j<kpiTmpList.size();j++) {
					KpiTmp kpiTmp2 = kpiTmpList.get(j);
					if(parent.equals(kpiTmp2.getName())){
						kpiTmp.setParent(kpiTmp2.getId());
						kpiTmp.setIdSeq(kpiTmp2.getIdSeq()+kpiTmp2.getId()+".");
					}
				}
			}
		}
	}
	
	/**设置是否是叶子节点
	 * @param strategyMapTmpList
	 */
	private void findIsLeaf(List<KpiTmp> kpiTmpList){
		for (KpiTmp kpiTmp : kpiTmpList) {
			boolean isLeaf = true;
			String id = kpiTmp.getId();
			for (int j=0;j<kpiTmpList.size();j++) {
				KpiTmp kpiTmp2 =  kpiTmpList.get(j);
				if(id.equals(kpiTmp2.getParent())){
					isLeaf = false;
					break;
				}
			}
			kpiTmp.setIsLeaf(isLeaf);
		}
	}
	
	/**
	 * 导入Excel中的数据到临时表
	 * @param excelDatas Excel数据
	 * @param kpiType 指标类型
	 * @throws ParseException
	 */
	@Transactional
	public Boolean saveKpiDataFromExcel(List<List<String>> excelDatas,String kpiType,Boolean addStyle) throws ParseException {
		Boolean flag = true;
		//清空临时表数据 
		String sql = "delete from tmp_imp_kpi_kpi where IS_KPI_CATEGORY = 'KPI'";
		SQLQuery sqlQuery = o_kpiTmpDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		String companyId = UserContext.getUser().getCompanyid();
		//部门map
		Map<String,String> orgIdMap = o_dataImportCommBO.findOrgIdMap(companyId);
		//人员map
		Map<String,String> empIdMap = o_dataImportCommBO.findEmpIdMap(companyId);
		//告警方案map
		Map<String, String> alarmFMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_forecast");
		//预警方案map
		Map<String, String> alarmAMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_alarm");
		//主纬度,辅助纬度List
		List<DictEntry> mainOtherDimList = o_dictBO.findDictEntryByDictTypeId("kpi_dimension");
		//趋势list
		List<DictEntry> relaList = o_dictBO.findDictEntryByDictTypeId("kpi_relative_to");
		//是否list
		List<DictEntry> ynList = o_dictBO.findDictEntryByDictTypeId("0yn");
		// 单位List
		List<DictEntry> unitList = o_dictBO.findDictEntryByDictTypeId("0units");
		// 指标方向性List
		List<DictEntry> kpiDirList = o_dictBO.findDictEntryByDictTypeId("kpi_etype");
		// 指标性质List
		List<DictEntry> kpieTypeList = o_dictBO.findDictEntryByDictTypeId("kpi_kpi_type");
		// 亮灯依据List
		List<DictEntry> lightList = o_dictBO.findDictEntryByDictTypeId("kpi_alarm_measure");
		// 预警依据List 
		List<DictEntry> alarmBasisList = o_dictBO.findDictEntryByDictTypeId("kpi_alarm_basis");
		// 计算方式
		List<DictEntry> sumList = o_dictBO.findDictEntryByDictTypeId("kpi_sum_measure");
		// 数据类型
		// 指标类型map
		Map<String, String> kpiTypeMap = o_kpiBO.findAllKpiCodeList(Contents.KC_TYPE);
		// 指标map
		Map<String, String> kpiMap = o_kpiBO.findAllKpiCodeList(Contents.KPI_TYPE);
		List<Kpi> oldKpiList = o_kpiBO.findAllKpiListByCompanyId(UserContext.getUser().getCompanyid());
		// 已填写指标LIST
		List<String> existKpiCodeList = new ArrayList<String>();
		List<String> existKpiNameList = new ArrayList<String>();
        // 指标类型ID map
		Map<String, String> kpiTypeIdMap = o_kpiBO.findAllKpiTypeIdMap(Contents.KC_TYPE);
		// 设置各字段名称在EXCEL中的列位置
		// 指标类型编号
		int kpiTypeIndex = 0;
		// 指标类型名称
		int kpiTypeNameIndex = 1;
		// 指标编号
		int kpiCodeIndex = 2;
		// 指标名称
		int kpiNameIndex = 3;
		// 继承信息
		int isHeritIndex = 4;
		// 上级指标
		int parentIndex = 5;
		// 说明
		int descIndex = 6;
		// 短名称
		int shortNameIndex = 7;
		// 所属部门
		int belongDeptIndex = 8;
		// 所属人员
		int belongEmpIndex = 9;
		// 采集部门
		int gatherDeptIndex = 10;
		// 采集人员
		int gatherEmpIndex  = 11;
		// 目标部门
		int targetDeptIndex = 12;
		// 目标人员
		int targetEmpIndex = 13;
		// 报告部门 
		int reportDeptIndex = 14;
		// 报告人员
		int reportEmpIndex = 15;
		// 查看部门
		int viewDeptIndex = 16;
		// 查看人员
		int viewEmpIndex = 17;
		// 是否启用
		int enableIndex = 18;
		// 是否监控
		int monitorIndex = 19;
		// 单位
		int unitIndex = 20;
		// 开始日期
		int startDataIndex = 21;
		// 指标方向性
		int kpiDirIndex = 22;
		// 指标性质
		int eTypeIndex = 23;
		// 亮灯依据
		int lightIndex = 24;
		// 预警依据
		int alarmIndex = 25;
		// 主维度
		int mainDimIndex = 26;
		// 辅助维度
		int supportDimIndex = 27;
		// 目标值别名
		int targetAliasIndex = 28;
		// 实际值别名
		int resultAliasIndex = 29;
		// 结果值公式
		int resultFormulaIndex = 30;
		// 目标值公式
		int targetFormulaIndex = 31;
		// 评估值公式
		int assessFormulaIndex  = 32;
		// 预警公式
		int alarmFormulaIndex = 33;
	    // 关联关系公式
		//int relaFormulaIndex = 34;
		// 标杆值
		int modelValueIndex = 35;
		// 结果值累计计算
		int resultSumIndex = 36;
		// 目标值累计计算
		int targetSumIndex = 37;
		// 评估值累计计算
		int assessSumIndex = 38;
		// 结果收集日期显示名称
		int resultDateNameIndex = 39;
		// 结果收集日期设置
		int resultDateIndex = 40;
		// 结果收集频率
		int resultFreIndex = 41;
		// 结果收集延期天
		int resultDelayIndex = 42;
		// 结果收集报告显示名称
		int resultRepNameIndex = 43;
		//  结果收集报告日期设置
		int resultRepDateIndex = 44;
		// 结果收集报告频率
		int resultRepFreIndex = 45;
		// 目标收集日期显示名称
		int targetDateNameIndex = 46;
		// 目标收集日期设置
		int targetDateIndex = 47;
		// 目标收集日期频率
		int targetFreIndex = 48;
		// 目标收集延期天
		int targetDelayIndex = 49;
		// 目标收集报告日期显示名称
		int targetRepNameIndex = 50;
		// 目标收集报告日期设置
		int targetRepDateIndex = 51;
		// 目标收集报告频率
		int targetRepFreIndex = 52;
		// 报表小数点位置
		int positionIndex = 53;
		// 趋势相对于
		int relativeIndex = 54;
		// 方案生效日期
		int effIndex = 55;
		// 告警方案
		int alarmPlanIndex = 56;
		// 预警方案
		int warnPlanIndex = 57;
		// 最大值
		int maxIndex = 58;
		// 最小值
		int minIndex = 59;
		// 是否使用默认名称
		int defaultIndex = 60;
		// 序号
		int esortIndex = 61;
		// 层级 
		int levelIndex = 62;
		
		List<KpiTmp> kpiTmpList = new ArrayList<KpiTmp>();
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {
				KpiTmp kpiTmp = new KpiTmp();
				kpiTmp.setIsKpiCategory(Contents.KPI_TYPE);
				kpiTmp.setCalc(Contents.DICT_Y);
				// 验证信息汇总字符串
				StringBuffer validateInfo = new StringBuffer();
				List<String> rowDatas = (List<String>) excelDatas.get(row);
                // 验证指标类型
				if(StringUtils.isNotBlank(rowDatas.get(kpiTypeIndex)) && StringUtils.isNotBlank(rowDatas.get(kpiTypeNameIndex))){
					if(!(rowDatas.get(kpiTypeNameIndex)).equalsIgnoreCase(kpiTypeMap.get(rowDatas.get(kpiTypeIndex)))) {
						validateInfo.append("指标类型填写错误,");
					}
				}
				kpiTmp.setBelongKpiCategory(kpiTypeIdMap.get(rowDatas.get(kpiTypeIndex)));
				// 指标编号 名称
				if(StringUtils.isNotBlank(rowDatas.get(kpiCodeIndex))) {
				   if(!addStyle) {
					   if(kpiMap.containsKey(rowDatas.get(kpiCodeIndex)) ) {
						   validateInfo.append("指标编号重复,");
					   }
				   }
				   if(existKpiCodeList.contains(rowDatas.get(kpiCodeIndex))){
				       validateInfo.append("指标编号重复,");
				   }
				   existKpiCodeList.add(rowDatas.get(kpiCodeIndex));
				} else {
					 validateInfo.append("指标编号填写错误,");
				}
				if(StringUtils.isBlank(rowDatas.get(kpiNameIndex))){
				    validateInfo.append("指标名称不存在,");
				}else{
				    if(!addStyle) {
	                       if(StringUtils.isNotBlank(o_dataImportCommBO.findKpiIdByName(rowDatas.get(kpiNameIndex),oldKpiList)) || existKpiNameList.contains(rowDatas.get(kpiNameIndex))) {
	                           validateInfo.append("指标名称重复,");
	                       }
	                   }
				    existKpiNameList.add(rowDatas.get(kpiNameIndex));
				}
				kpiTmp.setCode(rowDatas.get(kpiCodeIndex));
				kpiTmp.setName(rowDatas.get(kpiNameIndex));
				//继承信息
				if("是".equals(rowDatas.get(isHeritIndex))) {
					kpiTmp.setIsInherit(true);
				} else {
					kpiTmp.setIsInherit(false);
				}
				//  上级指标
				if(StringUtils.isNotBlank(rowDatas.get(parentIndex)) && !existKpiCodeList.contains(rowDatas.get(parentIndex))){
					validateInfo.append("上级指标不存在,");
					kpiTmp.setParent(rowDatas.get(parentIndex));
					
				}
				// 说明
				kpiTmp.setDesc(rowDatas.get(descIndex));
				// 短名称
				kpiTmp.setShortName(rowDatas.get(shortNameIndex));
				// 所属部门名称人员
				String belongDept = rowDatas.get(belongDeptIndex);
				String belongEmp = rowDatas.get(belongEmpIndex);
				Map<String, Object> deptEmpMap = new HashMap<String, Object>();
				if(StringUtils.isBlank(belongDept)) {
					validateInfo.append("所属部门为空,");
				} else {
					deptEmpMap = validateDeptEmpInfo(belongDept,belongEmp,orgIdMap,empIdMap);
					if(deptEmpMap.containsKey("error")) {
						if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
							validateInfo.append("所属部门填写错误,");
						} else {
							validateInfo.append("所属人员填写错误,");
						}
					}
				}
				kpiTmp.setOwenrDept((String) deptEmpMap.get("empInfo"));
				// 结果采集部门
				String collectDept = rowDatas.get(gatherDeptIndex);
				String collectEmp = rowDatas.get(gatherEmpIndex);
				deptEmpMap = validateDeptEmpInfo(collectDept,collectEmp,orgIdMap,empIdMap);
				if(deptEmpMap.containsKey("error")) {
					if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
						validateInfo.append("采集部门填写错误,");
					} else {
						validateInfo.append("采集人员填写错误,");
					}
				}
				kpiTmp.setCollectDept((String) deptEmpMap.get("empInfo"));
				
				// 目标采集部门
				String targetDept = rowDatas.get(targetDeptIndex);
				String targetEmp = rowDatas.get(targetEmpIndex);
				deptEmpMap = validateDeptEmpInfo(targetDept,targetEmp,orgIdMap,empIdMap);
				if(deptEmpMap.containsKey("error")) {
					if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
						validateInfo.append("目标部门填写错误,");
					} else {
						validateInfo.append("目标人员填写错误,");
					}
				}
				kpiTmp.setTargetDept((String) deptEmpMap.get("empInfo"));
				// 报告部门
				String reportDept = rowDatas.get(reportDeptIndex);
				String reportEmp = rowDatas.get(reportEmpIndex);
				deptEmpMap = validateDeptEmpInfo(reportDept,reportEmp,orgIdMap,empIdMap);
				if(deptEmpMap.containsKey("error")) {
					if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
						validateInfo.append("报告部门填写错误,");
					} else {
						validateInfo.append("报告人员填写错误,");
					}
				}
				kpiTmp.setReportDept((String) deptEmpMap.get("empInfo"));
				// 查看部门
				String checkDept = rowDatas.get(viewDeptIndex);
				String checkEmp = rowDatas.get(viewEmpIndex);
				deptEmpMap = validateDeptEmpInfo(checkDept,checkEmp,orgIdMap,empIdMap);
				if(deptEmpMap.containsKey("error")) {
					if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
						validateInfo.append("查看部门填写错误,");
					} else {
						validateInfo.append("查看人员填写错误,");
					}					
				}
				kpiTmp.setCheckDept((String) deptEmpMap.get("empInfo"));
				// 是否启用
				kpiTmp.setStatus(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(enableIndex),ynList));
				// 是否监控
				if("是".equals(rowDatas.get(monitorIndex))) {
					kpiTmp.setIsMonitor(true);
				} else {
					kpiTmp.setIsMonitor(false);
				}
				// 单位
				kpiTmp.setUnits(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(unitIndex),unitList));
				// 开始如期
				boolean startDateFlag = true;
				if(StringUtils.isBlank(rowDatas.get(startDataIndex))) {
					validateInfo.append("开始日期为空,");
					kpiTmp.setStartDate(null);
				} else {
			        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");			       
			        java.util.Date startDate = null;
                    try {
                        startDate = dataFormat.parse(rowDatas.get(startDataIndex));
                    }
                    catch (Exception e) {
                        startDateFlag = false;
                    }
                    kpiTmp.setStartDate(startDate);
                } 
                if(!startDateFlag){
                    validateInfo.append("开始日期设置错误,");
                }
				// 指标方向性
				kpiTmp.setType(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(kpiDirIndex),kpiDirList));
				
				// 指标性质
				kpiTmp.setKpiType(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(eTypeIndex),kpieTypeList));
				// 亮灯依据
				kpiTmp.setAlarmMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(lightIndex),lightList));
				
				// 预警依据
				kpiTmp.setAlarmBasis(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(alarmIndex),alarmBasisList));
				
				// 主维度
				kpiTmp.setMainDim(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(mainDimIndex),mainOtherDimList));
				// 辅助维度
				StringBuffer supportDim = new StringBuffer();
				if(StringUtils.isNotBlank(rowDatas.get(supportDimIndex))) {
					for(String dimName: rowDatas.get(supportDimIndex).split(",")) {
						supportDim.append(o_dataImportCommBO.findDictIdByDictName(dimName,mainOtherDimList)).append(",");
					}
				}
				kpiTmp.setSupportDim(supportDim.toString());
				
				// 目标值别名
				kpiTmp.setTargetValueAlias(rowDatas.get(targetAliasIndex));
				// 实际值别名
				kpiTmp.setResultValueAlias(rowDatas.get(resultAliasIndex));
				// 结果值公式
				if(StringUtils.isNotBlank(rowDatas.get(resultFormulaIndex))) {
			        kpiTmp.setResultFormula(rowDatas.get(resultFormulaIndex));
			        kpiTmp.setIsResultFormula("0sys_use_formular_formula");
				} else {
					kpiTmp.setIsResultFormula("0sys_use_formular_manual");
				}
				// 目标值公式
				if(StringUtils.isNotBlank(rowDatas.get(targetFormulaIndex))) {
					 kpiTmp.setTargetFormula(rowDatas.get(targetFormulaIndex));
					 kpiTmp.setIsTargetFormula("0sys_use_formular_formula");
				} else {
					kpiTmp.setIsTargetFormula("0sys_use_formular_manual");
				}
				// 评估值公式
				if(StringUtils.isNotBlank(rowDatas.get(assessFormulaIndex))) {
					kpiTmp.setAssessmentFormula(rowDatas.get(assessFormulaIndex));
					kpiTmp.setIsAssessmentFormula("0sys_use_formular_formula");
				} else {
					kpiTmp.setIsAssessmentFormula("0sys_use_formular_manual");
				}
				//预警公式
				if(StringUtils.isNotBlank(rowDatas.get(alarmFormulaIndex))) {
					kpiTmp.setForecastFormula(rowDatas.get(alarmFormulaIndex));
				}
				// 标杆值
				if(StringUtils.isNotBlank(rowDatas.get(modelValueIndex))){
				    if(NumberUtils.isNumber(rowDatas.get(modelValueIndex))){
				        kpiTmp.setModelValue(Double.parseDouble(rowDatas.get(modelValueIndex)));
				    }else{
				        validateInfo.append("标杆值设置错误,");
				    }
				}
				// 结果值累计计算
				if(StringUtils.isNotBlank(rowDatas.get(resultSumIndex))) {
					kpiTmp.setResultSumMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(resultSumIndex),sumList));
				}
				// 目标值累计计算
				if(StringUtils.isNotBlank(rowDatas.get(targetSumIndex))) {
					kpiTmp.setTargetSumMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(targetSumIndex),sumList));
				}
				// 评估值累计计算
				if(StringUtils.isNotBlank(rowDatas.get(assessSumIndex))) {
					kpiTmp.setAssessmentSumMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(assessSumIndex),sumList));
				}
				// 结果收集频率显示（中文）
				if(StringUtils.isNotBlank(rowDatas.get(resultDateNameIndex))) {
					kpiTmp.setGatherDayFormulrShow(rowDatas.get(resultDateNameIndex));
				}
				// 结果值收集日期设置
				if(StringUtils.isNotBlank(rowDatas.get(resultDateIndex))) {
					kpiTmp.setGatherDayFormulr(rowDatas.get(resultDateIndex));
				}
				// 结果收集日期频率
				kpiTmp.setGatherFrequence(convertFrequece(rowDatas.get(resultFreIndex)));
				// 结果收集延期天
				if(StringUtils.isNotBlank(rowDatas.get(resultDelayIndex))) {
					kpiTmp.setResultCollectInterval(Integer.parseInt(rowDatas.get(resultDelayIndex)));
				}
				// 结果收集报告日期设置
				if(StringUtils.isNotBlank(rowDatas.get(resultRepNameIndex))) {
					kpiTmp.setGatherReportDayFormulrShow(rowDatas.get(resultRepNameIndex));
				}
				// 结果收集报告频率
				if(StringUtils.isNotBlank(rowDatas.get(resultRepDateIndex))) {
					kpiTmp.setGatherReportDayFormulr(rowDatas.get(resultRepDateIndex));
				}
				// 结果收集日期频率
				kpiTmp.setReportFrequence(convertFrequece(rowDatas.get(resultRepFreIndex)));
				// 目标收集频率
				if(StringUtils.isNotBlank(rowDatas.get(targetDateIndex))) {
					kpiTmp.setTargetSetDayFormular(rowDatas.get(targetDateIndex));
				}
				// 目标收集频率设置
				if(StringUtils.isNotBlank(rowDatas.get(targetDateNameIndex))) {
					kpiTmp.setTargetSetDayFormularShow(rowDatas.get(targetDateNameIndex));					
				}
				// 目标收集日期频率
				kpiTmp.setTargetSetFrequence(convertFrequece(rowDatas.get(targetFreIndex)));
				// 目标收集延期天数
				if(StringUtils.isNotBlank(rowDatas.get(targetDelayIndex))) {
					kpiTmp.setTargetSetInterval(Integer.parseInt(rowDatas.get(targetDelayIndex)));
				}
				// 目标收集报告频率
				if(StringUtils.isNotBlank(rowDatas.get(targetRepNameIndex))) {
					kpiTmp.setTargetReportDayFormulrShow(rowDatas.get(targetRepNameIndex));
				}
				// 目标收集报告频率设置
				if(StringUtils.isNotBlank(rowDatas.get(targetRepDateIndex))) {
					kpiTmp.setTargetSetReportDayFormulr(rowDatas.get(targetRepDateIndex));
				}
				// 目标收集报告日期频率
				kpiTmp.setTargetSetReportFrequence(convertFrequece(rowDatas.get(targetRepFreIndex)));
				
				// 报表小数点位置
				if(StringUtils.isNotBlank(rowDatas.get(positionIndex))) {
					kpiTmp.setScale(Integer.parseInt(rowDatas.get(positionIndex)));
				}
				// 趋势
				if(StringUtils.isNotBlank(rowDatas.get(relativeIndex))) {
					kpiTmp.setRelativeTo(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(relativeIndex),relaList));
				}
				boolean effDateFlag = true;
				// 方案生效日期
				if(StringUtils.isNotBlank(rowDatas.get(effIndex))){
					DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");					
					java.util.Date effDate = null;
					try {
					    effDate = fmt.parse(rowDatas.get(effIndex));
                    }
                    catch (Exception e) {
                        effDateFlag = false;
                    }
					kpiTmp.setWarningEffDate(effDate);
				}
				if(!effDateFlag){
				    validateInfo.append("方案日期设置错误,");
				}
				// 告警方案
				if(StringUtils.isNotBlank(rowDatas.get(alarmPlanIndex))){
				
				if(alarmFMap.containsKey(rowDatas.get(alarmPlanIndex))) {
					kpiTmp.setWarningSet(alarmFMap.get(rowDatas.get(alarmPlanIndex)));
				}else {
					validateInfo.append("告警方案不存在,");
				}
	
				}
				// 预警方案
				if(StringUtils.isNotBlank(rowDatas.get(warnPlanIndex))) {					
					if(alarmAMap.containsKey(rowDatas.get(warnPlanIndex))) {
						kpiTmp.setForeWarningSet(alarmAMap.get(rowDatas.get(warnPlanIndex)));
					} else {
						validateInfo.append("预警方案不存在,");
					}
				}
				// 最大值 
				if(StringUtils.isNotBlank(rowDatas.get(maxIndex))) {
				    if(NumberUtils.isNumber(rowDatas.get(maxIndex))){
				        kpiTmp.setMaxValue(Double.parseDouble(rowDatas.get(maxIndex)));
				    }
				    else{
				        validateInfo.append("最大值设置错误,");
				    }
				}				
				// 最小值
				if(StringUtils.isNotBlank(rowDatas.get(minIndex))) {
				    if(NumberUtils.isNumber(rowDatas.get(minIndex))){
				        kpiTmp.setMinValue(Double.parseDouble(rowDatas.get(minIndex)));
				    }
				    else{
				        validateInfo.append("最小值设置错误,");
				    }
				}
				
				// 公司
				kpiTmp.setCompany(UserContext.getUser().getCompanyid());
				// 行号
				kpiTmp.setRowNum(String.valueOf(row+1));
				// 序号
				if(StringUtils.isNotBlank(rowDatas.get(esortIndex))) {
					kpiTmp.setSort(Integer.parseInt(rowDatas.get(esortIndex)));
				}
				// 是否使用默认名称
				if("是".equals(rowDatas.get(defaultIndex))) {
					kpiTmp.setIsNameDefault(true);
				} else {
					kpiTmp.setIsNameDefault(false);
				}
				// 校验信息
				if(validateInfo.length() >0) {
					kpiTmp.setValidateInfoString(validateInfo.toString().substring(0, validateInfo.length() -1));
					flag = false;
				}
				kpiTmp.setId(Identities.uuid());
				kpiTmp.setDeleteStatus(true);
			    // 层级
				if(StringUtils.isNotBlank(rowDatas.get(levelIndex))) {
					kpiTmp.setLevel(Integer.parseInt(rowDatas.get(levelIndex)));
				}
				
				kpiTmpList.add(kpiTmp);
			}
			if(kpiTmpList.size() > 0) {
				findParent(kpiTmpList);
				findIsLeaf(kpiTmpList);
				batchImportKpiTmp(kpiTmpList);
			}
		}				
		return flag;
	}
	/**
	 * 导入Excel中的数据到临时表
	 * @param excelDatas Excel数据
	 * @param kpiType 指标类型
	 * @throws ParseException
	 */
	@Transactional
	public Boolean saveAllKpiDataFromExcel(List<List<String>> excelDatas,String kpiType,Boolean addStyle) throws ParseException {
	    Boolean flag = true;
	    //清空临时表数据 
	    String sql = "delete from tmp_imp_kpi_kpi where IS_KPI_CATEGORY = 'KPI'";
	    SQLQuery sqlQuery = o_kpiTmpDAO.createSQLQuery(sql);
	    sqlQuery.executeUpdate();
	    String companyId = UserContext.getUser().getCompanyid();
	    //部门map
	    Map<String,String> orgIdMap = o_dataImportCommBO.findOrgIdMap(companyId);
	    //人员map
	    Map<String,String> empIdMap = o_dataImportCommBO.findEmpIdMap(companyId);
	    //告警方案map
	    Map<String, String> alarmFMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_forecast");
	    //预警方案map
	    Map<String, String> alarmAMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_alarm");
	    //主纬度,辅助纬度List
	    List<DictEntry> mainOtherDimList = o_dictBO.findDictEntryByDictTypeId("kpi_dimension");
	    //趋势list
	    List<DictEntry> relaList = o_dictBO.findDictEntryByDictTypeId("kpi_relative_to");
	    //是否list
	    List<DictEntry> ynList = o_dictBO.findDictEntryByDictTypeId("0yn");
	    // 单位List
	    List<DictEntry> unitList = o_dictBO.findDictEntryByDictTypeId("0units");
	    // 指标方向性List
	    List<DictEntry> kpiDirList = o_dictBO.findDictEntryByDictTypeId("kpi_etype");
	    // 指标性质List
	    List<DictEntry> kpieTypeList = o_dictBO.findDictEntryByDictTypeId("kpi_kpi_type");
	    // 亮灯依据List
	    List<DictEntry> lightList = o_dictBO.findDictEntryByDictTypeId("kpi_alarm_measure");
	    // 预警依据List 
	    List<DictEntry> alarmBasisList = o_dictBO.findDictEntryByDictTypeId("kpi_alarm_basis");
	    // 计算方式
	    List<DictEntry> sumList = o_dictBO.findDictEntryByDictTypeId("kpi_sum_measure");
	    // 数据类型
	    // 指标类型map
	    Map<String, String> kpiTypeMap = o_kpiTypeDataImportBO.findAllKpiCodeList(Contents.KC_TYPE);
	    // 指标map
	    Map<String, String> kpiMap = o_kpiBO.findAllKpiCodeList(Contents.KPI_TYPE);
	    List<Kpi> oldKpiList = o_kpiBO.findAllKpiListByCompanyId(UserContext.getUser().getCompanyid());
	    // 已填写指标LIST
	    List<String> existKpiCodeList = new ArrayList<String>();
	    
	    List<String> existKpiNameList = new ArrayList<String>();
	    // 指标类型ID map
	    Map<String, String> kpiTypeIdMap = o_kpiTypeDataImportBO.findAllKpiTypeIdMap(Contents.KC_TYPE);
	    List<Kpi> kpiTypeList = o_kpiBO.findKpiTypeAll("", "id", "ASC");
	    // 设置各字段名称在EXCEL中的列位置
	    // 指标类型编号
	    int kpiTypeIndex = 0;
	    // 指标类型名称
	    int kpiTypeNameIndex = 1;
	    // 指标编号
	    int kpiCodeIndex = 2;
	    // 指标名称
	    int kpiNameIndex = 3;
	    // 继承信息
	    int isHeritIndex = 4;
	    // 上级指标
	    int parentIndex = 5;
	    // 说明
	    int descIndex = 6;
	    // 短名称
	    int shortNameIndex = 7;
	    // 所属部门
	    int belongDeptIndex = 8;
	    // 所属人员
	    int belongEmpIndex = 9;
	    // 采集部门
	    int gatherDeptIndex = 10;
	    // 采集人员
	    int gatherEmpIndex  = 11;
	    // 目标部门
	    int targetDeptIndex = 12;
	    // 目标人员
	    int targetEmpIndex = 13;
	    // 报告部门 
	    int reportDeptIndex = 14;
	    // 报告人员
	    int reportEmpIndex = 15;
	    // 查看部门
	    int viewDeptIndex = 16;
	    // 查看人员
	    int viewEmpIndex = 17;
	    // 是否启用
	    int enableIndex = 18;
	    // 是否监控
	    int monitorIndex = 19;
	    // 单位
	    int unitIndex = 20;
	    // 开始日期
	    int startDataIndex = 21;
	    // 指标方向性
	    int kpiDirIndex = 22;
	    // 指标性质
	    int eTypeIndex = 23;
	    // 亮灯依据
	    int lightIndex = 24;
	    // 预警依据
	    int alarmIndex = 25;
	    // 主维度
	    int mainDimIndex = 26;
	    // 辅助维度
	    int supportDimIndex = 27;
	    // 目标值别名
	    int targetAliasIndex = 28;
	    // 实际值别名
	    int resultAliasIndex = 29;
	    // 结果值公式
	    int resultFormulaIndex = 30;
	    // 目标值公式
	    int targetFormulaIndex = 31;
	    // 评估值公式
	    int assessFormulaIndex  = 32;
	    // 预警公式
	    int alarmFormulaIndex = 33;
	    // 关联关系公式
	    //int relaFormulaIndex = 34;
	    // 标杆值
	    int modelValueIndex = 35;
	    // 结果值累计计算
	    int resultSumIndex = 36;
	    // 目标值累计计算
	    int targetSumIndex = 37;
	    // 评估值累计计算
	    int assessSumIndex = 38;
	    // 结果收集日期显示名称
	    int resultDateNameIndex = 39;
	    // 结果收集日期设置
	    int resultDateIndex = 40;
	    // 结果收集频率
	    int resultFreIndex = 41;
	    // 结果收集延期天
	    int resultDelayIndex = 42;
	    // 结果收集报告显示名称
	    int resultRepNameIndex = 43;
	    //  结果收集报告日期设置
	    int resultRepDateIndex = 44;
	    // 结果收集报告频率
	    int resultRepFreIndex = 45;
	    // 目标收集日期显示名称
	    int targetDateNameIndex = 46;
	    // 目标收集日期设置
	    int targetDateIndex = 47;
	    // 目标收集日期频率
	    int targetFreIndex = 48;
	    // 目标收集延期天
	    int targetDelayIndex = 49;
	    // 目标收集报告日期显示名称
	    int targetRepNameIndex = 50;
	    // 目标收集报告日期设置
	    int targetRepDateIndex = 51;
	    // 目标收集报告频率
	    int targetRepFreIndex = 52;
	    // 报表小数点位置
	    int positionIndex = 53;
	    // 趋势相对于
	    int relativeIndex = 54;
	    // 方案生效日期
	    int effIndex = 55;
	    // 告警方案
	    int alarmPlanIndex = 56;
	    // 预警方案
	    int warnPlanIndex = 57;
	    // 最大值
	    int maxIndex = 58;
	    // 最小值
	    int minIndex = 59;
	    // 是否使用默认名称
	    int defaultIndex = 60;
	    // 序号
	    int esortIndex = 61;
	    // 层级 
	    int levelIndex = 62;
	    
	    List<KpiTmp> kpiTmpList = new ArrayList<KpiTmp>();
	    if (excelDatas != null && excelDatas.size() > 0) {
	        for (int row = 2; row < excelDatas.size(); row++) {
	            KpiTmp kpiTmp = new KpiTmp();
	            kpiTmp.setIsKpiCategory(Contents.KPI_TYPE);
	            kpiTmp.setCalc(Contents.DICT_Y);
	            // 验证信息汇总字符串
	            StringBuffer validateInfo = new StringBuffer();
	            List<String> rowDatas = (List<String>) excelDatas.get(row);
	            // 验证指标类型
	            if(StringUtils.isNotBlank(rowDatas.get(kpiTypeIndex)) && StringUtils.isNotBlank(rowDatas.get(kpiTypeNameIndex))){
	                if(!(rowDatas.get(kpiTypeNameIndex)).equalsIgnoreCase(kpiTypeMap.get(rowDatas.get(kpiTypeIndex)))) {
	                    //validateInfo.append("指标类型填写错误,");
	                    //校验真实表中,是否存在指标类型
	                    if(!addStyle){
	                        if(StringUtils.isBlank(o_dataImportCommBO.findKpiIdByCode(rowDatas.get(kpiTypeIndex), kpiTypeList))
	                                ||StringUtils.isBlank(o_dataImportCommBO.findKpiIdByName(rowDatas.get(kpiTypeNameIndex), kpiTypeList))){
	                            validateInfo.append("指标类型填写错误,");
	                        }
	                    }else{
	                        validateInfo.append("指标类型填写错误,");
	                    }
	                    
	                }
	            }
	            
	            kpiTmp.setBelongKpiCategory(kpiTypeIdMap.get(rowDatas.get(kpiTypeIndex)));
	            // 指标编号 名称
	            if(StringUtils.isNotBlank(rowDatas.get(kpiNameIndex)) && StringUtils.isNotBlank(rowDatas.get(kpiCodeIndex))) {
	                if(!addStyle) {
	                    if(kpiMap.containsKey(rowDatas.get(kpiCodeIndex))) {
	                        validateInfo.append("指标编号重复,");
	                    }
	                }
	                if(existKpiCodeList.contains(rowDatas.get(kpiCodeIndex))){
	                    validateInfo.append("指标编号重复,");
	                }
	                existKpiCodeList.add(rowDatas.get(kpiCodeIndex));
	            } else {
	                validateInfo.append("指标编号填写错误,");
	            }
	            
	            if(StringUtils.isBlank(rowDatas.get(kpiNameIndex))){
                    validateInfo.append("指标名称不存在,");
                }else{
                    if(!addStyle) {
                           if(StringUtils.isNotBlank(o_dataImportCommBO.findKpiIdByName(rowDatas.get(kpiNameIndex),oldKpiList)) || existKpiNameList.contains(rowDatas.get(kpiNameIndex))) {
                               validateInfo.append("指标名称重复,");
                           }
                       }
                    existKpiNameList.add(rowDatas.get(kpiNameIndex));
                }
	            
	            kpiTmp.setCode(rowDatas.get(kpiCodeIndex));
	            kpiTmp.setName(rowDatas.get(kpiNameIndex));
	            //继承信息
	            if("是".equals(rowDatas.get(isHeritIndex))) {
	                kpiTmp.setIsInherit(true);
	            } else {
	                kpiTmp.setIsInherit(false);
	            }
	            //  上级指标
	            if(StringUtils.isNotBlank(rowDatas.get(parentIndex)) && !existKpiCodeList.contains(rowDatas.get(parentIndex))){
	                validateInfo.append("上级指标不存在,");
	                kpiTmp.setParent(rowDatas.get(parentIndex));
	                
	            }
	            // 说明
	            kpiTmp.setDesc(rowDatas.get(descIndex));
	            // 短名称
	            kpiTmp.setShortName(rowDatas.get(shortNameIndex));
	            // 所属部门名称人员
	            String belongDept = rowDatas.get(belongDeptIndex);
	            String belongEmp = rowDatas.get(belongEmpIndex);
	            Map<String, Object> deptEmpMap = new HashMap<String, Object>();
	            if(StringUtils.isBlank(belongDept)) {
	                validateInfo.append("所属部门为空,");
	            } else {
	                deptEmpMap = validateDeptEmpInfo(belongDept,belongEmp,orgIdMap,empIdMap);
	                if(deptEmpMap.containsKey("error")) {
	                    if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
	                        validateInfo.append("所属部门填写错误,");
	                    } else {
	                        validateInfo.append("所属人员填写错误,");
	                    }
	                }
	            }
	            kpiTmp.setOwenrDept((String) deptEmpMap.get("empInfo"));
	            // 结果采集部门
	            String collectDept = rowDatas.get(gatherDeptIndex);
	            String collectEmp = rowDatas.get(gatherEmpIndex);
	            deptEmpMap = validateDeptEmpInfo(collectDept,collectEmp,orgIdMap,empIdMap);
	            if(deptEmpMap.containsKey("error")) {
	                if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
	                    validateInfo.append("采集部门填写错误,");
	                } else {
	                    validateInfo.append("采集人员填写错误,");
	                }
	            }
	            kpiTmp.setCollectDept((String) deptEmpMap.get("empInfo"));
	            
	            // 目标采集部门
	            String targetDept = rowDatas.get(targetDeptIndex);
	            String targetEmp = rowDatas.get(targetEmpIndex);
	            deptEmpMap = validateDeptEmpInfo(targetDept,targetEmp,orgIdMap,empIdMap);
	            if(deptEmpMap.containsKey("error")) {
	                if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
	                    validateInfo.append("目标部门填写错误,");
	                } else {
	                    validateInfo.append("目标人员填写错误,");
	                }
	            }
	            kpiTmp.setTargetDept((String) deptEmpMap.get("empInfo"));
	            // 报告部门
	            String reportDept = rowDatas.get(reportDeptIndex);
	            String reportEmp = rowDatas.get(reportEmpIndex);
	            deptEmpMap = validateDeptEmpInfo(reportDept,reportEmp,orgIdMap,empIdMap);
	            if(deptEmpMap.containsKey("error")) {
	                if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
	                    validateInfo.append("报告部门填写错误,");
	                } else {
	                    validateInfo.append("报告人员填写错误,");
	                }
	            }
	            kpiTmp.setReportDept((String) deptEmpMap.get("empInfo"));
	            // 查看部门
	            String checkDept = rowDatas.get(viewDeptIndex);
	            String checkEmp = rowDatas.get(viewEmpIndex);
	            deptEmpMap = validateDeptEmpInfo(checkDept,checkEmp,orgIdMap,empIdMap);
	            if(deptEmpMap.containsKey("error")) {
	                if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
	                    validateInfo.append("查看部门填写错误,");
	                } else {
	                    validateInfo.append("查看人员填写错误,");
	                }					
	            }
	            kpiTmp.setCheckDept((String) deptEmpMap.get("empInfo"));
	            // 是否启用
	            kpiTmp.setStatus(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(enableIndex),ynList));
	            // 是否监控
	            if("是".equals(rowDatas.get(monitorIndex))) {
	                kpiTmp.setIsMonitor(true);
	            } else {
	                kpiTmp.setIsMonitor(false);
	            }
	            // 单位
	            kpiTmp.setUnits(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(unitIndex),unitList));
	            // 开始如期
	            boolean startDateFlag = true;
	            if(StringUtils.isBlank(rowDatas.get(startDataIndex))) {
	                validateInfo.append("开始日期为空,");
	                kpiTmp.setStartDate(null);
	            } else {
	                SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");			       
	                java.util.Date startDate = null;
	                try {
	                    startDate = dataFormat.parse(rowDatas.get(startDataIndex));
                    }
                    catch (Exception e) {
                        startDateFlag = false;
                    }
	                kpiTmp.setStartDate(startDate);
	            } 
	            if(!startDateFlag){
	                validateInfo.append("开始日期设置错误,");
	            }
	            // 指标方向性
	            kpiTmp.setType(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(kpiDirIndex),kpiDirList));
	            
	            // 指标性质
	            kpiTmp.setKpiType(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(eTypeIndex),kpieTypeList));
	            // 亮灯依据
	            kpiTmp.setAlarmMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(lightIndex),lightList));
	            
	            // 预警依据
	            kpiTmp.setAlarmBasis(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(alarmIndex),alarmBasisList));
	            
	            // 主维度
	            kpiTmp.setMainDim(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(mainDimIndex),mainOtherDimList));
	            // 辅助维度
	            StringBuffer supportDim = new StringBuffer();
	            if(StringUtils.isNotBlank(rowDatas.get(supportDimIndex))) {
	                for(String dimName: rowDatas.get(supportDimIndex).split(",")) {
	                    supportDim.append(o_dataImportCommBO.findDictIdByDictName(dimName,mainOtherDimList)).append(",");
	                }
	            }
	            kpiTmp.setSupportDim(supportDim.toString());
	            
	            // 目标值别名
	            kpiTmp.setTargetValueAlias(rowDatas.get(targetAliasIndex));
	            // 实际值别名
	            kpiTmp.setResultValueAlias(rowDatas.get(resultAliasIndex));
	            // 结果值公式
	            if(StringUtils.isNotBlank(rowDatas.get(resultFormulaIndex))) {
	                kpiTmp.setResultFormula(rowDatas.get(resultFormulaIndex));
	                kpiTmp.setIsResultFormula("0sys_use_formular_formula");
	            } else {
	                kpiTmp.setIsResultFormula("0sys_use_formular_manual");
	            }
	            // 目标值公式
	            if(StringUtils.isNotBlank(rowDatas.get(targetFormulaIndex))) {
	                kpiTmp.setTargetFormula(rowDatas.get(targetFormulaIndex));
	                kpiTmp.setIsTargetFormula("0sys_use_formular_formula");
	            } else {
	                kpiTmp.setIsTargetFormula("0sys_use_formular_manual");
	            }
	            // 评估值公式
	            if(StringUtils.isNotBlank(rowDatas.get(assessFormulaIndex))) {
	                kpiTmp.setAssessmentFormula(rowDatas.get(assessFormulaIndex));
	                kpiTmp.setIsAssessmentFormula("0sys_use_formular_formula");
	            } else {
	                kpiTmp.setIsAssessmentFormula("0sys_use_formular_manual");
	            }
	            //预警公式
	            if(StringUtils.isNotBlank(rowDatas.get(alarmFormulaIndex))) {
	                kpiTmp.setForecastFormula(rowDatas.get(alarmFormulaIndex));
	            }
	            // 标杆值
	            if(StringUtils.isNotBlank(rowDatas.get(modelValueIndex))){
	                if(NumberUtils.isNumber(rowDatas.get(modelValueIndex))){
	                    kpiTmp.setModelValue(Double.parseDouble(rowDatas.get(modelValueIndex)));
	                }else{
	                    validateInfo.append("标杆值设置错误,");
	                }
	            }
	            // 结果值累计计算
	            if(StringUtils.isNotBlank(rowDatas.get(resultSumIndex))) {
	                kpiTmp.setResultSumMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(resultSumIndex),sumList));
	            }
	            // 目标值累计计算
	            if(StringUtils.isNotBlank(rowDatas.get(targetSumIndex))) {
	                kpiTmp.setTargetSumMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(targetSumIndex),sumList));
	            }
	            // 评估值累计计算
	            if(StringUtils.isNotBlank(rowDatas.get(assessSumIndex))) {
	                kpiTmp.setAssessmentSumMeasure(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(assessSumIndex),sumList));
	            }
	            // 结果收集频率显示（中文）
	            if(StringUtils.isNotBlank(rowDatas.get(resultDateNameIndex))) {
	                kpiTmp.setGatherDayFormulrShow(rowDatas.get(resultDateNameIndex));
	            }
	            // 结果值收集日期设置
	            if(StringUtils.isNotBlank(rowDatas.get(resultDateIndex))) {
	                kpiTmp.setGatherDayFormulr(rowDatas.get(resultDateIndex));
	            }
	            // 结果收集日期频率
	            kpiTmp.setGatherFrequence(convertFrequece(rowDatas.get(resultFreIndex)));
	            // 结果收集延期天
	            if(StringUtils.isNotBlank(rowDatas.get(resultDelayIndex))) {
	                kpiTmp.setResultCollectInterval(Integer.parseInt(rowDatas.get(resultDelayIndex)));
	            }
	            // 结果收集报告日期设置
	            if(StringUtils.isNotBlank(rowDatas.get(resultRepNameIndex))) {
	                kpiTmp.setGatherReportDayFormulrShow(rowDatas.get(resultRepNameIndex));
	            }
	            // 结果收集报告频率
	            if(StringUtils.isNotBlank(rowDatas.get(resultRepDateIndex))) {
	                kpiTmp.setGatherReportDayFormulr(rowDatas.get(resultRepDateIndex));
	            }
	            // 结果收集日期频率
	            kpiTmp.setReportFrequence(convertFrequece(rowDatas.get(resultRepFreIndex)));
	            // 目标收集频率
	            if(StringUtils.isNotBlank(rowDatas.get(targetDateIndex))) {
	                kpiTmp.setTargetSetDayFormular(rowDatas.get(targetDateIndex));
	            }
	            // 目标收集频率设置
	            if(StringUtils.isNotBlank(rowDatas.get(targetDateNameIndex))) {
	                kpiTmp.setTargetSetDayFormularShow(rowDatas.get(targetDateNameIndex));					
	            }
	            // 目标收集日期频率
	            kpiTmp.setTargetSetFrequence(convertFrequece(rowDatas.get(targetFreIndex)));
	            // 目标收集延期天数
	            if(StringUtils.isNotBlank(rowDatas.get(targetDelayIndex))) {
	                kpiTmp.setTargetSetInterval(Integer.parseInt(rowDatas.get(targetDelayIndex)));
	            }
	            // 目标收集报告频率
	            if(StringUtils.isNotBlank(rowDatas.get(targetRepNameIndex))) {
	                kpiTmp.setTargetReportDayFormulrShow(rowDatas.get(targetRepNameIndex));
	            }
	            // 目标收集报告频率设置
	            if(StringUtils.isNotBlank(rowDatas.get(targetRepDateIndex))) {
	                kpiTmp.setTargetSetReportDayFormulr(rowDatas.get(targetRepDateIndex));
	            }
	            // 目标收集报告日期频率
	            kpiTmp.setTargetSetReportFrequence(convertFrequece(rowDatas.get(targetRepFreIndex)));
	            
	            // 报表小数点位置
	            if(StringUtils.isNotBlank(rowDatas.get(positionIndex))) {
	                kpiTmp.setScale(Integer.parseInt(rowDatas.get(positionIndex)));
	            }
	            // 趋势
	            if(StringUtils.isNotBlank(rowDatas.get(relativeIndex))) {
	                kpiTmp.setRelativeTo(o_dataImportCommBO.findDictIdByDictName(rowDatas.get(relativeIndex),relaList));
	            }
	            // 方案生效日期
	            boolean effDateFlag = true;
	            if(StringUtils.isNotBlank(rowDatas.get(effIndex))){
	                DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");					
	                java.util.Date effDate = null;
                    try {
                        effDate = fmt.parse(rowDatas.get(effIndex));
                    }
                    catch (Exception e) {
                        effDateFlag = false;
                    }
                    kpiTmp.setWarningEffDate(effDate);
	            }
	            if(!effDateFlag){
	                validateInfo.append("方案日期设置错误,");
	            }
	            // 告警方案
	            if(StringUtils.isNotBlank(rowDatas.get(alarmPlanIndex))){
	                
	                if(alarmFMap.containsKey(rowDatas.get(alarmPlanIndex))) {
	                    kpiTmp.setWarningSet(alarmFMap.get(rowDatas.get(alarmPlanIndex)));
	                }else {
	                    validateInfo.append("告警方案不存在,");
	                }
	                
	            }
	            // 预警方案
	            if(StringUtils.isNotBlank(rowDatas.get(warnPlanIndex))) {					
	                if(alarmAMap.containsKey(rowDatas.get(warnPlanIndex))) {
	                    kpiTmp.setForeWarningSet(alarmAMap.get(rowDatas.get(warnPlanIndex)));
	                } else {
	                    validateInfo.append("预警方案不存在,");
	                }
	            }
	            // 最大值 
	            if(StringUtils.isNotBlank(rowDatas.get(maxIndex))) {
	                if(NumberUtils.isNumber(rowDatas.get(maxIndex))){
	                    kpiTmp.setMaxValue(Double.parseDouble(rowDatas.get(maxIndex)));
	                }else{
	                    validateInfo.append("最大值设置错误,");
	                }
	            }				
	            // 最小值
	            if(StringUtils.isNotBlank(rowDatas.get(minIndex))) {
	                if(NumberUtils.isNumber(rowDatas.get(minIndex))){
	                    kpiTmp.setMinValue(Double.parseDouble(rowDatas.get(minIndex)));
	                }else{
	                    validateInfo.append("最小值设置错误,");
	                }
	            }
	            
	            // 公司
	            kpiTmp.setCompany(UserContext.getUser().getCompanyid());
	            // 行号
	            kpiTmp.setRowNum(String.valueOf(row+1));
	            // 序号
	            if(StringUtils.isNotBlank(rowDatas.get(esortIndex))) {
	                kpiTmp.setSort(Integer.parseInt(rowDatas.get(esortIndex)));
	            }
	            // 是否使用默认名称
	            if("是".equals(rowDatas.get(defaultIndex))) {
	                kpiTmp.setIsNameDefault(true);
	            } else {
	                kpiTmp.setIsNameDefault(false);
	            }
	            // 校验信息
	            if(validateInfo.length() >0) {
	                kpiTmp.setValidateInfoString(validateInfo.toString().substring(0, validateInfo.length() -1));
	                flag = false;
	            }
	            kpiTmp.setId(Identities.uuid());
	            kpiTmp.setDeleteStatus(true);
	            // 层级
	            if(StringUtils.isNotBlank(rowDatas.get(levelIndex))) {
	                kpiTmp.setLevel(Integer.parseInt(rowDatas.get(levelIndex)));
	            }
	            
	            kpiTmpList.add(kpiTmp);
	        }
	        if(kpiTmpList.size() > 0) {
	            findParent(kpiTmpList);
	            findIsLeaf(kpiTmpList);
	            batchImportKpiTmp(kpiTmpList);
	        }
	    }				
	    return flag;
	}
	

	/**
	 * 批量插入数据到临时表
	 * @param KpiTmpList
	 */
	@Transactional
	public void batchImportKpiTmp(final List<KpiTmp> kpiTmpList) {
		o_kpiTmpDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException  {
				connection.setAutoCommit(false);
				PreparedStatement pst = null;
				String sql = "insert into tmp_imp_kpi_kpi (" + 
				             "COMPANY_ID,ID,KPI_CODE,KPI_NAME,SHORT_NAME," +
						     "EDESC,GATHER_FREQUENCE,GATHER_DAY_FORMULR,RESULT_COLLECT_INTERVAL,TARGET_SET_FREQUENCE," +
				             "TARGET_SET_DAY_FORMULAR,TARGET_SET_INTERVAL,GATHER_REPORT_FREQUENCE,GATHER_REPORT_DAY_FORMULR,TARGET_SET_REPORT_FREQUENCE," +
						     "TARGET_SET_REPORT_DAY_FORMULR,START_DATE,IS_TARGET_FORMULA,IS_RESULT_FORMULA,IS_ASSESSMENT_FORMULA," +
						     "TARGET_FORMULA,RESULT_FORMULA,ASSESSMENT_FORMULA,RELATION_FORMULA,FORECAST_FORMULA," +
						     "ESORT,DELETE_STATUS,IS_ENABLED,UNITS,TARGET_VALUE_ALIAS," +
						     "RESULT_VALUE_ALIAS,DATA_TYPE,IS_MONITOR,MONITOR_STATUS,ESCALE," +
						     "RELATIVE_TO,KPI_TYPE,ALARM_BASIS,ALARM_MEASURE,ETYPE,"+
						     "IS_KPI_CATEGORY,PARENT_ID,ID_SEQ,ELEVEL,IS_LEAF," +
						     "BELONG_KPI_CATEGORY,IS_INHERIT,MODEL_VALUE,TARGET_VALUE_SUM_MEASURE,FINISH_VALUE_SUM_MEASURE,"+
						     "ASSESSMENT_VALUE_SUM_MEASURE,CALCULATE_TIME,GATHER_DAY_FORMULR_SHOW,TARGET_SET_DAY_FORMULAR_show,GATHER_REPORT_DAY_FORMULR_show,"+
						     "IS_USE_DEFAULT_NAME,target_set_day_show,gather_day_show,anual_target_formula,MAX_VALUE," +
						     "MIN_VALUE,CHART_TYPE,TARGET_CALCULATE_TIME,TARGET_REPORT_DAY_FORMULR_SHOW,IS_CALC," +
						     "OWNER_DEPT,COLLECT_DEPT,TARGET_DEPT,REPORT_DEPT,CHECK_DEPT," +
						     "WARNING_EFF_DATE,WARNING_SET,FORE_WARNING_SET,VALIDATE_INFO,MAIN_DIM," +
						     "SUPPORT_DIM,EXCEL_ROWNO" +
						     ") values(" +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?,?,?,?," +
						     "?,?)";
				pst = connection.prepareStatement(sql);
				for(KpiTmp kpiTmp: kpiTmpList) {
					pst.setString(1, kpiTmp.getCompany());
					pst.setString(2, kpiTmp.getId());
					pst.setString(3, kpiTmp.getCode());
					pst.setString(4, kpiTmp.getName());
					pst.setString(5, kpiTmp.getShortName());
					pst.setString(6, kpiTmp.getDesc());
					pst.setString(7, kpiTmp.getGatherFrequence());
					pst.setString(8,kpiTmp.getGatherDayFormulr());
					if(null != kpiTmp.getResultCollectInterval()) {
						pst.setInt(9,kpiTmp.getResultCollectInterval());
					} else {
						pst.setInt(9, 0);
					}
					
					pst.setString(10,kpiTmp.getTargetSetFrequence());
					pst.setString(11,kpiTmp.getTargetSetDayFormular());
					if(null != kpiTmp.getTargetSetInterval()) {
						pst.setInt(12,kpiTmp.getTargetSetInterval());
					} else {
						pst.setInt(12, 0);
					}
					
					pst.setString(13,kpiTmp.getReportFrequence());
					pst.setString(14,kpiTmp.getGatherReportDayFormulr());
					pst.setString(15,kpiTmp.getTargetSetReportFrequence());
					pst.setString(16,kpiTmp.getTargetSetReportDayFormulr());
					if(null != kpiTmp.getStartDate()) {
						pst.setDate(17, new Date(kpiTmp.getStartDate().getTime()));
					} else {
						pst.setDate(17,null);
					}
					
					pst.setString(18,kpiTmp.getIsTargetFormula());
					pst.setString(19,kpiTmp.getIsResultFormula());
					pst.setString(20,kpiTmp.getIsAssessmentFormula());
					pst.setString(21,kpiTmp.getTargetFormula());
					pst.setString(22,kpiTmp.getResultFormula());
					pst.setString(23,kpiTmp.getAssessmentFormula());
					pst.setString(24,kpiTmp.getRelationFormula());
					pst.setString(25,kpiTmp.getForecastFormula());
					if(null != kpiTmp.getSort()) {
						pst.setInt(26,kpiTmp.getSort());
					} else {
						pst.setInt(26, 0);
					}
					
					pst.setBoolean(27,kpiTmp.getDeleteStatus());
					pst.setString(28,kpiTmp.getStatus());
					pst.setString(29,kpiTmp.getUnits());
					pst.setString(30,kpiTmp.getTargetValueAlias());
					pst.setString(31,kpiTmp.getResultValueAlias());
					pst.setString(32,kpiTmp.getDataType());
					pst.setBoolean(33,kpiTmp.getIsMonitor());
					pst.setString(34,kpiTmp.getMonitorStatus());
					if(null != kpiTmp.getScale()) {
						pst.setInt(35,kpiTmp.getScale());
					} else {
						pst.setInt(35,0);
					}					
					pst.setString(36,kpiTmp.getRelativeTo());
					pst.setString(37,kpiTmp.getKpiType());
					pst.setString(38,kpiTmp.getAlarmBasis());
					pst.setString(39,kpiTmp.getAlarmMeasure());
					pst.setString(40,kpiTmp.getType());
					pst.setString(41,kpiTmp.getIsKpiCategory());
					pst.setString(42,kpiTmp.getParent());
					pst.setString(43,kpiTmp.getIdSeq());
					if(null != kpiTmp.getLevel()) {
						pst.setInt(44,kpiTmp.getLevel());
					} else {
						pst.setInt(44,0);
					}					
					pst.setBoolean(45,kpiTmp.getIsLeaf());
					pst.setString(46,kpiTmp.getBelongKpiCategory());
					pst.setBoolean(47,kpiTmp.getIsInherit());
					if(null != kpiTmp.getModelValue()) {
						pst.setDouble(48,kpiTmp.getModelValue());
					} else {
						pst.setDouble(48,0);
					}					
					pst.setString(49,kpiTmp.getTargetSumMeasure());
					pst.setString(50,kpiTmp.getResultSumMeasure());
					pst.setString(51,kpiTmp.getAssessmentSumMeasure());
					if(null != kpiTmp.getCalculatetime()) {
						pst.setDate(52,new Date(kpiTmp.getCalculatetime().getTime()));
					} else {
						pst.setDate(52,null);
					}
					
					pst.setString(53,kpiTmp.getGatherDayFormulrShow());
					pst.setString(54,kpiTmp.getTargetSetDayFormularShow());
					pst.setString(55,kpiTmp.getGatherReportDayFormulrShow());
					pst.setBoolean(56,kpiTmp.getIsNameDefault());
					pst.setString(57,kpiTmp.getTargetSetDayFormularShow());
					pst.setString(58,kpiTmp.getGatherDayFormulrShow());
					pst.setString(59,null);
					if(null != kpiTmp.getMaxValue()) {
						pst.setDouble(60,kpiTmp.getMaxValue());
					} else {
						pst.setDouble(60,0);
					}
					if(null != kpiTmp.getMinValue()) {
						pst.setDouble(61,kpiTmp.getMinValue());
					} else {
						pst.setDouble(61,0);
					}
					pst.setString(62,null);
					if(null != kpiTmp.getTargetCalculatetime()) {
						pst.setDate(63,new Date(kpiTmp.getTargetCalculatetime().getTime()));
					} else {
						pst.setDate(63,null);
					}
					
					pst.setString(64,kpiTmp.getTargetReportDayFormulrShow());
					pst.setString(65,kpiTmp.getCalc());
					pst.setString(66,kpiTmp.getOwenrDept());
					pst.setString(67,kpiTmp.getCollectDept());
					pst.setString(68,kpiTmp.getTargetDept());
					pst.setString(69,kpiTmp.getReportDept());
					pst.setString(70,kpiTmp.getCheckDept());
					if(null != kpiTmp.getWarningEffDate()) {
						pst.setDate(71,new Date(kpiTmp.getWarningEffDate().getTime()));
					} else {
						pst.setDate(71,null);
					}					
					pst.setString(72,kpiTmp.getWarningSet());
					pst.setString(73,kpiTmp.getForeWarningSet());
					pst.setString(74,kpiTmp.getValidateInfo());
					pst.setString(75,kpiTmp.getMainDim());
					pst.setString(76,kpiTmp.getSupportDim());
					pst.setString(77, kpiTmp.getRowNum());
					pst.addBatch();
				}				
				pst.executeBatch();
	            connection.commit();
	            connection.setAutoCommit(true);
			}
		});
	}
	
	/**
	 * 导入临时表指标数据到业务表
	 * @param addStyle 导入方式
	 * @throws NumberFormatException
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void mergeKpiTmpKpi(Boolean addStyle,String type)  {
		// 覆盖导入的情况  初始化 指标数据
		if(addStyle) {
			initializeKpi(type);
		}
		String sql = "insert into t_kpi_kpi(" +
				          "COMPANY_ID," +
				          "ID," +
				          "KPI_CODE," +
				          "KPI_NAME," +
				          "SHORT_NAME," +
				          "EDESC," +
				          "GATHER_FREQUENCE," +
				          "GATHER_DAY_FORMULR," +
				          "RESULT_COLLECT_INTERVAL," +
				          "TARGET_SET_FREQUENCE," +
				          "TARGET_SET_DAY_FORMULAR," +
				          "TARGET_SET_INTERVAL," +
				          "GATHER_REPORT_FREQUENCE," +
				          "GATHER_REPORT_DAY_FORMULR," +
				          "TARGET_SET_REPORT_FREQUENCE," +
				          "TARGET_SET_REPORT_DAY_FORMULR," +
				          "START_DATE," +
				          "IS_TARGET_FORMULA," +
				          "IS_RESULT_FORMULA," +
				          "IS_ASSESSMENT_FORMULA," +
				          "TARGET_FORMULA," +
				          "RESULT_FORMULA," +
				          "ASSESSMENT_FORMULA," +
				          "RELATION_FORMULA," +
				          "FORECAST_FORMULA," +
				          "ESORT," +
				          "DELETE_STATUS," +
				          "IS_ENABLED," +
				          "UNITS," +
				          "TARGET_VALUE_ALIAS," +
				          "RESULT_VALUE_ALIAS," +
				          "DATA_TYPE," +
				          "IS_MONITOR," +
				          "MONITOR_STATUS," +
				          "ESCALE," +
				          "RELATIVE_TO," +
				          "KPI_TYPE," +
				          "ALARM_BASIS," +
				          "ALARM_MEASURE," +
				          "ETYPE," +
				          "IS_KPI_CATEGORY," +
				          "PARENT_ID," +
				          "ID_SEQ," +
				          "ELEVEL," +
				          "IS_LEAF," +
				          "BELONG_KPI_CATEGORY," +
				          "IS_INHERIT," +
				          "MODEL_VALUE," +
				          "TARGET_VALUE_SUM_MEASURE," +
				          "FINISH_VALUE_SUM_MEASURE," +
				          "ASSESSMENT_VALUE_SUM_MEASURE," +
				          "CALCULATE_TIME," +
				          "GATHER_DAY_FORMULR_show," +
				          "TARGET_SET_DAY_FORMULAR_show," +
				          "GATHER_REPORT_DAY_FORMULR_show," +
				          "IS_USE_DEFAULT_NAME," +
				          "MAX_VALUE," +
				          "MIN_VALUE," +
				          "TARGET_CALCULATE_TIME," +
				          "TARGET_REPORT_DAY_FORMULR_SHOW,"+
				          "is_calc" +
				          ") select " + 
						          "COMPANY_ID," +
						          "ID," +
						          "KPI_CODE," +
						          "KPI_NAME," +
						          "SHORT_NAME," +
						          "EDESC," +
						          "GATHER_FREQUENCE," +
						          "GATHER_DAY_FORMULR," +
						          "RESULT_COLLECT_INTERVAL," +
						          "TARGET_SET_FREQUENCE," +
						          "TARGET_SET_DAY_FORMULAR," +
						          "TARGET_SET_INTERVAL," +
						          "GATHER_REPORT_FREQUENCE," +
						          "GATHER_REPORT_DAY_FORMULR," +
						          "TARGET_SET_REPORT_FREQUENCE," +
						          "TARGET_SET_REPORT_DAY_FORMULR," +
						          "START_DATE," +
						          "IS_TARGET_FORMULA," +
						          "IS_RESULT_FORMULA," +
						          "IS_ASSESSMENT_FORMULA," +
						          "TARGET_FORMULA," +
						          "RESULT_FORMULA," +
						          "ASSESSMENT_FORMULA," +
						          "RELATION_FORMULA," +
						          "FORECAST_FORMULA," +
						          "ESORT," +
						          "DELETE_STATUS," +
						          "IS_ENABLED," +
						          "UNITS," +
						          "TARGET_VALUE_ALIAS," +
						          "RESULT_VALUE_ALIAS," +
						          "DATA_TYPE," +
						          "IS_MONITOR," +
						          "MONITOR_STATUS," +
						          "ESCALE," +
						          "RELATIVE_TO," +
						          "KPI_TYPE," +
						          "ALARM_BASIS," +
						          "ALARM_MEASURE," +
						          "ETYPE," +
						          "IS_KPI_CATEGORY," +
						          "PARENT_ID," +
						          "ID_SEQ," +
						          "ELEVEL," +
						          "IS_LEAF," +
						          "BELONG_KPI_CATEGORY," +
						          "IS_INHERIT," +
						          "MODEL_VALUE," +
						          "TARGET_VALUE_SUM_MEASURE," +
						          "FINISH_VALUE_SUM_MEASURE," +
						          "ASSESSMENT_VALUE_SUM_MEASURE," +
						          "CALCULATE_TIME," +
						          "GATHER_DAY_FORMULR_show," +
						          "TARGET_SET_DAY_FORMULAR_show," +
						          "GATHER_REPORT_DAY_FORMULR_show," +
						          "IS_USE_DEFAULT_NAME," +
						          "MAX_VALUE," +
						          "MIN_VALUE," +
						          "TARGET_CALCULATE_TIME," +
						          "TARGET_REPORT_DAY_FORMULR_SHOW,"+
						          "is_calc " +
				          "FROM tmp_imp_kpi_kpi " +
				          "where VALIDATE_INFO is null  " +
				          "AND IS_KPI_CATEGORY = ?";
				          
		SQLQuery sqlQuery = o_kpiDao.createSQLQuery(sql,type);
		sqlQuery.executeUpdate();
		// 查找校验无误的临时表数据
		Criteria criteria = o_kpiTmpDAO.createCriteria();
		criteria.add(Restrictions.isNull("validateInfo"));
		criteria.add(Restrictions.eq("isKpiCategory", type));
		criteria.addOrder(Order.asc("sort"));
		criteria.addOrder(Order.asc("code"));
		final List<KpiTmp> kpiTmplist = criteria.list();
		o_kpiTmpDAO.getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				//维度sql
				String dimSql = "insert into t_kpi_kpi_rela_dim(id,sm_dim_id,kpi_id,etype) values(?,?,?,?)";
				//关联部门人员
				String orgSql = "insert into t_kpi_kpi_rela_org_emp(id,KPI_ID,ETYPE,ORG_ID,EMP_ID) values(?,?,?,?,?)";
				//告警sql
				String alarmSql = "insert into t_kpi_kpi_rela_alarm(ID,KPI_ID,FC_ALARM_PLAN_ID,R_ALARM_PLAN_ID,START_DATE) values (?,?,?,?,?)";
				PreparedStatement dimPst = connection.prepareStatement(dimSql);
				PreparedStatement orgPst = connection.prepareStatement(orgSql);
				PreparedStatement alarmPst = connection.prepareStatement(alarmSql);
				for(KpiTmp kpiTmp : kpiTmplist) {
					//主维度
					String mainDim = kpiTmp.getMainDim();
					if(StringUtils.isNotBlank(mainDim)){
						dimPst.setString(1, Identities.uuid());
						dimPst.setString(2, mainDim);
						dimPst.setString(3,kpiTmp.getId());
						dimPst.setString(4, Contents.DUTY_DEPARTMENT);
						dimPst.addBatch();
					}
					//辅助纬度
					String otherDims = kpiTmp.getSupportDim();
					if(StringUtils.isNotBlank(otherDims)){
						String[] otherDimArray = StringUtils.split(otherDims, ",");
						for(String otherDim : otherDimArray){
							dimPst.setString(1, Identities.uuid());
							dimPst.setString(2, otherDim);
							dimPst.setString(3, kpiTmp.getId());
							dimPst.setString(4, Contents.RELATIVE_DEPARTMENT);
							dimPst.addBatch();
						}
					}
					//所属部门和人员
					String ownDeptJson = kpiTmp.getOwenrDept();
					String ownDeptId = null;
					String ownEmpId = null;
					if(StringUtils.isNotBlank(ownDeptJson)) {
						JSONArray jsonArray = JSONArray.fromObject(ownDeptJson);
						JSONObject jsonObject = (JSONObject) jsonArray.get(0);
						ownDeptId = jsonObject.getString("deptid");
						ownEmpId = jsonObject.getString("empid");								
					}
					//String ownEmp = strategyMapTmp.getOwnerEmp();
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, kpiTmp.getId());
					orgPst.setString(3, Contents.BELONGDEPARTMENT);
					orgPst.setString(4, ownDeptId);
					orgPst.setString(5, ownEmpId);
					orgPst.addBatch();
					//采集部门和人员
					String gatherDeptJson = kpiTmp.getOwenrDept();
					String gatherDeptId = null;
					String gatherEmpId = null;
					if(StringUtils.isNotBlank(gatherDeptJson)) {
						JSONArray jsonArray = JSONArray.fromObject(gatherDeptJson);
						JSONObject jsonObject = (JSONObject) jsonArray.get(0);
						gatherDeptId = jsonObject.getString("deptid");
						gatherEmpId = jsonObject.getString("empid");								
					}
					//String ownEmp = strategyMapTmp.getOwnerEmp();
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, kpiTmp.getId());
					orgPst.setString(3, Contents.GATHERDEPARTMENT);
					orgPst.setString(4, gatherDeptId);
					orgPst.setString(5, gatherEmpId);
					orgPst.addBatch();
					//目标部门和人员
					String targetDeptJson = kpiTmp.getOwenrDept();
					String targetDeptId = null;
					String targetEmpId = null;
					if(StringUtils.isNotBlank(targetDeptJson)) {
						JSONArray jsonArray = JSONArray.fromObject(targetDeptJson);
						JSONObject jsonObject = (JSONObject) jsonArray.get(0);
						targetDeptId = jsonObject.getString("deptid");
						targetEmpId = jsonObject.getString("empid");								
					}
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, kpiTmp.getId());
					orgPst.setString(3, Contents.TARGETDEPARTMENT);
					orgPst.setString(4, targetDeptId);
					orgPst.setString(5, targetEmpId);
					orgPst.addBatch();
					//报告部门人员
					String reportDeptJson = kpiTmp.getOwenrDept();
					String reportDeptId = null ;
					String reportEmpId = null;
					if(StringUtils.isNotBlank(reportDeptJson)) {
						JSONArray jsonArray = JSONArray.fromObject(reportDeptJson);
						JSONObject jsonObject = (JSONObject) jsonArray.get(0);
						reportDeptId = jsonObject.getString("deptid");
						reportEmpId = jsonObject.getString("empid");								
					}
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, kpiTmp.getId());
					orgPst.setString(3, Contents.REPORTDEPARTMENT);
					orgPst.setString(4, reportDeptId);
					orgPst.setString(5, reportEmpId);
					orgPst.addBatch();
					//查看部门人员
					String viewDeptJson = kpiTmp.getOwenrDept();
					String viewDeptId = null;
					String viewEmpId = null;
					if(StringUtils.isNotBlank(viewDeptJson)) {
						JSONArray jsonArray = JSONArray.fromObject(viewDeptJson);
						JSONObject jsonObject = (JSONObject) jsonArray.get(0);
						viewDeptId = jsonObject.getString("deptid");
						viewEmpId = jsonObject.getString("empid");								
					}
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, kpiTmp.getId());
					orgPst.setString(3, Contents.VIEWDEPARTMENT);
					orgPst.setString(4, viewDeptId);
					orgPst.setString(5, viewEmpId);
					orgPst.addBatch();
					//告警方案
					String warningSet = kpiTmp.getWarningSet();
					//预警方案
					String forcastSet = kpiTmp.getForeWarningSet();
					java.util.Date warningDate = kpiTmp.getWarningEffDate();
					if(StringUtils.isNotBlank(warningSet)||StringUtils.isNotBlank(forcastSet)){
						alarmPst.setString(1, Identities.uuid());
						alarmPst.setString(2, kpiTmp.getId());
						alarmPst.setString(3, forcastSet);
						alarmPst.setString(4, warningSet);
						if(null!=warningDate){
							alarmPst.setDate(5,new Date(warningDate.getTime()));
						}else{
							alarmPst.setDate(5,null);
						}
						alarmPst.addBatch();
					}
				}
				dimPst.executeBatch();
				orgPst.executeBatch();
				alarmPst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
			}

		});
	    // 生成采集结果数据 
		/*java.util.Date current =  new java.util.Date();
    	String currentYear = DateUtils.getYear(current);
		for(KpiTmp kpiTmp : kpiTmplist) {
			if(null != kpiTmp.getGatherFrequence() && StringUtils.isNotBlank(kpiTmp.getGatherFrequence())) {
				 this.o_kpiGatherResultBO.saveBatchKpiGatherResultByFrequenceType(kpiTmp.getId(), kpiTmp.getGatherFrequence(), Integer.parseInt(currentYear));
			}
		}*/
		// 清空临时表数据
		String deleteSql = "delete from tmp_imp_kpi_kpi where IS_KPI_CATEGORY = ?";
		o_kpiTmpDAO.createSQLQuery(deleteSql,type).executeUpdate();	
	}
	
	/**
	 *  初始化该公司下的指标数据(覆盖导入时用)
	 */
	@Transactional
	public void initializeKpi(String type) {
		String companyId = UserContext.getUser().getCompanyid();
		// 清空二级缓存
		o_kpiDao.getSessionFactory().evict(Kpi.class);
		// 初始化关联表数据
		String kpiRelaDimDel = "delete from t_kpi_kpi_rela_dim where kpi_id in " +
				"(select id from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?)";
		String kpiRelaOrgDel =  "delete from t_kpi_kpi_rela_org_emp where kpi_id in " +
				"(select id from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?)";
		String kpiRelaAlarmDel = "delete from t_kpi_kpi_rela_alarm where kpi_id in " +
				"(select id from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?)";
		// t_kpi_sm_rela_kpi
		String scRelaKpiDel = "delete from t_kpi_kpi_rela_category where kpi_id in " +
				"(select id from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?)";
		String smRelaKpiDel = "delete from t_kpi_sm_rela_kpi where kpi_id in " +
				"(select id from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?)";
		String gatherResultDel = "delete from t_kpi_kpi_gather_result where kpi_id in " +
				"(select id from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?)";
		
		// 风险模块关联指标表 删除
        String riskRelaDel = "delete from t_kpi_kpi_rela_risk  where kpi_id in " +
				"(select id from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?)";
		
		String kpiDel = "delete from t_kpi_kpi where IS_KPI_CATEGORY = ?" + 
				" and COMPANY_ID =  ?";
		//最后删除指标表中本公司的指标数据
		o_kpiDao.createSQLQuery(kpiRelaDimDel,type,companyId).executeUpdate();
		o_kpiDao.createSQLQuery(kpiRelaOrgDel,type,companyId).executeUpdate();
		o_kpiDao.createSQLQuery(kpiRelaAlarmDel,type,companyId).executeUpdate();
		o_kpiDao.createSQLQuery(scRelaKpiDel,type,companyId).executeUpdate();
		o_kpiDao.createSQLQuery(smRelaKpiDel,type,companyId).executeUpdate();
		o_kpiDao.createSQLQuery(gatherResultDel,type,companyId).executeUpdate();
		o_kpiDao.createSQLQuery(riskRelaDel,type,companyId).executeUpdate();
		o_kpiDao.createSQLQuery(kpiDel,type,companyId).executeUpdate();
		
	}
	
	
	/**
     * 导入指标历史数据
     * @param excelDatas Excel中的数据
     * @return 导入是否成功
     * @throws NumberFormatException
     * @throws SQLException
     */
    @SuppressWarnings({ "unchecked" })
    @Transactional
    public JSONObject saveKpiGatherDatasFromExcel(final List<List<String>> excelDatas) {
        
        // 指标类型ID map
        final Map<String, String> kpiTypeIdMap = o_kpiBO.findAllKpiTypeIdMap(Contents.KPI_TYPE);
        Criteria criteria = o_kpiRelaAlarmDAO.createCriteria();
        criteria.createAlias("kpi", "kpi");
        criteria.add(Restrictions.eq("kpi.company.id", UserContext.getUser().getCompanyid()));
        criteria.addOrder(Order.desc("startDate"));
        List<KpiRelaAlarm> list = criteria.list();
        // 指标CODE 与告警方案 映射map
        final Map<String, Object> map = new HashMap<String, Object>();
        if(list.size() > 0) {
            for(KpiRelaAlarm kpiRelaAlarm:list) {
                if(!map.containsKey(kpiRelaAlarm.getKpi().getCode())) {
                    if(null!=kpiRelaAlarm.getKpi().getCode()){
                        map.put(kpiRelaAlarm.getKpi().getCode(), kpiRelaAlarm.getrAlarmPlan().getId());
                    }
                }
            }
        }
        
        // 告警方案 id/区间map生成
        String alarmPlanQuery = "select alarm_plan_id,max_value,min_value,is_contain_min,is_contain_max,alarm_icon " +
                                "from t_com_alarm_region";
        String kpiResultStateQuery = "SELECT K.KPI_CODE,T.EYEAR " +
                                      "FROM T_KPI_KPI_GATHER_RESULT R INNER JOIN" +
                                      "     T_KPI_KPI K ON R.KPI_ID = K.ID," +
                                      "     T_COM_TIME_PERIOD T " +
                                      "WHERE K.KPI_CODE IN (:kpiIdList) " +
                                      "AND T.ID = R.TIME_PERIOD_ID " +
                                      "GROUP BY K.KPI_CODE,T.EYEAR";
         // 预警方案id/区间 映射
         final Map<String, Object> alarmMap = map;
         
         List<String> kpiCodeList = new ArrayList<String>();
         StringBuffer kpiCodes = new StringBuffer();
         Boolean flag = true;
         JSONObject resultObj = new JSONObject();
         JSONArray gatherDatasErrors = new JSONArray();
         resultObj.put("result", flag);
         Boolean validateFlag = true;
         if (excelDatas != null && excelDatas.size() > 0) {
             for (int row = 2; row < excelDatas.size(); row++) {
                 boolean rowValidateFlag = true;
                 StringBuffer msgBuf = new StringBuffer();
                 
                 List<String> rowDatas = (List<String>) excelDatas.get(row);
                 // 拼接查询in字符串
                 String code = rowDatas.get(0);
                 String name = rowDatas.get(1);
                 String frequence = rowDatas.get(2);
                 String date = rowDatas.get(3);
                 String targetValue = rowDatas.get(4);
                 String resultValue = rowDatas.get(5);
                 String assessmentValue = rowDatas.get(6);
                 String timeperiod = rowDatas.get(7);
                 String year = rowDatas.get(8);
                 
                 
                 if(StringUtils.isBlank(code)||!kpiTypeIdMap.containsKey(code)){
                     rowValidateFlag = false;
                     validateFlag = false;
                     msgBuf.append("编号错误,");
                 }else{
                     if(!kpiCodeList.contains(code)) {
                         kpiCodes.append("'").append(code).append("'").append(",");                   
                         kpiCodeList.add(code);
                      }
                 }
                 
                 
                 if(!"月".equals(frequence)){
                     validateFlag = false;
                     rowValidateFlag = false;
                     msgBuf.append("采集频率错误,");
                 }
                 
                 if(!NumberUtils.isNumber(targetValue)&&!"".equals(targetValue.trim())){              
                     validateFlag = false;
                     rowValidateFlag = false;
                     msgBuf.append("目标值错误,");
                 }
                 if(!NumberUtils.isNumber(resultValue)&&!"".equals(resultValue.trim())){              
                     validateFlag = false;
                     rowValidateFlag = false;
                     msgBuf.append("实际值错误,");
                 }
                 if(!NumberUtils.isNumber(resultValue)&&!"".equals(resultValue.trim())){              
                     validateFlag = false;
                     rowValidateFlag = false;
                     msgBuf.append("评估值错误,");
                 }
                 
                 if(!rowValidateFlag){
                     JSONObject rowObj = new JSONObject();
                     rowObj.put("rownum", row+1);
                     rowObj.put("validateMsg", msgBuf.toString());
                     rowObj.put("code", code);
                     rowObj.put("name", name);
                     rowObj.put("frequence", frequence);
                     rowObj.put("gatherDate", date);
                     rowObj.put("targetvalue", targetValue);
                     rowObj.put("finishvalue", resultValue);
                     rowObj.put("assessmentvalue", assessmentValue);
                     rowObj.put("eyear", year);
                     rowObj.put("timeperiod", timeperiod);
                     gatherDatasErrors.add(rowObj);
                 }
                 
             }
         }
         
         if(validateFlag) {
             List<Object[]> resultYearState = o_kpiGatherResultDAO.createSQLQuery(kpiResultStateQuery).setParameterList("kpiIdList", kpiCodeList,new StringType()).list();
             // kpiCode 与 数据库中存在年份的映射
             final Map<String,Object> resultYearMap = new HashMap<String,Object>();
             for(Object[] o : resultYearState) {
                 String code = (String) o[0];
                 String year = String.valueOf(o[1]) ;
                 if(resultYearMap.containsKey(code)) {
                     List<String> yearList = (List<String>) resultYearMap.get(code);
                     yearList.add(year);
                     resultYearMap.put(code, yearList);
                 } else {
                     List<String> newYearList  = new ArrayList<String>();
                     newYearList.add(year);
                     resultYearMap.put(code, newYearList);
                 }
             }
             List<Object[]> alarmRegionList = o_kpiGatherResultDAO.createSQLQuery(alarmPlanQuery).list();
             for(Object[] o: alarmRegionList) {
                 String alarmPlanId = (String) o[0]; 
                 String maxValue = (String) o[1];
                 String minValue = (String) o[2];
                 String isContainMin = ((String) o[3]).substring(16);
                 String isContainMax = ((String) o[4]).substring(16);
                 String icon  = (String) o[5];
                 Map<String,String> iconMap = new HashMap<String,String>();
                 iconMap.put("maxValue", maxValue);
                 iconMap.put("minValue", minValue);
                 iconMap.put("isContainMin",isContainMin);
                 iconMap.put("isContainMax", isContainMax);
                 if(alarmMap.containsKey(alarmPlanId)) {
                     Map<String, Object> regionMap = (Map<String, Object>) alarmMap.get(alarmPlanId);
                     regionMap.put(icon, iconMap);
                     alarmMap.put(alarmPlanId, regionMap);
                 } else {
                     Map<String,Object> regionMap = new HashMap<String,Object>();
                     regionMap.put(icon, iconMap);
                     alarmMap.put(alarmPlanId, regionMap);
                 }
             }
             // 生成当年的采集数据
             for (int row = 2; row < excelDatas.size(); row++) {
                    List<String> rowDatas = excelDatas.get(row);
                    List<String> yearList = new ArrayList<String>();
                    yearList = (List<String>) resultYearMap.get(rowDatas.get(0));
                    if(null==yearList){
                        yearList = new ArrayList<String>();
                    }
                    if(!yearList.contains(rowDatas.get(8))) {
                        yearList.add(rowDatas.get(8));
                       String kpiId =  kpiTypeIdMap.get(rowDatas.get(0));
                       if(null!=kpiId){
                           o_kpiGatherResultBO.saveBatchKpiGatherResultByFrequenceType(kpiId, convertFrequece(rowDatas.get(2)), Integer.parseInt(rowDatas.get(8)));
                            resultYearMap.put(rowDatas.get(0), yearList);
                       }
                        
                    }
               }

                o_kpiGatherResultDAO.getSession().doWork(new Work() {
                       @Override
                       public void execute(Connection connection)
                               throws SQLException {
                           connection.setAutoCommit(false);
                           PreparedStatement pst = null;
                           String sql = "update t_kpi_kpi_gather_result " +
                                        "set target_value = ? , " +
                                        "    finish_value = ? , " +
                                        "    assessment_value = ? , " +
                                        "    assessment_status = ?" +
                                        "where kpi_id = (" +
                                                        "select id from t_kpi_kpi where kpi_code = ? and company_id = ?) " + 
                                        "and time_period_id = ?";
                           pst = connection.prepareStatement(sql);
                            for (int row = 2; row < excelDatas.size(); row++) {
                                List<String> rowDatas = (List<String>) excelDatas.get(row);
                                if(StringUtils.isNotBlank(rowDatas.get(4))){
                                    pst.setDouble(1,Double.parseDouble(rowDatas.get(4)));
                                }else{
                                    pst.setObject(1,null);
                                }
                                if(StringUtils.isNotBlank(rowDatas.get(5))){
                                    pst.setDouble(2, Double.parseDouble(rowDatas.get(5)));
                                }else{
                                    pst.setObject(2, null);
                                }if(StringUtils.isNotBlank(rowDatas.get(6))){
                                    pst.setDouble(3, Double.parseDouble(rowDatas.get(6)));
                                }else{
                                    pst.setObject(3, null);
                                }
                                String alarmPlanId = (String) map.get(rowDatas.get(0));
                                if(StringUtils.isNotBlank(alarmPlanId))  {
                                   Map<String, Object> alarmRegion = (Map<String, Object>) alarmMap.get(alarmPlanId);
                                   if(STATUS_MEASURE.equals(rowDatas.get(9)))  {
                                       if(StringUtils.isNotBlank(rowDatas.get(6))) {
                                           Boolean isGetValue = false;
                                           Set<Map.Entry<String, Object>> keyset = alarmRegion.entrySet();
                                          for(Map.Entry<String, Object> key: keyset) {
                                             Map<String, String> keyValueMap = (Map<String, String>) key.getValue();
                                             if(validateRegionInfo(keyValueMap,rowDatas.get(6))) {
                                                 pst.setString(4, key.getKey());
                                                 isGetValue = true;
                                             }
                                          }
                                          if(!isGetValue) {
                                              pst.setString(4, null); 
                                          }
                                       }else{
                                           pst.setString(4, null); 
                                       }

                                   } 
                                   else if(RESULT_MEASURE.equals(rowDatas.get(9))) {
                                       if(StringUtils.isNotBlank(rowDatas.get(6))) {
                                           Boolean isGetValue = false;
                                           Set<Map.Entry<String, Object>> keyset = alarmRegion.entrySet();
                                          for(Map.Entry<String, Object> key: keyset) {
                                              Map<String, String> keyValueMap = (Map<String, String>) key.getValue();
                                             if(validateRegionInfo(keyValueMap,rowDatas.get(6))) {
                                                 pst.setString(4, key.getKey());
                                                 isGetValue = true;
                                             }
                                          }
                                          if(!isGetValue) {
                                              pst.setString(4, null); 
                                          }
                                       }else{
                                           pst.setString(4, null); 
                                       }
                                   }
                                   else {
                                       pst.setString(4, null);
                                   }
                                
                                }else{
                                    pst.setString(4, null);
                                }
                                pst.setString(5, rowDatas.get(0));
                                pst.setString(6, UserContext.getUser().getCompanyid());
                                pst.setString(7, rowDatas.get(7));
                                pst.addBatch();                                     
                            }
                               pst.executeBatch();
                               connection.commit();
                               connection.setAutoCommit(true);
                           
                       }
                    });
            }else{
                resultObj.put("result", false);
                resultObj.put("errors", gatherDatasErrors);
            }
         resultObj.put("errors", gatherDatasErrors);
         return resultObj;
    }
	
	/**
	 * 导入指标历史数据
	 * @param excelDatas Excel中的数据
	 * @return 导入是否成功
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked" })
	@Transactional
	public Boolean saveKpiGatherDataFromExcel(final List<List<String>> excelDatas) {
		
		// 指标类型ID map
        final Map<String, String> kpiTypeIdMap = o_kpiBO.findAllKpiTypeIdMap(Contents.KPI_TYPE);
		Criteria criteria = o_kpiRelaAlarmDAO.createCriteria();
		criteria.createAlias("kpi", "kpi");
		criteria.add(Restrictions.eq("kpi.company.id", UserContext.getUser().getCompanyid()));
		criteria.addOrder(Order.desc("startDate"));
		List<KpiRelaAlarm> list = criteria.list();
		// 指标CODE 与告警方案 映射map
		final Map<String, Object> map = new HashMap<String, Object>();
		if(list.size() > 0) {
			for(KpiRelaAlarm kpiRelaAlarm:list) {
				if(!map.containsKey(kpiRelaAlarm.getKpi().getCode())) {
				    if(null!=kpiRelaAlarm.getKpi().getCode()){
				        map.put(kpiRelaAlarm.getKpi().getCode(), kpiRelaAlarm.getrAlarmPlan().getId());
				    }
				}
			}
		}
		
		// 告警方案 id/区间map生成
		String alarmPlanQuery = "select alarm_plan_id,max_value,min_value,is_contain_min,is_contain_max,alarm_icon " +
				                "from t_com_alarm_region";
		String kpiResultStateQuery = "SELECT K.KPI_CODE,T.EYEAR " +
				                      "FROM T_KPI_KPI_GATHER_RESULT R INNER JOIN" +
				                      "     T_KPI_KPI K ON R.KPI_ID = K.ID," +
				                      "     T_COM_TIME_PERIOD T " +
				                      "WHERE K.KPI_CODE IN (:kpiIdList) " +
				                      "AND T.ID = R.TIME_PERIOD_ID " +
				                      "GROUP BY K.KPI_CODE,T.EYEAR";
		 // 预警方案id/区间 映射
		 final Map<String, Object> alarmMap = map;
		 
		 List<String> kpiCodeList = new ArrayList<String>();
		 StringBuffer kpiCodes = new StringBuffer();
		 Boolean flag = true;
		 if (excelDatas != null && excelDatas.size() > 0) {
			 for (int row = 2; row < excelDatas.size(); row++) {
				 List<String> rowDatas = (List<String>) excelDatas.get(row);
				 // 拼接查询in字符串
				 if(!kpiCodeList.contains(rowDatas.get(0))) {
					kpiCodes.append("'").append(rowDatas.get(0)).append("'").append(",");					
					kpiCodeList.add(rowDatas.get(0));
				 }
				 // 验证数据合法性
				 if((!isNumber(rowDatas.get(4))&&!"".equals(rowDatas.get(4).trim()) )||
			       ( !isNumber(rowDatas.get(5)) &&!"".equals(rowDatas.get(5).trim()) )||	
			        (!isNumber(rowDatas.get(6)) &&!"".equals(rowDatas.get(6).trim()) ) ||
			        StringUtils.isBlank(rowDatas.get(3))) { 
					 flag = false;
					 break;
				 } 
			 }
			
		     if(flag) {
		    	  List<Object[]> resultYearState = o_kpiGatherResultDAO.createSQLQuery(kpiResultStateQuery).setParameterList("kpiIdList", kpiCodeList,new StringType()).list();
		    	  // kpiCode 与 数据库中存在年份的映射
		    	  final Map<String,Object> resultYearMap = new HashMap<String,Object>();
		    	  for(Object[] o : resultYearState) {
		    		  String code = (String) o[0];
		    		  String year = String.valueOf(o[1]) ;
		    		  if(resultYearMap.containsKey(code)) {
		    			  List<String> yearList = (List<String>) resultYearMap.get(code);
		    			  yearList.add(year);
		    			  resultYearMap.put(code, yearList);
		    		  } else {
		    			  List<String> newYearList  = new ArrayList<String>();
		    			  newYearList.add(year);
		    			  resultYearMap.put(code, newYearList);
		    		  }
		    	  }
		    	  List<Object[]> alarmRegionList = o_kpiGatherResultDAO.createSQLQuery(alarmPlanQuery).list();
		    	  for(Object[] o: alarmRegionList) {
		    		  String alarmPlanId = (String) o[0]; 
		    		  String maxValue = (String) o[1];
		    		  String minValue = (String) o[2];
		    		  String isContainMin = ((String) o[3]).substring(16);
		    		  String isContainMax = ((String) o[4]).substring(16);
		    		  String icon  = (String) o[5];
		    		  Map<String,String> iconMap = new HashMap<String,String>();
		    		  iconMap.put("maxValue", maxValue);
		    		  iconMap.put("minValue", minValue);
		    		  iconMap.put("isContainMin",isContainMin);
		    		  iconMap.put("isContainMax", isContainMax);
		    		  if(alarmMap.containsKey(alarmPlanId)) {
		    			  Map<String, Object> regionMap = (Map<String, Object>) alarmMap.get(alarmPlanId);
		    			  regionMap.put(icon, iconMap);
		    			  alarmMap.put(alarmPlanId, regionMap);
		    		  } else {
		    			  Map<String,Object> regionMap = new HashMap<String,Object>();
		    			  regionMap.put(icon, iconMap);
		    			  alarmMap.put(alarmPlanId, regionMap);
		    		  }
		    	  }
				  // 生成当年的采集数据
		    	  for (int row = 2; row < excelDatas.size(); row++) {
		    		     List<String> rowDatas = excelDatas.get(row);
		    		     List<String> yearList = new ArrayList<String>();
						 yearList = (List<String>) resultYearMap.get(rowDatas.get(0));
						 if(null==yearList){
						     yearList = new ArrayList<String>();
						 }
						 if(!yearList.contains(rowDatas.get(8))) {
							 yearList.add(rowDatas.get(8));
							String kpiId =  kpiTypeIdMap.get(rowDatas.get(0));
							if(null!=kpiId){
							    o_kpiGatherResultBO.saveBatchKpiGatherResultByFrequenceType(kpiId, convertFrequece(rowDatas.get(2)), Integer.parseInt(rowDatas.get(8)));
	                             resultYearMap.put(rowDatas.get(0), yearList);
							}
							 
						 }
		    	    }

					 o_kpiGatherResultDAO.getSession().doWork(new Work() {
							@Override
							public void execute(Connection connection)
									throws SQLException {
								connection.setAutoCommit(false);
								PreparedStatement pst = null;
								String sql = "update t_kpi_kpi_gather_result " +
							                 "set target_value = ? , " +
										     "    finish_value = ? , " +
							                 "    assessment_value = ? , " +
										     "    assessment_status = ?" +
		 								     "where kpi_id = (" +
										                     "select id from t_kpi_kpi where kpi_code = ? and company_id = ?) " + 
										     "and time_period_id = ?";
								pst = connection.prepareStatement(sql);
								 for (int row = 2; row < excelDatas.size(); row++) {
									 List<String> rowDatas = (List<String>) excelDatas.get(row);
									 if(StringUtils.isNotBlank(rowDatas.get(4))){
									     pst.setDouble(1,Double.parseDouble(rowDatas.get(4)));
									 }else{
									     pst.setObject(1,null);
									 }
									 if(StringUtils.isNotBlank(rowDatas.get(5))){
									     pst.setDouble(2, Double.parseDouble(rowDatas.get(5)));
									 }else{
									     pst.setObject(2, null);
									 }if(StringUtils.isNotBlank(rowDatas.get(6))){
									     pst.setDouble(3, Double.parseDouble(rowDatas.get(6)));
									 }else{
									     pst.setObject(3, null);
									 }
									 String alarmPlanId = (String) map.get(rowDatas.get(0));
                                     if(StringUtils.isNotBlank(alarmPlanId))  {
                                    	Map<String, Object> alarmRegion = (Map<String, Object>) alarmMap.get(alarmPlanId);
                                        if(STATUS_MEASURE.equals(rowDatas.get(9)))  {
                                        	if(StringUtils.isNotBlank(rowDatas.get(6))) {
                                        		Boolean isGetValue = false;
                                        		Set<Map.Entry<String, Object>> keyset = alarmRegion.entrySet();
                                        	   for(Map.Entry<String, Object> key: keyset) {
                                        		  Map<String, String> keyValueMap = (Map<String, String>) key.getValue();
                                        		  if(validateRegionInfo(keyValueMap,rowDatas.get(6))) {
                                        			  pst.setString(4, key.getKey());
                                        			  isGetValue = true;
                                        		  }
                                        	   }
                                        	   if(!isGetValue) {
                                        		   pst.setString(4, null); 
                                        	   }
                                        	}else{
                                        	    pst.setString(4, null); 
                                        	}

                                        } 
                                        else if(RESULT_MEASURE.equals(rowDatas.get(9))) {
                                        	if(StringUtils.isNotBlank(rowDatas.get(6))) {
                                        		Boolean isGetValue = false;
                                        		Set<Map.Entry<String, Object>> keyset = alarmRegion.entrySet();
                                        	   for(Map.Entry<String, Object> key: keyset) {
                                        		   Map<String, String> keyValueMap = (Map<String, String>) key.getValue();
                                        		  if(validateRegionInfo(keyValueMap,rowDatas.get(6))) {
                                        			  pst.setString(4, key.getKey());
                                        			  isGetValue = true;
                                        		  }
                                        	   }
                                        	   if(!isGetValue) {
                                        		   pst.setString(4, null); 
                                        	   }
                                        	}else{
                                        	    pst.setString(4, null); 
                                        	}
                                        }
                                        else {
                                        	pst.setString(4, null);
                                        }
                                     
                                     }else{
                                         pst.setString(4, null);
                                     }
									 pst.setString(5, rowDatas.get(0));
									 pst.setString(6, UserContext.getUser().getCompanyid());
									 pst.setString(7, rowDatas.get(7));
									 pst.addBatch();									 
								 }
									pst.executeBatch();
						            connection.commit();
						            connection.setAutoCommit(true);
								
							}
						 });
					 // 计算当前采集结果的趋势
//					 String kpigatherResultQuery  =  "select kpi_id,time_period_id,assessment_value " +
//					 		                         "from T_KPI_KPI_GATHER_RESULT" +
//					 		                         " where kpi_id in (select id from t_kpi_kpi" +
//					 		                         "                 where kpi_code in (:kpiIdList))";
//					 List<Object[]> kpigatherResultList = o_kpiGatherResultDAO.createSQLQuery(kpigatherResultQuery).setParameterList("kpiIdList", kpiCodeList, new StringType()).list();
//					 final Map<String,Object> gatherResultMap = new HashMap<String,Object>();
//					 for(Object[] o:kpigatherResultList) {
//						String kpiId  = (String) o[0];
//						String timePeriodId  = (String) o[1];
//						if(null != o[2]) {
//							Double value  = Double.valueOf(String.valueOf(o[2]));
//							gatherResultMap.put(kpiId+timePeriodId, value);
//						} 
//					 }
//					 final Map<String, String> timePeriodPreMap =  o_timePeriodBO.findTimePeriodAndPre("PRE");
//					 final Map<String, String> timePeriodCurMap =  o_timePeriodBO.findTimePeriodAndPre("CUR");
//					 o_kpiGatherResultDAO.getSession().doWork(new Work() {
//
//						@Override
//						public void execute(Connection connection)
//								throws SQLException {
//							 connection.setAutoCommit(false);
//							PreparedStatement pst = null;
//							String sql = "update t_kpi_kpi_gather_result" +
//									"set " +
//									"    direction = ? " +
//									"where kpi_id = ? and " +
//									"      time_period_id = ?";
//							// ENDO Auto-generated method stub
//							pst = connection.prepareStatement(sql);
//							 for (int row = 2; row < excelDatas.size(); row++) {
//								 List<String> rowDatas = (List<String>) excelDatas.get(row);
//							     String kpiId =  kpiTypeIdMap.get(rowDatas.get(0));
//							     String timePeriod = rowDatas.get(7);
//							     String nextTimePeriod = timePeriodPreMap.get(timePeriod);
//							     String preTimePeriod = timePeriodCurMap.get(timePeriod);
//							     Double preValue = null;
//							     Double nextValue = null;
//							     Double value = null;
//							     if(gatherResultMap.containsKey(kpiId+preTimePeriod)) {
//							    	preValue = (Double) gatherResultMap.get(kpiId+preTimePeriod);
//							     }
//							     
//							     nextValue = (Double) gatherResultMap.get(kpiId+nextTimePeriod);
//							     if(StringUtils.isNotBlank(rowDatas.get(6))) {
//							    	 value = Double.parseDouble(rowDatas.get(6)); 
//							     }
//							    
//							     pst.setString(1, getTrend(preValue, value));
//							     pst.setString(2, kpiId);
//							     pst.setString(3, timePeriod);
//							     pst.addBatch();
//							     pst.setString(1, getTrend(value, nextValue));
//							     pst.setString(2, kpiId);
//							     pst.setString(3, nextTimePeriod);
//							 }
//							 pst.executeBatch();
//					         connection.commit();
//					         connection.setAutoCommit(true);
//							
//						}
//						 
//					 });
				 }
				 
		 }
		 return flag;
	}

	/**
	 * 验证字符串是否位数字
	 * @param ss 判断的字符串
	 * @return 验证结果
	 */
	public Boolean isNumber(String ss) {
		if(StringUtils.isBlank(ss)) {
			return true;
		} else {
			return NumberUtils.isNumber(ss);
		}
	}
	
	/**
	 * 验证传入值是否与告警区间匹配
	 * @param map 告警区间映射
	 * @param value 值
	 * @return 匹配结果
	 */
	public Boolean validateRegionInfo(Map<String, String> map,String value) {
		 String maxValueStr = map.get("maxValue");
         String minValueStr = map.get("minValue");
         String maxSign = map.get("isContainMax");
         String minSign = map.get("isContainMin");
         Double finishValue =  Double.valueOf(value);
         if (!Contents.POSITIVE_INFINITY.equals(maxValueStr) && !Contents.NEGATIVE_INFINITY.equals(minValueStr)) {
             Double maxValue = Double.valueOf(maxValueStr);
             Double minValue = Double.valueOf(minValueStr);
             if("<=".equals(minSign)&&"<".equals(maxSign)){
                 if (finishValue >= minValue && finishValue < maxValue) {
                     return true;
                 }
             }else if("<=".equals(minSign)&&"<=".equals(maxSign)){
                 if (finishValue >= minValue && finishValue <= maxValue) {
                	 return true;
                 }
             }else if("<".equals(minSign)&&"<=".equals(maxSign)){
                 if (finishValue > minValue && finishValue <= maxValue) {
                	 return true;
                 }
             }else if("<".equals(minSign)&&"<".equals(maxSign)){
                 if (finishValue > minValue && finishValue < maxValue) {
                	 return true;
                 }
             }             
         }
         else {
             if (Contents.POSITIVE_INFINITY.equals(maxValueStr)) {
                 Double minValue = Double.valueOf(minValueStr);
                 if("<".equals(minSign)){
                 	if (finishValue > Double.valueOf(minValue)) {
                 		return true;
                     }
                 }else if("<=".equals(minSign)){
                 	if (finishValue >= Double.valueOf(minValue)) {
                 		return true;
                     }
                 }
                 
             }
             else if (Contents.NEGATIVE_INFINITY.equals(minValueStr)) {
                 Double maxValue = Double.valueOf(maxValueStr);
                 if("<=".equals(maxSign)){
                 	if (finishValue <= maxValue) {
                 		return true;
                     }
                 }else if("<".equals(maxSign)){
                 	if (finishValue < maxValue) {
                 		return true;
                     }
                 }
             }
         }
         return false;
     }
	
	/**
	 * 验证指标的部门人员信息是否合法
	 * @param deptName 部门名称
	 * @param empName  人员名称
	 * @param orgMap   部门名称/Id 映射
	 * @param empMap   人员名称/Id 映射
	 * @return 验证结果
	 */
	public Map<String, Object> validateDeptEmpInfo(String deptName,String empName,Map<String, String> orgMap,Map<String, String> empMap) {

		Map<String, Object> map = new HashMap<String,Object>();
		String companyId = UserContext.getUser().getCompanyid();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		if(StringUtils.isBlank(deptName) && StringUtils.isBlank(empName)) {
			return map;
		}
		if(StringUtils.isBlank(deptName) && StringUtils.isNotBlank(empName)) {
			map.put("error", "DEPT_ERROR");
            return map;
		}
		if(orgMap.containsKey(deptName)) {
			jsonObject.put("deptid", orgMap.get(deptName));
			if(StringUtils.isNotBlank(empName)) {
				if(empMap.containsKey(empName)) {
					if(o_empOrgBO.isEmpOrgBySome(empName, deptName, companyId)) {
						jsonObject.put("empid", empMap.get(empName));
					} else {
						map.put("error", "EMP_ERROR");
						return map;
					}				
				} else {
					map.put("error", "EMP_ERROR");
					return map;
				}
			} else {				
				jsonObject.put("empid", "");
			}
		} else {
			map.put("error", "DEPT_ERROR");
			return map;
		}
		jsonArray.add(jsonObject);
		map.put("empInfo", jsonArray.toString());
		return map;
	}
	
	
	/**
	 * 获得数值趋势
	 * @param preAssvalue 前期值
	 * @param assvalue 当期值
	 * @return 趋势字符串
	 */
	public String getTrend(Double preAssvalue,Double assvalue) {
		   if(null != preAssvalue && null != assvalue) {
			   if (preAssvalue.compareTo(assvalue) < 0) {
                  return "up";
               }
               else if (preAssvalue.compareTo(assvalue) == 0) {
                   return "flat";
               }
               else {
                   return "down";
               }
		   }
		   return null;
	}
	
	/**
	 * 采集频率数据库中的值
	 * @param id 频率中文名
	 * @return 采集频率数据库中的值
	 */
	public String convertFrequece(String id) {
		if(StringUtils.isNotBlank(id)) {
			if("年".equals(id)) {
			   return "0frequecy_year";
			  }
			if("月".equals(id)) {
			   return "0frequecy_month";
			  }
			if("周".equals(id)) {
			   return "0frequecy_week";
			  }
			if("季度".equals(id)) {
			   return "0frequecy_quarter";
			  }
			if("半年".equals(id)){
			    return "0frequecy_halfyear";
			}
		}
		return null;
	}
}
