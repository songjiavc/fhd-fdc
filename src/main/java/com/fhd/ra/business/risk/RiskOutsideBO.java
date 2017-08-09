package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.process.ProcessRelaRiskDAO;
import com.fhd.dao.risk.KpiAdjustHistoryDAO;
import com.fhd.dao.risk.KpiRelaRiskDAO;
import com.fhd.dao.risk.OrgAdjustHistoryDAO;
import com.fhd.dao.risk.ProcessAdjustHistoryDAO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.dao.risk.StrategyAdjustHistoryDAO;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.StrategyAdjustHistory;
import com.fhd.entity.sys.dic.DictEntryRelation;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;

@Service
@SuppressWarnings("unchecked")
public class RiskOutsideBO {

	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;

	@Autowired
	private KpiRelaRiskDAO o_kpiRelaRiskDAO;
	
	@Autowired
	private ProcessRelaRiskDAO o_processRelaRiskDAO;
	
	//评估记录
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	
	@Autowired
	private OrgAdjustHistoryDAO o_orgAdjustHistoryDAO;
	
	@Autowired
	private StrategyAdjustHistoryDAO o_strategyAdjustHistoryDAO;
	
	@Autowired
	private KpiAdjustHistoryDAO o_kpiAdjustHistoryDAO;
	
	@Autowired
	private ProcessAdjustHistoryDAO o_processAdjustHistoryDAO;
	
	/**
	 * 指标
	 */
	@Autowired
    private KpiDAO o_kpiDAO;
	
	/**
	 * 添加风险，并且添加实体的相关信息
	 * @author 郑军祥		2013-06-18
	 * @param risk					风险实体
	 * @param respOrgIds			风险责任部门
     * @param relaOrgIds			风险相关部门
	 * @param influenceKpiIds		影响指标
	 * @param influeceProcessIds	影响流程
	 * @return true:成功 false:失败
	 */
	@Transactional
	public boolean saveRisk(Risk risk,List<String> respOrgIds,List<String> relaOrgIds,
		            List<String> influenceKpiIds,List<String> influeceProcessIds){
		o_riskDAO.merge(risk);
		
//		//添加责任部门
//		for(String id : respOrgIds){
//			RiskOrg riskOrg = new RiskOrg();
//			riskOrg.setId(UUID.randomUUID().toString());
//			riskOrg.setRisk(risk);
//			riskOrg.setType("M");
//			SysOrganization sysOrganization = new SysOrganization();
//			sysOrganization.setId(id);
//			riskOrg.setSysOrganization(sysOrganization);
//			o_riskOrgDAO.merge(riskOrg);
//		}
//		
//		//添加相关部门
//		for(String id : relaOrgIds){
//			RiskOrg riskOrg = new RiskOrg();
//			riskOrg.setId(UUID.randomUUID().toString());
//			riskOrg.setRisk(risk);
//			riskOrg.setType("A");
//			SysOrganization sysOrganization = new SysOrganization();
//			sysOrganization.setId(id);
//			riskOrg.setSysOrganization(sysOrganization);
//			o_riskOrgDAO.merge(riskOrg);
//		}
//		
//		//添加影响指标
//		for(String id : influenceKpiIds){
//			KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
//			kpiRelaRisk.setId(UUID.randomUUID().toString());
//			kpiRelaRisk.setRisk(risk);
//			kpiRelaRisk.setType("I");
//			Kpi kpi = new Kpi();
//			kpi.setId(id);
//			kpiRelaRisk.setKpi(kpi);
//			o_kpiRelaRiskDAO.merge(kpiRelaRisk);
//		}
//		
//		//添加影响流程
//		for(String id : influeceProcessIds){
//			ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
//			processRelaRisk.setId(UUID.randomUUID().toString());
//			processRelaRisk.setRisk(risk);
//			processRelaRisk.setType("I");
//			Process process = new Process();
//			process.setId(id);
//			processRelaRisk.setProcess(process);
//			o_processRelaRiskDAO.merge(processRelaRisk);
//		}
//				
		return true;
	}

