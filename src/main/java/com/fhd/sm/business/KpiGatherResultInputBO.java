package com.fhd.sm.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.kpi.KpiInfoDto;
import com.fhd.fdc.utils.Contents;

/**
 * 指标采集结果录入定时触发BO.
 * 触发时间表达式为：0 0 0 5 * ? --每月5号凌晨00:00:00触发
 * @author   吴德福
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-2-1		下午07:36:35
 */
@Service
public class KpiGatherResultInputBO {

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private JBPMBO o_jbpmBO;
    
    @Autowired
	private JBPMOperate o_jbpmOperate;	
    
    @Autowired
    private ChartForEmailBO o_chartForEmailBO;
    
    private static final Log logger = LogFactory.getLog(KpiGatherResultInputBO.class);
    
    
	/**
	 * 定时任务发起实际值和目标值采集
	 */
	@Transactional
    public Boolean generateKpiGatherResultInput() {
		List<Object[]> kpiList = o_kpiGatherResultBO.findAllKpiRelaInfo();
		doFlowProcess(kpiList,null);
		return true;
	}
    
    /**启动工作流程
     * @param kpiList指标对象集合
     */
    @SuppressWarnings("unchecked")
	@Transactional
	public void doFlowProcess(List<Object[]> kpiList,Map<String, Object> extraMap){
    	if(null!=kpiList&&kpiList.size()>0){
			Map<String,Map<String,Object>> finishMap = new HashMap<String, Map<String,Object>>();
			Map<String,Map<String,Object>> targetMap = new HashMap<String, Map<String,Object>>();
			for (Object[] object : kpiList){
            	String empId =  String.valueOf(object[0]);
            	String orgType = String.valueOf(object[6]);
            	String resultId = String.valueOf(object[8]);
            	if (null!=empId) {
            		if(Contents.GATHERDEPARTMENT.equals(orgType)){//采集人员
    	        		if(!finishMap.containsKey(empId)){//不包含empid
    	        				List<String> resultIdList = new ArrayList<String>();
    	        				resultIdList.add(resultId);
    	        				Map<String,Object> resultMap = new HashMap<String, Object>();
    	        				resultMap.put("resultIdList", resultIdList);
    	        				finishMap.put(empId, resultMap);
    	        			
    	        		}else{
    	        			Map<String,Object> resultMap = finishMap.get(empId);
    	        			List<String> resultIdList = (List<String>)resultMap.get("resultIdList");
    	        			resultIdList.add(resultId);
    	        		}
            		}else if(Contents.TARGETDEPARTMENT.equals(orgType)){
            			if(!targetMap.containsKey(empId)){//不包含empid
	        				List<String> resultIdList = new ArrayList<String>();
	        				resultIdList.add(resultId);
	        				Map<String,Object> resultMap = new HashMap<String, Object>();
	        				resultMap.put("resultIdList", resultIdList);
	        				targetMap.put(empId, resultMap);
	        			
	        		}else{
	        			Map<String,Object> resultMap = targetMap.get(empId);
	        			List<String> resultIdList = (List<String>)resultMap.get("resultIdList");
	        			resultIdList.add(resultId);
	        		}
            		}
            		
            	}
			}
			doStart(finishMap, Contents.KPI_GATHER_VALUE_FINISH_TYPE,extraMap);
			
			doStart(targetMap, Contents.KPI_GATHER_VALUE_TARGET_TYPE,extraMap);
		}
    }
    
    
    
    
    /**启动工作流
     * @param map 采集值map
     * @param valueType 值类型 finish:实际值 ,target:目标值 
     */
    @SuppressWarnings("unchecked")
	@Transactional
	private void doStart(Map<String,Map<String,Object>> map ,String valueType,Map<String, Object> extraMap){
    	if(null!=map && map.size()>0){
    		String jsUrl = "";
        	String entityType = "";
        	String taskName = "";
        	if(Contents.KPI_GATHER_VALUE_FINISH_TYPE.equals(valueType)){
        		entityType = "KpiFinishGatherResultInput";
        		if(null!=extraMap&&extraMap.containsKey("planName")&&null!=extraMap.get("planName")){
        		    taskName = String.valueOf(extraMap.get("planName"));
        		}else{
        		    taskName = "实际值采集数据收集";
        		}
        		jsUrl = "FHD.view.kpi.bpm.finishgather.KpiGatherRecorded";
        	}else if(Contents.KPI_GATHER_VALUE_TARGET_TYPE.equals(valueType)){
        		entityType = "KpiTargetGatherResultInput";
        		if(null!=extraMap&&extraMap.containsKey("planName")&&null!=extraMap.get("planName")){
                    taskName = String.valueOf(extraMap.get("planName"));
                }else{
                    taskName = "目标值采集数据收集";
                }
        		jsUrl = "FHD.view.kpi.bpm.targetgather.KpiGatherRecorded";
        	}
        	List<KpiInfoDto> kpiInfoList = new ArrayList<KpiInfoDto>();
        	Iterator<Map.Entry<String, Map<String,Object>>> iter = map.entrySet().iterator();
            while(iter.hasNext()){
            	Map.Entry<String,Map<String,Object>> entry = iter.next();
            	String empId = entry.getKey();
            	Map<String,Object> resultMap = entry.getValue();
            	List<String> resultIdList =(List<String>)resultMap.get("resultIdList");
            	if(null!=resultIdList&&resultIdList.size()>0){
            		KpiInfoDto kpiInfoDto = new KpiInfoDto();
            		kpiInfoDto.setExecuteEmpId(empId);
                	kpiInfoList.add(kpiInfoDto);
            	}
            }
            if(kpiInfoList.size()>0){

            	Map<String, Object> variables = new HashMap<String, Object>();
            	if(null != extraMap && extraMap.containsKey("planId")) {
            		variables.put("planId", extraMap.get("planId"));
            	}else{
            		variables.put("planId","");
            	}
                variables.put("entityType", entityType);
                variables.put("date", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                variables.put("name", taskName);
                variables.put("items", kpiInfoList);
                variables.put("joinCount", kpiInfoList.size());
                String executionId = o_jbpmBO.startProcessInstance(entityType, variables);
                variables.put("executionId", executionId);
                o_jbpmOperate.saveVariables(executionId, variables);
                List<Task>  tasks = o_jbpmBO.findTaskByProcessInstanceId(executionId);
                if(null!=tasks&&tasks.size()>0){
                	for (Task task : tasks) {
                		String taskId = task.getId();
                		String empId = task.getAssignee();
                		String subExecutionId = task.getExecutionId();
                		Map<String, Object> subVariables = new HashMap<String, Object>();
                		List<String> resultIdList = (List<String>) map.get(empId).get("resultIdList");
                		String resultIds = StringUtils.join(resultIdList, ",");
                		subVariables.put("resultIds",resultIds);
                		o_jbpmOperate.saveVariables(subExecutionId, subVariables);
                		//按照采集人发送邮件
                		if(o_chartForEmailBO.findIsSend()){
                            String weburl = o_chartForEmailBO.findWebUrl(valueType, subExecutionId, taskId, empId,jsUrl);
                        	o_chartForEmailBO.sendKpiGatherEmail(empId, resultIdList,taskName,weburl,"");
                        }
                		logger.info("分发子流程实例ID:"+subExecutionId+"  采集结果ID:"+resultIds);
					}
                }
                
                logger.info("主流程实例ID:"+executionId);
            }
            
    	}
    	
    }
    
    
    //# 以人为单位起流程
    /**
     * 定时触发发送指标目标值和实际值待办.
     */
    /*@SuppressWarnings("unchecked")
	@Transactional
    public boolean generateKpiGatherResultInput() {

    	
    	//收集信息
        List<Object[]> list = o_kpiGatherResultBO.findAllKpiRelaInfo();
        Map<String,Map<String,Object>> targetMap = new HashMap<String, Map<String,Object>>();
        Map<String,Map<String,Object>> finishMap = new HashMap<String, Map<String,Object>>();
        if(null!=list){
        	for (Object[] object : list){
            	String kpiId = String.valueOf(object[2]);
            	String empId =  String.valueOf(object[0]);
            	String orgType = String.valueOf(object[6]);
            	String resultId = String.valueOf(object[8]);
            	if (null!=empId) {
            		if (Contents.TARGETDEPARTMENT.equals(orgType)){//目标人员
    	        		if(!targetMap.containsKey(empId)){//不包含empid
    	        				List<String> resultIdList = new ArrayList<String>();
    	        				resultIdList.add(resultId);
    	        				Map<String,Object> resultMap = new HashMap<String, Object>();
    	        				resultMap.put("resultIdList", resultIdList);
    	        				resultMap.put("kpiId", kpiId);
    	        				targetMap.put(empId, resultMap);
    	        		}else{
    	        			Map<String,Object> resultMap = targetMap.get(empId);
    	        			resultMap.put("kpiId", kpiId);
    	        			List<String> resultIdList = (List<String>)resultMap.get("resultIdList");
    	        			resultIdList.add(resultId);
    	        		}
            		}
            		if(Contents.GATHERDEPARTMENT.equals(orgType)){//采集人员
    	        		if(!finishMap.containsKey(empId)){//不包含empid
    	        				List<String> resultIdList = new ArrayList<String>();
    	        				resultIdList.add(resultId);
    	        				Map<String,Object> resultMap = new HashMap<String, Object>();
    	        				resultMap.put("resultIdList", resultIdList);
    	        				resultMap.put("kpiId", kpiId);
    	        				finishMap.put(empId, resultMap);
    	        			
    	        		}else{
    	        			Map<String,Object> resultMap = finishMap.get(empId);
    	        			resultMap.put("kpiId", kpiId);
    	        			List<String> resultIdList = (List<String>)resultMap.get("resultIdList");
    	        			resultIdList.add(resultId);
    	        		}
            		}
            		
            	}
            }
            
            //启动流程
            doStartProcess(targetMap, "target");
            
            doStartProcess(finishMap, "finish");
        }

        return true;
    }*/
    
    
    
    
    /*@SuppressWarnings("unchecked")
	public void doStartProcess(Map<String,Map<String,Object>> map ,String valueType){
    	
    	String jsUrl = "";
    	String entityType = "";
    	String taskName = "";
    	if("target".equals(valueType)){
    		entityType = "KpiTargetGatherResultInput";
    		taskName = "目标值采集数据收集";
    		jsUrl = "FHD.view.kpi.bpm.targetgather.KpiGatherRecorded";
    	}else if("finish".equals(valueType)){
    		entityType = "KpiFinishGatherResultInput";
    		taskName = "实际值采集数据收集";
    		jsUrl = "FHD.view.kpi.bpm.finishgather.KpiGatherRecorded";
    	}
    	Iterator<Map.Entry<String, Map<String,Object>>> iter = map.entrySet().iterator();
        while(iter.hasNext()){
        	Map.Entry<String,Map<String,Object>> entry = iter.next();
        	String empId = entry.getKey();
        	Map<String,Object> resultMap = entry.getValue();
        	List<String> resultIdList =(List<String>)resultMap.get("resultIdList");
        	String kpiId = String.valueOf(resultMap.get("kpiId"));
        	String resultIds = StringUtils.join(resultIdList, ",");
        	Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("entityType", entityType);
            variables.put("empId", empId);
            variables.put("kpiId", kpiId);
            variables.put("date", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            variables.put("name", taskName);
            variables.put("resultIds", resultIds);
            
            //发送提醒邮件
            String executionId = o_jbpmBO.startProcessInstance(entityType, variables);
            if(o_chartForEmailBO.findIsSend()){
                String taskId = o_jbpmBO.findTaskByExecutionId(executionId).getId();
                String weburl = o_chartForEmailBO.findWebUrl(valueType, executionId, taskId, empId,jsUrl);
            	o_chartForEmailBO.sendKpiGatherEmail(empId, resultIdList,taskName,weburl,"");
            }
        }
    }*/
}
