package com.fhd.ra.business.assess.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.templatemanage.TemplateManage;
import com.fhd.ra.business.assess.approval.ApprovalAssessBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.SysEmployeeBO;
import com.fhd.sm.business.ChartForEmailBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.templatemanage.TemplateManageBO;

@Service
public class AssessEmailBO {

	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
	
	@Autowired
	private SysEmployeeBO o_sysEmployeeBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private ChartForEmailBO o_chartForEmailBO;
	
	@Autowired
	private AddRessUtilBO o_addRessUtilBO;
	
	@Autowired
	private TemplateManageBO o_templateManageBO;
	
	@Autowired
	private EmpGridBO o_empGridBO;
	
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	
	@Autowired
	private ApprovalAssessBO o_approvalAssessBO;
	
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	/**
	 * 评估计划审批提醒email
	 * @param assessPlanId		计划id
	 * @param empIdTaskIdMap	人员taskId对应map
	 * @param request
	 */
	public void sendAssessEmail(String assessPlanId, String empId, String taskId, String jsUrl, String nodeTitle){
		TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_assessplan_all");
	   	if(null!=temp){
	   		RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
	   		String title = plan.getPlanName() + nodeTitle;
			SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empId);
			String content = temp.getContent();//获得邮件模板
			String loginType = "email";
			String userName = emp.getUsername();// 员工ID
			
			String basePath = o_addRessUtilBO.getAddRessEmil(assessPlanId, taskId, jsUrl, loginType, userName);
   			String assessButton = "<a href=\"" + basePath + "\">立即审批<a/>";
   			String[] emails = {emp.getOemail()};//邮箱地址
   			//得到未评估标红的风险内容
			StringBuffer riskConten = new StringBuffer();
			Map<String,Object> rtnMap = o_scoreDeptBO.findScoreDeptsByPlanId(assessPlanId);//该计划的所有打分对象
			List<Map<String,Object>> scoreDeptmap = (List<Map<String, Object>>) rtnMap.get("scoreDept");
			for(Map<String,Object> map : scoreDeptmap){
				RiskScoreDept scoreDept = (RiskScoreDept)map.get("scoreDept");
				riskConten = riskConten.append("<tr><td>" + scoreDept.getOrganization().getOrgname() + "</td><td>" 
							+ scoreDept.getScoreObject().getRisk().getName() + "</td></tr>");
			}
	   		
			String riskContenStr = HtmlUtil.getStyle() + HtmlUtil.getStartTable() + HtmlUtil.getTwoTh() + riskConten.toString() + HtmlUtil.getEndTable();
			content = StringUtils.replace(content, "$user$", emp.getEmpname());
			content = StringUtils.replace(content, "$riskCounts$", scoreDeptmap.size() + "");
			content = StringUtils.replace(content, "$riskConten$", riskContenStr);
			content = StringUtils.replace(content, "$assesPlanName$", plan.getPlanName());
			content = StringUtils.replace(content, "$assessButton$", assessButton);
			o_chartForEmailBO.sendEmail(emails, null, null, null, title, content, null);
		}
	}
	/**
	 * 多级审批邮件发送(风险内容按评估人过滤)
	 * add by 王再冉
	 * 2014-3-10  下午6:08:12
	 * desc : 
	 * @param assessPlanId	计划id
	 * @param empId			审批人id
	 * @param taskId		工作流任务id
	 * @param jsUrl			js路径
	 * @param nodeTitle 	邮件标题
	 * void
	 */
	@SuppressWarnings("unchecked")
	public void sendAssessEmailByEmpIdsAndSome(String assessPlanId, String empId, String taskId,
												String jsUrl, String nodeTitle,String executionId){
		TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_assessplan_all");
		ArrayList<RiskAssessPlanTakerObject> Objectlist = 
				(ArrayList<RiskAssessPlanTakerObject>)o_jbpmOperate.getVariableObj(executionId, "riskTaskEvaluators");
		ArrayList<String> scoreEmpIdList = new ArrayList<String>();
		for(RiskAssessPlanTakerObject taker : Objectlist){
			String eId = taker.getRiskTaskEvaluatorId();
			scoreEmpIdList.add(eId);
		}
	   	if(null!=temp){
	   		RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
	   		String title = plan.getPlanName() + nodeTitle;
			SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empId);
			String content = temp.getContent();//获得邮件模板
			String loginType = "email";
			String userName = emp.getUsername();// 员工ID
			
			String basePath = o_addRessUtilBO.getAddRessEmil(assessPlanId, taskId, jsUrl, loginType, userName);
   			String assessButton = "<a href=\"" + basePath + "\">立即审批<a/>";
   			String[] emails = {emp.getOemail()};//邮箱地址
   			StringBuffer riskConten = new StringBuffer();//风险内容
			
			ArrayList<HashMap<String, Object>> riskMaps = o_approvalAssessBO.findIdentifyApproveGridByPlanIdAndEmpIds(assessPlanId,scoreEmpIdList);
			for(Map<String,Object> map : riskMaps){
				riskConten = riskConten.append("<tr><td>" + map.get("orgName")+ "</td><td>" 
							+ map.get("riskName") + "</td></tr>");
			}
	   		
			String riskContenStr = HtmlUtil.getStyle() + HtmlUtil.getStartTable() + HtmlUtil.getTwoTh() + riskConten.toString() + HtmlUtil.getEndTable();
			content = StringUtils.replace(content, "$user$", emp.getEmpname());
			content = StringUtils.replace(content, "$riskCounts$", riskMaps.size() + "");
			content = StringUtils.replace(content, "$riskConten$", riskContenStr);
			content = StringUtils.replace(content, "$assesPlanName$", plan.getPlanName());
			content = StringUtils.replace(content, "$assessButton$", assessButton);
			o_chartForEmailBO.sendEmail(emails, null, null, null, title, content, null);
		}
	}
	
	
	
	/**
	 * 发送风险评估邮件提醒
	 * @param businessId
	 * @param empId
	 */
	public void sendAssessEmail(String assessPlanId, HashMap<String, Long> empIdTaskIdMap){
		TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_assessplan_all");
	   	if(null!=temp){
	   		RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
	   		String title = plan.getPlanName()+"　风险评估邮件";
	   		Map<String, SysEmployee> sysEmployeeAllMap = o_sysEmployeeBO.findSysEmployeeMapAll();
	   		HashMap<String, HashMap<String, Boolean>> scoreRiskAllMap = o_rangObjectDeptEmpBO.findOrangObjectDeptEmpByAssessPlanId(assessPlanId);
			HashMap<String, HashMap<String, Boolean>> notScoreRiskAllMap = o_rangObjectDeptEmpBO.findNotOrangObjectDeptEmpByAssessPlanId(assessPlanId);
			Iterator<Entry<String, Long>> iter = empIdTaskIdMap.entrySet().iterator();
			while (iter.hasNext()) {
				String content = temp.getContent();//获得邮件模板
				Entry<String, Long> key1 = iter.next();
				Object value = empIdTaskIdMap.get(key1.getKey());
				String empId = key1.getKey().toString();
				String taskId = value.toString();
				String loginType = "email";
				String userName = sysEmployeeAllMap.get(empId).getUsername();// 员工ID
	   			
	   			String basePath = o_addRessUtilBO.getAddRessEmil(assessPlanId, taskId, "FHD.view.risk.assess.quaAssess.QuaAssessMan", loginType, userName);
	   			String assessButton = "<a href=\"" + basePath + "\">立即评价<a/>";
	   			SysEmployee emp = sysEmployeeAllMap.get(empId);
	   			String[] emails = {emp.getOemail()};//邮箱地址
	   			HashMap<String, Boolean> riskAllMap = scoreRiskAllMap.get(empId);
	   			HashMap<String, Boolean> tempMap = notScoreRiskAllMap.get(empId);
	   			
	   			if(null != tempMap){
	   				Set<Entry<String, Boolean>> key = tempMap.entrySet();
					for (Iterator<Entry<String, Boolean>> it = key.iterator(); it.hasNext();) {
						Entry<String, Boolean> keys = it.next();
						riskAllMap.put(keys.getKey(), tempMap.get(keys.getKey()));
					}
					
					//得到未评估标红的风险内容
					StringBuffer riskConten = new StringBuffer();
					Set<Entry<String, Boolean>> key2 = riskAllMap.entrySet();
					for (Iterator<Entry<String, Boolean>> it2 = key2.iterator(); it2.hasNext();) {
						String keys2 = it2.next().getKey();
						boolean isBool = riskAllMap.get(keys2);
						if(!isBool){
							riskConten.append("<tr><td><font color='red'>" + keys2 + "</font></td></tr>");
						}else{
							riskConten.append("<tr><td>" + keys2 + "</font></td></tr>");
						}
					}
					
					String riskContenStr = HtmlUtil.getStyle() + HtmlUtil.getStartTable() + HtmlUtil.getTh() + riskConten.toString() + HtmlUtil.getEndTable();
					
					content = StringUtils.replace(content, "$user$", emp.getEmpname());
					content = StringUtils.replace(content, "$riskCounts$", riskAllMap.size() + "");
					content = StringUtils.replace(content, "$riskConten$", riskContenStr);
					content = StringUtils.replace(content, "$assesPlanName$", plan.getPlanName());
					content = StringUtils.replace(content, "$assessButton$", assessButton);
					o_chartForEmailBO.sendEmail(emails, null, null, null, title, content, null);
	   			}
			}
	   	}
	}
}