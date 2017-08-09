/**
 * TaskWebInit.java
 * com.fhd.sys.business.st
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-15 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/
/**
 * TaskWebInit.java
 * com.fhd.sys.business.st
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-15        金鹏祥
 *
 * Copyright (c) 2012, FirstHuida All Rights Reserved.
*/


package com.fhd.sys.business.st.task;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.fhd.sys.business.st.PlanBO;
import com.fhd.dao.sys.st.PlanDAO;
import com.fhd.entity.sys.st.Plan;

/**
 * 初始化定时任务
 *
 * @author   金鹏祥
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-15		下午02:15:38
 *
 * @see 	 
 */

public class TaskWebInit implements InitializingBean{
	
	private Logger log = Logger.getLogger(TaskWebInit.class);
	
	@Autowired
	private PlanDAO o_planDAO;
	
	@Autowired
	private QuartzManagerBO o_quartzBO;
	
	@Autowired
	private TriggerEmailBO o_triggerEmailBO;
	
	@Autowired
	private PlanBO o_planBO;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Session session = o_planDAO.getSessionFactory().openSession();
		List<Plan> planList = o_planBO.findPlanBySome(session, "1", "1");
		if(planList != null){
			for (Plan plan : planList) {
				new TaskThread(plan, o_quartzBO, o_triggerEmailBO).start();
			}
		}else{
			log.info("没有定时任务可以启动");
		}
		session.close();
	}
}