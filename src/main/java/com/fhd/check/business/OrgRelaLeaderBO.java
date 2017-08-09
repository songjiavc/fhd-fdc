package com.fhd.check.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.check.BussinessOrgRelaEmpDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.check.Bussiness;
import com.fhd.entity.check.BussinessOrgRelaEmp;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 评价点参与人BO.
 * @author 吴德福
 * @version
 * @since Ver 1.1
 * @Date 2013-3-20 下午22:28:25
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class OrgRelaLeaderBO {

	@Autowired
	private BussinessOrgRelaEmpDAO o_BussinessOrgRelaEmpDAO;
	
	@Autowired
	private EmployeeDAO o_EmployeeDAO;
	 
	/**
	 * @author songjia 
	 * @desc     保存机构关联主管人员信息
	 */
	@Transactional
	public void saveOrgRelaEmpInfo(String bussinessId,String empIds,String[] orgIdArr){
		List<String> orgIdList = null;
		if(orgIdArr.length > 0){
			orgIdList = Arrays.asList(orgIdArr);
			for(String orgId : orgIdList){
				Long count = getCountFromLeaderRelaOrgByEmpIdAndOrgId(empIds,orgId);
				if(count == 0){
					BussinessOrgRelaEmp bussinessOrgRelaEmp = new BussinessOrgRelaEmp();
					bussinessOrgRelaEmp.setId(Identities.uuid());
					bussinessOrgRelaEmp.setBussinessId(new Bussiness(bussinessId));
					SysEmployee emp = o_EmployeeDAO.get(empIds);
					bussinessOrgRelaEmp.setEmp(emp);
					bussinessOrgRelaEmp.setManagedOrgId(new SysOrganization(orgId));
					bussinessOrgRelaEmp.setManageOrgId(emp.getSysEmpOrgs().iterator().next().getSysOrganization());
					o_BussinessOrgRelaEmpDAO.merge(bussinessOrgRelaEmp);
				}else{
					continue;
				}
			}
		}
	}
	
	/**@Author songjia
	 * @desc     判断是否存在相同结果记录
	 * @param empId
	 * @param orgId
	 * @return
	 */
	public Long getCountFromLeaderRelaOrgByEmpIdAndOrgId(String empId,String orgId){
		String hqlQuery = "SELECT COUNT(*) FROM BussinessOrgRelaEmp orgRelaEmp WHERE orgRelaEmp.managedOrgId.id = :orgId AND orgRelaEmp.emp.id = :empId";
		Long count = (Long)o_BussinessOrgRelaEmpDAO.createQuery(hqlQuery).setParameter("orgId", orgId).setParameter("empId", empId).uniqueResult();
		return count;
	}
	
	/**
	 * @author songjia
	 * @desc     根据ids删除关联关系
	 * @param ids
	 */
	@Transactional
	public void deleteOrgRelaEmp(String[] ids){
		o_BussinessOrgRelaEmpDAO.createQuery("delete BussinessOrgRelaEmp where id in (:ids)").setParameterList("ids", ids).executeUpdate();
	}
	
	
	public List<Map<String,String>> findEmpFromLeaderRelaOrgByAll(){
		List<Map<String,String>> rtnList = new ArrayList<Map<String,String>>();
		Map<String,String> tempMap = null;
		String queryHql = "SELECT distinct emp.id,emp.empname FROM BussinessOrgRelaEmp orgRelaEmp,SysEmployee emp WHERE orgRelaEmp.emp.id = emp.id";
		Query query = o_BussinessOrgRelaEmpDAO.createQuery(queryHql);
		List<Object[]> list = query.list();
		for (Object[] objects : list) {
			tempMap = new HashMap<String,String>();
			if(objects[0] != null){
				tempMap.put("id", objects[0].toString());
			}
			if(objects[1] != null){
				tempMap.put("empName", objects[1].toString());
			}
			rtnList.add(tempMap);
		}
		return rtnList;
	}
	
	/**
	 * @author songjia
	 * @desc    分页查询业务数据
	 * @param page
	 * @param bussinessId
	 * @param manageId
	 * @param managedId
	 * @param query
	 * @return
	 */
	public Page<BussinessOrgRelaEmp> findOrgRelaEmpInfoByBussIdOrOrgId(Page<BussinessOrgRelaEmp> page,String bussinessId,String managePeopleId,String managedId,String query){
			DetachedCriteria criteria = DetachedCriteria.forClass(BussinessOrgRelaEmp.class);
			if(StringUtils.isNotBlank(bussinessId)){
				criteria.add(Restrictions.eq("bussinessId.id", bussinessId));
			}
			if(StringUtils.isNotBlank(managedId)){
				criteria.add(Restrictions.eq("managedOrgId.id", managedId));
			}
			if(StringUtils.isNotBlank(managePeopleId)){
				criteria.add(Restrictions.eq("emp.id", managePeopleId));
			}
			return o_BussinessOrgRelaEmpDAO.findPage(criteria, page, false);
	}
	
	/**
	 * @outerinterface
	 * @给考核外部提供接口
	 * @param bussinessId
	 * @param managedId
	 * @desc    根据业务id和被管理部门id获取管理人员id和name
	 * @return
	 */
	public List<Map<String,String>> findEmpIdsFromOrgRelaEmpByManagedIdAndBussinessId(String bussinessId,String managedId,String manageOrgId){
		List<Map<String,String>> rtnList = new ArrayList<Map<String,String>>();
		Map<String,String> tempMap = null;
		//郭鹏添加查询结果 BussinessOrgRelaEmp.ID
		StringBuffer hqlQuery = new StringBuffer("SELECT emp.id,emp.empname ,orgRelaEmp.managedOrgId.id FROM BussinessOrgRelaEmp orgRelaEmp,SysEmployee emp  WHERE orgRelaEmp.emp.id = emp.id ");
				if (null!=bussinessId) {
					hqlQuery.append("AND orgRelaEmp.bussinessId.id = :bussinessId");
				}
				if (null!=managedId) {
					hqlQuery.append(" AND orgRelaEmp.managedOrgId.id =:managedId");
				}
				if (null!=manageOrgId) {
					hqlQuery.append(" AND orgRelaEmp.manageOrgId.id =:manageOrgId");
				}
		Query query = o_BussinessOrgRelaEmpDAO.createQuery(hqlQuery.toString());
		//郭鹏添加根据参数判断查询条件
		if(null!=bussinessId)
		{
			query.setParameter("bussinessId", bussinessId);
		}
		if (null!=managedId) {
			query.setParameter("managedId", managedId);
		}
		if (null!=manageOrgId) {
			query.setParameter("manageOrgId", manageOrgId);
		}
		List<Object[]> list = query.list();
		for (Object[] objects : list) {
			tempMap = new HashMap<String,String>();
			if(objects[0] != null){
				tempMap.put("id", objects[0].toString());
			}
			if(objects[1] != null){
				tempMap.put("empName", objects[1].toString());
			}
			if(objects[1] != null){
				tempMap.put("orgId", objects[2].toString());
			}
			rtnList.add(tempMap);
		}
		return rtnList;
	}
	
}
