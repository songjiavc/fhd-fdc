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
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaLog;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.ra.business.assess.oper.OrgAdjustHistoryBO;
import com.fhd.ra.business.assess.util.CalculateUtil;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.assess.WeightSetBO;
import com.fhd.sys.business.organization.OrgGridBO;

/**
 * 计算部门状态分业务
 * */

@Service
public class OrgOperBO {
	
	@Autowired
	private SummarizinggAtherUtilBO o_summarizinggAtherUtilBO;
	
	@Autowired
	private OrgAdjustHistoryBO o_orgAdjustHistoryBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private FormulaLogBO o_formulaLogBO;
	
	@Autowired
	private OrgGridBO o_orgBO;
	
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	private Log log = LogFactory.getLog(OrgOperBO.class);
	
	/**
	 * 查询组织并已公司ID过滤已MAP方式存储
	 * @param companyId 公司ID
	 * @return HashMap<String, SysOrganization>
	 * @author 金鹏祥
	 * */
	public HashMap<String, SysOrganization> findOrgMapByCompanyId(String companyId){
		List<SysOrganization> orgMapByCompanyId = o_orgBO.findOrganizationByCompanyIds(companyId);
		HashMap<String, SysOrganization> map = new HashMap<String, SysOrganization>();
		for (SysOrganization sysOrganization : orgMapByCompanyId) {
			map.put(sysOrganization.getId(), sysOrganization);
		}
		
		return map;
	}
	
