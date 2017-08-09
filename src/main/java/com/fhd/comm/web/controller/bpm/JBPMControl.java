/**
 * RiskControl.java
 * com.fhd.fdc.commons.web.controller.risk
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2010-11-18 		David
 *
 * Copyright (c) 2010, Firsthuida All Rights Reserved.
 */

package com.fhd.comm.web.controller.bpm;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.pvm.internal.history.model.HistoryActivityInstanceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.HistoryActivityInstanceBO;
import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.comm.business.bpm.jbpm.JbpmHistProcinstBO;
import com.fhd.comm.business.bpm.jbpm.VJbpmHistActinstBO;
import com.fhd.comm.web.form.bpm.ProcessDefinitionDeployForm;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.bpm.BusinessWorkFlow;
import com.fhd.entity.bpm.ExamineApproveIdea;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.ProcessDefinitionDeploy;
import com.fhd.entity.bpm.view.VJbpm4Task;
import com.fhd.entity.bpm.view.VJbpmDeployment;
import com.fhd.entity.bpm.view.VJbpmHistActinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;

@Controller
public class JBPMControl {
	@Autowired
	private JBPMBO o_jBPMBO;
	@Autowired
	private JBPMOperate o_jBPMOperate;
	@Autowired
	private JbpmHistProcinstBO o_jbpmHistProcinstBO;
	@Autowired
	private VJbpmHistActinstBO o_vJbpmHistActinstBO;
	@Autowired
	private HistoryActivityInstanceBO o_historyActivityInstanceBO;
	/**
	 * 
	 * saveRateByExecutionId:计算当前完成百分比
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/jbpm/saveRateByExecutionId.f")
	public void saveRateByExecutionId(String executionId){
		executionId = o_jBPMBO.getExecutionIdBySubExecutionId(executionId);
		o_jBPMBO.saveRateByExecutionId(executionId);
	}
	
	/**
	 * 
	 * removePorcessInstance:
	 * 
	 * @author 杨鹏
	 * @param processInstanceIdsStr
	 * @param response
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/jbpm/removePorcessInstance.f")
	public void removePorcessInstance(String processInstanceIdsStr,ServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String[] processInstanceIds = processInstanceIdsStr.split(",");
		o_jBPMBO.removePorcessInstance(processInstanceIds);
	}

	/**
	 * 
	 * processDefinitionDeployPage:查询流程定义
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param limit
	 * @param start
	 * @param sort
	 * @param dir
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/jbpm/processDefinitionDeploy/processDefinitionDeployPage.f")
	public Map<String,Object> processDefinitionDeployPage(String query,String deleteStatus, Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<ProcessDefinitionDeploy> page=new Page<ProcessDefinitionDeploy>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_jBPMBO.findProcessDefinitionDeployPageBySome(page,query,deleteStatus,sortList);
		List<ProcessDefinitionDeploy> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (ProcessDefinitionDeploy processDefinitionDeploy : list) {
			Map<String, Object> processDefinitionDeployToMap = processDefinitionDeployToMap(processDefinitionDeploy);
			datas.add(processDefinitionDeployToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	/**
	 * 
	 * findProcessDefinitionDeployById:读取流程定义
	 * 
	 * @author 杨鹏
	 * @param processDefinitionDeployId
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/jbpm/processDefinitionDeploy/findProcessDefinitionDeployById.f")
	public Map<String,Object> findProcessDefinitionDeployById(String processDefinitionDeployId) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> data = new HashMap<String, Object>();
		ProcessDefinitionDeploy processDefinitionDeploy = o_jBPMBO.findProcessDefinitionDeployById(processDefinitionDeployId);
		String id = processDefinitionDeploy.getId();
		String disName = processDefinitionDeploy.getDisName();
		String deleteStatus = processDefinitionDeploy.getDeleteStatus();
		String url = processDefinitionDeploy.getUrl();
		String fileUploadEntityId = "";
		FileUploadEntity fileUploadEntity = processDefinitionDeploy.getFileUploadEntity();
		if(fileUploadEntity!=null){
			fileUploadEntityId = fileUploadEntity.getId();
			
		}
		data.put("id", id);
		data.put("disName", disName);
		data.put("deleteStatus", deleteStatus);
		data.put("url", url);
		data.put("fileUploadEntityId", fileUploadEntityId);
		map.put("data", data);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 
	 * mergeDeployProcessDefinitionDeploy:添加并发布流程定义
	 * 
	 * @author 杨鹏
	 * @param form
	 * @param result
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/jbpm/processDefinitionDeploy/mergeDeployProcessDefinitionDeploy.f")
	public Boolean mergeDeployProcessDefinitionDeploy(ProcessDefinitionDeployForm form, BindingResult result)throws Exception {
		Boolean flag=false;
		try {
			ProcessDefinitionDeploy processDefinitionDeploy = null;
			processDefinitionDeploy=new ProcessDefinitionDeploy();
			BeanUtils.copyProperties(form, processDefinitionDeploy);
			processDefinitionDeploy.setId(Identities.uuid());
			FileUploadEntity fileUploadEntity = new FileUploadEntity();
			fileUploadEntity.setId(form.getFileUploadEntityId());
			processDefinitionDeploy.setFileUploadEntity(fileUploadEntity);
			o_jBPMBO.mergeDeployProcessDefinitionDeploy(processDefinitionDeploy);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 
	 * mergeProcessDefinitionDeploy:更改流程定义
	 * 
	 * @author 杨鹏
	 * @param form
	 * @param result
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/jbpm/processDefinitionDeploy/mergeProcessDefinitionDeploy.f")
	public Boolean mergeProcessDefinitionDeploy(ProcessDefinitionDeployForm form, BindingResult result)throws Exception {
		Boolean flag=false;
		try {
			ProcessDefinitionDeploy processDefinitionDeploy = null;
			String id = form.getId();
			processDefinitionDeploy=o_jBPMBO.findProcessDefinitionDeployById(id);
			processDefinitionDeploy.setDisName(form.getDisName());
			processDefinitionDeploy.setUrl(form.getUrl());
			o_jBPMBO.mergeProcessDefinitionDeploy(processDefinitionDeploy);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 
	 * removeDeployment:删除流程定义
	 * 
	 * @author 杨鹏
	 * @param deploymentIdsStr
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/jbpm/removeDeployment.f")
	public void removeDeployment(String deploymentIdsStr,ServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String[] deploymentIds = StringUtils.split(deploymentIdsStr, ",");
		o_jBPMBO.removeDeployment(deploymentIds);
	}
	
	/**
	 * 
	 * vJbpmDeploymentPage:分页查询所有发布后的流程
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param limit
	 * @param start
	 * @param sort
	 * @param dir
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/jbpm/processDefinitionDeploy/vJbpmDeploymentPage.f")
	public Map<String,Object> vJbpmDeploymentPage(String query, Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<VJbpmDeployment> page=new Page<VJbpmDeployment>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_jBPMBO.findVJbpmDeploymentPageBySome(page,query, sortList);
		List<VJbpmDeployment> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (VJbpmDeployment vJbpmDeployment : list) {
			Map<String, Object> vJbpmDeploymentToMap = vJbpmDeploymentToMap(vJbpmDeployment);
			datas.add(vJbpmDeploymentToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 
	 * p_processInstanceList:读取流程实例
	 * 
	 * @author 杨鹏
	 * @param businessName
	 * @param vJbpm4DeploymentName
	 * @param startEmpName
	 * @param limit
	 * @param start
	 * @param sort
	 * @param dir
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/jbpm/processInstance/processInstanceList.f")
	public Map<String,Object> p_processInstanceList(String query,String businessName,String jbpmDeploymentName,String startEmpName,Long dbversion,String assigneeId, Integer limit, Integer start,String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<JbpmHistProcinst> page=new Page<JbpmHistProcinst>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_jbpmHistProcinstBO.findJbpmHistProcinstPageBySome(page,query, businessName, jbpmDeploymentName,startEmpName,dbversion,assigneeId, sortList);
		List<JbpmHistProcinst> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (JbpmHistProcinst jbpmHistProcinst : list) {
			Map<String, Object> jbpmHistProcinstToMap = jbpmHistProcinstToMap(jbpmHistProcinst);
			datas.add(jbpmHistProcinstToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 
	 * jbpmHistProcinstForm:流程实例信息
	 * 
	 * @author 杨鹏
	 * @param jbpmHistProcinstId
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/jbpm/processinstance/jbpmHistProcinstForm.f")
	public Map<String,Object> jbpmHistProcinstForm(Long jbpmHistProcinstId) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> data = new HashMap<String, Object>();
		String businessName="";
		String endactivityStr="";
		String realname="";
		String orgnames="";
		String createTimeStr="";
		String updateTimeStr="";
		
		JbpmHistProcinst jbpmHistProcinst=o_jbpmHistProcinstBO.findJbpmHistProcinstById(jbpmHistProcinstId);
		List<JbpmHistActinst> jbpmHistActinsts = jbpmHistProcinst.getJbpmHistActinsts();
		Date updateTime=null;
		for (JbpmHistActinst jbpmHistActinst : jbpmHistActinsts) {
			Date end = jbpmHistActinst.getEnd();
			Date start = jbpmHistActinst.getStart();
			if(end!=null&&(updateTime==null||updateTime.getTime()<end.getTime())){
				updateTime=end;
			}
			if(start!=null&&(updateTime==null||updateTime.getTime()<start.getTime())){
				updateTime=start;
			}
		}
		businessName=jbpmHistProcinst.getBusinessWorkFlow().getBusinessName();
		String endactivity = jbpmHistProcinst.getEndactivity();
		if("end1".equals(endactivity)){
			endactivityStr="已完成";
		}else if("remove1".equals(endactivity)){
			endactivityStr="已完成";
		}else{
			endactivityStr="进行中";
		}
		if(Contents.SYSTEM.equals(jbpmHistProcinst.getBusinessWorkFlow().getCreateBy())){
			realname=Contents.SYSTEM;
		}else{
			realname=jbpmHistProcinst.getBusinessWorkFlow().getCreateByEmp().getRealname();
			Set<SysEmpOrg> sysEmpOrgs = jbpmHistProcinst.getBusinessWorkFlow().getCreateByEmp().getSysEmpOrgs();
			List<String> orgnameList = new ArrayList<String>();
			for (SysEmpOrg sysEmpOrg : sysEmpOrgs) {
				orgnameList.add(sysEmpOrg.getSysOrganization().getOrgname());
			}
			orgnames=StringUtils.join(orgnameList.toArray(),",");
		}
		Date createTime = jbpmHistProcinst.getBusinessWorkFlow().getCreateTime();
		createTimeStr=DateUtils.formatDate(createTime, "yyyy-MM-dd hh:mm:ss");
		updateTimeStr=DateUtils.formatDate(updateTime, "yyyy-MM-dd hh:mm:ss");
		
		data.put("businessName", businessName);
		data.put("endactivity", endactivityStr);
		data.put("realname", realname);
		data.put("orgnames", orgnames);
		data.put("createTime", createTimeStr);
		data.put("updateTime", updateTimeStr);
		map.put("data", data);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 
	 * jbpmHistActinstPage:
	 * 
	 * @author 杨鹏
	 * @param assigneeId
	 * @param jbpmHistProcinstId
	 * @param dbversion
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/jbpm/processInstance/jbpmHistActinstPage.f")
	public Map<String,Object> jbpmHistActinstPage(String query,String executionId,String processInstanceId,String assigneeId,Long jbpmHistProcinstId,String endactivity,Long dbversion,Integer limit, Integer start, String sort)  {
		Map<String,Object> map = new HashMap<String, Object>();
		Page<JbpmHistActinst> page=new Page<JbpmHistActinst>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		page=o_jBPMBO.findJbpmHistActinstPageBySome(page,query,executionId,processInstanceId,assigneeId, jbpmHistProcinstId,endactivity,dbversion, sortList);
		List<JbpmHistActinst> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (JbpmHistActinst jbpmHistActinst : list) {
			Map<String, Object> jbpmHistActinstToMap = jbpmHistActinstToMap(jbpmHistActinst);
			datas.add(jbpmHistActinstToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	/**
	 * 
	 * vJbpmHistActinstPage:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param jbpmHistProcinstId
	 * @param type
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/jbpm/processInstance/vJbpmHistActinstPage.f")
	public Map<String,Object> vJbpmHistActinstPage(String query,Long jbpmHistProcinstId,String type,Integer limit, Integer start, String sort)  {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<VJbpmHistActinst> page=o_vJbpmHistActinstBO.findPageBySome(query,jbpmHistProcinstId, type,limit,start, sortList);
		List<VJbpmHistActinst> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (VJbpmHistActinst vJbpmHistActinst : list) {
			Map<String, Object> vJbpmHistActinstToMap = vJbpmHistActinstToMap(vJbpmHistActinst);
			datas.add(vJbpmHistActinstToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}


	/**
	 * 
	 * findVJbpm4TaskPageBySome:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param assigneeId
	 * @param endactivity
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/jbpm/processInstance/findVJbpm4TaskBySome.f")
	public Map<String,Object> findVJbpm4TaskBySome(String query,String assigneeId,String endactivity,Integer limit, Integer start, String sort)  {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		List<VJbpm4Task> list = o_jBPMBO.findVJbpm4TaskBySome(query,assigneeId,endactivity, sortList);
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (VJbpm4Task vJbpm4Task : list) {
			Map<String, Object> vJbpm4TaskToMap = vJbpm4TaskToMap(vJbpm4Task);
			datas.add(vJbpm4TaskToMap);
		}
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 
	 * 工作流日志列表查询
	 * 
	 * @param jbpmHistProcinstId 流程实例id
	 * @author vincent
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/jbpm/processInstance/histActinstLog.f")
	public Map<String,Object> histActinstLog(String jbpmHistProcinstId)  {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Object[]> histActinstList = o_jBPMBO.findJbpmHistActinstBySome(jbpmHistProcinstId);
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (Object[] histActinst : histActinstList) {
			Map<String,Object> data = new HashMap<String, Object>();
			data.put("id", histActinst[0]);
			data.put("disName", histActinst[1]);
			data.put("activityName", histActinst[1]);
			
			Integer duration = ((BigInteger)histActinst[2]).intValue();
			data.put("dbversion", duration);
			
			String dbversionStr = "";
			if(duration == 0){
				dbversionStr="未处理";
			}else {
				dbversionStr="已处理";
			}
			data.put("dbversionStr", dbversionStr);
			if(histActinst[4]==null){
				data.put("assigneeRealname", Contents.SYSTEM);
			}else{
				data.put("assigneeRealname", histActinst[4] + "(" + histActinst[3] + ")");
			}
			data.put("assigneeCompanyName", histActinst[5]);
			data.put("startStr", DateUtils.formatLongDate((Date)histActinst[7]));
			data.put("ea_Content", histActinst[6] != null? histActinst[6] : "");
			data.put("endStr", DateUtils.formatLongDate((Date)histActinst[8]));
			datas.add(data);
		}
		
		map.put("datas", datas);
		
		return map;
	}
	
	/**
	 * 
	 * flowChart:读取流程图
	 * 
	 * @author 杨鹏
	 * @param jbpmHistProcinstId:发布流程ID
	 * @param response
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/jbpm/flowChart.f")
	public void flowChart(Long jbpmHistProcinstId, ServletResponse response) throws Exception {
		OutputStream outputStream=null;
		InputStream inputStream=null;
		try {
			outputStream = response.getOutputStream();
			RepositoryService repositoryService = o_jBPMOperate.getRepositoryService();
			JbpmHistProcinst jbpmHistProcinst = o_jbpmHistProcinstBO.findJbpmHistProcinstById(jbpmHistProcinstId);
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(jbpmHistProcinst.getvJbpmDeployment().getPdid()).uniqueResult();
			inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),processDefinition.getImageResourceName());
			response.reset();
			IOUtils.copy(inputStream, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(null!=outputStream){
				outputStream.flush();
				outputStream.close();
			}
			if(null!=inputStream){
				inputStream.close();
			}
		}
	}
	
	/**
	 * 
	 * toDoTaskAC:读取当前代办活动坐标
	 * 
	 * @author 杨鹏
	 * @param processInstanceId
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/jbpm/toDoTaskAC.f")
	public List<Map<String, Object>> toDoTaskAC(String processInstanceId) {
		ExecutionService executionService = o_jBPMOperate.getExecutionService();
		ProcessInstance processInstance = executionService.findProcessInstanceById(processInstanceId);
		List<Map<String, Object>> activityCoordinatesList = new ArrayList<Map<String, Object>>();
		if(processInstance!=null){
			RepositoryService repositoryService = o_jBPMOperate.getRepositoryService();
			Set<String> activityNames = processInstance.findActiveActivityNames();
			for (String activityName : activityNames) {
				ActivityCoordinates activityCoordinates = repositoryService.getActivityCoordinates(processInstance.getProcessDefinitionId(),activityName);
				Map<String, Object> activityCoordinatesMap=new HashMap<String, Object>();
				activityCoordinatesMap.put("height", activityCoordinates.getHeight());
				activityCoordinatesMap.put("width", activityCoordinates.getWidth());
				activityCoordinatesMap.put("x", activityCoordinates.getX());
				activityCoordinatesMap.put("y", activityCoordinates.getY());
				activityCoordinatesList.add(activityCoordinatesMap);
			}
		}
		return activityCoordinatesList;
	}
	
	/**
	 * 
	 * findProcessDefinitionDeployById:读取流程定义
	 * 
	 * @author 杨鹏
	 * @param processDefinitionDeployId
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/jbpm/processInstance/findBusinessWorkFlowBySome.f")
	public Map<String,Object> findBusinessWorkFlowBySome(String id,String processInstanceId,String businessId) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<BusinessWorkFlow> list = o_jBPMBO.findBusinessWorkFlowBySome(id, processInstanceId, businessId);
		BusinessWorkFlow businessWorkFlow = list.get(0);
		List<JbpmHistProcinst> jbpmHistProcinsts = o_jbpmHistProcinstBO.findJbpmHistProcinstBySome(businessWorkFlow.getProcessInstanceId());
		JbpmHistProcinst jbpmHistProcinst = jbpmHistProcinsts.get(0);
		Long jbpmHistProcinstId = jbpmHistProcinst.getId();
		map.put("jbpmHistProcinstId", jbpmHistProcinstId);
		map.put("processInstanceId", businessWorkFlow.getProcessInstanceId());
		return map;
	}
	
	
	/**
	 * 
	 * jbpmHistProcinstToMap:jbpm历史实例转Map
	 * 
	 * @author 杨鹏
	 * @param jbpmHistProcinst
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> jbpmHistProcinstToMap(JbpmHistProcinst jbpmHistProcinst){
		Map<String,Object> map = new HashMap<String, Object>();
		if(jbpmHistProcinst!=null){
			Long id = jbpmHistProcinst.getId();
			String endactivity=jbpmHistProcinst.getEndactivity();
			String endactivityShow="";
			BusinessWorkFlow businessWorkFlow = jbpmHistProcinst.getBusinessWorkFlow();
			
			String businessName="";
			String createTime="";
			String createByRealname="";
			String processInstanceId="";
			String jbpmDeploymentName = "";
			String url="";
			String businessId = "";
			String rate = "";
			
			if("end1".equals(endactivity)){
				endactivityShow="已完成";
			}else if("remove1".equals(endactivity)){
				endactivityShow="已删除";
			}else{
				endactivityShow="执行中";
			}

			if(businessWorkFlow!=null){
				businessName = businessWorkFlow.getBusinessName();
				url = businessWorkFlow.getUrl();
				businessId = businessWorkFlow.getBusinessId();
				rate = businessWorkFlow.getRate();
				SysEmployee createByEmp = businessWorkFlow.getCreateByEmp();
				if(null != createByEmp){
					if(Contents.SYSTEM.equals(businessWorkFlow.getCreateBy())){
						createByRealname = Contents.SYSTEM;
					}else{
						createByRealname = createByEmp.getRealname();
					}
				}
				Date date = businessWorkFlow.getCreateTime();
				if(date!=null){
					createTime = new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(date);
				}
				processInstanceId = businessWorkFlow.getProcessInstanceId();
			}
			VJbpmDeployment vJbpmDeployment = jbpmHistProcinst.getvJbpmDeployment();
			if(vJbpmDeployment!=null){
				ProcessDefinitionDeploy processDefinitionDeploy = vJbpmDeployment.getProcessDefinitionDeploy();
				if(null!=processDefinitionDeploy){
					jbpmDeploymentName = processDefinitionDeploy.getDisName();
				}
			}
			
			map.put("id", id);
			map.put("endactivity", endactivity);
			map.put("endactivityShow", endactivityShow);
			map.put("businessName", businessName);
			map.put("createByRealname", createByRealname);
			map.put("createTime", createTime);
			map.put("jbpmDeploymentName", jbpmDeploymentName);
			map.put("processInstanceId", processInstanceId);
			map.put("url", url);
			map.put("businessId", businessId);
			map.put("rate", rate);
		}
		return map;
	}

	/**
	 * 
	 * processDefinitionDeployToMap:将流程定义类转为map
	 * 
	 * @author 杨鹏
	 * @param processDefinitionDeploy
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> processDefinitionDeployToMap(ProcessDefinitionDeploy processDefinitionDeploy){
		String id = processDefinitionDeploy.getId();
		String disName = processDefinitionDeploy.getDisName();
		String url = processDefinitionDeploy.getUrl();
		String fileName="";
		
		FileUploadEntity fileUploadEntity = processDefinitionDeploy.getFileUploadEntity();
		if(null!=fileUploadEntity){
			fileName = fileUploadEntity.getOldFileName();
		}
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("disName", disName);
		map.put("url", url);
		map.put("fileName", fileName);
		return map;
	}
	
	/**
	 * 
	 * vJbpmDeploymentToMap:将发布后的流程转为map格式
	 * 
	 * @author 杨鹏
	 * @param vJbpmDeployment
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> vJbpmDeploymentToMap(VJbpmDeployment vJbpmDeployment){
		String id = vJbpmDeployment.getId();
		String pdid = vJbpmDeployment.getPdid();
		Integer pdversion = vJbpmDeployment.getPdversion();
		Integer executionCount = vJbpmDeployment.getExecutionCount();
		String processDefinitionDeployId=null;
		String disName = "";
		
		if(null==executionCount){
			executionCount=0;
		}
		ProcessDefinitionDeploy processDefinitionDeploy = vJbpmDeployment.getProcessDefinitionDeploy();
		if(null!=processDefinitionDeploy){
			processDefinitionDeployId = processDefinitionDeploy.getId();
			disName = processDefinitionDeploy.getDisName();
		}
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("pdid", pdid);
		map.put("pdversion", pdversion);
		map.put("executionCount", executionCount);
		map.put("processDefinitionDeployId", processDefinitionDeployId);
		map.put("disName", disName);
		return map;
	}
	/**
	 * 
	 * jbpmHistActinstToMap:将历史节点转成MAP
	 * 
	 * @author 杨鹏
	 * @param jbpmHistActinst
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> jbpmHistActinstToMap(JbpmHistActinst jbpmHistActinst){
		Long id=jbpmHistActinst.getId();
		Long dbversion =null;
		String disName = "";
		
		String processInstanceId = "";
		String dbversionStr="";
		String activityName = jbpmHistActinst.getActivityName();
		String businessId="";
		String businessName="";
		String assigneeRealname="";
		String assigneeRealCode="";
		String assigneeCompanyName="";
		String assigneeOrgNamesStr="";
		String createByRealname="";
		String startStr="";
		String startDate="";
		String ea_Content="";
		String ea_Operate = "";
		String gatherBy="";
		String endStr="";
		String form="";
		String rate="0";
		String executionId="";
		
		JbpmHistProcinst jbpmHistProcinst = jbpmHistActinst.getJbpmHistProcinst();
		VJbpmDeployment vJbpmDeployment = jbpmHistProcinst.getvJbpmDeployment();
		ProcessDefinitionDeploy processDefinitionDeploy = vJbpmDeployment.getProcessDefinitionDeploy();
		disName = processDefinitionDeploy.getDisName();
		dbversion = jbpmHistActinst.getDbversion();
		if(dbversion==0){
			dbversionStr="未处理";
		}else if(dbversion==1){
			dbversionStr="已处理";
		}
		VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
		
		if(jbpmHistTask!=null){
			SysEmployee assignee = jbpmHistTask.getAssignee();
			SysEmployee taskAssignee = jbpmHistTask.getTaskAssignee();
			if(assignee!=null){
				assigneeRealCode=assignee.getEmpcode();
				assigneeRealname = assignee.getRealname();
				SysOrganization assigneeCompany = assignee.getSysOrganization();
				if(assigneeCompany!=null){
					assigneeCompanyName=assigneeCompany.getOrgname();
				}
				/*Set<SysEmpOrg> sysEmpOrgs = assignee.getSysEmpOrgs();
				for (SysEmpOrg sysEmpOrg : sysEmpOrgs) {
					assigneeOrgNames.add(sysEmpOrg.getSysOrganization().getOrgname());
				}*/
			}else if(taskAssignee!=null){
				assigneeRealCode=taskAssignee.getEmpcode();
				assigneeRealname=taskAssignee.getRealname();
				SysOrganization assigneeCompany = taskAssignee.getSysOrganization();
				if(assigneeCompany!=null){
					assigneeCompanyName=assigneeCompany.getOrgname();
				}
				/*Set<SysEmpOrg> sysEmpOrgs = taskAssignee.getSysEmpOrgs();
				for (SysEmpOrg sysEmpOrg : sysEmpOrgs) {
					assigneeOrgNames.add(sysEmpOrg.getSysOrganization().getOrgname());
				}*/
			}
			/*assigneeOrgNamesStr=StringUtils.join(assigneeOrgNames.toArray(),",");*/
			
			ExamineApproveIdea examineApproveIdea = jbpmHistTask.getExamineApproveIdea();
			if(examineApproveIdea!=null){
				ea_Content=examineApproveIdea.getEa_Content();
				ea_Operate = examineApproveIdea.getEa_Operate();
				gatherBy = examineApproveIdea.getGatherBy().getRealname();
			}
			
			Date end = jbpmHistTask.getEnd();
			if(end!=null){
				endStr=new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(end);
			}
			form = jbpmHistTask.getForm();
		}
		if(jbpmHistProcinst!=null){
			BusinessWorkFlow businessWorkFlow = jbpmHistProcinst.getBusinessWorkFlow();
			if(null!=businessWorkFlow){
				businessId = businessWorkFlow.getBusinessId();
				businessName = businessWorkFlow.getBusinessName();
				if(businessWorkFlow!=null){
					processInstanceId = businessWorkFlow.getProcessInstanceId();
					if(StringUtils.isNotBlank(businessWorkFlow.getRate())){
						rate = businessWorkFlow.getRate();
					}
					String createBy = businessWorkFlow.getCreateBy();
					if(null != createBy && !Contents.SYSTEM.equals(createBy)){
						createByRealname = businessWorkFlow.getCreateByEmp().getRealname();
					} else {
						createByRealname = createBy;
					}
				}
			}
		}
		Date start = jbpmHistActinst.getStart();
		if(start!=null){
			startStr = new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(start);
			startDate = new  SimpleDateFormat( "yyyy-MM-dd" ).format(start);
		}
		executionId = jbpmHistActinst.getExecution_();
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("disName", disName);
		map.put("activityName", activityName);
		map.put("dbversion", dbversion);
		map.put("dbversionStr", dbversionStr);
		map.put("businessId", businessId);
		map.put("businessName", businessName);
		map.put("assigneeRealCode", assigneeRealCode);
		map.put("assigneeRealname", assigneeRealname);
		map.put("assigneeCompanyName", assigneeCompanyName);
		map.put("assigneeOrgNamesStr", assigneeOrgNamesStr);
		map.put("createByRealname", createByRealname);
		map.put("startStr", startStr);
		map.put("startDate", startDate);
		map.put("ea_Content", ea_Content);
		map.put("ea_Operate", ea_Operate);
		map.put("gatherBy", gatherBy);
		map.put("endStr", endStr);
		map.put("form", form);
		map.put("executionId", executionId);
		map.put("processInstanceId", processInstanceId);
		map.put("rate", rate);
		
		return map;
	}
	
	public Map<String, Object> vJbpmHistActinstToMap(VJbpmHistActinst vJbpmHistActinst){
			Long id=vJbpmHistActinst.getId();
			String activityName = vJbpmHistActinst.getActivityName();
			String executionId = vJbpmHistActinst.getExecutionId();
			String assigneeCompanyId = vJbpmHistActinst.getAssigneeCompanyId();
			String assigneeCompanyName = vJbpmHistActinst.getAssigneeCompanyName();
			String assigneeOrgId = vJbpmHistActinst.getAssigneeOrgId();
			String assigneeOrgName = vJbpmHistActinst.getAssigneeOrgName();
			String assigneeId = vJbpmHistActinst.getAssigneeId();
			String assigneeCode = vJbpmHistActinst.getAssigneeCode();
			String assigneeRealName = vJbpmHistActinst.getAssigneeRealName();
			Long dbversion = vJbpmHistActinst.getDbversion();
			String type = vJbpmHistActinst.getType();
			String transition = vJbpmHistActinst.getTransition();
			Date start = vJbpmHistActinst.getStart();
			Date end = vJbpmHistActinst.getEnd();
			String eaContent = "";
			if(StringUtils.isNotBlank(vJbpmHistActinst.getEaContent())){
				eaContent = vJbpmHistActinst.getEaContent();
			}
			String jbpmHistProcinstKey = vJbpmHistActinst.getJbpmHistProcinstKey();
			Long jbpmHistProcinstId = vJbpmHistActinst.getJbpmHistProcinstId();
			Long jbpmHistTaskId = vJbpmHistActinst.getJbpmHistTaskId();
			String dbversionStr="";
			if(Contents.JBPM_ACTINST_UNTREAD.equals(transition)){
				dbversionStr="退回";
			}else if(Contents.JBPM_ACTINST_REMOVE.equals(transition)){
				dbversionStr="删除";
			}else if(dbversion==0){
				dbversionStr="未处理";
			}else if(dbversion==1){
				dbversionStr="已处理";
			}
			
			String startStr="";
			String startDate="";
			if(start!=null){
				startStr = new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(start);
				startDate = new  SimpleDateFormat( "yyyy-MM-dd" ).format(start);
			}
			String endStr="";
			String endDate="";
			if(end!=null){
				endStr = new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(end);
				endDate = new  SimpleDateFormat( "yyyy-MM-dd" ).format(end);
			}
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("activityName", activityName);
			map.put("executionId", executionId);
			map.put("assigneeCompanyId", assigneeCompanyId);
			map.put("assigneeCompanyName", assigneeCompanyName);
			map.put("assigneeOrgId", assigneeOrgId);
			map.put("assigneeOrgName", assigneeOrgName);
			map.put("assigneeId", assigneeId);
			map.put("assigneeCode", assigneeCode);
			map.put("assigneeRealName", assigneeRealName);
			map.put("dbversion", dbversion);
			map.put("dbversionStr", dbversionStr);
			map.put("type", type);
			map.put("transition", transition);
			map.put("eaContent", eaContent);
			map.put("jbpmHistProcinstKey", jbpmHistProcinstKey);
			map.put("jbpmHistProcinstId", jbpmHistProcinstId);
			map.put("jbpmHistTaskId", jbpmHistTaskId);
			map.put("startStr", startStr);
			map.put("startDate", startDate);
			map.put("endStr", endStr);
			map.put("endDate", endDate);
			
			return map;
	}
	
	/**
	 * 
	 * vJbpm4TaskToMap:
	 * 
	 * @author 杨鹏
	 * @param vJbpm4Task
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> vJbpm4TaskToMap(VJbpm4Task vJbpm4Task){
		Long id=vJbpm4Task.getId();
		String disName = vJbpm4Task.getDisName();
		String activityName = vJbpm4Task.getActivityName();
		String businessId=vJbpm4Task.getBusinessId();
		String businessName=vJbpm4Task.getBusinessName();
		String createStr="";
		String createDate="";
		String form=vJbpm4Task.getForm();
		String rate="0";
		String executionId=vJbpm4Task.getExecutionId();
		Date create = vJbpm4Task.getCreate();
		if(create!=null){
			createStr = new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(create);
			createDate = new  SimpleDateFormat( "yyyy-MM-dd" ).format(create);
		}
		rate=vJbpm4Task.getRate();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("disName", disName);
		map.put("activityName", activityName);
		map.put("businessId", businessId);
		map.put("businessName", businessName);
		map.put("createStr", createStr);
		map.put("createDate", createDate);
		map.put("form", form);
		map.put("executionId", executionId);
		map.put("rate", rate);
		//启动流程时将schm作为流程变量保存在流程实例中 2017年4月14日14:34:08 吉志强添加
		map.put("schm", vJbpm4Task.getSchm());
		
		return map;
	}
	/**
	 * 
	 * mergeTaskAssignee:人员转办
	 * 
	 * @author 杨鹏
	 * @param taskId
	 * @param empId
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/jbpm/mergeTaskAssignee.f")
	public void mergeTaskAssignee(String taskId,String empId){
		o_jBPMBO.mergeTaskAssignee(taskId, empId);
	}
	/**
	 * 
	 * findUntreadToActinsts:
	 * 
	 * @author 杨鹏
	 * @param jbpmHistActinstId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
    @RequestMapping(value = "/jbpm/processInstance/findUntreadToActinsts.f")
    public Map<String,Object> findUntreadToActinsts(Long jbpmHistActinstId){
		Map<String,Object> map=new HashMap<String,Object>();
		List<Map<String, String>> datas=new ArrayList<Map<String, String>>();
		List<HistoryActivityInstanceImpl> list = o_historyActivityInstanceBO.findUntreadToActinstsById(jbpmHistActinstId);
		for (HistoryActivityInstanceImpl historyActivityInstanceImpl : list) {
			String activityName = historyActivityInstanceImpl.getActivityName();
			HashMap<String, String> mapTemp = new HashMap<String, String>();
			mapTemp.put("id", activityName);
			mapTemp.put("name", activityName);
			datas.add(mapTemp);
		}
		map.put("datas", datas);
		return map;
    }
	/**
	 * 
	 * UntreadToActinst:代办退回功能
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @param processInstanceId
	 * @param untreadToActinst
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/jbpm/processInstance/UntreadToActinst.f")
	public Boolean UntreadToActinst(String executionId,String processInstanceId,String untreadToActinst)throws Exception {
		Boolean flag=false;
		try {
			o_jBPMBO.UntreadToActinst(executionId,processInstanceId,untreadToActinst);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	@RequestMapping(value = "/jbpm/removeTask.f")
	public void removeTask(String taskIdsStr, ServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String[] taskIds = taskIdsStr.split(",");
		o_jBPMBO.removeTask(taskIds);
	}
}
