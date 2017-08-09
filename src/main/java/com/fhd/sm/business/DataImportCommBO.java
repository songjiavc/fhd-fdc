package com.fhd.sm.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.CategoryBO;
import com.fhd.entity.comm.Category;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiTmp;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

/**
 * 监控预警导入公用BO
 *
 */
@Service
public class DataImportCommBO {
	@Autowired
	private StrategyMapBO o_strategyMapBO;
	@Autowired
	private CategoryBO o_categoryBO;
	@Autowired
	private KpiBO o_kpiBO;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	
	
	/**根据目标名称查询目标Id
	 * @param smName 目标名称
	 * @param smList 目标对象集合
	 * @return
	 */
	public String findStrategyIdByName(String smName,List<StrategyMap> smList){
		String smId = null;
		for (StrategyMap strategyMap : smList) {
			if(strategyMap.getName().equals(smName)){
				smId = strategyMap.getId();
				break;
			}
		}
		return smId;
	}
	
	/**根据人员或机构名称，查找人员或机构ID
	 * @param name 人员或机构名称
	 * @param orgIdMap 人员或机构map
	 * @return
	 */
	public String findEmpOrgIdByName(String name,Map<String,String> orgIdMap){
		String orgName = null;
		if(orgIdMap.containsKey(name)){
			orgName = orgIdMap.get(name);
		}
		return orgName;
	}
	
	/**根据数据字典名称的集合(用,分割)得到字典id的集合(用,好分割)
	 * @param name数据字典名称
	 * @param dictList数据字典列表
	 * @return
	 */
	public String findMutilOptionDictIdByName(String name,List<DictEntry> dictList){
		StringBuffer nameSb = new StringBuffer();
		String[] chartTypes = StringUtils.split(name,",");
		for (String ctype : chartTypes) {
			String ctypeId = findDictIdByDictName(ctype, dictList);
			nameSb.append(ctypeId).append(",");
		}
		if(nameSb.length()>0){
			return nameSb.substring(0, nameSb.length()-1);
		}
		return null;
	}
	
	/**根据字典名称查询字典Id
	 * @param name字典名称
	 * @param dictList数据字典列表
	 * @return
	 */
	public String findDictIdByDictName(String name,List<DictEntry> dictList){
		String dictId = null;
		for (DictEntry dictEntry : dictList) {
			if(dictEntry.getName().equals(name)){
				dictId = dictEntry.getId();
			}
		}
		return dictId;
	}
	/**根据字典名称查询字典Id
	 * @param name 字典名称
	 * @return
	 */
	public String findDictIdByName(String name,Map<String,String> dictMap){
		String dictId = null;
		if(dictMap.containsKey(name)){
			dictId = dictMap.get(name);
		}
		return dictId;
	}
	
	/**根据指标名称查找指标ID
	 * @param name 指标名称
	 * @param kpiList 指标对象列表
	 * @return
	 */
	public String findKpiIdByName(String name,List<Kpi> kpiList){
		String kpiId = "";
		for (Kpi kpi : kpiList) {
			if(name.equals(kpi.getName())){
				kpiId = kpi.getId();
			}
		}
		return kpiId;
	}
	/**根据指标编号查找指标ID
	 * @param code 指标编号
	 * @param kpiList 指标对象列表
	 * @return
	 */
	public String findKpiIdByCode(String code,List<Kpi> kpiList){
	    String kpiId = "";
	    for (Kpi kpi : kpiList) {
	        if(code.equals(kpi.getCode())){
	            kpiId = kpi.getId();
	        }
	    }
	    return kpiId;
	}
	
