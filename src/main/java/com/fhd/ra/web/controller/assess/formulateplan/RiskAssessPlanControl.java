package com.fhd.ra.web.controller.assess.formulateplan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskCircusee;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.email.AssessEmailBO;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.oper.CircuseeBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.quaassess.ShowAssessBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.ra.web.form.assess.formulateplan.RiskAssessPlanForm;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;

/**
 * 计划制定控制类
 * @author 王再冉
 *
 */
@Controller
public class RiskAssessPlanControl {
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private OrgGridBO o_OrgGridBO ;
	@Autowired
	private EmpGridBO o_empBO ;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private ShowAssessBO o_showAssessBO;
	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired 
	private CircuseeBO o_circuseeBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired
	private AssessEmailBO o_assessEmailBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private TemplateBO o_templateBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
    private OrgGridBO o_orgGridBO;

	/**
	 * 根据计划类型和状态查询计划列表（分页）
	 * add by 王再冉
	 * 2014-3-7  上午9:35:00
	 * desc : 
	 * @param start		分页开始
	 * @param limit		限制页数
	 * @param query		查询关键字
	 * @param planType	计划类型
	 * @param status	计划状态
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/queryAccessPlansPage.f")
	public Map<String, Object> queryAccessPlansPage(int start, int limit, String query, String planType, String status,String schm){
		Page<RiskAssessPlan> page = new Page<RiskAssessPlan>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		String companyId = UserContext.getUser().getCompanyid();//当前登录者的公司id
		page = o_riskAssessPlanBO.findPlansPageBySome(query, page, companyId, planType,status,schm);
		
		List<RiskAssessPlan> entityList = page.getResult();
		List<RiskAssessPlan> datas = new ArrayList<RiskAssessPlan>();
		for(RiskAssessPlan de : entityList){
			if(StringUtils.isNotBlank(query)){
				String conPersonName = "";	//联系人姓名
				String resPersonName = "";	//负责人姓名
				String beginDateStr = "";	//开始时间
				String endDateStr = "";		//结束时间
				if(null != de.getContactPerson()){
					conPersonName = de.getContactPerson().getEmpname();
				}
				if(null!= de.getResponsPerson()){
					resPersonName = de.getResponsPerson().getEmpname();
				}
				if(null != de.getBeginDate()){
					beginDateStr = de.getBeginDate().toString();
				}
				if(null != de.getEndDate()){
					endDateStr = de.getEndDate().toString();
				}
				String dealSta = de.getDealStatus();//处理状态
				if("N".equals(dealSta)){
					dealSta = "未开始";
				}else if("H".equals(dealSta)){
					dealSta = "处理中";
				}else if("F".equals(dealSta)){
					dealSta = "已完成";
				}
				if(de.getPlanName().contains(query)||conPersonName.contains(query)||
						resPersonName.contains(query)||beginDateStr.contains(query)||
						endDateStr.contains(query)||dealSta.contains(query)){
					datas.add(new RiskAssessPlanForm(de));
				}
			}else{
				datas.add(new RiskAssessPlanForm(de));
			}
			
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	/**
	 * 保存计划任务
	 * @param planForm	计划表单实体
	 * @param id		计划id
	 * @param executionId 工作流id
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/saveriskplan.f")
	public Map<String, Object> saveRiskPlan(RiskAssessPlanForm planForm,String id,String executionId,String schm) throws Exception{
		String companyId = UserContext.getUser().getCompanyid();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = new RiskAssessPlan();
		assessPlan.setId(Identities.uuid());
		assessPlan.setPlanName(planForm.getPlanName());//计划名称
		assessPlan.setPlanCode(planForm.getPlanCode());//计划编号
		assessPlan.setCompany(o_OrgGridBO.findOrganizationByOrgId(companyId));//保存当前机构
		assessPlan.setWorkType(planForm.getWorkType());//工作类型
		assessPlan.setRangeReq(planForm.getRangeReq());//范围要求
		assessPlan.setCollectRate(planForm.getCollectRate());//采集频率
		assessPlan.setPlanType("riskAssess");//计划类型（风险评估） 2017年5月2日16:23:58吉志强去掉
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
		if(StringUtils.isNotBlank(planForm.getEndDataStr())){//结束时间
			assessPlan.setEndDate(DateUtils.parseDate(planForm.getEndDataStr(), "yyyy-MM-dd"));
		}
		
		if(null != planForm.getContactName()){//联系人
			String conEmpId = "";
			JSONArray conArray = JSONArray.fromObject(planForm.getContactName());
			if(null != conArray && conArray.size()>0){
				conEmpId = (String)JSONObject.fromObject(conArray.get(0)).get("id");
			}
			SysEmployee conEmp = o_empBO.findEmpEntryByEmpId(conEmpId);
			if(null != conEmp){
				assessPlan.setContactPerson(conEmp);//保存联系人
			}
		}
		if(null != planForm.getResponsName()){//负责人
			String respEmpId = "";
			JSONArray respArray = JSONArray.fromObject(planForm.getResponsName());
			if(null != respArray && respArray.size()>0){
				respEmpId = (String)JSONObject.fromObject(respArray.get(0)).get("id");
			}
			SysEmployee respEmp = o_empBO.findEmpEntryByEmpId(respEmpId);
			if(null != respEmp){
				assessPlan.setResponsPerson(respEmp);//保存负责人
			}
		}
		assessPlan.setDealStatus(Contents.DEAL_STATUS_NOTSTART);//点保存按钮，处理状态为"未开始"
		assessPlan.setStatus(Contents.STATUS_SAVED);//保存：状态为"已保存"
		if(StringUtils.isNotBlank(id)){//修改
			String ids[] = id.split(",");
			assessPlan.setId(ids[0]);
			RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(assessPlan.getId());
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
			assessPlan.setSchm(plan.getSchm());
			
			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
		}else{
			assessPlan.setSchm(schm);
			if("dept".equals(schm)){
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				assessPlan.setCreateOrg(deptId);
			}
			o_riskAssessPlanBO.saveRiskAssessPlan(assessPlan);//保存
		}
		inmap.put("planId", assessPlan.getId());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	
	/**
	 * 部门风险计划在计划审批中被退回再提交时点击下一步保存计划  吉志强 2017年5月2日16:52:54
	 * @param planForm
	 * @param id
	 * @param executionId
	 * @param schm
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/saveriskplanNew.f")
	public Map<String, Object> saveRiskPlanNew(RiskAssessPlanForm planForm,String id,String executionId,String schm) throws Exception{
		String companyId = UserContext.getUser().getCompanyid();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = new RiskAssessPlan();
		assessPlan.setId(Identities.uuid());
		assessPlan.setPlanName(planForm.getPlanName());//计划名称
		assessPlan.setPlanCode(planForm.getPlanCode());//计划编号
		assessPlan.setCompany(o_OrgGridBO.findOrganizationByOrgId(companyId));//保存当前机构
		assessPlan.setWorkType(planForm.getWorkType());//工作类型
		assessPlan.setRangeReq(planForm.getRangeReq());//范围要求
		assessPlan.setCollectRate(planForm.getCollectRate());//采集频率
		assessPlan.setPlanType("riskAssessDept");//计划类型（风险评估） 2017年5月2日16:23:58吉志强去掉
		assessPlan.setWorkTage(planForm.getWorkTage());//工作目标
		assessPlan.setPlanCreatTime(new Date());//创建时间
		if("dept".equals(schm)){
			SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
			String seq = org.getOrgseq();
			String deptId = seq.split("\\.")[2];//部门编号
			
			assessPlan.setCreateOrg(deptId);
		}
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
		if(StringUtils.isNotBlank(planForm.getEndDataStr())){//结束时间
			assessPlan.setEndDate(DateUtils.parseDate(planForm.getEndDataStr(), "yyyy-MM-dd"));
		}
		
		if(null != planForm.getContactName()){//联系人
			String conEmpId = "";
			JSONArray conArray = JSONArray.fromObject(planForm.getContactName());
			if(null != conArray && conArray.size()>0){
				conEmpId = (String)JSONObject.fromObject(conArray.get(0)).get("id");
			}
			SysEmployee conEmp = o_empBO.findEmpEntryByEmpId(conEmpId);
			if(null != conEmp){
				assessPlan.setContactPerson(conEmp);//保存联系人
			}
		}
		if(null != planForm.getResponsName()){//负责人
			String respEmpId = "";
			JSONArray respArray = JSONArray.fromObject(planForm.getResponsName());
			if(null != respArray && respArray.size()>0){
				respEmpId = (String)JSONObject.fromObject(respArray.get(0)).get("id");
			}
			SysEmployee respEmp = o_empBO.findEmpEntryByEmpId(respEmpId);
			if(null != respEmp){
				assessPlan.setResponsPerson(respEmp);//保存负责人
			}
		}
		assessPlan.setDealStatus(Contents.DEAL_STATUS_NOTSTART);//点保存按钮，处理状态为"未开始"
		assessPlan.setStatus(Contents.STATUS_SAVED);//保存：状态为"已保存"
		if(StringUtils.isNotBlank(id)){//修改
			String ids[] = id.split(",");
			assessPlan.setId(ids[0]);
			RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(assessPlan.getId());
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
			assessPlan.setSchm(plan.getSchm());
			
			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
		}else{
			assessPlan.setSchm(schm);
			if("dept".equals(schm)){
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				assessPlan.setCreateOrg(deptId);
			}
			o_riskAssessPlanBO.saveRiskAssessPlan(assessPlan);//保存
		}
		inmap.put("planId", assessPlan.getId());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	/**
	 * 根据计划id修改计划
	 * @param id	计划id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findriskassessplanById.f")
	public Map<String, Object> findRiskAssessPlanById(String id) {
		RiskAssessPlan riskPlan = new RiskAssessPlan();
		if(StringUtils.isNotBlank(id)){
			String ids[] = id.split(",");
			riskPlan.setId(ids[0]);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		RiskAssessPlan plan = o_assessPlanBO.findRiskAssessPlanById(riskPlan.getId());
		Map<String, Object> inmap = new HashMap<String, Object>();
		inmap.put("planName", plan.getPlanName());//计划名称
		inmap.put("templateType", plan.getTemplateType());//模板类型
		if(null != plan.getTemplate()){//评估模板
			inmap.put("templateId", plan.getTemplate().getId());
		}else{
			inmap.put("templateId", "dim_template_type_custom");//评估自定义模板
		}
		inmap.put("planCode", plan.getPlanCode());//计划编号
		inmap.put("rangeReq", plan.getRangeReq());//范围
		JSONArray conarray = new JSONArray();
		JSONObject conjson = new JSONObject();
		if(null != plan.getContactPerson()){//联系人
			conjson.put("id", plan.getContactPerson().getId());
			conjson.put("empno", plan.getContactPerson().getEmpcode());
			conjson.put("empname", plan.getContactPerson().getEmpname());
			conarray.add(conjson);
			inmap.put("contactName",conarray.toString() );
		}else{
			/*conjson.put("id", "");
			conarray.add(conjson);
			inmap.put("contactName",conarray.toString() );*/
			inmap.put("contactName","");
		}
		JSONArray resparray = new JSONArray();
		JSONObject respjson = new JSONObject();
		if(null != plan.getResponsPerson()){//负责人
			respjson.put("id", plan.getResponsPerson().getId());
			respjson.put("empno", plan.getResponsPerson().getEmpcode());
			respjson.put("empname", plan.getResponsPerson().getEmpname());
			resparray.add(respjson);
			inmap.put("responsName",resparray.toString() );
		}else{
			/*respjson.put("id", "");
			resparray.add(respjson);
			inmap.put("responsName",resparray.toString() );*/
			inmap.put("responsName","");
		}
		inmap.put("collectRate", plan.getCollectRate());//采集频率
		inmap.put("workType", plan.getWorkType());//工作类型
		inmap.put("workTage", plan.getWorkTage());//工作目标
		if(null != plan.getTemplate()){//模板
			inmap.put("templateName", plan.getTemplate().getName());
		}else{
			inmap.put("templateName", "自定义模板");
		}
		if(null != plan.getBeginDate()){//开始时间
			inmap.put("beginDataStr", plan.getBeginDate().toString().split(" ")[0]);
		}
		if(null != plan.getEndDate()){//结束时间
			inmap.put("endDataStr", plan.getEndDate().toString().split(" ")[0]);
		}
		if(null != plan.getContingencyType()){
            inmap.put("contingencyType", plan.getContingencyType());
        }
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	/**
	 * 根据计划id删除计划及打分对象，打分部门，综合实体
	 * add by 王再冉
	 * 2014-3-7  上午9:57:39
	 * desc : 
	 * @param ids	一个或多个计划id
	 * @return 
	 * boolean	true:成功
	 */
	 @ResponseBody
	 @RequestMapping(value = "/access/formulateplan/removeriskassessplanbyid.f")
	 public boolean removeRiskAssessPlanById(String ids) {
		  if (StringUtils.isNotBlank(ids)) {
			   o_riskAssessPlanBO.deleteAllByPlanIds(ids);
			   return true;
		  } else {
			   return false;
		  }
	 }
	
	/**
	 * 模板类型下拉列表
	 * add by 王再冉
	 * 2014-3-7  上午10:10:17
	 * desc : 
	 * @return 
	 * Map<String,Object>	type:模板类型id，name：模板类型名称
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findmodeltype.f")
	public Map<String, Object> findModelType(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("dim_template_type");//评估模板类型
		for (DictEntry dictEntry : dictEntryList) {
			inmap = new HashMap<String, Object>();
			inmap.put("type", dictEntry.getId());
			inmap.put("name",dictEntry.getName());
			list.add(inmap);
		}
		map.put("datas", list);
		return map;
	}
	/**
	 * 评估模板下拉列表（风险评估用）
	 * add by 王再冉
	 * 2014-3-7  上午10:11:59
	 * desc : 
	 * @return 
	 * Map<String,Object>	type:模板id，name：模板名称
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findtemplates.f")
	public Map<String, Object> findTemplates(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String companyId = UserContext.getUser().getCompanyid();//当前登录人公司id
		List<Template> tempEntryList = o_templateBO.findTemplateByQuery(null);//所有模板实体集合
		//List<Template> tempEntryList = o_templateBO.findTemplateByQueryAndCompanyId(null,companyId);//按公司查询，以后可能用到
		for (Template tempEntry : tempEntryList) {
			inmap = new HashMap<String, Object>();
			if("dim_template_type_sys".equals(tempEntry.getType().getId())){//系统模板
				inmap.put("type", tempEntry.getId());
				inmap.put("name",tempEntry.getName());
				list.add(inmap);
			}else{
				if(null != tempEntry.getCompany() && companyId.equals(tempEntry.getCompany().getId())){
					inmap.put("type", tempEntry.getId());
					inmap.put("name",tempEntry.getName());
					list.add(inmap);
				}
			}
		}
		inmap = new HashMap<String, Object>();//评估下拉框中包含自定义模板
		inmap.put("type", "dim_template_type_custom");
		inmap.put("name","自定义模板");
		list.add(inmap);
		map.put("datas", list);
		return map;
	}
	/**
	 * 评估模板下拉列表,为风险提供的接口（不包含自定义模板）
	 * add by 王再冉
	 * 2014-3-7  上午10:14:51
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findTemplatesrisk.f")
	public Map<String, Object> findTemplatesRisk(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String companyId = UserContext.getUser().getCompanyid();//当前登录人公司id
		List<Template> tempEntryList = o_templateBO.findTemplateByQuery(null);//所有模板实体集合
		for (Template tempEntry : tempEntryList) {
			inmap = new HashMap<String, Object>();
			if("dim_template_type_sys".equals(tempEntry.getType().getId())){//系统模板
				inmap.put("type", tempEntry.getId());
				inmap.put("name",tempEntry.getName());
				list.add(inmap);
			}else{
				if(null != tempEntry.getCompany() && companyId.equals(tempEntry.getCompany().getId())){
					inmap.put("type", tempEntry.getId());
					inmap.put("name",tempEntry.getName());
					list.add(inmap);
				}
			}
		}
		map.put("datas", list);
		return map;
	}
	/**
	 * 根据计划类型，查询工作类型下拉列表
	 * add by 王再冉
	 * 2014-3-7  上午10:16:34
	 * desc : 
	 * @param planType	计划类型
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findworktypestore.f")
	public Map<String, Object> findWorkTypeStore(String planType){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("assess_work_type");//工作类型数据字典集合
		if(null != dictEntryList){
			for (DictEntry dictEntry : dictEntryList) {
				if("responsePlan".equals(planType)){//风险应对计划
					if("assess_work_type_project".equals(dictEntry.getId())){
						inmap = new HashMap<String, Object>();
						inmap.put("type", dictEntry.getId());
						inmap.put("name",dictEntry.getName());
						list.add(inmap);
					}
				}else{//风险评估，风险辨识等其他计划
					if(!"assess_work_type_project".equals(dictEntry.getId())){
						inmap = new HashMap<String, Object>();
						inmap.put("type", dictEntry.getId());
						inmap.put("name",dictEntry.getName());
						list.add(inmap);
					}
				}
			}
		}
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 工作流--审批人查看评估计划表单
	 * @param businessId	计划id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/queryassessplanbyplanId.f")
	public Map<String,Object> findAssessPlanFormByPlanId(String businessId){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(businessId);
		inmap.put("planName", assessPlan.getPlanName());//计划名称
		String beginDateStr = "";//开始时间
		String endDateStr = "";//结束时间 
		if(null != assessPlan.getBeginDate()){
			beginDateStr = assessPlan.getBeginDate().toString().split(" ")[0];
		}
		if(null != assessPlan.getEndDate()){
			endDateStr = assessPlan.getEndDate().toString().split(" ")[0];
		}
		inmap.put("beginendDateStr", beginDateStr+" 至   "+endDateStr);//起止时间
		inmap.put("contactName",null==assessPlan.getContactPerson()?"":assessPlan.getContactPerson().getEmpname());//联系人
		inmap.put("responsName",null==assessPlan.getResponsPerson()?"":assessPlan.getResponsPerson().getEmpname());//负责人
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	/**
	 *  计划预览表单页面
	 * add by 王再冉
	 * 2014-3-7  上午10:23:30
	 * desc : 
	 * @param pid 计划id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/querypreviewpanelbyplanId.f")
	public Map<String,Object> findPreviewPanelByPlanId(String pid){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(pid);
		inmap.put("planName", assessPlan.getPlanName());//计划名称
		inmap.put("planCode", assessPlan.getPlanCode());//计划编号
		//inmap.put("workType", assessPlan.getWorkType());//工作类型
		inmap.put("collectRate", assessPlan.getCollectRate());//采集频率
		if(null != assessPlan.getTemplate()){//模板（只有风险评估计划有模板）
			inmap.put("templateName", assessPlan.getTemplate().getName());
		}
		inmap.put("rangeReq", assessPlan.getRangeReq());//范围
		if(null != assessPlan.getWorkType()){//工作类型
			DictEntry dictEntry = o_dictBO.findDictEntryById(assessPlan.getWorkType());
			if(null != dictEntry){
				inmap.put("workType", dictEntry.getName());
			}
		}
		inmap.put("workTage", assessPlan.getWorkTage());//工作目标
		String beginDateStr = "";
		String endDateStr = "";
		if(null != assessPlan.getBeginDate()){
			beginDateStr = assessPlan.getBeginDate().toString().split(" ")[0];
		}
		if(null != assessPlan.getEndDate()){
			endDateStr = assessPlan.getEndDate().toString().split(" ")[0];
		}
		inmap.put("beginendDateStr", beginDateStr+" 至  "+endDateStr);//起止日期
		inmap.put("contactName",null==assessPlan.getContactPerson()?"":assessPlan.getContactPerson().getEmpname());
		inmap.put("responsName",null==assessPlan.getResponsPerson()?"":assessPlan.getResponsPerson().getEmpname());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 已提交的计划，预览页面显示评估人和审批人列表信息
	 * @param planId	计划id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/queryriskcircuseegrid.f")
	public List<Map<String,Object>> findRiskCircuseeGrid(String planId){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		List<RiskCircusee> list = o_circuseeBO.findRiskCircuseeByplanId(planId);//查询计划的审批人承办人信息
		if(null != list && list.size()>0){
			for(RiskCircusee cir : list){
				Map<String,Object> map = new HashMap<String,Object>();
					map.put("deptName", cir.getOrganization().getOrgname());//部门名称
					map.put("empName", cir.getUnderTaker().getEmpname());//承办人名称
					map.put("approverName", null!=cir.getApprover()?cir.getApprover().getEmpname():"");//审批人名称
				listmap.add(map);
			}
		}
		return listmap;
	}
	/**
	 * 评估计划问卷监控--部门列表
	 * add by 王再冉
	 * 2014-3-7  上午11:04:28
	 * desc : 
	 * @param businessId	计划id
	 * @param query			搜索关键字
	 * @return 
	 * List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/queryreladeptbyplanId.f")
	public List<Map<String,Object>> queryrelaDeptByPlanId(String businessId,String query){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		List<String> deptList = new ArrayList<String>();
		List<String> scoreObjIdList = new ArrayList<String>();//打分对象id集合
		List<String> allorgIds = new ArrayList<String>();//全部部门id
		List<String> orgIdList = new ArrayList<String>();
		JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
		List<Object[]> jbpmHistActinstList = o_riskAssessPlanBO.findJbpmHistActinstsBySomeSQl(jbpmhp.getId());
		Map<String,Object> rtnMap = o_scoreDeptBO.findScoreDeptsByPlanId(businessId);//该计划的所有打分对象
		List<Map<String,Object>> scoreDeptMapList = (List<Map<String, Object>>) rtnMap.get("scoreDept");
		for(Map<String,Object> deptMap : scoreDeptMapList){
			RiskScoreDept scoreDept = (RiskScoreDept)deptMap.get("scoreDept");
			allorgIds.add(scoreDept.getOrganization().getId());
			if(!scoreObjIdList.contains(scoreDept.getScoreObject().getId())){
				scoreObjIdList.add(scoreDept.getScoreObject().getId());
			}
		}
		//根据打分对象查综合实体
		List<Map<String,Object>> rodeMapList = o_rangObjectDeptEmpBO.findobjDeptEmpListByScoreObjIdsMap(scoreObjIdList);
		if(rodeMapList.size()>0){
			for(Map<String,Object> rodeMap : rodeMapList){
				RiskScoreDept scoredept = (RiskScoreDept)rodeMap.get("scoreDept");
				if(!deptList.contains(scoredept.getOrganization().getId())){//查询该计划下的部门（去重）
					deptList.add(scoredept.getOrganization().getId());
					List<SysEmployee> nocompleteEmp = new ArrayList<SysEmployee>();//该部门已执行任务的员工集合
					List<SysEmployee> empList = new ArrayList<SysEmployee>();//该部门的员工人数
					for(Map<String,Object> rodeNextMap : rodeMapList){//查询相同部门的人员集合
						RiskScoreDept scorenextDept = (RiskScoreDept)rodeNextMap.get("scoreDept");
						if(scorenextDept.getOrganization().equals(scoredept.getOrganization())){
							SysEmployee scoreEmp = (SysEmployee)rodeNextMap.get("scoreEmp");
							if(!empList.contains(scoreEmp)){//去重
								empList.add(scoreEmp);//该部门下所有员工集合
							}
						}
					}
					for(SysEmployee emp : empList){
						for (Iterator<Object[]> iterator = jbpmHistActinstList.iterator(); iterator.hasNext();) {
							Object[] objects = (Object[]) iterator.next();
							String assigneeId = "";
							String activityName = "";
							if(null != objects[0]){
								assigneeId = objects[0].toString();
							}if(null != objects[3]){
								activityName = objects[3].toString();
							}
							if("评估任务分配".equals(activityName)){//存在任务分配节点，保存部门信息，以后做判断
								orgIdList = new ArrayList<String>();
								SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(assigneeId);
								orgIdList.add(empOrg.getSysOrganization().getId());
							}
							if(!("评估任务审批".equals(activityName)||
									"评估结果整理".equals(activityName))){
								if(StringUtils.isNotBlank(assigneeId)){
									if(assigneeId.equals(emp.getId())){
										nocompleteEmp.add(emp);//完成任务的员工集合
									}
								}
							}
						}
						if(jbpmHistActinstList.size()==0){
							nocompleteEmp.add(emp);//未完成任务的员工集合
						}
						Boolean isMeOrg = false;//判断任务分配节点是否是该员工部门的
						Set<SysEmpOrg> empDeptSet = emp.getSysEmpOrgs();
						for(SysEmpOrg empDept : empDeptSet){
							if(orgIdList.contains(empDept.getSysOrganization().getId())){
								isMeOrg = true;
							}
						}
						if(isMeOrg && !nocompleteEmp.contains(emp)){
							nocompleteEmp.add(emp);
						}
					}
					Map<String,Object> map = new HashMap<String,Object>();
					String deptRate = "";
					map.put("planId", businessId);
					map.put("riskworkflowId", null!=jbpmhp?jbpmhp.getBusinessWorkFlow().getProcessInstanceId():"");
					map.put("id", scoredept.getOrganization().getId());
					map.put("deptName", scoredept.getOrganization().getOrgname());
					if(empList.size()>0){
						double eSize = empList.size();
						double cSize = nocompleteEmp.size();
						BigDecimal b = new BigDecimal(1-cSize/eSize);
						double rate = b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); //四舍五入，保留两位小数
						if(nocompleteEmp.size()==0){//没有未完成的员工，说明计划已完成
							rate = 1.0;
						}else if(nocompleteEmp.size() == empList.size()){
							rate = 0.0;
						}
						map.put("complateRate", rate*100+"%");
						map.put("endactivity", rate);//状态
						if(1.0==rate){
							deptRate = "已完成";
						}else{
							deptRate = "处理中";
						}
					}else{
						map.put("complateRate", "0%");
						map.put("endactivity", "未分配");//状态
						deptRate = "未分配";
					}
					if(StringUtils.isNotBlank(query)){
						if(scoredept.getOrganization().getOrgname().contains(query)||deptRate.contains(query)){
							listmap.add(map);
						}
					}else{
						listmap.add(map);
					}
				}
			}
			if(allorgIds.size() > deptList.size()){//有部门未进行任务分配
				for(Map<String,Object> scoreDeptMap : scoreDeptMapList){
					RiskScoreDept scoreDept = (RiskScoreDept)scoreDeptMap.get("scoreDept");
					String oid = scoreDept.getOrganization().getId();
					if(!deptList.contains(oid)){
						deptList.add(oid);
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("planId", businessId);
						map.put("riskworkflowId", "");
						map.put("id", oid);
						map.put("deptName", scoreDept.getOrganization().getOrgname());
						map.put("complateRate", "0%");
						map.put("endactivity", 0.0);//状态
						listmap.add(map);
					}
				}
			}
		}else{//未到任务分配流程，只查询所有部门，状态设为未开始
			List<String> deptIdList = new ArrayList<String>();
			for(Map<String,Object> scoreDeptMap : scoreDeptMapList){
				RiskScoreDept scoreDept = (RiskScoreDept)scoreDeptMap.get("scoreDept");
				if(!deptIdList.contains(scoreDept.getOrganization().getId())){
					deptIdList.add(scoreDept.getOrganization().getId());
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("planId", businessId);
					map.put("riskworkflowId", "");
					map.put("id", scoreDept.getOrganization().getId());
					map.put("deptName", scoreDept.getOrganization().getOrgname());
					map.put("complateRate", "0%");
					map.put("endactivity", 0.0);//状态
					listmap.add(map);
				}
			}
		}
		return listmap;
	}
	/**
	 * 问卷监控--查询部门明细
	 * add by 王再冉
	 * 2014-3-7  上午11:07:20
	 * desc : 
	 * @param orgId			部门id
	 * @param businessId	计划id
	 * @param query			搜索关键字
	 * @return 
	 * List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/querydeptdetailedbydeptId.f")
	public List<Map<String,Object>> queryDeptDetailedByDeptId(String orgId, String businessId, String query){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		List<SysEmployee> empList = new ArrayList<SysEmployee>();//部门下人员集合
		List<String> objIdList = new ArrayList<String>();//打分对象id集合
		List<String> userIdList = new ArrayList<String>();//用户id
		List<RiskScoreObject> scoreObjectList = o_scoreObjectBO.findRiskScoreObjByplanId(businessId);
		for(RiskScoreObject obj : scoreObjectList){
			objIdList.add(obj.getId());
		}
		//根据打分对象查综合实体
		List<Map<String,Object>> rodeMapList = o_rangObjectDeptEmpBO.findobjDeptEmpListByScoreObjIdsMap(objIdList);
		for(Map<String,Object> rodeMap : rodeMapList){
			RiskScoreDept scoredept = (RiskScoreDept)rodeMap.get("scoreDept");
			SysEmployee scoreEmp = (SysEmployee)rodeMap.get("scoreEmp");
			if(orgId.equals(scoredept.getOrganization().getId())){
				if(!userIdList.contains(scoreEmp.getUserid())){
					userIdList.add(scoreEmp.getUserid());
				}
				if(!empList.contains(scoreEmp)){
					empList.add(scoreEmp);
				}
			}
		}
		List<SysUser> userList = o_riskAssessPlanBO.findUsersByuserIdList(userIdList);
		JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
		List<Object[]> jbpmHistActinstList = o_riskAssessPlanBO.findJbpmHistActinstsBySomeSQl(jbpmhp.getId());
		
		for(SysEmployee emp : empList){
			HashMap<String, Object> map = new HashMap<String, Object>();
			Set<SysRole> roleList = new HashSet<SysRole>();
			String empStatus = "";//人员状态
			String empEmailStr = "";//邮箱
			int count = 0;
			for(SysUser user : userList){
				if(user.getId().equals(emp.getUserid())){
					roleList = user.getSysRoles();
				}
			}
			String roleName = "";//查询人员的所有角色
			if(roleList.size()>0){
				for(SysRole role : roleList){
					if("".equals(roleName)){
						roleName = role.getRoleName();
					}else{
						roleName =roleName + "," + role.getRoleName();
					}
				}
			}
			Map<String,String> statusMap = o_riskAssessPlanBO.findPlanEmpStatusBySome(emp.getId(), jbpmHistActinstList,orgId);
			map.put("taskStatus", statusMap.get("status"));//执行状态
			map.put("executionId", statusMap.get("executionId"));
			empStatus = statusMap.get("queryStatus");
			for(Map<String,Object> rodeMap : rodeMapList){
				SysEmployee scoreEmp = (SysEmployee)rodeMap.get("scoreEmp");
				if(scoreEmp.getId().equals(emp.getId())){
					count++;
				}
			}
			map.put("id", emp.getId());
			map.put("empName", emp.getEmpname());
			map.put("emprole", roleName);
			map.put("scoreObjCount", count);//评价风险数量
			if(null != emp.getOemail()){
				empEmailStr = emp.getOemail();
			}
			map.put("email", empEmailStr);//邮箱
			if(StringUtils.isNotBlank(query)){//查询功能
				String countStr = count + "";
				if(emp.getEmpname().contains(query)||
						empEmailStr.contains(query)||empStatus.contains(query)||countStr.contains(query)){
					listmap.add(map);
				}
			}else{
				listmap.add(map);
			}
		}
		return listmap;
	}
	
	/**
	 * 浏览评估结果
	 * @param assessPlanId
	 * @param empId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findassessshowgridbyempid.f")
	public ArrayList<HashMap<String, Object>> findAssessShowGridByEmpId(String query,
			String businessId, String empId) {
		SysEmployee emp = o_empBO.findEmpEntryByEmpId(empId);
		String companyid = emp.getSysOrganization().getId();
		// 风险事件GRID
		return o_showAssessBO.findRiskByScoreEmpId(query, empId, o_showAssessBO.getTemplateId(businessId), true, businessId, companyid);
	}
	/**
	 * 根据业务ID查工作流ID
	 * @param businessId	计划ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findexcutionidbybusinessid.f")
	public Map<String,Object> findExcutionIdByBusinessId(String businessId){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		List<JbpmHistActinst> jbpmHistActinstList = new ArrayList<JbpmHistActinst>();
		JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
		if(null != jbpmhp){
			jbpmHistActinstList = jbpmhp.getJbpmHistActinsts();
			inmap.put("executionId", jbpmhp.getId_());//工作流id
		}
		for(JbpmHistActinst jbpmHistActinst:jbpmHistActinstList){
			VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
			if(jbpmHistTask!=null){
				inmap.put("resultNode", "true");
			}
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	/**
	 * 发送风险评估提醒邮件
	 * @param businessId	计划id
	 * @param empId			人员id
	 * @param executionId	工作流执行id
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/sendassessplanemailtoemp.f")
	public Map<String,Object> sendAssessPlanEmailToEmp(String businessId, String executionId,String empId){
		Map<String,Object> map = new HashMap<String,Object>();
		HashMap<String, Long> empIdTaskIdMap = new HashMap<String, Long>();
		TaskQuery taskQuery = o_jbpmOperate.getTaskService().createTaskQuery();
		Task task = taskQuery.assignee(empId).executionId(executionId).uniqueResult();
		String taskId = task.getId();
		empIdTaskIdMap.put(empId, Long.parseLong(taskId));
		o_assessEmailBO.sendAssessEmail(businessId, empIdTaskIdMap);
		map.put("success", true);
		return map;
	}
	/**
	 * exportQuestMoniDept：问卷监控，导出部门列表
	 * @param businessId		计划id
	 * @param exportFileName	导出列表名称
	 * @param sheetName			sheet页名称
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/exportquestmonidept.f")
	public void exportQuestMoniDept(String businessId, String exportFileName, String sheetName, HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		List<Map<String,Object>> deptListMap = this.queryrelaDeptByPlanId(businessId,null);
		for(Map<String,Object> deptMap : deptListMap){
			objects = new Object[3];
			objects[0] = null!=deptMap.get("deptName")?deptMap.get("deptName"):"";//部门名称
			objects[1] = null!=deptMap.get("complateRate")?deptMap.get("complateRate"):"";//完成比例
			if("1.0".equals(deptMap.get("endactivity").toString())){
				objects[2] = "已完成";
			}else if("0.0".equals(deptMap.get("endactivity").toString())){
				objects[2] = "未开始";
			}else{
				objects[2] = "处理中";
			}
			list.add(objects);
		}
		
		String[] fieldTitle = new String[3];
		fieldTitle[0] = "部门名称";
		fieldTitle[1] = "完成情况";
		fieldTitle[2] = "状态";
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "问卷监控部门列表.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	/**
	 * exportQuestMoniEmp：导出Excel问卷监控人员列表
	 * @param businessId		计划id
	 * @param orgId				人员所属部门id
	 * @param exportFileName	导出excel文件名
	 * @param sheetName			sheet页名称
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/exportquestmoniemp.f")
	public void exportQuestMoniEmp(String businessId, String orgId, String exportFileName, String sheetName, HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		List<Map<String,Object>> empListMap = this.queryDeptDetailedByDeptId(orgId, businessId, null);
		for(Map<String,Object> empMap : empListMap){
			objects = new Object[5];
			objects[0] = null!=empMap.get("empName")?empMap.get("empName"):"";
			objects[1] = null!=empMap.get("scoreObjCount")?empMap.get("scoreObjCount").toString():"";
			objects[2] = null!=empMap.get("emprole")?empMap.get("emprole"):"";
			objects[3] = null!=empMap.get("email")?empMap.get("email"):"";
			if("1".equals(empMap.get("taskStatus").toString())){
				objects[4] = "已完成";
			}else{
				objects[4] = "未开始";
			}
			list.add(objects);
		}
		
		String[] fieldTitle = new String[5];
		fieldTitle[0] = "姓名";
		fieldTitle[1] = "评价风险";
		fieldTitle[2] = "角色";
		fieldTitle[3] = "邮箱";
		fieldTitle[4] = "任务状态";
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "问卷监控人员列表.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 评估进度跟踪列表查询
	 * @param businessId	计划id
	 * @param query			搜索关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findplanfollowbybusinessid.f")
	public List<Map<String,Object>> findPlanFollowByBusinessId(String businessId, String query){
		return o_riskAssessPlanBO.findPlanFollowGridByBusinessId(businessId,query);
	}
	
	/**
	 * 评估进度跟踪导出email
	 * @param businessId		计划id
	 * @param exportFileName	导出文件名称
	 * @param sheetName			sheet文件名称
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/exportquestmoniall.f")
	public void exportQuestMoniAll(String businessId, String exportFileName, String sheetName, HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		List<Map<String,Object>> empListMap = o_riskAssessPlanBO.findPlanFollowGridByBusinessId(businessId,null);
		for(Map<String,Object> empMap : empListMap){
			objects = new Object[5];
			objects[0] = null!=empMap.get("deptName")?empMap.get("deptName"):"";
			objects[1] = null!=empMap.get("empName")?empMap.get("empName").toString():"";
			objects[2] = null!=empMap.get("scoreObjCount")?empMap.get("scoreObjCount"):"";
			if(null!=empMap.get("taskStatus")){
				if("1".equals(empMap.get("taskStatus").toString())){
					objects[3] = "已完成";
				}else{
					objects[3] = "未开始";
				}
			}else{
				objects[3] = "";
			}
			list.add(objects);
		}
		String[] fieldTitle = new String[5];
		fieldTitle[0] = "部门";
		fieldTitle[1] = "人员";
		fieldTitle[2] = "风险数量";
		fieldTitle[3] = "任务状态";
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "评估进度跟踪列表.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 评估范围选择风险过滤验证
	 * add by 金鹏祥
	 * @param assessPlanId	计划id
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value = "riskFiltering.f")
	public Map<String, Object> riskFiltering(String assessPlanId) {
//		String templateType = o_showAssessBO.getTemplateId(assessPlanId);
		Map<String, Object> result = new HashMap<String, Object>();
		boolean isSuccess = true;
		String message = "";
		Risk risk = null;
		List<RiskScoreObject> riskScoreObjectList = o_scoreObjectBO.findRiskScoreObjByplanId(assessPlanId);
		for (RiskScoreObject riskScoreObject : riskScoreObjectList) {
			risk = riskScoreObject.getRisk();
			if(null != risk){
				if(StringUtils.isBlank(risk.getIsRiskClass())){
					isSuccess = false;
					message = risk.getName() + "===风险类型不明确,请到基础信息维护==";
					break;
				}if(null == risk.getLevel()){
					message = risk.getName() + "===风险级别不明确,请到基础信息维护==";
					break;
				}
//				if("dim_template_type_custom".equalsIgnoreCase(templateType)){
//					if(null == risk.getTemplate()){
//						isSuccess = false;
//						message = risk.getName() + "===未选择评估模板类型,请到基础信息维护==";
//						break;
//					}
//				}
			}
		}
		
		result.put("message", message);//意见内容
		result.put("success", isSuccess);

		return result;
	}
	
	/**
	 * 查询默认模板（集团模板）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findsystemplate.f")
	public Map<String,String> findSysTemplate(){
		Map<String,String> map = new HashMap<String, String>();
		Template template = o_templateBO.findTemplateByType("dim_template_type_sys");
		if(null != template){
			map.put("template", template.getId());
		}
		return map;
	}
}