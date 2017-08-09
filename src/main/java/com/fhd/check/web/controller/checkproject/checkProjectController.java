package com.fhd.check.web.controller.checkproject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * 考评项目controller
 * AUTHOR:Perry Guo
 * DATE:2017-07-17
 * */
import javax.servlet.http.HttpServletResponse;

import org.compass.core.json.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.check.business.checkproject.CheckProjectBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.check.checkproject.CheckProject;

import net.sf.json.JSONArray;
@Controller
public class checkProjectController {

	@Autowired
	private CheckProjectBO checkProjectBO;
	
	/*
	 * 查询所有考评项目controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: int start 起页数
	 * 		  int limit 每页显示个数
	 * 		  String query 查询 
	 * 		  String sort  排序 
	 * */
	@ResponseBody
	@RequestMapping("/check/checkproject/findCheckProject.f")
	public HashMap<String, Object> findCheckProject(int start, int limit, String query, String sort) {
		Page<CheckProject> page = new Page<CheckProject>();
		page.setPageNo(limit == 0 ? 0 : (start / limit) + 1);
		page.setPageSize(limit);
		page=checkProjectBO.findCheckProject(page, query,sort);
		HashMap<String,Object> map=new HashMap<String, Object>();
		map.put("datas",page.getResult());
		
		return map;
	}
	/*
	 * 查询所有考评项目controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: int start 起页数
	 * 		  int limit 每页显示个数
	 * 		  String query 查询 
	 * 		  String sort  排序 
	 * */
	@ResponseBody
	@RequestMapping("/check/checkproject/findCheckProjects.f")
	public List<CheckProject> findCheckProjects() {
		List<CheckProject> list=checkProjectBO.findCheckProjects();
		return list;
	}
	/*
	 * 保存更新考评项目controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@ResponseBody
	@RequestMapping("/check/checkproject/savaCheckProject.s")
	public boolean savaCheckProject(String data) {
		return checkProjectBO.savaCheckProject(data);
	}
	
	/*
	 * 删除考评项目controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@ResponseBody
	@RequestMapping("/check/checkproject/deleteCheckProject.d")
	public boolean deleteCheckProject(String data) {
		return checkProjectBO.deleteCheckProject(data);
	}
}