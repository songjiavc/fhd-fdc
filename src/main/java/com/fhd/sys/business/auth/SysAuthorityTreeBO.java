package com.fhd.sys.business.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.sys.auth.AuthorityDAO;
import com.fhd.dao.sys.auth.view.VSysAuthorityDAO;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.auth.view.VSysAuthority;

@Service
public class SysAuthorityTreeBO {

	@Autowired
	private AuthorityDAO o_sysAuthorityDAO;
	@Autowired
	private VSysAuthorityDAO o_vSysAuthorityDAO;
	@Autowired
	private AuthorityBO o_sysAuthorityBO;
	@Autowired
	private SysUserBO o_sysUserBO;
	@Autowired
	private RoleBO o_sysRoleBO;

	/**
	 * 
	 * getAuthorityTreeRoot:查询根节点
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public SysAuthority getTreeRoot() {
		Criteria createCriteria = o_sysAuthorityDAO.createCriteria();
		createCriteria.add(Restrictions.isNull("parentAuthority"));
		return (SysAuthority)createCriteria.uniqueResult();
	}
	/**
	 * 
	 * findNodeIdByNoUserId:查找指定用户不包含的权限列表
	 * 
	 * @author 杨鹏
	 * @param noUserId:用户Id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Set<String> findNodeIdByNoUserId(String noUserId){
		Boolean isLeaf=true;
		Set<String> nodeIds = new HashSet<String>();
		SysUser sysUser = o_sysUserBO.findById(noUserId);
		if(null!=sysUser){
			Set<SysAuthority> sysAuthorities = sysUser.getSysAuthorities();
			List<String> noIdList = new ArrayList<String>();
			for (SysAuthority sysAuthority : sysAuthorities) {
				String sysAuthorityId = sysAuthority.getId();
				noIdList.add(sysAuthorityId);
			}
			List<SysAuthority> sysAuthoritieList = o_sysAuthorityBO.findBySome(null, noIdList.toArray(new String[noIdList.size()]), null, null, isLeaf, null, null, null);
			for (SysAuthority sysAuthority : sysAuthoritieList) {
				String idSeq = sysAuthority.getIdSeq();
				String[] ids = StringUtils.split(idSeq,".");
				for (String id : ids) {
					nodeIds.add(id);
				}
			}
		}
		return nodeIds;
	}
	/**
	 * 
	 * findNodeIdByUserId:查找指定用户包含的权限列表
	 * 
	 * @author 杨鹏
	 * @param userId:角色ID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Set<String> findNodeIdByUserId(String userId){
		Boolean isLeaf=true;
		Set<String> nodeIds = new HashSet<String>();
		SysUser sysUser = o_sysUserBO.findById(userId);
		if(sysUser!=null){
			Set<SysAuthority> sysAuthorities = sysUser.getSysAuthorities();
			List<String> idList = new ArrayList<String>();
			for (SysAuthority sysAuthority : sysAuthorities) {
				String sysAuthorityId = sysAuthority.getId();
				idList.add(sysAuthorityId);
			}
			List<SysAuthority> sysAuthoritieList = o_sysAuthorityBO.findBySome(idList.toArray(new String[idList.size()]), null, null, null, isLeaf, null, null, null);
			for (SysAuthority sysAuthority : sysAuthoritieList) {
				String idSeq = sysAuthority.getIdSeq();
				String[] ids = StringUtils.split(idSeq,".");
				for (String id : ids) {
					nodeIds.add(id);
				}
			}
		}
		return nodeIds;
	}
	/**
	 * 
	 * findNodeIdByNoRoleId:查找指定角色不包含的权限列表
	 * 
	 * @author 杨鹏
	 * @param noRoleId:角色Id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
    public Set<String> findNodeIdByNoRoleId(String noRoleId){
    	Boolean isLeaf=true;
        Set<String> nodeIds = new HashSet<String>();
        SysRole sysRole = o_sysRoleBO.findById(noRoleId);
        if(null!=sysRole){
        	Set<SysAuthority> sysAuthorities = sysRole.getSysAuthorities();
        	List<String> noIdList = new ArrayList<String>();
        	for (SysAuthority sysAuthority : sysAuthorities) {
        		String sysAuthorityId = sysAuthority.getId();
        		noIdList.add(sysAuthorityId);
        	}
        	List<SysAuthority> sysAuthoritieList = o_sysAuthorityBO.findBySome(null, noIdList.toArray(new String[noIdList.size()]), null, null, isLeaf, null, null, null);
        	for (SysAuthority sysAuthority : sysAuthoritieList) {
        		String idSeq = sysAuthority.getIdSeq();
        		String[] ids = StringUtils.split(idSeq,".");
        		for (String id : ids) {
        			nodeIds.add(id);
        		}
        	}
        }
        return nodeIds;
    }
	/**
	 * 
	 * findNodeIdByRoleId:查找指定角色包含的权限列表
	 * 
	 * @author 杨鹏
	 * @param roleId:角色ID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
    public Set<String> findNodeIdByRoleId(String roleId){
    	Boolean isLeaf=true;
        Set<String> nodeIds = new HashSet<String>();
        SysRole sysRole = o_sysRoleBO.findById(roleId);
        if(sysRole!=null){
        	Set<SysAuthority> sysAuthorities = sysRole.getSysAuthorities();
        	List<String> idList = new ArrayList<String>();
        	for (SysAuthority sysAuthority : sysAuthorities) {
        		String sysAuthorityId = sysAuthority.getId();
        		idList.add(sysAuthorityId);
        	}
        	List<SysAuthority> sysAuthoritieList = o_sysAuthorityBO.findBySome(idList.toArray(new String[idList.size()]), null, null, null, isLeaf, null, null, null);
        	for (SysAuthority sysAuthority : sysAuthoritieList) {
        		String idSeq = sysAuthority.getIdSeq();
        		String[] ids = StringUtils.split(idSeq,".");
        		for (String id : ids) {
        			nodeIds.add(id);
        		}
        	}
        }
        return nodeIds;
    }
    /**
     * 
     * findBySome:多条件查询视图
     * 
     * @author 杨鹏
     * @param ids
     * @param noIds
     * @param authorityName
     * @param authorityNameSeq
     * @param parentAuthorityId
     * @param isLeaf
     * @param parentAuthorityIdIsNull
     * @param property
     * @param direction
     * @return
     * @since  fhd　Ver 1.1
     */
	@SuppressWarnings("unchecked")
	public List<VSysAuthority> findBySome(String[] ids,String[] noIds,String authorityName,String authorityNameSeq,String parentAuthorityId,Boolean isLeaf,Boolean parentAuthorityIdIsNull, String property, String direction){
		Criteria createCriteria = o_vSysAuthorityDAO.createCriteria();
		if(StringUtils.isNotBlank(authorityName)){
			createCriteria.add(Restrictions.like("authorityName", authorityName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(authorityNameSeq)){
			Criteria createCriteriaId = o_vSysAuthorityDAO.createCriteria();
			createCriteriaId.add(Restrictions.like("authorityName", authorityNameSeq,MatchMode.ANYWHERE));
			createCriteriaId.setProjection(Property.forName("idSeq"));
			List<String> idSeqListTemp = createCriteriaId.list();
			Set<String> idList = new HashSet<String>();
			for (String idSeq : idSeqListTemp) {
				String[] idsTemp = StringUtils.split(idSeq,".");
				for (String idTemp : idsTemp) {
					idList.add(idTemp);
				}
			}
			createCriteria.add(Restrictions.in("id", idList));
		}
		if(ids!=null){
			if(ids.length==0){
				createCriteria.add(Restrictions.isNull("id"));
			}else{
				createCriteria.add(Restrictions.in("id", ids));
			}
		}
		if(noIds!=null){
			if(noIds.length==0){
				createCriteria.add(Restrictions.isNotNull("id"));
			}else{
				createCriteria.add(Restrictions.not(Restrictions.in("id", noIds)));
			}
		}
		if(StringUtils.isNotBlank(parentAuthorityId)){
			createCriteria.add(Restrictions.eq("parentAuthority.id", parentAuthorityId));
		}
		if(isLeaf!=null){
			createCriteria.add(Restrictions.eq("isLeaf", isLeaf));
		}
		if(parentAuthorityIdIsNull!=null&&parentAuthorityIdIsNull){
			createCriteria.add(Restrictions.isNull("parentAuthority.id"));
		}
		if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
			if("desc".equalsIgnoreCase(direction)){
				createCriteria.addOrder(Order.desc(property));
			}else{
				createCriteria.addOrder(Order.asc(property));
			}
		}
		return createCriteria.list();
	}
	/**
	 * 
	 * findBySome:
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @param noIds
	 * @param etypes
	 * @param authorityName
	 * @param authorityNameSeq
	 * @param parentAuthorityId
	 * @param isLeaf
	 * @param parentAuthorityIdIsNull
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<VSysAuthority> findBySome(String[] ids,String[] noIds,String[] etypes,String authorityName,String authorityNameSeq,String parentAuthorityId,Boolean isLeaf,Boolean parentAuthorityIdIsNull, String property, String direction){
		Criteria createCriteria = o_vSysAuthorityDAO.createCriteria();
		if(StringUtils.isNotBlank(authorityName)){
			createCriteria.add(Restrictions.like("authorityName", authorityName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(authorityNameSeq)){
			Criteria createCriteriaId = o_vSysAuthorityDAO.createCriteria();
			createCriteriaId.add(Restrictions.like("authorityName", authorityNameSeq,MatchMode.ANYWHERE));
			createCriteriaId.setProjection(Property.forName("idSeq"));
			List<String> idSeqListTemp = createCriteriaId.list();
			Set<String> idList = new HashSet<String>();
			for (String idSeq : idSeqListTemp) {
				String[] idsTemp = StringUtils.split(idSeq,".");
				for (String idTemp : idsTemp) {
					idList.add(idTemp);
				}
			}
			createCriteria.add(Restrictions.in("id", idList));
		}
		if(ids!=null){
			if(ids.length==0){
				createCriteria.add(Restrictions.isNull("id"));
			}else{
				createCriteria.add(Restrictions.in("id", ids));
			}
		}
		if(noIds!=null){
			if(noIds.length==0){
				createCriteria.add(Restrictions.isNotNull("id"));
			}else{
				createCriteria.add(Restrictions.not(Restrictions.in("id", noIds)));
			}
		}
		if(etypes!=null){
			if(etypes.length==0){
				createCriteria.add(Restrictions.isNotNull("etype"));
			}else{
				createCriteria.add(Restrictions.in("etype", etypes));
			}
		}
		if(StringUtils.isNotBlank(parentAuthorityId)){
			createCriteria.add(Restrictions.eq("parentAuthority.id", parentAuthorityId));
		}
		if(isLeaf!=null){
			createCriteria.add(Restrictions.eq("isLeaf", isLeaf));
		}
		if(parentAuthorityIdIsNull!=null&&parentAuthorityIdIsNull){
			createCriteria.add(Restrictions.isNull("parentAuthority.id"));
		}
		if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
			if("desc".equalsIgnoreCase(direction)){
				createCriteria.addOrder(Order.desc(property));
			}else{
				createCriteria.addOrder(Order.asc(property));
			}
		}
		return createCriteria.list();
	}
    
	/**
	 * 
	 * treeLoader:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param nodeId:不包含的角色ID
	 * @param ids:包含的角色ID
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> treeLoader(String query,String nodeId,String[] ids, String property, String direction) {
		Boolean parentAuthorityIdIsNull=false;
		Boolean isLeaf=null;
		if("root".equals(nodeId)){
			parentAuthorityIdIsNull=true;
			nodeId=null;
		}
		return o_sysAuthorityBO.findBySome(ids, null,query, nodeId, isLeaf, parentAuthorityIdIsNull, property, direction);
	}
	/**
	 * 
	 * treeLoader:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param etype
	 * @param nodeId:不包含的角色ID
	 * @param ids:包含的角色ID
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> treeLoader(String query,String etype,String nodeId,String[] ids, String property, String direction) {
		Boolean parentAuthorityIdIsNull=false;
		Boolean isLeaf=null;
		if("root".equals(nodeId)){
			parentAuthorityIdIsNull=true;
			nodeId=null;
		}
		return o_sysAuthorityBO.findBySome(ids, null,etype,null, query, nodeId, isLeaf, parentAuthorityIdIsNull, property, direction);
	}
	
    /**
     * 
     * treeLoaderAll:
     * 
     * @author 杨鹏
     * @param query
     * @param nodeId
     * @param ids
     * @param property
     * @param direction
     * @return
     * @since  fhd　Ver 1.1
     */
	public List<VSysAuthority> treeLoaderAll(String query,String nodeId,String[] ids,String[] etypes, String property, String direction) {
		Boolean parentAuthorityIdIsNull=false;
		Boolean isLeaf=null;
		if("root".equals(nodeId)){
			parentAuthorityIdIsNull=true;
			nodeId=null;
		}
		return this.findBySome(ids, null,etypes,null, query, nodeId, isLeaf, parentAuthorityIdIsNull, property, direction);
	}

	/**
	 * 
	 * authorityTreeLoader:一次查询加载权限树
	 * 
	 * @author 杨鹏
	 * @param request
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public String authorityTreeLoader(HttpServletRequest request){
		String etype="M";
		List<Map<String, String>> sortList=new ArrayList<Map<String,String>>();
		Map<String, String> sortMap=new HashMap<String, String>();
		sortMap.put("property", "level");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		sortMap=new HashMap<String, String>();
		sortMap.put("property", "sn");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		List<SysAuthority> sysAuthorityList = o_sysAuthorityBO.findByEtype(etype, sortList);//按照层级排序保证把根节点放到前面，方便形成树结构。
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (SysAuthority sysAuthority : sysAuthorityList) {
			String id = sysAuthority.getId();
			String code = sysAuthority.getAuthorityCode();
			if(request.isUserInRole("ROLE_"+code)&&!"ALL".equals(code)){//判断是否是该用户权限
				Map<String,Object> sysAuthorityMap = new HashMap<String, Object>();//权限Map
				SysAuthority parentAuthority = sysAuthority.getParentAuthority();
				String idSeq = sysAuthority.getIdSeq();
				String name = sysAuthority.getAuthorityName();
				String url = sysAuthority.getUrl();
				String icon = sysAuthority.getIcon();
				sysAuthorityMap.put("id", id);
				sysAuthorityMap.put("text", name);
				if(sysAuthority.getLevel() > 2) {
					sysAuthorityMap.put("iconCls", icon);
				}else {
					sysAuthorityMap.put("style", "color:#FFFFFF");
				}
				
				
				if(StringUtils.isNotBlank(url)){
					sysAuthorityMap.put("url1", url);
					sysAuthorityMap.put("handler", "onMenuClick");
				}
				if(null==parentAuthority||"ALL".equals(parentAuthority.getId())){//判断是否是根节点，为空的是新树结构用，all判断是旧树结构用
					list.add(sysAuthorityMap);
				}else{//循环模拟递归寻找父节点位置
					List<Map<String,Object>> items =null;
					String[] parentIds = StringUtils.split(idSeq,".");
					List<Map<String, Object>> listTemp = list;
					for (String parentId : parentIds) {
						if(!"ALL".equals(parentId)&&StringUtils.isNotBlank(parentId)&&!id.equals(parentId)){
							for (Map<String, Object> map : listTemp) {
								if(parentId.equals(map.get("id"))){
									Object menuObj = map.get("menu");
									Map<String,Object> menuMap =null;
									if(menuObj==null){
										menuMap = new HashMap<String, Object>();
										List<Map<String,Object>> itemsTemp = new ArrayList<Map<String,Object>>();
										menuMap.put("items", itemsTemp);
										map.put("menu", menuMap);
									}else{
										menuMap = (Map<String, Object>) menuObj;
									}
									Object itemsObj = menuMap.get("items");
									items = (List<Map<String, Object>>) itemsObj;
								}
							}
							listTemp=items;
						}
					}
					if(null!=items){
						items.add(sysAuthorityMap);
					}
				}
			}
		}
		JSONArray fromObject = JSONArray.fromObject(list);
		String string = fromObject.toString();
		string = string.replace("\"onMenuClick\"", "onMenuClick");//无法转换指向对象onMenuClick，只能转换后替换字符串
		return string;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAuthorityTree(HttpServletRequest request,String parentAuthId){
	//	String parentAuthId = request.getParameter("parentAuthId");
		String etype="M";
		List<Map<String, String>> sortList=new ArrayList<Map<String,String>>();
		Map<String, String> sortMap=new HashMap<String, String>();
		sortMap.put("property", "level");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		sortMap=new HashMap<String, String>();
		sortMap.put("property", "sn");
		sortMap.put("direction", "asc");
		sortList.add(sortMap);
		if("root".equals(parentAuthId)){
			parentAuthId = "ALL";
		}
		List<SysAuthority> sysAuthorityList = o_sysAuthorityBO.findBySome(parentAuthId,etype,sortList);//按照层级排序保证把根节点放到前面，方便形成树结构。
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (SysAuthority sysAuthority : sysAuthorityList) {
			String id = sysAuthority.getId();
			String code = sysAuthority.getAuthorityCode();
			if(request.isUserInRole("ROLE_"+code)&&!"ALL".equals(code)){//判断是否是该用户权限
				Map<String,Object> sysAuthorityMap = new HashMap<String, Object>();//权限Ma
			//	String idSeq = sysAuthority.getIdSeq();
				String name = sysAuthority.getAuthorityName();
				String url = sysAuthority.getUrl();
				String icon = sysAuthority.getIcon();
				String parentId = sysAuthority.getParentAuthority().getId();
				Boolean leaf = sysAuthority.getIsLeaf();
				sysAuthorityMap.put("id", id);
				sysAuthorityMap.put("text", name);
				sysAuthorityMap.put("leaf", leaf);
				sysAuthorityMap.put("iconCls", icon);
				if(StringUtils.isNotBlank(parentId)){
					sysAuthorityMap.put("parentId", parentId);
				}
				if(StringUtils.isNotBlank(url)){
					sysAuthorityMap.put("code", url);
				}
				list.add(sysAuthorityMap);
			}
		}
		return list;
	}
}

