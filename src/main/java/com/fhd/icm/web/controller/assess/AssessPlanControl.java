package com.fhd.icm.web.controller.assess;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.bpm.BusinessWorkFlow;
import com.fhd.entity.icm.assess.AssessFrequence;
import com.fhd.entity.icm.assess.AssessPlan;
import com.fhd.entity.icm.assess.AssessPlanRelaOrgEmp;
import com.fhd.entity.icm.assess.AssessRelaDefect;
import com.fhd.entity.icm.assess.AssessResult;
import com.fhd.entity.icm.assess.Assessor;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.assess.AssessPlanBO;
import com.fhd.icm.business.assess.AssessPlanRelaPointBO;
import com.fhd.icm.business.assess.AssessRelaDefectBO;
import com.fhd.icm.business.assess.AssessResultBO;
import com.fhd.icm.business.chart.AngularGauge;
import com.fhd.icm.business.chart.ChartColumn2D;
import com.fhd.icm.business.defect.DefectBO;
import com.fhd.icm.utils.DaysUtils;
import com.fhd.icm.web.form.AssessPlanForm;
import com.fhd.sys.business.organization.EmployeeBO;

/**
 * @author 刘中帅
 * @version
 * @since Ver 1.1
 * @Date 2013-1-6 下午6:39:05
 * 
 * @see
 */
@Controller
public class AssessPlanControl {

	@Autowired
	private AssessPlanBO o_assessPlanBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private AssessPlanRelaPointBO o_assessPlanRelaPointBO;
	@Autowired
	private AssessResultBO o_assessResultBO;
	@Autowired
	private DefectBO o_defectBO;
	@Autowired
	private AssessRelaDefectBO o_assessRelaDefectBO;
	@Autowired
	private EmployeeBO employeeBO;
	
