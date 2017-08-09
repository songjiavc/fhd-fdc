package com.fhd.sys.business.auth;

import java.io.Serializable;
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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.dao.utils.PropertyFilter;
import com.fhd.dao.sys.auth.AuthorityDAO;
import com.fhd.dao.sys.auth.SysAuthorityDAOold;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.fdc.commons.orm.sql.SqlBuilder;
import com.fhd.sys.business.log.BusinessLogBO;
import com.fhd.sys.web.form.auth.SysAuthorityForm;

/**
 * 功能权限BO类.
 * @author  wudefu
 * @version V1.0  创建时间：2010-8-30
 * Company FirstHuiDa.
 */
@Service
@SuppressWarnings({"unchecked","deprecation"})
public class AuthorityBO {

	@Autowired
	private AuthorityDAO o_authorityDAO;
	@Autowired
	private SysAuthorityDAOold o_sysAuthorityDAOold;
	@Autowired
	private BusinessLogBO o_businessLogBO;
	
	/**
	 * 
	 * findById:根据ID查询
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public SysAuthority findById(String id){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		createCriteria.add(Restrictions.idEq(id));
		return (SysAuthority)createCriteria.uniqueResult();
	}
	
	/**
	 * 
	 * findByAll:
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findByAll(){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		return createCriteria.list();
	}
	/**
	 * 
	 * findByName:
	 * 
	 * @author 杨鹏
	 * @param authorityName
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findByName(String authorityName){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		createCriteria.add(Restrictions.eq("authorityName", authorityName));
		return createCriteria.list();
	}
	/**
	 * 
	 * findByCodes:
	 * 
	 * @author 杨鹏
	 * @param authorityCodes
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findByCodes(String...authorityCodes){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		createCriteria.add(Restrictions.in("authorityCode", authorityCodes));
		return createCriteria.list();
	}
	/**
	 * 
	 * findByEtype:
	 * 
	 * @author 杨鹏
	 * @param etype
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findByEtype(String etype, List<Map<String, String>> sortList){
		Criteria criteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(etype)){
			criteria.add(Restrictions.eq("etype", etype));
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
	 * findBySome:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @param etype:类型
	 * @param parentId:父节点Id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findBySome(String id,String etype,String parentId){
		Criteria criteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(id)){
			criteria.add(Restrictions.idEq(id));
		}
		if(StringUtils.isNotBlank(etype)){
			criteria.add(Restrictions.eq("etype", etype));
		}
		if(StringUtils.isNotBlank(parentId)){
			criteria.add(Restrictions.eq("parentAuthority.id", parentId));
		}
		return criteria.list();
	}
	
	/**
	 * 
	 * findBySome:验证code
	 * 
	 * @author 杨鹏
	 * @param noId 不包含的ID
	 * @param authorityCode 编号
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findBySome(String noId,String authorityCode){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(noId)){
			createCriteria.add(Restrictions.not(Restrictions.idEq(noId)));
		}
		if(StringUtils.isNotBlank(authorityCode)){
			createCriteria.add(Restrictions.eq("authorityCode", authorityCode));
		}
		return createCriteria.list();
	}
	
	/**
	 * add by  宋佳
	 */
	public List<SysAuthority> findBySome(String parentAuthorityId,String etype,List<Map<String, String>> sortList){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(parentAuthorityId)){
			createCriteria.add(Restrictions.eq("parentAuthority.id", parentAuthorityId));
		}else{
			createCriteria.add(Restrictions.eq("parentAuthority.id", "ALL"));
		}
		if(StringUtils.isNotBlank(etype)){
			createCriteria.add(Restrictions.eq("etype", etype));
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						createCriteria.addOrder(Order.desc(property));
					}else{
						createCriteria.addOrder(Order.asc(property));
					}
				}
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
	 * @param authorityName
	 * @param parentAuthorityId
	 * @param parentAuthorityIdIsNull
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findBySome(String[] ids,String[] noIds,String authorityName,String parentAuthorityId,Boolean isLeaf,Boolean parentAuthorityIdIsNull, String property, String direction){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(authorityName)){
			createCriteria.add(Restrictions.like("authorityName", authorityName,MatchMode.ANYWHERE));
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
	 * @param parentAuthorityId
	 * @param isLeaf
	 * @param parentAuthorityIdIsNull
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> findBySome(String[] ids,String[] noIds,String[] etypes,String authorityName,String parentAuthorityId,Boolean isLeaf,Boolean parentAuthorityIdIsNull, String property, String direction){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(authorityName)){
			createCriteria.add(Restrictions.like("authorityName", authorityName,MatchMode.ANYWHERE));
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
				createCriteria.add(Restrictions.isNull("etype"));
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
	 * findBySome:条件查询
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @param noIds不包含的ID
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
	public List<SysAuthority> findBySome(String[] ids,String[] noIds,String authorityName,String authorityNameSeq,String parentAuthorityId,Boolean isLeaf,Boolean parentAuthorityIdIsNull, String property, String direction){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(authorityName)){
			createCriteria.add(Restrictions.like("authorityName", authorityName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(authorityNameSeq)){
			Criteria createCriteriaId = o_authorityDAO.createCriteria();
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
	
	public List<SysAuthority> findBySome(String[] ids,String[] noIds,String etype,String authorityName,String authorityNameSeq,String parentAuthorityId,Boolean isLeaf,Boolean parentAuthorityIdIsNull, String property, String direction){
		Criteria createCriteria = o_authorityDAO.createCriteria();
		if(StringUtils.isNotBlank(authorityName)){
			createCriteria.add(Restrictions.like("authorityName", authorityName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(authorityNameSeq)){
			Criteria createCriteriaId = o_authorityDAO.createCriteria();
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
		if(StringUtils.isNotBlank(etype)){
			createCriteria.add(Restrictions.eq("etype", etype));
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
	 * findPageBySome:分页查询
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param parentId
	 * @param etype
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<SysAuthority> findPageBySome(Page<SysAuthority> page,String query,String parentId,String etype,List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(SysAuthority.class);
		criteria.createAlias("parentAuthority", "parentAuthority");
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("authorityName", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		if(StringUtils.isNotBlank(parentId)){
			criteria.add(Restrictions.eq("parentAuthority.id", parentId));
		}
		if(StringUtils.isNotBlank(etype)){
			criteria.add(Restrictions.in("etype", etype.split(",")));
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
		
		return o_authorityDAO.findPage(criteria, page, false);
	}
	
	/**
	 * 
	 * merge:
	 * 
	 * @author 杨鹏
	 * @param sysAuthority
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void merge(SysAuthority sysAuthority){
		SysAuthority parentAuthority = sysAuthority.getParentAuthority();
		String id = sysAuthority.getId();
		String etype = sysAuthority.getEtype();
		Integer level=1;
		String idSeq="."+id+".";
		Boolean isLeaf=false;
		if(parentAuthority!=null){
			parentAuthority = this.findById(parentAuthority.getId());
			if(parentAuthority.getIsLeaf()&&"M".equals(etype)){
				parentAuthority.setIsLeaf(false);
				o_authorityDAO.merge(parentAuthority);
			}
			idSeq=parentAuthority.getIdSeq()+id+".";
			level=parentAuthority.getLevel()+1;
		}
		sysAuthority.setLevel(level);
		sysAuthority.setIdSeq(idSeq);
		List<SysAuthority> children = this.findBySome(null,"M", id);
		if(children.size()==0){
			isLeaf=true;
		}
		sysAuthority.setIsLeaf(isLeaf);
		o_authorityDAO.merge(sysAuthority);
	}
	/**
	 * 
	 * merge:批量保存
	 * 
	 * @author 杨鹏
	 * @param sysAuthoritys
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void merge(SysAuthority[] sysAuthoritys){
		for (SysAuthority sysAuthority : sysAuthoritys) {
			this.merge(sysAuthority);
		}
	}
	
	/**
	 * 
	 * removeById:根据ID删除权限
	 * 
	 * @author 杨鹏
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeById(String id) {
		o_authorityDAO.delete(id);
	}
	/**
	 * 
	 * removeByIds:根据ID删除权限
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeByIds(String...ids) {
		for (String id : ids) {
			o_authorityDAO.delete(id);
		}
	}
/*********************************************新旧代码分割线**************/	
	
	/**
	 * 取得根结点.
	 * @author 吴德福
	 * @return SysAuthority
	 * @since  fhd　Ver 1.1
	 */
	public SysAuthority getRootOrg() {
		SysAuthority sysAuthority = new SysAuthority();
		DetachedCriteria dc = DetachedCriteria.forClass(SysAuthority.class);
		dc.add(Restrictions.isNull("parentAuthority"));
		dc.add(Restrictions.eq("level", 1));
		List<SysAuthority> sysAuthorityList = o_sysAuthorityDAOold.findByCriteria(dc);
		if(null != sysAuthorityList && sysAuthorityList.size()>0){
			sysAuthority = sysAuthorityList.get(0);
		}
		return sysAuthority;
	}
	/**
	 * 构造根结点的全部树结点：所有子权限的全部结点.
	 * @author 吴德福
	 * @param id 机构id.
	 * @param contextPath 发布应用路径.
	 * @return List<Map<String, Object>> 根结点的树结点集合.
	 * @since  fhd　Ver 1.1
	 */
	public List<Map<String, Object>> loadAuthorityTree(Serializable id,
			String contextPath) {
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		SysAuthority sysAuthority = o_sysAuthorityDAOold.get(id);
		Set<SysAuthority> subAuthoritys = sysAuthority.getChildren();
		for (SysAuthority subAuthority : subAuthoritys) {
			Map<String, Object> node = new HashMap<String, Object>();
			//node.put("id", subAuthority.getId() + RandomUtils.nextInt(9999));
			node.put("id", subAuthority.getId());
			node.put("name", subAuthority.getId());
			node.put("text", subAuthority.getAuthorityName());
			node.put("leaf", subAuthority.getIsLeaf());
			node.put("href", contextPath + "/sys/auth/authority/tabs.do?id=" + subAuthority.getId());
			node.put("hrefTarget", "mainframe");
			String iconCls = null;
			if(subAuthority.getIsLeaf()==true)
			{   
				iconCls="icon-menu";
			}else{
			    iconCls="icon-tree-folder";
			}
			node.put("iconCls", iconCls);
			node.put("cls", "authority");
			node.put("draggable", true);
			nodes.add(node);
		}

		return nodes;
	}
	/**
	 * 构造根结点的静态树json数据.
	 * @author 吴德福
	 * @param id 权限id.
	 * @param roleid 角色id.
	 * @return String json数据
	 * @since  fhd　Ver 1.1
	 */
	public String loadStaticAuthorityTree(Serializable id) {
		Criteria criteria = o_sysAuthorityDAOold.createCriteria();
		criteria.setFetchMode("parentAuthority", FetchMode.SELECT);
		criteria.setFetchMode("subAuthoritys", FetchMode.SELECT);
		criteria.setFetchMode("sysRoles", FetchMode.SELECT);
		criteria.setFetchMode("sysUsers", FetchMode.SELECT);
		criteria.setFetchMode("sysPosition", FetchMode.SELECT);
		//criteria.setFetchMode("sysGroup", FetchMode.SELECT);
		criteria.add(Restrictions.not(Restrictions.eq("id", id)));
		criteria.addOrder(Order.asc("idSeq"));
		List<SysAuthority> list = criteria.list();
		SysAuthority sysAuthority = o_sysAuthorityDAOold.get(id);
		String res = this.getChildTest(new HashSet<SysAuthority>(list), sysAuthority);
		if(res.length()>0){
			return res.substring(0, res.length()-1);
		}else{
			return res;
		}
	}
	/**
	 * 构造根结点的静态树json数据.
	 * @author 吴德福
	 * @param list
	 * @param parentSysAuthority
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	private String getChildTest(Set<SysAuthority> set,SysAuthority parentSysAuthority) {
        String result = "";
        List<SysAuthority> childrenList = (List<SysAuthority>) o_sysAuthorityDAOold.createCriteria()
        		.add(Restrictions.eq("parentAuthority", parentSysAuthority)).list();
        Iterator<SysAuthority> iterator = childrenList.iterator();
        if (childrenList.size() > 0 ){//不是叶子节点
        	if(null != parentSysAuthority.getParentAuthority()){
        		result="{"
                    +"id:'"+parentSysAuthority.getId()+"',"
                    +"text:'"+parentSysAuthority.getAuthorityName()+"',"
                    +"expanded:true,"
                    +"checked:true,"
                    +"children:";
        	}
            for (int i=0;i<childrenList.size();i++) {
            	SysAuthority sysAuthority = (SysAuthority)iterator.next();
                if (childrenList.size() == 1) {
                    result += "["+ getChildTest(sysAuthority.getChildren(),sysAuthority) +"]";
                } else {
                    if (i == childrenList.size() -1) {//last row
                        result +=  getChildTest(sysAuthority.getChildren(),sysAuthority) +"]";
                    } else if( i == 0 ){//first row
                        result += "["+ getChildTest(sysAuthority.getChildren(),sysAuthority) +"," ;
                    } else {//middle row
                        result +=  getChildTest(sysAuthority.getChildren(),sysAuthority) +"," ;
                    }
                }
                
                    
            }
            result=result+"}"; 
        } else {//是叶子节点
             result="{"
                +"id:'"+parentSysAuthority.getId()+"',"
                +"text:'"+parentSysAuthority.getAuthorityName()+"',"
                +"checked:true,"
                +"leaf: true"
                +"}"; 
        }    
        return result;
    }
	/**
	 * 查询选择权限的所有的下级权限.
	 * @author 吴德福
	 * @param id 选择的权限id.
	 * @return List<SysAuthority> 权限集合.
	 * @since fhd　Ver 1.1
	 */
	public List<SysAuthority> query(String id, List<PropertyFilter> filters) {
		return o_sysAuthorityDAOold.find(filters, "sn", true, Restrictions.like("idSeq", "." + id
						+ ".", MatchMode.ANYWHERE), Restrictions
						.not(Restrictions.eq("id", id)));
	}
	/**
	 * 查询所有的权限.SelectTag控件中┠展示.
	 * @author 吴德福
	 * @return List<SysModule> 模块集合.
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> queryAllAuthorityBySqlServer(){
		Map<String,String> model = new HashMap<String,String>();
		String sql = SqlBuilder.getSql("select_authorityName", model);
		return o_sysAuthorityDAOold.find(sql);
	}
	/**
	 * 新增功能权限.
	 * @author 吴德福
	 * @param sysAuthority
	 * @since  fhd　Ver 1.1
	 */
	@Transactional	
	public Object saveAuthority(SysAuthority sysAuthority){
		try {
			SysAuthority parentAuthority = queryAuthorityById(sysAuthority.getParentAuthority().getId());
			parentAuthority.setIsLeaf(false);
			updateAuthority(parentAuthority);
			sysAuthority.setParentAuthority(parentAuthority);
			sysAuthority.setLevel(parentAuthority.getLevel() + 1);
			sysAuthority.setIdSeq(parentAuthority.getIdSeq() + sysAuthority.getId() + ".");
			o_businessLogBO.saveBusinessLogInterface("新增", "功能权限", "成功", sysAuthority.getAuthorityCode(),sysAuthority.getAuthorityName(),String.valueOf(sysAuthority.getSn()));
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.saveBusinessLogInterface("新增", "功能权限", "失败", sysAuthority.getAuthorityCode(),sysAuthority.getAuthorityName(),String.valueOf(sysAuthority.getSn()));
		}
		return updateAuthority(sysAuthority);
	}
	/**
	 * 修改功能权限.
	 * @author 吴德福
	 * @param sysAuthority
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public Object updateAuthority(SysAuthority sysAuthority){
		try {
			o_businessLogBO.modBusinessLogInterface("修改", "功能权限", "成功", sysAuthority.getId());
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.modBusinessLogInterface("修改", "功能权限", "失败", sysAuthority.getId());
		}
		return o_sysAuthorityDAOold.merge(sysAuthority);
	}
	/**
	 * 删除功能权限.
	 * @author 吴德福
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeAuthority(String id){
		try {
			o_sysAuthorityDAOold.removeById(id);
			o_businessLogBO.delBusinessLogInterface("删除", "功能权限", "成功", id);
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.delBusinessLogInterface("删除", "功能权限", "失败", id);
		}
	}
	/**
	 * 查询所有的权限.
	 * @author 吴德福
	 * @return List<SysAuthority>
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> queryAllAuthority(){
		return o_sysAuthorityDAOold.getAll();
	}
	/**
	 * 根据id查询功能权限
	 * @author 吴德福
	 * @param id
	 * @return SysAuthority 功能权限.
	 * @since  fhd　Ver 1.1
	 */
	public SysAuthority queryAuthorityById(String id){
		return o_sysAuthorityDAOold.get(id);
	}
	/**
	 * 查询作用于菜单的所有功能权限.
	 * @author 吴德福
	 * @return List<SysAuthority>
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> queryAllAuthorityByAuthority(){
		Criteria criteria = o_sysAuthorityDAOold.createCriteria();
		criteria.setFetchMode("parentAuthority", FetchMode.SELECT);
		criteria.setFetchMode("subAuthoritys", FetchMode.SELECT);
		criteria.setFetchMode("sysRoles", FetchMode.SELECT);
		criteria.setFetchMode("sysUsers", FetchMode.SELECT);
		criteria.setFetchMode("sysPosition", FetchMode.SELECT);
		criteria.add(Restrictions.eq("isAuthority", true));
		return criteria.list();
	}
	// 不知是否有用
/*	*//**
	 * 根据查询条件查询所有的作用于菜单的权限.
	 * @author 吴德福
	 * @param page
	 * @param authorityCode
	 * @param authorityName
	 * @return Page<SysAuthority>
	 * @since  fhd　Ver 1.1
	 *//*
	public Page<SysAuthority> queryAuthorityList(Page<SysAuthority> page, String authorityCode,String authorityName){
		DetachedCriteria dc = DetachedCriteria.forClass(SysAuthority.class);
		
		dc.add(Restrictions.eq("isAuthority", true));
		if(StringUtils.isNotBlank(authorityCode)){
			dc.add(Restrictions.like("authorityCode", authorityCode, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(authorityName)){
			dc.add(Restrictions.like("authorityName", authorityName, MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.asc("authorityCode"));
		return o_sysAuthorityDAOold.pagedQuery(dc, page);
	}*/
	/**
	 * 根据查询条件查询模块.
	 * @author 吴德福
	 * @param filters 页面查询条件传递的参数集合.
	 * @return List<SysAuthority> 功能权限集合.
	 * @since  fhd　Ver 1.1
	 */
	public List<SysAuthority> query(SysAuthorityForm sysAuthorityForm) {
		StringBuilder hql = new StringBuilder();
		hql.append("From SysAuthority Where 1=1 ");
		String authorityCode = "";
		String authorityName = "";
		String parentId = "";
		if(sysAuthorityForm != null && !"".equals(sysAuthorityForm.getAuthorityCode()) && sysAuthorityForm.getAuthorityCode() != null){
			authorityCode = sysAuthorityForm.getAuthorityCode();
			hql.append(" and authorityCode like '%"+authorityCode+"%'");
		}
		if(sysAuthorityForm != null && !"".equals(sysAuthorityForm.getAuthorityName()) && sysAuthorityForm.getAuthorityName() != null){
			authorityName = sysAuthorityForm.getAuthorityName();
			hql.append(" and authorityName like '%"+authorityName+"%'");
		}
		if(sysAuthorityForm != null && !"".equals(sysAuthorityForm.getParentId()) && sysAuthorityForm.getParentId() != null){
			parentId = sysAuthorityForm.getParentId();
			hql.append(" and idSeq like '%."+parentId+".%'");
		}
		return o_sysAuthorityDAOold.find(hql.toString());
	}
	/**
	 * Ext拖拽移动node是否成功.
	 * @author 吴德福
	 * @param currentId 当前拖拽的结点id.
	 * @param pid 当前拖拽的父结点id.
	 * @param targetId 当前拖拽的结点的目标结点id.
	 * @return Boolean 操作是否成功.
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public Boolean moveNode(String currentId,String pid,String targetId){
		try {
			SysAuthority sysAuthority = queryAuthorityById(currentId);
			sysAuthority.setParentAuthority(queryAuthorityById(targetId));
			o_sysAuthorityDAOold.merge(sysAuthority);
			o_businessLogBO.modBusinessLogInterface("修改", "权限", "成功", currentId, targetId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.modBusinessLogInterface("修改", "权限", "失败", currentId, targetId);
			return false;
		}
	}
	//知是否有用
/*	*//**
	 * 获取Authority 分页列表
	 * @author 万业
	 * @param page
	 * @param authForm
	 * @return
	 *//*
	public Page<SysAuthority> queryAuthPage(Page<SysAuthority> page,String authorityCode, String authorityName, String parentId)
	{
		DetachedCriteria dc = DetachedCriteria.forClass(SysAuthority.class,"auth");
		if(StringUtils.isNotBlank(authorityCode)){
			dc.add(Property.forName("authorityCode").like(authorityCode, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(authorityName)){
			dc.add(Property.forName("authorityName").like(authorityName, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(parentId)){
			dc.add(Property.forName("auth.parentAuthority.id").eq(parentId));
		}
		
		return o_sysAuthorityDAOold.pagedQuery(dc, page);
		
	}*/
	/**
	 * 把seqid转成auth名字
	 * @author 万业
	 * @param ids
	 * @return
	 */
	public String getGuidName(String[] ids){
		StringBuffer guidNames=new StringBuffer("");
		String result="";
		if(ids.length!=0){
			for (int i = 0; i < ids.length-1; i++) {
				if(StringUtils.isNotBlank(ids[i])){
					guidNames.append(this.o_sysAuthorityDAOold.get(ids[i]).getAuthorityName());
					guidNames.append(">>");
				}
			}
			result=guidNames.substring(0, guidNames.length()-2);
		}
		return result;
	}
	
	/**
	 * 
	 * 根据用户ID 查询 用户权限
	 * 
	 * @author vincent
	 * @param userId
	 * @return
	 */
	public List<SysAuthority> findUserAuthsByUserId(String userId){
		Criteria criteria = o_authorityDAO.createCriteria();
		criteria.createAlias("sysUsers", "u");
		criteria.add(Restrictions.eq("u.id", userId));
		return criteria.list();
	}
	
	/**
	 * 
	 * 根据用户ID 查询 角色权限
	 * 
	 * @author vincent
	 * @param userId
	 * @return
	 */
	public List<SysAuthority> findRoleAuthsByUserId(String userId){
		Criteria criteria = o_authorityDAO.createCriteria();
		criteria.createAlias("sysRoles", "r");
		criteria.createAlias("r.sysUsers", "u");
		
		criteria.add(Restrictions.eq("u.id", userId));
		return criteria.list();
	}

	/**
	 * 根据角色ID查询 角色权限.
	 * @author 吴德福
	 * @param roleId 角色id
	 * @param etype 权限类型:'B--按钮';'F--字段';
	 * @return List<SysAuthority>
	 */
	public List<SysAuthority> findRoleAuthsByRoleId(String roleId, String etype){
		Criteria criteria = o_authorityDAO.createCriteria();
		criteria.createAlias("sysRoles", "r");
		if(StringUtils.isNotBlank(etype)){
			criteria.add(Restrictions.eq("etype",etype));
		}
		if(StringUtils.isNotBlank(roleId)){
			criteria.add(Restrictions.eq("r.id", roleId));
		}
		return criteria.list();
	}
}