/**
 * OrgCmpBO.java
 * com.fhd.sys.business.orgstructure
 *
 * Function : ENDO
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-10-24 		张 雷
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.business.orgstructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.dao.sys.orgstructure.SysOrganizationDAO;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;

/**
 * ClassName:OrgCmpBO
 *
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-10-24		上午10:18:34
 *
 * @see 	 
 */
@SuppressWarnings({ "deprecation", "unchecked" })
@Service 
public class OrgCmpBO {
	
	@Autowired
	private SysOrganizationDAO o_sysOrganizationDAO;
	
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	
	/**
	 * <pre>
	 * 根据parentId查询所有公司
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param parentId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Map<String, Object> findCompanyByParentId(String parentId) {
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		Criteria criteria = o_sysOrganizationDAO.createCriteria();
		criteria.add(Restrictions.like("orgseq", "." + parentId + ".",MatchMode.ANYWHERE));
		criteria.add(Restrictions.in("orgType", new String[]{"0orgtype_c","0orgtype_sc"}));
		criteria.addOrder(Order.asc("orgseq"));
		List<SysOrganization> organizations = criteria.list();
		for (SysOrganization org : organizations) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", org.getId());
			StringBuffer sb = new StringBuffer();
			node.put("name", sb.append(org.getOrgname()).toString());
			nodes.add(node);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("companys", nodes);
		return map;
	}
	/**
	 * <pre>
	 * 根据机构的Id的Set集合查询机构列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idSet 机构的Id的Set集合
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<SysOrganization> findOrgByIdSet(Set<String> idSet) {
		Criteria criteria = o_sysOrganizationDAO.createCriteria();
		if(null != idSet && idSet.size()>0){
			criteria.add(Restrictions.in("id", idSet.toArray()));
		}else{
			criteria.add(Restrictions.isNotNull("id"));
		}
		criteria.addOrder(Order.asc("orgseq"));
		return criteria.list();
	}
	
	/**
	 * <pre>
	 * 根据员工的Id的Set集合查询员工列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idSet 员工的Id的Set集合
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<SysEmployee> findEmpByIdSet(Set<String> idSet) {
		Criteria criteria = o_sysEmployeeDAO.createCriteria();
		if(null != idSet && idSet.size()>0){
			criteria.add(Restrictions.in("id", idSet.toArray()));
		}else{
			criteria.add(Restrictions.isNull("id"));
		}
		return criteria.list();
	}
	
	/**
	 * 根据公司id查询公司的所有部门列表.
	 * @author 吴德福
	 * @param companyId
	 * @return List<SysOrganization>
	 */
	public List<SysOrganization> findDeptListByCompanyId(String companyId){
		Criteria criteria = o_sysOrganizationDAO.createCriteria();
		criteria.add(Restrictions.eq("parentOrg.id", companyId));
		criteria.add(Restrictions.in("orgType", new Object[]{"0orgtype_d","0orgtype_sd"}));
		return criteria.list();
	}
	
	/**
	 * @author songjia
	 * @param page
	 * @param query
	 * @desc     
	 * @return
	 */
	public Page<SysOrganization> findOrgByQuery(Page<SysOrganization> page,String query,String companyId){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(SysOrganization.class);
		criteria.add(Restrictions.in("orgType", new Object[]{"0orgtype_d","0orgtype_sd"}));
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		criteria.add(Restrictions.eq("parentOrg.id", companyId));
		if(StringUtils.isNotBlank(query)){
            Disjunction disjunction = Restrictions.disjunction();  
            disjunction.add(Restrictions.like("orgcode", "%"+query+"%"));
            disjunction.add(Restrictions.like("orgname", "%"+query+"%"));
            criteria.add(disjunction);  
	    }
		return o_sysOrgDAO.findPage(criteria, page, false);
	}
	
}

