package com.fhd.check.web.controller.yearcheck;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

import com.fhd.check.business.checkDetail.CheckDetailBO;
import com.fhd.check.business.yearcheck.YearCheckBO;
import com.fhd.check.business.yearcheck.YearCheckPlanOrgBO;
import com.fhd.check.business.yearcheck.YearCheckScoreOrgBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.check.checkdetail.CheckDetail;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlan;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlanForm;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlanOrg;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlanOrgForm;
import com.fhd.entity.check.yearcheck.plan.YearCheckScoreOrg;
import com.fhd.fdc.utils.UserContext;


@Controller
public class checkYearController {

	@Autowired
	private YearCheckBO yearCheckBO;
	@Autowired
	private YearCheckPlanOrgBO checkPlanOrgBO;
	@Autowired
	private YearCheckScoreOrgBO yearCheckScoreOrgBO;
	
	/**
	 * 分页查询所有年度考核
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/yearcheck/findAllPlans.f")
	public Map<String, Object> findAllPlanTypesGridPageNew(int start, int limit, String query, String sort,String status){
		Page<YearCheckPlan> page = new Page<YearCheckPlan>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = yearCheckBO.findAllPlanTypesGridPage(query, page,status);
		List<YearCheckPlan> entityList = page.getResult();
		List<YearCheckPlanForm> datas = new ArrayList<YearCheckPlanForm>();
		for(YearCheckPlan plan : entityList){
			
			datas.add(new YearCheckPlanForm(plan));
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	/*
	 * 保存年度考核计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/yearcheck/savaYearCheckPlan.s")
	public Map<String, Object> savaYearCheckPlan(YearCheckPlan yearCheckPlan) throws Exception{
		return yearCheckBO.savaYearCheckPlan(yearCheckPlan);
	}
	
	/*
	 * 删除计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/yearcheck/deleteYearPlan.d")
	public Map<String, Object> deleteYearPlan(String id) throws Exception{
		return yearCheckBO.deleteYearPlan(id);
	}
	/*
	 * 根据ID查询年度考核计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/yearcheck/findYearCheckPlanById.f")
	public Map<String, Object> findYearCheckPlanById(String id){
		return yearCheckBO.findYearCheckPlanById(id);
	}
	
	/*
	 * 根据计划ID查询部门管理人员关系
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/yearcheck/findEmpOrgByPlanId.f")
	public List<YearCheckPlanOrgForm> findEmpOrgByPlanId(String planId){
	
		List<YearCheckPlanOrg> orgList=checkPlanOrgBO.getPlanOrgByPlanID(planId,null,null);
		List<YearCheckPlanOrgForm> orgFormList=new ArrayList<YearCheckPlanOrgForm>();
		for (YearCheckPlanOrg checkPlanOrg:orgList) {
			YearCheckPlanOrgForm checkPlanOrgForm=new YearCheckPlanOrgForm(checkPlanOrg);
			orgFormList.add(checkPlanOrgForm);
		}
		return orgFormList ;
	}
	
	/**
	 * 提交考核计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/yearcheck/submitYearCheckPlan.f")
	public boolean submitYearCheckPlan(String approverId, String businessId, 
			String executionId){
		
		return yearCheckBO.submitAssessRiskPlanToApproverSecrecy(approverId, businessId, executionId);
	}
	
	/**
	 * 提交考核计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-30
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/yearcheck/checkYearCheckPlan.f")
	public Map<String, Object> checkYearCheckPlan(String approverId, String businessId, 
			String executionId){
		
		return yearCheckBO.checkYearCheckPlan(approverId, businessId, executionId);
	}
	
	/**
	 * 计划提交主管领导审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/yearCheckLeaderApproval.s")
	public void yearCheckLeaderApproval(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.mergeYearCheckPlanApproval(executionId, businessId, isPass, examineApproveIdea,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	/**
	 * 计划提交负责人审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/yearCheckLeaderApprovalForCharge.s")
	public void yearCheckLeaderApprovalForCharge(
			String executionId, String businessId, String isPass, String examineApproveIdea, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.mergeYearCheckPlanApprovalForCharge(executionId, businessId, isPass, examineApproveIdea);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 查询打分表
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @return 
	 */
	@RequestMapping("/check/yearcheck/findCheckRuleByEmp.f")
	@ResponseBody
	public List<YearCheckScoreOrg> findCheckRuleByEmp(String executionId, String businessId) {
		List<YearCheckScoreOrg> scoreList=yearCheckScoreOrgBO.findCheckRuleByEmp(businessId,null);
		return scoreList;
	}
	
