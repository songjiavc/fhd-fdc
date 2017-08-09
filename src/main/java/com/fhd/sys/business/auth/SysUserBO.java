
package com.fhd.sys.business.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DigestUtils;
import com.fhd.dao.sys.auth.SysRoleDAOold;
import com.fhd.dao.sys.auth.SysUserDAO;
import com.fhd.dao.sys.auth.SysUserDAOold;
import com.fhd.dao.sys.auth.view.VSysUserDAO;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.auth.view.VSysUser;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.Contents;
import com.fhd.sys.business.log.BusinessLogBO;
import com.fhd.sys.business.organization.EmployeeBO;

/**
 * 用户BO类.
 * 
 * @author wudefu
 * @version V1.0 创建时间：2010-8-30 Company FirstHuiDa.
 */
@Service
@SuppressWarnings({"unchecked","deprecation"})
public class SysUserBO {

	@Autowired
	private VSysUserDAO o_vSysUserDAO;
	@Autowired
	private SysUserDAO o_sysUserDAO;
	@Autowired
	private EmployeeBO o_empolyeeBO;
	@Autowired
	private AuthorityBO o_sysAuthorityBO;
	@Autowired
	private BusinessLogBO o_businessLogBO;
	
	@Autowired
	private SysUserDAOold o_sysUserDAOold;
	@Autowired
	private SysRoleDAOold o_sysRoleDAO;
	
