package com.fhd.ra.web.controller.risk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ObjectNotFoundException;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskOrgRelationTemp;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.quaAssess.DeptLeadEditIdea;
import com.fhd.entity.assess.quaAssess.DeptLeadResponseIdea;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.response.RiskResponse;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.RiskRelaRisk;
import com.fhd.entity.risk.Score;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.dic.DictEntryRelation;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.formulateplan.LastLeaderBO;
import com.fhd.ra.business.assess.formulateplan.RiskOrgTempBO;
import com.fhd.ra.business.assess.formulateplan.RiskResponseBO;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.DeptLeadEditIdeaBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreInstanceBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.ra.web.form.risk.RiskForm;
import com.fhd.sm.web.controller.util.Utils;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class RiskCmpControl {

	@Autowired
	private RiskCmpBO o_riskCmpBO;
	
	//模板
	@Autowired
	private TemplateBO o_templateBO;
	
	//字典
	@Autowired
	private DictBO o_dicBO;
	
	//人员
	@Autowired
	private EmpGridBO o_empGridBO;
	
	//维度
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
    private ScoreInstanceBO o_scoreInstanceBO;
	
	@Autowired
    private DictBO o_dictEntryBO;
	
	@Autowired
	private OrgGridBO o_orgGridBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private RiskOrgBO o_riskOrgBO;
	
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private RiskOrgTempBO riskOrgTempBO;
	
	@Autowired
	private LastLeaderBO o_lastLeaderBO;
	
	@Autowired
	private DeptLeadEditIdeaBO o_deptLeadEditIdeaBO;
	
	/**
	 * add by jia.song@pcitc.com
	 */
	@Autowired
	private AdjustHistoryResultBO adjustHistoryResultBO;
	
	@Autowired
	private ShowRiskTidyBO showRiskTidyBO;
	
	@Autowired
	private RiskResponseBO o_riskResponseBO;
	
	/**
	 * 风险库保存风险
	 * state=2,专用于风险评估模块风险的添加
	 * @author zhengjunxiang
	 * @param riskForm,gatherfrequenceDict采集频率相关字段,采集频率|采集公式|采集频率显示内容|显示内容后缀
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/saveRiskStorage.f")
	public Map<String, Object> saveRiskStorage(RiskForm riskForm,String id,String state,String archiveStatus,String gatherfrequenceDict,
											   String assessValue,String schm,String respondValue) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		
		Risk risk = new Risk();
		//删除状态
		if(state==null || state.equals("")){
			risk.setDeleteStatus("1");
		}else{
			risk.setDeleteStatus(state);	//将2的状态保存起来
		}
		
		//归档状态
		if(archiveStatus==null || archiveStatus.equals("")){
			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
		}else{
			risk.setArchiveStatus(archiveStatus);
		}
		
		//添加
		
		String makeId = Identities.uuid();
		risk.setId(makeId);
		String companyId = UserContext.getUser().getCompanyid();
		SysOrganization company = new SysOrganization();
		company.setId(companyId);
		risk.setCompany(company);
		//添加风险分库标识
		risk.setSchm(schm);
		//上级风险
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && !parentIdStr.equals("")){
			JSONArray parentIdArr =JSONArray.fromObject(parentIdStr);
			String parentId = ((JSONObject)parentIdArr.get(0)).get("id").toString();
			Risk parent = o_riskCmpBO.findRiskById(parentId);
			if(null!=parent){
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq()+makeId+".");
				risk.setLevel(parent.getLevel()==null?1:parent.getLevel()+1);
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("."+makeId+".");
			risk.setLevel(1);
		}

		risk.setIsLeaf(true);
		risk.setIsRiskClass(riskForm.getIsRiskClass());
		
		/**
		 * 基本信息
		 */
		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());
		//责任部门，相关部门，责任人，相关人，影响指标，影响流程
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		String influKpiName = riskForm.getInfluKpiName();
		String influProcessureName = riskForm.getInfluProcessureName();
		
		//责任部门去重复的部门id操场，容错代码
		List<String> existList = new ArrayList<String>();
		if(StringUtils.isNotBlank(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				JSONObject object = (JSONObject)respDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					respDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			respDeptName = respDeptArray.toString();
		}

		//相关部门去重复的部门id操场，容错代码
		existList.clear();
		if(StringUtils.isNotBlank(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				JSONObject object = (JSONObject)relaDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					relaDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			relaDeptName = relaDeptArray.toString();
		}

		/**
		 * 扩展信息		
		 */
		//是否合规
		String riskKind = riskForm.getRiskKind();
		
		//可控性
		String riskType = riskForm.getRiskType();
		
		//序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
        
		//是否启用
		risk.setIsUse(riskForm.getIsUse());
		
		//内部动因和外部动因
		String innerReason = riskForm.getInnerReason();
		String outterReason = riskForm.getOutterReason();
		
		//应对策略
		String responseStrategy = riskForm.getResponseStrategy();
		
		//影响期间
		String impactTime = riskForm.getImpactTime();
		
		//风险分类
		String riskStructure = riskForm.getRiskStructure();
		
		//风险附件		
		//模板id
		if(StringUtils.isNotBlank(riskForm.getTemplateId())){
			Template t = new Template();
			t.setId(riskForm.getTemplateId());
			risk.setTemplate(t);
		}
		
		/**
		 * 评价信息
		 */
		
		//保存
		o_riskCmpBO.saveRisk(risk, respDeptName, relaDeptName,null,null,riskKind
				  			,null,null,influKpiName,null,influProcessureName,
				  			innerReason,outterReason,null,null,null,riskType,impactTime,
				  			responseStrategy,riskStructure,assessValue);
		//保密风险保存应对信息
		if (null!=respondValue) {
			RiskResponse responseIdeaForRisk = new RiskResponse();
			responseIdeaForRisk.setId(Identities.uuid());
			responseIdeaForRisk.setRiskId(risk.getId());
			responseIdeaForRisk.setEditIdeaContent(respondValue);
			o_riskCmpBO.mergeEditIdea(responseIdeaForRisk);
		}
		
		map.put("success",true);
		map.put("id", risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	/**
	 * 风险库保存风险
	 * state=2,专用于风险评估模块风险的添加
	 * @author zhengjunxiang
	 * @param riskForm,gatherfrequenceDict采集频率相关字段,采集频率|采集公式|采集频率显示内容|显示内容后缀
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/saveRiskStorageForSecurity.f")
	public Map<String, Object> saveRiskStorageForSecurity(RiskForm riskForm,String id,String state,String archiveStatus,String gatherfrequenceDict,String templateId,
											   String assessValue,String schm,String respondValue) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		
		Risk risk = new Risk();
		//删除状态
		if(state==null || state.equals("")){
			risk.setDeleteStatus("1");
		}else{
			risk.setDeleteStatus(state);	//将2的状态保存起来
		}
		
		//归档状态
		if(archiveStatus==null || archiveStatus.equals("")){
			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
		}else{
			risk.setArchiveStatus(archiveStatus);
		}
		
		//添加
		
		String makeId = Identities.uuid();
		risk.setId(makeId);
		String companyId = UserContext.getUser().getCompanyid();
		SysOrganization company = new SysOrganization();
		company.setId(companyId);
		risk.setCompany(company);
		//添加风险分库标识
		risk.setSchm(schm);
		//上级风险
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && !parentIdStr.equals("")){
			JSONArray parentIdArr =JSONArray.fromObject(parentIdStr);
			String parentId = ((JSONObject)parentIdArr.get(0)).get("id").toString();
			Risk parent = o_riskCmpBO.findRiskById(parentId);
			if(null!=parent){
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq()+makeId+".");
				risk.setLevel(parent.getLevel()==null?1:parent.getLevel()+1);
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("."+makeId+".");
			risk.setLevel(1);
		}

		risk.setIsLeaf(true);
		risk.setIsRiskClass(riskForm.getIsRiskClass());
		
		/**
		 * 基本信息
		 */
		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());
		//责任部门，相关部门，责任人，相关人，影响指标，影响流程
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		String influKpiName = riskForm.getInfluKpiName();
		String influProcessureName = riskForm.getInfluProcessureName();
		
		//责任部门去重复的部门id操场，容错代码
		List<String> existList = new ArrayList<String>();
		if(StringUtils.isNotBlank(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				JSONObject object = (JSONObject)respDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					respDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			respDeptName = respDeptArray.toString();
		}

		//相关部门去重复的部门id操场，容错代码
		existList.clear();
		if(StringUtils.isNotBlank(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				JSONObject object = (JSONObject)relaDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					relaDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			relaDeptName = relaDeptArray.toString();
		}

		/**
		 * 扩展信息		
		 */
		//是否合规
		String riskKind = riskForm.getRiskKind();
		
		//可控性
		String riskType = riskForm.getRiskType();
		
		//序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
        
		//是否启用
		risk.setIsUse(riskForm.getIsUse());
		
		//内部动因和外部动因
		String innerReason = riskForm.getInnerReason();
		String outterReason = riskForm.getOutterReason();
		
		//应对策略
		String responseStrategy = riskForm.getResponseStrategy();
		
		//影响期间
		String impactTime = riskForm.getImpactTime();
		
		//风险分类
		String riskStructure = riskForm.getRiskStructure();
		
		//风险附件		
		//模板id
		if(StringUtils.isNotBlank(riskForm.getTemplateId())){
			Template t = new Template();
			t.setId(riskForm.getTemplateId());
			risk.setTemplate(t);
		}
		
		/**
		 * 评价信息
		 */
		
		//保存
		o_riskCmpBO.saveRiskForSecurity(risk, respDeptName, relaDeptName,null,null,riskKind
				  			,null,null,influKpiName,null,influProcessureName,
				  			innerReason,outterReason,null,null,null,riskType,templateId,impactTime,
				  			responseStrategy,riskStructure,assessValue);
		//保密风险保存应对信息
		if (null!=respondValue) {
			RiskResponse responseIdeaForRisk = new RiskResponse();
			responseIdeaForRisk.setId(Identities.uuid());
			responseIdeaForRisk.setRiskId(risk.getId());
			responseIdeaForRisk.setEditIdeaContent(respondValue);
			o_riskCmpBO.mergeEditIdea(responseIdeaForRisk);
		}
		
		map.put("success",true);
		map.put("id", risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	
	
	/**
	 * 风险库保存风险指标信息和计算信息
	 * @param riskForm
	 * @param id 风险id
	 * @param gatherfrequenceDict
	 * @param kpiParam 指标的列表字符串
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/saveRiskStorageKpi.f")
	public Map<String, Object> saveRiskStorageKpi(RiskForm riskForm,String riskId,String gatherfrequenceDict,String kpiParam) {
		Map<String,Object> map = new HashMap<String,Object>();

		/**
		 * 修改风险的计算信息
		 */
		Risk risk = o_riskCmpBO.findRiskById(riskId);
		
		//告警方案 
		if(StringUtils.isNotBlank(riskForm.getAlarmPlanId())){
			AlarmPlan alarmPlan = new AlarmPlan();
			alarmPlan.setId(riskForm.getAlarmPlanId());
			risk.setAlarmScenario(alarmPlan);
		}
		
		//小数点位数
		risk.setDigit(riskForm.getDigit());
				
		//公式
		risk.setFormulaDefine(riskForm.getFormulaDefine());
        
        //是否计算
        String calcStr = riskForm.getCalcStr();
        if (StringUtils.isNotBlank(calcStr)) {
            risk.setCalc(o_dictEntryBO.findDictEntryById(calcStr));
        }
        //采集频率
        if(StringUtils.isNotBlank(gatherfrequenceDict)){
            //采集频率|采集公式|采集频率显示内容|显示内容后缀
            String gatherDictTemp[] = gatherfrequenceDict.split("\\|");
            if(StringUtils.isNotBlank(gatherDictTemp[0])){
                risk.setGatherFrequence(o_dictEntryBO.findDictEntryById(gatherDictTemp[0]));
            }
            if(StringUtils.isNotBlank(gatherDictTemp[1])){
                risk.setGatherDayFormulr(gatherDictTemp[1]);
            }
            if(StringUtils.isNotBlank(gatherDictTemp[2]) || StringUtils.isNotBlank(gatherDictTemp[3])){
                risk.setGatherDayFormulrShow(gatherDictTemp[2] + "@" + gatherDictTemp[3]);
            }
        }
        
        //延迟天数
        risk.setResultCollectInterval(riskForm.getResultCollectInterval());
		
		/**
		 * 添加风险指标
		 */
		o_riskCmpBO.mergeRiskAndKpi(kpiParam,risk);
		
		map.put("success",true);
		map.put("id",risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	/**
	 * 风险库修改风险
	 * @author zhengjunxiang
	 * @param riskForm
	 * @param id riskid
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/mergeRiskStorage.f")
	public Map<String, Object> mergeRiskStorage(RiskForm riskForm,String id,String assessValue,String respondValue) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		//修改
		Risk risk = o_riskCmpBO.findRiskById(id);
		
		/**
		 * 基本信息
		 */
		//上级风险
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && (!parentIdStr.equals("")) && (!parentIdStr.equals("[]"))){
			JSONArray arr = JSONArray.fromObject(parentIdStr);
			if(arr != null){
				JSONObject obj = (JSONObject)arr.get(0);
				Risk parent = o_riskCmpBO.findRiskById(obj.getString("id"));
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq() + risk.getId() + ".");
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("." + risk.getId() + ".");
		}

		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());
		
		//责任部门，相关部门，责任人，相关人，影响指标，影响流程
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		String influKpiName = riskForm.getInfluKpiName();
		String influProcessureName = riskForm.getInfluProcessureName();
		
		//责任部门去重复的部门id操场，容错代码
		List<String> existList = new ArrayList<String>();
		if(StringUtils.isNotBlank(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				JSONObject object = (JSONObject)respDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					respDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			respDeptName = respDeptArray.toString();
		}

		//相关部门去重复的部门id操场，容错代码
		existList.clear();
		if(StringUtils.isNotBlank(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				JSONObject object = (JSONObject)relaDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					relaDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			relaDeptName = relaDeptArray.toString();
		}

		/**
		 * 扩展信息		
		 */
		//是否合规
		String riskKind = riskForm.getRiskKind();

		//可控性
		String riskType = riskForm.getRiskType();
		
		//是否启用
		risk.setIsUse(riskForm.getIsUse());
		
		//序号
		risk.setSort(riskForm.getSort());

		//评估模板
//		if(null!=riskForm.getTemplateId() && !"".equals(riskForm.getTemplateId())){
//			Template template = new Template();
//			template.setId(riskForm.getTemplateId());
//			risk.setTemplate(template);
//		}else{
//			risk.setTemplate(null);
//		}

		//影响期间
		String impactTime = riskForm.getImpactTime();
		
		//应对策略
		String responseStrategy = riskForm.getResponseStrategy();
		
		//内部动因和外部动因
		String innerReason = riskForm.getInnerReason();
		String outterReason = riskForm.getOutterReason();
		
		//风险分类多选
		String riskStructure = riskForm.getRiskStructure();
			
		//模板id
		if(StringUtils.isNotBlank(riskForm.getTemplateId())){
			Template t = new Template();
			t.setId(riskForm.getTemplateId());
			risk.setTemplate(t);
		}
				
		//保存
		o_riskCmpBO.mergeRisk(risk, respDeptName, relaDeptName,null,null,riskKind
				  			,null,null,influKpiName,null,influProcessureName,
				  			innerReason,outterReason,null,null,null,riskType,impactTime,
				  			responseStrategy,riskStructure,assessValue);
		//保密风险应对信息
		if (null!=respondValue) {
			RiskResponse response=o_riskResponseBO.getResponseByRiskId(id);
		if (null!=response) {
			response.setEditIdeaContent(respondValue);
		}else 
		{
			response=new RiskResponse();
			response.setId(Identities.uuid());
			response.setRiskId(id);
			response.setEditIdeaContent(respondValue);
		}
		o_riskCmpBO.mergeEditIdea(response);
		}
		
		map.put("success",true);
		map.put("id",risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	/**
	 * 风险库修改风险
	 * @author zhengjunxiang
	 * @param riskForm
	 * @param id riskid
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/mergeRiskStorageForSecurity.f")
	public Map<String, Object> mergeRiskStorageForSecurity(RiskForm riskForm,String id,String templateId,String assessValue,String respondValue) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		//修改
		Risk risk = o_riskCmpBO.findRiskById(id);
		
		/**
		 * 基本信息
		 */
		//上级风险
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && (!parentIdStr.equals("")) && (!parentIdStr.equals("[]"))){
			JSONArray arr = JSONArray.fromObject(parentIdStr);
			if(arr != null){
				JSONObject obj = (JSONObject)arr.get(0);
				Risk parent = o_riskCmpBO.findRiskById(obj.getString("id"));
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq() + risk.getId() + ".");
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("." + risk.getId() + ".");
		}

		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());
		
		//责任部门，相关部门，责任人，相关人，影响指标，影响流程
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		String influKpiName = riskForm.getInfluKpiName();
		String influProcessureName = riskForm.getInfluProcessureName();
		
		//责任部门去重复的部门id操场，容错代码
		List<String> existList = new ArrayList<String>();
		if(StringUtils.isNotBlank(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				JSONObject object = (JSONObject)respDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					respDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			respDeptName = respDeptArray.toString();
		}

		//相关部门去重复的部门id操场，容错代码
		existList.clear();
		if(StringUtils.isNotBlank(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				JSONObject object = (JSONObject)relaDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					relaDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			relaDeptName = relaDeptArray.toString();
		}

		/**
		 * 扩展信息		
		 */
		//是否合规
		String riskKind = riskForm.getRiskKind();

		//可控性
		String riskType = riskForm.getRiskType();
		
		//是否启用
		risk.setIsUse(riskForm.getIsUse());
		
		//序号
		risk.setSort(riskForm.getSort());

		//评估模板
//		if(null!=riskForm.getTemplateId() && !"".equals(riskForm.getTemplateId())){
//			Template template = new Template();
//			template.setId(riskForm.getTemplateId());
//			risk.setTemplate(template);
//		}else{
//			risk.setTemplate(null);
//		}

		//影响期间
		String impactTime = riskForm.getImpactTime();
		
		//应对策略
		String responseStrategy = riskForm.getResponseStrategy();
		
		//内部动因和外部动因
		String innerReason = riskForm.getInnerReason();
		String outterReason = riskForm.getOutterReason();
		
		//风险分类多选
		String riskStructure = riskForm.getRiskStructure();
			
		//模板id
		if(StringUtils.isNotBlank(riskForm.getTemplateId())){
			Template t = new Template();
			t.setId(riskForm.getTemplateId());
			risk.setTemplate(t);
		}
				
		//保存
		o_riskCmpBO.mergeRiskForSecurity(risk, respDeptName, relaDeptName,null,null,riskKind
				  			,null,null,influKpiName,null,influProcessureName,
				  			innerReason,outterReason,null,null,null,riskType,impactTime,
				  			responseStrategy,riskStructure,assessValue);
		//保密风险应对信息
		if (null!=respondValue) {
		RiskResponse response=o_riskResponseBO.getResponseByRiskId(id);
		if (null!=response) {
			response.setEditIdeaContent(respondValue);
		}else 
		{
			response=new RiskResponse();
			response.setId(Identities.uuid());
			response.setRiskId(id);
			response.setEditIdeaContent(respondValue);
		}
		o_riskCmpBO.mergeEditIdea(response);
		}
		
		map.put("success",true);
		map.put("id",risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	/**
	 * 保存风险
	 * state=2,专用于风险评估模块风险的添加
	 * @author zhengjunxiang
	 * @param riskForm,gatherfrequenceDict采集频率相关字段,采集频率|采集公式|采集频率显示内容|显示内容后缀
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/saveRiskInfo")
	public Map<String, Object> saveRiskInfo(RiskForm riskForm,String id,String state,String archiveStatus,String gatherfrequenceDict) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		Risk risk = new Risk();
		if(state==null || state.equals("")){
			risk.setDeleteStatus("1");
		}else{
			risk.setDeleteStatus(state);	//将2的状态保存起来
		}
		
		//归档状态
		if(archiveStatus==null || archiveStatus.equals("")){
			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
		}else{
			risk.setArchiveStatus(archiveStatus);
		}
		
		//添加
		String makeId = Identities.uuid();
		risk.setId(makeId);
		String companyId = UserContext.getUser().getCompanyid();
		SysOrganization company = new SysOrganization();
		company.setId(companyId);
		risk.setCompany(company);
		//上级风险
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && !parentIdStr.equals("")){
			JSONArray parentIdArr =JSONArray.fromObject(parentIdStr);
			String parentId = ((JSONObject)parentIdArr.get(0)).get("id").toString();
			Risk parent = o_riskCmpBO.findRiskById(parentId);
			if(null!=parent){
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq()+makeId+".");
				risk.setLevel(parent.getLevel()+1);
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("."+makeId+".");
			risk.setLevel(1);
		}

		risk.setIsLeaf(true);
		risk.setIsRiskClass(riskForm.getIsRiskClass());
		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());
		
		//是否定量，是否启用
		//risk.setIsFix(riskForm.getIsFix());
		risk.setIsUse(riskForm.getIsUse());
		
		//序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
		//是否继承
		risk.setIsInherit(riskForm.getIsInherit());
		//评估模板
		if(null!=riskForm.getTemplateId() && !"".equals(riskForm.getTemplateId())){
			Template template = new Template();
			template.setId(riskForm.getTemplateId());
			risk.setTemplate(template);
		}else{
			risk.setTemplate(null);
		}
		//公式
		risk.setFormulaDefine(riskForm.getFormulaDefine());
		//是否计算
		String calcStr = riskForm.getCalcStr();
		if (StringUtils.isNotBlank(calcStr)) {
		    risk.setCalc(o_dictEntryBO.findDictEntryById(calcStr));
        }
		
		//采集频率
		if(StringUtils.isNotBlank(gatherfrequenceDict)){
		    //采集频率|采集公式|采集频率显示内容|显示内容后缀
		    String gatherDictTemp[] = gatherfrequenceDict.split("\\|");
		    if(StringUtils.isNotBlank(gatherDictTemp[0])){
		        risk.setGatherFrequence(o_dictEntryBO.findDictEntryById(gatherDictTemp[0]));
		    }
		    if(StringUtils.isNotBlank(gatherDictTemp[1])){
		        risk.setGatherDayFormulr(gatherDictTemp[1]);
            }
		    if(StringUtils.isNotBlank(gatherDictTemp[2]) || StringUtils.isNotBlank(gatherDictTemp[3])){
		        risk.setGatherDayFormulrShow(gatherDictTemp[2] + "@" + gatherDictTemp[3]);
            }
		}
		
		//延迟天数
		risk.setResultCollectInterval(riskForm.getResultCollectInterval());
		
				
		//风险类别，涉及版块
		String riskKind = riskForm.getRiskKind();
		String relePlate = riskForm.getRelePlate();
		
		//是否应对
		//risk.setIsAnswer(riskForm.getIsAnswer());
		
		//风险类型
		String riskType = riskForm.getRiskType();
		
		//影响期间
		String impactTime = riskForm.getImpactTime();
		
		//应对策略
		String responseStrategy = riskForm.getResponseStrategy();
		
		//内部动因和外部动因
		String innerReason = riskForm.getInnerReason();
		String outterReason = riskForm.getOutterReason();
		
		//风险动因和风险影响
		String riskReason = riskForm.getRiskReason();
		String riskInfluence = riskForm.getRiskInfluence();
		
		//风险价值链
		String valueChain = "";//riskForm.getValueChain();
		
		//责任部门，相关部门，责任人，相关人，风险指标，影响指标，控制流程，影响流程
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		String riskKpiName = riskForm.getRiskKpiName();
		String influKpiName = riskForm.getInfluKpiName();
		String controlProcessureName = riskForm.getControlProcessureName();
		String influProcessureName = riskForm.getInfluProcessureName();
		
		//下面2个暂时没用到，责任岗位，相关岗位
		String respPositionName = riskForm.getRespPositionName();
		String relaPositionName = riskForm.getRelaPositionName();
		
		//风险分类多选
		String riskStructure = riskForm.getRiskStructure();
		
		//责任部门去重复的部门id操场，容错代码
		List<String> existList = new ArrayList<String>();
		if(StringUtils.isNotBlank(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				JSONObject object = (JSONObject)respDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					respDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			respDeptName = respDeptArray.toString();
		}

		//相关部门去重复的部门id操场，容错代码
		existList.clear();
		if(StringUtils.isNotBlank(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				JSONObject object = (JSONObject)relaDeptArray.get(i);
				String deptidStr = object.get("deptid").toString();
				if(existList.contains(deptidStr)){
					relaDeptArray.remove(i);
				}else{
					existList.add(deptidStr);
				}
			}
			relaDeptName = relaDeptArray.toString();
		}

		//保存
		o_riskCmpBO.saveRisk(risk, respDeptName, relaDeptName,respPositionName,relaPositionName,riskKind
				  			,relePlate,riskKpiName,influKpiName,controlProcessureName,influProcessureName,
				  			innerReason,outterReason,riskReason,riskInfluence,valueChain,riskType,impactTime,
				  			responseStrategy,riskStructure,null);
		
		map.put("success",true);
		map.put("id",risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	/**
	 * 修改风险
	 * state=2,专用于风险评估模块风险的添加
	 * @author zhengjunxiang
	 * @param riskForm
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/mergeRiskInfo")
	public Map<String, Object> mergeRiskInfo(RiskForm riskForm,String id,String state,String gatherfrequenceDict) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		//修改
		Risk risk = o_riskCmpBO.findRiskById(id);
		//上级风险
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && (!parentIdStr.equals("")) && (!parentIdStr.equals("[]"))){
			JSONArray arr = JSONArray.fromObject(parentIdStr);
			if(arr != null){
				JSONObject obj = (JSONObject)arr.get(0);
				Risk parent = o_riskCmpBO.findRiskById(obj.getString("id"));
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq() + risk.getId() + ".");
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("." + risk.getId() + ".");
		}
		
		risk.setIsRiskClass(riskForm.getIsRiskClass());
		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());
		risk.setSort(riskForm.getSort());
		
		//是否定量，是否启用
		//risk.setIsFix(riskForm.getIsFix());
		risk.setIsUse(riskForm.getIsUse());
		
		//序号
		risk.setSort(riskForm.getSort());

		//是否继承
		risk.setIsInherit(riskForm.getIsInherit());
		//评估模板
		if(null!=riskForm.getTemplateId() && !"".equals(riskForm.getTemplateId())){
			Template template = new Template();
			template.setId(riskForm.getTemplateId());
			risk.setTemplate(template);
		}else{
			risk.setTemplate(null);
		}
		//公式
		risk.setFormulaDefine(riskForm.getFormulaDefine());
		//公式
        risk.setFormulaDefine(riskForm.getFormulaDefine());
        
        //是否计算
        String calcStr = riskForm.getCalcStr();
        if (StringUtils.isNotBlank(calcStr)) {
            risk.setCalc(o_dictEntryBO.findDictEntryById(calcStr));
        }
        //采集频率
        if(StringUtils.isNotBlank(gatherfrequenceDict)){
            //采集频率|采集公式|采集频率显示内容|显示内容后缀
            String gatherDictTemp[] = gatherfrequenceDict.split("\\|");
            if(StringUtils.isNotBlank(gatherDictTemp[0])){
                risk.setGatherFrequence(o_dictEntryBO.findDictEntryById(gatherDictTemp[0]));
            }
            if(StringUtils.isNotBlank(gatherDictTemp[1])){
                risk.setGatherDayFormulr(gatherDictTemp[1]);
            }
            if(StringUtils.isNotBlank(gatherDictTemp[2]) || StringUtils.isNotBlank(gatherDictTemp[3])){
                risk.setGatherDayFormulrShow(gatherDictTemp[2] + "@" + gatherDictTemp[3]);
            }
        }
        
        //延迟天数
        risk.setResultCollectInterval(riskForm.getResultCollectInterval());
				
		//风险类别，涉及版块
		String riskKind = riskForm.getRiskKind();
		String relePlate = riskForm.getRelePlate();
		
		//是否应对
		//risk.setIsAnswer(riskForm.getIsAnswer());
		
		//风险类型
		String riskType = riskForm.getRiskType();
		
		//影响期间
		String impactTime = riskForm.getImpactTime();
		
		//应对策略
		String responseStrategy = riskForm.getResponseStrategy();
		
		//内部动因和外部动因
		String innerReason = riskForm.getInnerReason();
		String outterReason = riskForm.getOutterReason();
		
		//风险动因和风险影响
		String riskReason = riskForm.getRiskReason();
		String riskInfluence = riskForm.getRiskInfluence();
		
		//风险价值链
		String valueChain = "";//riskForm.getValueChain();
		
		//责任部门，相关部门，责任人，相关人，风险指标，影响指标，控制流程，影响流程
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		String riskKpiName = riskForm.getRiskKpiName();
		String influKpiName = riskForm.getInfluKpiName();
		String controlProcessureName = riskForm.getControlProcessureName();
		String influProcessureName = riskForm.getInfluProcessureName();
		
		//下面2个暂时没用到，责任岗位，相关岗位
		String respPositionName = riskForm.getRespPositionName();
		String relaPositionName = riskForm.getRelaPositionName();
		
		//风险分类多选
		String riskStructure = riskForm.getRiskStructure();
				
		//保存
		o_riskCmpBO.mergeRisk(risk, respDeptName, relaDeptName,respPositionName,relaPositionName,riskKind
				  			,relePlate,riskKpiName,influKpiName,controlProcessureName,influProcessureName,
				  			innerReason,outterReason,riskReason,riskInfluence,valueChain,riskType,impactTime,
				  			responseStrategy,riskStructure,null);
		
		map.put("success",true);
		map.put("id",risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	/**
	 * 保存风险
	 * state=2,专用于风险评估模块风险的添加
	 * @author zhengjunxiang
	 * @param riskForm
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/saveRiskDefineInfo")
	public Map<String, Object> saveRiskDefineInfo(RiskForm riskForm,String id,String state,String archiveStatus) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		Risk risk = new Risk();
		if(state==null || state.equals("")){
			risk.setDeleteStatus("1");
		}else{
			risk.setDeleteStatus(state);	//将2的状态保存起来
		}
		//归档状态
		if(archiveStatus==null || archiveStatus.equals("")){
			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
		}else{
			risk.setArchiveStatus(archiveStatus);
		}
		//添加
		String makeId = Identities.uuid();
		risk.setId(makeId);
		String companyId = UserContext.getUser().getCompanyid();
		SysOrganization company = new SysOrganization();
		company.setId(companyId);
		risk.setCompany(company);
		//上级风险和上级风险模板
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && !parentIdStr.equals("")){
			JSONArray parentIdArr =JSONArray.fromObject(parentIdStr);
			String parentId = ((JSONObject)parentIdArr.get(0)).get("id").toString();
			Risk parent = o_riskCmpBO.findRiskById(parentId);
			if(null!=parent){
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq()+makeId+".");
				//有可能数据level有问题
				risk.setLevel(parent.getLevel()==null?null:parent.getLevel()+1);
				//模板
				risk.setIsInherit("0yn_y");
				risk.setTemplate(parent.getTemplate());
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("."+makeId+".");
			risk.setLevel(1);
			//模板
			risk.setIsInherit("0yn_y");
			risk.setTemplate(null);
		}

		risk.setIsLeaf(true);
		risk.setIsRiskClass(riskForm.getIsRiskClass());
		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());

		//序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
		
		//责任部门，相关部门
		String respDeptName = riskForm.getRespDeptName();
		String relaDeptName = riskForm.getRelaDeptName();
		
		//保存
		o_riskCmpBO.saveRisk(risk, respDeptName, relaDeptName
							,null,null,null,null,null,null,null,null,null,null
							,null,null,null,null,null,null,null,null);
		
		map.put("success",true);
		map.put("id",risk.getId());
		map.put("name",risk.getName());
		
		return map;
	}
	
	/**
	 * 修改风险
	 * state=2,专用于风险评估模块风险的添加
	 * @author zhengjunxiang
	 * @param riskForm
	 * @param id
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/mergeRiskDefineInfo")
	public Map<String, Object> mergeRiskDefineInfo(RiskForm riskForm,String id,String state) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		//修改
		Risk risk = o_riskCmpBO.findRiskById(id);
		//上级风险
		String parentIdStr = riskForm.getParentId();
		if(parentIdStr!=null && (!parentIdStr.equals("")) && (!parentIdStr.equals("[]"))){
			JSONArray arr = JSONArray.fromObject(parentIdStr);
			if(arr != null){
				JSONObject obj = (JSONObject)arr.get(0);
				Risk parent = o_riskCmpBO.findRiskById(obj.getString("id"));
				risk.setParent(parent);
				risk.setParentName(parent.getName());
				risk.setIdSeq(parent.getIdSeq() + risk.getId() + ".");
			}
		}else{
			risk.setParent(null);
			risk.setParentName("");
			risk.setIdSeq("." + risk.getId() + ".");
		}
		
		risk.setIsRiskClass(riskForm.getIsRiskClass());
		//编码，名称，描述
		risk.setCode(riskForm.getCode());
		risk.setName(riskForm.getName());
		risk.setDesc(riskForm.getDesc());
		
		
		//责任部门
		String respDeptName = riskForm.getRespDeptName();
		//相关部门
		String relaDeptName = riskForm.getRelaDeptName();

				
		//保存
		o_riskCmpBO.mergeRisk(risk, respDeptName, relaDeptName
									,null,null,null,null,null,null,null,null,null,null
									,null,null,null,null,null,null,null,null);
		
		map.put("success",true);
		map.put("id",risk.getId());
		
		return map;
	}
	
	/**
	 * 删除风险及风险关联，支持批量删除
	 * @param ids  逗号分割的字符串。比如‘1,2’
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/removeRiskByIds.f")
	public boolean deleteRiskByIds(String ids){
		if(StringUtils.isBlank(ids)){
			return false;
		}
		String[] idArr = ids.split(",");
		o_riskCmpBO.deleteRisk(idArr);
		
		return true;
	}
	
	/**
	 * 获取新添加的风险事件的编码（自动生成）
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cmp/risk/getRiskCode.f")
	public Map<String, Object> getRiskCode(String parentId) {
		String code = o_riskCmpBO.getGenerateRiskCode(parentId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		return map;
	}
	
	/**
	 * 获取新添加的风险事件的编码（自动生成）
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cmp/risk/getLoginDept.f")
	public Map<String, Object> getLoginDept(String employeeId, String executionId) {
		Map<String, Object> map = new HashMap<String, Object>();
		employeeId = UserContext.getUser().getEmpid();//人员ID
		SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(employeeId);
		SysOrganization org = empOrg.getSysOrganization();
		String orgIds = "";
		
		if(StringUtils.isNotBlank(executionId)){
			orgIds = o_jbpmOperate.getVariable(executionId,"deptIdBS");
		}else{
			HashMap<String, Risk> orgRiskMap = o_riskOrgBO.findRiskOrgMap();
			String idSeq[] = org.getOrgseq().replace(".", ",").split(",");
			for (String orgId : idSeq) {
				if(null != orgRiskMap.get(orgId)){
					orgIds = orgId;
					break;
				}
			}
		}
		if(StringUtils.isBlank(orgIds)){
			orgIds = UserContext.getUser().getMajorDeptId();
		}
		org = o_orgGridBO.findOrganizationByOrgId(orgIds);
		map.put("deptid", org.getId());
		map.put("deptname", org.getOrgname());
		
		return map;
	}
	
	/** 
	  * @Description: 结果整理获取保密风险的风险基本信息
	  * @author jia.song@pcitc.com
	  * @date 2017年5月24日 下午7:11:41 
	  * @param objectId
	  * @param riskId
	  * @return 
	  */
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findSecurityRiskEditInfoById.f")
	public Map<String,Object> findSecurityRiskEditInfoById(String objectId){
		Map<String,Object> rtnMap = null;
		rtnMap = findRiskEditInfoByObjectId(objectId);
		//根据objectId 获取填写的应对信息
		rtnMap.putAll(o_lastLeaderBO.findResponseTextByObjectId(objectId));
		return rtnMap;
	}
	/**
	 * 获取风险的编辑信息
	 * 只使用：添加风险时，初始化一些值：是否继承，模板id，公式字段
	 * @param riskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findRiskEditInfoById")
	public Map<String, Object> findRiskEditInfoById(@RequestParam(value="objectId",required=false )String objectId,@RequestParam(value="riskId",required=false )String riskId) {
		if(riskId != null && objectId == null){
			Map<String, Object> result=findRiskEditInfoByRiskId(riskId);
			RiskResponse riskResponse=o_riskResponseBO.getResponseByRiskId(riskId);
			if (null!=riskResponse) {
				result.put("responseText", riskResponse.getEditIdeaContent());	
			}
			return result;
		}
		else if(objectId != null && riskId == null ){
			return findRiskEditInfoByObjectId(objectId);
		}
		Log.debug("========= 获取风险的编辑信息出错   ===========");
		return null;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findRiskEditInfoByScoreObjectIdForCleanUp.f")
	public Map<String,Object> findRiskEditInfoByScoreObjectIdForCleanUp(@RequestParam(value="scoreObjectId",required=false )String scoreObjectId){
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		//获取打分对象表详细内容
		RiskScoreObject scoreObject = o_riskCmpBO.findRiskObjectById(scoreObjectId);
		JSONArray jsonArray = new JSONArray();
		if(scoreObject!=null && scoreObject.getParent()!=null){
			JSONObject object = new JSONObject();
			object.put("id", scoreObject.getParent().getId());
			jsonArray.add(object);
		}
		rtnMap.put("scoreObjectId", scoreObjectId);
		rtnMap.put("parentId", jsonArray.toString());
		rtnMap.put("code", null == scoreObject ? "" : scoreObject.getCode());
		rtnMap.put("name", null == scoreObject ? "" : scoreObject.getName());
		rtnMap.put("desc", null == scoreObject ? "" : scoreObject.getDesc());
		//获取机构关联信息
		List<RiskOrgRelationTemp> riskOrgs = this.riskOrgTempBO.getRiskOrgRelationTempListByObjectId(scoreObjectId);
		if(riskOrgs != null && riskOrgs.size() > 0){
			JSONObject object = null;
			JSONArray respDeptName = new JSONArray();
			JSONArray relaDeptName = new JSONArray();
			for(RiskOrgRelationTemp riskOrg : riskOrgs){
				if(riskOrg.getType().equals("M")){	//责任部门/人
					object = new JSONObject();
					SysOrganization sysOrganizationIte = riskOrg.getSysOrganization();
					if(sysOrganizationIte!=null){
						object.put("deptid", sysOrganizationIte.getId());
						object.put("deptno", sysOrganizationIte.getOrgcode());
						object.put("deptname", sysOrganizationIte.getOrgname());
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
			rtnMap.put("respDeptName", respDeptName.toString());
			rtnMap.put("relaDeptName", relaDeptName.toString());
		}
		//获取应对信息
		Map<String,Object> responseMap = o_lastLeaderBO.findResponseTextByObjectId(scoreObjectId);
		if(responseMap != null){
			rtnMap.put("responseText", responseMap.get("responseText"));
		}
		return rtnMap;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findRiskEditInfoByScoreObjectIdForCleanUpDetail.f")
	public Map<String,Object> findRiskEditInfoByScoreObjectIdForCleanUpDetail(@RequestParam(value="scoreObjectId",required=false )String scoreObjectId){
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		//获取打分对象表详细内容
		RiskScoreObject scoreObject = o_riskCmpBO.findRiskObjectById(scoreObjectId);

		rtnMap.put("parentName", scoreObject.getParentName());
		rtnMap.put("code", null == scoreObject ? "" : scoreObject.getCode());
		rtnMap.put("name", null == scoreObject ? "" : scoreObject.getName());
		rtnMap.put("desc", null == scoreObject ? "" : scoreObject.getDesc());
		StringBuffer mDeptName = new StringBuffer();
		StringBuffer aDeptName = new StringBuffer();
		
		//获取机构关联信息
		List<RiskOrgRelationTemp> riskOrgs = this.riskOrgTempBO.getRiskOrgRelationTempListByObjectId(scoreObjectId);
		if(riskOrgs != null && riskOrgs.size() > 0){
			for(RiskOrgRelationTemp riskOrg : riskOrgs){
				if(riskOrg.getType().equals("M")){	//责任部门和责任人
					if(null!=riskOrg.getSysOrganization()){
						try {
							mDeptName.append(riskOrg.getSysOrganization().getOrgname());
						} catch (Exception e) {
							//hibnate错误
						}
					}
					String empStr = null;
					if(null != riskOrg.getEmp()){
						try {
							empStr = "(" + riskOrg.getEmp().getEmpname() + ")";
							mDeptName.append(":");
							mDeptName.append(empStr);
						}catch (Exception e) {
							//hibnate错误
						}
					}
				}
				
				if(riskOrg.getType().equals("A")){	//相关部门和相关人
					String empStr = "";
					if(null!=riskOrg.getSysOrganization()){
						try {
							if(aDeptName.length() != 0){
								aDeptName.append(",");
							}
							aDeptName.append(riskOrg.getSysOrganization().getOrgname());
						}catch (Exception e) {
							//hibnate错误
						}
					}
					if(null!=riskOrg.getEmp()){
						try {
							empStr = "(" + riskOrg.getEmp().getEmpname() + ")";
							aDeptName.append(empStr);
						} catch (Exception e) {
							//hibnate错误
						}
					}
				}
			}
			rtnMap.put("respDeptName", mDeptName.toString());
			rtnMap.put("relaDeptName", aDeptName);
		}
		//获取应对信息
		Map<String,Object> responseMap = o_lastLeaderBO.findResponseTextByObjectId(scoreObjectId);
		if(responseMap != null){
			rtnMap.put("responseText", responseMap.get("responseText"));
		}
		return rtnMap;
	}
	
	
	public Map<String,Object> findRiskEditInfoByObjectId(String objectId){
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
//		Set<RiskOrgTemp> riskOrgs = risk.getRiskOrgTemps();
		List<RiskOrgRelationTemp> riskOrgs = this.riskOrgTempBO.getRiskOrgRelationTempListByObjectId(risk.getId());
		
		for(RiskOrgRelationTemp riskOrg : riskOrgs){
			if(riskOrg.getType().equals("M")){	//责任部门/人
				object = new JSONObject();
				SysOrganization sysOrganizationIte = riskOrg.getSysOrganization();
				if(sysOrganizationIte!=null){
					object.put("deptid", sysOrganizationIte.getId());
					object.put("deptno", sysOrganizationIte.getOrgcode());
					object.put("deptname", sysOrganizationIte.getOrgname());
				}
				/*SysEmployee empIte = riskOrg.getEmp();
				if(empIte!=null){
					object.put("empid", empIte.getId());
					object.put("empno", empIte.getEmpcode());
					object.put("empname", empIte.getEmpname());
					
				}else{
					object.put("empid", "");
				}*/
				object.put("empid", "");
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
				/*SysEmployee empIte = riskOrg.getEmp();
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
				}*/
				object.put("empid", "");
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
	
	
	//风险对比
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findRiskEditInfoByRiskIdCompare.f")
	public Map<String,Object> findRiskEditInfoByRiskIdCompare(String riskId){
		Risk risk = o_riskCmpBO.findRiskById(riskId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		//上级风险


		map.put("parentId", risk.getParent().getName());
		map.put("code", null == risk?"":risk.getCode());
		map.put("name", null == risk?"":risk.getName());
		map.put("desc", null == risk?"":risk.getDesc());
		
		//责任部门/人和相关部门/人

	Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
		
		for(RiskOrg riskOrg : riskOrgs){
			if(riskOrg.getType().equals("M")){	//责任部门/人
				SysOrganization sysOrganizationIte = riskOrg.getSysOrganization();
				if(sysOrganizationIte!=null){
		
					map.put("respDeptName", sysOrganizationIte.getOrgname());
				}

			}
			if(riskOrg.getType().equals("A")){	//相关部门/人
				SysOrganization sysOrganizationIte = riskOrg.getSysOrganization();
				if(sysOrganizationIte!=null){
		
					map.put("relaDeptName", sysOrganizationIte.getOrgname());
				}

			}
		}

	
		RiskResponse riskResponse=o_riskResponseBO.getResponseByRiskId(riskId);
		if (null!=riskResponse) {
			map.put("riskResponse", riskResponse);	
		}
				
		return map;
	}
	
	
	public Map<String,Object> findRiskEditInfoByRiskId(String riskId){
		Risk risk = o_riskCmpBO.findRiskById(riskId);
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
					object.put("deptno", sysOrganizationIte.getOrgcode());
					object.put("deptname", sysOrganizationIte.getOrgname());
				}
				/*SysEmployee empIte = riskOrg.getEmp();
				if(empIte!=null){
					object.put("empid", empIte.getId());
					object.put("empno", empIte.getEmpcode());
					object.put("empname", empIte.getEmpname());
					
				}else{
					object.put("empid", "");
				}*/
				object.put("empid", "");
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
				/*SysEmployee empIte = riskOrg.getEmp();
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
				}*/
				object.put("empid", "");
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
	
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findObjectAndMeasureDetailByObjectId.f")
	public Map<String,Object> findObjectAndMeasureDetailByObjectId(String objectId,String hIdeaId,String hResponseId){
		Map<String, Object> map = new HashMap<String, Object>();
		RiskScoreObject object = o_riskCmpBO.findRiskScoreObjectById(objectId);
		map.put("code", object.getCode());
		map.put("name", object.getName());
		map.put("parentName", object.getParent()==null?"无":object.getParent().getName());
		map.put("desc", object.getDesc());
		StringBuffer mDeptName = new StringBuffer();
		String aDeptName = "";	//相关部门
		Set<RiskOrgTemp> riskOrgs = object.getRiskOrgTemps();
		for(RiskOrgTemp org : riskOrgs){
			if(org.getType().equals("M")){	//责任部门和责任人
				String empStr = "";
				if(null!=org.getEmp()){
					try {
						empStr = "(" + org.getEmp().getEmpname() + ")";
					} catch (Exception e) {
						//hibnate错误
					}
				}
				if(null!=org.getSysOrganization()){
					try {
						mDeptName.append(org.getSysOrganization().getOrgname());
						mDeptName.append(empStr);
						mDeptName.append(",");
					} catch (Exception e) {
						//hibnate错误
					}
				}
			}
			if(org.getType().equals("A")){	//相关部门和相关人
				String empStr = "";
				if(null!=org.getEmp()){
					try {
						empStr = "(" + org.getEmp().getEmpname() + ")";
					} catch (Exception e) {
						//hibnate错误
					}
				}
				if(null!=org.getSysOrganization()){
					try {
						aDeptName += org.getSysOrganization().getOrgname() + empStr + ",";
					} catch (Exception e) {
						//hibnate错误
					}
				}
			}
		}
		if(!mDeptName.toString().equals("")){
				map.put("respDeptName", mDeptName.substring(0, mDeptName.length()-1));
		}else{
		    map.put("respDeptName", mDeptName);
		}
		if(!aDeptName.equals("")){
			aDeptName = aDeptName.substring(0, aDeptName.length()-1);
		}
		
		map.put("relaDeptName", aDeptName);
		DeptLeadEditIdea editIdea = o_deptLeadEditIdeaBO.findEntityById(hIdeaId);
		map.put("hEditIdea", editIdea.getEditIdeaContent());
		DeptLeadResponseIdea responseIdea = o_deptLeadEditIdeaBO.findEntityByHresponseId(hResponseId);
		map.put("hResponseIdea", responseIdea.getEditIdeaContent());
		return map;
	}
	
	/**
	 * 查询风险的详细信息
	 * 只使用：上级风险名称，风险名称，编号，风险描述，责任部门，相关部门，影响指标，风险指标，影响流程，控制流程
	 * @param riskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findRiskDetailInfoById")
	public Map<String, Object> findRiskDetailInfoById(String riskId,String objectId) {
		
		//Risk risk = o_riskCmpBO.findRiskById(riskId);
		Risk risk = o_riskCmpBO.getRiskByObjectIdFromObjectTable(objectId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", risk.getCode());
		map.put("name", risk.getName());
		map.put("parentName", risk.getParent()==null?"无":risk.getParent().getName());
		map.put("desc", risk.getDesc());
		
		StringBuffer mDeptName = new StringBuffer();
		String aDeptName = "";//相关部门
		String respPositionName = "";//责任岗位
		String relaPositionName = "";//相关岗位
		
		Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
		for(RiskOrg org : riskOrgs){
			if(org.getType().equals("M")){	//责任部门和责任人
				String empStr = "";
				if(null!=org.getEmp()){
					try {
						empStr = "(" + org.getEmp().getEmpname() + ")";
					} catch (Exception e) {
						//hibnate错误
					}
				}
				if(null!=org.getSysOrganization()){
					try {
						mDeptName.append(org.getSysOrganization().getOrgname());
						mDeptName.append(empStr);
						mDeptName.append(",");
					} catch (Exception e) {
						//hibnate错误
					}
				}
			}
			if(org.getType().equals("A")){	//相关部门和相关人
				String empStr = "";
				if(null!=org.getEmp()){
					try {
						empStr = "(" + org.getEmp().getEmpname() + ")";
					} catch (Exception e) {
						//hibnate错误
					}
				}
				if(null!=org.getSysOrganization()){
					try {
						aDeptName += org.getSysOrganization().getOrgname() + empStr + ",";
					} catch (Exception e) {
						//hibnate错误
					}
				}
			}
		}
		if(!mDeptName.toString().equals("")){
				map.put("respDeptName", mDeptName.substring(0, mDeptName.length()-1));
		}else{
		    map.put("respDeptName", mDeptName);
		}
		if(!aDeptName.equals("")){
			aDeptName = aDeptName.substring(0, aDeptName.length()-1);
		}
		if(!respPositionName.equals("")){
			respPositionName = respPositionName.substring(0, respPositionName.length()-1);
		}
		if(!relaPositionName.equals("")){
			relaPositionName = relaPositionName.substring(0, relaPositionName.length()-1);
		}
		
		map.put("relaDeptName", aDeptName);
		map.put("respPositionName", respPositionName);	//责任岗位
		map.put("relaPositionName", relaPositionName);	//相关岗位
		
		String riskKpiName = "";//风险指标
		String influKpiName = "";//影响指标
		Set<KpiRelaRisk> kpiRelaRisks = risk.getKpiRelaRisks();
		for(KpiRelaRisk kpi : kpiRelaRisks){
			if(kpi.getType().equals("RM")){	//风险指标
				riskKpiName += kpi.getKpi().getName() + ",";
			}
			if(kpi.getType().equals("I")){	//影响指标
				try {
					influKpiName += kpi.getKpi().getName() + ",";
				} catch (Exception e) {
					// ENDO: handle exception
				}
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
				try {
					influProcessureName += processure.getProcess().getName() + ",";
				} catch (Exception e) {
					// ENDO: handle exception
				}
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
			//map.put("isInherit", o_dicBO.findDictEntryById(risk.getIsInherit()).getName());
			//金鹏祥--验证字典实体start
			DictEntry dictEntry = o_dicBO.findDictEntryById(risk.getIsInherit());
			if(null != dictEntry){
				map.put("isInherit", o_dicBO.findDictEntryById(risk.getIsInherit()).getName());
			}
			//金鹏祥--验证字典实体end
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
			map.put("isFix", o_dicBO.findDictEntryById(risk.getIsFix()).getName());
		}
		
		//是否启用
		if(null == risk.getIsUse()){
			map.put("isUse","");
		}else{
			map.put("isUse", o_dicBO.findDictEntryById(risk.getIsUse()).getName());
		}

		return map;
	}
	
	/**
	 * 风险添加页面模板列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findTemplateList")
	public Map<String,Object> findTemplateList(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<Template> templateList = o_templateBO.findTemplateByQuery(null);
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		for (Template template : templateList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", template.getId());
			item.put("name",template.getName());
			datas.add(item);
		}
		
		map.put("totalCount", datas.size());
		map.put("datas", datas);
		
		return map;
	}
	
	/**
	 * 部门风险
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @param type
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findAllRiskEventByOrgId")
	public Map<String, Object> findAllRiskEventByOrgId(int start, int limit, String query, String sort,String type,String id){
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Map> page = new Page<Map>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        String dir = "ASC";
        String sortColumn = null;
		if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
		if("org".equals(type)){
		    page = o_riskCmpBO.findAllRiskEventByOrgId(id, page, sortColumn, dir, query);
		}else if("myfolder".equals(type)){
		    page = o_riskCmpBO.findAllRiskEventByMyFolder(id, page, sortColumn, dir, query);
		}else if("risk".equals(type)){
		    page = o_riskCmpBO.findRiskEventByRiskId(id, page, sortColumn, dir, query,null);
		}
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		List<Map> list = page.getResult();
		
		
  		String[] ids = new String[list.size()];
  		Set<String> idSet = new HashSet<String>();
  		for(int i=0;i<list.size();i++){
  			Map r = (Map)list.get(i);
  			ids[i] = r.get("id").toString();
  			//把上级节点id都存起来，去掉重复的
  			if(r.get("idSeq")!=null){
  				String[] idsTemp = r.get("idSeq").toString().split("\\.");
  				idSet.addAll(Arrays.asList(idsTemp));
  			}
  		}
  		Object[] idSeqArr = idSet.toArray();
  		String[] idSeqs = new String[idSeqArr.length];
  		for(int i=0;i<idSeqs.length;i++){
  			idSeqs[i] = idSeqArr[i].toString();
  		}

  	    //1.获取风险事件的责任部门名称：‘内控部，生成部’
  		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
  		//2.获取风险的所有上级节点
  		Map<String,Object> riskMap = o_riskCmpBO.findParentRiskNameByRiskIds(idSeqs);
		
		Map<String, Object> item = null;
		for (Map event: list) {
			String eventId = event.get("id").toString();
			item = new HashMap<String, Object>();
			item.put("id",event.get("id").toString());
			item.put("name",event.get("name").toString());
			item.put("etrend",event.get("etrend").toString());
			item.put("assessementStatus",event.get("assessementStatus").toString());
			
			//风险的责任部门
			Object respDeptName = ((Map)orgMap.get("respDeptMap")).get(eventId);
			if(respDeptName==null){
				item.put("respDeptName","");
			}else{
				item.put("respDeptName",respDeptName.toString());
			}
			//风险的相关部门
			Object relaDeptName = ((Map)orgMap.get("relaDeptMap")).get(eventId);
			if(relaDeptName==null){
				item.put("relaDeptName","");
			}else{
				item.put("relaDeptName",relaDeptName.toString());
			}
			
			//所属风险	获取风险事件的所以上级风险名称：‘战略风险>战略管理风险’
			StringBuffer belongRisk = new StringBuffer();
			if(event.get("idSeq") != null){
				String idSeqStr = event.get("idSeq").toString().replace(".", ",");
				String[] idSeq = idSeqStr.split(",");
				for(int i=0;i<idSeq.length;i++){
					if(!idSeq[i].equals("") && !idSeq[i].equals(eventId)){
						belongRisk.append(((Risk)riskMap.get(idSeq[i])).getName());
						belongRisk.append(">");
					}
				}
				if(belongRisk.length() > 1){
				    belongRisk.append(belongRisk.substring(0, belongRisk.length()-1));
				}
			}
			item.put("belongRisk",belongRisk);
	  		
			//上级风险
			int pos = belongRisk.lastIndexOf(">")+1;
			if(pos!=-1){
				item.put("parentName",belongRisk.substring(pos));
			}else{
				item.put("parentName","");
			}
			item.put("parentId",event.get("parentId").toString());
			
			//风险归档状态
			item.put("archiveStatus",event.get("archiveStatus").toString());
			
			//是否是最新风险事件.规则:最近30天内的都是最新事件
			if(event.get("lastModifyTime") != null){
				Long t = ((Date)event.get("lastModifyTime")).getTime();
				Long curentTime = new Date().getTime();
				Long diff = 30*24*60*60*1000l;
				if((curentTime - t)<diff){
					item.put("isNew",true);
				}else{
					item.put("isNew",false);
				}
			}else{
				item.put("isNew",false);
			}
			
			datas.add(item);
		}
		
		map.put("totalCount",page.getTotalItems());
		map.put("datas", datas);
		
		return map;
	}
	
	/**
	 * 获取风险列表，可以根据风险分类编号、组织编号、指标编号和流程编号得到风险或风险事件列表
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @param type	按照不同的分类来查询风险
	 * @param id    分类id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findEventById")
	public Map<String, Object> findRiskEventById(int start, int limit, String query, String sort,String type,String id,String schm){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Page<Map> page = new Page<Map>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        String dir = "ASC";
        String sortColumn = null;
		if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
		page = o_riskCmpBO.findRiskEventByRiskIdForSf2(id, page, sortColumn, dir, query,schm);
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		List<Map> list = page.getResult();
  		String[] ids = new String[list.size()];
  		Set<String> idSet = new HashSet<String>();
  		for(int i=0;i<list.size();i++){
  			Map r = (Map)list.get(i);
  			ids[i] = r.get("id").toString();
  			//把上级节点id都存起来，去掉重复的
  			if(r.get("idSeq")!=null){
  				String[] idsTemp = r.get("idSeq").toString().split("\\.");
  				idSet.addAll(Arrays.asList(idsTemp));
  			}
  		}
  		Object[] idSeqArr = idSet.toArray();
  		String[] idSeqs = new String[idSeqArr.length];
  		for(int i=0;i<idSeqs.length;i++){
  			idSeqs[i] = idSeqArr[i].toString();
  		}
  		//2.获取风险事件的最新历史记录
  		//Map<String,Object> historyMap = o_riskCmpBO.findLatestRiskAdjustHistoryByRiskIds(ids);
  	    //1.获取风险事件的责任部门名称：‘内控部，生成部’
  		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
  		//2.获取风险的所有上级节点
  		Map<String,Object> riskMap = o_riskCmpBO.findParentRiskNameByRiskIds(idSeqs);
		
		Map<String, Object> item = null;
		for (Map event: list) {
			String eventId = event.get("id").toString();
			item = new HashMap<String, Object>();
			item.put("id",event.get("id").toString());
			item.put("name",event.get("name").toString());
			item.put("assessementStatus",event.get("assessementStatus").toString());
			item.put("responseText", event.get("responseText").toString());
			//风险的责任部门
			Object respDeptName = ((Map)orgMap.get("respDeptMap")).get(eventId);
			if(respDeptName==null){
				item.put("respDeptName","");
			}else{
				item.put("respDeptName",respDeptName.toString());
			}
			//风险的相关部门
			Object relaDeptName = ((Map)orgMap.get("relaDeptMap")).get(eventId);
			if(relaDeptName==null){
				item.put("relaDeptName","");
			}else{
				item.put("relaDeptName",relaDeptName.toString());
			}
			
			//所属风险	获取风险事件的所以上级风险名称：‘战略风险>战略管理风险’
			StringBuffer belongRisk = new StringBuffer();
			if(event.get("idSeq") != null){
				String idSeqStr = event.get("idSeq").toString().replace(".", ",");
				String[] idSeq = idSeqStr.split(",");
				for(int i=0;i<idSeq.length;i++){
					if(!idSeq[i].equals("") && !idSeq[i].equals(eventId)){
						belongRisk.append(((Risk)riskMap.get(idSeq[i])).getName());
						belongRisk.append(">");
					}
				}
				if(belongRisk.length()>0){
					belongRisk.append(belongRisk.substring(0, belongRisk.length()-1));
				}
			}
			item.put("belongRisk",belongRisk);
	  		
			//上级风险
			int pos = belongRisk.lastIndexOf(">")+1;
			if(pos!=-1){
				item.put("parentName",belongRisk.substring(pos));
			}else{
				item.put("parentName","");
			}
			item.put("parentId",event.get("parentId").toString());
			
			//风险归档状态
			item.put("archiveStatus",event.get("archiveStatus").toString());
			datas.add(item);
		}
		
		map.put("totalCount",page.getTotalItems());
		map.put("datas", datas);
		
		return map;
	}
	
	/**
	 * 查询风险下的潜在风险分类,主要用于风险事件组件点击节点的查询
	 * 
	 * @author 郑军祥
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findPotentialRiskByRiskId.f")
	public Map<String, Object> findPotentialRiskByRiskId(String id,
			String query) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Risk> page = new Page<Risk>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		page = o_riskCmpBO.findRiskCatalogByRiskId(id, page, null, null, query);
		List<Risk> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Risk r : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", r.getId());
			item.put("code", r.getCode());
			item.put("name", r.getName());
			String assessementStatus = "";
			if(r.getAdjustHistory()!=null && r.getAdjustHistory().size()>0){
				RiskAdjustHistory[] historyArr = new RiskAdjustHistory[r.getAdjustHistory().size()];
				r.getAdjustHistory().toArray(historyArr);
				assessementStatus = historyArr[0].getAssessementStatus();
			}
			item.put("assessementStatus", assessementStatus);
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}
	
	/**
	 * 查询风险下的潜在风险事件,主要用于风险事件组件点击节点的查询
	 * 
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findPotentialRiskEventByRiskId")
	public Map<String, Object> findPotentialRiskEventByRiskId(String id,
			String query,String schm,String planType,String planId) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Map> page = new Page<Map>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		page = o_riskCmpBO.findRiskEventByRiskId(id, page, null, null, query,schm,planType,planId);
		List<Map> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Map m : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", m.get("id").toString());
			item.put("code", m.get("code")==null?"":m.get("code").toString());
			item.put("name", m.get("name").toString());
			item.put("assessementStatus", m.get("assessementStatus"));
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}
	
	/**
	 * 查询组织下的潜在风险事件
	 * 
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findPotentialRiskEventByOrgId")
	public Map<String, Object> findPotentialRiskEventByOrgId(String id,
			String query,String schm) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Map> page = new Page<Map>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		//未查询到具体使用页面，schm参数先设置为null wzr
		page = o_riskCmpBO.findRiskEventByOrgId(id, page, null, null, query,schm);
		List<Map> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Map m : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", m.get("id").toString());
			item.put("code", m.get("code")==null?"":m.get("code").toString());
			item.put("name", m.get("name").toString());
			item.put("assessementStatus", m.get("assessementStatus"));
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}

	/**
	 * 查询指标下的潜在风险事件
	 * id 可以是目标id，也可以是指标id：目标id_指标id
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findPotentialRiskEventByKpiId")
	public Map<String, Object> findPotentialRiskEventByKpiId(String id,
			String query) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Map> page = new Page<Map>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		
		//判断传入的是目标id还是指标id
		String type = "sm";	//kpi
		String[] ids = id.split("_");
		if(ids.length>1){
			id = ids[1];
			List<String> idList = new ArrayList<String>();
            idList.add(id);
            //尚未找到使用页面，schm风险分库标识设置为null wzr
			page = o_riskCmpBO.findRiskEventByKpiIds(idList, page, null, null, query, null);
		}else{
			//尚未找到使用页面，schm风险分库标识设置为null wzr
			page = o_riskCmpBO.findRiskEventByStrategyMapId(id, page, null, null, query, null);
		}
		
		List<Map> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Map m : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", m.get("id").toString());
			item.put("code", m.get("code")==null?"":m.get("code").toString());
			item.put("name", m.get("name").toString());
			item.put("assessementStatus", m.get("assessementStatus"));
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}

	/**
	 * 查询流程下的潜在风险事件
	 * 
	 * @author 郑军祥
	 * @param id
	 *            组织id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findPotentialRiskEventByProcessId")
	public Map<String, Object> findPotentialRiskEventByProcessId(String id,
			String query) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Map> page = new Page<Map>();
		page.setPageNo(1);
		page.setPageSize(1000000);
		//尚未找到使用页面，schm分库标识设置为null wzr
		page = o_riskCmpBO.findRiskEventByProcessId(id, page, null, null, query,null);
		List<Map> list = page.getResult();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (Map m : list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", m.get("id").toString());
			item.put("code", m.get("code")==null?"":m.get("code").toString());
			item.put("name", m.get("name").toString());
			item.put("assessementStatus", m.get("assessementStatus"));
			datas.add(item);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}
	
	
	/**
	 * 风险树的构建
	 * 用于风险事件组件的风险树显示
	 * @author 郑军祥
	 * @param node			树id
	 * @param query		  	查询条件
	 * @param subCompany	当登陆人是集团时，风险树是否显示子节点。 这个功能暂时没有做
	 * @param showLight		是否现在风险状态灯
	 * @param checkable		设置是否是复选树
	 * @param chooseId		当为复选树时，设置选中节点的初始值，格式：‘no001,no002’
	 * @param onlyLeaf		风险树选择风险分类时，标识是否只有叶子节点可选
	 * @return
	 */
    @ResponseBody
	@RequestMapping(value = "/cmp/risk/getRiskTreeRecord")
    public List<Map<String, Object>> getRiskTreeRecord(String node, String query, Boolean subCompany, 
    		Boolean showLight, Boolean checkable,String chooseId,Boolean onlyLeaf,Boolean showAllUsed,
    		String schm){
    	if(subCompany==null){
    		subCompany = false;
    	}
    	if(showLight==null){
    		showLight = false;
    	}
    	if(checkable==null){
    		checkable = false;
    	}
    	if(onlyLeaf==null){
    		onlyLeaf = false;
    	}
    	if(showAllUsed==null){
    		showAllUsed = false;
    	}
    	return o_riskCmpBO.getRiskTreeRecord(node, query, subCompany, showLight, checkable, chooseId,onlyLeaf,showAllUsed,schm);
    }
    
    /**
	 * 风险树组件初始化
	 * 用于风险分类选择组件
	 * @author 郑军祥
	*/
	@ResponseBody
    @RequestMapping(value = "/cmp/risk/getRiskByIds")
    public Map<String,Object> getRiskByIds(String ids) throws Exception{

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
			List<HashMap<String, Object>> list = o_riskCmpBO.findRiskByIdSet(idSet);
			
			map.put("success", true);
			map.put("data", list);
		}
		return map;
	}
	
    /**
     * 风险分析查询
     * 
     * @author 张健
     * @param id 所查询节点的id
     * @param type 节点类型 sm 战略目标，sc 记分卡
     * @return
     * @date 2013-9-3
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/findriskanalysis.f")
    public Map<String, Object> findRiskAnalysis(int start, int limit, String query, String sort, String id, String type) {
        String property = "";
        String direction = "";

        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                property = jsobj.getString("property");
                direction = jsobj.getString("direction");
            }
        } else {
            property = "kpi.KPI_NAME";
            direction = "ASC";
        }
        List<Object[]> objList = o_riskCmpBO.findRiskAnalysis(query, property, direction, id, type);
        String[] ids = new String[objList.size()];
        Map<String, String> kpiMap = new HashMap<String, String>();
        int z = 0;
        for (Object[] obs : objList) {
            kpiMap.put(obs[0].toString(), obs[1].toString());
            ids[z] = obs[2].toString();
            z++;
        }
        Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
        List<Map<String, Object>> alldatas = new ArrayList<Map<String, Object>>();

        for (Object[] obs : objList) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", obs[0].toString() + "_" + obs[2].toString());
            data.put("aimTarget", obs[1].toString());
            data.put("riskname", obs[3].toString());
            Risk risk = o_riskCmpBO.findRiskById(obs[2].toString());
            StringBuffer kpiname = new StringBuffer();
            Set<KpiRelaRisk> krelarSet = risk.getKpiRelaRisks();
            for (KpiRelaRisk krelar : krelarSet) {
                if (krelar.getType().equals("RM")) {
                    kpiname.append(krelar.getKpi().getName());
                    kpiname.append(",");
                }
            }
            data.put("riskMeasure", StringUtils.isBlank(kpiname.toString()) ? "" : kpiname.substring(0, kpiname.length() - 1));
            //风险的责任部门
            Object respDeptName = ((Map)orgMap.get("respDeptMap")).get(obs[2].toString());
            if(respDeptName==null){
                data.put("respDeptName","");
            }else{
                data.put("respDeptName",respDeptName.toString());
            }
            // 状态和趋势
            if (obs[4] == null) {
                data.put("assessementStatus", "");
            } else {
                data.put("assessementStatus", obs[4].toString());
            }
            data.put("archiveStatus", null == obs[5]?"":obs[5].toString());
            alldatas.add(data);
        }
        if(StringUtils.isBlank(query)){
            List<Object[]> allKpi = o_riskCmpBO.findAllKpi(id, type);
            for (Object[] obs : allKpi) {
                String kid = obs[0].toString();
                if (StringUtils.isBlank(kpiMap.get(kid))) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("id", kid + "_");
                    data.put("aimTarget", obs[1].toString());
                    data.put("riskname", "");
                    data.put("riskMeasure", "");
                    data.put("respDeptName","");
                    data.put("assessementStatus", "");
                    alldatas.add(data);
                }
            }
        }
        //重新分页
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        int pageNo = limit == 0 ? 1 : start / limit + 1;
        int pageSize = limit;
        int allNum = alldatas.size();
        int allPage = allNum % pageSize == 0 ? allNum / pageSize : allNum / pageSize + 1;
        if (pageNo < allPage) {
            // 不是最后一页
            for (int i = 0; i < pageSize; i++) {
                datas.add(alldatas.get((pageNo - 1) * limit + i));
            }
        } else {
            pageSize = allNum % pageSize;
            for (int i = 0; i < pageSize; i++) {
                datas.add(alldatas.get((pageNo - 1) * limit + i));
            }
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("totalCount", alldatas.size());
        map.put("datas", datas);
        return map;
    }

    /**
     * 根据id查询所关联的风险
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-27
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/findinflukpi.f")
    public Map<String, Object> findInfluKpi(String kpiid, String id, String type) {
        List<Object[]> riskList = o_riskCmpBO.findInfluKpi(kpiid, id, type);
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        for (Object[] obs : riskList) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", obs[0] == null ? "" : obs[0].toString());
            data.put("code", obs[1] == null ? "" : obs[1].toString());
            data.put("name", obs[2] == null ? "" : obs[2].toString());
            datas.add(data);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("datas", datas);
        return map;
    }

    /**
     * 风险关联方法
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-28
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/savekpirelarisk.f")
    public Map<String, Object> saveKpiRelaRisk(String kpiid, String riskids, String id, String type) {
        o_riskCmpBO.saveKpiRelaRisk(kpiid, riskids, id, type);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return map;
    }

    /**
     * 显示所有维度
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-9-11
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/finddimensiondimlist.f")
    public Map<String, Object> findDimensionDimList() {
        Map<String, Object> map = new HashMap<String, Object>();
        String companyId = UserContext.getUser().getCompanyid();
        String jtId = o_orgGridBO.findOrgByParentIdIsNUll().getId();
        List<Dimension> list = o_dimensionBO.findDimensionsByCompanyId(companyId, jtId);
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        for (Dimension dim : list) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", dim.getId());
            item.put("name", dim.getName());
            datas.add(item);
        }
        map.put("totalCount", datas.size());
        map.put("datas", datas);
        map.put("success", true);
        return map;
    }

    /**
     * 生成风险图谱
     * 
     * @author 张健
     * @param id 查询值ID
     * @param type 类型 risk：风险，org：机构，sm：目标，process：流程
     * @param xvalue X轴维度
     * @param yvalue Y轴维度
     * @param showgroup 是否显示集团内容
     * @param assessPlanId 评估模板ID
     * @param
     * @return
     * @date 2013-9-11
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/getheatmap.f")
    public Map<String, Object> getHeatMap(String id, String type, String xvalue, String yvalue, boolean showgroup,String assessPlanId, String meid, String schm) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer xmlData = new StringBuffer();
        List<Object[]> xRiskList = o_riskCmpBO.findDimRiskAll(id, type, xvalue, showgroup, assessPlanId,schm);
        List<Object[]> yRiskList = o_riskCmpBO.findDimRiskAll(id, type, yvalue, showgroup, assessPlanId,schm);
        List<Score> xscore = o_dimensionBO.findScoreBySome(xvalue, null);
        List<Score> yscore = o_dimensionBO.findScoreBySome(yvalue, null);
        Dimension xDim = o_riskCmpBO.findDimensionDimList(xvalue).get(0);
        Dimension yDim = o_riskCmpBO.findDimensionDimList(yvalue).get(0);
        if(yscore.size()==0 || xscore.size()==0){
            map.put("data", "");
            map.put("success", true);
            return map;
        }
        int[][] xmlArray = new int[yscore.size()][xscore.size()];
        if (xRiskList.size() != 0 && yRiskList.size() != 0) {
            Map<String, String> xRiskScore = new HashMap<String, String>();
            Map<String, String> yRiskScore = new HashMap<String, String>();
            for (Object[] obs : xRiskList) {
                xRiskScore.put(obs[0].toString(), obs[3].toString());
            }
            for (Object[] obs : yRiskList) {
                yRiskScore.put(obs[0].toString(), obs[3].toString());
            }
            // 存数量的数组，初始值0
            for (int i = 0; i < yRiskList.size(); i++) {
                String riskid = yRiskList.get(i)[0].toString();
                if (StringUtils.isNotBlank(yRiskScore.get(riskid)) && StringUtils.isNotBlank(xRiskScore.get(riskid))) {
                    int x = Integer.valueOf(xRiskScore.get(riskid).toString());
                    int y = Integer.valueOf(yRiskScore.get(riskid).toString());
                    if(x <=0 || y <= 0){
                        continue;
                    }
                    xmlArray[y - 1][x - 1] = xmlArray[y - 1][x - 1] + 1;
                }
            }
        }
        xmlData.append("<chart showBorder='0' bgColor='FFFFFF' baseFontSize='15' mapByCategory='1' chartRightMargin='50' ");
        xmlData.append(" xAxisName='"+xDim.getName()+"' yAxisName='"+yDim.getName()+"' > ");
        // Y轴列名
        xmlData.append("<rows>");
        for (int i = yscore.size() - 1; i >= 0; i--) {
            // 倒序
            Score ys = yscore.get(i);
            xmlData.append("<row id='" + ys.getName() + "' />");
        }
        xmlData.append("</rows>");
        // x轴行名
        xmlData.append("<columns>");
        for (Score xs : xscore) {
            xmlData.append("<column id='" + xs.getName() + "' />");
        }
        xmlData.append("</columns>");
        // 显示内容
        xmlData.append("<dataset  >");
        for (int i = yscore.size() - 1; i >= 0; i--) {
            Score ys = yscore.get(i);
            for (int j = 0; j < xscore.size(); j++) {
                Score xs = xscore.get(j);
                String xmlValue = "";
                if (xmlArray[i][j] != 0) {
                    xmlValue = String.valueOf(xmlArray[i][j]);
                }
                if ("".equals(xmlValue)) {
                    xmlData.append("<set rowId='" + ys.getName() + "' columnId='" + xs.getName() + "' ");
                } else {
                    xmlData.append("<set rowId='" + ys.getName() + "' columnId='" + xs.getName() + "' value='" + xmlValue + "' toolText='"
                            + yDim.getName() + ':' + ys.getName() + "," + xDim.getName() + ":" + xs.getName() + ",数量：" + xmlArray[i][j] + "' ");
                    if(StringUtils.isNotBlank(assessPlanId)){
                        xmlData.append(" link='JavaScript:Ext.getCmp(\""+meid+"\").showHeatRiskGridPanel(\""+id+"\",\""+type+"\",\""+xvalue+"\",\""+yvalue+"\","+showgroup+",\""+assessPlanId+"\",\""+String.valueOf(j+1)+"\",\""+String.valueOf(i+1)+"\");' ");
                    }else{
                        xmlData.append(" link='JavaScript:Ext.getCmp(\""+meid+"\").showHeatRiskGridPanel(\""+id+"\",\""+type+"\",\""+xvalue+"\",\""+yvalue+"\","+showgroup+","+null+",\""+String.valueOf(j+1)+"\",\""+String.valueOf(i+1)+"\");' ");
                    }
                }
                if ((i == 4 && j == 0) || (i == 3 && j == 0) || (i == 3 && j == 1) || (i == 2 && j == 1) || (i == 2 && j == 2)
                        || (i == 1 && j == 2) || (i == 1 && j == 3) || (i == 0 && j == 3) || (i == 0 && j == 4)) {
                    xmlData.append(" color='EDC738' ");// 黄色
                } else if ((i == 2 && j == 0) || (i == 1 && j == 0) || (i == 1 && j == 1) || (i == 0 && j == 0) || (i == 0 && j == 1)
                        || (i == 0 && j == 2)) {
                    xmlData.append(" color='8EA715' ");// 绿色
                } else {
                    xmlData.append(" color='BB1610' ");// 红色
                }

                xmlData.append(" />");
            }
        }
        xmlData.append("</dataset>");
        // xmlData.append("<colorRange gradient='0'>");
        // xmlData.append("<color minValue='0' maxValue='5' code='8EA715' label='低'/>");
        // xmlData.append("<color minValue='5' maxValue='10' code='EDC738' label='中'/>");
        // xmlData.append("<color minValue='10' maxValue='15' code='BB1610' label='高 '/>");
        // xmlData.append("</colorRange>");
        xmlData.append("<styles>");
        xmlData.append("<definition>");
        xmlData.append("<style name='DataValueStyle' type='font' color='FFFFFF' size='20' bold='1'/>");
        xmlData.append("</definition>");
        xmlData.append("<application>");
        xmlData.append("<apply toObject='DataValues' styles='DataValueStyle' />");
        xmlData.append("</application>");
        xmlData.append("</styles>");
        xmlData.append("</chart>");
        map.put("data", xmlData);
        map.put("success", true);
        return map;
    }
    
    
    /**
     * 生成分类分析
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-9-18
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/getriskgroupcount.f")
    public Map<String, Object> getRiskGroupCount(String id, String type, boolean showgroup ,String assessPlanId) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer xmlData = new StringBuffer();
        List<Object[]> groupCountList = o_riskCmpBO.findRiskGroupCount(id, type, showgroup, assessPlanId);
        if(groupCountList != null && groupCountList.size() != 0){
            String[] ids = new String[groupCountList.size()];
            for(int i=0;i<groupCountList.size();i++){
                Object[] obs = groupCountList.get(i);
                ids[i] = obs[0].toString();
            }
            Map<String,Object> riskMap = o_riskCmpBO.findParentRiskNameByRiskIds(ids);
            xmlData.append("<chart showBorder='0' bgColor='FFFFFF' legendShadow='0' bgAlpha='30,100' bgAngle='45' pieYScale='50' startingAngle='175'  smartLineColor='7D8892' smartLineThickness='2' baseFontSize='12' showLegend='1' legendShadow='0' showPlotBorder='1' >");
            for(Object[] obs : groupCountList){
                xmlData.append("<set label='"+((Risk)riskMap.get(obs[0].toString())).getName()+"' value='"+obs[1].toString()+"' />");
            }
            xmlData.append("<styles>")
            .append("<definition>")
            .append("<style name='CaptionFont' type='FONT' face='Verdana' size='20' color='000000' bold='1' /><style name='LabelFont' type='FONT' color='000000' bold='1'/>")
            .append("</definition>")
            .append("<application>")
            .append("<apply toObject='DATALABELS' styles='LabelFont' /><apply toObject='CAPTION' styles='CaptionFont' />")
            .append("</application>")
            .append("</styles>")
            .append("</chart>");
        }
        map.put("data", xmlData);
        map.put("success", true);
        return map;
    }
    
    /**
     * 查询历史记录，生成趋势图
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-9-18
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/getrisktrendline.f")
    public Map<String, Object> getRiskTrendLine(String id, String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer xmlData = new StringBuffer();
        List<Object[]> trendList = o_riskCmpBO.getRiskTrendLine(id, type);//趋势图数据
        List<Object> alarmList = o_riskCmpBO.getAlarmValue();
        if(trendList.size() != 0 && alarmList.size() != 0){
            List<Object> maxList = o_riskCmpBO.getMaxRiskTrendLine(id, type);//获取最大趋势值
            float maxvalue = Float.valueOf(maxList.get(0).toString());
            int ymaxValue = 0;
            if(maxvalue <= Float.valueOf(alarmList.get(1).toString())){
                ymaxValue = Integer.valueOf(alarmList.get(1).toString()) + 10;
            }else if(Float.valueOf(alarmList.get(1).toString()) < maxvalue){
                ymaxValue = Integer.valueOf(maxList.get(0).toString().substring(0, maxList.get(0).toString().indexOf("."))) + 10;
            }
            xmlData.append("<chart canvasBorderColor='C0C0C0' showBorder='0' yAxisMinValue='0' yAxisMaxValue='"+ymaxValue+"' bgColor='FFFFFF' yAxisName='实际值' yAxisValuesStep='1' numdivlines='5' showAlternateHGridColor='0' showValues='1' decimals='2' labelDisplay='rotate' slantLabels='1' >")
            .append("<categories>");
            for(Object[] obs : trendList){
            	if(null != obs[0]){
            		xmlData.append("<category label='"+obs[0].toString().substring(0, 10)+"' />");
            	}
            }
            xmlData.append("</categories>");
            xmlData.append("<dataset>");
            for(Object[] obs : trendList){
                xmlData.append("<set value='"+obs[1].toString()+"' />");
            }
            xmlData.append("</dataset>");
            xmlData.append("<trendlines>");
            if(maxvalue <= Float.valueOf(alarmList.get(0).toString())){
                xmlData.append("<line startvalue='0'  endValue='"+alarmList.get(0).toString()+"' displayValue='低' color='8EA715' isTrendZone='1' alpha='75' valueOnRight='1' />");
                xmlData.append("<line startvalue='"+alarmList.get(0).toString()+"'  endValue='"+alarmList.get(1).toString()+"' displayValue='中' color='EDC738' isTrendZone='1' alpha='75' valueOnRight='1' />");
                xmlData.append("<line startvalue='"+alarmList.get(1).toString()+"'  endValue='"+ymaxValue+"' displayValue='高' color='BB1610' isTrendZone='1' alpha='75' valueOnRight='1' />");
            }else if(maxvalue <= Float.valueOf(alarmList.get(1).toString())){
                xmlData.append("<line startvalue='0'  endValue='"+alarmList.get(0).toString()+"' displayValue='低' color='8EA715' isTrendZone='1' alpha='75' valueOnRight='1' />");
                xmlData.append("<line startvalue='"+alarmList.get(0).toString()+"'  endValue='"+alarmList.get(1).toString()+"' displayValue='中' color='EDC738' isTrendZone='1' alpha='75' valueOnRight='1' />");
                xmlData.append("<line startvalue='"+alarmList.get(1).toString()+"'  endValue='"+ymaxValue+"' displayValue='高' color='BB1610' isTrendZone='1' alpha='75' valueOnRight='1' />");
            }else if(Float.valueOf(alarmList.get(1).toString()) < maxvalue){
                xmlData.append("<line startvalue='0'  endValue='"+alarmList.get(0).toString()+"' displayValue='低' color='8EA715' isTrendZone='1' alpha='75' valueOnRight='1' />");
                xmlData.append("<line startvalue='"+alarmList.get(0).toString()+"'  endValue='"+alarmList.get(1).toString()+"' displayValue='中' color='EDC738' isTrendZone='1' alpha='75' valueOnRight='1' />");
                xmlData.append("<line startvalue='"+alarmList.get(1).toString()+"'  endValue='"+ymaxValue+"' displayValue='高' color='BB1610' isTrendZone='1' alpha='75' valueOnRight='1' />");
            }
            xmlData.append("</trendlines>")
            .append("</chart>");
        }
        map.put("data", xmlData);
        map.put("success", true);
        return map;
    }
    
    
    /**
     * 风险图谱详细列表
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-9-30
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/getheatriskgrid.f")
    @SuppressWarnings("unchecked")
    public Map<String, Object> getHeatRiskGrid(String id, String type, String xvalue, String yvalue, boolean showgroup,String assessPlanId,String xscore,String yscore,int start, int limit, String query, String sort) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        Map<String, Object> result = o_riskCmpBO.getHeatRiskGrid(id, type, xvalue, yvalue, showgroup ,assessPlanId,xscore,yscore,start,limit);
        List<Object[]> trendList = (List<Object[]>) result.get("list");
        
        String[] ids = new String[trendList.size()];
        Set<String> idSet = new HashSet<String>();
        for(int i=0;i<trendList.size();i++){
            Object[] obs = trendList.get(i);
            ids[i] = obs[0].toString();
            //把上级节点id都存起来，去掉重复的
            if(obs[2]!=null){
                String[] idsTemp = obs[2].toString().split("\\.");
                idSet.addAll(Arrays.asList(idsTemp));
            }
        }
        Object[] idSeqArr = idSet.toArray();
        String[] idSeqs = new String[idSeqArr.length];
        for(int i=0;i<idSeqs.length;i++){
            idSeqs[i] = idSeqArr[i].toString();
        }
        //2.获取风险事件的最新历史记录
        //Map<String,Object> historyMap = o_riskCmpBO.findLatestRiskAdjustHistoryByRiskIds(ids);
        //1.获取风险事件的责任部门名称：‘内控部，生成部’
        Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
        //2.获取风险的所有上级节点
        Map<String,Object> riskMap = o_riskCmpBO.findParentRiskNameByRiskIds(idSeqs);
        
        Map<String, Object> item = null;
        for (Object[] obs: trendList) {
            String eventId = obs[0].toString();
            item = new HashMap<String, Object>();
            item.put("id",obs[0].toString());
            item.put("name",obs[1].toString());
            
            //风险的责任部门
            Object respDeptName = ((Map<String,Object>)orgMap.get("respDeptMap")).get(eventId);
            if(respDeptName==null){
                item.put("respDeptName","");
            }else{
                item.put("respDeptName",respDeptName.toString());
            }
            //风险的相关部门
            Object relaDeptName = ((Map<String,Object>)orgMap.get("relaDeptMap")).get(eventId);
            if(relaDeptName==null){
                item.put("relaDeptName","");
            }else{
                item.put("relaDeptName",relaDeptName.toString());
            }
            
            //所属风险  获取风险事件的所以上级风险名称：‘战略风险>战略管理风险’
            StringBuffer belongRisk = new StringBuffer();
            if(obs[2] != null){
                String idSeqStr = obs[2].toString().replace(".", ",");
                String[] idSeq = idSeqStr.split(",");
                for(int i=0;i<idSeq.length;i++){
                    if(!idSeq[i].equals("") && !idSeq[i].equals(eventId)){
                        belongRisk.append(((Risk)riskMap.get(idSeq[i])).getName());
                        belongRisk.append(">");
                    }
                }
                belongRisk.append(belongRisk.substring(0, belongRisk.length()-1));
            }
            item.put("belongRisk",belongRisk);
            
            //上级风险
            int pos = belongRisk.lastIndexOf(">")+1;
            if(pos!=-1){
                item.put("parentName",belongRisk.substring(pos));
            }else{
                item.put("parentName","");
            }
            
            //是否是最新风险事件.规则:最近30天内的都是最新事件
            if(obs[3] != null){
                Long t = ((Date)obs[3]).getTime();
                Long curentTime = new Date().getTime();
                Long diff = 30*24*60*60*1000l;
                if((curentTime - t)<diff){
                    item.put("isNew",true);
                }else{
                    item.put("isNew",false);
                }
            }else{
                item.put("isNew",false);
            }
            
            //打分
            item.put("xAxisCom",obs[5].toString());
            item.put("yAxisCom",obs[6].toString());
            
            datas.add(item);
        }
        
        map.put("totalCount",result.get("count"));
        map.put("datas", datas);
        return map;
    }
    
    /**
     * 通用风险导出excel
     * 
     * @author 张健
     * @param id    类型值
     * @param type 类型
     * @param exportFileName    excel文件名
     * @param sheetName     sheet页名称
     * @param headerData   列名称，JSON格式
     * @param style  风格   analysis：风险分析，event：风险列表
     * @return 
     * @throws Exception 
     * @date 2013-9-30
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/exportriskgrid.f")
    @SuppressWarnings("unchecked")
    public void exportRiskGrid(String id, String type, String exportFileName, 
            String sheetName,  String headerData, String style,
            HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> indexList = new ArrayList<String>();//列ID
        List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();//列表数据
        String[] fieldTitle = null;
        JSONArray headerArray=JSONArray.fromObject(headerData);
        int j = headerArray.size();
        fieldTitle = new String[j];
        for(int i=0;i<j;i++){
            JSONObject jsonObj = headerArray.getJSONObject(i);
            
            if(jsonObj.get("dataIndex").toString().equalsIgnoreCase("assessementStatus")){
            	fieldTitle[i] = "风险状态";
            }else{
            	fieldTitle[i] = jsonObj.get("text").toString();
            }
            indexList.add(jsonObj.get("dataIndex").toString());
        }
        if("analysis".equals(style)){
            Map<String, Object> map = findRiskAnalysis(0, 10000, null, null, id, type);
            listMap = (List<Map<String, Object>>) map.get("datas");
        }else if("event".equals(style)){
            Map<String, Object> map = findRiskEventById(0, 10000, null, null, type, id,null);
            listMap = (List<Map<String, Object>>) map.get("datas");
        }
        for(Map<String,Object> maps : listMap){
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
                        //object[z] = "color|red";
                    	object[z] = "高";
                    }else if("icon-ibm-symbol-6-sm".equals(maps.get(indexList.get(z)))){
                        //object[z] = "color|green";
                    	object[z] = "低";
                    }else if("icon-ibm-symbol-5-sm".equals(maps.get(indexList.get(z)))){
                        //object[z] = "color|yellow";
                    	object[z] = "中";
                    }else if("icon-ibm-symbol-0-sm".equals(maps.get(indexList.get(z)))){
                        object[z] = "color|";
                    }else{
                        object[z] = "无";//maps.get(indexList.get(z));
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
                }else if("archiveStatus".equals(indexList.get(z))){
                    if(Contents.RISK_STATUS_ARCHIVED.equals(maps.get(indexList.get(z)))){
                        object[z] = "已归档";
                    }else if(Contents.RISK_STATUS_EXAMINE.equals(maps.get(indexList.get(z)))){
                        object[z] = "审批中";
                    }else if(Contents.RISK_STATUS_SAVED.equals(maps.get(indexList.get(z)))){
                        object[z] = "待提交";
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
            exportFileName = "风险列表数据.xls";
        }
        
        if(StringUtils.isBlank(sheetName)){
            sheetName = "风险列表";
        }
        //数据，列名称，文件名称，sheet名称，sheet位置
        ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
    }
    
    /**
     * 根据风险id或模板，进行查询模板需要打分的内容,修改会返回具体的分值
     * 
     * @author 张健
     * @param templateid 新增的时候为风险ID，修改的时候为模板ID
     * @param id 历史记录ID，在修改的时候查询分值
     * @param isEdit true:修改，false新增
     * @param type formula为公式计算
     * @return 
     * @date 2013-10-11
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/riskassesscmp.f")
    public Map<String, Object> riskAssessCmp(String templateid,String hisid,boolean isEdit,String value,String type){
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object[]> list = new ArrayList<Object[]>();
        if("risk".equals(type) || "riskevent".equals(type)){
            //模板下所有维度的详细描述
            //维度ID -- 分值ID -- 分值 -- 分值描述 -- 维度名称 -- 分值名称
            HashMap<String, ArrayList<String>> templateDicDescAllMap = o_scoreInstanceBO.findTemplateDicDescAllMap();
            if(isEdit){
                //修改
                if(templateid != null){
                    list = o_riskCmpBO.riskAssessCmp(templateid,hisid,value,type);
                    map.put("datasDesc", templateDicDescAllMap.get(templateid));
                }else if(o_riskCmpBO.findRiskById(value).getTemplate() != null){
                    list = o_riskCmpBO.riskAssessCmp(o_riskCmpBO.findRiskById(value).getTemplate().getId(),hisid,value,type);
                    map.put("datasDesc", templateDicDescAllMap.get(o_riskCmpBO.findRiskById(value).getTemplate().getId()));
                }
            }else{
                if(StringUtils.isNotBlank(templateid)){
                    list = o_riskCmpBO.riskAssessCmp(templateid,null,value,type);
                    map.put("datasDesc", templateDicDescAllMap.get(templateid));
                }
                else if(o_riskCmpBO.findRiskById(value).getTemplate() != null){
                    list = o_riskCmpBO.riskAssessCmp(o_riskCmpBO.findRiskById(value).getTemplate().getId(),null,value,type);
                    map.put("datasDesc", templateDicDescAllMap.get(o_riskCmpBO.findRiskById(value).getTemplate().getId()));
                }else{
                    list = o_riskCmpBO.riskAssessCmp(o_templateBO.findTemplateByType("dim_template_type_sys").getId(),null,value,type);
                    map.put("datasDesc", templateDicDescAllMap.get(o_templateBO.findTemplateByType("dim_template_type_sys").getId()));
                }
            }
        }else{
            if(isEdit){
                list = o_riskCmpBO.riskAssessCmp(null,hisid,value,type);
            }else{
                Object[] obs = new Object[1];
                obs[0] = "";
                list.add(obs);
            }
        }
        map.put("datas", list);
        return map;
    }
    
    /**
     * 根据风险ID查询最新的历史记录，非公式计算
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-12-23
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/riskassessmaketemplate.f")
    public Map<String, Object> riskAssessMakeTemplate(String riskId){
        Map<String, Object> map = new HashMap<String, Object>();
        /*郭鹏
         * 修改是否公式计算 false->true
         * 20170426
         * */
        RiskAdjustHistory riskAdjustHistory = o_riskAdjustHistoryBO.findNowRiskAdjustHistoryByRiskId(riskId, true);
        map.put("templateid", null == riskAdjustHistory?"":riskAdjustHistory.getTemplate() == null?"":riskAdjustHistory.getTemplate().getId());
        map.put("hisId", null == riskAdjustHistory?"":riskAdjustHistory.getId());
        return map;
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/findTemplateAndWeightByRiskIdForSecurity.f")
    public Map<String,Object> findTemplateAndWeightByRiskId(String riskId){
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	List<Map<String,String>> resultLIst = null;
    	Template template = null;
    	// getRiskEntity by riskid
    	Risk risk = o_riskCmpBO.findRiskById(riskId);
    	String companyId =  UserContext.getUser().getCompanyid();
    	//is have assessplanid 
    	if(risk.getRiskAssessPlan() == null){
    		template = risk.getTemplate();
    		if(template == null){
    			rtnMap.put("templateId", null);
    		}else{
    			rtnMap.put("templateId", template.getId());
    			resultLIst = adjustHistoryResultBO.findRiskDimensionValuesByRiskId(riskId); 
    		}
    	}else{
    		template = risk.getRiskAssessPlan().getTemplate();
    		resultLIst =  adjustHistoryResultBO.findAdjustHistoryResultListByAssessPlanId(riskId,risk.getRiskAssessPlan().getId());
    		rtnMap.put("templateId", template.getId());
    		rtnMap.put("planId", risk.getRiskAssessPlan().getId());
    	}
    	if(resultLIst != null && resultLIst.size() > 0){
			Map<String,String> caluMap = new HashMap<String,String>();
			for(Map<String,String> map : resultLIst){
				caluMap.put(map.get("scoreName"), map.get("score"));
			}
			// get score by CaluFormula 
			double riskScore = Utils.eval(Utils.replaceWeightToNum(template.getCaluFormula(),caluMap));
			//get risk status`	`																															`	
			String riskStatus = showRiskTidyBO.getRiskIconByRiskScore(riskScore, companyId);
			rtnMap.put("riskResultScore", resultLIst);
			rtnMap.put("riskStatus", riskStatus);	
			rtnMap.put("riskScore", riskScore);
		}
		return rtnMap;
    }
    
    
    /**
     * 风险直接用模板进行打分的组件
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-12-23
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/riskassessmakecmp.f")
    public Map<String, Object> riskAssessMakeCmp(String templateid,String hisId,boolean isEdit,String riskId){
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object[]> list = null;
        //模板下所有维度的详细描述
        //维度ID -- 分值ID -- 分值 -- 分值描述 -- 维度名称 -- 分值名称
        HashMap<String, ArrayList<String>> templateDicDescAllMap = o_scoreInstanceBO.findTemplateDicDescAllMap();
        if(isEdit){
            list = o_riskCmpBO.riskAssessCmp(templateid,hisId,riskId,"risk");
            map.put("datasDesc", templateDicDescAllMap.get(templateid));
        }else{
            //新增,templateid为风险id
            list = o_riskCmpBO.riskAssessCmp(templateid,null,null,"risk");
            map.put("datasDesc", templateDicDescAllMap.get(templateid));
        }
        map.put("datas", list);
        return map;
    }
    

    /**
     * @author jia.song@pcitc.com
     * @param templateid
     * @desc    get dimens by templateid
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/riskassessmakecmpforsecurity.f")
    public Map<String, Object> riskAssessMakeCmpForSecurity(String templateid){
        Map<String,Object> rtnMap = new HashMap<String,Object>();
    	List<Map<String,String>> tempList = new ArrayList<Map<String,String>>();
    	Map<String,String> map = null;
    	//get weight by templateid
        List<TemplateRelaDimension> relaList = o_templateBO.findTemplateRelaDimensionBySome(null,templateid);
        for(TemplateRelaDimension templateRelaDimension : relaList){
        	map = new HashMap<String,String>();
        	map.put("scoreId", templateRelaDimension.getDimension().getId());
        	map.put("scoreName", templateRelaDimension.getDimension().getName());
        	tempList.add(map);
        }
        rtnMap.put("datas", tempList);
        return rtnMap;
    }
    
    /**
     * 计算风险值，返回风险灯，保存信息
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-12-25
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/riskassesstype.f")
    public Map<String, Object> riskAssessType(String saveinfo,String templateid,String type) throws ParseException{
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String,Object> mapValue = new HashMap<String,Object>();//所有分值
        String[] saveinfos = null;
        saveinfos = saveinfo.split("\\|");//每个维度打分情况
        for(String info : saveinfos){
            String[] infos  = info.split("\\*");
            if(infos.length > 1){
                mapValue.put(infos[0].split(",")[0],infos[1]);
            }
        }
        double riskScoreValue = 0;
        StringBuffer scoreValue = new StringBuffer();
        riskScoreValue = o_riskCmpBO.riskAssessHisStatus(saveinfos,templateid,mapValue);
        if(!"load".equals(type)){
            Set<String> setValue = null;
            setValue = o_riskCmpBO.getRiskAssessHisStatusSet(saveinfos,templateid,mapValue);
            for(String info : setValue){
                String[] infos  = info.split(",");
                scoreValue.append(infos[0]);
                scoreValue.append("*");
                scoreValue.append(infos[1]);
                scoreValue.append("|");
            }
        }else{
            scoreValue.append(saveinfo);
        }
        String riskIcon = "";
        riskIcon = o_riskCmpBO.getRiskIcon(riskScoreValue);
        
        map.put("success", true);
        map.put("riskIcon", riskIcon);//风险灯
        map.put("riskScoreValue", riskScoreValue);//风险值
        map.put("scoreValue", scoreValue);//所有结果
        return map;
    } 
    
    /**
     * 评估模板历史记录重新打分的新增方法
     * 
     * @author 张健
     * @param 
     * @return 
     * @throws ParseException 
     * @date 2013-10-11
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/riskassesssave.f")
    public Map<String, Object> riskAssessSave(String saveinfo,String value,String templateid,String type,String hisid,boolean isEdit) throws ParseException{
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String,Object> mapValue = new HashMap<String,Object>();//所有分值
        String[] saveinfos = null;
        if("risk".equals(type) || "riskevent".equals(type)){
            saveinfos = saveinfo.split("\\|");//每个维度打分情况
            for(String info : saveinfos){
                String[] infos  = info.split("\\*");
                mapValue.put(infos[0].split(",")[0],infos[1]);
            }
        }
        double riskScoreValue = 0;
        Set<String> setValue = null;
        if("risk".equals(type) || "riskevent".equals(type)){
            riskScoreValue = o_riskCmpBO.riskAssessHisStatus(saveinfos,templateid,mapValue);
            setValue = o_riskCmpBO.getRiskAssessHisStatusSet(saveinfos,templateid,mapValue);
        }else{
            riskScoreValue = Double.valueOf(saveinfo);
        }
        if(!isEdit){
            o_riskCmpBO.riskAssessSave(setValue,value,templateid,type,riskScoreValue);
        }else{
            o_riskCmpBO.riskAssessMerge(setValue,value,templateid,type,riskScoreValue,hisid);
        }
        map.put("success", true);
        return map;
    }
    
    /**
     * 历史删除方法
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-10-14
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/riskassessdelete.f")
    public Map<String, Object> riskAssessDelete(String hisid,String type,String value){
        Map<String, Object> map = new HashMap<String, Object>();
        o_riskCmpBO.riskAssessDelete(hisid,type,value);
        map.put("success", true);
        return map;
    }
    
    /**
	 * 获取风险列表，可以根据风险分类编号、组织编号、指标编号和流程编号得到风险或风险事件列表
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @param type	按照不同的分类来查询风险
	 * @param id    分类id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findEventByArchiveState")
	public Map<String, Object> findRiskEventByArchiveState(String state,int start, int limit, String query, String sort){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Page<Map> page = new Page<Map>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        String dir = "ASC";
        String sortColumn = null;
		if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
		page = o_riskCmpBO.findRiskEventByRiskId(state, page, sortColumn, dir, query,null);
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		List<Map> list = page.getResult();
		
		
  		String[] ids = new String[list.size()];
  		Set<String> idSet = new HashSet<String>();
  		for(int i=0;i<list.size();i++){
  			Map r = (Map)list.get(i);
  			ids[i] = r.get("id").toString();
  			//把上级节点id都存起来，去掉重复的
  			if(r.get("idSeq")!=null){
  				String[] idsTemp = r.get("idSeq").toString().split("\\.");
  				idSet.addAll(Arrays.asList(idsTemp));
  			}
  		}
  		Object[] idSeqArr = idSet.toArray();
  		String[] idSeqs = new String[idSeqArr.length];
  		for(int i=0;i<idSeqs.length;i++){
  			idSeqs[i] = idSeqArr[i].toString();
  		}

  	    //1.获取风险事件的责任部门名称：‘内控部，生成部’
  		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
  		//2.获取风险的所有上级节点
  		Map<String,Object> riskMap = o_riskCmpBO.findParentRiskNameByRiskIds(idSeqs);
		
		Map<String, Object> item = null;
		for (Map event: list) {
			String eventId = event.get("id").toString();
			item = new HashMap<String, Object>();
			item.put("id",event.get("id").toString());
			item.put("name",event.get("name").toString());
			item.put("etrend",event.get("etrend").toString());
			item.put("assessementStatus",event.get("assessementStatus").toString());
			
			//风险的责任部门
			Object respDeptName = ((Map)orgMap.get("respDeptMap")).get(eventId);
			if(respDeptName==null){
				item.put("respDeptName","");
			}else{
				item.put("respDeptName",respDeptName.toString());
			}
			//风险的相关部门
			Object relaDeptName = ((Map)orgMap.get("relaDeptMap")).get(eventId);
			if(relaDeptName==null){
				item.put("relaDeptName","");
			}else{
				item.put("relaDeptName",relaDeptName.toString());
			}
			
			//所属风险	获取风险事件的所以上级风险名称：‘战略风险>战略管理风险’
			StringBuffer belongRisk = new StringBuffer();
			if(event.get("idSeq") != null){
				String idSeqStr = event.get("idSeq").toString().replace(".", ",");
				String[] idSeq = idSeqStr.split(",");
				for(int i=0;i<idSeq.length;i++){
					if(!idSeq[i].equals("") && !idSeq[i].equals(eventId)){
						belongRisk.append(((Risk)riskMap.get(idSeq[i])).getName());
						belongRisk.append(">");
					}
				}
				belongRisk.append(belongRisk.substring(0, belongRisk.length()-1));
			}
			item.put("belongRisk",belongRisk);
	  		
			//上级风险
			int pos = belongRisk.lastIndexOf(">")+1;
			if(pos!=-1){
				item.put("parentName",belongRisk.substring(pos));
			}else{
				item.put("parentName","");
			}
			item.put("parentId",event.get("parentId").toString());
			
			//是否是最新风险事件.规则:最近30天内的都是最新事件
			if(event.get("lastModifyTime") != null){
				Long t = ((Date)event.get("lastModifyTime")).getTime();
				Long curentTime = new Date().getTime();
				Long diff = 30*24*60*60*1000l;
				if((curentTime - t)<diff){
					item.put("isNew",true);
				}else{
					item.put("isNew",false);
				}
			}else{
				item.put("isNew",false);
			}
			
			datas.add(item);
		}
		
		map.put("totalCount",page.getTotalItems());
		map.put("datas", datas);
		
		return map;
	}
	/**
	 * 
	 * @param id
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @param year
	 * @param quarter
	 * @param month
	 * @param week
	 * @param eType
	 * @param isNewValue
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/cmp/risk/findRiskRelaKpiById")
	public Map<String, Object> findRiskRelaKpiById(String id,int start, int limit, String query, String sort, String year, String quarter,
            String month, String week, String eType, String isNewValue) {
		Map<String, Object> map = new HashMap<String,Object>();
		List<Map<String, Object>> alldatas = new ArrayList<Map<String, Object>>();
		alldatas = o_riskCmpBO.findRiskRelaKpiById(id,query,sort, year, quarter,
	            month, week, eType, isNewValue);
        //重新分页
        List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
        int pageNo = limit == 0 ? 1 : start / limit + 1;
        int pageSize = limit;
        int allNum = 0;
        if(alldatas !=null && alldatas.size() > 0 ) {
        	allNum = alldatas.size();
        }
        
        int allPage = allNum % pageSize == 0 ? allNum / pageSize : allNum / pageSize + 1;
        if (pageNo < allPage) {
            // 不是最后一页
            for (int i = 0; i < pageSize; i++) {
                datas.add(alldatas.get((pageNo - 1) * limit + i));
            }
        } else {
            pageSize = allNum % pageSize;
            for (int i = 0; i < pageSize; i++) {
                datas.add(alldatas.get((pageNo - 1) * limit + i));
            }
        }
        map.put("totalCount", allNum);
        map.put("datas", datas);
		return map;
	}
	/**
	 * 
	 * @param kpiItems
	 * @param riskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cmp/risk/removeriskrelakpi.f")
	public Map<String, Object> removeriskrelakpi(String kpiItems,String riskId){
		Map<String, Object> map = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(kpiItems) && StringUtils.isNotBlank(riskId)){
			o_riskCmpBO.deleteriskrelakpi(kpiItems,riskId);
		}
		map.put("success", true);
		return map;
	}
	/**
	 * 
	 * @param riskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cmp/risk/findRiskNameById.f")
	public Map<String, Object> findRiskNameById(String riskId){
		Map<String, Object> map = new HashMap<String,Object>();
        if(StringUtils.isNotBlank(riskId)){
        	map.put("riskName", o_riskCmpBO.findRiskById(riskId).getName());
        }        
		map.put("success", true);
		return map;
	}
	
	/**根据风险id查询所关联的指标;列包括{指标名称,单位,频率,所属部门,权重}
	 * @param query指标名称
	 * @param riskId 风险ID
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/cmp/risk/findkpirelarisk.f")
    public Map<String, Object> findkpiRelaRisk(String query,String riskId) {
    	List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
    	Map<String, Object> map = new HashMap<String,Object>();
    	if (StringUtils.isNotBlank(riskId)) {
    		datas = o_riskCmpBO.findRiskRelaKpi(query,riskId);
    	}
    	map.put("datas", datas);
    	return map;
    }
	
   @ResponseBody
   @RequestMapping(value = "/cmp/risk/mergeRiskrelakpi.f")
   public Map<String, Object> mergeRiskrelakpi(String param) {
		Map<String, Object> map = new HashMap<String,Object>();
        if(StringUtils.isNotBlank(param)){
        	o_riskCmpBO.mergeRiskrelakpi(param);
        }        
		map.put("success", true);
		return map;
   }
   
   @ResponseBody
   @RequestMapping(value = "/cmp/risk/deleteRiskrelakpi.f")
   public Map<String, Object> deleteRiskrelakpi(String param) {
        Map<String, Object> map = new HashMap<String,Object>();
        if(StringUtils.isNotBlank(param)){
            o_riskCmpBO.deleteRiskrelakpi(param);
        }        
        map.put("success", true);
        return map;
   }
       
    /**
     * 风险选择组件，我的文件夹节点
     * 
     * @author 张健
     * @param
     * @return
     * @date 2014-1-5
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/cmp/risk/getMyfolderRiskTreeRecord")
    public List<Map<String, Object>> getMyfolderRiskTreeRecord(String node, String query) {
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();// 返回前台的nodelist
        Map<String, Object> myRiskNode = new HashMap<String, Object>();
        myRiskNode.put("id", "my_risk");
        myRiskNode.put("text", "我的风险");
        myRiskNode.put("type", "my_risk");
        myRiskNode.put("leaf", true);
        myRiskNode.put("iconCls", "icon-ibm-icon-metrics");
        nodes.add(myRiskNode);
        
        Map<String, Object> orgRiskNode = new HashMap<String, Object>();
        orgRiskNode.put("id", "org_risk");
        orgRiskNode.put("text", "部门风险");
        orgRiskNode.put("type", "org_risk");
        orgRiskNode.put("leaf", true);
        orgRiskNode.put("iconCls", "icon-ibm-icon-owner");
        nodes.add(orgRiskNode);
        
        return nodes;

    }
    
    /**
     * 我的文件夹，部门风险
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-1-5
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/cmp/risk/findmyfolderRiskEventByRiskId")
    public Map<String, Object> findmyfolderRiskEventByRiskId(String id,
            String query) {
        Map<String, Object> map = new HashMap<String, Object>();
        String value = "";
        Page<Map> page = new Page<Map>();
        page.setPageNo(1);
        page.setPageSize(1000000);
        if("org_risk".equals(id)){
            //部分风险，当前机构的风险
            value = UserContext.getUser().getCompanyid();
            //尚未找到使用页面，schm分库标识设置为null wzr
            page = o_riskCmpBO.findRiskEventByOrgId(value, page, null, null, query, null);
        }else if("my_risk".equals(id)){
            //我的风险，责任人为当前操作员
            value = UserContext.getUser().getEmpid();
            page = o_riskCmpBO.findAllRiskEventByMyFolder(value, page, null, null, query);
        }
        List<Map> list = page.getResult();
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        for (Map m : list) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", m.get("id").toString());
            item.put("code", m.get("code")==null?"":m.get("code").toString());
            item.put("name", m.get("name").toString());
            item.put("assessementStatus", m.get("assessementStatus"));
            datas.add(item);
        }
        map.put("totalCount", page.getTotalItems());
        map.put("datas", datas);

        return map;
    }
	
}
