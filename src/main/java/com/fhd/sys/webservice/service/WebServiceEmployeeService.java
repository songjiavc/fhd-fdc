package com.fhd.sys.webservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fhd.sys.business.organization.WebServiceEmployeeBO;
import com.fhd.sys.webservice.dto.WSEmployee;
import com.fhd.sys.webservice.interfaces.IWebServiceEmployeeService;
@Controller
public class WebServiceEmployeeService implements IWebServiceEmployeeService{
	@Autowired
	private WebServiceEmployeeBO o_webServiceEmployeeBO;
	/**
	 * 
	 * edit:
	 * 
	 * @author 杨鹏
	 * @param wSEmployees
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Override
	public Integer edit(List<WSEmployee> wSEmployeeList){
		Integer flag=30001;
		try {
			flag=o_webServiceEmployeeBO.validate(wSEmployeeList);
			if(flag==0){
				o_webServiceEmployeeBO.edit(wSEmployeeList);
			}
		} catch (Exception e) {
			flag=30006;
			e.printStackTrace();
		}
		return flag;
	}
}

