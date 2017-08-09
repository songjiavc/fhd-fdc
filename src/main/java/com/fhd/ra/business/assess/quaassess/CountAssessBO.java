package com.fhd.ra.business.assess.quaassess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.assess.kpiSet.RangObjectDeptEmpDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreResultBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.ra.business.risk.TemplateBO;

@Service
public class CountAssessBO {

	@Autowired
	private RangObjectDeptEmpDAO o_rangObjectDeptEmpDAO;
	
	@Autowired
	private ShowAssessBO o_showAssessBO;
	
	@Autowired
	private QuaAssessNextBO o_quaAssessNextBO;
	
	@Autowired
	private ScoreResultBO o_scoreResultBO;
	
	@Autowired
	private TemplateBO o_templateBO;
	
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	/**
	 * 通过风险ID集合查询已评估风险总数、未评估总数、已评估总数
	 * @param scoreEmpId 人员ID
	 * @param params 风险信息参数
	 * @param assessPlanId 评估计划ID
	 * @return HashMap<String, Integer>
	 * @author 金鹏祥
	 */
	public HashMap<String, Integer> getAssessCountMap(String scoreEmpId, String params, String assessPlanId) {
		HashMap<String, String> dicByAssessPlanIdMapAll = o_scoreResultBO.findDicByAssessPlanIdMapAll(assessPlanId, scoreEmpId);
		HashMap<String, Integer> countMap = new HashMap<String, Integer>(); //组织信息键值
		int isAssessCount = 0; //风险评价数
		int isNotAssessCount = 0; //风险未评价数
		int totalCount = 0; //评价风险总数
		String templateId = ""; //模板ID
		ArrayList<String> dimArrayList = null; //动态维度集合列表
		JSONArray jsonarr = JSONArray.fromObject(params); //风险参数信息数组
		String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId(); //系统模板ID
		HashMap<String, HashMap<String, TemplateRelaDimension>> templateRelaDimensionParentIdIsNullMapAll = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionParentIdIsNull();
		HashMap<String, String> dicByAssessPlanIdMapAllNew = null; //打分结果
		String templateType = o_showAssessBO.getTemplateId(assessPlanId); //评估模板类型
		Map<String, Risk> riskAllMap = null; //风险实体键值
		String companyId = UserContext.getUser().getCompanyid(); //公司ID
		
		if("dim_template_type_custom".equalsIgnoreCase(templateType)){
			//自定义模板
			riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
		}else{
			//其他模板
			templateId = o_assessPlanBO.findRiskAssessPlanById(assessPlanId).getTemplate().getId();
			if(null == dimArrayList){
				dimArrayList = o_quaAssessNextBO.getDimByTemplatesId(templateId, templateRelaDimensionParentIdIsNullMapAll);
			}
			
			dicByAssessPlanIdMapAllNew = o_scoreResultBO.getDicByAssessPlanIdMapAll(dicByAssessPlanIdMapAll, templateId);
		}
		
		for (Object objects : jsonarr) {
			JSONObject jsobjs = (JSONObject) objects;
			String riskId = jsobjs.get("riskId").toString();
			String rangObjectDeptEmpId = jsobjs.get("rangObjectDeptEmpId").toString();
			boolean isScore = true;
			
			if("dim_template_type_custom".equalsIgnoreCase(templateType)){
				//自定义模板
				Template template = riskAllMap.get(riskId).getTemplate();
				if(template != null){
					templateId = template.getId();
				}else{
					templateId = tempSys;
				}
				
				dimArrayList = o_quaAssessNextBO.getDimByTemplatesId(templateId, templateRelaDimensionParentIdIsNullMapAll);
				dicByAssessPlanIdMapAllNew = o_scoreResultBO.getDicByAssessPlanIdMapAll(dicByAssessPlanIdMapAll, templateId);
			}
			
			//此模板没有维度
			if(dimArrayList.size() == 0){
				isNotAssessCount = jsonarr.size();
			}
			
			//遍历此模板下维度是否打分
			for (String dim : dimArrayList) {
				if(dicByAssessPlanIdMapAllNew.get(rangObjectDeptEmpId + "--" + riskId + "--" + dim) == null){
					isScore = false;
				}
			}
			
			if(isScore){
				isAssessCount++;
			}else{
				isNotAssessCount++;
			}
			
			totalCount++;
		}
		
		countMap.put("isAssessCount", isAssessCount);
		countMap.put("isNotAssessCount", isNotAssessCount);
		countMap.put("totalCount", totalCount);
		
		return countMap;
	}
	/**
	 * 根据评估计划ID和 类型获得风险数量
	 * type:{
	 * 	1: “一级风险分类数量”，
	 * 	2：“二级风险分类数量”，
	 * 	3：“三级风险分类数量”
	 *  4：“风险事件数量”
	 * }
	 * @author 邓广义
	 * @return
	 */
	public Map<String,Object> getRiskNumByAssessPlanIdAndType(String assessPlanId,int type){
		Map<String,Object> map = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(assessPlanId)){
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(*) FROM T_RM_RISKS r");
			sql.append(" LEFT JOIN T_RM_RISK_SCORE_OBJECT o");
			sql.append(" on o.RISK_ID = r.ID");
			sql.append(" LEFT JOIN T_RM_RISK_ASSESS_PLAN p ");
			sql.append(" on p.ID = o.ASSESS_PLAN_ID ");
			sql.append(" WHERE p.ID = ? ");
			String typeS = "";
			switch (type){
			case 1:typeS = "1"; sql.append(" AND r.ELEVEL = ?");
			break;
			case 2:typeS = "2"; sql.append(" AND r.ELEVEL = ?");
			break;
			case 3:typeS = "3"; sql.append(" AND r.ELEVEL = ?");
			break;
			case 4:typeS = "re"; sql.append(" AND r.IS_RISK_CLASS = ? ");
			}
			SQLQuery countQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
			countQuery.setString(0, assessPlanId);
			countQuery.setString(0, typeS);
			map.put("totalCount", countQuery.uniqueResult());
			return  map;
		}
		return map;
	}
	/**
	 * 风险分类分析饼图
	 * 查询风险分类的数量（一级分类）
	 * @return List<Object []>
	 */
	@SuppressWarnings("unchecked")
	public List<Object []> getRiskCategoryNum(){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT	r.RISK_NAME, COUNT(*)")
		.append(" FROM T_RM_RISKS r ")
		.append(" LEFT JOIN  T_RM_RISK_SCORE_OBJECT o ")
		.append(" ON o.RISK_ID = r.ID ")
		.append(" LEFT JOIN T_RM_RISK_ASSESS_PLAN p  ")
		.append(" on p.ID = o.ASSESS_PLAN_ID  ")
		.append(" GROUP BY r.RISK_NAME, r.ELEVEL")
		.append(" HAVING  r.ELEVEL = ? ");
		SQLQuery sqlquery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
		sqlquery.setString(0, "1");
		return sqlquery.list();
	}
}