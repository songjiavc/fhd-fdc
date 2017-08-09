package com.fhd.ra.business.assess.email;

import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

@Service
public class AddRessUtilBO {

	public String getAddRessEmil(String businessId, String taskId, String url, String loginType, String username){
		StringBuffer address = new StringBuffer();
		WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        String contextPath = applicationContext.getServletContext().getContextPath();
        String ipPort = applicationContext.getServletContext().getInitParameter("webUrl");
        
        address.append(ipPort).append(contextPath).append("/sso/jbpmTask.do");
        address.append("?businessId=" + businessId);
        address.append("&taskId=" + taskId);
        address.append("&url=" + url);
        address.append("&loginType=" + loginType);
        address.append("&username=" + username);
    	
    	return address.toString();
	}
}
