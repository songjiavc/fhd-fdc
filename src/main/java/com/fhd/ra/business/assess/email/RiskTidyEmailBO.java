package com.fhd.ra.business.assess.email;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.templatemanage.TemplateManage;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.sm.business.ChartForEmailBO;
import com.fhd.sys.business.assess.SendEmailBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.templatemanage.TemplateManageBO;

@Service
public class RiskTidyEmailBO {

	@Autowired
	private ChartForEmailBO o_chartForEmailBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private AddRessUtilBO o_addRessUtilBO;
	@Autowired
	private ShowRiskTidyBO o_showRiskTidyBO;
	@Autowired
	private SendEmailBO o_sendEmailBO;
	@Autowired
	private TemplateManageBO o_templateManageBO;
	
	/**
	 * 评估结果整理提醒email
	 * @param assessPlanId		计划id
	 * @param empIdTaskIdMap
	 * @param request
	 */
	public void sendRiskTidyEmail(String assessPlanId, String empId, String taskId){
		if(o_sendEmailBO.findIsSendValue("send_email_assess_riskplan_all")){//查询计划审批节点是否发送email
			String dbid = o_showRiskTidyBO.findjbpm4Task(taskId);
			if(!"".equalsIgnoreCase(dbid)){
				TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_assessplan_all");
			   	if(null!=temp){
			   		RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
			   		String title = plan.getPlanName()+"　评估结果整理邮件";
					SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empId);
					String content = temp.getContent();//获得邮件模板
		   			String loginType = "email";
		   			String userName = emp.getUsername();// 员工ID
		   			
		   			String basePath = o_addRessUtilBO.getAddRessEmil(assessPlanId, taskId, 
		   					"FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyMan", loginType, userName);
		   			String assessButton = "<a href=\"" + basePath + "\">立即整理<a/>";
		   			String[] emails = {emp.getOemail()};//邮箱地址
		   			//得到未评估标红的风险内容
		   			StringBuffer riskConten = new StringBuffer();
		   			Map<String,Object> rtnMap = o_scoreDeptBO.findScoreDeptsByPlanId(assessPlanId);//该计划的所有打分对象
		   			List<Map<String,Object>> scoreDeptmap = (List<Map<String, Object>>) rtnMap.get("scoreDept");
					for(Map<String,Object> map : scoreDeptmap){
						RiskScoreDept scoreDept = (RiskScoreDept)map.get("scoreDept");
						riskConten = riskConten.append("<tr><td>" + scoreDept.getScoreObject().getRisk().getName() + "</td></tr>");
					}
			   		
					String riskContenStr = HtmlUtil.getStyle() + HtmlUtil.getStartTable() + HtmlUtil.getTh() + riskConten.toString() + HtmlUtil.getEndTable();
					content = StringUtils.replace(content, "$user$", emp.getEmpname());
					content = StringUtils.replace(content, "$riskCounts$", scoreDeptmap.size() + "");
					content = StringUtils.replace(content, "$riskConten$", riskContenStr);
					content = StringUtils.replace(content, "$assesPlanName$", plan.getPlanName());
					content = StringUtils.replace(content, "$assessButton$", assessButton);
					o_chartForEmailBO.sendEmail(emails, null, null, null, title, content, null);
				}
			}
		}
	}
}
