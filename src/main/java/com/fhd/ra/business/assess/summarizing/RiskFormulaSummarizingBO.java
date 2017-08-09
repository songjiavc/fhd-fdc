package com.fhd.ra.business.assess.summarizing;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.comm.business.formula.FormulaLogBO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.assess.riskTidy.AdjustHistoryResult;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.StrategyAdjustHistory;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.approval.RiskTidyBO;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.business.assess.oper.KpiAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.OrgAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.StatisticsResultBO;
import com.fhd.ra.business.assess.oper.StrategyAdjustHistoryBO;
import com.fhd.ra.business.assess.quaassess.ShowAssessBO;
import com.fhd.ra.business.assess.summarizing.util.KpiOperBO;
import com.fhd.ra.business.assess.summarizing.util.OrgOperBO;
import com.fhd.ra.business.assess.summarizing.util.ProcessureOperBO;
import com.fhd.ra.business.assess.summarizing.util.RiskRbsOperBO;
import com.fhd.ra.business.assess.summarizing.util.StrategyMapOperBO;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.ProcessAdjustHistoryBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.sm.web.controller.util.Utils;
import com.fhd.sys.business.assess.WeightSetBO;

/**
 * 风险分类、指标、目标、部门、流程控制业务
 * */

@Service
public class RiskFormulaSummarizingBO {
	
	@Autowired
	private RiskRbsOperBO o_riskRbsOperBO;
	
	@Autowired
	private ProcessureOperBO o_processureOperBO;
	
	@Autowired
	private ShowAssessBO o_showAssessBO;
	
	@Autowired
	private KpiOperBO o_kpiOperBO;
	
	@Autowired
	private OrgOperBO o_orgOperBO;
	
	@Autowired
	private StrategyMapOperBO o_strategyMapOperBO;
	
	@Autowired
	private RiskRbsOperBO o_riskRbsOrReOperBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	@Autowired
	private RiskTidyBO o_riskTidyBO;
	
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private AdjustHistoryResultBO o_adjustHistoryResultBO;
	
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	@Autowired
	private FormulaSetBO o_formularSetBO;
	
	@Autowired
	private KpiAdjustHistoryBO o_kpiAdjustHistoryBO;
	
	@Autowired
	private OrgAdjustHistoryBO o_orgAdjustHistoryBO;
	
	@Autowired
	private StrategyAdjustHistoryBO o_strategyAdjustHistoryBO;
	
	@Autowired
	private ProcessAdjustHistoryBO o_processAdjustHistoryBO;
	
	@Autowired
	private RAssessPlanBO o_rAssessPlanBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	@Autowired
	private JBPMBO o_jbpmBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private FormulaLogBO o_formulaLogBO;
	
	/** 
	  * @Description: 打分汇总核心内容
	  * @author jia.song@pcitc.com
	  * @date 2017年4月17日 下午4:35:27 
	  * @param assessPlanId
	  * @param isAfreshSummarizing 
	  */
	private void summarizingOperSf2(String assessPlanId, boolean isAfreshSummarizing){
		
		//undo 权重表拿到权重对应关系（主责、相关、角色）
		JbpmHistProcinst jbpmHistProcinst = o_jbpmBO.findJbpmHistProcinstByBusinessId(assessPlanId); //得到工作流实体
		String companyId = ""; //公司ID
		try {
			companyId = o_jbpmOperate.getVariable(jbpmHistProcinst.getId_(), "companyId");
		} catch (Exception e) {
			companyId = UserContext.getUser().getCompanyid();
		}
		HashMap<String, Double> weightMap = o_weightSetBO.findWeightSetAllMap(companyId);
		// 获取部门打分对象对应的分数
		// 获取计划对应的所有打分对象以及部门信
		List<Map<String,String>> rtnList = o_statisticsResultBO.findAccessPlayRelaScoreObject(assessPlanId);
		//根据权重表计算风险打分的平均分
		//根据现实打的分数结合各种权重最终计算AdjustHistoryResultBO 
		List<AdjustHistoryResult> averList = getWeightAverage(assessPlanId,rtnList,weightMap);
		o_adjustHistoryResultBO.deleteAdjustHistoryByAssessPlanId(assessPlanId);
		o_adjustHistoryResultBO.saveAdjustHistorySqlSf2(averList);
		//计算风险综合分数
		List<Map<String,Double>> sorceList = this.caluComposite(assessPlanId,averList);
		//将获取的综合得分写入到object表中
		o_scoreObjectBO.updateRiskScoreByRiskId(sorceList);
		//将汇总得分写入object表中
		o_riskTidyBO.execRiskTidy(assessPlanId);
		
	}
	
	/** 
	  * @Description: 通过维度得分计算综合得分
	  * @author jia.song@pcitc.com
	  * @date 2017年4月24日 上午10:15:23 
	  * @param assessPlanId
	  * @param paramMap 
	  */
	private List<Map<String,Double>> caluComposite(String assessPlanId,List<AdjustHistoryResult> paramMap){
	    //获取计算公式
	    String caluFormula = this.findTemplateRelaCalu(assessPlanId);
	    String objectId = null;
	    double riskScore = 0.0;
	    int z = 0;
	    Map<String,String> tempMap = new HashMap<String,String>();
	    List<Map<String,Double>> riskScoreList = new ArrayList<Map<String,Double>>();
	    Map<String,Double> scoreMap = null;
	    for(AdjustHistoryResult adjustHistoryResult : paramMap){
	    	z++;
			if(objectId == null){
			    objectId = adjustHistoryResult.getRiskScoreObject().getId();
			}
			if(!objectId.equals(adjustHistoryResult.getRiskScoreObject().getId())){
			    //map 清空一下
			    scoreMap = new HashMap<String,Double>();
			    //获取表达式
			    riskScore = Utils.eval(Utils.replaceWeightToNum(caluFormula,tempMap));
			    scoreMap.put(objectId, riskScore);
			    riskScoreList.add(scoreMap);
			    objectId = adjustHistoryResult.getRiskScoreObject().getId();
			    tempMap = new HashMap<String,String>();
			}
			tempMap.put(adjustHistoryResult.getDimension().getName(), adjustHistoryResult.getScore());
			//当循环到最后一条时进行处理
			if(z == paramMap.size()){
				scoreMap = new HashMap<String,Double>();
			    //获取表达式
			    riskScore = Utils.eval(Utils.replaceWeightToNum(caluFormula,tempMap));
			    scoreMap.put(objectId, riskScore);
			    riskScoreList.add(scoreMap);
			    continue;
			}
	    }
	    return riskScoreList;
	}
	
