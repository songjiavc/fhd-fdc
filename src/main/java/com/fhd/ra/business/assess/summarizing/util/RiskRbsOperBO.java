package com.fhd.ra.business.assess.summarizing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.comm.business.formula.FormulaLogBO;
import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.assess.riskTidy.AdjustHistoryResult;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.StatisticsResultBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.assess.util.CalculateUtil;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.assess.WeightSetBO;

/**
 * 计算风险分类状态分业务
 * */

@Service
public class RiskRbsOperBO {
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private AdjustHistoryResultBO o_adjustHistoryResultBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	@Autowired
	private FormulaLogBO o_formulaLogBO;
	
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
	@Autowired
	private ShowRiskTidyBO o_showRiskTidyBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	private Log log = LogFactory.getLog(RiskRbsOperBO.class);
	
	/**
	 * 存储打分结果
	 * @param scoreValueFinal 分值
	 * @param scoreDimId 打分维度ID
	 * @param assessPlanId 评估计划ID
	 * @param riskScoreObject 打分对象ID
	 * @param statisticsResultList 总体打分集合
	 * @param statisticsResultByDimIdAllMap 总体通过打分维度ID,MAP集合
	 * @return List<StatisticsResult>
	 * @author 金鹏祥
	 * */
	private List<StatisticsResult> getStatisticsResult(double scoreValueFinal, String scoreDimId, String assessPlanId, String riskScoreObject,
			List<StatisticsResult> statisticsResultList,
			HashMap<String, StatisticsResult> statisticsResultByDimIdAllMap){
		StatisticsResult statisticsResult = new StatisticsResult();
		Dimension dimension = new Dimension();
		RiskAssessPlan riskAssessPlan = new RiskAssessPlan();
		
		riskAssessPlan.setId(assessPlanId);
		dimension.setId(scoreDimId);
		statisticsResult.setDimension(dimension);
		
		if(statisticsResultByDimIdAllMap.get(riskScoreObject + "--" + scoreDimId) != null){
			statisticsResult.setId(statisticsResultByDimIdAllMap.get(riskScoreObject + "--" + scoreDimId).getId());
		}else{
			statisticsResult.setId(Identities.uuid());
		}
		
		if(scoreValueFinal != 0){
			statisticsResult.setScore(String.valueOf(scoreValueFinal));
		}
		
		statisticsResult.setRiskAssessPlan(riskAssessPlan);
		statisticsResult.setRiskScoreObject(riskScoreObject);
		statisticsResult.setStatus(null);
		statisticsResultList.add(statisticsResult);
		
		return statisticsResultList;
	}
	
