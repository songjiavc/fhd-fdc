package com.fhd.check.business.yearcheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.check.business.OrgRelaLeaderBO;
import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.check.yearcheck.YearCheckDAO;
import com.fhd.dao.check.yearcheck.YearCheckPlanOrgDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlan;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlanOrg;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.sys.business.organization.EmpGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Service
public class YearCheckBO {
	
	@Autowired
	private YearCheckDAO checkDAO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private YearCheckPlanOrgBO yearCheckPlanOrgBO;
	@Autowired
	private OrgRelaLeaderBO orgRelaLeaderBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private RiskScoreBO riskScoreBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	public Page<YearCheckPlan> findAllPlanTypesGridPage(String query, Page<YearCheckPlan> page, String status) {
		DetachedCriteria dc = DetachedCriteria.forClass(YearCheckPlan.class);

		if(StringUtils.isNotBlank(status)){//处理状态
		dc.add(Restrictions.eq("dealStatus", status));
		}
		if(StringUtils.isNotBlank(query)){
		dc.add(Restrictions.like("planName", query, MatchMode.ANYWHERE));
		}
		
		return checkDAO.findPage(dc, page, false);
	}
	/*
	 * 保存年度考核计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@Transactional
	public Map<String, Object> savaYearCheckPlan(YearCheckPlan yearCheckPlan) throws Exception {
		String companyId = UserContext.getUser().getCompanyid();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		if(StringUtils.isBlank(yearCheckPlan.getId())) {
			yearCheckPlan.setId(Identities.uuid());
		} 
	if(null != yearCheckPlan.getContactName()){
			String conEmpId = "";
			JSONArray conArray = JSONArray.fromObject(yearCheckPlan.getContactName());
			if(null != conArray && conArray.size()>0){
				conEmpId = (String)JSONObject.fromObject(conArray.get(0)).get("id");
			}
			SysEmployee conEmp = o_empGridBO.findEmpEntryByEmpId(conEmpId);
			if(null != conEmp){
				yearCheckPlan.setContactPerson(conEmp);//保存联系人
			}
		}
		if(null != yearCheckPlan.getResponsName()){
			String respEmpId = "";
			JSONArray respArray = JSONArray.fromObject(yearCheckPlan.getResponsName());
			if(null != respArray && respArray.size()>0){
				respEmpId = (String)JSONObject.fromObject(respArray.get(0)).get("id");
			}
			SysEmployee respEmp = o_empGridBO.findEmpEntryByEmpId(respEmpId);
			if(null != respEmp){
				yearCheckPlan.setResponsiblePerson(respEmp);//保存负责人
			}
		}
		if(StringUtils.isNotBlank(yearCheckPlan.getBeginDateStr())){//开始时间
			yearCheckPlan.setBeginDate(DateUtils.parseDate(yearCheckPlan.getBeginDateStr(), "yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(yearCheckPlan.getEndDateStr())){//开始时间
			yearCheckPlan.setEndDate(DateUtils.parseDate(yearCheckPlan.getEndDateStr(), "yyyy-MM-dd"));
		}
		yearCheckPlan.setDealStatus(Contents.DEAL_STATUS_NOTSTART);//点保存按钮，处理状态为"未开始"
		yearCheckPlan.setStatus(Contents.DEAL_STATUS_SAVED);//保存：状态为"已保存"

		checkDAO.merge(yearCheckPlan);;
		//将考核任务分配表数据赋值到年度考核计划部门关系表
		List<Map<String,String>> orgEmpList=orgRelaLeaderBO.findEmpIdsFromOrgRelaEmpByManagedIdAndBussinessId("01",null);
		yearCheckPlanOrgBO.saveEmpOrgByPlan(orgEmpList,yearCheckPlan);
		inmap.put("planId", yearCheckPlan.getId());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}

	public Map<String, Object> findYearCheckPlanById(String id) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		YearCheckPlan plan = checkDAO.get(id);
		if(null != plan){
			inmap.put("id", id);
			inmap.put("name", plan.getName());
			inmap.put("planCode", plan.getPlanCode());
			inmap.put("rangeRequire", plan.getRangeRequire());
			//联系人
			JSONArray conarray = new JSONArray();
			JSONObject conjson = new JSONObject();
			if(null != plan.getContactPerson()){
				conjson.put("id", plan.getContactPerson().getId());
				conjson.put("empno", plan.getContactPerson().getEmpcode());
				conjson.put("empname", plan.getContactPerson().getEmpname());
				conarray.add(conjson);
				inmap.put("contactName",conarray.toString());
				inmap.put("cName",plan.getContactPerson().getEmpname());
			}else{
				//conjson.put("id", "");
				//conarray.add(conjson);
				//inmap.put("contactName",conarray.toString() );
			}
			//负责人
			JSONArray resparray = new JSONArray();
			JSONObject respjson = new JSONObject();
			if(null != plan.getResponsiblePerson()){
				respjson.put("id", plan.getResponsiblePerson().getId());
				respjson.put("empno", plan.getResponsiblePerson().getEmpcode());
				respjson.put("empname", plan.getResponsiblePerson().getEmpname());
				resparray.add(respjson);
				inmap.put("responsName",resparray.toString() );
				inmap.put("rName",plan.getResponsiblePerson().getEmpname() );
			}else{
				//respjson.put("id", "");
				//resparray.add(respjson);
				//inmap.put("responsName",resparray.toString() );
			}
			inmap.put("workTarg", plan.getWorkTarg());
			//开始时间
			if(null != plan.getBeginDate()){
				inmap.put("beginDataStr", plan.getBeginDate().toString().split(" ")[0]);
			}else{
				inmap.put("beginDataStr", "");
			}
			//结束时间
			if(null != plan.getEndDate()){
				inmap.put("endDataStr", plan.getEndDate().toString().split(" ")[0]);
			}else{
				inmap.put("endDataStr", "");
			}
			if(null != plan.getEndDate()&&null != plan.getBeginDate()){
				inmap.put("beginendDateStr", plan.getBeginDate().toString().split(" ")[0]+"-"+plan.getEndDate().toString().split(" ")[0]);
			}
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}

	/**
	 * 年度考核计划提交
	 * @param empIds	承办人id
	 * @param approver	审批人
	 * @param businessId	评估计划id
	 * change by 郭鹏
	 * @return 
	 */
	@SuppressWarnings("deprecation")
	@Transactional
	@RecordLog("提交计划")
	public boolean submitAssessRiskPlanToApproverSecrecy(String approverId, String businessId, 
										String executionId){
		YearCheckPlan yearCheckPlan = checkDAO.get(businessId);
		String makePlanEmpId = UserContext.getUser().getEmpid();//制定计划人员id
		String approver="";
		JSONArray jsonArray = JSONArray.fromObject(approverId);
		if (jsonArray.size() > 0){
			 JSONObject jsobj = jsonArray.getJSONObject(0);
			 approver = jsobj.getString("id");//审批人
		}
		if(null != yearCheckPlan){
			yearCheckPlan.setStatus(Contents.STATUS_SUBMITTED);//计划状态
			yearCheckPlan.setDealStatus(Contents.DEAL_STATUS_HANDLING);//计划处理状态
			if (!"".equals(approver)) {
				if(StringUtils.isBlank(executionId)){
					//菜单触发提交开启工作流
					Map<String, Object> variables = new HashMap<String, Object>();
					String entityType = "YearCheck";//流程名称
					variables.put("entityType", entityType);
					variables.put("riskMainLeader", approver);//审批人
					variables.put("id", businessId);
					variables.put("name", yearCheckPlan.getName());//评估计划名称
					variables.put("companyId", UserContext.getUser().getCompanyid());
					variables.put("planMakerEmpId", makePlanEmpId);//计划制定人
					executionId = o_jbpmBO.startProcessInstance(entityType,variables);//开启工作流
					//工作流提交
					Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
					variablesBpmTwo.put("yearCheckPlanApproverEmpId", approver);
					//
					o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
				}else{
					//工作流提交
					Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
					variablesBpmTwo.put("yearCheckPlanApproverEmpId", approver);
					o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
				}
			}
	
		}
		return true;
	}
	@Transactional
	public Map<String, Object> deleteYearPlan(String id) {
		Map<String, Object> data=new HashMap<String, Object>();
		boolean t=true;
		try {
			String ids[]=id.split(",");
			for (int i = 0; i < ids.length; i++) {
				YearCheckPlan checkPlan=new YearCheckPlan();
				checkPlan.setId(ids[i]);
				checkDAO.delete(checkPlan);
			}
		} catch (Exception e) {
			t=false;
			e.printStackTrace();
		   
		}
		data.put("data", t);
		
		return data;
	}
	/**
	 * 年度考核计划发起主管领导审批
	 * AUTHOR:Perry Guo
	 * DATE:2017-08-01
	 * */
	@Transactional
	public void mergeYearCheckPlanApproval(String executionId, String businessId, String isPass,
			String examineApproveIdea, String approverId) {
		YearCheckPlan assessPlan = checkDAO.get(businessId);//计划实体
		if(null != assessPlan){
			if ("no".equals(isPass)) {//审批未通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{// 审批通过，工作流提交
				String riskCharge="";
				JSONArray jsonArray = JSONArray.fromObject(approverId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 riskCharge = jsobj.getString("id");//审批人
				}
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("riskCharge", riskCharge);//承办人
				variables.put("examineApproveIdea", examineApproveIdea);
				variables.put("planId", businessId);
				o_jbpmBO.doProcessInstance(executionId, variables);
		
	}
		}}
	
