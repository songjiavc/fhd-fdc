/**
 * TaskThread.java
 * com.fhd.sys.business.st
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-15 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/
/**
 * TaskThread.java
 * com.fhd.sys.business.st
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-15        金鹏祥
 *
 * Copyright (c) 2012, FirstHuida All Rights Reserved.
*/


package com.fhd.sys.business.st.task;

import org.apache.log4j.Logger;

import com.fhd.entity.sys.st.Plan;

/**
 * 初始化定时任务线程
 *
 * @author   金鹏祥
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-15		下午02:52:01
 *
 * @see 	 
 */
public class TaskThread extends Thread {
	
	private Logger logger = Logger.getLogger(TaskThread.class);
	private Plan plan;
	private QuartzManagerBO o_quartzBO;
	private TriggerEmailBO o_triggerEmailBO;
	
	public TaskThread(Plan plan, QuartzManagerBO o_quartzBO, TriggerEmailBO o_triggerEmailBO){
		this.plan = plan;
		this.o_quartzBO = o_quartzBO;
		this.o_triggerEmailBO = o_triggerEmailBO;
	}
	
	@Override
	public void run() {
		try {
			String cronsStr[] = plan.getTriggerDataSet().split(";");//获取CRON表达式
			o_quartzBO.addJob(plan.getId(), o_triggerEmailBO, cronsStr[cronsStr.length - 1].toString());//添加定时任务
			logger.info("定时任务ID:" + plan.getId() + "重新启用");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}