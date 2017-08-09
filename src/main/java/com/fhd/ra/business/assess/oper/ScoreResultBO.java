package com.fhd.ra.business.assess.oper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.quaAssess.ScoreResultDAO;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.Score;
import com.fhd.entity.risk.TemplateRelaDimension;

@Service
public class ScoreResultBO {
	
	@Autowired
	private ScoreResultDAO o_scoreResultDAO;
	
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	/**
	 * 保存更新打分结果
	 * @param rangObjectDeptEmp 综合实体
	 * @author 金鹏祥
	 * */
	@Transactional
	public void mergeScoreResult(ScoreResult scoreResult) {
		o_scoreResultDAO.merge(scoreResult);
	}
	
	/**
	 * 查询该评估计划下的打分结果 -- 通过评估人
	 * @param assessPlanId 评估计划ID
	 * @param scoreEmpId 人员ID
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<ScoreResult>> findScoreResultByAssessPlanIdAllMap(String assessPlanId, String scoreEmpId){
		HashMap<String, ArrayList<ScoreResult>> maps = new HashMap<String, ArrayList<ScoreResult>>();
		ArrayList<ScoreResult> lists = null;
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.ID,a.SCORE_DIM_ID,a.SCORE_DIC_ID,a.RANG_OBJECT_DEPT_EMP_ID," +
				"a.SUBMIT_TIME,a.IS_APPROVAL,a.STATUS  st  from t_rm_score_result a, " +
				"t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :assessPlanId and b.score_emp_id = :scoreEmpId ");
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.setParameter("scoreEmpId", scoreEmpId);
		List<ScoreResult> scoreResultList = new ArrayList<ScoreResult>();
		ScoreResult scoreResult = null;
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String id = "";
        	String dimId = "";
        	String dicId = "";
        	String objeId = "";
        	String submitTime = "";
        	
        	if(null != objects[0]){
        		id = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	objeId = objects[3].toString();
            }if(null != objects[4]){
            	submitTime = objects[4].toString();
            }
            
        	try {
        		scoreResult = new ScoreResult();
            	scoreResult.setId(id);
            	Dimension dim = new Dimension();
            	dim.setId(dimId);
            	scoreResult.setDimension(dim);
            	Score score = new Score();
            	score.setId(dicId);
            	scoreResult.setScore(score);
            	RangObjectDeptEmp obje = new RangObjectDeptEmp();
            	obje.setId(objeId);
            	scoreResult.setRangObjectDeptEmpId(obje);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Date date = sdf.parse(submitTime);
            	scoreResult.setSubmitTime(date);
            	scoreResult.setApproval(false);
            	scoreResult.setStatus(null);
            	
            	scoreResultList.add(scoreResult);
			} catch (Exception e) {
			}
        }
		
		for (ScoreResult scoreResults : scoreResultList) {
			if(maps.get(scoreResults.getRangObjectDeptEmpId().getId()) != null){
				maps.get(scoreResults.getRangObjectDeptEmpId().getId()).add(scoreResults);
			}else{
				lists = new ArrayList<ScoreResult>();
				lists.add(scoreResults);
				maps.put(scoreResults.getRangObjectDeptEmpId().getId(), lists);
			}
		}
		
		return maps;
	}
	
	/**
	 * 查询该评估计划下的打分结果 --部门评估人
	 * @param assessPlanId 评估计划ID
	 * @param 人员ID集合
	 * @return HashMap<String, ArrayList<ScoreResult>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<ScoreResult>> findScoreResultByAssessPlanIdAllMap(String assessPlanId, ArrayList<String> scoreEmpIdList){
		HashMap<String, ArrayList<ScoreResult>> maps = new HashMap<String, ArrayList<ScoreResult>>();
		ArrayList<ScoreResult> lists = null;
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.ID,a.SCORE_DIM_ID,a.SCORE_DIC_ID,a.RANG_OBJECT_DEPT_EMP_ID," +
				"a.SUBMIT_TIME,a.IS_APPROVAL,a.STATUS  st  from t_rm_score_result " +
				"a, t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :assessPlanId ");
		sql.append(" and b.score_emp_id in (:scoreEmpIdList) ");
		
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.setParameterList("scoreEmpIdList", scoreEmpIdList);
		
		List<ScoreResult> scoreResultList = new ArrayList<ScoreResult>();
		ScoreResult scoreResult = null;
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String id = ""; //打分ID
        	String dimId = ""; //维度ID
        	String dicId = ""; //分值ID
        	String objeId = ""; //综合打分ID
        	String submitTime = ""; //提交时间
        	
        	if(null != objects[0]){
        		id = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	objeId = objects[3].toString();
            }if(null != objects[4]){
            	submitTime = objects[4].toString();
            }
            
        	try {
        		scoreResult = new ScoreResult();
            	scoreResult.setId(id);
            	Dimension dim = new Dimension();
            	dim.setId(dimId);
            	scoreResult.setDimension(dim);
            	Score score = new Score();
            	score.setId(dicId);
            	scoreResult.setScore(score);
            	RangObjectDeptEmp obje = new RangObjectDeptEmp();
            	obje.setId(objeId);
            	scoreResult.setRangObjectDeptEmpId(obje);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Date date = sdf.parse(submitTime);
            	scoreResult.setSubmitTime(date);
            	scoreResult.setApproval(false);
            	scoreResult.setStatus(null);
            	
            	scoreResultList.add(scoreResult);
			} catch (Exception e) {
			}
        }
		
		for (ScoreResult scoreResults : scoreResultList) {
			if(maps.get(scoreResults.getRangObjectDeptEmpId().getId()) != null){
				maps.get(scoreResults.getRangObjectDeptEmpId().getId()).add(scoreResults);
			}else{
				lists = new ArrayList<ScoreResult>();
				lists.add(scoreResults);
				maps.put(scoreResults.getRangObjectDeptEmpId().getId(), lists);
			}
		}
		
		return maps;
	}
	
	/**
	 * 查询该评估计划下的打分结果 -- 通过风险ID
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<ScoreResult>> findScoreResultByAssessPlanIdAndRiskIdAllMap(String assessPlanId, String riskId){
		HashMap<String, ArrayList<ScoreResult>> maps = new HashMap<String, ArrayList<ScoreResult>>();
		ArrayList<ScoreResult> lists = null;
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.ID,a.SCORE_DIM_ID,a.SCORE_DIC_ID,a.RANG_OBJECT_DEPT_EMP_ID,a.SUBMIT_TIME,a.IS_APPROVAL,a.STATUS  st  from t_rm_score_result a, t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :assessPlanId and c.risk_id = :riskId ");
		
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.setParameter("riskId", riskId);
		
		List<ScoreResult> scoreResultList = new ArrayList<ScoreResult>();
		ScoreResult scoreResult = null;
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String id = "";
        	String dimId = "";
        	String dicId = "";
        	String objeId = "";
        	String submitTime = "";
        	
        	if(null != objects[0]){
        		id = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	objeId = objects[3].toString();
            }if(null != objects[4]){
            	submitTime = objects[4].toString();
            }
            
        	try {
        		scoreResult = new ScoreResult();
            	scoreResult.setId(id);
            	Dimension dim = new Dimension();
            	dim.setId(dimId);
            	scoreResult.setDimension(dim);
            	Score score = new Score();
            	score.setId(dicId);
            	scoreResult.setScore(score);
            	RangObjectDeptEmp obje = new RangObjectDeptEmp();
            	obje.setId(objeId);
            	scoreResult.setRangObjectDeptEmpId(obje);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Date date = sdf.parse(submitTime);
            	scoreResult.setSubmitTime(date);
            	scoreResult.setApproval(false);
            	scoreResult.setStatus(null);
            	
            	scoreResultList.add(scoreResult);
			} catch (Exception e) {
			}
        }
		
		for (ScoreResult scoreResults : scoreResultList) {
			if(maps.get(scoreResults.getRangObjectDeptEmpId().getId()) != null){
				maps.get(scoreResults.getRangObjectDeptEmpId().getId()).add(scoreResults);
			}else{
				lists = new ArrayList<ScoreResult>();
				lists.add(scoreResults);
				maps.put(scoreResults.getRangObjectDeptEmpId().getId(), lists);
			}
		}
		
		return maps;
	}
	
	/**
	 * 查询该评估计划下的打分结果 -- 通过风险ID
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<ScoreResult>> findScoreResultByAssessPlanIdAllMap(String assessPlanId){
		HashMap<String, ArrayList<ScoreResult>> maps = new HashMap<String, ArrayList<ScoreResult>>();
		ArrayList<ScoreResult> lists = null;
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.ID,a.SCORE_DIM_ID,a.SCORE_DIC_ID,a.RANG_OBJECT_DEPT_EMP_ID,a.SUBMIT_TIME,a.IS_APPROVAL,a.STATUS  st  from t_rm_score_result a, t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :assessPlanId ");
		
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		
		List<ScoreResult> scoreResultList = new ArrayList<ScoreResult>();
		ScoreResult scoreResult = null;
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String id = "";
        	String dimId = "";
        	String dicId = "";
        	String objeId = "";
        	String submitTime = "";
        	
        	if(null != objects[0]){
        		id = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	objeId = objects[3].toString();
            }if(null != objects[4]){
            	submitTime = objects[4].toString();
            }
            
        	try {
        		scoreResult = new ScoreResult();
            	scoreResult.setId(id);
            	Dimension dim = new Dimension();
            	dim.setId(dimId);
            	scoreResult.setDimension(dim);
            	Score score = new Score();
            	score.setId(dicId);
            	scoreResult.setScore(score);
            	RangObjectDeptEmp obje = new RangObjectDeptEmp();
            	obje.setId(objeId);
            	scoreResult.setRangObjectDeptEmpId(obje);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Date date = sdf.parse(submitTime);
            	scoreResult.setSubmitTime(date);
            	scoreResult.setApproval(false);
            	scoreResult.setStatus(null);
            	
            	scoreResultList.add(scoreResult);
			} catch (Exception e) {
			}
        }
		
		for (ScoreResult scoreResults : scoreResultList) {
			if(maps.get(scoreResults.getRangObjectDeptEmpId().getId()) != null){
				maps.get(scoreResults.getRangObjectDeptEmpId().getId()).add(scoreResults);
			}else{
				lists = new ArrayList<ScoreResult>();
				lists.add(scoreResults);
				maps.put(scoreResults.getRangObjectDeptEmpId().getId(), lists);
			}
		}
		
		return maps;
	}
	
	/**
	 * 查询该评估计划下的打分结果
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<ScoreResult>> findScoreResultByAssessPlanIdQueryAllMap(String planIdQuery){
		HashMap<String, ArrayList<ScoreResult>> maps = new HashMap<String, ArrayList<ScoreResult>>();
		ArrayList<ScoreResult> lists = null;
		StringBuffer sql = new StringBuffer();
		HashMap<String, String> tempMap = new HashMap<String, String>();
//		sql.append(" select a.ID,a.SCORE_DIM_ID,a.SCORE_DIC_ID,a.RANG_OBJECT_DEPT_EMP_ID," +
//				"a.SUBMIT_TIME,a.IS_APPROVAL,a.STATUS  st,c.assess_plan_id from t_rm_score_result a, " +
//				"t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
//		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
//		sql.append(" b.SCORE_OBJECT_ID = c.ID");
		
		sql.append(" select a.ID,a.SCORE_DIM_ID,a.SCORE_DIC_ID,a.RANG_OBJECT_DEPT_EMP_ID," +
				"a.SUBMIT_TIME,a.IS_APPROVAL,a.STATUS  st,c.assess_plan_id  from t_rm_score_result a, " +
				"t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :planIdQuery ");
		
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("planIdQuery", planIdQuery);
		List<ScoreResult> scoreResultList = new ArrayList<ScoreResult>();
		ScoreResult scoreResult = null;
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String id = "";
        	String dimId = "";
        	String dicId = "";
        	String objeId = "";
        	String submitTime = "";
        	String assessPlanId = "";
        	
        	if(null != objects[0]){
        		id = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	objeId = objects[3].toString();
            }if(null != objects[4]){
            	submitTime = objects[4].toString();
            }if(null != objects[7]){
            	assessPlanId = objects[7].toString();
            }
            
        	try {
        		scoreResult = new ScoreResult();
            	scoreResult.setId(id);
            	Dimension dim = new Dimension();
            	dim.setId(dimId);
            	scoreResult.setDimension(dim);
            	Score score = new Score();
            	score.setId(dicId);
            	scoreResult.setScore(score);
            	RangObjectDeptEmp obje = new RangObjectDeptEmp();
            	obje.setId(objeId);
            	scoreResult.setRangObjectDeptEmpId(obje);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Date date = sdf.parse(submitTime);
            	scoreResult.setSubmitTime(date);
            	scoreResult.setApproval(false);
            	scoreResult.setStatus(null);
            	tempMap.put(id, assessPlanId);
            	
            	scoreResultList.add(scoreResult);
			} catch (Exception e) {
			}
        }
		
		for (ScoreResult scoreResults : scoreResultList) {
			if(maps.get(scoreResults.getRangObjectDeptEmpId().getId() + "--" + tempMap.get(scoreResults.getId())) != null){
				maps.get(scoreResults.getRangObjectDeptEmpId().getId() + "--" + tempMap.get(scoreResults.getId())).add(scoreResults);
			}else{
				lists = new ArrayList<ScoreResult>();
				lists.add(scoreResults);
				maps.put(scoreResults.getRangObjectDeptEmpId().getId() + "--" + tempMap.get(scoreResults.getId()), lists);
			}
		}
		
		return maps;
	}
	
	/**
	 * 查询该评估计划下的打分结果,按评估人过滤
	 * @param assessPlanId 评估计划ID
	 * @param scoreEmpId 人员ID
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findDicByAssessPlanIdMapAll(String assessPlanId, String scoreEmpId){
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.rang_object_dept_emp_id,a.score_dim_id,a.score_dic_id,c.risk_id" +
				" from t_rm_score_result a, t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :assessPlanId and b.SCORE_EMP_ID = :scoreEmpId ");
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		sqlQuery.setParameter("scoreEmpId", scoreEmpId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String rangObjectDeptEmpId = "";
        	String dimId = ""; //维度ID
        	String dicId = ""; //分值ID
        	String riskId = ""; //风险ID
        	
        	if(null != objects[0]){
        		rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	riskId = objects[3].toString();
            }
            
            map.put(rangObjectDeptEmpId + "--" + riskId + "--" + dimId, dicId);
        }
        
		return map;
	}
	
	/**
	 * 查询该评估计划下的打分结果(New)
	 * */
	public HashMap<String, String> getDicByAssessPlanIdMapAll(HashMap<String, String> dicByAssessPlanIdMapAll, String tempId){
		HashMap<String, String> dicByAssessPlanIdMapAllNew = new HashMap<String, String>();
		HashMap<String, HashMap<String, TemplateRelaDimension>> templateRelaDimensionMapByDimAll = o_templateRelaDimensionBO.findTemplateRelaDimensionMapByDimAll();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionAllMap = o_templateRelaDimensionBO.findTemplateRelaDimensionAllMap();
		HashMap<String, TemplateRelaDimension> templateRelaDimensionMapByDim = templateRelaDimensionMapByDimAll.get(tempId);
		
		Set<Entry<String, String>> key = dicByAssessPlanIdMapAll.entrySet();
		for (Iterator<Entry<String, String>> it = key.iterator(); it.hasNext();) {
			Entry<String, String> id = it.next();
			String dicId = dicByAssessPlanIdMapAll.get(id.getKey());
			String str[] = id.getKey().split("--");
			if(null != str[2]){
				if(null != templateRelaDimensionMapByDim.get(str[2].toString())){
					if(null != templateRelaDimensionMapByDim.get(str[2].toString()).getParent()){
						String idStr = str[0].toString() + "--" + str[1].toString() + "--" + 
									templateRelaDimensionAllMap.get(
											templateRelaDimensionMapByDim.get(str[2].toString()).getParent().getId()).getDimension().getId();
						dicByAssessPlanIdMapAllNew.put(idStr, dicId);
					}else{
						dicByAssessPlanIdMapAllNew.put(id.getKey(), dicId);
					}
				}
			}
		}
		
		return dicByAssessPlanIdMapAllNew;
	}
	
