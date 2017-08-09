package com.fhd.ra.web.controller.assess.approval;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.risk.Risk;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.approval.ApprovalAssessBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.business.riskidentify.RiskIdentifyBO;
import com.fhd.sys.business.orgstructure.SysEmpOrgBO;

@Controller
public class AssessMoreApprovalControl {

	@Autowired
	private RiskIdentifyBO o_riskIdentifyBO;
	
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private SysEmpOrgBO o_sysEmpOrgBO;
	@Autowired
	private ApprovalAssessBO o_approvalAssessBO;
	
	@Autowired
	private RiskOrgBO o_riskOrgBO;
	
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	
	
	/**
	 * 评估计划主管审批
	 */
	@ResponseBody
	@RequestMapping("/access/approval/submitriskidentifyapprovalbysupervisor.f")
	public void submitRiskIdentifyApprovalBySupervisor(String executionId, String businessId, String isPass, String approverId,
														String examineApproveIdea, HttpServletResponse response, String approverKey) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitRiskIdentifyApprovalBySupervisor(executionId, businessId, isPass, examineApproveIdea, approverId, approverKey);
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
	  * @Description: 保密风险最后一级审批提交方法
	  * @author jia.song@pcitc.com
	  * @date 2017年5月25日 上午9:05:37 
	  * @param executionId
	  * @param businessId
	  * @param isPass
	  * @param approverId
	  * @param examineApproveIdea
	  * @param response
	  * @param approverKey 
	  */
	@ResponseBody
	@RequestMapping("/access/approval/submittidyriskassessriskforsecurity.f")
	public void submitTidyRiskAssessRiskForSecurity(String executionId, String businessId, String isPass, String approverId,
			String examineApproveIdea, HttpServletResponse response, String approverKey){
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitTidyRiskAssessRiskForSecurity(executionId, businessId, isPass, examineApproveIdea);
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
	 * 得到判断多级、单级审批
	 * */
	@ResponseBody
	@RequestMapping(value = "/access/approval/findStrApproval.f")
	public Map<String, Object> findStrApproval(String executionId) {
		Map<String, Object> result = new HashMap<String, Object>();
		//判断多级、单级审批
		String type = o_jbpmBO.findPDDByExecutionId(executionId);
		result.put("type", type);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 风险评估多级审批列表查询
	 * add by 王再冉
	 * 2014-2-11  下午4:39:50
	 * desc : 
	 * @param query
	 * @param executionId
	 * @param assessPlanId
	 * @param loginType
	 * @param username
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/approval/findleaderapprovegrid.f")
	public ArrayList<HashMap<String, Object>> findLeaderApproveGrid(String query, String executionId, String assessPlanId,
			String loginType, String username) {
		assessPlanId = this.getAssessPlanId(null, executionId);
		assessPlanId = this.getFilterAssessPlanId(assessPlanId);
		String orgId = "";
		
		@SuppressWarnings("unchecked")
		ArrayList<RiskAssessPlanTakerObject> Objectlist = 
				(ArrayList<RiskAssessPlanTakerObject>)o_jbpmOperate.getVariableObj(executionId, "riskTaskEvaluators");
		ArrayList<String> scoreEmpIdList = new ArrayList<String>();
		for(RiskAssessPlanTakerObject taker : Objectlist){
			String empId = taker.getRiskTaskEvaluatorId();
			scoreEmpIdList.add(empId);
		}
		try {
			
			HashMap<String, Risk> orgRiskMap = o_riskOrgBO.findRiskOrgMap();
			String idSeq[] = o_sysEmpOrgBO.queryOrgByEmpid(scoreEmpIdList.get(0)).getOrgseq().replace(".", ",").split(",");
			
			for (String id : idSeq) {
				if(null != orgRiskMap.get(id)){
					orgId = id;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<String> orgList = o_riskScoreBO.findAllDeptIdsBydeptId(orgId);
		orgList.add(orgId);
		
		return o_approvalAssessBO.findAssessByDept(query, assessPlanId, orgList, scoreEmpIdList);
	}
	/**
	 * 风险评估多级审批列表
	 * add by 王再冉
	 * 2014-2-13  下午4:28:56
	 * desc : 
	 * @param query
	 * @param executionId
	 * @param assessPlanId
	 * @param loginType
	 * @param username
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/approval/findleadergridassessplan.f")
	public ArrayList<HashMap<String, Object>> findLeaderGridAssessPlan(String query,String executionId, String assessPlanId,
			String loginType, String username) {
		assessPlanId = this.getAssessPlanId(null, executionId);
		assessPlanId = this.getFilterAssessPlanId(assessPlanId);
		String companyId = UserContext.getUser().getCompanyid();
		
		return o_approvalAssessBO.findLeaderGridAssessPlan(query, assessPlanId, companyId);
	}
	/**
	 * 得到业务ID
	 * */
	private String getAssessPlanId(String assessPlanId, String executionId){
		if(null == assessPlanId){
			return o_jbpmOperate.getVariable(executionId,"id");
		}
		return assessPlanId;
	}
	
	/**
	 * 过滤评估计划ID
	 * */
	private String getFilterAssessPlanId(String assessPlanId){
		if(assessPlanId.indexOf(",") != -1){
			assessPlanId = assessPlanId.split(",")[0];
		}
		
		return assessPlanId;
	}
}