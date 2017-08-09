package com.fhd.ra.business.assess.risktidy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.dao.assess.quaAssess.EditIdeaDAO;
import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.entity.assess.quaAssess.EditIdea;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.AlarmRegion;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.sys.assess.WeightSet;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.NumberUtil;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.approval.ApplyContainer;
import com.fhd.ra.business.assess.approval.AssessDateInitBO;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.StatisticsResultBO;
import com.fhd.ra.business.assess.summarizing.util.SummarizinggAtherUtilBO;
import com.fhd.ra.business.assess.util.DynamicDim;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.assess.util.RiskStatusBO;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.web.controller.util.Utils;
import com.fhd.sys.business.assess.WeightSetBO;

import edu.emory.mathcs.backport.java.util.Collections;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class ShowRiskTidyBO {

	@Autowired
	private StatisticsResultDAO o_statisticsResultDAO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private WeightSetBO o_weightSetBO;
	
	@Autowired
	private RiskOutsideBO o_risRiskService;
	
	@Autowired
	private SummarizinggAtherUtilBO o_summarizinggAtherUtilBO;
	
	@Autowired
	private EditIdeaDAO o_editIdeaDAO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private FormulaSetBO o_formulaSetBO;
	
	@Autowired
	private StatisticsResultBO o_statisticsResultBO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private RiskStatusBO o_riskStatusBO;
	
	@Autowired
	private RiskLevelBO o_riskLevelBO;
	
	@Autowired
	private AssessDateInitBO o_assessDateInitBO;
	
	/**
	 * 得到该风险事件下的发生可能性、影响程度分值
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String,ArrayList<String>>
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String,ArrayList<String>> findStatisticsResultByScoreObjectIdAndDimIdMapAll(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select score_object_id,score_dim_id,score from t_rm_statistics_result " +
				"where assess_plan_id=:assessPlanId order by score_object_id DESC ");
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String scoreObjectId = "";
            String scoreDimId = "";
            String score = "";
            
            if(null != objects[0]){
            	scoreObjectId = objects[0].toString();
            }if(null != objects[1]){
            	scoreDimId = objects[1].toString();
            }if(null != objects[2]){
            	score = objects[2].toString();
            }
            
            
            ArrayList<String> arrayList = new ArrayList<String>();
            
            if(StringUtils.isBlank(score)){
            	arrayList.add(scoreDimId + "--" + "null");
            }else{
            	arrayList.add(scoreDimId + "--" + Double.parseDouble(score));
            }
            
			if(map.get(scoreObjectId) != null){
				if(StringUtils.isBlank(score)){
					map.get(scoreObjectId).add(scoreDimId + "--" + "null");
				}else{
					map.get(scoreObjectId).add(scoreDimId + "--" + Double.parseDouble(score));
				}
			}else{
				map.put(scoreObjectId, arrayList);
			}
        }
		
		return map;
	}
	
	
	/** 
	  * @Description: 根据风险分类查询风险分类
	  * @author jia.song@pcitc.com
	  * @date 2017年4月20日 下午4:17:03 
	  * @param query
	  * @param assessPlanId
	  * @param typeId
	  * @param type
	  * @return 
	  */
	public List<Map<String, String>> findRiskReListSf2(String query, String assessPlanId, String typeId, String type,String companyId){
		
		List<Map<String, String>> rtnMap = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" result.ID RESULT_ID,object.ID OJBECT_ID,risks.RISK_CODE,risks.RISK_NAME,risks.PARENT_ID,risks.PARENT_NAME,result.ASSESS_EMP_COUNT,result.SCORE_DIM_ID,result.SCORE_DIM_VALUE,result.SCORE,object.RISK_SCORE ");
		sql.append(" FROM ");
		sql.append(" T_RM_RISK_SCORE_OBJECT object,t_rm_risks risks,t_rm_adjust_history_result result ");
		sql.append(" WHERE ");
		sql.append(" result.ASSESS_PLAN_ID = :assessPlanId ");
		if(!"root".equals(typeId)){
			sql.append(" AND risks.id_seq like concat('%','"+typeId+"','%') ");
		}
		sql.append(" AND object.ID = result.SCORE_OBJECT_ID ");
		sql.append(" AND object.RISK_ID = risks.ID ");
		sql.append(" ORDER BY RISK_ID,result.SCORE_DIM_ID  ");
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
//		sqlQuery.executeUpdate();
		//默认
		int i = 0,j = 0;
		String riskId = "";
		Map<String,String> tempMap = null;
		for(Object[] objects : list){
			if(!riskId.equals( objects[1].toString())){
				if(tempMap != null){
					rtnMap.add(tempMap);
				}
				tempMap = new HashMap<String,String>();
				riskId = objects[1].toString();
				i = 0;
			}
			if(i == 0){
				if(null != objects[0]){
					tempMap.put("id", objects[0].toString());
				}if(null != objects[4]){
					tempMap.put("riskParentId", objects[4].toString());
				}if(null != objects[5]){
					tempMap.put("riskParentName", objects[5].toString());
				}if(null != objects[1]){
					tempMap.put("objectId", objects[1].toString());
//					riskId = objects[3].toString();
				}if(null != objects[3]){
					tempMap.put("riskName", objects[3].toString());
				}if(null != objects[7]){
					tempMap.put(objects[7].toString(), Utils.retain2Decimals(Double.parseDouble(objects[9].toString())));
				}if(null != objects[6]){
					tempMap.put("assessEmpSum", objects[6].toString());
				}if(null != objects[10]){
					double riskScore = Double.parseDouble(objects[10].toString());
					tempMap.put("riskScore", Utils.retain2Decimals(riskScore));
					tempMap.put("riskIcon", getRiskIconByRiskScore(riskScore,companyId));
				}
			}else{
				if(null != objects[7]&&null != objects[9]){
					tempMap.put(objects[7].toString(), Utils.retain2Decimals(Double.parseDouble(objects[9].toString())));
				}
			}
			i++;
			j++;
			if(j == list.size()){
				rtnMap.add(tempMap);
			}
		}
		return rtnMap;
	}

	/** 
	  * @Description: 根据风险分类查询风险分类
	  * @author jia.song@pcitc.com
	  * @date 2017年4月20日 下午4:17:03 
	  * @param query
	  * @param assessPlanId
	  * @param typeId
	  * @param type
	  * @return 
	  */
	public List<Map<String, String>> findRiskReListSf2ForSecurity(String query, String assessPlanId, String typeId, String type,String companyId){
		List<Map<String, String>> rtnMap = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" result.ID RESULT_ID,object.ID OJBECT_ID,object.RISK_CODE,object.RISK_NAME,object.PARENT_ID,risks.RISK_NAME parent_name,result.ASSESS_EMP_COUNT,result.SCORE_DIM_ID,result.SCORE_DIM_VALUE,result.SCORE,object.RISK_SCORE,");
		sql.append(" object.RISK_ID ");
		sql.append(" FROM ");
		sql.append(" t_rm_risks risks,t_rm_adjust_history_result result,T_RM_RISK_SCORE_OBJECT object ");
		sql.append(" WHERE ");
		sql.append(" result.ASSESS_PLAN_ID = :assessPlanId ");
		if(!"root".equals(typeId)){
			sql.append(" AND risks.id_seq like concat('%','"+typeId+"','%') ");
		}
		sql.append(" AND object.ID = result.SCORE_OBJECT_ID ");
		sql.append(" AND object.PARENT_ID = risks.ID ");
		sql.append(" ORDER BY RISK_ID,result.SCORE_DIM_ID  ");
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		//默认
		int i = 0,j = 0;
		String riskId = "";
		Map<String,String> tempMap = null;
		for(Object[] objects : list){
			if(!riskId.equals( objects[1].toString())){
				if(tempMap != null){
					rtnMap.add(tempMap);
				}
				tempMap = new HashMap<String,String>();
				riskId = objects[1].toString();
				i = 0;
			}
			if(i == 0){
				if(null != objects[0]){
					tempMap.put("id", objects[0].toString());
				}if(null != objects[4]){
					tempMap.put("riskParentId", objects[4].toString());
				}if(null != objects[5]){
					tempMap.put("riskParentName", objects[5].toString());
				}if(null != objects[1]){
					tempMap.put("objectId", objects[1].toString());
//					riskId = objects[3].toString();
				}if(null != objects[3]){
					tempMap.put("riskName", objects[3].toString());
				}if(null != objects[7]){
					tempMap.put(objects[7].toString(), Utils.retain2Decimals(Double.parseDouble(objects[9].toString())));
				}if(null != objects[6]){
					tempMap.put("assessEmpSum", objects[6].toString());
				}if(null != objects[10]){
					double riskScore = Double.parseDouble(objects[10].toString());
					tempMap.put("riskScore", Utils.retain2Decimals(riskScore));
					tempMap.put("riskIcon", getRiskIconByRiskScore(riskScore,companyId));
				}if(null != objects[11]){
					tempMap.put("riskId", objects[11].toString());
				}
			}else{
				if(null != objects[7]&&null != objects[9]){
					tempMap.put(objects[7].toString(), Utils.retain2Decimals(Double.parseDouble(objects[9].toString())));
				}
			}
			i++;
			j++;
			if(j == list.size()){
				rtnMap.add(tempMap);
			}
		}
		return rtnMap;
	}
	
	/** 
	  * @Description: 根据计划获取辨识结果列表，其中包含各个部门的意见反馈
	  * @author jia.song@pcitc.com
	  * @date 2017年5月25日 下午4:31:53 
	  * @param query
	  * @param assessPlanId
	  * @param typeId
	  * @param type
	  * @return 
	  */
	public List<Map<String, String>> findTidyRiskReListSf2ForSecurity(String query, String assessPlanId, String typeId, String type){
		List<Map<String, String>> rtnMap = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" object.ID OJBECT_ID, object.RISK_ID, object.RISK_CODE, object.RISK_NAME, object.PARENT_ID, risks.RISK_NAME parent_name, object.DELETE_ESTATUS, leaderEdit.EDIT_IDEA_CONTENT AS idea, leaderResponse.EDIT_IDEA_CONTENT AS residea,emp.EMP_NAME,org.ORG_NAME ");
		sql.append(" FROM ");
		sql.append(" t_rm_risks risks,T_RM_RISK_SCORE_OBJECT object ");
		sql.append(" LEFT JOIN t_rm_dept_lead_circusee leader ON leader.SCORE_OBJECT_ID = object.ID ");
		sql.append(" LEFT JOIN t_sys_employee emp ON emp.ID = leader.DEPT_LEAD_EMP_ID ");
		sql.append(" LEFT JOIN t_sys_emp_org emporg ON emp.ID = emporg.EMP_ID ");
		sql.append( "LEFT JOIN t_sys_organization org ON emporg.ORG_ID = org.ID ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_idea leaderEdit ON leaderEdit.DEPT_LEAD_CIRCUSEE_ID = leader.ID ");
		sql.append(" LEFT JOIN t_rm_dept_lead_edit_response leaderResponse ON leaderResponse.DEPT_LEAD_CIRCUSEE_ID = leader.ID");
		sql.append(" WHERE ");
		sql.append(" object.ASSESS_PLAN_ID = :assessPlanId ");
		if(!"root".equals(typeId)){
			sql.append(" AND risks.id_seq like concat('%','"+typeId+"','%') ");
		}
		sql.append(" AND object.PARENT_ID = risks.ID ");
		sql.append(" ORDER BY RISK_ID  ");
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		//默认
		int i = 0,j = 0;
		String riskId = "";
		Map<String,String> tempMap = null;
		for(Object[] objects : list){
			tempMap = new HashMap<String,String>();
			if(null != objects[0]){
				tempMap.put("objectId", objects[0].toString());
			}if(null != objects[1]){
				tempMap.put("riskId", objects[1].toString());
			}if(null != objects[5]){
				tempMap.put("riskParentName", objects[5].toString());
			}if(null != objects[6]){
				tempMap.put("deleteEstatus", objects[6].toString());
			}if(null != objects[3]){
				tempMap.put("riskName", objects[3].toString());
			}if(null != objects[7]){
				tempMap.put("idea", objects[7].toString());
			}if(null != objects[8]){
				tempMap.put("residea", objects[8].toString());
			}if(null != objects[9]){
				tempMap.put("empName", objects[9].toString());
			}if(null != objects[10]){
				tempMap.put("orgName", objects[10].toString());
			}
			rtnMap.add(tempMap);
		}
		return rtnMap;
	}
	
	
	/** 
	  * @Description: 根据风险综合得分获取风险指示灯
	  * @author jia.song@pcitc.com
	  * @date 2017年5月8日 下午1:19:19 
	  * @param riskScore
	  * @return 
	  */
	public  String getRiskIconByRiskScore(double riskScore,String companyId){
		//companlyid  直接给 1
		String rtnValue = null;
		WeightSet weightSet = o_weightSetBO.findWeightSetAll(companyId);
		Set<AlarmRegion> alarmRegionSet = weightSet.getAlarmScenario().getAlarmRegions();
		for(AlarmRegion alarmRegion : alarmRegionSet){
			int min = Integer.parseInt(alarmRegion.getMinValue());
			int max = Integer.parseInt(alarmRegion.getMaxValue());
			if(riskScore > min && riskScore < max){
				rtnValue = alarmRegion.getAlarmIcon().getValue();
			}
		}
		return rtnValue;
	}
	
	
	/**
	 * 风险分类下的风险事件
	 * @param query 条件查询
	 * @param assessPlanId 评估计划ID
	 * @param typeId 操作类型ID
	 * @param type 操作类型
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	@RecordLog("查询风险整理风险、组织、目标、流程信息")
	public ArrayList<HashMap<String, String>> findRiskReList(String query, String assessPlanId, String typeId, String type){
		StringBuffer sql = new StringBuffer();
		ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>(); //数据信息键值
		//获得计划下每个风险事件的纬x度分值
		Map<String, Map<String,String>> dimScoreMap = this.findDimScoreByAssessPlanId(assessPlanId);
		
		//该风险下评估人数量
		StringBuffer assessEmpCountSQL = new StringBuffer();
		assessEmpCountSQL.append(" LEFT JOIN (SELECT ");
		assessEmpCountSQL.append(" a.RISK_ID,COUNT(a.RISK_ID) assess_risk FROM ");
		assessEmpCountSQL.append(" T_RM_RISK_SCORE_OBJECT a, ");
		assessEmpCountSQL.append(" T_RM_RANG_OBJECT_DEPT_EMP b  ");
		assessEmpCountSQL.append(" WHERE a.ASSESS_PLAN_ID = :assessPlanId ");
		assessEmpCountSQL.append(" AND a.id = b.SCORE_OBJECT_ID GROUP BY a.risk_id) ct ON ct.risk_id = formRiskId ");
		
		if("risk".equalsIgnoreCase(type)){
			sql.append(" select ");
			sql.append(" a.id, ");
			sql.append(" risk.parent_id, ");
			sql.append(" c.risk_name, ");
			sql.append(" risk.id  risk_id, ");
			sql.append(" risk.risk_name  name, ");
			sql.append(" risk.template_id, ");
			sql.append(" d.temp_id, ");
			sql.append(" risk.DELETE_ESTATUS , ");
			sql.append(" edit.edit_idea_id,z.id as score_object_id, a.ASSESSEMENT_STATUS, ct.assess_risk ");
			sql.append(" from ");
			sql.append(" t_rm_risk_adjust_history  a ");
			sql.append(" LEFT JOIN  t_rm_risks risk on a.RISK_ID=risk.id  ");
			sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = risk.id ");
			sql.append(" LEFT JOIN t_rm_risks c on risk.parent_id=c.id   ");
			sql.append(" left JOIN t_rm_risk_assess_plan d on d.id = a.assess_plan_id ");
			sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
			sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
			sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
			sql.append("GROUP BY b.risk_id) edit on a.RISK_ID = edit.risk_id ");
			sql.append(assessEmpCountSQL.toString().replace("formRiskId", "a.risk_id"));
			sql.append(" where ");
			sql.append(" a.assess_plan_id=:assessPlanId ");
			if(StringUtils.isNotBlank(typeId)){
				if(!"root".equalsIgnoreCase(typeId)){
					//如果节点不为root 则是树节点下的过滤查询
					sql.append(" and risk.ID_SEQ LIKE :likeTypeId ");
				}
			}
			if(StringUtils.isNotBlank(query)){
				if(!query.matches("\\d*")){//判断查询条件是否为数字
					sql.append(" and (c.risk_name like :likeQuery or risk.risk_name like :likeQuery) ");
				}
			}
			sql.append(" and risk.IS_RISK_CLASS ='re' ");
			sql.append(" ORDER BY ");
			sql.append(" risk.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
			sql.append(" c.risk_name ");
		}else if("dept".equalsIgnoreCase(type)){
			sql.append(" select ");
			sql.append(" a.id, ");
			sql.append(" b.PARENT_ID, ");
			sql.append(" e.risk_name  parent_name, ");
			sql.append(" a.RISK_ID, ");
			sql.append(" b.RISK_NAME, ");
			sql.append(" b.TEMPLATE_ID, ");
			sql.append(" c.TEMP_ID , ");
			sql.append(" b.DELETE_ESTATUS, edit.edit_idea_id,z.id as score_object_id, d.ASSESSEMENT_STATUS, ct.assess_risk ");
			sql.append(" from ");
			sql.append(" t_rm_risk_adjust_history d ");
			sql.append(" LEFT JOIN t_rm_risk_assess_plan c on d.ASSESS_PLAN_ID = c.id ");
			sql.append(" LEFT JOIN t_rm_risk_org a on a.RISK_ID = d.risk_id ");
			sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = a.RISK_ID ");
			sql.append(" LEFT JOIN t_rm_risks b on d.RISK_ID = b.id   ");
			sql.append(" LEFT JOIN t_sys_organization o on o.ID = a.ORG_ID  ");
			sql.append(" LEFT JOIN t_rm_risks e  on  e.id= b.PARENT_ID ");
			sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
			sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
			sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
			sql.append("GROUP BY b.risk_id) edit on d.RISK_ID = edit.risk_id ");
			sql.append(assessEmpCountSQL.toString().replace("formRiskId", "a.risk_id"));
			sql.append(" where ");
			if(!"root".equalsIgnoreCase(typeId)){
				//如果节点不为root 则是树节点下的过滤查询
				sql.append(" o.ID_SEQ like :likeTypeId and ");
			}
			sql.append(" d.assess_plan_id=:assessPlanId ");
			sql.append(" and b.is_risk_class = 're' ");
			if(StringUtils.isNotBlank(query)){
				if(!query.matches("\\d*")){
					sql.append(" and (e.risk_name like :likeQuery or b.RISK_NAME like :likeQuery) ");
				}
			}
			sql.append(" GROUP BY ");
			sql.append(" a.RISK_ID  ");
			sql.append(" ORDER BY ");
			sql.append(" b.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
			sql.append(" parent_name ");
		}else if("strategyMap".equalsIgnoreCase(type)){
			sql.append(" select * from ");
			sql.append(" (select ");
			sql.append(" a.id, ");
			sql.append(" b.PARENT_ID, ");
			sql.append(" f.risk_name  PARENT_NAME, ");
			sql.append(" a.RISK_ID, ");
			sql.append(" b.RISK_NAME, ");
			sql.append(" b.TEMPLATE_ID, ");
			sql.append(" c.TEMP_ID , ");
			sql.append(" b.DELETE_ESTATUS,edit.edit_idea_id, e.strategy_map_id,z.id as score_object_id, d.ASSESSEMENT_STATUS, ct.assess_risk ");
			sql.append(" from ");
			sql.append(" t_rm_risk_adjust_history d ");
			sql.append(" LEFT JOIN t_rm_risk_assess_plan c on c.ID = d.ASSESS_PLAN_ID ");
			sql.append(" LEFT JOIN t_kpi_kpi_rela_risk a on a.RISK_ID = d.RISK_ID ");
			sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = a.RISK_ID ");
			sql.append(" LEFT JOIN t_rm_risks b on a.RISK_ID = b.id  ");
			sql.append(" LEFT JOIN t_kpi_sm_rela_kpi e on e.kpi_id = a.kpi_id   ");
			sql.append(" LEFT JOIN t_rm_risks f on f.id=b.parent_id ");
			sql.append(" LEFT JOIN t_rm_rang_object_dept_emp object on d.id = object.score_object_id ");
			sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
			sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
			sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
			sql.append("GROUP BY b.risk_id) edit on d.RISK_ID = edit.risk_id ");
			sql.append(assessEmpCountSQL.toString().replace("formRiskId", "a.risk_id"));
			sql.append(" where ");
			sql.append(" a.etype='i'");
			sql.append(" and d.assess_plan_id=:assessPlanId ");
			if(!"root".equalsIgnoreCase(typeId)){
				//如果节点不为root 则是树节点下的过滤查询
				if(StringUtils.isNotBlank(typeId)){
					String ids [] = typeId.split("\\_");
					if(ids.length==1){
						sql.append(" and e.strategy_map_id = :typeId");
					}else{
						sql.append(" and e.kpi_id = :ids");
					}
				}
			}
			sql.append(" and b.IS_RISK_CLASS = 're' ");
			if(StringUtils.isNotBlank(query)){
				if(!query.matches("\\d*")){
					sql.append(" and (f.risk_name like :likeQuery or b.RISK_NAME like :likeQuery) ");
				}
			}
			sql.append(" GROUP BY ");
			sql.append(" a.risk_id  ");
			sql.append(" order by ");
			sql.append(" b.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
			sql.append(" PARENT_NAME ) a where a.strategy_map_id is not null ");
		}else if("processure".equalsIgnoreCase(type) ){
			sql.append("select ");
			sql.append("a.id, ");
			sql.append("b.PARENT_ID, ");
			sql.append("e.risk_name  parent_name, ");
			sql.append("a.RISK_ID, ");
			sql.append("b.RISK_NAME, ");
			sql.append("b.TEMPLATE_ID, ");
			sql.append("c.TEMP_ID , ");
			sql.append("b.DELETE_ESTATUS,edit.edit_idea_id,z.id as score_object_id, d.ASSESSEMENT_STATUS, ct.assess_risk ");
			sql.append("from ");
			sql.append("t_rm_risk_adjust_history d ");
			sql.append("LEFT JOIN t_rm_risk_assess_plan c on c.ID = d.ASSESS_PLAN_ID ");
			sql.append("LEFT JOIN t_rm_risks b on b.id = d.risk_id ");
			sql.append("LEFT JOIN t_processure_risk_processure a on a.RISK_ID = b.id   ");
			sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = a.RISK_ID ");
			sql.append("LEFT JOIN t_ic_processure p on a.PROCESSURE_ID = p.ID  ");
			sql.append("LEFT JOIN t_rm_risks e on e.id=b.PARENT_ID   ");
			sql.append("LEFT JOIN t_rm_rang_object_dept_emp object on d.id = object.score_object_id ");
			sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
			sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
			sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
			sql.append("GROUP BY b.risk_id) edit on d.RISK_ID = edit.risk_id ");
			sql.append(assessEmpCountSQL.toString().replace("formRiskId", "a.risk_id"));
			sql.append("where ");
			sql.append("a.etype='i' ");
			sql.append("and d.assess_plan_id=:assessPlanId ");
			if(!"root".equalsIgnoreCase(typeId)){
				//如果节点不为root 则是树节点下的过滤查询
				sql.append("and p.ID_SEQ like :likeTypeId ");
			}
			if(StringUtils.isNotBlank(query)){
				if(!query.matches("\\d*")){
					sql.append(" and (e.risk_name like :likeQuery or b.RISK_NAME like :likeQuery) ");
				}
			}
			sql.append(" and b.is_risk_class = 're' ");
			sql.append("GROUP BY ");
			sql.append("a.RISK_ID  ");
			sql.append("order by ");
			sql.append("b.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
			sql.append("parent_name ");
		}
		
		if(StringUtils.isNotBlank(sql)){
			SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
			sqlQuery.setParameter("assessPlanId", assessPlanId);
			
			if(StringUtils.isNotBlank(query)){
				if(!query.matches("\\d*")){//判断查询条件是否为数字
					sqlQuery.setParameter("likeQuery", "%"+query+"%");
				}
			}
			
			if("strategyMap".equalsIgnoreCase(type)){
				if(StringUtils.isNotBlank(typeId)){
					if(!"root".equalsIgnoreCase(typeId)){
						String ids [] = typeId.split("\\_");
						if(ids.length==1){
							sqlQuery.setParameter("typeId", typeId);
						}else{
							sqlQuery.setParameter("ids", ids[1]);
						}
					}
				}
			}else{
				if(StringUtils.isNotBlank(typeId)){
					if(!"root".equalsIgnoreCase(typeId)){
						sqlQuery.setParameter("likeTypeId", "%." + typeId + ".%");
					}
				}
			}
			
			List<Object[]> list = sqlQuery.list();
			for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				
				String id = ""; //风险历史记录ID
				String riskParentId = ""; //风险上级ID
				String riskParentName = ""; //风险上级名称
				String riskId = ""; //风险ID
				String riskName = ""; //风险名称
				String riskIcon = ""; //排序风险状态灯
				String templateId = ""; //模板ID
				String tempId = ""; //模板ID
				String assessEmpSum = ""; //评估人数
				String riskStatusIcon = ""; //风险状态灯
				String editIdeaId = ""; //编辑ID
				String scoreObjectId = ""; //打分对象
				String strategyMapId = ""; //目标ID
				
				if(objects.length == 12){
					//风险、组织、流程
					if(null != objects[0]){
						id = objects[0].toString();
					}if(null != objects[1]){
						riskParentId = objects[1].toString();
					}if(null != objects[2]){
						riskParentName = objects[2].toString();
					}if(null != objects[3]){
						riskId = objects[3].toString();
					}if(null != objects[4]){
						riskName = objects[4].toString();
					}if(null != objects[5]){
						templateId = objects[5].toString();
					}if(null != objects[6]){
						tempId = objects[6].toString();
					}if(null != objects[7]){
						riskStatusIcon = objects[7].toString();
					}if(null != objects[8]){
						editIdeaId = objects[8].toString();
					}if(null != objects[9]){
						scoreObjectId = objects[9].toString();
					}if(null != objects[10]){
						riskIcon = objects[10].toString();
					}if(null != objects[11]){
						assessEmpSum = objects[11].toString();
					}
				}else if(objects.length == 13){
					//目标
					if(null != objects[0]){
						id = objects[0].toString();
					}if(null != objects[1]){
						riskParentId = objects[1].toString();
					}if(null != objects[2]){
						riskParentName = objects[2].toString();
					}if(null != objects[3]){
						riskId = objects[3].toString();
					}if(null != objects[4]){
						riskName = objects[4].toString();
					}if(null != objects[5]){
						templateId = objects[5].toString();
					}if(null != objects[6]){
						tempId = objects[6].toString();
					}if(null != objects[7]){
						riskStatusIcon = objects[7].toString();
					}if(null != objects[8]){
						editIdeaId = objects[8].toString();
					}if(null != objects[9]){
						strategyMapId = objects[9].toString();
					}if(null != objects[10]){
						scoreObjectId = objects[10].toString();
					}if(null != objects[11]){
						riskIcon = objects[11].toString();
					}if(null != objects[12]){
						assessEmpSum = objects[12].toString();
					}
				}else if(objects.length == 11){
					//默认
					if(null != objects[0]){
						id = objects[0].toString();
					}if(null != objects[1]){
						riskParentId = objects[1].toString();
					}if(null != objects[2]){
						riskParentName = objects[2].toString();
					}if(null != objects[3]){
						riskId = objects[3].toString();
					}if(null != objects[4]){
						riskName = objects[4].toString();
					}if(null != objects[5]){
						templateId = objects[5].toString();
					}if(null != objects[6]){
						tempId = objects[6].toString();
					}if(null != objects[7]){
						riskStatusIcon = objects[7].toString();
					}if(null != objects[8]){
						editIdeaId = objects[8].toString();
					}if(null != objects[9]){
						riskIcon = objects[9].toString();
					}if(null != objects[10]){
						assessEmpSum = objects[10].toString();
					}
				}
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				if("icon-ibm-symbol-4-sm".equalsIgnoreCase(riskIcon)){
					//高
					map.put("icon", "4");
				}else if("icon-ibm-symbol-5-sm".equalsIgnoreCase(riskIcon)){
					//中
					map.put("icon", "3");
				}else if("icon-ibm-symbol-6-sm".equalsIgnoreCase(riskIcon)){
					//低
					map.put("icon", "2");
				}else if("icon-ibm-symbol-0-sm".equalsIgnoreCase(riskIcon)){
					//无
					map.put("icon", "1");
				}else{
					map.put("icon", "0");
				}
				
				map.put("id", id);
				map.put("riskParentId", riskParentId);
				map.put("riskParentName", riskParentName);
				map.put("riskId", riskId);
				map.put("riskName", riskName);
				map.put("riskIcon", riskIcon);
				map.put("assessEmpSum", assessEmpSum);
				map.put("assessPlanId", assessPlanId);
				map.put("scoreObjectId", scoreObjectId);
				if(!"".equalsIgnoreCase(strategyMapId)){
					map.put("strategyMapId", strategyMapId);
				}
				
				if(riskStatusIcon.equalsIgnoreCase("1")){
					map.put("riskStatus", "icon-status-assess_mr");
				}else if(riskStatusIcon.equalsIgnoreCase("2")){
					map.put("riskStatus", "icon-status-assess_new");
				}
				
				if(!riskStatusIcon.equalsIgnoreCase("2")){
					if(!editIdeaId.equalsIgnoreCase("")){
						map.put("riskStatus", "icon-status-assess_edit");
					}
				}
				
				if(StringUtils.isNotBlank(riskId)){
					Map<String,String> temp = dimScoreMap.get(riskId);
					if(null!=temp){
						map.putAll(temp);
					}
				}
				if(org.apache.commons.lang.StringUtils.isBlank(tempId)){
					map.put("templateId", templateId);
				}else{
					map.put("templateId", tempId);
				}
				if(StringUtils.isNotBlank(query)&&query.matches("\\d*")){
					if(assessEmpSum.contains(query)){
						maps.add(map);
					}
				}else{
					maps.add(map);
				}
			}
		}
        
		//将风险状态灯排序,红、黄、绿
		Collections.sort(maps, new Comparator<Map<String, String>>() {
			@Override
			public int compare(Map<String, String> arg0,
					Map<String, String> arg1) {
				return -arg0.get("icon").compareTo(arg1.get("icon"));
			}
		});
		
        return maps;
	}
	/**
	 * 根据计划ID获得 每个风险事件在相应纬度下的分值
	 * @param assessPlanId 评估计划ID
	 * @return Map<String,Map<String,String>>
	 * @author 金鹏祥
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Map<String,String>> findDimScoreByAssessPlanId(String assessPlanId){
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>(); //数据信息键值
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT score_object_id,score_dim_id,score FROM ");
		sql.append(" T_RM_STATISTICS_RESULT WHERE assess_plan_id = :assessPlanId ORDER BY score_object_id ");
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		for(Object[] object : list){
			if(null!=object[0]){
				Map<String,String> temp = result.get(object[0].toString());
				if(null==temp){
					Map<String,String> map = new HashMap<String,String>();
					if(null == object[2]){
						map.put(object[1].toString(), null);
					}else{
						if(StringUtils.isBlank(object[2].toString())){
							map.put(object[1].toString(), null);
						}else{
							map.put(object[1].toString(), NumberUtil.meg(new Double(object[2].toString()), 2).toString());
						}
					}
					result.put(object[0].toString(), map);
				}else{
					if(null == object[2]){
						temp.put(object[1].toString(), null);
					}else{
						
						if(StringUtils.isBlank(object[2].toString())){
							temp.put(object[1].toString(), null);
						}else{
							temp.put(object[1].toString(), NumberUtil.meg(new Double(object[2].toString()), 2).toString());
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 查询告警状态
	 * @param assessPlanId 评估计划ID
	 * @return String
	 * */
	public String findOneReAlarmScenario(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.id,risk.parent_id,risk.parent_name,risk.id as risk_id,risk.risk_name,risk.template_id,d.temp_id,risk.alarm_scenario ");
		sql.append(" from t_rm_statistics_result  a, t_rm_risks risk , t_rm_risks c ,t_rm_risk_assess_plan d ");
		sql.append(" where a.score_object_id=risk.id and risk.is_risk_class = 're'   and a.assess_plan_id=:assessPlanId ");
		sql.append(" and risk.parent_id=c.id  GROUP BY risk.id ");
      
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String alarmScenario = "";

            
            if(null != objects[7]){
            	alarmScenario = objects[7].toString();
            }
            
            return alarmScenario;
        }
        
        return "";
	}
	

	/** 
	  * @Description: 通过评估计划id 找到风险事件对应的风险分类
	  * @author jia.song@pcitc.com
	  * @date 2017年4月25日 下午2:05:16 
	  * @param node
	  * @param query
	  * @param eventIds
	  * @param nodeMap
	  * @return 
	  */
	public List<Map<String, Object>> getRiskTreeRecordByEventIds(String node, String query,String assessPlanId){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT distinct");
		sql.append(" risks1.ID,risks1.RISK_NAME,risks1.RISK_CODE,risks1.IS_LEAF ");
		sql.append(" FROM ");
		sql.append(" t_rm_risks risks1,t_rm_risk_score_object objects " );
		sql.append(" WHERE ");
		sql.append(" objects.ASSESS_PLAN_ID = :assessPlanId ");
//		sql.append(" AND objects.PARENT_ID = risks1.ID ");
		sql.append(" AND objects.id_seq like concat(risks1.id_seq,'%') ");
		if("root".equals(node)){
			sql.append(" AND risks1.PARENT_ID IS NULL ");
		}else{
			sql.append(" AND risks1.PARENT_ID =  '"+ node +"'");
		}
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		Map map = null;
		if(list != null && list.size() > 0){
			for(Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				map = new HashMap<String, String>();
				if(objects[0] != null){
					map.put("id", objects[0].toString());
					map.put("dbid", objects[0].toString());
				}
				if(objects[1] != null){
					map.put("text", objects[1].toString());
				}
				if(objects[2] != null){
					map.put("code", objects[2].toString());
				}
				if(objects[3] != null){
					boolean isLeaf = "1".equals(objects[3].toString())?true:false;
					map.put("leaf", isLeaf);
				}
				nodes.add(map);
			}
		}
		return nodes;
	}
	
	/** 
	  * @Description: 通过评估计划id 找到风险事件对应的风险分类 security
	  * @author jia.song@pcitc.com
	  * @date 2017年4月25日 下午2:05:16 
	  * @param node
	  * @param query
	  * @param eventIds
	  * @param nodeMap
	  * @return 
	  */
	public List<Map<String, Object>> getRiskTreeRecordByEventIdsForSecurity(String node, String query,String assessPlanId,String companyId){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" distinct risk1.ID,risk1.RISK_NAME,risk1.RISK_CODE,score.RISK_SCORE,risk1.IS_LEAF ");
		sql.append(" FROM ");
		sql.append("  t_rm_risks risk1 left join temp_sync_riskrbs_score score on score.RISK_ID = risk1.ID AND score.ASSESS_PLAN_ID = :assessPlanId ,t_rm_risk_score_object objects " );
		sql.append(" WHERE ");
		sql.append(" objects.id_seq LIKE CONCAT(risk1.id_seq, '%') ");
		sql.append("  AND objects.ASSESS_PLAN_ID = :assessPlanId ");
		sql.append(" AND risk1.IS_RISK_CLASS = 'rbs' ");
		if("root".equals(node)){
			sql.append(" AND risk1.PARENT_ID IS NULL ");
		}else{
			sql.append(" AND risk1.PARENT_ID =  '"+ node +"'");
		}
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
       sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
		Map map = null;
		if(list != null && list.size() > 0){
			for(Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				map = new HashMap<String, String>();
				if(objects[0] != null){
					map.put("id", objects[0].toString());
					map.put("dbid", objects[0].toString());
				}
				if(objects[1] != null){
					map.put("text", objects[1].toString());
				}
				if(objects[2] != null){
					map.put("code", objects[2].toString());
				}
				if(objects[3] != null){
					double riskScore = Double.parseDouble(objects[3].toString());
					map.put("riskScore", Utils.retain2Decimals(riskScore));
					map.put("iconCls", getRiskIconByRiskScore(riskScore,companyId));
				}
				if(objects[4] != null){
					boolean isLeaf = "1".equals(objects[4].toString())?true:false;
					map.put("leaf", isLeaf);
				}
				nodes.add(map);
			}
		}
		return nodes;
	}
	
	/**
	 * 风险事件、分类
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @return JSONArray
	 * */
	@Transactional
	public JSONArray findRiskRbsOrReList(String assessPlanId, String companyId){
		JSONArray array = new JSONArray();
		
		HashMap<String, ArrayList<String>> statisticsResultByScoreObjectIdMapAll = 
				o_summarizinggAtherUtilBO.findStatisticsResultRiskElevelByAssessPlanId(assessPlanId);
		HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
		ArrayList<String> arrayList = null;
		String companyid = UserContext.getUser().getCompanyid();
		Map<String, Risk> riskAllMap = o_risRiskService.getAllRiskInfo(companyid);
		HashMap<String, String> involvesRiskIdMap = o_statisticsResultBO.findStatisticsResultRiskReOrRbsByAssessPlanId(assessPlanId);
		ArrayList<String> dimIdAllList = o_dimensionBO.findDimensionDimIdAllList();
		HashMap<String, RiskAdjustHistory> riskAdjustHistoryAllMap = o_riskAdjustHistoryBO.findRiskAdjustHistoryByRiskIdAllMap(assessPlanId);
		String alarmScenario = "";
		if(null != o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid()).getAlarmScenario()){
			alarmScenario = o_weightSetBO.findWeightSetAll(UserContext.getUser().getCompanyid()).getAlarmScenario().getId();
		}
		
		String riskLevelFormula = "";
		if(null != o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid()).getRiskLevelFormula()){
    		riskLevelFormula = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid()).getRiskLevelFormula();
    	}
		
		
		Set<Entry<String, String>> key2 = involvesRiskIdMap.entrySet();
        for (Iterator<Entry<String, String>> it2 = key2.iterator(); it2.hasNext();) {
        	Entry<String, String> keys = it2.next();
            involvesRiskIdMap.get(keys.getKey());
            String riskId = keys.getKey();
            String isRiskClass = riskAllMap.get(keys.getKey()).getIsRiskClass();
            String riskIcon = "";
            String valueStr = "";
            double value = 0;
            
            if(riskAdjustHistoryAllMap.get(riskId) != null){
            	if(riskAdjustHistoryAllMap.get(riskId).getAdjustType().equalsIgnoreCase("2")){
                	value = riskAdjustHistoryAllMap.get(riskId).getStatus();
                }else{
                	 arrayList = statisticsResultByScoreObjectIdMapAll.get(keys.getKey());
                	 valueStr = o_riskLevelBO.getRisklevel(arrayList, riskLevelFormula, dimIdAllList);
                	 if(valueStr.indexOf("--") == -1){
                		 value = Double.parseDouble(valueStr);
                	 }
                }
                
                JSONObject resultJSON = new JSONObject(); 
                
                if(value != 0){
    	            DictEntry dict = o_kpiBO.findKpiAlarmStatusByValues(
    	        			alarmPlanAllMap.get(alarmScenario), value);
    	    		if(dict != null){
    	    			riskIcon = dict.getValue();
    	    		}
                }
        		
        		resultJSON.put("id", riskId);
                resultJSON.put("type", isRiskClass);
            	resultJSON.put("icon", riskIcon);
            	array.add(resultJSON);
            }
        }
        
        return array;
	}
	
	/**
	 * 风险事件
	 * @param assessPlanId 评估计划ID
	 * @return String
	 * */
	public String findRiskReList(String assessPlanId){
		StringBuffer sql = new StringBuffer();
		
		StringBuffer ids = new StringBuffer();
		
		sql.append(" select o.risk_id,o.id from t_rm_risk_score_object o ");
		sql.append(" where o.assess_plan_id=:assessPlanId ");
      
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		
		for(Object[] o :list){
            String riskId = "";
            if(null != o[0]){
            	riskId = o[0].toString();
            }
            
            //ids += riskId + ",";
            ids = ids.append(riskId + ",");
        }
        
        return ids.toString();
	}
	/**
	 * 根据评估计划ID获得每个风险的评估人数量
	 * @param assessPlanId 评估计划ID
	 * @return Map<String,String>
	 */
	public Map<String,String> getAssessEmpCount(String assessPlanId){
		Map<String,String> map = new HashMap<String,String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT")
		.append(" a.RISK_ID,")
		.append(" COUNT(a.RISK_ID)")
		.append(" FROM")
		.append(" T_RM_RISK_SCORE_OBJECT a,")
		.append(" T_RM_RANG_OBJECT_DEPT_EMP b")
		.append(" WHERE")
		.append(" a.ASSESS_PLAN_ID = :assessPlanId ")
		.append(" AND a.id = b.SCORE_OBJECT_ID")
		.append(" GROUP BY")
		.append(" a.risk_id");
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o : list){
			if(null!=o[0]&&null!=o[1]){
				map.put(o[0].toString(), o[1].toString());
			}
		}
		return map;
	}
	/**
	 * 根据评估计划ID与风险ID获得此条风险的评估人id
	 * @param assessPlanId 评估计划ID
	 * @param riskId 风险ID
	 * @return List<String>
	 */
	public List<String> getAssessEmpIds(String assessPlanId,String riskId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT b.score_emp_id")
		.append(" FROM")
		.append(" T_RM_RISK_SCORE_OBJECT a,")
		.append(" T_RM_RANG_OBJECT_DEPT_EMP b")
		.append(" WHERE")
		.append(" a.ASSESS_PLAN_ID = :assessPlanId")
		.append(" AND a.id = b.SCORE_OBJECT_ID")
		.append(" AND")
		.append(" a.risk_id = :riskId ");
		SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.setParameter("riskId", riskId);
		 @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		List<String> ids = new ArrayList<String>();
		for(Object[] o : list){
			if(null!=o[0]){
				ids.add(o[0].toString());
			}
		}
		return ids;
	}
	
	/**
	 * 查询修改意见内容
	 * @return HashMap<String,EditIdea>
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String,EditIdea> findEditIdeaMapAll(){
		HashMap<String, EditIdea> map = new HashMap<String, EditIdea>();
		try {
			Criteria criteria = o_editIdeaDAO.createCriteria();
			List<EditIdea> list = criteria.list();
			
			for (EditIdea editIdea : list) {
				map.put(editIdea.getId(), editIdea);
			}
		} catch (Exception e) {
		}
		return map;
	}
	
	/**
	 * 通过评估计划ID,风险ID得到所有评估人相关信息
	 * @param assessPlanId 评估计划ID
	 * @param riskId 风险ID
	 * @param companyId 公司ID
	 * */
	@RecordLog("查询评估人评价风险信息")
	public List<Map<String, Object>> findRiskByAssessPlanIdAndRiskId(String scoreObjId, String companyId){
		List<Map<String,Object>> rtnList = new ArrayList<Map<String,Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rang.id,sysemp.EMP_NAME,sysorg.ORG_NAME,org.ETYPE,ideas.EDIT_IDEA_CONTENT as idea,responses.EDIT_IDEA_CONTENT as response,obj.RISK_NAME ");
		sql.append(" FROM t_rm_risk_score_object obj, t_rm_risk_org_temp org, t_rm_rang_object_dept_emp rang LEFT JOIN t_rm_edit_idea ideas ON rang.id = ideas.OBJECT_DEPT_EMP_ID ");
        sql.append(" LEFT JOIN  t_rm_edit_response responses ON rang.id = responses.OBJECT_DEPT_EMP_ID,t_sys_organization sysorg, t_sys_employee sysemp ");
		sql.append(" WHERE obj.id = org.OBJECT_ID AND rang.SCORE_DEPT_ID = org.id AND rang.SCORE_EMP_ID = sysemp.id AND org.org_id = sysorg.id AND obj.id =:scoreObjId ");
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("scoreObjId", scoreObjId);
		List<Object[]> list = sqlQuery.list();
        String rangId = null;
        String empName = null;
        String orgName = null;
        String etype = null;
        String idea = null;
        String response = null;
        String riskName = null;
        Map<String, Object> map = null;
        
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            map = new HashMap<String, Object>();
            if(null != objects[0]){
            	rangId = objects[0].toString();
            }if(null != objects[1]){
            	empName = objects[1].toString();
            }if(null != objects[2]){
            	orgName = objects[2].toString();
            }if(null != objects[3]){
            	etype = objects[3].toString();
            }if(null != objects[4]){
            	idea = objects[4].toString();
            }if(null != objects[5]){
            	response = objects[5].toString();
            }if(null != objects[6]){
            	riskName = objects[6].toString();
            }
            
            map.put("rangId", rangId);
            map.put("empName", empName);
            map.put("orgName", orgName);
            map.put("typeName" ,  "M".equals(etype) ? "主责" : "相关");
            map.put("idea", idea);
            map.put("response", response);
            map.put("riskName", riskName);
            rtnList.add(map);
        }
        return rtnList;
	}
	
	/***
	 * 通过工作流任务ID查询
	 * @param taskId 工作流任务ID
	 * @return String
	 * */
	@SuppressWarnings("unchecked")
	public String findjbpm4Task(String taskId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select DBID_,CLASS_ FROM jbpm4_task where DBID_ = :taskId ");
        SQLQuery sqlQuery = o_statisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("taskId", taskId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String dbid = "";
            
            if(null != objects[0]){
            	dbid = objects[0].toString();
            }
            
            return dbid;
        }
        
        return "";
	}
}