package com.fhd.ra.business.planconform;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.riskidentify.RiskIdentifyBO;
import com.fhd.ra.web.form.planconform.PlanConformForm;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 计划整合BO
 * @功能 : 
 * @author 王再冉
 * @date 2014-1-2
 * @since Ver
 * @copyRight FHD
 */
@Service
public class PlanConformBO {
	
	@Autowired
	private OrgGridBO o_orgGridBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private RiskIdentifyBO o_riskIdentifyBO;
	
	/**
	 * 保存计划
	 * add by 王再冉
	 * 2014-3-13  下午1:07:54
	 * desc : 
	 * @param planForm	计划表单实体
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	public Map<String, Object> savePlanByPlanType(PlanConformForm planForm) throws Exception{
		String companyId = UserContext.getUser().getCompanyid();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = null;
		if(StringUtils.isBlank(planForm.getId())) {
			assessPlan = new RiskAssessPlan();
			assessPlan.setId(Identities.uuid());
		} else {
			assessPlan = o_assessPlanBO.findRiskAssessPlanById(planForm.getId());
		}
		assessPlan.setPlanName(planForm.getPlanName());//计划名称
		assessPlan.setPlanCode(planForm.getPlanCode());//计划编号
		assessPlan.setCompany(o_orgGridBO.findOrganizationByOrgId(companyId));//保存当前机构
		assessPlan.setWorkType(planForm.getWorkType());//工作类型
		assessPlan.setRangeReq(planForm.getRangeReq());//范围要求
		assessPlan.setWorkTage(planForm.getWorkTage());//工作目标
		assessPlan.setPlanType(planForm.getPlanType());//计划类型
		assessPlan.setPlanCreatTime(new Date());//创建时间
		//分库标识 2017年3月28日13:01:49吉志强添加
		assessPlan.setSchm(planForm.getSchm());
		//风险应对不保存模板
		if(null != o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId()) && "riskAssess".equals(planForm.getPlanType())){
			Template temp = o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId());
			if(null != temp){
				assessPlan.setTemplate(temp);//评估模板
				assessPlan.setTemplateType(temp.getType().getId());//模板类型
			}
		}else{
		    if("riskContingencyPlanTotal".equals(planForm.getPlanType())){
		        assessPlan.setContingencyType(planForm.getContingencyType());
		        assessPlan.setTemplate(null);
				assessPlan.setTemplateType(null);
		    }
		    //为保密风险添加模板   20170424 郭鹏
		    else if("secrecyRiskResponse".equals(planForm.getPlanType()))
		    {
		    	Template temp = o_riskAssessPlanBO.findTemplateById("cb87546f-4b36-412d-bbc3-e7f667deaabd");
				if(null != temp){
					assessPlan.setTemplate(temp);//评估模板
					assessPlan.setTemplateType(temp.getType().getId());//模板类型
				}
		    }
			
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
		assessPlan.setStatus(Contents.DEAL_STATUS_SAVED);//保存：状态为"已保存"
		if(StringUtils.isNotBlank(planForm.getId())){//修改
			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
		}else{
			o_riskAssessPlanBO.saveRiskAssessPlan(assessPlan);//保存
		}
		DictEntry entry = o_dictBO.findDictEntryById(planForm.getPlanType());
//		DictEntry entry = o_dictBO.findDictEntryById(planForm.getPlanType()+"New");
		if(null != entry){
			inmap.put("dictValue", entry.getValue().split(";")[0]);//下一步js路径 
			inmap.put("valueAll", entry.getValue());
			System.out.println("planType:="+planForm.getPlanType());		
			System.out.println("dictValue:="+entry.getValue().split(";")[0]);
		}else{
			inmap.put("dictValue", "");
			inmap.put("valueAll", "");
		}
		
		
		
		inmap.put("planId", assessPlan.getId());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	
	/**
	 * 为计划管理菜单抽离 代码复制，不影响其他功能
	 * @author Jzq
	 * @param planForm
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> savePlanByPlanTypeNew(PlanConformForm planForm) throws Exception{
		String companyId = UserContext.getUser().getCompanyid();
		
		String deptId = UserContext.getUser().getMajorDeptId();
		String deptNo = UserContext.getUser().getMajorDeptNo();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = null;
		if(StringUtils.isBlank(planForm.getId())) {
			assessPlan = new RiskAssessPlan();
			assessPlan.setId(Identities.uuid());
		} else {
			assessPlan = o_assessPlanBO.findRiskAssessPlanById(planForm.getId());
		}
		assessPlan.setPlanName(planForm.getPlanName());//计划名称
		assessPlan.setPlanCode(planForm.getPlanCode());//计划编号
		assessPlan.setCompany(o_orgGridBO.findOrganizationByOrgId(companyId));//保存当前机构
		assessPlan.setWorkType(planForm.getWorkType());//工作类型
		assessPlan.setRangeReq(planForm.getRangeReq());//范围要求
		assessPlan.setWorkTage(planForm.getWorkTage());//工作目标
		assessPlan.setPlanType(planForm.getPlanType());//计划类型
		assessPlan.setPlanCreatTime(new Date());//创建时间
		/////////////部门风险要加入这个字段
		assessPlan.setCreateOrg(deptId);
		//分库标识 2017年3月28日13:01:49吉志强添加
		assessPlan.setSchm(planForm.getSchm());
		//风险应对不保存模板
		if(null != o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId()) && "riskAssess".equals(planForm.getPlanType())){
			Template temp = o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId());
			if(null != temp){
				assessPlan.setTemplate(temp);//评估模板
				assessPlan.setTemplateType(temp.getType().getId());//模板类型
			}
			//部门风险评估
		}else if(null != o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId()) && "riskAssessDept".equals(planForm.getPlanType())){
			Template temp = o_riskAssessPlanBO.findTemplateById(planForm.getTemplateId());
			if(null != temp){
				assessPlan.setTemplate(temp);//评估模板
				assessPlan.setTemplateType(temp.getType().getId());//模板类型
			}
		}else{
		    if("riskContingencyPlanTotal".equals(planForm.getPlanType())){
		        assessPlan.setContingencyType(planForm.getContingencyType());
		    }
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
		assessPlan.setStatus(Contents.DEAL_STATUS_SAVED);//保存：状态为"已保存"
		if(StringUtils.isNotBlank(planForm.getId())){//修改
			o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
		}else{
			o_riskAssessPlanBO.saveRiskAssessPlan(assessPlan);//保存
		}
		DictEntry entry = o_dictBO.findDictEntryById(planForm.getPlanType()+"New");
		if(null != entry){
			inmap.put("dictValue", entry.getValue().split(";")[0]);//下一步js路径 
			inmap.put("valueAll", entry.getValue());
		}else{
			inmap.put("dictValue", "");
			inmap.put("valueAll", "");
		}
		
		
		
		inmap.put("planId", assessPlan.getId());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	
	
	/**
	 * 查询计划整合列表
	 * add by 王再冉
	 * 2014-3-13  下午1:15:33
	 * desc : 
	 * @param query		搜索框关键字
	 * @param page		page实体
	 * @param sort		排序关键字
	 * @param companyId	公司id
	 * @param status	状态
	 * @return 
	 * Page<RiskAssessPlan>
	 */
	public Page<RiskAssessPlan> findAllPlanTypesGridPage(String query, Page<RiskAssessPlan> page, String sort,
																			String companyId,String status,String schm){
		DetachedCriteria dc = DetachedCriteria.forClass(RiskAssessPlan.class);
		if(null != companyId){
			dc.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(status)){//处理状态
			dc.add(Restrictions.eq("dealStatus", status));
		}
		if(StringUtils.isNotBlank(query)){
			dc.add(Restrictions.like("planName", query, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(schm)){
			dc.add(Restrictions.eq("schm", schm));
			if("dept".equals(schm)){
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				dc.add(Restrictions.eq("createOrg", deptId));
			}
		}
		
		dc.addOrder(Order.desc(sort));
		return o_riskAssessPlanDAO.findPage(dc, page, false);
	}
	
	
	/**
	 * 为不同planType查询计划列表     主要为风险评估、风险辨识、风险应对、应急预案
	 * @Time 2017年3月29日17:29:33
	 * @author Jzq
	 * @param query
	 * @param page
	 * @param sort
	 * @param companyId
	 * @param status
	 * @param schm
	 * @param planType
	 * @return
	 */
	public Page<RiskAssessPlan> findAllPlanTypesGridPage(String query, Page<RiskAssessPlan> page, String sort,
			String companyId,String status,String schm ,String planType){
		DetachedCriteria dc = DetachedCriteria.forClass(RiskAssessPlan.class);
		if(null != companyId){
		dc.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(status)){//处理状态
		dc.add(Restrictions.eq("dealStatus", status));
		}
		if(StringUtils.isNotBlank(planType)){//计划类型  2017年3月29日17:30:07 吉志强添加
			dc.add(Restrictions.eq("planType", planType));
			}
		if(StringUtils.isNotBlank(query)){
		dc.add(Restrictions.like("planName", query, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(schm)){
		dc.add(Restrictions.eq("schm", schm));
		if("dept".equals(schm)){
		SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
		String seq = org.getOrgseq();
		String deptId = seq.split("\\.")[2];//部门编号
		
		dc.add(Restrictions.eq("createOrg", deptId));
		}
		}
		
		dc.addOrder(Order.desc(sort));
		return o_riskAssessPlanDAO.findPage(dc, page, false);
}
	
	
	
	/**
	 * 加载计划表单数据
	 * add by 王再冉
	 * 2014-1-2  下午3:16:27
	 * desc : 
	 * @param id	计划id
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findPlanFormById(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan plan = o_riskAssessPlanDAO.get(id);
		if(null != plan){
			inmap.put("id", id);
			inmap.put("planName", plan.getPlanName());
			if(null != plan.getTemplate() && "riskAssess".equals(plan.getPlanType())){//风险评估模板
				inmap.put("templateId", plan.getTemplate().getId());
				inmap.put("templateName", plan.getTemplate().getName());
				inmap.put("templateType", plan.getTemplateType());
			}
			else if(null != plan.getTemplate() && "riskAssessDept".equals(plan.getPlanType())){//部门风险评估模板
				inmap.put("templateId", plan.getTemplate().getId());
				inmap.put("templateName", plan.getTemplate().getName());
				inmap.put("templateType", plan.getTemplateType());
			}
			else if("riskContingencyPlanTotal".equals(plan.getPlanType())){//应急方案
			    inmap.put("contingencyType", plan.getContingencyType());
            }else{
				if("dim_template_type_custom".equals(plan.getTemplateType())){
					inmap.put("templateName", "自定义模板");
					inmap.put("templateType", "dim_template_type_custom");
				}else{
					inmap.put("templateId", "");
					inmap.put("templateName", "");
					inmap.put("templateType", "");
				}
			}
			inmap.put("effective", plan.getEffective());
			inmap.put("planCode", plan.getPlanCode());
			inmap.put("rangeReq", plan.getRangeReq());
			inmap.put("planType", plan.getPlanType());//计划类型
			//联系人
			JSONArray conarray = new JSONArray();
			JSONObject conjson = new JSONObject();
			if(null != plan.getContactPerson()){
				conjson.put("id", plan.getContactPerson().getId());
				conjson.put("empno", plan.getContactPerson().getEmpcode());
				conjson.put("empname", plan.getContactPerson().getEmpname());
				
				conarray.add(conjson);
				inmap.put("contactName",conarray.toString() );
			}else{
				//conjson.put("id", "");
				//conarray.add(conjson);
				//inmap.put("contactName",conarray.toString() );
			}
			//负责人
			JSONArray resparray = new JSONArray();
			JSONObject respjson = new JSONObject();
			if(null != plan.getResponsPerson()){
				respjson.put("id", plan.getResponsPerson().getId());
				respjson.put("empno", plan.getResponsPerson().getEmpcode());
				respjson.put("empname", plan.getResponsPerson().getEmpname());
				resparray.add(respjson);
				inmap.put("responsName",resparray.toString() );
			}else{
				//respjson.put("id", "");
				//resparray.add(respjson);
				//inmap.put("responsName",resparray.toString() );
			}
			inmap.put("collectRate", plan.getCollectRate());
			inmap.put("workType", plan.getWorkType());
			inmap.put("workTage", plan.getWorkTage());
			//开始时间
			if(null != plan.getBeginDate()){
				inmap.put("beginDataStr", plan.getBeginDate().toString().split(" ")[0]);
			}else{
				inmap.put("beginDataStr", "");
			}
			//结束时间
			if(null != plan.getEndDate()){
				inmap.put("endDataStr", plan.getEndDate().toString().split(" ")[0]);
			}else{
				inmap.put("endDataStr", "");
			}
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	/**
	 * 按部门添加风险sql
	 * add by 王再冉
	 * 2014-2-20  下午1:12:52
	 * desc : 
	 * @param orgIdList	部门id集合
	 * @param planId	计划id
	 * @param isMain	是否只添加主部门（应对计划要求值添加主部门）
	 * @return 
	 * Boolean
	 */
	public Boolean saveScoreObjectsAndScoreDeptsByOrgIds(List<String> orgIdList, String planId, Boolean isMain){
		List<RiskScoreObject> saveObjList = new ArrayList<RiskScoreObject>();
		List<RiskScoreDept> saveDeptList = new ArrayList<RiskScoreDept>();
		Map<String,String> savedRiskIdsMap = new HashMap<String, String>();//已经保存的风险（打分对象只保存一次）
		//查询计划下所有打分对象打分部门和风险id
		HashMap<String, ArrayList<String>> savedMap = o_scoreDeptBO.findAllSaveScoresByplanId(planId);
		//查询部门相关的所有风险事件
		//TODO 0521
		ArrayList<HashMap<String, String>> riskOrgMapList = o_riskIdentifyBO.findRisksByOrgIds(orgIdList,isMain,null);
		for(HashMap<String, String> hashMap : riskOrgMapList){
			Boolean saved = true;
			String riskId = hashMap.get("riskId");
			String orgId = hashMap.get("orgId");
			String eType = hashMap.get("eType");
			if(null == savedMap.get(riskId)){//该风险未保存
				RiskScoreObject scoreObj = new RiskScoreObject();
				scoreObj.setId(Identities.uuid());
				scoreObj.setRisk(new Risk(riskId));
				scoreObj.setAssessPlan(new RiskAssessPlan(planId));
				if(null == savedRiskIdsMap.get(riskId)){//判断之前保存的打分对象中是否有重复的数据
					saveObjList.add(scoreObj);//批量保存的打分对象list
					savedRiskIdsMap.put(riskId, scoreObj.getId());
				}else{
					scoreObj.setId(savedRiskIdsMap.get(riskId));
				}
				//保存打分部门
				RiskScoreDept scoreDept = new RiskScoreDept();
				scoreDept.setId(Identities.uuid());
				scoreDept.setScoreObject(scoreObj);
				scoreDept.setOrganization(new SysOrganization(orgId));
				scoreDept.setOrgType(eType);//部门类型
				saveDeptList.add(scoreDept);
			}else{
				ArrayList<String> savedStr = savedMap.get(riskId);
				for(String str : savedStr){
					String[] strArray = str.split("--");//0:scoreObjId,1:scoreDeptId,2:orgId,3:orgType
					if(orgId.equals(strArray[2]) && eType.equals(strArray[3])){//部门id和部门类型不同时满足，保存打分部门
						saved = false;
					}
				}
				if(saved){
					String objId = savedStr.get(0).split("--")[0];
					RiskScoreDept scoreDept = new RiskScoreDept();
					scoreDept.setId(Identities.uuid());
					scoreDept.setScoreObject(new RiskScoreObject(objId));
					scoreDept.setOrganization(new SysOrganization(orgId));
					scoreDept.setOrgType(eType);//部门类型
					saveDeptList.add(scoreDept);
				}
			}
		}
		o_riskScoreBO.saveRiskScoreObjectsSql(saveObjList);//批量保存打分对象
		o_riskScoreBO.saveRiskScoreDeptsSql(saveDeptList);//批量保存打分部门
		return true;
	}
	
	/**
	 * 根据部门id集合查询风险部门实体集合
	 * add by 王再冉
	 * 2014-1-3  下午5:02:37
	 * desc : 
	 * @param orgIds	部门id集合
	 * @param isMain	true：只查询主部门
	 * @return 
	 * List<RiskOrg>
	 */
	@SuppressWarnings("unchecked")
	public List<RiskOrg> findRiskOrgsByorgIds(List<String> orgIds, Boolean isMain) {
		Criteria criteria = o_riskOrgDAO.createCriteria();
		List<RiskOrg> riskOrgList = new ArrayList<RiskOrg>();
		if(orgIds.size()>0){
			criteria.createAlias("risk", "risk", CriteriaSpecification.LEFT_JOIN);
			criteria.add(Restrictions.in("sysOrganization.id", orgIds));
			if(isMain){
				criteria.add(Restrictions.eq("type", "M"));
			}
			riskOrgList = criteria.list();
		}
		return riskOrgList;
	}
	
}
