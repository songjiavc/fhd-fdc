package com.fhd.sys.webservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fhd.sys.business.organization.WebServiceOrgBO;
import com.fhd.sys.webservice.dto.WSOrganization;
import com.fhd.sys.webservice.interfaces.IWebServiceOrgService;
@Controller
public class WebServiceOrgService implements IWebServiceOrgService {
	@Autowired
	private WebServiceOrgBO o_webServiceOrgBO;
	/**
	 * 
	 * edit:
	 * 
	 * @author 杨鹏
	 * @param wSOrganizationList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Override
	public Integer edit(List<WSOrganization> wSOrganizationList){
		Integer flag=10001;
		try {
			flag=o_webServiceOrgBO.validate(wSOrganizationList);
			if(flag==0){
				o_webServiceOrgBO.edit(wSOrganizationList);
			}
		} catch (Exception e) {
			flag=10006;
			e.printStackTrace();
		}
		return flag;
	}
}