	/**
	 * 修改风险
	 * @author 郑军祥
	 * @param risk					风险实体
	 * @param respOrgIds			风险责任部门
     * @param relaOrgIds			风险相关部门
	 * @param influenceKpiIds		影响指标
	 * @param influeceProcessIds	影响流程
	 * @return true:成功 false:失败
	 */
	@Transactional
	public boolean mergeRiskEvent(Risk risk,List<String> respOrgIds,List<String> relaOrgIds,
            List<String> influenceKpiIds,List<String> influeceProcessIds) {
		
		//删除责任部门
		for(String id : respOrgIds){
			o_riskOrgDAO.delete(id);
		}
		//删除相关部门
		for(String id : relaOrgIds){
			o_riskOrgDAO.delete(id);
		}

		//删除影响指标
		for(String id : influenceKpiIds){
			o_kpiRelaRiskDAO.delete(id);
		}
		
		//删除影响流程
		for(String id : influeceProcessIds){
			o_processRelaRiskDAO.delete(id);
		}
				
		o_riskDAO.merge(risk);
		
		//添加责任部门
		for(String id : respOrgIds){
			RiskOrg riskOrg = new RiskOrg();
			riskOrg.setId(UUID.randomUUID().toString());
			riskOrg.setRisk(risk);
			riskOrg.setType("M");
			SysOrganization sysOrganization = new SysOrganization();
			sysOrganization.setId(id);
			riskOrg.setSysOrganization(sysOrganization);
			o_riskOrgDAO.merge(riskOrg);
		}
		
		//添加相关部门
		for(String id : relaOrgIds){
			RiskOrg riskOrg = new RiskOrg();
			riskOrg.setId(UUID.randomUUID().toString());
			riskOrg.setRisk(risk);
			riskOrg.setType("A");
			SysOrganization sysOrganization = new SysOrganization();
			sysOrganization.setId(id);
			riskOrg.setSysOrganization(sysOrganization);
			o_riskOrgDAO.merge(riskOrg);
		}
		
		//添加影响指标
		for(String id : influenceKpiIds){
			KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
			kpiRelaRisk.setId(UUID.randomUUID().toString());
			kpiRelaRisk.setRisk(risk);
			kpiRelaRisk.setType("I");
			Kpi kpi = new Kpi();
			kpi.setId(id);
			kpiRelaRisk.setKpi(kpi);
			o_kpiRelaRiskDAO.merge(kpiRelaRisk);
		}
		
		//添加影响流程
		for(String id : influeceProcessIds){
			ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
			processRelaRisk.setId(UUID.randomUUID().toString());
			processRelaRisk.setRisk(risk);
			processRelaRisk.setType("I");
			Process process = new Process();
			process.setId(id);
			processRelaRisk.setProcess(process);
			o_processRelaRiskDAO.merge(processRelaRisk);
		}
		
		return true;
	}

	/**
	 * 删除风险
	 * @author 郑军祥
	 * @param id
	 * @return
	 */
	@Transactional
	public boolean removeRiskById(String id) {
		Risk risk = findRiskById(id);
		risk.setDeleteStatus("0");
		o_riskDAO.merge(risk);
		return true;
	}

	/**
	 * 批量删除风险
	 * @author 郑军祥
	 * @param ids
	 * @return
	 */
	@Transactional
	public boolean removeRiskByIds(String[] ids) {
		for (String id : ids) {
			Risk risk = findRiskById(id);
			risk.setDeleteStatus("0");
			o_riskDAO.merge(risk);
		}
		return true;
	}
	
	/**
	 * 查询所有风险信息
	 * @author 郑军祥 2013-6-25
	 * @return Map map的key值是riskId,value值是risk对象
	 */
	public Map<String, Risk> getAllRiskInfo(String companyId) {
		Map<String, Risk> map = new HashMap<String,Risk>();
		List<Risk> riskList = getAllRisk(companyId);
		for(Risk risk : riskList){
			map.put(risk.getId(), risk);
		}
		List<RiskScoreObject> riskScoreObjectList = getRiskScoreObjectList(companyId);
		if(riskScoreObjectList!= null && riskScoreObjectList.size()>0){
			for(RiskScoreObject riskScoreObject :riskScoreObjectList){
				Risk _risk = new Risk();
				_risk.setId(riskScoreObject.getRiskId());
				_risk.setCompany(riskScoreObject.getCompany());
				_risk.setTemplate(riskScoreObject.getTemplate());
				_risk.setCode(riskScoreObject.getCode());
				_risk.setName(riskScoreObject.getName());
				_risk.setDesc(riskScoreObject.getDesc());
				_risk.setParent(riskScoreObject.getParent());
				_risk.setParentName(riskScoreObject.getParentName());
				_risk.setLevel(riskScoreObject.getLevel());
				_risk.setIdSeq(riskScoreObject.getIdSeq());
				_risk.setDeleteStatus(riskScoreObject.getDeleteStatus());
				_risk.setArchiveStatus(riskScoreObject.getArchiveStatus());
				_risk.setSort(riskScoreObject.getSort());
				_risk.setCreateTime(riskScoreObject.getCreateTime());
				_risk.setCreateBy(riskScoreObject.getCreateBy());
				_risk.setCreateOrg(riskScoreObject.getCreateOrg());
				_risk.setLastModifyTime(riskScoreObject.getLastModifyTime());
				_risk.setResponseStrategys(riskScoreObject.getResponseStrategys());
				_risk.setIsRiskClass(riskScoreObject.getIsRiskClass());
				_risk.setIsLeaf(riskScoreObject.getIsLeaf());
				_risk.setIsInherit(riskScoreObject.getIsInherit());
				_risk.setFormulaDefine(riskScoreObject.getFormulaDefine());
				_risk.setAlarmScenario(riskScoreObject.getAlarmScenario());
				_risk.setGatherFrequence(riskScoreObject.getGatherFrequence());
				_risk.setGatherDayFormulr(riskScoreObject.getGatherDayFormulr());
				_risk.setGatherDayFormulrShow(riskScoreObject.getGatherDayFormulrShow());
				_risk.setResultCollectInterval(riskScoreObject.getResultCollectInterval());
				_risk.setIsFix(riskScoreObject.getIsFix());
				_risk.setIsUse(riskScoreObject.getIsUse());
				_risk.setIsAnswer(riskScoreObject.getIsAnswer());
				_risk.setCalc(riskScoreObject.getCalc());
				_risk.setSchm(riskScoreObject.getSchm());
				if(!map.containsKey(_risk.getId())){
					map.put(_risk.getId(), _risk);
				}
				
			}
		}
		return map;
	}
	
