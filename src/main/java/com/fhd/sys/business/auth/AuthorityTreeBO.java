/**
 * SysAuthorityBO.java
 * com.fhd.fdc.commons.business.sys.auth
 *
 *
 * ──────────────────────────────────
 *   		 2010-8-30 		吴德福
 *
 * Copyright (c) 2010, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.business.auth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.sys.auth.SysAuthorityDAOold;
import com.fhd.entity.sys.auth.SysAuthority;

/**
 * 功能权限业务类
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-8-31		上午10:45:27
 *
 * @see 	 
 */
@Service
public class AuthorityTreeBO {

	@Autowired
	private SysAuthorityDAOold o_sysAuthorityDAOold;
	
	/**
	 * <pre>
	 * getFirstAuthority:查找一级权限
	 * </pre>
	 * 
	 * @author 张 雷
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("deprecation")
	public List<SysAuthority> findFirstAuthority() {
		SysAuthority root = (SysAuthority) o_sysAuthorityDAOold.createCriteria(Restrictions.isNull("parentAuthority")).uniqueResult();

		return o_sysAuthorityDAOold.findBy("parentAuthority.id", root.getId(),"sn",true);
	}
	
	/**
	 * <pre>
	 * 加载一级菜单(app.js用)
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param request
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public String findFirstAuthority(HttpServletRequest request){
		StringBuffer sb = new StringBuffer();
		List<SysAuthority> entityList = this.findFirstAuthority();
		for (SysAuthority entity : entityList) {
			if(!request.isUserInRole("ROLE_"+entity.getAuthorityCode())|| !"M".equals(entity.getEtype())){//没有权限或者类型不是菜单
				continue;
    		}
			sb.append("{id:'").append(entity.getId()).append("',");
			sb.append("text:'").append(entity.getAuthorityName()).append("',");
			sb.append("iconCls:'").append(null!=entity.getIcon()?entity.getIcon():"").append("'},");
		}
		return sb.substring(0, sb.length()>0?sb.length()-1:0); 
	}
	
	/**
	 * <pre>
	 * 根据父级ID获得其子集中有权限的菜单(app.js用)
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param parentId
	 * @param request
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("deprecation")
	public String authorityTreeLoaderByParentId(String parentId,HttpServletRequest request){
		SysAuthority parent = o_sysAuthorityDAOold.get(parentId);
		return findChildrenByParent(new ArrayList<SysAuthority>(parent.getChildren()),parent,request);
	}
	
	/**
	 * <pre>
	 * 根据父级获得有权限的子集菜单
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param list 父级的子集
	 * @param parent 父级
	 * @param request
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	private String findChildrenByParent(List<SysAuthority> list,SysAuthority parent,HttpServletRequest request) {
		StringBuilder result = new StringBuilder();
        List<SysAuthority> childrenList = new ArrayList<SysAuthority>();
        if(null != list && list.size()>0){
        	for(SysAuthority entity : list){
        		if(!request.isUserInRole("ROLE_"+entity.getAuthorityCode()) || !"M".equals(entity.getEtype())){//没有权限或者类型不是菜单
        			continue;
        		}
            	if(null == parent){
            		if(null == entity.getParentAuthority()){
            			childrenList.add(entity);
            		}
            	}else{
            		if(null != entity.getParentAuthority() && StringUtils.isNotBlank(entity.getParentAuthority().getId()) 
            				&& entity.getParentAuthority().getId().equals(parent.getId())){
                		childrenList.add(entity);
                	}
            	}
            }
        }
        Iterator<SysAuthority> iterator = childrenList.iterator();
        if (childrenList.size() > 0 ){//不是叶子节点
        	if(null != parent){
        		result.append("{")
        			.append("id:'"+parent.getId()+"',")
                    .append("text:'"+parent.getAuthorityName()+"',")
                    .append("expanded:true,")
                    .append("url:'"+parent.getUrl()+"',")
                    .append("leaf: false,");
        		if(StringUtils.isNotBlank(parent.getIcon())){
            		result.append("iconCls:").append("'"+parent.getIcon()+"',");
            	}
        		result.append("children:");
        	}else{
        		result.append("{").append("children:");
        	}
            for(int i=0;i<childrenList.size();i++) {
            	SysAuthority temp = (SysAuthority)iterator.next();
                if(childrenList.size() == 1) {
                	result.append("["+ findChildrenByParent(new ArrayList<SysAuthority>(temp.getChildren()),temp,request) +"]");
                }else {
                    if (i == childrenList.size() -1) {//last row
                    	result.append(findChildrenByParent(new ArrayList<SysAuthority>(temp.getChildren()),temp,request) +"]");
                    } else if( i == 0 ){//first row
                    	result.append("["+ findChildrenByParent(new ArrayList<SysAuthority>(temp.getChildren()),temp,request) +",");
                    } else {//middle row
                    	result.append(findChildrenByParent(new ArrayList<SysAuthority>(temp.getChildren()),temp,request) +",");
                    }
                }
            }
            result.append("}"); 
        }else {//是叶子节点
        	if(null != parent){
        		if(parent.getIsLeaf()){
        			result.append("{")
    	            .append("id:'"+parent.getId()+"',")
    	            .append("text:'"+parent.getAuthorityName()+"',")
    	            .append("url:'"+parent.getUrl()+"',");
        			if(StringUtils.isNotBlank(parent.getIcon())){
                		result.append("iconCls:").append("'"+parent.getIcon()+"',");
                	}
            		result.append("leaf: true").append("}"); 
        		}
        	}
	    }
	    return result.toString();
    }
}