	/**根据指标名称查找指标ID
     * @param name 指标名称
     * @param kpiList 指标对象列表
     * @return
     */
	public String findTmpKpiIdByName(String name,List<KpiTmp> kpiList){
	    String kpiId = "";
	    for (KpiTmp kpi : kpiList) {
	        if(name.equals(kpi.getName())){
	            kpiId = kpi.getId();
	        }
	    }
	    return kpiId;
	}
	/**根据指标编号查找指标ID
	 * @param code 指标编号
	 * @param kpiList指标对象列表
	 * @return
	 */
	public String findTmpKpiIdByCode(String code,List<KpiTmp> kpiList){
	    String kpiId = "";
	    for (KpiTmp kpi : kpiList) {
	        if(code.equals(kpi.getCode())){
	            kpiId = kpi.getId();
	        }
	    }
	    return kpiId;
	}
	
	/**
	 *  查询所有预警方案
	 *  @param name 名称
	 *  @param type 预警 :0alarm_type_kpi_alarm , 告警:0alarm_type_kpi_forecast
	 */
	public String findAlarmPlanbyName(String name,Map<String, String> alarmMap ) {
		if(alarmMap.containsKey(name)) {
			return alarmMap.get(name);
		}
		return null;
	}
	
	/**
	 * 查询指标的编号集合
	 * @param type KC,KPI
	 * @return  当前指标的编号集合
	 */
	public Map<String, String> findAllKpiCodeList(String type){
		return o_kpiBO.findAllKpiCodeList(type);
	}
	
	
	/**查找公司下战略目标编码集合
	 * @param smList公司下战略目标集合
	 * @return
	 */
	public List<String> findSmCodeList(List<StrategyMap> smList ){
		List<String> codeList = new ArrayList<String>();
		for (StrategyMap strategyMap : smList) {
			if(StringUtils.isNotBlank(strategyMap.getCode())){
				codeList.add(strategyMap.getCode());
			}
		}
		return codeList;
	}
	/**查找公司下记分卡编码集合
	 * @param scList公司下记分卡集合
	 * @return
	 */
	public List<String> findScCodeList(List<Category> scList ){
		List<String> codeList = new ArrayList<String>();
		for (Category item : scList) {
			if(StringUtils.isNotBlank(item.getCode())){
				codeList.add(item.getCode());
			}
		}
		return codeList;
	}
	
	/**查找公司下战略目标名称集合
	 * @param smList公司下战略目标集合
	 * @return
	 */
	public List<String> findSmNameList(List<StrategyMap> smList ){
		List<String> nameList = new ArrayList<String>();
		for (StrategyMap strategyMap : smList) {
			if(StringUtils.isNotBlank(strategyMap.getName())){
				nameList.add(strategyMap.getName());
			}
		}
		return nameList;
	}
	
	/**查找公司下战略目标集合
	 * @param companyId公司id
	 * @return
	 */
	public List<StrategyMap> findStrategyMapAllByCompanyId(String companyId){
		return o_strategyMapBO.findStrategyMapAllByCompanyId(companyId);
	}
	
	/**查找公司下记分卡集合
	 * @param companyId公司id
	 * @return
	 */
	public List<Category> findCategoryAllByCompanyId(String companyId){
		return o_categoryBO.findCategoryAllByCompanyId(companyId);
	}
	
	/**根据公司ID查询部门名称和部门id的map集合
	 * @param companyId公司id
	 * @return
	 */
	public Map<String,String> findOrgIdMap(String companyId){
		List<SysOrganization> organizationList = o_organizationBO.findByCompanyId (companyId);
		Map<String,String> organizationMap = new HashMap<String, String>();
		for (SysOrganization organization : organizationList) {
		organizationMap.put(organization.getOrgname(), organization.getId());
		}
		return organizationMap;
	}
	/**根据公司ID查询员工名称和员工id的map集合
	 * @param companyId公司ID
	 * @return
	 */
	public Map<String,String> findEmpIdMap(String companyId){
		List<SysEmployee> employeeList = o_employeeBO.findByCompanyId(companyId);
		Map<String,String> employeeMap = new HashMap<String, String>();
		for (SysEmployee employee : employeeList) {
			employeeMap.put(employee.getEmpname(), employee.getId());
		}
		return employeeMap;
	}
}