	@Autowired
	private RiskScoreBO riskScoreBO;
	public List<RiskScoreObject> getRiskScoreObjectList(String companyId){
		return this.riskScoreBO.getRiskScoreObjectListByCompanyId(companyId);
	}
	
	public Map<String, Object> findRiskInfoById(String riskId) {
		Risk risk = findRiskById(riskId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		//上级风险
		JSONArray jsonArray = new JSONArray();
		if(risk.getParent()!=null){
			JSONObject object = new JSONObject();
			object.put("id", risk.getParent().getId());
			jsonArray.add(object);
		}
		map.put("parentId", jsonArray.toString());
		map.put("code", risk.getCode());
		map.put("name", risk.getName());
		map.put("desc", risk.getDesc());
		
		//责任部门/人和相关部门/人
		JSONObject object = new JSONObject();
		JSONArray respDeptName = new JSONArray();
		JSONArray relaDeptName = new JSONArray();
		Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
		for(RiskOrg riskOrg : riskOrgs){
			if(riskOrg.getType().equals("M")){	//责任部门/人
				object = new JSONObject();
				SysOrganization sysOrganizationIte = riskOrg.getSysOrganization();
				if(sysOrganizationIte!=null){
					object.put("deptid", sysOrganizationIte.getId());
				}
				SysEmployee empIte = riskOrg.getEmp();
				if(empIte!=null){
					object.put("empid", empIte.getId());
				}else{
					object.put("empid", "");
				}
				respDeptName.add(object);
			}
			if(riskOrg.getType().equals("A")){	//相关部门/人
				object = new JSONObject();
				SysOrganization sysOrganizationIte = riskOrg.getSysOrganization();
				if(sysOrganizationIte!=null){
					object.put("deptid", sysOrganizationIte.getId());
				}
				SysEmployee empIte = riskOrg.getEmp();
				if(empIte!=null){
					object.put("empid", empIte.getId());
				}else{
					object.put("empid", "");
				}
				relaDeptName.add(object);
			}
		}
		map.put("respDeptName", respDeptName.toString());
		map.put("relaDeptName", relaDeptName.toString());
		
		//风险类别
		StringBuffer riskKind = new StringBuffer();
		Set<DictEntryRelation> dictEntryRelations = risk.getRiskKinds();
		for(DictEntryRelation relation : dictEntryRelations){
			riskKind.append(relation.getDictEntry().getId());
			riskKind.append(",");
		}
		if(!riskKind.equals("")){
			riskKind.append(riskKind.substring(0, riskKind.length()-1));
		}
		map.put("riskKind", riskKind);
		
		//定量分析
		map.put("isFix", risk.getIsFix());	
		
		//是否启用
		map.put("isUse", risk.getIsUse());

		//影响指标，风险指标
		String riskKpiName = "";
		String influKpiName = "";
		Set<KpiRelaRisk> kpiRelaRisks = risk.getKpiRelaRisks();
		for(KpiRelaRisk kpi : kpiRelaRisks){
			if(kpi.getType().equals("RM")){	//风险指标
				riskKpiName += kpi.getKpi().getId() + ",";
			}
			if(kpi.getType().equals("I")){	//影响指标
				influKpiName += kpi.getKpi().getId() + ",";
			}
		}
		if(!riskKpiName.equals("")){
			riskKpiName = riskKpiName.substring(0, riskKpiName.length()-1);
		}
		if(!influKpiName.equals("")){
			influKpiName = influKpiName.substring(0, influKpiName.length()-1);
		}
		map.put("riskKpiName", riskKpiName);
		map.put("influKpiName", influKpiName);

		//影响流程，控制流程
		String controlProcessureName = "";
		String influProcessureName = "";
		Set<ProcessRelaRisk> ProcessRelaRisks = risk.getRiskProcessures();
		for(ProcessRelaRisk processure : ProcessRelaRisks){
			if(processure.getType().equals("C")){	//控制流程
				controlProcessureName += processure.getProcess().getId() + ",";
			}
			if(processure.getType().equals("I")){	//影响指标
				influProcessureName += processure.getProcess().getId() + ",";
			}
		}
		if(!controlProcessureName.equals("")){
			controlProcessureName = controlProcessureName.substring(0, controlProcessureName.length()-1);
		}
		if(!influProcessureName.equals("")){
			influProcessureName = influProcessureName.substring(0, influProcessureName.length()-1);
		}
		map.put("controlProcessureName", controlProcessureName);
		map.put("influProcessureName", influProcessureName);
		
		//是否继承
		map.put("isInherit", risk.getIsInherit());
		//模板下拉框
		map.put("templateId", risk.getTemplate()==null?"":risk.getTemplate().getId());
		//告警方案
		map.put("alarmPlanId", risk.getAlarmScenario()==null?"":risk.getAlarmScenario().getId());
		//公式计算
		map.put("formulaDefine", risk.getFormulaDefine());
		//涉及版块
		String relePlate = "";
		Set<DictEntryRelation> palteRelations = risk.getRelePlates();
		for(DictEntryRelation relation : palteRelations){
			relePlate += relation.getDictEntry().getId() + ",";
		}
		if(!relePlate.equals("")){
			relePlate = relePlate.substring(0, relePlate.length()-1);
		}
		map.put("relePlate", relePlate);
		
		return map;
	}

	/**
	 * 查询风险信息
	 * @author 郑军祥
	 * @param ids
	 * @return Map map的key值是riskId,value值是risk对象
	 */
	public Map<Integer, Map<String, Object>> findRiskInfoByIds(String params) {
		List<Risk> risks = null;
		Map<Integer, Map<String, Object>> mapsCount = new HashMap<Integer, Map<String, Object>>();
		JSONArray jsonarr = JSONArray.fromObject(params);
		
		String[] riskIds = new String[jsonarr.size()];
		int j = 0;
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			riskIds[j] = jsobjs.getString("riskId");
			j++;
		}
		risks = this.findRisksByIds(riskIds);
		
		int count = 0;
		for (Risk risk : risks) {
			//Risk risk = risks.get(count);
			Map<String, Object> map = new HashMap<String, Object>();

			// 上级风险
			// JSONArray jsonArray = new JSONArray();
			String parentName = "";
			if (risk.getParent() != null) {
				// JSONObject object = new JSONObject();
				// object.put("id", risk.getParent().getId());
				parentName = risk.getParent().getName();
				// object.put("name", risk.getParent().getName());
				// jsonArray.add(object);
			}
			map.put("parentRiskName", parentName);
			map.put("code", risk.getCode());
			map.put("riskName", risk.getName());
			map.put("desc", risk.getDesc());

			// 责任部门/人和相关部门/人
			JSONObject object = null;
			JSONArray respDeptName = new JSONArray();
			JSONArray relaDeptName = new JSONArray();
			Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
			for (RiskOrg riskOrg : riskOrgs) {
				if (riskOrg.getType().equals("M")) { // 责任部门/人
					object = new JSONObject();
					SysOrganization sysOrganizationIte = riskOrg
							.getSysOrganization();
					if (sysOrganizationIte != null) {
						object.put("deptid", sysOrganizationIte.getOrgname());
					}
					SysEmployee empIte = riskOrg.getEmp();
					if (empIte != null) {
						try {
							object.put("empid", empIte.getEmpname());
						} catch (Exception e) {
						}
						
					} else {
						object.put("empid", "");
					}
					respDeptName.add(object);
				}
				if (riskOrg.getType().equals("A")) { // 相关部门/人
					object = new JSONObject();
					SysOrganization sysOrganizationIte = riskOrg
							.getSysOrganization();
					if (sysOrganizationIte != null) {
						object.put("deptid", sysOrganizationIte.getOrgname());
					}
					SysEmployee empIte = riskOrg.getEmp();
					if (empIte != null) {
						try {
							object.put("empid", empIte.getEmpname());
						} catch (Exception e) {
							//object.put("empid", empIte.getEmpname());
						}
						
					} else {
						object.put("empid", "");
					}
					relaDeptName.add(object);
				}
			}

			StringBuffer respDept = new StringBuffer();
			StringBuffer relaDept = new StringBuffer();
			for (int i = 0; i < respDeptName.size(); i++) {
				JSONObject jsobj = respDeptName.getJSONObject(i);
				respDept.append(jsobj.get("deptid"));
				respDept.append(",");
			}

			for (int i = 0; i < relaDeptName.size(); i++) {
				JSONObject jsobj = relaDeptName.getJSONObject(i);
				relaDept.append(jsobj.get("deptid"));
				relaDept.append(",");
			}
			respDept.append(";");
			relaDept.append(";");
			map.put("respDeptName", respDept.toString().replace(",;", ""));
			map.put("relaDeptName", relaDept.toString().replace(",;", ""));

			// 风险类别
			String riskKind = "";
			Set<DictEntryRelation> dictEntryRelations = risk.getRiskKinds();
			for (DictEntryRelation relation : dictEntryRelations) {
				riskKind += relation.getDictEntry().getId() + ",";
			}
			if (!riskKind.equals("")) {
				riskKind = riskKind.substring(0, riskKind.length() - 1);
			}
			map.put("riskKind", riskKind);

			// 定量分析
			map.put("isFix", risk.getIsFix());

			// 是否启用
			map.put("isUse", risk.getIsUse());

			// 影响指标，风险指标
			String riskKpiName = "";
			String influKpiName = "";
			Set<KpiRelaRisk> kpiRelaRisks = risk.getKpiRelaRisks();
			for (KpiRelaRisk kpi : kpiRelaRisks) {
				if (kpi.getType().equals("RM")) { // 风险指标
					riskKpiName += kpi.getKpi().getId() + ",";
				}
				if (kpi.getType().equals("I")) { // 影响指标
					influKpiName += kpi.getKpi().getName() + ",";
				}
			}
			if (!riskKpiName.equals("")) {
				riskKpiName = riskKpiName
						.substring(0, riskKpiName.length() - 1);
			}
			if (!influKpiName.equals("")) {
				influKpiName = influKpiName.substring(0,
						influKpiName.length() - 1);
			}

			map.put("riskKpiName", riskKpiName);
			map.put("influKpiName", influKpiName);

			// 影响流程，控制流程
			String controlProcessureName = "";
			String influProcessureName = "";
			Set<ProcessRelaRisk> ProcessRelaRisks = risk.getRiskProcessures();
			for (ProcessRelaRisk processure : ProcessRelaRisks) {
				if (processure.getType().equals("C")) { // 控制流程
					controlProcessureName += processure.getProcess().getId()
							+ ",";
				}
				if (processure.getType().equals("I")) { // 影响指标
					influProcessureName += processure.getProcess().getName()
							+ ",";
				}
			}
			if (!controlProcessureName.equals("")) {
				controlProcessureName = controlProcessureName.substring(0,
						controlProcessureName.length() - 1);
			}
			if (!influProcessureName.equals("")) {
				influProcessureName = influProcessureName.substring(0,
						influProcessureName.length() - 1);
			}
			map.put("controlProcessureName", controlProcessureName);
			map.put("influProcessureName", influProcessureName);

			// 是否继承
			map.put("isInherit", risk.getIsInherit());
			// 模板下拉框
			map.put("templateId", risk.getTemplate() == null ? "" : risk
					.getTemplate().getId());
			// 告警方案
			map.put("alarmPlanId", risk.getAlarmScenario() == null ? "" : risk
					.getAlarmScenario().getId());
			// 公式计算
			map.put("formulaDefine", risk.getFormulaDefine());
			// 涉及版块
			String relePlate = "";
			Set<DictEntryRelation> palteRelations = risk.getRelePlates();
			for (DictEntryRelation relation : palteRelations) {
				relePlate += relation.getDictEntry().getId() + ",";
			}
			if (!relePlate.equals("")) {
				relePlate = relePlate.substring(0, relePlate.length() - 1);
			}
			map.put("relePlate", relePlate);
			map.put("riskId", risk.getId());
			mapsCount.put(count, map);
			count++;
		}

		return mapsCount;

	}
	
