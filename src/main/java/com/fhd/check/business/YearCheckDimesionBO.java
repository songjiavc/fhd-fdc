package com.fhd.check.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.dao.check.YearCheckDimesionDAO;
import com.fhd.entity.check.YearCheckDimesion;
import com.fhd.entity.check.checkdetail.CheckDetail;

import net.sf.json.JSONArray;

@Service
public class YearCheckDimesionBO {

	@Autowired
	private YearCheckDimesionDAO yearCheckDimesionDAO;
	
	public List<Map<String, Object>> finCheckDimesionAllPage(Page<YearCheckDimesion> page, String query, String sort) {
		// TODO Auto-generated method stub
		DetachedCriteria dc = DetachedCriteria.forClass(YearCheckDimesion.class);
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.eq("name", query));
		}
		if (StringUtils.isNotBlank(query)) {
			dc.addOrder(Order.desc(sort));
		}
		List<YearCheckDimesion> list= yearCheckDimesionDAO.findPage(dc, page, false).getResult();
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		for(YearCheckDimesion yearCheckDimesion:list){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("id", yearCheckDimesion.getId());
		map.put("detailName", yearCheckDimesion.getCheckDetail().getName());
		map.put("detailId", yearCheckDimesion.getCheckDetail().getId());
		map.put("dalayDate", yearCheckDimesion.getDalayDate());
		map.put("subScore", yearCheckDimesion.getSubScore());
		map.put("planTypeID", yearCheckDimesion.getPlayType().getId());
		map.put("planTypeName", yearCheckDimesion.getPlayType().getName());
		data.add(map);
		}
		return data;
	}
/**
 * 保存维度
 * */
	@Transient
	public boolean savaCheckDimesion(String data) {
		boolean t = true;
		try {
			data.replaceAll("\'", "\"");
			JSONArray str = JSONArray.fromObject(data);
			@SuppressWarnings("unchecked")
			List<YearCheckDimesion> list = (List<YearCheckDimesion>) JSONArray.toCollection(str, YearCheckDimesion.class);
			for (YearCheckDimesion yearCheckDimesion : list) {
				if (yearCheckDimesion.getId().equals("")) {
					yearCheckDimesion.setId(UUID.randomUUID().toString());
				}
				yearCheckDimesionDAO.merge(yearCheckDimesion);		
			}
		} catch (Exception e) {
			t = false;
		}
		return t;
	}
	@Transient
public boolean deleteCheckProject(String data) {
	boolean t=true;
	try {
		String ids[]=data.split(",");
		for (int i = 0; i < ids.length; i++) {
			YearCheckDimesion yearCheckDimesion=new YearCheckDimesion();
			yearCheckDimesion.setId(ids[i]);	
			yearCheckDimesionDAO.delete(yearCheckDimesion);
		}
	} catch (Exception e) {
		e.printStackTrace();
		t=false;
	}
	
	return t;
}

}
