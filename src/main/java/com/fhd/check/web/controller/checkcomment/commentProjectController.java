package com.fhd.check.web.controller.checkcomment;
import java.util.HashMap;
import java.util.List;

import org.compass.core.json.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.check.business.checkcomment.CheckCommentBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.check.checkcomment.CheckComment;
import com.fhd.entity.check.checkproject.CheckProject;

import net.sf.json.JSONArray;
@Controller
public class commentProjectController {

	@Autowired
	private CheckCommentBO CheckCommentBO;
	
	/*
	 * 查询所有考核内容controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: int start 起页数
	 * 		  int limit 每页显示个数
	 * 		  String query 查询 
	 * 		  String sort  排序 
	 * */
	@ResponseBody
	@RequestMapping("/check/checkcomment/findCheckComment.f")
	public HashMap<String, Object> findCheckComment(int start, int limit, String query, String sort) {
		Page<CheckComment> page = new Page<CheckComment>();
		page.setPageNo(limit == 0 ? 0 : (start / limit) + 1);
		page.setPageSize(limit);
		page=CheckCommentBO.findCheckComment(page, query,sort);
		HashMap<String,Object> map=new HashMap<String, Object>();
		JSONArray ja=JSONArray.fromObject(page.getResult());
		System.out.println(ja.toString());
		map.put("datas",page.getResult());
		return map;
	}
	
	/*
	 * 查询所有考评内容controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: String projectId 考评项目ID
	 * */
	@ResponseBody
	@RequestMapping("/check/checkcomment/findCheckComments.f")
	public List<CheckComment> findCheckProjects(String projectId) {
		List<CheckComment> list=CheckCommentBO.findCheckProjects(projectId);
		return list;
	}
	/*
	 * 保存更新考评项目controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@ResponseBody
	@RequestMapping("/check/checkcomment/savaCheckComment.s")
	public boolean savaCheckComment(String data) {
		return CheckCommentBO.savaCheckComment(data);
	}
	
	/*
	 * 删除考评项目controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@ResponseBody
	@RequestMapping("/check/checkcomment/deleteCheckComment.d")
	public boolean deleteCheckComment(String data) {
		return CheckCommentBO.deleteCheckComment(data);
	}
}