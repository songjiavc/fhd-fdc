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
import com.fhd.entity.sys.orgstructure.SysPosition;
import com.fhd.fdc.utils.Contents;
import com.fhd.sys.business.orgstructure.OrganizationBO;
import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.WSPosition;

/**
 * 
 * ClassName:WebServicePositionBO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-11-14		下午1:39:43
 *
 * @see
 */
@Service
public class WebServicePositionBO{
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private PositionBO o_positionBO;
	/**
	 * 
	 * validate:验证
	 * 
	 * @author 杨鹏
	 * @param wSPositionList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Integer validate(List<WSPosition> wSPositionList){
		Integer flag=20001;
		try {
			List<SysOrganization> organizationList = o_organizationBO.findByAll();
			Map<String,SysOrganization> organizationMap = new HashMap<String, SysOrganization>();
			for (SysOrganization organization : organizationList) {
				organizationMap.put(organization.getOrgcode(), organization);
			}
			List<SysPosition> positionList = o_positionBO.findByAll();
			Map<String,SysPosition> positionMap = new HashMap<String, SysPosition>();
			for (SysPosition position : positionList) {
				positionMap.put(position.getPosicode(), position);
			}
			for (WSPosition wSPosition : wSPositionList) {
				String editType = wSPosition.getEditType();
				String code = wSPosition.getCode();
				if (!EditType.save.equals(editType)&&!EditType.update.equals(editType)&&!EditType.remove.equals(editType)) {
					flag = 21001;
					break;
				} else if (StringUtils.isBlank(wSPosition.getCode())) {
					flag = 22001;
					break;
				} else if (!EditType.save.equals(editType)&& positionMap.get(code) == null) {
					flag = 22001;
					break;
				} else if (EditType.save.equals(editType)&& positionMap.get(code) != null) {
					flag = 22001;
					break;
				} else if (StringUtils.isBlank(wSPosition.getName())){
					flag = 22002;
					break;
				} else if (organizationMap.get(wSPosition.getOrgCode()) == null){
					flag = 22003;
					break;
				}
				if (flag == 20001) {
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
	 * @param wSPositionList
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void edit(List<WSPosition> wSPositionList){
		for (WSPosition wsPosition : wSPositionList) {
			String editType = wsPosition.getEditType();
			if(EditType.save.equals(editType)){
				this.save(wsPosition);
			}else if(EditType.update.equals(editType)){
				this.merge(wsPosition);
			}else if(EditType.remove.equals(editType)){
				this.remove(wsPosition);
			}
		}
	}
	/**
	 * 
	 * save:
	 * 
	 * @author 杨鹏
	 * @param wSPosition
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void save(WSPosition wSPosition){
		String orgCode = wSPosition.getOrgCode();
		String code = wSPosition.getCode();
		String name = wSPosition.getName();
		List<SysOrganization> organizations = o_organizationBO.findByOrgcode(orgCode);
		SysOrganization organization = organizations.get(0);
		Integer sn = organization.getSysPositions().size()+1;
		SysPosition position=new SysPosition();
		position.setId(Identities.uuid());
		position.setPosicode(code);
		position.setPosiname(name);
		position.setSn(sn);
		position.setSysOrganization(organization);
		position.setPosiStatus(Contents.STATUS_NORMAL);
		position.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
		o_positionBO.save(position);
	}
	
	/**
	 * 
	 * merge:
	 * 
	 * @author 杨鹏
	 * @param wSPosition
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void merge(WSPosition wSPosition){
		String orgCode = wSPosition.getOrgCode();
		String code = wSPosition.getCode();
		String name = wSPosition.getName();
		List<SysOrganization> organizations = o_organizationBO.findByOrgcode(orgCode);
		SysOrganization organization = organizations.get(0);
		Integer sn = organization.getSysPositions().size()+1;
		List<SysPosition> positions = o_positionBO.findBySome(code,null, organization.getId());
		SysPosition position=new SysPosition();
		position=positions.get(0);
		position.setPosiname(name);
		position.setSn(sn);
		position.setSysOrganization(organization);
		position.setPosiStatus(Contents.STATUS_NORMAL);
		position.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
		o_positionBO.merge(position);
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
	public void remove(WSPosition wSPosition){
		String orgCode = wSPosition.getOrgCode();
		String code = wSPosition.getCode();
		List<SysOrganization> organizations = o_organizationBO.findByOrgcode(orgCode);
		SysOrganization organization = organizations.get(0);
		List<SysPosition> positions = o_positionBO.findBySome(code, null, organization.getId());
		SysPosition position = positions.get(0);
		position.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
		o_positionBO.merge(position);
	}
}

