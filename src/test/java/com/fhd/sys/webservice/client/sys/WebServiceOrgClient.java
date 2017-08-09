package com.fhd.sys.webservice.client.sys;

import java.util.ArrayList;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.OrganizationType;
import com.fhd.sys.webservice.dto.WSOrganization;
import com.fhd.sys.webservice.interfaces.IWebServiceOrgService;

public class WebServiceOrgClient {
	public static void main(String[] args) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IWebServiceOrgService.class);
		factory.setAddress("http://127.0.0.1:8180/fdc_5_0/cxfservices/orgService");
		IWebServiceOrgService service = (IWebServiceOrgService) factory.create();
		ArrayList<WSOrganization> wSOrganizationList = new ArrayList<WSOrganization>();
		WSOrganization wSOrganization = new WSOrganization();
		wSOrganization.setCode("test");
		wSOrganization.setName("测试");
		wSOrganization.setParentCode("FHD0103");
		wSOrganization.setType(OrganizationType.subOrg);
		wSOrganization.setEditType(EditType.save);
		wSOrganizationList.add(wSOrganization);
		Integer edit = service.edit(wSOrganizationList);
		System.out.println(edit);
	}
}
