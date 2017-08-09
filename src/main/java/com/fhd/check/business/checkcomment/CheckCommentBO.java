package com.fhd.check.business.checkcomment;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fhd.core.dao.Page;
import com.fhd.dao.check.CheckCommentDAO;
import com.fhd.entity.check.checkcomment.CheckComment;
import com.fhd.entity.check.checkproject.CheckProject;

import net.sf.json.JSONArray;

@Service
public class CheckCommentBO {
	@Autowired
	private CheckCommentDAO checkCommentDAO;

	/*
	 * 查询所有考核内容server 
	 * AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	public Page<CheckComment> findCheckComment(Page<CheckComment> page, String query, String sort) {
		DetachedCriteria dc = DetachedCriteria.forClass(CheckComment.class);
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.eq("name", query));
		}
		if (StringUtils.isNotBlank(query)) {
			dc.addOrder(Order.desc(sort));
		}

		return checkCommentDAO.findPage(dc, page, false);
	}

	/*
	 * 保存考评内容
	 * server AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	@Transactional
	public boolean savaCheckComment(String jsonArray) {
		boolean t = true;
		try {
			jsonArray.replaceAll("\'", "\"");
			JSONArray str = JSONArray.fromObject(jsonArray);
			@SuppressWarnings("unchecked")
			List<CheckComment> list = (List<CheckComment>) JSONArray.toCollection(str, CheckComment.class);
			for (CheckComment checkComment : list) {
				if (checkComment.getId().equals("")) {
					checkComment.setId(UUID.randomUUID().toString());
				}
				checkCommentDAO.merge(checkComment);		
			}
		} catch (Exception e) {
			t = false;
		}
		return t;
	}
	/*
	 * 删除考评内容
	 * server AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	@Transactional
	public boolean deleteCheckComment(String data) {
		// TODO Auto-generated method stub
		String ids[]=data.split(",");
		for (int i = 0; i < ids.length; i++) {
			CheckComment checkComment=new CheckComment();
			checkComment.setId(ids[i]);	
			checkCommentDAO.delete(checkComment);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<CheckComment> findCheckProjects(String projectId) {
		Criteria c=checkCommentDAO.createCriteria();
		if(StringUtils.isNotBlank(projectId))
		{
		c.add(Restrictions.eq("project.id", projectId))	;
		}
		List<CheckComment> list=c.list();
		return list;
	}
	
}
