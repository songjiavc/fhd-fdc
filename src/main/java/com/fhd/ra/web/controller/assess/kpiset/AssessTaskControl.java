package com.fhd.ra.web.controller.assess.kpiset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.kpiset.AssessTaskBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.web.form.assess.formulateplan.RiskScoreObjectForm;
import com.fhd.sys.business.assess.WeightSetBO;
import com.fhd.sys.business.organization.EmpGridBO;
/**
 * 任务分配control类
 * @author 王再冉
 *
 */
@Controller
public class AssessTaskControl {
	@Autowired
	private AssessTaskBO o_assessTaskBO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private RiskBO o_riskBO;
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	
	
	/**
	 * 过滤评估计划ID
	 * @param assessPlanId	计划id
	 * @return 
	 * String
	 */
	private String getFilterAssessPlanId(String assessPlanId){
		if(assessPlanId.indexOf(",") != -1){
			assessPlanId = assessPlanId.split(",")[0];
		}
		return assessPlanId;
	}
	/**
	 * 任务分配列表查询
	 * @param start		开始页数
	 * @param limit		每页限制条数
	 * @param query		搜索关键字
	 * @param sort		排序关键字
	 * @param businessId计划id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/kpiSet/queryassesstaskspage.f")
	public Map<String, Object> queryAssessTasksPage(int start, int limit, String query, String sort, String businessId){
		String property = "assessPlan";
		String direction = "ASC";
		Page<RiskScoreObject> page = new Page<RiskScoreObject>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		String empId = UserContext.getUser().getEmpid();//当前登录者的员工id
		businessId = this.getFilterAssessPlanId(businessId);
		//获取打分对象idlist
		List<String> ObjIds = o_assessTaskBO.findUserDeptScoreObjIdsByplanIdAndempId(empId,businessId);
		page = o_assessTaskBO.findScoreObjsPageByPlanIdAndObjids(query, page, property, direction, ObjIds, businessId);
		List<RiskScoreObject> entityList = page.getResult();
		List<RiskScoreObjectForm> datas = o_assessTaskBO.queryAssessTasksPage(entityList,empId);
		if(StringUtils.isNotBlank(query)){
			List<RiskScoreObjectForm> newdatas = new ArrayList<RiskScoreObjectForm>();
			for(RiskScoreObjectForm rf : datas){
				String mainorgname = null!=rf.getMainOrgName()?rf.getMainOrgName():"";
				String relaorgname = null!=rf.getRelaOrgName()?rf.getRelaOrgName():"";
				if(rf.getRiskName().contains(query)||rf.getParentRiskName().contains(query)||
						mainorgname.contains(query)||relaorgname.contains(query)){
					newdatas.add(rf);
				}
			}
			datas = newdatas;
		}
		//按上级风险排序
		Collections.sort(datas, new Comparator<RiskScoreObjectForm>() {
			public int compare(RiskScoreObjectForm o1, RiskScoreObjectForm o2){
				return (null!=o1.getParentRiskName()?o1.getParentRiskName():"").compareTo(null!=o2.getParentRiskName()?o2.getParentRiskName():"");
			}
		});
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	/**
	 * 登录人所在主部门的人员下拉列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/kpiSet/findempsbyuserdeptId.f")
	public List<Map<String,String>> findEmpsByuserDeptId(){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String deptId = UserContext.getUser().getMajorDeptId();
		List<SysEmployee> empList = o_riskScoreBO.findAllEmpsBydeptId(deptId);
		for(SysEmployee emp : empList){
			Map<String,String> mapEmp = new HashMap<String,String>();
			mapEmp.put("id", emp.getId());
			mapEmp.put("name",emp.getEmpname());
			list.add(mapEmp);
		}
		return list;
	}
	
	/**
	 * 根据准则中设置权重的角色查询部门下的员工
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/kpiSet/findempsbyuserdeptidandroles.f")
	public List<Map<String,String>> findEmpsByuserDeptIdAndRoles(){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String deptId = UserContext.getUser().getMajorDeptId();//主部门
		List<String> roleCodes = o_weightSetBO.findRoleCodes();//准则权重中设置的角色编号
		 List<SysEmployee> empList = o_riskScoreBO.findAllEmpsBydeptId(deptId);
		for(SysEmployee emp : empList){
			if(null != emp){
				Set<SysRole> userRoles = emp.getSysUser().getSysRoles();
				Map<String,String> mapEmp = new HashMap<String,String>();
				mapEmp.put("id", emp.getId());
				mapEmp.put("name",emp.getEmpname());
				for(SysRole role : userRoles){
					if(roleCodes.contains(role.getRoleCode()) && !list.contains(mapEmp)){
						list.add(mapEmp);
					}
				}
			}
		}
		return list;
	}
	/**
	 * 按风险分配列表
	 * @param riskIds	风险id
	 * @param query		搜索关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/kpiSet/queryassesstaskbyriskid.f")
	public List<Map<String,Object>> queryAssessTaskByriskId(String riskIds, String query){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		SysEmployee deptRiskManager = new SysEmployee();//部门风险管理员
		String empId = UserContext.getUser().getEmpid();//当前登录者的员工id
		SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(empId);//查询人员主部门
		String deptId = "";
		List<String> roleCodes = o_weightSetBO.findRoleCodes();
		if(null != empOrg){
			deptId = empOrg.getSysOrganization().getId();//得到当前登录人员的部门id
			deptRiskManager  = o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId,"DeptRiskManager");
		}
		List<String> riskIdList = new ArrayList<String>();
		if (StringUtils.isNotBlank(riskIds)) {
			  String[] idArray = riskIds.split(",");
			  for (String id : idArray) {
				  if(!(riskIdList.contains(id))){
					  riskIdList.add(id);
				  }
			  }
		}
		List<Risk> riskList = new ArrayList<Risk>();
		List<String> parRiskList = new ArrayList<String>();
		for(String rId : riskIdList){
			Risk risk = o_riskBO.findRiskById(rId);
			riskList.add(risk);
		}
		for(Risk r : riskList){
			Map<String,Object> map = new HashMap<String,Object>();
			if(null != r.getParent()){//上级风险不为空
				if(!parRiskList.contains(r.getParent().getId())){
					parRiskList.add(r.getParent().getId());
					map.put("parRiskName", r.getParent().getName());
					map.put("riskId", r.getParent().getId());
					if(null != deptRiskManager && roleCodes.contains("DeptRiskManager")){
						map.put("empId", null!=deptRiskManager.getId()?deptRiskManager.getId():"");
					}else{
						map.put("empId", "");
					}
					if(StringUtils.isNotBlank(query)){
						if(r.getParent().getName().contains(query)){
							listmap.add(map);
						}
					}else{
						listmap.add(map);
					}
				}
			}else{
				if(!(riskIdList.contains(r.getId()))){//上级风险为空，且该条风险在riskIdList不存在
					map.put("parRiskName", r.getName());
					map.put("riskId", r.getId());
					if(null != deptRiskManager && roleCodes.contains("DeptRiskManager")){
						map.put("empId", null!=deptRiskManager.getId()?deptRiskManager.getId():"");
					}else{
						map.put("empId", "");
					}
					if(StringUtils.isNotBlank(query)){
						if(r.getName().contains(query)){
							listmap.add(map);
						}
					}else{
						listmap.add(map);
					}
				}
			}
		}
		return listmap;
	}
	/**
	 * 保存对象，部门，人员综合表(按风险分配)
	 * add by 王再冉
	 * 2014-3-6  下午2:34:17
	 * desc : 
	 * @param modifyRecords		风险分配列表对应数据
	 * @param planId			计划id
	 * @return 	
	 * Map<String,Object>		success
	 */
	@ResponseBody
	@RequestMapping(value = "/access/kpiSet/saveObjdeptempbysome.f")
	public Map<String, Object> saveObjDeptEmpBySome(String modifyRecords, String planId){
		Map<String, Object> map = new HashMap<String, Object>();
		List<RangObjectDeptEmp> saveRanges = new ArrayList<RangObjectDeptEmp>();//需要保存的综合实体
		String delIds = "";		//需要删除的综合实体id
		String deptId = UserContext.getUser().getMajorDeptId();
		Map<String, ArrayList<RangObjectDeptEmp>> rodeAllMap = 
						o_assessTaskBO.getRangObjectDeptEmpsIdByPlanIdAndOrgID(planId,deptId);
		JSONArray jsonArray=JSONArray.fromObject(modifyRecords);
		int j = jsonArray.size();
		for(int i=0;i<j;i++){
			List<String> empIdList = new ArrayList<String>();
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			String parRiakId = jsonObj.getString("riskId");		//上级风险id
			String empIds = jsonObj.getString("empId");		//评估人id,可能多个评估人
			if(empIds.contains("[")){	//多个评估人是json格式，需要转换
				JSONArray empArr = JSONArray.fromObject(empIds);
				int m = empArr.size();
				for(int n=0;n<m;n++){
				String empStr = empArr.get(n).toString();
					empIdList.add(empStr);
				}
			}else{
				empIdList.add(empIds);
			}
			ArrayList<RangObjectDeptEmp> rodeList = rodeAllMap.get(parRiakId);//根据上级风险id取出综合表数据
			ArrayList<String> sameSavedStr = new ArrayList<String>();//相同的打分对象和打分部门数据去重
			for(RangObjectDeptEmp rode : rodeList){
				delIds = rode.getId() + "," + delIds;
				if(!sameSavedStr.contains(rode.getScoreObject().getId()+"--"+rode.getRiskOrgTemp().getId())){
					sameSavedStr.add(rode.getScoreObject().getId()+"--"+rode.getRiskOrgTemp().getId());
					for(String empid : empIdList){
						RangObjectDeptEmp newrode = new RangObjectDeptEmp();
						newrode.setId(Identities.uuid());
						newrode.setScoreObject(rode.getScoreObject());
						newrode.setRiskOrgTemp(rode.getRiskOrgTemp());
						newrode.setScoreEmp(new SysEmployee(empid));
						saveRanges.add(newrode);
					}
				}
			}
		}
		o_assessTaskBO.removeObjectDeptEmpsByids(delIds);
		o_assessTaskBO.saveRangObjDeptEmpsSql(saveRanges);	//批量保存综合表
		map.put("success", true);
		return map;
	}
	/**
	 * 保存综合表（grid）
	 * add by 王再冉
	 * 2014-3-6  下午3:14:52
	 * desc : 
	 * @param modifyRecords	列表对应数据
	 * @param empIds		人员id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/access/kpiSet/saveobjdeptempgridbysome.f")
	public Map<String, Object> saveObjDeptEmpGridBySome(String modifyRecords, String empIds){
		Map<String, Object> map = new HashMap<String, Object>();
		List<RangObjectDeptEmp> saveRodeList = new ArrayList<RangObjectDeptEmp>();//要保存的综合实体集合
		ArrayList<String> sameSavedStr = new ArrayList<String>();//相同的打分对象和打分部门数据去重
		String delIds = "";
		JSONArray jsonArray=JSONArray.fromObject(modifyRecords);
		JSONObject jsonObj = jsonArray.getJSONObject(0);
		String riskId = jsonObj.getString("riskId");//风险id
		String planId = jsonObj.getString("planId");//评估计划id
		ArrayList<RangObjectDeptEmp> rangList = o_assessTaskBO.getRangObjectDeptEmpsIdByPlanIdAndRiskID(planId,riskId);
		for(RangObjectDeptEmp rode : rangList){
			delIds = rode.getId() + "," + delIds;
			if(!sameSavedStr.contains(rode.getScoreObject().getId()+"--"+rode.getRiskOrgTemp().getId())){
				sameSavedStr.add(rode.getScoreObject().getId()+"--"+rode.getRiskOrgTemp().getId());
				String[] idArray = empIds.split(",");
				for (String empid : idArray) {
					RangObjectDeptEmp newrode = new RangObjectDeptEmp();
					newrode.setId(Identities.uuid());
					newrode.setScoreObject(rode.getScoreObject());
					newrode.setRiskOrgTemp(rode.getRiskOrgTemp());
					newrode.setScoreEmp(new SysEmployee(empid));
					saveRodeList.add(newrode);
				}
			}
		}
		o_assessTaskBO.removeObjectDeptEmpsByids(delIds);
		o_assessTaskBO.saveRangObjDeptEmpsSql(saveRodeList);//批量保存综合表
		map.put("success", true);
		return map;
	}
	/**
	 * findIsSaveRolesByEmpIds: 根据人员id验证该员工角色是否已在准则中设置
	 * @param empIds	人员id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/access/kpiSet/findissaverolesbyempids.f")
	public Map<String,Object> findIsSaveRolesByEmpIds(String empIds){
		return o_assessTaskBO.findIsSaveRolesByEmpIds(empIds);
	}
}