	/**
	 * 根据风险事件id数组，查询合并的风险事件。用于合并风险事件
	 * @author 郑军祥
	 * @param id
	 * @return map{parentRisk:risk对象，riskCodes:List<String>,respDeptIds:List<String>,relaDeptIds:List<String>,influKpiIds:List<String>,influProcessIds:List<String>}
	 */
	public Map<String, Object> findRiskByEventsId(String[] ids) {
		Map<String, Object> map = new HashMap<String,Object>();
		Criteria criteria = o_riskDAO.createCriteria();
		
		criteria.add(Restrictions.in("id", ids));
		List<Risk> risks = criteria.list();
		
		Risk parentRisk = risks.get(0).getParent();
		List<String> riskCodes = new ArrayList<String>();
		List<String> respDeptIds = new ArrayList<String>();
		List<String> relaDeptIds = new ArrayList<String>();
		List<String> influKpiIds = new ArrayList<String>();
		List<String> influProcessIds = new ArrayList<String>();
		for(Risk risk : risks){
			//风险编号
			riskCodes.add(risk.getCode());
			
			//责任相关部门
			Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
			for(RiskOrg riskOrg : riskOrgs){
				if(riskOrg.getType().equals("M")){
					String id = riskOrg.getSysOrganization().getId();
					if(!respDeptIds.contains(id)){
						respDeptIds.add(id);
					}
				}
				if(riskOrg.getType().equals("A")){
					String id = riskOrg.getSysOrganization().getId();
					if(!relaDeptIds.contains(id)){
						relaDeptIds.add(id);
					}
				}
			}
			
			//影响指标
			Set<KpiRelaRisk> kpiRelaRisks = risk.getKpiRelaRisks();
			for(KpiRelaRisk kpiRelaRisk : kpiRelaRisks){
				if(kpiRelaRisk.getType().equals("I")){
					String id = kpiRelaRisk.getKpi().getId();
					if(!influKpiIds.contains(id)){
						influKpiIds.add(id);
					}
				}
			}
			
			//影响流程
			Set<ProcessRelaRisk> processRelaRisks = risk.getRiskProcessures();
			for(ProcessRelaRisk processRelaRisk : processRelaRisks){
				if(processRelaRisk.getType().equals("I")){
					String id = processRelaRisk.getProcess().getId();
					if(!influProcessIds.contains(id)){
						influProcessIds.add(id);
					}
				}
			}
		}
		map.put("parentRisk", parentRisk);
		map.put("riskCodes", riskCodes);
		map.put("respDeptIds", respDeptIds);
		map.put("relaDeptIds", relaDeptIds);
		map.put("influKpiIds", influKpiIds);
		map.put("influProcessIds", influProcessIds);
		
		return map;
	}
	
