/**
 * AssessRelaDefectControl.java
 * com.fhd.icm.web.controller.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-2-21 		黄晨曦
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.icm.web.controller.assess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.icm.assess.AssessPlanProcessRelaOrgEmp;
import com.fhd.entity.icm.assess.AssessPlanRelaProcess;
import com.fhd.entity.icm.assess.AssessRelaDefect;
import com.fhd.entity.icm.assess.AssessResult;
import com.fhd.entity.icm.assess.Assessor;
import com.fhd.entity.icm.defect.DefectRelaOrg;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessRelaOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.assess.AssessPlanRelaProcessBO;
import com.fhd.icm.business.assess.AssessRelaDefectBO;
import com.fhd.icm.business.assess.AssessResultBO;
import com.fhd.icm.business.bpm.AssessPlanBpmBO;
import com.fhd.icm.web.controller.bpm.AssessPlanBpmObject;
import com.fhd.sys.business.organization.EmployeeBO;

/**
 * @author   黄晨曦
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-2-21		上午11:05:23
 *
 * @see 	 
 */
@Controller
public class AssessRelaDefectControl {

	@Autowired
	private AssessRelaDefectBO o_assessRelaDefectBO;
	@Autowired
	private AssessPlanBpmBO o_assessPlanBpmBO;
	@Autowired
	private AssessPlanRelaProcessBO o_assessPlanRelaProcessBO;
	@Autowired
	private AssessResultBO o_assessResultBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	
	/**
	 * 评价计划相应流程对应的缺陷列表--计划执行第3步缺陷列表.
	 * @author 黄晨曦
	 * @modify 吴德福
	 * @param assessPlanId 评价计划id
	 * @param executionId 工作流id
	 * @param limit
	 * @param start
	 * @param sort
	 * @param dir
	 * @param query
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessRelaDefectListByPage.f")
	public Map<String,Object> findAssessRelaDefectListByPage(String assessPlanId, String executionId, int limit, int start, String sort, String dir, String query){
		Map<String,Object> map=new HashMap<String, Object>();
		List<Map<String,Object>> datas=new ArrayList<Map<String,Object>>();
		
		AssessPlanBpmObject assessPlanBpmObject = o_assessPlanBpmBO.findBpmObjectByExecutionId(executionId);
		String assessPlanRelaProcessId = assessPlanBpmObject.getAssessPlanRelaProcessId();
		if(StringUtils.isNotBlank(assessPlanRelaProcessId)){
			//根据评价计划参与流程id查询评价计划参与流程
			AssessPlanRelaProcess assessPlanRelaProcess = o_assessPlanRelaProcessBO.findAssessPlanRelaProcessById(assessPlanRelaProcessId);
			if(null != assessPlanRelaProcess){
				Process process = assessPlanRelaProcess.getProcess();
				
				if(null != process){
					Page<AssessRelaDefect> page = new Page<AssessRelaDefect>();
					page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
					page.setPageSize(limit);
					page = o_assessRelaDefectBO.findAssessRelaDefectListByPage(page, assessPlanId, process.getId(), query);
					
					List<AssessResult> assessResultList = o_assessResultBO.findAssessResultByAssessPlanIdAndProcessId(assessPlanId, "");
					
					List<AssessRelaDefect> assessRelaDefectList = page.getResult();
					for(AssessRelaDefect assessRelaDefect : assessRelaDefectList){
						Map<String,Object> data=new HashMap<String, Object>();
						data.put("assessRelaDefectId", assessRelaDefect.getId());
						if(null!=assessRelaDefect.getProcess()){
							data.put("processId", assessRelaDefect.getProcess().getId());
							data.put("processName", assessRelaDefect.getProcess().getName());//流程名称
							if(null!=assessRelaDefect.getProcess().getParent()){
								data.put("parentProcess", assessRelaDefect.getProcess().getParent().getName());//流程分类
							}
							
							String assessorId = "";
							String assessorName = "";
							//List<AssessResult> assessResultList = o_assessResultBO.findAssessResultByAssessPlanIdAndProcessId(assessPlanId, assessRelaDefect.getProcess().getId());
							for (AssessResult assessResult : assessResultList) {
								if(null != assessResult.getProcess() && null != assessRelaDefect.getProcess() && assessResult.getProcess().getId().equals(assessRelaDefect.getProcess().getId()) && null != assessResult.getAssessor()){
									Assessor assessor = assessResult.getAssessor();
									if(null != assessor){
										SysEmployee emp = assessor.getEmp();
										if(null != emp){
											assessorId = emp.getId();
											assessorName = emp.getEmpname()+"("+emp.getEmpcode()+")";
											break;
										}
									}
								}
							}
							//参评人id
							data.put("assessorId", assessorId);
							//参评人名称
							data.put("assessorName", assessorName);
							//影响程度和发生频率
							for (AssessResult assessResult : assessResultList) {
								if(null != assessResult.getProcess() && null != assessRelaDefect.getProcess() && assessResult.getProcess().getId().equals(assessRelaDefect.getProcess().getId())){
									if(null != assessResult.getAssessPoint() && null != assessRelaDefect.getAssessPoint() && assessResult.getAssessPoint().getId().equals(assessRelaDefect.getAssessPoint().getId())){
										data.put("impact", assessResult.getImpact());
										data.put("probability", assessResult.getProbability());
										break;
									}
								}
							}
						}
						if(null != assessRelaDefect.getAssessPoint()){
							data.put("pointName",assessRelaDefect.getAssessPoint().getDesc());//评价点
						}
						if(null != assessRelaDefect.getControlMeasure()){
							data.put("measureName", assessRelaDefect.getControlMeasure().getName());
						}
						if(null!=assessRelaDefect.getDefect()){
							data.put("defectId", assessRelaDefect.getDefect().getId());//缺陷Id
							data.put("desc", assessRelaDefect.getDefect().getDesc());//缺陷描述
							if(null!=assessRelaDefect.getDefect().getLevel()){
								data.put("level", assessRelaDefect.getDefect().getLevel().getId());//缺陷级别
							}
							if(null!=assessRelaDefect.getDefect().getType()){
								data.put("type", assessRelaDefect.getDefect().getType().getId());//缺陷类型
							}
							Set<DefectRelaOrg> defectRelaOrgs = assessRelaDefect.getDefect().getDefectRelaOrg();
							if(null!=defectRelaOrgs && defectRelaOrgs.size()>0){
								//缺陷关联部门
								for (DefectRelaOrg defectRelaOrg : defectRelaOrgs) {
									if(null != defectRelaOrg.getOrg()){
										data.put("orgId", defectRelaOrg.getOrg().getId());//责任部门名称
									}
								}
							}else{
								//流程关联部门
								Set<ProcessRelaOrg> processRelaOrgs = process.getProcessRelaOrg();
								for (ProcessRelaOrg processRelaOrg : processRelaOrgs) {
									SysOrganization org = processRelaOrg.getOrg();
									if(Contents.ORG_RESPONSIBILITY.equals(processRelaOrg.getType()) && null != org){
										data.put("orgId", org.getId());//责任部门名称
									}
								}
							}
						}
						if(null != assessRelaDefect.getIsAgree()){
							data.put("isAgree", assessRelaDefect.getIsAgree().getId());//是否同意
						}
						if(StringUtils.isNotBlank(assessRelaDefect.getFeedback())){
							data.put("feedback", assessRelaDefect.getFeedback());//反馈意见
						}
						datas.add(data);
					}
					
					map.put("datas",datas);
					map.put("totalCount",assessRelaDefectList.size());
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 评价计划相应流程对应的缺陷列表--缺陷汇总和修改列表.
	 * @author 吴德福
	 * @param assessPlanId 评价计划id
	 * @param executionId 工作流id
	 * @param limit
	 * @param start
	 * @param sort
	 * @param dir
	 * @param query
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessRelaDefectListByAssessPlanId.f")
	public Map<String,Object> findAssessRelaDefectListByAssessPlanId(String assessPlanId, String executionId, int limit, int start, String sort, String dir, String query){
		Map<String,Object> map=new HashMap<String, Object>();
		List<Map<String,Object>> datas=new ArrayList<Map<String,Object>>();
		
		Page<AssessRelaDefect> page = new Page<AssessRelaDefect>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_assessRelaDefectBO.findAssessRelaDefectListByPage(page, assessPlanId, "", query);
		List<AssessRelaDefect> assessRelaDefectList = page.getResult();
		for(AssessRelaDefect assessRelaDefect : assessRelaDefectList){
			Map<String,Object> data=new HashMap<String, Object>();
			data.put("assessRelaDefectId", assessRelaDefect.getId());
			
			if(null!=assessRelaDefect.getDefect()){
				data.put("defectId", assessRelaDefect.getDefect().getId());//缺陷Id
				data.put("desc", assessRelaDefect.getDefect().getDesc());//缺陷描述
				if(null!=assessRelaDefect.getDefect().getLevel()){
					data.put("level", assessRelaDefect.getDefect().getLevel().getId());//缺陷级别
				}
				if(null!=assessRelaDefect.getDefect().getType()){
					data.put("type", assessRelaDefect.getDefect().getType().getId());//缺陷类型
				}
				Set<DefectRelaOrg> defectRelaOrgs = assessRelaDefect.getDefect().getDefectRelaOrg();
				for (DefectRelaOrg defectRelaOrg : defectRelaOrgs) {
					SysOrganization org = defectRelaOrg.getOrg();
					if(Contents.ORG_RESPONSIBILITY.equals(defectRelaOrg.getType()) && null != org){
						//缺陷的责任部门id
						data.put("orgId", org.getId());//责任部门名称
					}
				}
			}
			
			if(null!=assessRelaDefect.getProcess()){
				data.put("processId", assessRelaDefect.getProcess().getId());
				data.put("processName", assessRelaDefect.getProcess().getName());//流程名称
				if(null!=assessRelaDefect.getProcess().getParent()){
					data.put("parentProcess", assessRelaDefect.getProcess().getParent().getName());//流程分类
				}
				
				String assessorId = "";
				String assessorName = "";
				List<AssessResult> assessResultList = o_assessResultBO.findAssessResultByAssessPlanIdAndProcessId(assessPlanId, assessRelaDefect.getProcess().getId());
				for (AssessResult assessResult : assessResultList) {
					if(null != assessResult.getAssessor()){
						Assessor assessor = assessResult.getAssessor();
						if(null != assessor){
							SysEmployee emp = assessor.getEmp();
							if(null != emp){
								assessorId = emp.getId();
								assessorName = emp.getEmpname()+"("+emp.getEmpcode()+")";
								break;
							}
						}
					}
				}
				//参评人id
				data.put("assessorId", assessorId);
				//参评人名称
				data.put("assessorName", assessorName);
			}
			if(null != assessRelaDefect.getProcessPoint()){
				data.put("pointName",assessRelaDefect.getProcessPoint().getName());//评价点
			}
			if(null != assessRelaDefect.getControlMeasure()){
				data.put("measureName", assessRelaDefect.getControlMeasure().getName());
			}
			if(null != assessRelaDefect.getIsAgree()){
				data.put("isAgree", assessRelaDefect.getIsAgree().getId());//是否同意
			}
			if(StringUtils.isNotBlank(assessRelaDefect.getFeedback())){
				data.put("feedback", assessRelaDefect.getFeedback());//反馈意见
			}
			
			datas.add(data);
		}
					
		map.put("datas",datas);
		map.put("totalCount",assessRelaDefectList.size());
		
		return map;
	}
	
	/**
	 * 评价计划相应流程对应的缺陷列表--缺陷反馈列表.
	 * @author 吴德福
	 * @param assessPlanId 评价计划id
	 * @param executionId 工作流id
	 * @param limit
	 * @param start
	 * @param sort
	 * @param query
	 * @param isOwnerOrg
	 * @return Map<String,Object>
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessRelaFeedbackDefectListByAssessPlanId.f")
	public Map<String,Object> findAssessRelaFeedbackDefectListByAssessPlanId(String assessPlanId, String executionId, int limit, int start, String sort, String query, String isOwnerOrg){
		Map<String,Object> map=new HashMap<String, Object>();
		List<Map<String,Object>> datas=new ArrayList<Map<String,Object>>();
			
		String deptId = "";
		String orgId = "";
		SysOrganization department = o_employeeBO.getDepartmentByEmpId(UserContext.getUser().getEmpid());
		if(null != department){
			//当前登录人所在的部门id
			deptId = department.getId();
		}
		
		List<AssessRelaDefect> defectRelaInfoList = o_assessRelaDefectBO.findAssessRelaDefectListByAssessPlanId(assessPlanId, "");
		List<AssessPlanProcessRelaOrgEmp> assessPlanProcessRelaOrgEmpList = o_assessPlanRelaProcessBO.findAssessPlanProcessRelaOrgEmpBySome(assessPlanId, true, false, null, null, null);
		List<AssessResult> assessResultList = o_assessResultBO.findAssessResultByAssessPlanIdAndProcessId(assessPlanId, "");
		
		for (AssessRelaDefect assessRelaDefect : defectRelaInfoList) {
			Map<String,Object> data=new HashMap<String, Object>();
			data.put("assessRelaDefectId", assessRelaDefect.getId());
			
			if(null!=assessRelaDefect.getDefect()){
				data.put("defectId", assessRelaDefect.getDefect().getId());//缺陷Id
				data.put("desc", assessRelaDefect.getDefect().getDesc());//缺陷描述
				if(null!=assessRelaDefect.getDefect().getLevel()){
					data.put("level", assessRelaDefect.getDefect().getLevel().getId());//缺陷级别
				}
				if(null!=assessRelaDefect.getDefect().getType()){
					data.put("type", assessRelaDefect.getDefect().getType().getId());//缺陷类型
				}
				Set<DefectRelaOrg> defectRelaOrgs = assessRelaDefect.getDefect().getDefectRelaOrg();
				for (DefectRelaOrg defectRelaOrg : defectRelaOrgs) {
					SysOrganization org = defectRelaOrg.getOrg();
					if(Contents.ORG_RESPONSIBILITY.equals(defectRelaOrg.getType()) && null != org){
						//缺陷的责任部门id
						orgId = org.getId();
						data.put("orgId", org.getId());//责任部门名称
					}
				}
				//是否存在风险
				if(null != assessRelaDefect.getDefect().getRisk() && Contents.DELETE_STATUS_USEFUL.equals(assessRelaDefect.getDefect().getRisk().getDeleteStatus())){
					data.put("riskId", assessRelaDefect.getDefect().getRisk().getId());
					data.put("riskName", assessRelaDefect.getDefect().getRisk().getName());
				}else{
					data.put("riskId", "");
					data.put("riskName", "");
				}
			}
			
			if(null!=assessRelaDefect.getProcess()){
				data.put("processId", assessRelaDefect.getProcess().getId());
				data.put("processName", assessRelaDefect.getProcess().getName());//流程名称
				if(null!=assessRelaDefect.getProcess().getParent()){
					data.put("parentProcess", assessRelaDefect.getProcess().getParent().getName());//流程分类
				}
				//List<AssessResult> assessResultList = o_assessResultBO.findAssessResultByAssessPlanIdAndProcessId(assessPlanId, assessRelaDefect.getProcess().getId());
				for (AssessResult assessResult : assessResultList) {
					if(null != assessResult.getAssessPoint() && null != assessRelaDefect.getAssessPoint() && assessResult.getAssessPoint().getId().equals(assessRelaDefect.getAssessPoint().getId())){
						data.put("impact", assessResult.getImpact());
						data.put("probability", assessResult.getProbability());
						break;
					}
				}
			}
			
			//流程节点
			if(null != assessRelaDefect.getProcessPoint()){
				data.put("processPointId", assessRelaDefect.getProcessPoint().getId());
				data.put("processPointName", assessRelaDefect.getProcessPoint().getName());
			}
			//控制措施
			if(null != assessRelaDefect.getControlMeasure()){
				data.put("measureId", assessRelaDefect.getControlMeasure().getId());
				data.put("measureName", assessRelaDefect.getControlMeasure().getName());
			}
			
			if("Y".equals(isOwnerOrg) && !deptId.equals(orgId)){
				//不是当前登录人所在部门的缺陷不显示
				continue;
			}
			
			if(null != assessRelaDefect.getAssessPoint()){
				data.put("pointName",assessRelaDefect.getAssessPoint().getDesc());//评价点
			}
			if(null != assessRelaDefect.getIsAgree()){
				data.put("isAgree", assessRelaDefect.getIsAgree().getId());//是否同意
			}
			if(StringUtils.isNotBlank(assessRelaDefect.getFeedback())){
				data.put("feedback", assessRelaDefect.getFeedback());//反馈意见
			}
			for (AssessPlanProcessRelaOrgEmp assessPlanProcessRelaOrgEmp : assessPlanProcessRelaOrgEmpList) {
				if(assessRelaDefect.getProcess().getId().equals(assessPlanProcessRelaOrgEmp.getAssessPlanRelaProcess().getProcess().getId())){
					if(Contents.EMP_HANDLER.equals(assessPlanProcessRelaOrgEmp.getType())){
						data.put("executeEmpName", assessPlanProcessRelaOrgEmp.getAssessPlanRelaOrgEmp().getEmp().getEmpname()+"("+assessPlanProcessRelaOrgEmp.getAssessPlanRelaOrgEmp().getEmp().getEmpcode()+")");
					}else if(Contents.EMP_REVIEW_PERSON.equals(assessPlanProcessRelaOrgEmp.getType())){
						data.put("reviewerEmpName", assessPlanProcessRelaOrgEmp.getAssessPlanRelaOrgEmp().getEmp().getEmpname()+"("+assessPlanProcessRelaOrgEmp.getAssessPlanRelaOrgEmp().getEmp().getEmpcode()+")");
					}
					AssessPlanRelaProcess assessPlanRelaProcess = assessPlanProcessRelaOrgEmp.getAssessPlanRelaProcess();
					if(null != assessPlanRelaProcess){
						if(null != assessPlanRelaProcess.getAssessDate()){
							data.put("assessDate", DateUtils.formatDate(assessPlanRelaProcess.getAssessDate(), "yyyy-MM-dd"));
						}
						if(null != assessPlanRelaProcess.getReviewDate()){
							data.put("reviewDate", DateUtils.formatDate(assessPlanRelaProcess.getReviewDate(), "yyyy-MM-dd"));
						}
					}
				}
			}
			//增加搜索过滤条件
			if(StringUtils.isNotBlank(query)){
				if(data.get("desc").toString().indexOf(query) != -1){
					datas.add(data);
				}
			}else{
				datas.add(data);
			}
		}
		
		map.put("datas",datas);
		map.put("totalCount",defectRelaInfoList.size());
		
		return map;
	}
	
	/**
	 * 缺陷反馈意见指保存.
	 * @param jsonString
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/mergeDefectFeedback.f")
	public Boolean mergeDefectFeedback(String jsonString){
		o_assessRelaDefectBO.mergeDefectFeedback(jsonString);
		return true;
	}
}

