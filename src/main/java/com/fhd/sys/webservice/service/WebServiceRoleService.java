package com.fhd.sys.webservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fhd.sys.business.organization.WebServiceRoleBO;
import com.fhd.sys.webservice.dto.WSRole;
import com.fhd.sys.webservice.interfaces.IWebServiceRoleService;
@Controller
public class WebServiceRoleService implements IWebServiceRoleService{
	@Autowired
	private WebServiceRoleBO o_webServiceRoleBO;
	/**
	 * 
	 * edit:
	 * 
	 * @author 杨鹏
	 * @param wSRoleList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Override
	public Integer edit(List<WSRole> wSRoleList){
		Integer flag=40001;
		try {
			flag=o_webServiceRoleBO.validate(wSRoleList);
			if(flag==0){
				o_webServiceRoleBO.edit(wSRoleList);
			}
		} catch (Exception e) {
			flag=40006;
			e.printStackTrace();
		}
		return flag;
	}
}

