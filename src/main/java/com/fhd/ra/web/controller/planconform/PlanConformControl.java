package com.fhd.ra.web.controller.planconform;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.SpringContextHolder;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.planconform.PlanConformBO;
import com.fhd.ra.business.riskidentify.RiskIdentifyBO;
import com.fhd.ra.web.form.planconform.PlanConformForm;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpGridBO;

/**
 * 计划整合control类
 * @功能 : 计划整合的功能
 * @author 王再冉
 * @date 2013-12-31
 * @since Ver
 * @copyRight FHD
 */
@Controller
public class PlanConformControl {

	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private PlanConformBO o_planConformBO;
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private RiskIdentifyBO o_riskIdentifyBO;
	/**
	 * 查询流程选择下拉列表
	 * add by 王再冉
	 * 2013-12-31  上午10:34:17
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findriskworkflowtype.f")
	public Map<String, Object> findRiskWorkFlowType(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("riskworkflow_type");
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
	 * 根据计划类型保存计划
	 * add by 王再冉
	 * 2014-1-2  下午1:27:01
	 * desc : 
	 * @param planForm	计划表单
	 * @param planType	计划类型
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/saveplanbyplantype.f")
	public Map<String, Object> savePlanByPlanType(PlanConformForm planForm) throws Exception{
		return o_planConformBO.savePlanByPlanType(planForm);
	}
	@ResponseBody
	@RequestMapping(value = "/access/planconform/saveplanbyplantypeNew.f")
	public Map<String, Object> savePlanByPlanTypeNew(PlanConformForm planForm) throws Exception{
		return o_planConformBO.savePlanByPlanTypeNew(planForm);
	}
	/**
	 * 查询计划整合列表
	 * add by 王再冉
	 * 2014-1-2  下午2:17:57
	 * desc : 
	 * @param start		开始页数
	 * @param limit		每页限制条数
	 * @param query		搜索框关键字
	 * @param sort		排序关键字
	 * @param status	处理状态
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findallplantypesgridpage.f")
	public Map<String, Object> findAllPlanTypesGridPage(int start, int limit, String query, String sort,String status,String schm){
		String property = "planCreatTime";//计划创建时间
		Page<RiskAssessPlan> page = new Page<RiskAssessPlan>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		String companyId = UserContext.getUser().getCompanyid();//当前登录者的公司id
		page = o_planConformBO.findAllPlanTypesGridPage(query, page, property, companyId,status,schm);
		List<RiskAssessPlan> entityList = page.getResult();
		List<RiskAssessPlan> datas = new ArrayList<RiskAssessPlan>();
		for(RiskAssessPlan plan : entityList){
			datas.add(new PlanConformForm(plan));
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 为不同planType查询计划列表     主要为风险评估、风险辨识、风险应对、应急预案
	 * @author Jzq
	 * @Time 2017年3月29日17:25:40
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @param status
	 * @param schm
	 * @param planType
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findallplantypesgridpageNew.f")
	public Map<String, Object> findAllPlanTypesGridPageNew(int start, int limit, String query, String sort,String status,String schm,String planType){
		String property = "planCreatTime";//计划创建时间
		Page<RiskAssessPlan> page = new Page<RiskAssessPlan>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		String companyId = UserContext.getUser().getCompanyid();//当前登录者的公司id
		page = o_planConformBO.findAllPlanTypesGridPage(query, page, property, companyId,status,schm,planType);
		List<RiskAssessPlan> entityList = page.getResult();
		List<RiskAssessPlan> datas = new ArrayList<RiskAssessPlan>();
		for(RiskAssessPlan plan : entityList){
			datas.add(new PlanConformForm(plan));
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	
	
	
	
	/**
	 * 修改计划，加载表单数据
	 * add by 王再冉
	 * 2014-1-2  下午3:14:55
	 * desc : 
	 * @param id	计划id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findplanformbyid.f")
	public Map<String, Object> findPlanFormById(String id) {
		return o_planConformBO.findPlanFormById(id);
	}
	
	/**
	 * 删除计划
	 * add by 王再冉
	 * 2014-1-2  下午5:26:44
	 * desc : 
	 * @param request
	 * @param ids	需要删除的计划ids
	 * @return 
	 * boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/removeplansbyids.f")
	public boolean removePlansByIds(HttpServletRequest request, String ids) {
		  if (StringUtils.isNotBlank(ids)) {
			  o_riskAssessPlanBO.deleteAllByPlanIds(ids);
			   return true;
		  } else {
			   return false;
		  }
	}
	/**
	 * 保存打分对象及打分部门
	 * add by 王再冉
	 * 2014-1-3  上午10:44:00
	 * desc : 
	 * @param riskIds	风险id
	 * @param planId	计划id
	 * @param typeId	流程类型
	 * @param deptId	当前部门id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/savescoreobjectrisksbyriskids.f")
	public Map<String, Object> saveScoreObjectRisksByRiskIds(String riskIds, String planId, String typeId, String deptId){
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean re = true;
		//dept：部门风险辨识 riskAeeseeDept：部门风险评估 riskResponse：风险应对
		if("riskResponse".equals(typeId)||"dept".equals(typeId)||"riskAssessDept".equals(typeId)){
			re = o_riskScoreBO.saveScoreObjectsAndScoreDepts(riskIds,planId,true, null);//只保存责任部门
		}else{
			re = o_riskScoreBO.saveScoreObjectsAndScoreDepts(riskIds,planId,false, deptId);
		}
		result.put("success", re);
		return result;
	}
	/**
	 * 查询风险事件选择列表
	 * add by 王再冉
	 * 2014-1-3  上午11:12:03
	 * desc : 
	 * @param query		搜索框关键字
	 * @param planId	计划id
	 * @return 
	 * List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findscoredeptsgridbyplanid.f")
	public List<Map<String,String>> findscoreDeptsGridByplanId(String query, String planId){
		return o_riskScoreBO.findscoreDeptAndEmpGridByPlanIdSQL(query,planId);
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findscoredeptsgridbyplanid_Dept.f")
	public List<Map<String,String>> findscoreDeptsGridByplanId_Dept(String query, String planId){
		return o_riskScoreBO.findscoreDeptAndEmpGridByPlanIdSQL_Dept(query,planId);
	}
	
	
	
	/**
	 * 查看风险明细列表
	 * add by 王再冉
	 * 2014-1-3  下午1:15:39
	 * desc : 
	 * @param query		搜索框关键字
	 * @param planId	计划id
	 * @param deptId	部门id，按部门查询
	 * @return 
	 * List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findrisksgridbyplanidordeptid.f")
	public List<Map<String,String>> findRisksGridByplanIdOrDeptId(String query, String planId, String deptId){
		Map<String,String> countMap = o_riskScoreBO.getOrgCountByPlanId(planId);
		List<Map<String,String>>listmap = o_riskScoreBO.findInfoByplanIdAndDeptId(query, planId, deptId,countMap);
		return listmap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findrisksgridbyplanidordeptidNew.f")
	public List<Map<String,String>> findRisksGridByplanIdOrDeptIdNew(String query, String planId, String deptId){
		Map<String,String> countMap = o_riskScoreBO.getOrgCountByPlanId(planId);
		List<Map<String,String>>listmap = o_riskScoreBO.findInfoByplanIdAndDeptId(query, planId, deptId,countMap);
		return listmap;
	}
	
	
	/**
	 * 删除打分对象和打分部门
	 * add by 王再冉
	 * 2014-1-3  下午4:17:40
	 * desc : 
	 * @param ids		打分部门id
	 * @param objIds	打分对象ids
	 * @return 
	 * boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/removeriskscoresbyidsandobjids.f")
	public boolean removeRiskScoresByIdsAndObjIds(String ids,String objIds) {
		 Boolean isDel = o_riskScoreBO.removeScoreDeptsAndScoreObjBydeptIdsAndPlanId(ids,objIds);
		 return isDel;
	}
	/**
	 * 按部门添加风险：添加部门下全部风险，风险应对只添加责任部门风险
	 * add by 王再冉
	 * 2014-1-3  下午4:28:09
	 * desc : 
	 * @param orgIds	部门id集合
	 * @param planId	计划id
	 * @param typeId	流程id类型
	 * @return 
	 * Map<String,Object>
	 */
	
	
	@ResponseBody
	@RequestMapping(value = "/access/planconform/savescoreobjectrisksbyorgids.f")
	public Map<String, Object> saveScoreObjectRisksByOrgIds(String orgIds, String planId, String typeId){
		Map<String, Object> result = new HashMap<String, Object>();
//		List<String> orgIdList = new ArrayList<String>();
//		if (StringUtils.isNotBlank(orgIds)) {
//			String[] idArray = orgIds.split(",");
//			for (String id : idArray) {
//				orgIdList.add(id);//添加的部门id集合
//			}
//		}
//		Boolean re = true;
//		if("riskResponse".equals(typeId)){//是风险应对流程
//			o_planConformBO.saveScoreObjectsAndScoreDeptsByOrgIds(orgIdList,planId,true);
//		}else{
			//o_planConformBO.saveScoreObjectsAndScoreDeptsByOrgIds(orgIdList,planId,false);
		o_riskIdentifyBO.saveScoreObjectsAndScoreDeptsByOrgIds(orgIds,planId,typeId);
//		}
		result.put("success", true);
		return result;
	}
	
