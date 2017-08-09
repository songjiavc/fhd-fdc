package com.fhd.sys.web.controller.assess;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.core.utils.Identities;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.assess.WeightSet;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.kpiset.AssessTaskBO;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.web.form.assess.quaassess.FormulaSetForm;
import com.fhd.sys.business.assess.WeightSetBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.OrgGridBO;
/**
 * 权重分配Control
 * @author 王再冉
 *
 */
@Controller
public class WeightSetControl {
	@Autowired
	private WeightSetBO o_weightSetBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	@Autowired
	private FormulaSetBO o_formulaSetBO;
	@Autowired
	private AssessTaskBO o_assessTaskBO;
	@Autowired
	private OrgGridBO o_orgGridBO;

	/**
	 * 查询部门权重分配列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/queryweightsetgrid.f")
	public List<Map<String,Object>> queryWeightSetGrid(){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		WeightSet weigSet = o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid());
		if(null == weigSet){
			SysOrganization company = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getCompanyid());
			weigSet = new WeightSet();
			weigSet.setId(Identities.uuid());
			weigSet.setCompany(company);
			weigSet.setObjectKey("5");
			weigSet.setRelatedDeptWeight("4");
			weigSet.setAssistDeptWeight("3");
			weigSet.setLeadWeight("2");
			weigSet.setStaffWeight("1");
			o_weightSetBO.saveWeightSetDept(weigSet);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", weigSet.getId());
		map.put("ZRDept", weigSet.getObjectKey());//责任部门
		map.put("XGDept", weigSet.getRelatedDeptWeight());//相关部门
		map.put("FZDept", weigSet.getAssistDeptWeight());//辅助部门
		//map.put("CYDept", weigSet.getPartakeDeptWeight());//参与部门
		listmap.add(map);
		
		return listmap;
	}
	/**
	 * 保存部门权重(更新)
	 * @param modifyRecords 对应数据记录
	 * @param id			权重id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/saveweightsetdept.f")
	public Map<String, Object> saveWeightSetDept(String modifyRecords, String id){
		Map<String, Object> map = new HashMap<String, Object>();
		JSONArray jsonArray=JSONArray.fromObject(modifyRecords);
		JSONObject jsonObj = jsonArray.getJSONObject(0);
		WeightSet weigSet = o_weightSetBO.findWeightSetById(id);
		if(null != weigSet){
			weigSet.setObjectKey(null==jsonObj.getString("ZRDept")?"":jsonObj.getString("ZRDept"));//责任部门
			weigSet.setRelatedDeptWeight(null==jsonObj.getString("XGDept")?"":jsonObj.getString("XGDept"));//相关部门
			weigSet.setAssistDeptWeight(null==jsonObj.getString("FZDept")?"":jsonObj.getString("FZDept"));//辅助部门
			//weigSet.setPartakeDeptWeight(null==jsonObj.getString("CYDept")?"":jsonObj.getString("CYDept"));//参与部门
			o_weightSetBO.saveWeightSetDept(weigSet);
		}
		map.put("success", true);
		return map;
	}
	/**
	 * 查询职务权重列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/queryweightsetgridbyduty.f")
	public List<Map<String,Object>> queryWeightSetGridByDuty(){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		WeightSet weigSet = o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid());
		HashMap<String, Double> roleWeightHash = o_weightSetBO.findRoleWeight();
		if(null == weigSet){
			SysOrganization company = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getCompanyid());
			weigSet = new WeightSet();
			weigSet.setId(Identities.uuid());
			weigSet.setCompany(company);
		}else{
			if(roleWeightHash.size()>0){
				Set<String> keySet = roleWeightHash.keySet();
				Map<String,Object> roleMap = o_weightSetBO.findSysRoleByRoleCodes(keySet);
				for(String key : keySet){
					SysRole role = (SysRole)roleMap.get(key);
					if(null != role){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("wSetId", weigSet.getId());
						map.put("roleCode", key);//员工权重
						map.put("duty", role.getRoleName());//员工权重
						map.put("weighting", null!=roleWeightHash.get(key)?roleWeightHash.get(key):"");
						listmap.add(map);
					}
				}
			}
		}
		o_weightSetBO.findRoleCodes();
		return listmap;
	}
	/**
	 * 保存职务权重
	 * @param modifyRecords	数据对应记录
	 * @param id			权重id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/saveweightsetduty.f")
	public Map<String, Object> saveWeightSetDuty(String modifyRecords, String id){
		Map<String, Object> map = new HashMap<String, Object>();
		WeightSet weiSet = o_weightSetBO.findWeightSetById(id);
		JSONArray jsonArray=JSONArray.fromObject(modifyRecords);
		int j = jsonArray.size();
		StringBuffer roleWeightStr = new StringBuffer();
		for(int i=0;i<j;i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			//String dutyKind = jsonObj.getString("duty");//职务
			String roleCode = jsonObj.getString("roleCode");
			String weighting = jsonObj.getString("weighting");
			//roleWeightStr = roleWeightStr + roleCode + "," + weighting + ";";
			roleWeightStr = roleWeightStr.append(roleCode + "," + weighting + ";");
		}
		weiSet.setRoleWeight(roleWeightStr.toString());
		o_weightSetBO.saveWeightSetDept(weiSet);
		map.put("success", true);
		return map;
	}
	/**
	 * 保存选中的职务权重(添加)
	 * @param roleId	角色id
	 * @param id		权重id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/saveroleweightsetbyroleid.f")
	public Map<String, Object> saveRoleWeightSetByRoleId(String roleIds, String id){
		Map<String, Object> map = new HashMap<String, Object>();
		WeightSet weiSet = o_weightSetBO.findWeightSetById(id);
		String roleWeightSet = o_weightSetBO.setRoleWeightString(roleIds,weiSet);
		weiSet.setRoleWeight(roleWeightSet);
		o_weightSetBO.saveWeightSetDept(weiSet);
		map.put("success", true);
		return map;
	}
	/**
	 * 删除职务权重
	 * @param modifyRecords
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/deleteroleweight.f")
	public Map<String, Object> deleteRoleWeight(String modifyRecords){
		Map<String, Object> map = new HashMap<String, Object>();
		JSONArray jsonArray=JSONArray.fromObject(modifyRecords);
		JSONObject jsonObj = jsonArray.getJSONObject(0);
		StringBuffer roWeiStr = new StringBuffer();
		String wetSetId = jsonObj.getString("wSetId");
		String roleCode = jsonObj.getString("roleCode");
		String weighting = jsonObj.getString("weighting");
		String delStr = roleCode+","+weighting;
		WeightSet weiSet = o_weightSetBO.findWeightSetById(wetSetId);
		String wetRoleStr = weiSet.getRoleWeight();
		String idArray[] = wetRoleStr.split(";");
		for (String roWeightId : idArray) {
			if(!delStr.equals(roWeightId)){
				//roWeiStr = roWeiStr+roWeightId+";";
				roWeiStr = roWeiStr.append(roWeightId+";");
			}
		}
		weiSet.setRoleWeight(roWeiStr.toString());
		o_weightSetBO.saveWeightSetDept(weiSet);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 公式计算下拉列表(最大值，平均值)
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/quaAssess/findformulatecalculatestore.f")
	public Map<String, Object> findFormulateCalculateStore(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("dim_calculate_method");
		for (DictEntry dictEntry : dictEntryList) {
			if("dim_calculate_method_avg".equals(dictEntry.getId())||"dim_calculate_method_max".equals(dictEntry.getId())){
				inmap = new HashMap<String, Object>();
				inmap.put("id", dictEntry.getId());
				inmap.put("name",dictEntry.getName());
				list.add(inmap);
			}
		}
		map.put("datas", list);
		return map;
	}
	/**
	 * 公式计算（最大值，加权平均）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/quaAssess/findformulatemaxweightstore.f")
	public Map<String, Object> findFormulateMaxWeightStore(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("dim_calculate_method");
		for (DictEntry dictEntry : dictEntryList) {
			if("dim_calculate_method_weight_avg".equals(dictEntry.getId())||"dim_calculate_method_max".equals(dictEntry.getId())){
				inmap = new HashMap<String, Object>();
				inmap.put("id", dictEntry.getId());
				inmap.put("name",dictEntry.getName());
				list.add(inmap);
			}
		}
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 汇总公式计算（最大值，加权平均）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/quaAssess/summarizingStore.f")
	public Map<String, Object> summarizingStore(String type){
		String str = "kpiSummarizingTrue:true:1,kpiSummarizingFalse:false:0--orgSummarizingTrue:true:1," +
				"orgSummarizingFalse:false:0--" +
				"strategySummarizingTrue:true:1," +
				"strategySummarizingFalse:false:0--" +
				"processSummarizingTrue:true:1,processSummarizingFalse:false:0";
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		HashMap<String, String> mapSummarizing = new HashMap<String, String>();
		FormulaSet formulaSet = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid());
		if(null == formulaSet){
			formulaSet = new FormulaSet();
			formulaSet.setId(Identities.uuid());
			formulaSet.setDeptRiskFormula("dim_calculate_method_avg");
			formulaSet.setStrategyRiskFormula("dim_calculate_method_weight_avg");
			formulaSet.setKpiRiskFormula("dim_calculate_method_avg");
			formulaSet.setProcessRiskFormula("dim_calculate_method_avg");
			//formula.setRiskLevelFormula("dim_calculate_method_level");
			formulaSet.setSummarizing(str);
			formulaSet.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
			o_weightSetBO.saveFormulaSet(formulaSet);
		}
		
		String summarizing = formulaSet.getSummarizing();
		if(StringUtils.isBlank(summarizing)){
			formulaSet.setSummarizing(str);
			o_formulaSetBO.mergeFormlaSet(formulaSet);
			summarizing = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid()).getSummarizing();
		}
		String strs[] = summarizing.split("--");
		for (String types : strs) {
			if(types.indexOf(type) != -1){
				if(Boolean.parseBoolean(types.split(",")[0].toString().split(":")[1].toString())){
					mapSummarizing.put(types.split(",")[0].toString().split(":")[0], "是");
				}
				
				if(!Boolean.parseBoolean(types.split(",")[1].toString().split(":")[1])){
					mapSummarizing.put(types.split(",")[1].toString().split(":")[0], "否");
				}
				break;
				
			}
		}
		Set<Entry<String, String>> key = mapSummarizing.entrySet();
        for (Iterator<Entry<String, String>> it = key.iterator(); it.hasNext();) {
            String s = it.next().getKey();
            inmap = new HashMap<String, Object>();
			inmap.put("id", s);
			inmap.put("name", mapSummarizing.get(s));
			list.add(inmap);
        }
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 公式计算（加权平均）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/quaAssess/findformulateweightavgstore.f")
	public Map<String, Object> findFormulateWeightAvgStore(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("dim_calculate_method");
		for (DictEntry dictEntry : dictEntryList) {
			if("dim_calculate_method_weight_avg".equals(dictEntry.getId())){
				inmap = new HashMap<String, Object>();
				inmap.put("id", dictEntry.getId());
				inmap.put("name",dictEntry.getName());
				list.add(inmap);
			}
		}
		map.put("datas", list);
		return map;
	}
	/**
	 * 风险水平计算下拉框
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/quaAssess/findformulaterisklevelstore.f")
	public Map<String, Object> findFormulateRiskLevelStore(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("dim_calculate_method");
		for (DictEntry dictEntry : dictEntryList) {
			if("dim_calculate_method_level".equals(dictEntry.getId())){
				inmap = new HashMap<String, Object>();
				inmap.put("id", dictEntry.getId());
				inmap.put("name",dictEntry.getName());
				list.add(inmap);
			}
		}
		map.put("datas", list);
		return map;
	}
	/**
	 * 查询公式计算下拉框默认值
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/quaAssess/queryformulatwcalculatevalue.f")
	public List<Map<String,Object>> queryFormulatwCalculateValue(){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		List<Dimension> dimenList = new ArrayList<Dimension>();
		StringBuffer riskLevel = new StringBuffer();
		FormulaSet formula = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid());
		
		if(null == formula){
			formula = new FormulaSet();
			String str = "kpiSummarizingTrue:true:0,kpiSummarizingFalse:false:1--" +
					"orgSummarizingTrue:true:1,orgSummarizingFalse:false:0--" +
					"strategySummarizingTrue:true:0,strategySummarizingFalse:false:1--" +
					"processSummarizingTrue:true:0,processSummarizingFalse:false:1";
			formula.setId(Identities.uuid());
			formula.setDeptRiskFormula("dim_calculate_method_avg");
			formula.setStrategyRiskFormula("dim_calculate_method_weight_avg");
			formula.setKpiRiskFormula("dim_calculate_method_avg");
			formula.setProcessRiskFormula("dim_calculate_method_avg");
			//formula.setRiskLevelFormula("dim_calculate_method_level");
			formula.setSummarizing(str);
			formula.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
			o_weightSetBO.saveFormulaSet(formula);
		}
		if(StringUtils.isNotBlank(formula.getRiskLevelFormula())){
			String ids[] = formula.getRiskLevelFormula().split("[><]+");
			 for (String id : ids) {
				 Dimension dim = o_weightSetBO.findDimensionBydimId(id);
				 if(null != dim){
					 //riskLevel = riskLevel + dim.getName();
					 riskLevel = riskLevel.append(dim.getName());
				 }else{
					 //riskLevel = riskLevel + id.toString();
					 riskLevel = riskLevel.append(id.toString());
				 }
				
				 dimenList.add(dim);
			  }
		}
		HashMap<String, String> summarizingMap = o_weightSetBO.findInitValue("orgSummarizing,kpiSummarizing,strategySummarizing,processSummarizing");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", formula.getId());
		map.put("deptRiskFormula", formula.getDeptRiskFormula());
		map.put("strategyRiskFormula", formula.getStrategyRiskFormula());
		map.put("kpiRiskFormula", formula.getKpiRiskFormula());
		map.put("processRiskFormula", formula.getProcessRiskFormula());
		map.put("orgSummarizing", summarizingMap.get("orgSummarizing"));
		map.put("kpiSummarizing", summarizingMap.get("kpiSummarizing"));
		map.put("strategySummarizing", summarizingMap.get("strategySummarizing"));
		map.put("processSummarizing", summarizingMap.get("processSummarizing"));
		if(null != formula.getRiskTypeDimFormula()){
			String dimIdAndValues[] = formula.getRiskTypeDimFormula().split("\\|");
			for (String dinIdAndValue : dimIdAndValues) {
				String dimStrs[] = dinIdAndValue.split(",");
				if(dimStrs.length > 1){
					map.put(dimStrs[0], dimStrs[1]);
				}
			}
		}
		map.put("riskLevelFormula", riskLevel.toString());
		map.put("formularId", formula.getRiskLevelFormula());
		listmap.add(map);
		return listmap;
	}
	
	/**
	 * 保存公式计算
	 * @param modifyRecords
	 * @param id
	 * @param formularId	风险水平公式id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/quaAssess/saveformulaset.f")
	public Map<String, Object> saveFormulaSet(FormulaSetForm formuForm, String id, String formularId, String dimensionStr){
		Map<String, Object> map = new HashMap<String, Object>();
		FormulaSet formulaSet = new FormulaSet();
		SysOrganization company = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getCompanyid());
		String summarizing = o_weightSetBO.updateValue(formuForm.getOrgSummarizing() + "," 
				+ formuForm.getKpiSummarizing() + "," 
				+ formuForm.getStrategySummarizing() + "," 
				+ formuForm.getProcessSummarizing());
		
		formulaSet.setId(id);
		formulaSet.setDeptRiskFormula(formuForm.getDeptRiskFormula());
		formulaSet.setKpiRiskFormula(formuForm.getKpiRiskFormula());
		formulaSet.setProcessRiskFormula(formuForm.getProcessRiskFormula());
		formulaSet.setRiskTypeDimFormula(dimensionStr);//动态维度值
		formulaSet.setStrategyRiskFormula(formuForm.getStrategyRiskFormula());
		formulaSet.setRiskLevelFormulaText(formuForm.getRiskLevelFormula());
		formulaSet.setRiskLevelFormula(formularId);
		formulaSet.setSummarizing(summarizing);
		formulaSet.setCompany(company);
		o_weightSetBO.saveFormulaSet(formulaSet);//更新
		map.put("success", true);
		return map;
	}
	/**
	 * 告警方案下拉列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/findalarmplan.f")
	public Map<String, Object> findAlarmPlan(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<AlarmPlan> alarmEntryList = o_weightSetBO.findAlarmPlanByAlarmType("0alarm_type_rm");
		if(null != alarmEntryList){
			for (AlarmPlan alarmEntry : alarmEntryList) {
				inmap = new HashMap<String, Object>();
				inmap.put("id", alarmEntry.getId());
				inmap.put("name",alarmEntry.getName());
				list.add(inmap);
			}
		}
		map.put("datas", list);
		return map;
	}
	/**
	 * 保存告警方案
	 * @param alarmId	告警方案id
	 * @param id		权重id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/saveriskalarmplan.f")
	public Map<String, Object> saveRiskAlarmPlan(String alarmId, String id){
		Map<String, Object> map = new HashMap<String, Object>();
		AlarmPlan alarmPlan = o_alarmPlanBO.findAlarmPlanById(alarmId);
		WeightSet weigSet = o_weightSetBO.findWeightSetById(id);
		weigSet.setAlarmScenario(alarmPlan);
		o_weightSetBO.saveWeightSetDept(weigSet);
		map.put("success", true);
		return map;
	}
	/**
	 * 查询告警方案默认值
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/queryalarmplanvalue.f")
	public List<Map<String,Object>> queryAlarmPlanValue(){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		WeightSet weigSet = o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid());
		if(null != weigSet){
			if(null != weigSet.getAlarmScenario()){
				map.put("alarmid", weigSet.getAlarmScenario().getId());
			}
			
		}
		listmap.add(map);
		return listmap;
	}
	/**
	 * 公式编辑树加载
	 * @param node
	 * @param query
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/assess/formularsetedittreeloader.f")
	public List<Map<String, Object>> formularSetEditTreeLoader(String sqrt)
	{
		String jtId = o_orgGridBO.findOrgByParentIdIsNUll().getId();
	    return o_weightSetBO.treeLoader(sqrt, jtId);
	}
	
	/**
	 * 查询主维度(动态combox)
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/findmaindimensions.f")
	public Map<String, Object> findMainDimensions(){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> mapList = o_weightSetBO.findDimensionIsParentIdIsNullAllMapObj();
		map.put("dimensionList", mapList);
		return map;
	}
	
	/**
	 * 验证部门风险管理员的数量
	 * @param roleIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/finduserrolessamebyroleids.f")
	public Map<String,String> findUserRolesSameByRoleIds(String deptIds){
		Map<String,String> map = new HashMap<String, String>();
		String deptNames = "";
		if (StringUtils.isNotBlank(deptIds)) {
			String[] idArray = deptIds.split(",");
			for (String id : idArray) {
				List<SysEmployee> deptRiskMan = o_assessTaskBO.findEmpsByRoleIdAnddeptId(id,"DeptRiskManager");//查找部门风险管理员
				if(deptRiskMan.size()!=1){
					SysOrganization org = o_orgGridBO.findOrganizationByOrgId(id);
					map.put("DeptRiskManagerCount", deptRiskMan.size() + "");
					deptNames = org.getOrgname() + "," + deptNames;
				}else{
					map.put("DeptRiskManagerCount", "");
				}
			}
		}
		map.put("deptName", deptNames);
		return map;
	}
	/**
	 * 初始化公式计算
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/findformulasetbycompanyid.f")
	public void findFormulaSetByCompanyId(){
		FormulaSet formula = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid());
		if(null == formula){
			formula = new FormulaSet();
			String str = "kpiSummarizingTrue:true:0,kpiSummarizingFalse:false:1--" +
					"orgSummarizingTrue:true:1,orgSummarizingFalse:false:0--" +
					"strategySummarizingTrue:true:0,strategySummarizingFalse:false:1--" +
					"processSummarizingTrue:true:0,processSummarizingFalse:false:1";
			formula.setId(Identities.uuid());
			formula.setDeptRiskFormula("dim_calculate_method_avg");
			formula.setStrategyRiskFormula("dim_calculate_method_weight_avg");
			formula.setKpiRiskFormula("dim_calculate_method_avg");
			formula.setProcessRiskFormula("dim_calculate_method_avg");
			//formula.setRiskLevelFormula("dim_calculate_method_level");
			formula.setSummarizing(str);
			formula.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
			o_weightSetBO.saveFormulaSet(formula);
		}
	}
	
}