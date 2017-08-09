package com.fhd.check.business.yearcheck;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.check.yearcheck.YearCheckPlanOrgDAO;
import com.fhd.entity.check.BussinessOrgRelaEmp;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlan;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlanOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Service
public class YearCheckPlanOrgBO {


@Autowired
private YearCheckPlanOrgDAO checkPlanOrgDAO;

/*
 * 根据planId查询所有年度考核计划部门关系
 * AUTHOR：Perry Guo
 * DATE:2017-07-31
 * */
public List<YearCheckPlanOrg> getPlanOrgByPlanID(String planId,String orgId,String empId)
{
	Criteria ca=checkPlanOrgDAO.createCriteria();
	ca.createAlias("yearCheckPlan", "plan");
	ca.createAlias("orgId", "org");
	ca.createAlias("empId", "emp");
	if (StringUtils.isNotBlank(planId)) {
		ca.add(Restrictions.eq("plan.id", planId));
	}
	if (StringUtils.isNotBlank(orgId)) {
		ca.add(Restrictions.eq("org.id", orgId));
	}if (StringUtils.isNotBlank(empId)) {
		ca.add(Restrictions.eq("emp.id", empId));
	}
	ca.addOrder(Order.desc("emp.empname"));
	@SuppressWarnings("unchecked")
	List<YearCheckPlanOrg> list=ca.list();
	
	return list;
}

/*
 * 临时存储plan中部门与管理人员关系
 * AUTHOR：Perry Guo
 * DATE:2017-07-31
 * */
@Transactional
public void saveEmpOrgByPlan(List<Map<String,String>> mapList,YearCheckPlan plan)
{
	for (int i = 0; i < mapList.size(); i++) {
		YearCheckPlanOrg checkPlanOrg=new YearCheckPlanOrg();
		checkPlanOrg.setYearCheckPlan(plan);
		List<YearCheckPlanOrg> list=this.getPlanOrgByPlanID(plan.getId(),mapList.get(i).get("orgId"),mapList.get(i).get("id"));
		if (list.size()<=0) {
			checkPlanOrg.setId(Identities.uuid());
		}else{
			
			checkPlanOrg.setId(list.get(0).getId());
		}
		SysOrganization org=new SysOrganization();
		org.setId(mapList.get(i).get("orgId"));
		checkPlanOrg.setOrgId(org);
		SysEmployee emp=new SysEmployee();
		emp.setId(mapList.get(i).get("id"));
		checkPlanOrg.setEmpId(emp);
		checkPlanOrgDAO.merge(checkPlanOrg);
		}
	}
	
/*
 *根据PlanID删除plan中部门与管理人员关系
 * AUTHOR：Perry Guo
 * DATE:2017-07-31
 * */
@Transactional
public void deleteEmpOrgByPlan(String planId)
{
      String sql="DELETE FROM t_rm_check_year_plan_org WHERE PLAN_ID=:planId";	
      SQLQuery 	query=checkPlanOrgDAO.createSQLQuery(sql);
      query.setParameter("planId", planId);
      query.executeUpdate();
	}



}
