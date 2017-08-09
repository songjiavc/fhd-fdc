package com.fhd.sys.webservice.client.sys;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.fhd.sys.webservice.dto.EditType;
import com.fhd.sys.webservice.dto.WSPosition;
import com.fhd.sys.webservice.interfaces.IWebServicePositionService;

public class WebServicePositionClient {
	public static void main(String[] args) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IWebServicePositionService.class);
		factory.setAddress("http://127.0.0.1:8180/fdc_5_0/cxfservices/positionService");
		IWebServicePositionService service = (IWebServicePositionService) factory.create();
		List<WSPosition> wSPositionList = new ArrayList<WSPosition>();
		WSPosition wSPosition = new WSPosition();
		wSPosition.setCode("test");
		wSPosition.setName("测试");
		wSPosition.setOrgCode("test");
		wSPosition.setEditType(EditType.save);
		wSPositionList.add(wSPosition);
		Integer edit = service.edit(wSPositionList);
		System.out.println(edit);
	}
}