	private Logger logger = Logger.getLogger(AssessPlanControl.class);
	/**
	 * 内控评价计划列表--保存功能.
	 * @author 刘中帅
	 * @param form  评价计划form
	 * @param result
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/mergeAssessPlanByForm.f")
	public Map<String, Object> mergeAssessPlanByForm(AssessPlanForm form, BindingResult result) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String assessPlanId = o_assessPlanBO.mergeAssessPlanByForm(form);
		ret.put("success", true);
		ret.put("assessPlanId", assessPlanId);
		return ret;
	}

	/**
	 * 内控评价计划列表--删除功能.
	 * @author 刘中帅
	 * @modify 吴德福 2013-3-11
	 * @param assessPlanIds
	 * @param response
	 * @throws IOException
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/removeAssessPlanByIds.f")
	public void removeAssessPlanByIds(String assessPlanIds, HttpServletResponse response) throws IOException {
		PrintWriter out = null;
		boolean flag = false;
		try {
			out = response.getWriter();
			
			if(StringUtils.isNotBlank(assessPlanIds)){
				List<String> idList = new ArrayList<String>();
				String[] idArray = assessPlanIds.split(",");
				for (String id : idArray) {
					if(!idList.contains(id)){
						idList.add(id);
					}
				}
				o_assessPlanBO.removeAssessPlanByIdList(idList);
				flag = true;
			}
			out.print(flag);
		} catch(Exception e){
			e.printStackTrace();
			out.print(flag);
		}finally{
			if(null != out){
				out.close();
			}
		}
	}

	/**
	 * 评价计划预览--所有字段为只读.
	 * @author 张 雷
	 * @param assessPlanId 评价计划ID
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlanForView.f")
	public Map<String, Object> findAssessPlanForView(String assessPlanId) {
		Map<String, Object> map = new HashMap<String, Object>();
		AssessPlan assessPlan = o_assessPlanBO.findAssessPlanById(assessPlanId);
		Map<String, Object> assessPlanMap = new HashMap<String, Object>();
		if(null!=assessPlan){
			assessPlanMap.put("companyName",assessPlan.getCompany().getOrgname());// 所属公司
			assessPlanMap.put("code", assessPlan.getCode());// 编号
			assessPlanMap.put("name", assessPlan.getName());// 计划名称
			// 评价方式id
			if(null != assessPlan.getAssessMeasure()){
				assessPlanMap.put("assessMeasureId", assessPlan.getAssessMeasure().getId());
				assessPlanMap.put("assessMeasureName", assessPlan.getAssessMeasure().getName());
			}else{
				assessPlanMap.put("assessMeasureId", "");
				assessPlanMap.put("assessMeasureName", "");
			}
			//评价类型
			if(null != assessPlan.getType()){
				assessPlanMap.put("typeId", assessPlan.getType().getId());// 评价类型id
				assessPlanMap.put("type", assessPlan.getType().getName());// 评价类型
			}else{
				assessPlanMap.put("typeId", "");
				assessPlanMap.put("type", "");
			}
			assessPlanMap.put("targetStartDate", DateUtils.formatDate(assessPlan.getTargetStartDate(), "yyyy-MM-dd"));// 评价期始
			assessPlanMap.put("targetEndDate", DateUtils.formatDate(assessPlan.getTargetEndDate(), "yyyy-MM-dd"));// 评价期止
			assessPlanMap.put("planStartDate", DateUtils.formatDate(assessPlan.getPlanStartDate(), "yyyy-MM-dd"));// 计划开始日期
			assessPlanMap.put("planEndDate", DateUtils.formatDate(assessPlan.getPlanEndDate(), "yyyy-MM-dd"));// 计划结束日期
			assessPlanMap.put("assessStandard", assessPlan.getAssessStandard());// 范围要求
			assessPlanMap.put("assessTarget", assessPlan.getAssessTarget());// 目标
			assessPlanMap.put("requirement", assessPlan.getRequirement());// 时间要求
			assessPlanMap.put("desc", assessPlan.getDesc());// 考核要求
			assessPlanMap.put("createBy", assessPlan.getCreateBy().getEmpname()+"("+assessPlan.getCreateBy().getEmpcode()+")");// 创建人
			assessPlanMap.put("createTime", DateUtils.formatDate(assessPlan.getCreateTime(), "yyyy-MM-dd"));// 创建日期
			assessPlanMap.put("dealStatus", assessPlan.getDealStatus());//处理状态
			List<String> responsibilityEmp = new ArrayList<String>();
			List<String> handlerEmp = new ArrayList<String>();
			Set<AssessPlanRelaOrgEmp> assessPlanRelaOrgEmpSet = assessPlan.getAssessPlanRelaOrgEmp();
			for (AssessPlanRelaOrgEmp assessPlanRelaOrgEmp : assessPlanRelaOrgEmpSet) {
				if (Contents.EMP_RESPONSIBILITY.equals(assessPlanRelaOrgEmp.getType()) && null != assessPlanRelaOrgEmp.getEmp()) {
					responsibilityEmp.add(assessPlanRelaOrgEmp.getEmp().getEmpname()+"("+assessPlanRelaOrgEmp.getEmp().getEmpcode()+")");
				} else if (Contents.EMP_HANDLER.equals(assessPlanRelaOrgEmp.getType()) && null != assessPlanRelaOrgEmp.getEmp()) {
					handlerEmp.add(assessPlanRelaOrgEmp.getEmp().getEmpname()+"("+assessPlanRelaOrgEmp.getEmp().getEmpcode()+")");
				}
			}
			assessPlanMap.put("responsibilityEmp",StringUtils.join(responsibilityEmp, "、"));// 组长
			assessPlanMap.put("handlerEmp", StringUtils.join(handlerEmp, "、"));// 组员
		}
		
		map.put("data", assessPlanMap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 根据评价计划id查询评价方式.
	 * @author 吴德福
	 * @param assessPlanId 评价计划ID
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlanMeasureByAssessPlanId.f")
	public Map<String, Object> findAssessPlanMeasureByAssessPlanId(String assessPlanId) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(assessPlanId)){
			AssessPlan assessPlan = o_assessPlanBO.findAssessPlanById(assessPlanId);
			if(null!=assessPlan){
				String assessMeasureId = "";
				if(null != assessPlan.getAssessMeasure()){
					// 评价方式id
					assessMeasureId = assessPlan.getAssessMeasure().getId();
				}
				map.put("assessMeasureId", assessMeasureId);
				o_assessPlanBO.mergeAssessPlanProcessureByAssessPlanId(assessPlan, assessMeasureId);
			}
			map.put("success", true);
		}else{
			map.put("success", false);
		}
		
		return map;
	}

	/**
	 * <pre>
	 * 选择所有的AssessPointFrequence（去重）
	 * </pre>
	 * 
	 * @author 刘中帅
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/assess/assessPlan/findAssessPointFrequences.f")
	public Map<String, Object> findAssessPointFrequences() {
		Map<String, Object> assessPlanMap = new HashMap<String, Object>();
		List<AssessFrequence> list = o_assessPlanBO.findAssessPointFrequences();
		StringBuffer code=new StringBuffer();
		StringBuffer name=new StringBuffer();
		if (!list.isEmpty()) {
			for(int i=0;i<list.size();i++){
				code.append(list.get(i).getCode());
				name.append(list.get(i).getName());
				if(i!=list.size()-1){
					code.append(",");
					name.append(",");
				}
			}
		}
		assessPlanMap.put("success", true);
		assessPlanMap.put("code", code.toString());
		assessPlanMap.put("name", name.toString());
		return assessPlanMap;
	}
	
	/**
	 * <pre>
	 *根据计划ID查找评价人列表
	 * </pre>
	 * 
	 * @author 黄晨曦
	 * @param assessPlanID 计划ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/assess/findEmpByAssessPlanId.f")
	public List<Map<String,Object>> findEmpByAssessPlanId(String assessPlanId){
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		List<AssessPlanRelaOrgEmp> assessPlanRelaOrgEmpList = o_assessPlanBO.findAssessPlanRelaOrgEmpBySome(assessPlanId,true,false, null);
		if(null != assessPlanRelaOrgEmpList && assessPlanRelaOrgEmpList.size()>0){
			Map<String, Object> data = null;
			for(AssessPlanRelaOrgEmp assessPlanRelaOrgEmp:assessPlanRelaOrgEmpList){
				if(null != assessPlanRelaOrgEmp.getEmp()){
					data = new HashMap<String, Object>();
					data.put("id", assessPlanRelaOrgEmp.getEmp().getId());
					data.put("name", assessPlanRelaOrgEmp.getEmp().getEmpname()+"("+assessPlanRelaOrgEmp.getEmp().getEmpcode()+")");
					list.add(data);
				}
			}
		}
		return list;
	}

	@ResponseBody
	@RequestMapping("icm/assess/findAssessPlanIdByExecutionId.f")
	public String findAssessPlanIdByExecutionId(String executionId){
		String[] newexecutionIds=executionId.split("\\.");
		String newexecutionId=newexecutionIds[0]+"."+newexecutionIds[1];
		String assessPlanId=null;
		List<BusinessWorkFlow> businessWorkFlowList=o_jbpmBO.findBusinessWorkFlowBySome(null, newexecutionId);
		if(null!=businessWorkFlowList&&businessWorkFlowList.size()>0){
			assessPlanId=businessWorkFlowList.get(0).getBusinessId();
		}
		return assessPlanId;
	} 
	
	/**
	 * 修改内控评价计划查询数据，初始化form.
	 * @author 刘中帅
	 * @param assessPlanId 内控评价计划Id
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlan.f")
	public Map<String, Object> findAssessPlan(String assessPlanId) {
		AssessPlan assessPlan=o_assessPlanBO.findAssessPlanByAssessPlanId(assessPlanId);
		Map<String, Object> map=new HashMap<String,Object>();
		Map<String, Object> formMap = new HashMap<String, Object>();
		formMap.put("id", assessPlan.getId());// id
		formMap.put("code", assessPlan.getCode());// 设置编码
		formMap.put("company", assessPlan.getCompany().getId());//所属公司
		formMap.put("name", assessPlan.getName());// 设置内控名称
		formMap.put("desc", assessPlan.getDesc());//考核要求
		formMap.put("requirement", assessPlan.getRequirement());//时间要求
		formMap.put("assessTarget", assessPlan.getAssessTarget());//目标
		formMap.put("targetEndDate", assessPlan.getTargetEndDate());//评价期始
		formMap.put("targetStartDate", assessPlan.getTargetStartDate());//评价期始
		formMap.put("planEndDate", assessPlan.getPlanEndDate());//计划期止
		formMap.put("assessStandard", assessPlan.getAssessStandard());//范围要求
		formMap.put("planStartDate", assessPlan.getPlanStartDate());//计划期始
		if(null != assessPlan.getType()){// 评价类型:自评、他评
			formMap.put("type", assessPlan.getType().getId());
		}
		if(null!=assessPlan.getAssessMeasure()){//评价方式：穿行、抽样、全部
			formMap.put("assessMeasure", assessPlan.getAssessMeasure().getId());
		}
		//查询相应的机构关联表，并组装成json串
		List<AssessPlanRelaOrgEmp> assessPlanListGroupLeader=o_assessPlanBO.findAssessPlanRelaOrgEmpBySome(assessPlanId,true,false,Contents.EMP_RESPONSIBILITY);	
		List<Map<String,Object>> groupLeaderJsonArray=new ArrayList<Map<String,Object>>();
		if(null != assessPlanListGroupLeader && assessPlanListGroupLeader.size()>0){
			//组长
			for(AssessPlanRelaOrgEmp emp:assessPlanListGroupLeader){
				Map<String,Object> groupLeaderMap=new HashMap<String,Object>();
				if(null != emp.getEmp()){
					groupLeaderMap.put("id", emp.getEmp().getId());
					groupLeaderMap.put("empno", emp.getEmp().getEmpcode());
					groupLeaderMap.put("empname", emp.getEmp().getEmpname());
				}
				groupLeaderJsonArray.add(groupLeaderMap);
			}
		}
		
		List<AssessPlanRelaOrgEmp> assessPlanListGroupPers=o_assessPlanBO.findAssessPlanRelaOrgEmpBySome(assessPlanId,true,false,Contents.EMP_HANDLER);
		List<Map<String,Object>> testEmpJsonArray=new ArrayList<Map<String,Object>>();
		
		if(null != assessPlanListGroupPers && assessPlanListGroupPers.size()>0){
			//组成员
			for(AssessPlanRelaOrgEmp pers:assessPlanListGroupPers){
				//Map<String,Object> groupPersMap=new HashMap<String,Object>();
				Map<String,Object> testEmpMap=new HashMap<String,Object>();
				
				if(null != pers.getEmp()){
					
					//人员
					testEmpMap.put("id", pers.getEmp().getId());
					testEmpMap.put("empno", pers.getEmp().getEmpcode());
					testEmpMap.put("empname", pers.getEmp().getEmpname());
					
					/*部门
					SysOrganization organization = employeeBO.getDepartmentByEmpId(pers.getEmp().getId());
					testEmpMap.put("id", organization.getId());
					testEmpMap.put("deptno", organization.getOrgcode());
					testEmpMap.put("deptname", organization.getOrgname());
					*/
					