	/**
	 * 开启工作流
	 * add by 王再冉
	 * 2014-1-6  下午5:08:16
	 * desc : 
	 * @param value		执行方法路径和方法名
	 * @param args		参数对象
	 * @param response 
	 * void
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = "/access/planconform/startprocessbyvalue.f")
	public void startProcessByValue(String value, String args, HttpServletResponse response){
		PrintWriter out = null;
		try {
			out = response.getWriter();
			JSONObject objs = JSONObject.fromObject(args);
			Object[] objectParam = new Object[objs.size()];
			Set se = objs.keySet();
			int i = 0;
			for(Object j : se){
				objectParam[i] = objs.get(j);
				i++;
			}
			String values[] = value.split(";");//将规则拆分
			Class<?> addressesClass = Class.forName(values[1].toString());//取类
			Object bean = SpringContextHolder.getBean(addressesClass);
			
			Class[] argsClass = new Class[objs.size()];
			for (int m = 0, j = objectParam.length; m < j; m++) {
				argsClass[m] = objectParam[m].getClass();
			}   
			
			Method method = addressesClass.getMethod(values[2].toString(),argsClass);//取类方法
			//郭鹏添加 判定方法返回结果   20170607
			Object t= method.invoke(bean,objectParam);
			if (((Boolean) t).booleanValue()) {
				out.write("true");
			}else
			{
				out.write("false");
			}
			
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
	 * 预览：基础信息查询
	 * add by 王再冉
	 * 2014-1-8  上午10:32:39
	 * desc : 
	 * @param id	计划id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findpreviewgridbyplanid.f")
	public Map<String,Object> findPreviewGridByplanId(String id){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(id);
		if(null != assessPlan){
			inmap.put("planName", assessPlan.getPlanName());//计划名称
			inmap.put("planCode", assessPlan.getPlanCode());//计划编号
			if(null != assessPlan.getTemplate()){
				inmap.put("templateName", assessPlan.getTemplate().getName());//模板名称
			}else{
				inmap.put("templateName", "");
			}
			inmap.put("rangeReq", assessPlan.getRangeReq());//范围
			inmap.put("workTage", assessPlan.getWorkTage());//工作目标
			//起止日期
			String beginDateStr = "";
			String endDateStr = "";
			if(null != assessPlan.getBeginDate()){
				beginDateStr = assessPlan.getBeginDate().toString().split(" ")[0];
			}
			if(null != assessPlan.getEndDate()){
				endDateStr = assessPlan.getEndDate().toString().split(" ")[0];
			}
			inmap.put("beginendDateStr", beginDateStr+" 至  "+endDateStr);
			inmap.put("contactName",null==assessPlan.getContactPerson()?"":assessPlan.getContactPerson().getEmpname());
			inmap.put("responsName",null==assessPlan.getResponsPerson()?"":assessPlan.getResponsPerson().getEmpname());
			map.put("success", true);
		}else{
			map.put("success", false);
		}
		map.put("data", inmap);
		return map;
	}
	/**
	 * 验证用户是否过期，过期用户不可用
	 * add by 王再冉
	 * 2014-6-10  下午2:38:04
	 * desc : 
	 * @param empIds
	 * @return 
	 * Boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/access/planconform/findempdatebyempids.f")
	public List<String> findEmpDateByEmpIds(String empIds, String approverId){
		Date nowDate = new Date();
		List<String> unUsedEmp = new ArrayList<String>();
		List<String> empIdList = new ArrayList<String>();
		String[] idArray = empIds.split(",");
		for (String id : idArray) {
			empIdList.add(id);
		}
		String approver = "";//审批人
		JSONArray jsonArray = JSONArray.fromObject(approverId);
		if (jsonArray.size() > 0){
			 JSONObject jsobj = jsonArray.getJSONObject(0);
			 approver = jsobj.getString("id");//审批人
			 empIdList.add(approver);
		}
		List<SysEmployee> emps = o_empGridBO.findEmpEntryListByEmpIds(empIdList);
		for(SysEmployee emp : emps){
			Date userExpData = emp.getSysUser().getCredentialsexpiryDate();
			if(null != userExpData){
				if(nowDate.after(userExpData)){
					unUsedEmp.add(emp.getEmpname());
				}
			}
		}
		return unUsedEmp;
	}

}