	/**
	 * 查询该评估计划下的打分结果
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findDicByAssessPlanIdMapAll(String assessPlanId){
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.rang_object_dept_emp_id,a.score_dim_id,a.score_dic_id,c.risk_id" +
				" from t_rm_score_result a, t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :assessPlanId ");
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String rangObjectDeptEmpId = "";
        	String dimId = "";
        	String dicId = "";
        	String riskId = "";
        	
        	if(null != objects[0]){
        		rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	riskId = objects[3].toString();
            }
            
            map.put(rangObjectDeptEmpId + "--" + riskId + "--" + dimId, dicId);
        }
        
		return map;
	}
	
	/**
	 * 查询打分结果全部信息以MAP方式存储
	 * @param assessPlanId 评估计划ID
	 * @author 金鹏祥
	 * @return String
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ScoreResult> findScoreResultAllMap(String assessPlanId){
		HashMap<String, ScoreResult> map = new HashMap<String, ScoreResult>();
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select a.ID,a.SCORE_DIM_ID,a.SCORE_DIC_ID,a.RANG_OBJECT_DEPT_EMP_ID,a.SUBMIT_TIME,a.IS_APPROVAL,a.STATUS  st " +
				" from t_rm_score_result a, t_rm_rang_object_dept_emp b, t_rm_risk_score_object c where  ");
		sql.append(" a.RANG_OBJECT_DEPT_EMP_ID = b.id and  ");
		sql.append(" b.SCORE_OBJECT_ID = c.ID AND ");
		sql.append(" c.assess_plan_id = :assessPlanId ");
		SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("assessPlanId", assessPlanId);
		List<ScoreResult> scoreResultList = new ArrayList<ScoreResult>();
		ScoreResult scoreResult = null;
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String id = ""; //ID
        	String dimId = ""; //维度ID
        	String dicId = ""; //分值ID
        	String objeId = ""; //批准ID
        	String submitTime = "";//提交时间
        	
        	if(null != objects[0]){
        		id = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	dicId = objects[2].toString();
            }if(null != objects[3]){
            	objeId = objects[3].toString();
            }if(null != objects[4]){
            	submitTime = objects[4].toString();
            }
            
        	try {
        		scoreResult = new ScoreResult();
            	scoreResult.setId(id);
            	Dimension dim = new Dimension();
            	dim.setId(dimId);
            	scoreResult.setDimension(dim);
            	Score score = new Score();
            	score.setId(dicId);
            	scoreResult.setScore(score);
            	RangObjectDeptEmp obje = new RangObjectDeptEmp();
            	obje.setId(objeId);
            	scoreResult.setRangObjectDeptEmpId(obje);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Date date = sdf.parse(submitTime);
            	scoreResult.setSubmitTime(date);
            	scoreResult.setApproval(false);
            	scoreResult.setStatus(null);
            	
            	scoreResultList.add(scoreResult);
			} catch (Exception e) {
			}
        }
		
        for (ScoreResult scoreResults : scoreResultList) {
			if(scoreResults.getRangObjectDeptEmpId() != null){
				map.put(scoreResults.getScore().getId() + "--" + scoreResults.getRangObjectDeptEmpId().getId(), scoreResults);
			}
		}
        
		return map;
	}
	
	/**
	 * 通过综合ID查询打分结果
	 * */
	@SuppressWarnings("unchecked")
	public List<ScoreResult> findScoreResultByRangObjectDeptEmpId(String rangObjectDeptEmpId[]){
		Criteria criteria = o_scoreResultDAO.createCriteria();
		criteria.add(Restrictions.in("rangObjectDeptEmpId.id", rangObjectDeptEmpId));
		return criteria.list();
	}
	
