package com.fhd.ra.web.controller.assess.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.risk.Template;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.risk.TemplateBO;

@Controller
public class AssessValidationController {

	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	@Autowired
	private TemplateBO o_templateBO;
	
	/**
	 * 过滤评估计划ID
	 * */
	private String getFilterAssessPlanId(String assessPlanId){
		if(assessPlanId.indexOf(",") != -1){
			assessPlanId = assessPlanId.split(",")[0];
		}
		
		return assessPlanId;
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
	 * 严正评估计划中模板是否存在
	 * */
	@ResponseBody
	@RequestMapping(value = "toAssessPlanValidationTemplate.f")
	public Map<String, Object> toAssessPlanValidationTemplate(String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId);
		assessPlanId = this.getFilterAssessPlanId(assessPlanId);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		RiskAssessPlan riskAssessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
		HashMap<String, Template> templateMapAll = o_templateBO.findTemplateAllMap();
		ArrayList<String> notTemplate = new ArrayList<String>();
		String message = "";
		
		try {
			if(null == riskAssessPlan.getTemplate()){
				//自定义模板
				ArrayList<String> riskScoreObjByplanIdList = o_scoreObjectBO.findRisksNameAndTemplateIdByPlanId(assessPlanId);
				for (String str : riskScoreObjByplanIdList) {
					String s[] = str.split("--");
					String riskName = s[1];
					String templateId = s[2];
					if(!templateId.equalsIgnoreCase("not")){
						if(null == templateMapAll.get(templateId)){
							//该风险没有模板
							notTemplate.add(riskName + "<font color='red'>指定模板已不存在.</font></br>");
						}
					}
				}
			}else{
				if(riskAssessPlan.getTemplate().getId() != null){
					returnMap.put("success", true);
				}
			}
			
			if(notTemplate.size() == 0){
				returnMap.put("success", true);
			}else{
				for (String str : notTemplate) {
					message += str;
				}
				returnMap.put("success", false);
				returnMap.put("message", message + "建议,系统管理->运行监控->工作流跟踪,删除该流程重新建立已有模板计划.");
			}
		} catch (Exception e) {
			returnMap.put("success", false);
			returnMap.put("message", message);
			returnMap.put("planName", riskAssessPlan.getPlanName());
		}
		
		return returnMap;
	}
}
