package com.fhd.check.business.quarterlycheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.check.quarterlycheck.QuarterlyCheckPlanOrgDAO;
import com.fhd.entity.check.quarterlycheck.QuarterlyCheckPlanOrg;

@Service
public class QuarterlyCheckPlanOrgBO {
	
	@Autowired
	private QuarterlyCheckPlanOrgDAO quarterlyCheckPlanOrgDAO;
	/*
	 * 根据planId查询所有年度考核计划部门关系
	 * AUTHOR：Perry Guo
	 * DATE:2017-07-31
	 * */
	public List<QuarterlyCheckPlanOrg> getPlanOrgByPlanID(String planId,String orgId,String empId,String checkEmpId)
	{
		Criteria ca=quarterlyCheckPlanOrgDAO.createCriteria();
		ca.createAlias("yearCheckPlan", "plan",ca.LEFT_JOIN);
		ca.createAlias("orgId", "org",ca.LEFT_JOIN);
		ca.createAlias("empId", "emp",ca.LEFT_JOIN);
		ca.createAlias("checkEmpId", "checkEmp",ca.LEFT_JOIN);
		if (StringUtils.isNotBlank(planId)) {
			ca.add(Restrictions.eq("plan.id", planId));
		}
		if (StringUtils.isNotBlank(orgId)) {
			ca.add(Restrictions.eq("org.id", orgId));
		}
		if (StringUtils.isNotBlank(checkEmpId)) {
			ca.add(Restrictions.eq("checkEmp.id", checkEmpId));
		}
		ca.addOrder(Order.desc("emp.empname"));
		@SuppressWarnings("unchecked")
		List<QuarterlyCheckPlanOrg> list=ca.list();
		for(QuarterlyCheckPlanOrg quarterlyCheckPlanOrg:list)
		{
			Map<String,Object> map=new HashMap<String,Object>();
		}
		
		return list;
	}
}
