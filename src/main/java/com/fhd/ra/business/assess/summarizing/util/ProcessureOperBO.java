package com.fhd.ra.business.assess.summarizing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.process.Process;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.ra.business.assess.util.CalculateUtil;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.ProcessAdjustHistoryBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.assess.WeightSetBO;

/**
 * 计算流程状态分业务
 * */

@Service
public class ProcessureOperBO {
	
	@Autowired
	private SummarizinggAtherUtilBO o_summarizinggAtherUtilBO;

	@Autowired
	private KpiBO o_kpiBO;

	@Autowired
	private ProcessAdjustHistoryBO o_processAdjustHistoryBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private FormulaLogBO o_formulaLogBO;
	
	@Autowired 
	private ProcessBO o_processBO;
	
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	private Log log = LogFactory.getLog(ProcessureOperBO.class);
	
	/**
	 * 保存流程涉及风险
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 计算公式实体
	 * @param involvesRiskIds 关联风险
	 * @param isAfreshSummarizing 是否重新计算
	 * @return HashMap<String, ArrayList<Object>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("rawtypes")
	@Transactional
	public HashMap<String, ArrayList<Object>> getProcessureOper(
			String companyId,
			String assessPlanId,
			FormulaSet formulaSet, 
			String involvesRiskIds,
			boolean isAfreshSummarizing) {
		double scoreValueFinal = 0l;
		ArrayList<Double> riskValueList = null;
		HashMap<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>();
		ArrayList<Object> processAdjustHistoryArrayList = new ArrayList<Object>();
		ArrayList<Object> processAdjustHistoryNextArrayList = new ArrayList<Object>();
		HashMap<String, Object> processAdjustHistoryMap = null;
		
		try {
			HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
			HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
			HashMap<String, ProcessAdjustHistory> processAdjustHistoryAllMap = null;
			String alarmScenario = "";
			if(o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario() != null){
	    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
	    	}
			if(isAfreshSummarizing){
				processAdjustHistoryAllMap = o_processAdjustHistoryBO.findProcessureAdjustHistoryByIdAndAssessPlanId(assessPlanId);
			}
			List<ProcessAdjustHistory> processAdjustHistoryListAll = o_processAdjustHistoryBO.findProcessAdjustHistoryByAssessAllList();
			HashMap<String, ProcessAdjustHistory> processAdjustHistoryByIdMapAll = o_processAdjustHistoryBO.findProcessAdjustHistoryByAssessAllMap();
			HashMap<String, ArrayList<ProcessAdjustHistory>> processAdjustHistoryListMapAll = 
					o_processAdjustHistoryBO.findProcessAdjustHistoryById();
			ArrayList<String> involvesRiskIdsList = new ArrayList<String>();
			String strs[] = involvesRiskIds.split(",");
			for (String string : strs) {
				involvesRiskIdsList.add(string.replace("'", ""));
			}
			HashMap<String, ArrayList<Object>> processureToRiskByCompanyIdMap = 
					o_processAdjustHistoryBO.findProcessureToRiskByCompanyId(companyId, involvesRiskIdsList);
			HashMap<String, ArrayList<String>> statisticsResultRiskElevelByAssessPlanIdMap = 
					o_summarizinggAtherUtilBO.findStatisticsResultRiskElevelByAssessPlanId(assessPlanId);
			String riskLevelFormula = "";
			if(null != formulaSet.getRiskLevelFormula()){
        		riskLevelFormula = formulaSet.getRiskLevelFormula();
        	}
			
			//流程计算公式
			ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
			Set<String> key = processureToRiskByCompanyIdMap.keySet();
			FormulaLog formulaLog = null;
			Map<String, Process> processMapAll = o_processBO.findProcessMapByCompanyId(companyId);
			String processCalculateFormula = "";
			
			for (Iterator it = key.iterator(); it.hasNext();) {
				String s = (String) it.next();
				ArrayList<Object> ProcessureToRisklist = processureToRiskByCompanyIdMap.get(s);
				riskValueList = new ArrayList<Double>();
				
				for (Object risk : ProcessureToRisklist) {
					if (statisticsResultRiskElevelByAssessPlanIdMap.get(risk.toString()) != null) {
						//统计结果表中拿取
						ArrayList<String> scoreValueList = statisticsResultRiskElevelByAssessPlanIdMap.get(risk.toString());
						String valueStr = o_riskLevelBO.getRisklevel(scoreValueList, riskLevelFormula, dimIdAllList);
						double value = 0;
						if(valueStr.indexOf("--") == -1){
							value = Double.valueOf(valueStr);
						}
						
                		riskValueList.add(value);
					}
				}
				
				try {
					if (formulaSet.getProcessRiskFormula().equalsIgnoreCase("dim_calculate_method_max")) {// 最大值
						scoreValueFinal = CalculateUtil.getMaxValue(riskValueList);
						processCalculateFormula = "最大值";
					} else if (formulaSet.getProcessRiskFormula().equalsIgnoreCase("dim_calculate_method_avg")) {// 平均值
						scoreValueFinal = CalculateUtil.getMaValue(riskValueList);
						processCalculateFormula = "平均值";
					}
					
					if(scoreValueFinal == 0){
						//失败
	                	String formulaContent = "读取不到下级指标分值";
	                	formulaLog = new FormulaLog();
	                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, processMapAll.get(s).getName(), "risk",
	                            "assessValueFormula", formulaContent, "failure", null, null, null));
					}else{
						//成功
	                	String formulaContent = processCalculateFormula;
	                	formulaLog = new FormulaLog();
	                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, processMapAll.get(s).getName(), "risk",
	                            "assessValueFormula", formulaContent, "success", String.valueOf(scoreValueFinal), null, null));
					}
				} catch (Exception e) {
					//失败
                	String formulaContent = e.getMessage();
                	formulaLog = new FormulaLog();
                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, processMapAll.get(s).getName(), "risk",
                            "assessValueFormula", formulaContent, "failure", null, null, null));
				}

				// 保存
				processAdjustHistoryMap = this.getProcessureSummarizing(scoreValueFinal, s, companyId,
						timePeriodMapAll, processAdjustHistoryListMapAll,
						processAdjustHistoryListAll, alarmPlanAllMap, alarmScenario, assessPlanId, isAfreshSummarizing, processAdjustHistoryAllMap,
						processAdjustHistoryByIdMapAll, processCalculateFormula);
				
				processAdjustHistoryArrayList.add(processAdjustHistoryMap.get("processAdjustHistory"));
                if(null != processAdjustHistoryMap.get("processAdjustHistoryNext")){
                	processAdjustHistoryNextArrayList.add(processAdjustHistoryMap.get("processAdjustHistoryNext"));
                }
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		map.put("processAdjustHistoryArrayList", processAdjustHistoryArrayList);
		map.put("processAdjustHistoryNextArrayList", processAdjustHistoryNextArrayList);
		return map;
	}
	
	/**
	 * 存储流程评估记录
	 * @param scoreValueFinal 分值
	 * @param processureId 流程ID
	 * @param companyId 公司ID
	 * @param timePeriodMapAll 时间区间集合
	 * @param processAdjustHistoryListMapAll 流程记录ListMAP
	 * @param processAdjustHistoryListAll 流程记录集合
	 * @param alarmPlanAllMap 告警MAP
	 * @param alarmScenario 计算公式
	 * @param assessPlanId 评估计划ID
	 * @param isAfreshSummarizing 是否汇总
	 * @param processAdjustHistoryAllMap 流程集合MAP
	 * @param processAdjustHistoryByIdMapAll 流程通过ID
	 * @param processCalculateFormula 流程计算公式
	 * @return HashMap<String, Object>
	 * @author 金鹏祥
	 * */
	@Transactional
	public HashMap<String, Object> getProcessureSummarizing(
			double scoreValueFinal,
			String processureId,
			String companyId,
			HashMap<String, TimePeriod> timePeriodMapAll,
			HashMap<String, ArrayList<ProcessAdjustHistory>> processAdjustHistoryListMapAll,
			List<ProcessAdjustHistory> processAdjustHistoryListAll,
			HashMap<String, AlarmPlan> alarmPlanAllMap, String alarmScenario, String assessPlanId,
			boolean isAfreshSummarizing,
			HashMap<String, ProcessAdjustHistory> processAdjustHistoryAllMap,
			HashMap<String, ProcessAdjustHistory> processAdjustHistoryByIdMapAll,
			String processCalculateFormula) {
		ProcessAdjustHistory processAdjustHistory = null;
		ProcessAdjustHistory processAdjustHistoryNext = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		try {
			SysOrganization sysOrganization = new SysOrganization();
			sysOrganization.setId(companyId);

			Process process = new Process();
			process.setId(processureId);
			
			if(isAfreshSummarizing){
				processAdjustHistory = processAdjustHistoryAllMap.get(processureId);
				if(null == processAdjustHistory){
					processAdjustHistory = new ProcessAdjustHistory();
					processAdjustHistory.setId(Identities.uuid());
					processAdjustHistory.setIsLatest("0");
				}
			}else{
				processAdjustHistory = new ProcessAdjustHistory();
				processAdjustHistory.setId(Identities.uuid());
				processAdjustHistory.setIsLatest("0");
			}
			
			processAdjustHistory.setProcess(process);
			processAdjustHistory.setAdjustType("0");
			
			if(!isAfreshSummarizing){
				processAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
			}
			
			if(null == processAdjustHistory.getAdjustTime()){
				processAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
			}
			
			double parentRiskLevel = this.getProcessParentRiskLevel(
					processAdjustHistoryListAll, processureId, processAdjustHistory.getAdjustTime().toString());
			
			if (parentRiskLevel != -1) {
				if (scoreValueFinal > parentRiskLevel) {
					// up：向上；
					processAdjustHistory.setEtrend("up");
				} else if (scoreValueFinal < parentRiskLevel) {
					// down：向下；
					processAdjustHistory.setEtrend("down");
				} else if (scoreValueFinal == parentRiskLevel) {
					// flat：水平
					processAdjustHistory.setEtrend("flat");
				}
			}

			processAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
			processAdjustHistory.setRiskStatus(scoreValueFinal);

			String assessementStatus = "";

			if(scoreValueFinal != 0){
				DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(alarmScenario),scoreValueFinal);
				if (dict != null) {
					assessementStatus = dict.getValue();
				}
			}

			processAdjustHistory.setAssessementStatus(assessementStatus);
			processAdjustHistory.setCompany(sysOrganization);
			
			RiskAssessPlan assessPlan = new RiskAssessPlan();
			assessPlan.setId(assessPlanId);
			processAdjustHistory.setRiskAssessPlan(assessPlan);
			processAdjustHistory.setCalculateFormula(processCalculateFormula);
			
			processAdjustHistoryNext = this.getProcessNextRiskLevel(
					processAdjustHistoryListAll, processureId, processAdjustHistory.getAdjustTime().toString(), 
						processAdjustHistoryByIdMapAll, scoreValueFinal);
			
			map.put("processAdjustHistory", processAdjustHistory);
			map.put("processAdjustHistoryNext", processAdjustHistoryNext);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return map;
	}
	