	/**
	 * 查询打分表Map
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @return 
	 */
	@RequestMapping("/check/yearcheck/findCheckRuleByEmpMap.f")
	@ResponseBody
	public List<Map<String, Object>> findCheckRuleByEmpMap(String executionId, String businessId) {
		String orgId=UserContext.getUser().getMajorDeptId();
		List<YearCheckScoreOrg> scoreList=yearCheckScoreOrgBO.findCheckRuleByEmp(businessId,orgId);
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		for(YearCheckScoreOrg scoreOrg:scoreList){
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("id", scoreOrg.getId());
		map.put("checkProjectName", scoreOrg.getCheckProjectName());
		map.put("checkProjectScore", scoreOrg.getCheckProjectScore());
		map.put("checkCommenttName", scoreOrg.getCheckCommenttName());
		map.put("checkDetailName", scoreOrg.getCheckDetailName());
		map.put("checkDetailDescribe", scoreOrg.getCheckDetailDescribe());
		map.put("checkDetailScore", scoreOrg.getCheckDetailScore());
		map.put("maxScore", scoreOrg.getCheckDetailScore()-scoreOrg.getCheckDetailAutoSub());
		data.add(map);
		}
		return data;
	}
	
	/**
	 * 根据OrgId查询打分表Map
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @return 
	 */
	@RequestMapping("/check/yearcheck/findCheckRuleByEmpId.f")
	@ResponseBody
	public List<Map<String, Object>> findCheckRuleByEmpId(String executionId, String businessId,String orgId) {
		List<YearCheckScoreOrg> scoreList=yearCheckScoreOrgBO.findCheckRuleByEmp(businessId, orgId);
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		for(YearCheckScoreOrg scoreOrg:scoreList){
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("id", scoreOrg.getId());
		map.put("checkProjectName", scoreOrg.getCheckProjectName());
		map.put("checkProjectScore", scoreOrg.getCheckProjectScore());
		map.put("checkCommenttName", scoreOrg.getCheckCommenttName());
		map.put("checkDetailName", scoreOrg.getCheckDetailName());
		map.put("checkDetailDescribe", scoreOrg.getCheckDetailDescribe());
		map.put("checkDetailScore", scoreOrg.getCheckDetailScore());
		map.put("owenScore", scoreOrg.getOwenScore());
		map.put("riskScore", scoreOrg.getRiskScore());
		map.put("auditScore", scoreOrg.getAuditScore());
		map.put("maxScore", scoreOrg.getCheckDetailScore()-scoreOrg.getCheckDetailAutoSub());
		data.add(map);
		}
		return data;
	}
	
	/**
	 * 根据OrgId查询打分表Map
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @return 
	 */
	@RequestMapping("/check/yearcheck/findCheckRuleByOrgId.f")
	@ResponseBody
	public List<Map<String, Object>> findCheckRuleByOrgId(String executionId, String businessId) {
		List<YearCheckScoreOrg> scoreList=yearCheckScoreOrgBO.findCheckRuleByOrgId(businessId,executionId);
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		for(YearCheckScoreOrg scoreOrg:scoreList){
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("id", scoreOrg.getId());
		map.put("checkProjectName", scoreOrg.getCheckProjectName());
		map.put("checkProjectScore", scoreOrg.getCheckProjectScore());
		map.put("checkCommenttName", scoreOrg.getCheckCommenttName());
		map.put("checkDetailName", scoreOrg.getCheckDetailName());
		map.put("checkDetailDescribe", scoreOrg.getCheckDetailDescribe());
		map.put("checkDetailScore", scoreOrg.getCheckDetailScore());
		map.put("owenScore", scoreOrg.getOwenScore());
		map.put("riskScore", scoreOrg.getRiskScore());
		map.put("auditScore", scoreOrg.getAuditScore());
		map.put("Score", scoreOrg.getAuditScore());
		map.put("maxScore", scoreOrg.getCheckDetailScore()-scoreOrg.getCheckDetailAutoSub());
		data.add(map);
		}
		return data;
	}
	