	/**
	 * 查询打分结果维度分值所有数据过滤部门下人员
	 * @param assessPlanId 评估计划ID
	 * @param scoreEmpIdArrayList 人员ID集合
	 * @return ArrayList<ArrayList<String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<ArrayList<String>> findScoreResultAllList(String assessPlanId, ArrayList<String> scoreEmpIdArrayList) {
		StringBuffer sql = new StringBuffer();
        
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
        
        sql.append(" select a.rang_object_dept_emp_id,a.score_dim_id,c.score_dim_name, b.score_dic_value ");
        sql.append(" from t_rm_score_result a, t_dim_score_dic b ,T_DIM_SCORE_DIM c,t_rm_rang_object_dept_emp d, t_rm_risk_score_object e ");
        sql.append(" where a.score_dic_id = b.id and c.id=a.score_dim_id and " +
        		"a.RANG_OBJECT_DEPT_EMP_ID = d.id and d.SCORE_EMP_ID in (:scoreEmpIdArrayList) ");
        sql.append(" and e.id = d.SCORE_OBJECT_ID and e.ASSESS_PLAN_ID = :assessPlanId ");
        SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("scoreEmpIdArrayList", scoreEmpIdArrayList);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String scoreDimId = "";//维度ID
            String scoreDimName = "";//维度名称
            String scoreDicValue = "";//维度分值
            
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	scoreDimId = objects[1].toString();
            }if(null != objects[2]){
            	scoreDimName = objects[2].toString();
            }if(null != objects[3]){
            	scoreDicValue = objects[3].toString();
            }
            ArrayList<String> array = new ArrayList<String>();
            array.add(rangObjectDeptEmpId);
            array.add(scoreDimId);
            array.add(scoreDimName);
            array.add(scoreDicValue);
            arrayList.add(array);
        }
        
        return arrayList;
	}
	
	/**
	 * 查询打分结果维度分值所有数据
	 * @param assessPlanId 评估计划ID
	 * @return ArrayList<ArrayList<String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<ArrayList<String>> findScoreResultAllList(String assessPlanId) {
		StringBuffer sql = new StringBuffer();
        
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
        
        sql.append(" select a.rang_object_dept_emp_id,a.score_dim_id,c.score_dim_name, b.score_dic_value ");
        sql.append(" from t_rm_score_result a, t_dim_score_dic b ,T_DIM_SCORE_DIM c, t_rm_rang_object_dept_emp d, t_rm_risk_score_object e ");
        sql.append(" where a.score_dic_id = b.id and c.id=a.score_dim_id and d.id = a.RANG_OBJECT_DEPT_EMP_ID ");
        sql.append(" and d.SCORE_OBJECT_ID = e.ID and e.ASSESS_PLAN_ID = :assessPlanId ");
      
        SQLQuery sqlQuery = o_scoreResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String rangObjectDeptEmpId = "";//范围-对象-部门-人员综合表ID
            String scoreDimId = "";//维度ID
            String scoreDimName = "";//维度名称
            String scoreDicValue = "";//维度分值
            
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	scoreDimId = objects[1].toString();
            }if(null != objects[2]){
            	scoreDimName = objects[2].toString();
            }if(null != objects[3]){
            	scoreDicValue = objects[3].toString();
            }
            ArrayList<String> array = new ArrayList<String>();
            array.add(rangObjectDeptEmpId);
            array.add(scoreDimId);
            array.add(scoreDimName);
            array.add(scoreDicValue);
            arrayList.add(array);
        }
        
        return arrayList;
	}
}