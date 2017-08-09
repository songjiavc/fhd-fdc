package com.fhd.sm.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.KpiMemo;
import com.fhd.sm.business.ChartForEmailBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.business.KpiScheduleGatherBO;
import com.fhd.sm.web.form.KpiMemoForm;

@Controller
public class KpiScheduleGatherControl {
	
	@Autowired
	private KpiScheduleGatherBO o_kpiScheduleGatherBO;
	@Autowired
	private KpiGatherResultBO o_kpiGatherResultBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;	
    @Autowired
    private JBPMBO o_jbpmBO; 
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
    @Autowired
    private ChartForEmailBO o_chartForEmailBO;
    
    
    
    /**
     * 根据采集结果id查询备注信息
     * @param id 采集结果ID
     * @return 当前采集结果的备注
     */
    @ResponseBody
	@RequestMapping(value="/kpi/kpi/findmemoinfobykgrid.f")
	public Map<String,Object> findMemoInfoByKgrId(String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		KpiGatherResult kgr = o_kpiGatherResultBO.findKpiGatherResultById(id);
		// 备注信息
		map.put("isMemo",kgr.getIsMemo());
		List<Object[]> objList = o_kpiMemoBO.findMemoByKgrId(kgr.getId());
		KpiMemoForm memoForm = new KpiMemoForm(objList);
		map.put("memoStr",memoForm.getMemoStr());
		return map;
	}
    
    
    /**
     * 显示所有待采集的指标列表
     * @param query
     * @param sort
     * @param gatherResultIdList
     * @return 待采集的目标
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/showGatherResultList.f")
    public Map<String, Object> findGatherResultList(String items,String query) {
		JSONObject jsonObject  = JSONObject.fromObject(items);
		String executionId = jsonObject.getString("executionId");
    	Object resultIds = o_jbpmOperate.getVariable(executionId,"resultIds");
		String resultId[] = String.valueOf(resultIds).split(",");
		List<String> resultIdList = Arrays.asList(resultId);
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
    	Map<String,KpiGatherResult> resultMap = new HashMap<String, KpiGatherResult>();
    	if(resultIdList.size()>0){
    		List<KpiGatherResult> resultList = o_kpiGatherResultBO.findKpiGatherResultByIds(resultIdList);
    		for (KpiGatherResult kpiGatherResult : resultList) {
    			resultMap.put(kpiGatherResult.getId(), kpiGatherResult);
			}
    	}
    	for(int i=0;i<resultId.length;i++){
    		KpiGatherResult kgr = resultMap.get(resultId[i]);
			if(kgr != null) {
				Kpi kpi = kgr.getKpi();
				Map<String,Object> resultItemMap = new HashMap<String,Object>();
				//指标名称
				resultItemMap.put("id", kgr.getId());
				// 指标ID
				resultItemMap.put("kpiId", kpi.getId());
				resultItemMap.put("kpiName",kpi.getName());
				// 最大值
				resultItemMap.put("maxValue", kpi.getMaxValue());
				// 最小值
				resultItemMap.put("minValue", kpi.getMinValue());
				//采集说明
				if(StringUtils.isNotBlank(kpi.getGatherDesc())){
					resultItemMap.put("gatherDesc",kpi.getGatherDesc());
				}
				KpiGatherResult preKpiGatherResult = o_kpiGatherResultBO.findPreGatherResultByCurrent(kgr);
				// 单位
				if(null!=kpi.getUnits()){
				    resultItemMap.put("units",kpi.getUnits().getName());
				}
				if(null==preKpiGatherResult){
					resultItemMap.put("preTargetValue","");
					resultItemMap.put("preFinishValue","");
					resultItemMap.put("assessmentStatus","");
				}else{
					//上期目标值
					resultItemMap.put("preTargetValue",preKpiGatherResult.getTargetValue());
					//上期完成值
					resultItemMap.put("preFinishValue",preKpiGatherResult.getFinishValue());
					
					// 上期状态
					if(preKpiGatherResult.getAssessmentStatus() != null){
						resultItemMap.put("assessmentStatus", preKpiGatherResult.getAssessmentStatus().getValue());
					} else {
						resultItemMap.put("assessmentStatus","");
					}
				}
				resultItemMap.put("kgrId", kgr.getId());
				//当期目标值
				if("0".equals(kgr.getTargetSetStatus())){
					resultItemMap.put("targetValue", kgr.getTempTargetValue());
				} else {
					if(null!=kpi.getModelValue()){
						resultItemMap.put("targetValue", kpi.getModelValue());
					}else{
						resultItemMap.put("targetValue", kgr.getTargetValue());
					}
				}
				//时间区间
				if(kgr.getTimePeriod() != null){
					resultItemMap.put("timePeriod", kgr.getTimePeriod().getTimePeriodFullName());
				}
				// 备注信息
				resultItemMap.put("isMemo",kgr.getIsMemo());
			   
				datas.add(resultItemMap);
		  }
    	}
    	
    	Map<String,KpiMemo> memoMap = o_kpiMemoBO.findMemoByKpiGatherIds(resultIdList);
    	for(Map<String, Object> item : datas){
    		String kgrId = String.valueOf(item.get("kgrId"));
    		if(memoMap.containsKey(kgrId))
    		{
    			KpiMemo kpiMemo = memoMap.get(kgrId);
    			KpiMemoForm memoForm = new KpiMemoForm(kpiMemo);
        		item.put("memoStr",memoForm.getMemoStr());
    		}
    		
    	}
    	
		map.put("datas",datas);
		return map;
    }
    
    /**
     * 显示待采集的完成值列表
     * @param items
     * @param query
     * @return 指标列表
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/showFinishGatherList.f")
    public Map<String, Object> findFinishGatherList(String items,String query) {
		JSONObject jsonObject  = JSONObject.fromObject(items);
		String executionId = jsonObject.getString("executionId");
    	Object resultIds = o_jbpmOperate.getVariable(executionId,"resultIds");
		String resultId[] = String.valueOf(resultIds).split(",");
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
    	List<String> resultIdList = Arrays.asList(resultId);
    	Map<String,KpiGatherResult> resultMap = new HashMap<String, KpiGatherResult>();
    	if(resultIdList.size()>0){
    		List<KpiGatherResult> resultList = o_kpiGatherResultBO.findKpiGatherResultByIds(resultIdList);
    		for (KpiGatherResult kpiGatherResult : resultList) {
    			resultMap.put(kpiGatherResult.getId(), kpiGatherResult);
			}
    	}
    	for(int i=0;i< resultId.length;i++) {
    		KpiGatherResult kgr = resultMap.get(resultId[i]);
    		if(kgr != null) {
    			Kpi kpi = kgr.getKpi();
    			Map<String,Object> resultItemMap = new HashMap<String,Object>();
    			KpiGatherResult preKpiGatherResult = o_kpiGatherResultBO.findPreGatherResultByCurrent(kgr);
    			//指标名称
    			resultItemMap.put("id", kgr.getId());
    			// 指标ID
    			resultItemMap.put("kpiId", kpi.getId());
    			//指标名称
    			resultItemMap.put("kpiName",kpi.getName());
    			// 最大值
    			resultItemMap.put("maxValue", kpi.getMaxValue());
    			// 最小值
    			resultItemMap.put("minValue", kpi.getMinValue());
    			//采集说明
    			if(StringUtils.isNotBlank(kpi.getGatherDesc())){
    				resultItemMap.put("gatherDesc",kpi.getGatherDesc());
    			}
    			// 目标值
    			if(null!=kpi.getModelValue()){
    				if("0".equals(kgr.getTargetSetStatus())){
    					resultItemMap.put("targetValue", kgr.getTempTargetValue());
    				}else if("1".equals(kgr.getTargetSetStatus())){
    					resultItemMap.put("targetValue", kgr.getTargetValue());
    				}else{
    					resultItemMap.put("targetValue", kpi.getModelValue());
    				}
    			}else{
    				if("0".equals(kgr.getTargetSetStatus())){
    					resultItemMap.put("targetValue", kgr.getTempTargetValue());
    				}else{
    					resultItemMap.put("targetValue", kgr.getTargetValue());
    				}
    			}
    			// 上期状态
    			if(preKpiGatherResult!= null && preKpiGatherResult.getAssessmentStatus() != null){
    				resultItemMap.put("assessmentStatus", preKpiGatherResult.getAssessmentStatus().getValue());
    			} else {
    				resultItemMap.put("assessmentStatus","");
    			} 
    			// 上期采集结果ID(生成历史数据用)
//    		    if(null != preKpiGatherResult)  {
//    		    	resultItemMap.put("kgrId", preKpiGatherResult.getId());
//    		    	
//    		    } else {
//    		    	resultItemMap.put("kgrId",null);
//    		    }
    			resultItemMap.put("kgrId", kgr.getId());
    			// 单位
    			if(kpi.getUnits() != null){
    				resultItemMap.put("units",kpi.getUnits().getName());
    			} else {
    				resultItemMap.put("units","");
    			}
    			
    			if("0".equals(kgr.getFinishSetStatus())){
    				resultItemMap.put("finishValue", kgr.getTempFinishValue());
    			} else {
    				resultItemMap.put("finishValue", kgr.getFinishValue());
    			}
    			//时间区间
    			if(kgr.getTimePeriod() != null){
    				resultItemMap.put("timePeriod", kgr.getTimePeriod().getTimePeriodFullName());
    			}
    			// 备注信息
    			resultItemMap.put("isMemo",kgr.getIsMemo());
    			datas.add(resultItemMap);
    		}
    	}
    	
    	Map<String,KpiMemo> memoMap = o_kpiMemoBO.findMemoByKpiGatherIds(resultIdList);
    	for(Map<String, Object> item : datas){
    		String kgrId = String.valueOf(item.get("kgrId"));
    		if(memoMap.containsKey(kgrId)){
    			KpiMemo kpiMemo = memoMap.get(kgrId);
    			KpiMemoForm memoForm = new KpiMemoForm(kpiMemo);
        		item.put("memoStr",memoForm.getMemoStr());
    		}
    	}
    	
		map.put("datas",datas);
		return map;
    }
    /**
     * 保存完成值列表
     * @param param
     * @param bpmStart
     * @param executionId
     * @param examinePerson
     * @return 保存成功或失败
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/saveFinishGatherGatherList.f")
    public Map<String, Object> mergeFinishGatherGatherList(String param,Boolean bpmStart,String executionId,String examinePerson) {
    	Map<String, Object> result = new HashMap<String, Object>();
        JSONArray jsonArray = JSONArray.fromObject(param);   
        Map<String, Object> variables = new HashMap<String, Object>();
        List<String> resultIdList = new ArrayList<String>();
        for(int i = 0;i<jsonArray.size();i++) {
        	JSONObject jsonObject = jsonArray.getJSONObject(i);
        	String gatherResultId = jsonObject.getString("id");
        	resultIdList.add(gatherResultId);
        }
        Map<String,KpiGatherResult> resultMap = new HashMap<String, KpiGatherResult>();
    	if(resultIdList.size()>0){
    		List<KpiGatherResult> resultList = o_kpiGatherResultBO.findKpiGatherResultByIds(resultIdList);
    		for (KpiGatherResult kpiGatherResult : resultList) {
    			resultMap.put(kpiGatherResult.getId(), kpiGatherResult);
			}
    	}
    	
    	for(int i = 0;i<jsonArray.size();i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String gatherResultId = jsonObject.getString("id");
			KpiGatherResult kgr = resultMap.get(gatherResultId);
			if((jsonObject.containsKey("kpivalue")&&!"null".equals(jsonObject.getString("kpivalue")))) {
				kgr.setTempFinishValue(Double.parseDouble(jsonObject.getString("kpivalue")));
			} else {
				kgr.setTempFinishValue(null);
			}  			
			//目标值状态为保存
			kgr.setFinishSetStatus("0");
			o_kpiGatherResultBO.mergeKpiGatherResult(kgr);
		}
    	// 提交按钮点击事件
    	if(bpmStart) {
			JSONArray examiners = JSONArray.fromObject(examinePerson);
			//审批人id
			String examineId = examiners.getJSONObject(0).getString("id");
			variables.put("examineId",examineId);
			o_jbpmBO.doProcessInstance(executionId, variables);
			//发邮件给审批人
			String taskId = o_jbpmBO.findTaskByExecutionId(executionId).getId();
			
			if(o_chartForEmailBO.findIsSend()){
				o_chartForEmailBO.sendApprovalEmail("finish",executionId,taskId,examineId,resultMap,resultIdList);
			}

    	} 
		
    	result.put("success", true);
    	return result;
    }
	
    /**
     * 保存采集结果 根据bpmStart判断是否流转到下一节点
     * @param param
     * @param bpmStart
     * @param executionId
     * @param examinePerson
     * @return 保存结果
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/saveGatherResultList.f")
    public Map<String, Object> mergeGatherResultList(String param,Boolean bpmStart,String executionId,String examinePerson){
    	Map<String, Object> result = new HashMap<String, Object>();
        JSONArray jsonArray = JSONArray.fromObject(param);   
        Map<String, Object> variables = new HashMap<String, Object>();
    	// 提交按钮点击事件
        List<String> resultIdList = new ArrayList<String>();
        for(int i = 0;i<jsonArray.size();i++) {
        	JSONObject jsonObject = jsonArray.getJSONObject(i);
        	String gatherResultId = jsonObject.getString("id");
        	resultIdList.add(gatherResultId);
        }
        Map<String,KpiGatherResult> resultMap = new HashMap<String, KpiGatherResult>();
    	if(resultIdList.size()>0){
    		List<KpiGatherResult> resultList = o_kpiGatherResultBO.findKpiGatherResultByIds(resultIdList);
    		for (KpiGatherResult kpiGatherResult : resultList) {
    			resultMap.put(kpiGatherResult.getId(), kpiGatherResult);
			}
    	}
    	
    	for(int i = 0;i<jsonArray.size();i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String gatherResultId = jsonObject.getString("id");
			KpiGatherResult kgr = resultMap.get(gatherResultId);
			if((jsonObject.containsKey("kpivalue")&&!"null".equals(jsonObject.getString("kpivalue")))) {
				kgr.setTempTargetValue(Double.parseDouble(jsonObject.getString("kpivalue")));
			} else {
				kgr.setTempTargetValue(null);
			}
			//目标值状态为提交
			kgr.setTargetSetStatus("0");
			o_kpiGatherResultBO.mergeKpiGatherResult(kgr);
		}
    	if(bpmStart) {
			
			JSONArray examiners = JSONArray.fromObject(examinePerson);
			String examineId = examiners.getJSONObject(0).getString("id");
			variables.put("examineId", examineId);
			o_jbpmBO.doProcessInstance(executionId, variables);
			//发邮件给审批人
			String taskId = o_jbpmBO.findTaskByExecutionId(executionId).getId();
			if(o_chartForEmailBO.findIsSend()){
				o_chartForEmailBO.sendApprovalEmail("target",executionId,taskId,examineId,resultMap,resultIdList);
			}

    	}
		
    	result.put("success", true);
    	return result;
    }
    
	
    
    /**
     * 目标采集审批节点提交
     * @param items{isPass:'是否同意,yes or no',idea:'审批意见',executionId:'工作流程ID',isSubmit:'保持或提交 true or false' }
     * @return
     * @throws ParseException 
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/kpigatherresultapproval.f")
    public Map<String, Object> mergeKpiGatherResultApproval(String items) throws ParseException{
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	if(StringUtils.isNotBlank(items)){
    		JSONObject params = JSONObject.fromObject(items);
    		String isPass = params.getString("isPass");
    		String idea = params.getString("idea");
    		String executionId = params.getString("executionId");
    		Boolean isSubmit = params.getBoolean("isSubmit");
    		String  valueType = params.getString("valueType");
    		o_kpiScheduleGatherBO.mergeGatherResultApproval(isSubmit,isPass, idea, executionId,valueType);
    	}
    	map.put("success", true);
    	return map;
    }
    
    
}
