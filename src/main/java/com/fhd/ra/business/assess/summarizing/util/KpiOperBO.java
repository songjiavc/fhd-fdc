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
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.ra.business.assess.oper.KpiAdjustHistoryBO;
import com.fhd.ra.business.assess.util.CalculateUtil;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.assess.WeightSetBO;

/**
 * 计算指标状态分业务
 * */

@Service
public class KpiOperBO {
	
	@Autowired
	private SummarizinggAtherUtilBO o_summarizinggAtherUtilBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private KpiAdjustHistoryBO o_kpiAdjustHistoryBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private FormulaLogBO o_formulaLogBO;

	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	private Log log = LogFactory.getLog(KpiOperBO.class);
	
	/**
	 * 已MAP方式存储查询公司指标数据
	 * @param companyId 公司ID
	 * @return HashMap<String, Kpi>
	 * @author 金鹏祥
	 * */
	public HashMap<String, Kpi> findKpiMapByCompanyId(String companyId){
		List<Kpi> list = o_kpiBO.findAllKpiListByCompanyId(companyId);
		HashMap<String, Kpi> map = new HashMap<String, Kpi>();
		for (Kpi kpi : list) {
			map.put(kpi.getId(), kpi);
		}
		
		return map;
	}
	
	/**
	 * 保存指标涉及风险
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 计算公式实体
	 * @param involvesRiskIds 风险关联
	 * @param isAfreshSummarizing 是否汇总
	 * @return HashMap<String, ArrayList<Object>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("rawtypes")
	@Transactional
	public HashMap<String, ArrayList<Object>> getKpiOper(
			String companyId, 
			String assessPlanId, 
			FormulaSet formulaSet,
			String involvesRiskIds,
			boolean isAfreshSummarizing){
		
		double scoreValueFinal = 0l; //分值
		ArrayList<Double> riskValueList = null; //风险分值
		FormulaLog formulaLog = null; //日志
		HashMap<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>(); //数据信息键值
		ArrayList<Object> kpiAdjustHistoryArrayList = new ArrayList<Object>(); //指标记录实体集合
		ArrayList<Object> kpiAdjustHistoryNextArrayList = new ArrayList<Object>(); //指标下一期间实体集合
		HashMap<String, Object> kpiAdjustHistoryMap = null; //指标记录实体
		
		try {
			HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
			HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
			HashMap<String, KpiAdjustHistory> kpiAdjustHistoryAllMap = null;
			String alarmScenario = "";
			if(o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario() != null){
	    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
	    	}
			if(isAfreshSummarizing){
				kpiAdjustHistoryAllMap = o_kpiAdjustHistoryBO.findKpiAdjustHistoryByIdAndAssessPlanId(assessPlanId);
			}
			HashMap<String, ArrayList<KpiAdjustHistory>> kpiAdjustHistoryListMapAll = o_kpiAdjustHistoryBO.findKpiAdjustHistoryById();
			List<KpiAdjustHistory> kpiAdjustHistoryListAll = o_kpiAdjustHistoryBO.findKpiAdjustHistoryByAssessAllList();
			HashMap<String, KpiAdjustHistory> kpiAdjustHistoryByIdMapAll = o_kpiAdjustHistoryBO.findKpiAdjustHistoryByAssessAllMap();
			String strs[] = involvesRiskIds.split(",");
			ArrayList<String> idsArrayList = new ArrayList<String>();
			for (String string : strs) {
				idsArrayList.add(string.replace("'", ""));
			}
			HashMap<String, ArrayList<Object>> kpiToRiskByCompanyIdMap = o_kpiAdjustHistoryBO.findKpiToRiskByCompanyId(companyId, idsArrayList);
			HashMap<String, ArrayList<String>> statisticsResultRiskElevelByAssessPlanIdMap = 
					o_summarizinggAtherUtilBO.findStatisticsResultRiskElevelByAssessPlanId(assessPlanId);
			String riskLevelFormula = "";
			if(null != formulaSet.getRiskLevelFormula()){
        		riskLevelFormula = formulaSet.getRiskLevelFormula();
        	}
			
			//指标计算公式
			String kpiCalculateFormula = "";
			ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
			Set<String> key = kpiToRiskByCompanyIdMap.keySet();
			HashMap<String, Kpi> kpiMapByCompanyId = this.findKpiMapByCompanyId(companyId);
			
            for (Iterator it = key.iterator(); it.hasNext();) {
                String s = (String) it.next();
                
                ArrayList<Object> kpiToRisklist = kpiToRiskByCompanyIdMap.get(s);
                riskValueList = new ArrayList<Double>();
                
                for (Object risk : kpiToRisklist) {
                	if(statisticsResultRiskElevelByAssessPlanIdMap.get(risk.toString()) != null){
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
                	if(formulaSet.getKpiRiskFormula().equalsIgnoreCase("dim_calculate_method_max")){//最大值
                    	scoreValueFinal = CalculateUtil.getMaxValue(riskValueList);
                    	kpiCalculateFormula = "最大值";
                    }else if(formulaSet.getKpiRiskFormula().equalsIgnoreCase("dim_calculate_method_avg")){//平均值
                    	scoreValueFinal = CalculateUtil.getMaValue(riskValueList);
                    	kpiCalculateFormula = "平均值";
                    }
                	
                	if(scoreValueFinal == 0){
                		//失败
                    	String formulaContent = "读取不到下级风险分值";
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, kpiMapByCompanyId.get(s).getName(), "risk",
                                "assessValueFormula", formulaContent, "failure", null, null, null));
                	}else{
                		//成功
                    	String formulaContent = kpiCalculateFormula;
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, kpiMapByCompanyId.get(s).getName(), "risk",
                                "assessValueFormula", formulaContent, "success", String.valueOf(scoreValueFinal), null, null));
                	}
				} catch (Exception e) {
					//失败
                	String formulaContent = e.getMessage();
                	formulaLog = new FormulaLog();
                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, kpiMapByCompanyId.get(s).getName(), "risk",
                            "assessValueFormula", formulaContent, "failure", null, null, null));
				}
                
                
                //保存
                kpiAdjustHistoryMap = this.getKpiSummarizing(scoreValueFinal, 
                		s, companyId, timePeriodMapAll, kpiAdjustHistoryListMapAll, kpiAdjustHistoryListAll, alarmPlanAllMap, 
                		alarmScenario, assessPlanId, isAfreshSummarizing, kpiAdjustHistoryAllMap, kpiAdjustHistoryByIdMapAll,
                		kpiCalculateFormula);
                
                kpiAdjustHistoryArrayList.add(kpiAdjustHistoryMap.get("kpiAdjustHistory"));
                if(null != kpiAdjustHistoryMap.get("kpiAdjustHistoryNext")){
                	kpiAdjustHistoryNextArrayList.add(kpiAdjustHistoryMap.get("kpiAdjustHistoryNext"));
                }
            }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		map.put("kpiAdjustHistoryArrayList", kpiAdjustHistoryArrayList);
		map.put("kpiAdjustHistoryNextArrayList", kpiAdjustHistoryNextArrayList);
		return map;
	}
	
	/**
	 * 存储指标评估记录
	 * @param scoreValueFinal 分值
	 * @param kpiId 指标ID
	 * @param companyId 公司ID
	 * @param timePeriodMapAll 时间区间集合
	 * @param kpiAdjustHistoryListMapAll 指标记录ListMAP
	 * @param kpiAdjustHistoryAllList 指标记录集合
	 * @param alarmPlanAllMap 告警MAP
	 * @param alarmScenario 计算公式
	 * @param assessPlanId 评估计划ID
	 * @param isAfreshSummarizing 是否汇总
	 * @param kpiAdjustHistoryAllMap 指标集合MAP
	 * @param kpiAdjustHistoryByIdMapAll 指标通过ID
	 * @param kpiCalculateFormula 指标计算公式
	 * @return HashMap<String, Object>
	 * @author 金鹏祥
	 * */
	@Transactional
	public HashMap<String, Object> getKpiSummarizing(
			double scoreValueFinal, 
			String kpiId, 
			String companyId, 
			HashMap<String, TimePeriod> timePeriodMapAll,
			HashMap<String, ArrayList<KpiAdjustHistory>> kpiAdjustHistoryListMapAll,
			List<KpiAdjustHistory> kpiAdjustHistoryAllList,
			HashMap<String, AlarmPlan> alarmPlanAllMap,
			String alarmScenario,
			String assessPlanId,
			boolean isAfreshSummarizing,
			HashMap<String, KpiAdjustHistory> kpiAdjustHistoryAllMap,
			HashMap<String, KpiAdjustHistory> kpiAdjustHistoryByIdMapAll,
			String kpiCalculateFormula){
		KpiAdjustHistory kpiAdjustHistory = null;
		KpiAdjustHistory kpiAdjustHistoryNext = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		try {
			
			SysOrganization sysOrganization = new SysOrganization();
			sysOrganization.setId(companyId);
			
			Kpi kpi = new Kpi();
			kpi.setId(kpiId);
			
			if(isAfreshSummarizing){
				kpiAdjustHistory = kpiAdjustHistoryAllMap.get(kpiId);
				if(null == kpiAdjustHistory){
					kpiAdjustHistory = new KpiAdjustHistory();
					kpiAdjustHistory.setId(Identities.uuid());
					kpiAdjustHistory.setIsLatest("0");
				}
			}else{
				//将同一流程下的之前评估记录修改为不是最新状态
				kpiAdjustHistory = new KpiAdjustHistory();
				kpiAdjustHistory.setId(Identities.uuid());
				kpiAdjustHistory.setIsLatest("0");
			}
			
			
			kpiAdjustHistory.setKpiId(kpi);
			kpiAdjustHistory.setAdjustType("0");
			
			if(!isAfreshSummarizing){
				kpiAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
			}
			
			if(null == kpiAdjustHistory.getAdjustTime()){
				kpiAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
			}
			
			double parentRiskLevel = this.getKpiParentRiskLevel(kpiAdjustHistoryAllList, kpiId, kpiAdjustHistory.getAdjustTime().toString());
			
			if(parentRiskLevel != -1){
				if(scoreValueFinal > parentRiskLevel){
					//up：向上；
					kpiAdjustHistory.setEtrend("up");
				}else if(scoreValueFinal < parentRiskLevel){
					//down：向下；
					kpiAdjustHistory.setEtrend("down");
				}else if(scoreValueFinal == parentRiskLevel){
					//flat：水平
					kpiAdjustHistory.setEtrend("flat");
				}
			}
			
			kpiAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
			kpiAdjustHistory.setRiskStatus(scoreValueFinal);
			
			String assessementStatus = "";
			
			if(scoreValueFinal != 0){
				DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(
						alarmPlanAllMap.get(alarmScenario), scoreValueFinal);
	    		if(dict != null){
	    			assessementStatus = dict.getValue();
	    		}
			}
			
			kpiAdjustHistory.setAssessementStatus(assessementStatus);
			kpiAdjustHistory.setCompanyId(sysOrganization);
			
			RiskAssessPlan assessPlan = new RiskAssessPlan();
			assessPlan.setId(assessPlanId);
			kpiAdjustHistory.setRiskAssessPlan(assessPlan);
			kpiAdjustHistory.setCalculateFormula(kpiCalculateFormula);
			
			kpiAdjustHistoryNext = this.getKpiNextRiskLevel(
					kpiAdjustHistoryAllList, kpiId, kpiAdjustHistory.getAdjustTime().toString(), kpiAdjustHistoryByIdMapAll, scoreValueFinal);
			
			map.put("kpiAdjustHistory", kpiAdjustHistory);
			map.put("kpiAdjustHistoryNext", kpiAdjustHistoryNext);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return map;
	}
	
	/**
	 * 更新指标评估记录is_latest=1为本次最新值
	 * @param assessPlanId 评估计划ID
	 * @param isLatest 是否改为最新
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void updateProcessAdjustHistoryIsLatestIsOne(String assessPlanId, boolean isLatest){
		List<KpiAdjustHistory> list = o_kpiAdjustHistoryBO.findKpiAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		ArrayList<String> idsArrayList = new ArrayList<String>();
		if(list.size() != 0){
			for (KpiAdjustHistory kpiAdjustHistory : list) {
				idsArrayList.add(kpiAdjustHistory.getKpiId().getId());
			}
			if(isLatest){
				o_kpiAdjustHistoryBO.updateKpiAdjustHistory(idsArrayList);
				o_kpiAdjustHistoryBO.updateKpiAdjustHistoryByAssessPlanId(assessPlanId);
			}
		}
	}
	
	/**
	 * 指标通过录入具体时间、风险ID,得到此风险上一区间值
	 * @param kpiAdjustHistoryListAll 指标记录集合
	 * @param kpiId 指标ID
	 * @param adjustTime 指标adjustTime时间
	 * @return double
	 * @author 金鹏祥
	 * */
	public double getKpiParentRiskLevel(List<KpiAdjustHistory> kpiAdjustHistoryListAll, String kpiId, String adjustTime){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, Double> adjustTimeStatusMap = new HashMap<Long, Double>();
		long[] maxTime = null;
		double parentRiskLevel = 0l;
		
		for (KpiAdjustHistory kpiAdjustHistory : kpiAdjustHistoryListAll) {
			if(kpiAdjustHistory.getKpiId().getId().equalsIgnoreCase(kpiId)){
				if(!kpiAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							kpiAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(kpiAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
							kpiAdjustHistory.getRiskStatus());
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
	 * @param kpiAdjustHistoryListAll 指标记录集合
	 * @param kpiId 指标ID
	 * @param adjustTime 指标adjustTime
	 * @param kpiAdjustHistoryListMapAll 指标记录MAP集合
	 * @param riskLevel 指标分值
	 * @return KpiAdjustHistory
	 * @author 金鹏祥
	 * */
	@Transactional
	public KpiAdjustHistory getKpiNextRiskLevel(List<KpiAdjustHistory> kpiAdjustHistoryListAll, 
			String kpiId, String adjustTime, HashMap<String, KpiAdjustHistory> kpiAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (KpiAdjustHistory kpiAdjustHistory : kpiAdjustHistoryListAll) {
			if(kpiAdjustHistory.getKpiId().getId().equalsIgnoreCase(kpiId)){
				if(!kpiAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							kpiAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(kpiAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
							kpiAdjustHistory.getRiskStatus() + "--" + kpiAdjustHistory.getId());
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
		
		KpiAdjustHistory history = kpiAdjustHistoryListMapAll.get(nextId);
		
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
	 * @param kpiAdjustHistoryListAll 指标集合
	 * @param kpiId 指标ID
	 * @param adjustTime 指标adjustTime
	 * @param kpiAdjustHistoryListMapAll 指标MAP集合
	 * @param riskLevel 指标分值
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean kpiNextRiskLevel(List<KpiAdjustHistory> kpiAdjustHistoryListAll, 
			String kpiId, String adjustTime, HashMap<String, KpiAdjustHistory> kpiAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (KpiAdjustHistory kpiAdjustHistory : kpiAdjustHistoryListAll) {
			if(kpiAdjustHistory.getKpiId().getId().equalsIgnoreCase(kpiId)){
				if(!kpiAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							kpiAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(kpiAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
							kpiAdjustHistory.getRiskStatus() + "--" + kpiAdjustHistory.getId());
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
		
		KpiAdjustHistory history = kpiAdjustHistoryListMapAll.get(nextId);
		
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
		
		o_kpiAdjustHistoryBO.mergeKpiAdjustHistory(history);
		
		return true;
	}
}