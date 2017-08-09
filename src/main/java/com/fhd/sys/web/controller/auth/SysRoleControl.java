package com.fhd.sys.web.controller.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import com.fhd.core.dao.support.Page;
import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.auth.IndexInfo;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.ObjectUtil;
import com.fhd.sys.business.auth.AuthorityBO;
import com.fhd.sys.business.auth.IndexInfoBO;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.auth.SysRoleTreeBO;
import com.fhd.sys.business.auth.SysUserBO;
import com.fhd.sys.web.form.auth.SysRoleForm;

/**
 * 角色Controller类.
 * @author  wudefu
 * @version V1.0  创建时间：2010-8-30
 * Company FirstHuiDa.
 */

@Controller
public class SysRoleControl {
	
	@Autowired
	private RoleBO o_sysRoleBO;
	@Autowired
	private SysRoleTreeBO o_sysRoleTreeBO;
	@Autowired
	private AuthorityBO o_sysAuthorityBO;
	@Autowired
	private SysUserBO o_sysUserBO;
	@Autowired
	private IndexInfoBO o_indexInfoBO;
	/**
	 * treeLoader:查询角色树
	 * 
	 * @author 杨鹏
	 * @param query
	 * @return
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/treeLoader.f")
    public Map<String, Object> treeLoader(String query) {
    	return o_sysRoleTreeBO.treeLoader(query);
    }
    
	/**
	 * 
	 * findById:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/findById.f")
	public Map<String, Object> findById(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> data = new HashMap<String, String>();
		SysRole role = o_sysRoleBO.findById(id);
		if(role!=null){
			data.put("roleCode", role.getRoleCode());
			data.put("roleName", role.getRoleName());
			Integer sort = role.getSort();
			String sortStr="";
			if(sort!=null){
				sortStr=sort+"";
			}
			data.put("sort", sortStr);
			String homeUrl ="";
			List<IndexInfo> indexInfoList = role.getIndexInfoList();
			if(indexInfoList.size()>0){
				IndexInfo indexInfo = indexInfoList.get(0);
				homeUrl = indexInfo.getHomeUrl();
			}
			data.put("homeUrl", homeUrl);
		}
		map.put("data", data);
		map.put("success", true);
		return map;
    }
	/**
	 * 
	 * findByUserId:根据用户ID读取角色信息
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/findByUserId.f")
	public Map<String, Object> findByUserId(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String,Object>> roleIdlist = new ArrayList<Map<String, Object>>();
		List<Map<String, String>> sortList= new ArrayList<Map<String,String>>();
		Map<String, String> sortMap=new HashMap<String, String>();
		sortMap.put("property", "sort");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		sortMap=new HashMap<String, String>();
		sortMap.put("property", "roleName");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		List<SysRole> roles = o_sysRoleBO.findByUserId(userId, sortList);
		for (SysRole sysRole : roles) {
			Map<String, Object> roleIdMap=new HashMap<String, Object>();
			String id = sysRole.getId();
			roleIdMap.put("inputValue", id);
			roleIdMap.put("checked", true);
			roleIdlist.add(roleIdMap);
		}
		
		data.put("roleIds", roleIdlist);
		map.put("data", data);
		map.put("success", true);
		return map;
    }
	
	/**
	 * 
	 * findByAll:读取所有角色
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/findByAll.f")
	public List<Map<String, Object>> findByAll(String id) {
		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, String>> sortList= new ArrayList<Map<String,String>>();
		Map<String, String> sortMap=new HashMap<String, String>();
		sortMap.put("property", "sort");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		sortMap=new HashMap<String, String>();
		sortMap.put("property", "roleName");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		List<SysRole> sysRoles = o_sysRoleBO.findBySome(null, null, sortList);
		for (SysRole sysRole : sysRoles) {
			Map<String, Object> map = this.sysRoleToMap(sysRole);
			list.add(map);
		}
		return list;
    }
	
	/**
	 * 
	 * sysRoleToMap:角色实体转成Map格式
	 * 
	 * @author 杨鹏
	 * @param sysRole
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> sysRoleToMap(SysRole sysRole){
		Map<String, Object> map=new HashMap<String, Object>();
		String id = sysRole.getId();
		String roleCode = sysRole.getRoleCode();
		String roleName = sysRole.getRoleName();
		map.put("id", id);
		map.put("roleCode", roleCode);
		map.put("roleName", roleName);
		return map;
	}
	
	/**
	 * 
	 * add:添加一个新角色
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
	@RequestMapping(value = "/sys/auth/role/add.f")
	public String add(){
		SysRole sysRole = o_sysRoleBO.add();
		return sysRole.getId();
	}
    
	/**
	 * 
	 * removeById:根据ID删除角色
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
	@RequestMapping(value = "/sys/auth/role/removeById.f")
	public void removeById(String roleId){
		o_sysRoleBO.removeById(roleId);
	}
    /**
     * 
     * isRoleCode:编号重复验证
     * 
     * @author 杨鹏
     * @param roleCode
     * @param roleId
     * @return
     * @throws Exception
     * @since  fhd　Ver 1.1
     */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/isRoleCode.f")
	public Boolean isRoleCode(String roleCode,String roleId)throws Exception {
		Boolean flag=false;
		List<SysRole> list = o_sysRoleBO.findBySome(roleId, roleCode, null, null, null);
		if(list!=null&&list.size()==0){
			flag=true;
		}
		return flag;
	}
	/**
	 * 
	 * isRoleName:名称重复验证
	 * 
	 * @author 杨鹏
	 * @param roleName
	 * @param roleId
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/isRoleName.f")
	public Boolean isRoleName(String roleName,String roleId)throws Exception {
		Boolean flag=false;
		List<SysRole> list = o_sysRoleBO.findBySome(roleId, null, roleName, null, null);
		if(list!=null&&list.size()==0){
			flag=true;
		}
		return flag;
	}
    
    /**
	 * 
	 * merge:保存修改角色
	 * 
	 * @author 杨鹏
	 * @param sysRoleForm
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/merge.f")
	public Map<String,Object> merge(SysRoleForm form,BindingResult result) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			SysRole sysRole = new SysRole();
			String id = form.getId();
			if(StringUtils.isNotBlank(id)){
				sysRole = o_sysRoleBO.findById(form.getId());
			}else{
				sysRole.setId(Identities.uuid());
			}
			sysRole.setRoleCode(form.getRoleCode());
			sysRole.setRoleName(form.getRoleName());
			sysRole.setSort(form.getSort());
			String homeUrl = form.getHomeUrl();
			List<IndexInfo> indexInfoList = sysRole.getIndexInfoList();
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
					sysRole.setIndexInfoList(indexInfoList);
				}
			}else{
				indexInfoList.clear();
				sysRole.setIndexInfoList(indexInfoList);
			}
			o_sysRoleBO.merge(sysRole);
			map.put("id", sysRole.getId());
			map.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
    
    /**
     * 
     * saveUserRole:新增用户关系
     * 
     * @author 杨鹏
     * @param roleId
     * @param userIds
     * @since  fhd　Ver 1.1
     */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/role/saveUserRole.f")
	public void saveUserRole(String roleId,String userIdsStr) {
		String[] userIds = StringUtils.split(userIdsStr,",");
		o_sysRoleBO.saveUserRole(roleId, userIds);
    }
	
