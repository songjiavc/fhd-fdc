package com.fhd.ra.business.assess.email;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.templatemanage.TemplateManage;
import com.fhd.ra.business.assess.kpiset.AssessTaskBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.oper.SysEmployeeBO;
import com.fhd.sm.business.ChartForEmailBO;
import com.fhd.sys.business.templatemanage.TemplateManageBO;

@Service
public class QuaAssessEmailBO {

	@Autowired
	private ChartForEmailBO o_chartForEmailBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	@Autowired
	private SysEmployeeBO o_sysEmployeeBO;
	
	@Autowired
	private AssessTaskBO o_assessTaskBO;
	
	@Autowired
	private AddRessUtilBO o_addRessUtilBO;
	
	@Autowired
	private TemplateManageBO o_templateManageBO;
	
	/**
	 * 发送评估任务审批邮件提醒
	 * @param businessId
	 * @param empId
	 */
	public void sendQuaAssessEmail(String assessPlanId, HashMap<String, Long> empIdTaskIdMap){
		TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_assessplan_all");
	   	if(null!=temp){
	   		RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
	   		String title = plan.getPlanName()+"　评估任务审批邮件";
	   		Map<String, SysEmployee> sysEmployeeAllMap = o_sysEmployeeBO.findSysEmployeeMapAll();
			Iterator<Entry<String, Long>> iter = empIdTaskIdMap.entrySet().iterator();
			
			while (iter.hasNext()) {
				String content = temp.getContent();//获得邮件模板
				Entry<String, Long> key = iter.next();
				Object value = empIdTaskIdMap.get(key.getKey());
				String empId = key.getKey().toString();
				String taskId = value.toString();
				String loginType = "email";
				String userName = sysEmployeeAllMap.get(empId).getUsername();
				String scoreEmpId = empId;// 员工ID
				String basePath = o_addRessUtilBO.getAddRessEmil(assessPlanId, taskId, "FHD.view.risk.assess.AssessApproveSubmit", loginType, userName);
	   			String assessButton = "<a href=\"" + basePath + "\">立即审批<a/>";
	   			SysEmployee emp = sysEmployeeAllMap.get(empId);
	   			String[] emails = {emp.getOemail()};//邮箱地址
	   			//得到未评估标红的风险内容
	   			StringBuffer riskConten = new StringBuffer();
				List<String> objIds = o_assessTaskBO.findUserDeptScoreObjIdsByplanIdAndempId(scoreEmpId, assessPlanId);
				List<RiskScoreObject> scoreObjectList = o_scoreObjectBO.findRiskScoreObjsByobjIdList(objIds);
				
		   		for (RiskScoreObject riskScoreObject : scoreObjectList) {
		   			riskConten =riskConten.append("<tr><td>" + riskScoreObject.getRisk().getName() + "</td></tr>");
				}
		   		
				String riskContenStr = HtmlUtil.getStyle() + HtmlUtil.getStartTable() + HtmlUtil.getTh() + riskConten.toString() + HtmlUtil.getEndTable();
				content = StringUtils.replace(content, "$user$", emp.getEmpname());
				content = StringUtils.replace(content, "$riskCounts$", scoreObjectList.size() + "");
				content = StringUtils.replace(content, "$riskConten$", riskContenStr);
				content = StringUtils.replace(content, "$assesPlanName$", plan.getPlanName());
				content = StringUtils.replace(content, "$assessButton$", assessButton);
				o_chartForEmailBO.sendEmail(emails, null, null, null, title, content, null);
			}
		}
	}
}
