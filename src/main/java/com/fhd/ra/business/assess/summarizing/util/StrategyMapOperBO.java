package com.fhd.ra.business.assess.summarizing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.risk.StrategyAdjustHistory;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.ra.business.assess.oper.KpiAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.StrategyAdjustHistoryBO;
import com.fhd.ra.business.assess.util.CalculateUtil;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sys.business.assess.WeightSetBO;

/**
 * 计算目标状态分业务
 * */

@Service
public class StrategyMapOperBO {
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private StrategyAdjustHistoryBO o_strategyAdjustHistoryBO;
	
	@Autowired
	private KpiAdjustHistoryBO o_kpiAdjustHistoryBO;
	
	@Autowired
	private StrategyMapBO o_strategyMapBO;
	
	@Autowired
	private FormulaLogBO o_formulaLogBO;
	
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	private Log log = LogFactory.getLog(StrategyMapOperBO.class);
	
	/**查询目标并已MAP方式存储
	 * @param companyId 公司ID
	 * @return HashMap<String, StrategyMap>
	 * @author 金鹏祥
	 * */
	public HashMap<String, StrategyMap> findStrategyMapAllByCompanyId(String companyId){
		List<StrategyMap> list = o_strategyMapBO.findStrategyMapAllByCompanyId(companyId);
		HashMap<String, StrategyMap> map = new HashMap<String, StrategyMap>();
		for (StrategyMap strategyMap : list) {
			map.put(strategyMap.getId(), strategyMap);
		}
		
		return map;
	}
	
