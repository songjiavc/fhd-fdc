/**
 * JBPMOperate.java
 * com.fdc.jbpm.fxsb
 *
 * Function：  
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2011-1-11 		史永亮
 *
 * Copyright (c) 2011, Firsthuida All Rights Reserved.
 */

package com.fhd.comm.business.bpm.jbpm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.jbpm.api.Execution;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.HistoryService;
import org.jbpm.api.IdentityService;
import org.jbpm.api.ManagementService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.history.HistoryProcessInstance;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.history.HistoryTaskQuery;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName:JBPMOperate Function:  JBPM操作接口 
 * Reason: 
 * 
 * @author 史永亮
 * @version
 * @since Ver 1.1
 * @Date 2011-1-11 上午09:35:05
 * 
 * @see 
 */
@Component
public class JBPMOperate {

	/**
	 * 流程引擎对象 此对象在resources/spring/spring-config-jbpm.xml文件中配置
	 */
	@Autowired
	protected ProcessEngine processEngine;	
	
	/**
	 * getRepositoryService:(获取流程资源服务的接口)
	 * 
	 * @author 史永亮
	 * @return
	 * @since fhd Ver 1.1
	 */
	public RepositoryService getRepositoryService() {
		return processEngine.getRepositoryService();
	}

	/**
	 * 
	 * getExecutionService:(获取流程执行服务的接口)
	 * 
	 * @author 史永亮
	 * @return
	 * @since fhd Ver 1.1
	 */
	public ExecutionService getExecutionService() {
		return processEngine.getExecutionService();
	}

	/**
	 * 
	 * getTaskService:(获取任务服务的接口)
	 * 
	 * @author 史永亮
	 * @return
	 * @since fhd Ver 1.1
	 */
	public TaskService getTaskService() {
		return processEngine.getTaskService();
	}

	/**
	 * getHistoryService:(获取流程历史服务的接口)
	 * 
	 * @author 史永亮
	 * @return
	 * @since fhd Ver 1.1
	 */
	public HistoryService getHistoryService() {
		return processEngine.getHistoryService();
	}

	/**
	 * getIdentityService:(获取身份认证服务的接口)
	 * 
	 * @author 史永亮
	 * @return
	 * @since fhd Ver 1.1
	 */
	public IdentityService getIdentityService() {
		return processEngine.getIdentityService();
	}

	/**
	 * getManagementService:(获取流程管理控制服务的接口)
	 * 
	 * @author 史永亮
	 * @return
	 * @since fhd Ver 1.1
	 */
	public ManagementService getManagementService() {
		return processEngine.getManagementService();
	}

	/**
	 * 
	 * startPocessInstance:开启流程
	 * 
	 * @author 杨鹏
	 * @param processDefinitionKey
	 * @param variables
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public ProcessInstance startPocessInstance(String processDefinitionKey,Map<String, ?> variables) {
		return this.getExecutionService().startProcessInstanceByKey(processDefinitionKey, variables);
	}

	/**
	 * 
	 * singelPocessInstance:执行流程
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @param variables
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public ProcessInstance singelPocessInstance(String executionId,Map<String, ?> variables) {
		return this.getExecutionService().signalExecutionById(executionId,variables);
	}

	/**
	 * isRiskComplete:(判断风险流程是否结束)
	 * @author 史永亮
	 * @param businessId 业务id
	 * @return 该业务对象是否流转结束
	 * @since fhd Ver 1.1
	 */
	public boolean isComplete(String processInstanceId) {
		//根据流程实例id来查找执行
		Execution execution = this.getExecutionService().findProcessInstanceById(processInstanceId);
		//如果没找到，就到历史中去找
		if (execution == null) {
			List<HistoryProcessInstance> hpiList = this.getHistoryService().createHistoryProcessInstanceQuery().list();
			for (HistoryProcessInstance hpi : hpiList) {
				if (hpi.getProcessInstanceId().equals(processInstanceId)) {
					return true;
				}
			}
			return false;
		}
		return execution.isEnded();
	}

	/**
	 * 执行id获取不合理，取第一条历史节点不合理
	 * getLastAssignee:(获取上一结点参与人)
	 * 
	 * @author 史永亮
	 * @param processInstanceId流程实例id
	 * @return 流程参与人的员工id
	 * @since fhd Ver 1.1
	 */
	@Deprecated
	public String getLastAssignee(String processInstanceId) {
		String pexecutionid = processInstanceId.split("\\.")[0]+"."+processInstanceId.split("\\.")[1];
		//根据实例id来查找历史任务集合
		List<HistoryTask> list = this.getHistoryService().createHistoryTaskQuery()	.executionId(pexecutionid).orderDesc(HistoryTaskQuery.PROPERTY_ID).list();

		HistoryTask ht = list.get(1);
		return ht.getAssignee();
	}


	/**
	 * findParentExcetionId:(返回当前流程父流程exceutionId)
	 * 如果没有父流程，则返回当前流程exceutionId
	 * 
	 * @author 史永亮
	 * @param executionid
	 * @return
	 * @since fhd Ver 1.1
	 */
	public String findParentExcetionId(String executionid) {
		//找该执行id的父执行
		Execution parentExcetion = ((ExecutionImpl) this.processEngine.getExecutionService().findExecutionById(executionid)).getSuperProcessExecution();
		// 如果有父执行就返回该父执行的id
		if (parentExcetion != null) {
			return parentExcetion.getParent().getId();
		}
		return executionid;

	}

