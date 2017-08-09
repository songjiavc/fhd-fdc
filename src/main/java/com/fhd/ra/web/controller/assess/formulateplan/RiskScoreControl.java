package com.fhd.ra.web.controller.assess.formulateplan;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskCircusee;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.CircuseeBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;
/**
 * 打分对象，打分部门control类
 * @author 王再冉
 *
 */
@Controller
public class RiskScoreControl {
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private OrgGridBO o_orgGridBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private CircuseeBO o_circuseeBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	/**
	 * 保存打分对象
	 * add by 王再冉
	 * 2014-3-7  下午1:28:21
	 * @param riskIds	风险id字符串
	 * @param planId	计划id
	 * @param isMain	true：只保存责任部门
	 * @param deptId	部门id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/savescoreobjectrisks.f")
	public Map<String, Object> saveScoreObjectRisks(String riskIds, String planId, Boolean isMain, String deptId){
		Map<String, Object> result = new HashMap<String, Object>();
		if (o_riskScoreBO.saveScoreObjectsAndScoreDepts(riskIds,planId,isMain, deptId)) {
			result.put("success", true);
		} else {
			result.put("success", false);
		}
		return result;
	}
	/**
	 * 删除打分对象，打分部门（单条）
	 * @param ids	打分对象id字符串
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/removeriskscorebyId.f")
	public boolean removeRiskScoreById(String id) {
		  List<String> ObjIdList = new ArrayList<String>();
		  List<String> deptIdList = new ArrayList<String>();
		  if (StringUtils.isNotBlank(id)) {
			  String[] idArray = id.split(",");
			  for (String oid : idArray) {
				  String[] objIdAnddeptId = oid.split(";");//用分号拼接的打分对象和打分部门id，第一个是打分对象id
				  int i = 0;
				  for(String objOrDeptId : objIdAnddeptId){
					  if(i==0){
						  ObjIdList.add(objOrDeptId);
						  i++;
					  }else{
						  deptIdList.add(objOrDeptId);
					  }
				  }
			  }
		  } 
		  o_riskScoreBO.removeRiskScoreDeptsByIds(deptIdList);//删除打分部门
		  for(String objId : ObjIdList){
			  List<RiskScoreDept> deptList = o_scoreDeptBO.findRiskDeptByObjId(objId);
			  if(null == deptList || deptList.size()== 0){//如果打分对象不存在打分部门，删除；否则不删
				  o_riskScoreBO.removeRiskScoreObjById(objId);
			  }
		  }
		  return true;
	}
	/**
	 * 批量删除打分对象，打分部门
	 * @param ids		打分部门id
	 * @param objIds	打分对象id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/removeriskscoresbyIds.f")
	public boolean removeRiskScoresByIds(String ids,String objIds) {
		 Boolean isDel = o_riskScoreBO.removeScoreDeptsAndScoreObjBydeptIdsAndPlanId(ids,objIds);
		 return isDel;
	}
	/**
	 * 查询承办人列表
	 * add by 王再冉
	 * 2014-3-7  下午1:32:59
	 * @param planId	计划id
	 * @return 
	 * List<Map<String,String>>
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/queryscoredeptandempgrid.f")
	public List<Map<String,String>> queryscoreDeptAndEmpGrid(String planId){
		return o_riskScoreBO.findscoreDeptAndEmpGridByPlanIdSQL(null, planId);
	}
	/**
	 * 查询打分对象的所有部门（去重）
	 * @param planId	计划id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/findscoredeptids.f")
	public Map<String, Object> findScoreDeptIds(String planId){
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> deptIdlist = o_scoreDeptBO.findScoreDeptIdsByPlanId(planId);
		map.put("deptIds", deptIdlist);
		return map;
	}
	/**
	 * 查询出计划所对应的的承办人，作为stroe中的数据
	 * @param deptIds	部门id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/findempsbydeptids.f")
	public List<Map<String,String>> findEmpsBydeptIds(String deptId){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<SysEmployee> empList = o_riskScoreBO.findEmpsBydeptId(deptId);//查询部门中所有员工
		for(SysEmployee emp : empList){
			Map<String,String> mapEmp = new HashMap<String,String>();
			mapEmp.put("id", emp.getId());
			mapEmp.put("name",emp.getEmpname());
			list.add(mapEmp);
		}
		return list;
	}
	
	/**
	 * 查询出计划所对应的的承办人，作为stroe中的数据
	 * @param deptIds	部门id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/findempsbydeptidsNew.f")
	public List<Map<String,String>> findEmpsBydeptIdsNew(String deptId,String roleCode){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SysEmployee sysEmployee = this.o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId, roleCode);
		if(sysEmployee !=null){
			Map<String,String> mapEmp = new HashMap<String,String>();
			mapEmp.put("id", sysEmployee.getId());
			mapEmp.put("name",sysEmployee.getEmpname());
			list.add(mapEmp);
		}
		return list;
	}

	/**
	 * 评估计划提交
	 * add by 王再冉
	 * @param empIds		承办人id
	 * @param approverId	审批人id
	 * @param businessId	计划id
	 * @param executionId	工作流执行id
	 * @param deptEmpId		部门对应审批人id（保存审批人表）
	 * @param response
	 * @param entityType 	工作流类型
	 * void
	 */
	@RequestMapping("/access/formulateplan/submitassessriskplan.f")
	public void submitAssessRiskPlan(String empIds, String approverId, String businessId, String executionId, 
						String deptEmpId, HttpServletResponse response,String entityType) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskScoreBO.submitAssessRiskPlanToApprover(empIds, approverId, businessId, executionId, entityType,deptEmpId);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	/**
	 * 评估计划审批
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param isPass		是否通过：yes/no
	 * @param examineApproveIdea	审批意见
	 * @param response
	 */
	@RequestMapping("/access/formulateplan/riskassessplanapproval.f")
	public void riskAssessPlanApproval(
			String executionId, String businessId, String isPass, String examineApproveIdea, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskScoreBO.mergeRiskAssessPlanApproval(executionId, businessId, isPass, examineApproveIdea);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 任务分配工作流
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param pingguEmpId	评估人id
	 * @param response
	 */
	@RequestMapping("/access/formulateplan/risktaskdistribute.f")
	public void riskTaskDistribute(String executionId, String businessId, String pingguEmpIds, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskScoreBO.mergeRiskTaskDistribute(executionId, businessId, pingguEmpIds);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 导出评估计划下的风险列表分页
	 * @author 吴德福
	 * @param planId		计划id
	 * @param exportFileName导出文件名称
	 * @param sheetName		sheet文件名
	 * @param deptId		部门id，null：导出计划下全部数据;不为空：导出计划下该部门所有数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/exportriskscoreobjspage.f")
	@RecordLog("导出计划下的风险列表")
	public void exportriskscoreobjspage(String planId, String exportFileName, String sheetName, String deptId,
			HttpServletRequest request, HttpServletResponse response, String query) throws Exception{
		List<String> scoreObjIdlist = new ArrayList<String>(); //打分对象id集合
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		List<RiskScoreObject> riskScoreObjectList = o_scoreObjectBO.findRiskScoreObjByplanId(planId);//查询计划下打分对象集合
		for(RiskScoreObject scoreObject : riskScoreObjectList){
			scoreObjIdlist.add(scoreObject.getId());
		}
		List<Map<String,Object>> scoreDeptlist = o_scoreDeptBO.findRiskDeptByObjIdList(scoreObjIdlist);//打分部门集合
		for(Map<String,Object> map : scoreDeptlist){
			objects = new Object[4];
			String etype = "";
			RiskScoreDept scoredept = (RiskScoreDept)map.get("scoredept");
			RiskScoreObject obj = scoredept.getScoreObject();//打分对象
			SysOrganization dept = scoredept.getOrganization();
			if(StringUtils.isNotBlank(deptId) && !"undefined".equals(deptId)){//只导出该部门的数据
				if(deptId.equals(dept.getId())){
					if("M".equals(scoredept.getOrgType())){
						etype = "责任部门";
					}else if("A".equals(scoredept.getOrgType())){
						etype = "相关部门";
					}
					objects[0] = null!=dept?dept.getOrgname():"";//部门
					objects[3] = etype;//责任类型
					if(null != obj.getRisk()){//上级风险
						objects[1] = null!=obj.getRisk().getParent()?obj.getRisk().getParent().getName():"";
					}else{
						objects[1] = "";
					}
					objects[2] = null!=obj.getRisk()?obj.getRisk().getName():"";//风险名称
					if(StringUtils.isNotBlank(query)){
						if(objects[0].toString().contains(query)||objects[1].toString().contains(query)
								||objects[2].toString().contains(query)){
							list.add(objects);
						}
					}else{
						list.add(objects);
					}
				}
			}else{
				if("M".equals(scoredept.getOrgType())){
					etype = "主责部门";
				}else if("A".equals(scoredept.getOrgType())){
					etype = "相关部门";
				}else{
					etype = "";
				}
				objects[0] = null!=dept?dept.getOrgname():"";//部门
				objects[3] = etype;//责任类型
				if(null != obj.getRisk()){//上级风险
					objects[1] = null!=obj.getRisk().getParent()?obj.getRisk().getParent().getName():"";
				}else{
					objects[1] = "";
				}
				objects[2] = null!=obj.getRisk()?obj.getRisk().getName():"";//风险名称
				if(StringUtils.isNotBlank(query)){
					if(objects[0].toString().contains(query)||objects[1].toString().contains(query)
							||objects[2].toString().contains(query)){
						list.add(objects);
					}
				}else{
					list.add(objects);
				}
			}
		}
		String[] fieldTitle = new String[4];
		fieldTitle[0] = "部门";
		fieldTitle[1] = "上级风险";
		fieldTitle[2] = "风险名称";
		fieldTitle[3] = "责任类型";
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "评估范围数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	
	/**
	 * 评估范围列表，显示部门及承办人信息
	 * @param planId	评估计划id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/queryscoredeptsandcbrgrid.f")
	public List<Map<String,String>> findScoreDeptsAndCbrGrid(String query, String planId){
		return o_riskScoreBO.findscoreDeptAndEmpGridByPlanIdSQL(query, planId);
	}
	/**
	 * 通过计划id和部门id查部门打分明细
	 * @param planId	计划id
	 * @param deptId	部门id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/findscoreobjectsinfobyplanidanddeptid.f")
	public List<Map<String,String>> findScoreObjectsInfoByplanIdAndDeptid(String query, String planId, String deptId){
		Map<String,String> countMap = o_riskScoreBO.getOrgCountByPlanId(planId);
		List<Map<String,String>>listmap = o_riskScoreBO.findInfoByplanIdAndDeptId(query, planId, deptId,countMap);
		return listmap;
	}

	/**
	 * 保存承办人
	 * @param modifyRecords	列表记录
	 * @param empId			人员id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/formulateplan/saveriskcircuseebysome.f")
	public void saveRiskCircuseeBySome(String modifyRecords, String empId){
		JSONArray jsonArray=JSONArray.fromObject(modifyRecords);
		JSONObject jsonObj = jsonArray.getJSONObject(0);
		String deptId = jsonObj.getString("id");//风险id
		String planId = jsonObj.getString("planId");//评估计划id
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
		SysEmployee taker = o_empGridBO.findEmpEntryByEmpId(empId);
		SysOrganization dept = o_orgGridBO.findOrganizationByOrgId(deptId);
		List<RiskCircusee> cirList = o_circuseeBO.findRiskCircuseeByplanIdAnddeptId(planId, deptId);
		if(cirList.size()>0){
			o_riskScoreBO.removeRiskCircusees(cirList);//批量删除原部门承办人
		}
		RiskCircusee circusee = new RiskCircusee();
		circusee.setId(Identities.uuid());
		circusee.setAssessPlan(assessPlan);
		circusee.setOrganization(dept);
		circusee.setUnderTaker(taker);
		o_riskScoreBO.saveRisCircusee(circusee);
}
	
}
