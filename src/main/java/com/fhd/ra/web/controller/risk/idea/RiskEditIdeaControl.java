package com.fhd.ra.web.controller.risk.idea;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.Identities;
import com.fhd.entity.risk.EmpRelaRiskIdea;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskIdea;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.util.TimeUtil;
import com.fhd.ra.business.risk.EmpRelaRiskIdeaBO;
import com.fhd.ra.business.risk.RiskDataEditFlowBO;
import com.fhd.ra.business.risk.RiskIdeaBO;

@Controller
public class RiskEditIdeaControl {

	@Autowired
	private EmpRelaRiskIdeaBO o_empRelaRiskIdeaBO;
	
	@Autowired
	private RiskIdeaBO o_riskIdeaBO;
	
	@Autowired
	private RiskDataEditFlowBO o_RiskDataEditFlowBO;
	
	/**
	 * 通过
	 * */
	@ResponseBody
	@RequestMapping(value = "riskTowWork.f")
	public void riskWork(String executionId, String isPass, String examineApproveIdea, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_RiskDataEditFlowBO.approveRisk(executionId, isPass, examineApproveIdea);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		}finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 结束工作流
	 * */
	@ResponseBody
	@RequestMapping(value = "endWork.f")
	public void endWork(String executionId, String empRelaRiskIdeaIdDatas, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_RiskDataEditFlowBO.archiveRisk(executionId, empRelaRiskIdeaIdDatas);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		}finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 领导审批
	 * */
	@ResponseBody
	@RequestMapping(value = "findRiskEmpIdeaList.f")
	public ArrayList<HashMap<String, Object>> findRiskEmpIdeaList(String query, String orgId) {
		orgId = orgId.replace("Work", "");
		return o_riskIdeaBO.findRiskEmpIdeaList(TimeUtil.getCurrentTimeStrHMS(), query, orgId);
	}
	
	/**
	 * 查询修改意见
	 * */
	@ResponseBody
	@RequestMapping(value = "findEditIdea.f")
	public Map<String, Object> findEditIdea(String riskId) {
		String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
		Map<String, Object> result = new HashMap<String, Object>();
		String empRelaRiskIdeaId = "";
		String editIdeaContent = "";
		ArrayList<HashMap<String, Object>> arrayList = o_empRelaRiskIdeaBO.findDeptLeadIdea(riskId, scoreEmpId);
		if(null != arrayList){
			empRelaRiskIdeaId = arrayList.get(0).get("empRelaRiskIdeaId").toString();
			editIdeaContent = arrayList.get(0).get("editIdeaContent").toString();
		}
		
		result.put("empRelaRiskIdeaId", empRelaRiskIdeaId);//综合ID
		result.put("editIdeaContent", editIdeaContent);//意见内容
		result.put("success", true);

		return result;
	}
	
	/**
	 * 删除修改意见
	 * */
	@ResponseBody
	@RequestMapping(value = "delIdea.f")
	public Map<String, Object> delIdea(String empRelaRiskIdeaId, String riskIdeaId) {
		Map<String, Object> result = new HashMap<String, Object>();
		EmpRelaRiskIdea empRelaRiskIdea = new EmpRelaRiskIdea();
		empRelaRiskIdea.setId(empRelaRiskIdeaId);
		RiskIdea riskIdea = new RiskIdea();
		riskIdea.setId(riskIdeaId);
		
		o_empRelaRiskIdeaBO.delEmpRelaRiskIdea(empRelaRiskIdea);
		o_riskIdeaBO.delRiskIdea(riskIdea);
		result.put("success", true);

		return result;
	}
	
	/**
	 * 保存修改意见
	 * */
	@ResponseBody
	@RequestMapping(value = "saveRiskEditIdea.f")
	public void saveRiskEditIdea(String empRelaRiskIdeaId, String editIdeaContent, String riskId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
			String deptId = UserContext.getUser().getMajorDeptId();//登录用户所在部门
			out = response.getWriter();
			if(StringUtils.isNotBlank(editIdeaContent)){
				EmpRelaRiskIdea empRelaRiskIdea = null;
				RiskIdea riskIdea = null;
				if(StringUtils.isBlank(empRelaRiskIdeaId)){
					empRelaRiskIdea = new EmpRelaRiskIdea();
					empRelaRiskIdea.setId(Identities.uuid());
					riskIdea = new RiskIdea();
					riskIdea.setId(Identities.uuid());
				}else{
					riskIdea = o_riskIdeaBO.findRiskIdeaByEmpRelaRiskIdeaId(empRelaRiskIdeaId);
					empRelaRiskIdea = riskIdea.getEmpRelaRiskIdea();
				}
				
			    Risk risk = new Risk();
			    risk.setId(riskId);
			    empRelaRiskIdea.setRisk(risk);
			    SysEmployee sysEmployee = new SysEmployee();
			    sysEmployee.setId(scoreEmpId);
			    empRelaRiskIdea.setEmp(sysEmployee);
			    SysOrganization org = new SysOrganization();
			    org.setId(deptId);
			    empRelaRiskIdea.setOrg(org);
			    o_empRelaRiskIdeaBO.mergeEmpRelaRiskIdea(empRelaRiskIdea);
			    
			    riskIdea.setContent(editIdeaContent);
			    riskIdea.setCreateTime(TimeUtil.getCurrentTime());
			    riskIdea.setEmpRelaRiskIdea(empRelaRiskIdea);
			    o_riskIdeaBO.mergeRiskIdea(riskIdea);
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
}