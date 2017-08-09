/*
 * 北京第一会达风险管理有限公司 版权所有 2013
 * Copyright(C) 2013 Firsthuida Co.,Ltd. All rights reserved. 
 */
package com.fhd.comm.business.bpm.activiti;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

/**
 * 流程其他操作类<br>
 *
 * @author   胡迪新
 * @since    fhd Ver 4.5
 * @Date	 2013-2-17  上午11:48:40
 *
 * @see 	 
 */
public class ProcessOtherBO {

	protected RepositoryService repositoryService;

	protected RuntimeService runtimeService;

	protected TaskService taskService;

	protected FormService formService;

	protected HistoryService historyService;
	
	public Boolean isJointTask(String taskId) {

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		
		return "jointProcess".equals(task.getDescription());
	}
}

