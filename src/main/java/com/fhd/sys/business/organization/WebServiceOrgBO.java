package com.fhd.sys.business.organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.sys.business.orgstructure.OrganizationBO;
import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.OrganizationType;
import com.fhd.sys.webservice.dto.WSOrganization;


@Service
public class WebServiceOrgBO{
	@Autowired
	private OrganizationBO o_organizationBO;
	/**
	 * 
	 * validate:验证
	 * 
	 * @author 杨鹏
	 * @param wSOrganizationList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Integer validate(List<WSOrganization> wSOrganizationList){
		Integer flag=10001;
		try {
			List<SysOrganization> organizationList = o_organizationBO.findByAll();
			Map<String,SysOrganization> organizationMap = new HashMap<String, SysOrganization>();
			for (SysOrganization organization : organizationList) {
				organizationMap.put(organization.getOrgcode(), organization);
			}
			for (WSOrganization wSOrganization : wSOrganizationList) {
				String code = wSOrganization.getCode();
				String name = wSOrganization.getName();
				String parentCode = wSOrganization.getParentCode();
				String type = wSOrganization.getType();
				String editType = wSOrganization.getEditType();
				if(StringUtils.isNotBlank(code)){
					organizationMap.put(code, new SysOrganization());
				}
				if (!EditType.save.equals(editType)&&!EditType.update.equals(editType)&&!EditType.remove.equals(editType)) {
					flag = 11001;
					break;
				} else if (StringUtils.isBlank(code)) {
					flag = 12001;
					break;
				} else if (!EditType.save.equals(editType)&& organizationMap.get(code) == null) {
					flag = 12001;
					break;
				} else if (EditType.save.equals(editType)&& organizationMap.get(code) != null) {
					flag = 12001;
					break;
				} else if (StringUtils.isBlank(name)){
					flag = 12002;
					break;
				} else if (organizationMap.get(parentCode) == null){
					flag = 12003;
					break;
				} else if (!OrganizationType.headCompany.equals(type)&&!OrganizationType.headOrg.equals(type)&&!OrganizationType.subCompany.equals(type)&&!OrganizationType.subOrg.equals(type)){
					flag = 12004;
					break;
				}
				if (flag == 10001) {
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
	 * @param wSOrganizationList
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void edit(List<WSOrganization> wSOrganizationList){
		for (WSOrganization wSOrganization : wSOrganizationList) {
			String editType = wSOrganization.getEditType();
			if(EditType.save.equals(editType)){
				this.save(wSOrganization);
			}else if(EditType.update.equals(editType)){
				this.merge(wSOrganization);
			}else if(EditType.remove.equals(editType)){
				this.remove(wSOrganization);
			}
		}
	}
	
	@Transactional
	public void save(WSOrganization wSOrganization){
		String code = wSOrganization.getCode();
		String name = wSOrganization.getName();
		String parentCode = wSOrganization.getParentCode();
		String type = wSOrganization.getType();
		SysOrganization company = null;
		List<SysOrganization> parentOrgs = o_organizationBO.findByOrgcode(parentCode);
		SysOrganization parentOrg = null;
		Integer sn = 1;
		if (parentOrgs != null && parentOrgs.size() > 0) {
			parentOrg = parentOrgs.get(0);
			company = parentOrg.getCompany();
			sn = parentOrg.getChildrenOrg().size() + 1;
		}
		SysOrganization organization = new SysOrganization();
		organization.setId(Identities.uuid());
		organization.setOrgcode(code);
		organization.setOrgname(name);
		organization.setIsLeaf(true);
		organization.setOrgType(type);
		/**
		 * 类型为公司或上级部门为空则设定所属公司为自己。
		 */
		if (OrganizationType.headCompany.equals(type)|| OrganizationType.subCompany.equals(type)|| company == null) {
			company = organization;
		}
		organization.setParentOrg(parentOrg);
		organization.setCompany(company);
		organization.setSn(sn);
		organization.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
		organization.setOrgStatus(Contents.STATUS_NORMAL);
		o_organizationBO.save(organization);
	}
	
	/**
	 * 
	 * merge:
	 * 
	 * @author 杨鹏
	 * @param wSOrganization
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void merge(WSOrganization wSOrganization){
		String code = wSOrganization.getCode();
		String name = wSOrganization.getName();
		String parentCode = wSOrganization.getParentCode();
		String type = wSOrganization.getType();
		List<SysOrganization> parentOrgs = o_organizationBO.findByOrgcode(parentCode);
		SysOrganization parentOrg = null;
		SysOrganization company = null;
		Integer sn = 1;
		if (parentOrgs != null && parentOrgs.size() > 0) {
			parentOrg = parentOrgs.get(0);
			company = parentOrg.getCompany();
			sn = parentOrg.getChildrenOrg().size() + 1;
		}
		List<SysOrganization> organizations = o_organizationBO.findByOrgcode(code);
		SysOrganization organization = organizations.get(0);
		organization.setOrgname(name);
		organization.setParentOrg(parentOrg);
		organization.setOrgType(type);
		/**
		 * 类型为公司或上级部门为空则设定所属公司为自己。
		 */
		if (OrganizationType.headCompany.equals(type)|| OrganizationType.subCompany.equals(type)|| company == null) {
			company = organization;
		}
		organization.setCompany(company);
		organization.setSn(sn);
		organization.setOrgStatus(Contents.STATUS_NORMAL);
		o_organizationBO.merge(organization);
	}
	/**
	 * 
	 * remove:删除
	 * 
	 * @author 杨鹏
	 * @param code
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void remove(WSOrganization wSOrganization){
		String code = wSOrganization.getCode();
		List<SysOrganization> organizations = o_organizationBO.findByOrgcode(code);
		if (organizations != null && organizations.size() > 0) {
			SysOrganization organization = organizations.get(0);
			organization.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
			o_organizationBO.merge(organization);
		}
	}
}

