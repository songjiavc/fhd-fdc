package com.fhd.fdc.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.fdc.commons.exception.FHDException;
import com.fhd.fdc.commons.security.OperatorDetails;
import com.fhd.fdc.utils.UserContext;

@Controller
public class DispatcherControl {

	
	
	//@Autowired
	private RepositoryService o_repositoryService;
	//@Autowired
	private RuntimeService o_runtimeService;
	@Autowired
	private JBPMBO o_jbpmBO;
	
	
	@RequestMapping("/login.do")
	public String login(){
		//LicenseControl licenseControl = new LicenseControl();licenseControl = null; 
		return "login";
	}

	@RequestMapping("/index.do")
	public String index(Model model, HttpServletRequest request) throws FHDException {

		request.getSession().getServletContext().setAttribute("pageSize", "");
		OperatorDetails user = UserContext.getUser();
		request.getSession().getServletContext().setAttribute("user", user);
		return "index";
	}

	@ResponseBody
	@RequestMapping("/sessiontimeout.do")
	public void sessionTimeout(HttpServletRequest request,HttpServletResponse response) throws IOException {
		if (request.getHeader("x-requested-with") != null
				&& request.getHeader("x-requested-with").equalsIgnoreCase(
						"XMLHttpRequest")) { // ajax 超时处理
			response.setHeader("sessionstatus", "timeout");
			response.setStatus(403);	//设置这句话，才能是前台request请求监听到requestexception
			PrintWriter out = response.getWriter();
			out.print("{timeout:true}");
			out.flush();
			out.close();
		}else { // http 超时处理
			response.sendRedirect(request.getContextPath() + "/login.do");
		}

	}

	@RequestMapping(value = "/activitiDeploy.do", method = RequestMethod.GET)
	public void g_activitiDeploy() {
	}

	@RequestMapping(value = "/activitiDeploy.do", method = RequestMethod.POST)
	public void p_activitiDeploy(@RequestParam MultipartFile file)
			throws FHDException {
		try {
			ZipInputStream inputStream = new ZipInputStream(
					file.getInputStream());
			o_repositoryService.createDeployment().name("risk")
					.addZipInputStream(inputStream).deploy();
		} catch (Exception e) {
			throw new FHDException(e);
		}
	}

	@RequestMapping(value = "/viewProcess.do", method = RequestMethod.GET)
	public void viewProcess(String id,OutputStream output) throws Exception {
		ProcessInstance processInstance = o_runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) o_repositoryService).getDeployedProcessDefinition(processInstance
	            .getProcessDefinitionId());

		for (ActivityImpl activity : processDefinition.getActivities()) {
	      System.out.println(activity.getProperty("name"));

	    }
		
	    if (processDefinition != null && processDefinition.isGraphicalNotationDefined()) {
	      InputStream definitionImageStream = ProcessDiagramGenerator.generateDiagram(processDefinition, "png", 
	        o_runtimeService.getActiveActivityIds(processInstance.getId()));
	      IOUtils.write(IOUtils.toByteArray(definitionImageStream), output);
	      
	    }
	    
	    
	}
	
	/**
	 * 首页上的通知需要的内容，如：待办任务数
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/notificationcontent.f")
	public Map<String, Object> notificationContent() {
		// 查询当前登录用户待办数量 
		Integer taskNum = 0;
		
		if(null != UserContext.getUser().getEmp()){
			taskNum = o_jbpmBO.findDoneVJbpm4TaskCountByEmpId(UserContext.getUser().getEmpid());
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("taskNum", taskNum);
		return data;
	}
	
	
	@RequestMapping("/singlePointLogin.do")
	public String singlePointLogin(Model model, HttpServletRequest request) {
		String userName = request.getHeader("iv-user");
		
		model.addAttribute("userName", userName);
		model.addAttribute("passWord", "111111");
		return "tempLogin";
		
	}
	
	
}