					/*部门人员
					testEmpMap.put("empid", pers.getEmp().getId());
					testEmpMap.put("empno", pers.getEmp().getEmpcode());
					testEmpMap.put("empname", pers.getEmp().getEmpname());
					
					SysOrganization organization = employeeBO.getDepartmentByEmpId(pers.getEmp().getId());
					testEmpMap.put("deptid", organization.getId());
					testEmpMap.put("deptno", organization.getOrgcode());
					testEmpMap.put("deptname", organization.getOrgname());
					*/
				}
				testEmpJsonArray.add(testEmpMap);
			}
		}
		JSONArray jsonArrayGroupLeader=JSONArray.fromObject(groupLeaderJsonArray);
		formMap.put("groupLeaderId",jsonArrayGroupLeader.toString());//组长
		
		JSONArray jsonArrayGroupPers=JSONArray.fromObject(testEmpJsonArray);
		formMap.put("groupPersId",jsonArrayGroupPers.toString());//组成员
		
		map.put("data", formMap);
		map.put("success", true);
		return map;
	}
	
	

	/**
	 * 
	 * <pre>
	 *通过评价计划Id，和当前登录人的员工编号查询t_ca_assessment_assessor的Id
	 * </pre>
	 * 
	 * @author 刘中帅
	 * @param assessPlanId:评价计划Id
	 * @param empId:员工编号
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlanRelaOperatorIdByAssessPlanIdAndEmpId.f")
	public Map<String,Object> findAssessPlanRelaOperatorIdByAssessPlanIdAndEmpId(String assessPlanId,String empId){
		Map<String,Object> map= new HashMap<String,Object>();
		AssessPlan assessPlan=o_assessPlanBO.findAssessPlanByAssessPlanId(assessPlanId);
		String id="";
		if(null!=assessPlan){
			Set<Assessor> assessorList=assessPlan.getAssessor();
			for(Assessor assessor:assessorList){
				if(assessor.getEmp().getId().equals(empId)){
					id=assessor.getId();
				}
			}
		}
		map.put("id", id);
		return map;
	}
	/**
	 * 保存或修改内控评价计划时做数据验证.
	 * @author 刘中帅
	 * @param assessPlanId 评价计划id
	 * @param name 评价名称
	 * @param code  评价编码
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/validateAssessPlanForm.f")
	public Map<String, Object> validateAssessPlanForm(String assessPlanId, String name, String code) {

		String flagString = "notRepeat";
		// 修改数据时验证
		if (StringUtils.isNotEmpty(assessPlanId)) {
			List<AssessPlan> nameList = o_assessPlanBO.findAssessPlandBySome("",name);
			List<AssessPlan> codeList = o_assessPlanBO.findAssessPlandBySome(code, "");
			if (!nameList.isEmpty()) {
				if(nameList.size() == 1){
					if (!nameList.get(0).getId().equals(assessPlanId)) {
						flagString = "nameRepeat";
					}
				}else{
					flagString = "nameRepeat";
				}
			}
			if (!codeList.isEmpty()) {
				if(codeList.size() == 1){
					if (!codeList.get(0).getId().equals(assessPlanId)) {
						flagString = "codeRepeat";
					}
				}else{
					flagString = "codeRepeat";
				}
			}
		} else {// 添加数据时验证
			List<AssessPlan> nameList = o_assessPlanBO.findAssessPlandBySome("",name);
			List<AssessPlan> codeList = o_assessPlanBO.findAssessPlandBySome(code, "");
			if (!nameList.isEmpty()) {
				flagString = "nameRepeat";
			}
			if (!codeList.isEmpty()) {
				flagString = "codeRepeat";
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		result.put("flagStr", flagString);
		return result;
	}

	
	/**
	 * 内控评价维护创建内控评价编号.
	 * @author 刘中帅
	 * @return Map<String, Object> 内控评价计划编号
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/createAssessPlanCode.f")
	public Map<String, Object> createAssessPlanCode() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS"));
		resultMap.put("success", true);
		return resultMap;
	}
	/**
	 * 驾驶舱计划进度列表.
	 * @author 吴德福
	 * @param limit
	 * @param start
	 * @param sort
	 * @param query
	 * @param companyId
	 * @param dealStatus
	 * @return Map<String, Object>
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlanListByParams.f")
	public Map<String, Object> findAssessPlanListBySome(int limit, int start, String sort, String query, String companyId, String dealStatus) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		
		Page<AssessPlan> page = new Page<AssessPlan>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_assessPlanBO.findAssessPlanListBySome(page, sort, query, companyId, dealStatus);
		List<AssessPlan> assessPlanList=page.getResult();
		List<String> idList = new ArrayList<String>();
		for (AssessPlan assessPlan : assessPlanList) {
			idList.add(assessPlan.getId());
		}
		String businessObjectType="icmAssessPlan";
		List<BusinessWorkFlow> businessWorkFlows = o_jbpmBO.findBusinessWorkFlowBySome(null, null,businessObjectType,idList.toArray(new String[idList.size()]));
		Map<String,String> rateMap = new HashMap<String, String>();
		for (BusinessWorkFlow businessWorkFlow : businessWorkFlows) {
			String businessId = businessWorkFlow.getBusinessId();
			String rate = businessWorkFlow.getRate();
			rateMap.put(businessId, rate);
		}
		if (!assessPlanList.isEmpty()) {
			Map<String, Object> row = null;
			for (AssessPlan assessPlan : assessPlanList) {
				row = new HashMap<String, Object>();
				row.put("id", assessPlan.getId());
				//编号
				row.put("code", assessPlan.getCode());
				//名称
				row.put("name", assessPlan.getName());
				//计划进度
				if(null != assessPlan.getPlanStartDate() && null != assessPlan.getPlanEndDate()){
					row.put("schedule", DaysUtils.calculateRate(assessPlan.getPlanStartDate(), new Date(), assessPlan.getPlanEndDate()));
				}else{
					row.put("schedule", "");
				}
				
				//实际进度
				String rate = rateMap.get(assessPlan.getId());
				if(null==rate && (Contents.DEAL_STATUS_FINISHED.equals(assessPlan.getDealStatus()) || Contents.DEAL_STATUS_AFTER_DEADLINE.equals(assessPlan.getDealStatus()))){
					rate="100";
				}else if(null==rate){
					rate="0";
				}
				row.put("actualProgress", rate);
				// 评价类型
				if (null != assessPlan.getType()) {
					row.put("type", assessPlan.getType().getName());
				}else{
					row.put("type", "");
				}
				// 评价方式
				if (null != assessPlan.getAssessMeasure()) {
					row.put("assessMeasure", assessPlan.getAssessMeasure().getName());
				}else{
					row.put("assessMeasure", "");
				}
				//执行状态
				row.put("dealStatus", assessPlan.getDealStatus());
				//状态
				row.put("status", assessPlan.getStatus());
				//计划起止日期
				if(null != assessPlan.getPlanStartDate()){
					row.put("beginDate", DateUtils.formatDate(assessPlan.getPlanStartDate(), "yyyy-MM-dd"));
				}else{
					row.put("beginDate", "");
				}
				if(null != assessPlan.getPlanEndDate()){
					row.put("endDate", DateUtils.formatDate(assessPlan.getPlanEndDate(), "yyyy-MM-dd"));
				}else{
					row.put("endDate", "");
				}
				//样本起止时间
				if(null!=assessPlan.getTargetStartDate() && null!=assessPlan.getTargetEndDate()){
					row.put("planStartDate", DateUtils.formatDate(assessPlan.getTargetStartDate(),"yyyy-MM-dd"));
				}else{
					row.put("planStartDate", "");
				}
				if(null!=assessPlan.getTargetEndDate()){
					row.put("planEndDate", DateUtils.formatDate(assessPlan.getTargetEndDate(),"yyyy-MM-dd"));
				}else{
					row.put("planEndDate", "");
				}
				if(null != assessPlan.getCompany()){
					row.put("companyId", assessPlan.getCompany().getId());
					row.put("companyName", assessPlan.getCompany().getOrgname());
				}
				if(null != assessPlan.getCreateTime()){
					row.put("createTime", DateUtils.formatDate(assessPlan.getCreateTime(),"yyyy-MM-dd"));
				}else{
					row.put("createTime", "");
				}
				datas.add(row);
			}
			map.put("datas", datas);
			map.put("totalCount", page.getTotalItems());
		}else{
			map.put("datas", new Object[0]);
			map.put("totalCount", "0");
		}
		return map;
	}
	
	/**
	 * 根据评价计划id查询监控指标的xml.
	 * @param assessPlanId
	 * @return Map<String, Object>
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlanChartXmlByAssessPlanId.f")
	public Map<String, Object> findAssessPlanChartXmlByAssessPlanId(String assessPlanId) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		//计划完成率--评价结果已完成/评价结果总数
		int finishCount = 0;
		String finishRate = "0.00";
		//制度缺陷率--(1-穿行测试所有有效的评价结果/穿行评价结果总数)
		int systemDefectCount = 0;
		int qualifiedNumByPracticeTest = 0;
		int allSystemDefectCount = 0;
		String systemDefectRate = "0.00";
		String practiceTestQualifiedRate = "0.00";//有效率
		//执行疏失率--(1-抽样测试所有有效的评价结果/抽样评价结果总数)
		int performErrorCount = 0;
		int qualifiedNumBySampleTest = 0;
		int allPerformErrorCount = 0;
		String performErrorRate = "0.00";
		String sampleTestQualifiedRate = "0.00";//有效率
		
		List<AssessResult> assessResultList = o_assessResultBO.findAssessResultByAssessPlanIdAndProcessId(assessPlanId,"");
		List<AssessRelaDefect> assessRelaDefectList = o_assessRelaDefectBO.findDefectRelaInfoByAssessPlanIds(assessPlanId);
		for (AssessResult assessResult : assessResultList) {
			//结果不为空
			if(null != assessResult.getHasDefect()){
				if(null != assessResult.getHasDefectAdjust()){
					//调整结果不为空
					if(assessResult.getHasDefectAdjust()){
						//调整结果为'是'
						if(assessResult.getHasDefect() != assessResult.getHasDefectAdjust()){
							//调整结果与结果不一致，补充说明不为空
							if(StringUtils.isNotBlank(assessResult.getAdjustDesc())){
								finishCount++;
							}
						}
					}else{
						boolean flag = false;
						//调整结果为'否'
						for (AssessRelaDefect assessRelaDefect : assessRelaDefectList) {
							if(assessResult.getAssessPoint().getId().equals(assessRelaDefect.getAssessPoint().getId())){
								//调整结果为'否'，缺陷描述存在
								flag = true;
							}
						}
						if(assessResult.getHasDefect() != assessResult.getHasDefectAdjust()){
							//调整结果与结果不一致，补充说明不为空
							if(StringUtils.isNotBlank(assessResult.getAdjustDesc())){
								flag = true;
							}
						}
						if(flag){
							finishCount++;
						}
					}
				}else{
					//调整结果为空
					if(!assessResult.getHasDefect()){
						//结果为'否'
						for (AssessRelaDefect assessRelaDefect : assessRelaDefectList) {
							if(assessResult.getAssessPoint().getId().equals(assessRelaDefect.getAssessPoint().getId())){
								//调整结果为空，结果为否，缺陷描述存在
								finishCount++;
							}
						}
					}else if(assessResult.getHasDefect()){
						finishCount++;
					}
				}
			}
			
			//穿行测试评价点
			if(Contents.ASSESS_MEASURE_PRACTICE_TEST.equals(assessResult.getAssessMeasure().getId())){
				allSystemDefectCount++;
				/*
				 * 评价结果有效标准：
				 * 1.调整结果不为空，且为是，有效
				 * 2.调整结果为空，结果为是，有效
				 */
				if(null == assessResult.getHasDefectAdjust()){
					//调整结果为空
					if(null != assessResult.getHasDefect() && assessResult.getHasDefect()){
						//结果为是
						qualifiedNumByPracticeTest++;
					}else{
						systemDefectCount++;
					}
				}else{
					//调整结果不为空
					if(assessResult.getHasDefectAdjust()){
						qualifiedNumByPracticeTest++;
					}else{
						systemDefectCount++;
					}
				}
			}
			//抽样测试
			if(Contents.ASSESS_MEASURE_SAMPLE_TEST.equals(assessResult.getAssessMeasure().getId())){
				allPerformErrorCount++;
				/*
				 * 评价结果有效标准：
				 * 1.调整结果不为空，且为是，有效
				 * 2.调整结果为空，结果为是，有效
				 */
				if(null == assessResult.getHasDefectAdjust()){
					//调整结果为空
					if(null != assessResult.getHasDefect() && assessResult.getHasDefect()){
						//结果为是
						qualifiedNumBySampleTest++;
					}else{
						performErrorCount++;
					}
				}else{
					//调整结果不为空
					if(assessResult.getHasDefectAdjust()){
						qualifiedNumBySampleTest++;
					}else{
						performErrorCount++;
					}
				}
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		if(null != assessResultList && assessResultList.size()>0){
			finishRate = df.format(((double)finishCount/assessResultList.size())*100);
		}
		
		List<Object[]> defectLevelInfoList = new ArrayList<Object[]>();
		List<Object[]> orgDefectInfoList = new ArrayList<Object[]>();
		
		AssessPlan assessPlan = o_assessPlanBO.findAssessPlanById(assessPlanId);
		if(null != assessPlan){
			if(Contents.DEAL_STATUS_AFTER_DEADLINE.equals(assessPlan.getDealStatus()) || Contents.DEAL_STATUS_FINISHED.equals(assessPlan.getDealStatus())){
				//制度缺陷率
				if(allSystemDefectCount != 0){
					practiceTestQualifiedRate = df.format(((double)qualifiedNumByPracticeTest/allSystemDefectCount)*100);
					systemDefectRate = df.format(((double)systemDefectCount/allSystemDefectCount)*100);
				}
				//执行疏失率
				if(allPerformErrorCount != 0){
					sampleTestQualifiedRate = df.format(((double)qualifiedNumBySampleTest/allPerformErrorCount)*100);
					performErrorRate = df.format(((double)performErrorCount/allPerformErrorCount)*100);
				}
				//ENDO留存他用
				logger.info(practiceTestQualifiedRate+"\t"+systemDefectRate+"\t"+sampleTestQualifiedRate+"\t"+performErrorRate);
				//缺陷级别
				defectLevelInfoList = o_defectBO.findDefectRelaInfoByAssessPlanId(assessPlanId, "level");
				//部门缺陷
				orgDefectInfoList = o_defectBO.findDefectRelaInfoByAssessPlanId(assessPlanId, "org");
			}
		}
		
		map.put("finishRateXml", AngularGauge.getXml(finishRate, "","0"));
		map.put("finishRate", finishRate);
		map.put("systemDefectRateXml", AngularGauge.getXml(systemDefectRate, "","1"));
		map.put("systemDefectRate", systemDefectRate);
		map.put("performErrorRateXml", AngularGauge.getXml(performErrorRate, "","1"));
		map.put("performErrorRate", performErrorRate);
		map.put("defectLevelXml", ChartColumn2D.getXml("各个等级的缺陷数量统计图", "", "", "", defectLevelInfoList));
		map.put("orgDefectXml", ChartColumn2D.getXml("各个部门的缺陷数量统计图", "", "", "", orgDefectInfoList));
		return map;
	}
	
	/**
	 * 初始化驾驶舱根据评价计划id查询监控指标的xml.
	 * @author 吴德福
	 * @param companyId
	 * @return Map<String, Object>
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlanChartXmlByCompanyId.f")
	public Map<String, Object> findAssessPlanChartXmlByCompanyId(String companyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(StringUtils.isBlank(companyId)){
			companyId = UserContext.getUser().getCompanyid();
		}
		
		//计划完成率--评价结果已完成/评价结果总数
		int finishCount = 0;
		String finishRate = "0.00";
		//制度缺陷率--(1-穿行测试所有有效的评价结果/穿行评价结果总数)
		String systemDefectRate = "0.00";
		//执行疏失率--(1-抽样测试所有有效的评价结果/抽样评价结果总数)
		String performErrorRate = "0.00";
		
		List<AssessResult> assessResultList = o_assessResultBO.findAssessResultListByCompanyId(companyId);
		List<AssessRelaDefect> assessRelaDefectList = o_assessRelaDefectBO.findAssessRelaDefectListByCompanyId(companyId);
		for (AssessResult assessResult : assessResultList) {
			//结果不为空
			if(null != assessResult.getHasDefect()){
				if(null != assessResult.getHasDefectAdjust()){
					//调整结果不为空
					if(assessResult.getHasDefectAdjust()){
						//调整结果为'是'
						if(assessResult.getHasDefect() != assessResult.getHasDefectAdjust() && StringUtils.isNotBlank(assessResult.getAdjustDesc())){
							//调整结果与结果不一致，补充说明不为空
							finishCount++;
						}
					}else{
						boolean flag = false;
						//调整结果为'否'
						for (AssessRelaDefect assessRelaDefect : assessRelaDefectList) {
							if(assessResult.getAssessPoint().getId().equals(assessRelaDefect.getAssessPoint().getId())){
								//调整结果为空，结果为否，缺陷描述存在
								flag = true;
							}
						}
						if(assessResult.getHasDefect() != assessResult.getHasDefectAdjust() && StringUtils.isNotBlank(assessResult.getAdjustDesc())){
							//调整结果与结果不一致，补充说明不为空
							flag = true;
						}
						if(flag){
							finishCount++;
						}
					}
				}else{
					//调整结果为空
					if(!assessResult.getHasDefect()){
						boolean flag = false;
						//结果为'否'
						for (AssessRelaDefect assessRelaDefect : assessRelaDefectList) {
							if(assessResult.getAssessPoint().getId().equals(assessRelaDefect.getAssessPoint().getId())){
								//调整结果为空，结果为否，缺陷描述存在
								flag = true;
							}
						}
						if(flag){
							finishCount++;
						}
					}else if(assessResult.getHasDefect()){
						finishCount++;
					}
				}
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		if(null != assessResultList && assessResultList.size()>0){
			finishRate = df.format(((double)finishCount/assessResultList.size())*100);
		}
		
		List<Object[]> assessMeasureTestList = o_defectBO.findAssessMeasureTestListByCompanyId(companyId);
		for (Object[] objects : assessMeasureTestList) {
			//制度缺陷率
			if(null != objects[0] && Contents.ASSESS_MEASURE_PRACTICE_TEST.equals(objects[0])){
				systemDefectRate = df.format(Double.valueOf(String.valueOf(objects[1]))*100);
			}
			//执行疏失率
			if(null != objects[0] && Contents.ASSESS_MEASURE_SAMPLE_TEST.equals(objects[0])){
				performErrorRate = df.format(Double.valueOf(String.valueOf(objects[1]))*100);
			}
		}
		//缺陷级别
		List<Object[]> defectLevelInfoList = o_defectBO.findDefectLevelListByCompanyId(companyId,"assess");
		//部门缺陷
		List<Object[]> orgDefectInfoList = o_defectBO.findOrgDefectListByCompanyId(companyId,"assess");
		
		map.put("finishRateXml", AngularGauge.getXml(finishRate, "","0"));
		map.put("finishRate", finishRate);
		map.put("systemDefectRateXml", AngularGauge.getXml(systemDefectRate, "","1"));
		map.put("systemDefectRate", systemDefectRate);
		map.put("performErrorRateXml", AngularGauge.getXml(performErrorRate, "","1"));
		map.put("performErrorRate", performErrorRate);
		map.put("defectLevelXml", ChartColumn2D.getXml("各个等级的缺陷数量统计图", "", "", "", defectLevelInfoList));
		map.put("orgDefectXml", ChartColumn2D.getXml("各个部门的缺陷数量统计图", "", "", "", orgDefectInfoList));
		return map;
	}
	
	/**
	 * 内控评价计划任务审批时批量处理评价计划关联的评价点及对应的评价人和复核人数据
	 * @param assessPlanId
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/saveAssessPlanRelaDatasById.f")
	public Map<String, Object> saveAssessPlanRelaDatasById(String assessPlanId){
		if(StringUtils.isNotBlank(assessPlanId)){
			//根据评价计划ID批量生成评价关联的评价点数据
			o_assessPlanRelaPointBO.saveAssessPlanRelaPointBatch(assessPlanId);
			//根据评价流程的评价人和复核人，生成评价评价点的评价人和复核人数据
			o_assessPlanRelaPointBO.mergeAssessPlanPointRelaOrgEmpFromAssessPlanProcessRelaOrgEmpBatch(assessPlanId);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
		return resultMap;
	}
	/**
	 * 内控评价计划发布时批量添加参评人信息和评价结果数据
	 * @param assessPlanId
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/saveAssessorAndAssessResultById.f")
	public Map<String, Object> saveAssessorAndAssessResultBatch(String assessPlanId){
		if(StringUtils.isNotBlank(assessPlanId)){
			//根据评价计划ID批量添加参评人信息和评价结果数据
			o_assessResultBO.saveAssessorAndAssessResultBatch(assessPlanId);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
		return resultMap;
	}
}
