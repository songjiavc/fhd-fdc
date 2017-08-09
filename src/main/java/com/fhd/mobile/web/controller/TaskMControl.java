package com.fhd.mobile.web.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.encode.JsonBinder;

@Controller
public class TaskMControl {

	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	@RequestMapping("/mobile/findtaskbyuserid.f")
	public void findTaskByUserId(PrintWriter out,HttpServletResponse response,String callback,String username) {
		List<Map<String, String>> tasks = new ArrayList<Map<String,String>>();
		
		List<Task> personalTasks = o_jbpmOperate.getTaskService().findPersonalTasks(username);
		for (Task task : personalTasks) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("id", task.getId());
			data.put("name", task.getName());
			data.put("date", DateUtils.formatShortDate(task.getCreateTime()));
			tasks.add(data);
		}
		
		boolean jsonP = false;
		if (callback != null) {
		    jsonP = true;
		    response.setContentType("text/javascript");
		} else {
		    response.setContentType("application/x-json");
		}
		if (jsonP) {
		    out.write(callback + "(");
		}
		out.print(JsonBinder.buildNormalBinder().toJson(tasks));
		if (jsonP) {
		    out.write(");");
		}
		
	}
	
}
