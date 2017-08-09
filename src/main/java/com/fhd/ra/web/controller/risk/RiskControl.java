/**
 * RiskControl.java
 * com.fhd.risk.web.controller
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-1 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.ra.web.controller.risk;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.dao.Page;
import com.fhd.dao.sys.orgstructure.SysOrganizationDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.RiskRelaRisk;
import com.fhd.entity.risk.StrategyAdjustHistory;
import com.fhd.entity.sys.dic.DictEntryRelation;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.quaassess.ShowAssessBO;
import com.fhd.ra.business.risk.RiskAssessReportBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.ra.web.form.risk.RiskForm;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.OrgEmpTreeBO;
import com.fhd.sys.web.controller.orgstructure.OrganizationCmpControl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName:风险控制分发
 *
 * @author   zhengjunxiang
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-1		上午10:52:03
 *
 * @see 	 
 */
@Controller
public class RiskControl {
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private RiskOrgBO o_riskOrgBo;
	
	@Autowired
    private RiskCmpBO o_riskCmpBO;
	
	@Autowired
    private ShowAssessBO o_howAssessBO;

	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
    private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
    
	/**
	 * 字典表
	 */
	@Autowired
	private DictBO o_dicBo;
	
	/**
	 * 模板
	 */
	@Autowired
	private TemplateBO o_templateBO;
	
	/**
	 * 告警方案
	 */
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private RAssessPlanBO o_rAssessPlanBO;
	
	@Autowired
	private OrganizationCmpControl o_organizationCmpControl;
	
	@Autowired
	private OrgEmpTreeBO o_orgEmpTreeBO;
	
	@Autowired
    private RiskAssessReportBO o_riskAssessReportBO;
	