	/**
	 * 存储风险评估记录
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param isAfreshSummarizing 是否重新汇总
	 * @param riskAssessPlan 评估计划实体
	 * @param formulaSet 计算公式实体
	 * @param templateRelaDimensionMapList 模板关联维度实体
	 * @return HashMap<String, ArrayList<Object>>
	 * @author 金鹏祥
	 * */
	@Transactional
	public HashMap<String, ArrayList<Object>> getRiskSummarizing(
			String companyId, 
			String assessPlanId,
			boolean isAfreshSummarizing,
			RiskAssessPlan riskAssessPlan,
			FormulaSet formulaSet,
			List<Map<String, Object>> templateRelaDimensionMapList){
		
		RiskAdjustHistory riskAdjustHistory = null;
		FormulaLog formulaLog = null;
		HashMap<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>();
		ArrayList<Object> riskAdjustHistoryArrayList = new ArrayList<Object>();
		ArrayList<Object> riskAdjustHistoryNextArrayList = new ArrayList<Object>();
		ArrayList<Object> adjustHistoryResultArrayList = new ArrayList<Object>();
		
		try {
			HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
			HashMap<String,ArrayList<String>> statisticsResultValueMapAll = 
					o_showRiskTidyBO.findStatisticsResultByScoreObjectIdAndDimIdMapAll(assessPlanId);
			HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
			HashMap<String, String> involvesRiskIdMap = o_statisticsResultBO.findStatisticsResultRiskReOrRbsByAssessPlanId(assessPlanId);
			HashMap<String, RiskAdjustHistory> riskAdjustHistoryAllMap = null;
			HashMap<String, AdjustHistoryResult> adjustHistoryResultAllMap = o_adjustHistoryResultBO.findAdjustHistoryResultAllMap(assessPlanId);
			Map<String, Risk> riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
			String alarmScenario = "";
			if(o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario() != null){
	    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
	    	}
			
			if(isAfreshSummarizing){
				riskAdjustHistoryAllMap = o_riskAdjustHistoryBO.findRiskAdjustHistoryByIdAndAssessPlanId(assessPlanId);
			}
			
			HashMap<String, RiskAdjustHistory> riskAdjustHistoryListMapAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllMap();
			ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
			String riskLevelFormula = formulaSet.getRiskLevelFormula();
			String riskLevelFormulaText = formulaSet.getRiskLevelFormulaText();
			if(null == riskLevelFormula){
        		riskLevelFormula = "";
        	}
			ArrayList<String> scoreValueList = null;
			ArrayList<String> scoreValueTempList = null;
			Set<Entry<String, ArrayList<String>>> key = statisticsResultValueMapAll.entrySet();
			
			for (Iterator<Entry<String, ArrayList<String>>> it = key.iterator(); it.hasNext();) {
                String riskId = it.next().getKey();
                
                List<RiskAdjustHistory> riskAdjustHistoryListAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllList(riskId);
                if(involvesRiskIdMap.get(riskId) == null){
                	continue;
                }
               
                double riskLevel = 0l;
                String riskLevelStr = "";
                scoreValueList = statisticsResultValueMapAll.get(riskId);
                scoreValueTempList = new ArrayList<String>();
                
                for (String dimId : scoreValueList) {
                	for (Map<String, Object> keys : templateRelaDimensionMapList) {
                		if(dimId.split("--")[0].toString().equalsIgnoreCase(keys.get("dimId").toString())){
    						scoreValueTempList.add(dimId);
    					}
    				}
				}
                
                //得到风险水平
                riskLevelStr = o_riskLevelBO.getRisklevel(scoreValueTempList, riskLevelFormula, dimIdAllList);
                if(riskLevelStr.indexOf("--") == -1){
                	riskLevel = Double.valueOf(riskLevelStr);
                }
                
                if("rbs".equalsIgnoreCase(riskAllMap.get(riskId).getIsRiskClass())){
                	//风险分类
                	if(riskLevelStr.indexOf("--") != -1){
                		//失败
                    	String formulaContent = riskLevelStr;
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, riskId, riskAllMap.get(riskId).getName(), "risk",
                                "assessValueFormula", formulaContent, "failure", null, null, null));
                	}else{
                		//成功
                    	String formulaContent = riskLevelFormulaText;
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, riskId, riskAllMap.get(riskId).getName(), "risk",
                                "assessValueFormula", formulaContent, "success", String.valueOf(riskLevelStr), null, null));
                	}
                }
                
                SysOrganization sysOrganization = new SysOrganization();
                sysOrganization.setId(companyId);//公司ID
                
                Risk risk = new Risk();
                risk.setId(riskId);//风险ID
                
                if(isAfreshSummarizing){
                	riskAdjustHistory = riskAdjustHistoryAllMap.get(riskId);
                	if(null == riskAdjustHistory){
                		//结构分类改变新增进来
                		riskAdjustHistory = new RiskAdjustHistory();
                        riskAdjustHistory.setId(Identities.uuid());
                        riskAdjustHistory.setIsLatest("0");
                	}
                }else{
                	//将同一风险下的之前评估记录修改为不是最新状态
                	riskAdjustHistory = new RiskAdjustHistory();
                    riskAdjustHistory.setId(Identities.uuid());
                    riskAdjustHistory.setIsLatest("0");
                }
                
                riskAdjustHistory.setRisk(risk);
                riskAdjustHistory.setAdjustType("0");
                
                if(!isAfreshSummarizing){
                	riskAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
                }
                
                if(null == riskAdjustHistory.getAdjustTime()){
                	riskAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
    			}
                
                double parentRiskLevel = this.getRiskParentRiskLevel(riskAdjustHistoryListAll, riskId, riskAdjustHistory.getAdjustTime().toString());
                
    			if(parentRiskLevel != -1){
    				if(riskLevel > parentRiskLevel){
    					//up：向上；
    					riskAdjustHistory.setEtrend("up");
    				}else if(riskLevel < parentRiskLevel){
    					//down：向下；
    					riskAdjustHistory.setEtrend("down");
    				}else if(riskLevel == parentRiskLevel){
    					//flat：水平
    					riskAdjustHistory.setEtrend("flat");
    				}
    			}
    			
    			riskAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
    			
    			String assessementStatus = "";
    			
    			if(riskLevel != 0){
	    			DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario), riskLevel);
	        		if(dict != null){
	        			assessementStatus = dict.getValue();
	        		}
    			}
    			
        		if(null != riskAssessPlan.getTemplate()){
        			if(riskAssessPlan.getTemplate().getType().getId().equalsIgnoreCase("dim_template_type_sys")){
        				if(null != riskAssessPlan.getTemplate()){
        					try {
        						Template template = new Template();
        						template.setId(riskAssessPlan.getTemplate().getId());
        						riskAdjustHistory.setTemplate(template);
							} catch (Exception e) {
							}
            			}
        			}else if(riskAssessPlan.getTemplate().getType().getId().equalsIgnoreCase("dim_template_type_self")){
        				if(null != riskAssessPlan.getTemplate()){
        					try {
        						Template template = new Template();
        						template.setId(riskAssessPlan.getTemplate().getId());
        						riskAdjustHistory.setTemplate(template);
							} catch (Exception e) {
							}
            			}
        			}
        		}else{
        			//自有模板
    				if(riskAllMap.get(riskId).getIsRiskClass().equalsIgnoreCase("re")){
    					//风险事件
    					if(null != riskAllMap.get(riskId).getTemplate()){
    						try {
    							Template template = new Template();
    							template.setId(riskAllMap.get(riskId).getTemplate().getId());
    							riskAdjustHistory.setTemplate(template);
							} catch (Exception e) {
							}
            			}
    				}else if(riskAllMap.get(riskId).getIsRiskClass().equalsIgnoreCase("rbs")){
    					//风险分类
    					if(null != riskAllMap.get(riskId).getTemplate()){
    						try {
    							Template template = new Template();
    							template.setId(riskAllMap.get(riskId).getTemplate().getId());
    							riskAdjustHistory.setTemplate(template);
							} catch (Exception e) {
							}
            			}
    				}
        		}
        		
    			riskAdjustHistory.setAssessementStatus(assessementStatus);
    			riskAdjustHistory.setCompany(sysOrganization);
    			riskAdjustHistory.setStatus(riskLevel);
    			riskAdjustHistory.setCalculateFormula(riskLevelFormulaText);
    			RiskAssessPlan assessPlan = new RiskAssessPlan();
    			assessPlan.setId(assessPlanId);
    			riskAdjustHistory.setRiskAssessPlan(assessPlan);
    			riskAdjustHistoryArrayList.add(riskAdjustHistory);
    			
    			for (Object scoreDicValue : scoreValueList) {
					AdjustHistoryResult adjustHistoryResult = new AdjustHistoryResult();
					RiskAssessPlan rap = new RiskAssessPlan();
					rap.setId(assessPlanId);
					
					Dimension dimension = new Dimension();
					dimension.setId(scoreDicValue.toString().split("--")[0]);
					
					if(isAfreshSummarizing){
						adjustHistoryResult = adjustHistoryResultAllMap.get(riskAdjustHistory.getId() + "--" + dimension.getId() + "--" + assessPlanId);
						if(adjustHistoryResult == null){
							//新增分类维度增加
							adjustHistoryResult = new AdjustHistoryResult();
							adjustHistoryResult.setId(Identities.uuid());
						}
					}else{
						adjustHistoryResult.setId(Identities.uuid());
					}
					
					adjustHistoryResult.setAdjustHistoryId(riskAdjustHistory.getId());
					
					adjustHistoryResult.setDimension(dimension);
					adjustHistoryResult.setScore(scoreDicValue.toString().split("--")[1]);
					
					adjustHistoryResult.setRiskAssessPlan(rap);
					adjustHistoryResultArrayList.add(adjustHistoryResult);
				}
    			
    			RiskAdjustHistory riskAdjustHistoryNext = 
    					this.riskNextRiskLevel(riskAdjustHistoryListAll, riskId, 
    							riskAdjustHistory.getAdjustTime().toString(), riskAdjustHistoryListMapAll, riskLevel);
    			if(null != riskAdjustHistoryNext){
    				riskAdjustHistoryNextArrayList.add(riskAdjustHistoryNext);
    			}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		map.put("riskAdjustHistoryArrayList", riskAdjustHistoryArrayList);
		map.put("riskAdjustHistoryNextArrayList", riskAdjustHistoryNextArrayList);
		map.put("adjustHistoryResultArrayList", adjustHistoryResultArrayList);
		
		return map;
	}
	
	/**
	 * 找到评估的风险事件及对应的此维度及分值
	 * @param statisticsResultList 总体打分集合
	 * @param scoreDimId 打分维度ID
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, Double>
	 * @author 金鹏祥
	 * */
	private HashMap<String, Double> getScoreRisk(List<StatisticsResult> statisticsResultList, String scoreDimId, String assessPlanId){
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (StatisticsResult statisticsResult : statisticsResultList) {
			if(statisticsResult.getDimension().getId().equalsIgnoreCase(scoreDimId)){
				if(statisticsResult.getRiskAssessPlan().getId().equalsIgnoreCase(assessPlanId)){
					if(StringUtils.isBlank(statisticsResult.getScore())){
						map.put(statisticsResult.getRiskScoreObject(), Double.parseDouble("0"));
					}else{
						map.put(statisticsResult.getRiskScoreObject(), Double.parseDouble(statisticsResult.getScore()));
					}
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 风险分类向风险分类上汇总
	 * @param parentReRiskAllMap 风险分类集合
	 * @param parentRbsRiskAllMap 风险事件集合
	 * @param maxElevel 最高级别
	 * @param companyId 公司ID
	 * @param scoreDimId 打分维度ID
	 * @param assessPlanId 评估计划ID
	 * @param formula 计算公式
	 * @param levelRbsRiskAllMap 级别风险集合
	 * @param statisticsResultList 总体打分集合
	 * @param statisticsResultByDimIdAllMap 总体打分MAP
	 * @return List<StatisticsResult>
	 * @author 金鹏祥
	 * */
	public List<StatisticsResult> getReAndRbsDimList(
			HashMap<String, ArrayList<Risk>> parentReRiskAllMap,
			HashMap<String, ArrayList<Risk>> parentRbsRiskAllMap,
			int maxElevel, 
			String companyId, 
			String scoreDimId, 
			String assessPlanId,
			String formula,
			HashMap<Integer, ArrayList<Risk>> levelRbsRiskAllMap,
			List<StatisticsResult> statisticsResultList,
			HashMap<String, StatisticsResult> statisticsResultByDimIdAllMap){
		
		ArrayList<Double> scoreValueList = null; //打分集合
		ArrayList<String> tempList = null;  //模板集合
		double scoreValueFinal = 0l; //分值
		FormulaLog formulaLog = null; //日志
		
		for (int i = 0; i < maxElevel + 1; i++) {
			ArrayList<Risk> riskList = levelRbsRiskAllMap.get(maxElevel - i);
			
			if(riskList != null){
				for (Risk risk : riskList) {
					HashMap<String, Double> maps = this.getScoreRisk(statisticsResultList, scoreDimId, assessPlanId);
					if(maps.size() == 0){
						return null;
					}
					
					scoreValueList = new ArrayList<Double>();
		            tempList = new ArrayList<String>();
		            if(i == 0){
		            	//汇总最下级风险分类
		            	if(parentReRiskAllMap.get(risk.getId()) != null){//该分类下是否存在风险事件
			            	//该风险分类下的所有子分类及事件进行汇总
			            	ArrayList<Risk> riskChildList = parentReRiskAllMap.get(risk.getId());
			            	for (Risk riskChild : riskChildList) {
			            		if(maps.get(riskChild.getId()) != null){
		                    		//向统计打分结果记录中拿取.....
		                    		scoreValueList.add(maps.get(riskChild.getId()));
		                    		tempList.add(riskChild.getId());
			            		}
			            	}
			            }
		             }else{
		            	 //汇总无限级风险分类
		            	 if(parentRbsRiskAllMap.get(risk.getId()) != null){//该风险分类下是否存在分类
			            	 //该风险分类下的所有子分类及事件进行汇总
			            	 ArrayList<Risk> riskChildList = parentRbsRiskAllMap.get(risk.getId());
			            	 for (Risk riskChild : riskChildList) {
			            		if(maps.get(riskChild.getId()) != null){
		                    			//向统计打分结果记录中拿取.....
		                    			scoreValueList.add(maps.get(riskChild.getId()));
		                    			tempList.add(riskChild.getId());
	                    		}
							}
			            }
		            	
		            	//汇总最下级风险分类
		            	if(parentReRiskAllMap.get(risk.getId()) != null){//该分类下是否存在风险事件
			            	//该风险分类下的所有子分类及事件进行汇总
			            	ArrayList<Risk> riskChildList = parentReRiskAllMap.get(risk.getId());
			            	for (Risk riskChild : riskChildList) {
			            		if(maps.get(riskChild.getId()) != null){
		                    		//向统计打分结果记录中拿取.....
		                    		scoreValueList.add(maps.get(riskChild.getId()));
		                    		tempList.add(riskChild.getId());
			            		}
			            	}
			            }
		             }
		            
	                if(scoreValueList.size() != 0){
	                	try {
	                		if(formula.equalsIgnoreCase("dim_calculate_method_max")){//最大值
			                	//最大值
			                	scoreValueFinal = CalculateUtil.getMaxValue(scoreValueList);
			                	this.getStatisticsResult(scoreValueFinal, scoreDimId, assessPlanId, risk.getId(), statisticsResultList, 
			                			statisticsResultByDimIdAllMap);
			                }else if(formula.equalsIgnoreCase("dim_calculate_method_weight_avg")){//加权平均
			                	//加权平均
			                	scoreValueFinal = CalculateUtil.getSunMaValue(scoreValueList);
			                	this.getStatisticsResult(scoreValueFinal, scoreDimId, assessPlanId, risk.getId(), statisticsResultList,
			                			statisticsResultByDimIdAllMap);
			                }
						} catch (Exception e) {
							//失败
		                	String formulaContent = e.getMessage();
		                	formulaLog = new FormulaLog();
		                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, risk.getId(), 
		                				risk.getName(), "risk",
		                            "assessValueFormula", formulaContent, "failure", null, null, null));
						}
		                
	                }
				}   
			}
		}
		
		return statisticsResultList;
	}
	
	/**
	 * 更新该评估计划下所有新增风险delete_estatus=0
	 * @param assessPlanId 评估计划ID
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void updateRiskAdjustHistoryIsLatestIsZero(String assessPlanId){
		ArrayList<String> riskIdsList = o_scoreObjectBO.findScoreObjectNewsRiskId(assessPlanId);
		o_riskBO.updateRiskDelectEstatusByInRiskId(riskIdsList);
		o_statisticsResultBO.updateRiskEventByAssessPlanId(assessPlanId);
	}
	
	/**
	 * 更新风险评估记录is_latest=1为本次最新值
	 * @param assessPlanId 评估计划ID
	 * @param isLatest 是否更新最新状态
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void updateRiskAdjustHistoryIsLatestIsOne(String assessPlanId, boolean isLatest){
		List<RiskAdjustHistory> list = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		ArrayList<String> idsArrayList = new ArrayList<String>();
		if(list.size() != 0){
			for (RiskAdjustHistory riskAdjustHistory : list) {
				idsArrayList.add(riskAdjustHistory.getRisk().getId());
			}
			if(isLatest){
				o_riskAdjustHistoryBO.updateRiskAdjustHistory(idsArrayList);
				o_riskAdjustHistoryBO.updateRiskAdjustHistoryByAssessPlanId(assessPlanId);
			}
		}
	}
	
	/**
	 * 风险通过录入具体时间、风险ID,得到此风险上一区间值
	 * @param riskAdjustHistoryListAll 风险记录集合
	 * @param riskId 风险ID
	 * @param adjustTime 风险adjustTime时间
	 * @return double
	 * @author 金鹏祥
	 * */
	public double getRiskParentRiskLevel(List<RiskAdjustHistory> riskAdjustHistoryListAll, String riskId, String adjustTime){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, Double> adjustTimeStatusMap = new HashMap<Long, Double>();
		long[] maxTime = null;
		double parentRiskLevel = 0l;
		
		for (RiskAdjustHistory riskAdjustHistory : riskAdjustHistoryListAll) {
			if(riskAdjustHistory.getRisk().getId().equalsIgnoreCase(riskId)){
				if(null != riskAdjustHistory.getAdjustTime()){
					if(!riskAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
						adjustTimeArrayList.add(Long.parseLong(
										riskAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
						adjustTimeStatusMap.put(
								Long.parseLong(riskAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
								riskAdjustHistory.getStatus());
					}
				}
			}
		}
		
		if(adjustTimeArrayList.size() == 0){
			return -1;
		}
		
		for (long l : adjustTimeArrayList) {
			if(Long.parseLong(adjustTime.replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")) > l){
				adjustTimeMaxArrayList.add(l);
			}
		}
		
		if(adjustTimeMaxArrayList.size() == 1){
			parentRiskLevel = adjustTimeStatusMap.get(adjustTimeMaxArrayList.get(0));
		}else if(adjustTimeMaxArrayList.size() == 0){
			return -1;
		}else{
			maxTime = new long[adjustTimeMaxArrayList.size()];
			for (int i = 0; i < adjustTimeMaxArrayList.size(); i++) {
				maxTime[i] = adjustTimeMaxArrayList.get(i);
			}
			
			parentRiskLevel = adjustTimeStatusMap.get(CalculateUtil.getMax(maxTime));
		}
		
		return parentRiskLevel;
	}
	
	/**
	 * 风险通过录入具体时间、风险ID,得到此风险下一区间值
	 * @param riskAdjustHistoryListAll 风险记录集合
	 * @param riskId 风险ID
	 * @param adjustTime 风险adjustTime
	 * @param riskAdjustHistoryListMapAll 风险记录MAP集合
	 * @param riskLevel 风险分值
	 * @return RiskAdjustHistory
	 * @author 金鹏祥
	 * */
	@Transactional
	public RiskAdjustHistory riskNextRiskLevel(List<RiskAdjustHistory> riskAdjustHistoryListAll, 
			String riskId, String adjustTime, HashMap<String, RiskAdjustHistory> riskAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (RiskAdjustHistory riskAdjustHistory : riskAdjustHistoryListAll) {
			if(riskAdjustHistory.getRisk().getId().equalsIgnoreCase(riskId)){
				if(null != riskAdjustHistory.getAdjustTime()){
					if(!riskAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
						adjustTimeArrayList.add(Long.parseLong(
										riskAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
						adjustTimeStatusMap.put(
								Long.parseLong(riskAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
								riskAdjustHistory.getStatus() + "--" + riskAdjustHistory.getId());
					}
				}
			}
		}
		
		if(adjustTimeArrayList.size() == 0){
			return null;
		}
		
		for (long l : adjustTimeArrayList) {
			if(Long.parseLong(adjustTime.replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")) < l){
				adjustTimeMaxArrayList.add(l);
			}
		}
		
		if(adjustTimeMaxArrayList.size() == 1){
			nextRiskLevel = Double.parseDouble(adjustTimeStatusMap.get(adjustTimeMaxArrayList.get(0)).split("--")[0]);
			nextId = adjustTimeStatusMap.get(adjustTimeMaxArrayList.get(0)).split("--")[1].toString();
		}else if(adjustTimeMaxArrayList.size() == 0){
			return null;
		}else{
			maxTime = new long[adjustTimeMaxArrayList.size()];
			for (int i = 0; i < adjustTimeMaxArrayList.size(); i++) {
				maxTime[i] = adjustTimeMaxArrayList.get(i);
			}
			
			nextRiskLevel = Double.parseDouble(adjustTimeStatusMap.get(CalculateUtil.getMin(maxTime)).split("--")[0]);
			nextId = adjustTimeStatusMap.get(CalculateUtil.getMin(maxTime)).split("--")[1].toString();
		}
		
		RiskAdjustHistory history = riskAdjustHistoryListMapAll.get(nextId);
		
		if(nextRiskLevel != -1){
			if(nextRiskLevel > riskLevel){
				//up：向上；
				history.setEtrend("up");
			}else if(nextRiskLevel < riskLevel){
				//down：向下；
				history.setEtrend("down");
			}else if(nextRiskLevel == riskLevel){
				//flat：水平
				history.setEtrend("flat");
			}
		}
		
		return history;
	}
}