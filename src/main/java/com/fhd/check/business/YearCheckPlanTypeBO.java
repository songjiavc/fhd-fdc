package com.fhd.check.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.check.YearCheckPlanTypeDAO;

@Service
public class YearCheckPlanTypeBO {

	@Autowired
	private YearCheckPlanTypeDAO yearCheckPlanTypeDAO;
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String,String>> findPlanTypeByAll(){
		List<Map<String,String>> rtnList = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		String querysql = "select id,name,typeValue from YearCheckPlanType";
		Query query = yearCheckPlanTypeDAO.createQuery(querysql);
		List<Object[]> list = query.list();
		for (Object[] objects : list) {
			map = new HashMap<String,String>();
			if(objects[0] != null){
				map.put("id", objects[0].toString());
			}
			if(objects[1] != null){
				map.put("name", objects[1].toString());
			}
			if(objects[2] != null){
				map.put("typeValue", objects[2].toString());
			}
			rtnList.add(map);
		}
		return rtnList;
	}
}
