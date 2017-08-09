package com.fhd.sys.webservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fhd.sys.business.organization.WebServicePositionBO;
import com.fhd.sys.webservice.dto.WSPosition;
import com.fhd.sys.webservice.interfaces.IWebServicePositionService;
@Controller
public class WebServicePositionService implements IWebServicePositionService{
	@Autowired
	private WebServicePositionBO o_webServicePositionBO;
	/**
	 * 
	 * edit:
	 * 
	 * @author 杨鹏
	 * @param wSPositionList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Override
	public Integer edit(List<WSPosition> wSPositionList){
		Integer flag=20001;
		try {
			flag=o_webServicePositionBO.validate(wSPositionList);
			if(flag==0){
				o_webServicePositionBO.edit(wSPositionList);
			}
		} catch (Exception e) {
			flag=20006;
			e.printStackTrace();
		}
		return flag;
	}
}

