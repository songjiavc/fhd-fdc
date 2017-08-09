package com.fhd.sys.webservice.interfaces;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.fhd.sys.webservice.dto.WSOrganization;
@WebService
@SOAPBinding(style = Style.RPC)
public interface IWebServiceOrgService {
	/**
	 * 
	 * edit:
	 * 
	 * @author 杨鹏
	 * @param wSOrganizationList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public abstract Integer edit(List<WSOrganization> wSOrganizationList);

}