package com.fhd.sm.business;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;

@Service
public class KpiGatherResultQuarterBO {


	@Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
	
	@Autowired
	private KpiGatherReultUtilsBO o_kpiGatherReultUtilsBO;
	
	@Autowired
    private KpiBO o_kpiBO;
	
	/**
	 * 查询该指标全部数据存放到MAP中
	 * @param params 当前点击的类型与值
	 * @param kpiId 指标ID
	 * @return 指标数据Map
	 */
	public String getParams(String params, String kpiId){
		//查询该指标全部数据存放到MAP中
		Map<String, String> kpiGatherResultMap = null;
    	//月
		
		String[] whereIn = null;
		
		String yearKey = "";
		String yearTargetValue = "";
		String yearFinishValue = "";
		String yearAssessmentValue = "";
		
		String key = "";
		String targetValue = "";
		String finishValue = "";
		String assessmentValue = "";
		
		//得到当前点击的类型与值
		key = params.split(":")[0].replace("[{\"", "").replace("\"", "");
		finishValue = params.split(":")[1].split(",")[0].replace("\"", "");
		targetValue = params.split(":")[1].split(",")[1];
		assessmentValue = params.split(":")[1].split(",")[2].replace("\"}]", "");
		
		int year = 0;
		if(params.indexOf('Q') != -1){
			//点击的是季度
			year = Integer.parseInt(params.split(":")[0].replace("[{\"", "").replace("\"", "").split("Q")[0]);
		
			kpiGatherResultMap = o_kpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiId, String.valueOf(year));
			kpiGatherResultMap = o_kpiGatherReultUtilsBO.filterMap(kpiGatherResultMap);
			
			yearKey = String.valueOf(year);
			
			//单条修改
			kpiGatherResultMap.put(key, key + "," + finishValue + "," + targetValue + "," + assessmentValue);
			
			//求季度
			List<Double> finishValueList = null;
			List<Double> targetValueList = null;
			List<Double> assessmentValueList = null;
			
			//得到指标实体
			Kpi kpi = o_kpiBO.findKpiById(kpiId);
			
			//得到年度汇总
			whereIn = new String[4];
			whereIn[0] = year + "Q1";
			whereIn[1] = year + "Q2";
			whereIn[2] = year + "Q3";
			whereIn[3] = year + "Q4";
			List<KpiGatherResult> list = o_kpiGatherResultBO.findKpiGatherResultListByTimePeriodId(kpiId, whereIn);
			
			if(list.size() >= 3 ){
				if(list.size() < 4){
					list = o_kpiGatherReultUtilsBO.addKpiGatherResultList(list, kpiGatherResultMap, key);
				}
				
				finishValueList = o_kpiGatherReultUtilsBO.addFinishValueList(list);
				targetValueList = o_kpiGatherReultUtilsBO.addTargetValueList(list);
				assessmentValueList = o_kpiGatherReultUtilsBO.addAssessmentValueList(list);
				
    			yearTargetValue = o_kpiGatherReultUtilsBO.getTargetValue(kpi, targetValueList);
    			yearFinishValue = o_kpiGatherReultUtilsBO.getFinishValue(kpi, finishValueList);
    			yearAssessmentValue = o_kpiGatherReultUtilsBO.getAssessmentValue(kpi, assessmentValueList);
    			//年度
    			kpiGatherResultMap.put(yearKey, yearKey + "," + yearFinishValue + "," + yearTargetValue + "," + yearAssessmentValue);
			}
				
			params = o_kpiGatherReultUtilsBO.getMapToParams(kpiGatherResultMap, params);
		}
    	return params;
	}
}