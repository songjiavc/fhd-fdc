package com.fhd.ra.web.controller.contingencyplan.approval;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.ra.business.contingencyplan.ContingencyPlanBO;

@Controller
public class ContingencyPlanMoreApprovalControl {

	@Autowired
	private ContingencyPlanBO o_contingencyPlanBO;
	
	/**
	 * 通用审批
	 */
	@ResponseBody
	@RequestMapping("/contingencyPlan/approval/submitriskidentifyapprovalbysupervisor.f")
	public void submitRiskIdentifyApprovalBySupervisor(String executionId, String businessId, String isPass, String approverId,
	        								String examineApproveIdea, HttpServletResponse response, String approverKey,String approverKeyType,String totalExecutionId) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_contingencyPlanBO.submiApprovalBySupervisor(executionId, businessId, isPass, examineApproveIdea, approverId, approverKey,approverKeyType,totalExecutionId);
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
}