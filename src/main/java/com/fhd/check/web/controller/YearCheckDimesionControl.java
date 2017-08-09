package com.fhd.check.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.check.business.YearCheckDimesionBO;
import com.fhd.check.business.YearCheckPlanTypeBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.check.YearCheckDimesion;

@Controller
public class YearCheckDimesionControl {

	@Autowired
	private YearCheckDimesionBO yearCheckDimsionBO;
	@Autowired
	private YearCheckPlanTypeBO checkPlanTypeBO;
	/**
	 * 查询所有维度列表
	 * */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/check/finCheckDimesionAllPage.f")
	public Map<String, Object> finCheckDimesionAllPage (int start, int limit, String query, String sort)
	{
		HashMap<String, Object> maps = new HashMap<String, Object>();
		Page<YearCheckDimesion> page = new Page<YearCheckDimesion>();
		page.setPageNo(limit == 0 ? 0 : (start / limit) + 1);
		page.setPageSize(limit);
		List<Map<String,Object>> datas=yearCheckDimsionBO.finCheckDimesionAllPage(page, query, sort);
		maps.put("totalCount", page.getTotalItems());
		maps.put("datas", datas);
		return maps;
	}
	/**
	 * 查询计划类型列表
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/findPlanTypeByAll.f")
	public List<Map<String,String>> findPlanTypeByAll(){
		return checkPlanTypeBO.findPlanTypeByAll();
	}
	/**
	 * 保存更新自动扣分维度controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的扣分维度
	 * */
	@ResponseBody
	@RequestMapping("/check/savaCheckDimesion.s")
	public boolean savaCheckDimesion(String data) {
		return yearCheckDimsionBO.savaCheckDimesion(data);
	}
	
	/**
	 * 删除打分维度controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@ResponseBody
	@RequestMapping("/check/deleteCheckDimesion.d")
	public boolean deleteCheckDimesion(String data) {
		return yearCheckDimsionBO.deleteCheckProject(data);
	}
}
