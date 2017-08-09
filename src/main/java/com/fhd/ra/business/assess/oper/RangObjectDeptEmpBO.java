package com.fhd.ra.business.assess.oper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.assess.kpiSet.RangObjectDeptEmpDAO;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.NumberUtil;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.approval.ApplyContainer;
import com.fhd.ra.business.assess.approval.AssessDateInitBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.util.DynamicDim;
import com.fhd.ra.business.assess.util.MoreRoleDeptSummarizing;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.RiskStatusBO;
import com.fhd.sm.business.KpiBO;

import edu.emory.mathcs.backport.java.util.Collections;

@Service
public class RangObjectDeptEmpBO {

	@Autowired
	private RangObjectDeptEmpDAO o_rangObjectDeptEmpDAO;
	
	@Autowired
	private RiskStatusBO o_riskStatusBO;
	
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private AssessDateInitBO o_assessDateInitBO;
	
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	
	/**
	 * 查询领导部门下的同部门所有评估人(过滤risk汇总LIST) 部门总集合
	 * @param assessPlanId 评估计划ID
	 * @param applyContainer 组织数据实体
	 * @return HashMap<String, ArrayList<HashMap<String, Object>>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings({ "unchecked"})
	public HashMap<String, ArrayList<HashMap<String, Object>>> findAssessByDeptFiltrate(String assessPlanId, ApplyContainer applyContainer){
		StringBuffer sql = new StringBuffer();
		ArrayList<String> deptRisksList = null; //同部门打分人员ID集合
		ArrayList<String> deptRisksTempList = null; //过滤掉没打分的人员集合
 		HashMap<String, ArrayList<HashMap<String, Object>>> maps = new HashMap<String, ArrayList<HashMap<String,Object>>>(); //数据信息键值
		ArrayList<String> dimList = null; //动态维度集合
    	HashMap<String, String> filtrateMap = new HashMap<String, String>(); //记录同部门风险是否已执行
    	
		sql.append(" select a.id,a.score_emp_id,c.id  risk_id,e.org_id,c.risk_name,d.risk_name  parent_name  ");
		sql.append(",(select id from t_dim_template where id = " +
				"(select temp_id from t_rm_risk_assess_plan where id=:assessPlanId))  templateId");
		sql.append(" from t_rm_rang_object_dept_emp  a, ");
		sql.append(" (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) b, ");
		sql.append(" t_rm_risks c, ");
		sql.append(" t_rm_risks d, ");
		sql.append(" t_rm_risk_score_dept e ");
		sql.append(" where a.score_object_id = b.id ");
		sql.append(" and b.risk_id = c.id ");
		sql.append(" and c.parent_id=d.id ");
		sql.append(" and a.score_dept_id = e.id ");
		sql.append(" ORDER BY c.risk_name ");
		
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
	        String riskId = "";//风险ID
	        String deptId = "";//ID
	        String riskName = "";//部门ID
	        String parentRiskName = "";//上级风险名称
	        String templateId = ""; //模板ID
	        String tempId = ""; //模板ID
	        
	        if(null != objects[0]){
	        	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[3]){
            	deptId = objects[3].toString();
            }if(null != objects[4]){
            	riskName = objects[4].toString();
            }if(null != objects[5]){
            	parentRiskName = objects[5].toString();
            }if(null != objects[6]){
            	templateId = objects[6].toString();
            }
            
            if(templateId.equalsIgnoreCase("dim_template_type_custom")){
            	//自定义模板
            	if(applyContainer.getRiskAllMap().get(riskId).getTemplate() != null){
            		tempId = applyContainer.getRiskAllMap().get(riskId).getTemplate().getId();
            	}
            }else{
            	//其他模板
            	tempId = templateId;
            }
            
            if(filtrateMap.get(riskId + "--" + deptId) != null){
            	//同部门风险执行完毕后进行下一次遍历
            	continue;
            }
            
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("parentRiskName", parentRiskName);
            map.put("templateId", tempId);
            map.put("deptId", deptId);
            
        	deptRisksList = applyContainer.getDeptByRiskAllMap().get(riskId + "--" + deptId);
        	deptRisksTempList = new ArrayList<String>();
        	for (String rangIdAndScore : deptRisksList) {
				String strs[] = rangIdAndScore.split("--");
				if(strs.length > 0){
					if(strs.length > 1){
						if(null != applyContainer.getScoreResultByrangObjectDeptEmpIdAllMap().get(strs[0])){
							deptRisksTempList.add(rangIdAndScore);
						}
					}
				}
			}
        	
        	if(deptRisksTempList.size() > 1){
        		//多人评估
        		HashMap<String, Double> dimValueMap = 
        				MoreRoleDeptSummarizing.getDynamicDimValueMap("role", riskId, tempId, deptRisksTempList, applyContainer);
        		Set<Entry<String, Double>> key = dimValueMap.entrySet();
                for (Iterator<Entry<String, Double>> it = key.iterator(); it.hasNext();) {
                    Entry<String, Double> keys = it.next();
                    double dimValue = dimValueMap.get(keys.getKey());
                    map.put(keys.getKey(), dimValue);
                }
                
                filtrateMap.put(riskId + "--" + deptId, riskId);
        	}else{
        		//一人评估
        		dimList = DynamicDim.getDimValue(riskId, templateId, rangObjectDeptEmpId, applyContainer);
        		if(dimList != null){
	            	for (String obj : dimList) {
	            		map.put(obj.split("--")[0], NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()), 2));
	    			}
        		}
        	}
        	
        	if(maps.get(deptId) != null){
        		maps.get(deptId).add(map);
        	}else{
        		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
        		mapsList.add(map);
        		maps.put(deptId, mapsList);
        	}
        }
        
        return maps;
	}
	
	/**
	 * 查询领导部门下的同部门所有辨识人
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	@RecordLog("查询辨识汇总")
	public ArrayList<HashMap<String, Object>> findIdentifyByDept(String query, String assessPlanId, String orgId) {
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>();
    	String orgDeptEmpId = UserContext.getUser().getEmpid();// 部门风险管理员EMPID
		sql.append(" select DISTINCT a.id,f.emp_name,b.RISK_ID risk_id,e.org_id,b.risk_name,d.risk_name  parent_name, ");
		sql.append(" a.score_object_id,h.id  hId, z.EDIT_IDEA_CONTENT, h.EDIT_IDEA_CONTENT  Hedit,r.EDIT_IDEA_CONTENT Hresponse, q.EDIT_IDEA_CONTENT response from  ");
		sql.append(" t_rm_rang_object_dept_emp  a  ");
		sql.append(" LEFT JOIN t_rm_risk_score_object b on a.score_object_id = b.id ");
		sql.append(" LEFT JOIN t_rm_risks d on b.parent_id=d.id  ");
		sql.append(" LEFT JOIN t_rm_risk_org_temp e on a.score_object_id = e.object_id ");
		sql.append(" LEFT JOIN t_sys_employee f on f.id = a.score_emp_id  ");
		sql.append(" LEFT JOIN t_rm_dept_lead_circusee g on g.score_object_id = a.score_object_id and g.dept_lead_emp_id=:orgDeptEmpId ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_idea h on h.dept_lead_circusee_id = g.id ");
		sql.append(" LEFT JOIN t_rm_edit_idea z on z.OBJECT_DEPT_EMP_ID = a.id ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_response r ON r.dept_lead_circusee_id = g.id");
		sql.append(" LEFT JOIN t_rm_edit_response q ON q.OBJECT_DEPT_EMP_ID = a.id");
		sql.append(" where b.assess_plan_id=:assessPlanId and b.risk_id is not null ");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (c.risk_name like :query or d.risk_name like :query or f.emp_name like :query) ");
		}
		sql.append(" and e.org_id in (:orgId) and f.id = a.score_emp_id AND e.id = a.SCORE_DEPT_ID ORDER BY b.risk_name ");
		System.out.println("&&&&&&&&&&&&&&&辨识汇总 列表sql：="+sql.toString());
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
		if(StringUtils.isNotBlank(query)){
			sqlQuery.setParameter("query", "%"+query+"%");
		}
		sqlQuery.setParameter("orgDeptEmpId", orgDeptEmpId);
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<String> orgIdList = o_riskScoreBO.findAllDeptIdsBydeptId(orgId);
		orgIdList.add(orgId);
		sqlQuery.setParameterList("orgId", orgIdList);
		List<Object[]> list = sqlQuery.list();
		String tempStr = "";
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
	        String assessEmpId = "";//打分人员ID
	        String riskId = "";//风险ID
	        String riskName = "";//部门ID
	        String parentRiskName = "";//上级风险名称
	        String objectScoreId = "";
	        String hId = "";
	        String empEditIdea = "";
	        String hEditIdeaContent = "";
	        String hResponse = null;
	        String response = null;
	        if(null != objects[0]){
	        	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	assessEmpId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[4]){
            	riskName = objects[4].toString();
            }if(null != objects[5]){
            	parentRiskName = objects[5].toString();
            }if(null != objects[6]){
            	objectScoreId = objects[6].toString();
            }if(null != objects[7]){
            	hId = objects[7].toString();
            }if(null != objects[8]){
            	empEditIdea = objects[8].toString();
            }if(null != objects[9]){
            	hEditIdeaContent = objects[9].toString();
            }if(null != objects[10]){
            	hResponse = objects[10].toString();
            }if(null != objects[11]){
            	response = objects[11].toString();
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
            map.put("assessEmpId", assessEmpId);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("parentRiskName", parentRiskName);
            map.put("empEditIdea", empEditIdea);
            map.put("hResponse",hResponse);
            map.put("response", response);
            if("".equalsIgnoreCase(hEditIdeaContent)){
            	tempStr += "\t";
            	map.put("hEditIdeaContent", tempStr);
            }else{
            	 map.put("hEditIdeaContent", hEditIdeaContent);
            }
        	map.put("objectScoreId", objectScoreId);
        	map.put("hId", hId);
        	mapsList.add(map);
        }
        return mapsList;
	}
	
	/**
	 * 查询领导部门下的同部门所有评估人
	 * @param String query 条件查询
	 * @param assessPlanId 评估计划ID
	 * @param orgId 部门ID
	 * @param companyId 公司ID
	 * @param scoreEmpIdList 人员ID集合
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	@RecordLog("查询风险评价审阅")
	public ArrayList<HashMap<String, Object>> findAssessByDept(String query,String assessPlanId, 
			List<String> orgIdList,String companyId, ArrayList<String> scoreEmpIdList) {
		StringBuffer sql = new StringBuffer();
		ArrayList<String> deptRisksList = null; //风险打分人员集合
		ArrayList<HashMap<String, Object>> mapsList = new ArrayList<HashMap<String, Object>>(); //数据信息键值
		ArrayList<String> dimList = null; //动态维度集合
		String orgDeptEmpId = UserContext.getUser().getEmpid();// 部门风险管理员EMPID
		ArrayList<ArrayList<String>> deptByRiskAllList = this.finAssessByDeptRiskAllList(assessPlanId, orgIdList);
		HashMap<String, String> isSummarizingRiskIconMap = new HashMap<String, String>(); //部门风险汇总状态灯
		ApplyContainer applyContainer = o_assessDateInitBO.getLeadPreviewDateInit(companyId, assessPlanId, scoreEmpIdList);
		
		sql.append(" select a.id,f.emp_name,b.risk_id,e.org_id,b.risk_name,d.risk_name  parent_name, ");
		sql.append(" plan.temp_id templateId, ");
		sql.append(" a.score_object_id,h.id  hId, z.EDIT_IDEA_CONTENT, h.EDIT_IDEA_CONTENT  Hedit,hr.id  hrId,zr.EDIT_IDEA_CONTENT zrResponse,hr.EDIT_IDEA_CONTENT  Hredit from  ");
		sql.append(" t_rm_rang_object_dept_emp  a  ");
		sql.append(" LEFT JOIN t_rm_risk_score_object b on a.score_object_id = b.id ");
		sql.append(" LEFT JOIN t_rm_risk_assess_plan plan ON plan.ID = b.assess_plan_id");
		sql.append(" LEFT JOIN t_rm_risks d on b.parent_id=d.id  ");
		sql.append(" LEFT JOIN t_rm_risk_org_temp e on a.score_object_id = e.object_id ");
		sql.append(" LEFT JOIN t_sys_employee f on f.id = a.score_emp_id  ");
		sql.append(" LEFT JOIN t_rm_dept_lead_circusee g on g.score_object_id = a.score_object_id and g.dept_lead_emp_id=:orgDeptEmpId ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_idea h on h.dept_lead_circusee_id = g.id ");
		sql.append(" LEFT JOIN t_rm_edit_idea z on z.OBJECT_DEPT_EMP_ID = a.id ");
		sql.append(" LEFT JOIN t_rm_edit_response zr ON zr.OBJECT_DEPT_EMP_ID = a.id");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_response hr on hr.dept_lead_circusee_id = g.id ");
		sql.append(" where b.assess_plan_id=:assessPlanId ");
		if(StringUtils.isNotBlank(query)){
			sql.append(" and (b.risk_name like :query or d.risk_name like :query or f.emp_name like :query) ");
		}
		sql.append(" and f.id in (:empIds) ");
		sql.append(" and e.org_id in (:orgId) ORDER BY b.risk_name ");
		
		
		SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
		if(StringUtils.isNotBlank(query)){
			sqlQuery.setParameter("query", "%"+query+"%");
		}
		sqlQuery.setParameter("orgDeptEmpId", orgDeptEmpId);
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.setParameterList("orgId", orgIdList);
		sqlQuery.setParameterList("empIds", scoreEmpIdList);
		@SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		String tempStr = "";
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
	        String assessEmpId = "";//打分人员ID
	        String riskId = "";//风险ID
	        String riskName = "";//部门ID
	        String parentRiskName = "";//上级风险名称
	        String templateId = ""; //模板ID
	        String tempId = ""; //模板ID
	        String riskIcon = ""; //打分风险灯
	        String isSummarizingRiskIcon = ""; //汇总风险灯
	        String objectScoreId = ""; //打分对象ID
	        String hId = ""; //意见ID
	        String empEditIdea = ""; //领导意见ID
	        String hEditIdeaContent = ""; //领导意见
	        String hrId = ""; //应对意见ID
	        String empResponseIdea = ""; //应对意见
	        String hrResponseIdeaContent = ""; //领导应对意见
	        if(null != objects[0]){
	        	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	assessEmpId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[4]){
            	riskName = objects[4].toString();
            }if(null != objects[5]){
            	parentRiskName = objects[5].toString();
            }if(null != objects[6]){
            	templateId = objects[6].toString();
            }if(null != objects[7]){
            	objectScoreId = objects[7].toString();
            }if(null != objects[8]){
            	hId = objects[8].toString();
            }if(null != objects[9]){
            	empEditIdea = objects[9].toString();
            }if(null != objects[10]){
            	hEditIdeaContent = objects[10].toString();
            }if(null != objects[11]){
            	hrId = objects[11].toString();
            }if(null != objects[12]){
            	empResponseIdea = objects[12].toString();
            }if(null != objects[13]){
            	hrResponseIdeaContent = objects[13].toString();
            }
            
            if(StringUtils.isBlank(templateId)){
            	if(applyContainer.getRiskAllMap().get(riskId).getTemplate() != null){
            		tempId = applyContainer.getRiskAllMap().get(riskId).getTemplate().getId();
            	}else{
            		tempId = applyContainer.getTempSys();
            	}
            }else{
            	tempId = templateId;
            }
            
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
            map.put("assessEmpId", assessEmpId);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("parentRiskName", parentRiskName);
            map.put("templateId", tempId);
            map.put("empEditIdea", empEditIdea);
            map.put("hrId", hrId);
            map.put("empResponseIdea", empResponseIdea);
            map.put("hrResponseIdeaContent", hrResponseIdeaContent);
            if("".equalsIgnoreCase(hEditIdeaContent)){
            	tempStr += "\t";
            	map.put("hEditIdeaContent", tempStr);
            }else{
            	 map.put("hEditIdeaContent", hEditIdeaContent);
            }
            
            riskIcon = o_riskStatusBO.getRiskIcon(riskId, tempId, rangObjectDeptEmpId, applyContainer);
            map.put("riskIcon", riskIcon);
            
            dimList = DynamicDim.getDimValue(riskId, tempId, rangObjectDeptEmpId, applyContainer);
        	
            if(dimList != null){
	        	for (String obj : dimList) {
	        		map.put(obj.split("--")[0], NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()),2));
				}
            }
        	
        	deptRisksList = o_scoreDeptBO.getDeptRisks(deptByRiskAllList, riskId);
        	if(deptRisksList.size() > 1){
        		if(isSummarizingRiskIconMap.get(riskId) == null){
	        		//多人评估
        			HashMap<String, Double> dimValueMap = 
        					MoreRoleDeptSummarizing.getDynamicDimValueMap("role", riskId, tempId, deptRisksList, applyContainer);
        			
        			String valueStr = "";
	        		double value = 1L;
	        		Set<String> key = dimValueMap.keySet();
	        		ArrayList<String> scoreDicValuelist = new ArrayList<String>();
	                for (Iterator<String> it = key.iterator(); it.hasNext();) {
	                    String keys = (String) it.next();
	                    scoreDicValuelist.add(keys + "--" + dimValueMap.get(keys).toString());
	                }
	                
	                valueStr = o_riskLevelBO.getRisklevel(scoreDicValuelist, applyContainer.getRiskLevelFormula(), applyContainer.getDimIdAllList());
	                if(valueStr.indexOf("--") == -1){
	                	value = Double.parseDouble(valueStr);
	                }
	                
	        		//得到风险灯
	        		DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(
	        				applyContainer.getAlarmPlanAllMap().get(applyContainer.getAlarmScenario()), value);
	        		if(dict != null){
	        			isSummarizingRiskIcon = dict.getValue();
	        		}
	        		
	        		isSummarizingRiskIconMap.put(riskId, isSummarizingRiskIcon);
        		}else{
        			isSummarizingRiskIcon = isSummarizingRiskIconMap.get(riskId);
        		}
        	}else{
        		//一人评估
        		isSummarizingRiskIcon = riskIcon;
        	}
        	
        	map.put("isSummarizingRiskIcon", isSummarizingRiskIcon);
        	if("icon-ibm-symbol-4-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//高
 				map.put("icon", "4");
 			}else if("icon-ibm-symbol-5-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//中
 				map.put("icon", "3");
 			}else if("icon-ibm-symbol-6-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//低
 				map.put("icon", "2");
 			}else if("icon-ibm-symbol-0-sm".equalsIgnoreCase(isSummarizingRiskIcon)){
 				//无
 				map.put("icon", "1");
 			}else{
 				map.put("icon", "0");
 			}
        	
        	map.put("objectScoreId", objectScoreId);
        	map.put("hId", hId);
        	mapsList.add(map);
        }
        
        Collections.sort(mapsList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> arg0,
					Map<String, Object> arg1) {
				return -arg0.get("icon").toString().compareTo(arg1.get("icon").toString());
			}
		});
        
        return mapsList;
	}
	
	/**
	 * 查询所有用户属于哪个角色
	 * @return HashMap<String, ArrayList<String>>
	 * @author 金鹏祥
	 * */
	public HashMap<String, ArrayList<String>> findUserRoleAllMap(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		StringBuffer sql = new StringBuffer();
        
        sql.append(" select c.id , a.ROLE_ID, b.ROLE_name, b.role_code from t_sys_user_role a, t_sys_role b, " +
        		"t_sys_employee c where a.ROLE_ID = b.id and c.user_id=a.user_id ");
        
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String empId = "";//人员ID
            String roleCode = "";//角色编号
            
            ArrayList<String> array = new ArrayList<String>();
            if(null != objects[0]){
            	empId = objects[0].toString();
            }if(null != objects[3]){
            	roleCode = objects[3].toString();
            }
            
            if(map.get(empId) != null){
            	map.get(empId).add(roleCode);
            }else{
            	array.add(roleCode);
            	map.put(empId, array);
            }
        }
		
