package com.fhd.sys.webservice.client.sys;

import java.util.ArrayList;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.WSEmployee;
import com.fhd.sys.webservice.interfaces.IWebServiceEmployeeService;

public class WebServiceEmployeeClient {
	public static void main(String[] args) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IWebServiceEmployeeService.class);
		factory.setAddress("http://127.0.0.1:8180/fdc_5_0/cxfservices/employeeService");
		IWebServiceEmployeeService service = (IWebServiceEmployeeService) factory.create();
		ArrayList<WSEmployee> wSEmployees = new ArrayList<WSEmployee>();
		WSEmployee wSEmployee = new WSEmployee();
		wSEmployee.setCode("a");
		wSEmployee.setName("a");
		wSEmployee.setUsername("a");
		wSEmployee.setPassword("a");
		wSEmployee.setCompanyCode("FHD01");
		wSEmployee.setOrgCodes(new String[]{"FHD0103"});
		wSEmployee.setPositionCodes(new String[]{"XA01GW2"});
		wSEmployee.setRoleCodes(new String[]{"newRole_1","newRole_2","test"});
		wSEmployee.setEditType(EditType.update);
		wSEmployees.add(wSEmployee);
		Integer edit = service.edit(wSEmployees);
		System.out.println(edit);
	}
}
