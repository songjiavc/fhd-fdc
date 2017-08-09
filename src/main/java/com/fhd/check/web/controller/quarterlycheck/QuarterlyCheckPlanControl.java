package com.fhd.check.web.controller.quarterlycheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.check.business.quarterlycheck.QuarterlyCheckPlanBO;
import com.fhd.check.business.quarterlycheck.QuarterlyCheckPlanOrgBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.check.quarterlycheck.QuarterlyCheck;
import com.fhd.entity.check.quarterlycheck.QuarterlyCheckForm;
import com.fhd.entity.check.quarterlycheck.QuarterlyCheckPlanOrg;

@Controller
public class QuarterlyCheckPlanControl {

	@Autowired
	private QuarterlyCheckPlanBO quarterlyCheckPlanBO;
	
	@Autowired
	private QuarterlyCheckPlanOrgBO quarterlyCheckPlanOrgBO;
	
	/**
	 * 分页查询季度抽查计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-08-8
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/quarterly/findAllQuarterlyCheckPlan.f")
	public Map<String, Object> findAllQuarterlyCheckPlan(int start, int limit, String query, String sort,String status){
		Page<QuarterlyCheck> page = new Page<QuarterlyCheck>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = quarterlyCheckPlanBO.findAllQuarterlyCheckPlan(query, page,status);
		List<QuarterlyCheck> entityList = page.getResult();
		List<QuarterlyCheck> datas = new ArrayList<QuarterlyCheck>();
		for(QuarterlyCheck plan : entityList){
			
			datas.add(new QuarterlyCheckForm(plan));
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 根据ID查询季度抽查计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-08-8
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/quarterlycheck/findQuarterlyCheckPlanById.f")
	public Map<String, Object> findQuarterlyCheckPlanById(String id){
		return quarterlyCheckPlanBO.findQuarterlyCheckPlanById(id);
	}
	
	/**
	 * 保存季度抽查计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-08-8
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/quarterlycheck/savaQuarterlyCheck.s")
	public Map<String, Object> savaQuarterlyCheck(QuarterlyCheck quarterlyCheck) throws Exception{
		return quarterlyCheckPlanBO.savaQuarterlyCheck(quarterlyCheck);
	}
	
	/**
	 * 删除计划
	 * AUTHOR：Perry Guo
	 * DATE:2017-08-8
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/quarterlycheck/deleteQuarterlyPlan.d")
	public Map<String, Object> deleteQuarterlyPlan(String id) throws Exception{
		return quarterlyCheckPlanBO.deleteQuarterlyPlan(id);
	}
	
	/**
	 * 查询所有计划部门关系
	 * AUTHOR：Perry Guo
	 * DATE:2017-08-8
	 * */
	@ResponseBody
	@RequestMapping(value = "/check/quarterlycheck/findQuarterlyCheckPlanOrg.d")
	public List<QuarterlyCheckPlanOrg> findQuarterlyCheckPlanOrg(String planID){
		return quarterlyCheckPlanOrgBO.getPlanOrgByPlanID(planID, null, null, null);
	}
}
