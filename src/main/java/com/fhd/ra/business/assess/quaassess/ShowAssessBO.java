package com.fhd.ra.business.assess.quaassess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.formula.StatisticFunctionCalculateBO;
import com.fhd.dao.assess.kpiSet.RangObjectDeptEmpDAO;
import com.fhd.dao.risk.TemplateRelaDimensionDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.NumberUtil;
import com.fhd.ra.business.assess.approval.ApplyContainer;
import com.fhd.ra.business.assess.approval.AssessDateInitBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.ScoreBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.assess.util.DynamicDim;
import com.fhd.ra.business.assess.util.RiskStatusBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.sm.web.controller.util.Utils;
import edu.emory.mathcs.backport.java.util.Collections;

@Service
public class ShowAssessBO {
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	@Autowired
	private RiskStatusBO o_riskStatusBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private RangObjectDeptEmpDAO o_rangObjectDeptEmpDAO;
	
	@Autowired
	private ScoreBO o_scoreBO;
	
	@Autowired
	private TemplateRelaDimensionDAO o_templateRelaDimensionDAO;
	
	@Autowired
	private TemplateBO o_templateBO;
	
	@Autowired
	private AssessDateInitBO o_assessDateInitBO;
	
	/**
	 * 得到风险水平分(已经不用)
	 * @param scoreDicValue1 维度1
	 * @param scoreDicValue2 维度2
	 * @return double
	 * @author 金鹏祥
	 * */
	public double getRiskScore(double scoreDicValue1, double scoreDicValue2){
		double riskScoreValue = 0l;
		riskScoreValue = scoreDicValue1 * scoreDicValue2;
		return riskScoreValue;
	}
	
	/**
	 * 最大值存储
	 * @param scoreValueList 分值集合
	 * @return double
	 * @author 金鹏祥
	 * */
	private double getMaxValue(ArrayList<Double> scoreValueList){
		double scoreValueFinal = 0l;
        double[] scoreValues = new double[scoreValueList.size()];
        
        for (int j = 0; j < scoreValueList.size(); j++) {
        	scoreValues[j] = scoreValueList.get(j);
		}
        scoreValueFinal = StatisticFunctionCalculateBO.max(scoreValues);
        
        if("NaN".equalsIgnoreCase(String.valueOf(scoreValueFinal))){
    		scoreValueFinal = 0;
    	}
        
        return scoreValueFinal;
	}
	
