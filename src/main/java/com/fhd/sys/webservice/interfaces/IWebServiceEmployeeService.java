package com.fhd.sys.webservice.interfaces;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.fhd.sys.webservice.dto.WSEmployee;

/**
 * 人员同步接口
 * 
 * @author vincent
 *
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface IWebServiceEmployeeService {
	
	public abstract Integer edit(List<WSEmployee> wSEmployeeList);


}