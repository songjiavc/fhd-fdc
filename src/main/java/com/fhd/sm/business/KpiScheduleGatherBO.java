package com.fhd.sm.business;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.fdc.utils.Contents;

@Service
public class KpiScheduleGatherBO {
	
	
	@Autowired
	private JBPMBO o_jbpmBO;
	
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	
	@Autowired
    private ChartForEmailBO o_chartForEmailBO;
	
	
	private static Log logger = LogFactory.getLog(KpiScheduleGatherBO.class);
	
	
	
	/**实际值采集审批后汇总计算
	 * @param resultIds 采集结果id
	 * @throws ParseException
	 */
	@Transactional
	public void mergeFinishGatherSummarizing(final String resultIds) throws ParseException{
		if(StringUtils.isNotBlank(resultIds)){
			Thread calcThread = new Thread(new Runnable() {
				public void run() {
					try {
						KpiScheduleGatherCalcBO calcBO = ContextLoader.getCurrentWebApplicationContext().getBean(KpiScheduleGatherCalcBO.class);
						calcBO.mergeGatherResultSummarizing(resultIds,Contents.KPI_GATHER_VALUE_FINISH_TYPE);
					} catch (Exception e) {
						logger.error("实际值采集审批后汇总计算异常:["+e.toString());
						e.printStackTrace();
					}
				}
			});
			calcThread.start();
		}
	}
	
	/**目标值采集审批后汇总计算
	 * @param resultIds 采集结果id
	 * @throws ParseException
	 */
	@Transactional
	public void mergeTargetGatherSummarizing(final String resultIds) throws ParseException{
		if(StringUtils.isNotBlank(resultIds)){
			Thread calcThread = new Thread(new Runnable() {
				public void run() {
					try {
						KpiScheduleGatherCalcBO calcBO = ContextLoader.getCurrentWebApplicationContext().getBean(KpiScheduleGatherCalcBO.class);
						calcBO.mergeGatherResultSummarizing(resultIds,Contents.KPI_GATHER_VALUE_TARGET_TYPE);
					} catch (Exception e) {
						logger.error("目标值采集审批后汇总计算异常:["+e.toString());
						e.printStackTrace();
					}
				}
			});
			calcThread.start();
		}
	}
	
	
	/**采集值最后的汇总
	 * @param planId 计划id
	 * @param executionId 执行id
	 */
	@Transactional
	public void mergeGatherTotalSummarizing(String planId,String executionId){
		if(StringUtils.isNotBlank(planId)) {    				
			//更新计划状态为已完成
			KpiTargetValueGatherPlanBO kpiTargetValueGatherPlanBO = ContextLoader.getCurrentWebApplicationContext().getBean(KpiTargetValueGatherPlanBO.class);
			kpiTargetValueGatherPlanBO.changeWorkState(planId,Contents.DEAL_STATUS_FINISHED);
		}
	}

    
    /**目标采集审批节点提交
     * @param isSubmit '保持或提交 true or false'
     * @param isPass '是否同意,yes or no'
     * @param idea '审批意见'
     * @param executionId '工作流程ID'
     * @throws ParseException
     */
	@Transactional
    public void mergeGatherResultApproval(boolean isSubmit,String isPass,String idea,String executionId,String valueType) throws ParseException{
    	Map<String, Object> variables = new HashMap<String, Object>();
    	variables.put("path", isPass);
    	variables.put("examineApproveIdea", idea);
    	if(isSubmit){
	        o_jbpmBO.doProcessInstance(executionId, variables);
    		if("no".equals(isPass)){//不同意需要发送邮件
    			String jsUrl = "";
    			String taskName = "";
    			Object resultIds = o_jbpmOperate.getVariable(executionId,"resultIds");
    			String resultId[] = String.valueOf(resultIds).split(",");
    			List<String> resultIdList = Arrays.asList(resultId);
    			Task task = o_jbpmBO.findTaskByExecutionId(executionId);
    			String empId = task.getAssignee();
    			if("target".equalsIgnoreCase(valueType)) {
    				taskName = "目标值采集数据收集-审批不同意";
    				jsUrl = "FHD.view.kpi.bpm.targetgather.KpiGatherRecorded";
                }else if ("finish".equalsIgnoreCase(valueType)){
                	taskName = "实际值采集数据收集-审批不同意";
                	jsUrl = "FHD.view.kpi.bpm.finishgather.KpiGatherRecorded";
                }
                String taskId = o_jbpmBO.findTaskByExecutionId(executionId).getId();
    			String weburl = o_chartForEmailBO.findWebUrl(valueType, executionId, taskId, empId, jsUrl);
    			o_chartForEmailBO.sendKpiGatherEmail(empId, resultIdList,taskName,weburl,idea);
    		} 
    	}
    	
    }
    
}