	/**
	 * 查询模版关联维度(主维度)
	 * @param tempLateId 模板ID
	 * @return List<Map<String, Object>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> findTemplateRelaDimensionIsParentIdIsNullAllMapObj(String tempLateId){
		HashMap<String, List<Double>> maps = o_scoreBO.findDicAllMap();
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionParentIdAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullInfoAllMap();
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>(); //组织信息键值
		String templateType = tempLateId; //模板ID
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		criteria.add(Restrictions.isNull("parent.id"));
		
		if(templateType.equalsIgnoreCase("dim_template_type_custom")){
			//自定义模板
			criteria.createAlias("dimension", "dimension");
			ProjectionList projList = Projections.projectionList(); 
			criteria.addOrder(Order.asc("dimension.sort"));
			projList.add(Projections.groupProperty("dimension.id")); 
			projList.add(Projections.property("dimension.name")); 
			projList.add(Projections.property("id")); 
			criteria.setProjection(projList); 
		}else{
			//其他模板
			criteria.createAlias("dimension", "dimension");
			criteria.add(Restrictions.eq("template.id", tempLateId));
			criteria.addOrder(Order.asc("dimension.sort"));
		}
		
		if(templateType.equalsIgnoreCase("dim_template_type_custom")){
			//自定义模板
			List list = null;
			list = criteria.list();
			for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
	            Object[] objects = (Object[]) iterator.next();
	           
	           
	            String dimId = ""; //维度ID
	            String dimName = ""; //维度名称
	            String id = ""; //ID
	            
	            if(null != objects[0]){
	            	dimId = objects[0].toString();
	            }if(null != objects[1]){
	            	dimName = objects[1].toString();
	            }if(null != objects[2]){
	            	id = objects[2].toString();
	            }
	            
	            Map<String, Object> map = new HashMap<String, Object>();
				map.put("dimId", dimId);
				map.put("dimName", dimName);
				
				if(templateRelaDimensionParentIdAllMap.get(id) != null){
					//存在子维度
					List<TemplateRelaDimension> templateRelaDimension2List = templateRelaDimensionParentIdAllMap.get(id);
					ArrayList<Double> doArrayList = new ArrayList<Double>();
					for (TemplateRelaDimension templateRelaDimension2 : templateRelaDimension2List) {
						doArrayList.add(this.getMaxValue((ArrayList<Double>)maps.get(templateRelaDimension2.getDimension().getId())));
					}
					map.put("dicValue",this.getMaxValue(doArrayList));
				}else{
					//不存在
					map.put("dicValue",this.getMaxValue((ArrayList<Double>)maps.get(dimId)));
				}
				
				mapList.add(map);
	        }
		}else{
			List<TemplateRelaDimension> list = null;
			list = criteria.list();
			for (TemplateRelaDimension templateRelaDimension : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dimId", templateRelaDimension.getDimension().getId());
				map.put("dimName", templateRelaDimension.getDimension().getName());
				
				if(templateRelaDimensionParentIdAllMap.get(templateRelaDimension.getId()) != null){
					//存在子维度
					List<TemplateRelaDimension> templateRelaDimension2List = templateRelaDimensionParentIdAllMap.get(templateRelaDimension.getId());
					ArrayList<Double> doArrayList = new ArrayList<Double>();
					for (TemplateRelaDimension templateRelaDimension2 : templateRelaDimension2List) {
						doArrayList.add(this.getMaxValue((ArrayList<Double>)maps.get(templateRelaDimension2.getDimension().getId())));
					}
					map.put("dicValue",this.getMaxValue(doArrayList));
				}else{
					//不存在
					map.put("dicValue",this.getMaxValue((ArrayList<Double>)maps.get(templateRelaDimension.getDimension().getId())));
				}
				
				mapList.add(map);
			}
		}
		
		return mapList;
	}
	
	/**
	 * 通过风险评估ID得到评估模板ID
	 * @param assessPlanId 评估计划ID
	 * @return String
	 * @author 金鹏祥
	 * */
	public String getTemplateId(String assessPlanId){
		String templateId = ""; //模板ID
		RiskAssessPlan riskssessPlan = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
		String templateType = riskssessPlan.getTemplateType();//模板类型
		if(templateType == null){
			return "dim_template_type_custom"; //自定义模板
		}
		
		if(null == riskssessPlan.getTemplate()){
			return "dim_template_type_custom"; //自定义模板
		}else{
			templateId = riskssessPlan.getTemplate().getId(); //其他模板
		}
		
		return templateId;
	}
	
