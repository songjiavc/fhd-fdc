/**
 * RiskBO.java
 * com.fhd.risk.business
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/
/**
 * RiskBO.java
 * com.fhd.risk.business
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19        金鹏祥
 *
 * Copyright (c) 2012, FirstHuida All Rights Reserved.
*/


package com.fhd.ra.business.risk;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.dao.assess.riskTidy.AdjustHistoryResultDAO;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.response.RiskResponseDAO;
import com.fhd.dao.risk.DimensionDAO;
import com.fhd.dao.risk.HistoryEventDAO;
import com.fhd.dao.risk.KpiAdjustHistoryDAO;
import com.fhd.dao.risk.KpiRelaRiskDAO;
import com.fhd.dao.risk.OrgAdjustHistoryDAO;
import com.fhd.dao.risk.ProcessAdjustHistoryDAO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.dao.risk.RiskRelaRiskDAO;
import com.fhd.dao.risk.StrategyAdjustHistoryDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskOrgRelationTemp;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.assess.riskTidy.AdjustHistoryResult;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.KpiRelaOrgEmp;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.response.RiskResponse;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.RiskRelaRisk;
import com.fhd.entity.risk.StrategyAdjustHistory;
import com.fhd.entity.risk.TempSyncRiskrbsScore;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.dic.DictEntryRelation;
import com.fhd.entity.sys.dic.DictEntryRelationType;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.MapListUtil;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.LastLeaderBO;
import com.fhd.ra.business.assess.formulateplan.RiskOrgTempBO;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.business.assess.oper.KpiAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.OrgAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.StrategyAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.TempSyncRiskRbsBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.assess.quaassess.QuaAssessNextBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.assess.summarizing.util.KpiOperBO;
import com.fhd.ra.business.assess.summarizing.util.OrgOperBO;
import com.fhd.ra.business.assess.summarizing.util.ProcessureOperBO;
import com.fhd.ra.business.assess.summarizing.util.RiskRbsOperBO;
import com.fhd.ra.business.assess.summarizing.util.StrategyMapOperBO;
import com.fhd.ra.business.assess.summarizing.util.Times;
import com.fhd.ra.business.assess.util.DynamicDim;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.web.form.KpiMemoForm;
import com.fhd.sys.business.assess.WeightSetBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.dic.DictEntryRelationBO;
import com.fhd.sys.business.organization.OrgGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class RiskCmpBO {
	
	public final static String riskKindDict = "1";		//风险类别
	public final static String relePlateDict = "2";		//设计板块
	public final static String riskTypeDict = "3";		//风险类型
	public final static String innerReasonDict = "4";	//内部动因
	public final static String outterReasonDict = "5";	//外部动因
	public final static String valueChainsDict = "8";	//风险价值链
	public final static String impactTimeDict = "9";    //影响期间
	public final static String responseStrategyDict = "10";	//应对策略
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	//部门
	@Autowired
	private RiskOrgBO o_riskOrgBO;

	//指标
	@Autowired
	private KpiRelaRiskBO o_kpiRelaRiskBO;
	
	//流程
	@Autowired
	private ProcessRelaRiskBO o_processRelaRiskBO;
	
	//字典
	@Autowired
	private DictEntryRelationBO o_dictEntryRelationBo;

	//风险自关联
	@Autowired
	private RiskRelaRiskDAO o_riskRelaRiskDAO;
	
	//指标关联风险
	@Autowired
    private KpiRelaRiskDAO o_kpiRelaRiskDAO;
	
	//维度
	@Autowired
    private DimensionDAO o_dimensionDAO;
	
	@Autowired
    private DimensionBO o_dimensionBO;
	
	@Autowired
    private FormulaSetBO o_formulaSetBO;
	
	@Autowired
    private TimePeriodBO o_timePeriodBO;
	
	@Autowired
    private AdjustHistoryResultBO o_adjustHistoryResultBO;
	
	@Autowired
    private AdjustHistoryResultDAO o_adjustHistoryResultDAO;
	
	/**
	 *	 add by jia.song@pcitc.com
	 */
	@Autowired
	private TempSyncRiskRbsBO tempSyncRiskRbsBO;
	
	@Autowired
	private ShowRiskTidyBO showRiskTidyBO;
	
	/**
	 * 风险历史记录
	 */
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	@Autowired
    private OrgAdjustHistoryDAO o_orgAdjustHistoryDAO;
    @Autowired
    private ProcessAdjustHistoryDAO o_processAdjustHistoryDAO;
    @Autowired
    private StrategyAdjustHistoryDAO o_strategyAdjustHistoryDAO;
    @Autowired
    private KpiAdjustHistoryDAO o_kpiAdjustHistoryDAO;
	@Autowired
    private RiskAdjustHistoryBO  o_riskAdjustHistoryBO;
	@Autowired
    private OrgAdjustHistoryBO o_orgAdjustHistoryBO;
    @Autowired
    private ProcessAdjustHistoryBO o_processAdjustHistoryBO;
    @Autowired
    private StrategyAdjustHistoryBO o_strategyAdjustHistoryBO;
    @Autowired
    private KpiAdjustHistoryBO o_kpiAdjustHistoryBO;
	
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	
	@Autowired
    private WeightSetBO o_weightSetBO;
	
	@Autowired
    private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	@Autowired
    private QuaAssessNextBO o_quaAssessNextBO;
	
	@Autowired
    private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
    private KpiBO o_kpiBO;
	
	
	/**
	 * 指标
	 */
	@Autowired
    private KpiDAO o_kpiDAO;
	
	@Autowired 
	private KpiGatherResultBO o_kpigGatherResultBO;
	
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
	
	/**
     * 字典表
     */
    @Autowired
    private DictBO o_dicBo;
	
	/**
     * 历史事件
     */
    @Autowired
	private HistoryEventDAO o_historyEventDAO;
    @Autowired
    private RiskRbsOperBO o_riskRbsOperBO;
    @Autowired
    private KpiOperBO o_kpiOperBO;
    @Autowired
    private OrgOperBO o_orgOperBO;
    @Autowired
    private ProcessureOperBO o_processureOperBO;
    @Autowired
    private StrategyMapOperBO o_strategyMapOperBO;
    @Autowired
	private RiskLevelBO o_riskLevelBO;
    
    @Autowired
    private StatisticsResultDAO o_statisticsResultDAO;

    //组织机构
    @Autowired
	private SysOrgDAO sysOrgDAO;
    
    @Autowired
    private OrgGridBO o_orgGridBO;
    
    @Autowired
    private RiskScoreObjectDAO o_riskScoreObjectDAO;
    
    //风险库应对信息
    @Autowired
    private RiskResponseDAO o_riskResponseDao;
    
	/**
	 * 添加风险，表单项是最全的
	 * 并且更新上级风险叶子状态
	 * @param riskStructure 风险上级分类，可以多个
	 * @param assessValue   评价打的分值信息
	 */
	@Transactional
	public void saveRisk(Risk risk,String respDeptName,String relaDeptName,
			String respPositionName, String relaPositionName, String riskKind,
			String relePlate, String riskKpiName, String influKpiName,
			String controlProcessureName, String influProcessureName,
			String innerReason, String outterReason,
			String riskReason, String riskInfluence,
			String valueChains,String riskType,
			String impactTime,String responseStrategy,String riskStructure,String assessValue){
		
		//创建时间和最后修改时间,创建人
		risk.setCreateTime(new Date());
		risk.setLastModifyTime(new Date());
		risk.setCreateBy(UserContext.getUser().getEmpid());
		risk.setCreateOrg(sysOrgDAO.get(UserContext.getUser().getMajorDeptId()));
		
		o_riskDAO.merge(risk);
		
		//添加责任部门/人和相关部门/人
		JSONObject object = new JSONObject();
		RiskOrg riskOrg = null;
		if(null!=respDeptName && !"".equals(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				object = (JSONObject)respDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("M");
				//保存责任部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存责任人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		if(null!=relaDeptName && !"".equals(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				object = (JSONObject)relaDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("A");
				//保存相关部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存相关人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		/*
		//添加风险指标和影响指标
		KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
		if(null!=riskKpiName && !"".equals(riskKpiName)){
			String[] riskKpiArray = riskKpiName.split(",");
			for(int i=0;i<riskKpiArray.length;i++){
				String id = riskKpiArray[i];
				//保存风险指标
				Kpi kpi = new Kpi();
				kpi.setId(id);
				kpiRelaRisk = new KpiRelaRisk();
				kpiRelaRisk.setId(UUID.randomUUID().toString());
				kpiRelaRisk.setRisk(risk);
				kpiRelaRisk.setType("RM");
				kpiRelaRisk.setKpi(kpi);
				o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
			}
		}
		if(null!=influKpiName && !"".equals(influKpiName)){
			String[] influKpiArray = influKpiName.split(",");
			for(int i=0;i<influKpiArray.length;i++){
				String id = influKpiArray[i];
				//保存影响指标
				Kpi kpi = new Kpi();
				kpi.setId(id);
				kpiRelaRisk = new KpiRelaRisk();
				kpiRelaRisk.setId(UUID.randomUUID().toString());
				kpiRelaRisk.setRisk(risk);
				kpiRelaRisk.setType("I");
				kpiRelaRisk.setKpi(kpi);
				o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
			}
		}
		
		//添加控制流程和影响流程
		ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
		if(null!=controlProcessureName && !"".equals(controlProcessureName)){
			String[] processArray = controlProcessureName.split(",");
			for(int i=0;i<processArray.length;i++){
				String id = processArray[i];
				//保存控制流程
				Process process = new Process();
				process.setId(id);
				processRelaRisk = new ProcessRelaRisk();
				processRelaRisk.setId(UUID.randomUUID().toString());
				processRelaRisk.setRisk(risk);
				processRelaRisk.setType("C");
				processRelaRisk.setProcess(process);
				o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
			}
		}
		if(null!=influProcessureName && !"".equals(influProcessureName)){
			String[] processArray = influProcessureName.split(",");
			for(int i=0;i<processArray.length;i++){
				String id = processArray[i];
				//保存影响流程
				Process process = new Process();
				process.setId(id);
				processRelaRisk = new ProcessRelaRisk();
				processRelaRisk.setId(UUID.randomUUID().toString());
				processRelaRisk.setRisk(risk);
				processRelaRisk.setType("I");
				processRelaRisk.setProcess(process);
				o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
			}
		}
		
		//添加风险类别和涉及版块
		DictEntryRelation dictRelation = new DictEntryRelation();
		if(null!=riskKind && !"".equals(riskKind)){
			String[] dictArray = riskKind.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类别
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.riskKindDict);	//1代表风险分类
				dictRelation = new DictEntryRelation();	
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		if(null!=relePlate && !"".equals(relePlate)){
			String[] dictArray = relePlate.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存涉及版块
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.relePlateDict);	//2代表涉及板块
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加风险类型
		if(null!= riskType && !"".equals(riskType)){
			String[] dictArray = riskType.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类型
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.riskTypeDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加内部动因和外部动因
		if(null!= innerReason && !"".equals(innerReason)){
			String[] dictArray = innerReason.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存内部动因
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.innerReasonDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		if(null!= outterReason && !"".equals(outterReason)){
			String[] dictArray = outterReason.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存外部动因
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.outterReasonDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加风险动因和风险影响
		RiskRelaRisk riskRelaRisk = new RiskRelaRisk();
		if(null!=riskReason && !"".equals(riskReason)){
			JSONArray riskReasonArr = JSONArray.fromObject(riskReason);
			for(int i=0;i<riskReasonArr.size();i++){
				object = (JSONObject)riskReasonArr.get(i);
				//保存风险动因
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("R");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
		if(null!=riskInfluence && !"".equals(riskInfluence)){
			JSONArray riskInfluenceArr = JSONArray.fromObject(riskInfluence);
			for(int i=0;i<riskInfluenceArr.size();i++){
				object = (JSONObject)riskInfluenceArr.get(i);
				//保存风险影响
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("I");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
		//风险上级分类
		if(StringUtils.isNotBlank(riskStructure) && !"[]".equals(riskStructure)){
			JSONArray riskStructureArr = JSONArray.fromObject(riskStructure);
			for(int i=0;i<riskStructureArr.size();i++){
				object = (JSONObject)riskStructureArr.get(i);
				//保存风险影响
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("RBS");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
		
		//添加风险价值链
		if(null!= valueChains && !"".equals(valueChains)){
			String[] dictArray = valueChains.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险价值链
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.valueChainsDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加影响期间
		if(null!= impactTime && !"".equals(impactTime)){
			String[] dictArray = impactTime.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存影响期间
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.impactTimeDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加应对策略
		if(null!= responseStrategy && !"".equals(responseStrategy)){
			String[] dictArray = responseStrategy.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存应对策略
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.responseStrategyDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}*/
		
		//添加评价信息
		JSONObject objs = JSONObject.fromObject(assessValue);
		int s=objs.size();
		if(StringUtils.isNotBlank(assessValue)){
			JSONObject obj = JSONObject.fromObject(assessValue);
			try {
				if(obj.size() > 1){
					this.riskAssessMakeSave(obj.getString("saveinfo").toString(), risk.getId(), obj.getString("templateid"));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		
		//更新上级风险，修改叶子节点
		if(risk.getIsRiskClass().equalsIgnoreCase("rbs")){	//添加风险事件不修改叶子节点状态
			Risk parent = risk.getParent();
			if(null!=parent){
				parent.setIsLeaf(false);
				mergeRisk(parent);
			}
		}
	}
	/**
	 * 添加风险，表单项是最全的
	 * 并且更新上级风险叶子状态
	 * @param riskStructure 风险上级分类，可以多个
	 * @param assessValue   评价打的分值信息
	 */
	@Transactional
	public void saveRiskForSecurity(Risk risk,String respDeptName,String relaDeptName,
			String respPositionName, String relaPositionName, String riskKind,
			String relePlate, String riskKpiName, String influKpiName,
			String controlProcessureName, String influProcessureName,
			String innerReason, String outterReason,
			String riskReason, String riskInfluence,
			String valueChains,String riskType,String templateId,
			String impactTime,String responseStrategy,String riskStructure,String assessValue){
		
		//创建时间和最后修改时间,创建人
		risk.setCreateTime(new Date());
		risk.setLastModifyTime(new Date());
		risk.setCreateBy(UserContext.getUser().getEmpid());
		risk.setCreateOrg(sysOrgDAO.get(UserContext.getUser().getMajorDeptId()));
		
		o_riskDAO.merge(risk);
		
		//添加责任部门/人和相关部门/人
		JSONObject object = new JSONObject();
		RiskOrg riskOrg = null;
		if(null!=respDeptName && !"".equals(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				object = (JSONObject)respDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("M");
				//保存责任部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存责任人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				
				o_riskOrgBO.saveRiskOrg(riskOrg);
				
				
			}
		}
		if(null!=relaDeptName && !"".equals(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				object = (JSONObject)relaDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("A");
				//保存相关部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存相关人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		//添加评价信息

		if(StringUtils.isNotBlank(assessValue)){
				try {
					this.riskAssessMakeSaveForSf2(assessValue, risk.getId());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		
		//更新上级风险，修改叶子节点
		if(risk.getIsRiskClass().equalsIgnoreCase("rbs")){	//添加风险事件不修改叶子节点状态
			Risk parent = risk.getParent();
			if(null!=parent){
				parent.setIsLeaf(false);
				mergeRisk(parent);
			}
		}
	}
	/**
	 * 最简单的风险修改
	 * @param risk
	 */
	@Transactional
	public void mergeRisk(Risk risk){
		o_riskDAO.merge(risk);
	}
	
	/**
	 * 修改风险的指标信息和风险的计算信息
	 * @param kpiParam 指标，权重的列表字符串
	 * @param riskId
	 */
	@Transactional
    public void mergeRiskAndKpi(String kpiParam, Risk risk) {
		o_riskDAO.merge(risk);
        
		/**
		 * 修改风险指标
		 */
        //删除风险关联的指标
        Set<KpiRelaRisk> kpiRelaRisks = risk.getKpiRelaRisks();
        for (KpiRelaRisk tmp : kpiRelaRisks) {
        	if("RM".equals(tmp.getType())){
        		o_kpiRelaRiskDAO.delete(tmp);
        	}
        }

        String[] params = StringUtils.split(kpiParam, ";");
        if(null!=params){
            String kpiId = "";
            String[] kpiStr = null;
            Kpi kpi = null;// 指标临时对象
            KpiRelaRisk kr = null;// 风险与指标临时关联对象
        	for (String para : params) {
            	String kpiWeight = "";
                if (StringUtils.isNotBlank(para)) {
                    kpiStr = StringUtils.split(para, ",");
                    kpiId = kpiStr[0];
                    if(null!=kpiStr&&kpiStr.length>1){
                    	kpiWeight = kpiStr[1];
                    }
                    /*
                     * 创建风险和指标关联对象
                     */
                    kr = new KpiRelaRisk();
                    kr.setId(Identities.uuid());
                    kr.setRisk(risk);
                    kpi = new Kpi();
                    kpi.setId(kpiId);
                    kr.setKpi(kpi);
                    kr.setType("RM");//风险指标
                    
                    if(StringUtils.isNotBlank(kpiWeight)){
                    	kr.setWeight(Double.parseDouble(kpiWeight));
                    }
                    o_kpiRelaRiskDAO.merge(kr);

                }
            }
        }
    }
	
	/**
     * 修改风险定义
     * 
     * @param risk
     */
    @Transactional
    public void mergeRiskDefine(Risk risk, String respDeptName, String relaDeptName) {

        // 删除责任部门/人和相关部门/人
        Set<RiskOrg> orgs = risk.getRiskOrgs();
        for (RiskOrg org : orgs) {
            if (org.getType().equals("M") || org.getType().equals("A")) {
                o_riskOrgBO.removeRiskOrgById(org);
            }
        }
        // 最后修改时间
        risk.setLastModifyTime(new Date());
        o_riskDAO.merge(risk);

        // 添加责任部门/人和相关部门/人
        JSONObject object = new JSONObject();
        RiskOrg riskOrg = new RiskOrg();
        if (null != respDeptName && !"".equals(respDeptName)) {
            JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
            for (int i = 0; i < respDeptArray.size(); i++) {
                object = (JSONObject) respDeptArray.get(i);
                riskOrg = new RiskOrg();
                riskOrg.setId(UUID.randomUUID().toString());
                riskOrg.setRisk(risk);
                riskOrg.setType("M");
                // 保存责任部门
                String deptidStr = object.get("deptid").toString();
                if (!deptidStr.equals("") && !deptidStr.equals("null")) { // 空保存出错，查询的时候报错
                    SysOrganization org = new SysOrganization();
                    org.setId(deptidStr);
                    riskOrg.setSysOrganization(org);
                }
                // 保存责任人
                String empidStr = object.get("empid").toString();
                if (!empidStr.equals("") && !empidStr.equals("null")) {
                    SysEmployee emp = new SysEmployee();
                    emp.setId(empidStr);
                    riskOrg.setEmp(emp);
                }

                o_riskOrgBO.saveRiskOrg(riskOrg);
            }
        }
        if (null != relaDeptName && !"".equals(relaDeptName)) {
            JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
            for (int i = 0; i < relaDeptArray.size(); i++) {
                object = (JSONObject) relaDeptArray.get(i);
                riskOrg = new RiskOrg();
                riskOrg.setId(UUID.randomUUID().toString());
                riskOrg.setRisk(risk);
                riskOrg.setType("A");
                // 保存相关部门
                String deptidStr = object.get("deptid").toString();
                if (!deptidStr.equals("") && !deptidStr.equals("null")) { // 空保存出错，查询的时候报错
                    SysOrganization org = new SysOrganization();
                    org.setId(deptidStr);
                    riskOrg.setSysOrganization(org);
                }
                // 保存相关人
                String empidStr = object.get("empid").toString();
                if (!empidStr.equals("") && !empidStr.equals("null")) {
                    SysEmployee emp = new SysEmployee();
                    emp.setId(empidStr);
                    riskOrg.setEmp(emp);
                }
                o_riskOrgBO.saveRiskOrg(riskOrg);
            }
        }
    }
    
	@Autowired
	private RiskScoreObjectDAO o_riskScoreObjDAO;
	/**
     * 修改风险关联
     * 
     * @param risk
     */
    @Transactional
    public void mergeRiskRelate(Risk risk, String respDeptName, String relaDeptName, String riskKpiName, String controlProcessureName, String riskReason,
            String riskInfluence, String influKpiName, String influProcessureName) {

        // 删除责任部门/人和相关部门/人
        Set<RiskOrg> orgs = risk.getRiskOrgs();
        for (RiskOrg org : orgs) {
            if (org.getType().equals("M") || org.getType().equals("A")) {
                o_riskOrgBO.removeRiskOrgById(org);
            }
        }
        // 删除风险指标和影响指标
        Set<KpiRelaRisk> kpis = risk.getKpiRelaRisks();
        for (KpiRelaRisk kpi : kpis) {
            if (kpi.getType().equals("RM") || kpi.getType().equals("I")) {
                o_kpiRelaRiskBO.removeKpiRelaRiskById(kpi.getId());
            }
        }
        // 删除控制流程和影响流程
        Set<ProcessRelaRisk> processes = risk.getRiskProcessures();
        for (ProcessRelaRisk process : processes) {
            if (process.getType().equals("C") || process.getType().equals("I")) {
                o_processRelaRiskBO.removeProcessRelaRiskById(process.getId());
            }
        }

        // 删除风险动因和风险影响
        Set<RiskRelaRisk> riskRelaRisks = risk.getrRisk();
        for (RiskRelaRisk riskrelarisk : riskRelaRisks) {
            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
        }
        Set<RiskRelaRisk> riskRelaRisks2 = risk.getiRisk();
        for (RiskRelaRisk riskrelarisk : riskRelaRisks2) {
            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
        }
        // 最后修改时间
        risk.setLastModifyTime(new Date());
        o_riskDAO.merge(risk);
        
        // 添加责任部门/人和相关部门/人
        JSONObject object = new JSONObject();
        RiskOrg riskOrg = new RiskOrg();
        if (null != respDeptName && !"".equals(respDeptName)) {
            JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
            for (int i = 0; i < respDeptArray.size(); i++) {
                object = (JSONObject) respDeptArray.get(i);
                riskOrg = new RiskOrg();
                riskOrg.setId(UUID.randomUUID().toString());
                riskOrg.setRisk(risk);
                riskOrg.setType("M");
                // 保存责任部门
                String deptidStr = "";
                if(object.get("deptid") != null) {
                	deptidStr = object.get("deptid").toString();
                }
                 
                if (!deptidStr.equals("") && !deptidStr.equals("null")) { // 空保存出错，查询的时候报错
                    SysOrganization org = new SysOrganization();
                    org.setId(deptidStr);
                    riskOrg.setSysOrganization(org);
                }
                // 保存责任人
                String empidStr = "";
                if(object.get("empid") != null){
                	empidStr = object.get("empid").toString();
                }
                if (!empidStr.equals("") && !empidStr.equals("null")) {
                    SysEmployee emp = new SysEmployee();
                    emp.setId(empidStr);
                    riskOrg.setEmp(emp);
                }

                o_riskOrgBO.saveRiskOrg(riskOrg);
            }
        }
        if (null != relaDeptName && !"".equals(relaDeptName)) {
            JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
            for (int i = 0; i < relaDeptArray.size(); i++) {
                object = (JSONObject) relaDeptArray.get(i);
                riskOrg = new RiskOrg();
                riskOrg.setId(UUID.randomUUID().toString());
                riskOrg.setRisk(risk);
                riskOrg.setType("A");
                // 保存相关部门
                String deptidStr = object.get("deptid").toString();
                if (!deptidStr.equals("") && !deptidStr.equals("null")) { // 空保存出错，查询的时候报错
                    SysOrganization org = new SysOrganization();
                    org.setId(deptidStr);
                    riskOrg.setSysOrganization(org);
                }
                // 保存相关人
                String empidStr = object.get("empid").toString();
                if (!empidStr.equals("") && !empidStr.equals("null")) {
                    SysEmployee emp = new SysEmployee();
                    emp.setId(empidStr);
                    riskOrg.setEmp(emp);
                }
                o_riskOrgBO.saveRiskOrg(riskOrg);
            }
        }
        // 添加风险指标和影响指标
        KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
        if (null != riskKpiName && !"".equals(riskKpiName)) {
            String[] riskKpiArray = riskKpiName.split(",");
            for (int i = 0; i < riskKpiArray.length; i++) {
                String id = riskKpiArray[i];
                // 保存风险指标
                Kpi kpi = new Kpi();
                kpi.setId(id);
                kpiRelaRisk = new KpiRelaRisk();
                kpiRelaRisk.setId(UUID.randomUUID().toString());
                kpiRelaRisk.setRisk(risk);
                kpiRelaRisk.setType("RM");
                kpiRelaRisk.setKpi(kpi);
                o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
            }
        }
        if (null != influKpiName && !"".equals(influKpiName)) {
            String[] influKpiArray = influKpiName.split(",");
            for (int i = 0; i < influKpiArray.length; i++) {
                String id = influKpiArray[i];
                // 保存影响指标
                Kpi kpi = new Kpi();
                kpi.setId(id);
                kpiRelaRisk = new KpiRelaRisk();
                kpiRelaRisk.setId(UUID.randomUUID().toString());
                kpiRelaRisk.setRisk(risk);
                kpiRelaRisk.setType("I");
                kpiRelaRisk.setKpi(kpi);
                o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
            }
        }
        // 添加控制流程和影响流程
        ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
        if (null != controlProcessureName && !"".equals(controlProcessureName)) {
            String[] processArray = controlProcessureName.split(",");
            for (int i = 0; i < processArray.length; i++) {
                String id = processArray[i];
                // 保存控制流程
                Process process = new Process();
                process.setId(id);
                processRelaRisk = new ProcessRelaRisk();
                processRelaRisk.setId(UUID.randomUUID().toString());
                processRelaRisk.setRisk(risk);
                processRelaRisk.setType("C");
                processRelaRisk.setProcess(process);
                o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
            }
        }
        if (null != influProcessureName && !"".equals(influProcessureName)) {
            String[] processArray = influProcessureName.split(",");
            for (int i = 0; i < processArray.length; i++) {
                String id = processArray[i];
                // 保存影响流程
                Process process = new Process();
                process.setId(id);
                processRelaRisk = new ProcessRelaRisk();
                processRelaRisk.setId(UUID.randomUUID().toString());
                processRelaRisk.setRisk(risk);
                processRelaRisk.setType("I");
                processRelaRisk.setProcess(process);
                o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
            }
        }
        // 添加风险动因和风险影响
        RiskRelaRisk riskRelaRisk = new RiskRelaRisk();
        if (null != riskReason && !"".equals(riskReason)) {
            JSONArray riskReasonArr = JSONArray.fromObject(riskReason);
            for (int i = 0; i < riskReasonArr.size(); i++) {
                object = (JSONObject) riskReasonArr.get(i);
                // 保存风险动因
                Risk relaRisk = new Risk();
                relaRisk.setId(object.getString("id"));
                riskRelaRisk = new RiskRelaRisk();
                riskRelaRisk.setId(UUID.randomUUID().toString());
                riskRelaRisk.setRisk(risk);
                riskRelaRisk.setType("R");
                riskRelaRisk.setRelaRisk(relaRisk);
                o_riskRelaRiskDAO.merge(riskRelaRisk);
            }
        }
        if (null != riskInfluence && !"".equals(riskInfluence)) {
            JSONArray riskInfluenceArr = JSONArray.fromObject(riskInfluence);
            for (int i = 0; i < riskInfluenceArr.size(); i++) {
                object = (JSONObject) riskInfluenceArr.get(i);
                // 保存风险影响
                Risk relaRisk = new Risk();
                relaRisk.setId(object.getString("id"));
                riskRelaRisk = new RiskRelaRisk();
                riskRelaRisk.setId(UUID.randomUUID().toString());
                riskRelaRisk.setRisk(risk);
                riskRelaRisk.setType("I");
                riskRelaRisk.setRelaRisk(relaRisk);
                o_riskRelaRiskDAO.merge(riskRelaRisk);
            }
        }
    }
    
    @Autowired
    private ScoreObjectBO o_scoreObjectBO;
    
    @Autowired
    private LastLeaderBO o_lastLeaderBO;
    
    @Autowired
    private RiskOrgTempBO riskOrgTempBO;

    
    /**
     * @author songjia
     * @param scoreObject
     * @desc    保存整理结果中的风险事项
     */
    @Transactional
    public void mergeScoreObject(RiskScoreObject scoreObject,List<RiskOrgRelationTemp> majorOrgList,List<RiskOrgRelationTemp> relateOrgList,String responseText){
    	String scoreObjectId = scoreObject.getId();
    	//保存打分对象表
    	o_scoreObjectBO.updateScoreObject(scoreObject);
    	//删除关联关系重新保存关联
    	riskOrgTempBO.deleteRiskOrgRelationTempByObjectId(scoreObjectId);
    	riskOrgTempBO.saveRiskOrgRelationBatch(majorOrgList);
    	riskOrgTempBO.saveRiskOrgRelationBatch(relateOrgList);
    	//保存应对措施
    	Long count = o_lastLeaderBO.getCountFromLastResponseByScoreObjectId(scoreObjectId);
    	if(count == 0){
    		o_lastLeaderBO.saveLastLeader(scoreObject.getRiskId(), scoreObject.getId(), responseText);
    	}else{
    		o_lastLeaderBO.updateResponseTextByObjectId(scoreObject.getId(), scoreObject.getRiskId(), responseText);
    	}
    }
    
    @Transactional
    public void mergeRiskRelate(Risk risk,List<RiskOrgTemp>zhuzeOrgList,List<RiskOrgTemp> relaOrgList,String planId,String objectId,String riskId,String schm){
    	RiskScoreObject scoreObj = o_scoreObjectBO.get(objectId);
    	if(scoreObj != null){
//			scoreObj.setRisk(risk);
			/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
			scoreObj.setCompany(risk.getCompany());
			scoreObj.setTemplate(risk.getTemplate());
			scoreObj.setCode(risk.getCode());
			scoreObj.setName(risk.getName());
			scoreObj.setDesc(risk.getDesc());
			scoreObj.setParent(risk.getParent());
			scoreObj.setParentName(risk.getParentName());
			scoreObj.setLevel(risk.getLevel());
			scoreObj.setIdSeq(risk.getIdSeq());
			scoreObj.setDeleteStatus(risk.getDeleteStatus());
			scoreObj.setArchiveStatus(risk.getArchiveStatus());
			scoreObj.setSort(risk.getSort());
			scoreObj.setCreateTime(risk.getCreateTime());
			scoreObj.setCreateBy(risk.getCreateBy());
			scoreObj.setCreateOrg(risk.getCreateOrg());
			scoreObj.setLastModifyTime(risk.getLastModifyTime());
			scoreObj.setResponseStrategys(risk.getResponseStrategys());
			scoreObj.setIsRiskClass(risk.getIsRiskClass());
			scoreObj.setIsLeaf(risk.getIsLeaf());
			scoreObj.setIsInherit(risk.getIsInherit());
			scoreObj.setFormulaDefine(risk.getFormulaDefine());
			scoreObj.setAlarmScenario(risk.getAlarmScenario());
			scoreObj.setGatherFrequence(risk.getGatherFrequence());
			scoreObj.setGatherDayFormulr(risk.getGatherDayFormulr());
			scoreObj.setGatherDayFormulrShow(risk.getGatherDayFormulrShow());
			scoreObj.setResultCollectInterval(risk.getResultCollectInterval());
			scoreObj.setIsFix(risk.getIsFix());
			scoreObj.setIsUse(risk.getIsUse());
			scoreObj.setIsAnswer(risk.getIsAnswer());
			scoreObj.setCalc(risk.getCalc());
			//scoreObj.setSchm(risk.getSchm());
			scoreObj.setName(risk.getName());
			o_scoreObjectBO.updateScoreObject(scoreObj);
			
			//根据打分对象id删除t_rm_risk_org_temp中数据
			//o_riskScoreBO.deleteRiskOrgTempByObjectId(objectId);
			//插入主责部门
			//o_riskScoreBO.saveRiskOrgTemp(zhuzeOrgList);
			//插入相关部门
			//o_riskScoreBO.saveRiskOrgTemp(relaOrgList);
			
			this.riskOrgTempBO.deleteRiskOrgRelationTempByObjectId(objectId);
			
			
			List<RiskOrgRelationTemp> majorList = new ArrayList<RiskOrgRelationTemp>();
			for(RiskOrgTemp riskOrgTemp :zhuzeOrgList){
				RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
				riskOrgRelationTemp.setId(riskOrgTemp.getId());
				riskOrgRelationTemp.setRisk(risk);
				riskOrgRelationTemp.setRiskScoreObject(scoreObj);
				riskOrgRelationTemp.setSysOrganization(riskOrgTemp.getSysOrganization());
				riskOrgRelationTemp.setType(riskOrgTemp.getType());
				majorList.add(riskOrgRelationTemp);
			}
			
			List<RiskOrgRelationTemp> relaList = new ArrayList<RiskOrgRelationTemp>();
			for(RiskOrgTemp riskOrgTemp :relaOrgList){
				RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
				riskOrgRelationTemp.setId(riskOrgTemp.getId());
				riskOrgRelationTemp.setRisk(risk);
				riskOrgRelationTemp.setRiskScoreObject(scoreObj);
				riskOrgRelationTemp.setSysOrganization(riskOrgTemp.getSysOrganization());
				riskOrgRelationTemp.setType(riskOrgTemp.getType());
				relaList.add(riskOrgRelationTemp);
			}
			this.riskOrgTempBO.saveRiskOrgRelationBatch(majorList);
			this.riskOrgTempBO.saveRiskOrgRelationBatch(relaList);
    	}
    }
    
    
	/**
	 * 修改风险
	 * @param assessValue 评估信息
	 */
	@Transactional
	public void mergeRisk(Risk risk,String respDeptName,String relaDeptName,
			String respPositionName, String relaPositionName, String riskKind,
			String relePlate, String riskKpiName, String influKpiName,
			String controlProcessureName, String influProcessureName,
			String innerReason, String outterReason,
			String riskReason, String riskInfluence,
			String valueChains,String riskType,
			String impactTime,String responseStrategy,String riskStructure,String assessValue){
		
		Set<RiskOrg> orgs = risk.getRiskOrgs();
		for(RiskOrg org : orgs){
			//删除责任部门/人
			if(org.getType().equals("M")){
				o_riskOrgBO.removeRiskOrgById(org);
			}
			//删除相关部门/人
			if(org.getType().equals("A")){
				o_riskOrgBO.removeRiskOrgById(org);
			}
		}
		
		Set<KpiRelaRisk> kpis = risk.getKpiRelaRisks();
		for(KpiRelaRisk kpi : kpis){
			//删除影响指标
			if(kpi.getType().equals("I")){
				o_kpiRelaRiskBO.removeKpiRelaRiskById(kpi.getId());
			}
		}
		
		Set<ProcessRelaRisk> processes = risk.getRiskProcessures();
		for(ProcessRelaRisk process : processes){
			//删除影响流程
			if(process.getType().equals("I")){
				o_processRelaRiskBO.removeProcessRelaRiskById(process.getId());
			}
			//删除控制流程
			if(process.getType().equals("C")){
				o_processRelaRiskBO.removeProcessRelaRiskById(process.getId());
			}
		}
		
		//删除风险动因
        Set<RiskRelaRisk> riskRelaRisks = risk.getrRisk();
        for(RiskRelaRisk riskrelarisk : riskRelaRisks){
            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
        }

        //删除风险影响
        Set<RiskRelaRisk> riskRelaRisks2  = risk.getiRisk();
        for(RiskRelaRisk riskrelarisk : riskRelaRisks2){
            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
        }

        
        //删除风险分类
        Set<RiskRelaRisk> riskRelaRisks3 = risk.getRbsRisk();
        for(RiskRelaRisk riskrelarisk : riskRelaRisks3){
            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
        }
		
		//删除风险类别
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.riskKindDict,risk.getId());
		//删除涉及版块
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.relePlateDict,risk.getId());
		//删除风险类型
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.riskTypeDict,risk.getId());
		//删除内部动因
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.innerReasonDict,risk.getId());
		//删除外部动因
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.outterReasonDict,risk.getId());
		//删除风险价值链
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.valueChainsDict,risk.getId());
		//删除影响期间
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.impactTimeDict,risk.getId());
		//删除应对策略
        o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.responseStrategyDict,risk.getId());
		
		//最后修改时间
		risk.setLastModifyTime(new Date());
		o_riskDAO.merge(risk);
		
		//添加责任部门/人
		JSONObject object = new JSONObject();
		RiskOrg riskOrg = new RiskOrg();
		if(null!=respDeptName && !"".equals(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				object = (JSONObject)respDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("M");
				//保存责任部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存责任人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		//添加相关部门/人
		if(null!=relaDeptName && !"".equals(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				object = (JSONObject)relaDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("A");
				//保存相关部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存相关人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		
		//添加风险指标和影响指标
		KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
		if(null!=riskKpiName && !"".equals(riskKpiName)){
			String[] riskKpiArray = riskKpiName.split(",");
			for(int i=0;i<riskKpiArray.length;i++){
				String id = riskKpiArray[i];
				//保存风险指标
				Kpi kpi = new Kpi();
				kpi.setId(id);
				kpiRelaRisk = new KpiRelaRisk();
				kpiRelaRisk.setId(UUID.randomUUID().toString());
				kpiRelaRisk.setRisk(risk);
				kpiRelaRisk.setType("RM");
				kpiRelaRisk.setKpi(kpi);
				o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
			}
		}
		if(null!=influKpiName && !"".equals(influKpiName)){
			String[] influKpiArray = influKpiName.split(",");
			for(int i=0;i<influKpiArray.length;i++){
				String id = influKpiArray[i];
				//保存影响指标
				Kpi kpi = new Kpi();
				kpi.setId(id);
				kpiRelaRisk = new KpiRelaRisk();
				kpiRelaRisk.setId(UUID.randomUUID().toString());
				kpiRelaRisk.setRisk(risk);
				kpiRelaRisk.setType("I");
				kpiRelaRisk.setKpi(kpi);
				o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
			}
		}
		
		//添加控制流程和影响流程
		ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
		if(null!=controlProcessureName && !"".equals(controlProcessureName)){
			String[] processArray = controlProcessureName.split(",");
			for(int i=0;i<processArray.length;i++){
				String id = processArray[i];
				//保存控制流程
				Process process = new Process();
				process.setId(id);
				processRelaRisk = new ProcessRelaRisk();
				processRelaRisk.setId(UUID.randomUUID().toString());
				processRelaRisk.setRisk(risk);
				processRelaRisk.setType("C");
				processRelaRisk.setProcess(process);
				o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
			}
		}
		if(null!=influProcessureName && !"".equals(influProcessureName)){
			String[] processArray = influProcessureName.split(",");
			for(int i=0;i<processArray.length;i++){
				String id = processArray[i];
				//保存影响流程
				Process process = new Process();
				process.setId(id);
				processRelaRisk = new ProcessRelaRisk();
				processRelaRisk.setId(UUID.randomUUID().toString());
				processRelaRisk.setRisk(risk);
				processRelaRisk.setType("I");
				processRelaRisk.setProcess(process);
				o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
			}
		}
		
		//添加风险类别和涉及版块
		DictEntryRelation dictRelation = new DictEntryRelation();
		if(null!=riskKind && !"".equals(riskKind)){
			String[] dictArray = riskKind.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类别
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.riskKindDict);	//1代表风险分类
				dictRelation = new DictEntryRelation();	
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		if(null!=relePlate && !"".equals(relePlate)){
			String[] dictArray = relePlate.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存涉及版块
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.relePlateDict);	//2代表涉及板块
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加风险类型
		if(null!= riskType && !"".equals(riskType)){
			String[] dictArray = riskType.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类型
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.riskTypeDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加内部动因和外部动因
		if(null!= innerReason && !"".equals(innerReason)){
			String[] dictArray = innerReason.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存内部动因
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.innerReasonDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		if(null!= outterReason && !"".equals(outterReason)){
			String[] dictArray = outterReason.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存外部动因
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.outterReasonDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加风险动因和风险影响
		RiskRelaRisk riskRelaRisk = new RiskRelaRisk();
		if(null!=riskReason && !"".equals(riskReason)){
			JSONArray riskReasonArr = JSONArray.fromObject(riskReason);
			for(int i=0;i<riskReasonArr.size();i++){
				object = (JSONObject)riskReasonArr.get(i);
				//保存风险动因
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("R");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
		if(null!=riskInfluence && !"".equals(riskInfluence)){
			JSONArray riskInfluenceArr = JSONArray.fromObject(riskInfluence);
			for(int i=0;i<riskInfluenceArr.size();i++){
				object = (JSONObject)riskInfluenceArr.get(i);
				//保存风险影响
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("I");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
		
		//风险上级分类
		if(StringUtils.isNotBlank(riskStructure) && !"[]".equals(riskStructure)){
			JSONArray riskStructureArr = JSONArray.fromObject(riskStructure);
			for(int i=0;i<riskStructureArr.size();i++){
				object = (JSONObject)riskStructureArr.get(i);
				//保存风险影响
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("RBS");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
				
		//添加风险价值链
		if(null!= valueChains && !"".equals(valueChains)){
			String[] dictArray = valueChains.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险价值链
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.valueChainsDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加影响期间
		if(null!= impactTime && !"".equals(impactTime)){
			String[] dictArray = impactTime.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存影响期间
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.impactTimeDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加应对策略
		if(null!= responseStrategy && !"".equals(responseStrategy)){
			String[] dictArray = responseStrategy.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存应对策略
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskCmpBO.responseStrategyDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBo.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加评价信息
		if(StringUtils.isNotBlank(assessValue)){
			JSONObject obj = JSONObject.fromObject(assessValue);
			try {
				//评价信息没有改变，不进行修改
				if(obj.get("saveinfo")!=null){
					this.riskAssessMakeSave(obj.getString("saveinfo").toString(), risk.getId(), obj.getString("templateid"));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Transactional
	public void mergeRiskForSecurity(Risk risk,String respDeptName,String relaDeptName,
			String respPositionName, String relaPositionName, String riskKind,
			String relePlate, String riskKpiName, String influKpiName,
			String controlProcessureName, String influProcessureName,
			String innerReason, String outterReason,
			String riskReason, String riskInfluence,
			String valueChains,String riskType,
			String impactTime,String responseStrategy,String riskStructure,String assessValue){
		
		Set<RiskOrg> orgs = risk.getRiskOrgs();
		for(RiskOrg org : orgs){
			//删除责任部门/人
			if(org.getType().equals("M")){
				o_riskOrgBO.removeRiskOrgById(org);
			}
			//删除相关部门/人
			if(org.getType().equals("A")){
				o_riskOrgBO.removeRiskOrgById(org);
			}
		}
		//最后修改时间
		risk.setLastModifyTime(new Date());
		o_riskDAO.merge(risk);
		
		//添加责任部门/人
		JSONObject object = new JSONObject();
		RiskOrg riskOrg = new RiskOrg();
		if(null!=respDeptName && !"".equals(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				object = (JSONObject)respDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("M");
				//保存责任部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存责任人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		//添加相关部门/人
		if(null!=relaDeptName && !"".equals(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				object = (JSONObject)relaDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("A");
				//保存相关部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存相关人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		
		//添加评价信息
		if(StringUtils.isNotBlank(assessValue)){
			//评价信息没有改变，不进行修改
			try {
				this.riskAssessMakeSaveForSf2(assessValue, risk.getId());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除风险
	 * @param risk
	 */
	@Transactional
	public void deleteRisk(String[] ids){
		for (String id : ids) {
			Risk risk = o_riskDAO.get(id);
			//删除责任部门/人和相关部门/人
			Set<RiskOrg> orgs = risk.getRiskOrgs();
			for(RiskOrg org : orgs){
				if(org.getType().equals("M") || org.getType().equals("A")){
					o_riskOrgBO.removeRiskOrgById(org);
				}
			}
			//删除风险指标和影响指标
			Set<KpiRelaRisk> kpis = risk.getKpiRelaRisks();
			for(KpiRelaRisk kpi : kpis){
				if(kpi.getType().equals("RM") || kpi.getType().equals("I")){
					o_kpiRelaRiskBO.removeKpiRelaRiskById(kpi.getId());
				}
			}
			//删除控制流程和影响流程
			Set<ProcessRelaRisk> processes = risk.getRiskProcessures();
			for(ProcessRelaRisk process : processes){
				if(process.getType().equals("C") || process.getType().equals("I")){
					o_processRelaRiskBO.removeProcessRelaRiskById(process.getId());
				}
			}
			//删除风险动因和风险影响
	        Set<RiskRelaRisk> riskRelaRisks = risk.getrRisk();
	        for(RiskRelaRisk riskrelarisk : riskRelaRisks){
	            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
	        }
	        Set<RiskRelaRisk> riskRelaRisks2  = risk.getiRisk();
	        for(RiskRelaRisk riskrelarisk : riskRelaRisks2){
	            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
	        }
	        //删除风险分类
	        Set<RiskRelaRisk> riskRelaRisks3 = risk.getRbsRisk();
	        for(RiskRelaRisk riskrelarisk : riskRelaRisks3){
	            o_riskRelaRiskDAO.delete(riskrelarisk.getId());
	        }
			//删除风险字典数据
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.riskKindDict,risk.getId());
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.relePlateDict,risk.getId());
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.riskTypeDict,risk.getId());
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.innerReasonDict,risk.getId());
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.outterReasonDict,risk.getId());
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.valueChainsDict,risk.getId());
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.impactTimeDict,risk.getId());
			o_dictEntryRelationBo.removeDictEntryRelationByBusinessId(RiskBO.responseStrategyDict,risk.getId());
					

			//删除自身
			risk.setDeleteStatus("0");
			o_riskDAO.merge(risk);
			
			//如果上级没有孩子，修改is_leaf属性
			Risk parent = risk.getParent();
			Criteria c = o_riskDAO.createCriteria();
			if(parent == null){
				c.add(Restrictions.isNull("parent.id"));
			}else{
				c.add(Restrictions.eq("parent.id", parent.getId()));
			}
			c.add(Restrictions.eq("deleteStatus", "1"));
			c.setProjection(Projections.rowCount());
			Long size = (Long)c.uniqueResult();
			if(size==0){	//risk是没删除前缓存的状态
				parent.setIsLeaf(true);
				o_riskDAO.merge(parent);
			}
		}
	}
	
	
	/**根据风险id查询所关联的指标;列包括{指标名称,单位,频率,所属部门,权重}
	 * @param query 指标名称
	 * @param id 风险ID
	 * @return
	 */
	public List<Map<String, Object>> findRiskRelaKpi(String query,String id) {
	   	   List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	   	   Criteria dc = o_kpiRelaRiskDAO.createCriteria();
	       // 添加按照公司过滤
	       dc.createAlias("kpi", "kpi");
	       dc.createAlias("risk", "risk");
	       dc.add(Restrictions.eq("kpi.deleteStatus", true));
	       dc.add(Restrictions.eq("kpi.company.id", UserContext.getUser().getCompanyid()));
	       if(StringUtils.isNotBlank(query)) {
	       	  dc.add(Restrictions.like("kpi.name", query,MatchMode.ANYWHERE));
	       }
	       dc.add(Restrictions.eq("type","RM"));	//只查询风险指标
	       dc.add(Restrictions.eq("risk.id",id));
	       if(dc.list().size() > 0) {
		       	List<KpiRelaRisk> relaList =  dc.list();
		       	for(KpiRelaRisk riskRela : relaList) {
		       		Map<String, Object> map = new HashMap<String,Object>();
		              StringBuffer orgBuf = new StringBuffer();
		               Kpi kpi = riskRela.getKpi();
		               Set<KpiRelaOrgEmp> orgSet =kpi.getKpiRelaOrgEmps();
		               for (KpiRelaOrgEmp kpiRelaOrgEmp : orgSet) {
		                   if (Contents.BELONGDEPARTMENT.equals(kpiRelaOrgEmp.getType())) {
		                       orgBuf.append(kpiRelaOrgEmp.getOrg().getOrgname()).append(",");
		                   }
		               }
		               String orgname = null;
		               if (orgBuf.length() > 0) {
		               	  orgname = orgBuf.toString().substring(0, orgBuf.length() - 1);
		               }
		                
		       		map.put("name", kpi.getName());
		       		map.put("unit", kpi.getUnits().getName());
		       		String frequence = kpi.getGatherFrequence().getName();
		       		if(frequence.equals("0frequecy_month")){
	   	    			frequence = "每月";
	   	    		}else if(frequence.equals("0frequecy_week")){
	   	    			frequence = "每周";
	   	    		}else if(frequence.equals("0frequecy_quarter")){
	   	    			frequence = "每季";
	   	    		}else if(frequence.equals("0frequecy_year")){
	   	    			frequence = "每年";
	   	    		}else if(frequence.equals("0frequecy_halfyear")){
	   	    			frequence = "每半年";
	   	    		}
		       		map.put("frequence", frequence);
		       		map.put("dept", orgname);
		       		map.put("weight", riskRela.getWeight());
		       		map.put("id", riskRela.getKpi().getId());
		       		list.add(map);
		       	}
	       }
	   	 return list;
	   }
	
	/**
	 * 根据ID获得实体
	 * @param id
	 * @return
	 */
	public Risk findRiskById(String id){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("id", id));
		return (Risk)criteria.uniqueResult();
	}
	
	public RiskScoreObject findRiskScoreObjectById(String id){
		return this.o_riskScoreObjectDAO.get(id);
	}
	public Risk getRiskByObjectIdFromObjectTable(String objectId){
		RiskScoreObject object = findRiskScoreObjectById(objectId);
		Risk risk = new Risk();
		risk.setCompany(object.getCompany());
		risk.setTemplate(object.getTemplate());
		risk.setCode(object.getCode());
		risk.setName(object.getName());
		risk.setDesc(object.getDesc());
		risk.setParent(object.getParent());
		risk.setParentName(object.getParentName());
		risk.setLevel(object.getLevel());
		risk.setIdSeq(object.getIdSeq());
		risk.setDeleteStatus(object.getDeleteStatus());
		risk.setArchiveStatus(object.getArchiveStatus());
		risk.setSort(object.getSort());
		risk.setCreateTime(object.getCreateTime());
		risk.setCreateBy(object.getCreateBy());
		risk.setCreateOrg(object.getCreateOrg());
		risk.setLastModifyTime(object.getLastModifyTime());
		risk.setResponseStrategys(object.getResponseStrategys());
		risk.setIsRiskClass(object.getIsRiskClass());
		risk.setIsLeaf(object.getIsLeaf());
		risk.setIsInherit(object.getIsInherit());
		risk.setFormulaDefine(object.getFormulaDefine());
		risk.setAlarmScenario(object.getAlarmScenario());
		risk.setGatherFrequence(object.getGatherFrequence());
		risk.setGatherDayFormulr(object.getGatherDayFormulr());
		risk.setGatherDayFormulrShow(object.getGatherDayFormulrShow());
		risk.setResultCollectInterval(object.getResultCollectInterval());
		risk.setIsFix(object.getIsFix());
		risk.setIsUse(object.getIsUse());
		risk.setIsAnswer(object.getIsAnswer());
		risk.setCalc(object.getCalc());
		risk.setSchm(object.getSchm());
		Set<RiskOrgTemp> riskOrgTempSet = object.getRiskOrgTemps();
		Set<RiskOrg> riskOrgSet = new HashSet<RiskOrg>();
		for(RiskOrgTemp temp :riskOrgTempSet){
			RiskOrg org = new RiskOrg();
			org.setEmp(temp.getEmp());
			org.setId(temp.getId());
			org.setSysOrganization(temp.getSysOrganization());
			org.setType(temp.getType());
			riskOrgSet.add(org);
		}
		risk.setRiskOrgs(riskOrgSet);
		return risk;
	}
	
	
	/**
	 * @param id
	 * @return
	 */
	public RiskScoreObject findRiskObjectById(String id){
		RiskScoreObject riskScoreObject = o_riskScoreObjectDAO.get(id);
		return riskScoreObject;
	}
	/**
	 * 风险树
	 * 1.普通树
	 * 2.显示灯的普通树
	 * 3.复选树，复选树可以配置是否只可以选择叶子节点
	 * 增加显示不启用的过滤
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getRiskTreeRecord(String id, String query, Boolean subCompany, 
			Boolean showLight, Boolean checkable, String checkedIds,Boolean onlyLeaf,Boolean showAllUsed,String schm){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		
		Criteria criteria = o_riskDAO.createCriteria();
		Set<String> idSet = queryObjectBySearchName(query);
		if(StringUtils.isNotBlank(query)&&idSet.size()==0){
			return nodes;
		}

		if("root".equals(id)){
			criteria.add(Restrictions.isNull("parent.id"));
		}else{
			criteria.add(Restrictions.eq("parent.id", id));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		//添加风险库分库查询标识
		/**
    	 * modify by 宋佳
    	 * 当部门级风险分类录入时，只显示主责部门是当前登录人部门的
    	 */
		criteria.add(Restrictions.eq("schm", schm));
		if("dept".equals(schm)){
			SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
			String seq = org.getOrgseq();
			String deptId = seq.split("\\.")[2];//部门编号
			
			criteria.add(Restrictions.eq("createOrg.id", deptId));
		}
//		if(!showAllUsed){
//			criteria.add(Restrictions.eq("isUse", "0yn_y"));	//字典值写死，可能会出现问题
//		}
		criteria.addOrder(Order.asc("sort"));
		//自动查询历史记录,最新历史记录出现多条，外关联报错
//		if(showLight){
//			criteria.createAlias("adjustHistory", "adjustHistory", CriteriaSpecification.LEFT_JOIN, Restrictions.eq("adjustHistory.isLatest", "1"));
//		}
		List<Risk> list = criteria.list();

		List<String> ids = new ArrayList<String>();	//保存显示的节点id数组
		for (Risk risk : list) {
			if(!idSet.contains(risk.getId())){
				continue;
			}
			ids.add(risk.getId());
			map = new HashMap<String, Object>();
			map.put("id", risk.getId());
			map.put("text", risk.getName());
			map.put("code", risk.getCode());
			map.put("name", risk.getName());
			//展示出不同的灯图标
			if(showLight){
				String iconCls = "icon-ibm-symbol-0-sm";	//分为高中低，默认中
				String assessPlanId = null;
				RiskAssessPlan riskAssessPlan = risk.getRiskAssessPlan();
				if(riskAssessPlan != null){
					 assessPlanId = risk.getRiskAssessPlan().getId();
				}
				TempSyncRiskrbsScore riskScore = tempSyncRiskRbsBO.getRiskScoreByRiskIdAndAssessPlanId(risk.getId(), assessPlanId);
				if(riskScore != null){
					map.put("iconCls", showRiskTidyBO.getRiskIconByRiskScore(riskScore.getRiskScore(),companyId));
				}else{
					map.put("iconCls", iconCls);
				}
			}
			nodes.add(map);
		}
		
		//添加是否为叶子节点(这块避免单个取节点造成的嵌套查询)
		Map<String,Integer> riskMap = getRiskNumInRisk(ids);
		for(Map node : nodes){
			//设置叶子节点
			Boolean isLeaf = true;
			Integer count = riskMap.get(node.get("id"));
			if(count!=null && count>0){
				isLeaf = false;
			}
			node.put("leaf", isLeaf);
			//设置复选框
			if(checkable) {
				if(onlyLeaf && !isLeaf){	//只有叶子节点才加复选框
					
				}else{
					if(StringUtils.isNotEmpty(checkedIds)){
						for (String choose : StringUtils.split(checkedIds,",")) {
							if(node.get("id").toString().equals(choose)){
								node.put("checked", true);
								break;
							}
							node.put("checked", false);
						}
					}else{
						node.put("checked", false);
					}
				}
			}
		}
		
		return nodes;
	}
	@SuppressWarnings("unchecked")
	protected Set<String> queryObjectBySearchName(String query){
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_riskDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		List<Risk> list = criteria.list();
		
		for (Risk entity : list) {
			if(null==entity.getIdSeq()){
				continue;
			}
			String[] idsTemp = entity.getIdSeq().split("\\.");
			idSet.addAll(Arrays.asList(idsTemp));
		}
		return idSet;
	}
	
	/**
	 * 获取风险分类下风险的个数
	 * 有可能传入的ids有4个，传出的map只有3个key.因为这个id并没有子节点
	 * @author zhengjunxiang
	 * @return
	 */
	private Map<String,Integer> getRiskNumInRisk(List<String> ids){
		Map<String,Integer> riskMap = new HashMap<String,Integer>();
		if(ids.size()==0){	//数组为空
			return riskMap;
		}
		
		List<String> id = new ArrayList<String>();
		for(int i=0;i<ids.size();i++){
			id.add(ids.get(i).toString());
		}
		
		StringBuffer sql = new StringBuffer();
		String companyId = UserContext.getUser().getCompanyid();
		Map<String,Object> mapParams = new HashMap<String,Object>();
		sql.append("SELECT parent_id,count(1)")
		.append(" FROM t_rm_risks r")
		.append(" WHERE r.is_risk_class = 'rbs' and r.delete_estatus = '1' and r.company_id = :companyId and r.parent_id in(:id)")
		.append(" GROUP BY r.parent_id");
		mapParams.put("companyId", companyId);
		mapParams.put("id", id);
		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
		
  		for(Object[] o : list){
  			riskMap.put(o[0].toString(), Integer.parseInt(o[1].toString()));
  		}
  		
  		return riskMap;
	}

	@SuppressWarnings("unchecked")
	public Boolean riskHasChildren(String parentId){
		boolean hasChildren = false;
		
		Criteria criteria = o_riskDAO.createCriteria();
		if("root".equals(parentId)){
			criteria.add(Restrictions.isNull("parent.id"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.setProjection(Projections.rowCount());
		int count = Integer.parseInt(criteria.uniqueResult().toString());
		if(count>0){
			hasChildren = true;
		}
		
		return hasChildren;
	}
	
	/**
	 * 根据兄弟节点的个数
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int getSiblingRiskNum(Risk parent){
		Criteria criteria = o_riskDAO.createCriteria();
		if(parent == null){
			criteria.add(Restrictions.isNull("parent.id"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parent.getId()));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		criteria.setProjection(Projections.rowCount());
		int count = Integer.parseInt(criteria.uniqueResult().toString());
		
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public Page<Risk> findRiskCatalogByRiskId(String id,Page<Risk> page,String sort, String dir,String query){
		String companyId = UserContext.getUser().getCompanyid();
		DetachedCriteria dc = DetachedCriteria.forClass(Risk.class);
		//关联上级风险
		dc.createAlias("parent", "parent",CriteriaSpecification.LEFT_JOIN);
		dc.setFetchMode("parent", FetchMode.SELECT);
		//关联最新记录
		dc.createAlias("adjustHistory", "adjustHistory",CriteriaSpecification.LEFT_JOIN,Restrictions.eq("isLatest", "1"));
		dc.setFetchMode("parent", FetchMode.SELECT);
		dc.add(Restrictions.eq("company.id", companyId));
		dc.add(Restrictions.eq("isRiskClass", "rbs"));
		dc.add(Restrictions.eq("deleteStatus", "1"));
		dc.add(Restrictions.eq("archiveStatus", Contents.RISK_STATUS_ARCHIVED));
		if(id != null && !id.equalsIgnoreCase("root")){
			dc.add(Restrictions.like("idSeq", id, MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
		//排序
		if(sort==null){
			dc.addOrder(Order.asc("parent.id"));
			dc.addOrder(Order.desc("lastModifyTime"));
		}
		
        return o_riskDAO.findPage(dc, page, false);
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventByRiskId(String id,Page<Map> page,String sort, String dir,String query,String schm,String planType,String planId){
	    Map<String,String> mapParams = new HashMap<String,String>();
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT tt.id,tt.code,tt.name,tt.idSeq,tt.lastModifyTime,tt.etrend,"); //最外层，用于排序开始
		sql.append(" case when tt.assessementStatus is null then ");
		//初始化control传入的是Null
        if(dir==null){
        	dir="ASC";
        }
		if(dir.equals("ASC")){
        	sql.append(" 'nothing' "); 
        }else{
        	sql.append(" 'anothing' ");
        }
        sql.append(" else tt.assessementStatus end assessementStatus ");
        sql.append(",tt.parent_id,tt.archive_status FROM ( ");//最外层，用于排序结束
		sql.append("SELECT distinct r.id,r.risk_code as code,r.risk_name as name,r.id_seq as idSeq,r.last_modify_time as lastModifyTime" +
				   ",dict.dict_entry_value etrend,h.assessement_status as assessementStatus,r.parent_id,r.archive_status")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.parent_id in(")
		.append(" 	SELECT tb.id from t_rm_risks tb")
		.append(" 	WHERE tb.company_id=:companyId ")
		.append(" 	and tb.is_risk_class='rbs'")
		.append("   and tb.delete_estatus='1'");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				sql.append(")");
				//点击风险树根节点，查询时增加风险分库标识
				if(null != schm && !"".equals(schm)){
					sql.append(" and r.schm=:schm");
					if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
						SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
						String seq = org.getOrgseq();
						String deptId = seq.split("\\.")[2];//部门编号
						
						sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
					}
					mapParams.put("schm", schm);
				}
			}else{
				sql.append("	and tb.id_seq like :id )");
				mapParams.put("id", "%."+id+".%");
			}
		}
		//过滤
//		if (StringUtils.isNotBlank(query)) {
//			sql.append(" and r.risk_name like :query");
//			mapParams.put("query", "%"+query+"%");
//		}
		sql.append(" and r.company_id=:companyId ");
		sql.append(" and r.is_risk_class='re'");
		sql.append(" and r.delete_estatus='1'");
		sql.append(" and r.archive_status='"+Contents.RISK_STATUS_ARCHIVED+"'");
		
		//联合
		sql.append(" UNION ");
		sql.append("select distinct r.id,r.risk_code as code,r.risk_name as name,r.id_seq as idSeq,r.last_modify_time as lastModifyTime" +
				   ",dict.dict_entry_value etrend,h.assessement_status as assessementStatus,r.parent_id,r.archive_status")
		.append(" FROM t_rm_risks_risks rr")
		.append(" LEFT JOIN t_rm_risks r on rr.risk_id = r.id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE rr.rela_risk_id in(")
		.append(" 	SELECT tb.id from t_rm_risks tb")
		.append(" 	WHERE tb.company_id=:companyId ")
		.append(" 	and tb.is_risk_class='rbs'")
		.append("   and tb.delete_estatus='1'");
		sql.append(" and tb.archive_status='"+Contents.RISK_STATUS_ARCHIVED+"'");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				sql.append(")");
				//点击风险树根节点，查询时增加风险分库标识
				if(null != schm && !"".equals(schm)){
					sql.append(" and r.schm=:schm");
					if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
						SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
						String seq = org.getOrgseq();
						String deptId = seq.split("\\.")[2];//部门编号
						
						sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
					}
					mapParams.put("schm", schm);
				}
			}else{
				sql.append("	and tb.id_seq like :id )");
				mapParams.put("id", "%."+id+".%");
			}
		}
		//过滤
//		if (StringUtils.isNotBlank(query)) {
//			sql.append(" and r.risk_name like :query");
//			mapParams.put("query", "%"+query+"%");
//		}
		sql.append(" and r.company_id='" + companyId + "'");
		sql.append(" and r.is_risk_class='re'");
		sql.append(" and r.delete_estatus='1'");
		//排序
		sql.append(") tt "); //最外层，用于排序
		
		//增加所有条件查询BY金鹏祥
		if (StringUtils.isNotBlank(query)) {
			sql.append(" LEFT JOIN t_rm_risk_org  g on g.risk_id = tt.id ");
			sql.append(" LEFT JOIN t_sys_organization z on z.id = g.ORG_ID ");
			sql.append(" LEFT JOIN t_rm_risks zz on zz.id = tt.parent_id ");
			sql.append(" where z.ORG_NAME like :query or zz.RISK_NAME like :query or tt.name like :query ");
			//过滤掉正在某个计划中没有执行完的风险
			if(planType!=null && !planType.equals("")){
				sql.append(" and tt.id not in (SELECT a.RISK_ID FROM t_rm_risk_score_object a, t_rm_risk_org_temp b, t_rm_risk_assess_plan c WHERE a.ID = b.OBJECT_ID AND a.ASSESS_PLAN_ID = c.ID AND b.`STATUS` = 'running' AND c.PLAN_TYPE = :planType)  ");
				mapParams.put("planType", planType);
			}
			mapParams.put("query", "%"+query+"%");
		}else{
			//过滤掉正在某个计划中没有执行完的风险
			if(planType!=null && !planType.equals("")){
				sql.append(" where tt.id not in (SELECT a.RISK_ID FROM t_rm_risk_score_object a, t_rm_risk_org_temp b, t_rm_risk_assess_plan c WHERE a.ID = b.OBJECT_ID AND a.ASSESS_PLAN_ID = c.ID AND b.`STATUS` = 'running' AND c.PLAN_TYPE = :planType) ");
				mapParams.put("planType", planType);
			}
		}
		
		
		if(sort==null){
			sql.append(" GROUP BY tt.name order by assessementStatus asc");
		}else if(sort.equals("etrend")){
			sql.append(" GROUP BY tt.name order by tt.etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" GROUP BY tt.name order by assessementStatus " + dir);
		}else{}
		mapParams.put("companyId", companyId);
		
		System.out.println("$$$$$$$$$$$$$$$获取风险树风险事件节点sql：="+sql.toString());
		
		
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
		
        //分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
//  		sql.append(" limit "+start+","+limit+"");
//  		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString()).list();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		
  		//封装对象
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			m.put("archiveStatus", o[8]==null?"":o[8].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		
        return page;
	}
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventByRiskId(String id,Page<Map> page,String sort, String dir,String query,String schm){
	    Map<String,String> mapParams = new HashMap<String,String>();
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT tt.id,tt.code,tt.name,tt.idSeq,tt.lastModifyTime,tt.etrend,"); //最外层，用于排序开始
		sql.append(" case when tt.assessementStatus is null then ");
		//初始化control传入的是Null
        if(dir==null){
        	dir="ASC";
        }
		if(dir.equals("ASC")){
        	sql.append(" 'nothing' "); 
        }else{
        	sql.append(" 'anothing' ");
        }
        sql.append(" else tt.assessementStatus end assessementStatus ");
        sql.append(",tt.parent_id,tt.archive_status FROM ( ");//最外层，用于排序结束
		sql.append("SELECT distinct r.id,r.risk_code as code,r.risk_name as name,r.id_seq as idSeq,r.last_modify_time as lastModifyTime" +
				   ",dict.dict_entry_value etrend,h.assessement_status as assessementStatus,r.parent_id,r.archive_status")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.parent_id in(")
		.append(" 	SELECT tb.id from t_rm_risks tb")
		.append(" 	WHERE tb.company_id=:companyId ")
		.append(" 	and tb.is_risk_class='rbs'")
		.append("   and tb.delete_estatus='1'");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				sql.append(")");
				//点击风险树根节点，查询时增加风险分库标识
				if(null != schm && !"".equals(schm)){
					sql.append(" and r.schm=:schm");
					if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
						SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
						String seq = org.getOrgseq();
						String deptId = seq.split("\\.")[2];//部门编号
						
						sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
					}
					mapParams.put("schm", schm);
				}
			}else{
				sql.append("	and tb.id_seq like :id )");
				mapParams.put("id", "%."+id+".%");
			}
		}
		//过滤
//		if (StringUtils.isNotBlank(query)) {
//			sql.append(" and r.risk_name like :query");
//			mapParams.put("query", "%"+query+"%");
//		}
		sql.append(" and r.company_id=:companyId ");
		sql.append(" and r.is_risk_class='re'");
		sql.append(" and r.delete_estatus='1'");
		sql.append(" and r.archive_status='"+Contents.RISK_STATUS_ARCHIVED+"'");
		
		//联合
		sql.append(" UNION ");
		sql.append("select distinct r.id,r.risk_code as code,r.risk_name as name,r.id_seq as idSeq,r.last_modify_time as lastModifyTime" +
				   ",dict.dict_entry_value etrend,h.assessement_status as assessementStatus,r.parent_id,r.archive_status")
		.append(" FROM t_rm_risks_risks rr")
		.append(" LEFT JOIN t_rm_risks r on rr.risk_id = r.id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE rr.rela_risk_id in(")
		.append(" 	SELECT tb.id from t_rm_risks tb")
		.append(" 	WHERE tb.company_id=:companyId ")
		.append(" 	and tb.is_risk_class='rbs'")
		.append("   and tb.delete_estatus='1'");
		sql.append(" and tb.archive_status='"+Contents.RISK_STATUS_ARCHIVED+"'");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				sql.append(")");
				//点击风险树根节点，查询时增加风险分库标识
				if(null != schm && !"".equals(schm)){
					sql.append(" and r.schm=:schm");
					if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
						SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
						String seq = org.getOrgseq();
						String deptId = seq.split("\\.")[2];//部门编号
						
						sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
					}
					mapParams.put("schm", schm);
				}
			}else{
				sql.append("	and tb.id_seq like :id )");
				mapParams.put("id", "%."+id+".%");
			}
		}
		//过滤
//		if (StringUtils.isNotBlank(query)) {
//			sql.append(" and r.risk_name like :query");
//			mapParams.put("query", "%"+query+"%");
//		}
		sql.append(" and r.company_id='" + companyId + "'");
		sql.append(" and r.is_risk_class='re'");
		sql.append(" and r.delete_estatus='1'");
		//排序
		sql.append(") tt "); //最外层，用于排序
		
		//增加所有条件查询BY金鹏祥
		if (StringUtils.isNotBlank(query)) {
			sql.append(" LEFT JOIN t_rm_risk_org  g on g.risk_id = tt.id ");
			sql.append(" LEFT JOIN t_sys_organization z on z.id = g.ORG_ID ");
			sql.append(" LEFT JOIN t_rm_risks zz on zz.id = tt.parent_id ");
			sql.append(" where z.ORG_NAME like :query or zz.RISK_NAME like :query or tt.name like :query ");
			mapParams.put("query", "%"+query+"%");
		}
		if(sort==null){
			sql.append(" GROUP BY tt.name order by assessementStatus asc");
		}else if(sort.equals("etrend")){
			sql.append(" GROUP BY tt.name order by tt.etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" GROUP BY tt.name order by assessementStatus " + dir);
		}else{}
		mapParams.put("companyId", companyId);
		
		System.out.println("$$$$$$$$$$$$$$$获取风险树风险事件节点sql：="+sql.toString());
		
		
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
		
        //分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
//  		sql.append(" limit "+start+","+limit+"");
//  		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString()).list();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		
  		//封装对象
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			m.put("archiveStatus", o[8]==null?"":o[8].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		
        return page;
	}
	
	/**
	 * @author jia.song@pcitc.com
	 * @desc    refactor query riskevents method
	 * @param id
	 * @param page
	 * @param sort
	 * @param dir
	 * @param query
	 * @param schm
	 * @return
	 */
	public Page<Map> findRiskEventByRiskIdForSf2(String id,Page<Map> page,String sort, String dir,String query,String schm){
	    Map<String,String> mapParams = new HashMap<String,String>();
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT risk.id,risk.risk_code,risk.risk_name,risk.id_seq,score.risk_score,risk.parent_id,risk.archive_status,res.edit_idea_content");
		sql.append(" FROM t_rm_risks risk LEFT JOIN temp_sync_riskrbs_score score ON score.risk_id = risk.id and risk.assess_plan_id = score.ASSESS_PLAN_ID ");
		sql.append(" LEFT JOIN t_rm_risk_response res on res.risk_id = risk.id" );
		sql.append(" WHERE risk.is_risk_class = 're' AND risk.delete_estatus = '1'");
		if(id.equalsIgnoreCase("root")){
			//点击风险树根节点，查询时增加风险分库标识
			if(null != schm && !"".equals(schm)){
				sql.append(" AND risk.schm=:schm");
				if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
					SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
					String seq = org.getOrgseq();
					String deptId = seq.split("\\.")[2];//部门编号
					sql.append(" AND risk.CREATE_ORG_ID='"+deptId+"'");
				}
				mapParams.put("schm", schm);
			}
		}else{
			sql.append("	AND risk.id_seq like :id ");
			mapParams.put("id", "%."+id+".%");
		}
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
		
        //分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
//  		sql.append(" limit "+start+","+limit+"");
//  		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString()).list();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		
  		//封装对象
  		List<Map> objList = new ArrayList<Map>();
  		String iconCls = "icon-ibm-symbol-0-sm";	//分为高中低，默认中
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			if(o[4] != null){
  				m.put("assessementStatus", showRiskTidyBO.getRiskIconByRiskScore((Double)o[4],companyId));
  			}else{
  				m.put("assessementStatus", iconCls);
  			}
  			m.put("parentId", o[5]==null?"":o[5].toString());
  			m.put("archiveStatus", o[6]==null?"":o[6].toString());
  			m.put("responseText",o[7]==null?"":o[7].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		
        return page;
	}
	
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventByOrgId(String id,Page<Map> page,String sort, String dir,String query, String schm){
	    Map<String,String> mapParams = new HashMap<String,String>();
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct r.id,r.risk_code,r.risk_name,r.id_seq,r.last_modify_time,dict.dict_entry_value etrend,");
		//风险状态排序
		sql.append("case when h.assessement_status is null then");
		if(dir==null){
        	dir="ASC";
        }
		if(dir.equals("ASC")){
        	sql.append(" 'nothing'"); 
        }else{
        	sql.append(" 'anothing'");
        }
        sql.append(" else h.assessement_status end assessement_status,");
        sql.append("r.parent_id,r.archive_status");
		sql.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_org riskorg on r.id = riskorg.risk_id")
		.append(" LEFT JOIN t_sys_organization org on org.id=riskorg.org_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'");
		sql.append(" and r.archive_status='"+Contents.RISK_STATUS_ARCHIVED+"'");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				//点击风险树根节点，查询时增加风险分库标识
				if(null != schm && !"".equals(schm)){
					sql.append(" and r.schm=:schm");
					if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
						SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
						String seq = org.getOrgseq();
						String deptId = seq.split("\\.")[2];//部门编号
						
						sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
					}
					mapParams.put("schm", schm);
				}
			}else{
				sql.append(" and org.id_seq like :id ");
				mapParams.put("id", "%."+id+".%");
			}
		}
		//增加schm分库标识
		if(null != schm && !"".equals(schm)){
			sql.append(" and r.schm=:schm");
			if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
			}
			mapParams.put("schm", schm);
		}
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like :query ");
			mapParams.put("query", "%"+query+"%");
		}
		
		//排序
		if(sort==null){
			sql.append(" order by assessement_status asc");
		}else if(sort.equals("etrend")){
			sql.append(" order by h.etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" order by assessement_status " + dir);
		}else{}
		mapParams.put("companyId", companyId);
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		
		int size = sqlQuery.list().size();
 
		//分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
//  		sql.append(" limit "+start+","+limit+"");
//  		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString()).list();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			m.put("archiveStatus", o[8]==null?"":o[8].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
		
        return page;
	}
	
	
	
	/**
	 * id 可以是指标id
	 * @param type
	 * @param id
	 * @param page
	 * @param sort
	 * @param dir
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventByStrategyMapId(String id,Page<Map> page,String sort, String dir,String query, String schm){
		//查询目标下有哪些指标
		List<Kpi> kpiList = findKpiBySmId(id);
		List<String> kpiIdStr = new ArrayList<String>();
		for(int i=0;i<kpiList.size();i++){
			kpiIdStr.add(((Kpi)kpiList.get(i)).getId());
		}
		return findRiskEventByKpiIds(kpiIdStr,page,sort,dir,query,schm);
	}
	
	/**
	 * 查询目标下,包含子目标下的所有指标
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Kpi> findKpiBySmId(String id){
		Criteria criteria = o_kpiDAO.createCriteria();
		criteria.createAlias("kpiRelaSm", "kpirelasm");
		criteria.setFetchMode("kpirelasm", FetchMode.SELECT);
		criteria.createAlias("kpirelasm.strategyMap", "sm");
		criteria.setFetchMode("sm", FetchMode.SELECT);
		criteria.add(Restrictions.eq("isKpiCategory", "KPI"));
		criteria.add(Restrictions.eq("deleteStatus", true));
		//过滤掉指标停用状态
		criteria.add(Restrictions.eq("status.id", Contents.DICT_Y));
		if(id.indexOf("root") == -1){	//不是根节点
			criteria.add(Restrictions.like("sm.idSeq", "."+id+".",MatchMode.ANYWHERE));
		}

		return (List<Kpi>)criteria.list();
	}
	
	/**
	 * 
	 * @param type
	 * @param id 指标id,格式：‘1111’ 或者 ‘111’，‘2222’，‘333’
	 * @param page
	 * @param sort
	 * @param dir
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventByKpiIds(List<String> id,Page<Map> page,String sort, String dir,String query, String schm){
  		List<Map> objList = new ArrayList<Map>();
		if(id.size()==0){	//ids数组为空
			page.setResult(objList);
	  		page.setTotalItems(0);
			return page;
		}
		Map<String,Object> mapParams = new HashMap<String,Object>();
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct r.id,r.risk_code,r.risk_name,r.id_seq,r.last_modify_time,dict.dict_entry_value etrend,");
		//风险状态排序
		sql.append("case when h.assessement_status is null then");
		if(dir==null){
        	dir="ASC";
        }
		if(dir.equals("ASC")){
        	sql.append(" 'nothing'"); 
        }else{
        	sql.append(" 'anothing'");
        }
		sql.append(" else h.assessement_status end assessement_status,");
		sql.append("r.parent_id,r.archive_status");
		sql.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_kpi_kpi_rela_risk riskkpi on r.id = riskkpi.risk_id")
		.append(" LEFT JOIN t_kpi_kpi kpi on kpi.id=riskkpi.kpi_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'");
		sql.append(" and r.archive_status=:status ");
		if(id != null){
			if(id.size() == 0){
				if(null != schm && !"".equals(schm)){
					sql.append(" and r.schm=:schm");
					if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
						SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
						String seq = org.getOrgseq();
						String deptId = seq.split("\\.")[2];//部门编号
						
						sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
					}
					mapParams.put("schm", schm);
				}
			}else{
				sql.append(" and kpi.id in( :id )");
				mapParams.put("id", id);
			}
		}
		if(null != schm && !"".equals(schm)){
			sql.append(" and r.schm=:schm");
			if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
			}
			mapParams.put("schm", schm);
		}
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like :query");
			mapParams.put("query", "%"+query+"%");
		}

		//排序
		if(sort==null){
			sql.append(" order by assessement_status asc");
		}else if(sort.equals("etrend")){
			sql.append(" order by h.etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" order by assessement_status " + dir);
		}else{}
		mapParams.put("companyId", companyId);
		mapParams.put("status", Contents.RISK_STATUS_ARCHIVED);
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
 
		//分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
//  		sql.append(" limit "+start+","+limit+"");
//  		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString()).list();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			m.put("archiveStatus", o[8]==null?"":o[8].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
		
        return page;
	}
	
	/**
	 * qbc分页查询出现问题
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventByProcessId(String id,Page<Map> page,String sort, String dir,String query, String schm){
	    Map<String,String> mapParams = new HashMap<String,String>();
		String companyId = UserContext.getUser().getCompanyid();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct r.id,r.risk_code,r.risk_name,r.id_seq,r.last_modify_time,dict.dict_entry_value etrend,");
		//风险状态排序
		sql.append("case when h.assessement_status is null then");
		if(dir==null){
        	dir="ASC";
        }
		if(dir.equals("ASC")){
        	sql.append(" 'nothing'"); 
        }else{
        	sql.append(" 'anothing'");
        }
        sql.append(" else h.assessement_status end assessement_status,");
        sql.append("r.parent_id,r.archive_status");
		sql.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_processure_risk_processure riskpro on r.id = riskpro.risk_id")
		.append(" LEFT JOIN t_ic_processure pro on pro.id=riskpro.processure_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'");
		sql.append(" and r.archive_status=:status");
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				if(null != schm && !"".equals(schm)){
					sql.append(" and r.schm=:schm");
					if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
						SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
						String seq = org.getOrgseq();
						String deptId = seq.split("\\.")[2];//部门编号
						
						sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
					}
					mapParams.put("schm", schm);
				}
			}else{
				sql.append(" and pro.id_seq like :id ");
				mapParams.put("id", "%."+id+".%");
			}
		}
		if(null != schm && !"".equals(schm)){
			sql.append(" and r.schm=:schm");
			if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
			}
			mapParams.put("schm", schm);
		}
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like :query ");
			mapParams.put("query", "%"+query+"%");
		}
		
		//排序
		if(sort==null){
			sql.append(" order by assessement_status asc");
		}else if(sort.equals("etrend")){
			sql.append(" order by h.etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" order by assessement_status " + dir);
		}else{}
		mapParams.put("companyId", companyId);
		mapParams.put("status", Contents.RISK_STATUS_ARCHIVED);
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();

		//分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
//  		sql.append(" limit "+start+","+limit+"");
//  		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString()).list();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			m.put("archiveStatus", o[8]==null?"":o[8].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
		
        return page;
	}
	
	/**
	 * 根据Id的Set集合查询列表 zhengjunxiang
	 */
	public List<HashMap<String, Object>> findRiskByIdSet(Set<String> idSet) {
		Criteria criteria = o_riskDAO.createCriteria();
		if(null != idSet && idSet.size()>0){
			criteria.add(Restrictions.in("id", idSet.toArray()));
		}else{
			criteria.add(Restrictions.isNotNull("id"));
		}
		criteria.addOrder(Order.asc("level"));
		return MapListUtil.toMapList(criteria.list());
	}

	/**
	 * 查找风险的历史记录状态和趋势
	 * Map,1个风险对应1个最新的历史记录
	 * 事件列表查询没有使用，直接通过sql将状态和趋势查出来
	 * @param idList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findLatestRiskAdjustHistoryByRiskIds(String[] ids){
		Map<String,Object> historyMap = new HashMap<String,Object>();
		if(ids.length==0){	//ids数组为空
			return historyMap;
		}
		
		List<String> idStr = new ArrayList<String>();
		for(int i=0;i<ids.length;i++){
			idStr.add(ids[i]);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select h.id,h.assessement_status,dict.dict_entry_value,h.risk_id")
		.append(" from t_rm_risk_adjust_history h,t_sys_dict_entry dict")
		.append(" where h.etrend = dict.id")
		.append(" and h.is_latest='1'")
		.append(" and h.risk_id in (:idStr)");
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameterList("idStr", idStr);
		List<Object[]> list = sqlQuery.list();
			
		RiskAdjustHistory h = null;
  		for(Object[] o : list){
  			h = new RiskAdjustHistory();
  			h.setId(o[0].toString());
  			h.setAssessementStatus(o[1].toString());
  			h.setEtrend(o[2].toString());
  			String riskId = o[3].toString();
  			historyMap.put(riskId, h);
  		}
  		
		return historyMap;
	}
	/**
	 * 查找风险的历史记录
	 * Map:key:respDeptName,relaDeptName   他们的value是一个map，机构如下 key-riskId  value-list<sysorganization>
	 * 1个风险对应多个责任部门和相关部门
	 * @param idList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findOrgNameByRiskIds(String[] ids){
		Map<String,Object> orgMap = new HashMap<String,Object>();
		Map<String,Object> respDeptMap = new HashMap<String,Object>();
		Map<String,Object> relaDeptMap = new HashMap<String,Object>();
		if(ids.length==0){	//ids数组为空
			orgMap.put("respDeptMap", respDeptMap);
			orgMap.put("relaDeptMap", relaDeptMap);
			return orgMap;
		}
		
		Criteria criteria = o_riskOrgDAO.createCriteria();
		//自动关联风险
		criteria.createAlias("risk", "risk");
		criteria.setFetchMode("risk", FetchMode.SELECT);
		//自动关联部门
		criteria.createAlias("sysOrganization", "sysOrganization");
		criteria.setFetchMode("sysOrganization", FetchMode.SELECT);
		criteria.add(Restrictions.in("risk.id",ids));
		List<RiskOrg> lists = (List<RiskOrg>)criteria.list();
		for(RiskOrg riskOrg : lists){
			String riskId = riskOrg.getRisk().getId();
			if(riskOrg.getType().equalsIgnoreCase("M")){
				if(respDeptMap.containsKey(riskId)){
					String orgName = respDeptMap.get(riskId).toString() + "," + riskOrg.getSysOrganization().getOrgname();
					respDeptMap.put(riskId, orgName);
				}else{
					respDeptMap.put(riskId, riskOrg.getSysOrganization().getOrgname());
				}
			}
			if(riskOrg.getType().equalsIgnoreCase("A")){
				if(relaDeptMap.containsKey(riskId)){
					String orgName = relaDeptMap.get(riskId).toString() + "," + riskOrg.getSysOrganization().getOrgname();
					relaDeptMap.put(riskId, orgName);
				}else{
					relaDeptMap.put(riskId, riskOrg.getSysOrganization().getOrgname());
				}
			}
		}
		orgMap.put("respDeptMap", respDeptMap);
		orgMap.put("relaDeptMap", relaDeptMap);
		
		return orgMap;
	}
	
	/**
	 * 根据风险列表获取风险的键值对
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findParentRiskNameByRiskIds(String[] ids){
		Map<String,Object> riskMap = new HashMap<String,Object>();
		if(ids.length==0){
			return riskMap;
		}
		
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.in("id",ids));
		List<Risk> lists = (List<Risk>)criteria.list();
		
		for(Risk risk : lists){
			riskMap.put(risk.getId(), risk);
		}
		
		return riskMap;
	}
	
	/**
	 * 生成风险编码
	 * 规则：每个级别节点有4位，中间用-隔开。 
	 * 一级节点RISK0001,RISK0002依此类推； 二级节点RISK0001-0001，RISK0001-0002
	 * 每级节点最大数为9999
	 * @param parentId 上级节点id
	 * @return
	 */
	public String getGenerateRiskCode(String parentId) {
		StringBuffer code = new StringBuffer();
		long count = 0;
		if (StringUtils.isBlank(parentId)) {
            Criteria criteria = o_riskDAO.createCriteria();
            criteria.add(Restrictions.isNull("parent"));
            criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
            count = (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();
            Long index = count + 1;
            if(index>=0 && index<=9){
            	code.append("RISK").append("000").append(count + 1);
            }else if(index>=10 && index<=99){
            	code.append("RISK").append("00").append(count + 1);
            }else if(index>=100 && index<=999){
            	code.append("RISK").append("0").append(count + 1);
            }else{
            	code.append("RISK").append(count + 1);
            }
        }else{
        	Risk parentRisk = o_riskDAO.get(parentId);
        	String parentCode = parentRisk.getCode();
        	Criteria criteria = o_riskDAO.createCriteria();
        	criteria.add(Restrictions.eq("parent.id", parentId));
            criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
            count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
            Long index = count + 1;
            if(index>=0 && index<=9){
            	code.append(parentCode+"-").append("000").append(count + 1);
            }else if(index>=10 && index<=99){
            	code.append(parentCode+"-").append("00").append(count + 1);
            }else if(index>=100 && index<=999){
            	code.append(parentCode+"-").append("0").append(count + 1);
            }else{
            	code.append(parentCode+"-").append(count + 1);
            }
        }
		return code.toString();
	}
	
	/**
	 * 风险分析查询
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2013-9-5
	 * @since Ver 1.1
	 */
	@SuppressWarnings("unchecked")
    public List<Object[]> findRiskAnalysis(String query,String sort, String dir,String value,String type){
        Map<String,String> mapParams = new HashMap<String,String>();
        String companyId = UserContext.getUser().getCompanyid();
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT DISTINCT ");
        sql.append(" kpi.ID kid,kpi.KPI_NAME kname,r.ID rid,r.RISK_NAME rname,h.assessement_status assessement,r.ARCHIVE_STATUS archiveStatus ");
        sql.append(" FROM T_KPI_KPI kpi ");
        sql.append(" left JOIN t_kpi_kpi_rela_risk  kr ON kpi.ID = kr.KPI_ID AND kr.ETYPE = 'I' ");
        sql.append(" left JOIN t_rm_risks  r ON kr.RISK_ID = r.ID AND ");
        sql.append(" r.DELETE_ESTATUS = '1' AND ");
        sql.append(" r.IS_RISK_CLASS = 're' AND ");
        sql.append(" kpi.DELETE_STATUS = '1' AND ");
        sql.append(" r.COMPANY_ID = :companyId  ");
        sql.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'");
        
        if("sm".equals(type)){
            sql.append(" left JOIN t_kpi_sm_rela_kpi  sk ON kpi.ID = sk.KPI_ID ");
            sql.append(" WHERE ");
            sql.append(" sk.STRATEGY_MAP_ID = :value ");
            mapParams.put("value", value);
        }else if("sc".equals(type)){
            sql.append(" LEFT JOIN T_KPI_KPI_RELA_CATEGORY kc ON kpi.ID = kc.KPI_ID ");
            sql.append(" WHERE ");
            sql.append(" kc.CATEGORY_ID = :value ");
            mapParams.put("value", value);
        }else if("kpi".equals(type)){
            sql.append(" WHERE ");
            sql.append(" kpi.ID = :value ");
            mapParams.put("value", value);
        }else{
            sql.append(" WHERE 1=1 ");
        }
        sql.append(" AND r.DELETE_ESTATUS = '1' AND ");
        sql.append(" r.IS_RISK_CLASS = 're' AND ");
        sql.append(" r.COMPANY_ID = :companyId  ");
        if(StringUtils.isNotBlank(query)){
            sql.append(" and r.RISK_NAME like :query  ");
            mapParams.put("query", "%"+query+"%");
        }
        if(sort==null){
            sql.append(" order by kpi.ID asc");
        }else{
            sql.append(" order by " + sort + " " + dir);
        }
        mapParams.put("companyId", companyId);
        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
        
        return list;
    }
    
    /**
     * 根据id查询所有的目标
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-9-5
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllKpi(String value,String type){
        Map<String,String> mapParams = new HashMap<String,String>();
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT DISTINCT ");
        sql.append(" kpi.ID AS kid,kpi.KPI_NAME AS kname ");
        sql.append(" FROM T_KPI_KPI AS kpi ");
        
        if("sm".equals(type)){
            sql.append(" left JOIN t_kpi_sm_rela_kpi AS sk ON kpi.ID = sk.KPI_ID ");
            sql.append(" WHERE ");
            sql.append(" sk.STRATEGY_MAP_ID = :value ");
            mapParams.put("value", value);
        }else if("sc".equals(type)){
            sql.append(" LEFT JOIN T_KPI_KPI_RELA_CATEGORY AS kc ON kpi.ID = kc.KPI_ID ");
            sql.append(" WHERE ");
            sql.append(" kc.CATEGORY_ID = :value ");
            mapParams.put("value", value);
        }else if("kpi".equals(type)){
            sql.append(" WHERE ");
            sql.append(" kpi.ID = :value ");
            mapParams.put("value", value);
        }else{
            sql.append(" WHERE 1=1 ");
        }
        sql.append(" AND kpi.DELETE_STATUS = '1'  ");
        sql.append(" order by kpi.ID desc");
        
        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
        
        return list;
    }
    
    /**
     * 根据影响指标查询风险
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-27
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findInfluKpi(String kpiid, String value, String type) {
        Map<String,String> mapParams = new HashMap<String,String>();
        String companyId = UserContext.getUser().getCompanyid();
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT DISTINCT ");
        sql.append(" r.ID AS id,r.RISK_CODE AS code,r.RISK_NAME AS name ");
        sql.append(" FROM T_RM_RISKS AS r ");
        sql.append(" INNER JOIN t_kpi_kpi_rela_risk AS kr ON r.ID = kr.RISK_ID ");
        
        if("sm".equals(type)){
            sql.append(" INNER JOIN t_kpi_sm_rela_kpi AS sk ON kr.KPI_ID = sk.KPI_ID ");
            sql.append(" WHERE ");
            sql.append(" sk.STRATEGY_MAP_ID = :value ");
            mapParams.put("value", value);
        }else if("sc".equals(type)){
            sql.append(" INNER JOIN t_kpi_kpi_rela_category AS kc ON kr.KPI_ID = kc.KPI_ID ");
            sql.append(" WHERE ");
            sql.append(" kc.CATEGORY_ID = :value ");
            mapParams.put("value", value);
        }else if("kpi".equals(type)){
            sql.append(" WHERE ");
            sql.append(" kr.KPI_ID = :value ");
            mapParams.put("value", value);
        }else{
            sql.append(" WHERE 1=1 ");
        }
        sql.append(" AND r.DELETE_ESTATUS = '1' AND ");
        sql.append(" r.IS_RISK_CLASS = 're' AND ");
        sql.append(" r.COMPANY_ID = :companyId AND ");
        sql.append(" kr.KPI_ID = :kpiid ");
        
        sql.append(" order by r.ID asc");
        mapParams.put("companyId", companyId);
        mapParams.put("kpiid", kpiid);
        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
        
        return list;
    }
    
    /**
     * 查询风险关联实体
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-9-5
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<KpiRelaRisk> findInfluKpiEntity(String kpiid, String value, String type) {
        String companyId = UserContext.getUser().getCompanyid();
        Criteria criteria = o_kpiRelaRiskDAO.createCriteria();
        criteria.add(Restrictions.eq("type", "I"));// 影响指标
        // 风险筛选条件
        criteria.createCriteria("risk", "r");
        criteria.add(Restrictions.eq("r.company.id", companyId));
        criteria.add(Restrictions.eq("r.isRiskClass", "re"));
        criteria.add(Restrictions.eq("r.deleteStatus", "1"));
        criteria.createCriteria("kpi", "kpi");
        criteria.add(Restrictions.eq("kpi.id", kpiid));
        if ("sm".equals(type)) {
            criteria.createCriteria("kpi.dmRelaKpis", "dk");
            criteria.createCriteria("dk.strategyMap", "dsm");
            criteria.add(Restrictions.eq("dsm.id", value));
        } else if ("sc".equals(type)) {
            criteria.createCriteria("kpi.KpiRelaCategorys", "kk");
            criteria.createCriteria("kk.category", "kc");
            criteria.add(Restrictions.eq("kc.id", value));
        }
        return criteria.list();
    }

    /**
     * 风险关联保存
     * 
     * @author 张健
     * @param
     * @return
     * @date 2013-8-28
     * @since Ver 1.1
     */
    @Transactional
    public void saveKpiRelaRisk(String kpiid, String riskids, String id, String type) {
        List<KpiRelaRisk> krrList = findInfluKpiEntity(kpiid, id, type);
        for (KpiRelaRisk kkr : krrList) {
            o_kpiRelaRiskDAO.delete(kkr);
        }
        if (StringUtils.isNotBlank(riskids)) {
            String[] riskidArray = riskids.split(",");
            for (String riskid : riskidArray) {
                KpiRelaRisk krr = new KpiRelaRisk();
                krr.setId(Identities.uuid());
                krr.setKpi(o_kpiDAO.get(kpiid));
                krr.setRisk(o_riskDAO.get(riskid));
                krr.setType("I");
                o_kpiRelaRiskDAO.merge(krr);
            }
        }
    }

    /**
     * 查询所有维度
     */
    @SuppressWarnings("unchecked")
    public List<Dimension> findDimensionDimList(String id) {
        Criteria criteria = o_dimensionDAO.createCriteria();
        if(StringUtils.isNotBlank(id)){
            criteria.add(Restrictions.eq("id", id));
        }
        return criteria.list();
    }

    /**
     * 按type查询所有打过分的风险
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-9-11
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findDimRiskAll(String value, String type, String dimvalue, boolean showgroup, String assessPlanId,String schm) {
        Map<String,Object> returnValue = this.getHeatMapSql(value,type,dimvalue,showgroup,assessPlanId,null,null, schm);
        String sql = (String) returnValue.get("sql");
        Map<String,String> mapParams = (Map<String, String>) returnValue.get("mapParams");
        List<Object[]> listReturn = new ArrayList<Object[]>();
        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
        for(Object[] obs : list){
            Object[] obsReturn = obs;
            if(obsReturn[3].toString().indexOf(".") != -1){
                String obsR = obsReturn[3].toString();
                obsReturn[3] = obsR.substring(0, obsR.length() > obsR.indexOf(".") ? obsR.indexOf(".") : obsR.length());
            }else if("null".equals(obsReturn[3].toString())){
                obsReturn[3] = "";
            }
            listReturn.add(obsReturn);
        }
        
        return list;
    }
    
    /**
     * 根据所属风险,分组显示数量
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-9-12
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findRiskGroupCount(String value ,String type, boolean showgroup, String assessPlanId){
        Map<String,Object> mapParams = new HashMap<String,Object>();
        String companyId = UserContext.getUser().getCompanyid();
        StringBuffer sql = new StringBuffer();
        if("risk".equals(type)){
            sql.append(" SELECT tt.PARENT_ID,COUNT(1) FROM ( ")
            .append("SELECT distinct r.id,r.parent_id ")
            .append(" FROM t_rm_risks r ")
            .append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id ")
            .append(" WHERE r.parent_id in(")
            .append("   SELECT tb.id from t_rm_risks tb");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append("   WHERE tb.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append("   tb.is_risk_class='rbs' ")
            .append(" and tb.delete_estatus='1' ");
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    sql.append(")");
                }else{
                    sql.append("    and tb.id_seq like :value )");
                    mapParams.put("value", "%."+value+".%");
                }
            }
            sql.append(" and r.is_risk_class='re' ");
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" and h.ASSESS_PLAN_ID=:assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            //联合
            sql.append(" UNION ");
            sql.append("select distinct r.id,r.parent_id ")
            .append(" FROM t_rm_risks_risks rr")
            .append(" LEFT JOIN t_rm_risks r on rr.risk_id = r.id")
            .append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id ")
            .append(" WHERE rr.rela_risk_id in(")
            .append("   SELECT tb.id from t_rm_risks tb");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append("   WHERE tb.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append(" tb.is_risk_class='rbs' ")
            .append("   and tb.delete_estatus='1' ");
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    sql.append(")");
                }else{
                    sql.append("    and tb.id_seq like :value )");
                    mapParams.put("value", "%."+value+".%");
                }
            }
            sql.append(" and r.is_risk_class='re' ");
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" and h.ASSESS_PLAN_ID=:assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            sql.append(" ) as tt GROUP BY tt.parent_id ");
        }else if("org".equals(type)){
            sql.append(" SELECT tt.PARENT_ID,COUNT(1) FROM ( ")
            .append("SELECT distinct r.id,r.parent_id ")
            .append(" FROM t_rm_risks r")
            .append(" LEFT JOIN t_rm_risk_org riskorg on r.id = riskorg.risk_id ")
            .append(" LEFT JOIN t_sys_organization org on org.id=riskorg.org_id ")
            .append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id ");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append(" WHERE r.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append(" r.is_risk_class='re' ");
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    
                }else{
                    sql.append(" and org.id_seq like :value ");
                    mapParams.put("value", "%."+value+".%");
                }
            }
            sql.append(" ) as tt GROUP BY tt.parent_id ");
        }else if("sm".equals(type)){
            String vas[] = value.split("_");
            List<String> kpi = new ArrayList<String>();//单个指标，或者是一个目标下的所有指标
            if(vas.length>1){
                //指标
                kpi.add(vas[1]);
            }else{
                //目标
                //查询目标下有哪些指标
                List<Kpi> kpiList = findKpiBySmId(value);
                for(int i=0;i<kpiList.size();i++){
                    kpi.add(((Kpi)kpiList.get(i)).getId());
                }
            }
            sql.append(" SELECT tt.PARENT_ID,COUNT(1) FROM ( ")
            .append("SELECT distinct r.id,r.parent_id ")
            .append(" FROM t_rm_risks r  ")
            .append(" LEFT JOIN t_kpi_kpi_rela_risk riskkpi on r.id = riskkpi.risk_id  ")
            .append(" LEFT JOIN t_kpi_kpi kpi on kpi.id=riskkpi.kpi_id ")
            .append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id ");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append(" WHERE r.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append(" r.is_risk_class='re' ");
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    
                }else{
                    sql.append(" and kpi.id in(:kpi)");
                    mapParams.put("kpi", kpi);
                }
            }
            sql.append(" ) as tt GROUP BY tt.parent_id ");
        }else if("process".equals(type)){
            sql.append(" SELECT tt.PARENT_ID,COUNT(1) FROM ( ")
            .append("SELECT distinct r.id,r.parent_id ")
            .append(" FROM t_rm_risks r  ")
            .append(" LEFT JOIN t_processure_risk_processure riskpro on r.id = riskpro.risk_id  ")
            .append(" LEFT JOIN t_ic_processure pro on pro.id=riskpro.processure_id  ")
            .append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id ");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append(" WHERE r.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append(" r.is_risk_class='re' ");
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    
                }else{
                    sql.append(" and pro.id_seq like :value ");
                    mapParams.put("value", "%."+value+".%");
                }
            }
            sql.append(" ) as tt GROUP BY tt.parent_id ");
        }
        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
        return list;
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
    @SuppressWarnings("unchecked")
    public List<Object[]> getRiskTrendLine(String value, String type){
        Map<String,String> mapParams = new HashMap<String,String>();
        StringBuffer sql = new StringBuffer();
        if("risk".equals(type) || "riskevent".equals(type)){
            sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS FROM T_RM_RISK_ADJUST_HISTORY ")
            .append(" where ")
            .append(" RISK_ID = :value ")
            .append("  GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
            mapParams.put("value", value);
        }else if("org".equals(type)){
            sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_ORG_ADJUST_HISTORY  ")
            .append(" where ")
            .append(" ORG_ID = :value ")
            .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
            mapParams.put("value", value);
        }else if("sm".equals(type)){
            String vas[] = value.split("_");
            if(vas.length>1){
                //指标
                sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_KPI_ADJUST_HISTORY  ")
                .append(" where ")
                .append(" KPI_ID = :value ")
                .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
                mapParams.put("value", vas[1]);
            }else{
                //目标
                sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_STRATEGY_ADJUST_HISTORY  ")
                .append(" where ")
                .append(" STRATEGY_ID = :value ")
                .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
                mapParams.put("value", value);
            }
        }else if("process".equals(type)){
            sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_PROCESSURE_ADJUST_HISTORY  ")
            .append(" where ")
            .append(" PROCESSURE_ID = :value ")
            .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
            mapParams.put("value", value);
        }
        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
        return list;
    }
    
    /**
     * 根据类型，获取点线图的最大数值
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-3-11
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMaxRiskTrendLine(String value, String type){
        Map<String,String> mapParams = new HashMap<String,String>();
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT MAX(aa.RISK_STATUS) from ( ");
        if("risk".equals(type) || "riskevent".equals(type)){
            sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS FROM T_RM_RISK_ADJUST_HISTORY ")
            .append(" where ")
            .append(" RISK_ID = :value ")
            .append("  GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
            mapParams.put("value", value);
        }else if("org".equals(type)){
            sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_ORG_ADJUST_HISTORY  ")
            .append(" where ")
            .append(" ORG_ID = :value ")
            .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
            mapParams.put("value", value);
        }else if("sm".equals(type)){
            String vas[] = value.split("_");
            if(vas.length>1){
                //指标
                sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_KPI_ADJUST_HISTORY  ")
                .append(" where ")
                .append(" KPI_ID = :value ")
                .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
                mapParams.put("value", vas[1]);
            }else{
                //目标
                sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_STRATEGY_ADJUST_HISTORY  ")
                .append(" where ")
                .append(" STRATEGY_ID = :value ")
                .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
                mapParams.put("value", value);
            }
        }else if("process".equals(type)){
            sql.append(" SELECT DISTINCT ADJUST_TIME,RISK_STATUS from T_RM_PROCESSURE_ADJUST_HISTORY  ")
            .append(" where ")
            .append(" PROCESSURE_ID = :value ")
            .append(" GROUP BY ADJUST_TIME,RISK_STATUS ORDER BY ADJUST_TIME ASC ");
            mapParams.put("value", value);
        }
        sql.append(" ) aa ");
        List<Object> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
        return list;
    }
    
    /**
     * 查询风险灯评价准则的最小值
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-3-11
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<Object> getAlarmValue(){
        String planid = "";
        String companyId = UserContext.getUser().getCompanyid();
        if(null != o_weightSetBO.findWeightSetAll(companyId) && null != o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario()){
            planid = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
        }
        String sql = " select min_value from t_com_alarm_region where " +
        		"alarm_plan_id=? and min_value != '0' and min_value != '-∞' order by min_value asc ";
        List<Object> list = o_riskDAO.createSQLQuery(sql.toString(),planid).list();
        return list;
    }
    
    /**
     * 根据风险图谱生成风险列表
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-9-27
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getHeatRiskGrid(String value,String type, String xvalue, String yvalue, boolean showgroup ,String assessPlanId,String xscore,String yscore,int start,int limit){
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> returnValuex = this.getHeatMapSql(value,type,xvalue,showgroup,assessPlanId,xscore,"x",null);
        Map<String, Object> returnValuey = this.getHeatMapSql(value,type,yvalue,showgroup,assessPlanId,yscore,"y",null);
        StringBuffer sql = new StringBuffer();
        sql.append(" select aa.rid,aa.`name`,aa.idSeq,aa.lastModifyTime,aa.parentid,aa.score,bb.score from ");
        sql.append(" ( " + returnValuex.get("sql") + " ) as aa, ");
        sql.append(" ( " + returnValuey.get("sql") + " ) as bb ");
        sql.append(" where aa.rid = bb.rid ");
        sql.append(" order by aa.parentid,aa.lastModifyTime desc ");
//        int size = o_riskDAO.createSQLQuery(sql.toString()).list().size();
//        int pagenum = (limit == 0 ? 0 : start / limit) + 1;
//        sql.append(" limit "+(pagenum-1)*limit+","+limit+" ");
//        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString()).list();
        Map<String,String> mapParamsX = (Map<String, String>) returnValuex.get("mapParams");
        Map<String,String> mapParamsY = (Map<String, String>) returnValuey.get("mapParams");
        mapParamsX.putAll(mapParamsY);
        
        //分页实现
        SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParamsX);
        int pagenum = (limit == 0 ? 0 : start / limit) + 1;
  		start = (pagenum-1)*limit;
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		List<Object[]> listReturn = new ArrayList<Object[]>();
  		
        for(Object[] obs : list){
            Object[] obsReturn = obs;
            if(obsReturn[5].toString().indexOf(".") != -1){
                String obsR = obsReturn[5].toString();
                obsReturn[5] = obsR.substring(0, obsR.length() > obsR.indexOf(".")+2 ? obsR.indexOf(".")+2 : obsR.length());
            }
            if(obsReturn[6].toString().indexOf(".") != -1){
                String obsR = obsReturn[6].toString();
                obsReturn[6] = obsR.substring(0, obsR.length() > obsR.indexOf(".")+2 ? obsR.indexOf(".")+2 : obsR.length());
            }
            listReturn.add(obsReturn);
        }
  		
        map.put("count", list.size());
        map.put("list", list);
        return map;
    }
    
    /**
     * 拼装HeatMap图表相关SQL
     * 
     * @author 张健
     * @param  schm 风险分库标识
     * @return 
     * @date 2013-9-27
     * @since Ver 1.1
     */
    private Map<String,Object> getHeatMapSql(String value, String type, String dimvalue, boolean showgroup, String assessPlanId,String score,String point, String schm){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,Object> mapParams = new HashMap<String,Object>();
        String companyId = UserContext.getUser().getCompanyid();
        StringBuffer sql = new StringBuffer();
        if ("org".equals(type)) {
            sql.append(" SELECT DISTINCT ");
            sql.append(" r.ID rid,r.RISK_NAME name,re.SCORE_DIM_ID dim,re.SCORE score, ");
            sql.append(" r.risk_code code,r.id_seq idSeq,r.last_modify_time lastModifyTime,r.parent_id parentid ");
            sql.append(" FROM T_RM_RISKS r ");
            if(StringUtils.isBlank(assessPlanId)){
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID AND h.IS_LATEST = '1' ");
            }else{
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID ");
            }
            sql.append(" LEFT JOIN t_rm_adjust_history_result re ON h.ID = re.ADJUST_HISTORY_ID ");
            sql.append(" LEFT JOIN t_rm_risk_org riskorg ON r.ID = riskorg.RISK_ID ");
            sql.append(" LEFT JOIN t_sys_organization org ON org.ID = riskorg.ORG_ID ");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append(" WHERE r.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    sql.append(" 1=1 ");
                }else{
                    sql.append(" org.id_seq like :value ");
                    mapParams.put("value", "%."+value+".%");
                }
            }
			if(null != schm && !"".equals(schm)){
				sql.append(" and r.schm=:schm");
				if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
					SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
					String seq = org.getOrgseq();
					String deptId = seq.split("\\.")[2];//部门编号
					
					sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
				}
				mapParams.put("schm", schm);
			}
            sql.append(" AND re.SCORE IS NOT NULL ");
            sql.append(" and r.is_risk_class='re' ");
            if(StringUtils.isNotBlank(point)){
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue"+point+" ");
                mapParams.put("dimvalue"+point, dimvalue);
            }else{
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue");
                mapParams.put("dimvalue", dimvalue);
            }
            if(StringUtils.isNotBlank(score)){
                if(StringUtils.isNotBlank(point)){
                    sql.append(" AND re.SCORE >= :score"+point+" AND re.SCORE < :scorema"+point+" ");
                    mapParams.put("score"+point, score);
                    mapParams.put("scorema"+point, String.valueOf(Integer.valueOf(score)+1));
                }else{
                    sql.append(" AND re.SCORE >= :score AND re.SCORE < :scorema ");
                    mapParams.put("score", score);
                    mapParams.put("scorema", String.valueOf(Integer.valueOf(score)+1));
                }
            }
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            sql.append(" order by r.ID asc ");
        }else if("risk".equals(type)){
            sql.append("SELECT DISTINCT ")
            .append(" r.ID rid,r.RISK_NAME name,re.SCORE_DIM_ID dim,re.SCORE score, ")
            .append(" r.risk_code code,r.id_seq idSeq,r.last_modify_time lastModifyTime,r.parent_id parentid ")
            .append(" FROM T_RM_RISKS r ");
            if(StringUtils.isBlank(assessPlanId)){
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID AND h.IS_LATEST = '1' ");
            }else{
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID ");
            }
            sql.append(" LEFT JOIN t_rm_adjust_history_result re ON h.ID = re.ADJUST_HISTORY_ID ")
            .append(" WHERE r.parent_id in(")
            .append("   SELECT tb.id from t_rm_risks tb");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append("   WHERE tb.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append("   tb.is_risk_class='rbs'")
            .append("   and tb.delete_estatus='1'");
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    sql.append(")");
                }else{
                    sql.append("    and tb.id_seq like :value )");
                    mapParams.put("value", "%."+value+".%");
                }
            }else{
                sql.append(")");
            }
            sql.append(" and r.is_risk_class='re'");
            if(StringUtils.isNotBlank(point)){
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue"+point+" ");
                mapParams.put("dimvalue"+point, dimvalue);
            }else{
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue");
                mapParams.put("dimvalue", dimvalue);
            }
            if(StringUtils.isNotBlank(score)){
                if(StringUtils.isNotBlank(point)){
                    sql.append(" AND re.SCORE >= :score"+point+" AND re.SCORE < :scorema"+point+" ");
                    mapParams.put("score"+point, score);
                    mapParams.put("scorema"+point, String.valueOf(Integer.valueOf(score)+1));
                }else{
                    sql.append(" AND re.SCORE >= :score AND re.SCORE < :scorema ");
                    mapParams.put("score", score);
                    mapParams.put("scorema", String.valueOf(Integer.valueOf(score)+1));
                }
            }
            if(assessPlanId != null && StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            if(StringUtils.isNotBlank(assessPlanId)){
                returnValue.put("sql", sql.toString());
                returnValue.put("mapParams", mapParams );
                return returnValue;
            }
            //联合
            sql.append(" UNION ");
            sql.append("SELECT DISTINCT ")
            .append(" r.ID rid,r.RISK_NAME name,re.SCORE_DIM_ID dim,re.SCORE score, ")
            .append(" r.risk_code code,r.id_seq idSeq,r.last_modify_time lastModifyTime,r.parent_id parentid ")
            .append(" FROM t_rm_risks_risks rr")
            .append(" LEFT JOIN t_rm_risks r on rr.risk_id = r.id");
            if(StringUtils.isBlank(assessPlanId)){
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID AND h.IS_LATEST = '1' ");
            }else{
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID ");
            }
            sql.append(" LEFT JOIN t_rm_adjust_history_result re ON h.ID = re.ADJUST_HISTORY_ID ")
            .append(" WHERE rr.rela_risk_id in(")
            .append("   SELECT tb.id from t_rm_risks tb");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append("   WHERE tb.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append("   tb.is_risk_class='rbs'")
            .append("   and tb.delete_estatus='1'");
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    sql.append(")");
                }else{
                    sql.append("  and tb.id_seq like :value )");
                    mapParams.put("value", "%."+value+".%");
                }
            }else{
                sql.append(")");
            }
            sql.append(" and r.is_risk_class='re'");
            if(StringUtils.isNotBlank(point)){
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue"+point+" ");
                mapParams.put("dimvalue"+point, dimvalue);
            }else{
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue");
                mapParams.put("dimvalue", dimvalue);
            }
            if(StringUtils.isNotBlank(score)){
                if(StringUtils.isNotBlank(point)){
                    sql.append(" AND re.SCORE >= :score"+point+" AND re.SCORE < :scorema"+point+" ");
                    mapParams.put("score"+point, score);
                    mapParams.put("scorema"+point, String.valueOf(Integer.valueOf(score)+1));
                }else{
                    sql.append(" AND re.SCORE >= :score AND re.SCORE < :scorema ");
                    mapParams.put("score", score);
                    mapParams.put("scorema", String.valueOf(Integer.valueOf(score)+1));
                }
            }
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
        }else if("sm".equals(type)){
            String vas[] = value.split("_");
            List<String> kpi = new ArrayList<String>();//单个指标，或者是一个目标下的所有指标
            if(vas.length>1){
                //指标
                kpi.add(vas[1]);
            }else{
                //目标
                //查询目标下有哪些指标
                List<Kpi> kpiList = findKpiBySmId(value);
                for(int i=0;i<kpiList.size();i++){
                    kpi.add(((Kpi)kpiList.get(i)).getId());
                }
            }
            sql.append(" SELECT DISTINCT ")
            .append(" r.ID rid,r.RISK_NAME name,re.SCORE_DIM_ID dim,re.SCORE score, ")
            .append(" r.risk_code code,r.id_seq idSeq,r.last_modify_time lastModifyTime,r.parent_id parentid ")
            .append(" FROM t_rm_risks r  ")
            .append(" LEFT JOIN t_kpi_kpi_rela_risk riskkpi on r.id = riskkpi.risk_id  ")
            .append(" LEFT JOIN t_kpi_kpi kpi on kpi.id=riskkpi.kpi_id ");
            if(StringUtils.isBlank(assessPlanId)){
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID AND h.IS_LATEST = '1' ");
            }else{
                sql.append(" LEFT JOIN t_kpi_kpi_rela_risk kkr on kkr.RISK_ID = r.ID ");
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID ");
            }
            sql.append(" LEFT JOIN t_rm_adjust_history_result AS re ON h.ID = re.ADJUST_HISTORY_ID ");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append(" WHERE r.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append(" r.is_risk_class='re' and ")
            .append(" re.SCORE IS NOT NULL ");
            if(value != null){
                if(value.equalsIgnoreCase("root")){
					if(null != schm && !"".equals(schm)){
						sql.append(" and r.schm=:schm");
						if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
							SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
							String seq = org.getOrgseq();
							String deptId = seq.split("\\.")[2];//部门编号
							
							sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
						}
						mapParams.put("schm", schm);
					}
                }else{
                    sql.append(" and kpi.id in(:kpi)");
                    mapParams.put("kpi", kpi);
                }
            }
			if(null != schm && !"".equals(schm)){
				sql.append(" and r.schm=:schm");
				if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
					SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
					String seq = org.getOrgseq();
					String deptId = seq.split("\\.")[2];//部门编号
					
					sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
				}
				mapParams.put("schm", schm);
			}
            if(StringUtils.isNotBlank(point)){
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue"+point+" ");
                mapParams.put("dimvalue"+point, dimvalue);
            }else{
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue");
                mapParams.put("dimvalue", dimvalue);
            }
            if(StringUtils.isNotBlank(score)){
                if(StringUtils.isNotBlank(point)){
                    sql.append(" AND re.SCORE >= :score"+point+" AND re.SCORE < :scorema"+point+" ");
                    mapParams.put("score"+point, score);
                    mapParams.put("scorema"+point, String.valueOf(Integer.valueOf(score)+1));
                }else{
                    sql.append(" AND re.SCORE >= :score AND re.SCORE < :scorema ");
                    mapParams.put("score", score);
                    mapParams.put("scorema", String.valueOf(Integer.valueOf(score)+1));
                }
            }
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                sql.append(" AND kkr.etype='i' ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
            sql.append("  order by r.ID asc  ");
        }else if("process".equals(type)){
            sql.append(" SELECT DISTINCT ")
            .append(" r.ID rid,r.RISK_NAME name,re.SCORE_DIM_ID dim,re.SCORE score, ")
            .append(" r.risk_code code,r.id_seq idSeq,r.last_modify_time lastModifyTime,r.parent_id parentid ")
            .append(" FROM t_rm_risks r  ")
            .append(" LEFT JOIN t_processure_risk_processure riskpro on r.id = riskpro.risk_id  ")
            .append(" LEFT JOIN t_ic_processure pro on pro.id=riskpro.processure_id  ");
            if(StringUtils.isBlank(assessPlanId)){
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID AND h.IS_LATEST = '1' ");
            }else{
                sql.append("LEFT JOIN t_processure_risk_processure prp on prp.RISK_ID = r.id   ");
                sql.append(" LEFT JOIN t_rm_risk_adjust_history h ON r.ID = h.RISK_ID ");
            }
            sql.append(" LEFT JOIN t_rm_adjust_history_result re ON h.ID = re.ADJUST_HISTORY_ID ");
            if(showgroup){
                sql.append(" WHERE ");
            }else{
                sql.append(" WHERE r.company_id=:companyId AND ");
                mapParams.put("companyId", companyId);
            }
            sql.append(" r.is_risk_class='re' and ")
            .append(" re.SCORE IS NOT NULL ");
            if(value != null){
                if(value.equalsIgnoreCase("root")){
                    
                }else{
                    sql.append(" and pro.id_seq like :value ");
                    mapParams.put("value", "%."+value+".%");
                }
            }
            if(StringUtils.isNotBlank(point)){
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue"+point+" ");
                mapParams.put("dimvalue"+point, dimvalue);
            }else{
                sql.append(" AND re.SCORE_DIM_ID =:dimvalue");
                mapParams.put("dimvalue", dimvalue);
            }
            if(StringUtils.isNotBlank(score)){
                if(StringUtils.isNotBlank(point)){
                    sql.append(" AND re.SCORE >= :score"+point+" AND re.SCORE < :scorema"+point+" ");
                    mapParams.put("score"+point, score);
                    mapParams.put("scorema"+point, String.valueOf(Integer.valueOf(score)+1));
                }else{
                    sql.append(" AND re.SCORE >= :score AND re.SCORE < :scorema ");
                    mapParams.put("score", score);
                    mapParams.put("scorema", String.valueOf(Integer.valueOf(score)+1));
                }
            }
            if(StringUtils.isNotBlank(assessPlanId)){
                sql.append(" and (r.delete_estatus='1' or r.delete_estatus='2') ");
                sql.append(" AND h.ASSESS_PLAN_ID = :assessPlanId ");
                sql.append(" AND prp.etype='i'  ");
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql.append(" and r.delete_estatus='1' ");
            }
			if(null != schm && !"".equals(schm)){
				sql.append(" and r.schm=:schm");
				if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
					SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
					String seq = org.getOrgseq();
					String deptId = seq.split("\\.")[2];//部门编号
					
					sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
				}
				mapParams.put("schm", schm);
			}
            sql.append("  order by r.ID asc  ");
        }
        returnValue.put("sql", sql.toString());
        returnValue.put("mapParams", mapParams );
        return returnValue;
    }
    
    
    public List<Object[]> riskAssessCmp(String templateid,String hisid,String value,String type){
        List<Object[]> list = new ArrayList<Object[]>();
        //所有被打分维度的打分情况
        if("risk".equals(type) || "riskevent".equals(type)){
            Map<String, Object> scoreMap = this.getDimScoreById(hisid,value);
            //模板维度的全部信息
            List<TemplateRelaDimension> templateRelaDimensionAllList = o_templateRelaDimensionBO.findTemplateRelaDimensionAllList();
            List<TemplateRelaDimension> templateRelaDimensionList = null;
            List<TemplateRelaDimension> templateRelaDimensionList2 = null;
            //当前模板的维度
            templateRelaDimensionList = o_quaAssessNextBO.getTemplateRelaDimensionListByTemplateId(templateid, templateRelaDimensionAllList);
            for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionList) {
                if(templateRelaDimension.getParent() == null){
                    //当前模板的下级维度
                    templateRelaDimensionList2 = o_quaAssessNextBO.getTemplateRelaDimensionListByParentId(templateRelaDimension.getId(), templateRelaDimensionAllList);
                    Object[] obs = new Object[5];
                    if(templateRelaDimensionList2.size() != 0){
                        //存在下级
                        obs[0] = templateid;//模板ID
                        obs[1] = templateRelaDimension.getDimension().getId();//主维度ID
                        obs[2] = templateRelaDimension.getDimension().getName();//主维度NAME
                        List<Object[]> listdown = new ArrayList<Object[]>();
                        for (TemplateRelaDimension template : templateRelaDimensionList2) {
                            Object[] obdown = new Object[3];
                            obdown[0] = template.getDimension().getId();//下级维度ID
                            obdown[1] = template.getDimension().getName();//下级维度NAME
                            obdown[2] = scoreMap.get(template.getDimension().getId());//子维度分值
                            listdown.add(obdown);
                        }
                        obs[3] = listdown;//子维度list
                        obs[4] = (
                                scoreMap.get(templateRelaDimension.getDimension().getId()) == null ? 
                                        "" : scoreMap.get(templateRelaDimension.getDimension().getId())
                                );//主维度分值
                        list.add(obs);
                    }else{
                        //不存在下级
                        obs[0] = templateid;//模板ID
                        obs[1] = templateRelaDimension.getDimension().getId();//主维度ID
                        obs[2] = templateRelaDimension.getDimension().getName();//主维度NAME
                        obs[3] = null;//子维度list
                        obs[4] = (
                                scoreMap.get(templateRelaDimension.getDimension().getId()) == null ? 
                                        "" : scoreMap.get(templateRelaDimension.getDimension().getId())
                                );//主维度分值
                        list.add(obs);
                    }
                }
            }
        }else{
            Object[] obs = new Object[1];
            if("org".equals(type)){
                OrgAdjustHistory orgAdjustHistory = o_orgAdjustHistoryDAO.get(hisid);
                if(orgAdjustHistory != null){
                    obs[0] = orgAdjustHistory.getRiskStatus();//风险值
                }
            }else if("strategy".equals(type) || "sm".equals(type)){
                String[] ids = value.split("_");
                if(ids.length>1){
                    KpiAdjustHistory kpiAdjustHistory = o_kpiAdjustHistoryDAO.get(hisid);
                    if(kpiAdjustHistory != null){
                        obs[0] = kpiAdjustHistory.getRiskStatus();//风险值
                    }
                }else{
                    StrategyAdjustHistory strategyAdjustHistory = o_strategyAdjustHistoryDAO.get(hisid);
                    if(strategyAdjustHistory != null){
                        obs[0] = strategyAdjustHistory.getRiskStatus();//风险值
                    }
                }
            }else if("process".equals(type)){
                ProcessAdjustHistory processAdjustHistory = o_processAdjustHistoryDAO.get(hisid);
                if(processAdjustHistory != null){
                    obs[0] = processAdjustHistory.getRiskStatus();//风险值
                }
            }else if("formula".equals(type)){
                RiskAdjustHistory riskAdjustHistory = o_riskAdjustHistoryDAO.get(hisid);
                if(riskAdjustHistory != null){
                    obs[0] = riskAdjustHistory.getStatus();//风险值
                }
            }
            list.add(obs);
        }
        return list;
    }
    
    /**
     * 根据历史记录ID查询维度的打分情况
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-10-9
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDimScoreById(String hisid,String value){
        Map<String, Object> map = new HashMap<String, Object>();
        if(StringUtils.isNotBlank(hisid)){
            RiskAdjustHistory riskAdjustHistory = o_riskAdjustHistoryDAO.get(hisid);
            if(riskAdjustHistory != null){
                java.text.DecimalFormat df =new java.text.DecimalFormat("#.00");
                if("0".equals(riskAdjustHistory.getAdjustType())){
                    if(riskAdjustHistory.getRiskAssessPlan() != null){
                        //评估计划状态，到评估计划表中查找维度分值
                        String sql = "select SCORE_DIM_ID,score from t_rm_statistics_result where assess_plan_id = ? and score_object_id = ? ";
                        List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),riskAdjustHistory.getRiskAssessPlan().getId(),value).list();
                        for(Object[] obs : list){
                            map.put(obs[0].toString(), df.format(Double.valueOf(obs[1].toString())));
                        }
                    }
                }else{
                    //手动输入的，和评估修改后的，到历史结果表中查找维度分值
                    String sql = "select score_dim_id,score from t_rm_adjust_history_result where adjust_history_id = ? and assess_plan_id is null ";
                    List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),hisid).list();
                    for(Object[] obs : list){
                    	if(!"".equalsIgnoreCase(obs[1].toString())){
                    		map.put(obs[0].toString(), df.format(Double.valueOf(obs[1].toString())));
                    	}
                    }
                    
                }
            }
        }
        return map;
    }
    
    
    /**
     * 根据子维度和主维度的打分情况，去计算风险历史记录的风险值
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-10-12
     * @since Ver 1.1
     */
    @Transactional
    public double riskAssessHisStatus(String[] saveinfos,String templateid,Map<String,Object> mapValue) throws ParseException{
        String temp = "";
        ArrayList<String> scoreDicValuelist = new ArrayList<String>();
        String riskLevelFormula = "";//公式
        String companyId = UserContext.getUser().getCompanyid();
        if(null != o_formulaSetBO.findFormulaSet(companyId)){
            riskLevelFormula = o_formulaSetBO.findFormulaSet(companyId).getRiskLevelFormula();
        }
        ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
        HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
                o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
        HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
                o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
        HashMap<String, TemplateRelaDimension> map = 
                o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
        
        for(String info : saveinfos){
            String[] infos  = info.split("\\*");
            String scoreDimId = infos[0].split(",")[0];
            
            String tempLateRelaDimensionId = "";
            ArrayList<TemplateRelaDimension> templateRelaDimensionList = null;
            TemplateRelaDimension parent = templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateid).getParent();
            if(parent != null){
                //子维度
                templateRelaDimensionList = templateRelaDimensionIsParentIdIsNullInfoAllMap.get(parent.getId());
                temp += parent.getId() + "--";
                tempLateRelaDimensionId = parent.getId();
            }else{
                tempLateRelaDimensionId = scoreDimId;
            }
            
            
            if(temp.split("--").length > 1){
                if(temp.indexOf(tempLateRelaDimensionId) != -1){
                    continue;
                }
            }
            
            if(null != templateRelaDimensionList){
                String calculateMethod = "";
                if(map.get(tempLateRelaDimensionId).getCalculateMethod() != null){
                    calculateMethod = map.get(tempLateRelaDimensionId).getCalculateMethod().getId();//计算方式
                }
                
                double scoreDimWeight = 0L;
                if(map.get(tempLateRelaDimensionId) != null){
                    scoreDimWeight = map.get(tempLateRelaDimensionId).getWeight();//权重
                }
                
                double[] scoreDicValue = null;
                String[] tempScoreDicValue = null;
                StringBuffer scoreDicValues = new StringBuffer();
                double scoreDimValue = 0l;
                
                //多维度
                for (TemplateRelaDimension templateRelaDimensions : templateRelaDimensionList) {
                    scoreDicValues.append(mapValue.get(templateRelaDimensions.getDimension().getId()));
                    scoreDicValues.append("--");
                }
                
                tempScoreDicValue = scoreDicValues.toString().split("--");
                scoreDicValue = new double[tempScoreDicValue.length];
                int count = 0;
                for (int i = 0; i < scoreDicValue.length; i++) {
                    if(!tempScoreDicValue[i].toString().equalsIgnoreCase("null")){
                        scoreDicValue[i] = Double.parseDouble(tempScoreDicValue[i]);
                        count++;
                    }
                }
                if(count == scoreDicValue.length){
                    scoreDimValue = DynamicDim.getValue(calculateMethod, scoreDimWeight, scoreDicValue);//维度分值
                    scoreDicValuelist.add(map.get(tempLateRelaDimensionId).getDimension().getId() + "--" + scoreDimValue);
                }
            }else{
                //单维度
                if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateid) != null){
                    if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateid).getParent() == null){
                        if(null != mapValue.get(scoreDimId)){
                            scoreDicValuelist.add(scoreDimId + "--" + mapValue.get(scoreDimId));
                        }
                    }
                }
            }
        }
        
        String riskScoreValueStr = o_riskLevelBO.getRisklevel(scoreDicValuelist, riskLevelFormula, dimIdAllList);
        double riskScoreValue = 0;
        if(riskScoreValueStr.indexOf("--") == -1){
        	riskScoreValue = Double.parseDouble(riskScoreValueStr);
        }
        
        return riskScoreValue;
    }
    
    public Set<String> getRiskAssessHisStatusSet(String[] saveinfos,String templateid,Map<String,Object> mapValue) throws ParseException{
        Set<String> setValue = new HashSet<String>();
        String temp = "";
        ArrayList<String> scoreDicValuelist = new ArrayList<String>();
        HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionIsParentIdIsNullInfoAllMap = 
                o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
        HashMap<String, TemplateRelaDimension> templateRelaDimensionByIdAndTemplateIdAllMap = 
                o_templateRelaDimensionBO.findTemplateRelaDimensionByIdAndTemplateIdAllMap();
        HashMap<String, TemplateRelaDimension> map = 
                o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
        
        for(String info : saveinfos){
            String[] infos  = info.split("\\*");
            String scoreDimId = infos[0].split(",")[0];
            
            String tempLateRelaDimensionId = "";
            ArrayList<TemplateRelaDimension> templateRelaDimensionList = null;
            TemplateRelaDimension parent = templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateid).getParent();
            if(parent != null){
                //子维度
                templateRelaDimensionList = templateRelaDimensionIsParentIdIsNullInfoAllMap.get(parent.getId());
                temp += parent.getId() + "--";
                tempLateRelaDimensionId = parent.getId();
            }else{
                tempLateRelaDimensionId = scoreDimId;
            }
            
            
            if(temp.split("--").length > 1){
                if(temp.indexOf(tempLateRelaDimensionId) != -1){
                    continue;
                }
            }
            
            if(null != templateRelaDimensionList){
                String calculateMethod = "";
                if(map.get(tempLateRelaDimensionId).getCalculateMethod() != null){
                    calculateMethod = map.get(tempLateRelaDimensionId).getCalculateMethod().getId();//计算方式
                }
                
                double scoreDimWeight = 0L;
                if(map.get(tempLateRelaDimensionId) != null){
                    scoreDimWeight = map.get(tempLateRelaDimensionId).getWeight();//权重
                }
                
                double[] scoreDicValue = null;
                String[] tempScoreDicValue = null;
                String scoreDicValues = "";
                double scoreDimValue = 0l;
                
                //多维度
                for (TemplateRelaDimension templateRelaDimensions : templateRelaDimensionList) {
                    scoreDicValues += mapValue.get(templateRelaDimensions.getDimension().getId()) + "--";
                    setValue.add(templateRelaDimensions.getDimension().getId() + "," + mapValue.get(templateRelaDimensions.getDimension().getId()));
                }
                
                tempScoreDicValue = scoreDicValues.split("--");
                scoreDicValue = new double[tempScoreDicValue.length];
                int count = 0;
                for (int i = 0; i < scoreDicValue.length; i++) {
                    if(!tempScoreDicValue[i].toString().equalsIgnoreCase("null")){
                        scoreDicValue[i] = Double.parseDouble(tempScoreDicValue[i]);
                        count++;
                    }
                }
                if(count == scoreDicValue.length){
                    scoreDimValue = DynamicDim.getValue(calculateMethod, scoreDimWeight, scoreDicValue);//维度分值
                    scoreDicValuelist.add(map.get(tempLateRelaDimensionId).getDimension().getId() + "--" + scoreDimValue);
                    setValue.add(infos[0].split(",")[1] + "," + scoreDimValue);
                }
            }else{
                //单维度
                if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateid) != null){
                    if(templateRelaDimensionByIdAndTemplateIdAllMap.get(scoreDimId + "--" + templateid).getParent() == null){
                        if(null != mapValue.get(scoreDimId)){
                            scoreDicValuelist.add(scoreDimId + "--" + mapValue.get(scoreDimId));
                            setValue.add(scoreDimId + "," + mapValue.get(scoreDimId));
                        }
                    }
                }
            }
        }
        return setValue;
    }
    
    /**
     * 根据风险值计算灯的颜色
     * 
     * @author 张健
     * @param riskScoreValue 风险值
     * @return 
     * @date 2014-3-5
     * @since Ver 1.1
     */
    public String getRiskIcon(double riskScoreValue){
        String companyId = UserContext.getUser().getCompanyid();
        String alarmScenario = "";
        if(null != o_weightSetBO.findWeightSetAll(companyId)){
            alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
        }
        HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
        DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), riskScoreValue);
        String riskIcon = "";
        if(dict != null){
            riskIcon = dict.getValue();//风险灯
        }
        return riskIcon;
    }
    
    /**
     * 评估模板历史记录重新打分的新增方法
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-10-12
     * @since Ver 1.1
     */
    @Transactional
    public void riskAssessSave(Set<String> setValue,String value,String templateid,String type,double riskScoreValue) throws ParseException{
        String companyId = UserContext.getUser().getCompanyid();
        HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
        String riskIcon = "";
        ArrayList<String> idsArrayList = new ArrayList<String>();
        riskIcon = this.getRiskIcon(riskScoreValue);//灯的颜色
        if("risk".equals(type) || "riskevent".equals(type)){
            //保存风险历史记录
        	idsArrayList.add(value);
            o_riskAdjustHistoryBO.updateRiskAdjustHistory(idsArrayList);//使所有风险的历史记录，最终状态改为0
            RiskAdjustHistory riskAdjustHistory = new RiskAdjustHistory();
            riskAdjustHistory.setId(Identities.uuid());
            riskAdjustHistory.setRisk(this.findRiskById(value));
            riskAdjustHistory.setStatus((double)(Math.round(riskScoreValue*100)/100.0));
            riskAdjustHistory.setAdjustType("1");//手动输入
            riskAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
            riskAdjustHistory.setIsLatest("1");
            Template template = new Template();
            template.setId(templateid);
            riskAdjustHistory.setTemplate(template);
            SysOrganization sysOrganization = new SysOrganization();
            sysOrganization.setId(UserContext.getUser().getCompanyid());//公司ID
            riskAdjustHistory.setCompany(sysOrganization);
            riskAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
            riskAdjustHistory.setAssessementStatus(riskIcon);
            riskAdjustHistory.setCalculateFormula(o_formulaSetBO.findFormulaSet(companyId).getRiskLevelFormulaText());
            //趋势计算
            HashMap<String, RiskAdjustHistory> riskAdjustHistoryListMapAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllMap();
            List<RiskAdjustHistory> riskAdjustHistoryListAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllList(value);
            //上一区间值
            if(null!=value){
            double parentRiskLevel = o_riskRbsOperBO.getRiskParentRiskLevel(riskAdjustHistoryListAll, value, riskAdjustHistory.getAdjustTime().toString());
            if(parentRiskLevel != -1){
                if(riskScoreValue > parentRiskLevel){
                    //up：向上；
                    riskAdjustHistory.setEtrend("up");
                }else if(riskScoreValue < parentRiskLevel){
                    //down：向下；
                    riskAdjustHistory.setEtrend("down");
                }else if(riskScoreValue == parentRiskLevel){
                    //flat：水平
                    riskAdjustHistory.setEtrend("flat");
                }
            }
          
            //修改下一区间值
            RiskAdjustHistory riskAdjustHistoryNext = o_riskRbsOperBO.riskNextRiskLevel(riskAdjustHistoryListAll, value, riskAdjustHistory.getAdjustTime().toString(), riskAdjustHistoryListMapAll, riskScoreValue);
            if(riskAdjustHistoryNext != null){
                o_riskAdjustHistoryDAO.merge(riskAdjustHistoryNext);
            }
            
            o_riskAdjustHistoryDAO.merge(riskAdjustHistory);
            }
            //保存维度分值记录
            for(String info : setValue){
                String[] infos  = info.split(",");
                String scoreDimId = infos[0];
                AdjustHistoryResult adjustHistoryResult = new AdjustHistoryResult();
                adjustHistoryResult.setId(Identities.uuid());
                adjustHistoryResult.setAdjustHistoryId(riskAdjustHistory.getId());
                Dimension dimension = new Dimension();
                dimension.setId(scoreDimId);
                adjustHistoryResult.setDimension(dimension);
                adjustHistoryResult.setScore(infos[1]);
                o_adjustHistoryResultDAO.merge(adjustHistoryResult);
            }
            
        }else{
            if("org".equals(type)){
            	idsArrayList.add(value);
                o_orgAdjustHistoryBO.updateOrgAdjustHistory(idsArrayList);
                OrgAdjustHistory orgAdjustHistory = new OrgAdjustHistory();
                orgAdjustHistory.setId(Identities.uuid());
                SysOrganization sysOrganization = new SysOrganization();
                sysOrganization.setId(value);//公司ID
                orgAdjustHistory.setOrganization(sysOrganization);
                orgAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                orgAdjustHistory.setAdjustType("1");//手动收入
                orgAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
                orgAdjustHistory.setIsLatest("1");
                orgAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
                orgAdjustHistory.setAssessementStatus(riskIcon);
                orgAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                //趋势计算
                List<OrgAdjustHistory> orgAdjustHistoryListAll = o_orgAdjustHistoryBO.findOrgAdjustHistoryByAssessAllList();
                HashMap<String, OrgAdjustHistory> orgAdjustHistoryByIdMapAll = o_orgAdjustHistoryBO.findOrgAdjustHistoryByAssessAllMap();
                //上一区间值
                double parentRiskLevel = o_orgOperBO.getOrgParentRiskLevel(orgAdjustHistoryListAll, value, orgAdjustHistory.getAdjustTime().toString());
                if(parentRiskLevel != -1){
                    if(riskScoreValue > parentRiskLevel){
                        //up：向上；
                        orgAdjustHistory.setEtrend("up");
                    }else if(riskScoreValue < parentRiskLevel){
                        //down：向下；
                        orgAdjustHistory.setEtrend("down");
                    }else if(riskScoreValue == parentRiskLevel){
                        //flat：水平
                        orgAdjustHistory.setEtrend("flat");
                    }
                }
                //修改下一区间值
                o_orgOperBO.orgNextRiskLevel(orgAdjustHistoryListAll, value, orgAdjustHistory.getAdjustTime().toString(), orgAdjustHistoryByIdMapAll, riskScoreValue);
                o_orgAdjustHistoryDAO.merge(orgAdjustHistory);
            }else if("strategy".equals(type) || "sm".equals(type)){
                String[] ids = value.split("_");
                if(ids.length>1){
                	idsArrayList.add(ids[1]);
                    o_kpiAdjustHistoryBO.updateKpiAdjustHistory(idsArrayList);
                    KpiAdjustHistory kpiAdjustHistory = new KpiAdjustHistory();
                    kpiAdjustHistory.setId(Identities.uuid());
                    Kpi kpi = new Kpi();
                    kpi.setId(ids[1]);
                    kpiAdjustHistory.setKpiId(kpi);
                    kpiAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                    kpiAdjustHistory.setAdjustType("1");//手动收入
                    kpiAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
                    kpiAdjustHistory.setIsLatest("1");
                    kpiAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
                    kpiAdjustHistory.setAssessementStatus(riskIcon);
                    kpiAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                    //趋势计算
                    List<KpiAdjustHistory> kpiAdjustHistoryListAll = o_kpiAdjustHistoryBO.findKpiAdjustHistoryByAssessAllList();
                    HashMap<String, KpiAdjustHistory> kpiAdjustHistoryByIdMapAll = o_kpiAdjustHistoryBO.findKpiAdjustHistoryByAssessAllMap();
                    //上一区间值
                    double parentRiskLevel = o_kpiOperBO.getKpiParentRiskLevel(kpiAdjustHistoryListAll, value, kpiAdjustHistory.getAdjustTime().toString());
                    if(parentRiskLevel != -1){
                        if(riskScoreValue > parentRiskLevel){
                            //up：向上；
                            kpiAdjustHistory.setEtrend("up");
                        }else if(riskScoreValue < parentRiskLevel){
                            //down：向下；
                            kpiAdjustHistory.setEtrend("down");
                        }else if(riskScoreValue == parentRiskLevel){
                            //flat：水平
                            kpiAdjustHistory.setEtrend("flat");
                        }
                    }
                    //修改下一区间值
                    o_kpiOperBO.kpiNextRiskLevel(kpiAdjustHistoryListAll, value, kpiAdjustHistory.getAdjustTime().toString(), kpiAdjustHistoryByIdMapAll, riskScoreValue);
                    o_kpiAdjustHistoryDAO.merge(kpiAdjustHistory);
                }else{
                	idsArrayList.add(ids[0]);
                    o_strategyAdjustHistoryBO.updateStrategyAdjustHistory(idsArrayList);
                    StrategyAdjustHistory strategyAdjustHistory = new StrategyAdjustHistory();
                    strategyAdjustHistory.setId(Identities.uuid());
                    StrategyMap strategyMap = new StrategyMap();
                    strategyMap.setId(ids[0]);
                    strategyAdjustHistory.setStrategyMap(strategyMap);
                    strategyAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                    strategyAdjustHistory.setAdjustType("1");//手动收入
                    strategyAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
                    strategyAdjustHistory.setIsLatest("1");
                    strategyAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
                    strategyAdjustHistory.setAssessementStatus(riskIcon);
                    strategyAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                    //趋势计算
                    List<StrategyAdjustHistory> strategyAdjustHistoryListAll = o_strategyAdjustHistoryBO.findStrategyAdjustHistoryByAssessAllList();
                    HashMap<String, StrategyAdjustHistory> strategyAdjustHistoryByIdMapAll = 
                            o_strategyAdjustHistoryBO.findStrategyAdjustHistoryByAssessAllMap();
                    //上一区间值
                    double parentRiskLevel = o_strategyMapOperBO.getStrategyParentRiskLevel(strategyAdjustHistoryListAll, value, strategyAdjustHistory.getAdjustTime().toString());
                    if(parentRiskLevel != -1){
                        if(riskScoreValue > parentRiskLevel){
                            //up：向上；
                            strategyAdjustHistory.setEtrend("up");
                        }else if(riskScoreValue < parentRiskLevel){
                            //down：向下；
                            strategyAdjustHistory.setEtrend("down");
                        }else if(riskScoreValue == parentRiskLevel){
                            //flat：水平
                            strategyAdjustHistory.setEtrend("flat");
                        }
                    }
                    //修改下一区间值
                    o_strategyMapOperBO.strategyNextRiskLevel(strategyAdjustHistoryListAll, value, strategyAdjustHistory.getAdjustTime().toString(), strategyAdjustHistoryByIdMapAll, riskScoreValue);
                    o_strategyAdjustHistoryDAO.merge(strategyAdjustHistory);
                }
            }else if("process".equals(type)){
                o_processAdjustHistoryBO.updateProcessAdjustHistory("'"+value+"'");
                ProcessAdjustHistory processAdjustHistory = new ProcessAdjustHistory();
                processAdjustHistory.setId(Identities.uuid());
                Process process = new Process();
                process.setId(value);
                processAdjustHistory.setProcess(process);
                processAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                processAdjustHistory.setAdjustType("1");//手动收入
                processAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
                processAdjustHistory.setIsLatest("1");
                processAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
                processAdjustHistory.setAssessementStatus(riskIcon);
                processAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                //趋势计算
                List<ProcessAdjustHistory> processAdjustHistoryListAll = o_processAdjustHistoryBO.findProcessAdjustHistoryByAssessAllList();
                HashMap<String, ProcessAdjustHistory> processAdjustHistoryByIdMapAll = o_processAdjustHistoryBO.findProcessAdjustHistoryByAssessAllMap();
                //上一区间值
                double parentRiskLevel = o_processureOperBO.getProcessParentRiskLevel(processAdjustHistoryListAll, value, processAdjustHistory.getAdjustTime().toString());
                if(parentRiskLevel != -1){
                    if(riskScoreValue > parentRiskLevel){
                        //up：向上；
                        processAdjustHistory.setEtrend("up");
                    }else if(riskScoreValue < parentRiskLevel){
                        //down：向下；
                        processAdjustHistory.setEtrend("down");
                    }else if(riskScoreValue == parentRiskLevel){
                        //flat：水平
                        processAdjustHistory.setEtrend("flat");
                    }
                }
                //修改下一区间值
                o_processureOperBO.processNextRiskLevel(processAdjustHistoryListAll, value, processAdjustHistory.getAdjustTime().toString(), processAdjustHistoryByIdMapAll, riskScoreValue);
                
                o_processAdjustHistoryDAO.merge(processAdjustHistory);
            }
        }
    }
    
    /**
     * 风险模板面板保存方法
     * 
     * @author 张健
     * @param saveinfo：维度数据，riskId：风险ID，templateid模板ID
     * @return 
     * @date 2013-12-25
     * @since Ver 1.1
     */
    @Transactional
    public void riskAssessMakeSave(String saveinfo,String riskId,String templateid) throws ParseException{
        Map<String,Object> mapValue = new HashMap<String,Object>();//所有分值
        String[] saveinfos = null;
        saveinfos = saveinfo.split("\\|");//每个维度打分情况
        for(String info : saveinfos){
            String[] infos  = info.split("\\*");
            mapValue.put(infos[0].split(",")[0],infos[1]);
        }
        double riskScoreValue = 0;
        Set<String> setValue = null;
        riskScoreValue = this.riskAssessHisStatus(saveinfos,templateid,mapValue);
        setValue = this.getRiskAssessHisStatusSet(saveinfos,templateid,mapValue);
        
        String companyId = UserContext.getUser().getCompanyid();
        HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
        String riskIcon = "";
        riskIcon = this.getRiskIcon(riskScoreValue);
        //保存风险历史记录
        ArrayList<String> idsArrayList = new ArrayList<String>();
        idsArrayList.add(riskId);
        o_riskAdjustHistoryBO.updateRiskAdjustHistory(idsArrayList);//使所有风险的历史记录，最终状态改为0
        RiskAdjustHistory riskAdjustHistory = new RiskAdjustHistory();
        riskAdjustHistory.setId(Identities.uuid());
        riskAdjustHistory.setRisk(this.findRiskById(riskId));
        riskAdjustHistory.setStatus((double)(Math.round(riskScoreValue*100)/100.0));
        riskAdjustHistory.setAdjustType("1");//手动输入
        riskAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
        riskAdjustHistory.setIsLatest("1");
        Template template = new Template();
        template.setId(templateid);
        riskAdjustHistory.setTemplate(template);
        SysOrganization sysOrganization = new SysOrganization();
        sysOrganization.setId(UserContext.getUser().getCompanyid());//公司ID
        riskAdjustHistory.setCompany(sysOrganization);
        riskAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
        riskAdjustHistory.setAssessementStatus(riskIcon);
        if(null!=o_formulaSetBO.findFormulaSet(companyId)){
            riskAdjustHistory.setCalculateFormula(o_formulaSetBO.findFormulaSet(companyId).getRiskLevelFormulaText());
        }
        //趋势计算
        List<RiskAdjustHistory> riskAdjustHistoryListAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllList(riskId);
        HashMap<String, RiskAdjustHistory> riskAdjustHistoryListMapAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllMap();
        //上一区间值
        double parentRiskLevel = o_riskRbsOperBO.getRiskParentRiskLevel(riskAdjustHistoryListAll, riskId, riskAdjustHistory.getAdjustTime().toString());
        if(parentRiskLevel != -1){
            if(riskScoreValue > parentRiskLevel){
                //up：向上；
                riskAdjustHistory.setEtrend("up");
            }else if(riskScoreValue < parentRiskLevel){
                //down：向下；
                riskAdjustHistory.setEtrend("down");
            }else if(riskScoreValue == parentRiskLevel){
                //flat：水平
                riskAdjustHistory.setEtrend("flat");
            }
        }
        //修改下一区间值
        o_riskRbsOperBO.riskNextRiskLevel(riskAdjustHistoryListAll, riskId, riskAdjustHistory.getAdjustTime().toString(), riskAdjustHistoryListMapAll, riskScoreValue);
        o_riskAdjustHistoryDAO.merge(riskAdjustHistory);
        //保存维度分值记录
        for(String info : setValue){
            String[] infos  = info.split(",");
            String scoreDimId = infos[0];
            AdjustHistoryResult adjustHistoryResult = new AdjustHistoryResult();
            adjustHistoryResult.setId(Identities.uuid());
            adjustHistoryResult.setAdjustHistoryId(riskAdjustHistory.getId());
            Dimension dimension = new Dimension();
            dimension.setId(scoreDimId);
            adjustHistoryResult.setDimension(dimension);
            adjustHistoryResult.setScore(infos[1]);
            o_adjustHistoryResultDAO.merge(adjustHistoryResult);
        }
    }
    
    @Transactional // TODO 2 JAVA
    public void riskAssessMakeSaveForSf2(String saveinfo,String riskId) throws ParseException{
    	o_adjustHistoryResultBO.deleteRiskDimensionValuesByRiskId(riskId);
    	JSONArray ja = JSONArray.fromObject(saveinfo);
    	o_adjustHistoryResultBO.insertIntoRiskDimensionValues(ja, riskId);
    }
    
    
    /**
     * 评估模板历史记录重新打分的修改方法
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-10-12
     * @since Ver 1.1
     */
    @Transactional
    public void riskAssessMerge(Set<String> setValue,String value,String templateid,String type,double riskScoreValue,String hisid) throws ParseException{
    	String companyId = UserContext.getUser().getCompanyid();
    	String alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
        HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
        DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), riskScoreValue);
        List<RiskAdjustHistory> riskAdjustHistoryListAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllList(value);
        String riskIcon = "";
        if(dict != null){
            riskIcon = dict.getValue();//风险灯
        }
        if("risk".equals(type) || "riskevent".equals(type) || "formula".equals(type)){
            //修改风险历史记录,更改状态
            RiskAdjustHistory riskAdjustHistory = o_riskAdjustHistoryDAO.get(hisid);
            riskAdjustHistory.setStatus((double)(Math.round(riskScoreValue*100)/100.0));
            riskAdjustHistory.setAssessementStatus(riskIcon);
            riskAdjustHistory.setCalculateFormula(o_formulaSetBO.findFormulaSet(companyId).getRiskLevelFormulaText());
            if(!"formula".equals(type)){
                Template template = new Template();
                template.setId(templateid);
                riskAdjustHistory.setTemplate(template);
            }
            //上一区间值
            double parentRiskLevel = o_riskRbsOperBO.getRiskParentRiskLevel(riskAdjustHistoryListAll, value, riskAdjustHistory.getAdjustTime().toString());
            if(parentRiskLevel != -1){
                if(riskScoreValue > parentRiskLevel){
                    //up：向上；
                    riskAdjustHistory.setEtrend("up");
                }else if(riskScoreValue < parentRiskLevel){
                    //down：向下；
                    riskAdjustHistory.setEtrend("down");
                }else if(riskScoreValue == parentRiskLevel){
                    //flat：水平
                    riskAdjustHistory.setEtrend("flat");
                }
            }
            if("0".equals(riskAdjustHistory.getAdjustType())){
                //评估计划，修改临时维度表中的值，修改结果表中的值
                o_riskAdjustHistoryBO.mergeRiskAdjustHistory(riskAdjustHistory);
                Map<String,StatisticsResult> mapSta = this.getStatisticsMapByHis(value, riskAdjustHistory.getRiskAssessPlan().getId());
                Map<String,AdjustHistoryResult> mapHis = this.getAdjustAllMapByHis(hisid,true,riskAdjustHistory.getRiskAssessPlan().getId());
                for(String info : setValue){
                    String[] infos  = info.split(",");
                    String scoreDimId = infos[0];
                    StatisticsResult statisticsResult = mapSta.get(scoreDimId);
                    if(null != statisticsResult){
                        statisticsResult.setScore(infos[1]);
                        o_statisticsResultDAO.merge(statisticsResult);
                    }
                    AdjustHistoryResult adjustHistoryResult = mapHis.get(scoreDimId);
                    if(null != adjustHistoryResult){
                        adjustHistoryResult.setScore(infos[1]);
                        o_adjustHistoryResultDAO.merge(adjustHistoryResult);
                    }
                }
            }else if("1".equals(riskAdjustHistory.getAdjustType())){
                //手动输入
                o_riskAdjustHistoryDAO.merge(riskAdjustHistory);
                Map<String,AdjustHistoryResult> mapHis = this.getAdjustAllMapByHis(hisid,false,null);
                //保存维度分值记录
                for(String info : setValue){
                    String[] infos  = info.split(",");
                    String scoreDimId = infos[0];
                    AdjustHistoryResult adjustHistoryResult = mapHis.get(scoreDimId);
                    if(null != adjustHistoryResult){
                        adjustHistoryResult.setScore(infos[1]);
                        o_adjustHistoryResultBO.mergeAdjustHistoryResult(adjustHistoryResult);
                    }
                }
                
            }
            HashMap<String, RiskAdjustHistory> riskAdjustHistoryListMapAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllMap();
            RiskAdjustHistory riskAdjustHistoryNext = o_riskRbsOperBO.riskNextRiskLevel(riskAdjustHistoryListAll, value, riskAdjustHistory.getAdjustTime().toString(), riskAdjustHistoryListMapAll, riskScoreValue);
            if(riskAdjustHistoryNext != null){
                o_riskAdjustHistoryDAO.merge(riskAdjustHistoryNext);
            }
        }else{
            if("org".equals(type)){
                OrgAdjustHistory orgAdjustHistory = o_orgAdjustHistoryDAO.get(hisid);
                orgAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                orgAdjustHistory.setAssessementStatus(riskIcon);
                orgAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                HashMap<String, OrgAdjustHistory> groupAdjustTimeMapAll = o_orgAdjustHistoryBO.findGroupAdjustTime(UserContext.getUser().getCompanyid());
                if(groupAdjustTimeMapAll.get(value) != null){
                    if(riskScoreValue > groupAdjustTimeMapAll.get(value).getRiskStatus()){
                        //up：向上；
                        orgAdjustHistory.setEtrend("up");
                    }else if(riskScoreValue < groupAdjustTimeMapAll.get(value).getRiskStatus()){
                        //down：向下；
                        orgAdjustHistory.setEtrend("down");
                    }else if(riskScoreValue == groupAdjustTimeMapAll.get(value).getRiskStatus()){
                        //flat：水平
                        orgAdjustHistory.setEtrend("flat");
                    }
                }
                o_orgAdjustHistoryDAO.merge(orgAdjustHistory);
            }else if("strategy".equals(type) || "sm".equals(type)){
                String[] ids = value.split("_");
                if(ids.length>1){
                    KpiAdjustHistory kpiAdjustHistory = o_kpiAdjustHistoryDAO.get(hisid);
                    kpiAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                    kpiAdjustHistory.setAssessementStatus(riskIcon);
                    kpiAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                    HashMap<String, KpiAdjustHistory> groupAdjustTimeMapAll = o_kpiAdjustHistoryBO.findGroupAdjustTime(UserContext.getUser().getCompanyid());
                    if(groupAdjustTimeMapAll.get(ids[1]) != null){
                        if(riskScoreValue > groupAdjustTimeMapAll.get(ids[1]).getRiskStatus()){
                            //up：向上；
                            kpiAdjustHistory.setEtrend("up");
                        }else if(riskScoreValue < groupAdjustTimeMapAll.get(ids[1]).getRiskStatus()){
                            //down：向下；
                            kpiAdjustHistory.setEtrend("down");
                        }else if(riskScoreValue == groupAdjustTimeMapAll.get(ids[1]).getRiskStatus()){
                            //flat：水平
                            kpiAdjustHistory.setEtrend("flat");
                        }
                    }
                    o_kpiAdjustHistoryDAO.merge(kpiAdjustHistory);
                }else{
                    StrategyAdjustHistory strategyAdjustHistory = o_strategyAdjustHistoryDAO.get(hisid);
                    strategyAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                    strategyAdjustHistory.setAssessementStatus(riskIcon);
                    strategyAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                    HashMap<String, StrategyAdjustHistory> groupAdjustTimeMapAll = o_strategyAdjustHistoryBO.findGroupAdjustTime(UserContext.getUser().getCompanyid());
                    if(groupAdjustTimeMapAll.get(ids[0]) != null){
                        if(riskScoreValue > groupAdjustTimeMapAll.get(ids[0]).getRiskStatus()){
                            //up：向上；
                            strategyAdjustHistory.setEtrend("up");
                        }else if(riskScoreValue < groupAdjustTimeMapAll.get(value).getRiskStatus()){
                            //down：向下；
                            strategyAdjustHistory.setEtrend("down");
                        }else if(riskScoreValue == groupAdjustTimeMapAll.get(value).getRiskStatus()){
                            //flat：水平
                            strategyAdjustHistory.setEtrend("flat");
                        }
                    }
                    o_strategyAdjustHistoryDAO.merge(strategyAdjustHistory);
                }
            }else if("process".equals(type)){
                ProcessAdjustHistory processAdjustHistory = o_processAdjustHistoryDAO.get(hisid);
                processAdjustHistory.setRiskStatus((double)(Math.round(riskScoreValue*100)/100.0));
                processAdjustHistory.setAssessementStatus(riskIcon);
                processAdjustHistory.setCalculateFormula(o_dicBo.findDictEntryById(o_formulaSetBO.findFormulaSet(companyId).getDeptRiskFormula()).getName());
                HashMap<String, ProcessAdjustHistory> groupAdjustTimeMapAll = o_processAdjustHistoryBO.findGroupAdjustTime(UserContext.getUser().getCompanyid());
                if(groupAdjustTimeMapAll.get(value) != null){
                    if(riskScoreValue > groupAdjustTimeMapAll.get(value).getRiskStatus()){
                        //up：向上；
                        processAdjustHistory.setEtrend("up");
                    }else if(riskScoreValue < groupAdjustTimeMapAll.get(value).getRiskStatus()){
                        //down：向下；
                        processAdjustHistory.setEtrend("down");
                    }else if(riskScoreValue == groupAdjustTimeMapAll.get(value).getRiskStatus()){
                        //flat：水平
                        processAdjustHistory.setEtrend("flat");
                    }
                }
                o_processAdjustHistoryDAO.merge(processAdjustHistory);
            }
        }
    }
    
    /**
     * 查询风险历史记录中所有的评分信息
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-10-12
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    private Map<String,AdjustHistoryResult> getAdjustAllMapByHis(String hisid,Boolean isPlan,String assessPlanId){
        Map<String, AdjustHistoryResult> map = new HashMap<String, AdjustHistoryResult>();
        if(StringUtils.isNotBlank(hisid)){
            Map<String,String> mapParams = new HashMap<String,String>();
            String sql = "select ID,SCORE_DIM_ID from t_rm_adjust_history_result where adjust_history_id = :hisid ";
            mapParams.put("hisid", hisid);
            if(isPlan){
                sql = sql + " and assess_plan_id = :assessPlanId ";
                mapParams.put("assessPlanId", assessPlanId);
            }else{
                sql = sql + " and assess_plan_id is null ";
            }
            List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
            for(Object[] obs : list){
                map.put(obs[1].toString(), o_adjustHistoryResultDAO.get(obs[0].toString()));
            }
        }
        return map;
    }
    
    /**
     * 根据历史记录，查询评分结果
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-12-30
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    private Map<String,StatisticsResult> getStatisticsMapByHis(String riskid,String assessPlanId){
        Map<String, StatisticsResult> map = new HashMap<String, StatisticsResult>();
        if(StringUtils.isNotBlank(riskid)){
            String sql = "select ID,SCORE_DIM_ID from t_rm_statistics_result where score_object_id = ? and assess_plan_id = ? ";
            List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),riskid,assessPlanId).list();
            for(Object[] obs : list){
                map.put(obs[1].toString(), o_statisticsResultDAO.get(obs[0].toString()));
            }
        }
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
    @Transactional
    @SuppressWarnings("unchecked")
    public void riskAssessDelete(String hisid,String type,String value){
        if("risk".equals(type) || "riskevent".equals(type)){
            o_riskAdjustHistoryDAO.createSQLQuery(" delete from t_rm_adjust_history_result " +
                    "where adjust_history_id= ? ",hisid).executeUpdate();//删除评分记录
            //恢复最近时间的一条为最新
            RiskAdjustHistory riskAdjustHistory = o_riskAdjustHistoryDAO.get(hisid);
            if("1".equals(riskAdjustHistory.getIsLatest())){
                //删除最新的
                StringBuffer sql = new StringBuffer();
                sql.append(" select id from t_rm_risk_adjust_history where risk_id = ? and id != '"+riskAdjustHistory.getId()+"'  order by adjust_time desc ");
                SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString(),value);
                List<Object> list = sqlQuery.list();
                if(list.size()>0){
                    Object obs = list.get(0);
                    RiskAdjustHistory rah = o_riskAdjustHistoryDAO.get(obs.toString());
                    rah.setIsLatest("1");
                    o_riskAdjustHistoryDAO.merge(rah);
                }
            }
            o_riskAdjustHistoryDAO.delete(hisid);
        }else if("org".equals(type)){
            OrgAdjustHistory orgAdjustHistory = o_orgAdjustHistoryDAO.get(hisid);
            if("1".equals(orgAdjustHistory.getIsLatest())){
                StringBuffer sql = new StringBuffer();
                sql.append(" select id from t_rm_org_adjust_history where org_id = ? and id != '"+orgAdjustHistory.getId()+"'  order by adjust_time desc ");
                SQLQuery sqlQuery = o_orgAdjustHistoryDAO.createSQLQuery(sql.toString(),value);
                List<Object> list = sqlQuery.list();
                if(list.size()>0){
                    Object obs = list.get(0);
                    OrgAdjustHistory oah = o_orgAdjustHistoryDAO.get(obs.toString());
                    oah.setIsLatest("1");
                    o_orgAdjustHistoryDAO.merge(oah);
                }
            }
            o_orgAdjustHistoryDAO.delete(hisid);
        }else if("strategy".equals(type) || "sm".equals(type)){
            String[] ids = value.split("_");
            if(ids.length>1){
                KpiAdjustHistory kpiAdjustHistory = o_kpiAdjustHistoryDAO.get(hisid);
                if("1".equals(kpiAdjustHistory.getIsLatest())){
                    StringBuffer sql = new StringBuffer();
                    sql.append(" select id from t_rm_kpi_adjust_history where kpi_id = ? and id != '"+kpiAdjustHistory.getId()+"'  order by adjust_time desc ");
                    SQLQuery sqlQuery = o_kpiAdjustHistoryDAO.createSQLQuery(sql.toString(),ids[1]);
                    List<Object> list = sqlQuery.list();
                    if(list.size()>0){
                        Object obs = list.get(0);
                        KpiAdjustHistory kah = o_kpiAdjustHistoryDAO.get(obs.toString());
                        kah.setIsLatest("1");
                        o_kpiAdjustHistoryDAO.merge(kah);
                    }
                }
                o_kpiAdjustHistoryDAO.delete(hisid);
            }else{
                StrategyAdjustHistory strategyAdjustHistory = o_strategyAdjustHistoryDAO.get(hisid);
                if("1".equals(strategyAdjustHistory.getIsLatest())){
                    StringBuffer sql = new StringBuffer();
                    sql.append(" select id from t_rm_strategy_adjust_history where strategy_id = ? and  id != '"+strategyAdjustHistory.getId()+"'  order by adjust_time desc ");
                    SQLQuery sqlQuery = o_strategyAdjustHistoryDAO.createSQLQuery(sql.toString(),ids[0]);
                    List<Object> list = sqlQuery.list();
                    if(list.size()>0){
                        Object obs = list.get(0);
                        StrategyAdjustHistory sah = o_strategyAdjustHistoryDAO.get(obs.toString());
                        sah.setIsLatest("1");
                        o_strategyAdjustHistoryDAO.merge(sah);
                    }
                }
                o_strategyAdjustHistoryDAO.delete(hisid);
            }
        }else if("process".equals(type)){
            ProcessAdjustHistory processAdjustHistory = o_processAdjustHistoryDAO.get(hisid);
            if("1".equals(processAdjustHistory.getIsLatest())){
                StringBuffer sql = new StringBuffer();
                sql.append(" select id from t_rm_processure_adjust_history where processure_id = ? and  id != '"+processAdjustHistory.getId()+"'  order by adjust_time desc ");
                SQLQuery sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql.toString(),value);
                List<Object> list = sqlQuery.list();
                if(list.size()>0){
                    Object obs = list.get(0);
                    ProcessAdjustHistory pah = o_processAdjustHistoryDAO.get(obs.toString());
                    pah.setIsLatest("1");
                    o_processAdjustHistoryDAO.merge(pah);
                }
            }
            o_processAdjustHistoryDAO.delete(hisid);
        }
    }
    
    /**
     * 查询风险事件列表
     * @param id 部门id,root,表示根节点
     * @param type all全部风险列表，high重大风险列表，attention关注风险列表，safe安全风险列表
     * @param query 风险名称的搜索值
     * @param start null表示不需要分页
     * @param limit
     * @param sort  null表示不需要排序
     * @param dir
     * @return
     */
    @SuppressWarnings("unchecked")
	public Page<Map> findRiskEventByOrgId(String companyId,String id,String type,String query,String sort,String dir,Integer start,Integer limit){
    	Page<Map> page = new Page<Map>();
    	Map<String,String> mapParams = new HashMap<String,String>();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct r.id,r.risk_code,r.risk_name,r.id_seq,r.last_modify_time,dict.dict_entry_value etrend,h.assessement_status,r.parent_id")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_org riskorg on r.id = riskorg.risk_id")
		.append(" LEFT JOIN t_sys_organization org on org.id=riskorg.org_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId ")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'");
		mapParams.put("companyId", companyId);
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				
			}else{
				sql.append(" and org.id_seq like :id ");
				mapParams.put("id", "%."+id+".%");
			}
		}
		
		//类型过滤
		if(type.equals("high")){
			sql.append(" and h.assessement_status='" + Contents.RISK_LEVEL_HIGH + "'");
		}else if(type.equals("attention")){
			sql.append(" and h.assessement_status='" + Contents.RISK_LEVEL_MIDDLE + "'");
		}else if(type.equals("safe")){
			sql.append(" and h.assessement_status='" + Contents.RISK_LEVEL_LOW + "'");
		}else{	
		}
		
		//搜索过滤
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like :query ");
			mapParams.put("query", "%"+query+"%");
		}
		
		//排序
		if(sort==null){
			sql.append(" order by r.parent_id,r.last_modify_time desc");
		}else if(sort.equals("etrend")){
			sql.append(" order by h.etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" order by h.assessement_status " + dir);
		}else{}
		
		//执行
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
		
		//分页
		if(start!=null){
	  		sqlQuery.setFirstResult(start);
	  		sqlQuery.setMaxResults(limit);
		}
  		List<Object[]> list = sqlQuery.list();
  		
		//得到id数组
		String[] ids = new String[list.size()];
  		Set<String> idSet = new HashSet<String>();
  		for(int i=0;i<list.size();i++){
  			Object[] o = (Object[])list.get(i);
  			ids[i] = o[0].toString();
  			//把上级节点id都存起来，去掉重复的
  			if(o[3]!=null){
  				String[] idsTemp = o[3].toString().toString().split("\\.");
  				idSet.addAll(Arrays.asList(idsTemp));
  			}
  		}
  		Object[] idSeqArr = idSet.toArray();
  		String[] idSeqs = new String[idSeqArr.length];
  		for(int i=0;i<idSeqArr.length;i++){
  			idSeqs[i] = idSeqArr[i].toString();
  		}
  	    //1.获取风险事件的责任部门名称：‘内控部，生成部’
  		Map<String,Object> orgMap = this.findOrgNameByRiskIds(ids);
  		//2.获取风险的所有上级节点
  		Map<String,Object> riskMap = this.findParentRiskNameByRiskIds(idSeqs);
  		
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			String eventId = o[0].toString();
  			m.put("id", eventId);
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3]==null?"":o[3].toString().toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			
  		    //风险的责任部门
			Object respDeptName = ((Map)orgMap.get("respDeptMap")).get(eventId);
			if(respDeptName==null){
				m.put("respDeptName","");
			}else{
				m.put("respDeptName",respDeptName.toString());
			}
			//风险的相关部门
			Object relaDeptName = ((Map)orgMap.get("relaDeptMap")).get(eventId);
			if(relaDeptName==null){
				m.put("relaDeptName","");
			}else{
				m.put("relaDeptName",relaDeptName.toString());
			}
			//所属风险	获取风险事件的所以上级风险名称：‘战略风险>战略管理风险’
			StringBuffer belongRisk = new StringBuffer();
			if(!m.get("idSeq").equals("")){
				String idSeqStr = m.get("idSeq").toString().replace(".", ",");
				String[] idSeq = idSeqStr.split(",");
				for(int i=0;i<idSeq.length;i++){
					if(!idSeq[i].equals("") && !idSeq[i].equals(eventId)){
						belongRisk.append(((Risk)riskMap.get(idSeq[i])).getName());
						belongRisk.append(">");
					}
				}
				if(belongRisk.length() != 0){
				    belongRisk.append(belongRisk.substring(0, belongRisk.length()-1));
				}
			}
			m.put("belongRisk",belongRisk);
			//上级风险
			int pos = belongRisk.lastIndexOf(">")+1;
			if(pos!=-1){
				m.put("parentName",belongRisk.substring(pos));
			}else{
				m.put("parentName","");
			}
			m.put("parentId",m.get("parentId").toString());
			//是否是最新风险事件.规则:最近30天内的都是最新事件
			if(m.get("lastModifyTime") != null){
				Long t = ((Date)m.get("lastModifyTime")).getTime();
				Long curentTime = new Date().getTime();
				Long diff = 30*24*60*60*1000l;
				if((curentTime - t)<diff){
					m.put("isNew",true);
				}else{
					m.put("isNew",false);
				}
			}else{
				m.put("isNew",false);
			}
			
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		
        return page;
	}
    
    /**
	 * 查询公司下全部风险和风险事件
	*/
	@SuppressWarnings("unchecked")
	public List<Risk> findRiskAndEventByCompany(String companyId) {
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		List<Risk> risks = criteria.list();
		return risks;
	}
	
	/**
	 * 部门风险管理员查询本部门带审批的风险事件
	 * @author zhengjunxiang 2013-11-19
	 * @param archiveState
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventForOrgManager(String companyId,String deptId,Page<Map> page,String sort, String dir,String query){
		StringBuffer sql = new StringBuffer();
		Map<String,String> mapParams = new HashMap<String,String>();
		sql.append("SELECT r.id,r.risk_code as code,r.risk_name as name,r.id_seq as idSeq,r.last_modify_time as lastModifyTime" +
				   ",dict.dict_entry_value etrend,h.assessement_status as assessementStatus,r.parent_id")
		.append(" FROM t_rm_risks r")
		.append(" INNER JOIN t_rm_risk_org riskorg on riskorg.risk_id=r.id and riskorg.etype='M' and riskorg.org_id='"+deptId+"'")	//责任部门
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId ")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'")
		.append(" and r.archive_status='"+Contents.RISK_STATUS_WAITINGAPPROVE+"'");
		mapParams.put("companyId", companyId);
		//过滤
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like :query ");
			mapParams.put("query", "%"+query+"%");
		}
		
		if(sort==null){
			sql.append(" order by parent_id,lastModifyTime desc");
		}else if(sort.equals("etrend")){
			sql.append(" order by etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" order by assessementStatus " + dir);
		}else{}
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
		
        //分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		
  		//封装对象
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		
        return page;
	}
	
	/**
	 * 公司风险管理员查询本公司带归档的风险事件
	 * @author zhengjunxiang 2013-11-19
	 * @param archiveState
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> findRiskEventForCompanyManager(String companyId,Page<Map> page,String sort, String dir,String query){
		StringBuffer sql = new StringBuffer();
		Map<String,String> mapParams = new HashMap<String,String>();
		sql.append("SELECT r.id,r.risk_code as code,r.risk_name as name,r.id_seq as idSeq,r.last_modify_time as lastModifyTime" +
				   ",dict.dict_entry_value etrend,h.assessement_status as assessementStatus,r.parent_id")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId ")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'")
		.append(" and r.archive_status='"+Contents.RISK_STATUS_WAITINGARCHIVE+"'");
		mapParams.put("companyId", companyId);
		//过滤
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like :query ");
			mapParams.put("query", "%"+query+"%");
		}
		
		if(sort==null){
			sql.append(" order by parent_id,lastModifyTime desc");
		}else if(sort.equals("etrend")){
			sql.append(" order by etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" order by assessementStatus " + dir);
		}else{}
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
		
        //分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		
  		//封装对象
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		
        return page;
	}
	
	/**
	 * 查询本部门下（包括子部门）的风险事件，包括所有状态
	 * @param id
	 * @param page
	 * @param sort
	 * @param dir
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> findAllRiskEventByOrgId(String id,Page<Map> page,String sort, String dir,String query){
		String companyId = UserContext.getUser().getCompanyid();
		Map<String,String> mapParams = new HashMap<String,String>();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct r.id,r.risk_code,r.risk_name,r.id_seq,r.last_modify_time,dict.dict_entry_value etrend,");
		//风险状态排序
		sql.append("case when h.assessement_status is null then");
		if(dir==null){
        	dir="ASC";
        }
		if(dir.equals("ASC")){
	    	sql.append(" 'nothing'"); 
	    }else{
	    	sql.append(" 'anothing'");
	    }
	    sql.append(" else h.assessement_status end assessement_status,");
	    sql.append("r.parent_id,r.archive_status");
		sql.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risk_org riskorg on r.id = riskorg.risk_id")
		.append(" LEFT JOIN t_sys_organization org on org.id=riskorg.org_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
		.append(" WHERE r.company_id=:companyId ")
		.append(" and r.is_risk_class='re'")
		.append(" and r.delete_estatus='1'");
		mapParams.put("companyId", companyId);
		if(id != null){
			if(id.equalsIgnoreCase("root")){
				
			}else{
				sql.append(" and org.id_seq like :id ");
				mapParams.put("id", "%."+id+".%");
			}
		}
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like '%" + query + "%'");
			mapParams.put("query", "%"+query+"%");
		}
		
		//排序
		if(sort==null){
			sql.append(" order by assessement_status asc");//r.parent_id,r.last_modify_time desc
		}else if(sort.equals("etrend")){
			sql.append(" order by h.etrend " + dir);
		}else if(sort.equals("assessementStatus")){
			sql.append(" order by assessement_status " + dir);
		}else{}
				
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
		int size = sqlQuery.list().size();
 
		//分页实现
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
  		List<Object[]> list = sqlQuery.list();
  		List<Map> objList = new ArrayList<Map>();
  		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("code", o[1]==null?"":o[1].toString());
  			m.put("name", o[2].toString());
  			m.put("idSeq", o[3].toString());
  			m.put("lastModifyTime", (Date)o[4]);
  			m.put("etrend", o[5]==null?"":o[5].toString());
  			m.put("assessementStatus", o[6]==null?"":o[6].toString());
  			m.put("parentId", o[7]==null?"":o[7].toString());
  			m.put("archiveStatus", o[8]==null?"":o[8].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
		
        return page;
	}
	/**
     * 查询本人的风险事件，包括所有状态
     * @param id
     * @param page
     * @param sort
     * @param dir
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    public Page<Map> findAllRiskEventByMyFolder(String id,Page<Map> page,String sort, String dir,String query){
        Map<String,String> mapParams = new HashMap<String,String>();
        String companyId = UserContext.getUser().getCompanyid();
        StringBuffer sql = new StringBuffer();
        sql.append("select distinct r.id,r.risk_code,r.risk_name,r.id_seq,r.last_modify_time,dict.dict_entry_value etrend,");
        //风险状态排序
		sql.append("case when h.assessement_status is null then");
		if(dir==null){
        	dir="ASC";
        }
		if(dir.equals("ASC")){
	    	sql.append(" 'nothing'"); 
	    }else{
	    	sql.append(" 'anothing'");
	    }
	    sql.append(" else h.assessement_status end assessement_status,");
	    sql.append("r.parent_id,r.archive_status");
        sql.append(" FROM t_rm_risks r")
        .append(" LEFT JOIN t_rm_risk_org riskorg on r.id = riskorg.risk_id")
        .append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
        .append(" LEFT JOIN t_sys_dict_entry dict on h.etrend = dict.id")
        .append(" WHERE r.company_id=:companyId ")
        .append(" and r.is_risk_class='re'")
        .append(" and r.delete_estatus='1'");
        mapParams.put("companyId", companyId);
        if(id != null){
            if(id.equalsIgnoreCase("root")){
                
            }else{
                sql.append(" and riskorg.EMP_ID = :id ");
                mapParams.put("id", id);
            }
        }
        if (StringUtils.isNotBlank(query)) {
            sql.append(" and r.risk_name like :query ");
            mapParams.put("query", "%"+query+"%");
        }
        
        //排序
        if(sort==null){
            sql.append(" order by assessement_status asc");//r.parent_id,r.last_modify_time desc
        }else if(sort.equals("etrend")){
            sql.append(" order by h.etrend " + dir);
        }else if(sort.equals("assessementStatus")){
            sql.append(" order by assessement_status " + dir);
        }else{}
                
        SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
        int size = sqlQuery.list().size();
 
        //分页实现
        int start = (page.getPageNo()-1)*page.getPageSize();
        int limit = page.getPageSize();
        sqlQuery.setFirstResult(start);
        sqlQuery.setMaxResults(limit);
        List<Object[]> list = sqlQuery.list();
        List<Map> objList = new ArrayList<Map>();
        for(Object[] o : list){
            Map m = new HashMap();
            m.put("id", o[0].toString());
            m.put("code", o[1]==null?"":o[1].toString());
            m.put("name", o[2].toString());
            m.put("idSeq", o[3].toString());
            m.put("lastModifyTime", (Date)o[4]);
            m.put("etrend", o[5]==null?"":o[5].toString());
            m.put("assessementStatus", o[6]==null?"":o[6].toString());
            m.put("parentId", o[7]==null?"":o[7].toString());
            m.put("archiveStatus", o[8]==null?"":o[8].toString());
            objList.add(m);
        }
        page.setResult(objList);
        page.setTotalItems(size);
        
        return page;
    }
    
    /**
     * 根据风险ID 查询风险指标
     * @param id
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
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> findRiskRelaKpiById(String id,String query,String sort, String year, String quarter,
            String month, String week, String eType, String isNewValue) {
    	List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    	if(StringUtils.isNotBlank(id)) {
    		List<Object[]> list = null;
    		StringBuffer sql = new StringBuffer("select risk_id,kpi_id,eweight from t_kpi_kpi_rela_risk where risk_id = ? and ETYPE = 'RM'");
    		if(StringUtils.isNotBlank(query)) {
    			sql.append(" and kpi_id in (select id from t_kpi_kpi where kpi_name like ").append("'%").append(query).append("%')");
    		}
            list = o_kpiRelaRiskDAO.createSQLQuery(sql.toString(), id).list();
            for(Object[] o : list) {
            	Map<String, Object> map = new HashMap<String,Object>();
            	String riskId  = (String) o[0];
            	String kpiId = (String) o[1];
            	map.put("riskId", riskId);
            	map.put("kpiId", kpiId);
            	map.put("weight", o[2]);           	
            	Kpi kpi = o_kpiDAO.get(kpiId);
            	map.put("kpiName", kpi.getName());
            	map.put("frequency", kpi.getGatherFrequence().getName());
            	map.put("units", kpi.getUnits().getName());
            	String gatherFrequency  = kpi.getGatherFrequence().getId();
            	String timePeriodId = null;
            	if("0frequecy_year".equals(gatherFrequency) && gatherFrequency.equals(eType)) {
            		timePeriodId = year;
            	} else if ("0frequecy_month".equals(gatherFrequency) && gatherFrequency.equals(eType)) {
            		timePeriodId = month;
            	}else if ("0frequecy_quarter".equals(gatherFrequency) && gatherFrequency.equals(eType)) {
            		timePeriodId = month;
            	} else if("0frequecy_week".equals(gatherFrequency) && gatherFrequency.equals(eType)){
            		timePeriodId = week;
            	}
            	KpiGatherResult kgr = o_kpigGatherResultBO.findCurrentKpiGatherResultByKpiId(kpiId, timePeriodId);
            	if(kgr != null) {
            		map.put("kgrId", kgr.getId());
            		if(null != kgr.getAssessmentStatus() ) {
            			map.put("assessmentStatus", kgr.getAssessmentStatus().getValue());
            		} else {
            			map.put("assessmentStatus",null);
            		}   
            		if(null != kgr.getDirection()) {
            			map.put("directionstr", kgr.getDirection().getValue());
            		} else {
            			map.put("directionstr", null);
            		}  
            		List<Object[]> objList = o_kpiMemoBO.findMemoByKgrId(kgr.getId());
            		KpiMemoForm memoForm = new KpiMemoForm(objList);
           		    map.put("isMemo", kgr.getIsMemo());
           		    if(null !=  kgr.getFinishValue()) {
           		       map.put("finishValue",convertValue(kgr.getFinishValue()) + map.get("units"));
           		    }
           		    if(null != kgr.getTargetValue()) {
           		       map.put("targetValue",convertValue(kgr.getTargetValue()) + map.get("units"));	
           		    }
           		    if(null!=kgr.getAssessmentValue()){
           		        
           		        map.put("assessmentValue",convertValue(kgr.getAssessmentValue()));
           		    }
           		    map.put("timePeriod", kgr.getTimePeriod().getId());
           		    map.put("dateRange", kgr.getTimePeriod().getTimePeriodFullName());
           		    map.put("memoStr", memoForm.getMemoStr());
            	}
            	data.add(map);
            }
    	}
    	return data;
    }
    /**
     * 删除选中的风险指标的关联关系
     * @param kpiIds
     * @param riskId
     */
    @Transactional
    public void deleteriskrelakpi(String kpiIds,String riskId) {
    	String sql = "delete from t_kpi_kpi_rela_risk where kpi_id in (:kpiIdArray) and risk_id = :riskId and etype = 'RM'";
    	JSONArray kpiIdArray = JSONArray.fromObject(kpiIds);    	
    	o_kpiRelaRiskDAO.createSQLQuery(sql).setParameterList("kpiIdArray", kpiIdArray, new StringType()).setParameter("riskId", riskId).executeUpdate();
    }
    /**
     * 添加风险指标时保存关联关系
     * @param kpiId
     * @param riskid
     */
    @Transactional
    public void saveSingleKpiRela(String kpiId,String riskid) {
    	String delSql = "delete  from t_kpi_kpi_rela_risk where kpi_id = ? and risk_id = ? and etype = 'RM'";
    	o_kpiRelaRiskDAO.createSQLQuery(delSql,kpiId,riskid).executeUpdate();
    	String sql = "insert into t_kpi_kpi_rela_risk (id,kpi_id,risk_id,etype) values(?,?,?,?)";
    	o_kpiRelaRiskDAO.createSQLQuery(sql,Identities.uuid(),kpiId,riskid,"RM").executeUpdate();
    }
    /**
     * 关联指标到指定风险下
     * @param param riskId/kpiIds
     */
    @Transactional
    public void mergeRiskrelakpi(String param) {
    	JSONObject jsobj = JSONObject.fromObject(param);
    	if(null != jsobj) {
             String riskId = jsobj.getString("riskId");
             JSONArray kpiids = jsobj.getJSONArray("kpiIds");
             if(null != kpiids && kpiids.size() > 0) {
            	 String delSql = "delete  from t_kpi_kpi_rela_risk where kpi_id = ? and risk_id = ? and etype = 'RM'";
            	 String sql = "insert into t_kpi_kpi_rela_risk (id,kpi_id,risk_id,etype) values(?,?,?,?)";
            	
            	 for (Object kpiId : kpiids) {
            		 o_kpiRelaRiskDAO.createSQLQuery(delSql,(String) kpiId,riskId).executeUpdate();
            		 o_kpiRelaRiskDAO.createSQLQuery(sql,Identities.uuid(),(String) kpiId,riskId,"RM").executeUpdate();
            	 }
            	
             }
    	}
    }
    
    @Transactional
    public void deleteRiskrelakpi(String param) {
        JSONObject jsobj = JSONObject.fromObject(param);
        if(null != jsobj) {
             String riskId = jsobj.getString("riskId");
             JSONArray kpiids = jsobj.getJSONArray("kpiIds");
             if(null != kpiids && kpiids.size() > 0) {
                 String delSql = "delete  from t_kpi_kpi_rela_risk where kpi_id = ? and risk_id = ? and etype = 'RM'";
                
                 for (Object kpiId : kpiids) {
                     o_kpiRelaRiskDAO.createSQLQuery(delSql,(String) kpiId,riskId).executeUpdate();
                 }
                
             }
        }
    }
    //添加/修改 保存风险库风险应对
	@Transactional
	public void mergeEditIdea(RiskResponse riskResponse){

		o_riskResponseDao.merge(riskResponse);
	}	
    /**
     * 
     * @param valueObj
     * @return
     */
	private String convertValue(Double valueObj){
		String valueStr = "";
		if(null == valueObj){
			valueStr = "";
    	}else{
    		BigDecimal value = new BigDecimal(valueObj);
        	int valueInt = value.intValue();
        	float valueFloat = value.floatValue();
        	if(valueFloat>valueInt){
        		valueStr = new DecimalFormat("0.00").format(value).toString();//权重
        	}else{
        		valueStr = new DecimalFormat("0").format(value).toString();//权重
        	}
    	}
		return valueStr;
	}
}

