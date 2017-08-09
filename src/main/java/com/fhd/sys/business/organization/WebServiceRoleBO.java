package com.fhd.sys.business.organization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.sys.business.auth.AuthorityBO;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.WSRole;

@Service
public class WebServiceRoleBO{
	@Autowired
	private AuthorityBO o_authorityBO;
	@Autowired
	private RoleBO o_roleBO;
	/**
	 * 
	 * validate:验证
	 * 
	 * @author 杨鹏
	 * @param wSRoleList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Integer validate(List<WSRole> wSRoleList){
		Integer flag=40001;
		try {
			List<SysAuthority> authorityList = o_authorityBO.findByAll();
			Map<String, SysAuthority> authorityMap = new HashMap<String, SysAuthority>();
			for (SysAuthority authority : authorityList) {
				authorityMap.put(authority.getAuthorityCode(), authority);
			}
			List<SysRole> roleList = o_roleBO.findByAll();
			Map<String, SysRole> roleMap = new HashMap<String, SysRole>();
			for (SysRole role : roleList) {
				roleMap.put(role.getRoleCode(), role);
			}
			for (WSRole wsRole : wSRoleList) {
				String editType = wsRole.getEditType();
				String code = wsRole.getCode();
				if (!EditType.save.equals(editType)&&!EditType.update.equals(editType)&&!EditType.remove.equals(editType)) {
					flag = 41001;
					break;
				} else if (!EditType.save.equals(editType)&& roleMap.get(code) == null) {
					flag = 42001;
					break;
				} else if (EditType.save.equals(editType)&& roleMap.get(code) != null) {
					flag = 42001;
					break;
				} else if (StringUtils.isBlank(wsRole.getCode())) {
					flag = 41001;
					break;
				} else if (StringUtils.isBlank(wsRole.getName())) {
					flag = 41002;
					break;
				} else {
					String[] authorityCodes = wsRole.getAuthorityCodes();
					if(authorityCodes!=null){
						for (String authorityCode : authorityCodes) {
							if (authorityMap.get(authorityCode) == null) {
								flag = 41003;
								break;
							}
						}
					}
				}
				if (flag == 40001) {
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
	 * @param wSRoleList
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void edit(List<WSRole> wSRoleList){
		for (WSRole wSRole : wSRoleList) {
			String editType = wSRole.getEditType();
			if(EditType.save.equals(editType)){
				this.save(wSRole);
			}else if(EditType.update.equals(editType)){
				this.merge(wSRole);
			}else if(EditType.remove.equals(editType)){
				this.remove(wSRole);
			}
		}
	}
	
	@Transactional
	public void save(WSRole wSRole){
		String code = wSRole.getCode();
		String name = wSRole.getName();
		String[] authorityCodes = wSRole.getAuthorityCodes();
		SysRole role = new SysRole();
		role.setId(Identities.uuid());
		role.setRoleCode(code);
		role.setRoleName(name);
		role.setSort(o_roleBO.findCountByAll()+1);
		List<SysAuthority> authoritys = o_authorityBO.findByCodes(authorityCodes);
		role.setSysAuthorities(new HashSet<SysAuthority>(authoritys));
		o_roleBO.save(role);
	}
	
	@Transactional
	public void merge(WSRole wSRole){
		String code = wSRole.getCode();
		String name = wSRole.getName();
		String[] authorityCodes = wSRole.getAuthorityCodes();
		List<SysRole> roles = o_roleBO.findByRoleCode(code);
		SysRole role = roles.get(0);
		role.setRoleName(name);
		List<SysAuthority> authoritys = o_authorityBO.findByCodes(authorityCodes);
		role.setSysAuthorities(new HashSet<SysAuthority>(authoritys));
		o_roleBO.merge(role);
	}
	@Transactional
	public void remove(WSRole wSRole){
		String code = wSRole.getCode();
		List<SysRole> roles = o_roleBO.findByRoleCode(code);
		SysRole role = roles.get(0);
		o_roleBO.removeById(role.getId());
	}
}

