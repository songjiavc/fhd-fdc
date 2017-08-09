package com.fhd.ra.business.assess.summarizing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.impl.calendar.AnnualCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.comm.business.formula.FormulaCalculateBO;
import com.fhd.core.utils.Identities;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.util.StrUtil;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.sys.business.assess.WeightSetBO;
import common.Logger;

@Service
public class AssessRiskTaskBO {
	
	private static final Logger log = Logger.getLogger(AssessRiskTaskBO.class);
	
	@Autowired
	private FormulaCalculateBO o_formulaCalculateBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	
	/**
	 * 触发定时采集频率
	 * @return boolean
	*/
	@Transactional
	public boolean saveRiskScore(){
		try {
			HashMap<String, ArrayList<String>> timePeriodByCurrentTimeMapAll = o_timePeriodBO.findTimePeriodByCurrentTimeMapAll();
			List<Risk> riskListAll = o_riskBO.findRiskListAll();
			String alarmScenario = "";
//			if(o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid()).getAlarmScenario() != null){
//	    		alarmScenario = o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid()).getAlarmScenario().getId();
//	    	}
			HashMap<String, TimePeriod>  timePeriodMapAll = o_timePeriodBO.findTimePeriodMapAll();
			HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap();
			ArrayList<String> riskTempArrayList = new ArrayList<String>();
			ArrayList<String> riskAdjustHistoryIdTempArrayList = new ArrayList<String>();
			ArrayList<RiskAdjustHistory> riskAdjustHistoryList = new ArrayList<RiskAdjustHistory>();
			String result = "";
			for (Risk risk : riskListAll) {
				if(null != risk.getGatherFrequence()){//是否设置采集频率
				    List<RiskAdjustHistory> riskAdjustHistoryListAll = o_riskAdjustHistoryBO.findRiskAdjustHistoryByAssessAllList(risk.getId());
					String isUse = risk.getIsUse();//是否启用
					if("0yn_y".equalsIgnoreCase(isUse)){
//						Date date = this.calculateNextExecuteTime(risk);//得到下一期时间
//						String frequencyTime = TimeUtil.getFrequencyTime(date);
						String currentTime = TimeUtil.getCurrentTimeStr();//当前时间
//						if(frequencyTime.equalsIgnoreCase(currentTime)){//下一期时间、当前时间如果相等
							if(null != risk.getCompany()){//公司不为空
								if(risk.getGatherFrequence() == null){
									continue;
								}
								
								String timePeriodId = this.getTimePeriodId(
										risk.getGatherFrequence().getId(), currentTime, timePeriodByCurrentTimeMapAll);//得到时间区间维ID
								
								if(null == timePeriodId){
									continue;
								}
								
								try {
									result = o_formulaCalculateBO.calculate(
											risk.getId(), risk.getName(), "risk", risk.getFormulaDefine(), 
											timePeriodId, risk.getCompany().getId());//得到风险水平
								} catch (Exception e) {
									result = e.getMessage();
								}
								
								if (StringUtils.isNotBlank(result)) {
									if(!StrUtil.isChinese(result)){//如果不是错误中午描述信息
										double riskScore = Double.parseDouble(result);//得到自动计算风险水平分
										String riskAdjustHistoryId = Identities.uuid();
										alarmScenario = o_weightSetBO.findWeightSetAll(risk.getCompany().getId()).getAlarmScenario().getId();
										RiskAdjustHistory riskAdjustHistory = o_riskAdjustHistoryBO.getRiskAdjustHistory(riskAdjustHistoryId, 
												timePeriodMapAll, alarmPlanAllMap, riskAdjustHistoryListAll, risk, alarmScenario, riskScore);//存储风险记录
										
										riskAdjustHistoryList.add(riskAdjustHistory);
										riskTempArrayList.add(risk.getId());//存放临时风险ID
										riskAdjustHistoryIdTempArrayList.add(riskAdjustHistoryId);//存放临时记录ID
									}else{
										log.info("=====" + risk.getName() + "自动计算未算出风险水平," + result + "======");
									}
								}
							}else{
								log.info("=====" + risk.getName() + "公司ID为空======");
							}
//						}
					}
				}
			}
			
			o_riskAdjustHistoryBO.saveRiskAdjustHistory(riskAdjustHistoryList);//批量添加风险记录
			o_riskAdjustHistoryBO.updateRiskAdjustHistoryZeroByInRiskId(riskTempArrayList);//更新该风险集合最新值为0
			o_riskAdjustHistoryBO.updateRiskAdjustHistoryOneByInRiskId(riskAdjustHistoryIdTempArrayList);//刷新最新记录ID的风险最新值为1
		} catch (Exception e) {
			log.error("==========" + e.getMessage() + "==========");
			return false;
		}
		
		return true;
	}
	
	/**
     * 根据风险采集频率CRON表达式计算下次执行时间.
     * @param risk 风险实体
     * @return Date
     */
    public Date calculateNextExecuteTime(Risk risk) throws ParseException {
        CronTrigger trigger = new CronTrigger();
        if(StringUtils.isNotBlank(risk.getGatherDayFormulr())){
            trigger.setCronExpression(risk.getGatherDayFormulr());
        }else {
            return null;
        }
        AnnualCalendar cal = new AnnualCalendar();
        trigger.computeFirstFireTime(cal);
        return trigger.getNextFireTime();
    }
    
    /**
     * 通过频率得到区间维度ID
     * @param frequency 实践趋势
     * @param currentTime 当前时间
     * @param timePeriodByCurrentTimeMapAll 时间区间集合
     * @return String
     * @author 金鹏祥
     * */
    public String getTimePeriodId(String frequency, String currentTime, 
    		HashMap<String, ArrayList<String>> timePeriodByCurrentTimeMapAll){
     	ArrayList<String> arrayList = timePeriodByCurrentTimeMapAll.get(frequency);
     	if(null != arrayList){
     		Date time = TimeUtil.getTime(currentTime);
        	for (String string : arrayList) {
    			String id = string.split("--")[0].toString();
    			Date startTime = TimeUtil.getTime(string.split("--")[1].toString());
    			Date endTime = TimeUtil.getTime(string.split("--")[2].toString());
    			
    			if(startTime.getTime() <= time.getTime()){
    				if(endTime.getTime() >= time.getTime()){
    					return id;
    				}
    			}
    		}
     	}
    	
    	return null;
    }
    
    public static void main(String[] args) {
    	try {
    		CronTrigger trigger = new CronTrigger();
            if(StringUtils.isNotBlank("0 0 0 * * ?")){
                trigger.setCronExpression("0 0 0 1 * ?");
            }
            AnnualCalendar cal = new AnnualCalendar();
            trigger.computeFirstFireTime(cal);
            System.out.println( TimeUtil.getFrequencyTime(trigger.getNextFireTime()));
           
		} catch (Exception e) {
			// ENDO: handle exception
		}
    	
	}
}