	/**
	 * 自评提交
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param approverId	审批人id
	 * @param data	          打分信息
	 * @return void
	 */
	@RequestMapping("/check/yearcheck/subOwenMark.f")
	@ResponseBody
	public void subOwenMark(String executionId,String businessId,String approverId,String data,HttpServletResponse response) {
		
		
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckScoreOrgBO.savaCheckScoreOrg(null, data);
			yearCheckBO.submitOwenMark(executionId, businessId,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}	
	}
	
	/**
	 * 风险办评分提交
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param approverId	审批人id
	 * @param data	          打分信息
	 * @return void
	 */
	@RequestMapping("/check/yearcheck/subRiskMark.f")
	@ResponseBody
	public void subRiskMark(String executionId,String businessId,String data,HttpServletResponse response) {
		
		
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckScoreOrgBO.savaCheckScoreOrg(null, data);
			yearCheckBO.submitRiskMark(executionId, businessId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}	
	}
	/**
	 * 自评提交负责人领导审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submOwenMarkForLeader.s")
	public void submOwenMarkForLeader(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId ,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submOwenMarkForLeader(executionId, businessId, isPass, examineApproveIdea,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 自评提交负责人领导审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submOwenMarkForCharge.s")
	public void submOwenMarkForCharge(
			String executionId, String businessId, String isPass,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submOwenMarkForCharge(executionId, businessId, isPass);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	/**
	 * 查询所有评分单位
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @return 
	 */
	@RequestMapping("/check/yearcheck/findCheckGroupByOrg.f")
	@ResponseBody
	public List<Map<String,Object>> findCheckGroupByOrg(String executionId,String businessId,boolean isAll) {
		
		return yearCheckScoreOrgBO.findCheckGroupByOrg(executionId,businessId,isAll);
	}
	
