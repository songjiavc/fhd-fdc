package com.fhd.sys.webservice.client.sys;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.WSRole;
import com.fhd.sys.webservice.interfaces.IWebServiceRoleService;

public class WebServiceRoleClient {
	public static void main(String[] args) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IWebServiceRoleService.class);
		factory.setAddress("http://127.0.0.1:8180/fdc_5_0/cxfservices/roleService");
		IWebServiceRoleService service = (IWebServiceRoleService) factory.create();
		List<WSRole> wSRoleList = new ArrayList<WSRole>();
		WSRole wSRole = new WSRole();
		wSRole.setCode("test");
		wSRole.setName("测试");
		wSRole.setAuthorityCodes(new String[]{"personLog","workPlan"});
		wSRole.setEditType(EditType.update);
		wSRoleList.add(wSRole);
		Integer edit = service.edit(wSRoleList);
		System.out.println(edit);
	}
}
