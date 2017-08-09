package com.fhd.sm.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.core.utils.Identities;
import com.fhd.dao.comm.AlarmPlanDAO;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.kpi.KpiRelaAlarmDAO;
import com.fhd.dao.kpi.KpiTmpDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiRelaAlarm;
import com.fhd.entity.kpi.KpiTmp;
import com.fhd.fdc.commons.security.OperatorDetails;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.web.form.KpiTmpForm;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpOrgBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;
@Service
public class KpiTypeDataImportBO {
	@Autowired
	private KpiTmpDAO  o_kpiTmpDAO;
	@Autowired
	private KpiBO o_kpiBO;
	@Autowired
	private KpiDAO o_kpiDao;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
    @Autowired
    private KpiRelaAlarmDAO o_kpiRelaAlarmDAO;
    @Autowired
    private AlarmPlanDAO o_alarmPlanDAO;
	@Autowired
	private DataImportCommBO o_dataImportCommBO;
    @Autowired
    private EmpOrgBO o_empOrgBO;
    @Autowired
    private KpiDataImportBO o_kpiDataImportBO;
	
    /**根据类型查询所有的code和名称的对应关系
     * @param type代办是指标类型,还是指标
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findAllKpiCodeList(String type){
        Map<String, String> map = new HashMap<String, String>();
        Criteria criteria = o_kpiTmpDAO.createCriteria();      
        //criteria.setCacheable(true);
        String companyId = "";
        OperatorDetails userDetails = UserContext.getUser();// 所在公司id
        if(null!=userDetails){
            companyId = userDetails.getCompanyid();
        }
        if(StringUtils.isNotBlank(companyId)){
            criteria.add(Restrictions.eq("company", companyId));
        }
        if(StringUtils.isNotBlank(type)) {
            criteria.add(Restrictions.eq("isKpiCategory", type));
        }
        List<KpiTmp> kpiList = criteria.list();
        for(KpiTmp kpi:kpiList) {
            if(StringUtils.isNotBlank(kpi.getCode()))
            {
                map.put(kpi.getCode(), kpi.getName());  
            }
        }
        return map;
    }
    
    /**根据类型查询所有的code和id的对应关系
     * @param type代办是指标类型,还是指标
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findAllKpiTypeIdMap(String type){
        Map<String, String> map = new HashMap<String, String>();
        Criteria criteria = o_kpiTmpDAO.createCriteria();      
        //criteria.setCacheable(true);
        String companyId = "";
        OperatorDetails userDetails = UserContext.getUser();// 所在公司id
        if(null!=userDetails){
            companyId = userDetails.getCompanyid();
        }
        if(StringUtils.isNotBlank(companyId)){
            
            criteria.add(Restrictions.eq("company", companyId));
        }
        if(StringUtils.isNotBlank(type)) {
            criteria.add(Restrictions.eq("isKpiCategory", type));
        }
        List<KpiTmp> kpiList = criteria.list();
        for(KpiTmp kpi:kpiList) {
            if(StringUtils.isNotBlank(kpi.getCode()))
            { 
                map.put(kpi.getCode(), kpi.getId());    
            }           
        }
        return map;
    }
    
    
	/**
	 * excel中的数据导入临时表
	 * @param excelDatas excel数据
	 * @param kpiType 代表是指标类型还是指标
	 * @throws ParseException
	 */
	@Transactional
	public Boolean saveKpiDataFromExcel(List<List<String>> excelDatas,String kpiType,Boolean addStyle) throws ParseException {
		//清空临时表数据
		String sql = "delete from tmp_imp_kpi_kpi where IS_KPI_CATEGORY = 'KC'";
		SQLQuery sqlQuery = o_kpiTmpDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
        Boolean flag = true;
		//部门map
		Map<String,String> orgIdMap = o_dataImportCommBO.findOrgIdMap(UserContext.getUser().getCompanyid());
		//人员map
		Map<String,String> empIdMap = o_dataImportCommBO.findEmpIdMap(UserContext.getUser().getCompanyid());
		List<KpiTmp> kpiTmpList = new ArrayList<KpiTmp>();
		List<Kpi> kpiTypeList = o_kpiBO.findKpiTypeAll("","id","ASC");
		// 保存已经导入的指标编号
		List<String> kpiCodeList = new ArrayList<String>();
		// 保存已经导入的指标名称
		List<String> kpiNameList = new ArrayList<String>();
		if (excelDatas != null && excelDatas.size() > 0) {
			
			// 依次读取EXCEL第三行以后的数据（前两行为标题与说明）
			for (int row = 2; row < excelDatas.size(); row++) {
				KpiTmp kpiTmp = new KpiTmp();
				kpiTmp.setIsKpiCategory(Contents.KC_TYPE);
				// 验证信息汇总字符串
				StringBuffer validateInfo = new StringBuffer();
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				
				// 验证指标编号并保存
				String kpiCode = rowDatas.get(0);
				if(kpiCodeList.contains(kpiCode)) {
					validateInfo.append("编号重复,");
				} else {
					if(StringUtils.isNotBlank(o_dataImportCommBO.findKpiIdByCode(kpiCode,kpiTypeList))) {
						validateInfo.append("编号重复,");
					} 
						kpiCodeList.add(kpiCode);
				}
				kpiTmp.setCode(kpiCode);
				
				//指标名称验证
				String kpiName = rowDatas.get(1);
				if(StringUtils.isBlank(kpiName)) {
					validateInfo.append("名称为空,");
				} else {
					if(kpiNameList.contains(kpiName)){
						validateInfo.append("名称重复,");
					} else {
						if(!addStyle) {
							if(StringUtils.isNotBlank(o_dataImportCommBO.findKpiIdByName(kpiName,kpiTypeList))) {
								validateInfo.append("名称重复,");
							}
						}
						kpiNameList.add(kpiName);
					}
				}
				kpiTmp.setName(kpiName);
				
				// 说明
				kpiTmp.setDesc(rowDatas.get(2));
				
				// 短名称
				kpiTmp.setShortName(rowDatas.get(3));
                
				// 所属部门名称人员
				String belongDept = rowDatas.get(4);
				String belongEmp = rowDatas.get(5);
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
				String collectDept = rowDatas.get(6);
				String collectEmp = rowDatas.get(7);
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
				String targetDept = rowDatas.get(8);
				String targetEmp = rowDatas.get(9);
				deptEmpMap = validateDeptEmpInfo(targetDept,targetEmp,orgIdMap,empIdMap);
				if(deptEmpMap.containsKey("error")) {
					if("DEPT_ERROR".equalsIgnoreCase((String) deptEmpMap.get("error"))) {
						validateInfo.append("报告部门填写错误,");
					} else {
						validateInfo.append("报告人员填写错误,");
					}
				}
				kpiTmp.setTargetDept((String) deptEmpMap.get("empInfo"));
				// 报告部门
				String reportDept = rowDatas.get(10);
				String reportEmp = rowDatas.get(11);
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
				String checkDept = rowDatas.get(12);
				String checkEmp = rowDatas.get(13);
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
				kpiTmp.setStatus(o_dictBO.findDictEntryByName(rowDatas.get(14)));
				
				// 是否监控
				if("是".equals(rowDatas.get(15))) {
					kpiTmp.setIsMonitor(true);
				} else {
					kpiTmp.setIsMonitor(false);
				}
				
				// 单位
				kpiTmp.setUnits(o_dictBO.findDictEntryByName(rowDatas.get(16)));
				
				// 开始日期
				if(StringUtils.isBlank(rowDatas.get(17))) {
					validateInfo.append("开始日期为空,");
					kpiTmp.setStartDate(null);
				} else {
				    boolean startDateFlag = true;
			        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");			       
			        Date startDate = null;
			        try {
			            startDate = dataFormat.parse(rowDatas.get(17));
                    }
                    catch (Exception e) {
                        startDateFlag = false;
                    }
					kpiTmp.setStartDate(startDate);
					if(!startDateFlag){
					    validateInfo.append("开始日期设置错误,");
					}
				}
				
				// 指标方向性
				kpiTmp.setType(o_dictBO.findDictEntryByName(rowDatas.get(18)));
				
				// 指标性质
				kpiTmp.setKpiType(o_dictBO.findDictEntryByName(rowDatas.get(19)));
				
				// 亮灯依据
				kpiTmp.setAlarmMeasure(o_dictBO.findDictEntryByName(rowDatas.get(20)));
				
				// 预警依据
				kpiTmp.setAlarmBasis(o_dictBO.findDictEntryByName(rowDatas.get(21)));
				
				// 主维度
				kpiTmp.setMainDim(o_dictBO.findDictEntryByName(rowDatas.get(22)));
				
				// 辅助维度
				StringBuffer supportDim = new StringBuffer();
				if(StringUtils.isNotBlank(rowDatas.get(23))) {
					for(String dimName: rowDatas.get(23).split(",")) {
						supportDim.append(o_dictBO.findDictEntryByName(dimName)).append(",");
					}
				}
				kpiTmp.setSupportDim(supportDim.toString());
				
				// 目标值别名
				kpiTmp.setTargetValueAlias(rowDatas.get(24));
				
				// 实际值别名
				kpiTmp.setResultValueAlias(rowDatas.get(25));
				
				// 结果值公式
				if(StringUtils.isNotBlank(rowDatas.get(26))) {
			       kpiTmp.setResultFormula(rowDatas.get(26));
			       kpiTmp.setIsResultFormula("0sys_use_formular_formula");
				} else {
					kpiTmp.setIsResultFormula("0sys_use_formular_manual");
				}
				// 目标值公式
				if(StringUtils.isNotBlank(rowDatas.get(27))) {
					 kpiTmp.setTargetFormula(rowDatas.get(27));
					 kpiTmp.setIsTargetFormula("0sys_use_formular_formula");
				} else {
					kpiTmp.setIsTargetFormula("0sys_use_formular_manual");
				}
				// 评估值公式
				if(StringUtils.isNotBlank(rowDatas.get(28))) {
					kpiTmp.setIsAssessmentFormula("0sys_use_formular_formula");
					kpiTmp.setAssessmentFormula(rowDatas.get(28));
				} else {
					kpiTmp.setAssessmentFormula("0sys_use_formular_manual");
				}
				//预警公式
				if(StringUtils.isNotBlank(rowDatas.get(29))) {
					kpiTmp.setForecastFormula(rowDatas.get(29));
				}
				// 标杆值
				if(StringUtils.isNotBlank(rowDatas.get(31))){
				    if(NumberUtils.isNumber(rowDatas.get(31))){
				        kpiTmp.setModelValue(Double.parseDouble(rowDatas.get(31)));
				    }
				    else{
				        validateInfo.append("标杆值设置错误,");
				    }
				}
				// 结果值累计计算
				if(StringUtils.isNotBlank(rowDatas.get(32))) {
					kpiTmp.setResultSumMeasure(o_dictBO.findDictEntryByName(rowDatas.get(32)));
				}
				// 目标值累计计算
				if(StringUtils.isNotBlank(rowDatas.get(33))) {
					kpiTmp.setTargetSumMeasure(o_dictBO.findDictEntryByName(rowDatas.get(33)));
				}
				// 评估值累计计算
				if(StringUtils.isNotBlank(rowDatas.get(34))) {
					kpiTmp.setAssessmentSumMeasure(o_dictBO.findDictEntryByName(rowDatas.get(34)));
				}
				// 结果收集频率显示（中文）
				if(StringUtils.isNotBlank(rowDatas.get(35))) {
					kpiTmp.setGatherDayFormulrShow(rowDatas.get(35));
				}
				// 结果值收集日期设置
				if(StringUtils.isNotBlank(rowDatas.get(36))) {
					kpiTmp.setGatherDayFormulr(rowDatas.get(36));
				}
				// 结果收集日期频率
				kpiTmp.setGatherFrequence(convertFrequece(rowDatas.get(37)));
				// 结果收集延期天
				if(StringUtils.isNotBlank(rowDatas.get(38))) {
					kpiTmp.setResultCollectInterval(Integer.parseInt(rowDatas.get(38)));
				}
				// 结果收集报告日期设置
				if(StringUtils.isNotBlank(rowDatas.get(39))) {
					kpiTmp.setGatherReportDayFormulrShow(rowDatas.get(39));
				}
				// 结果收集报告频率
				if(StringUtils.isNotBlank(rowDatas.get(40))) {
					kpiTmp.setGatherReportDayFormulr(rowDatas.get(40));
				}
				// 结果收集日期频率
				kpiTmp.setReportFrequence(convertFrequece(rowDatas.get(41)));
				// 目标收集频率
				if(StringUtils.isNotBlank(rowDatas.get(43))) {
					kpiTmp.setTargetSetDayFormular(rowDatas.get(43));
				}
				// 目标收集频率设置
				if(StringUtils.isNotBlank(rowDatas.get(42))) {
					kpiTmp.setTargetSetDayFormularShow(rowDatas.get(42));					
				}
				// 目标收集日期频率
				kpiTmp.setTargetSetFrequence(convertFrequece(rowDatas.get(44)));
				// 目标收集延期天数
				if(StringUtils.isNotBlank(rowDatas.get(45))) {
					kpiTmp.setTargetSetInterval(Integer.parseInt(rowDatas.get(45)));
				}
				// 目标收集报告频率
				if(StringUtils.isNotBlank(rowDatas.get(46))) {
					kpiTmp.setTargetReportDayFormulrShow(rowDatas.get(46));
				}
				// 目标收集报告频率设置
				if(StringUtils.isNotBlank(rowDatas.get(47))) {
					kpiTmp.setTargetSetReportDayFormulr(rowDatas.get(47));
				}
				// 目标收集报告日期频率
				kpiTmp.setTargetSetReportFrequence(convertFrequece(rowDatas.get(48)));
				
				// 报表小数点位置
				if(StringUtils.isNotBlank(rowDatas.get(49))) {
					kpiTmp.setScale(Integer.parseInt(rowDatas.get(49)));
				}
				// 趋势
				if(StringUtils.isNotBlank(rowDatas.get(50))) {
					kpiTmp.setRelativeTo(o_dictBO.findDictEntryByName(rowDatas.get(50)));
				}
				// 方案生效日期
				if(StringUtils.isNotBlank(rowDatas.get(51))){
				    boolean efDateFlag = true;
					DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");					
					Date efDate = null;
					try {
                        efDate = fmt.parse(rowDatas.get(51));
                    }
                    catch (Exception e) {
                        efDateFlag = false;
                    }
					kpiTmp.setWarningEffDate(efDate);
					if(!efDateFlag){
					    validateInfo.append("方案日期设置错误,");
					}
				}
				// 告警方案
				if(StringUtils.isNotBlank(rowDatas.get(52))){
				AlarmPlan forecaseAlarmPlan = 	o_alarmPlanBO.findAlarmPlanByNameType(rowDatas.get(52),"0alarm_type_kpi_forecast");
				if(null != forecaseAlarmPlan) {
					kpiTmp.setWarningSet(forecaseAlarmPlan.getId());
				}else {
					validateInfo.append("告警方案不存在,");
				}
	
				}
				// 预警方案
				if(StringUtils.isNotBlank(rowDatas.get(53))) {
					AlarmPlan warnAlarmPlan = 	o_alarmPlanBO.findAlarmPlanByNameType(rowDatas.get(53),"0alarm_type_kpi_alarm");
					if(null != warnAlarmPlan) {
						kpiTmp.setForeWarningSet(warnAlarmPlan.getId());
					} else {
						validateInfo.append("预警方案不存在,");
					}
				}
				// 最大值 
				if(StringUtils.isNotBlank(rowDatas.get(54))) {
				    if(NumberUtils.isNumber(rowDatas.get(54))){
				        kpiTmp.setMaxValue(Double.parseDouble(rowDatas.get(54)));
				    }else{
				        validateInfo.append("最大值设置错误,");
				    }
				}				
				// 最小值
				if(StringUtils.isNotBlank(rowDatas.get(55))) {
				    if(NumberUtils.isNumber(rowDatas.get(55))){
				        kpiTmp.setMinValue(Double.parseDouble(rowDatas.get(55)));
				    }
				    else{
				        validateInfo.append("最小值设置错误,");
				    }
				}
				
				kpiTmp.setCompany(UserContext.getUser().getCompanyid());
				// 行号
				kpiTmp.setRowNum(String.valueOf(row+1));
				kpiTmp.setCalc(Contents.DICT_Y);
				if(validateInfo.length() >0) {
					kpiTmp.setValidateInfoString(validateInfo.toString().substring(0, validateInfo.length() -1));
					flag = false;
				}
				kpiTmp.setId(Identities.uuid());
				kpiTmp.setDeleteStatus(true);
				//o_kpiTmpDAO.merge(kpiTmp);
				
				
				kpiTmp.setIsLeaf(true);
				kpiTmp.setIsInherit(false);
				kpiTmp.setIsNameDefault(false);
				kpiTmpList.add(kpiTmp);
				
			}
			if(kpiTmpList.size()>0){
                o_kpiDataImportBO.batchImportKpiTmp(kpiTmpList);
            }
		}
		return flag;		
	}
	/**
	 * 校验部门和人员是否匹配
	 * @param deptName部门名称
	 * @param empName人员名称
	 * @return
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
     * 根据类型查询临时表中指标类型或是指标
     * @return
     */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findAllKpiTmp(String type) {
		  Criteria criteria = o_kpiTmpDAO.createCriteria();
		  if(StringUtils.isNotBlank(type)) {
			  criteria.add(Restrictions.eq("isKpiCategory", type));  
		  }
		  //criteria.add(Restrictions.isNotNull("validateInfo"));
		  criteria.addOrder(Order.desc("validateInfo"));
		  criteria.addOrder(Order.asc("rowNum"));
		  List<KpiTmp> kpiTmpList = criteria.list();
		  List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		  for(KpiTmp kpiTmp : kpiTmpList) {
			  Map<String, Object> map = new HashMap<String, Object>();
			  map.put("id", kpiTmp.getId());
			  map.put("kpiName", kpiTmp.getName());
			  map.put("code", kpiTmp.getCode());
			  map.put("validateInfo", kpiTmp.getValidateInfo());
			  map.put("rowNum", kpiTmp.getRowNum());
			  list.add(map);
		  }
		  return list;
	}
	/**
	 * 根据id查询临时表中的数据
	 * @param id 临时表中指标id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public KpiTmp findKpiTmpById(String id) {
		if(StringUtils.isNotBlank(id)) {
			Criteria criteria = o_kpiTmpDAO.createCriteria();
			criteria.add(Restrictions.eq("id", id));
			List<KpiTmp> list = criteria.list();
			if(list.size() > 0) {
				return list.get(0);
			
			}
			return null;
		}
		return null;
	}
	/**
	 * 
	 * @param form
	 * @return
	 */
	public String validateEditForm(KpiTmp form) {
		StringBuffer validateInfo = new StringBuffer();
		if(validateInfo.length() > 0) {
		   return validateInfo.toString();
		}
		return null;
	}
	
