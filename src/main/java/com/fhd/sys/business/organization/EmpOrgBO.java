package com.fhd.sys.business.organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.sys.organization.EmpOrgDAO;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;

/**
 * 
 * ClassName:EmpOrgBO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-11-15		上午11:37:39
 *
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class EmpOrgBO {
	
	@Autowired
	private EmpOrgDAO o_empOrgDAO;
	
	private Map<String, Boolean> map=null;
	/**
	 * 
	 * findByAll:
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysEmpOrg> findByAll(){
		Criteria createCriteria = o_empOrgDAO.createCriteria();
		createCriteria.createAlias("sysEmployee", "sysEmployee");
		createCriteria.createAlias("sysOrganization", "sysOrganization");
		createCriteria.createAlias("sysOrganization.company", "company");
		createCriteria.add(Restrictions.eq("sysEmployee.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		createCriteria.add(Restrictions.eq("sysOrganization.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return createCriteria.list();
	}
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
	public List<SysEmpOrg> findBySome(String empId,String orgId){
		Criteria createCriteria = o_empOrgDAO.createCriteria();
		if(StringUtils.isNotBlank(empId)){
			createCriteria.add(Restrictions.eq("sysEmployee.id", empId));
		}
		if(StringUtils.isNotBlank(orgId)){
			createCriteria.add(Restrictions.eq("sysOrganization.id", orgId));
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
	public void save(SysEmpOrg empOrg){
		o_empOrgDAO.merge(empOrg);
	}
	/**
	 * 
	 * remove:
	 * 
	 * @author 杨鹏
	 * @param empOrg
	 * @since  fhd　Ver 1.1
	 */
	public void remove(SysEmpOrg empOrg){
		o_empOrgDAO.delete(empOrg);
	}
	/**
	 * 
	 * isEmpOrgBySome:根据员工名称、部门名称、公司ID确认数据是否正确
	 * 
	 * @author 杨鹏
	 * @param empName
	 * @param orgName
	 * @param companyId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Boolean isEmpOrgBySome(String empName,String orgName,String companyId){
		Boolean flag=false;
		if(this.map==null){
			this.map=new HashMap<String, Boolean>();
			List<SysEmpOrg> empOrgList = this.findByAll();
			for (SysEmpOrg empOrg : empOrgList) {
				SysEmployee employee = empOrg.getSysEmployee();
				SysOrganization organization = empOrg.getSysOrganization();
				if(employee!=null&&organization!=null){
					SysOrganization company = organization.getCompany();
					if(company!=null){
						String empnameTemp = employee.getEmpname();
						String orgnameTemp = organization.getOrgname();
						String companyIdTemp = company.getId();
						if(StringUtils.isNotBlank(empnameTemp)&&StringUtils.isNotBlank(orgnameTemp)&&StringUtils.isNotBlank(companyIdTemp)){
							map.put(empnameTemp+"\n"+orgnameTemp+"\n"+companyIdTemp, true);
						}
					}
				}
			}
		}
		if(map.get(empName+"\n"+orgName+"\n"+companyId)!=null){
			flag=true;
		}
		return flag;
	}
	
}