		return map;
	}
	
	/**
	 * 查询领导部门下的同部门所有评估人
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<ArrayList<String>> finAssessByDeptRiskAllList(String assessPlanId) {
		StringBuffer sql = new StringBuffer();
        
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
        
		sql.append(" select a.id,a.score_emp_id,b.risk_id,a.score_org_name,a.score_emp_name,a.org_id from  ");
        sql.append(" (select a.id,a.score_emp_id,a.score_org_name ,a.score_emp_name,a.score_object_id,b.org_id from   ");
        sql.append(" T_RM_RANG_OBJECT_DEPT_EMP a, (select id,ORG_ID from T_RM_RISK_SCORE_DEPT where SCORE_OBJECT_ID in  ");
        sql.append(" (select id from t_rm_risk_score_object where ASSESS_PLAN_ID = :assessPlanId)) b where a.score_dept_id = b.id) a,   ");
        sql.append(" t_rm_risk_score_object b   ");
        sql.append(" where  a.score_object_id=b.id ");
      
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String empId = "";//打分人员ID
            String riskId = "";//风险ID
            
            ArrayList<String> array = new ArrayList<String>();
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	empId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }
            array.add(riskId);
            array.add(rangObjectDeptEmpId);
            array.add(empId);
            arrayList.add(array);
        }
        
        return arrayList;
	}
	
	/**
	 * 查询领导部门下的同部门所有评估人
	 * @param assessPlanId 评估计划ID
	 * @param ogrId 部门ID
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<ArrayList<String>> finAssessByDeptRiskAllList(String assessPlanId, List<String> ogrIdList) {
		StringBuffer sql = new StringBuffer();
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>(); //数据信息键值
		sql.append(" select a.id,a.score_emp_id,b.risk_id,a.score_org_name,a.score_emp_name,a.org_id from  ");
        sql.append(" (select a.id,a.score_emp_id,a.score_org_name ,a.score_emp_name,a.score_object_id,b.org_id from   ");
        sql.append(" T_RM_RANG_OBJECT_DEPT_EMP a, (select id,ORG_ID from T_RM_RISK_SCORE_DEPT where SCORE_OBJECT_ID in  ");
        sql.append(" (select id from t_rm_risk_score_object where ASSESS_PLAN_ID = :assessPlanId)) b where a.score_dept_id = b.id) a,   ");
        sql.append(" t_rm_risk_score_object b   ");
        sql.append(" where  a.score_object_id=b.id and a.org_id in (:ogrId) ");
      
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        sqlQuery.setParameterList("ogrId", ogrIdList);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String empId = "";//打分人员ID
            String riskId = "";//风险ID
            
            ArrayList<String> array = new ArrayList<String>();
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	empId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }
            array.add(riskId);
            array.add(rangObjectDeptEmpId);
            array.add(empId);
            arrayList.add(array);
        }
        
        return arrayList;
	}
	
	/**
	 * 查询所有评估人
	 * @param assessPlanId 评估计划ID
	 * @param ogrIds 部门ID集合
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	public HashMap<String, ArrayList<String>> finAssessByInDeptRiskAllMap(String assessPlanId, ArrayList<String> ogrIds) {
		StringBuffer sql = new StringBuffer();
		HashMap<String, ArrayList<String>> maps = new HashMap<String, ArrayList<String>>();
		sql.append(" select a.id,a.score_emp_id,b.risk_id,a.score_org_name,a.score_emp_name,a.org_id from  ");
        sql.append(" (select a.id,a.score_emp_id,a.score_org_name ,a.score_emp_name,a.score_object_id,b.org_id from   ");
        sql.append(" T_RM_RANG_OBJECT_DEPT_EMP a, (select id,ORG_ID from T_RM_RISK_SCORE_DEPT where SCORE_OBJECT_ID in  ");
        sql.append(" (select id from t_rm_risk_score_object where ASSESS_PLAN_ID = :assessPlanId)) b where a.score_dept_id = b.id) a, ");
        sql.append(" t_rm_risk_score_object b   ");
        sql.append(" where  a.score_object_id=b.id and a.org_id in (:ogrIds) ");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        sqlQuery.setParameterList("ogrIds", ogrIds);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String empId = "";//打分人员ID
            String riskId = "";//风险ID
            String orgId = "";//部门ID
            
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	empId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[5]){
            	orgId = objects[5].toString();
            }
            
            
            if(maps.get(riskId + "--" + orgId) != null){
            	maps.get(riskId + "--" + orgId).add(rangObjectDeptEmpId + "--" + empId);
            }else{
            	ArrayList<String> arrayList = new ArrayList<String>();
            	arrayList.add(rangObjectDeptEmpId + "--" + empId);
            	maps.put(riskId + "--" + orgId, arrayList);
            }
            
            
            
        }
        
        return maps;
	}
	
	/**
	 * 查询所有评估人
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<ArrayList<String>> finAssessByInDeptRiskAllList(String assessPlanId, ArrayList<String> ogrIds) {
		StringBuffer sql = new StringBuffer();
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
		sql.append(" select a.id,a.score_emp_id,b.risk_id,a.score_org_name,a.score_emp_name,a.org_id from  ");
        sql.append(" (select a.id,a.score_emp_id,a.score_org_name ,a.score_emp_name,a.score_object_id,b.org_id from   ");
        sql.append(" T_RM_RANG_OBJECT_DEPT_EMP a, (select id,ORG_ID from T_RM_RISK_SCORE_DEPT where SCORE_OBJECT_ID in  ");
        sql.append(" (select id from t_rm_risk_score_object where ASSESS_PLAN_ID = :assessPlanId)) b where a.score_dept_id = b.id) a, ");
        sql.append(" t_rm_risk_score_object b   ");
        sql.append(" where  a.score_object_id=b.id and a.org_id in (:ogrIds) ");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        sqlQuery.setParameterList("ogrIds", ogrIds);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String empId = "";//打分人员ID
            String riskId = "";//风险ID
            String orgId = "";//部门ID
            
            ArrayList<String> array = new ArrayList<String>();
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	empId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[5]){
            	orgId = objects[5].toString();
            }
            array.add(riskId + "--" + orgId);
            array.add(rangObjectDeptEmpId);
            array.add(empId);
            arrayList.add(array);
        }
        
        return arrayList;
	}
	
	/**
	 * 查询全部打分综合ID对应维度ID、维度值结果集合 -- 通过评估人
	 * @param assessPlanId 评估计划ID
	 * @param ArrayList<String> scoreEmpId 人员ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	public HashMap<String, String> findRangObjectDeptEmpDimDicList(String assessPlanId, ArrayList<String> scoreEmpId) {
		StringBuffer sql = new StringBuffer();
        
		HashMap<String, String> map = new HashMap<String, String>();
        
		sql.append(" select a.RANG_OBJECT_DEPT_EMP_ID,a.score_dim_id,b.SCORE_DIC_value ");
		sql.append(" from t_rm_score_result a , ");
		sql.append(" t_dim_score_dic b ,t_dim_score_dim c, t_rm_rang_object_dept_emp d, t_rm_risk_score_object e ");
		sql.append(" where a.score_dic_id=b.id and a.score_dim_id = c.id ");
		sql.append("  and d.id = a.RANG_OBJECT_DEPT_EMP_ID and d.SCORE_EMP_ID in (:scoreEmpId) ");
		sql.append(" and e.id = d.SCORE_OBJECT_ID and e.ASSESS_PLAN_ID = :assessPlanId ");
		
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        sqlQuery.setParameterList("scoreEmpId", scoreEmpId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String scoreDimId = "";//维度名称
            String scoreDicValue = "";//维度Value
            
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	scoreDimId = objects[1].toString();
            }if(null != objects[2]){
            	scoreDicValue = objects[2].toString();
            }
            
            map.put(rangObjectDeptEmpId + "--" + scoreDimId, scoreDicValue);
        }
        
        return map;
	}
	
	/**
	 * 查询全部打分综合ID对应维度ID、维度值结果集合 -- 通过评估计划
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	public HashMap<String, String> findRangObjectDeptEmpDimDicList(String assessPlanId) {
		StringBuffer sql = new StringBuffer();
        
		HashMap<String, String> map = new HashMap<String, String>();
        
		sql.append(" select a.RANG_OBJECT_DEPT_EMP_ID,a.score_dim_id,b.SCORE_DIC_value ");
		sql.append(" from t_rm_score_result a , ");
		sql.append(" t_dim_score_dic b ,t_dim_score_dim c, t_rm_rang_object_dept_emp d, t_rm_risk_score_object e ");
		sql.append(" where a.score_dic_id=b.id and a.score_dim_id = c.id  ");
		sql.append(" and d.id = a.RANG_OBJECT_DEPT_EMP_ID ");
		sql.append(" and e.id = d.SCORE_OBJECT_ID and e.ASSESS_PLAN_ID = :assessPlanId ");
		
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String scoreDimId = "";//维度名称
            String scoreDicValue = "";//维度Value
            
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	scoreDimId = objects[1].toString();
            }if(null != objects[2]){
            	scoreDicValue = objects[2].toString();
            }
            
            map.put(rangObjectDeptEmpId + "--" + scoreDimId, scoreDicValue);
        }
        
        return map;
	}

	/**
	 * 通过综合ID查询综合实体
	 * @param objectDeptEmpId 打分综合ID
	 * @return RangObjectDeptEmp
	 * @author 金鹏祥
	 * */
	public RangObjectDeptEmp findRangObjectDeptEmpById(String objectDeptEmpId){
		Criteria criteria = o_rangObjectDeptEmpDAO.createCriteria();
		criteria.add(Restrictions.eq("id", objectDeptEmpId));
		
		return (RangObjectDeptEmp) criteria.list().get(0);
	}
	
	/**
	 * 打分部门打分对象相同的综合实体
	 * @param ode
	 * @return	List<RangObjectDeptEmp>
	 * @author 王再冉
	 */
	@SuppressWarnings("unchecked")
	public List<RangObjectDeptEmp> findObjDeptEmpsByAll(RiskScoreDept dept, RiskScoreObject obj) {
		Criteria c = o_rangObjectDeptEmpDAO.createCriteria();
		List<RangObjectDeptEmp> list = null;
		if (null!=dept && null!=obj) {
			c.add(Restrictions.and(Restrictions.eq("scoreObject.id", obj.getId()),
									Restrictions.eq("scoreDept.id", dept.getId())));
		} 
		list = c.list();
		return list;
	}
	
	/**
	 * 根据打分对象id查所有实体
	 * @param objId
	 * @return	List<RangObjectDeptEmp>
	 * @author 王再冉
	 */
	@SuppressWarnings("unchecked")
	public List<RangObjectDeptEmp> findObjDeptEmpsByObjId(String objId) {
		Criteria c = o_rangObjectDeptEmpDAO.createCriteria();
		List<RangObjectDeptEmp> list = null;
		if (StringUtils.isNotBlank(objId)) {
			c.add(Restrictions.eq("scoreObject.id", objId));
		} 
		list = c.list();
		if(list.size()>0){
			return list;
		}else{
			return null;
		}
	}
	
	/**
	 * 根据打分对象id集合查综合实体 ,放到Map中
	 * @param objIdList
	 * @return	List<Map<String,Object>>
	 * @author 王再冉
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findobjDeptEmpListByScoreObjIdsMap(List<String> objIdList) {
		Criteria c = o_rangObjectDeptEmpDAO.createCriteria();
		
		List<RangObjectDeptEmp> list = null;
		if(objIdList.size()>0){
			c.add(Restrictions.in("scoreObject.id", objIdList));
			list = c.list();
		}
		
		List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
		if(null!= list){
			for(RangObjectDeptEmp rode : list){
				Map<String, Object> exitObjMap = new HashMap<String, Object>();
				exitObjMap.put("scoreObj", rode.getScoreObject());
				exitObjMap.put("scoreDept",rode.getRiskOrgTemp());
				exitObjMap.put("scoreEmp",rode.getScoreEmp());
				exitObjMap.put("rode",rode);
				mapList.add(exitObjMap);
			}
		}
		return mapList;
	}
	
	/**
	 * removeRangObjectDeptEmpsByIds：根据综合实体id集合删除综合实体
	 * @param ids	综合实体id集合
	 */
	public void removeRangObjectDeptEmps(List<RangObjectDeptEmp> rodes){
		for(RangObjectDeptEmp rode : rodes){
			o_rangObjectDeptEmpDAO.delete(rode);
		}
	}
	
	/**
	 * 根据评估计划所有参加的评估人员ID
	 * @param assessPlanId 评估计划ID
	 * @param scoreEmpIdList 人员ID集合
	 * @return ArrayList<String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unused")
	public ArrayList<String> findRangObjectDeptEmpEmpIdList(String assessPlanId, ArrayList<String> scoreEmpIdList) {
		StringBuffer sql = new StringBuffer();
        
		ArrayList<String> arrayList = new ArrayList<String>();
        
		sql.append(" select id,score_emp_id from t_rm_rang_object_dept_emp ");
		sql.append(" where score_object_id in (select id from t_rm_risk_score_object where assess_plan_id=:assessPlanId) ");
		sql.append(" and SCORE_EMP_ID in (:scoreEmpIdList) GROUP BY SCORE_EMP_ID ");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        sqlQuery.setParameterList("scoreEmpIdList", scoreEmpIdList);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String rangObjectDeptEmpId = ""; //范围-对象-部门-人员综合表ID
            String empId = ""; //人员ID

            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	empId = objects[1].toString();
            }
            arrayList.add(empId);
        }
        
        return arrayList;
	}
	
	/**
	 * 根据评估计划所有参加的评估人员ID
	 * @param assessPlanId 评估计划ID
	 * @return ArrayList<String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unused")
	public ArrayList<String> findRangObjectDeptEmpEmpIdGroupList(String assessPlanId) {
		StringBuffer sql = new StringBuffer();
        
		ArrayList<String> arrayList = new ArrayList<String>();
        
		sql.append(" select id,score_emp_id from t_rm_rang_object_dept_emp ");
		sql.append(" where score_object_id in (select id from t_rm_risk_score_object where assess_plan_id=:assessPlanId) GROUP BY score_emp_id ");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String rangObjectDeptEmpId = "";
            String empId = "";//范围-对象-部门-人员综合表ID

            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	empId = objects[1].toString();
            }
            arrayList.add(empId);
        }
        
        return arrayList;
	}
	
	/**
	 * 根据评估计划所有参加的评估人员ID
	 * */
	@SuppressWarnings("unused")
	public ArrayList<String> findRangObjectDeptEmpEmpIdList(String assessPlanId) {
		StringBuffer sql = new StringBuffer();
        
		ArrayList<String> arrayList = new ArrayList<String>();
        
		sql.append(" select id,score_emp_id from t_rm_rang_object_dept_emp ");
		sql.append(" where score_object_id in (select id from t_rm_risk_score_object where assess_plan_id=:assessPlanId) ");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String rangObjectDeptEmpId = "";
            String empId = "";//范围-对象-部门-人员综合表ID

            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	empId = objects[1].toString();
            }
            arrayList.add(empId);
        }
        
        return arrayList;
	}
	
	/**
	 * 该empID下评估的全部风险事件
	 * */
	public HashMap<String, HashMap<String, Boolean>> findOrangObjectDeptEmpByAssessPlanId(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		HashMap<String, HashMap<String, Boolean>> map = new HashMap<String, HashMap<String,Boolean>>();
		sql.append(" select a.score_emp_id,c.risk_name from t_rm_rang_object_dept_emp a ");
		sql.append(" LEFT JOIN t_rm_risk_score_object b on a.score_object_id=b.id ");
		sql.append(" LEFT JOIN t_rm_risks c on b.risk_id = c.id ");
		sql.append(" where b.assess_plan_id=:assessPlanId ");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String empId = "";//范围-对象-部门-人员综合表ID
            String riskName = "";
            
            if(null != objects[0]){
            	empId = objects[0].toString();
            }if(null != objects[1]){
            	riskName = objects[1].toString();
            }
            
            if(map.get(empId) != null){
            	map.get(empId).put(riskName, true);
            }else{
            	 HashMap<String, Boolean> tempMap = new HashMap<String, Boolean>();
                 tempMap.put(riskName, true);
                 map.put(empId, tempMap);
            }
            
        }
        
        return map;
	}
	
	/**
	 * 该empID下未评估的风险事件
	 * */
	/**
	 * 该empID下评估的全部风险事件
	 * */
	public HashMap<String, HashMap<String, Boolean>> findNotOrangObjectDeptEmpByAssessPlanId(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		HashMap<String, HashMap<String, Boolean>> map = new HashMap<String, HashMap<String,Boolean>>();
		sql.append(" select a.score_emp_id,d.risk_name from t_rm_rang_object_dept_emp a ");
		sql.append(" LEFT JOIN t_rm_risk_score_object b on a.score_object_id=b.id ");
		sql.append(" LEFT JOIN t_rm_score_result c on a.id = c.rang_object_dept_emp_id ");
		sql.append(" LEFT JOIN t_rm_risks d on b.risk_id = d.id ");
		sql.append(" where b.assess_plan_id=:assessPlanId and score_dim_id is null");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
           
            String empId = "";//范围-对象-部门-人员综合表ID
            String riskName = "";
            
            if(null != objects[0]){
            	empId = objects[0].toString();
            }if(null != objects[1]){
            	riskName = objects[1].toString();
            }
            
            if(map.get(empId) != null){
            	map.get(empId).put(riskName, false);
            }else{
            	 HashMap<String, Boolean> tempMap = new HashMap<String, Boolean>();
                 tempMap.put(riskName, false);
                 map.put(empId, tempMap);
            }
            
        }
        
        return map;
	}
}