package com.fhd.sys.webservice.interfaces;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.fhd.sys.webservice.dto.WSPosition;
@WebService
@SOAPBinding(style = Style.RPC)
public interface IWebServicePositionService {

	public abstract Integer edit(List<WSPosition> wSPositionList);

}