
package com.fhd.ra.web.controller.response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.response.ResponseSolutionBO;
import com.fhd.ra.web.form.assess.formulateplan.RiskAssessPlanForm;
import com.fhd.ra.web.form.response.solution.SolutionForm;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;


/**
 * 应对措施
 */
@Controller
@SuppressWarnings("deprecation")
public class ResponseSolutionControl{
	@Autowired
	private ResponseSolutionBO o_responseSolutionBO;
	@Autowired
	private OrgGridBO o_OrgGridBO;
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	@Autowired
	private EmpGridBO o_empGridBO;
	
	/**
	 * add by 宋佳
	 * 保存应对措施的内容
	 * @throws UnsupportedEncodingException 
	 * 
	 * */
	@ResponseBody
	@RequestMapping("/response/saveresponsesolution.f")
	public Map<String,Object> saveSolution(SolutionForm solutionForm) throws UnsupportedEncodingException{
		Map<String,Object> resultMap = o_responseSolutionBO.saveSolutionForm(solutionForm);
		return resultMap;
	}
	/**
	 * 加载应对措施的内容
	 * add by 宋佳 
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping("/response/loadsolutionformbysolutionid.f")
	public Map<String,Object> loadSolutionFormBySolutionId(String solutionId) throws IOException{
		return o_responseSolutionBO.getSolutionFormBySolutionId(solutionId);
	}
	/**
	 * 加载只读窗体
	 * add by 宋佳 
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping("/response/loadsolutionformbysolutionidforview.f")
	public Map<String,Object> loadSolutionFormBySolutionIdForView(String solutionId) throws IOException{
		return o_responseSolutionBO.getSolutionFormBySolutionIdForView(solutionId);
	}
	
	/**
	 * 保存风险应对
	 * add by 王再冉
	 * 2014-1-13  上午9:49:08
	 * desc : 
	 * @param planForm
	 * @param id
	 * @param executionId
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/response/saveresponsplan.f")
	public Map<String, Object> saveResponsePlan(RiskAssessPlanForm planForm,String id,String executionId,String schm) throws Exception{
		String companyId = UserContext.getUser().getCompanyid();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = new RiskAssessPlan();
		assessPlan.setId(Identities.uuid());
		assessPlan.setSchm(schm);
		assessPlan.setPlanName(planForm.getPlanName());
		assessPlan.setPlanCode(planForm.getPlanCode());
		assessPlan.setCompany(o_OrgGridBO.findOrganizationByOrgId(companyId));//保存当前机构
		assessPlan.setWorkType(planForm.getWorkType());//工作类型
		assessPlan.setRangeReq(planForm.getRangeReq());//范围要求
		assessPlan.setCollectRate(planForm.getCollectRate());//采集频率
		assessPlan.setPlanType("riskResponse");//计划类型
		assessPlan.setWorkTage(planForm.getWorkTage());//工作目标
		assessPlan.setPlanCreatTime(new Date());//创建时间
		if(null != o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId())){
			Template temp = o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId());
			if(null != temp){
				assessPlan.setTemplate(temp);//评估模板
				assessPlan.setTemplateType(temp.getType().getId());//模板类型
			}
		}else{
			assessPlan.setTemplate(null);
			assessPlan.setTemplateType(null);
		}
		if(StringUtils.isNotBlank(planForm.getBeginDataStr())){//开始时间
			assessPlan.setBeginDate(DateUtils.parseDate(planForm.getBeginDataStr(), "yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(planForm.getEndDataStr())){//开始时间
			assessPlan.setEndDate(DateUtils.parseDate(planForm.getEndDataStr(), "yyyy-MM-dd"));
		}
		
		if(null != planForm.getContactName()){
			String conEmpId = "";
			JSONArray conArray = JSONArray.fromObject(planForm.getContactName());
			if(null != conArray && conArray.size()>0){
				conEmpId = (String)JSONObject.fromObject(conArray.get(0)).get("id");
			}
			SysEmployee conEmp = o_empGridBO.findEmpEntryByEmpId(conEmpId);
			if(null != conEmp){
				assessPlan.setContactPerson(conEmp);//保存联系人
			}
		}
		if(null != planForm.getResponsName()){
			String respEmpId = "";
			JSONArray respArray = JSONArray.fromObject(planForm.getResponsName());
			if(null != respArray && respArray.size()>0){
				respEmpId = (String)JSONObject.fromObject(respArray.get(0)).get("id");
			}
			SysEmployee respEmp = o_empGridBO.findEmpEntryByEmpId(respEmpId);
			if(null != respEmp){
				assessPlan.setResponsPerson(respEmp);//保存负责人
			}
		}
		assessPlan.setDealStatus(Contents.DEAL_STATUS_NOTSTART);//点保存按钮，处理状态为"未开始"
		assessPlan.setStatus(Contents.STATUS_SAVED);//保存：状态为"已保存"
		if(StringUtils.isNotBlank(id)){//修改
			String ids[] = id.split(",");
			assessPlan.setId(ids[0]);
			RiskAssessPlan plan = o_riskAssessPlanDAO.get(assessPlan.getId());
			if(null==assessPlan.getTemplateType()){
				if(null!=plan.getTemplate()){
					assessPlan.setTemplate(plan.getTemplate());//评估模板
					assessPlan.setTemplateType(plan.getTemplateType());//模板类型
				}else{
					assessPlan.setTemplate(null);
					assessPlan.setTemplateType(null);
				}
			}
			if(StringUtils.isNotBlank(executionId)){
				assessPlan.setDealStatus(plan.getDealStatus());//点保存按钮，处理状态为"未开始"
				assessPlan.setStatus(plan.getStatus());//保存：状态为"已保存"
			}
			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
		}else{
			o_riskAssessPlanBO.saveRiskAssessPlan(assessPlan);//保存
		}
		inmap.put("planId", assessPlan.getId());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
}