	/**
	 * isSubPorcess:(返回当前流程是否是子流程)
	 * 
	 * @author 史永亮
	 * @param executionid
	 * @return
	 * @since fhd Ver 1.1
	 */
	public boolean isSubPorcess(String executionid) {
		//找该执行id的父执行
		Execution parentExcetion = ((ExecutionImpl) this.processEngine.getExecutionService().findExecutionById(executionid)).getSuperProcessExecution();
		//如果有父执行，则该流程就是子的
		if (parentExcetion != null) {
			return true;
		}
		return false;

	}


	/**
	 * deletePorcessInstance:(根据id删除流程实例)
	 * 
	 * @author 史永亮
	 * @param id
	 * @since fhd Ver 1.1
	 */
	@Transactional
	public void deletePorcessInstance(String processInstanceId) {
		ExecutionImpl exceution = ((ExecutionImpl) this.processEngine.getExecutionService().findExecutionById(processInstanceId));
		// 有父流程的，也就是子流程的，直接结束
		if(exceution!=null){
			if (exceution.getSuperProcessExecution() != null) {
				this.processEngine.getExecutionService().signalExecutionById(processInstanceId);
			}else{//没有的，也就是单一流程实例的，直接删除
				this.processEngine.getExecutionService().deleteProcessInstance(processInstanceId);
			}
		}

	}

	/**
	 * findSubPorcessInstanceIds:(根据parentId查出该流程下的所有子流程id，除了参数id的)
	 * 
	 * @author 史永亮
	 * @param parentId
	 *            父流程id
	 * @param id
	 *            要排除的id
	 * @return
	 * @since fhd Ver 1.1
	 */
	public List<String> findSubPorcessInstanceIds(String parentId, String id) {

		//根据流程实例id查询到该流程实例对象
		ExecutionImpl exceution = ((ExecutionImpl) this.processEngine.getExecutionService().findExecutionById(parentId));
		
		//获取子执行容器
		Collection<ExecutionImpl> subList = exceution.getExecutions();
		//排除子执行id等于参数id的对象
		List<String> re = new ArrayList<String>();
		for (ExecutionImpl ei : subList) {
			if (!ei.getId().equals(id)) {
				re.add(ei.getSubProcessInstance().getId());

			}
		}
		return re;

	}

	/**
	 * 
	 * <pre>
	 * findSubPorcessInstance:(根据parentId查出该流程下的所有子流程)
	 * </pre>
	 * 
	 * @author 史永亮
	 * @param parentId
	 *            父流程id
	 * @return
	 * @since fhd Ver 1.1
	 */
	public Collection<ExecutionImpl> findSubPorcessInstance(String parentId) {
		//根据流程实例id查询到该流程实例对象
		ExecutionImpl exceution = ((ExecutionImpl) this.processEngine
				.getExecutionService().findExecutionById(parentId));
		//获取子执行容器
		Collection<ExecutionImpl> subList = exceution.getExecutions();
		Collection<ExecutionImpl> re = new ArrayList<ExecutionImpl>();
		//全部加入到返回结果中
		for (ExecutionImpl ei : subList) {
			if(ei.getSubProcessInstance() != null)
			{
				re.add(ei.getSubProcessInstance());
			}
		}

		return re;

	}
	/**
	 * 
	 * deploy:发布流程
	 * 
	 * @author 杨鹏
	 * @param inputStream
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	public void deploy(InputStream inputStream) throws Exception {
		ZipInputStream zis = new ZipInputStream(inputStream);
		this.processEngine.getRepositoryService().createDeployment().addResourcesFromZipInputStream(zis).deploy();
	}
	/**
	 * 
	 * <pre>
	 * deletePD:(根据流程定义id删除该流程定义)
	 * </pre>
	 * 
	 * @author 史永亮
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
	public void deletePorcessDefinition(String id) {
		//根据id删除该流程定义，并删除该定义下的所有流程实例和任务等。级联删除
		this.processEngine.getRepositoryService().deleteDeploymentCascade(id);
		
	}

	
	/**
	 * 
	 * saveVariables:保存流程变量
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @param variables
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void saveVariables(String executionId,Map<String, ?> variables) {
		this.processEngine.getExecutionService().createVariables(executionId, variables,true);
	}
	/**
	 * 
	 * getVariable:读取流程变量强转字符串类型
	 * 
	 * @author 杨鹏
	 * @param executionid
	 * @param name
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public String getVariable(String executionId,String name) {
		String variableStr=null;
		Object variable = this.getVariableObj(executionId,name);
		if(variable!=null){
			variableStr=String.valueOf(variable);
		}
		return variableStr;
	}
	/**
	 * 
	 * getVariableObj:读取流程变量
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @param name
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Object getVariableObj(String executionId,String name) {
		return this.processEngine.getExecutionService().getVariable(executionId,name);
	}
	
	/**
	 * 
	 * setVariablesByTaskId:设定jbpm任务变量
	 * 
	 * @author 杨鹏
	 * @param taskId
	 * @param variables
	 * @since  fhd　Ver 1.1
	 */
	public void setVariablesByTaskId(String taskId,Map<String, ?> variables){
		this.getTaskService().setVariables(taskId, variables);
	}
	
	/**
	 * 
	 * getVariablesByTaskId:获取jbpm任务变量
	 * 
	 * @author 杨鹏
	 * @param taskId
	 * @param name
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Object getVariablesByTaskId(String taskId,String name){
		return this.getTaskService().getVariable(taskId, name);
	}
}
