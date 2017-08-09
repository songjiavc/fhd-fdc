/**
 * 
 * 
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-9-27 		宋佳
 * 
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.web.controller.response.execute;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.dao.assess.formulatePlan.RiskAccessPlanDAO;
import com.fhd.entity.response.SolutionRelaRisk;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.response.ResponseListBO;
import com.fhd.ra.business.response.ResponseSolutionBO;
import com.fhd.ra.business.response.execute.ResponseSolutionExecuteBO;
import com.fhd.ra.business.response.execute.ResponseSolutionExecuteBpmBO;


/**
 * 
 */
@Controller
public class ResponseSolutionExecuteBpmControl{
	@Autowired
	private ResponseSolutionExecuteBO o_responseSolutionExecuteBO;
	@Autowired
	private ResponseSolutionExecuteBpmBO o_responseSolutionExecuteBpmBO;
	@Autowired
	private ResponseListBO o_responseListBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private ResponseSolutionBO o_responseSolutionBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	/**
	 * 开启工作流
	 * @param executionId 流程id
	 * @param businessId 业务id@Transactional
    public boolean formulaAutoCalculate()
	 * @param response
	 */
	public boolean startProcess() {
		return o_responseSolutionExecuteBpmBO.startResponseProcess();
	}
	/**
	 * 初始化工作流执行变量
	 * @param executeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/response/bpm/getbpmvalues.f")
	public Map<String,Object> getBpmValues(String executeId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("data", o_responseSolutionExecuteBpmBO.getBpmValuesByExecuteId(executeId));
		map.put("success", true);
		return map;
	}
	/**
	 * 应对执行
	 * @param executionId 流程id
	 * 
	 * @param response
	 */
	@ResponseBody
	@RequestMapping("/response/workflow/responseexecution.f")
	public Map<String,Object> executionProcess(String executionId) {
		return o_responseSolutionExecuteBpmBO.responseExecutionBpm(executionId);
	}
	/**
	 * 应对方案 制定完毕后 提交工作流 
	 * add by 宋佳 
	 * @param executionId 流程id
	 * 
	 * @param response
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/makesolutionplan.f")
	public Map<String,Object> makeSolutionPlanSubmit(String executionId,String businessId) {
		return o_responseSolutionExecuteBpmBO.makeSolutionPlanBpm(executionId,businessId);
	}
	/**
	 * 应对方案 制定完毕后 提交工作流 
	 * add by 宋佳 
	 * @param executionId 流程id
	 * isPass:isPass,
	 * examineApproveIdea:examineApproveIdea
	 * @param response
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/solutionplanbpmapprove.f")
	public Map<String,Object> solutionPlanBpmApprove(String executionId,String businessId,String isPass,
												String examineApproveIdea,String approverId) {
		if(StringUtils.isNotBlank(approverId)){
			return o_responseSolutionExecuteBpmBO.solutionApproveLeader(executionId, businessId, isPass, examineApproveIdea, approverId);
		}else{
			return o_responseSolutionExecuteBpmBO.solutionPlanApproveToEnd(executionId, businessId, isPass, examineApproveIdea);
		}
		
	}
	/**
	 * <pre>
	 *  工作流调用
	 *  工作流汇总
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param constructPlanId
	 * @since  fhd　Ver 1.1
	*/
	public void mergeResponsePlanStatus(String id) {
		// 计划工作流执行完毕，更新所有的计划关联的风险所对应的应对措施
		ResponseListBO o_responseListBO = ContextLoader.getCurrentWebApplicationContext().getBean(ResponseListBO.class);
		ResponseSolutionBO o_responseSolutionBO = ContextLoader.getCurrentWebApplicationContext().getBean(ResponseSolutionBO.class);
		List<String> riskIds = o_responseListBO.getRiskIdsByBusinessId(id);
		List<SolutionRelaRisk> solutionRelaRisks = o_responseListBO.findSolutionsByRisk(riskIds, Contents.DEAL_STATUS_HANDLING, Contents.SOLUTION_PLAN,"",id);
		for(SolutionRelaRisk solutionRelaRisk : solutionRelaRisks){
			o_responseSolutionBO.updateStatusFromSolutionById(solutionRelaRisk.getSolution().getId(), Contents.DEAL_STATUS_FINISHED);
		}
		//应对计划状态更改为已完成
		RiskAccessPlanDAO o_riskAccessPlanDAO = ContextLoader.getCurrentWebApplicationContext().getBean(RiskAccessPlanDAO.class);
		String hqlSql = "update RiskAssessPlan ra set ra.dealStatus = :dealStatus where ra.id = :id";
		Query query = o_riskAccessPlanDAO.createQuery(hqlSql);
		query.setParameter("dealStatus", Contents.DEAL_STATUS_FINISHED);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	/**
	 * 应对计划主管审批
	 * add by 王再冉
	 * 2014-1-16  下午1:32:48
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/riskresponseplanapproval.f")
	public void riskResponsePlanApproval(String executionId, String businessId, String isPass, String examineApproveIdea,
											String approverId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String description = o_jbpmBO.findPDDByExecutionId(executionId);
			if("responseMore".equals(description)){//多级审批
				o_responseSolutionExecuteBpmBO.submitRiskResponseApprovalBySupervisor(executionId, businessId, isPass, examineApproveIdea, approverId);
			}else{
				o_responseSolutionExecuteBpmBO.submitRiskResponseApprovalByLeader(executionId, businessId, isPass, examineApproveIdea);
			}
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
	 * 计划领导审批
	 * add by 王再冉
	 * 2014-1-16  下午3:41:30
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	审批人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/riskresponseplanapprovalleader.f")
	public void riskResponsePlanApprovalLeader(String executionId, String businessId, String isPass, String examineApproveIdea,
											HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_responseSolutionExecuteBpmBO.submitRiskResponseApprovalByLeader(executionId, businessId, isPass, examineApproveIdea);
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
	 * 应对任务分配提交
	 * add by 王再冉
	 * 2014-1-16  下午4:17:31
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param pingguEmpIds	评估人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/submitrisktaskdistribute.f")
	public void submitRiskTaskDistribute(String executionId, String businessId, String pingguEmpIds, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_responseSolutionExecuteBpmBO.submitRiskTaskDistribute(executionId, businessId, pingguEmpIds);
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
	 * 单位主管审批
	 * add by 王再冉
	 * 2014-1-20  下午3:20:27
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	审批人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/solutionplanapprovebysupervisor.f")
	public void solutionPlanApproveBySupervisor(String executionId,String businessId,String isPass,
										String examineApproveIdea,String approverId,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_responseSolutionExecuteBpmBO.solutionPlanApproveBySupervisor(executionId, businessId, isPass, examineApproveIdea, approverId);
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
	 * 单位领导审批
	 * add by 王再冉
	 * 2014-1-18  下午2:25:41
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	审批人id
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/solutionplanapprovebyleader.f")
	public void solutionPlanApproveByLeader(String executionId,String businessId,String isPass,
										String examineApproveIdea,String approverId,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_responseSolutionExecuteBpmBO.solutionPlanApproveByLeader(executionId, businessId, isPass, examineApproveIdea, approverId);
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
	 * 业务分管副总审批
	 * add by 王再冉
	 * 2014-1-18  下午3:03:55
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/solutionplanapprovetoend.f")
	public void solutionPlanApproveToEnd(String executionId,String businessId,String isPass,
														String examineApproveIdea,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_responseSolutionExecuteBpmBO.solutionPlanApproveToEnd(executionId, businessId, isPass, examineApproveIdea);
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
	 * 审批列表查询
	 * add by 王再冉
	 * 2014-1-21  上午10:27:20
	 * desc : 
	 * @param query			搜索框关键字
	 * @param businessId	计划id
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/findapproveGgridbybusinessid.f")
	public ArrayList<HashMap<String, String>> findApproveGridBybusinessId(String query,String businessId){
		return o_responseSolutionExecuteBpmBO.findApproveGridBybusinessId(query, businessId);
	}
	/**
	 * 应对执行开启工作流
	 * add by 王再冉
	 * 2014-1-21  上午11:43:07
	 * desc : 
	 * @param businessId	应对措施id
	 * @param empId			执行人id
	 * @param name			应对措施名称
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/startexcuteresponse.f")
	public void startExcuteResponse(String businessId, String empId, String name, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_responseSolutionExecuteBpmBO.startExcuteResponse(empId, businessId, name);
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
	 * 方案执行--风险应对主管审批
	 * add by 王再冉
	 * 2014-1-21  下午4:56:48
	 * desc : 
	 * @param executionId		工作流程id
	 * @param businessId		计划id
	 * @param isPass			审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId		审批人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/responseplan/workflow/solutionexcutetosupervisor.f")
	public void solutionExcuteToSupervisor(String executionId,String businessId,String isPass,
										String examineApproveIdea,String approverId,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_responseSolutionExecuteBpmBO.solutionExcuteToSupervisor(executionId, businessId, isPass, examineApproveIdea, approverId);
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
	 * 应对执行--个人工作台发起执行
	 * add by 王再冉
	 * @param executionId	工作流id
	 * @param businessId	应对执行id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping("/response/workflow/executesolution.f")
	public Map<String,Object> executeSolution(String executionId,String businessId,String isPass,String examineApproveIdea) {
		return o_responseSolutionExecuteBpmBO.executeSolution(executionId,businessId,isPass,examineApproveIdea);
	}
}
