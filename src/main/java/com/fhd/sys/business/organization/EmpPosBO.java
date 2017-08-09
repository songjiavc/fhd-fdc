package com.fhd.sys.business.organization;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.sys.organization.EmpPosDAO;
import com.fhd.entity.sys.orgstructure.SysEmpPosi;


@Service
@SuppressWarnings("unchecked")
public class EmpPosBO {
	
	@Autowired
	private EmpPosDAO o_empPosDAO;
	/**
	 * 
	 * findBySome:条件查询
	 * 
	 * @author 杨鹏
	 * @param empId
	 * @param orgId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysEmpPosi> findBySome(String empId,String posId){
		Criteria createCriteria = o_empPosDAO.createCriteria();
		if(StringUtils.isNotBlank(empId)){
			createCriteria.add(Restrictions.eq("sysEmployee.id", empId));
		}
		if(StringUtils.isNotBlank(posId)){
			createCriteria.add(Restrictions.eq("sysPosition.id", posId));
		}
		return createCriteria.list();
	}
	/**
	 * 
	 * save:保存
	 * 
	 * @author 杨鹏
	 * @param empOrg
	 * @since  fhd　Ver 1.1
	 */
	public void save(SysEmpPosi empPos){
		o_empPosDAO.merge(empPos);
	}
	/**
	 * 
	 * remove:
	 * 
	 * @author 杨鹏
	 * @param empPos
	 * @since  fhd　Ver 1.1
	 */
	public void remove(SysEmpPosi empPos){
		o_empPosDAO.delete(empPos);
	}
}