	/**
	 * 更新流程评估记录is_latest=1为本次最新值
	 * @param assessPlanId 评估计划ID
	 * @param isLatest 是否更新最新状态
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void updateProcessAdjustHistoryIsLatestIsOne(String assessPlanId, boolean isLatest){
		List<ProcessAdjustHistory> list = o_processAdjustHistoryBO.findProcessAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		if(list.size() != 0){
			String processIds = "";
			for (ProcessAdjustHistory processAdjustHistory : list) {
				processIds += "'" + processAdjustHistory.getProcess().getId() + "',";
			}
			processIds += "--";
			processIds = processIds.replace(",--", "");
			if(isLatest){
				o_processAdjustHistoryBO.updateProcessAdjustHistory(processIds);
				o_processAdjustHistoryBO.updateProcessAdjustHistoryByAssessPlanId(assessPlanId);
			}
		}
	}
	
	/**
	 * 流程通过录入具体时间、风险ID,得到此风险上一区间值
	 * @param processAdjustHistoryListAll 流程记录集合
	 * @param processId 流程ID
	 * @param adjustTime 流程adjustTime时间
	 * @return double
	 * @author 金鹏祥
	 * */
	public double getProcessParentRiskLevel(List<ProcessAdjustHistory> processAdjustHistoryListAll, String processId, String adjustTime){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, Double> adjustTimeStatusMap = new HashMap<Long, Double>();
		long[] maxTime = null;
		double parentRiskLevel = 0l;
		
		for (ProcessAdjustHistory processAdjustHistory : processAdjustHistoryListAll) {
			if(processAdjustHistory.getProcess().getId().equalsIgnoreCase(processId)){
				if(!processAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							processAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(processAdjustHistory.getAdjustTime().toString().replace(".0", "").
									replace(" ", "").replace("-", "").replace(":", "")), 
							processAdjustHistory.getRiskStatus());
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
	 * @param processAdjustHistoryListAll 流程集合
	 * @param processId 流程ID
	 * @param adjustTime 流程adjustTime
	 * @param processAdjustHistoryListMapAll 流程MAP集合
	 * @param riskLevel 流程分值
	 * @return ProcessAdjustHistory
	 * @author 金鹏祥
	 * */
	@Transactional
	public ProcessAdjustHistory getProcessNextRiskLevel(List<ProcessAdjustHistory> processAdjustHistoryListAll, 
			String processId, String adjustTime, HashMap<String, ProcessAdjustHistory> processAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (ProcessAdjustHistory processAdjustHistory : processAdjustHistoryListAll) {
			if(processAdjustHistory.getProcess().getId().equalsIgnoreCase(processId)){
				if(!processAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							processAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(processAdjustHistory.getAdjustTime().toString().replace(".0", "").
									replace(" ", "").replace("-", "").replace(":", "")), 
							processAdjustHistory.getRiskStatus() + "--" + processAdjustHistory.getId());
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
		
		ProcessAdjustHistory history = processAdjustHistoryListMapAll.get(nextId);
		
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
	 * @param processAdjustHistoryListAll 流程集合
	 * @param processId 流程ID
	 * @param adjustTime 流程adjustTime
	 * @param processAdjustHistoryListMapAll 流程MAP集合
	 * @param riskLevel 流程分值
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean processNextRiskLevel(List<ProcessAdjustHistory> processAdjustHistoryListAll, 
			String processId, String adjustTime, HashMap<String, ProcessAdjustHistory> processAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (ProcessAdjustHistory processAdjustHistory : processAdjustHistoryListAll) {
			if(processAdjustHistory.getProcess().getId().equalsIgnoreCase(processId)){
				if(!processAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							processAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(processAdjustHistory.getAdjustTime().toString().replace(".0", "").
									replace(" ", "").replace("-", "").replace(":", "")), 
							processAdjustHistory.getRiskStatus() + "--" + processAdjustHistory.getId());
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
		
		ProcessAdjustHistory history = processAdjustHistoryListMapAll.get(nextId);
		
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
		
		o_processAdjustHistoryBO.mergeProcessAdjustHistory(history);
		
		return true;
	}
}