	/**
	 * 保存指标涉及风险
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 计算公式实体
	 * @param isAfreshSummarizing 是否重新计算
	 * @param involvesRiskIds 关联风险
	 * @return HashMap<String, ArrayList<Object>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("rawtypes")
	@Transactional
	public HashMap<String, ArrayList<Object>> getStrategyOper(
			String companyId, 
			String assessPlanId, 
			FormulaSet formulaSet,
			boolean isAfreshSummarizing,
			String involvesRiskIds){
		
		double eweightFinal = 0l;
		ArrayList<Object> list = null;
		HashMap<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>();
		ArrayList<Object> strategyAdjustHistoryArrayList = new ArrayList<Object>();
		ArrayList<Object> strategyAdjustHistoryNextArrayList = new ArrayList<Object>();
		HashMap<String, Object> strategyAdjustHistoryMap = null;
		
		try {
			HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
			HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
			HashMap<String, StrategyAdjustHistory> strategyAdjustHistoryAllMap = null;
			String alarmScenario = "";
			if(o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario() != null){
	    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
	    	}
			
			if(isAfreshSummarizing){
				strategyAdjustHistoryAllMap = o_strategyAdjustHistoryBO.findStrategyMapAdjustHistoryByIdAndAssessPlanId(assessPlanId);
			}
			String strs[] = involvesRiskIds.split(",");
			ArrayList<String> idsArrayList = new ArrayList<String>();
			for (String string : strs) {
				idsArrayList.add(string.replace("'", ""));
			}
			HashMap<String, ArrayList<Object>> strategyToRiskByCompanyIdMap = 
					o_strategyAdjustHistoryBO.findStrategyToRiskByCompanyId(companyId, idsArrayList, assessPlanId);
			HashMap<String, KpiAdjustHistory> kpiAdjustHistoryByIdAndAssessPlanIdMapAll = 
					o_kpiAdjustHistoryBO.findKpiAdjustHistoryByIdAndAssessPlanId(assessPlanId);
			List<StrategyAdjustHistory> strategyAdjustHistoryListAll = o_strategyAdjustHistoryBO.findStrategyAdjustHistoryByAssessAllList();
			HashMap<String, StrategyAdjustHistory> strategyAdjustHistoryByIdMapAll = 
					o_strategyAdjustHistoryBO.findStrategyAdjustHistoryByAssessAllMap();
			//目标计算公式
			String strategyCalculateFormula = "";
			HashMap<String, StrategyMap> strategyMapAllByCompanyId = this.findStrategyMapAllByCompanyId(companyId);
			Set<String> key = strategyToRiskByCompanyIdMap.keySet();
			FormulaLog formulaLog = null;
			
            for (Iterator it = key.iterator(); it.hasNext();) {
                String s = (String) it.next();
                list = strategyToRiskByCompanyIdMap.get(s);
                
                try {
                	if(formulaSet.getStrategyRiskFormula().equalsIgnoreCase("dim_calculate_method_weight_avg")){//加权平均
                		strategyCalculateFormula = "加权平均";
                    	eweightFinal = CalculateUtil.getEweightMaValue(list, kpiAdjustHistoryByIdAndAssessPlanIdMapAll);
                    }
                	
                	if(eweightFinal == 0){
                		//失败
                    	String formulaContent = "读取不到下级指标分值";
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, strategyMapAllByCompanyId.get(s).getName(), "risk",
                                "assessValueFormula", formulaContent, "failure", null, null, null));
                	}else{
                		//成功
                    	String formulaContent = strategyCalculateFormula;
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, strategyMapAllByCompanyId.get(s).getName(), "risk",
                                "assessValueFormula", formulaContent, "success", String.valueOf(strategyCalculateFormula), null, null));
                	}
				} catch (Exception e) {
					//失败
                	String formulaContent = e.getMessage();
                	formulaLog = new FormulaLog();
                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, strategyMapAllByCompanyId.get(s).getName(), "risk",
                            "assessValueFormula", formulaContent, "failure", null, null, null));
				}
                
                //保存
                strategyAdjustHistoryMap = this.getStrategySummarizing(eweightFinal, 
                		s, companyId, timePeriodMapAll, alarmPlanAllMap, alarmScenario,
                		assessPlanId, isAfreshSummarizing, strategyAdjustHistoryAllMap, strategyAdjustHistoryListAll, 
                		strategyAdjustHistoryByIdMapAll, strategyCalculateFormula);
                
                strategyAdjustHistoryArrayList.add(strategyAdjustHistoryMap.get("strategyAdjustHistory"));
                if(null != strategyAdjustHistoryMap.get("strategyAdjustHistoryNext")){
                	strategyAdjustHistoryNextArrayList.add(strategyAdjustHistoryMap.get("strategyAdjustHistoryNext"));
                }
            }
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		map.put("strategyAdjustHistoryArrayList", strategyAdjustHistoryArrayList);
		map.put("strategyAdjustHistoryNextArrayList", strategyAdjustHistoryNextArrayList);
		return map;
	}
	
	/**
	 * 存储目标评估记录
	 * @param scoreValueFinal 分值
	 * @param strategyId 目标ID
	 * @param companyId 公司ID
	 * @param timePeriodMapAll 时间区间集合
	 * @param alarmPlanAllMap 告警MAP
	 * @param alarmScenario 计算公式
	 * @param assessPlanId 评估计划ID
	 * @param isAfreshSummarizing 是否汇总
	 * @param strategyAdjustHistoryAllMap 目标记录ListMAP
	 * @param strategyAdjustHistoryAllList 目标记录集合
	 * @param strategyAdjustHistoryByIdMapAll 目标通过ID
	 * @param strategyCalculateFormula 目标计算公式
	 * @return HashMap<String, Object>
	 * @author 金鹏祥
	 * */
	@Transactional
	public HashMap<String, Object> getStrategySummarizing(
			double scoreValueFinal, 
			String strategyId, 
			String companyId, 
			HashMap<String, TimePeriod> timePeriodMapAll,
			HashMap<String, AlarmPlan> alarmPlanAllMap,
			String alarmScenario,
			String assessPlanId,
			boolean isAfreshSummarizing,
			HashMap<String, StrategyAdjustHistory> strategyAdjustHistoryAllMap,
			List<StrategyAdjustHistory> strategyAdjustHistoryAllList,
			HashMap<String, StrategyAdjustHistory> strategyAdjustHistoryByIdMapAll,
			String strategyCalculateFormula){
		
		StrategyAdjustHistory strategyAdjustHistory = null;
		StrategyAdjustHistory strategyAdjustHistoryNext = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		try {
			SysOrganization sysOrganization = new SysOrganization();
			sysOrganization.setId(companyId);
			
			StrategyMap strategyMap = new StrategyMap();
			strategyMap.setId(strategyId);
			
			if(isAfreshSummarizing){
				strategyAdjustHistory = strategyAdjustHistoryAllMap.get(strategyId);
				if(null == strategyAdjustHistory){
					strategyAdjustHistory = new StrategyAdjustHistory();
					strategyAdjustHistory.setId(Identities.uuid());
					strategyAdjustHistory.setIsLatest("0");
				}
			}else{
				//将同一流程下的之前评估记录修改为不是最新状态
				strategyAdjustHistory = new StrategyAdjustHistory();
				strategyAdjustHistory.setId(Identities.uuid());
				strategyAdjustHistory.setIsLatest("0");
			}
			
			
			strategyAdjustHistory.setStrategyMap(strategyMap);
			strategyAdjustHistory.setAdjustType("0");
			
			if(!isAfreshSummarizing){
				strategyAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
			}
			
			if(null == strategyAdjustHistory.getAdjustTime()){
				strategyAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
			}
			
			double parentRiskLevel = this.getStrategyParentRiskLevel(strategyAdjustHistoryAllList, 
					strategyId, strategyAdjustHistory.getAdjustTime().toString());
			
			if(parentRiskLevel != -1){
				if(scoreValueFinal > parentRiskLevel){
					//up：向上；
					strategyAdjustHistory.setEtrend("up");
				}else if(scoreValueFinal < parentRiskLevel){
					//down：向下；
					strategyAdjustHistory.setEtrend("down");
				}else if(scoreValueFinal == parentRiskLevel){
					//flat：水平
					strategyAdjustHistory.setEtrend("flat");
				}
			}
			
			strategyAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
			strategyAdjustHistory.setRiskStatus(scoreValueFinal);

			String assessementStatus = "";
			
//			if(scoreValueFinal != 0){
				DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(
						alarmPlanAllMap.get(alarmScenario), scoreValueFinal);
	    		if(dict != null){
	    			assessementStatus = dict.getValue();
	    		}
//			}
    		
			strategyAdjustHistory.setAssessementStatus(assessementStatus);
			strategyAdjustHistory.setCompanyId(sysOrganization);
			
			RiskAssessPlan assessPlan = new RiskAssessPlan();
			assessPlan.setId(assessPlanId);
			strategyAdjustHistory.setRiskAssessPlan(assessPlan);
			strategyAdjustHistory.setCalculateFormula(strategyCalculateFormula);
			
			strategyAdjustHistoryNext = this.getStrategyNextRiskLevel(
					strategyAdjustHistoryAllList, strategyId, strategyAdjustHistory.getAdjustTime().toString(), 
					strategyAdjustHistoryByIdMapAll, scoreValueFinal);
			
			map.put("strategyAdjustHistory", strategyAdjustHistory);
			map.put("strategyAdjustHistoryNext", strategyAdjustHistoryNext);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return map;
	}
	
	/**
	 * 更新目标评估记录is_latest=1为本次最新值
	 * @param assessPlanId 评估计划ID
	 * @param isLatest 是否更新最新状态
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void updateStrategyAdjustHistoryIsLatestIsOne(String assessPlanId, boolean isLatest){
		List<StrategyAdjustHistory> list = o_strategyAdjustHistoryBO.findStrategyAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		ArrayList<String> idsArrayList = new ArrayList<String>();
		if(list.size() != 0){
			for (StrategyAdjustHistory strategyAdjustHistory : list) {
				idsArrayList.add(strategyAdjustHistory.getStrategyMap().getId());
			}
			if(isLatest){
				o_strategyAdjustHistoryBO.updateStrategyAdjustHistory(idsArrayList);
				o_strategyAdjustHistoryBO.updateStrategyAdjustHistoryByAssessPlanId(assessPlanId);
			}
		}
	}
	
	/**
	 * 指标通过录入具体时间、风险ID,得到此风险上一区间值
	 * @param strategyAdjustHistoryListAll 目标记录集合
	 * @param strategyId 目标ID
	 * @param adjustTime 目标adjustTime时间
	 * @return double
	 * @author 金鹏祥
	 * */
	public double getStrategyParentRiskLevel(List<StrategyAdjustHistory> strategyAdjustHistoryListAll, String strategyId, String adjustTime){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, Double> adjustTimeStatusMap = new HashMap<Long, Double>();
		long[] maxTime = null;
		double parentRiskLevel = 0l;
		
		for (StrategyAdjustHistory strategyAdjustHistory : strategyAdjustHistoryListAll) {
			if(strategyAdjustHistory.getStrategyMap().getId().equalsIgnoreCase(strategyId)){
				if(!strategyAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							strategyAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").
							replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(strategyAdjustHistory.getAdjustTime().toString().replace(".0", "").
									replace(" ", "").replace("-", "").replace(":", "")), 
							strategyAdjustHistory.getRiskStatus());
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
	 * @param strategyAdjustHistoryListAll 目标集合
	 * @param strategyId 目标ID
	 * @param adjustTime 目标adjustTime
	 * @param strategyAdjustHistoryListMapAll 目标MAP集合
	 * @param riskLevel 目标分值
	 * @return StrategyAdjustHistory
	 * @author 金鹏祥
	 * */
	@Transactional
	public StrategyAdjustHistory getStrategyNextRiskLevel(List<StrategyAdjustHistory> strategyAdjustHistoryListAll, 
			String strategyId, String adjustTime, HashMap<String, StrategyAdjustHistory> strategyAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (StrategyAdjustHistory strategyAdjustHistory : strategyAdjustHistoryListAll) {
			if(strategyAdjustHistory.getStrategyMap().getId().equalsIgnoreCase(strategyId)){
				if(!strategyAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							strategyAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").
							replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(strategyAdjustHistory.getAdjustTime().toString().replace(".0", "").
									replace(" ", "").replace("-", "").replace(":", "")), 
							strategyAdjustHistory.getRiskStatus() + "--" + strategyAdjustHistory.getId());
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
		
		StrategyAdjustHistory history = strategyAdjustHistoryListMapAll.get(nextId);
		
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
	
	/**
	 * 风险通过录入具体时间、风险ID,得到此风险下一区间值
	 * @param strategyAdjustHistoryListAll 目标集合
	 * @param strategyId 目标ID
	 * @param adjustTime 目标adjustTime
	 * @param strategyAdjustHistoryListMapAll 目标MAP集合
	 * @param riskLevel 目标分值
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean strategyNextRiskLevel(List<StrategyAdjustHistory> strategyAdjustHistoryListAll, 
			String strategyId, String adjustTime, HashMap<String, StrategyAdjustHistory> strategyAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (StrategyAdjustHistory strategyAdjustHistory : strategyAdjustHistoryListAll) {
			if(strategyAdjustHistory.getStrategyMap().getId().equalsIgnoreCase(strategyId)){
				if(!strategyAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							strategyAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").
							replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(strategyAdjustHistory.getAdjustTime().toString().replace(".0", "").
									replace(" ", "").replace("-", "").replace(":", "")), 
							strategyAdjustHistory.getRiskStatus() + "--" + strategyAdjustHistory.getId());
				}
			}
		}
		
		if(adjustTimeArrayList.size() == 0){
			return false;
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
			return false;
		}else{
			maxTime = new long[adjustTimeMaxArrayList.size()];
			for (int i = 0; i < adjustTimeMaxArrayList.size(); i++) {
				maxTime[i] = adjustTimeMaxArrayList.get(i);
			}
			
			nextRiskLevel = Double.parseDouble(adjustTimeStatusMap.get(CalculateUtil.getMin(maxTime)).split("--")[0]);
			nextId = adjustTimeStatusMap.get(CalculateUtil.getMin(maxTime)).split("--")[1].toString();
		}
		
		StrategyAdjustHistory history = strategyAdjustHistoryListMapAll.get(nextId);
		
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
		
		o_strategyAdjustHistoryBO.mergeStrategyAdjustHistory(history);
		
		return true;
	}
}