	/**
	 * 年度考核计划发起负责人审批
	 * AUTHOR:Perry Guo
	 * DATE:2017-08-01
	 * */
	@Transactional
	public void mergeYearCheckPlanApprovalForCharge(String executionId, String businessId, String isPass,
			String examineApproveIdea) {
		YearCheckPlan assessPlan = checkDAO.get(businessId);//计划实体
		if(null != assessPlan){
			if ("no".equals(isPass)) {//审批未通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{// 审批通过，工作流提交
				Map<String, Object> variables = new HashMap<String, Object>();
				List<SysEmployee> raters=new ArrayList<SysEmployee>();
				List<YearCheckPlanOrg> checkPlans=yearCheckPlanOrgBO.getPlanOrgByPlanID(businessId,null,null);
				for (int i = 0; i < checkPlans.size(); i++) {
					SysEmployee emp=riskScoreBO.findEmpsByRoleIdAnddeptId(checkPlans.get(i).getOrgId().getId(),"DeptRiskManager");
					raters.add(emp);
				}
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				variables.put("raters", raters);
				variables.put("checkCount", checkPlans.size());
				variables.put("planId", businessId);
				o_jbpmBO.doProcessInstance(executionId, variables);
	}
		
	}
	}
	
	/**
	 * 年度考核计划发起负责人审批
	 * AUTHOR:Perry Guo
	 * DATE:2017-08-01
	 * */
	@Transactional
	public void submitOwenMark(String executionId, String businessId,String approverId) {
				Map<String, Object> variables = new HashMap<String, Object>();
				String orgID=UserContext.getUser().getMajorDeptId();
				SysEmployee emp=UserContext.getUser().getEmp();
				String approver="";
				JSONArray jsonArray = JSONArray.fromObject(approverId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 approver = jsobj.getString("id");//审批人
				}
				variables.put("empId", emp.getId());
				variables.put("empName", emp.getEmpname());
				variables.put("orgId", orgID);
				variables.put("raterLeader", approver);
				o_jbpmBO.doProcessInstance(executionId, variables);
	
	}
}