	/**
	 * 
	 * saveEmpRole:根据员工新增用户关系
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @param empIds
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/saveEmpRole.f")
    public List<String> saveEmpRole(String roleId,String empIdsStr) {
    	String[] empIds = StringUtils.split(empIdsStr,",");
    	return o_sysRoleBO.saveEmpRole(roleId, empIds);
    }
	
    /**
     * 
     * removeUserRole:删除用户关联
     * 
     * @author 杨鹏
     * @param roleId
     * @param userIds
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/removeUserRole.f")
    public void removeUserRole(String roleId,String userIdsStr){
    	String[] userIds = StringUtils.split(userIdsStr,",");
    	o_sysRoleBO.removeUserRole(roleId, userIds);
    }
    
    /**
     * 
     * saveRoleAuth:添加角色权限对应关系
     * 
     * @author 杨鹏
     * @param roleId
     * @param authorityIdsStr
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/saveRoleAuth.f")
    public Map<String,Object> saveRoleAuth(String roleId,String authorityIdsStr) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	String[] authorityIds = StringUtils.split(authorityIdsStr,",");
    	o_sysRoleBO.saveRoleAuth(roleId,authorityIds);
    	map.put("success", true);
    	return map;
    }
    /**
     * 
     * saveRoleAllAuth:给指定角色添加所有权限
     * 
     * @author 杨鹏
     * @param roleId
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/saveRoleAllAuth.f")
    public void saveRoleAllAuth(String roleId) {
    	o_sysRoleBO.saveRoleAllAuth(roleId);
    }
    

	/**
	 * 
	 * removeRoleAuth:删除角色权限对应关系
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @param authorityIdsStr
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/removeRoleAuth.f")
    public void removeRoleAuth(String roleId,String authorityIdsStr){
    	String[] authorityIds = StringUtils.split(authorityIdsStr,",");
    	o_sysRoleBO.removeRoleAuth(roleId,authorityIds);
    }
	/**
	 * 
	 * saveRoleRemoveAuth:给指定角色去除所有权限
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/removeRoleAllAuth.f")
    public void removeRoleAllAuth(String roleId) {
    	o_sysRoleBO.removeRoleAllAuth(roleId);
    }
    
/**************************新旧方法分割线******************************************/    
    
    
	/**
	 * 删除角色.
	 * @author 吴德福
	 * @param id 要删除的权限id数组.
	 * @return String 跳转到roleList.jsp页面.
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/roleDel.do")
	public void removeRole(String ids,HttpServletResponse response, HttpServletRequest request)throws Exception{
		PrintWriter out = null;
		try{
			out = response.getWriter();
			String[] roleIds=ids.split(",");
			for(String roleId:roleIds){
				if(StringUtils.isNotBlank(roleId)){
					o_sysRoleBO.removeById(roleId);
				}
			}
			out.write("true");
		} finally {
			out.close();
		}
	}
	
    
	/**
	 * 进入角色页面
	 * @return
	 */
	@RequestMapping(value="/sys/auth/roleList.do")
	public String queryAllRole(){
		return "sys/auth/role/roleList";
	}
	/**
	 * 查询所有的角色.分页 条件
	 * @author 万业
	 * @param model
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/role/roleList.do")
	public Map<String,Object> queryAllRole(Model model,int start,int limit,String sort,String dir,String roleName,String roleCode){
		Map<String,Object> reMap = new HashMap<String,Object>();
		Page<SysRole> page = new Page<SysRole>();
		page.setPageNumber((limit == 0 ? 0 : start / limit) + 1);
		page.setObjectsPerPage(limit);
		SysRole sysRole = new SysRole();
		sysRole.setRoleCode(roleCode);
		sysRole.setRoleName(roleName);
		o_sysRoleBO.querySysRoleByPage(page, sysRole,sort,dir);
		
		List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
		for (SysRole role : page.getList()) {
			Map<String, String> record = new HashMap<String, String>();
			ObjectUtil.objectPropertyToMap(record, role);
			datas.add(record);
		}
		reMap.put("totalCount", page.getFullListSize());
		reMap.put("datas", datas);
		return reMap;
		
	}
	
	
	/**
	 * 根据查询条件查询角色.
	 * @author 吴德福
	 * @param model
	 * @param request
	 * @return String 跳转到roleList.jsp页面.
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/roleByCondition.do")
	public String queryRoleByCondition(Model model,HttpServletRequest request,SysRoleForm sysRoleForm){
		String roleCode = request.getParameter("roleCode");
		String roleName = request.getParameter("roleName");
		List<SysRole> roleList = o_sysRoleBO.query(roleCode,roleName);
		model.addAttribute("roleList", roleList);
		//进入角色时显示右面的权限列表
		request.setAttribute("orgRoot", o_sysAuthorityBO.getRootOrg());
		return "sys/auth/role/roleList";
	}
	
	/**
	 * 新增时保存角色Form.
	 * @author 吴德福
	 * @param model
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/roleAdd.do")
	public String addRole(Model model){
		model.addAttribute("sysRoleForm", new SysRoleForm());
		return "sys/auth/role/roleAdd";
	}
	
	/**
	 * 保存角色.
	 * @author 吴德福
	 * @param sysRoleForm 权限Form.
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/roleSave.do", method = RequestMethod.POST)
	public void saveRole(SysRoleForm sysRoleForm,Model model,HttpServletResponse response, HttpServletRequest request)throws Exception{
		PrintWriter out = null;
		String flag = "false";
		out = response.getWriter();
		
		//根据角色编号判断角色是否存在.
		List<SysRole> sysRoleList1 = o_sysRoleBO.isExistByRoleCode(sysRoleForm.getRoleCode());
		if(null != sysRoleList1 && sysRoleList1.size()>0){
			out.write("角色编号已存在，请修改!");
			return;
		}
		//根据角色名称判断角色是否存在.
		List<SysRole> sysRoleList2 =o_sysRoleBO.isExistByRoleName(sysRoleForm.getRoleName());
		if(null != sysRoleList2 && sysRoleList2.size()>0){
			out.write("角色名称已存在，请修改!");
			return;
		}
		
		SysRole sysRole = new SysRole();
		BeanUtils.copyProperties(sysRoleForm, sysRole);
		sysRole.setRoleCode(sysRoleForm.getRoleCode().toUpperCase());
		sysRole.setId(Identities.uuid());
		
		try{
			o_sysRoleBO.merge(sysRole);
			flag = "true";
			out.write(flag);
		} catch (Exception e) {
			e.printStackTrace();
			out.write(flag);
		} finally {
			out.close();
		}
	}
	
	/**
	 * 修改角色.
	 * @author 吴德福
	 * @param sysRoleForm 模块Form.
	 * @return String 跳转到roleList.jsp页面.
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/roleUpdate.do")
	public void updateRole(SysRoleForm sysRoleForm, HttpServletResponse response, HttpServletRequest request)throws Exception {
		PrintWriter out = null;
		String flag = "false";
		out = response.getWriter();
		
		//根据角色编号判断角色是否存在.
		List<SysRole> sysRoleList1 = o_sysRoleBO.isExistByRoleCode(sysRoleForm.getRoleCode());
		if(null != sysRoleList1 && sysRoleList1.size()>1){
			out.write("角色编号已存在，请修改!");
			return;
		}else if(1 == sysRoleList1.size()){
			if(!sysRoleList1.get(0).getId().equals(sysRoleForm.getId())){
				out.write("角色编号已存在，请修改!");
				return;
			}
		}
		
		//根据角色名称判断角色是否存在.
		List<SysRole> sysRoleList2 = o_sysRoleBO.isExistByRoleName(sysRoleForm.getRoleName());
		if(null != sysRoleList2 && sysRoleList2.size()>1){
			out.write("角色名称已存在，请修改!");
			return;
		}else if(1 == sysRoleList2.size()){
			if(!sysRoleList2.get(0).getId().equals(sysRoleForm.getId())){
				out.write("角色名称已存在，请修改!");
				return;
			}
		}
		
		SysRole sysRole = new SysRole();
		BeanUtils.copyProperties(sysRoleForm, sysRole);
		
		try{
			o_sysRoleBO.merge(sysRole);		
			flag = "true";
			out.write(flag);
		} catch (Exception e) {
			e.printStackTrace();
			out.write(flag);
		} finally {
			out.close();
		}
		
	}
	
	/**
	 * 为角色添加用户
	 * @author 万业
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/sys/auth/roleAssignUser.do")
	public String roleAssignEmp(Model model,HttpServletRequest request){
		String roleid = request.getParameter("id");		
		SysRoleForm sysRoleForm = new SysRoleForm();
		if (roleid != null) {
			SysRole sysRole = o_sysRoleBO.queryRoleById(roleid);
			BeanUtils.copyProperties(sysRole,sysRoleForm);
		}
		model.addAttribute("sysRoleForm", sysRoleForm);
		//model.addAttribute("roleid", roleid);
		
		return "/sys/auth/role/roleAssignUser";
		
	}
	/**
	 * * Ajax给角色分配用户.
	 * @author 万业
	 * @param model
	 * @param roleid 角色ID
	 * @param userid 用户ID 
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/auth/roleAssignUserSubmit.do")
	public void roleAssignEmp(SessionStatus status, HttpServletRequest request,HttpServletResponse response ) throws IOException{
		PrintWriter out = null;
		String flag = "false";
		out = response.getWriter();
		
		try {
			String roleId = request.getParameter("roleid");
			String selectIds = request.getParameter("selectIds");
			String[] selectIdArray=null;
			if(StringUtils.isNotBlank(selectIds.trim())){
				selectIdArray=selectIds.substring(0,selectIds.length()-1).split(",");
				for(int i=0;i<selectIdArray.length;i++){
					SysUser sysUser = o_sysUserBO.get(selectIdArray[i]);
					if(null != sysUser && !"".equals(sysUser.getId())){
						continue;
					}else{
						selectIdArray[i]=o_sysUserBO.getSysUserByEmpId(selectIdArray[i]).getId();
					}
				}
			}
				
			//String allIds=request.getParameter("allIds");
			
			//String[] allIdArray=allIds.substring(0,allIds.length()-1).split(",");
			//this.o_sysRoleBO.addBatchUserRole(roleId,selectIdArray,allIdArray);
			o_sysRoleBO.addBatchUserRole(roleId, selectIdArray);
			status.setComplete();
			
			flag="true";
			out.write(flag);
		} catch (DataIntegrityViolationException e){
			e.printStackTrace();
			out.write(flag);
		}finally{
			out.close();
		}
	}
	/**
	 * 获取某权限已经分配的人员ID
	 * @author 万业
	 * @param request
	 * @param response
	 * @param id role id
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/roleAssignedEmp.do")
	public void getAssignedemp(String id, HttpServletRequest request,HttpServletResponse response)throws Exception{

		if(StringUtils.isBlank(id)){
			return;
		}
		String ids = this.o_sysRoleBO.getRoleEmpIds(id);
		response.getWriter().write(ids);
		
	}
	/**
	 * 获取某权限已经分配的人员ID
	 * @author 万业
	 * @param request
	 * @param response
	 * @param id role id
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/roleAssignedUser.do")
	public void getAssigneduser(String id, HttpServletRequest request,HttpServletResponse response)throws Exception{

		if(StringUtils.isBlank(id)){
			return;
		}
		String ids = this.o_sysRoleBO.getRoleUserIds(id);
		request.getSession().setAttribute("selectId_session", ids);
		response.getWriter().write("sessioncope");
		
	}
	/**
	 * 给角色分配权限时保存角色Form.
	 * @author 吴德福
	 * @param model
	 * @param request
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/roleAssignAuthority.do")
	public String userAssignAuthority(Model model,HttpServletRequest request){
		
		String roleid = request.getParameter("id");		
		SysRoleForm sysRoleForm = new SysRoleForm();
		if (roleid != null) {
			SysRole sysRole = o_sysRoleBO.queryRoleById(roleid);
			BeanUtils.copyProperties(sysRole,sysRoleForm);
		}
		//进入角色时显示权限列表
		request.setAttribute("orgRoot", o_sysAuthorityBO.getRootOrg());
		model.addAttribute("sysRoleForm", sysRoleForm);
		
		return "sys/auth/role/roleAssignAuthority";
	}
	
	/**
	 * ajax取得当前选中的角色拥有的权限.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/queryAuthorityByRoleAjax.do")
	public void queryAuthorityAjax(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String roleid = request.getParameter("rid");
		PrintWriter out = null;
		
		try {
			out = response.getWriter();
			List<String> authorityIds = o_sysRoleBO.queryAuthorityByRole(roleid);
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
	 * 给角色分配权限.
	 * @author 吴德福
	 * @param status
	 * @param request
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sys/auth/roleAssignAuthoritySubmit.do")
	public void roleAssignAuthority(SessionStatus status, HttpServletRequest request,HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag = "false";
		out = response.getWriter();
		
		try {
			String sysRoleId = request.getParameter("rid");
			String selectIds = request.getParameter("selectIds");
			String[] authorityIds = selectIds.substring(0,selectIds.length()-1).split(",");
			
			SysRole sysRole = o_sysRoleBO.queryRoleById(sysRoleId);
			
			Set<SysAuthority> authorities= new HashSet<SysAuthority>(); 
			for(String authorityId : authorityIds) {
				SysAuthority authority = new SysAuthority();
				//authority.setId(authorityId);
				authority = o_sysAuthorityBO.queryAuthorityById(authorityId);//设置级联操作时使用
				authorities.add(authority);
			}
			sysRole.setSysAuthorities(authorities);
		
			o_sysRoleBO.merge(sysRole);
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
	 * 角色分配首页检测.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value="/sys/auth/roleAssignIndex.do")
	public void saveAndDelWarningRegion(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = null;
		String flag = "false";
		
		try{
			response.setContentType("text/html;charset=utf-8");
			out = response.getWriter();
			
			// 接收参数
			String roleId = request.getParameter("roleId");
			
			// 根据角色id查询该角色下的人员
			Set<SysUser> sysRoleUserList = o_sysRoleBO.queryUsersByRoleId(roleId);
			if(null !=sysRoleUserList && sysRoleUserList.size()>0){
				flag = "true";
			}else{
				flag = "该角色下没有人员，请检查!";
			}
			out.write(flag);
		}catch (Exception e) {
			e.printStackTrace();
			out.write(flag);
		} finally {
			if(null != out){
				out.close();
			}
		}
	}
	
	
	@ResponseBody
	@RequestMapping(value="/sys/auth/role/treeloader")
	public List<Map<String, Object>> treeLoader(String node, String query){
		return o_sysRoleTreeBO.treeLoader(node,query);
	}
	
	/**
	 * 根据角色id和etype查询功能权限需要的数据：
	 * 1.主对象及相应的etype类型的权限
	 * 2.当前角色已经赋予的权限
	 * @author 吴德福
	 * @param roleId 角色id
	 * @param etype 值为'B--按钮'或'F--字段'
	 * @return Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/role/findFunctionAuthorityByRoleIdAndEtype.f")
	public Map<String,Object> findFunctionAuthorityByRoleIdAndEtype(String roleId, String etype){
		Map<String,Object> map = new HashMap<String,Object>();
		
		//主对象及对应的etype类型的权限列表
		//排序
		List<Map<String, String>> sortList=new ArrayList<Map<String,String>>();
		Map<String, String> sortMap = new HashMap<String, String>();
		sortMap.put("property", "sn");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		
		//主对象list
		List<SysAuthority> objectAuthorityList = o_sysAuthorityBO.findByEtype("O", sortList);
		//按钮或字段权限列表
		List<SysAuthority> sysAuthorityList = o_sysAuthorityBO.findByEtype(etype, sortList);
		
		Map<String,Object> tempMap = new LinkedHashMap<String,Object>();
		for (SysAuthority objectAuthority : objectAuthorityList) {
			List<Map<String,Object>> tempSysAuthorityList = new ArrayList<Map<String,Object>>();
			Map<String,Object> row = null;
			for (SysAuthority sysAuthority : sysAuthorityList) {
				if(objectAuthority.getId().equals(sysAuthority.getParentAuthority().getId())){
					row = new HashMap<String,Object>();
					
					row.put("authorityCode", sysAuthority.getAuthorityCode());
					row.put("authorityName", sysAuthority.getAuthorityName());
					
					tempSysAuthorityList.add(row);
				}
			}
			tempMap.put(objectAuthority.getAuthorityName(), tempSysAuthorityList);
		}
		map.put("authorityMap", tempMap);
		
		//当前用户的对应的etype类型的权限，以","分隔
		List<String> authorityCodeList = new ArrayList<String>();
		List<SysAuthority> roleAuthorityList = o_sysAuthorityBO.findRoleAuthsByRoleId(roleId, etype);
		for (SysAuthority sysAuthority : roleAuthorityList) {
			authorityCodeList.add(sysAuthority.getAuthorityCode());
		}
		String defaultAuthority = StringUtils.join(authorityCodeList, ",");
		map.put("defaultAuthority", defaultAuthority);
		
		return map;
	}
	/**
	 * 根据角色id和etype查询当前角色已经赋予的权限.
	 * @author 吴德福
	 * @param roleId 角色id
	 * @param etype 值为'B--按钮'或'F--字段'
	 * @return Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value="/sys/auth/role/findDefaulAuthorityByRoleIdAndEtype.f")
	public Map<String,Object> findDefaulAuthorityByRoleIdAndEtype(String roleId, String etype){
		Map<String,Object> map = new HashMap<String,Object>();
		
		//当前用户的对应的etype类型的权限，以","分隔
		List<String> authorityCodeList = new ArrayList<String>();
		List<SysAuthority> roleAuthorityList = o_sysAuthorityBO.findRoleAuthsByRoleId(roleId, etype);
		for (SysAuthority sysAuthority : roleAuthorityList) {
			authorityCodeList.add(sysAuthority.getAuthorityCode());
		}
		String defaultAuthority = StringUtils.join(authorityCodeList, ",");
		map.put("defaultAuthority", defaultAuthority);
		
		return map;
	}
	 /**
     * 添加角色权限对应关系.
     * @author 吴德福
     * @param roleId 角色id
     * @param authorityIdsStr 权限编号字符串，以","分隔
     * @param type 权限类型：F--表单字段；B--按钮；
     * @since  fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/auth/role/saveRoleAuthority.f")
    public Map<String,Object> saveRoleAuthority(String roleId,String authorityIdsStr, String type) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	o_sysRoleBO.saveRoleAuthority(roleId,authorityIdsStr,type);
    	map.put("success", true);
    	return map;
    }
}