package com.fhd.check.business.checkDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.dao.check.CheckDetailDAO;
import com.fhd.entity.check.checkcomment.CheckComment;
import com.fhd.entity.check.checkdetail.CheckDetail;
import com.fhd.entity.check.checkproject.CheckProject;

import net.sf.json.JSONArray;

@Service
public class CheckDetailBO {

@Autowired
private CheckDetailDAO checkDetailDAO;

public Page<CheckDetail> finCheckDetailAllPage(Page<CheckDetail> page, String query, String sort)
{
	DetachedCriteria dc = DetachedCriteria.forClass(CheckDetail.class);
	if (StringUtils.isNotBlank(query)) {
		dc.add(Restrictions.eq("name", query));
	}
	if (StringUtils.isNotBlank(query)) {
		dc.addOrder(Order.desc(sort));
	}
    dc.createAlias("checkComment", "checkComment",dc.LEFT_JOIN);
    dc.createAlias("checkComment.project", "project",dc.LEFT_JOIN);
	dc.addOrder(Order.desc("project.name")).addOrder(Order.desc("checkComment.name"));
	return checkDetailDAO.findPage(dc, page, false);
	
}
@Transactional
public boolean savaCheckDetail(String jsonArray) {
	// TODO Auto-generated method stub
	boolean t = true;
	try {
		jsonArray.replaceAll("\'", "\"");
		JSONArray str = JSONArray.fromObject(jsonArray);
		@SuppressWarnings("unchecked")
		List<CheckDetail> list = (List<CheckDetail>) JSONArray.toCollection(str, CheckDetail.class);
		for (CheckDetail checkDetail : list) {
			if (checkDetail.getId().equals("")) {
				checkDetail.setId(UUID.randomUUID().toString());
			}
			checkDetailDAO.merge(checkDetail);		
		}
	} catch (Exception e) {
		t = false;
	}
	return t;

}
	/*
	 * 删除考评细则
	 * server AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	@Transactional
	public boolean deleteCheckDetail(String data) {
		// TODO Auto-generated method stub
		String ids[]=data.split(",");
		for (int i = 0; i < ids.length; i++) {
			CheckDetail checkDetail=new CheckDetail();
			checkDetail.setId(ids[i]);	
			checkDetailDAO.delete(checkDetail);
		}
		return false;
	}
	/*
	 * 查询考评规则是否合法
	 * server AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> validaCheckDetail() {
		StringBuffer sql=new StringBuffer();
		List<Map> data=new ArrayList<Map>();
		sql.append("select crp.NAME,crp.TOTAL_SCORE,IFNULL(SUM(DETAIL_SCORE),0) FROM t_rm_check_rule_project crp");
		sql.append(" LEFT JOIN t_rm_check_rule_comment rc ON crp.ID=rc.PROJECT_ID");
		sql.append(" LEFT JOIN t_rm_check_rule_detail  detail ON detail.COMMENT_ID=rc.ID");
		sql.append(" GROUP BY crp.ID");
		SQLQuery sqlQuery = checkDetailDAO.createSQLQuery(sql.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
            if(objects[1]!=objects[2]){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("errorMes", objects[0]+"总分："+objects[1]+"&nbsp&nbsp实际分："+objects[2]);
            data.add(map);
            }
        	
        }
		return data;	
	}
	/*
	 * 查询所有考评细则
	 * server AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */

	@SuppressWarnings("unchecked")
	public List<CheckDetail> findCheckDetail(String detailId) {
		Criteria c= checkDetailDAO.createCriteria();
		if(StringUtils.isNotBlank(detailId)){
			c.add(Restrictions.eq("id", detailId));
		}
		List<CheckDetail> detailList=c.list();
		return detailList;
	}

}
