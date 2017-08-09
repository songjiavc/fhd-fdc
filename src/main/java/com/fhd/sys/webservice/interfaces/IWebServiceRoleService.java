package com.fhd.sys.webservice.interfaces;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.fhd.sys.webservice.dto.WSRole;
@WebService
@SOAPBinding(style = Style.RPC)
public interface IWebServiceRoleService {
	/**
	 * 
	 * edit:
	 * 
	 * @author 杨鹏
	 * @param wSRoleList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public abstract Integer edit(List<WSRole> wSRoleList);

}