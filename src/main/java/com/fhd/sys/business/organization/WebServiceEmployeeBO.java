package com.fhd.sys.business.organization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.DigestUtils;
import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmpPosi;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.SysPosition;
import com.fhd.fdc.utils.Contents;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.auth.SysUserBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;
import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.WSEmployee;


@Service
public class WebServiceEmployeeBO{
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private PositionBO o_positionBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private SysUserBO o_userBO;
	@Autowired
	private EmpOrgBO o_empOrgBO;
	@Autowired
	private EmpPosBO o_empPosBO;
	@Autowired
	private RoleBO o_roleBO;
	/**
	 * 
	 * validate:验证
	 * 
	 * @author 杨鹏
	 * @param wSEmployeeList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Integer validate(List<WSEmployee> wSEmployeeList){
		Integer flag=30001;
		try {
			List<SysEmployee> employeeList = o_employeeBO.findByAll();
			Map<String,SysEmployee> employeeMap = new HashMap<String, SysEmployee>();
			for (SysEmployee employee : employeeList) {
				employeeMap.put(employee.getEmpcode(), employee);
			}
			
			List<SysOrganization> organizationList = o_organizationBO.findByAll();
			Map<String,SysOrganization> organizationMap = new HashMap<String, SysOrganization>();
			for (SysOrganization organization : organizationList) {
				organizationMap.put(organization.getOrgcode(), organization);
			}
			
			List<SysPosition> positionList = o_positionBO.findByAll();
			Map<String,SysPosition> positionMap = new HashMap<String, SysPosition>();
			for (SysPosition position : positionList) {
				positionMap.put(position.getPosicode(), position);
			}
			
			List<SysRole> roleList = o_roleBO.findByAll();
			Map<String,SysRole> roleMap = new HashMap<String, SysRole>();
			for (SysRole role : roleList) {
				roleMap.put(role.getRoleCode(), role);
			}
			for (WSEmployee wSEmployee : wSEmployeeList) {
				String code = wSEmployee.getCode();
				String name = wSEmployee.getName();
				String companyCode = wSEmployee.getCompanyCode();
				String[] orgCodes = wSEmployee.getOrgCodes();
				String[] positionCodes = wSEmployee.getPositionCodes();
				String[] roleCodes = wSEmployee.getRoleCodes();
				String editType = wSEmployee.getEditType();
				if (!EditType.save.equals(editType)&&!EditType.update.equals(editType)&&!EditType.remove.equals(editType)) {
					flag = 31001;
					break;
				} else if (StringUtils.isBlank(code)) {
					flag = 32001;
					break;
				} else if (!EditType.save.equals(editType)&& employeeMap.get(code) == null) {
					flag = 32001;
					break;
				} else if (EditType.save.equals(editType)&& employeeMap.get(code) != null) {
					flag = 32001;
					break;
				} else if (StringUtils.isBlank(name)){
					flag = 32002;
					break;
				} else if (organizationMap.get(companyCode) == null){
					flag = 32003;
					break;
				} else{
					if(orgCodes!=null){
						for (String orgCode : orgCodes) {
							if(organizationMap.get(orgCode)==null){
								flag = 32006;
								break;
							}
						}
					}
					if(positionCodes!=null){
						for (String positionCode : positionCodes) {
							if(positionMap.get(positionCode)==null){
								flag = 32007;
								break;
							}
						}
					}
					if(roleCodes!=null){
						for (String roleCode : roleCodes) {
							if(roleMap.get(roleCode)==null){
								flag = 32008;
								break;
							}
						}
					}
				}
				if (flag == 30001) {
					flag = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 
	 * edit:编辑
	 * 
	 * @author 杨鹏
	 * @param wSEmployeeList
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void edit(List<WSEmployee> wSEmployeeList){
		for (WSEmployee wSEmployee : wSEmployeeList) {
			String editType = wSEmployee.getEditType();
			if(EditType.save.equals(editType)){
				this.save(wSEmployee);
			}else if(EditType.update.equals(editType)){
				this.merge(wSEmployee);
			}else if(EditType.remove.equals(editType)){
				this.remove(wSEmployee);
			}
		}
	}
	
	@Transactional
	public void save(WSEmployee wSEmployee){
		String code = wSEmployee.getCode();
		String name = wSEmployee.getName();
		String username = wSEmployee.getUsername();
		String password = wSEmployee.getPassword();
		String companyCode = wSEmployee.getCompanyCode();
		String[] orgCodes = wSEmployee.getOrgCodes();
		String[] positionCodes = wSEmployee.getPositionCodes();
		String[] roleCodes = wSEmployee.getRoleCodes();
		List<SysOrganization> companys = o_organizationBO.findByOrgcode(companyCode);
		SysOrganization company=companys.get(0);
		SysEmployee employee=new SysEmployee();
		employee.setId(Identities.uuid());
		employee.setEmpcode(code);
		employee.setUsername(username);
		employee.setEmpname(name);
		employee.setRealname(name);
		SysUser user = new SysUser();
		if(StringUtils.isNotBlank(username)){
			List<SysUser> users = o_userBO.findByUsername(username);
			if(users!=null&&users.size()>0){
				user=users.get(0);
			}else{
				user.setId(Identities.uuid());
				user.setUsername(username);
			}
			user.setRealname(name);
			if(StringUtils.isBlank(password)){
				password="111111";
			}
			user.setPassword(DigestUtils.md5ToHex(password));
			user.setLockstate(false);
			user.setUserStatus(Contents.STATUS_NORMAL);
			user.setEnable(true);
			o_userBO.merge(user);
			employee.setUserid(user.getId());
		}
		employee.setSysOrganization(company);
		employee.setEmpStatus(Contents.STATUS_NORMAL);
		employee.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
		o_employeeBO.save(employee);
		
		if(orgCodes!=null){
			List<SysOrganization> orgs = o_organizationBO.findByOrgcodes(orgCodes);
			List<SysEmpOrg> empOrgs = o_empOrgBO.findBySome(employee.getId(), null);
			for (SysEmpOrg empOrgTemp : empOrgs) {
				o_empOrgBO.remove(empOrgTemp);
			}
			for (SysOrganization orgTemp : orgs) {
				SysEmpOrg empOrg=new SysEmpOrg();
				empOrg.setId(Identities.uuid());
				empOrg.setSysOrganization(orgTemp);
				empOrg.setSysEmployee(employee);
				empOrg.setIsmain(true);
				o_empOrgBO.save(empOrg);
			}
		}
		
		if(positionCodes!=null){
			List<SysPosition> positions = o_positionBO.findByPosicode(positionCodes);
			List<SysEmpPosi> empPosis = o_empPosBO.findBySome(employee.getId(), null);
			for (SysEmpPosi empPosiTemp : empPosis) {
				o_empPosBO.remove(empPosiTemp);
			}
			for (SysPosition positionTemp : positions) {
				SysEmpPosi empPos=new SysEmpPosi();
				empPos.setId(Identities.uuid());
				empPos.setSysPosition(positionTemp);
				empPos.setSysEmployee(employee);
				empPos.setIsmain(true);
				o_empPosBO.save(empPos);
			}
		}
		
		/**
		 * 角色赋予
		 */
		if(roleCodes!=null){
			List<SysRole> roles = o_roleBO.findByRoleCodes(roleCodes);
			user.setSysRoles(new HashSet<SysRole>(roles));
			o_userBO.merge(user);
		}
	}
	
	/**
	 * 
	 * merge:
	 * 
	 * @author 杨鹏
	 * @param wSEmployee
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void merge(WSEmployee wSEmployee){
		String code = wSEmployee.getCode();
		String name = wSEmployee.getName();
		String username = wSEmployee.getUsername();
		String password = wSEmployee.getPassword();
		String companyCode = wSEmployee.getCompanyCode();
		String[] orgCodes = wSEmployee.getOrgCodes();
		String[] positionCodes = wSEmployee.getPositionCodes();
		String[] roleCodes = wSEmployee.getRoleCodes();
		List<SysOrganization> companys = o_organizationBO.findByOrgcode(companyCode);
		SysOrganization company=companys.get(0);
		List<SysEmployee> employees = o_employeeBO.findByEmpcode(code);
		SysEmployee	employee = employees.get(0);
		employee.setUsername(username);
		employee.setEmpname(name);
		employee.setRealname(name);
		SysUser user = new SysUser();
		if(StringUtils.isNotBlank(username)){
			List<SysUser> users = o_userBO.findByUsername(username);
			if(users!=null&&users.size()>0){
				user=users.get(0);
			}else{
				user.setId(Identities.uuid());
				user.setUsername(username);
			}
			user.setRealname(name);
			if(StringUtils.isBlank(password)){
				password="111111";
			}
			user.setPassword(DigestUtils.md5ToHex(password));
			user.setLockstate(false);
			user.setUserStatus(Contents.STATUS_NORMAL);
			user.setEnable(true);
			o_userBO.merge(user);
			employee.setUserid(user.getId());
		}
		employee.setSysOrganization(company);
		employee.setEmpStatus(Contents.STATUS_NORMAL);
		employee.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
		o_employeeBO.save(employee);
		
		List<SysOrganization> orgs = o_organizationBO.findByOrgcodes(orgCodes);
		List<SysEmpOrg> empOrgs = o_empOrgBO.findBySome(employee.getId(), null);
		for (SysEmpOrg empOrgTemp : empOrgs) {
			o_empOrgBO.remove(empOrgTemp);
		}
		for (SysOrganization orgTemp : orgs) {
			SysEmpOrg empOrg=new SysEmpOrg();
			empOrg.setId(Identities.uuid());
			empOrg.setSysOrganization(orgTemp);
			empOrg.setSysEmployee(employee);
			empOrg.setIsmain(true);
			o_empOrgBO.save(empOrg);
		}
		
		List<SysPosition> positions = o_positionBO.findByPosicode(positionCodes);
		List<SysEmpPosi> empPosis = o_empPosBO.findBySome(employee.getId(), null);
		for (SysEmpPosi empPosiTemp : empPosis) {
			o_empPosBO.remove(empPosiTemp);
		}
		for (SysPosition positionTemp : positions) {
			SysEmpPosi empPos=new SysEmpPosi();
			empPos.setId(Identities.uuid());
			empPos.setSysPosition(positionTemp);
			empPos.setSysEmployee(employee);
			empPos.setIsmain(true);
			o_empPosBO.save(empPos);
		}
		
		/**
		 * 角色赋予
		 */
		List<SysRole> roles = o_roleBO.findByRoleCodes(roleCodes);
		user.setSysRoles(new HashSet<SysRole>(roles));
		o_userBO.merge(user);
	}
	/**
	 * 
	 * remove:删除
	 * 
	 * @author 杨鹏
	 * @param code
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void remove(WSEmployee wSEmployee){
		String code = wSEmployee.getCode();
		List<SysEmployee> employees = o_employeeBO.findByEmpcode(code);
		if (employees != null && employees.size() > 0) {
			SysEmployee employee = employees.get(0);
			employee.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
			o_employeeBO.merge(employee);
		}
	}
}