	/**
	 * 按Id查询风险
	 * @author 郑军祥
	 * @param id
	 * @return
	 */
	private Risk findRiskById(String id) {
		Criteria criteria = o_riskDAO.createCriteria();
		List<Risk> list = null;
		
		criteria.add(Restrictions.eq("id", id));
		//加这一段是为了查询关联表是不发送查询请求，否则添加编辑后查询出现hibnate问题，现在已经把关联的缓存去掉了还有问题，所以必须这么加
		criteria.createAlias("riskOrgs", "riskOrgs",CriteriaSpecification.LEFT_JOIN);
		criteria.setFetchMode("riskOrgs", FetchMode.JOIN);
		criteria.createAlias("riskPosition", "riskPosition",CriteriaSpecification.LEFT_JOIN);
		criteria.setFetchMode("riskPosition", FetchMode.JOIN);
		criteria.createAlias("riskEmp", "riskEmp",CriteriaSpecification.LEFT_JOIN);
		criteria.setFetchMode("riskEmp", FetchMode.JOIN);
		criteria.createAlias("kpiRelaRisks", "kpiRelaRisks",CriteriaSpecification.LEFT_JOIN);
		criteria.setFetchMode("kpiRelaRisks", FetchMode.JOIN);
		
		list = criteria.list();
		if(list.size() != 0)
			return (Risk) criteria.list().get(0);
		else
			return null;
	}
	