	/**
	 * 保存指标涉及风险
	 * @param companyId 公司ID
	 * @param assessPlanId 评估计划ID
	 * @param formulaSet 公式计算实体
	 * @param involvesRiskIds 关联风险
	 * @param isAfreshSummarizing 是否汇总
	 * @return HashMap<String, ArrayList<Object>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("rawtypes")
	@Transactional
	public HashMap<String, ArrayList<Object>> getOrgOper(
			String companyId, 
			String assessPlanId, 
			FormulaSet formulaSet,
			String involvesRiskIds,
			boolean isAfreshSummarizing){
		
		double scoreValueFinal = 0l; //分值
		ArrayList<Double> riskValueList = null; //风险值集合
		FormulaLog formulaLog = null; //日志
		HashMap<String, OrgAdjustHistory> orgSummarizingMap = null; //组织是MAP
		ArrayList<Object> orgAdjustHistoryArrayList = new ArrayList<Object>(); //组织记录集合
		ArrayList<Object> orgAdjustHistoryNextArrayList = new ArrayList<Object>(); //组织下一期间集合
		HashMap<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>(); //数据信息键值
		
		try {
			HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
			HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
			HashMap<String, OrgAdjustHistory> orgAdjustHistoryAllMap = null;
			String alarmScenario = "";
			if(o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario() != null){
	    		alarmScenario = o_weightSetBO.findWeightSetAll(companyId).getAlarmScenario().getId();
	    	}
			
			if(isAfreshSummarizing){
				orgAdjustHistoryAllMap = o_orgAdjustHistoryBO.findOrgAdjustHistoryByIdAndAssessPlanId(assessPlanId);
			}
			
			List<OrgAdjustHistory> orgAdjustHistoryListAll = o_orgAdjustHistoryBO.findOrgAdjustHistoryByAssessAllList();
			HashMap<String, OrgAdjustHistory> orgAdjustHistoryByIdMapAll = o_orgAdjustHistoryBO.findOrgAdjustHistoryByAssessAllMap();
			HashMap<String, ArrayList<OrgAdjustHistory>> orgAdjustHistoryListMapAll = o_orgAdjustHistoryBO.findOrgAdjustHistoryById();
			String strs[] = involvesRiskIds.split(",");
			ArrayList<String> idsArrayList = new ArrayList<String>();
			for (String string : strs) {
				idsArrayList.add(string.replace("'", ""));
			}
			HashMap<String, ArrayList<Object>> orgToRiskByCompanyIdMap = o_orgAdjustHistoryBO.findOrgToRiskByCompanyId(companyId, idsArrayList);
			HashMap<String, ArrayList<String>> statisticsResultRiskElevelByAssessPlanIdMap = 
					o_summarizinggAtherUtilBO.findStatisticsResultRiskElevelByAssessPlanId(assessPlanId);
			String riskLevelFormula = "";
			
			if(null != formulaSet.getRiskLevelFormula()){
        		riskLevelFormula = formulaSet.getRiskLevelFormula();
			}
			
			//组织计算公式
			String orgCalculateFormula = "";
			ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
			Set<String> key = orgToRiskByCompanyIdMap.keySet();
			HashMap<String, SysOrganization> orgMapByCompanyId = this.findOrgMapByCompanyId(companyId);
			
            for (Iterator it = key.iterator(); it.hasNext();) {
                String s = (String) it.next();
                
                ArrayList<Object> orgToRisklist = orgToRiskByCompanyIdMap.get(s);
                riskValueList = new ArrayList<Double>();
                
                for (Object risk : orgToRisklist) {
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
                	if(formulaSet.getDeptRiskFormula().equalsIgnoreCase("dim_calculate_method_max")){//最大值
                    	scoreValueFinal = CalculateUtil.getMaxValue(riskValueList);
                    	orgCalculateFormula = "最大值";
                    }else if(formulaSet.getDeptRiskFormula().equalsIgnoreCase("dim_calculate_method_avg")){//平均值
                    	scoreValueFinal = CalculateUtil.getMaValue(riskValueList);
                    	orgCalculateFormula = "平均值";
                    }
                	
                	if(scoreValueFinal == 0){
                		//失败
                    	String formulaContent = "读取不到下级风险分值";
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, orgMapByCompanyId.get(s).getOrgname(), "risk",
                                "assessValueFormula", formulaContent, "failure", null, null, null));
                	}else{
                		//成功
                    	String formulaContent = orgCalculateFormula;
                    	formulaLog = new FormulaLog();
                    	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, orgMapByCompanyId.get(s).getOrgname(), "risk",
                                "assessValueFormula", formulaContent, "success", String.valueOf(scoreValueFinal), null, null));
                	}
				} catch (Exception e) {
					//失败
                	String formulaContent = e.getMessage();
                	formulaLog = new FormulaLog();
                	o_formulaLogBO.saveFormulaLog(o_formulaLogBO.packageFormulaLog(formulaLog, s, orgMapByCompanyId.get(s).getOrgname(), "risk",
                            "assessValueFormula", formulaContent, "failure", null, null, null));
				}
                
                //保存
                orgSummarizingMap = this.getOrgSummarizing(scoreValueFinal, 
                		s, companyId, timePeriodMapAll, orgAdjustHistoryListMapAll, orgAdjustHistoryListAll, alarmPlanAllMap, alarmScenario,
                		assessPlanId, isAfreshSummarizing, orgAdjustHistoryAllMap, orgAdjustHistoryByIdMapAll, orgCalculateFormula);
                
                orgAdjustHistoryArrayList.add(orgSummarizingMap.get("orgAdjustHistory"));
                if(null != orgSummarizingMap.get("orgAdjustHistoryNext")){
                	 orgAdjustHistoryNextArrayList.add(orgSummarizingMap.get("orgAdjustHistoryNext"));
                }
            }
			
			map.put("orgAdjustHistoryArrayList", orgAdjustHistoryArrayList);
			map.put("orgAdjustHistoryNextArrayList", orgAdjustHistoryNextArrayList);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return map;
	}
	
	/**
	 * 存储指标评估记录
	 * @param scoreValueFinal 分值
	 * @param orgId 组织ID
	 * @param companyId 公司ID
	 * @param timePeriodMapAll 时间区间集合
	 * @param orgAdjustHistoryListMapAll 组织记录MAP集合
	 * @param orgAdjustHistoryListAll 组织记录集合
	 * @param alarmPlanAllMap 告警MAP
	 * @param alarmScenario 公式日志
	 * @param assessPlanId 评估计划ID
	 * @param isAfreshSummarizing 是否重新计算
	 * @param orgAdjustHistoryAllMap 组织记录MAP
	 * @param orgAdjustHistoryByIdMapAll 组织通过ID
	 * @param orgCalculateFormula 组织计算公式
	 * @return HashMap<String, OrgAdjustHistory>
	 * @author 金鹏祥
	 * */
	@Transactional
	public HashMap<String, OrgAdjustHistory> getOrgSummarizing(
			double scoreValueFinal, 
			String orgId, 
			String companyId, 
			HashMap<String, TimePeriod> timePeriodMapAll,
			HashMap<String, ArrayList<OrgAdjustHistory>> orgAdjustHistoryListMapAll,
			List<OrgAdjustHistory> orgAdjustHistoryListAll,
			HashMap<String, AlarmPlan> alarmPlanAllMap,
			String alarmScenario,
			String assessPlanId,
			boolean isAfreshSummarizing,
			HashMap<String, OrgAdjustHistory> orgAdjustHistoryAllMap,
			HashMap<String, OrgAdjustHistory> orgAdjustHistoryByIdMapAll,
			String orgCalculateFormula){
		
		OrgAdjustHistory orgAdjustHistory = null;
		OrgAdjustHistory orgAdjustHistoryNext = null;
		HashMap<String, OrgAdjustHistory> OrgAdjustHistoryMap = new HashMap<String, OrgAdjustHistory>();
		
		try {
			
			SysOrganization sysOrganization = new SysOrganization();
			sysOrganization.setId(companyId);
			
			SysOrganization org = new SysOrganization();
			org.setId(orgId);
			
			if(isAfreshSummarizing){
				orgAdjustHistory = orgAdjustHistoryAllMap.get(orgId);
				if(null == orgAdjustHistory){
					orgAdjustHistory = new OrgAdjustHistory();
					orgAdjustHistory.setId(Identities.uuid());
					orgAdjustHistory.setIsLatest("0");
				}
			}else{
				orgAdjustHistory = new OrgAdjustHistory();
				orgAdjustHistory.setId(Identities.uuid());
				orgAdjustHistory.setIsLatest("0");
			}
			
			orgAdjustHistory.setOrganization(org);
			orgAdjustHistory.setAdjustType("0");
			
			if(!isAfreshSummarizing){
				orgAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
            }
			
			if(null == orgAdjustHistory.getAdjustTime()){
				orgAdjustHistory.setAdjustTime(TimeUtil.getCurrentTime());
			}
			
			double parentRiskLevel = this.getOrgParentRiskLevel(orgAdjustHistoryListAll, orgId, orgAdjustHistory.getAdjustTime().toString());
			
			if(parentRiskLevel !=  -1){
				if(scoreValueFinal > parentRiskLevel){
					//up：向上；
					orgAdjustHistory.setEtrend("up");
				}else if(scoreValueFinal < parentRiskLevel){
					//down：向下；
					orgAdjustHistory.setEtrend("down");
				}else if(scoreValueFinal == parentRiskLevel){
					//flat：水平
					orgAdjustHistory.setEtrend("flat");
				}
			}
			
			orgAdjustHistory.setTimePeriod(timePeriodMapAll.get(Times.getYear() + "mm" + Times.getMonth()));
			orgAdjustHistory.setRiskStatus(scoreValueFinal);
			
			String assessementStatus = "";
			
			if(scoreValueFinal != 0){
				DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(
						alarmPlanAllMap.get(alarmScenario), scoreValueFinal);
	    		if(dict != null){
	    			assessementStatus = dict.getValue();
	    		}
			}
			
			orgAdjustHistory.setAssessementStatus(assessementStatus);
			orgAdjustHistory.setCompanyId(sysOrganization);
			
			RiskAssessPlan assessPlan = new RiskAssessPlan();
			assessPlan.setId(assessPlanId);
			orgAdjustHistory.setRiskAssessPlan(assessPlan);
			orgAdjustHistory.setCalculateFormula(orgCalculateFormula);
			
			orgAdjustHistoryNext = this.getOrgNextRiskLevel(
					orgAdjustHistoryListAll, orgId, orgAdjustHistory.getAdjustTime().toString(), orgAdjustHistoryByIdMapAll, scoreValueFinal);
			
			OrgAdjustHistoryMap.put("orgAdjustHistory", orgAdjustHistory);
			OrgAdjustHistoryMap.put("orgAdjustHistoryNext", orgAdjustHistoryNext);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return OrgAdjustHistoryMap;
	}
	
	/**
	 * 更新组织评估记录is_latest=1为本次最新值
	 * @param assessPlanId 评估计划ID
	 * @param isLatest 是否更新最新状态
	 * @return VOID
	 * @author 金鹏祥
	 * */
	public void updateOrgAdjustHistoryIsLatestIsOne(String assessPlanId, boolean isLatest){
		List<OrgAdjustHistory> list = o_orgAdjustHistoryBO.findOrgAdjustHistoryByAssessPlanIdAllList(assessPlanId);
		ArrayList<String> idsArrayList = new ArrayList<String>();
		if(list.size() != 0){
			for (OrgAdjustHistory orgAdjustHistory : list) {
				idsArrayList.add(orgAdjustHistory.getOrganization().getId());
			}
			if(isLatest){
				o_orgAdjustHistoryBO.updateOrgAdjustHistory(idsArrayList);
				o_orgAdjustHistoryBO.updateOrgAdjustHistoryByAssessPlanId(assessPlanId);
			}
		}
	}
	
	/**
	 * 组织通过录入具体时间、风险ID,得到此风险上一区间值
	 * @param orgAdjustHistoryListAll 组织记录集合
	 * @param orgId 组织ID
	 * @param adjustTime 组织adjustTime时间
	 * @return double
	 * @author 金鹏祥
	 * */
	public double getOrgParentRiskLevel(List<OrgAdjustHistory> orgAdjustHistoryListAll, String orgId, String adjustTime){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, Double> adjustTimeStatusMap = new HashMap<Long, Double>();
		long[] maxTime = null;
		double parentRiskLevel = 0l;
		
		for (OrgAdjustHistory orgAdjustHistory : orgAdjustHistoryListAll) {
			if(orgAdjustHistory.getOrganization().getId().equalsIgnoreCase(orgId)){
				if(!orgAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							orgAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(orgAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
							orgAdjustHistory.getRiskStatus());
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
	 * @param orgAdjustHistoryListAll 组织记录集合
	 * @param orgId 组织ID
	 * @param adjustTime 组织adjustTime
	 * @param orgAdjustHistoryListMapAll 组织记录MAP集合
	 * @param riskLevel 组织分值
	 * @return OrgAdjustHistory
	 * @author 金鹏祥
	 * */
	@Transactional
	public OrgAdjustHistory getOrgNextRiskLevel(List<OrgAdjustHistory> orgAdjustHistoryListAll, 
			String orgId, String adjustTime, HashMap<String, OrgAdjustHistory> orgAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (OrgAdjustHistory orgAdjustHistory : orgAdjustHistoryListAll) {
			if(orgAdjustHistory.getOrganization().getId().equalsIgnoreCase(orgId)){
				if(!orgAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							orgAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(orgAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
							orgAdjustHistory.getRiskStatus() + "--" + orgAdjustHistory.getId());
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
		
		OrgAdjustHistory history = orgAdjustHistoryListMapAll.get(nextId);
		
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
	 * @param orgAdjustHistoryListAll 组织集合
	 * @param orgId 组织ID
	 * @param adjustTime 组织adjustTime
	 * @param orgAdjustHistoryListMapAll 组织MAP集合
	 * @param riskLevel 组织分值
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean orgNextRiskLevel(List<OrgAdjustHistory> orgAdjustHistoryListAll, 
			String orgId, String adjustTime, HashMap<String, OrgAdjustHistory> orgAdjustHistoryListMapAll, double riskLevel){
		adjustTime = CalculateUtil.getTime(adjustTime);
		ArrayList<Long> adjustTimeArrayList = new ArrayList<Long>();
		ArrayList<Long> adjustTimeMaxArrayList = new ArrayList<Long>();
		HashMap<Long, String> adjustTimeStatusMap = new HashMap<Long, String>();
		String nextId = "";
		
		long[] maxTime = null;
		double nextRiskLevel = 0l;
		
		for (OrgAdjustHistory orgAdjustHistory : orgAdjustHistoryListAll) {
			if(orgAdjustHistory.getOrganization().getId().equalsIgnoreCase(orgId)){
				if(!orgAdjustHistory.getAdjustTime().toString().equalsIgnoreCase(adjustTime)){
					adjustTimeArrayList.add(Long.parseLong(
							orgAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")));
					adjustTimeStatusMap.put(
							Long.parseLong(orgAdjustHistory.getAdjustTime().toString().replace(".0", "").replace(" ", "").replace("-", "").replace(":", "")), 
							orgAdjustHistory.getRiskStatus() + "--" + orgAdjustHistory.getId());
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
		
		OrgAdjustHistory history = orgAdjustHistoryListMapAll.get(nextId);
		
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
		
		o_orgAdjustHistoryBO.mergeOrgAdjustHistory(history);
		
		return true;
	}
}