	/**
	 * 查询评估列表
	 * @param query	 查询条件
	 * @param scoreEmpId 人员ID
	 * @param templateId 模板ID
	 * @param isAlarm 是否显示风险告警灯
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @return ArrayList<HashMap<String, Object>>
	 * @author 金鹏祥
	 * */
	@RecordLog("查询风险辨识")
	public ArrayList<HashMap<String, Object>> findRiskByScoreEmpId(String query,
			String scoreEmpId, String templateId, boolean isAlarm, String assessPlanId, String companyId) {
		StringBuffer sql = new StringBuffer();
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>(); //返回集合列表
        String riskIcon = ""; //告警灯样式
		ArrayList<String> dimList = null; //维度维度及分值列表
		ArrayList<String> scoreEmpIdList = new ArrayList<String>(); //人员ID列表
		ApplyContainer applyContainer = null; //浏览风险告警组织数据实体
		HashMap<String, Object> map = null;//存储信息键值
		scoreEmpIdList.add(scoreEmpId);
		
        if(isAlarm){
        	applyContainer = new ApplyContainer();
        	applyContainer = o_assessDateInitBO.getAssessPreviewDateInit(companyId, assessPlanId, scoreEmpId, scoreEmpIdList);
        }else{
        	Map<String, Risk> riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
        	String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId(); //默认系统模板ID
        	applyContainer = new ApplyContainer();
        	applyContainer.setRiskAllMap(riskAllMap);
        	applyContainer.setTempSys(tempSys);
        }
        sql.append("SELECT 	rang.id,risk.ID parentRiskId,risk.risk_name parentRiskName,object.RISK_ID riskId,object.risk_name riskName,object.template_id,rang.score_emp_id,z.ID as EDIT_IDEA_ID,s.edit_idea_content  as responseText,object.DELETE_ESTATUS,z.edit_idea_content as editidea,object.id AS objectId "
        		+ " FROM T_RM_RISK_SCORE_OBJECT object,t_rm_risks risk,T_RM_RANG_OBJECT_DEPT_EMP rang LEFT JOIN T_RM_EDIT_IDEA z ON z.OBJECT_DEPT_EMP_ID = rang.ID LEFT JOIN T_RM_EDIT_RESPONSE s ON s.OBJECT_DEPT_EMP_ID = rang.ID  WHERE object.ID = rang.SCORE_OBJECT_ID AND risk.ID = object.PARENT_ID "
        		+ " AND object.ASSESS_PLAN_ID = :assessPlanId"
        		+ " AND rang.SCORE_EMP_ID = :scoreEmpId ");
        //sql.append(" GROUP BY c.risk_name order " + "by c.DELETE_ESTATUS desc ,i.EDIT_IDEA_ID desc,parentRiskId");
        //2017年4月11日16:48:54吉志强修改，针对部门来说，部门可能是有的风险的主责部门也有可能是相关部门
        System.out.println("******************* 风险辨识子流程的风险辨识环节 列表数据SQL:="+sql.toString());
        sql.append(" ORDER BY object.DELETE_ESTATUS DESC, z.EDIT_IDEA_CONTENT");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("scoreEmpId", scoreEmpId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        if(StringUtils.isNotBlank(query)){//搜索框关键字匹配
        	sqlQuery.setParameter("query", "%" + query + "%");
        }
        
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String rangObjectDeptEmpId = ""; //打分综合ID
            String parentRiskId= ""; //上级风险ID
            String parentRiskName = ""; //上级风险名称
            String riskId = ""; //风险ID
            String riskName = ""; //风险名称
            String assessEmpId = ""; //人员ID
            String tempId = ""; //模板ID
            String editIdeaId = ""; //人员意见ID
            String responseContent = null;  //  应对措施
            String deleteEstatus = ""; //风险删除状态
            String editIdeaContent = ""; //人员意见内容
            String objectId ="";
            if(null != objects[0]){
            	rangObjectDeptEmpId = objects[0].toString();
            }if(null != objects[1]){
            	parentRiskId = objects[1].toString();
            }if(null != objects[2]){
            	parentRiskName = objects[2].toString();
            }if(null != objects[3]){
            	riskId = objects[3].toString();
            }if(null != objects[4]){
            	riskName = objects[4].toString();
            }if(null != objects[6]){
            	assessEmpId = objects[6].toString();
            }if(null != objects[7]){
            	editIdeaId = objects[7].toString();
            }if(null != objects[8]){
            	responseContent = objects[8].toString();
            }if(null != objects[9]){
            	deleteEstatus = objects[9].toString();
            }if(null != objects[10]){
            	editIdeaContent = objects[10].toString();
            }
            if(null != objects[11]){
            	objectId = objects[11].toString();
            }
            
            if(!"dim_template_type_custom".equalsIgnoreCase(templateId)){
            	//此计划用的是系统及其他模板
            	tempId = templateId;
            }else{
            	//此计划用的是自定义模板
            	if(null == applyContainer.getRiskAllMap().get(riskId).getTemplate()){
            		//该风险下的模板ID为空,选用系统模板
            		tempId = applyContainer.getTempSys();
            	}else{
            		//选用该风险下的模板ID
            		tempId = applyContainer.getRiskAllMap().get(riskId).getTemplate().getId();
            	}
            }
            
            map = new HashMap<String, Object>();
            if("".equalsIgnoreCase(editIdeaId)){
            	
            	map.put("riskTitle", "icon-status-assess_mr");//默认状态
            }else{
            	map.put("riskTitle", "icon-status-assess_edit");//修改意见状态
            }
            
            if(deleteEstatus.equalsIgnoreCase("2")){
            	map.put("riskTitle", "icon-status-assess_new");//新风险添加状态
            }
            map.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
            map.put("parentRiskId", parentRiskId);
            map.put("parentRiskName", parentRiskName);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("objectId", objectId);
            map.put("responseContent", responseContent);
            if(isAlarm){
            	riskIcon = o_riskStatusBO.getRiskIcon(riskId, tempId, rangObjectDeptEmpId, applyContainer);
            	map.put("riskIcon", riskIcon);
            	map.put("assessEmpId", assessEmpId);
            	
            	dimList = DynamicDim.getDimValue(riskId, tempId, rangObjectDeptEmpId, applyContainer);
            	
            	if(dimList != null){
            		for (String obj : dimList) {
	            		map.put(obj.split("--")[0], NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()), 2));
					}
            	}
            	
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
            }
            