	/**
	 * 按Id数组查询风险数组
	 * @author 郑军祥
	 * @param id
	 * @return
	 */
	private List<Risk> findRisksByIds(String[] ids) {
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.in("id", ids));
		
		return criteria.list();
	}
	
	/**
	 * 查询所有风险
	 * @author 郑军祥
	 * @param id
	 * @return
	 */
	private List<Risk> getAllRisk(String companyId) {
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		//criteria.add(Restrictions.eq("deleteStatus", "1"));
		
		return criteria.list();
	}
	
	/**
	 * 获取目标下的风险事件分页列表，给王再冉提供，没有使用到
	 * @param id	目标id
	 * @param page
	 * @param sort
	 * @param dir
	 * @param query
	 * @return 风险列表数据的集合[ID：id,名称：name,风险指标：riskKpiName，影响指标：influKpiName，风险水平：riskStatus]
	 */
	public List<Map<String,Object>> findRiskEventByStrategyMapId(String id,int pageNo,int pageSize,String sort, String dir,String query){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		Page<Risk> page = new Page<Risk>();
		page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        
		DetachedCriteria dc = DetachedCriteria.forClass(Risk.class);
		dc = dc.createAlias("kpiRelaRisks", "kpiRelaRisks");
		dc = dc.createAlias("kpiRelaRisks.kpi", "kpi");
		dc = dc.createAlias("kpi.dmRelaKpis", "dmRelaKpis");
		dc = dc.createAlias("dmRelaKpis.strategyMap", "strategyMap");
		//自动查询历史记录
		dc = dc.createAlias("adjustHistory", "adjustHistory", CriteriaSpecification.LEFT_JOIN, Restrictions.eq("adjustHistory.isLatest", "1"));

		String companyId = UserContext.getUser().getCompanyid();
	    dc.add(Restrictions.eq("company.id", companyId));
	    dc.add(Restrictions.eq("isRiskClass", "re"));
	    dc.add(Restrictions.eq("deleteStatus", "1"));
	    //目标指标过滤
	    dc.add(Restrictions.eq("kpi.deleteStatus", "1"));
	    if(id != null){
	    	dc.add(Restrictions.eq("strategyMap.id", id));
	    }
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.asc("level"));
		page = o_riskDAO.findPage(dc, page, false);
		
		/**
		 * 取得分页中的数据，封装的list中
		 */
		List<Risk> list = page.getResult();
		Map<String, Object> item = null;
		for (Risk event: list) {
			item = new HashMap<String, Object>();
			item.put("id",event.getId().toString());
			item.put("name",event.getName());

			StringBuffer riskKpiName = new StringBuffer();
			StringBuffer influKpiName = new StringBuffer();
			Set<KpiRelaRisk> kpiRelaRisks = event.getKpiRelaRisks();
			for(KpiRelaRisk kpi : kpiRelaRisks){
				if(kpi.getType().equals("RM")){	//风险指标
					riskKpiName.append(kpi.getKpi().getId());
					riskKpiName.append(",");
				}
				if(kpi.getType().equals("I")){	//影响指标
					influKpiName.append(kpi.getKpi().getId());
					influKpiName.append(",");
				}
			}
			if(!riskKpiName.equals("")){
				riskKpiName.append(riskKpiName.substring(0, riskKpiName.length()-1));
			}
			if(!influKpiName.equals("")){
				influKpiName.append(influKpiName.substring(0, influKpiName.length()-1));
			}
			//风险指标
			item.put("riskKpiName", riskKpiName);
			//影响指标
			item.put("influKpiName", influKpiName);	
			
			String riskStatus = "";
			Set<RiskAdjustHistory> historys = event.getAdjustHistory();
			if(historys!=null && historys.size()==1){
				Iterator<RiskAdjustHistory> iterator = historys.iterator();
				if(iterator.hasNext()){
					riskStatus = iterator.next().getAssessementStatus();
				}
			}
			//风险水平
			item.put("iconCls", riskStatus);
			
			datas.add(item);
		}
		
        return datas;
	}
	
	/**
	 * 查询风险、组织、目标、指标、流程下的评估状态灯
	 * @param type risk:风险,org:组织，sm:目标，kpi:指标，process:流程
	 * @param ids  id数组
	 * @return
	 */
	public Map<String,Object> findRiskAssementStatusById(String type,List<String> ids){
		Map<String,Object> map = new HashMap<String,Object>();
		for(int i=0;i<ids.size();i++){
			String id = ids.get(i);
			if(type.equals("risk")){
				String icon = findRiskAssementStatusByRiskId(id);
				map.put(id, icon);
			}else if(type.equals("org")){
				String icon = findRiskAssementStatusByOrgId(id);
				map.put(id, icon);
			}else if(type.equals("sm")){
				String icon = findRiskAssementStatusBySmId(id);
				map.put(id, icon);
			}else if(type.equals("kpi")){
				String icon = findRiskAssementStatusByKpiId(id);
				map.put(id, icon);
			}else if(type.equals("process")){
				String icon = findRiskAssementStatusByProcessId(id);
				map.put(id, icon);
			}else{
				map.put(id, "");
			}		
		}
		return map;
	}
	public String findRiskAssementStatusByRiskId(String riskId){
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("risk.id", riskId));
		criteria.add(Restrictions.eq("isLatest", "1"));		
		RiskAdjustHistory history = (RiskAdjustHistory)criteria.uniqueResult();
		
		if(history == null){
			return "icon-ibm-symbol-0-sm";
		}else{
			return history.getAssessementStatus();
		}
	}
	public String findRiskAssementStatusByOrgId(String orgId){
		Criteria criteria = o_orgAdjustHistoryDAO.createCriteria();
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("organization.id", orgId));
		criteria.add(Restrictions.eq("isLatest", "1"));		
		OrgAdjustHistory history = (OrgAdjustHistory)criteria.uniqueResult();
		
		if(history == null){
			return "icon-ibm-symbol-0-sm";
		}else{
			return history.getAssessementStatus();
		}
	}
	public String findRiskAssementStatusBySmId(String smId){
		Criteria criteria = o_strategyAdjustHistoryDAO.createCriteria();
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("strategyMap.id", smId));
		criteria.add(Restrictions.eq("isLatest", "1"));		
		StrategyAdjustHistory history = (StrategyAdjustHistory)criteria.uniqueResult();
		
		if(history == null){
			return "icon-ibm-symbol-0-sm";
		}else{
			return history.getAssessementStatus();
		}
	}
	public String findRiskAssementStatusByKpiId(String kpiId){
		Criteria criteria = o_kpiAdjustHistoryDAO.createCriteria();
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("kpiId.id", kpiId));
		criteria.add(Restrictions.eq("isLatest", "1"));		
		KpiAdjustHistory history = (KpiAdjustHistory)criteria.uniqueResult();
		
		if(history == null){
			return "icon-ibm-symbol-0-sm";
		}else{
			return history.getAssessementStatus();
		}
	}
	/**
	 * 根据流程id集合查询流程对应的风险状态.
	 * @author 吴德福
	 * @param processIds
	 * @return List<ProcessAdjustHistory>
	 */
	public List<ProcessAdjustHistory> findRiskAssementStatusByProcessIds(String processIds){
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		criteria.add(Restrictions.in("process.id", processIds.split(",")));
		criteria.add(Restrictions.eq("isLatest", "1"));		
		return criteria.list();
	}
	
	public String findRiskAssementStatusByProcessId(String processId){
		Criteria criteria = o_processAdjustHistoryDAO.createCriteria();
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("process.id", processId));
		criteria.add(Restrictions.eq("isLatest", "1"));		
		ProcessAdjustHistory history = (ProcessAdjustHistory)criteria.uniqueResult();
		
		if(history == null){
			return "icon-ibm-symbol-0-sm";
		}else{
			return history.getAssessementStatus();
		}
	}
	
	
	/**
	 * 获取风险的状态和趋势样式名称
	 * @param ids 风险数组
	 * @return Map(status[状态]=Map(id[风险id]=样式名称))
	 */
	public Map<String,Object> findRiskStatusAndTrendByRiskIds(List<String> ids){
		Map<String,Object> map = new HashMap<String,Object>();
 		Map<String,Object> statusMap = new HashMap<String,Object>(0);
 		Map<String,Object> trendMap = new HashMap<String,Object>(0);
		if(ids.size() == 0){
	 		map.put("statusMap", statusMap);
	 		map.put("trendMap", trendMap);
			return map;
		}
		
		List<String> str = new ArrayList<String>();
		for(int i=0;i<ids.size();i++){
			str.add(ids.get(i));
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT h.risk_id,h.assessement_status,dict.dict_entry_value")
		.append(" FROM t_rm_risk_adjust_history h")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE h.is_latest = '1' and h.risk_id in(:str)");
		
		Session session = o_riskAdjustHistoryDAO.getSessionFactory().openSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
		sqlQuery.setParameterList("str", str);
 		//sqlQuery.setMaxResults(1);
 		List<Object[]> list = sqlQuery.list();
		
  		for(Object[] o : list){
  			String id = o[0].toString();
  			statusMap.put(id, o[1]==null?"":o[1].toString());
  			trendMap.put(id, o[2]==null?"":o[2].toString());
  		}
 		map.put("statusMap", statusMap);
 		map.put("trendMap", trendMap);
 		
  		return map;
	}
	
	/**
	 * 查询风险分类下的所有风险事件
	 * 供其他模块使用风险事件数组来关联自己的业务表
	 * @param id
	 * @return 风险事件id数组
	 */
	public List<String> findRiskEventByRiskId(String id){
		
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct r.id riskId")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.parent_id in(")
		.append(" 	SELECT tb.id from t_rm_risks tb")
		.append(" 	WHERE tb.company_id=:companyId")
		.append(" 	and tb.is_risk_class='rbs'")
		.append("   and tb.delete_estatus=:deleteStatus");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				sql.append(")");
			}else{
				sql.append("	and tb.id_seq like '%." + id + ".%')");
			}
		}
		sql.append(" and r.company_id=:companyId");
		sql.append(" and r.is_risk_class='re'");
		sql.append(" and r.delete_estatus=:deleteStatus");
		
		//联合
		sql.append(" UNION ");
		sql.append("select distinct r.id ")
		.append(" FROM t_rm_risks_risks rr")
		.append(" LEFT JOIN t_rm_risks r on rr.risk_id = r.id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE rr.rela_risk_id in(")
		.append(" 	SELECT tb.id from t_rm_risks tb")
		.append(" 	WHERE tb.company_id=:companyId")
		.append(" 	and tb.is_risk_class='rbs'")
		.append("   and tb.delete_estatus=:deleteStatus");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				sql.append(")");
			}else{
				sql.append("	and tb.id_seq like :selectId)");
			}
		}
		sql.append(" and r.company_id=:companyId");
		sql.append(" and r.is_risk_class='re'");
		sql.append(" and r.delete_estatus=:deleteStatus");
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("companyId", companyId);
		sqlQuery.setParameter("selectId", "%."+id+".%");
		sqlQuery.setParameter("deleteStatus", Contents.DELETE_STATUS_USEFUL);
		List<String> list = sqlQuery.list();
        return list;
	}
	
	/**
	 * 获取组织下的所有风险事件
	 * 供其他模块使用风险事件数组来关联自己的业务表
	 * @param id
	 * @return 风险事件id数组
	 */
	public List<String> findRiskEventByOrgId(String id){
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct r.id riskId ")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_org riskorg on r.id = riskorg.risk_id")
		.append(" LEFT JOIN t_sys_organization org on org.id=riskorg.org_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus=:deleteStatus");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				
			}else{
				sql.append(" and org.id_seq like :selectId ");
			}
		}
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("companyId", companyId);
		sqlQuery.setParameter("selectId", "%."+id+".%");
		sqlQuery.setParameter("deleteStatus", Contents.DELETE_STATUS_USEFUL);
		List<String> idList = sqlQuery.list();
        return idList;
	}
	
	/**
	 * 获取目标下的所有风险事件
	 * 供其他模块使用风险事件数组来关联自己的业务表
	 * id
	 * @param id
	 * @return 风险事件id数组
	 */
	public List<String> findRiskEventByStrategyMapId(String id){
		//查询目标下有哪些指标
		List<Kpi> kpiList = findKpiBySmId(id);
		List<String> idsList = new ArrayList<String>();
		if(id.equalsIgnoreCase("root")){
			idsList.add("root");
		}else{
			for(Kpi kpi : kpiList){
				idsList.add(kpi.getId());
			}
		}
		return findRiskEventByKpiIds(idsList);
	}
	
	/**
	 * 查询目标下有哪些指标
	 * @return
	 */
	private List<Kpi> findKpiBySmId(String id){
		Criteria criteria = o_kpiDAO.createCriteria();
		criteria.createAlias("kpiRelaSm", "kpirelasm");
		criteria.setFetchMode("kpirelasm", FetchMode.SELECT);
		criteria.add(Restrictions.eq("isKpiCategory", "KPI"));
		criteria.add(Restrictions.eq("deleteStatus", true));
		//过滤掉指标停用状态
		criteria.add(Restrictions.eq("status.id", Contents.DICT_Y));
		criteria.add(Restrictions.eq("kpirelasm.strategyMap.id", id));
		return (List<Kpi>)criteria.list();
	}
	
	/**
	 * 获取指标下的所有风险事件
	 * 供其他模块使用风险事件数组来关联自己的业务表
	 * @param id 指标id,格式：‘1111’ 或者 ‘111’，‘2222’，‘333’
	 * @return 风险事件id数组
	 */
	public List<String> findRiskEventByKpiIds(List<String> ids){
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct r.id riskId ")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_kpi_kpi_rela_risk riskkpi on r.id = riskkpi.risk_id")
		.append(" LEFT JOIN t_kpi_kpi kpi on kpi.id=riskkpi.kpi_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus=:deleteStatus");
		if(ids != null && ids.size() > 0){
			if(ids.get(0).equalsIgnoreCase("root")){
				
			}else{
				sql.append(" and kpi.id in(:ids)");
			}
		}else{
			sql.append(" and 1=0 ");
		}
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("companyId", companyId);
		if(ids != null && ids.size() > 0){
			if(ids.get(0).equalsIgnoreCase("root")){
				
			}else{
				sqlQuery.setParameterList("ids", ids);
			}
		}
		sqlQuery.setParameter("deleteStatus", Contents.DELETE_STATUS_USEFUL);
		List<String> idLists = sqlQuery.list();
        return idLists;
	}
	
	/**
	 * 获取流程下的所有风险事件
	 * 供其他模块使用风险事件数组来关联自己的业务表
	 * @param id
	 * @return 风险事件id数组
	 */
	public List<String> findRiskEventByProcessId(String id){
		String companyId = UserContext.getUser().getCompanyid();
		Map<String,String> mapParams = new HashMap<String,String>();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct r.id,r.risk_code,r.risk_name,r.id_seq,r.last_modify_time,dict.dict_entry_value etrend,h.assessement_status,r.parent_id")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_processure_risk_processure riskpro on r.id = riskpro.risk_id")
		.append(" LEFT JOIN t_ic_processure pro on pro.id=riskpro.processure_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId ")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'");
		mapParams.put("companyId", companyId);
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				
			}else{
				sql.append(" and pro.id_seq like :id ");
				mapParams.put("id", "%."+id+".%");
			}
		}
				
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
		List<String> idList = new ArrayList<String>();
  		for(Object[] o : list){
  			String idStr= o[0].toString();
  			idList.add(idStr);
  		}
  		
        return idList;
	}
}
