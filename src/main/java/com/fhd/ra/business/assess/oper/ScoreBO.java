package com.fhd.ra.business.assess.oper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.risk.ScoreDAO;
import com.fhd.entity.risk.Score;

@Service
public class ScoreBO {
	
	@Autowired
	private ScoreDAO o_scoreDAO;
	
	/**
	 * 查询全部维度分值并已MAP方式存储
	 * @return HashMap<String, List<Double>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<Double>> findDicAllMap(){
		HashMap<String, List<Double>> map = new HashMap<String, List<Double>>();
		List<Double> lists = null;
		StringBuffer sql = new StringBuffer();
        sql.append(" select score_dim_id,score_dic_value from T_DIM_SCORE_DIC ");
        
        SQLQuery sqlQuery = o_scoreDAO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String scoreDimId = "";
            String scoreValue = "";
            
            if(null != objects[0]){
            	scoreDimId = objects[0].toString();
            }if(null != objects[1]){
            	scoreValue = objects[1].toString();
            }
            
            if(map.get(scoreDimId) != null){
				map.get(scoreDimId).add(Double.parseDouble(scoreValue));
			}else{
				lists = new ArrayList<Double>();
				lists.add(Double.parseDouble(scoreValue));
				map.put(scoreDimId, lists);
			}
           
        }
		
		return map;
	}
	
	/**
	 * 查询维度分值关联全部信息并以sort字段进行排序
	 * @author 金鹏祥
	 * @return List<Score>
	 * */
	@SuppressWarnings("unchecked")
	public List<Score> findScoreAllList(){
		List<Score> arrayList = new ArrayList<Score>();
		Criteria criteria = o_scoreDAO.createCriteria();
		criteria.addOrder(Order.asc("sort"));
		List<Score> list = null;
		list = criteria.list();
		
		for (Score score : list) {
			arrayList.add(score);
		}
		
		return arrayList;
	}
}