    		map.put("editIdeaContent", editIdeaContent);
            map.put("templateId", tempId);
            
            
            arrayList.add(map);
        }
        
        if(isAlarm){
        	//将告警灯已红、黄、绿分别排序
	        Collections.sort(arrayList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> arg0,
						Map<String, Object> arg1) {
					return -arg0.get("icon").toString().compareTo(arg1.get("icon").toString());
				}
			});
        }
        
        return arrayList;
	}
	
	
	/**
	 * 查询评估保密表单结果
	 * @param riskId	  风险ID
	 * @param scoreEmpId 人员ID
	 * @param templateId 模板ID
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @return ArrayList<HashMap<String, Object>>
	 * @author 郭鹏
	 * 20170519
	 * */
	@RecordLog("查询风险辨识")
	public ArrayList<HashMap<String, Object>> findRiskByScoreEmpIdForSecrecy(String riskId,
			String scoreEmpId, String assessPlanId, String companyId) {
		StringBuffer sql = new StringBuffer();
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>(); //返回集合列表
		ArrayList<String> scoreEmpIdList = new ArrayList<String>(); //人员ID列表
		HashMap<String, Object> map = null;//存储信息键值
		scoreEmpIdList.add(scoreEmpId);
		

        
        sql.append(" select DISTINCT i.objectId,i.id, g.id  parentRiskId, g.risk_name  parentRiskName ,c.RISK_ID  riskId, i.risk_name  riskName,");
        sql.append(" c.template_id, i.score_emp_id ,i.EDIT_IDEA_ID, c.DELETE_ESTATUS,z.edit_idea_content,zr.EDIT_IDEA_CONTENT responseIdea,tdsd.SCORE_DIC_VALUE,tdsd.score_dim_id");
        sql.append(" from T_RM_RISK_SCORE_OBJECT c, t_rm_risks g, ");
        sql.append(" (select a.id,b.id objectId,b.risk_name, a.score_emp_id, b.risk_id, a.EDIT_IDEA_ID from T_RM_RANG_OBJECT_DEPT_EMP a,  T_RM_RISK_SCORE_OBJECT b ");
        sql.append(" where a.score_emp_id = :scoreEmpId ");
        sql.append(" and  a.score_object_id = b.id and b.ASSESS_PLAN_ID=:assessPlanId GROUP BY b.risk_id)  i ");
        sql.append(" LEFT JOIN T_RM_EDIT_IDEA z on z.id = i.EDIT_IDEA_ID ");
        sql.append(" LEFT JOIN t_rm_edit_response zr ON zr.OBJECT_DEPT_EMP_ID = i.id");
        sql.append(" LEFT JOIN t_rm_score_result trsr ON trsr.RANG_OBJECT_DEPT_EMP_ID=i.id");
        sql.append(" LEFT JOIN t_dim_score_dic  tdsd ON tdsd.ID=trsr.SCORE_DIC_ID");
        
        sql.append(" where  g.id = c.parent_id AND i.objectId = c.ID  and  c.RISK_ID=:riskId");

        //sql.append(" GROUP BY c.risk_name order " + "by c.DELETE_ESTATUS desc ,i.EDIT_IDEA_ID desc,parentRiskId");
        //2017年4月11日16:48:54吉志强修改，针对部门来说，部门可能是有的风险的主责部门也有可能是相关部门
        System.out.println("******************* 风险辨识子流程的风险辨识环节 列表数据SQL:="+sql.toString());
        sql.append(" order " + "by c.DELETE_ESTATUS desc ,i.EDIT_IDEA_ID desc,parentRiskId");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("scoreEmpId", scoreEmpId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        sqlQuery.setParameter("riskId", riskId);
        
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
	 	int i=0;
	 	int j=0;
	 	String riskIds="";
        for (Object[] objects : list) {
          	 if (!riskIds.equals( objects[4].toString())) {
          		if(map!=null)
             	{
             		arrayList.add(map);  
             	}
        		 map = new HashMap<String, Object>();
        		 riskIds = objects[1].toString();
    				i = 0;
             	}
          	   if(i==0){
            String objectId="";
            String rangObjectDeptEmpId = ""; //打分综合ID
            String parentRiskId= ""; //上级风险ID
            String parentRiskName = ""; //上级风险名称
            String riskName = ""; //风险名称
            String assessEmpId = ""; //人员ID
            String tempId = ""; //模板ID
            String editIdeaId = ""; //人员意见ID
            String deleteEstatus = ""; //风险删除状态
            String editIdeaContent = ""; //人员意见内容
            String responseIdeaContent = ""; //人员意见内容
            if(null != objects[0]){
            	objectId = objects[0].toString();
            }if(null != objects[1]){
            	rangObjectDeptEmpId = objects[1].toString();
            }if(null != objects[2]){
            	parentRiskId = objects[2].toString();
            }if(null != objects[3]){
            	parentRiskName = objects[3].toString();
            }if(null != objects[4]){
            	riskIds = objects[4].toString();
            }if(null != objects[5]){
            	riskName = objects[5].toString();
            }if(null != objects[7]){
            	assessEmpId = objects[7].toString();
            }if(null != objects[8]){
            	editIdeaId = objects[8].toString();
            }if(null != objects[9]){
            	deleteEstatus = objects[9].toString();
            }if(null != objects[10]){
            	editIdeaContent = objects[10].toString();
            }
            if(null != objects[11]){
            	responseIdeaContent = objects[11].toString();
            }

            map.put("parentRiskId", parentRiskId);
            map.put("parentRiskName", parentRiskName);
            map.put("riskId", riskIds);
            map.put("riskName", riskName);       
    		map.put("editIdeaContent", editIdeaContent);
            map.put("templateId", tempId);
            //郭鹏添加打分对象表ID
            map.put("objectId", objectId);
            //郭鹏添加应对意见
        	map.put("responseIdeaContent", responseIdeaContent);
        	if(null != objects[13]&&null != objects[12]){
				map.put(objects[13].toString(), Utils.retain2Decimals(Double.parseDouble(objects[12].toString())));
			}
          	}else{
				if(null != objects[13]&&null != objects[12]){
					map.put(objects[13].toString(), Utils.retain2Decimals(Double.parseDouble(objects[12].toString())));
				}
			}
			i++;
			j++;
        	  if(j == list.size())
           	{
           		arrayList.add(map);  
           	}
		}
        return arrayList;
	}
	
	
	/**
	 * 查询评估列表
	 * @param query	 查询条件
	 * @param scoreEmpId 人员ID
	 * @param templateId 模板ID
	 * @param isAlarm 是否显示风险告警灯
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @return ArrayList<HashMap<String, Object>>
	 * @author 金鹏祥
	 * */
	@RecordLog("查询风险辨识")
	public ArrayList<HashMap<String, Object>> findRiskByScoreEmpIdSecrecy(String query,
			String scoreEmpId, String templateId, boolean isAlarm, String assessPlanId, String companyId) {
		StringBuffer sql = new StringBuffer();
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>(); //返回集合列表
        String riskIcon = ""; //告警灯样式
		ArrayList<String> dimList = null; //维度维度及分值列表
		ArrayList<String> scoreEmpIdList = new ArrayList<String>(); //人员ID列表
		ApplyContainer applyContainer = null; //浏览风险告警组织数据实体
		HashMap<String, Object> map = null;//存储信息键值
		scoreEmpIdList.add(scoreEmpId);
		
        if(isAlarm){
        	applyContainer = new ApplyContainer();
        	applyContainer = o_assessDateInitBO.getAssessPreviewDateInit(companyId, assessPlanId, scoreEmpId, scoreEmpIdList);
        }else{
        	Map<String, Risk> riskAllMap = o_riskOutsideBO.getAllRiskInfo(companyId);
        	String tempSys = o_templateBO.findTemplateByType("dim_template_type_sys").getId(); //默认系统模板ID
        	applyContainer = new ApplyContainer();
        	applyContainer.setRiskAllMap(riskAllMap);
        	applyContainer.setTempSys(tempSys);
        }
        
        sql.append(" select DISTINCT i.objectId,i.id, g.id  parentRiskId, g.risk_name  parentRiskName ,c.RISK_ID  riskId, i.risk_name  riskName,");
        sql.append(" c.template_id, i.score_emp_id ,i.EDIT_IDEA_ID, c.DELETE_ESTATUS,z.edit_idea_content,zr.EDIT_IDEA_CONTENT responseIdea,tdsd.SCORE_DIC_VALUE,tdsd.score_dim_id");
        sql.append(" from T_RM_RISK_SCORE_OBJECT c, t_rm_risks g, ");
        sql.append(" (select a.id,b.id objectId,b.risk_name, a.score_emp_id, b.risk_id, a.EDIT_IDEA_ID from T_RM_RANG_OBJECT_DEPT_EMP a,  T_RM_RISK_SCORE_OBJECT b ");
        sql.append(" where a.score_emp_id = :scoreEmpId ");
        sql.append(" and  a.score_object_id = b.id and b.ASSESS_PLAN_ID=:assessPlanId GROUP BY b.risk_id)  i ");
        sql.append(" LEFT JOIN T_RM_EDIT_IDEA z on z.id = i.EDIT_IDEA_ID ");
        sql.append(" LEFT JOIN t_rm_edit_response zr ON zr.OBJECT_DEPT_EMP_ID = i.id");
        sql.append(" LEFT JOIN t_rm_score_result trsr ON trsr.RANG_OBJECT_DEPT_EMP_ID=i.id");
        sql.append(" LEFT JOIN t_dim_score_dic  tdsd ON tdsd.ID=trsr.SCORE_DIC_ID");
        
        sql.append(" where  g.id = c.parent_id AND i.objectId = c.ID  ");
        if(StringUtils.isNotBlank(query)){//搜索框关键字匹配
            sql.append(" and (g.risk_name like :query or c.risk_name like :query ) ");
        }
        //sql.append(" GROUP BY c.risk_name order " + "by c.DELETE_ESTATUS desc ,i.EDIT_IDEA_ID desc,parentRiskId");
        //2017年4月11日16:48:54吉志强修改，针对部门来说，部门可能是有的风险的主责部门也有可能是相关部门
        System.out.println("******************* 风险辨识子流程的风险辨识环节 列表数据SQL:="+sql.toString());
        sql.append(" order " + "by c.RISK_ID,c.DELETE_ESTATUS desc ,i.EDIT_IDEA_ID desc,parentRiskId");
        SQLQuery sqlQuery = o_rangObjectDeptEmpDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("scoreEmpId", scoreEmpId);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        if(StringUtils.isNotBlank(query)){//搜索框关键字匹配
        	sqlQuery.setParameter("query", "%" + query + "%");
        }
        
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
	 	int i=0;
	 	int j=0;
	 	String riskId="";
        for (Object[] objects : list) {
          	 if (!riskId.equals( objects[4].toString())) {
          		if(map!=null)
             	{
             		arrayList.add(map);  
             	}
        		 map = new HashMap<String, Object>();
        		riskId = objects[1].toString();
    				i = 0;
             	}
          	   if(i==0){
            String objectId="";
            String rangObjectDeptEmpId = ""; //打分综合ID
            String parentRiskId= ""; //上级风险ID
            String parentRiskName = ""; //上级风险名称
            String riskName = ""; //风险名称
            String assessEmpId = ""; //人员ID
            String tempId = ""; //模板ID
            String editIdeaId = ""; //人员意见ID
            String deleteEstatus = ""; //风险删除状态
            String editIdeaContent = ""; //人员意见内容
            String responseIdeaContent = ""; //人员意见内容
            if(null != objects[0]){
            	objectId = objects[0].toString();
            }if(null != objects[1]){
            	rangObjectDeptEmpId = objects[1].toString();
            }if(null != objects[2]){
            	parentRiskId = objects[2].toString();
            }if(null != objects[3]){
            	parentRiskName = objects[3].toString();
            }if(null != objects[4]){
            	riskId = objects[4].toString();
            }if(null != objects[5]){
            	riskName = objects[5].toString();
            }if(null != objects[7]){
            	assessEmpId = objects[7].toString();
            }if(null != objects[8]){
            	editIdeaId = objects[8].toString();
            }if(null != objects[9]){
            	deleteEstatus = objects[9].toString();
            }if(null != objects[10]){
            	editIdeaContent = objects[10].toString();
            }
            if(null != objects[11]){
            	responseIdeaContent = objects[11].toString();
            }
            
            if(!"dim_template_type_custom".equalsIgnoreCase(templateId)){
            	//此计划用的是系统及其他模板
            	tempId = templateId;
            }else{
            	//此计划用的是自定义模板
            	if(null == applyContainer.getRiskAllMap().get(riskId).getTemplate()){
            		//该风险下的模板ID为空,选用系统模板
            		tempId = applyContainer.getTempSys();
            	}else{
            		//选用该风险下的模板ID
            		tempId = applyContainer.getRiskAllMap().get(riskId).getTemplate().getId();
            	}
            }
            if("".equalsIgnoreCase(editIdeaId)){
            	
            	map.put("riskTitle", "icon-status-assess_mr");//默认状态
            }else{
            	map.put("riskTitle", "icon-status-assess_edit");//修改意见状态
            }
            
            if(deleteEstatus.equalsIgnoreCase("2")){
            	map.put("riskTitle", "icon-status-assess_new");//新风险添加状态
            }
            map.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
            map.put("parentRiskId", parentRiskId);
            map.put("parentRiskName", parentRiskName);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            if(isAlarm){
            	riskIcon = o_riskStatusBO.getRiskIcon(riskId, tempId, rangObjectDeptEmpId, applyContainer);
            	map.put("riskIcon", riskIcon);
            	map.put("assessEmpId", assessEmpId);
            	
            	dimList = DynamicDim.getDimValue(riskId, tempId, rangObjectDeptEmpId, applyContainer);
            	
            	if(dimList != null){
            		for (String obj : dimList) {
	            		map.put(obj.split("--")[0], NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()), 2));
					}
            	}
            	
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
            }
            
            
    		map.put("editIdeaContent", editIdeaContent);
            map.put("templateId", tempId);
            //郭鹏添加打分对象表ID
            map.put("objectId", objectId);
            //郭鹏添加应对意见
        	map.put("responseIdeaContent", responseIdeaContent);
        	if(null != objects[13]&&null != objects[12]){
				map.put(objects[13].toString(), Utils.retain2Decimals(Double.parseDouble(objects[12].toString())));
			}
          	}else{
				if(null != objects[13]&&null != objects[12]){
					map.put(objects[13].toString(), Utils.retain2Decimals(Double.parseDouble(objects[12].toString())));
				}
			}
			i++;
			j++;
        	  if(j == list.size())
           	{
           		arrayList.add(map);  
           	}
		}
        if(isAlarm){
        	//将告警灯已红、黄、绿分别排序
	        Collections.sort(arrayList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> arg0,
						Map<String, Object> arg1) {
					return -arg0.get("icon").toString().compareTo(arg1.get("icon").toString());
				}
			});
    }
        return arrayList;
	}
}
	