package com.fhd.sys.business.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.support.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.auth.RoleDAO;
import com.fhd.dao.sys.auth.SysRoleDAOold;
import com.fhd.dao.sys.auth.SysUserDAOold;
import com.fhd.dao.sys.orgstructure.SysEmployeeDAOold;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.sys.business.log.BusinessLogBO;
import com.fhd.sys.business.organization.EmployeeBO;

@Service
@SuppressWarnings({"unchecked","deprecation"})
public class RoleBO {

	@Autowired
	private SysRoleDAOold o_sysRoleDAOold;
	@Autowired
	private RoleDAO o_roleDAO;
	@Autowired
	private AuthorityBO o_authorityBO;
	@Autowired
	private BusinessLogBO o_businessLogBO;
	@Autowired
	private SysUserDAOold o_sysUserDAOold;
	@Autowired
	private SysEmployeeDAOold employeeDAO;
	@Autowired
	private EmployeeBO o_empolyeeBO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	
	/**
	 * 新增角色.
	 * @author 吴德福
	 * @param sysRole
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public void save(SysRole sysRole) {
		o_roleDAO.merge(sysRole);
	}
	/**
	 * 
	 * findCountByAll:查询数量
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Integer findCountByAll(){
		Criteria criteria = o_roleDAO.createCriteria();
		criteria.setProjection(Projections.rowCount());
		Object object = criteria.uniqueResult();
		return Integer.valueOf(object.toString());
	}
	/**
	 * 
	 * findByAll:
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findByAll(){
		Criteria criteria = o_roleDAO.createCriteria();
		return criteria.list();
	}
	
	/**
	 * 
	 * findByRoleCode:
	 * 
	 * @author 杨鹏
	 * @param roleCode
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findByRoleCode(String roleCode){
		Criteria criteria = o_roleDAO.createCriteria();
		if(StringUtils.isNotBlank(roleCode)){
			criteria.add(Restrictions.eq("roleCode", roleCode));
		}
		return criteria.list();
	}
	/**
	 * 
	 * findByRoleCodes:
	 * 
	 * @author 杨鹏
	 * @param roleCodes
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findByRoleCodes(String...roleCodes){
		Criteria criteria = o_roleDAO.createCriteria();
		criteria.add(Restrictions.in("roleCode", roleCodes));
		return criteria.list();
	}
	/**
	 * 
	 * findByRoleName:
	 * 
	 * @author 杨鹏
	 * @param roleName
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findByRoleName(String roleName){
		Criteria criteria = o_roleDAO.createCriteria();
		if(StringUtils.isNotBlank(roleName)){
			criteria.add(Restrictions.like("roleName", roleName, MatchMode.ANYWHERE));
		}
		return criteria.list();
	}
	/**
	 * 
	 * findByRoleNames:
	 * 
	 * @author 杨鹏
	 * @param roleNames
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findByRoleNames(String...roleNames){
		Criteria criteria = o_roleDAO.createCriteria();
		if(roleNames!=null&&roleNames.length>0){
			criteria.add(Restrictions.in("roleName", roleNames));
		}else{
			criteria.add(Restrictions.eq("roleName", null));
		}
		return criteria.list();
	}
	
	/**
	 * 
	 * findByUserId:根据用户ID查询角色
	 * 
	 * @author 杨鹏
	 * @param userId
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findByUserId(String userId,List<Map<String, String>> sortList) {
		Criteria criteria = o_roleDAO.createCriteria();
		if(StringUtils.isNotBlank(userId)){
			criteria.createAlias("sysUsers", "sysUsers");
			criteria.add(Restrictions.like("sysUsers.id", userId, MatchMode.ANYWHERE));
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
		return criteria.list();
	}
	
	/**
	 * 
	 * findBySome:条件查询
	 * 
	 * @author 杨鹏
	 * @param roleCode
	 * @param roleName
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findBySome(String roleCode,String roleName, String property, String direction){
		Criteria criteria = o_roleDAO.createCriteria();
		if(StringUtils.isNotBlank(roleCode)){
			criteria.add(Restrictions.like("roleCode", roleCode, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(roleName)){
			criteria.add(Restrictions.like("roleName", roleName, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
			if("desc".equalsIgnoreCase(direction)){
				criteria.addOrder(Order.desc(property));
			}else{
				criteria.addOrder(Order.asc(property));
			}
		}
		return criteria.list();
	}
	/**
	 * 
	 * findBySome:条件查询
	 * 
	 * @author 杨鹏
	 * @param roleCode
	 * @param roleName
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findBySome(String roleCode,String roleName, List<Map<String, String>> sortList){
		Criteria criteria = o_roleDAO.createCriteria();
		if(StringUtils.isNotBlank(roleCode)){
			criteria.add(Restrictions.like("roleCode", roleCode, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(roleName)){
			criteria.add(Restrictions.like("roleName", roleName, MatchMode.ANYWHERE));
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
		return criteria.list();
	}
	/**
	 * 
	 * findBySome:条件查询
	 * 
	 * @author 杨鹏
	 * @param noId
	 * @param roleCode
	 * @param roleName
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> findBySome(String noId,String roleCode,String roleName, String property, String direction){
		Criteria criteria = o_roleDAO.createCriteria();
		if(StringUtils.isNotBlank(noId)){
			criteria.add(Restrictions.not(Restrictions.idEq(noId)));
		}
		if(StringUtils.isNotBlank(roleCode)){
			criteria.add(Restrictions.eq("roleCode", roleCode));
		}
		if(StringUtils.isNotBlank(roleName)){
			criteria.add(Restrictions.eq("roleName", roleName));
		}
		if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
			if("desc".equalsIgnoreCase(direction)){
				criteria.addOrder(Order.desc(property));
			}else{
				criteria.addOrder(Order.asc(property));
			}
		}
		return criteria.list();
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
	public SysRole findById(String id){
		Criteria createCriteria = o_roleDAO.createCriteria();
		createCriteria.add(Restrictions.idEq(id));
		return (SysRole)createCriteria.uniqueResult();
	}
	
	/**
	 * 
	 * add:增加一个默认角色
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public SysRole add() {
		SysRole sysRole=new SysRole();
		String id=Identities.uuid();
		String roleCode="newRole_";
		String roleName="newRole_";
		Integer num=1;
		List<SysRole> sysRoleListTemp = this.findBySome(roleCode, null,"roleCode","asc");
		Map<String,Boolean> sysRoleMapTemp = new HashMap<String, Boolean>();
		if(sysRoleListTemp!=null&&sysRoleListTemp.size()>0){
			for (SysRole sysRoleTemp : sysRoleListTemp) {
				String roleCodeTemp = sysRoleTemp.getRoleCode();
				sysRoleMapTemp.put(roleCodeTemp.substring(roleCode.length()), true);
			}
			while (sysRoleMapTemp.get(num+"")!=null){
				num++;
			}
		}
		roleCode+=num;
		
		sysRoleListTemp = this.findBySome(null, roleName,"roleName","desc");
		sysRoleMapTemp = new HashMap<String, Boolean>();
		if(sysRoleListTemp!=null&&sysRoleListTemp.size()>0){
			for (SysRole sysRoleTemp : sysRoleListTemp) {
				String roleNameTemp = sysRoleTemp.getRoleName();
				sysRoleMapTemp.put(roleNameTemp.substring(roleName.length()), true);
			}
			while (sysRoleMapTemp.get(num+"")!=null){
				num++;
			}
		}
		roleName+=num;
		sysRole.setId(id);
		sysRole.setRoleCode(roleCode);
		sysRole.setRoleName(roleName);
		o_roleDAO.merge(sysRole);
		return sysRole;
	}
	
	/**
	 * 
	 * merge:新增或修改角色
	 * 
	 * @author 杨鹏
	 * @param sysRole
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void merge(SysRole sysRole){
		o_roleDAO.merge(sysRole);
	}
	/**
	 * 
	 * removeById:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeById(String id){
		o_roleDAO.delete(id);
	}
	
	/**
	 * 
	 * saveEmpRole:新增用户关系
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @param empIdsStr
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void saveUserRole(String roleId,String...userIds) {
		SysRole sysRole = this.findById(roleId);
		Set<SysUser> sysUsers = sysRole.getSysUsers();
		for (String userId : userIds) {
			SysUser sysUser = new SysUser();
			sysUser.setId(userId);
			sysUsers.add(sysUser);
		}
		sysRole.setSysUsers(sysUsers);
		this.merge(sysRole);
    }
	/**
	 * 
	 * saveEmpRole:根据员工新增用户关系
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @param empIdsStr
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
    public List<String> saveEmpRole(String roleId,String...empIds) {
    	SysRole sysRole = this.findById(roleId);
    	List<SysEmployee> sysEmployees = o_empolyeeBO.findByIds(empIds);
    	Set<SysUser> sysUsers = sysRole.getSysUsers();
    	List<String> compnayIdlist = new ArrayList<String>();
    	List<String> unSaveEmpName = new ArrayList<String>();
    	if("RiskManagemer".equals(roleId)){//验证该公司是否有公司风险管理员角色
    		for(SysUser user : sysUsers){
    			List<SysEmployee> emps = o_riskScoreBO.findEmpEntryByUserId(user.getId());
    			if(emps.size()>0){
    				for(SysEmployee emp : emps){
    					compnayIdlist.add(emp.getSysOrganization().getId());
    				}
    			}
    		}
    	}
    	for (SysEmployee sysEmployee : sysEmployees) {
    		if(compnayIdlist.contains(sysEmployee.getSysOrganization().getId())){//该公司存在风险管理员
    			unSaveEmpName.add(sysEmployee.getEmpname());
    		}else{
    			String userId = sysEmployee.getUserid();
        		SysUser sysUser = new SysUser();
        		sysUser.setId(userId);
        		sysUsers.add(sysUser);
        		compnayIdlist.add(sysEmployee.getSysOrganization().getId());
    		}
		}
    	sysRole.setSysUsers(sysUsers);
    	this.merge(sysRole);
    	return unSaveEmpName;
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
	@Transactional
    public void removeUserRole(String roleId,String...userIds){
    	SysRole sysRole = this.findById(roleId);
    	Set<SysUser> sysUsers = sysRole.getSysUsers();
    	Map<String,Boolean> userIdMap = new HashMap<String, Boolean>();
    	Set<SysUser> newSysUsers = new HashSet<SysUser>();
    	for (String userId : userIds) {
    		userIdMap.put(userId, true);
		}
    	for (SysUser sysUser : sysUsers) {
    		if(userIdMap.get(sysUser.getId())==null){
    			newSysUsers.add(sysUser);
    		}
		}
    	sysRole.setSysUsers(newSysUsers);
    	this.update(sysRole);
    }
	
	/**
	 * 
	 * saveRoleAuth:添加权限关联
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @param authorityIds
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
    public void saveRoleAuth(String roleId,String...authorityIds) {
    	SysRole sysRole = this.findById(roleId);
    	Set<SysAuthority> sysAuthorities = sysRole.getSysAuthorities();
    	List<SysAuthority> sysAuthorityList = o_authorityBO.findBySome(authorityIds, null, null, null, null, null, null, null);
    	for (SysAuthority sysAuthority : sysAuthorityList) {
    		sysAuthorities.add(sysAuthority);
		}
    	sysRole.setSysAuthorities(sysAuthorities);
    	this.merge(sysRole);
    }
	
	/**
	 * 
	 * saveRoleAllAuth:给指定角色添加所有权限
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void saveRoleAllAuth(String roleId) {
		SysRole sysRole = this.findById(roleId);
		Set<SysAuthority> sysAuthorities = sysRole.getSysAuthorities();
		List<String> noIdList=new ArrayList<String>();
		for (SysAuthority sysAuthority : sysAuthorities) {
			String id=sysAuthority.getId();
			noIdList.add(id);
		}
		String[] etypes={"M","G","T"};
		List<SysAuthority> sysAuthoritysTemp = o_authorityBO.findBySome(null, noIdList.toArray(new String[noIdList.size()]),etypes, null, null, null, null, null, null);
		sysAuthorities.addAll(sysAuthoritysTemp);
		sysRole.setSysAuthorities(sysAuthorities);
		this.merge(sysRole);
	}
	/**
	 * 
	 * removeRoleAuth:删除权限关联
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @param authorityIds
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
    public void removeRoleAuth(String roleId,String...authorityIds){
    	SysRole sysRole = this.findById(roleId);
    	Set<SysAuthority> sysAuthorities = sysRole.getSysAuthorities();
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
    	sysRole.setSysAuthorities(newSysAuthorities);
    	this.merge(sysRole);
    }
	
	/**
	 * 
	 * removeRoleAllAuth:给指定角色去除所有权限
	 * 
	 * @author 杨鹏
	 * @param roleId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeRoleAllAuth(String roleId) {
		SysRole sysRole = this.findById(roleId);
		Set<SysAuthority> sysAuthorities = sysRole.getSysAuthorities();
		Set<SysAuthority> newSysAuthorities = new HashSet<SysAuthority>();
		for (SysAuthority sysAuthority : sysAuthorities) {
			String etype = sysAuthority.getEtype();
			if(!"M".equals(etype)&&!"G".equals(etype)&&!"T".equals(etype)){
				newSysAuthorities.add(sysAuthority);
			}
		}
		sysRole.setSysAuthorities(newSysAuthorities);
		this.merge(sysRole);
	}
	
	/**
	 * 查询所有角色id及编号（导入角色验证）
	 * add by 王再冉
	 * 2014-4-24  上午10:37:41
	 * desc : 
	 * @return 
	 * List<String>
	 */
	public List<String> findAllRoleIds() {
		List<String> idSet = new ArrayList<String>();
		Criteria c = o_roleDAO.createCriteria();
		List<SysRole> list = c.list();
		for (SysRole role : list) {
		    idSet.add(role.getId());
		    idSet.add(role.getRoleCode());
		}
		return idSet;
	}
	/**
	 * 批量保存角色数据
	 * add by 王再冉
	 * 2014-4-24  下午1:51:02
	 * desc : 
	 * @param roleList 
	 * void
	 */
	@Transactional
    public void saveRoles(final List<SysRole> roleList) {
        this.o_roleDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into T_SYS_ROLE " +
                		 " (ID,ROLE_CODE,ROLE_NAME) " + 
                		 " values(?,?,?) ";
                
                pst = connection.prepareStatement(sql);
                
                for (SysRole role : roleList) {
					pst.setString(1, role.getId());
					pst.setString(2, role.getRoleCode());
					pst.setString(3, role.getRoleName());
					pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	/**************************新旧方法分割线******************************************/    
	/**
	 * 修改角色.
	 * @author 吴德福
	 * @param sysRole
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public void update(SysRole sysRole) {
		try {
			o_sysRoleDAOold.update(sysRole);
			o_businessLogBO.modBusinessLogInterface("修改", "角色", "成功", sysRole.getId());
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.modBusinessLogInterface("修改", "角色", "失败", sysRole.getId());
		}
	}
	
	/**
	 * 
	 * <pre>
	 * 根据公司ID和角色名称 查询员工
	 * </pre>
	 * 
	 * @author 胡迪新
	 * @param companyId 公司id
	 * @param roleName 角色名称
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysEmployee> getEmpByCorpAndRole(String companyId,String roleName) {
		Criteria criteria = employeeDAO.createCriteria();
		criteria.createAlias("sysUser", "u");
		criteria.createAlias("u.sysRoles", "r");
		criteria.add(Restrictions.eq("r.roleName", roleName));
		criteria.add(Restrictions.eq("sysOrganization.id", companyId));
		return (List<SysEmployee>) criteria.list();
	}
	
	/**
	 * 
	 * <pre>
	 * 根据所在公司和角色名称查询员工
	 * </pre>
	 * 
	 * @author 胡迪新
	 * @param roleName 角色名称
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysEmployee> getEmpByCorpAndRole(String roleName) {
		return getEmpByCorpAndRole(UserContext.getUser().getCompanyid(), roleName);
	}
	
	
	public Set<SysUser> getSysUserByRoleName(String roleName) {
		SysRole sysRole = o_sysRoleDAOold.findUniqueBy("roleName", roleName);
		if(sysRole==null) {
			return null;
		}
		return sysRole.getSysUsers();
	}
	
	/**
	 * 根据id查询角色.
	 * @author 吴德福
	 * @param id
	 * @return SysRole
	 * @since fhd　Ver 1.1
	 */
	public SysRole queryRoleById(String id) {
		return o_sysRoleDAOold.get(id);
	}
	/**
	 * 查询所有角色.
	 * @author 吴德福
	 * @return List<SysRole>
	 * @since fhd　Ver 1.1
	 */
	public List<SysRole> queryAllRole() {
		return o_sysRoleDAOold.getAll();
	}
	/**
	 * 根据查询条件查询角色.
	 * @author 吴德福
	 * @param filters 页面查询条件传递的参数集合.
	 * @return List<SysRole> 模块集合.
	 * @since fhd　Ver 1.1
	 */
	public List<SysRole> query(String roleCode, String roleName) {
		Criteria criteria = o_sysRoleDAOold.createCriteria();
		criteria.setFetchMode("sysUsers", FetchMode.SELECT);
		criteria.setFetchMode("sysAuthorities", FetchMode.SELECT);
		criteria.add(Restrictions.like("roleCode", roleCode, MatchMode.ANYWHERE));
		criteria.add(Restrictions.like("roleName", roleName, MatchMode.ANYWHERE));
		return criteria.list();
	}
	/**
	 * 查询角色当前拥有的权限.
	 * @author 吴德福
	 * @param roleid 角色id.
	 * @return List<String> 选中的权限集合.
	 * @since fhd　Ver 1.1
	 */
	public List<String> queryAuthorityByRole(String roleid) {
		List<String> authorityIds = new ArrayList<String>();
		SysRole sysRole = o_sysRoleDAOold.get(roleid);
		for (SysAuthority authority : sysRole.getSysAuthorities()) {
			authorityIds.add(authority.getId());
		}
		return authorityIds;
	}
	/**
	 * 获取未选中的角色信息.
	 * @param selects
	 * @return List<SysRole>
	 * @return List<SysRole>
	 */
	public List<SysRole> getUnselectedRole(String selects) {
		List<String> ids = new ArrayList<String>();
		if (selects != null) {
			String[] idarr = selects.split(",");
			for (String id : idarr) {
				ids.add(id);
			}
		}
		DetachedCriteria dc = DetachedCriteria.forClass(SysRole.class);
		if(!ids.isEmpty()) {
			dc.add(Restrictions.not(Restrictions.in("id", ids)));
		}
		return this.o_sysRoleDAOold.findByCriteria(dc);
	}
	/**
	 * 分页Role.
	 * @author 万业
	 * @param page
	 * @param role
	 * @return Page<SysRole>
	 */
	public Page<SysRole> querySysRoleByPage(Page<SysRole> page, SysRole role,String sort,String dir){
		DetachedCriteria dc=DetachedCriteria.forClass(SysRole.class);
		if(StringUtils.isNotBlank(role.getRoleCode())){
			dc.add(Restrictions.like("roleCode", role.getRoleCode(), MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(role.getRoleName())){
			dc.add(Restrictions.like("roleName", role.getRoleName(), MatchMode.ANYWHERE));
		}
		if("ASC".equalsIgnoreCase(dir)) {
			if(!"id".equals(sort)){
				dc.addOrder(Order.asc(sort));
			}else{
				dc.addOrder(Order.asc("roleCode"));
			}
		} else {
			if(!"id".equals(sort)){
				dc.addOrder(Order.desc(sort));
			}else{
				dc.addOrder(Order.asc("roleCode"));
			}
		}
		return o_sysRoleDAOold.pagedQuery(dc,page);
		
	}
	/**
	 * 为角色添加用户.
	 * @author 万业
	 * @param roleId 角色
	 * @param selectIds 已选ID
	 * @param allIds 所有ID
	 */
	@Transactional
	public void addBatchUserRole(String roleId,String[] selectIds,String[] allIds){
		if(allIds.length>0){
			
			SysRole role=o_sysRoleDAOold.get(roleId);
			String hql = "select u from SysUser u where u.id in(:allIds)";
			Query query = o_sysUserDAOold.getSessionFactory().getCurrentSession().createQuery(hql);
			query.setParameterList("allIds", allIds);
			List<SysUser> list = query.list();
			for (Iterator<SysUser> iterator = list.iterator(); iterator.hasNext();) {
				SysUser sysUser = iterator.next();
				//role.getSysUsers().remove(sysUser);
				sysUser.getSysRoles().remove(role);
				o_sysUserDAOold.update(sysUser);//关系只交给user方来维护
			}
			//o_sysRoleDAOold.update(role);//清空关系
			
			if(selectIds.length>0){
				for(String userId:selectIds){
					SysUser sysUser=this.o_sysUserDAOold.get(userId);
					role.getSysUsers().add(sysUser);
					sysUser.getSysRoles().add(role);
					o_sysUserDAOold.update(sysUser);
				}
			}
		}else{
			return;
		}
		
	}
	/**
	 * 为角色添加用户.
	 * @author 万业
	 * @param roleId 角色
	 * @param selectIds 已选ID employee id
	 */
	@Transactional
	public void addBatchUserRole(String roleId,String[] selectIds){
			
		SysRole role=o_sysRoleDAOold.get(roleId);
		//清空当前所有关系
		for(SysUser sysUser:role.getSysUsers()){
			sysUser.getSysRoles().remove(role);
			o_sysUserDAOold.update(sysUser);//关系只交给user方来维护
		}
		if(null==selectIds || selectIds.length==0){
			return;
		}
		//设置新关系
		/*
		for(String empId:selectIds){
			String userId=this.o_sysEmployeeDAO.get(empId).getUserid();
			if(StringUtils.isNotBlank(userId)){
				
				SysUser sysUser=this.o_sysUserDAOold.get(userId);
				role.getSysUsers().add(sysUser);
				sysUser.getSysRoles().add(role);
				o_sysUserDAOold.update(sysUser);
				
			}
		}*/
		for(String userId:selectIds){
			if(StringUtils.isNotBlank(userId)){
				
				SysUser sysUser=this.o_sysUserDAOold.get(userId);
				role.getSysUsers().add(sysUser);
				sysUser.getSysRoles().add(role);
				o_sysUserDAOold.update(sysUser);
				
			}
		}
	}
	/**
	 * 获取与roleid相关的employee的 ID
	 * @author 万业
	 * @param roleId
	 * @return
	 */
	public String getRoleEmpIds(String roleId){
		SysRole role = this.o_sysRoleDAOold.get(roleId);
		Set<SysUser> users = role.getSysUsers();
		List<String> userIds=new ArrayList<String>();
		for(SysUser user:users){
			userIds.add(user.getId());
		}
		if(userIds.size() == 0){
			return"";
		}
		String hql = "select emp.id from SysEmployee emp where emp.userid in (:ids)";
		Query query= o_sysRoleDAOold.getSessionFactory().getCurrentSession().createQuery(hql);
		query.setParameterList("ids", userIds);
		
		List<String> list = query.list();
		StringBuilder sb=new StringBuilder();
		for(String id:list){
			sb.append(id);
			sb.append(",");
		}
		return sb.toString();
	}
	/**
	 * 获取与roleid相关的sysuser的 ID
	 * @author 万业
	 * @param roleId
	 * @return
	 */
	public String getRoleUserIds(String roleId){
		SysRole role = this.o_sysRoleDAOold.get(roleId);
		Set<SysUser> users = role.getSysUsers();
		StringBuilder sb=new StringBuilder();
		for(SysUser user:users){
			sb.append(user.getId());
			sb.append(",");
		}
		return sb.toString();
	}
	/**
	 * 查询角色id为roleId下的所有人员列表.
	 * @author 吴德福
	 * @param roleId
	 * @return Set<SysUser>
	 * @since  fhd　Ver 1.1
	 */
	public Set<SysUser> queryUsersByRoleId(String roleId){
		if(StringUtils.isNotBlank(roleId)){
			SysRole role = o_sysRoleDAOold.get(roleId);
			return role.getSysUsers();
		}
		return null;
	}
	/**
	 * 根据角色编号判断角色是否存在.
	 * @author 吴德福
	 * @param roleCode
	 * @return List<SysRole>
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> isExistByRoleCode(String roleCode){
		DetachedCriteria dc = DetachedCriteria.forClass(SysRole.class);
		dc.add(Restrictions.eq("roleCode", roleCode.trim()));
		return o_sysRoleDAOold.findByCriteria(dc);
	}
	/**
	 * 根据角色名称判断角色是否存在.
	 * @author 吴德福
	 * @param roleName
	 * @return List<SysRole>
	 * @since  fhd　Ver 1.1
	 */
	public List<SysRole> isExistByRoleName(String roleName){
		DetachedCriteria dc = DetachedCriteria.forClass(SysRole.class);
		dc.add(Restrictions.eq("roleName", roleName.trim()));
		return o_sysRoleDAOold.findByCriteria(dc);
	}
	/**
	 * 查询除超级管理员角色之外的所有角色.
	 * @return List<SysRole>
	 */
	public List<SysRole> queryRolesNotExistAdmin(){
		DetachedCriteria dc = DetachedCriteria.forClass(SysRole.class);
		dc.add(Restrictions.ne("roleCode", "SYSTEM_ADMIN"));
		return o_sysRoleDAOold.findByCriteria(dc);
	}
	/**
     * 添加角色权限对应关系.
     * @author 吴德福
     * @param roleId 角色id
     * @param authorityIdsStr 权限编号字符串，以","分隔
     * @param type 权限类型：F--表单字段；B--按钮；
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
    public void saveRoleAuthority(String roleId, String authorityIdsStr, String type) {
		Set<SysAuthority> sysAuthoritys = new HashSet<SysAuthority>();
		
    	SysRole sysRole = this.findById(roleId);
    	//角色原有的所有权限
    	Set<SysAuthority> sysAuthorities = sysRole.getSysAuthorities();
    	for (SysAuthority sysAuthority : sysAuthorities) {
    		//过滤掉type类型的所有权限
			if(!sysAuthority.getEtype().equals(type)){
				sysAuthoritys.add(sysAuthority);
			}
		}
    	String[] authorityIds = StringUtils.split(authorityIdsStr,",");
    	List<SysAuthority> sysAuthorityList = o_authorityBO.findBySome(authorityIds, null, null, null, null, null, null, null);
    	for (SysAuthority sysAuthority : sysAuthorityList) {
    		//添加新的type类型的权限
    		sysAuthoritys.add(sysAuthority);
		}
    	sysRole.setSysAuthorities(sysAuthoritys);
    	this.merge(sysRole);
    }
}