	@Autowired
    private SysOrganizationDAO o_sysOrganizationDAO;
	
	
	@ResponseBody
	@RequestMapping(value="/risk/findRiskById.f")
	public Map<String, Object> findRiskById(String riskId) {
		Risk risk = o_riskBO.findRiskById(riskId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", risk.getCode());
		map.put("name", risk.getName());
		map.put("parentName", risk.getParent()==null?"无":risk.getParent().getName());
		map.put("desc", risk.getDesc());
		
		String mDeptName = "";//责任部门
		StringBuffer aDeptName = new StringBuffer();//相关部门
		String respPositionName = "";//责任岗位
		String relaPositionName = "";//相关岗位
		String respName = "";//责任人
		String relaName = "";//相关人
		
		Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
		//如何通过criteria去直接查询优化？，先通过遍历实现
		for(RiskOrg org : riskOrgs){
			if(org.getType().equals("M")){	//责任部门
				if(null!=org.getSysOrganization()){
					mDeptName += org.getSysOrganization().getOrgname() + ",";
				}
			}
			if(org.getType().equals("A")){	//相关部门
				if(null!=org.getSysOrganization()){
					aDeptName.append("org.getSysOrganization().getOrgname()");
					aDeptName.append(",");
				}
			}
		}

		Set<RiskOrg> riskEmps = risk.getRiskEmp();
		for(RiskOrg org : riskEmps){
			if(org.getType().equals("M")){	//责任部门
				if(null!=org.getEmp()){
					respName += org.getEmp().getEmpname() + ",";
				}
			}
			if(org.getType().equals("A")){	//相关部门
				if(null!=org.getEmp()){
					relaName += org.getEmp().getEmpname() + ",";
				}
			}
		}
		if(!mDeptName.equals("")){
			mDeptName = mDeptName.substring(0, mDeptName.length()-1);
		}
		if(!aDeptName.equals("")){
			aDeptName.append("aDeptName.substring(0, aDeptName.length()-1)");
		}
		if(!respPositionName.equals("")){
			respPositionName = respPositionName.substring(0, respPositionName.length()-1);
		}
		if(!relaPositionName.equals("")){
			relaPositionName = relaPositionName.substring(0, relaPositionName.length()-1);
		}
		if(!respName.equals("")){
			respName = respName.substring(0, respName.length()-1);
		}
		if(!relaName.equals("")){
			relaName = relaName.substring(0, relaName.length()-1);
		}
		
		map.put("respDeptName", mDeptName);
		map.put("relaDeptName", aDeptName);
		map.put("respPositionName", respPositionName);	//责任岗位
		map.put("relaPositionName", relaPositionName);	//相关岗位
		map.put("respName", respName); //责任人
		map.put("relaName", relaName); //相关人
		
		String riskKpiName = "";//风险指标
		String influKpiName = "";//影响指标
		Set<KpiRelaRisk> kpiRelaRisks = risk.getKpiRelaRisks();
		for(KpiRelaRisk kpi : kpiRelaRisks){
			if(kpi.getType().equals("RM")){	//风险指标
				riskKpiName += kpi.getKpi().getName() + ",";
			}
			if(kpi.getType().equals("I")){	//影响指标
				influKpiName += kpi.getKpi().getName() + ",";
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

		String controlProcessureName = "";//控制流程
		String influProcessureName = "";//影响流程
		Set<ProcessRelaRisk> ProcessRelaRisks = risk.getRiskProcessures();
		for(ProcessRelaRisk processure : ProcessRelaRisks){
			if(processure.getType().equals("C")){	//控制流程
				controlProcessureName += processure.getProcess().getName() + ",";
			}
			if(processure.getType().equals("I")){	//影响指标
				influProcessureName += processure.getProcess().getName() + ",";
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
		
		//是否继承上级模板
		if(null == risk.getIsInherit()){
			map.put("isInherit","");
		}else{
			map.put("isInherit", o_dicBo.findDictEntryById(risk.getIsInherit()).getName());
		}
		
		//评估模板
		map.put("templeteName", risk.getTemplate()==null?"":risk.getTemplate().getName());
		
		//公式
		map.put("formulaDefine", risk.getFormulaDefine());
		
		//告警方案  下拉框
		map.put("alarmScenario",risk.getAlarmScenario()==null?"":risk.getAlarmScenario().getName());

		//是否定量
		if(null == risk.getIsFix()){
			map.put("isFix","");
		}else{
			map.put("isFix", o_dicBo.findDictEntryById(risk.getIsFix()).getName());
		}
		
		//是否启用
		if(null == risk.getIsUse()){
			map.put("isUse","");
		}else{
			map.put("isUse", o_dicBo.findDictEntryById(risk.getIsUse()).getName());
		}

		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/risk/findRiskEditInfoById.f")
	public Map<String, Object> findRiskEditInfoById(String riskId,String rangObjectDeptEmpId,String assessPlanId,String executionId) {
		Risk risk = o_riskBO.findRiskById(riskId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		//上级风险
		JSONArray jsonArray = new JSONArray();
		if(risk!=null && risk.getParent()!=null){
			JSONObject object = new JSONObject();
			object.put("id", risk.getParent().getId());
			jsonArray.add(object);
		}
		map.put("parentId", jsonArray.toString());
		map.put("code", null == risk?"":risk.getCode());
		map.put("name", null == risk?"":risk.getName());
		map.put("desc", null == risk?"":risk.getDesc());
		
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
			if(riskKind.length() != 0){
				riskKind.append(riskKind.substring(0, riskKind.length()-1));
			}
		}
		map.put("riskKind", riskKind);
		
		//定量分析
		map.put("isFix", risk.getIsFix());	
		
		//是否启用
		map.put("isUse", risk.getIsUse());
		
		//序号
		map.put("sort", risk.getSort());

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
		//map.put("templateId", risk.getTemplate()==null?"":risk.getTemplate().getId());
		try{
			map.put("templateId", risk.getTemplate()==null?"":risk.getTemplate().getId());
		}catch(ObjectNotFoundException e){
			map.put("templateId", "");
		}
//		//告警方案
//		map.put("alarmPlanId", risk.getAlarmScenario()==null?"":risk.getAlarmScenario().getId());
//		//监控频率
//		map.put("monitorFrequence", risk.getMonitorFrequence());
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
		//是否应对
		map.put("isAnswer", risk.getIsAnswer());
		//风险类别
		String riskType = "";
		Set<DictEntryRelation> riskTypes = risk.getRiskTypes();
		for(DictEntryRelation relation : riskTypes){
			riskType += relation.getDictEntry().getId() + ",";
		}
		if(!riskType.equals("")){
			riskType = riskType.substring(0, riskType.length()-1);
		}
		map.put("riskType", riskType);
		//影响期间
		String impactTime = "";
		Set<DictEntryRelation> impactTimes = risk.getImpactTimes();
		for(DictEntryRelation relation : impactTimes){
			impactTime += relation.getDictEntry().getId() + ",";
		}
		if(!impactTime.equals("")){
			impactTime = impactTime.substring(0, impactTime.length()-1);
		}
		map.put("impactTime", impactTime);
		//应对策略
		String responseStrategy = "";
		Set<DictEntryRelation> responseStrategys = risk.getResponseStrategys();
		for(DictEntryRelation relation : responseStrategys){
			responseStrategy += relation.getDictEntry().getId() + ",";
		}
		if(!responseStrategy.equals("")){
			responseStrategy = responseStrategy.substring(0, responseStrategy.length()-1);
		}
		map.put("responseStrategy", responseStrategy);
		//内部动因
		String innerReason = "";
		Set<DictEntryRelation> innerReasons = risk.getInnerReasons();
		for(DictEntryRelation relation : innerReasons){
			innerReason += relation.getDictEntry().getId() + ",";
		}
		if(!innerReason.equals("")){
			innerReason = innerReason.substring(0, innerReason.length()-1);
		}
		map.put("innerReason", innerReason);
		//外部动因
		String outterReason = "";
		Set<DictEntryRelation> outterReasons = risk.getOutterReasons();
		for(DictEntryRelation relation : outterReasons){
			outterReason += relation.getDictEntry().getId() + ",";
		}
		if(!outterReason.equals("")){
			outterReason = outterReason.substring(0, outterReason.length()-1);
		}
		map.put("outterReason", outterReason);
		//风险价值链
		String valueChain = "";
		Set<DictEntryRelation> valueChains = risk.getValueChains();
		for(DictEntryRelation relation : valueChains){
			valueChain += relation.getDictEntry().getId() + ",";
		}
		if(!valueChain.equals("")){
			valueChain = valueChain.substring(0, valueChain.length()-1);
		}
		map.put("valueChain", valueChain);
		
		//风险动因
		JSONArray riskReasonJsonArr = new JSONArray();
		Set<RiskRelaRisk> riskRelaRisks = risk.getrRisk();
		for(RiskRelaRisk r : riskRelaRisks){
			JSONObject obj = new JSONObject();
			obj.put("id", r.getRelaRisk().getId());
			riskReasonJsonArr.add(obj);
		}
		map.put("riskReason", riskReasonJsonArr.toString());
		
		//风险影响
		JSONArray riskInfluenceJsonArr = new JSONArray();
		riskRelaRisks = risk.getiRisk();
		for(RiskRelaRisk r : riskRelaRisks){
			JSONObject obj = new JSONObject();
			obj.put("id", r.getRelaRisk().getId());
			riskInfluenceJsonArr.add(obj);
		}
		map.put("riskInfluence", riskInfluenceJsonArr.toString());

		
		return map;
	}
	@ResponseBody
	@RequestMapping(value="/risk/findRiskEditInfoByIdSecurity.f")
	public Map<String, Object> findRiskEditInfoByIdSecurity(String objectId) {
		RiskScoreObject risk = o_riskCmpBO.findRiskObjectById(objectId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		//上级风险
		JSONArray jsonArray = new JSONArray();
		if(risk!=null && risk.getParent()!=null){
			JSONObject object = new JSONObject();
			object.put("id", risk.getParent().getId());
			jsonArray.add(object);
		}
		map.put("parentId", jsonArray.toString());
		map.put("code", null == risk?"":risk.getCode());
		map.put("name", null == risk?"":risk.getName());
		map.put("desc", null == risk?"":risk.getDesc());
		
		//责任部门/人和相关部门/人
		JSONObject object = new JSONObject();
		JSONArray respDeptName = new JSONArray();
		JSONArray relaDeptName = new JSONArray();
		Set<RiskOrgTemp> riskOrgs = risk.getRiskOrgTemps();
		for(RiskOrgTemp riskOrg : riskOrgs){
			if(riskOrg.getType().equals("M")){	//责任部门/人
				object = new JSONObject();
				SysOrganization sysOrganizationIte = riskOrg.getSysOrganization();
				if(sysOrganizationIte!=null){
					object.put("deptid", sysOrganizationIte.getId());
					object.put("deptno", sysOrganizationIte.getOrgcode());
					object.put("deptname", sysOrganizationIte.getOrgname());
					map.put("respDeptLabel",sysOrganizationIte.getOrgname());
				}
				SysEmployee empIte = riskOrg.getEmp();
				if(empIte!=null){
					object.put("empid", empIte.getId());
					object.put("empno", empIte.getEmpcode());
					object.put("empname", empIte.getEmpname());
					
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
					object.put("deptno", sysOrganizationIte.getOrgcode());
					object.put("deptname", sysOrganizationIte.getOrgname());
				}
				SysEmployee empIte = riskOrg.getEmp();
				if(empIte!=null){
					try {
						object.put("empid", empIte.getId());
						object.put("empno", empIte.getEmpcode());
	                    object.put("empname", empIte.getEmpname());
					} catch (Exception e) {
						object.put("empid", "");
					}
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
		if(!riskKind.toString().equals("")){
			riskKind.append(riskKind.substring(0, riskKind.length()-1));
		}
		map.put("riskKind", riskKind);
		
		//定量分析
		map.put("isFix", risk.getIsFix());	
		
		//是否启用
		map.put("isUse", risk.getIsUse());
		
		//是否启用
		map.put("sort", risk.getSort());
		
		//是否计算
		map.put("calcStr", risk.getCalc() == null?"":risk.getCalc().getId());
		
		map.put("gatherfrequenceDictType", risk.getGatherFrequence()==null?"":risk.getGatherFrequence().getId());
		map.put("gatherfrequenceCron", risk.getGatherDayFormulr()==null?"":risk.getGatherDayFormulr());
		map.put("gatherfrequence", risk.getGatherDayFormulrShow()==null?"":risk.getGatherDayFormulrShow().split("\\@")[0]);
		map.put("gatherfrequenceRule", risk.getGatherDayFormulrShow()==null?"":risk.getGatherDayFormulrShow().split("\\@")[1]);
		
		//采集频率
		map.put("gatherfrequenceDict", risk.getGatherFrequence()==null?"":risk.getGatherFrequence().getId());
		//延迟天数
		map.put("resultCollectInterval", risk.getResultCollectInterval());

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
		try{
			map.put("templateId", risk.getTemplate()==null?"":risk.getTemplate().getId());
		}catch(ObjectNotFoundException e){
			map.put("templateId", "");
		}
		
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
		//是否应对
		map.put("isAnswer", risk.getIsAnswer());
		//风险类别
		String riskType = "";
		Set<DictEntryRelation> riskTypes = risk.getRiskTypes();
		for(DictEntryRelation relation : riskTypes){
			riskType += relation.getDictEntry().getId() + ",";
		}
		if(!riskType.equals("")){
			riskType = riskType.substring(0, riskType.length()-1);
		}
		map.put("riskType", riskType);
		//影响期间
		String impactTime = "";
		Set<DictEntryRelation> impactTimes = risk.getImpactTimes();
		for(DictEntryRelation relation : impactTimes){
			impactTime += relation.getDictEntry().getId() + ",";
		}
		if(!impactTime.equals("")){
			impactTime = impactTime.substring(0, impactTime.length()-1);
		}
		map.put("impactTime", impactTime);
		//应对策略
		String responseStrategy = "";
		Set<DictEntryRelation> responseStrategys = risk.getResponseStrategys();
		for(DictEntryRelation relation : responseStrategys){
			responseStrategy += relation.getDictEntry().getId() + ",";
		}
		if(!responseStrategy.equals("")){
			responseStrategy = responseStrategy.substring(0, responseStrategy.length()-1);
		}
		map.put("responseStrategy", responseStrategy);
		//内部动因
		String innerReason = "";
		Set<DictEntryRelation> innerReasons = risk.getInnerReasons();
		for(DictEntryRelation relation : innerReasons){
			innerReason += relation.getDictEntry().getId() + ",";
		}
		if(!innerReason.equals("")){
			innerReason = innerReason.substring(0, innerReason.length()-1);
		}
		map.put("innerReason", innerReason);
		//外部动因
		String outterReason = "";
		Set<DictEntryRelation> outterReasons = risk.getOutterReasons();
		for(DictEntryRelation relation : outterReasons){
			outterReason += relation.getDictEntry().getId() + ",";
		}
		if(!outterReason.equals("")){
			outterReason = outterReason.substring(0, outterReason.length()-1);
		}
		map.put("outterReason", outterReason);
		//风险价值链
		String valueChain = "";
		Set<DictEntryRelation> valueChains = risk.getValueChains();
		for(DictEntryRelation relation : valueChains){
			valueChain += relation.getDictEntry().getId() + ",";
		}
		if(!valueChain.equals("")){
			valueChain = valueChain.substring(0, valueChain.length()-1);
		}
		map.put("valueChain", valueChain);
		
		//风险动因
		JSONArray riskReasonJsonArr = new JSONArray();
		Set<RiskRelaRisk> riskRelaRisks = risk.getrRisk();
		for(RiskRelaRisk r : riskRelaRisks){
			JSONObject obj = new JSONObject();
			obj.put("id", r.getRelaRisk().getId());
			riskReasonJsonArr.add(obj);
		}
		map.put("riskReason", riskReasonJsonArr.toString());
		
		//风险影响
		JSONArray riskInfluenceJsonArr = new JSONArray();
		riskRelaRisks = risk.getiRisk();
		for(RiskRelaRisk r : riskRelaRisks){
			JSONObject obj = new JSONObject();
			obj.put("id", r.getRelaRisk().getId());
			riskInfluenceJsonArr.add(obj);
		}
		map.put("riskInfluence", riskInfluenceJsonArr.toString());
		
		//风险分类
		JSONArray riskStructureJsonArr = new JSONArray();
		riskRelaRisks = risk.getRbsRisk();
		for(RiskRelaRisk r : riskRelaRisks){
			JSONObject obj = new JSONObject();
			obj.put("id", r.getRelaRisk().getId());
			riskStructureJsonArr.add(obj);
		}
		map.put("riskStructure", riskStructureJsonArr.toString());
		
		//告警方案  下拉框
		map.put("alarmPlanId",risk.getAlarmScenario()==null?"":risk.getAlarmScenario().getId());
		
		//小数点位数
		map.put("digit", risk.getDigit());
				
		return map;
	}
	
	
	
	/*
	 * 风险评估意见，应对意见，及分值查询
	 * 郭鹏
	 * 20170509
	 * */
	@ResponseBody
	@RequestMapping(value="/risk/findRiskEditInfoByIdForSecrecy.f")
	public HashMap<String, Object> findRiskEditInfoByIdForSecrecy(String riskId,String assessPlanId,String executionId) {
		assessPlanId =  o_jbpmOperate.getVariable(executionId,"id");; //通过流程ID得到评估计划ID
		String scoreEmpId = UserContext.getUser().getEmpid(); //人员ID
		String companyid = UserContext.getUser().getCompanyid(); //公司ID
		
		ArrayList<HashMap<String, Object>> results=o_howAssessBO.findRiskByScoreEmpIdForSecrecy(riskId, scoreEmpId, assessPlanId, companyid);
	
		
		return results.get(0);
	}
	
	
	
	/**
	 * 删除风险前，验证是否可以删除
	 * @author zhengjunxiang
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/risk/findRiskCanBeRemoved.f")
	public Map<String,Object> findRiskCanBeRemoved(String id){
		Map<String,Object> resultMap = new HashMap<String,Object>();

		Risk risk = o_riskCmpBO.findRiskById(id);
		String empId = UserContext.getUser().getEmpid();
		if(o_riskCmpBO.riskHasChildren(id)){	//风险下是否有子风险
			resultMap.put("success", false);
			resultMap.put("type", "hasChildren");
			return resultMap;
		}else if(o_riskAdjustHistoryBO.findRiskAdjustHistoryByRiskId(id)){	//风险是不是被评估模块引用
			resultMap.put("success", false);
			resultMap.put("type", "hasRef");
			return resultMap;
		}else if(!empId.equals(risk.getCreateBy())){	//风险是不是被评估模块引用
			resultMap.put("success", false);
			resultMap.put("type", "notSelfCreate");
			return resultMap;
		}else{
			resultMap.put("success", true);
			resultMap.put("type", "ok");
			return resultMap;
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/risk/risk/removeRiskById.f")
	public boolean removeRiskById(String ids){
		o_riskBO.removeRiskByIds(ids);
		return true;
	}
	
	/**
	 * 根据风险id获取风险历史记录
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findRiskAdjustHistoryById.f")
	public Map<String, Object> findRiskAdjustHistoryById(int start, int limit, String query, String sort,String id){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(id)) {
	        String dir = "DESC";
	        String sortColumn = "adjustTime";	//时间段
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			Page<RiskAdjustHistory> page = new Page<RiskAdjustHistory>();
	        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	        page.setPageSize(limit);
	        page = o_riskBO.findRiskAdjustHistoryBySome(id,page,sortColumn,dir,query);
	        
			List<RiskAdjustHistory> list = page.getResult();
			Map<String, Object> item = null;
			for (RiskAdjustHistory history : list) {
				item = new HashMap<String, Object>();
				item.put("id",history.getId());
				if(history.getEtrend() == null || history.getEtrend().equals("")){
					item.put("etrend","");
				}else{
					item.put("etrend",o_dicBo.findDictEntryById(history.getEtrend()).getValue());
				}
				if(history.getAdjustType().equals("0") || history.getAdjustType().equals("2")){//评估计划，或评估计划修改
				    if(history.getRiskAssessPlan() == null){
				        item.put("adjustType","评估计划（）");
				    }else{
				        RiskAssessPlan rap = o_rAssessPlanBO.findRiskAssessPlanById(history.getRiskAssessPlan().getId());
                        item.put("adjustType","评估计划（"+rap.getPlanName()+"）");
				    }
                }else if(history.getAdjustType().equals("1")){
                    item.put("adjustType","手动输入");
                }else if(history.getAdjustType().equals("3")){
                    item.put("adjustType","公式定义");
                }
				item.put("adjustTypeValue",history.getAdjustType());
				DecimalFormat fnum = new DecimalFormat("0.00"); 
				String status=fnum.format(history.getStatus()==null?0:history.getStatus()); 
				item.put("riskStatus",status);
				item.put("assessementStatus",history.getAssessementStatus());
				item.put("year", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(0, 4));
				item.put("month", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(5, 7));
				
				if(history.getTimePeriod()==null){
					item.put("dateRange","");
				}else{
					String dataRange = history.getTimePeriod().getYear()+"年"+history.getTimePeriod().getMonth()+"月";
					item.put("dateRange",dataRange);
				}
				item.put("status", history.getStatus());
				//模板
                item.put("template",history.getTemplate()==null?"":history.getTemplate().getName()+"");
                //公式计算
                item.put("calculateFormula",StringUtils.isBlank(history.getCalculateFormula())?"":history.getCalculateFormula()+"");
                
				item.put("templateid",history.getTemplate()==null?"":history.getTemplate().getId()+"");
				datas.add(item);
			}
			map.put("totalCount", page.getTotalItems());
			map.put("datas", datas);
		}

		return map;
	}
	
	/**
	 * 根据风险id获取风险历史记录
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findEventById.f")
	public Map<String, Object> findRiskEventById(int start, int limit, String query, String sort,String riskId){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(riskId)) {
	        String dir = "ASC";
	        String sortColumn = "sort";
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			Page<Risk> page = new Page<Risk>();
	        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	        page.setPageSize(limit);
	        page = o_riskBO.findRiskEventBySome(riskId,page,sortColumn,dir,query);
	        
			List<Risk> list = page.getResult();
			Map<String, Object> item = null;
			for (Risk event: list) {
				item = new HashMap<String, Object>();
				item.put("id",event.getId().toString());
				item.put("name",event.getName());
				//风险的责任部门
				StringBuffer respDeptName = new StringBuffer();
				Risk risk =o_riskBO.findRiskById(event.getId());
				Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
				for(RiskOrg org : riskOrgs){
					if(org.getType().equals("M")){	//责任部门
						if(null!=org.getSysOrganization()){
						    respDeptName.append(org.getSysOrganization().getOrgname());
						    respDeptName.append(",");
						}
					}
				}
				if(!respDeptName.equals("")){
					respDeptName.append(respDeptName.substring(0,respDeptName.length()-1));
				}
				item.put("respDeptName",respDeptName);
				//最新评估记录
				RiskAdjustHistory latestAdjustHistory = o_riskBO.findLatestRiskAdjustHistoryByRiskId(event.getId());
				if(latestAdjustHistory==null){
					item.put("assessementStatus","");
					item.put("etrend","");
					item.put("status","");
					item.put("adjustTime","");
				}else{
					item.put("assessementStatus",latestAdjustHistory.getAssessementStatus());
					item.put("etrend",latestAdjustHistory.getEtrend());
					item.put("status",latestAdjustHistory.getStatus());
					item.put("adjustTime",latestAdjustHistory.getAdjustTime()==null?"":latestAdjustHistory.getAdjustTime().toString());
				}
				
				datas.add(item);
			}
			
			map.put("totalCount",page.getTotalItems());
			map.put("datas", datas);
		}
		return map;
	}
	
	/**
	 * 风险树的构建
	 * @author 郑军祥
	 * @param id			nodeId
	 * @param canChecked  	是否多选
	 * @param query		  	查询条件
	 * @param rbs			true表示值查询风险，不带有风险事件；false带有风险事件
	 * @return
	 */
    @ResponseBody
	@RequestMapping(value = "/risk/riskTreeLoader")
    public List<Map<String, Object>> riskTreeLoader(String node, String query, Boolean rbs, Boolean canChecked,String chooseId){
    	return o_riskBO.riskTreeLoader(node, query, rbs, canChecked, chooseId);
    }
    /**
	 * 风险树组件初始化
	 * @author 郑军祥
	*/
	@ResponseBody
    @RequestMapping(value = "/risk/initRiskByIds")
    public Map<String,Object> initRiskByIds(String ids) throws Exception{

		Set<String> idSet = new HashSet<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(ids)){
			JSONArray jsonArray=JSONArray.fromObject(ids);
			if(jsonArray.size()==0){
				return null;
			}
			
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject=jsonArray.getJSONObject(i);
				if(StringUtils.isNotBlank(jsonObject.getString("id"))){
					idSet.add(jsonObject.getString("id"));
				}
			}
			List<HashMap<String, Object>> list = o_riskBO.findRiskByIdSet(idSet);
			
			map.put("success", true);
			map.put("data", list);
		}
		return map;
	}
    
    @ResponseBody
	@RequestMapping(value = "/risk/enableRisk")
    public void enableRisk(String ids,String isUsed,HttpServletResponse response) throws Exception {
    	PrintWriter out = response.getWriter();
		String isSave = "false";
		o_riskBO.enableRisk(ids, isUsed);
		isSave = "true";
		out.write(isSave);
		out.close();
    }
    
    /**
	 * 查询目标下的风险列表
	 * @author 郑军祥 2013-6-6
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findRiskByStrategyMapId")
	public Map<String, Object> findRiskByStrategyMapId(int start, int limit, String query, String sort,String id){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(id)) {
	        String dir = "DESC";
	        String sortColumn = "id";
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			Page<Risk> page = new Page<Risk>();
	        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	        page.setPageSize(limit);
	        page = o_riskBO.findRiskEventByStrateMapId(id, page, sortColumn, dir, query);
	        
			List<Risk> list = page.getResult();
			Map<String, Object> item = null;
			
			for (Risk event: list) {
				item = new HashMap<String, Object>();
				item.put("id",event.getId().toString());
				item.put("name",event.getName());				
				//风险的责任部门
				StringBuffer respDeptName = new StringBuffer();
				Risk risk =o_riskBO.findRiskById(event.getId());
				Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
				for(RiskOrg org : riskOrgs){
					if(org.getType().equals("M")){	//责任部门
						if(null!=org.getSysOrganization()){
							respDeptName.append(org.getSysOrganization().getOrgname());
							respDeptName.append("");
						}
					}
				}
				if(!respDeptName.equals("")){
					respDeptName.append(respDeptName.substring(0,respDeptName.length()-1));
				}
				item.put("respDeptName",respDeptName);
				//最新评估记录
				RiskAdjustHistory latestAdjustHistory = o_riskBO.findLatestRiskAdjustHistoryByRiskId(event.getId());
				if(latestAdjustHistory==null){
					item.put("assessementStatus","");
					item.put("etrend","");
					item.put("status","");
					item.put("adjustTime","");
				}else{
					item.put("assessementStatus",latestAdjustHistory.getAssessementStatus());
					item.put("etrend",latestAdjustHistory.getEtrend());
					item.put("status",latestAdjustHistory.getStatus());
					item.put("adjustTime",latestAdjustHistory.getAdjustTime()==null?"":latestAdjustHistory.getAdjustTime().toString());
				}
				
				datas.add(item);
			}
			
			map.put("totalCount",page.getTotalItems());
			map.put("datas", datas);
		}
		return map;
	}
	
	/**
	 * 根据目标id获取目标历史记录
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findStrategyAdjustHistoryByStrategyMapId")
	public Map<String, Object> findStrategyAdjustHistoryByStrategyMapId(int start, int limit, String query, String sort,String id,String schm){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(id)) {
	        String dir = "DESC";
	        String sortColumn = "adjustTime";
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			//判断传入的节点是目标，还是目标指标
			String[] ids = id.split("_");
			if(ids.length>1){
				Page<KpiAdjustHistory> page = new Page<KpiAdjustHistory>();
		        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		        page.setPageSize(limit);
		        page = o_riskBO.findKpiHistoryByKpiId(ids[1],page,sortColumn,dir,query,schm);
		        
				List<KpiAdjustHistory> list = page.getResult();
				Map<String, Object> item = null;
				for (KpiAdjustHistory history : list) {
					item = new HashMap<String, Object>();
					item.put("id",history.getId());
					if(history.getEtrend() == null || history.getEtrend().equals("")){
						item.put("etrend","");
					}else{
						item.put("etrend",o_dicBo.findDictEntryById(history.getEtrend()).getValue());
					}
					if(history.getAdjustType().equals("0")){
					    if(history.getRiskAssessPlan() == null){
	                        item.put("adjustType","评估计划（）");
	                    }else{
	                        RiskAssessPlan rap = o_rAssessPlanBO.findRiskAssessPlanById(history.getRiskAssessPlan().getId());
	                        item.put("adjustType","评估计划（"+rap.getPlanName()+"）");
	                    }
	                }else if(history.getAdjustType().equals("1")){
	                    item.put("adjustType","手动输入");
	                }else if(history.getAdjustType().equals("3")){
	                    item.put("adjustType","公式定义");
	                }
					DecimalFormat fnum = new DecimalFormat("0.00"); 
	                String status=fnum.format(history.getRiskStatus()); 
	                item.put("riskStatus",status);
					item.put("assessementStatus",history.getAssessementStatus());
					item.put("year", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(0, 4));
	                item.put("month", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(5, 7));
					
					if(history.getTimePeriod()==null){
						item.put("dateRange","");
					}else{
						String dataRange = history.getTimePeriod().getYear()+"年"+history.getTimePeriod().getMonth()+"月";
						item.put("dateRange",dataRange);
					}
					//计算公式
					item.put("calculateFormula",null==history.getCalculateFormula()?"":history.getCalculateFormula());
					datas.add(item);
				}
				map.put("totalCount", page.getTotalItems());
				map.put("datas", datas);
			}else{
				Page<StrategyAdjustHistory> page = new Page<StrategyAdjustHistory>();
		        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		        page.setPageSize(limit);
		        page = o_riskBO.findStrategyHistoryByStrateMapId(id,page,sortColumn,dir,query,schm);
		        
				List<StrategyAdjustHistory> list = page.getResult();
				Map<String, Object> item = null;
				for (StrategyAdjustHistory history : list) {
					item = new HashMap<String, Object>();
					item.put("id",history.getId());
					if(history.getEtrend() == null || history.getEtrend().equals("")){
						item.put("etrend","");
					}else{
						item.put("etrend",o_dicBo.findDictEntryById(history.getEtrend()).getValue());
					}
					if(history.getAdjustType().equals("0")){
					    if(history.getRiskAssessPlan() == null){
	                        item.put("adjustType","评估计划（）");
	                    }else{
	                        RiskAssessPlan rap = o_rAssessPlanBO.findRiskAssessPlanById(history.getRiskAssessPlan().getId());
	                        item.put("adjustType","评估计划（"+rap.getPlanName()+"）");
	                    }
	                }else if(history.getAdjustType().equals("1")){
	                    item.put("adjustType","手动输入");
	                }else if(history.getAdjustType().equals("3")){
	                    item.put("adjustType","公式定义");
	                }
					DecimalFormat fnum = new DecimalFormat("0.00"); 
                    String status=fnum.format(history.getRiskStatus()); 
                    item.put("riskStatus",status);
					item.put("assessementStatus",history.getAssessementStatus());
					item.put("year", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(0, 4));
	                item.put("month", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(5, 7));
					
					if(history.getTimePeriod()==null){
						item.put("dateRange","");
					}else{
						String dataRange = history.getTimePeriod().getYear()+"年"+history.getTimePeriod().getMonth()+"月";
						item.put("dateRange",dataRange);
					}
					//计算公式
					item.put("calculateFormula",null==history.getCalculateFormula()?"":history.getCalculateFormula());
					datas.add(item);
				}
				map.put("totalCount", page.getTotalItems());
				map.put("datas", datas);
			}
		}

		return map;
	}
	
	/**
	 * 查询组织下的风险列表
	 * @author 郑军祥 2013-6-6
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findRiskByOrgId")
	public Map<String, Object> findRiskByOrgId(int start, int limit, String query, String sort,String id){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(id)) {
	        String dir = "DESC";
	        String sortColumn = "id";
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			Page<Risk> page = new Page<Risk>();
	        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	        page.setPageSize(limit);
	        page = o_riskBO.findRiskEventByOrgId(id, page, sortColumn, dir, query);
	        
			List<Risk> list = page.getResult();
			Map<String, Object> item = null;
			
			for (Risk event: list) {
				item = new HashMap<String, Object>();
				item.put("id",event.getId().toString());
				item.put("name",event.getName());				
				//风险的责任部门
				StringBuffer respDeptName = new StringBuffer();
				Risk risk =o_riskBO.findRiskById(event.getId());
				Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
				for(RiskOrg org : riskOrgs){
					if(org.getType().equals("M")){	//责任部门
						if(null!=org.getSysOrganization()){
							respDeptName.append(org.getSysOrganization().getOrgname());
							respDeptName.append(",");
						}
					}
				}
				if(!respDeptName.equals("")){
					respDeptName.append(respDeptName.substring(0,respDeptName.length()-1));
				}
				item.put("respDeptName",respDeptName);
				//最新评估记录
				RiskAdjustHistory latestAdjustHistory = o_riskBO.findLatestRiskAdjustHistoryByRiskId(event.getId());
				if(latestAdjustHistory==null){
					item.put("assessementStatus","");
					item.put("etrend","");
					item.put("status","");
					item.put("adjustTime","");
				}else{
					item.put("assessementStatus",latestAdjustHistory.getAssessementStatus());
					item.put("etrend",latestAdjustHistory.getEtrend());
					item.put("status",latestAdjustHistory.getStatus());
					item.put("adjustTime",latestAdjustHistory.getAdjustTime()==null?"":latestAdjustHistory.getAdjustTime().toString());
				}
				
				datas.add(item);
			}
			
			map.put("totalCount",page.getTotalItems());
			map.put("datas", datas);
		}
		return map;
	}
	
	/**
	 * 根据目标id获取目标历史记录
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findOrgAdjustHistoryByOrgId")
	public Map<String, Object> findOrgAdjustHistoryByOrgId(int start, int limit, String query, String sort,String id, String schm){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(id)) {
	        String dir = "DESC";
	        String sortColumn = "adjustTime";
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			Page<OrgAdjustHistory> page = new Page<OrgAdjustHistory>();
	        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	        page.setPageSize(limit);
	        page = o_riskBO.findOrgHistoryByOrgId(id,page,sortColumn,dir,query,schm);//schm 风险分库标识
	        
			List<OrgAdjustHistory> list = page.getResult();
			Map<String, Object> item = null;
			for (OrgAdjustHistory history : list) {
				item = new HashMap<String, Object>();
				item.put("id",history.getId());
				if(history.getEtrend() == null || history.getEtrend().equals("")){
					item.put("etrend","");
				}else{
					item.put("etrend",o_dicBo.findDictEntryById(history.getEtrend()).getValue());
				}
				item.put("assessementStatus",history.getAssessementStatus());
				if(history.getAdjustType().equals("0")){
				    if(history.getRiskAssessPlan() == null){
                        item.put("adjustType","评估计划（）");
                    }else{
                        RiskAssessPlan rap = o_rAssessPlanBO.findRiskAssessPlanById(history.getRiskAssessPlan().getId());
                        item.put("adjustType","评估计划（"+rap.getPlanName()+"）");
                    }
				}else if(history.getAdjustType().equals("1")){
				    item.put("adjustType","手动输入");
				}else if(history.getAdjustType().equals("3")){
                    item.put("adjustType","公式定义");
                }
				DecimalFormat fnum = new DecimalFormat("0.00"); 
                String status=fnum.format(history.getRiskStatus()); 
                item.put("riskStatus",status);
                item.put("year", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(0, 4));
                item.put("month", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(5, 7));
				
				if(history.getTimePeriod()==null){
					item.put("dateRange","");
				}else{
					String dataRange = history.getTimePeriod().getYear()+"年"+history.getTimePeriod().getMonth()+"月";
					item.put("dateRange",dataRange);
				}
				//计算公式
				item.put("calculateFormula",null==history.getCalculateFormula()?"":history.getCalculateFormula());
				datas.add(item);
			}
			map.put("totalCount", page.getTotalItems());
			map.put("datas", datas);
		}

		return map;
	}
	
	/**
	 * 查询流程下的风险列表
	 * @author 郑军祥 2013-6-6
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findRiskByProcessId")
	public Map<String, Object> findRiskByPrcessId(int start, int limit, String query, String sort,String id){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(id)) {
	        String dir = "DESC";
	        String sortColumn = "id";
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			Page<Risk> page = new Page<Risk>();
	        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	        page.setPageSize(limit);
	        page = o_riskBO.findRiskEventByProcessId(id, page, sortColumn, dir, query);
	        
			List<Risk> list = page.getResult();
			Map<String, Object> item = null;
			
			for (Risk event: list) {
				item = new HashMap<String, Object>();
				item.put("id",event.getId().toString());
				item.put("name",event.getName());				
				//风险的责任部门
				StringBuffer respDeptName = new StringBuffer();
				Risk risk =o_riskBO.findRiskById(event.getId());
				Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
				for(RiskOrg org : riskOrgs){
					if(org.getType().equals("M")){	//责任部门
						if(null!=org.getSysOrganization()){
							respDeptName.append(org.getSysOrganization().getOrgname());
							respDeptName.append(",");
						}
					}
				}
				if(!respDeptName.equals("")){
					respDeptName.append(respDeptName.substring(0,respDeptName.length()-1));
				}
				item.put("respDeptName",respDeptName);
				//最新评估记录
				RiskAdjustHistory latestAdjustHistory = o_riskBO.findLatestRiskAdjustHistoryByRiskId(event.getId());
				if(latestAdjustHistory==null){
					item.put("assessementStatus","");
					item.put("etrend","");
					item.put("status","");
					item.put("adjustTime","");
				}else{
					item.put("assessementStatus",latestAdjustHistory.getAssessementStatus());
					item.put("etrend",latestAdjustHistory.getEtrend());
					item.put("status",latestAdjustHistory.getStatus());
					item.put("adjustTime",latestAdjustHistory.getAdjustTime()==null?"":latestAdjustHistory.getAdjustTime().toString());
				}
				
				datas.add(item);
			}
			
			map.put("totalCount",page.getTotalItems());
			map.put("datas", datas);
		}
		return map;
	}
	
	/**
	 * 根据目标id获取目标历史记录
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/findProcessAdjustHistoryByProcessId")
	public Map<String, Object> findProcessAdjustHistoryByProcessId(int start, int limit, String query, String sort,String id,String schm){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		
		if (StringUtils.isNotBlank(id)) {
	        String dir = "DESC";
	        String sortColumn = "adjustTime";
			if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");//按照哪个字段排序
                    dir = jsobj.getString("direction");//排序方向
                }
            }
			
			Page<ProcessAdjustHistory> page = new Page<ProcessAdjustHistory>();
	        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	        page.setPageSize(limit);
	        page = o_riskBO.findProcessHistoryByProcessId(id,page,sortColumn,dir,query,schm);
	        
			List<ProcessAdjustHistory> list = page.getResult();
			Map<String, Object> item = null;
			for (ProcessAdjustHistory history : list) {
				item = new HashMap<String, Object>();
				item.put("id",history.getId());
				if(history.getEtrend() == null || history.getEtrend().equals("")){
					item.put("etrend","");
				}else{
					item.put("etrend",o_dicBo.findDictEntryById(history.getEtrend()).getValue());
				}
				if(history.getAdjustType().equals("0")){
				    if(history.getRiskAssessPlan() == null){
                        item.put("adjustType","评估计划（）");
                    }else{
                        RiskAssessPlan rap = o_rAssessPlanBO.findRiskAssessPlanById(history.getRiskAssessPlan().getId());
                        item.put("adjustType","评估计划（"+rap.getPlanName()+"）");
                    }
                }else if(history.getAdjustType().equals("1")){
                    item.put("adjustType","手动输入");
                }else if(history.getAdjustType().equals("3")){
                    item.put("adjustType","公式定义");
                }
				DecimalFormat fnum = new DecimalFormat("0.00"); 
                String status=fnum.format(history.getRiskStatus()); 
                item.put("riskStatus",status);
				item.put("assessementStatus",history.getAssessementStatus());
				item.put("year", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(0, 4));
                item.put("month", history.getAdjustTime()==null?"":history.getAdjustTime().toString().substring(5, 7));
				
				if(history.getTimePeriod()==null){
					item.put("dateRange","");
				}else{
					String dataRange = history.getTimePeriod().getYear()+"年"+history.getTimePeriod().getMonth()+"月";
					item.put("dateRange",dataRange);
				}
				//计算公式
                item.put("calculateFormula",null==history.getCalculateFormula()?"":history.getCalculateFormula());
				datas.add(item);
			}
			map.put("totalCount", page.getTotalItems());
			map.put("datas", datas);
		}

		return map;
	}
	
	/**
	 * 修改风险  给小金提供
	 * @author zhengjunxiang
	 * @param riskForm
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/risk/updateRiskInfo.f")
	public void updateRiskInfo(RiskForm riskForm,String id,HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		String isSave = "false";
		
		Risk risk = o_riskBO.findRiskById(id);
		//上级风险
		JSONArray arr = JSONArray.fromObject(riskForm.getParentId());
		if(arr != null){
			JSONObject obj = (JSONObject)arr.get(0);
			risk.setParent(o_riskBO.findRiskById(obj.getString("id")));
		}
		//名称
		risk.setName(riskForm.getName());
		
		//责任部门，相关部门，责任人，相关人，风险指标，影响指标，控制流程，影响流程
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		String influKpiName = riskForm.getInfluKpiName();
		String influProcessureName = riskForm.getInfluProcessureName();

		
		//更新
		o_riskBO.updateRisk(risk, respDeptName, relaDeptName,influKpiName,influProcessureName);
		isSave = "true";
		out.write(isSave);
		out.close();

	}
	
	
	/**
	 * 风险分类概述报表页面，查询子节点
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2013-10-23
	 * @since Ver 1.1
	 */
	@ResponseBody
    @RequestMapping(value = "/risk/risk/reportriskview.f")
    public Map<String, Object> reportRiskView(String node,boolean isGroup,String query) throws Exception{
	    Map<String, Object> map = new HashMap<String, Object>();
	    List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> orgList = o_organizationCmpControl.deptTreeLoader(node, false, true, null, query, false, false,null);
        for(Map<String, Object> org : orgList){
            String companyId = node;
            if(o_sysOrganizationDAO.get(org.get("id").toString()).getCompany() != null){
                companyId = o_sysOrganizationDAO.get(org.get("id").toString()).getCompany().getId();
            }
            Integer[] info = this.getOrgSummaryInteger(org.get("id").toString(),companyId);
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", org.get("id"));
            item.put("companyid", companyId);
            item.put("orgname", org.get("text"));
            item.put("leaf", org.get("leaf"));
            if(null != info){
                item.put("riskcount", info[0]);
                item.put("highrisk", info[1]);
                item.put("attentionrisk", info[2]);
                item.put("saferisk", info[3]);
            }else{
                item.put("riskcount", "0");
                item.put("highrisk", "0");
                item.put("attentionrisk", "0");
                item.put("saferisk", "0");
            }
            children.add(item);
        }
	    map.put("children", children);
	    return map;
	}
	
	/**
     * 风险分类概述报表页面，查询根节点
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-10-23
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/risk/risk/reportriskviewroot.f")
    public Map<String, Object> reportRiskViewRoot(boolean isGroup) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        SysOrganization root = o_orgEmpTreeBO.findTreeRoot(isGroup);
        Integer[] info = getOrgSummaryInteger(root.getId(),root.getId());
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", root.getId());
        item.put("companyid", root.getId());
        item.put("orgname", root.getOrgname());
        item.put("leaf", false);
        item.put("expanded", true);
        if(null != info){
            item.put("riskcount", info[0]);
            item.put("highrisk", info[1]);
            item.put("attentionrisk", info[2]);
            item.put("saferisk", info[3]);
        }else{
            item.put("riskcount", "0");
            item.put("highrisk", "0");
            item.put("attentionrisk", "0");
            item.put("saferisk", "0");
        }
        map.put("organization", item);
        return map;
    }
	
    private Integer[] getOrgSummaryInteger(String node,String companyId){
        Integer[] summary = new Integer[]{0,0,0,0}; //统计的集合
        List<Map> list = o_riskCmpBO.findRiskEventByOrgId(companyId, node, "all", null, null, null, null, null).getResult();
        for(Map o : list){
            String id  = o.get("id").toString();
            String status = o.get("assessementStatus")==null?"":o.get("assessementStatus").toString();
            //计算风险红黄绿灯的个数
            if(Contents.RISK_LEVEL_HIGH.equals(status)){
                summary[1] = summary[1] + 1;
            }else if(Contents.RISK_LEVEL_MIDDLE.equals(status)){
                summary[2] = summary[2] + 1;
            }else if(Contents.RISK_LEVEL_LOW.equals(status)){
                summary[3] = summary[3] + 1;
            }else{}
            summary[0] = summary[0] + 1;    //风险事件的总数也包括没有灯的事件个数
        }
        return summary;
    }
    
    @ResponseBody
    @RequestMapping(value = "/risk/risk/showreportriskgrid.f")
    public Map<String, Object> showreportriskgrid(int start, int limit, String query, String sort,String companyid,String orgid,String type){
        Map<String, Object> map = new HashMap<String, Object>();
        Page<Map> page = o_riskCmpBO.findRiskEventByOrgId(companyid, orgid, type, query, null, null, start, limit);
        List<Map> list = page.getResult();
        map.put("totalCount",page.getTotalItems());
        map.put("datas", list);
        return map;
    }
	
	/**
	 * 导出部门或集团的报表
	 * 
	 * @author 张健
	 * @param type 是否是集团
	 * @return 
	 * @date 2013-12-18
	 * @since Ver 1.1
	 */
	@ResponseBody
    @RequestMapping(value = "/risk/risk/exportreportriskview.f")
	public void exportreportriskview(String id, Boolean type, String exportFileName, 
            String sheetName,  String headerData, String style,
            HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> indexList = new ArrayList<String>();//列ID
        List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();//列表数据
        //根节点
        Map<String, Object> map = (Map<String, Object>) this.reportRiskViewRoot(type).get("organization");
        listMap.add(map);
        if("false".equals(map.get("leaf").toString())){
            //非子节点
            List<Map<String,Object>> listItem = this.getAllReportRisk(map.get("id").toString());
            listMap.addAll(listItem);
        }
        String[] fieldTitle = null;
        JSONArray headerArray=JSONArray.fromObject(headerData);
        int j = headerArray.size();
        fieldTitle = new String[j];
        for(int i=0;i<j;i++){
            JSONObject jsonObj = headerArray.getJSONObject(i);
            fieldTitle[i] = jsonObj.get("text").toString();
            indexList.add(jsonObj.get("dataIndex").toString());
        }
        for(Map<String,Object> maps : listMap){
            Object[] object = new Object[j];
            object[0] = maps.get("orgname").toString();
            object[1] = maps.get("highrisk").toString();
            object[2] = maps.get("attentionrisk").toString();
            object[3] = maps.get("saferisk").toString();
            object[4] = maps.get("riskcount").toString();
            list.add(object);
        }
        
        if(StringUtils.isBlank(exportFileName)){
            exportFileName = "风险概况.xls";
        }
        if(StringUtils.isBlank(sheetName)){
            sheetName = "风险概况";
        }
        //数据，列名称，文件名称，sheet名称，sheet位置
        ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
    }
	
	/**
     * 导出详细的风险列表
     * 
     * @author 张健
     * @param type 是否是集团
     * @return 
     * @date 2013-12-18
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/risk/risk/exportreportriskgrid.f")
    public void exportreportriskgrid(String id, String companyid,String orgid,String type, String exportFileName, 
            String sheetName,  String headerData, String style,
            HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> indexList = new ArrayList<String>();//列ID
        List<Map> listMap = (List<Map>) this.showreportriskgrid(0, 10000, null, null, companyid, orgid, type).get("datas");//列表数据
        String[] fieldTitle = null;
        JSONArray headerArray=JSONArray.fromObject(headerData);
        int j = headerArray.size();
        fieldTitle = new String[j];
        for(int i=0;i<j;i++){
            JSONObject jsonObj = headerArray.getJSONObject(i);
            fieldTitle[i] = jsonObj.get("text").toString();
            indexList.add(jsonObj.get("dataIndex").toString());
        }
        for(Map maps : listMap){
            Object[] object = new Object[j];
            for(int z=0;z<j;z++){
                if("isNew".equals(indexList.get(z))){
                    if("true".equals(maps.get(indexList.get(z)).toString())){
                        object[z] = "新";
                    }else{
                        object[z] = "";
                    }
                }else if("assessementStatus".equals(indexList.get(z))){
                    if("icon-ibm-symbol-4-sm".equals(maps.get(indexList.get(z)))){
                        object[z] = "color|red";
                    }else if("icon-ibm-symbol-6-sm".equals(maps.get(indexList.get(z)))){
                        object[z] = "color|green";
                    }else if("icon-ibm-symbol-5-sm".equals(maps.get(indexList.get(z)))){
                        object[z] = "color|yellow";
                    }else if("icon-ibm-symbol-0-sm".equals(maps.get(indexList.get(z)))){
                        object[z] = "color|";
                    }else{
                        object[z] = maps.get(indexList.get(z));
                    }
                }else if("etrend".equals(indexList.get(z))){
                    if("icon-ibm-icon-trend-rising-positive".equals(maps.get(indexList.get(z)))){
                        object[z] = "上升";
                    }else if("icon-ibm-icon-trend-falling-negative".equals(maps.get(indexList.get(z)))){
                        object[z] = "下降";
                    }else if("icon-ibm-icon-trend-neutral-null".equals(maps.get(indexList.get(z)))){
                        object[z] = "持平";
                    }else{
                        object[z] = maps.get(indexList.get(z));
                    }
                }else{
                    object[z] = maps.get(indexList.get(z));
                }
            }
            list.add(object);
        }
        
        if(StringUtils.isBlank(exportFileName)){
            exportFileName = "风险详细列表.xls";
        }
        if(StringUtils.isBlank(sheetName)){
            sheetName = "风险详细列表";
        }
        //数据，列名称，文件名称，sheet名称，sheet位置
        ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
    }
	
	/**
	 * 递归返回跟节点下所有子节点
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2013-12-18
	 * @since Ver 1.1
	 */
	public List<Map<String,Object>> getAllReportRisk(String node) throws Exception{
	    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String, Object> map = this.reportRiskView(node, true, null);
        List<Map<String,Object>> listItem = (List<Map<String, Object>>) map.get("children");
	    for(Map<String,Object> maplist : listItem){
	        list.add(maplist);
	        if("false".equals(maplist.get("leaf").toString())){
	            List<Map<String,Object>> listItemC = this.getAllReportRisk(maplist.get("id").toString());
	            list.addAll(listItemC);
	        }
	    }
	    return list;
	}
	
	
	@ResponseBody
    @RequestMapping(value = "/risk/risk/findRisksByIds.f")
	public List<Map<String,Object>> findRisksByIds(String[] ids){
		return o_riskBO.findRiskByIds(ids);
	}
	
}