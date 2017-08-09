package com.fhd.check.business.checkproject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctc.wstx.util.StringUtil;
import com.fhd.core.dao.Page;
import com.fhd.dao.check.CheckProjectDAO;
import com.fhd.entity.check.checkproject.CheckProject;

import net.sf.json.JSONArray;

@Service
public class CheckProjectBO {
	@Autowired
	private CheckProjectDAO checkProjectDAO;

	/*
	 * 查询所有考评项目server 
	 * AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	public Page<CheckProject> findCheckProject(Page<CheckProject> page, String query, String sort) {
		DetachedCriteria dc = DetachedCriteria.forClass(CheckProject.class);
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.eq("name", query));
		}
		if (StringUtils.isNotBlank(query)) {
			dc.addOrder(Order.desc(sort));
		}

		return checkProjectDAO.findPage(dc, page, false);
	}
	/*
	 * 查询所有考评项目server 
	 * AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	public List<CheckProject> findCheckProjects() {
		Criteria dc = checkProjectDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<CheckProject> checkProjects=dc.list();
		return checkProjects;
	}
	/*
	 * 保存考评项目
	 * server AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	@Transactional
	public boolean savaCheckProject(String jsonArray) {
		boolean t = true;
		try {
			JSONArray str = JSONArray.fromObject(jsonArray);
			@SuppressWarnings("unchecked")
			List<CheckProject> list = (List<CheckProject>) JSONArray.toCollection(str, CheckProject.class);
			for (CheckProject checkProject : list) {
				if (checkProject.getId().equals("")) {
					checkProject.setId(UUID.randomUUID().toString());
				}
				 checkProjectDAO.merge(checkProject);		
			}
		} catch (Exception e) {
			t = false;
		}
		return t;
	}
	/*
	 * 删除考评项目
	 * server AUTOR:Perry Guo 
	 * Date:2017-07-17
	 */
	@Transactional
	public boolean deleteCheckProject(String data) {
		// TODO Auto-generated method stub
		String ids[]=data.split(",");
		for (int i = 0; i < ids.length; i++) {
			CheckProject checkProject=new CheckProject();
			checkProject.setId(ids[i]);	
			checkProjectDAO.delete(checkProject);
		}
		return false;
	}
	
}
