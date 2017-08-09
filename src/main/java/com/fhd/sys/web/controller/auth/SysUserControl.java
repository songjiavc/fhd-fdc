package com.fhd.sys.web.controller.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.DigestUtils;
import com.fhd.core.utils.Identities;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.sys.auth.IndexInfo;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.auth.view.VSysUser;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.ObjectUtil;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.SysEmployeeBO;
import com.fhd.sys.business.auth.AuthorityBO;
import com.fhd.sys.business.auth.IndexInfoBO;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.auth.SysUserBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.web.form.auth.SysUserForm;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 用户Controller类.
 * @author  wudefu
 * @version V1.0  创建时间：2010-8-30
 * Company FirstHuiDa.
 */

@Controller
public class SysUserControl {

	@Autowired
	private SysUserBO o_sysUserBO;
	@Autowired
	private AuthorityBO o_sysAuthorityBO;
	@Autowired
	private RoleBO o_sysRoleBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private IndexInfoBO o_indexInfoBO;
	@Autowired
	private SysEmployeeBO o_sysEmployeeBO;
	/**
	 * 
	 * findById:根据ID查询
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/user/findById.f")
	public Map<String, Object> findById(String id){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		SysUser sysUser = o_sysUserBO.findById(id);
		List<IndexInfo> indexInfoList = sysUser.getIndexInfoList();
		String homeUrl="";
		if(indexInfoList.size()>0){
			IndexInfo indexInfo = indexInfoList.get(0);
			homeUrl=indexInfo.getHomeUrl();
		}
		data.put("id", id);
		data.put("username", sysUser.getUsername());
		data.put("password", sysUser.getPassword());
		data.put("regdate", sysUser.getRegdate());
		data.put("expiryDate", sysUser.getExpiryDate());
		data.put("credentialsexpiryDate", sysUser.getCredentialsexpiryDate());
		data.put("userStatus", sysUser.getUserStatus());
		data.put("lockstate", sysUser.getLockstate());
		data.put("enable", sysUser.getEnable());
		data.put("mac", sysUser.getMac());
		data.put("homeUrl", homeUrl);
		map.put("data", data);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 
	 * isUsername:验证用户名是否可用
	 * 
	 * @author 杨鹏
	 * @param id
	 * @param username
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/user/isUsername.f")
	public Boolean isUsername(String id,String username){
		Boolean flag=true;
		List<SysUser> sysUserList = o_sysUserBO.findBySome(null, id, username);
		if(sysUserList.size()>0){
			flag=false;
		}
		return flag;
	}
	
	/**
	 * 
	 * findVSysUserPageBySome:分页查询用户视图列表
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param companyId
	 * @param deleteStatus
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/sys/auth/user/findVSysUserPageBySome.f")
	public Map<String,Object> findVSysUserPageBySome(String query,String companyId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		companyId = UserContext.getUser().getCompanyid();//当前登录人所在公司
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
			for (Map<String, String> sortMap : sortList){
				if("userStatusStr".equals(sortMap.get("property"))){
					sortMap.put("property","userStatus");
				}else if("lockstateStr".equals(sortMap.get("property"))){
					sortMap.put("property","lockstate");
				}else if("enableStr".equals(sortMap.get("property"))){
					sortMap.put("property","enable");
				}else if("regdateStr".equals(sortMap.get("property"))){
					sortMap.put("property","regdate");
				}else if("expiryDateStr".equals(sortMap.get("property"))){
					sortMap.put("property","expiryDate");
				}else if("credentialsexpiryDateStr".equals(sortMap.get("property"))){
					sortMap.put("property","credentialsexpiryDate");
				}
			}
		}
		Page<VSysUser> page=new Page<VSysUser>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysUserBO.findVSysUserPageBySome(page, query, companyId, sortList);
		List<VSysUser> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (VSysUser vSysUser : list) {
			Map<String, Object> vSysUserToMap = vSysUserToMap(vSysUser);
			datas.add(vSysUserToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	

	/** 
	  * @Description: 用户管理查询所有用户列表
	  * @author jia.song@pcitc.com
	  * @date 2017年5月11日 上午10:24:31 
	  * @param query
	  * @param companyId
	  * @param limit
	  * @param start
	  * @param sort
	  * @return
	  * @throws Exception 
	  */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/sys/auth/user/findSysUserPageBySomeSf2.f")
	public Map<String,Object> findVSysUserPageBySomeSf2(String query,String companyId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		companyId = UserContext.getUser().getCompanyid();//当前登录人所在公司
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
			for (Map<String, String> sortMap : sortList){
				if("userStatusStr".equals(sortMap.get("property"))){
					sortMap.put("property","userStatus");
				}else if("lockstateStr".equals(sortMap.get("property"))){
					sortMap.put("property","lockstate");
				}else if("enableStr".equals(sortMap.get("property"))){
					sortMap.put("property","enable");
				}else if("regdateStr".equals(sortMap.get("property"))){
					sortMap.put("property","regdate");
				}else if("expiryDateStr".equals(sortMap.get("property"))){
					sortMap.put("property","expiryDate");
				}else if("credentialsexpiryDateStr".equals(sortMap.get("property"))){
					sortMap.put("property","credentialsexpiryDate");
				}
			}
		}
		Page<SysUser> page=new Page<SysUser>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysUserBO.findSysUserPageBySomeSf2(page, query, companyId, sortList);
		List<SysUser> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (SysUser vSysUser : list) {
			Map<String, Object> vSysUserToMap = vSysUserToMapSf2(vSysUser);
			datas.add(vSysUserToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 查询组件使用方法返回人员和部门列表
	 * @param query
	 * @param orgId
	 * @param roleId
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/user/findSysUserPageByRoleIdAndDeptId.f")
	public Map<String,Object> findSysUserPageByRoleIdAndDeptId(String query,String orgId,String roleId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		//当传入参数都为空查询结果集过大，无意义  直接返回空
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		Map<String,Object> tempMap = null;
		Page<SysEmployee> page=new Page<SysEmployee>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysEmployeeBO.findSysUserPageByDeptIdAndRoleId(page, orgId, roleId, query);
		List<SysEmployee> tempList = page.getResult();
		for(Object temp : tempList){
			Object[] obj = (Object[]) temp;
			tempMap = getMapByIdFromList(obj[0].toString(),dataList);
			if(tempMap.isEmpty()){
				dataList.add(tempMap);
				tempMap.put("roleName", obj[4]);
			}else{
				tempMap.put("roleName", tempMap.get("roleName")+"|"+obj[4]);
			}
			tempMap.put("id", obj[0]);
			tempMap.put("empcode",obj[2]);
			tempMap.put("empname", obj[1]);
			tempMap.put("orgName", obj[3]);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", dataList);
		map.put("success", true);
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/sys/auth/user/findSysUserPageByEmpIds.f")
	public Map<String,Object> findSysUserPageByEmpIds(String[] ids){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> tempList = o_sysEmployeeBO.findSysUserPageByEmpIds(Arrays.asList(ids));
		Map<String,Object> tempMap = null;
		for(Object temp : tempList){
			Object[] obj = (Object[]) temp;
			tempMap = getMapByIdFromList(obj[0].toString(),dataList);
			if(tempMap.isEmpty()){
				dataList.add(tempMap);
				tempMap.put("roleName", obj[4]);
			}else{
				tempMap.put("roleName", tempMap.get("roleName")+"|"+obj[4]);
			}
			tempMap.put("id", obj[0]);
			tempMap.put("empcode",obj[2]);
			tempMap.put("empname", obj[1]);
			tempMap.put("orgName", obj[3]);
		}
		map.put("datas", dataList);
		map.put("success", true);
		return map;
	}
	/**
	 * 判断 list 中是否存在map 的id
	 * @author songjia 
	 */
	private Map<String,Object> getMapByIdFromList(String id,List<Map<String,Object>> paramList){
		Map<String,Object> rtnMap = new HashMap<String,Object>();
		for(Map<String,Object> tempMap : paramList){
			if(id.equals(tempMap.get("id").toString())){
				rtnMap = tempMap;
			}
		}
		return rtnMap;
	}
	
	/** 
	  * @Description: 将用户信息转换为列表显示
	  * @author jia.song@pcitc.com
	  * @date 2017年5月11日 上午10:29:23 
	  * @param vSysUser
	  * @return 
	  */
	public Map<String,Object> vSysUserToMapSf2(SysUser sysUser){
		String id = sysUser.getId();
		String username = sysUser.getUsername();
		String companyName="";
		String userStatusStr="";
		String lockstateStr="";
		String enableStr="";
		String regdateStr="";
		String expiryDateStr="";
		String credentialsexpiryDateStr="";
		String mac="";
		//SysOrganization company = sysUser
//		if(company!=null){
//			companyName = company.getOrgname();
//		}
		String userStatus = sysUser.getUserStatus();
		if(Contents.STATUS_NORMAL.equals(userStatus)){
			userStatusStr="正常";
		}else if(Contents.STATUS_DELETE.equals(userStatus)){
			userStatusStr="注销";
		}
		Boolean lockstate = sysUser.getLockstate();
		if(Contents.LOCK_STATE_NORMAL.equals(lockstate)){
			lockstateStr="正常";
		}else if(Contents.LOCK_STATE_LOCK.equals(lockstate)){
			lockstateStr="锁定";
		}
		Boolean enable = sysUser.getEnable();
		if(Contents.ENABLE_T.equals(enable)){
			enableStr="启用";
		}else if(Contents.ENABLE_F.equals(enable)){
			enableStr="放弃";
		}
		Date regdate = sysUser.getRegdate();
		regdateStr = DateUtils.formatDate(regdate, "yyyy年MM月dd日");
		Date expiryDate = sysUser.getExpiryDate();
		expiryDateStr = DateUtils.formatDate(expiryDate, "yyyy年MM月dd日");
		Date credentialsexpiryDate = sysUser.getCredentialsexpiryDate();
		credentialsexpiryDateStr = DateUtils.formatDate(credentialsexpiryDate, "yyyy年MM月dd日");
		mac = sysUser.getMac();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("username", username);
//		map.put("companyName", companyName);
		map.put("userStatusStr", userStatusStr);
		map.put("lockstateStr", lockstateStr);
		map.put("enableStr", enableStr);
		map.put("regdateStr", regdateStr);
		map.put("expiryDateStr", expiryDateStr);
		map.put("credentialsexpiryDateStr", credentialsexpiryDateStr);
		map.put("mac", mac);
		return map;
	}
	
	/**
	 * 
	 * vSysUserToMap:用户视图转成Map格式
	 * 
	 * @author 杨鹏
	 * @param vSysUser
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> vSysUserToMap(VSysUser vSysUser){
		String id = vSysUser.getId();
		String username = vSysUser.getUsername();
		String companyName="";
		String userStatusStr="";
		String lockstateStr="";
		String enableStr="";
		String regdateStr="";
		String expiryDateStr="";
		String credentialsexpiryDateStr="";
		String mac="";
		SysOrganization company = vSysUser.getCompany();
		if(company!=null){
			companyName = company.getOrgname();
		}
		String userStatus = vSysUser.getUserStatus();
		if(Contents.STATUS_NORMAL.equals(userStatus)){
			userStatusStr="正常";
		}else if(Contents.STATUS_DELETE.equals(userStatus)){
			userStatusStr="注销";
		}
		Boolean lockstate = vSysUser.getLockstate();
		if(Contents.LOCK_STATE_NORMAL.equals(lockstate)){
			lockstateStr="正常";
		}else if(Contents.LOCK_STATE_LOCK.equals(lockstate)){
			lockstateStr="锁定";
		}
		Boolean enable = vSysUser.getEnable();
		if(Contents.ENABLE_T.equals(enable)){
			enableStr="启用";
		}else if(Contents.ENABLE_F.equals(enable)){
			enableStr="放弃";
		}
		Date regdate = vSysUser.getRegdate();
		regdateStr = DateUtils.formatDate(regdate, "yyyy年MM月dd日");
		Date expiryDate = vSysUser.getExpiryDate();
		expiryDateStr = DateUtils.formatDate(expiryDate, "yyyy年MM月dd日");
		Date credentialsexpiryDate = vSysUser.getCredentialsexpiryDate();
		credentialsexpiryDateStr = DateUtils.formatDate(credentialsexpiryDate, "yyyy年MM月dd日");
		mac = vSysUser.getMac();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("username", username);
		map.put("companyName", companyName);
		map.put("userStatusStr", userStatusStr);
		map.put("lockstateStr", lockstateStr);
		map.put("enableStr", enableStr);
		map.put("regdateStr", regdateStr);
		map.put("expiryDateStr", expiryDateStr);
		map.put("credentialsexpiryDateStr", credentialsexpiryDateStr);
		map.put("mac", mac);
		return map;
	}
	
	/**
	 * 
	 * findVSysUserPageBySome:分页查询用户视图列表
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param roleId
	 * @param companyId
	 * @param deleteStatus
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/findVSysUserPageBySome.f")
	public Map<String,Object> findVSysUserPageBySome(String query,String roleId,String companyId,String deleteStatus, Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<VSysUser> page=new Page<VSysUser>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysUserBO.findVSysUserPageBySome(page, query, roleId,companyId, deleteStatus, sortList);
		List<VSysUser> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (VSysUser vSysUser : list) {
			Map<String, Object> vSysUserToRoleMap = vSysUserToRoleMap(vSysUser);
			datas.add(vSysUserToRoleMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	
	
	/**
	 * 重写角色用户关联关系方法
	 */
	
	/**
	 * 查询组件使用方法返回人员和部门列表
	 * @param query
	 * @param orgId
	 * @param roleId
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/findSysUserPageByRoleId.f")
	public Map<String,Object> findSysUserPageByRoleId(String query,String roleId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		//当传入参数都为空查询结果集过大，无意义  直接返回空
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		Map<String,Object> tempMap = null;
		Page<SysEmployee> page=new Page<SysEmployee>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysEmployeeBO.findSysUserPageByRoleId(page, roleId, query);
		List<SysEmployee> tempList = page.getResult();
		for(Object temp : tempList){
			Object[] obj = (Object[]) temp;
			tempMap = getMapByIdFromList(obj[0].toString(),dataList);
			if(tempMap.isEmpty()){
				dataList.add(tempMap);
				tempMap.put("roleNames", obj[4]);
			}else{
				tempMap.put("roleNames", tempMap.get("roleNames")+"|"+obj[4]);
			}
			tempMap.put("id", obj[0]);
			tempMap.put("realname",obj[2]);
			tempMap.put("username", obj[1]);
			tempMap.put("orgname", obj[3]);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", dataList);
		map.put("success", true);
		return map;
	}
	/** 
	  * @Description: 分页查询角色关联的用户
	  * @author jia.song@pcitc.com
	  * @date 2017年5月23日 下午3:27:07 
	  * @param query
	  * @param roleId
	  * @param companyId
	  * @param deleteStatus
	  * @param limit
	  * @param start
	  * @param sort
	  * @return
	  * @throws Exception 
	  */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/findSysUserPageBySome.f")
	public Map<String,Object> findSysUserPageBySome(String query,String roleId,String companyId,String deleteStatus, Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<SysUser> page=new Page<SysUser>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_sysUserBO.findSysUserPageBySome(page, query, roleId,companyId, deleteStatus, sortList);
		List<SysUser> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (SysUser vSysUser : list) {
			Map<String, Object> vSysUserToRoleMap = sysUserToRoleMap(vSysUser);
			datas.add(vSysUserToRoleMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	/**
	 * 
	 * vSysUserToMap:用户视图转成Map格式
	 * 
	 * @author 杨鹏
	 * @param vSysUser
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> vSysUserToRoleMap(VSysUser vSysUser){
		String id = vSysUser.getId();
		String realname = vSysUser.getRealname();
		String username = vSysUser.getUsername();
		String roleNames="";
		String companyName="";
		String userStatusStr="";
		String lockstateStr="";
		Set<SysRole> sysRoles = vSysUser.getSysRoles();
		List<String> roleNameList = new ArrayList<String>();
		for (SysRole sysRole : sysRoles) {
			String roleName = sysRole.getRoleName();
			roleNameList.add(roleName);
		}
		roleNames=StringUtils.join(roleNameList,",");
		SysOrganization company = vSysUser.getCompany();
		if(company!=null){
			companyName = company.getOrgname();
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("username", username);
		map.put("realname", realname);
		map.put("roleNames", roleNames);
		map.put("companyName", companyName);
		map.put("userStatusStr", userStatusStr);
		map.put("lockstateStr", lockstateStr);
		return map;
	}
	
	/**
	 * 
	 * SysUserToMap:用户视图转成Map格式
	 * 
	 * @author 宋佳
	 * @param vSysUser
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> sysUserToRoleMap(SysUser sysUser){
		String id = sysUser.getId();
		String realname = sysUser.getRealname();
		String username = sysUser.getUsername();
		String roleNames="";
		String companyName="";
		String userStatusStr="";
		String lockstateStr="";
		Set<SysRole> sysRoles = sysUser.getSysRoles();
		List<String> roleNameList = new ArrayList<String>();
		for (SysRole sysRole : sysRoles) {
			String roleName = sysRole.getRoleName();
			roleNameList.add(roleName);
		}
		roleNames=StringUtils.join(roleNameList,",");
		/*
			SysOrganization company = sysUser.getCompany();
			if(company!=null){
				companyName = company.getOrgname();
			}
		*/
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("username", username);
		map.put("realname", realname);
		map.put("roleNames", roleNames);
		map.put("companyName", companyName);
		map.put("userStatusStr", userStatusStr);
		map.put("lockstateStr", lockstateStr);
		return map;
	}
	
	/**
	 * 
	 * merge:修改保存用户
	 * 
	 * @author 杨鹏
	 * @param sysUserForm
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/auth/user/merge.f")
	public Boolean merge(SysUserForm form,BindingResult result)throws Exception{
		Boolean flag=false;
		try {
			String id = form.getId();
			SysUser sysUser = new SysUser();
			List<IndexInfo> indexInfoList = new ArrayList<IndexInfo>();
			String password="";
			String passwordNew=form.getPassword();
			if(StringUtils.isNotBlank(id)){//修改
				sysUser = o_sysUserBO.findById(id);
				String passwordOld = sysUser.getPassword();
				if(passwordOld.equals(passwordNew)){//如果未修改
					password=passwordOld;
				}else{//如果修改了
					password=DigestUtils.md5ToHex(passwordNew);
				}
				indexInfoList = sysUser.getIndexInfoList();
			}else{//新增
				form.setId(Identities.uuid());
				password=DigestUtils.md5ToHex(passwordNew);
			}
			sysUser.setId(form.getId());
			sysUser.setUsername(form.getUsername());
			sysUser.setPassword(password);
			sysUser.setRegdate(form.getRegdate());
			sysUser.setRegdate(form.getRegdate());
			sysUser.setExpiryDate(form.getExpiryDate());
			sysUser.setCredentialsexpiryDate(form.getCredentialsexpiryDate());
			sysUser.setUserStatus(form.getUserStatus());
			sysUser.setLockstate(form.getLockstate());
			sysUser.setEnable(form.getEnable());
			sysUser.setMac(form.getMac());
			String homeUrl = form.getHomeUrl();
			if(StringUtils.isNotBlank(homeUrl)){
				IndexInfo indexInfo = new IndexInfo();
				if(indexInfoList.size()>0){
					indexInfo=indexInfoList.get(0);
					indexInfo.setHomeUrl(homeUrl);
					o_indexInfoBO.merge(indexInfo);
				}else{
					indexInfo.setId(Identities.uuid());
					indexInfo.setStatus(Contents.STATUS_NORMAL);
					indexInfo.setHomeUrl(homeUrl);
					o_indexInfoBO.merge(indexInfo);
					indexInfoList.add(indexInfo);
					sysUser.setIndexInfoList(indexInfoList);
				}
			}else{
				indexInfoList.clear();
				sysUser.setIndexInfoList(indexInfoList);
			}
			
			sysUser.setErrCount(0);
			o_sysUserBO.merge(sysUser);
			flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 
	 * userRoleMerge:保存用户角色关系
	 * 
	 * @author 杨鹏
	 * @param form
	 * @param result
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/sys/auth/user/userRoleMerge.f")
	public Boolean userRoleMerge(SysUserForm form,BindingResult result)throws Exception{
		Boolean flag=false;
		try {
			String id = form.getId();
			String[] roleIds = form.getRoleIds();
			SysUser sysUser = o_sysUserBO.findById(id);
			Set<SysRole> sysRoles = sysUser.getSysRoles();
			sysRoles.clear();
			for (String roleId : roleIds) {
				SysRole sysRole = new SysRole();
				sysRole.setId(roleId);
				sysRoles.add(sysRole);
			}
			o_sysUserBO.merge(sysUser);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 
	 * remove:删除用户
	 * 
	 * @author 杨鹏
	 * @param idsStr
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/user/remove.f")
	public void remove(String idsStr){
		String[] ids = StringUtils.split(idsStr,",");
		o_sysUserBO.remove(ids);
	}
	
	/**
	 * 
	 * resetPassword:重置密码
	 * 
	 * @author 杨鹏
	 * @param idsStr
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/user/resetPassword.f")
	public void resetPassword(String idsStr){
		String[] ids = StringUtils.split(idsStr,",");
		o_sysUserBO.resetPassword(ids);
	}
	
	/**
	 * 
	 * getUserHomeUrl:获取登陆用户的主页
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/user/getUserHomeUrl.f")
	public String getUserHomeUrl(){
		String homeUrl ="";
		String userId = UserContext.getUserid();
		SysUser sysUser = o_sysUserBO.findById(userId);
		if(sysUser!=null){
			List<IndexInfo> indexInfoList = sysUser.getIndexInfoList();
			if(!indexInfoList.isEmpty()){
				IndexInfo indexInfo = indexInfoList.get(0);
				homeUrl = indexInfo.getHomeUrl();
			}
		}
		if(StringUtils.isBlank(homeUrl)){
			Set<SysRole> sysRoles = sysUser.getSysRoles();
			for (SysRole sysRole : sysRoles) {
				List<IndexInfo> indexInfoList = sysRole.getIndexInfoList();
				if(!indexInfoList.isEmpty()){
					IndexInfo indexInfo = indexInfoList.get(0);
					homeUrl = indexInfo.getHomeUrl();
				}
				if(StringUtils.isNotBlank(homeUrl)){
					break;
				}
			}
		}
		return homeUrl;
	}
	
    /**
     * 
     * saveUserAuth:添加角色权限对应关系
     * 
     * @author 杨鹏
     * @param userId
     * @param authorityIdsStr
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/user/saveUserAuth.f")
    public void saveUserAuth(String userId,String authorityIdsStr) {
    	String[] authorityIds = StringUtils.split(authorityIdsStr,",");
    	o_sysUserBO.saveUserAuth(userId,authorityIds);
    }
    /**
     * 
     * saveUserAllAuth:给指定角色添加所有权限
     * 
     * @author 杨鹏
     * @param userId
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/user/saveUserAllAuth.f")
    public void saveUserAllAuth(String userId) {
    	o_sysUserBO.saveUserAllAuth(userId);
    }
    

	/**
	 * 
	 * removeUserAuth:删除角色权限对应关系
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @param authorityIdsStr
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/user/removeUserAuth.f")
    public void removeUserAuth(String userId,String authorityIdsStr){
    	String[] authorityIds = StringUtils.split(authorityIdsStr,",");
    	o_sysUserBO.removeUserAuth(userId,authorityIds);
    }
	/**
	 * 
	 * saveUserRemoveAuth:给指定角色去除所有权限
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/user/removeUserAllAuth.f")
    public void removeUserAllAuth(String userId) {
    	o_sysUserBO.removeUserAllAuth(userId);
    }
    
	
	
/**************************新旧方法分割线******************************************/ 
	
	
	/**
	 * 查询所有的用户.
	 * @author 吴德福
	 * @param model
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value="/sys/auth/userList.do")
	public String queryAllUser(Model model,String success){
		List<SysUser> userList = o_sysUserBO.queryAllUser();
		model.addAttribute("userList", userList);
		model.addAttribute("success", success);
		return "sys/auth/user/userList";
	}
	/**
	 * 获取用户列表
	 * @author 万业
	 * @param start
	 * @param limit
	 * @param userName
	 * @param realname
	 * @param fromRoleAss 是从为role指定用户所使用的的标记
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/userListJSON.do")
	public Map<String,Object> queryUser(Model model,int start,int limit,String sort,String dir,String userName,String realname,String roleId){
		Map<String,Object> reMap = new HashMap<String,Object>();
		com.fhd.core.dao.support.Page<SysUser> page = new com.fhd.core.dao.support.Page<SysUser>();
		page.setPageNumber((limit == 0 ? 0 : start / limit)+1);
		page.setObjectsPerPage(limit);
		String ids="";
		String userIdInRole = o_sysUserBO.queryPage(userName,realname,roleId,page,sort,dir);
		List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
		for (SysUser user : page.getList()) {
			Map<String, String> record = new HashMap<String, String>();
			ObjectUtil.objectPropertyToMap(record, user);
			datas.add(record);
			ids=user.getId()+","+ids;
		}
		reMap.put("totalCount", page.getFullListSize());
		reMap.put("datas", datas);
		reMap.put("userIdInRole",userIdInRole);
		return reMap;
	}
	/**
	 * 根据查询条件查询用户.
	 * @author 吴德福
	 * @param model
	 * @param request
	 * @return String 跳转到userList.jsp页面.
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/userByCondition.do")
	public String queryUserByCondition(Model model,HttpServletRequest request){
		model.addAttribute("userList", o_sysUserBO.query(request.getParameter("username")));
		return "sys/auth/user/userList";
	}
	/**
	 * 新增时保存用户Form.
	 * @author 吴德福
	 * @param model
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/userAdd.do")
	public String addUser(Model model){
		model.addAttribute("sysUserForm", new SysUserForm());
		return "sys/auth/user/userAdd";
	}
	/**
	 * 保存用户.
	 * @author 吴德福
	 * @param sysUserForm 权限Form.
	 * @return String
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/userSave.do")
	public void saveUser(SysUserForm sysUserForm,BindingResult result,HttpServletResponse response) throws IOException{
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(sysUserForm, sysUser);
		String homeUrl = sysUserForm.getHomeUrl();
		IndexInfo indexInfo = new IndexInfo();
		List<IndexInfo> indexInfoList = new ArrayList<IndexInfo>();
		if(StringUtils.isNotBlank(homeUrl)){
			indexInfo.setId(Identities.uuid());
			indexInfo.setStatus(Contents.STATUS_NORMAL);
			indexInfo.setHomeUrl(homeUrl);
			o_indexInfoBO.merge(indexInfo);
			indexInfoList.add(indexInfo);
			sysUser.setIndexInfoList(indexInfoList);
		}
		sysUser.setId(Identities.uuid());
		sysUser.setErrCount(0);
		sysUser.setPassword(DigestUtils.md5ToHex(sysUserForm.getPassword()));
		o_sysUserBO.saveUser(sysUser);
		response.getWriter().print("true");
		//return "/commons/openReload";
	}
	/**
	 * 修改时保存用户密码Form.
	 * @author 万业
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/sys/auth/changePwd.do")
	public String changUserPwd(Model model,HttpServletRequest request){
		String uid=request.getParameter("userid");
		SysUser user=o_sysUserBO.queryUserById(uid);
		
		SysUserForm sysUserForm = new SysUserForm();
		BeanUtils.copyProperties(user, sysUserForm);
		model.addAttribute("sysUserForm", sysUserForm);
		return "sys/auth/user/userChangpwd";
		
	}
	/**
	 * 用户密码重置.
	 * @author 吴德福
	 * @param userIds
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value="/sys/auth/resetPwd.do")
	public void changUserPwd(String userIds, HttpServletResponse response) throws IOException {
		PrintWriter out = null;
		String flag = "false";
		try{
			out = response.getWriter();
			if(null != userIds && !"".equals(userIds)){
				String[] userIdArray = userIds.split(",");
				for(String userId : userIdArray){
					SysUser sysUser = o_sysUserBO.get(userId);
					sysUser.setPassword(DigestUtils.md5ToHex(sysUser.getUsername()));
					o_sysUserBO.updateUser(sysUser);
				}
			}
			
			flag = "true";
			out.write(flag);
		}catch(Exception e){
			e.printStackTrace();
			out.write(flag);
		}finally{
			if(null != out){
				out.close();
			}
		}
	}
	/**
	 * 修改用户.
	 * @author 吴德福
	 * @param sysUserForm 模块Form.
	 * @return String 跳转到userList.jsp页面.
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/userUpdate.do")
	public void updateUser(SysUserForm sysUserForm,BindingResult result,HttpServletResponse response) throws IOException{
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(sysUserForm, sysUser);
		String homeUrl = sysUserForm.getHomeUrl();
		List<IndexInfo> indexInfoList = sysUser.getIndexInfoList();
		if(StringUtils.isNotBlank(homeUrl)){
			IndexInfo indexInfo = new IndexInfo();
			if(indexInfoList.size()>0){
				indexInfo=indexInfoList.get(0);
				indexInfo.setHomeUrl(homeUrl);
				o_indexInfoBO.merge(indexInfo);
			}else{
				indexInfo.setId(Identities.uuid());
				indexInfo.setStatus(Contents.STATUS_NORMAL);
				indexInfo.setHomeUrl(homeUrl);
				o_indexInfoBO.merge(indexInfo);
				indexInfoList.add(indexInfo);
				sysUser.setIndexInfoList(indexInfoList);
			}
		}else{
			indexInfoList.clear();
			sysUser.setIndexInfoList(indexInfoList);
		}
		o_sysUserBO.updateUser(sysUser);
		response.getWriter().print("true");
	}
	/**
	 * 修改密码
	 * @author 万业
	 * @param sysUserForm
	 * @param result
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/sys/auth/userUpdatePwd.do")
	public void updateUserPwd(SysUserForm sysUserForm,HttpServletRequest request,HttpServletResponse response) throws IOException{
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(sysUserForm, sysUser);
		sysUser.setPassword(DigestUtils.md5ToHex(sysUserForm.getPassword()));
		o_sysUserBO.updateUser(sysUser);
		response.getWriter().print("true");
	}
	/**
	 * 删除用户.
	 * @author 吴德福
	 * @param id 要删除的权限id数组.
	 * @return String 跳转到userList.jsp页面.
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/userDel.do")
	public String removeUser(String ids,HttpServletResponse response, HttpServletRequest request)throws Exception{
		PrintWriter out = null;
				
		try{
			out = response.getWriter();
			
			String[] userIds = ids.split(",");
			for(String userId : userIds){
				SysUser sysUser = o_sysUserBO.queryUserById(userId);
				//admin用户不可以删除
				if(null != sysUser && !"admin".equals(sysUser.getUsername().trim())){
					SysEmployee employee = o_employeeBO.questEmployeeByUserid(userId);
					if(null != employee){
						employee.setUserid("");
						employee.setUsername(null);
						employee.setRealname(null);
						o_employeeBO.updateEmployee(employee);
					}

					o_sysUserBO.removeUser(userId);
				}
			}
			
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if(null != out){
				out.close();
			}
		}
		return null;
	}
	/**
	 * 给用户分配权限时保存用户Form.
	 * @author 吴德福
	 * @param model
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/userAssignAuthority.do")
	public String userAssignAuthority(Model model,HttpServletRequest request){
		
		String userid = request.getParameter("id");		
		SysUserForm sysUserForm = new SysUserForm();
		if (userid != null) {
			SysUser sysUser = o_sysUserBO.queryUserById(userid);
			BeanUtils.copyProperties(sysUser,sysUserForm);
		}
		//进入角色时显示权限列表
		request.setAttribute("orgRoot", o_sysAuthorityBO.getRootOrg());
		model.addAttribute("sysUserForm", sysUserForm);
		
		return "sys/auth/user/userAssignAuthority";
	}
	/**
	 * ajax取得当前选中的用户拥有的权限.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/queryAuthorityByUserAjax.do")
	public void queryAuthorityAjax(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String userid = request.getParameter("uid");
		PrintWriter out = null;
		
		try {
			out = response.getWriter();
			List<String> authorityIds = o_sysUserBO.queryAuthorityByUser(userid);
			StringBuilder aid = new StringBuilder();
			for(int i=0;i<authorityIds.size();i++){
				aid.append(authorityIds.get(i));
				if(i != authorityIds.size()-1){
					aid.append(",");
				}
			}
			out.write(aid.toString());
		} catch (Exception e) {
			e.printStackTrace();
			out.write("NO");
		}finally{
			if(null != out){
				out.close();
			}
		}
	}
	/**
	 * 给用户分配权限.
	 * @author 吴德福
	 * @param status
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/userAssignAuthoritySubmit.do")
	public void userAssignAuthoritySubmit(SessionStatus status, HttpServletRequest request,HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag = "false";
		
		try {
			out = response.getWriter();
			String userid = request.getParameter("userid");
			String selectIds = request.getParameter("selectIds");
			String[] authorityIds = selectIds.substring(0,selectIds.length()-1).split(",");
			SysUser sysUser = o_sysUserBO.queryUserById(userid);
			Set<SysAuthority> authorities= new HashSet<SysAuthority>(); 
			for(String authorityId : authorityIds) {
				SysAuthority authority = o_sysAuthorityBO.queryAuthorityById(authorityId);//设置级联操作时使用
				authorities.add(authority);
			}
			sysUser.setSysAuthorities(authorities);
			o_sysUserBO.updateUser(sysUser);
			status.setComplete();
			flag="true";
			out.write(flag);
		} catch (DataIntegrityViolationException e){
			e.printStackTrace();
			out.write(flag);
		}finally{
			if(null != out){
				out.close();
			}
		}
	}
	/**
	 * 给用户分配角色.
	 * @author 吴德福
	 * @param sysUserForm 模块Form.
	 * @return String 跳转到userList.jsp页面.
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/userAssignRoleSubmit.do")
	public void userAssignRoleSubmit(SessionStatus status, HttpServletRequest request,HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag = "false";
		
		try {
			out = response.getWriter();
			String userid = request.getParameter("userid");
			String selectIds = request.getParameter("selectIds");
			String[] roleIds = selectIds.substring(0,selectIds.length()-1).split(",");
			SysUser sysUser = o_sysUserBO.queryUserById(userid);
			Set<SysRole> sysRoles= new HashSet<SysRole>(); 
			for(String roleId : roleIds) {
				SysRole sysRole = o_sysRoleBO.queryRoleById(roleId);//设置级联操作时使用
				sysRoles.add(sysRole);
			}
			sysUser.setSysRoles(sysRoles);
			o_sysUserBO.updateUser(sysUser);
			status.setComplete();
			flag="true";
			out.write(flag);
		} catch (DataIntegrityViolationException e){
			e.printStackTrace();
			out.write(flag);
		}finally{
			if(null != out){
				out.close();
			}
		}
	}
	/**
	 * ajax获取用户名过去密码是否正确.
	 * @author 万业
	 * @param request
	 * @param response
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/queryUserByUsername.do")
	public void queryUserByUsername(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
        try {
            String oldpwd = request.getParameter("oldpwd");
            String uid = request.getParameter("uid");
            response.setContentType("text/html;charset=utf-8");
            String flag = "false";
            out = response.getWriter();
            
            SysUser user = o_sysUserBO.queryUserById(uid);
            if(DigestUtils.md5ToHex(oldpwd).equals(user.getPassword())){
            	flag="true";
            }
            out.write(flag);
           
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(null != out){
        		out.close();
        	}
        }
	}
	/**
	 * 用户修改密码.
	 * @author 吴德福
	 * @param oldPassword
	 * @param newPassword
	 * @param confirmPassword
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/user/modPassword.f")
	public Map<String,Object> modPassword(String oldPassword, String newPassword, String confirmPassword) throws Exception {
		Map<String,Object> map = new TreeMap<String,Object>();
		
		if(StringUtils.isNotBlank(oldPassword)){
			SysUser sysUser = o_sysUserBO.getByUsername(UserContext.getUsername());
			if((DigestUtils.md5ToHex(oldPassword).equals(sysUser.getPassword()))){
				if(!newPassword.equals(confirmPassword)){
					map.put("message", "两次输入的密码不一致!");
					map.put("type", "3");
				}else{
					sysUser.setPassword(DigestUtils.md5ToHex(newPassword));
					o_sysUserBO.merge(sysUser);
					map.put("message", "操作成功!");
					map.put("type", "4");
				}
			}else{
				map.put("message", "用户原密码输入不正确!");
				map.put("type", "2");
			}
		}else {
			map.put("message", "用户原密码为空!");
			map.put("type", "1");
		}
		
		return map;
	}
}