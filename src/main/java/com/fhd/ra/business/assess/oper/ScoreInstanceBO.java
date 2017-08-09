package com.fhd.ra.business.assess.oper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.risk.ScoreInstanceDAO;

@Service
public class ScoreInstanceBO {

	@Autowired
	private ScoreInstanceDAO o_scoreInstanceBO;
	
	/**
	 * 查询全部维度分值,并已MAP方式存储(维度ID,维度下所有分值描述)
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findDimDescAllMap(){
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select c.template_id,c.score_dim_id,b.score_dic_value,a.edesc ");
		sql.append(" from t_dim_template_rela_dim c ");
		sql.append(" LEFT JOIN t_dim_score_dic_inst a ON a.template_rela_dim_id = c.id ");
		sql.append(" LEFT JOIN t_dim_score_dic b on a.score_dic_id=b.id ORDER BY c.score_dim_id,b.score_dic_value ");
        SQLQuery sqlQuery = o_scoreInstanceBO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String templateId = "";
            String dimId = "";
            String scoreValue = "";
            String scoreDesc = "";
            
            if(null != objects[0]){
            	templateId = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	scoreValue = objects[2].toString();
            }if(null != objects[3]){
            	scoreDesc = objects[3].toString();
            }
            
            if(map.get(dimId + "--" + templateId) != null){
            	
            	if(scoreDesc.length() > 20){
            		scoreDesc = scoreDesc.substring(0, 20) + "...";
            	}
            	
            	map.put(dimId + "--" + templateId, map.get(dimId + "--" + templateId) + 
            			scoreValue.replace(".", "").replace("0", "") + "分 : " + scoreDesc + "<br>");
            }else{
            	if(scoreDesc.length() > 20){
            		scoreDesc = scoreDesc.substring(0, 20) + "...";
            	}
            	
            	map.put(dimId + "--" + templateId, scoreValue.replace(".", "").replace("0", "") + "分 : " + scoreDesc + "<br>");
            }
        }
        
        return map;
	}
	
	/**
	 * 查询全部维度分值,并已MAP方式存储(维度ID--分值,分值下描述)
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findDicDescAllMap(){
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select c.template_id,c.score_dim_id,b.score_dic_value,a.edesc ");
		sql.append(" from t_dim_template_rela_dim c ");
		sql.append(" LEFT JOIN t_dim_score_dic_inst a ON a.template_rela_dim_id = c.id ");
		sql.append(" LEFT JOIN t_dim_score_dic b on a.score_dic_id=b.id ORDER BY c.score_dim_id,b.score_dic_value ");
        SQLQuery sqlQuery = o_scoreInstanceBO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String templateId = ""; //模板ID
            String dimId = ""; //维度ID
            String scoreValue = ""; //分值
            String scoreDesc = ""; //分值描述
            
            if(null != objects[0]){
            	templateId = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	scoreValue = objects[2].toString();
            }if(null != objects[3]){
            	scoreDesc = objects[3].toString();
            }
            
            map.put(dimId + "--" + templateId + "--" + scoreValue.replace(".", "").replace("0", ""), scoreDesc);
        }
        
        return map;
	}
	
	/**
	 * 查询全部维度分值,并已MAP方式存储(模板ID,List)
	 * @return HashMap<String, ArrayList<String>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findTemplateDicDescAllMap(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = null;
		sql.append(" select c.template_id,c.score_dim_id,b.score_dic_value,a.edesc,a.score_dic_id,d.score_dim_name,a.score_dic_name,g.score_dim_name  parent_dim_name ");
		sql.append(" from t_dim_template_rela_dim c ");
		sql.append(" LEFT JOIN t_dim_score_dic_inst a ON a.template_rela_dim_id = c.id ");
		sql.append(" LEFT JOIN t_dim_score_dic b on a.score_dic_id=b.id LEFT JOIN t_dim_score_dim d on d.id= c.score_dim_id ");
		sql.append(" LEFT JOIN t_dim_template_rela_dim e on e.parent_template_dim_id = c.id ");
		sql.append(" LEFT JOIN t_dim_score_dim g on g.id = e.score_dim_id ");
		sql.append(" ORDER BY c.score_dim_id,b.score_dic_value ");
        SQLQuery sqlQuery = o_scoreInstanceBO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String templateId = "";
            String dimId = "";
            String scoreDicValue = "";
            String scoreDicDesc = "";
            String scoreDicId = "";
            String dimName = "";
            String scoreDicName = "";
            String parentDimName = "";
            
            if(null != objects[0]){
            	templateId = objects[0].toString();
            }if(null != objects[1]){
            	dimId = objects[1].toString();
            }if(null != objects[2]){
            	scoreDicValue = objects[2].toString();
            }if(null != objects[3]){
            	scoreDicDesc = objects[3].toString();
            }if(null != objects[4]){
            	scoreDicId = objects[4].toString();
            }if(null != objects[5]){
            	dimName = objects[5].toString();
            }if(null != objects[6]){
            	scoreDicName = objects[6].toString();
            }if(null != objects[7]){
            	parentDimName = objects[7].toString();
            }
            
            if(!parentDimName.equalsIgnoreCase("")){
            	dimName = parentDimName;
            }
            
            if(map.get(templateId) != null){
            	map.get(templateId).add(dimId + "--" + scoreDicId + "--" + scoreDicValue + "--" + scoreDicDesc + "--" + dimName + "--" + scoreDicName);
            }else{
            	arrayList = new ArrayList<String>();
            	arrayList.add(dimId + "--" + scoreDicId + "--" + scoreDicValue + "--" + scoreDicDesc + "--" + dimName + "--" + scoreDicName);
            	map.put(templateId, arrayList);
            }
        }
        
        return map;
	}
}