	/**
	 * 
	 * findById:Id查询
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public SysUser findById(String userId){
		Criteria criteria = o_sysUserDAO.createCriteria();
		criteria.setFetchMode("sysRoles", FetchMode.JOIN);
		criteria.add(Restrictions.idEq(userId));
		return (SysUser) criteria.uniqueResult();
	}
	/**
	 * 
	 * findByUsername:
	 * 
	 * @author 杨鹏
	 * @param username
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysUser> findByUsername(String username){
		Criteria criteria = o_sysUserDAO.createCriteria();
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq("userStatus", Contents.STATUS_NORMAL));
		return criteria.list();
		
	}
	
	/**
	 * 
	 * findByUsername:
	 * 
	 * @author 杨鹏
	 * @param username
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysUser> findUserAll(){
		Criteria criteria = o_sysUserDAO.createCriteria();
		criteria.add(Restrictions.eq("userStatus", Contents.STATUS_NORMAL));
		return criteria.list();
		
	}
	
	/**
	 * 
	 * findByIds:根据ID数组查询
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysUser> findByIds(String...ids){
		Criteria criteria = o_sysUserDAO.createCriteria();
		criteria.add(Restrictions.in("id",ids));
		return criteria.list();
	}
	
	/**
	 * 
	 * findBySome:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @param notId
	 * @param username
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysUser> findBySome(String id,String notId,String username){
		Criteria criteria = o_sysUserDAO.createCriteria();
		if(StringUtils.isNotBlank(id)){
			criteria.add(Restrictions.idEq(id));
		}
		if(StringUtils.isNotBlank(notId)){
			criteria.add(Restrictions.not(Restrictions.idEq(notId)));
		}
		if(StringUtils.isNotBlank(username)){
			criteria.add(Restrictions.eq("username",username));
		}
		return criteria.list();
	}
	
	/**
	 * 
	 * findVSysUserPageBySome:分页查询用户视图
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param deleteStatus
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<VSysUser> findVSysUserPageBySome(Page<VSysUser> page,String query,String companyId,List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(VSysUser.class);
		criteria.setFetchMode("sysEmployee", FetchMode.JOIN);
		criteria.setFetchMode("sysEmployee.sysEmpPosis", FetchMode.JOIN);
		criteria.setFetchMode("company", FetchMode.JOIN);
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("username", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("companyId", companyId));
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}
		
		return o_vSysUserDAO.findPage(criteria, page, false);
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
	public Page<SysUser> findSysUserPageBySomeSf2(Page<SysUser> page,String query,String companyId,List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(SysUser.class);
		criteria.setFetchMode("sysEmployee", FetchMode.JOIN);
		criteria.setFetchMode("sysEmployee.sysEmpPosis", FetchMode.JOIN);
		criteria.setFetchMode("company", FetchMode.JOIN);
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("username", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}
		
		return o_sysUserDAO.findPage(criteria, page, false);
	}
	
	/**
	 * 
	 * findVSysUserPageBySome:分页查询用户视图
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param roleId
	 * @param deleteStatus
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<VSysUser> findVSysUserPageBySome(Page<VSysUser> page,String query,String roleId,String companyId,String deleteStatus, List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(VSysUser.class);
		criteria.setFetchMode("sysEmployee", FetchMode.JOIN);
		criteria.setFetchMode("sysEmployee.sysEmpPosis", FetchMode.JOIN);
		criteria.setFetchMode("company", FetchMode.JOIN);
		criteria.createAlias("sysRoles", "sysRoles");
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("username", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		if(StringUtils.isNotBlank(roleId)){
			criteria.add(Restrictions.eq("sysRoles.id", roleId));
		}
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("companyId", companyId));
		}
//		if(StringUtils.isNotBlank(deleteStatus)){
			criteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
//		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}
		
		return o_vSysUserDAO.findPage(criteria, page, false);
	}
	
	/**
	 * 
	 * findSysUserPageBySome:分页查询用户视图
	 * 
	 * @author 宋佳
	 * @param page
	 * @param query
	 * @param roleId
	 * @param deleteStatus
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<SysUser> findSysUserPageBySome(Page<SysUser> page,String query,String roleId,String companyId,String deleteStatus, List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(SysUser.class);
		criteria.setFetchMode("sysEmployee", FetchMode.JOIN);
		criteria.setFetchMode("sysEmployee.sysEmpPosis", FetchMode.JOIN);
		criteria.setFetchMode("company", FetchMode.JOIN);
		criteria.createAlias("sysRoles", "sysRoles");
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("username", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		if(StringUtils.isNotBlank(roleId)){
			criteria.add(Restrictions.eq("sysRoles.id", roleId));
		}
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("companyId", companyId));
		}
//		if(StringUtils.isNotBlank(deleteStatus)){
			criteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
//		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}
		
		return o_sysUserDAO.findPage(criteria, page, false);
	}
	
	/**
	 * 
	 * merge:保存更改用户
	 * 
	 * @author 杨鹏
	 * @param sysUser
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void merge(SysUser sysUser){
		o_sysUserDAO.merge(sysUser);
	}
	/**
	 * 
	 * save:
	 * 
	 * @author 杨鹏
	 * @param sysUser
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void save(SysUser user){
		o_sysUserDAO.merge(user);
	}
	
	/**
	 * 
	 * resetPassword:重置密码
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void resetPassword(String...ids){
		List<SysUser> sysUserList = this.findByIds(ids);
		String password="111111";
		for (SysUser sysUser : sysUserList) {
			sysUser.setPassword(DigestUtils.md5ToHex(password));
			this.merge(sysUser);
		}
	}
	
	/**
	 * 
	 * remove:批量删除用户
	 * 
	 * @author 杨鹏
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void remove(String[] ids){
		List<SysUser> sysUserList = this.findByIds(ids);
		for (SysUser sysUser : sysUserList) {
			String userStatus=Contents.STATUS_DELETE;
			sysUser.setUserStatus(userStatus);
			this.merge(sysUser);
		}
	}
	
	/**
	 * 
	 * saveUserAuth:添加权限关联
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @param authorityIds
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
    public void saveUserAuth(String userId,String...authorityIds) {
    	SysUser sysUser = this.findById(userId);
    	Set<SysAuthority> sysAuthorities = sysUser.getSysAuthorities();
    	List<SysAuthority> sysAuthorityList = o_sysAuthorityBO.findBySome(authorityIds, null, null, null, null, null, null, null);
    	for (SysAuthority sysAuthority : sysAuthorityList) {
    		sysAuthorities.add(sysAuthority);
		}
    	sysUser.setSysAuthorities(sysAuthorities);
    	this.merge(sysUser);
    }
	
	/**
	 * 
	 * saveUserAllAuth:给指定角色添加所有权限
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void saveUserAllAuth(String userId) {
		SysUser sysUser = this.findById(userId);
		Set<SysAuthority> sysAuthorities = sysUser.getSysAuthorities();
		List<String> noIdList=new ArrayList<String>();
		for (SysAuthority sysAuthority : sysAuthorities) {
			String id=sysAuthority.getId();
			noIdList.add(id);
		}
		String[] etypes={"M","G","T"};
		List<SysAuthority> sysAuthoritysTemp = o_sysAuthorityBO.findBySome(null, noIdList.toArray(new String[noIdList.size()]),etypes, null, null, null, null, null, null);
		sysAuthorities.addAll(sysAuthoritysTemp);
		sysUser.setSysAuthorities(sysAuthorities);
		this.merge(sysUser);
	}
	/**
	 * 
	 * removeUserAuth:删除权限关联
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @param authorityIds
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
    public void removeUserAuth(String userId,String...authorityIds){
    	SysUser sysUser = this.findById(userId);
    	Set<SysAuthority> sysAuthorities = sysUser.getSysAuthorities();
    	Map<String,Boolean> authorityIdMap = new HashMap<String, Boolean>();
    	Set<SysAuthority> newSysAuthorities = new HashSet<SysAuthority>();
    	for (String authorityId : authorityIds) {
    		authorityIdMap.put(authorityId, true);
    	}
    	for (SysAuthority sysAuthority : sysAuthorities) {
    		if(authorityIdMap.get(sysAuthority.getId())==null){
    			newSysAuthorities.add(sysAuthority);
    		}
    	}
    	sysUser.setSysAuthorities(newSysAuthorities);
    	this.merge(sysUser);
    }
	
	/**
	 * 
	 * removeUserAllAuth:给指定用户去除所有权限
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeUserAllAuth(String userId) {
		SysUser sysUser = this.findById(userId);
		Set<SysAuthority> sysAuthorities = sysUser.getSysAuthorities();
		Set<SysAuthority> newSysAuthorities = new HashSet<SysAuthority>();
		for (SysAuthority sysAuthority : sysAuthorities) {
			String etype = sysAuthority.getEtype();
			if(!"M".equals(etype)&&!"G".equals(etype)&&!"T".equals(etype)){
				newSysAuthorities.add(sysAuthority);
			}
		}
		sysUser.setSysAuthorities(newSysAuthorities);
		this.merge(sysUser);
	}
	
/********************************************************新旧代码分割线*****************************************************************/	
	/**
	 * 新增用户.
	 * @author 吴德福
	 * @param sysUser
	 * @return Object
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public Object saveUser(SysUser sysUser) {
		SysUser user = new SysUser();
		try {
			user = (SysUser) o_sysUserDAOold.merge(sysUser);
			/*
			// 新增用户时默认为每个用户插入3个portlet：任务列表、新闻、公告，以后维护portal时修改
			if(null != sysUser.getPortal() && "1".equals(sysUser.getPortal())){
				OperatorDetails operator = UserContext.getUser();
				for(int i=1;i<4;i++){
					//新增portal
					Portal portal = new Portal();
					portal.setId(GuidGeneratorUtils.getUUID32());
					portal.setSort(i);
					portal.setCol(i);
					portal.setUpdateTime(new Date());
					portal.setSysUser(sysUser);
					portal.setOperator(operator.getUsername());
					o_portalBO.updatePortal(portal);
					
					//新增portlet
					Portlet portlet = new Portlet();
					portlet.setId(GuidGeneratorUtils.getUUID32());
					portlet.setSort(i);
					portlet.setHeight("300");
					if(i==1){
						portlet.setTitle("任务列表");
						portlet.setUrl("/jbpm/ifarme.do");
					}else if(i==2){
						portlet.setTitle("通知公告");
						portlet.setUrl("/sys/portal/advicesPublish.do");
					}else if(i==3){
						portlet.setTitle("新闻");
						portlet.setUrl("/sys/portal/newsPublish.do");
					}
//					portlet.setPortal(portal);
					portlet.setOperator(operator.getUsername());
					portlet.setUpdateTime(new Date());
					o_portletBO.mege(portlet);
				}
			}
			*/
			o_businessLogBO.saveBusinessLogInterface("新增", "用户", "成功", sysUser.getUsername(), sysUser.getRealname(), sysUser.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.saveBusinessLogInterface("新增", "用户", "失败", sysUser.getUsername(), sysUser.getRealname(),sysUser.getPassword());
		}
		return user;
	}

	/**
	 * 修改用户.
	 * @author 吴德福
	 * @param sysUser 用户.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public void updateUser(SysUser sysUser) {
		try {
			o_sysUserDAOold.merge(sysUser);
			o_businessLogBO.modBusinessLogInterface("修改", "用户", "成功", sysUser.getId(), sysUser.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.modBusinessLogInterface("修改", "用户", "失败", sysUser.getId(), sysUser.getUsername());
		}
	}
	/**
	 * 删除用户.
	 * @author 吴德福
	 * @param id 用户id.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public void removeUser(String id) {
		try {
			//删除人时逻辑删除，不要物理删除，关联太多
			SysUser user = o_sysUserDAOold.get(id);
			user.setEnable(false);
			o_sysUserDAOold.merge(user);
//			o_sysUserDAOold.removeById(id);
			o_businessLogBO.delBusinessLogInterface("删除", "用户", "成功", id);
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.delBusinessLogInterface("删除", "用户", "失败", id);
		}
	}

	/**
	 * 根据id查询用户.
	 * @author 吴德福
	 * @param id 用户id.
	 * @return SysUser 用户.
	 * @since fhd　Ver 1.1
	 */
	public SysUser queryUserById(String id) {
		Criteria criteria = o_sysUserDAOold.createCriteria(Restrictions.eq("id",id));
		criteria.setFetchMode("sysRoles", FetchMode.SELECT);
		criteria.setFetchMode("sysAuthorities", FetchMode.SELECT);
		List<SysUser> list = criteria.list();
		if (!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询所有用户.
	 * @author 吴德福
	 * @return List<SysUser> 用户集合.
	 * @since fhd　Ver 1.1
	 */
	public List<SysUser> queryAllUser() {
		return o_sysUserDAOold.find("from SysUser where enable=true");
	}

	/**
	 * 根据查询条件查询用户.
	 * @author 吴德福
	 * @param username 用户名称.
	 * @return List<SysUser> 用户集合.
	 * @since fhd　Ver 1.1
	 */
	public List<SysUser> query(String username) {
		Criteria criteria = o_sysUserDAOold.createCriteria();
		criteria.setFetchMode("sysRoles", FetchMode.SELECT);
		criteria.setFetchMode("sysAuthorities", FetchMode.SELECT);
		criteria.add(Restrictions.like("username", username, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("enable", true));//过滤无效的
		return criteria.list();
	}
	public List<SysUser> queryUnique(String username) {
		Criteria criteria = o_sysUserDAOold.createCriteria();
		criteria.setFetchMode("sysRoles", FetchMode.SELECT);
		criteria.setFetchMode("sysAuthorities", FetchMode.SELECT);
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq("enable", true));//过滤无效的
		return criteria.list();
	}
	/**
	 * 根据查询条件查询用户. 有分页功能
	 * @param username
	 * @param page
	 */
	public String queryPage(String username,String realname, String roleId, com.fhd.core.dao.support.Page<SysUser> page,String sort,String dir) {
		
		DetachedCriteria criteria =DetachedCriteria.forClass(SysUser.class);
		if(StringUtils.isNotBlank(username)){
			criteria.add(Restrictions.like("username", username, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(realname)){
			criteria.add(Restrictions.like("realname", realname, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("enable", true));
		criteria.addOrder(Order.desc("username"));
		o_sysUserDAOold.pagedQuery(criteria, page);
		
		//查函有此Role的用户
		StringBuffer userIdinRole=new StringBuffer("");
		if(StringUtils.isNotBlank(roleId)){
			SysRole role= o_sysRoleDAO.get(roleId);
			Set<SysUser> userSet=role.getSysUsers();
			for (SysUser user : page.getList()){
				if(userSet.contains(user)){
					//userIdinRole.append(user.getId()+",");
					//作为前台是否为已经选定的标记
					user.setPassword("checked");
				}
				//String hql="from SysRoleUser ru where ru.sysUser.id='"+user.getId()+"' and ru.sysRole.id='"+roleId+"'";
				//String hql="from u from SysUser where "
				
				/*List list=this.o_sysRoleUserDAO.createQuery(hql).list();
				if(list.size()>0){
				}*/
			}
			
		}
		return userIdinRole.toString();
		
	}

	/**
	 * 登录时判断用户名是否可用.
	 * @author 吴德福
	 * @param username
	 * @return List<SysUser>
	 * @since fhd　Ver 1.1
	 */
	public List<SysUser> queryLoginUsername(String username) {
		Criteria criteria = o_sysUserDAOold.createCriteria();
		criteria.setFetchMode("sysRoles", FetchMode.SELECT);
		criteria.setFetchMode("sysAuthorities", FetchMode.SELECT);
		criteria.add(Restrictions.eq("username", username));
		return criteria.list();
	}

	/**
	 * 登录时判断密码是否正确.
	 * @author 吴德福
	 * @param username
	 * @param password
	 * @return List<SysUser>
	 * @since fhd　Ver 1.1
	 */
	public List<SysUser> queryLoginUser(String username, String password) {
		Criteria criteria = o_sysUserDAOold.createCriteria();
		criteria.setFetchMode("sysRoles", FetchMode.SELECT);
		criteria.setFetchMode("sysAuthorities", FetchMode.SELECT);
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq("password", password));
		return criteria.list();
	}

	/**
	 * 根据员工id查询操作员.
	 * @author 吴德福
	 * @param id 员工id.
	 * @return SysUser 用户.
	 * @since fhd　Ver 1.1
	 */
	public SysUser getSysUserByEmpId(String id) {
		SysUser sysUser = null;
		SysEmployee sysEmployee = o_empolyeeBO.get(id);
		if (null != sysEmployee && !"".equals(sysEmployee.getUserid())) {
			sysUser = o_sysUserDAOold.get(sysEmployee.getUserid());
		}
		return sysUser;
	}

	/**
	 * 根据用户名查询用户.
	 * @author 吴德福
	 * @param username 用户名.
	 * @return SysUser 用户.
	 * @since fhd　Ver 1.1
	 */
	public SysUser getByUsername(String username) {
		DetachedCriteria dc = DetachedCriteria.forClass(SysUser.class);
		dc.add(Restrictions.eq("username", StringUtils.lowerCase(username.trim())));
		dc.add(Restrictions.eq("enable", true));
		List<SysUser> sysUserList = o_sysUserDAOold.findByCriteria(dc);
		if(null != sysUserList && sysUserList.size()>0){
			return sysUserList.get(0);
		}
		return null;
	}
	/**
	 * 单点登录：hibernate回调方式--根据用户名查询用户.
	 * @author 吴德福
	 * @param username 用户名.
	 * @return List<SysUser> 用户列表.
	 * @since fhd　Ver 1.1
	 */
	public List<Object[]> findUserInfoListByUsername(final String username){
		final String sql = "select user_name,password from t_sys_user where user_name =:username";
		
		Session session = o_sysUserDAOold.getSessionFactory().openSession();
		//Transaction transaction = session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setString("username", StringUtils.lowerCase(username.trim()));
		List<Object[]> sysUserList = sqlQuery.list();
		//transaction.commit();
		session.close();
		return sysUserList;
		//ENDO--Hibernate回调sql用法
		/*
		return o_sysUserDAOold.getHibernateTemplate().execute(new HibernateCallback<List<SysUser>>() {
			@Override
			public List<SysUser> doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery sqlQuery = session.createSQLQuery(sql);
				sqlQuery.setString("username", StringUtils.lowerCase(username.trim()));
				return sqlQuery.list();
			}
		});
		*/
	}

	/**
	 * 查询用户当前拥有的权限.
	 * @author 吴德福
	 * @param userid 用户id.
	 * @return List<String> 选中的权限集合.
	 * @since fhd　Ver 1.1
	 */
	public List<String> queryAuthorityByUser(String userid) {
		List<String> authorityIds = new ArrayList<String>();
		SysUser sysUser = o_sysUserDAOold.get(userid);
		for (SysAuthority authority : sysUser.getSysAuthorities()) {
			if(authority.getIsLeaf()){
				authorityIds.add(authority.getId());
			}
		}
		return authorityIds;
	}
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public SysUser get(String userId){
		return this.o_sysUserDAOold.get(userId);
		
	}
	
	/**
	 * 修改用户权限.
	 * @author David
	 * @param sysUser 用户.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public void updateUserPermissions(SysUser sysUser,String operatorId,String roleStr) {
		try {
			o_sysUserDAOold.merge(sysUser);
			o_businessLogBO.addModBusinessLog(operatorId,"修改", "用户权限", "成功", sysUser.getId(), roleStr);
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.addModBusinessLog(operatorId,"修改", "用户权限", "失败", sysUser.getId(), roleStr);
		}
	}
}
