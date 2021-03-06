package com.fhd.ra.web.controller.risk;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskRelaTemplate;
import com.fhd.entity.risk.ScoreInstance;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.ra.business.risk.TemplateTreeBO;
import com.fhd.ra.web.form.risk.TemplateRelaDimensionForm;
import com.fhd.sys.business.organization.OrgGridBO;

import common.Logger;

/**
 * ClassName:TemplateControl 模板控制类
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-13		下午3:50:34
 *
 * @see 	 
 */
@Controller
public class TemplateControl {

	@Autowired
	private TemplateBO o_templateBO;
	
	@Autowired
	private TemplateTreeBO o_templateTreeBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private RAssessPlanBO o_rAssessPlanBO;
	
	@Autowired
	private OrgGridBO o_orgGridBO;
	
	private static final Logger log = Logger.getLogger(TemplateControl.class);
	
	/**
	 * <pre>
	 * 拷贝模板
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param templateId 源模板ID
	 * @param riskId 风险ID
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value="risk/saveTemplateById.f")
	public void saveTemplateById(String templateId, String riskId, HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag="false";
		try{
			out = response.getWriter();
			o_templateBO.saveTemplateById(templateId, riskId);
			flag="true";
		}finally{
			out.write(flag);
			out.close();
		}
	}
	
	/**
	 * <pre>
	 * 从待选维度列表里新增模板关联维度
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param parentId 上级模板关联维度ID
	 * @param dimensionId 维度ID
	 * @param templateId 模板ID
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value="risk/saveTemplateRelaDimension.f")
	public void saveTemplateRelaDimension(String parentId, String dimensionId, String templateId, HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag="false";
		try{
			out = response.getWriter();
			o_templateBO.saveTemplateRelaDimension(parentId, dimensionId, templateId);
			flag="true";
		}finally{
			out.write(flag);
			out.close();
		}
	}
	
	/**
	 * <pre>
	 * 批量更新维度信息
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param jsonString 要更新的记录的字符串
	 * @param riskId 风险ID
	 * @param response
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value="risk/mergeTemplateBatch.f")
	public void mergeTemplateBatch(String jsonString,String riskId, HttpServletResponse response) throws IOException {
		PrintWriter out = null;
		String flag="false";
		try{
			out = response.getWriter();
			String companyId = UserContext.getUser().getCompanyid();
			o_templateBO.mergeTemplateBatch(jsonString, companyId, riskId);
			flag="true";
		}finally{
			out.write(flag);
			out.close();
		}
	}
	
	
	@ResponseBody
    @RequestMapping(value="risk/mergeTemplateRelaCaluFormular.f")
	public Map<String,Object> mergeTemplateRelaCaluFormular(String templateId,String formularContext){
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		try{
			o_templateBO.saveTemplateRelaCaluFormular(templateId,formularContext);
			rtnMap.put("success", true);
		}catch(Exception ex){
			rtnMap.put("success", false);
		}
		return rtnMap;
	}
	
	
	/**
	 * <pre>
	 * 批量更新模板关联维度的分值信息
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param jsonString 要更新的记录的字符串
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value="risk/mergeScoreInstanceBatch.f")
	public void mergeScoreInstanceBatch(String jsonString,HttpServletResponse response) throws IOException {
		PrintWriter out = null;
		String flag="false";
		try{
			out = response.getWriter();
			o_templateBO.mergeScoreInstanceBatch(jsonString);
			flag="true";
		}finally{
			out.write(flag);
			out.close();
		}
	}
	
	/**
	 * <pre>
	 * 保存附件
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param fileId 附件ID
	 * @param templateId 模板ID
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value="risk/mergeTemplateFile.f")
	public void mergeTemplateFile(String fileId, String templateId, HttpServletResponse response) throws IOException {
		PrintWriter out = null;
		String flag="false";
		try{
			out = response.getWriter();
			Template template = o_templateBO.findTemplateById(templateId);
			template.setFile(new FileUploadEntity(fileId));
			o_templateBO.mergeTemplate(template);
			flag="true";
		}finally{
			out.write(flag);
			out.close();
		}
	}
	
	/**
	 * <pre>
	 * 保存模板关联维度数据
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param data 模板关联维度form表单
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value="risk/mergeTemplateRelaDimension.f")
	public void mergeTemplateRelaDimension(TemplateRelaDimensionForm data,HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag = "false";
		TemplateRelaDimension templateRelaDimension = o_templateBO.findTemplateRelaDimensionById(data.getId());
		try{
			out = response.getWriter();
			if(StringUtils.isNotBlank(data.getCalculateMethodId())){
				templateRelaDimension.setCalculateMethod(new DictEntry(data.getCalculateMethodId()));
				templateRelaDimension.setIsCalculate(true);//是计算项
			}else{
				templateRelaDimension.setIsCalculate(false);//不是计算项
			}
			templateRelaDimension.setWeight(data.getWeight());
			templateRelaDimension.setDesc(data.getDesc());
			o_templateBO.mergeTemplateRelaDimension(templateRelaDimension);
			flag = "true";
		} finally {
			out.write(flag);
			out.close();
		}
	}
	
	/**
	 * <pre>
	 * 根据维度ID物理级联删除维度及该维度下的维度分值数据
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param dimensionId 维度ID
	 * @param response 
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "risk/removeTemplateById.f")
	public void removeTemplateById(String templateId,HttpServletResponse response) throws Exception{
		PrintWriter out = null;
		String  flag = "false";
		try{
			out = response.getWriter();
			o_templateBO.removeTemplateById(templateId);
			flag = "true";
		} finally {
			out.write(flag);
			out.close();
		}
	}
	
	/**
	 * <pre>
	 * 删除模板关联维度
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param templateRelaDimensionId 模板关联维度ID
	 * @param response
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "risk/removeTemplateRelaDimensionById.f")
	public void removeTemplateRelaDimensionById(String templateRelaDimensionId,HttpServletResponse response) throws Exception{
		PrintWriter out = null;
		String  flag = "false";
		try{
			out = response.getWriter();
			o_templateBO.removeTemplateRelaDimensionById(templateRelaDimensionId);
			flag = "true";
		} finally {
			out.write(flag);
			out.close();
		}
	}
	/**
	 * <pre>
	 * 查询TemplateRelaDimension对象
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param templateRelaDimensionId 模板关联维度ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "risk/findTemplateRelaDimension.f")
	public Map<String, Object> findTemplateRelaDimension(String templateRelaDimensionId){
		Map<String, Object> map = new HashMap<String, Object>();
		TemplateRelaDimensionForm templateRelaDimensionForm = new TemplateRelaDimensionForm();
		TemplateRelaDimension templateRelaDimension = o_templateBO.findTemplateRelaDimensionById(templateRelaDimensionId);
		templateRelaDimensionForm.setId(templateRelaDimension.getId());
		templateRelaDimensionForm.setName(templateRelaDimension.getDimension().getName());
		if(StringUtils.isNotBlank(templateRelaDimension.getDesc())){//如果描述为空则将维度的描述赋值给它
			templateRelaDimensionForm.setDesc(templateRelaDimension.getDesc());
		}else{
			templateRelaDimensionForm.setDesc(templateRelaDimension.getDimension().getDesc());
		}
		if(null != templateRelaDimension.getCalculateMethod()){//计算方法不为空时将该数据字典的ID赋值给它
			templateRelaDimensionForm.setCalculateMethodId(templateRelaDimension.getCalculateMethod().getId());
		}else{
			templateRelaDimensionForm.setCalculateMethodId("");
		}
		templateRelaDimensionForm.setWeight(templateRelaDimension.getWeight());
		map.put("success", true);
		map.put("data", templateRelaDimensionForm);
		return map;
	}
	/**
	 * <pre>
	 * 查询模板关联维度下的分值信息
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param templateRelaDimensionId 模板关联维度ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value="risk/findScoreInstanceList.f")
	public Map<String, Object> findScoreInstanceList(String templateRelaDimensionId){
		List<ScoreInstance> scoreInstanceList = new ArrayList<ScoreInstance>();
		scoreInstanceList = o_templateBO.findScoreInstanceBySome(null, templateRelaDimensionId);
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		for (ScoreInstance scoreInstance : scoreInstanceList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", scoreInstance.getId());
			item.put("name",scoreInstance.getName());
			item.put("value",scoreInstance.getScore().getValue());
			item.put("desc",scoreInstance.getDesc());
			item.put("sort",scoreInstance.getScore().getSort());
			datas.add(item);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", datas.size());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * <pre>
	 * 根据查询条件查询模板列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param start 不可用
	 * @param limit 不可用
	 * @param query 查询条件
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "/risk/findTemplateList.f")
	public Map<String, Object> findTemplateList(int start,int limit,String riskId, String query) throws Exception {
		if("null".equalsIgnoreCase(riskId)){
			riskId = null;
		}
		Boolean isCompanyEmp = false;	//true表示是集团的员工
		String companyId = UserContext.getUser().getCompanyid();
		SysOrganization org = o_orgGridBO.findOrganizationByOrgId(companyId);
		if(null != org && null == org.getParentOrg()){
			isCompanyEmp = true;  //是集团的员工
		}
		List<RiskAssessPlan> planList = new ArrayList<RiskAssessPlan>();
		Set<String> usedPlanTemplateIdSet = new HashSet<String>();//所有使用中的模板的ID的集合
		List<Template> templateList = new ArrayList<Template>();
		List<RiskRelaTemplate> riskRelaTemplateList = new ArrayList<RiskRelaTemplate>();
		templateList = o_templateBO.findTemplateByQueryAndCompanyId(query,null);//按公司Id查询模板
		Set<String> usedTemplateIdSet = new HashSet<String>();//所有使用中的模板的ID的集合
		List<Risk> riskList = new ArrayList<Risk>();
		List<Template> newTemplateList = new ArrayList<Template>();
		//自有模板按公司查询
		if(!isCompanyEmp){
			for(Template temp : templateList){
				if(!"dim_template_type_self".equals(temp.getType().getId())){
					newTemplateList.add(temp);
				}else if(companyId.equals(temp.getCompany().getId())){
					newTemplateList.add(temp);
				}
			}
		}else{
			//newTemplateList  = templateList;
			for(Template temp : templateList){
				if("dim_template_type_sys".equals(temp.getType().getId())){
					newTemplateList.add(temp);
				}else if(companyId.equals(temp.getCompany().getId())){
					newTemplateList.add(temp);
				}
			}
		}
		
		riskList = o_riskBO.findRiskBySome(companyId, "1");
		if(StringUtils.isNotBlank(riskId)){//如果有风险ID
			Risk risk = null;
			riskRelaTemplateList = o_templateBO.findRiskRelaTemplateBySome(riskId,null);
			risk = o_riskBO.findRiskById(riskId);
			if(null != risk && null != risk.getTemplate() && StringUtils.isNotBlank(risk.getTemplate().getId())){
				usedTemplateIdSet.add(risk.getTemplate().getId());
			}
		}
		for (Risk risk : riskList) {
			if(null != risk && null != risk.getTemplate() && StringUtils.isNotBlank(risk.getTemplate().getId())){
				usedTemplateIdSet.add(risk.getTemplate().getId());
			}
		}
		
		planList = o_rAssessPlanBO.findRiskAssessPlansBydealStatus();//未开始和执行中的评估计划集合
		for(RiskAssessPlan plan : planList){
			if(null != plan.getTemplate()){ // && "dim_template_type_self".equals(plan.getTemplate().getType().getId())
				try {
					usedPlanTemplateIdSet.add(plan.getTemplate().getId());
				} catch (Exception e) {
					log.error("=============================" + e.getMessage());
				}
				
			}
		}
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		for (Template template : newTemplateList) {
			if(riskRelaTemplateList.size()>0){//如果该风险关联了模板
				for (RiskRelaTemplate riskRelaTemplate : riskRelaTemplateList) {
					if(null != riskRelaTemplate.getTemplate() && template.getId().equals(riskRelaTemplate.getTemplate().getId())){
						Map<String, Object> item = new HashMap<String, Object>();
						item.put("id", template.getId());
						item.put("name",template.getName());
						item.put("type",template.getType().getId());
						item.put("desc",template.getDesc());
						item.put("sort",template.getSort());
						item.put("caluFormula", template.getCaluFormula());
						//自定义模板初始化为已使用，不允许修改
						if(usedTemplateIdSet.contains(template.getId())||"dim_template_type_custom".equals(template.getType().getId())){
							item.put("used", true);
						}else{
							item.put("used", false);
						}
						if("dim_template_type_sys".equals(template.getType().getId())){//如果是集团模板，只能由集团的人编辑
							item.put("isDefault", "1");//集团模板为默认模板
							if(isCompanyEmp){
								item.put("roleEdit", true);//有权限修改集团模板
							}else{
								item.put("roleEdit", false);
							}
						}else{
							item.put("roleEdit", true);
						}
						item.put("editable", riskRelaTemplate.getIsCreator());
						item.put("fileId", null != template.getFile()?template.getFile().getId():"");
						datas.add(item);
					}
				}
			}else if(usedTemplateIdSet.contains(template.getId())){
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("id", template.getId());
				item.put("name",template.getName());
				item.put("type",template.getType().getId());
				item.put("desc",template.getDesc());
				item.put("sort",template.getSort());
				item.put("used", true);
				item.put("caluFormula", template.getCaluFormula());
				item.put("editable", false);//已使用的模板不可编辑
				if("dim_template_type_sys".equals(template.getType().getId())){//如果是集团模板，只能由集团的人编辑
					item.put("isDefault", "1");//集团模板为默认模板
					if(isCompanyEmp){
						item.put("roleEdit", true);//有权限修改集团模板
					}else{
						item.put("roleEdit", false);
					}
				}else{
					item.put("roleEdit", true);
				}
				item.put("fileId", null != template.getFile()?template.getFile().getId():"");
				datas.add(item);
			}else{//如果该风险没有关联模板
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("id", template.getId());
				item.put("name",template.getName());
				item.put("type",template.getType().getId());
				item.put("desc",template.getDesc());
				item.put("sort",template.getSort());
				item.put("caluFormula", template.getCaluFormula());
				item.put("used", false);
				if("dim_template_type_sys".equals(template.getType().getId())){//如果是集团模板，只能由集团的人编辑
					item.put("isDefault", "1");//集团模板为默认模板
					if(isCompanyEmp){
						item.put("editable", true);
						item.put("roleEdit", true);//有权限修改集团模板
					}else{
						item.put("editable", false);
						item.put("roleEdit", false);
					}
				}else{
					item.put("editable", true);
					item.put("roleEdit", true);
				}
				/*if(usedTemplateIdSet.contains(template.getId())||"dim_template_type_custom".equals(template.getType().getId())){
					item.put("used", true);
				}else{
					item.put("used", false);
				}
				item.put("editable", true);*/
				item.put("fileId", null != template.getFile()?template.getFile().getId():"");
				datas.add(item);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", datas.size());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 查询模板列表，按评估计划情况判断是否使用，并判断人员修改权限
	 * @param start
	 * @param limit
	 * @param query
	 * @return
	 * @throws Exception
	 * 
	 * @author 王再冉
	 */
	@ResponseBody
	@RequestMapping(value = "risk/findtemplatelistbyassessplan.f")
	public Map<String, Object> findTemplateListByassessPlan(int start,int limit, String query) throws Exception {
		List<Template> templateList = new ArrayList<Template>();
		Boolean isCompanyEmp = false;	//true表示是集团的员工
		String companyId = UserContext.getUser().getCompanyid();
		SysOrganization org = o_orgGridBO.findOrganizationByOrgId(companyId);
		if(null != org && null == org.getParentOrg()){
			isCompanyEmp = true;  //是集团的员工
		}
		templateList = o_templateBO.findTemplateByQueryAndCompanyId(query,null);//按公司Id查询模板
		Set<String> usedTemplateIdSet = new HashSet<String>();//所有使用中的模板的ID的集合
		List<RiskAssessPlan> planList = new ArrayList<RiskAssessPlan>();
		List<Template> newTemplateList = new ArrayList<Template>();
		//自有模板按公司查询
		if(!isCompanyEmp){
			for(Template temp : templateList){
				if(!"dim_template_type_self".equals(temp.getType().getId())){
					newTemplateList.add(temp);
				}else if(companyId.equals(temp.getCompany().getId())){
					newTemplateList.add(temp);
				}
			}
		}else{
			newTemplateList  = templateList;
		}
		
		planList = o_rAssessPlanBO.findRiskAssessPlansBydealStatus();//未开始和执行中的评估计划集合
		
		for(RiskAssessPlan plan : planList){
			if(null != plan.getTemplate()){ // && "dim_template_type_self".equals(plan.getTemplate().getType().getId())
				try {
					usedTemplateIdSet.add(plan.getTemplate().getId());
				} catch (Exception e) {
					log.error("=============================" + e.getMessage());
				}
				
			}
		}
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		for (Template template : newTemplateList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", template.getId());
			item.put("name",template.getName());
			item.put("type",template.getType().getId());
			item.put("desc",template.getDesc());
			item.put("sort",template.getSort());
			//如果有正在执行的评估计划使用该模板，设为已使用
			if(usedTemplateIdSet.contains(template.getId())){
				item.put("used", true);
				item.put("editable", false);//已使用的模板不可编辑
				if("dim_template_type_sys".equals(template.getType().getId())){//如果是集团模板，只能由集团的人编辑
					item.put("isDefault", "1");//集团模板为默认模板
				}
			}else{
				item.put("used", false);
				if("dim_template_type_sys".equals(template.getType().getId())){//如果是集团模板，只能由集团的人编辑
					item.put("isDefault", "1");//集团模板为默认模板
					if(isCompanyEmp){
						item.put("editable", true);
					}else{
						item.put("editable", false);
					}
				}else{
					item.put("editable", true);
				}
			}
			item.put("fileId", null != template.getFile()?template.getFile().getId():"");
			datas.add(item);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", datas.size());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * <pre>
	 * 根据查询条件查询所有满足条件的待选维度数据
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param start 不可用
	 * @param limit 不可用
	 * @param templateId 模板ID
	 * @param query 查询条件
	 * @param deleteStatus 删除状态
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "risk/findDimensionForSelectList.f")
	public Map<String, Object> findDimensionForSelectList(int start,int limit,String templateId, String query,String deleteStatus) throws Exception {
		List<Dimension> dimensionList = new ArrayList<Dimension>();
		Set<String> ingnorDimensionIdSet = new HashSet<String>();
		SysOrganization company = o_orgGridBO.findOrgByParentIdIsNUll();//集团
		//查找集团模板,将集团模板的所有维度添加到列表中
//		List<TemplateRelaDimension> templateRelaDimensionSysList = new ArrayList<TemplateRelaDimension>();
//		Template templateSys = o_templateBO.findTemplateByType("dim_template_type_sys");
//		if(null != templateSys){
//			templateRelaDimensionSysList = o_templateBO.findTemplateRelaDimensionBySome(null, templateSys.getId());
//		}
		List<TemplateRelaDimension> templateRelaDimensionList = o_templateBO.findTemplateRelaDimensionBySome(null, templateId);//查找被模板选中的维度数据
//		templateRelaDimensionList.addAll(templateRelaDimensionSysList);
		for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionList) {
			ingnorDimensionIdSet.add(templateRelaDimension.getDimension().getId());//加入到忽略的集合里
		}
		if(StringUtils.isNotBlank(templateId)){
			dimensionList = o_dimensionBO.findDimensionAndCompanyDimBySome(query, deleteStatus, ingnorDimensionIdSet, company.getId());//查询有效的维度列表，并且忽略已经被模板选中的
		}
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		for (Dimension dimension : dimensionList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", dimension.getId());
			item.put("name",dimension.getName());
			item.put("code",dimension.getCode());
			item.put("desc",dimension.getDesc());
			item.put("deleteStatus",dimension.getDeleteStatus());
			item.put("sort",dimension.getSort());
			datas.add(item);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", datas.size());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * <pre>
	 * 加载模板维度相关树列表信息
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param query 查询条件
	 * @param templateId 模板ID
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping(value = "risk/templateRelaDimensionTreeLoader.f")
	public Map<String,Object> templateRelaDimensionTreeLoader(String query, String templateId) throws Exception {
		return o_templateTreeBO.templateRelaDimensionTreeLoader(query, templateId);
	}
	
}

