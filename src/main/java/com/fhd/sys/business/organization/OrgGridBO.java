/**
 * OrgGridBO.java
 * com.fhd.sys.business.organization
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-14 		黄晨曦
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.business.organization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.utils.FHDUtils;
import com.fhd.core.dao.Page;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.interfaces.IOrgGridBO;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author   王再冉
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-1-14		下午1:25:04
 *
 * @see 	 
 */
@Service
public class OrgGridBO implements IOrgGridBO{
	
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	
	/**
	 *	查询机构实体对象分页 
	 * @author 
	 * @param orgLevel	 机构层级
	 * @param page	coreDAOPage
	 * @param sort	排序字段
	 * @param dir	排序方式
	 * @param orgStatus	机构状态
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<SysOrganization> findOrgPageBySome(String orgName, Page<SysOrganization> page, String sort, String dir, String orgId) {
		DetachedCriteria dc = DetachedCriteria.forClass(SysOrganization.class);
		dc.setFetchMode("parentOrg", FetchMode.JOIN);
		String companyId =  UserContext.getUser().getCompanyid();// 所在公司id
		if(StringUtils.isNotBlank(companyId)){
			dc.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(orgName)){
			dc.add(Property.forName("orgname").like(orgName,MatchMode.ANYWHERE));	
		}
		if(StringUtils.isNotBlank(orgId)){
			dc.add(Restrictions.eq("parentOrg.id", orgId));
		}
		dc.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		
		if("ASC".equalsIgnoreCase(dir)) {
			dc.addOrder(Order.asc(sort));
		} else {
			dc.addOrder(Order.desc(sort));
		}
		return o_sysOrgDAO.findPage(dc, page, false);
	}
	
	/**
	 * 
	 * 根据机构id查询机构实体
	 * 
	 * @author 
	 * @param planId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public SysOrganization findOrganizationByOrgId(String orgId) {
		Criteria c = o_sysOrgDAO.createCriteria();
		List<SysOrganization> list = null;
		
		if (StringUtils.isNotBlank(orgId)) {
			c.add(Restrictions.eq("id", orgId));
		} else {
			return null;
		}
		
		list = c.list();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	
	/**
	 * 根据id查询机构集合
	 * @author 
	 * @param orgId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findOrganizationByOrgIds(List<String> orgIds) {
		Criteria c = o_sysOrgDAO.createCriteria();
		List<SysOrganization> list = null;
		
		if (orgIds.size()>0) {
			c.add(Restrictions.in("id", orgIds));
		} else {
			return null;
		}
		
		list = c.list();
		if (list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}
	/**
	 * 根据id查询机构及其下级机构的集合
	 * @param orgId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findOrgsAndChildOrgsByOrgIds(String orgId) {
		Criteria c = o_sysOrgDAO.createCriteria();
		c.setCacheable(true);
		List<SysOrganization> list = null;
		
		if (StringUtils.isNotBlank(orgId)) {
			//c.add(Restrictions.eq("id", orgId));
			c.add(Restrictions.or(Restrictions.eq("id", orgId),Restrictions.eq("parentOrg.id", orgId)));
		}
		list = c.list();
		if (list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}
	
	/**
	 * 根据公司id查询机构
	 * @author 
	 * @param companyId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findOrganizationByCompanyIds(String companyId) {
		Criteria c = o_sysOrgDAO.createCriteria();
		c.setCacheable(true);
		List<SysOrganization> list = null;
		if (StringUtils.isNotBlank(companyId)) {
			c.add(Restrictions.eq("company.id", companyId));
		} else {
			return null;
		}
		list = c.list();
		if (list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}
	
	/**
	 * 查询所有机构
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findAllOrganizations() {
		Criteria c = o_sysOrgDAO.createCriteria();
		c.setCacheable(true);
		List<SysOrganization> list = null;
		list = c.list();
		if (list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}
	
	/**
	 * 查询所有机构
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findAllOrganizationsByDeleteStatusFalse() {
		Criteria c = o_sysOrgDAO.createCriteria();
		c.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<SysOrganization> list = null;
		list = c.list();
		if (list.size() > 0) {
			return list;
		} else {
			return null;
		}
	}
	
	/**
	 * 查询公司所有机构的id集合
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> findOrgIdsByCompanyId(String companyId) {
		Set<String> idSet = new HashSet<String>();
		Criteria c = o_sysOrgDAO.createCriteria();
		if (StringUtils.isNotBlank(companyId)) {
			c.add(Restrictions.eq("company.id", companyId));
		} else {
			return null;
		}
		List<SysOrganization> list = c.list();
		for (SysOrganization org : list) {
		    String[] idsTemp = org.getOrgseq().split("\\.");
		    idSet.addAll(Arrays.asList(idsTemp));
		}
		return idSet;
	}
	
	 
	/**
	 * 查询下级机构
	 * @param orgId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findChildOrgByOrgId(String orgId) {
		Criteria c = o_sysOrgDAO.createCriteria();
		c.setCacheable(true);
		if (StringUtils.isNotBlank(orgId)) {
			c.add(Restrictions.eq("company.id", orgId));
		} 
		return c.list();
				
	}
	
	/**
	 * 根据parentId查询子机构
	 * add by 王再冉
	 * 2013-12-30  上午11:24:04
	 * desc : 
	 * @param orgId
	 * @return 
	 * List<SysOrganization>
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findChildOrgByparentId(String orgId) {
		Criteria c = o_sysOrgDAO.createCriteria();
		//c.setCacheable(true);
		if (StringUtils.isNotBlank(orgId)) {
			c.add(Restrictions.eq("parentOrg.id", orgId));
		} 
		return c.list();
				
	}
		
	
	/**
	 * 根据机构名称查询机构实体
	 * @author 
	 * @param orgName 机构名称
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public SysOrganization findOrganizationByorgName(String orgName) {
		Criteria c = o_sysOrgDAO.createCriteria();
		List<SysOrganization> list = null;
		
		if (StringUtils.isNotBlank(orgName)) {
			c.add(Restrictions.eq("orgname", orgName));
		}
		
		list = c.list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 删除机构
	 * @author 
	 * @param ids
	 * @since  fhd　Ver 1.1
	 */
	 @Transactional
	 public void removeOrganizationByIds(List<String> ids) {
		//String[] idArray = ids.split(",");
		for (String id : ids) {
			SysOrganization orgEntry = findOrganizationByOrgId(id);
			if(orgEntry.getParentOrg().getChildrenOrg().size()<=1){//如果父机构不存在子集，设置叶子节点为true
				orgEntry.getParentOrg().setIsLeaf(true);
				mergeOrganization(orgEntry.getParentOrg());
			}
			removeOrganizationBySome(orgEntry, orgEntry.getChildrenOrg());
		}
	}	
	 
