package com.fhd.check.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.check.BussinessDAO;

/**
 * 评价点参与人BO.
 * @author 吴德福
 * @version
 * @since Ver 1.1
 * @Date 2013-3-20 下午22:28:25
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class BussinessBO {

	@Autowired
	private BussinessDAO o_BussinessDAO;
	 
	public List<Map<String,String>> findBussinessByAll(){
		List<Map<String,String>> rtnList = new ArrayList<Map<String,String>>();
		Map<String,String> tempMap = null;
		String queryHql = "select id,name from Bussiness";
		Query query = o_BussinessDAO.createQuery(queryHql);
		List<Object[]> list = query.list();
		for (Object[] objects : list) {
			tempMap = new HashMap<String,String>();
			if(objects[0] != null){
				tempMap.put("id", objects[0].toString());
			}
			if(objects[1] != null){
				tempMap.put("name", objects[1].toString());
			}
			rtnList.add(tempMap);
		}
		return rtnList;
	}
}
