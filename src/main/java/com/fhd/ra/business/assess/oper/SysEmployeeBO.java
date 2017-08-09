package com.fhd.ra.business.assess.oper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.sys.orgstructure.SysEmployee;

@Service
public class SysEmployeeBO {
	
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	
	/**
	 * 查询所有员工实体,并已MAP方式存储(empId, 员工实体)
	 * */
	@SuppressWarnings("unchecked")
	public Map<String, SysEmployee> findSysEmployeeMapAll(){
		HashMap<String, SysEmployee> SysEmployeeMapAll = new HashMap<String, SysEmployee>();
		Criteria c = o_sysEmployeeDAO.createCriteria();
		List<SysEmployee> list = c.list();
		for (SysEmployee sysEmployee : list) {
			SysEmployeeMapAll.put(sysEmployee.getId(), sysEmployee);
		}
		
		return SysEmployeeMapAll;
	}
	
	/**
	 * 查询所有员工实体,并已MAP方式存储(empCode, 员工实体)
	 * */
	@SuppressWarnings("unchecked")
	public Map<String, SysEmployee> findSysEmployeeByCodeMapAll(){
		HashMap<String, SysEmployee> SysEmployeeMapAll = new HashMap<String, SysEmployee>();
		Criteria c = o_sysEmployeeDAO.createCriteria();
		List<SysEmployee> list = c.list();
		for (SysEmployee sysEmployee : list) {
			SysEmployeeMapAll.put(sysEmployee.getEmpcode(), sysEmployee);
		}
		
		return SysEmployeeMapAll;
	}
	
	
	/**
	 * 
	 * findVSysUserPageBySome:分页查询用户视图
	 * 
	 * @author 宋佳
	 * @param page
	 * @param query
	 * @param deleteStatus
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<SysEmployee> findSysUserPageByDeptIdAndRoleId(Page<SysEmployee> page,String orgId,String roleId,String query){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SysEmployee.class);
		criteria.createAlias("sysEmpOrgs", "sysEmpOrgs",Criteria.LEFT_JOIN);
		criteria.createAlias("sysEmpOrgs.sysOrganization", "org",Criteria.LEFT_JOIN);
		criteria.createAlias("sysUser",  "sysUser");
		criteria.createAlias("sysUser.sysRoles", "sysRoles",Criteria.LEFT_JOIN);
		
		if(StringUtils.isNotBlank(orgId)){
			criteria.add(Restrictions.eq("sysEmpOrgs.sysOrganization.id", orgId));
		}
		
		if(StringUtils.isNotBlank(roleId)){
			criteria.add(Restrictions.eq("sysRoles.id", roleId));
		}
		
		if(StringUtils.isNotBlank(query)){
            Disjunction disjunction = Restrictions.disjunction();  
            disjunction.add(Restrictions.like("sysUser.realname", "%"+query+"%"));
            disjunction.add(Restrictions.like("sysUser.username", "%"+query+"%"));
            criteria.add(disjunction);  
	    }
		criteria.setProjection(Projections.projectionList().add(Property.forName("id")).add(Property.forName("empname")).add(Property.forName("empcode")).add(Property.forName("org.orgname")).add(Property.forName("sysRoles.roleName"))); //加入Projections 后返回结果是object
		//加入Projections 后返回结果是object
		return o_sysEmployeeDAO.findPage(criteria, page, false);
	}
	
	
	/**
	 * 
	 * findVSysUserPageBySome:分页查询用户视图
	 * 
	 * @author 宋佳
	 * @param page
	 * @param query
	 * @param deleteStatus
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<SysEmployee> findSysUserPageByRoleId(Page<SysEmployee> page,String roleId,String query){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SysEmployee.class);
		criteria.createAlias("sysEmpOrgs", "sysEmpOrgs",Criteria.LEFT_JOIN);
		criteria.createAlias("sysEmpOrgs.sysOrganization", "org",Criteria.LEFT_JOIN);
		criteria.createAlias("sysUser",  "sysUser");
		criteria.createAlias("sysUser.sysRoles", "sysRoles",Criteria.LEFT_JOIN);
		
		if(StringUtils.isNotBlank(roleId)){
			criteria.add(Restrictions.eq("sysRoles.id", roleId));
		}
		
		if(StringUtils.isNotBlank(query)){
            Disjunction disjunction = Restrictions.disjunction();  
            disjunction.add(Restrictions.like("empcode", "%"+query+"%"));
            disjunction.add(Restrictions.like("username", "%"+query+"%"));
            criteria.add(disjunction);  
	    }
		
		criteria.setProjection(Projections.projectionList().add(Property.forName("sysUser.id")).add(Property.forName("sysUser.realname")).add(Property.forName("sysUser.username")).add(Property.forName("org.orgname")).add(Property.forName("sysRoles.roleName")));
		return o_sysEmployeeDAO.findPage(criteria, page, false);
	}
	
	/**
	 * 
	 * findVSysUserPageBySome:分页查询用户视图
	 * 
	 * @author 宋佳
	 * @param page
	 * @param query
	 * @param deleteStatus
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<Map<String,Object>> findSysUserPageByEmpIds(List<String> ids){
		Criteria criteria = o_sysEmployeeDAO.createCriteria();
		criteria.add(Restrictions.in("id", ids));
		criteria.createAlias("sysEmpOrgs", "sysEmpOrgs",Criteria.LEFT_JOIN);
		criteria.createAlias("sysEmpOrgs.sysOrganization", "org",Criteria.LEFT_JOIN);
		criteria.createAlias("sysUser",  "sysUser");
		criteria.createAlias("sysUser.sysRoles", "sysRoles",Criteria.LEFT_JOIN);
		criteria.setProjection(Projections.projectionList().add(Property.forName("id")).add(Property.forName("empname")).add(Property.forName("empcode")).add(Property.forName("org.orgname")).add(Property.forName("sysRoles.roleName"))); //加入Projections 后返回结果是object
		return criteria.list();
	}
	
	
	
}