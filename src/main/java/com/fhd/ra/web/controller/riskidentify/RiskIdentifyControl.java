package com.fhd.ra.web.controller.riskidentify;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.example.domain.Employee;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.kpiset.AssessTaskBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.quaassess.ShowAssessBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.risk.PotentialRiskEventBO;
import com.fhd.ra.business.riskidentify.RiskIdentifyBO;
import com.fhd.ra.web.form.riskidentify.RiskIdentifyForm;
import com.fhd.sys.business.auth.SysUserBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.business.orgstructure.SysEmpOrgBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 风险辨识control
 * @功能 : 
 * @author 王再冉
 * @date 2014-1-7
 * @since Ver
 * @copyRight FHD
 */
@Controller
public class RiskIdentifyControl {

	@Autowired
	private RiskIdentifyBO o_riskIdentifyBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private PotentialRiskEventBO o_potentialRiskEventBO;
	@Autowired
	private ShowRiskTidyBO o_showRiskTidyBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
	@Autowired
	private SysEmpOrgBO o_sysEmpOrgBO;
	@Autowired
	private ShowAssessBO o_showAssessBO;
	@Autowired
	private AssessTaskBO o_assessTaskBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private SysUserBO o_sysUserBO;
	/**
	 * 保存风险辨识
	 * add by 王再冉
	 * 2014-1-7  下午3:14:36
	 * desc : 
	 * @param planForm	计划表单实体
	 * @param id		辨识计划id
	 * @return
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/saveriskidentifyplan.f")
	public Map<String, Object> saveRiskIdentifyPlan(RiskIdentifyForm planForm, String id) throws Exception{
		return o_riskIdentifyBO.saveRiskIdentifyPlan(planForm,id);
	}
	
	/**
	 * 辨识计划提交
	 * add by 王再冉
	 * 2014-1-7  下午4:36:26
	 * desc : 
	 * @param empIds		承办人id集合
	 * @param approverId	审批人id
	 * @param businessId	计划id
	 * @param executionId	工作流程id
	 * @param deptEmpId		部门承办人对应id
	 * @param entityType 	流程类型
	 * @param response
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitriskidentifyplanbysome.f")
	public void submitRiskIdentifyPlanBySome(String empIds, String approverId, String businessId, String executionId, 
						String deptEmpId, HttpServletResponse response,String entityType) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitRiskIdentifyPlanBySome(empIds, approverId, businessId, executionId, entityType, deptEmpId);
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
	 * 辨识计划主管审批
	 * add by 王再冉
	 * 2014-1-8  下午1:30:00
	 * desc : 
	 * @param executionId	流程id
	 * @param businessId	计划id
	 * @param isPass		是否同意
	 * @param examineApproveIdea	审批意见
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitriskidentifyapprovalbysupervisor.f")
	public void submitRiskIdentifyApprovalBySupervisor(String executionId, String businessId, String isPass, String approverId,
														String examineApproveIdea, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String description = o_jbpmBO.findPDDByExecutionId(executionId);
			if("complex".equals(description)){//多级审批
				o_riskIdentifyBO.submitRiskIdentifyApprovalBySupervisor(executionId, businessId, isPass, examineApproveIdea,approverId);
			}else{//单级审批
				o_riskIdentifyBO.submitRiskIdentifyApprovalByLeader(executionId, businessId, isPass, examineApproveIdea);
			}
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
	 * 计划领导审批
	 * add by 王再冉
	 * 2014-1-8  下午3:31:58
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitriskidentifyapprovalbyleader.f")
	public void submitRiskIdentifyApprovalByLeader(String executionId, String businessId, String isPass, 
														String examineApproveIdea, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitRiskIdentifyApprovalByLeader(executionId, businessId, isPass, examineApproveIdea);
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
	 * 任务分配提交工作流
	 * add by 王再冉
	 * 2014-1-9  上午9:17:42
	 * desc : 
	 * @param executionId	流程id
	 * @param businessId	计划id
	 * @param pingguEmpIds	评估人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitriskidentifytask.f")
	public void submitRiskIdentifyTask(String executionId, String businessId, String pingguEmpIds, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitRiskIdentifyTask(executionId, businessId, pingguEmpIds);
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
	 * 查询所选员工是否拥有领导或普通员工角色
	 * 2017-06-08 pm 14:11
	 * desc : 
	 * @param empIds	   员工ID
	 * @param response 
	 * void
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/judgeEmps.f")
	public  Map<String,String> judgeEmps(String empIds, HttpServletResponse response) {
	String[] emp=empIds.split(",");
      List<SysEmployee> emps=o_employeeBO.findByIds(emp);
      Map<String,String> illegalUser=new HashMap<String, String>();
      String illegalName="";
      if (emps!=null) {
    	  String userIds=emps.get(0).getUserid();
          for (int i = 1; i < emps.size(); i++) {
        	  userIds=userIds+","+emps.get(i).getUserid();
    	}
          String[] userId=userIds.split(",");
         List<SysUser> userList=o_sysUserBO.findByIds(userId);
  
         for (int i = 0; i < userList.size(); i++) {
        	 Set<SysRole> sysRoles=userList.get(i).getSysRoles();
        	 int check=0;
        	 for(SysRole role:sysRoles)
        	 {
        		 if (role.getRoleCode().equals("Employee")||role.getRoleCode().equals("DeptLeader")) {
        			 check++ ;
        		 }
			}
      		 if(check==0){
        		 if (illegalName.equals("")) {
    				 illegalName=userList.get(i).getUsername();	
				}else
				{
					illegalName=illegalName+","+userList.get(i).getUsername();	
				}
        		}
			
		}
      }
      illegalUser.put("name", illegalName);
      return illegalUser;    
		
	}
	
	
	/**
	 * 风险辨识列表查询
	 * add by 王再冉
	 * 2014-2-17  上午10:08:31
	 * desc : 
	 * @param query			搜索框关键字
	 * @param assessPlanId	计划id
	 * @param executionId	流程id
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/findidentifygrid.f")
	public ArrayList<HashMap<String, Object>> findIdentifyGrid(String query, String assessPlanId, String executionId) {
		assessPlanId = this.getAssessPlanId(null, executionId);
		String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
		String companyid = UserContext.getUser().getCompanyid();
		assessPlanId = this.getFilterStr(assessPlanId);
		
		//风险事件GRID
		return o_showAssessBO.findRiskByScoreEmpId(query, scoreEmpId, o_showAssessBO.getTemplateId(assessPlanId), false, assessPlanId, companyid);
	}
	/**
	 * 风险辨识新增风险
	 * add by 王再冉
	 * 2014-1-9  下午1:31:33
	 * desc : 
	 * @param parentId		上级风险id
	 * @param riskId		风险id
	 * @param assessPlanId	计划id
	 * @param executionId	流程id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/savetaskbysome.f")
	public void saveTaskBySome(String parentId, String riskId, String assessPlanId, String executionId,
																String type, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.saveTaskBySome(parentId, riskId, assessPlanId, executionId, type);
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
	 * 风险辨识提交
	 * add by 王再冉
	 * 2014-1-9  下午2:41:08
	 * desc : 
	 * @param executionId	工作流程id
	 * @param assessPlanId	计划id
	 * @param response	
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/submitidentitiassess.f")
	public void submitIdentitiAssess(String executionId, String assessPlanId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitIdentitiAssess(executionId, assessPlanId);
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
	 * 辨识汇总审阅
	 * add by 王再冉
	 * 2014-1-10  上午10:06:19
	 * desc : 
	 * @param executionId	流程id
	 * @param approverId	审批人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/submitidentifycollect.f")
	public void submitIdentifyCollect(String executionId, String approverId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, Object> variables = new HashMap<String, Object>();
			String scoreEmpId = UserContext.getUser().getEmpid();// 员工ID
			String scoreOrgId = UserContext.getUser().getMajorDeptId();
			String approveOneId = "";
			if(StringUtils.isNotBlank(approverId)){
				JSONArray jsonArray = JSONArray.fromObject(approverId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 approveOneId = jsobj.getString("id");//审批人
				}
				variables.put("approveOne", approveOneId);//单位主管
				variables.put("scoreEmpId", scoreEmpId);
				variables.put("scoreOrgId", scoreOrgId);
			}
			o_jbpmBO.doProcessInstance(executionId, variables);
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
	 * 单位主管审批，业务副总审批
	 * add by 王再冉
	 * 2014-1-10  下午1:11:30
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitidentifyapproveone.f")
	public void submitIdentifyApproveOne(String executionId, String businessId, String isPass, 
									String examineApproveIdea, String approverId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitIdentifyApproveOne(executionId, businessId, isPass, examineApproveIdea,approverId);
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
	 * 单位领导审批
	 * add by 王再冉
	 * 2014-1-13  下午4:15:17
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	审批人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitidentifyapprovetwo.f")
	public void submitIdentifyApproveTwo(String executionId, String businessId, String isPass, 
									String examineApproveIdea, String approverId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitIdentifyApproveTwo(executionId, businessId, isPass, examineApproveIdea,approverId);
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
	 * 结果整理
	 * add by 王再冉
	 * 2014-1-13  下午4:31:57
	 * desc : 
	 * @param executionId	工作流程id
	 * @param approverId	审批人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/submitidentifytidy.f")
	public void submitIdentifyTidy(String executionId, String businessId, String approverId, String description, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			if("complex".equals(description)){
				Map<String, Object> variables = new HashMap<String, Object>();
				String approveForeId = "";
				if(StringUtils.isNotBlank(approverId)){
					JSONArray jsonArray = JSONArray.fromObject(approverId);
					if (jsonArray.size() > 0){
						 JSONObject jsobj = jsonArray.getJSONObject(0);
						 approveForeId = jsobj.getString("id");//审批人
					}
					variables.put("approveFore", approveForeId);//单位主管
				}
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				o_riskIdentifyBO.finishRiskIdentify(executionId,businessId);
			}
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
	 * 风险部门主管审批
	 * add by 王再冉
	 * 2014-1-13  下午4:51:52
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param approverId	审批人id
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitidentifyapprovefour.f")
	public void submitIdentifyApproveFour(String executionId, String businessId, String isPass, 
									String examineApproveIdea, String approverId, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitIdentifyApproveFour(executionId, businessId, isPass, examineApproveIdea,approverId);
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
	 * 风险部门领导审批，通过结束工作流
	 * add by 王再冉
	 * 2014-1-10  下午5:00:51
	 * desc : 
	 * @param executionId	工作流程id
	 * @param businessId	计划id
	 * @param isPass		审批是否通过
	 * @param examineApproveIdea 审批意见
	 * @param response 
	 * void
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/submitandcomplete.f")
	public void submitAndComplete(String executionId, String businessId, String isPass, 
														String examineApproveIdea, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			o_riskIdentifyBO.submitAndComplete(executionId, businessId, isPass, examineApproveIdea);
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
	 * 评估结果整理列表查询
	 * add by 王再冉
	 * 2014-1-11  下午1:52:49
	 * desc : 
	 * @param query			搜索框关键字
	 * @param assessPlanId	计划id
	 * @param typeId		树节点类型
	 * @param type			折叠树类型
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	@ResponseBody
	@RequestMapping(value="/access/riskidentify/findriskrebyrisk.f")
	public ArrayList<HashMap<String, String>> findRiskReByRisk(String query, String assessPlanId, String typeId, String type) {
		//风险事件GRID
		if(assessPlanId.indexOf(",") != -1){
			assessPlanId = assessPlanId.split(",")[0];
		}
		return o_riskIdentifyBO.findRiskReList(query,assessPlanId, typeId, type);
	}
	/**
	 * 评估结果整理，风险树查询
	 * add by 王再冉
	 * 2014-1-11  下午1:53:59
	 * desc : 
	 * @param node			树节点
	 * @param query			搜索框关键字
	 * @param ids			
	 * @param assessPlanId	计划id
	 * @return 
	 * List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/getrisktreerecordbyeventids.f")
    public List<Map<String, Object>> getRiskTreeRecordByEventIds(String node, String query,String ids, String assessPlanId){
		Map<String,String> riskMap = new HashMap<String,String>();
		ids =  o_showRiskTidyBO.findRiskReList(assessPlanId);
		String[] eventIds = ids.split(",");
    	return o_potentialRiskEventBO.getRiskIdentifyTidyTreeRecordByEventIds(node, query, eventIds, riskMap);
    }
	/**
	 * 导出风险整理列表
	 * add by 王再冉
	 * 2014-1-11  下午2:43:20
	 * desc : 
	 * @param businessId	计划id
	 * @param headerData	列名称
	 * @param type			类型
	 * @param typeId		类型id
	 * @param request
	 * @param response
	 * @throws Exception 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/exportidenntifytidygrid.f")
	public void exportIdentifyTidyGrid(String businessId, String headerData,
			String type, String typeId, HttpServletRequest request, HttpServletResponse response,String query) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		if("undefined".equals(type)){
			type = null;
		}
		if("undefined".equals(typeId)){
			typeId = null;
		}
		ArrayList<HashMap<String, String>> mapList = o_riskIdentifyBO.findRiskReList(null, businessId, typeId, type);
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		for(HashMap<String, String> map : mapList){
			objects = new Object[j];
			for(int m=0;m<indexList.size();m++){
				if(null!=map.get(indexList.get(m))){
					if("riskStatus".equals(indexList.get(m))){//状态
						if("icon-status-assess_new".equals(map.get(indexList.get(m)).toString())){
							objects[m] = "新增";
						}else if("icon-status-assess_edit".equals(map.get(indexList.get(m)).toString())){
							objects[m] = "修改";
						}else{
							objects[m] = "正常";
						}
					}else{
						objects[m] = map.get(indexList.get(m)).toString();
					}
				}
			}
			if(StringUtils.isNotBlank(query)){//查询结果导出
				for(int i=0;i<objects.length;i++){
					if(objects[i].toString().contains(query) && (!list.contains(objects))){
						list.add(objects);
					}
				}
			}else{
				list.add(objects);
			}
		}
		String exportFileName = "辨识结果预览数据.xls";
		String sheetName = "全面风险管理信息系统";
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	/**
	 * 风险部门主管和领导审批列表查询
	 * add by 王再冉
	 * 2014-1-11  下午3:00:54
	 * desc : 
	 * @param query			搜索框关键字
	 * @param businessId	计划id
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/findapprovetidygrid.f")
	public ArrayList<HashMap<String, String>> findApproveTidyGrid(String query,String businessId){
		return o_riskIdentifyBO.findApproveTidyGrid(query, businessId);
	}
	/**
	 * 查询当前工作流的描述
	 * add by 王再冉
	 * 2014-1-15  上午11:37:51
	 * desc : 
	 * @param executionId	工作流程id
	 * @return 
	 * String
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/findriskidentifydescription.f")
	public String findRiskIdentifyDescription(String executionId){
		return o_jbpmBO.findPDDByExecutionId(executionId);
	}
	/**
	 * 审批列表
	 * add by 王再冉
	 * 2014-2-11  下午3:13:14
	 * desc : 
	 * @param businessId	计划id
	 * @param query			搜索框关键字
	 * @param executionId	工作流程id
	 * @return 
	 * Map<String,Object>
	 */
	/**
	 * rebuild by 宋佳
	 * @param businessId
	 * @param executionId
	 * @param query
	 * @date 2017-7-25
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/findidentifylistbybusinessid.f")
	public ArrayList<HashMap<String, Object>> findIdentifyListByBusinessId(String businessId, String executionId, String query) {
		String scoreEmpId = (String) o_jbpmOperate.getVariableObj(executionId,"scoreEmpId");
		String scoreOrgId = (String) o_jbpmOperate.getVariableObj(executionId,"scoreOrgId");
		return o_riskIdentifyBO.findIdentifyListByBusinessId(businessId, scoreEmpId, scoreOrgId , query);
	}
	
	
	/**
	 * 辨识汇总
	 * @author 金鹏祥
	 */
	@ResponseBody
	@RequestMapping("findIdentifyByDept.f")
	public ArrayList<HashMap<String, Object>> findIdentifyByDept(String query, String businessId, String executionId) {
		String orgEmpId = UserContext.getUser().getEmpid();// 承办人ID(部门风险管理员)
		String orgId = "";
		try {
			orgId = o_sysEmpOrgBO.queryOrgByEmpid(orgEmpId).getId();// 部门领导ID
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o_rangObjectDeptEmpBO.findIdentifyByDept(query, businessId, orgId);
	}
	
	/**
	 * 得到业务ID
	 * */
	private String getAssessPlanId(String assessPlanId, String executionId){
		if(null == assessPlanId){
			return o_jbpmOperate.getVariable(executionId,"id");
		}
		return assessPlanId;
	}
	
	/**
	 * 过滤评估计划ID
	 * */
	private String getFilterStr(String str){
		if(str.indexOf(",") != -1){
			str = str.split(",")[0];
		}
		
		return str;
	}
	/**
	 * 风险辨识按部门添加风险
	 * add by 王再冉
	 * 2014-2-17  下午1:10:18
	 * desc : 
	 * @param orgIds	部门id
	 * @param planId	辨识计划id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/savescoreobjectsandscoredeptsbyorgids.f")
	public Map<String, Object> saveScoreObjectsAndScoreDeptsByOrgIds(String orgIds, String planId,String typeId){
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean re = o_riskIdentifyBO.saveScoreObjectsAndScoreDeptsByOrgIds(orgIds, planId,typeId);
		result.put("success", re);
		return result;
	}
	/**
	 * 任务分配风险列表查询
	 * add by 王再冉
	 * 2014-2-18  下午3:42:29
	 * desc : 
	 * @param businessId	计划id
	 * @param query			搜索框关键字
	 * @return 
	 * ArrayList<HashMap<String,Object>>
	 */
	@ResponseBody
	@RequestMapping("/access/riskidentify/findidentifyrisksbybusinessid.f")
	public ArrayList<HashMap<String, Object>> findIdentifyRisksByBusinessId(String businessId,String query) {
		return o_riskIdentifyBO.findIdentifyRisksByBusinessId(businessId,query);
	}
	/**
	 * 风险辨识保存辨识人
	 * add by 王再冉
	 * 2014-2-18  下午5:36:25
	 * desc : 
	 * @param businessId	计划id
	 * @param empIds 		辨识人id,可能有多个，用“，”隔开
	 * void
	 * 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/saveidentifyobjdeptempgridbysome.f")
	public void saveIdentifyObjDeptEmpGridBySome(String businessId, String empIds){
		String userEmpId = UserContext.getUser().getEmpid();
		List<String> orgIds = new ArrayList<String>();
		List<SysEmpOrg> empOrgs = o_riskScoreBO.findEmpDeptsByEmpId(userEmpId);
		for(SysEmpOrg emporg : empOrgs){
			orgIds.add(emporg.getSysOrganization().getId());
		}
		List<String> empIdList = new ArrayList<String>();
		if (StringUtils.isNotBlank(empIds)) {
			  String[] idArray = empIds.split(",");
			  for (String id : idArray) {
					  empIdList.add(id);
			  }
		}
		List<RangObjectDeptEmp> saveRodeList = new ArrayList<RangObjectDeptEmp>();//要保存的综合实体集合
		Map<String,Object> rtnMap = o_scoreDeptBO.findScoreDeptsByPlanId(businessId);//该计划的所有打分对象
		List<RiskOrgTemp> scoreDeptList = (List<RiskOrgTemp>) rtnMap.get("scoreDept");
		for(RiskOrgTemp riskOrgTemp : scoreDeptList){
			String orgId = riskOrgTemp.getSysOrganization().getId();
			String objectId = riskOrgTemp.getRiskScoreObject().getId();
			for(String empId : empIdList){
				//当前登录人的所属部门与t_rm_risk_org_temp表中id一致则保存T_RM_RANG_OBJECT_DEPT_EMP表     add by 吉志强
				//TODO 要在sql中过滤部门
				if(orgIds.contains(orgId)){
					RangObjectDeptEmp ode = new RangObjectDeptEmp();
					ode.setId(Identities.uuid());
					ode.setRiskOrgTemp(riskOrgTemp);
					ode.setScoreObject(new RiskScoreObject(objectId));
					ode.setScoreEmp(new SysEmployee(empId));
					saveRodeList.add(ode);
				}
			}
		}
		//批量保存综合表
		o_assessTaskBO.saveRangObjDeptEmpsSql(saveRodeList);
	}
	
	/**吉志强备份 原逻辑代码 风险辨识保存辨识人
	 * 2017年5月13日06:49:54
	 * @param businessId
	 * @param empIds
	 */
	public void saveIdentifyObjDeptEmpGridBySome_bak(String businessId, String empIds){
		String userEmpId = UserContext.getUser().getEmpid();
		List<String> orgIds = new ArrayList<String>();
		List<SysEmpOrg> empOrgs = o_riskScoreBO.findEmpDeptsByEmpId(userEmpId);
		for(SysEmpOrg emporg : empOrgs){
			orgIds.add(emporg.getSysOrganization().getId());
		}
		List<String> empIdList = new ArrayList<String>();
		if (StringUtils.isNotBlank(empIds)) {
			  String[] idArray = empIds.split(",");
			  for (String id : idArray) {
					  empIdList.add(id);
			  }
		}
		List<RangObjectDeptEmp> saveRodeList = new ArrayList<RangObjectDeptEmp>();//要保存的综合实体集合
		Map<String,Object> rtnMap = o_scoreDeptBO.findScoreDeptsByPlanId(businessId);//该计划的所有打分对象
		List<Map<String,Object>> scoreDeptList = (List<Map<String, Object>>) rtnMap.get("scoreDept");
		for(Map<String,Object> map : scoreDeptList){
			RiskOrgTemp scoreDept = (RiskOrgTemp)map;
			for(String empId : empIdList){
				if(orgIds.contains(scoreDept.getSysOrganization().getId())){
					RangObjectDeptEmp ode = new RangObjectDeptEmp();
					ode.setId(Identities.uuid());
					ode.setRiskOrgTemp(scoreDept);
					ode.setScoreObject(scoreDept.getRiskScoreObject());
					ode.setScoreEmp(new SysEmployee(empId));
					saveRodeList.add(ode);
				}
			}
		}
		//批量保存综合表
		o_assessTaskBO.saveRangObjDeptEmpsSql(saveRodeList);
	}
	
	
	/**
	 * 辨识审阅导出
	 * add by 王再冉
	 * 2014-2-19  上午11:37:08
	 */
	@ResponseBody
	@RequestMapping(value = "/access/riskidentify/exportleaderidentifygrid.f")
	public void exportLeaderIdentifyGrid(String businessId, String exportFileName, 
			String sheetName, String executionId, String headerData, String query, 
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		businessId = this.getAssessPlanId(businessId, executionId);
		@SuppressWarnings("unchecked")
		String scoreEmpId = (String) o_jbpmOperate.getVariableObj(executionId,"scoreEmpId");
		String scoreOrgId = (String) o_jbpmOperate.getVariableObj(executionId,"scoreOrgId");
		ArrayList<HashMap<String, Object>> mapList = o_riskIdentifyBO.findIdentifyListByBusinessId(businessId, scoreEmpId, scoreOrgId , query);
		
		JSONArray headerArray=JSONArray.fromObject(headerData);
		int j = headerArray.size();
		fieldTitle = new String[j];
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		
		for(HashMap<String, Object> map : mapList){
			objects = new Object[j];
			for(int m=0;m<indexList.size();m++){
				if(null!=map.get(indexList.get(m))){
					objects[m] = map.get(indexList.get(m)).toString();
				}
			}
			list.add(objects);
		}
		
		
		if(StringUtils.isBlank(exportFileName)){
			exportFileName = "辨识审批数据.xls";
		}else{
			exportFileName += ".xls";
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = "全面风险管理信息系统";
		}
		ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
}