	 /**
	  * 从数据库删除选中机构及下级机构
	  * @author 
	  * @param orgEntry
	  * @param orgChildeSnet
	  * @since  fhd　Ver 1.1
	  */
	 public void removeOrganizationBySome(SysOrganization orgEntry, Set<SysOrganization> orgChildeSnet) {
		if (orgChildeSnet != null) {
		    for (SysOrganization children: orgChildeSnet) {
				if (children.getChildrenOrg() != null
					&& children.getChildrenOrg().size() > 0) {
					removeOrganizationBySome(children, children.getChildrenOrg());
				} else {
					removeOrganizationBySome(children, null);
				}
		    }
		}
		if (orgEntry != null) {
			orgEntry.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
			o_sysOrgDAO.merge(orgEntry);
		}
	}
	
	/**
	 * 保存机构实体对象
	 * @author 
	 * @param org
	 * @since  fhd　Ver 1.1
	 */
	 @Transactional
	public void saveOrganization(SysOrganization org) {
		o_sysOrgDAO.merge(org);
	}
	
	 /**
	  * 更新机构对象
	  * 
	  * @author 
	  * @param org
	  * @since  fhd　Ver 1.1
	  */
	@Transactional
	public void mergeOrganization(SysOrganization org) {
		o_sysOrgDAO.merge(org);
	}
	/**
	 * 
	 * getOrgcode:获取机构编号
	 * 
	 * @author 杨鹏
	 * @param parentOrgId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public String getOrgcode(String parentOrgId){
		String orgcode=null;
		if(StringUtils.isNotBlank(parentOrgId)){
			SysOrganization parentOrg = this.findOrganizationByOrgId(parentOrgId);
			String parentOrgcode = parentOrg.getOrgcode();
			if(StringUtils.isNotBlank(parentOrgcode)){
				String orgcodePrefix=parentOrgcode+"_";
				Integer orgcodeSuffix=1;
				Criteria createCriteria = o_sysOrgDAO.createCriteria();
				createCriteria.add(Restrictions.like("orgcode", orgcodePrefix, MatchMode.START));
				createCriteria.add(Restrictions.eq("parentOrg.id", parentOrgId));
				createCriteria.addOrder(Order.desc("orgcode"));
				List<SysOrganization> list = createCriteria.list();
				for (SysOrganization sysOrganization : list) {
					String orgcodeTemp = sysOrganization.getOrgcode();
					String orgcodeSuffixTemp = orgcodeTemp.substring(orgcodePrefix.length());
					if(FHDUtils.isInteger(orgcodeSuffixTemp)){
						Integer orgcodeSuffixTempNum = Integer.valueOf(orgcodeSuffixTemp);
						orgcodeSuffix=orgcodeSuffixTempNum+1;
						break;
					}
				}
				DecimalFormat decimalFormat = new DecimalFormat("000");
				String orgcodeNumStr=decimalFormat.format(orgcodeSuffix);
				orgcode=parentOrgcode+"_"+orgcodeNumStr;
			}
		}
		if(StringUtils.isBlank(orgcode)){
			orgcode="001";
		}
		return orgcode;
	}
	/**
	 * <pre>
	 * 根据公司ID获得其子公司列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param companyId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findSubCompanyList(String companyId){
		Criteria criteria = o_sysOrgDAO.createCriteria();
		criteria.add(Restrictions.eq("parentOrg.id", UserContext.getUser().getCompanyid()));
		criteria.add(Restrictions.in("orgType", new String[]{"0orgtype_c","0orgtype_sc"}));
		return criteria.list();
	}
	
	/**
	 * 查询集团ID
	 * */
	public SysOrganization findOrgByParentIdIsNUll(){
		Criteria criteria = o_sysOrgDAO.createCriteria();
		criteria.add(Restrictions.isNull("parentOrg.id"));
		return (SysOrganization) criteria.list().get(0);
	}
	/**
	 * 查询所有机构的id（编号，组织导入时验证）
	 * add by 王再冉
	 * 2014-4-22  下午1:44:05
	 * desc : 
	 * @return 
	 * List<String>
	 */
	@SuppressWarnings("unchecked")
	public List<String> findAllOrgIds() {
		List<String> idSet = new ArrayList<String>();
		Criteria c = o_sysOrgDAO.createCriteria();
		List<SysOrganization> list = c.list();
		for (SysOrganization org : list) {
		    idSet.add(org.getId());
		}
		return idSet;
	}
	/**
	 * 批量保存机构信息（部分字段，导入用方法）
	 * add by 王再冉
	 * 2014-4-23  下午4:43:47
	 * desc : 
	 * @param orgList 
	 * void
	 */
	@Transactional
    public void saveOrgsSome(final List<SysOrganization> orgList) {
        this.o_sysOrgDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String isLeaf = "";
                String sql = " insert into T_SYS_ORGANIZATION " +
                		 " (ID,PARENT_ID,ORG_CODE,ORG_NAME,ORG_LEVEL,ESORT,ID_SEQ,IS_LEAF,COMPANY_ID,NAME_SEQ," +
                		 "ORG_TYPE,DELETE_STATUS,ESTATUS) " + 
                		 " values(?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                
                pst = connection.prepareStatement(sql);
                
                for (SysOrganization org : orgList) {
                	if(org.getIsLeaf()){
                		isLeaf = "1";
                	}else{
                		isLeaf = "0";
                	}
					pst.setString(1, org.getId());
					if(null == org.getParentOrg()){
						pst.setString(2, null);
					}else{
						pst.setString(2, org.getParentOrg().getId());
					}
					pst.setString(3, org.getOrgcode());
					pst.setString(4, org.getOrgname());
					pst.setInt(5, org.getOrgLevel());
					pst.setInt(6, org.getSn());
					pst.setString(7, org.getOrgseq());
					pst.setString(8, isLeaf);
					pst.setString(9, org.getCompany().getId());
					pst.setString(10, org.getNameSeq());
					pst.setString(11, org.getOrgType());
					pst.setString(12, org.getOrgStatus());
					pst.setString(13, org.getDeleteStatus());
					pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
}