package com.fhd.sys.business.orgstructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.cliping.Cliping;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.commons.security.OperatorDetails;
import com.fhd.sys.business.auth.AuthorityBO;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.auth.SysUserBO;
import com.fhd.sys.business.cliping.ClipingBO;
import com.fhd.sys.business.organization.EmployeeBO;

/**
 * UserDetailsBO类.
 * @author   wudefu
 * @version V1.0  创建时间：2010-9-19 
 * @since    Ver 1.1
 * @Date	 2010-9-19		下午12:45:33
 * Company FirstHuiDa.
 * @see 	 
 */
@Service
public class UserDetailsBO implements UserDetailsService {

	@Autowired
	private SysUserBO o_sysUserBO;
	@Autowired
	private EmployeeBO o_empolyeeBO;
	@Autowired
	private ClipingBO o_clipingBO;
	@Autowired
	private RoleBO o_sysRoleBO;
	@Autowired
	private AuthorityBO o_sysAuthorityBO;
	
	
	/**
	 * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		SysUser user = o_sysUserBO.getByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("无此" + username + "用户");
		}
		Set<GrantedAuthority> grantedAuths = obtainGrantedAuthorities(user);
		functionCutAndFormControlsAuthorities(grantedAuths);
		 
		boolean enabled = user.isEnabled();
		boolean accountNonExpired = user.isAccountNonExpired();
		boolean credentialsNonExpired = user.isCredentialsNonExpired();
		boolean accountNonLocked = user.isAccountNonLocked();
		
		OperatorDetails userDetails = new OperatorDetails(user.getUsername(), user.getPassword(), enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuths);
		String userId = user.getId();
		List<Map<String, String>> sortList=new ArrayList<Map<String,String>>();
		Map<String, String> sortMap=new HashMap<String, String>();
		sortMap.put("property", "sort");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		List<SysRole> sysRoleList = o_sysRoleBO.findByUserId(userId, sortList);
		Set<SysRole> sysRoles=new HashSet<SysRole>(sysRoleList);
		userDetails.setSysRoles(sysRoles);
		userDetails.setRealname(user.getRealname());
		userDetails.setUserid(user.getId());
		SysEmployee employee = o_empolyeeBO.getEmployee(user.getId());
		
		if(null != employee){
		    userDetails.setEmpNo(employee.getEmpcode());
		    userDetails.setEmpName(employee.getEmpname());
			// 将员工主责部门放到session
			List<Object[]> orgs = o_empolyeeBO.findMainDeptByEmpid(employee.getId());
			if(null != orgs && orgs.size() > 0) {
			    String orgNo = "";
				Object[] objects = orgs.get(0);
				userDetails.setMajorDeptId(String.valueOf(objects[0]));
				userDetails.setMajorDeptName(String.valueOf(objects[1]));
				if(null!=objects[2]){
				    orgNo = String.valueOf(objects[2]);
				}
				userDetails.setMajorDeptNo(orgNo);
				userDetails.setDivisionManagerId(o_empolyeeBO.findDivisionManager(String.valueOf(objects[0])));
			}
			userDetails.setEmp(employee);
			userDetails.setEmpid(employee.getId());
			userDetails.setCompanyid(employee.getSysOrganization().getId());
			userDetails.setCompanyName(employee.getSysOrganization().getOrgname());
			
		}
		return userDetails;
	}

	/**
	 * 
	 * <pre>
	 * 获得功能剪裁和表单控制权限
	 * </pre>
	 * 
	 * @author 胡迪新
	 * @since  fhd　Ver 1.1
	 */
	private void functionCutAndFormControlsAuthorities(Set<GrantedAuthority> grantedAuths) {
		
		// 功能剪裁 表单控制
		List<Cliping> list = this.o_clipingBO.findClipingAll();
		for (Cliping cliping : list) {
			grantedAuths.add(new GrantedAuthorityImpl("ROLE_"+cliping.getCode()));
		}
		
		
	}
	
	/**
	 * 获得用户所有角色的权限.
	 */
	private Set<GrantedAuthority> obtainGrantedAuthorities(SysUser user) {
		Set<GrantedAuthority> grantedAuthoritySet = new HashSet<GrantedAuthority>();
		Set<String> authorityIdSet = new HashSet<String>();
		grantedAuthoritySet.add(new GrantedAuthorityImpl("ROLE_USER"));
		//读取用户特有的权限
		List<SysAuthority> userAuths = o_sysAuthorityBO.findUserAuthsByUserId(user.getId());
		if (userAuths!=null && !userAuths.isEmpty()) {
			for (SysAuthority authority : userAuths) {
				String idSeq = authority.getIdSeq();
				String[] ids = StringUtils.split(idSeq,".");
				for (String id : ids) {
					authorityIdSet.add(id);
				}
			}
		}
		//读取用户所属角色的权限
		List<SysAuthority> roleAuths = o_sysAuthorityBO.findRoleAuthsByUserId(user.getId());
		if (roleAuths!=null && !roleAuths.isEmpty()) {
			for (SysAuthority authority : roleAuths) {
				String idSeq = authority.getIdSeq();
				String[] ids = StringUtils.split(idSeq,".");
				for (String id : ids) {
					authorityIdSet.add(id);
				}
			}
		}
		
		//判断该用户是否有对应员工
		/*SysEmployee employee = o_empolyeeBO.getEmployee(user.getId());
		if(null != employee){
			//判断员工是否有岗位，如果有岗位，加入岗位的特有权限和岗位角色的权限
						
			
			//如果员工属于工作组，加入工作组的特有权限和工作组角色的权限
			
		}*/
		List<SysAuthority> sysAuthoritys = o_sysAuthorityBO.findBySome(authorityIdSet.toArray(new String[authorityIdSet.size()]), null, null, null, null, null, null, null, null, null);
		for (SysAuthority sysAuthority : sysAuthoritys) {
			grantedAuthoritySet.add(new GrantedAuthorityImpl("ROLE_"+sysAuthority.getAuthorityCode()));
		}
		
		return grantedAuthoritySet;
	}
	
}