	/**
	 * 分数汇总
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @return 
	 */
	@RequestMapping("/check/yearcheck/findCheckScoreGroup.f")
	@ResponseBody
	public List<Map<String,Object>> findCheckScoreGroup(String executionId,String businessId) {
		List<Map<String,Object>>  list=yearCheckScoreOrgBO.findCheckGroupByOrg(executionId,businessId,true);
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> map=list.get(i);
			BigDecimal owenScore=(BigDecimal) list.get(i).get("owenScore"); 
			BigDecimal riskScore=(BigDecimal) list.get(i).get("riskScore"); 
			BigDecimal auditScore=(BigDecimal) list.get(i).get("auditScore"); 
			//TODO 公式设置
			Double finalScore= 0.2*owenScore.doubleValue()+0.5*riskScore.doubleValue()+0.3*auditScore.doubleValue();
			map.put("finalScore",new DecimalFormat("#.00").format(finalScore));
		}
		return  list;
	}
	
	/**
	 * 审计处评分提交
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param approverId	审批人id
	 * @param data	          打分信息
	 * @return void
	 */
	@RequestMapping("/check/yearcheck/submitAuditMark.f")
	@ResponseBody
	public void submitAuditMark(String executionId,String businessId,String approverId,String data,HttpServletResponse response) {
		
		
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckScoreOrgBO.savaCheckScoreOrg(null, data);
			yearCheckBO.submitAuditMark(executionId, businessId,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
		
	}
	
	/**
	 * 逐个部门修改得分
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param approverId	审批人id
	 * @param data	                      打分信息
	 * @return void
	 */
	@RequestMapping("/check/yearcheck/savaMarkInfo.s")
	@ResponseBody
	public void savaMarkInfo(String executionId,String businessId,String approverId,String data,HttpServletResponse response) {
		
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckScoreOrgBO.savaCheckScoreOrg(null, data);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
		
	}
	/**
	 * 审计处提交领导审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submAuditMarkForLeader.s")
	public void submAuditMarkForLeader(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId ,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submAuditMarkForLeader(executionId, businessId, isPass, examineApproveIdea,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 审计处提交领导审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submAuditMarkForCharge.s")
	public void submAuditMarkForCharge(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId ,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submAuditMarkForCharge(executionId, businessId, isPass, examineApproveIdea);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 汇总提交领导审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submTidyMark.s")
	public void submTidyMark(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId ,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submTidyMark(executionId, businessId, isPass, examineApproveIdea,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 汇总提交负责人审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submTidyMarkForLeader.s")
	public void submTidyMarkforLeader(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId ,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submTidyMarkforLeader(executionId, businessId, isPass, examineApproveIdea,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	/**
	 * 汇总提交集团副总审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submTidyMarkforCharge.s")
	public void submTidyMarkforCharge(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId ,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submTidyMarkforCharge(executionId, businessId, isPass, examineApproveIdea,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 汇总提交集团副总审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param approverId    主管领导ID
	 * @param response
	 */
	@RequestMapping("/check/yearcheck/submTidyMarkforPresident.s")
	public void submTidyMarkforPresident(
			String executionId, String businessId, String isPass, String examineApproveIdea,String approverId ,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			yearCheckBO.submTidyMarkforPresident(executionId, businessId, isPass, examineApproveIdea,approverId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	public void autoInsertScoreOrg(String businessId){
		
		YearCheckPlanOrgBO yearCheckPlanOrgBO = ContextLoader.getCurrentWebApplicationContext().getBean(YearCheckPlanOrgBO.class);
		CheckDetailBO checkDetailBO = ContextLoader.getCurrentWebApplicationContext().getBean(CheckDetailBO.class);
		YearCheckScoreOrgBO yearCheckScoreOrgBO = ContextLoader.getCurrentWebApplicationContext().getBean(YearCheckScoreOrgBO.class);
		List<YearCheckPlanOrg> checkPlans=yearCheckPlanOrgBO.getPlanOrgByPlanID(businessId.toString(),null,null);
	
		//将打分表及评价准则表数据复制
		List<CheckDetail> detailList=checkDetailBO.findCheckDetail(null);
		List<YearCheckScoreOrg> yearCheckScoreOrgs=new ArrayList<YearCheckScoreOrg>();
		for (int i = 0; i < checkPlans.size(); i++) {
			for (CheckDetail checkDetail:detailList) {
				YearCheckScoreOrg yearCheckScoreOrg=new YearCheckScoreOrg();
				yearCheckScoreOrg.setCheckDetailName(checkDetail.getName());
				yearCheckScoreOrg.setCheckDetailDescribe(checkDetail.getDetailStandard());
				yearCheckScoreOrg.setCheckDetailScore(checkDetail.getDetailScore());
				yearCheckScoreOrg.setCheckCommenttName(checkDetail.getCheckComment().getName());
				yearCheckScoreOrg.setCheckProjectName(checkDetail.getCheckComment().getProject().getName());
				yearCheckScoreOrg.setCheckProjectScore(Integer.parseInt(checkDetail.getCheckComment().getProject().getTotalScore()));
				yearCheckScoreOrg.setPlanOrgId(checkPlans.get(i));
				yearCheckScoreOrgs.add(yearCheckScoreOrg);
			}
		}
		yearCheckScoreOrgBO.savaCheckScoreOrg(yearCheckScoreOrgs,null);
	}
}