	/**
	 * 
	 * @param form
	 */
	@Transactional
	public void mergeKpiForm(KpiTmpForm form) {
		KpiTmp kpiTmp = new KpiTmp();
	    BeanUtils.copyProperties(form, kpiTmp);
	    o_kpiTmpDAO.merge(kpiTmp);
	}
	
	/**
	 * 
	 * @param id
	 * @return 采集频率数据库中的值
	 */
	public String convertFrequece(String id) {
		String frequecy = "";
		if(StringUtils.isNotBlank(id)) {
			if("年".equals(id)){
				frequecy = "0frequecy_year";
			}
			else if("月".equals(id)){
				frequecy = "0frequecy_month";
			}
			else if("周".equals(id)) {
				frequecy = "0frequecy_week";
			}
			else if("季度".equals(id)) {
				frequecy = "0frequecy_quarter";
			}
		}
		return frequecy;
	}
	
	/**
	 *  插入临时表数据
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void mergeKpiTmpKpiType(Boolean addStyle) throws ParseException {
		// 查找校验无误的临时表数据
		Criteria criteria = o_kpiTmpDAO.createCriteria();
		criteria.add(Restrictions.isNull("validateInfo"));
		criteria.addOrder(Order.asc("sort"));
		criteria.addOrder(Order.asc("code"));
		criteria.add(Restrictions.eq("isKpiCategory", "KC"));
		List<KpiTmp> list = criteria.list();
		if(list.size() > 0) {
			if(addStyle) {
				String companyId = UserContext.getUser().getCompanyid();
				// 初始化数据库中的数据
				String kpiRelaDimDel = "delete from t_kpi_kpi_rela_dim where kpi_id in " +
						"(select id from t_kpi_kpi where IS_KPI_CATEGORY = 'KC'" + 
						" and COMPANY_ID =  " + "'" + companyId + "')";
				String kpiRelaOrgDel =  "delete from t_kpi_kpi_rela_org_emp where kpi_id in " +
						"(select id from t_kpi_kpi where IS_KPI_CATEGORY = 'KC'" + 
						" and COMPANY_ID =  " + "'" + companyId + "')";
				String kpiRelaAlarmDel = "delete from t_kpi_kpi_rela_alarm where kpi_id in " +
						"(select id from t_kpi_kpi where IS_KPI_CATEGORY = 'KC'" + 
						" and COMPANY_ID =  " + "'" + companyId + "')";
				String kpiDel = "delete from t_kpi_kpi where IS_KPI_CATEGORY = 'KC'" + 
						" and COMPANY_ID =  " + "'" + companyId + "'";
				String kpiUpdate = "update t_kpi_kpi set BELONG_KPI_CATEGORY = null,IS_INHERIT = 0 where IS_KPI_CATEGORY = 'KPI' and COMPANY_ID =  " + "'" + companyId + "'";
				o_kpiDao.createSQLQuery(kpiRelaDimDel).executeUpdate();
				o_kpiDao.createSQLQuery(kpiRelaOrgDel).executeUpdate();
				o_kpiDao.createSQLQuery(kpiRelaAlarmDel).executeUpdate();
				o_kpiDao.createSQLQuery(kpiDel).executeUpdate();
				o_kpiDao.createSQLQuery(kpiUpdate).executeUpdate();
			}
			o_kpiDataImportBO.mergeKpiTmpKpi(addStyle, Contents.KC_TYPE);
 			/*for(KpiTmp kpiTmp: list) {
				Kpi	 kpi = new Kpi();
				//String id = kpiTmp.getCompany() + kpiTmp.getId();
				String id =  kpiTmp.getId();
				// 设置新的ID
				kpi.setId(id);
	             // KPI:指标，KC:指标类型
				kpi.setIsKpiCategory(Contents.KC_TYPE);
				// id全路径
				kpi.setIdSeq("."+kpi.getId());
				// 是否是叶子
				kpi.setIsLeaf(true);
				// 公司
				//kpi.setCompany(o_organizationBO.findById(kpiTmp.getCompany()));
				kpi.setCompany(o_organizationBO.findById(UserContext.getUser().getCompanyid()));
				// 编号
				kpi.setCode(kpiTmp.getCode());
				// 名称
				kpi.setName(kpiTmp.getName());
				// 简称
				kpi.setShortName(kpiTmp.getShortName());
				// 说明
				kpi.setDesc(kpiTmp.getDesc());
				// 采集频率   日、周、月、季、半年、每年
				kpi.setGatherFrequence(o_dictBO.findDictEntryById(kpiTmp.getGatherFrequence()));
				// 采集时间公式
				kpi.setGatherDayFormulr(kpiTmp.getGatherDayFormulr());
				//  结果采集扩充天数
				kpi.setResultCollectInterval(kpiTmp.getResultCollectInterval());
				// 结果值累计计算
				kpi.setResultSumMeasure(o_dictBO.findDictEntryById(kpiTmp.getResultSumMeasure()));
				// 目标值累计计算
				kpi.setTargetSumMeasure(o_dictBO.findDictEntryById(kpiTmp.getTargetSumMeasure()));
				// 评估值累计计算
				kpi.setAssessmentSumMeasure(o_dictBO.findDictEntryById(kpiTmp.getAssessmentSumMeasure()));
				// 目标收集频率
				kpi.setTargetSetFrequence(o_dictBO.findDictEntryById(kpiTmp.getTargetSetFrequence()));
				// 目标设定时间公式
				kpi.setTargetSetDayFormular(kpiTmp.getTargetSetDayFormular());
				// 采集报告频率
				kpi.setReportFrequence(o_dictBO.findDictEntryById(kpiTmp.getReportFrequence()));
				// 采集报告频率公式
				kpi.setGatherReportDayFormulr(kpiTmp.getGatherReportDayFormulr());
				// 目标收集报告频率
				kpi.setTargetSetReportFrequence(o_dictBO.findDictEntryById(kpiTmp.getTargetSetReportFrequence()));
				//目标收集报告频率公式
				kpi.setTargetSetReportDayFormulr(kpiTmp.getTargetSetReportDayFormulr());
				//起始日期
				kpi.setStartDate(kpiTmp.getStartDate());
				// 目标公式定义
				kpi.setTargetFormula(kpiTmp.getTargetFormula());
				// 结果公式定义
				kpi.setResultFormula(kpiTmp.getResultFormula());
				// 关联关系公式
				kpi.setRelationFormula(kpiTmp.getRelationFormula());
				// 目标设定扩充天数
				kpi.setTargetSetInterval(kpiTmp.getTargetSetInterval());
				// 排列顺序
				kpi.setSort(kpiTmp.getSort());
				// 删除状态
				kpi.setDeleteStatus(true);
				// status
				kpi.setStatus(o_dictBO.findDictEntryById(kpiTmp.getStatus()));
				// 单位
				kpi.setUnits(o_dictBO.findDictEntryById(kpiTmp.getUnits()));
				//是否计算
				kpi.setCalc(o_dictBO.findDictEntryById(kpiTmp.getCalc()));
				//目标值别名
				kpi.setTargetValueAlias(kpiTmp.getTargetValueAlias());
				// 实际值别名
				kpi.setResultValueAlias(kpiTmp.getResultValueAlias());
				// 是否监控
				kpi.setIsMonitor(kpiTmp.getIsMonitor());
				// 监控状态
				kpi.setMonitorStatus(kpiTmp.getMonitorStatus());
				// 预警依据
				kpi.setAlarmBasis(o_dictBO.findDictEntryById(kpiTmp.getAlarmBasis()));
				// 数据类型
				kpi.setDataType(o_dictBO.findDictEntryById(kpiTmp.getDataType()));
				// 指标性质
				kpi.setKpiType(o_dictBO.findDictEntryById(kpiTmp.getKpiType()));
				//当前数据趋势相对于
				kpi.setRelativeTo(o_dictBO.findDictEntryById(kpiTmp.getRelativeTo()));
				// 评估公式
				kpi.setAssessmentFormula(kpiTmp.getAssessmentFormula());
				// 报表位置
				kpi.setScale(kpiTmp.getScale());
				//  预警值公式
				kpi.setForecastFormula(kpiTmp.getForecastFormula());
				// 亮灯依据
				kpi.setAlarmMeasure(o_dictBO.findDictEntryById(kpiTmp.getAlarmMeasure()));
				//类型   正向指标、逆向指标。。。
				kpi.setType(o_dictBO.findDictEntryById(kpiTmp.getType()));
				// 目标值是否使用公式
				kpi.setIsTargetFormula(kpiTmp.getIsTargetFormula());
				// 结果值是否使用公式
				kpi.setIsResultFormula(kpiTmp.getIsResultFormula());
				// 评估值是否使用公式
				kpi.setIsAssessmentFormula(kpiTmp.getIsAssessmentFormula());
				//标杆值
				kpi.setModelValue(kpiTmp.getModelValue());
				// 最大值
				kpi.setMaxValue(kpiTmp.getMaxValue());
				// 最小值
				kpi.setMinValue(kpiTmp.getMinValue());
				// 是否关注
				kpi.setIsFocus("0");
				// 是否计算
				kpi.setCalc(o_dictBO.findDictEntryById(kpiTmp.getCalc()));
				// 结果收集频率显示
				kpi.setGatherDayFormulrShow(kpiTmp.getGatherDayFormulrShow());
				// 目标收集频率显示
				kpi.setTargetSetDayFormularShow(kpiTmp.getTargetSetDayFormularShow());
				// 结果收集报告频率显示
				kpi.setGatherReportDayFormulrShow(kpiTmp.getGatherReportDayFormulrShow());
				// 目标收集报告频率显示
				kpi.setTargetReportDayFormulrShow(kpiTmp.getTargetReportDayFormulrShow());				
				o_kpiBO.mergeKpi(kpi);
				// 指标部门关联关系保存
		        String ownDept = kpiTmp.getOwenrDept();// 所属部门
		        String viewDept = kpiTmp.getCheckDept();// 查看部门
		        String reportDept = kpiTmp.getReportDept();// 报告部门
		        String gatherDept = kpiTmp.getCollectDept();// 采集部门
		        String targetDept = kpiTmp.getTargetDept();// 采集部门
		        // 保存所属部门信息
		        o_kpiBO.saveKpiRelaOrgEmp(kpi, ownDept, Contents.BELONGDEPARTMENT);
		        // 保存采集部门信息
		        o_kpiBO.saveKpiRelaOrgEmp(kpi, gatherDept, Contents.GATHERDEPARTMENT);
		        // 保存目标部门信息
		        o_kpiBO.saveKpiRelaOrgEmp(kpi, targetDept, Contents.TARGETDEPARTMENT);
		        // 保存查看部门信息
		        o_kpiBO.saveKpiRelaOrgEmp(kpi, viewDept, Contents.VIEWDEPARTMENT);
		        // 保存报告部门信息
		        o_kpiBO.saveKpiRelaOrgEmp(kpi, reportDept, Contents.REPORTDEPARTMENT);
		        // 保存主维度信息
		        String mainDim = kpiTmp.getMainDim();
		        o_kpiBO.saveKpiRelaDim(kpi, mainDim, Contents.MAIN);
		        // 保存辅助维度信息
		        String otherDim = kpiTmp.getSupportDim();
		        if (StringUtils.isNotBlank(otherDim)) {
		            String[] otherDims = otherDim.split(",");
		            for (String tmpstr : otherDims) {
		            	o_kpiBO.saveKpiRelaDim(kpi, tmpstr, Contents.ASSISTANT);
		            }
		        }  
		        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");	
		        String alarmId = kpiTmp.getWarningSet();
                String warningId = kpiTmp.getForeWarningSet();
                String date = dataFormat.format(kpiTmp.getWarningEffDate());
                AlarmPlan alarmPlan = null;
                AlarmPlan warningPlan = null;
                KpiRelaAlarm kpiRelaAlarm = null;
                
                 * 保存告警,预警信息
                 
                if (StringUtils.isNotBlank(alarmId)) {
                    alarmPlan = o_alarmPlanDAO.get(alarmId);
                }
                if (StringUtils.isNotBlank(warningId)) {
                    warningPlan = o_alarmPlanDAO.get(warningId);
                }
                kpiRelaAlarm = new KpiRelaAlarm();
                kpiRelaAlarm.setId(Identities.uuid());
                kpiRelaAlarm.setKpi(kpi);
                kpiRelaAlarm.setrAlarmPlan(alarmPlan);
                kpiRelaAlarm.setFcAlarmPlan(warningPlan);
                kpiRelaAlarm.setStartDate(dataFormat.parse(date));
                o_kpiRelaAlarmDAO.merge(kpiRelaAlarm);	

			}*/
		}
	}
}