	/** 
	  * @Description: 通过评估模板id获取模板所对应的计算公式 
	  * @author jia.song@pcitc.com
	  * @date 2017年4月24日 上午9:10:32 
	  * @param assessPlanId
	  * @return 
	  */
	private String findTemplateRelaCalu(String assessPlanId){
	    
	    return o_statisticsResultBO.queryCaluFormulaByAssessPlanId(assessPlanId);
	}
	
	/** 
	  * @Description: 计算方法内部实现
	  * @author jia.song@pcitc.com
	  * @date 2017年4月18日 下午2:02:58 
	  * @param paramMap
	  * @return 
	  */
	private List<AdjustHistoryResult> getWeightAverage(String assessPlanId,List<Map<String,String>> paramMap,HashMap<String, Double> weightMap){
		List<AdjustHistoryResult> rtnMap = new ArrayList<AdjustHistoryResult>();
		String weightId = null,weightName = null,objectId = null;
		double point = 0.0,pointM = 0.0,pointA = 0.0,orgMRoleDPoint = 0.0,orgMRoleEPoint = 0.0,orgARoleDPoint = 0.0,orgARoleAPoint = 0.0;
		int i=0,j=0,z=0;  //用i来存放同一个维度中主责部门数量，用j存放相关部门数量，维度更换时清空
		int p=0,q=0,t=0,s=0;  //用p存放角色为主管领导的人员数量，用q存放普通员工的人员数量，维度更新时清空
		for(Map<String,String> map : paramMap){
			//给map赋初值
		    z++;
		    if(weightId == null){
		    	weightId = map.get("scoreDimId");
		    	weightName = map.get("scoreDimName");
		    	objectId = map.get("objectId");
		    }
			//如果维度发生改变
			if(!weightId.equals(map.get("scoreDimId"))){
				if(p != 0 && q != 0){
					orgMRoleDPoint = orgMRoleDPoint / q * weightMap.get("DeptLeader")/weightMap.get("totleRoleWeight");
					orgMRoleEPoint = orgMRoleEPoint / p * weightMap.get("Employee")/weightMap.get("totleRoleWeight");
				}else if(p == 0 && q != 0 ){
					orgMRoleDPoint = orgMRoleDPoint / q;
				}else if(p != 0 && q == 0){
					orgMRoleEPoint = orgMRoleEPoint / p;
				}
				if(s != 0 && t != 0){
					orgARoleDPoint = orgARoleDPoint / s * weightMap.get("DeptLeader")/weightMap.get("totleRoleWeight");
					orgARoleAPoint = orgARoleAPoint / t * weightMap.get("Employee")/weightMap.get("totleRoleWeight");
				}else if(s != 0 && t == 0){
					orgARoleDPoint = orgARoleDPoint / s;
				}else if(s == 0 && t != 0){
					orgARoleAPoint = orgARoleAPoint / t;
				}
				if(i != 0 && j != 0){
					pointM = (orgMRoleDPoint + orgMRoleEPoint) * weightMap.get("dutyDeptWeight")/weightMap.get("totleDeptWeight");
					pointA = (orgARoleDPoint + orgARoleAPoint) * weightMap.get("relatedDeptWeight")/weightMap.get("totleDeptWeight");
				}else if(i == 0 && j != 0){
					pointA = orgARoleDPoint + orgARoleAPoint;
				}else if(i != 0 && j == 0){ 
					pointM =  orgMRoleDPoint + orgMRoleEPoint ;
				}
				point = pointM+pointA;
				AdjustHistoryResult adjustHistoryResult = new AdjustHistoryResult();
				adjustHistoryResult.setRiskAssessPlan(new RiskAssessPlan(assessPlanId));
				adjustHistoryResult.setRiskScoreObject(new RiskScoreObject(objectId));
				Dimension dimension = new Dimension();
				
				dimension.setId(weightId);
				dimension.setName(weightName);
				adjustHistoryResult.setDimension(dimension);
				adjustHistoryResult.setScore(Double.toString(point));
				adjustHistoryResult.setCount(Integer.toString(p + q));
				adjustHistoryResult.setStatus("1");
				adjustHistoryResult.setUpdateTime(new Date());
				adjustHistoryResult.setUpdater(Contents.SYSTEM);
				rtnMap.add(adjustHistoryResult);
				pointM = 0;pointA = 0;orgMRoleDPoint = 0;orgMRoleEPoint = 0;orgARoleDPoint = 0;orgARoleAPoint = 0;
				i = 0;j = 0;p = 0;q = 0;point = 0;s=0;t=0;
				weightId = map.get("scoreDimId");
				weightName = map.get("scoreDimName");
				objectId = map.get("objectId");
			}
			
			if("M".equals(map.get("orgType"))){
				//orgMPoint = orgMPoint + Double.parseDouble(map.get("scoreDicValue")) * weightMap.get("dutyDeptWeight")/weightMap.get("totleDeptWeight");
				if("Employee".equals(map.get("roleCode"))){
					orgMRoleEPoint = orgMRoleEPoint + Double.parseDouble(map.get("scoreDicValue"));
					p++;
				}else if("DeptLeader".equals(map.get("roleCode"))){
					orgMRoleDPoint = orgMRoleDPoint + Double.parseDouble(map.get("scoreDicValue")) ;
					q++;
				}
				i++;
			}else{
				if("Employee".equals(map.get("roleCode"))){
					orgARoleAPoint = orgARoleAPoint + Double.parseDouble(map.get("scoreDicValue"));
					t++;
				}else if("DeptLeader".equals(map.get("roleCode"))){
					orgARoleDPoint = orgARoleDPoint + Double.parseDouble(map.get("scoreDicValue"));
					s++;
				}
				j++;
			}
			
			//当循环到最后一条时进行处理
			if(z == paramMap.size()){
				if(p != 0 && q != 0){
					orgMRoleDPoint = orgMRoleDPoint / q * weightMap.get("DeptLeader")/weightMap.get("totleRoleWeight");
					orgMRoleEPoint = orgMRoleEPoint / p * weightMap.get("Employee")/weightMap.get("totleRoleWeight");
				}else if(p == 0 && q != 0 ){
					orgMRoleDPoint = orgMRoleDPoint / q;
				}else if(p != 0 && q == 0){
					orgMRoleEPoint = orgMRoleEPoint / p;
				}
				if(s != 0 && t != 0){
					orgARoleDPoint = orgARoleDPoint / s * weightMap.get("DeptLeader")/weightMap.get("totleRoleWeight");
					orgARoleAPoint = orgARoleAPoint / t * weightMap.get("Employee")/weightMap.get("totleRoleWeight");
				}else if(s != 0 && t == 0){
					orgARoleDPoint = orgARoleDPoint / s;
				}else if(s == 0 && t != 0){
					orgARoleAPoint = orgARoleAPoint / t;
				}
				if(i != 0 && j != 0){
					pointM = (orgMRoleDPoint + orgMRoleEPoint) * weightMap.get("dutyDeptWeight")/weightMap.get("totleDeptWeight");
					pointA = (orgARoleDPoint + orgARoleAPoint) * weightMap.get("relatedDeptWeight")/weightMap.get("totleDeptWeight");
				}else if(i == 0 && j != 0){
					pointA = orgARoleDPoint + orgARoleAPoint;
				}else if(i != 0 && j == 0){ 
					pointM =  orgMRoleDPoint + orgMRoleEPoint;
				}
				point = pointM+pointA;
				AdjustHistoryResult adjustHistoryResult = new AdjustHistoryResult();
				adjustHistoryResult.setRiskAssessPlan(new RiskAssessPlan(assessPlanId));
				adjustHistoryResult.setRiskScoreObject(new RiskScoreObject(map.get("objectId")));
				Dimension dimension = new Dimension();
				dimension.setId(map.get("scoreDimId"));
				dimension.setName(map.get("scoreDimName"));
				adjustHistoryResult.setDimension(dimension);
				adjustHistoryResult.setScore(Double.toString(point));
				adjustHistoryResult.setCount(Integer.toString(i + j));
				adjustHistoryResult.setStatus("1");
				adjustHistoryResult.setUpdateTime(new Date());
				adjustHistoryResult.setUpdater(Contents.SYSTEM);
				rtnMap.add(adjustHistoryResult);
				continue;
			}
		}
		return rtnMap;
	}
	/*
	public static void main(String[] args){
		String aa = "songjia";
		
		List<String> test = new ArrayList<String>();
		
		test.add(aa);
		
		aa = "jiasong";
		
		System.out.println(test.get(0));
		
	}
	*/
	/** 
	  * @Description: list 中存放的是不同角色的一组评估人，将多组评估结果进行汇总
	  * @author jia.song@pcitc.com
	  * @date 2017年4月20日 上午9:23:47 
	  * @param paramList
	  * @return 
	  */
	private Map<String,Double> getRiskScoreAver(List<Map<String,Double>> paramList){
		Map<String,Double> rtnMap = new HashMap<String,Double>();
		double temp = 0,count = 0;
		for(Map<String,Double> param : paramList){
			temp = temp + param.get("score");
			count = count + param.get("count");
		}
		rtnMap.put("score", temp);
		rtnMap.put("count", count);
		return rtnMap;
	}
	
	
//	private 
	
	
	/**
	 * 汇总核心
	 * @param assessPlanId 评估计划ID
	 * @param isAfreshSummarizing 是否重新汇总
	 * @return VOID
	 * @author 金鹏祥
	 * */
	private void summarizingOper(String assessPlanId, boolean isAfreshSummarizing){
		JbpmHistProcinst jbpmHistProcinst = o_jbpmBO.findJbpmHistProcinstByBusinessId(assessPlanId); //得到工作流实体
		String companyId = ""; //公司ID
		try {
			companyId = o_jbpmOperate.getVariable(jbpmHistProcinst.getId_(), "companyId");
		} catch (Exception e) {
			companyId = UserContext.getUser().getCompanyid();
		}
		List<StatisticsResult> statisticsResultList = null; //打分总实体集合
		RiskAssessPlan riskAssessPlan = o_rAssessPlanBO.findRiskAssessPlanById(assessPlanId);
		String dimId = ""; //维度ID
		String typeFormula = ""; //汇总类型
		boolean orgSummarizing = false; //组织是否汇总
		boolean kpiSummarizing = false; //指标是否汇总
		boolean strategySummarizing = false; //目标是否汇总
		boolean processSummarizing = false; //流程是否汇总
		ArrayList<String> idsArrayList = new ArrayList<String>(); //ID集合		
		HashMap<String, StatisticsResult> statisticsResultAllMap = o_statisticsResultBO.findStatisticsResultByAssessPlanIdAllMap(assessPlanId);
		HashMap<String, StatisticsResult> statisticsResultByDimIdAllMap = 
				o_statisticsResultBO.findStatisticsResultByAssessPlanIdByDimIdAllMap(assessPlanId);
		FormulaSet formulaSet = o_formularSetBO.findFormulaSet(companyId);
		if(org.apache.commons.lang.StringUtils.isBlank(formulaSet.getRiskTypeDimFormula())){
			FormulaSet formulaSet2 = o_formularSetBO.findFormulaSet("XA");
			if(null != formulaSet2){
				formulaSet.setRiskTypeDimFormula(formulaSet2.getRiskTypeDimFormula());
			}
		}
		HashMap<String, String> summarizingMap = o_weightSetBO.findInitValue("orgSummarizing,kpiSummarizing,strategySummarizing,processSummarizing");
		HashMap<String, ArrayList<Risk>> parentReRiskAllMap = o_riskBO.findParentReRiskAllMap(companyId, "re");
		HashMap<String, ArrayList<Risk>> parentRbsRiskAllMap = o_riskBO.findParentReRiskAllMap(companyId, "rbs");
		int maxRbsElevel = o_riskBO.findRiskMaxElevel(companyId);
		HashMap<Integer, ArrayList<Risk>> levelRbsRiskAllMap = o_riskBO.findLevelRbsRiskAllMap(companyId, maxRbsElevel);
		List<Map<String, Object>> templateRelaDimensionMapList = 
				o_showAssessBO.findTemplateRelaDimensionIsParentIdIsNullAllMapObj(o_showAssessBO.getTemplateId(assessPlanId));
		String involvesRiskIds = this.getInvolvesRiskIds(assessPlanId);
		
		if(summarizingMap.size() != 0){
			if(summarizingMap.get("orgSummarizing").indexOf("True") != -1){
				orgSummarizing = true;
			}if(summarizingMap.get("kpiSummarizing").indexOf("True") != -1){
				kpiSummarizing = true;
			}if(summarizingMap.get("strategySummarizing").indexOf("True") != -1){
				strategySummarizing = true;
			}if(summarizingMap.get("processSummarizing").indexOf("True") != -1){
				processSummarizing = true;
			}
		}else{
			orgSummarizing = true;
			kpiSummarizing = true;
			strategySummarizing = true;
			processSummarizing = true;
		}
		
		if(isAfreshSummarizing){
			//重新汇总
			statisticsResultList = o_statisticsResultBO.findStatisticsResultByAssessPlanIdAllList(assessPlanId);
		}else{
			//初始化汇总
			statisticsResultList = o_riskTidyBO.getRiskReSummarizingList(assessPlanId, companyId, statisticsResultAllMap, formulaSet, 
					templateRelaDimensionMapList);
		}
		
		//查询出来的风险事件分值汇总计算存日志
		if(isAfreshSummarizing){
			ArrayList<String> scoreValueTempList = null; //分值
			HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
			String riskLevelFormula = formulaSet.getRiskLevelFormula();
	    	ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
	    	FormulaLog formulaLog = null;
	    	String riskLevelFormulaText = formulaSet.getRiskLevelFormulaText();
	    	Map<String, Risk> riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
	    	
			for (StatisticsResult statisticsResult : statisticsResultList) {
				if(map.get(statisticsResult.getRiskScoreObject()) != null){
					map.get(statisticsResult.getRiskScoreObject()).add(statisticsResult.getDimension().getId() + "--" + statisticsResult.getScore());
				}else{
					ArrayList<String> dimAndValue = new ArrayList<String>();
					dimAndValue.add(statisticsResult.getDimension().getId() + "--" + statisticsResult.getScore());
					map.put(statisticsResult.getRiskScoreObject(), dimAndValue);
				}
			}
			
            Set<Entry<String, ArrayList<String>>> key = map.entrySet();
            for (Iterator<Entry<String, ArrayList<String>>> it = key.iterator(); it.hasNext();) {
                Entry<String, ArrayList<String>> keys = it.next();
                ArrayList<String> arrayList = map.get(keys.getKey());
                scoreValueTempList = new ArrayList<String>();
                Risk risk = riskAllMap.get(keys.getKey());
                for (String string : arrayList) {
                	String strs[] = string.split("--");
                	scoreValueTempList.add(strs[0] + "--" + strs[1]);
				}
                
                String riskLevelStr = o_riskLevelBO.getRisklevel(scoreValueTempList, riskLevelFormula, dimIdAllList);
                if(riskLevelStr.indexOf("--") != -1){
                	//失败
                	String formulaContent = riskLevelStr;
                	formulaLog = new FormulaLog();
                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, risk.getId(), 
                				riskAllMap.get(risk.getId()).getName(), "risk",
                            "assessValueFormula", formulaContent, "failure", null, null, null));
                }else{
                	if(Double.valueOf(riskLevelStr) == -1){
                		//失败
                    	String formulaContent = riskLevelStr;
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, risk.getId(), 
                    				riskAllMap.get(risk.getId()).getName(), "risk",
                                "assessValueFormula", formulaContent, "failure", null, null, null));
                	}else{
                		//成功
                    	String formulaContent = riskLevelFormulaText;
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, risk.getId(), 
                    				riskAllMap.get(risk.getId()).getName(), "risk",
                                "assessValueFormula", formulaContent, "success", String.valueOf(riskLevelStr), null, null));
                	}
                }
            }
		}
		
		if(statisticsResultList.size() != 0){
			//分类汇总得值
			for (Map<String, Object> map : templateRelaDimensionMapList) {
				String dimS[] = formulaSet.getRiskTypeDimFormula().replace("|", "--").split("--");
				
				for (String str : dimS) {
					if(str.indexOf(map.get("dimId").toString()) != -1){
						dimId = str.split(",")[0];
						typeFormula = str.split(",")[1];
						break;
					}
				}
				
				this.getRiskRbsSummarizingList(dimId, companyId,
						assessPlanId, typeFormula, parentReRiskAllMap,
						parentRbsRiskAllMap, maxRbsElevel, levelRbsRiskAllMap, statisticsResultList,
						statisticsResultByDimIdAllMap);
			}
		}
		
		if(isAfreshSummarizing){
			//风险事件、分类汇总存储
			HashMap<String, StatisticsResult> statisticsResultByAssessPlanIdMapAll = 
					o_statisticsResultBO.findStatisticsResultByAssessPlanIdAllMap(assessPlanId);
			ArrayList<StatisticsResult> tempStatisticsResult = new ArrayList<StatisticsResult>();
			for (StatisticsResult statisticsResult : statisticsResultList) {
				if(statisticsResultByAssessPlanIdMapAll.get(statisticsResult.getRiskScoreObject()) == null){
					tempStatisticsResult.add(statisticsResult);
				}
			}
			
			if(tempStatisticsResult.size() != 0){
				//更改结构
				o_statisticsResultBO.saveStatisticsResultSql(tempStatisticsResult);
			}
			
			o_statisticsResultBO.updateStatisticsResultSql(statisticsResultList);
		}else{
			//风险事件、分类汇总存储
			o_statisticsResultBO.saveStatisticsResultSql(statisticsResultList);
		}
			
		//风险事件、风险分类、定性评估结果存储评估纪录
		this.isSaveRiskAdjustHistory(companyId, assessPlanId, isAfreshSummarizing, riskAssessPlan, formulaSet, templateRelaDimensionMapList);
		if(orgSummarizing){
			//部门汇总
			this.isOrgSummarizing(companyId, assessPlanId, formulaSet, isAfreshSummarizing, involvesRiskIds);
		}if(kpiSummarizing){
			//指标汇总
			this.isKpiSummarizing(companyId, assessPlanId, formulaSet, isAfreshSummarizing, involvesRiskIds);
		}if(strategySummarizing){
			//目标汇总
			this.isStrategySummarizing(companyId, assessPlanId, formulaSet, isAfreshSummarizing, involvesRiskIds);
		}if(processSummarizing){
			//流程汇总
			this.isProcessSummarizing(companyId, assessPlanId, formulaSet, isAfreshSummarizing, involvesRiskIds);
		}
		
		if(!orgSummarizing){
			//组织不进行汇总(之前汇总过)
			this.notOrgSummarizing(assessPlanId);
		}if(!kpiSummarizing){
			//指标不进行汇总(之前汇总过)
			this.notKpiSummarizing(assessPlanId);
		}if(!strategySummarizing){
			//目标不进行汇总(之前汇总过)
			this.notStrategySummarizing(assessPlanId);
		}if(!processSummarizing){
			//流程不进行汇总(之前汇总过)
			this.notProcessSummarizing(assessPlanId);
		}
		
		//改变风险分类后,删除垃圾风险历史
		this.riskRbsChange(assessPlanId, statisticsResultList, idsArrayList);
		//改变组织分类后,删除垃圾组织历史
		this.orgChange(assessPlanId, companyId, idsArrayList, involvesRiskIds);
		//改变指标后,删除垃圾指标历史
		this.kpiChange(assessPlanId, companyId, idsArrayList, involvesRiskIds);
		//改变目标后,删除垃圾目标历史
		this.strategyChange(assessPlanId, companyId, idsArrayList, involvesRiskIds);
		//改变流程后,删除垃圾流程历史
		this.processChange(assessPlanId, companyId, idsArrayList, involvesRiskIds);
	}
	
	/**
	 * 删除该计划下的所有历史结果
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void deleteHistoryResult(String assessPlanId){
		o_statisticsResultBO.deleteStatisticsResultAdjustHistory(assessPlanId);
		o_riskAdjustHistoryBO.deleteRiskAdjustHistory(assessPlanId);
		o_orgAdjustHistoryBO.deleteOrgAdjustHistory(assessPlanId);
		o_kpiAdjustHistoryBO.deleteKpiAdjustHistory(assessPlanId);
		o_strategyAdjustHistoryBO.deleteStrategyAdjustHistory(assessPlanId);
		o_processAdjustHistoryBO.deleteProcessAdjustHistory(assessPlanId);
	}
	
	/**
	 * 风险分类:
	 * 发生可能性汇总(风险事件发生可能性计算公式：加权平均/最大值)
	 * 影响程度汇总(风险事件影响程度计算公式：加权平均/最大值)
	 * @param scoreDimId 打分维度ID
	 * @param companyId 公司ID
	 * @param dictId 分值ID
	 * @param dictId 数据字典ID
	 * @param parentReRiskAllMap 风险分类集合
	 * @param parentRbsRiskAllMap 风险事件集合
	 * @param maxRbsElevel 风险最高级别
	 * @param levelRbsRiskAllMap 级别集合
	 * @param statisticsResultList 打分总实体集合
	 * @param statisticsResultByDimIdAllMap 打分维度集合
	 * */
	public List<StatisticsResult> getRiskRbsSummarizingList(String scoreDimId, 
			String companyId, 
			String assessPlanId, 
			String dictId,
			HashMap<String, ArrayList<Risk>> parentReRiskAllMap,
			HashMap<String, ArrayList<Risk>> parentRbsRiskAllMap,
			int maxRbsElevel,
			HashMap<Integer, ArrayList<Risk>> levelRbsRiskAllMap,
			List<StatisticsResult> statisticsResultList,
			HashMap<String, StatisticsResult> statisticsResultByDimIdAllMap){
		
		return o_riskRbsOperBO.getReAndRbsDimList(parentReRiskAllMap, parentRbsRiskAllMap, maxRbsElevel, companyId, scoreDimId, 
						assessPlanId, dictId, levelRbsRiskAllMap ,statisticsResultList, statisticsResultByDimIdAllMap);
	}
	
	/**
	 * 流程风险汇总(平均值/最大值(风险事件))
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 计算公式实体
	 * @param isAfreshSummarizing 是否重新计算
	 * @param involvesRiskIds 关联风险
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean isProcessSummarizing(String companyId, String assessPlanId, FormulaSet formulaSet, 
			boolean isAfreshSummarizing, String involvesRiskIds){
		HashMap<String, ArrayList<Object>> map = o_processureOperBO.getProcessureOper(companyId, assessPlanId, formulaSet, involvesRiskIds, isAfreshSummarizing);
		ArrayList<Object> processAdjustHistoryArrayList = map.get("processAdjustHistoryArrayList");
		ArrayList<Object> processAdjustHistoryNextArrayList = map.get("processAdjustHistoryNextArrayList");
		
		this.notProcessSummarizing(assessPlanId);
		o_processAdjustHistoryBO.saveProcessAdjustHistorySql(processAdjustHistoryArrayList);
		if(processAdjustHistoryNextArrayList.size() != 0){
			o_processAdjustHistoryBO.updateProcessAdjustHistorySql(processAdjustHistoryNextArrayList);
		}
		
		return true;
	}
	
	
	/**
	 * 指标风险汇总(平均值/最大值(风险事件))
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 计算公式实体
	 * @param isAfreshSummarizing 是否汇总
	 * @param involvesRiskIds 风险关联
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean isKpiSummarizing(String companyId, String assessPlanId, FormulaSet formulaSet, boolean isAfreshSummarizing, String involvesRiskIds){
		HashMap<String, ArrayList<Object>> map = o_kpiOperBO.getKpiOper(companyId, assessPlanId, formulaSet, involvesRiskIds, isAfreshSummarizing);
		ArrayList<Object> kpiAdjustHistoryArrayList = map.get("kpiAdjustHistoryArrayList");
		ArrayList<Object> kpiAdjustHistoryNextArrayList = map.get("kpiAdjustHistoryNextArrayList");
		
		this.notKpiSummarizing(assessPlanId);
		o_kpiAdjustHistoryBO.savekpiAdjustHistorySql(kpiAdjustHistoryArrayList);
		if(kpiAdjustHistoryNextArrayList.size() != 0){
			o_kpiAdjustHistoryBO.updatekpiAdjustHistorySql(kpiAdjustHistoryNextArrayList);
		}
		
		return true;
	}
	
	/**
	 * 目标风险汇总(加权平均值(目标下指标权重))
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 计算公式实体
	 * @param isAfreshSummarizing 是否汇总
	 * @param involvesRiskIds 风险关联
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean isStrategySummarizing(String companyId, String assessPlanId, FormulaSet formulaSet, boolean isAfreshSummarizing, String involvesRiskIds){
		HashMap<String, ArrayList<Object>> map = o_strategyMapOperBO.getStrategyOper(companyId, assessPlanId, formulaSet, isAfreshSummarizing, involvesRiskIds);
		ArrayList<Object> strategyAdjustHistoryArrayList = map.get("strategyAdjustHistoryArrayList");
		ArrayList<Object> strategyAdjustHistoryNextArrayList = map.get("strategyAdjustHistoryNextArrayList");
		
		this.notStrategySummarizing(assessPlanId);
		o_strategyAdjustHistoryBO.saveStrategyAdjustHistorySql(strategyAdjustHistoryArrayList);
		if(strategyAdjustHistoryNextArrayList.size() != 0){
			o_strategyAdjustHistoryBO.updateStrategyAdjustHistorySql(strategyAdjustHistoryNextArrayList);
		}
		
		return true;
	}
	
	/**
	 * 组织风险汇总(平均值/最大值(组织下所有风险事件))
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 计算公式实体
	 * @param isAfreshSummarizing 是否汇总
	 * @param involvesRiskIds 关联风险
	 * */
	@Transactional
	public boolean isOrgSummarizing(String companyId, String assessPlanId, FormulaSet formulaSet, 
			boolean isAfreshSummarizing, String involvesRiskIds){
		HashMap<String, ArrayList<Object>> orgOperMap = o_orgOperBO.getOrgOper(companyId, assessPlanId, formulaSet, involvesRiskIds, isAfreshSummarizing);
		ArrayList<Object> orgAdjustHistoryArrayList = orgOperMap.get("orgAdjustHistoryArrayList");//组织历史记录
		ArrayList<Object> orgAdjustHistoryNextArrayList = orgOperMap.get("orgAdjustHistoryNextArrayList");//组织上一区间
		
		this.notOrgSummarizing(assessPlanId);
		o_orgAdjustHistoryBO.saveOrgAdjustHistorySql(orgAdjustHistoryArrayList);
		if(orgAdjustHistoryNextArrayList.size() != 0){
			o_orgAdjustHistoryBO.updateOrgAdjustHistorySql(orgAdjustHistoryNextArrayList);
		}
		
		return true;
	}
	
	
	/**存储风险类别、事件到风险评估纪录中
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param isAfreshSummarizing 是否汇总
	 * @param riskAssessPlan 评估计划实体
	 * @param formulaSet 计算公式实体
	 * @param templateRelaDimensionMapList 模板关联维度集合
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean isSaveRiskAdjustHistory(String companyId, String assessPlanId, 
			boolean isAfreshSummarizing, RiskAssessPlan riskAssessPlan, FormulaSet formulaSet,
			List<Map<String, Object>> templateRelaDimensionMapList){
		HashMap<String, ArrayList<Object>> riskSummarizingMap = 
				o_riskRbsOrReOperBO.getRiskSummarizing(companyId, assessPlanId, isAfreshSummarizing, riskAssessPlan, formulaSet, 
						templateRelaDimensionMapList);
		
		ArrayList<Object> riskAdjustHistoryArrayList = riskSummarizingMap.get("riskAdjustHistoryArrayList");//风险历史记录
		ArrayList<Object> riskAdjustHistoryNextArrayList = riskSummarizingMap.get("riskAdjustHistoryNextArrayList");//风险下一区间值
		ArrayList<Object> adjustHistoryResultArrayList = riskSummarizingMap.get("adjustHistoryResultArrayList");//定性评估结果
		
		if(!isAfreshSummarizing){
			o_riskAdjustHistoryBO.saveRiskAdjustHistorySql(riskAdjustHistoryArrayList);
			if(riskAdjustHistoryNextArrayList.size() != 0){
				o_riskAdjustHistoryBO.updateRiskAdjustHistorySql(riskAdjustHistoryNextArrayList);
			}
			o_adjustHistoryResultBO.saveAdjustHistorySql(adjustHistoryResultArrayList);
		}else{
			o_riskAdjustHistoryBO.updateRiskAdjustHistorySql(riskAdjustHistoryArrayList);
			if(riskAdjustHistoryNextArrayList.size() != 0){
				o_riskAdjustHistoryBO.updateRiskAdjustHistorySql(riskAdjustHistoryNextArrayList);
			}
			o_adjustHistoryResultBO.updateAdjustHistorySql(adjustHistoryResultArrayList);
		}
		
		return true;
	}
	
	/**
	 * 通过评估计划ID 查询所有关联到的风险事件
	 * @param assessPlanId 评估计划ID
	 * @return String
	 * @author 金鹏祥
	 * */
	public String getInvolvesRiskIds(String assessPlanId){
		StringBuffer riskIds = new StringBuffer();
		List<RiskScoreObject> riskScoreObjectList = o_scoreObjectBO.findRiskScoreObjByplanId(assessPlanId);
		for (RiskScoreObject riskScoreObject : riskScoreObjectList) {
			//riskIds += "'" + riskScoreObject.getRisk().getId() + "'" + ",";
			riskIds = riskIds.append("'" + riskScoreObject.getRisk().getId() + "'" + ",");
		}
		String riskIdsStr = riskIds.toString() + ",";
		riskIdsStr = riskIdsStr.replace(",,", "");
		
		return riskIdsStr;
	}
	
	/**
	 * 将综合打分结果LIST汇总成MAP方式<riskId,riskId>
	 * @param statisticsResultList 总体打分集合
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	public HashMap<String, String> getStatisticsResultMap(List<StatisticsResult> statisticsResultList){
		HashMap<String, String> map = new HashMap<String, String>();
		for (StatisticsResult statisticsResult : statisticsResultList) {
			map.put(statisticsResult.getRiskScoreObject(), statisticsResult.getRiskScoreObject());
		}
		
		return map;
	}
	
	/**
	 * 组织不进行汇总(之前汇总过)
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥 
	 * */
	public void notRiskSummarizing(String assessPlanId){
		//组织不进行汇总(之前汇总过)
		HashMap<String, RiskAdjustHistory> riskAdjustHistoryByIdAndAssessPlanIdMap =  
				o_riskAdjustHistoryBO.findRiskAdjustHistoryByIdAndAssessPlanId(assessPlanId);
		if(riskAdjustHistoryByIdAndAssessPlanIdMap.size() != 0){
			ArrayList<String> idsArrayList = new ArrayList<String>();
			Iterator<Entry<String, RiskAdjustHistory>> iter = riskAdjustHistoryByIdAndAssessPlanIdMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, RiskAdjustHistory> key = iter.next();
				RiskAdjustHistory riskAdjustHistory = riskAdjustHistoryByIdAndAssessPlanIdMap.get(key.getKey());
				idsArrayList.add(riskAdjustHistory.getId());
			}
			o_riskAdjustHistoryBO.deleteRiskAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 组织不进行汇总(之前汇总过)
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void notOrgSummarizing(String assessPlanId){
		//组织不进行汇总(之前汇总过)
		HashMap<String, OrgAdjustHistory> orgAdjustHistoryByIdAndAssessPlanIdMap =  
				o_orgAdjustHistoryBO.findOrgAdjustHistoryByIdAndAssessPlanId(assessPlanId);
		if(orgAdjustHistoryByIdAndAssessPlanIdMap.size() != 0){
			ArrayList<String> idsArrayList = new ArrayList<String>();
			Iterator<Entry<String, OrgAdjustHistory>> iter = orgAdjustHistoryByIdAndAssessPlanIdMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, OrgAdjustHistory> key = iter.next();
				OrgAdjustHistory orgAdjustHistory = orgAdjustHistoryByIdAndAssessPlanIdMap.get(key.getKey());
				idsArrayList.add(orgAdjustHistory.getId());
			}
			o_orgAdjustHistoryBO.deleteOrgAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 指标不进行汇总(之前汇总过)
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void notKpiSummarizing(String assessPlanId){
		//指标不进行汇总(之前汇总过)
		HashMap<String, KpiAdjustHistory> kpiAdjustHistoryByIdAndAssessPlanIdMap =  
				o_kpiAdjustHistoryBO.findKpiAdjustHistoryByIdAndAssessPlanId(assessPlanId);
		if(kpiAdjustHistoryByIdAndAssessPlanIdMap.size() != 0){
			ArrayList<String> idsArrayList = new ArrayList<String>();
			Iterator<Entry<String, KpiAdjustHistory>> iter = kpiAdjustHistoryByIdAndAssessPlanIdMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, KpiAdjustHistory> key = iter.next();
				KpiAdjustHistory kpiAdjustHistory = kpiAdjustHistoryByIdAndAssessPlanIdMap.get(key.getKey());
				idsArrayList.add(kpiAdjustHistory.getId());
			}
			o_kpiAdjustHistoryBO.deleteKpiAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 目标不进行汇总(之前汇总过)
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void notStrategySummarizing(String assessPlanId){
		//目标不进行汇总(之前汇总过)
		HashMap<String, StrategyAdjustHistory> strategyMapAdjustHistoryByIdAndAssessPlanIdMap = 
				o_strategyAdjustHistoryBO.findStrategyMapAdjustHistoryByIdAndAssessPlanId(assessPlanId);
		if(strategyMapAdjustHistoryByIdAndAssessPlanIdMap.size() != 0){
			ArrayList<String> idsArrayList = new ArrayList<String>();
			Iterator<Entry<String, StrategyAdjustHistory>> iter = strategyMapAdjustHistoryByIdAndAssessPlanIdMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, StrategyAdjustHistory> key = iter.next();
				StrategyAdjustHistory strategyAdjustHistory = strategyMapAdjustHistoryByIdAndAssessPlanIdMap.get(key.getKey());
				idsArrayList.add(strategyAdjustHistory.getId());
			}
			o_strategyAdjustHistoryBO.deleteStrategyAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 流程不进行汇总(之前汇总过)
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void notProcessSummarizing(String assessPlanId){
		//流程不进行汇总(之前汇总过)
		HashMap<String, ProcessAdjustHistory> processAdjustHistoryByAssessPlanIdMap = 
				o_processAdjustHistoryBO.findProcessAdjustHistoryByAssessPlanIdMapAll(assessPlanId);
		if(processAdjustHistoryByAssessPlanIdMap.size() != 0){
			ArrayList<String> idsArrayList = new ArrayList<String>();
			Iterator<Entry<String, ProcessAdjustHistory>> iter = processAdjustHistoryByAssessPlanIdMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, ProcessAdjustHistory> key = iter.next();
				ProcessAdjustHistory processAdjustHistory = processAdjustHistoryByAssessPlanIdMap.get(key.getKey());
				idsArrayList.add(processAdjustHistory.getId());
			}
			o_processAdjustHistoryBO.deleteProcessAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 改变风险分类后,删除垃圾风险历史
	 * @param assessPlanId 评估计划ID
	 * @param statisticsResultList 总体打分集合
	 * @param idsArrayList 关联ID集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void riskRbsChange(String assessPlanId, List<StatisticsResult> statisticsResultList, ArrayList<String> idsArrayList){
		List<RiskAdjustHistory> riskAdjustHistoryListAll = o_riskAdjustHistoryBO.findTransactionalRiskAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		HashMap<String, String> statisticsResultMap = this.getStatisticsResultMap(statisticsResultList);
		for (RiskAdjustHistory riskAdjustHistory : riskAdjustHistoryListAll) {
			if(statisticsResultMap.get(riskAdjustHistory.getRisk().getId()) == null){
				idsArrayList.add(riskAdjustHistory.getId());
			}
		}
		
		if(idsArrayList.size() != 0){
			o_adjustHistoryResultBO.deleteAdjustHistoryId(idsArrayList);
			o_riskAdjustHistoryBO.deleteRiskAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 改变组织分类后,删除垃圾组织历史
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @param idsArrayList ID集合
	 * @param involvesRiskIds 关联风险
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void orgChange(String assessPlanId, String companyId, ArrayList<String> idsArrayList, String involvesRiskIds){
		String strs[] = involvesRiskIds.split(",");
		ArrayList<String> ids = new ArrayList<String>();
		for (String string : strs) {
			ids.add(string.replace("'", ""));
		}
		HashMap<String, ArrayList<Object>> orgToRiskByCompanyIdMap = o_orgAdjustHistoryBO.findOrgToRiskByCompanyId(companyId, ids);
		List<OrgAdjustHistory> orgAdjustHistoryByAssessListAll = o_orgAdjustHistoryBO.findTransactionalOrgAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		for (OrgAdjustHistory orgAdjustHistory : orgAdjustHistoryByAssessListAll) {
			if(orgToRiskByCompanyIdMap.get(orgAdjustHistory.getOrganization().getId()) == null){
				idsArrayList.add(orgAdjustHistory.getId());
			}
		}
		
		if(idsArrayList.size() != 0){
			o_orgAdjustHistoryBO.deleteOrgAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 改变指标后,删除垃圾指标历史
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @param idsArrayList ID集合
	 * @param involvesRiskIds
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void kpiChange(String assessPlanId, String companyId, ArrayList<String> idsArrayList, String involvesRiskIds){
		String strs[] = involvesRiskIds.split(",");
		ArrayList<String> ids = new ArrayList<String>();
		for (String string : strs) {
			ids.add(string.replace("'", ""));
		}
		HashMap<String, ArrayList<Object>> kpiToRiskByCompanyIdMap = o_kpiAdjustHistoryBO.findKpiToRiskByCompanyId(companyId, ids);
		List<KpiAdjustHistory> kpiAdjustHistoryByAssessListAll = o_kpiAdjustHistoryBO.findTransactionalKpiAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		for (KpiAdjustHistory kpiAdjustHistory : kpiAdjustHistoryByAssessListAll) {
			if(kpiToRiskByCompanyIdMap.get(kpiAdjustHistory.getKpiId().getId()) == null){
				idsArrayList.add(kpiAdjustHistory.getId());
			}
		}
		
		if(idsArrayList.size() != 0){
			o_kpiAdjustHistoryBO.deleteKpiAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 改变目标后,删除垃圾目标历史
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @param idsArrayList ID集合
	 * @param involvesRiskIds 关联
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void strategyChange(String assessPlanId, String companyId, ArrayList<String> idsArrayList, String involvesRiskIds){
		String strs[] = involvesRiskIds.split(",");
		ArrayList<String> ids = new ArrayList<String>();
		for (String string : strs) {
			ids.add(string.replace("'", ""));
		}
		HashMap<String, ArrayList<Object>> strategyToRiskByCompanyIdMap = 
				o_strategyAdjustHistoryBO.findStrategyToRiskByCompanyId(companyId, ids, assessPlanId);
		List<StrategyAdjustHistory> strategyAdjustHistoryByAssessListAll = 
				o_strategyAdjustHistoryBO.findTransactionalStrategyAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		for (StrategyAdjustHistory strategyAdjustHistory : strategyAdjustHistoryByAssessListAll) {
			if(strategyToRiskByCompanyIdMap.get(strategyAdjustHistory.getStrategyMap().getId()) == null){
				idsArrayList.add(strategyAdjustHistory.getId());
			}
		}
		
		if(idsArrayList.size() != 0){
			o_strategyAdjustHistoryBO.deleteStrategyAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 改变流程后,删除垃圾流程历史
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @param idsArrayList ID集合
	 * @param involvesRiskIds 关联
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void processChange(String assessPlanId, String companyId, ArrayList<String> idsArrayList, String involvesRiskIds){
		ArrayList<String> involvesRiskIdsList = new ArrayList<String>();
		String strs[] = involvesRiskIds.split(",");
		for (String string : strs) {
			involvesRiskIdsList.add(string.replace("'", ""));
		}
		HashMap<String, ArrayList<Object>> processureToRiskByCompanyIdMap = 
				o_processAdjustHistoryBO.findProcessureToRiskByCompanyId(companyId, involvesRiskIdsList);
		
		List<ProcessAdjustHistory> processAdjustHistoryByAssessListAll = 
				o_processAdjustHistoryBO.findTransactionalProcessAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		for (ProcessAdjustHistory processAdjustHistory : processAdjustHistoryByAssessListAll) {
			if(processureToRiskByCompanyIdMap.get(processAdjustHistory.getProcess().getId()) == null){
				idsArrayList.add(processAdjustHistory.getId());
			}
		}
		
		if(idsArrayList.size() != 0){
			o_processAdjustHistoryBO.deleteProcessAdjustHistory(idsArrayList);
		}
	}
	
	/**
	 * 重新汇总
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥 
	 * */
	public void afreshSummarizing(String assessPlanId){
		this.summarizingOperSf2(assessPlanId, true);
	}
	
	public void afreshCalu(String assessPlanId){
		List<AdjustHistoryResult> resultList = o_adjustHistoryResultBO.findHistoryResultByAssessPlanId(assessPlanId);
		//计算风险综合分数
		List<Map<String,Double>> sorceList = this.caluComposite(assessPlanId,resultList);
		//将获取的综合得分写入到object表中
		o_scoreObjectBO.updateRiskScoreByRiskId(sorceList);
		o_riskTidyBO.execRiskTidy(assessPlanId);
	}
	
	/**
	 * 汇总
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥 
	 * */
	public void summarizing(String assessPlanId){
		this.summarizingOperSf2(assessPlanId, false);
	}
}